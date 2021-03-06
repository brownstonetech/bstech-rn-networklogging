package ca.bstech.networklogging;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.Observer;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import ca.bstech.networklogging.logging.NetworkLoggingHelper;
import ca.bstech.networklogging.networkinfo.TelephonyHelper;
import ca.bstech.networklogging.ping.PingHelper;
import ca.bstech.networklogging.utils.FileGenerator;

public class BSTNetworkLoggingModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final int READ_PHONE_STATE_REQUEST = 37;

    private TelephonyHelper telephonyHelper;
    private PingHelper pingHelper;
    private NetworkLoggingHelper networkLoggingHelper;
    private FileGenerator fileGenerator;

    public BSTNetworkLoggingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        pingHelper = new PingHelper(reactContext);
        Observer pingEventEmitter = new PingEventEmitter(reactContext, Constants.PING_EVENT);
        pingHelper.getObservable().addObserver(pingEventEmitter);
        fileGenerator = new FileGenerator(reactContext);
    }

    @ReactMethod
    public void initializeAsync(final Promise promise) {
        try {
            ReactApplicationContext reactContext = getReactApplicationContext();
            telephonyHelper = new TelephonyHelper(reactContext);
            networkLoggingHelper = new NetworkLoggingHelper(reactContext, telephonyHelper);

            telephonyHelper.addObserver(networkLoggingHelper.getNetInfoObserver());
            telephonyHelper.addObserver(new NetInfoEventEmitter(reactContext, Constants.NETWORK_INFO_EVENT));

            telephonyHelper.startListener();

            pingHelper.getObservable().addObserver(networkLoggingHelper.getPingObserver());
            promise.resolve(null);
        } catch(Exception e) {
            Log.w(Constants.MODULE_NAME, "Initialize network logging module failed", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    @Override
    public String getName() {
        return Constants.MODULE_NAME;
    }


    @Override
    public void onHostResume() {
        // Activity `onResume`
    }

    @Override
    public void onHostPause() {
        // Activity `onPause`
    }

    @ReactMethod
    public void startPingAsync(final String domainName, ReadableMap params, ReadableMap pingOptions, final Promise promise) {
        try {
            Log.d(Constants.MODULE_NAME, "Start ping "+domainName);
            pingHelper.startPingAsync(domainName, params, pingOptions, promise);
        } catch(ApplicationException e) {
            Log.w(Constants.MODULE_NAME, "Start ping encounter exception", e);
            promise.reject(e.getCode(), e.getMessage(), e);
        } catch(Exception e) {
            Log.w(Constants.MODULE_NAME, "Start ping encounter exception", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e.getMessage(), e);
        }
    }

    @ReactMethod
    public void stopPingAsync(final Promise promise) {
        try {
            pingHelper.stopPingAsync(promise);
        } catch(Exception e) {
            Log.w(Constants.MODULE_NAME, "Start ping encounter exception", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e.getMessage(), e);
        }
    }

    @Override
    public void onHostDestroy() {
        stopPingAsync(null);
        stopNetworkLoggingAsync(null);
    }

    @ReactMethod
    public void feedLocationAsync(ReadableMap location, Promise promise) {
        try {
            networkLoggingHelper.getLocationObserver().update(null, location);
            promise.resolve(null);
        } catch(Exception e) {
            Log.e(Constants.MODULE_NAME, "Unexpected exception in feedLocationAsync", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    @ReactMethod
    public void hasTelephonyFeatureAsync(Promise promise) {
        try {
            boolean hasTelephonyFeature = getReactApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
            promise.resolve(hasTelephonyFeature);
        } catch (Exception e) {
            Log.e(Constants.MODULE_NAME, "Unexpected exception in hasTelephonyFeatureAsync", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    @ReactMethod
    public void getPhoneInfoAsync(final Promise promise) {
        try {
            if ( telephonyHelper == null ) {
                throw new ApplicationException(Constants.E_INVALID_PARAM, "Network logging module haven't been initialized yet.");
            }
            WritableMap phoneInfo = telephonyHelper.getPhoneInfo();
            promise.resolve(phoneInfo);
        } catch (ApplicationException e) {
            Log.w(Constants.MODULE_NAME, "Call getPhoneInfo encounter exception", e);
            promise.reject(e.getCode(), e.getMessage(), e);
        } catch (Exception e) {
            Log.w(Constants.MODULE_NAME, "Call getPhoneInfo encounter unexpected exception", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    @ReactMethod
    public void startNetworkLoggingAsync(ReadableMap map, final Promise promise) {
        if ( networkLoggingHelper == null ) {
            promise.reject(Constants.E_INVALID_PARAM, "networkLogging module haven't been initialized yet.");
            return;
        }
        String imsi = map.getString("imsi");
        int loggingInterval = map.hasKey("loggingInterval")? map.getInt("loggingInterval"):1000;
        networkLoggingHelper.startNetworkLoggingAsync(imsi,loggingInterval, promise);
    }

    @ReactMethod
    public void stopNetworkLoggingAsync(final Promise promise) {
        try {
            if ( networkLoggingHelper != null ) {
                networkLoggingHelper.stopNetworkLogging();
            }
            if (promise != null) {
                promise.resolve(null);
            }
        } catch(Exception e) {
            Log.w(Constants.MODULE_NAME, "Stop networkLogging task encounter unexpected Exception", e);
            if ( promise != null) {
                promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
            }
        }
    }

    @ReactMethod
    public void requestPermissionsAsync(final ReadableMap map, final Promise promise) {
        try {
            Log.i(Constants.MODULE_NAME, "##### Executando requestPermissions(" + (map != null && map.hasKey("requestPermission") ? map.getBoolean("requestPermission") : "null") + ")");
            boolean requestPermission = (map != null && map.hasKey("requestPermission")) ? map.getBoolean("requestPermission"): true;
            if (!hasPermissionToReadNetworkHistory(requestPermission)) {
                boolean hasPermission = hasPermissionToReadNetworkHistory(false);
                if (!hasPermission) {
                    promise.resolve(false);
                    return;
                }
            }

            promise.resolve(true);
        } catch (Exception e) {
            Log.w(Constants.MODULE_NAME, "Error requesting permissions: " + e.getMessage(), e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    @ReactMethod
    public void generateTestFileAsync(final ReadableMap map, final Promise promise) {
        try {
            String fileName = map.getString("fileName");
            int sizeKB = map.getInt("sizeKB");
            String filePath = fileGenerator.generateTestFile(fileName, sizeKB);
            promise.resolve(filePath);
        } catch (Exception e) {
            Log.w(Constants.MODULE_NAME, "Error requesting permissions: " + e.getMessage(), e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    private boolean hasPermissionToReadNetworkHistory(boolean requestPermission) {
        final Activity activity = getCurrentActivity();
        if (activity == null) {
            Log.w(Constants.MODULE_NAME, "Could not read network usage permission: current activity is null.");
            return false;
        }
        final AppOpsManager appOps = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), activity.getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                activity.getApplicationContext().getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    public void onOpChanged(String op, String packageName) {
                        try {
                            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), activity.getPackageName());
                            if (mode != AppOpsManager.MODE_ALLOWED) {
                                return;
                            }
                            appOps.stopWatchingMode(this);
                            Intent intent = new Intent(activity, activity.getClass());
                            if (activity.getIntent().getExtras() != null) {
                                intent.putExtras(activity.getIntent().getExtras());
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.getApplicationContext().startActivity(intent);
                        } catch (Exception e) {
                            Log.e(Constants.MODULE_NAME, "Error reading data usage statistics: " + e.getMessage(), e);
                        }
                    }
                });
        if (requestPermission) requestReadNetworkHistoryAccess();
        return false;
    }

    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        getCurrentActivity().startActivity(intent);
    }

}

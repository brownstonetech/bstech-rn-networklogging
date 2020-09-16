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

import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import ca.bstech.networklogging.logging.NetworkLoggingHelper;
import ca.bstech.networklogging.networkinfo.TelephonyHelper;
import ca.bstech.networklogging.ping.PingHelper;

public class BSTNetworkLoggingModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final int READ_PHONE_STATE_REQUEST = 37;

    private TelephonyHelper telephonyHelper;
    private PingHelper pingHelper;
    private NetworkLoggingHelper networkLoggingHelper;

    public BSTNetworkLoggingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        pingHelper = new PingHelper(reactContext);
        Observer pingEventEmitter = new EventEmitter(reactContext, Constants.PING_EVENT);
        pingHelper.getObservable().addObserver(pingEventEmitter);
    }

    @ReactMethod
    public void initializeAsync(final Promise promise) {
        try {
            ReactApplicationContext reactContext = getReactApplicationContext();
            networkLoggingHelper = new NetworkLoggingHelper(reactContext);

            telephonyHelper = new TelephonyHelper(reactContext);
            telephonyHelper.addObserver(networkLoggingHelper.getNetInfoObserver());
            telephonyHelper.startListener();

            pingHelper.getObservable().addObserver(networkLoggingHelper.getPingObserver());
            promise.resolve(null);
        } catch(Exception e) {
            Log.e(Constants.MODULE_NAME, "initialize networkLogging module failed", e);
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
            pingHelper.startPingAsync(domainName, params, pingOptions);
            promise.resolve(null);
        } catch(ApplicationException e) {
            Log.e(Constants.MODULE_NAME, "Start ping encounter exception", e);
            promise.reject(e.getCode(), e.getMessage(), e);
        } catch(Exception e) {
            Log.e(Constants.MODULE_NAME, "Start ping encounter exception", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e.getMessage(), e);
        }
    }

    @ReactMethod
    public void stopPingAsync(final Promise promise) {
        try {
            pingHelper.stopPingAsync(promise);
        } catch(Exception e) {
            Log.e(Constants.MODULE_NAME, "Start ping encounter exception", e);
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e.getMessage(), e);
        }
    }

    @Override
    public void onHostDestroy() {
        stopPingAsync(null);
        stopNetworkLoggingAsync(null);
    }

    @ReactMethod
    public void getPhoneInfoAsync(final Promise promise) {
        try {
            WritableMap phoneInfo = telephonyHelper.getPhoneInfo();
            promise.resolve(phoneInfo);
        } catch (ApplicationException e) {
            promise.reject(e.getCode(), e.getMessage(), e);
        } catch (Exception e) {
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    @ReactMethod
    public void startNetworkLoggingAsync(final Promise promise) {
        if ( networkLoggingHelper == null ) {
            promise.reject(Constants.E_INVALID_PARAM, "networkLogging module haven't been initialized yet.");
            return;
        }
        networkLoggingHelper.startNetworkLoggingAsync(1000, promise);
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
            Log.e(Constants.MODULE_NAME, "Stop networkLogging task encounter unexpected Exception", e);
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

            if (requestPermission && !hasPermissionToReadPhoneStats()) {
                requestPhoneStatePermissions();
                promise.resolve(hasPermissionToReadPhoneStats());
                return;
            }

            promise.resolve(true);
        } catch (Exception e) {
            Log.e(Constants.MODULE_NAME, "Error requesting permissions: " + e.getMessage(), e);
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

    private boolean hasPermissionToReadPhoneStats() {
        boolean isPermitReadPhoneState = ActivityCompat.checkSelfPermission(getCurrentActivity(), android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        boolean isPermitReadPhoneNumbers = ActivityCompat.checkSelfPermission(getCurrentActivity(), android.Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED;
        boolean isPermitReadSMS = ActivityCompat.checkSelfPermission(getCurrentActivity(), android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
        boolean isPermitAccessFineLocation = ActivityCompat.checkSelfPermission(getCurrentActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean isPermitAccessCoarseLocation = ActivityCompat.checkSelfPermission(getCurrentActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Log.d(Constants.MODULE_NAME, "Current permission status: READ_PHONE_STATE="+isPermitReadPhoneState
                +", READ_PHONE_NUMBERS="+isPermitReadPhoneNumbers
                +", READ_SMS="+isPermitReadSMS
                +", ACCESS_FINE_LOCATION="+isPermitAccessFineLocation
                +", ACCESS_COARSE_LOCATION="+isPermitAccessCoarseLocation
        );
        if ( !isPermitReadPhoneState
                || !isPermitReadPhoneNumbers
                || !isPermitReadSMS
                || !isPermitAccessFineLocation
                || !isPermitAccessCoarseLocation
        ) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPhoneStatePermissions() {
        ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_PHONE_NUMBERS,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        }, READ_PHONE_STATE_REQUEST);
    }

}

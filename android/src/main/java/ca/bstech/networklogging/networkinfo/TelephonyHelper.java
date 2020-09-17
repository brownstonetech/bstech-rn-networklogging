package ca.bstech.networklogging.networkinfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ca.bstech.networklogging.ApplicationException;
import ca.bstech.networklogging.Constants;

public class TelephonyHelper extends Observable
        implements TelephonyListener.PhoneCallStateUpdate {

    private ReactApplicationContext mReactContext;
    private TelephonyManager telephonyManager;
    private TelephonyListener telephonyPhoneStateListener;
//    private String PHONE_STATE_LISTENER = "Telephony-PhoneStateListener";

//    private String LISTEN_CALL_FORWARDING_INDICATOR = "LISTEN_CALL_FORWARDING_INDICATOR";
//    private String LISTEN_CALL_STATE = "LISTEN_CALL_STATE";
//    private String LISTEN_CELL_INFO = "LISTEN_CELL_INFO";
//    private String LISTEN_CELL_LOCATION = "LISTEN_CELL_LOCATION";
//    private String LISTEN_DATA_ACTIVITY = "LISTEN_DATA_ACTIVITY";
//    private String LISTEN_DATA_CONNECTION_STATE = "LISTEN_DATA_CONNECTION_STATE";
//    private String LISTEN_MESSAGE_WAITING_INDICATOR = "LISTEN_MESSAGE_WAITING_INDICATOR";
//    private String LISTEN_SERVICE_STATE = "LISTEN_SERVICE_STATE";
//    private String LISTEN_SIGNAL_STRENGTHS = "LISTEN_SIGNAL_STRENGTHS";
//
//    private int DATA_ACTIVITY_NONE = 0;
//    private int DATA_ACTIVITY_IN = 1;
//    private int DATA_ACTIVITY_OUT = 2;
//    private int DATA_ACTIVITY_INOUT = 3;
//    private int DATA_ACTIVITY_DORMANT = 4;
//
//    private int DATA_DISCONNECTED = 0;
//    private int DATA_CONNECTING = 1;
//    private int DATA_CONNECTED = 2;
//    private int DATA_SUSPENDED = 3;
//
//    private int CALL_STATE_IDLE = 0;
//    private int CALL_STATE_RINGING = 1;
//    private int CALL_STATE_OFFHOOK = 2;

    public TelephonyHelper(ReactApplicationContext reactContext) {
        mReactContext = reactContext;
        telephonyManager = (TelephonyManager) mReactContext.getSystemService(
                Context.TELEPHONY_SERVICE);
    }

    public void startListener() throws SecurityException {
        if (telephonyPhoneStateListener == null ) {
           telephonyPhoneStateListener = new TelephonyListener(this);
        }
        int events = PhoneStateListener.LISTEN_CELL_INFO;
        Log.d(Constants.MODULE_NAME, "Register telephonyPhoneStateListener with events "+events);
        telephonyManager.listen(telephonyPhoneStateListener, events);
        getCellInfo();
    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
        getCellInfo();
    }

    @Override
    public synchronized boolean hasChanged() {
        // Change the default behavior on changed
        return true;
    }

    public void stopListener() {
        if ( telephonyPhoneStateListener == null ) return;
        telephonyManager.listen(telephonyPhoneStateListener,
                PhoneStateListener.LISTEN_NONE);
    }

    @TargetApi(Build.VERSION_CODES.Q)
    public void getCellInfo() {

        WritableArray mapArray = Arguments.createArray();

        if (ActivityCompat.checkSelfPermission(mReactContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(Constants.MODULE_NAME, "Don't have permission ACCESS_FINE_LOCATION, call getAllCellInfo() ignored");
            return;
        }
        Log.d(Constants.MODULE_NAME, "Invoking telephonyManager.getAllCellInfo");
        List<CellInfo> cellInfo = telephonyManager.getAllCellInfo();
        Log.d(Constants.MODULE_NAME, "Invoking telephonyManager.getAllCellInfo returned "+cellInfo.size()+" CellInfos.");
        handleCellInfos(cellInfo);
    }

    public Integer getDataNetworkType() throws ApplicationException {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            if (ActivityCompat.checkSelfPermission(this.mReactContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.w(Constants.MODULE_NAME, "Don't have permission READ_PHONE_STATE");
                return telephonyManager.getNetworkType();
            }
            return telephonyManager.getDataNetworkType();
        } else {
            return telephonyManager.getNetworkType();
        }
    }


    public WritableMap getPhoneInfo() throws ApplicationException {
        WritableMap mapPhoneInfo = Arguments.createMap();
        try {
            String imsi = telephonyManager.getSubscriberId();
            mapPhoneInfo.putString("imsi", telephonyManager.getSubscriberId());
        } catch (SecurityException e ) {
            // silently ignore this exception
            Log.d(Constants.MODULE_NAME, "Can not retrieve IMSI:"+e.getMessage());
        }
        try {
            mapPhoneInfo.putString("imei", telephonyManager.getDeviceId());
        } catch (SecurityException e ) {
            // silently ignore this exception
            Log.d(Constants.MODULE_NAME, "Can not retrieve IMEI:"+e.getMessage());
        }
        mapPhoneInfo.putString("model", Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
        return mapPhoneInfo;
    }

    @Override
    public void phoneCellInfoUpdated(List<CellInfo> cellInfo) {
        Log.d(Constants.MODULE_NAME, "CellInfo update event received. cellInfo="+cellInfo);
        if (cellInfo == null) {
            return;
        }
        handleCellInfos(cellInfo);
    }

    private void handleCellInfos(List<CellInfo> cellInfo) {
        Log.d(Constants.MODULE_NAME, "Processing cellInfos:"+ cellInfo);
        CellInfos cellInfos = new CellInfos();
        for (CellInfo info : cellInfo) {
            if (info instanceof CellInfoLte) {
                Log.d(Constants.MODULE_NAME, "Processing cellInfoLte");
                cellInfos.addCellInfoLte((CellInfoLte)info);
            } else if (Build.VERSION.SDK_INT >= 29 && (info instanceof CellInfoNr)) {
                Log.d(Constants.MODULE_NAME, "Processing cellInfoNr");
                cellInfos.addCellInfoNr((CellInfoNr)info);
            } else {
                Log.d(Constants.MODULE_NAME, "Ignoring cellInfo class="+info.getClass().getSimpleName());
            }
        }
        Log.d(Constants.MODULE_NAME, "Processed CellInfos:"+ cellInfos);
        this.notifyObservers(cellInfos);
    }

//    @Override
//    public void phoneSignalStrengthsUpdated(SignalStrength signalStrength) {
//        WritableMap map = Arguments.createMap();
//        map.putInt("cdmaDbm", signalStrength.getCdmaDbm());
//        map.putInt("cdmaEcio", signalStrength.getCdmaEcio());
//        map.putInt("evdoDbm", signalStrength.getEvdoDbm());
//        map.putInt("evdoEcio", signalStrength.getEvdoEcio());
//        map.putInt("evdoSnr", signalStrength.getEvdoSnr());
//        map.putInt("gsmBitErrorRate", signalStrength.getGsmBitErrorRate());
//        map.putInt("gsmSignalStrength", signalStrength.getGsmSignalStrength());
//        map.putBoolean("gsm", signalStrength.isGsm());
//
//        WritableMap result = Arguments.createMap();
//        result.putString("type", "LISTEN_SIGNAL_STRENGTHS");
//        result.putMap("data", map);
//
//    }

//    private String decToHex(int dec) {
//        return String.format("%x", dec);
//    }
//
//    private int hexToDec(String hex) {
//        return Integer.parseInt(hex, 16);
//    }
}

package ca.bstech.networklogging;

import android.os.Build;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ca.bstech.networklogging.networkinfo.CellInfoUtils;
import ca.bstech.networklogging.networkinfo.CellInfos;

public class NetInfoEventEmitter implements Observer {

    private ReactApplicationContext reactContext;
    private String eventType;

    public NetInfoEventEmitter(ReactApplicationContext reactContext, String eventType) {
        this.reactContext = reactContext;
        this.eventType = eventType;
    }

    private void sendEvent(Object params) {
        Log.d(Constants.MODULE_NAME, "Sending NetworkInfo event to JS App.");
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventType, params);
    }

    @Override
    public void update(Observable o, Object result) {
        if ( result == null ) return;
        sendEvent(process(result));
    }

    private WritableMap process(Object result) {
        Log.d(Constants.MODULE_NAME, "Start processing network info for JS");
        WritableMap ret = Arguments.createMap();
        CellInfos cellInfos = (CellInfos) result;
        List<CellInfoLte> cellInfoLtes = cellInfos.getCellInfoLte();
        if ( cellInfoLtes != null ) {
            WritableArray array = Arguments.createArray();
            for (CellInfoLte cellInfoLte: cellInfoLtes) {
                WritableMap mCellInfoNr = processCellInfo(cellInfoLte);
                array.pushMap(mCellInfoNr);
            }
            ret.putArray("lte", array);
        }
        if ( android.os.Build.VERSION.SDK_INT >= 29) {
            List<CellInfoNr> cellInfoNrs = cellInfos.getCellInfoNr();
            if (cellInfoNrs != null) {
                WritableArray array = Arguments.createArray();
                for (CellInfoNr cellInfoNr : cellInfoNrs) {
                    WritableMap mCellInfoNr = processCellInfo(cellInfoNr);
                    array.pushMap(mCellInfoNr);
                }
                ret.putArray("nr", array);
            }
        }
        return ret;
    }

    private WritableMap processCellInfo(CellInfoLte cellInfoLte) {
        WritableMap ret = Arguments.createMap();
        CellInfoUtils.safeAddToMap(ret, "cellIdentity", processCellIdentity(cellInfoLte.getCellIdentity()));
        CellInfoUtils.safeAddToMap(ret, "cellSignalStrength", processCellSignalStrength(cellInfoLte.getCellSignalStrength()));
        return ret;
    }

    private WritableMap processCellIdentity(CellIdentityLte cellIdentity) {
        if ( cellIdentity == null ) return null;
        WritableMap ret = Arguments.createMap();
        // TODO handle bands
        CellInfoUtils.safeAddToMap(ret, "bandwidth", CellInfoUtils.getBandwidth(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "ci", CellInfoUtils.getCi(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "earfcn", CellInfoUtils.getEarfcn(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "mccString", CellInfoUtils.getMccString(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "mncString", CellInfoUtils.getMncString(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "pci", CellInfoUtils.getPci(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "tac", CellInfoUtils.getTac(cellIdentity));
        return ret;
    }

    private WritableMap processCellSignalStrength(CellSignalStrengthLte cellSignalStrength) {
        if ( cellSignalStrength == null ) return null;
        WritableMap ret = Arguments.createMap();
        CellInfoUtils.safeAddToMap(ret, "cqi", CellInfoUtils.getCqi(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "rsrp", CellInfoUtils.getRsrp(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "rsrq", CellInfoUtils.getRsrq(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "rssi", CellInfoUtils.getRssi(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "rssnr", CellInfoUtils.getRssnr(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "timingAdvance", CellInfoUtils.getTimingAdvance(cellSignalStrength));
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private WritableMap processCellInfo(CellInfoNr cellInfoNr) {
        if ( cellInfoNr == null ) return null;
        WritableMap ret = Arguments.createMap();
        CellInfoUtils.safeAddToMap(ret, "cellIdentity", processCellIdentity((CellIdentityNr)cellInfoNr.getCellIdentity()));
        CellInfoUtils.safeAddToMap(ret, "cellSignalStrength", processCellSignalStrength((CellSignalStrengthNr)cellInfoNr.getCellSignalStrength()));
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private WritableMap processCellIdentity(CellIdentityNr cellIdentity) {
        if ( cellIdentity == null ) return null;
        WritableMap ret = Arguments.createMap();
        CellInfoUtils.safeAddToMap(ret, "mccString", cellIdentity.getMccString());
        CellInfoUtils.safeAddToMap(ret, "mncString", cellIdentity.getMncString());
        CellInfoUtils.safeAddToMap(ret, "nci", CellInfoUtils.getNci(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "nrarfcn", CellInfoUtils.getNrarfcn(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "pci", CellInfoUtils.getPci(cellIdentity));
        CellInfoUtils.safeAddToMap(ret, "tac", CellInfoUtils.getTac(cellIdentity));
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private WritableMap processCellSignalStrength(CellSignalStrengthNr cellSignalStrength) {
        if ( cellSignalStrength == null ) return null;
        WritableMap ret = Arguments.createMap();
        CellInfoUtils.safeAddToMap(ret, "csiRsrp", CellInfoUtils.getCsiRsrp(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "csiRsrq", CellInfoUtils.getCsiRsrq(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "csiSinr", CellInfoUtils.getCsiSinr(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "csiSinr", CellInfoUtils.getCsiSinr(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "ssRsrp", CellInfoUtils.getSsRsrp(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "ssRsrq", CellInfoUtils.getSsRsrq(cellSignalStrength));
        CellInfoUtils.safeAddToMap(ret, "ssSinr", CellInfoUtils.getSsSinr(cellSignalStrength));
        return ret;
    }

}

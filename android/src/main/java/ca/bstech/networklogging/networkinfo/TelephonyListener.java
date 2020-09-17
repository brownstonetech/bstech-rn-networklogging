package ca.bstech.networklogging.networkinfo;

import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

import java.util.List;

import ca.bstech.networklogging.Constants;

public class TelephonyListener extends PhoneStateListener {

    private PhoneCallStateUpdate callStatCallBack;

    public TelephonyListener(PhoneCallStateUpdate callStatCallBack) {
        super();
        this.callStatCallBack = callStatCallBack;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
//        this.callStatCallBack.phoneCallStateUpdated(state, incomingNumber);
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
//        this.callStatCallBack.phoneCallForwardingIndicatorUpdated(cfi);
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        try {
            this.callStatCallBack.phoneCellInfoUpdated(cellInfo);
        } catch (Exception e) {
            Log.e(Constants.MODULE_NAME, "Processing CellInfoChanged event encounter unexpected exception", e);
        }
    }

    @Override
    public void onDataActivity(int direction) {
//        this.callStatCallBack.phoneDataActivityUpdated(direction);
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
//        this.callStatCallBack.phoneDataConnectionStateUpdated(state);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//        this.callStatCallBack.phoneSignalStrengthsUpdated(signalStrength);
    }

    public interface PhoneCallStateUpdate {
        void phoneCellInfoUpdated(List<CellInfo> cellInfo);
//        void phoneCellLocationUpdated(CellLocation location);
//        void phoneSignalStrengthsUpdated(SignalStrength signalStrength);
    }

}
package ca.bstech.networklogging.networkinfo;

import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;

import ca.bstech.networklogging.Constants;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class CellInfoCallback extends TelephonyManager.CellInfoCallback {

    private TelephonyListener.PhoneCallStateUpdate callStatCallBack;

    public CellInfoCallback(TelephonyListener.PhoneCallStateUpdate callStatCallBack) {
        super();
        this.callStatCallBack = callStatCallBack;
    }

    @Override
    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {
        try {
            Log.d(Constants.MODULE_NAME, "Received cell info from requestCellInfoUpdate");
            this.callStatCallBack.phoneCellInfoUpdated(cellInfo);
        } catch (Exception e) {
            Log.e(Constants.MODULE_NAME, "Processing CellInfoChanged event encounter unexpected exception", e);
        }
    }
}

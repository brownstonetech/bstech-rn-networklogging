package ca.bstech.networklogging.usage;

import android.annotation.SuppressLint;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;

import ca.bstech.networklogging.Constants;

public class NetworkStatsHelper {

    private NetworkStatsManager networkStatsManager;
    private int packageUid;
    private ReactApplicationContext reactContext;
    private TelephonyManager tm;

    public NetworkStatsHelper(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
        networkStatsManager = (NetworkStatsManager) reactContext.getSystemService(Context.NETWORK_STATS_SERVICE);
        packageUid = reactContext.getApplicationInfo().uid;
        tm = (TelephonyManager) reactContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

//    public NetworkStats.Bucket getAllNetworkStatesMobile(Context context) throws RemoteException {
//        return getAllNetworkStatsMobile(context, null, null);
//    }
//
//    public NetworkStats.Bucket getAllNetworkStatsMobile(Context context, Long startDate, Long endDate) throws RemoteException {
//        NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
//                getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
//                startDate != null ? startDate : 0,
//                endDate != null ? endDate : System.currentTimeMillis());
//        return bucket;
//    }

    public NetworkStats.Bucket getPackageNetworkStatsMobile() {
        return getPackageNetworkStatsMobile(null, null);
    }

    public NetworkStats.Bucket getPackageNetworkStatsMobile(Long startDate, Long endDate) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(ConnectivityManager.TYPE_MOBILE),
                    startDate != null ? startDate : 0,
                    endDate != null ? endDate : System.currentTimeMillis(),
                    packageUid);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            networkStats.getNextBucket(bucket);
            return bucket;
        } finally {
            if (networkStats != null) {
                networkStats.close();
            }
        }
    }

    private String getSubscriberId(int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            return tm.getSubscriberId();
        }
        return "";
    }

    private WritableArray getDataUsage(long startTs, long endTs) {
        WritableArray appStats = getNetworkManagerStats(startTs, endTs);
        return appStats;
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     * Array contains 2 elements:
     * [0] start timestamp
     * [1] end timestamp
     * [2] Tx KB
     * [3] Rx KB
     */
    private WritableArray getNetworkManagerStats(Long startDate, Long endDate) {
        WritableArray array = Arguments.createArray();
        //Log.i(TAG, "##### Step getNetworkManagerStats(" + uid + ", " + name + ", ...)");

        NetworkStats.Bucket bucket = getPackageNetworkStatsMobile(
                startDate, endDate);
        array.pushString(String.valueOf(bucket.getStartTimeStamp()));
        array.pushString(String.valueOf(bucket.getEndTimeStamp()));
        array.pushInt((int)bucket.getTxBytes()/1024);
        array.pushInt((int)bucket.getRxBytes()/1024);
        Log.i(Constants.MODULE_NAME, "##### getNetworkManagerStats "
                + " - tx: " + bucket.getTxBytes() + " | rx: " + bucket.getRxBytes());
        return array;
    }

}

package ca.bstech.networklogging.logging;

import android.net.TrafficStats;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrengthNr;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import ca.bstech.networklogging.ApplicationException;
import ca.bstech.networklogging.Constants;
import ca.bstech.networklogging.networkinfo.CellInfos;
import ca.bstech.networklogging.networkinfo.TelephonyHelper;

import static ca.bstech.networklogging.Constants.ICMP_PACKET;

public class NetworkLoggingHelper {

    private ReactApplicationContext reactContext;
    private TelephonyHelper telephonyHelper;

    private Observer pingObserver;
    private Observer netInfoObserver;
    private Observer locationObserver;

    private LogFileWriter logFileWriter;

    private LoggingItem currentLoggingItem;

    private Timer loggingIntervalTimer = new Timer();
    private TimerTask loggingTask;
    private Promise savedPromise;
    private String imsi;
    private String imei;

    public NetworkLoggingHelper(ReactApplicationContext reactContext, TelephonyHelper telephonyHelper) {
        this.reactContext = reactContext;
        this.telephonyHelper = telephonyHelper;
        pingObserver = new LoggingPingObserver();
        netInfoObserver = new NetworkInfo();
        locationObserver = new LocationObserver();
        currentLoggingItem = new LoggingItem();
    }

    public Observer getPingObserver() {
        return pingObserver;
    }

    public Observer getNetInfoObserver() {
        return netInfoObserver;
    }

    public Observer getLocationObserver() {
        return locationObserver;
    }

    public void startNetworkLoggingAsync(String imsi, long loggingInveral, final Promise promise) {
        final NetworkLoggingHelper me = this;
        synchronized (this) {
            if (loggingTask != null) {
                promise.reject(Constants.E_LOGGER_ALREADY_STARTED, "Network logging has already started.");
            }
            try {
                File file = new File(reactContext.getCacheDir(), "networklogging.json");
                logFileWriter = new LogFileWriter(file);
                loggingTask = new TimerTask() {

                    @Override
                    public void run() {
                        try {
                            LoggingItem loggingItem;
                            synchronized (me) {
                                if ( logFileWriter == null ) {
                                    Log.d(Constants.MODULE_NAME, "Detected loggingTask run after stop at point 1");
                                    return;
                                }
                                // switching currentLoggingItem;
                                loggingItem = switchLoggingItem(false);
                            }

                            // Capture realtime network info
                            loggingItem.setNetworkType(telephonyHelper.getDataNetworkType());
                            // TODO: accessNetworkType
                            // loggingItem.setAccessNetworkType(??);

                            // Capture historical net usage stats
                            long endTxBytes = currentLoggingItem.getStartTxBytes();
                            long endRxBytes = currentLoggingItem.getStartRxBytes();
                            calculateMbps(loggingItem, endTxBytes, endRxBytes);
                            calculateMedianLatency(loggingItem);
                            synchronized(me) {
                                if ( logFileWriter == null ) {
                                    Log.d(Constants.MODULE_NAME, "Detected loggingTask run after stop at point 2");
                                    return;
                                }
                                logFileWriter.appendLoggingItem(loggingItem);
                            }
                        } catch (Exception e) {
                            // catch all exception during task execution
                            if (e instanceof IOException) {
                                Log.e(Constants.MODULE_NAME, "Writing log file failure", e);
                                promise.reject(Constants.E_LOGFILE_WRITE, "", e);
                            } else {
                                Log.e(Constants.MODULE_NAME, "Unexpected exception when running network logging task", e);
                                promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
                            }
                            synchronized(me) {
                                savedPromise = null;
                                try {
                                    cleanupLoggingTask();
                                } catch (Exception e1) {
                                    Log.e(Constants.MODULE_NAME, "Unexpected exception when stopNetworkLogging", e);
                                }
                            }
                        }
                    }

                };
                loggingIntervalTimer.purge();
                currentLoggingItem.setImsi(imsi);
                // switch once when new task start
                switchLoggingItem(true);
                loggingIntervalTimer.scheduleAtFixedRate(loggingTask, loggingInveral, loggingInveral);
                this.savedPromise = promise;
            } catch (Exception e) {
                if (e instanceof IOException) {
                    Log.e(Constants.MODULE_NAME, "Cannot create logfile for writting", e);
                    promise.reject(Constants.E_LOGFILE_CREATE, "Cannot create logfile for writing", e);
                }
                Log.e(Constants.MODULE_NAME, "Unexpected exception when initializing network logging task ", e);
                promise.reject(Constants.E_RUNTIME_EXCEPTION, "Unexpected exception when initializing netowrk logging task", e);
            }
        }
    }

    private void calculateMedianLatency(LoggingItem loggingItem) {
        List<Double> latencies = loggingItem.getAccumulatedLatency();
        if ( latencies.size() > 0 ) {
            int middle = latencies.size()/2;
            loggingItem.setLatencyMedian(latencies.get(middle));
        }
    }

    private void calculateMbps(LoggingItem loggingItem, long endTxBytes, long endRxBytes) {
        long period = loggingItem.getPeriod();
        long txBytes = endTxBytes - loggingItem.getStartTxBytes();
        long rxBytes = endRxBytes - loggingItem.getStartRxBytes();
        Log.d(Constants.MODULE_NAME, "Get networkStats"
                +": startTimeStamp="+loggingItem.getStartTs()
                +", rxBytes="+rxBytes
                +", txBytes="+txBytes
                +", period="+period
        );

        double downlinkBps = ((double) rxBytes * 8) /((double)period / 1000)/(1024 * 1024);
        loggingItem.setDownlinkBps(new BigDecimal(downlinkBps).setScale(2, RoundingMode.HALF_EVEN));

        double uplinkBps = ((double) txBytes * 8) / ((double)period / 1000)/(1024 * 1024);
        loggingItem.setUplinkBps(new BigDecimal(uplinkBps).setScale(2, RoundingMode.HALF_EVEN));
    }

    private synchronized LoggingItem switchLoggingItem(boolean ignorePrevious) {
        LoggingItem previousLoggingItem = currentLoggingItem;
        currentLoggingItem = new LoggingItem(previousLoggingItem);
        long txBytesTotal = TrafficStats.getTotalTxBytes();
        long rxBytesTotal = TrafficStats.getTotalRxBytes();
        currentLoggingItem.setStartTxBytes(txBytesTotal);
        currentLoggingItem.setStartRxBytes(rxBytesTotal);
        if ( !ignorePrevious ) {
            previousLoggingItem.setTxBytes((int) (txBytesTotal - previousLoggingItem.getStartTxBytes()));
            previousLoggingItem.setRxBytes((int) (rxBytesTotal - previousLoggingItem.getStartRxBytes()));
            previousLoggingItem.setPeriod(currentLoggingItem.getStartTs() - previousLoggingItem.getStartTs());
        }
        return previousLoggingItem;
    }

    private synchronized void cleanupLoggingTask() throws ApplicationException {
        loggingTask.cancel();
        loggingTask = null;
        loggingIntervalTimer.purge();
        try {
            logFileWriter.close();
        } catch (IOException e) {
            throw new ApplicationException(Constants.E_LOGFILE_CLOSE, "Close logfile error", e);
        } finally {
            logFileWriter = null;
        }
    }

    public void stopNetworkLogging() {
        synchronized (this) {
            try {
                if (loggingTask == null) {
                    // Already done cleanup.
                    // check on saved error
                    if (this.savedPromise != null) {
                        this.savedPromise.reject(Constants.E_INVALID_PARAM, "NetworkLogging task is not running");
                        this.savedPromise = null;
                    }
                    return;
                }
                if (this.savedPromise != null) {
                    this.savedPromise.resolve(logFileWriter.getFilePath());
                }
                cleanupLoggingTask();
            } catch (ApplicationException e) {
                Log.e(Constants.MODULE_NAME, "Unexpected exception when closing log file", e);
                if (this.savedPromise != null) {
                    this.savedPromise.reject(e.getCode(), e.getMessage(), e);
                }
            } catch (Exception e) {
                Log.e(Constants.MODULE_NAME, "Unexpected exception when stopNetworkLogging task", e);
                if (this.savedPromise != null) {
                    this.savedPromise.reject(Constants.E_RUNTIME_EXCEPTION, e.getMessage(), e);
                }
            } finally {
                this.savedPromise = null;
            }
        }
    }


    public class LoggingPingObserver implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if ( arg == null ) return;
            synchronized (NetworkLoggingHelper.this) {
                if (currentLoggingItem == null) return;
                ReadableMap map = (ReadableMap) arg;
                Double time = null;
                if (ICMP_PACKET.equals(map.getString("type"))) {
                    ReadableMap parsed = map.getMap("icmpPacket");
                    if (parsed != null) {
                        time = parsed.getDouble("time");
                    }
                }
                if (time != null && currentLoggingItem != null) {
                    if (currentLoggingItem.getLatencyMax() == null ||
                            time > currentLoggingItem.getLatencyMax()) {
                        currentLoggingItem.setLatencyMax(time);
                    }
                    if (currentLoggingItem.getLatencyMin() == null ||
                            time < currentLoggingItem.getLatencyMin()) {
                        currentLoggingItem.setLatencyMin(time);
                    }
                    currentLoggingItem.getAccumulatedLatency().add(time);
                }
            }
        }
    }

    public class NetworkInfo implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if ( arg == null ) return;
            synchronized(NetworkLoggingHelper.this) {
                Log.d(Constants.MODULE_NAME, "Processing network info for log file");
                if (currentLoggingItem == null) return;
                CellInfos cellInfos = (CellInfos)arg;
                List<CellInfoLte> cellInfoLteList = cellInfos.getCellInfoLte();
                if ( cellInfoLteList != null && cellInfoLteList.size()>0 ) {
                    if (cellInfoLteList.size()>1 ) {
                        Log.w(Constants.MODULE_NAME, "Find more than one cellInfoLTE items, will take the first one");
                    }
                    // Just return the first one
                    CellInfoLte cellInfoLte = cellInfoLteList.get(0);
                    currentLoggingItem.setCellIdentityLte(cellInfoLte.getCellIdentity());
                    currentLoggingItem.setCellSignalStrengthLte(cellInfoLte.getCellSignalStrength());
                }
                List<CellInfoNr> cellInfoNrList = cellInfos.getCellInfoNr();
                if ( cellInfoNrList != null && cellInfoNrList.size()>0 ) {
                    if (cellInfoNrList.size() >1 ) {
                        Log.w(Constants.MODULE_NAME, "Find more than one cellInfoNr items, will take the first one");
                    }
                    // Just return the first one
                    CellInfoNr cellInfoNr = cellInfoNrList.get(0);
                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        currentLoggingItem.setCellIdentityNr((CellIdentityNr) cellInfoNr.getCellIdentity());
                        currentLoggingItem.setCellSignalStrengthNr((CellSignalStrengthNr) cellInfoNr.getCellSignalStrength());
                    }
                }
                Log.d(Constants.MODULE_NAME, "Processed network info for log file");
            }
        }
    }

    public class LocationObserver implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            ReadableMap location = (ReadableMap) arg;
            double latitude = location.getDouble("latitude");
            double longitude = location.getDouble("longitude");
            synchronized(NetworkLoggingHelper.this) {
                currentLoggingItem.setLatitude(latitude);
                currentLoggingItem.setLongitude(longitude);
            }
        }

    }

}

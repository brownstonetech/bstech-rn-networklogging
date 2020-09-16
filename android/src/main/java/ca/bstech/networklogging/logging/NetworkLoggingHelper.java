package ca.bstech.networklogging.logging;

import android.app.usage.NetworkStats;
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
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import ca.bstech.networklogging.ApplicationException;
import ca.bstech.networklogging.Constants;
import ca.bstech.networklogging.networkinfo.CellInfos;
import ca.bstech.networklogging.usage.NetworkStatsHelper;

import static ca.bstech.networklogging.Constants.ICMP_PACKET;

public class NetworkLoggingHelper {

    private ReactApplicationContext reactContext;

    private Observer pingObserver;
    private Observer netInfoObserver;
    private Observer locationObserver;
    private NetworkStatsHelper networkStatsHelper;
    private Exception savedException;

    private LogFileWriter logFileWriter;

    private LoggingItem currentLoggingItem;

    private Timer loggingIntervalTimer = new Timer();
    private TimerTask loggingTask;
    private Promise savedPromise;

    public NetworkLoggingHelper(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
        pingObserver = new LoggingPingObserver();
        netInfoObserver = new NetworkInfo();
        networkStatsHelper = new NetworkStatsHelper(reactContext);
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

    public void startNetworkLoggingAsync(long loggingInveral, final Promise promise) {
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
                            long now = System.currentTimeMillis();
                            LoggingItem loggingItem;
                            synchronized (me) {
                                // switching currentLoggingItem;
                                loggingItem = currentLoggingItem;
                                currentLoggingItem = new LoggingItem(loggingItem);
                                loggingItem.setPeriod(currentLoggingItem.getStartTs() - loggingItem.getStartTs());
                            }
                            // wrap up
                            NetworkStats.Bucket bucket = networkStatsHelper.getPackageNetworkStatsMobile(loggingItem.getStartTs(), currentLoggingItem.getStartTs()-1);
                            loggingItem.setDataUsage(bucket);
                            if (bucket != null) {
                                long period = loggingItem.getPeriod();
                                long rxBytes = bucket.getRxBytes();
                                loggingItem.setDownlinkBps(((double) rxBytes * 8) / (1024 * 1024) / (period / 1000));
                                long txBytes = bucket.getTxBytes();
                                loggingItem.setUplinkBps(((double) txBytes * 8) / (1024 * 1024) / (period / 1000));
                            }
                            logFileWriter.appendLoggingItem(loggingItem);
                        } catch (Exception e) {
                            // catch all exception during task execution
                            if (e instanceof IOException) {
                                Log.e(Constants.MODULE_NAME, "Writing log file failure", e);
                                promise.reject(Constants.E_LOGFILE_WRITE, "", e);
                            } else {
                                Log.e(Constants.MODULE_NAME, "Unexpected exception when running network logging task", e);
                                promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
                            }
                            savedPromise = null;
                            try {
                                cleanupLoggingTask();
                            } catch (Exception e1) {
                                Log.e(Constants.MODULE_NAME, "Unexpected exception when stopNetworkLogging", e);
                            }
                        }
                    }

                };
                // switch once when new task start
                loggingIntervalTimer.purge();
                currentLoggingItem = new LoggingItem(currentLoggingItem);
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

    private void cleanupLoggingTask() throws ApplicationException {
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
                if (currentLoggingItem == null) return;
                CellInfos cellInfos = (CellInfos)arg;
                List<CellInfoLte> cellInfoLteList = cellInfos.getCellInfoLte();
                if ( cellInfoLteList != null && cellInfoLteList.size()>0 ) {
                    if (cellInfoLteList.size()>1 ) {
                        // TODO notify application there is something unusual,
                    }
                    // Just return the first one
                    CellInfoLte cellInfoLte = cellInfoLteList.get(0);
                    currentLoggingItem.setCellIdentityLte(cellInfoLte.getCellIdentity());
                    currentLoggingItem.setCellSignalStrengthLte(cellInfoLte.getCellSignalStrength());
                }
                List<CellInfoNr> cellInfoNrList = cellInfos.getCellInfoNr();
                if ( cellInfoNrList != null && cellInfoNrList.size()>0 ) {
                    if (cellInfoNrList.size() >1 ) {
                        // TODO notify application there is something unusual
                    }
                    // Just return the first one
                    CellInfoNr cellInfoNr = cellInfoNrList.get(0);
                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        currentLoggingItem.setCellIdentityNr((CellIdentityNr) cellInfoNr.getCellIdentity());
                        currentLoggingItem.setCellSignalStrengthNr((CellSignalStrengthNr) cellInfoNr.getCellSignalStrength());
                    }
                }
            }
        }
    }

    public class LocationObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            ReadableMap map = (ReadableMap) arg;
            // TODO implement
        }
    }

}

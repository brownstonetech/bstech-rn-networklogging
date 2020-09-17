package ca.bstech.networklogging.ping;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;

import ca.bstech.networklogging.ApplicationException;
import ca.bstech.networklogging.Constants;
import ca.bstech.networklogging.Consumer;


public class PingHelper {

    private Process process = null;
    //    private static Object processSync = new Object();
    private boolean breakSignal = false;

    private HandlerThread handlerThread = new HandlerThread("BSTPingThread");
    private HandlerThread cleanupThread = new HandlerThread("BSTPingCleanupThread");
    private final PingResultParser pingResultParser;

    public PingHelper(ReactApplicationContext reactContext) {
        handlerThread.start();
        cleanupThread.start();
        pingResultParser = new PingResultParser();
    }

    public Observable getObservable() {
        return pingResultParser;
    };

    public void startPingAsync(final String domainName, ReadableMap params, ReadableMap pingOptions) throws ApplicationException {
        try {
            if (domainName == null || domainName.length() == 0) {
                throw new IllegalArgumentException("domainName");
            }

            final Map<String, String> options = new LinkedHashMap<>();
            if (pingOptions != null) {
                ReadableMapKeySetIterator iter = pingOptions.keySetIterator();
                while (iter.hasNextKey()) {
                    String key = iter.nextKey();
                    String value = pingOptions.getString(key);
                    options.put(key, value);
                }
            }
            final Integer durationSeconds = params.hasKey("durationSeconds") ?
                    params.getInt("durationSeconds") : null;
            final int reportIntervalSeconds = params.hasKey("reportIntervalSeconds") ?
                    params.getInt("reportIntervalSeconds") : 30;
            Handler mHandler = new Handler(handlerThread.getLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
//                        pingEventEmitter.emitStartMessage();
                        ping(domainName, options, durationSeconds,
                                reportIntervalSeconds, pingResultParser);
//                        pingEventEmitter.emitStopMessage();
                    } catch (RuntimeException e) {
                        Log.e(Constants.MODULE_NAME, Constants.E_RUNTIME_EXCEPTION, e);
//                        pingEventEmitter.emitExceptionMessage(Constants.E_RUNTIME_EXCEPTION, e);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(Constants.E_INVALID_PARAM, e.getMessage(), e);
        }
    }

    public void stopPingAsync(final Promise promise) {
        Handler mHandler = new Handler(cleanupThread.getLooper());
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    stop();
                    if (promise != null) {
                        promise.resolve(null);
                    }
                } catch (RuntimeException e) {
                    Log.e(Constants.MODULE_NAME, "Stop ping process encounter exception", e);
                    if (promise != null) {
                        promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
                    }
                }
            }
        });
    }

    private void stop() {
        synchronized (this) {
            if (process == null) return;
            breakSignal = true;
        }
        // waiting for grace stop
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // do nothing
            }
            if (process == null) return;
        }
        synchronized (this) {
            if (process != null) {
                // force stop
                process.destroy();
                process = null;
            }
        }
    }

    private void ping(String domain, Map<String, String> options, Integer durationSeconds, int reportIntervalSeconds, Consumer<String> resultHandler) {
        long startTime = System.currentTimeMillis();
        Long endTime = durationSeconds == null ? null : startTime + durationSeconds * 1000;
        // Loop until duration approach
        while (true) {
            // stop every report interval to retrieve statistic and summary data
            int runningDuration = reportIntervalSeconds;
            if (endTime != null) {
                runningDuration = (int) ((endTime - System.currentTimeMillis()) / 1000) + 1;
                if (runningDuration > reportIntervalSeconds) {
                    runningDuration = reportIntervalSeconds;
                }
            }
            String command = createSimplePingCommand(domain, options, runningDuration);
            ping(command, resultHandler);
            long currentTime = System.currentTimeMillis();
            if (breakSignal || endTime != null && (currentTime >= endTime)) {
                breakSignal = false;
                break;
            }
        }
    }

    private void ping(String command, Consumer<String> resultHandler) {
        synchronized (this) {
            if (process != null) {
                try {
                    process.destroy();
                    process = null;
                } catch (Exception e) {
                    throw new RuntimeException("Terminating running process before start failed", e);
                }
            }
        }
        try {
            synchronized (this) {
                if (breakSignal) return;
                process = Runtime.getRuntime().exec(command);
            }
            InputStream is = process.getInputStream();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                try {
                    String line;
                    while (null != (line = reader.readLine())) {
                        resultHandler.accept(line);
                        if (breakSignal) break;
                    }
                } finally {
                    reader.close();
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception while running ping command", e);
        } finally {
            synchronized (this) {
                if (null != process) {
                    process.destroy();
                    process = null;
                }
            }
        }
    }

    private static String createSimplePingCommand(String domain, Map<String, String> options, int durationSeconds) {
        StringBuilder sb = new StringBuilder("/system/bin/ping");
        if (options != null) {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                sb.append(" ").append(entry.getKey()).append(" ").append(entry.getValue());
            }
        }
        sb.append(" -w ").append(durationSeconds);
        sb.append(" ").append(domain);
        return sb.toString();
    }

}
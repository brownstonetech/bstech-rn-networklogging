package ca.bstech.networklogging.logging;

import android.annotation.TargetApi;
import android.os.Build;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfo;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.util.JsonWriter;

import com.facebook.react.bridge.WritableMap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFileWriter {

    private JsonWriter writer;
    private OutputStream fos;
    private SimpleDateFormat tsFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");

    public LogFileWriter(String filepath) throws IOException {
        fos = new FileOutputStream(filepath, false);
        writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        appendTitle();
    }

    public void close() throws IOException {
        writer.setIndent("  ");
        writer.endArray();
        writer.flush();
        writer.close();
        fos.flush();
        fos.close();
    }

    private synchronized void appendTitle() throws IOException {
        writer.setIndent("  ");
        writer.beginArray();
        writer.setIndent("");
        outputTitle();
        writer.endArray();
    }

    public synchronized void appendLoggingItem(LoggingItem loggingItem) throws IOException {
        writer.setIndent("  ");
        writer.beginArray();
        writer.setIndent("");
        outputColumns(loggingItem);
        writer.endArray();
    }

    private void outputColumns(LoggingItem loggingItem) throws IOException {
        writer.value(tsFormatter.format(new Date(loggingItem.getStartTs())));
        writer.value(loggingItem.getImsi());
        writer.value(loggingItem.getLongitude());
        writer.value(loggingItem.getLatitude());
        writer.value(loggingItem.getAccessNetworkType());
        writer.value(loggingItem.getNetworkType());
        writeCellIdentityLte(loggingItem.getCellIdentityLte());
        writeCellIdentityNr(loggingItem.getCellIdentityNr());
        writeCellSignalStrengthLte(loggingItem.getCellSignalStrengthLte());
        writeCellSignalStrengthNr(loggingItem.getCellSignalStrengthNr());
        writeThroughoutput(loggingItem);
        writeLatency(loggingItem);
    }

    private void outputTitle() throws IOException {
        writer.value("Current Time");
        writer.value("IMSI");
        writer.value("Longitude");
        writer.value("Latitude");
        writer.value("Access Network Type");
        writer.value("Network Type");
        writeCellIdentityLteTitle();
        writeCellIdentityNrTitle();
        writeCellSignalStrengthLteTitle();
        writeCellSignalStrengthNrTitle();
        writeThroughoutputTitle();
        writeLatencyTitle();
    }

    private void writeCellIdentityLte(CellIdentityLte cellIdentityLte) throws IOException {
        // Group separator
        writer.value("|");
        // TODO figure where bands comes from
        // writer.value(cellIdentityLte==null?null:cellIdentityLte.getBands());
        writer.value(getBandwidth(cellIdentityLte));
        writer.value(cellIdentityLte==null?null:cellIdentityLte.getCi());
        writer.value(getEarfcn(cellIdentityLte));
        writer.value(getMccString(cellIdentityLte));
        writer.value(getMncString(cellIdentityLte));
        writer.value(cellIdentityLte==null?null:cellIdentityLte.getPci());
        writer.value(cellIdentityLte==null?null:cellIdentityLte.getTac());
    }

    @TargetApi(24)
    private Integer getEarfcn(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            // Do something for nougat and above versions
            int earfcn = cellIdentity.getEarfcn();
            if ( earfcn == CellInfo.UNAVAILABLE ) return null;
            return earfcn;
        } else {
            // do something for phones running an SDK before lollipop
            return null;
        }
    }

    @TargetApi(28)
    private Integer getBandwidth(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            // Do something for Android 9 Pie and above versions
            int bandwidth = cellIdentity.getBandwidth();
            if ( bandwidth == CellInfo.UNAVAILABLE ) return null;
            return bandwidth;
        } else {
            // do something for phones running an SDK before Android 9 Pie
            return null;
        }
    }

    @TargetApi(28)
    private String getMccString(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            // Do something for Android 9 Pie and above versions
            String ret = cellIdentity.getMccString();
            return ret;
        }
        // do something for phones running an SDK before Android 9 Pie
        return null;
    }

    @TargetApi(28)
    private String getMncString(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            // Do something for Android 9 Pie and above versions
            String ret = cellIdentity.getMncString();
            return ret;
        }
        // do something for phones running an SDK before Android 9 Pie
        return null;
    }

    private void writeCellIdentityLteTitle() throws IOException {
        writer.value(""); // For separator
        // TODO
        // writer.value("Bands");
        writer.value("LTE Bandwidth");
        writer.value("Cell Identity");
        writer.value("Earfcn");
        writer.value("LTE MCC");
        writer.value("LTE MNC");
        writer.value("LTE PCI");
        writer.value("LTE TAC");
    }

    private void writeCellIdentityNr(CellIdentityNr cellIdentityNr) throws IOException {
        // Group separator
        writer.value("|");
        // TODO implement
        // writer.value(cellIdentityNr == null? null: cellIdentityNr.getBands());
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getMccString());
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getMncString());
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getNci());
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getNrarfcn());
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getPci());
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getTac());
    }

    private void writeCellIdentityNrTitle() throws IOException {
        writer.value(""); // For separator
        // TODO implement
        // writer.value("5G Bands");
        writer.value("5G MCC");
        writer.value("5G MNC");
        writer.value("NR Cell Identity");
        writer.value("BR ARFCN");
        writer.value("5G PCI");
        writer.value("5G TAC");
    }

    private void writeCellSignalStrengthLte(CellSignalStrengthLte cellSignalStrengthLte) throws IOException {
        // TODO implement
    }

    private void writeCellSignalStrengthLteTitle() throws IOException {
        // TODO implement
    }

    private void writeCellSignalStrengthNr(CellSignalStrengthNr cellSignalStrengthNr) throws IOException {
        // TODO implement
    }

    private void writeCellSignalStrengthNrTitle() throws IOException {
        // TODO implement
    }

    private void writeThroughoutput(LoggingItem loggingItem) throws IOException {
        writer.value("|");
        writer.value(loggingItem.getDownlinkBps());
        writer.value(loggingItem.getUplinkBps());
    }

    private void writeThroughoutputTitle() throws IOException {
        writer.value("");
        writer.value("Downlink Throughput");
        writer.value("Uplink Throughput");
    }

    private void writeLatency(LoggingItem loggingItem) throws IOException {
        writer.value("|");
        writer.value(loggingItem.getLatencyMin());
        writer.value(loggingItem.getLatencyMedian());
        writer.value(loggingItem.getLatencyMax());
    }

    private void writeLatencyTitle() throws IOException {
        writer.value("Latency");
        writer.value("best");
        writer.value("median");
        writer.value("worst");
    }

}

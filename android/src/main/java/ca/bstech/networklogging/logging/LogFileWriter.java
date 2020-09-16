package ca.bstech.networklogging.logging;

import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.util.JsonWriter;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.bstech.networklogging.Constants;
import ca.bstech.networklogging.networkinfo.CellInfoUtils;

public class LogFileWriter {

    private JsonWriter writer;
    private OutputStream fos;
    private SimpleDateFormat tsFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");
    private String filePath;

    public LogFileWriter(File file) throws IOException {
        this.filePath = file.getCanonicalPath();
        fos = new FileOutputStream(file, false);
        writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        appendTitle();
        Log.d(Constants.MODULE_NAME, "Created log file path:"+filePath);
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
        writer.value(CellInfoUtils.getBandwidth(cellIdentityLte));
        writer.value(cellIdentityLte==null?null:CellInfoUtils.filterUnavailable(cellIdentityLte.getCi()));
        writer.value(CellInfoUtils.getEarfcn(cellIdentityLte));
        writer.value(CellInfoUtils.getMccString(cellIdentityLte));
        writer.value(CellInfoUtils.getMncString(cellIdentityLte));
        writer.value(cellIdentityLte==null?null:CellInfoUtils.filterUnavailable(cellIdentityLte.getPci()));
        writer.value(cellIdentityLte==null?null:CellInfoUtils.filterUnavailable(cellIdentityLte.getTac()));
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
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: CellInfoUtils.filterUnavailable(cellIdentityNr.getNci()));
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: CellInfoUtils.filterUnavailable(cellIdentityNr.getNrarfcn()));
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: CellInfoUtils.filterUnavailable(cellIdentityNr.getPci()));
        writer.value(cellIdentityNr == null||android.os.Build.VERSION.SDK_INT < 29? null: CellInfoUtils.filterUnavailable(cellIdentityNr.getTac()));
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
        // Group separator
        writer.value("|");

        writer.value(cellSignalStrengthLte == null||android.os.Build.VERSION.SDK_INT < 26?null:CellInfoUtils.filterUnavailable(cellSignalStrengthLte.getCqi()));
        writer.value(cellSignalStrengthLte == null||android.os.Build.VERSION.SDK_INT < 26?null:CellInfoUtils.filterUnavailable(cellSignalStrengthLte.getRsrp()));
        writer.value(cellSignalStrengthLte == null||android.os.Build.VERSION.SDK_INT < 26?null:CellInfoUtils.filterUnavailable(cellSignalStrengthLte.getRsrq()));
        writer.value(cellSignalStrengthLte == null||android.os.Build.VERSION.SDK_INT < 29?null:CellInfoUtils.filterUnavailable(cellSignalStrengthLte.getRssi()));
        writer.value(cellSignalStrengthLte == null||android.os.Build.VERSION.SDK_INT < 26?null:CellInfoUtils.filterUnavailable(cellSignalStrengthLte.getRssnr()));
        writer.value(cellSignalStrengthLte == null?null:CellInfoUtils.filterUnavailable(cellSignalStrengthLte.getTimingAdvance()));
    }

    private void writeCellSignalStrengthLteTitle() throws IOException {
        writer.value(""); // For separator

        writer.value("LTE CQI");
        writer.value("LTE RSRP");
        writer.value("LTE RSRQ");
        writer.value("LTE RSSI");
        writer.value("LTE SNR");
        writer.value("LTE TA");
    }

    private void writeCellSignalStrengthNr(CellSignalStrengthNr cellSignalStrengthNr) throws IOException {
        writer.value("|");

        writer.value(cellSignalStrengthNr == null||android.os.Build.VERSION.SDK_INT < 29?null:CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getCsiRsrp()));
        writer.value(cellSignalStrengthNr == null||android.os.Build.VERSION.SDK_INT < 29?null:CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getCsiRsrq()));
        writer.value(cellSignalStrengthNr == null||android.os.Build.VERSION.SDK_INT < 29?null:CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getCsiSinr()));
        writer.value(cellSignalStrengthNr == null||android.os.Build.VERSION.SDK_INT < 29?null:CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getSsRsrp()));
        writer.value(cellSignalStrengthNr == null||android.os.Build.VERSION.SDK_INT < 29?null:CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getSsRsrq()));
        writer.value(cellSignalStrengthNr == null||android.os.Build.VERSION.SDK_INT < 29?null:CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getSsSinr()));
    }

    private void writeCellSignalStrengthNrTitle() throws IOException {
        writer.value(""); // For separator

        writer.value("5G CSI RSRP");
        writer.value("5G CSI RSRQ");
        writer.value("5G CSI SINR");
        writer.value("5G SS RSRP");
        writer.value("5G SS RSRQ");
        writer.value("5G SS SINR");
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

    public String getFilePath() {
        return this.filePath;
    }
}

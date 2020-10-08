package ca.bstech.networklogging.logging;

import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.util.JsonWriter;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.bstech.networklogging.Constants;
import ca.bstech.networklogging.networkinfo.CellInfoUtils;
import ca.bstech.networklogging.networkinfo.CellInfos;

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
        writeCellIdentityLte(loggingItem.getCellInfos());
        writeCellSignalStrengthLte(loggingItem.getCellInfos());
        writeCellIdentityNr(loggingItem.getCellInfos());
        writeCellSignalStrengthNr(loggingItem.getCellInfos());
        writeThroughoutput(loggingItem);
        writeLatency(loggingItem);
    }

    private void outputTitle() throws IOException {
        writer.value("Current Time");
        writer.value("IMEI");
        writer.value("Longitude");
        writer.value("Latitude");
        writer.value("Access Network Type");
        writer.value("Network Type");
        writeCellIdentityLteTitle();
        writeCellSignalStrengthLteTitle();
        writeCellIdentityNrTitle();
        writeCellSignalStrengthNrTitle();
        writeThroughoutputTitle();
        writeLatencyTitle();
    }

    private void writeCellIdentityLte(CellInfos cellInfos) throws IOException {
        // Group separator
        writer.value("|");
        // TODO figure where bands comes from
        // writer.value(cellIdentityLte==null?null:cellIdentityLte.getBands());
        List<CellInfoLte> cellIdentityLte = cellInfos == null? null:cellInfos.getCellInfoLte();
        writer.value(CellInfoUtils.getBandwidthLte(cellIdentityLte));
        writer.value(CellInfoUtils.getCiLte(cellIdentityLte));
        writer.value(CellInfoUtils.getEarfcnLte(cellIdentityLte));
        writer.value(CellInfoUtils.getMccStringLte(cellIdentityLte));
        writer.value(CellInfoUtils.getMncStringLte(cellIdentityLte));
        writer.value(CellInfoUtils.getPciLte(cellIdentityLte));
        writer.value(CellInfoUtils.getTacLte(cellIdentityLte));
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

    private void writeCellIdentityNr(CellInfos cellInfos) throws IOException {
        // Group separator
        writer.value("|");
        List<CellInfoNr> cellIdentityNrList = cellInfos == null? null: cellInfos.getCellInfoNr();
        // TODO implement
        // writer.value(cellIdentityNr == null? null: cellIdentityNr.getBands());
        writer.value(CellInfoUtils.getMccStringNr(cellIdentityNrList));
        writer.value(CellInfoUtils.getMncStringNr(cellIdentityNrList));
        writer.value(CellInfoUtils.getNciNr(cellIdentityNrList));
        writer.value(CellInfoUtils.getNrarfcnNr(cellIdentityNrList));
        writer.value(CellInfoUtils.getPciNr(cellIdentityNrList));
        writer.value(CellInfoUtils.getTacNr(cellIdentityNrList));
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

    private void writeCellSignalStrengthLte(CellInfos cellInfos) throws IOException {
        // Group separator
        writer.value("|");
        List<CellInfoLte> cellIdentityLte = cellInfos == null? null:cellInfos.getCellInfoLte();
        Log.d(Constants.MODULE_NAME, "Writing cellSignalStrengthLte "+cellIdentityLte);
        writer.value(CellInfoUtils.getCqiLte(cellIdentityLte));
        writer.value(CellInfoUtils.getRsrpLte(cellIdentityLte));
        writer.value(CellInfoUtils.getRsrqLte(cellIdentityLte));
        writer.value(CellInfoUtils.getRssiLte(cellIdentityLte));
        writer.value(CellInfoUtils.getRssnrLte(cellIdentityLte));
        writer.value(CellInfoUtils.getTimingAdvanceLte(cellIdentityLte));

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

    private void writeCellSignalStrengthNr(CellInfos cellInfos) throws IOException {
        writer.value("|");
        List<CellInfoNr> cellInfoNrList = cellInfos == null? null:cellInfos.getCellInfoNr();
        writer.value(CellInfoUtils.getCsiRsrp(cellInfoNrList));
        writer.value(CellInfoUtils.getCsiRsrq(cellInfoNrList));
        writer.value(CellInfoUtils.getCsiSinr(cellInfoNrList));
        writer.value(CellInfoUtils.getSsRsrp(cellInfoNrList));
        writer.value(CellInfoUtils.getSsRsrq(cellInfoNrList));
        writer.value(CellInfoUtils.getSsSinr(cellInfoNrList));
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
        writer.value("");
        writer.value("best");
        writer.value("median");
        writer.value("worst");
    }

    public String getFilePath() {
        return this.filePath;
    }
}

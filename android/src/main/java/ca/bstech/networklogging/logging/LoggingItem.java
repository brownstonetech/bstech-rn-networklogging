package ca.bstech.networklogging.logging;

import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LoggingItem {

    private long startTs;
    private long period;

    private String imsi;
    private Double latitude;
    private Double longitude;

    private Integer networkType;
    // TODO how to get this?
    private Integer accessNetworkType;

    private CellIdentityLte cellIdentityLte;
    private CellSignalStrengthLte cellSignalStrengthLte;
    private CellIdentityNr cellIdentityNr;
    private CellSignalStrengthNr cellSignalStrengthNr;

    //    private NetworkStats.Bucket dataUsage;
    private long startRxBytes;
    private long startTxBytes;

    private int txBytes;
    private int rxBytes;

    private BigDecimal downlinkBps;
    private BigDecimal uplinkBps;

    private Double latencyMin;
    private Double latencyMax;
    private Double latencyMedian;
    private List<Double> accumulatedLatency = new ArrayList<>();

    public LoggingItem() {
        this.startTs = System.currentTimeMillis();
    }

    public LoggingItem(LoggingItem previous) {
        this();
        // carry over these fields
        this.imsi = previous.getImsi();
        this.latitude = previous.getLatitude();
        this.longitude = previous.getLongitude();
        this.networkType = previous.getNetworkType();
        this.accessNetworkType = previous.getAccessNetworkType();
        this.cellIdentityLte = previous.getCellIdentityLte();
        this.cellIdentityNr = previous.getCellIdentityNr();
        // Don't carry over
        this.latencyMin = null;
        this.latencyMax = null;
        this.latencyMedian = null;
    }

    public long getStartTs() {
        return startTs;
    }

    public void setStartTs(long startTs) {
        this.startTs = startTs;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getNetworkType() {
        return networkType;
    }

    public void setNetworkType(Integer networkType) {
        this.networkType = networkType;
    }

    public Integer getAccessNetworkType() {
        return accessNetworkType;
    }

    public void setAccessNetworkType(Integer accessNetworkType) {
        this.accessNetworkType = accessNetworkType;
    }

    public CellIdentityLte getCellIdentityLte() {
        return cellIdentityLte;
    }

    public void setCellIdentityLte(CellIdentityLte cellIdentityLte) {
        this.cellIdentityLte = cellIdentityLte;
    }

    public CellIdentityNr getCellIdentityNr() {
        return cellIdentityNr;
    }

    public void setCellIdentityNr(CellIdentityNr cellIdentityNr) {
        this.cellIdentityNr = cellIdentityNr;
    }

    public CellSignalStrengthLte getCellSignalStrengthLte() {
        return cellSignalStrengthLte;
    }

    public void setCellSignalStrengthLte(CellSignalStrengthLte cellSignalStrengthLte) {
        this.cellSignalStrengthLte = cellSignalStrengthLte;
    }

    public CellSignalStrengthNr getCellSignalStrengthNr() {
        return cellSignalStrengthNr;
    }

    public void setCellSignalStrengthNr(CellSignalStrengthNr cellSignalStrengthNr) {
        this.cellSignalStrengthNr = cellSignalStrengthNr;
    }

    public BigDecimal getDownlinkBps() {
        return downlinkBps;
    }

    public void setDownlinkBps(BigDecimal downlinkBps) {
        this.downlinkBps = downlinkBps;
    }

    public BigDecimal getUplinkBps() {
        return uplinkBps;
    }

    public void setUplinkBps(BigDecimal uplinkBps) {
        this.uplinkBps = uplinkBps;
    }

    public Double getLatencyMin() {
        return latencyMin;
    }

    public void setLatencyMin(Double latencyMin) {
        this.latencyMin = latencyMin;
    }

    public Double getLatencyMax() {
        return latencyMax;
    }

    public void setLatencyMax(Double latencyMax) {
        this.latencyMax = latencyMax;
    }

    public Double getLatencyMedian() {
        return latencyMedian;
    }

    public void setLatencyMedian(Double latencyMedian) {
        this.latencyMedian = latencyMedian;
    }

    public List<Double> getAccumulatedLatency() {
        return accumulatedLatency;
    }

    public long getStartRxBytes() {
        return startRxBytes;
    }

    public void setStartRxBytes(long startRxBytes) {
        this.startRxBytes = startRxBytes;
    }

    public long getStartTxBytes() {
        return startTxBytes;
    }

    public void setStartTxBytes(long startTxBytes) {
        this.startTxBytes = startTxBytes;
    }

    public int getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(int txBytes) {
        this.txBytes = txBytes;
    }

    public int getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(int rxBytes) {
        this.rxBytes = rxBytes;
    }

}

package ca.bstech.networklogging.networkinfo;

import android.os.Build;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

public class CellInfoUtils {

    public static Integer getEarfcn(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            // Do something for nougat and above versions
            int earfcn = cellIdentity.getEarfcn();
            return filterUnavailable(earfcn);
        } else {
            // do something for phones running an SDK before lollipop
            return null;
        }
    }

    public static String getEarfcnLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            Integer earfcn = getEarfcn(cellIdentityLte);
            sb.append(earfcn==null?"":String.valueOf(earfcn));
        }
        return sb.toString();
    }

    public static Integer getBandwidth(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            // Do something for Android 9 Pie and above versions
            int bandwidth = cellIdentity.getBandwidth();
            return filterUnavailable(bandwidth);
        } else {
            // do something for phones running an SDK before Android 9 Pie
            return null;
        }
    }

    public static String getBandwidthLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            Integer bandwidth = getBandwidth(cellIdentityLte);
            sb.append(bandwidth==null?"":String.valueOf(bandwidth));
        }
        return sb.toString();
    }

    public static Integer getCi(CellIdentityLte cellIdentity) {
        return cellIdentity == null?null:
                filterUnavailable(cellIdentity.getCi());
    }

    public static String getCiLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            Integer ci = getCi(cellIdentityLte);
            sb.append(ci==null?"":String.valueOf(ci));
        }
        return sb.toString();
    }

    public static String getMccString(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            // Do something for Android 9 Pie and above versions
            String ret = cellIdentity.getMccString();
            return ret;
        }
        // do something for phones running an SDK before Android 9 Pie
        return null;
    }

    public static String getMccStringLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            String mcc = getMccString(cellIdentityLte);
            sb.append(mcc==null?"":mcc);
        }
        return sb.toString();
    }

    public static String getMncString(CellIdentityLte cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            // Do something for Android 9 Pie and above versions
            String ret = cellIdentity.getMncString();
            return ret;
        }
        // do something for phones running an SDK before Android 9 Pie
        return null;
    }

    public static String getMncStringLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            String mnc = getMncString(cellIdentityLte);
            sb.append(mnc==null?"":mnc);
        }
        return sb.toString();
    }

    public static Integer getTac(CellIdentityLte cellIdentityLte) {
        return cellIdentityLte==null?null: filterUnavailable(cellIdentityLte.getTac());
    }

    public static String getTacLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            Integer tac = getTac(cellIdentityLte);
            sb.append(tac==null?"":tac);
        }
        return sb.toString();
    }

    public static Integer getPci(CellIdentityLte cellIdentityLte) {
        return cellIdentityLte==null?null: filterUnavailable(cellIdentityLte.getPci());
    }

    public static String getPciLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            Integer pci = getPci(cellIdentityLte);
            sb.append(pci==null?"":pci);
        }
        return sb.toString();
    }

    public static Integer filterUnavailable(Integer input) {
        if ( input == CellInfo.UNAVAILABLE ) return null;
        return input;
    }

    public static Long filterUnavailable(Long input) {
        if ( input == CellInfo.UNAVAILABLE_LONG) return null;
        return input;
    }

    public static Integer getCqi(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int ret = cellSignalStrength.getCqi();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getCqiLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthLte cellSignalStrength = cellInfoLte.getCellSignalStrength();
            Integer cqi = getCqi(cellSignalStrength);
            sb.append(cqi==null?"":cqi);
        }
        return sb.toString();
    }

    public static Integer getRsrp(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int ret = cellSignalStrength.getRsrp();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getRsrpLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthLte cellSignalStrength = cellInfoLte.getCellSignalStrength();
            Integer rsrp = getRsrp(cellSignalStrength);
            sb.append(rsrp==null?"":rsrp);
        }
        return sb.toString();
    }

    public static Integer getRsrq(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int ret = cellSignalStrength.getRsrq();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getRsrqLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthLte cellSignalStrength = cellInfoLte.getCellSignalStrength();
            Integer rsrq = getRsrq(cellSignalStrength);
            sb.append(rsrq==null?"":rsrq);
        }
        return sb.toString();
    }

    public static Integer getRssi(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellSignalStrength.getRssi();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getRssiLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthLte cellSignalStrength = cellInfoLte.getCellSignalStrength();
            Integer rssi = getRssi(cellSignalStrength);
            sb.append(rssi==null?"":rssi);
        }
        return sb.toString();
    }

    public static Integer getRssnr(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int ret = cellSignalStrength.getRssnr();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getRssnrLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthLte cellSignalStrength = cellInfoLte.getCellSignalStrength();
            Integer rssnr = getRssnr(cellSignalStrength);
            sb.append(rssnr==null?"":rssnr);
        }
        return sb.toString();
    }

    public static Integer getTimingAdvance(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        int ret = cellSignalStrength.getTimingAdvance();
        return filterUnavailable(ret);
    }

    public static String getTimingAdvanceLte(List<CellInfoLte> cellInfoLteList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoLteList == null || cellInfoLteList.size() == 0 ) {
            return "";
        }
        for ( CellInfoLte cellInfoLte: cellInfoLteList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthLte cellSignalStrength = cellInfoLte.getCellSignalStrength();
            Integer ta = getTimingAdvance(cellSignalStrength);
            sb.append(ta==null?"":ta);
        }
        return sb.toString();
    }

    public static void safeAddToMap(WritableMap dest, String key, String nullableValue) {
        if ( nullableValue == null ) return;
        dest.putString(key, nullableValue);
    }

    public static void safeAddToMap(WritableMap dest, String key, Integer nullableValue) {
        if ( nullableValue == null ) return;
        dest.putInt(key, nullableValue);
    }

    public static void safeAddToMap(WritableMap dest, String key, Long nullableValue) {
        if ( nullableValue == null ) return;
        dest.putString(key, String.valueOf(nullableValue));
    }

    public static void safeAddToMap(WritableMap dest, String key, ReadableMap nullableValue) {
        if ( nullableValue == null ) return;
        dest.putMap(key, nullableValue);
    }

    private static String getMccString(CellIdentityNr cellIdentityNr) {
        return cellIdentityNr == null? null:
                (Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getMccString());
    }

    public static String getMccStringNr(List<CellInfoNr> cellIdentityNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellIdentityNrList == null || cellIdentityNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellIdentityNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityNr cellIdentityNr = (CellIdentityNr)cellInfoNr.getCellIdentity();
            String mcc = getMccString(cellIdentityNr);
            sb.append(mcc==null?"":mcc);
        }
        return sb.toString();
    }

    private static String getMncString(CellIdentityNr cellIdentityNr) {
        return cellIdentityNr == null? null:
                (Build.VERSION.SDK_INT < 29? null: cellIdentityNr.getMncString());
    }

    public static String getMncStringNr(List<CellInfoNr> cellIdentityNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellIdentityNrList == null || cellIdentityNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellIdentityNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityNr cellIdentityNr = (CellIdentityNr)cellInfoNr.getCellIdentity();
            String mnc = getMncString(cellIdentityNr);
            sb.append(mnc==null?"":mnc);
        }
        return sb.toString();
    }

    public static Long getNci(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            long ret = cellIdentity.getNci();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getNciNr(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityNr cellIdentityNr = (CellIdentityNr)cellInfoNr.getCellIdentity();
            Long nci = getNci(cellIdentityNr);
            sb.append(nci==null?"":nci);
        }
        return sb.toString();
    }

    public static Integer getNrarfcn(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellIdentity.getNrarfcn();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getNrarfcnNr(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityNr cellIdentityNr = (CellIdentityNr)cellInfoNr.getCellIdentity();
            Integer nrarfcn = getNrarfcn(cellIdentityNr);
            sb.append(nrarfcn==null?"":nrarfcn);
        }
        return sb.toString();
    }

    public static Integer getPci(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellIdentity.getPci();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getPciNr(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityNr cellIdentityNr = (CellIdentityNr)cellInfoNr.getCellIdentity();
            Integer pci = getPci(cellIdentityNr);
            sb.append(pci==null?"":pci);
        }
        return sb.toString();
    }

    public static Integer getTac(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellIdentity.getTac();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static String getTacNr(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellIdentityNr cellIdentityNr = (CellIdentityNr)cellInfoNr.getCellIdentity();
            Integer tac = getPci(cellIdentityNr);
            sb.append(tac==null?"":tac);
        }
        return sb.toString();
    }

    public static Integer getCsiRsrp(CellSignalStrengthNr cellSignalStrengthNr) {
        return cellSignalStrengthNr == null? null:
                (Build.VERSION.SDK_INT < 29? null: CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getCsiRsrp()));
    }

    public static String getCsiRsrp(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr)cellInfoNr.getCellSignalStrength();
            Integer value = getCsiRsrp(cellSignalStrengthNr);
            sb.append(value==null?"":value);
        }
        return sb.toString();
    }

    public static Integer getCsiRsrq(CellSignalStrengthNr cellSignalStrengthNr) {
        return cellSignalStrengthNr == null? null:
                (Build.VERSION.SDK_INT < 29?null: CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getCsiRsrq()));
    }

    public static String getCsiRsrq(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr)cellInfoNr.getCellSignalStrength();
            Integer value = getCsiRsrq(cellSignalStrengthNr);
            sb.append(value==null?"":value);
        }
        return sb.toString();
    }

    public static Integer getCsiSinr(CellSignalStrengthNr cellSignalStrengthNr) {
        return cellSignalStrengthNr == null? null:
                (Build.VERSION.SDK_INT < 29?null: CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getCsiSinr()));
    }

    public static String getCsiSinr(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr)cellInfoNr.getCellSignalStrength();
            Integer value = getCsiSinr(cellSignalStrengthNr);
            sb.append(value==null?"":value);
        }
        return sb.toString();
    }

    public static Integer getSsRsrp(CellSignalStrengthNr cellSignalStrengthNr) {
        return cellSignalStrengthNr == null? null:
                (Build.VERSION.SDK_INT < 29?null: CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getSsRsrp()));
    }

    public static String getSsRsrp(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr)cellInfoNr.getCellSignalStrength();
            Integer value = getSsRsrp(cellSignalStrengthNr);
            sb.append(value==null?"":value);
        }
        return sb.toString();
    }

    public static Integer getSsRsrq(CellSignalStrengthNr cellSignalStrengthNr) {
        return cellSignalStrengthNr == null? null:
                (Build.VERSION.SDK_INT < 29?null: CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getSsRsrq()));
    }

    public static String getSsRsrq(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr)cellInfoNr.getCellSignalStrength();
            Integer value = getSsRsrq(cellSignalStrengthNr);
            sb.append(value==null?"":value);
        }
        return sb.toString();
    }

    public static Integer getSsSinr(CellSignalStrengthNr cellSignalStrengthNr) {
        return cellSignalStrengthNr == null? null:
                (Build.VERSION.SDK_INT < 29?null: CellInfoUtils.filterUnavailable(cellSignalStrengthNr.getSsSinr()));
    }

    public static String getSsSinr(List<CellInfoNr> cellInfoNrList) {
        StringBuilder sb = new StringBuilder();
        if ( cellInfoNrList == null || cellInfoNrList.size() == 0
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "";
        }
        for ( CellInfoNr cellInfoNr: cellInfoNrList) {
            if ( sb.length() > 0 ) sb.append(';');
            CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr)cellInfoNr.getCellSignalStrength();
            Integer value = getSsSinr(cellSignalStrengthNr);
            sb.append(value==null?"":value);
        }
        return sb.toString();
    }

}

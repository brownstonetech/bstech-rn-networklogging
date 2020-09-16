package ca.bstech.networklogging.networkinfo;

import android.annotation.TargetApi;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfo;
import android.telephony.CellSignalStrengthLte;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

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

    public static Integer getRsrp(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int ret = cellSignalStrength.getRsrp();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static Integer getRsrq(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int ret = cellSignalStrength.getRsrq();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static Integer getRssi(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellSignalStrength.getRssi();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static Integer getRssnr(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int ret = cellSignalStrength.getRssnr();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static Integer getTimingAdvance(CellSignalStrengthLte cellSignalStrength) {
        if (cellSignalStrength == null) return null;
        int ret = cellSignalStrength.getTimingAdvance();
        return filterUnavailable(ret);
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

    public static Long getNci(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            long ret = cellIdentity.getNci();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static Integer getNrarfcn(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellIdentity.getNrarfcn();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static Integer getPci(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellIdentity.getPci();
            return filterUnavailable(ret);
        }
        return null;
    }

    public static Integer getTac(CellIdentityNr cellIdentity) {
        if (cellIdentity == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int ret = cellIdentity.getTac();
            return filterUnavailable(ret);
        }
        return null;
    }
}

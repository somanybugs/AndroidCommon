package lhg.common.utils;

import android.text.TextUtils;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormater {

    static DateFormater instance;
    SimpleDateFormat showyyyyMMddHHmm = new SimpleDateFormat("yyyyMMdd HH:mm");
    SimpleDateFormat showyyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    public static DateFormater getInstance() {
        if (instance == null) {
            synchronized (DateFormater.class) {
                if (instance == null) {
                    instance = new DateFormater();
                }
            }
        }
        return instance;
    }

    public static String yyyyMMddHHmmss(Date date) {
       return getInstance().yyyyMMddHHmmss.format(date);
    }


    public static String showyyyyMMddHHmm(Date date) {
        return getInstance().showyyyyMMddHHmm.format(date);
    }

    public static String showYyyyMMdd(String date) {
        if (TextUtils.isEmpty(date) || date.length() != 8) {
            return "";
        }
        StringBuilder sb = new StringBuilder(date);
        sb.insert(6, "/");
        sb.insert(4, "/");
        return sb.toString();
    }

    public static String showYyyyMMddHHmmss2(String date) {
        if (TextUtils.isEmpty(date) || date.length() != 14) {
            return "";
        }
        StringBuilder sb = new StringBuilder(date);
        sb.insert(12, ":");
        sb.insert(10, ":");
        sb.insert(8, " ");
        sb.insert(6, "-");
        sb.insert(4, "-");
        return sb.toString();
    }
    public static String showYyyyMMddHHmmss(String date) {
        if (TextUtils.isEmpty(date) || date.length() != 14) {
            return "";
        }
        StringBuilder sb = new StringBuilder(date);
        sb.insert(12, ":");
        sb.insert(10, ":");
        sb.insert(8, " ");
        sb.insert(6, "/");
        sb.insert(4, "/");
        return sb.toString();
    }

    public static String showYyyyMMddHHmmss(Date date) {
        return getInstance().showyyyyMMddHHmmss.format(date);
    }

    public static String yyyyMMdd(Date date) {
        return getInstance().yyyyMMdd.format(date);
    }
    public static Date yyyyMMddParse(String date) {
        try {
            return getInstance().yyyyMMdd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String nextDay(String yyyyMMdd) {
        try {
            Date d = getInstance().yyyyMMdd.parse(yyyyMMdd);
            d.setTime(d.getTime() + DateUtils.DAY_IN_MILLIS);
            return getInstance().yyyyMMdd.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

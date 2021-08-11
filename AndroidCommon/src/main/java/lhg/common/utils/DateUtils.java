package lhg.common.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static boolean isSameDay(long millis1, long millis2, TimeZone timeZone) {
        long interval = millis1 - millis2;
        return interval < 86400000 && interval > -86400000 && millis2Days(millis1, timeZone) == millis2Days(millis2, timeZone);
    }

    private static long millis2Days(long millis, TimeZone timeZone) {
        return (((long) timeZone.getOffset(millis)) + millis) / 86400000;
    }

    public static boolean isNotToday(long timeInMills) {
        return !isToday(timeInMills);
    }

    public static boolean isToday(long timeInMills) {
        return isToday(Calendar.getInstance(), timeInMills);
    }

    public static boolean isToday(Calendar calendar, long timeInMills) {
        calendar.setTimeInMillis(timeInMills);
        int day = calendar.get(Calendar.YEAR)*1000 + calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.YEAR)*1000 + calendar.get(Calendar.DAY_OF_YEAR);
        return day == today;
    }

    ///今天凌晨0点00以来的毫秒数, //一天有 86400000 毫秒, 总计最大8位数字
    public static long millsOfToday() {
        long now = System.currentTimeMillis();
        long mills = (now - DateUtils.dayBegin(now).getTime());
        return mills;
    }

    public static Date dayBegin(long timeInMills) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills);
        setDay0000(cal);
        return cal.getTime();
    }

    public static Date dayEnd(long timeInMills) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills);
        setDay2359(cal);
        return cal.getTime();
    }

    public static Date weekBegin(long timeInMills){
        return weekBegin2(timeInMills).getTime();
    }

    public static Calendar weekBegin2(long timeInMills){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(Calendar.SUNDAY == dayWeek) {
            //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        setDay0000(cal);
        return cal;
    }
    /**
     * 得到本周周五日期
     * @return
     */
    public static Date weekEnd(long timeInMills){
        Calendar cal = weekBegin2(timeInMills);
        cal.add(Calendar.DATE, 6);
        setDay2359(cal);
        return cal.getTime();
    }


    public static Date monthBegin(long timeInMills) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        setDay0000(cal);
        return cal.getTime();
    }

    public static Date monthEnd(long timeInMills) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills);
        //获取某月最小天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        setDay2359(cal);
        return cal.getTime();
    }

    //设置为当天的0点0分0秒
    public static void setDay0000(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
    //设置为当天的23点59分59秒
    public static void setDay2359(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }
}

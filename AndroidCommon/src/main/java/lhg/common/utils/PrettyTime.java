package lhg.common.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;


import lhg.common.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrettyTime {

	static SimpleDateFormat	sdf				= null;
	static final String		LONG_TEMPLATE	= "%Y-%m-%d";
	static final String		SHORT_TEMPLATE	= "%m-%d %H:%M";

	public static String format(Date thenDate, Date nowDate, Context context) {
		long thenMills = thenDate.getTime();
		long nowMills = nowDate.getTime();

		if (thenMills - nowMills > 0) {
			if (thenMills - nowMills < 5 * DateUtils.MINUTE_IN_MILLIS) {
				//认为是手机时间的误差5分钟
				return context.getString(R.string.agg_justnow);
			} else {
				//未来的时间
				Time time = new Time();
				time.set(thenDate.getTime());
				return time.format(LONG_TEMPLATE);
			}
		}

		if (nowMills - thenMills < DateUtils.MINUTE_IN_MILLIS) {
			//60秒以内 刚刚
			return context.getString(R.string.agg_justnow);
		}

		if (nowMills - thenMills < DateUtils.HOUR_IN_MILLIS) {
			//60分钟以内
			return context.getString(R.string.agg_minutes_ago_fmt, (nowMills - thenMills) / DateUtils.MINUTE_IN_MILLIS);
		}

		Time time = new Time();
		time.set(nowMills);
		int nowYear = time.year;
		int nowDay = Time.getJulianDay(nowMills, time.gmtoff);

		time.set(thenDate.getTime());
		int thenYear = time.year;
		int thenDay = Time.getJulianDay(thenMills, time.gmtoff);

		if (thenDay == nowDay) {
			return time.format(context.getString(R.string.agg_today_date_fmt));
		}

		if (thenDay + 1 == nowDay) {
			return time.format(context.getString(R.string.agg_yesterday_date_fmt));
		}

		if (thenYear != nowYear) {
			return time.format(LONG_TEMPLATE);
		} else {
			return time.format(SHORT_TEMPLATE);
		}
	}

	public static String format(Date thenDate, Context context) {
		return format(thenDate, new Date(), context);
	}
}

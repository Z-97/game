package com.alex.game.common.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/3/20.
 */
public class TimeUtil {

	public static String getResultTimeTo(String timeTo) {
		if (timeTo.equals("")) {
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			return year + "-" + (month + 1) + "-" + day;
		} else {
			return timeTo;
		}
	}

	public static int getCurrentSeconds() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	public static int getSecondsOfTimeStamp(String timeStamp) {
		if (timeStamp.equals("")) {
			return 0;
		}
		String[] strs = timeStamp.split("-");
		if (strs.length < 3) {
			return 0;
		}
		int year = Integer.parseInt(strs[0]);
		int month = Integer.parseInt(strs[1]);
		int day = Integer.parseInt(strs[2]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return (int) (cal.getTimeInMillis() / 1000);
	}

	public static String getCurrentTimeFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		return format.format(new Date());
	}

	public static List<String> spiltDayList(String timeFrom, String timeTo) {
		String[] strs0 = timeFrom.split("-");
		String[] strs1 = timeTo.split("-");
		int fromYear = Integer.parseInt(strs0[0]);
		int fromMonth = Integer.parseInt(strs0[1]);
		int fromDay = Integer.parseInt(strs0[2]);
		int toYear = Integer.parseInt(strs1[0]);
		int toMonth = Integer.parseInt(strs1[1]);
		int toDay = Integer.parseInt(strs1[2]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, fromYear);
		cal.set(Calendar.MONTH, fromMonth - 1);
		cal.set(Calendar.DAY_OF_MONTH, fromDay);
		long mill = cal.getTimeInMillis();
		List<String> resultList = new ArrayList<>();
		cal.set(Calendar.YEAR, toYear);
		cal.set(Calendar.MONTH, toMonth - 1);
		cal.set(Calendar.DAY_OF_MONTH, toDay);
		long toMill = cal.getTimeInMillis();
		while (mill <= toMill) {
			SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
			resultList.add(sft.format(new Date(mill)));
			mill += 24 * 60 * 60 * 1000;
		}
		return resultList;
	}

	public static int getSecondsOfTimeStamp_ex(String timeStamp, String timeFromat) {
		if (timeStamp.equals("")) {
			timeStamp = getCurrentTimeFormat();
		}
		SimpleDateFormat sdf = new SimpleDateFormat(timeFromat);
		Date date = null;
		try {
			date = sdf.parse(timeStamp);
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
		if (null == date) {
			return -1;
		}
		Calendar calendar = Calendar.getInstance();
		if (null == calendar) {
			return -1;
		}
		calendar.setTime(date);
		return (int) (calendar.getTimeInMillis() / 1000);
	}

	/**
	 * 获取当前时间往上的整点时间
	 *
	 * @return
	 */
	public static int getIntegralTimeBegin(int updateTime) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (int) (cal.getTimeInMillis() / 1000 + updateTime);
	}

	public static int getIntegralTimeEnd(int updateTime) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return (int) (cal.getTimeInMillis() / 1000 + updateTime);
	}

	public static String getStringTime(int ms, String format) {
		String str = "";
		if (ms > 0) {
			long msl = (long) ms * 1000;
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				str = sdf.format(msl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}

}

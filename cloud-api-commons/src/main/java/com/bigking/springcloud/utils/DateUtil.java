
package com.bigking.springcloud.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DateUtil {
	public final static long SECOND_MILLIS = 1000;
	public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
	public final static long HOURS_MILLIS = MINUTE_MILLIS * 60;
	public final static long DAY_MILLIS = HOURS_MILLIS * 24;
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
	private static final long START_TICK = 0;

	public static final DateFormat formater = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

	private final static ThreadSafeSimpleDateFormat yyyyMMddHHmmss = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getString(Date date, String format) {
		String ret = null;
		if (date == null) {
			return null;
		}
		if (StringUtils.isBlank(format)) {
			format = "MM/dd/yyyy HH:mm:ss";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		ret = df.format(date);
		return ret;
	}

	public static Date getDate(String dateStr, String format) {
		Date ret = null;
		if (StringUtils.isBlank(dateStr)) {
			return null;
		}
		if (StringUtils.isBlank(format)) {
			format = "MM/dd/yyyy HH:mm:ss";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			ret = df.parse(dateStr);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			return null;
		}
		return ret;
	}

	public static long getTickCount() {
		return (System.currentTimeMillis() - START_TICK);
	}

	// Make Calendar return mm/dd/yyyy
	public static String formatCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		String tmpDate = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(cal.DATE) + "/" + cal.get(cal.YEAR);
		return tmpDate;
	}

	// Make Calendar return mm/dd/yyyy
	public static String formatCalendar(Calendar cal) {
		String tmpDate = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(cal.DATE) + "/" + cal.get(cal.YEAR);
		return tmpDate;
	}

	// return the last weekend in mm/yyyy
	public static ArrayList getWeeklyEndDate(int nYear, int nMonth) {
		Calendar WDate = Calendar.getInstance();
		WDate.set(nYear, nMonth - 1, 1);

		int nextMM = nMonth;
		int nextYY = -1;
		if (nextMM == 12) {
			nextYY = nYear + 1;
			nextMM = 0;
		} else
			nextYY = nYear;

		Calendar nextDate = Calendar.getInstance();
		nextDate.set(nextYY, nextMM, 1);

		int tmpDay = WDate.get(Calendar.DAY_OF_WEEK);
		while (tmpDay != Calendar.FRIDAY) {
			WDate.add(Calendar.DATE, 1);
			tmpDay = WDate.get(Calendar.DAY_OF_WEEK);
		}

		ArrayList arrList = new ArrayList();
		int i = 0;
		while (WDate.getTime().before(nextDate.getTime())) {
			String strDate = formatCalendar(WDate);
			arrList.add(strDate);
			WDate.add(Calendar.DATE, 7);
			i++;
		}
		return arrList;
	}

	// the date string format is mm/dd/yyyy
	public static String getWeekBeginDate(String WeekEnd) {
		int nPos = WeekEnd.indexOf("/");
		int nPos1 = WeekEnd.lastIndexOf("/");

		int nYY = Integer.parseInt(WeekEnd.substring(nPos1 + 1));
		int nMM = Integer.parseInt(WeekEnd.substring(0, nPos));
		int nDD = Integer.parseInt(WeekEnd.substring(nPos + 1, nPos1));

		Calendar WDate = Calendar.getInstance();
		WDate.set(nYY, nMM - 1, nDD);

		WDate.add(Calendar.DATE, -7);

		return formatCalendar(WDate);
	}

	public static Date getEndTimeOfOneDay(Date dtDate) {
		Calendar end = Calendar.getInstance();
		end.setTime(dtDate);

		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);

		return end.getTime();
	}

	public static String[] getLatestWeek() {
		String[] LatestWeek = new String[2];
		Calendar LatestWeekEnd = Calendar.getInstance();

		LatestWeekEnd.add(Calendar.DATE, -1);

		while (LatestWeekEnd.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
			LatestWeekEnd.add(Calendar.DATE, -1);
		}

		Calendar LatestWeekStart = Calendar.getInstance();
		LatestWeekStart.setTime(LatestWeekEnd.getTime());
		LatestWeekStart.add(Calendar.DATE, -6);

		LatestWeek[0] = formatCalendar(LatestWeekStart);
		LatestWeek[1] = formatCalendar(LatestWeekEnd);

		return LatestWeek;
	}

	public static String[] getLastWeek() {
		String[] Week = new String[2];
		Calendar tmpDay = Calendar.getInstance();

		if (tmpDay.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY)
			tmpDay.add(Calendar.DATE, -7);

		while (tmpDay.get(Calendar.DAY_OF_WEEK) < Calendar.FRIDAY) {
			tmpDay.add(Calendar.DATE, 1);
		}
		while (tmpDay.get(Calendar.DAY_OF_WEEK) > Calendar.FRIDAY) {
			tmpDay.add(Calendar.DATE, -1);
		}

		Week[1] = formatCalendar(tmpDay);
		tmpDay.add(Calendar.DATE, -6);
		Week[0] = formatCalendar(tmpDay);
		return Week;
	}

	public static String[] getLast3Month() {
		String[] hehe = new String[2];
		Calendar tmpDate = Calendar.getInstance();
		Calendar newDate = Calendar.getInstance();
		newDate.set(tmpDate.get(Calendar.YEAR), tmpDate.get(Calendar.MONTH), 1);

		newDate.add(Calendar.DATE, -1);
		int tmpMonth = newDate.get(Calendar.MONTH) + 1;
		int tmpYear = newDate.get(Calendar.YEAR);

		for (int i = 1; i < 3; i++) {
			if (tmpMonth == 0) {
				tmpMonth = 12;
				tmpYear--;
			}
			tmpMonth--;
		}

		hehe[1] = formatCalendar(newDate);
		hehe[0] = tmpMonth + "/1/" + tmpYear;
		return hehe;
	}

	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy hh:mm aa", Locale.getDefault());
		return formatter.format(date);
	}

	// mm/dd/yyyy hh24:mi:ss
	public static String formatDateSql(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return formatter.format(date);
	}

	/**
	 * "MM/dd/yyyy HH:mm:ss"->Date
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date parseDateSql(String dateStr) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public static String convertTimeZoneDate(Date InitialDate, int nTimeZoneOffset) {
		Calendar tmpCal = Calendar.getInstance();
		tmpCal.setTime(InitialDate);
		tmpCal.add(Calendar.HOUR, nTimeZoneOffset);

		return formatDate(tmpCal.getTime());
	}

	/*
	 * convert timezone and date ====> Begin
	 */
	public static Date convertTimeZoneDate(Date date, TimeZone oldTZ, TimeZone newTZ) {
		int nOldOffset = oldTZ.getRawOffset() / 60000;
		int nNewOffset = newTZ.getRawOffset() / 60000;
		int nDifference = nNewOffset - nOldOffset;
		Calendar cal = Calendar.getInstance(oldTZ);
		cal.setTime(date);
		cal.add(Calendar.MINUTE, nDifference);

		return cal.getTime();
	}

	/*
	 * Convert Your current date into PST date NOT GMT
	 *
	 * date --- your current date tz --- your current timezone
	 */
	static public Date convertCurToPST(Date date, TimeZone tz) {
		TimeZone tzPST = TimeZone.getTimeZone("PST");
		return convertTimeZoneDate(date, tz, tzPST);
	}

	// convert current date into PST date
	static public Date convertPSTToCur(Date date, TimeZone tz) {
		TimeZone tzPST = TimeZone.getTimeZone("PST");
		return convertTimeZoneDate(date, tzPST, tz);
	}

	public static String timeZoneServer2Owner(int iOwnerTimeZone, Date InitialDate) {
		Calendar datServer = Calendar.getInstance();
		int iServerTimeZone = datServer.getTimeZone().getRawOffset() / 3600000; // get
		// hours
		System.out.println(iServerTimeZone);
		int iDifTimeZone = iOwnerTimeZone - iServerTimeZone;
		System.out.println(iDifTimeZone);
		return convertTimeZoneDate(InitialDate, iDifTimeZone);
	}

	public static String getServerDate() {
		Calendar Today = Calendar.getInstance();
		String tmpDate = formatCalendar(Today);
		return tmpDate;
	}

	public static Calendar getCurrentServerTime() {
		Calendar Today = Calendar.getInstance();
		return Today;
	}

	public static String getMonthName(int Month) {
		String[] x = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		return x[Month - 1];
	}

	public static String getDayName(int tmpDay) {
		switch (tmpDay) {
		case 0:
			return "Sun";
		case 1:
			return "Mon";
		case 2:
			return "Tue";
		case 3:
			return "Wed";
		case 4:
			return "Thu";
		case 5:
			return "Fri";
		case 6:
			return "Sat";
		}
		return "";
	}

	// String format is mm/dd/yyyy
	public static int getIntDate(String dateStr) throws ParseException {
		int nPos = dateStr.indexOf("/");
		int nPos1 = dateStr.lastIndexOf("/");

		int thisDay = Integer.parseInt(dateStr.substring(nPos + 1, nPos1));
		return thisDay;
	}

	public static String getDayofMonth(Calendar cal) {
		int nDate = cal.get(Calendar.DAY_OF_MONTH);
		int nMonth = cal.get(Calendar.MONTH) + 1;
		String strMonth = DateUtil.getMonthName(nMonth);

		return nDate + "-" + strMonth;
	}

	public static Vector getLastTwoMonthBeginWeek() {
		Vector vt = new Vector();

		Calendar StartWeekDate = Calendar.getInstance();
		StartWeekDate.add(Calendar.MONTH, -2);
		StartWeekDate.set(Calendar.DAY_OF_MONTH, 1);
		StartWeekDate.set(Calendar.HOUR_OF_DAY, 00);
		StartWeekDate.set(Calendar.MINUTE, 00);
		StartWeekDate.set(Calendar.SECOND, 00);

		Calendar EndWeekDate = Calendar.getInstance();
		EndWeekDate.add(Calendar.MONTH, -2);
		EndWeekDate.set(Calendar.DAY_OF_MONTH, 1);
		while (EndWeekDate.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
			EndWeekDate.add(Calendar.DATE, 1);
		}
		EndWeekDate.set(Calendar.HOUR_OF_DAY, 23);
		EndWeekDate.set(Calendar.MINUTE, 59);
		EndWeekDate.set(Calendar.SECOND, 59);

		vt.add(StartWeekDate);
		vt.add(EndWeekDate);

		return vt;
	}

	public static int getOverDays(Date startDate) {
		Date sysDate = DateUtil.getCurrentServerTime().getTime();

		long interval = sysDate.getTime() - startDate.getTime();

		return (int) (interval / (1000 * 60 * 60 * 24));
	}

	public final static DateFormat MMddHHMMSS = new SimpleDateFormat("MM-dd HH:mm:ss");

	public static String getCurrentMMddHHMMSS() {
		return MMddHHMMSS.format(new Date());
	}

	public final static DateFormat MMddHHMM = new SimpleDateFormat("MM-dd HH:mm");

	public static String ms2str(long ms) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ms);
		Date date = cal.getTime();
		return formater.format(date);
	}

	public static String getCurrentMMddHHMM() {
		return MMddHHMM.format(new Date());
	}

	public static boolean between(Date date, Date dateStart, Date dateEnd) {
		if (date.after(dateStart) && date.before(dateEnd)) {
			return true;
		} else {
			return false;
		}
	}

	public static Date addSecond(Date date, int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, second);
		return c.getTime();

	}

	/**
	 * Get days between two date<br>
	 * Less than one day counts as one day
	 *
	 * @param smDate smaller date
	 * @param bgDate biger date
	 * @return java.lang.Long
	 */
	public static Long daysBetween2Date(Date smDate, Date bgDate) {
		long betweenDays = 0L;
		Calendar cal = Calendar.getInstance();
		cal.setTime(smDate);
		long smTime = cal.getTimeInMillis();

		cal.setTime(bgDate);
		long bgTime = cal.getTimeInMillis();

		long betweenMillis = (bgTime - smTime);

		if (betweenMillis < 0) {
			betweenDays = 0;
		} else if (betweenMillis / DAY_MILLIS < 1 && betweenMillis % DAY_MILLIS > 0) {
			betweenDays = 1;
		} else {
			betweenDays = betweenMillis / DAY_MILLIS;
			if (betweenMillis % DAY_MILLIS > 0) {
				betweenDays += 1;
			}
		}
		return betweenDays;
	}

	/**
	 * Get the minutes difference
	 */
	public static int minutesDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null)
			return 0;

		return (int) ((laterDate.getTime() / MINUTE_MILLIS) - (earlierDate.getTime() / MINUTE_MILLIS));
	}

	/**
	 * Get the hours difference
	 */
	public static int hoursDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null)
			return 0;

		return (int) ((laterDate.getTime() / HOURS_MILLIS) - (earlierDate.getTime() / HOURS_MILLIS));
	}

	public static String getDuration(long le) {
		StringBuffer duration = new StringBuffer(500);

		long hour = le / (60 * 60 * 1000);
		long min = (le / (60 * 1000)) - hour * 60;
		long sec = (le / 1000 - hour * 60 * 60 - min * 60);

		if (hour == 0) {
			if (min == 0) {
				if (sec == 0) {
					duration.append("00:00:00");
				} else {
					duration.append("00:00:" + (sec < 10 ? "0" + sec : sec));
				}
			} else {
				if (sec == 0) {
					duration.append("00:" + (min < 10 ? "0" + min : min) + ":00");
				} else {
					duration.append("00:" + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec));
				}
			}
		} else {
			if (min == 0) {
				if (sec == 0) {
					duration.append((hour < 10 ? "0" + hour : hour) + ":00:00");
				} else {
					duration.append((hour < 10 ? "0" + hour : hour) + ":00:" + (sec < 10 ? "0" + sec : sec));
				}
			} else {
				if (sec == 0) {
					duration.append((hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min) + ":00");
				} else {
					duration.append((hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min) + ":"
							+ (sec < 10 ? "0" + sec : sec));
				}
			}
		}

		return duration.toString();
	}

	public static Date cvtToGmt(Date date) {
		TimeZone tz = TimeZone.getDefault();
		Date ret = new Date(date.getTime() - tz.getRawOffset());

		// if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
		if (tz.inDaylightTime(ret)) {
			Date dstDate = new Date(ret.getTime() - tz.getDSTSavings());

			// check to make sure we have not crossed back into standard time
			// this happens when we are on the cusp of DST (7pm the day before the change for PDT)
			if (tz.inDaylightTime(dstDate)) {
				ret = dstDate;
			}
		}
		return ret;
	}

	public static List<String> getAllDays(Date startDate, Date endDate, SimpleDateFormat sfymd) {
		if (sfymd == null) {
			sfymd = new SimpleDateFormat("yyyy.MM.dd");
		}
		if (null == startDate || null == endDate) {
			return new ArrayList<>();
		}

		List<String> days = new ArrayList<>();

		days.add(sfymd.format(startDate.getTime()));
		if (endDate.getTime() >= startDate.getTime()) {
			int ds = differentDays(startDate, endDate);
			for (int i = 0; i < ds; i++) {
				Date tmpDate = org.apache.commons.lang3.time.DateUtils.addDays(startDate, i + 1);
				days.add(sfymd.format(tmpDate));
			}
		}
		return days;
	}

	public static int differentDays(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) {
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
					timeDistance += 366;
				} else {
					timeDistance += 365;
				}
			}

			return timeDistance + (day2 - day1);
		} else {
			return day2 - day1;
		}
	}

	public static Date getNextDays(Date date, int nDates) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, nDates);
		return cal.getTime();
	}

	public static Date getCurrentGMTDate() {
		Calendar time = Calendar.getInstance();
		time.add(Calendar.MILLISECOND, -time.getTimeZone().getOffset(time.getTimeInMillis()));
		return time.getTime();
	}

	public static String get_yyyyMMdd_HHmmss(Date date) {
		if (date == null) {
			return "null";
		}
		return yyyyMMddHHmmss.format(date);
	}

	public static Date getDateByNumeric(long milliseconds) {
		return new Date(milliseconds);
	}

	public static String convertDigitalToTime(BigDecimal digital){
		if(digital == null) return null;
		String intValueStr = String.valueOf(digital.intValue());
		if (new BigDecimal(digital.intValue()).compareTo(digital)==0){
			if(intValueStr.length() > 1){
				return intValueStr + ":00:00";
			}
			return "0"+intValueStr+":00:00";
		}else {
			BigDecimal fractionalPart = digital.remainder(BigDecimal.ONE);
			BigDecimal bigDecimal = fractionalPart.multiply(new BigDecimal("60")).setScale(0, BigDecimal.ROUND_HALF_UP);
			if(intValueStr.length() > 1){
				return intValueStr+":"+bigDecimal+":00";
			}
			return  "0"+intValueStr+":"+bigDecimal+":00";
		}
	}

	public static void main(String[] agrs) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long betDays1 = daysBetween2Date(sdf.parse("2019-02-20 00:00:00"),
					DateUtil.getCurrentServerTime().getTime());
			long betDays2 = daysBetween2Date(sdf.parse("2019-02-19 13:50:00"),
					DateUtil.getCurrentServerTime().getTime());
			long betDays3 = daysBetween2Date(sdf.parse("2019-02-18 00:00:00"),
					DateUtil.getCurrentServerTime().getTime());
			logger.info(String.valueOf(betDays1));
			logger.info(String.valueOf(betDays2));
			logger.info(String.valueOf(betDays3));
		} catch (ParseException e1) {
			logger.error(e1.getLocalizedMessage());
		}
	}

}

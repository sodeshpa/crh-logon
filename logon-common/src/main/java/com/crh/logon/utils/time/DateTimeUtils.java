/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import ch.nevis.jcan.log.AdnTrace;


/**
 * @author AdNovum Informatik AG
 */
public class DateTimeUtils {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DateTimeUtils.class);


    /**
     * Returns an Integer containing huors and minutes of the date
     * parameter transformed in minutes after midnight
     * (this format is used for some fields in the DB)
     *
     * @param date
     * @return Integer
     */
    public static Long getMinAfterMidnight(Date date) {
		// [[
		Long result = null;
		Object[][] params = {{"date", date}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getMinAfterMidnight", params);
		}
		// ]]
		try {
			if (date == null) {
				return result = null;
			}
			Calendar c = Calendar.getInstance();
    		c.setTime(date);
    		return result = Long.valueOf(c.get(Calendar.MINUTE) + c.get(Calendar.HOUR_OF_DAY) * 60);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    /**
     * Inverse operation to getMinAfterMidnight.
     * @param minutesAfterMidnight
     * @return
     *
     * @adnlog.notrace
     *
     */
    public static Date getHoursMinutes(Long minutesAfterMidnight){
    	return getHoursMinutesCal(minutesAfterMidnight).getTime();
    }

    public static Calendar getHoursMinutesCal(Long minutesAfterMidnight){
		// [[
		Calendar result = null;
		Object[][] params = {{"minutesAfterMidnight", minutesAfterMidnight}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getHoursMinutesCal", params);
		}
		// ]]
		try {
			return result = getHoursMinutesCal(Calendar.getInstance(), minutesAfterMidnight);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]

		}
    }

	public static Calendar getHoursMinutesCal(Calendar base, Long minutesAfterMidnight){
		// [[
		Calendar result = null;
		Object[][] params = {{"base", base}, {"minutesAfterMidnight", minutesAfterMidnight}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getHoursMinutesCal", params);
		}
		// ]]
		try {
			if (minutesAfterMidnight == null){
				return result = null;
			}
			int hours = (int) (minutesAfterMidnight / 60);
			int minutes = (int) (minutesAfterMidnight % 60);
			if ((hours < 0) || (hours > 23)){
				// no need to check minutes, it's correct automatically if hours is
				return result = null;
			}

			Calendar c = (Calendar) base.clone();
			c.setTimeInMillis(0);
			c.set(Calendar.HOUR_OF_DAY, hours);
			c.set(Calendar.MINUTE, minutes);
			return result = c;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]

		}
	}

    /**
     * retruns a HH:mm string of the date
     * @param date
     * @return
     */
    public static String getHoursMinutesString(Date date){
		// [[
		String result = null;
		Object[][] params = {{"date", date}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getHoursMinutesString", params);
		}
		// ]]
		try {
			return result = DateTimeFormat.asHourMinute(date);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]

		}
    }

	public static Date parseTimestamp(String longString) {
		if (longString == null) {
			return null;
		}
		try {
			Long longValue = Long.valueOf(longString);
			Date date = new Date(longValue);
			return date;
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	/**
	 * Parses a HH:mm String
	 *
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date parseHoursMinutes(String date) throws ParseException{
		// [[
		Date result = null;
		Object[][] params = {{"date", date}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "parseHoursMinutes", params);
		}
		// ]]
		try {
			if ((date == null) || (date.length() == 0)){
				return result = null;
			}
			return result = DateTimeParser.parseHourMinute(date);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Parses a HH:mm String
	 *
	 * @param date
	 * @param day day to which the HH:mm String will be added (only year, month and day values are to be set here)
	 * @return
	 * @throws ParseException
	 */
	public static Date parseHoursMinutes(String date, Date day) throws ParseException{
		// [[
		Date result = null;
		Object[][] params = {{"date", date},
							{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "parseHoursMinutes", params);
		}
		// ]]
		try {
			Date hm = parseHoursMinutes(date);
			if (hm == null){
				return result = null;
			}
			Calendar hmc = Calendar.getInstance();
			hmc.setTime(hm);
			Calendar resultc = Calendar.getInstance();
			resultc.setTime(day);
			resultc.set(Calendar.HOUR_OF_DAY, hmc.get(Calendar.HOUR_OF_DAY));
			resultc.set(Calendar.MINUTE, hmc.get(Calendar.MINUTE));
			return result = resultc.getTime();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Inverse operation to parseHoursMinutes
	 *
	 * @param hoursMinutes
	 * @return
	 */
	public static String formatHoursMinutes(Date hoursMinutes) {
		// [[
		String result = null;
		Object[][] params = {{"hoursMinutes", hoursMinutes}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "formatHoursMinutes", params);
		}
		// ]]
		try {
			if (hoursMinutes == null) {
				return result=null;
			}
			return result = DateTimeFormat.asHourMinute(hoursMinutes);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}

	}

	/**
	 * Inverse operation to parseDayMonthYear
	 *
	 * @param
	 * @return
	 */
	public static String formatDayMonthYear(Date date, Locale locale) {
		// [[
		String result = null;
		Object[][] params = {{"date", date},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "formatDayMonthYear", params);
		}
		// ]]
		try {
			return result = DateTimeFormat.asLogonDayMonthYear(locale, date);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Parses a String according to DateTimeUtils.dateFormat pattern. Returns a Date.
	 *
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDayMonthYear(String date) throws ParseException{
		// [[
		Date result = null;
		Object[][] params = {{"date", date}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "parseDayMonthYear", params);
		}
		// ]]
		try {
			if ((date == null) || (date.length() == 0)){
				return result = null;
			}
			return result = DateTimeParser.parseLogonDayMonthYear(date);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Return date with GMT +00 timezone
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	public static Date parseDayMonthYearInGMTZone(String date) throws ParseException{
		return parseDayMonthYear(date, "GMT");
	}

	/**
	 * Return date by timezone
	 * @param date
	 * @param timeZone
	 * @return
	 * @throws ParseException 
	 */
	public static Date parseDayMonthYear(String date, String timeZone) throws ParseException{
		if ((date == null) || (date.length() == 0)){
			return null;
		}
		String timeZoneId = timeZone;
		if(org.apache.commons.lang.StringUtils.isEmpty(timeZone)) {
			timeZoneId = "GMT";
		}
		return DateTimeParser.parseLogonDayMonthYear(date, Locale.ENGLISH, timeZoneId);
	}
	
	/**
	 * Returns today date as String given by DateTimeUtils.dateFormat pattern.
	 *
	 * @return
	 */
	public static String getToday() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getToday");
		}
		// ]]
		try {
			Calendar cal = Calendar.getInstance();
			String today = DateTimeFormat.asLogonDayMonthYear(cal.getTime());
			return result = today;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static Date firstDayThisWeek(Calendar cal) {
		// [[
		Date result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "firstDayThisWeek");
		}
		// ]]
		try {
			int dow = cal.get(Calendar.DAY_OF_WEEK);
			cal.add(Calendar.DAY_OF_MONTH, -1 * (dow-Calendar.MONDAY) );

			return result = cal.getTime();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public static Date firstDayThisMonth(Calendar cal) {
		// [[
		Date result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "firstDayThisMonth");
		}
		// ]]
		try {
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = 1;
			cal.set(year, month, day);

			return result = cal.getTime();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public static Date firstDayThisYear(Calendar cal) {
		// [[
		Date result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "firstDayThisYear");
		}
		// ]]
		try {
			int year = cal.get(Calendar.YEAR);
			int month = Calendar.JANUARY;
			int day = 1;
			cal.set(year, month, day);

			return result = cal.getTime();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public static Date tomorrow(Date reference) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(reference);
		cal.add(Calendar.DAY_OF_MONTH, 1);

		return cal.getTime();
	}

	public static Date tomorrow(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	public static Calendar getNextWorkingDay(Calendar day) {
		// [[
		Calendar result = null;
		Object[][] params = {{"day", day}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getNextWorkingDay", params);
		}
		// ]]
		try {
	        Calendar c = (Calendar)day.clone();
	        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
	            c.add(Calendar.DAY_OF_MONTH, 3);
	        }
	        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
	            c.add(Calendar.DAY_OF_MONTH, 2);
	        }
	        else {
	            c.add(Calendar.DAY_OF_MONTH, 1);
	        }
	        return result = c;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	public static Date yesterday(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * Method to return the current date (also with time components) an
	 * XMLGregorianCalendar instance.
	 *
	 * @return Returns the current date.
	 * @throws DatatypeConfigurationException
	 */
	public static final XMLGregorianCalendar getTodayAsXmlGregorianCalendar()
			throws DatatypeConfigurationException {
		// [[
		XMLGregorianCalendar result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getTodayAsXmlGregorianCalendar");
		}
		// ]]
		try {
			DatatypeFactory typeFactory = DatatypeFactory.newInstance();

			return (result = (typeFactory
					.newXMLGregorianCalendar(new GregorianCalendar())));
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns the given date but with time 0:00:00
	 *
	 * Notice that this is also timezone-dependent!
	 * Here we are using the default timezone...
	 * 
	 * @param date date to transform
	 * @return the given date without the time of day components
	 */
	public static Date cleanTimeWithDefaultTimeZone(Date date) {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTime(date);
		return getMorningOfDay(c).getTime();
	}
	
	/**
	 * Returns the given date but with time 00:00:00 in GMT timezone
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMorningOfDayInGMTZone(Date date) {
		return getMorningOfDay(date, "GMT");
	}

	/**
	 * Returns the given date by timezone
	 * 
	 * @param date
	 * @param timeZone
	 * @return
	 */
	public static Date getMorningOfDay(Date date, String timeZone) {
		String timeZoneId = timeZone;
		if(org.apache.commons.lang.StringUtils.isEmpty(timeZone)) {
			timeZoneId = "GMT";
		}		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		cal.setTime(date);
		return getMorningOfDay(cal).getTime();
	}
	
	/**
	 * Returns the given date but with time 23:59:59 in GMT timezone
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMidnightOfDayInGMTZone(Date date) {
		return getMidnightOfDay(date, "GMT");
	}
	
	/**
	 * Returns the given date by timezone
	 * 
	 * @param date
	 * @param timeZone
	 * @return
	 */
	public static Date getMidnightOfDay(Date date, String timeZone) {
		String timeZoneId = timeZone;
		if(org.apache.commons.lang.StringUtils.isEmpty(timeZone)) {
			timeZoneId = "GMT";
		}		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		cal.setTime(date);
		return getMidnightOfDay(cal).getTime();
	}
	/**
     * @adnlog.notrace
	 */
	public static Calendar getDateAsCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
		}
		return calendar;
	}

	/**
     * @adnlog.notrace
	 */
	public static boolean isBetween(Date date, Date from, Date to) {
		return (date.equals(from) || date.after(from)) && (date.equals(to) || date.before(to));
	}


	/**
     * @adnlog.notrace
	 */
	public static Calendar setTimePart(Calendar cal, int hour, int minute, int second, int msec) {
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, msec);
		return cal;
	}

	/**
     * @adnlog.notrace
	 */
	public static Calendar getTimePart(Calendar cal) {
		Calendar timePart = (Calendar)cal.clone();
		timePart.set(Calendar.YEAR, 0);
		timePart.set(Calendar.MONTH, 0);
		timePart.set(Calendar.DAY_OF_MONTH, 0);
		return timePart;
	}

	/**
     * @adnlog.notrace
	 */
	public static Calendar getDatePart(Calendar cal) {
		Calendar datePart = (Calendar)cal.clone();
		setTimePart(datePart, 0, 0, 0, 0);
		return datePart;
	}

	/**
	 * Returns the number of weekends between two dates.
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getWeekendsBetween(Calendar from, Calendar to) {
		int result = 0;
		if (from == null || to == null) {
			LOG.error("Invalid from or to cannot calculate the week difference between them");
			return result = 0;
		}
		Calendar delFromClone = getDatePart(from);
		Calendar delToClone = getDatePart(to);

		Long oneDayMillis = 24 * 60 * 60 * 1000L;
		Long oneWeekMillis = 7L * oneDayMillis;

		double delToWeek = (double)delToClone.getTimeInMillis() / (double)oneWeekMillis;

		double delFromWeek = (double) delFromClone.getTimeInMillis() / (double)oneWeekMillis;
		double weekDiff = delToWeek - delFromWeek;
		int weekends = Double.valueOf(weekDiff).intValue();
		delFromClone.add(Calendar.WEEK_OF_YEAR, weekends);

		//check if the non-whole time remaining which is less than a week contains weekend or not
		if (delToClone.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
				&& delToClone.get(Calendar.DAY_OF_WEEK) < delFromClone.get(Calendar.DAY_OF_WEEK)) {
			weekends ++;
		}

		return result = weekends;
	}

	/**
	 * Returns the difference between two dates in days
	 * E.g. 2009/03/15 09:00 - 2009/03/14 22:00 = 1
	 *
	 * @return
	 *
     * @adnlog.notrace
	 */
	public static Long getDayDiff(Calendar from, Calendar to) {
		Long oneDayMillis = 24 * 60 * 60 * 1000L;
		Calendar toWoTime = getDatePart(to);
		Calendar fromWoTime = getDatePart(from);
		return (toWoTime.getTimeInMillis() - fromWoTime.getTimeInMillis()) / oneDayMillis;
	}

	public static Long getMillisBeforeMidnight(Calendar cal) {
		Calendar midnight = (Calendar)cal.clone();
		setTimePart(midnight, 23, 59, 59, 999);
		return midnight.getTimeInMillis() - cal.getTimeInMillis();
	}

	public static Long getMillisAfterMidnight(Calendar cal) {
		return cal.getTimeInMillis() - getDatePart(cal).getTimeInMillis();
	}

    public static Long getDeliveryTimeDiffMillis(Calendar delFrom, Calendar delTo, Calendar open1From, Calendar open1To, Calendar open2From, Calendar open2To) throws IllegalArgumentException {
    	
    	if (delFrom.after(delTo)) {
    		throw new IllegalArgumentException("Delivery From must not be later than Delivery To");
    	}
    	
    	final Long delFromTimePartTimeInMillis = getTimePart(delFrom).getTimeInMillis();
    	final Long delToTimePartTimeInMillis = getTimePart(delTo).getTimeInMillis();
    	
    	final Long open1ToTimeInMillis = open1To.getTimeInMillis();
    	final Long open2FromTimeInMillis = open2From.getTimeInMillis();

    	final Long dayDiff = getDayDiff(delFrom, delTo);
    	final Long lunchTimeDiff = open2FromTimeInMillis - open1ToTimeInMillis;
    	Long diffMillis = delTo.getTimeInMillis() - delFrom.getTimeInMillis();
    	Long lunchMultiplier = 0L;

    	// Minus nights
    	diffMillis -= dayDiff * (DateTimeUtils.getMillisBeforeMidnight(open2To) + DateTimeUtils.getMillisAfterMidnight(open1From));

    	// Minus lunch breaks
    	// Case 1
    	if (delFromTimePartTimeInMillis <= open1ToTimeInMillis && delToTimePartTimeInMillis >= open2FromTimeInMillis) {
    		lunchMultiplier = dayDiff + 1;
    	}
    	// Case 2
    	else if (delFromTimePartTimeInMillis >= open2FromTimeInMillis && delToTimePartTimeInMillis >= open2FromTimeInMillis) {
    		lunchMultiplier = dayDiff;
    	}
    	// Case 3
    	else if (dayDiff > 0 && delFromTimePartTimeInMillis >= open2FromTimeInMillis && delToTimePartTimeInMillis <= open1ToTimeInMillis) {
    		lunchMultiplier = dayDiff - 1;
    	}
    	// Case 4
    	else if (delFromTimePartTimeInMillis <= open1ToTimeInMillis && delToTimePartTimeInMillis <= open1ToTimeInMillis) {
    		lunchMultiplier = dayDiff;
    	}
    	else {
    		throw new LogonException("Unhandled case of lunch breaks", Reason.RES_IMPL_BUG);
    	}

		diffMillis -= lunchMultiplier * lunchTimeDiff;

		//LOG.debug(String.format("Delivery From=%s, Delivery To=%s, Lunch Breaks=%s, Day Diff=%s", delFrom.getTime(), delTo.getTime(), lunchMultiplier, dayDiff));

    	return diffMillis;

    }

    public static Calendar getCalendarFromMinAfterMidnight(Long mins) {
    	Calendar cal = Calendar.getInstance();
		setTimePart(cal, 0, 0, 0, 0);
		cal.add(Calendar.MINUTE, mins.intValue());
		return cal;
    }

	public static String formatDuration (long dur) {
		return String.format("%dh%02dm%02ds", dur/3600000, ((dur/1000)%3600)/60, ((dur/1000)%60));
	}

	public static Calendar getMidnightOfDay(Calendar day) {
		Calendar cal = (Calendar)day.clone();
		setTimePart(cal, 23, 59, 59, 999);
		return cal;
	}

	public static Date getMidnightOfDay(Date day) {
		return getMidnightOfDay(getDateAsCalendar(day)).getTime();
	}

	public static Calendar getMorningOfDay(Calendar day) {
		Calendar cal = (Calendar)day.clone();
		setTimePart(cal, 0, 0, 0, 0);
		return cal;
	}

	public static Date getMorningOfDay(Date day) {
		return getMorningOfDay(getDateAsCalendar(day)).getTime();
	}

	/*
	 * Only a helper method to help mocking the current time in a centralized place during development
	 */
	public static Date getNow() {
		Calendar cal = Calendar.getInstance();
		//setTimePart(cal, 11, 22, 0, 0);
		return cal.getTime();
	}

	/*
	 * Only a helper method to help mocking the current time in a centralized place during development
	 */
	public static Calendar getNowCal() {
		Calendar cal = Calendar.getInstance();
		//setTimePart(cal, 11, 22, 0, 0);
		return cal;
	}
	/*
	 * Returns the length of the time in d:h: min.
	 */
	public static String getFormattedTimeLengthFromMillis(Long timeLengthInMillis) {

		if (timeLengthInMillis != null) {
			Long oneDayMillis = 24 * 60 * 60 * 1000L;
    		int days = (int) Math.floor(timeLengthInMillis/(oneDayMillis));
    		int hours = (int)Math.floor((timeLengthInMillis -(days*oneDayMillis))/(60*60*1000L));
    		int minutes = (int)((timeLengthInMillis-(days*oneDayMillis)-(hours*60*60*1000L))/(60*1000L));

    	  	return  days + "d " + hours + "h " + minutes + "min";
		}
		else{
			return "";
		}

	}

	public static Calendar getNextQuarterHour(Calendar cal) {
		Calendar now = (Calendar) cal.clone();

		if (now.get(Calendar.MINUTE) < 15) {
			now.set(Calendar.MINUTE, 15);
			return now;
		}
		if (now.get(Calendar.MINUTE) < 30) {
			now.set(Calendar.MINUTE, 30);
			return now;
		}
		if (now.get(Calendar.MINUTE) < 45) {
			now.set(Calendar.MINUTE, 45);
			return now;
		}

		now.set(Calendar.MINUTE, 0);
		now.add(Calendar.HOUR, 1);
		return now;
	}

	public static boolean isToday(Date date, String timeZoneId) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timeZoneId);

		DateTime now = new DateTime(tz);
		DateTime d = new DateTime(date, tz);

		return DateTimeComparator.getDateOnlyInstance().compare(now, d) == 0;
	}

	public static Boolean isOnSameDay(Date date1, Date date2){
		Boolean result = null;

		Date morning = DateTimeUtils.getMorningOfDay(date1);
		Date midnight = DateTimeUtils.getMidnightOfDay(date1);

		if ( date2.after(morning) && date2.before(midnight) || date2.equals(morning) || date2.equals(midnight) ){
			result = Boolean.TRUE;
			result = Boolean.TRUE;
		}
		else {
			result = Boolean.FALSE;
		}

		return result;
	}

	/**
	 * Returns the diff in minutes
	 */
	public static int getDifferenceInMinutes(Date from, Date to){
		// [[
		int result = 0;
		Object[][] params = {{"from", from},
							{"to", to}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getDifferenceInMinutes", params);
		}
		// ]]
		try {
			int oneminute = 1000 * 60;
			long l = ((to.getTime() - from.getTime()) / oneminute);
			return result = Double.valueOf(l).intValue();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static int getDayDifference(Date fromDate, Date toDate) {
		//clear time on dates
		Calendar fromCal = Calendar.getInstance();
		fromCal.setTime(fromDate);
		fromCal = getDatePart(fromCal);

		Calendar toCal = Calendar.getInstance();
		toCal.setTime(toDate);
		toCal = getDatePart(toCal);

		long fromMs = fromCal.getTimeInMillis();
		long toMs = toCal.getTimeInMillis();
		long diffMs = toMs - fromMs;
		long diffDays = TimeUnit.MILLISECONDS.toDays(diffMs);
		return (int) diffDays;
	}

	/**
	 * getTimePartInMinutes reversal
	 */
	@AdnTrace
	public static Calendar setTimePartInMinutes(Calendar calendar, long timePartInMinutes) {
		int hour = (int) timePartInMinutes / 60;
		int minutes = (int) timePartInMinutes % 60;
		return setTimePart(calendar, hour, minutes, 0,0);
	}

	/**
	 * Returns the elapsed time until the time given in the calendar time
	 * fields (hour, minute). Example: 1:32am -> 92min.
	 * @param calendar
	 * @return elapsed time in minutes
	 */
	public static long getTimePartInMinutes(Calendar calendar) {
		// [[
		long result = 0L;
		Object[][] params = {{"calendar", calendar}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DateTimeUtils.class, "getTimePartInMinutes", params);
		}
		// ]]
		try {
			result = 60L * calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE);
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Checks whether the two timezones given by the ids <em>tz1</em> and <em>tz2</em>
	 * represent the same region of Earth sharing the same standard time.
	 *
	 * @param tz1 the Olsen TimeZone id for the first timezone
	 * @param tz2 the Olsen TimeZone id for the second timezone
	 * @return true of both timezones due in fact represent the same region, false otherwise
	 */
	public static boolean areSameTimeZones(String tz1, String tz2) {
		DateTimeZone dtz1 = DateTimeZoneFactory.forID(tz1);
		DateTimeZone dtz2 = DateTimeZoneFactory.forID(tz2);

		return areSameTimeZones(dtz1, dtz2);
	}

	/**
	 * Checks whether the two timezone objects, <em>dtz1</em> and <em>dtz2</em>,
	 * represent the same region of Earth sharing the same standard time.
	 *
	 * @param dtz1 reference to the first timezone
	 * @param dtz2 reference to the second timezone
	 * @return true of both timezones due in fact represent the same region, false otherwise
	 */
	public static boolean areSameTimeZones(DateTimeZone dtz1, DateTimeZone dtz2) {
		if (dtz1.equals(dtz2)) {
			return true;
		}

		// The JODA DateTimeZoneFactory.equals() implementation checks that the ID, offset, and
		// standard offset are all equal, but it is unclear why, since two timezones
		// with different ids can have the same offset and standard offset. For example,
		// Europe/Paris and Europe/Zurich due in fact represent the same timezone
		// even though they have different ids.
		//
		// In our case, we are only interested if both DateTimeZone objects do in fact
		// represent the same region on the Earth sharing the same standard time - that is,
		// would a Date object be represented the same in both time zones.

		long now = System.currentTimeMillis();

		// checks that both DST and non-DST times are equal
		return dtz1.getOffset(now) == dtz2.getOffset(now)
				&& dtz1.getStandardOffset(now) == dtz2.getStandardOffset(now);
	}

	/**
	 * Utility method which parses the given number of <em>minutes</em> representing
	 * the minutes after midnight (or the minutes from the beginning of the day) and
	 * formats the result in the form <em>HH:mm</em> (2-digit hour of day and 2-digit
	 * minute of hour)
	 *
	 * @param minutes number of minutes since midnight (start of day) in UTC
	 * @return a String object of the form <em>HH:mm</em> whereby HH is the 2-digit hour
	 *         of the day and mm is the 2-digit minute of the hour. If parsing of the given
	 *         <em>minutes</em> fails for any reason, then an empty String <code>""</code>
	 *         is returned.
	 */
	public static String formatMinutesSinceMidnight(Long minutes) {
		Date d = DateTimeParser.parseMinutesSinceMidnight(minutes);
		return DateTimeFormat.asHourMinute(d, "UTC");
	}

	/**
	 * Returns a short name for the given TimeZone Id.
	 */
	public static String getShortNameForTimeZoneId(Date timestamp, String timeZoneId) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timeZoneId);
		return tz.getShortName(timestamp.getTime(), Locale.ENGLISH);
	}

	/**
	 * Returns the current time in the given time zone as a Calendar object.
	 */
	public static Calendar getCalendarInstance(String timeZoneId, Locale locale) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timeZoneId);
		return Calendar.getInstance(tz.toTimeZone(), locale);
	}

	/**
	 * Returns a java.util.Date object representing the current date in the given timezone.
	 */
	public static Date today(String timeZoneId) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timeZoneId);

		LocalDate ld = new LocalDate(tz);
		return ld.toDateTimeAtStartOfDay(tz).toDate();
	}

	/**
	 * Computes the millisecond time in the given timezone.
	 *
	 * For example, if the given reference Date holds the number of milliseconds for
	 * 12:30 in Europe/Paris (ie. 10:30Z), then result from this method is a Date object
	 * holding the number of milliseconds for the time 12:30 in UTC.
	 *
	 * Required for the Scheduling screen.
	 */
	public static Date convertDateToTimeZone(Date reference, String fromTimeZoneId, String toTimeZoneId) {
		if (reference == null) {
			return reference;
		}

		DateTimeZone tz = DateTimeZoneFactory.forID(fromTimeZoneId);

		DateTime from = new DateTime(reference.getTime(), tz);
		DateTime to = from.withZoneRetainFields(DateTimeZoneFactory.forID(toTimeZoneId));
		return to.toDate();
	}

	public static Long getMinutesAfterMidnightFromDateString(String dateString) throws ParseException {
		Date date = parseHoursMinutes(dateString);
		return getMinAfterMidnight(date);
	}

	public static String getHourMinuteTimePart(Long rawData, Long defaultValue) {
		Long rawDataNotNull = rawData != null ? rawData : defaultValue;
		return formatHoursMinutes(getHoursMinutes(rawDataNotNull));
	}

	public static String getTimePart(Date defaultdeliveryFromDat, Long defaultValue) {
		Calendar defaultdeliveryFromDatCal = getDateAsCalendar(defaultdeliveryFromDat);
		long timePartInMinutes = getTimePartInMinutes(defaultdeliveryFromDatCal);
		return getHourMinuteTimePart(new Long(timePartInMinutes), defaultValue);
	}
}

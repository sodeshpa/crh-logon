/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

import com.crh.logon.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author AdNovum Informatik AG
 */
public class DateTimeParser implements DateTimePatterns {

	public static Date parseLogonDayMonthYear(Locale locale, String text) throws ParseException {
		return parseText(text, LOGON_DAY_MONTH_YEAR, locale);
	}

	public static Date parseLogonDayMonthYear(String text) throws ParseException {
		return parseLogonDayMonthYear(Locale.ENGLISH, text);
	}

	public static Date parseLogonDayMonthYear(String text, Locale locale, String timezone) throws ParseException {
		return parseText(text, LOGON_DAY_MONTH_YEAR, locale, timezone);
	}

	public static LocalDate parseLogonDayMonthYearToLocalDate(String text, Locale locale) throws ParseException {
		if (StringUtils.empty(text)) {
			return null;
		}

		try {
			return LocalDate.parse(text, java.time.format.DateTimeFormatter.ofPattern(LOGON_DAY_MONTH_YEAR, locale));
		} catch (DateTimeParseException e) {
			// callers are expecting this type of exception
			throw new ParseException(e.toString(), -1);
		}
	}

	public static Date parseHourMinute(String text) throws ParseException {
		return parseText(text, HOUR_MINUTE);
	}

	public static Date parseHourMinuteSecond(String text) throws ParseException {
		return parseText(text, HOUR_MINUTE_SECOND);
	}

	public static Date parseLogonDayMonthYear4(String text) throws ParseException {
		return parseText(text, LOGON_DAY_MONTH_YEAR4);
	}

	public static Date parseLogonDayMonthYear4(String text, Locale locale, String timezone) throws ParseException {
		return parseText(text, LOGON_DAY_MONTH_YEAR4, locale, timezone);
	}

	public static Date parseLogonDayMonthYear4JS(Locale locale,String text) throws ParseException {
		return parseText(text, DATETIME_NO_TZ, locale);
	}

	public static Date parseDayMonthYearHourMinute(String text, String timezone) throws ParseException {
		return parseText(text, LOGON_DAY_MONTH_YEAR_HOUR_MINUTE, Locale.ENGLISH, timezone);
	}

	public static Date parseDayMonthYear4HourMinute(String text, String timezone) throws ParseException {
		return parseText(text, LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE, Locale.ENGLISH, timezone);
	}

	public static Date parseDayMonthYear4HourMinuteSecond(String text, String timezone) throws ParseException {
		return parseText(text, LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE_SECOND, Locale.ENGLISH, timezone);
	}

	/**
	 * Converts the given number of minutes since the start of the day to a date object
	 * with the hour-of-day and minute-of-day.
	 * <p>
	 * The number of <em>minutes</em> are independent of timezone. That is, a value of
	 * 600 represents 10AM in UTC, CET, PST, etc. For this reason, when the number
	 * of <em>minutes</em> is internally converted to a <code>java.util.Date</code> object,
	 * a timezone of UTC is used.
	 * </p>
	 *
	 * @param minutes number of minutes since midnight (start of day) in UTC
	 * @return a new Date object
	 */
	public static Date parseMinutesSinceMidnight(Long minutes) {
		if (minutes == null) {
			return null;
		}

		int hourOfDay = (int) (minutes / 60);
		int minuteOfHour = (int) (minutes % 60);

		if (hourOfDay < 0 || hourOfDay > 23) {
			return null;
		}

		LocalTime lt = new LocalTime(hourOfDay, minuteOfHour);

		DateTime dt = lt.toDateTimeToday(DateTimeZone.UTC);
		return dt.toDate();
	}

	private static Date parseText(String text, String pattern) throws ParseException {
		return parseText(text, pattern, Locale.ENGLISH);
	}

	private static Date parseText(String text, String pattern, Locale locale) throws ParseException {
		return parseText(text, pattern, locale, "Europe/Paris");
	}

	private static Date parseText(String text, String pattern, Locale locale, String timezone) throws ParseException {
		if (StringUtils.empty(text)) {
			return null;
		}

		DateTimeZone zone = DateTimeZoneFactory.forID(timezone);

		DateTimeFormatter formatter =
				org.joda.time.format.DateTimeFormat.forPattern(pattern);

		formatter = formatter.withZone(zone)
							 .withLocale(locale);
		try {
			DateTime dt = formatter.parseDateTime(text);
			return dt.toDate();
		}
		catch (Exception exc) {
			// callers are expecting this type of exception
			throw new ParseException(exc.toString(), -1);
		}
	}
}

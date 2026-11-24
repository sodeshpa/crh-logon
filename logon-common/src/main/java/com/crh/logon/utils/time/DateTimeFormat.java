/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.FormatUtils;

/**
 * The <code>DateTimeFormat</code> is the low level class for formatting dates. It is used by
 * higher level classes.
 *
 * @author AdNovum Informatik AG
 */
public class DateTimeFormat implements DateTimePatterns {

	/**
	 * 
	 */
	private static final String GMT_TIMEZONE = "GMT";

	// taken from org.joda.time.format.DateTimeFormatterBuilder.TimeZoneOffset and
	// adapted for Crh
	private static class TimeZoneOffsetPrinter implements org.joda.time.format.DateTimePrinter {
		public int estimatePrintedLength() {
			// at a minimum we return: +H (3 chars) and max we return: +HH:MM (6 chars)
			return 6;
		}

		public void printTo(StringBuffer buf, long instant, Chronology chrono,
							int displayOffset, DateTimeZone displayZone, Locale locale) {

			if (displayZone == null) {
				return;  // no zone
			}
			if (displayOffset >= 0) {
				buf.append('+');
			}
			else {
				buf.append('-');
				displayOffset = -displayOffset;
			}

			int hours = displayOffset / DateTimeConstants.MILLIS_PER_HOUR;
			FormatUtils.appendPaddedInteger(buf, hours, 1);

			displayOffset -= hours * DateTimeConstants.MILLIS_PER_HOUR;
			if (displayOffset == 0) {
				return;
			}

			int minutes = displayOffset / DateTimeConstants.MILLIS_PER_MINUTE;
			buf.append(':');

			FormatUtils.appendPaddedInteger(buf, minutes, 2);
		}

		public void printTo(Writer out, long instant, Chronology chrono,
							int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {

			StringBuffer buf = new StringBuffer(estimatePrintedLength());
			printTo(buf, instant, chrono, displayOffset, displayZone, locale);

			if (buf.length() > 0) {
				out.write(buf.toString());
			}
		}

		public void printTo(StringBuffer buf, ReadablePartial partial, Locale locale) {
			// no zone info
		}

		public void printTo(Writer out, ReadablePartial partial, Locale locale) throws IOException {
			// no zone info
		}
	}


	private static HashMap<String, DateTimeFormatter> customDateTimeFormatters = new HashMap<String, DateTimeFormatter> (14);
	
	static {
		customDateTimeFormatters.put(HOUR_MINUTE, org.joda.time.format.DateTimeFormat.forPattern(HOUR_MINUTE));
		customDateTimeFormatters.put(HOUR_MINUTE_SECOND, org.joda.time.format.DateTimeFormat.forPattern(HOUR_MINUTE_SECOND));
		customDateTimeFormatters.put(DAY_MONTH, org.joda.time.format.DateTimeFormat.forPattern(DAY_MONTH));
		customDateTimeFormatters.put(DAY_MONTH_WITH_DOT, org.joda.time.format.DateTimeFormat.forPattern(DAY_MONTH_WITH_DOT));
		customDateTimeFormatters.put(LOGON_DAY_MONTH_YEAR, org.joda.time.format.DateTimeFormat.forPattern(LOGON_DAY_MONTH_YEAR));
		customDateTimeFormatters.put(LOGON_DAY_MONTH_YEAR4, org.joda.time.format.DateTimeFormat.forPattern(LOGON_DAY_MONTH_YEAR4));
		customDateTimeFormatters.put(LOGON_DAY_MONTH_HOUR_MINUTE, org.joda.time.format.DateTimeFormat.forPattern(LOGON_DAY_MONTH_HOUR_MINUTE));
		customDateTimeFormatters.put(LOGON_DAY_MONTH_YEAR_HOUR_MINUTE, org.joda.time.format.DateTimeFormat.forPattern(LOGON_DAY_MONTH_YEAR_HOUR_MINUTE));
		customDateTimeFormatters.put(LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE, org.joda.time.format.DateTimeFormat.forPattern(LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE));
		customDateTimeFormatters.put(LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE_SECOND, org.joda.time.format.DateTimeFormat.forPattern(LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE_SECOND));
		customDateTimeFormatters.put(DATETIME_NO_TZ, org.joda.time.format.DateTimeFormat.forPattern(DATETIME_NO_TZ));
		customDateTimeFormatters.put(DATE_NO_TZ, org.joda.time.format.DateTimeFormat.forPattern(DATE_NO_TZ));
		customDateTimeFormatters.put(LOGON_DETAILED_DATE_TIME, org.joda.time.format.DateTimeFormat.forPattern(LOGON_DETAILED_DATE_TIME));
		customDateTimeFormatters.put(USER_ACTIVITY, org.joda.time.format.DateTimeFormat.forPattern(USER_ACTIVITY));
		customDateTimeFormatters.put(DATETIME_ISO8601, org.joda.time.format.DateTimeFormat.forPattern(DATETIME_ISO8601));

		customDateTimeFormatters.put(UTC_OFFSET_FULL,
									 new DateTimeFormatterBuilder()
									 .appendLiteral("UTC")
									 .append(new TimeZoneOffsetPrinter())
									 .toFormatter());

		customDateTimeFormatters.put(EXPORT_REPORT_TITLE_DATE,
									 org.joda.time.format.DateTimeFormat.forPattern(EXPORT_REPORT_TITLE_DATE));
		
		customDateTimeFormatters.put(JS_YEAR_MONTH_DAY_HOUR_MINUTE, org.joda.time.format.DateTimeFormat.forPattern(JS_YEAR_MONTH_DAY_HOUR_MINUTE));
		customDateTimeFormatters.put(JS_YEAR_MONTH_DAY_24HOUR_MINUTE, org.joda.time.format.DateTimeFormat.forPattern(JS_YEAR_MONTH_DAY_24HOUR_MINUTE));
	}


	
	@SuppressWarnings("unused")
	public static String getDateTimeFormat4JS(Locale locale) {
		return "d.m.y H:i:s";
	}

	@SuppressWarnings("unused")
	public static String getDateAltFormats4JS(Locale locale) {
		return "j.n.y|j.n.Y";
	}

	@SuppressWarnings("unused")
	public static String getDateFormat4JS(Locale locale) {
		return "d.m.y";
	}

	@SuppressWarnings("unused")
	public static String getHourMinuteFormat4JS(Locale locale) {
		return JS_HOUR_MINUTE;
	}

	/**
	 * @return the time for the given date in the format HH:mm or an empty string if
	 *         date is null
	 */
	public static String asHourMinute(Date date) {
		return formatWithPattern(date, HOUR_MINUTE);
	}

	public static String asHourMinute(Date date, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, Locale.ENGLISH, tz, HOUR_MINUTE);
	}

	/**
	 * @return the time for the given date in the format HH:mm:ss or an empty string if
	 *         date is null
	 */
	public static String asHourMinuteSecond(Date date) {
		return formatWithPattern(date, HOUR_MINUTE_SECOND);
	}

	public static String asHourMinuteSecond(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, HOUR_MINUTE_SECOND);
	}

	/**
	 * @return the date and time for the given date object in the format
	 *         yyyy-MM-dd'T'HH:mm:ss or null if date is null
	 */
	public static String forMop(Date date) {
		if (date == null) {
			return null;
		}

		return formatWithPattern(date, DATETIME_ISO8601);
	}

	/**
	 * @return the date and time for the given date object in the format
	 *         yyyy-MM-dd'T'HH:mm:ss or null if date is null
	 */
	public static String forPws(Date date) {
		if (date == null) {
			return null;
		}

		return formatWithPattern(date, DATETIME_NO_TZ);
	}

	/**
	 * @return the date for the given date object in the format
	 *         yyyy-MM-dd or null if date is null
	 */
	public static String forSap(Date date) {
		if (date == null) {
			return null;
		}

		return formatWithPattern(date, DATE_NO_TZ);
	}

	public static String forUserActivity(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, USER_ACTIVITY);
	}

	/**
	 * Returning Logon common Date with Day and Month from the given Date in the
	 * manner of '31.12'.
	 * If the date parameter is null, than an empty String is returned.
	 */
	public static String asDayMonth(Date date) {
		return formatWithPattern(date, DAY_MONTH);
	}

	public static String asDayMonth(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, DAY_MONTH);
	}

	public static String asDayMonthTrailingDot(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, DAY_MONTH_WITH_DOT);
	}

	public static String asDayMonthHourMinuteTrailingDot(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, LOGON_DAY_MONTH_HOUR_MINUTE);
	}

	/**
	 * Returning Logon common Date with Day and Month and Year from the given
	 * Date in the manner of '31.12.13'.
	 * If the date parameter is null, than an empty String is returned.
	 */
	public static String asLogonDayMonthYear(Date date) {
		return asLogonDayMonthYear(Locale.GERMAN, date);
	}

	public static String asLogonDayMonthYear(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, LOGON_DAY_MONTH_YEAR);
	}

	public static String asLogonDayMonthYearHourMinute(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, LOGON_DAY_MONTH_YEAR_HOUR_MINUTE);
	}
	
	/**
	 * Returns the {@link Date} in "YYYY,mm,DD,HH,MM" format
	 * @param date the {@link Date}
	 * @param locale the {@link Locale}
	 * @param timezone the time zone as {@link String}
	 * @return the {@link Date} in "YYYY,mm,DD,HH,MM" format
	 */
	public static String asYearMonthDayHourMinute(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, JS_YEAR_MONTH_DAY_24HOUR_MINUTE);
	}

	/**
	 * Returns a String representation of the given date in a format like: dd.MM.yyyy HH:mm  (the year is in 4 digit format)
	 * @param date
	 * @param locale
	 * @param timezone
	 * @return
	 */
	public static String asLogonDayMonthYear4HourMinute(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE);
	}
	
	public static String asLogonDayMonthYearInGMTZone(Locale locale, Date date) {
		return asLogonDayMonthYear(locale, date, GMT_TIMEZONE);
	}

	public static String asLogonDayMonthYear(Locale locale, Date date) {
		return formatWithPattern(date, locale, LOGON_DAY_MONTH_YEAR);
	}

	public static String asLogonDayMonthYear(Locale locale, Date date, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, LOGON_DAY_MONTH_YEAR);
	}


	public static String asLogonDayMonthYear4(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, LOGON_DAY_MONTH_YEAR4);
	}

	public static String asLogonDetailedDateTime(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, LOGON_DETAILED_DATE_TIME);
	}

	public static String asUTCOffset(Date date, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, Locale.ENGLISH, tz, UTC_OFFSET_FULL);
	}

	/**
	 * @return the date in a full style pattern such as Friday, December 3, 2013.
	 */
	public static String asExportReportTitleDate(Date date, Locale locale, String timezone) {
		DateTimeZone tz = DateTimeZoneFactory.forID(timezone);
		return formatWithPattern(date, locale, tz, EXPORT_REPORT_TITLE_DATE);
	}

	private static String formatWithPattern(Date date, String pattern) {
		return formatWithPattern(date, Locale.ENGLISH, pattern);
	}

	private static String formatWithPattern(Date date, Locale locale, String pattern) {
		return formatWithPattern(date, locale, null, pattern);
	}

	private static String formatWithPattern(Date date, Locale locale, DateTimeZone tz, String pattern) {
		if (date == null) {
			return "";
		}
		DateTimeFormatter formatter = customDateTimeFormatters.get(pattern);
		formatter = formatter.withLocale(locale);

		DateTime dt = new DateTime(date, tz);
		return formatter.print(dt);
	}

	public static String[] splitDateTimeAsStringParts(DateTime dateTime) {

		// 2013-10-17T14:00:00.000+02:00
		// 2013-11-21T08:12:44.026Z
		String toDateStr = dateTime.toString();

		// In the following code part we cannot use String.subString() because this method is called five times for every record of the export(!) this
		// character based 'substring' is 3-4 times faster. Otherwise for ~8k records the export would be around 20 second which is not acceptable
		String shiftedDatePart = String.valueOf(
			new char[]{
				// 17.10.2013
				toDateStr.charAt(8), toDateStr.charAt(9), '.',toDateStr.charAt(5), toDateStr.charAt(6),'.',toDateStr.charAt(0),toDateStr.charAt(1),toDateStr.charAt(2),toDateStr.charAt(3)
			}
		);
		String shiftedTimePart = String.valueOf(
			new char[]{
				// 14.00.00 
				toDateStr.charAt(11), toDateStr.charAt(12), ':',toDateStr.charAt(14), toDateStr.charAt(15),'.',toDateStr.charAt(17),toDateStr.charAt(18)
			}
		);

		String shiftedTimezonePart = null;
		// If in greenwich it won't contain the +XY:00 shift
		if (toDateStr.indexOf('+')==-1) {
			shiftedTimezonePart = "+00:00";
		}
		else {
			shiftedTimezonePart = String.valueOf(
				new char[]{
					// +02:00
					toDateStr.charAt(23),toDateStr.charAt(24),toDateStr.charAt(25),toDateStr.charAt(26),toDateStr.charAt(27),toDateStr.charAt(28)
				}
			);
		}

		return new String[]{shiftedDatePart, shiftedTimePart, shiftedTimezonePart};

	}

}
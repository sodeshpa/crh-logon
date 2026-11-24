package com.crh.logon.business.services.utils;


import java.io.Serializable;
import java.text.Collator;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.utils.time.DateTimeParser;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.time.DateTimeZoneFactory;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The <code>TimeZone</code> object represents a business level entity which encapsulates information
 * pertaining to an entity's timezone.
 *
 * @author AdNovum Informatik AG
 */
public class TimeZone implements Serializable {

	private static final long serialVersionUID = 1L;

	private static volatile TimeZone APP_TIMEZONE;

	private static abstract class TZComparator implements Comparator {
		protected final Collator collator;

		protected TZComparator(Locale locale) {
			this.collator = Collator.getInstance(locale);
		}

		protected int compareTZ(TimeZone tz1, TimeZone tz2) {
			int c = collator.compare(tz1.country, tz2.country);
			if (c == 0) {
				c = collator.compare(tz1.region, tz2.region);
			}

			return c;
		}
	}

	public static class AscComparator extends TZComparator {
		public AscComparator(Locale locale) {
			super(locale);
		}

		public int compare(Object o1, Object o2) {
			return compareTZ((TimeZone) o1, (TimeZone) o2);
		}
	}

	private final String timeZoneCd;
	private final Locale locale;
	private final ZoneId zoneId;

	private String country;
	private String region;

	public TimeZone(String timeZoneCd, Locale locale) {
		this.timeZoneCd = timeZoneCd;
		this.locale = locale;
		this.country = this.region = "";
		this.zoneId = DateTimeZoneFactory.getZoneId(timeZoneCd);
	}

	public static TimeZone getAppTimeZone() {
		if (APP_TIMEZONE == null) {
			APP_TIMEZONE = new TimeZone(DateTimeZoneFactory.getDefaultTimeZoneId(), Locale.ENGLISH);
		}

		return APP_TIMEZONE;
	}

	public static TimeZone getDBTimeZone() {
		return getAppTimeZone();
	}

	public String getId() {
		return getTimezoneCd();
	}

	public String getTimezoneCd() {
		return timeZoneCd;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}

	public TimeZone withCountry(String country) {
		if (country != null) {
			this.country = country;
		}
		return this;
	}

	public TimeZone withRegion(String region) {
		if (region != null) {
			this.region = region;
		}
		return this;
	}

	public boolean isSame(TimeZone other) {
		return isSame(other.timeZoneCd);
	}

	public boolean isSame(String timeZoneId) {
		return DateTimeUtils.areSameTimeZones(timeZoneCd, timeZoneId);
	}

	public String fmtAsDetailedDateTime(Date date) {
		return DateTimeFormat.asLogonDetailedDateTime(date, locale, timeZoneCd);
	}

	public String fmtAsDayMonth(Date date) {
		return DateTimeFormat.asDayMonth(date, locale, timeZoneCd);
	}

	public String fmtAsDayMonthTrailingDot(Date date) {
		return DateTimeFormat.asDayMonthTrailingDot(date, locale, timeZoneCd);
	}

	public String fmtAsDayMonthHourMinuteTrailingDot(Date date) {
		return DateTimeFormat.asDayMonthHourMinuteTrailingDot(date, locale, timeZoneCd);
	}

	public String fmtAsDayMonthYear(Date date) {
		return DateTimeFormat.asLogonDayMonthYear(date, locale, timeZoneCd);
	}

	public String fmtAsDayMonthYearHourMinute(Date date) {
		return DateTimeFormat.asLogonDayMonthYearHourMinute(date, locale, timeZoneCd);
	}
	
	/**
	 * Returns the {@link Date} as "YYYY,mm,dd,HH,MM"
	 * @param date the {@link Date}
	 * @return the {@link Date} as "YYYY,mm,dd,HH,MM"
	 */
	public String fmtAsYearMonthDayHourMinute(Date date) {
		return DateTimeFormat.asYearMonthDayHourMinute(date, locale, timeZoneCd);
	}

	public String fmtAsDayMonthYearHourMinuteWithOffset(Date date, TimeZone referenceTZ) {
		String text = fmtAsDayMonthYearHourMinute(date);
		return appendUTCOffsetIfDifferentTZs(text, date, referenceTZ);
	}

	public String fmtAsDayMonthYearHourMinuteWithOffset(Date date) {
		String text = fmtAsDayMonthYearHourMinute(date);
		return appendUTCOffset(text, date);
	}

	public String fmtAsDayMonthYear4HourMinute(Date date) {
		return DateTimeFormat.asLogonDayMonthYear4HourMinute(date, locale, timeZoneCd);
	}

	public String fmtAsDayMonthYear4(Date date) {
		return DateTimeFormat.asLogonDayMonthYear4(date, locale, timeZoneCd);
	}

	public String fmtAsHourMinute(Date date) {
		return DateTimeFormat.asHourMinute(date, timeZoneCd);
	}

	public String fmtAsHourMinuteWithOffset(Date date, TimeZone referenceTZ) {
		String text = fmtAsHourMinute(date);
		return appendUTCOffsetIfDifferentTZs(text, date, referenceTZ);
	}

	public String fmtAsHourMinuteWithOffset(Date date) {
		String text = fmtAsHourMinute(date);
		return appendUTCOffset(text, date);
	}

	public String fmtAsHourMinuteSecond(Date date) {
		return DateTimeFormat.asHourMinuteSecond(date, locale, timeZoneCd);
	}

	public String fmtAsHourMinuteSecondWithOffset(Date date) {
		String text = fmtAsHourMinuteSecond(date);
		return appendUTCOffset(text, date);
	}

	public String fmtForUserActivity(Date date) {
		return DateTimeFormat.forUserActivity(date, locale, timeZoneCd);
	}

	public String fmtForUserActivityWithUTCOffset(Date date) {
		StringBuilder buf = new StringBuilder();
		buf.append(fmtForUserActivity(date))
		   .append(' ')
		   .append(fmtAsUTCOffset(date));

		return buf.toString();
	}

	public String fmtForExportReportTitle(Date date) {
		return DateTimeFormat.asExportReportTitleDate(date, locale, timeZoneCd);
	}
	
	public String fmtAsISO8601(Date date){
		DateTimeZone dateTimeZone = toDateTimeZone();
		DateTimeFormatter fmt = ISODateTimeFormat.basicDateTimeNoMillis().withZone(dateTimeZone);
		return fmt.print(date.getTime());
	}

	public Date parseDayMonthYear(String text) throws ParseException {
		return DateTimeParser.parseLogonDayMonthYear(text, locale, timeZoneCd);
	}

	public Date parseDayMonthYear4(String text) throws ParseException {
		return DateTimeParser.parseLogonDayMonthYear4(text, locale, timeZoneCd);
	}

	public Date parseDayMonthYearHourMinute(String dmy, String hhmm) throws ParseException {
		String text = joinDateTimeStrings(dmy, hhmm);
		return DateTimeParser.parseDayMonthYearHourMinute(text, timeZoneCd);
	}

	public Date parseDayMonthYear4HourMinute(String dmyyyy, String hhmm) throws ParseException {
		String text = joinDateTimeStrings(dmyyyy, hhmm);
		return DateTimeParser.parseDayMonthYear4HourMinute(text, timeZoneCd);
	}

	public Date parseDayMonthYear4HourMinuteSecond(String dmyyy, String hhmmss) throws ParseException {
		String text = joinDateTimeStrings(dmyyy, hhmmss);
		return DateTimeParser.parseDayMonthYear4HourMinuteSecond(text, timeZoneCd);
	}

	public String getFormattedAsTimeZoneOnly() {
		StringBuilder buf = new StringBuilder();

		String tzOffset = fmtAsUTCOffset(new Date());
		if (StringUtils.notEmpty(country) && StringUtils.notEmpty(region)) {
			buf.append(country).append('/').append(region);
		}
		else {
			buf.append(DateTimeUtils.getShortNameForTimeZoneId(
					new Date(), timeZoneCd));
		}

		buf.append(' ').append(tzOffset);
		return buf.toString();
	}

	public String getFormattedAsUTCOffset() {
		return fmtAsUTCOffset(new Date());
	}

	public String fmtAsUTCOffset(Date date) {
		StringBuilder buf = new StringBuilder();
		buf.append('(')
		   .append(DateTimeFormat.asUTCOffset(date, timeZoneCd))
		   .append(')');

		return buf.toString();
	}

	public boolean isToday(Date d) {
		return DateTimeUtils.isToday(d, timeZoneCd);
	}

	public Calendar asCalendar() {
		return DateTimeUtils.getCalendarInstance(timeZoneCd, locale);
	}

	/**
	 * 
	 * @param reference date
	 * @return The Calendar instance that contains the reference date converted to the time zone of the object
	 */
	public Calendar asCalendar(Date reference) {
		Calendar cal = asCalendar();
		cal.setTime(reference);
		return cal;
	}

	public Date today() {
		return DateTimeUtils.today(timeZoneCd);
	}

	public Date tomorrow() {
		return tomorrow(new Date());
	}

	public Date tomorrow(Date from) {
		return DateTimeUtils.tomorrow(asCalendar(from));
	}

	public Date yesterday(Date from) {
		return DateTimeUtils.yesterday(asCalendar(from));
	}

	public Date firstDayThisWeek() {
		return DateTimeUtils.firstDayThisWeek(asCalendar());
	}

	public Date firstDayThisMonth() {
		return DateTimeUtils.firstDayThisMonth(asCalendar());
	}

	public Date firstDayThisYear() {
		return DateTimeUtils.firstDayThisYear(asCalendar());
	}

	public Date nextWorkingDay() {
		return DateTimeUtils.getNextWorkingDay(asCalendar()).getTime();
	}

	/**
	 * Returns the given date but with time 0:00:00
	 * 
	 * @param date
	 * @param timeZone
	 * @return
	 */
	public Date getDayStart(Date d) {
		return DateTimeUtils.getMorningOfDay(d);
	}

	public Date midnightOf(Date d) {
		return DateTimeUtils.getMidnightOfDay(asCalendar(d)).getTime();
	}

	public Date morningOf(Date d) {
		return DateTimeUtils.getMorningOfDay(asCalendar(d)).getTime();
	}

	/**
	 * Shifts the current Date in this time zone to UTC
	 */
	public Date shiftToUTC(Date reference) {
		if (reference == null) {
			return reference;
		}
		return DateTimeUtils.convertDateToTimeZone(reference, timeZoneCd, "UTC");
	}

	public Date shiftToTimeZone(Date reference, TimeZone toTz) {
		if (reference == null) {
			return reference;
		}
		return DateTimeUtils.convertDateToTimeZone(reference, timeZoneCd, toTz.getTimezoneCd());
	}

	/**
	 * It shifts the given Calendar reference into the given timezone
	 * 
	 * @param reference
	 * @param toTz
	 * @return Calendar
	 */
	public Calendar shiftToTimeZone(Calendar reference, TimeZone toTz) {
		if (reference == null) {
			return reference;
		}
		Date currentDate = DateTimeUtils.convertDateToTimeZone(reference.getTime(), timeZoneCd, toTz.getTimezoneCd());
		return toTz.asCalendar(currentDate);
	}
	
	public String toString() {
		return getTimezoneCd();
	}

	private String joinDateTimeStrings(String date, String time) {
		if (StringUtils.empty(date)) {
			return "";
		}

		StringBuilder text = new StringBuilder();
		text.append(date);

		if (StringUtils.notEmpty(time)) {
			text.append(' ').append(time);
		}

		return text.toString();
	}

	private String appendUTCOffsetIfDifferentTZs(String text, Date date, TimeZone referenceTZ) {
		if (StringUtils.empty(text)) {
			return text;
		}
		if (referenceTZ != null && referenceTZ.isSame(this)) {
			return text;
		}

		return appendUTCOffset(text, date);
	}

	private String appendUTCOffset(String text, Date date) {
		StringBuilder buf = new StringBuilder();
		buf.append(text)
		   .append(' ')
		   .append(fmtAsUTCOffset(date));

		return buf.toString();
	}

	public DateTimeZone toDateTimeZone() {
		return DateTimeZoneFactory.forID(getId());
	}

	public Locale getLocale() {
		return locale;
	}
}

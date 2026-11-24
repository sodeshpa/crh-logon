/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author AdNovum Informatik AG
 */
public class DateTimeParserTest {

	// Tue, 28 May 2013 23:22:29 GMT
	// Wed, 29 May 2013 01:22:29 GMT+2 (Europe/Paris)
	private Date testDateTime = new Date(1369783349L * 1000);

	// Wed, 29 May 2013 00:00:00 GMT+2 (Europe/Paris)
	private Date testDate = new Date(1369778400L * 1000);

	// Thu, 01 Jan 1970 01:22:00 GMT+1 (Europe/Paris)
	private Date testTime = new Date(1320L * 1000);

	// Thu, 01 Jan 1970 01:22:29 GMT+1 (Europe/Paris)
	private Date testTimeSec = new Date(1349L * 1000);


	@Test
	public void parseLogonDayMonthYearWithEnglishNull() throws ParseException {
		Date d = DateTimeParser.parseLogonDayMonthYear(Locale.ENGLISH, null);
		assertThat(d, is(nullValue()));
	}

	@Test
	public void parseLogonDayMonthYearWithEnglishValid() throws ParseException {
		String s = DateTimeFormat.asLogonDayMonthYear(testDateTime);
		Date d = DateTimeParser.parseLogonDayMonthYear(Locale.ENGLISH, s);
		assertThat(d, is(equalTo(testDate)));
	}

	@Test
	public void parseLogonDayMonthYearWithFrenchValid() throws ParseException {
		String s = DateTimeFormat.asLogonDayMonthYear(testDateTime);
		Date d = DateTimeParser.parseLogonDayMonthYear(Locale.FRENCH, s);
		assertThat(d, is(equalTo(testDate)));
	}

	@Test(expected = ParseException.class)
	public void parseLogonDayMonthYearWithEnglishInvalid() throws ParseException {
		DateTimeParser.parseLogonDayMonthYear(Locale.ENGLISH, "Feb 292, 100");
	}

	@Test
	public void parseLogonDayMonthYearWithDefaultNull() throws ParseException {
		Date d = DateTimeParser.parseLogonDayMonthYear(null);
		assertThat(d, is(nullValue()));
	}

	@Test
	public void parseHourMinuteNull() throws ParseException {
		Date d = DateTimeParser.parseHourMinute(null);
		assertThat(d, is(nullValue()));
	}

	@Test
	public void parseHourMinuteValid() throws ParseException {
		String s = DateTimeFormat.asHourMinute(testDateTime);
		Date d = DateTimeParser.parseHourMinute(s);
		assertThat(d, is(equalTo(testTime)));
	}

	@Test
	public void parseHourMinuteSecondNull() throws ParseException {
		Date d = DateTimeParser.parseHourMinuteSecond(null);
		assertThat(d, is(nullValue()));
	}

	@Test
	public void parseHourMinuteSecondValid() throws ParseException {
		String s = DateTimeFormat.asHourMinuteSecond(testDateTime);
		Date d = DateTimeParser.parseHourMinuteSecond(s);
		assertThat(d, is(equalTo(testTimeSec)));
	}

	@Test
	public void parseMinutesSinceMidnightWithNull() {
		Date d = DateTimeParser.parseMinutesSinceMidnight(null);
		assertThat(d, is(nullValue()));
	}

	@Test
	public void parseMinutesSinceMidnightWithCET() {
		Date d = DateTimeParser.parseMinutesSinceMidnight(300L);

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTime(d);

		assertThat(c.get(Calendar.HOUR), is(equalTo(5)));
		assertThat(c.get(Calendar.MINUTE), is(equalTo(0)));
		assertThat(c.get(Calendar.SECOND), is(equalTo(0)));
	}
}
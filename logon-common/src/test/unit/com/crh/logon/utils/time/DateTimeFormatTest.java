/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.Date;
import java.util.Locale;


/**
 * @author AdNovum Informatik AG
 */
public class DateTimeFormatTest {

	// Tue, 28 May 2013 23:22:29 GMT
	// Wed, 29 May 2013 01:22:29 GMT+2 (Europe/Paris)
	private Date testDateTime = new Date(1369783349L * 1000);

	@Test
	public void getDateFormat4JSWithEnglish() {
		String fmt = DateTimeFormat.getDateFormat4JS(Locale.ENGLISH);
		assertThat(fmt, is(equalTo("d.m.y")));
	}

	@Test
	public void getDateFormat4JSWithGerman() {
		String fmt = DateTimeFormat.getDateFormat4JS(Locale.GERMAN);
		assertThat(fmt, is(equalTo("d.m.y")));
	}

	@Test
	public void getDateFormat4JSWithFrench() {
		String fmt = DateTimeFormat.getDateFormat4JS(Locale.FRENCH);
		assertThat(fmt, is(equalTo("d.m.y")));
	}

	@Test
	public void getDateFormat4JSWithSpanish() {
		String fmt = DateTimeFormat.getDateFormat4JS(new Locale("es"));
		assertThat(fmt, is(equalTo("d.m.y")));
	}

	@Test
	public void getDateFormat4JSWithUnsupported() {
		String fmt = DateTimeFormat.getDateFormat4JS(Locale.CHINESE);
		assertThat(fmt, is(equalTo("d.m.y")));
	}

	@Test
	public void getDateTimeFormat4JSWithEnglish() {
		String fmt = DateTimeFormat.getDateTimeFormat4JS(Locale.ENGLISH);
		assertThat(fmt, is(equalTo("d.m.y H:i:s")));
	}

	@Test
	public void getDateTimeFormat4JSWithGerman() {
		String fmt = DateTimeFormat.getDateTimeFormat4JS(Locale.GERMAN);
		assertThat(fmt, is(equalTo("d.m.y H:i:s")));
	}

	@Test
	public void getDateTimeFormat4JSWithFrench() {
		String fmt = DateTimeFormat.getDateTimeFormat4JS(Locale.FRENCH);
		assertThat(fmt, is(equalTo("d.m.y H:i:s")));
	}

	@Test
	public void getDateTimeFormat4JSWithSpanish() {
		String fmt = DateTimeFormat.getDateTimeFormat4JS(new Locale("es"));
		assertThat(fmt, is(equalTo("d.m.y H:i:s")));
	}

	@Test
	public void getDateTimeFormat4JSWithChinese() {
		String fmt = DateTimeFormat.getDateTimeFormat4JS(Locale.CHINESE);
		assertThat(fmt, is(equalTo("d.m.y H:i:s")));
	}

	@Test
	public void asLogonDayMonthYearWithLocaleNull() {
		String fmt = DateTimeFormat.asLogonDayMonthYear(Locale.ENGLISH, null);
		assertThat(fmt, is(equalTo("")));
	}

	@Test
	public void asLogonDayMonthYearWithEnglishDate() {
		String fmt = DateTimeFormat.asLogonDayMonthYear(Locale.ENGLISH, testDateTime);
		assertThat(fmt, is(equalTo("29.05.13")));
	}

	@Test
	public void asLogonDayMonthYearWithFrenchDate() {
		String fmt = DateTimeFormat.asLogonDayMonthYear(Locale.FRENCH, testDateTime);
		assertThat(fmt, is(equalTo("29.05.13")));
	}

	@Test
	public void asLogonDayMonthYearWithNull() {
		String fmt = DateTimeFormat.asLogonDayMonthYear(null);
		assertThat(fmt, is(equalTo("")));
	}

	@Test
	public void asLogonDayMonthYear() {
		String fmt = DateTimeFormat.asLogonDayMonthYear(testDateTime);
		assertThat(fmt, is(equalTo("29.05.13")));
	}

	@Test
	public void asLogonDayMonthWithNull() {
		String fmt = DateTimeFormat.asDayMonth(null);
		assertThat(fmt, is(equalTo("")));
	}

	@Test
	public void asHourMinuteWithNull() {
		String fmt = DateTimeFormat.asHourMinute(null);
		assertThat(fmt, is(equalTo("")));
	}

	@Test
	public void asHourMinute() {
		String fmt = DateTimeFormat.asHourMinute(testDateTime);
		assertThat(fmt, is(equalTo("01:22")));
	}

	@Test
	public void asHourMinuteWithUTC() {
		String fmt = DateTimeFormat.asHourMinute(testDateTime, "UTC");
		assertThat(fmt, is(equalTo("23:22")));
	}

	@Test
	public void asHourMinuteSecondWithNull() {
		String fmt = DateTimeFormat.asHourMinuteSecond(null);
		assertThat(fmt, is(equalTo("")));
	}

	@Test
	public void asHourMinuteSecond() {
		String fmt = DateTimeFormat.asHourMinuteSecond(testDateTime);
		assertThat(fmt, is(equalTo("01:22:29")));
	}

	@Test
	public void forMopWithNull() {
		String fmt = DateTimeFormat.forMop(null);
		assertThat(fmt, is(nullValue()));
	}

	@Test
	public void forMop() {
		String fmt = DateTimeFormat.forMop(testDateTime);
		assertThat(fmt, is(equalTo("2013-05-29T01:22:29.000+02:00")));
	}

	@Test
	public void forPwsWithNull() {
		String fmt = DateTimeFormat.forPws(null);
		assertThat(fmt, is(nullValue()));
	}

	@Test
	public void forPws() {
		String fmt = DateTimeFormat.forPws(testDateTime);
		assertThat(fmt, is(equalTo("2013-05-29T01:22:29")));
	}

	@Test
	public void asLogonDetailedDateTimeWithCET() {
		String expected = DateTimeFormat.asLogonDetailedDateTime(
				testDateTime, Locale.ENGLISH, "Europe/Paris");
		assertThat(expected, is(equalTo("Wed May 29 01:22:29 CEST 2013")));
	}

	@Test
	public void asLogonDetailedDateTimeWithUTC() {
		String formatted = DateTimeFormat.asLogonDetailedDateTime(
				testDateTime, Locale.ENGLISH, "UTC");
		assertThat(formatted, is(equalTo("Tue May 28 23:22:29 UTC 2013")));
	}

	@Test
	public void asLogonDetailedDateTimeWithSpanishCET() {
		String formatted = DateTimeFormat.asLogonDetailedDateTime(
				testDateTime, new Locale("es"), "Europe/Paris");

		// U+00E9 = mié
		assertThat(formatted, is(equalTo("mi\u00e9 may 29 01:22:29 CEST 2013")));
	}

	@Test
	public void asLogonDetailedDateTimeWithSpanishUTC() {
		String formatted = DateTimeFormat.asLogonDetailedDateTime(
				testDateTime, new Locale("es"), "UTC");

		assertThat(formatted, is(equalTo("mar may 28 23:22:29 UTC 2013")));
	}

	@Test
	public void asUTCOffsetWithNonDST() {
		// time instant 0 is the start of the Unix epoch (Jan 1, 1980 UTC) and this corresponds
		// to winter time in the Europe/Paris zone
		String formatted = DateTimeFormat.asUTCOffset(new Date(0), "Europe/Paris");
		assertThat(formatted, is(equalTo("UTC+1")));
	}

	@Test
	public void aUTCOffsetWithDST() {
		String formatted = DateTimeFormat.asUTCOffset(testDateTime, "Europe/Paris");
		assertThat(formatted, is(equalTo("UTC+2")));
	}

	@Test
	public void asUTCOffsetWithMinutes() {
		// Nov 26 2013 18:22:58 GMT+5:30
		Date date = new Date(1385470378L * 1000);

		String formatted = DateTimeFormat.asUTCOffset(date, "Asia/Calcutta");
		assertThat(formatted, is(equalTo("UTC+5:30")));
	}

	@Test
	public void asUTCOffsetWithDoubleDigitHour() {
		// Nov 27 2013 01:52:58 GMT+13:00
		Date date = new Date(1385470378L * 1000);

		String formatted = DateTimeFormat.asUTCOffset(date, "Pacific/Auckland");
		assertThat(formatted, is(equalTo("UTC+13")));
	}
}

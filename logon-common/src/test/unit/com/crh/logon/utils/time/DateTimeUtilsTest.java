/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class DateTimeUtilsTest {

	@Test
	public void areSameTimeZonesWithSameIds() {
		boolean equal = DateTimeUtils.areSameTimeZones("Europe/Paris", "Europe/Paris");
		assertThat(equal, is(true));
	}

	@Test
	public void areSameTimeZoneshDifferentIdsButSameOffets() {
		boolean equal = DateTimeUtils.areSameTimeZones("Europe/Paris", "Europe/Amsterdam");
		assertThat(equal, is(true));
	}

	@Test
	public void areSameTimeZonesWithDifferentIdsAndDifferentOffets() {
		boolean equal = DateTimeUtils.areSameTimeZones("Europe/Paris", "Europe/Moscow");
		assertThat(equal, is(false));
	}

	@Test
	public void formatMinutesSinceMidnightWithNull() {
		String formatted = DateTimeUtils.formatMinutesSinceMidnight(null);
		assertThat(formatted, is(equalTo("")));
	}

	@Test
	public void formatMinutesSinceMidnightWithZero() {
		String formatted = DateTimeUtils.formatMinutesSinceMidnight(0L);
		assertThat(formatted, is(equalTo("00:00")));
	}

	@Test
	public void formatMinutesSinceMidnightWith() {
		String formatted = DateTimeUtils.formatMinutesSinceMidnight(420L);
		assertThat(formatted, is(equalTo("07:00")));
	}

	@Test
	public void dayDifferenceBetweenMondayAndWednesDayShouldBeTwo() {

		Calendar mondayCal = Calendar.getInstance();
		mondayCal.set(Calendar.YEAR, 2014);
		mondayCal.set(Calendar.MONTH, 9);
		mondayCal.set(Calendar.DAY_OF_MONTH, 13);

		Calendar wednesdayCal = Calendar.getInstance();
		wednesdayCal.set(Calendar.YEAR, 2014);
		wednesdayCal.set(Calendar.MONTH, 9);
		wednesdayCal.set(Calendar.DAY_OF_MONTH, 15);

		Date monday = mondayCal.getTime();
		Date wednesday = wednesdayCal.getTime();

		int differenceInDays = DateTimeUtils.getDayDifference(monday, wednesday);
		assertEquals(2, differenceInDays);

	}
	
	@Test
	public void dayDifferenceBetweenVariedPrecisionShouldBeThree() {

		Calendar fromCal = Calendar.getInstance();
		fromCal.set(Calendar.YEAR, 2014);
		fromCal.set(Calendar.MONTH, 6);
		fromCal.set(Calendar.DAY_OF_MONTH, 6);
		fromCal.set(Calendar.HOUR_OF_DAY, 12);
		fromCal.set(Calendar.MINUTE, 24);
		fromCal.set(Calendar.SECOND, 28);

		Calendar toCal = Calendar.getInstance();
		toCal.set(Calendar.YEAR, 2014);
		toCal.set(Calendar.MONTH, 6);
		toCal.set(Calendar.DAY_OF_MONTH, 9);

		Date toDate = toCal.getTime();
		Date fromDate = fromCal.getTime();

		int differenceInDays = DateTimeUtils.getDayDifference(fromDate, toDate);
		assertEquals(3, differenceInDays);

	}

	@Test
	public void getTimePartInMinutesReversalTest(){
		Calendar cal = Calendar.getInstance();
		long timePartInMinutes = DateTimeUtils.getTimePartInMinutes(cal);
		Calendar clone = (Calendar )cal.clone();


		Calendar timePartInMinutesReversal = DateTimeUtils.setTimePartInMinutes(clone, timePartInMinutes);

		assertEquals(timePartInMinutesReversal.get(Calendar.HOUR), cal.get(Calendar.HOUR));
		assertEquals(timePartInMinutesReversal.get(Calendar.MINUTE), cal.get(Calendar.MINUTE));

		assertEquals(timePartInMinutesReversal.get(Calendar.SECOND), 0);
		assertEquals(timePartInMinutesReversal.get(Calendar.MILLISECOND), 0);
	}

	@Test
	public void getTimePartInMinutesReversalTest2(){
		Calendar cal = Calendar.getInstance();
		long timePartInMinutes = 88L;


		Calendar timePartInMinutesReversal = DateTimeUtils.setTimePartInMinutes(cal, timePartInMinutes);

		assertEquals(timePartInMinutesReversal.get(Calendar.HOUR), 1);
		assertEquals(timePartInMinutesReversal.get(Calendar.MINUTE), 28);

		assertEquals(timePartInMinutesReversal.get(Calendar.SECOND), 0);
		assertEquals(timePartInMinutesReversal.get(Calendar.MILLISECOND), 0);
	}


	@Test
	public void getTimePartInMinutesReversalTest3(){
		Calendar cal = Calendar.getInstance();
		long timePartInMinutes = 188L;


		Calendar timePartInMinutesReversal = DateTimeUtils.setTimePartInMinutes(cal, timePartInMinutes);

		assertEquals(timePartInMinutesReversal.get(Calendar.HOUR), 3);
		assertEquals(timePartInMinutesReversal.get(Calendar.MINUTE), 8);

		assertEquals(timePartInMinutesReversal.get(Calendar.SECOND), 0);
		assertEquals(timePartInMinutesReversal.get(Calendar.MILLISECOND), 0);
	}
}

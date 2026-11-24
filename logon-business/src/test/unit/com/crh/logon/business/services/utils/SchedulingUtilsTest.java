/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.builders.CalendarBuilder;
import com.crh.logon.utils.time.DateTimeUtils;

public class SchedulingUtilsTest {

	private static final long TIME_8AM_IN_MINUTE = 8*60;
	private static final long TIME_12PM_IN_MINUTE = 12*60;
	private static final long TIME_1PM_IN_MINUTE = 13*60;
	private static final long TIME_7PM_IN_MINUTE = 19*60;
	
	/**  (TIME_12PM_IN_MINUTE-TIME_8AM_IN_MINUTE)+(TIME_7PM_IN_MINUTE-TIME_1PM_IN_MINUTE)
	 * <p> 
	 * 600 MIN as of now*/
	private static final long MINUTES_IN_wORKING_DAY = (TIME_12PM_IN_MINUTE-TIME_8AM_IN_MINUTE)+(TIME_7PM_IN_MINUTE-TIME_1PM_IN_MINUTE);

	private static final Calendar MARCH30 = new CalendarBuilder().year(2014).month(Calendar.MARCH).day(30).hour(3).minute(29).second(52).build();
	
	private static final int FROM_DATE = 0;
	private static final int TO_DATE = 1;
	private static final int NET_TIME = 2;
	
	private static final TimeZone timeZone = new TimeZone(Calendar.getInstance().getTimeZone().getID(), Locale.ENGLISH);
	private static final ShiptoEntity shipTo = initOpeningTimes();

	private static ShiptoEntity initOpeningTimes() {
		ShiptoEntity entity = new ShiptoEntity();
		entity.setOpen1From(TIME_8AM_IN_MINUTE);
		entity.setOpen1To(TIME_12PM_IN_MINUTE);
		entity.setOpen2From(TIME_1PM_IN_MINUTE);
		entity.setOpen2To(TIME_7PM_IN_MINUTE);

		return entity;
	}

	private static Calendar initCalendarToMarch30() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.DAY_OF_MONTH, 30);

		calendar.set(Calendar.HOUR_OF_DAY, 3);
		calendar.set(Calendar.MINUTE, 29);
		calendar.set(Calendar.SECOND, 52);
		calendar.set(Calendar.MILLISECOND,0);

		return calendar;
	}

	private static Calendar initCalendarToMarch1() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 29);
		calendar.set(Calendar.SECOND, 52);
		calendar.set(Calendar.MILLISECOND,0);

		return calendar;
	}

	/**
	 * Returns the difference between two dates in days
	 * E.g. 2009/03/15 09:00 - 2009/03/14 22:00 = 1
	 * An int is enough for counting about 11 million years worth of days, and also easier to use in test asserts.

	 */
	public static int getDayDiffInt(Calendar from, Calendar to) {
		return DateTimeUtils.getDayDiff(from, to).intValue();
	}

	@Test
	public void metaTestIsBuilderValidMarch1() {
		Calendar march1OldInit = initCalendarToMarch1();
		Calendar march1NewBuilder = new CalendarBuilder().year(2014).month(Calendar.MARCH).day(1).hour(2).minute(29).second(52).ms(0).build();

		long timeDiff = march1OldInit.getTimeInMillis() - march1NewBuilder.getTimeInMillis();
		assertEquals(0, timeDiff);
	}

	@Test
	public void metaTestIsBuilderValidMarch30() {
		Calendar march30OldInit = initCalendarToMarch30();
		Calendar march30NewBuilder = new CalendarBuilder().year(2014).month(Calendar.MARCH).day(30).hour(3).minute(
				29).second(52).build();

		long timeDiff = march30OldInit.getTimeInMillis() - march30NewBuilder.getTimeInMillis();
		assertEquals(0, timeDiff);
	}
	

	private void test(Calendar fromDate, Calendar toDate, int fromDateAdjustment,
			int toDateAdjusment, SalesAreaWebOrderOnWeekendSettings webOrderOnWeekend, long expectedNetTime) {
		SchedulingUtils.CalculatedDeliveryTimes newTimes = SchedulingUtils.calculateDeliveryFromToAndNetTime(
				shipTo, fromDate.getTime(), toDate.getTime(), timeZone, webOrderOnWeekend);

		//Check if Date adjustment happened as expected
		assertEquals(fromDateAdjustment, getDayDiffInt(fromDate, newTimes.getFromDate()));
		assertEquals(toDateAdjusment, getDayDiffInt(toDate, newTimes.getToDate()));
		
		//Expected net time is calculated before each test() call.
		assertEquals(new Long(expectedNetTime), newTimes.getNetTimeInMinutes());
	}

	@Test
	public void OneWeekdayNoAdj() {
		CalendarBuilder builder = new CalendarBuilder();
		Calendar fromDate = builder.year(2014).month(Calendar.NOVEMBER).day(12).hour(1).minute(1).second(1).build();
		Calendar toDate = builder.year(2014).month(Calendar.NOVEMBER).day(12).hour(23).minute(23).second(23).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.DISABLED, 1 * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FourWeekdaysNoAdj() {
		int fullWorkingDaysInTest = 4;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(10).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(13).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.DISABLED, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void SixWeekdaysOneWeekendNoAdj() {
		int fullWorkingDaysInTest = 6;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(12).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(19).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.DISABLED, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromSaturdayToWorkingDayAdjusted() {
		int fullWorkingDaysInTest = 2;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(15).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(18).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 2, 0, SalesAreaWebOrderOnWeekendSettings.DISABLED, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromSundayToWorkingDayAdjusted() {
		int fullWorkingDaysInTest = 2;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(16).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(18).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 1, 0, SalesAreaWebOrderOnWeekendSettings.DISABLED, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromWorkingToSaturdayAdjusted() {
		int fullWorkingDaysInTest = 2;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(13).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(15).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, -1, SalesAreaWebOrderOnWeekendSettings.DISABLED, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromWorkingToSundayAdjusted() {
		int fullWorkingDaysInTest = 2;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(13).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(16).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, -2, SalesAreaWebOrderOnWeekendSettings.DISABLED, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	//==========================================================
	//-------------Saturday is a working day below this line----
	//==========================================================	

	@Test
	public void OneWeekdayNoAdjWorkOnSat() {
		CalendarBuilder builder = new CalendarBuilder();
		Calendar fromDate = builder.year(2014).month(Calendar.NOVEMBER).day(12).hour(1).minute(1).second(1).build();
		Calendar toDate = builder.year(2014).month(Calendar.NOVEMBER).day(12).hour(23).minute(23).second(23).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, 1 * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FourWeekdaysNoAdjWorkOnSatWorkOnSat() {
		int fullWorkingDaysInTest = 4;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(10).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(13).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void SixWeekdaysOneWeekendNoAdjWorkOnSat() {
		int fullWorkingDaysInTest = 7;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(12).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(19).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromSaturdayToWorkingDayAdjustedWorkOnSat() {
		int fullWorkingDaysInTest = 3;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(15).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(18).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromSundayToWorkingDayAdjustedWorkOnSat() {
		int fullWorkingDaysInTest = 2;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(16).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(18).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 1, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromWorkingToSaturdayAdjustedWorkOnSat() {
		int fullWorkingDaysInTest = 3;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(13).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(15).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromWorkingToSundayAdjustedWorkOnSat() {
		int fullWorkingDaysInTest = 3;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(13).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(16).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, -1, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromWorkingSaturdayToWorkingNoAdj() {
		int fullWorkingDaysInTest = 2;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(15).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(17).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void FromWorkingToWorkingSaturdayNoAdj() {
		int fullWorkingDaysInTest = 2;

		Calendar fromDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(14).hour(1).minute(1).second(1).ms(0).build();
		Calendar toDate = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(15).hour(23).minute(23).second(23).ms(0).build();

		test(fromDate, toDate, 0, 0, SalesAreaWebOrderOnWeekendSettings.SATURDAY, fullWorkingDaysInTest * MINUTES_IN_wORKING_DAY);
	}

	@Test
	public void testCalculateDeliveryFromToAndNetTime() {

		Calendar deliveryFrom = initCalendarToMarch1();
		Calendar deliveryTo = initCalendarToMarch30();

		while (deliveryFrom.before(MARCH30)) {

			Object[] oldTimes = oldestCalculateDeliveryFromToAndNetTime(shipTo,
					deliveryFrom.getTime(), deliveryTo.getTime());
			SchedulingUtils.CalculatedDeliveryTimes newTimes = SchedulingUtils.calculateDeliveryFromToAndNetTime(
					shipTo, deliveryFrom.getTime(), deliveryTo.getTime(), timeZone, SalesAreaWebOrderOnWeekendSettings.DISABLED);

			Assert.assertTrue(newTimes.getFromDate().equals(oldTimes[FROM_DATE]));
			Assert.assertTrue(newTimes.getToDate().equals(oldTimes[TO_DATE]));
			Assert.assertTrue(newTimes.getNetTimeInMinutes().equals(oldTimes[NET_TIME]));

			deliveryFrom.add(Calendar.HOUR, 5);
			deliveryTo.add(Calendar.HOUR, 5);
		}
	}

	///-------------------------------------------------------------
	///---Old implementation, for testing purposes only-------------
	///-------------------------------------------------------------


	/**
	 * Oldest implementation of calculateDeliveryFromToAndNetTime. It needs to test the new, refactored method. Logging has been removed because of unit test.
	 * 
	 * @param shiptoEntity
	 * @param deliveryFromDat
	 * @param deliveryToDat
	 * @return
	 */
	public static Object[] oldestCalculateDeliveryFromToAndNetTime(ShiptoEntity shiptoEntity,
			final Date deliveryFromDat, final Date deliveryToDat) {
		Object[] calculationValues = new Object[3];

		Calendar delfrom = null;
		Calendar delto = null;
		Calendar from = null;
		Calendar to = null;
		Calendar deltoclone = null;
		Calendar delfromclone = null;

		if (deliveryFromDat != null) {

			delfrom = Calendar.getInstance();
			delfrom.setTime(deliveryFromDat);

			from = DateTimeUtils.getTimePart(delfrom);

			delfromclone = adjustDeliveryFromToWorkingDay(delfrom);

		}

		if (deliveryToDat != null) {

			delto = Calendar.getInstance();
			delto.setTime(deliveryToDat);
			to = DateTimeUtils.getTimePart(delto);

			deltoclone = adjustDeliveryToToWorkingDay(delto);

		}

		if (delfrom != null && delto != null && shiptoEntity.getOpen1From() != null
				&& shiptoEntity.getOpen2From() != null && shiptoEntity.getOpen1To() != null
				&& shiptoEntity.getOpen2To() != null) {

			//Calendar containing info only about the HOUR + MINUTE 
			final Calendar open1From = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(shiptoEntity.getOpen1From()));
			final Calendar open1To = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(shiptoEntity.getOpen1To()));
			final Calendar open2From = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(shiptoEntity.getOpen2From()));
			final Calendar open2To = DateTimeUtils.getTimePart(DateTimeUtils.getHoursMinutesCal(shiptoEntity.getOpen2To()));

			final boolean isDeliveryFromWeekend = isDateNonWorkingday(delfrom);

			//Step 5: in case it is on weekend, it is not valid, adjust the time window
			if (isDeliveryFromWeekend) {
				from = open1From;
			}

			final boolean isDeliveryToNonWorkingDay = isDateNonWorkingday(delto);

			if (isDeliveryToNonWorkingDay) {
				to = open2To;
			}

			Long diffToSubtractFrom = 0l;
			Long diffToSubtractTo = 0l;

			//Step 1: If from is between an opening interval: Define the amount of minutes which has to be removed later - from will be anyway an open intervals start
			if (from.after(open1From) && from.before(open1To)) {
				long diff = DateTimeUtils.getMinAfterMidnight(from.getTime())
						- DateTimeUtils.getMinAfterMidnight(open1From.getTime());
				diffToSubtractFrom += diff;
				from = open1From;
			}
			else if (from.after(open2From) && from.before(open2To)) {
				long diff = DateTimeUtils.getMinAfterMidnight(from.getTime())
						- DateTimeUtils.getMinAfterMidnight(open2From.getTime());
				diffToSubtractFrom += diff;
				from = open2From;
			}

			//Step 2: If to is between an opening interval: Define the amount of minutes which has to be removed later - to will be anyway an open intervals end
			if (to.after(open1From) && to.before(open1To)) {
				long diff = DateTimeUtils.getMinAfterMidnight(open1To.getTime())
						- DateTimeUtils.getMinAfterMidnight(to.getTime());
				diffToSubtractTo += diff;
				to = open1To;
			}
			else if (to.after(open2From) && to.before(open2To)) {
				long diff = DateTimeUtils.getMinAfterMidnight(open2To.getTime())
						- DateTimeUtils.getMinAfterMidnight(to.getTime());
				diffToSubtractTo += diff;
				to = open2To;
			}
			Long diffToSubtract = diffToSubtractFrom + diffToSubtractTo;

			//Step 3: If from is not between an opening interval the opening hours from will be used 
			if (from.after(open1To) && from.before(open2From)) {
				from = open2From;
			}
			else if (from.after(open2To) || from.before(open1From)) {
				from = open1From;
			}

			//Step 4: If to is not between an opening interval the opening hours to will be used
			if (to.after(open1To) && to.before(open2From)) {
				to = open1To;
			}
			else if (to.after(open2To) || from.before(open1From)) {
				to = open2To;
			}

			//Step 5: Mind the gaps for days inbetween in case there are whole days
			Long wholeDayMinutesToAdd = 0l;

			Long gap1Length = DateTimeUtils.getMinAfterMidnight(open1From.getTime())
					+ ((DateTimeUtils.getMillisBeforeMidnight(open2To) / (60 * 1000)) + 1);
			Long gap2Length = shiptoEntity.getOpen2From() - shiptoEntity.getOpen1To();

			//subtract the not whole day time gap
			Long time = 0l;
			if (from.compareTo(open1From) == 0) {
				Long value = DateTimeUtils.getMinAfterMidnight(to.getTime())
						- DateTimeUtils.getMinAfterMidnight(from.getTime());
				if (to.compareTo(open2To) == 0) {
					value -= gap2Length;
				}
				time = value;
			}
			else if (from.compareTo(open2From) == 0) {
				Long value = 0l;
				if (to.compareTo(open2To) == 0) {
					value = DateTimeUtils.getMinAfterMidnight(to.getTime())
							- DateTimeUtils.getMinAfterMidnight(from.getTime());
				}
				//in case it is open1To there is a full day inbetween, that will be handled below
				time = value;
			}
			final Long dayDiff = DateTimeUtils.getDayDiff(delfromclone, deltoclone);
			//if there are more days inbetween the from and the to, there will be first of all the full net window per day * the days inbetween, BUT weekends do not count
			if (dayDiff >= 1) {
				Long minutesInADay = 60l * 24l;

				//this is the length of the delivery window for a whole day
				Long minutesPerDay = minutesInADay - gap1Length - gap2Length;
				int numberOfWeekends = DateTimeUtils.getWeekendsBetween(delfromclone, deltoclone);

				wholeDayMinutesToAdd = (dayDiff - (2 * numberOfWeekends)) * minutesPerDay;
			}

			time += wholeDayMinutesToAdd;

			//subtract the time what was added when the times were adjusted to valid opening times
			time -= diffToSubtract;

			from.add(Calendar.MINUTE, -diffToSubtractFrom.intValue());
			to.add(Calendar.MINUTE, -diffToSubtractTo.intValue());

			Calendar fromm = DateTimeUtils.setTimePart(delfromclone,
					from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE),
					from.get(Calendar.SECOND), from.get(Calendar.MILLISECOND));
			Calendar too = DateTimeUtils.setTimePart(deltoclone, to.get(Calendar.HOUR_OF_DAY),
					to.get(Calendar.MINUTE), to.get(Calendar.SECOND), to.get(Calendar.MILLISECOND));
			calculationValues[0] = fromm;
			calculationValues[1] = too;
			calculationValues[2] = time;
		}
		else {
			calculationValues[0] = null;
			calculationValues[1] = null;
			calculationValues[2] = null;

			if (from != null && delfromclone != null) {
				Calendar fromm = DateTimeUtils.setTimePart(delfromclone,
						from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE),
						from.get(Calendar.SECOND), from.get(Calendar.MILLISECOND));
				calculationValues[0] = fromm;
			}
			if (to != null && deltoclone != null) {
				Calendar too = DateTimeUtils.setTimePart(deltoclone, to.get(Calendar.HOUR_OF_DAY),
						to.get(Calendar.MINUTE), to.get(Calendar.SECOND),
						to.get(Calendar.MILLISECOND));
				calculationValues[0] = too;
			}

		}

		return calculationValues;
	}

	/**
	 * Adjusts the delivery to to a friday in case it turns out to be a weekend day. Sets to monday if weekend day.
	 * @param delfromclone
	 */
	private static Calendar adjustDeliveryFromToWorkingDay(Calendar delfrom) {
		Calendar delfromclone = (Calendar) delfrom.clone();
		//Step 5: in case it is on weekend, it is not valid, adjust the time window
		if (isDateNonWorkingday(delfromclone)) {
			if (delfromclone.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				delfromclone.add(Calendar.DAY_OF_MONTH, 2);
			}
			else if (delfromclone.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				delfromclone.add(Calendar.DAY_OF_MONTH, 1);
			}
		}

		return delfromclone;
	}

	/**
	 * @param delto
	 * @return
	 */
	private static boolean isDateNonWorkingday(Calendar delto) {
		return delto.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| delto.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	/**
	 * Adjusts the delivery to to a friday in case it turns out to be a weekend day. Sets to friday.
	 * @param delto
	 * @return
	 */
	private static Calendar adjustDeliveryToToWorkingDay(Calendar delto) {

		Calendar deltoclone = (Calendar) delto.clone();

		if (isDateNonWorkingday(deltoclone)) {
			if (deltoclone.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				deltoclone.add(Calendar.DAY_OF_MONTH, -1);
			}
			else if (deltoclone.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				deltoclone.add(Calendar.DAY_OF_MONTH, -2);
			}
		}

		return deltoclone;
	}
}

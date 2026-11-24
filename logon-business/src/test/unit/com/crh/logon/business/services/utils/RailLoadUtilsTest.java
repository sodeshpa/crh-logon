/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * @author AdNovum Informatik AG
 */
public class RailLoadUtilsTest {

	private SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT_YYYY_MM_DD);

	private List<TimeZone> timeZoneList;

	private static final String DATE_2014_02_19_WED = "2014.02.19";

	private static final String DATE_2014_02_20_THU = "2014.02.20";

	private static final String DATE_2014_02_21_FRI = "2014.02.21";

	private static final String DATE_2014_02_24_MON = "2014.02.24";

	private static final String DATE_2014_09_12_FRI = "2014.09.12";

	private static final String DATE_2014_09_13_SAT = "2014.09.13";

	private static final String DATE_2014_09_14_SUN = "2014.09.14";

	private static final String DATE_2014_09_15_MON = "2014.09.15";

	@Before
	public void setup() {
		String[] timezoneStrings = new String[] { 
				"Europe/Sarajevo", "Europe/Pristina",
				"Europe/Ljubljana", "Europe/Vienna", "Europe/Warsaw", "Europe/Sofia",
				"Europe/Prague", "Europe/Zagreb", "Europe/Bucharest", "Europe/Belgrade",
				"Europe/Paris", "Europe/Brussels", "Europe/Luxembourg", "Europe/Amsterdam",
				"Europe/Zurich", "Europe/Rome", "Europe/Berlin", "Europe/Budapest",
				"Europe/Bratislava", "Europe/Madrid", "Europe/Kaliningrad", "Europe/Moscow",

				"Africa/Casablanca",

				"Atlantic/Canary",

				"Asia/Baku", "Asia/Beirut", "Asia/Yekaterinburg", "Asia/Omsk", "Asia/Krasnoyarsk",
				"Asia/Irkutsk", "Asia/Yakutsk", "Asia/Vladivostok", "Asia/Magadan"
				};

		timeZoneList = new ArrayList<TimeZone>();
		for (String timezoneString : timezoneStrings) {
			timeZoneList.add(new TimeZone(timezoneString, Locale.ENGLISH));
		}
	}

	@Test
	public void testIsOrderArrivesInTimeJustInTime() {
		testArriveOnTime(DATE_2014_02_21_FRI, DATE_2014_02_19_WED, DATE_2014_02_24_MON, 1, true);
	}

	@Test
	public void testIsOrderArrivesInTimeTooLate() {
		testArriveOnTime(DATE_2014_02_21_FRI, DATE_2014_02_19_WED, DATE_2014_02_24_MON, 4, false);
	}

	@Test
	public void testIsOrderArrivesInTimeTooEarly() {
		testArriveOnTime(DATE_2014_02_20_THU, DATE_2014_02_24_MON, DATE_2014_02_24_MON, 1, false);
	}

	@Test
	public void deliveryDateFromMondayToMondayLoadedFridayOneDayOffset() {
		testArriveOnTime(DATE_2014_09_12_FRI, DATE_2014_09_15_MON, DATE_2014_09_15_MON, 1, true);
	}

	@Test
	public void deliveryDateFromSundayToMondayLoadedFridayOneDayOffset() {
		testArriveOnTime(DATE_2014_09_12_FRI, DATE_2014_09_14_SUN, DATE_2014_09_15_MON, 1, true);
	}

	@Test
	public void deliveryDateFromSaturdayToMondayLoadedFridayOneDayOffset() {
		testArriveOnTime(DATE_2014_09_12_FRI, DATE_2014_09_13_SAT, DATE_2014_09_15_MON, 1, true);
	}
	
	@Test
	public void deliveryDateFromFridayToMondayLoadedFridayOneDayOffset() {
		testArriveOnTime(DATE_2014_09_12_FRI, DATE_2014_09_12_FRI, DATE_2014_09_15_MON, 1, true);
	}

	@Test
	public void deliveryDateFromMondayToMondayLoadedFridayTwoDaysOffset() {
		testArriveOnTime(DATE_2014_09_12_FRI, DATE_2014_09_15_MON, DATE_2014_09_15_MON, 2, true);
	}
	
	@Test
	public void deliveryDateFromMondayToMondayLoadedFridayThreeDaysOffset() {
		testArriveOnTime(DATE_2014_09_12_FRI, DATE_2014_09_15_MON, DATE_2014_09_15_MON, 3, true);
	}
	
	@Test
	public void deliveryDateFromMondayToMondayLoadedFridayFourDaysOffset() {
		testArriveOnTime(DATE_2014_09_12_FRI, DATE_2014_09_15_MON, DATE_2014_09_15_MON, 4, false);
	}
	

	/**
	 * Test whether an order with the from/to delivery date can arrive on time 
	 * on the specified reference date with the rail offset as travel time
	 * <p>
	 * It takes "all" time zones into account. 
	 * 
	 * @param reference date string
	 * @param from delivery date string
	 * @param to delivery date string
	 * @param railOffset number of days the train takes to arrive
	 * @param expectedResult true/false
	 * @throws AssertionError Date parse exception for the reference, from and to date string or the list of time zone where the expected result didn't matched the actual
	 */
	private void testArriveOnTime(String reference, String from, String to, long railOffset,
			boolean expectedResult) {
		DispoOrderListEntity order = new DispoOrderListEntity();
		order.setArrOffsetDays(railOffset);
		try {
			order.setDeliveryFromDat(sdf.parse(from));
		}
		catch (ParseException e) {
			fail("Cannot parse from date string " + from + "with format string: "
					+ Constant.DATE_FORMAT_YYYY_MM_DD);
		}
		try {
			order.setDeliveryToDat(sdf.parse(to));
		}
		catch (ParseException e) {
			fail("Cannot parse to date string " + to + "with format string: "
					+ Constant.DATE_FORMAT_YYYY_MM_DD);
		}

		Date referenceDate = null;
		try {
			referenceDate = sdf.parse(reference);
		}
		catch (ParseException e) {
			fail("Cannot parse reference date string " + reference + "with format string: "
					+ Constant.DATE_FORMAT_YYYY_MM_DD);
		}
		StringBuilder failedTimeZones = new StringBuilder();
		for (TimeZone userTimeZone : timeZoneList) {
			boolean orderArrivesInTime = RailLoadUtils.isOrderArrivesInTime(order, referenceDate,
					userTimeZone);
			if (orderArrivesInTime != expectedResult) {
				failedTimeZones.append(userTimeZone.getTimezoneCd()).append(", ");
			}
		}
		if (failedTimeZones.length() > 0) {
			fail(String.format(
					"The expected result '%s' were not met for the following timezones: %s",
					expectedResult, failedTimeZones.toString()));
		}
	}
}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.crh.logon.business.services.utils.DeliveryDateUtils;
import com.crh.logon.business.services.utils.SalesAreaWebOrderOnWeekendSettings;
import com.crh.logon.constants.CustorderError;

/**
 * Responsible to test the lead time order creation prevention logic for Web-order tab.
 * 
 * Latest possible delivery time: In case the order's delivery time window's end is on weekend,
 * the validator will overwrite the deliveryTo to be the latest possible minute of the last working day
 * for the later deliveryToTime checks.
 * 
 * @author AdNovum Informatik AG
 */
public class WebDeliveryLeadTimeTest {

	//
	//  |-------------|--------|M----------|---------|
	//  C1            C2       T1          T2
	//                          M(idnight)
	// C1, C2, T1: same day with order creation
	// T2: next day
	
	//constants same as the same as the ones in TLGNR_CONFIG
	private final long defaultAllowSdWebOrderUntilMi = 0;//00:00, C1
	private final long defaultAllowNdWebOrderUntilMi = 960;//16:00, C2
	private final long defaultApplySdLeadtimeChkFromMi = 1439;//23:59, T1
	private final long defaultApplyNdLeadtimeChkUntilMi = 720;//12:00, T2
	
	/**
	 * Case 1: The whole delivery window is fully on weekend days.
	 * 
	 * <br/>orderCreation: 2012. November 19. Monday 08:00
	 * <br/>deliveryFrom: 2012. November 24. Saturday 12:00
	 * <br/>deliveryTo: 2012. November 25. Sunday 17:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_WEEKEND}
	 */
	@Test
	public void testRoadCaseWeekend() {
		GregorianCalendar deliveryFromCalendar = getDay(2012, 10, 24, 10);
		GregorianCalendar deliveryToCalendar = getDay(2012, 10, 25, 17);
		
		SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings = SalesAreaWebOrderOnWeekendSettings.DISABLED;
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkWeekendDelivery(
				salesAreaWebOrderOnWeekendSettings,
				deliveryFromCalendar.getTime(), 
				deliveryToCalendar.getTime());
		
		assertEquals(CustorderError.LEADTIME_WEB_ORDER_WEEKEND, checkLeadTimeError);
	}
	
	/**
	 * Case 2: The whole delivery window is not fully on weekend days.
	 * 
	 * <br/>orderCreation: 2012. November 19. Monday 08:00
	 * <br/>deliveryFrom: 2012. November 24. Saturday 12:00
	 * <br/>deliveryTo: 2012. November 26. Monday 17:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseNotWeekend() {
		GregorianCalendar deliveryFromCalendar = getDay(2012, 10, 24, 10);
		GregorianCalendar deliveryToCalendar = getDay(2012, 10, 26, 17);
		
		SalesAreaWebOrderOnWeekendSettings salesAreaWebOrderOnWeekendSettings = SalesAreaWebOrderOnWeekendSettings.DISABLED;
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkWeekendDelivery(
				salesAreaWebOrderOnWeekendSettings,
				deliveryFromCalendar.getTime(), 
				deliveryToCalendar.getTime());
		
		assertNull(checkLeadTimeError);
	}
	
	////////CASES WITH DEFAULT CONSTANS SETTINGS FROM TLGNR_CONFIG
	/**
	 * case a): deliveryTo is before T1, any order creation date
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 08:00
	 * <br/>deliveryTo: 13 May 2013 Monday 18:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_SD_EARLY}
	 */
	@Test
	public void testRoadCaseDefaultConstantsSameDayEarly() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 8);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 18);
				
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, defaultAllowNdWebOrderUntilMi, defaultApplyNdLeadtimeChkUntilMi, 
				defaultAllowSdWebOrderUntilMi, defaultApplySdLeadtimeChkFromMi);

		assertEquals(CustorderError.LEADTIME_WEB_ORDER_SD_EARLY, checkLeadTimeError);
	}
	
	/**
	 * case a): deliveryTo is before T1, creation date 00:00
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 00:00
	 * <br/>deliveryTo: 13 May 2013 Monday 18:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_SD_EARLY}
	 */
	@Test
	public void testRoadCaseDefaultConstantsSameDayEarly2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 0);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 18);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, defaultAllowNdWebOrderUntilMi, defaultApplyNdLeadtimeChkUntilMi, 
				defaultAllowSdWebOrderUntilMi, defaultApplySdLeadtimeChkFromMi);

		assertEquals(CustorderError.LEADTIME_WEB_ORDER_SD_EARLY, checkLeadTimeError);
	}
	
	/**
	 * case b): deliveryTo = T1, orderCreation >= C1
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 08:00
	 * <br/>deliveryTo: 13 May 2013 Monday 23:59:59.000
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_SD_LATER}
	 */
	@Test
	public void testRoadCaseDefaultConstantsSameDayLater() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 8);

		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 23);
		deliveryToCalendar.set(Calendar.MINUTE, 59);
		deliveryToCalendar.set(Calendar.SECOND, 59);
		deliveryToCalendar.set(Calendar.MILLISECOND, 0);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, defaultAllowNdWebOrderUntilMi, defaultApplyNdLeadtimeChkUntilMi, 
				defaultAllowSdWebOrderUntilMi, defaultApplySdLeadtimeChkFromMi);

		assertEquals(CustorderError.LEADTIME_WEB_ORDER_SD_LATER, checkLeadTimeError);
	}
	
	/**
	 * case c): deliveryTo < T2 next day, orderCreation < C2
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 15:00
	 * <br/>deliveryTo: 14 May 2013 Monday 11:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseDefaultConstantsNextDayEarly() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 15);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 11);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, defaultAllowNdWebOrderUntilMi, defaultApplyNdLeadtimeChkUntilMi, 
				defaultAllowSdWebOrderUntilMi, defaultApplySdLeadtimeChkFromMi);

		assertNull(checkLeadTimeError);
	}

	/**
	 * case c): deliveryTo < T2 next day, orderCreation = C2
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 16:00
	 * <br/>deliveryTo: 14 May 2013 Monday 11:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_ND_EARLY}
	 */
	@Test
	public void testRoadCaseDefaultConstantsNextDayEarly2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 16);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 11);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, defaultAllowNdWebOrderUntilMi, defaultApplyNdLeadtimeChkUntilMi, 
				defaultAllowSdWebOrderUntilMi, defaultApplySdLeadtimeChkFromMi);

		assertEquals(CustorderError.LEADTIME_WEB_ORDER_ND_EARLY, checkLeadTimeError);
	}

	/**
	 * case d): deliveryTo = T2 next day, orderCreation < C2
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 23:00
	 * <br/>deliveryTo: 14 May 2013 Monday 12:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseDefaultConstantsNextDayLater() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 23);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 12);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, defaultAllowNdWebOrderUntilMi, defaultApplyNdLeadtimeChkUntilMi, 
				defaultAllowSdWebOrderUntilMi, defaultApplySdLeadtimeChkFromMi);

		assertNull(checkLeadTimeError);
	}
	
	/**
	 * case d): deliveryTo > T2 next day, orderCreation > C2
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 17:00
	 * <br/>deliveryTo: 14 May 2013 Monday 14:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseDefaultConstantsNextDayLater2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 17);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 14);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, defaultAllowNdWebOrderUntilMi, defaultApplyNdLeadtimeChkUntilMi, 
				defaultAllowSdWebOrderUntilMi, defaultApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}
	
	
	////////CASES WITH SETTINGS, WHICH MAKES SAME DAY DELIVERY POSSIBLE
	//valid settings, which makes same day delivery possible
	private final long customAllowSdWebOrderUntilMi = 480;//08:00, C1
	private final long customAllowNdWebOrderUntilMi = 1020;//17:00, C2
	private final long customApplySdLeadtimeChkFromMi = 1140;//19:00, T1
	private final long customApplyNdLeadtimeChkUntilMi = 780;//13:00, T2

	/**
	 * case a): deliveryTo is before T1, any order creation date
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 08:00
	 * <br/>deliveryTo: 13 May 2013 Monday 18:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_SD_EARLY}
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayEarly() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 8);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 18);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);

		assertEquals(CustorderError.LEADTIME_WEB_ORDER_SD_EARLY, checkLeadTimeError);
	}
	
	/**
	 * case b): deliveryTo = T1, order creation date < C1
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 05:00
	 * <br/>deliveryTo: 13 May 2013 Monday 19:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayLater() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 5);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 19);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);

		assertNull(checkLeadTimeError);
	}
	
	/**
	 * case b): deliveryTo = T1, order creation date = C1
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 08:00
	 * <br/>deliveryTo: 13 May 2013 Monday 19:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_SD_LATER}
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayLater2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 8);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 19);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);

		assertEquals(CustorderError.LEADTIME_WEB_ORDER_SD_LATER, checkLeadTimeError);
	}

	/**
	 * case b): deliveryTo is after T1, order creation date < c1
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 06:00
	 * <br/>deliveryTo: 13 May 2013 Monday 20:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayLater3() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 6);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 20);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}
	
	/**
	 * case c): deliveryTo < T2 next day, order creation date < c2
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 16:00
	 * <br/>deliveryTo: 14 May 2013 Monday 12:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseCustomConstantsNextDayEarly() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 16);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 12);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}

	/**
	 * case c): deliveryTo < T2 next day, order creation date = c2
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 17:00
	 * <br/>deliveryTo: 14 May 2013 Monday 12:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_ND_EARLY}
	 */
	@Test
	public void testRoadCaseCustomConstantsNextDayEarly2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 17);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 12);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);
		
		assertEquals(CustorderError.LEADTIME_WEB_ORDER_ND_EARLY, checkLeadTimeError);
	}
	
	/**
	 * case c): deliveryTo < T2 next day, order creation date > C2
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 18:00
	 * <br/>deliveryTo: 13 May 2013 Monday 12:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_ND_EARLY}
	 */
	@Test
	public void testRoadCaseCustomConstantsNextDayEarly3() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 18);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 12);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);
		
		assertEquals(CustorderError.LEADTIME_WEB_ORDER_ND_EARLY, checkLeadTimeError);
	}
	
	/**
	 * case d): deliveryTo = T2 next day, any order creation date
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 01:00
	 * <br/>deliveryTo: 14 May 2013 Monday 13:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseCustomConstantsNextDayLater() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 1);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 13);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}
	
	/**
	 * case d): deliveryTo > T2 next day, any order creation date
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 00:00
	 * <br/>deliveryTo: 14 May 2013 Monday 16:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseCustomConstantsNextDayLater2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 0);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 16);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, customAllowNdWebOrderUntilMi, customApplyNdLeadtimeChkUntilMi, 
				customAllowSdWebOrderUntilMi, customApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}

	//valid setting: C1 > T1, as orders in the past date can be created
	private final long custom2AllowSdWebOrderUntilMi = 1439;//23:59, C1
	private final long custom2AllowNdWebOrderUntilMi = 1020;//17:00, C2
	private final long custom2ApplySdLeadtimeChkFromMi = 1;//00:01, T1
	private final long custom2ApplyNdLeadtimeChkUntilMi = 780;//13:00, T2

	/**
	 * case b): Valid settings, C1 > T1. The order must be created.
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 23:00
	 * <br/>deliveryTo: 13 May 2013 Monday 01:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayLaterWholeDay() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 23);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 1);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, custom2AllowNdWebOrderUntilMi, custom2ApplyNdLeadtimeChkUntilMi, 
				custom2AllowSdWebOrderUntilMi, custom2ApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}
	
	//valid setting: C1 > T1, as orders in the past date can be created
	private final long custom3AllowSdWebOrderUntilMi = 960;//16:00, C1
	private final long custom3AllowNdWebOrderUntilMi = 1020;//17:00, C2
	private final long custom3ApplySdLeadtimeChkFromMi = 480;//08:00, T1
	private final long custom3ApplyNdLeadtimeChkUntilMi = 780;//13:00, T2

	/**
	 * case b): Valid settings, C1 > T1. The order must be created.
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 15:00
	 * <br/>deliveryTo: 13 May 2013 Monday 10:00
	 * 
	 * <br/>Expected return: null
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayT1LtC1() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 15);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 10);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, custom3AllowNdWebOrderUntilMi, custom3ApplyNdLeadtimeChkUntilMi, 
				custom3AllowSdWebOrderUntilMi, custom3ApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}
	
	/**
	 * case b): deliveryTo > T1 same day, order creation date > C1
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 17:00
	 * <br/>deliveryTo: 13 May 2013 Monday 10:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_SD_LATER}
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayLaterT1LtC1() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 17);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 10);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, custom3AllowNdWebOrderUntilMi, custom3ApplyNdLeadtimeChkUntilMi, 
				custom3AllowSdWebOrderUntilMi, custom3ApplySdLeadtimeChkFromMi);
		
		assertEquals(CustorderError.LEADTIME_WEB_ORDER_SD_LATER, checkLeadTimeError);
	}
	
	//invalid setting: C2 < C1, so there is a time interval between [C1,C2], where order can be created for 
	//the same day, but not for the next day
	private final long invalidAllowSdWebOrderUntilMi = 1020;//17:00, C1
	private final long invalidAllowNdWebOrderUntilMi = 480;//08:00, C2
	private final long invalidApplySdLeadtimeChkFromMi = 1140;//19:00, T1
	private final long invalidApplyNdLeadtimeChkUntilMi = 780;//13:00, T2
	
	/**
	 * case b): deliveryTo is after T1, order creation date < c1
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 12:00
	 * <br/>deliveryTo: 13 May 2013 Monday 20:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_ND_EARLY}
	 */
	@Test
	public void testRoadCaseCustomConstantsSameDayLaterInvalidC1C2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 12);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 13, 20);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, invalidAllowNdWebOrderUntilMi, invalidApplyNdLeadtimeChkUntilMi, 
				invalidAllowSdWebOrderUntilMi, invalidApplySdLeadtimeChkFromMi);
		
		assertNull(checkLeadTimeError);
	}
	
	/**
	 * case c): deliveryTo < T2 next day, order c2 < creation date < c1
	 * 
	 * <br/>orderCreation: 13 May 2013 Monday 12:00
	 * <br/>deliveryTo: 14 May 2013 Monday 08:00
	 * 
	 * <br/>Expected return: {@link CustorderError.LEADTIME_WEB_ORDER_ND_EARLY}
	 */
	@Test
	public void testRoadCaseCustomConstantsNextDayLaterInvalidC1C2() {
		GregorianCalendar orderCreationCalendar = getDay(2013, 04, 13, 12);
		GregorianCalendar deliveryToCalendar = getDay(2013, 04, 14, 8);
		
		CustorderError checkLeadTimeError = DeliveryDateUtils.checkLeadTimes(SalesAreaWebOrderOnWeekendSettings.DISABLED, deliveryToCalendar, 
				orderCreationCalendar, invalidAllowNdWebOrderUntilMi, invalidApplyNdLeadtimeChkUntilMi, 
				invalidAllowSdWebOrderUntilMi, invalidApplySdLeadtimeChkFromMi);
		
		assertEquals(CustorderError.LEADTIME_WEB_ORDER_ND_EARLY, checkLeadTimeError);
	}
	
	private static GregorianCalendar getDay(int year, int monthIndex, int day, int hours) {
		GregorianCalendar resultCalendar = new GregorianCalendar(year, monthIndex, day);
		resultCalendar.set(Calendar.HOUR_OF_DAY, hours);
		return resultCalendar;
	}
}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.constants.CustorderError;
import org.junit.Test;

@AdnTrace
public class DeliveryDistanceValidationUtilsTest {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryDistanceValidationUtilsTest.class);


	public static final Integer OFFSET_SATURDAY_SUNDAY = new Integer(4);
	public static final Integer OFFSET_SATURDAY_4 = new Integer(4);
	public static final Integer OFFSET_SATURDAY_11 = new Integer(11);
	public static final Integer OFFSET_DISABLED_4 = new Integer(4);
	public static final Integer OFFSET_DISABLED_11 = new Integer(11);

	public static final Long H_1_EARLY_ORDER_LIMIT_660 = new Long(660);

	public static final Long H_2_EARLIEST_DELIVERY_FROM_720 = new Long(720);

	@Test
	public void testSanityCheckEmptyEntity() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean = null;
		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertEquals("No error assumed for empty entity!", null, errors);
	}


	@Test
	public void testSanityCheckEmptyEntityEmptyFlag() {

		LeadTimeValidationBaseBean entity = new LeadTimeValidationBaseBean();
		entity.setDbltActiveFlag(Boolean.FALSE);
		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(entity);
		assertEquals("No error assumed for inactive flag!", null, errors);
	}

	@Test
	public void testSanityCheckBasic1() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		leadTimeValidationBaseBean.setDbltActiveFlag(Boolean.TRUE);
		leadTimeValidationBaseBean.setDbltDistanceL2(any(Long.class));
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertNotNull(errors);
		assertEquals("null L1 have to be catched!", CustorderError.LEADTIME_DISTANCE_SANITY_ALL_FIELDS_ARE_MANDATORY, errors
				.get(0));
	}

	@Test
	public void testSanityCheckBasic2() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		leadTimeValidationBaseBean.setDbltActiveFlag(Boolean.TRUE);
		leadTimeValidationBaseBean.setDbltDistanceL1(any(Long.class));
		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertNotNull(errors);
		assertEquals("null L2 have to be catched!", CustorderError.LEADTIME_DISTANCE_SANITY_ALL_FIELDS_ARE_MANDATORY, errors
				.get(0));
	}

	@Test
	public void testSanityCheckBasic3() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		leadTimeValidationBaseBean.setDbltActiveFlag(Boolean.TRUE);
		leadTimeValidationBaseBean.setDbltDistanceL1(any(Long.class));
		leadTimeValidationBaseBean.setDbltDistanceL2(any(Long.class));
		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertNotNull(errors);
		assertEquals("null Dx have to be catched!", CustorderError.LEADTIME_DISTANCE_SANITY_ALL_FIELDS_ARE_MANDATORY, errors
				.get(0));
	}

	@Test
	public void testSanityCheckBasic4() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean =
				getLeadTimeValidationBaseBean(new Long(333), new Long(2));

		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertNotNull(errors);
		assertEquals("Bigger L1 value have to be catched!", CustorderError.LEADTIME_DISTANCE_SANITY_L1_LESS_THAN_L2, errors
				.get(0));
	}

	@Test
	public void testSanityCheckDXIncremental() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean = getLeadTimeValidationBaseBean(new Long(111), new Long(12));

		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertEquals("No error have to be catched!", 0,
				errors.size());
	}

	@Test
	public void testSanityCheckNoBoth() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		leadTimeValidationBaseBean.setDbltActiveFlag(Boolean.TRUE);
		leadTimeValidationBaseBean.setSdltAndNdltActive(Boolean.TRUE);
		leadTimeValidationBaseBean.setDbltDistanceL1(any(Long.class));
		leadTimeValidationBaseBean.setDbltDistanceL2(any(Long.class));

		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertNotNull(errors);
		assertEquals("Error should have been popped up!", CustorderError.LEADTIME_DISTANCE_SANITY_ONLY_ONE,
				errors.get(0));
	}

	@Test
	public void testSanityCheckNoError() {

		LeadTimeValidationBaseBean leadTimeValidationBaseBean =
				getLeadTimeValidationBaseBean(new Long(111), new Long(2));

		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		List<CustorderError> errors = deliveryDistanceValidationUtils.sanityCheck(leadTimeValidationBaseBean);
		assertEquals("No error have to be catched!", 0, errors.size());
	}

	private LeadTimeValidationBaseBean getLeadTimeValidationBaseBean(Long dbltDistanceL1,
			Long dbltOffsetD2) {
		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		leadTimeValidationBaseBean.setDbltActiveFlag(Boolean.TRUE);
		leadTimeValidationBaseBean.setDbltDistanceL1(dbltDistanceL1);
		leadTimeValidationBaseBean.setDbltDistanceL2(new Long(222));

		leadTimeValidationBaseBean.setDbltOffsetD1(new Long(1));
		leadTimeValidationBaseBean.setDbltOffsetD2(dbltOffsetD2);
		leadTimeValidationBaseBean.setDbltOffsetD3(new Long(3));
		leadTimeValidationBaseBean.setDbltOffsetD4(new Long(4));
		leadTimeValidationBaseBean.setDbltOffsetD5(new Long(5));
		leadTimeValidationBaseBean.setDbltOffsetD6(new Long(6));

		leadTimeValidationBaseBean.setH1EarlyOrderLimit(new Long(6));
		leadTimeValidationBaseBean.setH2EarliestDeliveryFrom(new Long(6));
		return leadTimeValidationBaseBean;
	}

	@Test
	public void testAddWeekendDaysSATURDAY_AND_SUNDAY(){
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY, OFFSET_SATURDAY_SUNDAY, OFFSET_SATURDAY_SUNDAY, new
				GregorianCalendar(2018, Calendar.APRIL, 5));// Thursday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY, OFFSET_SATURDAY_SUNDAY, OFFSET_SATURDAY_SUNDAY, new
				GregorianCalendar(2018, Calendar.APRIL, 6)); // Friday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY, OFFSET_SATURDAY_SUNDAY, OFFSET_SATURDAY_SUNDAY, new
				GregorianCalendar(2018, Calendar.APRIL, 7)); // Saturday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY, OFFSET_SATURDAY_SUNDAY, OFFSET_SATURDAY_SUNDAY, new
				GregorianCalendar(2018, Calendar.APRIL, 8)); // Sunday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY, OFFSET_SATURDAY_SUNDAY, OFFSET_SATURDAY_SUNDAY, new
				GregorianCalendar(2018, Calendar.APRIL, 9)); // Monday
	}

	@Test
	public void testAddWeekendDaysSATURDAY(){

			testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_4,
				4,new GregorianCalendar(2018, Calendar.APRIL, 3)); // Tuesday
			testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_4,
				5,new GregorianCalendar(2018, Calendar.APRIL, 4)); // Wednesday
			testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_4,
				5,new GregorianCalendar(2018, Calendar.APRIL, 5));// Thursday
			testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_4,
				5,new GregorianCalendar(2018, Calendar.APRIL, 6)); // Friday
			testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_4,
				5,new GregorianCalendar(2018, Calendar.APRIL, 7)); // Saturday
			testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_4,
				5,new GregorianCalendar(2018, Calendar.APRIL, 8)); // Sunday
			testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_4,
				4,new GregorianCalendar(2018, Calendar.APRIL, 9)); // Monday

		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_11,
				13,new GregorianCalendar(2018, Calendar.APRIL, 3)); // Tuesday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_11,
				13,new GregorianCalendar(2018, Calendar.APRIL, 4)); // Wednesday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_11,
				13,new GregorianCalendar(2018, Calendar.APRIL, 5));// Thursday

		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_11,
				13,new GregorianCalendar(2018, Calendar.APRIL, 6)); // Friday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_11,
				13,new GregorianCalendar(2018, Calendar.APRIL, 7)); // Saturday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_11,
				13,new GregorianCalendar(2018, Calendar.APRIL, 8)); // Sunday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.SATURDAY, OFFSET_SATURDAY_11,
				12,new GregorianCalendar(2018, Calendar.APRIL, 9)); // Monday
	}

	@Test
	public void testAddWeekendDaysDISABLED(){

		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_4,
				6,new GregorianCalendar(2018, Calendar.APRIL, 3)); // Tuesday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_4,
				6,new GregorianCalendar(2018, Calendar.APRIL, 4)); // Wednesday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_4,
				6,new GregorianCalendar(2018, Calendar.APRIL, 5));// Thursday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_4,
				6,new GregorianCalendar(2018, Calendar.APRIL, 6)); // Friday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_4,
				6,new GregorianCalendar(2018, Calendar.APRIL, 7)); // Saturday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_4,
				5,new GregorianCalendar(2018, Calendar.APRIL, 8)); // Sunday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_4,
				4,new GregorianCalendar(2018, Calendar.APRIL, 9)); // Monday

		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_11,
				15,new GregorianCalendar(2018, Calendar.APRIL, 3)); // Tuesday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_11,
				15,new GregorianCalendar(2018, Calendar.APRIL, 4)); // Wednesday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_11,
				15,new GregorianCalendar(2018, Calendar.APRIL, 5));// Thursday

		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_11,
				17,new GregorianCalendar(2018, Calendar.APRIL, 6)); // Friday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_11,
				17,new GregorianCalendar(2018, Calendar.APRIL, 7)); // Saturday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_11,
				16,new GregorianCalendar(2018, Calendar.APRIL, 8)); // Sunday
		testParticularDateShift(SalesAreaWebOrderOnWeekendSettings.DISABLED, OFFSET_DISABLED_11,
				15,new GregorianCalendar(2018, Calendar.APRIL, 9)); // Monday
	}



	private void testParticularDateShift(SalesAreaWebOrderOnWeekendSettings weekendSetting, int originalOffset, int
			expectedOffset, Calendar cal) {

		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		int offsetWeekendDaysConsidered = deliveryDistanceValidationUtils.addWeekendDaysToOffset(cal, originalOffset, weekendSetting);

		assertEquals("Offset wrongly calculated! cal="+cal.toString()+
						" originalOffset="+originalOffset+ " weekendSetting="+weekendSetting,
				expectedOffset,
				offsetWeekendDaysConsidered);
	}

	@Test
	public void testCalculateEarliestPossibleDelivery_BEFORE_H1_ERROR(){
		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		SalesAreaWebOrderOnWeekendSettings weekendSettings = SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY;

		leadTimeValidationBaseBean.setH1EarlyOrderLimit(H_1_EARLY_ORDER_LIMIT_660); 		// 11:00 AM
		leadTimeValidationBaseBean.setH2EarliestDeliveryFrom(H_2_EARLIEST_DELIVERY_FROM_720); 	// 12:00 AM
		leadTimeValidationBaseBean.setAllowWebOrderOnWeekend(new Long(weekendSettings.getNumberOfDaysOnWeekend()));


		Calendar processingTimeCal = Calendar.getInstance();
		processingTimeCal.set(2018, Calendar.APRIL, 9, 8, 11, 12); // 8:11AM = 491 (processingTimeInMinutes)
		Date processingTime = processingTimeCal.getTime();

		Calendar deliveryFromCal = Calendar.getInstance();
		deliveryFromCal.set(2018, Calendar.APRIL, 11, 8, 11, 12);
		Date deliveryFromDate = deliveryFromCal.getTime();

		Long offsetBeforeH1 = new Long(3);
		Long offsetAfterH1  = any(Long.class);
		DistanceValidationInfoRow distanceValidationInfoRow = new DistanceValidationInfoRow();
		DistanceValidationInfoBean distanceValidationInfoBean = new DistanceValidationInfoBean();


		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		CustorderError error = deliveryDistanceValidationUtils.calculateEarliestPossibleDelivery(
				leadTimeValidationBaseBean,
				processingTime,
				deliveryFromDate,
				offsetBeforeH1,
				offsetAfterH1,
				true, distanceValidationInfoRow, distanceValidationInfoBean);

		checkEarliestPossibleDeliveryFromDat(distanceValidationInfoBean, "12.04.18");

		assertEquals("Error need to be exist!", CustorderError.LEADTIME_DISTANCE_ERROR,
					error);
	}

	private void checkEarliestPossibleDeliveryFromDat(DistanceValidationInfoBean distanceValidationInfoBean, String expected) {
		Date earliestPossibleDeliveryFromDat = distanceValidationInfoBean.getEarliestPossibleDeliveryFromDate();
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
		String earliestPossibleDeliveryFromDatString = dateFormat.format(earliestPossibleDeliveryFromDat);
		assertEquals("Incorrect earliestPossibleDeliveryFromDat!", expected,
				earliestPossibleDeliveryFromDatString);
	}

	@Test
	public void testCalculateEarliestPossibleDelivery_BEFORE_H1_NO_ERROR(){
		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		SalesAreaWebOrderOnWeekendSettings weekendSettings = SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY;

		leadTimeValidationBaseBean.setH1EarlyOrderLimit(H_1_EARLY_ORDER_LIMIT_660); 		// 11:00 AM
		leadTimeValidationBaseBean.setH2EarliestDeliveryFrom(H_2_EARLIEST_DELIVERY_FROM_720); 	// 12:00 AM
		leadTimeValidationBaseBean.setAllowWebOrderOnWeekend(new Long(weekendSettings.getNumberOfDaysOnWeekend()));


		Calendar processingTimeCal = Calendar.getInstance();
		processingTimeCal.set(2018, Calendar.APRIL, 9, 8, 11, 12); // 8:11AM = 491 (processingTimeInMinutes)
		Date processingTime = processingTimeCal.getTime();

		Calendar deliveryFromCal = Calendar.getInstance();
		deliveryFromCal.set(2018, Calendar.APRIL, 13, 8, 11, 12);
		Date deliveryFromDate = deliveryFromCal.getTime();

		Long offsetBeforeH1 = new Long(3);
		Long offsetAfterH1  =  any(Long.class);
		DistanceValidationInfoRow distanceValidationInfoRow = new DistanceValidationInfoRow();
		DistanceValidationInfoBean distanceValidationInfoBean = new DistanceValidationInfoBean();

		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		CustorderError error = deliveryDistanceValidationUtils.calculateEarliestPossibleDelivery(
				leadTimeValidationBaseBean,
				processingTime,
				deliveryFromDate,
				offsetBeforeH1,
				offsetAfterH1,
				true, distanceValidationInfoRow, distanceValidationInfoBean);

		checkEarliestPossibleDeliveryFromDat(distanceValidationInfoBean, "12.04.18");

		assertEquals("Error should not exist!", null,
				error);
	}

	@Test
	public void testCalculateEarliestPossibleDelivery_AFTER_H1_ERROR(){
		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		SalesAreaWebOrderOnWeekendSettings weekendSettings = SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY;

		leadTimeValidationBaseBean.setH1EarlyOrderLimit(H_1_EARLY_ORDER_LIMIT_660); 		// 11:00 AM
		leadTimeValidationBaseBean.setH2EarliestDeliveryFrom(H_2_EARLIEST_DELIVERY_FROM_720); 	// 12:00 AM
		leadTimeValidationBaseBean.setAllowWebOrderOnWeekend(new Long(weekendSettings.getNumberOfDaysOnWeekend()));


		Calendar processingTimeCal = Calendar.getInstance();
		processingTimeCal.set(2018, Calendar.APRIL, 9, 11, 10, 12); // 11:10AM = 670 (processingTimeInMinutes)
		Date processingTime = processingTimeCal.getTime();

		Calendar deliveryFromCal = Calendar.getInstance();
		deliveryFromCal.set(2018, Calendar.APRIL, 13, 8, 11, 12);
		Date deliveryFromDate = deliveryFromCal.getTime();

		Long offsetBeforeH1 = any(Long.class);
		Long offsetAfterH1  = new Long(4);
		DistanceValidationInfoRow distanceValidationInfoRow = new DistanceValidationInfoRow();
		DistanceValidationInfoBean distanceValidationInfoBean = new DistanceValidationInfoBean();



		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		CustorderError error = deliveryDistanceValidationUtils.calculateEarliestPossibleDelivery(
				leadTimeValidationBaseBean,
				processingTime,
				deliveryFromDate,
				offsetBeforeH1,
				offsetAfterH1,
				true, distanceValidationInfoRow, distanceValidationInfoBean);

		checkEarliestPossibleDeliveryFromDat(distanceValidationInfoBean, "13.04.18");

		assertEquals("Error need to be exist!", CustorderError.LEADTIME_DISTANCE_ERROR,
				error);
	}

	@Test
	public void testCalculateEarliestPossibleDelivery_AFTER_H1_NO_ERROR(){
		LeadTimeValidationBaseBean leadTimeValidationBaseBean = new LeadTimeValidationBaseBean();
		SalesAreaWebOrderOnWeekendSettings weekendSettings = SalesAreaWebOrderOnWeekendSettings.SATURDAY_AND_SUNDAY;

		leadTimeValidationBaseBean.setH1EarlyOrderLimit(H_1_EARLY_ORDER_LIMIT_660); 		// 11:00 AM
		leadTimeValidationBaseBean.setH2EarliestDeliveryFrom(H_2_EARLIEST_DELIVERY_FROM_720); 	// 12:00 AM
		leadTimeValidationBaseBean.setAllowWebOrderOnWeekend(new Long(weekendSettings.getNumberOfDaysOnWeekend()));

		Calendar processingTimeCal = Calendar.getInstance();
		processingTimeCal.set(2018, Calendar.APRIL, 9, 11, 10, 12); // 11:10AM = 670 (processingTimeInMinutes)
		Date processingTime = processingTimeCal.getTime();

		Calendar deliveryFromCal = Calendar.getInstance();
		deliveryFromCal.set(2018, Calendar.APRIL, 13, 12, 30, 12);
		Date deliveryFromDate = deliveryFromCal.getTime();

		Long offsetBeforeH1 = any(Long.class);
		Long offsetAfterH1  = new Long(4);
		DistanceValidationInfoRow distanceValidationInfoRow = new DistanceValidationInfoRow();
		DistanceValidationInfoBean distanceValidationInfoBean = new DistanceValidationInfoBean();


		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
		CustorderError error = deliveryDistanceValidationUtils.calculateEarliestPossibleDelivery(
				leadTimeValidationBaseBean,
				processingTime,
				deliveryFromDate,
				offsetBeforeH1,
				offsetAfterH1,
				true, distanceValidationInfoRow, distanceValidationInfoBean);

		checkEarliestPossibleDeliveryFromDat(distanceValidationInfoBean, "13.04.18");

			assertEquals("Error should not exist!", null,
					error);
	}

	@Test
	public void testGetTimeStringFromLong(){

		Long timePartInMinutes = new Long(780l);

		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();

		String timeStringFromLong = deliveryDistanceValidationUtils.getTimeStringFromLong(timePartInMinutes);

		assertEquals("Wrong time format!", "13:00",
				timeStringFromLong);

	}

	@Test
	public void testCheckOffsetsAreIncremental(){
		List<CustorderError> errors = null;
		DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();

		errors = new ArrayList<CustorderError>();
		Long dbltOffsetD1 = new Long(1);
		Long dbltOffsetD2 = new Long(2);
		Long dbltOffsetD3 = new Long(3);
		Long dbltOffsetD4 = new Long(4);
		Long dbltOffsetD5 = new Long(5);
		Long dbltOffsetD6 = new Long(6);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);
		assertEquals("No error have to be catched!", 0, errors.size());

		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = null;
		dbltOffsetD2 = null;
		dbltOffsetD3 = null;
		dbltOffsetD4 = null;
		dbltOffsetD5 = null;
		dbltOffsetD6 = null;

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);
		assertEquals("No error have to be catched!", 0, errors.size());

		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = new Long(5);
		dbltOffsetD2 = null;
		dbltOffsetD3 = null;
		dbltOffsetD4 = null;
		dbltOffsetD5 = null;
		dbltOffsetD6 = new Long(1);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);

		assertEquals("No error have to be catched!", 0, errors.size());

		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = null;
		dbltOffsetD2 = null;
		dbltOffsetD3 = new Long(5);
		dbltOffsetD4 = null;
		dbltOffsetD5 = null;
		dbltOffsetD6 = new Long(1);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);

		assertEquals("No error have to be catched!", 0, errors.size());

		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = null;
		dbltOffsetD2 = null;
		dbltOffsetD3 = new Long(5);
		dbltOffsetD4 = null;
		dbltOffsetD5 = null;
		dbltOffsetD6 = new Long(5);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);

		assertEquals("No error have to be catched!", 0, errors.size());

		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = null;
		dbltOffsetD2 = null;
		dbltOffsetD3 = new Long(5);
		dbltOffsetD4 = null;
		dbltOffsetD5 = new Long(2);
		dbltOffsetD6 = new Long(3);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);

		assertEquals("No error have to be catched!", 0, errors.size());


		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = new Long(1);
		dbltOffsetD2 = new Long(2);
		dbltOffsetD3 = new Long(8);
		dbltOffsetD4 = new Long(4);
		dbltOffsetD5 = new Long(5);
		dbltOffsetD6 = new Long(6);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);
		assertEquals("Error have to be catched!", CustorderError.LEADTIME_DISTANCE_SANITY_DX_VALUES_INCREMENTAL, errors.get(0));

		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = new Long(5);
		dbltOffsetD2 = new Long(6);
		dbltOffsetD3 = null;
		dbltOffsetD4 = null;
		dbltOffsetD5 = new Long(1);
		dbltOffsetD6 = new Long(2);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);
		assertEquals("No error have to be catched!", 0, errors.size());


		errors = new ArrayList<CustorderError>();
		dbltOffsetD1 = new Long(5);
		dbltOffsetD2 = new Long(6);
		dbltOffsetD3 = new Long(3);
		dbltOffsetD4 = new Long(4);
		dbltOffsetD5 = new Long(1);
		dbltOffsetD6 = new Long(2);

		deliveryDistanceValidationUtils.checkOffsetsAreIncremental(errors, dbltOffsetD1,
				dbltOffsetD2, dbltOffsetD3, dbltOffsetD4, dbltOffsetD5, dbltOffsetD6);
		assertEquals("No error have to be catched!", 0, errors.size());
	}


}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.crh.logon.constants.Constant;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * @author AdNovum Informatik AG
 */
public class IntervalTest {

	private static final int EXPECTED_5_RESULT = 5;

	private static final int EXPECTED_2_RESULT = 2;

	private static final int EXPECTED_1_RESULT = 1;

	private static final int EXPECTED_0_RESULT = 0;

	private static final String DATE_2014_02_13 = "2014.02.13";

	private static final String DATE_2014_02_20 = "2014.02.20";

	private static final String DATE_2014_02_21 = "2014.02.21";

	private static final String DATE_2014_02_22 = "2014.02.22";

	private static final String DATE_2014_02_24 = "2014.02.24";

	private static final String DATE_2014_03_01 = "2014.03.01";

	private SimpleDateFormat sdf;

	// 9[_X__]12
	@Test
	public void testIntervalInsisdeInterval() {

		Calendar outernFrom = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar outernTo = AdjustTimesUtils.getCalendarFromTime(12, 0);

		Calendar innerFrom = AdjustTimesUtils.getCalendarFromTime(9, 30);
		Calendar innerTo = AdjustTimesUtils.getCalendarFromTime(10, 0);
		
		Interval innerInterval = new Interval(innerFrom, innerTo);
		Interval outerntInterval = new Interval(outernFrom, outernTo);

		Assert.assertTrue(innerInterval.inside(outerntInterval));

	}
	
	@Test
	public void testIntervalInsisdeIntervalOnTime() {

		Calendar outernFrom = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar outernTo = AdjustTimesUtils.getCalendarFromTime(12, 0);

		Calendar innerFrom = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar innerTo = AdjustTimesUtils.getCalendarFromTime(10, 0);
		
		Interval innerInterval = new Interval(innerFrom, innerTo);
		Interval outerntInterval = new Interval(outernFrom, outernTo);

		Assert.assertTrue(innerInterval.inside(outerntInterval));

	}

	// 9[_XX]X12
	@Test
	public void testIntervalNotInsisdeInterval() {

		Calendar outernFrom = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar outernTo = AdjustTimesUtils.getCalendarFromTime(12, 0);

		Calendar innerFrom = AdjustTimesUtils.getCalendarFromTime(9, 30);
		Calendar innerTo = AdjustTimesUtils.getCalendarFromTime(12, 30);

		Interval innerInterval = new Interval(innerFrom, innerTo);
		Interval outerntInterval = new Interval(outernFrom, outernTo);

		Assert.assertFalse(innerInterval.inside(outerntInterval));

	}

	// 9[_X__]12
	@Test
	public void testGetResidualIntervals() {

		Calendar outernFrom = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar outernTo = AdjustTimesUtils.getCalendarFromTime(12, 0);

		Calendar innerFrom = AdjustTimesUtils.getCalendarFromTime(9, 30);
		Calendar innerTo = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval innerInterval = new Interval(innerFrom, innerTo);
		Interval outerntInterval = new Interval(outernFrom, outernTo);

		Interval[] residualIntervals = outerntInterval.substract(innerInterval);

		Assert.assertEquals(2, residualIntervals.length);

		Interval beforeInterval = residualIntervals[0];
		Interval afterInterval = residualIntervals[1];

		Calendar expectedBeforeFrom = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar expectedBeforeTo = AdjustTimesUtils.getCalendarFromTime(9, 30);

		Interval expectedBeforeInterval = new Interval(expectedBeforeFrom, expectedBeforeTo);

		Assert.assertEquals(expectedBeforeInterval, beforeInterval);

		Calendar expectedAfterFrom = AdjustTimesUtils.getCalendarFromTime(10, 0);
		Calendar expectedAfterTo = AdjustTimesUtils.getCalendarFromTime(12, 0);

		Interval expectedAfterInterval = new Interval(expectedAfterFrom, expectedAfterTo);

		Assert.assertEquals(expectedAfterInterval, afterInterval);

	}

	// 9[_X__]12
	@Test(expected = RuntimeException.class)
	public void testGetNonInsisdeResidualIntervals() {

		Calendar outernFrom = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar outernTo = AdjustTimesUtils.getCalendarFromTime(12, 0);

		Calendar innerFrom = AdjustTimesUtils.getCalendarFromTime(8, 30);
		Calendar innerTo = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval innerInterval = new Interval(innerFrom, innerTo);
		Interval outerntInterval = new Interval(outernFrom, outernTo);

		outerntInterval.substract(innerInterval);

	}

	@Test
	public void testShorter() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(12, 00);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(12, 30);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(12, 30);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(12, 59);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval2.shorterThan(interval1));

	}
	
	@Test
	public void testShorterOnTime() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(12, 00);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(12, 00);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(12, 30);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(12, 59);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.shorterThan(interval2));

	}
	
	@Test
	public void testStartsBefore() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(10, 30);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(9, 30);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.startsBefore(interval2.getFrom()));

	}
	
	@Test
	public void testFinishesBefore() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(9, 45);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(8, 45);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.finishesBefore(interval2.getTo()));

	}
	
	@Test
	public void testFinishesAfter() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(9, 45);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(8, 45);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.finishesAfter(interval2.getFrom()));

	}
	
	@Test
	public void testStartsAfter() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(9, 45);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(8, 45);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.startsAfter(interval2.getFrom()));

	}
	
	@Test
	public void testIntersectsBefore() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(8, 45);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(9, 45);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.intersectsBefore(interval2));
		
	}
	
	@Test
	public void testIntersectsBeforeOnTime() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(9, 45);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.intersectsBefore(interval2));
		
	}
	
	@Test
	public void testBeforeIsNotIntersect() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(8, 45);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(8, 59);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertFalse(interval1.intersectsBefore(interval2));
		
	}
	
	@Test
	public void testIntersectsAfter() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(9, 10);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(10, 10);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);

		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertTrue(interval1.intersectsAfter(interval2));
		
	}
	
	@Test
	public void testAfterIsNotIntersect() {

		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(10, 10);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(10, 50);

		Calendar c2From = AdjustTimesUtils.getCalendarFromTime(9, 0);
		Calendar c2To = AdjustTimesUtils.getCalendarFromTime(10, 0);
		
		Interval interval1 = new Interval(c1From, c1To);
		Interval interval2 = new Interval(c2From, c2To);

		Assert.assertFalse(interval1.intersectsAfter(interval2));
		
	}
	
	@Test
	public void testContains() {
		
		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(10, 10);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(10, 50);
		
		Interval interval = new Interval(c1From, c1To);
		
		Calendar inTheMiddleOfTheNight = AdjustTimesUtils.getCalendarFromTime(10, 30);
		Assert.assertTrue(interval.contains(inTheMiddleOfTheNight));
		
		Calendar beforeDawn = AdjustTimesUtils.getCalendarFromTime(10, 0);
		Assert.assertFalse(interval.contains(beforeDawn));
		
		Calendar outSider = AdjustTimesUtils.getCalendarFromTime(10, 50);
		Assert.assertFalse(interval.contains(outSider));
		
	}
	
	@Test
	public void testLengthInMillis() {
		
		Calendar c1From = AdjustTimesUtils.getCalendarFromTime(10, 10);
		Calendar c1To = AdjustTimesUtils.getCalendarFromTime(10, 12);
		
		Interval interval = new Interval(c1From, c1To);
		
		Assert.assertEquals(120000, interval.lengthInMillis());
		
	}
	
	@Test
	public void testGetCountOfWeekendDaysIncluded() throws ParseException {
		sdf = new SimpleDateFormat(Constant.DATE_FORMAT_YYYY_MM_DD);
		doGetCountOfWeekendDaysIncludedTest(DATE_2014_02_20, DATE_2014_02_21,
				EXPECTED_0_RESULT);
		doGetCountOfWeekendDaysIncludedTest(DATE_2014_02_20, DATE_2014_02_22,
				EXPECTED_1_RESULT);
		doGetCountOfWeekendDaysIncludedTest(DATE_2014_02_20, DATE_2014_02_24,
				EXPECTED_2_RESULT);
		doGetCountOfWeekendDaysIncludedTest(DATE_2014_02_13, DATE_2014_03_01,
				EXPECTED_5_RESULT);
	}

	/**
	 * Does one test with given dates and expected result.
	 * 
	 * @param fromDateAsText
	 * @param toDateAsText
	 * @param expectedResult
	 * @throws ParseException
	 */
	private void doGetCountOfWeekendDaysIncludedTest(String fromDateAsText, String toDateAsText,
			int expectedResult)
			throws ParseException {
		Date fromDate = sdf.parse(fromDateAsText);
		Date toDate = sdf.parse(toDateAsText);
		Calendar from = Calendar.getInstance();
		from.setTime(fromDate);
		Calendar to = Calendar.getInstance();
		to.setTime(toDate);
		Interval interval = new Interval(from, to);
		int weekendDaysIncluded = interval.getCountOfWeekendDaysIncluded();
		Assert.assertEquals(expectedResult, weekendDaysIncluded);
	}

}
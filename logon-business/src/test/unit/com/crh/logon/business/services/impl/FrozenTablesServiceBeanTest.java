/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.builders.CalendarBuilder;
import com.crh.logon.utils.ConfigBean;

public class FrozenTablesServiceBeanTest {

	static final int MINUTES_IN_DAY = 60 * 24;

	MockFrozenTablesServiceBean service = new MockFrozenTablesServiceBean();

	@Test
	public void testWithoutPdaWithPreLoading() {
		// loadTs + transportDuration + tolerance(300min) is still earlier than actual delivery date
		// so we expect null
		Date loadedTs = cal2014Nov().day(1).hour(0).minute(0).buildDate();
		Date actDelivDate = cal2014Nov().day(1).hour(6).minute(0).buildDate();

		Date result = service.getArrivalTime(actDelivDate, null, loadedTs, 60);
		assertNull(result);
	}

	@Test
	public void testWithoutPdaNoPreLoading() {
		// loadTs + transportDuration + tolerance(300) is later than actual delivery date
		Date loadedTs = cal2014Nov().day(1).hour(0).minute(0).buildDate();
		Date actDelivDate = cal2014Nov().day(1).hour(6).minute(0).buildDate();

		Date result = service.getArrivalTime(actDelivDate, null, loadedTs, 61);
		assertEquals(cal2014Nov().day(1).hour(0).minute(61).buildDate(), result);
	}

	@Test
	public void testWithPda() {
		Date actDelivDate = cal2014Nov().day(1).hour(1).minute(0).buildDate();

		Date bizEnterStTs = cal2014Nov().day(1).hour(1).minute(1).buildDate();

		Date result = service.getArrivalTime(actDelivDate, bizEnterStTs, null, 59);
		assertEquals(bizEnterStTs, result);
	}

	@Test
	public void testWithoutPadNoLoading() {
		Date actDelivDate = cal2014Nov().day(1).hour(1).minute(0).buildDate();

		Date result = service.getArrivalTime(actDelivDate, null, null, 59);
		assertEquals(actDelivDate, result);
	}

	@Test
	public void nullParam() {
		Date from = cal2014Nov().buildDate();
		Date to = cal2014Nov().buildDate();
		Date arrival = cal2014Nov().buildDate();

		Boolean result = service.deliveredEarlyOrLate(null, to, arrival);
		assertNull(result);

		result = service.deliveredEarlyOrLate(from, null, arrival);
		assertNull(result);

		result = service.deliveredEarlyOrLate(from, to, null);
		assertNull(result);
	}

	@Test
	public void deliveredJustOnTime() {
		service.setTolerance(0, 0);

		Date from = cal2014Nov().buildDate();
		Date to = cal2014Nov().buildDate();
		Date arrival = cal2014Nov().buildDate();
		Boolean result = service.deliveredEarlyOrLate(from, to, arrival);

		assertFalse(result.booleanValue());
	}

	@Test
	public void deliveredLate() {
		service.setTolerance(0, 0);

		Date from = cal2014Nov().buildDate();
		Date to = cal2014Nov().buildDate();
		Date arrival = cal2014Nov().day(2).buildDate();
		Boolean result = service.deliveredEarlyOrLate(from, to, arrival);

		assertTrue(result.booleanValue());
	}

	@Test
	public void deliveredEarly() {
		service.setTolerance(0, 0);

		Date from = cal2014Nov().buildDate();
		Date to = cal2014Nov().buildDate();
		Date arrival = cal2014Nov().month(Calendar.OCTOBER).day(1).buildDate();
		Boolean result = service.deliveredEarlyOrLate(from, to, arrival);

		assertTrue(result.booleanValue());
	}

	@Test
	public void deliveredEarlyBeyondTolerance() {
		service.setTolerance(MINUTES_IN_DAY - 1, 0);

		Date from = cal2014Nov().day(2).buildDate();
		Date to = cal2014Nov().day(3).buildDate();
		Date arrival = cal2014Nov().day(1).buildDate();
		Boolean result = service.deliveredEarlyOrLate(from, to, arrival);

		assertTrue(result.booleanValue());
	}

	@Test
	public void deliveredOnTimeWithinFromTolerance() {
		service.setTolerance(MINUTES_IN_DAY + 1, 0);

		Date from = cal2014Nov().day(2).buildDate();
		Date to = cal2014Nov().day(3).buildDate();
		Date arrival = cal2014Nov().day(1).buildDate();
		Boolean result = service.deliveredEarlyOrLate(from, to, arrival);

		assertFalse(result.booleanValue());
	}

	@Test
	public void deliveredLateBeyondTolerance() {
		service.setTolerance(0, MINUTES_IN_DAY - 1);

		Date from = cal2014Nov().day(2).buildDate();
		Date to = cal2014Nov().day(3).buildDate();
		Date arrival = cal2014Nov().day(4).buildDate();
		Boolean result = service.deliveredEarlyOrLate(from, to, arrival);

		assertTrue(result.booleanValue());
	}

	@Test
	public void deliveredOnTimeWithinToTolerance() {
		service.setTolerance(0, MINUTES_IN_DAY + 1);

		Date from = cal2014Nov().day(2).buildDate();
		Date to = cal2014Nov().day(3).buildDate();
		Date arrival = cal2014Nov().day(4).buildDate();
		Boolean result = service.deliveredEarlyOrLate(from, to, arrival);

		assertFalse(result.booleanValue());
	}

	@Test
	public void testCalendarAddMinuteDayChange() {
		// wasn't sure what happens when we add so many minutes to a Calendar that should skip to next/previous day. so made a test for it :)

		Calendar cal = new CalendarBuilder().year(2014).month(Calendar.NOVEMBER).day(1).hour(0).minute(
				1).build();

		int minutesInADay = 24 * 60;

		assertEquals(cal.get(Calendar.DAY_OF_MONTH), 1);
		cal.add(Calendar.MINUTE, minutesInADay);
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), 2);
		cal.add(Calendar.MINUTE, -1 * minutesInADay);
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), 1);
	}

	/** Returns new CalendarBuilder. Used to make test cases shorter */
	private static CalendarBuilder calendar() {
		return new CalendarBuilder();
	}

	/** Returns CalendarBuilder with year 2014 and month November set. Used to make test cases shorter */
	private static CalendarBuilder cal2014Nov() {
		return calendar().year(2014).month(Calendar.NOVEMBER);
	}

	/** Override some methods to be able to test the Frozen Tables Service */
	private static class MockFrozenTablesServiceBean extends FrozenTablesServiceBean {
		private ConfigBean configBean;

		public MockFrozenTablesServiceBean() {
			this.configBean = new MockConfigBean(0, 0);
		}

		/** for testing */
		public void setTolerance(int from, int to) {
			this.configBean.setAppDefaultDeliveryToleranceFrom(from);
			this.configBean.setAppDefaultDeliveryToleranceTo(to);
		}

		@Override
		protected ConfigBean getConfigBean() {
			return configBean;
		}
	}

	/** We don't need the whole config bean stuff, just these properties for the test */
	private static class MockConfigBean extends ConfigBean {
		private static final long serialVersionUID = 1L;

		private int toleranceFrom;

		private int toleranceTo;

		public MockConfigBean(int toleranceFrom, int toleranceTo) {
			this.toleranceFrom = toleranceFrom;
			this.toleranceTo = toleranceTo;
		}

		@Override
		public void setAppDefaultDeliveryToleranceFrom(int toleranceFrom) {
			this.toleranceFrom = toleranceFrom;
		}

		@Override
		public int getAppDefaultDeliveryToleranceFrom() {
			return toleranceFrom;
		}

		@Override
		public void setAppDefaultDeliveryToleranceTo(int toleranceTo) {
			this.toleranceTo = toleranceTo;
		}

		@Override
		public int getAppDefaultDeliveryToleranceTo() {
			return toleranceTo;
		}
	}
}

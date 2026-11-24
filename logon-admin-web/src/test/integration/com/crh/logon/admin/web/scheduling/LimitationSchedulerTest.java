/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;



public class LimitationSchedulerTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/LimitationSchedulerTest.properties";
	private final static String OUT_PROPERTIES_PATH = "etc/test/input/LimitationSchedulerTestOutput.properties";

	public LimitationSchedulerTest() {
		super(LimitationSchedulerTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Test
	public void testLimitationScheduler() throws FileNotFoundException, IOException,
			InterruptedException {
		//create order
		createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);

		//login as crh_planner
		//fill appropriate search criteria
		//dispatch the new order to a planning truck
		dispatchOrderToPlanningTruck(orderProperties);

		deallocateAndMoveOrderWithUser1(orderProperties);
	}

	private void dispatchOrderToPlanningTruck(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, CRH_PLANNER_NAME, CRH_PLANNER_PASSWORD);

		// initialize Scheduling page
		SchedulingPage schedulingPage = initSchedulePage(getInProperty(TestProperties.SchedulePageKey.POOL_NAME),
				getInProperty(TestProperties.OrderPageKey.CARRIER_NAME),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		//move order to planning truck 1
		schedulingPage.schedulingAnOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1));

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	private void deallocateAndMoveOrderWithUser1(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, USER1_NAME, USER1_PASSWORD);

		// initialize Scheduling page
		SchedulingPage schedulingPage = initSchedulePage(getInProperty(TestProperties.SchedulePageKey.POOL_NAME),
				getInProperty(TestProperties.OrderPageKey.CARRIER_NAME),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		//verify that user_1 can see dispatched order 
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1),
				custOrderNr));

		//deallocate order
		schedulingPage.deallocateOrder(custOrderNr);

		//verify that deallocate failed by checking whether truck1 contains order
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1),
				custOrderNr));

		//try to move order to truck 2
		schedulingPage.schedulingAnOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID2));

		//verify move failed by checking: 
		//     - the order is contained in truck 1
		//     - truck 2 does not contain the order
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1),
				custOrderNr));
		
		// TODO 18-Dec-2014/nhattan: This make nightly build timed out often when it cound not found truck that contains order
		// Assert.assertFalse(schedulingPage.isTruckContainsOrder(getInProperty(SchedulePageKey.TRUCK_ID2),
		//		custOrderNr));

		//double click on assignment
		schedulingPage.doubleClickScheduledOrder(custOrderNr);

		//verify that popup does not appear
		schedulingPage.verifyPopup(false);

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}
}

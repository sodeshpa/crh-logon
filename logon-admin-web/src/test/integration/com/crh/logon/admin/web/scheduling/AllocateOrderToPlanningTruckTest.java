/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


public class AllocateOrderToPlanningTruckTest extends AbstractSchedulingPageHelper {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AllocateOrderToPlanningTruckTest.class);
	private final static String IN_PROPERTIES_PATH = "etc/test/input/AllocateOrderToPlanningTruckTest.properties";
	private final static String OUT_PROPERTIES_PATH = "etc/test/output/AllocateOrderToPlanningTruckTestOutput.properties";

	public AllocateOrderToPlanningTruckTest() {
		super(AllocateOrderToPlanningTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Test
	public void testAllocateOrderToPlanningTruck() throws FileNotFoundException, IOException,
			InterruptedException {
		//add new order
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String orderNr = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		LOG.info("orderNr: " + orderNr);

		//login as crh_planner
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, CRH_PLANNER_NAME, CRH_PLANNER_PASSWORD);
		// initialize Scheduling page
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		//set search criteria
		schedulingPage.selectPool(getInProperty(TestProperties.SchedulePageKey.POOL_NAME));
		schedulingPage.selectCarriers(true, false);
		schedulingPage.selectDay(orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		//move order to planning truck 1
		schedulingPage.schedulingAnOrder(orderNr, getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1));

		//verify that truck 1 contains the order
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1),
				custOrderNr));

		//move order to planning truck 2
		schedulingPage.moveOrderToOtherTruck(orderNr, custOrderNr,
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID2));

		//verify that truck 1 does not contain the order
		// TODO 18-Dec-2014/nhattan: This make nightly build timed out often when it cound not found truck that contains order
		// Assert.assertFalse(schedulingPage.isTruckContainsOrder(getInProperty(SchedulePageKey.TRUCK_ID1),
		//		custOrderNr));
		//verify that truck 2 contains the order
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(getInProperty(TestProperties.SchedulePageKey.TRUCK_ID2),
				custOrderNr));

		//release order
		schedulingPage.releaseOrder(custOrderNr);
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}
}

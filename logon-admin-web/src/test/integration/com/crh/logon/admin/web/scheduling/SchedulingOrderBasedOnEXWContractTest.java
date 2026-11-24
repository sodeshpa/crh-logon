/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


import com.crh.logon.admin.web.order.pages.OrderStatus;

/**
 * @testspec https://aww.adnovum.ch/jira/browse/LOGON-8942
 */

public class SchedulingOrderBasedOnEXWContractTest extends AbstractSchedulingPageHelper {

	private static final String IN_PROPERTIES_PATH = "etc/test/input/SchedulingOrderBasedOnEXWContractTest.properties";

	private static final String OUT_PROPERTIES_PATH = "etc/test/output/SchedulingOrderBasedOnEXWContractTestOutput.properties";

	public SchedulingOrderBasedOnEXWContractTest() {
		super(SchedulingOrderBasedOnEXWContractTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Override
	protected void modifyProperties(Properties properties) {
		setDeliveryDate(new Date(), properties);
	}

	@Test
	public void schedulingOrderBasedOnEXWContractTest() throws FileNotFoundException,
			InterruptedException, IOException {
		createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		//create truck
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
		String truckId = this.createTruck(inProperties);
		LogonSeleniumTestCaseBase.logoutOfLogon();

		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);

		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();

		// initialize Scheduling page
		SchedulingPage schedulingPage = initSchedulePage(getInProperty(TestProperties.SchedulePageKey.POOL_NAME),
				getInProperty(TestProperties.OrderPageKey.CARRIER_NAME),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		String orderNr = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());

		//verify that order is available among the unallocated orders
		schedulingPage.selectUnallocatedOrder(orderNr);

		//move order to truck
		schedulingPage.schedulingAnOrder(orderNr, truckId);
		//verify that truck contains the order
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(truckId, custOrderNr));

		schedulingPage.verifyOrderStatus(custOrderNr, OrderStatus.SCHEDULED.getStatus());

		schedulingPage.releaseOrder(custOrderNr);
		schedulingPage.verifyOrderStatus(custOrderNr,
				OrderStatus.RELEASED_FOR_EXECUTION.getStatus());

		schedulingPage.enterShippingPointForScheduledOrder(custOrderNr);
		schedulingPage.verifyOrderStatus(custOrderNr,
				OrderStatus.ARRIVED_AT_LOADING_POINT.getStatus());

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

}

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

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


public class DispatchingOrderToPlanningTruckTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/DispatchingOrderToPlanningTruckTest.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/DispatchingOrderToPlanningTruckTestOutput.properties";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispatchingOrderToPlanningTruckTest.class);

	public DispatchingOrderToPlanningTruckTest() {
		super(DispatchingOrderToPlanningTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Test
	public void testDispatchingOrderToPlanningTruck() throws FileNotFoundException,
			InterruptedException, IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		String truckId = this.createTruck(inProperties);
		LOG.info("Created Truck ID: " + truckId);

		dispatchOrderToTruck(inProperties, orderProperties, truckId);
	}

	/**
	 * Dispatch order to truck, release it and then deallocate it
	 * 
	 * @param inProperties the input properties
	 * @param orderProperties create order properties from previous step
	 * @param truckId created truck id
	 */
	private void dispatchOrderToTruck(Properties inProperties, Properties orderProperties,
			String truckId) {
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());

		SchedulingPage schedulePage = initSchedulePage(
				inProperties.getProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				inProperties.getProperty(TestProperties.SchedulePageKey.CARRIER_NAME.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		
		// TODO 18-Dec-2014/nhattan: This make nightly build timed out often when it cound not found truck that contains order
		// Assert.assertFalse(schedulePage.isTruckContainsOrder(truckId, custOrderNr));
		
		schedulePage.schedulingAnOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()), truckId);
		
		Assert.assertTrue(schedulePage.isTruckContainsOrder(truckId, custOrderNr));
		
		schedulePage.releaseOrder(custOrderNr);
		schedulePage.deallocateOrder(custOrderNr);
	}
}

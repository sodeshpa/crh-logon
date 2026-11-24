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
import org.junit.Test;
import org.junit.runner.RunWith;



public class DispatchingOrderWithoutPDATest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/DispatchingOrderWithoutPDA.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/DispatchingOrderWithoutPDAOutput.properties";

	public DispatchingOrderWithoutPDATest() {
		super(DispatchingOrderWithoutPDATest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	/**
	 * @TestSpec logon-833:Dispatching order with PDA
	 * @TestSpec LOGON-8887: Automate Testcase: logon-833:Dispatching order with PDA
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testDispatchingOrderWithPDA() throws FileNotFoundException, InterruptedException,
			IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();

		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		String truckId = this.createTruck(inProperties);
		dispatchOrder(inProperties, orderProperties, truckId);

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * Dispatch the order to truck
	 * @param inProperties input properties
	 * @param orderProperties the order output properties
	 * @param truckId the created truck id
	 */
	private void dispatchOrder(Properties inProperties, Properties orderProperties, String truckId) {
		SchedulingPage schedulePage = initSchedulePage(
				inProperties.getProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				inProperties.getProperty(TestProperties.SchedulePageKey.CARRIER_NAME.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulePage.schedulingAnOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()), truckId);
	}
}
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



public class DispatchingBagOrderFromTruckToTruckTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/DispatchingBagOrderFromTruckToTruck.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/DispatchingBagOrderFromTruckToTruckOutput.properties";

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public DispatchingBagOrderFromTruckToTruckTest() {
		super(DispatchingBagOrderFromTruckToTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH,
				null);
	}

	/**
	 * 
	 * @TestSpec logon-832: Dispatching (bag) order from truck to truck
	 * @TestSpec LOGON-8886: Automate Testcase: logon-832 Dispatching (bag) order from truck to truck
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testDispatchingBagOrderFromTruckToTruck() throws FileNotFoundException,
			InterruptedException, IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();

		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		String fromTruck = this.createTruck(inProperties);
		String toTruck = this.createTruck(inProperties);
		dispatchOrderFromTruckToTruck(inProperties, orderProperties, fromTruck, toTruck);

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * Dispatch the order into the one truck and then move it to another truck
	 * @param fromTruck first truck id
	 * @param toTruck second truck id
	 */
	private void dispatchOrderFromTruckToTruck(Properties inProperties, Properties orderProperties,
			String fromTruck, String toTruck) {
		String custOrderNo = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String orderNo = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());

		SchedulingPage schedulePage = initSchedulePage(
				inProperties.getProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				inProperties.getProperty(TestProperties.SchedulePageKey.CARRIER_NAME.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		// schedule to the truck
		// TODO 18-Dec-2014/nhattan: This make nightly build timed out often when it cound not found truck that contains order
		//Assert.assertFalse(schedulePage.isTruckContainsOrder(fromTruck, custOrderNo));
		schedulePage.schedulingAnOrder(orderNo, fromTruck);
		
		Assert.assertTrue(schedulePage.isTruckContainsOrder(fromTruck, custOrderNo));

		// move to other truck
		// TODO 18-Dec-2014/nhattan: This make nightly build timed out often when it cound not found truck that contains order
		//Assert.assertFalse(schedulePage.isTruckContainsOrder(toTruck, custOrderNo));
		schedulePage.moveOrderToOtherTruck(orderNo, custOrderNo, toTruck);
		
		Assert.assertTrue(schedulePage.isTruckContainsOrder(toTruck, custOrderNo));
	}
}

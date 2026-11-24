/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.crh.logon.admin.web.loading.pages.LoadingPage;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Ignore;
import org.junit.Test;

import com.crh.logon.admin.web.pages.LogonPageFactory;

@Ignore
public class ReroutSingleDeliveryOrderWithTwoOrderItemsTest extends AbstractSchedulingPageHelper {

	private static final String IN_PROPERTIES_PATH_FIRST = "etc/test/input/ReroutSingleDeliveryOrderWithTwoOrderItemsTest1.properties";

	private static final String IN_PROPERTIES_PATH_SECOND = "etc/test/input/ReroutSingleDeliveryOrderWithTwoOrderItemsTest1.properties";

	private static final String OUT_PROPERTIES_PATH_FIRST = "etc/test/output/ReroutSingleDeliveryOrderWithTwoOrderItemsTestOutput1.properties";

	private static final String OUT_PROPERTIES_PATH_SECOND = "etc/test/output/ReroutSingleDeliveryOrderWithTwoOrderItemsTestOutput2.properties";

	/**
	 */
	public ReroutSingleDeliveryOrderWithTwoOrderItemsTest() {
		super(ReroutSingleDeliveryOrderWithTwoOrderItemsTest.class.getSimpleName(),
				IN_PROPERTIES_PATH_FIRST, null);
	}

	/**
	 * This is not done yet since we couldn't load the order. An warning message: "invalid quantity" appears
	 * @TestSpec Automate Testcase: logon-1234:Re-routing single delivery order with two order items
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testReroutSingleDeliveryOrderWithTwoOrderItems() throws FileNotFoundException,
			InterruptedException, IOException {
		this.createOrder(IN_PROPERTIES_PATH_FIRST, OUT_PROPERTIES_PATH_FIRST);
		this.createOrder(IN_PROPERTIES_PATH_SECOND, OUT_PROPERTIES_PATH_SECOND);

		Properties firstInProps = propertyManager.loadProperties(IN_PROPERTIES_PATH_FIRST);
		Properties secondInProps = propertyManager.loadProperties(IN_PROPERTIES_PATH_SECOND);
		Properties firstOrderProps = propertyManager.loadProperties(OUT_PROPERTIES_PATH_FIRST);
		Properties secondOrderProps = propertyManager.loadProperties(OUT_PROPERTIES_PATH_SECOND);

		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
		// create new truck
		String truckId = this.createTruck(firstInProps);
		System.out.println(truckId);

		SchedulingPage schedulePage = initSchedulePage(
				getInProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME.getKeyString()),
				firstOrderProps.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		String firstCustOrder = firstOrderProps.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String firstOrderNr = firstOrderProps.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		String secondCustOrder = secondOrderProps.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String secondOrderNr = secondOrderProps.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());

		// schedule the first order to truck
		schedulePage.schedulingAnOrder(firstOrderNr, truckId);
		schedulePage.isTruckContainsOrder(truckId, firstCustOrder);

		// schedule the second order to truck
		schedulePage.schedulingAnOrder(secondOrderNr, truckId);
		schedulePage.isTruckContainsOrder(truckId, secondCustOrder);

		// merge 2 assignments
		schedulePage.mergeAssignment(firstCustOrder, secondCustOrder);

		// release order
		schedulePage.releaseOrder(secondCustOrder);

		// enter net quantity for first order
		enterLoadingInfo(firstOrderNr,
				firstInProps.getProperty(TestProperties.LoadingPageKey.NET_QUANTITY.getKeyString()),
				firstInProps.getProperty(TestProperties.LoadingPageKey.UNIT_OF_MEASURE.getKeyString()));

		// enter net quantity for second order
		enterLoadingInfo(secondOrderNr,
				secondInProps.getProperty(TestProperties.LoadingPageKey.NET_QUANTITY.getKeyString()),
				secondInProps.getProperty(TestProperties.LoadingPageKey.UNIT_OF_MEASURE.getKeyString()));

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * Enter loading information
	 * @param loadingPage loading page
	 * @param netQuantity net quantity
	 * @param uom unit of measure
	 * @param orderNr order number
	 */
	private void enterLoadingInfo(String orderNr, String netQuantity, String uom) {
		LoadingPage loadingPage = LogonPageFactory.initElements(seleniumDriver, LoadingPage.class);
		loadingPage.searchOrder(orderNr);
		loadingPage.enterNetQuantityForBag(netQuantity);
		loadingPage.selectUnitOfMeasure(uom);
		loadingPage.checkAndSave();
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.crh.logon.admin.web.loading.pages.LoadingPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.TestProperties.LoadingPageKey;
import com.crh.logon.admin.web.pages.TestProperties.OrderPageKey;
import com.crh.logon.admin.web.pages.TestProperties.SchedulePageKey;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;

public class MultidropCaseLoadingTourBagTest extends AbstractSchedulingPageHelper {

	private static final String IN_PROPERTIES_PATH_FIRST = "etc/test/input/MultidropCaseLoadingTourBagTest1.properties";

	private static final String IN_PROPERTIES_PATH_SECOND = "etc/test/input/MultidropCaseLoadingTourBagTest2.properties";

	private static final String OUT_PROPERTIES_PATH_FIRST = "etc/test/output/MultidropCaseLoadingTourBagTestOutput1.properties";

	private static final String OUT_PROPERTIES_PATH_SECOND = "etc/test/output/MultidropCaseLoadingTourBagTestOutput2.properties";

	/**
	 */
	public MultidropCaseLoadingTourBagTest() {
		super(MultidropCaseLoadingTourBagTest.class.getSimpleName(), IN_PROPERTIES_PATH_FIRST, null);
	}

	/**
	 * @TestSpec logon-983:Multidrop case (Loading tour) - bag
	 * @TestSpec LOGON-9034: Automate Testcase: logon-983:Multidrop case (Loading tour) - bag
	 *
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws FileNotFoundException  
	 */
	@Test
	public void testMultidropCaseLoadingTourBag() throws FileNotFoundException,
			InterruptedException, IOException {
		this.createOrder(IN_PROPERTIES_PATH_FIRST, OUT_PROPERTIES_PATH_FIRST);
		Properties secondInProps = propertyManager.loadProperties(IN_PROPERTIES_PATH_SECOND);
		possibleBillTo(secondInProps);
		this.createOrder(IN_PROPERTIES_PATH_SECOND, OUT_PROPERTIES_PATH_SECOND);

		Properties firstInProps = propertyManager.loadProperties(IN_PROPERTIES_PATH_FIRST);
		Properties firstOrderProps = propertyManager.loadProperties(OUT_PROPERTIES_PATH_FIRST);
		Properties secondOrderProps = propertyManager.loadProperties(OUT_PROPERTIES_PATH_SECOND);

		loginToLogonAsAdminEN();
		SchedulingPage schedulePage = initSchedulePage(
				getInProperty(SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(SchedulePageKey.CARRIER_NAME.getKeyString()),
				firstOrderProps.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		String truckId = getInProperty(SchedulePageKey.TRUCK_ID1.getKeyString());
		String firstCustOrder = firstOrderProps.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString());
		String firstOrderNr = firstOrderProps.getProperty(OrderPageKey.ORDER_NO.getKeyString());
		String secondCustOrder = secondOrderProps.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString());
		String secondOrderNr = secondOrderProps.getProperty(OrderPageKey.ORDER_NO.getKeyString());

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
				firstInProps.getProperty(LoadingPageKey.NET_QUANTITY.getKeyString()),
				firstInProps.getProperty(LoadingPageKey.UNIT_OF_MEASURE.getKeyString()));

		// enter net quantity for second order
		enterLoadingInfo(secondOrderNr,
				secondInProps.getProperty(LoadingPageKey.NET_QUANTITY.getKeyString()),
				secondInProps.getProperty(LoadingPageKey.UNIT_OF_MEASURE.getKeyString()));

		logoutOfLogon();
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

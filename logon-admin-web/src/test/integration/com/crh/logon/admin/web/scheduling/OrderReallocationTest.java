/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision: $
 * Date of last edit : $Date: $
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTab.ExtJsCombo;
import com.crh.logon.admin.web.masterdata.road.pages.TruckPage;
import com.crh.logon.admin.web.order.pages.OrderStatus;
import com.crh.logon.admin.web.pages.CarrierPreSelectionPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class OrderReallocationTest extends AbstractSchedulingPageHelper {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderReallocationTest.class);

	private static final String IN_PROPERTIES_PATH = "etc/test/input/OrderReallocationTest.properties";

	private static final String OUT_PROPERTIES_PATH = "etc/test/output/OrderReallocationTest.properties";

	public OrderReallocationTest() {
		super(OrderReallocationTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	/**
	 * @throws FileNotFoundException	
	 * @throws InterruptedException
	 * @throws IOException
	 * @TestSpec logon-1237:Order reallocation from planning truck to planning truck
	 * @TestSpec LOGON-8944: Automate Testcase: logon-1237:Order reallocation from planning truck to planning truck
	 */
	@Test
	public void testOrderReallocation() throws FileNotFoundException, InterruptedException,
			IOException {
		// create new truck
		loginToLogonAsAdminEN();
		String fromTruck = createBulkPowderTruck();
		LOG.info("First Truck with ID " + fromTruck + " is created successfully");
		saveTruckIdToPropertiesFile(TestProperties.SchedulePageKey.TRUCK_ID1.getKeyString(), fromTruck);

		// create new truck in the same Carrier as first truck
		String toTruck1 = createBulkPowderTruck();
		LOG.info("Second Truck with ID " + toTruck1 + " is created successfully");
		saveTruckIdToPropertiesFile(TestProperties.SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_1.getKeyString(),
				toTruck1);
		logoutOfLogon();

		Properties inProperties = propertyManager.loadProperties(IN_PROPERTIES_PATH);
		this.possibleBillTo(inProperties);

		// create new order
		createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		LOG.info("Order created");

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);
		String custOrderNo = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String orderNo = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		LOG.info("orderNr: " + orderNo);

		loginToLogonAsPoolManager();
		SchedulingPage schedulePage = initSchedulePage(getInProperty(TestProperties.SchedulePageKey.POOL_NAME),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME),
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME2));

		// submit the order to carrier
		schedulePage.submitOrderToCarrier(orderNo);

		// confirm order
		schedulePage.confirmOrder(orderNo);

		//set the Executing carrier to Carrier
		schedulePage.selectExecuteCarriersBelongToCustOrderNo(custOrderNo,
				getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1));

		// schedule the order to Carrier 1
		schedulePage.schedulingAnOrder(orderNo, fromTruck);
		schedulePage.verifyOrderStatus(custOrderNo, OrderStatus.SCHEDULED.getStatus());
		// Assert.assertTrue(schedulePage.isTruckContainsOrder(fromTruck, custOrderNo));

		// move to other truck of the same Carrier 1
		schedulePage.moveOrderToOtherTruck(orderNo, custOrderNo, toTruck1);
		schedulePage.verifyOrderStatus(custOrderNo, OrderStatus.SCHEDULED.getStatus());

		// move to other truck of Carrier 2
		String toTruck2 = getInProperty(TestProperties.SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_2);
		schedulePage.moveOrderToOtherTruck(orderNo, custOrderNo, toTruck2);
		schedulePage.verifyOrderStatus(custOrderNo, OrderStatus.SCHEDULED.getStatus());

		logoutOfLogon();
	}

	public void selectExtJsCombo(ExtJsCombo combo, String value) {
		String extjsComboPrefix = "combosettings[" + 0 + "].";
		selectExtJsCombo(extjsComboPrefix + combo.getName(), combo.getSearchField(), value);
	}

	public void selectExtJsCombo(String jsComboVarName, String by, String valueToSelect) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) seleniumDriver;
			String javaScriptForSelect = "var record = " + jsComboVarName + ".findRecord('" + by
					+ "', " + valueToSelect + ");" + "var index = " + jsComboVarName
					+ ".store.indexOf(record);" + jsComboVarName + ".onSelect(record, index);";
			js.executeScript(javaScriptForSelect);
			waitForLoading(seleniumDriver);
		}
		catch (Exception e) {
			Assert.fail("Error selecting combo: " + jsComboVarName);
		}
	}

	public void waitForLoading(WebDriver driver) {
		new AdNWebDriverWait(driver, 20).until(invisibilityOfElementLocated(By.id("loading-msg")));
	}

	private String createBulkPowderTruck() throws FileNotFoundException, IOException {
		Properties properties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		CarrierPreSelectionPage carrierPreSelectionPage = LogonPageFactory.initElements(
				seleniumDriver, CarrierPreSelectionPage.class);

		TruckPage truckPage = carrierPreSelectionPage.selectCarrier(
				properties.getProperty(TestProperties.TruckPageKey.CARRIER.getKeyString()), TruckPage.class);

		String truckId = truckPage.createNewTruck(properties);
		truckPage.expectSuccessMessageShown(true);
		return truckId;
	}

	/**
	 * Save truck id to properties file
	 *
	 * @param truckId
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void saveTruckIdToPropertiesFile(String propertyName, String truckId)
			throws FileNotFoundException, IOException {
		Map properties = new HashMap<String, String>();
		properties.put(propertyName, truckId);

		propertyManager.storeTestRelatedProperties(properties, OUT_PROPERTIES_PATH, true);
	}
}

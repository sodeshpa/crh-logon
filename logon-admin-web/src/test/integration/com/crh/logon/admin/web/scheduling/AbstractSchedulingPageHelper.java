/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.crh.logon.admin.web.masterdata.pages.ShiptoGeneralTab;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTab;
import com.crh.logon.admin.web.order.pages.OrderResult;
import com.crh.logon.admin.web.pages.CarrierPreSelectionPage;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSearchPage;
import com.crh.logon.admin.web.masterdata.road.pages.TruckPage;
import com.crh.logon.admin.web.order.pages.OrderPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.utils.StringUtils;

public abstract class AbstractSchedulingPageHelper extends PropertyTransferTest {

	protected final static String ORDER_IN_PROPERTIES_PATH = "etc/test/input/OrderAndTruckInfoForSchedulingPage.properties";

	protected static final String ORDER_URL = "/logon/order/dispo/DispoEntry.do";

	protected static final String CRH_PLANNER_NAME = "crh_planner";

	protected static final String CRH_PLANNER_PASSWORD = "1234";

	protected static final String USER1_NAME = "user_1";

	protected static final String USER1_PASSWORD = "1234";

	protected static final String USER2_NAME = "user_2";

	protected static final String USER2_PASSWORD = "1234";

	private final static String DATE_PATTERN = "dd.MM.yy";

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public AbstractSchedulingPageHelper(String className, String inPropertiesPath,
			String outPropertiesPath) {
		super(className, inPropertiesPath, outPropertiesPath);
	}

	/**
	 * Method to modify properties, maybe necessary for subclasses to modify existent property.
	 * @param properties the properties to modify
	 */
	protected void modifyProperties(Properties properties) {
	}

	protected void setDeliveryDate(Date date, Properties properties) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
		String deliveryDate = dateFormat.format(date);
		properties.put(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString(), deliveryDate);
		properties.put(TestProperties.OrderPageKey.DELIVERY_TO_DATE.getKeyString(), deliveryDate);
	}

	/**
	 * @param inProperties
	 * @throws InterruptedException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void createOrder(String orderInPropertiesPath, String inPropertiesPath,
			String outPropertiesPath) throws InterruptedException, FileNotFoundException,
			IOException {
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
		Properties properties = null;
		//merge from in properties to order properties if inPropertiesPath available
		if (StringUtils.notEmpty(inPropertiesPath)) {
			propertyManager.mergeProperties(orderInPropertiesPath, inPropertiesPath);
			properties = propertyManager.getPropertiesForPath(inPropertiesPath);
		}
		else {
			propertyManager.loadProperties(orderInPropertiesPath);
			properties = propertyManager.getPropertiesForPath(orderInPropertiesPath);
		}

		modifyProperties(properties);

		// initialize order page
		OrderPage orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		orderPage.selectShipto(properties.getProperty(TestProperties.OrderPageKey.SHIPTO.getKeyString()));

		// create an order
		OrderResult orderResult = orderPage.createOrderWithSampledata(properties);
		saveOrderToPropertiesFile(orderResult, outPropertiesPath);
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	protected void createOrder(String inPropertiesPath, String outPropertiesPath)
			throws FileNotFoundException, InterruptedException, IOException {
		createOrder(ORDER_IN_PROPERTIES_PATH, inPropertiesPath, outPropertiesPath);
	}

	/**
	 * @param orderResult
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void saveOrderToPropertiesFile(OrderResult orderResult, String outPropertiesPath)
			throws FileNotFoundException, IOException {
		Map properties = new HashMap<String, String>();
		properties.put(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString(), orderResult.getCustOrderNr());
		properties.put(TestProperties.OrderPageKey.ORDER_NO.getKeyString(), orderResult.getOrderNr());
		properties.put(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString(),
				orderResult.getDeliveryFromDate());

		propertyManager.storeTestRelatedProperties(properties, outPropertiesPath, false);
	}

	/**
	 * @param inProperties
	 * @param orderProperties
	 */
	protected void schedulingOrderWithCrhPlanner(Properties inProperties,
			Properties orderProperties, boolean isDeallocate) {
		LogonSeleniumTestCaseBase.loginToLogon("/logon/admin/AdminEntry.do", "crh_planner", "1234");

		String orderNo = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		String custOrderNo = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());

		// initialize scheduling page
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		schedulingPage.selectPool(inProperties.getProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()));
		schedulingPage.selectDay(orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		// verify the created order is available
		schedulingPage.selectUnallocatedOrder(orderNo);
		verifyConfirmDeclineButtons(schedulingPage);

		// select the carrier for unallocated order
		schedulingPage.selectCommercialCarriersBelongToCustOrderNo(custOrderNo,
				inProperties.getProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()));
		verifyThreeAvailableButtons(schedulingPage);

		// submit the order to carrier
		schedulingPage.submitOrderToCarrier(orderNo);

		// schedule order to truck
		schedulingPage.selectCarriers(false, false,
				inProperties.getProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()));
		schedulingPage.schedulingAnOrder(
				orderNo,
				inProperties.getProperty(TestProperties.SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_1.getKeyString()));

		// release order
		schedulingPage.releaseOrder(custOrderNo);

		// true if user want to deallocate the order right after allocating it
		schedulingPage.waitForLoading(seleniumDriver);
		if (isDeallocate) {
			schedulingPage.deallocateOrder(custOrderNo);
		}
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * Initialize the schedule page with some values
	 * @param carrierName the carrier name
	 * @param date the delivery date
	 * @return the new schedule page
	 */
	protected SchedulingPage initSchedulePage(String carrierName, String date) {
		// initialize scheduling page
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		schedulingPage.selectCarriers(false, false, carrierName);
		schedulingPage.selectDay(date);

		return schedulingPage;
	}

	/**
	 * Initialize the schedule page with some values
	 * @param poolName the pool name
	 * @param carrierName the carrier name
	 * @param date the delivery date
	 * @return the new schedule page
	 */
	protected SchedulingPage initSchedulePage(String poolName, String carrierName, String date) {
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		schedulingPage.selectPool(poolName);
		schedulingPage.selectCarriers(false, false, carrierName);
		schedulingPage.selectDay(date);

		return schedulingPage;
	}

	/**
	 * Initialize the schedule page with Pool Name, Date, and Multiple Carrier Names
	 * 
	 * @param poolName
	 * @param date
	 * @param carrierNames
	 * @return
	 */
	protected SchedulingPage initSchedulePage(String poolName, String date, String... carrierNames) {
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		schedulingPage.selectPool(poolName);
		schedulingPage.selectCarriers(false, false, carrierNames);
		schedulingPage.selectDay(date);

		return schedulingPage;
	}

	/**
	 * Verify the confirm order / decline order / submit to carrier buttons are enabled with the selected order
	 * @param schedulingPage
	 */
	protected void verifyThreeAvailableButtons(SchedulingPage schedulingPage) {
		verifyConfirmDeclineButtons(schedulingPage);
		schedulingPage.verifyEnableButton("buttonTransmitToCarrier");
	}

	/**
	 * Verify the confirm order / decline order buttons are enabled with the selected order
	 * @param schedulingPage 
	 */
	private void verifyConfirmDeclineButtons(SchedulingPage schedulingPage) {
		// check the confirm order / decline order button is enable
		schedulingPage.verifyEnableButton("buttonCancelOrder");
		schedulingPage.verifyEnableButton("buttonAcceptOrder");
	}

	/**
	 * Create bag truck for testing
	 * @param plateNumber plate number
	 * @param truckName truck name
	 * @return truck id
	 */
	protected String createTruck(Properties inProperties) {
		CarrierPreSelectionPage carrierPreSelectionPage = LogonPageFactory.initElements(
				seleniumDriver, CarrierPreSelectionPage.class);
		TruckPage truckPage = carrierPreSelectionPage.selectCarrier(
				inProperties.getProperty(TestProperties.TruckPageKey.CARRIER.getKeyString()), TruckPage.class);

		return truckPage.createNewTruck(inProperties);
	}

	/**
	 * Add possible bill to for this ship to
	 * @param inProperties input properties
	 */
	protected void possibleBillTo(Properties inProperties) {
		// select Possible Bill To in Shipto Master Data
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
		ShiptoSearchPage shiptoSearchPage = LogonPageFactory.initElements(seleniumDriver,
				ShiptoSearchPage.class);
		ShiptoGeneralTab shiptoGeneralTab = shiptoSearchPage.searchForShipto(inProperties.getProperty(TestProperties.OrderPageKey.SHIPTO.getKeyString()));
		ShiptoSaleAreaTab shiptoSaleAreaTab = shiptoGeneralTab.switchToSaleAreaTab(1);
		shiptoSaleAreaTab.selectPossibleBillto(inProperties.getProperty(TestProperties.OrderPageKey.BILLTO_NAME.getKeyString()));
		shiptoSaleAreaTab = shiptoSaleAreaTab.saveSetting();
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}
}

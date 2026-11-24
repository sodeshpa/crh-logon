/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.crh.logon.admin.web.externalordersearch.pages.ExternalOrderSearchPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.TestProperties.ExternalOrderKey;
import com.crh.logon.admin.web.pages.TestProperties.SchedulePageKey;
import com.crh.logon.admin.web.scheduling.pages.ExternalOrderForm;
import com.crh.logon.admin.web.scheduling.pages.ExternalOrderResult;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;

public class CreateAndDeliverExternalOrderTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/CreateAndDeliverExternalOrderTest.properties";

	private String deliveryFromDate = "";

	public CreateAndDeliverExternalOrderTest() {
		super(CreateAndDeliverExternalOrderTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	/**
	 * @TestSpec Automate Test: logon-1047:Create and deliver external order
	 */
	@Test
	public void testCreateAndDeliverExernalOrder() {
		loginToLogonAsAdminEN();
		Properties inProperties = propertyManager.loadProperties(IN_PROPERTIES_PATH);

		// create an external order
		ExternalOrderResult externalOrderResult = createExternalOrder(inProperties);
		deliveryFromDate = externalOrderResult.getDeliveryFromDate();

		// get order number from scheduling page
		SchedulingPage schedulingPage = getSchedulingPage();
		String extCustNo = schedulingPage.getLastOrderItemId();
		Assert.assertFalse(StringUtils.isEmpty(extCustNo));

		// edit quantity
		editExternalOrderQuantity(schedulingPage, extCustNo);

		// dispatch external order to truck
		dispatchExternalOrder(schedulingPage, extCustNo);

		// load external order
		loadExternalOrder(deliveryFromDate, extCustNo,
				getInProperty(ExternalOrderKey.LOADED_QUANTITY.getKeyString()));

		// process on other thing
		goOnOrderProcess();

		logoutOfLogon();
	}

	/**
	 * dispatch an external order to truck
	 * @param schedulingPage
	 * @param extCustNo
	 */
	private void dispatchExternalOrder(SchedulingPage schedulingPage, String extCustNo) {
		schedulingPage.schedulingAnExternalOrder(extCustNo,
				getInProperty(SchedulePageKey.TRUCK_ID1.getKeyString()));
		schedulingPage.selectDay(deliveryFromDate);
		schedulingPage.releaseExternalOrder();
	}

	/**
	 * Edit the quantity of an external order
	 * @param schedulingPage
	 * @param extCustNo
	 */
	private void editExternalOrderQuantity(SchedulingPage schedulingPage, String extCustNo) {
		ExternalOrderForm orderForm = schedulingPage.doubleClickUnallocatedOrder(extCustNo);
		orderForm.enterQuantity(getInProperty(ExternalOrderKey.EDIT_QUANTITY.getKeyString()));
		orderForm.clickSaveAndCloseButton();
	}

	/**
	 * Create an external order based on input properties file
	 * @param inProperties input data
	 * @return external order result
	 */
	private ExternalOrderResult createExternalOrder(Properties inProperties) {
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		ExternalOrderForm externalOrderForm = schedulingPage.clickCreatingExternalOrderButton();
		ExternalOrderResult externalOrderResult = externalOrderForm.createExternalOrder(inProperties);

		return externalOrderResult;
	}

	/**
	 * Get the scheduling page with some initial data
	 * @return scheduling page
	 */
	private SchedulingPage getSchedulingPage() {
		SchedulingPage schedulingPage = this.initSchedulePage(
				getInProperty(SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(SchedulePageKey.CARRIER_NAME.getKeyString()), deliveryFromDate);

		return schedulingPage;
	}

	/**
	 * Go on with other process on an order
	 */
	private void goOnOrderProcess() {
		SchedulingPage schedulingPage = getSchedulingPage();
		schedulingPage.selectScheduledExternalOrder();
		schedulingPage.clickArriveButton();
		schedulingPage.clickUnloadButton();
		schedulingPage.clickReturnOrderFinishButton();
	}

	/**
	 * Load an external order (search order, input loaded quantity)
	 * @param deliveryFromDate
	 * @param orderNr
	 * @param loadedQuantity
	 */
	private void loadExternalOrder(String deliveryFromDate, String orderNr, String loadedQuantity) {
		ExternalOrderSearchPage externalOrderSearchPage = LogonPageFactory.initElements(
				seleniumDriver, ExternalOrderSearchPage.class);
		externalOrderSearchPage.searchAnOrder(deliveryFromDate, orderNr);
		ExternalOrderForm externalOrderForm = externalOrderSearchPage.openOrderDetail(orderNr);
		externalOrderForm.enterLoadedQuantity(loadedQuantity);
		externalOrderForm.clickSaveAndCloseButton();
	}
}

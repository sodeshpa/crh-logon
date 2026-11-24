/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.road.external;

import java.util.Properties;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.ExternalOrderForm;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.scheduling.pages.ExternalOrderResult;

/**
 * This test is designed to test external order functionality. As the workflow is using pda, this is
 * part of the test triple for external workflow.
 * 
 * Part 1: logon-admin-web - Order creation - CreateExternalOrderTest (ant target: adnant test-external-pda-workflow-part0)
 * Part 2: logon-admin-web - Order dispatching - DispatchExternalOrderTest (ant target: adnant test-external-pda-workflow-part1 (runs part0) automatically)
 * Part 3: logon-simulator - Pda workflow execution - integration-test-soapui-project/ExternalWorkflowTest (test suite) (ant target: adnant test-external-pda-workflow-part2)
 * 
 * Please see how the properties are saved back in @ After
 * Note: This test has not been migrated to use the PageObjectPattern. Please 
 * don't use it as a blueprint for new tests. See also LOGON-8463.
 * 
 * @author AdNovum Informatik AG
 */
public final class CreateExternalOrderTest extends PropertyTransferTest {
	public static final String IN_PROPERTIES = "etc/test/input/ExternalOrderTest.properties";

	public static final String OUT_PROPERTIES = "etc/test/output/external_workflow.properties";

	public CreateExternalOrderTest() {
		super(CreateExternalOrderTest.class.getSimpleName(), IN_PROPERTIES, OUT_PROPERTIES);
	}

	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogon("/logon/order/dispo/DispoEntry.do", "adn_role_22", "adn_role_22");
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	@Test
	public void createExternalOrder() {

		// creata an external order
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		ExternalOrderForm externalOrderForm = schedulingPage.clickCreatingExternalOrderButton();
		Properties inProperties = propertyManager.loadProperties(IN_PROPERTIES);
		ExternalOrderResult externalOrderResult = externalOrderForm.createExternalOrder(inProperties);

		String deliveryFromDate = externalOrderResult.getDeliveryFromDate();

		setOutProperty(TestProperties.ExternalOrderKey.FROM_DATE, deliveryFromDate);

		// init the scheduling page
		schedulingPage = LogonPageFactory.initElements(seleniumDriver, SchedulingPage.class);
		schedulingPage.selectDay(deliveryFromDate);
		// select the pool 'ADN Pool External':
		schedulingPage.selectPool(getInProperty(TestProperties.ExternalOrderKey.POOL_NAME));
		// select 'keiner' and then 'alle' from combobox 'Spediteure' (this also forces a reload of the
		// orders list, so that no StaleElementReferenceExceptions should occur anymore):
		schedulingPage.selectCarriers(true, false);

		// Obtain the order number, assuming that the created order is the first one in the table.
		String orderNr = schedulingPage.getLastOrderItemId();
		Assert.assertFalse(StringUtils.isEmpty(orderNr));

		setOutProperty(TestProperties.ExternalOrderKey.ORDER_NO, orderNr);
	}

}
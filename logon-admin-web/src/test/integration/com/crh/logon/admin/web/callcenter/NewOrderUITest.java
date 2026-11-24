/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.callcenter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.order.pages.OrderPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * This class contains tests which verifies if the call center is generated with expected UI.
 * TODO (balakirev Sep 12, 2012): this test could be better if we could have one assert per test.
 * Problem: execBefore restarts selenium before each test.
 * 
 * Note: This test has not been migrated to use the PageObjectPattern. Please 
 * don't use it as a blueprint for new tests. See also LOGON-8463.
 * 
 * @author AdNovum Informatik AG
 */
public class NewOrderUITest extends PropertyTransferTest {
	private final static String IN_PATH = "etc/test/input/NewOrderUITest.properties";

	private final static String OUT_PATH = "etc/test/output/workflow.properties";

	private Properties properties;

	public NewOrderUITest() throws FileNotFoundException, IOException {
		super(NewOrderUITest.class.getSimpleName(), IN_PATH, OUT_PATH);
		properties = propertyManager.getTestRelatedStoredProperties(getInPropertiesPath());
	}

	@Before
	public void login() throws Exception {
		// Login to application to the Order screen (call center):
		loginToLogon("/logon/order/bestellung/BestellungShipTo.do", "logonadmin_de",
				"logonadmin_de");
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}

	@Test
	public void testButtonsShouldBeDisabledByDefault() throws Exception {
		String soldTo = properties.getProperty(TestProperties.OrderPageKey.SOLDTO.getKeyString()); // 73161
		String shipTo = properties.getProperty(TestProperties.OrderPageKey.SHIPTO.getKeyString()); // 1209037

		OrderPage orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		orderPage.selectShipto(soldTo + " " + shipTo);

		// Check if buttons are disabled by default:
		Assert.assertFalse(orderPage.isSaveButtonEnable());
		Assert.assertFalse(orderPage.isAdjustDeliveriesBtnEnable());
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.callcenter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.crh.logon.admin.web.order.pages.OrderPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.TestProperties.OrderPageKey;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 * Note: This test has not been migrated to use the PageObjectPattern. Please 
 * don't use it as a blueprint for new tests. See also LOGON-8463.
 */
@Ignore
public class CustorderElseTest extends PropertyTransferTest {

	
	private final static String IN_PATH = "etc/test/input/CustorderElseTest.properties";
	private final static String OUT_PATH = "etc/test/output/workflow.properties";
	
	private Properties properties;
	
	
	public CustorderElseTest() throws FileNotFoundException, IOException {
		super(CustorderElseTest.class.getSimpleName(), IN_PATH, OUT_PATH);
		properties = propertyManager.getTestRelatedStoredProperties(getInPropertiesPath());
	}
	
	@Before
	public void login() throws Exception {
		loginToLogon("/logon/order/bestellung/BestellungShipTo.do", "logonadmin_de", "logonadmin_de");
	}
	
	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}
	
	@Test
	public void testCreateCustorder() throws InterruptedException {
		OrderPage orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		
		// shipto: 73161, shipTo: 1209037
		orderPage.selectShipto(getProperty(OrderPageKey.SOLDTO) + " " + getProperty(OrderPageKey.SHIPTO));
		
		orderPage.createOrderWithSampledata(properties);
	}
	
	private String getProperty(OrderPageKey key) {
		return this.properties.getProperty(key.getKeyString());
	}

}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.admin;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.admin.web.admin.pages.UserAdminPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.constants.Constant;

/**
 * This test creates three users of different types (crh, customer, vendor)
 * and updates their user restrictions.
 */
@AdnTrace
public final class UserAuthorizationSeleniumTest extends PropertyTransferTest {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/CreatingUser.properties";

	public UserAuthorizationSeleniumTest() {
		super(UserAuthorizationSeleniumTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogon("/logon/admin/useradmin/UserAdminEntry.do", "logonadmin_de", "logonadmin_de");
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	@Test
	public void testCreateUserTypeCrh() throws Exception {
		testCreateUser(Constant.USER_TYPE_CRH, "Dispatching/Loading Road");
	}

	@Test
	public void testCreateUserTypeCustomer() throws Exception {
		testCreateUser(Constant.USER_TYPE_CUSTOMER, "Customer (Web Order)");
	}

	@Test
	public void testCreateUserTypeVendor() throws Exception {
		testCreateUser(Constant.USER_TYPE_VENDOR, "Scheduling Road");
	}

	// TODO 5-Nov-2014/nhattan: temporary apply Page Object Pattern, split UserAdminPage functions to smaller functions with more specific component and re-usable 
	public void testCreateUser(int userType, String roleLabel) throws Exception {
		Properties properties = propertyManager.getTestRelatedStoredProperties(IN_PROPERTIES_PATH);

		UserAdminPage userAdminPage = LogonPageFactory.initElements(seleniumDriver,
				UserAdminPage.class);

		userAdminPage.clickNewUserButton();
		userAdminPage.selectUserType(properties, userType);
		userAdminPage.checkForActive();

		userAdminPage.fillBasicInfo(properties);
		userAdminPage.fillExtInfo(properties, userType);

		userAdminPage.setRole(roleLabel);

		userAdminPage.saveUser(properties);

		userAdminPage.updateUserRestrictions(properties);
	}
}
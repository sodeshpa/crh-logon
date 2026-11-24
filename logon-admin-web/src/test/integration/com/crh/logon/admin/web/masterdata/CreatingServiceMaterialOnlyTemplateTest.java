/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.pages.ShiptoGeneralTab;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTabServiceMaterialTemplate;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSearchPage;
import com.crh.logon.admin.web.order.pages.OrderPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;

/**
 * @author AdNovum Informatik AG
 * 
 * @TestSpec name="logon-1140:Dispocard - create service material only template"
 */
public class CreatingServiceMaterialOnlyTemplateTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/CreatingServiceMaterialOnlyTemplateTest.properties";

	/**
	 * @param className name of the class
	 * @param inPropertiesPath the input test information
	 * @param outPropertiesPath the output test information
	 */
	public CreatingServiceMaterialOnlyTemplateTest() {
		super(CreatingServiceMaterialOnlyTemplateTest.class.getSimpleName(), IN_PROPERTIES_PATH,
				null);
	}

	@Before
	public void login() throws Exception {
		loginToLogonAsAdminEN();
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}

	@Test
	public void testCreateServiceMaterialOnlyTemplate() {
		Properties properties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		ShiptoSearchPage shiptoSearchPage = LogonPageFactory.initElements(seleniumDriver,
				ShiptoSearchPage.class);
		ShiptoGeneralTab shiptoGeneralTab = shiptoSearchPage.searchForShipto(properties.getProperty("service.material.keyword"));
		Assert.assertTrue(shiptoGeneralTab.isShiptoName(properties.getProperty("service.material.shipTo")));

		ShiptoSaleAreaTabServiceMaterialTemplate serviceMaterialPage = shiptoGeneralTab.switchToServiceMaterialTemplate();
		serviceMaterialPage.createNewServiceMaterialOnlyTemplate(properties);
		serviceMaterialPage.expectSuccessMessageShown(true);

		OrderPage orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		orderPage.selectShipto(properties.getProperty("service.material.shipTo"));

		int templateCount = orderPage.searchMaterialAndCountResults(
				properties.getProperty("service.material.materialName"), true,
				properties.getProperty("service.material.materialName"),
				properties.getProperty("service.material.uom"),
				properties.getProperty("service.material.quanlity"));

		Assert.assertTrue("No template created!", templateCount > 0);
	}
}

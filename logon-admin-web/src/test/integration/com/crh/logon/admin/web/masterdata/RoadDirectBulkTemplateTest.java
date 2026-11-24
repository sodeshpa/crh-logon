/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata;

import java.util.Properties;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.pages.ShiptoGeneralTab;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTab;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTab.ExtJsCombo;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTab.Template_Element;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSearchPage;
import com.crh.logon.admin.web.order.pages.OrderPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * 
 * @author AdNovum Informatik AG
 */
public class RoadDirectBulkTemplateTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/OrderTemplateTest.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/workflow.properties";

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public RoadDirectBulkTemplateTest() {
		super(RoadDirectBulkTemplateTest.class.getSimpleName(), IN_PROPERTIES_PATH,
				OUT_PROPERTIES_PATH);
	}

	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * @TestSpec logon-1048: Dispocard - create road direct bulk template
	 * @TestSpec LOGON-8455 Automate Testcase logon 1048: Dispocard - create road direct bulk template
	 */
	@Test
	public void testCreateRoadDirectBulkTemplate() {
		Properties properties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		String[] materialTexts = { properties.getProperty("bulkDirect.materialName"),
				properties.getProperty("bulkDirect.materialContract"), "Road direct" };
		//count the template

		OrderPage orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		orderPage.selectShipto(properties.getProperty("bulkDirect.shiptoName"));

		int templateCountBefore = orderPage.searchMaterialAndCountResults(
				properties.getProperty("bulkDirect.materialName"), false, materialTexts);
		//add new
		ShiptoSearchPage shiptoSearchPage = LogonPageFactory.initElements(seleniumDriver,
				ShiptoSearchPage.class);
		ShiptoGeneralTab shiptoGeneralTab = shiptoSearchPage.searchForShipto(properties.getProperty("bulkDirect.keySearch"));
		shiptoGeneralTab.resetOpeningTimes();
		ShiptoSaleAreaTab shiptoSaleAreaTab = shiptoGeneralTab.switchToSaleAreaTab();

		shiptoSaleAreaTab.searchMaterial(properties.getProperty("bulkDirect.materialName"),
				properties.getProperty("bulkDirect.materialContract"));

		fillNewTemplateAsRoadDirectBulk(shiptoSaleAreaTab, properties);
		shiptoSaleAreaTab.saveSetting();
		shiptoSaleAreaTab.expectSuccessMessageShown(true);

		//count again
		orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		orderPage.selectShipto(properties.getProperty("bulkDirect.shiptoName"));

		int templateCountAfter = orderPage.searchMaterialAndCountResults(
				properties.getProperty("bulkDirect.materialName"), false, materialTexts);

		Assert.assertTrue("No template created!", templateCountAfter > templateCountBefore);
	}

	private void fillNewTemplateAsRoadDirectBulk(ShiptoSaleAreaTab saleTab, Properties properties) {
		saleTab.selectExtJsCombo(ExtJsCombo.PRODUCTION_PLANT,
				properties.getProperty("bulkDirect.orderTemplate.productionPlantExtJs"));
		saleTab.selectExtJsCombo(ExtJsCombo.DELIVERY_TYPE,
				properties.getProperty("bulkDirect.orderTemplate.deliveryTypeExtJs"));
		saleTab.selectExtJsComboWithStringValue(ExtJsCombo.UOM,
				properties.getProperty("bulkDirect.orderTemplate.uomExtJs"));
		saleTab.selectExtJsCombo(ExtJsCombo.SUPPLY_PLANT,
				properties.getProperty("bulkDirect.orderTemplate.supplyPlantExtJs"));
		saleTab.selectExtJsComboWithStringValue(ExtJsCombo.SHIPPINGPOINT,
				properties.getProperty("bulkDirect.orderTemplate.shippingPointExtJs"));
		saleTab.selectExtJsCombo(ExtJsCombo.LOADINGPOINT,
				properties.getProperty("bulkDirect.orderTemplate.loaddingPointExtJs"));
		saleTab.selectExtJsCombo(ExtJsCombo.POOL,
				properties.getProperty("bulkDirect.orderTemplate.poolExtJs"));
		saleTab.selectExtJsCombo(ExtJsCombo.COMMERCIAL_CARRIER,
				properties.getProperty("bulkDirect.orderTemplate.commercialCarrierExtJs"));
		saleTab.selectExtJsCombo(ExtJsCombo.BILL_TO,
				properties.getProperty("bulkDirect.orderTemplate.billToExtJs"));
		saleTab.selectExtJsCombo(ExtJsCombo.PAYER,
				properties.getProperty("bulkDirect.orderTemplate.payerExtJs"));
		saleTab.inputTextField(
				Template_Element.QUANLITY.getHtmlTagId() + saleTab.getOrderTemplateNumberInList(),
				properties.getProperty("bulkDirect.orderTemplate.quanlity"));
		saleTab.selectCheckBox(Template_Element.EXACT_CUST_ORDER_QUANTITY_FLAG.getHtmlTagId()
				+ saleTab.getOrderTemplateNumberInList());
		saleTab.selectCheckBox(Template_Element.EXACT_SD_ORDER_QUANTITY_FLAG.getHtmlTagId()
				+ saleTab.getOrderTemplateNumberInList());
		saleTab.selectCheckBox(Template_Element.AUTO_POOL_FLAG.getHtmlTagId()
				+ saleTab.getOrderTemplateNumberInList());
		saleTab.selectCheckBox(Template_Element.AUTO_CARRIER_FLAG.getHtmlTagId()
				+ saleTab.getOrderTemplateNumberInList());
		saleTab.selectCheckBox(Template_Element.AVAILABLE_IN_CUSTWEB_FLAG.getHtmlTagId()
				+ saleTab.getOrderTemplateNumberInList());
	}
}
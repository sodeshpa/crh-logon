/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata;

import com.crh.logon.admin.web.masterdata.pages.ShiptoGeneralTab;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTab;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSearchPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class BagPickupTemplateTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/OrderTemplateTest.properties";

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public BagPickupTemplateTest() {
		super(BagPickupTemplateTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
	}

	/**
	 * @TestSpec logon-856: Dispocard - create bag pickup template
	 * @TestSpec LOGON-8456: Automate Testcase logon 856: Dispocard - create bag pickup template
	 */
	@Test
	public void testCreateBagPickupTemplate() {
		ShiptoSearchPage shiptoSearchPage = LogonPageFactory.initElements(seleniumDriver,
				ShiptoSearchPage.class);

		ShiptoGeneralTab shiptoGeneralTab = shiptoSearchPage.searchForShipto(getInProperty("bagPickup.keySearch"));
		shiptoGeneralTab.resetOpeningTimes();
		Assert.assertTrue(shiptoGeneralTab.isShiptoName(getInProperty("bagPickup.shiptoName")));
		shiptoGeneralTab.saveSetting();

		ShiptoSaleAreaTab shiptoSaleAreaTab = shiptoGeneralTab.switchToSaleAreaTab();
		shiptoSaleAreaTab.searchMaterial(getInProperty("bagPickup.materialName"),
				getInProperty("bagPickup.materialContract"));

		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.PRODUCTION_PLANT,
				getInProperty("bagPickup.orderTemplate.productionPlant"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.DELIVERY_TYPE,
				getInProperty("bagPickup.orderTemplate.deliveryType"));
		shiptoSaleAreaTab.selectExtJsComboWithStringValue(ShiptoSaleAreaTab.ExtJsCombo.UOM,
				getInProperty("bagPickup.orderTemplate.uom"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.SUPPLY_PLANT,
				getInProperty("bagPickup.orderTemplate.supplyPlant"));
		shiptoSaleAreaTab.selectExtJsComboWithStringValue(ShiptoSaleAreaTab.ExtJsCombo.SHIPPINGPOINT,
				getInProperty("bagPickup.orderTemplate.shippingPoint"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.LOADINGPOINT,
				getInProperty("bagPickup.orderTemplate.loaddingPoint"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.POOL,
				getInProperty("bagPickup.orderTemplate.pool"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.COMMERCIAL_CARRIER,
				getInProperty("bagPickup.orderTemplate.carrier"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.BILL_TO,
				getInProperty("bagPickup.orderTemplate.billTo"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.PAYER,
				getInProperty("bagPickup.orderTemplate.payer"));
		shiptoSaleAreaTab.inputTextField(ShiptoSaleAreaTab.Template_Element.QUANLITY.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList(),
				getInProperty("bagPickup.orderTemplate.quanlity"));
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AVAILABLE_IN_PICKUP_APP.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AVAILABLE_IN_CUSTWEB_FLAG.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());

		shiptoSaleAreaTab.saveSetting();
		shiptoSaleAreaTab = LogonPageFactory.initElements(seleniumDriver, ShiptoSaleAreaTab.class);
		shiptoSaleAreaTab.expectSuccessMessageShown(true);

		LogonSeleniumTestCaseBase.logoutOfLogon();

		/*
		TODO 14 Oct 2014/nhattan: Can not located the element in nightly build
		loginTerminalWebApp();
		
		TerminalHomePage terminalHomepage = LogonPageFactory.initElements(seleniumDriver, TerminalHomePage.class);
		
		TerminalShiptoPage terminalShiptoPage = terminalHomepage.clickNewOrderButton();
		terminalShiptoPage.selectShipto(getInProperty("bagPickup.shiptoName"));
		
		TerminalProductTypePage terminalMaterialTypePage = terminalShiptoPage.clickContinueButton();
		TerminalProductPage terminalProductPage = terminalMaterialTypePage.selectProductType(properties.getProperty("bagPickup.orderTemplate.teminalApp.bagOrder"));
		
		Assert.assertTrue(terminalProductPage.hasTemplate(properties.getProperty("bagPickyp.materialName.de")));
		logoutTerminalWebApp();
		*/
	}
}

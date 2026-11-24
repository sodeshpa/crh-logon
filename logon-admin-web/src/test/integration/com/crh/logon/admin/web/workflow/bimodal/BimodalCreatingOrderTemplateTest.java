/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.crh.logon.admin.web.masterdata.pages.ShiptoGeneralTab;
import com.crh.logon.admin.web.masterdata.pages.ShiptoSaleAreaTab;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import com.crh.logon.admin.web.masterdata.pages.ShiptoSearchPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.workflow.bimodal.BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY;

/**
 * 
 * @author AdNovum Informatik AG
 * 
 * @TestSpec name="logon-1150: General settings - Creating templates"
 * 
 * @TestSpec name="LOGON-8476: This workflow is documented in a tutorial: /share/doc/crh/logon_2_2/qa/testing tutorials/Bimodal Usecase.pdf""
 */
public class BimodalCreatingOrderTemplateTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/BimodalOrderTemplateTest.properties";

	private final static String SHIPPINGPOINT_PROPERTIES_PATH = "etc/test/input/BimodalShippingPointTest.properties";
	private final static String BIMODAL_WORKFLOW_PROPERTIES_PATH = "etc/test/output/bimodal_workflow.properties";

	public BimodalCreatingOrderTemplateTest() {
		super(BimodalCreatingShippingpointTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
		propertyManager.loadProperties(SHIPPINGPOINT_PROPERTIES_PATH);
		propertyManager.loadProperties(BIMODAL_WORKFLOW_PROPERTIES_PATH);
	}

	@Before
	public void login() throws Exception {
		loginToLogonAsAdminEN();
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}

	/**
	 * @TestSpec("logon-1150: General settings - Creating templates") 
	 * @TestSpec("LOGON-8476: This workflow is documented in a tutorial: /share/doc/crh/logon_2_2/qa/testing tutorials/Bimodal Usecase.pdf")
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testCreateBimodalTemplate() throws FileNotFoundException, IOException {
		// Create template for bimodal rail part
		ShiptoSearchPage shiptoSearchPage = LogonPageFactory.initElements(seleniumDriver,
				ShiptoSearchPage.class);
		ShiptoGeneralTab shiptoGeneralTab = shiptoSearchPage.searchForShipto(getInProperty("bimodal.railPart.shiptoName"));
		shiptoGeneralTab.resetOpeningTimes();
		shiptoGeneralTab.selectShiptoType(getInProperty("bimodal.railPart.shiptoType"));
		shiptoGeneralTab.selectMaterialFlowPlant(getInProperty("bimodal.railPart.materialFlowPlant"));
		shiptoGeneralTab.selectmaterialFlowShippingPoint(getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
				BIMODAL_WORKFLOW_KEY.SHIPPING_POINT_NAME.getKeyString()));

		shiptoGeneralTab.saveSetting();

		ShiptoSaleAreaTab shiptoSaleAreaTab = shiptoGeneralTab.switchToSaleAreaTab();
		shiptoSaleAreaTab.selectPossibleBillto(getInProperty("bimodal.railPart.orderTemplate.billTo.name"));

		shiptoSaleAreaTab = shiptoSaleAreaTab.saveSetting();

		shiptoSaleAreaTab.searchMaterial(getInProperty("bimodal.railPart.materialName"));

		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.PRODUCTION_PLANT,
				getInProperty("bimodal.railPart.orderTemplate.productionPlant"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.DELIVERY_TYPE,
				getInProperty("bimodal.railPart.orderTemplate.deliveryType"));
		shiptoSaleAreaTab.selectExtJsComboWithStringValue(ShiptoSaleAreaTab.ExtJsCombo.UOM,
				getInProperty("bimodal.railPart.orderTemplate.uom"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.SUPPLY_PLANT,
				getInProperty("bimodal.railPart.orderTemplate.supplyPlant"));

		shiptoSaleAreaTab.selectExtJsComboWithStringValue(ShiptoSaleAreaTab.ExtJsCombo.SHIPPINGPOINT,
				getInProperty("bimodal.railPart.orderTemplate.shippingPoint"));

		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.LOADINGPOINT,
				getInProperty("bimodal.railPart.orderTemplate.loaddingPoint"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.POOL,
				getInProperty("bimodal.railPart.orderTemplate.pool"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.COMMERCIAL_CARRIER,
				getInProperty("bimodal.railPart.orderTemplate.commercialCarrier"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.BILL_TO,
				getInProperty("bimodal.railPart.orderTemplate.billTo"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.PAYER,
				getInProperty("bimodal.railPart.orderTemplate.payer"));
		shiptoSaleAreaTab.inputTextField(ShiptoSaleAreaTab.Template_Element.QUANLITY.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList(),
				getInProperty("bimodal.railPart.orderTemplate.quanlity"));
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AUTO_POOL_FLAG.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AUTO_CARRIER_FLAG.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AVAILABLE_IN_PICKUP_APP.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AVAILABLE_IN_CUSTWEB_FLAG.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());

		shiptoSaleAreaTab.saveSetting();
		shiptoSaleAreaTab.expectSuccessMessageShown(true);

		// Create template for bimodal road part
		shiptoSearchPage = LogonPageFactory.initElements(seleniumDriver, ShiptoSearchPage.class);
		shiptoGeneralTab = shiptoSearchPage.searchForShipto(getInProperty("bimodal.roadPart.keySearch"));
		shiptoGeneralTab.resetOpeningTimes();
		Assert.assertTrue(shiptoGeneralTab.isShiptoName(getInProperty("bimodal.roadPart.shiptoName")));
		shiptoGeneralTab.selectShiptoType(getInProperty("bimodal.roadPart.shiptoType"));
		shiptoGeneralTab.saveSetting();

		shiptoSaleAreaTab = shiptoGeneralTab.switchToSaleAreaTab();
		shiptoSaleAreaTab.searchMaterial(getInProperty("bimodal.roadPart.materialName"),
				getInProperty("bimodal.roadPart.materialContract"));

		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.PRODUCTION_PLANT,
				getInProperty("bimodal.roadPart.orderTemplate.productionPlant"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.DELIVERY_TYPE,
				getInProperty("bimodal.roadPart.orderTemplate.deliveryType"));
		shiptoSaleAreaTab.selectExtJsComboWithStringValue(ShiptoSaleAreaTab.ExtJsCombo.UOM,
				getInProperty("bimodal.roadPart.orderTemplate.uom"));
		shiptoSaleAreaTab.selectExtJsComboWithStringValue(ShiptoSaleAreaTab.ExtJsCombo.SUPPLY_PLANT,
				getInProperty("bimodal.roadPart.orderTemplate.supplyPlant"));
		shiptoSaleAreaTab.selectExtJsComboWithStringValue(
				ShiptoSaleAreaTab.ExtJsCombo.SHIPPINGPOINT,
				getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
						BIMODAL_WORKFLOW_KEY.SHIPPING_POINT_NAME.getKeyString()));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.POOL,
				getInProperty("bimodal.roadPart.orderTemplate.pool"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.COMMERCIAL_CARRIER,
				getInProperty("bimodal.roadPart.orderTemplate.carrier"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.BILL_TO,
				getInProperty("bimodal.roadPart.orderTemplate.billTo"));
		shiptoSaleAreaTab.selectExtJsCombo(ShiptoSaleAreaTab.ExtJsCombo.PAYER,
				getInProperty("bimodal.roadPart.orderTemplate.payer"));
		shiptoSaleAreaTab.inputTextField(ShiptoSaleAreaTab.Template_Element.QUANLITY.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList(),
				getInProperty("bimodal.roadPart.orderTemplate.quanlity"));
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AUTO_POOL_FLAG.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AUTO_CARRIER_FLAG.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());
		shiptoSaleAreaTab.selectCheckBox(ShiptoSaleAreaTab.Template_Element.AVAILABLE_IN_CUSTWEB_FLAG.getHtmlTagId()
				+ shiptoSaleAreaTab.getOrderTemplateNumberInList());
		shiptoSaleAreaTab.saveSetting();
		shiptoSaleAreaTab.expectSuccessMessageShown(true);
	}
}

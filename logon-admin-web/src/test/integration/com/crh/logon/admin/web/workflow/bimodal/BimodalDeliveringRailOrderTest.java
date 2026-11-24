/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.rail.pages.WagonPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.rail.pages.RailPage;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.admin.web.workflow.bimodal.BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY;

/**
 * @author AdNovum Informatik AG
 */
public class BimodalDeliveringRailOrderTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/BimodalOrderTemplateTest.properties";

	private final static String BIMODAL_WORKFLOW_PROPERTIES_PATH = "etc/test/output/bimodal_workflow.properties";

	public BimodalDeliveringRailOrderTest() {
		super(BimodalDeliveringRailOrderTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
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
	 * @TestSpec LOGON-8479 Automate Testcase logon-1043: Delivering bimodal-rail order
	 * @TestSpec logon-1043: Delivering bimodal-rail order
	 */
	@Test
	public void testDeliverRailOrder() {
		RailPage railPage = LogonPageFactory.initElements(seleniumDriver, RailPage.class);
		railPage.selectPlantByValue(getInProperty("bimodal.railPart.orderTemplate.productionPlant"));

		railPage.selectShippingpoints(false, false,
				getInProperty("bimodal.railPart.orderTemplate.shippingPoint"));
		railPage.selectStracks(false, false, "AD 8101 Track");
		railPage.selectStatus(true, false);

		railPage.checkNoTrack(true);

		railPage.enterOrderNumber(getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
				BIMODAL_WORKFLOW_KEY.RAIL_PART_ORDER_NO.getKeyString()));
		railPage.enterWagonNumber(getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
				BIMODAL_WORKFLOW_KEY.WAGON_NO.getKeyString()));

		railPage.clickFilterButton();

		railPage.assignOrderToWagon(
				getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
						BIMODAL_WORKFLOW_KEY.RAIL_PART_ORDER_NO.getKeyString()),
				getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
						BIMODAL_WORKFLOW_KEY.WAGON_NO.getKeyString()));

		railPage.enterTareGross(
				getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
						BIMODAL_WORKFLOW_KEY.WAGON_NO.getKeyString()), "25", "80");

		railPage.transmitToRailCarrier();
		Assert.assertTrue(railPage.isUnloadedButtonClickable());

		railPage.unloaded();

		WagonPage wagonPage = LogonPageFactory.initElements(seleniumDriver, WagonPage.class);
		wagonPage.searchForWagon(getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
				BIMODAL_WORKFLOW_KEY.WAGON_NO.getKeyString()));
		wagonPage.setToDelivered();
	}
}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import java.util.Properties;

import com.crh.logon.admin.web.loading.pages.LoadingPage;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.pages.LogonPageFactory;

public class BimodalSchedulingLoadingRoadOrderTest extends PropertyTransferTest {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/BimodalSchedulingLoadingRoadOrderTest.properties";

	private final static String BIMODAL_WORKFLOW_PROPERTIES_PATH = "etc/test/output/bimodal_workflow.properties";

	public BimodalSchedulingLoadingRoadOrderTest() {
		super(BimodalSchedulingLoadingRoadOrderTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
		propertyManager.loadProperties(BIMODAL_WORKFLOW_PROPERTIES_PATH);
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
	 * @TestSpec LOGON-8480: Automate Testcase logon-1044: Scheduling and loading bimodal-road order 
	 * @TestSpec logon-1044: Scheduling and loading bimodal-road order
	 */
	@Test
	public void testSchedulingLoadingRoadOrder() {
		Properties properties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		Properties bimodalWorkFlowProperties = propertyManager.getPropertiesForPath(BIMODAL_WORKFLOW_PROPERTIES_PATH);

		// scheduling page
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);

		schedulingPage.selectPool(properties.getProperty("bimodal.scheduling.poolName"));
		schedulingPage.selectCarriers(false, false,
				properties.getProperty("bimodal.scheduling.carrierName"));
		schedulingPage.selectDay(bimodalWorkFlowProperties.getProperty(BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY.DELIVERY_FROM_DATE.getKeyString()));
		SchedulingPage scheduleAnOrder = schedulingPage.renewPage();

		// loading page
		scheduleAnOrder.schedulingAnOrder(
				bimodalWorkFlowProperties.getProperty(BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY.ROAD_PART_ORDER_NO.getKeyString()),
				bimodalWorkFlowProperties.getProperty(BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY.TRUCK_ID.getKeyString()));

		scheduleAnOrder.releaseOrder(bimodalWorkFlowProperties.getProperty(BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY.CUST_ORDER_NO.getKeyString()));

		LoadingPage loadingPage = LogonPageFactory.initElements(seleniumDriver, LoadingPage.class);
		loadingPage.searchOrder(bimodalWorkFlowProperties.getProperty(BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY.ROAD_PART_ORDER_NO.getKeyString()));
		loadingPage.enterTareWeightAndNetQuantity(
				properties.getProperty("bimodal.loading.tareWeight"),
				properties.getProperty("bimodal.loading.netQuantity"));

		loadingPage.checkAndSave();
	}
}

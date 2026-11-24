/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.crh.logon.admin.web.pages.CarrierPreSelectionPage;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.road.pages.TruckPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.workflow.bimodal.BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY;

public class BimodalCreatingBulkPowerTruckTest extends PropertyTransferTest {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/BulkPowerTruck.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/bimodal_workflow.properties";

	/**
	 * @param className name of the class
	 * @param inPropertiesPath the input test information
	 * @param outPropertiesPath the output test information
	 */
	public BimodalCreatingBulkPowerTruckTest() {
		super(BimodalCreatingBulkPowerTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	@Test
	public void testBimodalCreatingBulkPowerTruck() throws FileNotFoundException, IOException {
		Properties properties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		CarrierPreSelectionPage carrierPreSelectionPage = LogonPageFactory.initElements(
				seleniumDriver, CarrierPreSelectionPage.class);
		TruckPage truckPage = carrierPreSelectionPage.selectCarrier(
				properties.getProperty(TestProperties.TruckPageKey.CARRIER.getKeyString()), TruckPage.class);

		String truckId = truckPage.createNewTruck(properties);

		truckPage.expectSuccessMessageShown(true);

		saveTruckIdToPropertiesFile(truckId);
	}

	/**
	 * Save truck id to properties file, will be used in BimodalDeliveringRoadOrder test
	 * @param truckId
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void saveTruckIdToPropertiesFile(String truckId) throws FileNotFoundException,
			IOException {
		Map properties = new HashMap<String, String>();
		properties.put(BIMODAL_WORKFLOW_KEY.TRUCK_ID.getKeyString(), truckId);

		propertyManager.storeTestRelatedProperties(properties, OUT_PROPERTIES_PATH, true);
	}
}

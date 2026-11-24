/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.crh.logon.admin.web.masterdata.rail.pages.WagonPage;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.workflow.bimodal.BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY;

/**
 * @author AdNovum Informatik AG
 */
public class BimodalCreatingEmptyWagonTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/BimodalCreatingEmptyWagon.properties";

	private final static String BIMODAL_WORKFLOW_PROPERTIES_PATH = "etc/test/output/bimodal_workflow.properties";

	public BimodalCreatingEmptyWagonTest() {
		super(BimodalCreatingEmptyWagonTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
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
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @TestSpec LOGON-8478 Automate Testcase logon-1040: Creating an empty wagon
	 * @TestSpec logon-1040: create empty wagon
	 */
	@Test
	public void testCreatingAnEmptyWagon() throws FileNotFoundException, IOException {
		WagonPage wagonPage = LogonPageFactory.initElements(seleniumDriver, WagonPage.class);

		String wagonNo = wagonPage.selectFirstEmptyWagon();

		// Set setArrivalLastKnownStationDate to the past
		wagonPage.setArrivalLastKnownStationDate("23.09.2014");
		wagonPage.setArrivalLastKnownStationTime("00:00");

		wagonPage.selectLocation(getInProperty("bimodal.wagon.locationName"));

		String stationNo = wagonPage.getStationNo();

		assertTrue("StationNo doesn't get updated!!!",
				getInProperty("bimodal.wagon.expectedStationNo").equals(stationNo));

		wagonPage.selectTrack(getInProperty("bimodal.wagon.trackName"));

		wagonPage.saveWagon();
		wagonPage.expectSuccessMessageShown(true);

		saveWagonNoToPropertiesFile(wagonNo);
	}

	/**
	 * Save order number to properties file, Will be used in BimodalDeliveringRailOrder test
	 * @param orderNo
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void saveWagonNoToPropertiesFile(String wagonNo) throws FileNotFoundException,
			IOException {
		wagonNo = wagonNo.replaceAll("[\\-\\s+]", "");

		Map properties = new HashMap<String, String>();
		properties.put(BIMODAL_WORKFLOW_KEY.WAGON_NO.getKeyString(), wagonNo);

		propertyManager.storeTestRelatedProperties(properties, BIMODAL_WORKFLOW_PROPERTIES_PATH,
				true);
	}
}

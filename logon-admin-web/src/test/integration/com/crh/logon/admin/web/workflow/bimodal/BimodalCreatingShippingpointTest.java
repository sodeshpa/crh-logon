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

import com.crh.logon.admin.web.order.pages.ShippingpointResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.generalsettings.pages.ShippingPointPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.admin.web.workflow.bimodal.BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY;

/**
 * @author AdNovum Informatik AG
 * 
 * @TestSpec name="logon-1149: General settings - Creating new shippingpoint"
 * 
 * @TestSpec name="LOGON-8475: This workflow is documented in a tutorial: /share/doc/crh/logon_2_2/qa/testing tutorials/Bimodal Usecase.pdf
 */
public class BimodalCreatingShippingpointTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/BimodalShippingPointTest.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/workflow.properties";
	
	private final static String BIMODAL_WORKFLOW_PROPERTIES_PATH = "etc/test/output/bimodal_workflow.properties";

	public BimodalCreatingShippingpointTest() {
		super(BimodalCreatingShippingpointTest.class.getSimpleName(), IN_PROPERTIES_PATH,
				OUT_PROPERTIES_PATH);
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
	public void testCreateShippingPointBimodalWorkflow() throws FileNotFoundException, IOException {
		Properties properties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		
		ShippingPointPage shippingPointPage = LogonPageFactory.initElements(seleniumDriver,
				ShippingPointPage.class);

		ShippingpointResult result = shippingPointPage.createShippingPoint(properties);
		shippingPointPage.fillShippingPointDetailForBimodal(properties);
		shippingPointPage.saveShippingPointDetail();
		
		saveShippingpointNoToPropertyFile(result);
	}
	
	private void saveShippingpointNoToPropertyFile (ShippingpointResult shippingpointResult) throws FileNotFoundException, IOException {
		Map properties = new HashMap<String, String>();

		properties.put(BIMODAL_WORKFLOW_KEY.SHIPPING_POINT_SAP_NO.getKeyString(), shippingpointResult.getShippingPointSapNo());
		properties.put(BIMODAL_WORKFLOW_KEY.SHIPPING_POINT_NAME.getKeyString(),
				shippingpointResult.getShippingPointName());

		propertyManager.storeTestRelatedProperties(properties, BIMODAL_WORKFLOW_PROPERTIES_PATH,
				false);
	}
}

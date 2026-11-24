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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.order.pages.BimodalOrderResult;
import com.crh.logon.admin.web.order.pages.OrderPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.admin.web.workflow.bimodal.BimodalWorkflowTestProperties.BIMODAL_WORKFLOW_KEY;

/**
 * 
 * @author AdNovum Informatik AG
 * 
 * @TestSpec name="logon-1041: Creating bimodal-road order"
 * 
 * @TestSpec name="LOGON-8477: This workflow is documented in a tutorial: /share/doc/crh/logon_2_2/qa/testing tutorials/Bimodal Usecase.pdf"
 */
public class BimodalCreatingRoadOrderTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/BimodalOrderTemplateTest.properties";

	private final static String SHIPPINGPOINT_PROPERTIES_PATH = "etc/test/input/BimodalShippingPointTest.properties";

	private final static String BIMODAL_WORKFLOW_PROPERTIES_PATH = "etc/test/output/bimodal_workflow.properties";

	public BimodalCreatingRoadOrderTest() {
		super(BimodalCreatingRoadOrderTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
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

	@Test
	public void testCreatingBimodalRoadOrder() throws FileNotFoundException, IOException {
		OrderPage orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		orderPage.selectShipto(getInProperty("bimodal.roadPart.shiptoName"));

		BimodalOrderResult result = orderPage.createBimodalOrderWithTemplate(
				propertyManager.getTestRelatedStoredProperties(IN_PROPERTIES_PATH),
				getProperty(BIMODAL_WORKFLOW_PROPERTIES_PATH,
						BIMODAL_WORKFLOW_KEY.SHIPPING_POINT_NAME.getKeyString()));

		saveCreatedOrderNumberToPropertiesFile(result);
	}

	/**
	 * Save order number to properties file, Will be used in BimodalDeliveringRailOrder test
	 * @param orderResult
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void saveCreatedOrderNumberToPropertiesFile(BimodalOrderResult orderResult)
			throws FileNotFoundException, IOException {
		Map properties = new HashMap<String, String>();

		properties.put(BIMODAL_WORKFLOW_KEY.CUST_ORDER_NO.getKeyString(), orderResult.getCustNo());
		properties.put(BIMODAL_WORKFLOW_KEY.RAIL_PART_ORDER_NO.getKeyString(),
				orderResult.getRailDeliveryOrderNo());
		properties.put(BIMODAL_WORKFLOW_KEY.ROAD_PART_ORDER_NO.getKeyString(),
				orderResult.getRoadDeliveriesOrdersNo().get(0)); // save enough for later test
		properties.put(BIMODAL_WORKFLOW_KEY.DELIVERY_FROM_DATE.getKeyString(),
				orderResult.getDeliveryFromDate());

		propertyManager.storeTestRelatedProperties(properties, BIMODAL_WORKFLOW_PROPERTIES_PATH,
				true);
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.road.ch.parallel.singleorder;

import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * CreateChParallelOrderTest: creates an order, creates a truck (test-pda-workflow-part0)
 * DispatchChParallelOrderTest: dispatches to the truck created by the CreateChParallelOrderTest (test-pda-workflow-part1)
 * SOAP UI test: integration-tests-soapui-projects will wait for the PDA message DispatchChParallelOrderTest triggers (test-pda-workflow-part2) 
 * VerifyChParallelOrderTest: loads call center, and checks that the order is in status FINISHED (test-pda-workflow-part3)
 * 
 * All four tests use TestDataHelper.WORKFLOW_TEST_PROPERTIES_PATH to store the properties they want to give further.
 * 
 * See the test steps and their timing in /logon/build-test.xml
 * See targets test-pda-workflow-part0, test-pda-workflow-part1, test-pda-workflow-part2, test-pda-workflow-part3 in
 * build-test.xml in logon-admin-web
 * 
 * Note: The order can be in status finished in case it successfully went through all these tests,
 * so in case this test fails, look for an error in either of them.
 * Note: This test has not been migrated to use the PageObjectPattern. Please 
 * don't use it as a blueprint for new tests. See also LOGON-8463.
 * 
 * @author AdNovum Informatik AG
 */

public class VerifyChParallelRoadOrderTest extends PropertyTransferTest {
	/**
	 * @param className
	 */
	public VerifyChParallelRoadOrderTest() {
		super(VerifyChParallelRoadOrderTest.class.getSimpleName(), null, null);
	}

	@Test
	public void testVerifyOrder() throws Exception {
			/*
			final String orderNr = getTestWorkflowProperty(KEY_ORDER_NO);
			
			searchAndSelectFirstOrder(
					getTestWorkflowProperty(KEY_ORDER_DELIVERY_ROAD), 
					getTestWorkflowProperty(KEY_CALL_CENTER), 
					getTestWorkflowProperty(KEY_FINISHED), 
					orderNr, null, null,null, -1);
/*						
//			selenium.open("logon/order/bestellung/BestellungShipToEntry.do");
//			
//			//			selenium.waitForPageToLoad();
//			
//			if (selenium.isElementPresent("j_username")) {
//				selenium.type("j_username", TestDataHelper.LOGIN_NAME);
//				selenium.type("j_password", TestDataHelper.LOGIN_PASSWORD);
//				selenium.click("submit");
//				selenium.waitForPageToLoad();
//			}
//			
//			selenium.setTimeout("300000");
//			selenium.open("logon/order/bestellung/BestellungShipTo.do?soldtoNr=" + TestDataHelper.STANDARD_TEST_SOLDTO_SAP_ID + "&shiptoNr=" + TestDataHelper.STANDARD_TEST_SHIPTO_SAP_ID + "&sareaId=" + TestDataHelper.STANDARD_TEST_SAREA_CD);
//			//			selenium.waitForPageToLoad("300000");
//			
//			selenium.waitForElement("//a[contains(@href, 'OrderDetailEntry.do?orderid=') and text()='" + orderNr + "']");
//			
//			String orderStatusString = selenium.getHtmlSource();
//			orderStatusString = orderStatusString.substring(orderStatusString.indexOf(orderNr));
//			orderStatusString = orderStatusString.substring(orderStatusString.indexOf("<td>"));
//			orderStatusString = orderStatusString.substring(0, orderStatusString.indexOf("</td>"));
//
//			
//			Assert.assertEquals(true, orderStatusString.contains(orderStatus));
 */
	}

	@Test
	public void testOrderSAPData() throws Exception {
			/*
			final String orderNr = getTestWorkflowProperty(KEY_ORDER_NO);
			final String truckPlateNo = getTestWorkflowProperty(KEY_TRUCK_PLATE_NO);
		
			Assert.assertNotNull("Could not load properties to test", orderNr);
			Assert.assertNotNull("Could not load properties to test", truckPlateNo);
			
			super.loginToLogon("/logon/admin/test/ManualPDATriggerEntry.do");
			
			selenium.click("//ul[@id='subnavigation']/li[1]/a/span/span");
			selenium.waitForPageToLoad("30000");
			selenium.type("orderDeliveryWebService.orderNo", orderNr);
			selenium.click("//input[@value='get']");
			selenium.waitForElement("beanValue(OrderSAPDataBean:LogonDeliveryNumber)");
			
			
			Assert.assertEquals(orderNr, selenium.getValue("beanValue(OrderSAPDataBean:LogonDeliveryNumber)"));
			
			Assert.assertEquals("OUT", selenium.getValue("beanValue(OrderSAPDataBean:DistributionDestination)"));
			Assert.assertEquals(Constant.TRANSPORTATION_MODE_TRUCK, selenium.getValue("beanValue(OrderSAPDataBean:TransportationMode)"));
			
			Assert.assertTrue(!selenium.getValue("beanValue(OrderSAPDataBean:TransportationMode)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderSAPDataBean:Division)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderSAPDataBean:DistributionChannel)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderSAPDataBean:SalesOrganization)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderSAPDataBean:SupplyPlantNotInventoryManaged)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderSAPDataBean:FreeOfChargeFlag)").equals(""));
			Assert.assertEquals(truckPlateNo, selenium.getValue("beanValue(OrderSAPDataBean:TruckPlateNumber)"));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderSAPDataBean:KilometersByRoad)").equals("0"));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:ItemNumber)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:Plant)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:Plant)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:ShippingPoint)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:StorageLocation)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:Quantity)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:SapMaterialNumber)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(PartnerFunctionBeans{0}:PartnerFunction)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(PartnerFunctionBeans{0}:PartnerNumber)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(PartnerFunctionBeans{1}:PartnerNumber)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(PartnerFunctionBeans{1}:PartnerNumber)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(PartnerFunctionBeans{2}:PartnerFunction)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(PartnerFunctionBeans{2}:PartnerNumber)").equals(""));
			
			Assert.assertEquals("SH", selenium.getValue("beanValue(PartnerFunctionBeans{0}:PartnerFunction)"));
			Assert.assertEquals("SP", selenium.getValue("beanValue(PartnerFunctionBeans{1}:PartnerFunction)"));
			Assert.assertEquals("BP", selenium.getValue("beanValue(PartnerFunctionBeans{2}:PartnerFunction)"));
			Assert.assertEquals("CR", selenium.getValue("beanValue(PartnerFunctionBeans{3}:PartnerFunction)"));
	
			Assert.assertTrue(!selenium.getValue("beanValue(PartnerFunctionBeans{3}:PartnerNumber)").equals(""));
			Assert.assertTrue(!selenium.getValue("beanValue(SalesTextsBeans{0}:LanguageKey)").equals(""));
		
			Assert.assertEquals("TO", selenium.getValue("beanValue(OrderDeliveryItemBeans{0}:UnitOfMeasure)"));
			
//			selenium.click("callOrderDeliveryWebservice");
//			selenium.waitForElement("css=div[class=x-window-body]:contains('OrderDeliveryWebservice called')");
 */
	}
}

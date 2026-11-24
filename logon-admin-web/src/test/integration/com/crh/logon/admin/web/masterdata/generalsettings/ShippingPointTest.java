/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.generalsettings;

import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.generalsettings.pages.ShippingPointPage;
import com.crh.logon.admin.web.masterdata.pdarfid.pages.RFIDTagsPage;
import com.crh.logon.admin.web.order.pages.ShippingpointResult;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Creates a shippingpoint according to the specified properties according to the constant fields.
 * 
 * Before creating it it has to delete from the DB the selenium shippingpoint having the SEL1 sap
 * number, otherwise the creation fails. Related loadingpoints are also deleted before the test.
 * 
 * After the test also deleting the shippingpoint, in order to make it sure that no one ever selects
 * and uses the shippingpoint created here for executing an order -> it would be difficult to delete
 * the shippingpoint if it is used already.
 * 
 * @author AdNovum Informatik AG
 */
public class ShippingPointTest extends PropertyTransferTest {

	private String rfidName;

	private String rfidIdentity;

	private final static String IN_PROPERTIES_PATH = "etc/test/input/ShippingPointTest.properties";

	@Before
	public void login() throws Exception {
		loginToLogonAsAdminDE();
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();

	}

	public ShippingPointTest() {
		super(ShippingPointTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
		String fasttime = "_" + DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH) + "_"
				+ DateTimeUtils.formatHoursMinutes(new Date());
		this.rfidIdentity = fasttime;
		this.rfidName = fasttime.replaceAll("[^0-9]", "").substring(0, 10);
	}

	@Test
	public void testShippingPoint() throws Exception {
		Properties shippingPointProp = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		createNewRFID(shippingPointProp);
		
		ShippingPointPage shippingpointPage = LogonPageFactory.initElements(seleniumDriver,
				ShippingPointPage.class);
		ShippingpointResult result = shippingpointPage.createShippingPoint(shippingPointProp);
		
		shippingPointProp.put(ShippingPointKey.SHIPPING_POINT_SAP_NO.getKeyString(), result.getShippingPointSapNo());
		shippingPointProp.put(ShippingPointKey.SHIPPING_POINT_NAME.getKeyString(), result.getShippingPointName());

		// edit shipping point properties, add and remove a loading point:
		editShippingPoint(shippingPointProp);
	}

	/**
	 * Create a new RFID.
	 * @param shippingPointProp 
	 */
	private void createNewRFID(Properties shippingPointProp) {
		RFIDTagsPage rfidTagsPage = LogonPageFactory.initElements(seleniumDriver,
				RFIDTagsPage.class);
		rfidTagsPage.addNewRfidTag();
		rfidTagsPage.fillNewRfidInfo(rfidIdentity, rfidName, true);
		rfidTagsPage.saveNewRfid();
	}

	/**
	 * Edit the fields of a shipping point and add/remove a loading point.
	 */
	private void editShippingPoint(Properties shippingPointProp) {
		ShippingPointPage shippingpointPage = LogonPageFactory.initElements(seleniumDriver,
				ShippingPointPage.class);
		shippingpointPage.editShippingPoint(shippingPointProp);
		shippingpointPage.createLoadingPoint(shippingPointProp, rfidName);
		shippingpointPage.deleteShippingPoint();
	}
}
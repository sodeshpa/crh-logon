/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.road;

import java.util.Date;
import java.util.Locale;

import com.crh.logon.admin.web.pages.CarrierPreSelectionPage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.road.pages.ExternalMaterialPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class ExternalMaterialTest extends LogonSeleniumTestCaseBase {
	
	@Before
	public void login() throws Exception {
		loginToLogonAsAdminDE();
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}
	
	/**
	 * Creates an external material.
	 */
	@Test
	public void testCreateExternalMaterial() {
		ExternalMaterialData extMatData = new ExternalMaterialData();
		
		CarrierPreSelectionPage carrierPreSelectionPage =  LogonPageFactory.initElements(seleniumDriver, CarrierPreSelectionPage.class);
		
		ExternalMaterialPage externalMaterialPage = carrierPreSelectionPage.selectCarrier(extMatData.carrier, ExternalMaterialPage.class);
		externalMaterialPage.enterMaterialName(extMatData.name);
		externalMaterialPage.enterComment(extMatData.comment);
		externalMaterialPage.selectInputActive(extMatData.active.booleanValue());
		
		externalMaterialPage.clickSaveButton();
		externalMaterialPage.expectSuccessMessageShown(true);
		
		String extMatID = externalMaterialPage.getExternalMaterialId();
		
		// verify that the ID is an integer:
		try {
			Integer.parseInt(extMatID);
		}
		catch (NumberFormatException e) {
			Assert.fail("Material ID expected!");
		}
	}
	
	private class ExternalMaterialData {
		String uniq = DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH) + " " + 
					  DateTimeUtils.formatHoursMinutes(new Date());
		public String carrier = "carrier -11";
		public String name = "Selenium Material " + uniq;
		public Boolean active = Boolean.TRUE;
		public String comment = "This is the comment for the " + this.name;
	}
}

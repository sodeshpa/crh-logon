/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.road;

import java.util.Date;
import java.util.Locale;

import com.crh.logon.admin.web.masterdata.road.pages.ExternalSitePage;
import com.crh.logon.admin.web.pages.CarrierPreSelectionPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class ExternalSiteTest extends LogonSeleniumTestCaseBase {
	
	@Before
	public void login() throws Exception {
		loginToLogonAsAdminDE();
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}
	
	/**
	 * Creates an external site.
	 */
	@Test
	public void testCreateExternalSite() {
		ExternalSiteData extSiteData = new ExternalSiteData();
		CarrierPreSelectionPage carrierPreSelectionPage =  LogonPageFactory.initElements(seleniumDriver, CarrierPreSelectionPage.class);
		
		ExternalSitePage externalSitePage = carrierPreSelectionPage.selectCarrier(extSiteData.carrier, ExternalSitePage.class);
		
		
		externalSitePage.enterStreetHouseNo(extSiteData.street + " " + extSiteData.houseNumber);
		externalSitePage.enterSitePostalCode(extSiteData.postalCode);
		externalSitePage.enterSiteCity(extSiteData.city);
		externalSitePage.enterSiteCountry(extSiteData.country);
		
		if (extSiteData.coordLong == null || extSiteData.coordLat == null ) {
			externalSitePage.clickSetAddressButton();
		} else {
			externalSitePage.setAddress(extSiteData.coordLong, extSiteData.coordLat);
		}
		
		externalSitePage.enterSiteName(extSiteData.name);
		externalSitePage.selectActiveFlag(extSiteData.active.booleanValue());

		
		externalSitePage.enterSiteLoadingPoint(extSiteData.loadingPoint);
		externalSitePage.enterSiteContactPerson(extSiteData.contactPerson);
		externalSitePage.enterRadius(extSiteData.radius);
		externalSitePage.enterLoadTimeMin(extSiteData.loadingDuration);
		externalSitePage.enterTruckDriverNote(extSiteData.comment);
		
		externalSitePage.clickSaveButton();
		externalSitePage.expectSuccessMessageShown(true);
		
		String extSiteID = externalSitePage.getSiteId();
		
		// verify that the ID is an integer:
		try {
			Integer.parseInt(extSiteID);
		}
		catch (NumberFormatException e) {
			Assert.fail("Site ID expected!");
		}
	}

	
	private class ExternalSiteData {
		String uniq = DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH) + " " + 
					  DateTimeUtils.formatHoursMinutes(new Date());
		public String carrier = "carrier -11"; 
		public String name = "Selenium External site " + uniq;
		public Boolean active = Boolean.TRUE;
		public String loadingPoint = "";
		public String street = "Budapester";
		public String houseNumber = "18";
		public String postalCode = "18055";
		public String city = "Rostock";
		public String country = "DE";
		public String contactPerson = "Selenium Person";
		public String radius = "20";
		public String loadingDuration = "25";
		public String coordLong = "47.31";
		public String coordLat = "8.47";
		public String comment = "This is the comment for the " + this.name;
	}
}

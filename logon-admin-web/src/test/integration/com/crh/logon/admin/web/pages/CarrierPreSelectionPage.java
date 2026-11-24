/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.masterdata.road.pages.ExternalMaterialPage;
import com.crh.logon.admin.web.masterdata.road.pages.ExternalSitePage;
import com.crh.logon.admin.web.masterdata.road.pages.TrailerPage;
import com.crh.logon.admin.web.masterdata.road.pages.TruckPage;

/**
 * 
 * @author AdNovum Informatik AG
 */
public class CarrierPreSelectionPage extends LogonBasePage {
	private final String PRE_CARRIER_SELECTION_URL_FOR_EXTERNAL_MATERIAL_ADMIN_PAGE = 
			TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/admin/externalmaterialadmin/ExternalMaterialAdminEntry.do";
	
	private final String PRE_CARRIER_SELECTION_URL_FOR_EXTERNAL_SITE_ADMIN_PAGE = 
			TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/admin/externalsiteadmin/ExternalSiteAdminEntry.do";
	
	private final String PRE_CARRIER_SELECTION_URL_FOR_TRUCK_ADMIN_PAGE = 
			TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/admin/truckadmin/TruckAdminEntry.do";
	
	private final String PRE_CARRIER_SELECTION_URL_FOR_TRAILER_ADMIN_PAGE = 
			TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/admin/traileradmin/TrailerAdminEntry.do";
	
	public CarrierPreSelectionPage(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void verifyCorrectPage() {
		// 24-NOV-2014/nhattan: dynamic
	}
	
	/**
	 * In some current MasterData page, if we want to edit or add new a data entry, we have to go the
	 * page for select carrier first, after that it will be redirected to the final page. So the purpose
	 * is don't want to separate to two pages for each of those</br></br>
	 * 
	 * Example: </br>
	 * <b>TruckAdminEntry.do</b> for selection carrier and <br>
	 * <b>TruckAdmin.do</b> for add new truck or edit truck
	 * 
	 * @param text
	 * @param pageClass
	 * @return
	 */
	public <P extends LogonBasePage> P selectCarrier(String text, Class<P> pageClass) {
		String URL = getCarrierSelectionUrl(pageClass);
		this.driver.get(URL);
		
		WebElement carrierElement = driver.findElement(By.id("carrierId"));
		
		Assert.assertNotNull(carrierElement);
		
		this.selectComboboxByVisibleText(carrierElement, text);
		
		P page = LogonPageFactory.initElements(driver, pageClass);
		return page;
	}
	
	private <P extends LogonBasePage> String getCarrierSelectionUrl(Class<P> pageClass){
		if(pageClass == ExternalMaterialPage.class){
			return PRE_CARRIER_SELECTION_URL_FOR_EXTERNAL_MATERIAL_ADMIN_PAGE;
		} else if(pageClass == ExternalSitePage.class){
			return PRE_CARRIER_SELECTION_URL_FOR_EXTERNAL_SITE_ADMIN_PAGE;
		} else if(pageClass == TruckPage.class){
			return PRE_CARRIER_SELECTION_URL_FOR_TRUCK_ADMIN_PAGE;
		} else if(pageClass == TrailerPage.class) {
			return PRE_CARRIER_SELECTION_URL_FOR_TRAILER_ADMIN_PAGE;
		} else {
			Assert.fail(pageClass.getName());
			return "";
		}
	}
}

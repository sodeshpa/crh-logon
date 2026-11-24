/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.road.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;

/**
 * @author AdNovum Informatik AG
 */
public class ExternalSitePage extends LogonBasePage {
	@FindBy(id = "siteName")
	private WebElement siteName;

	@FindBy(id = "siteStreeHouseNo")
	private WebElement siteStreeHouseNo;

	@FindBy(id = "sitePostalCode")
	private WebElement sitePostalCode;

	@FindBy(id = "siteCity")
	private WebElement siteCity;

	@FindBy(id = "siteCountry")
	private WebElement siteCountry;

	@FindBy(name = "activeFlag")
	private WebElement activeFlag;
	
	@FindBy(xpath = "//input[contains(@onclick, 'setAddress')]")
	private WebElement setAddressButton;
	
	@FindBy(id = "coordLon")
	private WebElement coordLonInput;
	
	@FindBy(id = "coordLat")
	private WebElement coordLatInput;
	
	@FindBy(id = "siteLoadingPoint")
	private WebElement siteLoadingPoint;
	
	@FindBy(id = "siteContactPerson")
	private WebElement siteContactPerson;
	
	@FindBy(id = "radius")
	private WebElement radius;

	@FindBy(id = "loadTimeMin")
	private WebElement loadTimeMin;
	
	@FindBy(id = "truckDriverNote")
	private WebElement truckDriverNote;
	
	@FindBy(id = "saveButton")
	private WebElement saveButton;

	public ExternalSitePage(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void verifyCorrectPage() {
		WebElement currentTabLink = driver.findElement(
				By.xpath("//a[contains(@href, 'ExternalSiteAdminEntry.do')]"));
		Assert.assertNotNull(currentTabLink);
		Assert.assertEquals("highlight", currentTabLink.getAttribute("class"));
	}
	
	public void enterSiteName(String value){
		siteName.clear();
		siteName.sendKeys(value);
	}

	public void enterStreetHouseNo(String value) {
		siteStreeHouseNo.clear();
		siteStreeHouseNo.sendKeys(value);
	}

	public void enterSitePostalCode(String value) {
		sitePostalCode.clear();
		sitePostalCode.sendKeys(value);
	}

	public void enterSiteCity(String value) {
		siteCity.clear();
		siteCity.sendKeys(value);
	}

	public void enterSiteCountry(String value) {
		siteCountry.clear();
		siteCountry.sendKeys(value);
	}
	
	public void enterSiteLoadingPoint(String value) {
		siteLoadingPoint.clear();
		siteLoadingPoint.sendKeys(value);
	}
	
	public void enterSiteContactPerson(String value) {
		siteContactPerson.clear();
		siteContactPerson.sendKeys(value);
	}
	
	public void enterRadius(String value) {
		radius.clear();
		radius.sendKeys(value);
	}
	
	public void enterLoadTimeMin(String value) {
		loadTimeMin.clear();
		loadTimeMin.sendKeys(value);
	}
	
	public void enterTruckDriverNote(String value) {
		truckDriverNote.clear();
		truckDriverNote.sendKeys(value);
	}

	public void selectActiveFlag(boolean isSelect) {
		super.selectCheckBox(activeFlag, isSelect);
	}
	
	public void setAddress(String coordLong, String coorLat) {
		coordLonInput.clear();
		coordLonInput.sendKeys(coordLong);
		
		coordLatInput.clear();
		coordLatInput.sendKeys(coorLat);
	}
	
	public void clickSetAddressButton() {
		setAddressButton.click();
	}

	public void clickSaveButton() {
		saveButton.click();
	}
	
	/**
	 * Extract the ID 'xyz' of the newly added site from its link onclick="selectSite(xyz)":
	 * @return external site id
	 */
	public String getSiteId(){
		WebElement link = driver.findElement(By.xpath("//tr[@class='selected']/td/a"));
		Assert.assertNotNull(link);
		
		String attr = link.getAttribute("onclick");
		
		String extMatID = attr.replaceAll(".*\\(|\\).*", "");
		
		return extMatID;
	}
}

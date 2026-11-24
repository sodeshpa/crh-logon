/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.pages;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoPage extends LogonBasePage {
	
	@FindBy(xpath = "//a[@id='TabShipTo']")
	protected WebElement generalTabLink;
	
	@FindBy(xpath = "//a[@id='TabSalesArea0']")
	protected WebElement salesAreaTabLink;

	@FindBy(xpath = "//a[@id='TabSalesArea1']")
	protected WebElement salesAreaTabLink1;
	
	@FindBy(xpath = "//form[1]/table/tbody/tr/td[6]/b")
	protected WebElement shiptoNameTextField;
	
	@FindBy(xpath = "//input[contains(@onclick, 'submitWithAction') and position()=2]")
	protected WebElement saveButton;
	
	public ShiptoPage(WebDriver driver) {
		super(driver);
	}

	@Override
	public void verifyCorrectPage() {
		assertTrue("Subnavigation bar is NOT visible", isElementPresentAndVisible(subnavigation));
		String activeSubnavigation = subnavigation.findElement(By.className("highlight")).getText();
		assertThat("ShipTo sub-navigation is highlighted", activeSubnavigation, containsString("Ship To"));
	}
	
	public boolean isShiptoName(String shiptoName){
		return shiptoNameTextField.getText().contains(shiptoName);
	}
	
	public ShiptoPage saveSetting(){
		saveButton.click();
		return LogonPageFactory.initElements(driver, ShiptoPage.class);
	}
}

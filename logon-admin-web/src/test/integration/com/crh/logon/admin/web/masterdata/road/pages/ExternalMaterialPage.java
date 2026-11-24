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
public class ExternalMaterialPage extends LogonBasePage {
	
	@FindBy(id = "materialNameId")
	private WebElement materialNameInput;
	

	@FindBy(id = "inputCommentId")
	private WebElement commentInput;
	
	@FindBy(name = "inputActive")
	private WebElement inputActiveCheckbox;
	
	@FindBy(name = "saveButton")
	private WebElement saveButton;
	
			
	public ExternalMaterialPage(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void verifyCorrectPage() {
		String currentUrl = driver.getCurrentUrl();
		Assert.assertTrue(currentUrl.contains("ExternalMaterialAdmin.do"));
	}
	
	public void enterMaterialName(String value) {
		materialNameInput.clear();
		materialNameInput.sendKeys(value);
	}
	
	public void enterComment(String value) {
		commentInput.clear();
		commentInput.sendKeys(value);
	}
	
	public void selectInputActive(boolean isSelect){
		super.selectCheckBox(inputActiveCheckbox, isSelect);
	}
	
	public void clickSaveButton(){
		saveButton.click();
	}
	
	/**
	 * Extract the ID 'xyz' of the newly added material from its link onclick="selectMaterial(xyz)":
	 * @return external material id
	 */
	public String getExternalMaterialId(){
		WebElement link = driver.findElement(By.xpath("//tr[@class='selected']/td/a"));
		Assert.assertNotNull(link);
		
		String attr = link.getAttribute("onclick");
		
		String extMatID = attr.replaceAll(".*\\(|\\).*", "");
		
		return extMatID;
	}
}

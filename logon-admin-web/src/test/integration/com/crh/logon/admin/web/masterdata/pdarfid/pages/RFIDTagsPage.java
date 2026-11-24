/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.pdarfid.pages;

import java.util.Locale;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.business.services.utils.Message;

public class RFIDTagsPage extends LogonBasePage {

	public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl()
			+ "/logon/admin/rfidadmin/RFIDAdmin.do";

	/**
	 * @param driver
	 */
	public RFIDTagsPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		// TODO Auto-generated method stub
	}

	/**
	 * Click on add new rfid tag button
	 */
	public void addNewRfidTag() {
		WebElement buttonNew = driver.findElement(By.id("buttonNew"));
		Assert.assertNotNull(buttonNew);
		buttonNew.click();
	}

	/**
	 * Fill new rfid information
	 * @param rfidIdentity rfid identify
	 * @param rfidName rfid name
	 * @param isActive true if you want rfid is activated
	 */
	public void fillNewRfidInfo(String rfidIdentity, String rfidName, boolean isActive) {
		inputTextField("rfidIdentity", rfidIdentity);
		inputTextField("name", rfidName);
		if (isActive) {
			enableActiveCheckbox();
		}
	}

	/**
	 * Enable the active check box
	 */
	public void enableActiveCheckbox() {
		WebElement activeCheckbox = driver.findElement(By.id("active"));
		Assert.assertNotNull(activeCheckbox);
		activeCheckbox.click();
	}

	/**
	 * save new rfid information
	 */
	public void saveNewRfid() {
		clickAndExpectSuccessMsg(By.id("buttonSave"),
				Message.lookup(Locale.GERMAN, "rfidadmin.info.saved"));
	}
}

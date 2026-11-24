/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.admin.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.util.Locale;
import java.util.Properties;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.util.LogonSeleniumUtils;
import com.crh.logon.admin.web.workflow.road.ch.parallel.singleorder.DispatchSingleChParallelRoadOrderTest;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * @author AdNovum Informatik AG
 */
// TODO 5-Nov-2014/nhattan: temporary apply Page Object Pattern, split to smaller function with more specific component and re-usable 
public class UserAdminPage  extends LogonBasePage {
	private final String URL = TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/admin/useradmin/UserAdminEntry.do";
	private static boolean PASSWORD_STRENGTH_VALIDATION_ENABLED = Boolean.parseBoolean(TestProperties.INSTANCE.getCustomProperty("password.strength.validation.enabled"));
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispatchSingleChParallelRoadOrderTest.class);
	
	@FindBy(id = "searchFirstNameOrName")
	private WebElement searchFirstNameOrName;
	

	@FindBy(id = "searchHolspaceOrExtLogin")
	private WebElement searchHolspaceOrExtLogin;
	
	@FindBy(id = "newUser")
	private WebElement newUserButton;
	
	@FindBy(id = "userType")
	private WebElement userTypeCombo;

	/**
	 * @param driver
	 */
	public UserAdminPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	@Override
	protected void verifyCorrectPage() {
		Assert.assertEquals("", searchFirstNameOrName.getText());
		Assert.assertEquals("", searchHolspaceOrExtLogin.getText());
		Assert.assertNotNull(newUserButton);
	}

	public void clickNewUserButton(){
		newUserButton.click();
		waitForLoading(driver);
	}
	
	public void selectUserType(Properties properties, int userType) {
		String value = properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.CRH_USER_TYPE_LABEL.getKeyString());
		
		if (userType == Integer.valueOf(Constant.USER_TYPE_CUSTOMER)) {
			value = properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.CUSTOMER_USER_TYPE_LABEL.getKeyString());
		}
		else if (userType == Integer.valueOf(Constant.USER_TYPE_VENDOR)) {
			value = properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.VENDO_USER_TYPE_LABEL.getKeyString());
		}

		new AdNWebDriverWait(driver, 20).until(visibilityOfElementLocated(By.id("userType")));

		Select select = new Select(userTypeCombo);
		
		select.selectByVisibleText(value);
		
		WebElement button = driver.findElement(By.xpath("//input[@value='Ok']"));
		button.click();
		waitForLoading(driver);
	}
	
	public void checkForActive(){
		WebElement activeCheckbox = driver.findElement(By.id("userDetailsPanel_aktiv"));
		Assert.assertNotNull(activeCheckbox);
		
		if(!activeCheckbox.isSelected()){
			activeCheckbox.click();
		}
	}
	
	protected void createUser(Properties properties, int userType, String roleLabel) {
		this.clickNewUserButton();
		this.selectUserType(properties, userType);
		this.checkForActive();
		
		this.fillBasicInfo(properties);
		this.fillExtInfo(properties, userType);
		
		this.setRole(roleLabel);
		
		this.saveUser(properties);
	}

	/**
	 * @param properties
	 */
	public void saveUser(Properties properties) {
		String successMessage = Message.lookup(Locale.GERMAN, "message.successUpd");
		LOG.debug("Save the user and expect success message '" + successMessage + "'");
		
		clickAndExpectSuccessMsg(
			By.xpath("//input[@value='Speichern']"),
			successMessage
		);
		
		String detailInformationText = Message.lookup(Locale.GERMAN,
				"useradmin.texte.detailinformation");
		LOG.debug("Verify correct title '" + detailInformationText + " <firstname> <surname>'");
		String title = driver.findElement(By.xpath("//form[@id='admin_UserAdminFormUserAdmin']/table/tbody/tr/th")).getText();
		
		Assert.assertTrue(title.contains(detailInformationText));
		Assert.assertTrue(title.contains(properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.FIRSTNAME.getKeyString())));
		Assert.assertTrue(title.contains(properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.SURNAME.getKeyString())));
	}

	/**
	 * @param roleLabel
	 */
	public void setRole(String roleLabel) {
		LOG.debug("Add a role (clicking on Hinzufügen opens a popup menu with a second Hinzufügen button)");
		driver.findElement(By.xpath("//input[@value='Hinzuf\u00fcgen']")).click();
		Select roleSelect = new Select(driver.findElement(By.id("selectedRoleIds")));
		roleSelect.selectByVisibleText(roleLabel);
		driver.findElement(By.xpath("//input[@value='Hinzuf\u00fcgen']")).click();
		// If the role is already pre-selected (e.g. for customers), we wouldn't wait enough just by verifying the role selection below.
		LogonSeleniumUtils.waitForLoading(driver);
		
		LOG.debug("Verify that the role appears in the list");
		driver.findElement(By.xpath("//div[@id='DivRoleSelection']//label[text()[contains(., '" + roleLabel + "')]] "));
	}

	/**
	 * @param properties
	 */
	public void fillBasicInfo(Properties properties) {
		// Generate firstname, holspace, email
		String timestamp = Long.toString(System.currentTimeMillis());
		properties.setProperty(UserAdminTestProperties.USER_ADMIN_KEY.FIRSTNAME.getKeyString(), properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.FIRSTNAME.getKeyString()) + timestamp);
		properties.setProperty(UserAdminTestProperties.USER_ADMIN_KEY.HOLSPACE.getKeyString(), properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.HOLSPACE.getKeyString()) + timestamp);
		properties.setProperty(UserAdminTestProperties.USER_ADMIN_KEY.EMAIL.getKeyString(), properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.EMAIL.getKeyString()) + timestamp);
		
		fillElement("userDetailsPanel_firstname", properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.FIRSTNAME.getKeyString()));
		fillElement("userDetailsPanel_name", properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.SURNAME.getKeyString()));
		fillElement("userDetailsPanel_ldapHolspace", properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.HOLSPACE.getKeyString()));
		fillElement("userDetailsPanel_email", properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.EMAIL.getKeyString()));
		fillElement("userDetailsPanel_phoneNr", properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.PHONE_NR.getKeyString()));
		fillElement("userDetailsPanel_mobileNr", properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.MOBILE_NR.getKeyString()));
	}
	
	public void fillExtInfo(Properties properties, int userType){
		Select langSelect = new Select(driver.findElement(By.id("applicationLang")));
		langSelect.selectByVisibleText("Englisch");
		
		driver.findElement(By.id("userExternalAccessPanel_extAktiv")).click();
		
		fillElement("extLoginPwdNew", properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.PWD.getKeyString()));
		fillElement("extLoginPwdNew2", properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.PWD.getKeyString()));
		
		LOG.info("Password strength validation is: " + PASSWORD_STRENGTH_VALIDATION_ENABLED);
		if (PASSWORD_STRENGTH_VALIDATION_ENABLED == true) {
			LOG.info("Click the save button and expect 'Bitte wählen sie ein anderes Passwort'");
			clickAndExpectErrorMsg(
				By.id("pwdSave"),
				Message.lookup(Locale.GERMAN, "useradmin.error.ext.pwd.policy")
			);
			
			String securePassword = properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.PWD.getKeyString()) + "A!";
			LOG.info("Add a special and an upper case letter to make the password more secure ("
					+ securePassword + ")");
			
			fillElement("extLoginPwdNew", securePassword);
			fillElement("extLoginPwdNew2", securePassword);
		}
		driver.findElement(By.id("pwdSave")).click();
		waitForLoading(driver);
		
		LOG.debug("Set the external login name to the firstname (including timestamp)");
		WebElement extLogin = driver.findElement(By.xpath("//input[@name='extLogin' and contains(@value, '" + properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.EXT_LOGIN_NAME.getKeyString()) + "')]"));
		
		extLogin.clear();
		extLogin.sendKeys(properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.FIRSTNAME.getKeyString()));
		
		if (userType == Integer.valueOf(Constant.USER_TYPE_CRH)) {
			String expectedErrorMessage = Message.lookup(Locale.GERMAN, "useradmin.errors.noRole");
			LOG.debug("Click on 'Speichern' and expect an error depending on the user type: '"
					+ expectedErrorMessage);
			clickAndExpectErrorMsg(
				By.xpath("//input[@value='Speichern']"),
				expectedErrorMessage
			);
		}
		else if (userType == Integer.valueOf(Constant.USER_TYPE_CUSTOMER)) {
			String expectedErrorMessage = Message.lookup(Locale.GERMAN,
					"useradmin.errors.missing.bpid");
			LOG.debug("Click on 'Speichern' and expect an error depending on the user type: '"
					+ expectedErrorMessage);
			clickAndExpectErrorMsg(
				By.xpath("//input[@value='Speichern']"),
				expectedErrorMessage
			);
			typeAndSelectIndexInExtCombobox("searchCustomerById", properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.CUSTOMER_SAP_ID.getKeyString()),  "1", "1");
			String customerNameXPath = "//input[@id='searchCustomerByName' and @value='" + properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.CUSTOMER_ASSOCIATE_NAME.getKeyString()) + "']";
			driver.findElement(By.xpath(customerNameXPath));
		}
		else if (userType == Integer.valueOf(Constant.USER_TYPE_VENDOR)) {
			String expectedErrorMessage = Message.lookup(Locale.GERMAN,
					"useradmin.errors.missing.vendorid");
			LOG.debug("Click on 'Speichern' and expect an error depending on the user type: '"
					+ expectedErrorMessage);
			clickAndExpectErrorMsg(
				By.xpath("//input[@value='Speichern']"),
				expectedErrorMessage
			);
			typeAndSelectIndexInExtCombobox("searchVendorById", properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.SAP_VENDOR_ID.getKeyString()),  "1", "1");
			String vendorNameXPath = "//input[@id='searchVendorByName' and @value='" + properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.CARRIER_ASSOCIATE_NAME.getKeyString()) + "']";
			driver.findElement(By.xpath(vendorNameXPath));
		}
	}
	
	/**
	 * @param carrierLabel
	 * @param spLabel
	 * @param plantCd
	 * @param carrierId
	 * @param poolLabel
	 * @param shippingPointId
	 * @param poolId
	 * @param companyCode
	 */
	public final void updateUserRestrictions(Properties properties) {
		
		final String ADD_COMPANY_CODE_XPATH = "//input[@value='Hinzuf\u00fcgen' and @type='button' and " +
			"@onclick=\"showPopup('UserAdminAuthPopupEntry.do?restrictionType=9','');\"]";
		
		final String ADD_PLANTS_XPATH = "//input[@value='Hinzuf\u00fcgen' and @type='button' and " +
			"@onclick=\"showPopup('UserAdminAuthPopupEntry.do?restrictionType=2','');\"]";
		
		final String ADD_POOLS_XPATH = "//input[@value='Hinzuf\u00fcgen' and @type='button' and " +
			"@onclick=\"showPopup('UserAdminAuthPopupEntry.do?restrictionType=4','');\"]";
		
		final String ADD_SHIPPING_POINTS_XPATH = "//input[@value='Hinzuf\u00fcgen' and @type='button' and " +
			"@onclick=\"showPopup('UserAdminAuthPopupEntry.do?restrictionType=3','');\"]";
		
		final String ADD_CARRIERS_XPATH = "//input[@value='Hinzuf\u00fcgen' and @type='button' and " +
			"@onclick=\"showPopup('UserAdminAuthPopupEntry.do?restrictionType=5','');\"]";
		
		// Zugewiesene Company Code -> 'Hinzufügen' and select company code 'AD02':
		driver.findElement(By.xpath(ADD_COMPANY_CODE_XPATH)).click();
		
		Select compCodeSelect = new Select(driver.findElement(By.id("selectedCompCodeIds")));
		compCodeSelect.selectByValue(properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.COMPANY_CODE.getKeyString()));
		driver.findElement(By.xpath("//input[@value='Hinzuf\u00fcgen']")).click();
		
		// check that the chosen company code appears in the list:
		driver.findElement( 
			By.xpath("//div[@id='DivCompCodeSareaSelection']/table[1]//table/tbody/tr/td[text()[contains(., '" + properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.COMPANY_CODE.getKeyString()) + "')]]")
		);
		
		// 'Zugewiesene Werke' -> 'Hinzufügen' and select plant 'AD CEM Plant 8101 (8101)':
		driver.findElement(By.xpath(ADD_PLANTS_XPATH)).click();
		Select plantIdSelect = new Select(driver.findElement(By.id("selectedPlantIds")));
		plantIdSelect.selectByValue(properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.PLANT_CD.getKeyString()));
		driver.findElement(By.xpath("//input[@value='Hinzuf\u00fcgen']")).click();
		
		// check that the chosen plant appears in the list:
		driver.findElement( 
			By.xpath("//div[@id='DivPlantShippingPointSelection']/table[1]//table/tbody/tr/td[text()[contains(., '" + properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.PLANT_CD.getKeyString()) + "')]]")
		);
		
		// 'Zugewiesene Pools' -> 'Hinzufügen' of pool "CH-Ost" with poolId 1
		driver.findElement(By.xpath(ADD_POOLS_XPATH)).click();
		Select poolSelect = new Select(driver.findElement(By.id("selectedPoolIds")));
		poolSelect.selectByVisibleText(properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.POOL_LABEL.getKeyString()));
		driver.findElement(By.xpath("//input[@value='Hinzuf\u00fcgen']")).click();
		
		// check that the chosen pool appears in the list:
		driver.findElement( 
			By.xpath("//div[@id='DivPoolCarrierSelection']/table[1]//table/tbody/tr/td[text()[contains(., '" + properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.POOL_LABEL.getKeyString()) + "')]]")
		);
		
		// 'Zugewiesene Versandstelle' -> 'Hinzufügen'
		driver.findElement(By.xpath(ADD_SHIPPING_POINTS_XPATH)).click();
		Select shippingPointSelect = new Select(driver.findElement(By.id("selectedShippingPointIds")));
		shippingPointSelect.selectByVisibleText(properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.SP_LABEL.getKeyString()));
		driver.findElement(By.xpath("//input[@value='Hinzuf\u00fcgen']")).click();
		
		// check that the chosen shipping point appears in the list:
		driver.findElement( 
			By.xpath("//div[@id='DivPlantShippingPointSelection']/table[2]//table/tbody/tr/td[text()[contains(., '" + properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.SP_LABEL.getKeyString()) + "')]]")
		);
		
		// 'Zugewiesene Spediteure' -> 'Hinzufügen'
		driver.findElement(By.xpath(ADD_CARRIERS_XPATH)).click();
		Select carrierSelect = new Select(driver.findElement(By.id("selectedCarrierIds")));
		carrierSelect.selectByValue(properties.getProperty(UserAdminTestProperties.USER_ADMIN_KEY.CARRIER_ID.getKeyString()));
		driver.findElement(By.xpath("//input[@value='Hinzuf\u00fcgen']")).click();
		
		// check that the chosen plant appears in the list:
		driver.findElement( 
			By.xpath("//div[@id='DivPoolCarrierSelection']/table[2]//table/tbody/tr/td[text()[contains(., '" + properties.getProperty(
					UserAdminTestProperties.USER_ADMIN_KEY.CARRIER_LABEL.getKeyString()) + "')]]")
		);
		
		// save and verify that success message pops up:
		clickAndExpectSuccessMsg(
			By.xpath("//input[@value='Speichern']"),
			Message.lookup(Locale.GERMAN, "message.successUpd")
		);
		
		// check that all delete 'x' icons are present:
		WebElement el = driver.findElement(By.xpath("//img[@onclick='useradmin.removePlant(\"" + properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.PLANT_CD.getKeyString()) + "\");']"));
		Assert.assertNotNull("'x' icons for plant is not found", el);
		
		el = driver.findElement(By.xpath("//img[@onclick='useradmin.removePool(" + properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.POOL_ID.getKeyString()) + ")']"));
		Assert.assertNotNull("'x' icons for pool is not found", el);
		
		el = driver.findElement(By.xpath("//img[@onclick='useradmin.removeCarrier(" + properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.CARRIER_ID.getKeyString()) +")']"));
		Assert.assertNotNull("'x' icons for carrier is not found", el);
		
		el = driver.findElement(By.xpath("//img[@onclick='useradmin.removeShippingPoint(\"" + properties.getProperty(
				UserAdminTestProperties.USER_ADMIN_KEY.SHIPPING_POINT_ID.getKeyString()) + "\");']"));
		Assert.assertNotNull("'x' icons for shipping point is not found", el);
	}
	
	private void fillElement(String elementId, String value){
		WebElement el = driver.findElement(By.id(elementId));
		Assert.assertNotNull(el);
		el.sendKeys(value);
	}
}

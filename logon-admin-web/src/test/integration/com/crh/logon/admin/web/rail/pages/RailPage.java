/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.rail.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.adnovum.jcan.test.selenium.util.TestProperties;
import com.crh.logon.admin.web.pages.LogonBasePage;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * @author AdNovum Informatik AG
 */
public class RailPage extends LogonBasePage {
	public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl()	+ "/logon/order/raildispo/DispoEntry.do";

	@FindBy(id = "railplantselect")
	private WebElement selectedPlantCombobox;

	@FindBy(id = "NoTrackField")
	private WebElement noTrackCheckbox;

	@FindBy(id = "btnFilterRailDispo")
	private WebElement filterButton;

	@FindBy(id = "wagonNrFilter")
	private WebElement wagonNumberFilterextField;

	@FindBy(id = "orderNrFilter")
	private WebElement orderNrFilterTextField;

	private static String SHIPPING_POINT_COMBO_INPUT_ID = "ShippingpointsField";
	private static String SHIPPING_POINT_COMBO_DIV_ID = "DivShippingpointsEdit";

	private static String TRACK_COMBO_INPUT_ID = "TracksField";
	private static String TRACK_COMBO_DIV_ID = "DivTracksEdit";

	private static String STATUS_COMBO_INPUT_ID = "StatusField";
	private static String STATUS_COMBO_DIV_ID = "DivStatusEdit";

	/**
	 * @param driver
	 */
	public RailPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}



	@Override
	protected void verifyCorrectPage() {
		Assert.assertTrue("Order page expected!", isElementPresentAndVisible(selectedPlantCombobox));
	}

	public void selectPlantByValue(String value){
		Select plantSelectBox = new Select(selectedPlantCombobox);
		plantSelectBox.selectByValue(value);
		waitForLoading(driver);
	}

	public void selectShippingpoints(boolean isSelectAll, boolean isSelectNone, String... values){
		selectComboxWithMultipleCheckedItem(SHIPPING_POINT_COMBO_INPUT_ID, SHIPPING_POINT_COMBO_DIV_ID, isSelectAll, isSelectNone, values);
	}

	public void selectStracks(boolean isSelectAll, boolean isSelectNone, String... values){
		selectComboxWithMultipleCheckedItem(TRACK_COMBO_INPUT_ID, TRACK_COMBO_DIV_ID, isSelectAll, isSelectNone, values);
	}

	public void selectStatus(boolean isSelectAll, boolean isSelectNone, String... values){
		selectComboxWithMultipleCheckedItem(STATUS_COMBO_INPUT_ID, STATUS_COMBO_DIV_ID, isSelectAll, isSelectNone, values);
	}

	public void checkNoTrack(boolean isCheck){
		if((noTrackCheckbox.isSelected() && !isCheck) || (!noTrackCheckbox.isSelected() && isCheck)){
			noTrackCheckbox.click();
		}
	}

	public void enterWagonNumber(String value){
		wagonNumberFilterextField.clear();
		wagonNumberFilterextField.sendKeys(value);
	}

	public void enterOrderNumber(String value){
		orderNrFilterTextField.clear();
		orderNrFilterTextField.sendKeys(value);
	}

	public void clickFilterButton(){
		filterButton.click();
		waitForLoading(driver);
	}


	public boolean isOrderVisible(String orderNo){
		WebElement orderRow = driver.findElement(By.id("tro" + orderNo));
		return orderRow != null;
	}

	public boolean isWagonVisible(String wagonNo){
		WebElement orderRow = driver.findElement(By.id("trw" + wagonNo));
		return orderRow != null;
	}

	public void assignOrderToWagon(String orderNo, String wagonNo){
		WebElement orderRow = driver.findElement(By.id("tro" + orderNo));
		Assert.assertNotNull(orderRow);
		orderRow.findElement(By.xpath("./td")).sendKeys(Keys.DOWN);

		WebElement wagonRow = driver.findElement(By.id("trw" + wagonNo));
		Assert.assertNotNull(wagonRow);

		(new Actions(driver)).dragAndDrop(orderRow, wagonRow).perform();
		waitForLoading(driver);
	}

	public void enterTareGross(String wagonNo, String tareValue, String grossValue){
		WebElement wagonRow = driver.findElement(By.id("trw" + wagonNo));

		WebElement tareInput = wagonRow.findElement(By.xpath(".//input[contains(@onkeyup, 'saveTara')]"));
		tareInput.clear();
		tareInput.sendKeys(tareValue);

		WebElement grossInput = wagonRow.findElement(By.xpath(".//input[contains(@onkeyup, 'saveGross')]"));
		grossInput.clear();
		grossInput.sendKeys(grossValue);

		wagonRow = driver.findElement(By.id("trw" + wagonNo));
		WebElement saveHeightButton = wagonRow.findElement(By.xpath(".//span[contains(@onclick, 'saveWeight') and @class='oksign']"));
		saveHeightButton.click();
		waitForLoading(driver);

		new AdNWebDriverWait(driver, 30).until(elementToBeClickable(By.id("buttonCancel")));
	}

	public void transmitToRailCarrier(){
		WebElement transmitButton = driver.findElement(By.id("buttonTransmit"));
		Assert.assertNotNull(transmitButton);
		Assert.assertTrue(transmitButton.isEnabled());
		transmitButton.click();
		new AdNWebDriverWait(driver, 30).until(elementToBeClickable(By.id("buttonUnloaded")));
	}

	public boolean isUnloadedButtonClickable(){
		WebElement unloadedButton = driver.findElement(By.id("buttonUnloaded"));
		return unloadedButton != null && unloadedButton.isEnabled();
	}

	public void unloaded(){
		new AdNWebDriverWait(driver, 30).until(elementToBeClickable(By.id("buttonUnloaded")));

		WebElement unloadedButton = driver.findElement(By.id("buttonUnloaded"));
		Assert.assertNotNull(unloadedButton);
		Assert.assertTrue(unloadedButton.isEnabled());
		unloadedButton.click();
	}

}

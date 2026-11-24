/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.road;

import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import com.crh.logon.admin.web.pages.CarrierPreSelectionPage;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.masterdata.road.pages.TruckPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class TruckTest extends PropertyTransferTest {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/BulkPowerTruck.properties";

	public TruckTest() {
		super(TruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * @TestSpec name="LOGON-8458 Automate Testcase logon 823: Create a Real truck - transport category bulk powder"
	 * @TestSpec name="logon-823 Create a Real truck - transport category bulk powder"
	 */
	@Test
	public void testCreateBulkPowerTruck() {
		Properties properties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		CarrierPreSelectionPage carrierPreSelectionPage = LogonPageFactory.initElements(
				seleniumDriver, CarrierPreSelectionPage.class);
		TruckPage truckPage = carrierPreSelectionPage.selectCarrier(
				properties.getProperty(TestProperties.TruckPageKey.CARRIER.getKeyString()), TruckPage.class);

		truckPage.createNewTruck(properties);

		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));
	}

	@Test
	public void testCreateAndEditTruck() {
		String fasttime = "_" + DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH) + "_"
				+ DateTimeUtils.formatHoursMinutes(new Date());
		String pool = "Pool One";
		String carrier = "carrier -11";
		String taraMeasure = "15.5";
		String truckName = "l" + fasttime;
		String truckPlateNumber = "k" + fasttime;

		CarrierPreSelectionPage carrierPreSelectionPage = LogonPageFactory.initElements(
				seleniumDriver, CarrierPreSelectionPage.class);
		TruckPage truckPage = carrierPreSelectionPage.selectCarrier(carrier, TruckPage.class);

		truckPage.clickAddNewTruckButton();

		truckPage.typeTruckName(truckName);
		truckPage.typePopupPlateNr(truckPlateNumber);
		truckPage.typePopupTaraMeasure(taraMeasure);
		truckPage.selectSapNumber("7111291");

		truckPage.clickSaveNewTruckButton();

		truckPage.checkStatus(true);
		truckPage.typeMaxMeasure("100");
		truckPage.checkForPdaOnTruck(true);
		truckPage.typeVolumeM3("100");
		truckPage.assignPool(pool);

		truckPage.clickSaveTruckButton();
		String truckId = truckPage.selectTruck(truckName);

		Assert.assertNotNull(truckId);

		truckPage.checkStatus(true);

		truckPage.selectCategoryByValue("1"); //1-4 (trailer = 3)
		truckPage.selectLkwTypeByValue("1"); //1-6 , only if Tkategorie != 3
		truckPage.selectDustfilterByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.selectBrandingByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.clickSaveTruckButton();
		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));

		truckPage.selectCategoryByValue("2"); //1-4 (trailer = 3)
		truckPage.selectLkwTypeByValue("1"); //1-6 , only if Tkategorie != 3
		truckPage.selectDustfilterByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.selectBrandingByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.clickSaveTruckButton();
		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));

		truckPage.selectLkwTypeByValue("1"); //1-6 , only if Tkategorie != 3
		truckPage.selectDustfilterByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.selectBrandingByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.clickSaveTruckButton();
		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));

		truckPage.selectCategoryByValue("1"); //1-4 (trailer = 3)
		truckPage.selectLkwTypeByValue("3"); //1-6 , only if Tkategorie != 3
		truckPage.selectDustfilterByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.selectBrandingByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.clickSaveTruckButton();
		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));

		truckPage.selectCategoryByValue("1"); //1-4 (trailer = 3)
		truckPage.selectLkwTypeByValue("1"); //1-6 , only if Tkategorie != 3
		truckPage.selectDustfilterByValue("true"); //true, false, empty , only if Tkategorie != 3
		truckPage.selectBrandingByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.clickSaveTruckButton();
		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));

		truckPage.selectCategoryByValue("1"); //1-4 (trailer = 3)
		truckPage.selectLkwTypeByValue("1"); //1-6 , only if Tkategorie != 3
		truckPage.selectDustfilterByValue("false"); //true, false, empty , only if Tkategorie != 3
		truckPage.selectBrandingByValue("true"); //true, false, empty , only if Tkategorie != 3
		truckPage.clickSaveTruckButton();
		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));

		truckPage.selectCategoryByValue("2"); //1-4 (trailer = 3)
		truckPage.selectLkwTypeByValue("5"); //1-6 , only if Tkategorie != 3
		truckPage.selectDustfilterByValue("true"); //true, false, empty , only if Tkategorie != 3
		truckPage.selectBrandingByValue("true"); //true, false, empty , only if Tkategorie != 3
		truckPage.clickSaveTruckButton();
		//Assert.assertTrue(truckPage.isSuccessMessageShown(true));

		truckPage.unassignPool(pool);
		truckPage.assignPool(pool);

		truckPage.checkForPdaOnTruck(true);
		truckPage.clickSaveTruckButton();

		Assert.assertTrue(truckPage.isPdaFlagChecked());

		truckPage.checkForPdaOnTruck(false);
		truckPage.clickSaveTruckButton();

		Assert.assertFalse(truckPage.isPdaFlagChecked());
	}
}

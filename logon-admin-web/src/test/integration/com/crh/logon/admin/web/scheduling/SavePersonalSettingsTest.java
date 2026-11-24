/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;

import com.crh.logon.admin.web.pages.LogonPageFactory;

public class SavePersonalSettingsTest extends AbstractSchedulingPageHelper {

	private static final String IN_PROPERTIES_PATH = "etc/test/input/SavePersonalSettingsTest.properties";

	/**
	 */
	public SavePersonalSettingsTest() {
		super(SavePersonalSettingsTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	/**
	 * @TestSpec logon-1393:Save personal settings on scheduling screen
	 * @TestSpec LOGON-8943: Automate Testcase: logon-1393:Save personal settings on scheduling screen
	 */
	@Test
	public void testSavePersonalSettings() {
		setupPersonalSettings();
		verifyPersonalSettings();
	}

	/**
	 * Verify personal data settings
	 */
	private void verifyPersonalSettings() {
		loginToLogonAsAdminEN();
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);

		// verify pool
		Assert.assertEquals(getInProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				schedulingPage.getPool());
		// verify carrier
		Assert.assertTrue(schedulingPage.isCarrierChecked(getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME2.getKeyString())));

		// verify time grid
		Assert.assertEquals(getInProperty(TestProperties.SchedulePageKey.TIME_GRID.getKeyString()),
				schedulingPage.getTimeGrid());

		// verify time grid
		Assert.assertEquals(getInProperty(TestProperties.SchedulePageKey.HOUR_SPAN.getKeyString()),
				schedulingPage.getHourSpan());

		// verify display
		Assert.assertTrue(schedulingPage.isHidePlanningTruckChecked());

		schedulingPage.resetPersonalSettings();

		logoutOfLogon();
	}

	/**
	 * Set up personal data settings
	 */
	private void setupPersonalSettings() {
		loginToLogonAsAdminEN();
		SchedulingPage schedulePage = initSchedulePage(
				getInProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.DAY.getKeyString()));

		// select personal settings
		schedulePage.selectHourSpan(getInProperty(TestProperties.SchedulePageKey.HOUR_SPAN.getKeyString()));
		schedulePage.selectDisplay(getInProperty(TestProperties.SchedulePageKey.DISPLAY.getKeyString()));
		schedulePage.selectTimeGrid(getInProperty(TestProperties.SchedulePageKey.TIME_GRID.getKeyString()));

		// verify buttons
		schedulePage.verifyEnableButton("buttonSavePreferences");
		schedulePage.verifyEnableButton("buttonResetField");

		// select another carrier
		schedulePage.selectCarriers(false, true);
		schedulePage.selectCarriers(false, false,
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME2.getKeyString()));

		// save settings
		schedulePage.savePersonalSettings();

		logoutOfLogon();
	}
}

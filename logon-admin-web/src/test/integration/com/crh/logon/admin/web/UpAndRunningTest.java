/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web;

import java.util.Locale;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.crh.logon.business.services.utils.Message;

/**
 * Simple test only checking if Logon is available.
 * 
 * @author AdNovum Informatik AG
 */
public class UpAndRunningTest extends LogonSeleniumTestCaseBase {
	@Test
	public void testTableBrowserExists() {
		loginToLogon("/logon/admin/tablebrowser/TableViewEntry.do", "logonadmin_de",
				"logonadmin_de");

		String text = Message.lookup(Locale.GERMAN, "tablebrowser.noTableSelected");
		Assert.assertTrue(seleniumDriver.getPageSource().contains(text));

		logoutOfLogon();
	}
}
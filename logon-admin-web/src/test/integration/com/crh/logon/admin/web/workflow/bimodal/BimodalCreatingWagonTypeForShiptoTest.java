/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncShiptoPage;
import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncShiptoWagonTypePage;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.admin.tablebrowser.pages.TableBrowserPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class BimodalCreatingWagonTypeForShiptoTest extends PropertyTransferTest {
	private final String SAP_BP_ID = "-109672";
	private final String WAGON_TYPE_CD = "2";
	private final String EVALUATION_PRIORITY = "10";

	public BimodalCreatingWagonTypeForShiptoTest() {
		super(BimodalCreatingWagonTypeForShiptoTest.class.getSimpleName(), null,
				null);
	}
	
	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}
	
	@Test
	public void testCreateWagonTypeForShipto() {
		TableBrowserPage tableBrowserPage = LogonPageFactory.initElements(seleniumDriver, TableBrowserPage.class);
		
		TlgncShiptoPage tlgncShiptoPage = tableBrowserPage.viewTablePage(TlgncShiptoPage.class);
		
		tlgncShiptoPage.filterWithWhereClause("SHIPTO_SAP_BP_ID = " + SAP_BP_ID);
		
		String shiptoId = tlgncShiptoPage.getValueAtColumn(TlgncShiptoPage.ShiptoColumn.SHIPTO_ID);
		
		tableBrowserPage = LogonPageFactory.initElements(seleniumDriver, TableBrowserPage.class);
		
		TlgncShiptoWagonTypePage tlgncShiptoWagonTypePage = tableBrowserPage.viewTablePage(TlgncShiptoWagonTypePage.class);
		
		tlgncShiptoWagonTypePage.clickAddRowButton();
		
		String shiptoWhereClause = TlgncShiptoWagonTypePage.ShiptoWagonTypeColumn.SHIPTO_ID.getColumName() + " = " + shiptoId;
		String wagonTypeWhereClause = TlgncShiptoWagonTypePage.ShiptoWagonTypeColumn.WAGON_TYPE_CD.getColumName() + " = " + WAGON_TYPE_CD;
		
		tlgncShiptoWagonTypePage.selectReference(TlgncShiptoWagonTypePage.ShiptoWagonTypeColumn.SHIPTO_ID, shiptoWhereClause);
		tlgncShiptoWagonTypePage.selectReference(TlgncShiptoWagonTypePage.ShiptoWagonTypeColumn.WAGON_TYPE_CD, wagonTypeWhereClause);
		tlgncShiptoWagonTypePage.inputValueForColum(TlgncShiptoWagonTypePage.ShiptoWagonTypeColumn.EVALUATION_PRIORITY, EVALUATION_PRIORITY);
		
		tlgncShiptoWagonTypePage.clickSaveButton();
	}
}

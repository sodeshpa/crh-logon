package com.crh.logon.admin.web.admin.tablebrowser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncExtMaterialPage;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.admin.tablebrowser.pages.TableBrowserPage;
import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncCarrierPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class GenericTableBrowserSeleniumTest extends LogonSeleniumTestCaseBase {

	@Before
	public void login() throws Exception {
		loginToLogon(TableBrowserPage.ENTRY_PATH, "logonadmin_de", "logonadmin_de");
	}

	@After
	public void logout() throws Exception {
		logoutOfLogon();
	}

	@Test
	public void addSearchDelete() {
		String name = "TB_EXT_MAT_" + Long.toString(System.currentTimeMillis());
		String comment = "Selenium material created by table browser integration test";
		
		TableBrowserPage tableBrowserPage = LogonPageFactory.initElements(seleniumDriver, TableBrowserPage.class);
		
		TlgncExtMaterialPage tlgncExtMaterialPage = tableBrowserPage.viewTablePage(TlgncExtMaterialPage.class);
		
		String materialWhereClause = "MATERIAL_NAME = '" + name + "'";
		
		tlgncExtMaterialPage.filterWithWhereClause(materialWhereClause);
		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(0)));
		
		// add new external material
		tlgncExtMaterialPage.clickAddRowButton();
		
		String carrierReferenceWhereClauseFilter = TlgncCarrierPage.CarrierColumn.DISPLAY_NAME.getColumName() + " like '%carrier%'";
		
		tlgncExtMaterialPage.selectReference(TlgncCarrierPage.CarrierColumn.CARRIER_ID, carrierReferenceWhereClauseFilter);
		
		tlgncExtMaterialPage.inputValueForColum(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_NAME, name);
		tlgncExtMaterialPage.inputValueForColum(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_COMMENT, comment);
		tlgncExtMaterialPage.clickSaveButton();
		
		// filter again
		tlgncExtMaterialPage.filterWithWhereClause(materialWhereClause);
		
		// count again
		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(1)));
		assertThat(tlgncExtMaterialPage.getValueAtColumn(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_NAME), is(equalTo(name)));
		assertThat(tlgncExtMaterialPage.getValueAtColumn(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_COMMENT), is(equalTo(comment)));
		
		// delete
		tlgncExtMaterialPage.deleteFirstRow();
		
		// count again
		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(0)));
	}

	@Test
	public void addCancel() {
		String name = "TB_EXT_MAT_" + Long.toString(System.currentTimeMillis());
		String comment = "Selenium material created by table browser integration test";

		TableBrowserPage tableBrowserPage = LogonPageFactory.initElements(seleniumDriver,
				TableBrowserPage.class);
		
		
		TlgncExtMaterialPage tlgncExtMaterialPage = tableBrowserPage.viewTablePage(TlgncExtMaterialPage.class);
		
		String materialWhereClause = "MATERIAL_NAME = '" + name + "'";
		
		tlgncExtMaterialPage.filterWithWhereClause(materialWhereClause);
		// sanity check: make sure that our material doesn't already exist
		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(0)));
		
		// add new external material
		tlgncExtMaterialPage.clickAddRowButton();
		
		tlgncExtMaterialPage.inputValueForColum(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_NAME, name);
		tlgncExtMaterialPage.inputValueForColum(TlgncExtMaterialPage.ExtMaterialColumn.MATERIAL_COMMENT, comment);

		tlgncExtMaterialPage.clickCancelButton();

		// re-fetch page and check data from server
		tlgncExtMaterialPage.filterWithWhereClause(materialWhereClause);

		assertThat(tlgncExtMaterialPage.countNumberOfRows(), is(equalTo(0)));
	}
}
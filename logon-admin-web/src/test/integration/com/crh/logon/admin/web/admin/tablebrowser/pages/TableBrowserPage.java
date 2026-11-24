package com.crh.logon.admin.web.admin.tablebrowser.pages;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * The <code>TableBrowserPage</code> implements the <em>Page Object</em> pattern abstracting
 * away the underlying access to the main table browser page.
 *  
 * @author AdNovum Informatik AG
 */
public class TableBrowserPage extends LogonBasePage {
	public static final String ENTRY_PATH = "/logon/admin/tablebrowser/TableViewEntry.do";

	public TableBrowserPage(WebDriver driver) {
		super(driver);
		driver.get(LogonSeleniumTestCaseBase.LOGON_BASE_URL + ENTRY_PATH);
	}

	@Override
	public void verifyCorrectPage() {
		WebElement tableBrowseLink = driver.findElement(By.xpath("//a[contains(@href, 'TableViewEntry.do')]"));
		
		assertThat(tableBrowseLink, is(notNullValue()));
		assertThat(tableBrowseLink.getAttribute("class"), is(containsString("highlight")));
	}

	public <P extends EntryBrowserPage> P viewTablePage(Class<P> pageClass) {
		TargetTable targetTable = pageClass.getAnnotation(TargetTable.class);
		
		Assert.assertNotNull("Please specify the table name for the page(by adding annotation 'TargetTable')", targetTable);
		String tableName = targetTable.name();
		
		Assert.assertNotNull("Invalid table name for Table page" + pageClass.getName(),
				tableName);
		
		WebElement tableLink = driver.findElement(By.linkText(tableName));
		Assert.assertNotNull(tableLink);
		tableLink.click();

		return LogonPageFactory.initElements(driver, pageClass);

	}
}

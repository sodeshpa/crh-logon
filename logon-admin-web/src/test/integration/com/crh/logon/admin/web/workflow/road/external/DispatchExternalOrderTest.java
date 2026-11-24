/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.road.external;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.admin.web.workflow.road.RoadWorkflowTestProperties;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * This test is designed to test external order functionality. As the workflow is using pda, this is
 * part of the test triple for external workflow.
 * 
 * Part 1: logon-admin-web - Order creation - CreateExternalOrderTest (ant target: adnant test-external-pda-workflow-part0)
 * Part 2: logon-admin-web - Order dispatching - DispatchExternalOrderTest (ant target: adnant test-external-pda-workflow-part1 (runs part0) automatically)
 * Part 3: logon-simulator - Pda workflow execution - integration-test-soapui-project/ExternalWorkflowTest (test suite) (ant target: adnant test-external-pda-workflow-part2)
 * 
 * To run the test like in the NB:
 * Step 1. in logon-admin-web call adnant test-external-pda-workflow-part1
 * Step 2. in logon simulator call adnant test-external-pda-workflow-part2
 * 
 * Please see how the properties are saved back in @ After
 * 
 * Note: This test has not been migrated to use the PageObjectPattern. Please 
 * don't use it as a blueprint for new tests. See also LOGON-8463.
 * 
 * @author AdNovum Informatik AG
 */
public final class DispatchExternalOrderTest extends PropertyTransferTest {

	public static final String IN_PROPERTIES = "etc/test/output/external_workflow.properties";

	public static final String OUT_PROPERTIES = "etc/test/output/external_workflow.properties";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispatchExternalOrderTest.class);

	public DispatchExternalOrderTest() {
		super(DispatchExternalOrderTest.class.getSimpleName(), IN_PROPERTIES, OUT_PROPERTIES);
	}

	@Before
	public void login() throws Exception {
		LogonSeleniumTestCaseBase.loginToLogon("/logon/order/dispo/DispoEntry.do", "adn_role_22", "adn_role_22");
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	@Test
	public void testDispatchExternalOrder() throws Exception {
		final String truckId = getInProperty(RoadWorkflowTestProperties.ROAD_WORKFLOW_KEY.TRUCK_ID); // e.g., "-1001" (ADN External BULK truck)
		final String orderNr = getInProperty(RoadWorkflowTestProperties.ROAD_WORKFLOW_KEY.ORDER_NO); // e.g., "1000000"
		final String date = getInProperty(com.crh.logon.admin.web.pages.TestProperties.ExternalOrderKey.FROM_DATE); // e.g., "24.10.2012"

		//Prevent autoupdate of the page content by unchecking the autoupdate checkbox
		WebElement autoUpdate = seleniumDriver.findElement(By.id("devModeAutoUpdate"));
		Assert.assertNotNull(autoUpdate);
		autoUpdate.click();

		LOG.info("--Step - [" + date + "] Dispatch order " + orderNr + " to truck " + truckId);

		WebElement dispoDatePicker = seleniumDriver.findElement(By.id("DispoDayPicker"));
		dispoDatePicker.clear();
		dispoDatePicker.sendKeys(date);

		// select the pool 'ADN Pool External'
		Select poolSelect = new Select(seleniumDriver.findElement(By.name("selectedPool")));
		poolSelect.selectByVisibleText(getInProperty(com.crh.logon.admin.web.pages.TestProperties.ExternalOrderKey.POOL_NAME));

		// select 'keiner' and then 'alle' from combobox 'Spediteure' (this also forces a reload of the
		// trucks list, so that no StaleElementReferenceExceptions should occur anymore)
		seleniumDriver.findElement(By.xpath("//input[@id='CarriersField']/following-sibling::img")).click();
		seleniumDriver.findElement(
				By.xpath("//div[@id='DivCarriersEdit']//div[2]//input[@value='keiner']")).click();
		seleniumDriver.findElement(By.xpath("//input[@id='CarriersField']/following-sibling::img")).click();
		seleniumDriver.findElement(
				By.xpath("//div[@id='DivCarriersEdit']//div[2]//input[@value='alle']")).click();

		// wait until the expected truck appears in the list
		WebElement truckRow = seleniumDriver.findElement(By.id("trc" + truckId));

		// collect existing assignments of truck with truckID
		String assignmentXPath = "//div[contains(@id, 'dispoBarOverlay_') and @class='dispoBarOverlay']";
		int numExistingAssignments = 0;
		final Set<String> existingAssignmentIds = new HashSet<String>();
		if (!truckRow.getText().trim().isEmpty()) { // avoid that Selenium waits a full timeout to find 0 assignments
			final List<WebElement> existingAssignments = seleniumDriver.findElements(By.xpath(assignmentXPath));
			numExistingAssignments = existingAssignments.size();
			for (WebElement assignment : existingAssignments) {
				final String assignmentId = assignment.getAttribute("id").substring(16);
				existingAssignmentIds.add(assignmentId);
			}
		}

		// drag and drop the external order to the table row of the truck with truckId
		WebElement extOrderDrag = seleniumDriver.findElement(By.xpath("//div[@class='orderInner']//div[@class='orderCaption' and "
				+ "@*[contains(., '"
				+ orderNr
				+ "') and contains(., 'Minimal setup external material')]]"));
		WebElement truckDrop = seleniumDriver.findElement(By.id("cd" + truckId));
		(new Actions(seleniumDriver)).dragAndDrop(extOrderDrag, truckDrop).perform();

		// repeatedly check if a new assignment appears in the row of truck with truckID
		long startTime = System.currentTimeMillis();
		long timeout = TestProperties.INSTANCE.getSeleniumImplicitTimeout() * 1000;
		int numAssignmentsAfter = numExistingAssignments;
		while (numAssignmentsAfter <= numExistingAssignments) {
			numAssignmentsAfter = seleniumDriver.findElements(By.xpath(assignmentXPath)).size();
			if (System.currentTimeMillis() - startTime > timeout) {
				Assert.fail("Failed to drag-and-drop the external order " + orderNr
						+ " to the table row of truck " + truckId + "!");
			}
			Thread.sleep(1000);
		}

		// identify the new assignment
		final List<WebElement> newAssignments = seleniumDriver.findElements(By.xpath(assignmentXPath));
		LOG.debug("Truck " + truckId + " has " + newAssignments.size() + " assignments.");

		for (WebElement newAssignment : newAssignments) {
			// get the assignment id (e.g., the numeric part of dispoBarOverlay_1022) of the least recently added order
			String newAssignmentId = newAssignment.getAttribute("id").substring(16);
			if (existingAssignmentIds.contains(newAssignmentId)) {
				LOG.debug("The assignment with id " + newAssignmentId + " existed before.");
				continue;
			}
			LOG.info("The assignment id of order " + orderNr + " is: " + newAssignmentId);

			// select the currently assigned element by mouse-click and push the 'Uebertragen' button:
			// using .executeScript instead of .click(), because another element obscures it, but this way it can be clicked
			((JavascriptExecutor)seleniumDriver).executeScript("arguments[0].click();", newAssignment);
			seleniumDriver.findElement(By.id("buttonTransmit")).click();

			setOutProperty(com.crh.logon.admin.web.pages.TestProperties.ExternalOrderKey.TRANSMITTED, "true");
			break;
		}
	}
}

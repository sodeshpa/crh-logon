/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.pages;

import junit.framework.AssertionFailedError;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.google.common.base.Function;

/**
 * @author AdNovum Informatik AG
 */
public class LogonPageFactory {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonBasePage.class);

	/**
	 * Instantiate an instance of the given class, and set a lazy proxy for each of the WebElement
	 * and List<WebElement> fields that have been declared, assuming that the field name is also
	 * the HTML element's "id" or "name".
	 * 
	 * This method will attempt to instantiate the class given to it, preferably using a constructor
	 * which takes a WebDriver instance as its only argument or falling back on a no-arg constructor.
	 * An exception will be thrown if the class cannot be instantiated.
	 * 
	 * After successful instantiation a page verification is invoked.
	 * 
	 * @param driver
	 * @param pageClass
	 * @return
	 */
	public static <P extends LogonBasePage> P initElements(WebDriver driver, Class<P> pageClass) {
		final P page = PageFactory.initElements(driver, pageClass);

		// Note: Verification has been moved outside the page constructor because an exception will be thrown
		//       if the instantiation was not successful => a JUnit error is reported instead of a failure
		/**
		 * In many cases the page is already loaded, but javascript Ext.onReady function is not yet finished. As a consequence
		 * many web elements are yet incomplete, and also verifyCorrectPage() fails.			
		 * Since there is no possibity to detect the end of the Ext.onReady function execution, let's wait until succeeds.
		 */
		new AdNWebDriverWait(driver).until(new Function<WebDriver, Boolean>() {
			@Override
			public Boolean apply(WebDriver arg0) {
				try {
					page.verifyCorrectPage();
					return true;
				}
				catch (AssertionFailedError afe) {
					return false;
				}
			}

			@Override
			public String toString() {
				return "current page to be the correct one";
			}
		});

		return page;
	}
}

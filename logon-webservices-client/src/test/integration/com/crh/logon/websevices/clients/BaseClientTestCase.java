/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.websevices.clients;

import junit.framework.TestCase;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.webservices.clients.beans.ferrari.ResponseBean;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public abstract class BaseClientTestCase extends TestCase {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(BaseClientTestCase.class.getName());

	protected ConfigBean configBean = null;

	/**
	 * @param arg0
	 */
	public BaseClientTestCase(String arg0) {
		super(arg0);
		
		// [[
		Object[][] params = {{"arg0", arg0}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "BaseClientTestCase", params);
		}
		// ]]
		try {
			this.configBean = new ConfigBean();
			
			if (logDebugEnabled) {
				this.configBean.log();
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @param response
	 * @param operationName
	 * @param expectedResponseCode
	 */
	protected static void checkFerrariResponse(ResponseBean response,
			String operationName, String expectedResponseCode) {
		// [[
		Object[][] params = {{"response", response},
							{"operationName", operationName},
							{"expectedResponseCode", expectedResponseCode}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(BaseClientTestCase.class, "checkFerrariResponse", params);
		}
		// ]]
		try {
			assertNotNull("Error during the call for operation - "
					+ operationName + ".", response);
			
			assertEquals("Error during the call for operation - bad response.",
					expectedResponseCode, Boolean.toString(response
							.isSuccessful()));
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.websevices.clients;

import com.crh.logon.webservices.clients.LogonToFSms;
import com.crh.logon.webservices.clients.beans.ferrari.ResponseBean;
import com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class LogonToFSmsTestCase extends BaseClientTestCase {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(LogonToFSmsTestCase.class.getName());

	private static final String HELLO_WORLD = "Hello World!";
	private static final String MOCKUP_URL = "http://localhost:8090/mockFSmsSoap";
	/**
	 * @param arg0
	 */
	public LogonToFSmsTestCase(String arg0) {
		super(arg0);
		
		// [[
		Object[][] params = {{"arg0", arg0}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "LogonToFSmsTestCase", params);
		}
		// ]]
		try {
			; // PMD
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
	 * 
	 */
	public void testHelloWorld() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testHelloWorld");
		}
		// ]]
		try {
			String temp = LogonToFSms.helloWorld(this.configBean);
			
			assertEquals("FSms.HelloWorld sent bad response.", HELLO_WORLD,
					temp);
			
			LOG.info("FSms.HelloWorld: " + temp);
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
	 * 
	 */
	public void testSendSms() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testSendSms");
		}
		// ]]
		try {
			final String operationName = "SendSms";
			final String argsText = "Greetings from AdNovum!";
			final String argsNumberAny = "+36 1 487 50 00";
			ResponseBean response = null;
			
			response = LogonToFSms.sendSms(
					new TextMessageBean(argsNumberAny, argsText), this.configBean);
			checkFerrariResponse(response, operationName, Boolean.TRUE
					.toString());
			
			//			response = LogonToFSms.sendSms(new TextMessageBean(
			//					ARGSNUMBER_UNSUCCESSFUL_CALL, argsText), this.configBean);
			//			checkFerrariResponse(response, operationName, Boolean.FALSE
			//					.toString());
			//
			//			response = LogonToFSms.sendSms(new TextMessageBean(
			//					ARGSNUMBER_EMPTYRESPONSE, argsText), this.configBean);
			//			checkFerrariResponse(response, operationName, Boolean.FALSE
			//					.toString());
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

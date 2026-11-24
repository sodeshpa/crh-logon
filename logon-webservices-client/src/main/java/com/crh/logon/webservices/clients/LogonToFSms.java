/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.webservices.clients.beans.ferrari.LogonToFerrariAssembler;
import com.crh.logon.webservices.clients.beans.ferrari.ResponseBean;
import com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean;
import com.crh.logon.webservices.clients.util.ClientConfig;
import com.crh.logon.to.fsms.FSmsStub.ErrorText;
import com.crh.logon.to.fsms.FSmsStub.ErrorTextResponse;
import com.crh.logon.to.fsms.FSmsStub.HelloWorld;
import com.crh.logon.to.fsms.FSmsStub.HelloWorldResponse;
import com.crh.logon.to.fsms.FSmsStub.SendSms;
import com.crh.logon.to.fsms.FSmsStub.SendSmsResponse;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public final class LogonToFSms {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToFSms.class);
	private static final String CONFIG_LABEL = "fsms";
	
	/**
	 * @param configBean
	 * @return
	 */
	public static String helloWorld(ConfigBean configBean) {
		// [[
		String result = null;
		Object[][] params = {{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFSms.class, "helloWorld", params);
		}
		// ]]
		try {			
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedFSmsStub stub = new EnhancedFSmsStub(clientConfig);			
			final HelloWorld requestObject = new HelloWorld();
			final HelloWorldResponse responseObject = stub.helloWorld(requestObject);
			
			if (responseObject != null) {
				result = responseObject.getHelloWorldResult();
			}
			else {
				getAndThrowError(
						stub, 
						"helloWorld",
						responseObject, 
						null);
			}
			
			return result;
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * @param bean
	 * @param configBean
	 * @return
	 */
	public static ResponseBean sendSms(TextMessageBean bean, ConfigBean configBean) {
		// [[
		ResponseBean result = null;
		Object[][] params = {{"bean", bean},
							{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFSms.class, "sendSms", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedFSmsStub stub = new EnhancedFSmsStub(clientConfig);			
			final SendSms requestObject = LogonToFerrariAssembler.createSendSms(bean);
			final SendSmsResponse responseObject = stub.sendSms(requestObject);
			result = LogonToFerrariAssembler.getResponseBean(responseObject);
			
			if ((result == null) || (!result.isSuccessful())) {
				getAndThrowError(
						stub, 
						"sendSms", 
						responseObject, 
						new Object[][]{{"bean", bean}});
			}
			
			return result;
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private final static void getAndThrowError(EnhancedFSmsStub stub, String operationName, Object responseObject, Object[][] origParams) {
		// [[
		Object[][] params = {};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFFax.class, "checkForError", params);
		}
		// ]]
		try {
			final ErrorTextResponse errorResponseObject = stub.errorText(new ErrorText());
			
			if (errorResponseObject != null) {	
				throw new LogonException(String.format(
						"Error during the call for operation: %s; Response %s; Details: %s;",
						operationName, 
						responseObject.toString(),
						errorResponseObject.getErrorTextResult()), 
					Reason.RES_ERROR_WSCLIENT,
					origParams);
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
}

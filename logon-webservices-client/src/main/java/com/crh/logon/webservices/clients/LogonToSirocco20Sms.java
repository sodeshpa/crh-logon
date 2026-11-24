/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients;

import java.util.Arrays;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.webservices.clients.beans.sirocco20.ResponseBean;
import com.crh.logon.webservices.clients.beans.sirocco20.TextMessageBean;
import com.crh.logon.webservices.clients.util.ClientConfig;
import com.crh.logon.to.sirocco20.Sirocco20Stub.SubmitMTText;
import com.crh.logon.to.sirocco20.Sirocco20Stub.SubmitMTTextResponse;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.beans.sirocco20.LogonToSirocco20SmsAssembler;

/**
 * @author AdNovum Informatik AG
 */
public final class LogonToSirocco20Sms {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToSirocco20Sms.class);
	private static final String CONFIG_LABEL = "ssms";
	
	/**
	 * @param bean
	 * @param configBean
	 * @return
	 */
	public static ResponseBean[] sendSms(TextMessageBean bean, ConfigBean configBean) {
		// [[
		ResponseBean[] result = null;
		Object[][] params = {{"bean", bean},
							{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSirocco20Sms.class, "sendSms", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedSirocco20Stub stub = new EnhancedSirocco20Stub(clientConfig);			
			final SubmitMTText requestObject = LogonToSirocco20SmsAssembler.createSubmitMTText(bean);
			final SubmitMTTextResponse responseObject = stub.submitMTText(requestObject);
			result = LogonToSirocco20SmsAssembler.getResponseBean(responseObject);
			
			checkResult(
					"submitMTText", 
					result, 
					new Object[][]{{"bean", bean}});
			
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
	
	private final static void checkResult(String operationName, ResponseBean[] result, Object[][] origParams) {
		// [[
		Object[][] params = {};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFFax.class, "checkForError", params);
		}
		// ]]
		try {
			if ((result == null) || (result.length == 0)) {
				throw new LogonException(String.format(
						"Error during the call for operation: %s; no result returned",
						operationName), 
						Reason.RES_ERROR_WSCLIENT,
						origParams);
			}
			else {
				boolean successful = true;
				for (ResponseBean rb : result) {
					if (!rb.isSuccessful()) {
						successful = false;
						break;
					}
				}
				if (!successful) {
					throw new LogonException(String.format(
							"Error during the call for operation: %s; results: %s;",
							operationName,
							Arrays.toString(result)), 
						Reason.RES_ERROR_WSCLIENT,
						origParams);
				}
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

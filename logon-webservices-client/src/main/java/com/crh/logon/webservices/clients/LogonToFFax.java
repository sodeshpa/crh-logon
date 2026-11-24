/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients;

import com.crh.logon.webservices.clients.beans.ferrari.LogonToFerrariAssembler;
import com.crh.logon.webservices.clients.beans.ferrari.ResponseBean;
import com.crh.logon.webservices.clients.beans.ferrari.StationeriesBean;
import com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean;
import com.crh.logon.webservices.clients.util.ClientConfig;
import org.apache.axis2.AxisFault;

import com.crh.logon.to.ffax.FFaxStub.ErrorText;
import com.crh.logon.to.ffax.FFaxStub.ErrorTextResponse;
import com.crh.logon.to.ffax.FFaxStub.GetStationeries;
import com.crh.logon.to.ffax.FFaxStub.GetStationeriesResponse;
import com.crh.logon.to.ffax.FFaxStub.HelloWorld;
import com.crh.logon.to.ffax.FFaxStub.HelloWorldResponse;
import com.crh.logon.to.ffax.FFaxStub.SendFaxFile;
import com.crh.logon.to.ffax.FFaxStub.SendFaxFileResponse;
import com.crh.logon.to.ffax.FFaxStub.SendFaxText;
import com.crh.logon.to.ffax.FFaxStub.SendFaxTextResponse;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.beans.ferrari.FileMessageBean;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class LogonToFFax {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToFFax.class);
	private static final String CONFIG_LABEL = "ffax";
	
	/**
	 * @param configBean
	 * @return
	 * @throws AxisFault 
	 */
	public static StationeriesBean getStationeries(ConfigBean configBean) {
		// [[
		StationeriesBean result = null;
		Object[][] params = {{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFFax.class, "getStationeries", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedFFaxStub stub = new EnhancedFFaxStub(clientConfig);			
			final GetStationeries requestObject = new GetStationeries();			
			final GetStationeriesResponse responseObject = stub.getStationeries(requestObject);
			result = LogonToFerrariAssembler.getStationeriesBean(responseObject);
			
			if ((result == null) || (!result.isSuccessful())) {
				getAndThrowError(
						stub, 
						"getStationeries", 
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
	 * @param configBean
	 * @return
	 * @throws AxisFault 
	 */
	public static String helloWorld(ConfigBean configBean) {
		// [[
		String result = null;
		Object[][] params = {{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFFax.class, "helloWorld", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedFFaxStub stub = new EnhancedFFaxStub(clientConfig);			
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
	 * @throws AxisFault 
	 */
	public static ResponseBean sendFaxFile(FileMessageBean bean, ConfigBean configBean) {
		// [[
		ResponseBean result = null;
		Object[][] params = {{"bean", bean},
							{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFFax.class, "sendFaxFile", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedFFaxStub stub = new EnhancedFFaxStub(clientConfig);			
			final SendFaxFile requestObject = LogonToFerrariAssembler.createSendFaxFile(bean);			
			final SendFaxFileResponse responseObject = stub.sendFaxFile(requestObject);	
			result = LogonToFerrariAssembler.getResponseBean(responseObject);
			
			if ((result == null) || (!result.isSuccessful())) {
				getAndThrowError(
						stub, 
						"sendFaxFile", 
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

	/**
	 * @param bean
	 * @param configBean
	 * @return
	 * @throws AxisFault 
	 */
	public static ResponseBean sendFaxText(TextMessageBean bean, ConfigBean configBean) {
		// [[
		ResponseBean result = null;
		Object[][] params = {{"bean", bean},
							{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFFax.class, "sendFaxText", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedFFaxStub stub = new EnhancedFFaxStub(clientConfig);			
			final SendFaxText requestObject = LogonToFerrariAssembler.createSendFaxText(bean);			
			final SendFaxTextResponse responseObject = stub.sendFaxText(requestObject);
			result = LogonToFerrariAssembler.getResponseBean(responseObject);
			
			if ((result == null) || (!result.isSuccessful())) {
				getAndThrowError(
						stub, 
						"sendFaxText", 
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
	
	private final static void getAndThrowError(EnhancedFFaxStub stub, String operationName, Object responseObject, Object[][] origParams) {
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

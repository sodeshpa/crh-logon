/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.ferrari;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.ByteArrayDataSource;

import com.crh.logon.to.ffax.FFaxStub.ArrayOfString;
import com.crh.logon.to.ffax.FFaxStub.GetStationeriesResponse;
import com.crh.logon.to.ffax.FFaxStub.SendFaxFile;
import com.crh.logon.to.ffax.FFaxStub.SendFaxFileResponse;
import com.crh.logon.to.ffax.FFaxStub.SendFaxText;
import com.crh.logon.to.ffax.FFaxStub.SendFaxTextResponse;
import com.crh.logon.to.fsms.FSmsStub.SendSms;
import com.crh.logon.to.fsms.FSmsStub.SendSmsResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class LogonToFerrariAssembler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToFerrariAssembler.class);

	private static String setupNumber(String inputNumber) {
	    return inputNumber.replaceAll("[ \\)\\(]", "");
	}
	/**
	 * @param source
	 * @return
	 */
	public static final SendSms createSendSms(TextMessageBean source) {
		// [[
		SendSms result = null;
		Object[][] params = {{"source", source}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFerrariAssembler.class, "createSendSms", params);
		}
		// ]]
		try {
			result = new SendSms();
			
			if (source != null) {
				result.setArgsNumber(setupNumber(source.getNumber()));
				result.setArgsText(source.getMessage());
			}
			
			return result;
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
	 * @param source
	 * @return
	 */
	public static final SendFaxFile createSendFaxFile(FileMessageBean source) {
		// [[
		SendFaxFile result = null;
		Object[][] params = {{"source", source}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFerrariAssembler.class, "createSendFaxFile", params);
		}
		// ]]
		try {
			result = new SendFaxFile();
			
			if (source != null) {
				final ByteArrayDataSource dataSource = new ByteArrayDataSource(
						source.getFileContent(), 
						source.getMimeType());
				final DataHandler dataHandler = new DataHandler(dataSource);
			
				result.setArgsNumber(setupNumber(source.getNumber()));
				result.setArgsFileName(source.getFileName());
				result.setArgbytFileStream(dataHandler);
			}
			
			return result;
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
	 * @param source
	 * @return
	 */
	public static final SendFaxText createSendFaxText(TextMessageBean source) {
		// [[
		SendFaxText result = null;
		Object[][] params = {{"source", source}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFerrariAssembler.class, "createSendFaxText", params);
		}
		// ]]
		try {
			result = new SendFaxText();
			
			if (source != null) {
				result.setArgsNumber(setupNumber(source.getNumber()));
				result.setArgsText(source.getMessage());
			}
			
			return result;
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
	 * @param smsResponse
	 * @return
	 */
	public static final ResponseBean getResponseBean(SendSmsResponse smsResponse) {
		// [[
		ResponseBean result = null;
		Object[][] params = {{"smsResponse", smsResponse}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFerrariAssembler.class, "getResponseBean", params);
		}
		// ]]
		try {
			if (smsResponse != null) {
				result = new ResponseBean(
						smsResponse.getSendSmsResult(),
						smsResponse.getArgsJobId(),
						null);
			}
			else {
				result = new ResponseBean();
			}
			
			return result;
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
	 * @param faxFileResponse
	 * @return
	 */
	public static final ResponseBean getResponseBean(	SendFaxFileResponse faxFileResponse) {
		// [[
		ResponseBean result = null;
		Object[][] params = {{"faxFileResponse", faxFileResponse}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFerrariAssembler.class, "getResponseBean", params);
		}
		// ]]
		try {
			if (faxFileResponse != null) {
				result = new ResponseBean(
						faxFileResponse.getSendFaxFileResult(),
						faxFileResponse.getArgsJobId(),
						null);
			}
			else {
				result = new ResponseBean();
			}
			
			return result;
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
	 * @param faxTextResponse
	 * @return
	 */
	public static final ResponseBean getResponseBean(	SendFaxTextResponse faxTextResponse) {
		// [[
		ResponseBean result = null;
		Object[][] params = {{"faxTextResponse", faxTextResponse}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFerrariAssembler.class, "getResponseBean", params);
		}
		// ]]
		try {
			if (faxTextResponse != null) {
				result = new ResponseBean(
						faxTextResponse.getSendFaxTextResult(),
						faxTextResponse.getArgsJobId(),
						null);
			}
			else {
				result = new ResponseBean();
			}
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static final StationeriesBean getStationeriesBean(	GetStationeriesResponse stationeriesResponse) {
		// [[
		StationeriesBean result = null;
		Object[][] params = {{"stationeriesResponse", stationeriesResponse}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToFerrariAssembler.class, "getStationeriesBean", params);
		}
		// ]]
		try {
			if (stationeriesResponse != null) {				
				final ArrayOfString array = stationeriesResponse.getArgsStationeries();				
				result = new StationeriesBean(
						stationeriesResponse.getGetStationeriesResult(),
						(array != null) ? array.getString() : null);
			}
			else {
				result = new StationeriesBean();
			}
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigInteger;

import javax.ejb.ApplicationException;

import org.apache.commons.lang.ArrayUtils;

import com.holcim.logon.eventbeans.in.LogonEventResponse;

/**
 * @author AdNovum Informatik AG
 */
@ApplicationException(rollback=true)
public class LogonEventException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private LogonEventResponse logonEventResponse;
	private Object [] formatParameters;

	private static final String lineSeparator = System.getProperty("line.separator");
	
	/**
	 * Exception wrapping an error response itself,
	 * and the language and additional parameters to construct the final text message.
	 * 
	 * @param logonEventResponse
	 * @param formatParameters
	 */
	public LogonEventException(LogonEventResponse logonEventResponse, Object ... formatParameters) {
		super(createErrorMessage(logonEventResponse));
		this.logonEventResponse = logonEventResponse;
		this.formatParameters = formatParameters;
	}

	private static String createErrorMessage(LogonEventResponse logonEventResponse) {
		if (logonEventResponse == null) {
			return "LogonEventResponse was null";
		}
		StringBuilder msg = new StringBuilder();
		msg.append(lineSeparator);
		msg.append("\tResponse code: ");
		BigInteger code = logonEventResponse.getCode();
		msg.append(code);
		msg.append(lineSeparator);

		msg.append("\tResponse txt:");
		msg.append(lineSeparator);
		String txt = logonEventResponse.getTxt();
		msg.append(txt);
		msg.append(lineSeparator);

		msg.append("\tResponse context: ");
		msg.append(lineSeparator);
		String contextValue = logonEventResponse.getContextValue();
		msg.append(contextValue);

		String messageString = msg.toString();
		return messageString;
	}

	public LogonEventResponse getLogonEventResponse() {
		
		return logonEventResponse;
	}

	public Object[] getFormatParameters() {
		Object [] allParameters = ArrayUtils.add(formatParameters, 0, logonEventResponse.getContextValue());
		return allParameters;
	}

}

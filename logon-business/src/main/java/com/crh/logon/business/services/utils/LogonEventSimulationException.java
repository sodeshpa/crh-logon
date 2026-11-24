/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import javax.ejb.ApplicationException;

import com.holcim.logon.eventbeans.in.LogonEventResponse;

/**
 * @author AdNovum Informatik AG
 * 
 * This exception is used to roll back the transaction in case of a successful execution of the
 * request handler in case the sender desires a simulation only.
 */
@ApplicationException(rollback=true)
public class LogonEventSimulationException extends LogonEventException {

	/**
	 * @param logonEventResponse
	 */
	public LogonEventSimulationException(LogonEventResponse logonEventResponse) {
		super(logonEventResponse);
	}

}

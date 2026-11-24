/*
 * Author            : AdNovum Informatik AG

 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import javax.ejb.ApplicationException;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * An exception which can be used for notifying the web tier that printing failed.
 * 
 * @author AdNovum Informatik AG
 */
@ApplicationException
public class LogonPrintException extends LogonException {
	public LogonPrintException() {
	}

	public LogonPrintException(String message, Reason reason) {
		super(message, reason);
	}
}

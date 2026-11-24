/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

/**
 * @author AdNovum Informatik AG
 */
public class LogonConversionException extends Exception
{
	private static final long serialVersionUID = 3569446985430413294L;
	
	final private Reason reason;
	
	public LogonConversionException(Reason reason) {
		super();
		this.reason = reason;
	}

	public LogonConversionException(String message, Reason reason, Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	public LogonConversionException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}

	public LogonConversionException(Reason reason, Throwable cause) {
		super(cause);
		this.reason = reason;
	}

	public boolean isReason(Reason rsn) {
		return this.reason.getReason().equals(rsn.getReason());
	}
}

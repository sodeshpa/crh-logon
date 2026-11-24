/*
 * Author            : AdNovum Informatik AG

 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl.cron;

import javax.ejb.ApplicationException;

import com.crh.logon.utils.Reason;

/**
 *
 *	If this exception goes through transaction boundaries it will mark the transaction to
 *	be rolled back. 
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
@ApplicationException(rollback=true)
public final class CronJobException extends Exception {
		
	private static final long serialVersionUID = 1L;

	final private Reason reason;
	
	public CronJobException(Reason reason) {
		super();
		this.reason = reason;
	}

	public CronJobException(String message, Reason reason, Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	public CronJobException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}

	public CronJobException(Reason reason, Throwable cause) {
		super(cause);
		this.reason = reason;
	}

	public boolean isReason(Reason rsn) {
		return this.reason.getReason().equals(rsn.getReason());
	}
}

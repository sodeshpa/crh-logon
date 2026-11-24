/*
 * Author            : AdNovum Informatik AG

 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import javax.ejb.ApplicationException;

import com.crh.logon.utils.Reason;



/**
 * Exclusive exception for SapImportVehnPostprocessServiceBean.java.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
@ApplicationException(rollback=true)
public class VehnPostprocessException extends Exception {
	
	final private Reason reason;
	
	private static final long serialVersionUID = 1L;
	
	public VehnPostprocessException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}
	
	public boolean isReason(Reason rsn) {
		return this.reason.getReason().equals(rsn.getReason());
	}
}

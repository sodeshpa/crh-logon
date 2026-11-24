/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import org.apache.struts.action.ActionMessage;

/**
 * @author AdNovum Informatik AG
 */
public class ValueConversionException extends Exception {

	private static final long serialVersionUID = 1L;
	ActionMessage actionMessage;
	
	public ValueConversionException(ActionMessage message, Throwable cause){
		super (message.toString(), cause);
		this.actionMessage = message;
	}

	public ActionMessage getActionMessage() {
		return actionMessage;
	}
	
	
}

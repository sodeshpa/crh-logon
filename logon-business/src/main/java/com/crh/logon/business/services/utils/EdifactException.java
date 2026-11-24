/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

/**
 * @author AdNovum Informatik AG
 */
public class EdifactException extends RuntimeException {

	public EdifactException(String s) {
		super(s);
	}
	
	public EdifactException(Exception e) {
		super(e);
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

/**
 * @author AdNovum Informatik AG
 */
public class LogonEventContextBean {
	
	private long interfaceTypeCd;
	
	public LogonEventContextBean() {
		super();
	}
	
	public long getInterfaceTypeCd() {
		return interfaceTypeCd;
	}

	public void setInterfaceTypeCd(long interfaceTypeCd) {
		this.interfaceTypeCd = interfaceTypeCd;
	}
}

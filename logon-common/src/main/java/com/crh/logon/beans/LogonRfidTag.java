/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.beans;

import java.io.Serializable;


/**
 * @author AdNovum Informatik AG
 */
public class LogonRfidTag implements Serializable {

	private String rfidIdentity;
	private String rfidLabel;
	
	public String getRfidIdentity() {
		return rfidIdentity;
	}

	public void setRfidIdentity(String rfidIdentity) {
		this.rfidIdentity = rfidIdentity;
	}

	public String getRfidLabel() {
		return rfidLabel;
	}
	
	public void setRfidLabel(String rfidLabel) {
		this.rfidLabel = rfidLabel;
	}
	
}

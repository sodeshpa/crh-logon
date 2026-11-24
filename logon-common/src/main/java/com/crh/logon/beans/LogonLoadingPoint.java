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
public class LogonLoadingPoint implements Serializable {

	private LogonRfidObject rfidObject;
	private String weighingType;
	
	public LogonRfidObject getRfidObject() {
		return rfidObject;
	}
	public void setRfidObject(LogonRfidObject rfidObject) {
		this.rfidObject = rfidObject;
	}
	public String getWeighingType() {
		return weighingType;
	}
	public void setWeighingType(String weighingType) {
		this.weighingType = weighingType;
	}
	
	
}

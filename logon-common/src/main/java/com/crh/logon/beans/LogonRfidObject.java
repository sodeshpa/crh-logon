/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.beans;

import java.io.Serializable;
import java.util.List;


/**
 * @author AdNovum Informatik AG
 */
public class LogonRfidObject implements Serializable {

	private String objectID;
	private String objectName;
	private List rfidTagList; // list of LogonRfidTag
	
	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getObjectName() {
		return objectName;
	}
	
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	public List getRfidTagList() {
		return rfidTagList;
	}
	
	public void setRfidTagList(List rfidTagList) {
		this.rfidTagList = rfidTagList;
	}

}

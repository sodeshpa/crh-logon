/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class CalibrationData implements Serializable {
	private static final long serialVersionUID = 5324658215071028611L;
	
	private String tareData;
	private String grossData;
	public String getTareData() {
		return tareData;
	}
	public void setTareData(String tareData) {
		this.tareData = tareData;
	}
	public String getGrossData() {
		return grossData;
	}
	public void setGrossData(String grossData) {
		this.grossData = grossData;
	}
	
	
}

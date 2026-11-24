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
public class GeneralOrderData implements Serializable {
	private static final long serialVersionUID = 6360574497541015313L;
	
	private String containerNumbers;
	private String bimodalWagonNumber;

	public String getContainerNumbers() {
		return containerNumbers;
	}
	public void setContainerNumbers(String containerNumbers) {
		this.containerNumbers = containerNumbers;
	}
	public String getBimodalWagonNumber() {
		return bimodalWagonNumber;
	}
	public void setBimodalWagonNumber(String bimodalWagonNumber) {
		this.bimodalWagonNumber = bimodalWagonNumber;
	}
}

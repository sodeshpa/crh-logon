/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class CompanycodeSareaBean implements Serializable {

	private String companycodeCd;
	private Long sareaId;
	
	public CompanycodeSareaBean(String companycodeCd, Long sareaId) {
		this.companycodeCd = companycodeCd;
		this.sareaId = sareaId;
	}
	
	public String getCompanycodeCd() {
		return companycodeCd;
	}
	
	public void setCompanycodeCd(String companycodeCd) {
		this.companycodeCd = companycodeCd;
	}
	
	public Long getSareaId() {
		return sareaId;
	}
	
	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}
	
}

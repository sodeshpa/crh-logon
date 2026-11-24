/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class TransportData implements Serializable {
	private static final long serialVersionUID = -5062075203534344869L;

	private String chromatReductionComment;
	
	private boolean isCustomerResponsible = false;
	private boolean isCrhResponsible = false;
	private boolean isCarrierResponsible = false;
	
	public boolean renderChromatComment() {
		return StringUtils.isNotEmpty(chromatReductionComment);
	}
	
	public boolean isCustomerResponsible() {
		return isCustomerResponsible;
	}
	public void setCustomerResponsible(boolean isCustomerResponsible) {
		this.isCustomerResponsible = isCustomerResponsible;
	}
	public boolean isCrhResponsible() {
		return isCrhResponsible;
	}
	public void setCrhResponsible(boolean isCrhResponsible) {
		this.isCrhResponsible = isCrhResponsible;
	}
	public boolean isCarrierResponsible() {
		return isCarrierResponsible;
	}
	public void setCarrierResponsible(boolean isCarrierResponsible) {
		this.isCarrierResponsible = isCarrierResponsible;
	}
	public String getChromatReductionComment() {
		return chromatReductionComment;
	}
	public void setChromatReductionComment(String chromatReductionComment) {
		this.chromatReductionComment = chromatReductionComment;
	}
}

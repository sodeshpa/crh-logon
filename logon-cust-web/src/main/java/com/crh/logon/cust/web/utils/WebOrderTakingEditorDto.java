/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * DTO to send data for several UI components in one pack during Web Order Taking
 */
public class WebOrderTakingEditorDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean copyCustomerComment;

	private List<ShiptoBilltoEntity> billTos;
	private List<SapBpEntity> payers;
	private boolean onHoldOrderAndLockedFlag;


	public WebOrderTakingEditorDto() {
		this.copyCustomerComment = false;
		this.billTos = new ArrayList<ShiptoBilltoEntity>();
		this.payers = new ArrayList<SapBpEntity>();
	}
	
	public List<ShiptoBilltoEntity> getBillTos() {
		return billTos;
	}
	public void setBillTos(List<ShiptoBilltoEntity> billTos) {
		this.billTos = billTos;
	}
	
	public List<SapBpEntity> getPayers() {
		return this.payers;
	}

	public void setPayers(List<SapBpEntity> payers) {
		this.payers = payers;
	}

	public boolean isCopyCustomerComment() {
		return copyCustomerComment;
	}

	public void setCopyCustomerComment(boolean copyCustomerComment) {
		this.copyCustomerComment = copyCustomerComment;
	}

	/**
	 * @return the onHoldOrderAndLockedFlag
	 */
	public boolean isOnHoldOrderAndLockedFlag() {
		return onHoldOrderAndLockedFlag;
	}

	/**
	 * @param onHoldOrderAndLockedFlag the onHoldOrderAndLockedFlag to set
	 */
	public void setOnHoldOrderAndLockedFlag(boolean onHoldOrderAndLockedFlag) {
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
	}
}
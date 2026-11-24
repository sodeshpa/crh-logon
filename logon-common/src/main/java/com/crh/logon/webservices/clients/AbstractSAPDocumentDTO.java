package com.crh.logon.webservices.clients;

import java.io.Serializable;

public abstract class AbstractSAPDocumentDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * WS field to SAP: SNDPRN
	 */
	protected String partnerNumberOfSender;

	public AbstractSAPDocumentDTO() {
		super();
	}

	public String getPartnerNumberOfSender() {
		return partnerNumberOfSender;
	}

	public void setPartnerNumberOfSender(String partnerNumberOfSender) {
		this.partnerNumberOfSender = partnerNumberOfSender;
	}

}
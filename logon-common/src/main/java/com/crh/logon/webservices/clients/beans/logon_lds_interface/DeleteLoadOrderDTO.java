/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.logon_lds_interface;

import com.holcim.logon.eventbeans.in.logon_lds_interface.Request;

/**
 * @author AdNovum Informatik AG
 */
public class DeleteLoadOrderDTO extends Request {

	private String loadOrderId;
	private String ldsType;

	public String getLoadOrderId() {
		return loadOrderId;
	}

	public void setLoadOrderId(String loadOrderId) {
		this.loadOrderId = loadOrderId;
	}

	public String getLdsType() {
		return ldsType;
	}

	public void setLdsType(String ldsType) {
		this.ldsType = ldsType;
	}
}

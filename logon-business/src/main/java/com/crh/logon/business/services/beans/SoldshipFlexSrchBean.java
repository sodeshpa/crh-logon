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
public class SoldshipFlexSrchBean implements Serializable {

	private Long shiptoId;
	private Long soldtoId;
	
	public SoldshipFlexSrchBean (Long shiptoId, Long soldtoId) {
		this.shiptoId = shiptoId;
		this.soldtoId = soldtoId;
	}
	
	public Long getShiptoId() {
		return shiptoId;
	}
	public void setShiptoId(Long shiptoId) {
		this.shiptoId = shiptoId;
	}
	public Long getSoldtoId() {
		return soldtoId;
	}
	public void setSoldtoId(Long soldtoId) {
		this.soldtoId = soldtoId;
	}
	
	
}

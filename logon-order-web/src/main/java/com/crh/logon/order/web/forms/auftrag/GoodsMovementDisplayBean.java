/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

import com.crh.logon.persistence.entities.common.GoodsMovementEntity;

/**
 * This bean contains information for one line of good movement event in GoodsMovementPopupTile.jsp
 * 
 * @author AdNovum Informatik AG
 */
public class GoodsMovementDisplayBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private GoodsMovementEntity entity;
	private String movement;
	private String deliveryPlant;
	private String date;
	private String reason;
	private String fromPlant;
	
	public GoodsMovementEntity getEntity() {
		return entity;
	}
	public void setEntity(GoodsMovementEntity entity) {
		this.entity = entity;
	}
	public String getMovement() {
		return movement;
	}
	public void setMovement(String movement) {
		this.movement = movement;
	}
	public String getDeliveryPlant() {
		return deliveryPlant;
	}
	public void setDeliveryPlant(String fromPlant) {
		this.deliveryPlant = fromPlant;
	}
	public String getFromPlant() {
		return fromPlant;
	}
	public void setFromPlant(String fromPlant) {
		this.fromPlant = fromPlant;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
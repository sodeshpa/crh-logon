package com.crh.logon.order.web.beans;

import com.crh.logon.persistence.entities.views.ExtOrderSearchEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ExtOrderSearchBean {
	private static final long serialVersionUID = 1L;

	public static final String ATTR_DELIVERY_FROM = "deliveryFrom";
	public static final String ATTR_DELIVERY_TO = "deliveryTo";

	private String deliveryFrom;
	private String deliveryTo;

	private ExtOrderSearchEntity entity;

	public ExtOrderSearchBean() { }

	public ExtOrderSearchBean(ExtOrderSearchEntity entity) {
		this.entity = entity;
	}

	public ExtOrderSearchEntity getEntity() {
		return entity;
	}

	public String getDeliveryFrom() {
		return deliveryFrom;
	}

	public void setDeliveryFrom(String deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}

	public String getDeliveryTo() {
		return deliveryTo;
	}

	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
}

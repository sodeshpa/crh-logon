/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.order.pages;

/**
 * @author AdNovum Informatik AG
 */
public class OrderResult {

	private final String orderNr;

	private final String custOrderNr;

	private String deliveryFromDate;

	/**
	 * @param orderNr
	 * @param custOrderNr
	 */
	public OrderResult(String orderNr, String custOrderNr, String deliveryFromDate) {
		this.orderNr = orderNr;
		this.custOrderNr = custOrderNr;
		this.deliveryFromDate = deliveryFromDate;
	}

	public String getOrderNr() {
		return orderNr;
	}

	public String getCustOrderNr() {
		return custOrderNr;
	}

	public String getDeliveryFromDate() {
		return deliveryFromDate;
	}
}
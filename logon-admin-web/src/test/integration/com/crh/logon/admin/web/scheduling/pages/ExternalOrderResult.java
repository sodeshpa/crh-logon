/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling.pages;

public class ExternalOrderResult {

	private String orderNumber;

	private String deliveryFromDate;

	/**
	 * @return the deliveryFromDate
	 */
	public String getDeliveryFromDate() {
		return deliveryFromDate;
	}

	/**
	 * @param deliveryFromDate the deliveryFromDate to set
	 */
	public void setDeliveryFromDate(String deliveryFromDate) {
		this.deliveryFromDate = deliveryFromDate;
	}

	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

}

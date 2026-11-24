/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.order.pages;

public enum OrderStatus {
	SCHEDULED("Scheduled"),
	RELEASED_FOR_EXECUTION("Released for execution"),
	ARRIVED_AT_LOADING_POINT("Arrived at loadingpoint"),
	SHIPPING("Shipping");

	String status;

	private OrderStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}
}

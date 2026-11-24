/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class ReportingBean implements Serializable{

	private static final long serialVersionUID = 1L;

	//Prozess
	private String orderQuantityPlan;
	private boolean orderQuantityDev;
	
	
	public boolean isOrderQuantityDev() {
		return orderQuantityDev;
	}

	public void setOrderQuantityDev(boolean orderQuantityDev) {
		this.orderQuantityDev = orderQuantityDev;
	}

	public String getOrderQuantityPlan() {
		return orderQuantityPlan;
	}

	public void setOrderQuantityPlan(String orderQuantityPlan) {
		this.orderQuantityPlan = orderQuantityPlan;
	}

}


/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.crh.logon.utils.ListRange;

/**
 * @author AdNovum Informatik AG
 */
public class OrderSearchListRange extends ListRange<OrderSearchBean> {
	
	private static final long serialVersionUID = 1L;

	private int orderCount;
	
	public OrderSearchListRange() {}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
}

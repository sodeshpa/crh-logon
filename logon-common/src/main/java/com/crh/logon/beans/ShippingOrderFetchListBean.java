/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.beans;

import java.io.Serializable;
import java.util.List;


/**
 * @author AdNovum Informatik AG
 * 
 * This bean is used to represent the editability of an order-load on the "Loading(Shipping)" screen.
 */
public class ShippingOrderFetchListBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * List<OrdItemAssignmtLoadEntity>
	 */
	private List orderItemAssignmentLoadList;
	private Long totalCount;
	
	public List getOrderItemAssignmentLoadList() {
		return orderItemAssignmentLoadList;
	}
	public void setOrderItemAssignmentLoadList(List orderItemAssignmentLoadList) {
		this.orderItemAssignmentLoadList = orderItemAssignmentLoadList;
	}
	public Long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
}

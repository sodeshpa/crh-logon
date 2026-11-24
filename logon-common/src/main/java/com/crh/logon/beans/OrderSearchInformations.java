package com.crh.logon.beans;

import java.io.Serializable;

public class OrderSearchInformations implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long totalOrderCount;
	private Long countOrdersBeforeActivePage;
	
	public OrderSearchInformations(long totalOrderCount, long orderCountBeforeActivePage) {
		this.totalOrderCount = totalOrderCount;
		this.countOrdersBeforeActivePage = orderCountBeforeActivePage;
	}

	public Long getTotalOrderCount() {
		return totalOrderCount;
	}

	public void setTotalOrderCount(Long totalOrderCount) {
		this.totalOrderCount = totalOrderCount;
	}

	public Long getCountOrdersBeforeActivePage() {
		return countOrdersBeforeActivePage;
	}
	
	public void setCountOrdersBeforeActivePage(Long countOrdersBeforeActivePage) {
		this.countOrdersBeforeActivePage = countOrdersBeforeActivePage;
	}
}

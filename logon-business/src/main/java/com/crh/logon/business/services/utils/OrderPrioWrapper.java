/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class OrderPrioWrapper implements Comparable<OrderPrioWrapper>, Serializable {
	
	private static final long serialVersionUID = 1L;

	private OrderWrapper order;
	private Long prio;
	
	public OrderWrapper getOrder() {
		return order;
	}

	public Long getPrio() {
		return prio;
	}

	public OrderPrioWrapper(OrderWrapper order, Long prio) {
		super();
		this.order = order;
		this.prio = prio;
	}

	public int compareTo(OrderPrioWrapper other) {
		Long thisOrderNo = order.isExternalOrder() ? order.getExtOrder().getOrderNumber() : order.getLogonOrder().getOrderNumber(); 
		Long toOrderNo = other.getOrder().isExternalOrder() ? other.getOrder().getExtOrder().getOrderNumber() : other.getOrder().getLogonOrder().getOrderNumber(); 
		if (other.prio == null) {
			if (prio == null) {
				return thisOrderNo.compareTo(toOrderNo);
			}
			return -1;
		}
		if (prio == null) {
			return 1;
		}
		if (prio.equals(other.prio)){
			return thisOrderNo.compareTo(toOrderNo);
		}
		return prio.compareTo(other.prio);
	}
	
	@Override
	public boolean equals(Object o) {
		return compareTo((OrderPrioWrapper) o) == 0;
	}
}

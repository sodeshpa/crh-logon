/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.persistence.entities.views.DispoLoadtargetEntity;

/**
 * @author AdNovum Informatik AG
 */
public class DispoLoadtargetPrioWrapper implements Comparable, Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private Long count;
	private DispoLoadtargetEntity dl;
	
	public DispoLoadtargetPrioWrapper(Long count,
			DispoLoadtargetEntity dl) {
		this.count = count;
		this.dl = dl;
	}

	public int compareTo(Object o) {
		DispoLoadtargetPrioWrapper other = (DispoLoadtargetPrioWrapper)o;
		if (count.equals(other.count)){
			return dl.getShippingpointLoadtargetId().compareTo(other.dl.getShippingpointLoadtargetId());
		}
		return -count.compareTo(other.count);
	}
	
	@Override
	public boolean equals(Object o) {
		return compareTo(o) == 0;
	}

	public Long getCount() {
		return count;
	}

	public DispoLoadtargetEntity getTarget() {
		return dl;
	}

	public void decreaseCount() {
		if (count.intValue() > 0) {
			count = new Long(count.intValue() - 1);
		}
	}
}

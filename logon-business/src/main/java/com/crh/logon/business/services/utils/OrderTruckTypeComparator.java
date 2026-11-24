package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.Comparator;

import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;

public final class OrderTruckTypeComparator implements Comparator<OrderTruckTypeEntity> {
	public int compare(OrderTruckTypeEntity t1, OrderTruckTypeEntity t2) {
		final Long t1Prio = t1.getEvaluationPriority();
		final Long t2Prio = t2.getEvaluationPriority();
		final BigDecimal t1Capacity = t1.getTruckType().getPlanningNetWeightT();
		final BigDecimal t2Capacity = t2.getTruckType().getPlanningNetWeightT();
		final BigDecimal t1Volume = t1.getTruckType().getVolumeM3();
		final BigDecimal t2Volume = t2.getTruckType().getVolumeM3();
		final Long t1Cd = t1.getTruckType().getTruckTypeCd();
		final Long t2Cd = t2.getTruckType().getTruckTypeCd();
		
		return TruckWagonTypeWrapperUtil.genericVehicleCompareTo(t1Prio, t2Prio, t1Capacity,
				t2Capacity, t1Volume, t2Volume,
				t1Cd, t2Cd);
	}
}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.persistence.entities.views.OrderItemOrderedQtyEntity;

/**
 * Wrapper class for OrderItemOrderedQtyEntity and the material name.
 * Used on Simple Vehicle Scheduling screen,
 * 
 * @author AdNovum Informatik AG
 */
public class OrderItemOrderedQtyWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private final OrderItemOrderedQtyEntity orderItemOrderedQtyEntity;

	private final String materialName;

	public OrderItemOrderedQtyWrapper(OrderItemOrderedQtyEntity orderItemOrderedQtyEntity,
			String materialName) {

		this.orderItemOrderedQtyEntity = orderItemOrderedQtyEntity;
		this.materialName = materialName;
	}

	public OrderItemOrderedQtyEntity getOrderItemOrderedQtyEntity() {
		return orderItemOrderedQtyEntity;
	}

	public String getMaterialName() {
		return materialName;
	}
}

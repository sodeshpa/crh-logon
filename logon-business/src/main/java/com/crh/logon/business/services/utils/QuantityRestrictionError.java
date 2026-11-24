/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;

import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * Stores the details of the validation error that happens when ordered item quantity is restricted by
 * contract.
 * 
 * @author AdNovum Informatik AG
 */
public class QuantityRestrictionError implements Serializable {

	//private static final JcanLogger LOG = JcanLoggerFactory.getLogger(QuantityRestrictionError.class);
	private static final long serialVersionUID = 1L;

	private final OrderItemEntity orderItem;
	private final BigDecimal openQuantity;
	private final String targetQuantityUomCd;

	/**
	 * The ordered quantity summed up for each order item belonging to the same contract item. This is
	 * especially useful at customer order creation where you can split it to single-delivery orders.
	 */
	private final BigDecimal sumOrderedQuantity;


	public QuantityRestrictionError(OrderItemEntity orderItem, BigDecimal openQuantity,
			String targetQuantityUomCd, BigDecimal sumOrderedQuantity) {
		this.orderItem = orderItem;
		this.openQuantity = openQuantity;
		this.targetQuantityUomCd = targetQuantityUomCd;
		this.sumOrderedQuantity = sumOrderedQuantity;
	}

	public OrderItemEntity getOrderItem() {
		return orderItem;
	}


	public BigDecimal getOpenQuantity() {
		return openQuantity;
	}


	public String getTargetQuantityUomCd() {
		return targetQuantityUomCd;
	}


	public BigDecimal getSumOrderedQuantity() {
		return sumOrderedQuantity;
	}


	public String toString() {
		String str = "";
		str += "][orderItem=" + orderItem;
		str += "][openQuantity=" + openQuantity;
		str += "][targetQuantityUomCd=" + targetQuantityUomCd;
		str += "][sumOrderedQuantity=" + sumOrderedQuantity;
		return str;
	}

}

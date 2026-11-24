/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;

import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * This wrapper contains the difference between the order, which comes genuine
 * from the DB and the one which got modified. The wrapper holds the old and new
 * quantity and uom.
 * 
 * @author AdNovum Informatik AG
 */
public class OrderSplitDiffWrapper {
	
	private OrderEntity order;
	private BigDecimal oldOrderedQty;
	private String oldOrderedUom;
	private BigDecimal newOrderedQty;
	private String newOrderedUom;
	
	public OrderEntity getOrder() {
		return order;
	}
	public void setOrder(OrderEntity order) {
		this.order = order;
	}
	public BigDecimal getOldOrderedQty() {
		return oldOrderedQty;
	}
	public void setOldOrderedQty(BigDecimal oldOrderedQty) {
		this.oldOrderedQty = oldOrderedQty;
	}
	public String getOldOrderedUom() {
		return oldOrderedUom;
	}
	public void setOldOrderedUom(String oldOrderedUom) {
		this.oldOrderedUom = oldOrderedUom;
	}
	public BigDecimal getNewOrderedQty() {
		return newOrderedQty;
	}
	public void setNewOrderedQty(BigDecimal newOrderedQty) {
		this.newOrderedQty = newOrderedQty;
	}
	public String getNewOrderedUom() {
		return newOrderedUom;
	}
	public void setNewOrderedUom(String newOrderedUom) {
		this.newOrderedUom = newOrderedUom;
	}

}

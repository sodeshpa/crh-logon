/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * @author AdNovum Informatik AG
 */
public class SplittedOrderItemData implements Serializable {
	
	private static final long serialVersionUID = -5384109634770704600L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SplittedOrderItemData.class);
	
	private int index;
	private OrderItemEntity orderItem;
	private BigDecimal orderedQuantity;
	private String orderedUom;
	private MaterialType materialType;
	private Long sapMaterialId;

	/**
	 * Constructor for exclusive use by DWR.
	 */
	public SplittedOrderItemData() {
	}
	
	public SplittedOrderItemData(OrderItemEntity orderItem, int index, MaterialType materialType) {
		this.orderItem = orderItem;
		this.orderedQuantity = orderItem.getOrderedQuantity();
		this.orderedUom = orderItem.getOrderedUom().getUomCd();
		this.index = index;
		this.materialType = materialType;
		this.sapMaterialId = orderItem.getSapMaterial().getSapMaterialId();
	}
	
	/**
	 * Updates values that could be changed on the UI.
	 */
	public void updateFrom(SplittedOrderItemData other) {
		// [[
		Object[][] params = {{"other", other}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateFrom", params);
		}
		// ]]
		try {
			
			orderedQuantity = other.orderedQuantity;
			orderItem.setOrderedQuantity(orderedQuantity);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void setOrderItem(OrderItemEntity orderItem) {
		this.orderItem = orderItem;
	}

	public void setOrderedQuantity(BigDecimal orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	public void setOrderedUom(String orderedUom) {
		this.orderedUom = orderedUom;
	}

	public OrderItemEntity getOrderItem() {
		return orderItem;
	}

	public BigDecimal getOrderedQuantity() {
		return orderedQuantity;
	}

	public String getOrderedUom() {
		return orderedUom;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public MaterialType getMaterialType() {
		return materialType;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}
	
}
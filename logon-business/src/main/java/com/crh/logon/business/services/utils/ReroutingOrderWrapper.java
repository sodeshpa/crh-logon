/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author AdNovum Informatik AG
 */

/*
 * 1 ReroutingOrderWrapper object represents 1 line in the ReroutePopup.
 * 1 line belongs to 1 orderItem, but the orderItems belonging to one order
 * are grouped together, and the orders belonging to one customerOrder are
 * also grouped together. isFirstItem flag is true exactly once within a 
 * customerOrder, this ensures that the customerOrder cannot be split in
 * the reroute process.
 */
public class ReroutingOrderWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	//order related members
	/* isFirstItem flag is true exactly once within a customerOrder. this represents 
	 * the checkbox in the ReroutePopup: the checkbox is displayed exactly if this 
	 * flag is true.*/
	private boolean isFirstItem;

	private Long orderId;

	private Long orderNumber;

	private String shipToName;

	//orderItem related members
	private Long orderItemId;

	private String materialName;

	private BigDecimal orderedQuantity;

	private String orderedUomName;

	public ReroutingOrderWrapper(boolean isFirstItem, long orderId, long orderNumber,
			String shipToName, long orderItemId, String materialName, BigDecimal orderedQuantity,
			String orderedUomName) {
		this.isFirstItem = isFirstItem;
		this.orderId = orderId;
		this.orderNumber = orderNumber;
		this.shipToName = shipToName;
		this.orderItemId = orderItemId;
		this.materialName = materialName;
		this.orderedQuantity = orderedQuantity;
		this.orderedUomName = orderedUomName;
	}

	public boolean getIsFirstItem() {
		return isFirstItem;
	}

	public void setIsFirstItem(boolean isFirstItem) {
		this.isFirstItem = isFirstItem;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getShipToName() {
		return shipToName;
	}

	public void setShipToName(String shipToName) {
		this.shipToName = shipToName;
	}

	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public BigDecimal getOrderedQuantity() {
		return orderedQuantity;
	}

	public void setOrderedQuantity(BigDecimal orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	public String getOrderedUomName() {
		return orderedUomName;
	}

	public void setOrderedUomName(String orderedUomName) {
		this.orderedUomName = orderedUomName;
	}

}

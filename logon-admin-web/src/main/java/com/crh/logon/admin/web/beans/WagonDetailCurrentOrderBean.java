/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;

/**
 * Mainly read only bean containing info about the current order of the wagon.
 * @author AdNovum Informatik AG
 */
public class WagonDetailCurrentOrderBean implements Serializable {
	
	private OrderEntity order;
	
	public WagonDetailCurrentOrderBean(){
		this.order = new OrderEntity(); // not persisted, used in presentation only
	}

	private static final long serialVersionUID = 1L;
	private String orderStatus;
	private Long orderStatusCd;
	private String orderStatusSince;
	private String orderStatusSinceTimeString;
	private String orderNumber;
	private Long orderId;
	private String distributionDestCd;
	private Long deliveryTypeCd;
	
	//holdBackReOrderFlag is  changeable on wagon detail too
	private Boolean railHoldBackReOrderFlag;

	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void setOrderStatusSince(String orderStatusSince) {
		this.orderStatusSince = orderStatusSince;
	}

	public String getOrderStatusSince() {
		return orderStatusSince;
	}

	/**
	 * Deletes the properties, which must be empty for a new wagon.
	 */
	public void resetNewWagonProperties(){
		this.order = new OrderEntity(); // not persisted, used in presentation only
		setOrderStatus(null);
		setOrderStatusSince(null);
		setOrderStatusSinceTimeString(null);
		setOrderNumber(null);
		setOrderId(null);
		setOrderStatusCd(null);
		setDeliveryTypeCd(null);
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getDistributionDestCd() {
		return distributionDestCd;
	}
	public void setDistributionDestCd(String distributionDestCd) {
		this.distributionDestCd = distributionDestCd;
	}
	public void setOrderStatusSinceTimeString(String orderStatusSinceTimeString) {
		this.orderStatusSinceTimeString = orderStatusSinceTimeString;
	}
	public String getOrderStatusSinceTimeString() {
		return orderStatusSinceTimeString;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderStatusCd() {
		return orderStatusCd;
	}

	public void setOrderStatusCd(Long orderStatusCd) {
		OrderStatusBsEntity status = new OrderStatusBsEntity();
		status.setOrderStatusCd(orderStatusCd);
		this.order.setOrderStatus(status);		
		this.orderStatusCd = orderStatusCd;
	}

	public Boolean getRailHoldBackReOrderFlag() {
		return railHoldBackReOrderFlag;
	}

	public void setRailHoldBackReOrderFlag(Boolean railHoldBackReOrderFlag) {
		if ( railHoldBackReOrderFlag != null && railHoldBackReOrderFlag.booleanValue() ) {			
			this.railHoldBackReOrderFlag = railHoldBackReOrderFlag;
		}
		else {
			this.railHoldBackReOrderFlag = null;
		}
	}
	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}
	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		DeliveryTypeBsEntity type = new DeliveryTypeBsEntity();
		type.setDeliveryTypeCd(deliveryTypeCd);
		this.order.setDeliveryType(type);	
		this.deliveryTypeCd = deliveryTypeCd;
	}
	
	public OrderEntity getOrder() {
		return order;
	}

}

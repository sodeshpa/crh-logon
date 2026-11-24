/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.common.builders;

import java.util.Date;

import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;

public class OrderEntityBuilder {
	
	private DeliveryTypeBsEntity deliveryType;
	private OrderStatusBsEntity orderStatus;
	private Long transportDurationMin;
	private Date actualDeliveryDat;
	private SapContractEntity sapContract;
	
	private OrderEntityBuilder() {
	}

	public static OrderEntityBuilder get() {
		return new OrderEntityBuilder();
	}
	public void reset() {
		deliveryType = null;
		orderStatus = null;
		transportDurationMin = null;
		actualDeliveryDat = null;
		sapContract = null;
		
	}
	
	public OrderEntity build() {
		OrderEntity order = new OrderEntity();
		order.setDeliveryType(deliveryType);
		order.setOrderStatus(orderStatus);
		order.setTransportDurationMin(transportDurationMin);
		order.setActualDeliveryDat(actualDeliveryDat);
		order.setSapContract(sapContract);
		return order;
	}
	
	public OrderEntityBuilder deliveryType(DeliveryTypeBsEntity deliveryType) {
		this.deliveryType = deliveryType;
		return this;
	}
	
	public OrderEntityBuilder orderStatus(OrderStatusBsEntity orderStatus) {
		this.orderStatus = orderStatus;
		return this;
	}
	
	public OrderEntityBuilder transportDurationMin(Long transportDurationMin) {
		this.transportDurationMin = transportDurationMin;
		return this;
	}
	
	public OrderEntityBuilder actualDeliveryDat(Date actualDeliveryDat) {
		this.actualDeliveryDat = actualDeliveryDat;
		return this;
	}
	
	public OrderEntityBuilder sapContract(SapContractEntity sapContract) {
		this.sapContract = sapContract;
		return this;
	}
}

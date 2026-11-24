/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class CustOrderTabOrderBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<CustOrderTabOrderItemBean> materials;
	private String orderId;
	private String orderNumber;
	private String actualDeliveryDate;
	private String actualDeliveryTime;
	private String deliveryFromDate;
	private String deliveryFromTime;
	private String deliveryToDate;
	private String deliveryToTime;
	private String orderStatus;
	private Boolean serviceMaterialOnlyOrder;
	/**
	 * The delivered simple vehicle's name (if exists)
	 */
	private String vehicleName;

	
	public String getOrderId() {
		return orderId;
	}
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getActualDeliveryDate() {
		return actualDeliveryDate;
	}

	public String getActualDeliveryTime() {
		return actualDeliveryTime;
	}

	public void setActualDeliveryDate(String actualDeliveryDate) {
		this.actualDeliveryDate = actualDeliveryDate;
	}

	public void setActualDeliveryTime(String actualDeliveryTime) {
		this.actualDeliveryTime = actualDeliveryTime;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getDeliveryFromDate() {
		return deliveryFromDate;
	}

	public String getDeliveryFromTime() {
		return deliveryFromTime;
	}

	public void setDeliveryFromDate(String deliveryFromDate) {
		this.deliveryFromDate = deliveryFromDate;
	}

	public void setDeliveryFromTime(String deliveryFromTime) {
		this.deliveryFromTime = deliveryFromTime;
	}

	public String getDeliveryToDate() {
		return deliveryToDate;
	}

	public String getDeliveryToTime() {
		return deliveryToTime;
	}

	public void setDeliveryToDate(String deliveryToDate) {
		this.deliveryToDate = deliveryToDate;
	}

	public void setDeliveryToTime(String deliveryToTime) {
		this.deliveryToTime = deliveryToTime;
	}

	public List<CustOrderTabOrderItemBean> getMaterials() {
		return materials;
	}

	public void setMaterials(List<CustOrderTabOrderItemBean> materials) {
		this.materials = materials;
	}

	public Boolean getServiceMaterialOnlyOrder() {
		return serviceMaterialOnlyOrder;
	}

	public void setServiceMaterialOnlyOrder(Boolean serviceMaterialOnlyOrder) {
		this.serviceMaterialOnlyOrder = serviceMaterialOnlyOrder;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	
}

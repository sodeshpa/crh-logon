/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class OrderItemFinalBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long orderItemId;
	private String orderItemNo;
	private Long sapMaterialid;
	private String sapMaterialName;
	private String deliveredQuantity;
	private String deliveredUom;
	private String loadedQuantity;
	private String loadedUom;
	private String returnedQuantity;
	private String returnedUom;
	private String orderedQuantity;
	private String orderedUom;
	private boolean creditNote;
	
	//Prozess
	private String quantityPlan;
	private String quantityActual;
	private boolean quantityFlag;
	private String deliveryTypePlan;
	private String deliveryTypeActual;
	private boolean deliveryTypeFlag;
	private boolean basedOnDispocard;
	private boolean loadedByLds;
	
	private String weighingType;
	
	//Vergutung
	private String lc;
	private String netWeight;
	private String netWeightLC;
	private String distance;
	private String distanceLC;
	private String quantity;
	private String quantityLC;

	
	public String getWeighingType() {
		return weighingType;
	}
	public void setWeighingType(String weighingType) {
		this.weighingType = weighingType;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getNetWeight() {
		return netWeight;
	}
	public void setNetWeight(String netWeight) {
		this.netWeight = netWeight;
	}
	public String getNetWeightLC() {
		return netWeightLC;
	}
	public void setNetWeightLC(String netWeightLC) {
		this.netWeightLC = netWeightLC;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDistanceLC() {
		return distanceLC;
	}
	public void setDistanceLC(String distanceLC) {
		this.distanceLC = distanceLC;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getQuantityLC() {
		return quantityLC;
	}
	public void setQuantityLC(String quantityLC) {
		this.quantityLC = quantityLC;
	}
	public String getQuantityPlan() {
		return quantityPlan;
	}
	public void setQuantityPlan(String quantityPlan) {
		this.quantityPlan = quantityPlan;
	}
	public String getQuantityActual() {
		return quantityActual;
	}
	public void setQuantityActual(String quantityActual) {
		this.quantityActual = quantityActual;
	}
	public boolean isQuantityFlag() {
		return quantityFlag;
	}
	public void setQuantityFlag(boolean quantityFlag) {
		this.quantityFlag = quantityFlag;
	}
	public String getDeliveryTypePlan() {
		return deliveryTypePlan;
	}
	public void setDeliveryTypePlan(String deliveryTypePlan) {
		this.deliveryTypePlan = deliveryTypePlan;
	}
	public String getDeliveryTypeActual() {
		return deliveryTypeActual;
	}
	public void setDeliveryTypeActual(String deliveryTypeActual) {
		this.deliveryTypeActual = deliveryTypeActual;
	}
	public boolean isDeliveryTypeFlag() {
		return deliveryTypeFlag;
	}
	public void setDeliveryTypeFlag(boolean deliveryTypeFlag) {
		this.deliveryTypeFlag = deliveryTypeFlag;
	}
	public Long getSapMaterialid() {
		return sapMaterialid;
	}
	public void setSapMaterialid(Long sapMaterialid) {
		this.sapMaterialid = sapMaterialid;
	}
	public Long getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getSapMaterialName() {
		return sapMaterialName;
	}
	public void setSapMaterialName(String sapMaterialName) {
		this.sapMaterialName = sapMaterialName;
	}
	public boolean isCreditNote() {
		return creditNote;
	}
	public void setCreditNote(boolean creditNote) {
		this.creditNote = creditNote;
	}	
	public String getLoadedQuantity() {
		return loadedQuantity;
	}
	public void setLoadedQuantity(String loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}
	public String getLoadedUom() {
		return loadedUom;
	}
	public void setLoadedUom(String loadedUom) {
		this.loadedUom = loadedUom;
	}
	public String getReturnedQuantity() {
		return returnedQuantity;
	}
	public void setReturnedQuantity(String returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}
	public String getReturnedUom() {
		return returnedUom;
	}
	public void setReturnedUom(String returnedUom) {
		this.returnedUom = returnedUom;
	}
	
	public String getOrderItemNo() {
		return orderItemNo;
	}
	public void setOrderItemNo(String orderItemNo) {
		this.orderItemNo = orderItemNo;
	}
	public String getDeliveredQuantity() {
		return deliveredQuantity;
	}
	public void setDeliveredQuantity(String deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}
	public String getDeliveredUom() {
		return deliveredUom;
	}
	public void setDeliveredUom(String deliveredUom) {
		this.deliveredUom = deliveredUom;
	}
	public String getOrderedQuantity() {
		return orderedQuantity;
	}
	public void setOrderedQuantity(String orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}
	public String getOrderedUom() {
		return orderedUom;
	}
	public void setOrderedUom(String orderedUom) {
		this.orderedUom = orderedUom;
	}
	public boolean isBasedOnDispocard() {
		return basedOnDispocard;
	}
	public void setBasedOnDispocard(boolean basedOnDispocard) {
		this.basedOnDispocard = basedOnDispocard;
	}
	public boolean isLoadedByLds() {
		return loadedByLds;
	}
	public void setLoadedByLds(boolean loadedByLds) {
		this.loadedByLds = loadedByLds;
	}
	
}


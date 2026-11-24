/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.crh.logon.business.services.utils.LogonUtil;

/**
 * @author AdNovum Informatik AG
 */
public class OrderTakingEditorParamsDto {
	
	private int editorIdx;
	private int orderItemIdx;
	private Long sapMatId;
	private String supplyPlantCd; 
	private Long spId;
	private Long poolId;
	private Long commercialCarrierId;
	
	private Long shiptoSiloId;
	
	// TODO (emoke Sep 8, 2011):These four could be removed, since all dropdowns should use the combined sarea+material selection (minimal)
	//at the moment though bimodal rail order creation heavily depends on these as well.
	private Long sareaId;
	private Long shiptoSareaRestrId;
	private Long sapContractId;
	private Long sapContractItemId;
	
	private String compositeIdentifier;
	
	// --- For determining the price ---
	private String trspZoneCd;
	
	// --- For calculating actual delivery date for rail orders ---
	private Date delFromDat;
	private Date delToDat;
	
	// --- For Bimodal Rail Order Item list and for Transport Condition calculation for ship-tos with non-fixed location ---
	private BigDecimal quantity;
	
	private Long delTypeCd;
	
	// --- For Bimodal Rail Order Item list ---
	private String prodPlantCd;
	private String uomCd;
	
	public int getEditorIdx() {
		return editorIdx;
	}
	public void setEditorIdx(int editorIdx) {
		this.editorIdx = editorIdx;
	}

	public int getOrderItemIdx() {
		return orderItemIdx;
	}

	public void setOrderItemIdx(int orderItemIdx) {
		this.orderItemIdx = orderItemIdx;
	}

	public Long getSapMatId() {
		return sapMatId;
	}
	public void setSapMatId(Long sapMatId) {
		this.sapMatId = sapMatId;
	}
	public String getSupplyPlantCd() {
		return supplyPlantCd;
	}
	public void setSupplyPlantCd(String supplyPlantCd) {
		this.supplyPlantCd = supplyPlantCd;
	}
	public Long getSpId() {
		return spId;
	}
	public void setSpId(Long spId) {
		this.spId = spId;
	}
	public Long getPoolId() {
		return poolId;
	}
	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}
	public Long getCommercialCarrierId() {
		return commercialCarrierId;
	}
	public void setCommercialCarrierId(Long commercialCarrierId) {
		this.commercialCarrierId = commercialCarrierId;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public Long getDelTypeCd() {
		return delTypeCd;
	}
	public void setDelTypeCd(Long delTypeCd) {
		this.delTypeCd = delTypeCd;
	}
	public String getProdPlantCd() {
		return prodPlantCd;
	}
	public void setProdPlantCd(String prodPlantCd) {
		this.prodPlantCd = prodPlantCd;
	}
	public String getUomCd() {
		return uomCd;
	}
	public void setUomCd(String uomCd) {
		this.uomCd = uomCd;
	}
	public Date getDelFromDat() {
		return delFromDat;
	}
	public void setDelFromDat(Date delFromDat) {
		this.delFromDat = delFromDat;
	}
	public Date getDelToDat() {
		return delToDat;
	}
	public void setDelToDat(Date delToDat) {
		this.delToDat = delToDat;
	}
	public String getTrspZoneCd() {
		return trspZoneCd;
	}
	public void setTrspZoneCd(String trspZoneCd) {
		this.trspZoneCd = trspZoneCd;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	public Long getSareaId() {
		return sareaId;
	}
	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}
	public Long getShiptoSareaRestrId() {
		return shiptoSareaRestrId;
	}
	public void setShiptoSareaRestrId(Long shiptoSareaRestrId) {
		this.shiptoSareaRestrId = shiptoSareaRestrId;
	}
	public Long getSapContractId() {
		return sapContractId;
	}
	public void setSapContractId(Long sapContractId) {
		this.sapContractId = sapContractId;
	}
	public Long getSapContractItemId() {
		return sapContractItemId;
	}
	public void setSapContractItemId(Long sapContractItemId) {
		this.sapContractItemId = sapContractItemId;
	}
	public String getCompositeIdentifier() {
		return compositeIdentifier;
	}
	public void setCompositeIdentifier(String compositeIdentifier) {
		this.compositeIdentifier = compositeIdentifier;
	}
	public Long getShiptoSiloId() {
		return shiptoSiloId;
	}
	public void setShiptoSiloId(Long shiptoSiloId) {
		this.shiptoSiloId = shiptoSiloId;
	}
    public Boolean getSapSuppressMinChargeFlag() {
		return sapSuppressMinChargeFlag;
	}
	public void setSapSuppressMinChargeFlag(Boolean sapSuppressMinChargeFlag) {
		this.sapSuppressMinChargeFlag = sapSuppressMinChargeFlag;
	}
	public Boolean getFreeDeliveryFlag() {
		return freeDeliveryFlag;
	}
	public void setFreeDeliveryFlag(Boolean freeDeliveryFlag) {
		this.freeDeliveryFlag = freeDeliveryFlag;
	}
	public Boolean getReleaseSapFlag() {
		return releaseSapFlag;
	}
	public void setReleaseSapFlag(Boolean releaseSapFlag) {
		this.releaseSapFlag = releaseSapFlag;
	}
	public Boolean getOpenCustorderFlag() {
		return openCustorderFlag;
	}
	public void setOpenCustorderFlag(Boolean openCustorderFlag) {
		this.openCustorderFlag = openCustorderFlag;
	}
	public Boolean getOnHoldOrderAndLockedFlag() {
		if (LogonUtil.isAnyTypeOfPickupOrder(getDelTypeCd())) {
			return false;
		}
		return onHoldOrderAndLockedFlag;
	}
	public void setOnHoldOrderAndLockedFlag(Boolean onHoldOrderAndLockedFlag) {
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
	}

	private Boolean freeDeliveryFlag;
    private Boolean releaseSapFlag;
    private Boolean sapSuppressMinChargeFlag;
    private Boolean openCustorderFlag;
    private Boolean onHoldOrderAndLockedFlag;
    private Boolean autoReleaseFlag;

	public Boolean getAutoReleaseFlag() {
		return autoReleaseFlag;
	}
	public void setAutoReleaseFlag(Boolean automaticReleaseFlag) {
		this.autoReleaseFlag = automaticReleaseFlag;
	}

	private Long mixDesignId;
	
	public void setMixDesignId(Long mixDesignId) {
		this.mixDesignId = mixDesignId;
	}
	
	
    public Long getMixDesignId() {
		return mixDesignId;
	}
}

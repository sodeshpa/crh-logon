/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.math.BigDecimal;

import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * The class contains only Long, String, etc. values, and not entitites. This has currently historical reasons
 * and in the long run it's supposed to be removed, and completely replaced by DeliverySettingDTO objects!
 * If possible do not use this class!
 * 
 * @author AdNovum Informatik AG
 *
 */
public class DeliverySettingBasicDTO extends AbstractDeliverySetting implements DeliverySetting {

	private static final long serialVersionUID = 1L;

	public static final int DELIVERY_SETTING_BASED_ON_CONTRACT = 0;
	public static final int DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION = 1;
	public static final int DELIVERY_SETTING_BASED_ON_RESTRICTION = 2;
	public static final int DELIVERY_SETTING_NOT_DEFINED = 3;
	
	public static final int CONTRACT_TYPE_NONE = 1;
	public static final int CONTRACT_GENERIC_MATERIAL = 2;
	public static final int CONTRACT_EXPLICITELY_DEFINED = 3;
	public static final int CONTRACT_TYPE_NOT_RESOLVABLE = 4;

	//please make sure that in case you name field "compositeIdentifier" differently the code parts which use this also change!
	//name of the property attribute
	public static final String ATTR_COMPOSITE_IDENTIFIER = "compositeIdentifier";
//	private final  String compositeIdentifier;
//	private final int deliverySettingType;
//	private final int contractType;
	
	private SareaEntity sarea;
	
	//please make sure that in case you name field "sapMaterial" differently the code parts which use this also change!
	//name of the property attribute
	public static final String ATTR_MATERIAL_PACKAGE_CD = "sapMaterial.materialPackage." + MaterialPackageBsEntity.ATTR_MATERIAL_PACKAGE_CD;	
//	private final SapMaterialEntity sapMaterial;
	
	protected Long shippingpointId;
	private Long loadingpointId;
	private Long billtoSapBpId; 
	private Long commercialCarrierId;
	private Long executingCarrierId;
	private Long mixDesignId;
	
	
	private Boolean autoCarrierAssignmentFlag;
	private Boolean autoPoolAssignmentFlag;
	private Boolean autoReleaseFlag;

	private Boolean exactCustorderQtyFlag;
    private Boolean exactSdorderQtyFlag;    
    
    private Boolean freeDeliveryFlag;
    private Boolean releaseSapFlag;
    private Boolean sapSuppressMinChargeFlag;
    private Boolean openCustorderFlag;
    private Boolean onHoldOrderAndLockedFlag;
    
	//please make sure that in case you name field "pool" differently the code parts which use this also change!
	//name of the property attribute
	private Long poolId;
	public static final String ATTR_POOL_ID = "pool";
	
	private Long shiptoSiloId;

	//please make sure that in case you name field "deliveryTypeCd" differently the code parts which use this also change!
	//name of the property attribute
	public static final String ATTR_DELIVERY_TYPE_CD = DeliveryTypeBsEntity.ATTR_DELIVERY_TYPE_CD;
	private Long deliveryTypeCd;
	private String productionPlantCd;
	private String supplyPlantCd;
	private Long payerSapBpId;
	protected  ShiptoSareaRestrEntity shiptoSareaRestr;
	
	private BigDecimal quantity;
	private String uomCd;
	private BigDecimal perSdorderQuantity;
	private String perSdorderUomCd;
	private Double deliveryFreqEveryTime;
	private String deliveryFreqEveryTimeUomCd;
	private BigDecimal deliveryFreqQtyperhre;
	private String deliveryFreqQtyperhreUomCd;
	private Double deliveryTimeTolerance;
	private String deliveryTimeToleranceUomCd;
	private String serviceProcessIndicatorId;
	
	public String toString(){
		String str = "";
		str += "][deliverySettingType=" + this.deliverySettingType;
		str += "][sarea=" + (this.sarea!= null? this.sarea.getSareaId() : "null");
		str += "][sapMaterial=" + (this.sapMaterial != null ? this.sapMaterial.getSapMaterialId(): "null");
		str += "][shippingpoint=" + this.shippingpointId;
		str += "][loadingpoint=" + this.loadingpointId;
		str += "][billto=" + this.billtoSapBpId;
		str += "][commercialCarrier=" + this.commercialCarrierId;
		str += "][executingCarrierId=" + this.executingCarrierId;
		str += "][pool=" + this.poolId;
		str += "][silo=" + this.shiptoSiloId;
		str += "][deliveryTypeCd=" + this.deliveryTypeCd;
		str += "][uomCd=" + this.uomCd;
		str += "][productionPlantCd=" + this.productionPlantCd;
		str += "][quantity=" + this.quantity;
		str += "][supplyPlantCd=" + this.supplyPlantCd;
		str += "][serviceProcessIndicatorId=" + this.serviceProcessIndicatorId;
		str += "][shiptoSareaRestrId=" + (this.shiptoSareaRestr != null ? this.shiptoSareaRestr.getShiptoSareaRestrId(): "null");
		str += "][sapContractItemId=" + (this.sapContractItem != null ? this.sapContractItem.getSapContractItemId(): "null");
		return str;
	}
	
	public int getResolutionType() {
		return DeliverySettingDTO.RESOLUTION_NOT_RELEVANT;
	}
	
	public final SapMaterialEntity getSapMaterial() {
		// TODO (emoke Aug 3, 2012):This here is not good, but now code needs still be able to use basic settings based on other than restrictions, a concept change is needed here
		if ( getShiptoSareaRestr() != null ) {			
			return getShiptoSareaRestr() != null ? getShiptoSareaRestr().getSapMaterial() : null;
		}
		else {
			return this.sapMaterial;
		}
	}
	
	public void setSapMaterial(SapMaterialEntity sapMaterial) {
		this.sapMaterial = sapMaterial;
	}
	
	public final SareaEntity getSarea() {
		// TODO (emoke Aug 3, 2012):This here is not good, but now code needs still be able to use basic settings based on other than restrictions, a concept change is needed here
		if ( getShiptoSareaRestr() != null ) {			
			return getShiptoSareaRestr() != null ? getShiptoSareaRestr().getShiptoSarea().getSarea() : null;
		}
		else {
			return this.sarea;
		}
	}

	public void setSarea(SareaEntity sarea) {
		this.sarea = sarea;
	}
	
    public Boolean getExactCustorderQtyFlag() {
		return exactCustorderQtyFlag;
	}

	public void setExactCustorderQtyFlag(Boolean exactCustorderQtyFlag) {
		this.exactCustorderQtyFlag = exactCustorderQtyFlag;
	}

	public Boolean getExactSdorderQtyFlag() {
		return exactSdorderQtyFlag;
	}

	public void setExactSdorderQtyFlag(Boolean exactSdorderQtyFlag) {
		this.exactSdorderQtyFlag = exactSdorderQtyFlag;
	}

	public Boolean getAutoCarrierAssignmentFlag() {
		return autoCarrierAssignmentFlag;
	}

	public void setAutoCarrierAssignmentFlag(Boolean autoCarrierAssignmentFlag) {
		this.autoCarrierAssignmentFlag = autoCarrierAssignmentFlag;
	}

	public Boolean getAutoPoolAssignmentFlag() {
		return autoPoolAssignmentFlag;
	}

	public void setAutoPoolAssignmentFlag(Boolean autoPoolAssignmentFlag) {
		this.autoPoolAssignmentFlag = autoPoolAssignmentFlag;
	}
	
    public Boolean getAutoReleaseFlag() {
		return autoReleaseFlag;
	}

	public void setAutoReleaseFlag(Boolean autoReleaseFlag) {
		this.autoReleaseFlag = autoReleaseFlag;
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

	public Boolean getSapSuppressMinChargeFlag() {
		return sapSuppressMinChargeFlag;
	}

	public void setSapSuppressMinChargeFlag(Boolean sapSuppressMinChargeSapFlag) {
		this.sapSuppressMinChargeFlag = sapSuppressMinChargeSapFlag;
	}

	public Boolean getOpenCustorderFlag() {
		return openCustorderFlag;
	}

	public void setOpenCustorderFlag(Boolean openCustorderFlag) {
		this.openCustorderFlag = openCustorderFlag;
	}

	public Boolean getOnHoldOrderAndLockedFlag() {
		return onHoldOrderAndLockedFlag;
	}

	public void setOnHoldOrderAndLockedFlag(Boolean onHoldOrderAndLockedFlag) {
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
	}
    
	public Long getMixDesignId() {
		return mixDesignId;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	public void setLoadingpointId(Long loadingpointId) {
		this.loadingpointId = loadingpointId;
	}

	public void setBilltoSapBpId(Long billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}

	public void setCommercialCarrierId(Long commercialCarrierId) {
		this.commercialCarrierId = commercialCarrierId;
	}

	public void setExecutingCarrierId(Long executingCarrierId) {
		this.executingCarrierId = executingCarrierId;
	}

	public void setMixDesignId(Long mixDesignId) {
		this.mixDesignId = mixDesignId;
	}

	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}

	public void setShiptoSiloId(Long shiptoSiloId) {
		this.shiptoSiloId = shiptoSiloId;
	}

	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
	}

	public void setUomCd(String uomCd) {
		this.uomCd = uomCd;
	}

	public void setProductionPlantCd(String productionPlantCd) {
		this.productionPlantCd = productionPlantCd;
	}

	public void setSupplyPlantCd(String supplyPlantCd) {
		this.supplyPlantCd = supplyPlantCd;
	}

	public void setPayerSapBpId(Long payerSapBpId) {
		this.payerSapBpId = payerSapBpId;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public final Long getShippingpointId() {
		return shippingpointId;
	}

	public final Long getLoadingpointId() {
		return loadingpointId;
	}

	public final Long getBilltoSapBpId() {
		return billtoSapBpId;
	}

	public final Long getCommercialCarrierId() {
		return commercialCarrierId;
	}

	public final Long getExecutingCarrierId() {
		return executingCarrierId;
	}

	public final Long getPoolId() {
		return poolId;
	}

	public final Long getShiptoSiloId() {
		return shiptoSiloId;
	}

	public final Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}

	public final String getProductionPlantCd() {
		return productionPlantCd;
	}

	public final String getUomCd() {
		return uomCd;
	}

	public final String getSupplyPlantCd() {
		return supplyPlantCd;
	}

	public Long getPayerSapBpId() {
		return payerSapBpId;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}

	public int getDeliverySettingType() {
		return deliverySettingType;
	}

	public String getCompositeIdentifier() {
		return compositeIdentifier;
	}

	public ShiptoSareaRestrEntity getShiptoSareaRestr() {
		return shiptoSareaRestr;
	}

	public SapContractItemEntity getSapContractItem() {
		return getShiptoSareaRestr() != null ? getShiptoSareaRestr().getSapContractItem() : null;
	}


	public Long getSapContractItemId() {
		return getSapContractItem() != null ? getSapContractItem().getSapContractItemId() : null;
	}

	public Long getSapMaterialId() {
		return getSapMaterial().getSapMaterialId();
	}

	public Long getShiptoSareaRestrId() {
		return getShiptoSareaRestr() != null ? getShiptoSareaRestr().getShiptoSareaRestrId() : null;
	}

	public BigDecimal getPerSdorderQuantity() {
		return perSdorderQuantity;
	}

	public void setPerSdorderQuantity(BigDecimal perSdorderQuantity) {
		this.perSdorderQuantity = perSdorderQuantity;
	}

	public String getPerSdorderUomCd() {
		return perSdorderUomCd;
	}

	public void setPerSdorderUomCd(String perSdorderUomCd) {
		this.perSdorderUomCd = perSdorderUomCd;
	}

	public Double getDeliveryFreqEveryTime() {
		return deliveryFreqEveryTime;
	}

	public void setDeliveryFreqEveryTime(Double deliveryFreqEveryTime) {
		this.deliveryFreqEveryTime = deliveryFreqEveryTime;
	}

	public String getDeliveryFreqEveryTimeUomCd() {
		return deliveryFreqEveryTimeUomCd;
	}

	public void setDeliveryFreqEveryTimeUomCd(String deliveryFreqEveryTimeUomCd) {
		this.deliveryFreqEveryTimeUomCd = deliveryFreqEveryTimeUomCd;
	}

	public BigDecimal getDeliveryFreqQtyperhre() {
		return deliveryFreqQtyperhre;
	}

	public void setDeliveryFreqQtyperhre(BigDecimal deliveryFreqQtyperhre) {
		this.deliveryFreqQtyperhre = deliveryFreqQtyperhre;
	}

	public String getDeliveryFreqQtyperhreUomCd() {
		return deliveryFreqQtyperhreUomCd;
	}

	public void setDeliveryFreqQtyperhreUomCd(String deliveryFreqQtyperhreUomCd) {
		this.deliveryFreqQtyperhreUomCd = deliveryFreqQtyperhreUomCd;
	}

	public Double getDeliveryTimeTolerance() {
		return deliveryTimeTolerance;
	}

	public void setDeliveryTimeTolerance(Double deliveryTimeTolerance) {
		this.deliveryTimeTolerance = deliveryTimeTolerance;
	}

	public String getDeliveryTimeToleranceUomCd() {
		return deliveryTimeToleranceUomCd;
	}

	public void setDeliveryTimeToleranceUomCd(String deliveryTimeToleranceUomCd) {
		this.deliveryTimeToleranceUomCd = deliveryTimeToleranceUomCd;
	}

	public String getServiceProcessIndicatorId() {
		return serviceProcessIndicatorId;
	}

	public void setServiceProcessIndicatorId(String serviceProcessIndicatorId) {
		this.serviceProcessIndicatorId = serviceProcessIndicatorId;
	}	
}

/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.OrderCreateEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * DTO to send data for several UI components in one pack during Order Taking
 */
public class OrderTakingEditorDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String supplyPlantCd;
	private Long delTypeCd;
	private Long spId;
	private Long poolId;
	private Long commercialCarrierId;
	private Long executingCarrierId;
	private Double durationCorrectionFactor;
	private Double distanceCorrectionFactor;
	private OrderTakingEditorRailDeliveryDto railData;
	private String quantityPerSdoStr;
	private String quantityPerSdoUomCd;
	private String quantityPerSdoText;
	private Boolean exactCustorderQtyFlag;
	private Boolean exactQtyPerSdoFlag;
	private Boolean autoReleaseFlag = Boolean.FALSE;
	private String deliveryFqyEveryTimeStr;
	private String deliveryFqyEveryTimeUomCd;
	private String deliveryFqyQtyPerHreStr;
	private String deliveryFqyQtyPerHreUomCd;
	private String deliveryTimeToleranceStr;
	private String deliveryTimeToleranceUomCd;
	private String numberOfDeliveriesText;
	private Boolean serviceOnlyOrderFlag;
	// We need to duplicate these variables from the BestellungForm so in the Adjust deliveries pop-up
	// we can keep the user's selection while the window is open 
    private String open1From; 
    private String open1To; 
    private String open2From; 
    private String open2To;
    private String deliveryFromDatStr; 
    private String deliveryToDatStr;
    private String deliveryFromTime; 
    private String deliveryToTime;
    private String contractDescription;
    
    // --- Duration in minutes, and distance in meters of the route from the shipping point to the ship-to --- 
    private String durationTime;
    private String distanceMeters;
    private Double durationCorrFactor;
    

	private MaterialType materialType;
	
	private Long mixDesignId;
	
	// TODO (emoke Aug 27, 2012):Probably this should not be here but on item?
	public void setMixDesignId(Long mixDesignId) {
		this.mixDesignId = mixDesignId;
	}
    public Long getMixDesignId() {
		return mixDesignId;
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
	

	private String performanceOrderCalculationMode;

	
	private String truckTypes;
	public String getTruckTypes() {
		return truckTypes;
	}
	public void setTruckTypes(String truckTypes) {
		this.truckTypes = truckTypes;
	}
	
	private String selectedTruckTypes;
	
	private boolean payerIgnored;
	
	public String getSelectedTruckTypes() {
		return selectedTruckTypes;
	}
	public void setSelectedTruckTypes(String selectedTruckTypes) {
		this.selectedTruckTypes = selectedTruckTypes;
	}

	private DeliverySettingSalesAreaMap salesAreaMap;
	
	private List<ShippingpointDTO> spList;
	private List<LoadingPointDTO> lpList;
	private List<BimodalRailOrderWrapper> biRailItemList;

	private List<TruckWagonTypeWrapper> vehicleTypeWrapperList;
	private Map<String, String> timeUomMap;
	private Map<String, String> timeToleranceUomMap;

	private List<FireqMaterialMixdesignEntity> mixDesignList;

	private List<PoolTxEntity> poolList;
	private List<CommercialCarrierDTO> commercialCarrierList;
	private List<ExecutingCarrierDTO> executingCarrierList;
	private List<PlantDTO> prodPlantList;
	private List<UomTxEntity> uomList;
	private List<SupplyPlantDTO> supplyPlantList;
	
	private List<ShiptoBilltoEntity> billtoList;
	private Map<Long, ShiptoSareaRestrEntity> restrictionMap;
	private List<SapBpEntity> payerList;
	private String custorderCustrefNo; // Customer Reference Number
	
    private List<ShippingpointEntity> fullShippintpointList;
    private Set<Long> possibleShippingPointIdSet;
	private Set<String> possibleShippingPointNoSet;
    private Set<String> possibleSupplyPlantCdSet;

	private Set<Long> spIdSet;
	private Set<Long> poolIdSet;
	private List<OrderSpiTxEntity> serviceProcessIndicatorList;
	private String serviceProcessIndicatorId;
	
	private ShiptoSareaEntity shiptoSarea;
	private OrderEntity order;
	private OrderCreateEntity orderHelper;
	private Set<MaterialColumn> materialColumns;
	
	//Single Order Delivery related data
	private String custOrderQuantity;
	private String countOfDeliveries;
	private String quantityPerDelivery;
	private List<SplittedOrderData> ordersListFromCustOrders;
	
	private CustorderSplitWrapper wrapper;
	
	// --- Not saved values, only used as helper values on the GUI ---
	private Boolean autoPoolAssignmentFlag;
	private Boolean autoCarrierAssignmentFlag;
	private Boolean printSingleDeliveryNoteFlag;
	private Boolean printCollDeliveryNoteFlag;
	
	// --- Multiple Order Items (mainly used by bag) ---
	private List<OrderTakingEditorOrderItemDto> orderItems;

	private List<String> validationMessages;
   		
	// --- Constructor ---
	public OrderTakingEditorDto(TimeZone userTimeZone) {
		OrderTakingEditorOrderItemDto firstItem = new OrderTakingEditorOrderItemDto();
		
		this.orderItems = new ArrayList<OrderTakingEditorOrderItemDto>();
		this.orderItems.add(firstItem);
		this.railData = new OrderTakingEditorRailDeliveryDto(userTimeZone);
	}
	
	/**
	 * Sets print delivery note defaults from DB state depending on the selected material.
	 *  
	 * @param sapMaterial - the selected material
	 */
	public void initDeliveryNoteFlags(SapMaterialEntity sapMaterial) {
		if (sapMaterial == null || shiptoSarea == null) {
			return;
		}
		ShiptoEntity shipto = shiptoSarea.getShipto();
		if (sapMaterial.getMaterialIsCem()) {
			printSingleDeliveryNoteFlag = shipto.getPaperSingleDelnoteFlagCem();
			printCollDeliveryNoteFlag = shipto.getPaperCollDelnoteFlagCem();
		}
		else if (sapMaterial.getMaterialIsAgg() || sapMaterial.getMaterialIsLf()) {
			printSingleDeliveryNoteFlag = shipto.getPaperSingleDelnoteFlagAgg();
			printCollDeliveryNoteFlag = shipto.getPaperCollDelnoteFlagAgg();
		}
		else {
			printSingleDeliveryNoteFlag = false;
			printCollDeliveryNoteFlag = false;
		}
	}
	
	public OrderEntity getOrder() {
		return order;
	}
	public void setOrder(OrderEntity order) {
		this.order = order;
	}
	public OrderCreateEntity getOrderHelper() {
		return orderHelper;
	}
	public void setOrderHelper(OrderCreateEntity orderHelper) {
		this.orderHelper = orderHelper;
	}
	
	public List<ShippingpointDTO> getSpList() {
		return spList;
	}
	public void setSpList(List<ShippingpointDTO> shippingPoints) throws Exception {
		this.spList = shippingPoints;
		Set<Long> shippingPointIds = new HashSet<Long>();
		for (ShippingpointDTO shippingPoint : shippingPoints) {
			Long shippingPointId = shippingPoint.getShippingpointId();
			shippingPointIds.add(shippingPointId);
		}
		this.spIdSet = shippingPointIds;
	}
	public List<LoadingPointDTO> getLpList() {
		return lpList;
	}
	public void setLpList(List<LoadingPointDTO> lpList) {
		this.lpList = lpList;
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

	public Long getExecutingCarrierId() {
		return executingCarrierId;
	}
	public void setExecutingCarrierId(Long executingCarrierId) {
		this.executingCarrierId = executingCarrierId;
	}

	public String getSupplyPlantCd() {
		return supplyPlantCd;
	}
	public void setSupplyPlantCd(String supplyPlantCd) {
		this.supplyPlantCd = supplyPlantCd;
	}
	
	public Set<Long> getSpIdSet() {
		return spIdSet;
	}
	
	public Set<Long> getPoolIdSet() {
		return poolIdSet;
	}
	
	public Boolean getAutoPoolAssignmentFlag() {
		return autoPoolAssignmentFlag;
	}
	public void setAutoPoolAssignmentFlag(Boolean autoPoolAssignment) {
		this.autoPoolAssignmentFlag = autoPoolAssignment;
		if (getAutoCarrierAssignmentFlag() != null && getAutoCarrierAssignmentFlag().equals(Boolean.TRUE)){
			this.autoPoolAssignmentFlag = Boolean.TRUE;
		}	
	}
	public Boolean getAutoCarrierAssignmentFlag() {
		return autoCarrierAssignmentFlag;
	}
	public void setAutoCarrierAssignmentFlag(Boolean autoCarrierAssignment) {
		this.autoCarrierAssignmentFlag = autoCarrierAssignment;
		if (getAutoCarrierAssignmentFlag() != null && getAutoCarrierAssignmentFlag().equals(Boolean.TRUE)){
			setAutoPoolAssignmentFlag(Boolean.TRUE);
		}
	}
	public Long getDelTypeCd() {
		return delTypeCd;
	}
	public void setDelTypeCd(Long delTypeCd) {
		this.delTypeCd = delTypeCd;
	}
	public Double getDurationCorrectionFactor() {
		return durationCorrectionFactor;
	}
	public void setDurationCorrectionFactor(Double durationCorrectionFactor) {
		this.durationCorrectionFactor = durationCorrectionFactor;
	}
	public Double getDistanceCorrectionFactor() {
		return distanceCorrectionFactor;
	}
	public void setDistanceCorrectionFactor(Double distanceCorrectionFactor) {
		this.distanceCorrectionFactor = distanceCorrectionFactor;
	}
	public List<BimodalRailOrderWrapper> getBiRailItemList() {
		return biRailItemList;
	}
	public void setBiRailItemList(List<BimodalRailOrderWrapper> biRailItemList) {
		this.biRailItemList = biRailItemList;
	}
	public Map<Long, ShiptoSareaRestrEntity> getRestrictionMap() {
		return restrictionMap;
	}
	public void setRestrictionMap(Map<Long, ShiptoSareaRestrEntity> restrictionMap) {
		this.restrictionMap = restrictionMap;
	}
	public List<ShiptoBilltoEntity> getBilltoList() {
		return billtoList;
	}
	public void setBilltoList(List<ShiptoBilltoEntity> billtoList) {
		this.billtoList = billtoList;
	}
	
	public List<SapBpEntity> getPayerList() {
		return this.payerList;
	}

	public void setPayerList(List<SapBpEntity> payerList) {
		this.payerList = payerList;
	}

	public boolean isDummyShipto() {
		SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		String shiptoSapId = shiptoSapBp.getSapId();
		return shiptoSapId.startsWith("DMY");
	}
	
	public Set<String> getPossibleShippingPointNoSet() {
		return possibleShippingPointNoSet;
	}

	public void setPossibleShippingPointNoSet(Set<String> validShippingPointNoSet) {
		this.possibleShippingPointNoSet = validShippingPointNoSet;
	}

	public List<ShippingpointEntity> getFullShippintpointList() {
		return fullShippintpointList;
	}

	public void setFullShippintpointList(List<ShippingpointEntity> fullShippintpointList) {
		this.fullShippintpointList = fullShippintpointList;
	}
	
	public Set<String> getPossibleSupplyPlantCdSet() {
		return possibleSupplyPlantCdSet;
	}

	public void setPossibleSupplyPlantCdSet(Set<String> validSupplyPlantCdSet) {
		this.possibleSupplyPlantCdSet = validSupplyPlantCdSet;
	}
	
    public Set<Long> getPossibleShippingPointIdSet() {
		return possibleShippingPointIdSet;
	}

	public void setPossibleShippingPointIdSet(Set<Long> possibleShippingPointIdSet) {
		this.possibleShippingPointIdSet = possibleShippingPointIdSet;
	}
	
	public List<String> getValidationMessages() {
		return validationMessages;
	}
	public void setValidationMessages(List<String> validationMessages) {
		this.validationMessages = validationMessages;
	}
	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}
	public void setShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}
	public OrderTakingEditorRailDeliveryDto getRailData() {
		return railData;
	}
	public void setRailData(OrderTakingEditorRailDeliveryDto railData) {
		this.railData = railData;
	}
	public String getQuantityPerSdoStr() {
		return quantityPerSdoStr;
	}
	public void setQuantityPerSdoStr(String quantityPerSdoStr) {
		this.quantityPerSdoStr = quantityPerSdoStr;
	}
	public String getQuantityPerSdoUomCd() {
		return quantityPerSdoUomCd;
	}
	public void setQuantityPerSdoUomCd(String quantityPerSdoUomCd) {
		this.quantityPerSdoUomCd = quantityPerSdoUomCd;
	}
	public String getQuantityPerSdoText() {
		return quantityPerSdoText;
	}
	public void setQuantityPerSdoText(String quantityPerSdoText) {
		this.quantityPerSdoText = quantityPerSdoText;
	}
	public Boolean getExactCustorderQtyFlag() {
		return exactCustorderQtyFlag;
	}
	public void setExactCustorderQtyFlag(Boolean exactCustorderQtyFlag) {
		this.exactCustorderQtyFlag = exactCustorderQtyFlag;
	}
	public Boolean getExactQtyPerSdoFlag() {
		return exactQtyPerSdoFlag;
	}
	public void setExactQtyPerSdoFlag(Boolean exactQtyPerSdoFlag) {
		this.exactQtyPerSdoFlag = exactQtyPerSdoFlag;
	}
	public String getDeliveryFqyEveryTimeStr() {
		return deliveryFqyEveryTimeStr;
	}
	public void setDeliveryFqyEveryTimeStr(String deliveryFqyEveryTimeStr) {
		this.deliveryFqyEveryTimeStr = deliveryFqyEveryTimeStr;
	}
	public String getDeliveryFqyEveryTimeUomCd() {
		return deliveryFqyEveryTimeUomCd;
	}
	public void setDeliveryFqyEveryTimeUomCd(String deliveryFqyEveryTimeUomCd) {
		this.deliveryFqyEveryTimeUomCd = deliveryFqyEveryTimeUomCd;
	}
	public String getDeliveryFqyQtyPerHreStr() {
		return deliveryFqyQtyPerHreStr;
	}
	public void setDeliveryFqyQtyPerHreStr(String deliveryFqyQtyPerHreStr) {
		this.deliveryFqyQtyPerHreStr = deliveryFqyQtyPerHreStr;
	}
	public String getDeliveryFqyQtyPerHreUomCd() {
		return deliveryFqyQtyPerHreUomCd;
	}
	public void setDeliveryFqyQtyPerHreUomCd(String deliveryFqyQtyPerHreUomCd) {
		this.deliveryFqyQtyPerHreUomCd = deliveryFqyQtyPerHreUomCd;
	}
	public String getDeliveryTimeToleranceStr() {
		return deliveryTimeToleranceStr;
	}
	public void setDeliveryTimeToleranceStr(String deliveryTimeToleranceStr) {
		this.deliveryTimeToleranceStr = deliveryTimeToleranceStr;
	}
	public String getDeliveryTimeToleranceUomCd() {
		return deliveryTimeToleranceUomCd;
	}
	public void setDeliveryTimeToleranceUomCd(String deliveryTimeToleranceUomCd) {
		this.deliveryTimeToleranceUomCd = deliveryTimeToleranceUomCd;
	}
	public String getNumberOfDeliveriesText() {
		return numberOfDeliveriesText;
	}
	public void setNumberOfDeliveriesText(String numberOfDeliveriesText) {
		this.numberOfDeliveriesText = numberOfDeliveriesText;
	}
	public Boolean getServiceOnlyOrderFlag() {
		return serviceOnlyOrderFlag;
	}
	public void setServiceOnlyOrderFlag(Boolean serviceOnlyOrderFlag) {
		this.serviceOnlyOrderFlag = serviceOnlyOrderFlag;
	}
	public String getPerformanceOrderCalculationMode() {
		return performanceOrderCalculationMode;
	}
	public void setPerformanceOrderCalculationMode(String performanceOrderCalculationMode) {
		this.performanceOrderCalculationMode = performanceOrderCalculationMode;
	}
	public List<OrderTakingEditorOrderItemDto> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderTakingEditorOrderItemDto> orderItems) {
		this.orderItems = orderItems;
	}

	public Boolean getPrintSingleDeliveryNoteFlag() {
		return printSingleDeliveryNoteFlag;
	}
	
	public void setPrintSingleDeliveryNoteFlag(Boolean printSingleDeliveryNoteFlag) {
		this.printSingleDeliveryNoteFlag = printSingleDeliveryNoteFlag;
	}
	
	public Boolean getPrintCollDeliveryNoteFlag() {
		return printCollDeliveryNoteFlag;
	}
	
	public void setPrintCollDeliveryNoteFlag(Boolean printCollDeliveryNoteFlag) {
		this.printCollDeliveryNoteFlag = printCollDeliveryNoteFlag;
	}	

	public String getCustorderCustrefNo() {
		return custorderCustrefNo;
	}

	public void setCustorderCustrefNo(String custorderCustrefNo) {
		this.custorderCustrefNo = custorderCustrefNo;
	}

	public void setCustOrderQuantity(String custOrderQuantity) {
		this.custOrderQuantity = custOrderQuantity;
	}

	public String getCustOrderQuantity() {
		return custOrderQuantity;
	}

	public void setCountOfDeliveries(String countOfDeliveries) {
		this.countOfDeliveries = countOfDeliveries;
	}

	public String getCountOfDeliveries() {
		return countOfDeliveries;
	}

	public void setQuantityPerDelivery(String quantityPerDelivery) {
		this.quantityPerDelivery = quantityPerDelivery;
	}

	public String getQuantityPerDelivery() {
		return quantityPerDelivery;
	}

	public void setOrdersListFromCustOrders(List<SplittedOrderData> ordersListFromCustOrders) {
		this.ordersListFromCustOrders = ordersListFromCustOrders;
	}

	public List<SplittedOrderData> getOrdersListFromCustOrders() {
		return ordersListFromCustOrders;
	}
	
	public List<TruckWagonTypeWrapper> getVehicleTypeWrapperList() {
		return vehicleTypeWrapperList;
	}

	public void setVehicleTypeWrapperList(List<TruckWagonTypeWrapper> vehicleTypeWrapperList) {
		this.vehicleTypeWrapperList = vehicleTypeWrapperList;
	}
	
	public Map<String, String> getTimeUomMap() {
		return timeUomMap;
	}

	public void setTimeUomMap(Map<String, String> timeUomMap) {
		this.timeUomMap = timeUomMap;
	}
	
	public Map<String, String> getTimeToleranceUomMap() {
		return timeToleranceUomMap;
	}
	
	public void setTimeToleranceUomMap(Map<String, String> timeToleranceUomMap) {
		this.timeToleranceUomMap = timeToleranceUomMap;
	}
	
	public List<FireqMaterialMixdesignEntity> getMixDesignList() {
		return mixDesignList;
	}
	
	public void setMixDesignList(List<FireqMaterialMixdesignEntity> mixDesignList) {
		this.mixDesignList = mixDesignList;
	}
	
	public List<PoolTxEntity> getPoolList() {
		return poolList;
	}

	public void setPoolList(List<PoolTxEntity> poolList) {
		this.poolList = poolList;
	}

	public List<CommercialCarrierDTO> getCommercialCarrierList() {
		return commercialCarrierList;
	}

	public void setCommercialCarrierList(List<CommercialCarrierDTO> commercialCarrierList) {
		this.commercialCarrierList = commercialCarrierList;
	}

	public List<ExecutingCarrierDTO> getExecutingCarrierList() {
		return executingCarrierList;
	}

	public void setExecutingCarrierList(List<ExecutingCarrierDTO> executingCarrierList) {
		this.executingCarrierList = executingCarrierList;
	}

	public List<PlantDTO> getProdPlantList() {
		return prodPlantList;
	}

	public void setProdPlantList(List<PlantDTO> prodPlantList) {
		this.prodPlantList = prodPlantList;
	}

	public List<SupplyPlantDTO> getSupplyPlantList() {
		return supplyPlantList;
	}

	public void setSupplyPlantList(List<SupplyPlantDTO> supplyPlantList) {
		this.supplyPlantList = supplyPlantList;
	}

	public DeliverySettingSalesAreaMap getSalesAreaMap() {
		return salesAreaMap;
	}

	public void setSalesAreaMap(DeliverySettingSalesAreaMap salesAreaMap) {
		this.salesAreaMap = salesAreaMap;
	}

	public List<UomTxEntity> getUomList() {
		return uomList;
	}

	public void setUomList(List<UomTxEntity> uomList) {
		this.uomList = uomList;
	}

	public Set<MaterialColumn> getMaterialColumns() {
		return materialColumns;
	}

	public void setMaterialColumns(Set<MaterialColumn> materialColumns) {
		this.materialColumns = materialColumns;
	}

	public Boolean getAutoReleaseFlag() {
		return autoReleaseFlag;
	}

	public void setAutoReleaseFlag(Boolean autoReleaseFlag) {
		this.autoReleaseFlag = autoReleaseFlag;
	}

	public CustorderSplitWrapper getWrapper() {
		return wrapper;
	}

	public void setWrapper(CustorderSplitWrapper wrapper) {
		this.wrapper = wrapper;
	}
	
	public void setMaterialType(MaterialType materialType) {
		this.materialType = materialType;
	}
	
	public MaterialType getMaterialType() {
		return materialType;
	}

	public String getOpen1From() {
		return open1From;
	}

	public void setOpen1From(String open1From) {
		this.open1From = open1From;
	}

	public String getOpen1To() {
		return open1To;
	}

	public void setOpen1To(String open1To) {
		this.open1To = open1To;
	}

	public String getOpen2From() {
		return open2From;
	}

	public void setOpen2From(String open2From) {
		this.open2From = open2From;
	}

	public String getOpen2To() {
		return open2To;
	}

	public void setOpen2To(String open2To) {
		this.open2To = open2To;
	}
	public List<OrderSpiTxEntity> getServiceProcessIndicatorList() {
		return serviceProcessIndicatorList;
	}
	
	public void setServiceProcessIndicatorList(List<OrderSpiTxEntity> serviceProcessIndicatorList) {
		this.serviceProcessIndicatorList = serviceProcessIndicatorList;
	}
	
	public String getServiceProcessIndicatorId() {
		return serviceProcessIndicatorId;
	}
	
	public void setServiceProcessIndicatorId(String serviceProcessIndicatorId) {
		this.serviceProcessIndicatorId = serviceProcessIndicatorId;
	}	

	public String getDeliveryFromTime() {
		return deliveryFromTime;
	}

	public void setDeliveryFromTime(String deliveryFromTime) {
		this.deliveryFromTime = deliveryFromTime;
	}

	public String getDeliveryToTime() {
		return deliveryToTime;
	}

	public void setDeliveryToTime(String deliveryToTime) {
		this.deliveryToTime = deliveryToTime;
	}

	public String getDeliveryFromDatStr() {
		return deliveryFromDatStr;
	}

	public void setDeliveryFromDatStr(String deliveryFromDatStr) {
		this.deliveryFromDatStr = deliveryFromDatStr;
	}

	public String getDeliveryToDatStr() {
		return deliveryToDatStr;
	}

	public void setDeliveryToDatStr(String deliveryToDatStr) {
		this.deliveryToDatStr = deliveryToDatStr;
	}
	
	public String getContractDescription(){
		return contractDescription;
	}
	
	public void setContractDescription(String contractDescription){
		this.contractDescription = contractDescription;
	}
	
	public boolean getPayerIgnored() {
		return payerIgnored;
	}
	public void setPayerIgnored(boolean payerIgnored) {
		this.payerIgnored = payerIgnored;
	}
	public String getDurationTime() {
		return durationTime;
	}
	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}
	public String getDistanceMeters() {
		return distanceMeters;
	}
	public void setDistanceMeters(String distanceMeters) {
		this.distanceMeters = distanceMeters;
	}
	public Double getDurationCorrFactor() {
		return durationCorrFactor;
	}
	public void setDurationCorrFactor(Double durationCorrFactor) {
		this.durationCorrFactor = durationCorrFactor;
	}	
}
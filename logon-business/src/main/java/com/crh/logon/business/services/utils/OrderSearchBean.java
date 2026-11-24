package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.Date;

import com.crh.logon.persistence.entities.views.OrderMonitoringEntity;
import com.crh.logon.persistence.entities.views.OrderSearchEntity;

public class OrderSearchBean implements Serializable, Comparable<OrderSearchBean> {
	private static final long serialVersionUID = 1L;
	
	private String compositeId;
	private Long orderId;
	private Long orderCreationTypeCd;
	private Long shiptoSapBpId;
	private Long soldtoSapBpId;
	private Long billtoSapBpId;
	private Long sareaId;
	private Long sapMaterialId;
	private Long orderNumber;
	private Long orderItemNo;
	private String orderedUomCd;
	private String orderedUomTxt;
	private java.math.BigDecimal orderedQuantity;
	private String loadedUomCd;
	private String loadedUomTxt;
	private java.math.BigDecimal loadedQuantity;
	private String deliveredUomCd;
	private String deliveredUomTxt;
	private java.math.BigDecimal deliveredQuantity;
	private String returnedUomCd;
	private String returnedUomTxt;
	private java.math.BigDecimal returnedQuantity;
	private Date deliveryFromDat;
	private Date deliveryToDat;
	private String distributionDestCd;
	private Long orderStatusCd;
	private Long deliveryTypeCd;
	private Long shippingpointId;
	private Long transportDurationMin;
	private Long plannedLoadingTimeMin;
	private Long loadingpointId; 
	private Long poolId;
	private Long commCarrierId;
	private Date plannedDeliveryDat;
	private String orderValidationCode;
	private String soldtoName1;
	private String shiptoName1;
	private String shiptoCity;
	private String billtoName1;
	private Long shiptoSareaId;
	private String shiptoSgroupCd;
	private String sgroupCd;
	private String shiptoSofficeCd;
	private String langCd;
	private String statusTxt;
	private Long warnStatusCode;
	private Long timeToRedLine;
	private Boolean onHoldOrderAndLockedFlag;
	private String poolName;
	private String commCarrierName;
	private Long sapVendorId;
	private String plantCd;
	private String shippingpointName;
	private String loadingpointName;
	private Long loadingpointLoadingTime;
	private String materialNo;
	private String materialTxt;
	private String deliveryTypeTxt;
	private String plantName;
	private String sorgCd;
	private String companycodeCd;
	private String driverFullName;
	private Long truckId;
	private Long wagonId;
	private String vehicleName;
	private String materialPackageCd;
	private Double truckMaxNetWeightT;
	private Double wagonMaxNetWeightT;
	private Double statsQuantity;
	private String statsUomCd;
	private String shiptoTimezoneCd;
	private Long commSapVendorId;
	private String execCarrierName;
	private Long execSapVendorId;
	private Long simpleVehicleId;
	private Double simVehMaxNetWeightT;
	private Long execCarrierId;
	private String deliveryFrom;
	private String deliveryTo;
	private String plannedDelivery;
	private String ldsDeliveryNoteNo;
	private String noteOrderMgmt;
	private String noteDelivery;
	private String noteDispatching;
	private String noteTruckDriver;
    private String noteRailwayDisp;
    private String orderNumberFromSeqUsage;
    private String shiptoAddress;
    private Date loadingTs;
    private String loadingStr;
	private Long itrackStatusCd;
	
	public OrderSearchBean(OrderMonitoringEntity orderMonitoringEntity) {
		this.compositeId = orderMonitoringEntity.getCompositeId();
		this.orderId = orderMonitoringEntity.getOrderId();
		this.orderCreationTypeCd = orderMonitoringEntity.getOrderCreationTypeCd();
		this.shiptoSapBpId = orderMonitoringEntity.getShiptoSapBpId();
		this.soldtoSapBpId = orderMonitoringEntity.getSoldtoSapBpId();
		this.billtoSapBpId = orderMonitoringEntity.getBilltoSapBpId();
		this.sareaId = orderMonitoringEntity.getSareaId();
		this.sapMaterialId = orderMonitoringEntity.getSapMaterialId();
		this.orderNumber = orderMonitoringEntity.getOrderNumber();
		this.orderItemNo = orderMonitoringEntity.getOrderItemNo();
		this.orderedUomTxt = orderMonitoringEntity.getOrderedUomTxt();
		this.orderedQuantity = orderMonitoringEntity.getOrderedQuantity();
		this.loadedUomTxt = orderMonitoringEntity.getLoadedUomTxt();
		this.loadedQuantity = orderMonitoringEntity.getLoadedQuantity();
		this.deliveredUomTxt = orderMonitoringEntity.getDeliveredUomTxt();
		this.deliveredQuantity = orderMonitoringEntity.getDeliveredQuantity();
		this.returnedUomTxt = orderMonitoringEntity.getReturnedUomTxt();
		this.returnedQuantity = orderMonitoringEntity.getReturnedQuantity();
		this.deliveryFromDat = orderMonitoringEntity.getDeliveryFromDat();
		this.deliveryToDat = orderMonitoringEntity.getDeliveryToDat();
		this.distributionDestCd = orderMonitoringEntity.getDistributionDestCd();
		this.orderStatusCd = orderMonitoringEntity.getOrderStatusCd();
		this.deliveryTypeCd = orderMonitoringEntity.getDeliveryTypeCd();
		this.shippingpointId = orderMonitoringEntity.getShippingpointId();
		this.poolId = orderMonitoringEntity.getPoolId();
		this.commCarrierId = orderMonitoringEntity.getCommCarrierId();
		this.plannedDeliveryDat = orderMonitoringEntity.getPlannedDeliveryDat();
		this.orderValidationCode = orderMonitoringEntity.getOrderValidationCode();
		this.soldtoName1 = orderMonitoringEntity.getSoldtoName1();
		this.shiptoName1 = orderMonitoringEntity.getShiptoName1();
		this.shiptoCity = orderMonitoringEntity.getShiptoCity();
		this.billtoName1 = orderMonitoringEntity.getBilltoName1();
		this.shiptoSgroupCd = orderMonitoringEntity.getShiptoSgroupCd();
		this.shiptoSofficeCd = orderMonitoringEntity.getShiptoSofficeCd();
		this.langCd = orderMonitoringEntity.getLangCd();
		this.statusTxt = orderMonitoringEntity.getStatusTxt();
		this.warnStatusCode = orderMonitoringEntity.getWarnStatusCode();
		this.timeToRedLine = orderMonitoringEntity.getTimeToRedLine();
		this.poolName = orderMonitoringEntity.getPoolName();
		this.commCarrierName = orderMonitoringEntity.getCommCarrierName();
		this.execCarrierName = orderMonitoringEntity.getExecCarrierName();
		this.sapVendorId = orderMonitoringEntity.getSapVendorId();
		this.plantCd = orderMonitoringEntity.getPlantCd();
		this.shippingpointName = orderMonitoringEntity.getShippingpointName();
		this.materialTxt = orderMonitoringEntity.getMaterialTxt();
		this.deliveryTypeTxt = orderMonitoringEntity.getDeliveryTypeTxt();
		this.companycodeCd = orderMonitoringEntity.getCompanycodeCd();
		this.driverFullName = orderMonitoringEntity.getDriverFullName();
		this.vehicleName = orderMonitoringEntity.getVehicleName();
		this.materialPackageCd = orderMonitoringEntity.getMaterialPackageCd();
		this.shiptoTimezoneCd = orderMonitoringEntity.getShiptoTimezoneCd();
		this.ldsDeliveryNoteNo = orderMonitoringEntity.getLdsDeliveryNoteNo();
		this.noteOrderMgmt = orderMonitoringEntity.getNoteOrderMgmt();
		this.noteDelivery = orderMonitoringEntity.getNoteDelivery();
		this.noteDispatching = orderMonitoringEntity.getNoteDispatching();
		this.noteTruckDriver = orderMonitoringEntity.getNoteTruckDriver();
		this.noteRailwayDisp = orderMonitoringEntity.getNoteRailwayDisp();
		this.orderNumberFromSeqUsage = orderMonitoringEntity.getOrderNumberFromSeqUsage();
		this.shiptoAddress = orderMonitoringEntity.getShiptoAddress();
		this.loadingTs = orderMonitoringEntity.getLoadingTs();
	}
	
	public OrderSearchBean(OrderSearchEntity orderSearchEntity) {
			this.compositeId = orderSearchEntity.getCompositeId();
			this.orderId = orderSearchEntity.getOrderId();
			this.orderCreationTypeCd = orderSearchEntity.getOrderCreationTypeCd();
			this.shiptoSapBpId = orderSearchEntity.getShiptoSapBpId();
			this.soldtoSapBpId = orderSearchEntity.getSoldtoSapBpId();
			this.billtoSapBpId = orderSearchEntity.getBilltoSapBpId();
			this.sareaId = orderSearchEntity.getSareaId();
			this.sapMaterialId = orderSearchEntity.getSapMaterialId();
			this.orderNumber = orderSearchEntity.getOrderNumber();
			this.orderItemNo = orderSearchEntity.getOrderItemNo();
			this.orderedUomCd = orderSearchEntity.getOrderedUomCd();
			this.orderedUomTxt = orderSearchEntity.getOrderedUomTxt();
			this.orderedQuantity = orderSearchEntity.getOrderedQuantity();
			this.loadedUomCd = orderSearchEntity.getLoadedUomCd();
			this.loadedUomTxt = orderSearchEntity.getLoadedUomTxt();
			this.loadedQuantity = orderSearchEntity.getLoadedQuantity();
			this.deliveredUomCd = orderSearchEntity.getDeliveredUomCd();
			this.deliveredUomTxt = orderSearchEntity.getDeliveredUomTxt();
			this.deliveredQuantity = orderSearchEntity.getDeliveredQuantity();
			this.returnedUomCd = orderSearchEntity.getReturnedUomCd();
			this.returnedUomTxt = orderSearchEntity.getReturnedUomTxt();
			this.returnedQuantity = orderSearchEntity.getReturnedQuantity();
			this.deliveryFromDat = orderSearchEntity.getDeliveryFromDat();
			this.deliveryToDat = orderSearchEntity.getDeliveryToDat();
			this.distributionDestCd = orderSearchEntity.getDistributionDestCd();
			this.orderStatusCd = orderSearchEntity.getOrderStatusCd();
			this.deliveryTypeCd = orderSearchEntity.getDeliveryTypeCd();
			this.shippingpointId = orderSearchEntity.getShippingpointId();
			this.transportDurationMin = orderSearchEntity.getTransportDurationMin();
			this.poolId = orderSearchEntity.getPoolId();
			this.commCarrierId = orderSearchEntity.getCommCarrierId();
			this.execCarrierId = orderSearchEntity.getExecCarrierId();
			this.plannedDeliveryDat = orderSearchEntity.getPlannedDeliveryDat();
			this.orderValidationCode = orderSearchEntity.getOrderValidationCode();
			this.soldtoName1 = orderSearchEntity.getSoldtoName1();
			this.shiptoName1 = orderSearchEntity.getShiptoName1();
			this.shiptoCity = orderSearchEntity.getShiptoCity();
			this.billtoName1 = orderSearchEntity.getBilltoName1();
			this.shiptoSareaId = orderSearchEntity.getShiptoSareaId();
			this.shiptoSgroupCd = orderSearchEntity.getShiptoSgroupCd();
			this.sgroupCd = orderSearchEntity.getSgroupCd();
			this.shiptoSofficeCd = orderSearchEntity.getShiptoSofficeCd();
			this.langCd = orderSearchEntity.getLangCd();
			this.statusTxt = orderSearchEntity.getStatusTxt();
			this.warnStatusCode = orderSearchEntity.getWarnStatusCode();
			this.poolName = orderSearchEntity.getPoolName();
			this.commCarrierName = orderSearchEntity.getCommCarrierName();
			this.commSapVendorId = orderSearchEntity.getCommSapVendorId();
			this.execCarrierName = orderSearchEntity.getExecCarrierName();
			this.execSapVendorId = orderSearchEntity.getExecSapVendorId();
			this.plantCd = orderSearchEntity.getPlantCd();
			this.shippingpointName = orderSearchEntity.getShippingpointName();
			this.materialNo = orderSearchEntity.getMaterialNo();
			this.materialTxt = orderSearchEntity.getMaterialTxt();
			this.deliveryTypeTxt = orderSearchEntity.getDeliveryTypeTxt();
			this.plantName = orderSearchEntity.getPlantName();
			this.sorgCd = orderSearchEntity.getSorgCd();
			this.companycodeCd = orderSearchEntity.getCompanycodeCd();
			this.driverFullName = orderSearchEntity.getDriverFullName();
			this.truckId = orderSearchEntity.getTruckId();
			this.wagonId = orderSearchEntity.getWagonId();
			this.simpleVehicleId = orderSearchEntity.getSimpleVehicleId();
			this.vehicleName = orderSearchEntity.getVehicleName();
			this.truckMaxNetWeightT = orderSearchEntity.getTruckMaxNetWeightT();
			this.wagonMaxNetWeightT = orderSearchEntity.getWagonMaxNetWeightT();
			this.simVehMaxNetWeightT = orderSearchEntity.getSimVehMaxNetWeightT();
			this.statsQuantity = orderSearchEntity.getStatsQuantity();
			this.statsUomCd = orderSearchEntity.getStatsUomCd();
			this.shiptoTimezoneCd = orderSearchEntity.getShiptoTimezoneCd();
			this.ldsDeliveryNoteNo = orderSearchEntity.getLdsDeliveryNoteNo();
			this.noteOrderMgmt = orderSearchEntity.getNoteOrderMgmt();
			this.noteDelivery = orderSearchEntity.getNoteDelivery();
			this.noteDispatching = orderSearchEntity.getNoteDispatching();
			this.noteTruckDriver = orderSearchEntity.getNoteTruckDriver();
			this.noteRailwayDisp = orderSearchEntity.getNoteRailwayDisp();
			this.orderNumberFromSeqUsage = orderSearchEntity.getOrderNumberFromSeqUsage();
			this.shiptoAddress = orderSearchEntity.getShiptoAddress();
			this.loadingTs = orderSearchEntity.getLoadingTs();
			this.itrackStatusCd = orderSearchEntity.getItrackStatusCd();
			
	}
	
	public String getCompositeId() {
		return compositeId;
	}
	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getOrderCreationTypeCd() {
		return orderCreationTypeCd;
	}
	public void setOrderCreationTypeCd(Long orderCreationTypeCd) {
		this.orderCreationTypeCd = orderCreationTypeCd;
	}
	public Long getShiptoSapBpId() {
		return shiptoSapBpId;
	}
	public void setShiptoSapBpId(Long shiptoSapBpId) {
		this.shiptoSapBpId = shiptoSapBpId;
	}
	public Long getSoldtoSapBpId() {
		return soldtoSapBpId;
	}
	public void setSoldtoSapBpId(Long soldtoSapBpId) {
		this.soldtoSapBpId = soldtoSapBpId;
	}
	public Long getBilltoSapBpId() {
		return billtoSapBpId;
	}
	public void setBilltoSapBpId(Long billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}
	public Long getSareaId() {
		return sareaId;
	}
	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}
	public Long getSapMaterialId() {
		return sapMaterialId;
	}
	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Long getOrderItemNo() {
		return orderItemNo;
	}
	public void setOrderItemNo(Long orderItemNo) {
		this.orderItemNo = orderItemNo;
	}
	public String getOrderedUomCd() {
		return orderedUomCd;
	}
	public void setOrderedUomCd(String orderedUomCd) {
		this.orderedUomCd = orderedUomCd;
	}
	public String getOrderedUomTxt() {
		return orderedUomTxt;
	}
	public void setOrderedUomTxt(String orderedUomTxt) {
		this.orderedUomTxt = orderedUomTxt;
	}
	public java.math.BigDecimal getOrderedQuantity() {
		return orderedQuantity;
	}
	public void setOrderedQuantity(java.math.BigDecimal orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}
	public String getLoadedUomCd() {
		return loadedUomCd;
	}
	public void setLoadedUomCd(String loadedUomCd) {
		this.loadedUomCd = loadedUomCd;
	}
	public String getLoadedUomTxt() {
		return loadedUomTxt;
	}
	public void setLoadedUomTxt(String loadedUomTxt) {
		this.loadedUomTxt = loadedUomTxt;
	}
	public java.math.BigDecimal getLoadedQuantity() {
		return loadedQuantity;
	}
	public void setLoadedQuantity(java.math.BigDecimal loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}
	public String getDeliveredUomCd() {
		return deliveredUomCd;
	}
	public void setDeliveredUomCd(String deliveredUomCd) {
		this.deliveredUomCd = deliveredUomCd;
	}
	public String getDeliveredUomTxt() {
		return deliveredUomTxt;
	}
	public void setDeliveredUomTxt(String deliveredUomTxt) {
		this.deliveredUomTxt = deliveredUomTxt;
	}
	public java.math.BigDecimal getDeliveredQuantity() {
		return deliveredQuantity;
	}
	public void setDeliveredQuantity(java.math.BigDecimal deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}
	public String getReturnedUomCd() {
		return returnedUomCd;
	}
	public void setReturnedUomCd(String returnedUomCd) {
		this.returnedUomCd = returnedUomCd;
	}
	public String getReturnedUomTxt() {
		return returnedUomTxt;
	}
	public void setReturnedUomTxt(String returnedUomTxt) {
		this.returnedUomTxt = returnedUomTxt;
	}
	public java.math.BigDecimal getReturnedQuantity() {
		return returnedQuantity;
	}
	public void setReturnedQuantity(java.math.BigDecimal returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}
	public Date getDeliveryFromDat() {
		return deliveryFromDat;
	}
	public void setDeliveryFromDat(Date deliveryFromDat) {
		this.deliveryFromDat = deliveryFromDat;
	}
	public Date getDeliveryToDat() {
		return deliveryToDat;
	}
	public void setDeliveryToDat(Date deliveryToDat) {
		this.deliveryToDat = deliveryToDat;
	}
	public String getDistributionDestCd() {
		return distributionDestCd;
	}
	public void setDistributionDestCd(String distributionDestCd) {
		this.distributionDestCd = distributionDestCd;
	}
	public Long getOrderStatusCd() {
		return orderStatusCd;
	}
	public void setOrderStatusCd(Long orderStatusCd) {
		this.orderStatusCd = orderStatusCd;
	}
	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}
	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
	}
	public Long getShippingpointId() {
		return shippingpointId;
	}
	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}
	public Long getTransportDurationMin() {
		return transportDurationMin;
	}
	public void setTransportDurationMin(Long transportDurationMin) {
		this.transportDurationMin = transportDurationMin;
	}
	public Long getPlannedLoadingTimeMin() {
		return plannedLoadingTimeMin;
	}
	public void setPlannedLoadingTimeMin(Long plannedLoadingTimeMin) {
		this.plannedLoadingTimeMin = plannedLoadingTimeMin;
	}
	public Long getLoadingpointId() {
		return loadingpointId;
	}
	public void setLoadingpointId(Long loadingpointId) {
		this.loadingpointId = loadingpointId;
	}
	public Long getPoolId() {
		return poolId;
	}
	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}
	public Long getCommCarrierId() {
		return commCarrierId;
	}
	public void setCommCarrierId(Long commCarrierId) {
		this.commCarrierId = commCarrierId;
	}
	public Date getPlannedDeliveryDat() {
		return plannedDeliveryDat;
	}
	public void setPlannedDeliveryDat(Date plannedDeliveryDat) {
		this.plannedDeliveryDat = plannedDeliveryDat;
	}
	public String getOrderValidationCode() {
		return orderValidationCode;
	}
	public void setOrderValidationCode(String orderValidationCode) {
		this.orderValidationCode = orderValidationCode;
	}
	public String getSoldtoName1() {
		return soldtoName1;
	}
	public void setSoldtoName1(String soldtoName1) {
		this.soldtoName1 = soldtoName1;
	}
	public String getShiptoName1() {
		return shiptoName1;
	}
	public void setShiptoName1(String shiptoName1) {
		this.shiptoName1 = shiptoName1;
	}
	public String getShiptoCity() {
		return shiptoCity;
	}
	public void setShiptoCity(String shiptoCity) {
		this.shiptoCity = shiptoCity;
	}
	public String getBilltoName1() {
		return billtoName1;
	}
	public void setBilltoName1(String billtoName1) {
		this.billtoName1 = billtoName1;
	}
	public Long getShiptoSareaId() {
		return shiptoSareaId;
	}
	public void setShiptoSareaId(Long shiptoSareaId) {
		this.shiptoSareaId = shiptoSareaId;
	}
	public String getShiptoSgroupCd() {
		return shiptoSgroupCd;
	}
	public void setShiptoSgroupCd(String shiptoSgroupCd) {
		this.shiptoSgroupCd = shiptoSgroupCd;
	}
	public String getSgroupCd() {
		return sgroupCd;
	}
	public void setSgroupCd(String sgroupCd) {
		this.sgroupCd = sgroupCd;
	}
	public String getShiptoSofficeCd() {
		return shiptoSofficeCd;
	}
	public void setShiptoSofficeCd(String shiptoSofficeCd) {
		this.shiptoSofficeCd = shiptoSofficeCd;
	}
	public String getLangCd() {
		return langCd;
	}
	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}
	public String getStatusTxt() {
		return statusTxt;
	}
	public void setStatusTxt(String statusTxt) {
		this.statusTxt = statusTxt;
	}
	public Long getWarnStatusCode() {
		return warnStatusCode;
	}
	public void setWarnStatusCode(Long warnStatusCode) {
		this.warnStatusCode = warnStatusCode;
	}
	public Long getTimeToRedLine() {
		return timeToRedLine;
	}
	public void setTimeToRedLine(Long timeToRedLine) {
		this.timeToRedLine = timeToRedLine;
	}
	public Boolean getOnHoldOrderAndLockedFlag() {
		return onHoldOrderAndLockedFlag;
	}
	public void setOnHoldOrderAndLockedFlag(Boolean onHoldOrderAndLockedFlag) {
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getCommCarrierName() {
		return commCarrierName;
	}
	public void setCommCarrierName(String commCarrierName) {
		this.commCarrierName = commCarrierName;
	}
	public Long getSapVendorId() {
		return sapVendorId;
	}
	public void setSapVendorId(Long sapVendorId) {
		this.sapVendorId = sapVendorId;
	}
	public String getPlantCd() {
		return plantCd;
	}
	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}
	public String getShippingpointName() {
		return shippingpointName;
	}
	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}
	public String getLoadingpointName() {
		return loadingpointName;
	}
	public void setLoadingpointName(String loadingpointName) {
		this.loadingpointName = loadingpointName;
	}
	public Long getLoadingpointLoadingTime() {
		return loadingpointLoadingTime;
	}
	public void setLoadingpointLoadingTime(Long loadingpointLoadingTime) {
		this.loadingpointLoadingTime = loadingpointLoadingTime;
	}
	public String getMaterialNo() {
		return materialNo;
	}
	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}
	public String getMaterialTxt() {
		return materialTxt;
	}
	public void setMaterialTxt(String materialTxt) {
		this.materialTxt = materialTxt;
	}
	public String getDeliveryTypeTxt() {
		return deliveryTypeTxt;
	}
	public void setDeliveryTypeTxt(String deliveryTypeTxt) {
		this.deliveryTypeTxt = deliveryTypeTxt;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getSorgCd() {
		return sorgCd;
	}
	public void setSorgCd(String sorgCd) {
		this.sorgCd = sorgCd;
	}
	public String getCompanycodeCd() {
		return companycodeCd;
	}
	public void setCompanycodeCd(String companycodeCd) {
		this.companycodeCd = companycodeCd;
	}
	public String getDriverFullName() {
		return driverFullName;
	}
	public void setDriverFullName(String driverFullName) {
		this.driverFullName = driverFullName;
	}
	public Long getTruckId() {
		return truckId;
	}
	public void setTruckId(Long truckId) {
		this.truckId = truckId;
	}
	public Long getWagonId() {
		return wagonId;
	}
	public void setWagonId(Long wagonId) {
		this.wagonId = wagonId;
	}
	public String getVehicleName() {
		return vehicleName;
	}
	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}
	public String getMaterialPackageCd() {
		return materialPackageCd;
	}
	public void setMaterialPackageCd(String materialPackageCd) {
		this.materialPackageCd = materialPackageCd;
	}
	public Double getTruckMaxNetWeightT() {
		return truckMaxNetWeightT;
	}
	public void setTruckMaxNetWeightT(Double truckMaxNetWeightT) {
		this.truckMaxNetWeightT = truckMaxNetWeightT;
	}
	public Double getWagonMaxNetWeightT() {
		return wagonMaxNetWeightT;
	}
	public void setWagonMaxNetWeightT(Double wagonMaxNetWeightT) {
		this.wagonMaxNetWeightT = wagonMaxNetWeightT;
	}
	public Double getStatsQuantity() {
		return statsQuantity;
	}
	public void setStatsQuantity(Double statsQuantity) {
		this.statsQuantity = statsQuantity;
	}
	public String getStatsUomCd() {
		return statsUomCd;
	}
	public void setStatsUomCd(String statsUomCd) {
		this.statsUomCd = statsUomCd;
	}
	public String getShiptoTimezoneCd() {
		return shiptoTimezoneCd;
	}
	public void setShiptoTimezoneCd(String shiptoTimezoneCd) {
		this.shiptoTimezoneCd = shiptoTimezoneCd;
	}
	public Long getCommSapVendorId() {
		return commSapVendorId;
	}
	public void setCommSapVendorId(Long commSapVendorId) {
		this.commSapVendorId = commSapVendorId;
	}
	public String getExecCarrierName() {
		return execCarrierName;
	}
	public void setExecCarrierName(String execCarrierName) {
		this.execCarrierName = execCarrierName;
	}
	public Long getExecSapVendorId() {
		return execSapVendorId;
	}
	public void setExecSapVendorId(Long execSapVendorId) {
		this.execSapVendorId = execSapVendorId;
	}
	public Long getSimpleVehicleId() {
		return simpleVehicleId;
	}
	public void setSimpleVehicleId(Long simpleVehicleId) {
		this.simpleVehicleId = simpleVehicleId;
	}
	public Double getSimVehMaxNetWeightT() {
		return simVehMaxNetWeightT;
	}
	public void setSimVehMaxNetWeightT(Double simVehMaxNetWeightT) {
		this.simVehMaxNetWeightT = simVehMaxNetWeightT;
	}

	public Long getExecCarrierId() {
		return execCarrierId;
	}

	public void setExecCarrierId(Long execCarrierId) {
		this.execCarrierId = execCarrierId;
	}

	public String getDeliveryFrom() {
		return deliveryFrom;
	}

	public void setDeliveryFrom(String deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}

	public String getDeliveryTo() {
		return deliveryTo;
	}

	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}

	public String getPlannedDelivery() {
		return plannedDelivery;
	}

	public void setPlannedDelivery(String plannedDelivery) {
		this.plannedDelivery = plannedDelivery;
	}

	public String getLdsDeliveryNoteNo() {
		return ldsDeliveryNoteNo;
	}

	public void setLdsDeliveryNoteNo(String ldsDeliveryNoteNo) {
		this.ldsDeliveryNoteNo = ldsDeliveryNoteNo;
	}

	public String getNoteOrderMgmt() {
		return noteOrderMgmt;
	}

	public void setNoteOrderMgmt(String noteOrderMgmt) {
		this.noteOrderMgmt = noteOrderMgmt;
	}

	public String getNoteDelivery() {
		return noteDelivery;
	}

	public void setNoteDelivery(String noteDelivery) {
		this.noteDelivery = noteDelivery;
	}

	public String getNoteDispatching() {
		return noteDispatching;
	}

	public void setNoteDispatching(String noteDispatching) {
		this.noteDispatching = noteDispatching;
	}

	public String getNoteTruckDriver() {
		return noteTruckDriver;
	}

	public void setNoteTruckDriver(String noteTruckDriver) {
		this.noteTruckDriver = noteTruckDriver;
	}

	public String getNoteRailwayDisp() {
		return noteRailwayDisp;
	}

	public void setNoteRailwayDisp(String noteRailwayDisp) {
		this.noteRailwayDisp = noteRailwayDisp;
	}

	public String getOrderNumberFromSeqUsage() {
		return orderNumberFromSeqUsage;
	}

	public void setOrderNumberFromSeqUsage(String orderNumberFromSeqUsage) {
		this.orderNumberFromSeqUsage = orderNumberFromSeqUsage;
	}

	public String getShiptoAddress() {
		return shiptoAddress;
	}

	public void setShiptoAddress(String shiptoAddress) {
		this.shiptoAddress = shiptoAddress;
	}

	public Date getLoadingTs() {
		return loadingTs;
	}

	public void setLoadingTs(Date loadingTs) {
		this.loadingTs = loadingTs;
	}

	public String getLoadingStr() {
		return loadingStr;
	}

	public void setLoadingStr(String loadingStr) {
		this.loadingStr = loadingStr;
	}

	public Long getItrackStatusCd() {
		return itrackStatusCd;
	}

	public void setItrackStatusCd(Long itrackStatusCd) {
		this.itrackStatusCd = itrackStatusCd;
	}

	public int compareTo(OrderSearchBean other) {
		Long otherOrderNumber = other.getOrderNumber();
		return this.orderNumber.compareTo(otherOrderNumber);
	}
}

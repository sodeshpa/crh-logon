package com.crh.logon.order.web.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * In order to minimize changes in the liveMap.js file, we duplicate OrderFiltersEntity bean
 * here to support overwriting the DeliveryTo date field.
 *
 * @author AdNovum Informatik AG
 */
public class OrderFiltersBean implements Serializable {
	private static final long serialVersionUID = 1L;

	//--- New date/time fields ---//
	private String deliveryTo;

	//--- Fields copied from OrderFiltersEntity ---//
	private String compositeId;
	private Long orderId;
	private Long orderNumber;
	private String productionPlantCd;
	private String plantName;
	private Long shippingpointId;
	private String shippingpointName;
	private String plantCd;
	private Long loadingpointId;
	private String loadingpointName;
	private Long poolId;
	private String poolName;
	private Long commCarrierId;
	private String commCarrierName;
	private Long sapVendorId;
	private Boolean assignmentActiveFlag;
	private Long truckId;
	private Long wagonId;
	private Long trailerId;
	private Long containerId;
	private String truckName;
	private Long driverId;
	private String driverName;
	private String sofficeCd;
	private String sofficeName;
	private String sgroupCd;
	private String sgroupName;
	private Long sareaId;
	private String sareaName;
	private Long shiptoSapBpId;
	private String shiptoName1;
	private Long soldtoSapBpId;
	private String soldtoName1;
	private Long sapMaterialId;
	private String materialNo;
	private String materialTxt;
	private String statusTxt;
	private String langCd;
	private java.util.Date deliveryFromDat;
	private java.util.Date deliveryToDat;
	private java.util.Date actualDeliveryDat;
	private Long orderStatusCd;
	private Long orderItemNo;
	private Double plantCoordLat;
	private Double plantCoordLon;
	private Double shiptoCoordLat;
	private Double shiptoCoordLon;
	private Long shiptoGeofenceRadius;
	private Double shippCoordLat;
	private Double shippCoordLon;
	private Long shippGeofenceRadius;
	private java.util.Date loadingTs;
	private java.util.Date unloadingTs;
	private String shiptoTimezoneCd;

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

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getProductionPlantCd() {
		return productionPlantCd;
	}

	public void setProductionPlantCd(String productionPlantCd) {
		this.productionPlantCd = productionPlantCd;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public Long getShippingpointId() {
		return shippingpointId;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	public String getShippingpointName() {
		return shippingpointName;
	}

	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}

	public String getPlantCd() {
		return plantCd;
	}

	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}

	public Long getLoadingpointId() {
		return loadingpointId;
	}

	public void setLoadingpointId(Long loadingpointId) {
		this.loadingpointId = loadingpointId;
	}

	public String getLoadingpointName() {
		return loadingpointName;
	}

	public void setLoadingpointName(String loadingpointName) {
		this.loadingpointName = loadingpointName;
	}

	public Long getPoolId() {
		return poolId;
	}

	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public Long getCommCarrierId() {
		return commCarrierId;
	}

	public void setCommCarrierId(Long commCarrierId) {
		this.commCarrierId = commCarrierId;
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

	public Boolean getAssignmentActiveFlag() {
		return assignmentActiveFlag;
	}

	public void setAssignmentActiveFlag(Boolean assignmentActiveFlag) {
		this.assignmentActiveFlag = assignmentActiveFlag;
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

	public Long getTrailerId() {
		return trailerId;
	}

	public void setTrailerId(Long trailerId) {
		this.trailerId = trailerId;
	}

	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	public String getTruckName() {
		return truckName;
	}

	public void setTruckName(String truckName) {
		this.truckName = truckName;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getSofficeCd() {
		return sofficeCd;
	}

	public void setSofficeCd(String sofficeCd) {
		this.sofficeCd = sofficeCd;
	}

	public String getSofficeName() {
		return sofficeName;
	}

	public void setSofficeName(String sofficeName) {
		this.sofficeName = sofficeName;
	}

	public String getSgroupCd() {
		return sgroupCd;
	}

	public void setSgroupCd(String sgroupCd) {
		this.sgroupCd = sgroupCd;
	}

	public String getSgroupName() {
		return sgroupName;
	}

	public void setSgroupName(String sgroupName) {
		this.sgroupName = sgroupName;
	}

	public Long getSareaId() {
		return sareaId;
	}

	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}

	public String getSareaName() {
		return sareaName;
	}

	public void setSareaName(String sareaName) {
		this.sareaName = sareaName;
	}

	public Long getShiptoSapBpId() {
		return shiptoSapBpId;
	}

	public void setShiptoSapBpId(Long shiptoSapBpId) {
		this.shiptoSapBpId = shiptoSapBpId;
	}

	public String getShiptoName1() {
		return shiptoName1;
	}

	public void setShiptoName1(String shiptoName1) {
		this.shiptoName1 = shiptoName1;
	}

	public Long getSoldtoSapBpId() {
		return soldtoSapBpId;
	}

	public void setSoldtoSapBpId(Long soldtoSapBpId) {
		this.soldtoSapBpId = soldtoSapBpId;
	}

	public String getSoldtoName1() {
		return soldtoName1;
	}

	public void setSoldtoName1(String soldtoName1) {
		this.soldtoName1 = soldtoName1;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
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

	public String getStatusTxt() {
		return statusTxt;
	}

	public void setStatusTxt(String statusTxt) {
		this.statusTxt = statusTxt;
	}

	public String getLangCd() {
		return langCd;
	}

	public void setLangCd(String langCd) {
		this.langCd = langCd;
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

	public Date getActualDeliveryDat() {
		return actualDeliveryDat;
	}

	public void setActualDeliveryDat(Date actualDeliveryDat) {
		this.actualDeliveryDat = actualDeliveryDat;
	}

	public Long getOrderStatusCd() {
		return orderStatusCd;
	}

	public void setOrderStatusCd(Long orderStatusCd) {
		this.orderStatusCd = orderStatusCd;
	}

	public Long getOrderItemNo() {
		return orderItemNo;
	}

	public void setOrderItemNo(Long orderItemNo) {
		this.orderItemNo = orderItemNo;
	}

	public Double getPlantCoordLat() {
		return plantCoordLat;
	}

	public void setPlantCoordLat(Double plantCoordLat) {
		this.plantCoordLat = plantCoordLat;
	}

	public Double getPlantCoordLon() {
		return plantCoordLon;
	}

	public void setPlantCoordLon(Double plantCoordLon) {
		this.plantCoordLon = plantCoordLon;
	}

	public Double getShiptoCoordLat() {
		return shiptoCoordLat;
	}

	public void setShiptoCoordLat(Double shiptoCoordLat) {
		this.shiptoCoordLat = shiptoCoordLat;
	}

	public Double getShiptoCoordLon() {
		return shiptoCoordLon;
	}

	public void setShiptoCoordLon(Double shiptoCoordLon) {
		this.shiptoCoordLon = shiptoCoordLon;
	}

	public Long getShiptoGeofenceRadius() {
		return shiptoGeofenceRadius;
	}

	public void setShiptoGeofenceRadius(Long shiptoGeofenceRadius) {
		this.shiptoGeofenceRadius = shiptoGeofenceRadius;
	}

	public Double getShippCoordLat() {
		return shippCoordLat;
	}

	public void setShippCoordLat(Double shippCoordLat) {
		this.shippCoordLat = shippCoordLat;
	}

	public Double getShippCoordLon() {
		return shippCoordLon;
	}

	public void setShippCoordLon(Double shippCoordLon) {
		this.shippCoordLon = shippCoordLon;
	}

	public Long getShippGeofenceRadius() {
		return shippGeofenceRadius;
	}

	public void setShippGeofenceRadius(Long shippGeofenceRadius) {
		this.shippGeofenceRadius = shippGeofenceRadius;
	}

	public Date getLoadingTs() {
		return loadingTs;
	}

	public void setLoadingTs(Date loadingTs) {
		this.loadingTs = loadingTs;
	}

	public Date getUnloadingTs() {
		return unloadingTs;
	}

	public void setUnloadingTs(Date unloadingTs) {
		this.unloadingTs = unloadingTs;
	}

	public String getShiptoTimezoneCd() {
		return shiptoTimezoneCd;
	}

	public void setShiptoTimezoneCd(String shiptoTimezoneCd) {
		this.shiptoTimezoneCd = shiptoTimezoneCd;
	}

	public String getDeliveryTo() {
		return deliveryTo;
	}

	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
}

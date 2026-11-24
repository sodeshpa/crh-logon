package com.crh.logon.admin.web.beans;

import com.crh.logon.business.services.utils.TimeZone;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author AdNovum Informatik AG
 */
public class WagonSearchResultBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String compositeId;
	private Long wagonId;
	private String wagonNo;
	private String wagonTypeName;
	private String lastKnownStationName;
	private Double wagonIdleTimeMin;
	private Date arrivalLastKnownStationDat;
	private String arrivalLastKnownStation;
	private String plantTrackTxt;
	private Long currentTrackPositionNo;
	private String destRailwayStationName;
	private String returnShiptoName1;
	private Long orderId;
	private Long orderNumber;
	private Long orderItemNo;
	private String orderStatusTxt;
	private String distributionDestTxt;
	private String soldtoName1;
	private String shiptoName1;
	private String sapMaterialTxt;
	private BigDecimal loadedQuantity;
	private String loadedUom;
	private String wagonKeeperName;
	private String wagonNoteRailwaySpedition;
	private Boolean activeFlag;

	private TimeZone userTimeZone;

	public WagonSearchResultBean() { }

	public WagonSearchResultBean(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

	public String getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}

	public Long getWagonId() {
		return wagonId;
	}

	public void setWagonId(Long wagonId) {
		this.wagonId = wagonId;
	}

	public String getWagonNo() {
		return wagonNo;
	}

	public void setWagonNo(String wagonNo) {
		this.wagonNo = wagonNo;
	}

	public String getWagonTypeName() {
		return wagonTypeName;
	}

	public void setWagonTypeName(String wagonTypeName) {
		this.wagonTypeName = wagonTypeName;
	}

	public String getLastKnownStationName() {
		return lastKnownStationName;
	}

	public void setLastKnownStationName(String lastKnownStationName) {
		this.lastKnownStationName = lastKnownStationName;
	}

	public Double getWagonIdleTimeMin() {
		return wagonIdleTimeMin;
	}

	public void setWagonIdleTimeMin(Double wagonIdleTimeMin) {
		this.wagonIdleTimeMin = wagonIdleTimeMin;
	}

	public Date getArrivalLastKnownStationDat() {
		return arrivalLastKnownStationDat;
	}

	public void setArrivalLastKnownStationDat(Date arrivalLastKnownStationDat) {
		this.arrivalLastKnownStationDat = arrivalLastKnownStationDat;
		this.arrivalLastKnownStation = userTimeZone.fmtAsDayMonthYearHourMinute(arrivalLastKnownStationDat);
	}

	public String getArrivalLastKnownStation() {
		return this.arrivalLastKnownStation;
	}

	public String getPlantTrackTxt() {
		return plantTrackTxt;
	}

	public void setPlantTrackTxt(String plantTrackTxt) {
		this.plantTrackTxt = plantTrackTxt;
	}

	public Long getCurrentTrackPositionNo() {
		return currentTrackPositionNo;
	}

	public void setCurrentTrackPositionNo(Long currentTrackPositionNo) {
		this.currentTrackPositionNo = currentTrackPositionNo;
	}

	public String getDestRailwayStationName() {
		return destRailwayStationName;
	}

	public void setDestRailwayStationName(String destRailwayStationName) {
		this.destRailwayStationName = destRailwayStationName;
	}

	public String getReturnShiptoName1() {
		return returnShiptoName1;
	}

	public void setReturnShiptoName1(String returnShiptoName1) {
		this.returnShiptoName1 = returnShiptoName1;
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

	public Long getOrderItemNo() {
		return orderItemNo;
	}

	public void setOrderItemNo(Long orderItemNo) {
		this.orderItemNo = orderItemNo;
	}

	public String getOrderStatusTxt() {
		return orderStatusTxt;
	}

	public void setOrderStatusTxt(String orderStatusTxt) {
		this.orderStatusTxt = orderStatusTxt;
	}

	public String getDistributionDestTxt() {
		return distributionDestTxt;
	}

	public void setDistributionDestTxt(String distributionDestTxt) {
		this.distributionDestTxt = distributionDestTxt;
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

	public String getSapMaterialTxt() {
		return sapMaterialTxt;
	}

	public void setSapMaterialTxt(String sapMaterialTxt) {
		this.sapMaterialTxt = sapMaterialTxt;
	}

	public BigDecimal getLoadedQuantity() {
		return loadedQuantity;
	}

	public void setLoadedQuantity(BigDecimal loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}

	public String getLoadedUom() {
		return loadedUom;
	}

	public void setLoadedUom(String loadedUom) {
		this.loadedUom = loadedUom;
	}

	public String getWagonKeeperName() {
		return wagonKeeperName;
	}

	public void setWagonKeeperName(String wagonKeeperName) {
		this.wagonKeeperName = wagonKeeperName;
	}

	public String getWagonNoteRailwaySpedition() {
		return wagonNoteRailwaySpedition;
	}

	public void setWagonNoteRailwaySpedition(String wagonNoteRailwaySpedition) {
		this.wagonNoteRailwaySpedition = wagonNoteRailwaySpedition;
	}

	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
}


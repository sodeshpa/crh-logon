/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author AdNovum Informatik AG
 */
public class RailDispoExportWagonRow implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long wagonNo;
	private String wagonKeeperName;
	private String wagonType;
	private BigDecimal writtenTareWeightT;
	private String trackName;
	private Long position;
	private String containerNames;
	private Long status;
	private Long orderNo;
	private String etranOut;
	private String etranRet;
	private String orderMaterialName;
	private BigDecimal orderQty;
	private String orderUom;
	private String shippintPtName;
	private String loadingPtName;
	private String shippintPtStationNo;
	private String shippintPtStationName;
	private Long shippintPtCountryCd;
	private String soldToNumber;
	private String soldToName;
	private String soldToName2;
	private String shipToNumber;
	private String shipToName;
	private String shipToName2;
	private String shipToStationNo;
	private String shipToStationName;
	private Long shipToCountryCd;
	private Calendar manipulationTime;
	private Calendar deliveryFrom;
	private Calendar deliveryTo;
	private String carrierName;
	private String loadMaterial;
	private BigDecimal loadTare;
	private BigDecimal loadNet;
	private BigDecimal loadGross;
	private String nhm;
	private Calendar toRailCo;
	private Calendar departure;
	private Calendar arrival;
	private String noteRailwayDisp;
	private String wagonNoteRailwaySpedition;
	private String noteDispatching;
	private String wagonNoteDisposition;

	public Long getWagonNo() {
		return wagonNo;
	}
	public void setWagonNo(Long wagonNo) {
		this.wagonNo = wagonNo;
	}
	public String getWagonKeeperName() {
		return wagonKeeperName;
	}
	public void setWagonKeeperName(String wagonKeeperName) {
		this.wagonKeeperName = wagonKeeperName;
	}
	public String getWagonType() {
		return wagonType;
	}
	public void setWagonType(String wagonType) {
		this.wagonType = wagonType;
	}
	public String getTrackName() {
		return trackName;
	}
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	public Long getPosition() {
		return position;
	}
	public void setPosition(Long position) {
		this.position = position;
	}
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	public Long getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}
	public String getEtranOut() {
		return etranOut;
	}
	public void setEtranOut(String etranOut) {
		this.etranOut = etranOut;
	}
	public String getEtranRet() {
		return etranRet;
	}
	public void setEtranRet(String etranRet) {
		this.etranRet = etranRet;
	}
	public String getOrderMaterialName() {
		return orderMaterialName;
	}
	public void setOrderMaterialName(String orderMaterialName) {
		this.orderMaterialName = orderMaterialName;
	}
	public BigDecimal getOrderQty() {
		return orderQty;
	}
	public void setOrderQty(BigDecimal orderQty) {
		this.orderQty = orderQty;
	}
	public String getOrderUom() {
		return orderUom;
	}
	public void setOrderUom(String orderUom) {
		this.orderUom = orderUom;
	}
	public String getShippintPtName() {
		return shippintPtName;
	}
	public void setShippintPtName(String shippintPtName) {
		this.shippintPtName = shippintPtName;
	}
	public String getLoadingPtName() {
		return loadingPtName;
	}
	public void setLoadingPtName(String loadingPtName) {
		this.loadingPtName = loadingPtName;
	}
	public String getShippintPtStationNo() {
		return shippintPtStationNo;
	}
	public void setShippintPtStationNo(String shippintPtStationNo) {
		this.shippintPtStationNo = shippintPtStationNo;
	}
	public String getShippintPtStationName() {
		return shippintPtStationName;
	}
	public void setShippintPtStationName(String shippintPtStationName) {
		this.shippintPtStationName = shippintPtStationName;
	}
	public Long getShippintPtCountryCd() {
		return shippintPtCountryCd;
	}
	public void setShippintPtCountryCd(Long shippintPtCountryCd) {
		this.shippintPtCountryCd = shippintPtCountryCd;
	}
	public String getSoldToNumber() {
		return soldToNumber;
	}
	public void setSoldToNumber(String soldToNumber) {
		this.soldToNumber = soldToNumber;
	}
	public String getSoldToName() {
		return soldToName;
	}
	public void setSoldToName(String soldToName) {
		this.soldToName = soldToName;
	}
	public String getSoldToName2() {
		return soldToName2;
	}
	public void setSoldToName2(String soldToName2) {
		this.soldToName2 = soldToName2;
	}
	public String getShipToNumber() {
		return shipToNumber;
	}
	public void setShipToNumber(String shipToNumber) {
		this.shipToNumber = shipToNumber;
	}
	public String getShipToName() {
		return shipToName;
	}
	public void setShipToName(String shipToName) {
		this.shipToName = shipToName;
	}
	public String getShipToName2() {
		return shipToName2;
	}
	public void setShipToName2(String shipToName2) {
		this.shipToName2 = shipToName2;
	}
	public String getShipToStationNo() {
		return shipToStationNo;
	}
	public void setShipToStationNo(String shipToStationNo) {
		this.shipToStationNo = shipToStationNo;
	}
	public String getShipToStationName() {
		return shipToStationName;
	}
	public void setShipToStationName(String shipToStationName) {
		this.shipToStationName = shipToStationName;
	}
	public Long getShipToCountryCd() {
		return shipToCountryCd;
	}
	public void setShipToCountryCd(Long shipToCountryCd) {
		this.shipToCountryCd = shipToCountryCd;
	}
	public Calendar getManipulationTime() {
		return manipulationTime;
	}
	public void setManipulationTime(Calendar manipulationTime) {
		this.manipulationTime = manipulationTime;
	}
	public Calendar getDeliveryFrom() {
		return deliveryFrom;
	}
	public void setDeliveryFrom(Calendar deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}
	public Calendar getDeliveryTo() {
		return deliveryTo;
	}
	public void setDeliveryTo(Calendar deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
	public String getCarrierName() {
		return carrierName;
	}
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	public String getLoadMaterial() {
		return loadMaterial;
	}
	public void setLoadMaterial(String loadMaterial) {
		this.loadMaterial = loadMaterial;
	}
	public BigDecimal getLoadTare() {
		return loadTare;
	}
	public void setLoadTare(BigDecimal loadTare) {
		this.loadTare = loadTare;
	}
	public BigDecimal getLoadNet() {
		return loadNet;
	}
	public void setLoadNet(BigDecimal loadNet) {
		this.loadNet = loadNet;
	}
	public BigDecimal getLoadGross() {
		return loadGross;
	}
	public void setLoadGross(BigDecimal loadGross) {
		this.loadGross = loadGross;
	}
	public String getNhm() {
		return nhm;
	}
	public void setNhm(String nhm) {
		this.nhm = nhm;
	}
	public Calendar getToRailCo() {
		return toRailCo;
	}
	public void setToRailCo(Calendar toRailCo) {
		this.toRailCo = toRailCo;
	}
	public Calendar getDeparture() {
		return departure;
	}
	public void setDeparture(Calendar departure) {
		this.departure = departure;
	}
	public Calendar getArrival() {
		return arrival;
	}
	public void setArrival(Calendar arrival) {
		this.arrival = arrival;
	}
	public String getNoteRailwayDisp() {
		return noteRailwayDisp;
	}
	public void setNoteRailwayDisp(String noteRailwayDisp) {
		this.noteRailwayDisp = noteRailwayDisp;
	}
	public String getWagonNoteRailwaySpedition() {
		return wagonNoteRailwaySpedition;
	}
	public void setWagonNoteRailwaySpedition(String wagonNoteRailwaySpedition) {
		this.wagonNoteRailwaySpedition = wagonNoteRailwaySpedition;
	}
	public String getNoteDispatching() {
		return noteDispatching;
	}
	public void setNoteDispatching(String noteDispatching) {
		this.noteDispatching = noteDispatching;
	}
	public String getWagonNoteDisposition() {
		return wagonNoteDisposition;
	}
	public void setWagonNoteDisposition(String wagonNoteDisposition) {
		this.wagonNoteDisposition = wagonNoteDisposition;
	}
	public String getContainerNames() {
		return containerNames;
	}
	public void setContainerNames(String containerNames) {
		this.containerNames = containerNames;
	}
	public BigDecimal getWrittenTareWeightT() {
		return writtenTareWeightT;
	}
	public void setWrittenTareWeightT(BigDecimal writtenTareWeightT) {
		this.writtenTareWeightT = writtenTareWeightT;
	}

}

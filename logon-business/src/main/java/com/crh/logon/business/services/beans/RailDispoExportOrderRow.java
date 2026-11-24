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
public class RailDispoExportOrderRow implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long orderNumber;
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
	private String shipToPrefs;
	private String noteRailwayDisp;
	private String noteDispatching;

	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
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
	public String getShipToPrefs() {
		return shipToPrefs;
	}
	public void setShipToPrefs(String shipToPrefs) {
		this.shipToPrefs = shipToPrefs;
	}
	public String getNoteRailwayDisp() {
		return noteRailwayDisp;
	}
	public void setNoteRailwayDisp(String noteRailwayDisp) {
		this.noteRailwayDisp = noteRailwayDisp;
	}
	public String getNoteDispatching() {
		return noteDispatching;
	}
	public void setNoteDispatching(String noteDispatching) {
		this.noteDispatching = noteDispatching;
	}

}

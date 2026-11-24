/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author AdNovum Informatik AG
 */
public class MaterialTabBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<OrderItemBean> orderItemList;
	private Map<Long, String> deliveryNoteMap;
	private int selOrderItemIndex;
	private Long selOrderItemId;
	private Boolean sentToSapIsNullFlag;
	
	// Mixdesign
	private Map<Long, String> mixDesignAggRmxMap;
	private String mixDesignAggRmxMapJson;
	
	//sold to
	private String soldtoSapId;
	private String soldtoName;
	private String soldToStreetHouseNumber;
	private String soldtoStreet;
	private String soldtoHouseNumber;
	private String soldtoCountry;
	private String soldtoPostalCode;
	private String soldtoCity;
	private String soldToVatNumber;

	//ship to
	private String shiptoSapId;
	private String shiptoType;
	private Long shiptoTypeCd;
	private String shipToStreetHouseNumber;
	private String shiptoName;
	private String shiptoStreet;
	private String shiptoHouseNumber;
	private String shiptoCountry;
	private String shiptoPostalCode;
	private String shiptoCity;
	private String shipToVatNumber;
	private String locationNotFixed;

	//bill to
	private String billtoSapId;
	private String billtoSapBpId;
	private String billtoName;
	private String billtoStreetHouseNumber;	
	private String billtoStreet;
	private String billtoHouseNumber;
	private String billtoCountry;
	private String billtoPostalCode;
	private String billtoCity;	

	//payer
	private String payerId;
	private String payerSapId;
	private String payerName;
	private String payerStreetHouseNumber;
	private String payerStreet;
	private String payerHouseNumber;
	private String payerCountry;
	private String payerPostalCode;
	private String payerCity;

	//commercial carrier 
	private String commCarrierVendorSapId;
	private String commCarrierName;
	private String commCarrierStreet;
	private String commCarrierHouseNumber;
	private String commCarrierCountry;
	private String commCarrierPostalCode;
	private String commCarrierCity;	

	//executing carrier 
	private String execCarrierVendorSapId;
	private String execCarrierName;
	private String execCarrierStreet;
	private String execCarrierHouseNumber;
	private String execCarrierCountry;
	private String execCarrierPostalCode;
	private String execCarrierCity;
	
	//rail
	private String railDepUicCountryCd;
	private String railDepStationNo;
	private String railDepStationName;
	private String railDepStationLP;
	private String railArrUicCountryCd;
	private String railArrStationNo;
	private String railArrStationName;
	private String railArrStationUP;
	
	//goods movement
	private List<GoodsMovementDisplayBean> goodsMovementBeans;
	private Boolean goodsMovementPresent;
	
	// Conversion factors per orderItemId. (Stored here for easier access in JavaScript.)
	private String convFactorsMapJson;
	
	private Boolean hasServiceMaterial;
	
	
	private Boolean disabledBillToAndPayer;
	
	public String getLocationNotFixed() {
		return locationNotFixed;
	}
	public void setLocationNotFixed(String locationNotFixed) {
		this.locationNotFixed = locationNotFixed;
	}
	public Long getShiptoTypeCd() {
		return shiptoTypeCd;
	}
	public void setShiptoTypeCd(Long shiptoTypeCd) {
		this.shiptoTypeCd = shiptoTypeCd;
	}
	public String getShiptoType() {
		return shiptoType;
	}
	public void setShiptoType(String shiptoType) {
		this.shiptoType = shiptoType;
	}

	
	public Boolean getSentToSapIsNullFlag() {
		return sentToSapIsNullFlag;
	}
	public void setSentToSapIsNullFlag(Boolean sentToSapIsNullFlag) {
		this.sentToSapIsNullFlag = sentToSapIsNullFlag;
	}
	public String getRailDepUicCountryCd() {
		return railDepUicCountryCd;
	}
	public void setRailDepUicCountryCd(String railDepUicCountryCd) {
		this.railDepUicCountryCd = railDepUicCountryCd;
	}
	public String getRailDepStationNo() {
		return railDepStationNo;
	}
	public void setRailDepStationNo(String railDepStationNo) {
		this.railDepStationNo = railDepStationNo;
	}
	public String getRailDepStationName() {
		return railDepStationName;
	}
	public void setRailDepStationName(String railDepStationName) {
		this.railDepStationName = railDepStationName;
	}
	public String getRailDepStationLP() {
		return railDepStationLP;
	}
	public void setRailDepStationLP(String railDepStationLP) {
		this.railDepStationLP = railDepStationLP;
	}
	public String getRailArrUicCountryCd() {
		return railArrUicCountryCd;
	}
	public void setRailArrUicCountryCd(String railArrUicCountryCd) {
		this.railArrUicCountryCd = railArrUicCountryCd;
	}
	public String getRailArrStationNo() {
		return railArrStationNo;
	}
	public void setRailArrStationNo(String railArrStationNo) {
		this.railArrStationNo = railArrStationNo;
	}
	public String getRailArrStationName() {
		return railArrStationName;
	}
	public void setRailArrStationName(String railArrStationName) {
		this.railArrStationName = railArrStationName;
	}
	public String getRailArrStationUP() {
		return railArrStationUP;
	}
	public void setRailArrStationUP(String railArrStationUP) {
		this.railArrStationUP = railArrStationUP;
	}	
	public Long getSelOrderItemId() {
		return selOrderItemId;
	}
	public void setSelOrderItemId(Long selOrderItemId) {
		this.selOrderItemId = selOrderItemId;
	}
	public Map<Long, String> getDeliveryNoteMap() {
		return deliveryNoteMap;
	}
	public void setDeliveryNoteMap(Map<Long, String> deliveryNoteMap) {
		this.deliveryNoteMap = deliveryNoteMap;
	}
	public int getSelOrderItemIndex() {
		return selOrderItemIndex;
	}
	public void setSelOrderItemIndex(int selOrderItemIndex) {
		this.selOrderItemIndex = selOrderItemIndex;
	}
	public List<OrderItemBean> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItemBean> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public String getSoldtoSapId() {
		return soldtoSapId;
	}
	public void setSoldtoSapId(String soldtoSapId) {
		this.soldtoSapId = soldtoSapId;
	}
	public String getShiptoSapId() {
		return shiptoSapId;
	}
	public void setShiptoSapId(String shiptoSapId) {
		this.shiptoSapId = shiptoSapId;
	}

	public String getSoldtoName() {
		return soldtoName;
	}
	public void setSoldtoName(String soldtoName) {
		this.soldtoName = soldtoName;
	}
	public String getSoldtoStreet() {
		return soldtoStreet;
	}
	public void setSoldtoStreet(String soldtoStreet) {
		this.soldtoStreet = soldtoStreet;
	}
	public String getSoldtoHouseNumber() {
		return soldtoHouseNumber;
	}
	public void setSoldtoHouseNumber(String soldtoHouseNumber) {
		this.soldtoHouseNumber = soldtoHouseNumber;
	}
	public String getSoldtoCountry() {
		return soldtoCountry;
	}
	public void setSoldtoCountry(String soldtoCountry) {
		this.soldtoCountry = soldtoCountry;
	}
	public String getSoldtoPostalCode() {
		return soldtoPostalCode;
	}
	public void setSoldtoPostalCode(String soldtoPostalCode) {
		this.soldtoPostalCode = soldtoPostalCode;
	}
	public String getSoldtoCity() {
		return soldtoCity;
	}
	public void setSoldtoCity(String soldtoCity) {
		this.soldtoCity = soldtoCity;
	}
	public String getShiptoName() {
		return shiptoName;
	}
	public void setShiptoName(String shiptoName) {
		this.shiptoName = shiptoName;
	}
	public String getShiptoStreet() {
		return shiptoStreet;
	}
	public void setShiptoStreet(String shiptoStreet) {
		this.shiptoStreet = shiptoStreet;
	}
	public String getShiptoHouseNumber() {
		return shiptoHouseNumber;
	}
	public void setShiptoHouseNumber(String shiptoHouseNumber) {
		this.shiptoHouseNumber = shiptoHouseNumber;
	}
	public String getShiptoCountry() {
		return shiptoCountry;
	}
	public void setShiptoCountry(String shiptoCountry) {
		this.shiptoCountry = shiptoCountry;
	}
	public String getShiptoPostalCode() {
		return shiptoPostalCode;
	}
	public void setShiptoPostalCode(String shiptoPostalCode) {
		this.shiptoPostalCode = shiptoPostalCode;
	}
	public String getShiptoCity() {
		return shiptoCity;
	}
	public void setShiptoCity(String shiptoCity) {
		this.shiptoCity = shiptoCity;
	}
	public String getBilltoName() {
		return billtoName;
	}
	public void setBilltoName(String billtoName) {
		this.billtoName = billtoName;
	}
	public String getBilltoStreet() {
		return billtoStreet;
	}
	public void setBilltoStreet(String billtoStreet) {
		this.billtoStreet = billtoStreet;
	}
	public String getBilltoHouseNumber() {
		return billtoHouseNumber;
	}
	public void setBilltoHouseNumber(String billtoHouseNumber) {
		this.billtoHouseNumber = billtoHouseNumber;
	}
	public String getBilltoCountry() {
		return billtoCountry;
	}
	public void setBilltoCountry(String billtoCountry) {
		this.billtoCountry = billtoCountry;
	}
	public String getBilltoPostalCode() {
		return billtoPostalCode;
	}
	public void setBilltoPostalCode(String billtoPostalCode) {
		this.billtoPostalCode = billtoPostalCode;
	}
	public String getBilltoCity() {
		return billtoCity;
	}
	public void setBilltoCity(String billtoCity) {
		this.billtoCity = billtoCity;
	}

	public String getPayerId() {
		return payerId;
	}
	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}
	public String getPayerSapId() {
		return payerSapId;
	}
	public void setPayerSapId(String payerSapId) {
		this.payerSapId = payerSapId;
	}
	public String getPayerName() {
		return payerName;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public String getPayerStreet() {
		return payerStreet;
	}
	public void setPayerStreet(String payerStreet) {
		this.payerStreet = payerStreet;
	}
	public String getPayerHouseNumber() {
		return payerHouseNumber;
	}
	public void setPayerHouseNumber(String payerHouseNumber) {
		this.payerHouseNumber = payerHouseNumber;
	}
	public String getPayerCountry() {
		return payerCountry;
	}
	public void setPayerCountry(String payerCountry) {
		this.payerCountry = payerCountry;
	}
	public String getPayerPostalCode() {
		return payerPostalCode;
	}
	public void setPayerPostalCode(String payerPostalCode) {
		this.payerPostalCode = payerPostalCode;
	}
	public String getPayerCity() {
		return payerCity;
	}
	public void setPayerCity(String payerCity) {
		this.payerCity = payerCity;
	}
	public String getBilltoSapBpId() {
		return billtoSapBpId;
	}
	public void setBilltoSapBpId(String billtoSaBpId) {
		this.billtoSapBpId = billtoSaBpId;
	}
	public String getBilltoSapId() {
		return billtoSapId;
	}
	public void setBilltoSapId(String billtoSapId) {
		this.billtoSapId = billtoSapId;
	}
	public String getCommCarrierName() {
		return commCarrierName;
	}
	public void setCommCarrierName(String commCarrierName) {
		this.commCarrierName = commCarrierName;
	}
	public String getCommCarrierStreet() {
		return commCarrierStreet;
	}
	public void setCommCarrierStreet(String commCarrierStreet) {
		this.commCarrierStreet = commCarrierStreet;
	}
	public String getCommCarrierHouseNumber() {
		return commCarrierHouseNumber;
	}
	public void setCommCarrierHouseNumber(String commCarrierHouseNumber) {
		this.commCarrierHouseNumber = commCarrierHouseNumber;
	}
	public String getCommCarrierCountry() {
		return commCarrierCountry;
	}
	public void setCommCarrierCountry(String commCarrierCountry) {
		this.commCarrierCountry = commCarrierCountry;
	}
	public String getCommCarrierPostalCode() {
		return commCarrierPostalCode;
	}
	public void setCommCarrierPostalCode(String commCarrierPostalCode) {
		this.commCarrierPostalCode = commCarrierPostalCode;
	}
	public String getCommCarrierCity() {
		return commCarrierCity;
	}
	public void setCommCarrierCity(String commCarrierCity) {
		this.commCarrierCity = commCarrierCity;
	}
	public String getCommCarrierVendorSapId() {
		return commCarrierVendorSapId;
	}
	public void setCommCarrierVendorSapId(String commCarrierVendorSapId) {
		this.commCarrierVendorSapId = commCarrierVendorSapId;
	}
	public String getSoldToVatNumber() {
		return soldToVatNumber;
	}
	public void setSoldToVatNumber(String soldtoVatNumber) {
		this.soldToVatNumber = soldtoVatNumber;
	}
	public String getShipToVatNumber() {
		return shipToVatNumber;
	}
	public void setShipToVatNumber(String shipToVatNumber) {
		this.shipToVatNumber = shipToVatNumber;
	}
	public String getExecCarrierVendorSapId() {
		return execCarrierVendorSapId;
	}
	public void setExecCarrierVendorSapId(String execCarrierVendorSapId) {
		this.execCarrierVendorSapId = execCarrierVendorSapId;
	}
	public String getExecCarrierName() {
		return execCarrierName;
	}
	public void setExecCarrierName(String execCarrierName) {
		this.execCarrierName = execCarrierName;
	}
	public String getExecCarrierStreet() {
		return execCarrierStreet;
	}
	public void setExecCarrierStreet(String execCarrierStreet) {
		this.execCarrierStreet = execCarrierStreet;
	}
	public String getExecCarrierHouseNumber() {
		return execCarrierHouseNumber;
	}
	public void setExecCarrierHouseNumber(String execCarrierHouseNumber) {
		this.execCarrierHouseNumber = execCarrierHouseNumber;
	}
	public String getExecCarrierCountry() {
		return execCarrierCountry;
	}
	public void setExecCarrierCountry(String execCarrierCountry) {
		this.execCarrierCountry = execCarrierCountry;
	}
	public String getExecCarrierPostalCode() {
		return execCarrierPostalCode;
	}
	public void setExecCarrierPostalCode(String execCarrierPostalCode) {
		this.execCarrierPostalCode = execCarrierPostalCode;
	}
	public String getExecCarrierCity() {
		return execCarrierCity;
	}
	public void setExecCarrierCity(String execCarrierCity) {
		this.execCarrierCity = execCarrierCity;
	}
	public Map<Long, String> getMixDesignAggRmxMap() {
		return mixDesignAggRmxMap;
	}
	public void setMixDesignAggRmxMap(Map<Long, String> mixDesignAggRmxMap) {
		this.mixDesignAggRmxMap = mixDesignAggRmxMap;
	}
	public String getMixDesignAggRmxMapJson() {
		return mixDesignAggRmxMapJson;
	}
	public void setMixDesignAggRmxMapJson(String mixDesignAggRmxMapJson) {
		this.mixDesignAggRmxMapJson = mixDesignAggRmxMapJson;
	}
	public List<GoodsMovementDisplayBean> getGoodsMovementBeans() {
		return goodsMovementBeans;
	}
	public void setGoodsMovementBeans(List<GoodsMovementDisplayBean> goodsMovementBeans) {
		this.goodsMovementBeans = goodsMovementBeans;
	}
	public Boolean getGoodsMovementPresent() {
		return goodsMovementPresent;
	}
	public void setGoodsMovementPresent(Boolean goodsMovementPresent) {
		this.goodsMovementPresent = goodsMovementPresent;
	}
	public String getConvFactorsMapJson() {
		return convFactorsMapJson;
	}
	public void setConvFactorsMapJson(String convFactorsMapJson) {
		this.convFactorsMapJson = convFactorsMapJson;
	}
	public Boolean getHasServiceMaterial() {
		return hasServiceMaterial;
	}
	public void setHasServiceMaterial(Boolean hasServiceMaterial) {
		this.hasServiceMaterial = hasServiceMaterial;
	}
	public Boolean getDisabledBillToAndPayer() {
		return disabledBillToAndPayer;
	}
	public void setDisabledBillToAndPayer(Boolean disabledBillToAndPayer) {
		this.disabledBillToAndPayer = disabledBillToAndPayer;
	}
	
	public String getSoldToStreetHouseNumber() {
		return soldToStreetHouseNumber;
	}
	public void setSoldToStreetHouseNumber(String soldToStreetHouseNumber) {
		this.soldToStreetHouseNumber = soldToStreetHouseNumber;
	}
	public String getShipToStreetHouseNumber() {
		return shipToStreetHouseNumber;
	}
	public void setShipToStreetHouseNumber(String shipToStreetHouseNumber) {
		this.shipToStreetHouseNumber = shipToStreetHouseNumber;
	}
	public String getBillToStreetHouseNumber() {
		return billtoStreetHouseNumber;
	}
	public void setBillToStreetHouseNumber(String billtoStreetHouseNumber) {
		this.billtoStreetHouseNumber = billtoStreetHouseNumber;
	}
	public String getPayerStreetHouseNumber() {
		return payerStreetHouseNumber;
	}
	public void setPayerStreetHouseNumber(String payerStreetHouseNumber) {
		this.payerStreetHouseNumber = payerStreetHouseNumber;
	}
}
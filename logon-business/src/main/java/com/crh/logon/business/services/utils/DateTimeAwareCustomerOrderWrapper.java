package com.crh.logon.business.services.utils;

import com.crh.logon.persistence.entities.views.CustorderCreateEntity;
import com.crh.logon.persistence.entities.views.CustorderFinishedEntity;
import com.crh.logon.persistence.entities.views.OrderCreateEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @uathor AdNovum Informatik AG
 */
public class DateTimeAwareCustomerOrderWrapper implements Serializable {
	private static final long serialVersionUID = 1L;

	private OrderCreateEntity orderCreateEntity;

	private String deliveryFromDate;
	private String deliveryFromTime;

	private String deliveryToDate;
	private String deliveryToTime;

	private String unloadRealStartDate;
	private String unloadRealStartTime;

	private String unloadPlannedStartDate;
	private String unloadPlannedStartTime;

	private String compositeId;
    private Long custorderId;
    private Long sapContractId;
    private Long shiptoSapBpId;
    private Long sareaId;
    private Long custorderNumber;
    private String custorderCustrefNo;
    private Long custorderStatusCd;
    private Boolean delivnoteSinglePaperFlag;
    private Boolean delivnoteCollPaperFlag;
    private Boolean delivnoteEmailFlag;
    private Boolean delivnoteFaxFlag;
    private Boolean orderConfEmailFlag;
    private String orderConfEmailAdresses;
    private Boolean orderConfFaxFlag;
    private String orderConfFaxNumber;
    private Boolean orderConfSmsFlag;
    private String orderConfSmsNumber;
    private Date deliveryFromDat;
    private Date deliveryToDat;
    private Long deliveryTypeCd;
    private Long poolId;
    private Long commCarrierId;
    private Long billtoSapBpId;
    private Long payerSapBpId;
    private String noteOrderMgmt;
    private Long shippingpointId;
    private String noteDelivery;
    private String custWeborderNote;
    private String noteDispatching;
    private String noteTruckDriver;
    private Boolean releaseSapFlag;
    private String contactPersonTxt;
    private Long orderCreationTypeCd;
    private Boolean onHoldOrderAndLockedFlag;
    private Long custorderItemId;
    private Long sapContractItemId;
    private Long sapMaterialId;
    private String currencyCd;
    private java.math.BigDecimal rate;
    private String orderedUomCd;
    private java.math.BigDecimal orderedQuantity;
    private Long shiptoSiloId;
    private Long loadingpointId;
    private String productionPlantCd;
    private String langCd;
    private String poolName;
    private String commCarrierName;
    private String shippingpointName;
    private String billtoBpName;
    private String soldtoBpName;
    private String shiptoBpName;
    private String payerBpName;
    private String unitTxt;
    private String materialNo;
    private String materialTxt;
    private String statusTxt;
    private String loadingpointName;
    private String siloName;
    private String sapTrspModeCd;
    private String deliveryTypeTxt;
    private String productionPlantName;
    private String supplyPlantName;
    private String supplyPlantCd;

	/* required for Serialization */
	public DateTimeAwareCustomerOrderWrapper() { }

	public DateTimeAwareCustomerOrderWrapper(CustorderFinishedEntity entity, TimeZone shipToTz, TimeZone userTz) {

		this.deliveryFromDate = shipToTz.fmtAsDayMonthYear(entity.getDeliveryFromDat());
		this.deliveryFromTime = shipToTz.fmtAsHourMinuteWithOffset(
				entity.getDeliveryFromDat(), userTz);

		this.deliveryToDate = shipToTz.fmtAsDayMonthYear(entity.getDeliveryToDat());
		this.deliveryToTime = shipToTz.fmtAsHourMinuteWithOffset(
				entity.getDeliveryToDat(), userTz);
		
		 compositeId = entity.getCompositeId();
	     custorderId = entity.getCustorderId();
	     sapContractId = entity.getSapContractId();
	     shiptoSapBpId = entity.getShiptoSapBpId();
	     sareaId =  entity.getSareaId();
	     custorderNumber = entity.getCustorderNumber();
	     custorderCustrefNo  = entity.getCustorderCustrefNo();
	     custorderStatusCd =  entity.getCustorderStatusCd();
	     delivnoteSinglePaperFlag = entity.getDelivnoteSinglePaperFlag();
	     delivnoteCollPaperFlag  = entity.getDelivnoteCollPaperFlag();
	     delivnoteEmailFlag  = entity.getDelivnoteEmailFlag();
	     delivnoteFaxFlag  = entity.getDelivnoteFaxFlag();
	     orderConfEmailFlag  = entity.getOrderConfEmailFlag();
	     orderConfEmailAdresses  = entity.getOrderConfEmailAdresses();
	     orderConfFaxFlag  = entity.getOrderConfFaxFlag();
	     orderConfFaxNumber  = entity.getOrderConfFaxNumber();
	     orderConfSmsFlag  = entity.getOrderConfSmsFlag();
	     orderConfSmsNumber  = entity.getOrderConfSmsNumber();
	     deliveryFromDat  = entity.getDeliveryFromDat();
	     deliveryToDat  = entity.getDeliveryToDat();
	     deliveryTypeCd  = entity.getDeliveryTypeCd();
	     poolId  = entity.getPoolId();
	     commCarrierId  = entity.getCommCarrierId();
	     billtoSapBpId = entity.getBilltoSapBpId();
	     payerSapBpId  = entity.getPayerSapBpId();
	     noteOrderMgmt  = entity.getNoteOrderMgmt();
	     shippingpointId  = entity.getShippingpointId();
	     noteDelivery  = entity.getNoteDelivery();
	     custWeborderNote  = entity.getCustWeborderNote();
	     noteDispatching  = entity.getNoteDispatching();
	     noteTruckDriver  = entity.getNoteTruckDriver();
	     releaseSapFlag  = entity.getReleaseSapFlag();
	     contactPersonTxt  = entity.getContactPersonTxt();
	     orderCreationTypeCd  = entity.getOrderCreationTypeCd();
	     onHoldOrderAndLockedFlag  = entity.getOnHoldOrderAndLockedFlag();
	     custorderItemId =  entity.getCustorderItemId();
	     sapContractItemId  = entity.getSapContractItemId();
	     sapMaterialId =  entity.getSapMaterialId();
	     currencyCd  = entity.getCurrencyCd();
	     rate  = entity.getRate();
	     orderedUomCd  = entity.getOrderedUomCd();
	     orderedQuantity  = entity.getOrderedQuantity();
	     shiptoSiloId  = entity.getShiptoSiloId();
	     loadingpointId  = entity.getLoadingpointId();
	     productionPlantCd =  entity.getProductionPlantCd();
	     langCd  = entity.getLangCd();
	     poolName =  entity.getPoolName();
	     commCarrierName = entity.getCommCarrierName();
	     shippingpointName  = entity.getShippingpointName();
	     billtoBpName = entity.getBilltoBpName();
	     soldtoBpName = entity.getSoldtoBpName();
	     shiptoBpName  = entity.getShiptoBpName();
	     payerBpName = entity.getPayerBpName();
	     unitTxt  = entity.getUnitTxt();
	     materialNo  = entity.getMaterialNo();
	     materialTxt  = entity.getMaterialTxt();
	     statusTxt = entity.getStatusTxt();
	     loadingpointName  = entity.getLoadingpointName();
	     siloName  = entity.getSiloName();
	     sapTrspModeCd  = entity.getSapTrspModeCd();
	     deliveryTypeTxt  = entity.getDeliveryTypeTxt();
	     productionPlantName  = entity.getProductionPlantCd();
	     supplyPlantName =  entity.getSupplyPlantName();
	     supplyPlantCd = entity.getSupplyPlantCd();
		
	}
	
	public DateTimeAwareCustomerOrderWrapper(CustorderCreateEntity entity, TimeZone shipToTz, TimeZone userTz) {

		this.deliveryFromDate = shipToTz.fmtAsDayMonthYear(entity.getDeliveryFromDat());
		this.deliveryFromTime = shipToTz.fmtAsHourMinuteWithOffset(
				entity.getDeliveryFromDat(), userTz);

		this.deliveryToDate = shipToTz.fmtAsDayMonthYear(entity.getDeliveryToDat());
		this.deliveryToTime = shipToTz.fmtAsHourMinuteWithOffset(
				entity.getDeliveryToDat(), userTz);
		
		 compositeId = entity.getCompositeId();
	     custorderId = entity.getCustorderId();
	     sapContractId = entity.getSapContractId();
	     shiptoSapBpId = entity.getShiptoSapBpId();
	     sareaId =  entity.getSareaId();
	     custorderNumber = entity.getCustorderNumber();
	     custorderCustrefNo  = entity.getCustorderCustrefNo();
	     custorderStatusCd =  entity.getCustorderStatusCd();
	     delivnoteSinglePaperFlag = entity.getDelivnoteSinglePaperFlag();
	     delivnoteCollPaperFlag  = entity.getDelivnoteCollPaperFlag();
	     delivnoteEmailFlag  = entity.getDelivnoteEmailFlag();
	     delivnoteFaxFlag  = entity.getDelivnoteFaxFlag();
	     orderConfEmailFlag  = entity.getOrderConfEmailFlag();
	     orderConfEmailAdresses  = entity.getOrderConfEmailAdresses();
	     orderConfFaxFlag  = entity.getOrderConfFaxFlag();
	     orderConfFaxNumber  = entity.getOrderConfFaxNumber();
	     orderConfSmsFlag  = entity.getOrderConfSmsFlag();
	     orderConfSmsNumber  = entity.getOrderConfSmsNumber();
	     deliveryFromDat  = entity.getDeliveryFromDat();
	     deliveryToDat  = entity.getDeliveryToDat();
	     deliveryTypeCd  = entity.getDeliveryTypeCd();
	     poolId  = entity.getPoolId();
	     commCarrierId  = entity.getCommCarrierId();
	     billtoSapBpId = entity.getBilltoSapBpId();
	     payerSapBpId  = entity.getPayerSapBpId();
	     noteOrderMgmt  = entity.getNoteOrderMgmt();
	     shippingpointId  = entity.getShippingpointId();
	     noteDelivery  = entity.getNoteDelivery();
	     custWeborderNote  = entity.getCustWeborderNote();
	     noteDispatching  = entity.getNoteDispatching();
	     noteTruckDriver  = entity.getNoteTruckDriver();
	     releaseSapFlag  = entity.getReleaseSapFlag();
	     contactPersonTxt  = entity.getContactPersonTxt();
	     orderCreationTypeCd  = entity.getOrderCreationTypeCd();
	     onHoldOrderAndLockedFlag  = entity.getOnHoldOrderAndLockedFlag();
	     custorderItemId =  entity.getCustorderItemId();
	     sapContractItemId  = entity.getSapContractItemId();
	     sapMaterialId =  entity.getSapMaterialId();
	     currencyCd  = entity.getCurrencyCd();
	     rate  = entity.getRate();
	     orderedUomCd  = entity.getOrderedUomCd();
	     orderedQuantity  = entity.getOrderedQuantity();
	     shiptoSiloId  = entity.getShiptoSiloId();
	     loadingpointId  = entity.getLoadingpointId();
	     productionPlantCd =  entity.getProductionPlantCd();
	     langCd  = entity.getLangCd();
	     poolName =  entity.getPoolName();
	     commCarrierName = entity.getCommCarrierName();
	     shippingpointName  = entity.getShippingpointName();
	     billtoBpName = entity.getBilltoBpName();
	     soldtoBpName = entity.getSoldtoBpName();
	     shiptoBpName  = entity.getShiptoBpName();
	     payerBpName = entity.getPayerBpName();
	     unitTxt  = entity.getUnitTxt();
	     materialNo  = entity.getMaterialNo();
	     materialTxt  = entity.getMaterialTxt();
	     statusTxt = entity.getStatusTxt();
	     loadingpointName  = entity.getLoadingpointName();
	     siloName  = entity.getSiloName();
	     sapTrspModeCd  = entity.getSapTrspModeCd();
	     deliveryTypeTxt  = entity.getDeliveryTypeTxt();
	     productionPlantName  = entity.getProductionPlantCd();
	     supplyPlantName =  entity.getSupplyPlantName();
	     supplyPlantCd = entity.getSupplyPlantCd();
	}

	public DateTimeAwareCustomerOrderWrapper(OrderCreateEntity entity, TimeZone shipToTz, TimeZone userTz) {
		this.orderCreateEntity = entity;

		this.deliveryFromDate = shipToTz.fmtAsDayMonthYear(entity.getDeliveryFromDat());
		this.deliveryFromTime = shipToTz.fmtAsHourMinuteWithOffset(
				entity.getDeliveryFromDat(), userTz);

		this.deliveryToDate = shipToTz.fmtAsDayMonthYear(entity.getDeliveryToDat());
		this.deliveryToTime = shipToTz.fmtAsHourMinuteWithOffset(
				entity.getDeliveryToDat(), userTz);

		if (entity.getUnloadRealStart() != null) {
			this.unloadRealStartDate = shipToTz.fmtAsDayMonthYear(entity.getUnloadRealStart());
			this.unloadRealStartTime = shipToTz.fmtAsHourMinuteWithOffset(
					entity.getUnloadRealStart(), userTz);
		}

		if (entity.getUnloadPlannedStart() != null) {
			this.unloadPlannedStartDate = shipToTz.fmtAsDayMonthYear(entity.getUnloadPlannedStart());
			this.unloadPlannedStartTime = shipToTz.fmtAsHourMinuteWithOffset(
					entity.getUnloadPlannedStart(), userTz);
		}
	}

	public String getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}

	public Long getCustorderId() {
		return custorderId;
	}

	public void setCustorderId(Long custorderId) {
		this.custorderId = custorderId;
	}

	public Long getSapContractId() {
		return sapContractId;
	}

	public void setSapContractId(Long sapContractId) {
		this.sapContractId = sapContractId;
	}

	public Long getShiptoSapBpId() {
		return shiptoSapBpId;
	}

	public void setShiptoSapBpId(Long shiptoSapBpId) {
		this.shiptoSapBpId = shiptoSapBpId;
	}

	public Long getSareaId() {
		return sareaId;
	}

	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}

	public Long getCustorderNumber() {
		return custorderNumber;
	}

	public void setCustorderNumber(Long custorderNumber) {
		this.custorderNumber = custorderNumber;
	}

	public String getCustorderCustrefNo() {
		return custorderCustrefNo;
	}

	public void setCustorderCustrefNo(String custorderCustrefNo) {
		this.custorderCustrefNo = custorderCustrefNo;
	}

	public Long getCustorderStatusCd() {
		return custorderStatusCd;
	}

	public void setCustorderStatusCd(Long custorderStatusCd) {
		this.custorderStatusCd = custorderStatusCd;
	}

	public Boolean getDelivnoteSinglePaperFlag() {
		return delivnoteSinglePaperFlag;
	}

	public void setDelivnoteSinglePaperFlag(Boolean delivnoteSinglePaperFlag) {
		this.delivnoteSinglePaperFlag = delivnoteSinglePaperFlag;
	}

	public Boolean getDelivnoteCollPaperFlag() {
		return delivnoteCollPaperFlag;
	}

	public void setDelivnoteCollPaperFlag(Boolean delivnoteCollPaperFlag) {
		this.delivnoteCollPaperFlag = delivnoteCollPaperFlag;
	}

	public Boolean getDelivnoteEmailFlag() {
		return delivnoteEmailFlag;
	}

	public void setDelivnoteEmailFlag(Boolean delivnoteEmailFlag) {
		this.delivnoteEmailFlag = delivnoteEmailFlag;
	}

	public Boolean getDelivnoteFaxFlag() {
		return delivnoteFaxFlag;
	}

	public void setDelivnoteFaxFlag(Boolean delivnoteFaxFlag) {
		this.delivnoteFaxFlag = delivnoteFaxFlag;
	}

	public Boolean getOrderConfEmailFlag() {
		return orderConfEmailFlag;
	}

	public void setOrderConfEmailFlag(Boolean orderConfEmailFlag) {
		this.orderConfEmailFlag = orderConfEmailFlag;
	}

	public String getOrderConfEmailAdresses() {
		return orderConfEmailAdresses;
	}

	public void setOrderConfEmailAdresses(String orderConfEmailAdresses) {
		this.orderConfEmailAdresses = orderConfEmailAdresses;
	}

	public Boolean getOrderConfFaxFlag() {
		return orderConfFaxFlag;
	}

	public void setOrderConfFaxFlag(Boolean orderConfFaxFlag) {
		this.orderConfFaxFlag = orderConfFaxFlag;
	}

	public String getOrderConfFaxNumber() {
		return orderConfFaxNumber;
	}

	public void setOrderConfFaxNumber(String orderConfFaxNumber) {
		this.orderConfFaxNumber = orderConfFaxNumber;
	}

	public Boolean getOrderConfSmsFlag() {
		return orderConfSmsFlag;
	}

	public void setOrderConfSmsFlag(Boolean orderConfSmsFlag) {
		this.orderConfSmsFlag = orderConfSmsFlag;
	}

	public String getOrderConfSmsNumber() {
		return orderConfSmsNumber;
	}

	public void setOrderConfSmsNumber(String orderConfSmsNumber) {
		this.orderConfSmsNumber = orderConfSmsNumber;
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

	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}

	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
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

	public Long getBilltoSapBpId() {
		return billtoSapBpId;
	}

	public void setBilltoSapBpId(Long billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}

	public Long getPayerSapBpId() {
		return payerSapBpId;
	}

	public void setPayerSapBpId(Long payerSapBpId) {
		this.payerSapBpId = payerSapBpId;
	}

	public String getNoteOrderMgmt() {
		return noteOrderMgmt;
	}

	public void setNoteOrderMgmt(String noteOrderMgmt) {
		this.noteOrderMgmt = noteOrderMgmt;
	}

	public Long getShippingpointId() {
		return shippingpointId;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	public String getNoteDelivery() {
		return noteDelivery;
	}

	public void setNoteDelivery(String noteDelivery) {
		this.noteDelivery = noteDelivery;
	}

	public String getCustWeborderNote() {
		return custWeborderNote;
	}

	public void setCustWeborderNote(String custWeborderNote) {
		this.custWeborderNote = custWeborderNote;
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

	public Boolean getReleaseSapFlag() {
		return releaseSapFlag;
	}

	public void setReleaseSapFlag(Boolean releaseSapFlag) {
		this.releaseSapFlag = releaseSapFlag;
	}

	public String getContactPersonTxt() {
		return contactPersonTxt;
	}

	public void setContactPersonTxt(String contactPersonTxt) {
		this.contactPersonTxt = contactPersonTxt;
	}

	public Long getOrderCreationTypeCd() {
		return orderCreationTypeCd;
	}

	public void setOrderCreationTypeCd(Long orderCreationTypeCd) {
		this.orderCreationTypeCd = orderCreationTypeCd;
	}

	public Boolean getOnHoldOrderAndLockedFlag() {
		return onHoldOrderAndLockedFlag;
	}

	public void setOnHoldOrderAndLockedFlag(Boolean onHoldOrderAndLockedFlag) {
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
	}

	public Long getCustorderItemId() {
		return custorderItemId;
	}

	public void setCustorderItemId(Long custorderItemId) {
		this.custorderItemId = custorderItemId;
	}

	public Long getSapContractItemId() {
		return sapContractItemId;
	}

	public void setSapContractItemId(Long sapContractItemId) {
		this.sapContractItemId = sapContractItemId;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}

	public String getCurrencyCd() {
		return currencyCd;
	}

	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}

	public java.math.BigDecimal getRate() {
		return rate;
	}

	public void setRate(java.math.BigDecimal rate) {
		this.rate = rate;
	}

	public String getOrderedUomCd() {
		return orderedUomCd;
	}

	public void setOrderedUomCd(String orderedUomCd) {
		this.orderedUomCd = orderedUomCd;
	}

	public java.math.BigDecimal getOrderedQuantity() {
		return orderedQuantity;
	}

	public void setOrderedQuantity(java.math.BigDecimal orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	public Long getShiptoSiloId() {
		return shiptoSiloId;
	}

	public void setShiptoSiloId(Long shiptoSiloId) {
		this.shiptoSiloId = shiptoSiloId;
	}

	public Long getLoadingpointId() {
		return loadingpointId;
	}

	public void setLoadingpointId(Long loadingpointId) {
		this.loadingpointId = loadingpointId;
	}

	public String getProductionPlantCd() {
		return productionPlantCd;
	}

	public void setProductionPlantCd(String productionPlantCd) {
		this.productionPlantCd = productionPlantCd;
	}

	public String getLangCd() {
		return langCd;
	}

	public void setLangCd(String langCd) {
		this.langCd = langCd;
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

	public String getShippingpointName() {
		return shippingpointName;
	}

	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}

	public String getBilltoBpName() {
		return billtoBpName;
	}

	public void setBilltoBpName(String billtoBpName) {
		this.billtoBpName = billtoBpName;
	}

	public String getSoldtoBpName() {
		return soldtoBpName;
	}

	public void setSoldtoBpName(String soldtoBpName) {
		this.soldtoBpName = soldtoBpName;
	}

	public String getShiptoBpName() {
		return shiptoBpName;
	}

	public void setShiptoBpName(String shiptoBpName) {
		this.shiptoBpName = shiptoBpName;
	}

	public String getPayerBpName() {
		return payerBpName;
	}

	public void setPayerBpName(String payerBpName) {
		this.payerBpName = payerBpName;
	}

	public String getUnitTxt() {
		return unitTxt;
	}

	public void setUnitTxt(String unitTxt) {
		this.unitTxt = unitTxt;
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

	public String getLoadingpointName() {
		return loadingpointName;
	}

	public void setLoadingpointName(String loadingpointName) {
		this.loadingpointName = loadingpointName;
	}

	public String getSiloName() {
		return siloName;
	}

	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}

	public String getSapTrspModeCd() {
		return sapTrspModeCd;
	}

	public void setSapTrspModeCd(String sapTrspModeCd) {
		this.sapTrspModeCd = sapTrspModeCd;
	}

	public String getDeliveryTypeTxt() {
		return deliveryTypeTxt;
	}

	public void setDeliveryTypeTxt(String deliveryTypeTxt) {
		this.deliveryTypeTxt = deliveryTypeTxt;
	}

	public String getProductionPlantName() {
		return productionPlantName;
	}

	public void setProductionPlantName(String productionPlantName) {
		this.productionPlantName = productionPlantName;
	}

	public String getSupplyPlantName() {
		return supplyPlantName;
	}

	public void setSupplyPlantName(String supplyPlantName) {
		this.supplyPlantName = supplyPlantName;
	}

	public String getSupplyPlantCd() {
		return supplyPlantCd;
	}

	public void setSupplyPlantCd(String supplyPlantCd) {
		this.supplyPlantCd = supplyPlantCd;
	}

	public void setOrderCreateEntity(OrderCreateEntity orderCreateEntity) {
		this.orderCreateEntity = orderCreateEntity;
	}

	public void setDeliveryFromDate(String deliveryFromDate) {
		this.deliveryFromDate = deliveryFromDate;
	}

	public void setDeliveryFromTime(String deliveryFromTime) {
		this.deliveryFromTime = deliveryFromTime;
	}

	public void setDeliveryToDate(String deliveryToDate) {
		this.deliveryToDate = deliveryToDate;
	}

	public void setDeliveryToTime(String deliveryToTime) {
		this.deliveryToTime = deliveryToTime;
	}

	public void setUnloadRealStartDate(String unloadRealStartDate) {
		this.unloadRealStartDate = unloadRealStartDate;
	}

	public void setUnloadRealStartTime(String unloadRealStartTime) {
		this.unloadRealStartTime = unloadRealStartTime;
	}

	public void setUnloadPlannedStartDate(String unloadPlannedStartDate) {
		this.unloadPlannedStartDate = unloadPlannedStartDate;
	}

	public void setUnloadPlannedStartTime(String unloadPlannedStartTime) {
		this.unloadPlannedStartTime = unloadPlannedStartTime;
	}

	public Object getEntity() {
		
		if (orderCreateEntity != null) {
			return orderCreateEntity;
		}

		throw new IllegalStateException("No entity set");
	}

	public OrderCreateEntity getOrderCreateEntity() {
		return orderCreateEntity;
	}

	public String getDeliveryFromDate() {
		return deliveryFromDate;
	}

	public String getDeliveryFromTime() {
		return deliveryFromTime;
	}

	public String getDeliveryToDate() {
		return deliveryToDate;
	}

	public String getDeliveryToTime() {
		return deliveryToTime;
	}

	public String getUnloadRealStartDate() {
		return unloadRealStartDate;
	}

	public String getUnloadRealStartTime() {
		return unloadRealStartTime;
	}

	public String getUnloadPlannedStartDate() {
		return unloadPlannedStartDate;
	}

	public String getUnloadPlannedStartTime() {
		return unloadPlannedStartTime;
	}
}

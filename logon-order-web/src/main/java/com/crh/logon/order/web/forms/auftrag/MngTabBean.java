/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class MngTabBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MngTabBean.class);
	
	//Steuerung
	private boolean releaseSapFlag;
	private boolean noPdaOrRailEventsFlag;
	private boolean disabledFreeOfCharge;
	private boolean disabledNoPdaOrRailEventsFlag;

	private String orderType;
	private String distributionDest;
	private String deliveryType;
	private String shippingCond;
	private String sbbCustomerNumber;
	private String customerClassification;
	private String customerGroup;
	private String abcClass;
	private boolean sapInventoryManagedFlag;
	private boolean customerOrderBlockFlag;
	private boolean customerDeliveryBlockFlag;
	private String orderCreationType;
	private String orderValidationCode;

	private boolean custFreeOfChargeFlag;
	private boolean sapSuppressMinChargeFlag;
	private boolean sentToSap;
	
	//Lieferschein
	private String faxNumber;
	private String emailNumber;
	private String smsNumber;
	private String paperNumber;
	private String contactPersonSignature;
	private String delNotePrintType;
	private boolean bbFaxFlag;
	private boolean bbEmailFlag;
	private boolean bbSmsFlag;
	private boolean lsFaxFlag;
	private boolean lsEmailFlag;
	private boolean lsPaperFlag;

	//Bemerkungen
	private String noteOrderMgmt; 
	private String noteDispatching;
	private String noteCustWeborder;
	private String noteTruckDriver;
	private String noteDelivery;
	private String noteRailwayDisp;
	
	
	public String getOrderValidationCode() {
		return orderValidationCode;
	}
	public void setOrderValidationCode(String orderValidationCode) {
		this.orderValidationCode = orderValidationCode;
	}
	public String getOrderCreationType() {
		return orderCreationType;
	}
	public void setOrderCreationType(String orderCreationType) {
		this.orderCreationType = orderCreationType;
	}
	public boolean isSapInventoryManagedFlag() {
		return sapInventoryManagedFlag;
	}
	public void setSapInventoryManagedFlag(boolean sapInventoryManagedFlag) {
		this.sapInventoryManagedFlag = sapInventoryManagedFlag;
	}
	public boolean isCustomerOrderBlockFlag() {
		return customerOrderBlockFlag;
	}
	public void setCustomerOrderBlockFlag(boolean customerOrderBlockFlag) {
		this.customerOrderBlockFlag = customerOrderBlockFlag;
	}
	public boolean isCustomerDeliveryBlockFlag() {
		return customerDeliveryBlockFlag;
	}
	public void setCustomerDeliveryBlockFlag(boolean customerDeliveryBlockFlag) {
		this.customerDeliveryBlockFlag = customerDeliveryBlockFlag;
	}
	public String getCustomerGroup() {
		return customerGroup;
	}
	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}
	public String getAbcClass() {
		return abcClass;
	}
	public void setAbcClass(String abcClass) {
		this.abcClass = abcClass;
	}
	public String getCustomerClassification() {
		return customerClassification;
	}
	public void setCustomerClassification(String customerClassification) {
		this.customerClassification = customerClassification;
	}
	public String getSbbCustomerNumber() {
		return sbbCustomerNumber;
	}
	public void setSbbCustomerNumber(String sbbCustomerNumber) {
		this.sbbCustomerNumber = sbbCustomerNumber;
	}
	public String getShippingCond() {
		return shippingCond;
	}
	public void setShippingCond(String shippingCond) {
		this.shippingCond = shippingCond;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getDistributionDest() {
		return distributionDest;
	}
	public void setDistributionDest(String distributionDest) {
		this.distributionDest = distributionDest;
	}
	public boolean isReleaseSapFlag() {
		return releaseSapFlag;
	}
	public void setReleaseSapFlag(boolean releaseSapFlag) {
		this.releaseSapFlag = releaseSapFlag;
	}
	public boolean isNoPdaOrRailEventsFlag() {
		return noPdaOrRailEventsFlag;
	}
	public void setNoPdaOrRailEventsFlag(boolean noPdaOrRailEventsFlag) {
		this.noPdaOrRailEventsFlag = noPdaOrRailEventsFlag;
	}

	public String getDelNotePrintType() {
		return delNotePrintType;
	}
	public void setDelNotePrintType(String delNotePrintType) {
		this.delNotePrintType = delNotePrintType;
	}
	public String getContactPersonSignature() {
		return contactPersonSignature;
	}
	public void setContactPersonSignature(String contactPersonSignature) {
		this.contactPersonSignature = contactPersonSignature;
	}
	public String getNoteOrderMgmt() {
		return noteOrderMgmt;
	}
	public void setNoteOrderMgmt(String noteOrderMgmt) {
		this.noteOrderMgmt = noteOrderMgmt;
	}
	public String getNoteDispatching() {
		return noteDispatching;
	}
	public void setNoteDispatching(String noteDispatching) {
		this.noteDispatching = noteDispatching;
	}
	public String getNoteCustWeborder() {
		return noteCustWeborder;
	}
	public void setNoteCustWeborder(String noteCustWeborder) {
		this.noteCustWeborder = noteCustWeborder;
	}
	public String getNoteTruckDriver() {
		return noteTruckDriver;
	}
	public void setNoteTruckDriver(String noteTruckDriver) {
		this.noteTruckDriver = noteTruckDriver;
	}
	public String getNoteDelivery() {
		return noteDelivery;
	}
	public void setNoteDelivery(String noteDelivery) {
		this.noteDelivery = noteDelivery;
	}
	public String getNoteRailwayDisp() {
		return noteRailwayDisp;
	}
	public void setNoteRailwayDisp(String noteRailwayDisp) {
		this.noteRailwayDisp = noteRailwayDisp;
	}
	public String getEmailNumber() {
		return emailNumber;
	}
	public void setEmailNumber(String emailNumber) {
		this.emailNumber = emailNumber;
	}
	public String getSmsNumber() {
		return smsNumber;
	}
	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}
	public String getPaperNumber() {
		return paperNumber;
	}
	public void setPaperNumber(String paperNumber) {
		this.paperNumber = paperNumber;
	}
	public String getFaxNumber() {
		return faxNumber;
	}
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	public boolean isLsPaperFlag() {
		return lsPaperFlag;
	}
	public void setLsPaperFlag(boolean lsPaperFlag) {
		this.lsPaperFlag = lsPaperFlag;
	}
	public boolean isBbFaxFlag() {
		return bbFaxFlag;
	}
	public void setBbFaxFlag(boolean bbFaxFlag) {
		this.bbFaxFlag = bbFaxFlag;
	}
	public boolean isBbEmailFlag() {
		return bbEmailFlag;
	}
	public void setBbEmailFlag(boolean bbEmailFlag) {
		this.bbEmailFlag = bbEmailFlag;
	}
	public boolean isBbSmsFlag() {
		return bbSmsFlag;
	}
	public void setBbSmsFlag(boolean bbSmsFlag) {
		this.bbSmsFlag = bbSmsFlag;
	}
	public boolean isLsFaxFlag() {
		return lsFaxFlag;
	}
	public void setLsFaxFlag(boolean lsFaxFlag) {
		this.lsFaxFlag = lsFaxFlag;
	}
	public boolean isLsEmailFlag() {
		return lsEmailFlag;
	}
	public void setLsEmailFlag(boolean lsEmailFlag) {
		this.lsEmailFlag = lsEmailFlag;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public boolean isCustFreeOfChargeFlag() {
		return custFreeOfChargeFlag;
	}
	public void setCustFreeOfChargeFlag(boolean custFreeOfChargeFlag) {
		this.custFreeOfChargeFlag = custFreeOfChargeFlag;
	}
	public boolean isSentToSap() {
		return sentToSap;
	}
	public void setSentToSap(boolean sentToSap) {
		this.sentToSap = sentToSap;
	}
	public boolean isSapSuppressMinChargeFlag() {
		return sapSuppressMinChargeFlag;
	}
	public void setSapSuppressMinChargeFlag(boolean sapSuppressMinChargeFlag) {
		this.sapSuppressMinChargeFlag = sapSuppressMinChargeFlag;
	}
	
	
	public boolean isDisabledFreeOfCharge() {
		return disabledFreeOfCharge;
	}
	public void setDisabledFreeOfCharge(boolean disabledFreeOfCharge) {
		this.disabledFreeOfCharge = disabledFreeOfCharge;
	}
	
	public boolean isDisabledNoPdaOrRailEventsFlag() {
		return disabledNoPdaOrRailEventsFlag;
	}
	public void setDisabledNoPdaOrRailEventsFlag(
			boolean disabledNoPdaOrRailEventsFlag) {
		this.disabledNoPdaOrRailEventsFlag = disabledNoPdaOrRailEventsFlag;
	}

}


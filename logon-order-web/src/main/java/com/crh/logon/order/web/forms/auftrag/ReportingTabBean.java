/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class ReportingTabBean implements Serializable{


	private static final long serialVersionUID = 1L;

	//list of date from tlgnc_order_item_final
	private List OrderItemFBeanList;
	private int selOrderItemIndex = 0; //first one
	
	private boolean noPdaDeliveryFlag;
	private boolean truckReroutingFlag;
	private boolean shortTermOrderFlag;

	
	private String reactionTimeOrderMng;
	private String reactionTimePoolMng;
	private String reactionTimeDispatcher;
	private String reactionTimeTruckDriver;
	private String reactionDelivTimeWindow;
	private String loadingTime;
	private String actualDrivingTime;
	private String unloadingTime;
	private String cycleTimeShipPoint;
	private String cycleTimeShipto;
		
	//Service level
	private String endLoad;
	private String deliveryTo;
	private String enterSt;
	private boolean onTimeFlag;
	private Boolean relevantServiceLevelFlag;
	private String noteCrh;
	private String noteCarrier;
	
	//Regionen
	private String salesDistrict;
	private String salesOffice;
	private String salesGroup;
	private String zone;
	private String region;
	private String language;
	
	//Notes
	private String noteDisposition;
	private String noteDriver;
	private String noteShipping;
	

	public Boolean getRelevantServiceLevelFlag() {
		return relevantServiceLevelFlag;
	}
	public void setRelevantServiceLevelFlag(Boolean relevantServiceLevelFlag) {
		this.relevantServiceLevelFlag = relevantServiceLevelFlag;
	}
	public boolean isNoPdaDeliveryFlag() {
		return noPdaDeliveryFlag;
	}
	public void setNoPdaDeliveryFlag(boolean noPdaDeliveryFlag) {
		this.noPdaDeliveryFlag = noPdaDeliveryFlag;
	}
	public int getSelOrderItemIndex() {
		return selOrderItemIndex;
	}
	public void setSelOrderItemIndex(int selOrderItemIndex) {
		this.selOrderItemIndex = selOrderItemIndex;
	}
	public List getOrderItemFBeanList() {
		return OrderItemFBeanList;
	}
	public void setOrderItemFBeanList(List orderItemFBeanList) {
		OrderItemFBeanList = orderItemFBeanList;
	}
	public String getNoteDisposition() {
		return noteDisposition;
	}
	public void setNoteDisposition(String noteDisposition) {
		this.noteDisposition = noteDisposition;
	}
	public String getNoteDriver() {
		return noteDriver;
	}
	public void setNoteDriver(String noteDriver) {
		this.noteDriver = noteDriver;
	}
	public String getNoteShipping() {
		return noteShipping;
	}
	public void setNoteShipping(String noteShipping) {
		this.noteShipping = noteShipping;
	}
	public String getSalesDistrict() {
		return salesDistrict;
	}
	public void setSalesDistrict(String salesDistrict) {
		this.salesDistrict = salesDistrict;
	}
	public String getSalesOffice() {
		return salesOffice;
	}
	public void setSalesOffice(String salesOffice) {
		this.salesOffice = salesOffice;
	}
	public String getSalesGroup() {
		return salesGroup;
	}
	public void setSalesGroup(String salesGroup) {
		this.salesGroup = salesGroup;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getNoteCrh() {
		return noteCrh;
	}
	public void setNoteCrh(String noteCrh) {
		this.noteCrh = noteCrh;
	}
	public String getNoteCarrier() {
		return noteCarrier;
	}
	public void setNoteCarrier(String noteCarrier) {
		this.noteCarrier = noteCarrier;
	}
	public String getReactionTimeOrderMng() {
		return reactionTimeOrderMng;
	}
	public void setReactionTimeOrderMng(String reactionTimeOrderMng) {
		this.reactionTimeOrderMng = reactionTimeOrderMng;
	}

	public String getReactionTimePoolMng() {
		return reactionTimePoolMng;
	}
	public void setReactionTimePoolMng(String reactionTimePoolMng) {
		this.reactionTimePoolMng = reactionTimePoolMng;
	}
	public String getReactionTimeDispatcher() {
		return reactionTimeDispatcher;
	}
	public void setReactionTimeDispatcher(String reactionTimeDispatcher) {
		this.reactionTimeDispatcher = reactionTimeDispatcher;
	}
	public String getReactionTimeTruckDriver() {
		return reactionTimeTruckDriver;
	}
	public void setReactionTimeTruckDriver(String reactionTimeTruckDriver) {
		this.reactionTimeTruckDriver = reactionTimeTruckDriver;
	}
	public String getReactionDelivTimeWindow() {
		return reactionDelivTimeWindow;
	}
	public void setReactionDelivTimeWindow(String reactionDelivTimeWindow) {
		this.reactionDelivTimeWindow = reactionDelivTimeWindow;
	}
	public String getLoadingTime() {
		return loadingTime;
	}
	public void setLoadingTime(String loadingTime) {
		this.loadingTime = loadingTime;
	}
	public String getActualDrivingTime() {
		return actualDrivingTime;
	}
	public void setActualDrivingTime(String actualDrivingTime) {
		this.actualDrivingTime = actualDrivingTime;
	}
	public String getUnloadingTime() {
		return unloadingTime;
	}
	public void setUnloadingTime(String unloadingTime) {
		this.unloadingTime = unloadingTime;
	}
	public String getCycleTimeShipPoint() {
		return cycleTimeShipPoint;
	}
	public void setCycleTimeShipPoint(String cycleTimeShipPoint) {
		this.cycleTimeShipPoint = cycleTimeShipPoint;
	}
	public String getCycleTimeShipto() {
		return cycleTimeShipto;
	}
	public void setCycleTimeShipto(String cycleTimeShipto) {
		this.cycleTimeShipto = cycleTimeShipto;
	}
	public boolean isTruckReroutingFlag() {
		return truckReroutingFlag;
	}
	public void setTruckReroutingFlag(boolean truckReroutingFlag) {
		this.truckReroutingFlag = truckReroutingFlag;
	}
	public boolean isShortTermOrderFlag() {
		return shortTermOrderFlag;
	}
	public void setShortTermOrderFlag(boolean shortTermOrderFlag) {
		this.shortTermOrderFlag = shortTermOrderFlag;
	}
	/**
	 * @return the endLoad
	 */
	public String getEndLoad() {
		return endLoad;
	}
	/**
	 * @param endLoad the endLoad to set
	 */
	public void setEndLoad(String endLoad) {
		this.endLoad = endLoad;
	}
	/**
	 * @return the deliveryTo
	 */
	public String getDeliveryTo() {
		return deliveryTo;
	}
	/**
	 * @param deliveryTo the deliveryTo to set
	 */
	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
	/**
	 * @return the onTimeFlag
	 */
	public boolean isOnTimeFlag() {
		return onTimeFlag;
	}
	/**
	 * @param onTimeFlag the onTimeFlag to set
	 */
	public void setOnTimeFlag(boolean onTimeFlag) {
		this.onTimeFlag = onTimeFlag;
	}
	/**
	 * @return the enterSt
	 */
	public String getEnterSt() {
		return enterSt;
	}
	/**
	 * @param enterSt the enterSt to set
	 */
	public void setEnterSt(String enterSt) {
		this.enterSt = enterSt;
	}

}


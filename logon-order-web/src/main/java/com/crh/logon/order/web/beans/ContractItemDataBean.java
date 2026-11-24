/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.beans;

import java.util.List;




/**
 * @author AdNovum Informatik AG
 */
public class ContractItemDataBean {

	private boolean dataAvailable;
	private boolean hasGroupLeader;
	private String contractItemNo;
	private String materialTxt;
	private String uomTxt;
	private String sumDeliveries;
	private String sumNonDeliveries;
	private String deliveries;
	private String nonDeliveries;
	private String conversionFailures;

	private String targetQuantity;
	private String totalSentQuantity;
	
	private String sumItemQuantityOrdered;
	private String sumItemQuantityLoaded;
	private String sumItemQuantityDelivered;
	
	private String groupLeaderUomTxt;

	private boolean contractQuantityRestricted;
	
	private List<ContractItemOrderItemBean> contractItemOrderItemList; 
	
	public boolean isDataAvailable() {
		return dataAvailable;
	}

	public void setDataAvailable(boolean dataAvailable) {
		this.dataAvailable = dataAvailable;
	}

	public String getContractItemNo() {
		return contractItemNo;
	}

	public void setContractItemNo(String contractItemNo) {
		this.contractItemNo = contractItemNo;
	}

	public String getMaterialTxt() {
		return materialTxt;
	}

	public void setMaterialTxt(String materialTxt) {
		this.materialTxt = materialTxt;
	}

	public String getUomTxt() {
		return uomTxt;
	}

	public void setUomTxt(String uomTxt) {
		this.uomTxt = uomTxt;
	}

	public String getSumDeliveries() {
		return sumDeliveries;
	}

	public void setSumDeliveries(String sumDeliveries) {
		this.sumDeliveries = sumDeliveries;
	}

	public String getSumNonDeliveries() {
		return sumNonDeliveries;
	}

	public void setSumNonDeliveries(String sumNonDeliveries) {
		this.sumNonDeliveries = sumNonDeliveries;
	}

	public String getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(String deliveries) {
		this.deliveries = deliveries;
	}

	public String getNonDeliveries() {
		return nonDeliveries;
	}

	public void setNonDeliveries(String nonDeliveries) {
		this.nonDeliveries = nonDeliveries;
	}

	public String getConversionFailures() {
		return conversionFailures;
	}

	public void setConversionFailures(String conversionFailures) {
		this.conversionFailures = conversionFailures;
	}

	public boolean isHasGroupLeader() {
		return hasGroupLeader;
	}

	public void setHasGroupLeader(boolean hasGroupLeader) {
		this.hasGroupLeader = hasGroupLeader;
	}	
	
	public String getTargetQuantity() {
		return targetQuantity;
	}

	public void setTargetQuantity(String targetQuantity) {
		this.targetQuantity = targetQuantity;
	}

	public String getTotalSentQuantity() {
		return totalSentQuantity;
	}

	public void setTotalSentQuantity(String totalSentQuantity) {
		this.totalSentQuantity = totalSentQuantity;
	}

	public String getSumItemQuantityOrdered() {
		return sumItemQuantityOrdered;
	}

	public void setSumItemQuantityOrdered(String sumItemQuantityOrdered) {
		this.sumItemQuantityOrdered = sumItemQuantityOrdered;
	}

	public String getSumItemQuantityLoaded() {
		return sumItemQuantityLoaded;
	}

	public void setSumItemQuantityLoaded(String sumItemQuantityLoaded) {
		this.sumItemQuantityLoaded = sumItemQuantityLoaded;
	}

	public String getSumItemQuantityDelivered() {
		return sumItemQuantityDelivered;
	}

	public void setSumItemQuantityDelivered(String sumItemQuantityDelivered) {
		this.sumItemQuantityDelivered = sumItemQuantityDelivered;
	}
	
	public String getGroupLeaderUomTxt() {
		return groupLeaderUomTxt;
	}
	
	public void setGroupLeaderUomTxt(String groupLeaderUomTxt) {
		this.groupLeaderUomTxt = groupLeaderUomTxt;
	}

	public boolean isContractQuantityRestricted() {
		return contractQuantityRestricted;
	}
	
	public void setContractQuantityRestricted(boolean contractQuantityRestricted) {
		this.contractQuantityRestricted = contractQuantityRestricted;
	}
	
	public List<ContractItemOrderItemBean> getContractItemOrderItemList() {
		return contractItemOrderItemList;
	}
	
	public void setContractItemOrderItemList(
			List<ContractItemOrderItemBean> contractItemOrderItemList) {
		this.contractItemOrderItemList = contractItemOrderItemList;
	}
	
	public String toString() {
		String str = "";
		str += "][dataAvailable=" + dataAvailable;
		
		if (dataAvailable) {
			str += "][hasGroupLeader=" + hasGroupLeader;
			str += "][contractItemNo=" + contractItemNo;
			str += "][materialTxt=" + materialTxt;
			str += "][uomTxt=" + uomTxt;
			str += "][sumDeliveries=" + sumDeliveries;
			str += "][sumNonDeliveries=" + sumNonDeliveries;
			// str += "][deliveries=" + deliveries;
			// str += "][nonDeliveries=" + nonDeliveries;
			// str += "][conversionFailures=" + conversionFailures;
			str += "][targetQuantity=" + targetQuantity;
			str += "][totalSentQuantity=" + totalSentQuantity;
			str += "][sumItemQuantityOrdered=" + sumItemQuantityOrdered;
			str += "][sumItemQuantityLoaded=" + sumItemQuantityLoaded;
			str += "][sumItemQuantityDelivered=" + sumItemQuantityDelivered;
			str += "][groupLeaderUomTxt=" + groupLeaderUomTxt;
			str += "][contractQuantityRestricted=" + contractQuantityRestricted;
			str += "][contractItemOrderItemList=" + contractItemOrderItemList;
		}
		
		return str;
	}

}

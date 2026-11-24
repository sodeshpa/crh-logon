/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.saplogisticreport;

import java.io.Serializable;
import java.math.BigInteger;

public class SapLogisticReportItemDTO implements Serializable {

	private static final long serialVersionUID = -2876477594535170174L;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LITM_1 
	 */
	private BigInteger orderItemNumber;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LFLAG_2
	 */
	private int maintainedDispocardIndicator;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LFLAG_3
	 */
	private BigInteger deviationInOrderedQty;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LFLAG_4
	 */
	private BigInteger siloVerificationTypeId;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LFLA_10  
	 */
	private int loadingWithLdsInd;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LLPT_1 
	 */
	private String loadingPointId;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LLPT_2 
	 */
	private String loadingPointName;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LPLT_1 
	 */
	private String productionPlantId;

	/**
	 * WS field to SAP: LogisticReportCreation / item / LCON_1  
	 */
	private String containerNumber;

	/**
	 * WS field to SAP: LogisticReportCreation / item / DUMMY3 
	 */
	private String dummyString;

	/**
	 * WS field to SAP: LogisticReportCreation / item / DUMMY6 
	 */
	private int dummyInteger;

	/**
	 * WS field to SAP: LogisticReportCreation / item / DUMMY9 
	 */
	private String dummyString2;

	/**
	 * @return the productionPlantId
	 */
	public String getProductionPlantId() {
		return productionPlantId;
	}

	/**
	 * @param productionPlantId the productionPlantId to set
	 */
	public void setProductionPlantId(String productionPlantId) {
		this.productionPlantId = productionPlantId;
	}

	/**
	 * @return the loadingPointId
	 */
	public String getLoadingPointId() {
		return loadingPointId;
	}

	/**
	 * @param loadingPointId the loadingPointId to set
	 */
	public void setLoadingPointId(String loadingPointId) {
		this.loadingPointId = loadingPointId;
	}

	/**
	 * @return the loadingPointName
	 */
	public String getLoadingPointName() {
		return loadingPointName;
	}

	/**
	 * @param loadingPointName the loadingPointName to set
	 */
	public void setLoadingPointName(String loadingPointName) {
		this.loadingPointName = loadingPointName;
	}

	/**
	 * @return the containerNumber
	 */
	public String getContainerNumber() {
		return containerNumber;
	}

	/**
	 * @param containerNumber the containerNumber to set
	 */
	public void setContainerNumber(String containerNumber) {
		this.containerNumber = containerNumber;
	}

	/**
	 * @return the orderItemNumber
	 */
	public BigInteger getOrderItemNumber() {
		return orderItemNumber;
	}

	/**
	 * @param orderItemNumber the orderItemNumber to set
	 */
	public void setOrderItemNumber(BigInteger orderItemNumber) {
		this.orderItemNumber = orderItemNumber;
	}

	/**
	 * @return the maintainedDispocardIndicator
	 */
	public int getMaintainedDispocardIndicator() {
		return maintainedDispocardIndicator;
	}

	/**
	 * @param maintainedDispocardIndicator the maintainedDispocardIndicator to set
	 */
	public void setMaintainedDispocardIndicator(int maintainedDispocardIndicator) {
		this.maintainedDispocardIndicator = maintainedDispocardIndicator;
	}

	/**
	 * @return the deviationInOrderedQty
	 */
	public BigInteger getDeviationInOrderedQty() {
		return deviationInOrderedQty;
	}

	/**
	 * @param deviationInOrderedQty the deviationInOrderedQty to set
	 */
	public void setDeviationInOrderedQty(BigInteger deviationInOrderedQty) {
		this.deviationInOrderedQty = deviationInOrderedQty;
	}

	/**
	 * @return the siloVerificationTypeId
	 */
	public BigInteger getSiloVerificationTypeId() {
		return siloVerificationTypeId;
	}

	/**
	 * @param siloVerificationTypeId the siloVerificationTypeId to set
	 */
	public void setSiloVerificationTypeId(BigInteger siloVerificationTypeId) {
		this.siloVerificationTypeId = siloVerificationTypeId;
	}

	/**
	 * @return the loadingWithLdsInd
	 */
	public int getLoadingWithLdsInd() {
		return loadingWithLdsInd;
	}

	/**
	 * @param loadingWithLdsInd the loadingWithLdsInd to set
	 */
	public void setLoadingWithLdsInd(int loadingWithLdsInd) {
		this.loadingWithLdsInd = loadingWithLdsInd;
	}

	/**
	 * @return the dummyString
	 */
	public String getDummyString() {
		return dummyString;
	}

	/**
	 * @param dummyString the dummyString to set
	 */
	public void setDummyString(String dummyString) {
		this.dummyString = dummyString;
	}

	/**
	 * @return the dummyInteger
	 */
	public int getDummyInteger() {
		return dummyInteger;
	}

	/**
	 * @param dummyInteger the dummyInteger to set
	 */
	public void setDummyInteger(int dummyInteger) {
		this.dummyInteger = dummyInteger;
	}

	/**
	 * @return the dummyString2
	 */
	public String getDummyString2() {
		return dummyString2;
	}

	/**
	 * @param dummyString2 the dummyString2 to set
	 */
	public void setDummyString2(String dummyString2) {
		this.dummyString2 = dummyString2;
	}

}

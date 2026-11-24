/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class OrderHeaderData implements Serializable {
	private static final long serialVersionUID = -5990039225512692738L;
	
	private String label_customerRefContractNumber;
	
	private boolean transportReasonIsStpo;
	
	private boolean showDeliveryWindow;
	private boolean showDistance;
	private boolean showTrailer;
	private boolean showSealsNumbers;
	private boolean showSiloNumber;
	private boolean showCarrierCrm;
	private boolean showPlantCycle;
	
	private GeneralOrderData generalOrderData;
	private GeneralLoadData generalLoadData;

	private String endLoadDate;
	private String distance;
	private String deliveryWindow;
	private String customerRefContractNumber;
	private String carrierCrm;
	private String sealsNumbers;
	
	private String deliveryTime;
	private String incoterm;
	private String deliveryType;
	
	public boolean renderDeliveryType() {
		return StringUtils.isNotEmpty(deliveryType);
	}
	
	public boolean renderTruck() {
		return StringUtils.isNotEmpty(generalLoadData.getTruckPlateNumber());
	}
	
	public boolean renderTrailer() {
		return StringUtils.isNotEmpty(generalLoadData.getTrailerPlateNumber()) && showTrailer;
	}
	
	public boolean renderDistance() {
		return StringUtils.isNotEmpty(distance) && showDistance;
	}
	
	public boolean renderAdditionalVehicleData() {
		return  renderBimodalWagonNumber() || renderContainerNumbers();
	}
	
	public boolean renderBimodalWagonNumber() {
		return StringUtils.isNotEmpty(generalOrderData.getBimodalWagonNumber());
	}
	
	public boolean renderContainerNumbers() {
		return StringUtils.isNotEmpty(generalOrderData.getContainerNumbers());
	}
	
	public boolean renderDeliveryWindow() {
		return StringUtils.isNotEmpty(deliveryWindow) && showDeliveryWindow ;
	}
	
	public boolean renderCustomerRefContractNumber() {
		return StringUtils.isNotEmpty(customerRefContractNumber);
	}
	
	public boolean renderSealsNumbers() {
		return StringUtils.isNotEmpty(sealsNumbers) && showSealsNumbers ;
	}
	
	public boolean renderCarrierCrm() {
		return StringUtils.isNotEmpty(carrierCrm) && showCarrierCrm ;
	}
	
	public boolean renderSiloNumber() {
		return StringUtils.isNotEmpty(generalLoadData.getDeliverySilo()) && showSiloNumber ;
	}
	
	public boolean renderPlantEntance() {
		return StringUtils.isNotEmpty(generalLoadData.getPlantEntrance()) && showPlantCycle ;
	}
	
	public boolean renderPlantExit() {
		return StringUtils.isNotEmpty(generalLoadData.getPlantExit()) && showPlantCycle ;
	}
	
	public boolean renderEndLoadDate() {
		return StringUtils.isNotEmpty(endLoadDate);
	}
	
	public boolean renderDeliveryTime() {
		return StringUtils.isNotEmpty(deliveryTime);
	}
	
	public boolean renderIncoterm() {
		return StringUtils.isNotEmpty(incoterm);
	}
	
	
	public GeneralOrderData getGeneralOrderData() {
		return generalOrderData;
	}
	public void setGeneralOrderData(GeneralOrderData generalOrderData) {
		this.generalOrderData = generalOrderData;
	}
	public GeneralLoadData getGeneralLoadData() {
		return generalLoadData;
	}
	public void setGeneralLoadData(GeneralLoadData generalLoadData) {
		this.generalLoadData = generalLoadData;
	}
	public boolean isShowDeliveryWindow() {
		return showDeliveryWindow;
	}
	public void setShowDeliveryWindow(boolean showDeliveryWindow) {
		this.showDeliveryWindow = showDeliveryWindow;
	}
	public boolean isShowDistance() {
		return showDistance;
	}
	public void setShowDistance(boolean showDistance) {
		this.showDistance = showDistance;
	}
	public boolean isShowTrailer() {
		return showTrailer;
	}
	public void setShowTrailer(boolean showTrailer) {
		this.showTrailer = showTrailer;
	}
	public boolean isShowSealsNumbers() {
		return showSealsNumbers;
	}
	public void setShowSealsNumbers(boolean showSealsNumbers) {
		this.showSealsNumbers = showSealsNumbers;
	}
	public boolean isShowSiloNumber() {
		return showSiloNumber;
	}
	public void setShowSiloNumber(boolean showSiloNumber) {
		this.showSiloNumber = showSiloNumber;
	}
	public boolean isShowCarrierCrm() {
		return showCarrierCrm;
	}
	public void setShowCarrierCrm(boolean showCarrierCrm) {
		this.showCarrierCrm = showCarrierCrm;
	}
	public boolean isShowPlantCycle() {
		return showPlantCycle;
	}
	public void setShowPlantCycle(boolean showPlantCycle) {
		this.showPlantCycle = showPlantCycle;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDeliveryWindow() {
		return deliveryWindow;
	}
	public void setDeliveryWindow(String deliveryWindow) {
		this.deliveryWindow = deliveryWindow;
	}
	public String getCustomerRefContractNumber() {
		return customerRefContractNumber;
	}
	public void setCustomerRefContractNumber(String customerRefContractNumber) {
		this.customerRefContractNumber = customerRefContractNumber;
	}
	public String getCarrierCrm() {
		return carrierCrm;
	}
	public void setCarrierCrm(String carrierCrm) {
		this.carrierCrm = carrierCrm;
	}
	public String getSealsNumbers() {
		return sealsNumbers;
	}
	public void setSealsNumbers(String sealsNumbers) {
		this.sealsNumbers = sealsNumbers;
	}
	public String getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public String getIncoterm() {
		return incoterm;
	}
	public void setIncoterm(String incoterm) {
		this.incoterm = incoterm;
	}
	public String getEndLoadDate() {
		return endLoadDate;
	}
	public void setEndLoadDate(String endLoadDate) {
		this.endLoadDate = endLoadDate;
	}
	public String getLabel_customerRefContractNumber() {
		return label_customerRefContractNumber;
	}
	public void setLabel_customerRefContractNumber(String label_customerRefContractNumber) {
		this.label_customerRefContractNumber = label_customerRefContractNumber;
	}
	public boolean isTransportReasonIsStpo() {
		return transportReasonIsStpo;
	}
	public void setTransportReasonIsStpo(boolean transportReasonIsStpo) {
		this.transportReasonIsStpo = transportReasonIsStpo;
	}

	/**
	 * @return the deliveryType
	 */
	public String getDeliveryType() {
		return deliveryType;
	}

	/**
	 * @param deliveryType the deliveryType to set
	 */
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

}

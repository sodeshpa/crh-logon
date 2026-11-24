/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.deliverydocument;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class SalesDocumentItemDTO implements Serializable {

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / POSEX
	 */
	private String itemNumber;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / MENGE
	 */
	private String deliveredQtyInSalesUom;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / MENEE
	 */
	private String salesUom;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / PREIS
	 */
	private String netPrice;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / NTGEW
	 */
	private String deliveredQtyInTons;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / GEWEI
	 */
	private String weightUnit;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / WERKS
	 */
	private String plant;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / LGORT
	 */
	private String storageLocation;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / VSTEL
	 */
	private String shippingPointReceivingPoint;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / order_item_additional_data (ZE1EDP1) / ZRETURN
	 */
	private String returnIndicator;
	public static final String RETURN_INDICATOR_TRUE = "X";
	public static final String RETURN_INDICATOR_FALSE = "";

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / order_item_additional_data (ZE1EDP1) / ZFREECHR
	 */
	private String freeOfChargeIndicator;
	public static final String FREE_OF_CHARGE_INDICATOR_TRUE = "X";
	public static final String FREE_OF_CHARGE_INDICATOR_FALSE = "";

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / order_item_transport_data (ZE1EDP2) / ZPTRATY
	 */
	private String meansOfTransportType;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / document_item_reference_data (E1EDP02) / BELNR
	 */
	private String contract;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / document_item_reference_data (E1EDP02) / BELNRP
	 */
	private String itemFromContract;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / document_item_reference_data (E1EDP02) / REFLIFEX
	 */
	private String originalDocumentForReturns;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / document_item_reference_data (E1EDP02) / REFLIFEXPOS
	 */
	private String originalItem;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / document_item_reference_data (E1EDP02) / POSNR
	 */
	private String sapDeliveryItem;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / document_item_object_identification (E1EDP19) / MATNR
	 */
	private String materialNumber;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_item_general_data (E1EDP01) / document_item_object_identification (E1EDP19) / ARKTX
	 */
	private String materialDescription;

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getDeliveredQtyInSalesUom() {
		return deliveredQtyInSalesUom;
	}

	public void setDeliveredQtyInSalesUom(String deliveredQtyInSalesUom) {
		this.deliveredQtyInSalesUom = deliveredQtyInSalesUom;
	}

	public String getSalesUom() {
		return salesUom;
	}

	public void setSalesUom(String salesUom) {
		this.salesUom = salesUom;
	}

	public String getDeliveredQtyInTons() {
		return deliveredQtyInTons;
	}

	public void setDeliveredQtyInTons(String deliveredQtyInTons) {
		this.deliveredQtyInTons = deliveredQtyInTons;
	}

	public String getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getShippingPointReceivingPoint() {
		return shippingPointReceivingPoint;
	}

	public void setShippingPointReceivingPoint(String shippingPointReceivingPoint) {
		this.shippingPointReceivingPoint = shippingPointReceivingPoint;
	}

	public String getReturnIndicator() {
		return returnIndicator;
	}

	public void setReturnIndicator(String returnIndicator) {
		this.returnIndicator = returnIndicator;
	}

	public String getFreeOfChargeIndicator() {
		return freeOfChargeIndicator;
	}

	public void setFreeOfChargeIndicator(String freeOfChargeIndicator) {
		this.freeOfChargeIndicator = freeOfChargeIndicator;
	}

	public String getMeansOfTransportType() {
		return meansOfTransportType;
	}

	public void setMeansOfTransportType(String meansOfTransportType) {
		this.meansOfTransportType = meansOfTransportType;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getItemFromContract() {
		return itemFromContract;
	}

	public void setItemFromContract(String itemFromContract) {
		this.itemFromContract = itemFromContract;
	}

	public String getOriginalDocumentForReturns() {
		return originalDocumentForReturns;
	}

	public void setOriginalDocumentForReturns(String originalDocumentForReturns) {
		this.originalDocumentForReturns = originalDocumentForReturns;
	}

	public String getOriginalItem() {
		return originalItem;
	}

	public void setOriginalItem(String originalItem) {
		this.originalItem = originalItem;
	}

	public String getSapDeliveryItem() {
		return sapDeliveryItem;
	}

	public void setSapDeliveryItem(String sapDeliveryItem) {
		this.sapDeliveryItem = sapDeliveryItem;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}

	public String getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(String netPrice) {
		this.netPrice = netPrice;
	}

}
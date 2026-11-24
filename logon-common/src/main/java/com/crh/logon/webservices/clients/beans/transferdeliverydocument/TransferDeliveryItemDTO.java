/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.transferdeliverydocument;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class TransferDeliveryItemDTO implements Serializable {

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / POSNR
	 */
	private String itemNumberOfTheDocument;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / POSEX
	 */
	private String itemNumberOfSTPO;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / MATNR
	 */
	private String materialNumber;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / ARKTX
	 */
	private String shortTextForSalesOrderItem;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / WERKS
	 */
	private String plant;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / LGORT
	 */
	private String storageLocation;
	
	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / CHARG
	 */
	private String productionPlant;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / LFIMG
	 */
	private String deliveredQtyInSalesUom;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / MEINS
	 */
	private String salesUom;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / NTGEW
	 */
	private String deliveredQtyInTons;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / GEWEI
	 */
	private String weightUnit;

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / item_general_data (E1EDL24) / item_additional_data (ZE1EDL24) / ZTRATY
	 */
	private String meansOfTransportType;

	public String getItemNumberOfTheDocument() {
		return itemNumberOfTheDocument;
	}

	public void setItemNumberOfTheDocument(String itemNumberOfTheDocument) {
		this.itemNumberOfTheDocument = itemNumberOfTheDocument;
	}

	public String getItemNumberOfSTPO() {
		return itemNumberOfSTPO;
	}

	public void setItemNumberOfSTPO(String itemNumberOfSTPO) {
		this.itemNumberOfSTPO = itemNumberOfSTPO;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getShortTextForSalesOrderItem() {
		return shortTextForSalesOrderItem;
	}

	public void setShortTextForSalesOrderItem(String shortTextForSalesOrderItem) {
		this.shortTextForSalesOrderItem = shortTextForSalesOrderItem;
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
	
	public String getProductionPlant() {
		return productionPlant;
	}

	public void setProductionPlant(String productionPlant) {
		this.productionPlant = productionPlant;
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

	public String getMeansOfTransportType() {
		return meansOfTransportType;
	}

	public void setMeansOfTransportType(String meansOfTransportType) {
		this.meansOfTransportType = meansOfTransportType;
	}
}

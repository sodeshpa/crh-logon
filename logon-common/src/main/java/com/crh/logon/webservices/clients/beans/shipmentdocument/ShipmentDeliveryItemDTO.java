/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.shipmentdocument;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class ShipmentDeliveryItemDTO implements Serializable {

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / POSNR
	 */
	private String itemNumberOfTheSDDocument;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / MATNR
	 */
	private String materialNumber;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / WERKS
	 */
	private String plant;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / LGORT
	 */
	private String storageLocation;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / VRKME
	 */
	private String salesUom;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / GEWEI
	 */
	private String weightUnit;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / ORMNG
	 */
	private String deliveredQtyInSalesUom;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / VTWEG
	 */
	private String distributionChannel;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24) / SPART
	 */
	private String division;
	
	public String getItemNumberOfTheSDDocument() {
		return itemNumberOfTheSDDocument;
	}

	public void setItemNumberOfTheSDDocument(String itemNumberOfTheSDDocument) {
		this.itemNumberOfTheSDDocument = itemNumberOfTheSDDocument;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
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

	public String getSalesUom() {
		return salesUom;
	}

	public void setSalesUom(String salesUom) {
		this.salesUom = salesUom;
	}

	public String getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}

	public String getDeliveredQtyInSalesUom() {
		return deliveredQtyInSalesUom;
	}

	public void setDeliveredQtyInSalesUom(String deliveredQtyInSalesUom) {
		this.deliveredQtyInSalesUom = deliveredQtyInSalesUom;
	}
	
	public String getDistributionChannel() {
		return distributionChannel;
	}

	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.shipmentdocument;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class ShipmentDeliveryHeaderDTO implements Serializable {

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / VBELN
	 */
	private String salesAndDistributionDocumentNumber;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / LIFEX
	 */
	private String externalIdentificationOfDeliveryNote;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / VKORG
	 */
	private String salesOrganization;
		
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_delivery_header (E1EDL20) / shipment_delivery_item (E1EDL24)
	 */
	private List<ShipmentDeliveryItemDTO> shipmentDeliveryItemList;

	public String getSalesAndDistributionDocumentNumber() {
		return salesAndDistributionDocumentNumber;
	}

	public void setSalesAndDistributionDocumentNumber(String salesAndDistributionDocumentNumber) {
		this.salesAndDistributionDocumentNumber = salesAndDistributionDocumentNumber;
	}

	public String getExternalIdentificationOfDeliveryNote() {
		return externalIdentificationOfDeliveryNote;
	}

	public void setExternalIdentificationOfDeliveryNote(String externalIdentificationOfDeliveryNote) {
		this.externalIdentificationOfDeliveryNote = externalIdentificationOfDeliveryNote;
	}

	public String getSalesOrganization() {
		return salesOrganization;
	}

	public void setSalesOrganization(String salesOrganization) {
		this.salesOrganization = salesOrganization;
	}

	public List<ShipmentDeliveryItemDTO> getShipmentDeliveryItemList() {
		return shipmentDeliveryItemList;
	}

	public void setShipmentDeliveryItemList(List<ShipmentDeliveryItemDTO> shipmentDeliveryItemList) {
		this.shipmentDeliveryItemList = shipmentDeliveryItemList;
	}	
	
}
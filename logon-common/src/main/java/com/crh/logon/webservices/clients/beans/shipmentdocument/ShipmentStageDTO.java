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
public class ShipmentStageDTO implements Serializable {

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_stage (E1EDK33) / DISTZ
	 */
	private String distance;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_stage (E1EDK33) / MEDST
	 */
	private String distanceUom;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_stage (E1EDK33) / WARZTDA
	 */
	private String shipmentWaitingTime;

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDistanceUom() {
		return distanceUom;
	}

	public void setDistanceUom(String distanceUom) {
		this.distanceUom = distanceUom;
	}

	public String getShipmentWaitingTime() {
		return shipmentWaitingTime;
	}

	public void setShipmentWaitingTime(String shipmentWaitingTime) {
		this.shipmentWaitingTime = shipmentWaitingTime;
	}
	
}

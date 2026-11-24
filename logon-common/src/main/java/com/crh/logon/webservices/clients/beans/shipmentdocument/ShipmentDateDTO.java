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
public class ShipmentDateDTO implements Serializable {

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_date (E1EDT10) / QUALF
	 */
	private String idocQualifierDateShipment;
	public static final String IDOC_QUALIFIER_DATE_SHIPMENT_001 = "001";
	public static final String IDOC_QUALIFIER_DATE_SHIPMENT_002 = "002";
	public static final String IDOC_QUALIFIER_DATE_SHIPMENT_003 = "003";
	public static final String IDOC_QUALIFIER_DATE_SHIPMENT_004 = "004";
	public static final String IDOC_QUALIFIER_DATE_SHIPMENT_005 = "005";
	public static final String IDOC_QUALIFIER_DATE_SHIPMENT_499 = "499";

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_date (E1EDT10) / NTANF
	 */
	private String constraintForActivityStartBasic;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_date (E1EDT10) / ISDD
	 */
	private String actualStartExecutionDate;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_date (E1EDT10) / IEDD
	 */
	private String actualFinishExecutionDate;

	public String getIdocQualifierDateShipment() {
		return idocQualifierDateShipment;
	}

	public void setIdocQualifierDateShipment(String idocQualifierDateShipment) {
		this.idocQualifierDateShipment = idocQualifierDateShipment;
	}

	public String getConstraintForActivityStartBasic() {
		return constraintForActivityStartBasic;
	}

	public void setConstraintForActivityStartBasic(String constraintForActivityStartBasic) {
		this.constraintForActivityStartBasic = constraintForActivityStartBasic;
	}

	public String getActualStartExecutionDate() {
		return actualStartExecutionDate;
	}

	public void setActualStartExecutionDate(String actualStartExecutionDate) {
		this.actualStartExecutionDate = actualStartExecutionDate;
	}

	public String getActualFinishExecutionDate() {
		return actualFinishExecutionDate;
	}

	public void setActualFinishExecutionDate(String actualFinishExecutionDate) {
		this.actualFinishExecutionDate = actualFinishExecutionDate;
	}
}

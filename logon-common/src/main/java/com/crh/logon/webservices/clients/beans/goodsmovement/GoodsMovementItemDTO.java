/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.goodsmovement;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class GoodsMovementItemDTO implements Serializable {

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / MATNR
	 */
	private String materialNumber;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / WERKS
	 */
	private String plant;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / LGORT
	 */
	private String storageLocation;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / BWART
	 */
	private String movementType;
	/**
	 * BWART code Transfer storage location within plant
	 */
	public static final String TR_WITHIN_PLANT = "311";
	/**
	 * BWART code for virtual storage location back to the real sloc
	 */
	public static final String TR_SAME_PLANT_VIRT_TO_REAL_SLOC = "312";
	/**
	 * BWART code for movements between plants.
	 */
	public static final String TR_BETWEEN_PLANTS = "301";
	/**
	 * BWART code for the rubish case
	 */
	public static final String TR_RUBISH = "551";
	/**
	 * BWART code for the cancel rubish case
	 */
	public static final String TR_CANCEL_RUBISH = "552";

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / ERFMQ
	 */
	private String quantityInUnitOfEntry;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / ERFME
	 */
	private String unitOfEntry;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / UMWRK
	 */
	private String issuingPlant;

	/**
	 * WS field to SAP: ShipmentDocumentCreation / goods_movement /
	 * goods_movements_mobile_data_entry_header /
	 * goods_movement_external_system_item / UMLGO
	 */
	private String issuingStorageLocation;

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

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public String getQuantityInUnitOfEntry() {
		return quantityInUnitOfEntry;
	}

	public void setQuantityInUnitOfEntry(String quantityInUnitOfEntry) {
		this.quantityInUnitOfEntry = quantityInUnitOfEntry;
	}

	public String getUnitOfEntry() {
		return unitOfEntry;
	}

	public void setUnitOfEntry(String unitOfEntry) {
		this.unitOfEntry = unitOfEntry;
	}

	public String getIssuingPlant() {
		return issuingPlant;
	}

	public void setReceivingPlant(String issuingPlant) {
		this.issuingPlant = issuingPlant;
	}

	public String getIssuingStorageLocation() {
		return issuingStorageLocation;
	}

	public void setReceivingStorageLocation(String issuingStorageLocation) {
		this.issuingStorageLocation = issuingStorageLocation;
	}

}

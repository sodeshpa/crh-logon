/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * @author AdNovum Informatik AG
 */
public class OrderTakingEditorOrderItemDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private OrderItemEntity orderItem;
	

	private Long sapMatId;
	private String prodPlantCd;
	private String quantityStr;
	private String uomCd;
	private Long loadingpointId;
	private Long mixDesignId;

	private Long sareaId;
	private Long shiptoSareaRestrId;
	private Long sapContractItemId;
	private Long sapContractId;
	
	private String compositeIdentifier;

	private String sapMatPrice; // Rate and currency, displayed in the "List Price" column.
	private String sapMatNo;

	// If the contract has quantity restriction, the remaining quantity will be set here.
	private String openQuantity;
	
	/**
	 * JSON data for the full list of materials available to this Ship To when this item represents
	 * the first material in the editor, otherwise the material list is determined by the contract selected
	 * with the first material.
	 */
	private String jsonProductList;

	private String jsonServiceOnlyProductList;


	// These fields are for the item notes, which were removed from the entity
	private String itemNoteOrderMgmt;
	private String itemNoteDelivery;
	private String itemNoteDispatching;
	private String itemNoteTruckDriver;
	private String itemNoteRailway;

	private DeliverySettingDTO deliverySetting;
	
	public OrderTakingEditorOrderItemDto() {
		this.orderItem = new OrderItemEntity();
	}
	
	public OrderItemEntity getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(OrderItemEntity orderItem) {
		this.orderItem = orderItem;
	}
	public Long getSapMatId() {
		return sapMatId;
	}
	public void setSapMatId(Long sapMatId) {
		this.sapMatId = sapMatId;
	}
	public String getProdPlantCd() {
		return prodPlantCd;
	}
	public void setProdPlantCd(String prodPlantCd) {
		this.prodPlantCd = prodPlantCd;
	}
	public String getQuantityStr() {
		return quantityStr;
	}
	public void setQuantityStr(String quantityStr) {
		this.quantityStr = quantityStr;
	}
	public Long getLoadingpointId() {
		return loadingpointId;
	}
	public void setLoadingpointId(Long loadingpointId) {
		this.loadingpointId = loadingpointId;
	}
	public Long getMixDesignId() {
		return mixDesignId;
	}
	public void setMixDesignId(Long mixDesignId) {
		this.mixDesignId = mixDesignId;
	}
	public String getUomCd() {
		return uomCd;
	}
	public void setUomCd(String uomCd) {
		this.uomCd = uomCd;
	}

	public Long getSareaId() {
		return this.sareaId;
	}

	public Long getShiptoSareaRestrId() {
		return shiptoSareaRestrId;
	}

	public void setShiptoSareaRestrId(Long shiptoSareaRestrId) {
		this.shiptoSareaRestrId = shiptoSareaRestrId;
	}

	public Long getSapContractItemId() {
		return sapContractItemId;
	}

	public void setSapContractItemId(Long sapContractItemId) {
		this.sapContractItemId = sapContractItemId;
	}

	public Long getSapContractId() {
		return sapContractId;
	}

	public void setSapContractId(Long sapContractId) {
		this.sapContractId = sapContractId;
	}

	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}

	public String getJsonProductList() {
		return jsonProductList;
	}

	public void setJsonProductList(String jsonProductList) {
		this.jsonProductList = jsonProductList;
	}

	public String getJsonServiceOnlyProductList() {
		return jsonServiceOnlyProductList;
	}

	public void setJsonServiceOnlyProductList(String jsonServiceOnlyProductList) {
		this.jsonServiceOnlyProductList = jsonServiceOnlyProductList;
	}
	
	public String getSapMatPrice() {
		return sapMatPrice;
	}

	public void setSapMatPrice(String sapMatPrice) {
		this.sapMatPrice = sapMatPrice;
	}

	public String getSapMatNo() {
		return sapMatNo;
	}

	public void setSapMatNo(String sapMatNo) {
		this.sapMatNo = sapMatNo;
	}

	public String getCompositeIdentifier() {
		return compositeIdentifier;
	}

	public void setCompositeIdentifier(String compositeIdentifier) {
		this.compositeIdentifier = compositeIdentifier;
	}

	public String getOpenQuantity() {
		return openQuantity;
	}

	public void setOpenQuantity(String openQuantity) {
		this.openQuantity = openQuantity;
	}
	
	/**
	 * @return the itemNoteOrderMgmt
	 */
	public String getItemNoteOrderMgmt() {
		return itemNoteOrderMgmt;
	}

	/**
	 * @param itemNoteOrderMgmt the itemNoteOrderMgmt to set
	 */
	public void setItemNoteOrderMgmt(String itemNoteOrderMgmt) {
		this.itemNoteOrderMgmt = itemNoteOrderMgmt;
	}

	/**
	 * @return the itemNoteDelivery
	 */
	public String getItemNoteDelivery() {
		return itemNoteDelivery;
	}

	/**
	 * @param itemNoteDelivery the itemNoteDelivery to set
	 */
	public void setItemNoteDelivery(String itemNoteDelivery) {
		this.itemNoteDelivery = itemNoteDelivery;
	}

	/**
	 * @return the itemNoteDispatching
	 */
	public String getItemNoteDispatching() {
		return itemNoteDispatching;
	}

	/**
	 * @param itemNoteDispatching the itemNoteDispatching to set
	 */
	public void setItemNoteDispatching(String itemNoteDispatching) {
		this.itemNoteDispatching = itemNoteDispatching;
	}

	/**
	 * @return the itemNoteTruckDriver
	 */
	public String getItemNoteTruckDriver() {
		return itemNoteTruckDriver;
	}

	/**
	 * @param itemNoteTruckDriver the itemNoteTruckDriver to set
	 */
	public void setItemNoteTruckDriver(String itemNoteTruckDriver) {
		this.itemNoteTruckDriver = itemNoteTruckDriver;
	}

	/**
	 * @return the itemNoteRailway
	 */
	public String getItemNoteRailway() {
		return itemNoteRailway;
	}

	/**
	 * @param itemNoteRailway the itemNoteRailway to set
	 */
	public void setItemNoteRailway(String itemNoteRailway) {
		this.itemNoteRailway = itemNoteRailway;
	}

	public DeliverySettingDTO getDeliverySetting() {
		return deliverySetting;
	}

	public void setDeliverySetting(DeliverySettingDTO deliverySetting) {
		this.deliverySetting = deliverySetting;
	}


}

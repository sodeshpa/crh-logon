/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.math.BigDecimal;

import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 *
 * @author AdNovum Informatik AG
 *
 */
public class CustorderItemDeliverySetting extends AbstractDeliverySetting implements DeliverySetting{

	private final CustorderEntity order;
	private final CustorderItemEntity item;

	public CustorderItemDeliverySetting (CustorderEntity order, CustorderItemEntity orderItemEntity) {
		this.item = orderItemEntity;
		this.order = order;
		this.sarea = order.getSarea();
		this.sapMaterial = orderItemEntity.getSapMaterial();
		this.sapContractItem = orderItemEntity.getSapContractItem();

		this.deliverySettingType = DeliverySettingDTO.DELIVERY_SETTING_NOT_DEFINED;
		initCompositeIdentifier();
	}

	/**
	 * @param sarea
	 * @param sapContractItem
	 * @param sapMaterial
	 * @param restr
	 * @return
	 */
	private void  initCompositeIdentifier() {

		String compositeIdentifier = ""; 
		compositeIdentifier += this.sarea.getSareaId()+ "_";
		compositeIdentifier += this.sapContractItem != null ?  this.sapContractItem.getSapContractItemId()+ "_" : "";
		compositeIdentifier +=  this.sapMaterial.getSapMaterialId();
		this.compositeIdentifier = compositeIdentifier;
	}

	public Long getBilltoSapBpId() {
		return this.order.getBilltoSapBp() == null ? null : this.order.getBilltoSapBp().getSapBpId(); 
	}

	public Long getCommercialCarrierId() {
		return this.order.getCommCarrier() == null ? null : this.order.getCommCarrier().getCarrierId();	
	}

	public String getCompositeIdentifier() {
		return compositeIdentifier;
	}

	public int getDeliverySettingType() {
		return deliverySettingType;
	}

	public Long getDeliveryTypeCd() {
		return order.getDeliveryType()   == null ? null :order.getDeliveryType().getDeliveryTypeCd();
	}

	public Long getLoadingpointId() {
		return  item.getLoadingpoint()  == null ? null :item.getLoadingpoint().getLoadingpointId();
	}

	public Long getPayerSapBpId() {
		return order.getPayerSapBp() == null ? null :order.getPayerSapBp().getSapBpId();
	}

	public Long getPoolId() {
		return  order.getPool()== null ? null :order.getPool().getPoolId();
	}

	public String getProductionPlantCd() {
		return item.getProductionPlant() == null ? null :item.getProductionPlant().getPlantCd();
	}

	public BigDecimal getQuantity() {
		return  item.getOrderedQuantity()== null ? null :item.getOrderedQuantity();
	}

	public int getResolutionType() {
		return DeliverySettingDTO.RESOLUTION_NOT_RELEVANT;
	}

	public SapContractItemEntity getSapContractItem() {
		return  item.getSapContractItem()== null ? null :item.getSapContractItem();
	}

	public Long getSapContractItemId() {
		return  item.getSapContractItem()== null ? null :item.getSapContractItem().getSapContractItemId();
	}

	public SapMaterialEntity getSapMaterial() {
		return  sapMaterial;
	}

	public Long getSapMaterialId() {
		return  sapMaterial== null ? null :sapMaterial.getSapMaterialId();
	}

	public SareaEntity getSarea() {
		return  sarea;
	}

	public Long getShippingpointId() {
		return order.getShippingpoint() == null ? null :order.getShippingpoint().getShippingpointId();
	}

	public ShiptoSareaRestrEntity getShiptoSareaRestr() {
		return null;
	}

	public Long getShiptoSareaRestrId() {
		return null;
	}

	public Long getShiptoSiloId() {
		return  item.getShiptoSilo()== null ? null :item.getShiptoSilo().getShiptoSiloId();

	}

	public String getSupplyPlantCd() {
		return order.getShippingpoint()  == null ? null : order.getShippingpoint().getPlant().getPlantCd();

	}

	public String getUomCd() {
		return  item.getOrderedUom()== null ? null : item.getOrderedUom().getUomCd();
	}
}

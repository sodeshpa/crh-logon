/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.forms.pickup;

import java.io.Serializable;
import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.interfaces.UomConverterService;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.web.utils.ServiceLocatorProvider;


public class PickupCustorderItemBean extends AbstractPickupItemBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(PickupCustorderItemBean.class);

	private CustorderItemEntity itemEntity;

	/**
	 * Creates and itembean according to a valid restrictionId. This happens in create new pickup order. 
	 * @param compositeID
	 */
	public PickupCustorderItemBean(DeliverySettingDTO deliverySettingDTO) {
		super(deliverySettingDTO);
		
		itemEntity = new CustorderItemEntity();
		
		CustorderUtils.setupCustorderItemAttributes(deliverySettingDTO, itemEntity);
		
		// do not split orders initiated from terminal, see LOGON-6000
		itemEntity.setPerSdorderQuantity(null);
		itemEntity.setPerSdorderUom(null);
		
		// since there are currently just BAG orders, we can simply overwrite the exact single
		// delivery order flag with true
		itemEntity.setExactSdorderQtyFlag(Boolean.TRUE);
		
		itemEntity.setSapMaterial(deliverySettingDTO.getSapMaterial());
		itemEntity.setProductionPlant(deliverySettingDTO.getProductionPlant());
		itemEntity.setOrderedUom(deliverySettingDTO.getUom());
		itemEntity.setSapContractItem(deliverySettingDTO.getSapContractItem());
		
//		itemEntity.setLoadingpoint(orderBean.getLoadingpointEntity());
//		itemEntity.setCustorder(orderBean.getPickupOrder());
	}
	
	/**
	 * Creates an itembean from an existing orderItemEntity. This happens in case of edit mode.
	 * @param item
	 * @param orderBean
	 */
	public PickupCustorderItemBean(DeliverySettingDTO deliverySettingDTO, CustorderItemEntity item) {
		super(deliverySettingDTO);
		this.itemEntity = item;
	}
	

	public BigDecimal getQuantityInSalesUom() {
		
		if (itemEntity.getOrderedUom() != null && itemEntity.getOrderedQuantity() != null ) {
			UomBsEntity salesUom = null;
			try {
				UomBsEntity orderedUom = itemEntity.getOrderedUom();
				BigDecimal orderedQuantity = itemEntity.getOrderedQuantity();
				SareaEntity sarea = itemEntity.getCustorder().getSarea();
				salesUom = ServiceLocatorProvider.getServiceLocator().getUomConverterService().getSalesUom( itemEntity.getSapMaterial(), sarea);
				final UomConverterService converter;
				final UomConversionResult conversion;
				converter = ServiceLocatorProvider.getServiceLocator().getUomConverterService();
				conversion = converter.convert(orderedQuantity, orderedUom.getUomCd(), salesUom.getUomCd(), itemEntity.getSapMaterial().getSapMaterialId(), null, null, false);
				BigDecimal quantityInSalesUom = conversion.getQuantity();
				return quantityInSalesUom;
			}
			catch (Exception e) {
				LOG.error("Could not retrieve ordered uom in sales uom from pickup terminal.[FromUom=" + itemEntity.getOrderedUom().getUomCd() + "; toUom=" + (salesUom != null ? salesUom.getUomCd() : "null") + "]");
			}
		}
		return null;
	}
	
	public BigDecimal getQuantityInTons() {

		if (itemEntity.getOrderedUom() != null && itemEntity.getOrderedQuantity() != null ) {
			try {
				UomBsEntity orderedUom = itemEntity.getOrderedUom();
				BigDecimal orderedQuantity = itemEntity.getOrderedQuantity();
				final UomConverterService converter;
				final UomConversionResult conversion;
				converter = ServiceLocatorProvider.getServiceLocator().getUomConverterService();
				conversion = converter.convert(orderedQuantity, orderedUom.getUomCd(), Constant.UOM_TONS, itemEntity.getSapMaterial().getSapMaterialId(), null, null, false);
				BigDecimal quantityInTons = conversion.getQuantity();
				return quantityInTons;
			}
			catch (Exception e) {
				LOG.error("Could not retrieve ordered uom in tons[FromUom=" + itemEntity.getOrderedUom().getUomCd() +"]");
			}			
		}
		return null;
	}

	public CustorderItemEntity getItemEntity() {
		return itemEntity;
	}

	public void setItemEntity(CustorderItemEntity itemEntity) {
		this.itemEntity = itemEntity;
	}
	
	public void setQuantity(BigDecimal quantity){
		itemEntity.setOrderedQuantity(quantity);
	}
	
}


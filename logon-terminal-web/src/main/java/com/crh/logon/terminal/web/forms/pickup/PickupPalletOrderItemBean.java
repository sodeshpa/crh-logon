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
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.persistence.entities.common.OrderItemEntity;


public class PickupPalletOrderItemBean extends AbstractPickupItemBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(PickupPalletOrderItemBean.class);

	private OrderItemEntity itemEntity;
	
	
	/**
	 * Creates and itembean according to a valid restrictionId. This happens in create new pickup order. 
	 * @param restrictionId
	 * @param orderBean
	 */
	public PickupPalletOrderItemBean(DeliverySettingDTO deliverySettingDTO) {
		super(deliverySettingDTO);
		
		
		if( LogonPackageHandlingUtil.isPallet(deliverySettingDTO.getSapMaterial())) {
			
			OrderItemEntity palletOrderItem = new OrderItemEntity();
			OrderUtils.setupOrderItemAttributes(deliverySettingDTO, palletOrderItem);
			
			//if new element is a palet, then a palet was ordered by hand -> set the returned quantity as default 0.
			palletOrderItem.setReturnedQuantity(BigDecimal.ZERO);
			palletOrderItem.setReturnedUom(deliverySettingDTO.getUom());
		}
	}
	
	/**
	 * Creates an itembean from an existing orderItemEntity. This happens in case of edit mode.
	 * @param item
	 * @param orderBean
	 */
	public PickupPalletOrderItemBean(DeliverySettingDTO deliverySettingDTO, OrderItemEntity item) {
		super(deliverySettingDTO);
		this.itemEntity = item;
	}

	
	public void setQuantity(BigDecimal quantity){
		itemEntity.setOrderedQuantity(quantity);
	}

	public OrderItemEntity getItemEntity() {
		return itemEntity;
	}

	public void setItemEntity(OrderItemEntity itemEntity) {
		this.itemEntity = itemEntity;
	}

}


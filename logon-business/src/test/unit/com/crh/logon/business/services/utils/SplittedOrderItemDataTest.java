/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class SplittedOrderItemDataTest {

	@Test
	public void testUpdateFrom() {
	
		OrderItemEntity orderItem = getOrderItem("TO");
		int index = 0;
		
		SplittedOrderItemData original = getOrderItemData(orderItem, index, new BigDecimal("2"), "TO");
		
		SplittedOrderItemData updated = new SplittedOrderItemData();
		updated.setOrderedQuantity(new BigDecimal("3"));
		updated.setOrderedUom("TO");
		
		original.updateFrom(updated);
		
		// Assert the SplittedOrderItemData gets updated 
		Assert.assertEquals(new BigDecimal("3"), original.getOrderedQuantity());
		Assert.assertEquals("TO", original.getOrderedUom());

		// Assert the enclosed order item gets updated
		Assert.assertEquals(new BigDecimal("3"), original.getOrderItem().getOrderedQuantity());
		Assert.assertEquals("TO", original.getOrderItem().getOrderedUom().getUomCd());
		
	}
	
	@Test
	public void testQuantityEditable() {
		
		OrderItemEntity orderItem = getOrderItem("TO");
		
		SplittedOrderItemData data = new SplittedOrderItemData(orderItem, 0, null);
		
	}

	private SplittedOrderItemData getOrderItemData(OrderItemEntity orderItem,
			int index, BigDecimal orderedQuantity, String orderedUom) {
		
		MaterialType materialType = null;
		if (LogonPackageHandlingUtil.isBag(orderItem)) {
			materialType = MaterialType.BAG;
		}
		else if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
			materialType = MaterialType.BULK;
		}
		else if (LogonPackageHandlingUtil.isService(orderItem)) {
			materialType = MaterialType.SERVICE;
		}
		
		SplittedOrderItemData orderItemData = new SplittedOrderItemData(orderItem, index, materialType);
		orderItemData.setOrderedQuantity(orderedQuantity);
		orderItemData.setOrderedUom(orderedUom);
		
		return orderItemData;
	}

	private UomBsEntity getUomBsEntity(String uomCd) {
		UomBsEntity uomBs = new UomBsEntity();
		uomBs.setUomCd(uomCd);
		return uomBs;
	}

	private OrderItemEntity getOrderItem(String uomCd) {
		
		MaterialPackageBsEntity materialPackage = new MaterialPackageBsEntity();
		
		SapMaterialEntity sapMaterial = new SapMaterialEntity();
		sapMaterial.setMaterialPackage(materialPackage);
		
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setOrderedUom(getUomBsEntity(uomCd));
		orderItem.setSapMaterial(sapMaterial);
		
		return orderItem;
	}
	
}
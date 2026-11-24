/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AutoaddMaterialHelperTest {

	@Test
	public void testBuildAlreadyPresentMapBasedOnIds() {
		
		List<OrderItemEntity> orderItems = buildOrderItemList(new Long[]{1L,2L,3L,4L});
		
		Map<Long, OrderItemEntity> map = AutoaddMaterialHelper.buildAlreadyPresentMapBasedOnIds(orderItems);
		
		Assert.assertEquals(1L, map.get(1L).getSapMaterial().getSapMaterialId().longValue());
		Assert.assertEquals(2L, map.get(2L).getSapMaterial().getSapMaterialId().longValue());
		Assert.assertEquals(3L, map.get(3L).getSapMaterial().getSapMaterialId().longValue());
		Assert.assertEquals(4L, map.get(4L).getSapMaterial().getSapMaterialId().longValue());
		
	}
	
	@Test
	public void testBuildOrderItemList() {
		
		Long[] idList = new Long[]{1L,2L,3L,4L};
		List<OrderItemEntity> orderItems = buildOrderItemList(idList);
		
		Assert.assertEquals(4, orderItems.size());
		for (int i = 0; i < orderItems.size(); i++) {
			OrderItemEntity orderItem = orderItems.get(i);
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			Assert.assertEquals(idList[i], sapMaterial.getSapMaterialId());
		}
		
	}

	/**
	 * @param longs
	 * @return
	 */
	private List<OrderItemEntity> buildOrderItemList(Long[] ids) {
		
		List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
		
		for (Long id : ids) {
			SapMaterialEntity material = new SapMaterialEntity();
			material.setSapMaterialId(id);
			OrderItemEntity item = new OrderItemEntity();
			item.setSapMaterial(material);
			orderItems.add(item);
		}
		
		return orderItems;
	}
	
}

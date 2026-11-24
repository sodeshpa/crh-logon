/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class OrderUtilsTest {

	Long[][] materialNos = new Long[][]{
			new Long[]{1L, 2L, 3L}, 
			new Long[]{1L, 3L}, 
			new Long[]{3L}, 
			new Long[]{1L, 4L}
	};
	
	@Test
	public void testCollectAllMaterialsForOrders() {
		
		
		List<OrderEntity> orders = createOrderList(materialNos);
		
		Set<SapMaterialEntity> materials = OrderUtils.collectAllMaterialsForOrders(orders);
		
		Assert.assertEquals(4, materials.size());
		
	}
	
	@Test
	public void testCreateOrderWithMaterials() {
		
		Long[] materialNos = new Long[]{1L, 2L, 3L};
		OrderEntity orderEntity = createOrderWithMaterials(materialNos);
		List orderItems = orderEntity.getOrderItems();
		
		for (int i = 0; i < materialNos.length; i++) {
			OrderItemEntity item = (OrderItemEntity) orderItems.get(i);
			Assert.assertEquals(materialNos[i], item.getSapMaterial().getSapMaterialId());
		}
		
	}
	
	@Test
	public void testCreateOrderList() {
		
		List<OrderEntity> orders = createOrderList(materialNos);
		Assert.assertEquals(4, orders.size());
		
	}

	/**
	 * @param strings
	 * @param strings2
	 * @param strings3
	 * @param strings4
	 * @return
	 */
	private List<OrderEntity> createOrderList(Long[][] materialNos) {
		
		List<OrderEntity> orders = new ArrayList<OrderEntity>();
		
		for (int i = 0; i < materialNos.length; i++) {
			Long[] orderMaterialNos = materialNos[i];
			OrderEntity createOrderWithMaterials = createOrderWithMaterials(orderMaterialNos);
			orders.add(createOrderWithMaterials);
		}
		
		return orders;
	}

	/**
	 * @param materialNos
	 * @return
	 */
	private OrderEntity createOrderWithMaterials(Long[] materialNos) {

		OrderEntity order = new OrderEntity();
		order.setArchivedFlag(false);
		List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
		
		for (int i = 0; i < materialNos.length; i++) {
			SapMaterialEntity sapMaterial = new SapMaterialEntity();
			sapMaterial.setSapMaterialId(materialNos[i]);

			OrderItemEntity orderItem = new OrderItemEntity();
			orderItem.setSapMaterial(sapMaterial);
			
			orderItems.add(orderItem);
		}
		
		order.setOrderItems(orderItems);
		
		return order;
	}
	
}
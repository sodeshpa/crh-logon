/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;

/**
 * @author AdNovum Informatik AG
 */
public class OrderUtilsPresetOrdersTest {
	
	int bulkCountOfDeliveries = 5;
	
	BigDecimal qtyPerDelivery = new BigDecimal("3");
	ShiptoEntity shipTo = new ShiptoEntity();
	OrderEntity bagOrder = new OrderEntity();
	
	CustorderEntity custorderEntity;
	List<OrderEntity> bagOrders;
	List<OrderItemEntity> bagOrderItems;
	List<OrderEntity> bulkOrders;
	
	@Before
	public void setUp() {
		
		custorderEntity = new CustorderEntity();
		
		// Bulk Orders
		bulkOrders = new ArrayList<OrderEntity>();
		for (int i = 0; i < bulkCountOfDeliveries; i++) {
			
			OrderEntity bulkOrder = new OrderEntity();
			bulkOrder.setArchivedFlag(false);
			bulkOrder.setCustorder(custorderEntity);
			bulkOrder.setDeliveryFromDat(new Date());
			bulkOrder.setDeliveryToDat(new Date());
			bulkOrders.add(bulkOrder);
			
			List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
			orderItems.add(new OrderItemEntity());
			bulkOrder.setOrderItems(orderItems);
			
		}
		
		// Bag Orders
		bagOrderItems = new ArrayList<OrderItemEntity>();
		bagOrderItems.add(new OrderItemEntity());
		bagOrderItems.add(new OrderItemEntity());
		
		bagOrder.setOrderItems(bagOrderItems);
		bagOrder.setCustorder(custorderEntity);
		bagOrder.setDeliveryFromDat(new Date());
		bagOrder.setDeliveryToDat(new Date());
		bagOrder.setArchivedFlag(false);
		
		bagOrders = new ArrayList<OrderEntity>();
		bagOrders.add(bagOrder);
		
	}

	@Test
	public void testCreateSplittedOrdersBulkCheckNumberOfItems() {

		custorderEntity.setOrders(bulkOrders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBulk(custorderEntity, shipTo, bulkCountOfDeliveries, qtyPerDelivery);

		// Check number of splitted order data
		Assert.assertEquals(bulkCountOfDeliveries, resultList.size());

		// Check number of orderItems
		for (SplittedOrderData splittedOrderData : resultList) {
			Assert.assertEquals(1, splittedOrderData.getOrder().getOrderItems().size());
		}

	}

	@Test
	public void testCreateSplittedOrdersBagCheckNumberOfItems() {

		custorderEntity.setOrders(bagOrders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBag(custorderEntity, shipTo, qtyPerDelivery);

		// Check we have one splitted order for Bag 
		Assert.assertEquals(1, resultList.size());

		SplittedOrderData spOrder = resultList.get(0);

		// Check we have as many order item as custorder item
		Assert.assertEquals(bagOrderItems.size(), spOrder.getOrder().getOrderItems().size());

	}
	
	/**
	 * Bulk CustOrder <1..N> Order
	 * Bulk Order     <1..1> OrderItem
	 */
	@Test
	public void testCreateSplittedOrdersBulkCheckSameItems() {

		custorderEntity.setOrders(bulkOrders);
		
		List<SplittedOrderData> splittedOrderList = OrderUtils.createSplittedOrdersBulk(custorderEntity, shipTo, bulkCountOfDeliveries, qtyPerDelivery);

		for (int i = 0; i < splittedOrderList.size(); i++) {
			
			OrderEntity order = bulkOrders.get(i);
			OrderEntity resultOrder = splittedOrderList.get(i).getOrder();
			
			Assert.assertEquals(order.getOrderItems().size(), resultOrder.getOrderItems().size());
			
		}

	}
	
	/**
	 * Bag CustOrder <1..1> Order
	 * Bag Order     <1..N> OrderItem
	 */
	@Test
	public void testCreateSplittedOrdersBagCheckSameItems() {

		OrderEntity order = new OrderEntity();
		
		order.setArchivedFlag(false);
		List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
		orderItems.add(new OrderItemEntity());
		orderItems.add(new OrderItemEntity());
		order.setOrderItems(orderItems);
		order.setDeliveryFromDat(new Date());
		order.setDeliveryToDat(new Date());
		
		List<OrderEntity> orders = new ArrayList<OrderEntity>();
		orders.add(order);
		
		custorderEntity.setOrders(orders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBag(custorderEntity, shipTo, qtyPerDelivery);

		SplittedOrderData spOrder = resultList.get(0);
		
		OrderEntity resultOrder = spOrder.getOrder();
		
		Assert.assertSame(order, resultOrder);
		
		List<OrderItemEntity> resultOrderItems = resultOrder.getOrderItems();
		Assert.assertSame(orderItems, resultOrderItems);
		
	}
	
	@Test
	public void testCreateSplittedOrdersBulkCheckQtyPerDelivery() {

		custorderEntity.setOrders(bulkOrders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBulk(custorderEntity, shipTo, bulkCountOfDeliveries, qtyPerDelivery);

		// Check number of splitted order data
		Assert.assertEquals(bulkCountOfDeliveries, resultList.size());

		// Check number of orderItems
		for (SplittedOrderData splittedOrderData : resultList) {
			OrderEntity resultOrder = splittedOrderData.getOrder();
			OrderItemEntity resultOrderItem = (OrderItemEntity) resultOrder.getOrderItems().get(0);
			Assert.assertEquals(qtyPerDelivery, resultOrderItem.getOrderedQuantity());
		}

	}
	
	@Test
	public void testCreateSplittedOrdersBagCheckQtyPerDelivery() {

		custorderEntity.setOrders(bagOrders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBag(custorderEntity, shipTo, qtyPerDelivery);

		SplittedOrderData spOrder = resultList.get(0);
		OrderEntity resultOrder = spOrder.getOrder();
		List<OrderItemEntity> orderItems = resultOrder.getOrderItems();
		
		for (OrderItemEntity orderItemEntity : orderItems)
		{
			
			// Check we have as many order item as custorder item
			Assert.assertEquals(qtyPerDelivery, orderItemEntity.getOrderedQuantity());
			
		}

	}
	
	@Test
	public void testCreateSplittedOrdersBulkCheckOrderKnowCustorder() {

		custorderEntity.setOrders(bulkOrders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBulk(custorderEntity, shipTo, bulkCountOfDeliveries, qtyPerDelivery);

		// Check number of orderItems
		for (SplittedOrderData splittedOrderData : resultList) {
			OrderEntity resultOrder = splittedOrderData.getOrder();
			Assert.assertSame(resultOrder.getCustorder(), custorderEntity);
		}

	}
	
	@Test
	public void testCreateSplittedOrdersBagCheckOrderKnowCustorder() {

		custorderEntity.setOrders(bagOrders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBag(custorderEntity, shipTo, qtyPerDelivery);

		SplittedOrderData spOrder = resultList.get(0);
		OrderEntity resultOrder = spOrder.getOrder();
		
		Assert.assertSame(custorderEntity, resultOrder.getCustorder());

	}
	
	@Test
	public void testCreateSplittedOrdersBulkCheckCustorderKnowOrder() {

		custorderEntity.setOrders(bulkOrders);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBulk(custorderEntity,shipTo, bulkCountOfDeliveries, qtyPerDelivery);

		// Check number of orderItems
		for (int i =0;  i<resultList.size(); i++) {
			
			SplittedOrderData splittedOrderData = resultList.get(i);
			OrderEntity resultOrder = splittedOrderData.getOrder();
			Assert.assertSame(custorderEntity.getOrders().get(i), resultOrder);
			
		}

	}
	
}
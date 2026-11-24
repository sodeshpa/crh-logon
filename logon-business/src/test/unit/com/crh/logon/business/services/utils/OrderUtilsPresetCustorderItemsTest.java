/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;

/**
 * TODO: test: shipto
 * @author AdNovum Informatik AG
 */
public class OrderUtilsPresetCustorderItemsTest {
	
	static BigDecimal qtyPerDelivery = new BigDecimal("3");
	static List<CustorderItemEntity> bagCustorderItems = new ArrayList<CustorderItemEntity>();
	static ShiptoEntity shipTo = new ShiptoEntity();
	static int bulkCountOfDeliveries = 5;
	
	CustorderEntity custorderEntity;
	
	@BeforeClass
	public static void setUpClass() {
		
		// BagCustorderItems
		bagCustorderItems.add(new CustorderItemEntity());
		bagCustorderItems.add(new CustorderItemEntity());
		bagCustorderItems.add(new CustorderItemEntity());
		
	}
	
	@Before
	public void setUp() {
		
		custorderEntity = new CustorderEntity();
		custorderEntity.setOrders(Collections.<OrderEntity>emptyList());
		
	}

	@Test
	public void testCreateSplittedOrdersBulkCheckNumberOfItems() {

		List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
		custorderItems.add(new CustorderItemEntity());
		custorderEntity.setCustorderItems(custorderItems);

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

		custorderEntity.setCustorderItems(bagCustorderItems);

		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBag(custorderEntity, shipTo, qtyPerDelivery);

		// Check we have one splitted order for Bag 
		Assert.assertEquals(1, resultList.size());

		SplittedOrderData spOrder = resultList.get(0);

		// Check we have as many order item as custorder item
		Assert.assertEquals(bagCustorderItems.size(), spOrder.getOrder().getOrderItems().size());

	}
	
	@Test
	public void testCreateSplittedOrdersBulkCheckShipTo() {

		List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
		custorderItems.add(new CustorderItemEntity());
		custorderEntity.setCustorderItems(custorderItems);

		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBulk(custorderEntity, shipTo, bulkCountOfDeliveries, qtyPerDelivery);

		// Check number of orderItems
		for (SplittedOrderData splittedOrderData : resultList) {
			Assert.assertSame(shipTo, splittedOrderData.getShipTo());
		}

	}

	@Test
	public void testCreateSplittedOrdersBagCheckShipTo() {

		custorderEntity.setCustorderItems(bagCustorderItems);

		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBag(custorderEntity, shipTo, qtyPerDelivery);

		SplittedOrderData spOrder = resultList.get(0);

		// Check we have as many order item as custorder item
		Assert.assertSame(shipTo, spOrder.getShipTo());

	}
	
	@Test
	public void testCreateSplittedOrdersBulkCheckQtyPerDelivery() {

		List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
		custorderItems.add(new CustorderItemEntity());
		custorderEntity.setCustorderItems(custorderItems);

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

		custorderEntity.setCustorderItems(bagCustorderItems);
		
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

		List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
		custorderItems.add(new CustorderItemEntity());
		custorderEntity.setCustorderItems(custorderItems);

		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBulk(custorderEntity, shipTo, bulkCountOfDeliveries, qtyPerDelivery);

		// Check number of orderItems
		for (SplittedOrderData splittedOrderData : resultList) {
			OrderEntity resultOrder = splittedOrderData.getOrder();
			Assert.assertSame(resultOrder.getCustorder(), custorderEntity);
		}

	}

	@Test
	public void testCreateSplittedOrdersBagCheckOrderKnowCustorder() {

		custorderEntity.setCustorderItems(bagCustorderItems);
		
		List<SplittedOrderData> resultList = OrderUtils.createSplittedOrdersBag(custorderEntity, shipTo, qtyPerDelivery);

		SplittedOrderData spOrder = resultList.get(0);
		OrderEntity resultOrder = spOrder.getOrder();
		
		Assert.assertSame(custorderEntity, resultOrder.getCustorder());

	}
	
}
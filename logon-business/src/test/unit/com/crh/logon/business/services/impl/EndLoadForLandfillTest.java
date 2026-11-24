/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;

import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * Goal of this unit test is to test orderServiceBean.endLoad for landfill cases, which
 * are the following:
 * 
 * case1: pda, weighing by lds (at crh site)
 * case2: pda, no lds (enter weight in logon)
 * case3: pda, weighing data provided by pda (at crh site)
 * case4: no pda, no lds (enter weight in logon)
 * case5: no pda, weighing by lds (at crh site)
 * 
 * (see event calls detailed in LOGON-6871, 'comment - 12.11.2013 14:53')
 * 
 * @author AdNovum Informatik AG
 */


public class EndLoadForLandfillTest extends BaseLandfillSetup {

	@Test
	public void testOrderStatus_case1_2_3_LoadByPdaAtCustomerSite() {
		tareWeightT = BigDecimal.ZERO;
		grossWeightT = BigDecimal.ZERO;
		netQuantity = BigDecimal.ZERO;
		
		source = EventSource.MOP;
		isAccurate = false;
		validateComposition = true;
		
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_LOADING;
		long expectedOrderStatus = Constant.ORDER_STATUS_SHIPPING;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		boolean expectedDelivered = expectedOrderStatus == Constant.ORDER_STATUS_DELIVERED;
		
		
		orderServiceBean.endLoadAndSendGoodsMovements(event, load, loadingpoint, tareWeightT, grossWeightT, netQuantity, isAccurate, 
				validateComposition, source, lock);
		
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after endLoad (initiated by pda at customer site)", 
				expectedOrderStatus, orderStatusCd);
		
		BigDecimal truckNetWeight = truck.getMaxGrossWeightT().subtract(truck.getTareWeightT());
		assertLoadWeighingData(load, truck.getTareWeightT(), truck.getMaxGrossWeightT(), truckNetWeight);
		assertOrderItemWeighingData(order, truckNetWeight, expectedDelivered);
	}
	
	@Test
	public void testOrderStatus_case1_WeighingByLdsAtCrhSite() {
		tareWeightT = new BigDecimal(6);
		grossWeightT = new BigDecimal(24);
		netQuantity = new BigDecimal(18);
		
		source = EventSource.LDS;
		isAccurate = true;
		validateComposition = true;
		
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		long expectedOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		boolean expectedDelivered = expectedOrderStatus == Constant.ORDER_STATUS_DELIVERED;
		
		
		orderServiceBean.endLoadAndSendGoodsMovements(event, load, loadingpoint, tareWeightT, grossWeightT, netQuantity, isAccurate, 
				validateComposition, source, lock);
		
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after endLoad (initiated by LDS at customer site)", 
				expectedOrderStatus, orderStatusCd);
		
		assertLoadWeighingData(load, tareWeightT, grossWeightT, netQuantity);
		assertOrderItemWeighingData(order, netQuantity, expectedDelivered);
	}
	
	@Test
	public void testOrderStatus_case2_WeighingDataProvidedByLogonAtCrhSite() {
		tareWeightT = new BigDecimal(6);
		grossWeightT = new BigDecimal(24);
		netQuantity = new BigDecimal(18);
		
		source = EventSource.LOGON;
		isAccurate = true;
		validateComposition = true;
		
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		long expectedOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		boolean expectedDelivered = expectedOrderStatus == Constant.ORDER_STATUS_DELIVERED;
		
		
		orderServiceBean.endLoadAndSendGoodsMovements(event, load, loadingpoint, tareWeightT, grossWeightT, netQuantity, isAccurate, 
				validateComposition, source, lock);
		
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after endLoad (initiated by Logon/Loading Screen at crh site)",
				expectedOrderStatus, orderStatusCd);
		
		assertLoadWeighingData(load, tareWeightT, grossWeightT, netQuantity);
		assertOrderItemWeighingData(order, netQuantity, expectedDelivered);
	}
	
	@Test
	public void testOrderStatus_case3_WeighingDataProvidedByPdaAtCrhSite() {
		tareWeightT = new BigDecimal(6);
		grossWeightT = new BigDecimal(24);
		netQuantity = new BigDecimal(18);
		
		source = EventSource.MOP;
		isAccurate = true;
		validateComposition = true;
		
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		long expectedOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		boolean expectedDelivered = expectedOrderStatus == Constant.ORDER_STATUS_DELIVERED;
		
		
		orderServiceBean.endLoadAndSendGoodsMovements(event, load, loadingpoint, tareWeightT, grossWeightT, netQuantity, isAccurate, 
				validateComposition, source, lock);
		
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after endLoad (initiated by pda at crh site)",
				expectedOrderStatus, orderStatusCd);
		
		assertLoadWeighingData(load, tareWeightT, grossWeightT, netQuantity);
		assertOrderItemWeighingData(order, netQuantity, expectedDelivered);
	}
	
	@Test
	public void testOrderStatus_case4_LoadingByLogon_NoPda() {
		tareWeightT = new BigDecimal(6);
		grossWeightT = new BigDecimal(24);
		netQuantity = new BigDecimal(18);
		
		source = EventSource.LOGON;
		isAccurate = true;
		validateComposition = true;
		truck.setPdaAvailableFlag(false);
		
		long initialOrderStatus = Constant.ORDER_STATUS_UNLOADING;
		long expectedOrderStatus = Constant.ORDER_STATUS_DELIVERED;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		boolean expectedDelivered = expectedOrderStatus == Constant.ORDER_STATUS_DELIVERED;
		
		
		orderServiceBean.endLoadAndSendGoodsMovements(event, load, loadingpoint, tareWeightT, grossWeightT, netQuantity, isAccurate, 
				validateComposition, source, lock);
		
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after endLoad (initiated by Logon/Loading Screen)", 
				expectedOrderStatus, orderStatusCd);
		
		assertLoadWeighingData(load, tareWeightT, grossWeightT, netQuantity);
		assertOrderItemWeighingData(order, netQuantity, expectedDelivered);
	}
	
	@Test
	public void testOrderStatus_case5_LoadingByLds_NoPda() {
		tareWeightT = new BigDecimal(6);
		grossWeightT = new BigDecimal(24);
		netQuantity = new BigDecimal(18);
		
		source = EventSource.LDS;
		isAccurate = true;
		validateComposition = true;
		truck.setPdaAvailableFlag(false);
		
		long initialOrderStatus = Constant.ORDER_STATUS_UNLOADING;
		long expectedOrderStatus = Constant.ORDER_STATUS_DELIVERED;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		boolean expectedDelivered = expectedOrderStatus == Constant.ORDER_STATUS_DELIVERED;
		
		
		orderServiceBean.endLoadAndSendGoodsMovements(event, load, loadingpoint, tareWeightT, grossWeightT, netQuantity, isAccurate, 
				validateComposition, source, lock);
		
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after endLoad (initiated by LDS)", 
				expectedOrderStatus, orderStatusCd);
		
		assertLoadWeighingData(load, tareWeightT, grossWeightT, netQuantity);
		assertOrderItemWeighingData(order, netQuantity, expectedDelivered);
	}
	
	private void assertLoadWeighingData(LoadEntity load, BigDecimal expectedTareWeightT, BigDecimal expectedGrossWeightT, 
			BigDecimal expectedNetQuantity) {
		
		assertEquals("load.getTareWeightT is wrong", expectedTareWeightT, load.getTareWeightT());
		
		assertEquals("load.getGrossWeightT is wrong", expectedGrossWeightT, load.getGrossWeightT());
		
		assertEquals("load.getNetQuantity is wrong", expectedNetQuantity, load.getNetQuantity());
	}
	
	private void assertOrderItemWeighingData(OrderEntity order, BigDecimal expectedNetQuantity, boolean expectedDelivered) {
		int nrOfOrderItems = order == null || order.getOrderItems() == null ? null : order.getOrderItems().size();
		assertEquals("landfill order must have exactly 1 orderItem", 1, nrOfOrderItems);
		
		OrderItemEntity orderItem = order.getOrderItems().get(0);
		assertEquals("orderItem.getLoadedQuantity is wrong", expectedNetQuantity, orderItem.getLoadedQuantity());
		
		if (expectedDelivered) {
			assertEquals("orderItem.getDeliveredQuantity is wrong", expectedNetQuantity, orderItem.getDeliveredQuantity());
		}
		else {
			assertNull("orderItem.getDeliveredQuantity must be null", orderItem.getDeliveredQuantity());
		}
	}
	
}

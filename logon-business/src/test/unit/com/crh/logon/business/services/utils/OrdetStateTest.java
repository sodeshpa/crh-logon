/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class OrdetStateTest {

	int orderNumber; 
	
	@Test
	public void testCheckOrderStateAndFillListsNotDispatched() {
		
		Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
		List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>(); 
		
		long[] statusCodes = new long[] { 
				Constant.ORDER_STATUS_NEW, 
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK
		};
		
		for (long statusCd : statusCodes) {
			OrderEntity order = createOrder(statusCd, false);
			OrderUtils.checkOrderStateAndFillLists(order, deleteOrderMap, manualDeallocateOrderNumberList);
			
			ArrayList<Long> orderNrs = new ArrayList<Long>();
			for(OrderEntity o : deleteOrderMap.values()) {
				orderNrs.add(o.getOrderNumber());
			}
			Assert.assertTrue(orderNrs.contains(order.getOrderNumber()));
			Assert.assertFalse(manualDeallocateOrderNumberList.contains(order.getOrderId()));
		}
		
	}
	
	@Test
	public void testCheckOrderStateAndFillListsDispatchedWithNoSapCarrier() {
		
		Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
		List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>(); 
		
		long[] statusCodes = new long[] { 
				Constant.ORDER_STATUS_DISPATCHED, 
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER
		};
		
		for (long statusCd : statusCodes) {
			OrderEntity order = createOrder(statusCd, false);
			OrderUtils.checkOrderStateAndFillLists(order, deleteOrderMap, manualDeallocateOrderNumberList);
			ArrayList<Long> orderNrs = new ArrayList<Long>();
			for(OrderEntity o : deleteOrderMap.values()) {
				orderNrs.add(o.getOrderNumber());
			}
			Assert.assertTrue(orderNrs.contains(order.getOrderNumber()));
			Assert.assertFalse(manualDeallocateOrderNumberList.contains(order.getOrderId()));
		}
		
	}
	
	@Test
	public void testCheckOrderStateAndFillListsDispatchedWithSapCarrier() {
		
		Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
		List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>(); 
		
		long[] statusCodes = new long[] { 
				Constant.ORDER_STATUS_DISPATCHED, 
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER
		};
		
		for (long statusCd : statusCodes) {
			OrderEntity order = createOrder(statusCd, true);
			OrderUtils.checkOrderStateAndFillLists(order, deleteOrderMap, manualDeallocateOrderNumberList);
			Assert.assertTrue(manualDeallocateOrderNumberList.contains(order.getOrderNumber()));
			Assert.assertFalse(deleteOrderMap.keySet().contains(order.getOrderId()));
		}
		
		for (long statusCd : statusCodes) {
			OrderEntity order = createOrder(statusCd, false);
			OrderUtils.checkOrderStateAndFillLists(order, deleteOrderMap, manualDeallocateOrderNumberList);
			Assert.assertFalse(manualDeallocateOrderNumberList.contains(order.getOrderNumber()));
			Assert.assertTrue(deleteOrderMap.keySet().contains(order.getOrderId()));
		}
		
	}
	
	@Test
	public void testCheckOrderStateAndFillListsManuallyDeallocate() {
		
		Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
		List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>(); 
		
		long[] statusCodes = new long[] { 
				Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL, 
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
				Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING,
				Constant.ORDER_STATUS_LOADED,
				Constant.ORDER_STATUS_SHIPPING,
				Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
				Constant.ORDER_STATUS_UNLOADING
		};
		
		for (long statusCd : statusCodes) {
			OrderEntity order = createOrder(statusCd, false);
			OrderUtils.checkOrderStateAndFillLists(order, deleteOrderMap, manualDeallocateOrderNumberList);
			Assert.assertTrue(manualDeallocateOrderNumberList.contains(order.getOrderNumber()));
			Assert.assertFalse(deleteOrderMap.keySet().contains(order.getOrderId()));
		}
		
	}
	
	private OrderEntity createOrder(long orderStatusCd, boolean sapCarrier) {
		
		OrderEntity entity = new OrderEntity();
		entity.setOrderNumber(new Long(orderNumber++));
		OrderStatusBsEntity orderStatus = new OrderStatusBsEntity();
		orderStatus.setOrderStatusCd(orderStatusCd);
		entity.setOrderStatus(orderStatus );
		ArchivingStatusBsEntity archivingStatusBsEntity = new ArchivingStatusBsEntity();
		archivingStatusBsEntity.setArchivingStatusCd(Constant.ARCHIVING_STATUS_NEW);
		entity.setArchivingStatus(archivingStatusBsEntity);
		entity.setArchivedFlag(false);
		
		if (sapCarrier){
			CarrierEntity execCarrier = new CarrierEntity();
			entity.setExecCarrier(execCarrier);
		}
		
		return entity;
		
	}
	
}

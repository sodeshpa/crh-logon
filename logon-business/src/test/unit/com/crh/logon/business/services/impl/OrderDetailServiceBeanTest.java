/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.crh.logon.business.services.orderdetail.dto.BimodalTruckOrderItemDTO;
import com.crh.logon.business.stores.dictionaries.UnitOfMeasurementStoreInterface;
import com.crh.logon.business.stores.domain.AssignmentStoreInterface;
import com.crh.logon.business.stores.domain.OrderItemStoreInterface;
import com.crh.logon.business.stores.domain.OrderStoreInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

/**
 * @author AdNovum Informatik AG
 */
public class OrderDetailServiceBeanTest {

	OrderDetailServiceBean orderDetailService = new OrderDetailServiceBean();

	@Before
	public void setup() {
		orderDetailService.orderItemStore = mock(OrderItemStoreInterface.class);
		orderDetailService.orderStore = mock(OrderStoreInterface.class);
		orderDetailService.assignmentStore = mock(AssignmentStoreInterface.class);
		orderDetailService.unitOfMeasurementStore = mock(UnitOfMeasurementStoreInterface.class);
	}

	@Test
	public void testLoadBimodalTruckListReturnsEmptyListIfNoTruckItemFound() {

		List<OrderItemEntity> truckOrderItems = Collections.<OrderItemEntity>emptyList();

		long railOrderItemId = Mockito.anyLong();
		when (
			orderDetailService.orderItemStore.findBimodalTruckOrderItemByRailOrderItemId(railOrderItemId)
		).thenReturn(truckOrderItems);
		
		Long truckOrderItemId = -1L;
		List<BimodalTruckOrderItemDTO> bimodalTruckOrderItems
			= orderDetailService.loadBimodalTruckList(truckOrderItemId, Locale.ENGLISH.getLanguage());

		Assert.assertTrue(bimodalTruckOrderItems.isEmpty());

	}

	@Test
	public void testLoadBimodalTruckListFoundOneTruckOrderItem() {

		String language = Locale.ENGLISH.getLanguage();

		Long orderId = 2L;
		Long orderNumber = 11111111L;

		OrderEntity order = new OrderEntity();
		order.setOrderId(orderId);
		order.setOrderNumber(orderNumber);

		String uomCd = "DasCode";
		String uomTxt = "DasText";
		UomBsEntity uomBase = new UomBsEntity();
		uomBase.setUomCd(uomCd);
		UomTxEntity uomText = new UomTxEntity();
		uomText.setTxt(uomTxt);

		BigDecimal netQuantity = new BigDecimal("12.45");
		LoadEntity load = new LoadEntity();
		load.setLoadingTs(new Date());
		load.setNetQuantity(netQuantity);
		load.setNetQuantityUom(uomBase);

		OrderItemEntity truckOrderItem = new OrderItemEntity();
		truckOrderItem.setOrder(order);
		truckOrderItem.setLoad(load);

		List<OrderItemEntity> truckOrderItems = new ArrayList<OrderItemEntity>();
		truckOrderItems.add(truckOrderItem);

		String truckName = "MyLittleTruck";
		TruckEntity truck = new TruckEntity();
		truck.setTruckName(truckName);

		AssignmentEntity assignment = new AssignmentEntity();
		assignment.setTruck(truck);

		long railOrderItemId = Mockito.anyLong();
		when (
			orderDetailService.orderItemStore.findBimodalTruckOrderItemByRailOrderItemId(railOrderItemId)
		).thenReturn(truckOrderItems);
		when (
			orderDetailService.orderStore.findAndIntializeById(orderId)
		).thenReturn(order);
		when (
			orderDetailService.assignmentStore.findByOrderId(orderId)
		).thenReturn(assignment);
		when (
			orderDetailService.unitOfMeasurementStore.findText(uomBase.getUomCd(), language)
		).thenReturn(uomText);

		Long truckOrderItemId = 11L;
		List<BimodalTruckOrderItemDTO> bimodalTruckOrderItems 
			= orderDetailService.loadBimodalTruckList(truckOrderItemId, language);

		BimodalTruckOrderItemDTO bimodalTruckOrderItem = bimodalTruckOrderItems.get(0);

		Assert.assertEquals(orderId, bimodalTruckOrderItem.getOrderId());
		Assert.assertEquals(orderNumber, bimodalTruckOrderItem.getOrderNumber());
		Assert.assertEquals(truckName, bimodalTruckOrderItem.getTruckName());
		Assert.assertEquals(netQuantity, bimodalTruckOrderItem.getNetQuantity());
		Assert.assertEquals(uomTxt, bimodalTruckOrderItem.getUomTxt());
		Assert.assertTrue(bimodalTruckOrderItem.isAuthorized());

	}

}

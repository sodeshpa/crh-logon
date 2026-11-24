/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import com.crh.logon.business.services.utils.AssignmentStatusUtil;
import org.junit.Test;

import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Goal of this unit test is to test orderServiceBean.startLoad for landfill cases, which
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
public class StartLoadForLandfillTest extends BaseLandfillSetup {
	
	//TEST METHODS
	@Test
	public void testOrderStatus_case1_2_3_LoadByPdaAtCustomerSite() {
		source = EventSource.MOP;
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER;
		long expectedOrderStatus = Constant.ORDER_STATUS_LOADING;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		
		orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
		
		assertNotNull("order.orderStatus mustn't be null", order.getOrderStatus());
		assertNotNull("orderStatusCd mustn't be null",  order.getOrderStatus().getOrderStatusCd());
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after startLoad (initiated by pda at customer site)", 
				expectedOrderStatus, orderStatusCd);
	}
	
	@Test
	public void testOrderStatus_case1_WeighingDataProvidedByLdsAtCrhSite() {
		source = EventSource.LDS;
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		
		try {
			orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
			fail("not allowed startLoad call in this order status (arrived at unloading point or beyond)");
		}
		catch (LogonException e) {
			assertEquals("unexpected reason in exception", Reason.RES_ERROR_REVERSE_NOT_ALLOWED_STARTLOAD, e.getReason());
		}
	}
	
	@Test
	public void testOrderStatus_case2_WeighingDataProvidedByLoadingScreenAtCrhSite() {
		source = EventSource.LOGON;
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		
		try {
			orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
			fail("not allowed startLoad call in this order status (arrived at unloading point or beyond)");
		}
		catch (LogonException e) {
			assertEquals("unexpected reason in exception", Reason.RES_ERROR_REVERSE_NOT_ALLOWED_STARTLOAD, e.getReason());
		}
	}
	
	@Test
	public void testOrderStatus_case3_WeighingDataProvidedByPdaAtCrhSite() {
		source = EventSource.MOP;
		truck.setPdaAvailableFlag(true);
		long initialOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		long expectedOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		
		orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
		
		assertNotNull("order.orderStatus mustn't be null", order.getOrderStatus());
		assertNotNull("orderStatusCd mustn't be null",  order.getOrderStatus().getOrderStatusCd());
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after startLoad (initiated by pda at crh site)",
				expectedOrderStatus, orderStatusCd);
	}
	
	@Test
	public void testLoadNotNull_case4_LoadingByLogon_NoPda() {
		source = EventSource.LOGON;
		truck.setPdaAvailableFlag(false);
		order.getOrderStatus().setOrderStatusCd(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);
		
		load = null;
		
		try {
			orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
			fail("landfill orders mustn't have an empty load");
		}
		catch (LogonException e) {
			assertEquals("unexpected reason in exception", Reason.RES_ORDER_INSUFFICIENT_DATA, e.getReason());
		}
	}
	
	@Test
	public void testMultipleOrderItems_case4_LoadingByLogon_NoPda() {
		source = EventSource.LOGON;
		truck.setPdaAvailableFlag(false);
		order.getOrderStatus().setOrderStatusCd(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);
		
		List<OrderItemEntity> orderItems = order.getOrderItems();
		orderItems.add(new OrderItemEntity());
		
		try {
			orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
			fail("landfill order mustn't have multiple orderItems");
		}
		catch (LogonException e) {
			assertEquals("unexpected reason in exception", Reason.RES_ERROR_LANDFILL_MULTIPLE_ORDERITEMS, e.getReason());
		}
	}
	
	@Test
	public void test_case4_LoadingByLogon_NoPda() {
		source = EventSource.LOGON;
		truck.setPdaAvailableFlag(false);
		long initialOrderStatus = Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER;
		long expectedOrderStatus = Constant.ORDER_STATUS_UNLOADING;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		long expectedAssignmentStatus = AssignmentStatusUtil.ASSIGNMENT_STATUS_UNLOADING.getAssignStatusCd();
		long expectedAssignmentPartStatus = Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK;
		
		
		orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
		
		
		assertOrder(order, expectedOrderStatus);
		
		AssignmentPartEntity assignmentPartLoadType = orderServiceBean.getAssignmentPartLoad(load);
		assertAssignmentOfLoadAssignmentPart(assignmentPartLoadType, expectedAssignmentStatus);
		
		assertLoadAssignmentPart(assignmentPartLoadType, expectedAssignmentPartStatus);
	}
	
	@Test
	public void testOrderStatus_case5_LoadingByLds_NoPda() {
		source = EventSource.LDS;
		truck.setPdaAvailableFlag(false);
		long initialOrderStatus = Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER;
		long expectedOrderStatus = Constant.ORDER_STATUS_UNLOADING;
		order.getOrderStatus().setOrderStatusCd(initialOrderStatus);
		
		orderServiceBean.startLoad(event, load, loadingpoint, tareWeightT, source);
		
		assertNotNull("order.orderStatus mustn't be null", order.getOrderStatus());
		assertNotNull("orderStatusCd mustn't be null",  order.getOrderStatus().getOrderStatusCd());
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after startLoad (initiated by Logon/Loading Screen)", 
				expectedOrderStatus, orderStatusCd);
	}
	
	private void assertOrder(OrderEntity order, long expectedOrderStatus) {
		assertNotNull("order.orderStatus mustn't be null", order.getOrderStatus());
		assertNotNull("orderStatusCd mustn't be null",  order.getOrderStatus().getOrderStatusCd());
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		assertEquals("improper landfill order status after startLoad (initiated by Logon/Loading Screen)", 
				expectedOrderStatus, orderStatusCd);
	}
	
	private void assertAssignmentOfLoadAssignmentPart(AssignmentPartEntity assignmentPartLoadType, long expectedAssignmentStatus) {
		assertNotNull("load type assignmentPart must exist ", assignmentPartLoadType);
		
		AssignmentEntity assignment = assignmentPartLoadType.getAssignment();
		assertNotNull("assignment to load type assignmentPart must exist ", assignment);
		assertNotNull("assignment must have a status", assignment.getAssignStatus());
		assertNotNull("assignment must have a status code", assignment.getAssignStatus().getAssignStatusCd());
		
		assertEquals("assignment of assignmentPart (load type) must be in status unloading", 
				expectedAssignmentStatus, assignment.getAssignStatus().getAssignStatusCd().longValue());
	}
	
	private void assertLoadAssignmentPart(AssignmentPartEntity assignmentPartLoadType, long expectedAssignmentPartStatus) {
		assertNotNull("assignmentPart (load type) must exist ", assignmentPartLoadType);
		assertNotNull("assignmentPart (load type) must have a status", assignmentPartLoadType.getAssignPartStatus());
		assertNotNull("assignmentPart (load type) must have a status code", 
				assignmentPartLoadType.getAssignPartStatus().getAssignPartStatusCd());
		
		long assignmentPartLoadTypeStatus = assignmentPartLoadType.getAssignPartStatus().getAssignPartStatusCd();
		assertEquals("assignmentPart (load type) must be in transmitted_ack status",
				expectedAssignmentPartStatus, assignmentPartLoadTypeStatus);
	}
	
}

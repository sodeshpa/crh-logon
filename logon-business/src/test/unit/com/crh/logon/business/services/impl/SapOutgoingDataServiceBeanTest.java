/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;

/**
 * @spec /share/doc/crh/logon_2_2/spec/tech/latest/ifc_sap_send/released/20141120_Specification_Interface_DeliveryDocument_OUT_LRQ_041_V1_40.pdf
 * @author AdNovum Informatik AG
 */
public class SapOutgoingDataServiceBeanTest {
	private final String RETURN_INFIX = "R";
	
	private final Long ORDER_NUMBER = Long.valueOf(20008807);
	private final Long ORDER_ITEM_NUMBER = Long.valueOf(10);
	private final Long SAMPLE_ENTITY_ID = Long.valueOf(1);
	
	private final String LDS_DELIVERY_NOTE_NO = "LDS_NO";
	private final String EMPTY_STRING = "";
	private final String SEQUENTIAL = "sequential";

	MockSapOutgoingDataServiceBean sapOutgoingDataServiceBean = new MockSapOutgoingDataServiceBean();

	OrderEntity orderEntity;
	SareaEntity sareaEntity;

	Method getDispatchDeliveryNumberMethod;
	Method getExternalDeliveryNumber;
	
	String dispatchDeliveryNumber;
	String externalDeliveryNumber;

	@Before
	public void setup() throws NoSuchMethodException, SecurityException {
		orderEntity = new OrderEntity();
		orderEntity.setOrderId(Long.valueOf(SAMPLE_ENTITY_ID));
		orderEntity.setOrderNumber(ORDER_NUMBER);
		
		sareaEntity = new SareaEntity();
		sareaEntity.setSareaId(Long.valueOf(SAMPLE_ENTITY_ID));
		
		orderEntity.setSarea(sareaEntity);
		
		dispatchDeliveryNumber = EMPTY_STRING;
		externalDeliveryNumber = EMPTY_STRING;

		// Change the accessibility of getDispatchDeliveryNumber method to accessible = TRUE
		// Current accessibility is private
		getDispatchDeliveryNumberMethod = SapOutgoingDataServiceBean.class.getDeclaredMethod(
				"getDispatchDeliveryNumber", new Class[] { OrderEntity.class, boolean.class,
						CreditNoteEntity.class });
		getDispatchDeliveryNumberMethod.setAccessible(true);

		// Change the accessibility of getExternalDeliveryNumber method to accessible = TRUE
		// Current accessibility is protected
		getExternalDeliveryNumber = SapOutgoingDataServiceBean.class.getDeclaredMethod(
				"getExternalDeliveryNumber", new Class[] { OrderEntity.class });
		getExternalDeliveryNumber.setAccessible(true);
	}

	// CreditNote NOT NULL
	// ==> ORDER_NUMBER + RETURN_INFIX + ORDER_ITEM_NUMBER
	@Test
	public void testGetDispatchDeliveryNumberWithCreditNote() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		CreditNoteEntity creditNote = new CreditNoteEntity();
		creditNote.setCreditNoteId(SAMPLE_ENTITY_ID);
		
		OrderItemEntity orderItemEntity = new OrderItemEntity();
		orderItemEntity.setOrderItemId(SAMPLE_ENTITY_ID);
		orderItemEntity.setOrderItemNo(ORDER_ITEM_NUMBER);
		
		orderItemEntity.setOrder(orderEntity);
		creditNote.setOrderItem(orderItemEntity);

		dispatchDeliveryNumber = (String) getDispatchDeliveryNumberMethod.invoke(sapOutgoingDataServiceBean,
				new Object[] { orderEntity, true, creditNote });
		assertEquals(ORDER_NUMBER.toString() + RETURN_INFIX + ORDER_ITEM_NUMBER.toString(), dispatchDeliveryNumber);
	}
	
	// CreditNote NULL
	// ==> ORDER_NUMBER
	@Test
	public void testGetDispatchDeliveryNumberWithoutCreditNote() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		dispatchDeliveryNumber = (String) getDispatchDeliveryNumberMethod.invoke(sapOutgoingDataServiceBean,
				new Object[] { orderEntity, false, null });
		assertEquals(ORDER_NUMBER.toString(), dispatchDeliveryNumber);
	}
	
	// DelnoteUsesWsNumberFlag TRUE
	// lds NULL
	// sequential NULL
	// ==> LOGON_ORDER_NUMBER
	@Test
	public void testGetExternalDeliveryNumberCaseWithDelnoteFlagAndWithoutLdsAndWithoutSequential() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(true);
		orderEntity.setLdsDeliveryNoteNo(null);
		orderEntity.setOrderNumberFromSeqUsage(null);

		assertEquals(ORDER_NUMBER.toString(), getExternalDeliveryNumber(orderEntity));
	}
	
	// DelnoteUsesWsNumberFlag TRUE
	// lds NULL
	// sequential NOT NULL
	// ==> SEQUENTIAL
	@Test
	public void testGetExternalDeliveryNumberCaseWithDelnoteFlagAndWithoutLdsAndWithSequential() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(true);
		orderEntity.setLdsDeliveryNoteNo(null);
		orderEntity.setOrderNumberFromSeqUsage(SEQUENTIAL);

		assertEquals(SEQUENTIAL, getExternalDeliveryNumber(orderEntity));
	}
	
	// DelnoteUsesWsNumberFlag TRUE 
	// lds NOT NULL
	// sequential NULL
	// ==> LDS_DELIVERY_NOTE_NO
	@Test
	public void testGetExternalDeliveryNumberCaseWithDelnoteFlagAndWithLdsAndWithoutSequential() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(true);
		orderEntity.setLdsDeliveryNoteNo(LDS_DELIVERY_NOTE_NO);
		orderEntity.setOrderNumberFromSeqUsage(null);

		assertEquals(LDS_DELIVERY_NOTE_NO, getExternalDeliveryNumber(orderEntity));
	}
	
	// DelnoteUsesWsNumberFlag TRUE 
	// lds NOT NULL
	// sequential NOT NULL
	// ==> LDS_DELIVERY_NOTE_NO
	@Test
	public void testGetExternalDeliveryNumberCaseWithDelnoteFlagAndWithLdsAndWithSequential() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(true);
		orderEntity.setLdsDeliveryNoteNo(LDS_DELIVERY_NOTE_NO);
		orderEntity.setOrderNumberFromSeqUsage(SEQUENTIAL);

		assertEquals(LDS_DELIVERY_NOTE_NO, getExternalDeliveryNumber(orderEntity));
	}
	
	// DelnoteUsesWsNumberFlag FALSE
	// lds NULL
	// sequential NULL
	// ==> LOGON_ORDER_NUMBER
	@Test
	public void testGetExternalDeliveryNumberCaseWithoutDelnoteFlagAndWithoutLdsAndWithoutSequential() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(false);
		orderEntity.setLdsDeliveryNoteNo(null);
		orderEntity.setOrderNumberFromSeqUsage(null);

		assertEquals(ORDER_NUMBER.toString(), getExternalDeliveryNumber(orderEntity));
	}
	
	// DelnoteUsesWsNumberFlag FALSE
	// lds NULL
	// sequential NOT NULL
	// ==> SEQUENTIAL
	@Test
	public void testGetExternalDeliveryNumberCaseWithoutDelnoteFlagAndWithoutLdsAndWithSequential() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(false);
		orderEntity.setLdsDeliveryNoteNo(null);
		orderEntity.setOrderNumberFromSeqUsage(SEQUENTIAL);

		assertEquals(SEQUENTIAL, getExternalDeliveryNumber(orderEntity));
	}
	
	// DelnoteUsesWsNumberFlag FALSE
	// lds NOT NULL
	// sequential NULL
	// ==> LOGON_ORDER_NUMBER
	@Test
	public void testGetExternalDeliveryNumberCaseWithoutDelnoteFlagAndWithLdsAndWithoutSequential() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(false);
		orderEntity.setLdsDeliveryNoteNo(LDS_DELIVERY_NOTE_NO);
		orderEntity.setOrderNumberFromSeqUsage(null);
	
		assertEquals(ORDER_NUMBER.toString(), getExternalDeliveryNumber(orderEntity));
	}

	// DelnoteUsesWsNumberFlag FALSE
	// lds NOT NULL
	// sequential NOT NULL
	// ==> SEQUENTIAL
	@Test
	public void testGetExternalDeliveryNumberCaseWithoutDelnoteFlagAndWithLdsAndWithSequential() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		sareaEntity.setDelnoteUsesWsNumberFlag(false);
		orderEntity.setLdsDeliveryNoteNo(LDS_DELIVERY_NOTE_NO);
		orderEntity.setOrderNumberFromSeqUsage(SEQUENTIAL);

		assertEquals(SEQUENTIAL, getExternalDeliveryNumber(orderEntity));
	}
	
	private String getExternalDeliveryNumber(OrderEntity orderEntity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (String) getExternalDeliveryNumber.invoke(sapOutgoingDataServiceBean,
				new Object[] { orderEntity });
	}

	/** Override some methods to be able to test the Sap Outgoing Data Service */
	private static class MockSapOutgoingDataServiceBean extends SapOutgoingDataServiceBean {
		public MockSapOutgoingDataServiceBean() {
		}
	}
}
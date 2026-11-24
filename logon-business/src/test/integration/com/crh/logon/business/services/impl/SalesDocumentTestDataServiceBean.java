/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;

import com.crh.logon.business.services.interfaces.OrderTestDataService;
import com.crh.logon.business.services.interfaces.SalesDocumentTestDataService;
import com.crh.logon.business.services.interfaces.SapOutgoingDataService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class SalesDocumentTestDataServiceBean implements SalesDocumentTestDataService {
	@Inject
	OrderTestDataService orderTestDataService;

	@EJB
	SapOutgoingDataService sapOutgoingDataService;
	
	private static final int MAX_TEST_ORDER_ITEM_COUNT = 10;
	private static final String PARAM_ORDER_STATUS = "orderstatus";
	private static final String PARAM_PACKAGE_TYPE = "packagetype";
	
	private static final String QUERY_ORDERS_BY_MATERIAL = 
			"select orderitem from OrderItemEntity orderitem " + 
			"join orderitem.order o " + 
			"join o.orderStatus orderstatus " +
			"join orderitem.load load " + 
			"join orderitem.sapMaterial sapmaterial " + 
			"join sapmaterial.materialPackage materialpackage " + 
			"join materialpackage.logonPackageType logonpackagetype " + 
			"where logonpackagetype.packageTypeCd = :packagetype " + 
			"and orderstatus.orderStatusCd = :orderstatus " + 
			"order by o.actualDeliveryDat desc";

	private static final List<CreditNoteEntity> WITHOUT_CREDIT_NOTE = null;
	
	@PersistenceContext(unitName = "logon")
	protected EntityManager em;
	
	public List<SalesDocumentDTO> getBagOrderSalesDocumentSampleData() {
		TypedQuery<OrderItemEntity> query =  em.createQuery(QUERY_ORDERS_BY_MATERIAL, OrderItemEntity.class);
		
		query.setParameter(PARAM_PACKAGE_TYPE, Constant.PACKAGE_TYPE_BBAG);
		query.setParameter(PARAM_ORDER_STATUS, Constant.ORDER_STATUS_DELIVERED);
		query.setMaxResults(MAX_TEST_ORDER_ITEM_COUNT);
			
		List<OrderItemEntity> orderItems = query.getResultList();
		List<SalesDocumentDTO> salesDocuments = new ArrayList<SalesDocumentDTO>();
		
		for (OrderItemEntity orderItem : orderItems) {
			OrderEntity order = orderItem.getOrder();
			Hibernate.initialize(order.getOrderItems());
			
			SalesDocumentDTO salesDocument = sapOutgoingDataService.getSalesDocument(order,
					WITHOUT_CREDIT_NOTE);
			salesDocuments.add(salesDocument);
		}
		
		return salesDocuments;
	}

	public SalesDocumentDTO getDummySalesDocument() {
		OrderEntity testOrder = orderTestDataService.createOrderForTransferDeliveryDocumentGenerationTesting();
		CreditNoteEntity creditNote = new CreditNoteEntity();
		List<OrderItemEntity> orderItems = testOrder.getOrderItems();
		OrderItemEntity theOrderItem = orderItems.get(0);
		creditNote.setOrderItem(theOrderItem);
		List<CreditNoteEntity> noteList = new ArrayList<CreditNoteEntity>();
		noteList.add(creditNote);
		return sapOutgoingDataService.getSalesDocument(testOrder, noteList);
	}
	
}

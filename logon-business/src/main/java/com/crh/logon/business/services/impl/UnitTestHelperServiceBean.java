/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import javax.ejb.Stateless;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.UnitTestHelperServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.UnitTestHelperServiceLocalInterface;


/**
 * This serviceBean is responsible for operations related to JUnit Tests. 
 * Specifically for LogonToMopTestCase
 * 
 * @author AdNovum Informatik AG
 */
@Stateless
public class UnitTestHelperServiceBean extends UnitTestHelperServiceDispatcher 
	implements UnitTestHelperServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RFIDServiceBean.class);
	
	/**
	 * sets the assignment to inactive and unlinks it from the load if it has any,
	 * sets the order back to state: transmitted to carrier
	 * mandatory event fields:
	 * 	- assignment
	 *  - truck
	 */
	
	/**
	 * Fix me: entities from before the refactoring
	 */
	/*
	public void removeAssignmentFromTestOrder(Long orderNr) {
		// [[
		Object[][] params = {{"orderNr", orderNr}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeAssignmentFromTestOrder", params);
		}
		// ]]
		try {
			// find Assignment Entity
			OrderEntityDao orderDao = getDaoProvider().getOrderEntityDao();
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNr));			
			OrderEntity order = orderDao.findSingle(f);
			AssignmentEntityDao assignmentDao = getDaoProvider().getAssignmentEntityDao();
			f = new FilterImpl();
			f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ORDER_ID, order.getOrderId()));
			f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			List assignmentList = assignmentDao.find(f);
			
			for (Iterator i2 = assignmentList.iterator(); i2.hasNext();) {
				AssignmentEntity assignment = (AssignmentEntity)i2.next();
				
				// remove from tlgn_load
				LoadEntityDao loadDao = getDaoProvider().getLoadEntityDao();
				FilterImpl f1 = new FilterImpl();
				f1.addExpression(Restrictions.eq(LoadEntity.ATTR_ASSIGNMENT_ID, assignment.getAssignmentId()));			
				LoadEntity load = loadDao.findSingle(f1);
				if (load != null){
					if (load.getLoadingTimestamp() == null){
						loadDao.remove(load);
					}
					else{
						load.setAssignment(null);
					}
				}
				
				// reset order status
				if (assignment.getOrder() != null){
					changeOrderStatus(assignment.getOrder(), new Long(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER));
				}
				
				// set assignment inactive
				assignment.setActiveFlag(Boolean.FALSE);
				assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_CANCELLED);
				
				// set assignment parts inactive
				AssignPartStatusBsEntity partStatus = new AssignPartStatusBsEntity();
				partStatus.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_NEW);
				
				Iterator i = assignment.getAssignmentParts().iterator();
	            while (i.hasNext()){
	            	AssignmentPartEntity part = (AssignmentPartEntity)i.next();
	                part.setAssignPartStatus(partStatus);
	            }
			}
            
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	*/
	/**
	 * Updates TLGNC_ORDER and TLGNC_ORDER_STATUS
	 */
	/*
	private void changeOrderStatus(OrderEntity order, Long statusCd) {
		// [[
		Object[][] params = {{"order", order},
							{"statusCd", statusCd + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "changeOrderStatus", params);
		}
		// ]]
		try {
			
			if (order == null) throw new IllegalArgumentException("order cannot be null!");
			OrderEntityDao orderDao = getDaoProvider().getOrderEntityDao();
			
			long currentStatus = order.getOrderStatus().getOrderStatusCd().longValue();
			
			if (currentStatus == statusCd){
				return; //nothing to do
			}
			
			OrderStatusBsEntity newStatusRef = getRefDataGenericsService().findBsEntity(OrderStatusBsEntity.class, statusCd);
			order.setOrderStatus(newStatusRef);
									
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public boolean existsActiveAssignmentToOrderNr(Long orderNr){
		// [[
		Object[][] params = {{"orderNr", orderNr}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeAssignmentFromTestOrder", params);
		}
		// ]]
		try {
			OrderEntityDao orderDao = getDaoProvider().getOrderEntityDao();
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNr));			
			OrderEntity order = orderDao.findSingle(f);
			
			AssignmentEntityDao assignmentDao = getDaoProvider().getAssignmentEntityDao();
			f = new FilterImpl();
			f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ORDER_ID, order.getOrderId()));
			f.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			
			AssignmentEntity assignment = assignmentDao.findSingle(f);
			if (assignment == null){
				return false;
			}
			return true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	*/
	
}

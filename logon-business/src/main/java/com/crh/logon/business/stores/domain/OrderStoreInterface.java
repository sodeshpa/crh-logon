/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.business.ordercreation.dto.OrderDeliveryMinMaxDateDTO;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface OrderStoreInterface extends GenericCrudProviderInterface<OrderEntity> {

	/**
	 * @param orderId - primary key used for the lookup
	 * @return initialized entity
	 */
	OrderEntity findAndIntializeById(Long orderId);

	/**
	 * It changes the related order's status by the following rules:
	 * 	if the order's status is ORDER_STATUS_FINISHED or
	 *  ORDER_STATUS_DELETED then the archiving_status_cd will be changed to DELETABLE (since ARCHIVED status was removed)
	 *  
	 *  if there is any other order status then it will throw a LogonException
	 *  
	 * @param archivableOrderId This contains all of the data that we need to archive set the new order's status properly
	 * @exception LogonException This will be throwed with Reason.RES_IMPL_BUG if the order's status doesn't matching the rules 
	 */
	public void markOrderAsArchived(Long archivableOrderId);
	
	/**
	 * If the archiving process fails the archiving_status will be NON_ARCHIVABLE
	 * @param archivableOrderId This contains all of the data that we need to archive set the new order's status properly
	 * @exception LogonException This will be throwed with Reason.RES_IMPL_BUG if the order's status doesn't matching the rules 
	 */
	public void markOrderAsNonArchivable(Long archivableOrderId);

	
	/**
	 * It removes the related data of the given order from the database.
	 * Be careful to use it, it does physical (not logical) deletion from the database
	 * 
	 * @param order All of the data which needs for remove a given order
	 */
	
	public void removeOrder(Long orderId);	
	
	/**
	 * Sets the related order's archiving status to DELETABLE
	 * @param orderId
	 */
	public void markOrderAsDeletable(Long orderId);
	
	/**
	 * Sets the related order's archiving status to DELETION FAILED
	 * @param orderId
	 */
	public void markOrderAsDeletionFailed(Long orderId);
	
	
	/**
	 * It retrieves the Min(OrderEntity.DeliveryFromDat) and
	 *  Max(OrderEntity.DeliveryToDat) from OrderEntity group by CustumerId
	 * 
	 * @param custorderId
	 * @param orderId
	 * @return
	 */
	public OrderDeliveryMinMaxDateDTO findMinMaxOrderDeliveryFromToDate(Long custorderId, Long orderId);

	/**
	 * Returns the number of active assignments for the given order id.
	 * @param orderId
	 * @return
	 */
	public long getCountAssignmentForOrderId(Long orderId);
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.persistence.entities.common.ArchivingLogEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface ArchivingLogStoreInterface extends GenericCrudProviderInterface<ArchivingLogEntity> {

	
	/**
	 * It starts to archive the given order
	 */
	public void startArchiving(Long orderId);
	
	/**
	 * By first step we send all of the related data to the archiving service. sourceMessage has to contain the message which will be send except the 
	 * pdf's body because of the entry size
	 */
	public void transmitDataToArchivingService(Long orderId, String sourceMessage);
	
	/**
	 * Archiving system sends a response every time and this respone has to be stored in the responseMessage
	 */
	public void receivedResponseFromArchivingService(Long orderId, String responseMessage);
	
	public void unableToDeleteOrder(Long orderId, String message);

	public void wholeBatchFailed();
}

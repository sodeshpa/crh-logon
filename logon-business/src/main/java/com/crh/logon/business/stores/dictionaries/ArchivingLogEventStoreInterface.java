/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.dictionaries;


import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.refdata.ArchivingLogEventBsEntity;

@Local
public interface ArchivingLogEventStoreInterface extends GenericReadOnlyInterface<ArchivingLogEventBsEntity> {
	
	/**
	 * It returns with the entity instance of the StartArchiving from the ArchivingLogEvents  
	 *
	 */
	public ArchivingLogEventBsEntity getStartArchivingEvent();
	
	/**
	 * It returns with the entity instance of the TransmitToArchivingService from the ArchivingLogEvents  
	 *
	 */
	public ArchivingLogEventBsEntity getTransmitToArchivingServiceEvent();
	
	/**
	 * It returns with the entity instance of the ReceiveResponseFromArchivingService from the ArchivingLogEvents  
	 *
	 */
	public ArchivingLogEventBsEntity getReceiveResponseFromArchivingServiceEvent();

	public ArchivingLogEventBsEntity getUnableToDeleteOrderServiceEvent();
	
	public ArchivingLogEventBsEntity getWholeBatchFailedServiceEvent();
}

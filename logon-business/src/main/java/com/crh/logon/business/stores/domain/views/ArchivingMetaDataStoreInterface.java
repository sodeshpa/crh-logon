package com.crh.logon.business.stores.domain.views;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.views.ArchivingMetadataEntity;

@Local
public interface ArchivingMetaDataStoreInterface extends GenericReadOnlyInterface<ArchivingMetadataEntity> {

	/**
	 * It finds all of the informations which are necessary for archiving orders
	 * 
	 * @param maximumRecordCount This defines the maximum record count of the result
	 * @param actualDeliveryDate This defines the actual delivery date which will be used for filtering the archivable orders
	 * @return List<ArchivingMetadataEntity> The result contains all of the informations of the archivable order's 
	 */
	public List<ArchivingMetadataEntity> findDataOfArchivableOrders(Integer maximumRecordCount, Date actualDeliveryDate);
}

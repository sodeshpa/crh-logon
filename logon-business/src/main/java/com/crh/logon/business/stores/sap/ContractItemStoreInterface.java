package com.crh.logon.business.stores.sap;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;

@Local
public interface ContractItemStoreInterface extends GenericReadOnlyInterface<SapContractItemEntity> {

	/**
	 * Returns a list of {@link SapContractItemEntity} objects for the given ids.
	 * @param sapContractItemIds
	 * @return
	 */
	public List<SapContractItemEntity> findSapContractItemsById(Set<Long> sapContractItemIds);

	/**
	 * Returns a list of {@link SapContractItemEntity} objects for the given ids.
	 * @param sapContractItemIds
	 * @return
	 */
	public List<SapContractItemEntity> findSapContractItemsById(List<Long> sapContractItemIdList);
	
}

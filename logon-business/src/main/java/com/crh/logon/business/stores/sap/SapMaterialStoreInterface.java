package com.crh.logon.business.stores.sap;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

@Local
public interface SapMaterialStoreInterface extends GenericReadOnlyInterface<SapMaterialEntity> {

	
	/**
	 * Returns a list of {@link SapMaterialEntity} objects for the given 
	 * ids. 
	 * @param sapMaterialIds
	 * @return
	 */
	public List<SapMaterialEntity> findSapMaterialsById(Set<Long> sapMaterialIds);
	
	/**
	 * Returns a list of {@link SapMaterialEntity} objects for the given 
	 * ids. 
	 * @param sapMaterialIds
	 * @return
	 */
	public List<SapMaterialEntity> findSapMaterialsById(List<Long> sapMaterialIdList);

}

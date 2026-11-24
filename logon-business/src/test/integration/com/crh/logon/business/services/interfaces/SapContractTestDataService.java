package com.crh.logon.business.services.interfaces;

import java.util.List;

import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;

public interface SapContractTestDataService {

	/**
	 * Retrieves all contracts belonging to the specified sales area.
	 * Also fetches the corresponding contract items from the database.
	 * 
	 * @param salesAreaLongDisplayName
	 * @return
	 */
	List<SapContractEntity> getTestContractsBySalesArea(
			String salesAreaLongDisplayName);

	/**
	 * Sets the flag of the given sales area, persists it.
	 * 
	 * @param salesArea
	 * @param value
	 * @return the updated sales area
	 */
	SareaEntity setAllowPlanningMaterialFlag(SareaEntity salesArea, boolean value);

	/**
	 * Deletes all contracts and it's contract items belonging to the specified sales area.
	 * 
	 * @param salesAreaLongDisplayName
	 */
	void deleteContractsFromSalesArea(String salesAreaLongDisplayName);

}
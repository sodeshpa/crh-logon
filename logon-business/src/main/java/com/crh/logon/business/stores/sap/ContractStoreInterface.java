package com.crh.logon.business.stores.sap;

import java.util.List;
import java.util.Set;
import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.sap.SapContractEntity;

@Local
public interface ContractStoreInterface extends GenericReadOnlyInterface<SapContractEntity> {
	/**
	 * Retrieves all contract belonging to the specified sales area.
	 * 
	 * Does not initialize the contract items!
	 * 
	 * @param salesAreaIdentifier
	 * @return
	 */
	List<SapContractEntity> findBySalesAreaIdentifier(Long salesAreaIdentifier);

	/**
	 * Fetches all contracts for the given shipTo & soldTo pair.
	 * <p>
	 * The <em>filterOutExpired</em> flag is set by callers who need the expired contracts
	 * filtered out.
	 * <p>
	 *
	 * @return List<Long> of filtered contracts
	 */
	Set<Long> fetchContractsForShiptoSareaSoldto(
			Long salesAreaId,
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			boolean filterOutFutureContracts);

	/**
	 * Fetches all contract entities for the given shipTo, soldTo and contractNumber.
	 * <p>
	 * The <em>filterOutExpired</em> flag is set by callers who need the expired contracts
	 * filtered out.
	 * <p>
	 *
	 * @return List<SapContractEntity> of filtered contract entities
	 */
	Set<SapContractEntity> fetchContractEntitiesForShiptoSoldtoContractNo(
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			String contractNumber,
			boolean filterOutFutureContracts);

	/**
	 * Fetches all contract entities for the given shipTo, soldTo, contractNumber and SorgCd.
	 * <p>
	 * The <em>filterOutExpired</em> flag is set by callers who need the expired contracts
	 * filtered out.
	 * <p>
	 *
	 * @return List<SapContractEntity> of filtered contract entities
	 */
	Set<SapContractEntity> fetchContractEntitiesForShiptoSoldtoContractNoSorgCd(
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			String contractNumber,
			String sorgCd,
			boolean filterOutFutureContracts);

	SapContractEntity getSapContractByContractNumber(String contractNumber);
}

package com.crh.logon.business.stores.sap;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.refdata.SapContractTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

@Local
public interface ContractTypeStoreInterface extends GenericReadOnlyInterface<UomTxEntity> {

	/**
	 * It returns with the contract type texts based on the given ids and locale setting 
	 * @param contractTypeTextIds
	 * @param locale
	 * @return List <SapContractTypeTxEntity>
	 */
	public List<SapContractTypeTxEntity> fetchTexts(Set<String> contractTypeTextIds, Locale locale);
}

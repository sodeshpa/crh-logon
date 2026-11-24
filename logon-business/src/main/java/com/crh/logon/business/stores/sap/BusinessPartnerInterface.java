package com.crh.logon.business.stores.sap;

import java.util.List;
import java.util.Set;
import javax.ejb.Local;

import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

@Local
public interface BusinessPartnerInterface extends GenericReadOnlyInterface<UomTxEntity>{
	
	/**
	 * Loads all SAP BPs for a given set of IDs that have the is_payer flag set and that are not
	 * marked for deletion. 
	 * @param businessPartnerIds
	 * @return List<SapBpEntity>
	 */
	List<SapBpEntity> fetchPayers(Set<Long> businessPartnerIds);

	SapBpEntity getBilltoSapBpEntityBySapIfcId(String sapIfcId);

	ShiptoEntity getShiptoEntityBySapIfcId(String sapIfcId);

	SapBpEntity getPayerSapBpEntityBySapId(String sapIfcId);

	SapBpEntity getSoldToSapBpEntityBySapIfcId(String sapIfcId);

	List<ShiptoBilltoEntity> loadBilltos(Long shiptoId, Long sareaId);

	/**
	 * Loads a list of Payer SapBpEntities belonging to the specified Sold To and Sales Area.
	 *
	 * @param sareaId
	 * @param soldtoSapBpId
	 * @adnlog.exclude return
	 */
	@AdnTrace
	public List<SapBpEntity> loadPayers(Long sareaId, Long soldtoSapBpId);
}

package com.crh.logon.business.stores.sap;

import java.util.List;
import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;

@Local
public interface ShiptoSareaStoreInterface extends GenericReadOnlyInterface<ShiptoSareaEntity>{

	/**
	 * Returns the {@link ShiptoSareaEntity} for the given sap bp and sarea parameters,
	 * but only in case it belongs to an active {@link ShiptoEntity}
	 * @param shiptoSapBpEntity
	 * @param sarea
	 * @return
	 */
	public ShiptoSareaEntity findShiptoSareaWithActiveShipto(final SapBpEntity shiptoSapBpEntity,
			final SareaEntity sarea);

	/**
	 * Loads the shipto sales area by all means. (Not considering sold-to or  partner functions
	 * between them.)
	 */
	public ShiptoSareaEntity getShiptoSarea(Long shiptoId, Long sareaId);

	/**
	 * Returns a list of {@link SoldshipSareaFlexSrchEntity} object for the provided parameters. Each
	 * parameter is used as filter
	 *
	 * In case all 3 parameters specified -> if there is no match that means that the shipto
	 * business partner, the soldto business partner is not each other's partner according to SAP data
	 * in the sales area specified.
	 * @param shipTo
	 * @param soldTo
	 * @param sarea
	 * @return
	 */
	public List<SoldshipSareaFlexSrchEntity> getCommonSalesAreasForShiptoSoldto(ShiptoEntity shipTo, SapBpEntity soldTo,
			SareaEntity sarea);
}

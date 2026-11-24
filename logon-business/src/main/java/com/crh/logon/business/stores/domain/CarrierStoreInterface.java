/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.domain;

import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.CarrierEntity;

@Local
public interface CarrierStoreInterface extends GenericReadOnlyInterface<CarrierEntity> {

	/**
	 * Fetches a list of {@code CarrierEntity}s where the poolId equals to the parameter 
	 * and the vendor is a commercial carrier
	 * @param poolId - filter clause
	 * @return result list of entities if none found empty list
	 */
	List<CarrierEntity> findCommercialCarriersByPoolId(Long poolId);

	/**
	 * Returns the Carrier which belongs to the vendor which has the given vendorSapIfcId.
	 * @param vendorSapIfcId - SAP vendor id (the one from SAP)
	 */
	public CarrierEntity findCarrierEntityByVendorSapIfcId(String vendorSapIfcId);
}

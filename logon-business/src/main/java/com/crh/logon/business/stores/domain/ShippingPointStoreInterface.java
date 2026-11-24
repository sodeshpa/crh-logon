/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.List;
import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface ShippingPointStoreInterface extends GenericCrudProviderInterface<ShippingpointEntity> {

	/**
	 * Fetches a list of {@code ShippingpointEntity}s where the plantCode matches the one given 
	 * @param plantCd - filter clause
	 * @return entities found or empty list
	 */
	List<ShippingpointEntity> findShippingPointsByPlantCd(String plantCd);
	
	
	/**
	 * Returns the value of {@link ShippingpointEntity#ATTR_FVL_MIX_SHIPTO_PER_WAGON_FLAG}
	 * for the requested shippingpoint.
	 * @param shippingpointId
	 * @return
	 */
	Boolean getShippingpointFvlFlag(Long shippingpointId);

	/**
	 * Returns a list of {@link ShippingpointDTO} entries.
	 * @return
	 */
	List<ShippingpointDTO> fetchShippingpointList();

	ShippingpointEntity getShippingpointEntityByPlantCdAndSapNo(PlantBsEntity supplyPlant, String sapShippingPointNo);
}

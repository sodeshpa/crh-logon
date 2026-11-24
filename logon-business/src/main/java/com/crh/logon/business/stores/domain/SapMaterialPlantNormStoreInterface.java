/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;

/**
 * @author AdNovum Informatik AG
 */

@Local
public interface SapMaterialPlantNormStoreInterface extends GenericReadOnlyInterface<SapMatPlantNormEntity> {

	/**
	 * Gets all the SAP logos from TLGNS_SAP_MAT_PLANT_NORM table.
	 * The returned list will contain:
		a) Exact match (Production Plant, Material Sales Organization)
		b) Entries which match (Production Plant, Material), and have sorg_cd == null
	 */
	public List <SapMatPlantNormEntity> getSapMatPlantNormList(MatPlantEntity matPlantEntity, OrderEntity order);
}

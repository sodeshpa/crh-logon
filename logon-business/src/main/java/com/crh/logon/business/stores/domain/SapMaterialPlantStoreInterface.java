/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface SapMaterialPlantStoreInterface extends GenericReadOnlyInterface<SapMaterialPlantEntity> {

	public SapMaterialPlantEntity findByMaterialAndPlant(PlantBsEntity plant , SapMaterialEntity sapMaterialEntity);
}

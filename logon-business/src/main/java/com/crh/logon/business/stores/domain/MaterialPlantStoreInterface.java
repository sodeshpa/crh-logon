/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * @author AdNovum Informatik AG
 */
public interface MaterialPlantStoreInterface extends GenericReadOnlyInterface<MatPlantEntity> {

	public MatPlantEntity getMaterialPlantFromOrder(OrderEntity order);
	
	public MatPlantEntity getMaterialPlantByMaterialIdAndPlantCd(Long sapMaterialId, String plantCd);
}

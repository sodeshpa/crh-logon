/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class SapMaterialPlantStore extends GenericReadOnlyProvider<SapMaterialPlantEntity> implements SapMaterialPlantStoreInterface {

	public SapMaterialPlantEntity findByMaterialAndPlant(PlantBsEntity plant , SapMaterialEntity sapMaterialEntity) {
		ReadOnlyDao<SapMaterialPlantEntity> sapDao = getDaoProvider().getSapMaterialPlantEntityDao();
		Filter<SapMaterialPlantEntity> sapFilter = sapDao.createFilter();
		
		sapFilter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT, plant));
		sapFilter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL,sapMaterialEntity ));
		
		SapMaterialPlantEntity sapMaterialPlant = sapDao.findSingle(sapFilter);
		
		return sapMaterialPlant;
	}
}

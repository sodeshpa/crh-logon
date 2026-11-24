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
import com.crh.logon.persistence.entities.common.PlantCompcodeEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class PlantCompcodeStore extends GenericReadOnlyProvider<PlantCompcodeEntity> implements PlantCompcodeStoreInterface {

	public PlantCompcodeEntity getCodeForPlant(PlantBsEntity plant) {
		ReadOnlyDao<PlantCompcodeEntity> plantCompcodeEntityDao = getDaoProvider().getPlantCompcodeEntityDao();
		Filter<PlantCompcodeEntity> plantCompcodeEntityFilter = plantCompcodeEntityDao.createFilter();
		
		plantCompcodeEntityFilter.addExpression(Restrictions.eq(PlantCompcodeEntity.ATTR_PLANT, plant));
		
		PlantCompcodeEntity companyCode = plantCompcodeEntityDao.findSingle(plantCompcodeEntityFilter);
		return companyCode;
	}
}

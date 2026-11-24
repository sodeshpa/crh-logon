package com.crh.logon.business.stores.sap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class SapMaterialStore extends GenericReadOnlyProvider<SapMaterialEntity> implements SapMaterialStoreInterface {

	public List<SapMaterialEntity> findSapMaterialsById(Set<Long> sapMaterialIds) {

		List<Long> sapMaterialIdList = new ArrayList<Long>();
		sapMaterialIdList.addAll(sapMaterialIds);
		
		return findSapMaterialsById(sapMaterialIdList);
	}

	public List<SapMaterialEntity> findSapMaterialsById(List<Long> sapMaterialIdList) {
		
		final ReadOnlyDao<SapMaterialEntity> sapMaterialEntityDao = getDaoProvider().getSapMaterialEntityDao();
		
		Filter<SapMaterialEntity> matfilter = sapMaterialEntityDao.createFilter();
		final Expression inRestriction = DbUtils.createSplittedInRestriction(SapMaterialEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIdList);
		matfilter.addExpression(inRestriction);
		
		final List<SapMaterialEntity> sapMaterials = sapMaterialEntityDao.find(matfilter);
		
		return sapMaterials;
	}
}

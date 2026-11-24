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
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ContractItemStore extends GenericReadOnlyProvider<SapContractItemEntity> implements ContractItemStoreInterface {

	public List<SapContractItemEntity> findSapContractItemsById(Set<Long> sapContractItemIds) {
		
		List<Long> sapContractItemIdList = new ArrayList<Long>();
		sapContractItemIdList.addAll(sapContractItemIds);
		
		return findSapContractItemsById(sapContractItemIdList);
	}

	public List<SapContractItemEntity> findSapContractItemsById(List<Long> sapContractItemIdList) {
		
		final ReadOnlyDao<SapContractItemEntity> sapContractItemEntityDao = getDaoProvider().getSapContractItemEntityDao();
		
		final Expression inRestriction = DbUtils.createSplittedInRestriction(SapContractItemEntity.ATTR_SAP_CONTRACT_ITEM_ID, sapContractItemIdList);
		
		Filter<SapContractItemEntity> filter = sapContractItemEntityDao.createFilter();
		filter.addExpression(inRestriction);
		
		final List<SapContractItemEntity> sapContractItems = sapContractItemEntityDao.find(filter);
		
		return sapContractItems;
	}
}

package com.crh.logon.business.stores.domain.views;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.ArchivingMetadataEntity;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

@Stateless
@AdnTrace
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ArchivingMetaDataStore extends GenericReadOnlyProvider<ArchivingMetadataEntity> implements ArchivingMetaDataStoreInterface{

	public List<ArchivingMetadataEntity> findDataOfArchivableOrders(Integer maximumRecordCount, Date lastModificationTime) {		
		
		ReadOnlyDao<ArchivingMetadataEntity> metaDataDao = getDaoProvider().getArchivingMetadataEntityDao();
		Filter<ArchivingMetadataEntity> orderFilter = new FilterImpl<ArchivingMetadataEntity>();
		orderFilter.addExpression(
				Restrictions.and(
						Restrictions.or(
								Restrictions.eq(ArchivingMetadataEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_FINISHED),
								Restrictions.eq(ArchivingMetadataEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_DELETED)
						),
						Restrictions.lt(ArchivingMetadataEntity.ATTR_LAST_BIZEVENT_TIME, lastModificationTime)
				)
		);		
		
		return metaDataDao.findNRecs(0, maximumRecordCount, orderFilter);
	}

}

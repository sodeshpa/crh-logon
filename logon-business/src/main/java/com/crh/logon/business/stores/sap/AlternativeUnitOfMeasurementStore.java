package com.crh.logon.business.stores.sap;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class AlternativeUnitOfMeasurementStore extends GenericReadOnlyProvider<UomTxEntity> implements AlternativeUnitOfMeasurementInterface{
	
	public List<SapMaterialAltUomEntity> fetchForMaterials(List<Long> sapMaterials) {
			final ReadWriteDao<SapMaterialAltUomEntity> sapMaterialAltUomEntityDao = getDaoProvider().getSapMaterialAltUomEntityDao();
			final Filter<SapMaterialAltUomEntity> filter = sapMaterialAltUomEntityDao.createFilter();
			filter.addExpression(Restrictions.in(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL_ID, sapMaterials));
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM + "." + UomBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
						
			return sapMaterialAltUomEntityDao.find(filter);
	}
	
	public List<SapMaterialAltUomEntity> fetchForMaterialsWithWebOrderOnly(List<Long> sapMaterials) {
			final ReadWriteDao<SapMaterialAltUomEntity> sapMaterialAltUomEntityDao = getDaoProvider().getSapMaterialAltUomEntityDao();
			final Filter<SapMaterialAltUomEntity> filter = sapMaterialAltUomEntityDao.createFilter();
			filter.addExpression(Restrictions.in(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL_ID, sapMaterials));
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM + "." + UomBsEntity.ATTR_AVAILABLE_IN_WEB_ORDER_FLAG, Boolean.TRUE));
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM + "." + UomBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
						
			return sapMaterialAltUomEntityDao.find(filter);
	}

	public SapMaterialAltUomEntity fetchByMaterialAndUnitOfMeasureCode(Long materialId,
			String unitOfMeasureCode) {
		final ReadWriteDao<SapMaterialAltUomEntity> sapMaterialAltUomEntityDao = getDaoProvider().getSapMaterialAltUomEntityDao();
		final Filter<SapMaterialAltUomEntity> filter = sapMaterialAltUomEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL_ID,
				materialId));
		filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM + "."
				+ UomBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM_CD, unitOfMeasureCode));
		List<SapMaterialAltUomEntity> foundRecords = sapMaterialAltUomEntityDao.findNRecs(0, 1, filter);
		/**
		 * Only return the first record, because:
		 * - material and unit of measure are a unique combination
		 * - we passed 1 as limit parameter
		 */
		return foundRecords.get(0);
	}
}

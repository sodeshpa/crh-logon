/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class MaterialPlantStore extends  GenericReadOnlyProvider<MatPlantEntity> implements MaterialPlantStoreInterface {

	@Override
	public MatPlantEntity getMaterialPlantFromOrder(OrderEntity order) {
		MatPlantEntity matPlantEntity = null;
		
		for (OrderItemEntity orderItem : order.getOrderItems()) {
			if (matPlantEntity == null) {
				matPlantEntity = getMaterialPlantByMaterialIdAndPlantCd(
						orderItem.getSapMaterial().getSapMaterialId(),
						orderItem.getProductionPlant().getPlantCd());
				break;
			}
		}
		
		return matPlantEntity;
	}

	@Override
	public MatPlantEntity getMaterialPlantByMaterialIdAndPlantCd(Long sapMaterialId, String plantCd) {
		ReadWriteDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
		Filter<MatPlantEntity> filter = matPlantDao.createFilter();
		
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCd));
		
		final MatPlantEntity matPlantEntitiy = matPlantDao.findSingle(filter);
		return matPlantEntitiy;
	}

}

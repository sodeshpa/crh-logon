/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ShippingPointStore extends GenericCrudProvider<ShippingpointEntity> implements ShippingPointStoreInterface {

	public List<ShippingpointEntity> findShippingPointsByPlantCd(String plantCode) {
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<ShippingpointEntity> shippingPointDao = daoProvider.getShippingpointEntityDao();
		Filter<ShippingpointEntity> filter = shippingPointDao.createFilter();
		filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT_CD, plantCode));
		return shippingPointDao.find(filter);
	}

	public Boolean getShippingpointFvlFlag(Long shippingpointId) {
		Filter<ShippingpointEntity> shippingpointFilter = new FilterImpl<ShippingpointEntity>();
		shippingpointFilter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID, shippingpointId));

		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<ShippingpointEntity> dao = daoProvider.getShippingpointEntityDao();

		JPAQuery<ShippingpointEntity> query = dao.createJPAQuery();
		query.setFilter(shippingpointFilter);
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_FVL_MIX_SHIPTO_PER_WAGON_FLAG));
		query.setDistinct(true);
	
		return (Boolean) dao.findSingle(query); 
	}
	
	public List<ShippingpointDTO> fetchShippingpointList() {
		
		ReadWriteDao<ShippingpointEntity> shippingpointEntityDao = getDaoProvider().getShippingpointEntityDao();
		JPAQuery<ShippingpointEntity> query = shippingpointEntityDao.createJPAQuery();
		Filter<ShippingpointEntity> filter = shippingpointEntityDao.createFilter();
		
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID));
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_PLANT_CD));
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_PLANT_NAME));
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_SHIPPPOINT_TYPE_CD));
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_RAILWAY_STATION_ID));
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_COORD_LAT));
		query.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_COORD_LON));
		filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
		
		filter.addExpression(Order.asc(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));
		
		query.setFilter(filter);
		
		List<Object[]> shippingpointDataResult = shippingpointEntityDao.find(query);
		
		List<ShippingpointDTO> shippingpointDTOs = new ArrayList<ShippingpointDTO>();
		for (Object [] shippingpointData : shippingpointDataResult) {
			ShippingpointDTO shippingpointDTO = new ShippingpointDTO(
					(Long)shippingpointData[0], 
					(String) shippingpointData[1],
					(String) shippingpointData[2],
					(String) shippingpointData[3],
					(Long) shippingpointData[4],
					(Long) shippingpointData[5],
					(Double) shippingpointData[6],
					(Double) shippingpointData[7]);
			shippingpointDTOs.add(shippingpointDTO);
		}
		return shippingpointDTOs;
	}

	public ShippingpointEntity getShippingpointEntityByPlantCdAndSapNo(PlantBsEntity supplyPlant, String sapShippingPointNo) {
		final ShippingpointEntity shippingPoint;
		final ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
		final Filter<ShippingpointEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT, supplyPlant));
		filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_SAP_SHIPPINGPOINT_NO, sapShippingPointNo));
		shippingPoint = dao.findSingle(filter);
		return shippingPoint;
	}
	
}
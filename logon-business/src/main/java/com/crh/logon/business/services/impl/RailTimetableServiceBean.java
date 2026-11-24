/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.services.dispatchers.RailTimetableServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.RailTimetableServiceLocalInterface;
import com.crh.logon.persistence.entities.common.RailDistanceEntity;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.views.RailDistanceViewEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;

@Stateless
@Alternative
@AdnTrace
public class RailTimetableServiceBean extends RailTimetableServiceDispatcher
		implements RailTimetableServiceLocalInterface {
	
	public List<RailDistanceEntity> fetchFromToDistanceList(Long depRailwayStationId, Long arrRailwayStationId) {
		final ReadWriteDao<RailDistanceEntity> railDistanceDao = getDaoProvider().getRailDistanceEntityDao();
		Filter<RailDistanceEntity> railDistanceFilter = new FilterImpl<RailDistanceEntity>();
		Expression expr1 = Restrictions.eq(RailDistanceEntity.ATTR_DEP_RAILWAY_STATION_ID, depRailwayStationId);
		Expression expr2 = Restrictions.eq(RailDistanceEntity.ATTR_ARR_RAILWAY_STATION_ID, arrRailwayStationId);
		Expression expr3 = Restrictions.eq(RailDistanceEntity.ATTR_ARR_RAILWAY_STATION_ID, depRailwayStationId);
		Expression expr4 = Restrictions.eq(RailDistanceEntity.ATTR_DEP_RAILWAY_STATION_ID, arrRailwayStationId);
		railDistanceFilter.addExpression(Restrictions.or(Restrictions.and(expr1, expr2), Restrictions.and(expr3, expr4)));
		
		return railDistanceDao.find(railDistanceFilter);
	}
	
	public List<RailwayStationEntity> fetchRailwayStationList(String railwayStationNameSearchText, boolean isActive) {
		final ReadOnlyDao<RailwayStationEntity> railwayStationDao = getDaoProvider().getRailwayStationEntityDao();
		Filter<RailwayStationEntity> railDistanceFilter = new FilterImpl<RailwayStationEntity>();
		if(StringUtils.notEmpty(railwayStationNameSearchText)) {
			railDistanceFilter.addExpression(Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME, "%" + railwayStationNameSearchText + "%"));
		}
		railDistanceFilter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_ACTIVE_FLAG, isActive));
		
		return railwayStationDao.find(railDistanceFilter);
	}
	
	public ListRange fetchDistanceList(int skip, int count, String depRailwayStationId, String arrRailwayStationId, String orderby, boolean asc) {
		ListRange result = null;

		final ReadWriteDao<RailDistanceViewEntity> railDistanceDao = getDaoProvider().getRailDistanceViewEntityDao();
		Filter<RailDistanceViewEntity> railDistanceFilter = new FilterImpl<RailDistanceViewEntity>();
		
		if (StringUtils.notEmpty(depRailwayStationId)) {
			if(StringUtils.empty(arrRailwayStationId)) {
				railDistanceFilter.addExpression(Restrictions.or(Restrictions.eq(RailDistanceViewEntity.ATTR_DEP_STATION_ID, Long.valueOf(depRailwayStationId)), 
						Restrictions.eq(RailDistanceViewEntity.ATTR_ARR_STATION_ID, Long.valueOf(depRailwayStationId))));
			} else {
				Expression expr1 = Restrictions.eq(RailDistanceViewEntity.ATTR_DEP_STATION_ID, Long.valueOf(depRailwayStationId));
				Expression expr2 = Restrictions.eq(RailDistanceViewEntity.ATTR_ARR_STATION_ID, Long.valueOf(arrRailwayStationId));
				Expression expr3 = Restrictions.eq(RailDistanceViewEntity.ATTR_ARR_STATION_ID, Long.valueOf(depRailwayStationId));
				Expression expr4 = Restrictions.eq(RailDistanceViewEntity.ATTR_DEP_STATION_ID, Long.valueOf(arrRailwayStationId));
				railDistanceFilter.addExpression(Restrictions.or(Restrictions.and(expr1, expr2), Restrictions.and(expr3, expr4)));
			}

			railDistanceFilter.addExpression(asc ? Order.asc(orderby) : (Order.desc(orderby)));
		}
		
		result = new ListRange();
		List<RailDistanceViewEntity> railDistanceList = railDistanceDao.findNRecs(skip, count, railDistanceFilter);
		if (railDistanceList.isEmpty()) {
			result.setData(new Object[] {});
			result.setTotalSize(0);
		}
		else {
			result.setData(railDistanceList.toArray());
			result.setTotalSize(railDistanceDao.count(railDistanceFilter));
		}

		return result;
	}
	
	public ListRange fetchTimetableList(int skip, int count, String depRailwayStationId, String arrRailwayStationId, String orderby, boolean asc) {
		ListRange result = null;

		final ReadWriteDao<RailTimetableEntity> railTimetableDao = getDaoProvider().getRailTimetableEntityDao();
		Filter<RailTimetableEntity> railTimetableFilter = new FilterImpl<RailTimetableEntity>();
		
		if (StringUtils.notEmpty(depRailwayStationId)) {
			if(StringUtils.empty(arrRailwayStationId)) {
				railTimetableFilter.addExpression(Restrictions.or(Restrictions.eq(RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID, Long.valueOf(depRailwayStationId)), 
						Restrictions.eq(RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID, Long.valueOf(depRailwayStationId))));
			} else {
				Expression expr1 = Restrictions.eq(RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID, Long.valueOf(depRailwayStationId));
				Expression expr2 = Restrictions.eq(RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID, Long.valueOf(arrRailwayStationId));
				Expression expr3 = Restrictions.eq(RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID, Long.valueOf(depRailwayStationId));
				Expression expr4 = Restrictions.eq(RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID, Long.valueOf(arrRailwayStationId));
				railTimetableFilter.addExpression(Restrictions.or(Restrictions.and(expr1, expr2), Restrictions.and(expr3, expr4)));
			}
			
			railTimetableFilter.addExpression(asc ? Order.asc(orderby) : (Order.desc(orderby)));
			
			result = new ListRange();
			List<RailTimetableEntity> railDistanceList = railTimetableDao.findNRecs(skip, count, railTimetableFilter);
			if (railDistanceList.isEmpty()) {
				result.setData(new Object[] {});
				result.setTotalSize(0);
			}
			else {
				result.setData(railDistanceList.toArray());
				result.setTotalSize(railTimetableDao.count(railTimetableFilter));
			}
		}

		return result;
	}

	public RailDistanceEntity loadRailDistance(Long railDistanceId) {
		ReadOnlyDao<RailDistanceEntity> railDistanceDao = getDaoProvider().getRailDistanceEntityDao();
		
		return railDistanceDao.findById(railDistanceId);
	}
	
	public RailDistanceEntity createRailDistance(RailDistanceEntity railDistance) {
		ReadWriteDao<RailDistanceEntity> railDistanceDao = getDaoProvider().getRailDistanceEntityDao();
		
		return railDistanceDao.persist(railDistance);
	}
	
	public RailDistanceEntity updateRailDistance(RailDistanceEntity railDistance) {
		ReadWriteDao<RailDistanceEntity> railDistanceDao = getDaoProvider().getRailDistanceEntityDao();
		
		return railDistanceDao.merge(railDistance);
	}
	
	public void deleteRailDistance(Long railDistance) {
		ReadWriteDao<RailDistanceEntity> railDistanceDao = getDaoProvider().getRailDistanceEntityDao();
		RailDistanceEntity railDistanceEntity = loadRailDistance(railDistance);
		
		railDistanceDao.remove(railDistanceEntity);
	}
	
	
	
	public RailTimetableEntity loadRailTimetable(Long railTimetableId) {
		ReadOnlyDao<RailTimetableEntity> railTimetableDao = getDaoProvider().getRailTimetableEntityDao();
		
		return railTimetableDao.findById(railTimetableId);
	}
	
	public RailTimetableEntity createRailTimetable(RailTimetableEntity railTimetable) {
		ReadWriteDao<RailTimetableEntity> railTimetableDao = getDaoProvider().getRailTimetableEntityDao();
		
		return railTimetableDao.persist(railTimetable);
	}
	
	public RailTimetableEntity updateRailTimetable(RailTimetableEntity railTimetable) {
		ReadWriteDao<RailTimetableEntity> railTimetableDao = getDaoProvider().getRailTimetableEntityDao();
		
		return railTimetableDao.merge(railTimetable);
	}
	
	public void deleteRailTimetable(Long railTimetableId) {
		ReadWriteDao<RailTimetableEntity> railTimetableDao = getDaoProvider().getRailTimetableEntityDao();
		RailTimetableEntity railTimetableEntity = loadRailTimetable(railTimetableId);
		
		railTimetableDao.remove(railTimetableEntity);
	}
	
	public RailwayStationEntity loadRailwayStation(Long railwayStationId) {
		ReadOnlyDao<RailwayStationEntity> railwayStationDao = getDaoProvider().getRailwayStationEntityDao();
		
		return railwayStationDao.findById(railwayStationId);
	}
}

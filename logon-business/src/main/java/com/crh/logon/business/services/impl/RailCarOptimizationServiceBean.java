/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.RailCarOptimizationServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.RailCarOptimizationServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.RailChartEntity;
import com.crh.logon.persistence.entities.views.RailcarOptLoadedCarsEntity;
import com.crh.logon.persistence.entities.views.RailcarOptOpenOrdersEntity;
import com.crh.logon.persistence.entities.views.ShiptoWagonPrioFactorEntity;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class RailCarOptimizationServiceBean extends RailCarOptimizationServiceDispatcher
	implements RailCarOptimizationServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailCarOptimizationServiceBean.class);

	/**
	 * Fetch the list of rail wagons currently at the station(s) or due to return to the shippingpont(s) on dispodate 
	 * @param shippingpoints list of shippingpoint ids
	 * @param railwayStations list of railway station ids these should patch the shippingpoints from the other parameter
	 * @param langCd user language
	 * @param optimizationDate Date of the dispo
	 * @return list of DispoWagonListEntity 
	 */
	public List fetchWagons(List railwayStations, List wagonTypes,
			 String langCd, Date optimizationDate) {
		// [[
		List result = null;
		Object[][] params = {{"railwayStations", railwayStations},
							{"wagonTypes", wagonTypes},
							{"langCd", langCd},
							{"optimizationDate", optimizationDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchWagons", params);
		}
		// ]]
		try {
			if (railwayStations.isEmpty() || wagonTypes.isEmpty()){
				return result = new LinkedList();
			}
			Filter f = new FilterImpl();
			
			Date d1 = DateTimeUtils.cleanTimeWithDefaultTimeZone(optimizationDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d1);
			c.add(Calendar.DAY_OF_YEAR, 1);
			Date d2 = c.getTime();
			
			
			f.addExpression(Restrictions.eq(RailcarOptLoadedCarsEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));			
			f.addExpression(Restrictions.eq(RailcarOptLoadedCarsEntity.ATTR_LANG_CD, langCd));
			f.addExpression(Restrictions.in(RailcarOptLoadedCarsEntity.ATTR_WAGON_TYPE_CD, wagonTypes));
			f.addExpression(Restrictions.and(
						Restrictions.le(RailcarOptLoadedCarsEntity.ATTR_ARRIVAL_LAST_KNOWN_STATION_DAT, d2),
						Restrictions.in(RailcarOptLoadedCarsEntity.ATTR_LAST_KNOWN_RAILWAY_STATION_ID, railwayStations)));
			f.addExpression(Order.asc(RailcarOptLoadedCarsEntity.ATTR_ORDER_STATUS_CD));
			return result = getDaoProvider().getRailcarOptLoadedCarsEntityDao().find(f);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	 /**
     * Special query, which is needed for the table shown on the rail car optimization page. The cause why it is a JPA query
     * is simply that it is very similar to the rail chart query, and maybe faster because the classification of the wagons is
     * made in one step. Despite it would be better to have it in a normal query.
     * 
	 * @param optimizationDate
	 * @param railwayStationId
	 * @return
	 */
	public List getOptimizationData(Date optimizationDate, Set railwayStationIds, Set wagonTypeCds ) {
		// [[
		List result = null;
		Object[][] params = {{"optimizationDate", optimizationDate},
							{"railwayStationIds", railwayStationIds},
							{"wagonTypeCds", wagonTypeCds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOptimizationData", params);
		}
		// ]]
		try {
			boolean filterForWagonTypes = true;
			if ( wagonTypeCds == null ) {
				filterForWagonTypes = false;
			}
			if (railwayStationIds.isEmpty() || (filterForWagonTypes && wagonTypeCds.isEmpty() )) {
				return result = new ArrayList();
			}
			Calendar curr  = Calendar.getInstance();
			
			List arrguments = new ArrayList();
			Date date = DateTimeUtils.getMorningOfDay(optimizationDate);
			arrguments.add(date);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int sizeOfRwSet = railwayStationIds.size();
			int sizeOfWtypeSet = wagonTypeCds != null ? wagonTypeCds.size() : 0;
			c.add(Calendar.DAY_OF_YEAR, 1);
			Date d2 = c.getTime();
			Integer timeSliceLength = new Integer(5);
			Date nextMorning = DateTimeUtils.getMorningOfDay(d2);
			arrguments.add(nextMorning);
			String stationInClause = null;
			String wagonTypeInClause = null;
			{
				Iterator it = railwayStationIds.iterator();
				String inClause = " in ( ";
				int startIndex = 3;
				int index = startIndex;
				while (it.hasNext()) {
					Long rwStId = (Long) it.next();
					arrguments.add(rwStId);
					if ( index != startIndex ) {
						inClause += ",";
					}
					inClause += " ?" + index++;
				}
				inClause += " )";
				stationInClause = inClause;
				LOG.debug("stations in: " + stationInClause);
			}
			String wagonTypeFilter = null;
			if ( filterForWagonTypes ){
				Iterator it = wagonTypeCds.iterator();
				String inClause = " in ( ";
				int startIndex = 3 + sizeOfRwSet;
				int index = startIndex;
				while (it.hasNext()) {
					Long typeCd = (Long) it.next();
					arrguments.add(typeCd);
					if ( index != startIndex ) {
						inClause += ",";
					}
					inClause += " ?" + index++;
				}
				inClause += " )";
				wagonTypeInClause = inClause;
				wagonTypeFilter = " AND wagon_type_cd " + wagonTypeInClause;
				LOG.debug("wagonTypes in: " + wagonTypeInClause);
			}
			if (c.get(Calendar.DAY_OF_YEAR) == curr.get(Calendar.DAY_OF_YEAR) && c.get(Calendar.YEAR) == curr.get(Calendar.YEAR) ) {
				arrguments.add(curr.getTime());	
			}
			else {
				arrguments.add(nextMorning);
			}
			
			StringBuilder whereCond = new StringBuilder(" WHERE ((last_known_railway_station_id " + stationInClause + " and arrival_last_known_station_dat <= ?2) OR (return_railway_station_id " + stationInClause + " and planned_start<=?2)) AND active_flag <> 0 " + (wagonTypeFilter != null ? wagonTypeFilter : ""));
			
            List list = getDaoProvider().getDirectJpaDao().findNative(
                    " SELECT  wagon_type_cd,start_date, end_date,  gr, COUNT(*)	FROM ("+
                      " SELECT  wagon_type_cd, start_date, end_date," +
                      " CASE" +
                       " WHEN(return_railway_station_id " + stationInClause + " AND planned_start <= ?2 AND (planned_start <= start_date)) THEN 'incoming' " +
                       " WHEN (last_known_railway_station_id " + stationInClause + " AND arrival_last_known_station_dat <= ?2 AND planned_start IS NULL AND(load_id IS NULL OR loading_ts >= end_date)) THEN 'not loaded'"+
                       " WHEN(last_known_railway_station_id " + stationInClause + " AND arrival_last_known_station_dat <= ?2 AND( (outgoing_assign_status_cd IS NULL OR outgoing_assign_status_cd in (" + Constant.ASSIGNMENT_STATUS_PRE_LOAD  + "," + Constant.ASSIGNMENT_STATUS_NEW + "," + Constant.ASSIGNMENT_STATUS_LOADING + " )) AND load_id IS NOT NULL AND loading_ts <= end_date)) THEN 'loaded' " +
                       " END gr FROM " + Constant.SCHEMA_USER_PREPEND + RailChartEntity.TABLE_NAME + ", (" +
                       "(SELECT ?1 AS start_date, ?" + ( 3 + sizeOfRwSet + sizeOfWtypeSet) +  " AS end_date FROM dual )" +
                       ") " + whereCond + " ) WHERE gr IS NOT NULL " +
                       "GROUP BY start_date,  end_date,  gr,wagon_type_cd ORDER BY start_date,  end_date, gr", arrguments.toArray() );
                    return result = list;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns a result list containing the wagon type cds and their summed priority. The sum is
	 * on the selected shiptos.
	 * @param shiptoIds
	 * @param wagonTypeCds
	 * @return
	 */
	public List getWagonProrityFactorGroupby( Set shiptoIds, Set wagonTypeCds ) {
		// [[
		List result = null;
		Object[][] params = {{"shiptoIds", shiptoIds},
							{"wagonTypeCds", wagonTypeCds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagonPriorityGroupby", params);
		}
		// ]]
		try {
			boolean filterForWagonTypes = true;
			if ( wagonTypeCds == null ) {
				filterForWagonTypes = false;
			}
			if (shiptoIds.isEmpty() || (filterForWagonTypes && wagonTypeCds.isEmpty())) {
				return result = new ArrayList();
			}
			
			ReadOnlyDao<ShiptoWagonPrioFactorEntity> dao = getDaoProvider().getShiptoWagonPrioFactorEntityDao();
			JPAQuery jpaQuery = dao.createJPAQuery();
			Filter filt = new FilterImpl();
			
			filt.addExpression(Restrictions.in(ShiptoWagonPrioFactorEntity.ATTR_SHIPTO_ID, shiptoIds.toArray(new Long [0])));
			if ( filterForWagonTypes ) {
				filt.addExpression(Restrictions.in(ShiptoWagonPrioFactorEntity.ATTR_WAGON_TYPE_CD, wagonTypeCds.toArray(new Long [0])));
			}
			
			jpaQuery.setFilter(filt);
			jpaQuery.addGroupBy(ShiptoWagonPrioFactorEntity.ATTR_WAGON_TYPE_CD);
			jpaQuery.addSelect(SelectExpression.simpleSelect(ShiptoWagonPrioFactorEntity.ATTR_WAGON_TYPE_CD));				
			jpaQuery.addSelect(SelectExpression.sum(ShiptoWagonPrioFactorEntity.ATTR_FACTOR));
			
			List sumGroupBy = dao.find(jpaQuery);
			
			return result = sumGroupBy;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns a Map where the key is the wagonTypeCd (only those which are present in the parameter wagonTypeCds set,
	 * and the value is a map containing all those shiptos, which can use the wagonTypeCd.
	 * @param shiptoIds
	 * @param wagonTypeCds
	 * @return
	 */
	public Map getShiptosForWagonTypes( Set shiptoIds, Set wagonTypeCds ) {
		// [[
		Map result = null;
		Object[][] params = {{"shiptoIds", shiptoIds},
							{"wagonTypeCds", wagonTypeCds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShiptosForWagonTypes", params);
		}
		// ]]
		try {
			boolean filterForWagonTypes = true;
			if ( wagonTypeCds == null ) {
				filterForWagonTypes = false;
			}
			if (shiptoIds.isEmpty() || (filterForWagonTypes && wagonTypeCds.isEmpty())) {
				return result = new HashMap();
			}
			
			ReadOnlyDao<ShiptoWagonPrioFactorEntity> dao = getDaoProvider().getShiptoWagonPrioFactorEntityDao();
			Filter filt = new FilterImpl();
			
			filt.addExpression(Restrictions.in(ShiptoWagonPrioFactorEntity.ATTR_SHIPTO_ID, shiptoIds.toArray(new Long [0])));
			if ( filterForWagonTypes ) {
				filt.addExpression(Restrictions.in(ShiptoWagonPrioFactorEntity.ATTR_WAGON_TYPE_CD, wagonTypeCds.toArray(new Long [0])));
			}
			
			List shiptoWagonTypes = dao.find(filt);
			Iterator it = shiptoWagonTypes.iterator();
			result = new HashMap();
			while (it.hasNext()) {
				ShiptoWagonPrioFactorEntity shiptoWagonPrio = (ShiptoWagonPrioFactorEntity) it.next();
				if ( result.get(shiptoWagonPrio.getWagonTypeCd()) != null ) {
					Map map = (HashMap) result.get(shiptoWagonPrio.getWagonTypeCd());
					map.put(shiptoWagonPrio.getShiptoId(),shiptoWagonPrio.getShiptoId());
				}
				else {
					Map map = new HashMap();
					map.put(shiptoWagonPrio.getShiptoId(),shiptoWagonPrio.getShiptoId());
					result.put(shiptoWagonPrio.getWagonTypeCd(), map);
				}
			}
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	/**
	 * Returns a result list containing the wagon type cds and their summed priority. The sum is
	 * on the selected shiptos.
	 * @param shiptoIds
	 * @param wagonTypeCds
	 * @return
	 */
	public List fetchOpenOrders(Set railwayStations, String langCd,
			 Date optimizationDate) {
		// [[
		List result = null;
		Object[][] params = {{"railwayStations", railwayStations},
							{"langCd", langCd},
							{"optimizationDate", optimizationDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchOpenOrders", params);
		}
		// ]]
		try {
			
			if (railwayStations.isEmpty() ){
				return result = new LinkedList();
			}
			
			ReadOnlyDao<RailcarOptOpenOrdersEntity> olDao = getDaoProvider().getRailcarOptOpenOrdersEntityDao();
			Filter f = new FilterImpl();
			
			
			ArrayList orderStates = new ArrayList();
			orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
			orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);
			orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
			orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);
			
			f.addExpression(Restrictions.in(RailcarOptOpenOrdersEntity.ATTR_DEP_RAILWAY_STATION_ID, railwayStations));
			
			f.addExpression(Restrictions.in(RailcarOptOpenOrdersEntity.ATTR_ORDER_STATUS_CD, orderStates));
			f.addExpression(Restrictions.or(Restrictions.eq(RailcarOptOpenOrdersEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_RAIL_DIRECT),
					Restrictions.eq(RailcarOptOpenOrdersEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_BIMODAL_RAIL)));
			f.addExpression(Restrictions.eq(RailcarOptOpenOrdersEntity.ATTR_LANG_CD, langCd));
			
			Date morning = DateTimeUtils.getMorningOfDay(optimizationDate);
			
			Calendar c = Calendar.getInstance();
			c.setTime(DateTimeUtils.cleanTimeWithDefaultTimeZone(optimizationDate));
			c.add(Calendar.DAY_OF_YEAR, 1);
			Date nextMorning = DateTimeUtils.getMorningOfDay(c.getTime());
			
			f.addExpression(Restrictions.ge(RailcarOptOpenOrdersEntity.ATTR_LAST_DEPARTURE, morning ));
			f.addExpression(Restrictions.le(RailcarOptOpenOrdersEntity.ATTR_LAST_DEPARTURE, nextMorning ));
			f.addExpression(Order.asc(RailcarOptOpenOrdersEntity.ATTR_MIN_EVALUATION_PRIORITY));
			List rawList = olDao.find(f);
			
			return result = rawList;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
}

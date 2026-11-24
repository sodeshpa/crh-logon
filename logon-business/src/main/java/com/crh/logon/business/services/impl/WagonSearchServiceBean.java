/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.dispatchers.WagonSearchServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.WagonSearchServiceLocalInterface;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.views.LocationStationsInUseEntity;
import com.crh.logon.persistence.entities.views.OrderSearchEntity;
import com.crh.logon.persistence.entities.views.RailRepairEntity;
import com.crh.logon.persistence.entities.views.ReturnStationsInUseEntity;
import com.crh.logon.persistence.entities.views.WagonSearchEntity;
import com.crh.logon.utils.ListRange;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class WagonSearchServiceBean extends WagonSearchServiceDispatcher implements WagonSearchServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonSearchServiceBean.class);
	
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	
    final static String SELECT_DISTINCT_WAGON_NUMBERS = 
			"select distinct(rre." + RailRepairEntity.ATTR_WAGON_NO + ") " +
			"from " + RailRepairEntity.class.getSimpleName() + " rre " +
			"where rre." + RailRepairEntity.ATTR_WAGON_NO + " like ?1 " +
			"order by rre." + RailRepairEntity.ATTR_WAGON_NO;
	
	final static String COUNT_DISTINCT_WAGON_NUMBERS = 
			"select count(distinct rre." + RailRepairEntity.ATTR_WAGON_NO + ") " +
					"from " + RailRepairEntity.class.getSimpleName() + " rre " +
					"where rre." + RailRepairEntity.ATTR_WAGON_NO + " like ?1";
	
	/**
	 * Used by UC 4015 (Order Search) and UC 40XX (Order Monitoring)
	 * @param typeCd 
	 * @param keeperId 
	 * @param activeFlag 
	 * @param wagonNo 
	 * @param returnStationId 
	 * @param locationStationId 
	 */
	public ListRange searchWagons(
			int skip,
			int num,
			String sort,
			boolean asc,
			Locale locale,
			List<Long> typeCdList,
			String wagonNo,
			Long locationStationId,
			Long keeperId,
			List<Long> orderStatusCdList,
			Long destRailwayStationId,
			List<Long> distributionDestCdList,
			List<Long> inactiveReasonCdList,
			Boolean activeFlag) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"locale", locale},
							{"typeCdList", typeCdList},
							{"wagonNo", wagonNo},
							{"locationStationId", locationStationId},
							{"keeperId", keeperId},
							{"orderStatusCdList", orderStatusCdList},
							{"destRailwayStationId", destRailwayStationId},
							{"distributionDestCdList", distributionDestCdList},
							{"inactiveReasonCdList", inactiveReasonCdList},
							{"activeFlag", activeFlag}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchWagons", params);
		}
		// ]]
		try {
			final ReadOnlyDao<WagonSearchEntity> dao = getDaoProvider().getWagonSearchEntityDao();
			final Filter<WagonSearchEntity> filter = dao.createFilter();
			if ((typeCdList != null) && (!typeCdList.isEmpty())) {
				filter.addExpression(Restrictions.in(WagonSearchEntity.ATTR_WAGON_TYPE_CD , typeCdList));
			}
			if (StringUtils.isNotEmpty(wagonNo)) {
				filter.addExpression(Restrictions.ilike(WagonSearchEntity.ATTR_WAGON_NO ,'%' + wagonNo + '%'));
			}
			if (locationStationId != null) {
				filter.addExpression(Restrictions.eq(WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID, locationStationId));
			}
			if (keeperId != null) {
				filter.addExpression(Restrictions.eq(WagonSearchEntity.ATTR_WAGON_KEEPER_ID , keeperId));
			}
			if ((orderStatusCdList != null) && (!orderStatusCdList.isEmpty())) {
				Long refDataCountOfOrderStatuses = (Long)getDaoProvider().getDirectJpaDao().findSingle("SELECT COUNT(x) FROM " + OrderStatusBsEntity.class.getSimpleName() + " x");
				if ( refDataCountOfOrderStatuses.longValue() != orderStatusCdList.size() ) {
					// Only add the order status filter if not all order status are selected.
					// If 'all' is selected, wagons without order will be shown too (ADN53349).
					filter.addExpression(Restrictions.in(OrderSearchEntity.ATTR_ORDER_STATUS_CD, orderStatusCdList));
				}
			}
			if (destRailwayStationId != null) {
				filter.addExpression(Restrictions.eq(WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID , destRailwayStationId));
			}
			if ((distributionDestCdList != null) && (!distributionDestCdList.isEmpty())) {
				Long refdataCountOfDistrDestinations = (Long)getDaoProvider().getDirectJpaDao().findSingle("SELECT COUNT(x) FROM " + DistributionDestBsEntity.class.getSimpleName() + " x");
				
				if ( (refdataCountOfDistrDestinations.longValue() != distributionDestCdList.size()) ) {
					filter.addExpression(Restrictions.in(WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD, distributionDestCdList));
				}
			}
			if ((inactiveReasonCdList != null) && (!inactiveReasonCdList.isEmpty())) {
				filter.addExpression(Restrictions.or(Restrictions.eq(WagonSearchEntity.ATTR_ACTIVE_FLAG, BooleanUtils.isTrue(activeFlag)), 
						Restrictions.in(WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD, inactiveReasonCdList)));
			}
			else if (BooleanUtils.isTrue(activeFlag)) {
				filter.addExpression(Restrictions.eq(WagonSearchEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			}
			
			filter.addExpression(Restrictions.or(
					Restrictions.isNull(WagonSearchEntity.ATTR_LANG_CD),
					Restrictions.eq(WagonSearchEntity.ATTR_LANG_CD, locale.getLanguage())));
			
			filter.addExpression(asc ? Order.ascCaseInsensitive(WagonSearchEntity.class, sort) : Order.descCaseInsensitive(WagonSearchEntity.class, sort));
			final List<WagonSearchEntity> wagonList = dao.findNRecs(skip, num, filter);
			
			result = new ListRange();
			result.setTotalSize(dao.count(filter));
			result.setData(wagonList.toArray());
			
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
	 * Used by BW-Instandhaltung (RailRepair)
	 */
    public ListRange searchRailRepair(int skip, int num, String sort, boolean asc, Map<String, String> searchCrits, Locale locale) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrits", searchCrits},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchRailRepair", params);
		}
		// ]]
		try {
			final ReadOnlyDao<RailRepairEntity> dao = getDaoProvider().getRailRepairEntityDao();
			final Filter<RailRepairEntity> filter = dao.createFilter();
			final Set<Map.Entry<String, String>> searchCritEntries = searchCrits.entrySet();
			for (Map.Entry<String, String> entry : searchCritEntries) {
				if (RailRepairEntity.ATTR_WAGON_NO.equals(entry.getKey())) {
					final String wagonNo = entry.getValue();
					if (wagonNo != null && !wagonNo.isEmpty()) {
						filter.addExpression(Restrictions.eq(RailRepairEntity.ATTR_WAGON_NO, wagonNo));
					}
				}
				else {
					final List<Long> ids = new ArrayList<Long>();
					for (String id : StringUtils.split(entry.getValue(), ',')) {
						ids.add(Long.parseLong(id));
					}
					if (!ids.isEmpty()) {
						filter.addExpression(Restrictions.in(entry.getKey(), ids));
					}
				}
			}
			filter.addExpression(Restrictions.or(
					Restrictions.isNull(RailRepairEntity.ATTR_LANG_CD),
					Restrictions.eq(RailRepairEntity.ATTR_LANG_CD, locale.getLanguage())));
			
			filter.addExpression(asc ? Order.ascCaseInsensitive(RailRepairEntity.class, sort) : Order.descCaseInsensitive(RailRepairEntity.class, sort));
			final List<RailRepairEntity> wagonList = dao.findNRecs(skip, num, filter);
			
			result = new ListRange();
			result.setTotalSize(dao.count(filter));
			result.setData(wagonList.toArray());
			
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
    
	public ListRange searchRailRepairWagonNoList(int skip, int num, String wagonNoPart) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"wagonNoPart", wagonNoPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchRailRepairWagonNoList", params);
		}
		// ]]
		try {
			if (wagonNoPart == null) {
				wagonNoPart = "";
			}
			else if (!wagonNoPart.isEmpty()) {
				wagonNoPart = wagonNoPart.replaceAll("[^\\d]", "");
				try {
					Long.parseLong(wagonNoPart);
				}
				catch (NumberFormatException e) {
					final String msg = "wagonNoPart is invalid: " + wagonNoPart;
					LOG.warn(msg);
					result = new ListRange();
					result.setTotalSize(0);
					result.setData(new ArrayList<String>().toArray());
					return result;
				}
			}
			
			final DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			final List<Object> args = new ArrayList<Object>();
			args.add("%" + wagonNoPart + "%");
			
			final List<String> filteredWagonNumbers = dao.findNRecs(skip, num, SELECT_DISTINCT_WAGON_NUMBERS, args);
			
			Object count = dao.findSingle(COUNT_DISTINCT_WAGON_NUMBERS, args);
			final long totalSize;
			if (count != null) {
				totalSize = ((Long)count);
			}
			else {
				totalSize = 0;
			}
			
			result = new ListRange();
			result.setTotalSize(totalSize);
			result.setData(filteredWagonNumbers.toArray());
			
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
     * Returns shippingpoints which have active railway stations related, and are active.
     * @return
     */
	public ListRange getHomeRailwayStationList(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getHomeRailwayStationList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ReturnStationsInUseEntity> dao = getDaoProvider().getReturnStationsInUseEntityDao();			
			final Filter<ReturnStationsInUseEntity> filt = dao.createFilter();
			
			long longValue = Constant.INVALID_KEY;
			try{
				longValue = Long.parseLong(query);
			}
			catch (NumberFormatException e) {
				longValue = Constant.INVALID_KEY;
			}
			
			if (query != null) {
				if (longValue == Constant.INVALID_KEY) {
					filt.addExpression(Restrictions.or(
							Restrictions.ilike(ReturnStationsInUseEntity.ATTR_RAILWAY_STATION_NAME, '%'+ query +'%'),
							Restrictions.ilike(ReturnStationsInUseEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%')
					));
				}
				else{					
					filt.addExpression(Restrictions.or(
							Restrictions.ilike(ReturnStationsInUseEntity.ATTR_RAILWAY_STATION_NAME, '%'+ query +'%'),
							Restrictions.eq(ReturnStationsInUseEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(longValue)),
							Restrictions.ilike(ReturnStationsInUseEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%')
					));
				}
				filt.addExpression(Order.ascCaseInsensitive(ReturnStationsInUseEntity.ATTR_RAILWAY_STATION_NAME));
			}
			
			final List<ReturnStationsInUseEntity> homeStations = dao.findNRecs(skip, num, filt);
			result = new ListRange();
			result.setTotalSize(dao.count(filt));
			result.setData(homeStations.toArray());
			
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
	 * Returns all active railway stations, where trains are currently located.
	 * @return
	 */
	public ListRange getRailwayStationList(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailwayStationList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<LocationStationsInUseEntity> dao = getDaoProvider().getLocationStationsInUseEntityDao();
			final Filter<LocationStationsInUseEntity> filt = dao.createFilter();
			
			long longValue = Constant.INVALID_KEY;			
			try{
				longValue = Long.parseLong(query);
			}
			catch (NumberFormatException e){
				longValue = Constant.INVALID_KEY;
			}
			
			if (query != null) {
				if (longValue == Constant.INVALID_KEY) {
					filt.addExpression(Restrictions.or(
							Restrictions.ilike(LocationStationsInUseEntity.ATTR_RAILWAY_STATION_NAME, '%'+ query +'%'),
							Restrictions.ilike(LocationStationsInUseEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%')
					));
				}
				else{					
					filt.addExpression(Restrictions.or(
							Restrictions.ilike(LocationStationsInUseEntity.ATTR_RAILWAY_STATION_NAME, '%'+ query +'%'),
							Restrictions.eq(LocationStationsInUseEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(longValue)),
							Restrictions.ilike(LocationStationsInUseEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%')
					));
				}
				filt.addExpression(Order.ascCaseInsensitive(LocationStationsInUseEntity.ATTR_RAILWAY_STATION_NAME));
			}
			
			final List<LocationStationsInUseEntity> stations = dao.findNRecs(skip, num, filt);
			result = new ListRange();
			result.setTotalSize(dao.count(filt));
			result.setData(stations.toArray());
			
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
	
	public ListRange getAllRailwayStations(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAllRailwayStations", params);
		}
		// ]]
		try {

			final ReadOnlyDao<RailwayStationEntity> dao = getDaoProvider().getRailwayStationEntityDao();
			final Filter<RailwayStationEntity> filt = dao.createFilter();
			
			long longValue = Constant.INVALID_KEY;
			try{
				longValue = Long.parseLong(query);
			}
			catch (NumberFormatException e){
				longValue = Constant.INVALID_KEY;
			}
			
			if (query != null) {
				if (longValue == Constant.INVALID_KEY) {
					filt.addExpression(Restrictions.or(
							Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME, '%'+ query +'%'),
							Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%')
					));
				}
				else{					
					filt.addExpression(Restrictions.or(
							Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME, '%'+ query +'%'),
							Restrictions.eq(RailwayStationEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(longValue)),
							Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%')
					));
				}
				filt.addExpression(Order.ascCaseInsensitive(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME));
			}

			final List<RailwayStationEntity> stations = getDaoProvider().getRailwayStationEntityDao().findNRecs(skip, num, filt);
			result = new ListRange();
			result.setTotalSize(dao.count(filt));
			result.setData(stations.toArray());
			
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
	 * Returns all active railway stations.
	 * @return
	 */
	public ListRange getWagonNoList(int skip, int num, String wagonNoPart) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"wagonNoPart", wagonNoPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagonNoList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
			final Filter<WagonEntity> filt = dao.createFilter();
			
			filt.addExpression(Restrictions.ilike(WagonEntity.ATTR_WAGON_NO, '%' + wagonNoPart + '%'));			
			filt.addExpression(Order.ascCaseInsensitive(WagonEntity.ATTR_WAGON_NO));
			
			final List<WagonEntity> wagons = dao.findNRecs(skip, num, filt);
			result = new ListRange();
			result.setTotalSize(dao.count(filt));
			result.setData(wagons.toArray());
			
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
	 * Returns wagon types ordered by name
	 * @return
	 */
	public List<WagonTypeBsEntity> getWagonTypeBsList() {
		// [[
		List<WagonTypeBsEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagonTypeBsList");
		}
		// ]]
		try {
			final Filter<WagonTypeBsEntity> filt = new FilterImpl<WagonTypeBsEntity>();
			filt.addExpression(Order.ascCaseInsensitive(WagonTypeBsEntity.ATTR_WAGON_TYPE_NAME));
			
			return result = referentialDataService.findBsEntityFiltered(WagonTypeBsEntity.class, filt);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<OrderStatusTxEntity> getOrderStatusList(Locale locale) {
		// [[
		List<OrderStatusTxEntity> result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderStatusList", params);
		}
		// ]]
		try {
			final Filter<OrderStatusTxEntity> filter = new FilterImpl<OrderStatusTxEntity>();
			filter.addExpression(Restrictions.eq("orderStatusTxPK.langCd", locale.getLanguage()));
			filter.addExpression(Order.asc("orderStatusTxPK.orderStatusBs." + OrderStatusBsEntity.ATTR_SORTING_NO));
			
			return result = referentialDataService.findTxEntityFiltered(OrderStatusTxEntity.class, filter);
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

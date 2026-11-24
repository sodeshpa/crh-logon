/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.CarrierServiceLocalInterface;
import com.crh.logon.business.stores.domain.CarrierStoreInterface;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.services.dispatchers.CarrierServiceDispatcher;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.CarrierSvendorEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrPfActiveEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrierPfEntity;
import com.crh.logon.persistence.entities.views.PoolAllCarrierEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ListRange;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class CarrierServiceBean extends CarrierServiceDispatcher implements CarrierServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CarrierServiceBean.class);

	@EJB
	CarrierStoreInterface carrierStore;

	/**
	 * Used by the Master Data / Carrier screen / Carrier filter.
	 * 
	 * Load all carriers (object of {@link CarrierSvendorEntity}) where the 
	 * TLGNS_SAP_VENDOR.VN_IS_COMM_CARRIER attribute is '1' (true).
	 * 
	 * @param skip
	 * @param num
	 * @param query
	 * @return
	 */
	public ListRange fetchCommercialCarrierList(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
				{"num", num + ""},
				{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialList", params);
		}
		// ]]
		try {			
			final ReadOnlyDao<CarrierSvendorEntity> carrSVendorDao = getDaoProvider().getCarrierSvendorEntityDao();			
			final Filter<CarrierSvendorEntity> filter = carrSVendorDao.createFilter();
			filter.addExpression(Restrictions.ilike(CarrierSvendorEntity.ATTR_DISPLAY_NAME, "%" + query + "%"));
			filter.addExpression(Order.asc(CarrierSvendorEntity.ATTR_DISPLAY_NAME));
			filter.addExpression(Restrictions.eq(CarrierSvendorEntity.ATTR_VN_IS_COMM_CARRIER, true));
			
			result = new ListRange();
			result.setData(carrSVendorDao.findNRecs(skip, num, filter).toArray());
			result.setTotalSize(carrSVendorDao.count(filter));
			
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
	 * Returns a map of {@code CarrierEntity}s mapped by their carrier ID
	 * and the vendor is a commercial carrier
	 * @param poolId - filter clause
	 * @return result list of entities if none found empty list
	 */
	@AdnTrace
	public Map<Long, CarrierEntity> fetchCommercialCarriersByPoolId(Long poolId) {

		Map<Long, CarrierEntity> carrierMap = new HashMap<Long, CarrierEntity>();
		List<CarrierEntity> commercialCarriers = carrierStore.findCommercialCarriersByPoolId(poolId);
		for (CarrierEntity commercialCarrier : commercialCarriers) {
			Long carrierId = commercialCarrier.getCarrierId();
			carrierMap.put(carrierId, commercialCarrier);
		}

		return carrierMap;

	}

	/**
	 * Used by the Master Data / Carrier screen / Error filter.
	 * 
	 * Load a list of vendors (object of {@link CarrierSvendorEntity}) where an error occured 
	 * at the SAP import. The TLGNS_SAP_VENDOR.UPDATE_BLOCKED_BY_HJID is not null.
	 * 
	 * @param skip
	 * @param num
	 * @param query
	 * @return
	 */
	public ListRange fetchErrorVendorList(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
				{"num", num + ""},
				{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchErrorVendorList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<CarrierSvendorEntity> carrSVendorDao = getDaoProvider().getCarrierSvendorEntityDao();			
			final Filter<CarrierSvendorEntity> filter = carrSVendorDao.createFilter();
			filter.addExpression(Restrictions.isNotNull(CarrierSvendorEntity.ATTR_UPDATE_BLOCKED_BY_HJID));
			if (StringUtils.isNotEmpty(query)) {
				filter.addExpression(Restrictions.ilike(CarrierSvendorEntity.ATTR_NAME1, "%" + query + "%"));
			}
			filter.addExpression(Order.asc(CarrierSvendorEntity.ATTR_NAME1));
			
			result = new ListRange();
			result.setData(carrSVendorDao.findNRecs(skip, num, filter).toArray());
			result.setTotalSize(carrSVendorDao.count(filter));
			
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
	 * Search for carriers ({@link CarrierSvendorEntity})) by the given search criteria.
	 * 
	 * @param skip
	 * @param num
	 * @param sort
	 * @param asc
	 * @param searchCrit
	 * @param locale
	 * @return
	 */
	public ListRange carrierSearch(int skip, int num, String sort, boolean asc, HashMap<String, String> searchCrit, Locale locale) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "carrierSearch", params);
		}
		// ]]
		try {
			final DaoProvider daoProv = getDaoProvider();
			final ReadOnlyDao<CarrierSvendorEntity> carrSVendorDao = daoProv.getCarrierSvendorEntityDao();
			final ReadOnlyDao<CommExecCarrierPfEntity> commExecCarrierPfDao = daoProv.getCommExecCarrierPfEntityDao();
			final Filter<CarrierSvendorEntity> filter = carrSVendorDao.createFilter();
			final Filter<CommExecCarrierPfEntity> pfFilter = commExecCarrierPfDao.createFilter();
			
			boolean carrierFilterSet = StringUtils.isNotEmpty(searchCrit.get(CarrierSvendorEntity.ATTR_CARRIER_ID));
			
			if (carrierFilterSet) { // collect the 
				pfFilter.addExpression(Restrictions.eq(CommExecCarrierPfEntity.ATTR_COMM_CARRIER_ID, Long.valueOf(searchCrit.get(CarrierSvendorEntity.ATTR_CARRIER_ID))));
				final List<CommExecCarrierPfEntity> carrierList = commExecCarrierPfDao.findNRecs(0, 0, pfFilter);
				
				if (carrierList == null || carrierList.isEmpty()) {
					result = new ListRange();
					result.setTotalSize(0);
					result.setData(new Object[] {});
					return result;
				}
				filter.addExpression(DbUtils.createSplittedInRestriction(CarrierSvendorEntity.ATTR_CARRIER_ID, getCarrierIdList(carrierList)));
			}
			
			if (StringUtils.isNotEmpty(searchCrit.get(CarrierSvendorEntity.ATTR_SAP_VENDOR_ID))) {
				filter.addExpression(Restrictions.eq(CarrierSvendorEntity.ATTR_SAP_VENDOR_ID, Long.valueOf(searchCrit.get(CarrierSvendorEntity.ATTR_SAP_VENDOR_ID))));
			}

			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			final List<CarrierSvendorEntity> orderList = carrSVendorDao.findNRecs(skip, num, filter);				
			
			// get the count
			long count;
			if (skip == 0 && orderList.size() < num) {
				count = orderList.size();
			}
			else {
				count = carrSVendorDao.count(filter);
			}
			
			result = new ListRange();
			result.setTotalSize(count);
			result.setData(orderList.toArray());
			
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
	 * 
	 * @return List<PoolAllCarrierEntity> 
	 */
	public List<PoolAllCarrierEntity> fetchAllExecutingCarrierList() {
		// [[
		List<PoolAllCarrierEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllExecutingCarrierList");
		}
		// ]]
		try {
			final ReadOnlyDao<PoolAllCarrierEntity> poolAllCarrierDao = getDaoProvider().getPoolAllCarrierEntityDao();
			Filter<PoolAllCarrierEntity> filter = new FilterImpl<PoolAllCarrierEntity>();
			filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_VN_IS_EXEC_CARRIER, true));
			return result = poolAllCarrierDao.find(filter);
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
	 * 
	 * @return List<PoolAllCarrierEntity> 
	 */
	@AdnTrace
	public List<CommercialCarrierDTO> fetchAllCommercialCarrierList(List<Long> poolIds, Locale locale) {
			final ReadOnlyDao<PoolCarrierCarrierEntity> poolAllCarrierDao = getDaoProvider().getPoolCarrierCarrierEntityDao();
			JPAQuery<PoolCarrierCarrierEntity> query = poolAllCarrierDao.createJPAQuery();
			Filter<PoolCarrierCarrierEntity> filter = poolAllCarrierDao.createFilter();
			
			if (poolIds != null && !poolIds.isEmpty()) {
				filter.addExpression(DbUtils.createSplittedInRestriction(PoolCarrierCarrierEntity.ATTR_POOL_ID, poolIds));
			}
			
			String language = locale.getLanguage();
			filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_POOL_NAME_LANG_CD, language));
			filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_VN_IS_COMM_CARRIER, true));
			filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_CARRIER_ACTIVE_FLAG, true));
			filter.addExpression(Order.asc(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
			
			query.addSelect(SelectExpression.simpleSelect(PoolCarrierCarrierEntity.ATTR_CARRIER_ID));
			query.addSelect(SelectExpression.simpleSelect(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
			query.setFilter(filter);
			query.setDistinct(true);
			
			@SuppressWarnings("unchecked")
			List<Object[]> rows = poolAllCarrierDao.find(query);
			List<CommercialCarrierDTO> result = new ArrayList<CommercialCarrierDTO>();
			
			for (Object[] row: rows) {
				Long carrierId = (Long) row[0];
				String carrierName = (String) row[1];

				result.add(new CommercialCarrierDTO(carrierId, carrierName, null));
			}
			
			return result;
	}
	
	@AdnTrace
	public List<ExecutingCarrierDTO> fetchDistinctExecutingCarrierList(List<Long> poolIds, Locale locale) {
		ReadOnlyDao<PoolCarrierCarrierEntity> poolCarrierDao = getDaoProvider().getPoolCarrierCarrierEntityDao();
		JPAQuery<PoolCarrierCarrierEntity> query = poolCarrierDao.createJPAQuery();
		Filter<PoolCarrierCarrierEntity> filter = poolCarrierDao.createFilter();
		
		if (poolIds != null && !poolIds.isEmpty()) {
			filter.addExpression(DbUtils.createSplittedInRestriction(PoolCarrierCarrierEntity.ATTR_POOL_ID, poolIds));
		}
		
		String language = locale.getLanguage();
		filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_POOL_NAME_LANG_CD, language));
		filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_VN_IS_EXEC_CARRIER, true));
		filter.addExpression(Order.asc(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
		
		query.addSelect(SelectExpression.simpleSelect(PoolCarrierCarrierEntity.ATTR_CARRIER_ID));
		query.addSelect(SelectExpression.simpleSelect(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
		query.setFilter(filter);
		query.setDistinct(true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> rows = poolCarrierDao.find(query);
		List<ExecutingCarrierDTO> result = new ArrayList<ExecutingCarrierDTO>();
		
		for (Object[] row: rows) {
			Long carrierId = (Long) row[0];
			String carrierName = (String) row[1];

			result.add(new ExecutingCarrierDTO(carrierId, carrierName));
		}
		
		return result;
	}

	/**
	 * 
	 * @param poolIds
	 * @param locale
	 * @return List<PoolCarrierCarrierEntity>
	 */
	public List<PoolCarrierCarrierEntity> fetchExecutingCarrierList(List<Long> poolIds, Locale locale) {
		// [[
		List<PoolCarrierCarrierEntity> result = null;
		Object[][] params = {{"poolIds", poolIds},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchExecutingCarrierList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<PoolCarrierCarrierEntity> poolCarrierDao = getDaoProvider().getPoolCarrierCarrierEntityDao();
			final Filter<PoolCarrierCarrierEntity> filter = poolCarrierDao.createFilter();
			String language = locale.getLanguage();
			
			if (poolIds != null && !poolIds.isEmpty()) {
				filter.addExpression(Restrictions.in(PoolCarrierCarrierEntity.ATTR_POOL_ID, poolIds));
			}
			filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_POOL_NAME_LANG_CD, language));
			filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_VN_IS_EXEC_CARRIER, true));
			filter.addExpression(Order.asc(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
			
			return result = poolCarrierDao.find(filter);
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
	 * Finds the partner exec. carriers to the given comm. carriers.
	 */
	@AdnTrace
	public List<CommExecCarrierPfEntity> findPartnerExecCarriersOrdered(List<Long> commercialCarrierIds) {
		Expression carrierExpression = null;
		if (commercialCarrierIds != null && !commercialCarrierIds.isEmpty()) {
			carrierExpression = DbUtils.createSplittedInRestriction(
				CommExecCarrierPfEntity.ATTR_COMM_CARRIER_ID, commercialCarrierIds);
		}
		return findPartnerExecCarriersOrdered(carrierExpression);
	}
	
	/**
	 * Returns executing carriers which are considered as active, usable
	 * to execute a workflow.
	 * They need to fulfill basic criterias like they need to have an active
	 * connection to commercial carrier, and pool.
	 */
	public List<CommExecCarrPfActiveEntity> findCommercialExecCarrierPartnersOrdered() {

		final ReadOnlyDao<CommExecCarrPfActiveEntity> commExecCarrierPfDao = getDaoProvider().getCommExecCarrPfActiveEntityDao();
		Filter<CommExecCarrPfActiveEntity> filter = new FilterImpl<CommExecCarrPfActiveEntity>();
		filter.addExpression(Order.asc(CommExecCarrPfActiveEntity.ATTR_EXEC_CARRIER_DISPLAY_NAME));
		
		List<CommExecCarrPfActiveEntity> commExecCarrierPfList = commExecCarrierPfDao.find(filter);
		
		return commExecCarrierPfList;
	}
	
	/**
	 * Finds the partner exec. carriers to the given comm. carrier.
	 */
	public List<CommExecCarrierPfEntity> findPartnerExecCarriersOrdered(Long commercialCarrierId) {
		// [[
		List<CommExecCarrierPfEntity> result = null;
		Object[][] params = {{"commercialCarrierId", commercialCarrierId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findPartnerExecCarriersOrdered", params);
		}
		// ]]
		try {
			Expression carrierExpression = Restrictions.eq(
					CommExecCarrierPfEntity.ATTR_COMM_CARRIER_ID, commercialCarrierId);
			result = findPartnerExecCarriersOrdered(carrierExpression);
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
	
	public List<CarrierEntity> fetchPartnerExecutingCarrierList(Long commercialCarrierId) {
		// [[
		List<CarrierEntity> result = null;
		Object[][] params = {{"commercialCarrierId", commercialCarrierId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPartnerExecutingCarrierList", params);
		}
		// ]]
		try {
			List<CommExecCarrierPfEntity> carrierPartners = findPartnerExecCarriersOrdered(commercialCarrierId);
			
			if (!carrierPartners.isEmpty()) {
				Set<Long> partnerCarrierIds = new HashSet<Long>();
				for (CommExecCarrierPfEntity carrierPartner : carrierPartners) {
					Long carrierId = carrierPartner.getExecCarrierId();
					partnerCarrierIds.add(carrierId);
				}
				result = findCarrierEntities(partnerCarrierIds);
			}
			else {
				result = new ArrayList<CarrierEntity>();
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
	 * Returns a list of Executing Carriers for the given Commercial Carrier based on connection between them.
	 * If there is a preSelectedExecutingCarrierId provided and is not otherwise contained in the result will be added explicitly.
	 * @param commercialCarrierId - for which we are seeking the executing carrier partners
	 * @param preSelectedExecutingCarrierId - if provided will be added will be added explicitly to the result
	 * @return fetched entities
	 */
	@AdnTrace
	public List<CarrierEntity> fetchExecutingCarriersByCommercialCarrierWithPreSelection(Long commercialCarrierId, Long preSelectedExecutingCarrierId) {

		List<CarrierEntity> executingCarriers = fetchPartnerExecutingCarrierList(commercialCarrierId);
		if (preSelectedExecutingCarrierId != null) {
			boolean found = false;
			for (CarrierEntity executingCarrier : executingCarriers) {
				Long executingCarrierId = executingCarrier.getCarrierId();
				if (executingCarrierId.equals(preSelectedExecutingCarrierId)) {
					found = true;
					break;
				}
			}
			if (!found) {
				CarrierEntity preSelectedExecutingCarrier = carrierStore.findById(preSelectedExecutingCarrierId);
				executingCarriers.add(preSelectedExecutingCarrier);
			}
		}

		return executingCarriers;

	}

	/**
	 * Fetch a list with all carriers for a pool directly returning {@link PoolCarrierCarrierEntity} entries.
	 * If a screen is interested in the partner functions rather use {{@link #fetchCarriersForPool(Long)}
	 * @param poolId id of the pool
	 * @return a list with all carriers for given pool 
	 */
	// TODO (orsika Sep 6, 2013): can return the same object many times in the list!
	public List<PoolCarrierCarrierEntity> fetchCarriersForPoolBasic(Long poolId) {
		return fetchCarriersForPoolBasic(poolId, null);
	}
	
	public List<PoolCarrierCarrierEntity> fetchCarriersForPoolBasic(Long poolId, Set<Long> carrierIds) {
		// [[
		List<PoolCarrierCarrierEntity> result = null;
		Object[][] params = {{"poolId", poolId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarriersForPoolBasic", params);
		}
		// ]]
		try {

			final ReadOnlyDao<PoolCarrierCarrierEntity> carrierDao = getDaoProvider().getPoolCarrierCarrierEntityDao();
			final Filter<PoolCarrierCarrierEntity> f = carrierDao.createFilter();
			f.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_POOL_ID, poolId));
			f.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_CARRIER_ACTIVE_FLAG, Boolean.TRUE));
			if (carrierIds != null && !carrierIds.isEmpty()) {
				List<Long> carrierIdList = new ArrayList<Long>(carrierIds);
				f.addExpression(DbUtils.createSplittedInRestriction(PoolCarrierCarrierEntity.ATTR_CARRIER_ID, carrierIdList));
			}
			f.addExpression(Order.asc(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));

			return result = carrierDao.find(f);

		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<CarrierEntity> findCarrierEntities(Set<Long> carrierIds) {
		// [[
		List<CarrierEntity> result = null;
		Object[][] params = {{"carrierIds", carrierIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findCarrierEntities", params);
		}
		// ]]
		try {

			if (carrierIds == null || carrierIds.isEmpty()) {
				return result = Collections.<CarrierEntity>emptyList();
			}

			List<Long> carrierIdList = new ArrayList<Long>(carrierIds);

			ReadOnlyDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			Filter<CarrierEntity> carrierFilter = dao.createFilter();
			carrierFilter.addExpression(DbUtils.createSplittedInRestriction(CarrierEntity.ATTR_CARRIER_ID, carrierIdList));
			carrierFilter.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, true));
			carrierFilter.addExpression(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));

			return result = dao.find(carrierFilter);

		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public CarrierEntity findArtificalGenericCarrier() {
		// [[
		CarrierEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findArtificalGenericCarrier");
		}
		// ]]
		try {
			final ReadOnlyDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			final Filter<CarrierEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(CarrierEntity.ATTR_CARRIER_ID, Constant.ARTIFICIAL_GENERIC_CARRIER_ID));
			f.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, true));
			result = dao.findSingle(f);
			
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
	
	//partner relations of the carriers from the pool as comm.carrier
	//-> refers to exec carrier
	private List<CommExecCarrierPfEntity> findPartnerExecCarriersOrdered(Expression carrierExpression) {
		final ReadOnlyDao<CommExecCarrierPfEntity> commExecCarrierPfDao = getDaoProvider().getCommExecCarrierPfEntityDao();
		Filter<CommExecCarrierPfEntity> filter = new FilterImpl<CommExecCarrierPfEntity>();
		if (carrierExpression != null) {
			filter.addExpression(carrierExpression);
		}
		filter.addExpression(Order.asc(CommExecCarrierPfEntity.ATTR_EXEC_CARRIER_DISPLAY_NAME));
		List<CommExecCarrierPfEntity> commExecCarrierPfList = commExecCarrierPfDao.find(filter);
		return commExecCarrierPfList;
	}
	
	/**
	 * 
	 * @param carrierList
	 * @return
	 */
	private List<Long> getCarrierIdList(List<CommExecCarrierPfEntity> carrierList) {
		// [[
		List<Long> result = null;
		Object[][] params = {{"carrierList", carrierList + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCarrierIdList", params);
		}
		// ]]
		try {
			result = new ArrayList<Long>();
			if (carrierList == null || carrierList.isEmpty()) {
				return result;
			}
			
			for (CommExecCarrierPfEntity carrier : carrierList) {
				result.add(carrier.getExecCarrierId());
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

	@AdnTrace
	public boolean checkIfCommCarrierAndExecCarrierArePartner(Long commCarrierId, Long execCarrierId) {
		return !(findPartnerExecCarriersOrdered(Restrictions.and(Restrictions.eq(CommExecCarrierPfEntity.ATTR_COMM_CARRIER_ID,
				commCarrierId), Restrictions.eq(CommExecCarrierPfEntity.ATTR_EXEC_CARRIER_ID, execCarrierId)))).isEmpty();
	}
}

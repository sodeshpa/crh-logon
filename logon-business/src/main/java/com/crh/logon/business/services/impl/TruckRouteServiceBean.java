/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.TruckRouteServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.TruckRouteServiceLocalInterface;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.views.TruckRouteSearchEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class TruckRouteServiceBean extends TruckRouteServiceDispatcher
	implements TruckRouteServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckRouteServiceBean.class);
	
	public List<TruckRouteSearchEntity> searchTruckRoutes(Map<String, String> searchCrit, String sort, boolean asc) {
		// [[
		List<TruckRouteSearchEntity> result = null;
		Object[][] params = {{"searchCrit", searchCrit},
							{"sort", sort},
							{"asc", asc + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchTruckRoutes", params);
		}
		// ]]
		try {
			Filter<TruckRouteSearchEntity> filter = new FilterImpl<TruckRouteSearchEntity>();
			if (!StringUtils.empty(searchCrit.get(TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID))) {
				filter.addExpression(
						Restrictions.eq(
								TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID, 
								Long.valueOf(searchCrit.get(TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID))
						)
				);
			}
			if (!StringUtils.empty(searchCrit.get(TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID))) {
				filter.addExpression(
						Restrictions.eq(
								TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID, 
								Long.valueOf(searchCrit.get(TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID))
						)
				);
			}
			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			ReadOnlyDao<TruckRouteSearchEntity> dao = getDaoProvider().getTruckRouteSearchEntityDao();
			List<TruckRouteSearchEntity> truckRoutes = dao.find(filter);
			
			return result = truckRoutes;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ListRange searchTruckRoutes(int skip, int num, String sort, boolean asc, Map<String, String> searchCrit) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchTruckRoutes", params);
		}
		// ]]
		try {
			Filter<TruckRouteSearchEntity> filter = new FilterImpl<TruckRouteSearchEntity>();
			if (!StringUtils.empty(searchCrit.get(TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID))) {
				filter.addExpression(
						Restrictions.eq(
								TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID, 
								Long.valueOf((String)searchCrit.get(TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID))
						)
				);
			}
			if (!StringUtils.empty(searchCrit.get(TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID))) {
				filter.addExpression(
						Restrictions.eq(
								TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID, 
								Long.valueOf((String)searchCrit.get(TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID))
						)
				);
			}
			String sapTrspModeCd = searchCrit.get(TruckRouteSearchEntity.ATTR_SAP_TRSP_MODE_CD);
			if (!StringUtils.empty(sapTrspModeCd)) {
				filter.addExpression(
						Restrictions.eq(
								TruckRouteSearchEntity.ATTR_SAP_TRSP_MODE_CD, 
								sapTrspModeCd // string
						)
				);
			}
			String langCd = searchCrit.get(TruckRouteSearchEntity.ATTR_LANG_CD);
			if (StringUtils.empty(langCd)) {
				// the table is now joined with a TX table so each row appears once per language
				// a caller of this service that does not provide a language filter
				// is likely not interested in the localized text, but wants
				// the other data (and only once!) - thus we just add an artificial filter
				// for one language
				langCd = "en";
				if (logDebugEnabled) {
					LOG.debug("No filter by " + TruckRouteSearchEntity.ATTR_LANG_CD + ", added default " + langCd);
				}
			}
			filter.addExpression(
					Restrictions.eq(
							TruckRouteSearchEntity.ATTR_LANG_CD, 
							langCd // string
					)
			);
			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			ReadOnlyDao<TruckRouteSearchEntity> dao = getDaoProvider().getTruckRouteSearchEntityDao();
			ListRange listRange = new ListRange();
			listRange.setData(dao.findNRecs(skip, num, filter).toArray());
			listRange.setTotalSize(dao.count(filter));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	public ListRange fetchShippingpointList(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShippingpointList", params);
		}
		// ]]
		try {
			
			Filter<ShippingpointEntity> filter = new FilterImpl<ShippingpointEntity>();
			filter.addExpression(Restrictions.ilike(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME, "%" + query + "%"));
			filter.addExpression(Order.asc(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));
			ReadWriteDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
			ListRange listRange = new ListRange();
			listRange.setData(dao.findNRecs(skip, num, filter).toArray());
			listRange.setTotalSize(dao.count(filter));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	public TruckRouteEntity getTruckRouteEntity(Long truckRouteId) {
		// [[
		TruckRouteEntity result = null;
		Object[][] params = {{"truckRouteId", truckRouteId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getTruckRouteEntity", params);
		}
		// ]]
		try {
			
			ReadWriteDao<TruckRouteEntity> dao = getDaoProvider().getTruckRouteEntityDao();
			
			return result = dao.findById(truckRouteId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void storeTruckRouteEntity(TruckRouteEntity truckRouteEntity) {
		// [[
		Object[][] params = {{"truckRouteEntity", truckRouteEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeTruckRouteEntity", params);
		}
		// ]]
		try {
			
			ReadWriteDao<TruckRouteEntity> dao = getDaoProvider().getTruckRouteEntityDao();
			if (truckRouteEntity.getTruckRouteId() != null) {
				dao.merge(truckRouteEntity);
			}
			else {
				dao.persist(truckRouteEntity);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
}

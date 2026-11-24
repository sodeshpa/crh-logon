/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.TruckRouteDataBean;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.TruckRouteService;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.views.TruckRouteSearchEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DwrMessageBean;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="TruckRouteAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.beans.TruckRouteDataBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.web.utils.DwrMessageBean"
 * @adnbus.dwr.signature method="saveTruckRoutes" parameters="List<TruckRouteDataBean> truckRoutes, HttpSession session" imports="java.util.List, com.crh.logon.admin.web.beans.TruckRouteDataBean, javax.servlet.http.HttpSession"
 * 
 */
public class TruckRouteAjaxService extends DwrServiceSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckRouteAjaxService.class);
	
	public ListRange searchTruckRoutes(
			int skip, 
			int count, 
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchTruckRoutes", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R);
			TruckRouteService truckRouteService = getServiceLocator().getTruckRouteService();
			String sortBy = TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_NAME;
			boolean sortAsc = true;
			if (!StringUtils.empty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				sortAsc = "ASC".equals(sortArr[1]);
			}
			// always query for current language only 
			query.put(TruckRouteSearchEntity.ATTR_LANG_CD, request.getLocale().getLanguage());
			
			ListRange listRange = truckRouteService.searchTruckRoutes(skip, count, sortBy, sortAsc, query);
			
			if ( (listRange != null) && (listRange.getData() != null) ) {
				Object[] searchTruckRouteData = listRange.getData();
				Object[] truckRouteDataData = new Object[searchTruckRouteData.length];
				for (int i=0;i<searchTruckRouteData.length;i++) {
					TruckRouteSearchEntity truckRouteSearchEntity = (TruckRouteSearchEntity)searchTruckRouteData[i];
					TruckRouteDataBean truckRouteDataBean = new TruckRouteDataBean();
			
					truckRouteDataBean.setDistanceCalculatedM(truckRouteSearchEntity.getDistanceCalculatedM());
					truckRouteDataBean.setDistanceCorrectionFactor(truckRouteSearchEntity.getDistanceCorrectionFactor());
					truckRouteDataBean.setCorrectedDistance(truckRouteSearchEntity.getCorrectedDistance());
					truckRouteDataBean.setEndShiptoBpCity(truckRouteSearchEntity.getEndShiptoBpCity());
					truckRouteDataBean.setEndShiptoBpId(truckRouteSearchEntity.getEndShiptoBpId());
					truckRouteDataBean.setEndShiptoBpName1(truckRouteSearchEntity.getEndShiptoBpName1());
					truckRouteDataBean.setEndShiptoBpPostalCode(truckRouteSearchEntity.getEndShiptoBpPostalCode());
					truckRouteDataBean.setEndShiptoBpSapId(truckRouteSearchEntity.getEndShiptoBpSapId());
					truckRouteDataBean.setRouteComment(truckRouteSearchEntity.getRouteComment());
					truckRouteDataBean.setStartShippingpointId(truckRouteSearchEntity.getStartShippingpointId());
					truckRouteDataBean.setStartShippingpointName(truckRouteSearchEntity.getStartShippingpointName());
					truckRouteDataBean.setTimeCalculatedMin(truckRouteSearchEntity.getTimeCalculatedMin());
					truckRouteDataBean.setTimeCorrectionFactor(truckRouteSearchEntity.getTimeCorrectionFactor());
					truckRouteDataBean.setTimePlanned(truckRouteSearchEntity.getTimePlanned());
					truckRouteDataBean.setTruckRouteId(truckRouteSearchEntity.getTruckRouteId());
					truckRouteDataBean.setSapTrspModeCd(truckRouteSearchEntity.getSapTrspModeCd());
					truckRouteDataBean.setSapTrspModeTxt(truckRouteSearchEntity.getSapTrspModeTxt());
					truckRouteDataBean.setLangCd(truckRouteSearchEntity.getLangCd());
					
					truckRouteDataData[i] = truckRouteDataBean;
				}
				
				listRange.setData(truckRouteDataData);
			}
			
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
   
    public ListRange fetchShippingpointList(
    		int skip, 
			int count, 
			String query,
			HttpServletRequest request,
			HttpSession session) {
	   // [[
	   ListRange result = null;
	   Object[][] params = {{"skip", skip + ""},
						   {"count", count + ""},
						   {"query", query},
						   {"request", request},
						   {"session", session}};
	   boolean logDebugEnabled = LOG.isDebugEnabled();
	   if (logDebugEnabled) {
		   LOG.entering(this, "fetchShippingpointList", params);
	   }
	   // ]]
	   try {
		   Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R);
		   TruckRouteService truckRouteService = getServiceLocator().getTruckRouteService();
		   ListRange listRange = truckRouteService.fetchShippingpointList(skip, count, query);
		   
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
   
   /**
    * From BestellungAjaxService
    * @param start
    * @param count
    * @param orderBy
    * @param query
    * @param bpIsSoldto
    * @param bpIsShipto
    * @param bpIsBillto
    * @param session
    * @return
    */
   public ListRange fetchSapBpList(
   		int start, 
   		int count, 
   		String orderBy,
   		String query,
   		boolean bpIsSoldto,
   		boolean bpIsShipto,
   		boolean bpIsBillto,
   		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"bpIsSoldto", bpIsSoldto + ""},
							{"bpIsShipto", bpIsShipto + ""},
							{"bpIsBillto", bpIsBillto + ""},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapBpList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R);
			ListRange listRange = new ListRange();
			
			BestellungService bestService = getServiceLocator().getBestellungService();
			listRange.setData( bestService.fetchSapBpList(start, count, query, bpIsSoldto, bpIsShipto, bpIsBillto, true).toArray());
			listRange.setTotalSize( bestService.countSapBpList(query, bpIsSoldto, bpIsShipto, bpIsBillto, true));
			
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
   
   	public DwrMessageBean[] saveTruckRoutes(List<TruckRouteDataBean> truckRoutes, HttpSession session) {
		// [[
		DwrMessageBean[] result = null;
		Object[][] params = {{"truckRoutes", truckRoutes},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveTruckRoutes", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_W);
			
			TruckRouteService truckRouteService = getServiceLocator().getTruckRouteService();
			if ((truckRoutes != null) && (!truckRoutes.isEmpty())) {
				for (Iterator iterator=truckRoutes.iterator();iterator.hasNext();) {
					TruckRouteDataBean truckRouteData = (TruckRouteDataBean)iterator.next();
					TruckRouteEntity truckRouteEntity = truckRouteService.getTruckRouteEntity(truckRouteData.getTruckRouteId());
					truckRouteEntity.setTimeCorrectionFactor(truckRouteData.getTimeCorrectionFactor());
					truckRouteEntity.setDistanceCorrectionFactor(truckRouteData.getDistanceCorrectionFactor());
					truckRouteEntity.setRouteComment(truckRouteData.getRouteComment());
					truckRouteService.storeTruckRouteEntity(truckRouteEntity);
				}
			}
			
			result = new DwrMessageBean[1];
			
			result[0] = new DwrMessageBean(DwrMessageBean.MsgTypes.SUCCESS,
					"", "", "TruckRoutes: " + (truckRoutes != null ? truckRoutes.size() : 0) + " processed.");
			
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
   
   
}

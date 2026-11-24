/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.admin.web.beans.WagonSearchResultBean;
import com.crh.logon.utils.LogonException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.WagonSearchBean;
import com.crh.logon.admin.web.forms.masterdata.WagonDetailForm;
import com.crh.logon.admin.web.forms.masterdata.WagonSearchForm;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.views.WagonSearchEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

import flexjson.JSONSerializer;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="WagonAdminAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.beans.WagonSearchBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.beans.WagonSearchResultBean"
 * @adnbus.dwr.signature method="searchWagons" parameters="int skip, int count, String sort, Map<String,WagonSearchBean> searchParameters,HttpServletRequest request, HttpSession session" imports="java.util.List, com.crh.logon.admin.web.beans.WagonSearchBean, javax.servlet.http.HttpSession"  
 * @adnbus.dwr.signature method="storeSearchSettings" parameters="int skip, int count, String sort, Map<String,WagonSearchBean> searchParameters, HttpServletRequest request, HttpSession session" imports="java.util.List, com.crh.logon.admin.web.beans.WagonSearchBean, javax.servlet.http.HttpSession" 
 */
public class WagonAdminAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonAdminAjaxService.class);
    
    public static final String WAGON_SEARCH = "wagonSearch";
    
    /**
	 * Used by Wagon Search 
     */
    public ListRange searchWagons(
    		int skip, 
    		int count, 
    		String sort,
    		HashMap<String,WagonSearchBean> searchParameters,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchParameters", searchParameters},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchWagons", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			String sortBy = WagonEntity.ATTR_WAGON_NO;
			boolean sortAsc = true;
			if (!StringUtils.empty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				sortAsc = "ASC".equals(sortArr[1]);
			}
			
			
			WagonSearchForm form = (WagonSearchForm) session.getAttribute("admin_WagonSearchForm");
			if (form != null){
				//				form.setWagonList(Arrays.asList(searchRes.getData()));
			
				Long destRailwayStationId = null;
				Boolean activeFlag = null;
				Long locationStationId = null;
				String wagonNo = null;
				List typeCdList = null;
				List orderStatusCdList = null;
				List distributionDestCdList = null;
				List inactiveReasonCdList = null;
				Long keeperId = null;
				
				WagonSearchBean wagonTypeSearchBean = searchParameters.get(WagonSearchEntity.ATTR_WAGON_TYPE_CD);
				WagonSearchBean wagonNoSearchBean = searchParameters.get(WagonSearchEntity.ATTR_WAGON_NO);
				WagonSearchBean lastStationSearchBean = searchParameters.get(WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID);
				WagonSearchBean keeperSearchBean = searchParameters.get(WagonSearchEntity.ATTR_WAGON_KEEPER_ID);
				WagonSearchBean orderStatusBean = searchParameters.get(WagonSearchEntity.ATTR_ORDER_STATUS_CD);
				WagonSearchBean distributionDestBean = searchParameters.get(WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD);
				WagonSearchBean destRailwayStationBean = searchParameters.get(WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID);
				WagonSearchBean inactiveReasonBean = searchParameters.get(WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD);
				WagonSearchBean activeFlagSearchBean = searchParameters.get(WagonSearchEntity.ATTR_ACTIVE_FLAG);
							
				if (wagonTypeSearchBean != null){
					String[] typeArr = !StringUtils.empty(wagonTypeSearchBean.getId()) ? wagonTypeSearchBean.getId().split(",") : null;
					if (typeArr != null) {
						typeCdList = new ArrayList();
						for (int i=0; i<typeArr.length; i++) {
							typeCdList.add(Long.valueOf(typeArr[i]));
						}
					}
				}				
				if (wagonNoSearchBean != null){
					wagonNo = !StringUtils.empty(wagonNoSearchBean.getId()) ? wagonNoSearchBean.getId() : null;
				}				
				if (lastStationSearchBean != null){
					locationStationId  = !StringUtils.empty(lastStationSearchBean.getId()) ? Long.valueOf(lastStationSearchBean.getId()) : null;
				}
				if (keeperSearchBean != null){
					keeperId  = !StringUtils.empty(keeperSearchBean.getId()) ? Long.valueOf(keeperSearchBean.getId()) : null;
				}
				if (orderStatusBean != null) {
					String[] statArr = !StringUtils.empty(orderStatusBean.getId()) ? orderStatusBean.getId().split(",") : null;
					if (statArr != null) {
						orderStatusCdList = new ArrayList();
						for (int i=0; i<statArr.length; i++) {
							orderStatusCdList.add(Long.valueOf(statArr[i]));
						}
					}
				}
				if (distributionDestBean != null){
					distributionDestCdList = !StringUtils.empty(distributionDestBean.getId()) ? Arrays.asList(distributionDestBean.getId().split(",")) : null;
				}
				if (destRailwayStationBean != null) {
					destRailwayStationId = !StringUtils.empty(destRailwayStationBean.getId()) ? Long.valueOf(destRailwayStationBean.getId()) : null;
				}
				if (inactiveReasonBean != null){
					String[] statArr = !StringUtils.empty(inactiveReasonBean.getId()) ? inactiveReasonBean.getId().split(",") : null;
					if (statArr != null) {
						inactiveReasonCdList = new ArrayList();
						for (int i=0; i<statArr.length; i++) {
							inactiveReasonCdList.add(Long.valueOf(statArr[i]));
						}
					}
				}
				if (activeFlagSearchBean != null){
					activeFlag  = !StringUtils.empty(activeFlagSearchBean.getId()) ? Boolean.valueOf(activeFlagSearchBean.getId()) : null;
				}

				// data is an array of WagonSearchEntity objects
				result = getServiceLocator().getWagonSearchService().searchWagons(
						skip, count, sortBy, sortAsc,request.getLocale(),
						typeCdList,
						wagonNo, locationStationId, keeperId,
						orderStatusCdList,
						destRailwayStationId,
						distributionDestCdList,
						inactiveReasonCdList,
						activeFlag);

				List<WagonSearchResultBean> convertedResults = convertSearchEntitiesToBeans(result.getData());

				result.setData(convertedResults.toArray());
				form.setWagonList(convertedResults);
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

	private List<WagonSearchResultBean> convertSearchEntitiesToBeans(Object[] data) {
		BeanUtilsBean beanUtilsBean = getBeanUtilsBean();

		List<WagonSearchResultBean> convertedResults =
				new ArrayList<WagonSearchResultBean>(data.length);
		for (Object o: data) {
			WagonSearchEntity wagon = (WagonSearchEntity)o;
			WagonSearchResultBean bean = new WagonSearchResultBean(getUserTimeZone());
			try {
				beanUtilsBean.copyProperties(bean, wagon);
			}
			catch (Exception e) {
				throw new LogonException(e);
			}

			convertedResults.add(bean);
		}

		return convertedResults;
	}
    
    /**
	 * Used by Wagon Search 
     */
    public ListRange getWagonNoListForPart(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagonNoListForPart", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			ListRange searchRes = getServiceLocator().getWagonSearchService().getWagonNoList(start, count, query);
			
			return result = searchRes;
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
	 * Used by Wagon Search 
     */
    public ListRange getReturnStationsForPart(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getReturnStationsForPart", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			ListRange searchRes = getServiceLocator().getWagonSearchService().getHomeRailwayStationList(start, count, query);
			
			return result = searchRes;
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
	 * Used by Wagon Search 
     */
    public ListRange getLocationStationsForPart(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLocationStationsForPart", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			ListRange searchRes = getServiceLocator().getWagonSearchService().getRailwayStationList(start, count, query);
			
			return result = searchRes;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public ListRange getRailwayStations(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailwayStations", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			ListRange searchRes = getServiceLocator().getWagonSearchService().getAllRailwayStations(start, count, query);
			
			return result = searchRes;
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
	 * Used by Wagon Detail last known station dropdown 
     */
    public ListRange getRailwayStationsForName(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailwayStationsForName", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			ListRange searchRes = getServiceLocator().getWagonDetailService().getRailwayStationListForName(start, count, query);
			WagonDetailForm form = (WagonDetailForm) request.getSession().getAttribute(WagonDetailForm.formName);
			if (form != null){
				form.getWagonDetailMain().setRailwayStationList(Arrays.asList(searchRes.getData()));
			}
			
			return result = searchRes;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public ListRange fetchTrackListForRailwayStation(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request,
    		HttpSession session) {
			// [[
			ListRange result = null;
			Object[][] params = {{"start", start + ""},
								{"count", count + ""},
								{"orderBy", orderBy},
								{"query", query},
								{"request", request},
								{"session", session}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "fetchTrackListForRailwayStation", params);
			}
			// ]]
			try {
				Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
				Long rId = Long.parseLong(query);
				List tracks = getServiceLocator().getWagonDetailService().fetchTrackListForRailwayStation(rId, request.getLocale().getLanguage());
				result = new ListRange();
				result.setTotalSize(tracks.size());
				result.setData(tracks.toArray());
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
	 * Used by Wagon Detail last known station dropdown 
     */
    public ListRange getRailwayStationsForNo(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailwayStationsForNo", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			ListRange searchRes = getServiceLocator().getWagonDetailService().getRailwayStationListForNo(start, count, query);
			WagonDetailForm form = (WagonDetailForm) request.getSession().getAttribute(WagonDetailForm.formName);
			if (form != null){
				form.getWagonDetailMain().setRailwayStationList(Arrays.asList(searchRes.getData()));
			}
			
			return result = searchRes;
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
     * Called when the detail page is called. In order to be able to use previous search settings need to store those.
     * @param request
     */
    public void storeSearchSettings(
    		int skip, 
    		int count, 
    		String sort,
    		HashMap<String,WagonSearchBean> searchParameters,
    		HttpServletRequest request,
    		HttpSession session) {
    	Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
    	
    	WagonSearchForm form  = (WagonSearchForm) session.getAttribute("admin_WagonSearchForm");
    	form.setSearchSkip(skip);
    	form.setSearchCount(count);
    	form.setSearchBean(searchParameters);
    }

	
    protected JSONSerializer getJsonSerializer() {
    	return new JSONSerializer()
	    	.exclude("*.class")
	    	.exclude("*.ctlActCd")
	    	.exclude("*.ctlCreTs")
	    	.exclude("*.ctlCreUid")
	    	.exclude("*.ctlModTs")
	    	.exclude("*.ctlModUid");
    }

	public double checkBimodalLoadBalance(Long orderId, HttpServletRequest request) {
		// [[
		double result = 0.0;
		Object[][] params = {{"orderId", orderId},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "checkBimodalLoadBalance", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAILCARS_FREIMELDEN);
			return result = getServiceLocator().getDispoService().checkBimodalLoadBalance(orderId);
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
	 * Returns a list of damageCodeSearchEntities according to the query. Refdataservice not used because cannot be used 
	 * with paging. 
	 */
	public ListRange fetchDamageCodeList(int start, int count, String orderBy, String query, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchDamageCodeList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
			
			Locale locale = RequestUtils.getUserLocale(request, null);
			
			return result = getServiceLocator().getOrderDetailService().loadDamageCodes(start, count, query, locale);
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.views.ShiptoSareaRestrSearEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="DispocardViewAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange" 
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.OrderTakingEditorParamsDto" 
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.OrderTakingEditorDto" exclude="salesAreaMap,order,orderHelper,fullShippintpointList,fullSupplyPlantList,possibleSupplyPlantCdSet,possibleShippingPointIdSet,possibleShippingPointNoSet"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.SplittedOrderData"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.DeliverySettingDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.beans.MaterialQuantityUomBean"
 * 
 */
public class DispocardViewAjaxService extends DwrServiceSupport {
	
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispocardViewAjaxService.class);
    
    public static final String CALLER_ORDER_CREATE = "orderCreate";
    public static final String CALLER_ORDER_SEARCH = "orderSearch";
    public static final String CALLER_DISPOKARTE = "dispokarte";
    
    public ListRange fetchSapMaterialList(
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
			LOG.entering(this, "fetchSapMaterialList", params);
		}
		// ]]
		try {
			//monitoring and order list does not have the same visibility rules. entryactions are authorized.
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			return result = getServiceLocator().getBestellungService().fetchSapMaterialList(start, count, query, request.getLocale());
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}  
    
    public ListRange fetchCarrierList(
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
    		LOG.entering(this, "fetchCarrierList", params);
    	}
    	// ]]
    	try {
    		//monitoring and order list does not have the same visibility rules. entryactions are authorized.
    		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
    		return result = getServiceLocator().getDispokarteService().fetchCarrierList(start, count, query);
    		
    	}
    	finally {
    		// [[
    		if (logDebugEnabled) {
    			LOG.exiting(result);
    		}
    		// ]]
    	}
    }  

    public List getCarrierListByPoolId(Long poolId, HttpServletRequest request) {
		// [[
		List result = null;
		Object[][] params = {{"poolId", poolId},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCarrierListByPoolId", params);
		}
		// ]]
		try {
			
			//monitoring and order list does not have the same visibility rules. entryactions are authorized.
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			Locale locale = request.getLocale();
			
			Set<Long> poolIdSet = new HashSet<Long>();
			poolIdSet.add(poolId);
			
			return result = getServiceLocator().getPoolAdminService().fetchPoolCarrierCarrierEntitysByPoolIds(poolIdSet , locale);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public ListRange searchDispocard(
    		int skip, 
    		int count, 
    		String sort,
    		HashMap<String, String> searchCrits,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchCrits", searchCrits},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchOrders", params);
		}
		// ]]
		try {
			//monitoring and order list does not have the same visibility rules. entryactions are authorized.
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			String sortBy = ShiptoSareaRestrSearEntity.ATTR_LANG_CD;
			final boolean sortAsc;
			
			if (!StringUtils.isEmpty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];

				sortAsc = "ASC".equals(sortArr[1]);
			}
			else {
				sortAsc = true;
			}
			
			adjustSearchCrits(searchCrits, request.getLocale());
			ListRange searchRes = getDispokarteService().dispocardSearch(skip, count, sortBy, sortAsc, searchCrits, request.getLocale());
			
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
       
    private void adjustSearchCrits(HashMap<String, String> searchCrits, Locale locale) {
    	
    	searchCrits.put(ShiptoSareaRestrSearEntity.ATTR_LANG_CD, locale.getLanguage());
    }
}

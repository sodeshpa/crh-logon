/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax.masterdata;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.views.CarrierSvendorEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to
 * AJAX components.
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="CarrierAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class CarrierAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CarrierAjaxService.class);

	/**
	 * Used by the Master Data / Carrier screen / Carrier filter.
	 * 
	 * Load all carriers (object of {@link CarrierSvendorEntity}) where the 
	 * TLGNS_SAP_VENDOR.VN_IS_COMM_CARRIER attribute is '1' (true).
	 * 
	 * @param start
	 * @param count
	 * @param orderBy
	 * @param query
	 * @param request
	 * @param session
	 * @return
	 */
    public ListRange fetchCommercialCarrierList(
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
    		LOG.entering(this, "fetchCommercialCarrierList", params);
    	}
    	// ]]
    	try {
    		Authorization.authorize(Authorization.MASTER_DATA_CARRIER_VIEW);
			return result = getServiceLocator().getCarrierService().fetchCommercialCarrierList(
					start, count, query);
    		
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
     * Used by the Master Data / Carrier screen / Error filter.
	 * 
	 * Load a list of vendors (object of {@link CarrierSvendorEntity}) where an error occured 
	 * at the SAP import. The TLGNS_SAP_VENDOR.UPDATE_BLOCKED_BY_HJID is not null.
	 * 
     * @param start
     * @param count
     * @param orderBy
     * @param query
     * @param request
     * @param session
     * @return
     */
    public ListRange fetchErrorVendorList(
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
    		LOG.entering(this, "fetchErrorVendorList", params);
    	}
    	// ]]
    	try {
    		Authorization.authorize(Authorization.MASTER_DATA_CARRIER_VIEW);
			return result = getServiceLocator().getCarrierService().fetchErrorVendorList(start,
					count, query);
    		
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
     * @param count
     * @param sort
     * @param searchCrits
     * @param request
     * @param session
     * @return
     */
    public ListRange searchCarrier(
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
			LOG.entering(this, "searchCarrier", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_CARRIER_VIEW);
			
			String sortBy = CarrierSvendorEntity.ATTR_CARRIER_ID;
			final boolean sortAsc;
			
			if (StringUtils.isNotEmpty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];

				sortAsc = "ASC".equals(sortArr[1]);
			}
			else {
				sortAsc = true;
			}
			
			ListRange searchRes = getServiceLocator().getCarrierService().carrierSearch(skip,
					count, sortBy, sortAsc, searchCrits, request.getLocale());
			
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
}

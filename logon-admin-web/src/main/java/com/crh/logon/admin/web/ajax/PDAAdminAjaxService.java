/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.PDAAdminService;
import com.crh.logon.persistence.entities.common.PdaDeviceEntity;
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
 * @adnbus.service name="PDAAdminAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * 
 */
public class PDAAdminAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PDAAdminAjaxService.class);
    
    public static final String CALLER_ORDER_CREATE = "orderCreate";
    public static final String CALLER_ORDER_SEARCH = "orderSearch";
 
    public ListRange searchPDAs(
    		int skip, 
    		int count, 
    		String sort,
    		HashMap<String, String> query,
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
			LOG.entering(this, "searchPDAs", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_PDA);
			PDAAdminService pdaService = getServiceLocator().getPDAAdminService();
			//HashMap searchCrit = new HashMap();
			String sortBy = PdaDeviceEntity.ATTR_PDA_DEVICE_ID;
			boolean sortAsc = true;
			if (!StringUtils.isEmpty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				sortAsc = "ASC".equals(sortArr[1]);
			}
			
			return result = pdaService.fetchPDADeviceCarrierEntityList(skip, count, sortBy, sortAsc, query);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

    public ListRange fetchSimCardList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		boolean bySimNr,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"bySimNr", bySimNr + ""},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSimCardList", params);
		}
		// ]]
		try {
			// [[
				Authorization.authorize(Authorization.USER_ADMIN_PDA);
			
				ListRange listRange = new ListRange();
				
				PDAAdminService pdaService = getServiceLocator().getPDAAdminService();
			
				listRange.setData(pdaService.fetchSimCardBsEntityList(start, count, query, bySimNr).toArray());
				listRange.setTotalSize(pdaService.countSimCardBsEntityList(query, bySimNr));
				
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
    
}

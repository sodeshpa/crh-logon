/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;
import java.util.List;

import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.SiloAdminService;
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
 * @adnbus.service name="SiloAdminAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.persistence.entities.common.ShiptoEntity"
 */
public class SiloAdminAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SiloAdminAjaxService.class);
    
    public ListRange fetchShiptoList(
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
			LOG.entering(this, "fetchShiptoList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
			ListRange listRange = new ListRange();
			
			SiloAdminService service = getServiceLocator().getSiloAdminService();
			
			List shipTos  = service.fetchShipto(start, count, orderBy, query);
			listRange.setData( shipTos.toArray());
			listRange.setTotalSize( shipTos.size() );
			
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

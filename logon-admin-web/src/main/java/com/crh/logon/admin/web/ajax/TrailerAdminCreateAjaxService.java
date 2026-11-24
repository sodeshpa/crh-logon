package com.crh.logon.admin.web.ajax;

import java.util.List;

import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.traileradmin.TrailerAdminForm;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
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
 * @adnbus.service name="TrailerAdminCreateAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * 
 */
public class TrailerAdminCreateAjaxService  extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TrailerAdminCreateAjaxService.class);
    
    /**
	 * Called when Create Trailer popup is called to select SapVehicleId
	 */
	public ListRange fetchGenericTrailerList(int skip, int num, String sort, String query,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchGenericTrailerList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
			ListRange listRange = new ListRange();
		
			TrailerAdminForm trailerAdminForm = (TrailerAdminForm) session.getAttribute(TrailerAdminForm.formName);
			Long carrierId = trailerAdminForm.getCarrierId();
			
			TrailerAdminService trailerAdminService = getServiceLocator().getTrailerAdminService();
			List<SapVehicleEntity> genericTrucks = trailerAdminService.fetchGenericSapTrailersOfCarrier(carrierId, skip, num, query);
			
			listRange.setData(genericTrucks.toArray());
			listRange.setTotalSize(genericTrucks.size());
			
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

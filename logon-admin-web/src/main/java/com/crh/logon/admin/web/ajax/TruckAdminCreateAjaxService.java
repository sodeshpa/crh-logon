package com.crh.logon.admin.web.ajax;

import java.util.List;

import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm;
import com.crh.logon.business.services.interfaces.TruckAdminService;
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
 * @adnbus.service name="TruckAdminCreateAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * 
 */
public class TruckAdminCreateAjaxService  extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckAdminCreateAjaxService.class);
    
    /**
	 * Called when Create Truck popup is called to select SapVehicleId 
	 */
	public ListRange fetchGenericTruckList(int skip, int num, String sort, String query,
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
			LOG.entering(this, "fetchGenericTruckList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
			ListRange listRange = new ListRange();
		
			TruckAdminForm truckAdminForm = (TruckAdminForm) session.getAttribute(TruckAdminForm.formName);
			Long carrierId = truckAdminForm.getCarrierId();
			
			TruckAdminService truckAdminService = getServiceLocator().getTruckAdminService();
			List<SapVehicleEntity> genericTrucks = truckAdminService.fetchGenericSapTrucksOfCarrier(carrierId, skip, num, query);
			
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

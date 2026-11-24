/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.truckadmin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.ForwardingAgentBean;
import com.crh.logon.admin.web.forms.truckadmin.PoolAdminForm;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_PoolAdminForm"
 *  path="/truckadmin/PoolAdminEntry" scope="session"
 *  validate="false" input="/truckadmin/TruckAdminPool.def"
 *
 * @struts.action-forward name="success" path="/truckadmin/TruckAdminPool.def"
 */
public class PoolAdminEntryAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PoolAdminEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_POOL);			
			PoolAdminForm helpForm = (PoolAdminForm) form;
			helpForm.clear();
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("pool", request);
			String forwardName = "success";
			
			// TODO boon, 20090818: All data is retrieved at once from the service, instead of various calls.
			// This speeds up enormously. However, the current design of this page should be reconsidered.
			// (1) Make a dedicated bean instead of a Map to transfer the data from the service.
			// (2) Get rid of the pool list form the ForwardAgentBean (why add the same list to all instances of this bean?)
			PoolAdminService poolAdminService = getServiceLocator().getPoolAdminService();
			List poolAndCarriers = poolAdminService.fetchPoolsAndCarriers(request.getLocale());
			Map fwaMap = new HashMap();
			if ((poolAndCarriers != null) && (!poolAndCarriers.isEmpty())) {
				for (Iterator it = poolAndCarriers.iterator();it.hasNext();) {
					Map poolData = (Map)it.next();
					SapVendorEntity sapVendorEntity = (SapVendorEntity)poolData.get("sapVendorEntity");
					if (sapVendorEntity != null) {
						fwaMap.put(sapVendorEntity.getSapVendorId(), new ForwardingAgentBean(sapVendorEntity, (CarrierEntity)poolData.get("carrierEntity"), (List)poolData.get("pools"), (List)poolData.get("poolListForCarrier")));
					}
				}
			}
			
			helpForm.setFwaMap(fwaMap);
			
			return result = mapping.findForward(forwardName);
			
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

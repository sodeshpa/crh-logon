/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.truckadmin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.beans.ForwardingAgentBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.truckadmin.PoolAdminForm;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_PoolAdminForm"
 *  path="/truckadmin/PoolAdmin" scope="session"
 *  validate="false" input="/truckadmin/PoolAdmin.def"
 *
 * @struts.action-forward name="success" path="/truckadmin/PoolAdminEntry.do"
 */
public class PoolAdminAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PoolAdminAction.class);
    
    public static final String FORWARD_SAVE = "save";
    
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
			String forwardName = "success";
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("truck", request);
			PoolAdminForm helpForm = (PoolAdminForm) form;
			
			PoolAdminService service = getServiceLocator().getPoolAdminService();
			
			if (request.getParameter("save") != null) {
				
				Map carriersToSave = new HashMap();
				Map poolsToSaveForCarriers = new HashMap();
				
				Map fwaMap = helpForm.getFwaMap();
				
				for (Iterator iterator = fwaMap.keySet().iterator(); iterator.hasNext();) {
					Long key = (Long) iterator.next();
					ForwardingAgentBean fwaBean = (ForwardingAgentBean) fwaMap.get(key);
			
					if (fwaBean.isEdited()) {
						CarrierEntity carrier = fwaBean.getCarrier();
						carriersToSave.put(key, carrier);
						poolsToSaveForCarriers.put(key, fwaBean.getSelectedPools());
					}
				}
			
				service.saveCarriersAndAssignPools(carriersToSave, poolsToSaveForCarriers);
			
				MessageUtils.addActionMessage(request, "poolAdmin.message.savecompleted");
			}
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

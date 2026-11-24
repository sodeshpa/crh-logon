/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.externalsiteadmin;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.externalsiteadmin.ExternalSiteAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ExternalSiteService;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ExternalSiteAdminForm"
 *  path="/externalsiteadmin/ExternalSiteAdminEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/externalsiteadmin/ExternalSite.def"
 */

public class ExternalSiteAdminEntryAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalSiteAdminEntryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			// FIXME (szilagyi Jul 27, 2010):
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_EXT_SITE_R);
			
			
			Locale locale = getLocale(request);
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("externalsite", request);
			String forwardName = "success";
			
			ExternalSiteAdminForm externalSiteAdminForm;
			externalSiteAdminForm = (ExternalSiteAdminForm) form;
			externalSiteAdminForm.clear();
			
			ExternalSiteService externalSiteAdminService = getServiceLocator().getExternalSiteService();
			
			initFormMaps(externalSiteAdminForm, externalSiteAdminService);
			LOG.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
			
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


	/**
	 * @param helpForm
	 * @param externalSiteAdminService
	 */
	protected static void initFormMaps(ExternalSiteAdminForm helpForm, ExternalSiteService externalSiteAdminService) {
		//Carrier. Convert list to map.
		List carrierList = externalSiteAdminService.fetchCarriers();
		Map carrierMap = new LinkedHashMap();
		for (Iterator it = carrierList.iterator(); it.hasNext();) {
			CarrierEntity carrier = (CarrierEntity) it.next();
			carrierMap.put(carrier.getCarrierId(), carrier);
		}
		helpForm.setCarrierMap(carrierMap);

	}

}

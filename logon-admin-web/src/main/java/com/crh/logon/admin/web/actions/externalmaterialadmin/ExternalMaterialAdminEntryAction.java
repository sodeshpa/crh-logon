/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.externalmaterialadmin;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.externalmaterialadmin.ExternalMaterialAdminForm;
import com.crh.logon.business.services.interfaces.ExternalMaterialService;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ExternalMaterialAdminForm"
 *  path="/externalmaterialadmin/ExternalMaterialAdminEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/externalmaterialadmin/ExternalMaterial.def"
 */

public class ExternalMaterialAdminEntryAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalMaterialAdminEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_EXT_MAT_R);
			
			
			Locale locale = getLocale(request);
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("externalmaterial", request);
			String forwardName = "success";
			
			ExternalMaterialAdminForm externalMaterialAdminForm;
			externalMaterialAdminForm = (ExternalMaterialAdminForm) form;
			externalMaterialAdminForm.clear();
			
			ExternalMaterialService externalMaterialAdminService = getServiceLocator().getExternalMaterialService();
			
			initFormMaps(externalMaterialAdminForm, externalMaterialAdminService);
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
	 * @param externalMaterialAdminService
	 */
	protected static void initFormMaps(ExternalMaterialAdminForm helpForm, ExternalMaterialService externalMaterialAdminService) {
		//Carrier. Convert list to map.
		List carrierList = externalMaterialAdminService.fetchCarriers();
		Map carrierMap = new LinkedHashMap();
		for (Iterator it = carrierList.iterator(); it.hasNext();) {
			CarrierEntity carrier = (CarrierEntity) it.next();
			carrierMap.put(carrier.getCarrierId(), carrier);
		}
		helpForm.setCarrierMap(carrierMap);

	}

}

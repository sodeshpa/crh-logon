/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.siloadmin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.siloadmin.SiloAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.SiloAdminService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SiloTypeTxEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_SiloAdminForm"
 *  path="/siloadmin/SiloAdminEntry" scope="session"
 *  validate="false" input="/siloadmin/SiloAdmin.def"
 *
 * @struts.action-forward name="success" path="/siloadmin/SiloAdmin.def"
 */
public class SiloAdminEntryAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SiloAdminEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
			AdminRFIDPopupForm.resetForm(request.getSession());
			SiloAdminForm helpForm = (SiloAdminForm) form;
			helpForm.clear();
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("silo", request);
			String forwardName = "success";
			RfidobjectTypeBsEntity bs = getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_SHIPTO_SILO);
			helpForm.setSiloRefDataBsEntity(bs);
			
			SiloAdminService  service = getServiceLocator().getSiloAdminService();
			List siloList = service.fetchSilos();
			helpForm.setSiloList(siloList);
			if (siloList != null && siloList.size() >= 1) {
				ShiptoSiloEntity silo = (ShiptoSiloEntity) siloList.get(0);
				Long siloId = silo.getShiptoSiloId();
				silo = service.fetchSilo(siloId);
				helpForm.setSilo(silo);
				helpForm.resetSiloAndSession(request, silo);				
				helpForm.mapObjectToForm(helpForm.getSilo(), service, request.getLocale());
			}
			
			List list = getRefDataGenericsService().findTxEntityList(SiloTypeTxEntity.class, request.getLocale());
			helpForm.setSiloTypeList(list);
			
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

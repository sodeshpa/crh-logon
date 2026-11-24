/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.truckadmin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.TruckPriorityUtil;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_TruckAdminForm"
 *  path="/truckadmin/TruckAdminEntry" scope="session"
 *  validate="false" input="/truckadmin/TruckAdmin.def"
 *
 * @struts.action-forward name="success" path="/truckadmin/TruckAdmin.def"
 */
public class TruckAdminEntryAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TruckAdminEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);			
			AdminRFIDPopupForm.resetForm(request.getSession());
			TruckAdminForm helpForm = (TruckAdminForm) form;
			helpForm.clear();
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("truck", request);
			String forwardName = "success";
			TrailerAdminService  service = getServiceLocator().getTrailerAdminService();
			RfidobjectTypeBsEntity bs = getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_TRUCK);
			helpForm.setTruckRefDataBsEntity(bs);
			List carrierList = service.fetchCarriers();
			helpForm.setCarrierList(carrierList);			
			
			populateTruckPriorities(helpForm);
			
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
	 * Populates the truckPriorities array of TruckAdminForm.
	 * 
	 * @param truckAdminForm - the form instance
	 */
	private void populateTruckPriorities(TruckAdminForm truckAdminForm) {
		long[] truckPriorities = TruckPriorityUtil.createTruckPriorityArray();

		truckAdminForm.setTruckPriorities(truckPriorities);
	}
}

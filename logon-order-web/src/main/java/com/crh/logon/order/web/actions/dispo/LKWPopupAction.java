/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.LKWAvailabilityChangeService;
import com.crh.logon.order.web.forms.dispo.LKWPopupForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_LKWPopupForm"
 *  path="/dispo/LKWPopup" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="LKWPopupTile.def"
 * @struts.action-forward name="failure" path="LKWPopupTile.def"
 * @struts.action-forward name="lkwpopuperror" path="LKWPopupErrorTile.def"  
 */
public class LKWPopupAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(LKWPopupAction.class);

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
			Authorization.authorize(Authorization.DISPATCHING_W);
			setSelectedTab("dispo", request);
			
		    // Get the struts form.
			LKWPopupForm helpForm = (LKWPopupForm)form;
			String forwardName = "success";
			Date from = null;
			Date until = null;
			LKWAvailabilityChangeService service = getServiceLocator().getLKWAvailabilityChangeService();
			setSelectedTab("dispokarte", request);
			String reason = helpForm.getInputEvent();
			//save - save new, edit - save existing
			if ( helpForm.getInputAction().equals("save") || helpForm.getInputAction().equals("edit")) {				
				try {
					from = helpForm.getTimeStart();
				}
				catch (Exception e) {
					MessageUtils.addActionMessage(request, "lkwpopup.error.dateformaterror");
					return result = mapping.findForward("lkwpopuperror");
				}
				try {
					until = helpForm.getTimeUntil();
				}
				catch (Exception e) {
					MessageUtils.addActionMessage(request, "lkwpopup.error.dateformaterror");
					return result = mapping.findForward("lkwpopuperror");
				}
				if ( from.after( until )) {					
					MessageUtils.addActionMessage(request, "lkwpopup.error.beforelaterthanafter");
					return result = mapping.findForward("lkwpopuperror");
				}
				
				if (helpForm.getInputAction().equals("save")){
					service.storeTruckUnavailability(helpForm.getTruck().getTruckId(), reason, from, until);
				}
				else if (helpForm.getInputAction().equals("edit")){
					long id = helpForm.getInputTruckUnavailabilityId();
					service.updateTruckUnavailability(id, reason, from, until);
				}
			}
			else if (helpForm.getInputAction() .equals("delete") ){
				long id = helpForm.getInputTruckUnavailabilityId();
				service.deleteTruckUnavailability(id);				
			}
			
			List unAvailabilities = service.fetchTruckUnavailabilities(helpForm.getTruck().getTruckId());
			helpForm.setTruckUnavailabilities(unAvailabilities);
			helpForm.reset(mapping, request);
			
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

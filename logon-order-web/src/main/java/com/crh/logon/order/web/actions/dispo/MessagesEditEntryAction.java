/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.dispo.DispoForm;
import com.crh.logon.order.web.forms.dispo.MessagesEditForm;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_MessagesEditForm" 
 *  path="/dispo/MessagesEditEntry" scope="session"
 *  validate="true" input="DispoMessagesEditTile.def"
 *
 * @struts.action-forward name="edit" path="DispoMessagesEditTile.def"
 */
public class MessagesEditEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(MessagesEditEntryAction.class);

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
			// Get the struts form(s).
			Authorization.authorize(Authorization.DISPATCHING_W);
			MessagesEditForm helpForm = (MessagesEditForm) form;
			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_dispo_DispoForm");
			
			String forwardName = "edit";
			
			helpForm.setDay(dispoForm.getDate());
			helpForm.setDriver(getServiceLocator().getDispoService().fetchDriver(helpForm.getDriverId()));
			
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

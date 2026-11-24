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
import com.crh.logon.order.web.forms.dispo.DriverBreakForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_DriverBreakForm" 
 *  path="/dispo/DriverBreak" scope="session"
 *  validate="true" input="DriverBreakEditTile.def"
 *
 * @struts.action-forward name="close" path="DriverBreakCloseTile.def"
  */
public class DriverBreakAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DriverBreakAction.class);

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
			DriverBreakForm helpForm = (DriverBreakForm) form;
			
		    String forwardName = "close";
			
		    if (request.getParameter("save") != null) {
		    	getServiceLocator().getDispoService().persistDriverBreak(
		    			helpForm.getDriverBreak());
		    }			
		    else if (request.getParameter("delete") != null) {
		    	getServiceLocator().getDispoService().deleteDriverBreak(
		    			helpForm.getDriverBreak().getDriverBreakId());
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

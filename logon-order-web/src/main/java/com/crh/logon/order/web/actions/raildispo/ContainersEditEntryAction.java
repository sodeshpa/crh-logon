/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.raildispo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.raildispo.ContainersEditForm;
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
 * @struts.action name="order_raildispo_ContainersEditForm" 
 *  path="/raildispo/ContainersEditEntry" scope="session"
 *  validate="true" input="ContainersEditTile.def"
 *
  * @struts.action-forward name="edit" path="ContainersEditTile.def"
 */
public class ContainersEditEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ContainersEditEntryAction.class);

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
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
		    // Get the struts form(s).
			ContainersEditForm helpForm = (ContainersEditForm) form;
			//			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_dispo_DispoForm");
			
		    String forwardName = "edit";
		    
		    if (helpForm.getWagonId() != null){
		    	helpForm.setWagon(getServiceLocator().getDispoService()
		    		.fetchWagon(helpForm.getWagonId()));
		    	helpForm.setContainers(getServiceLocator().getDispoService()
			    	.fetchContainersOfWagon(helpForm.getWagonId()));
		    	helpForm.setLoads(getServiceLocator().getDispoService()
			    	.fetchLoadsOfContainers(helpForm.getContainerIds()));
		    	helpForm.setOrderItems(getServiceLocator().getDispoService()
				    	.fetchOrderItemsOfLoads(helpForm.getLoadIds()));
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.raildispo;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.raildispo.ContainersEditForm;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_raildispo_ContainersEditForm" 
 *  path="/raildispo/ContainersEdit" scope="session"
 *  validate="true" input="ContainersEditTile.def"
 *
 * @struts.action-forward name="close" path="ContainersEditCloseTile.def"
  */
public class ContainersEditAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ContainersEditAction.class);

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
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			ContainersEditForm helpForm = (ContainersEditForm) form;
			
		    String forwardName = "close";
			
		    if (request.getParameter("save") != null) {
		    	LinkedList remove = new LinkedList();
		    	LinkedList add = new LinkedList();
		    	for (int i = 0; i < helpForm.getNewContainers().size(); i++){
		    		String newNo = (String)helpForm.getNewContainers().get(i);
	    			ContainerEntity c = (helpForm.getContainers().size() > i)?(ContainerEntity)helpForm.getContainers().get(i):null;
		    		if (newNo != null){
		    			if (c != null){
		    				if (!newNo.equals(c.getContainerNo())) {
		    					add.add(newNo);
		    					remove.add(c);
		    				}
		    			}
		    			else {
		    				add.add(newNo);
		    			}
		    		}
		    		else if (c != null){
		    			remove.add(c);
		    		}
		    	}
		    	
		    	getServiceLocator().getDispoService().changeWagonComposition(
		    			helpForm.getWagon(), remove, add);
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

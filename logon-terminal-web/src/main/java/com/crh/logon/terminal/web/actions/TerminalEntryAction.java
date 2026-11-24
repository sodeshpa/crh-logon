/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action path="/TerminalEntry"
 *
 * @struts.action-forward name="customer" path="/pickup/PickupEntry.do"
 * @struts.action-forward name="chaffeur" path="/bagterminal/BagTerminalEntry.do"
 */
public class TerminalEntryAction extends ActionSupport {

    private static final JcanLogger LOG = 
    	JcanLoggerFactory.getLogger(TerminalEntryAction.class);

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
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
			String forwardName = "customer";
			
			if( Authorization.isAllowed(Authorization.BAG_ORDER_TERMINAL_R)) {
				forwardName = "chaffeur";
			}
			
			inactivateAllTabs(request);
			
			
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

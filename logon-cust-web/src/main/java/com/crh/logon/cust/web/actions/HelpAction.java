/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * The HelpAction forwards to the HelpTile.
 *
 * @struts.action path="/Help"
 *
 * @struts.action-forward name="help" path="Help.def"
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class HelpAction extends ActionSupport {
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(HelpAction.class);

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
		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
        String forwardName = "help";
		try {
			return result = mapping.findForward(forwardName);
		}
        catch (Exception ex) {
            // Forward to fatal error page.
            setFatalError(request, ex);
            forwardName = "fatalError";
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

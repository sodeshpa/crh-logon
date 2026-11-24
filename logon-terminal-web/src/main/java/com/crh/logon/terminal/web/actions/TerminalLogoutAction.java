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
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.web.forms.FatalErrorForm;
import com.crh.logon.web.utils.Authorization;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * The LogoutAction invalidates user session.
 * <br>
 * NOTE: Please assure, that target jsp does not refer to user session!
 *
 * @struts.action path="/Logout"
 *
 * @struts.action-forward name="logout" path="Logout.def"
 * @struts.action-forward name="logoutFatalError" path="LogoutFatalError.def"
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class TerminalLogoutAction extends TerminalActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TerminalLogoutAction.class);

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
        String forwardName = "logout";
		try {
			FatalErrorForm fatalform = (FatalErrorForm) request.getSession().getAttribute("FatalErrorForm");
			if ( fatalform != null && fatalform.getErrorId() != null ) {
				LOG.error(fatalform.getErrorId());
				if ( fatalform.getStackTrace() != null ) {					
					LOG.error(fatalform.getStackTrace());
				}
				if (fatalform.getReason() != null ) {
					LOG.error(fatalform.getReason());
				}
			}
        	// invalidate user session
        	HttpSession session = request.getSession(false);
        	if (session != null) {
        		session.invalidate();
        	}
			return result = mapping.findForward(forwardName);
		}
        catch (Exception ex) {
            // Forward to fatal error page.
            setFatalError(request, ex);
            forwardName = "logoutFatalError";
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


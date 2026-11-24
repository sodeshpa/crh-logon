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
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.business.services.interfaces.UserActivityService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ServiceLocatorProvider;

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
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class LogoutAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(LogoutAction.class);

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
			
        	// invalidate user session
        	HttpSession session = request.getSession(false);
        	if (session != null) {
        		try {
        			String loginType = (String) session.getAttribute(Constant.LOGON_LOGIN_TYPE_SESSION_ATTR_KEY);
        			if (loginType != null && !Constant.LOGIN_TYPE_LOGGED_OUT.equals(loginType)) {
	        			UserEntity user = (UserEntity) session.getAttribute(Constant.USER_FILTER_USER_SESSION_ATTR_KEY);
		        		UserActivityService uas = ServiceLocatorProvider.getServiceLocator().getUserActivityService();
		        		uas.recordLogOut(user.getUserId(), loginType, false);
		        		session.setAttribute(Constant.LOGON_LOGIN_TYPE_SESSION_ATTR_KEY, Constant.LOGIN_TYPE_LOGGED_OUT);
        			}
        		}
        		catch (Exception e) {
        			LOG.error("could not record logout user activity", e);
        		}
        		
        		session.invalidate();
        	}
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


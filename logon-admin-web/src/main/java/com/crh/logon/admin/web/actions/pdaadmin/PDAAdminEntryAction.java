/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.pdaadmin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.pdaadmin.PDAAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

import flexjson.JSONSerializer;

/**
 * @author AdNovum Informatik AG
 *
 * @struts.action name="admin_PDAAdminForm"
 *  path="/pdaadmin/PDAAdminEntry" scope="session"
 *  validate="false" input="/pdaadmin/PDAAdminTile.def"
 *
 * @struts.action-forward name="success" path="/pdaadmin/PDAAdminSearchTile.def"
 */
public class PDAAdminEntryAction extends ActionSupport {

	 private static final JcanLogger LOG =
	        JcanLoggerFactory.getLogger(PDAAdminEntryAction.class);

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
				Authorization.authorize(Authorization.USER_ADMIN_PDA);
				setSelectedTab("admin", request);
				setSelectedSub1Tab("useradmin", request);
				setSelectedSub2Tab("pdaadmin", request);
				
			    
				PDAAdminForm helpForm = (PDAAdminForm) form;
				helpForm.clean();
				
				Locale locale = getLocale(request);

				helpForm.setLocale(locale);
				helpForm.setTimeZone(getUserTimezone());
			    HashMap jsonData = new HashMap();
	   		    jsonData.put("carrierList", new JSONSerializer().serialize(getServiceLocator().getPDAAdminService().fetchPoolEntityList()));
	    		helpForm.setJson(jsonData);
	    		
				
				return result = mapping.findForward("success");
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointRFIDPopupForm;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ShippingPointRFIDPopupForm" path="/ShippingPointRFIDPopupEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="ShippingPointRFIDPopupTile.def"
 * @struts.action-forward name="failure" path="ShippingPointRFIDPopupTile.def"
 */
public class ShippingPointRFIDPopupEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointRFIDPopupEntryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT);
			
			ShippingPointRFIDPopupForm helpForm;
			String forwardName = "success";
			
			// Get the struts form.
			helpForm = (ShippingPointRFIDPopupForm) form;
			helpForm.clear();
			
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

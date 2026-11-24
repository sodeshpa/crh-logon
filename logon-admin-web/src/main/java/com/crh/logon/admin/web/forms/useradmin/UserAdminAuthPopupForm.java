/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.useradmin;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_UserAdminAuthPopupForm"
 */
public class UserAdminAuthPopupForm extends UserAdminForm {
    private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(UserAdminAuthPopupForm.class);

    private String inputAction="";

	/**
     * Default constructor.
     */
    public UserAdminAuthPopupForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "UserAdminAuthPopupForm");
		}
		// ]]
		try {
			; //PMD
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * Override struts method.
     * Perform form-specific validation of input parameters.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			ActionErrors errors = super.validate(mapping, request);
			
	        if (errors == null) {
	            errors = new ActionErrors();
	        }
	        return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

}

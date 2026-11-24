/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.SareaEntity;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_SalesAreaForm"
 */
public class SalesAreaForm extends ActionForm {

	private static final long serialVersionUID = -1524463535164325304L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SalesAreaForm.class);
	public static final String SESSION__SALES_AREA_FORM_NAME = "admin_SalesAreaForm";

	private String formAction;
	private List<SareaEntity> sareaBsEntityList;

	/**
	 * Default constructor.
	 */
	public SalesAreaForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SalesAreaForm");
		}
		// ]]
		try {
			init();
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
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			;
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
	 * Clears all the input fields on the web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the web form.
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			init();
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
	 * Note: this is not associated with or invoked by the reset button in the
	 * web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
				{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			;
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

			if (!errors.isEmpty()) {
				formAction = "";
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

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public List<SareaEntity> getSareaEntityList() {
		return sareaBsEntityList;
	}

	public void setSareaEntityList(List<SareaEntity> sareaBsEntityList) {
		this.sareaBsEntityList = sareaBsEntityList;
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.admin.web.actions.masterdata.ShippingPointSendSapSpPopupAction;
import com.crh.logon.admin.web.beans.ShippingPointSendSapSpBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.StringUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ShippingPointSendSapSpPopupForm"
 */
public class ShippingPointSendSapSpPopupForm extends ActionForm {

	private static final long serialVersionUID = 5786450621472189565L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointSendSapSpPopupForm.class);
	public static final String SESSION_SHIPPING_POINT_SEND_SAP_SP_POPUP_FORM_NAME = "admin_ShippingPointSendSapSpPopupForm";

	private String sendSapSpFormAction;
	
	private ShippingPointSendSapSpBean shippingPointSendSapSpBean;

	/**
	 * Default constructor.
	 */
	public ShippingPointSendSapSpPopupForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ShippingPointSendSapSpPopupForm");
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
			this.setShippingPointSendSapSpBean(new ShippingPointSendSapSpBean());
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

			if (ShippingPointSendSapSpPopupAction.FORWARD_SAVE.equals(sendSapSpFormAction)) {
				String plantCd = request.getParameter("shippingPointSendSapSpBean.plantCd");
				if (StringUtils.empty(plantCd)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.plant"));
				}
				String shippingpointId = request.getParameter("shippingPointSendSapSpBean.shippingpointId");
				if (StringUtils.empty(shippingpointId)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.shippingpoint"));
				}
			}

			if (!errors.isEmpty()) {
				sendSapSpFormAction = "";
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

	public String getSendSapSpFormAction() {
		return sendSapSpFormAction;
	}

	public void setSendSapSpFormAction(String sendSapSpFormAction) {
		this.sendSapSpFormAction = sendSapSpFormAction;
	}

	public void setShippingPointSendSapSpBean(ShippingPointSendSapSpBean shippingPointSendSapSpBean) {
		this.shippingPointSendSapSpBean = shippingPointSendSapSpBean;
	}

	public ShippingPointSendSapSpBean getShippingPointSendSapSpBean() {
		return shippingPointSendSapSpBean;
	}
}

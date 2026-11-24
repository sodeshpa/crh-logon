/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.admin.web.actions.masterdata.ShippingPointCreatePopupAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.ShippingPointCreateBean;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.StringUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ShippingPointCreatePopupForm"
 */
public class ShippingPointCreatePopupForm extends ActionForm {

	private static final long serialVersionUID = -5872285031646467469L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointCreatePopupForm.class);
	public static final String SESSION_SHIPPING_POINT_CREATE_POPUP_FORM_NAME = "admin_ShippingPointCreatePopupForm";

	private String createFormAction;
	
	private ShippingPointCreateBean shippingPointCreateBean;

	/**
	 * Default constructor.
	 */
	public ShippingPointCreatePopupForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ShippingPointCreatePopupForm");
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
			this.setShippingPointCreateBean(new ShippingPointCreateBean());
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

			if (ShippingPointCreatePopupAction.FORWARD_SAVE.equals(createFormAction)) {
				String sapShippingpointNo = request.getParameter("shippingPointCreateBean.sapShippingpointNo");
				if (StringUtils.empty(sapShippingpointNo)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.sapShippingpointNo"));
				}
				else if (sapShippingpointNo.length() > ShippingpointEntity.DBSIZE_SAP_SHIPPINGPOINT_NO) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldInputTooLong.sapShippingpointNo", new String[] { String.valueOf(ShippingpointEntity.DBSIZE_SAP_SHIPPINGPOINT_NO) }));
				}
				
				String shippingpointName = request.getParameter("shippingPointCreateBean.shippingpointName");
				if (StringUtils.empty(shippingpointName)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.shippingpointName"));
				}
				else if (shippingpointName.length() > ShippingpointEntity.DBSIZE_SHIPPINGPOINT_NAME) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldInputTooLong.shippingpointName", new String[] { String.valueOf(ShippingpointEntity.DBSIZE_SHIPPINGPOINT_NAME) }));
				}
			}

			if (!errors.isEmpty()) {
				createFormAction = "";
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

	public String getCreateFormAction() {
		return createFormAction;
	}

	public void setCreateFormAction(String createFormAction) {
		this.createFormAction = createFormAction;
	}

	public void setShippingPointCreateBean(ShippingPointCreateBean shippingPointCreateBean) {
		this.shippingPointCreateBean = shippingPointCreateBean;
	}

	public ShippingPointCreateBean getShippingPointCreateBean() {
		return shippingPointCreateBean;
	}
}

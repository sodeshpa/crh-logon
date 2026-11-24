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

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.ShippingPointSendSapSpBean;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointSendSapSpPopupForm;
import com.crh.logon.business.services.interfaces.ShippingPointService;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ShippingPointSendSapSpPopupForm" path="/ShippingPointSendSapSpPopup" scope="session"
 *  validate="true" input="ShippingPointSendSapSpPopupTile.def"
 *
 * @struts.action-forward name="shippingPointSendSapSpPopupEntry" path="/ShippingPointSendSapSpPopupEntry.do"
 * @struts.action-forward name="save" path="ShippingPointSendSapSpPopupTile.def"
 * @struts.action-forward name="close" path="ShippingPointSendSapSpPopupClose.def"
 * @struts.action-forward name="error" path="ShippingPointSendSapSpPopupTile.def"
 */
public class ShippingPointSendSapSpPopupAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointSendSapSpPopupAction.class);

	public static final String FORWARD_SUCCESS = "success";
	public static final String FORWARD_SAVE = "save";
	public static final String FORWARD_ERROR = "error";
	public static final String FORWARD_CLOSE = "close";

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
			ShippingPointSendSapSpPopupForm helpForm;
			String forwardName = FORWARD_CLOSE;
			helpForm = (ShippingPointSendSapSpPopupForm) form;

			if (FORWARD_SAVE.equals(helpForm.getSendSapSpFormAction())) {
				helpForm.setSendSapSpFormAction("");
				if (logDebugEnabled) {
					LOG.debug("Action: submit save button clicked!");
				}

				ActionErrors errors = MessageUtils.getActionErrors(request);
				if (errors == null || errors.isEmpty()) {
					ShippingPointSendSapSpBean shippingPointSendSapSpBean = helpForm.getShippingPointSendSapSpBean();
					ShippingPointService shippingPointService = getServiceLocator().getShippingPointService();

					String shippingpointIdStr = shippingPointSendSapSpBean.getShippingpointId();
					Long shippingpointId = -1L;
					if (!StringUtils.empty(shippingpointIdStr)) {
						try {
							shippingpointId = Long.valueOf(shippingpointIdStr);
						}
						catch (NumberFormatException e) {
							LOG.error("The given shippingpointId (" + shippingpointIdStr + ") is not a number.");
							throw new LogonException(e);
						}
					}
					ShippingpointEntity shippingPoint = shippingPointService.loadShippingPoint(shippingpointId);
					
					shippingPointSendSapSpBean.setShippingpointName(shippingPoint.getShippingpointName());
					shippingPointSendSapSpBean.setSapShippingpointNo(shippingPoint.getSapShippingpointNo());
				}
				else {
					return result = mapping.findForward(FORWARD_ERROR);
				}
			}

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

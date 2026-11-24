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

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.ShippingPointSendSapSpBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.masterdata.ShippingPointSendSapSpPopupForm;
import com.crh.logon.business.services.interfaces.ShippingPointService;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ShippingPointSendSapSpPopupForm" path="/ShippingPointSendSapSpPopupEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="ShippingPointSendSapSpPopupTile.def"
 * @struts.action-forward name="failure" path="ShippingPointSendSapSpPopupTile.def"
 */
public class ShippingPointSendSapSpPopupEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointSendSapSpPopupEntryAction.class);

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
			String forwardName = "success";
			helpForm = (ShippingPointSendSapSpPopupForm) form;
			helpForm.clear();

			String shippingpointIdParam = request.getParameter("shippingpointId");
			Long shippingpointId = -1L;
			if (!StringUtils.empty(shippingpointIdParam)) {
				try {
					shippingpointId = Long.valueOf(shippingpointIdParam);
				}
				catch (NumberFormatException e) {
					LOG.error("The given shippingpointId (" + shippingpointIdParam + ") is not a number.");
					throw new LogonException(e);
				}
			}
			
			ShippingPointSendSapSpBean shippingPointSendSapSpBean = new ShippingPointSendSapSpBean();

			if (shippingpointId != -1L) {
				ShippingPointService shippingPointService = getServiceLocator().getShippingPointService();
				ShippingpointEntity shippingPoint = shippingPointService.loadShippingPoint(shippingpointId);
			
				shippingPointSendSapSpBean.setShippingpointId(shippingPoint.getShippingpointId().toString());
				shippingPointSendSapSpBean.setPlantCd(shippingPoint.getPlant().getPlantCd());
			}
			else {
				shippingPointSendSapSpBean.setShippingpointId(null);
				shippingPointSendSapSpBean.setPlantCd(null);
			}
			helpForm.setShippingPointSendSapSpBean(shippingPointSendSapSpBean);

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

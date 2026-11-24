/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import com.crh.logon.order.web.forms.auftrag.OrderItemBean;
import com.crh.logon.order.web.forms.auftrag.ReturnReasonPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_auftrag_ReturnReasonPopupForm"
 *  path="/auftrag/ReturnReasonPopup" scope="session"
 *  validate="true"
 *  
 * @struts.action-forward name="success" path="ReturnReasonPopupClose.def"
 * @struts.action-forward name="error" path="ReturnReasonPopup.def"
 */
public class ReturnReasonPopupAction extends ReturnReasonPopupBase {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReturnReasonPopupAction.class);
	
	private static final String FORWARD_SUCCESS = "success";
	private static final String FORWARD_ERROR = "error";
	
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
			Authorization.authorize(Authorization.ORDER_DETAIL_R);
			
			
			ReturnReasonPopupForm helpForm = (ReturnReasonPopupForm) form;
			helpForm.validateReturnReason(request);
			if ((MessageUtils.getActionErrors(request) != null) && (MessageUtils.getActionErrors(request).size() > 0)) {
				return mapping.findForward(FORWARD_ERROR);
			}
			
			String forwardName = FORWARD_SUCCESS;
			
			OrderDetailForm ordDetForm = (OrderDetailForm)request.getSession().getAttribute(OrderDetailForm.SESSION_RFIDFORM_NAME);
			Long orderItemId = helpForm.getSelectedOrderItemId();
			
			OrderItemBean orderItemBean = getSelectedOrderItemBean(ordDetForm, orderItemId);
			if (orderItemBean == null) {
				String errorMsg = "Couldn't load the orderItem with id: '" + orderItemId + "', from OrderDetailForm.getMaterialBean().getOrderItemList()!";
				LOG.error(errorMsg);
				throw new ServletException(errorMsg);				
			}
			
			// prepare service
			OrderDetailService orderDetailService = getOrderDetailService();
			
			String returnReasonCd = helpForm.getSelectedReturnReasonCd();
			String plantCd = helpForm.getSelectedPlantCd();
			String shippingpointId = helpForm.getSelectedShippingpointId();;
			Map<Long, ShippingpointEntity> shippingpointMap = getShippingpointMap(plantCd, orderDetailService);
			
			orderItemBean.setReturnMaterialUsage(helpForm.getSelectedMaterialUsage());
			orderItemBean.setReturnPlant(helpForm.getSelectedPlant());
			orderItemBean.setReturnReason(returnReasonCd);
			orderItemBean.setReturnShippingpoint(shippingpointMap.get(Long.valueOf(shippingpointId)));
			
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

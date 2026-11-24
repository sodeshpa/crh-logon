/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_OrderDetailForm"
 *  path="/auftrag/OrderDetailEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="OrderDetailTile.def"
 * @struts.action-forward name="failure" path="OrderDetailTile.def"
 */
public class OrderDetailEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderDetailEntryAction.class);
	
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
			String forwardName = "success";
			
			OrderDetailForm helpForm = (OrderDetailForm) form;
			helpForm.clear();
			Locale locale = getLocale(request);
			helpForm.setLocale(locale);
			String orderIdParam = request.getParameter("orderid");
			
			if (orderIdParam != null) {
				Long orderId = new Long(orderIdParam);
				//get order from DB
				OrderEntity order = getOrderDetailService().loadOrder(orderId);
				if (order != null) {
					helpForm.setOrder(order);
					initOrderDetails(helpForm, order, request);
					initMaterialTab(helpForm);
				}
				else {
					LOG.error("Cannot find order for a given order id (" + orderIdParam + ")");
					// FIXME (valdas Apr 7, 2009):where to redirect?
					forwardName = "failure";
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.auftrag.LiveKarteForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_LiveKarteForm"
 *  path="/auftrag/LiveKarteEntry" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="success" path="LiveKarteTile.def"
 * @struts.action-forward name="failure" path="LiveKarteTile.def"
 */
public class LiveKarteEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LiveKarteEntryAction.class);
	
	public static Set possibleOrderStatusSet = new HashSet(); 
	static {
		possibleOrderStatusSet.add(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
		possibleOrderStatusSet.add(Constant.ORDER_STATUS_LOADING);
		possibleOrderStatusSet.add(Constant.ORDER_STATUS_LOADED);
		possibleOrderStatusSet.add(Constant.ORDER_STATUS_SHIPPING);
		possibleOrderStatusSet.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);
		possibleOrderStatusSet.add(Constant.ORDER_STATUS_UNLOADING);
	}
	
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
			Authorization.authorize(Authorization.LIVE_MAP);
			setSelectedTab("monitoring", request);
			setSelectedSub1Tab("monitoring", request);
			setSelectedSub2Tab("livekarte", request);
			
			LiveKarteForm helpForm = (LiveKarteForm) form;
			helpForm.clear();
			Locale locale = getLocale(request);
			
			List orderStatusList = new ArrayList();
			List orderStatusListTmp = getRefDataGenericsService().findTxEntityList(OrderStatusTxEntity.class, false, locale, "orderStatusTxPK.orderStatusBs.sortingNo", true);
			for (Iterator it = orderStatusListTmp.iterator(); it.hasNext();) {
				OrderStatusTxEntity status = (OrderStatusTxEntity) it.next();
				long statusCd = status.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd();
				
				if (possibleOrderStatusSet.contains(statusCd)){
					orderStatusList.add(status);
				}
			}
			
			helpForm.setOrderStatusList(orderStatusList);
			
			initLiveKarte(helpForm, locale, new ArrayList(possibleOrderStatusSet), null);
			
			String forwardName = "success";
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

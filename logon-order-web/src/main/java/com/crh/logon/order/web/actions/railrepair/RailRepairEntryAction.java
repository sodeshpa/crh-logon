/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.railrepair;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.WagonDetailService;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.railrepair.RailRepairForm;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_RailRepairForm" path="/railrepair/RailRepairEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="RailRepairTile.def"
 */
public class RailRepairEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailRepairEntryAction.class);
	private static final String FORWARD_SUCCESS = "success";
	
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
			Authorization.authorize(Authorization.BW_INSTANDHALTUNG);
			String forwardName = FORWARD_SUCCESS;
			
			setSelectedTab("railrepair", request);

			final WagonDetailService wagonDetailService = getServiceLocator().getWagonDetailService();
			final RailRepairForm iForm = (RailRepairForm)form;
			iForm.clear();
			iForm.setWagonKeeperList(wagonDetailService.getWagonKeeperList());
			iForm.setRepairShiptoList(wagonDetailService.fetchRepairShiptoList());
			iForm.setCombinedOrderStatusList(Arrays.asList(
					Constant.RAIL_REP_STATUS_OPEN, 
					Constant.RAIL_REP_STATUS_FINISHED, 
					Constant.RAIL_REP_STATUS_FINISHED_COMMERCIAL));
						
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

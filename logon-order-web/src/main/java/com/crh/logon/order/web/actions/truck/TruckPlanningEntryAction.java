/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.truck;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.actions.bestellung.ChartAction;
import com.crh.logon.order.web.forms.truck.ChartForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.BestellungChartBean;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_ChartForm" path="/bestellung/TruckPlanningEntry" scope="session"
 *  			  validate="false"
 *
 * @struts.action-forward name="success" path="TruckPlanningTile.def"
 */
public class TruckPlanningEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckPlanningEntryAction.class);

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
			Authorization.authorize(Authorization.ORDER_VIEW_4);
			
			setSelectedSub1Tab("truckplanning", request);
			
			ChartForm tpForm = (ChartForm)form;

		    Locale locale = request.getLocale();	    
		    
		    // init the chart form
		    ChartAction.initForm(tpForm, false, request, this, locale);
		    
		    // place the ChartBean object in Session (will be used at Chart Servlet)
		    request.getSession(true).setAttribute(BestellungChartBean.SESSION_KEY, tpForm.getChartBean());
		    
			return result = mapping.findForward("success");
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

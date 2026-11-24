/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.actions.demandplanning;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.web.utils.Authorization;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_demandplanning_DemandPlanningForm" path="/demandplanning/ReportExpectedEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/demandplanning/ReportExpected.def"
 */
@AdnTrace
public class ReportExpectedEntryAction extends DemandPlanningEntryAction {

	@Override
	public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Authorization.authorize(Authorization.DEMAND_PLANNING_REPORT_EXPECTED_DEMAND);

		super.execute(mapping, form, request, response);

		setSelectedSub1Tab("reportexpected", request);

		return mapping.findForward("success");
	}
}

/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.actions.demandplanning;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.order.web.forms.demandplanning.DemandPlanningForm;
import com.crh.logon.web.actions.ActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
@AdnTrace
public class DemandPlanningEntryAction extends ActionSupport {

	@Override
	public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		DemandPlanningForm demandPlanningForm = (DemandPlanningForm) form;

		setSelectedTab("demandplanning", request);
		demandPlanningForm.setUomTonsText(getServiceLocator().fetchDemandPlanningService().getUoMTonsText(getLocale(request)));

		// return is ignored
		return null;
	}
}

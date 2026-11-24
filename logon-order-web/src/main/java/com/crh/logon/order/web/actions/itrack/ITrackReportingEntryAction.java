/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.itrack;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.itrack.ITrackReportingForm;
import com.crh.logon.web.utils.Authorization;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_itrack_ITrackReportingForm"
 *  path="/itrack/ITrackReportingEntry" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="success" path="ITrackReportingTile.def"
 * @struts.action-forward name="failure" path="ITrackReportingTile.def"
 */
public class ITrackReportingEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ITrackReportingEntryAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {

			Authorization.authorize(Authorization.ITRACK_REPORTING_SERVICE_LEVEL_REPORT);
			setSelectedTab("itrackreporting", request);

			String forwardName = "success";
			return mapping.findForward(forwardName);
	}
}
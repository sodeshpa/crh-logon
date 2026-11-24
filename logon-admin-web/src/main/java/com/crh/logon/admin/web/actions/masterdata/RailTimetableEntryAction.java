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
import com.crh.logon.admin.web.forms.masterdata.RailTimetableForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RailTimetableForm" path="/RailTimetableEntry" scope="request"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="RailTimetableTile.def"
 */
public class RailTimetableEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableEntryAction.class);

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
			if(!Authorization.isAllowed(Authorization.MASTER_DATA_RAIL_TIMETABLE) &&
					!Authorization.isAllowed(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE)) {
				throw new LogonException("", Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
			}
			
			
			RailTimetableForm helpForm;
			String forwardName = "success";
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("rail", request);
			setSelectedSub2Tab("timetable", request);
			
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

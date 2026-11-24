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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.RailTimetableDetailForm;
import com.crh.logon.business.services.interfaces.RailTimetableService;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RailTimetableDetailForm" path="/RailTimetableDetailEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="RailTimetableDetailTile.def"
 * @struts.action-forward name="failure" path="RailTimetableDetailTile.def"
 */
@AdnTrace
public class RailTimetableDetailEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableDetailEntryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		ActionForward result = null;

		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);

		RailTimetableDetailForm helpForm;
		helpForm = (RailTimetableDetailForm) form;
		helpForm.clear();

		String railTimetableIdParam = request.getParameter("railTimetableDetails.railTimetableId");

		Long railTimetableId = convertRailTimetableIdToLong(railTimetableIdParam);
		if(railTimetableId != null) {
			RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
			RailTimetableEntity railTimetableDetails = railTimetableService.loadRailTimetable(railTimetableId);
			helpForm.setRailTimetableDetails(railTimetableDetails);
		} else {
			RailTimetableEntity railTimetableDetails = new RailTimetableEntity();
			railTimetableDetails.setArrRailwayStation(new RailwayStationEntity());
			railTimetableDetails.setDepRailwayStation(new RailwayStationEntity());
			helpForm.setRailTimetableDetails(railTimetableDetails);
		}

		String forwardName = "success";

		result = mapping.findForward(forwardName);
		return result;
	}

	private Long convertRailTimetableIdToLong(String railTimetableId) {
		if (!StringUtils.empty(railTimetableId)) {
			try {
				return Long.valueOf(railTimetableId);
			}
			catch (NumberFormatException e) {
				LOG.error("The given railTimetableId (" + railTimetableId + ") is not a number.", e);
				// continue
			}
		}

		return null;
	}
	
}

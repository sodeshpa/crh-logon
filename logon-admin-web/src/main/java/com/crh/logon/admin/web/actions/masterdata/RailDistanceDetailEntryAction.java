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
import com.crh.logon.admin.web.forms.masterdata.RailDistanceDetailForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.RailTimetableService;
import com.crh.logon.persistence.entities.common.RailDistanceEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RailDistanceDetailForm" path="/RailDistanceDetailEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="RailDistanceDetailTile.def"
 * @struts.action-forward name="failure" path="RailDistanceDetailTile.def"
 */
@AdnTrace
public class RailDistanceDetailEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDistanceDetailEntryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		ActionForward result = null;

		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);

		RailDistanceDetailForm helpForm;
		helpForm = (RailDistanceDetailForm) form;
		helpForm.clear();

		String railDistanceIdParam = request.getParameter("railDistanceDetails.railDistanceId");
		
		RailDistanceEntity railDistanceEntity = helpForm.getRailDistanceDetails();
		
		Long railDistanceId = convertRailDistanceIdToLong(railDistanceIdParam);
		String depStationId = request.getParameter("depStationId");
		String arrStationId = request.getParameter("arrStationId");
		
		boolean depStationNotEmpty = StringUtils.notEmpty(depStationId);
		boolean arrStationNotEmpty = StringUtils.notEmpty(arrStationId);
		
		if (railDistanceId == null && !depStationNotEmpty && !arrStationNotEmpty) {
			railDistanceEntity = new RailDistanceEntity();
			railDistanceEntity.setDepRailwayStation(new RailwayStationEntity());
			railDistanceEntity.setArrRailwayStation(new RailwayStationEntity());
		}
		else {
			if(railDistanceId == null) {
				railDistanceEntity = new RailDistanceEntity();
				initFormDistanceEntity(railDistanceEntity, depStationId, arrStationId);
			} else {
				RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
				railDistanceEntity = railTimetableService.loadRailDistance(railDistanceId);
			}
		}

		helpForm.setRailDistanceDetails(railDistanceEntity);

		String forwardName = "success";

		result = mapping.findForward(forwardName);
		return result;
	}

	private Long convertRailDistanceIdToLong(String railDistanceId) {
		if (!StringUtils.empty(railDistanceId)) {
			try {
				return Long.valueOf(railDistanceId);
			}
			catch (NumberFormatException e) {
				LOG.error("The given railDistanceId (" + railDistanceId + ") is not a number.", e);
				// continue
			}
		}

		return null;
	}
	
	public void initFormDistanceEntity(RailDistanceEntity formDistanceEntity,
			String depRailwayStationId, String arrRailwayStationId) {
		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		RailwayStationEntity depStation = railTimetableService.loadRailwayStation(Long.valueOf(depRailwayStationId));
		RailwayStationEntity arrStation = railTimetableService.loadRailwayStation(Long.valueOf(arrRailwayStationId));
		
		formDistanceEntity.setDepRailwayStation(depStation);
		formDistanceEntity.setArrRailwayStation(arrStation);
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.RailDistanceDetailForm;
import com.crh.logon.business.services.interfaces.RailTimetableService;
import com.crh.logon.persistence.entities.common.RailDistanceEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RailDistanceDetailForm" path="/RailDistanceDetail" scope="session"
 *  validate="true" input="RailDistanceDetailTile.def"
 *
 * @struts.action-forward name="railDistanceDetailEntry" path="/RailDistanceDetailEntry.do"
 * @struts.action-forward name="save" path="RailDistanceDetailTile.def"
 * @struts.action-forward name="close" path="RailDistanceDetailCloseTile.def"
 * @struts.action-forward name="error" path="RailDistanceDetailTile.def"
 */
@AdnTrace
public class RailDistanceDetailAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDistanceDetailAction.class);

	public static final String FORWARD_SUCCESS = "success";

	public static final String FORWARD_SAVE = "save";

	public static final String FORWARD_ERROR = "error";

	public static final String FORWARD_CLOSE = "close";

	public static final String FORWARD_DELETE = "delete";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		ActionForward result = null;

		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);
		RailDistanceDetailForm helpForm;
		String forwardName = FORWARD_CLOSE;
		helpForm = (RailDistanceDetailForm) form;

		if (FORWARD_SAVE.equals(helpForm.getFormAction())) {
			helpForm.setFormAction("");

			ActionErrors errors = MessageUtils.getActionErrors(request);
			if (errors == null || errors.isEmpty()) {
				RailDistanceEntity railDistanceEntity = helpForm.getRailDistanceDetails();
				RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
				String railDistanceIdParam = request.getParameter("railDistanceDetails.railDistanceId");

				if (!StringUtils.empty(railDistanceIdParam) && !railDistanceIdParam.equals("null")) {
					if(railDistanceEntity.getTariffDistanceM() > 0) {
						railTimetableService.updateRailDistance(railDistanceEntity);
						MessageUtils.addActionMessage(request,
								"raildistance.message.raildistanceupdated");
					} else {	
						MessageUtils.addActionError(request,
								"railtimetable.message.raildistanceupdateerror");
					}
				}
				else {
					String depRailwayStationIdString = request.getParameter("depStationId");
					String arrRailwayStationIdString = request.getParameter("arrStationId");
					Long depRailwayStationId = null;
					Long arrRailwayStationId = null;
					if(StringUtils.empty(depRailwayStationIdString) || StringUtils.empty(arrRailwayStationIdString)) {
						RailwayStationEntity arrStation = railDistanceEntity.getArrRailwayStation();
						RailwayStationEntity depStation = railDistanceEntity.getDepRailwayStation();
						if (arrStation != null) {
							arrRailwayStationId = arrStation.getRailwayStationId();
						}
						if (depStation != null) {
							depRailwayStationId = depStation.getRailwayStationId();
						}
					} else {
						depRailwayStationId = Long.valueOf(depRailwayStationIdString);
						arrRailwayStationId = Long.valueOf(arrRailwayStationIdString);
					}
					if(validateRailDistanceBean(railTimetableService, railDistanceEntity, request, depRailwayStationId, arrRailwayStationId)) {
						initFormDistanceEntity(railDistanceEntity, depRailwayStationId, arrRailwayStationId);
						railDistanceEntity.setActiveFlag(Boolean.TRUE);
						railTimetableService.createRailDistance(railDistanceEntity);
						MessageUtils.addActionMessage(request,
								"raildistance.message.newraildistancecreated");
					} else {
						MessageUtils.addActionError(request,
								"railtimetable.message.raildistanceupdateerror");
					}
				}

			}
			else {
				 result = mapping.findForward(FORWARD_ERROR);
				 return result;
			}
		}
		result = mapping.findForward(forwardName);
		return result;

	}
	
	public boolean checkIfDistanceDoesNotExists(RailTimetableService railTimetableService, Long depRailwayStationId, Long arrRailwayStationId) {
		List<RailDistanceEntity> distances = railTimetableService.fetchFromToDistanceList(Long.valueOf(depRailwayStationId), Long.valueOf(arrRailwayStationId));

		if (distances == null || distances.isEmpty()) {
			return true;
		}
		return false;
	}

	public void initFormDistanceEntity(RailDistanceEntity formDistanceEntity,
			Long depRailwayStationId, Long arrRailwayStationId) {
		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		RailwayStationEntity depStation = railTimetableService.loadRailwayStation(depRailwayStationId);
		RailwayStationEntity arrStation = railTimetableService.loadRailwayStation(arrRailwayStationId);
		
		formDistanceEntity.setDepRailwayStation(depStation);
		formDistanceEntity.setArrRailwayStation(arrStation);
	}
	
	public boolean validateRailDistanceBean(RailTimetableService railTimetableService, RailDistanceEntity railDistanceDetailBean, 
			HttpServletRequest request, Long depRailwayStationId, Long arrRailwayStationId) {
		boolean result = true;
		Long distance = railDistanceDetailBean.getTariffDistanceM();
		if (distance == null || distance == 0) {
			result = false;
			MessageUtils.addActionError(request,
					"railtimetable.message.raildistanceupdateerror");
		}
		boolean sameStations = depRailwayStationId.equals(arrRailwayStationId);
		boolean noDistance = checkIfDistanceDoesNotExists(railTimetableService, depRailwayStationId, arrRailwayStationId);
		if (sameStations) {
			result = false;
			MessageUtils.addActionError(request, "railtimetable.message.samestationerror");
		}
		if (!noDistance) {
			result = false;
			MessageUtils.addActionError(request, "raildistance.message.distancealreadyexists");
		}
		
		return result;
	}
	
	public boolean validateRailDistance(String distance) {
		if(distance == null || distance.equals("0") || distance.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

}

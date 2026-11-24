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
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.masterdata.RailTimetableDetailForm;
import com.crh.logon.business.services.interfaces.RailTimetableService;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;


/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RailTimetableDetailForm" path="/RailTimetableDetail" scope="session"
 *  validate="true" input="RailTimetableDetailTile.def"
 *
 * @struts.action-forward name="railTimetableDetailEntry" path="/RailTimetableDetailEntry.do"
 * @struts.action-forward name="save" path="RailTimetableDetailTile.def"
 * @struts.action-forward name="close" path="RailTimetableDetailCloseTile.def"
 * @struts.action-forward name="error" path="RailTimetableDetailTile.def"
 */
@AdnTrace
public class RailTimetableDetailAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableDetailAction.class);

	public static final String FORWARD_SUCCESS = "success";

	public static final String FORWARD_SAVE = "save";

	public static final String FORWARD_ERROR = "error";

	public static final String FORWARD_CLOSE = "close";

	public static final String FORWARD_DELETE = "delete";
	
	private static final String ARR_STATION_ID = "arrStationId";

	private static final String DEP_STATION_ID = "depStationId";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		ActionForward result = null;

		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);
		RailTimetableDetailForm helpForm;
		String forwardName = FORWARD_CLOSE;
		helpForm = (RailTimetableDetailForm) form;

		if (FORWARD_SAVE.equals(helpForm.getFormAction())) {
			helpForm.setFormAction("");

			ActionErrors errors = MessageUtils.getActionErrors(request);
			if (errors == null || errors.isEmpty()) {
				RailTimetableEntity railTimetableDetailBean = helpForm.getRailTimetableDetails();
				RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
				String railTimetableIdParam = request.getParameter("railTimetableDetails.railTimetableId");

				if (!StringUtils.empty(railTimetableIdParam)) {
					try {
						railTimetableService.updateRailTimetable(railTimetableDetailBean);
						MessageUtils.addActionMessage(request,
								"railtimetable.message.railtimetableupdated");
					} catch (Exception e) {
						LOG.error("Error updating timetable.",e);
						MessageUtils.addActionError(request,
								"railtimetable.message.railtimetableupdateerror");
					}
				}
				else {
					try {
						String depRailwayStationId = request.getParameter(DEP_STATION_ID);
						String arrRailwayStationId = request.getParameter(ARR_STATION_ID);
						
						if(depRailwayStationId.equals(arrRailwayStationId)) {
							MessageUtils.addActionError(request,
									"railtimetable.message.samestationerror");
						} else {
							initFormTimetableEntity(railTimetableDetailBean, depRailwayStationId, arrRailwayStationId);
							railTimetableDetailBean.setActiveFlag(Boolean.TRUE);
							railTimetableService.createRailTimetable(railTimetableDetailBean);
							MessageUtils.addActionMessage(request,
									"railtimetable.message.newrailtimetablecreated");
						}
					} catch (Exception e) {
						LOG.error("Error creating timetable.",e);
						MessageUtils.addActionError(request,
								"railtimetable.message.timetablecreateerror");
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

	public void initFormTimetableEntity(RailTimetableEntity formTimetableEntity,
			String depRailwayStationId, String arrRailwayStationId) {
		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		RailwayStationEntity depStation = railTimetableService.loadRailwayStation(Long.valueOf(depRailwayStationId));
		RailwayStationEntity arrStation = railTimetableService.loadRailwayStation(Long.valueOf(arrRailwayStationId));
		
		formTimetableEntity.setDepRailwayStation(depStation);
		formTimetableEntity.setArrRailwayStation(arrStation);
	}
}

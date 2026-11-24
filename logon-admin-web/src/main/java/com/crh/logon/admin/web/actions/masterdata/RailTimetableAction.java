/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.com.bytecode.opencsv.CSVWriter;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.RailTimetableForm;
import com.crh.logon.business.services.interfaces.RailTimetableService;
import com.crh.logon.persistence.entities.common.RailDistanceEntity;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date$
 * @struts.action name="admin_RailTimetableForm" path="/RailTimetable" scope="request" validate="true"
 * input="RailTimetableTile.def"
 * @struts.action-forward name="input" path="RailTimetableTile.def"
 */
public class RailTimetableAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableAction.class);

	public static final String FORWARD_DELETE_DISTANCE = "delete_distance";

	public static final String FORWARD_DELETE_TIMETABLE = "delete_timetable";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		// [[
		ActionForward result = null;
		Object[][] params = { { "mapping", mapping },
				{ "form", form },
				{ "request", request },
				{ "response", response } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {

			Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE);
			RailTimetableForm helpForm;
			String forwardName = "input";
			// Get the struts form.
			helpForm = (RailTimetableForm) form;

			String action = request.getParameter("action");

			// Check if cancel button clicked.
			if (isCancelled(request)) {
				helpForm.clear();
			}
			else if ("import".equals(action)) {
				actionImport(request, helpForm);
			}
			else if ("export".equals(action)) {
				actionExport(request, response, params);

				return null;
			}
			else if (FORWARD_DELETE_DISTANCE.equals(helpForm.getFormAction())) {
				formActionDeleteDistance(request);
			}
			else if (FORWARD_DELETE_TIMETABLE.equals(helpForm.getFormAction())) {
				formActionDeleteTimetable(request);
			}

			result = mapping.findForward(forwardName);
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void actionImport(HttpServletRequest request, RailTimetableForm helpForm) throws IOException {
		FormFile formFile = helpForm.getTimetableImportFile();
		int size = formFile.getFileSize();
		char[] buffer = new char[size];
		try (Reader r = new BufferedReader(new InputStreamReader(formFile.getInputStream()))) {
			int totalBytesRead = 0;
			int bytesRead;
			do {
				bytesRead = r.read(buffer, totalBytesRead, size - totalBytesRead);
				if (bytesRead > 0) {
					totalBytesRead += bytesRead;
				}
				LOG.debug("read " + totalBytesRead + "/" + size + " bytes");
			} while ((bytesRead >= 0) && (totalBytesRead < size));
		}
		if (buffer.length != 0) {
			try {
				int count = getServiceLocator().getRailTimetableImportService().importRailTimetable(buffer);
				MessageUtils.addActionMessage(request, "railtimetable.success", "" + count);
			}
			catch (EJBException e) {
				if (e.getCausedByException() instanceof LogonException) {
					LogonException lex = (LogonException) e.getCausedByException();
					if (lex.getErrorDesc() == null) {
						lex.setErrorDesc(lex.getMessage());
					}
					MessageUtils.setActionException(request, lex, null);
				}

				throw e;
			}
		}
		else {
			MessageUtils.addActionError(request, "railtimetable.importerror");
		}
	}

	private void actionExport(HttpServletRequest request, HttpServletResponse response, Object[][] params) throws IOException {
		setDLHeaders(request, response, null,
				"timetable_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
		CSVWriter csvWriter = new CSVWriter(response.getWriter(), ';');
		try {
			String[] line =
					("#Railway_Station_Country_From;Railway_Station_From;Railway_Station_From_Name;Train_no_departure;"
							+ "Stellz. Abfahrt;Abfahrt;Railway_Station_Country_To;Railway_Station_To;"
							+ "Railway_Station_To_Name;Train_no_arrival;Tage+;Ankunft;Stellz. Ankunft;Comment;Benutzer;"
							+ "Zeit;Active_in_LOGON_(optional)")
							.split(";");
			csvWriter.writeNext(line);
			List railTimetableList = getServiceLocator().getRailTimetableImportService().getRailTimetableList();
			for (Object o : railTimetableList) {
				RailTimetableEntity railTimetable = (RailTimetableEntity) o;
				List<String> values = new ArrayList<>();
				values.add("" + railTimetable.getDepRailwayStation().getUicCountry()
						.getUicCountryCd()); // #Railway_Station_Country_From
				values.add(StringUtils.stripStart(railTimetable.getDepRailwayStation().getRailwayStationNo(),
						"0")); // Railway_Station_From
				values.add(StringUtils.defaultString(railTimetable.getDepRailwayStation()
						.getRailwayStationName())); // Railway_Station_From_Name (this value is NOT the imported value)
				values.add(StringUtils.defaultString(railTimetable.getDepTrainNo())); // Train_no_departure
				values.add(formatMinutes(railTimetable.getDepSwitchingTimeMin())); // Stellz. Abfahrt
				values.add(formatMinutes(railTimetable.getDepTimeMin())); // Abfahrt
				values.add("" + railTimetable.getArrRailwayStation().getUicCountry()
						.getUicCountryCd()); // Railway_Station_Country_To
				values.add(StringUtils.stripStart(railTimetable.getArrRailwayStation().getRailwayStationNo(),
						"0")); // Railway_Station_To
				values.add(StringUtils.defaultString(railTimetable.getArrRailwayStation()
						.getRailwayStationName())); // Railway_Station_To_Name (this value is NOT the imported value)
				values.add(StringUtils.defaultString(railTimetable.getArrTrainNo())); // Train_no_arrival
				values.add("" + railTimetable.getArrOffsetDays()); // Tage+
				values.add(formatMinutes(railTimetable.getArrTimeMin())); // Ankunft
				values.add(formatMinutes(railTimetable.getArrSwitchingTimeMin())); // Stellz. Ankunft
				values.add(StringUtils.defaultString(railTimetable.getTimetableComment())); // Comment
				values.add(StringUtils.defaultString(null)); // Benutzer
				values.add(StringUtils.defaultString(null)); // Zeit
				values.add(railTimetable.getActiveFlag().toString());
				line = values.toArray(new String[0]);
				csvWriter.writeNext(line);
			}
			csvWriter.flush();
		}
		catch (Exception t) {
			ExceptionUtil.errException(LOG, t, params);
			String errorDesc = MessageUtils.getMessage(request, request.getLocale(), "fatalerror.unknownerror");
			String errorId = null;
			if (t instanceof LogonException) {
				errorId = ((LogonException) t).getId();
			}
			if (errorId == null) {
				errorId = com.crh.logon.utils.StringUtils.generateErrorId();
				LOG.error("The following errorId is generated for a non-LogonException: " + errorId);
			}
			String errMsg = MessageUtils.getMessage(request, request.getLocale(), "fatalerror.errormessage",
					new Object[] { errorDesc, errorId });
			response.getWriter().write(errMsg);
			csvWriter.flush();
		}
	}

	private void formActionDeleteDistance(HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);
		String railDistanceIdParam = request.getParameter("railDistanceId");
		Long railDistance = null;
		if (StringUtils.isNotEmpty(railDistanceIdParam)) {
			try {
				railDistance = Long.valueOf(railDistanceIdParam);
			}
			catch (NumberFormatException e) {
				throw new LogonException(e);
			}
		}
		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		RailDistanceEntity railDistanceEntity = railTimetableService.loadRailDistance(railDistance);
		if (railDistanceEntity != null) {
			railTimetableService.deleteRailDistance(railDistance);
			MessageUtils.addActionMessage(request, "timetable.message.distancedeleted");
		}
		else if (railDistance != null) {
			LOG.error("Cannot find RailDistance for the given railDistanceId (" + railDistance + ")");
		}
		else {
			LOG.error("Incoming param railDistanceId was null/empty");
		}
	}

	private void formActionDeleteTimetable(HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);
		String railTimetableIdParam = request.getParameter("railTimetableId");
		Long railTimetableId = null;
		if (StringUtils.isNotEmpty(railTimetableIdParam)) {
			try {
				railTimetableId = Long.valueOf(railTimetableIdParam);
			}
			catch (NumberFormatException e) {
				throw new LogonException(e);
			}
		}
		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		RailTimetableEntity railTimetableEntity = railTimetableService.loadRailTimetable(railTimetableId);
		if (railTimetableEntity != null) {
			railTimetableService.deleteRailTimetable(railTimetableId);
			MessageUtils.addActionMessage(request, "timetable.message.timetabledeleted");
		}
		else if (railTimetableId != null) {
			LOG.error("Cannot find RailTimetable for the given railTimetableId (" + railTimetableId + ")");
		}
		else {
			LOG.error("Incoming param railTimetableId was null/empty");
		}
	}

	private String formatMinutes(Long minutes) {

		if (minutes == null) {

			return null;
		}
		String hourPart = "" + minutes / 60;
		String minutePart = "" + minutes % 60;

		return StringUtils.leftPad(hourPart, 2, '0') + ":" + StringUtils.leftPad(minutePart, 2, '0');
	}
}

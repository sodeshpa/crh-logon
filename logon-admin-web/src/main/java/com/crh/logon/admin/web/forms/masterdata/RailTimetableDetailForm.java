/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_RailTimetableDetailForm"
 */
public class RailTimetableDetailForm extends ActionForm {

	private static final long serialVersionUID = -4172910147603503279L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableDetailForm.class);
	public static final String SESSION_RAIL_TIMETABLE_DETAIL_FORM_NAME = "admin_RailTimetableDetailForm";

	private String formAction;

	private RailTimetableEntity railTimetableDetails;
	
	private String depTimeMin;
	
	private String depSwitchingTimeMin;
	
	private String arrTimeMin;
	
	private String arrSwitchingTimeMin;

	/**
	 * Default constructor.
	 */
	public RailTimetableDetailForm() {
		init();
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the web form.
	 */
	private void init() {
		this.setRailTimetableDetails(new RailTimetableEntity());
	}

	/**
	 * Clears all the input fields on the web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the web form.
	 */
	public void clear() {
		init();
	}

	/**
	 * Override struts method.
	 * Note: this is not associated with or invoked by the reset button in the
	 * web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	}

	/**
	 * Override struts method.
	 * Perform form-specific validation of input parameters.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		Long offsetDays = railTimetableDetails.getArrOffsetDays();
		if (offsetDays == null || offsetDays < 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"railtimetable.message.invalidoffsetdays"));
		}
		
		RailwayStationEntity depStation = railTimetableDetails.getDepRailwayStation();
		RailwayStationEntity arrStation = railTimetableDetails.getArrRailwayStation();
		String depStationName = depStation.getRailwayStationName();
		String arrStationName = arrStation.getRailwayStationName();
		if (depStation == null || depStationName == null 
				|| arrStation == null || arrStationName == null ||
				StringUtils.empty(depStationName) || StringUtils.empty(arrStationName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"railtimetable.message.entervalidstations"));
		}
		
		return errors;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public RailTimetableEntity getRailTimetableDetails() {
		return railTimetableDetails;
	}

	public void setRailTimetableDetails(RailTimetableEntity railTimetableDetails) {
		this.railTimetableDetails = railTimetableDetails;
	}

	public String getDepTimeMin() {
		return depTimeMin;
	}

	public void setDepTimeMin(String depTimeMin) {
		this.depTimeMin = depTimeMin;
	}

	public String getDepSwitchingTimeMin() {
		return depSwitchingTimeMin;
	}

	public void setDepSwitchingTimeMin(String depSwitchingTimeMin) {
		this.depSwitchingTimeMin = depSwitchingTimeMin;
	}

	public String getArrTimeMin() {
		return arrTimeMin;
	}

	public void setArrTimeMin(String arrTimeMin) {
		this.arrTimeMin = arrTimeMin;
	}

	public String getArrSwitchingTimeMin() {
		return arrSwitchingTimeMin;
	}

	public void setArrSwitchingTimeMin(String arrSwitchingTimeMin) {
		this.arrSwitchingTimeMin = arrSwitchingTimeMin;
	}
	
}

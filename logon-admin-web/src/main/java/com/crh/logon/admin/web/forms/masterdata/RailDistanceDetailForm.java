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

import com.crh.logon.persistence.entities.common.RailDistanceEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.utils.StringUtils;


/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_RailDistanceDetailForm"
 */
public class RailDistanceDetailForm extends ActionForm {

	private static final long serialVersionUID = -4172910147603503279L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDistanceDetailForm.class);
	public static final String SESSION_RAIL_DISTANCE_DETAIL_FORM_NAME = "admin_RailDistanceDetailForm";

	private String formAction;

	private RailDistanceEntity railDistanceDetails;

	/**
	 * Default constructor.
	 */
	public RailDistanceDetailForm() {
		init();
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the web form.
	 */
	private void init() {
		this.setRailDistanceDetails(new RailDistanceEntity());
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
		
		Long distance = railDistanceDetails.getTariffDistanceM();
		if (distance == null || distance <= 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"railtimetable.message.raildistanceupdateerror"));
		}
		
		RailwayStationEntity depStation = railDistanceDetails.getDepRailwayStation();
		RailwayStationEntity arrStation = railDistanceDetails.getArrRailwayStation();
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

	public RailDistanceEntity getRailDistanceDetails() {
		return railDistanceDetails;
	}

	public void setRailDistanceDetails(RailDistanceEntity railDistanceDetails) {
		this.railDistanceDetails = railDistanceDetails;
	}
	
}

/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.forms.demandplanning;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Used by ManagePlanningEntryAction and ManagePlanningAction classes and their respective jsps.
 *
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date$
 * @struts.form name="order_demandplanning_DemandPlanningForm"
 */
public class DemandPlanningForm extends ActionForm {

	private String uomTonsText;

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		uomTonsText = null;
	}

	public String getUomTonsText() {
		return uomTonsText;
	}

	public void setUomTonsText(String uomTonsText) {
		this.uomTonsText = uomTonsText;
	}

}

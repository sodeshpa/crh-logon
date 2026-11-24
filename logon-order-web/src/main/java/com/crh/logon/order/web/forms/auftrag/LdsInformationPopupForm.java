/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import org.apache.struts.action.ActionForm;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;

/**
*
* @author      AdNovum Informatik AG
*
* @struts.form name="order_auftrag_LdsInformationPopupForm"
*/
public class LdsInformationPopupForm extends ActionForm {
	
	private static final long serialVersionUID = -4609415252918533185L;
	
	private OrdItemAssignmtLoadEntity orderItemAssignmentLoad;
	
	private String attorneyStartDateStr;
	
	public void init(OrdItemAssignmtLoadEntity orderItemAssignmentLoad) {
		this.orderItemAssignmentLoad = orderItemAssignmentLoad;
		if (orderItemAssignmentLoad != null && orderItemAssignmentLoad.getLdsAttorneyStart() != null) {
			TimeZone timeZone = TimeZone.getAppTimeZone();
			attorneyStartDateStr = timeZone.fmtAsDayMonthYear(orderItemAssignmentLoad.getLdsAttorneyStart());
		}
	}

	/**
	 * @return the orderItemAssignmentLoad
	 */
	public OrdItemAssignmtLoadEntity getOrderItemAssignmentLoad() {
		return orderItemAssignmentLoad;
	}

	/**
	 * @return the attorneyStartDateStr
	 */
	public String getAttorneyStartDateStr() {
		return attorneyStartDateStr;
	}

	/**
	 * @param attorneyStartDateStr the attorneyStartDateStr to set
	 */
	public void setAttorneyStartDateStr(String attorneyStartDateStr) {
		this.attorneyStartDateStr = attorneyStartDateStr;
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms;

import javax.servlet.http.HttpSession;

import com.crh.logon.web.forms.RFIDPopupForm;

/**
 *	Form of RFIDPopupTile, which contains dropdown with all available rfidTags.
 *	It is all about attaching rfIdTags to rfIdObjects. Supposed that an rfidFormbean is set to the session
 *  (with the containing object, and the rfidobject itself). The selected rfidTag is removed
 *	from that object back to the list in case we remove it on RFIDsTile. The div named DIV_NAME
 *	is going to be reloaded each case of add/remove.
 *	
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_RFIDPopupForm"
 */
public class OrderRFIDPopupForm extends RFIDPopupForm {
	private static final long serialVersionUID = -8910104051337644556L;
	public static final String SESSION_RFIDFORM_NAME = "order_RFIDPopupForm";

	public static void resetForm(HttpSession session) {
		RFIDPopupForm.resetForm(session);
		OrderRFIDPopupForm sessionForm = (OrderRFIDPopupForm) session.getAttribute(SESSION_RFIDFORM_NAME);
		if (sessionForm != null) {
			sessionForm.init();
		}
	}

	public static void saveForm(HttpSession session, OrderRFIDPopupForm orderRfidPopupForm) {
		session.setAttribute(SESSION_RFIDFORM_NAME, orderRfidPopupForm);
	}

}
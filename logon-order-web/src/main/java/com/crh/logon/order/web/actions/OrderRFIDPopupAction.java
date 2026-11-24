/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.OrderRFIDPopupForm;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.web.actions.RFIDPopupAction;
import com.crh.logon.web.utils.Authorization;

/**
 *	Calls RFIDPopupTile, which contains dropdown with all available rfidTags.
 *	It is all about attaching rfIdTags to rfIdObjects. Supposed that an rfIdObject is
 *	set to the session with the name RFIDPopupForm.SESSION_RFIDOBJECT_NAME. The selected rfidTag is removed
 *	from that object back to the list in case we remove it on RFIDsTile. The div named DIV_NAME
 *	is going to be reloaded each case of add/remove.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 * 
 *  @struts.action name="order_RFIDPopupForm"
 *  path="/OrderRFIDPopup" scope="session"
 *  validate="false" input="/OrderRFIDPopupTile.def"
 *  
 * @struts.action-forward name="RFIDPopup" path="/OrderRFIDPopupTile.def"
 * @struts.action-forward name="RFIDsTile" path="/OrderRFIDsTile.def"
 */
@AdnTrace
public class OrderRFIDPopupAction extends RFIDPopupAction {
	private static final ActionForward INVALID_ACTION_REQUEST = null;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		HttpSession session = request.getSession();
		AbstractRFIDBean rfidSelection = (AbstractRFIDBean) session.getAttribute(RFIDBean.SESSION_KEY);
		if (rfidSelection == Constant.NOT_SPECIFIED) {
			OrderRFIDPopupForm orderRfidPopupForm = (OrderRFIDPopupForm) form;
			OrderRFIDPopupForm.saveForm(session, orderRfidPopupForm);
			return INVALID_ACTION_REQUEST;
		}
		RfidobjectEntity selectedRfid = rfidSelection.getRfidObject();
		Object containingObject = rfidSelection.getContextObject();
		if (selectedRfid == Constant.NOT_SPECIFIED) {
			return INVALID_ACTION_REQUEST;
		}
		RfidobjectTypeBsEntity rfidObjectType = selectedRfid.getRfidobjectType();
		if (rfidObjectType == Constant.NOT_SPECIFIED) {
			return INVALID_ACTION_REQUEST;
		}

		Long rfidObjectTypeCode = rfidObjectType.getRfidobjectTypeCd();
		if (!rfidObjectTypeCode.equals(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO)) {
			return INVALID_ACTION_REQUEST;
		}

		if (containingObject != Constant.NOT_SPECIFIED
				&& !(containingObject instanceof ShiptoSiloEntity)) {
			throw new LogonException();
		}
		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
		return super.execute(mapping, form, request, response);
	}

}

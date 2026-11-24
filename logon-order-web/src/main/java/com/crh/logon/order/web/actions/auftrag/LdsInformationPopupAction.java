/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJBException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.forms.auftrag.LdsInformationPopupForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_auftrag_LdsInformationPopupForm"
 *  path="/auftrag/LdsInformationPopup" scope="session"
 *  validate="true"
 *  
 * @struts.action-forward name="success" path="LdsInformationPopupClose.def"
 * @struts.action-forward name="error" path="LdsInformationPopup.def"
 */
@AdnTrace
public class LdsInformationPopupAction extends ActionSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LdsInformationPopupAction.class);
	private static final String FORWARD_SUCCESS = "success";
	private static final String FORWARD_ERROR = "error";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			Authorization.authorize(Authorization.SHIPPING_R);
			LdsInformationPopupForm ldsInformationPopupForm = (LdsInformationPopupForm) form;
			String attorneyStartDateStr = ldsInformationPopupForm.getAttorneyStartDateStr();
			if (StringUtils.isNotBlank(attorneyStartDateStr)) {
				TimeZone timeZone = TimeZone.getAppTimeZone();
				try {
					Date attrneyStartDate = timeZone.parseDayMonthYear(attorneyStartDateStr);
					ldsInformationPopupForm.getOrderItemAssignmentLoad().setLdsAttorneyStart(attrneyStartDate);
				} catch (ParseException e) {
					LOG.error(String.format("Invalid: %s", attorneyStartDateStr), e);
					MessageUtils.addActionError(request, "orderdetail.popup.lds.information.error.wrong.date.format");
					return mapping.findForward(FORWARD_ERROR);
				}
			}
			getAssignmentService().updateLdsInformation(ldsInformationPopupForm.getOrderItemAssignmentLoad());
			MessageUtils.addActionMessage(request, "orderdetail.popup.lds.information.success.lds.information.update");
			return mapping.findForward(FORWARD_SUCCESS);
		}
		catch (EJBException ex) {
			String text = "orderdetail.popup.lds.information.error.update.failed";
			text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
			MessageUtils.addActionError(request, text);
			return mapping.findForward(FORWARD_ERROR);
		}
	}
}

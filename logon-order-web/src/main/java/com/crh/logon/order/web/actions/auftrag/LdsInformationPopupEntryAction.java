/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.forms.auftrag.LdsInformationPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_auftrag_LdsInformationPopupForm"
 *  path="/auftrag/LdsInformationPopupEntry" scope="session"
 *  validate="false"
 *  
 * @struts.action-forward name="success" path="LdsInformationPopup.def"
 */
@AdnTrace
public class LdsInformationPopupEntryAction extends AuftragBearbeitungAction {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LdsInformationPopupEntryAction.class);
	private static final String FORWARD_SUCCESS = "success";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		Authorization.authorize(Authorization.SHIPPING_R);
		HttpSession session = request.getSession();
		AuftragBearbeitungForm auftragBearbeitungForm = (AuftragBearbeitungForm) session.getAttribute("order_auftrag_AuftragBearbeitungForm");
		LdsInformationPopupForm ldsInfoForm = (LdsInformationPopupForm) form;
		Long loadId = Long.valueOf(request.getParameter("inputLoadId"));
		OrdItemAssignmtLoadEntity orderItemAssignmentLoad = getOrderWithLoadId(
				auftragBearbeitungForm, loadId);
		ldsInfoForm.init(orderItemAssignmentLoad);

		return mapping.findForward(FORWARD_SUCCESS);
	}
}

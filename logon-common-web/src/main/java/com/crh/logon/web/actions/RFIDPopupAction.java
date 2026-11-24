/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.crh.logon.constants.RfidTagOperation;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.rfidadministration.parameters.RFIDDataCache;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.forms.RFIDPopupForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

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
 */
public class RFIDPopupAction extends ActionSupport {
	private static final String OBJECT_LABEL_NOT_UNIQUE_REASON = Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason();
	private static final String VALIDATION_REASON = Reason.RES_STRUTS_VALIDATION.getReason();

	public RFIDServiceLocalInterface rfidService;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RFIDPopupAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			RFIDPopupForm helpForm = (RFIDPopupForm) form;
			String forwardName = "RFIDPopup";
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			rfidService = serviceLocator.getLocalRFIDService();

			HttpSession session = request.getSession();
			AbstractRFIDBean actualSiloSelection = (AbstractRFIDBean) session.getAttribute(RFIDBean.SESSION_KEY);
			if (actualSiloSelection == null) {
				return mapping.findForward(forwardName);
			}

			RfidobjectEntity selectedRfidObject = actualSiloSelection.getRfidObject();
			helpForm.setRfObject(selectedRfidObject);
			Long selectedRfidTagId = helpForm.getRfidTagId();
			RFIDDataCache rfidDataCache = RFIDPopupForm.getRFIDDataCache(session);
			if (request.getParameter("save") != null) {
				if (logDebugEnabled) {
					LOG.debug("Action: save button is pressed");
				}
				
				forwardName = "RFIDsTile";
				if (selectedRfidTagId == null || selectedRfidTagId.equals(Long.valueOf(0))) {
					return result = mapping.findForward(forwardName);
				}
				try {
					actualSiloSelection.generateAndValidateAndSetRFIDObjectLabel(rfidService);
				}
				catch (RuntimeException runtimeException) {
					handleRFIDLabelGenerationProblems(request, selectedRfidObject, runtimeException);
				}
				helpForm.setRfObject(selectedRfidObject);
				rfidService.changeRfidTagAssignment(
						selectedRfidTagId,
						RfidTagOperation.ADD_RFID_TAG,
 selectedRfidObject, rfidDataCache);
			}
			else if (request.getParameter("removeRfid") != null) {
				if (logDebugEnabled) {
					LOG.debug("Action: remove button is pressed");
				}
				forwardName = "RFIDsTile";
				rfidService.changeRfidTagAssignment(selectedRfidTagId,
						RfidTagOperation.REMOVE_RFID_TAG,
						selectedRfidObject, rfidDataCache);
			}
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Handles the Exception coming from 
	 * {@link RFIDBean.AbstractRFIDBean.#generateRFIDObjectLabel(RFIDServiceLocalInterface)}
	 * 
	 * @param request
	 * @param selectedRfidObject
	 * @param runtimeException
	 */
	private static void handleRFIDLabelGenerationProblems(HttpServletRequest request,
			RfidobjectEntity selectedRfidObject,
			RuntimeException runtimeException) {
		Throwable cause = runtimeException.getCause();
		if (!(runtimeException instanceof LogonException || cause instanceof LogonException)) {
			throw runtimeException;
		}
		Reason reason = null;
		LogonException logonException = null;
		if (runtimeException instanceof LogonException) {
			logonException = (LogonException) runtimeException;
			reason = logonException.getReason();
		}
		else if (cause instanceof LogonException) {
			logonException = (LogonException) cause;
			reason = logonException.getReason();
		}
		if (reason != null) {
			String reasonOfReason = reason.getReason();
			RfidobjectTypeBsEntity selectedRfidObjectType = selectedRfidObject.getRfidobjectType();
			Long selectedRfidObjectTypeCode = selectedRfidObjectType.getRfidobjectTypeCd();
			if (reasonOfReason.equals(OBJECT_LABEL_NOT_UNIQUE_REASON)) {
				if (logonException != null) {
					String messageOfLogonException = logonException.getMessage();
					MessageUtils.addActionWarning(request, messageOfLogonException);
				}
				MessageUtils.addActionWarning(request,
						RFIDBean.getErrorMessage(selectedRfidObjectTypeCode));
			}
			else {
				if (reasonOfReason.equals(VALIDATION_REASON)) {
					MessageUtils.addActionWarning(request,
							RFIDBean.getErrorMessage(selectedRfidObjectTypeCode));
				}
				else if (logonException != null) {
					throw logonException;
				}
			}
		}
	}
	
	/**
	 * Checks, if the given rfObj can be saved regarding the unique check of the object label. Sets
	 * the error message, and returns true if the validation failed. In this case the related entity cannot be saved, 
	 * since according to it's parameters the save is not successful. (Will violate unique constraint of
	 * tlgnc_rfidobject type_cd+object_label.
	 * @param request
	 * @param rfidSelection TODO
	 * @return
	 */
	public static boolean validateRFIDObject(HttpServletRequest request,
			AbstractRFIDBean rfidSelection, RFIDServiceLocalInterface rfidService) {
		boolean validationFailed = false;
		RfidobjectEntity selectedRfidObject = rfidSelection.getRfidObject();
		if (selectedRfidObject == null) {
			return validationFailed = false;
		}
		try{
			rfidSelection.generateAndValidateAndSetRFIDObjectLabel(rfidService);
		}
		catch (RuntimeException runtimeException) {
			handleRFIDLabelGenerationProblems(request, selectedRfidObject, runtimeException);
		}
		return validationFailed;
	}

}

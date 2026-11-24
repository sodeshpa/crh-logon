/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.pdaadmin;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.pdaadmin.PDAAdminForm;
import com.crh.logon.business.services.interfaces.PDAAdminService;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PdaDeviceEntity;
import com.crh.logon.persistence.entities.refdata.SimcardBsEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author AdNovum Informatik AG
 * @struts.action name="admin_PDAAdminForm" path="/pdaadmin/PDAAdmin" scope="session" validate="false"
 * input="/pdaadmin/PDAAdminTile.def"
 * @struts.action-forward name="success" path="/pdaadmin/PDAAdminTile.def"
 * @struts.action-forward name="search" path="/pdaadmin/PDAAdminSearchTile.def"
 * @struts.action-forward name="detail" path="/pdaadmin/PDAAdminTile.def"
 * @struts.action-forward name="reload" path="/pdaadmin/PDAAdminEntry.do"
 */
public class PDAAdminAction extends ActionSupport {

	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(PDAAdminAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
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

			String forwardName = "success";
			Authorization.authorize(Authorization.USER_ADMIN_PDA);
			setSelectedTab("admin", request);
			setSelectedSub1Tab("useradmin", request);
			setSelectedSub2Tab("pdaadmin", request);
			PDAAdminForm helpForm = (PDAAdminForm) form;
			PDAAdminService service = getServiceLocator().getPDAAdminService();
			PdaDeviceEntity pda;

			if ("new".equals(helpForm.getInputAction())) {
				helpForm.setPda(null);
			}
			else if ("modify".equals(helpForm.getInputAction())) {
				String pdaDeviceId = helpForm.getPdaDeviceId();
				long pdaDeviceIdLong = Long.parseLong(pdaDeviceId);
				pda = service.fetchPdaDeviceEntityById(pdaDeviceIdLong);
				helpForm.setPda(pda);
				mapPDAToForm(helpForm, pda);
				helpForm.setDeviceFunctionalityCheckState(PDAAdminForm.DEV_FUNC_CHECK_STATE_READY_TO_VERIFY);
			}
			else if ("reset".equals(helpForm.getInputAction())) {

				pda = helpForm.getPda();
				if (pda == null) {
					helpForm.initDetailFields();
				}
				else {
					mapPDAToForm(helpForm, pda);
					helpForm.setDeviceFunctionalityCheckState(PDAAdminForm.DEV_FUNC_CHECK_STATE_READY_TO_VERIFY);
				}

			}
			else if ("cancel".equals(helpForm.getInputAction())) {
				helpForm.initDetailFields();
				forwardName = "search";
			}
			else if ("save".equals(helpForm.getInputAction())) {
				storePDADevice(request, helpForm, service);
			}
			else if ("verify".equals(helpForm.getInputAction())) {
				pda = helpForm.getPda();

				if (getServiceLocator().getPDAAdminService().testPDAReady(pda.getPdaDeviceNo())) {
					helpForm.setDeviceFunctionalityCheckState(PDAAdminForm.DEV_FUNC_CHECK_STATE_VERIFIED_READY);
					MessageUtils.addActionMessage(request, "pdaadmin.info.deviceReady", pda.getPdaDeviceNo());
				}
				else {
					helpForm.setDeviceFunctionalityCheckState(PDAAdminForm.DEV_FUNC_CHECK_STATE_VERIFIED_NOT_READY);
					MessageUtils.addActionWarning(request, "pdaadmin.info.deviceNotReady", pda.getPdaDeviceNo());
				}
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

	private boolean storePDADevice(HttpServletRequest request, PDAAdminForm helpForm, PDAAdminService service) {
		// [[
		boolean result = false;
		Object[][] params = { { "request", request },
				{ "helpForm", helpForm },
				{ "service", service } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storePDADevice", params);
		}
		// ]]
		try {
			String carrierId = helpForm.getCarrierId();
			CarrierEntity carrier = null;
			if (!StringUtils.empty(carrierId)) {
				long carrierIdLong = Long.parseLong(carrierId);
				carrier = getServiceLocator().getTrailerAdminService().fetchCarrier(carrierIdLong);
			}

			String pdaDeviceNo = helpForm.getPdaDeviceNo();
			String typeDescription = helpForm.getTypeDescription();
			String inventoryNumber = helpForm.getInventoryNumber();
			String simcardCd = helpForm.getSimcardCd();
			Long simcardCdLong;

			try {
				simcardCdLong = Long.valueOf(simcardCd);
			}
			catch (NumberFormatException nfe) {
				MessageUtils.addActionWarning(request, "pdaadmin.errors.invalid.simcardCd");
				result = true;
				return result;
			}

			SimcardBsEntity simcard = getRefDataGenericsService().findBsEntity(SimcardBsEntity.class, simcardCdLong);

			if (simcard == null) {
				MessageUtils.addActionWarning(request, "pdaadmin.errors.invalid.simcardCd");
				result = true;
				return result;
			}

			String softwareVersion = helpForm.getSoftwareVersion();
			boolean activeFlag = helpForm.isActiveFlag();

			Date lastRegistrationDate;
			try {
				lastRegistrationDate = helpForm.getLastRegistrationDat();
			}
			catch (Exception e) {
				LOG.info("LastRegistrationDat parsing failed", e);
				MessageUtils.addActionWarning(request, "pdaadmin.errors.invalid.lastregistrationdate");
				result = true;
				return result;
			}

			UserEntity user = getUser();

			PdaDeviceEntity pda = helpForm.getPda();

			if (pda == null) {
				PdaDeviceEntity newPDA = new PdaDeviceEntity();
				newPDA.setActiveFlag(Boolean.TRUE);
				newPDA.setCarrier(carrier);
				newPDA.setLastUser(user);
				newPDA.setPdaDeviceNo(pdaDeviceNo);
				newPDA.setTypeDescription(typeDescription);
				newPDA.setInventoryNumber(inventoryNumber);
				newPDA.setSimcard(simcard);
				newPDA.setSoftwareVersion(softwareVersion);
				newPDA.setLastRegistrationDate(lastRegistrationDate);
				newPDA.setActiveFlag(activeFlag);
				newPDA = service.persistPDADevice(newPDA);
				helpForm.setPda(newPDA);
			}
			else {
				pda.setCarrier(carrier);
				pda.setLastUser(user);
				pda.setPdaDeviceNo(pdaDeviceNo);
				pda.setTypeDescription(typeDescription);
				pda.setInventoryNumber(inventoryNumber);
				pda.setSimcard(simcard);
				pda.setSoftwareVersion(softwareVersion);
				pda.setLastRegistrationDate(lastRegistrationDate);
				pda.setActiveFlag(activeFlag);
				pda = service.mergePDADevice(pda);
				helpForm.setPda(pda);
			}

			MessageUtils.addActionMessage(request, "pdaadmin.info.saved");
			helpForm.setDeviceFunctionalityCheckState(PDAAdminForm.DEV_FUNC_CHECK_STATE_READY_TO_VERIFY);

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

	/**
	 * Maps the trailerEntity object to the form's fields. trailerEntity fields --> form fields
	 */
	private void mapPDAToForm(PDAAdminForm helpForm, PdaDeviceEntity pda) {
		// [[
		Object[][] params = { { "helpForm", helpForm },
				{ "pda", pda } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mapPDAToForm", params);
		}
		// ]]
		try {
			helpForm.setPdaDeviceId(pda.getPdaDeviceId() == null ? "" : pda.getPdaDeviceId().toString());
			helpForm.setPdaDeviceNo(pda.getPdaDeviceNo());
			Long carrierId = pda.getCarrier() != null ? pda.getCarrier().getCarrierId() : null;
			String carrierIdString = carrierId == null ? "" : carrierId.toString();
			helpForm.setCarrierId(carrierIdString);
			helpForm.setTypeDescription(pda.getTypeDescription());
			helpForm.setInventoryNumber(pda.getInventoryNumber());
			SimcardBsEntity simcard = pda.getSimcard();
			if (simcard != null) {
				helpForm.setSimcardNumber(simcard.getSimcardNumber());
				helpForm.setPhoneNumber(simcard.getPhoneNumber());
				helpForm.setSimcardCd(simcard.getSimcardCd().toString());
			}
			helpForm.setSoftwareVersion(pda.getSoftwareVersion());
			helpForm.setActiveFlag(pda.getActiveFlag());

			helpForm.setLastRegistrationDat(pda.getLastRegistrationDate());
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

}

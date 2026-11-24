/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.siloadmin;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.forms.siloadmin.SiloAdminForm;
import com.crh.logon.business.services.interfaces.SiloAdminService;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.SiloTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SiloTypeTxEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date$
 * @struts.action name="admin_SiloAdminForm" path="/siloadmin/SiloAdmin" scope="session" validate="false"
 * input="/siloadmin/SiloAdmin.def"
 * @struts.action-forward name="success" path="/siloadmin/SiloAdmin.def"
 * @struts.action-forward name="shiptopopup" path="/siloadmin/SiloShiptoSearch.def"
 */
public class SiloAdminAction extends ActionSupport {

	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(SiloAdminAction.class);

	public static final String FORWARD_SAVE = "save";

	public static final String FORWARD_SAVE_NEW = "saveNew";

	public static final String FORWARD_RESET = "reset";

	public static final String FORWARD_CREATE_SILO = "create";

	public static final String FORWARD_LOAD_SILO = "load";

	public static final String FORWARD_SHIPTO_POPUP = "shiptopopup";

	public static final String FORWARD_CHANGE_SHIPTO = "changeshipto";

	public static final String FORWARD_REMOVE_SHIPTO = "removeshipto";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
			String forwardName = "success";

			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("silo", request);
			SiloAdminForm helpForm = (SiloAdminForm) form;

			SiloAdminService service = getServiceLocator().getSiloAdminService();
			ShiptoSiloEntity silo;
			String inputAction = helpForm.getInputAction();

			List list = getRefDataGenericsService().findTxEntityList(SiloTypeTxEntity.class, request.getLocale());
			helpForm.setSiloTypeList(list);

			switch (inputAction) {
				case FORWARD_LOAD_SILO: {
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
					long siloId = helpForm.getSiloId();
					silo = service.fetchSilo(siloId);
					helpForm.setSilo(silo);
					helpForm.resetSiloAndSession(request, silo);
					helpForm.mapObjectToForm(helpForm.getSilo(), service, request.getLocale());
					break;
				}
				case FORWARD_SAVE:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
					storeSilo(request, helpForm, service);
					break;
				case FORWARD_SAVE_NEW:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO_CREATE);
					storeSilo(request, helpForm, service);
					break;
				case FORWARD_RESET: {
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
					Long siloId = helpForm.getSiloId();
					if (siloId != null && siloId != 0) {
						silo = service.fetchSilo(siloId);
						helpForm.setSilo(silo);
					}
					helpForm.resetSiloAndSession(request, helpForm.getSilo());
					//going to reload the silo, no save happens...
					if (helpForm.getSilo().getShiptoSiloId() != null) {
						helpForm.mapObjectToForm(helpForm.getSilo(), service, request.getLocale());
					}
					break;
				}
				case FORWARD_CREATE_SILO:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO_CREATE);
					//create a brand new silo
					ShiptoSiloEntity newSilo = new ShiptoSiloEntity();
					newSilo.setPortableSiloFlag(true);
					helpForm.resetSiloAndSession(request, newSilo);
					helpForm.setSilo(newSilo);
					break;
				case FORWARD_SHIPTO_POPUP:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
					//call shipto search popup
					forwardName = FORWARD_SHIPTO_POPUP;
					break;
				case FORWARD_CHANGE_SHIPTO:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
					//call on click on shipto (on popup)
					Long id = helpForm.getInputShiptoId();
					ShiptoEntity shipto = service.fetchShipto(id);
					helpForm.setShipTo(shipto);
					break;
				case FORWARD_REMOVE_SHIPTO:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
					helpForm.setShipTo(null);
					helpForm.setInputShiptoId(null);
					break;
				default:
					LOG.debug("Action " + inputAction + " has no handler");
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

	private void storeSilo(HttpServletRequest request, SiloAdminForm helpForm,
			SiloAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
		//save
		boolean validationFailed = mapFormToObjectAndSave(helpForm, request, service);
		if (!validationFailed) {
			ShiptoSiloEntity newSilo = helpForm.getSilo();
			MessageUtils.addActionMessage(request, "siloadmin.info.saved");
			newSilo = service.fetchSilo(newSilo.getShiptoSiloId());
			helpForm.resetSiloAndSession(request, newSilo);
			helpForm.setSilo(newSilo);
			helpForm.mapObjectToForm(newSilo, service, request.getLocale());

			List siloList = service.fetchSilos();
			helpForm.setSiloList(siloList);
		}
	}

	/**
	 * Mapping of input fields into the siloEntity. form fields --> siloEntity fields
	 */
	private boolean mapFormToObjectAndSave(SiloAdminForm helpForm, HttpServletRequest request, SiloAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
		ShiptoSiloEntity entity;
		boolean validationFailed = false;

		ShiptoEntity shipto = helpForm.getShipTo();

		String comment = helpForm.getInputComment();

		Long cd = helpForm.getInputSiloType();

		String siloName = helpForm.getInputSiloName();
		if (StringUtils.empty(siloName)) {
			MessageUtils.addActionWarning(request, "siloadmin.errors.empty.siloname");
			validationFailed = true;
		}
		BigDecimal volume = null;
		try {
			if (StringUtils.notEmpty(helpForm.getInputMaxVolumeM3())) {
				volume = StringUtils.toBigDecimal(helpForm.getInputMaxVolumeM3());
			}
		}
		catch (ParseException e) {
			MessageUtils.addActionWarning(request, "siloadmin.errors.volumenm3");
			validationFailed = true;
		}

		BigDecimal volumeT = null;
		try {
			if (StringUtils.notEmpty(helpForm.getInputMaxVolumeT())) {
				volumeT = StringUtils.toBigDecimal(helpForm.getInputMaxVolumeT());
			}
		}
		catch (ParseException e) {
			MessageUtils.addActionWarning(request, "siloadmin.errors.volument");
			validationFailed = true;
		}

		Boolean status = helpForm.getInputStatus();
		Long unloadingTime = null;
		try {
			if (StringUtils.notEmpty(helpForm.getInputUnloadingTime())) {
				unloadingTime = Long.valueOf(helpForm.getInputUnloadingTime());
			}
		}
		catch (NumberFormatException e) {
			MessageUtils.addActionWarning(request, "siloadmin.errors.unloading");
			validationFailed = true;
		}
		ShiptoSiloEntity silo = helpForm.getSilo();
		setSiloData(comment, cd, siloName, volume, status, silo, service, volumeT, unloadingTime, shipto);

		if (!validationFailed) {
			try {

				if (silo.getShiptoSiloId() != null) {
					entity = service.updateSilo(silo);
				}
				else {
					entity = service.createSilo(silo);
					helpForm.setSilo(entity);
				}
				helpForm.setSilo(entity);
			}
			catch (LogonException e) {
				// TODO (emoke Jun 3, 2009):this is not clear how it is thrown... sometimes EJB exception sometimes simple
				//  Logonexception
				Reason reason = e.getReason();
				if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())) {
					MessageUtils.addActionWarning(request, e.getMessage());
					MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO));
				}
				else {
					throw e;
				}
			}
			catch (RuntimeException re) {
				if (re.getCause() instanceof LogonException) {
					LogonException le = (LogonException) re.getCause();
					Reason reason = le.getReason();
					validationFailed = true;
					if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())) {
						MessageUtils.addActionWarning(request, le.getMessage());
						MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO));
					}
					else {
						throw le;
					}
				}
				else {
					throw re;
				}
			}
		}
		return validationFailed;
	}


	/**
	 * Sets the ShiptoSiloEntity's fields to the parameters.
	 */
	private void setSiloData(String comment, Long typeCd, String siloName, BigDecimal volumeM3,
			Boolean status, ShiptoSiloEntity silo, SiloAdminService service, BigDecimal volumeT, Long unloadingTime,
			ShiptoEntity shipto) {
		// [[
		Object[][] params = { { "comment", comment },
				{ "typeCd", typeCd },
				{ "siloName", siloName },
				{ "volumeM3", volumeM3 },
				{ "status", status },
				{ "silo", silo },
				{ "service", service },
				{ "volumeT", volumeT },
				{ "unloadingTime", unloadingTime },
				{ "shipto", shipto } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSiloData", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
			silo.setSiloComment(comment);
			silo.setActiveFlag(status);

			SiloTypeBsEntity bs = getRefDataGenericsService().findBsEntity(SiloTypeBsEntity.class, typeCd);
			silo.setSiloType(bs);
			silo.setSiloName(siloName);
			silo.setMaxVolumeM3(volumeM3);
			silo.setMaxVolumeTons(volumeT);
			silo.setUnloadingTime(unloadingTime);
			silo.setSiloComment(comment);
			silo.setShipto(shipto);
			silo.setPortableSiloFlag(true);
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

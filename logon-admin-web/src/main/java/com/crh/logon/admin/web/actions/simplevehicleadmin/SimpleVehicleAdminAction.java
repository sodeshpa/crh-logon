/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.simplevehicleadmin;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm;
import com.crh.logon.business.services.interfaces.SimpleVehicleAdminService;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.refdata.CompanycodeBsEntity;
import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeTxEntity;
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
 * @struts.action name="admin_SimpleVehicleAdminForm" path="/simplevehicleadmin/SimpleVehicleAdmin" scope="session"
 * validate="false" input="/simplevehicleadmin/SimpleVehicleAdmin.def"
 * @struts.action-forward name="success" path="/simplevehicleadmin/SimpleVehicleAdmin.def"
 * @struts.action-forward name="update" path="/simplevehicleadmin/StammDatenTile.def"
 * @struts.action-forward name="changefields" path="/simplevehicleadmin/ChangeFields.def"
 * @struts.action-forward name="createSimpleVehiclePopup" path="/simplevehicleadmin/CreateSimpleVehiclePopup.def"
 */
public class SimpleVehicleAdminAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleVehicleAdminAction.class);

	public static final String FORWARD_ENTRY = "entry";

	public static final String FORWARD_SELECT_CARRIER = "selectCarrier";

	public static final String FORWARD_SAVE = "save";

	public static final String FORWARD_SAVE_NEW = "saveNew";

	public static final String FORWARD_UPDATE = "update";

	public static final String FORWARD_RESET = "reset";

	public static final String FORWARD_CREATE_SIMPLE_VEHICLE = "createSimpleVehiclePopup";

	public static final String FORWARD_ADJUST_FIELDS = "changefields";

	@Override
	public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
			String forwardName = "success";

			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("simplevehicle", request);
			SimpleVehicleAdminForm helpForm = (SimpleVehicleAdminForm) form;

			SimpleVehicleAdminService service = getServiceLocator().getSimpleVehicleAdminService();
			SimpleVehicleEntity simpleVehicle;
			String inputAction = request.getParameter("inputAction");
			if (inputAction == null || inputAction.equals(FORWARD_ENTRY) || (!inputAction.equals(FORWARD_SELECT_CARRIER)
					&& helpForm.getCarrier() == null)) {
				//clear was called most probably from another tab
				AdminRFIDPopupForm.resetForm(request.getSession());
				helpForm.clear();
				TrailerAdminService trailerservice = getServiceLocator().getTrailerAdminService();
				List carrierList = trailerservice.fetchCarriers();
				helpForm.setCarrierList(carrierList);
				MessageUtils.addActionWarning(request, "roadmasterdata.formreseted");
				result = mapping.findForward(FORWARD_UPDATE);
				return result;
			}
			long carrierId = helpForm.getCarrierId();

			switch (inputAction) {
				case FORWARD_SELECT_CARRIER:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
					CarrierEntity carrier = service.fetchCarrier(carrierId);
					helpForm.setCarrier(carrier);

					String carrierName = carrier.getDisplayName();
					helpForm.setCarrierLoginName(carrierName);

					Map simpleVehicleTypes = new TreeMap();
					List<SimpleVehicleTypeTxEntity> simpleVehicleTypeList =
							getRefDataGenericsService().findTxEntityList(SimpleVehicleTypeTxEntity.class, request.getLocale());
					for (SimpleVehicleTypeTxEntity object : simpleVehicleTypeList) {
						simpleVehicleTypes.put(
								object.getSimpleVehicleTypeTxPK().getSimpleVehicleTypeBs().getSimpleVehicleTypeCd(), object);
					}
					helpForm.setSimpleVehicleTypeMap(simpleVehicleTypes);

					List simpleVehicleList = service.fetchSimpleVehiclesForCarrier(carrierId);
					helpForm.setSimpleVehicleList(simpleVehicleList);

					if (simpleVehicleList != null && !simpleVehicleList.isEmpty()) {
						Long simpleVehicleId = ((SimpleVehicleEntity) simpleVehicleList.get(0)).getSimpleVehicleId();

						simpleVehicle = service.fetchSimpleVehicle(simpleVehicleId);
						reset(request, helpForm, simpleVehicle, service);
						mapObjectToForm(helpForm, helpForm.getSimpleVehicle());

					}
					else {
						helpForm.setSimpleVehicle(null);
					}

					forwardName = "success";
					result = mapping.findForward(forwardName);
					return result;
				case FORWARD_SAVE: {
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE);
					boolean validationFailed = storeSimpleVehicle(request, helpForm, carrierId, service);
					if (!validationFailed) {
						MessageUtils.addActionMessage(request, "simplevehicleadmin.info.saved");
					}
					forwardName = FORWARD_UPDATE;
					break;
				}
				case FORWARD_SAVE_NEW: {
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE);
					// store values since createSimpleVehicle resets the form
					String plate = helpForm.getInputVehicleRegNo();
					String tara = helpForm.getInputTaraMeasure();
					String inputVehicleName = helpForm.getInputVehicleName();
					String sapVehicleNo = helpForm.getInputSapVehicleNo();
					String sapVehicleTypeCd = helpForm.getInputSapVehicleTypeCd();
					String simpleVehicleTypeCd = helpForm.getInputSimpleVehicleTypeCd();
					String nrChambers = helpForm.getInputNrChambers();
					createSimpleVehicle(request, helpForm, service);
					helpForm.setInputVehicleRegNo(plate);
					helpForm.setInputTaraMeasure(tara);
					helpForm.setInputVehicleName(inputVehicleName);
					helpForm.setInputSapVehicleNo(sapVehicleNo);
					helpForm.setInputSapVehicleTypeCd(sapVehicleTypeCd);
					helpForm.setInputSimpleVehicleTypeCd(simpleVehicleTypeCd);
					helpForm.setInputNrChambers(nrChambers);

					boolean validationFailed = storeSimpleVehicle(request, helpForm, carrierId, service);
					if (!validationFailed) {
						request.setAttribute("reload", "reload");
						MessageUtils.addActionMessage(request, "simplevehicleadmin.info.savednew");
					}
					forwardName = FORWARD_CREATE_SIMPLE_VEHICLE;
					break;
				}
				case FORWARD_UPDATE:
				case FORWARD_RESET:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
					loadSimpleVehicle(request, helpForm, service);
					forwardName = FORWARD_UPDATE;
					break;
				case FORWARD_CREATE_SIMPLE_VEHICLE:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE);
					helpForm.setInputVehicleName(null);
					forwardName = FORWARD_CREATE_SIMPLE_VEHICLE;
					break;
				case FORWARD_ADJUST_FIELDS:
					Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
					//renders the ChangeFields.jsp and calls these changes
					forwardName = FORWARD_ADJUST_FIELDS;
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

	private void createSimpleVehicle(HttpServletRequest request, SimpleVehicleAdminForm helpForm,
			SimpleVehicleAdminService service) {
		SimpleVehicleEntity simpleVehiclek = new SimpleVehicleEntity();
		//create a brand new simpleVehicle
		reset(request, helpForm, simpleVehiclek, service);
		helpForm.resetSimpleVehicleData(simpleVehiclek);
	}

	private void loadSimpleVehicle(HttpServletRequest request, SimpleVehicleAdminForm helpForm,
			SimpleVehicleAdminService service) {
		SimpleVehicleEntity simpleVehicle;
		Long simpleVehicleId = helpForm.getSimpleVehicleId();

		simpleVehicle = service.fetchSimpleVehicle(simpleVehicleId);

		reset(request, helpForm, simpleVehicle, service);
		mapObjectToForm(helpForm, helpForm.getSimpleVehicle());
	}

	private boolean storeSimpleVehicle(HttpServletRequest request, SimpleVehicleAdminForm helpForm, long carrierId,
			SimpleVehicleAdminService service) {
		//save
		boolean validationFailed = mapFormToObjectAndSave(helpForm, request, service);
		if (!validationFailed) {
			SimpleVehicleEntity newSimpleVehicle = helpForm.getSimpleVehicle();
			newSimpleVehicle = service.fetchSimpleVehicle(newSimpleVehicle.getSimpleVehicleId());
			reset(request, helpForm, newSimpleVehicle, service);
			helpForm.setSimpleVehicle(newSimpleVehicle);
			mapObjectToForm(helpForm, newSimpleVehicle);
			List simpleVehicleList = service.fetchSimpleVehiclesForCarrier(carrierId);
			helpForm.setSimpleVehicleList(simpleVehicleList);
		}
		return validationFailed;
	}

	/**
	 * Resets the simpleVehicle and the session object RFID form, and SimpleVehicleAdminForm's fields.
	 */
	private void reset(HttpServletRequest request, SimpleVehicleAdminForm helpForm,
			SimpleVehicleEntity newSimpleVehicle, SimpleVehicleAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
		AdminRFIDPopupForm.resetForm(request.getSession());
		helpForm.resetSimpleVehicleData(newSimpleVehicle);

		if (newSimpleVehicle != null && newSimpleVehicle.getSimpleVehicleId() != null) {
			Long simpleVehicleId = newSimpleVehicle.getSimpleVehicleId();
			Double[] lastPosition = service.fetchLastPosition(simpleVehicleId);
			helpForm.setLastPosition(lastPosition);

			List loadsOfSimpleVehicle = service.getLoadsOfSimpleVehicle(newSimpleVehicle);
			final boolean isLoaded = loadsOfSimpleVehicle != null && !loadsOfSimpleVehicle.isEmpty();
			final boolean isAssigned = service.isActiveAssignmentPresent(loadsOfSimpleVehicle);
			helpForm.setSimpleVehicleLoaded(isLoaded);
			helpForm.setSimpleVehicleAssigned(isAssigned);

		}
		else {
			helpForm.setSimpleVehicleLoaded(false);
		}
	}

	/**
	 * Mapping of input fields into the simpleVehicleEntity. form fields --> simpleVehicleEntity fields
	 *
	 * @param helpForm
	 */
	private boolean mapFormToObjectAndSave(SimpleVehicleAdminForm helpForm, HttpServletRequest request,
			SimpleVehicleAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);

		String comment = helpForm.getInputComment();

		Double lat = null;
		try {
			if (StringUtils.notEmpty(helpForm.getInputHomeBaseLat())) {
				lat = Double.parseDouble(helpForm.getInputHomeBaseLat());
			}
		}
		catch (NumberFormatException e) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.lat");
		}
		Double lon = null;
		try {
			if (StringUtils.notEmpty(helpForm.getInputHomeBaseLon())) {
				lon = Double.parseDouble(helpForm.getInputHomeBaseLon());
			}
		}
		catch (NumberFormatException e) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.lon");
		}

		boolean weightNeg = false;
		BigDecimal maxMeasure = null;
		try {
			if (StringUtils.notEmpty(helpForm.getInputMaxMeasure())) {
				//in case not set, leave it null
				maxMeasure = new BigDecimal(helpForm.getInputMaxMeasure());
				checkFieldMaxValue(request, helpForm.getInputMaxMeasure(), SimpleVehicleEntity.DBSIZE_MAX_GROSS_WEIGHT_T,
						SimpleVehicleEntity.DBSIZE_PRECISION_MAX_GROSS_WEIGHT_T, "roadmasterdata.fields.maxmeasure");
				if (maxMeasure.compareTo(new BigDecimal(0)) < 0) {
					weightNeg = true;
				}
			}
		}
		catch (NumberFormatException e) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.maxmeasure");
		}

		String vehicleRegNo = helpForm.getInputVehicleRegNo();
		if (StringUtils.empty(vehicleRegNo)) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.empty.regno");
		}

		String sapNo = helpForm.getInputSapVehicleNo();
		if (StringUtils.empty(sapNo)) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.create.sapnumber");
		}

		BigDecimal taraMeasure = null;
		try {
			//cannot be left empty!
			taraMeasure = new BigDecimal(helpForm.getInputTaraMeasure());
			checkFieldMaxValue(request, helpForm.getInputTaraMeasure(), SimpleVehicleEntity.DBSIZE_TARE_WEIGHT_T,
					SimpleVehicleEntity.DBSIZE_PRECISION_TARE_WEIGHT_T, "roadmasterdata.fields.tarameasure");
			if (taraMeasure.compareTo(new BigDecimal(0)) < 0) {
				weightNeg = true;
			}
		}
		catch (NumberFormatException e) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.tarameasure");
		}

		if (maxMeasure != null && taraMeasure != null && maxMeasure.subtract(taraMeasure).compareTo(new BigDecimal(0)) <= 0) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.additionalweight");
		}

		SimpleVehicleEntity simpleVehicle = helpForm.getSimpleVehicle();
		String vehicleName = helpForm.getInputVehicleName();
		if (StringUtils.empty(vehicleName)) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.empty.simpleVehicleid");
		}
		else if (vehicleName.length() > SimpleVehicleEntity.DBSIZE_VEHICLE_NAME) {
			String fieldDesc = Message.lookup(request.getLocale(), "roadmasterdata.fields.simplevehicleid");
			MessageUtils.addActionWarning(request, "error.field.maxlength", fieldDesc,
					Long.toString(SimpleVehicleEntity.DBSIZE_VEHICLE_NAME));
		}

		if (!StringUtils.empty(vehicleRegNo) && !StringUtils.empty(vehicleName)) {
			boolean uniqueConstraintFails = getServiceLocator().getSimpleVehicleAdminService()
					.findSimpleVehicleWithUniqueParameters(simpleVehicle.getSimpleVehicleId(), vehicleName, vehicleRegNo);
			if (uniqueConstraintFails) {
				MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.notunique.simplevehicleid");
			}
		}
		BigDecimal volume = null;
		try {
			if (StringUtils.notEmpty(helpForm.getInputVolumeM3())) {
				volume = StringUtils.toBigDecimal((helpForm.getInputVolumeM3()));
			}
		}
		catch (ParseException e) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.volume");
		}

		String sapVehicleNo = helpForm.getInputSapVehicleNo();
		String sapVehicleTypeCd = helpForm.getInputSapVehicleTypeCd();

		long simpleVehiclePriority = helpForm.getSimpleVehiclePriority();

		/*
		 * Priority has inverted meaning for minimum and maximum, maximum prio is 1, minimum is 99.
		 * Setting it to "inactive".
		 */
		if ((Constant.TRUCK_PRIORITY_MAX <= simpleVehiclePriority && simpleVehiclePriority <= Constant.TRUCK_PRIORITY_MIN)
				|| simpleVehiclePriority == Constant.TRUCK_PRIORITY_TRUCK_INACTIVE) {
			simpleVehicle.setPriority(simpleVehiclePriority);
		}
		else {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.create.priority");
		}

		Boolean branding = helpForm.getInputBrandingBoolean();
		Boolean status = helpForm.getInputStatus();
		Boolean internal = helpForm.getInputInternal();

		// validate company code (conditionally mandatory, foreign key)
		String companycodeCd = helpForm.getInputCompanycodeCd();
		CompanycodeBsEntity companycode = null;
		if (StringUtils.empty(companycodeCd)) {
			if (Boolean.TRUE.equals(internal)) {
				MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.empty.companycode");
			}
		}
		else {
			companycode = getRefDataGenericsService().findBsEntity(CompanycodeBsEntity.class,
					companycodeCd);
			if (companycode == null) {
				MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.invalid.companycode");
			}
		}

		String simpleVehicleTypeCdStr = helpForm.getInputSimpleVehicleTypeCd();
		Long simpleVehicleTypeCd = null;
		if (StringUtils.notEmpty(simpleVehicleTypeCdStr)) {
			// value from a dropdown, NumberFormatException needs not be handled (anyone tampering with the request will get an
			// exception, this is fine)
			simpleVehicleTypeCd = Long.valueOf(simpleVehicleTypeCdStr);
		}
		else {
			LOG.debug("SimpleVehicleAdminForm.inputSimpleVehicleTypeCd is empty");
		}

		if (weightNeg) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.weightnegative");
		}

		if (Boolean.TRUE.equals(status) && (maxMeasure == null || taraMeasure == null)) {
			MessageUtils.addActionWarning(request, "simplevehicleadmin.errors.active");
		}

		String nrChambersStr = helpForm.getInputNrChambers();
		// value from a dropdown, NumberFormatException needs not be handled (anyone tampering with the request will get an
		// exception, this is fine)
		Long nrChambers = StringUtils.notEmpty(nrChambersStr) ? Long.valueOf(nrChambersStr) : null;

		String unitCount = helpForm.getInputUnitCount();

		setSimpleVehicleData(comment,
				lat, lon, maxMeasure, taraMeasure, volume, nrChambers, unitCount, branding, internal,
				companycode, vehicleRegNo, vehicleName, sapVehicleNo, sapVehicleTypeCd, simpleVehicleTypeCd,
				status, simpleVehicle);

		boolean noErrorWarningPresent = noErrorWarningPresent(request);
		if (noErrorWarningPresent) {
			SimpleVehicleEntity entity;
			if (simpleVehicle.getSimpleVehicleId() != null) {
				//the update simpleVehicle expects the WHOLE composition sent, but on the UI the container is only there if it
				// is directly attached to the simpleVehicle!
				if (LOG.isDebugEnabled()) {
					LOG.debug("Update new vehicle: " + simpleVehicle);
				}
				entity = service.updateSimpleVehicle(simpleVehicle, false);
			}
			else {
				simpleVehicle.setCarrier(helpForm.getCarrier());
				String inputSapVehicleNo = helpForm.getInputSapVehicleNo();

				if (inputSapVehicleNo.isEmpty()) {
					//Creation of new planning simpleVehicle
					if (simpleVehicle.getPriority() == null) {
						simpleVehicle.setPriority(Constant.TRUCK_PRIORITY_MIN);
					}
					simpleVehicle.setSapVehicleIsInternalFlag(simpleVehicle.getCarrier().getSapVendor().getVnIsInternal());
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("Create new vehicle: " + simpleVehicle);
				}
				entity = service.createSimpleVehicle(simpleVehicle);
			}

			helpForm.setSimpleVehicle(entity);
		}
		return !noErrorWarningPresent(request);
	}

	/**
	 * Maps the simpleVehicleEntity object to the form's fields. simpleVehicleEntity fields --> form fields
	 */
	private void mapObjectToForm(SimpleVehicleAdminForm helpForm, SimpleVehicleEntity simpleVehicle) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);

		mapObjectToPanel1(helpForm, simpleVehicle);
		mapObjectToPanel2(helpForm, simpleVehicle);
		mapObjectToPanel3(helpForm, simpleVehicle);
	}


	/**
	 * Maps the simpleVehicle's data to the screen's Stammdaten panel.
	 */
	private void mapObjectToPanel1(SimpleVehicleAdminForm helpForm, SimpleVehicleEntity simpleVehicle) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
		helpForm.setSimpleVehicleId(simpleVehicle.getSimpleVehicleId());
		helpForm.setInputBranding(StringUtils.toString(simpleVehicle.getBrandingFlag()));
		helpForm.setSimpleVehiclePriority(simpleVehicle.getPriority());
		helpForm.setInputStatus(simpleVehicle.getActiveFlag());
		helpForm.setInputSimpleVehicleTypeCd(simpleVehicle.getSimpleVehicleType() != null ?
				simpleVehicle.getSimpleVehicleType().getSimpleVehicleTypeCd().toString() : null);
		helpForm.setInputMaxMeasure(StringUtils.toString(simpleVehicle.getMaxGrossWeightT()));
		helpForm.setInputVehicleRegNo(simpleVehicle.getVehicleRegNo());
		helpForm.setInputTaraMeasure(StringUtils.toString(simpleVehicle.getTareWeightT()));
		helpForm.setInputVehicleName(simpleVehicle.getVehicleName());
		helpForm.setInputVolumeM3(StringUtils.toString(simpleVehicle.getVolumeM3()));
		helpForm.setInputUnitCount(simpleVehicle.getVehicleUnitCount());
		helpForm.setInputNrChambers(StringUtils.toString(simpleVehicle.getNrChambers()));
		helpForm.setInputSapVehicleNo(simpleVehicle.getSapVehicleNo());
		helpForm.setInputSapVehicleTypeCd(simpleVehicle.getSapVehicleTypeCd());
		helpForm.setInputInternal(simpleVehicle.getSapVehicleIsInternalFlag());
		if (simpleVehicle.getCompanycode() != null) {
			helpForm.setInputCompanycodeCd(simpleVehicle.getCompanycode().getCompanycodeCd());
		}
	}

	private void mapObjectToPanel2(SimpleVehicleAdminForm helpForm, SimpleVehicleEntity simpleVehicle) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
		helpForm.setInputComment(simpleVehicle.getVehicleComment());
	}

	private void mapObjectToPanel3(SimpleVehicleAdminForm helpForm, SimpleVehicleEntity simpleVehicle) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);
		helpForm.setInputHomeBaseLat(StringUtils.toString(simpleVehicle.getHomebaseCoordLat()));
		helpForm.setInputHomeBaseLon(StringUtils.toString(simpleVehicle.getHomebaseCoordLon()));
	}

	/**
	 * Sets the SimpleVehicleEntity's fields to the parameters.
	 */
	private void setSimpleVehicleData(String comment, Double lat, Double lon, BigDecimal maxMeasure, BigDecimal taraMeasure,
			BigDecimal volume, Long nrChambers, String unitCount, Boolean branding, Boolean internal,
			CompanycodeBsEntity companycode, String vehicleRegNo, String vehicleName, String sapVehicleNo,
			String sapVehicleTypeCd, Long simpleVehicleTypeCd, Boolean status, SimpleVehicleEntity simpleVehicle) {

		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);

		SimpleVehicleTypeBsEntity simpleVehicleType =
				getRefDataGenericsService().findBsEntity(SimpleVehicleTypeBsEntity.class, simpleVehicleTypeCd);
		simpleVehicle.setSimpleVehicleType(simpleVehicleType);

		simpleVehicle.setVehicleComment(comment);
		simpleVehicle.setHomebaseCoordLat(lat);
		simpleVehicle.setHomebaseCoordLon(lon);
		simpleVehicle.setBrandingFlag(branding);
		simpleVehicle.setSapVehicleIsInternalFlag(internal);
		simpleVehicle.setCompanycode(companycode);
		simpleVehicle.setActiveFlag(status);
		simpleVehicle.setSapVehicleNo(sapVehicleNo);
		simpleVehicle.setSapVehicleTypeCd(sapVehicleTypeCd);

		SimpleVehicleTypeBsEntity bs =
				getRefDataGenericsService().findBsEntity(SimpleVehicleTypeBsEntity.class, simpleVehicleTypeCd);
		simpleVehicle.setSimpleVehicleType(bs);

		simpleVehicle.setMaxGrossWeightT(maxMeasure);
		if (simpleVehicle.getVehicleRegNo() == null || !simpleVehicle.getVehicleRegNo().equals(vehicleRegNo)) {
			simpleVehicle.setVehicleRegNo(vehicleRegNo);
		}
		simpleVehicle.setVehicleName(vehicleName);
		simpleVehicle.setTareWeightT(taraMeasure);
		simpleVehicle.setVolumeM3(volume);
		simpleVehicle.setNrChambers(nrChambers);
		simpleVehicle.setVehicleUnitCount(unitCount);
	}
}

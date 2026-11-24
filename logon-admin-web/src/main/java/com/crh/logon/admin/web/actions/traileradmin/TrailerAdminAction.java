/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.traileradmin;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.traileradmin.TrailerAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.SapImportVehnPostprocessService;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleCharBsEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;


/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_TrailerAdminForm"
 *  path="/traileradmin/TrailerAdmin" scope="session"
 *  validate="false" input="/traileradmin/TrailerAdmin.def"
 *
 * @struts.action-forward name="success" path="/traileradmin/TrailerAdmin.def"
 * @struts.action-forward name="tryagain" path="/traileradmin/TrailerAdminCarrier.def"
 * @struts.action-forward name="updateChambers" path="/traileradmin/UpdateChambers.def" 
 * @struts.action-forward name="createTrailerPopup" path="/traileradmin/CreateTrailerPopup.def"  
 * @struts.action-forward name="entry" path="/traileradmin/TrailerAdminCarrier.def"
 */
public class TrailerAdminAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TrailerAdminAction.class);
    
    public static final String FORWARD_ENTRY = "entry";
    public static final String FORWARD_SELECT_CARRIER = "selectCarrier";
    public static final String FORWARD_SAVE = "save";
    public static final String FORWARD_SAVE_NEW = "saveNew";
    public static final String FORWARD_RESET = "reset";
    public static final String FORWARD_CREATE_TRAILER = "createTrailerPopup";
    public static final String FORWARD_LOAD_TRAILER = "load";
    public static final String FORWARD_UPDATE_CHAMBERS = "updateChambers";
    public static final String FORWARD_SHOW_ONLY_ACTIVE_TRAILERS = "showOnlyActiveTrailers";
    
	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
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
			String forwardName = "success";
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("trailer", request);
			TrailerAdminForm helpForm = (TrailerAdminForm) form;
			
			TrailerAdminService service = getServiceLocator().getTrailerAdminService();
			TrailerEntity trailer;
			String inputAction = helpForm.getInputAction();
			
			if ( !inputAction.equals(FORWARD_SELECT_CARRIER) && helpForm.getCarrier() == null ){
				//clear was called most probably from another tab
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
				AdminRFIDPopupForm.resetForm(request.getSession());
				helpForm.clear();
				RfidobjectTypeBsEntity bs = getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_TRAILER);
				helpForm.setTrailerRefDataBsEntity(bs);
				List carrierList = service.fetchCarriers();
				helpForm.setCarrierList(carrierList);
				MessageUtils.addActionWarning(request, "roadmasterdata.formreseted");
				return result = mapping.findForward(FORWARD_ENTRY);
			}
			long carrierId = helpForm.getCarrierId();
			
			if (inputAction.equals(FORWARD_SELECT_CARRIER)) {
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
				CarrierEntity carrier  = service.fetchCarrier(carrierId);
				helpForm.setCarrier(carrier);
				
				String carrierName = carrier.getDisplayName();
				helpForm.setCarrierLoginName(carrierName);
				
				List trailerTypeList = getRefDataGenericsService().findTxEntityList(TrailerTypeTxEntity.class, request.getLocale());
				helpForm.setTrailerTypeList(trailerTypeList);
				
				List trailerTransportTypes = getRefDataGenericsService().findTxEntityList(TrailerTrspCatTxEntity.class, request.getLocale());
				helpForm.setTrailerTransportTypeList(trailerTransportTypes);
				
				List trailerList = updateTrailerList(helpForm);
				if (trailerList != null && trailerList.size() >= 1) {
					Long trailerId = ((TrailerEntity)trailerList.get(0)).getTrailerId();
					trailer = service.fetchTrailer(trailerId);
					helpForm.setTrailer(trailer);
					
					resetTrailerAndSession(request, helpForm, trailer, service);				
					mapObjectToForm(helpForm, helpForm.getTrailer());
				}
				else{
					helpForm.setTrailer(null);
				}
				forwardName="success";
				return result = mapping.findForward(forwardName);
			}
			else if (inputAction.equals(FORWARD_SHOW_ONLY_ACTIVE_TRAILERS)) {
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
				updateTrailerList(helpForm);
				forwardName="success";
				return result = mapping.findForward(forwardName);
			}
			else if (inputAction.equals(FORWARD_LOAD_TRAILER)) {
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
				long trailerId = helpForm.getTrailerId();
				trailer = service.fetchTrailer(trailerId);
				helpForm.setTrailer(trailer);
				resetTrailerAndSession(request, helpForm, trailer, service);				
				mapObjectToForm(helpForm, helpForm.getTrailer());
			}						
			else if (inputAction.equals(FORWARD_SAVE)){
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE);
				storeTrailer(request, helpForm, carrierId, service);
			}
			else if (inputAction.equals(FORWARD_SAVE_NEW)){
				//The "add new trailer button" is only visible for the Authorized persons, therefore here is no need for additional authorization
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
				String plate = helpForm.getInputPlateNr();
				String maxMeasure = helpForm.getInputMaxMeasure();
				String tara = helpForm.getInputTaraMeasure();
				String inputTrailerName = helpForm.getInputTrailerName();
				String sapVehicleNo = helpForm.getInputSapVehicleNo();
				Long sapVehicleCharacteristic = helpForm.getCharacteristic();
				TrailerEntity newTrailer = new TrailerEntity();
				resetTrailerAndSession(request, helpForm, newTrailer, service);
				helpForm.setInputPlateNr(plate);
				helpForm.setInputMaxMeasure(maxMeasure);
				helpForm.setInputTaraMeasure(tara);
				helpForm.setInputTrailerName(inputTrailerName);
				helpForm.setInputSapVehicleNo(sapVehicleNo);
				helpForm.setCharacteristic(sapVehicleCharacteristic);
				
				boolean validationFailed = storeTrailer(request, helpForm, carrierId, service);
				if (validationFailed != true){
					request.setAttribute("reload", "reload");
				}
				forwardName = FORWARD_CREATE_TRAILER; 
			}			
			else if (inputAction.equals(FORWARD_RESET)){
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);				
				resetTrailerAndSession(request, helpForm, helpForm.getTrailer(), service);
				//going to reload the trailer, no save happens...
				if ( helpForm.getTrailer().getTrailerId() != null ) {					
					mapObjectToForm(helpForm, helpForm.getTrailer());
				}
			}
			else if (inputAction.equals(FORWARD_CREATE_TRAILER)){
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);				
				//create a brand new trailer
				TrailerEntity newTrailer = new TrailerEntity();
				resetTrailerAndSession(request, helpForm, newTrailer, service);
				helpForm.setTrailer(newTrailer);
				forwardName = FORWARD_CREATE_TRAILER;
			}
			else if (inputAction.equals(FORWARD_UPDATE_CHAMBERS)){
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);				
				return result = mapping.findForward(FORWARD_UPDATE_CHAMBERS);
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
	 * Updates the trailer list using carrier id and "show only active trailers" flag from {@link TrailerAdminForm}
	 * @param helpForm
	 * @param service
	 */
	private List<TrailerEntity> updateTrailerList(TrailerAdminForm helpForm) {
		TrailerAdminService service = getServiceLocator().getTrailerAdminService();
		List trailerList = service.fetchTrailersForCarrier(helpForm.getCarrierId(), helpForm.isShowOnlyActiveTrailers());
		helpForm.setTrailerList(trailerList);
		return trailerList;
	}

	/**
	 * @param request
	 * @param helpForm
	 * @param carrierId
	 * @param service
	 * @return boolean True, if validation failed
	 */
	private boolean storeTrailer(HttpServletRequest request, TrailerAdminForm helpForm,
			long carrierId, TrailerAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
		//save
		boolean validationFailed = mapFormToObjectAndSave(helpForm, request, service);
		if (validationFailed) {
			return true;
		}
		TrailerEntity newTrailer = helpForm.getTrailer();
		MessageUtils.addActionMessage(request, "traileradmin.info.saved");
		newTrailer = service.fetchTrailer(newTrailer.getTrailerId());
		resetTrailerAndSession(request, helpForm, newTrailer, service);
		helpForm.setTrailer(newTrailer);
		mapObjectToForm(helpForm, newTrailer);
		List historyList = service.loadTrailerHistory(newTrailer.getTrailerId());
		helpForm.setHistoryList(historyList);
		updateTrailerList(helpForm);
		return false;
	}

	/**
	 * @param request
	 * @param helpForm
	 * @param newTrailer
	 * @param service TODO
	 */
	private void resetTrailerAndSession(HttpServletRequest request, TrailerAdminForm helpForm,
			TrailerEntity newTrailer, TrailerAdminService service) {
		AdminRFIDPopupForm.resetForm(request.getSession());
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
		helpForm.resetTrailerData(newTrailer);
		if ( newTrailer != null && newTrailer.getTrailerId() != null ) {			
			Double [] lastPosition = service.fetchLastPosition(newTrailer.getTrailerId());		
			helpForm.setLastPosition(lastPosition);
			List historyList = service.loadTrailerHistory(newTrailer.getTrailerId());
			helpForm.setHistoryList(historyList);

			List containerList = getServiceLocator().getTruckAdminService().fetchAvailableContainers(null, newTrailer.getTrailerId());
			helpForm.setContainerList(containerList);
		}

		RfidobjectEntity rfObj = null; 
		if (newTrailer != null){
			rfObj = newTrailer.getRfidobject();
		}
		if (rfObj == null) {
			rfObj = new RfidobjectEntity();
			rfObj.setRfidobjectType(helpForm.getTrailerRefDataBsEntity());			
		}
		newTrailer.setRfidobject(rfObj);
		
		//try to init, in case the properties are not yet set, catch the exception, saving will prevented anyways at save
		AbstractRFIDBean bean = RFIDBean.TrailerRFIDBean(newTrailer);
		AdminRFIDPopupForm.initForm(request.getSession(),bean);

	}



	/**
	 * Mapping of input fields into the trailerEntity.
	 * form fields --> trailerEntity fields 
	 * @param helpForm
	 * @return boolean True, if validation failed.  
	 */
	private boolean mapFormToObjectAndSave(TrailerAdminForm helpForm, HttpServletRequest request,  TrailerAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
		TrailerEntity entity = null;
		String axis = helpForm.getInputAxisNumber();
		
		String comment = helpForm.getInputComment();
		
		Long cd = helpForm.getInputTrailerType();
		Long tkatCd = helpForm.getInputTrailerTransportType();
		String manufacturer = helpForm.getInputManufacturer();
		
		String plateNo = helpForm.getInputPlateNr();
		if (StringUtils.empty(plateNo)){
			MessageUtils.addActionWarning(request, "traileradmin.errors.empty.plateno");
		}
		
		
		boolean weightNeg = false;
		BigDecimal taraMeasure = null;
		try{
			//cannot be left empty!
			taraMeasure = new BigDecimal(helpForm.getInputTaraMeasure());
			checkFieldMaxValue(request, helpForm.getInputTaraMeasure(), TrailerEntity.DBSIZE_TARE_WEIGHT_T, TrailerEntity.DBSIZE_PRECISION_TARE_WEIGHT_T , "roadmasterdata.fields.tarameasure");
			if (taraMeasure.compareTo(new BigDecimal(0)) < 0) {
				weightNeg = true;
			}
		}
		catch (NumberFormatException e){
			MessageUtils.addActionWarning(request, "traileradmin.errors.tarameasure");
		}
		
		BigDecimal maxMeasure = null;
		try{
			//cannot be left empty!
			maxMeasure = new BigDecimal(helpForm.getInputMaxMeasure());
			checkFieldMaxValue(request, helpForm.getInputMaxMeasure(), TrailerEntity.DBSIZE_MAX_GROSS_WEIGHT_T, TrailerEntity.DBSIZE_PRECISION_MAX_GROSS_WEIGHT_T , "roadmasterdata.fields.maxmeasure");
			if (maxMeasure.compareTo(new BigDecimal(0)) < 0) {
				weightNeg = true;
			}
		}
		catch (NumberFormatException e){
			MessageUtils.addActionWarning(request, "traileradmin.errors.maxmeasure");
		}
		
		if (maxMeasure != null && taraMeasure != null && maxMeasure.subtract(taraMeasure).compareTo(new BigDecimal(0)) <= 0) {
			MessageUtils.addActionWarning(request, "traileradmin.errors.additionalweight");
		}
		
		TrailerEntity trailer = helpForm.getTrailer();
		
		String trailerName = helpForm.getInputTrailerName();
		if (StringUtils.empty(trailerName)){
			MessageUtils.addActionWarning(request, "traileradmin.errors.empty.trailerid");
		}
		else  if ( trailerName.length() > TrailerEntity.DBSIZE_TRAILER_NAME ) {
			String fieldDesc = Message.lookup(request.getLocale(), "roadmasterdata.fields.trailerid");
			MessageUtils.addActionWarning(request, "error.field.maxlength", fieldDesc, Long.toString(TrailerEntity.DBSIZE_TRAILER_NAME) );
		}
		

		if (!StringUtils.empty(plateNo) && !StringUtils.empty(trailerName)){
			boolean uniqueConstraintFails = getServiceLocator().getTrailerAdminService().findTailerWithUniqueParameters(trailer.getTrailerId(), trailerName, plateNo);
			if (uniqueConstraintFails) {
				MessageUtils.addActionWarning(request, "traileradmin.errors.notunique.trailerid");
			}			
		}

		BigDecimal volume = null;
		try{
			if (StringUtils.notEmpty(helpForm.getInputVolumeM3()) ){				
				volume = StringUtils.toBigDecimal(helpForm.getInputVolumeM3());
			}
		}
		catch (ParseException e){
			MessageUtils.addActionWarning(request, "traileradmin.errors.volumen");
		}
		
		//only has meaning if it is a silo
		
		Long nrChambers = 1l;
		BigDecimal maxWeightChamber1T = new BigDecimal(0);
		BigDecimal maxWeightChamber2T = new BigDecimal(0);
		
		if (StringUtils.notEmpty(helpForm.getInputNrChambers()) ){				
			nrChambers = Long.valueOf(helpForm.getInputNrChambers());
		}

		if (nrChambers.compareTo(new Long(1)) >= 0) {
			try{
				maxWeightChamber1T = new BigDecimal(helpForm.getInputMaxNetWeightChamber1T());
				checkFieldMaxValue(request, helpForm.getInputMaxNetWeightChamber1T(), TrailerEntity.DBSIZE_MAX_NET_WEIGHT_CHAMBER1_T, TrailerEntity.DBSIZE_PRECISION_MAX_NET_WEIGHT_CHAMBER1_T , "roadmasterdata.fields.chamber1T");
				if (maxWeightChamber1T.compareTo(new BigDecimal(0)) < 0) {
					weightNeg = true;
				}
			}
			catch (NumberFormatException e){
				MessageUtils.addActionWarning(request, "truckadmin.errors.chamber1t");
			}
		}
		
		if (nrChambers.compareTo(new Long(2)) == 0) {				
			try{
				maxWeightChamber2T = new BigDecimal(helpForm.getInputMaxNetWeightChamber2T());
				checkFieldMaxValue(request, helpForm.getInputMaxNetWeightChamber2T(), TrailerEntity.DBSIZE_MAX_NET_WEIGHT_CHAMBER2_T, TrailerEntity.DBSIZE_PRECISION_MAX_NET_WEIGHT_CHAMBER2_T , "roadmasterdata.fields.chamber2T");
				if (maxWeightChamber2T.compareTo(new BigDecimal(0)) < 0) {
					weightNeg = true;
				}
			}
			catch (NumberFormatException e){
				MessageUtils.addActionWarning(request, "truckadmin.errors.chamber2t");
			}
		}
		
		if (weightNeg) {
			MessageUtils.addActionWarning(request, "traileradmin.errors.weightnegative");
		}
		
		String sapVehicleNo = helpForm.getInputSapVehicleNo();
		Long sapVehicleCharacteristic = helpForm.getCharacteristic();
		
		if (sapVehicleCharacteristic == Constant.VEHICLE_CHARACTERISTIC_GENERIC) {
			if ("".equals(sapVehicleNo)) {
				MessageUtils.addActionWarning(request, "traileradmin.errors.create.sapnumber");
			}
		}
		
		Boolean dustFilter = helpForm.getInputDustfilter();
		Boolean branding = helpForm.getInputBranding();
		Boolean status = helpForm.getInputStatus();
		//Boolean pdaFlag = helpForm.getInputPdaFlag();
		
		//If there are warnings or errors until this point, return immediately
		if ( ! noErrorWarningPresent(request)) {
			return true;
		}
		ContainerEntity originalContainer = helpForm.getOriginalContainer();
		ContainerEntity newContainer = helpForm.getSelectedContainer();
		try{
			if (trailer.getTrailerId() != null ) {					
				if (StringUtils.notEmpty(sapVehicleNo)) {
					//Update of Generic trailer
					SapImportVehnPostprocessService sapImportVehnPostprocessService = getServiceLocator().getSapImportVehnPostprocessService();
					SapVehicleEntity sapVehicle = sapImportVehnPostprocessService.fetchSapVehicleBySapNo(sapVehicleNo);
					
					// Generic vehicles will always get the active status from the SAP vehicle
					status = sapVehicle.getSapVehicleActiveFlag();
					trailer.setSapVehicleIsInternalFlag(sapVehicle.getSapVehicleIsInternalFlag());
				}
				setTrailerData(axis, comment, cd, plateNo, taraMeasure, trailerName, volume, manufacturer, status,
						dustFilter, branding, trailer, service, tkatCd, maxWeightChamber1T, maxWeightChamber2T,
						nrChambers, maxMeasure, sapVehicleNo, sapVehicleCharacteristic);
				entity = service.updateTrailer(trailer, newContainer, originalContainer);
			}
			else {
				trailer.setCarrier(helpForm.getCarrier());
				
				if (StringUtils.empty(sapVehicleNo)) {
					//Creation of new planning trailer
					trailer.setSapVehicleIsInternalFlag(trailer.getCarrier().getSapVendor().getVnIsInternal());
				}
				else {
					//Creation of new generic trailer
					SapImportVehnPostprocessService sapImportVehnPostprocessService = getServiceLocator().getSapImportVehnPostprocessService();
					SapVehicleEntity sapVehicle = sapImportVehnPostprocessService.fetchSapVehicleBySapNo(sapVehicleNo);
					
					//fill default fields from sapVehicle
					trailer = sapImportVehnPostprocessService.fillDefaultTrailer(trailer, sapVehicle);
					
					// Generic vehicles will always get the active status from the SAP vehicle
					status = sapVehicle.getSapVehicleActiveFlag();
				}
				setTrailerData(axis, comment, cd, plateNo, taraMeasure, trailerName, volume, manufacturer, status,
						dustFilter, branding, trailer, service, tkatCd, maxWeightChamber1T, maxWeightChamber2T,
						nrChambers, maxMeasure, sapVehicleNo, sapVehicleCharacteristic);
				entity = service.createTrailer(trailer);
			}
			helpForm.setTrailer(entity);				
		}
		catch (LogonException lex) {
			addActionErrorForReason(lex, request);
		}
		catch (EJBException ejbException) {
			Exception cause = (Exception) ejbException.getCause();
			
			if (cause instanceof LogonException) {
				addActionErrorForReason((LogonException) cause, request);
			} else {
				throw ejbException;
			}
		}
		return !noErrorWarningPresent(request);
	}

	/**
	 * Get the reason from LitDict and add it to the action error
	 * @param le
	 * @param request
	 */
	private void addActionErrorForReason(LogonException le, HttpServletRequest request) {
		Reason reason = le.getReason();
		if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())){
			MessageUtils.addActionWarning(request, le.getMessage());
			MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_TRAILER));
		} else if(reason.getReason().equals(Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE.getReason())) {
			MessageUtils.addActionError(request, Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE.getReason());
		} else {
			throw le;
		}
	}

	/**
	 * Maps the trailerEntity object to the form's fields.
	 * trailerEntity fields --> form fields
	 * @param helpForm
	 */
	private void mapObjectToForm(TrailerAdminForm helpForm, TrailerEntity trailer) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);		
		helpForm.setTrailerId(trailer.getTrailerId());
		helpForm.setInputBranding(trailer.getBrandingFlag());
		helpForm.setInputStatus(trailer.getActiveFlag());
		helpForm.setInputInternalFlag(trailer.getSapVehicleIsInternalFlag());
		Long inputTrailerTransportType = trailer.getTrailerTrspCat() != null ? trailer.getTrailerTrspCat().getTrailerTrspCatCd() : null;
		helpForm.setInputTrailerTransportType(inputTrailerTransportType);
		helpForm.setInputTrailerType(trailer.getTrailerType() != null ? trailer.getTrailerType().getTrailerTypeCd() : null);
		helpForm.setInputAxisNumber(trailer.getAxisCount());
		helpForm.setInputComment(trailer.getTrailerComment());
		helpForm.setInputManufacturer(trailer.getManufacturer());
		helpForm.setInputPlateNr(trailer.getTrailerPlateNo());
		helpForm.setInputTaraMeasure(trailer.getTareWeightT() != null ? trailer.getTareWeightT().toString() : "");
		helpForm.setInputTrailerName(trailer.getTrailerName());
		helpForm.setInputVolumeM3((trailer.getVolumeM3()!= null) ? trailer.getVolumeM3().toString() : "");
		helpForm.setInputNrChambers(trailer.getNrChambers().toString());
		helpForm.setInputMaxNetWeightChamber1T(trailer.getMaxNetWeightChamber1T().toString());
		helpForm.setInputMaxNetWeightChamber2T(trailer.getMaxNetWeightChamber2T().toString());
		helpForm.setInputMaxMeasure(trailer.getMaxGrossWeightT() != null?  trailer.getMaxGrossWeightT().toString() : "");
		helpForm.setInputSapVehicleNo(trailer.getSapVehicleNo());
		helpForm.setCharacteristic(trailer.getSapVehicleChar().getSapVehicleCharCd());
		
		ContainerEntity container = getServiceLocator().getTrailerAdminService().fetchContainerForTrailer(trailer.getTrailerId(), true);
		if (container != null) {
			helpForm.setInputContainerSelect(container.getContainerId().toString());
		}
		helpForm.setOriginalContainer(container);		
	}
	
	/**
	 * Sets the TrailerEntity's fields to the parameters.
	 * @param maxWeightT TODO
	 */
	private void setTrailerData(String axis, String comment, Long typeCd, String plateNo,
			BigDecimal taraMeasure, String trailerName, BigDecimal volume, String manufacturer, Boolean status,
			Boolean dustFilter, Boolean branding, TrailerEntity trailer, TrailerAdminService service,
			Long transportCatCd, BigDecimal maxWeightChamber1T, BigDecimal maxWeightChamber2T,
			Long nrChambers, BigDecimal maxWeightT, String sapVehicleNo, Long sapVehicleCharacteristic) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);		
		trailer.setAxisCount(axis);
		trailer.setTrailerComment(comment);
		trailer.setManufacturer(manufacturer);
		trailer.setBrandingFlag(branding);
		trailer.setActiveFlag(status);
		trailer.setMaxGrossWeightT(maxWeightT);
		
		TrailerTypeBsEntity bs = getRefDataGenericsService().findBsEntity(TrailerTypeBsEntity.class, typeCd);
		trailer.setTrailerType(bs);
		
		SapVehicleCharBsEntity sapVehicleCharBsEntity = getRefDataGenericsService().findBsEntity(SapVehicleCharBsEntity.class, sapVehicleCharacteristic);
		
		if ( trailer.getTrailerPlateNo() == null || !trailer.getTrailerPlateNo().equals(plateNo)) {
			trailer.setTrailerPlateNo(plateNo);
		}
		trailer.setTrailerName(trailerName);
		trailer.setTareWeightT(taraMeasure);
		trailer.setVolumeM3(volume);
		TrailerTrspCatBsEntity trsp = getRefDataGenericsService().findBsEntity(TrailerTrspCatBsEntity.class, transportCatCd);
		trailer.setTrailerTrspCat(trsp);
		trailer.setNrChambers(nrChambers);
		trailer.setMaxNetWeightChamber1T(maxWeightChamber1T);
		trailer.setMaxNetWeightChamber2T(maxWeightChamber2T);
		trailer.setSapVehicleNo(sapVehicleNo);
		trailer.setSapVehicleChar(sapVehicleCharBsEntity);
	}		
}

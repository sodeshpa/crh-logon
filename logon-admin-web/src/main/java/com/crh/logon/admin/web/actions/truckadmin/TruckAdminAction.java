/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.truckadmin;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm;
import com.crh.logon.business.services.interfaces.SapImportVehnPostprocessService;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleCharBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.persistence.entities.views.DriverWithoutSessionEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_TruckAdminForm"
 *  path="/truckadmin/TruckAdmin" scope="session"
 *  validate="false" input="/truckadmin/TruckAdmin.def"
 *
 * @struts.action-forward name="success" path="/truckadmin/TruckAdmin.def"
 * @struts.action-forward name="update" path="/truckadmin/StammDatenTile.def"  
 * @struts.action-forward name="changefields" path="/truckadmin/ChangeFields.def"
 * @struts.action-forward name="createTruckPopup" path="/truckadmin/CreateTruckPopup.def"  
 * @struts.action-forward name="entryAction" path="/truckadmin/TruckAdminEntry.do"
 */
public class TruckAdminAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TruckAdminAction.class);
    
    public static final String FORWARD_ENTRY = "entry";
    public static final String FORWARD_SELECT_CARRIER = "selectCarrier";
    public static final String FORWARD_SAVE = "save";
    public static final String FORWARD_SAVE_NEW = "saveNew";
    public static final String FORWARD_UPDATE = "update";
    public static final String FORWARD_RESET = "reset";
    public static final String FORWARD_CREATE_TRUCK = "createTruckPopup";
    public static final String FORWARD_ADJUST_FIELDS = "changefields";
    public static final String FORWARD_ENTRY_ACTION = "entryAction";
    public static final String FORWARD_SHOW_ONLY_ACTIVE_TRUCKS = "showOnlyActiveTrucks";
    
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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);			
			String forwardName = "success";
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("truck", request);
			TruckAdminForm helpForm = (TruckAdminForm) form;
			
			TruckAdminService service = getServiceLocator().getTruckAdminService();
			TruckEntity truck;
			String inputAction = request.getParameter("inputAction");
			
			// Handle direct calls of this action by redirecting to entry action.
			if (inputAction == null) {
				return result = mapping.findForward(FORWARD_ENTRY_ACTION);
			}
			

			
			if ((inputAction == FORWARD_ENTRY) ||( !inputAction.equals(FORWARD_SELECT_CARRIER) && helpForm.getCarrier() == null )){
				//clear was called most probably from another tab
				AdminRFIDPopupForm.resetForm(request.getSession());
				helpForm.clear();
				TrailerAdminService  trailerservice = getServiceLocator().getTrailerAdminService();
				RfidobjectTypeBsEntity bs = getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_TRUCK);
				helpForm.setTruckRefDataBsEntity(bs);
				List carrierList = trailerservice.fetchCarriers();
				helpForm.setCarrierList(carrierList);
				MessageUtils.addActionWarning(request, "roadmasterdata.formreseted");
				return result = mapping.findForward(FORWARD_UPDATE);
			}
			long carrierId = helpForm.getCarrierId();
			
			if (inputAction.equals(FORWARD_SHOW_ONLY_ACTIVE_TRUCKS)) {
				updateTruckList(helpForm);
				forwardName="success";
				return result = mapping.findForward(forwardName);
			}
			else
			if (inputAction.equals(FORWARD_SELECT_CARRIER)) {
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
				CarrierEntity carrier  = service.fetchCarrier(carrierId);
				helpForm.setCarrier(carrier);
				
				String carrierName = carrier.getDisplayName();
				helpForm.setCarrierLoginName(carrierName);
				
				Map truckTypes = new TreeMap();
				List truckTypeList = getRefDataGenericsService().findTxEntityList(TruckTypeTxEntity.class, request.getLocale());
				Iterator it = truckTypeList.iterator();
				while (it.hasNext()) {
					TruckTypeTxEntity object = (TruckTypeTxEntity) it.next();
					truckTypes.put(object.getTruckTypeTxPK().getTruckTypeBs().getTruckTypeCd(), object);
				}
				helpForm.setTruckTypeMap(truckTypes);
				
				List truckTkategorieList = getRefDataGenericsService().findTxEntityList(TruckTransportCatTxEntity.class, request.getLocale());
				helpForm.setTruckTkategorieList(truckTkategorieList);
				
				List poolList = service.fetchPools(carrierId, request.getLocale());
				helpForm.setPoolList(poolList);
				helpForm.setFullPoolList(poolList);
				
				List<TruckEntity> truckList = updateTruckList(helpForm);
				
				if (truckList != null && truckList.size() >= 1) {					
					Long truckId = ((TruckEntity)truckList.get(0)).getTruckId();
					
					List trailerList = service.fetchAvailableTrailers(carrierId, truckId);
					helpForm.setTrailerList(trailerList);
					
					List containerList = service.fetchAvailableContainers(truckId, null);
					helpForm.setContainerList(containerList);
					
					truck = service.fetchTruck(truckId);
					reset(request, helpForm, truck, service);
                    loadDriverLists(truck, helpForm, service);
					mapObjectToForm(helpForm, helpForm.getTruck(), service, request.getLocale());				
					
				}
				else {
					helpForm.setTruck(null);
					helpForm.setTruckPriority(Constant.TRUCK_PRIORITY_MIN);
					helpForm.setInputTrailerSelect(null);
					helpForm.setSelectedDriver(null);
					helpForm.setInputTkategorie(Constant.TKATEGORIE_TRAILER);
					helpForm.setInputLkwType(defaultTruckTypeCd());
					helpForm.setInputPdaFlag(false);
					helpForm.setInputSmsFlag(false);
				}
			
				forwardName="success";
				return result = mapping.findForward(forwardName);
			}
			else if (inputAction.equals(FORWARD_SAVE)){
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE);
				boolean validationFailed = storeTruck(request, helpForm, carrierId, service);
				if ( !validationFailed ) {
					MessageUtils.addActionMessage(request, "truckadmin.info.saved");
				}
				forwardName = FORWARD_UPDATE;
			}
			else if (inputAction.equals(FORWARD_SAVE_NEW)){
				//The "add new truck button" is only visible for the Authorized persons, therefore here is no need for additional authorization
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
				String plate = helpForm.getInputPlateNr();
				String tara = helpForm.getInputTaraMeasure();
				String inputTruckName = helpForm.getInputTruckName();
				String sapVehicleNo = helpForm.getInputSapVehicleNo();
				Long sapVehicleCharacteristic = helpForm.getCharacteristic();
				createTruck(request, helpForm, carrierId, service);
				helpForm.setInputPlateNr(plate);
				helpForm.setInputTaraMeasure(tara);
				helpForm.setInputTruckName(inputTruckName);
				helpForm.setInputSapVehicleNo(sapVehicleNo);
				helpForm.setCharacteristic(sapVehicleCharacteristic);
				
				boolean validationFailed = storeTruck(request, helpForm, carrierId, service);
				if ( !validationFailed ) {
				    request.setAttribute("reload", "reload");			
					MessageUtils.addActionMessage(request, "truckadmin.info.savednew");
				}
				forwardName = FORWARD_CREATE_TRUCK; 
			}
            else if (inputAction.equals(FORWARD_UPDATE)) {
                Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
                loadTruck(request, helpForm, carrierId, service);
                forwardName = FORWARD_UPDATE;
            }
			else if (inputAction.equals(FORWARD_RESET)) {
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
				loadTruck(request, helpForm, carrierId, service);
				forwardName = FORWARD_UPDATE;
			}
			else if (inputAction.equals(FORWARD_CREATE_TRUCK)){
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
				helpForm.setInputTruckName(null);
				forwardName = FORWARD_CREATE_TRUCK;
			}		
			else if (inputAction.equals(FORWARD_ADJUST_FIELDS)) {
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
				
				changeShippingpointsForPlant(helpForm);
				
				//renders the ChangeFields.jsp and calls these changes
				forwardName=FORWARD_ADJUST_FIELDS;				
				return result = mapping.findForward(forwardName);
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
	 * Updates the Truck List based on Carrier Id and "Show only active trucks" flag from {@link TruckAdminForm}, returns the updated list.
	 * @param helpForm the {@link TruckAdminForm}
	 * @return the {@link List} of {@link TruckEntity}
	 */
	private List<TruckEntity> updateTruckList(TruckAdminForm helpForm) {
		TruckAdminService service = getServiceLocator().getTruckAdminService();
		List truckList = service.fetchTrucksForCarrier(helpForm.getCarrierId(), helpForm.isShowOnlyActiveTrucks());
		helpForm.setTruckList(truckList);
		return truckList;
	}
	
	/** Truck Home Plant - Changes the available shippingpoints according to the selected plant. */
	private void changeShippingpointsForPlant(TruckAdminForm helpForm) {
		if (!Constant.INVALID_OBJECT_ID_STR.equals(helpForm.getSelectedPlant())) {
			
			helpForm.resetShippingpiointFields();
			String plantCd = helpForm.getSelectedPlant();
	
			List<ShippingpointEntity> allShippingpoints = helpForm.getAllShippingpoints();
			List<ShippingpointEntity> shippingpoints = PlantSareaUtils.
					getShippingpointsForPlantCd(allShippingpoints, plantCd);
			helpForm.setShippingpoints(shippingpoints);
	
			if (shippingpoints.size() == 1) {
				Long shippingpointId = ((ShippingpointEntity) shippingpoints.get(0)).getShippingpointId();
				helpForm.setSelectedShippingpoint(shippingpointId);
			}
		}
		else {
			List<ShippingpointEntity> shippingpoints = new ArrayList<ShippingpointEntity>();
			helpForm.setShippingpoints(shippingpoints);
		}
	}

	private void loadDriverLists(TruckEntity truck, TruckAdminForm helpForm, TruckAdminService service) {
		List drivers = new ArrayList();
		List driverEntities = new ArrayList();
		DriverEntity currentDriver = null;
		if (truck != null) {
			drivers = service.fetchFreeDrivers(truck.getCarrier().getCarrierId());
			currentDriver = service.fetchDriver(truck.getTruckId());
			//add the driver itself to this list, since he has to be present in the dropdown too
			if (currentDriver != null) {
				boolean addDriver = true;
				for (Iterator it=drivers.iterator();it.hasNext();) {
					DriverWithoutSessionEntity d = (DriverWithoutSessionEntity)it.next();
					if (d.getDriverId().equals(currentDriver.getDriverId())) {
						// driver is already in the list
						addDriver = false;
						break;
					}
				}
				if (addDriver) {
					// TODO (emoke Jun 24, 2009):must be according to sort
					DriverWithoutSessionEntity driverSess = new DriverWithoutSessionEntity();
					driverSess.setDriverId(currentDriver.getDriverId());
					driverSess.setName(currentDriver.getUser().getPerson().getName() + ", " + currentDriver.getUser().getPerson().getFirstname());
					drivers.add(0, driverSess);
				}
			}
			List driverIds = new ArrayList();
			for (Iterator it = drivers.iterator(); it.hasNext();) {
                DriverWithoutSessionEntity driver = (DriverWithoutSessionEntity) it.next();
                driverIds.add(driver.getDriverId());
            }
			driverEntities = service.fetchDriverEntities(driverIds);
		}
        helpForm.setDriverLists(drivers, driverEntities);
        helpForm.setSelectedDriver(currentDriver);

	}

	/**
	 * @param request
	 * @param helpForm
	 * @param carrierId
	 * @param service
	 */
	private void createTruck(HttpServletRequest request, TruckAdminForm helpForm, long carrierId,
			TruckAdminService service) {
		TruckEntity truckk = new TruckEntity();
		//create a brand new truck
		reset(request, helpForm, truckk, service);
		helpForm.resetTruckData(truckk);
		helpForm.setInputTkategorie(Constant.TKATEGORIE_TRAILER);
		helpForm.setInputLkwType(defaultTruckTypeCd());
		helpForm.setSelectedDriver(null);
		helpForm.setInputTrailerSelect(null);
		helpForm.setInputContainerSelect(null);
	}

	/**
	 * Returns the default truck type to be used when creating new trucks.
	 * @return The default truck type code for new trucks
	 */
	Long defaultTruckTypeCd() 
	{
		// [[
		Long result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "defaultTruckTypeCd");
		}
		// ]]
		try {
			LOG.debug("Determinig default truck type for a new truck entity.");
			final List<TruckTypeBsEntity> truckTypes;
			truckTypes = (List<TruckTypeBsEntity>) getRefDataGenericsService().findActiveBsEntityList(TruckTypeBsEntity.class);
			
			if (truckTypes.isEmpty()) {
				throw new RuntimeException("It was attempted to create a new truck, " +
						"but no entries were found in the truck type reference data table.");
			}
			
			/* See LOGON-5655 (third comment). We cannot use a hardcoded value as default truck
			 * type code, so we just chose one that we know exists.
			 */ 
			LOG.debug("Getting first truck type entity from reference data list.");
			final TruckTypeBsEntity defaultType = truckTypes.get(0);
			
			return result = defaultType.getTruckTypeCd();
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
	 * @param request
	 * @param helpForm
	 * @param carrierId
	 * @param service
	 */
	private void loadTruck(HttpServletRequest request, TruckAdminForm helpForm, long carrierId,
			TruckAdminService service) {
		
		Long truckId = helpForm.getTruckId();
		
		TruckEntity truck = service.fetchTruck(truckId);

		reset(request, helpForm, truck, service);
        loadDriverLists(truck, helpForm, service);
		mapObjectToForm(helpForm, helpForm.getTruck(), service, request.getLocale());
		
	}

	/**
	 * @param request
	 * @param helpForm
	 * @param carrierId
	 * @param service
	 */
	private boolean storeTruck(HttpServletRequest request, TruckAdminForm helpForm, long carrierId,
			TruckAdminService service) {
		//save
		boolean validationFailed = mapFormToObjectAndSave(helpForm, request, service);
		if ( !validationFailed ) {
			TruckEntity newTruck = helpForm.getTruck();
			newTruck = service.fetchTruck(newTruck.getTruckId());
			reset(request, helpForm, newTruck, service);
			helpForm.setTruck(newTruck);
			mapObjectToForm(helpForm, newTruck, service, request.getLocale());
			updateTruckList(helpForm);
		}
		return validationFailed;
	}

	/**
	 * Resets the truck and the session object RFID form, and TruckAdminForm's fields.
	 * @param request
	 * @param helpForm
	 * @param newTruck
	 * @param service TODO
	 */
	private void reset(HttpServletRequest request, TruckAdminForm helpForm,
			TruckEntity newTruck, TruckAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		AdminRFIDPopupForm.resetForm(request.getSession());
		helpForm.resetTruckData(newTruck);
		
		Long carrierId = helpForm.getCarrierId();
		if ( newTruck != null && newTruck.getTruckId() != null ) {			
			Long truckId = newTruck.getTruckId();
			Double [] lastPosition = service.fetchLastPosition(truckId);		
			helpForm.setLastPosition(lastPosition);
			
			List loadsOfTruck = service.getLoadsOfTruck(newTruck);
			final boolean isLoaded = loadsOfTruck != null && !loadsOfTruck.isEmpty();
			final boolean isAssigned = service.isActiveAssignmentPresent(loadsOfTruck);
			helpForm.setTruckLoaded(isLoaded);
			helpForm.setTruckAssigned(isAssigned);
			
			List trailerList = service.fetchAvailableTrailers(carrierId, truckId);
			helpForm.setTrailerList(trailerList);

			List containerList = service.fetchAvailableContainers(truckId, null);
			helpForm.setContainerList(containerList);
		}
		else{
			helpForm.setTruckLoaded(false);
			List trailerList = service.fetchAvailableTrailers(carrierId, null);
			helpForm.setTrailerList(trailerList);
			
			List containerList = service.fetchAvailableContainers(null, null);
			helpForm.setContainerList(containerList);
		}

		RfidobjectEntity rfObj = null; 
		if (newTruck != null && newTruck.getTruckId() != null){
			rfObj = newTruck.getRfidobject();
		}
		if (rfObj == null) {
			rfObj = new RfidobjectEntity();
			rfObj.setRfidobjectType(helpForm.getTruckRefDataBsEntity());
		}
		newTruck.setRfidobject(rfObj);
		AbstractRFIDBean bean = RFIDBean.TruckRFIDBean(newTruck);
		AdminRFIDPopupForm.initForm(request.getSession(),bean);
	}


	/**
	 * Mapping of input fields into the truckEntity.
	 * form fields --> truckEntity fields 
	 * @param helpForm
	 */
	private boolean mapFormToObjectAndSave(TruckAdminForm helpForm, HttpServletRequest request,  TruckAdminService service) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		String axis = helpForm.getInputAxisNumber();
		String comment = helpForm.getInputComment();
		
		String euroNorm = helpForm.getInputEuroNorm();
		
		Long transportTypeCatCd = helpForm.getInputTkategorie();
		Long containerId = helpForm.getInputContainerSelectLong();
		
		long[] inputPoolIds = helpForm.getInputPoolIds();

		Double lat = null;
		try{					
			if (StringUtils.notEmpty(helpForm.getInputHomeBaseLat()) ) {				
				lat = Double.parseDouble(helpForm.getInputHomeBaseLat());
			}
		}
		catch (NumberFormatException e){
			MessageUtils.addActionWarning(request, "truckadmin.errors.lat");
		}
		Double lon = null;
		try{		
			if (StringUtils.notEmpty(helpForm.getInputHomeBaseLon() )) {				
				lon = Double.parseDouble(helpForm.getInputHomeBaseLon());
			}
		}
		catch (NumberFormatException e){
			MessageUtils.addActionWarning(request, "truckadmin.errors.lon");
		}				
		
		Long cd = helpForm.getInputLkwType();
		
		String manufacturer = helpForm.getInputManufacturer();

		boolean weightNeg = false;
		BigDecimal maxMeasure = null;
		try{
			if (StringUtils.notEmpty(helpForm.getInputMaxMeasure()) ){
				//in case not set, leave it null
				maxMeasure = new BigDecimal(helpForm.getInputMaxMeasure());
				checkFieldMaxValue(request, helpForm.getInputMaxMeasure(), TruckEntity.DBSIZE_MAX_GROSS_WEIGHT_T, TruckEntity.DBSIZE_PRECISION_MAX_GROSS_WEIGHT_T , "roadmasterdata.fields.maxmeasure");
				if (maxMeasure.compareTo(new BigDecimal(0)) < 0) {
					weightNeg = true;
				}
			}
		}
		catch (NumberFormatException e){
			MessageUtils.addActionWarning(request, "truckadmin.errors.maxmeasure");
		}
		
		String plateNo = helpForm.getInputPlateNr();
		if (StringUtils.empty(plateNo)){
			MessageUtils.addActionWarning(request, "truckadmin.errors.empty.plateno");
		}
		
		BigDecimal taraMeasure = null;
		try{
			//cannot be left empty!
			taraMeasure = new BigDecimal(helpForm.getInputTaraMeasure());
			checkFieldMaxValue(request, helpForm.getInputTaraMeasure(), TruckEntity.DBSIZE_TARE_WEIGHT_T, TruckEntity.DBSIZE_PRECISION_TARE_WEIGHT_T, "roadmasterdata.fields.tarameasure");
			if (taraMeasure.compareTo(new BigDecimal(0)) < 0) {
				weightNeg = true;
			}
		}
		catch (NumberFormatException e){
			MessageUtils.addActionWarning(request, "truckadmin.errors.tarameasure");
		}
		
		if (maxMeasure != null && taraMeasure != null && maxMeasure.subtract(taraMeasure).compareTo(new BigDecimal(0)) <= 0) {
			MessageUtils.addActionWarning(request, "truckadmin.errors.additionalweight");
		}
		
		TruckEntity truck = helpForm.getTruck();
		String truckName = helpForm.getInputTruckName();
		if (StringUtils.empty(truckName)){
			MessageUtils.addActionWarning(request, "truckadmin.errors.empty.truckid");
		}
		else  if ( truckName.length() > TruckEntity.DBSIZE_TRUCK_NAME ) {
			String fieldDesc = Message.lookup(request.getLocale(), "roadmasterdata.fields.truckid");
			MessageUtils.addActionWarning(request, "error.field.maxlength", fieldDesc, Long.toString(TruckEntity.DBSIZE_TRUCK_NAME) );
		}


		if (!StringUtils.empty(plateNo) && !StringUtils.empty(truckName)){			
			boolean uniqueConstraintFails = getServiceLocator().getTruckAdminService().findTruckWithUniqueParameters(truck.getTruckId(),truckName, plateNo);
			if (uniqueConstraintFails) {
				MessageUtils.addActionWarning(request, "truckadmin.errors.notunique.truckid");
			}
		}
		BigDecimal volume = null;
		try {					
			if (StringUtils.notEmpty(helpForm.getInputVolumeM3())) {				
				volume = StringUtils.toBigDecimal((helpForm.getInputVolumeM3()));
			}
		}
		catch (ParseException e){
			MessageUtils.addActionWarning(request, "truckadmin.errors.volumen");
		}
		
		String sapVehicleNo = helpForm.getInputSapVehicleNo();
		Long sapVehicleCharacteristic = helpForm.getCharacteristic();
		
		if (sapVehicleCharacteristic == Constant.VEHICLE_CHARACTERISTIC_GENERIC) {
			if (sapVehicleNo == "") {
				MessageUtils.addActionWarning(request, "truckadmin.errors.create.sapnumber");
			}
		}
		
		Long truckPriority = helpForm.getTruckPriority();
		
		/* 
		 * Priority has inverted meaning for minimum and maximum, maximum prio is 1, minimum is 99. 
		 * Setting it to "inactive".
		 */
		if ((Constant.TRUCK_PRIORITY_MAX <= truckPriority && truckPriority <= Constant.TRUCK_PRIORITY_MIN)
				|| truckPriority == Constant.TRUCK_PRIORITY_TRUCK_INACTIVE) {
			truck.setPriority(truckPriority);
		}
		else {
			MessageUtils.addActionWarning(request, "truckadmin.errors.create.priority");
		}
		
		Boolean dustFilter = helpForm.getInputDustfilterBoolean();
		Boolean branding = helpForm.getInputBrandingBoolean();
		Boolean status = helpForm.getInputStatus();
		Boolean pdaFlag = helpForm.getInputPdaFlag();
		Boolean smsSendFlag = helpForm.getInputSmsFlag();
		Boolean iTrackAvailable = helpForm.getiTrackAvailableFlag();
		Long nrChambers = 0l;
		BigDecimal maxWeightChamber1T = new BigDecimal(0);
		BigDecimal maxWeightChamber2T = new BigDecimal(0);
		
		if (transportTypeCatCd.equals(Constant.TKATEGORIE_SILO) || transportTypeCatCd.equals(Constant.TKATEGORIE_TRAILER)){
			//only has meaning if it is a silo
			
			nrChambers = new Long(1);
			if (StringUtils.notEmpty(helpForm.getInputNrChambers()) ){				
				nrChambers = Long.valueOf(helpForm.getInputNrChambers());
			}

			if (nrChambers.compareTo(new Long(1)) >= 0) {
				try{
					maxWeightChamber1T = new BigDecimal(helpForm.getInputMaxNetWeightChamber1T());
					checkFieldMaxValue(request, helpForm.getInputMaxNetWeightChamber1T(), TruckEntity.DBSIZE_MAX_NET_WEIGHT_CHAMBER1_T, TruckEntity.DBSIZE_PRECISION_MAX_NET_WEIGHT_CHAMBER1_T, "roadmasterdata.fields.chamber1T");
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
					checkFieldMaxValue(request, helpForm.getInputMaxNetWeightChamber2T(), TruckEntity.DBSIZE_MAX_NET_WEIGHT_CHAMBER2_T, TruckEntity.DBSIZE_PRECISION_MAX_NET_WEIGHT_CHAMBER2_T, "roadmasterdata.fields.chamber2T");
					if (maxWeightChamber2T.compareTo(new BigDecimal(0)) < 0) {
						weightNeg = true;
					}
				}
				catch (NumberFormatException e){
					MessageUtils.addActionWarning(request, "truckadmin.errors.chamber2t");
				}
			}
		}
		
		if (weightNeg) {
			MessageUtils.addActionWarning(request, "truckadmin.errors.weightnegative");
		}
		
		truck.setDispatchingCarrier(helpForm.getInputDispatchingCarrier());
		
		boolean noPoolPresent = inputPoolIds == null || (inputPoolIds != null && inputPoolIds.length < 1);
		if ( status.equals(true) && 
				( (noPoolPresent  || maxMeasure == null || taraMeasure == null )) ){
			MessageUtils.addActionWarning(request, "truckadmin.errors.active");	
		}
		
		// Get the selected Home Plant (the shippingpoint to return to).
		Long returnShippingpointId = helpForm.getSelectedShippingpoint();
		ShippingpointEntity returnShippingpoint = null;
	
		if (returnShippingpointId != null && returnShippingpointId != -1) {
			List<ShippingpointEntity> shippingpoints = helpForm.getShippingpoints();
			for (Iterator<ShippingpointEntity> it = shippingpoints.iterator(); it.hasNext();) {
				ShippingpointEntity shippingpoint = it.next();
				if (shippingpoint.getShippingpointId().equals(returnShippingpointId)) {
					returnShippingpoint = shippingpoint;
					break;
				}
			}
		}

		setTruckData(axis, comment, euroNorm, lat, lon, cd, maxMeasure, plateNo, taraMeasure,
				truckName, volume, manufacturer, status, sapVehicleCharacteristic, dustFilter,
				branding, pdaFlag, smsSendFlag, truck, service, transportTypeCatCd, containerId,
				nrChambers, maxWeightChamber1T, maxWeightChamber2T, sapVehicleNo, returnShippingpoint, iTrackAvailable);

		boolean noErrorWarningPresent = noErrorWarningPresent(request);
		if (  noErrorWarningPresent ) {
			try{		
			    TruckEntity entity;
				if (truck.getTruckId() != null ) {
					TrailerEntity trailer = helpForm.getSelectedTrailer();
					TrailerEntity originalTrailer = helpForm.getOriginalTrailer();
					ContainerEntity originalContainer = helpForm.getOriginalContainer();
					ContainerEntity newContainer = helpForm.getSelectedContainer();
					//the update truck expects the WHOLE composition sent, but on the UI the container is only there if it is directly attached to the truck!
					if ( originalContainer == null && originalTrailer != null ) {
						originalContainer = getServiceLocator().getTrailerAdminService().fetchContainerForTrailer(originalTrailer.getTrailerId(), false);
					}
					if ( newContainer == null && trailer != null) {
						newContainer = getServiceLocator().getTrailerAdminService().fetchContainerForTrailer(trailer.getTrailerId(), false); 
					}
					entity = service.updateTruck(truck, true, inputPoolIds, trailer, originalTrailer, newContainer, originalContainer, false);
				}
				else {	
					truck.setCarrier(helpForm.getCarrier());
					String inputSapVehicleNo = helpForm.getInputSapVehicleNo();
					
					if(inputSapVehicleNo.isEmpty()) {
						//Creation of new planning truck
						if (truck.getPriority() == null) {
							truck.setPriority(Constant.TRUCK_PRIORITY_MIN);
						}
						truck.setSapVehicleIsInternalFlag(truck.getCarrier().getSapVendor().getVnIsInternal());
					}
					else {
						//Creation of new generic truck
						SapImportVehnPostprocessService sapImportVehnPostprocessService = getServiceLocator().getSapImportVehnPostprocessService();
						SapVehicleEntity sapVehicle = sapImportVehnPostprocessService.fetchSapVehicleBySapNo(inputSapVehicleNo);
							
						//fill default fields from sapVehicle
						truck = sapImportVehnPostprocessService.fillDefaultTruck(truck, sapVehicle);
					}
					entity = service.createTruck(truck, inputPoolIds);
				}

				service.updateDriver(truck, helpForm.getLastDriver(), helpForm.getSelectedDriver(), false);

				helpForm.setTruck(entity);
				
				loadDriverLists(entity, helpForm, service);
		
			}
			catch (LogonException logonException) {
				addActionErrorForReason(logonException, request);
			}
			catch (EJBException ejbException) {
				Exception cause = (Exception) ejbException.getCause();
				
				if (cause instanceof LogonException) {
					addActionErrorForReason((LogonException) cause, request);
				} else {
					throw ejbException;
				}
			}
		}
		return !noErrorWarningPresent(request);
	}
	
	private void addActionErrorForReason(LogonException le, HttpServletRequest request) {
		Reason reason = le.getReason();
		if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())){
			MessageUtils.addActionWarning(request, le.getMessage());
			MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_TRUCK));
		} else if(reason.getReason().equals(Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE.getReason())) {
			MessageUtils.addActionError(request, Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE.getReason());
		} else {
			throw le;
		}
	}
	
	/**
	 * Maps the truckEntity object to the form's fields.
	 * truckEntity fields --> form fields
	 * @param helpForm
	 * @param service TODO
	 * @param locale TODO
	 */
	private void mapObjectToForm(TruckAdminForm helpForm, TruckEntity truck, TruckAdminService service, Locale locale) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		
		mapObjectToPanel1(helpForm, truck);
		mapObjectToPanel2(helpForm, service, truck, locale);
		mapObjectToPanel3(helpForm, truck);
	}
	

	/**
	 * Maps the truck's data to the screen's Stammdaten panel.
	 * @param helpForm
	 * @param truck
	 */
	private void mapObjectToPanel1(TruckAdminForm helpForm, TruckEntity truck) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		helpForm.setTruckId(truck.getTruckId());
		helpForm.setInputBranding(truck.getBrandingFlag() != null? truck.getBrandingFlag().toString() : null);
		helpForm.setTruckPriority(truck.getPriority());
		helpForm.setInputStatus(truck.getActiveFlag());
		helpForm.setInputInternalFlag(truck.getSapVehicleIsInternalFlag());
		helpForm.setCharacteristic(truck.getSapVehicleChar().getSapVehicleCharCd());
		helpForm.setInputDustfilter(truck.getHasDustfilterFlag() != null ? truck.getHasDustfilterFlag().toString() : null);
		helpForm.setInputPdaFlag(truck.getPdaAvailableFlag());
		helpForm.setInputSmsFlag(truck.getSmsSendFlag());
		helpForm.setInputLkwType(truck.getTruckType() != null ? truck.getTruckType().getTruckTypeCd() : null);
		helpForm.setInputManufacturer(truck.getManufacturer());
		helpForm.setInputMaxMeasure((truck.getMaxGrossWeightT() != null) ? truck.getMaxGrossWeightT().toString() : "");
		helpForm.setInputPlateNr(truck.getTruckPlateNo());
		helpForm.setInputTaraMeasure(truck.getTareWeightT() != null ? truck.getTareWeightT().toString() : "");
		helpForm.setInputTruckName(truck.getTruckName());
		helpForm.setInputVolumeM3((truck.getVolumeM3()!= null) ? truck.getVolumeM3().toString() : "");
		helpForm.setInputAxisNumber(truck.getAxisCount());
		helpForm.setInputEuroNorm(truck.getEuroNorm());
		helpForm.setInputTkategorie(truck.getTruckTransportCat().getTruckTransportCatCd());
		helpForm.setInputNrChambers(truck.getNrChambers().toString());
		helpForm.setInputMaxNetWeightChamber1T(truck.getMaxNetWeightChamber1T().toString());
		helpForm.setInputMaxNetWeightChamber2T(truck.getMaxNetWeightChamber2T().toString());
		helpForm.setInputDispatchingCarrierId(truck.getDispatchingCarrier() != null ? truck.getDispatchingCarrier().getCarrierId() : null);
		helpForm.setInputSapVehicleNo(truck.getSapVehicleNo());
		helpForm.setiTrackAvailableFlag(truck.getITrackAvailableFlag());
	}
	

	
	
	/**
	 * @param helpForm
	 * @param service
	 * @param truck TODO
	 * @param locale TODO
	 */
	private void mapObjectToPanel2(TruckAdminForm helpForm, TruckAdminService service, TruckEntity truck, Locale locale) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		helpForm.resetPoolPanel();
		helpForm.setInputComment(truck.getTruckComment());
		List fullPoolList = service.fetchPools(helpForm.getCarrierId(), locale);
		helpForm.setFullPoolList(fullPoolList);
		List pools = service.fetchSelectedPoolsTxForTruck(truck.getTruckId(), locale);
		List poolList = new ArrayList() ;
		poolList.addAll(fullPoolList);
		Iterator it = pools.iterator();
		while(it.hasNext()){
			PoolTxEntity ent = (PoolTxEntity) it.next();
			Iterator it2 = poolList.iterator();
			while (it2.hasNext()){
				PoolTxEntity entity = (PoolTxEntity) it2.next();
				if ( entity.getPoolTxPK().getPoolId().equals( ent.getPoolTxPK().getPoolId() )){
					it2.remove();
					break;
				}
			}
		}
		helpForm.setPoolList(poolList);
		long[] selectedPools = convertPoolsToLongArray(pools);
		helpForm.setSelectedPools(pools);
		helpForm.setSelectedPoolIds(selectedPools);

		TrailerEntity trailer = service.fetchTrailerForTruck(truck.getTruckId(), true);
		if (trailer != null) {
			helpForm.setInputTrailerSelect(trailer.getTrailerId().toString());
		}
		helpForm.setOriginalTrailer(trailer);

		helpForm.setSelectedDriver(null);
		helpForm.setTruckInSession(false);
		if (truck.getTruckId() != null){			
			DriverEntity driver = service.fetchDriver(truck.getTruckId());
			if (driver != null) {				
				helpForm.setSelectedDriver(driver);
				final boolean isTruckInSession = service.isTruckInSession(truck);
				helpForm.setTruckInSession(isTruckInSession);
			}
		}		
		
		//it is OK to fetch the container only for the truck!
		ContainerEntity container = service.fetchContainerForTruck(truck.getTruckId(), true);
		if (container != null) {
			helpForm.setInputContainerSelect(container.getContainerId().toString());
		}
		helpForm.setOriginalTrailer(trailer);
		
		helpForm.setOriginalContainer(container);
	}

	/**
	 * @param helpForm
	 * @param truck
	 */
	private void mapObjectToPanel3(TruckAdminForm helpForm, TruckEntity truck) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		helpForm.setInputHomeBaseLat(truck.getHomebaseCoordLat() != null ? truck.getHomebaseCoordLat().toString() : "");
		helpForm.setInputHomeBaseLon(truck.getHomebaseCoordLon() != null ? truck.getHomebaseCoordLon().toString() : "");
		setTruckHomePlant(helpForm);
	}

	
	/**
	 * @param list
	 * @return
	 */
	private long[] convertPoolsToLongArray(List list ) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		long [] returnArray = new long[list.size()];
		if (list != null){
			Iterator it = list.iterator();
			int i=0;
			while (it.hasNext() ){
				PoolTxEntity ent = (PoolTxEntity )it.next();
				returnArray[i] = ent.getPoolTxPK().getPoolId().longValue();
				i++;
			}
		}
		return returnArray;
	}
	
	/**
	 * Sets the TruckEntity's fields to the parameters.
	 * @param axis
	 * @param comment
	 * @param euroNorm
	 * @param lat
	 * @param lon
	 * @param typeCd
	 * @param maxMeasure
	 * @param plateNo
	 * @param taraMeasure
	 * @param truckName
	 * @param volume
	 * @param manufacturer
	 * @param status
	 * @param dustFilter
	 * @param branding
	 * @param pdaFlag
	 * @param truck
	 * @param service
	 * @param transportTypeCatCd
	 * @param containerId
	 * @param nrChambers TODO
	 * @param maxWeightChamber1T TODO
	 * @param maxWeightChamber2T TODO
	 * @param iTrackAvailable
	 */
	private void setTruckData(String axis, String comment, String euroNorm, Double lat,
			Double lon, Long typeCd, BigDecimal maxMeasure, String plateNo, BigDecimal taraMeasure, String truckName,
			BigDecimal volume, String manufacturer, Boolean status, Long sapVehicleCharacteristic, Boolean dustFilter,
			Boolean branding,
			Boolean pdaFlag, Boolean smsSendFlag, TruckEntity truck, TruckAdminService service, Long transportTypeCatCd,
			Long containerId,
			Long nrChambers, BigDecimal maxWeightChamber1T, BigDecimal maxWeightChamber2T, String sapVehicleNo,
			ShippingpointEntity returnShippingpoint, Boolean iTrackAvailable) {

		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		truck.setAxisCount(axis);
		
		TruckTransportCatBsEntity transportCd = getRefDataGenericsService().findBsEntity(TruckTransportCatBsEntity.class, transportTypeCatCd);
		truck.setTruckTransportCat(transportCd);
		
		SapVehicleCharBsEntity sapVehicleCharBsEntity = getRefDataGenericsService().findBsEntity(SapVehicleCharBsEntity.class, sapVehicleCharacteristic);
		
		truck.setTruckComment(comment);
		truck.setEuroNorm(euroNorm);
		truck.setHomebaseCoordLat(lat);
		truck.setHomebaseCoordLon(lon);
		truck.setManufacturer(manufacturer);
		truck.setBrandingFlag(branding);
		truck.setActiveFlag(status);
		truck.setHasDustfilterFlag(dustFilter);
		truck.setPdaAvailableFlag(pdaFlag);
		truck.setSmsSendFlag(smsSendFlag);
		truck.setSapVehicleChar(sapVehicleCharBsEntity);
		truck.setSapVehicleNo(sapVehicleNo);
				
		TruckTypeBsEntity bs = getRefDataGenericsService().findBsEntity(TruckTypeBsEntity.class, typeCd);
		truck.setTruckType(bs);
		
		truck.setMaxGrossWeightT(maxMeasure);
		if ( truck.getTruckPlateNo() == null || !truck.getTruckPlateNo().equals(plateNo)){			
			truck.setTruckPlateNo(plateNo);
		}
		truck.setTruckName(truckName);
		truck.setTareWeightT(taraMeasure);
		truck.setVolumeM3(volume);
		truck.setNrChambers(nrChambers);
		truck.setMaxNetWeightChamber1T(maxWeightChamber1T);
		truck.setMaxNetWeightChamber2T(maxWeightChamber2T);
		
		truck.setReturnShippingpoint(returnShippingpoint);
		truck.setITrackAvailableFlag(iTrackAvailable);
	}
	
	private void setTruckHomePlant(TruckAdminForm helpForm) {
		// Load the plants
		final List<DropdownEntity> plants = PlantSareaUtils.getPlantList(getPlantSareaService());
		if (plants.isEmpty()) {
			return;
		}
		
		TruckEntity truck = helpForm.getTruck();
		
		helpForm.setPlants(plants);
		
		List<String> plantCds = new ArrayList<String>();
		for (DropdownEntity plant : plants) {
			plantCds.add(plant.getValue());
		}
		
		// Load all the shipping points for the plants
		List<ShippingpointEntity> allShippingpoints = getServiceLocator().getShippingPointService().
				loadShippingPoints(null, true, plantCds, true);
		helpForm.setAllShippingpoints(allShippingpoints);
		
		// Remove those plants, which do not have connection to a shippingpoint 
		for (Iterator<DropdownEntity> it = plants.iterator(); it.hasNext();) {
			DropdownEntity plant = it.next();
			final String plantCd = plant.getValue();

			boolean exists = PlantSareaUtils.isShippingpointExistForPlantCd(allShippingpoints, plantCd);

			if (!exists) {
				it.remove();
			}
		}
		
		List<ShippingpointEntity> shippingpoints = new ArrayList<ShippingpointEntity>();
		
		// The truck may have Home Plant set, fill in the appropriate values
		if (truck.getReturnShippingpoint() != null) {
			Long returnShippingpointId = truck.getReturnShippingpoint().getShippingpointId();
			String plantCd = truck.getReturnShippingpoint().getPlant().getPlantCd();
			
			//Filter all shipping points for a plantCd 
			shippingpoints = PlantSareaUtils.getShippingpointsForPlantCd(allShippingpoints, plantCd);
			
			helpForm.setSelectedPlant(plantCd);
			helpForm.setSelectedShippingpoint(returnShippingpointId);
		}
		
		helpForm.setShippingpoints(shippingpoints);
	}
}

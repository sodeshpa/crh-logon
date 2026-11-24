/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.truckadmin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.admin.web.utils.TruckAdminChangeLogic;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.views.DriverWithoutSessionEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.DropdownEntity;

/**
 * Form of page LKWStammDatenTile,jsp.
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_TruckAdminForm"
 */
public class TruckAdminForm extends ActionForm {
	
	// TODO (emoke May 29, 2009):make sure use everywhere this
	public static final String formName = "admin_TruckAdminForm";
	
	private TruckEntity truck;
	private CarrierEntity carrier;
	private TrailerEntity originalTrailer;
	private ContainerEntity originalContainer;
	private String carrierLoginName;
	private Long carrierId;
	private Long truckId;
	private String inputAction;
	private List truckList;
	private List carrierList;
	private Map truckTypeMap;
	private List truckTkategorieList;
	private List poolList;
	private List driverList; // list of DriverWithoutSessionEntity-s
	private Map driverMap; // Map of driverId - DriverEntity
	private List trailerList;
	private List containerList;
	private List fullPoolList;
	
	private long [] selectedPoolIds;
	private List selectedPools;
	
	private String inputTruckId;
	private String inputTruckName;
	private String inputPlateNr;
	private Long inputLkwType;
	private String inputVolumeM3;
	private Boolean inputStatus;
	private Boolean inputInternalFlag;
	private String inputTaraMeasure;
	private String inputMaxMeasure;
	private String inputComment;
	private String inputHomeBaseLat;
	private String inputHomeBaseLon;
	private long [] inputPoolIds;
	private Long inputTkategorie;
	private String inputEuroNorm;
	private String inputBranding;
	private String inputDustfilter;
	private String inputAxisNumber;
	private String inputManufacturer;
	private Boolean inputPdaFlag;
	private String inputContainerSelect;
	private int inputDriverSelect;
	private DriverEntity lastDriver;
	private String inputTrailerSelect;
	private Long inputDispatchingCarrierId;
	private String inputSapVehicleNo;
	private Boolean inputSmsFlag;

	private String inputMaxNetWeightChamber1T;
	private String inputMaxNetWeightChamber2T;
	private String inputNrChambers;
	
	private RfidobjectTypeBsEntity truckRefDataBsEntity;
	
	private boolean truckLoaded;
	private boolean truckAssigned;
	private boolean trailerCompositionAssigned;
	private boolean containerAssigned;
	private boolean truckInSession;
	
	private Double []lastPosition;
    private static final long serialVersionUID = -8910104051337644556L;
    
    private Long characteristic;
    
    /** This array is used to create the dropdown on the UI. */
    private long[] truckPriorities;
    private long truckPriority;
    
    /* Truck Home Plant related fields */
    /** The list of plants */
	private List<DropdownEntity> plants;
    /** The list of shippingpoints which belongs to the selected plant */
	private List<ShippingpointEntity> shippingpoints;
	/** The id of the selected plant */
	private String selectedPlant;
	/** The id of the selected shippingpoint */
	private Long selectedShippingpoint;
	/** The list of shippingpoints for all the plants */
	private List<ShippingpointEntity> allShippingpoints;

	private boolean showOnlyActiveTrucks;
	private boolean firstTimeShowOnlyActiveTrucks;
	private Boolean iTrackAvailableFlag;
   

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TruckAdminForm.class);

    /**
     * Default constructor.
     */
    public TruckAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TruckAdminForm");
		}
		// ]]
		try {
    		init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
		
    }


	public TruckEntity getTruck() {
		return truck;
	}



	public void setTruck(TruckEntity truck) {
		this.truck = truck;
	}



	public CarrierEntity getCarrier() {
		return carrier;
	}



	public void setCarrier(CarrierEntity carrier) {
		this.carrier = carrier;
	}



	public Long getCarrierId() {
		return carrierId;
	}



	public void setCarrierId(Long carrierId) {
		this.carrierId = carrierId;
	}



	public Long getTruckId() {
		return truckId;
	}



	public void setTruckId(Long truckId) {
		this.truckId = truckId;
	}



	public String getInputAction() {
		return inputAction;
	}



	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}



	public List getTruckList() {
		return truckList;
	}



	public void setTruckList(List truckList) {
		this.truckList = truckList;
	}



	public List getCarrierList() {
		return carrierList;
	}



	public void setCarrierList(List carrierList) {
		this.carrierList = carrierList;
	}



	public List getPoolList() {
		return poolList;
	}



	public void setPoolList(List poolList) {
		this.poolList = poolList;
	}



	public long[] getSelectedPoolIds() {
		return selectedPoolIds;
	}



	public void setSelectedPoolIds(long[] selectedPoolIds) {
		this.selectedPoolIds = selectedPoolIds;
	}



	public List getSelectedPools() {
		return selectedPools;
	}



	public void setSelectedPools(List selectedPools) {
		this.selectedPools = selectedPools;
	}



	public String getInputTruckId() {
		return inputTruckId;
	}



	public void setInputTruckId(String inputTruckId) {
		this.inputTruckId = inputTruckId;
	}



	public String getInputTruckName() {
		return inputTruckName;
	}



	public void setInputTruckName(String inputTruckName) {
		if (!StringUtils.empty(inputTruckName) ) {			
			this.inputTruckName = inputTruckName;
		}
		else{
			int newestTruckNumber = getTruckList() != null ? getTruckList().size()+1 : 1;
			this.inputTruckName = this.carrierLoginName + " " + newestTruckNumber;
		}
	}



	public String getInputPlateNr() {
		return inputPlateNr;
	}



	public void setInputPlateNr(String inputPlateNr) {
		this.inputPlateNr = inputPlateNr;
	}



	public Long getInputLkwType() {
		return inputLkwType;
	}



	public void setInputLkwType(Long inputLkwType) {
		this.inputLkwType = inputLkwType;
	}



	public String getInputVolumeM3() {
		return inputVolumeM3;
	}



	public void setInputVolumeM3(String inputVolumeM3) {
		if (inputVolumeM3 != null){		
			inputVolumeM3 = inputVolumeM3.replaceAll(",", ".");
		}
		this.inputVolumeM3 = inputVolumeM3;
	}



	public Boolean getInputStatus() {
		return inputStatus;
	}



	public void setInputStatus(Boolean inputStatus) {
		this.inputStatus = inputStatus;
	}

	
	public Boolean getInputInternalFlag() {
		return inputInternalFlag;
	}

	
	public void setInputInternalFlag(Boolean inputInternalFalg) {
		this.inputInternalFlag = inputInternalFalg;
	}
	
	
	public String getInputTaraMeasure() {
		return inputTaraMeasure;
	}



	public void setInputTaraMeasure(String inputTaraMeasure) {
		if (inputTaraMeasure != null ) {		
			inputTaraMeasure = inputTaraMeasure.replaceAll(",", ".");
		}
		this.inputTaraMeasure = inputTaraMeasure;
	}



	public String getInputMaxMeasure() {
		return inputMaxMeasure;
	}



	public void setInputMaxMeasure(String inputMaxMeasure) {
		if (inputMaxMeasure != null) {		
			inputMaxMeasure = inputMaxMeasure.replaceAll(",", ".");
		}
		this.inputMaxMeasure = inputMaxMeasure;
	}



	public String getInputComment() {
		return inputComment;
	}



	public void setInputComment(String inputComment) {
		this.inputComment = inputComment;
	}



	public String getInputHomeBaseLat() {
		return inputHomeBaseLat;
	}



	public void setInputHomeBaseLat(String inputHomeBaseLat) {
		this.inputHomeBaseLat = inputHomeBaseLat;
	}



	public String getInputHomeBaseLon() {
		return inputHomeBaseLon;
	}



	public void setInputHomeBaseLon(String inputHomeBaseLon) {
		this.inputHomeBaseLon = inputHomeBaseLon;
	}



	public long[] getInputPoolIds() {
		return inputPoolIds;
	}



	public void setInputPoolIds(long[] inputPoolIds) {
		this.inputPoolIds = inputPoolIds;
		setPoolList(new ArrayList());
		poolList.addAll(fullPoolList);
		setSelectedPools(new ArrayList());
		if (inputPoolIds != null && inputPoolIds.length == 1 && inputPoolIds[0] == 0l){
			resetPoolPanel();
		}
		int i=0;
		if (inputPoolIds!= null && inputPoolIds.length != 0){
			for (i=0; i< inputPoolIds.length;i++){
				long id = inputPoolIds[i];
				Iterator it2 = poolList.iterator();
				while (it2.hasNext()){
					PoolTxEntity entity = (PoolTxEntity) it2.next();
					if ( entity.getPoolTxPK().getPoolId().equals( id )){
						selectedPools.add(entity);
						it2.remove();
						break;
					}
				}
			}
		}
	}



	public String getInputEuroNorm() {
		return inputEuroNorm;
	}



	public void setInputEuroNorm(String inputEuroNorm) {
		this.inputEuroNorm = inputEuroNorm;
	}



	public String getInputAxisNumber() {
		return inputAxisNumber;
	}



	public void setInputAxisNumber(String inputAxisNumber) {
		this.inputAxisNumber = inputAxisNumber;
	}



	public String getInputManufacturer() {
		return inputManufacturer;
	}



	public void setInputManufacturer(String inputManufacturer) {
		this.inputManufacturer = inputManufacturer;
	}



	public Boolean getInputPdaFlag() {
		return inputPdaFlag;
	}



	public void setInputPdaFlag(Boolean inputPdaFlag) {
		this.inputPdaFlag = inputPdaFlag;
	}

	public boolean isInputPdaFlagDisabled() {
		return TruckAdminChangeLogic.disableChangeFunctionality(this, TruckAdminChangeLogic.FUNCTIONALITY_SET_PDA_FLAG);
	}
	
	public boolean isInputStatusDisabled() {
		return characteristic == null || characteristic != Constant.VEHICLE_CHARACTERISTIC_PLANNING;
	}

	/**
     * Initializes the form. This has the effect of clearing all the input
     * fields on the ordermgr.web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			this.truck = null;
			this.carrier = null;
			this.originalTrailer = null;
			this.originalContainer = null;
			this.carrierId = null;
			this.carrierLoginName = null;
			this.truckId = null;
			this.inputAction = "";
			this.truckList = new ArrayList();
			this.carrierList = new ArrayList();
			this.truckTypeMap = new TreeMap();
			this.poolList = new ArrayList();
			this.fullPoolList = new ArrayList();
			this.selectedPoolIds = new long [0];
			this.selectedPools = new ArrayList();
			this.inputTruckId = "";
			this.inputTruckName = "";
			this.inputPlateNr = "";
			this.inputLkwType = null;
			this.inputVolumeM3 = "";
			this.inputStatus = false;
			this.inputTaraMeasure = "";
			this.inputMaxMeasure = "";
			this.inputComment = "";
			this.inputHomeBaseLat = "";
			this.inputHomeBaseLon = "";
			this.inputPoolIds = new long[0];
			this.inputEuroNorm = "";
			this.inputBranding = null;
			this.inputDustfilter = null;
			this.inputAxisNumber = "";
			this.inputManufacturer = "";			
			this.inputPdaFlag = false;
			this.lastPosition = new Double [2];
			this.truckLoaded = false;
			this.truckAssigned = false;
			this.trailerCompositionAssigned = false;
			this.containerAssigned = false;
			this.truckInSession = false;
			this.inputNrChambers = "";
			this.inputMaxNetWeightChamber1T = "";
			this.inputMaxNetWeightChamber1T = "";
			this.inputDispatchingCarrierId = null;
			this.inputSapVehicleNo = "";
		    this.characteristic = null;
		    this.firstTimeShowOnlyActiveTrucks = true;
		    plants = new ArrayList<DropdownEntity>();
			shippingpoints = new ArrayList<ShippingpointEntity>(); 			
			selectedPlant = null;			
			selectedShippingpoint = null;			
			allShippingpoints = new ArrayList<ShippingpointEntity>();
			this.iTrackAvailableFlag = false;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * Override struts method.
     * Note: is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     * 
     * Do not reset the flags, which are not sent in case they are not checked, and so they do not change their values.
     * This calls must only be called if the whole form is submitted. Otherwise they should not be reseted, since they may be
     * true, but not sent by AJAX call. 
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			if (request.getParameter("ajaxCall") == null) {
				// do not reset the 'PDA flag' in case it is disabled, otherwise the checked state will disappear on a screen reload
				if (!isInputPdaFlagDisabled()) {
					setInputPdaFlag(false);
				}
				setInputSmsFlag(false);
				// do not reset the 'active flag' in case it is disabled, otherwise the checked state will disappear on a screen reload
				if (!isInputStatusDisabled()) {
					setInputStatus(false);
				}
				// the 'internal flag' is always disabled, do not reset it, otherwise the checked state will disappear on a screen reload
				// setInputInternalFlag(false);
				setInputPoolIds(new long[0]);

				// avoid setting the "show only active trucks" flag to false for the first time
				setShowOnlyActiveTrucks(this.firstTimeShowOnlyActiveTrucks);
				this.firstTimeShowOnlyActiveTrucks = false;
			}
			
			String action = request.getParameter("inputAction");
			if (action != null && action.equals("selectCarrier")) {
				setShowOnlyActiveTrucks(true);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * Resets the values of the truck information input fields.
     */
	public void resetTruckData( TruckEntity truck ) {
		resetMainDataPanel(truck);
		resetPoolPanel();
	}
	/** Resets the values of the shippingpoint related fields. */
	public void resetShippingpiointFields() {
		this.shippingpoints = null;
		this.selectedShippingpoint = null;
	}

	/**
	 * 
	 */
	public void resetMainDataPanel(TruckEntity truck) {
		
		this.truck = truck;
		if (truck != null ) {			
			this.truckId = truck.getTruckId();
		}
		else{
			this.truckId = null;
		}
		this.inputTruckId = "";
		setInputTruckName(null);
		this.inputPlateNr = "";
		this.inputLkwType = null;
		this.inputTkategorie = null;
		this.inputTrailerSelect = null;
		this.inputContainerSelect = null;
		this.originalTrailer = null;
		this.originalContainer = null;
		this.inputVolumeM3 = "";
		this.inputStatus = false;
		this.inputTaraMeasure = "";
		this.inputMaxMeasure = "";
		this.inputComment = "";
		this.inputHomeBaseLat = "";
		this.inputHomeBaseLon = "";
		this.inputEuroNorm = "";
		this.inputBranding = null;
		this.inputDustfilter = null;
		this.inputAxisNumber = "";
		this.inputManufacturer = "";
		this.inputPdaFlag = false;
		this.lastPosition = new Double [2];
		this.inputNrChambers = "0";
		this.inputMaxNetWeightChamber1T = "0";
		this.inputMaxNetWeightChamber2T = "0";
		this.truckLoaded = false;
		this.truckAssigned = false;
		this.trailerCompositionAssigned = false;
		this.containerAssigned = false;
		this.truckInSession = false;
		this.inputDispatchingCarrierId = null;
		this.inputSapVehicleNo = "";
		this.characteristic = null;
	    plants = new ArrayList<DropdownEntity>();
		shippingpoints = new ArrayList<ShippingpointEntity>(); 			
		selectedPlant = null;			
		selectedShippingpoint = null;			
		allShippingpoints = new ArrayList<ShippingpointEntity>();
		this.iTrackAvailableFlag = false;
	}

	/**
	 * Resets the selected pool values;
	 */
	public void resetPoolPanel() {
		this.inputPoolIds = new long[0];
		this.selectedPoolIds = new long [0];
		this.selectedPools = new ArrayList();
	}
	
    /**
	 * Override struts method.
	 * Perform form-specific validation of input parameters.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = { { "mapping", mapping }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			ActionErrors errors = super.validate(mapping, request);

			if (errors == null) {
				errors = new ActionErrors();
			}

			try {
				new BigDecimal(getInputMaxMeasure());
			}
			catch (NumberFormatException e) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"truckadmin.errors.maxmeasure"));
			}

			String plateNo = getInputPlateNr();
			if (StringUtils.empty(plateNo)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"truckadmin.errors.empty.plateno"));
			}

			try {
				new BigDecimal(getInputTaraMeasure());
			}
			catch (NumberFormatException e) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"truckadmin.errors.tarameasure"));
			}

			String truckName = getInputTruckName();
			if (StringUtils.empty(truckName)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"truckadmin.errors.empty.truckid"));
			}

			try {
				new BigDecimal(getInputVolumeM3());
			}
			catch (NumberFormatException e) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"truckadmin.errors.volumen"));
			}

			return result = errors;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
    
    public void clear(){
    	init();
    }


	public Double[] getLastPosition() {
		return lastPosition;
	}


	public void setLastPosition(Double[] lastPosition) {
		this.lastPosition = lastPosition;
	}


	public Long getInputTkategorie() {
		return inputTkategorie;
	}


	public void setInputTkategorie(Long inputTkategorie) {
		this.inputTkategorie = inputTkategorie;
	}


	public List getTruckTkategorieList() {
		return truckTkategorieList;
	}


	public void setTruckTkategorieList(List truckTkategorieList) {
		this.truckTkategorieList = truckTkategorieList;
	}


	public List getTrailerList() {
		return trailerList;
	}


	public void setTrailerList(List trailerList) {
		this.trailerList = trailerList;
	}




	public void setInputContainerSelect(String inputContainerSelect) {
		this.inputContainerSelect = inputContainerSelect;
	}



	public void setInputTrailerSelect(String inputTrailerSelect) {
		this.inputTrailerSelect = inputTrailerSelect;
	}


	public String getInputContainerSelect() {
		return inputContainerSelect;
	}


	public String getInputTrailerSelect() {
		return inputTrailerSelect;
	}
	
	public TrailerEntity getSelectedTrailer() {
		List list = getTrailerList();
		TrailerEntity trailer = null;
		Long inputTrailerSelectLong = getInputTrailerSelectLong();
		if (inputTrailerSelectLong != null && list != null && !list.isEmpty()){
			Iterator it = list.iterator();
			while (it.hasNext()) {
				TrailerEntity tailer = (TrailerEntity) it.next();
				if (tailer.getTrailerId().equals(Long.valueOf(inputTrailerSelect))){
					trailer = tailer;
					break;
				}
			}
		}
		return trailer;
	}
	
	public ContainerEntity getSelectedContainer() {
		List list = getContainerList();
		ContainerEntity container = null;
		Long inputContainerSelectLong = getInputContainerSelectLong();
		if (inputContainerSelectLong != null && list != null && !list.isEmpty()){
			Iterator it = list.iterator();
			while (it.hasNext()) {
				ContainerEntity tailer = (ContainerEntity) it.next();
				if (tailer.getContainerId().equals(Long.valueOf(inputContainerSelect))){
					container = tailer;
					break;
				}
			}
		}
		return container;
	}

	public Long getInputContainerSelectLong() {
		Long id = null;
		try{
			if (StringUtils.empty(inputContainerSelect)) {
				id = null;
			}
			else{
				id = Long.parseLong(inputContainerSelect);
			}			
		}
		catch(Exception e){
			id = null;
		}
		return id;
	}

	
	public Long getInputTrailerSelectLong() {
		Long id = null;
		try{
			if (StringUtils.empty(inputTrailerSelect)) {
				id = null;
			}
			else{
				id = Long.parseLong(inputTrailerSelect);
			}			
		}
		catch(Exception e){
			id = null;
		}
		return id;
	}


	public boolean isTruckLoaded() {
		return truckLoaded;
	}


	public void setTruckLoaded(boolean truckLoaded) {
		this.truckLoaded = truckLoaded;
	}


	public String getInputBranding() {
		return inputBranding;
	}
	
	public Boolean getInputBrandingBoolean() {
		Boolean val = null;
		try{
			if (StringUtils.empty(inputBranding)) {
				val= null;
			}
			else{
				val = Boolean.parseBoolean(inputBranding);
			}			
		}
		catch(Exception e){
			val = null;
		}
		return val;
	}


	public void setInputBranding(String inputBranding) {
		this.inputBranding = inputBranding;
	}


	public String getInputDustfilter() {
		return inputDustfilter;
	}
	
	public Boolean getInputDustfilterBoolean() {
		Boolean val = null;
		try{
			if (StringUtils.empty(inputDustfilter)) {
				val= null;
			}
			else{
				val = Boolean.parseBoolean(inputDustfilter);
			}			
		}
		catch(Exception e){
			val = null;
		}
		return val;
	}


	public void setInputDustfilter(String inputDustfilter) {
		this.inputDustfilter = inputDustfilter;
	}


	public List getFullPoolList() {
		return fullPoolList;
	}


	public void setFullPoolList(List fullPoolList) {
		this.fullPoolList = fullPoolList;
	}


	public RfidobjectTypeBsEntity getTruckRefDataBsEntity() {
		return truckRefDataBsEntity;
	}


	public void setTruckRefDataBsEntity(RfidobjectTypeBsEntity truckRefDataBsEntity) {
		this.truckRefDataBsEntity = truckRefDataBsEntity;
	}


	public String getCarrierLoginName() {
		return carrierLoginName;
	}


	public void setCarrierLoginName(String carrierLoginName) {
		this.carrierLoginName = carrierLoginName;
	}


	public void setSelectedDriver(DriverEntity driver) {
	    lastDriver = driver;
	    inputDriverSelect = Constant.DROPDOWN_NOT_SELECTED;
	    if (driver != null) {
	        for (int i = 0; i < driverList.size(); i++) {
                if (((DriverWithoutSessionEntity)driverList.get(i)).getDriverId().equals(driver.getDriverId())) {
                    inputDriverSelect = i;
                    break;
                }
            }
	    }
	}
	public DriverEntity getSelectedDriver() {
	    DriverEntity result = null;
	    if (inputDriverSelect != Constant.DROPDOWN_NOT_SELECTED) {
	        DriverWithoutSessionEntity driver = (DriverWithoutSessionEntity) driverList.get(inputDriverSelect);
	        result = (DriverEntity) driverMap.get(driver.getDriverId());
	    }
	    return result;
	}
	public DriverEntity getLastDriver() {
	    return lastDriver;
	}
	
	public String getSelectedDriverMobileNr() {
		DriverEntity driver = getLastDriver();
		
		if (getLastDriver() == null) {
			return "-";
		}
		
		return driver.getUser().getPerson().getMobileNr();
	}
	
	public int getInputDriverSelect() {
		return inputDriverSelect;
	}


	public void setInputDriverSelect(int inputDriverSelect) {
		this.inputDriverSelect = inputDriverSelect;
	}
	
	public List getDriverList() {
		return driverList;
	}


	public void setDriverLists(List driverList, List driverEntityList) {
		this.driverList = driverList;
        this.driverMap = new HashMap();
		for (Iterator it = driverEntityList.iterator(); it.hasNext();) {
            DriverEntity driver = (DriverEntity) it.next();
            driverMap.put(driver.getDriverId(), driver);
        }
	}


	public TrailerEntity getOriginalTrailer() {
		return originalTrailer;
	}


	public void setOriginalTrailer(TrailerEntity originalTrailer) {
		this.originalTrailer = originalTrailer;
	}


	public String getInputMaxNetWeightChamber1T() {
		return inputMaxNetWeightChamber1T;
	}


	public void setInputMaxNetWeightChamber1T(String inputMaxNetWeightChamber1T) {
		if (inputMaxNetWeightChamber1T != null) {		
			inputMaxNetWeightChamber1T = inputMaxNetWeightChamber1T.replaceAll(",", ".");
		}
		this.inputMaxNetWeightChamber1T = inputMaxNetWeightChamber1T;
	}


	public String getInputMaxNetWeightChamber2T() {
		return inputMaxNetWeightChamber2T;
	}


	public void setInputMaxNetWeightChamber2T(String inputMaxNetWeightChamber2T) {
		if (inputMaxNetWeightChamber2T != null) {		
			inputMaxNetWeightChamber2T = inputMaxNetWeightChamber2T.replaceAll(",", ".");
		}
		this.inputMaxNetWeightChamber2T = inputMaxNetWeightChamber2T;
	}


	public String getInputNrChambers() {
		return inputNrChambers;
	}


	public void setInputNrChambers(String inputNrChambers) {
		this.inputNrChambers = inputNrChambers;
	}


	public List getContainerList() {
		return containerList;
	}


	public void setContainerList(List containerList) {
		this.containerList = containerList;
	}


	public ContainerEntity getOriginalContainer() {
		return originalContainer;
	}


	public void setOriginalContainer(ContainerEntity originalContainer) {
		this.originalContainer = originalContainer;
	}


	public Map getTruckTypeMap() {
		return truckTypeMap;
	}


	public void setTruckTypeMap(Map truckTypeMap) {
		this.truckTypeMap = truckTypeMap;
	}


	public boolean isTruckAssigned() {
		return truckAssigned;
	}


	public void setTruckAssigned(boolean truckAssigned) {
		this.truckAssigned = truckAssigned;
	}


	public boolean isTruckInSession() {
		return truckInSession;
	}


	public void setTruckInSession(boolean truckInSession) {
		this.truckInSession = truckInSession;
	}


	public boolean isTrailerCompositionAssigned() {
		return trailerCompositionAssigned;
	}


	public void setTrailerCompositionAssigned(boolean trailerCompositionAssigned) {
		this.trailerCompositionAssigned = trailerCompositionAssigned;
	}


	public boolean isContainerAssigned() {
		return containerAssigned;
	}


	public void setContainerAssigned(boolean containerAssigned) {
		this.containerAssigned = containerAssigned;
	}


	public CarrierEntity getInputDispatchingCarrier() {
		if ( inputDispatchingCarrierId == null || inputDispatchingCarrierId == Constant.INVALID_KEY ) {
			return null;
		}
		Iterator it = carrierList.iterator();
		while (it.hasNext()) {
			CarrierEntity carrier = (CarrierEntity) it.next();
			if (carrier.getCarrierId().equals(this.inputDispatchingCarrierId)) {
				return carrier;
			}
		}
		return null;
	}

	public void setInputDispatchingCarrierId(Long inputDispatchingCarrierId) {
		this.inputDispatchingCarrierId = inputDispatchingCarrierId;
	}

	public Long getInputDispatchingCarrierId() {
		return inputDispatchingCarrierId;
	}

	public String getInputSapVehicleNo() {
		return inputSapVehicleNo;
	}

	public void setInputSapVehicleNo(String inputSapVehicleNo) {
		this.inputSapVehicleNo = inputSapVehicleNo;
	}

	public Long getCharacteristic() {
		return characteristic;
	}

	public void setCharacteristic(Long characteristics) {
		this.characteristic = characteristics;
	}
	
	public Boolean getInputSmsFlag() {
		return inputSmsFlag;
	}

	public void setInputSmsFlag(Boolean inputSmsFlag) {
		this.inputSmsFlag = inputSmsFlag;
	}

	public long getTruckPriority() {
		return truckPriority;
	}

	public void setTruckPriority(long truckPriority) {
		this.truckPriority = truckPriority;
	}


	public long[] getTruckPriorities() {
		return truckPriorities;
	}


	public void setTruckPriorities(long[] truckPriorities) {
		this.truckPriorities = truckPriorities;
	}


	public List<DropdownEntity> getPlants() {
		return plants;
	}


	public void setPlants(List<DropdownEntity> plants) {
		this.plants = plants;
	}


	public List<ShippingpointEntity> getShippingpoints() {
		return shippingpoints;
	}


	public void setShippingpoints(List<ShippingpointEntity> shippingpoints) {
		this.shippingpoints = shippingpoints;
	}


	public String getSelectedPlant() {
		return selectedPlant;
	}


	public void setSelectedPlant(String selectedPlant) {
		this.selectedPlant = selectedPlant;
	}


	public Long getSelectedShippingpoint() {
		return selectedShippingpoint;
	}


	public void setSelectedShippingpoint(Long selectedShippingpoint) {
		this.selectedShippingpoint = selectedShippingpoint;
	}


	public List<ShippingpointEntity> getAllShippingpoints() {
		return allShippingpoints;
	}


	public void setAllShippingpoints(List<ShippingpointEntity> allShippingpoints) {
		this.allShippingpoints = allShippingpoints;
	}
	
	/**
	 * Returns true, if the user wants to see only the active trucks 
	 * @return true, if the user wants to see only the active trucks
	 */
	public boolean isShowOnlyActiveTrucks() {
		return showOnlyActiveTrucks;
	}

	/**
	 * Sets if the user only wants to see the active trucks.
	 * @param showOnlyActiveTrucks if true, only the active trailers should be showed to the user
	 */
	public void setShowOnlyActiveTrucks(boolean showOnlyActiveTrucks) {
		this.showOnlyActiveTrucks = showOnlyActiveTrucks;
	}

	public Boolean getiTrackAvailableFlag() {
		return iTrackAvailableFlag;
	}

	public void setiTrackAvailableFlag(Boolean iTrackAvailableFlag) {
		this.iTrackAvailableFlag = iTrackAvailableFlag;
	}
}
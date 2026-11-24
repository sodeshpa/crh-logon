/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.traileradmin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatTxEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Form of page LKWStammDatenTile,jsp.
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_TrailerAdminForm"
 */
public class TrailerAdminForm extends ActionForm {
	
	// TODO (emoke May 29, 2009):make sure use everywhere this
	public static final String formName = "admin_TrailerAdminForm";
	
	private TrailerEntity trailer;
	private CarrierEntity carrier;
	private String carrierLoginName;
	private Long carrierId;
	private Long trailerId;
	private String inputAction;
	private List trailerList;
	private List carrierList;
	private List trailerTypeList;	
	private List trailerTransportTypeList; //getter modified
	private List historyList;
	private List containerList;
	private ContainerEntity originalContainer;
	

	private String inputContainerSelect;
	private String inputTrailerId;
	private String inputTrailerName;
	private String inputPlateNr;
	private Long inputTrailerType;
	private Long inputTrailerTransportType;
	private String inputVolumeM3;
	private String inputMaxMeasure;
	private Boolean inputStatus;
	private Boolean inputInternalFlag;
	private String inputTaraMeasure;
	private String inputComment;
	private Boolean inputBranding;
	private Boolean inputDustfilter;
	private String inputAxisNumber;
	private String inputManufacturer;
	private Boolean inputPdaFlag;
	private String inputSapVehicleNo;
	
	private String inputMaxNetWeightChamber1T;
	private String inputMaxNetWeightChamber2T;
	private String inputNrChambers;
	
	private Long characteristic;
    
	private Double []lastPosition;
	
	private RfidobjectTypeBsEntity trailerRefDataBsEntity;
	
	private boolean showOnlyActiveTrailers;
	private boolean firstTimeShowOnlyActiveTrailers;
	
   

	private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TrailerAdminForm.class);

    /**
     * Default constructor.
     */
    public TrailerAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TrailerAdminForm");
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

	public TrailerEntity getTrailer() {
		return trailer;
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



	public void setTrailer(TrailerEntity trailer) {
		this.trailer = trailer;
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



	public Long getTrailerId() {
		return trailerId;
	}



	public void setTrailerId(Long trailerId) {
		this.trailerId = trailerId;
	}



	public String getInputAction() {
		return inputAction;
	}



	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}



	public List getTrailerList() {
		return trailerList;
	}



	public void setTrailerList(List trailerList) {
		this.trailerList = trailerList;
	}



	public List getCarrierList() {
		return carrierList;
	}



	public void setCarrierList(List carrierList) {
		this.carrierList = carrierList;
	}



	public List getTrailerTypeList() {
		return trailerTypeList;
	}



	public void setTrailerTypeList(List trailerTypeList) {
		this.trailerTypeList = trailerTypeList;
	}

	public String getInputTrailerId() {
		return inputTrailerId;
	}



	public void setInputTrailerId(String inputTrailerId) {
		this.inputTrailerId = inputTrailerId;
	}



	public String getInputTrailerName() {
		return inputTrailerName;
	}



	public void setInputTrailerName(String inputTrailerName) {
		if (!StringUtils.empty(inputTrailerName) ) {			
			this.inputTrailerName = inputTrailerName;
		}
		else{
			int newestTrailerNumber = getTrailerList() != null ? getTrailerList().size()+1 : 1;
			this.inputTrailerName = "TR " +  carrierLoginName + " " + newestTrailerNumber;
		}
	}



	public String getInputPlateNr() {
		return inputPlateNr;
	}



	public void setInputPlateNr(String inputPlateNr) {
		this.inputPlateNr = inputPlateNr;
	}



	public String getInputVolumeM3() {
		return inputVolumeM3;
	}



	public void setInputVolumeM3(String inputVolumeM3) {
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

	
	
	public void setInputInternalFlag(Boolean inputInternalFlag) {
		this.inputInternalFlag = inputInternalFlag;
	}
	
	

	public String getInputTaraMeasure() {
		return inputTaraMeasure;
	}



	public void setInputTaraMeasure(String inputTaraMeasure) {
		if (inputTaraMeasure != null){		
			inputTaraMeasure = inputTaraMeasure.replaceAll(",",".");
		}
		this.inputTaraMeasure = inputTaraMeasure;
	}


	public String getInputComment() {
		return inputComment;
	}



	public void setInputComment(String inputComment) {
		this.inputComment = inputComment;
	}



	public Boolean getInputBranding() {
		return inputBranding;
	}



	public void setInputBranding(Boolean inputBranding) {
		this.inputBranding = inputBranding;
	}



	public Boolean getInputDustfilter() {
		return inputDustfilter;
	}



	public void setInputDustfilter(Boolean inputDustfilter) {
		this.inputDustfilter = inputDustfilter;
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



	public String getInputSapVehicleNo() {
		return inputSapVehicleNo;
	}


	public void setInputSapVehicleNo(String inputSapVehicleNo) {
		this.inputSapVehicleNo = inputSapVehicleNo;
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
			this.historyList=new ArrayList();
			this.originalContainer = null;
			this.trailer = null;
			this.carrier = null;
			this.carrierLoginName = null;
			this.carrierId = null;
			this.trailerId = null;
			this.inputAction = "";
			this.trailerList = new ArrayList();
			this.carrierList = new ArrayList();
			this.containerList = new ArrayList();
			this.trailerTypeList = new ArrayList();			
			this.trailerTransportTypeList = new ArrayList();
			this.inputTrailerId = "";
			this.inputContainerSelect = "";
			this.inputTrailerName = "";
			this.inputPlateNr = "";
			this.inputTrailerType = null;
			this.inputTrailerTransportType = Constant.TRAILER_TKATEGORIE_BULK_POWDER;
			this.inputVolumeM3 = "";
			this.inputStatus = false;
			this.inputInternalFlag = false;
			this.inputTaraMeasure = "";
			this.inputComment = "";
			this.inputBranding = false;
			this.inputDustfilter = false;
			this.inputAxisNumber = "";
			this.inputManufacturer = "";			
			this.inputPdaFlag = false;
			this.historyList = null;
			this.lastPosition = new Double [2];
			this.inputNrChambers = "";
			this.inputMaxNetWeightChamber1T = "0";
			this.inputMaxNetWeightChamber1T = "0";
			this.inputMaxMeasure = "0";
			this.inputSapVehicleNo = "";
			this.characteristic = null;
			this.firstTimeShowOnlyActiveTrailers = true;
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
			if (request.getParameter("AjaxCall") == null) {
				setInputBranding(false);
				// do not reset the 'active flag' in case it is disabled, otherwise the checked state will disappear on a screen reload
				if (!isInputStatusDisabled()) {
					setInputStatus(false);
				}
				// the 'internal flag' is always disabled, do not reset it, otherwise the checked state will disappear on a screen reload
				// setInputInternalFlag(false);

				// avoid setting the "show only active trucks" flag to false for the first time
				setShowOnlyActiveTrailers(this.firstTimeShowOnlyActiveTrailers);
				this.firstTimeShowOnlyActiveTrailers = false;
			}
			
			String action = request.getParameter("inputAction");
			if (action != null && action.equals("selectCarrier")) {
				setShowOnlyActiveTrailers(true);
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
     * Resets the values of the trailer information input fields.
     */
	public void resetTrailerData( TrailerEntity trailer ) {

		this.trailer = trailer;
		if (trailer != null ) {			
			this.trailerId = trailer.getTrailerId();
		}
		else{
			this.trailerId = null;
		}
		this.historyList=new ArrayList();
		this.originalContainer = null;
		this.inputTrailerId = "";
		this.inputContainerSelect = "";
		setInputTrailerName(null);
		this.containerList = new ArrayList();
		this.inputPlateNr = "";
		this.inputTrailerTransportType = Constant.TRAILER_TKATEGORIE_BULK_POWDER;
		this.inputTrailerType = Constant.TRAILER_TYPE_HORIZONTAL;
		this.inputVolumeM3 = "";
		this.inputStatus = false;
		this.inputInternalFlag = false;
		this.inputTaraMeasure = "";
		this.inputComment = "";
		this.inputMaxMeasure = "0";
		this.inputBranding = false;
		this.inputDustfilter = false;
		this.inputAxisNumber = "";
		this.inputManufacturer = "";
		this.inputPdaFlag = false;
		this.lastPosition = new Double [2];
		this.inputNrChambers =  "0";
		this.inputMaxNetWeightChamber1T =  "0";
		this.inputMaxNetWeightChamber2T =  "0";
		this.inputSapVehicleNo = "";
		this.characteristic = null;
		
	}
	
    /**
     * Override struts method.
     * Perform form-specific validation of input parameters.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			// TODO (emoke Mar 20, 2009):it is not even validated here, since validation is turned off, but may change
	        ActionErrors errors = super.validate(mapping, request);
			
	        if (errors == null) {
	        	errors = new ActionErrors();
	        }
			String plateNo = getInputPlateNr();
			if (StringUtils.empty(plateNo)){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"traileradmin.errors.empty.plateno"));
			}
			
			
			BigDecimal taraMeasure = new BigDecimal("0");
			try{					
				taraMeasure = new BigDecimal(getInputTaraMeasure());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"traileradmin.errors.tarameasure"));
			}
			
			String trailerName = getInputTrailerName();
			if (StringUtils.empty(trailerName)){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"traileradmin.errors.empty.trailerid"));
			}		
			BigDecimal volume =  new BigDecimal("0");
			try{					
				volume = new BigDecimal(getInputVolumeM3());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"traileradmin.errors.volumen"));
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


	public Long getInputTrailerType() {
		return inputTrailerType;
	}


	public void setInputTrailerType(Long inputTrailerType) {
		this.inputTrailerType = inputTrailerType;
	}


	public List getHistoryList() {
		return historyList;
	}


	public void setHistoryList(List historyList) {
		this.historyList = historyList;
	}

	/**
	 * Temporairly blocked container type for planning track.
	 * @return
	 */
	public List getTrailerTransportTypeList() {
		
		if (this.characteristic == Constant.VEHICLE_CHARACTERISTIC_PLANNING) {
			List modifiedTransportTypeList = new ArrayList(2);
			
			for (Iterator iterator = trailerTransportTypeList.iterator(); iterator.hasNext();) {
				TrailerTrspCatTxEntity transportCat = (TrailerTrspCatTxEntity) iterator.next();
				TrailerTrspCatBsEntity trailerTrspCatBs = transportCat.getTrailerTrspCatTxPK().getTrailerTrspCatBs();
				if (trailerTrspCatBs.getTrailerTrspCatCd() != Constant.TRAILER_TKATEGORIE_CONTAINER) {
					modifiedTransportTypeList.add(transportCat);
				}
			}
			return modifiedTransportTypeList;
		}
		else {
			return trailerTransportTypeList;
		}
	}


	public void setTrailerTransportTypeList(List trailerTransportTypeList) {
		this.trailerTransportTypeList = trailerTransportTypeList;
	}


	public Long getInputTrailerTransportType() {
		return inputTrailerTransportType;
	}


	public void setInputTrailerTransportType(Long inputTrailerTransportType) {
		this.inputTrailerTransportType = inputTrailerTransportType;
	}

	public Double[] getLastPosition() {
		return lastPosition;
	}


	public void setLastPosition(Double[] lastPosition) {
		this.lastPosition = lastPosition;
	}


	public RfidobjectTypeBsEntity getTrailerRefDataBsEntity() {
		return trailerRefDataBsEntity;
	}


	public void setTrailerRefDataBsEntity(RfidobjectTypeBsEntity trailerRefDataBsEntity) {
		this.trailerRefDataBsEntity = trailerRefDataBsEntity;
	}


	public String getCarrierLoginName() {
		return carrierLoginName;
	}


	public void setCarrierLoginName(String carrierLoginName) {
		this.carrierLoginName = carrierLoginName;
	}


	public String getInputMaxNetWeightChamber1T() {
		return inputMaxNetWeightChamber1T;
	}


	public void setInputMaxNetWeightChamber1T(String inputMaxNetWeightChamber1T) {
		if (inputMaxNetWeightChamber1T != null){		
			inputMaxNetWeightChamber1T = inputMaxNetWeightChamber1T.replaceAll(",",".");
		}
		this.inputMaxNetWeightChamber1T = inputMaxNetWeightChamber1T;
	}


	public String getInputMaxNetWeightChamber2T() {
		return inputMaxNetWeightChamber2T;
	}


	public void setInputMaxNetWeightChamber2T(String inputMaxNetWeightChamber2T) {
		if (inputMaxNetWeightChamber2T != null){		
			inputMaxNetWeightChamber2T = inputMaxNetWeightChamber2T.replaceAll(",",".");
		}		
		this.inputMaxNetWeightChamber2T = inputMaxNetWeightChamber2T;
	}


	public String getInputNrChambers() {
		return inputNrChambers;
	}


	public void setInputNrChambers(String inputNrChambers) {
		this.inputNrChambers = inputNrChambers;
	}


	public ContainerEntity getOriginalContainer() {
		return originalContainer;
	}


	public void setOriginalContainer(ContainerEntity originalContainer) {
		this.originalContainer = originalContainer;
	}


	public String getInputContainerSelect() {
		return inputContainerSelect;
	}


	public void setInputContainerSelect(String inputContainerSelect) {
		this.inputContainerSelect = inputContainerSelect;
	}


	public List getContainerList() {
		return containerList;
	}


	public void setContainerList(List containerList) {
		this.containerList = containerList;
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

	public Long getCharacteristic() {
		return characteristic;
	}

	public void setCharacteristic(Long characteristics) {
		this.characteristic = characteristics;
	}
	
	/**
	 * Returns true, if the user wants to see only the active trailers
	 * @return true, if the user wants to see only the active trailers
	 */
	public boolean isShowOnlyActiveTrailers() {
		return showOnlyActiveTrailers;
	}

	/**
	 * Sets if the user only wants to see the active trailers.
	 * @param showOnlyActiveTrailers if true, only the active trailers should be showed to the user
	 */
	public void setShowOnlyActiveTrailers(boolean showOnlyActiveTrailers) {
		this.showOnlyActiveTrailers = showOnlyActiveTrailers;
	}

}

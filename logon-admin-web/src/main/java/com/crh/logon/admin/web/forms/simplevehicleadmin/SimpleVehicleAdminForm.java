/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.simplevehicleadmin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeBsEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Form of page SimpleVehicleStammDatenTile,jsp.
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_SimpleVehicleAdminForm"
 */
public class SimpleVehicleAdminForm extends ActionForm {
	
	public static final String formName = "admin_SimpleVehicleAdminForm";
	
	private SimpleVehicleEntity simpleVehicle;
	private SimpleVehicleTypeBsEntity simpleVehicleType;
	private CarrierEntity carrier;
	private String carrierLoginName;
	private Long carrierId;
	private Long simpleVehicleId;
	private String inputAction;
	private List simpleVehicleList;
	private List carrierList;
	private Map simpleVehicleTypeMap;
	private List simpleVehicleTkategorieList;
		
	private String inputSimpleVehicleId;
	private String inputSimpleVehicleTypeCd;
	private String inputVehicleName;
	private String inputVehicleRegNo;
	private String inputVolumeM3;
	private Boolean inputStatus;
	private Boolean inputInternal;
	private String inputCompanycodeCd;
	private String inputTaraMeasure;
	private String inputMaxMeasure;
	private String inputComment;
	private String inputHomeBaseLat;
	private String inputHomeBaseLon;
	private String inputBranding;
	private Long inputDispatchingCarrierId;
	private String inputSapVehicleNo;
	private String inputSapVehicleTypeCd;
	private String inputNrChambers;
	private String inputUnitCount;
		
	private boolean simpleVehicleLoaded;
	private boolean simpleVehicleAssigned;
	private boolean simpleVehicleInSession;
	
	private Double []lastPosition;
    private static final long serialVersionUID = -8910104051337644556L;
        
    /** This array is used to create the dropdown on the UI. */
    private long[] simpleVehiclePriorities;
    private long simpleVehiclePriority;
    
    private long[] validUnitCounts;
    private long[] validNrChambers;
    
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SimpleVehicleAdminForm.class);

    /**
     * Default constructor.
     */
    public SimpleVehicleAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SimpleVehicleAdminForm");
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

	public SimpleVehicleEntity getSimpleVehicle() {
		return simpleVehicle;
	}

	public void setSimpleVehicle(SimpleVehicleEntity simpleVehicle) {
		this.simpleVehicle = simpleVehicle;
	}
	
	public SimpleVehicleTypeBsEntity getSimpleVehicleType() {
		return simpleVehicleType;
	}

	public void setSimpleVehicleType(SimpleVehicleTypeBsEntity simpleVehicleType) {
		this.simpleVehicleType = simpleVehicleType;
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



	public Long getSimpleVehicleId() {
		return simpleVehicleId;
	}



	public void setSimpleVehicleId(Long simpleVehicleId) {
		this.simpleVehicleId = simpleVehicleId;
	}



	public String getInputAction() {
		return inputAction;
	}



	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}



	public List getSimpleVehicleList() {
		return simpleVehicleList;
	}



	public void setSimpleVehicleList(List simpleVehicleList) {
		this.simpleVehicleList = simpleVehicleList;
	}



	public List getCarrierList() {
		return carrierList;
	}



	public void setCarrierList(List carrierList) {
		this.carrierList = carrierList;
	}


	public String getInputSimpleVehicleId() {
		return inputSimpleVehicleId;
	}



	public void setInputSimpleVehicleId(String inputSimpleVehicleId) {
		this.inputSimpleVehicleId = inputSimpleVehicleId;
	}


	public String getInputSimpleVehicleTypeCd() {
		return inputSimpleVehicleTypeCd;
	}

	public void setInputSimpleVehicleTypeCd(String inputSimpleVehicleTypeCd) {
		this.inputSimpleVehicleTypeCd = inputSimpleVehicleTypeCd;
	}

	public String getInputVehicleName() {
		return inputVehicleName;
	}

	public void setInputVehicleName(String inputVehicleName) {
		if (!StringUtils.empty(inputVehicleName) ) {			
			this.inputVehicleName = inputVehicleName;
		}
		else{
			int newestSimpleVehicleNumber = getSimpleVehicleList() != null ? getSimpleVehicleList().size()+1 : 1;
			this.inputVehicleName = this.carrierLoginName + " " + newestSimpleVehicleNumber;
		}
	}

	public String getInputVehicleRegNo() {
		return inputVehicleRegNo;
	}



	public void setInputVehicleRegNo(String inputVehicleRegNo) {
		this.inputVehicleRegNo = inputVehicleRegNo;
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

	

	public Boolean getInputInternal() {
		return inputInternal;
	}

	public void setInputInternal(Boolean inputInternal) {
		this.inputInternal = inputInternal;
	}

	public String getInputCompanycodeCd() {
		return inputCompanycodeCd;
	}

	public void setInputCompanycodeCd(String inputCompanycodeCd) {
		this.inputCompanycodeCd = inputCompanycodeCd;
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
			this.simpleVehicle = null;
			this.simpleVehicleType = null;
			this.carrier = null;
			this.carrierId = null;
			this.carrierLoginName = null;
			this.simpleVehicleId = null;
			this.inputAction = "";
			this.simpleVehicleList = new ArrayList();
			this.carrierList = new ArrayList();
			this.simpleVehicleTypeMap = new TreeMap();
			this.inputSimpleVehicleId = "";
			this.inputSimpleVehicleTypeCd = "";
			this.inputVehicleName = "";
			this.inputVehicleRegNo = "";
			this.inputVolumeM3 = "";
			this.inputStatus = false;
			this.inputInternal = false;
			this.inputCompanycodeCd = "";
			this.inputTaraMeasure = "";
			this.inputMaxMeasure = "";
			this.inputComment = "";
			this.inputHomeBaseLat = "";
			this.inputHomeBaseLon = "";
			this.inputBranding = null;
			this.lastPosition = new Double [2];
			this.simpleVehicleLoaded = false;
			this.simpleVehicleAssigned = false;
			this.simpleVehicleInSession = false;
			this.inputUnitCount = "";
			this.inputNrChambers = "";
			this.inputDispatchingCarrierId = null;
			this.inputSapVehicleNo = "";
			this.inputSapVehicleTypeCd = "";
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
     * This calls must only be called if the whole form is submitted. Otherwise they should not be reset, since they may be
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
			if (request.getParameter("ajaxCall") == null){
				setInputStatus(false);						
				setInputInternal(false);
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
     * Resets the values of the simpleVehicle information input fields.
     */
	public void resetSimpleVehicleData( SimpleVehicleEntity simpleVehicle ) {
		resetMainDataPanel(simpleVehicle);
	}

	/**
	 * 
	 */
	public void resetMainDataPanel(SimpleVehicleEntity simpleVehicle) {
		
		this.simpleVehicle = simpleVehicle;
		if (simpleVehicle != null ) {			
			this.simpleVehicleId = simpleVehicle.getSimpleVehicleId();
		}
		else{
			this.simpleVehicleId = null;
		}
		this.simpleVehicleType = null;
		this.simpleVehiclePriority = Constant.TRUCK_PRIORITY_TRUCK_INACTIVE;
		this.inputSimpleVehicleId = "";
		setInputVehicleName(null);
		this.inputSimpleVehicleTypeCd = null;
		this.inputVehicleRegNo = "";
		this.inputVolumeM3 = "";
		this.inputStatus = false;
		this.inputInternal = false;
		this.inputCompanycodeCd = "";
		this.inputTaraMeasure = "";
		this.inputMaxMeasure = "";
		this.inputComment = "";
		this.inputHomeBaseLat = "";
		this.inputHomeBaseLon = "";
		this.inputBranding = null;
		this.lastPosition = new Double [2];
		this.inputUnitCount = "0";
		this.inputNrChambers = "0";
		this.simpleVehicleLoaded = false;
		this.simpleVehicleAssigned = false;
		this.simpleVehicleInSession = false;
		this.inputDispatchingCarrierId = null;
		this.inputSapVehicleNo = "";
		this.inputSapVehicleTypeCd = "";
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
			ActionErrors errors = super.validate(mapping, request);
			
			if (errors == null) {
				errors = new ActionErrors();
			}
			
			// validate max measure (number format)
			try{
				new BigDecimal(getInputMaxMeasure());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"simplevehicleadmin.errors.maxmeasure"));
			}
			
			// validate vehicle reg no (mandatory)
			String vehicleRegNo = getInputVehicleRegNo();
			if (StringUtils.empty(vehicleRegNo)){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"simplevehicleadmin.errors.empty.vehicleRegNo"));
			}
			
			// validate tare measure (number format)
			try{
				new BigDecimal(getInputTaraMeasure());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"simplevehicleadmin.errors.tarameasure"));
			}
			
			// validate vehicle name (mandatory)
			String vehicleName = getInputVehicleName();
			if (StringUtils.empty(vehicleName)){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"simplevehicleadmin.errors.empty.simpleVehicleid"));
			}
			
			// validate volume m3 (number format)
			try{
				new BigDecimal(getInputVolumeM3());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"simplevehicleadmin.errors.volume"));
			}
			
			// validate company code (conditionally mandatory)
			String companycodeCd = getInputCompanycodeCd();
			if (StringUtils.empty(companycodeCd)) {
				if (getInputInternal()) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"simplevehicleadmin.errors.empty.companycode"));
				}
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

	public List getSimpleVehicleTkategorieList() {
		return simpleVehicleTkategorieList;
	}


	public void setSimpleVehicleTkategorieList(List simpleVehicleTkategorieList) {
		this.simpleVehicleTkategorieList = simpleVehicleTkategorieList;
	}

	public boolean isSimpleVehicleLoaded() {
		return simpleVehicleLoaded;
	}


	public void setSimpleVehicleLoaded(boolean simpleVehicleLoaded) {
		this.simpleVehicleLoaded = simpleVehicleLoaded;
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

	public String getCarrierLoginName() {
		return carrierLoginName;
	}


	public void setCarrierLoginName(String carrierLoginName) {
		this.carrierLoginName = carrierLoginName;
	}

	public String getInputUnitCount() {
		return inputUnitCount;
	}


	public void setInputUnitCount(String inputUnitCount) {
		this.inputUnitCount = inputUnitCount;
	}

	public String getInputNrChambers() {
		return inputNrChambers;
	}

	public void setInputNrChambers(String inputNrChambers) {
		this.inputNrChambers = inputNrChambers;
	}

	public Map getSimpleVehicleTypeMap() {
		return simpleVehicleTypeMap;
	}


	public void setSimpleVehicleTypeMap(Map simpleVehicleTypeMap) {
		this.simpleVehicleTypeMap = simpleVehicleTypeMap;
	}


	public boolean isSimpleVehicleAssigned() {
		return simpleVehicleAssigned;
	}


	public void setSimpleVehicleAssigned(boolean simpleVehicleAssigned) {
		this.simpleVehicleAssigned = simpleVehicleAssigned;
	}


	public boolean isSimpleVehicleInSession() {
		return simpleVehicleInSession;
	}


	public void setSimpleVehicleInSession(boolean simpleVehicleInSession) {
		this.simpleVehicleInSession = simpleVehicleInSession;
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


	public String getInputSapVehicleTypeCd() {
		return inputSapVehicleTypeCd;
	}

	public void setInputSapVehicleTypeCd(String inputSapVehicleTypeCd) {
		this.inputSapVehicleTypeCd = inputSapVehicleTypeCd;
	}

	public long getSimpleVehiclePriority() {
		return simpleVehiclePriority;
	}

	public void setSimpleVehiclePriority(long simpleVehiclePriority) {
		this.simpleVehiclePriority = simpleVehiclePriority;
	}


	public long[] getSimpleVehiclePriorities() {
		return simpleVehiclePriorities;
	}


	public void setSimpleVehiclePriorities(long[] simpleVehiclePriorities) {
		this.simpleVehiclePriorities = simpleVehiclePriorities;
	}

	public long[] getValidUnitCounts() {
		return validUnitCounts;
	}

	public void setValidUnitCounts(long[] validUnitCounts) {
		this.validUnitCounts = validUnitCounts;
	}

	public long[] getValidNrChambers() {
		return validNrChambers;
	}

	public void setValidNrChambers(long[] validNrChambers) {
		this.validNrChambers = validNrChambers;
	}
}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.siloadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.business.services.interfaces.SiloAdminService;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * Form of page BaustellensiloTile,jsp.
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_SiloAdminForm"
 */
public class SiloAdminForm extends ActionForm {
	
	// TODO (emoke May 29, 2009):make sure use everywhere this
	public static final String formName = "admin_SiloAdminForm";
	
	private ShiptoSiloEntity silo;
	private ShiptoEntity shipTo;

	private Long siloId;
	private String inputAction;
	private List siloList;
	private List siloTypeList;
	//private List siloTkategorieList;
	private String materialTxt;
	
	private String inputSiloId;
	private String inputSiloName;
	//private String inputPlateNr;
	private Long inputSiloType;
	private Long inputShiptoId;
	
	private String inputMaxVolumeM3;
	private Boolean inputStatus;
	//private String inputTaraMeasure;
	private String inputMaxVolumeT;
	private String inputComment;
	private Long inputTkategorie;
	private String inputUnloadingTime;	
	private Double []lastPosition;
	
	RfidobjectTypeBsEntity siloRefDataBsEntity;
	
	private String shiptoSearchString;
	
    private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SiloAdminForm.class);

    /**
     * Default constructor.
     */
    public SiloAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SiloAdminForm");
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
			this.materialTxt = "";
			this.silo = null;
			this.shipTo = null;
			this.siloId = null;
			this.inputAction = "";
			this.siloList = new ArrayList();
			this.siloTypeList = new ArrayList();
			this.inputSiloId = "";
			this.inputShiptoId = null;
			this.inputSiloName = "";
			//this.inputPlateNr = "";
			this.inputSiloType = null;
			this.inputMaxVolumeM3 = "";
			this.inputStatus = false;
			//this.inputTaraMeasure = "";
			this.inputMaxVolumeT = "";
			this.inputUnloadingTime = "";
			this.inputComment = "";
			this.lastPosition = new Double [2];
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
			setInputStatus(false);
			setInputUnloadingTime(null);
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
     * Resets the values of the silo information input fields.
     */
	public void resetSiloData( ShiptoSiloEntity silo ) {
		this.silo = silo;
		if (silo != null ) {			
			this.siloId = silo.getShiptoSiloId();
		}
		else{
			this.siloId = null;
		}
		this.materialTxt = "";
		this.shipTo = null;
		this.inputSiloId = "";
		this.inputShiptoId = null;
		setInputSiloName(null);
		//this.inputPlateNr = "";
		this.inputSiloType = null;
		this.inputTkategorie = null;
		this.inputMaxVolumeM3 = "";
		this.inputStatus = false;
//		this.inputTaraMeasure = "";
		this.inputMaxVolumeT = "";
		this.inputUnloadingTime = "";
		this.inputComment = "";
		this.lastPosition = new Double [2];
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
			
			int maxMeasure = 0;
			try{					
				maxMeasure = Integer.parseInt(getInputMaxVolumeT());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"siloadmin.errors.maxmeasure"));
			}
			
			/*String plateNo = getInputPlateNr();
			if (StringUtils.empty(plateNo)){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"siloadmin.errors.empty.plateno"));
			}*/
			
			
			/*int taraMeasure = 0;
			try{					
				taraMeasure = Integer.parseInt(getInputTaraMeasure());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"siloadmin.errors.tarameasure"));
			}*/
			
			String siloName = getInputSiloName();
			if (StringUtils.empty(siloName)){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"siloadmin.errors.empty.siloid"));
			}		
			int volume = 0;
			try{					
				volume = Integer.valueOf(getInputMaxVolumeM3());
			}
			catch (NumberFormatException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"siloadmin.errors.volumen"));
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


	public ShiptoSiloEntity getSilo() {
		return silo;
	}


	public void setSilo(ShiptoSiloEntity silo) {
		this.silo = silo;
	}


	public Long getSiloId() {
		return siloId;
	}


	public void setSiloId(Long siloId) {
		this.siloId = siloId;
	}


	public String getInputAction() {
		return inputAction;
	}


	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}


	public List getSiloList() {
		return siloList;
	}


	public void setSiloList(List siloList) {
		this.siloList = siloList;
	}


	public List getSiloTypeList() {
		return siloTypeList;
	}


	public void setSiloTypeList(List siloTypeList) {
		this.siloTypeList = siloTypeList;
	}


	public String getInputSiloId() {
		return inputSiloId;
	}


	public void setInputSiloId(String inputSiloId) {
		this.inputSiloId = inputSiloId;
	}


	public String getInputSiloName() {
		return inputSiloName;
	}


	public void setInputSiloName(String inputSiloName) {
		this.inputSiloName = inputSiloName;
	}


	public Long getInputSiloType() {
		return inputSiloType;
	}


	public void setInputSiloType(Long inputSiloType) {
		this.inputSiloType = inputSiloType;
	}



	public String getInputComment() {
		return inputComment;
	}


	public void setInputComment(String inputComment) {
		this.inputComment = inputComment;
	}


	public Long getInputTkategorie() {
		return inputTkategorie;
	}


	public void setInputTkategorie(Long inputTkategorie) {
		this.inputTkategorie = inputTkategorie;
	}


	public Double[] getLastPosition() {
		return lastPosition;
	}


	public void setLastPosition(Double[] lastPosition) {
		this.lastPosition = lastPosition;
	}


	public Boolean getInputStatus() {
		return inputStatus;
	}


	public void setInputStatus(Boolean inputStatus) {
		this.inputStatus = inputStatus;
	}


	public String getInputMaxVolumeM3() {
		return inputMaxVolumeM3;
	}


	public void setInputMaxVolumeM3(String inputMaxVolumeM3) {
		this.inputMaxVolumeM3 = inputMaxVolumeM3;
	}


	public String getInputMaxVolumeT() {
		return inputMaxVolumeT;
	}


	public void setInputMaxVolumeT(String inputMaxVolumeT) {
		this.inputMaxVolumeT = inputMaxVolumeT;
	}


	public String getInputUnloadingTime() {
		return inputUnloadingTime;
	}


	public void setInputUnloadingTime(String inputUnloadingTime) {
		this.inputUnloadingTime = inputUnloadingTime;
	}


	public Long getInputShiptoId() {
		return inputShiptoId;
	}


	public void setInputShiptoId(Long inputShiptoId) {
		this.inputShiptoId = inputShiptoId;
	}


	public ShiptoEntity getShipTo() {
		return shipTo;
	}


	public void setShipTo(ShiptoEntity shipTo) {
		this.shipTo = shipTo;
	}


	public String getShiptoSearchString() {
		return shiptoSearchString;
	}


	public void setShiptoSearchString(String shiptoSearchString) {
		this.shiptoSearchString = shiptoSearchString;
	}

	/**
	 * Resets the input fields of the form, and removes the rf object also from the session.
	 * @param request
	 * @param newSilo
	 */
	public void resetSiloAndSession(HttpServletRequest request, ShiptoSiloEntity newSilo) {
		AdminRFIDPopupForm.resetForm(request.getSession());
		resetSiloData(newSilo);		
		RfidobjectEntity rfObj = null; 
		if (newSilo.getShiptoSiloId() != null){
			rfObj = newSilo.getRfidobject();
		}
		if (rfObj == null) {
			rfObj = new RfidobjectEntity();
			rfObj.setRfidobjectType(getSiloRefDataBsEntity());			
		}
		newSilo.setRfidobject(rfObj);
		AbstractRFIDBean bean = RFIDBean.ShiptoSiloRFIDBean(newSilo);
		AdminRFIDPopupForm.initForm(request.getSession(),bean);
	}
	
	/**
	 * Maps the siloEntity object to the form's fields.
	 * siloEntity fields --> form fields
	 * @param silo
	 * @param service TODO
	 * @param locale TODO
	 */
	public void mapObjectToForm(ShiptoSiloEntity silo, SiloAdminService service, Locale locale) {
		setShipTo(silo.getShipto());
		setSiloId(silo.getShiptoSiloId());
		setInputStatus( silo.getActiveFlag());
		setInputSiloType(silo.getSiloType() != null ? silo.getSiloType().getSiloTypeCd() : null);
		
		if (silo.getSapMaterial() != null) {			
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			SapMaterialDescriptionEntity descr = serviceLocator.getDispokarteService().fetchSapMaterialDescription(silo.getSapMaterial().getSapMaterialId(), locale.getLanguage());
			if (descr != null ){
				String materialTxt = descr.getTxt();
				setMaterialTxt(materialTxt);
			}
		}
		setInputComment(silo.getSiloComment());
		//setInputPlateNr(silo.getSiloPlateNo());
		//setInputTaraMeasure(silo.getTaraWeightT() != null ? silo.getTaraWeightT().toString() : "");
		setInputSiloName(silo.getSiloName());
		setInputMaxVolumeM3((silo.getMaxVolumeM3()!= null) ? silo.getMaxVolumeM3().toString() : "");
		setInputMaxVolumeT((silo.getMaxVolumeTons()!= null) ? silo.getMaxVolumeTons().toString() : "");
		setInputUnloadingTime((silo.getUnloadingTime()!= null) ? silo.getUnloadingTime().toString() : "");
	}


	public RfidobjectTypeBsEntity getSiloRefDataBsEntity() {
		return siloRefDataBsEntity;
	}


	public void setSiloRefDataBsEntity(RfidobjectTypeBsEntity siloRefDataBsEntity) {
		this.siloRefDataBsEntity = siloRefDataBsEntity;
	}


	public String getMaterialTxt() {
		return materialTxt;
	}


	public void setMaterialTxt(String materialTxt) {
		this.materialTxt = materialTxt;
	}



}

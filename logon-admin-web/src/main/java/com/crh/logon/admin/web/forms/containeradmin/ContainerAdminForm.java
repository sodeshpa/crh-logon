/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.containeradmin;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.containeradmin.ContainerAdminAction;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.refdata.ContainerTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.ContainerTypeTxEntity;
import com.crh.logon.utils.StringUtils;

/**
 * 
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ContainerAdminForm"
 */
public class ContainerAdminForm extends ActionForm {



	public static final String formName = "admin_ContainerAdminForm";

	private static final long serialVersionUID = -8910104051337644556L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerAdminForm.class);

	private String inputAction;
	private Long carrierId;
	private ContainerEntity container;
	private Long containerId;
	private Map carrierMap;
	private Map containerMap;
	private Map containerTrspCatMap;
	private Map containerTypeMap;
	private List contHistoryList;
	private String carrierName;
	private String carrierStreet;
	private String carrierCity;
	
	//input fields
	private String inputContainerName;
	private String inputContainerNo;
	private boolean inputActive;
	private String inputTareWeight;
	private String inputMaxNetWeight;
	private String inputUicProfile;
	private Long inputContainerTrspCat;
	private Long inputContainerType;
	private String inputLength;
	private String inputWidth;
	private String inputHeight;
	private String inputVolumeM3;
	private String inputManufacturer;
	private long inputBrandingFlag;
	private String inputComment;
	private String inputCoordLat;
	private String inputCoordLon;
	private boolean showOnlyActiveContainers;
	private boolean firstTimeShowOnlyActiveContainers;

	public List getContHistoryList() {
		return contHistoryList;
	}

	public void setContHistoryList(List contHistoryList) {
		this.contHistoryList = contHistoryList;
	}
	
	public long getInputBrandingFlag() {
		return inputBrandingFlag;
	}

	public void setInputBrandingFlag(long inputBrandingFlag) {
		this.inputBrandingFlag = inputBrandingFlag;
	}

	public String getInputCoordLat() {
		return inputCoordLat;
	}

	public void setInputCoordLat(String inputCoordLat) {
		this.inputCoordLat = inputCoordLat;
	}

	public String getInputCoordLon() {
		return inputCoordLon;
	}

	public void setInputCoordLon(String inputCoordLon) {
		this.inputCoordLon = inputCoordLon;
	}

	public Map getContainerMap() {
		return containerMap;
	}

	public void setContainerMap(Map containerMap) {
		this.containerMap = containerMap;
	}

	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	public String getInputLength() {
		return inputLength;
	}

	public void setInputLength(String inputLength) {
		inputLength = replaceComma(inputLength);
		this.inputLength = inputLength;
	}

	public String getInputWidth() {
		return inputWidth;
	}

	public void setInputWidth(String inputWidth) {
		inputWidth = replaceComma(inputWidth);
		this.inputWidth = inputWidth;
	}

	public String getInputHeight() {
		return inputHeight;
	}

	public void setInputHeight(String inputHeight) {
		inputHeight = replaceComma(inputHeight);
		this.inputHeight = inputHeight;
	}

	public String getInputVolumeM3() {
		return inputVolumeM3;
	}

	public void setInputVolumeM3(String inputVolumeM3) {
		this.inputVolumeM3 = inputVolumeM3;
	}

	public String getInputManufacturer() {
		return inputManufacturer;
	}

	public void setInputManufacturer(String inputManufacturer) {
		this.inputManufacturer = inputManufacturer;
	}

	public String getInputComment() {
		return inputComment;
	}

	public void setInputComment(String inputComment) {
		this.inputComment = inputComment;
	}

	public Long getInputContainerTrspCat() {
		return inputContainerTrspCat;
	}

	public void setInputContainerTrspCat(Long inputContainerTrspCat) {
		this.inputContainerTrspCat = inputContainerTrspCat;
	}

	public Long getInputContainerType() {
		return inputContainerType;
	}

	public void setInputContainerType(Long inputContainerType) {
		this.inputContainerType = inputContainerType;
	}

	public Map getContainerTrspCatMap() {
		return containerTrspCatMap;
	}

	public void setContainerTrspCatMap(Map containerTrspCatMap) {
		this.containerTrspCatMap = containerTrspCatMap;
	}

	public Map getContainerTypeMap() {
		return containerTypeMap;
	}

	public void setContainerTypeMap(Map containerTypeMap) {
		this.containerTypeMap = containerTypeMap;
	}

	public String getInputTareWeight() {
		return inputTareWeight;
	}

	public void setInputTareWeight(String inputTareWeight) {
		inputTareWeight = replaceComma(inputTareWeight);
		this.inputTareWeight = inputTareWeight;
	}

	public String getInputUicProfile() {
		return inputUicProfile;
	}

	public void setInputUicProfile(String inputUicProfile) {
		this.inputUicProfile = inputUicProfile;
	}

	public boolean isInputActive() {
		return inputActive;
	}

	public void setInputActive(boolean inputActive) {
		this.inputActive = inputActive;
	}

	public String getInputContainerName() {
		return inputContainerName;
	}

	public void setInputContainerName(String inputContainerName) {
		this.inputContainerName = inputContainerName;
	}

	public String getInputContainerNo() {
		return inputContainerNo;
	}

	public void setInputContainerNo(String inputContainerNo) {
		this.inputContainerNo = inputContainerNo;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getCarrierStreet() {
		return carrierStreet;
	}

	public void setCarrierStreet(String carrierStreet) {
		this.carrierStreet = carrierStreet;
	}

	public String getCarrierCity() {
		return carrierCity;
	}

	public void setCarrierCity(String carrierCity) {
		this.carrierCity = carrierCity;
	}

	public Map getCarrierMap() {
		return carrierMap;
	}

	public void setCarrierMap(Map carrierMap) {
		this.carrierMap = carrierMap;
	}

	public ContainerEntity getContainer() {
		return container;
	}

	public void setContainer(ContainerEntity container) {
		this.container = container;
	}

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

	public Long getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(Long carrierId) {
		this.carrierId = carrierId;
	}

	/**
	 * Default constructor.
	 */
	public ContainerAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ContainerAdminForm");
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
			
			carrierMap = new LinkedHashMap();
			containerMap = new LinkedHashMap();
			containerTrspCatMap = new LinkedHashMap();
			containerTypeMap = new LinkedHashMap();
			contHistoryList = new ArrayList();
			
			carrierId = Constant.INVALID_OBJECT_ID;
			container = null;
			containerId = null;
			
			carrierName = "";
			carrierStreet = "";
			carrierCity = "";
			
			inputAction = "";
			inputContainerName = "";
			inputContainerNo = "";
			inputActive = false;
			inputUicProfile = "";
			inputTareWeight = "0";
			inputMaxNetWeight = "0";
			inputLength = "";
			inputWidth = "";
			inputHeight = "";
			inputVolumeM3 = "";
			inputManufacturer = "";
			inputBrandingFlag = Constant.INVALID_OBJECT_ID;
			inputComment = "";
			inputCoordLat = "";
			inputCoordLon = "";
			inputContainerTrspCat = Constant.INVALID_OBJECT_ID;
			inputContainerType = Constant.INVALID_OBJECT_ID;
			this.firstTimeShowOnlyActiveContainers = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void resetInputFields() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resetInputFields");
		}
		// ]]
		try {
			
			inputContainerName = "";
			inputContainerNo = "";
			inputActive = false;
			inputUicProfile = "";
			inputTareWeight = "";
			inputMaxNetWeight = "0";
			inputLength = "";
			inputWidth = "";
			inputHeight = "";
			inputVolumeM3 = "";
			inputManufacturer = "";
			inputBrandingFlag = Constant.INVALID_OBJECT_ID;
			inputComment = "";
			inputCoordLat = "";
			inputCoordLon = "";
			inputContainerTrspCat = Constant.INVALID_OBJECT_ID;
			inputContainerType = Constant.INVALID_OBJECT_ID;
			
			contHistoryList = new ArrayList();
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
			inputActive = false;
			// avoid setting the "show only active containers" flag to false for the first time
			setShowOnlyActiveContainers(this.firstTimeShowOnlyActiveContainers);
			this.firstTimeShowOnlyActiveContainers = false;
			
			String action = request.getParameter("inputAction");
			if (action != null && action.equals("selectCarrier")) {
				setShowOnlyActiveContainers(true);
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
			
			if (!inputAction.equals(ContainerAdminAction.FORWARD_SAVE_NEW) && containerId != null && getContainerMap() != null && getContainerMap().get(containerId) != null) {
				container = (ContainerEntity) getContainerMap().get(containerId);
			}
			
			if ((inputAction.equals(ContainerAdminAction.FORWARD_SAVE))
					|| (inputAction.equals(ContainerAdminAction.FORWARD_SAVE_NEW))) {
				
				if(container == null) {					
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("roadmasterdata.formreseted"));
					return result = errors;
				}
			
				if (container.getCarrier() == null) {
					CarrierEntity carrier = (CarrierEntity) carrierMap.get(carrierId);
					container.setCarrier(carrier);	
				}
				
				try {
					if (StringUtils.empty(inputTareWeight)) {
						container.setTareWeightT(null);
					}
					else {
						BigDecimal taraWeightT = new BigDecimal(inputTareWeight);
						container.setTareWeightT(taraWeightT);
					}
				}
				catch (NumberFormatException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.tara.weight"));
				}
				
				try {
					if (StringUtils.empty(inputMaxNetWeight)) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"contadmin.errors.required.maxnetweight"));
					}
					else {
						BigDecimal maxWeightT = new BigDecimal(inputMaxNetWeight);
						container.setMaxNetWeightT(maxWeightT);
					}
				}
				catch (NumberFormatException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.net.weight"));
				}
			
				if (inputContainerTrspCat == Constant.INVALID_OBJECT_ID) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.required.trsp.cat"));
				}
				else {
					ContainerTrspCatTxEntity trspCat = (ContainerTrspCatTxEntity) containerTrspCatMap.get(inputContainerTrspCat);
					container.setContainerTrspCat(trspCat.getContainerTrspCatTxPK().getContainerTrspCatBs());
				}
			
				if (inputContainerType == Constant.INVALID_OBJECT_ID) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.required.type"));
				}
				else {
					ContainerTypeTxEntity type = (ContainerTypeTxEntity) containerTypeMap.get(inputContainerType);
					container.setContainerType(type.getContainerTypeTxPK().getContainerTypeBs());
				}
			
				try {
					if (StringUtils.empty(inputLength)) {
						container.setLengthFt(null);
					}
					else {
						BigDecimal length = new BigDecimal(inputLength);
						container.setLengthFt(length);
					}
				}
				catch (NumberFormatException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.length"));
				}
			
				try {
					if (StringUtils.empty(inputWidth)) {
						container.setWidthMm(null);
					}
					else {
						BigDecimal witdth = new BigDecimal(inputWidth);
						container.setWidthMm(witdth);
					}
				}
				catch (NumberFormatException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.width"));
				}
			
				try {
					if (StringUtils.empty(inputHeight)) {
						container.setHeightMm(null);
					}
					else {
						BigDecimal height = new BigDecimal(inputHeight);
						container.setHeightMm(height);
					}
				}
				catch (NumberFormatException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.height"));
				}
			
				try {
					if (StringUtils.empty(inputVolumeM3)) {
						container.setVolumeM3(null);
					}
					else {
						BigDecimal volumeM3 = StringUtils.toBigDecimal(inputVolumeM3);
						container.setVolumeM3(volumeM3);
					}
				}
				catch (ParseException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.volume"));
				}
			
				if (StringUtils.empty(inputContainerName)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.required.name"));
				}
				else {
					container.setContainerName(inputContainerName);
				}
				
				//unformat
				inputContainerNo = unformatContainerNo(inputContainerNo, request.getLocale());
				
				if (StringUtils.empty(inputContainerNo)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.required.no"));
				}
				else {
					container.setContainerNo(inputContainerNo);
				}
			
				container.setActiveFlag(inputActive);
				container.setUicProfile(inputUicProfile);
				container.setManufacturer(inputManufacturer);
				container.setContainerComment(inputComment);
			
				try {
					if (StringUtils.empty(inputCoordLat)) {
						container.setHomebaseCoordLat(null);
					}
					else {
						Double coordLat = new Double(inputCoordLat);
						container.setHomebaseCoordLat(coordLat);
					}
				}
				catch (NumberFormatException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.latitude"));
				}
			
				try {
					if (StringUtils.empty(inputCoordLon)) {
						container.setHomebaseCoordLon(null);
					}
					else {
						Double coordLon = new Double(inputCoordLon);
						container.setHomebaseCoordLon(coordLon);
					}
				}
				catch (NumberFormatException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"contadmin.errors.format.longitude"));
				}
			
				if (inputBrandingFlag == Constant.INVALID_OBJECT_ID) {
					container.setBrandingFlag(null);
				}
				else {
					boolean brandingFlag = (inputBrandingFlag == 0) ? false : true;
					container.setBrandingFlag(brandingFlag);
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

	
	/** Unformat UI value to save into db
	 * @param contNo string to format
	 * @return formatted string
	 */
	private String unformatContainerNo(String contNo, Locale locale) {
		// [[
		String result = null;
		Object[][] params = {{"contNo", contNo},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "unformatContainerNo", params);
		}
		// ]]
		try {
			StringBuffer str = new StringBuffer();
			Matcher mat = Pattern.compile("[A-Z0-9]").matcher(contNo.toUpperCase(locale));
			
			while (mat.find()) {
				str.append(mat.group());
			}
			
			return result = str.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void clear() {
		init();
	}

	public String getInputMaxNetWeight() {
		return inputMaxNetWeight;
	}

	public void setInputMaxNetWeight(String inputMaxNetWeight) {
		inputMaxNetWeight = replaceComma(inputMaxNetWeight);
		this.inputMaxNetWeight = inputMaxNetWeight;
	}
	
	/**
	 * @param inputFracionNumber
	 * @return
	 */
	private String replaceComma(String inputFracionNumber) {
		if (inputFracionNumber != null){		
			inputFracionNumber = inputFracionNumber.replaceAll(",",".");
		}
		return inputFracionNumber;
	}
	
	/**
	 * Returns true, if the user wants to see only the active trailers
	 * @return true, if the user wants to see only the active trailers
	 */
	public boolean isShowOnlyActiveContainers() {
		return showOnlyActiveContainers;
	}

	/**
	 * Sets if the user only wants to see the active trailers.
	 * @param showOnlyActiveContainers if true, only the active containers should be showed to the user
	 */
	public void setShowOnlyActiveContainers(boolean showOnlyActiveContainers) {
		this.showOnlyActiveContainers = showOnlyActiveContainers;
	}
	
}

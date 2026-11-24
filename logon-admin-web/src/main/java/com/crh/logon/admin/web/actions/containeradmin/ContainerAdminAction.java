/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.containeradmin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.containeradmin.ContainerAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.ContainerHistoryEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ContainerAdminForm"
 *  path="/containeradmin/ContainerAdmin" scope="session"
 *  validate="true" input="/containeradmin/ContainerAdmin.def"
 *
 * @struts.action-forward name="success" path="/containeradmin/ContainerAdmin.def"
 * @struts.action-forward name="tryagain" path="/containeradmin/ContainerAdminCarrier.def"
 * @struts.action-forward name="reloadtypes" path="/containeradmin/ContainerTypeSelect.def"
 * @struts.action-forward name="createContainerPopup" path="/containeradmin/CreateContainerPopup.def"   
 */
public class ContainerAdminAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerAdminAction.class);
	private static final Pattern CONTAINER_NO_PATTERN = Pattern.compile("^(\\w{0,10})(\\d{3})(\\d{3})(\\d{1})$");
	public static final String FORWARD_SELECT_CARRIER = "selectCarrier";
	public static final String FORWARD_SAVE = "save";
	public static final String FORWARD_SAVE_NEW = "saveNew";
	public static final String FORWARD_RESET = "reset";
	public static final String FORWARD_CREATE_CONTAINER = "createContainerPopup";
    public static final String FORWARD_SHOW_ONLY_ACTIVE_CONTAINERS = "showOnlyActiveContainers";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER);
			
			String forwardName = "success";
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("container", request);
			
			ContainerAdminForm helpForm = (ContainerAdminForm) form;
			String inputAction = helpForm.getInputAction();
			
			if (inputAction.equals(FORWARD_SELECT_CARRIER)) {
				if (logDebugEnabled) {
					LOG.debug("Action: selected carrier!");
				}
				
				if (helpForm.getCarrierId() == Constant.INVALID_OBJECT_ID){
					helpForm.setContainer(null);
					helpForm.setContainerId(null);
					helpForm.setCarrierName("");
					helpForm.setCarrierStreet("");
					helpForm.setCarrierCity("");
				}
				else {
					CarrierEntity carrier = (CarrierEntity) helpForm.getCarrierMap().get(
							helpForm.getCarrierId());
	
					//set vendor attributes
					SapVendorEntity vendor = carrier.getSapVendor();
					helpForm.setCarrierName(StringUtils.nvl(vendor.getName1()));
					helpForm.setCarrierStreet(StringUtils.nvl(vendor.getStreet()) + " "
							+ StringUtils.nvl(vendor.getHouseNumber()));
					helpForm.setCarrierCity(StringUtils.nvl(vendor.getCity()));
	
					//set container map
					List containerList = updateContainerList(helpForm);
	
					if ((containerList != null) && !containerList.isEmpty()) {
						ContainerEntity container = (ContainerEntity) containerList.get(0);
						helpForm.setContainer(container);
						helpForm.setContainerId(container.getContainerId());
						
						resetRfidObject(request, helpForm, container);
						//set input fields
						setInputFields(helpForm, container);
					}
					else {
						helpForm.setContainer(null);
						helpForm.setContainerId(null);
					}
				}
			}
			else if (inputAction.equals(FORWARD_SHOW_ONLY_ACTIVE_CONTAINERS)) {
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER);
				updateContainerList(helpForm);
				forwardName="success";
				return result = mapping.findForward(forwardName);
			}
			else if (inputAction.equals(FORWARD_SAVE)){
				if (logDebugEnabled) {
					LOG.debug("Action: save container!");
				}
				checkPrecisions(request, helpForm);
				
				Map containerNameNoMap = getContainerAdminService().checkContainerUniqueness(
								helpForm.getContainerId(),
								helpForm.getContainer().getContainerName(),
								helpForm.getContainer().getContainerNo());
			
				if (!(Boolean) containerNameNoMap.get(ContainerEntity.ATTR_CONTAINER_NAME)) {
					MessageUtils.addActionError(request, "contadmin.errors.not.unique.name");
				} 
				else if (!(Boolean) containerNameNoMap.get(ContainerEntity.ATTR_CONTAINER_NO)) {
					MessageUtils.addActionError(request,"contadmin.errors.not.unique.no");
				} 
				if (noErrorWarningPresent(request)) {
					ContainerEntity container = getContainerAdminService().updateContainer(helpForm.getContainer());
					if ((container != null) && (container.getContainerId() != null)) {
						MessageUtils.addActionMessage(request,"contadmin.message.success.save");
						helpForm.getContainerMap().remove(container.getContainerId());
						helpForm.getContainerMap().put(container.getContainerId(), container);
						helpForm.setContainer(container);
						helpForm.setInputContainerNo(formatContainerNo(StringUtils.nvl(container.getContainerNo())));
						updateContainerList(helpForm);
					}
					resetRfidObject(request, helpForm, container);
				}
			
			}
			else if (inputAction.equals(FORWARD_SAVE_NEW)){ 
				if (logDebugEnabled) {
					LOG.debug("Action: save NEW container!");
				}
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER_CREATE);
				
				checkPrecisions(request, helpForm);
				
				Map containerNameNoMap = getContainerAdminService().checkContainerUniqueness(
						null,
						helpForm.getContainer().getContainerName(),
						helpForm.getContainer().getContainerNo());
			
				if (!(Boolean) containerNameNoMap.get(ContainerEntity.ATTR_CONTAINER_NAME)) {
					MessageUtils.addActionError(request, "contadmin.errors.not.unique.name");
				} 
				else if (!(Boolean) containerNameNoMap.get(ContainerEntity.ATTR_CONTAINER_NO)) {
					MessageUtils.addActionError(request,"contadmin.errors.not.unique.no");
				} 
				if ( noErrorWarningPresent(request)){
					
					CarrierEntity carrier = (CarrierEntity)helpForm.getCarrierMap().get(helpForm.getCarrierId());
					helpForm.getContainer().setCarrier(carrier);
					helpForm.getContainer().setActiveFlag(true);
					
					RfidobjectEntity rfObj = createNewRfidObject();
					helpForm.getContainer().setRfidobject(rfObj);
					ContainerEntity container = getContainerAdminService().updateContainer(helpForm.getContainer());
					 
					if ((container != null) && (container.getContainerId() != null)) {
						request.setAttribute("reload", "reload");
						MessageUtils.addActionMessage(request, "contadmin.message.success.save");
						setInputFields(helpForm, container);
						helpForm.setContainer(container);
						helpForm.setContainerId(container.getContainerId());
						helpForm.getContainerMap().put(container.getContainerId(), container);
						helpForm.setInputContainerNo(formatContainerNo(StringUtils.nvl(container.getContainerNo())));
						updateContainerList(helpForm);
						resetRfidObject(request, helpForm, container);
					}
				}
			
				forwardName = FORWARD_CREATE_CONTAINER;
			
			}			
			else if (inputAction.equals(FORWARD_RESET)){
					if (logDebugEnabled) {
						LOG.debug("Action: reset or load container!");
					}
					
					ContainerEntity container = getServiceLocator().getContainerAdminService().fetchContainer(helpForm.getContainerId());
					helpForm.setContainer(container);
					helpForm.getContainerMap().put(container.getContainerId(), container);
					
					resetRfidObject(request, helpForm, container);
					//set input fields
					setInputFields(helpForm, container);
				}
			else if (inputAction.equals(FORWARD_CREATE_CONTAINER)){
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER_CREATE);				
				//copy the container - leave the input fields as they are it is an ajax call, only with these fields
				ContainerEntity newContainer = new ContainerEntity();
				ContainerEntity container = (ContainerEntity) helpForm.getContainerMap().get(helpForm.getContainerId());
				if (container != null ) {					
					setInputFields(helpForm, container);								
				}
				helpForm.setInputContainerName("");
				helpForm.setInputContainerNo("");
				helpForm.setInputContainerTrspCat(Constant.INVALID_OBJECT_ID);
				helpForm.setInputContainerType (Constant.INVALID_OBJECT_ID);
				helpForm.setContainer(newContainer);
				forwardName = FORWARD_CREATE_CONTAINER;
			}
			
			//reset
			helpForm.setInputAction("");
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
	 * Sets the container list based on the carrier id and if only the active containers should be displayed
	 * @param helpForm the {@link ContainerAdminForm}
	 * @return {@link List} of {@link ContainerEntity}-s 
	 */
	private List updateContainerList(ContainerAdminForm helpForm) {
		List containerList = getContainerAdminService().fetchContainersForCarrier(helpForm.getCarrierId(), helpForm.isShowOnlyActiveContainers());
		Map contMap = new LinkedHashMap();
		for (Iterator it = containerList.iterator(); it.hasNext();) {
			ContainerEntity cont = (ContainerEntity) it.next();
			contMap.put(cont.getContainerId(), cont);
		}
		helpForm.setContainerMap(contMap);
		return containerList;
	}

	/**
	 * @param request
	 * @param helpForm
	 */
	private void checkPrecisions(HttpServletRequest request, ContainerAdminForm helpForm) {
		boolean weightNeg = false;
		
		try{
			//cannot be left empty!
			BigDecimal taraMeasure = new BigDecimal(helpForm.getInputTareWeight());
			checkFieldMaxValue(request, helpForm.getInputTareWeight(), ContainerEntity.DBSIZE_TARE_WEIGHT_T, ContainerEntity.DBSIZE_PRECISION_TARE_WEIGHT_T , "contadmin.label.tara.weight");
			if (taraMeasure.compareTo(new BigDecimal(0)) < 0) {
				weightNeg = true;
			}
		}
		catch (NumberFormatException e){
			MessageUtils.addActionWarning(request, "traileradmin.errors.tarameasure");
		}
		
		if (StringUtils.notEmpty(helpForm.getInputMaxNetWeight()) ){
			BigDecimal maxMeasure = new BigDecimal(helpForm.getInputMaxNetWeight());
			if (maxMeasure.compareTo(new BigDecimal(0)) < 0) {
				weightNeg = true;
			}
		}
		
		if (weightNeg) {
			MessageUtils.addActionWarning(request, "contadmin.errors.weightnegative");
		}
		
		checkFieldMaxValue(request, helpForm.getInputLength(), ContainerEntity.DBSIZE_LENGTH_FT, ContainerEntity.DBSIZE_PRECISION_LENGTH_FT , "contadmin.label.tara.weight");			
		checkFieldMaxValue(request, helpForm.getInputWidth(), ContainerEntity.DBSIZE_WIDTH_MM, ContainerEntity.DBSIZE_PRECISION_WIDTH_MM , "contadmin.label.width");
		checkFieldMaxValue(request, helpForm.getInputMaxNetWeight(), ContainerEntity.DBSIZE_MAX_NET_WEIGHT_T, ContainerEntity.DBSIZE_PRECISION_MAX_NET_WEIGHT_T , "contadmin.label.net.weight");
		checkFieldMaxValue(request, helpForm.getInputHeight(), ContainerEntity.DBSIZE_HEIGHT_MM, ContainerEntity.DBSIZE_PRECISION_HEIGHT_MM , "contadmin.label.height");
	}

	private void setInputFields(ContainerAdminForm helpForm, ContainerEntity container) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"container", container}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setInputFields", params);
		}
		// ]]
		try {
			
			helpForm.setInputContainerName(StringUtils.nvl(container.getContainerName()));
			helpForm.setInputContainerNo(formatContainerNo(StringUtils.nvl(container.getContainerNo())));
			helpForm.setInputActive(container.getActiveFlag());
			helpForm.setInputUicProfile(StringUtils.nvl(container.getUicProfile()));
			helpForm.setInputContainerTrspCat(container.getContainerTrspCat().getContainerTrspCatCd());
			helpForm.setInputContainerType(container.getContainerType().getContainerTypeCd());
			String tareWeight = (container.getTareWeightT() == null) ? ""
					: container.getTareWeightT().toString();
			helpForm.setInputTareWeight(tareWeight);
			helpForm.setInputTareWeight(tareWeight);
			String maxNetWeight = (container.getMaxNetWeightT().toString());
			helpForm.setInputMaxNetWeight(maxNetWeight);
			String length = (container.getLengthFt() == null) ? ""
					: container.getLengthFt().toString();
			helpForm.setInputLength(length);
			String width = (container.getWidthMm() == null) ? "" : container.getWidthMm().toString();
			helpForm.setInputWidth(width);
			String height = (container.getHeightMm() == null) ? ""
					: container.getHeightMm().toString();
			helpForm.setInputHeight(height);
			String volumeM3 = (container.getVolumeM3() == null) ? ""
					: container.getVolumeM3().toString();
			helpForm.setInputVolumeM3(volumeM3);
			helpForm.setInputManufacturer(StringUtils.nvl(container.getManufacturer()));
			helpForm.setInputComment(StringUtils.nvl(container.getContainerComment()));
			String cordLat = (container.getHomebaseCoordLat() == null) ? "" : container.getHomebaseCoordLat().toString();
			helpForm.setInputCoordLat(cordLat);
			String cordLon = (container.getHomebaseCoordLon() == null) ? "" : container.getHomebaseCoordLon().toString();
			helpForm.setInputCoordLon(cordLon);
		
			if (container.getBrandingFlag() != null) {
				int brandingFlag = (container.getBrandingFlag()) ? 1 : 0;
				helpForm.setInputBrandingFlag(brandingFlag);
			}
			else {
				helpForm.setInputBrandingFlag(-1);
			}
			
			helpForm.setContHistoryList(new ArrayList());
			List histList = getContainerAdminService().fetchContainerHistory(container.getContainerId());
			
			for (Iterator it = histList.iterator(); it.hasNext();) {
				ContainerHistoryEntity history = (ContainerHistoryEntity) it.next();
				
				String histEntry = "";
				
				if (history.getTruck() != null) {
					histEntry = history.getTruck().getTruckName();
				}
				else if (history.getTrailer() != null) {
					histEntry = history.getTrailer().getTrailerName();
				}
				else if (history.getWagon() != null) {
					histEntry = history.getWagon().getWagonNo();
				}

				String dt = getUserTimezone().fmtAsDayMonthYearHourMinute(history.getCtlCreTs());
				helpForm.getContHistoryList().add(histEntry + " | " + dt);
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
	 * @param request
	 * @param helpForm
	 * @param container
	 */
	private void resetRfidObject(HttpServletRequest request, ContainerAdminForm helpForm, ContainerEntity container) {
		AdminRFIDPopupForm.resetForm(request.getSession());
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER);
		
		if (container.getRfidobject() == null) {
			RfidobjectEntity rfObj = createNewRfidObject();
			container.setRfidobject(rfObj);
		}
		
		//try to init, in case the properties are not yet set, catch the exception, saving will prevented anyways at save
		AbstractRFIDBean bean = RFIDBean.ContainerRFIDBean(container);
		AdminRFIDPopupForm.initForm(request.getSession(), bean);

	}

	/**
	 * @return
	 */
	private RfidobjectEntity createNewRfidObject() {
		RfidobjectTypeBsEntity rfidObjType = getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_CONTAINER);
		RfidobjectEntity rfObj = new RfidobjectEntity();
		rfObj.setRfidobjectType(rfidObjType);
		return rfObj;
	}


	/** Display a value from the DB as text(until the first number), "-", 3digits, " ", 3digits, '-', 1 digit
	 * @param contNo string to format
	 * @return formatted string
	 */
	private String formatContainerNo(String contNo) {
		// [[
		String result = null;
		Object[][] params = {{"contNo", contNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "formatContainerNo", params);
		}
		// ]]
		try {
			
			Matcher m = CONTAINER_NO_PATTERN.matcher(contNo);
			if ((m.matches()) && (m.groupCount() == 4)) {
				return result = m.group(1) + "-" + m.group(2) + "-" + m.group(3) + "-" + m.group(4);
			} 
			else {
				return result = contNo;
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
}

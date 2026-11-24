/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.externalmaterialadmin;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.externalmaterialadmin.ExternalMaterialAdminForm;
import com.crh.logon.business.services.interfaces.ExternalMaterialService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ExtMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ExternalMaterialAdminForm"
 *  path="/externalmaterialadmin/ExternalMaterialAdmin" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/externalmaterialadmin/ExternalMaterial.def"
 */
public class ExternalMaterialAdminAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalMaterialAdminAction.class);
	public static final String FORWARD_SELECT_CARRIER = "selectCarrier";
	public static final String FORWARD_SAVE = "save";
	public static final String FORWARD_SELECT_MATERIAL = "selectMaterial";
	public static final String FORWARD_RESET = "reset";
	public static final String FORWARD_ADD_MATERIAL = "addMaterial";
	


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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_EXT_MAT_R);
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("externalmaterial", request);
			String forwardName = "success";
			
			ExternalMaterialAdminForm externalMaterialAdminForm = (ExternalMaterialAdminForm) form;
		 	ExternalMaterialService externalMaterialAdminService = getServiceLocator().getExternalMaterialService();
			String inputAction = externalMaterialAdminForm.getInputAction();
		    
			if (inputAction.equals(FORWARD_SELECT_CARRIER)){
				if (logDebugEnabled) {
					LOG.debug("Action: selected carrier!");
				}
				externalMaterialAdminForm.resetFields();
				
				if (externalMaterialAdminForm.getCarrierId() == Constant.INVALID_OBJECT_ID){
					externalMaterialAdminForm.setCarrierName("");
					externalMaterialAdminForm.setCarrierStreet("");
					externalMaterialAdminForm.setCarrierCity("");
				} else {
					CarrierEntity carrier = (CarrierEntity) externalMaterialAdminForm.getCarrierMap().get(
							externalMaterialAdminForm.getCarrierId());
	
					//set vendor attributes
					SapVendorEntity vendor = carrier.getSapVendor();
					externalMaterialAdminForm.setCarrierName(StringUtils.nvl(vendor.getName1()));
					externalMaterialAdminForm.setCarrierStreet(StringUtils.nvl(vendor.getStreet()) + " "
							+ StringUtils.nvl(vendor.getHouseNumber()));
					externalMaterialAdminForm.setCarrierCity(StringUtils.nvl(vendor.getCity()));
				}
				
				setFormMaterialMap(externalMaterialAdminForm, externalMaterialAdminService);
				
			}
			else if (inputAction.equals(FORWARD_SAVE)){
			
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_EXT_MAT_W);
				
				LOG.debug("Action: save material!");
				String materialName = externalMaterialAdminForm.getMaterialName();
				Long materialId = externalMaterialAdminForm.getMaterialId();
				Long carrierId = externalMaterialAdminForm.getCarrierId();				
			
				if ( StringUtils.empty(materialName) ){
					MessageUtils.addActionError(request, "extmat.errors.required.name");
				}
				else {
					if( externalMaterialAdminService.isNameExist(materialName, carrierId) ){
						if( materialId != null && materialId != 0L ){
							if(!externalMaterialAdminService.getMaterialIdByName(materialName, carrierId).equals(materialId) ){
								//if the user try to rename a material but the new name is not unique
								MessageUtils.addActionError(request, "extmat.errors.not.unique.name");
							} 
						} 
						else{
							// if the user try to add a new material with an existing name
							MessageUtils.addActionError(request, "extmat.errors.not.unique.name");
						}
					} else {
						LOG.debug(""+ materialName + "  is not exist yet");
					}
				}
				
				
				if (noErrorWarningPresent(request)) {
					ExtMaterialEntity extMat = new ExtMaterialEntity();
					Map carrierMap = externalMaterialAdminForm.getCarrierMap();
					CarrierEntity carrier = (CarrierEntity) carrierMap.get(carrierId);
					
					String materialComment = externalMaterialAdminForm.getInputComment();
					boolean activeFlag = externalMaterialAdminForm.isInputActive();
					
					if (externalMaterialAdminForm.getMaterialId() != null && externalMaterialAdminForm.getMaterialId() != 0L){
						extMat = (ExtMaterialEntity) externalMaterialAdminForm.getMaterialMap().get(externalMaterialAdminForm.getMaterialId());
						LOG.debug("Update existing external material");
					}
					
					extMat.setCarrier(carrier);
					extMat.setMaterialName(materialName);
					extMat.setMaterialComment(materialComment);
					extMat.setActiveFlag(activeFlag);
				
					
					extMat = externalMaterialAdminService.saveMaterial(extMat);
					
					if ((extMat != null) && (extMat.getExtMaterialId() != null)) {
						MessageUtils.addActionMessage(request, "extmat.message.success.save");
						LOG.debug("The external material was saved successfully");
						
						externalMaterialAdminForm.getMaterialMap().put(extMat.getExtMaterialId(), extMat);
						externalMaterialAdminForm.setMaterialId(extMat.getExtMaterialId());
					}
				}
			}
			else if(inputAction.equals(FORWARD_SELECT_MATERIAL)){
				setFormFields(externalMaterialAdminForm);
			}
			else if(inputAction.equals(FORWARD_RESET)){
				if (externalMaterialAdminForm.getMaterialId() != null && externalMaterialAdminForm.getMaterialId() != 0L){
					setFormFields(externalMaterialAdminForm);
				} else {
					cleanFormFields(externalMaterialAdminForm);
				}
			}
			else if (inputAction.equals(FORWARD_ADD_MATERIAL)){
				cleanFormFields(externalMaterialAdminForm);
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
	
	
	private void setFormMaterialMap(ExternalMaterialAdminForm helpForm, ExternalMaterialService externalMaterialAdminService){
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"externalMaterialAdminService", externalMaterialAdminService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setFormMaterialMap", params);
		}
		// ]]
		try {
			long carrierId = helpForm.getCarrierId();
			List materialList = externalMaterialAdminService.fetchMaterialsForCarrier(carrierId);
			
			Map materialMap = new LinkedHashMap();
			materialMap = buildMaterialMap(materialList);
			helpForm.setMaterialMap(materialMap);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	
	//Convert list to map.
	private Map buildMaterialMap ( List materialList ) {
		Map materialMap = new LinkedHashMap();
		for (Iterator it = materialList.iterator(); it.hasNext();) {
			ExtMaterialEntity material = (ExtMaterialEntity) it.next();
			materialMap.put(material.getExtMaterialId(), material);
			LOG.debug(" material name: " + material.getMaterialName() );
		}
		return materialMap;
	}
	
	private void setFormFields( ExternalMaterialAdminForm externalMaterialAdminForm){
		long materialId = externalMaterialAdminForm.getMaterialId();
		Map materialMap = externalMaterialAdminForm.getMaterialMap();
		ExtMaterialEntity extMat = (ExtMaterialEntity) materialMap.get(materialId);
		externalMaterialAdminForm.setMaterialName(extMat.getMaterialName());
		externalMaterialAdminForm.setInputActive(extMat.getActiveFlag());
		externalMaterialAdminForm.setInputComment(extMat.getMaterialComment());
	}
	
	private void cleanFormFields( ExternalMaterialAdminForm externalMaterialAdminForm){
		externalMaterialAdminForm.setMaterialName(null);
		externalMaterialAdminForm.setInputActive(true);
		externalMaterialAdminForm.setInputComment(null);
		externalMaterialAdminForm.setMaterialId(null);
	}
}













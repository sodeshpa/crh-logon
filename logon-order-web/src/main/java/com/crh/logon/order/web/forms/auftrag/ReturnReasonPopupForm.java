/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnMaterialUsageBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonTxEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.MessageUtils;

/**
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_auftrag_ReturnReasonPopupForm"
 */
public class ReturnReasonPopupForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReturnReasonPopupForm.class);
    public static final String SESSION_RETURN_REASON_POPUP_FORM_NAME = "order_auftrag_ReturnReasonPopupForm";
    
	private List returnReasonList; 
	private String returnReasonPopupTitle;
	private Long selectedOrderItemId;
	
	private Boolean isServiceOnlyOrder;

	// input fields
	private String returnedMaterialUsedFor;
	private String selectedReturnReasonCd;
	private String selectedPlantCd;
	private String selectedShippingpointId;
	
	private Map<String, PlantBsEntity> plantMap;
	private Map<Long, ShippingpointEntity> shippingpointMap;
	private Map<Long, ReturnMaterialUsageBsEntity> returnMaterialUsageMap;
	
	private Map json;
	
	/**
	 * Init the input fields on the form
	 */
	public void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			this.returnedMaterialUsedFor = null;
			this.selectedReturnReasonCd = null;
			this.selectedPlantCd = null;
			this.selectedShippingpointId = null;
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
	 * Clean the form fields.
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			init();
			
			this.returnReasonPopupTitle = null;
			this.returnReasonList = null;
			
			this.plantMap = null;
			this.shippingpointMap = null;
			this.returnMaterialUsageMap = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void validateReturnReason(HttpServletRequest request) {
		// [[
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {			
			if (StringUtils.empty(this.returnedMaterialUsedFor)) {
				String msg = MessageUtils.getMessage(request, "orderdetail.popup.return.reason.materialUsedFor");
				MessageUtils.addActionError(request, "orderdetail.popup.error.return.reason.inputFieldEmpty", msg);
			}
			
			if (StringUtils.empty(this.selectedReturnReasonCd)) {
				String msg = MessageUtils.getMessage(request, "orderdetail.popup.return.reason.text");
				MessageUtils.addActionError(request, "orderdetail.popup.error.return.reason.inputFieldEmpty", msg);
				
			}
			
			if (StringUtils.empty(this.selectedPlantCd)) {
				String msg = MessageUtils.getMessage(request, "orderdetail.popup.return.reason.plant");
				MessageUtils.addActionError(request, "orderdetail.popup.error.return.reason.inputFieldEmpty", msg);
				
			}
			
			if (StringUtils.empty(this.selectedShippingpointId)) {
				if (isServiceOnlyOrder != null && isServiceOnlyOrder) {
					this.selectedShippingpointId = Constant.INVALID_OBJECT_ID_STR;
				}
				else {
					String msg = MessageUtils.getMessage(request, "orderdetail.popup.return.reason.shpoint");
					MessageUtils.addActionError(request, "orderdetail.popup.error.return.reason.inputFieldEmpty", msg);
				}
			}
			
			// check the mixed qualities
			if (!StringUtils.empty(this.returnedMaterialUsedFor)
					&& Constant.RETURN_MATERIAL_USAGE_MIXED_QUALITIES_CD.equals(Long.valueOf(this.returnedMaterialUsedFor))) {
				
				PlantBsEntity plantEntity = getSelectedPlant();
				if (plantEntity == null) {
					LOG.error("The selected plant cannot be loaded for the plant cd: '" + getSelectedPlantCd() + "'");
				}
				
				String plantName = plantEntity == null ? null : plantEntity.getPlantName();
				String mixedQualities = plantEntity == null ? null : plantEntity.getStorageLocMixQuality();

				if (StringUtils.empty(mixedQualities)) {
					MessageUtils.addActionError(request, "orderdetail.popup.error.return.reason.materialUsedFor", plantName);
				}
			}
			
			if (!StringUtils.empty(this.selectedReturnReasonCd)) {
				Boolean selectedReturnReasonActive = true;
				for (Object entity : returnReasonList) {
					ReturnOrderReasonTxEntity returnReason = (ReturnOrderReasonTxEntity) entity;
					if (returnReason.getReturnOrderReasonTxPK().getReturnOrderReasonBs().getReturnOrderReasonCd().equals(
							selectedReturnReasonCd)) {
						selectedReturnReasonActive = returnReason.getReturnOrderReasonTxPK().getReturnOrderReasonBs().getCtlActCd();
						break;
					}
				}
				if (!selectedReturnReasonActive) {
					MessageUtils.addActionWarning(request,
							"orderdetail.popup.error.return.reason.inactive.reasoncode");
				}
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
	
	public ReturnMaterialUsageBsEntity getSelectedMaterialUsage() {
		// [[
		ReturnMaterialUsageBsEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedMaterialUsage");
		}
		// ]]
		try {
			if (returnMaterialUsageMap == null) {
				LOG.warn("The 'returnMaterialUsageMap' is not initialized!");
				
				return result = null;
			}
			if (StringUtils.empty(returnedMaterialUsedFor)) {
				LOG.warn("The 'returnedMaterialUsedFor' is empty or null!");				

				return result = null;
			}
			return result = returnMaterialUsageMap.get(Long.valueOf(returnedMaterialUsedFor));
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public PlantBsEntity getSelectedPlant() {
		// [[
		PlantBsEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedPlant");
		}
		// ]]
		try {
			if (plantMap == null) {
				LOG.warn("'The plantMap' is not initialized!");
				
				return result = null;
			}
			
			return result = plantMap.get(selectedPlantCd);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List getReturnReasonList() {
		return returnReasonList;
	}
	
	public void setReturnReasonList(List returnReasonList) {
		this.returnReasonList = returnReasonList;
	}
	
	public Long getSelectedOrderItemId() {
		return selectedOrderItemId;
	}
	
	public void setSelectedOrderItemId(Long selectedOrderItemId) {
		this.selectedOrderItemId = selectedOrderItemId;
	}
	
	public Boolean getIsServiceOnlyOrder() {
		return isServiceOnlyOrder;
	}

	public void setIsServiceOnlyOrder(Boolean isServiceOnlyOrder) {
		this.isServiceOnlyOrder = isServiceOnlyOrder;
	}
	
	public String getSelectedReturnReasonCd() {
		return selectedReturnReasonCd;
	}
	
	public void setSelectedReturnReasonCd(String selectedReturnReasonCd) {
		this.selectedReturnReasonCd = selectedReturnReasonCd;
	}

	public String getReturnReasonPopupTitle() {
		return returnReasonPopupTitle;
	}
	
	public void setReturnReasonPopupTitle(String returnReasonPopupTitle) {
		this.returnReasonPopupTitle = returnReasonPopupTitle;
	}
	
	public String getReturnedMaterialUsedFor() {
		return returnedMaterialUsedFor;
	}
	
	public void setReturnedMaterialUsedFor(String returnedMaterialUsedFor) {
		this.returnedMaterialUsedFor = returnedMaterialUsedFor;
	}
	
	public String getSelectedPlantCd() {
		return selectedPlantCd;
	}
	
	public void setSelectedPlantCd(String selectedPlantCd) {
		this.selectedPlantCd = selectedPlantCd;
	}
	
	public String getSelectedShippingpointId() {
		return selectedShippingpointId;
	}
	
	public void setSelectedShippingpointId(String selectedShippingpointId) {
		this.selectedShippingpointId = selectedShippingpointId;
	}
	
	public Map getJson() {
		return json;
	}
	
	public void setJson(Map json) {
		this.json = json;
	}

	public Map<String, PlantBsEntity> getPlantMap() {
		return plantMap;
	}

	public void setPlantMap(Map<String, PlantBsEntity> plantMap) {
		this.plantMap = plantMap;
	}

	public Map<Long, ShippingpointEntity> getShippingpointMap() {
		return shippingpointMap;
	}

	public void setShippingpointMap(Map<Long, ShippingpointEntity> shippingpointMap) {
		this.shippingpointMap = shippingpointMap;
	}

	public Map<Long, ReturnMaterialUsageBsEntity> getReturnMaterialUsageMap() {
		return returnMaterialUsageMap;
	}

	public void setReturnMaterialUsageMap(Map<Long, ReturnMaterialUsageBsEntity> returnMaterialUsageMap) {
		this.returnMaterialUsageMap = returnMaterialUsageMap;
	}
	
}

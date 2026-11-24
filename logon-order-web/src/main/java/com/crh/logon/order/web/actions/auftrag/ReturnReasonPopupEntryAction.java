/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.auftrag.OrderItemBean;
import com.crh.logon.order.web.forms.auftrag.ReturnReasonPopupForm;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnMaterialUsageBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonTxEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

import flexjson.JSONSerializer;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_auftrag_ReturnReasonPopupForm"
 *  path="/auftrag/ReturnReasonPopupEntry" scope="session"
 *  validate="false"
 *  
 * @struts.action-forward name="success" path="ReturnReasonPopup.def"
 */
public class ReturnReasonPopupEntryAction extends ReturnReasonPopupBase {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReturnReasonPopupEntryAction.class);
	
	private static final String FORWARD_SUCCESS = "success";
	
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
			Authorization.authorize(Authorization.ORDER_DETAIL_R);
			
			ReturnReasonPopupForm helpForm = (ReturnReasonPopupForm) form;
			
			String selectedOrderItemId = request.getParameter("selectedOrderItemId");
			String selectedSapMaterialId = request.getParameter("selectedSapMaterialId");
			
			if (StringUtils.empty(selectedOrderItemId)) {
				LOG.error("The 'selectedOrderItemId' parameter is missing!");
				throw new ServletException("The 'selectedOrderItemId' parameter is missing!");
			}
					
			if (StringUtils.empty(selectedSapMaterialId)) {
				LOG.error("The 'selectedSapMaterialId' parameter is missing!");
				throw new ServletException("The 'selectedSapMaterialId' parameter is missing!");
			}
			
			OrderDetailForm ordDetForm = (OrderDetailForm)request.getSession().getAttribute(OrderDetailForm.SESSION_RFIDFORM_NAME);
			if (ordDetForm == null) {
				String errorMsg = "The 'OrderDetailForm' Action form is missing from the session!";
				LOG.error(errorMsg);
				throw new ServletException(errorMsg);
			}
			
			Long orderItemId = Long.valueOf(selectedOrderItemId);
			Long sapMaterialId = Long.valueOf(selectedSapMaterialId);
			
			OrderItemBean orderItemBean = getSelectedOrderItemBean(ordDetForm, orderItemId);
			if (orderItemBean == null) {
				String errorMsg = "Couldn't load the orderItem with id: '" + orderItemId + "', from OrderDetailForm.getMaterialBean().getOrderItemList()!";
				LOG.error(errorMsg);
				throw new ServletException(errorMsg);				
			}
			
			OrderEntity orderEntity = ordDetForm.getOrder();
			ShippingpointEntity shippingpointEtity = orderEntity.getShippingpoint();
			
			helpForm.setIsServiceOnlyOrder(LogonUtil.isServiceOnlyOrder(orderEntity));
			
			String plantCd;
			if (orderItemBean.getReturnPlant() != null) {
				plantCd = orderItemBean.getReturnPlant().getPlantCd();
			}
			else if (shippingpointEtity != null) {
				plantCd = shippingpointEtity.getPlant().getPlantCd();
			}
			else {
				// using the production plant (e.g. at Service Material Only orders where there is no shipping point see: LRQ 070.3 / LOGON-4743)
				plantCd = orderItemBean.getPlantCd();
			}

			final String shippingpointId;
			if (orderItemBean.getReturnShippingpoint() != null) {
				shippingpointId = orderItemBean.getReturnShippingpoint().getShippingpointId().toString();
			}
			else if (shippingpointEtity != null) {
				shippingpointId = shippingpointEtity.getShippingpointId().toString();
			}
			else {
				shippingpointId = Constant.INVALID_OBJECT_ID_STR;
			}
			
			String returnMaterialUsageCd = orderItemBean.getReturnMaterialUsage() != null ? orderItemBean.getReturnMaterialUsage().getReturnMaterialUsageCd().toString()
					: null;
			
			// prepare service
			OrderDetailService orderDetailService = getOrderDetailService();			
			
			List returnOrderReasonTxEntityList = getRefDataGenericsService().findTxEntityList(ReturnOrderReasonTxEntity.class, getLocale(request));
			Map<String, PlantBsEntity> plantMap = getPlantMap(sapMaterialId, orderDetailService);
			plantCd = plantMap.containsKey(plantCd) ? plantCd : null; // if the user is not authorized for that plant or the material is not linked --> set to null
			Map<Long, ShippingpointEntity> shippingpointMap = getShippingpointMap(plantCd, orderDetailService);
			Map<Long, ReturnMaterialUsageBsEntity> returnMaterialUsageMap = getReturnMaterialUsageMap();
			
			String returnReasonPopuptitle = orderEntity.getOrderNumber().toString() + " " + orderItemBean.getSapMaterialName();

			Map jsonData = new HashMap();
			JSONSerializer jsonSerializer = getJsonSerializer();
			jsonData.put("shippingPointList", jsonSerializer.serialize(shippingpointMap.values()));
			jsonData.put("plantBsList", jsonSerializer.serialize(plantMap.values()));		
			
			helpForm.setJson(jsonData);
			helpForm.setSelectedOrderItemId(Long.valueOf(selectedOrderItemId));
			helpForm.setReturnReasonList(returnOrderReasonTxEntityList);
			helpForm.setReturnReasonPopupTitle(returnReasonPopuptitle);	
			helpForm.setSelectedShippingpointId(shippingpointId);
			helpForm.setSelectedPlantCd(plantCd);
			helpForm.setReturnedMaterialUsedFor(returnMaterialUsageCd);
			helpForm.setReturnMaterialUsageMap(returnMaterialUsageMap);
			helpForm.setPlantMap(plantMap);
			helpForm.setSelectedReturnReasonCd(orderItemBean.getReturnReason());
			
			return result = mapping.findForward(FORWARD_SUCCESS);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private Map<String, PlantBsEntity> getPlantMap(Long sapMaterialId, OrderDetailService orderDetailService) {
		// [[
		Map<String,PlantBsEntity> result = null;
		Object[][] params = {{"sapMaterialId", sapMaterialId},
							{"orderDetailService", orderDetailService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlantMap", params);
		}
		// ]]
		try {
			List<PlantBsEntity> plantList = orderDetailService.loadSapMaterialPlantList(sapMaterialId, false);
			
			if (plantList == null || plantList.isEmpty()) {
				return result = Collections.EMPTY_MAP; // should never happen
			}
			
			result = new HashMap<String, PlantBsEntity>();
			for (Iterator<PlantBsEntity> iter = plantList.iterator(); iter.hasNext();) {
				PlantBsEntity entity = iter.next();
				result.put(entity.getPlantCd(), entity);
			}
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private Map<Long, ReturnMaterialUsageBsEntity> getReturnMaterialUsageMap() {
		// [[
		Map<Long,ReturnMaterialUsageBsEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getReturnMaterialUsageMap");
		}
		// ]]
		try {
			List<ReturnMaterialUsageBsEntity> entityList = getRefDataGenericsService().findActiveBsEntityList(ReturnMaterialUsageBsEntity.class);
			
			if (entityList == null || entityList.isEmpty()) {
				return result = Collections.EMPTY_MAP; // should never happen
			}
			
			result = new HashMap<Long, ReturnMaterialUsageBsEntity>();			
			for (Iterator<ReturnMaterialUsageBsEntity> iter = entityList.iterator(); iter.hasNext();) {
				ReturnMaterialUsageBsEntity entity = iter.next();
				
				result.put(entity.getReturnMaterialUsageCd(), entity);
			}
			
			return result;
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

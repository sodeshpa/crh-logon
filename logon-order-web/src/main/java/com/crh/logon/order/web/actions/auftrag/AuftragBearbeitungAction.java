/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.beans.HasLoadedQuantity;
import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.business.services.beans.OrderChangeLogic;
import com.crh.logon.business.services.beans.SimpleVehicleWeighingData;
import com.crh.logon.business.services.beans.pdf.PdfGeneratingResult;
import com.crh.logon.business.services.interfaces.AssignmentService;
import com.crh.logon.business.services.interfaces.ManualLoadingService;
import com.crh.logon.business.services.interfaces.OrderChangeService;
import com.crh.logon.business.services.interfaces.PDFGeneratorService;
import com.crh.logon.business.services.interfaces.SimpleVehicleSchedulingService;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderStatistic;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.persistence.entities.views.ShippBimodalRoadRailEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_AuftragBearbeitungForm"
 *  path="/auftrag/AuftragBearbeitung" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="fetchfirst" path="/auftrag/AuftragBearbeitungEntry.do"
 * @struts.action-forward name="success" path="AuftragBearbeitungTile.def"
 * @struts.action-forward name="selectDateInterval" path="DateSelector.def"
 * @struts.action-forward name="getStatistic" path="InfoStatistic.def"
 * @struts.action-forward name="getStatistic2" path="InfoStatistic2.def"
 * @struts.action-forward name="loadShippingPoints" path="ShippingPointSelector.def"
 * @struts.action-forward name="loadingpointSelect" path="LoadingPointSelector.def"
 * @struts.action-forward name="updateJs" path="UpdateJsTile.def"
 * @struts.action-forward name="message" path="MessageTile.def"
 * @struts.action-forward name="changeContainer" path="ContainerSelector.def"
 * @struts.action-forward name="editLdsInformation" path="EditLdsInformationTile.def"    
 */
public class AuftragBearbeitungAction extends OrderActionSupport {

	public static final int maxRowNumber = 25;
	
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AuftragBearbeitungAction.class);

	public static final String ACTION_TRANSMIT_TO_WEIGH_SYSTEM = "transmitToWeighSystem";
	public static final String ACTION_SELECT_LOADINGPOINT = "selectLp";
    public static final String ACTION_LOAD_SHIPPINGPOINTS = "loadShippingPoints";
    public static final String ACTION_SAVE_MEASURE_DATA = "saveMeasureData";
	public static final String ACTION_SET_LOADED_AS_PLANNED = "setLoadedAsPlanned";
    public static final String ACTION_SELECT_DATE_INTERVAL = "selectDateInterval";
    public static final String ACTION_GET_STATISTIC = "getStatistic";
    public static final String ACTION_GET_STATISTIC2 = "getStatistic2";
    public static final String ACTION_LOAD_ORDERS = "loadOrders";
    public static final String ACTION_CHANGE_LOADINGPOINT = "loadingpointSelect";
    public static final String ACTION_GENERATE_PDF = "generatepdf";
    public static final String ACTION_CANCEL_ON_WEIGH_SYSTEM = "cancelOnWeighSystem";
    public static final String ACTION_CHANGE_CONTAINER = "changeContainer";
    public static final String ACTION_SAVE_CONTAINER_SELECTION = "saveContainer";
    public static final String ACTION_CANCEL_LOADING = "cancelLoading";
    public static final String ACTION_EDIT_LDS_INFORMATION = "editLdsInformation";
    
    private static final String FORWARD_MESSAGE_TILE = "message";
    private static final String FORWARD_UPDATE_JS = "updateJs";
	private static final String FORWARD_LOAD_SHIPPINGPOINTS = "loadShippingPoints";
	private static final String FORWARD_SELECT_DATE_INTERVAL = "selectDateInterval";
	private static final String FORWARD_GET_STATISTIC = "getStatistic";
	private static final String FORWARD_GET_STATISTIC2 = "getStatistic2";
	private static final String FORWARD_EDIT_LDS_INFORMATION = "editLdsInformation";
	private static final String FORWARD_SUCCESS = "success";
	
	private static final String PARAMETER_BASE_UOM_CD = "baseUomCd";

	private static final Date NO_LOADING_START_TIME = null;
	private static final Date NO_LOADING_END_TIME = null;

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
			Authorization.authorize(Authorization.SHIPPING_R);
			setSelectedTab("auftragbearbeitung", request);
			
			AuftragBearbeitungForm helpForm = (AuftragBearbeitungForm) form;

			String forwardName = FORWARD_SUCCESS;
			String formAction = !StringUtils.empty(helpForm.getFormAction()) ? helpForm.getFormAction() : "";
			
			String plantCd = helpForm.getSelectedPlantCd();			
			List listShippingpoint = helpForm.getSelectableShippingPoints();
			Long shippingPointId = StringUtils.notEmpty(helpForm.getSelectedShippingPointId()) ? Long.valueOf(helpForm.getSelectedShippingPointId()) : null;
			
			Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping = helpForm.getOrderLoadingPointListMapping();
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo = helpForm.getBimodalRoadAndRelatedRailInfoMap();
			Map<Long, Date> truckNextMinPlannedStartMap = helpForm.getTruckNextOrderMapping();
			
			OrderChangeLogic orderChangeLogic = new OrderChangeLogic(truckNextMinPlannedStartMap, 
					orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo);

			OrderChangeService orderChangeService = getServiceLocator().getOrderChangeService();
			if (ACTION_SAVE_MEASURE_DATA.equals(formAction) || ACTION_SET_LOADED_AS_PLANNED.equals(formAction)) {
				Long loadId = helpForm.getInputLoadId();
				OrdItemAssignmtLoadEntity orderItemAssignmentLoad = getOrderWithLoadId(helpForm, loadId);
				Long logonPackageTypeCd = orderItemAssignmentLoad.getLogonPackageTypeCd();
				Long deliveryTypeCd = orderItemAssignmentLoad.getDeliveryTypeCd();
				
				String loadedQuantityString = request.getParameter(loadId + "inputOrderItemLoadedQuantity" + orderItemAssignmentLoad.getOrderItemId());
				boolean success = helpForm.validateSaveInput(request, logonPackageTypeCd, loadedQuantityString);
				if (! success){
					return result = mapping.findForward(FORWARD_MESSAGE_TILE);
				}
				
				Authorization.authorize(Authorization.SHIPPING_W);
				//check if all required circumstances are fulfilled for saving, show message/confirm if not, otherwise save asap
									
				if ( orderChangeLogic.disableSimpleChangeFunctionality(orderItemAssignmentLoad, OrderChangeLogic.FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA) == false ||
						orderChangeLogic.disableSimpleChangeFunctionality(orderItemAssignmentLoad, OrderChangeLogic.FUNCTIONALITY_UPDATE_TARA_MEASURE_DATA) == false ||
						orderChangeLogic.disableSimpleChangeFunctionality(orderItemAssignmentLoad, OrderChangeLogic.FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY) == false
						){
					boolean isEmpty = true;
					//loading has not started yet, but it is going to be
					if (orderItemAssignmentLoad.getLoadingTs() == null && helpForm.getInputTaraMeasureValue() != null){
						isEmpty = orderChangeService.preCheckTruckIsEmpty(orderItemAssignmentLoad.getTruckId(), orderItemAssignmentLoad.getAssignmentId());
					}
					if (!isEmpty) {
						MessageUtils.addActionError(request, "auftragbearbeitung.errors.truckisnotempty");
						return result = mapping.findForward(FORWARD_MESSAGE_TILE);
					}
											
					if (LogonUtil.isShipPickupOrder(deliveryTypeCd)){
						updateSimpleVehicleOrderData(mapping, request, helpForm, Constant.SIMPLE_VEHICLE_SHIP_PICKUP_CD);
					}
					else if (LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)){
						updateSimpleVehicleOrderData(mapping, request, helpForm, Constant.SIMPLE_VEHICLE_BLOCKTRAIN_PICKUP_CD);
					}
					else{
						final boolean isRealTimeLoading = !formAction.equals(ACTION_SET_LOADED_AS_PLANNED);
						updateOrderData(mapping, request, helpForm, isRealTimeLoading);
					}
				}
				return result = mapping.findForward(FORWARD_MESSAGE_TILE);
			}
			else if (ACTION_LOAD_SHIPPINGPOINTS.equals(formAction)) {
				//happens when plant is selected
				//load all shipping points availanle for the plant and user combination - leave it unselected
				listShippingpoint = orderChangeService.loadShippingPointsForPlant(plantCd);
				helpForm.setSelectableShippingPoints(listShippingpoint);
				helpForm.setSelectedShippingPointId(AuftragBearbeitungForm.EMPTY_SELECTION);
				forwardName = FORWARD_LOAD_SHIPPINGPOINTS;
				return result = mapping.findForward(forwardName);
			}
			else if (ACTION_LOAD_ORDERS.equals(formAction)) {

				forwardName = FORWARD_SUCCESS;
				return result = mapping.findForward(forwardName);
			}
			else if (ACTION_GET_STATISTIC.equals(formAction)) {
				OrderStatistic stat = orderChangeService.getStatistics(
						plantCd, shippingPointId,
						helpForm.getFromDate(), helpForm.getToDate(),
						Constant.LANGUAGE_CODE_ENGLISH);
				helpForm.setStat(stat);
				return result = mapping.findForward(FORWARD_GET_STATISTIC);
			}
			else if (ACTION_GET_STATISTIC2.equals(formAction)) {
				OrderStatistic stat = orderChangeService.getStatistics(
						plantCd, shippingPointId,
						helpForm.getFromDate(), helpForm.getToDate(),
						Constant.LANGUAGE_CODE_ENGLISH);
				helpForm.setStat(stat);
				return result = mapping.findForward(FORWARD_GET_STATISTIC2);
			}
			else if (ACTION_SELECT_DATE_INTERVAL.equals(formAction)) {
				updateDateIntervalSetting(helpForm);
				forwardName = FORWARD_SELECT_DATE_INTERVAL;
				return result = mapping.findForward(forwardName);
			}
			else if (ACTION_SELECT_LOADINGPOINT.equals(formAction)) {
				Authorization.authorize(Authorization.SHIPPING_W);
				Long loadingPoint = helpForm.getInputLoadingPointId();
				Long loadId = helpForm.getInputLoadId();
				List orderList = helpForm.getOrderList();
				Iterator it = orderList.iterator();
				while (it.hasNext()) {						
					OrdItemAssignmtLoadEntity object = (OrdItemAssignmtLoadEntity) it.next();
					if ( object.getLoadId() != null && object.getLoadId().equals(loadId)){
						// FIXME (emoke Sep 22, 2009):probably not only one order is related! think over, does this make any difference?
						object.setLoadingpointId(loadingPoint);
						break;
					}
				}	
				forwardName = FORWARD_UPDATE_JS;
				return result = mapping.findForward(forwardName);
			}
			else if (ACTION_CANCEL_ON_WEIGH_SYSTEM.equals(formAction)) {
				Authorization.authorize(Authorization.SHIPPING_W);
				Long loadId = helpForm.getInputLoadId();
				
				OrdItemAssignmtLoadEntity order = getOrderWithLoadId(helpForm, loadId);

				Long assignmentId = order.getAssignmentId();
				Long shippingpointId = order.getShippingpointId();

				if (assignmentId != null && shippingpointId != null) {
					try{									
						orderChangeService.cancelOnWeighSystem(assignmentId, shippingpointId);
						MessageUtils.addActionMessage(request, "auftragbearbeitung.info.weighingsystemcancelled");
					}
					catch (RuntimeException re) {
						if (re.getCause() instanceof LogonException) {
							LogonException le = (LogonException)re.getCause();
							Reason reason = le.getReason();
							if ( (reason.getReason().equals(Reason.RES_APP_ERROR_PARALLEL.getReason())) || (reason.getReason().equals(Reason.RES_TECH_ERROR_PARALLEL.getReason())) ) {
								MessageUtils.addActionError(request, "auftragbearbeitung.errors.parallelerrordetailedcancel", le.getMessage());
							}
							else{
								MessageUtils.addActionError(request, "auftragbearbeitung.errors.parallelerrorcancel");
							}
							forwardName = FORWARD_MESSAGE_TILE;
						}
						else {
							throw re;
						}
					}
				}

				forwardName = FORWARD_MESSAGE_TILE;
				return result = mapping.findForward(forwardName);		
			}
			else if (ACTION_CANCEL_LOADING.equals(formAction)) {
				// LOGON-5629 LRQ 073.2 StartLoading does not Block Deallocation Functionality
				Authorization.authorize(Authorization.SHIPPING_W);
				final Long loadId = helpForm.getInputLoadId();

				final OrdItemAssignmtLoadEntity order = getOrderWithLoadId(helpForm, loadId);
				final Long assignmentId = order.getAssignmentId();

				if (assignmentId != null) {
					try {
						final AssignmentService assignmentService = getServiceLocator().getAssignmentService();

						assignmentService.cancelLoading(assignmentId);
						
						MessageUtils.addActionMessage(request,
								"auftragbearbeitung.info.loadingcancelled");
					}
					catch (RuntimeException re) {
						if (re.getCause() instanceof LogonException) {
							LogonException le = (LogonException) re.getCause();
							Reason reason = le.getReason();
							MessageUtils.addActionError(request, reason.getReason());
						}
						else {
							throw re;
						}
					}
				}

				forwardName = FORWARD_MESSAGE_TILE;
				return result = mapping.findForward(forwardName);
			}
			else if (ACTION_TRANSMIT_TO_WEIGH_SYSTEM.equals(formAction)) {
				Authorization.authorize(Authorization.SHIPPING_W);
				Long loadId = helpForm.getInputLoadId();
				
				OrdItemAssignmtLoadEntity order = getOrderWithLoadId(helpForm, loadId);
				
				Long assignmentId = order.getAssignmentId();
				Long shippingpointId = order.getShippingpointId();
				
				if (assignmentId != null && shippingpointId != null) {
					try {
						getServiceLocator().getOrderService().transmitToWeighSystemFromTerminal(assignmentId, shippingpointId);
						MessageUtils.addActionMessage(request, "auftragbearbeitung.info.weighsystemtransmitted");
					}
					catch (RuntimeException re) {
						if (re.getCause() instanceof LogonException) {
							ExceptionUtil.errException(LOG, re.getCause(), params);
							LogonException le = (LogonException)re.getCause();
							MessageUtils.addActionError(request, "auftragbearbeitung.errors.weighsystemerror", le.getMessage());
							forwardName = FORWARD_MESSAGE_TILE;
						}
						else {
							throw re;
						}
					}
				}
		
				forwardName = FORWARD_MESSAGE_TILE;
				return result = mapping.findForward(forwardName);
			}
			else if (ACTION_CHANGE_CONTAINER.equals(formAction)) {
				// just in case of one-one relationship between load and order item
				String orderItemIdForContainer = request.getParameter(AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID);
				Long id = null;
				try {
					id = Long.valueOf(orderItemIdForContainer);
				}
				catch (Exception e) {
					id = null;						
				}
		
				List containerList = orderChangeService.getContainers(id);
				request.setAttribute(AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID, orderItemIdForContainer);
				request.setAttribute(AuftragBearbeitungForm.REQUEST_CONTAINERLIST, containerList);
				return result = mapping.findForward(ACTION_CHANGE_CONTAINER);						
			}
			else if (ACTION_SAVE_CONTAINER_SELECTION.equals(formAction)) {
				String orderItemId = request.getParameter(AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID);
				String containerIdStr = request.getParameter(AuftragBearbeitungForm.REQUEST_CONTAINER_ID);
				Long id = null;
				Long containerId = null;
				try {
					id = Long.valueOf(orderItemId);
					containerId = Long.valueOf(containerIdStr);
				}
				catch (Exception e) {
					id = null;
					containerId = null;
				}
				if ( orderItemId == null || containerId == null ) {
					MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.failedcontainerload");
					List containerList = orderChangeService.getContainers(id);
					request.setAttribute(AuftragBearbeitungForm.REQUEST_CONTAINERLIST, containerList);
					request.setAttribute(AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID, orderItemId);
					return result = mapping.findForward(ACTION_CHANGE_CONTAINER);	
				}
				OrdItemAssignmtLoadEntity orderViewEnt = getOrderWithOrderItemId(helpForm, id);
				try {
					getServiceLocator().getManualLoadingService().updateContainerLoad(orderViewEnt.getLoadId(), containerId, orderViewEnt.getLoadingpointId());
				}
				catch (Exception e) {
					if (e != null) {							
						LOG.error(ExceptionUtil.exceptionString(e, null));
					}
					MessageUtils.addActionError(request, "auftragbearbeitung.errors.failedcontainerload");
					List containerList = orderChangeService.getContainers(id);
					request.setAttribute(AuftragBearbeitungForm.REQUEST_CONTAINERLIST, containerList);
					request.setAttribute(AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID, orderItemId);
					return result = mapping.findForward(ACTION_CHANGE_CONTAINER);	
				}
				request.setAttribute("close", "close");
				return result = mapping.findForward(ACTION_CHANGE_CONTAINER);
			}
			else if (ACTION_GENERATE_PDF.equals(formAction)) {
				Authorization.authorize(Authorization.SHIPPING_W);
				Long id = Long.valueOf(helpForm.getInputOrderId());
				PDFGeneratorService pdfGeneratorService = getServiceLocator().getPDFGeneratorService();
				
				PdfGeneratingResult pdfGeneratingResult = pdfGeneratorService.generatePDF(id);
				if (!pdfGeneratingResult.isSuccess()){
					MessageUtils.addActionError(request, "errors.failureGeneration");
				}
				else {
					MessageUtils.addActionMessage(request, "message.successGeneration");
				}
				forwardName = FORWARD_MESSAGE_TILE;
				return result = mapping.findForward(forwardName);		
			}
			else if (ACTION_CHANGE_LOADINGPOINT.equals(formAction)) {
				Long loadId = helpForm.getInputLoadId();
				Long ordShippingPoint = Long.valueOf((String)request.getParameter("orderShippingId"));
				Long ordProductId = Long.valueOf((String)request.getParameter("orderProductId"));
				List<LdpointMatRestrEntity> loadingPoints = orderChangeService.fetchLoadingPoints(ordShippingPoint, ordProductId);
				orderLoadingPointListMapping.put(loadId, loadingPoints);
				helpForm.setSelectableOrderLoadingpointList(loadingPoints);
				request.setAttribute("order", getOrderWithLoadId(helpForm, loadId));
				
				return result = mapping.findForward(ACTION_CHANGE_LOADINGPOINT);
			}
			else if (ACTION_EDIT_LDS_INFORMATION.equals(formAction)) {
				Long loadId = helpForm.getInputLoadId();
				request.setAttribute("order", getOrderWithLoadId(helpForm, loadId));
				forwardName = FORWARD_EDIT_LDS_INFORMATION;
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
	 * This method returns true if all of the orderItems of the order have ZERO as loaded quantity.
	 * It is helpful in bag case, because we let individual order items to have zero quantity, but not for the sum of all.
	 */
	private boolean isSumOfLoadedQuantitiesZero(Map<Long, ? extends HasLoadedQuantity> newQuantities, Long orderId) {
		
		if (newQuantities == null){
			return false;
		}
		
		OrderEntity order = getServiceLocator().getOrderService().findOrderById(orderId);
				
		BigDecimal sum = new BigDecimal(0);
		sum.setScale(Constant.SCALE);

		for (OrderItemEntity item : order.getOrderItems()){
			HasLoadedQuantity newQuantity = newQuantities.get(item.getOrderItemId());
			if (newQuantity != null){
				sum = sum.add(newQuantity.getLoadedQuantity());
			}
			else if (item.getLoadedQuantity() == null){
				return false;
			}
			else {
				sum = sum.add(item.getLoadedQuantity());
			}
		}
		
		return sum.equals(BigDecimal.ZERO);
	}

	private Map<Long, LoadedQuantityData> parseLoadingData(Long loadId, AuftragBearbeitungForm helpForm,
			HttpServletRequest request) {
		// [[
		Map<Long,LoadedQuantityData> result = null;
		Object[][] params = {{"loadId", loadId},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "parseLoadingData", params);
		}
		// ]]
		try {
			
			final HashMap<Long, LoadedQuantityData> parsedBagLoadingData = new HashMap<Long, LoadedQuantityData>();
			
			List<Long> orderItemIds = getServiceLocator().getLoadingService().fetchOrderItemIdsForLoadId(loadId);
			for (Long orderItemId : orderItemIds) {
				String loadedQuantityString = request.getParameter(loadId + "inputOrderItemLoadedQuantity" + orderItemId);
				String loadedUom = request.getParameter(loadId + "inputOrderItemLoadedUomCd" + orderItemId);
				
				if (StringUtils.empty(loadedQuantityString) || StringUtils.empty(loadedUom)) {
					MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.invalidloadedquantity");
					return result = null;					
				}
				
				loadedQuantityString = loadedQuantityString.replace(",", ".");
				
				ActionSupport.checkFieldMaxValue(request, loadedQuantityString, 
						OrderItemEntity.DBSIZE_LOADED_QUANTITY, 
						OrderItemEntity.DBSIZE_PRECISION_LOADED_QUANTITY, 
						"auftragbearbeitung.texte.ordercolumn.nettoweight");
				if (MessageUtils.getActionWarnings(request) != null &&
						!MessageUtils.getActionWarnings(request).isEmpty()) {
					return result = null;
				}

				BigDecimal loadedQuantity = new BigDecimal(loadedQuantityString);
				
				parsedBagLoadingData.put(orderItemId, new LoadedQuantityData(loadedQuantity, loadedUom));

			}

			return result = parsedBagLoadingData;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private Map<Long, SimpleVehicleWeighingData> parseLoadingDataForSimpleVehicle(Long loadId, AuftragBearbeitungForm helpForm,
			HttpServletRequest request) {
		// [[
		Map<Long,SimpleVehicleWeighingData> result = null;
		Object[][] params = {{"loadId", loadId},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "parseLoadingData", params);
		}
		// ]]
		try {
			final HashMap<Long, SimpleVehicleWeighingData> parsedSimpleVehicleData = new HashMap<Long, SimpleVehicleWeighingData>();
			
			List<Long> orderItemIds = getServiceLocator().getLoadingService().fetchOrderItemIdsForLoadId(loadId);
			for (Long orderItemId : orderItemIds) {
				String loadedQuantityString = request.getParameter(loadId + "inputOrderItemLoadedQuantity" + orderItemId);
				String loadUnitOfMeasurement = request.getParameter(loadId + "inputOrderItemLoadedUomCd" + orderItemId);
				
				if (StringUtils.empty(loadedQuantityString) || StringUtils.empty(loadUnitOfMeasurement)) {
					MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.invalidloadedquantity");
					return result = null;					
				}

				loadedQuantityString = loadedQuantityString.replace(",", ".");

				ActionSupport.checkFieldMaxValue(request, loadedQuantityString, 
						OrderItemEntity.DBSIZE_LOADED_QUANTITY, 
						OrderItemEntity.DBSIZE_PRECISION_LOADED_QUANTITY, 
						"auftragbearbeitung.texte.ordercolumn.nettoweight");
				if (MessageUtils.getActionWarnings(request) != null &&
						!MessageUtils.getActionWarnings(request).isEmpty()) {
					return result = null;
				}

				BigDecimal loadedQuantity = new BigDecimal(loadedQuantityString);

				parsedSimpleVehicleData.put(orderItemId, 
						new SimpleVehicleWeighingData(orderItemId, loadId, loadedQuantity, loadUnitOfMeasurement));			
			}
			
			return result = parsedSimpleVehicleData;
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
	 * @param helpForm
	 * @param loadId
	 * @return
	 */
	protected OrdItemAssignmtLoadEntity getOrderWithLoadId(AuftragBearbeitungForm helpForm, long loadId) {
		Authorization.authorize(Authorization.SHIPPING_R);
		List orderList = helpForm.getOrderList();
		Iterator it = orderList.iterator();
		OrdItemAssignmtLoadEntity order = null; 
		while (it.hasNext()) {
			OrdItemAssignmtLoadEntity object = (OrdItemAssignmtLoadEntity) it.next();
			if (object.getLoadId()!= null && object.getLoadId().longValue() == loadId){
				order = object;
				break;
			}
		}
		return order;
	}
	
	/**
	 * @param helpForm
	 * @param loadId
	 * @return
	 */
	private OrdItemAssignmtLoadEntity getOrderWithOrderItemId(AuftragBearbeitungForm helpForm, long itemId) {
		Authorization.authorize(Authorization.SHIPPING_R);
		List orderList = helpForm.getOrderList();
		Iterator it = orderList.iterator();
		OrdItemAssignmtLoadEntity order = null; 
		while (it.hasNext()) {
			OrdItemAssignmtLoadEntity object = (OrdItemAssignmtLoadEntity) it.next();
			if (object.getOrderItemId()!= null && object.getOrderItemId().longValue() == itemId){
				order = object;
				break;
			}
		}
		return order;
	}
	
	private boolean updateOrderData(ActionMapping mapping, HttpServletRequest request,
			AuftragBearbeitungForm helpForm, boolean isRealTimeLoading) {
		
		BigDecimal netQuantity = null;
		BigDecimal tareWeightT = null;
		long loadId = helpForm.getInputLoadId();
		
		
		
		OrdItemAssignmtLoadEntity order = getOrderWithLoadId(helpForm, loadId);
		if ( helpForm.getInputLoadingPointId().equals(Long.valueOf(Constant.INVALID_KEY)) && order.getLoadingpointId() != null ) {
			helpForm.setInputLoadingPointId(order.getLoadingpointId());
		}
		try {
			Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping = helpForm.getOrderLoadingPointListMapping();
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo = helpForm.getBimodalRoadAndRelatedRailInfoMap();
			Map<Long, Date> truckNextMinPlannedStartMap = helpForm.getTruckNextOrderMapping();
			
			OrderChangeLogic orderChangeLogic = new OrderChangeLogic(truckNextMinPlannedStartMap, 
					orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo);
			
			ManualLoadingService manualLoadingService = getServiceLocator().getManualLoadingService();
			
			/**
			 * CASE BULK
			 * In case of bulk, there are two important input fields: inputTaraMeasure and
			 * inputNettoMeasure. The tare is not required to be set. In case not set, the order
			 * service will set the truck's master data tare weight to the load.
			 */
			if (orderChangeLogic.disableSimpleChangeFunctionality(order, OrderChangeLogic.FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA) == false ||
					orderChangeLogic.disableSimpleChangeFunctionality(order, OrderChangeLogic.FUNCTIONALITY_UPDATE_TARA_MEASURE_DATA) == false){
				
				boolean isNetQuantitySet = StringUtils.notEmpty(helpForm.getInputNettoMeasureValue());
				boolean isTareWeightSet = StringUtils.notEmpty(helpForm.getInputTaraMeasureValue());
				
				if (isNetQuantitySet) {
					addWarningIfLoadingpointUnset(request, helpForm, order);
					if (MessageUtils.getActionWarnings(request) != null
							&& !MessageUtils.getActionWarnings(request).isEmpty()) {
						return false;
					}
				}
				netQuantity = isNetQuantitySet ? new BigDecimal(helpForm.getInputNettoMeasureValue()) : null;
				tareWeightT = isTareWeightSet ? new BigDecimal(helpForm.getInputTaraMeasureValue()): null;
				String loadedUom = request.getParameter("baseUomCd");
				UomBsEntity netQuantityUom = getRefDataGenericsService().findBsEntity(UomBsEntity.class, loadedUom); 
						
				if (isTareWeightSet || isNetQuantitySet) {
					Date [] loadingTimes = getLoadingTimes(isRealTimeLoading, loadId);
					manualLoadingService.updateBulkLoad(loadId, helpForm.getInputLoadingPointId(),
							tareWeightT, netQuantity, netQuantityUom, loadingTimes[0], loadingTimes[1]);
				}
			}
			/**
			 * CASE BAG
			 * As far there may be more than one order items sharing the same load, those have one
			 * common save button, but separate input fields for setting the order item's loaded
			 * quantity. The load will get their sum at the end. They must share the same unit of
			 * measure.
			 */
			else if (orderChangeLogic.disableSimpleChangeFunctionality(order, OrderChangeLogic.FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY) == false){
				
				//parse the order item loaded quantities
				Map<Long, LoadedQuantityData> loadingData = parseLoadingData(order.getLoadId(), helpForm, request);
				
				if (loadingData != null) {
					
					if (isSumOfLoadedQuantitiesZero(loadingData, order.getOrderId())){
						MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.sumofloadsmustbepositive");
						return false;
					}
					
					if ( MessageUtils.getActionWarnings(request) != null && !MessageUtils.getActionWarnings(request).isEmpty()) {
						//something is not OK, abort
						return false;
					}
					else {
						addWarningIfLoadingpointUnset(request, helpForm, order);
						if (MessageUtils.getActionWarnings(request) != null
								&& !MessageUtils.getActionWarnings(request).isEmpty()) {
							return false;
						}
					}
					Date [] loadingTimes = getLoadingTimes(isRealTimeLoading, loadId);
					//this is the real update - contains a part which will first write the loaded quantities of the items, and then call endload with the sum of them
					manualLoadingService.updateBagLoad(loadId, helpForm.getInputLoadingPointId(),
							loadingData, loadingTimes[0], loadingTimes[1]);
				}
			}
		}
		catch (NumberFormatException e){
			LOG.error("Could not parse measure value");
		}
		catch (LogonException le) {
			return handleLogonException(request, helpForm, order, le);
		}
		catch (RuntimeException re) {
			if (re.getCause() instanceof LogonException) {
				LogonException le = (LogonException)re.getCause();
				return handleLogonException(request, helpForm, order, le);
			}
			else {
				throw re;
			}
		}
		return true;
	}

	/**
	 * Retruns the loading start time and loading end time to set as loading real
	 * start.
	 * @param isRealTimeLoading
	 * @param loadId
	 * @return
	 */
	private Date[] getLoadingTimes(boolean isRealTimeLoading, Long loadId) {

		Date loadingStartTime = NO_LOADING_START_TIME;
		Date loadingEndTime = NO_LOADING_END_TIME;

		if (!isRealTimeLoading) {
			AssignmentPartEntity loadAssignmentPart = getServiceLocator().getLoadingService().findAssignmentPartForLoad(loadId);
			loadingStartTime = loadAssignmentPart.getPlannedStart();
			loadingEndTime =
					getServiceLocator().getOrderChangeService().getNextAssignmentPart(loadAssignmentPart.getAssignment().getAssignmentId(),loadAssignmentPart.getAssignmentPartSeqNo() +1).getPlannedStart();
		}
		return new Date []{loadingStartTime, loadingEndTime};
	}

	private boolean handleLogonException(HttpServletRequest request, AuftragBearbeitungForm helpForm, OrdItemAssignmtLoadEntity order, LogonException le) {
		Reason reason = le.getReason();
		if (reason != null && reason.getReason() != null && reason.getReason().equals(Reason.RES_ORDER_PROCESSING_ERROR.getReason())){
			MessageUtils.addActionError(request, le.getReason().toString());
			return false;
		}
		if (reason != null && reason.getReason() != null && reason.getReason().equals(Reason.RES_ERROR_INVALID_MEASUREMENT.getReason())){
			ShippBimodalRoadRailEntity shippBimodalRoadRailEntity = (ShippBimodalRoadRailEntity) helpForm.getBimodalRoadAndRelatedRailInfoMap().get(order.getOrderId());
			String addInfo1 = null;
			String addInfo2 = null;
			if ( shippBimodalRoadRailEntity != null ) {
				BigDecimal loadedIntoWagon= shippBimodalRoadRailEntity.getWagonLoadNetQuantity();
				Double sum = shippBimodalRoadRailEntity.getAlreadyLoadedQuantity();
				if ( loadedIntoWagon != null ) {
					addInfo1 = loadedIntoWagon.toString();
				}
				if ( sum != null ) {
					addInfo2 = sum.toString();
				}
			}
			MessageUtils.addActionError(request, "auftragbearbeitung.errors.bimodal.invalid.load.weight", addInfo1, addInfo2);
			return false;
		}
		else if (reason != null && reason.getReason() != null && reason.getReason().equals(Reason.RES_ERROR_INVALID_TRUCK_COMPOSITION.getReason())){
			MessageUtils.addActionError(request, le.getReason().toString());
			return false;
		}
		else {
			throw le;
		}
	}
	
	private boolean updateSimpleVehicleOrderData(ActionMapping mapping, HttpServletRequest request,
			AuftragBearbeitungForm helpForm, Long simpleVehicleId) {
		BigDecimal netQuantity = null;
		long loadId = helpForm.getInputLoadId();
				
		OrdItemAssignmtLoadEntity order = getOrderWithLoadId(helpForm, loadId);
		if ( helpForm.getInputLoadingPointId().equals(Long.valueOf(Constant.INVALID_KEY)) && order.getLoadingpointId() != null ) {
			helpForm.setInputLoadingPointId(order.getLoadingpointId());
		}
		try {
			Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping = helpForm.getOrderLoadingPointListMapping();
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo = helpForm.getBimodalRoadAndRelatedRailInfoMap();
			Map<Long, Date> truckNextMinPlannedStartMap = helpForm.getTruckNextOrderMapping();
			
			OrderChangeLogic orderChangeLogic = new OrderChangeLogic(truckNextMinPlannedStartMap, 
					orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo);
			
			SimpleVehicleSchedulingService simpleVehicleSchedulingService = getServiceLocator().getSimpleVehicleSchedulingService();
			
			/**
			 * CASE BULK
			 * In case of bulk, there are two important input fields: inputTaraMeasure and
			 * inputNettoMeasure. The tare is not required to be set. In case not set, the order
			 * service will set the truck's master data tare weight to the load.
			 */
			if (orderChangeLogic.disableSimpleChangeFunctionality(order, OrderChangeLogic.FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA) == false ||
					orderChangeLogic.disableSimpleChangeFunctionality(order, OrderChangeLogic.FUNCTIONALITY_UPDATE_TARA_MEASURE_DATA) == false){
				
				boolean isNetQuantitySet = StringUtils.notEmpty(helpForm.getInputNettoMeasureValue());
				boolean isTareWeightSet = StringUtils.notEmpty(helpForm.getInputTaraMeasureValue());
				
				if (isNetQuantitySet) {
					addWarningIfLoadingpointUnset(request, helpForm, order);
					if (MessageUtils.getActionWarnings(request) != null
							&& !MessageUtils.getActionWarnings(request).isEmpty()) {
						return false;
					}
				}
				netQuantity = isNetQuantitySet ? new BigDecimal(helpForm.getInputNettoMeasureValue()) : null;
				
				if (isTareWeightSet || isNetQuantitySet) {
					Map<Long, SimpleVehicleWeighingData> loadingData = new HashMap<Long, SimpleVehicleWeighingData>();
					String loadUnitOfMeasurement = request.getParameter(PARAMETER_BASE_UOM_CD);
					
					SimpleVehicleWeighingData weighingData = new SimpleVehicleWeighingData(
							order.getOrderItemId(), helpForm.getInputLoadingPointId(),
							netQuantity, loadUnitOfMeasurement);
					loadingData.put(order.getOrderItemId(), weighingData);

					simpleVehicleSchedulingService.loadSimpleVehicle(simpleVehicleId, loadingData);
				}
			}
			/**
			 * CASE BAG
			 * As far there may be more than one order items sharing the same load, those have one
			 * common save button, but separate input fields for setting the order item's loaded
			 * quantity. The load will get their sum at the end. They must share the same unit of
			 * measure.
			 */
			else if (orderChangeLogic.disableSimpleChangeFunctionality(order, OrderChangeLogic.FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY) == false){
				
				//parse the order item loaded quantities
				Map<Long, SimpleVehicleWeighingData> loadingData = parseLoadingDataForSimpleVehicle(order.getLoadId(), helpForm, request);
								
				if (loadingData != null) {
					
					if (isSumOfLoadedQuantitiesZero(loadingData, order.getOrderId())){
						MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.sumofloadsmustbepositive");
						return false;
					}
					
					if ( MessageUtils.getActionWarnings(request) != null && !MessageUtils.getActionWarnings(request).isEmpty()) {
						//something is not OK, abort
						return false;
					}
					else {
						addWarningIfLoadingpointUnset(request, helpForm, order);
						if (MessageUtils.getActionWarnings(request) != null
								&& !MessageUtils.getActionWarnings(request).isEmpty()) {
							return false;
						}
					}
					simpleVehicleSchedulingService.loadSimpleVehicle(simpleVehicleId, loadingData);
				}
			}
		}
		catch (NumberFormatException e){
			LOG.error("Could not parse measure value");
		}
		catch (RuntimeException re) {
			if (re.getCause() instanceof LogonException) {
				LogonException le = (LogonException)re.getCause();
				Reason reason = le.getReason();
				if (reason != null && reason.getReason() != null && reason.getReason().equals(Reason.RES_ORDER_PROCESSING_ERROR.getReason())){
					MessageUtils.addActionError(request, le.getReason().toString());
					return false;
				}
				if (reason != null && reason.getReason() != null && reason.getReason().equals(Reason.RES_ERROR_INVALID_MEASUREMENT.getReason())){
					Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfoMap = helpForm.getBimodalRoadAndRelatedRailInfoMap();
					ShippBimodalRoadRailEntity shippBimodalRoadRailEntity =  bimodalRoadAndRelatedRailInfoMap.get(order.getOrderId());
					String addInfo1 = null;
					String addInfo2 = null;
					if ( shippBimodalRoadRailEntity != null ) {
						BigDecimal loadedIntoWagon= shippBimodalRoadRailEntity.getWagonLoadNetQuantity();
						Double sum = shippBimodalRoadRailEntity.getAlreadyLoadedQuantity();
						if ( loadedIntoWagon != null ) {
							addInfo1 = loadedIntoWagon.toString();
						}
						if ( sum != null ) {
							addInfo2 = sum.toString();
						}
					}
					MessageUtils.addActionError(request, "auftragbearbeitung.errors.bimodal.invalid.load.weight", addInfo1, addInfo2);
					return false;
				}
				else if (reason != null && reason.getReason() != null && reason.getReason().equals(Reason.RES_ERROR_INVALID_TRUCK_COMPOSITION.getReason())){
					MessageUtils.addActionError(request, le.getReason().toString());
					return false;
				}
				else {
					throw le;
				}
			}
			else {
				throw re;
			}
		}
		return true;
	}


	/**
	 * @param request
	 * @param helpForm
	 * @param order
	 */
	private void addWarningIfLoadingpointUnset(HttpServletRequest request,
			AuftragBearbeitungForm helpForm, OrdItemAssignmtLoadEntity order) {
		List lpList = getServiceLocator().getOrderChangeService().fetchLoadingPoints(order.getShippingpointId(), order.getSapMaterialId());
		//in case there is no loadingpoint at all, skip warning! this is because of FVL
		if (lpList.size() == 1 ) {
			LdpointMatRestrEntity lp = (LdpointMatRestrEntity) lpList.get(0);
			helpForm.setInputLoadingPointId(lp.getLoadingpoint().getLoadingpointId());
		}
		
		if (!LogonUtil.isLoadingpointPreventedForShippingpointType(order.getShipppointTypeCd())) {
			//loadingpoint has to be set in any way
			if (helpForm.isLoadingpointUnset() || (helpForm.getInputLoadingPointId().equals(Constant.INVALID_KEY) && order.getLoadingpointId() == null) && (order.getWeighSystemLpCd() == null || order.getWeighSystemLpCd().equals(Constant.WEIGH_SYSTEM_LP_LOGON))) {
				MessageUtils.addActionWarning(request, "auftragbearbeitung.errors.emptyloadingpoint");
			}			
		}
	}


	/**
	 * @param helpForm
	 */
	private void updateDateIntervalSetting(AuftragBearbeitungForm helpForm) {
		Authorization.authorize(Authorization.SHIPPING_R);
		String searchToDate = helpForm.getSearchToDate();
		String searchFromDate = helpForm.getSearchFromDate();

		TimeZone deliveryPlantTz = helpForm.getShippingpointTZ();

		//happens when one of the date manipulation links (today/tomorrow/this week...) is clicked

		switch (helpForm.getSelectedDateOption()){

		case AuftragBearbeitungForm.DATE_TODAY_SELECTION:
			searchToDate = deliveryPlantTz.fmtAsDayMonthYear(new Date());
			searchFromDate = searchToDate;
			break;

		case AuftragBearbeitungForm.DATE_TOMORROW_SELECTION:
			searchFromDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.tomorrow());
			searchToDate = searchFromDate;
			break;

		case AuftragBearbeitungForm.DATE_THIS_WEEK_SELECTION:
			searchToDate = deliveryPlantTz.fmtAsDayMonthYear(new Date());
			searchFromDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.firstDayThisWeek());
			break;

		case AuftragBearbeitungForm.DATE_THIS_MONTH_SELECTION:
			searchToDate= deliveryPlantTz.fmtAsDayMonthYear(new Date());
			searchFromDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.firstDayThisMonth());
			break;

		case AuftragBearbeitungForm.DATE_THIS_YEAR_SELECTION:
			searchToDate= deliveryPlantTz.fmtAsDayMonthYear(new Date());
			searchFromDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.firstDayThisYear());
			break;

		case AuftragBearbeitungForm.DATE_ONE_DAY_FORWARD_SELECTION:
			Date toDate;
			try {
				toDate = deliveryPlantTz.parseDayMonthYear(helpForm.getSearchToDate());
			}
			catch (ParseException e1) {
				toDate = helpForm.getToDate();
			}
			searchToDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.tomorrow(toDate));
			break;

		case AuftragBearbeitungForm.DATE_ONE_DAY_BACKWARD_SELECTION:
			Date fromDate;
			try {
				fromDate = deliveryPlantTz.parseDayMonthYear(helpForm.getSearchFromDate());
			}
			catch (ParseException e1) {
				fromDate = helpForm.getFromDate();
			}			
			searchFromDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.yesterday(fromDate));
			break;
			
		case AuftragBearbeitungForm.DATE_CUSTOM_SELECTION:
			if(searchToDate.isEmpty()) {
				searchToDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.tomorrow());
			}
			if(searchFromDate.isEmpty()) {
				searchFromDate = deliveryPlantTz.fmtAsDayMonthYear(deliveryPlantTz.today());
			}
			break;

		default:
			LOG.error("Invalid date selection given");
		}

		helpForm.setSearchToDate(searchToDate);
		helpForm.setSearchFromDate(searchFromDate);
		try {
			//set the real search date according to the input, but leave it untouched outside on the GUI
			helpForm.setFromDate(deliveryPlantTz.parseDayMonthYear(searchFromDate));

			Calendar calendar = deliveryPlantTz.asCalendar();
			calendar.setTime(deliveryPlantTz.parseDayMonthYear(searchToDate));
			//to date is one day forward's
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.SECOND, 1);

			Date toDate = calendar.getTime();
			helpForm.setToDate(toDate);
		}
		catch (ParseException e) {
			LOG.error("Parse error when parsing the date search field");
		}
	}

}

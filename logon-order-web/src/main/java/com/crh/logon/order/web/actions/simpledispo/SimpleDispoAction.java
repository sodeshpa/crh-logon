/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.simpledispo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.forms.simpledispo.SimpleVehicleLoadBean;
import com.crh.logon.order.web.forms.simpledispo.SimpleVehicleLoadSettingsDTO;
import com.crh.logon.order.web.forms.simpledispo.SimpleVehicleUnloadBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SimpleVehicleAssignmentInfo;
import com.crh.logon.business.services.beans.SimpleVehicleAssignmentPart;
import com.crh.logon.business.services.beans.SimpleVehicleLoadableItem;
import com.crh.logon.business.services.beans.SimpleVehicleWeighingData;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.SimpleVehicleSchedulingService;
import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.order.web.forms.simpledispo.SimpleDispoForm;
import com.crh.logon.order.web.forms.simpledispo.SimpleVehicleDeallocateBean;
import com.crh.logon.order.web.forms.simpledispo.SimpleVehicleUnloadSettingsDTO;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.views.SimpvehOrdItemMatEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * Struts Action for Simple Vehicle Scheduling
 * 
 * @author AdNovum Informatik AG
 * @version     $Revision$ $Date$
 * 
 * @struts.action name="order_simpledispo_SimpleDispoForm"
 *  path="/simpledispo/SimpleDispo" scope="session"
 *  validate="false"
 *  
 *  @struts.action-forward name="input" path="SimpleDispoTile.def"
 *  @struts.action-forward name="showLoadDialog" path="SimpleDispoLoadPopupTile.def"
 *  @struts.action-forward name="closeLoadDialog" path="SimpleDispoCloseLoadPopupTile.def"
 *  @struts.action-forward name="showUnloadDialog" path="SimpleDispoUnloadPopupTile.def"
 *  @struts.action-forward name="showDeallocateDialog" path="SimpleDispoDeallocatePopupTile.def"
 *  @struts.action-forward name="entryAction" path="/simpledispo/SimpleDispoEntry.do"
 */
public class SimpleDispoAction extends SimpleDispoAbstractAction {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleDispoAction.class);
	
	public static final String FORWARD_INPUT = "input";
	public static final String FORWARD_SELECT_POOL = "selectPool";
	public static final String FORWARD_SELECT_CARRIER = "selectCarrier";
	public static final String FORWARD_ASSIGN_ORDER_TO_VEHICLE = "assignOrderToVehicle";
	public static final String FORWARD_CHANGE_VEHICLE_PRIORITY = "changeVehiclePriority";
	public static final String FORWARD_SHOW_LOAD_DIALOG = "showLoadDialog";
	public static final String FORWARD_SHOW_UNLOAD_DIALOG = "showUnloadDialog";
	public static final String FORWARD_SHOW_DEALLOCATE_DIALOG = "showDeallocateDialog";
	public static final String FORWARD_RELEASE_ASSIGNMENT = "releaseAssignment";
	public static final String FORWARD_LOAD_VEHICLE = "loadVehicle";
	public static final String FORWARD_UNLOAD_VEHICLE = "unloadVehicle";
	public static final String FORWARD_DEALLOCATE = "deallocate";
	public static final String FORWARD_ONE_ORDER = "deallocateOneOrder";
	public static final String FORWARD_CLOSE_LOAD_DIALOG = "closeLoadDialog";
	public static final String FORWARD_ENTRY_ACTION = "entryAction";
	
	
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
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_R);
			setSelectedTab("simpledispo", request);

			SimpleDispoForm helpForm = (SimpleDispoForm)form;
			String forwardName = FORWARD_INPUT;
			
			/* Reroute to entry action if not everything has been loaded.
			 * This happens e.g. if someone enters the action URL directly instead of
			 * going through the entry action.
			 */
			if (helpForm.getPools() == null) {
				return result = mapping.findForward(FORWARD_ENTRY_ACTION);
			}

			String inputAction = request.getParameter("inputAction");
			if (inputAction == null) {
				inputAction = FORWARD_INPUT;
			}
			
			SimpleVehicleSchedulingService simpleVehicleSchedulingService = getServiceLocator().getSimpleVehicleSchedulingService();
			
			if (inputAction.equals(FORWARD_SELECT_POOL)) {
				changePoolSelection(simpleVehicleSchedulingService, helpForm);
			}
			else if (inputAction.equals(FORWARD_ASSIGN_ORDER_TO_VEHICLE)) {
				try {
					assignOrderToVehicle(simpleVehicleSchedulingService, helpForm, request);
				}
				catch (EJBException ex) {
					String text = "simpledispo.message.allocate.failed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if (inputAction.equals(FORWARD_CHANGE_VEHICLE_PRIORITY)) {
				try {
					changeVehiclePriority(simpleVehicleSchedulingService, helpForm, request);
				}
				catch (EJBException ex) {
					String text = "simpledispo.message.priochange.failed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if (inputAction.equals(FORWARD_SHOW_LOAD_DIALOG)) {
				try {
					initLoadBean(simpleVehicleSchedulingService, helpForm, request);
					forwardName = FORWARD_SHOW_LOAD_DIALOG;
				} 
				catch (EJBException ex) {
					String text = "simpledispo.message.load.initfailed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if (inputAction.equals(FORWARD_RELEASE_ASSIGNMENT)) {
				try {
					releaseAssignment(simpleVehicleSchedulingService, helpForm, request);
				}
				catch (EJBException ex) {
					String text = "simpledispo.message.release.failed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if (inputAction.equals(FORWARD_LOAD_VEHICLE)) {
				try {
					performVehicleLoading(simpleVehicleSchedulingService, helpForm, request);
					forwardName = FORWARD_CLOSE_LOAD_DIALOG;
				} 
				catch (EJBException ex) {
					String text = "simpledispo.message.load.failed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
					// Re-display the load dialog for the user
					forwardName = FORWARD_SHOW_LOAD_DIALOG;
				}
			}
			else if (inputAction.equals(FORWARD_SHOW_DEALLOCATE_DIALOG)) {
				try {
					initDeallocateBean(simpleVehicleSchedulingService, helpForm, request);
					forwardName = FORWARD_SHOW_DEALLOCATE_DIALOG;
				} 
				catch (EJBException ex) {
					String text = "simpledispo.message.deallocate.initfailed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if (inputAction.equals(FORWARD_DEALLOCATE)) {
				try {
					deallocateOrders(simpleVehicleSchedulingService, helpForm);
				} 
				catch (EJBException ex) {
					String text = "simpledispo.message.deallocate.failed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if(inputAction.equals(FORWARD_ONE_ORDER)) {
				try {
					deallocateOneOrder(simpleVehicleSchedulingService, helpForm, request);
				} 
				catch (EJBException ex) {
					String text = "simpledispo.message.deallocate.oneorder.failed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if (inputAction.equals(FORWARD_SHOW_UNLOAD_DIALOG)) {
				try {
					initializeUnloadBean(simpleVehicleSchedulingService, helpForm, request);
					forwardName = FORWARD_SHOW_UNLOAD_DIALOG;
				} 
				catch (EJBException ex) {
					String text = "simpledispo.message.unload.initfailed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
				}
			}
			else if (inputAction.equals(FORWARD_UNLOAD_VEHICLE)) {
				try {
					triggerUnload(simpleVehicleSchedulingService, helpForm, request);
					// We could use the same forward to close the unload as the load.
					forwardName = FORWARD_CLOSE_LOAD_DIALOG;
				} 
				catch (EJBException ex) {
					String text = "simpledispo.message.unload.failed";
					text = ExceptionUtil.getLogonExceptionReasonKey(ex, text);
					MessageUtils.addActionError(request, text);
					// Re-display the load dialog for the user
					forwardName = FORWARD_SHOW_UNLOAD_DIALOG;
				}
			}
			
			//(Re)load vehicle list
			loadVehicles(simpleVehicleSchedulingService, helpForm, request.getLocale());
			
			// Reload order list
			loadOrders(simpleVehicleSchedulingService, helpForm, request.getLocale());
			
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
	 * The user changed the selected pool, this method loads the carriers for that.
	 */
	private void changePoolSelection(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "changePoolSelection", params);
		}
		// ]]
		try {
			Long selectedPoolId = helpForm.getSelectedPoolId();
			
			Collection<DispoCarrierWrapper> carriers = simpleVehicleSchedulingService.getCarriers(selectedPoolId);
			helpForm.setCarriers(carriers);
			
			helpForm = SimpleDispoEntryAction.setAllCarriersSelected(carriers, helpForm);
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
	 * Gets and returns the Long value of a request parameter for the given name. 
	 */
	private static Long getRequestParameterLong(HttpServletRequest request, String paramName) {
		// [[
		Long result1 = null;
		Object[][] params = {{"request", request},
							{"paramName", paramName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SimpleDispoAction.class, "getRequestParameterLong", params);
		}
		// ]]
		try {
			Long result = -1l;
			try {
				result = Long.parseLong(request.getParameter(paramName));
			}
			catch (NumberFormatException ex) {
				MessageUtils.addActionError(request, "simpledispo.message.paramparse.failed");
				LOG.error("Could not parse the request parameter: " + paramName);
			}
			return result1 = result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	/**
	 * Gets and returns the selected vehicle from the request.
	 */
	private static SimpleVehicleAssignmentInfo getSelectedVehicle(HttpServletRequest request, SimpleDispoForm helpForm) {
		// [[
		SimpleVehicleAssignmentInfo result = null;
		Object[][] params = {{"request", request},
							{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SimpleDispoAction.class, "getSelectedVehicle", params);
		}
		// ]]
		try {
			Long selectedVehicleId = getRequestParameterLong(request, "simpleVehicleId");
			
			SimpleVehicleAssignmentInfo selectedVehicle = null;
			Collection<SimpleVehicleAssignmentInfo> vehicles = helpForm.getVehicles();
			for (SimpleVehicleAssignmentInfo vehicle : vehicles) {
				if (vehicle.getVehicleId().equals(selectedVehicleId)) {
					selectedVehicle = vehicle;
					break;
				}
			}
			return result = selectedVehicle;
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
	 * Triggers the assigning of the order to the simple vehicle.
	 */
	private void assignOrderToVehicle(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "assignOrderToVehicle", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			Long orderId = getRequestParameterLong(request, "orderId");
			Long simpleVehicleId = getRequestParameterLong(request, "simpleVehicleId");
			
			simpleVehicleSchedulingService.assignOrderToVehicle(orderId, simpleVehicleId);
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
	 * Changes the priority of the vehicle. 
	 */
	private void changeVehiclePriority(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "changeVehiclePriority", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			Long simpleVehicleId = getRequestParameterLong(request, "simpleVehicleId");
			Long priority = getRequestParameterLong(request, "priority");
			
			simpleVehicleSchedulingService.setVehiclePriority(simpleVehicleId, priority);
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
	 * Initializes the data for the Load Popup.
	 */
	private void initLoadBean(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initLoadBean", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			Long selectedVehicleId = getRequestParameterLong(request, "simpleVehicleId");
			
			Collection<SimpleVehicleLoadableItem> simpvehLoadItemsEntities = 
					simpleVehicleSchedulingService.getLoadableItems(selectedVehicleId, request.getLocale());
			
			SimpleVehicleLoadBean loadBean = new SimpleVehicleLoadBean(selectedVehicleId, simpvehLoadItemsEntities);
			helpForm.setSimpleVehicleLoadBean(loadBean);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private void releaseAssignment(SimpleVehicleSchedulingService svss,
			SimpleDispoForm helpForm, HttpServletRequest request)
	{
		Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
		
		Long assignmentId = getRequestParameterLong(request, "assignmentId");
		
		svss.releaseAssignment(assignmentId);
	}
	
	/**
	 * Triggers the loading of the selected Simple Vehicle.
	 */
	private void performVehicleLoading(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "performVehicleLoading", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			SimpleVehicleLoadBean vehicleLoadBean = helpForm.getSimpleVehicleLoadBean();
			
			Long selectedVehicleId = vehicleLoadBean.getSimpleVehicleId();
			
			Map<Long, SimpleVehicleWeighingData> orderItemMap = 
					new HashMap<Long, SimpleVehicleWeighingData>();
			
			Collection<SimpleVehicleLoadSettingsDTO> userSettings = vehicleLoadBean.getUserSettings();
			for (SimpleVehicleLoadSettingsDTO loadSetting : userSettings) {
				SimpleVehicleWeighingData weighingData = new SimpleVehicleWeighingData(
						loadSetting.getOrderItemId(), loadSetting.getLoadingPointId(),
						loadSetting.getLoadedQty());
				orderItemMap.put(loadSetting.getOrderItemId(), weighingData);
			}
			
			simpleVehicleSchedulingService.loadSimpleVehicle(selectedVehicleId, orderItemMap);
			MessageUtils.addActionMessage(request, "simpledispo.message.load.success");
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
	 * Initializes the data for the Deallocate Popup.
	 * @return boolean - true if need to open a popup for deallocation, false otherwise.
	 * @throws IllegalStateException if the vehicle doesn't have any allocated order to deallocate.
	 */
	private void initDeallocateBean(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initDeallocateBean", params);
		}
		// ]]
		try {			
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			SimpleVehicleAssignmentInfo selectedVehicle = getSelectedVehicle(request, helpForm);
			
			// If the vehicle has one order scheduled, then no need for the popup, deallocate directly.
			Collection<SimpleVehicleAssignmentPart> parts = selectedVehicle.getParts(); 
			if (parts.size() > 1) {
				try {
					SimpleVehicleDeallocateBean deallocateBean = new SimpleVehicleDeallocateBean(selectedVehicle);
					helpForm.setSimpleVehicleDeallocateBean(deallocateBean);
				}
				catch (IllegalStateException e) {
					LOG.warn("The vehicle {0} doesn't have any allocated order to deallocate.", new Object[]{selectedVehicle.getVehicleId()});
					MessageUtils.addActionWarning(request, "simpledispo.message.deallocate.noallocatedorders");
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
 
	/**
	 * Deallocates the selected orders.
	 * This method is called after the user selected the orders on the deallocate popup.
	 */
	private void deallocateOrders(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deallocateOrders", params);
		}
		// ]]
		try {			
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			final SimpleVehicleDeallocateBean deallocateBean = helpForm.getSimpleVehicleDeallocateBean();
			final String[] selectedOrderIds = deallocateBean.getSelectedOrderIds();
			final Collection<SimpleVehicleAssignmentPart> assigmentParts = deallocateBean.getSimpleVehicle().getParts();
			
			Set<Long> orderIds = new HashSet<Long>();
			
			int idx = 0;
			for (SimpleVehicleAssignmentPart part : assigmentParts) {
				String selected = selectedOrderIds[idx];
				if (selected != null && selected.equals("on")) {
					orderIds.add(part.getOrderId());
				}
				idx++;
			}
			simpleVehicleSchedulingService.deallocateOrders(orderIds);
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
	 * Deallocates only one order from the selected vehicle.
	 * This method is called instead of the deallocate popup.
	 */
	private void deallocateOneOrder(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deallocateOneOrder", params);
		}
		// ]]
		try {			
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			final SimpleVehicleAssignmentInfo selectedVehicle = getSelectedVehicle(request, helpForm);
			
			Collection<SimpleVehicleAssignmentPart> parts = selectedVehicle.getParts(); 
			if (parts.size() == 1) {
				Long orderId = null;
				for (SimpleVehicleAssignmentPart part : parts) {
					orderId = part.getOrderId();
				}
				Set<Long> orderIds = new HashSet<Long>();
				orderIds.add(orderId);
				
				simpleVehicleSchedulingService.deallocateOrders(orderIds);
			}
			else {
				LOG.error("The vehicle {0} has more than one order assigned.", new Object[]{selectedVehicle.getVehicleId()});
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
	 * Initializes the data for the Unload Popup.
	 */
	private void initializeUnloadBean(SimpleVehicleSchedulingService simpleVehicleSchedulingService,
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initializeUnloadBean", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			Long orderId = getRequestParameterLong(request, "orderId");

			List<SimpvehOrdItemMatEntity> items = simpleVehicleSchedulingService.getUnloadableItems(
					orderId, request.getLocale());

			OrderService orderService = getServiceLocator().getOrderService();
			OrderEntity order = orderService.findOrderById(orderId);
			Boolean releaseToSapFlag = order.getReleaseSapFlag();

			SimpleVehicleUnloadBean simpleVehicleUnloadBean = new SimpleVehicleUnloadBean(orderId,
					items, releaseToSapFlag);

			helpForm.setSimpleVehicleUnloadBean(simpleVehicleUnloadBean);
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
	 * Triggers the unload of the selected order.
	 */
	private void triggerUnload(SimpleVehicleSchedulingService simpleVehicleSchedulingService, 
			SimpleDispoForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"simpleVehicleSchedulingService", simpleVehicleSchedulingService},
							{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "triggerUnload", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.SIMPLE_VEHICLE_DISPATCHING_W);
			
			SimpleVehicleUnloadBean simpleVehicleUnloadBean = helpForm.getSimpleVehicleUnloadBean();
			
			Long orderId = simpleVehicleUnloadBean.getSelectedOrderId();
			Boolean finalizeToSap = simpleVehicleUnloadBean.getInitialReleaseToSAPFlag();

			Collection<SimpleVehicleUnloadSettingsDTO> userSettings = simpleVehicleUnloadBean.getUserSettings();

			Map<Long, BigDecimal> unloadedQuantities = new HashMap<Long, BigDecimal>(
					simpleVehicleUnloadBean.getItems().size());
			
			for (SimpleVehicleUnloadSettingsDTO userSetting : userSettings) {
				unloadedQuantities.put(userSetting.getOrderItemId(),
						userSetting.getDeliveredQuantity());
			}
			
			simpleVehicleSchedulingService.unloadOrder(orderId, unloadedQuantities, finalizeToSap);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.actions.pickup;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.terminal.web.forms.pickup.PickupCustorderBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.interfaces.CustorderService;
import com.crh.logon.business.services.interfaces.DeliverySettingService;
import com.crh.logon.business.services.interfaces.PickupService;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.LogonPrintException;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.terminal.web.actions.TerminalActionSupport;
import com.crh.logon.terminal.web.forms.pickup.PickupCustorderItemBean;
import com.crh.logon.terminal.web.forms.pickup.PickupForm;
import com.crh.logon.terminal.web.forms.pickup.PickupPalletOrderItemBean;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="terminal_PickupForm"
 *  path="/pickup/Pickup" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="fetchfirst" path="/pickup/PickupEntry.do"
 * @struts.action-forward name="shiptoSelector" path="PickupShiptoSelector.def"
 * @struts.action-forward name="billtoSelector" path="PickupBilltoSelector.def"
 * @struts.action-forward name="productSelector" path="PickupProductSelector.def"
 * @struts.action-forward name="productTypeSelector" path="PickupProductTypeSelector.def"
 * @struts.action-forward name="quantitySelector" path="PickupQuantitySelector.def"
 * @struts.action-forward name="showOrder" path="PickupOrderTile.def"
 * @struts.action-forward name="returnPallets" path="ReturnPalletsTile.def"
 * @struts.action-forward name="paletteQuantity" path="PalletsTile.def"
 * @struts.action-forward name="addNote" path="PickupDeliveryNoteTile.def"
 * @struts.action-forward name="counter" path="LogoutCounter.def"
 * 
 */
public class PickupAction extends TerminalActionSupport {

	public static final int maxRowNumber = 100;

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(PickupAction.class);

	public static final String ACTION_CREATE_NEW_PICKUP = "createPickup";
	public static final String ACTION_EDIT_PICKUP = "editPickup";
	public static final String ACTION_SAVE_NEW_PICKUP_AND_PRINT = "saveNewPickupAndPrint";
	public static final String ACTION_SAVE_PICKUP_AND_PRINT = "savePickupAndPrint";
	public static final String ACTION_SELECT_QUANTITY = "quantity";
	public static final String ACTION_ADD_ITEM = "addItem";
	public static final String ACTION_REMOVE_ITEM = "removeItem";
	public static final String ACTION_SELECT_PRODUCT = "selectProduct";
	public static final String ACTION_SELECT_PRODUCT_TYPE = "selectProductType";
	public static final String ACTION_SELECT_SHIPTO = "selectShipto";
	public static final String ACTION_SELECT_BILLTO = "selectBillto";
	public static final String ACTION_SELECT_RETURN_PALET_QUANTITY = "returnPalets";
	public static final String ACTION_CHANGE_RETURN_PALET_QUANTITY = "changeReturnPalets";
	public static final String ACTION_CHANGE_PALET_QUANTITY = "changePalets";
	public static final String ACTION_SELECT_PALET_QUANTITY = "palets";
	public static final String ACTION_CANCEL_ADDITEM = "cancelAdditem";
	public static final String ACTION_CANCEL_ADDED_ITEM = "cancelAddedItem";
	public static final String ACTION_CANCEL_CREATE = "cancelCreate";
	public static final String ACTION_CANCEL_EDIT = "cancelEdit";
	public static final String ACTION_CANCEL_DEFINE_RETURN_PALLET = "cancelReturn";
	public static final String ACTION_CANCEL_DELIVERY_NOTE = "cancelNote";
	public static final String ACTION_PRINT_DELIVERY_NOTE_AND_FINISH = "print";
	public static final String ACTION_DELIVERY_NOTE = "addNote";
	public static final String ACTION_SET_DELIVERY_NOTE = "setNote";

	private static final String FORWARD_SHOW_ORDER = "showOrder";
	private static final String FORWARD_MAIN = "fetchfirst";
	private static final String FORWARD_PRODUCT_SELECTOR = "productSelector";
	private static final String FORWARD_PRODUCT_TYPE_SELECTOR = "productTypeSelector";
	private static final String FORWARD_SHIPTO_SELECTOR = "shiptoSelector";
	private static final String FORWARD_BILLTO_SELECTOR = "billtoSelector";
	private static final String FORWARD_QUANTITY_SELECTOR = "quantitySelector";
	private static final String FORWARD_RETURN_PALETS = "returnPallets";
	private static final String FORWARD_PALETS = "paletteQuantity";
	private static final String FORWARD_DELIVERY_NOTE = "addNote";
	private static final String FORWARD_COUNTER = "counter";
	private static final String FORWARD_FATAL_ERROR = "fatalError";
	
	public static final String ACTION_URL = "pickup/Pickup.do";


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
			Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_R);
			PickupForm pickupForm = (PickupForm) form;
			
			HashMap<Integer, String> productTypeMap = initProductTypeMap(request);
			pickupForm.setProductTypeMap(productTypeMap);
			
			String inputAction = request.getParameter("inputAction");
			Locale locale = request.getLocale();
			if ( inputAction == null ) {
				return result = mapping.findForward(FORWARD_MAIN);
			}
			
			String forward;
			if (inputAction.equals(ACTION_CREATE_NEW_PICKUP)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = createNewPickupCustorder(pickupForm, locale);
			}
			else if (inputAction.equals(ACTION_SELECT_SHIPTO)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = selectShipto(pickupForm, locale);
			}
			else if (inputAction.equals(ACTION_CHANGE_RETURN_PALET_QUANTITY)) {
				forward = FORWARD_RETURN_PALETS;
			} 
			else if (inputAction.equals(ACTION_CHANGE_PALET_QUANTITY)) {
				forward = FORWARD_PALETS;
			}
			else if (inputAction.equals(ACTION_CANCEL_DEFINE_RETURN_PALLET)) {
				forward = FORWARD_SHOW_ORDER;
			}
			else if (inputAction.equals(ACTION_SELECT_RETURN_PALET_QUANTITY)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = selectReturnPalletQuantity(pickupForm);
			}
			else if (inputAction.equals(ACTION_SELECT_PALET_QUANTITY)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = selectPalletQuantity(pickupForm);
				
			}
			else if (inputAction.equals(ACTION_ADD_ITEM)) {				
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = FORWARD_PRODUCT_SELECTOR;
			}
			/**
			 * Before selecting the new material (or material type), user hits Cancel...
			 */
			else if (inputAction.equals(ACTION_CANCEL_ADDITEM)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = FORWARD_SHOW_ORDER;
			}
			/**
			 * After selecting the new material, user hits Cancel...
			 */
			else if (inputAction.equals(ACTION_CANCEL_ADDED_ITEM)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = removeCustorderItem(pickupForm);
			}
			/**
			 * After adding the new material, user hits Delete...
			 */
			else if (inputAction.equals(ACTION_REMOVE_ITEM)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = removeCustorderItem(pickupForm);
			}
			else if (inputAction.equals(ACTION_DELIVERY_NOTE)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = FORWARD_DELIVERY_NOTE;
			}
			else if (inputAction.equals(ACTION_SET_DELIVERY_NOTE)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = setDeliveryNote(pickupForm);;
			}
			else if (inputAction.equals(ACTION_CANCEL_DELIVERY_NOTE)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = FORWARD_SHOW_ORDER;
			}
			else if (inputAction.equals(ACTION_SELECT_PRODUCT_TYPE)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = FORWARD_PRODUCT_SELECTOR;
			}			
			else if (inputAction.equals(ACTION_SELECT_PRODUCT)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = selectProduct(pickupForm);
			}			
			else if (inputAction.equals(ACTION_SELECT_QUANTITY)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);	
				forward = selectQuantity(pickupForm);
			}	
			else if (inputAction.equals(ACTION_CANCEL_CREATE)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = clearPickupCustorderBean(pickupForm);
			}	
			else if (inputAction.equals(ACTION_CANCEL_EDIT)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = clearPickupCustorderBean(pickupForm);
			}
			else if (inputAction.equals(ACTION_PRINT_DELIVERY_NOTE_AND_FINISH)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				//as far print delivery note flag is always ON for pickup orders at endload the print will happen
				forward = loadAndPrintPickupCustorder(request, pickupForm);
			}	
			else if (inputAction.equals(ACTION_SELECT_BILLTO)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);			
				forward = selectBillto(pickupForm);
			}
			else if ( inputAction.equals(ACTION_SAVE_NEW_PICKUP_AND_PRINT) ) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER);
				forward = savePickupCustorderAndPrint(request, pickupForm);
			}
			else if (inputAction.equals(ACTION_SAVE_PICKUP_AND_PRINT)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = savePickupCustorderAndPrint(request, pickupForm);
			}
			else if (inputAction.equals(ACTION_EDIT_PICKUP)) {
				Authorization.authorize(Authorization.PICKUP_ORDER_TERMINAL_W);
				forward = editPickupCustorder(pickupForm);
			}
			else {
				forward = FORWARD_MAIN;
			}
			setAvailableDeliverySettings(pickupForm);
			Boolean ordersLoadableTerminalFlag = getPickupLoadingpoint().getOrdersLoadableTerminalFlag();
			pickupForm.setOrdersLoadableTerminalFlag(ordersLoadableTerminalFlag);
			String orderValidationCode = getOrderValidationCode(pickupForm);
			pickupForm.setOrderValidationCode(orderValidationCode);
			return result = mapping.findForward(forward);
		}
		catch (Exception e) {
			LOG.error(getExceptionString(e));
			return result = mapping.findForward(FORWARD_FATAL_ERROR);	
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
	 * Prepares a map filled with {material type code -> material type label} associations.
	 * 
	 * @param request
	 * @return
	 */
	private static HashMap<Integer, String> initProductTypeMap(HttpServletRequest request) {
		HashMap<Integer,String> productTypes = new HashMap<Integer, String>();
		String bulkOrder = MessageUtils.getMessage(request, "pickup.button.bulkorder");
		productTypes.put(Constant.PRODUCT_TYPE_BULK, bulkOrder);
		String bagOrder = MessageUtils.getMessage(request, "pickup.button.bagorder");
		productTypes.put(Constant.PRODUCT_TYPE_BAG, bagOrder);
		return productTypes;
	}
	
	private String selectQuantity(PickupForm pickupForm) {
		PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
		String quantity = pickupForm.getInputQuantity();
		
		String index = pickupForm.getSelectedCustorderItemBeanIndex();
		
		PickupCustorderItemBean itemBean = (PickupCustorderItemBean) custorderBean.getCustorderItemBeanList().get(Integer.valueOf(index));
		try {					
			itemBean.setQuantity(new BigDecimal(quantity));
		}
		catch (NumberFormatException e) {
			return FORWARD_QUANTITY_SELECTOR;
		}
		custorderBean = recreatePickupOrderBean(pickupForm);
		if ( custorderBean.getBilltoSapBp() == null ) { 
			if (pickupForm.getCustorderBean().getBilltoList() != null && pickupForm.getCustorderBean().getBilltoList().size() == 1) {
				custorderBean.setBilltoSapBp(((ShiptoBilltoEntity)pickupForm.getCustorderBean().getBilltoList().get(0)).getBilltoSapBp());
				return FORWARD_SHOW_ORDER;
			}
			return FORWARD_BILLTO_SELECTOR;	
		}
		else{
			return FORWARD_SHOW_ORDER;
		}
	}
	
	private String savePickupCustorderAndPrint(HttpServletRequest request, PickupForm pickupForm) {
		// [[
		String result = null;
		Object[][] params = {{"request", request},
							{"pickupForm", pickupForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createOrUpdatePickupCustorder", params);
		}
		// ]]
		try {
			result = FORWARD_SHOW_ORDER;
			PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
			CustorderEntity detachedCustorder = custorderBean.getCustorderEntity();
			Long detachedCustorderId = detachedCustorder.getCustorderId();
			
			validateOrderSetWarnings(request, custorderBean, pickupForm);
			boolean noErrorsWarnings = noErrorWarningPresent(request);
			if (!noErrorsWarnings) {
				return result;
			}
			
			try {
				PickupLoadingpointEntity pickupLoadingpoint = getPickupLoadingpoint();
				String serviceProcessIndicator = null;
				List<PickupCustorderItemBean> custorderItemBeanList = custorderBean.getCustorderItemBeanList();
				if (custorderItemBeanList != null && !custorderItemBeanList.isEmpty()) {
					PickupCustorderItemBean firstCustorderItemBean = custorderItemBeanList.get(0);
					final DeliverySettingDTO deliverySettings = firstCustorderItemBean.getDeliverySetting();
					serviceProcessIndicator = deliverySettings.getServiceProcessIndicatorId();
				}
				PickupService pickupService = getServiceLocator().getPickupService();
				
				for (OrderEntity order : detachedCustorder.getOrders()) {
					// On the pickup terminal we can order materials only with existing dispocard
					order.setDelivTypePlanActual(Boolean.TRUE);
					for (OrderItemEntity item : order.getOrderItems()) {
						// On pickup terminal the delivery type cannot change. It's always based on the dispocard (always pickup)
						item.setDispocardAvailalbe(Boolean.TRUE);
						// The quantity is always typed in by the customer, so this flag is always false.
						item.setOrderPlanActual(Boolean.FALSE);
					}
				}
				
				CustorderEntity updatedCustorder = pickupService.saveAndLoadPickupCustorder(
								detachedCustorder,
								pickupLoadingpoint,
								serviceProcessIndicator);
				try {
					LoadingpointEntity loadingpoint = pickupLoadingpoint.getLoadingpoint();
					pickupService.generateAndSendPDFsOfCustorder(updatedCustorder, loadingpoint);
				}
				catch (LogonPrintException logonPrintException) {
					MessageUtils.addActionError(request, "terminal.common.printfailed");
					result = FORWARD_MAIN;
				}
				
				boolean isPickupCustorderEffectiveLoadable =
						OrderUtils.isPickupCustorderEffectiveLoadable(
								detachedCustorder,
								pickupLoadingpoint);
				request.setAttribute("loadableTerminal", isPickupCustorderEffectiveLoadable);
				
				String orderValidationCode = getOrderValidationCode(pickupForm);
				List<OrderEntity> orders = updatedCustorder.getOrders();
				String orderNumber = getOrderNumber(orders);
				/*
				 * If multiple validation codes belong to this order, do not show the order number
				 * and the validation code on the logout popup window.
				 */
				if (orderValidationCode.contains(",")) {
					orderValidationCode = null;
					orderNumber = null;
				}
				request.setAttribute("orderValidationCode", orderValidationCode);
				request.setAttribute("orderNumber", orderNumber);
			}
			catch (ValidationException ve) {
				processErrors(request, ve, CallerContext.PICKUP_TERMINAL, false);
			}
			catch (Exception e) {
				String exceptionString = getExceptionString(e);
				LOG.error(exceptionString);
				boolean newCustorder = (detachedCustorderId == null);
				String actionError = (newCustorder ? "pickup.create.error" : "pickup.edit.error");
				MessageUtils.addActionError(request, actionError);
			}
			
			noErrorsWarnings = noErrorWarningPresent(request);
			
			if (noErrorsWarnings) {
				pickupForm.clearCreate();
				result = FORWARD_COUNTER;
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

	public static String getOrderValidationCode(PickupForm pickupForm) {
		PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
		if (custorderBean == null) {
			return "";
		}
		CustorderEntity custorderEntity = custorderBean.getCustorderEntity();
		List<OrderEntity> orders = custorderEntity.getOrders();
		return TerminalActionSupport.getOrderValidationCode(orders);
	}

	private String editPickupCustorder(PickupForm pickupForm) {
		Long custorderId = pickupForm.getInputCustOrderId();
		if (custorderId == null) {
			return FORWARD_MAIN;
		}
		CustorderEntity custOrder = getServiceLocator().getCustorderService().findCustorderById(custorderId);
		PickupCustorderBean custorderBean = new PickupCustorderBean(custOrder, getLoadingpoint().getShippingpoint(), getLoadingpoint());
		pickupForm.setOrderBean(custorderBean);
		return FORWARD_SHOW_ORDER;
	}

	private static String selectBillto(PickupForm pickupForm) {
		PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
		String sapBpId = pickupForm.getInputBilltoId();
		List billtoList = pickupForm.getCustorderBean().getBilltoList();
		Iterator it = billtoList.iterator();
		while (it.hasNext()) {
			ShiptoBilltoEntity shiptoBillto = (ShiptoBilltoEntity) it.next();
			if (shiptoBillto.getBilltoSapBp().getSapBpId().equals(Long.valueOf(sapBpId))){						
				custorderBean.setBilltoSapBp(shiptoBillto.getBilltoSapBp());
				break;
			}
		}
		return FORWARD_SHOW_ORDER;
	}

	private static String selectProduct(PickupForm pickupForm) {
		String compositeId = pickupForm.getCompositeId();
		try{
			DeliverySettingDTO deliverySetting = pickupForm.getCustorderBean().getValidDeliverySettings().get(compositeId);
			PickupCustorderItemBean itemBean = new PickupCustorderItemBean(deliverySetting);
			pickupForm.getCustorderBean().addCustorderItem(itemBean);
			pickupForm.setSelectedCustorderItemBeanIndex(pickupForm.getCustorderBean().getCustorderItemBeanList().indexOf(itemBean) + "");
		}
		catch (NumberFormatException e) {
			//pickupForm.getOrderBean().removeItem(compositeId);
			return FORWARD_PRODUCT_SELECTOR;	
		}
		return FORWARD_QUANTITY_SELECTOR;
	}
	
	private String removeCustorderItem(PickupForm pickupForm) {
		PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
		
		if (custorderBean.getCustorderItemBeanList().size() == 1) {
			return clearPickupCustorderBean(pickupForm);
		}
		else {
			final String indexStr;
			final int idx;
			try {
				indexStr = pickupForm.getSelectedCustorderItemBeanIndex();
				idx = Integer.valueOf(indexStr);
			}
			catch (NumberFormatException e) {
				return FORWARD_SHOW_ORDER;
			}
			
			PickupCustorderItemBean itemBean = custorderBean.getCustorderItemBeanList().get(idx);
			custorderBean.getCustorderEntity().getCustorderItems().remove(itemBean.getItemEntity());
			custorderBean.getAddedDeliverySettingMap().remove(indexStr);
			
			recreatePickupOrderBean(pickupForm);
		}
		return FORWARD_SHOW_ORDER;
	}
	
	private static String clearPickupCustorderBean(PickupForm pickupForm) {
		pickupForm.clearCreate();
		return FORWARD_MAIN;
	}
	
	private static String selectPalletQuantity(PickupForm pickupForm) {
		BigDecimal palettes = null;
		try{					
			String palettesString= pickupForm.getInputReturnPalets();
			palettes = new BigDecimal(palettesString);
		}
		catch (NumberFormatException e) {
			return FORWARD_PALETS;
		}
		String index = pickupForm.getSelectedPalletItemBeanIndex();
		PickupPalletOrderItemBean palletBean = pickupForm.getCustorderBean().getPalletOrderItemBeanList().get(Integer.valueOf(index));
		palletBean.setQuantity(palettes);
		return FORWARD_SHOW_ORDER;
	}
	
	private static String selectReturnPalletQuantity(PickupForm pickupForm) {
		BigDecimal retPals = null;
		try{					
			String returnPals = pickupForm.getInputReturnPalets();
			retPals = new BigDecimal(returnPals);
		}
		catch (NumberFormatException e) {
			return FORWARD_RETURN_PALETS;
		}
		String index = pickupForm.getSelectedPalletItemBeanIndex();
		PickupPalletOrderItemBean palletBean = pickupForm.getCustorderBean().getPalletOrderItemBeanList().get(Integer.valueOf(index));
		palletBean.getItemEntity().setReturnedQuantity(retPals);
		palletBean.getItemEntity().setReturnedUom(palletBean.getItemEntity().getOrderedUom());
		return FORWARD_SHOW_ORDER;
	}
	
	private String selectShipto(PickupForm pickupForm, Locale locale) {
		initCustorderBean(pickupForm, locale);
		String forward = forwardToProductTypeSelector();
		return forward;
	}
	
	private String createNewPickupCustorder(PickupForm pickupForm, Locale locale) {
		ShiptoEntity shipTo = pickupForm.getShipTo();
		String forward;
		//if these were set in the entry action in case there was only one --> skipping these selector steps in that case
		if ( shipTo == null ) {
			forward = FORWARD_SHIPTO_SELECTOR;		
		}
		else {
			initCustorderBean(pickupForm, locale);
			forward = forwardToProductTypeSelector();
		}
		return forward;
	}

	/**
	 * If no bulk materials can be choosed, skip the select material type screen.
	 * @return
	 */
	private String forwardToProductTypeSelector() {
		Boolean bulkPickupCreationEnabled = getPickupLoadingpoint().getBulkPickupCreationFlag();
		return bulkPickupCreationEnabled ? FORWARD_PRODUCT_TYPE_SELECTOR : FORWARD_PRODUCT_SELECTOR;
	}
	
	private void initCustorderBean(PickupForm pickupForm, Locale locale) {
		
		SapBpEntity soldTo = pickupForm.getSoldTo();
		CustorderEntity custorder = createCustorder(soldTo, pickupForm.getShipTo());
		
		LoadingpointEntity loadingpoint = getLoadingpoint();
		customInitPickupCustorder(custorder, loadingpoint);
		
		ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
		DeliverySettingService deliverySettingService = serviceLocator.getDeliverySettingService();
		List shiptoSalesAreas = pickupForm.getShiptoSalesAreas();
		DeliverySettingShiptoMap shiptoMap = deliverySettingService.initDeliverySettingShiptoMapForPickupTerminal(
				soldTo, locale, shiptoSalesAreas);
		shiptoMap.validateSalesAreas();
		
		PickupCustorderBean custorderBean = new PickupCustorderBean(
				shiptoSalesAreas, loadingpoint, custorder, shiptoMap);
		
		pickupForm.setOrderBean(custorderBean);
	}
	
	/**
	 * The logic for the available material types during Terminal Application order creation
	 * 
	 * It depends on the type of order (bag or bulk / buy or return) and on the phase of order creation
	 * (whether user selects the first material or additional materials).
	 * 
	 * @param pickupForm
	 */
	private void setAvailableDeliverySettings(PickupForm pickupForm) {
		PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
		if (custorderBean == null) {
			return;
		}
		Map<String, DeliverySettingDTO> deliverySettingMap = custorderBean.getValidDeliverySettings();
		CustorderEntity custorderEntity = custorderBean.getCustorderEntity();
		Map<String, DeliverySettingDTO> availableDeliverySettingMap = null;
		List<String> allowedMaterialPackages = null;
		PickupLoadingpointEntity pickupLoadingpoint = getPickupLoadingpoint();
		// Bag orders
		if (!pickupForm.isProductTypeBulkSelected() ||
			!pickupLoadingpoint.getBulkPickupCreationFlag()) {
			// Additional materials
			if (CustorderUtils.containsCustorderItem(custorderEntity)) {
				allowedMaterialPackages = new ArrayList(Arrays.asList(Constant.MATERIAL_PACKAGE_SERVICE));
				allowedMaterialPackages.addAll(Constant.bagOrderMaterialPackageCds);
			}
			// First material
			else {
				allowedMaterialPackages = Constant.bagOrderMaterialPackageCds;
			}
		}
		// Bulk orders
		else {
			// Additional materials
			if (CustorderUtils.containsCustorderItem(custorderEntity)) {
				allowedMaterialPackages = Arrays.asList(Constant.MATERIAL_PACKAGE_SERVICE);
			}
			// First material
			else {
				allowedMaterialPackages = Constant.bulkOrderMaterialPackageCds;
			}
		}
		availableDeliverySettingMap = filterMaterialTypes(deliverySettingMap, allowedMaterialPackages);
		pickupForm.setAvailableDeliverySettings(availableDeliverySettingMap);
	}

	/**
	 * Starts the split of customer order and re initializes the customer order bean,
	 * and their related pallet item beans.
	 * 
	 * @param pickupForm
	 */
	private PickupCustorderBean recreatePickupOrderBean(PickupForm pickupForm) {
		PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
		CustorderEntity custorderEntity = custorderBean.getCustorderEntity();
		preparePickupCustorder(custorderEntity);
		TruckWagonTypeWrapper dummyTruckWagonWrapper =
				TruckWagonTypeWrapperUtil.getDummyWrapper(Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE);
		ServiceLocator serviceLocator = getServiceLocator();
		CustorderService custorderService = serviceLocator.getCustorderService();
		CustorderDTO processCustorderResult =
				custorderService.processCustomerOrder(custorderEntity, dummyTruckWagonWrapper, false);
		CustorderEntity custorder = processCustorderResult.getCustorderEntity();
		LoadingpointEntity loadingpoint = getLoadingpoint();
		ShippingpointEntity shippingpoint = loadingpoint.getShippingpoint();
		PickupCustorderBean custOrderBean = new PickupCustorderBean(custorder, shippingpoint, loadingpoint);
		pickupForm.setOrderBean(custOrderBean);
		return custOrderBean;
	}

	/**
	 * Prepares an unmanaged custorder entity to create and persist it.
	 * 
	 * The function is similar to BestellungAction:createCustorderBasedOnEditor().
	 * 
	 * @return - a new unmanaged {@code CustorderEntity}
	 */
	public static CustorderEntity preparePickupCustorder(CustorderEntity custorder) {
		// do not split orders initiated from terminal, see LOGON-6000
		custorder.setOrders(null);
		List<CustorderItemEntity> custorderItems = custorder.getCustorderItems();
		CustorderItemEntity firstCustorderItem = custorderItems.get(0);
		Boolean isExactCustorder = firstCustorderItem.getExactCustorderQtyFlag();
		Boolean isExactSdorderQty = firstCustorderItem.getExactSdorderQtyFlag();
		for (CustorderItemEntity custorderItem : custorderItems) {
			custorderItem.setExactCustorderQtyFlag(isExactCustorder);
			custorderItem.setExactSdorderQtyFlag(isExactSdorderQty);
		}
		return custorder;
	}

	/**
	 * Loads the customer order if loading is possible at the current pickup loading point. In either
	 * case, prints a delivery note if one exists.
	 */
	private String loadAndPrintPickupCustorder(HttpServletRequest request, PickupForm pickupForm) {
		// [[
		String result = null;
		Object[][] params = {{"request", request},
							{"pickupForm", pickupForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadAndPrintPickupCustorder", params);
		}
		// ]]
		try {
			
			Long custorderId = pickupForm.getInputCustOrderId();
			if (custorderId != null) {
				try {
					PickupLoadingpointEntity pickupLoadingpoint = getPickupLoadingpoint();
					PickupService pickupService = getServiceLocator().getPickupService();
					pickupService.loadAndPrintPickupCustorder(custorderId, pickupLoadingpoint);
				}
				catch (LogonPrintException pe) {
					MessageUtils.addActionError(request, "terminal.common.printfailed");
				}
				catch (Exception e) {
					LOG.error(getExceptionString(e));
					MessageUtils.addActionError(request, "pickup.errors.processing");
				}
			}
			boolean noErrorsWarnings = noErrorWarningPresent(request);
			
			if (noErrorsWarnings) {
				return result = FORWARD_COUNTER;
			}
			else{
				return result = FORWARD_MAIN;
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
	
	/**
	 * @param request
	 * @param custorderBean
	 * @param pickupForm 
	 * @return
	 */
	private static void validateOrderSetWarnings(HttpServletRequest request,
			PickupCustorderBean custorderBean, PickupForm pickupForm) {
		ActionMessages actionWarnings = MessageUtils.getActionWarnings(request);
		if(actionWarnings == null) {
			actionWarnings = new ActionMessages();
		}
		validateAndFinalizeOrder(actionWarnings, pickupForm.getCustorderBean().getCustorderEntity());
		if ( ! actionWarnings.isEmpty() ) {
			request.setAttribute(MessageUtils.WARNINGS_KEY, actionWarnings);
		}
		return;
	}
	
	private static String setDeliveryNote(PickupForm pickupForm) {
		String inputDeliveryNote = pickupForm.getInputDeliveryNote();
		String deliveryNote = StringUtils.empty(inputDeliveryNote) ? null : inputDeliveryNote;
		// trim note in case of exceeding the max value
		if (String.valueOf(deliveryNote).length() > OrderEntity.DBSIZE_NOTE_DELIVERY) {
			deliveryNote = deliveryNote.substring(0, (int) OrderEntity.DBSIZE_NOTE_DELIVERY);
		}
		PickupCustorderBean custorderBean = pickupForm.getCustorderBean();
		CustorderEntity custorderEntity = custorderBean.getCustorderEntity();
		custorderEntity.setNoteDelivery(deliveryNote);
		return FORWARD_SHOW_ORDER;
	}
	
	private static void validateAndFinalizeOrder(ActionMessages messages, CustorderEntity custorderEntity) {
		if ( custorderEntity.getFreeDeliveryFlag().equals(Boolean.TRUE) ){
			//it can only be saved as free of charge if the sold to corresponds with the bill to
			if ( ! (custorderEntity.getBilltoSapBp()!= null && custorderEntity.getSoldtoSapBp() != null && custorderEntity.getBilltoSapBp().getSapBpId().equals(custorderEntity.getSoldtoSapBp().getSapBpId())) ) {
				messages.add(MessageUtils.WARNINGS_KEY, new ActionMessage("pickup.errors.freedelivery.impossible"));
			}
		}
	}
}

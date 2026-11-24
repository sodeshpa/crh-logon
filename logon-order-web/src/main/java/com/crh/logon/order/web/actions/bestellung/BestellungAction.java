/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.business.services.utils.OrderTakingHelperUtil;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.utils.OrderTakingEditorParamsDto;
import com.crh.logon.order.web.utils.OrderTakingUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionMessage;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySetting;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.CustorderService;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.RailSchedulingService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.ShippingPointService;
import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.business.services.utils.BeanUtilsBuilder;
import com.crh.logon.business.services.utils.CustorderSplitWrapper;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.DateTimeAwareCustomerOrderWrapper;
import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;
import com.crh.logon.business.services.utils.LogonInvalidDurationException;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.MaterialColumn;
import com.crh.logon.business.services.utils.MaterialType;
import com.crh.logon.business.services.utils.OrderTakingEditorDto;
import com.crh.logon.business.services.utils.OrderTakingEditorOrderItemDto;
import com.crh.logon.business.services.utils.OrderTakingEditorRailDeliveryDto;
import com.crh.logon.business.services.utils.OrderTruckTypeComparator;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.SplittedOrderData;
import com.crh.logon.business.services.utils.SplittedOrderItemData;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderLifecycleReturnCode;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.converters.Base64ByteArrayConverter;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.DateConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.converters.StringConverter;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.CustorderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DailyOrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.refdata.TrspZoneBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.OrderCreateEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;

import flexjson.JSONSerializer;

/**
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date: 2013-10-07 16:10:57 +0200 (Mon, 07 Oct
 *          2013) $
 * 
 * @struts.action name="order_bestellung_BestellungForm"
 *                path="/bestellung/Bestellung" scope="session" validate="true"
 *                input="BestellungTile.def"
 * 
 * @struts.action-forward name="reset" path="/bestellung/BestellungShipTo.do"
 * @struts.action-forward name="saveSuccessAjax"
 *                        path="/bestellung/BestellungEntry.do"
 * @struts.action-forward name="changeDailyOrderStatusSuccess"
 *                        path="/bestellung/BestellungShipToEntry.do?referrer=changeDailyOrderStatus"
 *                        redirect="true"
 * @struts.action-forward name="saveFailedAjax"
 *                        path="BestellungOrderProcessingResultTile.def"
 * @struts.action-forward name="listNewOrders"
 *                        path="BestellungNewOrdersTile.def"
 * @struts.action-forward name="listOpenOrders"
 *                        path="BestellungOpenOrdersTile.def"
 * @struts.action-forward name="listClosedOrders"
 *                        path="BestellungClosedOrdersTile.def"
 * @struts.action-forward name="listOrders" path="BestellungOrdersListTile.def"
 * @struts.action-forward name="bimodalRailPart"
 *                        path="BestellungBimodalRailPartTile.def"
 * @struts.action-forward name="addedOrderItem"
 *                        path="BestellungAddedOrderItemTile.def"
 * @struts.action-forward name="addedOrderItemBulk"
 *                        path="BestellungAddedOrderItemBulkTile.def"
 * @struts.action-forward name="editorsAllOrders"
 *                        path="BestellungEditorsAllOrdersTile.def"
 * @struts.action-forward name="createCustomerOrderStep1"
 *                        path="BestellungOrderProcessingResultTile.def"
 * @struts.action-forward name="showSinDelOrdSplitPopup1"
 *                        path="SingleDeliveryOrderPopup1Tile.def"
 * @struts.action-forward name="showSinDelOrdSplitPopup2"
 *                        path="AdjustDeliveriesTile.def"
 * @struts.action-forward name="showSinDelOrdSplitPopup1Error"
 *                        path="SingleDeliveryOrderPopup1ErrorTile.def"
 * @struts.action-forward name="saveCustomerOrderSuccess"
 *                        path="/bestellung/BestellungEntry.do"
 * @struts.action-forward name="showOrdersForCustorder"
 *                        path="OrdersForCustorder.def"
 * @struts.action-forward name="setCustorderToFinished"
 *                        path="BestellungOrderProcessingResultTile.def"
 */
public class BestellungAction extends OrderActionSupport {

	public static final String RELOAD = "reload";
	public static final String RELOAD_ALL = "reloadAll";
	public static final String RELOAD_ALL_OPEN = "reloadAllOpen";
	public static final String RELOAD_ALL_CUSTORDERS = "reloadAllCustorders"; // For
																				// internal
																				// use
	public static final String RELOAD_OPEN_ORDERS = "reloadOpenOrders";
	public static final String RELOAD_OPEN_CUSTORDERS = "reloadOpenCustorders";
	public static final String RELOAD_CLOSED_CUSTORDERS = "reloadClosedCustorders";
	public static final String RELOAD_CLOSED_ORDERS = "reloadClosedOrders";

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(BestellungAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// [[
		ActionForward result = null;
		Object[][] params = { { "mapping", mapping }, { "form", form },
				{ "request", request }, { "response", response } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);

			BestellungForm helpForm = (BestellungForm) form;
			helpForm.applyOrderCommonSettings();
			String forwardName = "saveSuccessAjax";
			String action = request.getParameter("action");

			// --- "Change" button of Daily order status of a regular customer
			if ("changeDailyOrderStatus".equals(action)) {
				forwardName = "changeDailyOrderStatusSuccess";
				ShiptoEntity newShiptoEntity = getOrderService()
						.changeDailyOrderStatus(helpForm.getShipTo(),
								helpForm.getDailyOrderStatusCd());
				helpForm.setShipTo(newShiptoEntity,
						createTimeZoneFor(newShiptoEntity));
			}
			else if ("saveAjax".equals(action)) {
				forwardName = saveAjax(request, helpForm);
			}
			else if ("reset".equals(action)) {
				forwardName = "reset";
			}
			else if ("addOrderItem".equals(action)) {
				forwardName = addOrderItem(request, helpForm);
			}
			else if ("addServiceItem".equals(action)) {
				forwardName = addServiceItem(request, helpForm);
			}
			else if ("addBimodalRailPart".equals(action)) {
				forwardName = addBimodalRailPart(request, helpForm);
			}
			else if ("createCustomerOrderStep1".equals(action)) {
				forwardName = createCustomerOrderStep1(request, helpForm);
			}
			else if ("showSinDelOrdSplitPopup1".equals(action)
					|| "showSinDelOrdSplitPopup2".equals(action)) {
				forwardName = showSinDelOrdSplitPopup2(helpForm, request);
			}
			else if ("saveCustomerOrder".equals(action)) {
				forwardName = saveCustomerOrder(request, helpForm, true);
			}
			else if ("showOrdersForCustorder".equals(action)) {
				forwardName = showOrdersForCustorder(request, helpForm);
			}
			else if ("initiateSetCustorderToFinished".equals(action)) {
				forwardName = initiateSetCustorderToFinished(request);
			}
			else if ("confirmSetCustorderToFinished".equals(action)) {
				forwardName = confirmSetCustorderToFinished(request, helpForm);
			}
			else if ("reloadEditorsAllOrders".equals(action)) {
				// Reload the New Order editors and all order lists
				request.setAttribute("isEditorDisabled", true); // FIXME cleanup
																// the disabled
																// flag
				forwardName = "editorsAllOrders";
			}
			else if (RELOAD_ALL.equals(action)
					|| RELOAD_OPEN_ORDERS.equals(action)
					|| RELOAD_OPEN_CUSTORDERS.equals(action)
					|| RELOAD_CLOSED_CUSTORDERS.equals(action)
					|| RELOAD_CLOSED_ORDERS.equals(action)) {

				updateBestellungFormOrderLists(request, action);

				if (RELOAD_OPEN_ORDERS.equals(action)) {
					forwardName = "listOpenOrders";
				}
				else if (RELOAD_OPEN_CUSTORDERS.equals(action)) {
					forwardName = "listOpenCustorders";
				}
				else if (RELOAD_CLOSED_CUSTORDERS.equals(action)) {
					forwardName = "listClosedCustorders";
				}
				else if (RELOAD_CLOSED_ORDERS.equals(action)) {
					forwardName = "listClosedOrders";
				}
				else {
					forwardName = "listOrders";
				}
			}
			else if ("resetOrdersOnSelectedLnfTimeZone".equals(action)) {
				// invoked when the user selects a TimeZone for a One-Time
				// shipto.
				// this reset is forced to clear out the Orders page - the
				// customer and shipto
				// data, however, are sent along and set on the form
				forwardName = "reset";
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

	private String saveAjax(HttpServletRequest request, BestellungForm helpForm)
			throws Exception {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveAjax", params);
		}
		// ]]
		try {
			result = createCustomerOrderStep1(request, helpForm);
			if (result != null && result.equals("createCustomerOrderStep1")) {
				result = fetchOrCreateCustorder(helpForm, request);
				if (result == null) {
					List<OrderTakingEditorDto> editorList = helpForm
							.getEditorList();
					OrderTakingEditorDto editor = editorList.get(0);
					List<SplittedOrderData> splittedOrderData = editor
							.getOrdersListFromCustOrders();
					CustorderEntity custorder = helpForm.getCustorders().get(0);
					Long delTypeCd = editor.getDelTypeCd();
					if (shouldShowAdjustDeliveriesPopup(result, splittedOrderData, custorder,
							delTypeCd)) {
						result = "createCustomerOrderStep1";
					}
					else {
						result = saveCustomerOrder(request, helpForm, false);
					}
				}
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

	/**
	 * Requirement in LRQ 003.3 with regards to auto displaying
	 * Adjust Deliveries => but in synch with Adjust Delivery rules: 
	 * only when road or pickup
	 * @param result
	 * @param splittedOrderData
	 * @param custorder
	 * @param delTypeCd
	 * @return whether it is needed to show the popup or not
	 * 
	 */
	private boolean shouldShowAdjustDeliveriesPopup(String result,
			List<SplittedOrderData> splittedOrderData, CustorderEntity custorder, Long delTypeCd) {
		if (splittedOrderData != null
				&& (LogonUtil.isRoadDirectOrder(delTypeCd) || LogonUtil
						.isPickupOrder(delTypeCd))) {
			if (splittedOrderData.size() > 10) {
				return true;
			}
			else if (LogonPackageHandlingUtil.isBag(custorder)) {
				List<CustorderItemEntity> nonServiceCustorderItems = LogonPackageHandlingUtil
						.getMaterialCustorderItems(custorder
								.getCustorderItems());
				// If more than one delivery and more than one bag item
				if (splittedOrderData.size() > 1
						&& nonServiceCustorderItems.size() > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private String createCustomerOrderStep1(HttpServletRequest request,
			BestellungForm helpForm) throws Exception {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCustomerOrderStep1", params);
		}
		// ]]
		try {
			
			List<OrderTakingEditorDto> editorList = helpForm.getEditorList();

			// Bimodal order with road _and_ rail parts
			if (editorList.size() == 2
					&& LogonUtil.isBimodalTruckPartOrder(editorList.get(0)
							.getDelTypeCd())) {

				OrderTakingEditorDto editor = editorList.get(0);

				validateAndProcessNewOrderData(helpForm, request, editor, "");

				// --- If something went wrong, go back to the edit page ---
				if (MessageUtils.getActionWarnings(request) != null
						&& !MessageUtils.getActionWarnings(request).isEmpty()) {
					request.setAttribute("createCustomerOrderStep1Failed", true);
					return result = "saveFailedAjax";
				}

				saveShiptoData(helpForm);

				OrderEntity order = editor.getOrder();
				order.setPlannedUnloadingTimeMin(helpForm.getUnloadingTime());

				// --- VALIDATING RAIL PART ---
				processOrderEntityOfEditor(request, helpForm,
						editorList.get(1), "");

				// --- If something went wrong, go back to the edit page ---
				if (MessageUtils.getActionWarnings(request) != null
						&& !MessageUtils.getActionWarnings(request).isEmpty()) {
					request.setAttribute("createCustomerOrderStep1Failed", true);
					return result = "saveFailedAjax";
				}

			}
			else {

				// Non bimodal orders or road section of bimodal orders
				for (int i = 0; i < editorList.size(); i++) {

					OrderTakingEditorDto editor = editorList.get(i);

					validateAndProcessNewOrderData(helpForm, request, editor,
							Integer.toString((i + 1)));

					// --- If something went wrong, go back to the edit page ---
					if (MessageUtils.getActionWarnings(request) != null
							&& !MessageUtils.getActionWarnings(request)
									.isEmpty()) {
						request.setAttribute("createCustomerOrderStep1Failed",
								true);
						return result = "saveFailedAjax";
					}

					saveShiptoData(helpForm);

					OrderEntity order = editor.getOrder();
					order.setPlannedUnloadingTimeMin(helpForm
							.getUnloadingTime());

				}

			}
			
			request.setAttribute("createCustomerOrderStep1Success", true);
			return result = "createCustomerOrderStep1";
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String fetchOrCreateCustorder(BestellungForm helpForm,
			HttpServletRequest request) {
		// [[
		String result = null;
		Object[][] params = { { "helpForm", helpForm }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchOrCreateCustorder", params);
		}
		// ]]
		try {
			// fetch or create CO
			List<CustorderEntity> custorders = new ArrayList<CustorderEntity>();
			BestellungForm.AddToCustorderModeData addToCustorderMode = helpForm
					.getAddToCustorderMode();
			if (addToCustorderMode.isEnabled()) {
				Long custorderId = addToCustorderMode.getCustorderId();
				custorders.add(getOrderService()
						.fetchCustorderById(custorderId));
			}
			else {
				custorders = createCustomerOrder(request, helpForm);
			}

			if (MessageUtils.getActionErrors(request) != null
					&& !MessageUtils.getActionErrors(request).isEmpty()) {
				return result = "showSinDelOrdSplitPopup1Error";
			}

			helpForm.setCustorders(custorders);

			List<OrderTakingEditorDto> editorList = helpForm.getEditorList();
			OrderTakingEditorDto editor = editorList.get(0);
			ShiptoEntity shipTo = helpForm.getShipTo();

			List<SplittedOrderData> splittedOrdersFromCustorders = new ArrayList<SplittedOrderData>();
			for (CustorderEntity custorder : custorders) {
				// LOGON-5630: DeliveryNoteCollFlag is no longer saved on
				// indiviadual Order but on CustOrder
				custorder.setDelivnoteCollPaperFlag(helpForm
						.getDelivnoteCollPaperFlag());

				TimeZone shipToTimeZone = helpForm.getShipToTimeZone();
				List<SplittedOrderData> splittedOrderDataList = CustorderUtils
.getSplittedOrderData(
						shipTo, custorder, shipToTimeZone);
				splittedOrdersFromCustorders.addAll(splittedOrderDataList);
			}

			editor.setOrdersListFromCustOrders(splittedOrdersFromCustorders);

			return result = null;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String showSinDelOrdSplitPopup2(BestellungForm helpForm,
			HttpServletRequest request) {
		// [[
		String result = null;
		Object[][] params = { { "helpForm", helpForm }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "showSinDelOrdSplitPopup2", params);
		}
		// ]]
		try {

			String skipSplit = request.getParameter("skipSplit");

			if (skipSplit != null && skipSplit.equalsIgnoreCase("true")) {
				return result = "showSinDelOrdSplitPopup2";
			}

			String forwardName = fetchOrCreateCustorder(helpForm, request);
			if (forwardName != null) {
				return result = forwardName;
			}

			ShiptoEntity shipTo = helpForm.getShipTo();

			List<OrderTakingEditorDto> editorList = helpForm.getEditorList();

			for (OrderTakingEditorDto editor : editorList) {

				CustorderSplitWrapper wrapper = editor.getWrapper();
				CustorderEntity custorder = wrapper.getCustorder();

				List<SplittedOrderData> splittedOrdersFromCustorders = new ArrayList<SplittedOrderData>();
				TimeZone shipToTimeZone = helpForm.getShipToTimeZone();
				List<SplittedOrderData> splittedOrderDataList = 
						CustorderUtils.getSplittedOrderData(shipTo, custorder, shipToTimeZone);
				splittedOrdersFromCustorders.addAll(splittedOrderDataList);

				Map<Long, BigDecimal> qtyPerMaterial = new HashMap<Long, BigDecimal>();

				for (SplittedOrderData splittedOrderData : splittedOrderDataList) {

					OrderEntity order = splittedOrderData.getOrder();

					List<OrderItemEntity> orderItems = order.getOrderItems();

					// Enrich list
					List<CustorderItemEntity> custorderItems = custorder
							.getCustorderItems();
					CustorderUtils.addMissingOrderItemsBasedOnCOI(wrapper,
							order, orderItems, custorderItems);

					// Generate SplittedOrderItemDatas
					List<SplittedOrderItemData> splittedOrderItemDatas = CustorderUtils
							.populateSplittedOrdetItems(orderItems);
					splittedOrderData
							.setSplittedOrderItemDatas(splittedOrderItemDatas);

					// Qty per material
					CustorderUtils.calculateQtyPerMaterial(wrapper,
							qtyPerMaterial, splittedOrderItemDatas);

					splittedOrderData
							.setSplittedOrderItemDatas(splittedOrderItemDatas);

					String serviceProcessIndicatorId;
					if (order.getSpi() != null) {
						serviceProcessIndicatorId = order.getSpi()
								.getOrderSpiCd();
					}
					else {
						serviceProcessIndicatorId = Constant.INVALID_OBJECT_ID_STR;
					}

					splittedOrderData
							.setServiceProcessIndicatorId(serviceProcessIndicatorId);

				}

				// Collecting possible materials
				Locale locale = request.getLocale();
				DispokarteService dispokarteService = getServiceLocator()
						.getDispokarteService();
				Set<MaterialColumn> materialColumns = CustorderUtils
						.generateMaterialColumns(locale, custorder,
								qtyPerMaterial, dispokarteService);

				MaterialType materialType = OrderTakingHelperUtil.getMaterialType(custorder);

				editor.setOnHoldOrderAndLockedFlag(custorder
						.getOnHoldOrderAndLockedFlag());
				editor.setMaterialType(materialType);
				editor.setMaterialColumns(materialColumns);
				editor.setOrdersListFromCustOrders(splittedOrdersFromCustorders);

				// Initialize the editor with ship to opening times
				editor.setOpen1From(helpForm.getOpen1From());
				editor.setOpen1To(helpForm.getOpen1To());
				editor.setOpen2From(helpForm.getOpen2From());
				editor.setOpen2To(helpForm.getOpen2To());

			}

			return result = "showSinDelOrdSplitPopup2";

		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private List<CustorderEntity> createCustomerOrder(
			HttpServletRequest request, BestellungForm helpForm) {
		// [[
		List<CustorderEntity> result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCustomerOrder", params);
		}
		// ]]
		try {

			result = new ArrayList<CustorderEntity>();

			BeanUtilsBean beanUtilsBean = getBeanUtilsBean();

			List<OrderTakingEditorDto> editorList = helpForm.getEditorList();

			// Bimodal constellation
			if (editorList.size() == 2
					&& LogonUtil.isBimodalTruckPartOrder(editorList.get(0)
							.getDelTypeCd())) {

				// Road part
				OrderTakingEditorDto bimRoadPartEditor = editorList.get(0);

				CustorderEntity bimRoadPartCustorder = createCustorderBasedOnEditor(
						request, bimRoadPartEditor, beanUtilsBean, helpForm, "");

				result.add(bimRoadPartCustorder);

				// Rail part

				OrderTakingEditorDto bimrailPartEditor = editorList.get(1);

				OrderEntity bimrpFirstOrder = bimrailPartEditor.getOrder();

				// Create CO
				CustorderEntity bimRailPartCustorder = new CustorderEntity();

				// Copy properties from first order
				try {
					beanUtilsBean.copyProperties(bimRailPartCustorder,
							bimrpFirstOrder);
				}
				catch (Exception e) {
					MessageUtils.addActionError(request, e);
					return result = null;
				}

				// Status
				CustorderStatusBsEntity bimrpCustorderStatus = getRefDataGenericsService()
						.findBsEntity(CustorderStatusBsEntity.class,
								Constant.CUSTORDER_STATUS_NEW);
				bimRailPartCustorder.setCustorderStatus(bimrpCustorderStatus);

				// Customer Reference No.
				bimRailPartCustorder.setCustorderCustrefNo(bimRailPartCustorder
						.getCustorderCustrefNo());

				// Special Order Indicators
				bimRailPartCustorder.setOpenCustorderFlag(bimrailPartEditor
						.getOpenCustorderFlag());
				bimRailPartCustorder
						.setOnHoldOrderAndLockedFlag(bimrailPartEditor
								.getOnHoldOrderAndLockedFlag());
				bimRailPartCustorder
						.setSapSuppressMinChargeFlag(bimrailPartEditor
								.getSapSuppressMinChargeFlag());
				bimRailPartCustorder.setFreeDeliveryFlag(bimrailPartEditor
						.getFreeDeliveryFlag());
				bimRailPartCustorder.setReleaseSapFlag(bimrailPartEditor
						.getReleaseSapFlag());

				composeCustomerOrder(request, bimRailPartCustorder,
						bimrpFirstOrder);

				result.add(bimRailPartCustorder);

			}
			else {

				// Normal custorders [bag; bulk]
				for (int i = 0; i < editorList.size(); i++) {
					OrderTakingEditorDto orderTakingEditorDto = editorList
							.get(i);
					CustorderEntity custorder = createCustorderBasedOnEditor(
							request, orderTakingEditorDto, beanUtilsBean,
							helpForm, Integer.toString(i));
					result.add(custorder);
				}

			}

			return result;
		
		}
		catch (Exception e) {
			addActionErrorFatalUnknown(request, e);
			return result = null;
		}
		finally {
			if (result == null || result.isEmpty()) {
				MessageUtils.addActionError(request,
						"bestellung.error.custorder.creation.failed");
			}

			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void addActionErrorFatalUnknown(HttpServletRequest request,	Exception e) {
		LOG.error(
				"unexpected exception occurred during custorder creation",
				e);
		MessageUtils.addActionError(request, e, "fatalerror.unknownerror");
	}

	/**
	 * Prepares an unmanaged custorder entity based on the given
	 * {@code OrderTakingEditorDto}.
	 * 
	 * @return - a new unmanaged {@code CustorderEntity}
	 */
	private CustorderEntity createCustorderBasedOnEditor(
			HttpServletRequest request,
			OrderTakingEditorDto orderTakingEditor,
			BeanUtilsBean beanUtilsBean, BestellungForm helpForm, String index) {
		// [[
		CustorderEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCustomerBasedOnEditor");
		}
		// ]]
		try {
			ShiptoEntity shipTo = helpForm.getShipTo();

			final OrderEntity firstOrder = orderTakingEditor.getOrder();
			List<OrderItemEntity> firstOrdersItems = firstOrder.getOrderItems();
			final List<OrderItemEntity> orderItemsOfFirstOrder = firstOrdersItems;

			CustorderEntity customerOrder = new CustorderEntity();

			try {
				beanUtilsBean.copyProperties(customerOrder, firstOrder);
			}
			catch (Exception e) {
				MessageUtils.addActionError(request, e);
				return result = null;
			}

			RefDataGenericsService referenceDataGenericsService = getRefDataGenericsService();
			CustorderStatusBsEntity newCustomerOrdersStatus = referenceDataGenericsService.findBsEntity(
					CustorderStatusBsEntity.class, Constant.CUSTORDER_STATUS_NEW);
			customerOrder.setCustorderStatus(newCustomerOrdersStatus);

			String customerReferenceNumber = orderTakingEditor.getCustorderCustrefNo();
			customerOrder.setCustorderCustrefNo(customerReferenceNumber);

			customerOrder.setOpenCustorderFlag(orderTakingEditor.getOpenCustorderFlag());
			customerOrder.setOnHoldOrderAndLockedFlag(orderTakingEditor.getOnHoldOrderAndLockedFlag());
			customerOrder.setSapSuppressMinChargeFlag(orderTakingEditor.getSapSuppressMinChargeFlag());
			customerOrder.setFreeDeliveryFlag(orderTakingEditor.getFreeDeliveryFlag());
			customerOrder.setReleaseSapFlag(orderTakingEditor.getReleaseSapFlag());

			// Delivery From Date
			TimeZone shipToTimeZone = helpForm.getShipToTimeZone();
			Calendar deliveryFrom = OrderTakingUtils.getEditorDeliveryFrom(
					orderTakingEditor, shipTo, shipToTimeZone);

			Date deliveryFromTime = deliveryFrom.getTime();
			customerOrder.setDeliveryFromDat(deliveryFromTime);

			Calendar deliveryTo = OrderTakingUtils.getEditorDeliveryTo(
					orderTakingEditor, shipTo, shipToTimeZone);

			String deliveryTimeToleranceAsString = orderTakingEditor.getDeliveryTimeToleranceStr();
			String deliveryTimeToleranceUnitOfMeasureCode = orderTakingEditor.getDeliveryTimeToleranceUomCd();
			if (com.crh.logon.utils.StringUtils.notEmpty(deliveryTimeToleranceAsString)
					&& com.crh.logon.utils.StringUtils.notEmpty(deliveryTimeToleranceUnitOfMeasureCode)) {
				double deliveryTimeTolerance = Double.parseDouble(deliveryTimeToleranceAsString);
				customerOrder.setDeliveryTimeTolerance(deliveryTimeTolerance);
				customerOrder.setDeliveryTimeToleranceUom(deliveryTimeToleranceUnitOfMeasureCode);
			}

			final Long deliveryTypeCode = orderTakingEditor.getDelTypeCd();

			// Order Vehicle Types (only for truck, pickup and return delivered
			// for the moment)
			List<TruckWagonTypeWrapper> vehicleTypes = orderTakingEditor.getVehicleTypeWrapperList();
			if (LogonUtil.isTruckOrder(deliveryTypeCode)
					|| LogonUtil.isPickupOrder(deliveryTypeCode)
					|| LogonUtil.isReturnDeliveredOrder(deliveryTypeCode)) {
				final String selectedVehicleTypes = orderTakingEditor.getSelectedTruckTypes();
				final List<TruckWagonTypeWrapper> vehicleTypeWrappers = vehicleTypes;
				final List<OrderTruckTypeEntity> orderTruckTypes = createOrderTruckTypes(
						firstOrder, parseNumberList(selectedVehicleTypes),
						vehicleTypeWrappers);

				if (orderTruckTypes != null && !orderTruckTypes.isEmpty()) {
					firstOrder.setOrderVehicleTypes(orderTruckTypes);
				}
				else {
					LOG.error("could not obtain vehicle");
					MessageUtils.addActionError(
							request,
							"bestellung.error.required.vehicletype",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
					return result = null;
				}
			}

			// Performance Order - calculate deliveryTo
			String performanceOrderCalculationMode = orderTakingEditor
					.getPerformanceOrderCalculationMode();
			if (Constant.PERFORMANCE_ORDER_CALCULATE_DELIVERYTO
					.equals(performanceOrderCalculationMode)) {
				customerOrder.setDeliveryToDat(null);
			}
			else {
				Date deliveryToTime = deliveryTo.getTime();
				customerOrder.setDeliveryToDat(deliveryToTime);
			}

			if (Constant.PERFORMANCE_ORDER_CALCULATE_QUANTITY
					.equals(performanceOrderCalculationMode)) {
				if (orderItemsOfFirstOrder != null
						&& orderItemsOfFirstOrder.get(0) != null) {
					orderItemsOfFirstOrder.get(0).setOrderedQuantity(null);
				}
			}

			String deliveryFrequencyAsString = orderTakingEditor.getDeliveryFqyEveryTimeStr();
			if (com.crh.logon.utils.StringUtils.notEmpty(deliveryFrequencyAsString)) {
				double deliveryFrequency = Double.parseDouble(deliveryFrequencyAsString);
				customerOrder.setDeliveryFreqEveryTime(deliveryFrequency);
				String deliveryFrequencyUnitOfMeasureCode = orderTakingEditor.getDeliveryFqyEveryTimeUomCd();
				customerOrder.setDeliveryFreqEveryTimeUom(deliveryFrequencyUnitOfMeasureCode);
			}

			String hourlyDeliveryQuantityAsString = orderTakingEditor.getDeliveryFqyQtyPerHreStr();
			if (com.crh.logon.utils.StringUtils.notEmpty(hourlyDeliveryQuantityAsString)) {
				BigDecimal deliveryFrequencyPerHour = new BigDecimal(
						hourlyDeliveryQuantityAsString);
				customerOrder.setDeliveryFreqQtyperhre(deliveryFrequencyPerHour);

				String unitOfMeasureCodeOfPerHourDelivery = orderTakingEditor.getDeliveryFqyQtyPerHreUomCd();
				UomBsEntity unitOfMeasureOfPerHourDelivery = referenceDataGenericsService.findBsEntity(
						UomBsEntity.class, unitOfMeasureCodeOfPerHourDelivery);
				customerOrder.setDeliveryFreqQtyperhreUom(unitOfMeasureOfPerHourDelivery);
			}

			boolean isExactCustomerOrderQuantity = BooleanUtils.isTrue(orderTakingEditor.getExactCustorderQtyFlag());

			// Per Single Delivery Quantity and UoM
			BigDecimal quantityPerDelivery = null;
			UomBsEntity unitOfMeasureOfDelivery = null;
			ServiceLocator serviceLocator = getServiceLocator();
			String quantityPerDeliveryAsString = orderTakingEditor.getQuantityPerSdoStr();
			String quantityPerDeliveryUnitOfMeasureCode = orderTakingEditor.getQuantityPerSdoUomCd();
			if (com.crh.logon.utils.StringUtils.notEmpty(quantityPerDeliveryAsString)
					&& com.crh.logon.utils.StringUtils.notEmpty(quantityPerDeliveryUnitOfMeasureCode)) {
				quantityPerDelivery = new BigDecimal(quantityPerDeliveryAsString);
				unitOfMeasureOfDelivery = referenceDataGenericsService.findBsEntity(
						UomBsEntity.class, quantityPerDeliveryUnitOfMeasureCode);
			}

			boolean isExactDeliveryQuantity = BooleanUtils.isTrue(orderTakingEditor.getExactQtyPerSdoFlag());

			List<CustorderItemEntity> customerOrderItems = new ArrayList<CustorderItemEntity>();
			for (OrderItemEntity orderItem : orderItemsOfFirstOrder) {
				CustorderItemEntity newCustomerOrderItem = new CustorderItemEntity();
				try {
					beanUtilsBean.copyProperties(newCustomerOrderItem, orderItem);
				}
				catch (Exception exception) {
					MessageUtils.addActionError(request, exception);
					return result = null;
				}

				newCustomerOrderItem.setExactCustorderQtyFlag(isExactCustomerOrderQuantity);
				newCustomerOrderItem.setPerSdorderQuantity(quantityPerDelivery);
				newCustomerOrderItem.setPerSdorderUom(unitOfMeasureOfDelivery);
				newCustomerOrderItem.setExactSdorderQtyFlag(isExactDeliveryQuantity);

				newCustomerOrderItem.setCustorder(customerOrder);
				customerOrderItems.add(newCustomerOrderItem);
			}

			customerOrder.setCustorderItems(customerOrderItems);

			// Vehicle with the highest priority
			TruckWagonTypeWrapper maximumPriority = null;
			final Long maxPriorityTruckTypeCode;

			if (LogonUtil.isTruckOrder(deliveryTypeCode)
					|| LogonUtil.isPickupOrder(deliveryTypeCode)
					|| LogonUtil.isReturnDeliveredOrder(deliveryTypeCode)) {
				List<OrderTruckTypeEntity> vehicleTypesOfFirstOrder = firstOrder.getOrderVehicleTypes();
				OrderTruckTypeEntity maxPriorityOrderTruck = Collections.max(
						vehicleTypesOfFirstOrder, new OrderTruckTypeComparator());
				TruckTypeBsEntity maxPriorityOrderTruckType = maxPriorityOrderTruck.getTruckType();
				maxPriorityTruckTypeCode = maxPriorityOrderTruckType.getTruckTypeCd();

				for (TruckWagonTypeWrapper vehicle : vehicleTypes) {
					Long vehicleTypeCode = vehicle.getTypeCd();
					if (vehicleTypeCode.equals(maxPriorityTruckTypeCode)) {
						maximumPriority = vehicle;
						break;
					}
				}
			}
			else if (vehicleTypes == null || !vehicleTypes.isEmpty()) {
				List<TruckWagonTypeWrapper> truckWagonTypes = vehicleTypes;
				maximumPriority = TruckWagonTypeWrapperUtil.findProperTruck(truckWagonTypes);
			}
			else {
				boolean isServiceOnlyOrder = BooleanUtils.isTrue(orderTakingEditor.getServiceOnlyOrderFlag());
				if (LogonUtil.isReturnPickupOrder(deliveryTypeCode) || (isServiceOnlyOrder)) {
					maximumPriority = null;
				}
				else {
					LOG.error("vehicleTypeWrapperList is null or empty");
					MessageUtils
							.addActionError(
									request,
									"bestellung.error.required.wagon",
									getEditorLocatorMessage(request).replaceAll(
											"@", index));
					return result = null;
				}
			}

			CustorderSplitWrapper splitWrapper = null;
			try {
				CustorderService customerOrderService = serviceLocator.getCustorderService();
				splitWrapper = customerOrderService
						.createCustorderSplitWrapper(customerOrder, maximumPriority);
				splitWrapper.setSdoTemplate(firstOrder);
				OrderItemEntity firstOrderItemOfFirstOrder = firstOrdersItems.get(0);
				splitWrapper.setSdoiTemplate(firstOrderItemOfFirstOrder);
				CustorderUtils.splitCustorder(splitWrapper);
			}
			catch (ValidationException validationException) {
				processErrors(request, validationException, CallerContext.CALL_CENTER, false);
				return result = null;
			}
			boolean isConversionFailed = splitWrapper.isConversionFailed();
			if (isConversionFailed) {
				MessageUtils.addActionError(request, Reason.RES_ERROR_CANNOT_CONVERT_UOM.toString());
			}
			if (logDebugEnabled) {
				String splitWrapperAsString = splitWrapper.toString();
				LOG.debug(splitWrapperAsString);
			}

			// Save the wrapper for later usage i.e. when adding / deleting
			// splitted order data elements
			orderTakingEditor.setWrapper(splitWrapper);

			customerOrder = splitWrapper.getCustorder();

			return result = customerOrder;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void composeCustomerOrder(HttpServletRequest request,
			CustorderEntity custorder, OrderEntity order) {
		// [[
		Object[][] params = { { "request", request },
				{ "custorder", custorder }, { "order", order } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "composeCustomerOrder", params);
		}
		// ]]
		try {
			// Link single Delivery Order and Custorder (both direction)
			List<OrderEntity> sdoList = new ArrayList<OrderEntity>();
			sdoList.add(order);
			custorder.setOrders(sdoList);
			order.setCustorder(custorder);

			// CustorderItems
			List<CustorderItemEntity> custorderItems = new ArrayList<CustorderItemEntity>();
			List<OrderItemEntity> orderItems = order.getOrderItems();

			BeanUtilsBean beanUtilsBean = getBeanUtilsBean();

			for (OrderItemEntity orderItem : orderItems) {
				CustorderItemEntity custorderItem = new CustorderItemEntity();

				try {
					beanUtilsBean.copyProperties(custorderItem, orderItem);
				}
				catch (Exception e) {
					MessageUtils.addActionError(request, e);
					return;
				}

				custorderItem.setExactCustorderQtyFlag(false);
				custorderItem.setExactSdorderQtyFlag(false);
				custorderItem.setPerSdorderQuantity(null);
				custorderItem.setPerSdorderUom(null);

				custorderItem.setCustorder(custorder);
				custorderItems.add(custorderItem);
			}

			custorder.setDeliveryTimeTolerance(null);
			custorder.setDeliveryTimeToleranceUom(null);
			custorder.setDeliveryFreqEveryTime(null);
			custorder.setDeliveryFreqEveryTimeUom(null);
			custorder.setDeliveryFreqQtyperhre(null);
			custorder.setDeliveryFreqQtyperhreUom(null);

			custorder.setCustorderItems(custorderItems);
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
	 * @return
	 */
	private BeanUtilsBean getBeanUtilsBean() {
		BeanUtilsBean beanUtilsBean = new BeanUtilsBuilder()
				.addConverter(new BigDecimalConverter())
				.addConverter(new LongConverter())
				.addConverter(new BooleanConverter())
				.addConverter(new DateConverter())
				.addConverter(new Base64ByteArrayConverter())
				.addConverter(new StringConverter()).createBean();
		return beanUtilsBean;
	}

	private String saveCustomerOrder(HttpServletRequest request,
			BestellungForm helpForm, boolean dontSkipSaveShiptoData) {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveCustomerOrder", params);
		}
		// ]]
		try {
			if (dontSkipSaveShiptoData) {
				saveShiptoData(helpForm);
			}

			boolean isCustorderFinished = false;

			try {
				// Prepare Customer Order Entity(s)
				if (helpForm.getCustorders() == null
						|| helpForm.getCustorders().isEmpty()) {
					request.setAttribute("saveCustomerOrderFailed", true);
					return result = "saveFailedAjax";
				}
				
				CustorderService custorderService = getServiceLocator()
						.getCustorderService();
				RefDataGenericsService refDataGenericsService = getRefDataGenericsService();

				List<CustorderEntity> custorders = helpForm.getCustorders();

				OrderTakingEditorDto firstOrderTakingEditorDTO = helpForm
						.getEditorList().get(0);
				
				// In case we have a bimodal constellation
				if (custorders.size() == 2
						&& LogonUtil
								.isBimodalTruckPartOrder(firstOrderTakingEditorDTO
										.getDelTypeCd())) {
					CustorderEntity roadPart = custorders.get(0);
					CustorderEntity railPart = custorders.get(1);

					final Boolean autoCarrierAssign = firstOrderTakingEditorDTO
							.getAutoCarrierAssignmentFlag();
					final Boolean autoPoolAssign = firstOrderTakingEditorDTO
							.getAutoPoolAssignmentFlag();
					final Boolean autoRelease = firstOrderTakingEditorDTO
							.getAutoReleaseFlag();
					
					custorderService.createBimodalCustorder(roadPart, railPart,
							autoPoolAssign, autoCarrierAssign, autoRelease);
				}
				else {
					// Otherwise we iterate over all the custorders [bag; bulk]
					BestellungForm.AddToCustorderModeData addToCustorderMode = helpForm
							.getAddToCustorderMode();

					for (OrderTakingEditorDto orderTakingEditorDto : helpForm
							.getEditorList()) {
						CustorderSplitWrapper wrapper = orderTakingEditorDto
								.getWrapper();
						CustorderEntity custorder = wrapper.getCustorder();

						if (addToCustorderMode.isEnabled()) {
							// Adding SDOs to an existing customer order.
							CustorderStatusBsEntity custorderStatus = custorder
									.getCustorderStatus();
							Long custorderStatusCd = custorderStatus
									.getCustorderStatusCd();

							if (custorderStatusCd == Constant.CUSTORDER_STATUS_FINISHED) {
								custorderStatus = refDataGenericsService
										.findBsEntity(
												CustorderStatusBsEntity.class,
												Constant.CUSTORDER_STATUS_OPEN);
								custorder.setCustorderStatus(custorderStatus);
							}
						}

						final Boolean autoCarrierAssign = orderTakingEditorDto
								.getAutoCarrierAssignmentFlag();
						final Boolean autoPoolAssign = orderTakingEditorDto
								.getAutoPoolAssignmentFlag();
						final Boolean autoRelease = orderTakingEditorDto
								.getAutoReleaseFlag();
						
						Boolean allowFutureContracts = helpForm.getAllowFutureContracts();
						
						boolean filterFutureContracts = true;
						
						if (allowFutureContracts != null) {
							filterFutureContracts = !allowFutureContracts;
						}
						custorder = custorderService.createCustomerOrder(
								custorder, autoPoolAssign, autoCarrierAssign,
								autoRelease, null,
								Constant.WEIGH_SYSTEM_LP_LOGON, filterFutureContracts);
						
						if (custorder.getCustorderStatus()
								.getCustorderStatusCd() != null
								&& custorder
										.getCustorderStatus()
										.getCustorderStatusCd()
										.equals(Constant.CUSTORDER_STATUS_FINISHED)) {
							isCustorderFinished = true;
						}
						addToCustorderMode.reset();
					}
				}

			}
			catch (ValidationException e) {
				processErrors(request, e, CallerContext.CALL_CENTER, false);
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					MessageUtils.addActionError(request, ((LogonException) e)
							.getReason().getReason());
				}
				else if (e.getCause() instanceof LogonException) {
					MessageUtils.addActionError(request, ((LogonException) e
							.getCause()).getReason().getReason());
				}
				else {
					MessageUtils.addActionError(request, e);
				}
			}

			ActionMessages actionWarnings = MessageUtils
					.getActionWarnings(request);
			ActionErrors actionErrors = MessageUtils.getActionErrors(request);

			if ((actionWarnings != null && !actionWarnings.isEmpty())
					|| (actionErrors != null && !actionErrors.isEmpty())) {
				request.setAttribute("saveCustomerOrderFailed", true);
				return result = "saveFailedAjax";
			}

			if (isCustorderFinished) {
				updateBestellungFormOrderLists(request, RELOAD_CLOSED_CUSTORDERS);
				MessageUtils.addActionMessage(request,
						"bestellung.success.savedelivered");
			}
			else {
				updateBestellungFormOrderLists(request, RELOAD_ALL_OPEN);
				MessageUtils.addActionMessage(request,
						"bestellung.success.save");
			}

			request.setAttribute("orderSaveSuccess", true);
			request.setAttribute("saveCustomerOrderSuccess", true);

			return result = "saveCustomerOrderSuccess";
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String showOrdersForCustorder(HttpServletRequest request,
			BestellungForm helpForm) {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "showOrdersForCustorder", params);
		}
		// ]]
		try {
			String custorderIdStr = request.getParameter("custorderId");
			Long custorderId = Long.valueOf(custorderIdStr);

			updateOrdersForCustorder(helpForm, custorderId);
			request.setAttribute("showOrdersForCustorder", true);

			return result = "showOrdersForCustorder";
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String addOrderItem(HttpServletRequest request,
			BestellungForm helpForm) throws Exception {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addOrderItem", params);
		}
		// ]]
		try {

			// Check what page to forward
			String packageType = request.getParameter("packageType");

			if (packageType
					.equalsIgnoreCase(Constant.PACKAGE_TYPE_BULK_GENERAL)) {
				addOrderItemBulk(request, helpForm);
				result = "addedOrderItemBulk";
			}
			else if (packageType
					.equalsIgnoreCase(Constant.PACKAGE_TYPE_BBAG_STR)) {
				addOrderItemBag(request, helpForm);
				result = "addedOrderItem";
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

	private void addOrderItemBag(HttpServletRequest request,
			BestellungForm helpForm) throws Exception {
		// [[
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addOrderItemBag", params);
		}
		// ]]
		try {

			List<OrderTakingEditorDto> editorList = helpForm.getEditorList();

			OrderTakingEditorDto editor = editorList.get(0);

			List<OrderTakingEditorOrderItemDto> orderItems = editor
					.getOrderItems();
			OrderTakingEditorOrderItemDto addedItem = new OrderTakingEditorOrderItemDto();

			Long orderItemIdx = null;

			// Find a whole or add a new
			for (int i = 0; i < orderItems.size(); i++) {
				if (orderItems.get(i) == null) {
					orderItems.set(i, addedItem);
					orderItemIdx = Long.valueOf(i);
					break;
				}
			}

			if (orderItemIdx == null) {
				orderItems.add(addedItem);
				orderItemIdx = Long.valueOf(orderItems.size() - 1);
			}

			// The first material defines the contract (and therefore the sales
			// area) of the other materials.
			OrderTakingEditorOrderItemDto firstOrderItemDto = orderItems.get(0);

			final DeliverySetting setting = editor.getSalesAreaMap()
					.getMaterialDeliverySettingByCompositeIdentifier(
							firstOrderItemDto.getCompositeIdentifier());

			Long sapContractId = setting.getSapContractItem().getSapContract()
					.getSapContractId();

			final List<DeliverySettingDTO> productsByContract = editor
					.getSalesAreaMap().getMaterialForFirstDeliverySetting(
							sapContractId);

			final OrderItemEntity firstOrderItem = firstOrderItemDto
					.getOrderItem();
			if (LogonPackageHandlingUtil.isPallet(firstOrderItem)) { // filter
																		// out
																		// non
																		// pallet
																		// items
				if (productsByContract != null) {
					Iterator<DeliverySettingDTO> iter = productsByContract
							.iterator();
					while (iter.hasNext()) {
						DeliverySettingDTO actDeliverySettingDTO = iter.next();
						final SapMaterialEntity actSapMaterial = actDeliverySettingDTO
								.getSapMaterial();
						if (!LogonPackageHandlingUtil.isPallet(actSapMaterial)) {
							iter.remove();
						}
					}
				}
			}

			final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
			jsonSerializer.exclude(DeliverySettingDTO.excludeEntities);

			String jsonProductList = jsonSerializer
					.serialize(productsByContract);

			addedItem.setJsonProductList(jsonProductList);

			// Selecting the proper order entry editor section (NewOrders.jsp)
			request.setAttribute("editorIdx",
					Long.valueOf(editorList.size() - 1));
			request.setAttribute("orderItemIdx", orderItemIdx);

		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void addOrderItemBulk(HttpServletRequest request,
			BestellungForm helpForm) throws Exception {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addOrderItem", params);
		}
		// ]]
		try {

			List<OrderTakingEditorDto> editorList = helpForm.getEditorList();

			OrderTakingEditorDto editor = OrderTakingUtils
					.getOrderEditorInstance(getServiceLocator(),
							helpForm.getShiptoMap(), request.getLocale());

			helpForm.initEditorWithDateAndTime(editor);

			helpForm.getEditorList().add(editor);

			// Selecting the proper order entry editor section (NewOrders.jsp)
			request.setAttribute("editorIdx",
					Long.valueOf(editorList.size() - 1));
			request.setAttribute("orderItemIdx", Long.valueOf(0));

		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String addServiceItem(HttpServletRequest request,
			BestellungForm helpForm) {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addServiceItem", params);
		}
		// ]]
		try {

			int selectedEditorIndex = Integer.parseInt(request
					.getParameter("selectedEditorIndex"));

			OrderTakingEditorDto editor = helpForm.getEditorList().get(
					selectedEditorIndex);

			List<OrderTakingEditorOrderItemDto> orderItems = editor
					.getOrderItems();
			OrderTakingEditorOrderItemDto addedItem = new OrderTakingEditorOrderItemDto();

			Long orderItemIdx = null;

			// Find a whole or add a new
			for (int i = 0; i < orderItems.size(); i++) {
				if (orderItems.get(i) == null) {
					orderItems.set(i, addedItem);
					orderItemIdx = Long.valueOf(i);
					break;
				}
			}

			if (orderItemIdx == null) {
				orderItems.add(addedItem);
				orderItemIdx = Long.valueOf(orderItems.size() - 1);
			}

			OrderTakingEditorOrderItemDto firstOrderItemDto = orderItems.get(0);

			final DeliverySetting setting;
			if (editor.getServiceOnlyOrderFlag() != null
					&& editor.getServiceOnlyOrderFlag().booleanValue()) {
				setting = editor.getSalesAreaMap()
						.getServiceDeliverySettingByCompositeIdentifier(
								firstOrderItemDto.getCompositeIdentifier());
			}
			else {
				setting = editor.getSalesAreaMap()
						.getMaterialDeliverySettingByCompositeIdentifier(
								firstOrderItemDto.getCompositeIdentifier());
			}

			final List<DeliverySettingDTO> productsByContract = editor
					.getSalesAreaMap().getServicesForFirstDeliverySetting(
							setting.getSapContractItem().getSapContract()
									.getSapContractId(), firstOrderItemDto.getProdPlantCd());

			final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
			jsonSerializer.exclude(DeliverySettingDTO.excludeEntities);

			String jsonProductList = jsonSerializer
					.serialize(productsByContract);

			addedItem.setJsonProductList(jsonProductList);

			request.setAttribute("orderItemIdx", orderItemIdx);
			request.setAttribute("editorIdx", Long.valueOf(selectedEditorIndex));

			return result = "addedOrderItem";
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String addBimodalRailPart(HttpServletRequest request,
			BestellungForm helpForm) throws Exception {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addBimodalRailPart", params);
		}
		// ]]
		try {
			Locale locale = request.getLocale();
			TimeZone userTimeZone = UserProvider.getTimezone();
			// --- Read the ship-to data (SP of the Truck part) ---
			OrderTakingEditorDto biTruckEditor = helpForm.getEditorList()
					.get(0);
			ShiptoSareaRestrEntity biRailRestr = biTruckEditor
					.getRestrictionMap().get(
							helpForm.getBimodalShiptoSareaRestrId());
			OrderTakingEditorRailDeliveryDto railData = null;

			// --- Bimodal rail part uses different shipto and contract =>
			// different shiptoMap! ---
			String bimodalSoldtoSapId = helpForm.getBimodalSoldtoSapId();
			SapBpEntity biRailSoldtoSapBp = getDispokarteService()
					.fetchSoldtoSapBpBySapId(bimodalSoldtoSapId);

			final ShiptoSareaEntity shiptoSarea = biRailRestr.getShiptoSarea();

			DeliverySettingShiptoMap biRailShiptoMap = getServiceLocator()
					.getDeliverySettingService().initDeliverySettingShiptoMapForBimodalOrderCreation(
							biRailSoldtoSapBp, locale,
							shiptoSarea, biRailRestr.getSapMaterial().getSapMaterialId());
			biRailShiptoMap.validateSalesAreas();

			final DeliverySettingSalesAreaMap biRailSalesAreaMapForSareaId = biRailShiptoMap
					.getSalesAreaMapForSareaId(shiptoSarea
							.getSarea().getSareaId());
			DeliverySettingDTO biRailSetting = new DeliverySettingDTO(
					biRailRestr, userTimeZone);
			biRailSetting = biRailSalesAreaMapForSareaId
					.getDeliverySettingByCompositeIdentifier(biRailSetting
							.getCompositeIdentifier());
			if (biRailSetting == null) {
				MessageUtils.addActionWarning(request,
						"bestellung.error.rail.bimodal.invalid.restriction");
			}

			// --- Calculating actual and earliest delivery date ---
			Long arrivalStationId = shiptoSarea
					.getShipto().getShiptoSapBp().getRailwayStation()
					.getRailwayStationId();
			ShippingpointEntity biRailRestrSp = biRailRestr.getShippingpoint();
			ShiptoEntity shipTo = helpForm.getShipTo();
			if (biRailRestrSp == null) {
				MessageUtils.addActionWarning(request,
						"bestellung.error.rail.bimodal.restriction.empty.sp");
			}
			else {
				if (biRailRestrSp.getRailwayStation() == null) {
					MessageUtils
							.addActionWarning(request,
									"bestellung.error.rail.bimodal.restriction.no.station.for.sp");
				}
				else {

					OrderTakingEditorDto selectedEditor = helpForm
							.getSelectedEditor();

					Calendar deliveryFrom = OrderTakingUtils
							.getEditorDeliveryFrom(selectedEditor,
									shipTo,
									helpForm.getShipToTimeZone());
					Calendar deliveryTo = OrderTakingUtils.getEditorDeliveryTo(
							selectedEditor, shipTo,
							helpForm.getShipToTimeZone());

					RailSchedulingService railSchedulingService = getRailSchedulingService();
					Long departureStationId = biRailRestrSp.getRailwayStation().getRailwayStationId();
					Date deliveryFromTime = deliveryFrom.getTime();
					Date deliveryToTime = deliveryTo.getTime();
					railData = railSchedulingService.calculateRailDeliveryData(
							departureStationId,
							arrivalStationId, 
							deliveryFromTime,
							deliveryToTime, 
							shipTo,
							userTimeZone,
							true);

					if (railData.getEarliestDeliveryDatDep() == null) {
						MessageUtils
								.addActionWarning(request,
										"bestellung.error.rail.bimodal.restriction.no.route");
					}
				}
			}

			// --- Validation the truck part, railway station info, etc ---
			processOrderEntityOfEditor(request, helpForm, biTruckEditor, "");
			
			// Set back the updated Order to the editor - preserve UI connection
			helpForm.setOrderCommon(biTruckEditor.getOrder());
			
			// --- Validation error (e.g. truck part is not valid, missing
			// railway station data, etc) ---
			if (MessageUtils.getActionWarnings(request) != null
					&& MessageUtils.getActionWarnings(request).size() > 0) {
				request.setAttribute("invalidBimodalTruckPart", true);
			}
			else {
				OrderTakingEditorDto biRailEditor = OrderTakingUtils
						.getOrderEditorInstance(getServiceLocator(),
								biRailShiptoMap, getLocale(request));

				biRailEditor.setRailData(railData);
				biRailEditor.setShiptoSarea(shiptoSarea);
				biRailEditor.setSalesAreaMap(biRailSalesAreaMapForSareaId);

				OrderEntity order = biRailEditor.getOrder();
				List<OrderTakingEditorOrderItemDto> railOrderItemDtos = biRailEditor
						.getOrderItems();
				OrderTakingEditorOrderItemDto firstRailItemDto = railOrderItemDtos
						.get(0);
				OrderCreateEntity orderHelper = biRailEditor.getOrderHelper();

				order.setActualDeliveryDat(railData.getActualDeliveryDatArr()); // TODO
																				// maybe
																				// can
																				// be
																				// removed,
																				// since
																				// it
																				// is
																				// needed
																				// to
																				// re-validate
																				// right
																				// before
																				// the
																				// saving

				// --- Item notes ---
				// append to the order itself. It comes from the master data,
				// thus it has to be be checked
				String note = com.crh.logon.utils.StringUtils
						.appendWithNewLine(order.getNoteDelivery(),
								biRailRestr.getNoteDelivery());
				note = OrderUtils.trimIfNeededWithErrorLogMessage(order, note,
						OrderEntity.ATTR_NOTE_DELIVERY,
						OrderEntity.DBSIZE_NOTE_DELIVERY);
				order.setNoteDelivery(note);

				note = com.crh.logon.utils.StringUtils.appendWithNewLine(
						order.getNoteDispatching(),
						biRailRestr.getNoteDispatching());
				note = OrderUtils.trimIfNeededWithErrorLogMessage(order, note,
						OrderEntity.ATTR_NOTE_DISPATCHING,
						OrderEntity.DBSIZE_NOTE_DISPATCHING);
				order.setNoteDispatching(note);

				note = com.crh.logon.utils.StringUtils.appendWithNewLine(
						order.getNoteOrderMgmt(),
						biRailRestr.getNoteOrderMgmt());
				note = OrderUtils.trimIfNeededWithErrorLogMessage(order, note,
						OrderEntity.ATTR_NOTE_ORDER_MGMT,
						OrderEntity.DBSIZE_NOTE_ORDER_MGMT);
				order.setNoteOrderMgmt(note);

				note = com.crh.logon.utils.StringUtils.appendWithNewLine(
						order.getNoteRailwayDisp(),
						biRailRestr.getNoteRailwayDisp());
				note = OrderUtils.trimIfNeededWithErrorLogMessage(order, note,
						OrderEntity.ATTR_NOTE_RAILWAY_DISP,
						OrderEntity.DBSIZE_NOTE_RAILWAY_DISP);
				order.setNoteRailwayDisp(note);

				note = com.crh.logon.utils.StringUtils.appendWithNewLine(
						order.getNoteTruckDriver(),
						biRailRestr.getNoteTruckDriver());
				note = OrderUtils.trimIfNeededWithErrorLogMessage(order, note,
						OrderEntity.ATTR_NOTE_TRUCK_DRIVER,
						OrderEntity.DBSIZE_NOTE_TRUCK_DRIVER);
				order.setNoteTruckDriver(note);

				// --- Material & ProdPlant & Quantity & UoM & DelType ---
				List<OrderTakingEditorOrderItemDto> truckOrderItemDtos = biTruckEditor
						.getOrderItems();
				OrderTakingEditorOrderItemDto firstTruckItemDto = truckOrderItemDtos
						.get(0);

				firstRailItemDto.setSapMatId(firstTruckItemDto.getSapMatId());
				firstRailItemDto.setProdPlantCd(firstTruckItemDto
						.getProdPlantCd());
				firstRailItemDto
						.setQuantityStr(biRailRestr.getQuantity() != null ? biRailRestr
								.getQuantity().toString() : "1");
				firstRailItemDto
						.setUomCd(biRailRestr.getUom() != null ? biRailRestr
								.getUom().getUomCd() : Constant.UOM_WAGON);
				firstRailItemDto.setSareaId(shiptoSarea
						.getSarea().getSareaId());
				orderHelper
						.setDeliveryTypeCd(Constant.DELIVERY_TYPE_BIMODAL_RAIL);

				PlantDTO prodPlant = null;
				String plantCd = firstRailItemDto.getProdPlantCd();
				List<PlantDTO> productionPlantList = biTruckEditor
						.getProdPlantList();
				for (PlantDTO currentProductionPlant : productionPlantList) {
					String currentProductionPlantCd = currentProductionPlant
							.getPlantCd();
					if (plantCd.equals(currentProductionPlantCd)) {
						prodPlant = currentProductionPlant;
					}
				}
				orderHelper.setProductionPlantName(prodPlant.getPlantName());

				/**
				 * In EBM there are no material conditions anymore, cannot set
				 * accordingly
				 */
				orderHelper.setRate(BigDecimal.ZERO);
				orderHelper.setCurrencyCd(Constant.CURRENCY_CODE_CHF);

				// --- SP and LP ---
				if (biRailRestr.getShippingpoint() != null) {
					orderHelper.setShippingpointId(biRailRestr
							.getShippingpoint().getShippingpointId());
				}
				if (biRailRestr.getLoadingpoint() != null) {
					firstRailItemDto.setLoadingpointId(biRailRestr
							.getLoadingpoint().getLoadingpointId());
				}

				// --- Auto pool/carrier flags ---
				if (biRailRestr.getAutoPoolAssignmentFlag() != null) {
					biRailEditor.setAutoPoolAssignmentFlag(biRailRestr
							.getAutoPoolAssignmentFlag());
				}
				if (biRailRestr.getAutoCarrierAssignmentFlag() != null) {
					biRailEditor.setAutoCarrierAssignmentFlag(biRailRestr
							.getAutoCarrierAssignmentFlag());
				}

				// --- Pool and Carrier ---
				if (biRailRestr.getPool() != null) {
					orderHelper.setPoolId(biRailRestr.getPool().getPoolId());
				}
				if (biRailRestr.getCommCarrier() != null) {
					orderHelper.setCommCarrierId(biRailRestr.getCommCarrier()
							.getCarrierId());
				}

				// --- Bill-to: rhytm is not used ---
				if (biRailRestr.getBilltoSapBp() != null) {
					orderHelper.setBilltoSapBpId(biRailRestr.getBilltoSapBp()
							.getSapBpId());
				}

				// --- Payer ---
				if (biRailRestr.getPayerSapBp() != null) {
					orderHelper.setPayerSapBpId(biRailRestr.getPayerSapBp()
							.getSapBpId());
				}

				// --- Release SAP flag and Free Delivery flag ---
				if (biRailRestr.getReleaseSapFlag() != null) {
					order.setReleaseSapFlag(!biRailRestr.getReleaseSapFlag());
				}
				order.setFreeDeliveryFlag(biRailRestr.getFreeDeliveryFlag());

				// --- Append to the editor list ---
				if (helpForm.getEditorList().size() == 1) {
					helpForm.getEditorList().add(1, biRailEditor);
				}
				else {
					helpForm.getEditorList().set(1, biRailEditor);
				}

				// --- Processing data for Supply Plant, SP, LP, Pool, Carrier
				// comboboxes ---
				OrderTakingEditorParamsDto paramsDto = new OrderTakingEditorParamsDto();

				OrderTakingEditorDto selectedEditor = helpForm
						.getSelectedEditor();

				Calendar deliveryFrom = OrderTakingUtils.getEditorDeliveryFrom(
						selectedEditor, shipTo,
						helpForm.getShipToTimeZone());
				Calendar deliveryTo = OrderTakingUtils.getEditorDeliveryTo(
						selectedEditor, shipTo,
						helpForm.getShipToTimeZone());

				paramsDto.setEditorIdx(1);
				paramsDto.setDelTypeCd(Constant.DELIVERY_TYPE_BIMODAL_RAIL);
				paramsDto.setDelFromDat(deliveryFrom.getTime());
				paramsDto.setDelToDat(deliveryTo.getTime());
				paramsDto.setUomCd(Constant.UOM_WAGON);
				paramsDto.setQuantity(biRailRestr.getQuantity());
				paramsDto.setSapMatId(biRailRestr.getSapMaterial()
						.getSapMaterialId()); // The material will never be null
				if (biRailRestr.getPool() != null) {
					paramsDto.setPoolId(biRailRestr.getPool().getPoolId());
				}
				if (biRailRestr.getProductionPlant() != null) {
					paramsDto.setProdPlantCd(biRailRestr.getProductionPlant()
							.getPlantCd());
				}
				if (biRailRestr.getShippingpoint() != null) {
					paramsDto.setSpId(biRailRestr.getShippingpoint()
							.getShippingpointId());
					paramsDto.setSupplyPlantCd(biRailRestr.getShippingpoint()
							.getPlant().getPlantCd());
				}

				Long shiptoSareaRestrId = biRailRestr.getShiptoSareaRestrId();
				paramsDto.setShiptoSareaRestrId(shiptoSareaRestrId);

				SapContractItemEntity sapContractItem = biRailRestr
						.getSapContractItem();

				// Old restrictions can still have a null reference to
				// sapContractItem.
				if (sapContractItem != null) {
					Long sapContractItemId = sapContractItem
							.getSapContractItemId();
					paramsDto.setSapContractItemId(sapContractItemId);
				}

				OrderTakingUtils util = new OrderTakingUtils(
						getServiceLocator(), locale);

				if (biRailSetting != null) {
					paramsDto.setCompositeIdentifier(biRailSetting
							.getCompositeIdentifier());
					util.getCollectedEditorData(paramsDto, request,
							request.getSession(true));

					// the bimodal rail material corresponds with the truck
					// order's material
					Long sapMaterialId = biRailSetting.getSapMaterialId();
					final SapMaterialDescriptionEntity bimodalDesc = getDispokarteService()
							.fetchSapMaterialDescription(sapMaterialId,
									request.getLocale().getLanguage());
					request.setAttribute("biRailMaterialDesc",
							bimodalDesc.getTxt());
				}

			} // END validation

			return result = "bimodalRailPart";
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String initiateSetCustorderToFinished(HttpServletRequest request) {
		// [[
		String result = null;
		Object[][] params = { { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initiateSetCustorderToFinished", params);
		}
		// ]]
		try {
			String custorderIdStr = request.getParameter("custorderId");
			Long custorderId = Long.valueOf(custorderIdStr);

			Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
			List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>();

			CustorderEntity custorderEntity = getServiceLocator()
					.getCustorderService().findCustorderById(custorderId);
			OrderUtils.fillOrderNumberLists(custorderEntity, deleteOrderMap,
					manualDeallocateOrderNumberList);

			request.setAttribute("sourceAction", "Bestellung");
			request.setAttribute("custorderId", custorderId);
			request.setAttribute("deleteOrderMap", deleteOrderMap);
			request.setAttribute("manualDeallocateOrderNumberList",
					manualDeallocateOrderNumberList);

			request.setAttribute("showCloseCustorderPopup", true);
			return result = "setCustorderToFinished";
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private String confirmSetCustorderToFinished(HttpServletRequest request,
			BestellungForm helpForm) {
		// [[
		String result = null;
		Object[][] params = { { "request", request }, { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "confirmSetCustorderToFinished", params);
		}
		// ]]
		try {
			String custorderIdStr = request.getParameter("custorderId");
			Long custorderId = Long.valueOf(custorderIdStr);

			Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
			List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>();

			CustorderEntity custorderEntity = getServiceLocator()
					.getCustorderService().findCustorderById(custorderId);
			OrderUtils.fillOrderNumberLists(custorderEntity, deleteOrderMap,
					manualDeallocateOrderNumberList);

			OrderService orderService = getServiceLocator().getOrderService();

			final CustorderLifecycleReturnCode returnCode = orderService
					.cascadeCloseCustomerOrder(deleteOrderMap, custorderId);

			if (returnCode.isSuccess()) {
				updateBestellungFormOrderLists(request, RELOAD_ALL_CUSTORDERS);
				MessageUtils.addActionMessage(request,
						"custorder.message.closed.successful");
				request.setAttribute("setCustorderToFinishedSuccess", true);
			}
			else {
				MessageUtils.addActionWarning(request,
						"custorder.warning.cannotsettofinished");
			}

			request.setAttribute("setCustorderToFinished", true);
			return result = "setCustorderToFinished";
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void processOrderEntityOfEditor(HttpServletRequest request,
			BestellungForm helpForm, OrderTakingEditorDto editor, String index)
			throws Exception {
		// [[
		Object[][] params = { { "request", request }, { "helpForm", helpForm },
				{ "editor", editor } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processOrderEntityOfEditor", params);
		}
		// ]]
		try {

			Locale locale = request.getLocale();

			RefDataGenericsService refData = getRefDataGenericsService();
			BestellungService orderSrv = getBestellungService();
			DispokarteService dispoSrv = getDispokarteService();
			TruckAdminService truckSrv = getServiceLocator()
					.getTruckAdminService();
			OrderEntity order = editor.getOrder();
			ShiptoEntity shipto = helpForm.getShipTo();
			OrderCreateEntity helper = editor.getOrderHelper();			
			OrderEntity orderCommon = helpForm.getOrderCommon();
			List<OrderItemEntity> itemList = new ArrayList<OrderItemEntity>();
			OrderTakingEditorOrderItemDto firstItemDto = editor.getOrderItems()
					.get(0);
			boolean isDualEditor = helpForm.getEditorList().size() > 1;
			boolean isDualEditorRailPart = helpForm.getEditorList().size() > 1
					&& LogonUtil.isBimodalRailPartOrder(helper
							.getDeliveryTypeCd());

			boolean isServiceOnlyOrder = false;
			if (editor.getServiceOnlyOrderFlag() != null) {
				isServiceOnlyOrder = editor.getServiceOnlyOrderFlag()
						.booleanValue();
			}

			if (Constant.INVALID_OBJECT_ID_STR.equals(editor
					.getServiceProcessIndicatorId())) {
				order.setSpi(null);
			}
			else {
				OrderSpiBsEntity serviceProcessIndicator = getRefDataGenericsService()
						.findBsEntity(OrderSpiBsEntity.class,
								editor.getServiceProcessIndicatorId());
				order.setSpi(serviceProcessIndicator);
			}

			order.setReleaseSapFlag(editor.getReleaseSapFlag());
			order.setFreeDeliveryFlag(editor.getFreeDeliveryFlag());
			order.setOnHoldOrderAndLockedFlag(editor
					.getOnHoldOrderAndLockedFlag());
			order.setSapSuppressMinChargeFlag(editor
					.getSapSuppressMinChargeFlag());
			
			// Mar 26, 2015/fszabo: LOGON-9292 these fields are actually initialized elsewhere, no need here.
//			order.setNoteOrderMgmt(shipto.getNoteOrderMgmt());
//			order.setNoteDelivery(shipto.getNoteDelivery());
//			order.setNoteDispatching(shipto.getNoteDispatching());
//			order.setNoteTruckDriver(shipto.getNoteTruckDriver());
//			order.setNoteRailwayDisp(shipto.getNoteRailwayDisp());			


			// KPI 7. 
			// Indicates if the order was created based on a dispocard
			Boolean isDispoCardAvailable = false;
			// if the field is not controlled in the dispocard we indicate it with a null;
			// KPI 14
			Boolean deliveryTypeDiff = null; // true if the dispocards delivery type matches the orders delvery type
			ShiptoSareaRestrEntity restriction = null;
			if (editor.getOrderItems() != null && editor.getOrderItems().size() > 0) {
				// only check the first order item. The delivery type should be the same for all items
				// this is quite common in the project to take the data from only the first item for decisions. confirmed with Nicolas
				OrderTakingEditorOrderItemDto itemDto = editor.getOrderItems().get(0);
								
				if (itemDto.getShiptoSareaRestrId() != null) {					
					restriction = getServiceLocator().getCarrierAssignmentService().fetchRestricitonById(itemDto.getShiptoSareaRestrId());
					if (restriction != null) {
						
						isDispoCardAvailable = true;
						
						if (restriction.getDeliveryType() != null) {
							Long orderDeliveryTypeCd = editor.getDelTypeCd();
							Long restrictionDeliveryTypeCd =  restriction.getDeliveryType().getDeliveryTypeCd();
							deliveryTypeDiff = orderDeliveryTypeCd.equals(restrictionDeliveryTypeCd);
						}
					}
				}
			}
			
			order.setDelivTypePlanActual(deliveryTypeDiff);
			
			// --- Iterate through multiple items ---
			for (int i = 0; i < editor.getOrderItems().size(); i++) {
				OrderTakingEditorOrderItemDto itemDto = editor.getOrderItems()
						.get(i);
				if (itemDto != null) {
					if (!OrderTakingUtils.isValidSelection(itemDto
							.getSapMatId())) {
						MessageUtils.addActionWarning(request,
								"error.field.required", MessageUtils
										.getMessage(request, locale,
												"fields.produkt.produkt"));
						return;
					}

					OrderItemEntity item = itemDto.getOrderItem();
					item.setOrder(order);

					String replacementMaterialError = validatePlanningMaterialReplacement(
							itemDto, helpForm.getShiptoMap());
					if (replacementMaterialError != null) {
						MessageUtils.addActionWarning(request,
								replacementMaterialError);
					}

					setSourceDependentAttributes(editor, itemDto);

					// --- QUANTITY ---
					BigDecimal orderQuantity;
					try {
						orderQuantity = new BigDecimal(itemDto.getQuantityStr());
						if (Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE
								.compareTo(orderQuantity) < 0
								|| Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE
										.compareTo(orderQuantity) > 0
								|| com.crh.logon.utils.StringUtils
										.getDecimalFractionLength(itemDto
												.getQuantityStr()) > 3) {
							MessageUtils.addActionWarning(request,
									"bestellung.error.invalid.quantity",
									getEditorLocatorMessage(request)
											.replaceAll("@", index));
						}
					}
					catch (NumberFormatException e) {
						MessageUtils.addActionWarning(
								request,
								"bestellung.error.invalid.quantity",
								getEditorLocatorMessage(request).replaceAll(
										"@", index));
						orderQuantity = null;
					}

					// --- UOM ---
					if (!StringUtils.isEmpty(itemDto.getUomCd())) {
						UomBsEntity uom = refData.findBsEntity(
								UomBsEntity.class, itemDto.getUomCd());
						if (uom != null && uom.getCreateMultiOrderFlag()
								&& orderQuantity != null
								&& orderQuantity.compareTo(BigDecimal.ZERO) > 0) {
							if (!Double.valueOf(
									Math.ceil(orderQuantity.doubleValue()))
									.equals(orderQuantity.doubleValue())) {
								MessageUtils
										.addActionWarning(
												request,
												"bestellung.error.multiple.truck.quantity.fraction",
												getEditorLocatorMessage(request)
														.replaceAll("@", index));
							}
							// FIXME (lajos/27.09.2012): clarify special
							// container case
							// else if
							// (Constant.UOM_CONTAINER.equals(itemDto.getUomCdprocessOrderEntityOfEditor())
							// && orderQuantity.doubleValue() > 3) {
							// MessageUtils.addActionWarning(request,"bestellung.error.multiple.container.item.max");
							// }
						}
						item.setOrderedUom(uom);
					}
					if (item.getOrderedUom() == null) {
						MessageUtils.addActionWarning(request,
								"error.field.required", MessageUtils
										.getMessage(request, locale,
												"fields.uom.uom"));
					}
					else if (item.getOrderedUom().getFuzzyQuantityFlag() != null
							&& item.getOrderedUom().getFuzzyQuantityFlag()
									.booleanValue()) {
						if (LogonUtil.isSimpleVehicleOrder(helper
								.getDeliveryTypeCd())) {
							MessageUtils
									.addActionWarning(request,
											"application.errors.custordervalidation.INVALID_ORDERED_UOM_SIPLVEH_FUZZY");
						}
					}
					item.setOrderedQuantity(orderQuantity);

					// --- PRODUCTION PLANT ---
					if (OrderTakingUtils.isValidSelection(itemDto
							.getProdPlantCd())) {
						item.setProductionPlant(refData.findBsEntity(
								PlantBsEntity.class, itemDto.getProdPlantCd()));

						if (OrderTakingUtils.isValidSelection(itemDto
								.getMixDesignId())) {
							List<FireqMaterialMixdesignEntity> fireqMaterialMixdesignList = editor
									.getMixDesignList();
							Long mixDesignId = itemDto.getMixDesignId();
							for (FireqMaterialMixdesignEntity currentFireqMaterialMixdesign : fireqMaterialMixdesignList) {
								Long currentFireqMaterialMixdesignId = currentFireqMaterialMixdesign
										.getMixDesignId();
								if (mixDesignId
										.equals(currentFireqMaterialMixdesignId)) {
									item.setMixDesign(currentFireqMaterialMixdesign);
									break;
								}
							}
						}
					}

					// --- BIMODAL TRUCK PART: RAIL ORDER ITEM ---
					if (LogonUtil.isBimodalTruckPartOrder(helper
							.getDeliveryTypeCd())
							&& !"addBimodalRailPart".equals(request
									.getParameter("action"))
							&& helpForm.getEditorList().size() == 1
							&& !LogonPackageHandlingUtil.isService(item)) {
						if (OrderTakingUtils.isValidSelection(helper
								.getBimodalRailOrderItemId())) {
							item.setBimodalRailOrderItem(orderSrv.fetchOrderItem(helper.getBimodalRailOrderItemId()));
						}
						else {
							MessageUtils
									.addActionWarning(
											request,
											"error.field.required",
											MessageUtils
													.getMessage(request,
															locale,
															"bestellung.rail.order.bimodal.item"));
						}
					}

					// --- SILO ---
					if (helper.getShiptoSiloId() != null) {
						Long shiptoSiloId = helper.getShiptoSiloId();
						List<ShiptoSiloEntity> activeShiptoSiloList = helpForm
								.getShiptoActiveSilos();
						for (ShiptoSiloEntity currentShiptoSilo : activeShiptoSiloList) {
							Long currentShiptoSiloId = currentShiptoSilo
									.getShiptoSiloId();
							if (shiptoSiloId.equals(currentShiptoSiloId)) {
								item.setShiptoSilo(currentShiptoSilo);
								break;
							}
						}
					}

					if (!LogonUtil.isBimodalTruckPartOrder(helper
							.getDeliveryTypeCd())
							&& firstItemDto.getLoadingpointId() != null) {
						if (i == 0) {
							item.setLoadingpoint(orderSrv
									.getLoadingPointById(firstItemDto
											.getLoadingpointId()));
						}
						else {
							item.setLoadingpoint(firstItemDto.getOrderItem()
									.getLoadingpoint()); // For 2nd+ items, the
															// LP is the same as
															// of the first item
						}
					}
					
					// if the field is not controlled in the dispocard we indicate it with a null;
					// KPI 15
					Boolean isQtyOrUomDifferent = null;
					if (restriction != null) {
						// if any of them is not available we leave the diff flag null to indicate the fact.
						if (restriction.getUom() != null && restriction.getQuantity() != null && item.getOrderedUom() != null) {
							String orderUomCd = item.getOrderedUom().getUomCd();
							String restrictionUomCd = restriction.getUom().getUomCd();
							isQtyOrUomDifferent = restrictionUomCd != null && restrictionUomCd.equals(orderUomCd);
							
							BigDecimal orderQty = item.getOrderedQuantity();
							BigDecimal restrictionQty = restriction.getQuantity();
							isQtyOrUomDifferent = isQtyOrUomDifferent && restrictionQty != null && restrictionQty.equals(orderQty);
						}							
					}
					
					item.setDispocardAvailalbe(isDispoCardAvailable);
					item.setOrderPlanActual(isQtyOrUomDifferent);

					mergeOrderNote(order, itemDto);

					itemList.add(item);
				}
				
				order.setOrderItems(itemList);
			}

			// --- DELIVERY TYPE ---
			if (OrderTakingUtils.isValidSelection(helper.getDeliveryTypeCd())) {
				order.setDeliveryType(refData.findBsEntity(
						DeliveryTypeBsEntity.class, helper.getDeliveryTypeCd()));
			}
			else {
				if (!isServiceOnlyOrder) { // delivery type is mandatory except
											// for service only orders
					MessageUtils.addActionWarning(request,
							"error.field.required", MessageUtils.getMessage(
									request, locale,
									"fields.konditionen.liefertyp"));
				}
			}

			// --- LNF: not relevant for bimodal rail part ---
			ShiptoEntity shipTo = helpForm.getShipTo();
			if (Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED == shipTo
					.getShiptoType().getShiptoTypeCd()
					&& !LogonUtil.isBimodalRailPartOrder(helper
							.getDeliveryTypeCd())) {
				// --- Coordinates for LNF ship-to ---
				if (orderCommon.getLnfCoordLat() == null
						|| orderCommon.getLnfCoordLat() == 0
						|| orderCommon.getLnfCoordLon() == null
						|| orderCommon.getLnfCoordLon() == 0) {
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.invalid.lnf.coordinates",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
				// --- Must be in a zone ---
				if (OrderTakingUtils.isValidSelection(helper
						.getDeliveryTrspZoneCd())) {
					order.setDeliveryTrspZone(refData.findBsEntity(
							TrspZoneBsEntity.class,
							helper.getDeliveryTrspZoneCd()));
				}
				else {
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.lnf.coordinates.no.zone",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
				// --- ZIP/City for LNF is mandatory (LOGON-6577)
				if (StringUtils.isEmpty(orderCommon.getLnfSitePostalCode())
						|| StringUtils.isEmpty(orderCommon.getLnfSiteCity())) {
					MessageUtils.addActionWarning(request,
							"error.field.required", MessageUtils.getMessage(
									request, locale,
									"bestellung.lnf.site.zip.city"));
				}
				// --- Country for LNF is mandatory (LOGON-6577)
				if (StringUtils.isEmpty(orderCommon.getLnfSiteCountry())) {
					MessageUtils.addActionWarning(request,
							"error.field.required", MessageUtils.getMessage(
									request, locale,
									"bestellung.lnf.site.country"));
				}
				// --- VAT number may be mandatory ---
				if (order.getSapContract().getVatNoMandatoryFlag() != null
						&& order.getSapContract().getVatNoMandatoryFlag()
						&& StringUtils.isEmpty(orderCommon.getLnfSiteVatNo())) {
					MessageUtils.addActionWarning(
							request,
							"error.field.required",
							MessageUtils.getMessage(request, locale,
									"bestellung.lnf.shipping.address")
									+ " / "
									+ MessageUtils.getMessage(request, locale,
											"bestellung.lnf.site.vat"));
				}
				// --- Setup configured timezone ---//
				TimezoneBsEntity tzEntity = getServiceLocator()
						.getRefDataGenericsService().findBsEntity(TimezoneBsEntity.class,
								helpForm.getLnfSelectedTimeZoneCd());
				orderCommon.setLnfTimezone(tzEntity);
			}

			// --- ONE-TIME SOLDTO ADDRESS
			DeliverySettingShiptoMap shiptoMap = helpForm.getShiptoMap();
			SapBpEntity soldTo = shiptoMap.getSoldTo();

			if (soldTo.getTreatAsOnetimeFlag().booleanValue()) {
				if (StringUtils.isEmpty(orderCommon.getOneTimeSoldtoName())) {
					MessageUtils.addActionWarning(
							request,
							"error.field.required",
							MessageUtils.getMessage(request, locale,
									"bestellung.onetimesoldto.address.title")
									+ " / "
									+ MessageUtils.getMessage(request, locale,
											"bestellung.onetimesoldto.name"));
				}
				if (StringUtils.isEmpty(orderCommon
						.getOneTimeSoldtoPostalCode())
						|| StringUtils.isEmpty(orderCommon
								.getOneTimeSoldtoCity())) {
					MessageUtils
							.addActionWarning(
									request,
									"error.field.required",
									MessageUtils
											.getMessage(request, locale,
													"bestellung.onetimesoldto.address.title")
											+ " / "
											+ MessageUtils
													.getMessage(request,
															locale,
															"bestellung.onetimesoldto.zip.city"));
				}
				if (StringUtils.isEmpty(orderCommon.getOneTimeSoldtoCountry())) {
					MessageUtils.addActionWarning(request,
							"error.field.required", MessageUtils.getMessage(
									request, locale,
									"bestellung.onetimesoldto.country"));
				}
			}

			final Calendar deliveryTo;
			final Calendar deliveryFrom;

			// In case of bimodal rail part we overwrite to use the delivery
			// intervals of the truck part as the rail part won't have those
			if (isDualEditorRailPart
					&& LogonUtil.isBimodalRailPartOrder(helper
							.getDeliveryTypeCd())) {
				List<OrderTakingEditorDto> editorList = helpForm
						.getEditorList();
				OrderTakingEditorDto bimodalTruckEditor = editorList.get(0);

				deliveryTo = OrderTakingUtils.getEditorDeliveryTo(
						bimodalTruckEditor, shipTo,
						helpForm.getShipToTimeZone());
				deliveryFrom = OrderTakingUtils.getEditorDeliveryFrom(
						bimodalTruckEditor, shipTo,
						helpForm.getShipToTimeZone());
			}
			else {
				deliveryTo = OrderTakingUtils.getEditorDeliveryTo(editor,
						shipTo, helpForm.getShipToTimeZone());
				deliveryFrom = OrderTakingUtils.getEditorDeliveryFrom(editor,
						shipTo, helpForm.getShipToTimeZone());
			}

			// --- DELIVERY FROM/TO: not needed for bimodal rail part in case of
			// 2 editor rows (it will be automatically set during the rail part
			// editor initialization) ---
			if (!LogonUtil.isBimodalRailPartOrder(helper.getDeliveryTypeCd())
					|| helpForm.getEditorList().size() == 1) {
				boolean validDates = true;
				try {
					if (deliveryFrom.getTime().before(
							DateTimeUtils.cleanTimeWithDefaultTimeZone(DateTimeUtils.getNow()))) {
						throw new Exception();
					}
					Calendar delFrom = LogonUtil.isRailOrder(helper
							.getDeliveryTypeCd()) ? DateTimeUtils
							.getMorningOfDay(deliveryFrom) : deliveryFrom;
					order.setDeliveryFromDat(delFrom.getTime());
				}
				catch (Exception e) {
					validDates = false;
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.invalid.delivery.from",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
				try {
					Calendar delTo = LogonUtil.isRailOrder(helper
							.getDeliveryTypeCd()) ? DateTimeUtils
							.getMidnightOfDay(deliveryTo) : deliveryTo;
					order.setDeliveryToDat(delTo.getTime());
				}
				catch (Exception e) {
					validDates = false;
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.invalid.delivery.to",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}

				if (validDates
						&& order.getDeliveryFromDat().after(
								order.getDeliveryToDat())) {
					validDates = false;
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.invalid.delivery.earlier.later",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
				if (validDates
						&& order.getDeliveryToDat().before(
								DateTimeUtils.getNow())) {
					validDates = false;
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.invalid.delivery.future",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
			}

			// --- DELIVERY TIME TOLERANCE ---
			String deliveryTimeToleranceStr = editor
					.getDeliveryTimeToleranceStr();
			if (StringUtils.isNotBlank(deliveryTimeToleranceStr)) {
				try {
					Double deliveryTimeTolerance = new Double(
							deliveryTimeToleranceStr);
					if (Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MAX_VALUE
							.compareTo(deliveryTimeTolerance) < 0
							|| Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE
									.compareTo(deliveryTimeTolerance) > 0) {
						MessageUtils
								.addActionWarning(
										request,
										"bestellung.error.invalid.delivery.time.tolerance",
										Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR,
										Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR,
										getEditorLocatorMessage(request)
												.replaceAll("@", index));
					}
				}
				catch (NumberFormatException e) {
					MessageUtils
							.addActionWarning(
									request,
									"bestellung.error.invalid.delivery.time.tolerance",
									Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR,
									Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR,
									getEditorLocatorMessage(request)
											.replaceAll("@", index));
				}
			}

			// --- QUANTITY PER DELIVERY ---
			String qtyPerSdoStr = editor.getQuantityPerSdoStr();
			if (StringUtils.isNotBlank(qtyPerSdoStr)) {
				try {
					BigDecimal qtyPerSdo = new BigDecimal(qtyPerSdoStr);
					if (Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE
							.compareTo(qtyPerSdo) < 0
							|| Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE
									.compareTo(qtyPerSdo) > 0) {
						MessageUtils
								.addActionWarning(
										request,
										"bestellung.error.invalid.quantity.per.delivery",
										Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR,
										Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR,
										getEditorLocatorMessage(request)
												.replaceAll("@", index));
					}
				}
				catch (NumberFormatException e) {
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.invalid.quantity.per.delivery",
							Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR,
							Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR,
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
			}

			// --- DELIVERY FREQUENCY EVERY TIME ---
			String deliveryFqyEveryTimeStr = editor
					.getDeliveryFqyEveryTimeStr();
			if (StringUtils.isNotBlank(deliveryFqyEveryTimeStr)) {
				try {
					Double deliveryFqyEveryTime = new Double(
							deliveryFqyEveryTimeStr);
					if (Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE
							.compareTo(deliveryFqyEveryTime) < 0
							|| Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE
									.compareTo(deliveryFqyEveryTime) > 0) {
						MessageUtils
								.addActionWarning(
										request,
										"bestellung.error.invalid.delivery.frequency",
										Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR,
										Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR,
										getEditorLocatorMessage(request)
												.replaceAll("@", index));
					}
				}
				catch (NumberFormatException e) {
					MessageUtils
							.addActionWarning(
									request,
									"bestellung.error.invalid.delivery.frequency",
									Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR,
									Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR,
									getEditorLocatorMessage(request)
											.replaceAll("@", index));
				}
			}

			// --- DELIVERY FREQUENCY QUANTITY PER HRE ---
			String deliveryFqyQtyPerHreStr = editor
					.getDeliveryFqyQtyPerHreStr();
			if (StringUtils.isNotBlank(deliveryFqyQtyPerHreStr)) {
				try {
					BigDecimal deliveryFqyQtyPerHre = new BigDecimal(
							deliveryFqyQtyPerHreStr);
					if (Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE
							.compareTo(deliveryFqyQtyPerHre) < 0
							|| Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE
									.compareTo(deliveryFqyQtyPerHre) > 0) {
						MessageUtils
								.addActionWarning(
										request,
										"bestellung.error.invalid.delivery.frequency",
										Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR,
										Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR,
										getEditorLocatorMessage(request)
												.replaceAll("@", index));
					}
				}
				catch (NumberFormatException e) {
					MessageUtils
							.addActionWarning(
									request,
									"bestellung.error.invalid.delivery.frequency",
									Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR,
									Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR,
									getEditorLocatorMessage(request)
											.replaceAll("@", index));
				}
			}

			// --- SHIPPING POINT---
			if (OrderTakingUtils.isValidSelection(helper.getShippingpointId())) {
				ShippingPointService shippingPointService = getShippingPointService();
				order.setShippingpoint(shippingPointService.getShippingPointById(helper.getShippingpointId()));
			}
			else {
				// shipping point is mandatory even for service only orders
				MessageUtils.addActionWarning(request, "error.field.required",
						MessageUtils.getMessage(request, locale,
								"fields.defaults.versandstelle"));
			}

			// --- ACTUAL DELIVERY DATE FOR RAIL ORDER ---
			if (LogonUtil.isRailOrder(helper.getDeliveryTypeCd())
					&& OrderTakingUtils.isValidSelection(helper
							.getShippingpointId())) {

				// --- For rail, the time part of the delivery to is 23:59 ---
				Calendar delToMidnight = DateTimeUtils
						.getMidnightOfDay(deliveryTo);

				// --- Recalculating to ensure that the train has not been
				// missed while editing the order ---
				TimeZone userTimeZone = UserProvider.getTimezone();
				OrderTakingEditorRailDeliveryDto railData = getRailSchedulingService()
						.calculateRailDeliveryData(
								order.getShippingpoint().getRailwayStation()
										.getRailwayStationId(),
								order.getShiptoSapBp().getRailwayStation()
										.getRailwayStationId(),
								LogonUtil.isRailOrder(helper
										.getDeliveryTypeCd()) ? DateTimeUtils
										.getMorningOfDay(deliveryFrom.getTime())
										: deliveryFrom.getTime(),
								LogonUtil.isRailOrder(helper
										.getDeliveryTypeCd()) ? DateTimeUtils
										.getMidnightOfDay(deliveryTo).getTime()
										: deliveryTo.getTime(),
								shipTo, 
								userTimeZone,
								isDualEditor);

				Date railActualDeliveryDat = railData.getActualDeliveryDatArr();
				editor.setRailData(railData);
				// editor.setRailData(OrderTakingUtils.getRailDeliveryData(timetableEntity,
				// helpForm.getDeliveryTo().getTime()));

				if (railActualDeliveryDat != null) {
					if (railActualDeliveryDat.after(delToMidnight.getTime())) {
						if (LogonUtil.isBimodalRailPartOrder(helper
								.getDeliveryTypeCd())) {
							MessageUtils
									.addActionWarning(request,
											"bestellung.error.bimodal.rail.earliest.too.late");
						}
						else {
							MessageUtils.addActionWarning(request,
									"bestellung.error.rail.earliest.too.late");
						}
					}
					else {
						order.setActualDeliveryDat(railActualDeliveryDat);
						order.setDeliveryFromDat(DateTimeUtils.getMorningOfDay(
								deliveryFrom).getTime());
						order.setDeliveryToDat(DateTimeUtils.getMidnightOfDay(
								deliveryTo).getTime());
					}
				}
				else {
					MessageUtils
							.addActionWarning(
									request,
									"error.field.required",
									MessageUtils
											.getMessage(request, locale,
													"bestellung.rail.order.actual.delivery.date"));
				}
			}

			// --- VEHICLE TYPE ---
			if (LogonUtil.isRoadDirectOrder(helper.getDeliveryTypeCd())
					|| LogonUtil.isPickupOrder(helper.getDeliveryTypeCd())
					|| LogonUtil.isSimpleVehicleOrder(helper
							.getDeliveryTypeCd())
					|| LogonUtil.isShipPickupOrder(helper.getDeliveryTypeCd())
					|| LogonUtil.isBlockTrainPickupOrder(helper
							.getDeliveryTypeCd())
					|| LogonUtil.isReturnDeliveredOrder(helper
							.getDeliveryTypeCd())) {
				if (!OrderTakingUtils.isValidSelection(editor
						.getSelectedTruckTypes())) {
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.required.vehicletype",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
			} else {
				if (!LogonUtil.isReturnPickupOrder(helper.getDeliveryTypeCd()) && !isServiceOnlyOrder) {
					List<TruckWagonTypeWrapper> vehicleTypes = editor.getVehicleTypeWrapperList();
					if(vehicleTypes.isEmpty()) {
						LOG.error("vehicleTypeWrapperList is null or empty");
						MessageUtils.addActionWarning(
								request,
								"bestellung.error.required.wagon",
								getEditorLocatorMessage(request).replaceAll(
										"@", index));
					}
				}
			}
			

			// --- POOL ---
			if (OrderTakingUtils.isValidSelection(helper.getPoolId())) {
				order.setPool(truckSrv.fetchPool(helper.getPoolId())); // nullable
			}

			// --- CARRIER ---
			if (OrderTakingUtils.isValidSelection(helper.getCommCarrierId())) {

				CarrierEntity commercialCarrier = truckSrv.fetchCarrier(helper.getCommCarrierId());
				order.setCommCarrier(commercialCarrier);

				if (LogonUtil.isRailOrder(helper.getDeliveryTypeCd())) {
					order.setExecCarrier(commercialCarrier);
				}
				else if (!LogonUtil.isPickupOrder(helper.getDeliveryTypeCd()) && OrderTakingUtils.isValidSelection(helper.getExecCarrierId())) {
					CarrierEntity executingCarrier = truckSrv.fetchCarrier(helper.getExecCarrierId());
					order.setExecCarrier(executingCarrier);
				}

			}

			// --- BILL-TO: always mandatory in Logon 2.0 (EBM) ---
			Long billtoSapBpId = helper.getBilltoSapBpId();

			if (OrderTakingUtils.isValidSelection(billtoSapBpId)) {
				SapBpEntity billtoSapBpById = dispoSrv
						.getBilltoSapBpById(billtoSapBpId);
				order.setBilltoSapBp(billtoSapBpById); // nullable
			}
			else {
				MessageUtils.addActionWarning(request, "error.field.required",
						MessageUtils.getMessage(request, locale,
								"fields.billto.billto"));
			}

			// --- PAYER: not always mandatory ---
			Long payerSapBpId = helper.getPayerSapBpId();

			if (OrderTakingUtils.isValidSelection(payerSapBpId)) {
				SapBpEntity payerSapBp = dispoSrv
						.getPayerSapBpById(payerSapBpId);
				order.setPayerSapBp(payerSapBp); // nullable
			}
			else if (!OrderUtils.isPayerIgnored(order.getSapContract())) {
				MessageUtils.addActionWarning(request, "error.field.required",
						MessageUtils.getMessage(request, locale,
								"fields.payer.payer"));
			}

			// --- FREE DELIVERY ---
			if (order.getFreeDeliveryFlag()
					&& billtoSapBpId != null
					&& !billtoSapBpId.equals(order.getSoldtoSapBp()
							.getSapBpId())) {
				MessageUtils
						.addActionWarning(
								request,
								"bestellung.error.freedelivery.impossible",
								getEditorLocatorMessage(request).replaceAll(
										"@", index));
			}

			// --- TRANSPORT DURATION, DISTANCE
			if (!isDualEditorRailPart
					&& OrderTakingUtils.isValidSelection(helper
							.getShippingpointId())
					&& !LogonUtil.isAnyTypeOfPickupOrder(helper
							.getDeliveryTypeCd())
					&& !LogonUtil
							.isServiceOnlyOrder(helper.getDeliveryTypeCd())) {
				Long uncorrectedDurationMin = null;// can be null, if no raw
													// data from google is
													// received (but
													// TruckRouteEntity)
				Long transportDurationMin = null;// in the end it cannot be null
				Long uncorrectedTransportDistanceM = null;// in the end it cannot be null
				Long transportDistanceM = null;
				// transform duration and distance received from google maps js
				// api -> uncorrectedDurationMin, transportDurationMin,
				// transportDistanceM
								
				if (!StringUtils.isEmpty(editor.getDurationTime())
						&& editor.getDurationCorrFactor() != null) {
					try {
						double correctedDurationMin = Double.valueOf(editor
								.getDurationTime());
						double correctionFactor = editor
								.getDurationCorrFactor();

						if (correctionFactor != 0) {
							uncorrectedDurationMin = Math
									.round(correctedDurationMin
											/ correctionFactor);
							transportDurationMin = Math
									.round(uncorrectedDurationMin
											* correctionFactor);
						}
					}
					catch (NumberFormatException e) {
						MessageUtils.addActionWarning(
								request,
								"bestellung.error.invalid.duration",
								getEditorLocatorMessage(request).replaceAll(
										"@", index));
					}
				}
				
				if (!StringUtils.isEmpty(editor.getDistanceMeters())) {
					uncorrectedTransportDistanceM = Long.valueOf(editor.getDistanceMeters());
					if (editor.getDistanceCorrectionFactor() != null) {
						transportDistanceM = Math.round(uncorrectedTransportDistanceM
								* editor.getDistanceCorrectionFactor());
					}
				}

				/**
				 * use js duration and distance values as default; if invalid:
				 * retrieve from truck route table/google service; store in the
				 * order
				 */
				try {
					order = getOrderService()
							.calculateAndSaveDistanceAndDuration(order,
									uncorrectedDurationMin,
									transportDurationMin, uncorrectedTransportDistanceM, transportDistanceM);
				}
				catch (LogonInvalidDurationException invalidDurationException) {
					MessageUtils.addActionWarning(
							request,
							"bestellung.error.invalid.duration",
							getEditorLocatorMessage(request).replaceAll("@",
									index));
				}
				editor.setOrder(order);
			}
			else if (isDualEditorRailPart) {
				Long duration = editor.getRailData().getDurationMinutes();
				Long distance = editor.getRailData().getDistanceMeters();
				order.setTransportDurationMin(duration > 0 ? duration : 1); // -1
																			// or
																			// 0
																			// means
																			// n.a.,
																			// save
																			// 1
																			// in
																			// that
																			// case
																			// as
																			// default
				long transportDistanceM = distance > 0 ? distance : 1;
				order.setTransportDistanceM(transportDistanceM); // -1 or 0
																	// means
																	// n.a.,
																	// save 1 in
																	// that case
																	// as
																	// default
				order.setCommercialDistanceM(transportDistanceM);
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

	private String validatePlanningMaterialReplacement(
			OrderTakingEditorOrderItemDto itemDto,
			DeliverySettingShiptoMap shiptoMap) {
		// [[
		String result = null;
		Object[][] params = { { "itemDto", itemDto },
				{ "shiptoMap", shiptoMap } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validatePlanningMaterialReplacement", params);
		}
		// ]]
		try {
			String planningMaterialError = null;

			String planningMaterialNo = null;
			if (Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING.equals(itemDto
					.getSapMatNo())) {
				planningMaterialNo = Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING;
				planningMaterialError = "bestellung.error.noReplacementMaterialLandfill";
			}
			else if (Constant.SAP_MATERIAL_NO_AGG_PLANNING.equals(itemDto
					.getSapMatNo())) {
				planningMaterialNo = Constant.SAP_MATERIAL_NO_AGG_PLANNING;
				planningMaterialError = "bestellung.error.noReplacementMaterialAgg";
			}

			if (planningMaterialNo == null) {
				planningMaterialError = null;
			}
			else {
				boolean hasReplacement = OrderTakingUtils
						.hasRealMaterialForReplacePlanningMaterial(itemDto,
								planningMaterialNo, shiptoMap);
				if (hasReplacement) {
					planningMaterialError = null;
				}
			}
			return result = planningMaterialError;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public String getEditorLocatorMessage(HttpServletRequest request) {
		return MessageUtils.getMessage(request,
				"bestellung.error.editorlocator");
	}

	/**
	 * Merge the item notes (from the new order form) with the order notes.
	 * i.e.: <code>OrderEntity.getNoteDelivery()</code> with
	 * <code>OrderTakingEditorOrderItemDto.getItemNoteDelivery()</code>.
	 * 
	 * @param orderEntity
	 * @param itemDto
	 */
	private void mergeOrderNote(OrderEntity orderEntity,
			OrderTakingEditorOrderItemDto itemDto) {
		// [[
		Object[][] params = { { "orderEntity", orderEntity },
				{ "itemDto", itemDto } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mergeOrderNote", params);
		}
		// ]]
		try {
			if (orderEntity == null || itemDto == null) {
				LOG.warn("The parameter 'itemDto' and/or 'orderEntity' is <NULL>");

				return; // nothing todo
			}

			// --- Append notes from itemDTOs. The max length is already checked
			// (see logon.js)
			String itemNoteDelivery = itemDto.getItemNoteDelivery();
			String itemNoteDispatching = itemDto.getItemNoteDispatching();
			String itemNoteOrderMgmt = itemDto.getItemNoteOrderMgmt();
			String itemNoteRailway = itemDto.getItemNoteRailway();
			String itemNoteTruckDriver = itemDto.getItemNoteTruckDriver();

			// append item comments if not empty
			orderEntity.setNoteDelivery(com.crh.logon.utils.StringUtils
					.appendWithNewLine(orderEntity.getNoteDelivery(),
							itemNoteDelivery));
			orderEntity.setNoteDispatching(com.crh.logon.utils.StringUtils
					.appendWithNewLine(orderEntity.getNoteDispatching(),
							itemNoteDispatching));
			orderEntity.setNoteOrderMgmt(com.crh.logon.utils.StringUtils
					.appendWithNewLine(orderEntity.getNoteOrderMgmt(),
							itemNoteOrderMgmt));
			orderEntity.setNoteRailwayDisp(com.crh.logon.utils.StringUtils
					.appendWithNewLine(orderEntity.getNoteRailwayDisp(),
							itemNoteRailway));
			orderEntity.setNoteTruckDriver(com.crh.logon.utils.StringUtils
					.appendWithNewLine(orderEntity.getNoteTruckDriver(),
							itemNoteTruckDriver));

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
	 * Setting the order's contract references. Since every restriction should
	 * be based on a contract now, we just set the order's reference to that
	 * contract.
	 * 
	 * @param editor
	 * @param itemDto
	 */
	private void setSourceDependentAttributes(OrderTakingEditorDto editor,
			OrderTakingEditorOrderItemDto itemDto) {
		// [[
		Object[][] params = { { "editor", editor }, { "itemDto", itemDto } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSourceDependentAttributes", params);
		}
		// ]]
		try {
			final DeliverySetting setting = editor.getSalesAreaMap()
					.getDeliverySettingByCompositeIdentifier(
							itemDto.getCompositeIdentifier());
			itemDto.getOrderItem().setSapContractItem(
					setting.getSapContractItem());
			itemDto.getOrderItem().setSapMaterial(setting.getSapMaterial());
			if (setting.getSapContractItem() != null) {
				editor.getOrder().setSapContract(
						setting.getSapContractItem().getSapContract());
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

	private void updateBestellungFormOrderLists(HttpServletRequest request,
			String type) {
		// [[
		Object[][] params = { { "request", request }, { "type", type } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateBestellungFormOrderLists", params);
		}
		// ]]
		try {
			// update bestellung form
			BestellungForm bestellungForm = BestellungForm
					.getSessionInstance(request);
			ShiptoEntity shipTo = bestellungForm.getShipTo();

			if (bestellungForm != null) {
				final Locale locale = bestellungForm.getLocale();

				if (RELOAD_ALL.equals(type) || RELOAD_OPEN_ORDERS.equals(type)) {
					List<DateTimeAwareOrderWrapper> orders = fetchOngoingOrders(
							shipTo, locale);
					bestellungForm.setOrderList(orders);
				}

				if (RELOAD_ALL.equals(type) || RELOAD_ALL_OPEN.equals(type)
						|| RELOAD_OPEN_CUSTORDERS.equals(type)
						|| RELOAD_ALL_CUSTORDERS.equals(type)) {
					List<DateTimeAwareCustomerOrderWrapper> custorders = fetchOpenCustorders(
							shipTo, locale);
					bestellungForm.setOpenCustorderList(custorders);
				}

				if (RELOAD_ALL.equals(type)
						|| RELOAD_CLOSED_CUSTORDERS.equals(type)
						|| RELOAD_ALL_CUSTORDERS.equals(type)) {
					List<DateTimeAwareCustomerOrderWrapper> custorders = fetchClosedCustorders(
							shipTo, locale);
					bestellungForm.setClosedCustorderList(custorders);
				}

				if (RELOAD_ALL.equals(type)
						|| RELOAD_CLOSED_ORDERS.equals(type)) {
					List<DateTimeAwareOrderWrapper> orders = fetchCompletedOrders(
							shipTo, locale);
					bestellungForm.setCompletedOrderList(orders);
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

	private void updateOrdersForCustorder(BestellungForm bestellungForm,
			Long custorderId) {
		// [[
		Object[][] params = { { "bestellungForm", bestellungForm },
				{ "custorderId", custorderId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateOrdersForCustorder", params);
		}
		// ]]
		try {
			ShiptoEntity shipTo = bestellungForm.getShipTo();
			BestellungService bestellungService = getBestellungService();
			final Locale locale = bestellungForm.getLocale();

			// --- Open orders ---
			List<DateTimeAwareOrderWrapper> openOrders = bestellungService
					.fetchDateTimeAwareOrdersByCustorderAndStatus(false,
							shipTo, custorderId, false, locale,
							getUserTimezone());
			bestellungForm.setOpenOrdersForCustorder(openOrders);

			// --- Closed orders ---
			List<DateTimeAwareOrderWrapper> closedOrders = bestellungService
					.fetchDateTimeAwareOrdersByCustorderAndStatus(false,
							shipTo, custorderId, true, locale,
							getUserTimezone());
			bestellungForm.setClosedOrdersForCustorder(closedOrders);
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
	 * Common initial validation and processing of the data given in the first
	 * New Order panel, that can be done right before saving a single delivery
	 * order or at the next step of creating a customer order (and the
	 * associated single delivery orders).
	 * 
	 * NOTE (romek Jul 20, 2011): Originally there was no clean separation of
	 * validation and processing.
	 * 
	 * @param helpForm
	 * @param request
	 * @param editor
	 * @throws Exception
	 */
	private void validateAndProcessNewOrderData(BestellungForm helpForm,
			HttpServletRequest request, OrderTakingEditorDto editor,
			String index) throws Exception {
		// [[
		Object[][] params = { { "helpForm", helpForm }, { "request", request },
				{ "editor", editor } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateAndProcessNewOrderData", params);
		}
		// ]]
		try {
			OrderEntity orderCommon = helpForm.getOrderCommon();

			// --- Common fields: SMS and e-mail validation ---
			if (!LogonUtil.validSmsNumber(orderCommon.getOrderConfSmsNumber())) {
				MessageUtils.addActionWarning(request,
						"dispokarte.errors.invalidSMSnumber");
			}
			if (!LogonUtil.validFaxNumber(orderCommon.getOrderConfFaxNumber())) {
				MessageUtils.addActionWarning(request,
						"dispokarte.errors.invalidFaxNumber");
			}
			if (!LogonUtil.validEmail(orderCommon.getOrderConfEmailAdresses())) {
				MessageUtils.addActionWarning(request,
						"dispokarte.errors.invalidEmailAddress");
			}
			// Validation of Ship To & Sales area related data
			Map<Object, List<ShiptoSalesAreaError>> invalidMap = helpForm
					.getInvalidMap();
			ShiptoSareaEntity shiptoSarea = editor.getShiptoSarea();
			
			RefDataGenericsService refData = getServiceLocator().getRefDataGenericsService();
			
			DeliveryTypeBsEntity deliveryTypeBsEntity = refData.findBsEntity(DeliveryTypeBsEntity.class, editor.getDelTypeCd()); 
			boolean applyBpBlockCheck = deliveryTypeBsEntity.getApplyBpBlockCheck();
			
			if (invalidMap.containsKey(shiptoSarea) && applyBpBlockCheck) {
				MessageUtils.addActionWarning(request,
						"bestellung.error.sarea.invalid");
			}

			// Custorder Reference Number
			String custorderRefNo = editor.getCustorderCustrefNo();
			if (!StringUtils.isEmpty(custorderRefNo)) {
				if (custorderRefNo.length() > CustorderEntity.DBSIZE_CUSTORDER_CUSTREF_NO) {
					MessageUtils
							.addActionWarning(request,
									"bestellung.error.invalid.customer.reference.number");
				}
			}

			processOrderEntityOfEditor(request, helpForm, editor, index);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void saveShiptoData(BestellungForm helpForm) {
		// [[
		Object[][] params = { { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveShiptoData", params);
		}
		// ]]
		try {
			// --- Saving the contact person list and daily order status ---
			ShiptoEntity shipto = helpForm.getShipTo();
			RefDataGenericsService refData = getRefDataGenericsService();

			if (shipto.getDailyOrderContactFlag() != null
					&& shipto.getDailyOrderContactFlag()) {
				DailyOrderStatusBsEntity dailyOrderStatusBsEntity = refData
						.findBsEntity(DailyOrderStatusBsEntity.class,
								Constant.DAILY_ORDER_STATUS_ALREADY_ORDERED);
				shipto.setDailyOrderStatus(dailyOrderStatusBsEntity);
				shipto.setDailyOrderLastContactDat(new Date());
			}

			// --- Saving print delivnote and coll delivnote checkbox values to
			// shipto ---
			OrderEntity newOrder = helpForm.getOrderCommon();
			OrderItemEntity firstOrderItem = newOrder.getOrderItems().get(0);
			SapMaterialEntity sapMaterial = firstOrderItem.getSapMaterial();
			if (sapMaterial.getMaterialIsCem()) {
				shipto.setPaperSingleDelnoteFlagCem(newOrder
						.getDelivnoteSinglePaperFlag());
				// TODO (pascal Nov 1, 2012): LOGON-5630: Remove?
				// shipto.setPaperCollDelnoteFlagCem(newOrder.getDelivnoteCollPaperFlag());
				CustorderEntity custorder = newOrder.getCustorder();
				if (custorder != null) {
					shipto.setPaperCollDelnoteFlagCem(custorder
							.getDelivnoteCollPaperFlag());
				}
			}
			else if (sapMaterial.getMaterialIsAgg()) {
				shipto.setPaperSingleDelnoteFlagAgg(newOrder
						.getDelivnoteSinglePaperFlag());
				// TODO (pascal Nov 1, 2012): LOGON-5630: Remove?
				// shipto.setPaperCollDelnoteFlagAgg(newOrder.getDelivnoteCollPaperFlag());
				CustorderEntity custorder = newOrder.getCustorder();
				if (custorder != null) {
					shipto.setPaperCollDelnoteFlagAgg(custorder
							.getDelivnoteCollPaperFlag() != null ? custorder
							.getDelivnoteCollPaperFlag() : Boolean.FALSE);
				}
			}

			shipto = getDispokarteService().mergeShipto(shipto, null);
			helpForm.setShipTo(shipto, createTimeZoneFor(shipto));
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

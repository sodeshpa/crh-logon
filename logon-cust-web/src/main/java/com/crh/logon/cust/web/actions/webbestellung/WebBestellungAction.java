/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.actions.webbestellung;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.localinterfaces.CustorderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RailSchedulingServiceLocalInterface;
import com.crh.logon.business.services.utils.CustorderSplitWrapper;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.MaterialColumn;
import com.crh.logon.business.services.utils.MaterialType;
import com.crh.logon.business.services.utils.OrderTakingHelperUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.SplittedOrderData;
import com.crh.logon.business.services.utils.SplittedOrderItemData;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.cust.web.forms.webbestellung.WebBestellungForm;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.refdata.DailyOrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.actions.OrderTakingActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="cust_WebBestellungForm"
 *  path="/webbestellung/WebBestellung" scope="session"
 *  validate="true" input="WebBestellungNewOrdersTile.def"
 *
 * @struts.action-forward name="saveSuccess" path="/webbestellung/WebBestellungEntry.do"
 * @struts.action-forward name="saveFailed" path="WebBestellungTile.def"
 * @struts.action-forward name="listNewOrders" path="WebBestellungNewOrdersTile.def"
 * @struts.action-forward name="listOngoingOrders" path="WebBestellungOngoingOrdersTile.def"
 * @struts.action-forward name="listCompletedOrders" path="WebBestellungCompletedOrdersTile.def"
 * @struts.action-forward name="callback" path="CallbackTile.def"
 * @struts.action-forward name="showWebBestellungAdjustDeliveries" path="WebBestellungAdjustDeliveriesTile.def"
 */
@AdnTrace
public class WebBestellungAction extends OrderTakingActionSupport {

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(WebBestellungAction.class);

	private static final DeliverySettingDTO DELIVERY_SETTING_NOT_IN_LIST = null;

	public static final String FORWARD_SAVE_SUCCESS = "saveSuccess";
	public static final String FORWARD_CALLBACK = "callback";
	public static final String FORWARD_ADJUST_DELIVERIES = "showWebBestellungAdjustDeliveries";

	public static final String ACTION_SAVE = "save";
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_ADD_BIMODAL_RAIL_PART = "addBimodalRailPart";
	public static final String ACTION_RELOAD = "reload";
	public static final String ACTION_PREPARE_ADJUST_DELIVERIES_1 = "prepareAdjustDeliveries1";
	public static final String ACTION_PREPARE_ADJUST_DELIVERIES_2 = "prepareAdjustDeliveries2";

	public static final String SAVE_PRESSED_ON_ADJUST_DELIVERIES_POP_UP = "savedOnAdjustDeliveries";

	private RailSchedulingServiceLocalInterface railSchedulingService;
	
	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		Authorization.authorize(Authorization.WEB_ORDER);
		ServiceLocator serviceLocator = getServiceLocator();
		railSchedulingService = serviceLocator.getLocalRailSchedulingService();
		// Get the struts form(s).
		WebBestellungForm helpForm = (WebBestellungForm) form;

		TimeZone userTimeZone = UserProvider.getTimezone();

		Locale locale = request.getLocale();

		String forwardName = FORWARD_SAVE_SUCCESS;

		String action = request.getParameter("formAction");

		// this variable is filled when save was pressed on the 'Adjust Deliveries' popup
		String saveLocation = request.getParameter("saveLocation");

		// if true customer opoder item handling is different
		Boolean isAdjustDeliveryPopup = ACTION_PREPARE_ADJUST_DELIVERIES_1.equals(action)||
				ACTION_PREPARE_ADJUST_DELIVERIES_2.equals(action)||SAVE_PRESSED_ON_ADJUST_DELIVERIES_POP_UP.equals(saveLocation);

		if (ACTION_SAVE.equals(action) || isAdjustDeliveryPopup) {
			request.setAttribute("action", action);
			forwardName = FORWARD_CALLBACK;

			CustorderEntity customerOrder = helpForm.getCustorder();
			final ShiptoEntity shipTo = helpForm.getShipTo();
			customerOrder.setShiptoSapBp(shipTo.getShiptoSapBp());
			final SapBpEntity soldTo = helpForm.getSoldTo();
			customerOrder.setSoldtoSapBp(soldTo);
			final SareaEntity salesArea = helpForm.getSarea();
			customerOrder.setSarea(salesArea);
			ShiptoSareaEntity shipToSalesArea = helpForm.getShiptoSarea();

			if (helpForm.getInvalidMap().containsKey(shipToSalesArea)) {
				MessageUtils.addActionWarning(request,
						"webbestellung.error.shiptosarea.or.soldto.not.initialized",
						shipToSalesArea.getShipto().getShiptoSapBp().getName1() + " ("
								+ shipToSalesArea.getShipto().getShiptoSapBp().getSapId() + ")",
						shipToSalesArea.getSarea().getSareaDisplayLongname(), soldTo.getName1()
								+ " (" + soldTo.getSapId() + ")");
			}
			final DeliverySettingDTO selectedDeliverySetting = helpForm.getSelectedDeliverySetting(
					request, locale);
			Boolean onHoldOrderFlag = customerOrder.getOnHoldOrderAndLockedFlag();
			setupAndValidateOrder(request, helpForm, locale, customerOrder, shipToSalesArea,
					selectedDeliverySetting, isAdjustDeliveryPopup);
			customerOrder.setOnHoldOrderAndLockedFlag(onHoldOrderFlag);
			if (noErrorWarningPresent(request)) {
				try {
					// Saving the order
					String serviceProcessIndicatorId = selectedDeliverySetting.getServiceProcessIndicatorId();
					if (LogonUtil.isBimodalTruckPartOrder(customerOrder.getDeliveryType().getDeliveryTypeCd())) {
						if (helpForm.getBimodalRailCustorder() == null) {
							MessageUtils.addActionWarning(request,
									"webbestellung.bimodalrailorder.missing");
						}
						else {

							final Date deliveryFrom = helpForm.getBimodalRailCustorder().getDeliveryFromDat();

							final Date deliveryTo = helpForm.getDeliveryTo();

							preSetDeliveryDates(request, helpForm.getBimodalRailCustorder(),
									deliveryFrom, deliveryTo);

							try {
								ConfigBean configBean = getConfigBean();
								railSchedulingService.applyLeadTimeCheck(shipTo,
										helpForm.getBimodalRailCustorder(), configBean,
										userTimeZone);
							}
							catch (ValidationException e) {
								processErrors(request, e, CallerContext.WEB_ORDER, false);
							}

						}
						if (noErrorWarningPresent(request)) {
							try {
								serviceLocator.getCustorderService().createBimodalCustorder(
										customerOrder, helpForm.getBimodalRailCustorder(),
										selectedDeliverySetting.getAutoPoolAssignmentFlag(),
										selectedDeliverySetting.getAutoCarrierAssignmentFlag(),
										false,
										serviceProcessIndicatorId);
							}
							catch (ValidationException validationException) {
								processErrors(request, validationException,
										CallerContext.WEB_ORDER, false);
							}
							catch (LogonException logonException) {
								Reason reason = logonException.getReason();
								MessageUtils.addActionError(request, reason.getReason());
							}
							catch (Exception exception) {
								Throwable cause = exception.getCause();
								if (cause instanceof LogonException) {
									Reason reason = ((LogonException) cause).getReason();
									MessageUtils.addActionError(request, reason.getReason());
								}
								else {
									MessageUtils.addActionError(request, exception);
								}
							}
						}
					}
					else {
						try {
							boolean copyCustomerRemarkToDriver = helpForm.getCopyCustomerRemarkToDriver();
							String customerRemarkLabel = null;
							if (copyCustomerRemarkToDriver) {
								customerRemarkLabel = MessageUtils.getMessage(request,
										"orderdetail.main.label.customercomment");
							}
							CustorderServiceLocalInterface custorderService = serviceLocator.getLocalCustorderService();

							if (ACTION_PREPARE_ADJUST_DELIVERIES_1.equals(action)) {

								String triggeredAutomatically = request.getParameter("triggeredAutomatically");
								boolean isTriggeredAutomatically = "true".equals(triggeredAutomatically);


									TruckWagonTypeWrapper maxPrio = TruckWagonTypeWrapperUtil
										.findProperTruck(selectedDeliverySetting.getSelectedVehicleTypeWrapperList());

								customerOrder.setOrders(null);
								// Pass isTriggeredAutomatically which controlls the lead time validation run.
								// If the adjust deliveries popup came up automatically (isTriggeredAutomatically=true)
								// than skip lead time validation.
								CustorderDTO custorderDTO = custorderService
										.processCustomerOrder(customerOrder, maxPrio, isTriggeredAutomatically);
								// custorderDTO will be needed in 'prepareAdjustDeliveries2' section
								// custorderDTO has CustorderSplitWrapper object!
								helpForm.setCustorderDTO(custorderDTO);


								// if the adjust deliveries popup triggered automatically than open popup only if
								// the number of deliveries is higher than 1
								if (isTriggeredAutomatically){
									int numberOfDeliveries = 0;
									if (custorderDTO != null && custorderDTO.getSplitWrapper() != null) {
										numberOfDeliveries = custorderDTO.getSplitWrapper().get_numOfDeliveries();
									}
									if (numberOfDeliveries > 1) {
										request.setAttribute("prepareAdjustDeliveries1Success", true);
									}
								}
								else {
									request.setAttribute("prepareAdjustDeliveries1Success", true);
								}

								return mapping.findForward(FORWARD_CALLBACK);
							}
							else if (ACTION_PREPARE_ADJUST_DELIVERIES_2.equals(action)) {
								CustorderDTO custorderDTO = helpForm.getCustorderDTO();
								CustorderEntity custorder = custorderDTO.getCustorderEntity();
								CustorderSplitWrapper wrapper = custorderDTO.getSplitWrapper();

								TimeZone shipToTimeZone = createTimeZoneForShipTo(shipTo);
								customerOrder = custorderDTO.getCustorderEntity();
								List<SplittedOrderData> splittedOrderDataList =
										CustorderUtils.getSplittedOrderData(shipTo, customerOrder, shipToTimeZone);

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

									String serviceProcessIndicatorIdOfOrder;
									if (order.getSpi() != null) {
										serviceProcessIndicatorIdOfOrder = order.getSpi()
												.getOrderSpiCd();
									}
									else {
										serviceProcessIndicatorIdOfOrder = Constant.INVALID_OBJECT_ID_STR;
									}

									splittedOrderData
											.setServiceProcessIndicatorId(serviceProcessIndicatorIdOfOrder);

								}

								// Collecting possible materials
								DispokarteService dispokarteService = getServiceLocator()
										.getDispokarteService();
								Set<MaterialColumn> materialColumns = CustorderUtils
										.generateMaterialColumns(locale, custorder,
												qtyPerMaterial, dispokarteService);

								helpForm.setMaterialColumns(materialColumns);

								helpForm.setSplittedOrderDatas(splittedOrderDataList);
								helpForm.setOnHoldOrderAndLockedFlag(customerOrder.getOnHoldOrderAndLockedFlag());
								MaterialType materialType = OrderTakingHelperUtil.getMaterialType(customerOrder);
								helpForm.setMaterialType(materialType);

								return mapping.findForward(FORWARD_ADJUST_DELIVERIES);

							}
							else {

								custorderService.createCustomerOrder(
										customerOrder,
										selectedDeliverySetting.getAutoPoolAssignmentFlag(),
										selectedDeliverySetting.getAutoCarrierAssignmentFlag(), false,
										null,
										Constant.WEIGH_SYSTEM_LP_LOGON, false,
										serviceProcessIndicatorId, copyCustomerRemarkToDriver,
										customerRemarkLabel, true);
							}
						}
						catch (ValidationException e) {
							processErrors(request, e, CallerContext.WEB_ORDER, false);
						}
						catch (Exception e) {
							if (e instanceof LogonException) {
								MessageUtils.addActionError(request,
										((LogonException) e).getReason().getReason());
							}
							else if (e.getCause() instanceof LogonException) {
								MessageUtils.addActionError(request,
										((LogonException) e.getCause()).getReason().getReason());
							}
							else {
								MessageUtils.addActionError(request, e);
							}

							LOG.error("Could not create order.");

							if (e != null) {
								LOG.error(ExceptionUtil.exceptionString(e, null));
							}
						}
					}

					if (noErrorWarningPresent(request)) {
						// Saving the daily order status
						if (shipTo.getDailyOrderContactFlag() != null
								&& shipTo.getDailyOrderContactFlag()) {
							shipTo.setDailyOrderStatus(getRefDataGenericsService().findBsEntity(
									DailyOrderStatusBsEntity.class,
									Constant.DAILY_ORDER_STATUS_ALREADY_ORDERED));
							shipTo.setDailyOrderLastContactDat(new Date());
						}
						ShiptoEntity mergedShipTo = getDispokarteService().mergeShipto(shipTo, null);
						helpForm.setShipTo(mergedShipTo, helpForm.getShipToTimeZone());

						MessageUtils.addActionMessage(request, "bestellung.success.save");
						request.setAttribute("savesuccessful", "savesuccessful");
						return mapping.findForward(forwardName);
					}
				}
				catch (Exception exception) {
					LOG.error("Could not create order.");
					if (exception != null) {
						LOG.error(ExceptionUtil.exceptionString(exception, null));
					}
				}
			}
			CustorderEntity newCustomerOrder = createCustorder(soldTo, shipTo);
			customInitWebCustorder(shipTo, newCustomerOrder);

			newCustomerOrder.setOnHoldOrderAndLockedFlag(onHoldOrderFlag);
			helpForm.setCustorder(newCustomerOrder);

			helpForm.populateForm();
			helpForm.initEditorFields();
			return mapping.findForward(forwardName);
		}

		if (ACTION_DELETE.equals(action)) {
			OrderService service = getOrderService();
			Long orderId = (Long.valueOf(request.getParameter("orderId")));
			try {
				service.deleteWebOrder(orderId, null);
				MessageUtils.addActionMessage(request, "webbestellung.success.delete");
			}
			catch (RuntimeException re) {
				if (re.getCause() instanceof LogonException) {
					LogonException le = (LogonException) re.getCause();
					Reason reason = le.getReason();
					if (reason.getReason().equals(Reason.RES_ORDER_WRONG_STATUS.getReason())) {
						MessageUtils.addActionMessage(request, le.getMessage());
					}
					else {
						throw le;
					}
				}
			}

			forwardName = FORWARD_SAVE_SUCCESS;
		}
		else if (ACTION_ADD_BIMODAL_RAIL_PART.equals(action)) {
			forwardName = FORWARD_CALLBACK;
			request.setAttribute("action", "createBimodalRailpart");
			final String deliverySettingCompositeId = helpForm.getDeliverySettingCompositeId();
			final DeliverySettingDTO deliverySetting = helpForm.getDeliverySettingByCompositeIdentifier(deliverySettingCompositeId);
			
			if (deliverySetting == null
					|| deliverySetting.getShippingpoint() == null
					|| deliverySetting.getSapMaterial() == null
					|| !LogonUtil.isBimodalTruckPartOrder(deliverySetting.getDeliveryType().getDeliveryTypeCd())
					|| deliverySetting.getProductionPlant() == null) {
				MessageUtils.addActionWarning(request,
						"webbestellung.bimodalrailorder.invalid.defaults");

				LOG.warn("Bimodal deliverySetting invalid, composite id: "
						+ deliverySettingCompositeId);
				
				if (deliverySetting != null) {
					if (deliverySetting.getShippingpoint() == null) {
						LOG.warn("getShippingpoint is null");
					}
					if (deliverySetting.getSapMaterial() == null) {
						LOG.warn("getSapMaterial is null");
					}
					if (deliverySetting.getProductionPlant() == null) {
						LOG.warn("getProductionPlant is null");
					}
					else {
						LOG.warn("Invalid delivery type: "
								+ deliverySetting.getDeliveryType().getDeliveryTypeCd());
					}
				}
				else {
					LOG.warn("deliverySetting is null");
				}
				
				return mapping.findForward(FORWARD_CALLBACK);
			}

			List<ShiptoSareaRestrEntity> restrList = getBestellungService().fetchShiptoRestrListForBimodalRailPart(
					deliverySetting.getShippingpoint().getShippingpointId(),
					deliverySetting.getSapMaterialId(),
					deliverySetting.getProductionPlant().getPlantCd(),
					deliverySetting.getSarea().getSareaId());
			if (restrList == null || restrList.size() != 1) {
				MessageUtils.addActionWarning(request,
						"webbestellung.bimodalrailorder.more.restrictions");
				return mapping.findForward(FORWARD_CALLBACK);
			}

			ShiptoSareaRestrEntity biRailRestr = restrList.get(0);

			ShippingpointEntity biRailRestrSp = biRailRestr.getShippingpoint();
			if (biRailRestrSp == null) {
				MessageUtils.addActionWarning(request,
						"bestellung.error.rail.bimodal.restriction.empty.sp");
			}
			else {
				if (biRailRestrSp.getRailwayStation() == null) {
					MessageUtils.addActionWarning(request,
							"bestellung.error.rail.bimodal.restriction.no.station.for.sp");
				}
			}

			ShiptoSareaEntity biRailShiptoSarea = biRailRestr.getShiptoSarea();
			ShiptoEntity biRailShipto = biRailShiptoSarea.getShipto();
			final List<SapBpEntity> soldtoBpList = getBestellungService().fetchSoldtoByShiptoSarea(
					biRailShipto.getShiptoSapBp().getSapBpId(),
					deliverySetting.getSarea().getSareaId(), Boolean.TRUE);

			if (soldtoBpList == null) {
				MessageUtils.addActionWarning(request, "webbestellung.bimodalrailorder.more.soldto");
				return mapping.findForward(FORWARD_CALLBACK);
			}
			else if (soldtoBpList.size() != 1) {
				MessageUtils.addActionWarning(request,
						"webbestellung.bimodalrailorder.more.restrictions");
				return mapping.findForward(FORWARD_CALLBACK);
			}
			SapBpEntity soldTo = (SapBpEntity) soldtoBpList.get(0);

			DeliverySettingShiptoMap biRailShiptoMap = serviceLocator.getDeliverySettingService().initDeliverySettingShiptoMapForBimodalOrderCreation(
					soldTo, locale, biRailShiptoSarea,
					biRailRestr.getSapMaterial().getSapMaterialId());
			biRailShiptoMap.validateSalesAreas();

			final DeliverySettingSalesAreaMap salesAreaMapForSareaId = biRailShiptoMap.getSalesAreaMapForSareaId(biRailShiptoSarea.getSarea().getSareaId());
			
			// --- Validation error (e.g. truck part is not valid, missing railway station data, etc) ---
			if (MessageUtils.getActionWarnings(request) != null
					&& MessageUtils.getActionWarnings(request).size() > 0) {
				return mapping.findForward(FORWARD_CALLBACK);
			}
			else {
				CustorderEntity bimodalRailOrder = createCustorder(soldTo, biRailShipto);
				customInitWebCustorder(biRailShipto, bimodalRailOrder);

				bimodalRailOrder.setSoldtoSapBp(soldTo);
				bimodalRailOrder.setSarea(biRailShiptoSarea.getSarea());

				helpForm.setOrderedUomCd(Constant.UOM_WAGON);
				helpForm.setOrderQuantityStr("1");

				if (biRailRestr.getUom() != null && biRailRestr.getQuantity() != null) {
					helpForm.setOrderedUomCd(biRailRestr.getUom().getUomCd());
					helpForm.setOrderQuantityStr(biRailRestr.getQuantity().toPlainString());
		    	}
		    	
				DeliverySettingDTO biRailDeliverySetting = new DeliverySettingDTO(biRailRestr, userTimeZone);
				LOG.info("Bimodal weborder rail restriction composite id: "
						+ biRailDeliverySetting.getCompositeIdentifier());
		    	
				biRailDeliverySetting = salesAreaMapForSareaId.getDeliverySettingByCompositeIdentifier(biRailDeliverySetting.getCompositeIdentifier());
				if (biRailDeliverySetting == null) {
					MessageUtils.addActionWarning(request,
							"webbestellung.bimodalrailorder.invalid.defaults");
					LOG.warn("biRailDeliverySetting is null, deliverySettings composite id: "
							+ deliverySettingCompositeId);
					return mapping.findForward(FORWARD_CALLBACK);
				}
				setupAndValidateOrder(request, helpForm, locale, bimodalRailOrder,
						biRailShiptoSarea, biRailDeliverySetting, isAdjustDeliveryPopup);

				if (noErrorWarningPresent(request)) {
					helpForm.setBimodalRailCustorder(bimodalRailOrder);
				}
			}

		}

		if (ACTION_RELOAD.equals(action)) {
			MessageUtils.addActionMessage(request, "Loaded list");
			forwardName = "listCompletedOrders";
		}
		return mapping.findForward(forwardName);
	}


	/**
	 * Validates the order, and sets the warnings with the help of MessageUtils, if something is wrong.
	 * The validation must happen for all the items added to the order, thus those are added one-bby-one
	 * added separately to the order in case they are valid.
	 * @param request
	 * @param helpForm
	 * @param locale
	 * @param customerOrder
	 * @param shipToSalesArea should be helpForm.getShiptoSarea() for normal orders, biRailShiptoSarea for
	 *                     bi-rail orders. Do not take it from deliverySetting, just in case in the future
	 *                     there will be a need for supporting non-restriction-based web orders.
	 * @param deliverySetting this restriction must be not null, this is the restriction of the FIRST order item of the order, which will define most of the fields
	 * @param itemEntity
	 */
	private void setupAndValidateOrder(HttpServletRequest request, WebBestellungForm helpForm,
			Locale locale, CustorderEntity customerOrder, ShiptoSareaEntity shipToSalesArea,
			DeliverySettingDTO deliverySetting, boolean isAdjustDeliveryPopup) {

		DeliveryTypeBsEntity deliveryTypeOfDeliverySetting = deliverySetting.getDeliveryType();
		if (deliverySetting == null || deliveryTypeOfDeliverySetting == null) {
			MessageUtils.addActionWarning(request,"webbestellung.restrictionnotknown");
			return;
		}
				
		//important to set here: defines the status: if set, must remain in new
		
		String customerWebOrderNote = helpForm.getCustWeborderNote();
		customerOrder.setCustWeborderNote(customerWebOrderNote);
		String customerWebOrderReferenceNumber = helpForm.getCustWeborderRefNo();
		customerOrder.setCustorderCustrefNo(customerWebOrderReferenceNumber);
		
		//Create the items - the first item will define several attributes of the order
		String orderedUnitOfMeasureCode = helpForm.getOrderedUomCd();
		String orderQuantityString = helpForm.getOrderQuantityStr();
		
		CustorderUtils.setupCustorderAttributes(customerOrder, deliverySetting);
		
		CustorderItemEntity customerOrderItem;
		// if we come from adjustDeliveriesPopup request then this part is called multiple times,
		// although only 1 customerItem should be created, otherwise works as-is
		if(customerOrder.getCustorderItems().size() > 0 && isAdjustDeliveryPopup) {
			customerOrderItem = customerOrder.getCustorderItems().get(0);
		}
		else {
			customerOrderItem = CustorderUtils.addNewCustOrderItem(customerOrder,
					deliverySetting);
		}

		applyUISettings(request, helpForm, locale, customerOrderItem, orderedUnitOfMeasureCode, orderQuantityString, deliverySetting);
		
		DeliveryTypeBsEntity deliveryTypeOfCustomerOrder = customerOrder.getDeliveryType();
		Long deliveryTypeCodeOfCustomerOrder = deliveryTypeOfCustomerOrder.getDeliveryTypeCd();
		ShiptoEntity shipTo = helpForm.getShipTo();
		if (!(LogonUtil.isPickupOrder(deliveryTypeCodeOfCustomerOrder) 
				|| LogonUtil.isShipPickupOrder(deliveryTypeCodeOfCustomerOrder)
				|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCodeOfCustomerOrder)
				|| LogonUtil.isBimodalRailPartOrder(deliveryTypeCodeOfCustomerOrder))) {
			
			// ---
			// Transport Duration: Must be a valid number, and >= 0 
			// ---
			try {
				String durationTime = helpForm.getDurationTime();
				double duration = Double.valueOf(durationTime);
				if (duration > 0) {
					String durationCorrectionFactor = helpForm.getDurationCorrFactor();
					customerOrder.setTransportDurPlanGoogleMin(Math.round(duration / Double.valueOf(durationCorrectionFactor)));
				}
			}
			catch (NumberFormatException numberFormatException) {
				LOG.error("Invalid duration of route on webbestellung.");
			}
			
			// --- Distance in meters ---
			try {
				String distanceMeters = helpForm.getDistanceMeters();
				double distance = Double.valueOf(distanceMeters);
				if (distance > 0) {
					long distanceAsLong = Double.valueOf(distance).longValue();
					customerOrder.setTransportDistanceM(distanceAsLong);
				}
			}
			catch (NumberFormatException numberFormatException) {
				LOG.error("Invalid distace of route on webbestellung.");
			}
			
			Long transportDistanceInMeter = customerOrder.getTransportDistanceM();
			ShippingpointEntity shippingPointOfCustomerOrder = customerOrder.getShippingpoint();
			if (transportDistanceInMeter == null && shippingPointOfCustomerOrder != null) {
				//if still null
				OrderService orderService = getOrderService();
				SapBpEntity shiptoSapBusinessPartner = customerOrder.getShiptoSapBp();
				TruckRouteEntity route = orderService.findTruckRoute(
						shiptoSapBusinessPartner, shippingPointOfCustomerOrder,
						deliveryTypeOfCustomerOrder);
				if (route != null) {				
					Long routeDistanceCalculatedInMeter = route.getDistanceCalculatedM();
					transportDistanceInMeter = routeDistanceCalculatedInMeter;
					customerOrder.setTransportDistanceM(routeDistanceCalculatedInMeter);
				}
			}
			if (transportDistanceInMeter == null || transportDistanceInMeter < 0) {
				MessageUtils.addActionWarning(request,"webbestellung.error.invalid.distance");
			}
		}

		// --- ACTUAL DELIVERY DATE FOR RAIL AND BIMODAL RAIL ORDER ---
		Date deliveryFrom = null;
		Date deliveryTo = null;
		try {
			deliveryFrom = helpForm.getDeliveryFrom();
		}
		catch (ParseException parseException) {
			MessageUtils.addActionWarning(request, "webbestellung.error.invalid.delivery.from");
		}
		try {
			deliveryTo = helpForm.getDeliveryTo();
		}
		catch (ParseException parseException) {
			MessageUtils.addActionWarning(request, "webbestellung.error.invalid.delivery.from");
		}
		if (LogonUtil.isRailOrder(deliveryTypeCodeOfCustomerOrder)) {
			try {
				preSetDeliveryDates(request, customerOrder, helpForm.getDeliveryFrom(),
						helpForm.getDeliveryTo());
			}
			catch (Exception e) {
				MessageUtils.addActionWarning(request, "webbestellung.error.invalid.delivery.to");
			}
			try {
				ConfigBean configBean = getConfigBean();
				TimeZone userTimeZone = UserProvider.getTimezone();
				railSchedulingService.applyLeadTimeCheck(
						shipTo, 
						customerOrder, 
						configBean,
						userTimeZone);
			}		
			catch (ValidationException validationException) {
				if (validationException.getCause() instanceof ValidationException) {
					validationException = (ValidationException) validationException.getCause();
				}
				processErrors(request, validationException, CallerContext.WEB_ORDER, false);
			}
		}
		else {
			Calendar deliveryFromAsCalendar = DateTimeUtils.getDateAsCalendar(deliveryFrom);
			Date deliveryFromTime = deliveryFromAsCalendar.getTime();
			if (deliveryFromTime.before(getToday00h00mDate())) {
				MessageUtils.addActionWarning(request,"webbestellung.error.invalid.delivery.from");
			}
			customerOrder.setDeliveryFromDat(deliveryFromTime);
			Calendar deliveryToAsCalendar = DateTimeUtils.getDateAsCalendar(deliveryTo);
			Date deliveryToTime = deliveryToAsCalendar.getTime();
			customerOrder.setDeliveryToDat(deliveryToTime);
		}
		
		//if the first item has been added successfully, create the others, if they are at all others
		//this is only possible for BAG orders
		String maxItemNumber = request.getParameter("maxNumberOfAdditionalItems");
		long maxItem = 0;
		try {
			maxItem = Long.valueOf(maxItemNumber).longValue();
		}
		catch (NumberFormatException numberFormatException) {
			maxItem = 0;
		}
		for (int index = 0; index < maxItem; index++) {
			String compositeIdentifierAndIndex = "compositeIdentifier" + index;
			if (request.getParameter(compositeIdentifierAndIndex) != null) {
				if (StringUtils.isEmpty(request.getParameter(compositeIdentifierAndIndex))) {
					MessageUtils.addActionWarning(request,"error.field.required", MessageUtils.getMessage(request, locale, "fields.produkt.produkt"));
					continue;
				}
				String orderedUnitOfMeasureCodeAndIndex = "orderedUomCd" + index;
				if (StringUtils.isEmpty(request.getParameter(orderedUnitOfMeasureCodeAndIndex))) {
					MessageUtils.addActionWarning(request,"error.field.required", MessageUtils.getMessage(request, locale, "fields.uom.uom"));
					continue;
				}
				orderedUnitOfMeasureCode = request.getParameter(orderedUnitOfMeasureCodeAndIndex);
				String deliverySettingCompositeIdentifier = request.getParameter(compositeIdentifierAndIndex);
				orderQuantityString = request.getParameter("orderQuantityStr" + index);
				DeliverySettingSalesAreaMap salesAreaMap = helpForm.getSalesAreaMap();
				SapContractItemEntity sapContractItem = deliverySetting.getSapContractItem();
				SapContractEntity sapContract = sapContractItem.getSapContract();
				Long sapContractIdentifier = sapContract.getSapContractId();
				final List<DeliverySettingDTO> possibleDeliverySettingsForFirstDeliverySetting = 
						salesAreaMap.getPossibleDeliverySettingsForFirstDeliverySetting(sapContractIdentifier);
				DeliverySettingDTO addedDeliverySetting = findDeliverySettingByCompositeIdentifier(
						deliverySettingCompositeIdentifier,
						possibleDeliverySettingsForFirstDeliverySetting);
				if (addedDeliverySetting == null) {
	    			MessageUtils.addActionWarning(request,"webbestellung.error.material.not.available.in.salesarea", salesAreaMap.getShiptoSarea().getSarea().getSareaDisplayLongname());
	    			continue;
				}
				customerOrderItem = CustorderUtils.addNewCustOrderItem(customerOrder,
						addedDeliverySetting);
				applyUISettings(request, helpForm, locale, customerOrderItem, orderedUnitOfMeasureCode, orderQuantityString, addedDeliverySetting);
			}

		}
		// make sure no material is duplicated
		Set<Long> existingMaterials = new HashSet<>();
		boolean noDuplicate = customerOrder.getCustorderItems().stream().map(CustorderItemEntity::getSapMaterial)
				// Set.add() returns true for new elements, Stream.allMatch() returns true if the function returns true for all elements.
				.map(SapMaterialEntity::getSapMaterialId).allMatch(existingMaterials::add);
		if (!noDuplicate) {
			MessageUtils.addActionWarning(request,"bestellung.error.alreadyselected");
		}
	}


	public static DeliverySettingDTO findDeliverySettingByCompositeIdentifier(
			String deliverySettingCompositeIdentifier,
			final List<DeliverySettingDTO> possibleDeliverySettingsForFirstDeliverySetting) {
		for (DeliverySettingDTO currentDeliverySetting : possibleDeliverySettingsForFirstDeliverySetting) {
			String currentCompositeIdentifier = currentDeliverySetting.getCompositeIdentifier();
			if (currentCompositeIdentifier.equals(deliverySettingCompositeIdentifier)) {
				return currentDeliverySetting;
			}
		}
		return DELIVERY_SETTING_NOT_IN_LIST;
	}





	/**
	 * @param request
	 * @param custorderEntity
	 * @param deliveryFrom 
	 * @param deliveryTo
	 * @return
	 */
	private boolean preSetDeliveryDates(HttpServletRequest request,
			CustorderEntity custorderEntity, Date deliveryFrom, Date deliveryTo) {
		boolean validDates = true;
		
		Date morningOfDay = null;

		morningOfDay = DateTimeUtils.getMorningOfDay(deliveryFrom);
		custorderEntity.setDeliveryFromDat(morningOfDay);						

		Date midnightOfDay = null;

		midnightOfDay = DateTimeUtils.getMidnightOfDay(deliveryTo);
		custorderEntity.setDeliveryToDat(midnightOfDay);

		return validDates;
	}

	/**
	 * @param request
	 * @param helpForm
	 * @param locale
	 */
	private void applyUISettings(HttpServletRequest request, WebBestellungForm helpForm,
			Locale locale, CustorderItemEntity custorderItemEntity, final String orderedUomCd , final String orderQuantityStr, DeliverySettingDTO deliverySettingDTO) {
		
		
		CustorderEntity custOrderEntity =  custorderItemEntity.getCustorder();
		
		RefDataGenericsService refData = getRefDataGenericsService();
		
		
		if ( custorderItemEntity.getCustorderItemNo().equals(Constant.ORDER_FIRST_ITEM_CD) ) {
		    String pickupTypeSelect = helpForm.getPickupTypeSelect();
			
		    Long deliveryTypeCd = deliverySettingDTO.getDeliveryType().getDeliveryTypeCd();
		    
		    if (pickupTypeSelect.equals(WebBestellungForm.PICKUP_SELECTION_PICKUP) && !(LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd) || LogonUtil.isShipPickupOrder(deliveryTypeCd))) {
		    	deliveryTypeCd = Constant.DELIVERY_TYPE_PICKUP;
		    }
		    			
			custOrderEntity.setDeliveryType(refData.findBsEntity(DeliveryTypeBsEntity.class, deliveryTypeCd)); // nullable

			boolean isBimodalRailPartOrder = LogonUtil.isBimodalRailPartOrder(custOrderEntity.getDeliveryType().getDeliveryTypeCd());
			
			// --- BILLTO ---
			String billtoSapBpId = helpForm.getBilltoSapBpId();
			if ( !isBimodalRailPartOrder && billtoSapBpId != null && !billtoSapBpId.isEmpty() ) {
					//nullable, but that would mean it is free of charge not possible in weborder
				custOrderEntity.setBilltoSapBp(getServiceLocator().getDispokarteService().getBilltoSapBpById(Long.valueOf(billtoSapBpId)));
			}

			if (custOrderEntity.getBilltoSapBp() == null) {
				MessageUtils.addActionWarning(request,"error.field.required", MessageUtils.getMessage(request, locale, "fields.billto.billto"));
			}

			
			// --- PAYER ---
			String payerSapBpId = helpForm.getPayerSapBpId();
			if ( !isBimodalRailPartOrder && payerSapBpId != null && !payerSapBpId.isEmpty() ) {
				custOrderEntity.setPayerSapBp(getServiceLocator().getDispokarteService().getPayerSapBpById(Long.valueOf(payerSapBpId)));
			}			
			
			if (custOrderEntity.getPayerSapBp() == null && 
					!OrderUtils.isPayerIgnored(custOrderEntity.getSapContract())) {
				String titleKey;
				
				if (LogonUtil.isBimodalRailPartOrder(custOrderEntity.getDeliveryType().getDeliveryTypeCd())) {
					
					titleKey = "webbestellung.bimodalrailorder.invalid.defaults";
					LOG.warn("Web order bimodal: Payer is not available for custorder: " + custOrderEntity.getCustorderId());
				}
				else {
					titleKey = "webbestellung.error.payer.invalid.default";
				}
				
				MessageUtils.addActionWarning(request, titleKey);
			}
			
	        // --- FREE DELIVERY by default it is never free ---
	        if ( custOrderEntity.getFreeDeliveryFlag() && !(custOrderEntity.getBilltoSapBp()!= null && custOrderEntity.getSoldtoSapBp() != null && custOrderEntity.getBilltoSapBp().getSapBpId().equals(custOrderEntity.getSoldtoSapBp().getSapBpId()))) {
	        		MessageUtils.addActionWarning(request,"webbestellung.error.freedelivery.impossible");
	        }
			
			//Shippingpoint is defined by the first order item
			if (custOrderEntity.getShippingpoint() == null) {
				MessageUtils.addActionWarning(request,"webbestellung.error.required.shippingpoint");
			}
			
		}
		else{
	        if ( !deliverySettingDTO.getFreeDeliveryFlag().equals(custOrderEntity.getFreeDeliveryFlag() ) ){
	        		MessageUtils.addActionWarning(request,"webbestellung.error.not.possible.mix.free.delivery", deliverySettingDTO.getSapMaterial().getSapMaterialNo());
	        }
			
			//in case the loadingpoint is not set, or is corresponding to the first item's loadingpoint, pre-set it! otherwise leave it empty
			LoadingpointEntity firstItemLoadingpoint = ((CustorderItemEntity)custOrderEntity.getCustorderItems().get(0)).getLoadingpoint();
			if ( firstItemLoadingpoint != null && ( deliverySettingDTO.getLoadingpoint() == null || ( deliverySettingDTO.getLoadingpoint().getLoadingpointId().equals(firstItemLoadingpoint.getLoadingpointId()) ))) {
				custorderItemEntity.setLoadingpoint(deliverySettingDTO.getLoadingpoint());
			}
		}

		
		BigDecimal orderQuantity = new BigDecimal("0");
		try{
			orderQuantity = new BigDecimal(orderQuantityStr);
			if (orderQuantity == null || orderQuantity.compareTo(BigDecimal.ZERO) == 0) {
				throw new LogonException();
			}			
		}
		catch (Exception e) {
			MessageUtils.addActionWarning(request,"webbestellung.error.invalid.quantity");
		}
		
		

		if (!StringUtils.isEmpty(orderedUomCd)) {
			UomBsEntity uom = refData.findBsEntity(UomBsEntity.class, orderedUomCd);
			if (uom != null && uom.getCreateMultiOrderFlag() && orderQuantity != null && orderQuantity.compareTo(BigDecimal.ZERO) > 0) {
				if (!Double.valueOf(Math.ceil(orderQuantity.doubleValue())).equals(orderQuantity.doubleValue())) {
		        	MessageUtils.addActionWarning(request,"webbestellung.error.multiple.truck.quantity.fraction");
				}
			}
			if (uom != null && !uom.getAvailableInWebOrderFlag()) {
				MessageUtils.addActionWarning(request,"webbestellung.error.copy.notweborder");
			}
			else if (uom != null && uom.getCtlActCd().equals(Boolean.FALSE)) {
				MessageUtils.addActionWarning(request,"webbestellung.error.copy.inactiveuom");
			}
			else{				
				custorderItemEntity.setOrderedUom(uom);
			}
		}
		if ( custorderItemEntity.getOrderedUom() == null ) {
			MessageUtils.addActionWarning(request,"error.field.required", MessageUtils.getMessage(request, locale, "fields.uom.uom"));
		}
		
		custorderItemEntity.setOrderedQuantity(orderQuantity);
		
		// the fields DelivTypePlanActual, OrderPlanActual and DispocardAvailalbe will be copied to the order/orderItem entities later and to the final table
		// These fields are used for KPIs 7,14 and 15
		
		// If this restriction is available it means that the order was created via dispocard
		ShiptoSareaRestrEntity shiptoSareaRestr = deliverySettingDTO.getShiptoSareaRestr();
		if (shiptoSareaRestr != null) {

			if (shiptoSareaRestr.getDeliveryType() != null
					&& custOrderEntity.getDeliveryType().getDeliveryTypeCd().equals(
							shiptoSareaRestr.getDeliveryType().getDeliveryTypeCd())) {
				custOrderEntity.setDelivTypePlanActual(Boolean.TRUE);
			}
			else {
				custOrderEntity.setDelivTypePlanActual(Boolean.FALSE);
			}

			for (CustorderItemEntity item : custOrderEntity.getCustorderItems()) {
				item.setDispocardAvailalbe(Boolean.TRUE);
				
				// we leave the order plan actual as null if any of these are not available to indicate that either quantity or uom was not controlled by the dispocard
				if (shiptoSareaRestr.getUom() != null
						&& shiptoSareaRestr.getQuantity() != null
						&& item.getOrderedUom() != null) {
					int qtyDiff = item.getOrderedQuantity().compareTo(
							shiptoSareaRestr.getQuantity());

					boolean uomDiff = item.getOrderedUom().getUomCd().equals(
							shiptoSareaRestr.getUom().getUomCd());

					if (qtyDiff == 0 && uomDiff) {
						item.setOrderPlanActual(Boolean.TRUE);
					} else {
						item.setOrderPlanActual(Boolean.FALSE);
					}
				}
			}
		}
		else {
			// this case shouldn't really happen for web and pickup orders
			for (CustorderItemEntity item : custOrderEntity.getCustorderItems()) {
				item.setDispocardAvailalbe(Boolean.FALSE);
			}
		}
		
		return;
	}

	private Date getToday00h00mDate() {
		Authorization.authorize(Authorization.WEB_ORDER);
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		return today.getTime();
	}

}

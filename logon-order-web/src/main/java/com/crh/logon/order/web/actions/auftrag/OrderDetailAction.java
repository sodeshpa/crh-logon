/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.dto.OrderDeliveryMinMaxDateDTO;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.beans.pdf.PdfGeneratingResult;
import com.crh.logon.business.services.interfaces.CustorderService;
import com.crh.logon.business.services.interfaces.MaterialEnhancementsService;
import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.UomConverterService;
import com.crh.logon.business.services.utils.BindingsDTO;
import com.crh.logon.business.services.utils.BizeventAuditWrapper;
import com.crh.logon.business.services.utils.CustorderSplitDiffWrapper;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.constants.CustorderLifecycleReturnCode;
import com.crh.logon.constants.ITrackStatus;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.actions.bestellung.BestellungAction;
import com.crh.logon.order.web.forms.auftrag.CommercialTabBean;
import com.crh.logon.order.web.forms.auftrag.CustOrderTabBean;
import com.crh.logon.order.web.forms.auftrag.CustOrderTabOrderBean;
import com.crh.logon.order.web.forms.auftrag.CustOrderTabOrderItemBean;
import com.crh.logon.order.web.forms.auftrag.MapTabBean;
import com.crh.logon.order.web.forms.auftrag.MngTabBean;
import com.crh.logon.order.web.forms.auftrag.OrderDetailBean;
import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import com.crh.logon.order.web.forms.auftrag.OrderDetailUploadAttachmentBean;
import com.crh.logon.order.web.forms.auftrag.OrderItemBean;
import com.crh.logon.order.web.forms.auftrag.OrderItemFinalBean;
import com.crh.logon.order.web.forms.auftrag.PayerSapBpBean;
import com.crh.logon.order.web.forms.auftrag.RepTabBean;
import com.crh.logon.order.web.forms.auftrag.ReportingTabBean;
import com.crh.logon.order.web.forms.auftrag.ShiptoBilltoBean;
import com.crh.logon.order.web.utils.BizeventAuditWrapperComparator;
import com.crh.logon.order.web.utils.comparator.CarrierComparator;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.CustorderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderRepAttEntity;
import com.crh.logon.persistence.entities.common.OrderRepDmgcodeTypeEntity;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.frozen.OrderItemFinalEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.CustorderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeBsEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeTxEntity;
import com.crh.logon.persistence.entities.refdata.DelivNotePrintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.ItrackStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.LangTxEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderRepAttTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapShippingCondTxEntity;
import com.crh.logon.persistence.entities.refdata.SgroupBsEntity;
import com.crh.logon.persistence.entities.refdata.SofficeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemLpTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;
import com.crh.logon.persistence.entities.views.CustordItemSumQtyExtEntity;
import com.crh.logon.persistence.entities.views.OrderItemMarginEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.NumberUtils;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.lang.BooleanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_OrderDetailForm"
 *  path="/auftrag/OrderDetail" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="fetchfirst" path="/auftrag/OrderDetailEntry.do"
 * @struts.action-forward name="success" path="OrderDetailTile.def"
 * @struts.action-forward name="loadDeleteOrder" path="OrderDetailPopupClose.def"
 * @struts.action-forward name="reloadOrder" path="OrderDetailPopupReload.def"
 * @struts.action-forward name="saveOrder" path="OrderDetailSave.def"
 * @struts.action-forward name="loadShPoint" path="OrderDetailShPoint.def"
 * @struts.action-forward name="loadCommCarrier" path="OrderDetailCommCarrier.def"
 * @struts.action-forward name="loadExecCarrier" path="OrderDetailExecCarrier.def"
 * @struts.action-forward name="loadLdPoint" path="OrderDetailLdPoint.def"
 * @struts.action-forward name="loadDelNote" path="OrderDetailDelNote.def"
 * @struts.action-forward name="loadUomList" path="OrderDetailUomList.def"
 * @struts.action-forward name="loadCollDelNote" path="OrderDetailCollDelNote.def"
 * @struts.action-forward name="loadRepItem" path="OrderDetailRepItem.def"
 * @struts.action-forward name="loadBillto" path="OrderDetailBillto.def"
 * @struts.action-forward name="loadPayer" path="OrderDetailPayer.def"
 * @struts.action-forward name="loadMaterialTab" path="OrderDetailMaterialTab.def"
 * @struts.action-forward name="loadMapTab" path="OrderDetailMapTab.def"
 * @struts.action-forward name="loadReportingTab" path="OrderDetailReportingTab.def"
 * @struts.action-forward name="loadMngTab" path="OrderDetailMngTab.def"
 * @struts.action-forward name="loadOrderLogTab" path="OrderDetailOrderLogTab.def"
 * @struts.action-forward name="loadTruckLogTab" path="OrderDetailTruckLogTab.def"
 * @struts.action-forward name="loadRepMaintTab" path="OrderDetailRepMaintTab.def"
 * @struts.action-forward name="loadServicesExtraCostTab" path="ServicesExtraCostTab.def"
 * @struts.action-forward name="loadCustOrderTab" path="OrderDetailCustOrder.def"
 * @struts.action-forward name="loadRepTabDamageCodeTile" path="RepTabDamageCodeTile.def"
 * @struts.action-forward name="loadAttachmentDiv" path="OrderDetailAttachmentDivTile.def"
 * @struts.action-forward name="loadOrderAudit" path="OrderDetailAuditPopup.def" 
 * @struts.action-forward name="loadSplitRecalcProposalPopup" path="CustorderSplitProposalPopupTile.def"
 * 
 * @struts.action-forward name="returnReasonPopup" path="ReturnReasonPopup.def"
 * 
 * @struts.action-forward name="addExtraCost" path="OrderExtraCost.def"
 * @struts.action-forward name="removeExtraCost" path="OrderExtraCost.def"
 * 
 * @struts.action-forward name="goodsMovementPopup" path="GoodsMovementPopup.def"
 * 
 * @struts.action-forward name="showVendorPfPopup" path="VendorPFPopup.def"
 * @struts.action-forward name="loadThirdPartyFreightersTab" path="OrderDetailThirdPartyFreightersTab.def"
 */
public class OrderDetailAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderDetailAction.class);
	
	private static final String FORWARD_SUCCESS = "success";
	public static final String FORWARD_DELETE_ORDER = "loadDeleteOrder";
	public static final String FORWARD_SEND_ORDER_TO_SAP = "sendOrderToSap";
	public static final String FORWARD_CONFIRM_UNLOAD_LOCATION = "confirmUnloadLocation";
	public static final String FORWARD_SAVE_ORDER = "saveOrder";
	public static final String FORWARD_SAVE_CLOSE_ORDER = "saveCloseOrder";
	public static final String FORWARD_SAVE_CUST_ORDER = "saveCustorder";
	public static final String FORWARD_SAVE_CUST_ORDER_CONFIRMED = "saveCustorderConfirmed";
	public static final String FORWARD_RELOAD_ORDER = "reloadOrder";
	public static final String FORWARD_SHIPPING_POINT = "loadShPoint";
	public static final String FORWARD_COMM_CARRIER = "loadCommCarrier";
	public static final String FORWARD_EXEC_CARRIER = "loadExecCarrier";
	public static final String FORWARD_LOADING_POINT = "loadLdPoint";
	public static final String FORWARD_DELIVERY_NOTE = "loadDelNote";
	public static final String FORWARD_REPLACE_MATERIAL = "loadReplacementMaterial";
	public static final String FORWARD_LOAD_UOMLIST = "loadUomList";
	public static final String FORWARD_COLL_DEL_NOTE = "loadCollDelNote";
	public static final String FORWARD_GENERATE_PDF = "generatePdf";
	public static final String FORWARD_GENERATE_COLL_PDF = "generateCollPdf";
	public static final String FORWARD_REPORTING_ITEM = "loadRepItem";
	public static final String FORWARD_BILLTO = "loadBillto";
	public static final String FORWARD_PAYER = "loadPayer";
	public static final String FORWARD_REPORT_WAGON_AS_EMPTY = "emptyWagon";
	public static final String FORWARD_VENDOR_PF_POPUP = "showVendorPfPopup";
	public static final String FORWARD_SEND_EMAIL = "sendEmail";
	public static final String FORWARD_RETURN_REASON_POPUP = "returnReasonPopup";
	public static final String FORWARD_RETURN_REASON_POPUP_OK = "returnReasonPopupOk";
	public static final String FORWARD_RETURN_REASON_POPUP_CANCEL = "returnReasonPopupCancel";
	public static final String FORM_ACTION_HOLD_BACK_RETURN_ORDER = "holdBackRepairOrder";
	public static final String FORM_ACTION_UPLOAD_REP_ORDER_ATT_FILE = "uploadRepOrderAtt";
	public static final String FORM_ACTION_REMOVE_REP_ORDER_ATT_FILE = "removeRepOrderAtt";
	public static final String FORM_ACTION_ADD_DAMAGECODE = "addDamageCode";
	public static final String FORM_ACTION_REMOVE_DAMAGECODE = "removeDamageCode";
	public static final String FORM_ACTION_REP_TAB_RELOAD_ATTACHMENT_DIV = "reloadAttachmentDiv";
	public static final String FORM_ACTION_LOAD_ORDER_AUDIT = "loadOrderAudit";
	public static final String FORM_ACTION_INITIATE_CLOSE_CUSTOMER_ORDER = "initiateSetCustorderToFinished";
	public static final String FORM_ACTION_CONFIRM_CLOSE_CUSTOMER_ORDER = "confirmSetCustorderToFinished";
	
	//Goods movement info popup
	public static final String FORWARD_GOODS_MOVEMENT_POPUP = "goodsMovementPopup";
	
	//Service material and Extra cost tab targets
	public static final String FORM_ACTION_ADD_SERVICE_MATERIAL = "addServiceMaterial";
	public static final String FORM_ACTION_ADD_EXTRA_COST = "addExtraCost";
	public static final String FORM_ACTION_REMOVE_EXTRA_COST = "removeExtraCost";
	
	//tabs
	public static final String LOAD_TAB = "loadTab";
	public static final String FORWARD_MATERIAL_TAB = "loadMaterialTab";
	public static final String FORWARD_MAP_TAB = "loadMapTab";
	public static final String FORWARD_REPORTING_TAB = "loadReportingTab";
	public static final String FORWARD_MNG_TAB = "loadMngTab";
	public static final String FORWARD_COMMERCIAL_TAB = "loadCommercialTab";
	public static final String FORWARD_ORDER_LOG_TAB = "loadOrderLogTab";
	public static final String FORWARD_TRUCK_LOG_TAB = "loadTruckLogTab";
	public static final String FORWARD_REP_MAINT_TAB = "loadRepMaintTab";
	public static final String FORWARD_ORDER_CUSTORDER_TAB = "loadCustOrderTab";
	public static final String FORWARD_REP_TAB_DAMAGECODE_TILE = "loadRepTabDamageCodeTile";
	public static final String FORWARD_REP_TAB_ATTACHMENT_DIV = "loadAttachmentDiv";
	public static final String FORWARD_SERVICES_EXTRA_COST_TAB = "loadServicesExtraCostTab";
	public static final String FORWARD_THIRD_PARTY_FREIGHTERS_TAB = "loadThirdPartyFreightersTab";

	public static final String ATTRIBUTE_PARTNER_FUNCTION_CODES = "partnerFunctionCodes";
	
	//Order management tab/Free of Charger delivery flag setting
	private static final boolean ENABLE_FREE_OF_CHARGE_DELIVERY = false;
	
	private static final List<Long> orderStatusAllowNoPdaOrRailEventFlagEdit = Arrays.asList(
			Constant.ORDER_STATUS_NEW,
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
			Constant.ORDER_STATUS_DISPATCHED,
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER //only if planning truck
		);
	
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
			Locale locale = getLocale(request);
			
			OrderDetailForm helpForm = (OrderDetailForm) form;
			String forwardName = FORWARD_SUCCESS;
			
			
			if (FORWARD_DELETE_ORDER.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_W);
				if (logDebugEnabled) {
					LOG.debug("Action: delete order!");
				}					
				OrderEntity order = helpForm.getOrder();
			
				Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
				if (orderStatusCd == Constant.ORDER_STATUS_FINISHED
						|| orderStatusCd.equals(Constant.ORDER_STATUS_DELIVERED)) {
					//only special delete is possible
					Authorization.authorize(Authorization.ORDER_DETAIL_DELETE_SPECIAL);
				}
				
				String reason = request.getParameter("reason");
				order = getOrderService().deleteOrder(order.getOrderId(), reason);
				
				AssignmentEntity ass = getOrderDetailService().loadAssignment(order.getOrderId());
				helpForm.setAssignment(ass);
				
				if (Constant.ORDER_STATUS_DELETED == order.getOrderStatus().getOrderStatusCd()) {
		            // reload bestellung page
		            request.setAttribute(BestellungAction.RELOAD, BestellungAction.RELOAD_ALL);
					
					MessageUtils.addActionMessage(request, "orderdetail.message.success.delele");
					return result = mapping.findForward(FORWARD_DELETE_ORDER);
				}
			}
			else if (FORWARD_RELOAD_ORDER.equals(helpForm.getFormAction())) {
				if (logDebugEnabled) {
					LOG.debug("Action: relaod order! order id to reload: " + helpForm.getReloadOrderId());
				}
				forwardName = FORWARD_RELOAD_ORDER;
			}
			else if (FORWARD_SEND_ORDER_TO_SAP.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DELIVERED_SEND_TO_SAP);
				if (logDebugEnabled) {
					LOG.debug("Action: send Deliviered order to SAP to Finish! order number : " + helpForm.getOrder().getOrderNumber());
				}
				forwardName = enableSendToSapFlag(helpForm, request);
				return result = mapping.findForward(forwardName);
			}
			else if (FORWARD_CONFIRM_UNLOAD_LOCATION.equals(helpForm.getFormAction())) {
				if (logDebugEnabled) {
					LOG.debug("Action: confirm unloading location! order number : " + helpForm.getOrder().getOrderNumber());
				}
				forwardName = wrongLocationApproved(helpForm, request);
				return result = mapping.findForward(forwardName);
			}
			else if (FORM_ACTION_INITIATE_CLOSE_CUSTOMER_ORDER.equals(helpForm.getFormAction())) {
				
				CustOrderTabBean custOrderTabBean = helpForm.getCustOrderTabBean();
				CustorderEntity custorderEntity = custOrderTabBean.getCustorderEntity();
				
				Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
				List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>();
				
				custorderEntity = getServiceLocator().getCustorderService().findCustorderById(custorderEntity.getCustorderId());
				OrderUtils.fillOrderNumberLists(custorderEntity, deleteOrderMap, manualDeallocateOrderNumberList);
				
				request.setAttribute("sourceAction", "OrderDetail");
				request.setAttribute("custorderId", custorderEntity.getCustorderId());
				request.setAttribute("deleteOrderMap", deleteOrderMap);
				request.setAttribute("manualDeallocateOrderNumberList", manualDeallocateOrderNumberList);

				request.setAttribute("showCloseCustorderPopup", true);
				
			    // reload bestellung page
			    helpForm.setReloadOrderId(helpForm.getOrder().getOrderId().toString());
			    return result = mapping.findForward(FORWARD_ORDER_CUSTORDER_TAB);
				
			}
			else if (FORM_ACTION_CONFIRM_CLOSE_CUSTOMER_ORDER.equals(helpForm.getFormAction())) {
				
				CustOrderTabBean custOrderTabBean = helpForm.getCustOrderTabBean();
				CustorderEntity custorderEntity = custOrderTabBean.getCustorderEntity();
				
				Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
				List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>();
				
				custorderEntity = getServiceLocator().getCustorderService().findCustorderById(custorderEntity.getCustorderId());
				OrderUtils.fillOrderNumberLists(custorderEntity, deleteOrderMap, manualDeallocateOrderNumberList);
				
				OrderService orderService = getServiceLocator().getOrderService();
				final CustorderLifecycleReturnCode returnCode = orderService.cascadeCloseCustomerOrder(deleteOrderMap, custorderEntity.getCustorderId());
				
				if (returnCode.isSuccess()) {
					MessageUtils.addActionMessage(request, "custorder.message.closed.successful");
				}
				
			    // reload bestellung page
			    helpForm.setReloadOrderId( helpForm.getOrder().getOrderId().toString() );
			    return result = mapping.findForward(FORWARD_RELOAD_ORDER);
				
			}
			else if (FORWARD_SAVE_CUST_ORDER.equals(helpForm.getFormAction())) {
				// manipulates helpForm
				CustorderSplitDiffWrapper resultWrapper;
				try {
					resultWrapper = splitRecalc(helpForm, request);
				}
				catch (Exception e){ 
					// Return to Custorder Tab Screen and show warning message
					return result = mapping.findForward(FORWARD_ORDER_CUSTORDER_TAB);
				}
				
				// Set the resultwrapper with the changed orders to publish in the popup
				helpForm.getCustOrderTabBean().setCustorderSplitDiffWrapper(resultWrapper);
				
				request.setAttribute("resultWrapper", resultWrapper);
				
				boolean showPopup = false;
				if (resultWrapper != null) {
					if ((resultWrapper.getTobeCreatedOrders() != null && !resultWrapper.getTobeCreatedOrders().isEmpty())
							|| (resultWrapper.getTobeDeletedOrders() != null && !resultWrapper.getTobeDeletedOrders().isEmpty())
							|| (resultWrapper.getTobeEditedOrders() != null && !resultWrapper.getTobeEditedOrders().isEmpty())) {
						showPopup = true;
					}
				}
					
				request.setAttribute("showInitRecalcPopup", showPopup);
				
				if (!showPopup) {
					//saveorder without user confirmation (popup) + reload
					forwardName = saveCustorderTab(helpForm, request);
					return result = mapping.findForward(forwardName);
				}
				return result = mapping.findForward(FORWARD_ORDER_CUSTORDER_TAB);//keeps open
			}
			else if (FORWARD_SAVE_CUST_ORDER_CONFIRMED.equals(helpForm.getFormAction())) {
				forwardName = saveCustorderTab(helpForm, request);
				return result = mapping.findForward(forwardName);
			}
			else if ((FORWARD_SAVE_ORDER.equals(helpForm.getFormAction())) 
					|| (FORWARD_SAVE_CLOSE_ORDER.equals(helpForm.getFormAction()))
					|| (FORWARD_SEND_EMAIL.equals(helpForm.getFormAction())) ) {
				
				Boolean recalcCostsReFreezeOrder = Boolean.FALSE;
				boolean orderSuccessfullySaved = false;
				
				if (logDebugEnabled) {
					LOG.debug("Action: save order!");
				}
				
				try {
					helpForm.validateDeliveryWindow(request);
				}
				catch (ValidationException e) {
					processErrors(request, e, CallerContext.ORDER_DETAIL, false);
					return mapping.findForward(FORWARD_SAVE_ORDER);
				}
				// form validation is called from here because we have popup screen!
				helpForm.validateOrderDetail(request);
				
				if ((MessageUtils.getActionErrors(request) == null) || (MessageUtils.getActionErrors(request).size() == 0)) {
					boolean	adjustedDeliveryDate = adjustCustorderDeliveryWindowIfNeeded(request, helpForm);
					//  Third Party Freighters Tab's data
					List<BindingsDTO> bindings = helpForm.getExistingBindings();
					//takes form fields and fills order
					setupOrder(helpForm, locale, request);
					OrderRepEntity orderRep = null;
					if (helpForm.getRepBean() != null){
						orderRep = helpForm.getRepBean().getOrderRep();
					}
					//update service level fields of the frozen order (if it was not persisted before)
					if ((helpForm.getOrderFinal() != null) && (helpForm.getReportingBean() != null)) {
						OrderFinalEntity ofe =  getFrozenTablesService().updateFrozenOrderSvcLevelInfo(helpForm.getOrderFinal());
						helpForm.setOrderFinal(ofe);
					}
					//true if all Delivered Quantities are not null
					boolean allOrderItemsDelivered = true;
					for (Iterator it = helpForm.getOrder().getOrderItems().iterator(); it.hasNext();) {
						OrderItemEntity oi = (OrderItemEntity) it.next();
						if (oi.getDeliveredQuantity() == null) {
							allOrderItemsDelivered = false;
						}
					}
					
					final boolean orderDelivered = helpForm.getOrder().getActualDeliveryDat() != null;
					final boolean orderSentToSap = LogonUtil.isSentToSap(helpForm.getOrder());
					final boolean bpCorrected = BooleanUtils.toBoolean(helpForm.getOrder().getBpCorrectedFlag() != null);
					if (allOrderItemsDelivered && orderDelivered && (!orderSentToSap || bpCorrected) ){
						recalcCostsReFreezeOrder = Boolean.TRUE;
					}

					Map map = null;
					OrderDetailService orderDetailService = getOrderDetailService();
					
					if ( !recalcCostsReFreezeOrder ) {
						
						// UPDATING THE ORDER =>=>=>==v-v-v-v-v-v
						try {
							OrderDetailBean orderDetailBean = helpForm.getOrderBean();
							OrderEntity order = helpForm.getOrder();
							boolean isPlanningOrder = (helpForm.getPlanningMaterialContractItemId() != null);
							map = orderDetailService.updateOrderAndCustorder(order,
									orderDetailBean.getAutoPoolAssignmentFlag(), 
									orderDetailBean.getAutoCommCarrierAssignmentFlag(), 
									orderRep, isPlanningOrder, bindings);
							if (adjustedDeliveryDate) {
								MessageUtils.addActionMessage(request, "orderdetail.message.custorder.todate.modified");	
							}
						}
						catch (ValidationException e) {
							processErrors(request, e, CallerContext.ORDER_DETAIL, false);
							return mapping.findForward(FORWARD_SAVE_ORDER);
						}
						catch (Exception e) {
							if (e.getCause() instanceof LogonException) {
								MessageUtils.addActionError(request, ((LogonException)e.getCause()).getReason().getReason());
							}
							else {
								MessageUtils.addActionError(request, e);
							}
							return mapping.findForward(FORWARD_SAVE_ORDER);
						}
					}
					else {
						//update costs, freeze, and save should be in the same transaction, but if the cost update fails, it should not prevent normal saving though
						try {									 
							map = orderDetailService.specialUpdateOrderAndOrderFinal( helpForm.getOrder(), 
									helpForm.getOrderBean().getAutoPoolAssignmentFlag(), 
									helpForm.getOrderBean().getAutoCommCarrierAssignmentFlag(), orderRep, bindings);
							if (adjustedDeliveryDate) {
								MessageUtils.addActionMessage(request, "orderdetail.message.custorder.todate.modified");	
							}
							if ( Authorization.isAllowed(Authorization.ORDER_DETAIL_COMMERCIAL )) {
								if ( map != null && !map.isEmpty() && map.containsKey(Reason.class.getName()) && map.get(Reason.class.getName()) != null ) {
									if ( map.get(Reason.class.getName()) instanceof List ) {												
										List exceptions = ((List) map.get(Reason.class.getName()));
										Iterator it = exceptions.iterator();
										while (it.hasNext()) {
											LogonException exc = (LogonException) it.next();
											if ( exc.getReason() != null ) {													
												MessageUtils.addActionWarning(request, exc.getReason().toString() );
											}
										}
									}
								}
								else if ( !map.containsKey(Reason.class.getName() ) ) {
									MessageUtils.addActionMessage(request, "orderdetail.message.cost.calculation");
								}										
							}
						}
						catch (RuntimeException re) {
							if (re.getCause() instanceof LogonException) {
								LogonException le = (LogonException)re.getCause();
								if (le.getReason() != null) {
									MessageUtils.addActionError(request, le.getReason().toString());
									//reset
									helpForm.setFormAction(null);
									forwardName = FORWARD_SAVE_ORDER;
									return result = mapping.findForward(forwardName);
								}
								else {
									throw re;
								}
							}
							else {
								throw re;
							}
						}								
					}
					
					OrderEntity orderEntity = (OrderEntity)map.get(OrderEntity.class.getName());
					OrderRepEntity orderRepEntity = (OrderRepEntity)map.get(OrderRepEntity.class.getName());
					OrderFinalEntity orderFinal = map.get(OrderFinalEntity.class.getName()) != null ? (OrderFinalEntity) map.get(OrderFinalEntity.class.getName()) : null;
					if (orderEntity != null) {
						if ( map.get(ValidationException.class.getName()) != null ) {
							ValidationException ex = (ValidationException) map.get(ValidationException.class.getName());
							processErrors(request, ex, CallerContext.ORDER_DETAIL, true);
						}
						helpForm.setOrder(orderEntity);
						// Initialize order status related fields after order is saved and page is reloaded
						initAfterSave(helpForm);
			
						if (helpForm.getRepBean()  != null) {
							helpForm.getRepBean().setOrderRep(orderRepEntity);
							initRepTab(helpForm);
						}
						helpForm.setOrderFinal(orderFinal);
						
						// if there were any changes in attachments or damage codes, send a rep mail to railadmin
						if (helpForm.getAttachmentListChanged()) {
							helpForm.setAttachmentListChanged(false);
							if ((helpForm.getRepBean()  != null) && (helpForm.getRepBean().getOrderRep()  != null)) {
								getOrderService().sendOrderRepEmail(helpForm.getOrder(), orderRepEntity, locale, true);
							}
						}
			
						
						if (FORWARD_SEND_EMAIL.equals(helpForm.getFormAction())) {
							Authorization.authorize(Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS);
							if ((helpForm.getRepBean()  != null) && (helpForm.getRepBean().getOrderRep()  != null)) {
								getOrderService().sendOrderRepEmail(helpForm.getOrder(), helpForm.getRepBean().getOrderRep(),  locale);
								
								MessageUtils.addActionMessage(request, "orderdetail.message.email.sent");
								//reset
								helpForm.setFormAction(null);
								forwardName = FORWARD_SAVE_ORDER;
								return result = mapping.findForward(forwardName);
							}
						}
													
						MessageUtils.addActionMessage(request, "orderdetail.message.success.save");
						orderSuccessfullySaved = true;
					}
				}
				
				if (FORWARD_SAVE_CLOSE_ORDER.equals(helpForm.getFormAction())) {
					//close Order Detail pop-up (Save & Close button was pressed)
					//reset
					helpForm.setFormAction(null);
					//reload all because the customer order can also be affected
					if (orderSuccessfullySaved) {
						forwardName = FORWARD_DELETE_ORDER;
						request.setAttribute(BestellungAction.RELOAD, BestellungAction.RELOAD_ALL);
					}
					else {
						forwardName = FORWARD_SAVE_ORDER;
					}
					return result = mapping.findForward(forwardName);
				}
				
				// reload bestellung page
				if (orderSuccessfullySaved) {
					request.setAttribute(BestellungAction.RELOAD, BestellungAction.RELOAD_ALL);
				}
				forwardName = FORWARD_SAVE_ORDER;
			}
			else if (FORM_ACTION_HOLD_BACK_RETURN_ORDER.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_W);
				Boolean holdBackReOrderFlag = helpForm.getOrderBean().getRailHoldBackReOrderFlag();
				getOrderService().setOrderRailHoldBackReOrderFlag(helpForm.getOrder().getOrderId(), holdBackReOrderFlag);
				forwardName = FORWARD_RELOAD_ORDER;
				helpForm.setReloadOrderId( helpForm.getOrder().getOrderId().toString() );
				return  result = mapping.findForward(forwardName);
			}
			else if (FORWARD_SHIPPING_POINT.equals(helpForm.getFormAction())) {
				String plantCdStr = request.getParameter("plantCd");
				if (logDebugEnabled) {
					LOG.debug("Action: load Shipping Points! Plant code: " + plantCdStr);
				}
			
				initFirstMaterialItemDependentLists(helpForm, plantCdStr, null);
			
				forwardName = FORWARD_SHIPPING_POINT;
			}
			else if (FORWARD_COMM_CARRIER.equals(helpForm.getFormAction())) {
				String poolIdStr = request.getParameter("poolId");
				if (logDebugEnabled) {
					LOG.debug("Action: load Comm Carriers! Pool id: " + poolIdStr);
				}
				Map <Long, CarrierEntity> commCarrierMap = new HashMap();
				if ( (poolIdStr != null) && (!Constant.INVALID_OBJECT_ID_STR.equals(poolIdStr)) ){
					commCarrierMap = getOrderDetailService().loadCommCarrierMap(StringUtils.toLongValue(poolIdStr));
					
				}
				//always add the attribute stored on the order!
				if ( helpForm.getOrder().getCommCarrier() != null && !commCarrierMap.containsKey(helpForm.getOrder().getCommCarrier().getCarrierId() )) {
					commCarrierMap.put( helpForm.getOrder().getCommCarrier().getCarrierId(), helpForm.getOrder().getCommCarrier() );
				}

				helpForm.setCommCarrierMap(commCarrierMap);
				forwardName = FORWARD_COMM_CARRIER;
			}
			
			else if (FORWARD_EXEC_CARRIER.equals(helpForm.getFormAction())) {
				String poolIdStr = request.getParameter("poolId");
				String commCarrierIdStr = request.getParameter("commCarrierId");
				
				if (logDebugEnabled) {
					LOG.debug("Action: load Exec Carriers! Comm carrier id: " + commCarrierIdStr);
				}

				List<CarrierEntity> execCarrierList = new ArrayList<CarrierEntity>();
				if ((commCarrierIdStr != null) && (!Constant.INVALID_OBJECT_ID_STR.equals(commCarrierIdStr))){
					Long poolId = StringUtils.toLongValue(poolIdStr);
					Long commCarrierId = StringUtils.toLongValue(commCarrierIdStr); 
					Long execCarrierId = null;
					
					if(helpForm.getOrder().getExecCarrier() != null){
						execCarrierId = helpForm.getOrder().getExecCarrier().getCarrierId();
					}
					execCarrierList = getOrderDetailService().loadExecCarrierList(commCarrierId, execCarrierId);					
				}
				Collections.sort(execCarrierList, new CarrierComparator());
				
				helpForm.setExecCarrierList(execCarrierList);
			
				forwardName = FORWARD_EXEC_CARRIER;
			}
			
			else if (FORWARD_LOADING_POINT.equals(helpForm.getFormAction())) {
				String shPointNoStr = request.getParameter("shPointNo");
				if (logDebugEnabled) {
					LOG.debug("Action: load Loading Points! Shipping point no: " + shPointNoStr);
				}
				
				//shipping point
				if (!Constant.INVALID_OBJECT_ID_STR.equals(shPointNoStr)) {
					ShippingpointEntity shPoint = (ShippingpointEntity) helpForm.getShPointMap().get(shPointNoStr);	
					initFirstMaterialItemDependentLists( helpForm, shPoint.getPlant().getPlantCd(), shPoint.getShippingpointId() );			
				}
				else {
					helpForm.setLdPointMap(new HashMap());
				}
				forwardName = FORWARD_LOADING_POINT;
			}
			else if (FORWARD_DELIVERY_NOTE.equals(helpForm.getFormAction())) {
				String matIndexStr = request.getParameter("matIndex");
				if (logDebugEnabled) {
					LOG.debug("Action: load Delivery Note! Material index: " + matIndexStr);
				}
			
				int matIndex = StringUtils.toIntValue(matIndexStr);
				helpForm.getMaterialBean().setSelOrderItemIndex(matIndex);
				OrderItemBean itemBean = helpForm.getMaterialBean().getOrderItemList().get(matIndex);
				helpForm.getMaterialBean().setSelOrderItemId(itemBean.getOrderItemId());
				
				//responsible to update MaterialTabBean.goodsMovementPresent flag,
				//->goods movement information popup icon is shown or not.
				checkGoodsMovementsForLoad(matIndex, helpForm.getMaterialBean());
				 
				forwardName = FORWARD_DELIVERY_NOTE;
			}
			else if (FORWARD_REPLACE_MATERIAL.equals(helpForm.getFormAction())) {
				String orderItemIdStr = request.getParameter("orderItemId");
				String sapContractItemIdStr = request.getParameter("sapContractItemId");
				Long orderItemId = Long.parseLong(orderItemIdStr);
				Long sapContractItemId = Long.parseLong(sapContractItemIdStr);
				
				OrderEntity updatedOrder = getOrderDetailService().replaceMaterialInItem(helpForm.getOrder(), orderItemId, sapContractItemId);
				helpForm.setOrder(updatedOrder);
				
				initOrderDetails(helpForm, helpForm.getOrder(), request);
				initMaterialTab(helpForm);

				
				forwardName = FORWARD_DELIVERY_NOTE;
			}
			else if (FORWARD_LOAD_UOMLIST.equals(helpForm.getFormAction())) {
				//reload uomlist
				forwardName = FORWARD_LOAD_UOMLIST;
			}
			else if (FORWARD_GENERATE_PDF.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_DELIVERY_NOTE);	

				PdfGeneratingResult pdfGeneratingResult =  getServiceLocator().getPDFGeneratorService().generatePDF(helpForm.getOrder().getOrderId());

				if (!pdfGeneratingResult.isSuccess()){
					if(!pdfGeneratingResult.getMessages().isEmpty()) {
						for(String message: pdfGeneratingResult.getMessages()) {
							MessageUtils.addActionError(request, message);
						}
					} else {
						MessageUtils.addActionError(request, "errors.failureGeneration");
					}
				}
				else{
					MessageUtils.addActionMessage(request, "message.successGeneration");
				}
				
				//load Order Outputdoc Entities 
				final Map<Long, String> deliveryNoteMap = loadDeliveryNoteMap(helpForm.getOrder(),
						helpForm.getLocale());
				
				helpForm.getMaterialBean().setDeliveryNoteMap(deliveryNoteMap);
				
				
				forwardName = FORWARD_DELIVERY_NOTE;
			}
			else if (FORWARD_GENERATE_COLL_PDF.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_DELIVERY_NOTE);	
				boolean success = false;
				CustorderEntity custOrder = helpForm.getOrder().getCustorder();
				if (custOrder != null) {
					success =  getServiceLocator().getPDFGeneratorService().generateCollPdf(custOrder.getCustorderId());
					helpForm.getCustOrderTabBean().setCollDeliveryNoteMap(getCollDelNoteMap(custOrder.getCustorderId(), helpForm.getLocale()));
				}
				if (!success) {
					MessageUtils.addActionError(request, "errors.failureGeneration");
				}
				else {
					MessageUtils.addActionMessage(request, "message.successGeneration");
				}
				
				forwardName = FORWARD_COLL_DEL_NOTE;
			}
			else if (FORWARD_REPORTING_ITEM.equals(helpForm.getFormAction())) {
				String matIndexStr = request.getParameter("matIndex");
				if (logDebugEnabled) {
					LOG.debug("Action: load Reportin item! Material index: " + matIndexStr);
				}
			
				int matIndex = StringUtils.toIntValue(matIndexStr);
				helpForm.getReportingBean().setSelOrderItemIndex(matIndex);
				 
				forwardName = FORWARD_REPORTING_ITEM;
			}
			else if (FORWARD_BILLTO.equals(helpForm.getFormAction())) {
				
				String billtoSapBpId = request.getParameter("billtoSapBpId");
				if (logDebugEnabled) {
					LOG.debug("Action: load Billto! billtoSaBppId: " + billtoSapBpId);
				}
			
				ShiptoBilltoBean shiptoBilltoBean = (ShiptoBilltoBean) helpForm.getBilltoMap().get(billtoSapBpId);
				// If billto is already empty (or changed to empty), 
				// display '-', but also show a warning message, because it is mandatory 
				if (shiptoBilltoBean != null && shiptoBilltoBean.getBilltoSapBpEntity() != null) {
					initSelectedBilltoAddress(helpForm.getMaterialBean(), shiptoBilltoBean.getBilltoSapBpEntity(), helpForm.getOrder().getSoldtoSapBp(), helpForm.getOrder(), helpForm);
				} else {
					initSelectedBilltoAddress(helpForm.getMaterialBean(), null, helpForm.getOrder().getSoldtoSapBp(), helpForm.getOrder(), helpForm);
					MessageUtils.addActionWarning(request, "orderspecial.errors.billto.required");
				}
				forwardName = FORWARD_BILLTO;
			}
			else if (FORWARD_PAYER.equals(helpForm.getFormAction())) {
				String payerIdStr = request.getParameter("payerId");
			
				if (logDebugEnabled) {
					LOG.debug("Action: load Payer! payerId: " + payerIdStr);
				}
			
				PayerSapBpBean payerSapBpBean = null;
			
				if (!StringUtils.empty(payerIdStr)) {
					Long payerId = Long.valueOf(payerIdStr);
					Map<Long, PayerSapBpBean> payerMap = helpForm.getPayerMap();
					payerSapBpBean = payerMap.get(payerId);
				}
				
				boolean payerIgnored = OrderUtils.isPayerIgnored(helpForm.getOrder().getSapContract());
				if (payerSapBpBean == null && !payerIgnored) {
					String payerFieldName = MessageUtils.getMessage(request, locale, "fields.payer.payer");
					MessageUtils.addActionWarning(request, "error.field.required", payerFieldName);
				}
				if (payerSapBpBean == null) {
					initSelectedPayerAddress(helpForm.getMaterialBean(), null, helpForm.getOrder().getSoldtoSapBp(), helpForm.getOrder(), helpForm);
				}
				else {
					initSelectedPayerAddress(helpForm.getMaterialBean(), payerSapBpBean.getSapBp(), helpForm.getOrder().getSoldtoSapBp(), helpForm.getOrder(), helpForm);
				}
				
				forwardName = FORWARD_PAYER;
			}
			else if (FORWARD_REPORT_WAGON_AS_EMPTY.equals(helpForm.getFormAction())) {
				Long orderId = helpForm.getOrder().getOrderId();
				AssignmentEntity ass = getOrderDetailService().loadAssignment(orderId);
				Authorization.authorize(Authorization.RAILCARS_FREIMELDEN);
				getServiceLocator().getDispoService().setRailUnloaded(ass.getAssignmentId());
				OrderEntity order = getOrderDetailService().loadOrder(orderId);
				if (order != null) {
					helpForm.setOrder(order);

					initOrderDetails(helpForm, order, request);
					initMaterialTab(helpForm);
				}
				forwardName = FORWARD_SUCCESS;
			}
			else if (LOAD_TAB.equals(helpForm.getFormAction())) {
				String selTab = helpForm.getSelectedTab();
				
				if (FORWARD_MATERIAL_TAB.equals(selTab)) {
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_R);
					helpForm.setMaterialTabOpened(true);
				}
				else if ((FORWARD_REPORTING_TAB.equals(selTab) && (helpForm.getReportingBean() == null))) {
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_R);
					initReportingTab(helpForm, locale);
				}
				else if ((FORWARD_MNG_TAB.equals(selTab) && (helpForm.getMngBean() == null))) {
					Authorization.authorize(Authorization.ORDER_DETAIL_ORDER_MANAGEMENT);
					initMngTab(helpForm, locale);
				}
				else if ((FORWARD_MAP_TAB.equals(selTab) && (helpForm.getMapBean() == null))) {
					Authorization.authorize(Authorization.ORDER_DETAIL_MAP);
					initMapTab(helpForm);
				}
				else if (FORWARD_COMMERCIAL_TAB.equals(selTab)) {
					Authorization.authorize(Authorization.ORDER_DETAIL_COMMERCIAL);
					initCommTab(helpForm);
				}	
				else if (FORWARD_REP_MAINT_TAB.equals(selTab)) {
					Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
				
					helpForm.setRepBean(null);
					if (Constant.DISTRIBUTION_DEST_REPAIR.equals(helpForm.getOrder().getDistributionDest().getDistributionDestCd())) {
						initRepTab(helpForm);
					}	
				}
				else if ((FORWARD_SERVICES_EXTRA_COST_TAB.equals(selTab))) {
					Authorization.authorize(Authorization.ORDER_DETAIL_S_EC_MOD_BEFORE_SAP);
					initServicesExtraCostTab(helpForm, locale);
				}
				else if ((FORWARD_ORDER_CUSTORDER_TAB.equals(selTab))) {
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_R);
					initCustorderTab(helpForm);
				}
				else if (FORWARD_THIRD_PARTY_FREIGHTERS_TAB.equals(selTab)) {
					
					Authorization.authorize(Authorization.ORDER_DETAIL_W);
					// init Tab data
					OrderEntity order = helpForm.getOrder();
					// init list
					if (helpForm.getExistingBindings() == null) {
						List<BindingsDTO> bindings = getOrderDetailService().getExistingBindings(order);
						helpForm.setExistingBindings(bindings);
					}
					forwardName = FORWARD_THIRD_PARTY_FREIGHTERS_TAB;
				}
				
				forwardName = selTab;
			}
			else if (FORM_ACTION_UPLOAD_REP_ORDER_ATT_FILE.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
				OrderRepEntity orderRepEnt = helpForm.getRepBean().getOrderRep();
				if (logDebugEnabled) {
					LOG.debug("Action: upload button is pressed");
				}
				OrderDetailUploadAttachmentBean imgForm = helpForm.getUploadBean();
				
				OrderRepAttEntity att = uploadImage(request, orderRepEnt, imgForm);
			
				if (att != null){					
					List list = helpForm.getRepBean().getFormListForAttachmentType(att);
					list.add(att);
					// don't set this at email attachment changes
					if (!Long.toString(Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL).equals(imgForm.getType())) {						
						helpForm.setAttachmentListChanged(true);
					}
				} else {
					helpForm.setLastUploadFailed(true);
					helpForm.setLastUnsupportedFileExtension(imgForm.getFile().getContentType());
				}
				request.setAttribute("attachmentTypeDiv", helpForm.getUploadBean().getType());
				forwardName = null;
			}
			else if (FORM_ACTION_REMOVE_REP_ORDER_ATT_FILE.equals(helpForm.getFormAction())){
				Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
				OrderDetailUploadAttachmentBean imgForm = helpForm.getUploadBean();
				OrderRepEntity orderRepEnt = helpForm.getRepBean().getOrderRep();
				OrderRepAttEntity att = deleteAttachment(imgForm, orderRepEnt);
				if (att != null )	{
					List list = helpForm.getRepBean().getFormListForAttachmentType(att);					
					list.remove(att);
					if (!Long.toString(Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL).equals(imgForm.getType())) {						
						helpForm.setAttachmentListChanged(true);
					}
				}
				request.setAttribute("attachmentTypeDiv", helpForm.getUploadBean().getType());
				forwardName = FORWARD_REP_TAB_ATTACHMENT_DIV;
			}
			else if (FORM_ACTION_REP_TAB_RELOAD_ATTACHMENT_DIV.equals(helpForm.getFormAction())){
				request.setAttribute("attachmentTypeDiv", helpForm.getUploadBean().getType());
				/*
				 * if there were some problems during file upload (unsupported mime type)
				 * The error should be displayed here due to the double call (see the
				 * callback function in WagonMaintenanceDmgTile.jsp
				 */
				if (helpForm.isLastUploadFailed()) {
					helpForm.setLastUploadFailed(false);
					MessageUtils.addActionWarning(request,"useradmin.errors.mimeunsupported", helpForm.getLastUnsupportedFileExtension());
				}
				forwardName = FORWARD_REP_TAB_ATTACHMENT_DIV;
			}
			else if (FORM_ACTION_ADD_DAMAGECODE.equals(helpForm.getFormAction())){
				Authorization.authorize(Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS);
				Long dCd = helpForm.getRepBean().getDamageCode();
				DamagecodeBsEntity bs = getRefDataGenericsService().findBsEntity(DamagecodeBsEntity.class, dCd);
				OrderRepDmgcodeTypeEntity damageCode = new OrderRepDmgcodeTypeEntity();
				damageCode.setDmgcodeType(bs);
				damageCode.setOrderRep(helpForm.getRepBean().getOrderRep());
				damageCode.setCtlActCd(Boolean.TRUE);
				helpForm.getRepBean().getOrderRep().getDamageCdList().add(damageCode);
				List list = new ArrayList();
				list.add(damageCode);
				List list2 = getOrderDetailService().getDamageCodeTxList(helpForm.getLocale(), list);
				DamagecodeTxEntity txEnt = (DamagecodeTxEntity)list2.get(0);
				helpForm.getRepBean().getDamageCodeTxList().add(txEnt);
				forwardName = FORWARD_REP_TAB_DAMAGECODE_TILE;
			}
			else if (FORM_ACTION_REMOVE_DAMAGECODE.equals(helpForm.getFormAction())){
				Authorization.authorize(Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS);
				Long dCd = helpForm.getRepBean().getDamageCode();
				Iterator it = helpForm.getRepBean().getOrderRep().getDamageCdList().iterator();
				while (it.hasNext()) {
					OrderRepDmgcodeTypeEntity dmg = (OrderRepDmgcodeTypeEntity) it.next();
					if (dmg.getDmgcodeType().getDamagecodeCd().equals(dCd)) {
						it.remove();
						break;
					}
				}
				it = helpForm.getRepBean().getDamageCodeTxList().iterator();
				while (it.hasNext()) {
					DamagecodeTxEntity dmg = (DamagecodeTxEntity) it.next();
					if (dmg.getDamagecodeTxPK().getDamagecodeBs().getDamagecodeCd().equals(dCd)) {
						it.remove();
						break;
					}
				}				
				forwardName = FORWARD_REP_TAB_DAMAGECODE_TILE;
			}
			else if (FORM_ACTION_LOAD_ORDER_AUDIT.equals(helpForm.getFormAction())){
				
				final Long orderId = helpForm.getOrder().getOrderId();
				
				loadBizeventAudits(request, orderId);
				forwardName = FORM_ACTION_LOAD_ORDER_AUDIT;
			}
			else if (FORM_ACTION_ADD_EXTRA_COST.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_S_EC_MOD_BEFORE_SAP);
				forwardName = FORM_ACTION_ADD_EXTRA_COST;
				addExtraCost(helpForm);
				initServicesExtraCostTab(helpForm, locale);
			}
			else if (FORM_ACTION_REMOVE_EXTRA_COST.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_S_EC_MOD_BEFORE_SAP);
				forwardName = FORM_ACTION_REMOVE_EXTRA_COST;
				removeExtraCost(helpForm);
				initServicesExtraCostTab(helpForm, locale);
			}
			else if (FORM_ACTION_ADD_SERVICE_MATERIAL.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_S_EC_MOD_BEFORE_SAP);
				forwardName = FORWARD_SERVICES_EXTRA_COST_TAB;
				addServiceItem(helpForm, request);
				initMaterialTab(helpForm);
				initServicesExtraCostTab(helpForm, locale);
			}
			else if (FORWARD_GOODS_MOVEMENT_POPUP.equals(helpForm.getFormAction())) {
				int matIndex = helpForm.getMaterialBean().getSelOrderItemIndex();
				initializeGoodsMovementsForItemIndex(matIndex, helpForm.getMaterialBean(), request);
				forwardName = FORWARD_GOODS_MOVEMENT_POPUP;
			}

			else if (FORWARD_VENDOR_PF_POPUP.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.ORDER_DETAIL_W);
				List<String> partnerFunctionCodeList = getServiceLocator().
						getOrderDetailService().fetchPartnerFunctionCodeList();
				
				request.setAttribute(ATTRIBUTE_PARTNER_FUNCTION_CODES, partnerFunctionCodeList);
				forwardName = FORWARD_VENDOR_PF_POPUP;
			}

			//reset
			helpForm.setFormAction(null);
			
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
	 * Triggers save of customer order. 
	 * @param helpForm
	 * @param request
	 * @return forwardName
	 */
	private String saveCustorderTab(OrderDetailForm helpForm, HttpServletRequest request) {
		String forwardName;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Action: save custorder!");
		}
		
		CustorderEntity custorder = helpForm.getCustOrderTabBean().getCustorderEntity();
		
		// Save the custorder and its leafs
		getServiceLocator().getCustorderService().mergeCustomerOrder(custorder);
		//todo orsika 20130120 exception handling?
		
		MessageUtils.addActionMessage(request, "orderdetail.message.custorder.closed.successful");
	
		//reset
		helpForm.setFormAction(null);
	
	    // reload right tab
	    helpForm.setReloadOrderId(helpForm.getOrder().getOrderId().toString());
		return FORWARD_RELOAD_ORDER;
	}
	
	/**
	 * Triggers save of customer order. 
	 * @param helpForm
	 * @param request
	 * @return forwardName
	 */
	private String enableSendToSapFlag(OrderDetailForm helpForm, HttpServletRequest request) {
		OrderEntity orderEntity = getServiceLocator().getOrderService().findOrderById(helpForm.getOrder().getOrderId());
		orderEntity.setForceToSapFlag(Boolean.TRUE);		
		MessageUtils.addActionMessage(request, "orderdetail.message.order.sendtosap");
		final OrderDetailService orderDetailService = getOrderDetailService();
		orderDetailService.saveSendToSapFlag(orderEntity);
		helpForm.setReloadOrderId(orderEntity.getOrderId().toString());
	   return FORWARD_RELOAD_ORDER;
	}

	private String wrongLocationApproved(OrderDetailForm helpForm, HttpServletRequest request) {
		OrderEntity orderEntity = getOrderService().findOrderById(helpForm.getOrder().getOrderId());
		ItrackStatusBsEntity itrackStatusBsEntity = getRefDataGenericsService().findBsEntity(ItrackStatusBsEntity.class,
				ITrackStatus.DELIVERED_LOCATION_APPROVED.getStatusCode());
		orderEntity.setItrackStatus(itrackStatusBsEntity);
		final OrderDetailService orderDetailService = getOrderDetailService();
		orderDetailService.saveItrackStatusChange(orderEntity);
		MessageUtils.addActionMessage(request, "orderdetail.message.order.checkunloadlocation");
		helpForm.setReloadOrderId(orderEntity.getOrderId().toString());
		return FORWARD_RELOAD_ORDER;
	}

	/**
	 * Triggers the recalc of the single delivery orders of a custorder and prepares the parameters for the split. 
	 * @param helpForm
	 * @param request
	 * @return the results in a wrapper
	 */
	private CustorderSplitDiffWrapper splitRecalc(OrderDetailForm helpForm, HttpServletRequest request) throws NumberFormatException {
		OrderEntity order = helpForm.getOrder();
		CustorderService coService = getServiceLocator().getCustorderService();
		CustorderEntity custorder = order.getCustorder();
		custorder = coService.findCustorderById(custorder.getCustorderId());
		
		CustOrderTabBean custorderTabBean = helpForm.getCustOrderTabBean();
		
		// Making sure to not mix up references when using the split methods
		String parameterExactCustorderQtyFlag = request.getParameter("exactCustorderQtyFlag");
		String parameterExactSdorderQtyFlag = request.getParameter("exactSdorderQtyFlag");
		
		
		for (CustorderItemEntity coi : (List<CustorderItemEntity>) custorder.getCustorderItems()) {
			if (parameterExactCustorderQtyFlag != null) {
				coi.setExactCustorderQtyFlag(BooleanUtils.toBoolean(parameterExactCustorderQtyFlag));
			}
			if (parameterExactSdorderQtyFlag != null) {
				coi.setExactSdorderQtyFlag(BooleanUtils.toBoolean(parameterExactSdorderQtyFlag));
			}
			
			Long sapMatId = coi.getSapMaterial().getSapMaterialId();
			Map<String, String> map = custorderTabBean.getCustorderItemsQtyBySapMatIdMap();
			
			String editedCoiQtyString = map.get(sapMatId.toString());
			try {
				BigDecimal editedCoiQty = new BigDecimal(editedCoiQtyString);
				
				if (Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE.compareTo(editedCoiQty) < 0
						|| Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE.compareTo(editedCoiQty) > 0
						|| StringUtils.getDecimalFractionLength(editedCoiQty.toString()) > 3)
				{
					String[] additionalInfo = {};
					throw new ValidationException(new ValidationErrorReason(CustorderError.INVALID_ORDERED_QUANTITY, custorder, additionalInfo));
				}
				coi.setOrderedQuantity(editedCoiQty);
			}
			catch (ValidationException e){
				processErrors(request, e, CallerContext.ORDER_DETAIL, true);
				throw e;
			}
		}
		
		try {
			// Do the split recalc
			CustorderEntity splittedCustorder = coService.splitBulkCustorderEntity(custorder);
			CustorderEntity previousCustOrder = coService.findCustorderById(custorder.getCustorderId());
			CustorderSplitDiffWrapper resultWrapper = CustorderUtils.getCustorderDiff(previousCustOrder, splittedCustorder);
			helpForm.getCustOrderTabBean().setCustorderEntity(splittedCustorder);
			return resultWrapper;
		}
		catch (ValidationException e) {
			processErrors(request, e, CallerContext.ORDER_DETAIL, true);
			throw e;
		}
		
	}

	/**
	 * Loads {@link BizeventAuditWrapper} objects. These wrapper objects already contain language resolved data for
	 * all the audits stored.
	 * 
	 * @param request
	 */
	private void loadBizeventAudits(HttpServletRequest request, final Long orderId ) {
		
		final OrderDetailService orderDetailService = getOrderDetailService();
		final String langCd = request.getLocale().getLanguage();
		
		/**
		 * This list in this form is not OK to show on screen, needs postprocessing as the audits contains info like:
		 *  
		 * pool_id changed from 1002 -> 1003
		 * 
		 */
		final List<BizeventAuditDisplayEntity> audits  = orderDetailService.loadBizeventAudits(orderId, langCd);
		
		List<BizeventAuditWrapper> wrappers = getServiceLocator()
				.getBizeventAuditService()
				.wrapAuditViewEntities(audits, request.getLocale(), getUserTimezone() );
		
		Collections.sort(wrappers, new BizeventAuditWrapperComparator());
		
		request.setAttribute(OrderDetailForm.REQUEST_BIZEVENT_AUDIT_LIST, wrappers);
	}

	/**
	 * Updates the costs, and calls freezeOrder in order to update the frozen tables accordingly.
	 * @param request
	 * @param helpForm
	 * @return
	 */
	private void updateCostsReFreezeOrder(HttpServletRequest request, OrderDetailForm helpForm) {
		
		//Cost Calculation
		try {
			List exceptions = getOrderDetailService().updateCostsReFreezeOrder(helpForm.getOrder().getOrderId());
			if ( exceptions != null && !exceptions.isEmpty()) {
				Iterator it2 = exceptions.iterator();
				while (it2.hasNext()) {
					LogonException object = (LogonException) it2.next();						
					MessageUtils.addActionError(request, object.getReason().toString());											
				}
			}
			else {
				MessageUtils.addActionMessage(request, "orderdetail.message.cost.calculation");
			}			
		} 
		catch (RuntimeException re) {
			if (re.getCause() instanceof LogonException) {
				LogonException le = (LogonException)re.getCause();
				LOG.error("Error when calculating costs or updating frozen tables: " + le.getReason() + ", " + le.getMessage());
				if (le.getReason() != null) {
					MessageUtils.addActionError(request, le.getReason().toString());
				} 
				else {
					MessageUtils.addActionError(request, "orderdetail.message.error.cost.calc");
				}
			}
			else {
				throw re;
			}
		}
	}


	private void initRepTab(OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initRepTab", params);
		}
		// ]]
		try {
			RepTabBean repBean = new RepTabBean();
			OrderDetailUploadAttachmentBean uploadBean = new OrderDetailUploadAttachmentBean();
			 helpForm.setUploadBean(uploadBean);
			
			// map of MaintTypeTxEntity
			Map maintTypeTxEntityMap = new LinkedHashMap();
			List maintTypeBsEntityList = new ArrayList();
			
			List maintTypeTxList = getOrderDetailService().getMaintTypeTxList(helpForm.getLocale());
			for (Iterator it = maintTypeTxList.iterator(); it.hasNext();) {
				MaintTypeTxEntity maintTx = (MaintTypeTxEntity) it.next();
				maintTypeTxEntityMap.put(maintTx.getMaintTypeTxPK().getMaintTypeBs().getMaintTypeCd(), maintTx);
				maintTypeBsEntityList.add(maintTx.getMaintTypeTxPK().getMaintTypeBs());
			}
			repBean.setMaintTypeTxEntityMap(maintTypeTxEntityMap);
			repBean.setMaintTypeBsEntityList(maintTypeBsEntityList);
			
			//map of CurrencyBsEntity
			List currBsEntityList = getRefDataGenericsService().findBsEntityList(CurrencyBsEntity.class);
			Map currencyBsEntityMap = new LinkedHashMap();
			for (Iterator it = currBsEntityList.iterator(); it.hasNext();) {
				CurrencyBsEntity bs = (CurrencyBsEntity) it.next();
				currencyBsEntityMap.put(bs.getCurrencyCd(), bs);
			}
			repBean.setCurrencyBsEntityMap(currencyBsEntityMap);		
			
			OrderRepEntity orderRep = getOrderDetailService().loadOrderRep(helpForm.getOrder().getOrderId());
			
			if (orderRep != null) {
				//in case list from db is empty (no Order Rep Maint Types)
				if (orderRep.getOrderRepMaintTypes() == null) {
					orderRep.setOrderRepMaintTypes(new ArrayList());
				}
				repBean.setOrderRep(orderRep);
				List damageCodeList = getOrderDetailService().getDamageCodeTxList(helpForm.getLocale(), orderRep.getDamageCdList());
				repBean.setDamageCodeTxList(damageCodeList);
				//email
				repBean.setEmailTo(orderRep.getEmailToAdresses());
				repBean.setEmailCc(orderRep.getEmailCcAdresses());
				repBean.setEmailBcc(orderRep.getEmailBccAdresses());
				repBean.setEmailText(orderRep.getEmailAdditionalRemarks());
				if (orderRep.getEmailLastSentDat() != null) {
					String lastSentDat = getUserTimezone().fmtAsDayMonthYearHourMinute(orderRep.getEmailLastSentDat());
					repBean.setEmailLastSent(lastSentDat);
				}
				else {
					repBean.setEmailLastSent("-");
				}
				
				//offer
				if (orderRep.getOfferAcceptDat() != null) {
					repBean.setOfferAcceptDate(DateTimeFormat.asLogonDayMonthYear(orderRep.getOfferAcceptDat()));
					repBean.setOfferAcceptTime(DateTimeFormat.asHourMinute(orderRep.getOfferAcceptDat()));
				}
				repBean.setOfferAcceptFlag(orderRep.getOfferAcceptFlag());
				if (orderRep.getLeavePlantDat() != null) {
					repBean.setLeavePlantDate(DateTimeFormat.asLogonDayMonthYear(orderRep.getLeavePlantDat()));
					repBean.setLeavePlantTime(DateTimeFormat.asHourMinute(orderRep.getLeavePlantDat()));
				}	
				if (orderRep.getEnterPlantDat() != null) {
					repBean.setEnterPlantDate(DateTimeFormat.asLogonDayMonthYear(orderRep.getEnterPlantDat()));
					repBean.setEnterPlantTime(DateTimeFormat.asHourMinute(orderRep.getEnterPlantDat()));
				}					
				if (orderRep.getOfferAmount() != null) {
					repBean.setOfferAmount(orderRep.getOfferAmount().toString());
				}
				if (orderRep.getOfferCurrency() != null) {
					repBean.setOfferAmountCur(orderRep.getOfferCurrency().getCurrencyCd());
				}
				repBean.setOfferDesiredFlag(orderRep.getOfferDesiredFlag());
				repBean.setDocumentationAcceptFlag(orderRep.getDocumentationAcceptFlag());
				
				//invoice
				if (orderRep.getInvoiceDat() != null) {
					repBean.setInvoiceDate(DateTimeFormat.asLogonDayMonthYear(orderRep.getInvoiceDat()));
					repBean.setInvoiceTime(DateTimeFormat.asHourMinute(orderRep.getInvoiceDat()));
				}
				if (orderRep.getInvoiceAmount() != null) {
					repBean.setInvoiceAmount(orderRep.getInvoiceAmount().toString());
				}
				if (orderRep.getInvoiceCurrency() != null) {
					repBean.setInvoiceAmountCur(orderRep.getInvoiceCurrency().getCurrencyCd());
				}
				repBean.setInvoicePaidFlag(orderRep.getInvoicePaidFlag());
				if (orderRep.getInvoiceNo() != null){
					repBean.setInvoiceNo(orderRep.getInvoiceNo().toString());
				}
				repBean.setInternalRemark(orderRep.getRemarksInternal());
				repBean.setDocumentationRemarks(orderRep.getDocumentationRemarks());
				repBean.setOfferRemarks(orderRep.getOfferRemarks());
			}
			
			helpForm.setRepBean(repBean);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	
	/** Initializes order status related fields after order is saved and page is reloaded
	 * @param helpForm
	 */
	private void initAfterSave(OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initAfterSave", params);
		}
		// ]]
		try {
			//update order status (in the title)
			long orderStatusCd = helpForm.getOrder().getOrderStatus().getOrderStatusCd();
			OrderStatusTxEntity orderStatus = getRefDataGenericsService().findTxEntity(OrderStatusTxEntity.class, helpForm.getLocale(),orderStatusCd);
			helpForm.getOrderBean().setOrderStatus(orderStatus.getTxt());
			
			//update order status log
			List orderStatusFormattedList = getOrderStatusList(helpForm.getLocale(), helpForm.getOrder());
			helpForm.getOrderBean().setOrderStatusList(orderStatusFormattedList);
			
			// set pool and carrier assignment flags
			setPoolAndCarrierFlags(helpForm.getOrderBean(), orderStatusCd);
			
			helpForm.setPlanningMaterialContractItemId(null);
			helpForm.setPlanningMaterialReplacementList(null);
			helpForm.setPlanningMaterialStillEditable(false);
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
	 * If auth parameter is sent, save should happen because some fields were editable.
	 * @param helpForm
	 * @return
	 */
	private boolean checkAuthorization(OrderDetailForm helpForm) {
		//maybe not even needed since for all mapping it is separately authorized if the parameter is sent... 
		boolean result = false;
		if (helpForm.getAuthParamList() != null && !helpForm.getAuthParamList().isEmpty()){			
			Iterator it = helpForm.getAuthParamList().iterator();
			while (it.hasNext()) {
				Long param = (Long) it.next();
				switch ((int) param.longValue()){
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_DELIVERY_DATE_TIME:	 // Order Detail (Main): write access for delivery date/time)
					Authorization.authorize(Authorization.ORDER_DETAIL_DELIVERY_DATE_TIME);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_BILLTO_PARTY:	 	// Order Detail Material: change billto party
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_BILLTO_PARTY);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_PAYER_PARTY: 	// Order Detail Material: change payer party
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_PAYER_PARTY);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_ONETIMESOLDTO_ADDR: // Order Detail Material: change one-time soldto address
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_ONETIMESOLDTO_ADDR);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_FORWARDING_AGENT:// Order Detail Material: change forwarding agent
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_FORWARDING_AGENT);
					break;			
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO:// Order Detail Material: change customs serial number
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_QUANTITY_UOM:// Order Detail Material: change order quantity and UoM
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_QUANTITY_UOM);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_W:// Order Detail Material: write access (except for 6 special functions)
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_W);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_W:
					Authorization.authorize(Authorization.ORDER_DETAIL_W);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_DELIVERY_NOTE: // Order Detail Material: change comment add/on delivery note
					Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_DELIVERY_NOTE);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_W: // Order Detail Report: write access (exept for 6 special functions)
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_W);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG: // Order Detail Report: change service level flag
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_RATING_COMMNT_CRH: // Order Detail Report: change rating comment crh
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CRH);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_RATING_COMMNT_CARR: // Order Detail Report: change rating comment carrier
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CARR);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_COMMENT_DISPATCHING: // Order Detail Report: change comment dispatching (rail/road)
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_COMMENT_DISPATCHING);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_COMMENT_DRIVER: // Order Detail Report: change comment truck driver
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_COMMENT_DRIVER);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_COMMENT_SHIPPING: // Order Detail Report: change comment shipping (rail/road)
					Authorization.authorize(Authorization.ORDER_DETAIL_REPORT_COMMENT_SHIPPING);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_ORDER_MANAGEMENT: // Order Detail Order Management
					Authorization.authorize(Authorization.ORDER_DETAIL_ORDER_MANAGEMENT);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_WAGON_SPECIAL_ORDER_CREATE_EDIT: // Wagon Special Order: create/edit access and repair mainteance tab
					Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
					break;
				case (int)OrderDetailForm.PARAM_ORDER_DETAIL_SPI: // Order Detail (Main): change and store the special processing indicator
					Authorization.authorize(Authorization.ORDER_DETAIL_SPI);
				break;
				default: 
					LOG.error("Invalid auth param given for order detail save functionality.");
				throw new LogonException();
				}
			}
			result = true;
		}
		return result;
	}

	private void initMapTab(OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initMapTab", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_DETAIL_MAP);
			MapTabBean mapBean = new MapTabBean();
			if (helpForm.getOrder().getShippingpoint() != null) {
				ShippingpointEntity shPoint = helpForm.getOrder().getShippingpoint();
				mapBean.setShpointCoordLat(shPoint.getCoordLat() + "");
				mapBean.setShpointCoordLon(shPoint.getCoordLon() + "");
				mapBean.setShpointName(shPoint.getShippingpointName());
			
				PlantBsEntity plant = shPoint.getPlant();
				mapBean.setPlantCoordLat(plant.getCoordLat() + "");
				mapBean.setPlantCoordLon(plant.getCoordLon() + "");
				mapBean.setPlantName(plant.getPlantName());
			}
			
			if (helpForm.getShipto() != null) {
				if (helpForm.getShipto().getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED){
					mapBean.setShiptoCoordLat(helpForm.getOrder().getLnfCoordLat() + "");
					mapBean.setShiptoCoordLon(helpForm.getOrder().getLnfCoordLon() + "");
					mapBean.setShiptoName(helpForm.getOrder().getShiptoSapBp().getName1() + "(" +
							helpForm.getOrder().getLnfSiteName() + "<br>" +
							helpForm.getOrder().getLnfSiteStreetHouseNo() + "<br>" +
							helpForm.getOrder().getLnfSitePostalCode() + " " +
							helpForm.getOrder().getLnfSiteCity() + "<br>" +
							helpForm.getOrder().getLnfSiteCountry() + ")");
				}
				else {
					mapBean.setShiptoCoordLat(helpForm.getShipto().getCoordLat() + "");
					mapBean.setShiptoCoordLon(helpForm.getShipto().getCoordLon() + "");
					mapBean.setShiptoName(helpForm.getOrder().getShiptoSapBp().getName1());
				}
			}
			final List<OrderItemEntity> sortedOrderItems = OrderUtils.getOrderItemsFromOrderSorted(helpForm.getOrder());

			helpForm.setMapBean(mapBean);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private void initCommTab(OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initCommTab", params);
		}
		// ]]
		try {
			
			List marginList = getOrderDetailService().loadMarginList(helpForm.getOrder().getOrderId());
			List<OrderItemBean> itemBeanList = helpForm.getMaterialBean().getOrderItemList();
			helpForm.setRecalculateCostsFlag(false);
			
			for (Iterator it = marginList.iterator(); it.hasNext();) {
				OrderItemMarginEntity oi = (OrderItemMarginEntity) it.next();
				
				for (Iterator<OrderItemBean> it1 = itemBeanList.iterator(); it1.hasNext();) {
					OrderItemBean itemBean = it1.next();
					
					if (StringUtils.empty(itemBean.getDeliveredQuantity())) {
						helpForm.setRecalculateCostsFlag(true);
					}
					
					if (itemBean.getOrderItemId() == oi.getOrderItemId().longValue()) {
						CommercialTabBean bean = new CommercialTabBean();
						bean.setCurrencyCd(oi.getCurrencyCd());
						BigDecimal ns = oi.getNetSalesEarning();
			
						boolean delQtysetFlag = false;
						if (StringUtils.notEmpty(itemBean.getDeliveredQuantity())) {
							delQtysetFlag = true;
						}
						
						//Income
						if (oi.getNetpriceQuantity() != null) {
							bean.setNetpriceQuantity(" (" + oi.getNetpriceQuantity().toString() +")");
						}
						else {
							bean.setNetpriceQuantity("");
						}
						
						bean.setGrossPrice(NumberUtils.formatNumber(oi.getGrossPrice()));
						String grossPricePt = ((oi.getGrossPricePt() == null) && (oi.getGrossPrice() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getGrossPricePt());
						bean.setGrossPricePt(grossPricePt);
						
						bean.setDiscounts(NumberUtils.formatNumber(oi.getDiscounts()));
						String discountsPt = ((oi.getDiscountsPt() == null) && (oi.getDiscounts() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getDiscountsPt());
						bean.setDiscountsPt(discountsPt);
						
						bean.setNetPrice(NumberUtils.formatNumber(oi.getNetPrice()));
						String netPricePt = ((oi.getNetPricePt() == null) && (oi.getNetPrice() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getNetPricePt());
						bean.setNetPricePt(netPricePt);
						
						bean.setRebates(NumberUtils.formatNumber(oi.getRebates()));
						String rebatesPt = ((oi.getRebatesPt() == null) && (oi.getRebates() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getRebatesPt());
						bean.setRebatesPt(rebatesPt);
						
						bean.setCashDiscount(NumberUtils.formatNumber(oi.getCashDiscount()));
						String cashDiscountPt = ((oi.getCashDiscountPt() == null) && (oi.getCashDiscount() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getCashDiscountPt());
						bean.setCashDiscountPt(cashDiscountPt);
						
						bean.setNetSalesEarining(NumberUtils.formatNumber(oi.getNetSalesEarning()));
						String netSalesEariningPt = ((oi.getNetSalesEarningPt() == null) && (oi.getNetSalesEarning() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getNetSalesEarningPt());
						bean.setNetSalesEariningPt(netSalesEariningPt);
						
						//Costs
						String prodCost = (oi.getProdCost() == null) ? ((delQtysetFlag) ? Constant.ERROR : "") : NumberUtils.formatNumber(oi.getProdCost());
						bean.setProdCost(prodCost);
						String prodCostPt = ((oi.getProdCostPt() == null) && (oi.getProdCost() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getProdCostPt());
						bean.setProdCostPt(prodCostPt);
					
						String iudCost = (oi.getIudCost() == null) ? ((delQtysetFlag) ? Constant.ERROR : "") : NumberUtils.formatNumber(oi.getIudCost());
						bean.setIudCost(iudCost);
						String iudCostPt = ((oi.getIudCostPt() == null) && (oi.getIudCost() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getIudCostPt());
						bean.setIudCostPt(iudCostPt);							
				
						String handlingCost = (oi.getHandlingCost() == null) ? ((delQtysetFlag) ? Constant.ERROR : "") : NumberUtils.formatNumber(oi.getHandlingCost());
						bean.setHandlingCost(handlingCost);
						String handlingCostPt = ((oi.getHandlingCostPt() == null) && (oi.getHandlingCost() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getHandlingCostPt());
						bean.setHandlingCostPt(handlingCostPt);							
				
						String outCost = (oi.getOutCost() == null) ? ((delQtysetFlag) ? Constant.ERROR : "") : NumberUtils.formatNumber(oi.getOutCost());
						bean.setOutCost(outCost);
						String outCostPt = ((oi.getOutCostPt() == null) && (oi.getOutCost() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getOutCostPt());
						bean.setOutCostPt(outCostPt);							
						
						bean.setTotalDistCost(NumberUtils.formatNumber(oi.getTotalDistCost()));
						String totalDistCostPt = ((oi.getTotalDistCostPt() == null) && (oi.getTotalDistCost() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getTotalDistCostPt());
						bean.setTotalDistCostPt(totalDistCostPt);							
						
						bean.setTotalCost(NumberUtils.formatNumber(oi.getTotalCost()));
						String totalCostPt = ((oi.getTotalCostPt() == null) && (oi.getTotalCost() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getTotalCostPt());
						bean.setTotalCostPt(totalCostPt);							
						
						//Marge
						bean.setContribMargin(NumberUtils.formatNumber(oi.getContribMargin()));
						String contribMarginPt = ((oi.getContribMarginPt() == null) && (oi.getContribMargin() != null)) ? Constant.NOT_AVAILABLE : NumberUtils.formatNumber(oi.getContribMarginPt());
						bean.setContribMarginPt(contribMarginPt);							
						
						
						if (ns != null) {
							 ns = ns.divide(new BigDecimal(100), Constant.SCALE, BigDecimal.ROUND_HALF_UP);
							 
							 if (ns.compareTo(BigDecimal.ZERO) != 0) {
								 //Income
								 String grossPricePer = (oi.getGrossPrice() != null) ? NumberUtils.formatNumber(oi.getGrossPrice().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setGrossPricePer(grossPricePer);
								 String discountsPer = (oi.getDiscounts() != null) ? NumberUtils.formatNumber(oi.getDiscounts().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setDiscountsPer(discountsPer);
								 String netPricePer = (oi.getNetPrice() != null) ? NumberUtils.formatNumber(oi.getNetPrice().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setNetPricePer(netPricePer);
								 String rebatesPer = (oi.getRebates() != null) ? NumberUtils.formatNumber(oi.getRebates().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setRebatesPer(rebatesPer);
								 String cashDiscountPer = (oi.getCashDiscount() != null) ? NumberUtils.formatNumber(oi.getCashDiscount().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setCashDiscountPer(cashDiscountPer);
								 bean.setNetSalesEariningPer(NumberUtils.formatNumber(new BigDecimal(100))  + "%");
	
								 //Costs
								 String prodCostPer = (oi.getProdCost() != null) ? NumberUtils.formatNumber(oi.getProdCost().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setProdCostPer(prodCostPer);
								 String iudCostPer = (oi.getIudCost() != null) ? NumberUtils.formatNumber(oi.getIudCost().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setIudCostPer(iudCostPer);
								 String handlingCostPer = (oi.getHandlingCost() != null) ? NumberUtils.formatNumber(oi.getHandlingCost().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setHandlingCostPer(handlingCostPer);
								 String outCostPer = (oi.getOutCost() != null) ? NumberUtils.formatNumber(oi.getOutCost().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setOutCostPer(outCostPer);
								 String totalDistCostPer = (oi.getTotalDistCost() != null) ? NumberUtils.formatNumber(oi.getTotalDistCost().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setTotalDistCostPer(totalDistCostPer);
								 String totalCostPer = (oi.getTotalCost() != null) ? NumberUtils.formatNumber(oi.getTotalCost().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setTotalCostPer(totalCostPer);
								 
								 //Marge
								 String contribMarginPer = (oi.getContribMargin() != null) ? NumberUtils.formatNumber(oi.getContribMargin().divide(ns, Constant.SCALE, BigDecimal.ROUND_HALF_UP)) + "%" : "";
								 bean.setContribMarginPer(contribMarginPer);
							 }
						}
						
						
						itemBean.setCommercialBean(bean);
						break;
					}
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
	
	private void initReportingTab(OrderDetailForm helpForm, Locale locale) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initReportingTab", params);
		}
		// ]]
		try {
			OrderFinalEntity orderFinal = getFrozenTablesService().loadOrderFinal(helpForm.getOrder().getOrderId());
			helpForm.setOrderFinal(orderFinal);
			
			if (orderFinal != null) {
				
				ReportingTabBean repBean = new ReportingTabBean();
				List orderItemFBeanList = new ArrayList();
				StringBuffer noteRailwayDisp = new StringBuffer();
				StringBuffer noteDispatching = new StringBuffer();
				
				
				DeliveryTypeTxEntity actDeliveryTypeTx = getRefDataGenericsService().findTxEntity(DeliveryTypeTxEntity.class, 
						locale, orderFinal.getODeliveryTypeCd());
				
				
				for (Iterator iterator = orderFinal.getOrderFinalItems().iterator(); iterator.hasNext();) {
					OrderItemFinalEntity oiFinal = (OrderItemFinalEntity) iterator.next();
					
					
					OrderItemFinalBean oiFinalBean = new OrderItemFinalBean();
					oiFinalBean.setOrderItemId(oiFinal.getOiOrderItemId());
					oiFinalBean.setOrderItemNo(oiFinal.getOiOrderItemNo().toString());
					oiFinalBean.setSapMaterialid(oiFinal.getOiSapMaterialId());
					
					SapMaterialDescriptionEntity loadMaterialDesc = getOrderDetailService().loadMaterialDesc(oiFinal.getOiSapMaterialId(), locale.getLanguage());
					oiFinalBean.setSapMaterialName(loadMaterialDesc.getTxt());
					
					//add delivered quantity
					if (oiFinal.getOiDeliveredQuantity() != null) {
						oiFinalBean.setDeliveredQuantity(oiFinal.getOiDeliveredQuantity().toString());
					}
					oiFinalBean.setDeliveredUom(oiFinal.getOiDeliveredUomCd());
					
					//add loaded quantity
					if (oiFinal.getOiLoadedQuantity() != null) {
						oiFinalBean.setLoadedQuantity(oiFinal.getOiLoadedQuantity().toString());
					}
					oiFinalBean.setLoadedUom(oiFinal.getOiLoadedUomCd());
					
					//add returned quantity
					if (oiFinal.getOiReturnedQuantity() != null) {
						oiFinalBean.setReturnedQuantity(oiFinal.getOiReturnedQuantity().toString());
					}
					oiFinalBean.setReturnedUom(oiFinal.getOiReturnedUomCd());
					
					//add ordered quantity
					if (oiFinal.getOiOrderedQuantity() != null) {
						oiFinalBean.setOrderedQuantity(oiFinal.getOiOrderedQuantity().toString());
					}
					oiFinalBean.setOrderedUom(oiFinal.getOiOrderedUomCd());
					
					Long landlingCd = oiFinal.getOiReturnQuantHandlingCd();
					if ((landlingCd != null) && (landlingCd == Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_CREATED)) {
						oiFinalBean.setCreditNote(true);	
					}
					else {
						oiFinalBean.setCreditNote(false);
					}
					
					
					//Prozess
					//Bestellmenge
					
					if (oiFinal.getOiOrderedQuantity() != null) {
						oiFinalBean.setQuantityActual(oiFinal.getOiOrderedQuantity().toString()
								+ " " + StringUtils.nvl(oiFinal.getOiOrderedUomCd()));
					}
					
					if (oiFinal.getOiRestrOrderedQuantity() != null) {
						oiFinalBean.setQuantityPlan(oiFinal.getOiRestrOrderedQuantity().toString() + " " + StringUtils.nvl(oiFinal.getOiRestrOrderedUomCd()));
					
						
						if ((StringUtils.nvl(oiFinalBean.getQuantityPlan()).equals(oiFinalBean.getQuantityActual()))) {
							oiFinalBean.setQuantityFlag(false);
						} 
						else {
							oiFinalBean.setQuantityFlag(true);
						}
						
					} 
					else {
						oiFinalBean.setQuantityFlag(true);
					}
					
					//Lieferart
					if (actDeliveryTypeTx != null) {
						oiFinalBean.setDeliveryTypeActual(actDeliveryTypeTx.getTxt());
					}
	
					if (oiFinal.getOiRestrDeliveryTypeCd() != null) {
			
						DeliveryTypeTxEntity planDeliveryTypeTx = getRefDataGenericsService().findTxEntity(DeliveryTypeTxEntity.class, locale, oiFinal.getOiRestrDeliveryTypeCd());
						if (actDeliveryTypeTx != null) {
							oiFinalBean.setDeliveryTypePlan(planDeliveryTypeTx.getTxt());
						}
			
						if ((StringUtils.nvl(oiFinalBean.getDeliveryTypeActual()).equals(oiFinalBean.getDeliveryTypePlan()))) {
							oiFinalBean.setDeliveryTypeFlag(false);
						} 
						else {
							oiFinalBean.setDeliveryTypeFlag(true);
						}
					} 
					else {
						oiFinalBean.setDeliveryTypeFlag(true);
					}
					
					//Weighing type
					if (oiFinal.getLpWeighSystemLpCd() != null) {
						WeighSystemLpTxEntity weighTx = getRefDataGenericsService().findTxEntity(WeighSystemLpTxEntity.class, locale, oiFinal.getLpWeighSystemLpCd());
						if (weighTx != null) {
							oiFinalBean.setWeighingType(weighTx.getTxt());
						}
					}
					
					//Vergutung
					oiFinalBean.setLc(oiFinal.getOiCurrencyCd());
					
					BigDecimal deliveredQuantity = oiFinal.getOiDeliveredQuantity();
					String deliveredUomCd = oiFinal.getOiDeliveredUomCd();
					
					if ((deliveredQuantity != null) && (deliveredUomCd != null)) {
						if (deliveredUomCd.equals(Constant.UOM_TONS)) {
							oiFinalBean.setNetWeight(deliveredQuantity.setScale(2,
									BigDecimal.ROUND_HALF_UP).toPlainString());
						}
						else {
							UomConverterService uomConverterService =
									getServiceLocator().getUomConverterService();
							UomBsEntity deliveredUom = getRefDataGenericsService().findBsEntity(UomBsEntity.class, 
									deliveredUomCd);
							UomBsEntity uomTons = getRefDataGenericsService().findBsEntity(UomBsEntity.class, 
									Constant.UOM_TONS);
							SapMaterialEntity sapMaterial = getBestellungService().getSapMaterialById(
									oiFinal.getOiSapMaterialId());
							
							try {
								UomConversionResult ucr = uomConverterService.convert(
										deliveredQuantity, deliveredUom, uomTons, sapMaterial,
										null, null, false);
								
								BigDecimal deliveredQuantityT = ucr.getQuantity();
								oiFinalBean.setNetWeight(deliveredQuantityT.toPlainString());
							}
							catch (LogonConversionException e)
							{
								Long sapMaterialId = (sapMaterial != null)
										? sapMaterial.getSapMaterialId() : null;
								
								LOG.warn("Could not convert delivered quantity in "
										+ deliveredUom.getUomCd() + " to tons (sapMaterialId = "
										+ sapMaterialId + "): " + e.getMessage());
							}
						}
					}
					
					if (orderFinal.getOTranspDistancePlanM() != null) {
						BigDecimal distanceKm = new BigDecimal(orderFinal.getOTranspDistancePlanM() / 1000);
						oiFinalBean.setDistance(distanceKm.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					}
					if (oiFinal.getOiRefundTrspCapaTkm() != null) {
						oiFinalBean.setQuantity(oiFinal.getOiRefundTrspCapaTkm().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					}
					if (oiFinal.getOiRefundNetWeightCurT() != null) {
						oiFinalBean.setNetWeightLC(oiFinal.getOiRefundNetWeightCurT().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					}
					if (oiFinal.getOiRefundDistanceCurKm() != null) {
						oiFinalBean.setDistanceLC(oiFinal.getOiRefundDistanceCurKm().setScale(2, BigDecimal.ROUND_HALF_UP).toString());	
					}
					if (oiFinal.getOiRefundTrspCapaCurTkm() != null) {
						oiFinalBean.setQuantityLC(oiFinal.getOiRefundTrspCapaCurTkm().setScale(2, BigDecimal.ROUND_HALF_UP).toString());	
					}
					
					if(oiFinal.getOiDispocardAvailalbe() != null){
						oiFinalBean.setBasedOnDispocard(oiFinal.getOiDispocardAvailalbe());
					}
					
					if(oiFinal.getLdsLoadedFlag() != null){
						oiFinalBean.setLoadedByLds(oiFinal.getLdsLoadedFlag());
					}
					
					orderItemFBeanList.add(oiFinalBean);
					noteDispatching.append(" " + StringUtils.nvl(oiFinal.getOiItemNoteDispatching()));
					noteRailwayDisp.append(" " + StringUtils.nvl(oiFinal.getOiItemNoteRailway()));
				}
				
				repBean.setOrderItemFBeanList(orderItemFBeanList);
				
				if (orderFinal.getONoPdaOrRailEventsFlag() != null) {
					repBean.setNoPdaDeliveryFlag(orderFinal.getONoPdaOrRailEventsFlag());
				}
				if (orderFinal.getOReschedulingFlag() != null) {
					repBean.setTruckReroutingFlag(orderFinal.getOReschedulingFlag());
				}
				if (orderFinal.getOShortTermOrderFlag() != null) {
					repBean.setShortTermOrderFlag(orderFinal.getOShortTermOrderFlag());
				}
				
				
				//reaction times
				repBean.setReactionTimeOrderMng(nvl(orderFinal.getBizReactTimeOrdMngMin()));
				repBean.setReactionTimePoolMng(nvl(orderFinal.getBizReactTimePoolMngMin()));
				repBean.setReactionTimeDispatcher(nvl(orderFinal.getBizReactTimeDispatcherMin()));
				repBean.setReactionTimeTruckDriver(nvl(orderFinal.getBizReactTimeTruckDrvMin()));
				repBean.setReactionDelivTimeWindow(nvl(orderFinal.getBizReactTimeNetWindowMin()));
				repBean.setLoadingTime(nvl(orderFinal.getBizLoadingTimeMin()));
				repBean.setActualDrivingTime(nvl(orderFinal.getActualDrivingTimeMin()));
				repBean.setUnloadingTime(nvl(orderFinal.getBizUnloadingMin()));
				repBean.setCycleTimeShipPoint(nvl(orderFinal.getBizCycleTimeAtSpMin()));
				repBean.setCycleTimeShipto(nvl(orderFinal.getBizCycleTimeAtShiptoMin()));
			
				
				//Service level
				repBean.setEndLoad(UserProvider.getTimezone().fmtAsDayMonthYearHourMinute(orderFinal.getBizEndLoadTs()));
				repBean.setDeliveryTo(UserProvider.getTimezone().fmtAsDayMonthYearHourMinute(orderFinal.getODeliveryToDat()));
				repBean.setEnterSt(UserProvider.getTimezone().fmtAsDayMonthYearHourMinute(orderFinal.getBizEnterStTs()));
				repBean.setOnTimeFlag(orderFinal.getOCalcLateDeliveryFlag());
				repBean.setRelevantServiceLevelFlag(orderFinal.getReportSvcLevelRelevantFlag());
				repBean.setNoteCrh(orderFinal.getReportNoteFromCrh());
				repBean.setNoteCarrier(orderFinal.getReportNoteFromCarrier());
				
				
				//Regionen
				repBean.setSalesDistrict(StringUtils.nvl(orderFinal.getWeSalesDistrict()));
				if (orderFinal.getWeSofficeCd() != null) {
					SofficeBsEntity soffice = getRefDataGenericsService().findBsEntity(SofficeBsEntity.class, orderFinal.getWeSofficeCd());
					if (soffice != null) {						
						repBean.setSalesOffice(soffice.getSofficeName());
					}
				}
				if (orderFinal.getWeSgroupCd() != null) {
					SgroupBsEntity sgroup = getRefDataGenericsService().findBsEntity(SgroupBsEntity.class, orderFinal.getWeSgroupCd());
					if (sgroup != null) {						
						repBean.setSalesGroup(sgroup.getSgroupName());
					}
				}
				
				repBean.setZone(StringUtils.nvl(orderFinal.getODeliveryTrspZoneCd()));
				repBean.setRegion(StringUtils.nvl(orderFinal.getWeRegion()));
			
				LangTxEntity langTx = getRefDataGenericsService().findLangTxEntity(locale, orderFinal.getWeLangCd());
				if (langTx != null) {
					repBean.setLanguage(StringUtils.nvl(langTx.getTxt()));
				}
			
				repBean.setNoteDisposition(StringUtils.nvl(orderFinal.getOCarrierDispatchingNote()));
				repBean.setNoteDriver(StringUtils.nvl(orderFinal.getOCarrierTruckDriverNote()));
				repBean.setNoteShipping(StringUtils.nvl(orderFinal.getOCarrierDeliveryNote()));
			
				helpForm.setReportingBean(repBean);
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

    private  String nvl(Long l) {
		// [[
		String result = null;
		Object[][] params = {{"l", l}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "nvl", params);
		}
		// ]]
		try {
	        if (l != null) {
	            return result = l.toString();
	        }
	        return result = "";
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
	
	
	private void initMngTab(OrderDetailForm helpForm, Locale locale) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initMngTab", params);
		}
		// ]]
		
		try {
			Authorization.authorize(Authorization.ORDER_DETAIL_ORDER_MANAGEMENT);
			OrderEntity order = helpForm.getOrder();
			ShiptoEntity shipTo = helpForm.getShipto();
			MngTabBean mngBean = new MngTabBean();
			
			//Steuerung
			mngBean.setReleaseSapFlag(order.getReleaseSapFlag());
			mngBean.setNoPdaOrRailEventsFlag(order.getNoPdaOrRailEventsFlag());
			if (helpForm.getAssignment() != null) { 
				AssignmentTypeTxEntity assType = getRefDataGenericsService().findTxEntity(AssignmentTypeTxEntity.class, locale, helpForm.getAssignment().getAssignmentType());
				mngBean.setOrderType(assType.getTxt()); 
			}
			
			DistributionDestTxEntity distDestTx = getRefDataGenericsService().findTxEntity(DistributionDestTxEntity.class, 
					locale, order.getDistributionDest().getDistributionDestCd());
			if (distDestTx != null) {
				mngBean.setDistributionDest(distDestTx.getTxt());
			}
			
			DeliveryTypeTxEntity delTypeTx = getRefDataGenericsService().findTxEntity(DeliveryTypeTxEntity.class, locale,
					order.getDeliveryType());
			if (delTypeTx != null) {
				mngBean.setDeliveryType(delTypeTx.getTxt());
			}
			
			if (order.getShippingpoint() != null) {
			
				if (order.getShippingpoint().getSapShippingCond() != null) {
					SapShippingCondTxEntity sapShipCondTx = getRefDataGenericsService().findTxEntity(SapShippingCondTxEntity.class, 
							locale, order.getShippingpoint().getSapShippingCond());
					if (sapShipCondTx != null) {
						mngBean.setShippingCond(sapShipCondTx.getTxt());
					}
				}
			
				if (order.getShippingpoint().getPlant() != null) {
					mngBean.setSapInventoryManagedFlag(order.getShippingpoint().getPlant().getSapInventoryManaged());
				}
			}
			
			mngBean.setSbbCustomerNumber(StringUtils.nvl(shipTo.getSbbCustomerNumber()));
			mngBean.setCustomerClassification(StringUtils.nvl(order.getSoldtoSapBp().getCustomerClassification()));
			
			SapBpSareaEntity sapBpSarea = helpForm.getSapBpSarea();
			
			if (sapBpSarea != null) {
				mngBean.setCustomerGroup(StringUtils.nvl(sapBpSarea.getCustomerGroup()));
				mngBean.setAbcClass(StringUtils.nvl(sapBpSarea.getAbcClass()));
				if (sapBpSarea.getCustomerOrderBlock() != null) {
					mngBean.setCustomerOrderBlockFlag(sapBpSarea.getCustomerOrderBlock());
				}
				if (sapBpSarea.getCustomerDeliveryBlock() != null) {
					mngBean.setCustomerDeliveryBlockFlag(sapBpSarea.getCustomerDeliveryBlock());
				}
			}
			
			if (order.getOrderCreationType() != null) {
				OrderCreationTypeTxEntity  crType = getRefDataGenericsService().findTxEntity(OrderCreationTypeTxEntity.class, locale, order.getOrderCreationType());
				if (crType != null)
				mngBean.setOrderCreationType(crType.getTxt());
			}
			
			mngBean.setOrderValidationCode(StringUtils.nvl(order.getOrderValidationCode()));
			
			
			Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
			

			BigDecimal nrOfOrdersInAssignment = getServiceLocator().getOrderDetailService().getNumberOfOrdersInAssignment(order.getOrderId());
			boolean isTourAssignment = nrOfOrdersInAssignment != null && nrOfOrdersInAssignment.intValue() > 1;
			boolean nonRoadDirect = !order.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_ROAD_DIRECT);
			boolean transmittedOnRealTruck = order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) && helpForm.getAssignment() != null && helpForm.getAssignment().getTruck()!= null && !LogonUtil.isPlanningTruck(helpForm.getAssignment().getTruck());
			boolean notAllowedOrderState = !orderStatusAllowNoPdaOrRailEventFlagEdit.contains(order.getOrderStatus().getOrderStatusCd()) || transmittedOnRealTruck;
			
			boolean disabledNoPdaOrRailEventsFlag = nonRoadDirect ||  notAllowedOrderState || isTourAssignment;

			mngBean.setDisabledNoPdaOrRailEventsFlag(disabledNoPdaOrRailEventsFlag);

			
			if (orderStatusCd == Constant.ORDER_STATUS_FINISHED) {	    
				mngBean.setDisabledFreeOfCharge(ENABLE_FREE_OF_CHARGE_DELIVERY);
		    }
		    else {			    	
		    	OrderService orderService = getOrderService();

		    	ShiptoSareaEntity shiptoSArea = orderService.fetchShiptoSarea(order.getShiptoSapBp(), order.getSarea());		    	
		    	mngBean.setDisabledFreeOfCharge(!shiptoSArea.getFreeDeliveryFlag());		    
		    }
		    		    
		    mngBean.setCustFreeOfChargeFlag(order.getFreeDeliveryFlag());
			mngBean.setSapSuppressMinChargeFlag(order.getSapSuppressMinChargeFlag());
			mngBean.setSentToSap(order.getSentSalesdocToSapTs() != null && order.getSentShipmentdocToSapTs() != null);
			
			//Lieferschein
			mngBean.setFaxNumber(StringUtils.nvl(order.getOrderConfFaxNumber()));
			mngBean.setEmailNumber(StringUtils.nvl(order.getOrderConfEmailAdresses()));
			mngBean.setSmsNumber(StringUtils.nvl(order.getOrderConfSmsNumber()));
			mngBean.setContactPersonSignature(StringUtils.nvl(order.getContactPersonSignatureTxt()));
		
			Set pNameSet = new HashSet();
			Set printTypeSet = new HashSet();
			//get all printers from all loading points
			for (Iterator it = helpForm.getOrder().getOrderItems().iterator(); it.hasNext();) {
				OrderItemEntity oi = (OrderItemEntity) it.next();
				if ( (oi.getLoad() != null) && (oi.getLoad().getLoadingpoint() != null) ) {
					if (order.getDelivnoteSinglePaperFlag() && (oi.getLoad().getLoadingpoint().getPrinter() != null)) {
						String printerName = oi.getLoad().getLoadingpoint().getPrinter().getPrinterName();
						if (!pNameSet.contains(printerName)) {
							pNameSet.add(printerName);
						}
					}	
				
					DelivNotePrintTypeTxEntity printType = getRefDataGenericsService().findTxEntity(DelivNotePrintTypeTxEntity.class, locale, oi.getLoad().getLoadingpoint().getDelivNotePrintType());
					if (!printTypeSet.contains(printType.getTxt())) {
						printTypeSet.add(printType.getTxt());
					}
				}
			}
			
			StringBuffer printers = new StringBuffer();
			for (Iterator it = pNameSet.iterator(); it.hasNext();) {
				printers.append((String) it.next());
				if (it.hasNext()) {
					printers.append("; ");
				}
			}
			mngBean.setPaperNumber(printers.toString());
			
			
			StringBuffer printTypes = new StringBuffer();
			for (Iterator it = printTypeSet.iterator(); it.hasNext();) {
				printTypes.append((String) it.next());
				if (it.hasNext()) {
					printTypes.append("; ");
				}
			}
			mngBean.setDelNotePrintType(printTypes.toString());
			
			mngBean.setBbEmailFlag(order.getOrderConfEmailFlag());
			mngBean.setBbFaxFlag(order.getOrderConfFaxFlag());
			mngBean.setBbSmsFlag(order.getOrderConfSmsFlag());
			mngBean.setLsEmailFlag(order.getDelivnoteEmailFlag());
			mngBean.setLsFaxFlag(order.getDelivnoteFaxFlag());
			mngBean.setLsPaperFlag(order.getDelivnoteSinglePaperFlag());
			
			

			
			
			//Bemerkungen
			mngBean.setNoteOrderMgmt(StringUtils.nvl(order.getNoteOrderMgmt()));
			mngBean.setNoteDispatching(StringUtils.nvl(order.getNoteDispatching()));
			mngBean.setNoteCustWeborder(StringUtils.nvl(order.getCustWeborderNote()));
			mngBean.setNoteTruckDriver(StringUtils.nvl(order.getNoteTruckDriver()));
			mngBean.setNoteDelivery(StringUtils.nvl(order.getNoteDelivery()));
			mngBean.setNoteRailwayDisp(StringUtils.nvl(order.getNoteRailwayDisp()));
			
			
			
			helpForm.setMngBean(mngBean);
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
	 * Takes form fields and fills order
	 * 
	 * @param helpForm
	 * @param locale
	 * @param request
	 */
	public void setupOrder(OrderDetailForm helpForm, Locale locale, HttpServletRequest request) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"locale", locale},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setupOrder", params);
		}
		// ]]
		try {
			
			OrderEntity order = helpForm.getOrder();
			
			//-------Main part-------//
			if (helpForm.getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_FORWARDING_AGENT)) {
				Authorization.authorize(Authorization.ORDER_DETAIL_MATERIAL_FORWARDING_AGENT);
				
				//carrier
				if (Constant.INVALID_OBJECT_ID == helpForm.getOrderBean().getCommCarrierId()) {
					order.setCommCarrier(null);
				}
				else {
					//gets carrier from the amp
					CarrierEntity carrier = (CarrierEntity)helpForm.getCommCarrierMap().get(helpForm.getOrderBean().getCommCarrierId()); 
					order.setCommCarrier(carrier);
				}
				
				//carrier
				if (Constant.INVALID_OBJECT_ID == helpForm.getOrderBean().getExecCarrierId()) {
					order.setExecCarrier(null);
				}
				else {
					//gets carrier from the amp
					if (order.getExecCarrier() == null 
							|| !order.getExecCarrier().getCarrierId().equals(helpForm.getOrderBean().getExecCarrierId())) {
						for (CarrierEntity carrier : helpForm.getExecCarrierList()) {
							if (carrier.getCarrierId().equals(helpForm.getOrderBean().getExecCarrierId())) {
								order.setExecCarrier(carrier);
								break;
							}
						}
					}
				}
			}
			if (helpForm.getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_W)) {
				Authorization.authorize(Authorization.ORDER_DETAIL_W);
				
				//pool
				if (Constant.INVALID_OBJECT_ID == helpForm.getOrderBean().getPoolId()) {
					order.setPool(null);
				}
				else {
					PoolEntity pool = getOrderDetailService().loadPool(helpForm.getOrderBean().getPoolId());
					order.setPool(pool);
				}
				
				// Update selected vehicle types
				updateSelectedTruckTypesInOrder(
						order,
						helpForm.getTruckTypeWrappers(),
						helpForm.getOrderBean().getSelectedVehicles());
				
				//shipping point
				setupShippingPoint(helpForm, request, order, helpForm.getOrderBean().getShPointNo());
			}	
			if (helpForm.getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_SPI)) {
				if (Constant.INVALID_OBJECT_ID_STR.equals(helpForm.getOrderBean().getSpiCd())) {
					order.setSpi(null);
				}
				else {
					order.setSpi(getRefDataGenericsService().findBsEntity(OrderSpiBsEntity.class, helpForm.getOrderBean().getSpiCd()));
				}
			}
			
			//-------Management tab-------//
			if (helpForm.getMngBean() != null) {
				if (helpForm.getAuthParamList().contains(OrderDetailForm.PARAM_ORDER_DETAIL_ORDER_MANAGEMENT)) {
					Authorization.authorize(Authorization.ORDER_DETAIL_ORDER_MANAGEMENT);			
					MngTabBean mng = helpForm.getMngBean();
					helpForm.getOrder().setReleaseSapFlag(mng.isReleaseSapFlag());
					helpForm.getOrder().setFreeDeliveryFlag(mng.isCustFreeOfChargeFlag());
					helpForm.getOrder().setSapSuppressMinChargeFlag(mng.isSapSuppressMinChargeFlag());
					helpForm.getOrder().setNoPdaOrRailEventsFlag(mng.isNoPdaOrRailEventsFlag());
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
	 * Changes the shipping point in the order.
	 * Calculates the duration/distance fields depending on the new shippingpoint, and
	 * saves it to the order.
	 * 
	 * @param helpForm
	 * @param order
	 * @param orderBean
	 */
	private void setupShippingPoint(OrderDetailForm helpForm, HttpServletRequest request, OrderEntity order,
			String shPointNo) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"request" , request},
							{"order", order},
							{"shPointNo", shPointNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setupShippingPoint", params);
		}
		// ]]
		try {
			if (Constant.INVALID_OBJECT_ID_STR.equals(shPointNo)) {
				return;
			}
			Map<String,ShippingpointEntity> shPointMap = helpForm.getShPointMap();
			ShippingpointEntity shPoint = shPointMap.get(shPointNo);
			if (order.getShippingpoint() == null || 
					!order.getShippingpoint().getShippingpointId().equals(shPoint.getShippingpointId())) {
				order.setShippingpoint(shPoint);
				OrderService orderService = getOrderService();
				order = orderService.calculateAndSaveDistanceAndDuration(order);
				helpForm.setOrder(order);
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
	 * Updates the list of truck types selected in an order
	 * @param order The order in which the list is to be updated
	 * @param truckTypes The total list of truck types that could be enabled,
	 * i.e., the whole list from which the user can select individual ones in the
	 * vehicle type dropdown in the order detail screen
	 * @param selectedTypes The list of type codes of those trucks that have been
	 * checked in the dropdown
	 */
	private void updateSelectedTruckTypesInOrder(
			OrderEntity order,
			List<TruckTypeWrapper> truckTypes, 
			String selectedTypes) 
	{
		// [[
		Object[][] params = {{"order", order},
							{"truckTypes", truckTypes},
							{"selectedTypes", selectedTypes}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateSelectedTruckTypesInOrder", params);
		}
		// ]]
		try {
			LOG.info("Updating truck types in order {}", new Object[] { order });
			
			/*
			 * We cannot directly set the new list newOrderTrucks in the order,
			 * i.e., it would be wrong to write
			 *   order.setOrderVehicleTypes(newOrderTrucks)
			 * We need to make the modifications on the original list, so that
			 * Hibernate knows to propagate them back to the DB.
			 */
			final List<TruckWagonTypeWrapper> twtwList = new LinkedList<TruckWagonTypeWrapper>(truckTypes);
			@SuppressWarnings("unchecked")
			final List<OrderTruckTypeEntity> orderTrucks = (List<OrderTruckTypeEntity>) order.getOrderVehicleTypes();
			final List<Long> truckCodesFinal = parseNumberList(selectedTypes);
			final List<Long> truckCodesCurrent = extractTruckTypeCodes(orderTrucks);
			
			LOG.debug("Truck types currently enabled in the order: {}", new Object[] {truckCodesCurrent});
			LOG.debug("Final truck types wanted in the order: {}", new Object[] {truckCodesFinal});
			
						// Add all those that must be in the final list but are not in the current one.
			final List<Long> truckCodesToAdd = subtractList(truckCodesFinal, truckCodesCurrent);
			LOG.debug("Truck types to add to order: {}", new Object[] {truckCodesToAdd});
			
			// Remove all those that are in the current list but must not be in the final one. 
			final List<Long> truckCodesToRemove = subtractList(truckCodesCurrent, truckCodesFinal);
			LOG.debug("Truck types to remove from order: {}", new Object[] {truckCodesToRemove});
			
			for (Iterator<OrderTruckTypeEntity> i = orderTrucks.iterator(); i.hasNext(); )
			{
				final OrderTruckTypeEntity truck = i.next();
				if (truckCodesToRemove.contains(truck.getTruckType().getTruckTypeCd())) {
					LOG.debug("Removing type with code {}", new Object[] {truck.getTruckType().getTruckTypeCd()});
					i.remove();
				}
			}
			
			final List<OrderTruckTypeEntity> trucksToAdd = OrderActionSupport.createOrderTruckTypes(order, truckCodesToAdd, twtwList);
			orderTrucks.addAll(trucksToAdd);
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
	 * Removes one list from another one, leaving the original list unchanged
	 * (as opposed to java.util.List's removeAll() method.
	 * @param minuend The list to remove elements from
	 * @param subtrahend The list of elements to remove
	 * @return The list obtained by removing all elements in <code>subtrahend</code> from <code>minuend</code>.
	 */
	final static <T> List<T> subtractList(final List<T> minuend, final List<T> subtrahend) 
	{
		final List<T> result = new LinkedList<T>(minuend);
		result.removeAll(subtrahend); // All that should be there in the end minus current ones
		return result;
	}

	/**
	 * @param trucks
	 * @return
	 */
	final private static List<Long> extractTruckTypeCodes(List<OrderTruckTypeEntity> trucks) 
	{
		final List<Long> codes = new LinkedList<Long>();
		for (OrderTruckTypeEntity truck : trucks) {
			codes.add(truck.getTruckType().getTruckTypeCd());
		}
		
		return codes;
	}

	/**
	 * Call to upload an image hold by the imgForm and attach it to the orderRepEnt order repair
	 * entity.
	 * In case an error happened, actionerror is added to the request.
	 * @param request
	 * @param orderRepEnt
	 * @param imgForm
	 */
	protected OrderRepAttEntity uploadImage(HttpServletRequest request, OrderRepEntity orderRepEnt,
			OrderDetailUploadAttachmentBean imgForm) {
		OrderRepAttEntity attachment = new OrderRepAttEntity();
		attachment.setOrderRep(orderRepEnt);
		FormFile file = imgForm.getFile();
		attachment.setAttachmentName(file.getFileName());
		try {					
			MimeTypeBsEntity dataMimeType = getServiceLocator().getUserAdminService().fetchMimeTypeByMimeTypeName(file.getContentType());
			attachment.setDataMimeType(dataMimeType);
			attachment.setData(file.getFileData());
		}
		catch (Exception e) {
			LOG.warn("Unsupported mime type: " + file.getContentType());
			return null;
		}
		attachment.setAttachmentName(file.getFileName());
		
		String type = imgForm.getType();
		if (Long.toString(Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER).equals(type)) {
			OrderRepAttTypeBsEntity attachmentType = getRefDataGenericsService().findBsEntity(OrderRepAttTypeBsEntity.class, Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER);
			attachment.setAttachmentType(attachmentType);
		}
		else if (Long.toString(Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION).equals(type)) {
			OrderRepAttTypeBsEntity attachmentType = getRefDataGenericsService().findBsEntity(OrderRepAttTypeBsEntity.class, Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION);
			attachment.setAttachmentType(attachmentType);
		}
		else if (Long.toString(Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE).equals(type)) {
			OrderRepAttTypeBsEntity attachmentType = getRefDataGenericsService().findBsEntity(OrderRepAttTypeBsEntity.class, Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE);
			attachment.setAttachmentType(attachmentType);				}
		else if (Long.toString(Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL).equals(type)) {
			Authorization.authorize(Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS);
			OrderRepAttTypeBsEntity attachmentType = getRefDataGenericsService().findBsEntity(OrderRepAttTypeBsEntity.class, Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL);
			attachment.setAttachmentType(attachmentType);
		}
		
		orderRepEnt.getOrderRepAttachments().add(attachment);
		return attachment;
	}
	
	/**
	 * Delete attachment of the orderRepEnt entity
	 * @param imgForm
	 * @param orderRepEnt
	 */
	protected OrderRepAttEntity deleteAttachment(OrderDetailUploadAttachmentBean imgForm,
			OrderRepEntity orderRepEnt) {
		OrderRepAttEntity att1 = null;
		Long id = imgForm.getOrderRepAttId();
		Iterator it = orderRepEnt.getOrderRepAttachments().iterator();
		while (it.hasNext()) {
			OrderRepAttEntity att = (OrderRepAttEntity) it.next();
			if (att.getAttachmentId().equals(id)){
				if (att.getAttachmentType().getAttachmentTypeCd().equals(Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL)) {
					Authorization.authorize(Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS);
				}
				att1 = att;
				it.remove();
				break;
			}
		}
		return att1;
	}
	
	/**
	 * @param helpForm
	 */
	private void initCustorderTab(OrderDetailForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initCustorderTab", params);
		}
		// ]]
		try {
			CustOrderTabBean custOrderTabBean = new CustOrderTabBean();
			
			CustorderService custorderService = getServiceLocator().getCustorderService();
			Locale locale = helpForm.getLocale();
			
			OrderEntity singleOrder = helpForm.getOrder();
			CustorderEntity custOrder = custorderService.findCustorderById(singleOrder.getCustorder().getCustorderId());
			custOrderTabBean.setCustorderEntity(custOrder);
			Long custorderId = custOrder.getCustorderId();
			
			custOrderTabBean.setCollDeliveryNoteMap(getCollDelNoteMap(custorderId, locale));
			List<Long> custorderMatIds = new ArrayList<Long>();
			custorderMatIds.add(custorderId);
			
			Map<Long, CustordItemSumQtyExtEntity> custorderItemSumQtyMap = custorderService.fetchQuantitiesPerMaterialInCustomerOrder(custorderId, locale);
			Map<Long, CustordItemSumQtyExtEntity> realMatOnlyMap = extractRealMaterial(custorderItemSumQtyMap);
			custOrderTabBean.setMaterialOnlyMap(realMatOnlyMap);
			
			List<CustorderItemEntity> coitems = custOrderTabBean.getCustorderEntity().getCustorderItems();
			Map<String, String> coitemMap = new HashMap<String, String>();
			for (CustorderItemEntity coitem : coitems) {
				Long sapMatId = coitem.getSapMaterial().getSapMaterialId();
				coitemMap.put(sapMatId.toString(), coitem.getOrderedQuantity().toString());
				LOG.info("CustorderItemEntities: coitemId["+coitem.getCustorderItemId()+"] sapMatId["+sapMatId+"]");
			}
			custOrderTabBean.setCustorderItemsQtyBySapMatIdMap(coitemMap);
			
			custOrderTabBean.setCustorderNumber(StringUtils.nvl(custOrder.getCustorderNumber().toString()));
			CustorderStatusTxEntity custorderStatus = getRefDataGenericsService().findTxEntity(CustorderStatusTxEntity.class, locale, custOrder.getCustorderStatus().getCustorderStatusCd());
			custOrderTabBean.setCustorderStatus(StringUtils.nvl(custorderStatus.getTxt()));
			
			Date custorderDeliveryFromDat = custOrder.getDeliveryFromDat();
			custOrderTabBean.setDeliveryFrom(
					helpForm.getShiptoTZ().fmtAsDayMonthYearHourMinuteWithOffset(
							custorderDeliveryFromDat, getUserTimezone()))
			;
			Date custorderDeliveryToDat = custOrder.getDeliveryToDat();
			custOrderTabBean.setDeliveryTo(
					helpForm.getShiptoTZ().fmtAsDayMonthYearHourMinuteWithOffset(
							custorderDeliveryToDat, getUserTimezone()));
			
			// Since the exact qty flags in all CustorderItems are in sync, only the first one has to be checked
			// There is always at least one item belonging to a CustOrder
			CustorderItemEntity firstCoItem = custOrder.getCustorderItems().get(0);
			custOrderTabBean.setExactCustOrderQty(firstCoItem.getExactCustorderQtyFlag() != null ? firstCoItem.getExactCustorderQtyFlag():Boolean.FALSE);
			custOrderTabBean.setExactSingleOrderQty(firstCoItem.getExactSdorderQtyFlag() != null ? firstCoItem.getExactSdorderQtyFlag():Boolean.FALSE);
			
			// Setting uom for qty sums
			List<OrderEntity> orders = getServiceLocator().fetchCustorderService().getOrdersByCustorder(custOrder);
			for (OrderEntity order : orders){
				CustOrderTabOrderBean custOrderTabOrderBean = new CustOrderTabOrderBean();
				custOrderTabOrderBean.setOrderId(StringUtils.nvl(order.getOrderId().toString()));
				
				Date orderActualDeliveryDat = order.getActualDeliveryDat();
				custOrderTabOrderBean.setActualDeliveryDate(
						helpForm.getShiptoTZ().fmtAsDayMonthYear(orderActualDeliveryDat));
				custOrderTabOrderBean.setActualDeliveryTime(
						helpForm.getShiptoTZ().fmtAsHourMinuteWithOffset(
								orderActualDeliveryDat, getUserTimezone()));

				Date orderDeliveryFromDat = order.getDeliveryFromDat();
				custOrderTabOrderBean.setDeliveryFromDate(
						helpForm.getShiptoTZ().fmtAsDayMonthYear(orderDeliveryFromDat));
				custOrderTabOrderBean.setDeliveryFromTime(
						helpForm.getShiptoTZ().fmtAsHourMinuteWithOffset(
								orderDeliveryFromDat, getUserTimezone()));
			
				Date orderDeliveryToDat = order.getDeliveryToDat();
				custOrderTabOrderBean.setDeliveryToDate(
						helpForm.getShiptoTZ().fmtAsDayMonthYear(orderDeliveryToDat));
				custOrderTabOrderBean.setDeliveryToTime(
						helpForm.getShiptoTZ().fmtAsHourMinuteWithOffset(
								orderDeliveryToDat, getUserTimezone()));
				
				
				Long statusCd = order.getOrderStatus().getOrderStatusCd();
				custOrderTabOrderBean.setOrderNumber(StringUtils.nvl(order.getOrderNumber().toString()));
				OrderStatusTxEntity orderStatusTx = getRefDataGenericsService().findTxEntity(OrderStatusTxEntity.class, locale, statusCd);
				custOrderTabOrderBean.setOrderStatus(StringUtils.nvl(orderStatusTx.getTxt()));
			
				SimpleVehicleEntity deliveredSimpleVehicle = order.getDeliveredSimpleVehicle();
				if (deliveredSimpleVehicle != null && deliveredSimpleVehicle.getVehicleName() != null) {
					custOrderTabOrderBean.setVehicleName(deliveredSimpleVehicle.getVehicleName());
				}
				
				LinkedList<CustOrderTabOrderItemBean> matList = new LinkedList<CustOrderTabOrderItemBean>();
				List<OrderItemEntity> orderItems = OrderUtils.sortOrderItems(order.getOrderItems());
				Boolean isServiceMatOnly = Boolean.TRUE;
				for(OrderItemEntity item : orderItems) {
					CustOrderTabOrderItemBean custOrderTabOrderItemBean = new CustOrderTabOrderItemBean();
					custOrderTabOrderItemBean.setServiceMaterial(LogonPackageHandlingUtil.isService(item));
					SapMaterialDescriptionEntity desc = getOrderDetailService().loadMaterialDesc(item.getSapMaterial().getSapMaterialId(), locale.getLanguage());
					custOrderTabOrderItemBean.setMaterialDesc(StringUtils.nvl(desc.getTxt()));
					
					if (item.getOrderedQuantity() != null) {
						custOrderTabOrderItemBean.setOrderedQuantity(StringUtils.nvl(item.getOrderedQuantity().toString()));
						UomTxEntity orderedUom = getRefDataGenericsService().findTxEntity(UomTxEntity.class, locale, item.getOrderedUom().getUomCd());
						custOrderTabOrderItemBean.setOrderedUom(StringUtils.nvl(orderedUom.getTxt()));
					}
					if (item.getLoadedQuantity() != null) {
						custOrderTabOrderItemBean.setLoadedQuantity(StringUtils.nvl(item.getLoadedQuantity().toString()));
						UomTxEntity loadedUom = getRefDataGenericsService().findTxEntity(UomTxEntity.class, locale, item.getLoadedUom().getUomCd());
						custOrderTabOrderItemBean.setLoadedUom(StringUtils.nvl(loadedUom.getTxt()));
					}
					if (item.getLoad() != null && item.getLoad().getTruck() != null && item.getLoad().getTruck().getTruckPlateNo() != null) {
						custOrderTabOrderItemBean.setTruckPlateNo(StringUtils.nvl(item.getLoad().getTruck().getTruckPlateNo()));
					}
						
					matList.add(custOrderTabOrderItemBean);
					if(!custOrderTabOrderItemBean.isServiceMaterial()) {
						isServiceMatOnly = Boolean.FALSE;
					}
					
				}
				
				custOrderTabOrderBean.setMaterials(matList);
				custOrderTabOrderBean.setServiceMaterialOnlyOrder(isServiceMatOnly);
				custOrderTabBean.getOrders().add(custOrderTabOrderBean);
				
			}
			
			// TODO (pascal Nov 30, 2012): LOGON-6066: Coordinate, use same code as in DeliverySettingDTO.java, used in OrderTaking
			// If there is a fuzzy uom involved in the Custorder, the exactCustorderQtyFlag cannot be changed
			boolean exactCustorderQtyFlagEditable = this.isExactCustorderQtyFlagEditable(custOrder);
			custOrderTabBean.setExactCustOrderQtyEditable(exactCustorderQtyFlagEditable);
			// If there is a Bag involved in the Custorder, the exactSdorderQtyFlag and the exactCustorderQtyFlag cannot be changed.
			boolean exactSdorderQtyFlagEditable = true;
			if (exactCustorderQtyFlagEditable) {
				exactSdorderQtyFlagEditable = this.isExactSdorderQtyFlagEditable(custOrder);
			}
			else {
				exactSdorderQtyFlagEditable = false;
			}
			custOrderTabBean.setExactSingleOrderQtyEditable(exactSdorderQtyFlagEditable);
			
			helpForm.setCustOrderTabBean(custOrderTabBean);
			
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
	 * Editability of the exactCustomerOrderQtyFlag depends on the packaging type.
	 * 
	 * @return
	 */
	private boolean isExactCustorderQtyFlagEditable(CustorderEntity co) {
		List<CustorderItemEntity> coItems = co.getCustorderItems();
		Long deliveryTypeCd = co.getDeliveryType().getDeliveryTypeCd();
		
		if (deliveryTypeCd != null && (LogonUtil.isSimpleVehicleOrder(deliveryTypeCd) || LogonUtil.isServiceOnlyOrder(deliveryTypeCd))) {
			return false;
		}
		for (CustorderItemEntity coi : coItems) {
			UomBsEntity uom = coi.getOrderedUom();
	    	if (LogonPackageHandlingUtil.isBag(coi.getSapMaterial())) {
				return false;
			}
			else if (uom != null && uom.getFuzzyQuantityFlag() != null && uom.getFuzzyQuantityFlag().booleanValue()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Editability of the ExactSdorderQtyFlag depends on the packaging type.
	 * @return
	 */
	public boolean isExactSdorderQtyFlagEditable(CustorderEntity co) {
		List<CustorderItemEntity> coItems = co.getCustorderItems();
		Long deliveryTypeCd = co.getDeliveryType().getDeliveryTypeCd();
		
		if (deliveryTypeCd != null && (LogonUtil.isSimpleVehicleOrder(deliveryTypeCd) || LogonUtil.isServiceOnlyOrder(deliveryTypeCd))) {
			return false;
		}
		for (CustorderItemEntity coi : coItems) {
	    	if (LogonPackageHandlingUtil.isBag(coi.getSapMaterial())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param custorderId
	 * @return
	 */
	private Map<Long, String> getCollDelNoteMap(Long custorderId, Locale locale) {
		OrderDetailService orderDetailService = getOrderDetailService();
		List noteList = orderDetailService.findCollectiveDeliveryNotesByCustomerOrderIdOrderedByCreationTimeStamp(custorderId);
		Map<Long, String> deliveryNoteMap = new LinkedHashMap<Long, String>();
		
		for (Object object : noteList) {
			CustorderOutputdocEntity note = (CustorderOutputdocEntity) object;
			if (note.getPdfOutputdoc() != null) {
				String deliveryNoteInfo = getUserTimezone().fmtAsDayMonthYearHourMinute(note.getCtlCreTs());
				
				CustorderStatusTxEntity custOrderStatus = getRefDataGenericsService().findTxEntity(CustorderStatusTxEntity.class, locale,
						note.getGeneratedInCustordStat());
				if (custOrderStatus != null) {
					deliveryNoteInfo = deliveryNoteInfo + ", " + custOrderStatus.getTxt();
				}
				
				deliveryNoteMap.put(note.getPdfOutputdoc().getOutputdocId(), deliveryNoteInfo);
			}
		}
		
		return deliveryNoteMap;
	}
	
	/**
	 * Returns a map but without services.
	 * @param custorderItemSumQtyMap
	 * @return
	 */
	private Map<Long, CustordItemSumQtyExtEntity> extractRealMaterial(
			Map<Long, CustordItemSumQtyExtEntity> custorderItemSumQtyMap) {
		// [[
		Map<Long,CustordItemSumQtyExtEntity> result = null;
		Object[][] params = {{"custorderItemSumQtyMap", custorderItemSumQtyMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "extractRealMaterial", params);
		}
		// ]]
		try {
			Map<Long, CustordItemSumQtyExtEntity> matOnlyMap =
					new LinkedHashMap<Long, CustordItemSumQtyExtEntity>();
			
			MaterialEnhancementsService materialEnhancementsService =
					getServiceLocator().getMaterialEnhancementsService();
			
			for (Entry<Long, CustordItemSumQtyExtEntity> entry :
					custorderItemSumQtyMap.entrySet()) {
				
				Long sapMaterialId = entry.getKey();
				CustordItemSumQtyExtEntity mat = entry.getValue();
				
				SapMaterialEntity sapMat = materialEnhancementsService.loadSapMaterial(
						sapMaterialId);
				
				if (!LogonPackageHandlingUtil.isService(sapMat)) {
					matOnlyMap.put(sapMaterialId, mat);
				}
			}
			
			return result = matOnlyMap;
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
	 * @param helpForm 
	 * @param delivDateTo
	 * @param custorder
	 */
	public boolean adjustCustorderDeliveryWindowIfNeeded(HttpServletRequest request, OrderDetailForm helpForm) {
		// [[
		boolean result = false;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "adjustCustorderDeliveryWindowIfNeeded", params);
		}
		// ]]
		try {
			OrderEntity order = helpForm.getOrder();
			OrderDetailBean orderBean = helpForm.getOrderBean();
			CustorderEntity custorder = order.getCustorder();
			TimeZone shiptoTZ = helpForm.getShiptoTZ();
			Date delivDateTo = shiptoTZ.parseDayMonthYearHourMinute(
					orderBean.getDelivDateTo(),
					orderBean.getDelivTimeTo());
			Date customerOrderDeliveryWindowTo = custorder.getDeliveryToDat();
			
			Date delivDateFrom = shiptoTZ.parseDayMonthYearHourMinute(
					orderBean.getDelivDateFrom(),
					orderBean.getDelivTimeFrom());
			Date customerOrderDeliveryWindowFrom = custorder.getDeliveryFromDat();
			
			ServiceLocator locator = ServiceLocatorProvider.getServiceLocator();
		    OrderService orderService = locator.getOrderService();
			OrderDeliveryMinMaxDateDTO orderDeliveryTimeIntervalEntity = orderService.fetchOrderDeliveryTimeIntervalEntity(
					custorder.getCustorderId(), order.getOrderId());
			Date maxOrderDelivToDate;
			Date minOrderDelivFromDate;
			if (orderDeliveryTimeIntervalEntity != null) {
				maxOrderDelivToDate = orderDeliveryTimeIntervalEntity.getDeliveryToDat();
				minOrderDelivFromDate = orderDeliveryTimeIntervalEntity.getDeliveryFromDat();

				if (maxOrderDelivToDate.compareTo(delivDateTo) < 0) {
					maxOrderDelivToDate = delivDateTo;
				}

				if (minOrderDelivFromDate.compareTo(delivDateFrom) > 0) {
					minOrderDelivFromDate = delivDateFrom;
				}

			}
			else {
				maxOrderDelivToDate = delivDateTo;
				minOrderDelivFromDate = delivDateFrom;
			}

			if (customerOrderDeliveryWindowTo.compareTo(maxOrderDelivToDate) != 0) {
				custorder.setDeliveryToDat(maxOrderDelivToDate);
				result = true;
			}
			if (customerOrderDeliveryWindowFrom.compareTo(minOrderDelivFromDate) != 0) {
				custorder.setDeliveryFromDat(minOrderDelivFromDate);
				result = true;
			}
		}
		catch (Exception e) {
			LOG.error("Error while adjusting delivery window. ", e);
			result = false;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
		
		return result;
	}

}


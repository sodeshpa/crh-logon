/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.bestellung;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.services.utils.DateTimeAwareCustomerOrderWrapper;
import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderTakingEditorDto;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TrspZonePolygonEntity;
import com.crh.logon.persistence.entities.refdata.DailyOrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.ShiptoSoldtoLnfDataEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.utils.time.DateTimeParser;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.ConfigBeanProvider;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_bestellung_BestellungForm"
 */
@AdnTrace
public class BestellungForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BestellungForm.class);
	
	public static final String SESSION_KEY = "order_bestellung_BestellungForm";

	/**
	 * Displayed in the "List Price" column when rate is not known. If the customer wants, we can later
	 * change this text or move it into LitDict.
	 */
	public static final String NO_LIST_PRICE = "n.a.";
	/**
	 * Displayed in the "Open Quantity" column when it is not known.
	 */
	public static final String NO_OPEN_QUANTITY = "n.a.";
	public static final String SLASH = "/";

	// input from the user (BestellungShipTo)
	private String searchtext;
	private String shiptoSapId;
	private String soldtoSapId;
	
	private String tab;
	private String referrer;
	
	private String open1From; 
	private String open1To; 
	private String open2From; 
	private String open2To;
	
	private long selectedContactPersonId;
	private Long dailyOrderStatusCd;
	
	private ShiptoEntity shipTo;
	private SapBpEntity soldTo;
	
	private Locale locale;
	
	private Map<Object, List<ShiptoSalesAreaError>> invalidMap;
    private TimeZone shipToTZ;

	private List<CustorderEntity> custorders;
	private AddToCustorderModeData addToCustorderMode;
	
	private Long unloadingTime;

	private List<TimeZone> timeZoneList;
	private TimeZone lnfSelectedTimeZone;

	private String formAction;

	private boolean withActiveCemContracts;
	private boolean withActiveAggContracts;
	
	/**
	 * Default constructor.
	 */
	public BestellungForm() {
		init();
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the ordermgr.web form.
	 */
	private void init() {
		shiptoSapId = "";
		searchtext = "";
		shipTo = null;
		soldTo = null;
		siloMaterialDescriptionMap = new HashMap<Long, SapMaterialDescEntity>();
		materialPackageTxMap = new HashMap<String, MaterialPackageTxEntity>();
		referrer = "";
		tab = "";
		invalidMap = new HashMap<Object, List<ShiptoSalesAreaError>>();
		custorders = new ArrayList<CustorderEntity>();
		addToCustorderMode = new AddToCustorderModeData();
		allowFutureContracts = Boolean.FALSE;
		withActiveCemContracts = false;
		withActiveAggContracts = false;

		formAction = null;
	}

	/**
	 * Clears all the input fields on the ordermgr.web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the ordermgr.web form.
	 */
	public void clear() {
		init();
	}

	/**
	 * Override struts method.
	 * Note: this is not associated with or invoked by the reset button in the
	 * ordermgr.web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		referrer = "";
		if (orderCommon != null) {
			orderCommon.setDelivnoteSinglePaperFlag(false);
			orderCommon.setDelivnoteFaxFlag(false);
			orderCommon.setDelivnoteEmailFlag(false);
			orderCommon.setOrderConfFaxFlag(false);
			orderCommon.setOrderConfSmsFlag(false);
			orderCommon.setOrderConfEmailFlag(false);
			orderCommon.setLoadConfEmailFlag(false);
			orderCommon.setLoadConfFaxFlag(false);
			orderCommon.setLoadConfSmsFlag(false);
		}
		if (editorList != null && shipTo != null) {
			for (OrderTakingEditorDto editor : editorList) {
				OrderEntity order = editor.getOrder();

				order.setReleaseSapFlag(false);
				order.setFreeDeliveryFlag(false);
				order.setOnHoldOrderAndLockedFlag(false);

				// --- The order of the reset calls is important! Carrier MUST be set first ---
				editor.setAutoCarrierAssignmentFlag(false);
				editor.setAutoPoolAssignmentFlag(false);

				editor.setExactCustorderQtyFlag(false);
				editor.setExactQtyPerSdoFlag(false);

				editor.setServiceOnlyOrderFlag(false);
			}
		}
		openOrdersForCustorder = null;
		closedOrdersForCustorder = null;

		withActiveCemContracts = false;
		withActiveAggContracts = false;

		formAction = null;
	}

	/**
	 * Copy values from helpForm.orderCommon to each order.
	 * 
	 */
	public void applyOrderCommonSettings() {
		if (editorList == null) {
			return;
		}
		int latestIndex = editorList.size() - 1;
		OrderTakingEditorDto newestEditor = editorList.get(latestIndex);
		/**
		 * Do not copy the edited flag values of the selected ship to (represented by orderCommon) 
		 * into the new order part's editor if:
		 * - it is a bimodal rail order part AND
		 * - the ship to is different from the selected one!
		 */
		Long deliveryTypeCode = newestEditor.getDelTypeCd();
		if (LogonUtil.isBimodalRailPartOrder(deliveryTypeCode) && hasDifferentShipto(newestEditor)) {
			return;
		}

		Boolean deliveryNoteSinglePaperFlag = orderCommon.getDelivnoteSinglePaperFlag();
		Boolean deliveryNoteFaxFlag = orderCommon.getDelivnoteFaxFlag();
		Boolean deliveryNoteEmailFlag = orderCommon.getDelivnoteEmailFlag();
		Boolean orderConfirmationFaxFlag = orderCommon.getOrderConfFaxFlag();
		Boolean orderConfirmationSmsFlag = orderCommon.getOrderConfSmsFlag();
		Boolean orderConfirmationEmailFlag = orderCommon.getOrderConfEmailFlag();
		Boolean loadConfEmailFlag = orderCommon.getLoadConfEmailFlag();
		Boolean loadConfFaxFlag = orderCommon.getLoadConfFaxFlag();
		Boolean loadConfSmsFlag = orderCommon.getLoadConfSmsFlag();
		
		OrderEntity order = newestEditor.getOrder();
		order.setDelivnoteFaxFlag(deliveryNoteFaxFlag);
		order.setDelivnoteEmailFlag(deliveryNoteEmailFlag);
		order.setDelivnoteSinglePaperFlag(deliveryNoteSinglePaperFlag);

		order.setOrderConfFaxFlag(orderConfirmationFaxFlag);
		order.setOrderConfEmailFlag(orderConfirmationEmailFlag);
		order.setOrderConfSmsFlag(orderConfirmationSmsFlag);
		
		order.setLoadConfEmailFlag(loadConfEmailFlag);
		order.setLoadConfFaxFlag(loadConfFaxFlag);
		order.setLoadConfSmsFlag(loadConfSmsFlag);
	}

	/**
	 * Checks whether the selected ship to on CC order taking is not the same
	 * as the ship to of the current editor (~new order)
	 * @param editor
	 * @return
	 */
	private boolean hasDifferentShipto(OrderTakingEditorDto editor) {
		OrderEntity newOrder = editor.getOrder();
		SapBpEntity newOrdersShipTo = newOrder.getShiptoSapBp();
		Long newOrdersShipToSapId = newOrdersShipTo.getSapBpId();
		return !this.shiptoSapId.equals(newOrdersShipToSapId);
	}

	public void initShipto() {
		if (shipTo != null) {
			Long open1From2 = shipTo.getOpen1From();
			open1From = DateTimeUtils.formatMinutesSinceMidnight(open1From2);
			Long open1To2 = shipTo.getOpen1To();
			open1To = DateTimeUtils.formatMinutesSinceMidnight(open1To2);
			Long open2From2 = shipTo.getOpen2From();
			open2From = DateTimeUtils.formatMinutesSinceMidnight(open2From2);
			Long open2To2 = shipTo.getOpen2To();
			open2To = DateTimeUtils.formatMinutesSinceMidnight(open2To2);

			OrderTakingEditorDto selectedEditor = getSelectedEditor();

			initEditorWithDateAndTime(selectedEditor);
		}
	}

	/**
	 * Override struts method.
	 * Perform form-specific validation of input parameters.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);

		/* no need to validate if 'delete' button was pressed */
		if (request.getParameter("delete") != null) {
			return null;
		}
		if (errors == null) {
			errors = new ActionErrors();
		}
		String action = request.getParameter("action");

		if ("save".equals(action)) {
			if (shipTo != null) {
				try {
					Date von1 = DateTimeUtils.parseHoursMinutes(open1From);
					Date bis1 = DateTimeUtils.parseHoursMinutes(open1To);
					Date von2 = DateTimeUtils.parseHoursMinutes(open2From);
					Date bis2 = DateTimeUtils.parseHoursMinutes(open2To);

					if (((von1 == null) && (bis1 != null)) || ((von1 != null) && (bis1 == null))
							|| ((von2 == null) && (bis2 != null))
							|| ((von2 != null) && (bis2 == null))) {
							errors.add(ActionMessages.GLOBAL_MESSAGE,
								new ActionMessage("dispokarte.errors.opennotcomplete"));
					}
					else if ((von1 == null) && (von2 != null)) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"dispokarte.errors.openwrongorder"));
					}
					else if ((von1 != null) && (bis1 != null) && bis1.before(von1)) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"dispokarte.errors.openwrongorder"));
					}
					else if ((von2 != null) && (bis2 != null) && bis2.before(von1)) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"dispokarte.errors.openwrongorder"));
					}
					else if ((von2 != null) && von2.before(bis1)) {
						errors.add(ActionMessages.GLOBAL_MESSAGE,
								new ActionMessage("dispokarte.errors.openwrongorder"));
					}

					shipTo.setOpen1From(DateTimeUtils.getMinAfterMidnight(von1));
					shipTo.setOpen1To(DateTimeUtils.getMinAfterMidnight(bis1));
					shipTo.setOpen2From(DateTimeUtils.getMinAfterMidnight(von2));
					shipTo.setOpen2To(DateTimeUtils.getMinAfterMidnight(bis2));

					if (!LogonUtil.validSmsNumber(shipTo.getOrderConfSmsNumber())) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"dispokarte.errors.invalidSMSnumber"));
					}
					if (!LogonUtil.validFaxNumber(shipTo.getOrderConfFaxNumber())) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"dispokarte.errors.invalidFaxNumber"));
					}
					if (!LogonUtil.validSmsNumber(shipTo.getOrderConfEmailAddresses())) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"dispokarte.errors.invalidEmailAddress"));
					}
				}
				catch (ParseException e) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"dispokarte.errors.openbadformat"));
				}
			}
		}

		return errors;
	}

	public String getSearchtext() {
		return searchtext;
	}

	public void setSearchtext(String searchtext) {
		this.searchtext = searchtext;
	}
	
	public boolean shipToInitialized(){
		return shipTo != null;
	}

	public ShiptoEntity getShipTo() {
		if (shipTo == null){
			// never return null, there can be problems when user hits reload
			return new ShiptoEntity();
		}
		return shipTo;
	}

	public void setShipTo(ShiptoEntity shipTo, TimeZone shipToTZ) {
		this.shipTo = shipTo;
        this.shipToTZ = shipToTZ;
	}


	public SapBpEntity getSoldTo() {
		return soldTo;
	}

	public void setSoldTo(SapBpEntity soldTo) {
		this.soldTo = soldTo;
	}

	/**
	 * @return the currently selected {@code OrderTakingEditorDto} of the editorList. <b>Currently always the first!</b> 
	 */
	public OrderTakingEditorDto getSelectedEditor() {
		return editorList.get(0);
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getOpen1From() {
		return open1From;
	}

	public void setOpen1From(String newValue) {
		this.open1From = parseAndFormatOpeningTime(newValue, open1From);
	}

	public String getOpen1To() {
		return open1To;
	}

	public void setOpen1To(String newValue) {
		this.open1To = parseAndFormatOpeningTime(newValue, open1To);
	}

	public String getOpen2From() {
		return open2From;
	}

	public void setOpen2From(String newValue) {
		this.open2From = parseAndFormatOpeningTime(newValue, open2From);
    }

	public String getOpen2To() {
		return open2To;
	}

	public void setOpen2To(String newValue) {
		this.open2To = parseAndFormatOpeningTime(newValue, open2To);
	}

    private String parseAndFormatOpeningTime(String newValue, String currentValue) {
        try {
            Date d = DateTimeParser.parseHourMinute(newValue);
            return DateTimeFormat.asHourMinute(d);
        }
        catch (ParseException exc) {
            LOG.error(String.format("Invalid: %s", newValue), exc);
            return currentValue;
        }
    }
	
	// --- Properties which apply for the whole order screen ---
	private DummyFields dummyFields;
	private CompletedOrdersBean completedOrdersBean; // TODO eliminate if not needed
	private List<DateTimeAwareOrderWrapper> orderList; // Lists of open Single-Delivery Orders
	private List<DateTimeAwareOrderWrapper> completedOrderList; // List of closed Single-Delivery Orders
	private List<DateTimeAwareCustomerOrderWrapper> openCustorderList; // List of open Customer Orders
	private List<DateTimeAwareCustomerOrderWrapper> closedCustorderList; // List of finished (closed) Customer Orders
	private List<DateTimeAwareOrderWrapper> openOrdersForCustorder; // Data for currently expanded Cust. Order
	private List<DateTimeAwareOrderWrapper> closedOrdersForCustorder; // Data for currently expanded Cust. Order
	private List<DeliveryTypeTxEntity> deliveryTypeList;
	private List<DailyOrderStatusTxEntity> dailyOrderStatusList;
	private List<TrspZonePolygonEntity> polygonList;
	private List<ShiptoSoldtoLnfDataEntity> lnfList;
	private Map<String, String> json; // JSON data for this Ship To (independent from the selected product)
	private Map<Long, SapMaterialDescEntity> siloMaterialDescriptionMap;
	private Map<String, MaterialPackageTxEntity> materialPackageTxMap;
	private OrderEntity orderCommon; // TODO move to the editor DTO
	private Boolean delivnoteCollPaperFlag;
	private Boolean allowFutureContracts;
	
	private List<PoolTxEntity> poolTruckList;
	private List<PoolTxEntity> poolRailList;
	
	/** In the business parter entity we only have the country code. 
	 * For the first lookup in google maps we need the actual country name 
	 * to be able to have correct coordinates especially for russian and azeri adresses.  */
	private String countryForShipto;
	
	private Long bimodalShiptoSareaRestrId;
	private String bimodalSoldtoSapId;
	
	// --- For multiple orders and multiple order items
	private List<OrderTakingEditorDto> editorList;
	
	private DeliverySettingShiptoMap shiptoMap;
	
	public List<OrderTakingEditorDto> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<OrderTakingEditorDto> orderEditorList) {
		this.editorList = orderEditorList;
	}

	public DummyFields getDummyFields() {
		return dummyFields;
	}

	public void setDummyFields(DummyFields tmpFields) {
		this.dummyFields = tmpFields;
	}

	public List<DateTimeAwareOrderWrapper> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<DateTimeAwareOrderWrapper> orderList) {
		this.orderList = orderList;
	}

	public List<DeliveryTypeTxEntity> getDeliveryTypeList() {
		return deliveryTypeList;
	}

	public void setDeliveryTypeList(List<DeliveryTypeTxEntity> deliveryTypeList) {
		this.deliveryTypeList = deliveryTypeList;
	}

	public Map<String, String> getJson() {
		return json;
	}

	public void setJson(Map<String, String> json) {
		this.json = json;
	}

	public void initEditorWithDateAndTime(OrderTakingEditorDto editor) {
		
		// --- Setup of Delivery To / From time fields ---
		Long open1FromNotNull = shipTo.getOpen1From() != null ? shipTo.getOpen1From() : 300;
		Long open2ToNotNull = shipTo.getOpen2To() != null ? shipTo.getOpen2To() : 1380;

		editor.setDeliveryFromTime(DateTimeUtils.formatMinutesSinceMidnight(open1FromNotNull));
		editor.setDeliveryToTime(DateTimeUtils.formatMinutesSinceMidnight(open2ToNotNull));
		
		// Initial value for the delivery times
		String fmtNextWeekDay = getShipToTimeZone().fmtAsDayMonthYear(
				getShipToTimeZone().nextWorkingDay());
		editor.setDeliveryFromDatStr(fmtNextWeekDay);
		editor.setDeliveryToDatStr(fmtNextWeekDay);
		
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public OrderEntity getOrderCommon() {
		return orderCommon;
	}

	public void setOrderCommon(OrderEntity order) {
		this.orderCommon = order;
	}

	public CompletedOrdersBean getCompletedOrdersBean() {
		return completedOrdersBean;
	}

	public void setCompletedOrdersBean(CompletedOrdersBean completedOrdersBean) {
		this.completedOrdersBean = completedOrdersBean;
	}

	public String getShiptoSapId() {
		return shiptoSapId;
	}

	public void setShiptoSapId(String shiptoSapId) {
		this.shiptoSapId = shiptoSapId;
	}

	public String getSoldtoSapId() {
		return soldtoSapId;
	}

	public void setSoldtoSapId(String shiptoSapId) {
		this.soldtoSapId = shiptoSapId;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}
	
	public List<DailyOrderStatusTxEntity> getDailyOrderStatusList() {
		return dailyOrderStatusList;
	}

	public void setDailyOrderStatusList(List<DailyOrderStatusTxEntity> dailyOrderStatusList) {
		this.dailyOrderStatusList = dailyOrderStatusList;
	}

	public Long getDailyOrderStatusCd() {
		return dailyOrderStatusCd;
	}

	public void setDailyOrderStatusCd(Long dailyOrderStatusCd) {
		this.dailyOrderStatusCd = dailyOrderStatusCd;
	}

	public long getSelectedContactPersonId() {
		return selectedContactPersonId;
	}

	public void setSelectedContactPersonId(long selectedContactPersonId) {
		this.selectedContactPersonId = selectedContactPersonId;
	}
	
	public static BestellungForm getSessionInstance(HttpServletRequest request) {
		return (BestellungForm)request.getSession().getAttribute(SESSION_KEY);
	}

	public Map<String, MaterialPackageTxEntity> getMaterialPackageTxMap() {
		return materialPackageTxMap;
	}

	public void setMaterialPackageTxMap(Map<String, MaterialPackageTxEntity> materialPackageTxMap) {
		this.materialPackageTxMap = materialPackageTxMap;
	}

	public List<ShiptoSiloEntity> getShiptoActiveSilos() {
		List<ShiptoSiloEntity> result = new ArrayList<ShiptoSiloEntity>();
		ShiptoEntity shipto = getShipTo();
		List<ShiptoSiloEntity> shipToSilos = shipto.getSilos();
		if (shipToSilos == null) {
			return result;
		}
		for (ShiptoSiloEntity silo : shipToSilos) {
			if (silo.getActiveFlag()) {
				result.add(silo);
			}
		}
		Collections.sort(result, new BeanComparator(ShiptoSiloEntity.ATTR_SILO_NAME));
		return result;
	}
	
	public List<DateTimeAwareCustomerOrderWrapper> getOpenCustorderList() {
		return openCustorderList;
	}

	public void setOpenCustorderList(List<DateTimeAwareCustomerOrderWrapper> openCustorderList) {
		this.openCustorderList = openCustorderList;
	}

	public List<DateTimeAwareCustomerOrderWrapper> getClosedCustorderList() {
		return closedCustorderList;
	}

	public void setClosedCustorderList(List<DateTimeAwareCustomerOrderWrapper> closedCustorderList) {
		this.closedCustorderList = closedCustorderList;
	}

	public List<DateTimeAwareOrderWrapper> getOpenOrdersForCustorder() {
		return openOrdersForCustorder;
	}

	public void setOpenOrdersForCustorder(List<DateTimeAwareOrderWrapper> openOrdersForCustorder) {
		this.openOrdersForCustorder = openOrdersForCustorder;
	}

	public List<DateTimeAwareOrderWrapper> getClosedOrdersForCustorder() {
		return closedOrdersForCustorder;
	}

	public void setClosedOrdersForCustorder(List<DateTimeAwareOrderWrapper> closedOrdersForCustorder) {
		this.closedOrdersForCustorder = closedOrdersForCustorder;
	}

	public String getCountryForShipto() {
		return countryForShipto;
	}

	public void setCountryForShipto(String countryForShipto) {
		this.countryForShipto = countryForShipto;
	}

	/**
	 * JSON data for each New Order editor.
	 * Keep the include/exclude lists in alphabetic order for easier reading!
	 * 
	 * @return
	 */
	public String getJsonData() {
		return JsonUtils.getJsonSerializer()
		.exclude(
				"ordersListFromCustOrders",
				"sareaMap",
				"productMap",
				"lpList.rfidobject")
		.include(
				"billtoList",
				"biRailItemList",
				"commercialCarrierId",
				"commercialCarrierList",
				"executingCarrierId",
				"executingCarrierList",
				"delTypeCd",
				"durationCorrectionFactor",
				"lpList",
				"payerList",
				"poolId",
				"poolList",
				"prodPlantCd",
				"prodPlantList",
				"productList",
				"*.railwayStationId",
				"railDistanceMeters",
				"railDurationMinutes",
				"railActualDeliveryDat",
				"sapMatId",
				"selectedTruckTypes",
				"serviceOnlyProductList",
				"spId",
				"spList",
				"supplyPlantCd",
				"supplyPlantList",
				"timeToleranceUomMap",
				"timeUomMap",
				"truckTypes",
				"uomList",
				"vehicleTypeWrapperList",
				"mixDesignList",
				"serviceProcessIndicatorList",
				"durationTime",
				"distanceMeters",
				"durationCorrFactor"
				)
		.serialize(
				editorList);
	}
	
	public String getDeliverySettingJSON(int editorIndex, int itemIndex){
		return JsonUtils.getDeliverySettingJSONSerializer().serialize(editorList.get(editorIndex).getOrderItems().get(itemIndex).getDeliverySetting());
	}

	public String getJsonSplittedOrders() {
		return JsonUtils.getJsonSerializer().include("ordersListFromCustOrders.index",
				"ordersListFromCustOrders.deliveryFromDatStr",
				"ordersListFromCustOrders.deliveryToDatStr",
				"ordersListFromCustOrders.deliveryFromTime",
				"ordersListFromCustOrders.deliveryToTime", "ordersListFromCustOrders.onTime",
				"ordersListFromCustOrders.onHoldOrderAndLockedFlag",
				"ordersListFromCustOrders.deliveryType",
				"ordersListFromCustOrders.serviceProcessIndicatorId",
				"ordersListFromCustOrders.exactCustorderQtyFlag",
				"ordersListFromCustOrders.splittedOrderItemDatas",
				"ordersListFromCustOrders.splittedOrderItemDatas.uomList.*",
				"ordersListFromCustOrders.splittedOrderItemDatas.orderedQuantity",
				"ordersListFromCustOrders.splittedOrderItemDatas.orderedUom",
				"ordersListFromCustOrders.splittedOrderItemDatas.materialType",
				"ordersListFromCustOrders.splittedOrderItemDatas.sapMaterialId",
				"ordersListFromCustOrders.splittedOrderItemDatas.orderItem.orderedUom.*",
				"ordersListFromCustOrders.splittedOrderItemDatas.orderItem.orderedQuantity").exclude(
				"*", "ordersListFromCustOrders.*", "ordersListFromCustOrders.order.*",
				"ordersListFromCustOrders.orderItem.*").serialize(editorList);
	}

	public boolean isValidShiptoCoords() {
		return shipTo != null && shipTo.getCoordLat() != null && shipTo.getCoordLon() != null 
		&& !shipTo.getCoordLat().equals(Constant.DEFAULT_COORD_LAT) && !shipTo.getCoordLon().equals(Constant.DEFAULT_COORD_LON);
	}
	
	public List<PoolTxEntity> getPoolTruckList() {
		return poolTruckList;
	}

	public void setPoolTruckList(List<PoolTxEntity> poolTruckList) {
		this.poolTruckList = poolTruckList;
	}

	public List<PoolTxEntity> getPoolRailList() {
		return poolRailList;
	}

	public void setPoolRailList(List<PoolTxEntity> poolRailList) {
		this.poolRailList = poolRailList;
	}

	public Long getBimodalShiptoSareaRestrId() {
		return bimodalShiptoSareaRestrId;
	}

	public void setBimodalShiptoSareaRestrId(Long bimodalShiptoSareaRestrId) {
		this.bimodalShiptoSareaRestrId = bimodalShiptoSareaRestrId;
	}

	public String getBimodalSoldtoSapId() {
		return bimodalSoldtoSapId;
	}

	public void setBimodalSoldtoSapId(String bimodalSoldtoSapId) {
		this.bimodalSoldtoSapId = bimodalSoldtoSapId;
	}

	public List<DateTimeAwareOrderWrapper> getCompletedOrderList() {
		return completedOrderList;
	}

	public void setCompletedOrderList(List<DateTimeAwareOrderWrapper> completedOrderList) {
		this.completedOrderList = completedOrderList;
	}

	public List<TrspZonePolygonEntity> getPolygonList() {
		return polygonList;
	}	

	public void setPolygonList(List<TrspZonePolygonEntity> polygonList) {
		this.polygonList = polygonList;
	}

	public Map<Object, List<ShiptoSalesAreaError>> getInvalidMap() {
		return invalidMap;
	}

	public void setInvalidMap(Map<Object, List<ShiptoSalesAreaError>> invalidMap) {
		this.invalidMap = invalidMap;
	}

	public List<ShiptoSoldtoLnfDataEntity> getLnfList() {
		return lnfList;
	}

	public void setLnfList(List<ShiptoSoldtoLnfDataEntity> lnfList) {
		this.lnfList = lnfList;
	}

	public DeliverySettingShiptoMap getShiptoMap() {
		return shiptoMap;
	}

	public void setShiptoMap(DeliverySettingShiptoMap shiptoMap) {
		this.shiptoMap = shiptoMap;
	}

	public List<CustorderEntity> getCustorders() {
		return custorders;
	}

	public void setCustorders(List<CustorderEntity> custorders) {
		this.custorders = custorders;
	}

	public AddToCustorderModeData getAddToCustorderMode() {
		return addToCustorderMode;
	}

	/**
	 * @return the allowFutureContract
	 */
	public Boolean getAllowFutureContracts() {
		return allowFutureContracts;
	}

	/**
	 * @param allowFutureContract the allowFutureContract to set
	 */
	public void setAllowFutureContracts(Boolean allowFutureContracts) {
		this.allowFutureContracts = allowFutureContracts;
	}


	/**
	 * UC-006 Create Single-Delivery Orders based on a Customer Order
	 */
	public static class AddToCustorderModeData {

		private boolean enabled;
		private Long custorderId;

		public AddToCustorderModeData() {
			reset();
		}

		public void reset() {
			enabled = false;
			custorderId = null;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public Long getCustorderId() {
			return custorderId;
		}

		public void setCustorderId(Long custorderId) {
			this.custorderId = custorderId;
		}

	}


	public Map<Long, SapMaterialDescEntity> getSiloMaterialDescriptionMap() {
		return siloMaterialDescriptionMap;
	}

	public void setSiloMaterialDescriptionMap(
			Map<Long, SapMaterialDescEntity> siloMaterialDescriptionMap) {
		this.siloMaterialDescriptionMap = siloMaterialDescriptionMap;
	}

	public Long getUnloadingTime() {
		return unloadingTime;
	}

	public void setUnloadingTime(Long unloadingTime) {
		this.unloadingTime = unloadingTime;
	}

	public Boolean getDelivnoteCollPaperFlag() {
		return delivnoteCollPaperFlag;
	}

	public void setDelivnoteCollPaperFlag(Boolean delivnoteCollPaperFlag) {
		this.delivnoteCollPaperFlag = delivnoteCollPaperFlag;
	}

	public boolean getDisplayShipToTimezone() {
		TimeZone tz = getShipToTimeZone();
		return !tz.isSame(UserProvider.getTimezone());
	}

	public TimeZone getShipToTimeZone() {
		// we always take the LNF (One-Time) timezone over the currently set ship-to timezone
		// The reason is that a one-time order can have a different timezone/address and we
		// need to take this into consideration.
		if (lnfSelectedTimeZone != null) {
			return lnfSelectedTimeZone;
		}
		if (shipToTZ != null) {
			return shipToTZ;
		}

		return TimeZone.getAppTimeZone();
	}

	public void setTimeZoneList(List<TimeZone> timeZones) {
		this.timeZoneList = timeZones;
	}

	public List<TimeZone> getTimeZoneList() {
		return this.timeZoneList;
	}

	public String getLnfSelectedTimeZoneCd() {
		// if the LNF timezone is not set, then getShipToTimeZone returns the timezone of the
		// shipto (which is ok as a default). otherwise (that is, if the user has already selected
		// an LNF timezone from the dropdown list), then getShipToTimeZone() returns that.
		return getShipToTimeZone().getTimezoneCd();
	}

	public void setLnfSelectedTimeZoneCd(String selectedTimeZoneCd) {
		if (StringUtils.empty(selectedTimeZoneCd)) {
			selectedTimeZoneCd = ConfigBeanProvider.getConfigBean().getAppDefaultTimezone();
		}

		lnfSelectedTimeZone = new TimeZone(selectedTimeZoneCd, locale);
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public boolean isWithActiveCemContracts() {
		return withActiveCemContracts;
	}

	public void setWithActiveCemContracts(boolean withActiveCemContracts) {
		this.withActiveCemContracts = withActiveCemContracts;
	}

	public boolean isWithActiveAggContracts() {
		return withActiveAggContracts;
	}

	public void setWithActiveAggContracts(boolean withActiveAggContracts) {
		this.withActiveAggContracts = withActiveAggContracts;
	}
}

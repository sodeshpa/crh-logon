/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.ajax;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.interfaces.DeliveryDateService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RailSchedulingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface;
import com.crh.logon.business.services.utils.BeanUtilsBuilder;
import com.crh.logon.business.services.utils.CustorderSplitWrapper;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.DeliveryDistanceValidationUtils;
import com.crh.logon.business.services.utils.DistanceValidationInfoBean;
import com.crh.logon.business.services.utils.LeadTimeValidationResultGuiBean;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.MaterialColumn;
import com.crh.logon.business.services.utils.OrderTakingEditorRailDeliveryDto;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.SplittedOrderData;
import com.crh.logon.business.services.utils.SplittedOrderItemData;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.converters.Base64ByteArrayConverter;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.DateConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.converters.StringConverter;
import com.crh.logon.cust.web.forms.webbestellung.WebBestellungForm;
import com.crh.logon.cust.web.utils.WebOrderTakingEditorDto;
import com.crh.logon.cust.web.utils.WebOrderTakingEditorParamsDto;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.ObjectUtils;
import sun.rmi.runtime.Log;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="WebOrderAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.cust.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.SplittedOrderData"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.SplittedOrderItemData"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.LeadTimeValidationResultGuiBean"
 * @adnbus.dwr.signature method="getNextTrainArr" parameters="Long sapMaterialId, HttpServletRequest request, HttpSession session" imports="java.util.Date, javax.servlet.http.HttpSession"
 * @adnbus.dwr.signature method="calculateDurDistBetweenRailwayStations" parameters="Long shippRailwayStationId, Long shiptoRailwayStationId" imports="java.util.List" 
 * @adnbus.dwr.signature method="selectMaterial" parameters="String compositeId, HttpServletRequest request, HttpSession session" 
 * @adnbus.dwr.signature method="getCollectedEditorData" parameters="WebOrderTakingEditorParamsDto paramsDto, HttpServletRequest request, HttpSession session"
 * 
 */
@AdnTrace
public class WebOrderAjaxService extends DwrServiceSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WebOrderAjaxService.class);
	/**
	 * Called by the Order Taking screen when the bill-to belonging to the selected product is defined by a schedule
	 * 
	 * @param restrId
	 * @return
	 */
	public Long findBilltoByRhythm(Date delivery, Long restrId) {
		Authorization.authorize(Authorization.WEB_ORDER);
			
		Calendar delCal = DateTimeUtils.getDateAsCalendar(delivery);
		return getDispokarteService().findBilltoByRhythm(delCal, restrId);
	}
	
	public WebOrderTakingEditorDto selectMaterial(String compositeId, HttpServletRequest request,
			HttpSession session) {
		Authorization.authorize(Authorization.WEB_ORDER);
		WebBestellungForm webBestellungForm = getWebBestellungForm(session);
		DeliverySettingShiptoMap shipToMap = webBestellungForm.getShipToMap();
		DeliverySettingDTO selectedDeliverySetting = shipToMap.getDeliverySettingByCompositeIdentifier(compositeId);
		SareaEntity salesArea = selectedDeliverySetting.getSarea();
		webBestellungForm.setSarea(salesArea);
		ShiptoSareaEntity shipToSalesArea = selectedDeliverySetting.getShiptoSarea();
		webBestellungForm.setShiptoSarea(shipToSalesArea);
		webBestellungForm.getCustorder().setOnHoldOrderAndLockedFlag(selectedDeliverySetting.getOnHoldOrderAndLockedFlag());
		
		ShiptoEntity shipTo = webBestellungForm.getShipTo();
		SapBpEntity shipToSapBp = shipTo.getShiptoSapBp();
		String shipToName = shipToSapBp.getName1();
		Long shipToSapId = shipToSapBp.getSapBpId();
		if (shipToSalesArea == null) {
			// -------
			// Ship-To information is not prepared by Dispokarte yet,
			// thus there is not enough information available to create a new order
			// -------
			MessageUtils.addActionWarning(request,
					"webbestellung.error.shipto.not.initialized.detailed", shipToName,
					shipToSapId.toString());
			throw new LogonException(Reason.RES_STRUTS_VALIDATION);
		}

		/**
		 * Display warning for sales area related errors
		 */
		if (InvalidMapSupport.containsErrorExcept(shipToMap.getInvalidMap(), shipToSalesArea,
				ShiptoSalesAreaError.CUSTOMER_CREDIT_BLOCK)) {
			SapBpEntity soldTo = webBestellungForm.getSoldTo();
			String soldToName = soldTo.getName1();
			String soldToSapId = soldTo.getSapId();
			String salesAreaDisplayLongname = salesArea.getSareaDisplayLongname();
			MessageUtils.addActionWarning(request,
					"webbestellung.error.shiptosarea.or.soldto.not.initialized",
					shipToName + " (" + shipToSapId + ")",
					salesAreaDisplayLongname, soldToName + " (" + soldToSapId + ")");
		}

		// Bill to
		ServiceLocator serviceLocator = getServiceLocator();
		OrderDetailServiceLocalInterface orderDetailService = serviceLocator.getLocalOrderDetailService();
		Long salesAreaId = salesArea.getSareaId();
		Long shipToId = shipTo.getShiptoId();
		List<ShiptoBilltoEntity> billTos = orderDetailService.loadBilltos(shipToId, salesAreaId);

		SapBpEntity soldTo = webBestellungForm.getSoldTo();

		// Payer list
		Long soldToSapBpId = soldTo.getSapBpId();
		DispokarteServiceLocalInterface dispokarteService = serviceLocator.getLocalDispokarteService();
		List<SapBpEntity> payers = dispokarteService.loadPayers(salesAreaId, soldToSapBpId);

		WebOrderTakingEditorDto webOrderTakingEditorDto = new WebOrderTakingEditorDto();
		webOrderTakingEditorDto.setPayers(payers);
		webOrderTakingEditorDto.setBillTos(billTos);
		webOrderTakingEditorDto.setOnHoldOrderAndLockedFlag(selectedDeliverySetting.getOnHoldOrderAndLockedFlag());

		SapContractItemEntity sapContractItem = selectedDeliverySetting.getSapContractItem();
		boolean payerIgnored = sapContractItem != null
				&& OrderUtils.isPayerIgnored(sapContractItem.getSapContract());
		Boolean copyCustomerComment = salesArea.getCustCommFlag();
		webOrderTakingEditorDto.setCopyCustomerComment(copyCustomerComment);

		return webOrderTakingEditorDto;
	}

	// turn off sonar complain with the next annotation
	@java.lang.SuppressWarnings("squid:S2259")
	public LeadTimeValidationResultGuiBean getDeliveryTimesAndRules(String distanceMeters, Long shippingPointId,
			Long deliveryTypeCd, String compositeId, HttpServletRequest request, HttpSession session){

		/**
		 * we are here to find values for the next 2 variable,
		 * which passes SDND and DBLT checks
		 */
		Date defaultdeliveryFromDat = null;
		Date defaultdeliveryToDat = null;


		WebBestellungForm webBestellungForm = getWebBestellungForm(session);
		ServiceLocator serviceLocator = getServiceLocator();

		DeliverySettingShiptoMap shipToMap = webBestellungForm.getShipToMap();
		DeliverySettingDTO selectedDeliverySetting = shipToMap.getDeliverySettingByCompositeIdentifier(compositeId);
		ShiptoSareaEntity shipToSalesArea = selectedDeliverySetting.getShiptoSarea();
		SareaEntity sarea = shipToSalesArea.getSarea();
		final ShiptoEntity shipTo = shipToSalesArea.getShipto();

		boolean isDbltValidationActive = Boolean.TRUE.equals(shipToSalesArea.getDbltActiveFlag());
		boolean isSdNdValidationActive = Boolean.TRUE.equals(shipToSalesArea.getSdltAndNdltActive());

		/*
		 initialize delivery time fields:
		 	- set defaultdeliveryFromDat to next working day
		 	- set defaultdeliveryToDat to next working day
		 	- set deliveryFromTime to defined in shipto/or same fallback value
		 	- set deliveryToTime to defined in shipto/or same fallback value
		 */
		String deliveryFromTime = DateTimeUtils.getHourMinuteTimePart(shipTo.getOpen1From(), WebBestellungForm.DELIVERY_FROM_DEFAULT_TIME);
		String deliveryToTime = DateTimeUtils.getHourMinuteTimePart(shipTo.getOpen2To(), WebBestellungForm
				.DELIVERY_TO_DEFAULT_TIME);
		TimeZone shipToTimeZone = webBestellungForm.getShipToTimeZone();
		String dateString = shipToTimeZone.fmtAsDayMonthYear(shipToTimeZone.nextWorkingDay());
		try {
			defaultdeliveryFromDat = shipToTimeZone.parseDayMonthYearHourMinute(dateString, deliveryFromTime);
			defaultdeliveryToDat = shipToTimeZone.parseDayMonthYearHourMinute(dateString, deliveryToTime);
			if (LOG.isDebugEnabled()){
				LOG.debug("getDeliveryTimesAndRules :: defaultdeliveryFromDat="+defaultdeliveryFromDat);
				LOG.debug("getDeliveryTimesAndRules :: defaultdeliveryToDat="+defaultdeliveryToDat);
			}
		}
		catch (ParseException e) {
			LOG.error("Cannot parse!");
		}

		/*
			check SDND
		 */
		if(isSdNdValidationActive) {
			DeliveryDateService deliveryDateService = serviceLocator.getDeliveryDateService();
			// create a new instance since the value might change during the validation
			Date deliveryToDatForCheck = new Date(defaultdeliveryToDat.getTime());
			CustorderError error =
					deliveryDateService.checkTruckLeadTime(defaultdeliveryFromDat, deliveryToDatForCheck, sarea, shipToSalesArea);

			// shift to next working day delivery dates if sdnd lead check throws error
			if (CustorderError.LEADTIME_WEB_ORDER_ND_EARLY.equals(error)) {
				Calendar cal = DateTimeUtils.getDateAsCalendar(defaultdeliveryToDat);
				Calendar nextWorkingDay = DateTimeUtils.getNextWorkingDay(cal);
				defaultdeliveryToDat = nextWorkingDay.getTime();

				Calendar cal2 = DateTimeUtils.getDateAsCalendar(defaultdeliveryFromDat);
				Calendar nextWorkingDay2 = DateTimeUtils.getNextWorkingDay(cal2);
				defaultdeliveryFromDat = nextWorkingDay2.getTime();
			}
			if (LOG.isDebugEnabled()){
				LOG.debug("getDeliveryTimesAndRules :: SdNdValidationActive defaultdeliveryFromDat="+defaultdeliveryFromDat);
				LOG.debug("getDeliveryTimesAndRules :: SdNdValidationActive defaultdeliveryToDat="+defaultdeliveryToDat);
			}
		}

		/*
		  	check DBLT
		 */
		DistanceValidationInfoBean distanceValidationInfoBean = null;
		LeadTimeValidationResultGuiBean leadTimeValidationResultGuiBean = new LeadTimeValidationResultGuiBean();
		if (isDbltValidationActive) {

			Long transportDistanceInMeter = getTransportDistanceInMeter(distanceMeters, shippingPointId, deliveryTypeCd,
					serviceLocator, shipTo);


			DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new
					DeliveryDistanceValidationUtils();
			distanceValidationInfoBean = deliveryDistanceValidationUtils
					.distanceBaseLeadTimeCheckCreatePopUpText(shipToSalesArea, sarea, webBestellungForm.getCustorder(),
							transportDistanceInMeter,
							request.getLocale());

			Date earliestPossibleDeliveryFromDate = distanceValidationInfoBean
					.getEarliestPossibleDeliveryFromDate();

			Calendar dbltEarliestDeliveryFromDatCal = DateTimeUtils.getDateAsCalendar(earliestPossibleDeliveryFromDate);
			Calendar deliveryFromDatCal = DateTimeUtils.getDateAsCalendar(defaultdeliveryFromDat);

			// adjust delivery dates only if dblt lead check would throw error
			if(deliveryFromDatCal.before(dbltEarliestDeliveryFromDatCal)){
				Long dayDiff = DateTimeUtils.getDayDiff(deliveryFromDatCal, dbltEarliestDeliveryFromDatCal);
				defaultdeliveryFromDat = dbltEarliestDeliveryFromDatCal.getTime();

				Calendar cal = DateTimeUtils.getDateAsCalendar(defaultdeliveryToDat);
				cal.add(Calendar.DAY_OF_MONTH, dayDiff.intValue());
				defaultdeliveryToDat = cal.getTime();
			}
			if (LOG.isDebugEnabled()){
				LOG.debug("getDeliveryTimesAndRules :: DbltValidationActive defaultdeliveryFromDat="+defaultdeliveryFromDat);
				LOG.debug("getDeliveryTimesAndRules :: DbltValidationActive defaultdeliveryToDat="+defaultdeliveryToDat);
				LOG.debug("getDeliveryTimesAndRules :: DbltValidationActive transportDistanceInMeter="+transportDistanceInMeter);
			}
			leadTimeValidationResultGuiBean.setDistanceMeters(transportDistanceInMeter);
		}

		String distanceValidationPopUpText = DeliveryDistanceValidationUtils
				.getDistanceValidationPopUpText(distanceValidationInfoBean, request.getLocale());
		leadTimeValidationResultGuiBean.setDistanceValidationPopUpText(distanceValidationPopUpText);

		if (isSdNdValidationActive || isDbltValidationActive) {

			String earliestPossibleDeliveryFromDateString = shipToTimeZone.fmtAsDayMonthYear(
					defaultdeliveryFromDat);
			String earliestPossibleDeliveryFromTimeString = DateTimeUtils.getTimePart(defaultdeliveryFromDat, WebBestellungForm
					.DELIVERY_FROM_DEFAULT_TIME);

			String earliestPossibleDeliveryToDateString = shipToTimeZone.fmtAsDayMonthYear(
					defaultdeliveryToDat);
			String earliestPossibleDeliveryToTimeString = DateTimeUtils.getTimePart(defaultdeliveryToDat, WebBestellungForm
					.DELIVERY_TO_DEFAULT_TIME);

			leadTimeValidationResultGuiBean.setEarliestPossibleDeliveryFromDateString(earliestPossibleDeliveryFromDateString);
			leadTimeValidationResultGuiBean.setEarliestPossibleDeliveryFromTimeString(earliestPossibleDeliveryFromTimeString);
			leadTimeValidationResultGuiBean.setEarliestPossibleDeliveryToDateString(earliestPossibleDeliveryToDateString);
			leadTimeValidationResultGuiBean.setEarliestPossibleDeliveryToTimeString(earliestPossibleDeliveryToTimeString);
		}

		if (LOG.isDebugEnabled()){
			LOG.debug("getDeliveryTimesAndRules :: leadTimeValidationResultGuiBean="+leadTimeValidationResultGuiBean);
		}

		return leadTimeValidationResultGuiBean;
	}

	private Long getTransportDistanceInMeter(String distanceMeters, Long shippingPointId, Long deliveryTypeCd, ServiceLocator serviceLocator,
			ShiptoEntity shipTo) {
		Long transportDistanceInMeter = null;
		try {
			long distance = Double.valueOf(distanceMeters).longValue();
			if (distance > 0) {
				transportDistanceInMeter = new Long(distance);
			}
		}
		catch (NumberFormatException numberFormatException) {
			LOG.error("Invalid distance of route on webbestellung.");

		}


		ShippingPointServiceLocalInterface shippingPointService = serviceLocator.getLocalShippingPointService();
		ShippingpointEntity shippingPointOfCustomerOrder = shippingPointService.getShippingPointById(shippingPointId);

		DeliveryTypeBsEntity deliveryType = serviceLocator.getRefDataGenericsService().findBsEntity(
				DeliveryTypeBsEntity.class, deliveryTypeCd);

		if (transportDistanceInMeter == null && shippingPointOfCustomerOrder != null) {
			//if still null
			OrderService orderService = serviceLocator.getOrderService();
			SapBpEntity shiptoSapBusinessPartner = shipTo.getShiptoSapBp();
			TruckRouteEntity route = orderService.findTruckRoute(
					shiptoSapBusinessPartner, shippingPointOfCustomerOrder,
					deliveryType);
			if (route != null) {
				Long routeDistanceCalculatedInMeter = route.getDistanceCalculatedM();
				transportDistanceInMeter = routeDistanceCalculatedInMeter;
			}
		}
		return transportDistanceInMeter;
	}

	public ShippingpointEntity getShippingPointById(Long shippingPointId, HttpSession session) {
			Authorization.authorize(Authorization.WEB_ORDER);
			ServiceLocator serviceLocator = getServiceLocator();
		ShippingPointServiceLocalInterface shippingPointService = serviceLocator.getLocalShippingPointService();
		return shippingPointService.getShippingPointById(shippingPointId);
	}

	protected static WebBestellungForm getWebBestellungForm(HttpSession session) {
		Authorization.authorize(Authorization.WEB_ORDER);
		return (WebBestellungForm) session.getAttribute("cust_WebBestellungForm");
	}
    
	public Double getDurationCorrectionFactor(Long shippingPointId, Long shiptoId, Long delTypeCd) {
		Authorization.authorize(Authorization.WEB_ORDER);
		Long spId = ObjectUtils.equals(shippingPointId, -1L) ? null : shippingPointId;
		OrderServiceLocalInterface orderService = getServiceLocator().getLocalOrderService();
		return orderService.getDurationCorrectionFactor(spId, shiptoId, delTypeCd);
	}
	
	public static Date getNextTrainArr(String compositeIdentifier, HttpServletRequest request, HttpSession session) {
		Authorization.authorize(Authorization.WEB_ORDER);
		WebBestellungForm webbestellungForm = getWebBestellungForm(session);
		DeliverySettingDTO deliverySetting = webbestellungForm.getShipToMap().getDeliverySettingByCompositeIdentifier(
				compositeIdentifier);
		ShiptoSareaEntity shiptoSarea = deliverySetting.getShiptoSarea();
		RailwayStationEntity railwayStation = shiptoSarea.getShipto().getShiptoSapBp().getRailwayStation();
		ShippingpointEntity railRestrSp = deliverySetting.getShiptoSareaRestr().getShippingpoint();
		if (railwayStation != null) {
			Long railShiptoRailwayStationId = railwayStation.getRailwayStationId();
			if (railRestrSp == null) {
				// TODO litdict
				MessageUtils.addActionWarning(
						request,
						"Unable to calculate actual delivery date: Shipping-point is not defined in the Dispokarte restriction for this FVL ship-to/product/production plant combination");
			}
			else {
				if (railRestrSp.getRailwayStation() == null) {
					MessageUtils.addActionWarning(request,
							"Railway station is not set for this FVL ship-tp"); // TODO litdict
				}
				else {
					Date morningOfDay = null;
					try {
						morningOfDay = DateTimeUtils.getMorningOfDay(webbestellungForm.getDeliveryFrom());
					}
					catch (Exception exception) {
						MessageUtils.addActionWarning(request,
								"webbestellung.error.invalid.delivery.from");
					}
					Date midnightOfDay = null;
					try {
						midnightOfDay = DateTimeUtils.getMidnightOfDay(webbestellungForm.getDeliveryTo());
					}
					catch (Exception exception) {
						MessageUtils.addActionWarning(request,
								"webbestellung.error.invalid.delivery.to");
					}

					ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
					RailSchedulingServiceLocalInterface railSchedulingService = serviceLocator.getLocalRailSchedulingService();
					TimeZone userTimeZone = UserProvider.getTimezone();
					ShiptoEntity shipTo = webbestellungForm.getShipTo();
					OrderTakingEditorRailDeliveryDto railData = railSchedulingService.calculateRailDeliveryData(
							railRestrSp.getRailwayStation().getRailwayStationId(),
							railShiptoRailwayStationId, morningOfDay, midnightOfDay, shipTo,
							userTimeZone, false);

					if (railData == null) {
						MessageUtils.addActionWarning(
								request,
								"There is no train route between this ship-to and FVL shipping-point according to the railway timetable"); // TODO litdict
					}
					else {
						Calendar nextTrainArr = Calendar.getInstance();
						nextTrainArr.setTime(railData.getActualDeliveryDatArr());
						if (nextTrainArr.after(webbestellungForm.getDeliveryToDatStr())) {
							MessageUtils.addActionWarning(
									request,
									"The next possible train arrival date ("
											+ nextTrainArr.getTime()
											+ ") to the given FVL shipping-point is after the delivery-to date"); // TODO litdict
						}
						Date actualDeliveryDat = nextTrainArr.getTime();

						return actualDeliveryDat;
					}
				}
			}
		}
		return null;
	}
	
	public List calculateDurDistBetweenRailwayStations(Long shippRailwayStationId, Long shiptoRailwayStationId) {
		Authorization.authorize(Authorization.WEB_ORDER);
		Long[] railDurDist = getRailSchedulingService().calculateDurDistBetweenRailwayStations(
				shippRailwayStationId, shiptoRailwayStationId);
		if (railDurDist != null) {
			return Arrays.asList(railDurDist);
		}
		else{
			return new ArrayList();
		}
	}
	
	public void getCollectedEditorData(
			WebOrderTakingEditorParamsDto paramsDto,
			HttpServletRequest request,
			HttpSession session 
		) throws Exception {
		// [[
		Object[][] params = {{"paramsDto", paramsDto},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCollectedEditorData", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.WEB_ORDER);
			
			WebBestellungForm webBestellungForm = getWebBestellungForm(session);
			webBestellungForm.getCustorder().setOnHoldOrderAndLockedFlag(paramsDto.getOnHoldOrderAndLockedFlag());
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(paramsDto);
			}
		// ]]
		}
	}

	/**
	 * Calculates and returns the sum quantity for the given material in base UOM.
	 * Please note it <b> will also save on the editors material column </b>!
	 * @param editorIndex - selected editor's id
	 * @param orderDatas - records containing the quantities
	 * @param sapMaterialId - the id of the selected column
	 */
	@AdnTrace
	public BigDecimal sumOrderedQtyInBase(int editorIndex, List<SplittedOrderData> orderDatas, Long sapMaterialId, HttpSession session) {
		Authorization.authorize(Authorization.WEB_ORDER);
		BigDecimal result = null;
		WebBestellungForm form = getWebBestellungForm(session);
		CustorderSplitWrapper wrapper = form.getCustorderDTO().getSplitWrapper();

		BigDecimal sumInBase = new BigDecimal("0");

		for (SplittedOrderData splittedOrderData : orderDatas) {
			List<SplittedOrderItemData> splittedOrderItemDatas = splittedOrderData.getSplittedOrderItemDatas();
			for (SplittedOrderItemData splittedOrderItemData : splittedOrderItemDatas) {
				if (splittedOrderItemData.getSapMaterialId().equals(sapMaterialId)) {

					List<SplittedOrderData> formSplittedOrders = form.getSplittedOrderDatas();
					SplittedOrderData formsplittedOrder = formSplittedOrders.get(splittedOrderData.getIndex());
					List<SplittedOrderItemData> formSplittedOrderItems = formsplittedOrder.getSplittedOrderItemDatas();
					SplittedOrderItemData formSplittedOrderItem = formSplittedOrderItems.get(splittedOrderItemData.getIndex());

					formSplittedOrderItem.updateFrom(splittedOrderItemData);

					BigDecimal baseUomOrderedQty =
							CustorderUtils.getBaseUomOrderedQty(wrapper, formSplittedOrderItem.getOrderItem());
					sumInBase = sumInBase.add(baseUomOrderedQty);
				}
			}
		}

		// We save the calculated quantity on the column
		Set<MaterialColumn> materialColumns = form.getMaterialColumns();
		for (MaterialColumn materialColumn : materialColumns) {
			if (materialColumn.getMaterial().getSapMaterialId().equals(sapMaterialId)) {
				materialColumn.setOrderedQuantity(sumInBase);
			}
		}

		return result = sumInBase.stripTrailingZeros();
	}

	/**
	 * Deletes an order from the splitted order list of the form based on the passed index
	 * Also re-indexes the entries because of the 'collapse' effect that occurs when removing
	 * an indexed entry from the list. This originally caused problem when removing elements
	 * then trying to use their indexes as keys.
	 * @param editorIndex - selected editor from which the element will be deleted
	 * @param index - designated order index
	 * @param session
	 */
	@AdnTrace
	public void deleteSplittedOrderData(int editorIndex, int index, HttpSession session) {
		Authorization.authorize(Authorization.WEB_ORDER);
		WebBestellungForm form = getWebBestellungForm(session);

		CustorderSplitWrapper wrapper = form.getCustorderDTO().getSplitWrapper();
		CustorderEntity custorder = wrapper.getCustorder();
		List<OrderEntity> orders = custorder.getOrders();

		// Need to re-index the entries because delete causes a 'collapse'
		int newIndex = 0;

		// Delete splitted order elements
		List<SplittedOrderData> ordersListFromCustOrders = form.getSplittedOrderDatas();
		Iterator<SplittedOrderData> iterator = ordersListFromCustOrders.iterator();
		while (iterator.hasNext()) {
			SplittedOrderData next = iterator.next();
			if (next.getIndex() == index) {

				List<SplittedOrderItemData> splittedOrderItemDatas = next.getSplittedOrderItemDatas();
				for (SplittedOrderItemData splittedOrderItemData : splittedOrderItemDatas) {
					Long sapMaterialId = splittedOrderItemData.getSapMaterialId();
					Set<MaterialColumn> materialColumns = form.getMaterialColumns();
					for (MaterialColumn materialColumn : materialColumns) {
						if (sapMaterialId.equals(materialColumn.getMaterial().getSapMaterialId())) {
							BigDecimal oldQty = materialColumn.getOrderedQuantity();
							BigDecimal baseUomOrderedQty =
									CustorderUtils.getBaseUomOrderedQty(wrapper, splittedOrderItemData.getOrderItem());
							BigDecimal newQty = oldQty.subtract(baseUomOrderedQty);
							materialColumn.setOrderedQuantity(newQty.stripTrailingZeros());
						}
					}
				}

				// Delete SDO
				orders.remove(index);
				// Delete SplittedOrderData
				iterator.remove();

			}
			else {
				// New index because of 'collapse' effect
				next.setIndex(newIndex++);
			}
		}
	}

	/*
	 * CRHLOGON-127: This method is required to re-initialize the custorder if the adjust deliveries popup is
	 * closed via cancel button. The reason this is needed is that when the adjust deliveries popup is opened, the code
	 * in WebBestellungAction calls setupAndValidateOrder(), which initializes the custorder with a single custorder item.
	 * If we did not reset things here, there would remain a spurious custorder item in the custorder. As a resuult,
	 * saving the order (even without adjusting deliveries) would fail because it could end up with two equal BULK custorder
	 * items.
	 */
	@AdnTrace
	public void cancelWebAdjustDeliveriesPopUp(HttpSession session) {
		Authorization.authorize(Authorization.WEB_ORDER);
		WebBestellungForm form = getWebBestellungForm(session);
		CustorderDTO custorderDTO = form.getCustorderDTO();

		custorderDTO.setSplitWrapper(null);
		CustorderEntity customerOrder = custorderDTO.getCustorderEntity();
		customerOrder.setOrders(null);
		customerOrder.setSapContract(null);
		form.setSplittedOrderDatas(null);

		customerOrder.setCustorderItems(new ArrayList<CustorderItemEntity>());
	}


	/**
	 * Adds a {@code SplittedOrderData} element to the selected editor
	 *
	 * @param editorIndex - selected editor
	 */
	@AdnTrace
	public void addSplittedOrderData(int editorIndex, HttpServletRequest request) {
		Authorization.authorize(Authorization.WEB_ORDER);
		HttpSession session = request.getSession();
		WebBestellungForm form = getWebBestellungForm(session);

		CustorderSplitWrapper wrapper = form.getCustorderDTO().getSplitWrapper();
		CustorderEntity custorder = wrapper.getCustorder();
		List<OrderEntity> orders = custorder.getOrders();

		// If there are no existing SDOs we can't template
		if (orders == null || orders.isEmpty()) {
			MessageUtils.addActionError(request, "errors.general");
			throw new RuntimeException("Cannot template new SDO as there are no existing SDO in CO!");
		}

		OrderEntity lastOrderAsTemplate = orders.get(orders.size() - 1);

		// Delete splitted order elements
		List<SplittedOrderData> ordersListFromCustOrders = form.getSplittedOrderDatas();

		// Create SDO
		OrderEntity newOrder = new OrderEntity();
		newOrder.setArchivedFlag(false);
		newOrder.setArchivingStatus(getArchivingStatusBsEntity(Constant.ARCHIVING_STATUS_NEW));
		BeanUtilsBean beanUtilsBean = new BeanUtilsBuilder().
				addConverter(new BigDecimalConverter()).
				addConverter(new LongConverter()).
				addConverter(new BooleanConverter()).
				addConverter(new DateConverter()).
				addConverter(new Base64ByteArrayConverter()).
				addConverter(new StringConverter()).
				createBean();

		try {
			// List variables cannot be copied!
			beanUtilsBean.copyProperties(newOrder, lastOrderAsTemplate);

			// Need to point order vehicle types to new order
			List<OrderTruckTypeEntity> orderVehicleTypes = createNewOrderTruckTypesForOrder(beanUtilsBean, newOrder);
			newOrder.setOrderVehicleTypes(orderVehicleTypes);

			List<OrderItemEntity> orderItems = lastOrderAsTemplate.getOrderItems();
			List<OrderItemEntity> newOrderItems = new ArrayList<OrderItemEntity>();
			for (OrderItemEntity orderItemEntity : orderItems) {
				OrderItemEntity newOrderItemEntity = new OrderItemEntity();
				beanUtilsBean.copyProperties(newOrderItemEntity, orderItemEntity);
				newOrderItemEntity.setOrderedQuantity(BigDecimal.ZERO);
				newOrderItems.add(newOrderItemEntity);
			}
			newOrder.setOrderItems(newOrderItems);
		}
		catch (Exception e) {
			MessageUtils.addActionError(request, e);
			return;
		}

		orders.add(newOrder);

		// SplittedOrderData attributes
		ShiptoEntity shipTo = form.getShipTo();
		SplittedOrderData.DeliveryType deliveryType = LogonUtil.getDeliveryType(newOrder);
		Boolean exactCustorderQtyFlag = CustorderUtils.getExactCustorderQty(custorder);
		int index = ordersListFromCustOrders.size();

		TimeZone shiptoTimeZone = form.getShipToTimeZone();
		SplittedOrderData splittedOrderData = new SplittedOrderData(newOrder, shipTo,
				deliveryType, exactCustorderQtyFlag, index, shiptoTimeZone);
		splittedOrderData.setServiceProcessIndicatorId(Constant.INVALID_OBJECT_ID_STR);
		ordersListFromCustOrders.add(splittedOrderData);

		List<OrderItemEntity> orderItems = newOrder.getOrderItems();
		// Enrich list
		CustorderUtils.addMissingOrderItemsBasedOnCOI(wrapper, newOrder, orderItems, custorder.getCustorderItems());

		List<SplittedOrderItemData> splittedOrderItemDatas = CustorderUtils.populateSplittedOrdetItems(orderItems);
		splittedOrderData.setSplittedOrderItemDatas(splittedOrderItemDatas);

		Map<Long, BigDecimal> qtyPerMaterial = new HashMap<Long, BigDecimal>();
		CustorderUtils.calculateQtyPerMaterial(wrapper, qtyPerMaterial, splittedOrderItemDatas);

		// Add the new order items qtys on top of the existing quantities
		Set<MaterialColumn> materialColumns = form.getMaterialColumns();
		for (MaterialColumn materialColumn : materialColumns) {
			Long sapMaterialId = materialColumn.getMaterial().getSapMaterialId();
			BigDecimal newOrderItemQty = qtyPerMaterial.get(sapMaterialId);

			// Can be in case of tricky bag orders
			if (newOrderItemQty == null) {
				continue;
			}

			BigDecimal oldMaterialQty = materialColumn.getOrderedQuantity();
			BigDecimal newMaterialQty = newOrderItemQty.add(oldMaterialQty);
			materialColumn.setOrderedQuantity(newMaterialQty.stripTrailingZeros());
		}
	}

	/**
	 * Synchronize the data between the GUI state and the model.
	 * @param splittedOrdersPerEditors
	 * @param session
	 */
	@AdnTrace
	public void sendSplittedOrderData(List<List<SplittedOrderData>> splittedOrdersPerEditors, HttpSession session) {
		Authorization.authorize(Authorization.WEB_ORDER);
		WebBestellungForm form = getWebBestellungForm(session);

		for (int editorIndex = 0; editorIndex < splittedOrdersPerEditors.size(); editorIndex++) {

			List<SplittedOrderData> ordersListFromCustOrders = form.getSplittedOrderDatas();

			List<SplittedOrderData> splittedOrders = splittedOrdersPerEditors.get(editorIndex);
			if (splittedOrders != null) {
				for (SplittedOrderData srcSplittedOrderData : splittedOrders) {
					if (ordersListFromCustOrders != null) {
						int index = srcSplittedOrderData.getIndex();
						SplittedOrderData dstSplittedOrderData = ordersListFromCustOrders.get(index);
						dstSplittedOrderData.updateFrom(srcSplittedOrderData, null);
					}
				}
			}

		}
	}

	/**
	 * Clears the SDOI items (and their respective {@link SplittedOrderItemData} where the ordered qty is zero
	 * => if an SDO is left without any SDOI it also gets deleted
	 * @param session
	 */
	public void clearZeroOrders(HttpSession session) {

		Authorization.authorize(Authorization.WEB_ORDER);
		WebBestellungForm form = getWebBestellungForm(session);

			// clear splitted order and items where zero
			List<SplittedOrderData> splittedOrders = form.getSplittedOrderDatas();
			Iterator<SplittedOrderData> splittedOrderIterator = splittedOrders.iterator();

			while (splittedOrderIterator.hasNext()) {
				SplittedOrderData splittedOrderData = splittedOrderIterator.next();
				List<SplittedOrderItemData> splittedOrderItemDatas = splittedOrderData.getSplittedOrderItemDatas();
				Iterator<SplittedOrderItemData> splittedOrderItemIterator = splittedOrderItemDatas.iterator();
				while (splittedOrderItemIterator.hasNext()) {
					SplittedOrderItemData next = splittedOrderItemIterator.next();
					if (next.getOrderedQuantity().equals(BigDecimal.ZERO)) {
						splittedOrderItemIterator.remove();
					}
				}
				// if there is no item left
				if (splittedOrderItemDatas.isEmpty()) {
					splittedOrderIterator.remove();
				}
			}

			CustorderSplitWrapper wrapper = form.getCustorderDTO().getSplitWrapper();
			CustorderEntity custorder = wrapper.getCustorder();

			// clear SDOI and SDO
			List<OrderEntity> orders = custorder.getOrders();
			Iterator<OrderEntity> ordersIterator = orders.iterator();

			while (ordersIterator.hasNext()) {
				OrderEntity order = ordersIterator.next();
				List<OrderItemEntity> orderItems = order.getOrderItems();
				Iterator<OrderItemEntity> orderItemIterator = orderItems.iterator();
				while (orderItemIterator.hasNext()) {
					OrderItemEntity next = orderItemIterator.next();
					if (next.getOrderedQuantity().equals(BigDecimal.ZERO)) {
						orderItemIterator.remove();
					}
				}
				// if there is no item left
				if (orderItems.isEmpty()) {
					ordersIterator.remove();
				}
			}

	}

	/**
	 * Creates new {@link OrderTruckTypeEntity} entities for the cloned order
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private static List<OrderTruckTypeEntity> createNewOrderTruckTypesForOrder(
			BeanUtilsBean beanUtilsBean, OrderEntity newOrder) throws IllegalAccessException,
			InvocationTargetException {

		List<OrderTruckTypeEntity> srcOrderVehicleTypes = newOrder.getOrderVehicleTypes();
		List<OrderTruckTypeEntity> dstOrderVehicleTypes = new ArrayList<OrderTruckTypeEntity>();

		for (OrderTruckTypeEntity srcTruck : srcOrderVehicleTypes) {
			OrderTruckTypeEntity dstTruck = new OrderTruckTypeEntity();
			beanUtilsBean.copyProperties(dstTruck, srcTruck);
			// This is very important because the trucks were also copied from another order
			dstTruck.setOrder(newOrder);
			dstOrderVehicleTypes.add(dstTruck);
		}

		return dstOrderVehicleTypes;
	}

	private ArchivingStatusBsEntity getArchivingStatusBsEntity(Long statusId) {
		return (ArchivingStatusBsEntity)this.getRefGenericsService().findBsEntity(ArchivingStatusBsEntity.class, statusId);
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.actions.webbestellung;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SalesAreaServiceLocalInterface;
import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.cust.web.forms.webbestellung.WebBestellungForm;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.OrdUnloadSchedInfoEntity;
import com.crh.logon.persistence.entities.views.WeborderShipToSoldToEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.actions.OrderTakingActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;

import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="cust_WebBestellungForm"
 *  path="/webbestellung/WebBestellungEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="WebBestellungTile.def"
 * @struts.action-forward name="emptyselect" path="WebBestellungShipToTile.def"
 */
@AdnTrace
public class WebBestellungEntryAction extends OrderTakingActionSupport {
	
	private DispoServiceLocalInterface dispoService;
	private ConfigServiceLocalInterface configService;
	private DispokarteServiceLocalInterface dispokarteService;

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(WebBestellungEntryAction.class);
	private DeliverySettingServiceLocalInterface deliverySettingService;

	private SalesAreaServiceLocalInterface salesAreaService;

	public ActionForward execute(ActionMapping mapping, 
			ActionForm form, 
			HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		try {
			Authorization.authorize(Authorization.WEB_ORDER);

			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			dispoService = serviceLocator.getLocalDispoService();
			configService = serviceLocator.getLocalConfigService();
			dispokarteService = serviceLocator.getLocalDispokarteService();

			setSelectedTab("webbestellung", request);
			WebBestellungForm helpForm = (WebBestellungForm) form;
			BestellungService bestellungService = getBestellungService();

			String compositeId = helpForm.getCompositeId();
			if (StringUtils.isEmpty(compositeId)) {
				List shipToList =  bestellungService.fetchWeborderShipToSoldToList();
				helpForm.setShiptoList(shipToList);
				if (shipToList != null && !shipToList.isEmpty()) {
					WeborderShipToSoldToEntity shipto = (WeborderShipToSoldToEntity) shipToList.get(0);
					compositeId = shipto.getCompositeId();
					helpForm.setCompositeId(compositeId);
				}
				else {
					return mapping.findForward("emptyselect");
				}
			}
		Authorization.authorize(Authorization.WEB_ORDER);

			setSelectedTab("webbestellung", request);

			WeborderShipToSoldToEntity selectedShipToAndSoldTo = helpForm.getSapSoldShip();
			String soldtoSapId = selectedShipToAndSoldTo.getSoldtoSapId();
			String shiptoSapId = selectedShipToAndSoldTo.getShiptoSapId();


			ShiptoEntity shipTo = dispokarteService.fetchAndInitializeShiptoBySapId(shiptoSapId);
			if (shipTo == null) {
				// Webbestellung is not allowed to create a new dispokarte. It
				// would not be possible to create an order anyway (without
				// shippingpoint set).
				MessageUtils.addActionWarning(request, "webbestellung.error.shipto.not.initialized.detailed", selectedShipToAndSoldTo.getShiptoName(), selectedShipToAndSoldTo.getShiptoSapId());

				throw new LogonException(Reason.RES_STRUTS_VALIDATION);
			}
			helpForm.setShipTo(shipTo, createTimeZoneForShipTo(shipTo));
			SapBpEntity soldTo = dispokarteService.fetchSoldtoSapBpBySapId(soldtoSapId);
			helpForm.setSoldTo(soldTo);

			if (helpForm.getShipTo().getOpen1From() == null
					|| helpForm.getShipTo().getOpen2From() == null
					|| helpForm.getShipTo().getOpen1To() == null
					|| helpForm.getShipTo().getOpen2To() == null) {
				// -------
				// Ship-To information is not prepared by Dispokarte yet,
				// an opening time is missing
				// -------
				LOG.error("Ship-To information is not prepared by Dispokarte yet, an opening time is missing");
				MessageUtils.addActionWarning(request,
						"webbestellung.error.shipto.not.initialized.detailed",
						selectedShipToAndSoldTo.getShiptoName(), selectedShipToAndSoldTo.getShiptoSapId());
				throw new LogonException(Reason.RES_STRUTS_VALIDATION);
			}

			CustorderEntity custOrder = createCustorder(soldTo, shipTo);

			customInitWebCustorder(shipTo, custOrder);

			helpForm.setCustorder(custOrder);

			loadOrderLists(shipTo, request, helpForm);

			setupMaterials(request, helpForm);

			boolean showProductionPlantName = false;
			boolean showNetPriceOnWebForm = false;
			List<DeliverySettingDTO> products = new ArrayList<DeliverySettingDTO>();
			List<DeliverySettingDTO> invalidMaterialList = new ArrayList<DeliverySettingDTO>();
			Collection<DeliverySettingDTO> l = helpForm.getProductList();
			for (DeliverySettingDTO deliverySetting: l) {
				products.add(deliverySetting);
				if (!helpForm.isMaterialValid(deliverySetting)) {
					invalidMaterialList.add(deliverySetting);
				}
				if (deliverySetting.getShowDeliveryPlantWebOrderFlag() != null
						&& deliverySetting.getShowDeliveryPlantWebOrderFlag()) {
					showProductionPlantName = true;
				}
				if (deliverySetting.getShowPriceInWebOrderFlag() != null
						&& deliverySetting.getShowPriceInWebOrderFlag()) {
					showNetPriceOnWebForm = true;
				}
			}

			// -----------------------
			// JSON for ExtJS widgets
			// Shared by all edited new orders (i.e. independent from the selected product)
			// -----------------------
			HashMap jsonData = new HashMap();
			final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
			jsonSerializer.exclude(DeliverySettingDTO.excludeEntities);

			jsonData.put("productList", jsonSerializer.serialize(products));
			jsonData.put("showProductionPlantName", showProductionPlantName);
			jsonData.put("showNetPriceOnWebForm", showNetPriceOnWebForm);
			jsonData.put("invalidMaterialList", new JSONSerializer().
					include("compositeIdentifier").
			        exclude("*").
			        serialize(invalidMaterialList));
			helpForm.setJson(jsonData);
			helpForm.populateForm();
			helpForm.initEditorFields();

			return mapping.findForward("success");

		}
		catch (LogonException logonException) {
			if (Reason.RES_STRUTS_VALIDATION.equals(logonException.getReason())) {
				return mapping.findForward("emptyselect");
			}
			else
				throw logonException;
		}
		catch (EJBException ejbException) {
			final Reason reason = ExceptionUtil.getLogonExceptionReason(ejbException, null);
			if (reason != null) {
				MessageUtils.addActionWarning(request, reason.getReason());
				return mapping.findForward("emptyselect");
			}
			else {
				throw ejbException;
			}
		}
	}

	private void setupMaterials(HttpServletRequest request, WebBestellungForm helpForm) {
		Locale locale = request.getLocale();
		ServiceLocator serviceLocator = getServiceLocator();
		deliverySettingService = serviceLocator.getLocalDeliverySettingService();
		SapBpEntity soldTo = helpForm.getSoldTo();
		ShiptoEntity shipTo = helpForm.getShipTo();
		List<ShiptoSareaEntity> shipToSalesAreas = dispokarteService.updateShiptoSalesAreas(shipTo,
				soldTo, false);
		DeliverySettingShiptoMap shipToMap = deliverySettingService.initDeliverySettingShiptoMapForWebOrder(
				soldTo, locale, shipToSalesAreas);
		shipToMap.validateSalesAreas();

		final Map<Object, List<ShiptoSalesAreaError>> invalidMap = shipToMap.getInvalidMap();
		helpForm.setInvalidMap(invalidMap);
		helpForm.setShipToMap(shipToMap);

		/**
		 * Display warning for ship to related errors
		 */
		if (InvalidMapSupport.containsErrorExcept(invalidMap, ShiptoEntity.class,
				ShiptoSalesAreaError.CUSTOMER_CREDIT_BLOCK)) {
			String soldToName = soldTo.getName1();
			String soldToSapId = soldTo.getSapId();
			SapBpEntity shiptoSapBp = shipTo.getShiptoSapBp();
			String shipToName = shiptoSapBp.getName1();
			Long shipToSapId = shiptoSapBp.getSapBpId();
			MessageUtils.addActionWarning(request,
					"webbestellung.error.shipto.not.initialized.detailed", shipToName + " ("
							+ shipToSapId + ")", soldToName + " (" + soldToSapId + ")");
		}

		List<DeliverySettingDTO> restrictionDeliverySettings = new ArrayList(shipToMap.getMaterialRestrictionDeliverySettingList());
		restrictionDeliverySettings.addAll(shipToMap.getServiceRestrictionDeliverySettingList());
		Collection<DeliverySettingDTO> filteredDeliverySettings = deliverySettingService.removeInvalidDeliverySettings(restrictionDeliverySettings);
		filteredDeliverySettings = sortDeliverySettings(filteredDeliverySettings);
		helpForm.setProductList(filteredDeliverySettings);
	}

	private Collection<DeliverySettingDTO> sortDeliverySettings(Collection<DeliverySettingDTO> deliverySettings){
		List<DeliverySettingDTO> sortedDeliverySettings = new ArrayList<DeliverySettingDTO>(deliverySettings);
		Collections.sort(sortedDeliverySettings, new Comparator<DeliverySettingDTO>() {
			public int compare(DeliverySettingDTO one, DeliverySettingDTO other) {
				return one.getTxt().compareTo(other.getTxt());
			}
		});

		return sortedDeliverySettings;
	}

	/**
	 * Loads the ongoing and completed orders on the web order screen. As the user needs to see the planned
	 * unloading time there is a postprocessing step to determine the expected unloading time.
	 * See also SchedulingUtils.getCalculatedTimeOfDelivery.
	 * @param shipTo
	 * @param request
	 * @param form
	 */
	public void loadOrderLists(ShiptoEntity shipTo, HttpServletRequest request,
			WebBestellungForm form) {
		ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
		BestellungServiceLocalInterface bestellungService = serviceLocator.getLocalBestellungService();
		Locale locale = request.getLocale();
		TimeZone userTimezone = getUserTimezone();
		List <DateTimeAwareOrderWrapper> orders = bestellungService.fetchDateTimeAwareOrdersByStatus(
				true,
				shipTo,
				false,
				locale,
				userTimezone);
		form.setOrderList(orders);

		List<DateTimeAwareOrderWrapper> completedOrders = bestellungService.fetchDateTimeAwareOrdersByStatus(
				true,
				shipTo,
				true,
				locale,
				userTimezone);
		form.setCompletedOrderList(completedOrders);

		List<OrdUnloadSchedInfoEntity> timeInfos = null;
		List<Long> ids = new ArrayList<Long>();
		if (orders != null && !orders.isEmpty()) {
			for (DateTimeAwareOrderWrapper order : orders) {
				//only calculate for those, which are not yet known
				Date unloadRealStart = order.getUnloadRealStart();
				if (unloadRealStart == null) {
					Long orderId = order.getOrderId();
					ids.add(orderId);
				}
			}
		}
		if (ids == null || ids.isEmpty()) {
			return;
		}
		try {
			timeInfos = bestellungService.getAssignmentTimeInfoForOrders(ids);
		}
		catch (Exception exception) {
			LOG.error(
					"An error happened when trying to retieve data for calculation of estimated unloading time.",
					exception);
			return;
		}

		Map<Long, Date> calculatedStartMap = new HashMap<Long, Date>();

		for (OrdUnloadSchedInfoEntity info : timeInfos) {
			Date calculatedTimeOfDelivery = SchedulingUtils.getCalculatedTimeOfDelivery(
					info.getDriverId(), info.getLastKnownPlannedStart(),
					info.getLastKnownRealStart(), info.getPlannedStartUnload(),
					info.getPlannedStartLoad(), info.getAssignTrStatusCd(), dispoService,
					configService);
			Long orderId = info.getOrderId();
			calculatedStartMap.put(orderId, calculatedTimeOfDelivery);
		}

		if (orders != null && !orders.isEmpty() && calculatedStartMap != null && !calculatedStartMap.isEmpty()) {
			for (DateTimeAwareOrderWrapper order: orders ) {
				Long orderId = order.getOrderId();
				Date planned = calculatedStartMap.get(orderId);
				if (planned != null) {
					order.setUnloadPlannedStart(planned);
				}
			}
		}
	}

}

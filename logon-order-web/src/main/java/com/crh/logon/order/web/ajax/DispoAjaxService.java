/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJBException;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.business.services.utils.AssignmentUtils;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.DispoDriverBreakBean;
import com.crh.logon.business.services.beans.GoogleMapsDistanceMatrixResultBean;
import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.GoogleMapsService;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.utils.AssignmentModificationDTO;
import com.crh.logon.business.services.utils.AssignmentPartWrapper;
import com.crh.logon.business.services.utils.AssignmentWrapper;
import com.crh.logon.business.services.utils.CustOrderWrapper;
import com.crh.logon.business.services.utils.CustorderItemSumQtyWrapper;
import com.crh.logon.business.services.utils.IndexedObject;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.TruckAndAssignmentModificationDTO;
import com.crh.logon.business.services.utils.TruckModificationDTO;
import com.crh.logon.business.services.utils.TruckWrapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.PackageTypeTrspCatMapping;
import com.crh.logon.order.web.forms.dispo.DispoForm;
import com.crh.logon.order.web.forms.dispo.MessagesEditForm;
import com.crh.logon.order.web.utils.comparator.OrderWrapperComparator;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.PoolTxPK;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TrafficJamEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity;
import com.crh.logon.persistence.entities.views.CustorderViewEntity;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoDriverBreakListEntity;
import com.crh.logon.persistence.entities.views.DispoExtOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.TransferContext;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 *
 * The purpose of this class is to make business functions available to
 * Ajax components.
 *
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 *
 * @adnbus.service name="DispoAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.beans.DispoDriverBreakBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
  */
@AdnTrace
public class DispoAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispoAjaxService.class);

	// Two keys for the map of truck and trailer transport category maps,
	// see getTrspCategoriesForPackageTypes()
	public static final String MAP_KEY_TRUCK = "truck";
	public static final String MAP_KEY_TRAILER = "trailer";

	/*
	 * LOGON-8967
	 * Remove the Time Grid from Dispo Form, no Ajax call occured
	 */
	@Deprecated
	private void setTimegrid(int timegrid, HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		dispoForm.setTimegrid(timegrid);
	}

	public void setDispoDate(String date, HttpSession session) throws ParseException {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		dispoForm.setStringDate(date);
	}

	/**
	 * Sets the given custOrderNumber to the sessions dispoForm.
	 * (This method is called from js.)
	 *
	 * @param custorderNumber - the number os the customer order to set
	 * @param session - the session which holds the dispoForm
	 */
	public void setCustOrderNumber(Long custorderNumber, HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		dispoForm.setCustorderNumber(custorderNumber);
	}
	
	/**
	 * Sets the given orderNumber to the sessions dispoForm.
	 * (This method is called from js.)
	 *
	 * @param orderNumber - the number is the single delivery order to set
	 * @param session - the session which holds the dispoForm
	 */
	public void setOrderNumber(Long orderNumber, HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		dispoForm.setOrderNumber(orderNumber);
	}
	
	// LOGON-8967
	// Remove the Visual from Dispo Form, no Ajax call occured
	/**
	 * Sets the given new value for Visual filter in the DispoForm instance.
	 * (This method is called from js.)
	 *
	 * @param selectedFilterForVisual - the new value for Visual filter to set
	 * @param session - the session which holds the dispoForm
	 */
	@Deprecated
	private void setVisualFilterValue(int selectedFilterForVisual, HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		dispoForm.setSelectedFilterForVisual(selectedFilterForVisual);
	}

	/**
	 * Fetches the list of external and LOGON orders for the Truck Scheduling screen.
	 * The external and LOGON orders are wrapped into CustOrderWrappers.
	 */
	public List<CustOrderWrapper> getOrderListWithLocalEJB(HttpServletRequest request) {

		Locale userLocale = RequestUtils.getUserLocale(request, null);
		List<CustOrderWrapper> customerOrders = null;

		Authorization.authorize(Authorization.DISPATCHING_R);
		HttpSession session = request.getSession();
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		boolean isDeliveringPlantsSelectionEmpty = dispoForm.isDeliveringPlantsSelectionEmpty();
		Set<String> selectedDeliveringPlants = dispoForm.getSelectedDeliveringPlants();
		Collection<String> plantCodes = (isDeliveringPlantsSelectionEmpty ? null
				: selectedDeliveringPlants);
		Long orderNumber = dispoForm.getOrderNumber();
		boolean hideScheduledCustorders = dispoForm.isHideScheduledCustorders();
		boolean hideClosedCustorders = dispoForm.isHideClosedCustorders();
		boolean hideTrucksAndOrderOfPartners = dispoForm.isHideTrucksAndOrderOfPartners();

		TimeZone userTimeZone = getUserTimeZone();
		try {
			ConfigBean configBean = getConfigBean();
			DispoServiceLocalInterface dispoServiceLocal = LocalServices.getDispoService(configBean);

			TransferContext localContext = LocalServices.createLocalContext();
			Locale locale = request.getLocale();
			Date selectedDate = (dispoForm.getDate() != null) ? dispoForm.getDate() : new Date();
			List<PoolTxEntity> pools = dispoForm.getPools();
			int selectedPoolIndex = dispoForm.getSelectedPool();
			PoolTxEntity selectedPoolText = pools.get(selectedPoolIndex);
			PoolTxPK keyOfSelectedPool = selectedPoolText.getPoolTxPK();
			Long selectedPoolIdentifier = keyOfSelectedPool.getPoolId();
			Set<Long> selectedCarriers = dispoForm.getSelectedCarriers();
			customerOrders = dispoServiceLocal.getOrderListLocal(
					localContext,
					locale,
					orderNumber,
					hideScheduledCustorders,
					hideClosedCustorders,
					selectedDate,
					selectedPoolIdentifier,
					new ArrayList<Long>(selectedCarriers),
					plantCodes,
					Authorization.isAllowed(Authorization.DISPATCHING_POOL_W),
					userLocale,
					Authorization.isAllowed(Authorization.DISPATCHING_CREATE_EXT_ORD),
					userTimeZone,
					hideTrucksAndOrderOfPartners);
		}
		catch (NamingException namingException) {
			LOG.error(Reason.RES_ERROR_LOCALEJB_NOT_FOUND.toString(), namingException);
			throw new LogonException(namingException, Reason.RES_ERROR_LOCALEJB_NOT_FOUND);
		}

		//--- Convert all Date objects to the correct milliseconds in UTC for the Javascript client ---//
		for (CustOrderWrapper customerOrder : customerOrders) {
			CustorderViewEntity customerOrderView = customerOrder.getCustorderViewEntity();
			// In case of external order, there is no customer order. 
			if (customerOrderView != null) {
				Date deliveryFromDate = customerOrderView.getDeliveryFromDat();
				customerOrderView.setDeliveryFromDat(userTimeZone.shiftToUTC(deliveryFromDate));
				Date deliveryToDate = customerOrderView.getDeliveryToDat();
				customerOrderView.setDeliveryToDat(userTimeZone.shiftToUTC(deliveryToDate));
			}

			List<OrderWrapper> orderWrappers = customerOrder.getOrderWrappers();
			for (OrderWrapper orderWrapper : orderWrappers) {
				Object order = orderWrapper.getOrder();
				if (order instanceof DispoOrderListEntity) {
					DispoOrderListEntity entity = (DispoOrderListEntity) order;
					Date deliveryFromDate = entity.getDeliveryFromDat();
					entity.setDeliveryFromDat(userTimeZone.shiftToUTC(deliveryFromDate));
					Date deliveryToDate = entity.getDeliveryToDat();
					entity.setDeliveryToDat(userTimeZone.shiftToUTC(deliveryToDate));
				}
				else {
					DispoExtOrderListEntity entity = (DispoExtOrderListEntity) order;
					Date deliveryFromDate = entity.getDeliveryFromDat();
					entity.setDeliveryFromDat(userTimeZone.shiftToUTC(deliveryFromDate));
					Date deliveryToDate = entity.getDeliveryToDat();
					entity.setDeliveryToDat(userTimeZone.shiftToUTC(deliveryToDate));
				}
				Date lastSwitchingTimeArrival = orderWrapper.getLastSwitchingTimeArr();
				orderWrapper.setLastSwitchingTimeArr(userTimeZone.shiftToUTC(lastSwitchingTimeArrival));
				Date lastSwitchingTimeDeparture = orderWrapper.getLastSwitchingTimeDep();
				orderWrapper.setLastSwitchingTimeDep(userTimeZone.shiftToUTC(lastSwitchingTimeDeparture));
				Date firstSwitchingTimeArrival = orderWrapper.getFirstSwitchingTimeArr();
				orderWrapper.setFirstSwitchingTimeArr(userTimeZone.shiftToUTC(firstSwitchingTimeArrival));
				Date firstSwitchingTimeDeperture = orderWrapper.getFirstSwitchingTimeDep();
				orderWrapper.setFirstSwitchingTimeDep(userTimeZone.shiftToUTC(firstSwitchingTimeDeperture));
			}
			
			// Sort sub-orders to have correct grouping in Scheduling screen
			Collections.sort(orderWrappers, new OrderWrapperComparator());
		}

		return customerOrders;
    }	

	/**
	 * Changes the truck priority value to the given one.
	 *
	 * @param truckId - the truck in which the priority must be changed
	 * @param priority - the new priority value
	 */
	public void changeTruckPriority(Long truckId, Long priority) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		TruckAdminService truckAdminService = getServiceLocator().getTruckAdminService();
		TruckEntity truck = truckAdminService.fetchTruck(truckId);
		truck.setPriority(priority);
		truckAdminService.updateTruckSimple(truck, true);
	}

	public void setOrderCommCarrier(String orderNumbers, Long commCarrierId) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		List<Long> orderNumberList = splitOrdernumbers(orderNumbers);
		getServiceLocator().getDispoService().setOrderCommCarrier(orderNumberList, commCarrierId);
    }

	public void setOrderExecCarrier(String orderNumbers, Long execCarrierId) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		List<Long> orderNumberList = splitOrdernumbers(orderNumbers);
		getServiceLocator().getDispoService().setOrderExecCarrier(orderNumberList, execCarrierId);
    }

	public void setOrderPool(String orderNumbers, Long poolId) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		List<Long> orderNumberList = splitOrdernumbers(orderNumbers);
		getServiceLocator().getDispoService().setOrderPool(orderNumberList, poolId);
    }

	/**
	 * @param orderNumbers
	 * @return
	 */
	private List<Long> splitOrdernumbers(String orderNumbers) {
		String[] split = orderNumbers.split(",");
		List<Long> ordernumbers = new ArrayList<Long>();
		for (int i = 0; i < split.length; i++) {
			ordernumbers.add(new Long(split[i]));
		}
		return ordernumbers;
	}

	public void transmitToCarrier(Long orderNumber) {
		Authorization.authorize(Authorization.DISPATCHING_POOL_W);
		getServiceLocator().getDispoService().transmitToCarrier(orderNumber);
    }

    public void acceptOrderCarrier(Long orderNumber, Boolean poolFallback, HttpServletRequest request){
		Authorization.authorize(Authorization.DISPATCHING_W);
		DispoOrderListEntity dole = getServiceLocator().getDispoService().fetchOrderForDispo(
				orderNumber, RequestUtils.getUserLocale(request, null).getLanguage());
		if (dole.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
			getServiceLocator().getDispoService().carrierAcceptOrder(orderNumber);
		}
		else if (poolFallback
				&& (dole.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL)) {
			acceptOrderPool(orderNumber, request);
		}
    }

    public void acceptOrderPool(Long orderNumber, HttpServletRequest request){
		Authorization.authorize(Authorization.DISPATCHING_POOL_W);
		DispoOrderListEntity dole = getServiceLocator().getDispoService().fetchOrderForDispo(
				orderNumber, RequestUtils.getUserLocale(request, null).getLanguage());
		if (dole.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) {
			getServiceLocator().getDispoService().poolAcceptOrder(orderNumber);
		}
    }

    private void cancelOrderPool(Long orderNumber, String reason, HttpServletRequest request){
		Authorization.authorize(Authorization.DISPATCHING_POOL_W);
		DispoOrderListEntity dole = getServiceLocator().getDispoService().fetchOrderForDispo(
				orderNumber, RequestUtils.getUserLocale(request, null).getLanguage());
		if ((dole.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK)
				|| (dole.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL)) {
			getServiceLocator().getDispoService().rejectByPool(orderNumber, reason);
		}
    }

    public void cancelOrderCarrier(Long orderNumber, String reason, Boolean poolFallback, HttpServletRequest request){
		Authorization.authorize(Authorization.DISPATCHING_W);
		DispoOrderListEntity dole = getServiceLocator().getDispoService().fetchOrderForDispo(
				orderNumber, RequestUtils.getUserLocale(request, null).getLanguage());
		if ((dole.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK)
				|| (dole.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER)) {
			getServiceLocator().getDispoService().rejectByCarrier(orderNumber, reason);
		}
		else if (poolFallback) {
			cancelOrderPool(orderNumber, reason, request);
		}
	}
    
	/**
	 * Fetches trucks (TruckWrapper) and assignments (AssignmentWrapper) on them.
	 *
	 * @param filterTruckIds If not empty only fetch the trucks with the given truck IDs.
	 * @param request A valid HttpServletRequest instance.
	 *
	 * @return A list of arrays with this structure:
	 * <ul>
	 * <li>list of trucks</li>
	 * <li>list of assignments</li>
	 * <li>list of truck ID-s</li>
	 * </ul>
	 */
	public TruckAndAssignmentModificationDTO getTruckAndAssignmentListWithLocalEJB(List<Long> filterTruckIds, HttpServletRequest request) {
		return getTruckAndAssignmentModificationListWithLocalEJB(filterTruckIds, null, null, null, request);
	}

	/**
	 * Returns the list of CustorderItemSumQtyWrapper objects to display the material popup.
	 * @param custOrderId
	 * @param request
	 */
   	public List<CustorderItemSumQtyWrapper> getDetailedMaterialInfo(Long custOrderId, HttpServletRequest request){
		Authorization.authorize(Authorization.DISPATCHING_R);
		final Locale userLocale = RequestUtils.getUserLocale(request, null);
		final String langCd = userLocale.getLanguage();
		DispoService dispoService = getServiceLocator().getDispoService();
		List<CustorderItemSumQtyWrapper> list = dispoService.fetchCustorderItemSumQtyEntities(
				custOrderId, langCd);
		return list;
	}

	/**
     * Creates a normal assignment, but for which the starting shippingpoint is the carrier stock shippingpoint of the carrier
     * instead of the shippingpoint defined on the order. As far the SAP expects the shippingpoint to be the original
     * one, and not the carrier stock shippingpoint, this exchange is only signed on the assignment, the order will still
     * contain the original shippingpoint as an attribute.
     * @param truckId
     * @param orderNr
     * @param startMinutes
     * @param loadDuration
     * @param unloadDuration
     * @param isExternalOrder
     * @param request
     */
	public void createCarrierStockAssignment(int truckId, int orderNr, int startMinutes,
			int loadDuration, int unloadDuration, boolean isExternalOrder,
			HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		OrderEntity order = getServiceLocator().getDispoService().fetchOrderForEdit(
				(long) orderNr);
		if (order == null) {
			return;
		}
		if (!LogonUtil.isServingByCarrierStockPossible(order)) {
			LOG.error("Carrier stock assignment cannot be created for a carrier, which does not have a bag stock shippingpoint");
			return;
		}

		final ShippingpointEntity bagStockShippingpoint = order.getCommCarrier().getBagStockShippingpoint();

		final GoogleMapsService googleMapsService = getServiceLocator().getGoogleMapsService();

		GoogleMapsDistanceMatrixResultBean distanceMatrixResult = null;

		// In case lnf coords are known use those instead of the shipto coordinates.
		if (order.getLnfCoordLat() != null && order.getLnfCoordLat() > 0 &&
				order.getLnfCoordLon() != null && order.getLnfCoordLon() > 0) {
			final Double lnfCoordLat = order.getLnfCoordLat();
			final Double lnfCoordLon = order.getLnfCoordLon();

			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(lnfCoordLat, lnfCoordLon, bagStockShippingpoint);
		}
		else {
			// FIXME (emoke Nov 30, 2011): Split carrier to executional and commercial carrier! executional is not handled.
			distanceMatrixResult = googleMapsService.getCarrierStockAssignmentDistanceMatrixResult(
					order.getShiptoSapBp(), bagStockShippingpoint,
					order.getDeliveryType());
		}

		if (distanceMatrixResult == null) {
			LOG.error("Carrier stock assignment creation failed. Could not determine duration and distance for order number "
					+ order.getOrderNumber()
					+ " order id "
					+ order.getOrderId()
					+ ". distanceMatrixResult is null.");
			return;
		}

		final String status = distanceMatrixResult.getStatus();
		if (!Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK.equals(status)) {
			LOG.error("Carrier stock assignment creation failed. Could not determine duration and distance for order number "
					+ order.getOrderNumber()
					+ " order id "
					+ order.getOrderId()
					+ ". distnaceMatrix response status not OK. status: " + status);
			return;
		}
		else if (distanceMatrixResult.getDurationMin() == null || distanceMatrixResult.getDistance() == null) {
			LOG.error("Carrier stock assignment creation failed. Could not determine duration and distance for order number "
					+ order.getOrderNumber()
					+ " order id "
					+ order.getOrderId()
					+ ". distanceMatrixResult.getDurationSec() or distanceMatrixResult.getDistance() is null.");
			return;
		}
		else {
			final int durationMin = distanceMatrixResult.getDurationMin().intValue();

			final int distanceMeter = distanceMatrixResult.getDistance().intValue();

			createAssignment(truckId, orderNr, startMinutes, loadDuration, durationMin,
					unloadDuration, isExternalOrder, request);
			getServiceLocator().getOrderService().setLoadingAtCarrierStockShippingpoint(
					order.getOrderId(), distanceMeter, durationMin);
		}
    }

    /**
     * Simple loading of the truck at a carrier stock shippingpoint.
     *
     * @param assignments The id-s of the assignments.
     * @param request Valid HttpServletRequest instance.
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
	public TruckAndAssignmentModificationDTO loadAtCarrierStock(int[] assignments, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoService ds = getServiceLocator().getDispoService();
		ds.loadAtCarrierStock(assignments);

		final List<Long> assignmentIds = new ArrayList<Long>();
		for (int i = 0; i < assignments.length; i++) {
			final Long assignmentId = new Long(assignments[i]);
			assignmentIds.add(assignmentId);
		}

		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = ds.fetchTruckIdsForAssignments(assignmentIds);
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
    }

    /**
     * Creates a new assignment on the given truck with the order.
	 *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
    public TruckAndAssignmentModificationDTO createAssignment(int truckId, int orderNr, int startMinutes,
    				int loadDuration, int trDuration, int unloadDuration, boolean isExternalOrder,
    				HttpServletRequest request){
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoForm dispoForm = (DispoForm) request.getSession().getAttribute(
				"order_dispo_DispoForm");

		final Calendar c = calculateStartTime(dispoForm,
				startMinutes, loadDuration, trDuration, isExternalOrder, orderNr);
		if (c == null) {
			LOG.error("Failed to create assignment for truck with ID: " + truckId);
			return null;
		}

		final Long truckIdLong = Long.valueOf(truckId);
		final Long orderNrLong = Long.valueOf(orderNr);

		if (isExternalOrder) {
			getServiceLocator().getOrderService().createAssignmentForExternalOrder(truckIdLong,
					orderNrLong, c.getTime(), getUserTimeZone(),
					loadDuration, trDuration, unloadDuration);
		}
		else {
			getServiceLocator().getOrderService().createAssignmentForTruckWithOrderDispatched(truckIdLong,
					orderNrLong, c.getTime(), getUserTimeZone(),
					loadDuration, trDuration, unloadDuration);
		}

		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = new ArrayList<Long>(1);
		affectedTruckIds.add(truckIdLong);

		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);		
    }

	private Calendar calculateStartTime(
			DispoForm dispoForm,
			int startMinutes,
			int loadDuration,
			int trDuration,
			boolean isExternalOrder,
			long orderNr) {

		final DispoService ds = getServiceLocator().getDispoService();

		Calendar c = getUserTimeZone().asCalendar(dispoForm.getDate());
		c.add(Calendar.HOUR, startMinutes/60);
		c.add(Calendar.MINUTE, startMinutes%60);
		c.add(Calendar.MINUTE, loadDuration);
		c.add(Calendar.MINUTE, trDuration);

		Date deliveryTime = c.getTime();

		if (!isExternalOrder) {
			OrderEntity order = ds.fetchOrderForEdit(orderNr);
			if (order == null) {
				return null;
			}
			if (deliveryTime.before(order.getDeliveryFromDat())) {
				deliveryTime = order.getDeliveryFromDat();
			}
			else if (deliveryTime.after(order.getDeliveryToDat())){
				deliveryTime = order.getDeliveryToDat();
			}
		}
		else {
			ExtOrderEntity order = ds.fetchExtOrderForEdit(orderNr);
			if (order == null) {
				return null;
			}
			if (deliveryTime.before(order.getDeliveryFromDat())) {
				deliveryTime = order.getDeliveryFromDat();
			}
			else if (deliveryTime.after(order.getDeliveryToDat())){
				deliveryTime = order.getDeliveryToDat();
			}
		}

		c.setTime(deliveryTime);
		c.add(Calendar.MINUTE, -loadDuration);
		c.add(Calendar.MINUTE, -trDuration);

		return c;
	}

    /**
     * Moves an assignment either on a truck (by modifying its delivery)
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
    public TruckAndAssignmentModificationDTO moveAssignment(long truckId, long assignmentId, Long orderId, int minutes, HttpServletRequest request, Long externalOrderId){
		Authorization.authorize(Authorization.DISPATCHING_W);

		final DispoService dispoService = getServiceLocator().getDispoService();
		final AssignmentEntity originalAssignment = dispoService.fetchAssignmentForEdit(assignmentId);
		final Long originalTruckId = originalAssignment.getTruck().getTruckId();

		AssignmentEntity assignment = getServiceLocator().getOrderService().moveAssignment(truckId,
				assignmentId);

		DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_dispo_DispoForm");
		Calendar c = getUserTimeZone().asCalendar(dispoForm.getDate());
		c.add(Calendar.HOUR, minutes / 60);
		c.add(Calendar.MINUTE, minutes % 60);

		getServiceLocator().getOrderService().setAssignmentDeliveryTime(
				assignment.getAssignmentId(), orderId, c.getTime(), externalOrderId);

		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = new ArrayList<Long>(1);
		affectedTruckIds.add(truckId);
		if (truckId != originalTruckId.longValue()) {
			affectedTruckIds.add(originalTruckId);
		}

		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
    }

    /**
     * moves the assingment in time
     * @param assignmentId
     * @param minutes new delivery time (minutes since startHour)
     */
    public void setAssignmentDeliveryTimeMinutes(long assignmentId, Long orderId, int minutes, HttpServletRequest request){
		Authorization.authorize(Authorization.DISPATCHING_W);
		DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_dispo_DispoForm");
		Calendar c = getUserTimeZone().asCalendar(dispoForm.getDate());
		c.add(Calendar.HOUR, minutes / 60);
		c.add(Calendar.MINUTE, minutes % 60);

		Date deliveryTime = c.getTime();

		OrderEntity order = getServiceLocator().getDispoService().fetchOrder(orderId);
		if (order == null) {
			return;
		}
		if (deliveryTime.before(order.getDeliveryFromDat())) {
			deliveryTime = order.getDeliveryFromDat();
		}
		else if (deliveryTime.after(order.getDeliveryToDat())) {
			deliveryTime = order.getDeliveryToDat();

		}
		getServiceLocator().getOrderService().setAssignmentDeliveryTime(assignmentId, orderId,
				deliveryTime, null);
    }

    /**
     * merges the mergedAssingmet into the targetAssignment, by moving all its load, drive-to and uload
     * assignment parts into the other assignment.The parts of the mergerd assingment will be inserted directly
     * after the parts associated with the order deliveryAfterOrderId in the target assignment.
     * If deliveryAfterOrderId is null, The parts of mergedAssignment should be inserted before the parts
     * from all other assignments.
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
	public TruckAndAssignmentModificationDTO mergeAssignments(Long mergedAssignmentId, Long targetAssignmentId,
			Long deliveryAfterOrderId, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);

		final List<Long> assignmentIds = new ArrayList<Long>();
		assignmentIds.add(mergedAssignmentId);
		assignmentIds.add(targetAssignmentId);
		final DispoService dispoService = getServiceLocator().getDispoService();

		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = dispoService.fetchTruckIdsForAssignments(assignmentIds);

		final OrderService orderService = getServiceLocator().getOrderService();
		AssignmentEntity ass = orderService.mergeAssignments(mergedAssignmentId, targetAssignmentId, deliveryAfterOrderId);
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(ass);
		for(OrderEntity oe : orders) {
			oe.setNoPdaOrRailEventsFlag(false);
		}
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
	}

	/**
	 * Ajax service which checks the assignment with the given id, if the conditions
	 * of reroutable state are fulfilled or not. Quantities are not yet checked here.
	 * @param assignmentId
	 * @return reroutable
	 */
	public boolean checkAssignmentReroutable(long assignmentId) {
		Authorization.authorize(Authorization.DISPATCHING_R);

		DispoService dispoService = getServiceLocator().getDispoService();
		try {
			return dispoService.checkAssignmentReroutable(assignmentId);
		}
		catch (EJBException e) {
			LOG.error("conditions of examining assignment routable state is not possible", e);
			return false;
		}
	}

	/**
	 * Reroutes the assignment, so the still connecting order(s) in the assignment
	 * will be replaced by ones given in orderIds, if all conditions apply. A new
	 * assignment entity is created, as orders and assignments goes through their
	 * workflow.
	 *
	 * @param assignmentId
	 * @param orderIds
	 *
	 * @return return an error code depending on the success/error of rerouting. See
	 * <code>Constant.REROUTING_*</code>.
	 */
	public int rerouteAssignment(Long assignmentId, Long[] orderIds){
		Authorization.authorize(Authorization.DISPATCHING_W);
		Authorization.authorize(Authorization.SHIPPING_W);

		DispoService ds = getServiceLocator().getDispoService();

		return ds.rerouteAssignment(assignmentId, orderIds);
	}

	/**
     * Transmits selected assignments to the driver.
     *
     * @param selection list of selected assignment ids
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
    public TruckAndAssignmentModificationDTO transmitToDriver(int[] selection, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoService ds = getServiceLocator().getDispoService();
		final List<Long> assignmentIds = new ArrayList<Long>();
		for (int i = 0; i < selection.length; i++) {
			final Long assignmentId = new Long(selection[i]);
			assignmentIds.add(assignmentId);
			ds.transmitToDriver(assignmentId, Constant.WEIGH_SYSTEM_SP_NONE,
					Constant.WEIGH_SYSTEM_LP_LOGON);
		}
		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = ds.fetchTruckIdsForAssignments(assignmentIds);
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
    }

    /**
     * Acts as if the truck would enter shippingpoint.
     *
     * @param selection list of selected assignment ids.
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
	public TruckAndAssignmentModificationDTO enterShippingpoint(int[] selection, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoService ds = getServiceLocator().getDispoService();
		final ArrayList<Long> assignmentIdList = new ArrayList<Long>();
		for (int i = 0; i < selection.length; i++) {
			assignmentIdList.add(new Long(selection[i]));
		}
		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = ds.enterLoadingSiteAndFetchTruckIds(assignmentIdList);
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
    }

    /**
     * Removes the selected assignments, placing the orders on the order list again
     *
     * @param selection  list of assignmets ids
     * @param noteCancel cancellation nopte for assignments
     * @param orders List of Strings with order numbers
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
	public TruckAndAssignmentModificationDTO cancelAssignment(int[] selection, String noteCancel, List orders,
			HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoService ds = getServiceLocator().getDispoService();
		final List<Long> assignmentIds = new ArrayList<Long>();
		for (int i = 0; i < selection.length; i++) {
			final Long assignmentId = new Long(selection[i]);
			assignmentIds.add(assignmentId);
			ds.cancelAssignment(assignmentId, noteCancel, orders, false);
		}

		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = ds.fetchTruckIdsForAssignments(assignmentIds);
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
    }

    /**
     * Sets the return shipping point for the prev assignment and removes an eventual
     * pre_load assignmet part for this assignment.
     *
     * @param prevAssignmentId  id of the previous assignment
     * @param thisAssignmentId id of this assignment
     */
    public void setPrevAssignmentReturnShippingpoint(long prevAssignmentId, long thisAssignmentId,
			HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final OrderService os = getServiceLocator().getOrderService();
		final DispoService dispoService = getServiceLocator().getDispoService();

		/** New duration of the trip in minutes (uncorrected) */
		final long newDurationMin = dispoService.calculateDurationForSetPrevAssignmentReturnShippingpoint(
				prevAssignmentId, thisAssignmentId, request.getLocale());

		if (newDurationMin <= 0) {
			LOG.error("Setting of return shippingpoint failed. Could not calculate duration for prevAssignmentId: "
					+ prevAssignmentId + ", thisAssignmentId: " + thisAssignmentId);
			return;
		}

		os.setPrevAssignmentReturnShippingpoint(prevAssignmentId, thisAssignmentId, newDurationMin);
    }

    /**
     * Sets the position of a pre_load assignment part of this assignment to correspond to the
     * finished assingment part of the prev assignment. Creates the pre assignment part if it
     * doesn't exist.
     *
     * @param prevAssignmentId  id of the previous assignment
     * @param thisAssignmentId id of this assignment
     */
    public void setAssignmentPrePosition(long prevAssignmentId, long thisAssignmentId) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final OrderService os = getServiceLocator().getOrderService();
		final DispoService dispoService = getServiceLocator().getDispoService();

		/** New duration of the trip in minutes (uncorrected) */
		final long newDurationMin = dispoService.calculateDurationForSetAssignmentPrePosition(
				prevAssignmentId, thisAssignmentId);

		if (newDurationMin <= 0) {
			LOG.error("Setting of assignment preposition failed. Could not calculate duration for prevAssignmentId: "
					+ prevAssignmentId + ", thisAssignmentId: " + thisAssignmentId);
			return;
		}

		os.setAssignmentPrePosition(prevAssignmentId, thisAssignmentId, newDurationMin);
    }

    /**
     * Sets the status of the return assignment to returned with the current timestamp.
     *
     * @param selectionReturn list of assignment IDs having just the return selected.
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
	public TruckAndAssignmentModificationDTO setReturned(int[] selectionReturn, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoService ds = getServiceLocator().getDispoService();
		final List<Long> assignmentIds = new ArrayList<Long>();
		for (int i = 0; i < selectionReturn.length; i++) {
			final Long assignmentId = new Long(selectionReturn[i]);
			assignmentIds.add(assignmentId);
			ds.setReturned(assignmentId);
		}

		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = ds.fetchTruckIdsForAssignments(assignmentIds);
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
    }

    /**
     * Sets the status of the order to unloaded with the current timestamp.
     *
     * @param selectionAssignment list of assignments having just the transport selected.
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
	public TruckAndAssignmentModificationDTO setUnloaded(int[] selectionAssignment, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoService ds = getServiceLocator().getDispoService();
		final List<Long> assignmentIds = new ArrayList<Long>();
		for (int i = 0; i < selectionAssignment.length; i++) {
			final Long assignmentId = new Long(selectionAssignment[i]);
			assignmentIds.add(assignmentId);
			ds.setTruckUnloaded(assignmentId);
		}
		// These trucks should be refreshed on the UI.
		final List<Long> affectedTruckIds = ds.fetchTruckIdsForAssignments(assignmentIds);
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
    }

    /**
     * Sets the status of the order to arrived at shipto with the current timestamp.
     *
     * @param selectionAssignment list of assignments having just the transport selected.
     *
     * @return A list with one truck and assignment data, on which the assignment is created.
     * This data can be used to refresh only the changed truck and assignment on the UI.
     */
	public TruckAndAssignmentModificationDTO setAtShipto(int[] selectionAssignment, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		final DispoService ds = getServiceLocator().getDispoService();
		final ArrayList<Long> assignmentIdList = new ArrayList<Long>();
		for (int i = 0; i < selectionAssignment.length; i++) {
			assignmentIdList.add(new Long(selectionAssignment[i]));
		}
		final List<Long> affectedTruckIds = ds.enterUnloadingSiteAndFetchTruckIds(assignmentIdList);
		return getTruckAndAssignmentListWithLocalEJB(affectedTruckIds, request);
	}

    public ListRange fetchShippingpointData(
    		int start,
    		int count,
    		String query) {
		Authorization.authorize(Authorization.DISPATCHING_R);

		DispoService dispoService = getServiceLocator().getDispoService();
		List list = dispoService.fetchShippingpoints(start, count, query);

		ListRange listRange = new ListRange();
		listRange.setData(list.toArray());
		listRange.setTotalSize(dispoService.countShippingpoints(query));

		return listRange;
	}

    public List fetchCurrentCarriers(HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_R);

		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");

		DispoService dispoService = getServiceLocator().getDispoService();
		return dispoService.fetchCarriersForPool(dispoForm.getPools().get(
				dispoForm.getSelectedPool()).getPoolTxPK().getPoolId());
	}

    public ListRange fetchShipToKundeList(
    		int start,
    		int count,
			String query) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		ListRange listRange = new ListRange();

		DispoService dispoService = getServiceLocator().fetchDispoService();
		listRange.setData(dispoService.fetchShipToKunde(start, count, query).toArray());
		listRange.setTotalSize(dispoService.countShipToKunde(query));

		return listRange;
	}

  	/**
	 * Assigns the pre-existing load of the assingment's truck to the assignment
	 */
	public void takeoverLoad(Long assignmentId) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		getServiceLocator().getOrderService().takeoverLoad(assignmentId);
    }

	/**
	 * Creates or modifies a DriverBreakEntity.
	 * Creating will occur if the default lunch break is modified.
	 * All other cases of creation are handled by the DriverBreak popup.
	 *
	 * @param driverBreakId
	 * @param driverId
	 * @param startMinute
	 * @param duration
	 * @param request
	 */
	public void saveDriverBreak(Long driverBreakId, Long driverId,
			Integer startMinute, Integer duration, boolean isLunchBreak,
			HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_dispo_DispoForm");
		Calendar c = getUserTimeZone().asCalendar(dispoForm.getDate());
		c.add(Calendar.MINUTE, startMinute.intValue());
		Date startDat = c.getTime();
		c.add(Calendar.MINUTE, duration.intValue());

		getServiceLocator().getDispoService().modifyDriverBreakTimes(
				driverBreakId, driverId, startDat, c.getTime(), isLunchBreak);
	}

	/**
	 * Sets startHour and hourSpan for dispo display. If an input parameter is null, then it's not set.
	 * @param startHour
	 * @param hourSpan
	 */
	public void setStartHourAndHourSpan(Integer startHour, Integer hourSpan, HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		if (startHour != null) {
			dispoForm.setStartHour(startHour);
		}
		if (hourSpan != null) {
			dispoForm.setHourSpan(hourSpan);
		}
	}

    public ListRange searchMessages(
    		int skip,
    		int count,
    		HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_R);

		MessagesEditForm messagesForm = (MessagesEditForm) session.getAttribute("order_dispo_MessagesEditForm");

		DispoService dispoService = getServiceLocator().getDispoService();
		List list = dispoService.fetchDriverMessages(count, skip, messagesForm.getDriverId(),
				messagesForm.getDay());

		ListRange result = new ListRange();
		result.setData(list.toArray());
		if (list.size() < count) {
			result.setTotalSize(skip + list.size());
		}
		else {
			result.setTotalSize(dispoService.countDriverMessages(messagesForm.getDriverId(),
					messagesForm.getDay()));
		}

		return result;
	}

    public void sendMessage(String message,
    		HttpSession session) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		MessagesEditForm messagesForm = (MessagesEditForm) session.getAttribute("order_dispo_MessagesEditForm");
		if (messagesForm != null) {
			if (message.length() > 250) {
				message = message.substring(0, 250);
			}
			getServiceLocator().getDispoService().sendMessageToDriver(messagesForm.getDriverId(),
					message);
		}
		else {
			LOG.error("messagesForm == null");
		}
	}

	public void setMessageRead(Long msgId) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		getServiceLocator().getDispoService().setDriverMessageRead(msgId);
	}

	public void resetCarrierDeclineNote(Long orderId) {
		Authorization.authorize(Authorization.DISPATCHING_W);
		getServiceLocator().getDispoService().resetCarrierDeclineNote(orderId);
	}

	/**
	 * Returns a map containing two maps (under the keys <code>MAP_KEY_TRUCK</code> and
	 * <code>MAP_KEY_TRAILER</code>), which are the allowed truck and trailer transport
	 * categories, respectively, for the different package types.
	 * @return
	 */
	public Map<String, Map<Long, Set<Long>>> getTrspCategoriesForPackageTypes() {
		final Map<String,Map<Long,Set<Long>>> mapOfMaps = new HashMap<String,Map<Long,Set<Long>>>();
		mapOfMaps.put(MAP_KEY_TRUCK, PackageTypeTrspCatMapping.getTruckTrspCatForAllPackageTypes());
		mapOfMaps.put(MAP_KEY_TRAILER, PackageTypeTrspCatMapping.getTrailerTrspCatForAllPackageTypes());

		return mapOfMaps;
	}
	
	public TruckAndAssignmentModificationDTO getTruckAndAssignmentModificationListWithLocalEJB(List<Long> filterTruckIds, List<Long> staleTruckIds, Map<Long, Long> staleTruckIdsWithPdaStatus, Long lastModDate, HttpServletRequest request) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_dispo_DispoForm");
		Long poolId = dispoForm.getPools().get(dispoForm.getSelectedPool()).getPoolTxPK().getPoolId();
		List<Long> carriers = new ArrayList<Long>(dispoForm.getSelectedCarriers());
		boolean showOnlyTrucksWithOrders = dispoForm.isShowOnlyTrucksWithOrders();
		boolean hideTrucksWithoutDeliveries = dispoForm.isHideTrucksWithoutDeliveries()
				|| showOnlyTrucksWithOrders;
		boolean hidePlanningTrucks = dispoForm.isHidePlanningTrucks();
		boolean hideInactiveTrucks = dispoForm.isHideInactiveTrucks();
		boolean hideTrucksAndOrderOfPartners = dispoForm.isHideTrucksAndOrderOfPartners();
		int filterForGenericTruck = Constant.FILTER_FOR_TRUCK_ALL;
		if (hidePlanningTrucks) {
			filterForGenericTruck = Constant.FILTER_FOR_TRUCK_REAL_ONLY;
		}
		Collection<Long> orderDeliveryStatusCds = dispoForm.getSelectedOrderStatuses();
		Collection<String> plantCds = (dispoForm.isDeliveringPlantsSelectionEmpty() ? null : dispoForm.getSelectedDeliveringPlants());
		Long orderNumber = dispoForm.getOrderNumber();
		Date date = (dispoForm.getDate() != null) ? dispoForm.getDate() : new Date();
		Locale locale = request.getLocale();
		final String language = RequestUtils.getUserLocale(request, null).getLanguage();

		Integer sizeOfOrderStatusList = dispoForm.getOrderStatusList().size();
		
		Long truckPriority = dispoForm.getTruckPriority();

		TimeZone userTimeZone = getUserTimeZone();

		TruckAndAssignmentModificationDTO truckAndAssignmentModification = null;
		try {
			ConfigBean configBean = getConfigBean();
			DispoServiceLocalInterface dispoServiceLocal = LocalServices.getDispoService(configBean);
			
			truckAndAssignmentModification = dispoServiceLocal.getTruckAndAssigmentModificationList(
					LocalServices.createLocalContext(), date, language, locale, poolId, carriers,
					hideTrucksWithoutDeliveries, hideInactiveTrucks, filterForGenericTruck,
					orderDeliveryStatusCds, plantCds, orderNumber, sizeOfOrderStatusList,
					filterTruckIds, userTimeZone, staleTruckIds, staleTruckIdsWithPdaStatus, lastModDate, truckPriority, hideTrucksAndOrderOfPartners);
		}
		catch (NamingException ex) {
			LOG.error(Reason.RES_ERROR_LOCALEJB_NOT_FOUND.toString(), ex);
			throw new LogonException(ex, Reason.RES_ERROR_LOCALEJB_NOT_FOUND);
		}

		if (truckAndAssignmentModification != null) {
			
			TruckModificationDTO truckModification = truckAndAssignmentModification.getTruckModification();
			
			if (truckModification != null) {
				
				List<IndexedObject> indexedObjects = truckModification.getNewTrucks();
				
				for (IndexedObject o : indexedObjects) {
					TruckWrapper truckWrapper = (TruckWrapper)o.getObject();
					if (truckWrapper == null) {
						continue;
					}

					List<TruckUnavailabilityEntity> unavailabilities = truckWrapper.getTruckUnavailabilities();
					for (TruckUnavailabilityEntity entity : unavailabilities) {
						entity.setStartDat(userTimeZone.shiftToUTC(entity.getStartDat()));
						entity.setEndDat(userTimeZone.shiftToUTC(entity.getEndDat()));
					}

					List<TrafficJamEntity> trafficJams = truckWrapper.getTrafficJams();
					for (TrafficJamEntity trafficJam : trafficJams) {
						trafficJam.setStartDat(userTimeZone.shiftToUTC(trafficJam.getStartDat()));
						trafficJam.setEndDat(userTimeZone.shiftToUTC(trafficJam.getEndDat()));
					}

					List<DispoDriverBreakBean> driverBreaks = truckWrapper.getDriverBreaks();
					for (DispoDriverBreakBean driverBreak : driverBreaks) {
						driverBreak.setStartDat(userTimeZone.shiftToUTC(driverBreak.getStartDat()));
						driverBreak.setEndDat(userTimeZone.shiftToUTC(driverBreak.getEndDat()));
					}
					
					List<DispoDriverBreakListEntity> mopBreaks = truckWrapper.getMopBreaks();
					for(DispoDriverBreakListEntity mopBreak : mopBreaks) {
						mopBreak.setStartDat(userTimeZone.shiftToUTC(mopBreak.getStartDat()));
						mopBreak.setEndDat(userTimeZone.shiftToUTC(mopBreak.getEndDat()));
					}
				}
			}

			AssignmentModificationDTO assignmentModificationDTO = truckAndAssignmentModification.getAssignmentModification();
			if (assignmentModificationDTO != null) {
				List<AssignmentWrapper> assignments = assignmentModificationDTO.getAssignments();

				for (AssignmentWrapper assgnWrapper : assignments) {
					List<AssignmentPartWrapper> parts = assgnWrapper.getParts();
					for (AssignmentPartWrapper part : parts) {
						DispoAssignPartListEntity entity = part.getAssignmentPart();
						entity.setDeliveryFromDat(userTimeZone.shiftToUTC(entity.getDeliveryFromDat()));
						entity.setDeliveryToDat(userTimeZone.shiftToUTC(entity.getDeliveryToDat()));
						entity.setRealStart(userTimeZone.shiftToUTC(entity.getRealStart()));
						entity.setPlannedStart(userTimeZone.shiftToUTC(entity.getPlannedStart()));
					}
				}
			}
		}
		
		return truckAndAssignmentModification;
	}

	/**
	 * Returns order numbers belonging to the given assignments which are at least
	 * in status arrived at loading point.
	 * 
	 * @param orderNumbers
	 * @return
	 */
	public List<Long> getOrderNumbersToConfirm(List<Long> orderNumbers) {
		Authorization.authorize(Authorization.DISPATCHING_R);
		final DispoService dispoService = getServiceLocator().getDispoService();
		List<Long> orderNumbersToConfirm = dispoService.getOrderNumbersToConfirm(orderNumbers);
		return orderNumbersToConfirm;
	}
	
	public void setTruckPriorityFilter(String truckPriority, HttpSession session) throws ParseException {
		Authorization.authorize(Authorization.DISPATCHING_R);
		DispoForm dispoForm = (DispoForm) session.getAttribute("order_dispo_DispoForm");
		dispoForm.setTruckPriority(truckPriority);
	}
}

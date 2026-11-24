/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.print.PrintException;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.BagTerminalServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.BagTerminalServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigurableSequencesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.utils.LogonPrintException;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.BagTerminalEntity;
import com.crh.logon.persistence.entities.views.FreeTrucksEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;

/**
 * Used from AuftragBearbeitungForm, EntryAction.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class BagTerminalServiceBean extends BagTerminalServiceDispatcher implements BagTerminalServiceLocalInterface{
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BagTerminalServiceBean.class);
	
	@EJB private ConfigurableSequencesServiceLocalInterface sequenceService;
	@EJB private BagTerminalServiceLocalInterface bagTerminalService;
	@EJB private LoadingServiceLocalInterface loadingService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;

	
	public static Long [] visibleStatuses = {
		Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
		Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK ,
		Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
		Constant.ORDER_STATUS_LOADING,
		Constant.ORDER_STATUS_LOADED,
		Constant.ORDER_STATUS_SHIPPING,
		Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
		Constant.ORDER_STATUS_UNLOADING,
		Constant.ORDER_STATUS_DELIVERED,
		Constant.ORDER_STATUS_FINISHED	
	};

	@EJB
	private OrderServiceLocalInterface orderService;
	
	/**
	 * @param locale
	 * @param user
	 * @param shippingpointId
	 * @param loadingpointId
	 * @param bulkPickupCreationFlag indicates whether bulk order creation is allowed on the pickup terminal
	 * 
	 */
	public List<BagTerminalEntity> loadOrders(UserEntity user, Locale locale, Long shippingpointId,
			Long loadingpointId, boolean bulkPickupCreationFlag) {
		// [[
		List<BagTerminalEntity> result = null;
		Object[][] params = {{"user", user},
							{"locale", locale},
							{"shippingpointId", shippingpointId},
							{"loadingpointId", loadingpointId},
							{"bulkPickupCreationFlag", bulkPickupCreationFlag + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrders", params);
		}
		// ]]
		try {
			DriverEntity driver = getDriverForUser(user);
			TruckEntity currentTruck = driver.getCurrentTruck();
			if (currentTruck == null) {
				return result = Collections.<BagTerminalEntity> emptyList();
			}
			DaoProvider daoProvider = getDaoProvider();
			ReadOnlyDao<BagTerminalEntity> dao = daoProvider.getBagTerminalEntityDao();
			
			Filter<BagTerminalEntity> filt = new FilterImpl<BagTerminalEntity>();
			String language = locale.getLanguage();
			if (language.isEmpty()) {
				language = Constant.DEFAULT_LANGUAGE;
			}
			Expression currentLanguage = Restrictions.eq(BagTerminalEntity.ATTR_LANG_CD, language);
			Expression withGivenShippingpoint = Restrictions.eq(BagTerminalEntity.ATTR_SHIPPINGPOINT_ID, shippingpointId);
			Expression noLoadingpoint = Restrictions.isNull(BagTerminalEntity.ATTR_LOADINGPOINT_ID);
			Expression withGivenLoadingpoint = Restrictions.eq(BagTerminalEntity.ATTR_LOADINGPOINT_ID, loadingpointId);
			Long truckId = currentTruck.getTruckId();
			Expression withUsersTruck = Restrictions.eq(BagTerminalEntity.ATTR_TRUCK_ID, truckId);
			filt.addExpression(currentLanguage);
			filt.addExpression(withGivenShippingpoint);
			filt.addExpression(Restrictions.or(withGivenLoadingpoint, noLoadingpoint));
			filt.addExpression(withUsersTruck);
			
			if (!bulkPickupCreationFlag) {
				Expression isBagOrder = Restrictions.in(BagTerminalEntity.ATTR_MATERIAL_PACKAGE_CD,
						Constant.bagOrderMaterialPackageCds);
				filt.addExpression(isBagOrder);
			}

			Order orderedByLoadingMinPlannedStartAsc = Order.asc(BagTerminalEntity.ATTR_LOADING_MIN_PLANNED_START);
			Order orderedByAssignmentIdAsc = Order.asc(BagTerminalEntity.ATTR_ASSIGNMENT_ID);
			Order orderedByOrderIdAsc = Order.asc(BagTerminalEntity.ATTR_ORDER_ID);
			filt.addExpression(orderedByLoadingMinPlannedStartAsc);
			filt.addExpression(orderedByAssignmentIdAsc);
			filt.addExpression(orderedByOrderIdAsc);
			return result = dao.find(filt);
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
	 * @param user
	 * @return
	 */
	public DriverEntity getDriverForUser(UserEntity user) {
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(DriverEntity.ATTR_USER_ID, user.getUserId()));
		DriverEntity driver = getDaoProvider().getDriverEntityDao().findSingle(filter);
		return driver;
	}
	/**
	 * Changes the truck for the driver. Old truck will be detached from the driver, new will get the 
	 * driver.
	 * @param driverId
	 * @param truckId
	 */
	public void changeTruck(DriverEntity driver, Long truckId) {		
		// [[
		Object[][] params = {{"driver", driver},
							{"truckId", truckId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "changeTruck", params);
		}
		// ]]
		try {
			if (driver == null ) {
				return;
			}
			DriverEntity lastTruckDriver = truckAdminService.fetchDriver(truckId);
			
			TruckEntity truck = truckAdminService.fetchTruck(truckId);
			if (truck != null) {
				truckAdminService.updateDriver(truck, lastTruckDriver, driver, false);
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
	
	public List fetchFreeTrucks(CarrierEntity carrier) {		
		// [[
		List result = null;
		Object[][] params = {{"carrier", carrier}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFreeTrucks", params);
		}
		// ]]
		try {
			ReadOnlyDao<FreeTrucksEntity> dao = getDaoProvider().getFreeTrucksEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(FreeTrucksEntity.ATTR_CARRIER_ID, carrier.getCarrierId()));
			filter.addExpression(Restrictions.eq(FreeTrucksEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			List freeTrucks = dao.find(filter);
			return result = freeTrucks;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public CarrierEntity getCarrierForUser(UserEntity user) {
		SapVendorEntity sapVendor = user.getSapVendor();
		if ( sapVendor == null ) {
			return null;
		}
		
		ReadWriteDao<CarrierEntity> cDao = getDaoProvider().getCarrierEntityDao();
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR_ID, sapVendor.getSapVendorId()));
		filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.neq(CarrierEntity.ATTR_SAP_VENDOR + "." + SapVendorEntity.ATTR_MARKED_FOR_DELETION, Constant.MARKED_FOR_DELETION));
		CarrierEntity carrier = cDao.findSingle(filter);
		
		return carrier;
		
	}
	
	public void loadAndPrintAssignment(Long assignmentId,
			PickupLoadingpointEntity pickupLoadingpoint) throws LogonPrintException {
		// [[
		Object[][] params = { { "assignmentId", assignmentId },
							{"pickupLoadingpoint", pickupLoadingpoint}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadAndPrintAssignment", params);
		}
		// ]]
		try {
			
			final List<OrderEntity> orders = getOrdersByAssignmentId(assignmentId);

			LoadingpointEntity loadingpoint = pickupLoadingpoint.getLoadingpoint();
			if (pickupLoadingpoint.getOrdersLoadableTerminalFlag()) {
				ConfSeqLock lock = null;
				try {
					lock = sequenceService.acquireLockForOrders(orders);
					bagTerminalService._loadAssignment(assignmentId, pickupLoadingpoint, lock);
				}
				finally {
					try {
						sequenceService.releaseLock(lock);
					}
					catch (LogonException e) {
						LOG.error(e.getMessage());
					}
				}
				
				em.clear();
				final List<OrderEntity> updatedOrders = getOrdersByAssignmentId(assignmentId);
				boolean allNecessaryDNsOfAllOrdersWerePrinted = true;
				for (OrderEntity order : updatedOrders) {
					OrderStatusBsEntity orderStatus = order.getOrderStatus();
					if (LogonStatusUtils.isLoadedOrBeyond(orderStatus)) {
						DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
						Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
						if (LogonUtil.isPickupOrder(deliveryTypeCd)) {
							// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
							allNecessaryDNsOfAllOrdersWerePrinted &=
									orderService.generateAndSendDeliveryNotes(order, 
											OrderServiceBean.SEND_FAX, OrderServiceBean.SEND_EMAIL,
											OrderServiceBean.PRINT_PAPER, loadingpoint);
						}
						else {
							allNecessaryDNsOfAllOrdersWerePrinted &=
									orderService.generateAndSendDeliveryNotes(order,
											OrderServiceBean.DO_NOT_SEND_FAX,
											OrderServiceBean.DO_NOT_SEND_EMAIL,
											OrderServiceBean.PRINT_PAPER, loadingpoint);
						}
					}
				}
				if (!allNecessaryDNsOfAllOrdersWerePrinted) {
					throw new LogonPrintException();
				}
			}
			else {
				
				for (OrderEntity order : orders) {
					Long orderId = order.getOrderId();
					Long orderNumber = order.getOrderNumber();
					List<OrderOutputdocEntity> docs = 
							orderService.loadDeliveryNoteList(orderId);
					if (docs.isEmpty()) {
						LOG.error("No tlgnc_order_outputdoc entry found for " 
								+ "order id " + orderId
								+ ", order number " + orderNumber
								+ ". Could not send delivery note.");
					}
					else {
						OrderOutputdocEntity doc = docs.get(0);
						try {
							boolean paperPrintingHappened = orderService.sendDeliveryNoteToPrinter(
									order, doc.getPdfOutputdoc(),
 loadingpoint);
							if (!paperPrintingHappened) {
								throw new LogonPrintException();
							}
						}
						catch (PrintException printException) {
							String errorMessage = "Printing failed for order id " + orderId +
									", order number: " + orderNumber + ".";
							LOG.error(errorMessage, printException);
							throw new LogonPrintException();
						}
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

	public List<OrderEntity> getOrdersByAssignmentId(Long assignmentId) {
		final AssignmentEntity assignment = 
				getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		return orders;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _loadAssignment(Long assignmentId, PickupLoadingpointEntity pickupLoadingpoint,
			ConfSeqLock lock) {
		
		final List<OrderEntity> orders = getOrdersByAssignmentId(assignmentId);
		
		loadingService.setOrdersToLoaded(orders,
				pickupLoadingpoint.getLoadingpoint(), null, null, null, null, EventSource.LOGON, lock);
	}
}

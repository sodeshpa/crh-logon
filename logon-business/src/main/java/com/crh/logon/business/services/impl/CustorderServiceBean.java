/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.kafka.KafkaPublisherService;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.business.ordercreation.dto.CustorderItemDeliverySetting;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.beans.CreateCustorderDTO;
import com.crh.logon.business.services.beans.CreateCustorderFromMasterDefaultDTO;
import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.dispatchers.CustorderServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.CarrierServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CustorderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LDSServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PricingCallServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.QuantityRestrictionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SimpleVehicleSchedulingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.CustorderSplitWrapper;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.DefaultFallbackLogic;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderTruckTypeComparator;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.business.services.utils.QuantityRestrictionCalculation;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.business.stores.domain.views.QuantityPerMaterialInCustomerOrderStoreInterface;
import com.crh.logon.business.stores.sap.BusinessPartnerInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.PoolTruckEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShsareaRestrTruckTypeEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.CustorderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.views.CustordItemSumQtyExtEntity;
import com.crh.logon.persistence.entities.views.PlanningTruckEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CreatedOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedLoadOrderData;
import org.apache.commons.lang.BooleanUtils;


/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class CustorderServiceBean extends CustorderServiceDispatcher implements CustorderServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CustorderServiceBean.class);

	private static final boolean DOES_NOT_COME_FROM_LDS = false;
	private static final boolean NOT_ARCHIVED = true;

	private static final Long NO_APPROPRIATE_PLANNING_TRUCK_FOUND = null;

	@EJB QuantityPerMaterialInCustomerOrderStoreInterface quantityPerMaterialInCustomerOrderStore;

	@EJB QuantityRestrictionServiceLocalInterface quantityRestrictionService;

	@EJB LDSServiceLocalInterface ldsService;

	@EJB private CustorderServiceLocalInterface customerOrderService;

	@EJB private DeliverySettingServiceLocalInterface deliverySettingService;

	@EJB private OrderServiceLocalInterface orderService;

	@EJB private PricingCallServiceLocalInterface pricingCallService;

	@EJB private DispokarteServiceLocalInterface dispokarteService;

	@EJB private OrderDetailServiceLocalInterface orderDetailService;

	@EJB private ConfigServiceLocalInterface configService;

	@EJB private SimpleVehicleSchedulingServiceLocalInterface simpleVehicleSchedulingService;

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB private CarrierServiceLocalInterface carrierService;

	@EJB private DispoServiceLocalInterface dispoService;

	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;

	@EJB private TimeZoneServiceLocalInterface timeZoneService;

	@EJB private TruckAdminServiceLocalInterface truckAdminService;

	@EJB private KafkaPublisherService kafkaPublisherService;

	@EJB
	BusinessPartnerInterface businessPartnerStore;

	private void validateCustorderDeliverySetting(
			CustorderEntity custorderEntity,
			DeliverySettingSalesAreaMap salesAreaMap)
			throws ValidationException {

		final List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();
		final List<CustorderItemEntity> custorderItems = custorderEntity
				.getCustorderItems();

		for (CustorderItemEntity custorderItemEntity : custorderItems) {

			final CustorderItemDeliverySetting itemDeliverySetting = new CustorderItemDeliverySetting(
					custorderEntity, custorderItemEntity);
			final DeliverySettingDTO matchDeliverySetting = salesAreaMap
					.getDeliverySettingByCompositeIdentifier(itemDeliverySetting
							.getCompositeIdentifier());

			// order.sarea+order.sapContractItem+order.sapMaterial.getSapMaterialId;
			if (matchDeliverySetting == null) {
				LOG.error("Delivery setting (restriction) cannot be found");
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(
						CustorderError.INVALID_PRODUCT, custorderItemEntity,
						additionalInfo));
			}

			final DeliveryOptionConfigurationDTO validOptionConfiguration = DeliverySettingFilterLogic
					.getDeliveryOptionConfiguration(itemDeliverySetting,
							salesAreaMap);
			final Map<Object, List<ShiptoSalesAreaError>> invalidMap = new HashMap<Object, List<ShiptoSalesAreaError>>();
			salesAreaMap.validateDeliverySetting(itemDeliverySetting,
					validOptionConfiguration, invalidMap);

			if (!invalidMap.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Validation error: " + invalidMap);
				}
				validationErrorReasons.addAll(InvalidMapSupport
						.getCustorderExceptionFromInvalidMap(invalidMap,
								custorderItemEntity));
			}

		}

		if (!validationErrorReasons.isEmpty()) {
			throw new ValidationException(validationErrorReasons);
		}

		return;
	}

	private void validatePlanningMaterial(CustorderEntity custorderEntity)
			throws ValidationException {

		final List<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();
		final List<CustorderItemEntity> custorderItems = custorderEntity.getCustorderItems();

		CustorderItemEntity planningCustorderItemEntity = null;
		//counting types
		int planningMaterialItemCounter = 0;
		int bagItemCounter = 0;
		int bulkItemCounter = 0;//planning material excluded
		for (CustorderItemEntity custorderItemEntity : custorderItems) {
			if (LogonUtil.isPlanningMaterial(custorderItemEntity.getSapMaterial())) {
				planningCustorderItemEntity = custorderItemEntity;
				planningMaterialItemCounter++;
			}
			else if (LogonPackageHandlingUtil.isBag(custorderItemEntity.getSapMaterial())) {
				bagItemCounter++;
			}
			else if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(custorderItemEntity.getSapMaterial())) {
				bulkItemCounter++;
			}
		}

		//evaluating counter results
		if (planningMaterialItemCounter > 1) {
			LOG.error("max 1 planning material item can be included in 1 order");
			String[] additionalInfo = {};
			validationErrorReasons.add(new ValidationErrorReason(
					CustorderError.INVALID_PRODUCT, planningCustorderItemEntity,
					additionalInfo));
		}
		if (planningMaterialItemCounter > 0 && bagItemCounter > 0) {
			LOG.error("planning material and bag cannot be mixed");
			String[] additionalInfo = {};
			validationErrorReasons.add(new ValidationErrorReason(
					CustorderError.INVALID_PRODUCT, planningCustorderItemEntity,
					additionalInfo));
		}
		if (planningMaterialItemCounter > 0 && bulkItemCounter > 0) {
			LOG.error("planning material and bulk cannot be mixed");
			String[] additionalInfo = {};
			validationErrorReasons.add(new ValidationErrorReason(
					CustorderError.INVALID_PRODUCT, planningCustorderItemEntity,
					additionalInfo));
		}

		if (planningMaterialItemCounter == 1) {
			//allowed flag on sales area
			if (!custorderEntity.getSarea().getAllowPlanningMaterialFlag()) {
				LOG.error("planning material is not allowed on this sales area");
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(
						CustorderError.INVALID_PRODUCT, planningCustorderItemEntity,
						additionalInfo));
			}
		}

		if (!validationErrorReasons.isEmpty()) {
			throw new ValidationException(validationErrorReasons);
		}

		return;
	}

	public CustorderEntity splitBulkCustorderEntity(CustorderEntity custorder) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "custorder", custorder } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "splitBulkCustorderEntity", params);
		}
		// ]]
		try {
			if (custorder != null
					&& custorder.getCustorderStatus() != null
					&& custorder.getCustorderStatus().getCustorderStatusCd() == Constant.CUSTORDER_STATUS_FINISHED) {
				// don't want to make validations on finished order, don't allow
				// automatic changes by split
				result = custorder;
			} else {
				DeliverySettingSalesAreaMap salesAreaMap = initSalesAreaMapForCustorder(
						custorder);

				// FIXME this should NOT come from the salesarea map, but from
				// ALL orders combined.
				TruckWagonTypeWrapper truckTypeWrapper = getVehicleWrapperFromCustorder(
						custorder, salesAreaMap);

				result = splitBulkCustorderEntity(custorder, truckTypeWrapper);
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

	public CustorderEntity splitBulkCustorderEntity(CustorderEntity customerOrder,
			TruckWagonTypeWrapper truckTypeWrapper) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "customerOrder", customerOrder },
				{ "truckTypeWrapper", truckTypeWrapper } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "splitBulkCustorderEntity", params);
		}
		// ]]
		try {
			CustorderSplitWrapper splitWrapper = createCustorderSplitWrapper(
					customerOrder, truckTypeWrapper);
			return result = splitBulkCustorderEntity(customerOrder,
					truckTypeWrapper, splitWrapper);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public CustorderEntity splitBulkCustorderEntity(CustorderEntity custorder,
			TruckWagonTypeWrapper truckTypeWrapper,
			CustorderSplitWrapper custorderBulkSplitWrapper) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "custorder", custorder },
				{ "vehicle", truckTypeWrapper },
				{ "custorderBulkSplitWrapper", custorderBulkSplitWrapper } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "splitBulkCustorderEntity", params);
		}
		// ]]
		try {
			CustorderUtils.splitCustorder(custorderBulkSplitWrapper);
			List<OrderEntity> orders = custorder.getOrders();

			/**
			 * This is a workaround, as certain authorization calls end up in
			 * flush calls, therefore the new order items which are not yet
			 * initialized has to be detached from the order in order that they
			 * do not trigger an insert which violates the not nullable field's
			 * constraint. See also BUSINESS2635: Endload fails in case exact
			 * customer order quantity flag is on.
			 *
			 * Steps: detach new items, and add to the order.
			 *
			 * See also {@link OrderServiceBean#processOnMerge} At some point
			 * this problem needs to be addressed.
			 */
			final List<OrderEntity> newOrders = CustorderUtils
					.detachNewOrders(orders);

			// updating orderitem numerator and denominator after split
			for (OrderEntity order : orders) {
				OrderItemEntity orderItem = CustorderUtils
						.getBulkDumpOrPowderOrderItem(order);
				if (orderItem != null) {
					orderService.updateOrderItemNumeratorAndDenominator(
							orderItem,
							Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);
				}
			}
			orders.addAll(newOrders);

			return result = custorderBulkSplitWrapper.getCustorder();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public CustorderSplitWrapper createCustorderSplitWrapper(
			CustorderEntity custorder, TruckWagonTypeWrapper truckTypeWrapper) {
		// [[
		CustorderSplitWrapper result = null;
		Object[][] params = { { "custorder", custorder },
				{ "truckTypeWrapper", truckTypeWrapper } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCustorderSplitWrapper", params);
		}
		// ]]
		try {
			// create new CustorderBulkSplitWrapper instance
			ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltDao = getDaoProvider()
					.getSapMaterialAltUomEntityDao();
			ConfigBean configBean = configService.getConfigBean();
			CustorderSplitWrapper splitWrapper = CustorderSplitWrapper.create(
					custorder, truckTypeWrapper, sapMatAltDao, referentialDataService,
					configBean, orderService);
			return result = splitWrapper;
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
	 * Returns an ordered map containing materials appearing in the parameter customer
	 * order and descriptive data for each material like: how much is ordered, loaded, etc.
	 *
	 * @param Map<Long, {@link CustordItemSumQtyExtEntity}> quantityPerMaterial
	 * @return
	 */
	@AdnTrace
	public Map<Long, CustordItemSumQtyExtEntity> fetchQuantitiesPerMaterialInCustomerOrder(
			Long custorderId, Locale locale) {

		final Map<Long, CustordItemSumQtyExtEntity> quantityPerMaterial = new LinkedHashMap<Long, CustordItemSumQtyExtEntity>();

		final List<CustordItemSumQtyExtEntity> perMaterialQuantities = quantityPerMaterialInCustomerOrderStore.findByCustorderId(custorderId, locale.getLanguage());
		for (CustordItemSumQtyExtEntity perMaterialQuantity : perMaterialQuantities) {
			quantityPerMaterial.put(perMaterialQuantity.getSapMaterialId(), perMaterialQuantity);
		}

		return quantityPerMaterial;
	}

	/**
	 * Fetches all {@link OrderEntity}s to a given {@link CustorderEntity}.
	 */
	public List<OrderEntity> getOrdersByCustorder(CustorderEntity custorder) {
		// [[
		List<OrderEntity> result = null;
		Object[][] params = { { "custorder", custorder } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findOrdersByCustorder", params);
		}
		// ]]
		try {
			final ReadOnlyDao<OrderEntity> dao = getDaoProvider()
					.getOrderEntityDao();
			final Filter<OrderEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.and(Restrictions.eq(OrderEntity.ATTR_CUSTORDER,
					custorder),
					Restrictions.eq(OrderEntity.ATTR_ARCHIVING_DISPLAY_FLAG, NOT_ARCHIVED))
					);
			filter.addExpression(Order.asc(OrderEntity.ATTR_ORDER_NUMBER));


			final List<OrderEntity> orderList = dao.find(filter);
			for (OrderEntity o : orderList) {
				o.initialize();
			}

			return result = orderList;
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
	 * Fetches a {@link CustorderEntity} and initializes the {@link Order}s and
	 * {@link OrderItem}s.
	 */
	public CustorderEntity findCustorderById(Long custorderid) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "custorderid", custorderid } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findCustorderById", params);
		}
		// ]]
		try {

			Filter<CustorderEntity> filter = new FilterImpl<CustorderEntity>();
			filter.addExpression(Restrictions.eq(
					CustorderEntity.ATTR_CUSTORDER_ID, custorderid));
			DaoProvider daoProvider = getDaoProvider();
			ReadWriteDao<CustorderEntity> customerOrderDao = daoProvider
					.getCustorderEntityDao();
			CustorderEntity customerOrder = customerOrderDao.findSingle(filter);
			customerOrder.initialize();

			List<OrderEntity> orders = customerOrder.getOrders();

			for (OrderEntity order : orders) {
				order.initialize();
			}
			return result = customerOrder;
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Fetches a {@link CustorderEntity} <b>by custorderNumber</b> and
	 * initializes the {@link Order}s and {@link OrderItem}s.
	 */
	public CustorderEntity findCustorderByNumber(Long custorderNumber) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "custorderNumber", custorderNumber } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findCustorderByNumber", params);
		}
		// ]]
		try {

			Filter<CustorderEntity> filter = new FilterImpl<CustorderEntity>();
			filter.addExpression(Restrictions.eq(
					CustorderEntity.ATTR_CUSTORDER_NUMBER, custorderNumber));
			DaoProvider daoProvider = getDaoProvider();
			ReadWriteDao<CustorderEntity> customerOrderDao = daoProvider
					.getCustorderEntityDao();
			CustorderEntity customerOrder = customerOrderDao.findSingle(filter);
			customerOrder.initialize();

			List<OrderEntity> orders = customerOrder.getOrders();

			for (OrderEntity order : orders) {
				order.initialize();
			}

			return result = customerOrder;
		} finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Finds out the proper vehicle type wrapper which can be used to consider
	 * maximum capacity calculations further on.
	 *
	 * @param salesAreaMap
	 * @return a proper (max. prio) truck wagon type wrapper or null for service
	 *         material only customer orders
	 * @throws LogonException
	 *             if a proper truck couldn't be found
	 */
	private TruckWagonTypeWrapper getVehicleWrapperFromCustorder(
			CustorderEntity custorderEntity,
			DeliverySettingSalesAreaMap salesAreaMap) {

		TruckWagonTypeWrapper truckWagonTypeWrapper = null;

		final DeliveryTypeBsEntity deliveryTypeBs = custorderEntity.getDeliveryType();
		if (LogonPackageHandlingUtil.isServiceOnlyCustomerOrder(custorderEntity)) {
			return null;
		}
		else if (deliveryTypeBs != null && LogonUtil.isReturnPickupOrder(deliveryTypeBs.getDeliveryTypeCd())) {
			return null;
		}
		else {
			List<CustorderItemEntity> custorderItems = custorderEntity
					.getCustorderItems();
			for (CustorderItemEntity custorderItemEntity : custorderItems) {

				final CustorderItemDeliverySetting itemDeliverySetting = new CustorderItemDeliverySetting(
						custorderEntity, custorderItemEntity);

				final DeliveryOptionConfigurationDTO validOptionConfiguration = DeliverySettingFilterLogic
						.getDeliveryOptionConfiguration(itemDeliverySetting,
								salesAreaMap);

				// FIXME this list below FOR ORDER DETAIL ONLY should come from
				// the order itself instead of the valid option configuration
				List<TruckWagonTypeWrapper> vehicleTypeWrapperList = validOptionConfiguration
						.getVehicleTypeWrapperList();
				if (vehicleTypeWrapperList == null
						|| vehicleTypeWrapperList.isEmpty()) {
					continue;
				}
				truckWagonTypeWrapper = TruckWagonTypeWrapperUtil
						.findProperTruck(vehicleTypeWrapperList);
				if (truckWagonTypeWrapper != null) {
					break;
				}

			}
		}

		if (truckWagonTypeWrapper == null) {
			String errMsg = "No truckWrapper found";
			LOG.error(errMsg);
			throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
		}

		return truckWagonTypeWrapper;

	}

	public CustorderEntity createCustomerOrder(
			final CustorderEntity rawCustorderEntity, boolean forwardToPool,
			boolean forwardToCarrier, boolean autoRelease,
			final Date plannedDeliveryTime, long weighSystemLp, boolean filterFutureContracts) {
		return createCustomerOrder(rawCustorderEntity, forwardToPool,
				forwardToCarrier, autoRelease, plannedDeliveryTime,
				weighSystemLp, false, filterFutureContracts);
	}

	@Override
	public CustorderEntity createCustomerOrder(
			CustorderEntity rawCustorderEntity, boolean forwardToPool,
			boolean forwardToCarrier, boolean autoRelease,
			Date plannedDeliveryTime, long weighSystemLp, boolean isLdsMessage, boolean filterFutureContracts) {
		return createCustomerOrder(rawCustorderEntity, forwardToPool,
				forwardToCarrier, autoRelease, plannedDeliveryTime,
				weighSystemLp, isLdsMessage, null, filterFutureContracts);
	}

	public CustorderEntity createCustomerOrder(
			final CustorderEntity rawCustorderEntity, boolean forwardToPool,
			boolean forwardToCarrier, boolean autoRelease,
			final Date plannedDeliveryTime, long weighSystemLp,
			boolean isLdsMessage, String serviceProcessIndicator, boolean filterFutureContracts) {
		return createCustomerOrder(rawCustorderEntity, forwardToPool,
				forwardToCarrier, autoRelease, plannedDeliveryTime,
				weighSystemLp, isLdsMessage, serviceProcessIndicator, false, null, filterFutureContracts);
	}

	/**
	 * Creates a {@link CustorderEntity} and in case the caller requires
	 * immediately initializes the steps forward to pool and forward to carrier.
	 *
	 * The input parameter rawCustomer order is can be unprocessed - which means
	 * that it will be a subject of the split in case it is a performance order,
	 * and its orders list will be automatically created when calling this
	 * method. In other cases (bag, etc.) the orders are supposed to be empty
	 * too, even if no split takes place for them, the single delivery orders
	 * are created here automatically. Do NOT call this method in case the order
	 * list is already populated.
	 *
	 * The input parameter rawCustomer order is can be processed - which means
	 * that it will NOT be processed further, it will simply be passed for the
	 * final persist method.
	 *
	 * @param weighSystemLp
	 *            : indicates if service is called via logon/web service/pda
	 * @param isLdsMessage
	 *            If it's true, than the call arrived from the LDS
	 * @param serviceProcessIndicator
	 *            : if the material template (dispocard) contains this
	 *            information, we have to set it to all orders belonging to this
	 *            customer order
	 *
	 */

	public CustorderEntity createCustomerOrder(
			final CustorderEntity rawCustorderEntity, boolean forwardToPool,
			boolean forwardToCarrier, boolean autoRelease,
			final Date plannedDeliveryTime, long weighSystemLp,
			boolean isLdsMessage, String serviceProcessIndicator,
			boolean copyCustomerRemarkToDriver, String customerRemarkLabel, boolean filterFutureContracts) {

			CustorderDTO processCustorderResult = new CustorderDTO(rawCustorderEntity);
			CustorderEntity detachedCustomerOrderCreationResult =  customerOrderService.initializeAndStoreOrders(
					processCustorderResult, forwardToPool, forwardToCarrier, autoRelease,
					plannedDeliveryTime, weighSystemLp, isLdsMessage, serviceProcessIndicator,
					copyCustomerRemarkToDriver, customerRemarkLabel, filterFutureContracts, false);

			// We need to get the orders here again because they became detached when initializeAndStoreOrders returned.
			CustorderEntity attachedCustOrderEntity = getDaoProvider().getCustorderEntityDao().findById(detachedCustomerOrderCreationResult.getCustorderId());

			List<AssignmentEntity> transmitableAssignments = createNewOrders(forwardToPool,
					forwardToCarrier, autoRelease,
					attachedCustOrderEntity.getOrders(), plannedDeliveryTime,
					weighSystemLp, isLdsMessage);
			em.flush();

			if (transmitableAssignments != null && !transmitableAssignments.isEmpty()) {
				transmitAssignmentsToWeighSystem(transmitableAssignments);
			}

			return attachedCustOrderEntity;

	}

	/**
	 * Creates a {@link CustorderEntity} and in case the caller requires
	 * immediately initializes the steps forward to pool and forward to carrier.
	 *
	 * The input parameter rawProcessCustorderResult order is can be unprocessed - which means
	 * that it will be a subject of the split in case it is a performance order,
	 * and its orders list will be automatically created when calling this
	 * method. In other cases (bag, etc.) the orders are supposed to be empty
	 * too, even if no split takes place for them, the single delivery orders
	 * are created here automatically. Do NOT call this method in case the order
	 * list is already populated.
	 *
	 * The input parameter rawProcessCustorderResult order is can be processed - which means
	 * that it will NOT be processed further, it will simply be passed for the
	 * final persist method.
	 *
	 * @param weighSystemLp
	 *            : indicates if service is called via logon/web service/pda
	 * @param isLdsMessage
	 *            If it's true, than the call arrived from the LDS
	 * @param serviceProcessIndicator
	 *            : if the material template (dispocard) contains this
	 *            information, we have to set it to all orders belonging to this
	 *            customer order
	 *
	 */

	public CreateCustorderDTO createCustomerOrderFromLds(
			CustorderDTO rawProcessCustorderResult, boolean forwardToPool,
			boolean forwardToCarrier, boolean autoRelease,
			Date plannedDeliveryTime, long weighSystemLp, boolean isLdsMessage) {


		CustorderEntity detachedCustomerOrderCreationResult = customerOrderService.initializeAndStoreOrders(
				rawProcessCustorderResult, forwardToPool, forwardToCarrier, autoRelease,
				plannedDeliveryTime, weighSystemLp, isLdsMessage, null, false, null, true, false);

		String custorderNumber = null;

		if (detachedCustomerOrderCreationResult != null && detachedCustomerOrderCreationResult.getCustorderNumber() != null) {
			custorderNumber = detachedCustomerOrderCreationResult.getCustorderNumber().toString();
		}

		CreateCustorderDTO createCustorderDTO = new CreateCustorderDTO(custorderNumber);

		// We need to get the orders here again because they became detached when initializeAndStoreOrders returned.
		CustorderEntity attachedCustOrderEntity = getDaoProvider().getCustorderEntityDao().findById(detachedCustomerOrderCreationResult.getCustorderId());

		List<AssignmentEntity> transmitableAssignments = createNewOrders(forwardToPool,
				forwardToCarrier, autoRelease,
				attachedCustOrderEntity.getOrders(), plannedDeliveryTime,
				weighSystemLp, isLdsMessage);

		em.flush();

		if (transmitableAssignments != null && !transmitableAssignments.isEmpty()) {

			transmitAssignmentsToWeighSystem(transmitableAssignments);

			List<ReducedLoadOrderData> loadOrderDataList = new ArrayList<ReducedLoadOrderData>();

			if (transmitableAssignments != null) {
				for (AssignmentEntity assignment : transmitableAssignments) {
					DeliveryTypeBsEntity deliveryType = attachedCustOrderEntity.getDeliveryType();
					if (deliveryType == null
							|| !LogonUtil.isPickupOrder(deliveryType.getDeliveryTypeCd())) {
						continue;
					}
					ShippingpointEntity shippingpoint = attachedCustOrderEntity.getShippingpoint();
					if (shippingpoint != null && shippingpoint.getWeighSystemSp() != null) {
						Long weighSystemSpCd = shippingpoint.getWeighSystemSp().getWeighSystemSpCd();
						if (weighSystemSpCd != null
								&& weighSystemSpCd.equals(Constant.WEIGH_SYSTEM_SP_FENICS)) {
							ReducedLoadOrderData loadOrderData = ldsService.createReducedLoadOrderData(assignment);
							loadOrderDataList.add(loadOrderData);
						}
					}
				}

				createCustorderDTO.getLoadOrderData().addAll(loadOrderDataList);
			}
		}
		return createCustorderDTO;

	}

	public CreateCustorderFromMasterDefaultDTO createCustomerOrderFromMasterDefault(
			CustorderDTO rawProcessCustorderResult, boolean autoRelease, long weighSystemLp, boolean isLdsMessage, Locale
			locale, ShiptoSareaRestrEntity ssRest, boolean warningMultipleDispocard) {
		boolean autoPoolAssignmentFlag = ssRest != null ? ssRest.getAutoPoolAssignmentFlag() : true;
		boolean autoCarrierAssignmentFlag = ssRest != null ? ssRest.getAutoCarrierAssignmentFlag() : true;
		String spiCd = ssRest != null ? ssRest.getSpiCd() : null;
		CustorderEntity detachedCustomerOrderCreationResult = customerOrderService.initializeAndStoreOrders(
				rawProcessCustorderResult, autoPoolAssignmentFlag, autoCarrierAssignmentFlag, autoRelease,
				null, weighSystemLp, isLdsMessage, spiCd, false, null, true, false);

		String custorderNumber = null;

		if (detachedCustomerOrderCreationResult != null && detachedCustomerOrderCreationResult.getCustorderNumber() != null) {
			custorderNumber = detachedCustomerOrderCreationResult.getCustorderNumber().toString();
		}


		// We need to get the orders here again because they became detached when initializeAndStoreOrders returned.
		CustorderEntity attachedCustOrderEntity = getDaoProvider().getCustorderEntityDao().findById(detachedCustomerOrderCreationResult.getCustorderId());

		List<AssignmentEntity> transmitableAssignments = createNewOrders(autoPoolAssignmentFlag,
				autoCarrierAssignmentFlag, autoRelease,
				attachedCustOrderEntity.getOrders(), null,
				weighSystemLp, isLdsMessage);
		if (ssRest != null) {
			OrderSpiBsEntity serviceProcessIndicator = null;
			if (ssRest.getSpiCd() != null) {
				serviceProcessIndicator = referentialDataService.findBsEntity(OrderSpiBsEntity.class, ssRest.getSpiCd());
			}
			for (OrderEntity newOrder : attachedCustOrderEntity.getOrders()) {
				newOrder.setSpi(serviceProcessIndicator);
				newOrder.setNoteRailwayDisp(ssRest.getNoteRailwayDisp());
				List<OrderTruckTypeEntity> orderVehicleTypes = new ArrayList<OrderTruckTypeEntity>();
				List<TruckTypeBsEntity> currentTruckTypes = new ArrayList<TruckTypeBsEntity>();
				List<TruckTypeBsEntity> newTruckTypes = new ArrayList<TruckTypeBsEntity>();
				List<TruckTypeBsEntity> reatinInCurrentTruckTypes = new ArrayList<TruckTypeBsEntity>();
				List<TruckTypeBsEntity> addToCurrentTruckTypes = new ArrayList<TruckTypeBsEntity>();
				for (OrderTruckTypeEntity orderTruckType : newOrder.getOrderVehicleTypes()) {
					currentTruckTypes.add(orderTruckType.getTruckType());
				}
				for (ShsareaRestrTruckTypeEntity ssrTruckType : ssRest.getRestrTruckTypes()) {
					newTruckTypes.add(ssrTruckType.getTruckType());
				}
				currentTruckTypes.retainAll(newTruckTypes);
				newTruckTypes.removeAll(currentTruckTypes);
				Iterator<OrderTruckTypeEntity> orderTruckTypeEntityIterator = newOrder.getOrderVehicleTypes().iterator();
				while (orderTruckTypeEntityIterator.hasNext()) {
					OrderTruckTypeEntity orderTruckTypeEntity = orderTruckTypeEntityIterator.next();
					if (!currentTruckTypes.contains(orderTruckTypeEntity.getTruckType())) {
						orderTruckTypeEntityIterator.remove();
					}
				}
				for (ShsareaRestrTruckTypeEntity truckType : ssRest.getRestrTruckTypes()) {
					if (newTruckTypes.contains(truckType.getTruckType())) {
						OrderTruckTypeEntity truckTypeEntity = new OrderTruckTypeEntity();
						truckTypeEntity.setTruckType(truckType.getTruckType());
						truckTypeEntity.setOrder(newOrder);
						truckTypeEntity.setEvaluationPriority(truckType.getEvaluationPriority());
						orderVehicleTypes.add(truckTypeEntity);
					}
				}
				newOrder.getOrderVehicleTypes().addAll(orderVehicleTypes);
			}
		}

		em.flush();

		CreateCustorderFromMasterDefaultDTO createCustorderFromMasterDefaultDTO = new CreateCustorderFromMasterDefaultDTO(custorderNumber);

		List<CreatedOrderData> createdOrderDataList = new ArrayList<CreatedOrderData>();
		CreatedOrderData createdOrderData = new CreatedOrderData();
		for (OrderEntity order : attachedCustOrderEntity.getOrders()) {
			createdOrderData.getOrder().add(ldsService.createOrder(order, locale));
		}
		createdOrderData.setWarningMultipleDispocard(warningMultipleDispocard);
		createdOrderDataList.add(createdOrderData);
		createCustorderFromMasterDefaultDTO.getCreatedOrderData().addAll(createdOrderDataList);
		return createCustorderFromMasterDefaultDTO;
	}

	/**
	 * @param rawCustorderEntity
	 * @param forwardToPool
	 * @param forwardToCarrier
	 * @param autoRelease
	 * @param plannedDeliveryTime
	 * @param weighSystemLp
	 * @param isLdsMessage
	 * @param serviceProcessIndicator
	 * @param copyCustomerRemarkToDriver
	 * @param customerRemarkLabel
	 * @param skipLeadTimeCheck
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CustorderEntity initializeAndStoreOrders(final CustorderDTO rawProcessCustorderResult,
			boolean forwardToPool, boolean forwardToCarrier, boolean autoRelease,
			final Date plannedDeliveryTime, long weighSystemLp, boolean isLdsMessage,
			String serviceProcessIndicator, boolean copyCustomerRemarkToDriver,
			String customerRemarkLabel, boolean filterFutureContracts, boolean skipLeadTimeCheck) {

		CustorderEntity processedCustorderEntity;
		CustorderDTO processCustorderResult;
		if (rawProcessCustorderResult.getCustorderEntity().getOrders() == null) {
			processCustorderResult = processCustomerOrder(rawProcessCustorderResult.getCustorderEntity(), skipLeadTimeCheck);
		} else {
			processCustorderResult = rawProcessCustorderResult;
		}
		processedCustorderEntity = processCustorderResult.getCustorderEntity();

		if (serviceProcessIndicator != null && processedCustorderEntity.getOrders() != null) {
			setSpecialProcessIndicatorOnOrders(serviceProcessIndicator, processedCustorderEntity);
		}

		final CustorderEntity newCustomerOrder = persistCustomerOrder(processCustorderResult, filterFutureContracts);

		if(copyCustomerRemarkToDriver){
			addCustomRemarkToDriver(customerRemarkLabel, newCustomerOrder);
		}

		List<OrderEntity> orders = newCustomerOrder.getOrders();

		em.flush();
		return newCustomerOrder;
	}

	/**
	 * Set the serviceProcessIndicator to all orders belonging to
	 * the customer order based on the material template
	 * @param serviceProcessIndicator
	 * @param processedCustorderEntity
	 */
	private void setSpecialProcessIndicatorOnOrders(String serviceProcessIndicator,
			CustorderEntity processedCustorderEntity) {
		OrderSpiBsEntity serviceProcessIndicatorEntity = referentialDataService.findBsEntity(OrderSpiBsEntity.class, serviceProcessIndicator);
		for (OrderEntity oe : processedCustorderEntity.getOrders()) {
			oe.setSpi(serviceProcessIndicatorEntity);
		}
	}

	/**
	 * @param customerRemarkLabel
	 * @param resultCustomerOrder
	 */
	private void addCustomRemarkToDriver(String customerRemarkLabel,
			final CustorderEntity resultCustomerOrder) {
		List<OrderEntity> orders = resultCustomerOrder.getOrders();
		for(OrderEntity order: orders){
			String existingComment = order.getCarrierTruckDriverNote() ==
					null ? "" : order.getCarrierTruckDriverNote() + " ";
			String newComment = existingComment + customerRemarkLabel +
					": " + order.getCustWeborderNote();
			if(newComment.length() > 250){
				newComment = newComment.substring(0, 247) + "...";
			}
			order.setCarrierTruckDriverNote(newComment);
		}
	}

	public CustorderDTO processCustomerOrder(
			final CustorderEntity custorderEntity, boolean skipLeadTimeCheck) {
		return processCustomerOrder(custorderEntity, null, false);
	}

	/**
	 * Entry function for creation customer orders. Depending on the type of the
	 * customer order calls the split, or simply copied all customer order
	 * fields into the corresponding order fields.
	 *
	 * WARNING: DOES NOT PERSIST THE CUSTOMER ORDER! For that version use {
	 * {@link #createCustomerOrder(CustorderEntity, boolean, boolean, boolean, Date)}
	 * which can handle the initial status settings depending on the obligatory
	 * forward flags.
	 *
	 * @param custorderEntity
	 * @param skipLeadTimeCheck
	 * @return
	 */
	@AdnTrace
	public CustorderDTO processCustomerOrder(
			final CustorderEntity custorderEntity,
			TruckWagonTypeWrapper truckWagon, boolean skipLeadTimeCheck) {
		CustorderEntity result = null;
		if (custorderEntity.getOrders() != null) {
			throw new LogonException();
		}

		CustorderEntity processedCustorder;

		postProcessCustorder(custorderEntity);

		DeliverySettingSalesAreaMap salesAreaMap = initSalesAreaMapForCustorder(
				custorderEntity);

		if (truckWagon == null) {
			truckWagon = getVehicleWrapperFromCustorder(custorderEntity, salesAreaMap);
		}

		validateCustorderDeliverySetting(custorderEntity, salesAreaMap);
		validatePlanningMaterial(custorderEntity);

		final CustorderSplitWrapper splitWrapper = createCustorderSplitWrapper(
				custorderEntity, truckWagon);

		CustorderUtils.splitCustorder(splitWrapper);

		String splitWrapperAsString = splitWrapper.toString();
		LOG.debug("Splitted bulkWrapper: " + splitWrapperAsString);

		final CustorderEntity splittedCustomerOrder = splitWrapper.getCustorder();

		processedCustorder = splittedCustomerOrder;

		List<OrderEntity> orders = processedCustorder.getOrders();
		Map<Long, QuantityRestrictionCalculation> quantityRestrCalcMap =
				quantityRestrictionService.getQuantityRestrCalcMap(orders);

		// Process all delivery orders (calculate costs, etc...)
		DeliverySettingSalesAreaMap settingSalesAreaMap = null;
		for (OrderEntity order : orders) {
			/**
			 * Will add a new material to the order, if it is required to be so.
			 */
			orderService.addAutoaddMaterial(order);

			if (settingSalesAreaMap == null) {

				settingSalesAreaMap = orderService.initSalesAreaMapForOrderValidation(order, true);
			}

			orderService.postProcessOrder(order, settingSalesAreaMap);
			orderService.validateOrder(order, settingSalesAreaMap, quantityRestrCalcMap, skipLeadTimeCheck);
		}

		processedCustorder.setOrders(orders);
		result = processedCustorder;
		return new CustorderDTO(result, settingSalesAreaMap, splitWrapper);
	}

	/**
	 * This is a workaround for the problem described in LOGON-4797 where it is
	 * described that the production plant attribute should actually be left
	 * empty for service items, but currently making it nullable is out of
	 * scope.
	 *
	 * Due to lack of time the current decision (24.10.2012) is to simply
	 * initialize the service item's production plant attribute to the value of
	 * the first material item's production plant.
	 *
	 * See also {OrderServiceBean#initProductionPlantForServiceItems}
	 *
	 * See also LOGON-5272
	 *
	 * @param custOrder
	 *            TODO
	 * @param custoOrderItems
	 */
	private void initProductionPlantForServiceItems(CustorderEntity custOrder,
			List<CustorderItemEntity> custoOrderItems) {

		final List<CustorderItemEntity> serviceItems = LogonPackageHandlingUtil
				.getServiceCustorderItems(custoOrderItems);
		if (serviceItems.isEmpty()) {
			return;
		}

		List<CustorderItemEntity> materialItems = LogonPackageHandlingUtil
				.getMaterialCustorderItems(custOrder.getCustorderItems());

		if (materialItems.isEmpty()) {
			materialItems = LogonPackageHandlingUtil
					.getMaterialCustorderItems(custoOrderItems);
		}
		if (materialItems.isEmpty()) {
			return;
		}

		final CustorderItemEntity item = materialItems.get(0);
		final PlantBsEntity plant = item.getProductionPlant();

		for (CustorderItemEntity serviceItem : serviceItems) {
			if (serviceItem.getProductionPlant() == null) {
				serviceItem.setProductionPlant(plant);
			}
		}

	}

	/**
	 * Handles all those initialization steps, which are not supposed to come
	 * from the UIs and handlers but are redundant in the database. There is
	 * only one rule which needs to be held, namely that only change values if
	 * they are NOT initialized sooner, never do an overwrite, if there is
	 * already a value.
	 *
	 * @param rawCustomerOrder
	 */
	private void postProcessCustorder(CustorderEntity rawCustomerOrder) {
		// [[
		Object[][] params = { { "rawCustorderEntity", rawCustomerOrder } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "postProcessCustorder", params);
		}
		// ]]
		try {
			List<CustorderItemEntity> rawCustomerOrderItems = rawCustomerOrder
					.getCustorderItems();
			List<CustorderItemEntity> custorderItems = rawCustomerOrderItems;
			initProductionPlantForServiceItems(rawCustomerOrder,
					rawCustomerOrderItems);

			int i = 0;

			for (CustorderItemEntity custorderItemEntity : custorderItems) {
				custorderItemEntity.setCustorderItemNo((i + 1) * 10L);
				OrderUtils.setContractDependentAttributes(rawCustomerOrder,
						custorderItemEntity,
						custorderItemEntity.getSapContractItem());
				i++;
			}

			if (rawCustomerOrder.getCustorderStatus() == null) {
				rawCustomerOrder
						.setCustorderStatus(referentialDataService.findBsEntity(CustorderStatusBsEntity.class,Constant.CUSTORDER_STATUS_NEW));
			}
			if (rawCustomerOrder.getDistributionDest() == null) {
				// LOGON-4365
				ShiptoSareaEntity shiptoSalesArea = orderService
						.fetchShiptoSarea(rawCustomerOrder.getShiptoSapBp(),
								rawCustomerOrder.getSarea());
				final DistributionDestBsEntity distributionDest = shiptoSalesArea
						.getDistributionDest();
				rawCustomerOrder.setDistributionDest(distributionDest);

			}
			if (rawCustomerOrder.getPlannedUnloadingTimeMin() == null) {
				ShiptoEntity shipto = deliverySettingService
						.fetchShiptoByBpIdDisableAccessRestriction(
								rawCustomerOrder.getShiptoSapBp().getSapId());
				DefaultFallbackLogic defaultFallbackLogic = new DefaultFallbackLogic(
						getConfigBean());
				CustorderItemEntity firstOrderItem = rawCustomerOrderItems.get(0);
				Long plannedUnloadingTimeMin = defaultFallbackLogic
						.getPlannedUnloadingTimeValue(firstOrderItem, shipto);
				rawCustomerOrder
						.setPlannedUnloadingTimeMin(plannedUnloadingTimeMin);
			}
			if (rawCustomerOrder.getDeliveryType().getDeliveryTypeCd()
					.equals(Constant.DELIVERY_TYPE_PICKUP)) {
				// in case the order initialize the pool and carrier fields to
				// the artificial pickup pool/carrier
				ReadWriteDao<PoolEntity> poolDao = getDaoProvider()
						.getPoolEntityDao();
				PoolEntity pool = poolDao
						.findById(Constant.ARTIFICIAL_PICKUP_POOL_ID);
				ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider()
						.getCarrierEntityDao();
				CarrierEntity carrier = carrierDao
						.findById(Constant.ARTIFICIAL_PICKUP_CARRIER_ID);
				rawCustomerOrder.setPool(pool);
				rawCustomerOrder.setCommCarrier(carrier);
			}
			if (LogonUtil.isShipPickupOrder(rawCustomerOrder
					.getDeliveryType().getDeliveryTypeCd())) {
				ReadWriteDao<PoolEntity> poolDao = getDaoProvider()
						.getPoolEntityDao();
				PoolEntity pool = poolDao
						.findById(Constant.ARTIFICIAL_SHIP_PICKUP_POOL_ID);
				ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider()
						.getCarrierEntityDao();
				CarrierEntity carrier = carrierDao
						.findById(Constant.ARTIFICIAL_PICKUP_CARRIER_ID);
				rawCustomerOrder.setPool(pool);
				rawCustomerOrder.setCommCarrier(carrier);
			}
			if (LogonUtil.isBlockTrainPickupOrder(rawCustomerOrder
					.getDeliveryType().getDeliveryTypeCd())) {
				ReadWriteDao<PoolEntity> poolDao = getDaoProvider()
						.getPoolEntityDao();
				PoolEntity pool = poolDao
						.findById(Constant.ARTIFICIAL_BLOCKTRAIN_PICKUP_POOL_ID);
				ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider()
						.getCarrierEntityDao();
				CarrierEntity carrier = carrierDao
						.findById(Constant.ARTIFICIAL_PICKUP_CARRIER_ID);
				rawCustomerOrder.setPool(pool);
				rawCustomerOrder.setCommCarrier(carrier);
			}

			//LRQ 095.5 Load confirmation SMS/Fax/Email see LOGON-7898 spec.
			//explicitly set the flags to false for the non supported cases! these cases are NOT supposed to receive SMS/email/fax currently
			if (rawCustomerOrder.getDistributionDest() == null ||
				(!rawCustomerOrder.getDistributionDest().getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_INBOUND) &&
					!rawCustomerOrder.getDistributionDest().getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_OUTBOUND) &&
					!rawCustomerOrder.getDistributionDest().getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_INTERUNIT_PROD) &&
					!rawCustomerOrder.getDistributionDest().getDistributionDestCd().equals(Constant.DISTRIBUTION_DEST_INTERUNIT_DIST)) ||
					rawCustomerOrder.getDeliveryType() == null ||
				(!rawCustomerOrder.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_BIMODAL_ROAD) &&
					 !rawCustomerOrder.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_ROAD_DIRECT) &&
					 !rawCustomerOrder.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_RAIL_DIRECT) &&
					 !rawCustomerOrder.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_PICKUP)) ||
					 LogonUtil.isLandfill(rawCustomerOrder)
					) {
				rawCustomerOrder.setLoadConfEmailFlag(Boolean.FALSE);
				rawCustomerOrder.setLoadConfSmsFlag(Boolean.FALSE);
				rawCustomerOrder.setLoadConfFaxFlag(Boolean.FALSE);
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
	 * See {{@link #postProcessCustorder(CustorderEntity)} and additionally
	 * initializes sequence values.
	 *
	 * @param custorderEntity
	 */
	private void finalInitCustorder(CustorderEntity custorderEntity) {

		// init sequences
		custorderEntity
				.setCustorderNumber(fetchSequenceNextVal("SLGNC_CUSTORDER_NUMBER"));

		postProcessCustorder(custorderEntity);

		// LOGON-6338: if there are no payer selected we select and set the
		// first one available for the CO
		// But LOGON-6762: only if the payer is NOT ignored
		if (custorderEntity.getPayerSapBp() == null && !OrderUtils.isPayerIgnored(custorderEntity.getSapContract())) {

			List<OrderEntity> orders = custorderEntity.getOrders();
			OrderEntity firstOrder = orders.get(0);
			SareaEntity sarea = firstOrder.getSarea();
			Long soldToSapBpId = firstOrder.getSoldtoSapBp().getSapBpId();

			List<SapBpEntity> payerList = businessPartnerStore.loadPayers(
					sarea.getSareaId(), soldToSapBpId);
			SapBpEntity payer = payerList.get(0);

			custorderEntity.setPayerSapBp(payer);

		}

		// LOGON-6338: if there are no billTo selected we select and set the
		// first one available for the CO
		if (custorderEntity.getBilltoSapBp() == null) {
			List<OrderEntity> orders = custorderEntity.getOrders();
			OrderEntity firstOrder = orders.get(0);
			SareaEntity sarea = firstOrder.getSarea();

			SapBpEntity shiptoSapBusinessPartner = firstOrder.getShiptoSapBp();
			String sapId = shiptoSapBusinessPartner.getSapId();

			ShiptoEntity shipToEntity = dispokarteService.fetchShiptoBySapId(sapId);
			Long shipToId = shipToEntity.getShiptoId();

			List<ShiptoBilltoEntity> billtoList = businessPartnerStore.loadBilltos(shipToId,
							sarea.getSareaId());
			ShiptoBilltoEntity shiptoBilltoEntity = billtoList.get(0);
			SapBpEntity billtoSapBp = shiptoBilltoEntity.getBilltoSapBp();

			custorderEntity.setBilltoSapBp(billtoSapBp);

		}

	}

	@AdnTrace
	public DeliverySettingSalesAreaMap initSalesAreaMapForCustorder(
			CustorderEntity custorderEntity) {

		SapBpEntity soldtoSapBp = custorderEntity.getSoldtoSapBp();
		SapBpEntity shiptoSapBp = custorderEntity.getShiptoSapBp();
		ShiptoEntity shipto = deliverySettingService.fetchShiptoByBpIdDisableAccessRestriction(shiptoSapBp.getSapId());
		SareaEntity sarea = custorderEntity.getSarea();

		ShiptoSareaEntity shiptoSarea = deliverySettingService.fetchShiptoSareaDisableAccessRestriction(shipto, sarea.getSareaId());

		Set<Long> sapMaterialIds = getSapMaterialsFromCustorder(custorderEntity);
		DeliverySettingShiptoMap shiptoMap = deliverySettingService.initDeliverySettingShiptoMapForDeliverySettingValidation(soldtoSapBp, Locale.ENGLISH, shiptoSarea, sapMaterialIds);
		DeliverySettingSalesAreaMap salesAreaMap = shiptoMap.getSalesAreaMapForSareaId(sarea.getSareaId());
		return salesAreaMap;
	}

	private Set<Long> getSapMaterialsFromCustorder(CustorderEntity custorder) {
		Set<Long> sapMaterialIds = new HashSet<Long>();
		for (CustorderItemEntity custorderItem : custorder.getCustorderItems()) {
			sapMaterialIds.add(custorderItem.getSapMaterial().getSapMaterialId());
		}
		return sapMaterialIds;
	}

	public CustorderEntity createBimodalCustorder(
			CustorderEntity bimodalRoadCustorder,
			CustorderEntity bimodalRailPartCustorder, boolean forwardToPool,
			boolean forwardToCarrier, boolean autoRelease) {
		return createBimodalCustorder(bimodalRoadCustorder,
				bimodalRailPartCustorder, forwardToPool, forwardToCarrier,
				autoRelease, null);
	}

	/**
	 * Creates two orders. Bimodal road and rail. Depending on the state of the
	 * initialization for the customer order calls the split for the customer
	 * order, or skips the splitting steps.
	 *
	 * This is necessary as on call center the splitting and customizing happens
	 * before this function is called, and the orders for the customer order are
	 * already managed by the user on screen.
	 *
	 * In the meantime from web order there is no such step, everything should
	 * be automatic.
	 *
	 * @param bimodalRoadCustorder
	 * @param bimodalRailPartCustorder
	 * @param item
	 */
	public CustorderEntity createBimodalCustorder(
			CustorderEntity bimodalRoadCustorder,
			CustorderEntity bimodalRailPartCustorder, boolean forwardToPool,
			boolean forwardToCarrier, boolean autoRelease,
			String serviceProcessIndicatorId) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "bimodalRoadCustorder", bimodalRoadCustorder },
				{ "bimodalRailPartCustorder", bimodalRailPartCustorder },
				{ "forwardToPool", forwardToPool + "" },
				{ "forwardToCarrier", forwardToCarrier + "" },
				{ "autoRelease", autoRelease + "" },
				{ "serviceProcessIndicatorId", serviceProcessIndicatorId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createBimodalCustorder", params);
		}
		// ]]
		try {
			CustorderEntity splittedBimodalRailCustomerOrder = createCustomerOrder(
					bimodalRailPartCustorder, true, true, false, null,
					Constant.WEIGH_SYSTEM_LP_LOGON, true);

			List<OrderEntity> splittedBimodalRailOrders = splittedBimodalRailCustomerOrder.getOrders();
			if (splittedBimodalRailOrders.size() != 1) {
				throw new LogonException(
						"Bimodal rail order contains more than one single delivery order.",
						Reason.RES_ERROR_IMPL);
			}

			CustorderEntity splittedBimodalRoadCustOrder = createCustomerOrder(
					bimodalRoadCustorder, forwardToPool, forwardToCarrier,
					autoRelease, null, Constant.WEIGH_SYSTEM_LP_LOGON, false,
					serviceProcessIndicatorId, true);

			OrderEntity bimodalRailOrder = splittedBimodalRailOrders.get(0);

			orderService.createBimodalOrder(
					splittedBimodalRoadCustOrder.getOrders(), bimodalRailOrder);

			return result = splittedBimodalRailCustomerOrder;

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
	 * Merges(updates) the parameter custorderEntity provided. The custorder may
	 * have new orders, for those orders the very same steps are supposed to be
	 * taken as if they would be created initially from the order taking sources
	 * like call center/web order/LDS.
	 *
	 * @param custorderEntity
	 * @return
	 */
	public CustorderEntity mergeCustomerOrder(CustorderEntity custorderEntity) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "custorderEntity", custorderEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mergeCustomerOrder", params);
		}
		// ]]
		try {
			final List<OrderEntity> orders = custorderEntity.getOrders();
			List<OrderEntity> newOrders = orderService.finalizeOrderCreation(orders);

			custorderEntity.setOrders(orders);
			orders.addAll(newOrders);

			ReadWriteDao<CustorderEntity> customerOrderDataObject = getDaoProvider().getCustorderEntityDao();
			custorderEntity = customerOrderDataObject.merge(custorderEntity);

			processNewOrders(false, false, false, newOrders, null, Constant.WEIGH_SYSTEM_LP_LOGON);

			return result = custorderEntity;

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
	 * Does all those steps which are supposed to be taken for new orders of the
	 * customer order. New orders does not necessarily mean that they are in
	 * status new, in certain cases it is intended to create orders immediately
	 * in status transmitted to pool/carrier or transmitted to carrier
	 * acknowledged (simple vehicle orders like ship and block-train).
	 *
	 * @param forwardToPool
	 *            depending on this flag the status will be arranged to
	 *            TRANSMITTED TO POOL
	 * @param forwardToCarrier
	 *            depending on this flag the status will be arranged to
	 *            TRANSMITTED TO CARRIER
	 * @param autoRelease
	 *            depending on this flag there will be an automatic release to
	 *            planning trucks of the commercial carrier
	 * @param newOrders
	 */
	private void processNewOrders(Boolean forwardToPool,
			Boolean forwardToCarrier, Boolean autoRelease,
			List<OrderEntity> newOrders, Date plannedDeliveryTime,
			long weighSystemLoadingpoint) {
		processNewOrders(forwardToPool, forwardToCarrier, autoRelease,
				newOrders, plannedDeliveryTime, weighSystemLoadingpoint, DOES_NOT_COME_FROM_LDS);
	}

	/**
	 * Does all those steps which are supposed to be taken for new orders of the
	 * customer order. New orders does not necessarily mean that they are in
	 * status new, in certain cases it is intended to create orders immediately
	 * in status transmitted to pool/carrier or transmitted to carrier
	 * acknowledged (simple vehicle orders like ship and block-train).
	 *
	 * @param forwardToPool
	 *            depending on this flag the status will be arranged to
	 *            TRANSMITTED TO POOL
	 * @param forwardToCarrier
	 *            depending on this flag the status will be arranged to
	 *            TRANSMITTED TO CARRIER
	 * @param autoRelease
	 *            depending on this flag there will be an automatic release to
	 *            planning trucks of the commercial carrier
	 * @param newOrders
	 * @param isLdsMessage
	 *            the call comes from the LDS
	 */

	private void processNewOrders(Boolean forwardToPool,
			Boolean forwardToCarrier, Boolean autoRelease,
			List<OrderEntity> newOrders, Date plannedDeliveryTime,
			long weighSystemLp, boolean isLdsMessage) {
		// [[
		Object[][] params = {{"forwardToPool", forwardToPool},
							{"forwardToCarrier", forwardToCarrier},
							{"autoRelease", autoRelease},
							{"newOrders", newOrders},
							{"plannedDeliveryTime", plannedDeliveryTime},
							{"weighSystemLp", weighSystemLp},
							{"isLdsMessage", isLdsMessage}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processNewOrders", params);
		}
		// ]]
		try {
			List<AssignmentEntity> transmitableAssignments = createNewOrders(forwardToPool,
					forwardToCarrier, autoRelease,
					newOrders, plannedDeliveryTime,
					weighSystemLp, isLdsMessage);

			transmitAssignmentsToWeighSystem(transmitableAssignments);
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
	 * It transmitts all of the assignments got through the parameter to the weight system and possible sends loadOrderNotification
	 * @param transmittableAssignments
	 */
	private void transmitAssignmentsToWeighSystem(List<AssignmentEntity> transmittableAssignments) {
		// [[
		Object[][] params = {{"transmittableAssignments", transmittableAssignments}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "transmitAssignmentsToWeighSystem", params);
		}
		// ]]
		try {
			for (AssignmentEntity transmittableAssignment : transmittableAssignments) {
				orderService.transmitToWeighSystem(transmittableAssignment);
				attachAssignmentAndAssignmentParts(transmittableAssignment);
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
	 * @param transmittableAssignment
	 */
	private void attachAssignmentAndAssignmentParts(AssignmentEntity transmittableAssignment) {
		for(AssignmentPartEntity assignmentPart : transmittableAssignment.getAssignmentParts()) {
			LoadEntity load = assignmentPart.getLoad();
			if (load != null) {
				em.merge(load);
			}
			em.merge(assignmentPart);
		}
		em.merge(transmittableAssignment);
	}

	@AdnTrace
	public List<AssignmentEntity> createNewOrders(Boolean forwardToPool,
			Boolean forwardToCarrier, Boolean autoRelease,
			List<OrderEntity> newOrders, Date plannedDeliveryTime,
			long weighSystemLoadingPoint, boolean isLdsMessage) {
		final BizeventTypeBsEntity businessEventTypeNew = referentialDataService.findBsEntity(BizeventTypeBsEntity.class, Constant.BIZEVENT_TYPE_NEW);

		createAndSendBusinessEvent(newOrders, businessEventTypeNew);

		for (OrderEntity order : newOrders) {
			kafkaPublisherService.publishNewOrder(order);
		}

		// create and initialize a map to collect required bizevents to send according initial order status
		final Map<Long, List<OrderEntity>> ordersByBusinessEventType = new HashMap<Long, List<OrderEntity>>();
		ordersByBusinessEventType.put(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL,
				new ArrayList<OrderEntity>());
		ordersByBusinessEventType.put(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK,
				new ArrayList<OrderEntity>());
		ordersByBusinessEventType.put(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER,
				new ArrayList<OrderEntity>());
		ordersByBusinessEventType.put(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK,
				new ArrayList<OrderEntity>());

		for (OrderEntity order : newOrders) {
			final OrderStatusBsEntity initialSataus = initialOrderStatus(forwardToPool, forwardToCarrier, order);
			order.setOrderStatus(initialSataus);

			if (order.getArchivingStatus() == null) {
				ArchivingStatusBsEntity archivingStatusBsEntity = referentialDataService.findBsEntity(ArchivingStatusBsEntity.class,
						Constant.ARCHIVING_STATUS_NEW);
				order.setArchivingStatus(archivingStatusBsEntity);
			}

			final Long initialStatusCode = initialSataus.getOrderStatusCd();
			if (initialStatusCode == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) {
				ordersByBusinessEventType.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL).add(
						order);
			}
			else if (initialStatusCode == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) {
				ordersByBusinessEventType.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL).add(
						order);
				ordersByBusinessEventType.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK).add(
						order);
			}
			else if (initialStatusCode == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
				ordersByBusinessEventType.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL).add(
						order);
				ordersByBusinessEventType.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK).add(
						order);
				ordersByBusinessEventType.get(
						Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER).add(order);
			}
			else if (initialStatusCode == Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK) {
				ordersByBusinessEventType.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL).add(
						order);
				ordersByBusinessEventType.get(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK).add(
						order);
				ordersByBusinessEventType.get(
						Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER).add(order);
				ordersByBusinessEventType.get(
						Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK).add(order);
			}

		}

		// create the biz events
		List<Long> sortedKeys = new ArrayList<Long>(ordersByBusinessEventType.keySet());
		Collections.sort(sortedKeys);
		for (Long businessEventTypeCode : sortedKeys) {
			final BizeventTypeBsEntity businessEventType = referentialDataService.findBsEntity(
					BizeventTypeBsEntity.class, businessEventTypeCode);
			final List<OrderEntity> orders = ordersByBusinessEventType.get(businessEventTypeCode);
			if (orders != null && !orders.isEmpty()) {
				createAndSendBusinessEvent(orders, businessEventType);
			}
		}

		Long deliveryTypeCode;
		List<AssignmentEntity> assignments = new ArrayList<AssignmentEntity>();
		for (OrderEntity order : newOrders) {
			DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
			deliveryTypeCode = deliveryType.getDeliveryTypeCd();
			AssignmentEntity assignment = null;

			PoolEntity pool = order.getPool();
			if (pool != null && LogonUtil.isPickupOrder(deliveryTypeCode)) {
				final Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil
						.getMaterialItems(order);
				final OrderItemEntity bulkPowderItem = LogonUtil
						.getBulkPowderItem(orderItems);
				final OrderItemEntity bulkDumpedItem = LogonUtil
						.getBulkDumpedItem(orderItems);

				// The order has either only a "bulk dumped" or a
				// "bulk powder" item, or otherwise it's a bag order.
				if (bulkPowderItem != null) {
					assignment = createAssignmentAndTransmitToDriverWithoutWeightSystemCall(
							order,
							Long.valueOf(Constant.ARTIFICIAL_PICKUP_BULK_PWDR_TRUCK_ID),
							null, weighSystemLoadingPoint, isLdsMessage);
				}
				else if (bulkDumpedItem != null) {
					assignment = createAssignmentAndTransmitToDriverWithoutWeightSystemCall(
							order,
							Long.valueOf(Constant.ARTIFICIAL_PICKUP_BULK_DMP_TRUCK_ID),
							null, weighSystemLoadingPoint, isLdsMessage);
				}
				else {
					assignment = createAssignmentAndTransmitToDriverWithoutWeightSystemCall(
							order,
							Long.valueOf(Constant.ARTIFICIAL_PICKUP_BAG_TRUCK_ID),
							null, weighSystemLoadingPoint, isLdsMessage);
				}
			}
			else if (pool != null
						&& (LogonUtil.isShipPickupOrder(deliveryTypeCode)
						|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCode))) {
					createAndTransmitShipOrBlocktrainPickupAssignment(order, deliveryTypeCode);
			}
			else if (BooleanUtils.isTrue(autoRelease)
					&& LogonUtil.isTruckOrder(deliveryTypeCode)) {
				Long carrierId = null;

				// commercial carrier set -> transmit to the commercial
				// carrier's truck
				CarrierEntity commercialCarrier = order.getCommCarrier();
				if (commercialCarrier != null) {
					carrierId = commercialCarrier.getCarrierId();
				}
				// no commercial carrier -> transmit to generic carrier's
				// planning truck
				else {
					CarrierEntity genericCarrier = carrierService.findArtificalGenericCarrier();
					order.setCommCarrier(genericCarrier);
					carrierId = genericCarrier.getCarrierId();
					OrderStatusBsEntity newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
					order.setOrderStatus(newOrderStatus);
				}

				List<OrderTruckTypeEntity> allowedOrderTruckTypes = new ArrayList<OrderTruckTypeEntity>();
				allowedOrderTruckTypes.addAll(order.getOrderVehicleTypes());
				/**
				 * This sorting could be moved to view as well. 
				 * However, the current implementation of the comparator is too complicated 
				 * for just converting it into a simple order by.
				 */
				Collections.sort(allowedOrderTruckTypes, new OrderTruckTypeComparator());

				Long topPriorityPlanningTruckId = null;

				Long poolId = null;
				if (pool != null) {
					poolId = pool.getPoolId();
				}
				topPriorityPlanningTruckId = findTopPriorityPlanningTruckId(poolId, carrierId, allowedOrderTruckTypes);

				if (topPriorityPlanningTruckId == null) {
					LOG.error("No appropriate truck found for automatic assignment creation.");
					String[] additionalInfo = {};
					CustorderEntity customerOrder = order.getCustorder();
					throw new ValidationException(
							new ValidationErrorReason(
									CustorderError.AUTORELEASE_NO_APPROPRIATE_TRUCK,
									customerOrder, additionalInfo));
				}
				if (pool == null) {
					DaoProvider daoProvider = getDaoProvider();
					final ReadOnlyDao<PoolTruckEntity> poolTruckDao = daoProvider.getPoolTruckEntityDao();
					final Filter<PoolTruckEntity> poolTruckFilter = poolTruckDao.createFilter();
					poolTruckFilter.addExpression(Restrictions.eq(PoolTruckEntity.ATTR_TRUCK_ID,
							topPriorityPlanningTruckId));

					final List<PoolTruckEntity> poolTrucks = poolTruckDao.find(poolTruckFilter);
					if (poolTrucks.size() == 1) {
						order.setPool(poolTrucks.get(0).getPool());
					}
					else {
						final ReadOnlyDao<PoolCarrierEntity> poolCarrierDao = daoProvider.getPoolCarrierEntityDao();
						final Filter<PoolCarrierEntity> poolCarrierFilter = poolCarrierDao
								.createFilter();
						poolCarrierFilter.addExpression(Restrictions.eq(
								PoolCarrierEntity.ATTR_CARRIER_ID, carrierId));

						final List<PoolCarrierEntity> poolCarriers = poolCarrierDao.find(poolCarrierFilter);
						if (poolCarriers.size() == 1) {
							order.setPool(poolCarriers.get(0).getPool());
						}
						else {
							LOG.warn("Order transmission will maybe fail, since there is no pool set"
									+ " (because the carrier is the artificial generic carrier), order="
									+ order.getOrderNumber());
						}
					}
				}

				try {
					assignment = createAssignmentAndTransmitToDriverWithoutWeightSystemCall(order,
							topPriorityPlanningTruckId, plannedDeliveryTime,
							weighSystemLoadingPoint, isLdsMessage);
				}
				catch (Exception exception) {
					ExceptionUtil.errException(LOG, exception, null);
					throw new LogonException(
							"Could not create and release assignment automatically. Set autorelease off to prevent automatic processing.",
							Reason.RES_ORDER_ASSIGNMENT_AUTOCREATE_ERROR_GENERAL);
				}
			}

			if (orderService.isPricingCallRequired(order)) {
				LOG.debug("Pricing call required");
				pricingCallService.sendPricingCall(order);
			}

			if (assignment != null) {
				assignments.add(assignment);
			}
		}
		return assignments;
	}

	/** For the given order creates the assignment and
	 *  transmits it to the simple vehicle. Just the shippickup and blocktrainpickup
	 *  are acceptable.
	 *
	 * @param order
	 * @param deliveryTypeCd
	 */
	private void createAndTransmitShipOrBlocktrainPickupAssignment(OrderEntity order,
																	Long deliveryTypeCd) {
		// [[
		Object[][] params = {{"order", order},
							{"deliveryTypeCd", deliveryTypeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createAndTransmitSimpleVehicleAssignment", params);
		}
		// ]]
		try {
			ReadWriteDao<SimpleVehicleEntity> simpleVehicleDao = getDaoProvider().getSimpleVehicleEntityDao();
			SimpleVehicleEntity simpleVehicle;
			if (LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)) {
				simpleVehicle = simpleVehicleDao.findById(Constant.SIMPLE_VEHICLE_BLOCKTRAIN_PICKUP_CD);
			}
			else {
				simpleVehicle = simpleVehicleDao.findById(Constant.SIMPLE_VEHICLE_SHIP_PICKUP_CD);
			}

			Long assignmentId = orderService.assignOrderToSimpleVehicle(order, simpleVehicle);
			simpleVehicleSchedulingService.releaseAssignment(assignmentId);
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
	 * Determines the status for a new order. This depends on a number of
	 * factors. The rules are, in the order of priority:
	 * <ol>
	 * <li>Pickup orders always get initial status "new".</li>
	 * <li>ServiceOnly and ReturnPickup orders always get initial status
	 * "delivered".</li>
	 * <li>Simple vehicle orders automatically have the initial status
	 * "accepted by carrier". This requires them to have the pool and carrier
	 * set. If a simple vehicle order has no pool or no carrier set, an
	 * exception is thrown.</li>
	 * <li>Web orders always get initial "new".</li>
	 * <li>If the order has a carrier set and <code>forwardToCarrier</code> is
	 * <code>true</code>, then the order gets the status
	 * "transmitted to carrier".</li>
	 * <li>If the order has a pool set and <code>forwardToPool</code> is true,
	 * then the order gets the status "transmitted to pool".</li>
	 * <li>In all other cases, the order gets the status "new".</li>
	 * </ol>
	 *
	 * @param forwardToPool
	 *            Flag corresponding to the "forward to pool" checkbox in the
	 *            order entry screen. If this is set and a pool is set in the
	 *            order, and it is not forwarded directly to a carrier (see
	 *            <code>forwardToCarrier</code>), then the returned status is
	 *            "forward to pool".
	 * @param forwardToCarrier
	 *            Flag corresponding to the "forward to carrier" checkbox in the
	 *            order entry screen. If this is set and a carrier is set in the
	 *            order, then the returned status is "forward to carrier".
	 * @param order
	 *            The new order
	 * @param refDataGenericsService TODO
	 * @return The status to give the new order.
	 * @throws LogonException
	 *             if the order's delivery type is single vehicle but the order
	 *             does not have both a pool and a carrier set.
	 */
	private OrderStatusBsEntity initialOrderStatus(Boolean forwardToPool, Boolean forwardToCarrier, OrderEntity order) {

		final OrderStatusBsEntity newOrderStatus;

		final Long deliveryTypeCd = order.getDeliveryType().getDeliveryTypeCd();

		if (LogonPackageHandlingUtil.isServiceOnlyCustomerOrder(order
				.getCustorder())
				|| LogonUtil.isReturnPickupOrder(deliveryTypeCd)) {
			newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_DELIVERED);
		}
		else if (deliveryTypeCd.equals(Constant.DELIVERY_TYPE_PICKUP)
				|| LogonUtil.isShipPickupOrder(deliveryTypeCd)
				|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd)) {
			newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
		}
		else if (LogonUtil.isSimpleVehicleOrder(deliveryTypeCd)) {
			if (order.getCommCarrier() != null && order.getPool() != null) {
				newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);
			}
			else {
				String errMsg = "Pool and Carrier are mandatory for simple vehicle orders.";
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_ERROR_IMPL);
			}
		}
		else if (order.getCustWeborderNote() != null
				&& !order.getCustWeborderNote().isEmpty()) {
			newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_NEW);
		}
		else if (forwardToCarrier && order.getCommCarrier() != null) {
			newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
		}
		else if (forwardToPool && order.getPool() != null) {
			newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
		}
		else {
			newOrderStatus = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_NEW);
		}
		return newOrderStatus;
	}

	/**
	 * Tries to find a planning truck which
	 * - belongs to the specified commercial carrier and pool
	 * - has the biggest priority
	 * - has the most appropriate order truck type
	 * - is active
	 *
	 * @param poolId
	 * @param carrierId
	 * @param allowedOrderTruckTypes sorted list of possible order truck types
	 *
	 * @return truckId if found, otherwise null
	 */
	private Long findTopPriorityPlanningTruckId(Long poolId,
			Long carrierId, List<OrderTruckTypeEntity> allowedOrderTruckTypes) {
		List<Long> allowedTruckTypeCodes = new ArrayList<Long>();
		for (OrderTruckTypeEntity orderTruckTypeEntity : allowedOrderTruckTypes) {
			TruckTypeBsEntity truckType = orderTruckTypeEntity.getTruckType();
			Long truckTypeCode = truckType.getTruckTypeCd();
			allowedTruckTypeCodes.add(truckTypeCode);
		}
		List<PlanningTruckEntity> possiblePlanningTrucks = truckAdminService.findPlanningTrucksByPoolCarrierAndTruckType(
				poolId, carrierId, allowedTruckTypeCodes);
		Map<Long, PlanningTruckEntity> planningTrucksByTruckTypeCode = new HashMap<Long, PlanningTruckEntity>();
		/**
		 * Currently the view is not selecting only the truck with max priority 
		 * for the given { carrier, pool, truck type } triple.
		 * In practice, since view is ordered by priority, the latest entity will remain 
		 * in the map below, so the one with the highest priority wins.
		 */
		for (PlanningTruckEntity planningTruck : possiblePlanningTrucks) {
			Long truckTypeCode = planningTruck.getTruckTypeCd();
			planningTrucksByTruckTypeCode.put(truckTypeCode, planningTruck);
		}
		for (OrderTruckTypeEntity orderTruckType : allowedOrderTruckTypes) {
			TruckTypeBsEntity truckType = orderTruckType.getTruckType();
			Long truckTypeCode = truckType.getTruckTypeCd();
			PlanningTruckEntity planningTruck = planningTrucksByTruckTypeCode.get(truckTypeCode);
			if (planningTruck != null) {
				return planningTruck.getTruckId();
			}
		}
		return NO_APPROPRIATE_PLANNING_TRUCK_FOUND;
	}

	/**
	 * For each and every order specified creates the bulk pickup assignment and
	 * transmits them to the pickup truck.
	 *
	 * @param order
	 * @param truckId
	 * @param plannedDeliveryTime
	 * @param weighSystemLp
	 * @param isLdsMessage
	 */
	private AssignmentEntity createAssignmentAndTransmitToDriverWithoutWeightSystemCall(OrderEntity order,
			Long truckId, Date plannedDeliveryTime, long weighSystemLp,
			boolean isLdsMessage) {

		final Long loadingTime = SchedulingUtils.calculateLoadingTimeForOrder(
				order, getConfigBean());
		ShiptoEntity shipto = dispokarteService.fetchShiptoBySapId(order.getShiptoSapBp().getSapId());

		TimeZone deliveryStartTimeZone;

		Date calculatedLatestDeliveryStart;
		if (plannedDeliveryTime == null && isLdsMessage) {
			// special case see ticket LOGON-6126
			calculatedLatestDeliveryStart = new Date();
			deliveryStartTimeZone = TimeZone.getAppTimeZone();
		} else {
			deliveryStartTimeZone = timeZoneService.loadForShipTo(shipto, Locale.ENGLISH);
			calculatedLatestDeliveryStart = SchedulingUtils
					.calculateLatestDeliveryStart(order, getConfigBean(),
							shipto, plannedDeliveryTime, deliveryStartTimeZone);
		}

		// create an assignment for the artificial pool/carrier/truck and set it
		// to dispatched immediately
		Long transportDurationMin = order.getTransportDurationMin();
		Long plannedUnloadingTimeMin = order.getPlannedUnloadingTimeMin();
		final AssignmentEntity assignment = orderService.createAssignmentForTruckWithOrderDispatched(
				truckId,
				order,
				calculatedLatestDeliveryStart,
				deliveryStartTimeZone,
				loadingTime.intValue(),
				transportDurationMin.intValue(),
				plannedUnloadingTimeMin.intValue());

		orderService.setManuallyScheduledFlag(order, weighSystemLp);

		OrderStatusBsEntity orderStatus = order.getOrderStatus();
		Long orderStatusCd = orderStatus.getOrderStatusCd();
		if (orderStatusCd == Constant.ORDER_STATUS_DISPATCHED) {
			dispoService.transmitToDriver(assignment.getAssignmentId(),
					Constant.WEIGH_SYSTEM_SP_NONE,
					Constant.WEIGH_SYSTEM_LP_LOGON, DispoServiceBean.DISALLOW_TRANSMIT_ASSIGNMENT_TO_WEIGHT_SYSTEM);
		}

		return assignment;
	}

	/**
	 * Persist both the customer order and the single delivery order(s).
	 *
	 * WARNING: This method expects the order list of the customer order already
	 * populated, will NOT do any kind of splitting.
	 *
	 * In the meantime it will call postProcessOrder for each single delivery
	 * order.
	 *
	 * @param custorderEntity
	 *            - which has a non-null non-empty list of single delivery
	 *            order(s)
	 * @return persisted {@link CustorderEntity} entity
	 */
	private CustorderEntity persistCustomerOrder(CustorderDTO processCustorderResult, boolean filterFutureContracts) {
		// [[
		CustorderEntity result = null;
		Object[][] params = { { "processCustorderResult", processCustorderResult } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "persistCustomerOrder", params);
		}
		// ]]
		try {
			CustorderEntity custorderEntity = processCustorderResult.getCustorderEntity();

			if (custorderEntity.getOrders() == null
					|| custorderEntity.getOrders().isEmpty()) {
				throw new LogonException(
						"Persisting customer order is not possible without single delivery orders",
						Reason.RES_ERROR_IMPL);
			}
			ReadWriteDao<CustorderEntity> custorderEntityDao = getDaoProvider()
					.getCustorderEntityDao();

			List<OrderEntity> orders = custorderEntity.getOrders();
			finalInitCustorder(custorderEntity);

			// Persist the customer order entity.

			orders = orderService.finalizeNewOrderCreation(orders, processCustorderResult.getDeliverySettingSalesAreaMap(), filterFutureContracts);
			custorderEntity.setOrders(orders);
			custorderEntity = custorderEntityDao.persist(custorderEntity);

			return result = custorderEntity;
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
	 * Creates a @{link BizeventEntity} and persists it.
	 *
	 * @param orders
	 *            - list of orders which the code will create bizevents for
	 * @param businessEventType
	 *            - will be set on the entity
	 * @return
	 */
	private void createAndSendBusinessEvent(List<OrderEntity> orders,
			BizeventTypeBsEntity businessEventType) {
		final Date eventTimeStamp = new Date();
		for (OrderEntity order : orders) {
			BizeventEntity businessEvent = new BizeventEntity();
			businessEvent.setBizeventType(businessEventType);
			businessEvent.setOrder(order);
			businessEvent.setEventTs(eventTimeStamp);
			monitoringTablesService.processBizevent(businessEvent);
		}
	}

	/**
	 * Sets payer to empty if auto payer-determination flags are ON
	 * in customer order and in all belonging order
	 */
	public void cleanUpPayerIfInvalid(CustorderEntity custorder) {
		// [[
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cleanUpPayerIfInvalid", params);
		}
		// ]]
		try {
			if (OrderUtils.isPayerIgnored(custorder.getSapContract())) {
				custorder.setPayerSapBp(null);
			}
			for(OrderEntity order : custorder.getOrders()) {
				orderService.cleanUpPayerIfInvalid(order);
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

}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.crh.logon.business.controllers.scheduling.containers.LoadTransferContainer;
import com.crh.logon.business.controllers.scheduling.containers.OrderItemLoadTransferContainer;
import com.crh.logon.business.services.beans.GoogleMapsDistanceMatrixResultBean;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.dispatchers.DispoServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ArrayDescriptor;

import org.apache.commons.beanutils.BeanUtils;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.beans.LoadMeasureData;
import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.beans.WagonPreLoadBean;
import com.crh.logon.business.services.localinterfaces.BizeventAuditServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CarrierServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigurableSequencesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoogleMapsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MopEventServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAssignmentServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentModificationDTO;
import com.crh.logon.business.services.utils.AssignmentPartWrapper;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.AssignmentWrapper;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.utils.ConfigInitializer;
import com.crh.logon.business.services.utils.ContainerWrapper;
import com.crh.logon.business.services.utils.CustOrderWrapper;
import com.crh.logon.business.services.utils.CustorderItemSumQtyWrapper;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.business.services.utils.DispoLoadtargetPrioWrapper;
import com.crh.logon.business.services.utils.DriverBreaksUtils;
import com.crh.logon.business.services.utils.IndexedObject;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderItemOrderedQtyWrapper;
import com.crh.logon.business.services.utils.OrderWrapper;
import com.crh.logon.business.services.utils.RailAssignmentWrapper;
import com.crh.logon.business.services.utils.RailLoadUtils;
import com.crh.logon.business.services.utils.ReroutingAssignmentUtil;
import com.crh.logon.business.services.utils.ReroutingOrderWrapper;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.business.services.utils.ShippingpointLoadtargetManager;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.TruckAndAssignmentModificationDTO;
import com.crh.logon.business.services.utils.TruckModificationDTO;
import com.crh.logon.business.services.utils.TruckWrapper;
import com.crh.logon.business.services.utils.WagonWrapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverBreakEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatCompatMatEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PdaMessageEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.TrackLoadingpointEntity;
import com.crh.logon.persistence.entities.common.TrafficJamEntity;
import com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.AssignTrStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PdaMessageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantTrackBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrierPfEntity;
import com.crh.logon.persistence.entities.views.CustordItemSumQtyExtEntity;
import com.crh.logon.persistence.entities.views.CustorderItemSumQtyEntity;
import com.crh.logon.persistence.entities.views.CustorderViewEntity;
import com.crh.logon.persistence.entities.views.DispatchingCarrierEntity;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoAssignPoolCarrEntity;
import com.crh.logon.persistence.entities.views.DispoAssignTruckEntity;
import com.crh.logon.persistence.entities.views.DispoAssignmentListEntity;
import com.crh.logon.persistence.entities.views.DispoDayLoadsEntity;
import com.crh.logon.persistence.entities.views.DispoDriverBreakListEntity;
import com.crh.logon.persistence.entities.views.DispoExtOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoHomePlantEntity;
import com.crh.logon.persistence.entities.views.DispoLoadtargetEntity;
import com.crh.logon.persistence.entities.views.DispoMaxWagonPosEntity;
import com.crh.logon.persistence.entities.views.DispoMessageListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoPlanningEntity;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.DispoRailAssListEntity;
import com.crh.logon.persistence.entities.views.DispoShiptoSoldtoEntity;
import com.crh.logon.persistence.entities.views.DispoTruckListV2Entity;
import com.crh.logon.persistence.entities.views.DispoTruckLoadListV2Entity;
import com.crh.logon.persistence.entities.views.DispoTruckTypeListEntity;
import com.crh.logon.persistence.entities.views.DispoWContainerListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonLoadListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonTrspCatEntity;
import com.crh.logon.persistence.entities.views.DispoWagonTypeListEntity;
import com.crh.logon.persistence.entities.views.OrderItemOrderedQtyEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierResEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;
import com.crh.logon.persistence.entities.views.SapbpRailwayStationEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.TransferContext;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@Interceptors(UserInfoInterceptor.class)
@Named
@AdnTrace
public class DispoServiceBean extends DispoServiceDispatcher implements DispoServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispoServiceBean.class);

	public static final boolean ALLOW_TRANSMIT_ASSIGNMENT_TO_WEIGHT_SYSTEM = true;
	public static final boolean DISALLOW_TRANSMIT_ASSIGNMENT_TO_WEIGHT_SYSTEM = false;
	private static final boolean DO_NOT_SEND_GOODS_MOVEMENTS = false;
	private static final BigDecimal NO_GROSS_WEIGHT = null;
	private static final Date NO_LOADING_START_TIME = null;
	private static final Date NO_LOADING_END_TIME = null;
	private static final BigDecimal NO_TARE_WEIGHT = null;
	private static final Long PARTS_SEQ_NO_ZERO = 0L;
	@EJB private ConfigurableSequencesServiceLocalInterface sequenceService;
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private GoogleMapsServiceLocalInterface googleMapsService;
	@EJB private LoadingServiceLocalInterface loadingService;
	@EJB private GoodsMovementServiceLocalInterface goodsMovementService;
	@EJB private ConfigServiceLocalInterface configService;
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;
	@EJB private BizeventAuditServiceLocalInterface bizeventAuditService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private UserServiceLocalInterface userService;
	@EJB private CarrierServiceLocalInterface carrierService;
	@EJB private PoolAssignmentServiceLocalInterface poolAssignmentService;
	@EJB private MopEventServiceLocalInterface mopEventService;
	@EJB private OrderDetailServiceLocalInterface orderDetailService;
	@EJB private DispokarteServiceLocalInterface dispokarteService;
	@EJB private ShippingPointServiceLocalInterface shippingPointService;
	@EJB private TimeZoneServiceLocalInterface timeZoneService;


	/**
	 * Fetch a list with all carriers for a pool, and builds up {@link DispoCarrierWrapper} entries.
	 * These entries also contain the available partner functions per commercial carrier. Used on the scheduling
	 * screen where dispatching can only happen on partner carrier's trucks.
	 *
	 * @param poolId id of the pool
	 * @return a list with all carriers for given pool
	 */
	public List<DispoCarrierWrapper> fetchCarriersForPool(Long poolId) {
		List<PoolCarrierCarrierEntity> carriersForPool = carrierService.fetchCarriersForPoolBasic(poolId);
		if (carriersForPool.isEmpty()) {
			return null;
		}
		List<Long> commercialCarrierIds = new ArrayList<Long>();
		for (PoolCarrierCarrierEntity carrier : carriersForPool) {
			if (carrier.getVnIsCommCarrier()) {
				commercialCarrierIds.add(carrier.getCarrierId());
			}
		}

		List<CommExecCarrierPfEntity> carrierPartnerships = commercialCarrierIds.isEmpty() ?
				new ArrayList<CommExecCarrierPfEntity>() : carrierService.findPartnerExecCarriersOrdered(commercialCarrierIds);

		Map<Long, List<CommExecCarrierPfEntity>> carrierPartnershipByCarrierId = new HashMap<Long, List<CommExecCarrierPfEntity>>();

		for (CommExecCarrierPfEntity carrierPartnership : carrierPartnerships) {
			if (carrierPartnership == null) {
				continue;
			}

			Long commercialCarrierId = carrierPartnership.getCommCarrierId();
			List<CommExecCarrierPfEntity> partnershipsOfCurrentCarrierId = carrierPartnershipByCarrierId.get(commercialCarrierId);

			if (partnershipsOfCurrentCarrierId == null) {
				partnershipsOfCurrentCarrierId = new ArrayList<CommExecCarrierPfEntity>();
				carrierPartnershipByCarrierId.put(commercialCarrierId,
						partnershipsOfCurrentCarrierId);
			}

			partnershipsOfCurrentCarrierId.add(carrierPartnership);
		}

		//populate the map for carrierId -> carrierWrapper
		Map<Long, DispoCarrierWrapper> carrierWrapperMap = new LinkedHashMap<Long, DispoCarrierWrapper>();
		for (PoolCarrierCarrierEntity carrier : carriersForPool) {
			DispoCarrierWrapper carrierWrapper = new DispoCarrierWrapper(carrier);
			Long carrierId = carrier.getCarrierId();
			carrierWrapperMap.put(carrierId, carrierWrapper);
			final List<CommExecCarrierPfEntity> carrierPartnerFunctions = carrierPartnershipByCarrierId.get(carrierId);
			if (carrierPartnerFunctions != null) {
				for (CommExecCarrierPfEntity partnerFunction : carrierPartnerFunctions) {
					carrierWrapper.addExecCarrierPartner(partnerFunction);
				}
			}
		}

		final Collection<DispoCarrierWrapper> values = carrierWrapperMap.values();
		final List<DispoCarrierWrapper> dispoCarrierWrappers = new ArrayList<DispoCarrierWrapper>(
				values);

		return dispoCarrierWrappers;
	}

	/**
	 * Fetch a list with restricted pools
	 * @param poolIds IDs of the pools
	 * @return a list with restricted pools
	 */
	public List<Long> fetchRestrictedPoolIds(List<Long> poolIds) {
		final ReadOnlyDao<PoolCarrierCarrierResEntity> carrierDao = getDaoProvider().getPoolCarrierCarrierResEntityDao();

		JPAQuery<PoolCarrierCarrierResEntity> query = carrierDao.createJPAQuery();
		query.setDistinct(true);
		query.addSelect(SelectExpression.simpleSelect(PoolCarrierCarrierResEntity.ATTR_POOL_ID));

		final Filter<PoolCarrierCarrierResEntity> filter = new FilterImpl<PoolCarrierCarrierResEntity>();
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(PoolCarrierCarrierResEntity.ATTR_POOL_ID, poolIds));
		query.setFilter(filter);
		return carrierDao.find(query);
	}

	/**
	 * for customer orders
	 *
	 * fetch a list with all orders for a pool, carrier list and date
	 * @param date date of dispo
	 * @param poolId id of the pool selected on dispo screen - only used if no carrier filter is
	 *                applied in this case the pool has to be considered
	 * @param carriers collection of the ids of the carriers
	 * @param areOrdersWithoutAssignedCarrierIncluded include orders with no assigned carrier
	 * @param langCd user language
	 * @param extOrders includes external orders if true
	 * @return a list with all bulk orders with status "transmitted to carrier"
	 * 			and pool and carrier according to the given ids and
	 * 			from date < date < to date
	 */
	private List fetchOrdersForDispo(Date date, Long poolId,
									 List<Long> carriers,
									 Collection<String> plantCodes,
									 boolean areOrdersWithoutAssignedCarrierIncluded,
									 Locale locale, boolean extOrders, TimeZone userTimeZone, Long orderNumber, boolean hideTrucksAndOrderOfPartners) {
		if (carriers.isEmpty() && !areOrdersWithoutAssignedCarrierIncluded) {
			return Collections.emptyList();
		}

		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<DispoOrderListEntity> dispoOrderListDao = daoProvider.getDispoOrderListEntityDao();
		Filter filter = new FilterImpl();

		Calendar dateAsCalendar = userTimeZone.asCalendar(date);
		dateAsCalendar.add(Calendar.DAY_OF_MONTH, 1);

		/*
		 * LOGON-6292 Order filtering in scheduling screen not working properly
		 * Re-added order status check as it was in LOGON v1.6.
		 * Should: deliveries with status transmitted to carrier or higher should only be displayed if the respective carrier is selected.
		 */
		List<Long> orderStatesCarrier = new ArrayList<Long>();
		List<Long> orderStatesNoCarrier = new ArrayList<Long>();

		orderStatesNoCarrier.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
		orderStatesNoCarrier.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);

		orderStatesCarrier.add(Constant.ORDER_STATUS_DISPATCHED);
		orderStatesCarrier.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);
		orderStatesCarrier.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK);
		orderStatesCarrier.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
		orderStatesCarrier.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);

		if (areOrdersWithoutAssignedCarrierIncluded) {
			if (orderNumber != null) {
				filter.addExpression(Restrictions.eq(DispoOrderListEntity.ATTR_ORDER_NUMBER,
						orderNumber));
			}

			if (carriers.isEmpty()) {
				filter.addExpression(
					Restrictions.eq(DispoOrderListEntity.ATTR_POOL_ID, poolId)
				);
				filter.addExpression(
					DbUtils.createSplittedInRestrictionOfIds(DispoOrderListEntity.ATTR_ORDER_STATUS_CD, orderStatesNoCarrier)
				);
			}
			else {
				filter.addExpression(
					Restrictions.or(
						Restrictions.and(
								createCarrierRestrictionWithHideTrucksAndOrdersOption(hideTrucksAndOrderOfPartners, carriers),
								DbUtils.createSplittedInRestrictionOfIds(DispoOrderListEntity.ATTR_ORDER_STATUS_CD, orderStatesCarrier)
						),
						Restrictions.and(
							Restrictions.eq(DispoOrderListEntity.ATTR_POOL_ID, poolId),
							DbUtils.createSplittedInRestrictionOfIds(DispoOrderListEntity.ATTR_ORDER_STATUS_CD, orderStatesNoCarrier)
						)
					)
				);
			}
		}
		else {
			if (carriers.isEmpty()) {
				filter.addExpression(
					Restrictions.eq(DispoOrderListEntity.ATTR_POOL_ID, poolId)
				);
			}
			else {
				filter.addExpression(
					Restrictions.and(
						createCarrierRestrictionWithHideTrucksAndOrdersOption(hideTrucksAndOrderOfPartners, carriers),
						Restrictions.not(Restrictions.in(DispoOrderListEntity.ATTR_ORDER_STATUS_CD, orderStatesNoCarrier))
					)
				);
			}
		}

		filter.addExpression(
				Restrictions.le(DispoOrderListEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_FINISHED)

		);
		filter.addExpression(
			Restrictions.le(DispoOrderListEntity.ATTR_DELIVERY_FROM_DAT, dateAsCalendar.getTime())
		);
		filter.addExpression(
			Restrictions.ge(DispoOrderListEntity.ATTR_DELIVERY_TO_DAT, date)
		);
		Long[] schedulableDeliveryTypes = new Long[] { Constant.DELIVERY_TYPE_ROAD_DIRECT,
					Constant.DELIVERY_TYPE_BIMODAL_ROAD, Constant.DELIVERY_TYPE_PICKUP,
					Constant.DELIVERY_TYPE_RETURN_DELIVERED };
		filter.addExpression(
			Restrictions.in(DispoOrderListEntity.ATTR_DELIVERY_TYPE_CD, schedulableDeliveryTypes)
		);
		filter.addExpression(
			Restrictions.eq(DispoOrderListEntity.ATTR_LANG_CD, locale.getLanguage())
		);

		filter.addExpression(Order.asc(DispoOrderListEntity.ATTR_DELIVERY_FROM_DAT));
		filter.addExpression(Order.asc(DispoOrderListEntity.ATTR_ORDER_NUMBER));

		List schedulableOrders = new ArrayList();

		// This needs to be in this order. GDI-JPA modifies the original
		// filter when adding authorization rules. Therefore,
		// DispoExtOrderListEntity should contain the same (authorization)
		// fields as DispoOrderListEntity. But it doesn't contain sarea_id.
		// See also ADN73985.
		if (extOrders) {
			ReadOnlyDao<DispoExtOrderListEntity> extOrderDao = daoProvider.getDispoExtOrderListEntityDao();
			schedulableOrders = extOrderDao.find(filter);
		}

		if (plantCodes != null && !plantCodes.isEmpty()) {
			filter.addExpression(DbUtils.<String> createSplittedInRestriction(
					DispoOrderListEntity.ATTR_PLANT_CD, new ArrayList<String>(plantCodes)));
		}
		schedulableOrders.addAll(dispoOrderListDao.find(filter));

		return schedulableOrders;
	}

	//LOGON-9538
	private Expression createCarrierRestrictionWithHideTrucksAndOrdersOption(boolean hideTrucksAndOrderOfPartners, List<Long> carriers){
		Expression expression = null;

		if (hideTrucksAndOrderOfPartners) {
			expression = Restrictions.or(
					Restrictions.and(DbUtils
							.createSplittedInRestrictionOfIds(
									DispoOrderListEntity.ATTR_COMM_CARRIER_ID,
									carriers), DbUtils
							.createSplittedInRestrictionOfIds(
									DispoOrderListEntity.ATTR_EXEC_CARRIER_ID,
									carriers)),
					Restrictions.and(DbUtils
							.createSplittedInRestrictionOfIds(
									DispoOrderListEntity.ATTR_COMM_CARRIER_ID,
									carriers), Restrictions.eq(
							DispoOrderListEntity.ATTR_EXEC_CARRIER_ID, null)));
		}
		else {
			expression = Restrictions
					.or(DbUtils
							.createSplittedInRestrictionOfIds(
									DispoOrderListEntity.ATTR_COMM_CARRIER_ID,
									carriers), DbUtils
							.createSplittedInRestrictionOfIds(
									DispoOrderListEntity.ATTR_EXEC_CARRIER_ID,
									carriers));
		}
		return expression;
	}

	private Expression createCarrierRestriction(List<Long> carriers) {

		Expression expression = Restrictions.or(DbUtils
				.createSplittedInRestrictionOfIds(
						DispoOrderListEntity.ATTR_COMM_CARRIER_ID, carriers),
				DbUtils.createSplittedInRestrictionOfIds(
						DispoOrderListEntity.ATTR_EXEC_CARRIER_ID, carriers));

		return expression;
	}

	/**
	 * Returns all those carriers which have at least one truck for which the dispatching carrier is in.
	 *
	 * @param selectedCarriers original carriers
	 * @param outputCarriers
	 */
	private List<Long> fetchOrderCarriersForDispatchingCarrier(List<Long> selectedCarriers) {
		ReadOnlyDao<DispatchingCarrierEntity> carrierDao = getDaoProvider().getDispatchingCarrierEntityDao();
		Filter<DispatchingCarrierEntity> filter = new FilterImpl<DispatchingCarrierEntity>();
		filter.addExpression(
			DbUtils.createSplittedInRestrictionOfIds(DispatchingCarrierEntity.ATTR_DISPATCHING_CARRIER_ID, selectedCarriers)
		);
		List<DispatchingCarrierEntity> dispatchingCarriers = carrierDao.find(filter);
		List<Long> orderCarriers = new LinkedList<Long>();

		for (DispatchingCarrierEntity carrier: dispatchingCarriers) {
			Long carrierId = carrier.getCarrierId();

			if (!orderCarriers.contains(carrierId)) {
				orderCarriers.add(carrierId);
				LOG.debug("Adding dispatching carrier to the list : " + carrierId);
			}
		}
		return orderCarriers;
	}

	/**
	 * fetch a list with all orders for a shippingpoint list and date.
	 * as the diposition is don for orders due to arrive the next day(s)
	 * the list returns orders taht need to be dispatched on the given date
	 * @param shippingpoints collection of the shippingpoint ids
	 * @return a list with all rail orders for the given date and shippingpoints
	 */
	public List<DispoOrderListEntity> fetchRailOrdersForDispo(List<Long> shippingpoints,
			Locale locale, String orderNumber) {
		if (shippingpoints.isEmpty()) {
			return new LinkedList<DispoOrderListEntity>();
		}

		ReadOnlyDao<DispoOrderListEntity> olDao = getDaoProvider().getDispoOrderListEntityDao();
		Filter<DispoOrderListEntity> f = olDao.createFilter();

		ArrayList<Long> orderStates = new ArrayList<Long>();
		orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
		orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);
		orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
		orderStates.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);

		if (!StringUtils.empty(orderNumber)) {
			StringBuilder orderNumberExprVal = new StringBuilder();
			orderNumberExprVal.append("%").append(orderNumber).append("%");
			f.addExpression(Restrictions.like(DispoOrderListEntity.ATTR_ORDER_NUMBER_IN_STR,
					orderNumberExprVal.toString()));
		}

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoOrderListEntity.ATTR_SHIPPINGPOINT_ID, shippingpoints));

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoOrderListEntity.ATTR_ORDER_STATUS_CD, orderStates));
		f.addExpression(Restrictions.or(Restrictions.eq(DispoOrderListEntity.ATTR_DELIVERY_TYPE_CD,
				Constant.DELIVERY_TYPE_RAIL_DIRECT), Restrictions.eq(
				DispoOrderListEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_BIMODAL_RAIL)));
		f.addExpression(Restrictions.eq(DispoOrderListEntity.ATTR_LANG_CD, locale.getLanguage()));

		f.addExpression(Order.asc(DispoOrderListEntity.ATTR_SORT_FROM_DAT));
		f.addExpression(Order.asc(DispoOrderListEntity.ATTR_SORT_TO_DAT));
		f.addExpression(Order.asc(DispoOrderListEntity.ATTR_DEP_SWITCHING_TIME_MIN));
		List<DispoOrderListEntity> rawList = olDao.find(f);

		//List can contain several entries for the same order, if there are several timetable
		//entries. filter the right one

		Map<Long, DispoOrderListEntity> resultMap = new HashMap<Long, DispoOrderListEntity>();
		for (DispoOrderListEntity dol : rawList) {
			DispoOrderListEntity alreadyInMap = resultMap.get(dol.getOrderId());
			if ((alreadyInMap == null)
					|| (alreadyInMap.getDepSwitchingTimeMin() == null)
					|| (alreadyInMap.getDepSwitchingTimeMin().longValue() < dol.getDepSwitchingTimeMin().longValue() && alreadyInMap.getArrOffsetDays().longValue() >= dol.getArrOffsetDays().longValue())) {
				resultMap.put(dol.getOrderId(), dol);
			}
		}

		return new ArrayList<DispoOrderListEntity>(resultMap.values());
	}

	/**
	 * fetch allowed wagon types for the list of given sipti_sap_bp_ids and language
	 * result is ordered by shipto_id  and wagon type priority.
	 */
	public List<DispoWagonTypeListEntity> fetchWagonTypesForDispo(List<Long> shiptos,
			Locale locale) {
		if (shiptos.isEmpty()) {
			return new LinkedList<DispoWagonTypeListEntity>();
		}
		ReadOnlyDao<DispoWagonTypeListEntity> dispoWagonTypeListDao = getDaoProvider().getDispoWagonTypeListEntityDao();
		Filter<DispoWagonTypeListEntity> f = dispoWagonTypeListDao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoWagonTypeListEntity.ATTR_SHIPTO_SAP_BP_ID, shiptos));
		f.addExpression(Restrictions.eq(DispoWagonTypeListEntity.ATTR_LANG_CD, locale.getLanguage()));
		f.addExpression(Order.asc(DispoWagonTypeListEntity.ATTR_SHIPTO_SAP_BP_ID));
		f.addExpression(Order.asc(DispoWagonTypeListEntity.ATTR_EVALUATION_PRIORITY));
		return dispoWagonTypeListDao.find(f);
	}

	/**
	 * Fetch allowed truck types for the given order identifier list and the language.
	 * The result is ordered by truck type priority.
	 *
	 * @param orderIdList the list of order identifiers to filter the truck types for.
	 * 		   If not given, no truck types are loaded.
	 * @param locale - Locale instance, from which the language could be identified.
	 */
	public List<DispoTruckTypeListEntity> fetchTruckTypesForDispo(List orderIdList, Locale locale) {
		if (orderIdList == null || orderIdList.isEmpty()) {
			return new LinkedList<DispoTruckTypeListEntity>();
		}
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(DispoTruckTypeListEntity.ATTR_LANG_CD, locale.getLanguage()));
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoTruckTypeListEntity.ATTR_ORDER_ID, orderIdList));
		f.addExpression(Order.asc(DispoTruckTypeListEntity.ATTR_EVALUATION_PRIORITY));
		return getDaoProvider().getDispoTruckTypeListEntityDao().find(f);
	}

	/**
	 * Fetches order items for given order id list
	 */
	public List<DispoOrderItemListEntity> fetchOrderItemsForDispo(List<Long> orders, String langCd) {
		if (orders.isEmpty()) {
			return new LinkedList<DispoOrderItemListEntity>();
		}
		ReadOnlyDao<DispoOrderItemListEntity> doilDao = getDaoProvider().getDispoOrderItemListEntityDao();
		Filter<DispoOrderItemListEntity> f = doilDao.createFilter();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoOrderItemListEntity.ATTR_ORDER_ID, orders));
		f.addExpression(Restrictions.eq(DispoOrderItemListEntity.ATTR_MATERIAL_LANG_CD, langCd));

		return doilDao.find(f);
	}

	public boolean checkAssignmentReroutable(Long assignmentId) {
		final AssignmentEntity assignment =
				getDaoProvider().getAssignmentEntityDao().findById(assignmentId);

		if (assignment == null) {
			throw new LogonException("no such assignment with id " + assignmentId, Reason.RES_ERROR_IMPL);
		}

		return checkAssignmentReroutable(assignment);
	}

	/**
	 * Checks the assignment with the given id, if the conditions of reroutable state
	 * are fulfilled or not: pda not available in the truck belonging to the assignment,
	 * every linked order is in delivered state, delivery type is 'road direct'.
	 * Quantities are not yet checked here.
	 * @param assignmentId
	 * @return reroutable
	 */
	public boolean checkAssignmentReroutable(AssignmentEntity assignment) {
		final Long assignmentId = assignment.getAssignmentId();

		if (assignment.getTruck() == null) {
			throw new LogonException("there is no truck to the assignment " + assignmentId,
					Reason.RES_ERROR_IMPL);
		}
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		if (orders.isEmpty()) {
			throw new LogonException("there is no order belonging to the assignment: "
					+ assignmentId, Reason.RES_ERROR_IMPL);
		}

		if (assignment.getTruck().getPdaAvailableFlag()) {
			LOG.info("the truck has a pda, so it's not reroutable for assignment: " + assignmentId);
			return false;
		}

		//order must be in delivered status and must have road direct type, in order to be reroutable
		for (OrderEntity order : orders) {
			if (!ReroutingAssignmentUtil.isReroutableOrder(order)) {
				return false;
			}
		}

		return true;
	}


	/**
	 * Fetches the orders, which can be used for rerouting the assignment, i.e. which are
	 * matching with the orders in the assignment.
	 */
	public List<ReroutingOrderWrapper> fetchAlternativeOrdersForRerouting(Long assignmentId,
																		  ArrayList<Long> filteredOrders, String languageCd){
		//original order(s), which will/can be replaced - more than 1 in case of tour
		final AssignmentEntity assignment = fetchAssignmentForEdit(assignmentId);
		final List<OrderEntity> ordersOfAssignment = AssignmentUtils.getOrdersFromAssignment(assignment);

		//unassigned orders of scheduling screen (filtered by day, pool etc)
		Set<OrderEntity> filterOrders = new HashSet<OrderEntity>(filteredOrders.size());
		for (Long orderId : filteredOrders) {
			OrderEntity order = orderService.findOrderById(orderId);
			filterOrders.add(order);
		}

		//orders, which can replace original order based on the structure (orderitems - materials)
		Set<OrderEntity> matchingOrders = ReroutingAssignmentUtil.matchingOrdersWithAssignment(
				ordersOfAssignment, filterOrders, false);

		Locale locale = new Locale(languageCd);
		UomTxEntity uomTx;
		List<ReroutingOrderWrapper> result = new ArrayList<ReroutingOrderWrapper>();
		for (OrderEntity matchingOrder : matchingOrders) {
			boolean isFirstItem = true;
			List<OrderItemEntity> matchingOrderItems = matchingOrder.getOrderItems();
			for (OrderItemEntity matchingOrderItem : matchingOrderItems) {

				// We should not consider sevice material order item with zero ordere quantity.
				if (ReroutingAssignmentUtil.isOrderItemServiceWithZeroQty(matchingOrderItem)) {
					continue;
				}

				Long sapMaterialId = matchingOrderItem.getSapMaterial().getSapMaterialId();
				SapMaterialDescriptionEntity loadMaterialDesc = orderDetailService.loadMaterialDesc(
						sapMaterialId, languageCd);

				uomTx = referentialDataService.findTxEntity(UomTxEntity.class, locale,
						matchingOrderItem.getOrderedUom().getUomCd());
				ReroutingOrderWrapper reroutingOrderWrapper = new ReroutingOrderWrapper(
						isFirstItem,//first item in the order
						matchingOrder.getOrderId(), matchingOrder.getOrderNumber(),
						matchingOrder.getShiptoSapBp().getName1(),//ship-to name
						matchingOrderItem.getOrderItemId(), loadMaterialDesc.getTxt(),//material name
						matchingOrderItem.getOrderedQuantity(),
						(uomTx == null ? matchingOrderItem.getOrderedUom().getUomCd()
								: uomTx.getTxt()));
				isFirstItem = false;
				result.add(reroutingOrderWrapper);
			}
		}
		return result;
	}


	/**
	 * Reroutes the assignment if proper orders are received, otherwise errorcode is returned.
	 * Rerouting: the loads belonging to the assignment will be linked to the given orders.
	 * The original order of the assignment will be placed back to
	 * ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK status. A new assignment will be created, and the
	 * order(s) will be linked to that assignment. The load of the input assignment will be linked
	 * to the new assignment.
	 */
	public int rerouteAssignment(Long oldAssignmentId, Long[] newOrderIds) {
		//FIXME orsika 18032013 if rerouteAssignment is called from DispoAjaxService, and a LogonException
		//is thrown here, ui doesn't indicate the error. It should print out the code of the exception
		//(logged) in a red popup
		//			if (true) {
		//				throw new LogonException("assignment is not reroutable", Reason.RES_ERROR_UNSPECIFIED);//tmp
		//			}

		final List<OrderEntity> newOrders = orderService.findOrdersByIds(newOrderIds);

		ConfSeqLock lock = null;
		int success = Constant.REROUTING_UNSUCCESSFUL_UNKNOWN;
		try {
			lock = sequenceService.acquireLockForOrders(newOrders);
			success = dispoService._rerouteAssignment(oldAssignmentId, newOrderIds, lock);
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
		final List<OrderEntity> updatedOrders = orderService.findOrdersByIds(newOrderIds);
		for (OrderEntity order : updatedOrders) {
			if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
				orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
			}
		}

		return success;
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int _rerouteAssignment(Long oldAssignmentId, Long[] newOrderIds, ConfSeqLock lock) {
		final ReadWriteDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
		final AssignmentEntity oldAssignment = dao.findById(oldAssignmentId);

		// validation, preparation of assignment data
		if (!checkAssignmentReroutable(oldAssignment)) {
			return Constant.REROUTING_UNSUCCESSFUL_UNKNOWN;
		}
		if (oldAssignment == null) {
			throw new LogonException("no such assignment with id " + oldAssignmentId,
					Reason.RES_ERROR_IMPL);
		}

		final AssignmentPartEntity firstLoadAssignmentPart = AssignmentUtils.getFirstLoadAssignmentPart(oldAssignment);
		if (firstLoadAssignmentPart == null) {
			throw new LogonException("assignmentPart (loading type) doesn't exist for assignment: "
					+ oldAssignmentId, Reason.RES_ERROR_IMPL);
		}

		final Date startTime = firstLoadAssignmentPart.getPlannedStart();
		final boolean isTour = AssignmentUtils.isTour(oldAssignment);
		final Long truckId = oldAssignment.getTruck().getTruckId();

		UserEntity user = userService.fetchUserEntity(firstLoadAssignmentPart.getCtlCreUid());
		TimeZone userTimeZone = timeZoneService.loadForUser(user);

		// preparation of order data (old and new)
		final List<OrderEntity> oldOrders = AssignmentUtils.getOrdersFromAssignment(oldAssignment);
		final List<OrderEntity> newOrders = orderService.findOrdersByIds(newOrderIds);

		// validation of order data (old and new)
		final int isValid = ReroutingAssignmentUtil.validateOldAndNewOrders(oldOrders, newOrders,
				isTour);
		if (isValid != Constant.REROUTING_SUCCESSFUL) {
			return isValid;
		}

		// calculation and validation of mapping
		final Map<OrderEntity, OrderEntity> orderMapping = ReroutingAssignmentUtil.matchingOrderCouples(
				oldOrders, newOrders, isTour);

		final int isMatching = ReroutingAssignmentUtil.matchingOrders(orderMapping,
				oldOrders.size(), isTour);
		if (isMatching != Constant.REROUTING_ORDERITEMS_MATCHING) {
			return isMatching;
		}

		final List<OrderItemLoadTransferContainer> allOrderItemLoadTransfers = ReroutingAssignmentUtil.getOrderItemLoadTransfers(
				orderMapping, isTour);

		rerouteAssignment(oldAssignment, truckId, startTime, userTimeZone, orderMapping,
				allOrderItemLoadTransfers, lock);

		return Constant.REROUTING_SUCCESSFUL;
	}

	private void rerouteAssignment(AssignmentEntity oldAssignment, Long truckId, Date startTime, TimeZone timeZone,
		Map<OrderEntity, OrderEntity> orderMapping, List<OrderItemLoadTransferContainer> allOrderItemLoadTransfers, ConfSeqLock lock) {

		Map<Long, List<GoodsMovementEntity>> orderItemToGoodsMovementAssignments =
				getOrderitemToGoodsMovementAssignments(orderMapping);

		for (Map.Entry<OrderEntity, OrderEntity> entry : orderMapping.entrySet()) {
			OrderEntity oldOrder = entry.getKey();
			OrderEntity newOrder = entry.getValue();
			newOrder.setLdsDeliveryNoteNo(oldOrder.getLdsDeliveryNoteNo());
		}

		LOG.debug("0. put every order of assignment back to shipping state from delivered");
		orderService.putOrdersToShippingState(oldAssignment);

		LOG.debug("1. cancel assignment, order is back to ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK state (before scheduled)");
		cancelAssignment(oldAssignment.getAssignmentId(), null, null, false);//for all orders

		LOG.debug("2. create (separate) assignment(s), order gets into state ORDER_STATUS_DISPATCHED");
		List<AssignmentEntity> newAssignments = ReroutingAssignmentUtil.createAssignment(orderService, orderMapping, truckId, startTime, timeZone);

		LOG.debug("3. merge assignment in case of tour - does nothing for single order assignments");
		AssignmentEntity newAssignment = ReroutingAssignmentUtil.mergeAssignments(orderService, newAssignments, oldAssignment);

		LOG.debug("4. release assignment, order gets into state ORDER_STATUS_TRANSMITTED_TO_DRIVER");
		transmitToDriver(newAssignment.getAssignmentId(), Constant.WEIGH_SYSTEM_SP_NONE, Constant.WEIGH_SYSTEM_LP_LOGON);

		LOG.debug("5. load order, order gets into state ORDER_STATUS_DELIVERED (as non-pda case)");

		transferRerouteLoad(allOrderItemLoadTransfers, lock);

		LOG.debug("6. make goods movements point to the new loads");
		final boolean isTour = AssignmentUtils.isTour(oldAssignment);
		makeGoodsMovementsPointToTheNewLoads(orderMapping, orderItemToGoodsMovementAssignments,
				isTour);
	}


	/**
	 * Transfer the old loading data belonging to the rerouted assignment to the
	 * assignment we are rerouting to. The transfer is done on the orderitems and their loads.
	 * The old load does not get reused but instead the new orderitems are loaded with the old data.
	 * @param allOrderItemLoadTransfers
	 * @param lock
	 */
	private void transferRerouteLoad(List<OrderItemLoadTransferContainer> allOrderItemLoadTransfers, ConfSeqLock lock) {
		final Map<LoadEntity, List<OrderItemLoadTransferContainer>> orderItemLoadTransfersGroupedByLoad =
				new HashMap<LoadEntity, List<OrderItemLoadTransferContainer>>();

		for (OrderItemLoadTransferContainer orderItemLoadTransfer : allOrderItemLoadTransfers) {
			OrderItemEntity newOrderItem = orderItemLoadTransfer.getTargetOrderItem();
			LoadEntity newLoad = newOrderItem.getLoad();

			if (newLoad != null) {
				List<OrderItemLoadTransferContainer> groupedOrderItemLoadTransfers =
						orderItemLoadTransfersGroupedByLoad.get(newLoad);

				if (groupedOrderItemLoadTransfers == null) {
					groupedOrderItemLoadTransfers = new ArrayList<OrderItemLoadTransferContainer>();
					orderItemLoadTransfersGroupedByLoad.put(newLoad, groupedOrderItemLoadTransfers);
				}

				groupedOrderItemLoadTransfers.add(orderItemLoadTransfer);
			}
		}

		for (LoadEntity newLoad : orderItemLoadTransfersGroupedByLoad.keySet()) {
			List<OrderItemLoadTransferContainer> groupedOrderItemLoadTransfers = orderItemLoadTransfersGroupedByLoad.get(newLoad);
			Map<Long, LoadedQuantityData> loadedQuantityDataMap = ReroutingAssignmentUtil.
					getLoadedQuantityDataMap(groupedOrderItemLoadTransfers);

			LoadTransferContainer firstLoadTransferContainer = groupedOrderItemLoadTransfers.get(0).getLoadTransferSource();
			LoadEntity oldLoad = firstLoadTransferContainer.getOldLoad();
			LoadingpointEntity oldLoadingpointOfLoad = oldLoad.getLoadingpoint();
			BigDecimal oldLoadTareWeightT = oldLoad.getTareWeightT();

			loadingService.updateSharedLoad(newLoad, oldLoadingpointOfLoad, loadedQuantityDataMap,
					oldLoadTareWeightT, NO_GROSS_WEIGHT, oldLoad.getLoadingTs(),
					oldLoad.getLoadingTs(), DO_NOT_SEND_GOODS_MOVEMENTS,
					EventSource.LOGON, lock);
		}
	}

	/**
	 * The goods movements are assigned to the order items through their load, which will
	 * removed during the reroute process, therefore we need to store the associations temporary
	 *
	 * @param orderMapping
	 * @return
	 */
	private Map<Long, List<GoodsMovementEntity>> getOrderitemToGoodsMovementAssignments(
			Map<OrderEntity, OrderEntity> orderMapping) {
		Set<Entry<OrderEntity, OrderEntity>> orderPairs = orderMapping.entrySet();
		Map<Long, List<GoodsMovementEntity>> orderItemToGoodsMovementAssignments =
				new HashMap<Long, List<GoodsMovementEntity>>();

		for (Map.Entry<OrderEntity, OrderEntity> orderPair : orderPairs) {
			OrderEntity oldOrder = orderPair.getKey();
			List<OrderItemEntity> oldOrderItems = oldOrder.getOrderItems();
			for (OrderItemEntity oldOrderItem : oldOrderItems) {
				LoadEntity oldLoad = oldOrderItem.getLoad();
				if (oldLoad == null) {
					continue;
				}
				Long oldLoadId = oldLoad.getLoadId();
				List<GoodsMovementEntity> allGoodsMovementsForTheOldLoad = goodsMovementService.getAllGoodsMovementsForLoad(oldLoadId);
				if (allGoodsMovementsForTheOldLoad == null) {
					continue;
				}
				Long orderItemId = oldOrderItem.getOrderItemId();
				orderItemToGoodsMovementAssignments.put(orderItemId, allGoodsMovementsForTheOldLoad);
			}
		}
		return orderItemToGoodsMovementAssignments;
	}

	/**
	 * The replacement order will not generate new goods movement during reroute,
	 * however, we need to update the pointer of the rerouted order's goods movements
	 * so that is will point to the replacements order's orderitem's loads
	 *
	 * @param orderMapping
	 * @param orderItemToGoodsMovementAssignments
	 * @param isTour
	 */
	private void makeGoodsMovementsPointToTheNewLoads(Map<OrderEntity, OrderEntity> orderMapping,
			Map<Long, List<GoodsMovementEntity>> orderItemToGoodsMovementAssignments, boolean isTour) {
		Set<Entry<OrderEntity, OrderEntity>> orderPairs = orderMapping.entrySet();
		for (Map.Entry<OrderEntity, OrderEntity> orderPair : orderPairs) {
			OrderEntity oldOrder = orderPair.getKey();
			OrderEntity newOrder = orderPair.getValue();
			List<OrderItemEntity> oldOrderItems = oldOrder.getOrderItems();
			List<OrderItemEntity> newOrderItems = newOrder.getOrderItems();

			Map<OrderItemEntity, OrderItemEntity> matchingOrderItemCoupleMap =
					ReroutingAssignmentUtil.getMatchingOrderItemCouples(
							oldOrderItems, newOrderItems, isTour);

			Set<Entry<OrderItemEntity, OrderItemEntity>> matchingOrderItemCouples =
					matchingOrderItemCoupleMap.entrySet();
			for (Map.Entry<OrderItemEntity, OrderItemEntity> orderItemCouple
						: matchingOrderItemCouples) {
				OrderItemEntity oldOrderItem = orderItemCouple.getKey();
				OrderItemEntity newOrderItem = orderItemCouple.getValue();
				LoadEntity newLoad = newOrderItem.getLoad();
				if (newLoad == null) {
					continue;
				}
				Long orderItemId = oldOrderItem.getOrderItemId();
				List<GoodsMovementEntity> allGoodsMovementsForTheOldLoad =
						orderItemToGoodsMovementAssignments.get(orderItemId);
				if (allGoodsMovementsForTheOldLoad == null) {
					continue;
				}
				for (GoodsMovementEntity goodsMovement : allGoodsMovementsForTheOldLoad) {
					goodsMovement.setLoad(newLoad);
				}
			}
		}
	}

	/**
	 * Returns a list containing all revenue information for the orders provided.
	 * Does not depend on the order status, even before the prescheduler released the order
	 * this list will have information about the revenue.
	 */
	public List fetchRevenuePlanningForOrders(List orderIds) {
		if (orderIds.isEmpty()) {
			return new ArrayList();
		}
		ReadOnlyDao<DispoPlanningEntity> doRevDao = getDaoProvider().getDispoPlanningEntityDao();
		Filter f = new FilterImpl();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(DispoPlanningEntity.ATTR_ORDER_ID,
				orderIds));

		return doRevDao.find(f);
	}

	/**
	 * fetch a single order with all language-dependent Strings
	 * @param orderNumber Order Number
	 * @param langCd user language
	 * @return  DispoOrderListEntity with the given Order Number
	 */
	public DispoOrderListEntity fetchOrderForDispo(Long orderNumber, String langCd) {
		ReadOnlyDao<DispoOrderListEntity> olDao = getDaoProvider().getDispoOrderListEntityDao();
		Filter f = new FilterImpl();

		f.addExpression(Restrictions.eq(DispoOrderListEntity.ATTR_ORDER_NUMBER, orderNumber));
		f.addExpression(Restrictions.eq(DispoOrderListEntity.ATTR_LANG_CD, langCd));
		return olDao.findSingle(f);
	}

	private Connection getConnection() {
		try {
			DataSource logonDS;
			logonDS = (DataSource)new InitialContext().lookup("java:jboss/datasources/logonDs");

			return logonDS.getConnection();
		}
		catch(NamingException e) {
			LOG.error("Exception occurred during data source lookup.", e);
			throw new LogonException(e);
		}
		catch (SQLException e) {
			LOG.error("Exception occurred during establishing the connection.", e);
			throw new LogonException(e);
		}
		catch (Exception e) {
			LOG.error("Unexpectd exception occurred.", e);
			throw new LogonException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Long> fetchTruckChangesForDispo(List<Long> filterTruckIds, Date date, Date lastAccessTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		List<Long> idList = new ArrayList<Long>();

		ConfigBean configuration = configService.getConfigBean();
		String owner = configuration.getWsserverLogonSchemaOwner();
		owner = (owner == null) ? null : owner.toUpperCase();

		String sql = "{call " + owner + ".FETCH_TRUCK_CHANGES_FOR_DISPO(?,?,?,?,?)}";

		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.prepareCall(sql);
			OracleConnection oracleConnection = connection.unwrap(OracleConnection.class);

			ArrayDescriptor arrayDesc = ArrayDescriptor.createDescriptor(owner + ".FILTER_TRUCK_IDS", oracleConnection);
			oracle.sql.ARRAY array = new oracle.sql.ARRAY(arrayDesc, oracleConnection, filterTruckIds.toArray());
			statement.setArray(1, array);
			statement.setDate(2, new java.sql.Date(date.getTime()));
			statement.setDate(3, new java.sql.Date(nextDay.getTime()));
			statement.setTimestamp(4, new Timestamp(lastAccessTime.getTime()));
			statement.registerOutParameter(5, OracleTypes.CURSOR);
			statement.execute();
			rs = (ResultSet) statement.getObject(5);
			while(rs.next()) {
				idList.add(rs.getLong(1));
			}
			return idList;
		}
		catch (SQLException sqlException) {
			LOG.error("Failed to execute: '" + sql + "'");
			throw new LogonException(sqlException);
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				}
				catch (SQLException sqlException) {
					LOG.error("Error closing rs", sqlException);
				}
			}
			if (statement != null) {
				try {
					statement.close();
					statement = null;
				}
				catch (SQLException sqlException) {
					LOG.error("Error closing createdStatement", sqlException);
				}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				}
				catch (SQLException sqlException) {
					LOG.error("Error closing connection", sqlException);
				}
			}
		}
	}


	/**
	 * Fetch display data for the given list of filtered trucks.
	 *
	 * @param language user language
	 * @param poolId The id of the selected pool.
	 * @param carriers for further filtering on carriers (see below)
	 * @param filterForTruck One of Constant.FILTER_FOR_TRUCK_ALL, Constant.FILTER_FOR_TRUCK_REAL_ONLY, FILTER_FOR_TRUCK_PLANNING_ONLY.
	 * @param hideInactiveTrucks Excludes those trucks which have priority set to Constant.TRUCK_PRIORITY_TRUCK_INACTIVE.
	 * @param filterTruckIds If not empty only fetch the trucks with the given truck IDs.
	 *
	 * @return display data for trucks
	 */
	public List<DispoTruckListV2Entity> fetchTrucksForDispo(String language, Long poolId, List<Long> carriers,
			int filterForTruck, boolean hideInactiveTrucks, List<Long> filterTruckIds, Long truckPriority) {
		if (carriers.isEmpty()){
			return new LinkedList<DispoTruckListV2Entity>();
		}
		List<Long> orderCarriers = fetchOrderCarriersForDispatchingCarrier(carriers);
		ReadOnlyDao<DispoTruckListV2Entity> olDao = getDaoProvider().getDispoTruckListV2EntityDao();
		Filter<DispoTruckListV2Entity> dispoTruckListFilter = new FilterImpl<DispoTruckListV2Entity>();

		if (filterTruckIds != null && !filterTruckIds.isEmpty()) {
			dispoTruckListFilter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					DispoTruckListV2Entity.ATTR_TRUCK_ID, filterTruckIds));
		}

		dispoTruckListFilter.addExpression(Restrictions.eq(DispoTruckListV2Entity.ATTR_LANG_CD,
				language));
		dispoTruckListFilter.addExpression(Restrictions.eq(DispoTruckListV2Entity.ATTR_POOL_ID,
				poolId));
		Expression dispatchingCarrierExpression = !orderCarriers.isEmpty() ? (
			Restrictions.and(
				DbUtils.createSplittedInRestrictionOfIds(DispoTruckListV2Entity.ATTR_CARRIER_ID, orderCarriers),
				DbUtils.createSplittedInRestrictionOfIds(DispoTruckListV2Entity.ATTR_DISPATCHING_CARRIER_ID, carriers)
			)
		) : Restrictions.falseExpression();

		dispoTruckListFilter.addExpression(Restrictions.or(
				DbUtils.createSplittedInRestrictionOfIds(DispoTruckListV2Entity.ATTR_CARRIER_ID,
						carriers),
				dispatchingCarrierExpression
		));

		// Filter by Truck (all/real/planning)
		switch (filterForTruck) {
			case Constant.FILTER_FOR_TRUCK_REAL_ONLY:
				//what is not planning is real
				dispoTruckListFilter.addExpression(Restrictions.neq(DispoTruckListV2Entity.ATTR_SAP_VEHICLE_CHAR_CD,
					Constant.VEHICLE_CHARACTERISTIC_PLANNING));
				break;
			case Constant.FILTER_FOR_TRUCK_PLANNING_ONLY:
				dispoTruckListFilter.addExpression(Restrictions.eq(DispoTruckListV2Entity.ATTR_SAP_VEHICLE_CHAR_CD,
					Constant.VEHICLE_CHARACTERISTIC_PLANNING));
				break;
			default:
				break;
		}

		if (truckPriority != null) {
			dispoTruckListFilter.addExpression(Restrictions.le(DispoTruckListV2Entity.ATTR_PRIORITY, truckPriority));
		}

		if (hideInactiveTrucks) {
			dispoTruckListFilter.addExpression(Restrictions.neq(DispoTruckListV2Entity.ATTR_PRIORITY,
					Constant.TRUCK_PRIORITY_TRUCK_INACTIVE));
		}

		addOrderByToTruckFilter(dispoTruckListFilter);
		return olDao.find(dispoTruckListFilter);
	}

	/**
	 * Fetches currently active loads for the given truck list
	 */
	public List<DispoTruckLoadListV2Entity> fetchTruckLoads(List<Long> trucks, String langCd) {
		if (trucks.isEmpty()) {
			return Collections.emptyList();
		}
		ReadOnlyDao<DispoTruckLoadListV2Entity> tlDao = getDaoProvider().getDispoTruckLoadListV2EntityDao();

		Filter<DispoTruckLoadListV2Entity> f = new FilterImpl<DispoTruckLoadListV2Entity>();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(DispoTruckLoadListV2Entity.ATTR_TRUCK_ID, trucks));
		f.addExpression(Restrictions.eq(DispoTruckLoadListV2Entity.ATTR_LANG_CD, langCd));

		return tlDao.find(f);
	}

	/**
	 * Fetches unavailability records for the given list of trucks in the given
	 * day
	 */
	public List<TruckUnavailabilityEntity> fetchTrucksUnavailability(List<Long> trucks, Date date) {
		if (trucks.isEmpty()) {
			return Collections.emptyList();
		}
		ReadWriteDao<TruckUnavailabilityEntity> tuDao = getDaoProvider().getTruckUnavailabilityEntityDao();

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		Filter<TruckUnavailabilityEntity> f = new FilterImpl<TruckUnavailabilityEntity>();

		f.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				TruckUnavailabilityEntity.ATTR_TRUCK_ID, trucks));
		f.addExpression(Restrictions.lt(TruckUnavailabilityEntity.ATTR_START_DAT, nextDay));
		f.addExpression(Restrictions.ge(TruckUnavailabilityEntity.ATTR_END_DAT, date));

		return tuDao.find(f);
	}

	/**
	 * Fetches the list of DispoHomePlantEntities for the given
	 * shippingpointids.
	 */
	public List<DispoHomePlantEntity> fetchDispoHomePlants(List<Long> shippingpointIds) {
		if ((shippingpointIds == null) || (shippingpointIds.isEmpty())) {
			return Collections.emptyList();
		}

		ReadOnlyDao<DispoHomePlantEntity> homePlantDao = getDaoProvider().getDispoHomePlantEntityDao();
		Filter<DispoHomePlantEntity> filter = homePlantDao.createFilter();

		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoHomePlantEntity.ATTR_SHIPPINGPOINT_ID, shippingpointIds));

		return homePlantDao.find(filter);
	}

	/**
	 * Fetches dispo_message_list records for given driver list and date
	 * Records are ordered by driver and time
	 *
	 */
	public List<DispoMessageListEntity> fetchDriverMessages(List<Long> drivers, Date date){
		if (drivers.isEmpty()) {
			return Collections.emptyList();
		}

		ReadOnlyDao<DispoMessageListEntity> dbDao = getDaoProvider().getDispoMessageListEntityDao();

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		Filter<DispoMessageListEntity> f = new FilterImpl<DispoMessageListEntity>();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoMessageListEntity.ATTR_DRIVER_ID, drivers));
		f.addExpression(Restrictions.lt(DispoMessageListEntity.ATTR_CRE_TS, nextDay));
		f.addExpression(Restrictions.ge(DispoMessageListEntity.ATTR_CRE_TS, date));

		f.addExpression(Order.asc(DispoMessageListEntity.ATTR_DRIVER_ID));
		f.addExpression(Order.desc(DispoMessageListEntity.ATTR_CRE_TS));

		return dbDao.find(f);
	}

	/**
	 * Fetches dispo_message_list records for given driver and date
	 * Records are ordered by driver and time
	 *
	 */
	public List fetchDriverMessages(int count, int skip, Long driverId, Date date){
		ReadOnlyDao<DispoMessageListEntity> dbDao = getDaoProvider().getDispoMessageListEntityDao();

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		Filter f = new FilterImpl();

		f.addExpression(Restrictions.eq(DispoMessageListEntity.ATTR_DRIVER_ID, driverId));
		f.addExpression(Restrictions.lt(DispoMessageListEntity.ATTR_CRE_TS, nextDay));
		f.addExpression(Restrictions.ge(DispoMessageListEntity.ATTR_CRE_TS, date));
		f.addExpression(Restrictions.neq(DispoMessageListEntity.ATTR_PDA_MESSAGE_TYPE_CD,
				Constant.PDA_MESSAGE_TYPE_MARKET_INFO));

		f.addExpression(Order.asc(DispoMessageListEntity.ATTR_DRIVER_ID));
		f.addExpression(Order.desc(DispoMessageListEntity.ATTR_CRE_TS));

		return dbDao.findNRecs(skip, count, f);
	}

	/**
	 * counts dispo_message_list records for given driver and date
	 * Records are ordered by driver and time
	 *
	 */
	public long countDriverMessages(Long driverId, Date date) {
		ReadOnlyDao<DispoMessageListEntity> dbDao = getDaoProvider().getDispoMessageListEntityDao();

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		Filter f = new FilterImpl();

		f.addExpression(Restrictions.eq(DispoMessageListEntity.ATTR_DRIVER_ID, driverId));
		f.addExpression(Restrictions.lt(DispoMessageListEntity.ATTR_CRE_TS, nextDay));
		f.addExpression(Restrictions.ge(DispoMessageListEntity.ATTR_CRE_TS, date));
		f.addExpression(Restrictions.neq(DispoMessageListEntity.ATTR_PDA_MESSAGE_TYPE_CD,
				Constant.PDA_MESSAGE_TYPE_MARKET_INFO));

		return dbDao.count(f);

	}




	/**
	 * Sends passed tex as message to driver with given id
	 *
	 */
	public void sendMessageToDriver(long driverId, String message){
		DriverEntity driver = getDaoProvider().getDriverEntityDao().findById(driverId);

		PdaMessageEntity pme = new PdaMessageEntity();
		pme.setDispatcherUser(getDaoProvider().getUserEntityDao().findById(getUserId()));
		pme.setMessage(message);
		PdaMessageTypeBsEntity mte = new PdaMessageTypeBsEntity();
		mte.setPdaMessageTypeCd(Constant.PDA_MESSAGE_TYPE_TO_DRIVER);
		pme.setPdaMessageType(mte);
		pme.setPdaUser(driver.getUser());
		pme.setReceiverAckFlag(Boolean.FALSE);

		pme = getDaoProvider().getPdaMessageEntityDao().persist(pme);

		mopEventService.sendMessageToPDA(driver, pme);

	}

	/**
	 * Sets message with given Id to acknowledged (by dispatcher).
	 * Does nothing, if message type doesn't allow for this operation
	 * @param msgId
	 */
	public void  setDriverMessageRead(Long msgId) {
		PdaMessageEntity msg = getDaoProvider().getPdaMessageEntityDao().findById(msgId);

		if (msg == null) {
			return;
		}

		long type = msg.getPdaMessageType().getPdaMessageTypeCd().longValue();
		if ((type == Constant.PDA_MESSAGE_TYPE_TO_DRIVER)
				|| (type == Constant.PDA_MESSAGE_TYPE_MARKET_INFO)) {
			return;
		}

		msg.setDispatcherUser(getDaoProvider().getUserEntityDao().findById(getUserId()));
		msg.setReceiverAckFlag(Boolean.TRUE);

	}

	/**
	 * Fetches DispoDriverBreakList records for the given driver list
	 * and the given day.
	 * Records are ordered by driver and break start time.
	 *
	 */
	public List<DispoDriverBreakListEntity> fetchDriverBreaks(List<Long> driverIds, Date date, boolean includeMop) {
		if (driverIds.isEmpty()) {
			return Collections.emptyList();
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		Filter<DispoDriverBreakListEntity> filter = new FilterImpl<DispoDriverBreakListEntity>();

		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(DispoDriverBreakListEntity.ATTR_DRIVER_ID, driverIds));
		filter.addExpression(Restrictions.lt(DispoDriverBreakListEntity.ATTR_START_DAT, nextDay));
		filter.addExpression(Restrictions.ge(DispoDriverBreakListEntity.ATTR_START_DAT, date));
		if (!includeMop) {
			filter.addExpression(Restrictions.eq(DispoDriverBreakListEntity.ATTR_IS_MOP_BREAK, Boolean.FALSE));
		}

		filter.addExpression(Order.asc(DispoDriverBreakListEntity.ATTR_DRIVER_ID));
		filter.addExpression(Order.asc(DispoDriverBreakListEntity.ATTR_START_DAT));

		ReadOnlyDao<DispoDriverBreakListEntity> dispoDriverBreakListDao = getDaoProvider().getDispoDriverBreakListEntityDao();
		return dispoDriverBreakListDao.find(filter);
	}

	/**
	 * same as fetchDriverBreaks, but returns a list if DriverBreak enitities
	 * @param drivers
	 * @param date
	 * @param includeMop
	 * @return
	 */
	public List fetchDriverBreaksForEdit(List drivers, Date date, boolean includeMop){
		if (drivers.isEmpty()) {
			return new LinkedList();
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		Filter f = new FilterImpl();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(DriverBreakEntity.ATTR_DRIVER_ID,
				drivers));
		f.addExpression(Restrictions.lt(DriverBreakEntity.ATTR_START_DAT, nextDay));
		f.addExpression(Restrictions.ge(DriverBreakEntity.ATTR_START_DAT, date));
		if (!includeMop) {
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_IS_MOP_BREAK, Boolean.FALSE));
		}
		f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));

		f.addExpression(Order.asc(DriverBreakEntity.ATTR_DRIVER_ID));
		f.addExpression(Order.asc(DriverBreakEntity.ATTR_START_DAT));

		return getDaoProvider().getDriverBreakEntityDao().find(f);

	}

	/**
	 * Fetches traffic_jam records for the given truck list
	 * and the given day.
	 * Records are ordered by truck and start time.
	 *
	 */
	public List<TrafficJamEntity> fetchTrafficJams(List<Long> trucks, Date date){
		if (trucks.isEmpty()) {
			return Collections.emptyList();
		}

		ReadWriteDao<TrafficJamEntity> tjDao = getDaoProvider().getTrafficJamEntityDao();

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = c.getTime();

		Filter<TrafficJamEntity> f = new FilterImpl<TrafficJamEntity>();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(TrafficJamEntity.ATTR_TRUCK_ID,
				trucks));
		f.addExpression(Restrictions.lt(TrafficJamEntity.ATTR_START_DAT, nextDay));
		f.addExpression(Restrictions.ge(TrafficJamEntity.ATTR_START_DAT, date));

		f.addExpression(Order.asc(TrafficJamEntity.ATTR_TRUCK_ID));
		f.addExpression(Order.asc(TrafficJamEntity.ATTR_START_DAT));

		return tjDao.find(f);
	}

	/**
	 * Fetches a DriverBreakEntity by its id;
	 */
	public DriverBreakEntity fetchDriverBreak(Long driverBreakId){
		ReadWriteDao<DriverBreakEntity> dbDao = getDaoProvider().getDriverBreakEntityDao();

		return dbDao.findById(driverBreakId);
	}


	/**
	 * Fetches a DriverEntity by its id;
	 */
	public DriverEntity fetchDriver(Long driverId){
		ReadWriteDao<DriverEntity> driverDao = getDaoProvider().getDriverEntityDao();
		return driverDao.findById(driverId);

	}

	/**
	 *
	 * @param truckIds
	 * @param date
	 * @return
	 */
	private List<Long> fetchAssignmentIdsByTruckIds(List<Long> truckIds, Date date, TimeZone timeZone) {
		if (truckIds == null || truckIds.isEmpty()) {
			return new LinkedList<Long>();
		}
		Date minTime = timeZone.midnightOf(date);
		Date maxTime = timeZone.morningOf(date);

		final ReadOnlyDao<DispoAssignTruckEntity> dao =
				getDaoProvider().getDispoAssignTruckEntityDao();
		final Filter<DispoAssignTruckEntity> f = dao.createFilter();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoAssignTruckEntity.ATTR_TRUCK_ID, truckIds));
		f.addExpression(
				// LOGON-6664 include cross-midnight orders
				Restrictions.and(
					Restrictions.le(DispoAssignTruckEntity.ATTR_MIN_START, minTime),
					Restrictions.ge(DispoAssignTruckEntity.ATTR_MAX_END, maxTime)
				)
		);

		JPAQuery<DispoAssignTruckEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.simpleSelect(DispoAssignTruckEntity.ATTR_ASSIGNMENT_ID));
		jpaQuery.setFilter(f);
		jpaQuery.setDistinct(true);

		return dao.find(jpaQuery);
	}


	/**
	 *
	 * @param poolId
	 * @param carrierIds
	 * @param date
	 * @param allowedTruckIds restrict the result list by truck ids if more than zero specified
	 * @return
	 */
	private List<Long> fetchAssignmentIdsByPoolIdAndCarrierIds(Long poolId, List<Long> carrierIds,
			Date date, TimeZone timeZone, List<Long> allowedTruckIds) {
		if (carrierIds.isEmpty()) {
			return new LinkedList<>();
		}

		Date minTime = timeZone.midnightOf(date);
		Date maxTime = timeZone.morningOf(date);

		final ReadOnlyDao<DispoAssignPoolCarrEntity> dao = getDaoProvider().getDispoAssignPoolCarrEntityDao();
		final Filter<DispoAssignPoolCarrEntity> assignmentFilter = dao.createFilter();

		assignmentFilter.addExpression(Restrictions.eq(DispoAssignPoolCarrEntity.ATTR_POOL_ID, poolId));
		assignmentFilter.addExpression(
			createCarrierRestriction(carrierIds)
		);

		if (allowedTruckIds != null && !allowedTruckIds.isEmpty()) {
			assignmentFilter.addExpression(
				Restrictions.or(
					DbUtils.createSplittedInRestrictionOfIds(DispoAssignPoolCarrEntity.ATTR_TRUCK_ID, allowedTruckIds),
					DbUtils.createSplittedInRestrictionOfIds(DispoAssignPoolCarrEntity.ATTR_PLANNING_TRUCK_ID, allowedTruckIds))
			);
		}

		// LOGON-6664 include cross-midnight orders
		Expression currentDayFilter = Restrictions.and(
				Restrictions.le(DispoAssignPoolCarrEntity.ATTR_MIN_START, minTime),
				Restrictions.ge(DispoAssignPoolCarrEntity.ATTR_MAX_END, maxTime)
		);

		Date currentTime =TimeZone.getDBTimeZone().asCalendar().getTime();
		if (maxTime.before(currentTime)) {
			// If we are showing a day before or on "today", then we have to show the previous days uncompleted deliveries.
			assignmentFilter.addExpression(
					Restrictions.or(currentDayFilter,
							// CRHLOGON-1325
							Restrictions.and(
									Restrictions.in(DispoAssignPoolCarrEntity.ATTR_ORDER_STATUS_CD, new Long[] {
											Constant.ORDER_STATUS_NEW,
											Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
											Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
											Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
											Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
											Constant.ORDER_STATUS_DISPATCHED,
											Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
											Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
											Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
											Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
											Constant.ORDER_STATUS_LOADING,
											Constant.ORDER_STATUS_LOADED,
											Constant.ORDER_STATUS_SHIPPING,
											Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
											Constant.ORDER_STATUS_UNLOADING
									}),
									Restrictions.le(DispoAssignPoolCarrEntity.ATTR_MIN_START, maxTime)
							)
					)
			);
		} else {
			// otherwise we are showing a future date and we don't need the uncompleted deliveries.
			assignmentFilter.addExpression(currentDayFilter);
		}

		JPAQuery<DispoAssignPoolCarrEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.simpleSelect(DispoAssignPoolCarrEntity.ATTR_ASSIGNMENT_ID));
		jpaQuery.setFilter(assignmentFilter);
		jpaQuery.setDistinct(true);

		@SuppressWarnings("unchecked")
		List<Long> assignmentIds = dao.find(jpaQuery);
		return assignmentIds;
	}


	/**
	 * Fetches assignments based on both authorization types (by truck / by pool & carrier)
	 * @param assignmentIdsByTruck
	 * @param assignmentIdsByPoolAndCarrier
	 * @param langCd
	 * @param orderNumber
	 * @return
	 */
	private List<DispoAssignmentListEntity> fetchAssignmentsForDispo(
			List<Long> assignmentIdsByTruck, List<Long> assignmentIdsByPoolAndCarrier, String langCd, Long orderNumber) {
		if (assignmentIdsByTruck.isEmpty() && assignmentIdsByPoolAndCarrier.isEmpty() && orderNumber == null) {
			return Collections.<DispoAssignmentListEntity>emptyList();
		}

		final List<Expression> assignmentIdRestrictions = new ArrayList<Expression>();

		if (!assignmentIdsByTruck.isEmpty()){
			assignmentIdRestrictions.add(DbUtils.createSplittedInRestrictionOfIds(
					DispoAssignmentListEntity.ATTR_ASSIGNMENT_ID, assignmentIdsByTruck));
		}

		if (!assignmentIdsByPoolAndCarrier.isEmpty()){
			assignmentIdRestrictions.add(DbUtils.createSplittedInRestrictionOfIds(
					DispoAssignmentListEntity.ATTR_ASSIGNMENT_ID, assignmentIdsByPoolAndCarrier));
		}

		final ReadOnlyDao<DispoAssignmentListEntity> dao = getDaoProvider().getDispoAssignmentListEntityDao();
		final Filter<DispoAssignmentListEntity> filter = dao.createFilter();
		if (orderNumber != null) {
			filter.addExpression(Restrictions.eq(DispoAssignmentListEntity.ATTR_ORDER_NUMBER,
					orderNumber));
		}
		filter.addExpression(Restrictions.or(assignmentIdRestrictions));

		filter.addExpression(Restrictions.or(
				Restrictions.eq(DispoAssignmentListEntity.ATTR_LANG_CD, langCd),
				Restrictions.isNull(DispoAssignmentListEntity.ATTR_LANG_CD))); //there are no language-dependent fields if there is no order

		filter.addExpression(Order.asc(DispoAssignmentListEntity.ATTR_TRUCK_ID));
		filter.addExpression(Order.asc(DispoAssignmentListEntity.ATTR_ASSIGNMENT_DAT));

		return dao.find(filter);
	}


	public List<DispoRailAssListEntity> fetchRailAssignmentsForDispo(Date date, List<Long> wagons,
			List<Long> shippingpoints, Long railwayStationId) {
		if (wagons.isEmpty()) {
			return new ArrayList<DispoRailAssListEntity>();
		}

		final ReadOnlyDao<DispoRailAssListEntity> ralDao = getDaoProvider().getDispoRailAssListEntityDao();
		final Filter<DispoRailAssListEntity> filter = ralDao.createFilter();
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoRailAssListEntity.ATTR_WAGON_ID, wagons));
		if ((shippingpoints != null) && (!shippingpoints.isEmpty())) {
			filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					DispoRailAssListEntity.ATTR_SHIPPINGPOINT_ID, shippingpoints));
		}
		if (railwayStationId != null) {
			filter.addExpression(Restrictions.eq(DispoRailAssListEntity.ATTR_RAILWAY_STATION_ID,
					railwayStationId));
		}

		//			filter.addExpression(Restrictions.ge(DispoRailAssListEntity.ATTR_ASSIGNMENT_DAT, date));
		filter.addExpression(Order.asc(DispoRailAssListEntity.ATTR_ASSIGNMENT_DAT));

		return ralDao.find(filter);
	}

	public List<DispoWContainerListEntity> fetchRailContainersForDispo(List wagons,
			String langCd) {
		if (wagons.isEmpty()) {
			return new ArrayList<DispoWContainerListEntity>();
		}
		ReadOnlyDao<DispoWContainerListEntity> wclDao = getDaoProvider().getDispoWContainerListEntityDao();
		Filter<DispoWContainerListEntity> f = wclDao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoWContainerListEntity.ATTR_CURRENT_WAGON_ID, wagons));
		f.addExpression(Restrictions.eq(DispoWContainerListEntity.ATTR_LANG_CD, langCd));
		return wclDao.find(f);
	}

	/**
	 * Fetches assignment parts for given assignment id list, but besides widens the queried parts
	 * according to order attributes: commercial, executing carrier, and pool parameters.
	 * The carrierIds and pool ids are widening the set of returned parts, and only unloading parts will be returned
	 * according to those.
	 */
	public List<DispoAssignPartListEntity> fetchAssignmentPartsForDispo(List assignments,
			String langCd) {
		if (assignments.isEmpty()) {
			return new ArrayList<DispoAssignPartListEntity>();
		}

		final ReadOnlyDao<DispoAssignPartListEntity> dao = getDaoProvider().getDispoAssignPartListEntityDao();
		final Filter<DispoAssignPartListEntity> f = dao.createFilter();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoAssignPartListEntity.ATTR_ASSIGNMENT_ID, assignments));
		f.addExpression(Restrictions.or(
				Restrictions.eq(DispoAssignPartListEntity.ATTR_LANG_CD, langCd),
				Restrictions.isNull(DispoAssignPartListEntity.ATTR_LANG_CD))); //there are no language-dependent fields if there is no order

		return dao.find(f);
	}

	public List<Long> fetchTruckIdsForAssignments(List<Long> assignmentIds) {
		if (assignmentIds == null || assignmentIds.isEmpty()) {
			return Collections.emptyList();
		}
		final ReadWriteDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
		final Filter<AssignmentEntity> filter = assignmentDao.createFilter();
		final List<Expression> expressions = new ArrayList<Expression>();

		for (Long assignmentId : assignmentIds) {
			expressions.add(Restrictions.eq(AssignmentEntity.ATTR_ASSIGNMENT_ID, assignmentId));
		}
		filter.addExpression(Restrictions.or(expressions));

		JPAQuery<AssignmentEntity> jpaQuery = assignmentDao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.simpleSelect(AssignmentEntity.ATTR_TRUCK_ID));
		jpaQuery.setFilter(filter);
		jpaQuery.setDistinct(true);

		@SuppressWarnings("unchecked")
		final List<Long> truckIds = assignmentDao.find(jpaQuery);

		return truckIds;
	}

	public AssignmentEntity fetchAssignmentForEdit(Long assignmentId) {
		ReadWriteDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity result = assignmentDao.findById(assignmentId);
		result.initialize();
		Iterator i = result.getAssignmentParts().iterator();
		while (i.hasNext()) {
			AssignmentPartEntity ape = (AssignmentPartEntity) i.next();
			if (ape.getOrder() != null) {
				ape.getOrder().initialize();
			}
		}
		return result;
	}

	public OrderEntity fetchOrderForEdit(Long orderNumber) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter<OrderEntity> f = new FilterImpl<OrderEntity>();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
		OrderEntity result = orderDao.findSingle(f);
		if(result != null) {
			result.initialize();
			if(result.getOrderItems() != null && result.getOrderItems().size() > 0) {
				result.getOrderItems().get(0);
			}
		}
		return result;
	}


	public OrderEntity fetchOrder(Long orderId) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();

		return orderDao.findById(orderId);
	}

	public ExtOrderEntity fetchExtOrderForEdit(Long orderNumber) {
		ReadWriteDao<ExtOrderEntity> extOrderDao = getDaoProvider().getExtOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(ExtOrderEntity.ATTR_EXT_ORDER_NUMBER, orderNumber));
		return extOrderDao.findSingle(f);
	}

	public List fetchShippingpoints(Integer start, Integer count, String query) {
		ReadWriteDao<ShippingpointEntity> shippingpointDao = getDaoProvider().getShippingpointEntityDao();
		/* See DispokarteService for comments
		 * TODO (cosmin 21.1.09): Write a utility function ti do these replacements
		 */
		query = query.replace("|", "||");
		query = query.replace("%", "|%");
		query = query.replace("_", "|_");
		query = query.replace('*', '%');
		query = query += '%';
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.ilike(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME, query));

		return shippingpointDao.findNRecs(start, count, f);
	}

	public long countShippingpoints(String query) {
		ReadWriteDao<ShippingpointEntity> shippingpointDao = getDaoProvider().getShippingpointEntityDao();
		/* See DispokarteService for comments
		 * TODO (cosmin 21.1.09): Write a utility function ti do these replacements
		 */
		query = query.replace("|", "||");
		query = query.replace("%", "|%");
		query = query.replace("_", "|_");
		query = query.replace('*', '%');
		query = query += '%';
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.ilike(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME, query));
		return shippingpointDao.count(f);
	}

	/**
	 * Returns a shipto by its sap_bp_id
	 * @param sapBpId
	 * @return ShiptoEntity with given sap id or null, if none found
	 */
	public ShiptoEntity fetchShipto(long sapBpId) {
		ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, sapBpId));
		f.addExpression(Restrictions.eq(ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		return shiptoDao.findSingle(f);
	}

	/**
	 * Returns a shippingpoint by its id
	 * @param shipingpointId
	 * @return ShippingpointEntity with given id or null, if none found
	 */
	public ShippingpointEntity fetchShippingpoint(long shipingpointId) {
		ReadWriteDao<ShippingpointEntity> shipingpointDao = getDaoProvider().getShippingpointEntityDao();
		return shipingpointDao.findById(shipingpointId);
	}

	/**
	 * Query for Ship-To-Kunde Dropdown search
	 * @param skip from pager
	 * @param num from pager
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @return List of SapShiptoSoldoSearchViewEntity entities matching the query
	 */
	public List fetchShipToKunde(int skip, int num, String query) {
		Filter f = fetchShipToKundeImpl(query);

		f.addExpression(Order.asc(DispoShiptoSoldtoEntity.ATTR_SHIPTO_NAME));
		f.addExpression(Order.asc(DispoShiptoSoldtoEntity.ATTR_SOLDTO_NAME));

		return getDaoProvider().getDispoShiptoSoldtoEntityDao().findNRecs(skip, num, f);
	}


	/**
	 * Query for Ship-To-Kunde Dropdowsn search
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @return nr of return records
	 */
	public long countShipToKunde(String query) {
		Filter f = fetchShipToKundeImpl(query);
		return getDaoProvider().getDispoShiptoSoldtoEntityDao().count(f);

	}

	/**
	 * Internal implementation of the fetch- and countShipToKunde methods.
	 * Query parameters from client and the select part of the query are
	 * passed as parameters.
	 *
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @return Filter implementing the search query
	 *
	 * @adnlog.exclude return
	 */
	private Filter fetchShipToKundeImpl(String query) {
		Long longValue = null;
		try {
			longValue = new Long(query);
		}
		catch (NumberFormatException ex) {
			/* Obviously not a number, now do wildcard replacements
			 * First replace special characters with escape sequences
			 * We use | as the escape character */
			query = query.replace("|", "||");
			query = query.replace("%", "|%");
			query = query.replace("_", "|_");
			/* now replace the query wildcard characlter '*'
			 * by the JPAQL escape character*/
			query = query.replace('*', '%');
			/* finally add a wildcard character at the end of the String*/
			query = query += '%';
			/* now convert to uppercase */
			query = query.toUpperCase();
		}
		LOG.debug("query : '" + query + "', longValue :" + longValue);
		Expression ex = Restrictions.or(
				Restrictions.ilike(DispoShiptoSoldtoEntity.ATTR_SHIPTO_NAME, query),
				Restrictions.ilike(DispoShiptoSoldtoEntity.ATTR_SOLDTO_NAME, query),
				Restrictions.ilike(DispoShiptoSoldtoEntity.ATTR_POSTAL_CODE, query),
				Restrictions.ilike(DispoShiptoSoldtoEntity.ATTR_CITY, query));

		if (longValue != null) {
			ex = Restrictions.or(ex,
					Restrictions.eq(DispoShiptoSoldtoEntity.ATTR_SHIPTO_NR, longValue),
					Restrictions.eq(DispoShiptoSoldtoEntity.ATTR_SOLDTO_NR, longValue));
		}

		Filter f = new FilterImpl();
		f.addExpression(ex);

		return f;
	}

	/**
	 * Modifies startTime and duration of a DriverBreakEntity.
	 * diroverBreakId is null, an new entity wil be created, using
	 * the passed parameters.
	 * All other cases of creation are handled by the DriverBreak popup.
	 *
	 * @param driverBreakId if null a new entity is created.
	 * @param driverId
	 * @param startMinute
	 * @param duration
	 * @param isLunchBreak used only for creation
	 * @param request
	 */
	public void modifyDriverBreakTimes(Long driverBreakId, Long driverId,
			Date startTime, Date endTime, boolean isLunchBreak){
		LinkedList drivers = new LinkedList();
		drivers.add(driverId);
		List oldBreaks = fetchDriverBreaksForEdit(drivers,
				DateTimeUtils.cleanTimeWithDefaultTimeZone(startTime), false);

		ReadWriteDao<DriverBreakEntity> dbeDao = getDaoProvider().getDriverBreakEntityDao();
		DriverBreakEntity dbe;
		boolean lunchBreakExists = false;
		if (driverBreakId != null) {
			dbe = dbeDao.findById(driverBreakId);
			Iterator breaks = oldBreaks.iterator();
			while (breaks.hasNext()) {
				DriverBreakEntity brk = (DriverBreakEntity) breaks.next();
				if (brk.getDriverBreakId().equals(dbe.getDriverBreakId())) {
					breaks.remove();
				}
				else if (brk.getIsLunchBreak()) {
					lunchBreakExists = true;
				}

			}
			dbe.setStartDat(startTime);
			dbe.setEndDat(endTime);
		}
		else {
			dbe = new DriverBreakEntity();
			dbe.setDriver(getDaoProvider().getDriverEntityDao().findById(driverId));
			dbe.setIsLunchBreak(isLunchBreak);
			dbe.setIsMopBreak(Boolean.FALSE);
			dbe.setStartDat(startTime);
			dbe.setEndDat(endTime);
			dbe.setActiveFlag(Boolean.TRUE);
			dbeDao.persist(dbe);
		}
		Date date = DateTimeUtils.cleanTimeWithDefaultTimeZone(new Date());
		if (!lunchBreakExists && !isLunchBreak) {
			ConfigBean cb = configService.getConfigBean();
			DriverBreakEntity lunchBreak = new DriverBreakEntity();
			lunchBreak.setDriver(getDaoProvider().getDriverEntityDao().findById(driverId));
			lunchBreak.setIsLunchBreak(Boolean.TRUE);
			lunchBreak.setIsMopBreak(Boolean.FALSE);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.MINUTE, cb.getAppDefaultDispoDriverLunchBreakStart());
			lunchBreak.setStartDat(c.getTime());
			c.setTime(date);
			c.add(Calendar.MINUTE, cb.getAppDefaultDispoDriverLunchBreakEnd());
			lunchBreak.setEndDat(c.getTime());
			lunchBreak.setActiveFlag(Boolean.TRUE);

			List newBreaks = DriverBreaksUtils.mergeDriverBreaks(lunchBreak, oldBreaks);

			Iterator breaks = oldBreaks.iterator();
			while (breaks.hasNext()){
				DriverBreakEntity brk = (DriverBreakEntity)breaks.next();
				brk.setActiveFlag(Boolean.FALSE);
				dbeDao.merge(brk);
			}

			if (newBreaks.contains(lunchBreak)) {
				dbeDao.persist(lunchBreak);
			}

			oldBreaks = newBreaks;

		}

		List newBreaks = DriverBreaksUtils.mergeDriverBreaks(dbe, oldBreaks);

		Iterator breaks = oldBreaks.iterator();
		while (breaks.hasNext()) {
			DriverBreakEntity brk = (DriverBreakEntity) breaks.next();
			brk.setActiveFlag(Boolean.FALSE);
			dbeDao.merge(brk);
		}

		if (!newBreaks.contains(dbe)) {
			dbe.setActiveFlag(Boolean.FALSE);
			dbeDao.merge(dbe);
		}
	}

	/**
	 * Persists or merges the passed entity
	 */
	public void persistDriverBreak(DriverBreakEntity driverBreak){
		LinkedList drivers = new LinkedList();
		drivers.add(driverBreak.getDriver().getDriverId());
		List oldBreaks = fetchDriverBreaksForEdit(drivers,
				DateTimeUtils.cleanTimeWithDefaultTimeZone(driverBreak.getStartDat()), false);

		driverBreak.setActiveFlag(Boolean.TRUE);

		ReadWriteDao<DriverBreakEntity> dbeDao = getDaoProvider().getDriverBreakEntityDao();
		if (driverBreak.getDriverBreakId() == null) {
			driverBreak = dbeDao.persist(driverBreak);
		}
		else {
			driverBreak = dbeDao.merge(driverBreak);
			Iterator breaks = oldBreaks.iterator();
			while (breaks.hasNext()){
				DriverBreakEntity brk = (DriverBreakEntity)breaks.next();
				if (brk.getDriverBreakId().equals(driverBreak.getDriverBreakId())) {
					breaks.remove();
				}
			}
		}

		DriverBreaksUtils.mergeDriverBreaks(driverBreak, oldBreaks);
		Iterator breaks = oldBreaks.iterator();
		while (breaks.hasNext()) {
			DriverBreakEntity brk = (DriverBreakEntity) breaks.next();
			brk.setActiveFlag(Boolean.FALSE);
			dbeDao.merge(brk);
		}
	}

	/**
	 * Deletes the antity with the passed id
	 */
	public void deleteDriverBreak(Long driverBreakId){
		ReadWriteDao<DriverBreakEntity> dbeDao = getDaoProvider().getDriverBreakEntityDao();
		if (driverBreakId != null) {
			DriverBreakEntity entity = dbeDao.findById(driverBreakId);
			entity.setActiveFlag(Boolean.FALSE);
			dbeDao.merge(entity);
		}
	}

	/**
	 * Creates a DriverBreakEntity, triggered by the PDA.
	 *
	 * @param driver
	 * @param startTime
	 */
	public void startMopBreak(DriverEntity driver, Date startTime){
		// break entity is created only for drivers
		if (driver != null) {
			ReadWriteDao<DriverBreakEntity> dbeDao = getDaoProvider().getDriverBreakEntityDao();

			// Find previous oben breaks and close them with duration 0
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_DRIVER_ID, driver.getDriverId()));
			f.addExpression(Restrictions.le(DriverBreakEntity.ATTR_START_DAT, startTime));
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_IS_MOP_BREAK, Boolean.TRUE));
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_END_DAT, Constant.INFINITE_DATE));
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));

			Iterator breaks = dbeDao.findNRecs(0, 100, f).iterator();
			while (breaks.hasNext()) {
				DriverBreakEntity dbe = (DriverBreakEntity) breaks.next();
				dbe.setEndDat(dbe.getStartDat());
		    }

			DriverBreakEntity newDbe = new DriverBreakEntity();
			newDbe.setDriver(driver);
			newDbe.setIsLunchBreak(Boolean.FALSE);
			newDbe.setIsMopBreak(Boolean.TRUE);
			newDbe.setStartDat(startTime);
			newDbe.setEndDat(Constant.INFINITE_DATE);
			newDbe.setActiveFlag(Boolean.TRUE);
			dbeDao.persist(newDbe);
		}
	}

	/**
	 * Ends a DriverBreakEntity, triggered by the PDA.
	 *
	 * @param driver
	 * @param endTime
	 */
	public void endMopBreak(DriverEntity driver, Date endTime){
		// break entity is created only for drivers
		if (driver != null) {
			ReadWriteDao<DriverBreakEntity> dbeDao = getDaoProvider().getDriverBreakEntityDao();

			// Find previous open breaks and close them with duration 0
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_DRIVER_ID, driver.getDriverId()));
			f.addExpression(Restrictions.le(DriverBreakEntity.ATTR_START_DAT, endTime));
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_IS_MOP_BREAK, Boolean.TRUE));
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_END_DAT, Constant.INFINITE_DATE));
			f.addExpression(Restrictions.eq(DriverBreakEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));

			f.addExpression(Order.asc(DriverBreakEntity.ATTR_START_DAT));

			// close all previous breaks with duration 0
			Iterator breaks = dbeDao.findNRecs(0, 100, f).iterator();
			DriverBreakEntity dbe = null;
			while (breaks.hasNext()) {
				dbe = (DriverBreakEntity) breaks.next();
				dbe.setEndDat(dbe.getStartDat());
			}
			// set duration for last break
			if (dbe != null) {
				dbe.setEndDat(endTime);
			}
		}
	}

	/**
	 * transmits assignment to driver PDA
	 *
	 */
	public void transmitToDriver(Long assignmentId, long weighSystemSpCd,
			long weighSystemLpCd) {
		transmitToDriver(assignmentId, weighSystemSpCd, weighSystemLpCd,
				BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK, ALLOW_TRANSMIT_ASSIGNMENT_TO_WEIGHT_SYSTEM);
	}

	public void transmitToDriver(Long assignmentId, long weighSystemSpCd,
			long weighSystemLpCd, boolean allowTransmitAssignmentToWeightSystem) {
		transmitToDriver(assignmentId, weighSystemSpCd, weighSystemLpCd,
				BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK, allowTransmitAssignmentToWeightSystem);
	}

	public void transmitToDriver(Long assignmentId, long weighSystemSpCd,
			long weighSystemLpCd, BizeventTypeBsEntity bizEventType) {
		transmitToDriver(assignmentId, weighSystemSpCd, weighSystemLpCd,
				bizEventType, ALLOW_TRANSMIT_ASSIGNMENT_TO_WEIGHT_SYSTEM);
	}

	public void transmitToDriver(Long assignmentId, long weighSystemSpCd,
			long weighSystemLpCd, BizeventTypeBsEntity bizEventType, boolean allowTransmitAssignmentToWeightSystem) {
		ReadWriteDao<AssignmentEntity> aed = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = aed.findById(assignmentId);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(bizEventType);
		event.setAssignment(assignment);
		event.setTruck(assignment.getTruck());
		event.setEventTs(new Date());
		constructEstimatedTimeArrivalForOrders(event);

		monitoringTablesService.processBizevent(event);

		orderService.transmitToDriver(event, weighSystemSpCd, weighSystemLpCd,
				allowTransmitAssignmentToWeightSystem);
	}

	private void constructEstimatedTimeArrivalForOrders(BizeventEntity event) {
		final AssignmentEntity assignment = event.getAssignment();
		String descriptionEta = "";

		for (Object parts : assignment.getAssignmentParts() ) {

			final AssignmentPartEntity part = (AssignmentPartEntity) parts;

			if ( part.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_UNLOAD) && part.getOrder() != null ) {

				final OrderEntity order = part.getOrder();

				try {
					final Date calculatedTimeOfDelivery = SchedulingUtils.getCalculatedTimeOfDelivery(part.getOrder(), assignment, dispoService, configService, truckAdminService);
					descriptionEta = StringUtils.appendWithNewLine(descriptionEta, DateTimeUtils.formatDayMonthYear(calculatedTimeOfDelivery, Locale.ENGLISH) + " " + DateTimeUtils.formatHoursMinutes(calculatedTimeOfDelivery) + " (" + (order.getOrderNumber()) + ")");
				}
				catch (Exception e){
					LOG.error("Could not calculate time of delivery. [OrderId=" + order.getOrderId() + ";assignmentId=" + assignment.getAssignmentId() + "ordernumber=" + order.getOrderNumber() + "]", e);
				}
			}
		}
		event.setDescription(descriptionEta);

	}


	/**
	 * Enters the loading site (shipping point or ship-to [LF]) for the assignment. Only possible if
	 * the shipping point or ship-to of the assignment is unambiguous, otherwise it would not be
	 * clear what would be the shipping point or ship-to. This is a manual possibility within LOGON
	 * for the cases when other sources like PDA cannot trigger this event.
	 */
	public void enterLoadingSite(Long assignmentId) {
		ReadWriteDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = assignmentDao.findById(assignmentId);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT);
		event.setAssignment(assignment);
		event.setTruck(assignment.getTruck());
		event.setEventTs(new Date());

		if (AssignmentUtils.isInternal(assignment)) {
			final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			OrderEntity firstOrder = null;
			if (orders.size() > 0) {
				firstOrder = orders.get(0);
			}
			if (firstOrder != null && LogonUtil.isOrderWithReversedDirection(firstOrder)) {
				ShiptoEntity shipto = getShiptoForReverseDirectedOrder(assignment);

				if (shipto == null && !LogonUtil.isReturnPalletOnly(firstOrder)) {
					LOG.error("Could not determine the shippingpoint unambiguously.AssignmentId="
							+ assignment.getAssignmentId());
					throw new LogonException(
							"Could not determine the shippingpoint unambiguously.",
							Reason.RES_ORDER_INSUFFICIENT_DATA);
				}
				if (shipto == null) {
					shipto = nextShiptoForArrivingForReverseOrder(assignment);
				}
				event.setShipto(shipto);
			}
			else {
				ShippingpointEntity sp = getAssignmentShippingpoint(assignment);

				if (sp == null) {
					LOG.error("Could not determine the shippingpoint unambiguously.AssignmentId="
							+ assignment.getAssignmentId());
					throw new LogonException(
							"Could not determine the shippingpoint unambiguously.",
							Reason.RES_ORDER_INSUFFICIENT_DATA);
				}
				event.setShippingpoint(sp);
			}
		}

		orderService.enterLoadingSite(event);

		monitoringTablesService.processBizevent(event);
	}

	/**
	 * Takes the first reverse order from the assignment, which is in a state at least released,
	 * but not yet arrived at loadingpoint. Returns the shipto belonging to that order.
	 *
	 * @param assignment
	 * @return shipto
	 * @exception LogonException when assignment has a non-reverse order connected.
	 */
	private ShiptoEntity nextShiptoForArrivingForReverseOrder(AssignmentEntity assignment) {
		ShiptoEntity nextShiptoForArriving = null;

		List<AssignmentPartEntity> loadAssignmentParts = new ArrayList<AssignmentPartEntity>();
		List<AssignmentPartEntity> unloadAssignmentParts = new ArrayList<AssignmentPartEntity>();
		AssignmentUtils.getSortedAssignmentParts(assignment, loadAssignmentParts, unloadAssignmentParts);

		for (AssignmentPartEntity unloadAssignmentPart : unloadAssignmentParts) {
			OrderEntity order = unloadAssignmentPart.getOrder();
			if (!LogonUtil.isOrderWithReversedDirection(order)) {
				String message = "next shipto is intended to be calculated only for reverse orders";
				throw new LogonException(message, Reason.RES_IMPL_BUG);
			}
		}

		SapBpEntity nextSapBpShiptoForArriving = null;
		for (AssignmentPartEntity unloadAssignmentPart : unloadAssignmentParts) {
			OrderEntity order = unloadAssignmentPart.getOrder();
			if (order != null) {
				OrderStatusBsEntity orderStatus = order.getOrderStatus();
				if (LogonStatusUtils.isBeyondDispatched(orderStatus)
						&& LogonStatusUtils.isBeforeArrivedAtLoadingPoint(orderStatus)) {
					nextSapBpShiptoForArriving = order.getShiptoSapBp();
					break;
				}
			}
		}

		if (nextSapBpShiptoForArriving != null) {
			for (AssignmentPartEntity loadAssignmentPart : loadAssignmentParts) {
				ShiptoEntity shiptoInLoadPart = loadAssignmentPart.getShipto();
				SapBpEntity sapBpInLoadPart = shiptoInLoadPart.getShiptoSapBp();
				Long sapBpIdInLoadPart = sapBpInLoadPart.getSapBpId();
				if (shiptoInLoadPart != null && sapBpIdInLoadPart.equals(nextSapBpShiptoForArriving.getSapBpId())) {
					nextShiptoForArriving = shiptoInLoadPart;
					break;
				}
			}
		}
		return nextShiptoForArriving;
	}
	/**
	 * Returns the shipto of the assignment. If it cannot be determined unambiguously it
	 * will return null.
	 * @param assignment
	 */
	private ShiptoEntity getShiptoForReverseDirectedOrder(
			AssignmentEntity assignment) {
		ShiptoEntity shipto = null;

		List<AssignmentPartEntity> parts = AssignmentUtils.getSortedAssignmentParts(assignment);
		for (AssignmentPartEntity ap : parts) {
			if (ap.getAssignmentPartType().getAssignmentPartTypeCd().equals(
					Constant.ASSIGNMENT_PART_TYPE_LOAD)
					|| ap.getAssignmentPartType().getAssignmentPartTypeCd().equals(
							Constant.ASSIGNMENT_PART_TYPE_FROM)) {
				if (ap.getShipto() != null) {
					ShiptoEntity shiptoOfAssignmentPart = ap.getShipto();
					Long shiptoId = shiptoOfAssignmentPart.getShiptoId();
					if (shipto != null && !shiptoId.equals(shipto.getShiptoId())) {
						return null;
					}
					shipto = shiptoOfAssignmentPart;
				}
			}
		}

		return shipto;
	}

	/**
	 * Returns the shippingpoint of the assignment. If it cannot be determined unambiguously it
	 * will return null.
	 * @param assignment
	 */
	private ShippingpointEntity getAssignmentShippingpoint(AssignmentEntity assignment) {
		ShippingpointEntity sp = null;

		List<AssignmentPartEntity> parts = AssignmentUtils.getSortedAssignmentParts(assignment);
		for (AssignmentPartEntity ap : parts) {
			if (ap.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_LOAD) ||
					ap.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_FROM)) {
				if (ap.getShippingpoint() != null) {
					if (sp != null && !ap.getShippingpoint().getShippingpointId().equals(sp.getShippingpointId())) {
						return null;
					}
					sp = ap.getShippingpoint();
				}
			}
		}

		return sp;
	}


	/**
	 * transmits assignment to driver PDA
	 *
	 */
	public void transmitToRailCompany(Long assignmentId) {
		ReadWriteDao<AssignmentEntity> aed = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = aed.findById(assignmentId);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_RAIL);
		event.setAssignment(assignment);
		event.setWagon(assignment.getWagon());
		event.setEventTs(new Date());
		event.setRailwayStation(assignment.getWagon().getLastKnownRailwayStation());

		if (assignment.getAssignmentParts() != null) {
			OrderEntity order = null;
			for (AssignmentPartEntity assignmentPart : assignment.getAssignmentParts()) {
				order = assignmentPart.getOrder();
				if (order != null) {
					event.setOrder(order);
					break;
				}
			}
		}

		monitoringTablesService.processBizevent(event);

		orderService.transmitToRailCompany(event);
	}


	public void setReturned(Long assignmentId) {
		setReturned(assignmentId, new Date());
	}

	/**
	 * sets assignement state to returned
	 *
	 */
	public void setReturned(Long assignmentId, Date date) {
		ReadWriteDao<AssignmentEntity> aed = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = aed.findById(assignmentId);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_RETURNED);
		event.setAssignment(assignment);
		event.setTruck(assignment.getTruck());
		event.setEventTs(date);
		Iterator i = assignment.getAssignmentParts().iterator();
		while (i.hasNext()) {
			AssignmentPartEntity ap = (AssignmentPartEntity) i.next();
			if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
				if (ap.getShippingpoint() != null) {
					event.setShippingpoint(ap.getShippingpoint());
				}
				else if (ap.getShipto() != null) {
					event.setShipto(ap.getShipto());
				}
				else if ((ap.getCoordLat() != null) && (ap.getCoordLon() != null)) {
					event.setCoordLat(ap.getCoordLat());
					event.setCoordLon(ap.getCoordLon());
				}

			}
		}

		monitoringTablesService.processBizevent(event);

		orderService.setReturned(event);
	}

	/**
	 * sets assignement state to unloaded for truck order
	 *
	 */
	public void setTruckUnloaded(Long assignmentId) {
		ReadWriteDao<AssignmentEntity> aed = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = aed.findById(assignmentId);

		BizeventEntity del_event = new BizeventEntity();
		del_event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DELIVERED);
		del_event.setAssignment(assignment);
		del_event.setEventTs(new Date());

		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID,
				assignment.getAssignmentId()));
		f.addExpression(Order.asc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));

		Iterator i = getDaoProvider().getAssignmentPartEntityDao().find(f).iterator();
		while (i.hasNext()) {
			AssignmentPartEntity ape = (AssignmentPartEntity) i.next();
			if ((ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD)
					&& (ape.getOrder() != null)
					&& ((ape.getOrder().getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) || (ape.getOrder().getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_UNLOADING))) {
				del_event.setOrder(ape.getOrder());
				if (ape.getShipto() != null) {
					del_event.setShipto(ape.getShipto());
				}
				else if (ape.getShippingpoint() != null) {
					del_event.setShippingpoint(ape.getShippingpoint());
				}
				else if ((ape.getCoordLat() != null) && (ape.getCoordLon() != null)) {
					del_event.setCoordLat(ape.getCoordLat());
					del_event.setCoordLon(ape.getCoordLon());
				}
				break;

			}
		}

		del_event.setTruck(assignment.getTruck());
		del_event.setEventTs(new Date());

		// This bizevent should not be persisted here (see OrderServiceBean._setUnloadedImpl)
		// monitoringTablesService.processBizevent(del_event);

		orderService.setUnloaded(del_event, null, false, true, false);

		BizeventEntity leave_event = new BizeventEntity();
		leave_event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LEAVE_SHIPTO);
		leave_event.setAssignment(assignment);
		leave_event.setOrder(del_event.getOrder());
		leave_event.setTruck(assignment.getTruck());
		leave_event.setEventTs(del_event.getEventTs());
		leave_event.setShipto(del_event.getShipto());
		leave_event.setShippingpoint(del_event.getShippingpoint());
		leave_event.setCoordLat(del_event.getCoordLat());
		leave_event.setCoordLon(del_event.getCoordLon());
		leave_event.setEventTs(del_event.getEventTs());

		monitoringTablesService.processBizevent(leave_event);

		orderService.leaveShipto(leave_event);
	}

	public void setTruckUnloaded(AssignmentPartEntity ape, Date asOfDate) {
		BizeventEntity del_event = new BizeventEntity();
		del_event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DELIVERED);
		del_event.setAssignment(ape.getAssignment());
		del_event.setOrder(ape.getOrder());
		del_event.setEventTs(asOfDate);
		del_event.setShipto(ape.getShipto());
		del_event.setShippingpoint(ape.getShippingpoint());
		del_event.setCoordLat(ape.getCoordLat());
		del_event.setCoordLon(ape.getCoordLon());
		del_event.setTruck(ape.getAssignment().getTruck());

		// This bizevent should not be persisted here (see OrderServiceBean._setUnloadedImpl)
		// monitoringTablesService.processBizevent(del_event);

		orderService.setUnloaded(del_event, null, false, true, false);

		BizeventEntity leave_event = new BizeventEntity();
		leave_event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LEAVE_SHIPTO);
		leave_event.setAssignment(del_event.getAssignment());
		leave_event.setOrder(del_event.getOrder());
		leave_event.setTruck(del_event.getTruck());
		leave_event.setEventTs(del_event.getEventTs());
		leave_event.setShipto(del_event.getShipto());
		leave_event.setShippingpoint(del_event.getShippingpoint());
		leave_event.setCoordLat(del_event.getCoordLat());
		leave_event.setCoordLon(del_event.getCoordLon());
		leave_event.setEventTs(del_event.getEventTs());

		monitoringTablesService.processBizevent(leave_event);

		orderService.leaveShipto(leave_event);

		for (Iterator it = ape.getAssignment().getAssignmentParts().iterator(); it.hasNext();) {
			AssignmentPartEntity ap = (AssignmentPartEntity) it.next();
			if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
				if (ap.getRealStart() != null) {
					setReturned(ape.getAssignment().getAssignmentId(), ap.getRealStart());
				}
				break;
			}
		}
	}


	public double checkBimodalLoadBalance(Long orderId) {
		OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);
		Filter filter = new FilterImpl();
		List materialItems = (List) LogonPackageHandlingUtil.getMaterialItems(order);
		if (materialItems.isEmpty()) {
			return 0.0;
		}
		List orderItems = materialItems;
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				OrderItemEntity.ATTR_BIMODAL_RAIL_ORDER_ITEM, orderItems));
		if ((order.getDeliveryType().getDeliveryTypeCd().longValue() == Constant.DELIVERY_TYPE_BIMODAL_RAIL)
				|| (getDaoProvider().getOrderItemEntityDao().count(filter) > 0)) {
			OrderItemEntity bulkOrderItem = LogonUtil.getBulkDumpOrPowderOrderItem(orderItems);
			if (bulkOrderItem != null) {
				BigDecimal remainingQuantityT = orderService.getWagonRemainingQuantityT(bulkOrderItem.getLoad());

				return remainingQuantityT.doubleValue();
			}
		}

		return 0.0;
	}

	public double checkBimodalLoadBalanceAssignment(Long assignmentId) {
		AssignmentEntity assignment = getDaoProvider().getAssignmentEntityDao().findById(
				assignmentId);
		for (Iterator it = assignment.getAssignmentParts().iterator(); it.hasNext();) {
			AssignmentPartEntity ap = (AssignmentPartEntity) it.next();
			if (ap.getOrder() != null) {
				return checkBimodalLoadBalance(ap.getOrder().getOrderId());
			}
		}

		return 0;
	}

	/**
	 * sets assignement state to unloaded for rail order
	 *
	 */
	public void setRailUnloaded(Long assignmentId) {
		ReadWriteDao<AssignmentEntity> aed = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = aed.findById(assignmentId);

		BizeventEntity del_event = new BizeventEntity();
		del_event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DELIVERED);
		del_event.setAssignment(assignment);

		// TODO (cosmin Aug 20, 2009): subquery because ass part collection of ass entity is not ordered
		// Oredering the collection would be great
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID,
				assignment.getAssignmentId()));
		f.addExpression(Order.asc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));

		Iterator i = getDaoProvider().getAssignmentPartEntityDao().find(f).iterator();
		while (i.hasNext()) {
			AssignmentPartEntity ape = (AssignmentPartEntity) i.next();
			if ((ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD)
					&& (ape.getOrder() != null)) {
				del_event.setOrder(ape.getOrder());
				if (ape.getShipto() != null) {
					del_event.setShipto(ape.getShipto());
				}
				else if (ape.getShippingpoint() != null) {
					del_event.setShippingpoint(ape.getShippingpoint());
					del_event.setRailwayStation(ape.getShippingpoint().getRailwayStation());
				}
				else if ((ape.getCoordLat() != null) && (ape.getCoordLon() != null)) {
					del_event.setCoordLat(ape.getCoordLat());
					del_event.setCoordLon(ape.getCoordLon());
				}

			}
			if (ape.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
				del_event.setContainer(ape.getLoad().getContainer());
			}
		}
		if (del_event.getOrder() == null) {
			return;
		}

		del_event.setWagon(assignment.getWagon());
		del_event.setEventTs(new Date());

		// This bizevent should not be persisted here (see OrderServiceBean._setUnloadedImpl)
		// monitoringTablesService.processBizevent(del_event);

		orderService.setUnloaded(del_event, null, false, true, false);
	}

	/**
	 * Enter loading site (shipping point or ship-to) for all assignment ids.
	 * @param assignmentIdList
	 * @return List<Long> List of truck ids for the given assignment ids
	 */
	public List<Long> enterLoadingSiteAndFetchTruckIds(ArrayList<Long> assignmentIdList) {
		for (Long assignmentId : assignmentIdList) {
			enterLoadingSite(assignmentId);
		}

		return fetchTruckIdsForAssignments(assignmentIdList);
	}


	/**
	 * Enter unloading site (ship-to or shipping point) for all assignment ids.
	 * @param assignmentIdList
	 * @return List<Long> List of truck ids for the given assignment ids
	 */
	public List<Long> enterUnloadingSiteAndFetchTruckIds(ArrayList<Long> assignmentIdList) {
		for (Long assignmentId : assignmentIdList) {
			enterUnloadingSite(assignmentId);
		}

		return fetchTruckIdsForAssignments(assignmentIdList);
	}

	/**
	 * Updates the next order in the assignment to "arrived at delivery point"
	 */
	public void enterUnloadingSite(Long assignmentId) {
		ReadWriteDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = assignmentDao.findById(assignmentId);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT);
		event.setAssignment(assignment);

		List<AssignmentPartEntity> unloadAps = new ArrayList<AssignmentPartEntity>();
		AssignmentUtils.getSortedAssignmentParts(assignment, null, unloadAps);

		for (AssignmentPartEntity ap : unloadAps) {
			OrderEntity order = ap.getOrder();

			if (order != null) {
				OrderStatusBsEntity orderStatus = order.getOrderStatus();
				long orderStatusCd = orderStatus.getOrderStatusCd();

				if (LogonStatusUtils.isLoadedOrBeyond(orderStatusCd)
						&& LogonStatusUtils.isBeforeArrivedAtUnloadingPoint(orderStatusCd)) {
					/*
					 * This is the order which is to enter its unloading point. Note that in
					 * the case of tours, there could be others, so we need the state-based
					 * logic above.
					 */
					event.setOrder(order);

					event.setShipto(ap.getShipto()); // standard case
					event.setShippingpoint(ap.getShippingpoint()); // landfill / return case

					if ((ap.getCoordLat() != null) && (ap.getCoordLon() != null)) {
						event.setCoordLat(ap.getCoordLat());
						event.setCoordLon(ap.getCoordLon());
					}

					// one after another, leave the other ones alone until the next click
					break;
				}
			}
		}

		event.setTruck(assignment.getTruck());
		event.setWagon(assignment.getWagon());
		event.setEventTs(new Date());

		orderService.enterUnloadingSite(event, false);

		monitoringTablesService.processBizevent(event);
	}

	/**
	 * Set the etran loaded number for an order (russian electronic delivery note)
	 * for loaded rail orders
	 * Set the etran empty number for an order (russian electronic delivery note)
	 * this is for empty returning rail orders
	 *
	 * @param etranLoaded
	 * @param etranEmpty
	 * @param orderNumber
	 */
	public void setOrderEtran(Long orderNumber, String etranReturn, String etranOutbound) {
		OrderEntity order = orderService.findOrderByOrderNumber(orderNumber, false);
		order.setEtranOutbound(etranOutbound);
		order.setEtranReturn(etranReturn);
	}

	public void setWagonEtran(Long wagonId, String etranReturn, String etranOutbound) {
		WagonEntity wagon = getDaoProvider().getWagonEntityDao().findById(wagonId);

		wagon.setEtranOutbound(etranOutbound);
		wagon.setEtranReturn(etranReturn);
	}

	public void transferEtranToWagon(WagonEntity wagon, OrderEntity order) {
		if (wagon == null || order == null) {
			LOG.error("Unable to transfer etran number to wagon");
			return;
		}

		String etranOutbound = order.getEtranOutbound();
		String etranReturn = order.getEtranReturn();

		if (etranReturn != null) {
			order.setEtranReturn(null);
		}

		if (etranOutbound != null) {
			order.setEtranOutbound(null);
		}

		if (StringUtils.notEmpty(etranOutbound)) {
			wagon.setEtranOutbound(etranOutbound);
		}

		if (StringUtils.notEmpty(etranReturn)) {
			wagon.setEtranReturn(etranReturn);
		}
	}

	public void transferEtranToOrder(WagonEntity wagon, OrderEntity order) {
		if (wagon == null || order == null) {
			LOG.error("Unable to transfer etran number to order");
			return;
		}

		String etranOutbound = wagon.getEtranOutbound();
		String etranReturn = wagon.getEtranReturn();

		if (etranReturn != null) {
			wagon.setEtranReturn(null);
		}

		if (etranOutbound != null) {
			wagon.setEtranOutbound(null);
		}

		if (StringUtils.notEmpty(etranOutbound)) {
			order.setEtranOutbound(etranOutbound);
		}

		if (StringUtils.notEmpty(etranReturn)) {
			order.setEtranReturn(etranReturn);
		}
	}

	/**
	 * Set (or removes) the carrier for an order
	 * OrderStatus is set to transmitted to carrier
	 *
	 * @param orderNumber
	 * @param commCarrierId
	 */

	public void setOrderCommCarrier(List orderNumbers, Long commCarrierId) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderEntity.ATTR_ORDER_NUMBER,
				orderNumbers));
		List<OrderEntity> ordersList = orderDao.find(f);

		CarrierEntity commCarrier = null;
		if (commCarrierId != null) {
			ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider().getCarrierEntityDao();
			commCarrier = carrierDao.findById(commCarrierId.longValue());
		}

		for (OrderEntity order : ordersList) {
			setOrderCommCarrier(order, commCarrier);
		}
	}

	public void setOrderCommCarrier(OrderEntity order, CarrierEntity commCarrier) {
		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_SET_COMM_CARRIER);
		event.setOrder(order);
		event.setEventTs(new Date());
		monitoringTablesService.processBizevent(event);

		if (order.getCommCarrier() != null
				&& (commCarrier == null || !order.getCommCarrier().getCarrierId().equals(
				commCarrier.getCarrierId()))) {
			orderService.rejectByCarrier(event);
		}

		orderService.setCommercialCarrier(order, commCarrier);

		bizeventAuditService.persistAuditData(event);
	}

	public void setOrderExecCarrier(List orderNumber, Long execCarrierId) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderEntity.ATTR_ORDER_NUMBER,
				orderNumber));
		List<OrderEntity> ordersList = orderDao.find(f);

		CarrierEntity execCarrier = null;

		if (execCarrierId != null) {
			ReadWriteDao<CarrierEntity> carrierDao = getDaoProvider().getCarrierEntityDao();
			execCarrier = carrierDao.findById(execCarrierId.longValue());
		}

		for (OrderEntity order : ordersList) {
			setOrderExecCarrier(order, execCarrier);
		}
	}

	public void setOrderExecCarrier(OrderEntity order, CarrierEntity execCarrier) {
		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_SET_EXEC_CARRIER);
		event.setOrder(order);
		event.setEventTs(new Date());
		monitoringTablesService.processBizevent(event);

		if (order.getExecCarrier() != null
				&& (execCarrier == null || !order.getExecCarrier().getCarrierId().equals(
				execCarrier.getCarrierId()))) {
			orderService.rejectByCarrier(event);
		}

		orderService.setExecutingCarrier(order, execCarrier);

		bizeventAuditService.persistAuditData(event);
	}

	/**
	 * OrderStatus is set to transmitted to carrier
	 *
	 * @param orderNumber
	 */

	public void transmitToCarrier(Long orderNumber) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
		OrderEntity order = orderDao.findSingle(f);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER);
		event.setOrder(order);
		event.setEventTs(new Date());
		monitoringTablesService.processBizevent(event);

		orderService.transmitToCarrier(event);
	}


	/**
	 * OrderStatus is set to transmitted to pool ack
	 *
	 * @param orderNumber
	 * @param reason optional reason for the operation, used only when carrierId is null (optionel even then)
	 */

	public void rejectByCarrier(Long orderNumber, String reason) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
		OrderEntity order = orderDao.findSingle(f);

		BizeventEntity event = new BizeventEntity();
		if (order.getExecCarrier() != null) {
			event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_EXEC_CARRIER_REJECTS);
		}
		else {
			event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_COMM_CARRIER_REJECTS);
		}

		event.setOrder(order);
		event.setDescription(reason);
		event.setEventTs(new Date());
		monitoringTablesService.processBizevent(event);

		orderService.rejectByCarrier(event);

		bizeventAuditService.persistAuditData(event);
	}

	/**
	 * Resets the carrierDeclineNote of the order.
	 *
	 * @param orderId
	 */

	public void resetCarrierDeclineNote(Long orderId) {
		OrderEntity order = fetchOrder(orderId);
		order.setCarrierDeclineNote(null);
	}

	/**
	 * Sets the order status to accepted by carrier
	 *
	 * @param orderNumber
	 */

	public void carrierAcceptOrder(Long orderNumber) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
		OrderEntity order = orderDao.findSingle(f);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_COMM_CARRIER_ACK);
		event.setOrder(order);
		event.setEventTs(new Date());

		monitoringTablesService.processBizevent(event);
		orderService.carrierAcceptOrder(event);
		bizeventAuditService.persistAuditData(event);
	}

	/**
	 * Set the pool for an order.
	 *
	 * @param orderNumber
	 * @param poolId null, if pool is to be removed
	 */

	public void setOrderPool(List orderNumbers, Long poolId) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderEntity.ATTR_ORDER_NUMBER,
				orderNumbers));
		List<OrderEntity> ordersList = orderDao.find(f);

		for (OrderEntity order : ordersList) {

			//it must be removed, as far the new pool might not be related to the ex-carrier
			if (order.getExecCarrier() != null
					&& order.getOrderStatus().getOrderStatusCd().equals(
							Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK)) {
				rejectByCarrier(order.getOrderNumber(), "pool reset");
			}
			if (order.getOrderStatus().getOrderStatusCd().equals(
					Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK)) {
				rejectByCarrier(order.getOrderNumber(), "pool reset");
			}
			order.setCommCarrier(null);
			order.setExecCarrier(null);
			long status = order.getOrderStatus().getOrderStatusCd().longValue();
			if ((status != Constant.ORDER_STATUS_TRANSMITTED_TO_POOL)
					&& (status != Constant.ORDER_STATUS_NEW)) {

				orderService.removeOrderFromPool(order);

			}
			poolAssignmentService.setOrderPool(order.getOrderNumber(), poolId, null);
		}
	}

	/**
	 * Set the order status to transmitted to pool
	 *
	 * @param orderNumber
	 */

	public void transmitToPool(Long orderNumber) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
		OrderEntity order = orderDao.findSingle(f);

		final BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_POOL);
		event.setOrder(order);
		event.setEventTs(new Date());
		monitoringTablesService.processBizevent(event);

		orderService.transmitToPool(event);
	}

	/**
	 * Sets the order status to new
	 *
	 * @param orderNumber
	 * @parem reason
	 */
	public void rejectByPool(Long orderNumber, String reason) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
		OrderEntity order = orderDao.findSingle(f);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_SET_POOL);
		event.setOrder(order);
		event.setDescription(reason);
		event.setEventTs(new Date());
		monitoringTablesService.processBizevent(event);

		orderService.rejectByPool(event);

		bizeventAuditService.persistAuditData(event);
	}


	/**
	 * Sets the order status to accepted  by pool
	 *
	 * @param orderNumber
	 */

	public void poolAcceptOrder(Long orderNumber) {
		ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
		OrderEntity order = orderDao.findSingle(f);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_POOL_ACK);
		event.setOrder(order);
		event.setEventTs(new Date());

		monitoringTablesService.processBizevent(event);

		orderService.poolAcceptOrder(event);

		bizeventAuditService.persistAuditData(event);
	}


	/**
	 * sets the assignment to inactive and unlinks it from the load, if it already has a load,
	 * moves the order back to carrier acknowledged state and writes the note to the bizevent
	 * if some orders from the assignment are already delivered, the assignment is kept and only
	 * the still uncompleted orders are unlinked from the loads and moved back to
	 * carrier acknowledged state
	 * @orderNumbers List of Strings with order numbers
	 */
	public void cancelAssignment(Long assignmentId, String noteCancel, List orderNumbers, boolean includeRailTransmitted) {
		ReadWriteDao<AssignmentEntity> aed = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = aed.findById(assignmentId);

		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_CANCEL_ASSIGNMENT);
		event.setAssignment(assignment);
		event.setTruck(assignment.getTruck());
		event.setDescription(noteCancel);
		event.setEventTs(new Date());

		//might not persist this, but creates the copy of that for all the specified orders, as the assignment is not cancelled just those from the assignment
		persistCloneBizeventForOrders(event, orderNumbers);

		orderService.removeAssignment(assignment, orderNumbers, includeRailTransmitted);

		if (assignment.getAssignStatus().getAssignStatusCd().equals(
				Constant.ASSIGNMENT_STATUS_CANCELLED)) {
			event = monitoringTablesService.processBizevent(event);
		}
	}

    /**
     * Creates clones from the event and attaches each of them to the orders of the assignment's orders.
     * Requirement is that the assignment is an internal assignment.
     *
     * WARNING: the bizevents created here are used on order log tab for each order to show the events of the inactive assignment for each of the
     * orders. This was necessary as inactive assignments have no reference anymore to the order, as they have
     * no assignment parts, and it would be a waste to store in all cases a complete assignment together with parts just to
     * be able to show on the order log the events related to the prior assignment.
     *
     * @param event
     * @param orderNumbers for null all orders a clone event will be stored
     */
    private void persistCloneBizeventForOrders(BizeventEntity event, List orderNumbers) {
        if (event.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL
        		&& event.getAssignment().getAssignTrStatus().getAssignTrStatusCd().equals(Constant.ASSIGN_STATUS_TR_TRANSMITTED)) {

            for (AssignmentPartEntity part : event.getAssignment().getAssignmentParts()) {

                if ((part.getOrder() != null) &&
                            part.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD &&
                            (orderNumbers == null || (orderNumbers.contains(part.getOrder().getOrderNumber().toString())))) {

                    final OrderEntity order = part.getOrder();

                    BizeventEntity eventClone = null;
                    try {
                        eventClone = (BizeventEntity)BeanUtils.cloneBean(event);
                    }
                    catch (Exception e) {
                        throw new LogonException("Error when splitting assignment event to order events assignment=" + (event.getAssignment() != null ? event.getAssignment().getAssignmentId()  : "null" ) + " order=" + (order != null ? order.getOrderId() : "null"), Reason.RES_ERROR_IMPL);
                    }
                    eventClone.setOrder(order);
                    monitoringTablesService.processBizevent(eventClone);

                    bizeventAuditService.persistAuditData(eventClone);
                }
            }
        }
    }

	/**
	 *
	 * @param tareMeasures orderItemIdString -> tareWeightTString
	 * @param grossMeasures orderItemIdString -> grossWeightTString
	 * @param includeTransmitted
	 */
	public void updateRailLoad(Map<String, String> tareMeasures, Map<String, String> grossMeasures,
			boolean includeTransmitted) {
		if (grossMeasures.isEmpty()) {
			return;
		}

		// orderItemId -> loadingData
		final Map<Long, LoadMeasureData> orderItemMap = new HashMap<Long, LoadMeasureData>();
		for (String orderItemIdStr : grossMeasures.keySet()) {

			final Long orderItemId = Long.valueOf(orderItemIdStr);

			BigDecimal grossWeightT = null;
			BigDecimal tareWeightT = null;

			final String grossWeightTString = grossMeasures.get(orderItemIdStr);
			if (grossWeightTString != null) {
				try {
					grossWeightT = new BigDecimal(grossWeightTString);
				}
				catch (NumberFormatException e) {
					// empty string -> OK
					grossWeightT = null;
				}
			}

			final String tareWeightTString = tareMeasures.get(orderItemIdStr);
			if (tareWeightTString != null) {
				try {
					tareWeightT = new BigDecimal(tareWeightTString);
				}
				catch (NumberFormatException e) {
					tareWeightT = null;
				}
			}

			orderItemMap.put(orderItemId, new LoadMeasureData(tareWeightT, grossWeightT));
		}

		final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
		final Filter<OrderItemEntity> f = dao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				OrderItemEntity.ATTR_ORDER_ITEM_ID, new ArrayList(orderItemMap.keySet())));
		final List<OrderItemEntity> orderItems = dao.find(f);

		final Set<OrderEntity> orders = new HashSet<OrderEntity>();
		final Set<Long> orderIds = new HashSet<Long>();
		for (OrderItemEntity orderItem : orderItems) {
			orders.add(orderItem.getOrder());
			orderIds.add(orderItem.getOrder().getOrderId());
		}

		ConfSeqLock lock = null;
		try {
			lock = sequenceService.acquireLockForOrders(orders);
			dispoService._updateRailLoad(orderItemMap, includeTransmitted, lock);
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
		final List<OrderEntity> updatedOrders = findOrdersByIds(orderIds);
		for (OrderEntity order : updatedOrders) {
			if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
				orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false,
						true);
			}
		}
	}

	/**
	 * Fetches orders by a set of IDs. This method does almost the same as the DAO's
	 * <code>findById()</code> method, but accepts a <em>set</em> of IDs instead.
	 * This is a copy of <code>SimpleVehicleSchedulingServiceBean.findOrdersByIds()</code>
	 * @param orderIds A set of order IDs
	 * @return The set of order entities with the specified IDs, if found.
	 */
	private List<OrderEntity> findOrdersByIds(Set<Long> orderIds) {
		final ReadOnlyDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
		final Filter<OrderEntity> orderFilter = dao.createFilter();

		orderFilter.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderEntity.ATTR_ORDER_ID, new ArrayList(orderIds)));
		final List<OrderEntity> orders = dao.find(orderFilter);
		return orders;
	}

	private List<OrderEntity> findOrdersByAssignmentIds(List<Long> assignmentIds) {
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> f = dao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, assignmentIds));
		f.addExpression(Restrictions.isNotNull(AssignmentPartEntity.ATTR_ORDER_ID));
		final List<AssignmentPartEntity> parts = dao.find(f);

		final List<OrderEntity> orders = new LinkedList<OrderEntity>();
		for (AssignmentPartEntity part : parts) {
			orders.add(part.getOrder());
		}

		return orders;
	}

	/**
	 * Fetches multiple assignment entities at once, saving individual dao calls
	 * @param assignmentIds
	 * @return List of assignment entities
	 */
	private List<AssignmentEntity> findAssignmentsByAssignmentIds(List<Long> assignmentIds) {
		final ReadOnlyDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
		final Filter<AssignmentEntity> f = dao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(AssignmentEntity.ATTR_ASSIGNMENT_ID, assignmentIds));
		final List<AssignmentEntity> assignments = dao.find(f);
		return assignments;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _updateRailLoad(Map<Long, LoadMeasureData> orderItemMap,
			boolean includeTransmitted, ConfSeqLock lock) {

		// loadId -> orderItemId -> loadingData
		final Map<LoadEntity, Map<Long, LoadedQuantityData>> loadMap =
				new HashMap<LoadEntity, Map<Long, LoadedQuantityData>>();

		final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
		final Filter<OrderItemEntity> f = dao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderItemEntity.ATTR_ORDER_ITEM_ID, new ArrayList(orderItemMap.keySet())));
		final List<OrderItemEntity> orderItems = dao.find(f);

		for (OrderItemEntity orderItem : orderItems) {

			// TODO 04-Apr-2013/stich: is this really required? imho already checked in DispoAjaxService
			if (!includeTransmitted &&
					LogonStatusUtils.isBeyondLoaded(orderItem.getOrder().getOrderStatus())) {
				throw new LogonException("Not Authorized !",
						Reason.RES_ERROR_SECURITY_TAMPERING);
			}

			final LoadMeasureData md = orderItemMap.get(orderItem.getOrderItemId());
			final BigDecimal tareWeightT = md.getTareWeightT();
			final BigDecimal grossWeightT = md.getGrossWeightT();

			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {

				if (orderItemMap.size() > 1) {
					throw new LogonException("Only one measure allowed for bulk items",
							Reason.RES_ERROR_IMPL);
				}

				final LoadEntity load = orderItem.getLoad();
				BigDecimal netQuantity = null;
				final UomBsEntity netQuantityUom = load.getNetQuantityUom();

				// TODO 04-Apr-2013/stich: is this conversion really necessary???
				if (tareWeightT != null && grossWeightT != null) {
					final BigDecimal netWeightT = grossWeightT.subtract(tareWeightT);

					final UomBsEntity uomTons = referentialDataService.findBsEntity(
							UomBsEntity.class,
							Constant.UOM_TONS);

					final SapMaterialEntity sapMaterial = load.getSapMaterial();

					try {
						UomConversionResult ucr = unitOfMeasurementConverterService.convert(netWeightT,
								uomTons, netQuantityUom, sapMaterial, null, null, false);

						netQuantity = ucr.getQuantity();
					}
					catch (LogonConversionException e) {
						LOG.warn("Cannot convert net quantity from tons to net quantity UoM "
								+ netQuantityUom.getUomCd() + " (sapMaterialId = "
								+ sapMaterial.getSapMaterialId() + "): " + e.getMessage());
					}
				}

				WagonEntity wagon = load.getWagon();
				LoadingpointEntity lp = getLoadingpoint(wagon);

				/*
				 * LOGON-6570 (Railwaycar will not back in the status Loading or Empty)
				 * In case the wagon was been pre-loaded before, do not create new GMR.
				 */
				// FIXME 04-Apr-2013/stich: what do to with this?
				//final boolean createGoodsMovement = !wagonHasPreloads(wagonId, plantCd, date);

				loadingService.updateLoadAndSendGoodsMovements(load, lp, tareWeightT, grossWeightT,
						netQuantity, netQuantityUom, NO_LOADING_START_TIME, NO_LOADING_END_TIME,
						EventSource.LOGON, lock);
				return;
			}
			else if (LogonPackageHandlingUtil.isBag(orderItem)) {

				final LoadEntity load = orderItem.getLoad();
				final LoadedQuantityData lqd =
						new LoadedQuantityData(grossWeightT, orderItem.getOrderedUom().getUomCd());

				final Map<Long, LoadedQuantityData> itemLoadedQuantityMap;
				if (loadMap.containsKey(load)) {
					itemLoadedQuantityMap = loadMap.get(load);
				}
				else {
					itemLoadedQuantityMap = new HashMap<Long, LoadedQuantityData>();
					loadMap.put(load, itemLoadedQuantityMap);
				}
				itemLoadedQuantityMap.put(orderItem.getOrderItemId(), lqd);
			}
			else if (LogonPackageHandlingUtil.isService(orderItem)) {
				//for service do not do anything with loads as far there are no loads
				continue;
			}
			else {
				throw new LogonException("Can't load item which is neither bulk nor bag",
						Reason.RES_ERROR_IMPL);
			}
		}

		/*
		 * At this point, all items are bag and all loading information is parsed into loadMap
		 */
		for (LoadEntity load : loadMap.keySet()) {

			WagonEntity wagon = load.getWagon();
			LoadingpointEntity loadingpoint = getLoadingpoint(wagon);

			Map<Long, LoadedQuantityData> loadingQuantityDataMap = loadMap.get(load);
			loadingService.updateSharedLoadAndSendGoodsMovements(load, loadingpoint, loadingQuantityDataMap,
					NO_TARE_WEIGHT, NO_GROSS_WEIGHT, NO_LOADING_START_TIME, NO_LOADING_END_TIME, EventSource.LOGON, lock);
		}
	}


	public LoadingpointEntity getLoadingpoint(WagonEntity wagon) {
		if (wagon == null || wagon.getCurrentPlantTrack() == null) {
			return null;
		}

		ReadOnlyDao<TrackLoadingpointEntity> dao = getDaoProvider().getTrackLoadingpointEntityDao();
		Filter<TrackLoadingpointEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(TrackLoadingpointEntity.ATTR_PLANT_TRACK_CD,
				wagon.getCurrentPlantTrack().getPlantTrackCd()));
		TrackLoadingpointEntity trackLoadingpoint = dao.findSingle(f);

		if (trackLoadingpoint != null) {
			return trackLoadingpoint.getLoadingpoint();
		}
		else {
			return null;
		}
	}

	public Map<Long, LoadingpointEntity> findLoadingPointsForPlantTrackCds(Set<Long> plantTrackCds) {
		if (plantTrackCds == null || plantTrackCds.isEmpty()) {
			return Collections.<Long, LoadingpointEntity> emptyMap();
		}

		Map<Long, LoadingpointEntity> loadingPoints = new HashMap<Long, LoadingpointEntity>();

		DaoProvider daoProvider = getDaoProvider();

		ReadWriteDao<TrackLoadingpointEntity> trackLoadingPtDAO = daoProvider.getTrackLoadingpointEntityDao();

		List<Long> plantTrackCdList = new ArrayList<Long>(plantTrackCds);

		Filter<TrackLoadingpointEntity> filter = new FilterImpl<TrackLoadingpointEntity>();
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				TrackLoadingpointEntity.ATTR_PLANT_TRACK_CD, plantTrackCdList));

		List<TrackLoadingpointEntity> trackLoadingPoints = trackLoadingPtDAO.find(filter);
		for (TrackLoadingpointEntity trackLoadingPoint : trackLoadingPoints) {
			LoadingpointEntity loadingPoint = trackLoadingPoint.getLoadingpoint();
			if (loadingPoint != null) {
				PlantTrackBsEntity plantTrack = trackLoadingPoint.getPlantTrack();
				loadingPoints.put(plantTrack.getPlantTrackCd(), loadingPoint);
			}
		}

		return loadingPoints;
	}

	public Map<Long, SapbpRailwayStationEntity> findSapBpRailwayStations(Set<Long> sapBpIds) {
		if (sapBpIds == null || sapBpIds.isEmpty()) {
			return Collections.<Long, SapbpRailwayStationEntity> emptyMap();
		}

		Map<Long, SapbpRailwayStationEntity> stationBySapBpId = new HashMap<Long, SapbpRailwayStationEntity>();

		DaoProvider daoProvider = getDaoProvider();

		ReadOnlyDao<SapbpRailwayStationEntity> stationsDAO = daoProvider.getSapbpRailwayStationEntityDao();

		List<Long> sapBpIdList = new ArrayList<Long>(sapBpIds);

		Filter<SapbpRailwayStationEntity> filter = new FilterImpl<SapbpRailwayStationEntity>();
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				SapbpRailwayStationEntity.ATTR_SAP_BP_ID, sapBpIdList));
		List<SapbpRailwayStationEntity> stations = stationsDAO.find(filter);

		for (SapbpRailwayStationEntity station : stations) {
			stationBySapBpId.put(station.getSapBpId(), station);
		}

		return stationBySapBpId;
	}

	/**
	 * removes external assignment
	 */
	public void removeExteralAssignment(Long assignmentId, String noteCancel) {
		ReadWriteDao<AssignmentEntity> aed = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignment = aed.findById(assignmentId);

		//external assignments only
		if (assignment.getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_EXTERNAL) {
			BizeventEntity event = new BizeventEntity();
			event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_REMOVE_ASSIGNMENT);
			event.setAssignment(assignment);

			event.setTruck(assignment.getTruck());
			event.setDescription(noteCancel);
			event.setEventTs(new Date());

			event = monitoringTablesService.processBizevent(event);
			orderService.removeAssignment(assignment, null, false);
		}
	}

	/**
	 * Saves a new or edited assignment
	 */
	public AssignmentEntity saveAssignment(AssignmentEntity assignment) {
		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_EDIT_ASSIGNMENT);
		event.setTruck(assignment.getTruck());
		event.setEventTs(new Date());

		event = monitoringTablesService.processBizevent(event);

		orderService.saveAssignment(event, assignment);

		return event.getAssignment();
	}

	public void saveAssignmentAndOrders(AssignmentEntity assignment, Map orderStateMap) {
		assignment = saveAssignment(assignment);

		Iterator i = orderStateMap.keySet().iterator();
		while (i.hasNext()) {
			AssignmentPartEntity ap = (AssignmentPartEntity) i.next();
			OrderEntity o = ap.getOrder();

			o = getDaoProvider().getOrderEntityDao().merge(o);
			Long newOrderStatus = (Long) orderStateMap.get(ap);

			long currentStatus = o.getOrderStatus().getOrderStatusCd().longValue();
			if ((newOrderStatus.longValue() == Constant.ORDER_STATUS_DELIVERED)
					|| (newOrderStatus.longValue() == Constant.ORDER_STATUS_FINISHED)) {
				if ((currentStatus != Constant.ORDER_STATUS_DELIVERED)
						&& (currentStatus != Constant.ORDER_STATUS_FINISHED)) {
					ap = getDaoProvider().getAssignmentPartEntityDao().findById(
							ap.getAssignmentPartId());
					setTruckUnloaded(ap, ap.getRealStart());
				}
			}
			else if (currentStatus != newOrderStatus) {
				orderService.changeOrderStatus(o, newOrderStatus);
				assignment.setAssignTrStatus(referentialDataService.findBsEntity(
						AssignTrStatusBsEntity.class, Constant.ASSIGN_STATUS_TR_NOT_TRANSMITTED));
			}
		}
	}

	/**
	 * fetch a list with all plants havin rail shippingpoints
	 */
	public List<RailPlantEntity> fetchAllRailPlants() {
		ReadOnlyDao<RailPlantEntity> rpDao = getDaoProvider().getRailPlantEntityDao();
		Filter filter = new FilterImpl();
		filter.addExpression(Order.asc(RailPlantEntity.ATTR_PLANT_NAME));

		return rpDao.find(filter);
	}

	/**
	 * fetch a list with all rail shippingpoints for a plant
	 */
	public List<RailPlantSpEntity> fetchRailShippingpontsForPlant(String plantCd) {
		ReadOnlyDao<RailPlantSpEntity> rpsDao = getDaoProvider().getRailPlantSpEntityDao();
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(RailPlantSpEntity.ATTR_PLANT_CD, plantCd));
		f.addExpression(Order.asc(RailPlantSpEntity.ATTR_SHIPPINGPOINT_NAME));

		return rpsDao.find(f);
	}

	/**
	 * Fetch the list of rail wagons currently at the station(s) or due to return to the shippingpont(s) on dispodate
	 * regarding to the packagetype(s) and desired wagonNumber
	 *
	 * @param shippingpoints list of shippingpoint ids
	 * @param railwayStations list of railway station ids these should patch the shippingpoints from the other parameter
	 * @param langCd user language
	 * @param dispoDate Date of the dispo
	 * @param wagonNumber the wagonnumber of the desired wagons - using like
	 * @param pkgTypes The set of packagetypes, If it is not empty those wagons will be selected which is in that.
	 *
	 * @return list of DispoWagonListEntity
	 */
	public List<DispoWagonListEntity> fetchWagonsForDispo(List<Long> railwayStations,
			 List<Long> tracks, boolean showNoTrack, String langCd, Date dispoDate, String wagonNumber,
			 TimeZone userTimeZone) {
		ReadOnlyDao<DispoWagonListEntity> dispoWagonListDao = getDaoProvider().getDispoWagonListEntityDao();

		if (railwayStations.isEmpty() || (tracks.isEmpty() && !showNoTrack)) {
			return new LinkedList<DispoWagonListEntity>();
		}
		Filter<DispoWagonListEntity> f = dispoWagonListDao.createFilter();

		Date d1 = userTimeZone.getDayStart(dispoDate);
		Calendar c = userTimeZone.asCalendar(d1);
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date d2 = c.getTime();

		if (!StringUtils.empty(wagonNumber)) {
			StringBuilder wagonNumberExpVal = new StringBuilder();
			wagonNumberExpVal.append("%").append(wagonNumber).append("%");
			f.addExpression(Restrictions.like(DispoWagonListEntity.ATTR_WAGON_NO,
					wagonNumberExpVal.toString()));
		}

		f.addExpression(Restrictions.eq(DispoWagonListEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		f.addExpression(Restrictions.eq(DispoWagonListEntity.ATTR_LANG_CD, langCd));
		f.addExpression(Restrictions.or(Restrictions.and(Restrictions.le(
				DispoWagonListEntity.ATTR_ARRIVAL_LAST_KNOWN_STATION_DAT, d2),
				DbUtils.createSplittedInRestrictionOfIds(
						DispoWagonListEntity.ATTR_LAST_KNOWN_RAILWAY_STATION_ID, railwayStations)),
				Restrictions.and(DbUtils.createSplittedInRestrictionOfIds(
						DispoWagonListEntity.ATTR_RETURN_RAILWAY_STATION_ID, railwayStations),
						Restrictions.le(DispoWagonListEntity.ATTR_PLANNED_START, d2))));
		if (showNoTrack) {
			if (tracks.isEmpty()) {
				f.addExpression(Restrictions.isNull(DispoWagonListEntity.ATTR_CURRENT_PLANT_TRACK_CD));
			}
			else {
				f.addExpression(Restrictions.or(DbUtils.createSplittedInRestrictionOfIds(
						DispoWagonListEntity.ATTR_CURRENT_PLANT_TRACK_CD, tracks),
						Restrictions.isNull(DispoWagonListEntity.ATTR_CURRENT_PLANT_TRACK_CD)));
			}
		}
		else {
			f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					DispoWagonListEntity.ATTR_CURRENT_PLANT_TRACK_CD, tracks));
		}
		return dispoWagonListDao.find(f);
	}

	/**
	 * Fetch the DispoWagonListEntity for given wagonId and language
	 * @param wagonId list
	 * @param langCd user language
	 * @return list of DispoWagonListEntity
	 */
	public DispoWagonListEntity fetchWagonForDispo(Long wagonId, String langCd) {
		Filter f = new FilterImpl();

		f.addExpression(Restrictions.eq(DispoWagonListEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		f.addExpression(Restrictions.eq(DispoWagonListEntity.ATTR_LANG_CD, langCd));
		f.addExpression(Restrictions.eq(DispoWagonListEntity.ATTR_WAGON_ID, wagonId));

		return getDaoProvider().getDispoWagonListEntityDao().findSingle(f);
	}

	public WagonEntity fetchWagon(Long wagonId) {
		return getDaoProvider().getWagonEntityDao().findById(wagonId);
	}

	public List fetchContainersOfWagon(Long wagonId) {
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_WAGON_ID, wagonId));

		return getDaoProvider().getContainerEntityDao().find(f);
	}

	public List<LdpointMatRestrEntity> fetchLdPointMatRestrs(Long loadingPointId) {
		ReadOnlyDao<LdpointMatRestrEntity> dao = getDaoProvider().getLdpointMatRestrEntityDao();
		Filter f = dao.createFilter();
		f.addExpression(Restrictions.eq(LdpointMatRestrEntity.ATTR_LOADINGPOINT_ID, loadingPointId));
		return dao.find(f);
	}

	public List fetchLoadsOfContainers(List containerIds) {
		if (containerIds.isEmpty()) {
			return new LinkedList();
		}
		Filter f = new FilterImpl();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(LoadEntity.ATTR_CONTAINER_ID,
				containerIds));
		f.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));

		return getDaoProvider().getLoadEntityDao().find(f);
	}

	public List fetchOrderItemsOfLoads(List loadIds) {
		if (loadIds.isEmpty()) {
			return new LinkedList();
		}
		Filter f = new FilterImpl();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderItemEntity.ATTR_LOAD_ID,
				loadIds));

		return getDaoProvider().getOrderItemEntityDao().find(f);
	}

	public void changeWagonComposition(WagonEntity wagon, List remove, List addNos) {
		Iterator i = remove.iterator();
		while (i.hasNext()) {
			ContainerEntity c = (ContainerEntity) i.next();
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(LoadEntity.ATTR_CONTAINER_ID, c.getContainerId()));
			f.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			LoadEntity l = getDaoProvider().getLoadEntityDao().findSingle(f);
			c.setCurrentWagon(null);
			if (l != null) {
				if (l.getLoadingTs() == null) {
					getDaoProvider().getLoadEntityDao().remove(l);
				}
				else {
					l.setWagon(null);
				}
			}
			getDaoProvider().getContainerEntityDao().merge(c);
		}
		i = addNos.iterator();
		while (i.hasNext()) {
			String cNo = (String) i.next();
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(ContainerEntity.ATTR_CONTAINER_NO, cNo));
			ContainerEntity c = getDaoProvider().getContainerEntityDao().findSingle(f);
			if (c != null) {
				c.setCurrentWagon(wagon);

				f = new FilterImpl();
				f.addExpression(Restrictions.eq(LoadEntity.ATTR_CONTAINER_ID, c.getContainerId()));
				f.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
				LoadEntity l = getDaoProvider().getLoadEntityDao().findSingle(f);
				if (l != null) {
					l.setWagon(wagon);
				}
			}
		}
	}

	public List<DispoWContainerListEntity> fetchContainers(Integer start, Integer count, String query, String lang) {
		/* See DispokarteService for comments
		 * TODO (cosmin 21.1.09): Write a utility function ti do these replacements
		 */
		query = query.replace("|", "||");
		query = query.replace("%", "|%");
		query = query.replace("_", "|_");
		query = query.replace('*', '%');
		query = query += '%';

		final ReadOnlyDao<DispoWContainerListEntity> dao = getDaoProvider().getDispoWContainerListEntityDao();
		final Filter<DispoWContainerListEntity> f = new FilterImpl();
		f.addExpression(Restrictions.ilike(DispoWContainerListEntity.ATTR_CONTAINER_NO, query));
		f.addExpression(Restrictions.eq(DispoWContainerListEntity.ATTR_LANG_CD, lang));

		return dao.findNRecs(start, count, f);
	}

	public long countContainers(String query, String lang) {
		/* See DispokarteService for comments
		 * TODO (cosmin 21.1.09): Write a utility function ti do these replacements
		 */
		query = query.replace("|", "||");
		query = query.replace("%", "|%");
		query = query.replace("_", "|_");
		query = query.replace('*', '%');
		query = query += '%';
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.ilike(DispoWContainerListEntity.ATTR_CONTAINER_NO, query));
		f.addExpression(Restrictions.eq(DispoWContainerListEntity.ATTR_LANG_CD, lang));
		return getDaoProvider().getDispoWContainerListEntityDao().count(f);
	}

	/**
	 * Returns a track list for rail scheduling
	 */
	public List<DispoPlantTrackListEntity> fetchRailSchedulingTrackList(String plantCode, String language) {
		final ReadOnlyDao<DispoPlantTrackListEntity> dao = getDaoProvider().getDispoPlantTrackListEntityDao();
		final Filter<DispoPlantTrackListEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(DispoPlantTrackListEntity.ATTR_PLANT_CD, plantCode));
		filter.addExpression(Restrictions.eq(DispoPlantTrackListEntity.ATTR_LANG_CD, language));
		return dao.find(filter);
	}

	public DispoMaxWagonPosEntity fetchMaxTrackPos(String wagonNo)  {
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(DispoMaxWagonPosEntity.ATTR_WAGON_NO, wagonNo));
		return getDaoProvider().getDispoMaxWagonPosEntityDao().findSingle(f);
	}

	public void setWagonTrackPos(String[] wagonNo, Long pos){
		final ReadOnlyDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
		final Filter<WagonEntity> filter = dao.createFilter();
		if ((wagonNo != null) && (wagonNo.length > 0)) {
			filter.addExpression(DbUtils.<String> createSplittedInRestriction(
					WagonEntity.ATTR_WAGON_NO, Arrays.asList(wagonNo)));
		}
		filter.addExpression(Order.asc(WagonEntity.ATTR_CURRENT_TRACK_POSITION_NO));

		final List<WagonEntity> wl = dao.find(filter);
		if (wl.isEmpty()) {
			return;
		}
		PlantTrackBsEntity firstPT = ((WagonEntity) wl.get(0)).getCurrentPlantTrack();
		if (firstPT == null) {
			return;
		}
		long cnt = 0;
		for (WagonEntity wagon : wl) {
			if ((wagon.getCurrentPlantTrack() == null)
					|| !wagon.getCurrentPlantTrack().getPlantTrackCd().equals(
							firstPT.getPlantTrackCd())) {
				continue;
			}
			wagon.setCurrentTrackPositionNo(null);
			cnt++;
		}
		getDaoProvider().getDirectJpaDao().flush();
		long maxPos = reassignWagonPositions(firstPT.getPlantTrackCd(), pos, cnt);
		if (pos == null) {
			return;
		}
		if (pos.longValue() > maxPos) {
			pos = new Long(maxPos);
		}

		for (WagonEntity wagon : wl) {
			if ((wagon.getCurrentPlantTrack() == null)
					|| !wagon.getCurrentPlantTrack().getPlantTrackCd().equals(
							firstPT.getPlantTrackCd())) {
				continue;
			}
			wagon.setCurrentTrackPositionNo(pos);
			pos = new Long(pos.longValue() + 1);
		}
	}

	public void setWagonTrack(String[] wagonNo, Long trackCd){
		long targetTrackPos = -1;
		PlantTrackBsEntity targetPlantTrack = null;
		if (trackCd != null) {
			targetPlantTrack = referentialDataService.findBsEntity(PlantTrackBsEntity.class,
					trackCd);
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(WagonEntity.ATTR_CURRENT_PLANT_TRACK_CD,
					targetPlantTrack.getPlantTrackCd()));
			f.addExpression(Order.desc(WagonEntity.ATTR_CURRENT_TRACK_POSITION_NO));
			List res = getDaoProvider().getWagonEntityDao().find(f);
			if (res.isEmpty()) {
				targetTrackPos = 1;
			}
			else {
				WagonEntity fw = (WagonEntity) res.get(0);
				if (fw.getCurrentTrackPositionNo() == null) {
					targetTrackPos = 1;
				}
				else {
					targetTrackPos = fw.getCurrentTrackPositionNo().longValue() + 1;
				}
			}

		}

		final ReadOnlyDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
		final Filter<WagonEntity> filter = dao.createFilter();
		if ((wagonNo != null) && (wagonNo.length > 0)) {
			filter.addExpression(DbUtils.<String> createSplittedInRestriction(
					WagonEntity.ATTR_WAGON_NO, Arrays.asList(wagonNo)));
		}
		filter.addExpression(Order.asc(WagonEntity.ATTR_CURRENT_PLANT_TRACK_CD));
		filter.addExpression(Order.asc(WagonEntity.ATTR_CURRENT_TRACK_POSITION_NO));

		final Collection<Long> fromTracks = new HashSet();
		final List<WagonEntity> wagons = dao.find(filter);
		for (WagonEntity wagon : wagons) {
			if (wagon.getCurrentPlantTrack() != null) {
				fromTracks.add(wagon.getCurrentPlantTrack().getPlantTrackCd());
			}
			wagon.setCurrentPlantTrack(targetPlantTrack);
			if ((wagon.getCurrentTrackPositionNo() != null) && (targetTrackPos > 0)) {
				wagon.setCurrentTrackPositionNo(targetTrackPos);
				targetTrackPos++;
			}
			else {
				wagon.setCurrentTrackPositionNo(null);
			}
		}
		getDaoProvider().getDirectJpaDao().flush(); // flush to enable accurate query in reassignWagonPositions
		for (Long plantTrackCd : fromTracks) {
			reassignWagonPositions(plantTrackCd, null, 1);
		}
	}

	private long reassignWagonPositions(Long currentPlantTrackCd, Long firstSparePos, long nrSparePos) {
		Filter f = new FilterImpl();
		f.addExpression(Restrictions.eq(WagonEntity.ATTR_CURRENT_PLANT_TRACK_CD,
				currentPlantTrackCd));
		f.addExpression(Restrictions.isNotNull(WagonEntity.ATTR_CURRENT_TRACK_POSITION_NO));
		f.addExpression(Order.asc(WagonEntity.ATTR_CURRENT_TRACK_POSITION_NO));
		Iterator i = getDaoProvider().getWagonEntityDao().find(f).iterator();
		long position = 1;
		while (i.hasNext()) {
			WagonEntity wagon = (WagonEntity) i.next();
			while ((firstSparePos != null) && (firstSparePos.longValue() <= position)
					&& ((firstSparePos.longValue() + nrSparePos) > position)) {
				position++;
			}
			wagon.setCurrentTrackPositionNo(position);
			position++;
		}
		return position;
	}


	/**
	 * Retrieves a list of DispoLoadtargetPrioWrapper, containing for each DispoLoadtargetEntity
	 * record the count of Wagons/Containers still to be loaded for the given date.
	 * the list is ordered by descending count.
	 */
	public Collection<DispoLoadtargetPrioWrapper> fetchPossiblePreloads(String plantCd,
			String langCd, Date date) {
		ReadOnlyDao<DispoLoadtargetEntity> dispoLoadtargetDao = getDaoProvider().getDispoLoadtargetEntityDao();
		Filter<DispoLoadtargetEntity> dltFilter = dispoLoadtargetDao.createFilter();
		dltFilter.addExpression(Restrictions.eq(DispoLoadtargetEntity.ATTR_PLANT_CD, plantCd));
		dltFilter.addExpression(Restrictions.eq(DispoLoadtargetEntity.ATTR_LANG_CD, langCd));

		List<DispoLoadtargetEntity> dispoLoadtargets = dispoLoadtargetDao.find(dltFilter);
		ShippingpointLoadtargetManager m = new ShippingpointLoadtargetManager(dispoLoadtargets);

		date = DateTimeUtils.cleanTimeWithDefaultTimeZone(date);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);

		ReadOnlyDao<DispoDayLoadsEntity> dispoDayLoadsDao = getDaoProvider().getDispoDayLoadsEntityDao();
		Filter<DispoDayLoadsEntity> ddlFilter = dispoDayLoadsDao.createFilter();
		ddlFilter.addExpression(Restrictions.ge(DispoDayLoadsEntity.ATTR_LOADING_TS, date));
		ddlFilter.addExpression(Restrictions.lt(DispoDayLoadsEntity.ATTR_LOADING_TS, c.getTime()));
		ddlFilter.addExpression(Restrictions.isNotNull(DispoDayLoadsEntity.ATTR_NET_QUANTITY));
		ddlFilter.addExpression(Restrictions.or(Restrictions.eq(
				DispoDayLoadsEntity.ATTR_ASSIGNMENT_PLANT_CD, plantCd), Restrictions.and(
				Restrictions.isNull(DispoDayLoadsEntity.ATTR_ASSIGNMENT_PLANT_CD),
				Restrictions.eq(DispoDayLoadsEntity.ATTR_WAGON_PLANT_CD, plantCd))));
		List<DispoDayLoadsEntity> dispoDayLoads = dispoDayLoadsDao.find(ddlFilter);
		m.removeLoaded(dispoDayLoads);

		return m.getTargets();
	}


	private WagonWrapper wrapWagon(Long wagonId, Locale locale) {
		// this internal method is used by methods required for parallel rail
		// and as far as i can tell, the date is not sent back in any stringified
		// form. so we can live with the default timezone.
		TimeZone timeZone = timeZoneService.createDefault(locale);

		WagonWrapper result = new WagonWrapper(fetchWagonForDispo(wagonId, locale.getLanguage()),
				false, unitOfMeasurementConverterService, timeZone);

		if (result == null) {
			return null;
		}

		LinkedList wlist = new LinkedList();
		wlist.add(wagonId);
		List cList = fetchRailContainersForDispo(wlist, locale.getLanguage());
		HashMap cMap = new HashMap();
		Iterator i = cList.iterator();
		while (i.hasNext()) {
			DispoWContainerListEntity dwcl = (DispoWContainerListEntity) i.next();
			ContainerWrapper cw = new ContainerWrapper(dwcl, unitOfMeasurementConverterService,
					timeZone);
			result.addContainer(cw);
			cMap.put(dwcl.getContainerId(), cw);
		}
		return result;

	}

	/**
	 * Retrieves a list of DispoLoadtargetPrioWrapper, containing for each DispoLoadtargetEntity
	 * record the count of Wagons/Containers still to be loaded for the given date. The list is
	 * filtered for the possible loads on the given wagonId
	 * The list is ordered by descending count.
	 */
	public Collection<DispoLoadtargetPrioWrapper> fetchPossiblePreloads(Long wagonId,
			String plantCd, Locale locale, Date date, double tareWeightT) {
		WagonWrapper ww = wrapWagon(wagonId, locale);

		if (ww == null) {
			return null;
		}

		List<Long> mcList = new ArrayList<Long>(1);
		if (ww.getWagon().getMatCompatId() != null) {
			mcList.add(ww.getWagon().getMatCompatId());
		}
		List<MatCompatMatEntity> matCompatMatList = fetchCompatibleMaterials(mcList);
		Set<Long> cmpList = new HashSet<Long>();
		for (MatCompatMatEntity mcm : matCompatMatList) {
			cmpList.add(mcm.getToSapMaterial().getSapMaterialId());
		}
		ww.setMatCompatSet(cmpList);
		ww.setTareWeightT(tareWeightT);

		Collection<DispoLoadtargetPrioWrapper> poss = fetchPossiblePreloads(plantCd,
				locale.getLanguage(), date);

		ww.setPossiblePreloads(poss);

		return ww.getPossiblePreloads();
	}

	/**
	 * returns a list of OrderPrioWrapper describing the orders that could be loaded on the given wagon,
	 * on the given day at the given plant (the method does not check that te wagon is actually at the
	 * plant, the plant is uses to retrieve the possible orders)
	 * The list is ordered by ascending prio
	 */
	public Collection fetchPossibleOrdersToLoad(Long wagonId, String plantCd, Date date,
			Locale locale, double tareWeightT){
		WagonWrapper ww = wrapWagon(wagonId, locale);

		if (ww == null) {
			return null;
		}

		WagonEntity wagon = getDaoProvider().getWagonEntityDao().findById(wagonId);

		LinkedList<Long> wlist = new LinkedList();
		wlist.add(wagonId);

		List aList = fetchRailAssignmentsForDispo(date, wlist, null,
				wagon.getLastKnownRailwayStation().getRailwayStationId());
		if (!aList.isEmpty()) {
			return null;
		}

		List railSpList = fetchRailShippingpontsForPlant(plantCd);
		List spList = new ArrayList();
		for (Iterator it = railSpList.iterator(); it.hasNext();) {
			RailPlantSpEntity railPlantSp = (RailPlantSpEntity) it.next();
			spList.add(railPlantSp.getShippingpointId());
		}

		List<DispoOrderListEntity> railOrders = fetchRailOrdersForDispo(spList, locale, null);

		TimeZone tz = timeZoneService.createDefault(locale);

		Iterator i = railOrders.iterator();
		HashMap oMap = new HashMap();
		HashMap sMap = new HashMap();
		List orders = new LinkedList();
		while (i.hasNext()) {
			DispoOrderListEntity dol = (DispoOrderListEntity) i.next();
			List wtlist = (List) sMap.get(dol.getShiptoSapBpId());
			if (wtlist == null) {
				wtlist = new LinkedList();
				sMap.put(dol.getShiptoSapBpId(), wtlist);
			}
			OrderWrapper ow = new OrderWrapper(dol, wtlist, tz);
			orders.add(ow);
			oMap.put(dol.getOrderId(), ow);
		}
		List iList = fetchOrderItemsForDispo(new ArrayList(oMap.keySet()), locale.getLanguage());
		i = iList.iterator();
		while (i.hasNext()) {
			DispoOrderItemListEntity doil = (DispoOrderItemListEntity) i.next();
			OrderWrapper ow = (OrderWrapper) oMap.get(doil.getOrderId());

			ow.addItem(doil);

		}
		List wagonList = fetchWagonTypesForDispo(new ArrayList(sMap.keySet()), locale);
		i = wagonList.iterator();
		while (i.hasNext()) {
			DispoWagonTypeListEntity dwtl = (DispoWagonTypeListEntity) i.next();
			List wl = (List) sMap.get(dwtl.getShiptoSapBpId());
			if (wl != null) {
				wl.add(dwtl);
			}
		}

		ArrayList mcList = new ArrayList(1);
		if (ww.getWagon().getMatCompatId() != null) {
			mcList.add(ww.getWagon().getMatCompatId());
		}
		List matCompatMatList = fetchCompatibleMaterials(mcList);
		Iterator matCompat = matCompatMatList.iterator();
		Set cmpList = new HashSet();
		while (matCompat.hasNext()) {
			MatCompatMatEntity mcm = (MatCompatMatEntity) matCompat.next();
			cmpList.add(mcm.getToSapMaterial().getSapMaterialId());
		}
		ww.setMatCompatSet(cmpList);
		ww.setTareWeightT(tareWeightT);

		TimeZone appTimeZone = TimeZone.getAppTimeZone();
		RailLoadUtils.computePossibleOrdersToLoad(ww, orders, date, appTimeZone);
		return ww.getPossibleOrders();
	}


	public boolean checkWagonAssignmentMat(Long wagonId, Locale locale, double tareWeightT) {
		TimeZone timeZone = timeZoneService.createDefault(locale);

		WagonWrapper ww = wrapWagon(wagonId, locale);

		if (ww == null) {
			return false;
		}

		WagonEntity wagon = getDaoProvider().getWagonEntityDao().findById(wagonId);

		List<Long> wlist = new LinkedList();
		wlist.add(wagonId);

		List<DispoRailAssListEntity> aList = fetchRailAssignmentsForDispo(null, wlist, null,
				wagon.getLastKnownRailwayStation().getRailwayStationId());
		if (aList.size() != 1) {
			return false;
		}

		RailAssignmentWrapper raw = new RailAssignmentWrapper(aList.get(0), timeZone);

		HashMap<Long, RailAssignmentWrapper> oMap = new HashMap<Long, RailAssignmentWrapper>();
		ArrayList<Long> aIdList = new ArrayList<Long>(1);
		aIdList.add(raw.getAssignment().getAssignmentId());
		List<DispoAssignPartListEntity> apList = fetchAssignmentPartsForDispo(aIdList,
				locale.getLanguage());
		for (DispoAssignPartListEntity dapl : apList) {
			raw.addPart(dapl);
			oMap.put(dapl.getOrderId(), raw);
		}

		List<DispoOrderItemListEntity> oiList = fetchOrderItemsForDispo(
				new ArrayList(oMap.keySet()), locale.getLanguage());
		for (DispoOrderItemListEntity doil : oiList) {
			raw.addItem(doil);
		}

		ArrayList<Long> mcList = new ArrayList(1);
		if (ww.getWagon().getMatCompatId() != null) {
			mcList.add(ww.getWagon().getMatCompatId());
		}
		List<MatCompatMatEntity> matCompatMatList = fetchCompatibleMaterials(mcList);
		Set<Long> cmpList = new HashSet();
		for (MatCompatMatEntity mcm : matCompatMatList) {
			cmpList.add(mcm.getToSapMaterial().getSapMaterialId());
		}
		ww.setMatCompatSet(cmpList);
		ww.setTareWeightT(tareWeightT);
		ww.setAssignment(raw);
		ww.initPossibleMaterialList();

		return !ww.getPossibleMaterialList().isEmpty();
	}

	/**
	 * fetch the MatCompatMat records for a list of matCompatIds
	 * @param matCompatIds list of matCompatIds
	 * @return a list with all MatCompatMatEntity records for the given  matCompatIds
	 */
	public List<MatCompatMatEntity> fetchCompatibleMaterials(List<Long> matCompatIds) {
		if ((matCompatIds == null) || matCompatIds.isEmpty()) {
			return new LinkedList<MatCompatMatEntity>();
		}
		ReadWriteDao<MatCompatMatEntity> matCompatMatDao = getDaoProvider().getMatCompatMatEntityDao();
		Filter<MatCompatMatEntity> f = matCompatMatDao.createFilter();
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				MatCompatMatEntity.ATTR_FROM_MAT_COMPAT_ID, matCompatIds));

		return matCompatMatDao.find(f);
	}

	/**
	 * fetch the list of DispoWagonLoad records for a given wagon and assignment
	 * @param wagonId id of the wagon
	 * @param assignmentId assignmentId or null, if the wagon has no current assignment
	 * @return a list with all DispoWagonLoad records for the given wagonId and having either
	 * 			the given assignmentId or no assignemnt at all if iassignmentId = null.
	 * 			ATTENTION: returns null if no records are found
	 */
	public List<DispoWagonLoadListEntity> fetchWagonLoads(Long wagonId, Long assignmentId,
			String langCd) {
		ReadOnlyDao<DispoWagonLoadListEntity> dispoWagonLoadListDao = getDaoProvider().getDispoWagonLoadListEntityDao();
		Filter<DispoWagonLoadListEntity> f = dispoWagonLoadListDao.createFilter();
		f.addExpression(Restrictions.eq(DispoWagonLoadListEntity.ATTR_WAGON_ID, wagonId));
		f.addExpression(Restrictions.eq(DispoWagonLoadListEntity.ATTR_LANG_CD, langCd));
		if (assignmentId == null) {
			f.addExpression(Restrictions.isNull(DispoWagonLoadListEntity.ATTR_ASSIGNMENT_ID));
		}
		else {
			f.addExpression(Restrictions.eq(DispoWagonLoadListEntity.ATTR_ASSIGNMENT_ID,
					assignmentId));
		}
		List<DispoWagonLoadListEntity> result = dispoWagonLoadListDao.find(f);
		if (result.isEmpty()) {
			result = null;
		}
		return result;
	}

	/**
	 * Fetch a list with all plants.
	 *
	 * TODO: additional filtering, eg. only list plants used that day, or restrict it to visible orders.
	 *
	 * @return a list with all plants
	 */
	public List fetchDeliveringPlants() {
		Filter<PlantBsEntity> f = new FilterImpl<PlantBsEntity>();
		f.addExpression(Order.asc(PlantBsEntity.ATTR_PLANT_NAME));

		return referentialDataService.findBsEntityFiltered(PlantBsEntity.class, f);
	}

	/**
	 * Fetches trucks specified in the truck id list. WARNING: authorization will not be applied here!
	 * @param language
	 *
	 */
	public List fetchNotAuthorizedTrucksForDispo(Long poolId, List truckIds, int filterForTruck, String language) {
		Authorizer.disableNextAccessRestriction();
		ReadOnlyDao<DispoTruckListV2Entity> olDao = getDaoProvider().getDispoTruckListV2EntityDao();
		Filter f = new FilterImpl();

		f.addExpression(Restrictions.eq(DispoTruckListV2Entity.ATTR_LANG_CD, language));
		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				DispoTruckListV2Entity.ATTR_TRUCK_ID, truckIds));

		f.addExpression(Restrictions.eq(DispoTruckListV2Entity.ATTR_POOL_ID, poolId));

		// Filter by Truck (all/real/planning)
		switch (filterForTruck) {
		case Constant.FILTER_FOR_TRUCK_REAL_ONLY:
			//what is not planning is real
			f.addExpression(Restrictions.neq(DispoTruckListV2Entity.ATTR_SAP_VEHICLE_CHAR_CD,
					Constant.VEHICLE_CHARACTERISTIC_PLANNING));
			break;
		case Constant.FILTER_FOR_TRUCK_PLANNING_ONLY:
			f.addExpression(Restrictions.eq(DispoTruckListV2Entity.ATTR_SAP_VEHICLE_CHAR_CD,
					Constant.VEHICLE_CHARACTERISTIC_PLANNING));
			break;
		default:
			break;
		}

		addOrderByToTruckFilter(f);
		return olDao.find(f);
	}


	/**
	 * Returns the standard ordering of trucks on scheduling screen.
	 *
	 * @param f
	 */
	private void addOrderByToTruckFilter(Filter<DispoTruckListV2Entity> f) {
		f.addExpression(Order.asc(DispoTruckListV2Entity.ATTR_CARRIER_DISPLAY_NAME));
		f.addExpression(Order.asc(DispoTruckListV2Entity.ATTR_PRIORITY));
		f.addExpression(Order.asc(DispoTruckListV2Entity.ATTR_TRUCK_NAME));
		f.addExpression(Order.asc(DispoTruckListV2Entity.ATTR_TRUCK_ID));
	}

	public Map fetchCustorderEntities(List custorderIds, String langCd) {
		Authorizer.disableNextAccessRestriction();
		Map<Long, CustorderViewEntity> map = new HashMap<Long, CustorderViewEntity>();
		if (!custorderIds.isEmpty()) {
			ReadOnlyDao<CustorderViewEntity> custorderViewEntityDao = getDaoProvider().getCustorderViewEntityDao();
			Filter filter = new FilterImpl<CustorderViewEntity>();
			filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					CustorderViewEntity.ATTR_CUSTORDER_ID, custorderIds));
			filter.addExpression(Restrictions.eq(CustorderViewEntity.ATTR_LANG_CD, langCd));
			List<CustorderViewEntity> list = custorderViewEntityDao.find(filter);
			for (CustorderViewEntity c : list) {
				map.put(c.getCustorderId(), c);
			}
		}
		return map;
	}

	/**
	 * Returns calculated delivery values per customer order and material.
	 *
	 * @param custorderIds
	 * @return
	 */
	public Map<Long, List<CustorderItemSumQtyEntity>> fetchCustorderItemSumQtyEntities(
			List custorderIds, String langCd) {
		Map<Long, List<CustorderItemSumQtyEntity>> map = new HashMap<Long, List<CustorderItemSumQtyEntity>>();

		if (!custorderIds.isEmpty()) {
			ReadOnlyDao<CustorderItemSumQtyEntity> custorderItemSumQtyEntityDao = getDaoProvider().getCustorderItemSumQtyEntityDao();
			Filter<CustorderItemSumQtyEntity> filter = custorderItemSumQtyEntityDao.createFilter();
			filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					CustorderItemSumQtyEntity.ATTR_CUSTORDER_ID, custorderIds));
			filter.addExpression(Restrictions.eq(CustorderItemSumQtyEntity.ATTR_LANG_CD, langCd));
			List<CustorderItemSumQtyEntity> list = custorderItemSumQtyEntityDao.find(filter);

			for (CustorderItemSumQtyEntity c : list) {
				Long custorderId = c.getCustorderId();
				List<CustorderItemSumQtyEntity> custordItemSQList = map.get(custorderId);

				if (custordItemSQList == null) {
					custordItemSQList = new ArrayList<CustorderItemSumQtyEntity>();
					map.put(custorderId, custordItemSQList);
				}

				custordItemSQList.add(c);
			}
		}

		return map;
	}

	/**
	 * Fetches the list of OrderItemOrderedQtyWrapper for a given Single Delivery Order number.
	 * @param deliveryOrderNr - the Single Delivery Order number
	 * @param langCd
	 */
	public List<OrderItemOrderedQtyWrapper> fetchOrderItemOrderedQtyWrappers(
			Long deliveryOrderNr, String langCd) {
		/*
		 * TODO 30-Nov-2012/paksyd: We should do joins on the DB side. This should be fixed
		 * by possibly introducing a new view (eg. one that is a view on another view).
		 */
		List<OrderItemOrderedQtyWrapper> orderItemOrderedQtyWrapperList = new ArrayList<OrderItemOrderedQtyWrapper>();

		ReadOnlyDao<OrderItemOrderedQtyEntity> orderItemOrderedQtyEntityDao = getDaoProvider().getOrderItemOrderedQtyEntityDao();
		Filter<OrderItemOrderedQtyEntity> filter = orderItemOrderedQtyEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(OrderItemOrderedQtyEntity.ATTR_ORDER_NUMBER,
				deliveryOrderNr));
		filter.addExpression(Restrictions.eq(OrderItemOrderedQtyEntity.ATTR_LANG_CD, langCd));
		List<OrderItemOrderedQtyEntity> orderItemOrderedQtyEntityList = orderItemOrderedQtyEntityDao.find(filter);

		if (orderItemOrderedQtyEntityList != null && !orderItemOrderedQtyEntityList.isEmpty()) {
			List<Long> materialIds = new ArrayList<Long>();

			for (OrderItemOrderedQtyEntity qtyEntity : orderItemOrderedQtyEntityList) {
				materialIds.add(qtyEntity.getSapMaterialId());
			}

			List<SapMaterialDescEntity> sapMaterialDescList = fetchSapMaterialDescEntities(
					materialIds, langCd);

			for (OrderItemOrderedQtyEntity cisqe : orderItemOrderedQtyEntityList) {
				for (SapMaterialDescEntity sapMaterialDescEntity : sapMaterialDescList) {
					if (cisqe.getSapMaterialId().equals(sapMaterialDescEntity.getSapMaterialId())) {
						orderItemOrderedQtyWrapperList.add(new OrderItemOrderedQtyWrapper(cisqe,
								sapMaterialDescEntity.getTxt()));
						break;
					}
				}
			}
		}

		return orderItemOrderedQtyWrapperList;
	}

	public List fetchCustorderItemSumQtyEntities(Long custorderNr, String langCd) {
		List<CustorderItemSumQtyWrapper> custorderItemSumQtyWrapperList = new ArrayList<CustorderItemSumQtyWrapper>();

		ReadOnlyDao<CustordItemSumQtyExtEntity> custorderItemSumQtyEntityDao = getDaoProvider().getCustordItemSumQtyExtEntityDao();
		Filter<CustordItemSumQtyExtEntity> filter = custorderItemSumQtyEntityDao.createFilter();
		filter.addExpression(Restrictions.eq(CustordItemSumQtyExtEntity.ATTR_CUSTORDER_NUMBER,
				custorderNr));
		filter.addExpression(Restrictions.eq(CustordItemSumQtyExtEntity.ATTR_LANG_CD, langCd));
		List<CustordItemSumQtyExtEntity> custorderItemSumQtyEntityList = custorderItemSumQtyEntityDao.find(filter);

		if (custorderItemSumQtyEntityList.size() > 0) {

			for (CustordItemSumQtyExtEntity cisqe : custorderItemSumQtyEntityList) {

				custorderItemSumQtyWrapperList.add(new CustorderItemSumQtyWrapper(cisqe,
						cisqe.getMaterialTxt()));
			}
		}

		return custorderItemSumQtyWrapperList;
	}

	/**
	 * Fetches a list of SapMaterialDescEntity for the given material IDs.
	 * @param materialIds
	 * @param langCd
	 * @return a list of SapMaterialDescEntity
	 */
	public List<SapMaterialDescEntity> fetchSapMaterialDescEntities(List<Long> materialIds,
			String langCd) {
		List<SapMaterialDescEntity> sapMaterialDescList = new ArrayList<SapMaterialDescEntity>();

		ReadOnlyDao<SapMaterialDescEntity> sapMaterialDescDao = getDaoProvider().getSapMaterialDescEntityDao();
		Filter<SapMaterialDescEntity> sapMaterialDescFilter = sapMaterialDescDao.createFilter();
		sapMaterialDescFilter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, materialIds));
		sapMaterialDescFilter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD,
				langCd));
		sapMaterialDescList = sapMaterialDescDao.find(sapMaterialDescFilter);

		return sapMaterialDescList;
	}

	public List<SapMaterialPlantDescEntity> fetchSapMaterialPlantDescEntitiesByMaterialIds(
			List<Long> sapMaterialIds, String langCd) {
		final ReadOnlyDao<SapMaterialPlantDescEntity> dao = getDaoProvider().getSapMaterialPlantDescEntityDao();

		final Filter<SapMaterialPlantDescEntity> filter = dao.createFilter();
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				SapMaterialPlantDescEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds));
		filter.addExpression(Restrictions.eq(SapMaterialPlantDescEntity.ATTR_LANG_CD, langCd));

		return dao.find(filter);
	}

	public void loadAtCarrierStock(int[] assignmentIds) {
		final List<Long> assignmentIdList = new ArrayList<Long>(assignmentIds.length);
		for (int assignmentId : assignmentIds) {
			assignmentIdList.add(Long.valueOf(assignmentId));
		}
		final List<AssignmentEntity> assignments = findAssignmentsByAssignmentIds(assignmentIdList);

		final Set<OrderEntity> orders = new HashSet<OrderEntity>();
		for (AssignmentEntity assignment : assignments) {
			orders.addAll(AssignmentUtils.getOrdersFromAssignment(assignment));
		}

		ConfSeqLock lock = null;
		try {
			lock = sequenceService.acquireLockForOrders(orders);
			dispoService._loadAtCarrierStock(assignmentIdList, lock);
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
		final List<OrderEntity> updatedOrders = findOrdersByAssignmentIds(assignmentIdList);
		for (OrderEntity order : updatedOrders) {
			if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
				orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false,
						true);
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _loadAtCarrierStock(List<Long> assignmentIds, ConfSeqLock lock) {
		final List<AssignmentEntity> assignments = findAssignmentsByAssignmentIds(assignmentIds);
		for (AssignmentEntity assignment : assignments) {
			loadingService.setOrdersToLoaded(AssignmentUtils.getOrdersFromAssignment(assignment),
					null, null, null, null, null, EventSource.LOGON, lock);
		}
	}

	/**
	 * Fetches the list of external and LOGON orders for the Truck Scheduling screen.
	 * The external and LOGON orders are wrapped into CustOrderWrappers.
	 * The LOGON orders are grouped together by customer orders,
	 * the external orders are grouped together by the shipto ID.
	 *
	 * @param transferContext
	 * @param requestLocale The locale of the request.
	 * @param orderNumber An existing Order number to filter for that only.
	 * @param hideScheduledCustomerOrders Should we hide those Customer Orders which are in the Scheduled state.
	 * @param hideClosedCustomerOrders Should we hide those Customer Orders which are in the Closed state.
	 * @param date The actual date for fetching the orders.
	 * @param poolIdentifier The id of the selected pool for which to fetch orders.
	 * @param carriers The id of the selected carriers for which to fetch orders.
	 * @param plantCodes The codes of the selected plants for which to fetch orders.
	 * @param noCarrier Include orders with no assigned carrier.
	 * @param userLocale
	 * @param areExternalOrdersIncluded Should return external orders also.
	 * @param userTimeZone The timezone of the user.
	 * @return A list of CustOrderWrapper objects.
	 */
	public List<CustOrderWrapper> getOrderListLocal(TransferContext transferContext,
			Locale requestLocale, Long orderNumber, boolean hideScheduledCustomerOrders,
			boolean hideClosedCustomerOrders, Date date, Long poolIdentifier, List carriers,
			Collection<String> plantCodes, boolean noCarrier, Locale userLocale,
			boolean areExternalOrdersIncluded, TimeZone userTimeZone, boolean hideTrucksAndOrderOfPartners) {
		try {
			setUp(transferContext);
			List<CustOrderWrapper> customerOrders = getOrderList(requestLocale,
					orderNumber, hideScheduledCustomerOrders, hideClosedCustomerOrders,
					date, poolIdentifier, carriers, plantCodes, noCarrier, userLocale,
					areExternalOrdersIncluded, userTimeZone, hideTrucksAndOrderOfPartners);
			// flush once more before 'setUserId(null)' to make sure that all modified
			// entities have ctlModUser set correctly.
			DaoProvider daoProvider = getDaoProvider();
			DirectJpaDao directJpaDao = daoProvider.getDirectJpaDao();
			directJpaDao.flush();
			return customerOrders;
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		finally {
			ThreadLocalUserInfo.setUserId(ThreadLocalUserInfo.UNKNOWN_USER);
			ThreadLocalUserInfo.setAccessRestrictions(ThreadLocalUserInfo.USE_ACCESS_RESTRICTIONS);
			ThreadLocalUserInfo.setDisableNextAccessRestrictions(ThreadLocalUserInfo.DO_NOT_DISABLE_NEXT_ACCESS_RESTRICTIONS);
		}
	}

	/**
	 * Fetches the list of external and LOGON orders for the Truck Scheduling screen.
	 * The external and LOGON orders are wrapped into CustOrderWrappers.
	 * The LOGON orders are grouped together by customer orders,
	 * the external orders are grouped together by the shipto ID.
	 *
	 * @param requestLocale The locale of the request.
	 * @param orderNumber An existing Order number to filter for that only.
	 * @param hideScheduledCustorders Should we hide those Customer Orders which are in the Scheduled state.
	 * @param hideClosedCustorders Should we hide those Customer Orders which are in the Closed state.
	 * @param date The actual date for fetching the orders.
	 * @param poolId The id of the selected pool for which to fetch orders.
	 * @param carriers The id of the selected carriers for which to fetch orders.
	 * @param plantCds The codes of the selected plants for which to fetch orders.
	 * @param noCarrier Include orders with no assigned carrier.
	 * @param userLocale
	 * @param extOrders Should return external orders also.
	 * @param userTimeZone The timezone of the user.
	 * @return A list of CustOrderWrapper objects.
	 */
	private List<CustOrderWrapper> getOrderList(Locale requestLocale, Long orderNumber,
			boolean hideScheduledCustorders, boolean hideClosedCustorders, Date date, Long poolId,
			List carriers, Collection<String> plantCds, boolean noCarrier, Locale userLocale,
			boolean extOrders, TimeZone userTimeZone, boolean hideTrucksAndOrderOfPartners) {

		String langCd = requestLocale.getLanguage();

        List oList = fetchOrdersForDispo(date,
				poolId,carriers,
				plantCds,
				noCarrier,
				userLocale,
				extOrders,
				userTimeZone, orderNumber, hideTrucksAndOrderOfPartners);

		List<Long> orderIds = new ArrayList<Long>();
		ConfigBean configurations = configService.getConfigBean();

		int defaultShiptoUnloadingTime = configurations.getAppDefaultShiptoUnloadingTimeCEMMin();

		//store custorderId unique to query the enities
		Set<Long> custorderEntitySet = new HashSet<Long>();
		//select custOrderIds
		for(Object o : oList){
			if(o instanceof DispoOrderListEntity){
				DispoOrderListEntity dol = (DispoOrderListEntity) o;
				Long custOrderId = dol.getCustorderId();
				custorderEntitySet.add(custOrderId);
				orderIds.add(dol.getOrderId());
			}
		}
		//fetch CustOrderIds
		List custOrderIds = new ArrayList<Long>(custorderEntitySet);
		Map<Long, CustorderViewEntity> custorderEntityMap = fetchCustorderEntities(custOrderIds, langCd);
		Map<Long, List<CustorderItemSumQtyEntity>> custorderItemSumQtyMap = fetchCustorderItemSumQtyEntities(custOrderIds, langCd);
		Map<String, CustOrderWrapper> custorderWrapperMap = new HashMap<String, CustOrderWrapper>();

		Iterator  i = oList.iterator();
		HashMap<Object, OrderWrapper> oMap = new HashMap<Object, OrderWrapper>();
		HashMap <Long, OrderWrapper> isPreschedulingRequiredOrders = new HashMap();
		List<Long> orderIdListForItems = new ArrayList<Long>();

		/** Map (orderId -> list of trucktypes) - holds a list of DispoTruckTypeListEntity for every order. */
		Map<Long, List<DispoTruckTypeListEntity>> ttMap = new HashMap<Long, List<DispoTruckTypeListEntity>>();

		//ConfigBean configBean = getConfigBean();
		while (i.hasNext()){
			Object order = i.next();
			OrderWrapper ow = new OrderWrapper(order, null, userTimeZone);


			if (ow.isExternalOrder()) {
				// If the custorder number filter is present do not display external orders
				if (orderNumber != null) {
					continue;
				}
				DispoExtOrderListEntity extOrd = ow.getExtOrder();
				oMap.put("EXT_" + extOrd.getExtOrderId(), ow);
				if (extOrd.getUnloadingTime() == null){
					// Not real material, so we consider app default value for cement unloading time
					extOrd.setUnloadingTime(new Long(defaultShiptoUnloadingTime));
				}

				// 'mimic' order item entities
				DispoOrderItemListEntity doil = new DispoOrderItemListEntity();

				doil.setMaterialName(extOrd.getMaterialName());
				doil.setOrderedQuantity(extOrd.getOrderedQuantity());
				doil.setUomName(extOrd.getUomName());
				doil.setUomCd(extOrd.getUomCd());
				doil.setPlannedLoadingTimeMin(extOrd.getLoadingTime());
				doil.setMaterialPackageCd(Constant.MATERIAL_PACKAGE_BULK_POWDER);
				doil.setLogonPackageTypeCd(Constant.PACKAGE_TYPE_BULK_POWDER);
				doil.setIslandfill(false);

				ow.addItem(doil);

				/*
				 * LOGON-6227 - Customer order IDs and External orders IDs could collide.
				 * LOGON-6238 - External orders - visualization in scheduling tab open orders
				 * LOGON-7027 - Scheduling screen - js error when creating two different external order
				 * Due to high space consuption, External orders should grouped by ShipTo.
				 * Hence we use the ShipTo ID as Map key but with az additional "ext_" prefix not to collide with Customer order IDs.
				 * By using the ShipTo ID as key, we get the effect of grouping the External orders by ShipTo.
				 */
				String externalOrderKey = "ext_" + extOrd.getShiptoId();
				CustOrderWrapper custOrderWrapper = custorderWrapperMap.get(externalOrderKey);
				if(custOrderWrapper == null){
					custOrderWrapper = new CustOrderWrapper(null, null);
				}
				if(LogonStatusUtils.isBeforeDispatched(extOrd.getOrderStatusCd()) && extOrd.getOrderStatusCd() != Constant.ORDER_STATUS_NEW){
					custOrderWrapper.addOrderWrapper(ow);
				}
				custorderWrapperMap.put(externalOrderKey, custOrderWrapper);
			}
			else {
				DispoOrderListEntity dol = ow.getLogonOrder();

				List<DispoTruckTypeListEntity> ttList = ttMap.get(dol.getShiptoSapBpId());
				if (ttList == null){
					ttList = new LinkedList<DispoTruckTypeListEntity>();
					ttMap.put(dol.getOrderId(), ttList);
				}
				ow.setTruckTypeList(ttList);

				CustorderViewEntity custorderViewEntity = custorderEntityMap.get(dol.getCustorderId());

				oMap.put(dol.getOrderId(), ow);
				orderIdListForItems.add(dol.getOrderId()); // 2nd call to fetch items, not needed https://aww.adnovum.ch/aww/admin/mazone/umantis/index.htmfor external orders
				isPreschedulingRequiredOrders.put(dol.getOrderId(),ow);

				Long custOrderId = dol.getCustorderId();
				if(custOrderId == null){
					custOrderId = -1L;
				}
				CustOrderWrapper custOrderWrapper = custorderWrapperMap.get(custOrderId.toString());
				if(custOrderWrapper == null){
					//create new custorderWrapper if not present
					List<CustorderItemSumQtyEntity> custorderItemSumQtyEntityList = custorderItemSumQtyMap.get(custOrderId);
					//custorderItemSumQtyMap contains lists. If this list has more than 1 elements, then the customer order header must not show quantity information because more materials can not be
					//displayed in one header. A popup will show this info on mouseover.
					CustorderItemSumQtyEntity custorderItemSumQtyEntity = null;
					/**
					 * Customer Order Header Color Coding:
					 * Determining the custorder header class to color the background.
					 * Possible values:
					 * - custOrderHead: initial color (simple grey),
					 * - custOrderHeadGrey: the first Single Delivery Order is scheduled to a truck (dark gray),
					 * - all Single Delivery Orders of a Customer Order are scheduled (blue).
					 */
					String custorderHeadClass = "custOrderHead";
					int nrOfScheduledOrders = 0;
					int nrOfOrders = 0;
					if (custorderItemSumQtyEntityList != null) {
						custorderItemSumQtyEntity =  (custorderItemSumQtyEntityList.size() == 1)
								? custorderItemSumQtyEntityList.get(0) : null;
						for (CustorderItemSumQtyEntity coisl : custorderItemSumQtyEntityList) {
							nrOfScheduledOrders += coisl.getNrOfScheduledOrders();
							nrOfOrders += coisl.getNrOfOrders();
						}
					}

					if(nrOfScheduledOrders > 0){
						custorderHeadClass = "custOrderHeadGrey";
						if(nrOfScheduledOrders == nrOfOrders){
							custorderHeadClass = "custOrderHeadBlue";
						}
					}

					custOrderWrapper = new CustOrderWrapper(custorderViewEntity, custorderItemSumQtyEntity);
					custOrderWrapper.setCustorderHeadClass(custorderHeadClass);
				}

				if (LogonStatusUtils.isBeforeDispatched(dol.getOrderStatusCd()) && dol.getOrderStatusCd() != Constant.ORDER_STATUS_NEW) {
					custOrderWrapper.addOrderWrapper(ow);
				}

				custorderWrapperMap.put(custOrderId.toString(), custOrderWrapper);
			}
		}

		// Iterate over Customer Orders
		Set<String> keys = custorderWrapperMap.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();

			CustOrderWrapper custOrderWrapper = custorderWrapperMap.get(key);

			List<OrderWrapper> orderWrappers = custOrderWrapper.getOrderWrappers();

			CustorderViewEntity custorderViewEntity = custOrderWrapper.getCustorderViewEntity();

			// Filter the Customer order ...
			boolean needsToBeFiltreredOut = false;

			// ... where all Single Delivery Orders have been scheduled on trucks.
			if (hideScheduledCustorders && (orderWrappers == null || orderWrappers.isEmpty())) {
				needsToBeFiltreredOut = true;
			}
			// ... where the custorder is closed.
			if (hideClosedCustorders && custorderViewEntity != null &&
					custorderViewEntity.getCustorderStatusCd() == Constant.CUSTORDER_STATUS_FINISHED) {
				needsToBeFiltreredOut = true;
			}
			// .. which do not have single delivery orders (eg. they are deleted).
			if(orderWrappers == null || orderWrappers.isEmpty()) {
				CustorderItemSumQtyEntity custorderItemSumQty = custOrderWrapper.getSumQuantities();
				if (custorderItemSumQty == null) {
					needsToBeFiltreredOut = true;
				}
				else {
					Double nrOfScheduledOrders = custorderItemSumQty.getNrOfScheduledOrders();
					Double nrOfOrders = custorderItemSumQty.getNrOfOrders();

					// If all of the orders are scheduled, we don't remove it from the display.
					if (nrOfOrders == null || nrOfScheduledOrders == null || nrOfOrders.compareTo(nrOfScheduledOrders) != 0) {
						needsToBeFiltreredOut = true;
					}
				}
			}
			if(needsToBeFiltreredOut) {
				it.remove();
			}
		}

		// Load the truck types
		List<DispoTruckTypeListEntity> truckTypeList = fetchTruckTypesForDispo(orderIds, requestLocale);

		//Associate the appropriate truck types with the order wrappers
		Iterator<DispoTruckTypeListEntity> it = truckTypeList.iterator();
		while (it.hasNext()) {
			DispoTruckTypeListEntity truckTypeListEntity = it.next();
			List<DispoTruckTypeListEntity> tl = ttMap.get(truckTypeListEntity.getOrderId());
			if (tl != null){
				tl.add(truckTypeListEntity);
			}
		}

		List iList = fetchOrderItemsForDispo(orderIdListForItems, langCd);
		i = iList.iterator();

		initPlanningInfo(isPreschedulingRequiredOrders);

		while (i.hasNext()){
			DispoOrderItemListEntity doil = (DispoOrderItemListEntity)i.next();
			OrderWrapper ow = (OrderWrapper)oMap.get(doil.getOrderId());

			ow.addItem(doil);
		}



		List<CustOrderWrapper> custOrderValues = new ArrayList<CustOrderWrapper>();

		custOrderValues.addAll(custorderWrapperMap.values());

		return custOrderValues;

	}

	/**
	 * Fetches a list of AssignmentWrappers for the given filters.
	 */
	public List<AssignmentWrapper> getAssignmentList(final String language, List<Long> carriers,
			List<Long> allowedTruckIds, List<Long> filterTruckIds, Collection<String> plantCds, Long poolId, Date date,
			Locale locale, TimeZone timeZone, Long orderNumber, boolean hideTrucksAndOrderOfPartners) {
		// fetch assignment id's authorized for based on the list of authorized trucks
		final List<Long> assignmentIdsByTruck = fetchAssignmentIdsByTruckIds(allowedTruckIds, date,
				timeZone);
		final Set<Long> assignmentIdsByTruckSet = new HashSet<Long>(assignmentIdsByTruck);

		// fetch assignment id's authorized for based on the pool and carrier
		final List<Long> assignmentIdsByPoolAndCarrier;

		if (hideTrucksAndOrderOfPartners) {
			assignmentIdsByPoolAndCarrier = Collections.emptyList();
		}
		else {
			assignmentIdsByPoolAndCarrier = fetchAssignmentIdsByPoolIdAndCarrierIds(
					poolId, carriers, date, timeZone, filterTruckIds);
		}

		// fetch all assignments for both authorization types
		final List<DispoAssignmentListEntity> assignments = fetchAssignmentsForDispo(
				assignmentIdsByTruck, assignmentIdsByPoolAndCarrier, language, orderNumber);

		// assignment id
		final SortedMap<Long, AssignmentWrapper> assignmentMap = new TreeMap<Long, AssignmentWrapper>();

		// for collecting all assignment wrappers
		List<AssignmentWrapper> aws = new ArrayList<AssignmentWrapper>();

		// order id
		final HashMap<Long, AssignmentPartWrapper> orderIdUnloadPartMap = new HashMap<Long, AssignmentPartWrapper>();
		// order id
		final HashMap<Long, AssignmentWrapper> orderIdAssignmentMap = new HashMap<Long, AssignmentWrapper>();

		// now we need to devide them again into the two authorization types and at the same
		// time put them into a map for constant-time access later on.
		for (DispoAssignmentListEntity ass : assignments) {
			final Long assignmentId = ass.getAssignmentId();

			// this must be determined through assignmentIdsByTruckSet - assignmentIdsByPoolAndCarrierSet
			// may also contain some elements from assignmentIdsByTruckSet, so the following is
			// wrong: notAuthorized = assignmentIdsByPoolAndCarrierSet.contains(assignmentId)
			final boolean authorized = assignmentIdsByTruckSet.contains(assignmentId);
			final AssignmentWrapper aw = new AssignmentWrapper(ass, !authorized, timeZone);

			assignmentMap.put(assignmentId, aw);
		}

		final Set<Long> assignmentIds = assignmentMap.keySet();

		final List<DispoAssignPartListEntity> assignmentParts = fetchAssignmentPartsForDispo(
				new ArrayList<Long>(assignmentIds), language);

		final Map<Long, DispoAssignPartListEntity> assignmentPartMap = new HashMap<Long, DispoAssignPartListEntity>();

		for (DispoAssignPartListEntity ap : assignmentParts) {
			addAssignmentPartWrapper(orderIdUnloadPartMap, orderIdAssignmentMap, ap, assignmentMap);
			assignmentPartMap.put(ap.getAssignmentPartId(), ap);
			em.detach(ap);
		}
		SortedMap<Long, AssignmentWrapper> assignmentSortedByLoadDate = new TreeMap<Long, AssignmentWrapper>();
		// If the assignment part has invalid duration, calculate the duration and the distance.
		for (Long assignmentId : assignmentIds) {
			final AssignmentWrapper assignmentWrapper = assignmentMap.get(assignmentId);

			final List<AssignmentPartWrapper> parts = assignmentWrapper.getParts();
			final Long assignmentWrapperId = parts.get(0).getAssignmentPart().getPlannedStart().getTime() + assignmentWrapper.getAssignment().getAssignmentId();
			assignmentSortedByLoadDate.put(assignmentWrapperId, assignmentWrapper);
			for (int j = 0; j < parts.size(); j++) {
				final AssignmentPartWrapper currentAPart = parts.get(j);

				if (currentAPart.isDurationInvalid()) {
					final AssignmentPartWrapper nextAPart = parts.get(j + 1);

					if (LOG.isDebugEnabled()) {
						LOG.debug(
								"## current: assignmentPartId: '{}', assignmentPartTypeCd: '{}', isDurationInvalid: '{}'",
								new Object[] {
										currentAPart.getAssignmentPart().getAssignmentPartId(),
										currentAPart.getAssignmentPart().getAssignmentPartTypeCd(),
										currentAPart.isDurationInvalid() });

						LOG.debug(
								"## next: assignmentPartId: '{}', assignmentPartTypeCd: '{}', isDurationInvalid: '{}'",
								new Object[] { nextAPart.getAssignmentPart().getAssignmentPartId(),
										nextAPart.getAssignmentPart().getAssignmentPartTypeCd(),
										nextAPart.isDurationInvalid() });
					}

					correctDistanceAndDurationOnInvalidAssignmentPart(
							currentAPart.getAssignmentPart(), nextAPart.getAssignmentPart(), assignmentPartMap);
					// Call checkduration() again, this will set the durationInvalid flag
					currentAPart.checkDuration(nextAPart);
				}
			}

		}
		aws.addAll(assignmentSortedByLoadDate.values());
		/**
		 * AT THIS POINT
		 * assignmentsMap holding authorizes and not authorized assignments INITIALIZED
		 * apList holding authorizes and not authorized assignmentParts INITIALIZED
		 */

		//filter for plant
		//notAuthorized assignments are also filtered, because they all the assignments are contained in assignments variable what is filtered.
		//According to the latest informations this filter is not working correctly: if a truck has at least one order for that plant all asssignments of that truck must be shown -> this will change
		if (plantCds != null) {
			// Filter for plants. This info is only available in the
			// assignmentpart loading. The assignment wrapper is removed if
			// it has a non-matching plant.

			//1. find all truck that has loads regarding plantCds

			//collect trucks where a truck has at least one order for plantCds's plants
			List<Long> tuckIdsWithAssignmentToShow = new ArrayList<Long>();
			for (DispoAssignPartListEntity dapl : assignmentParts) {
				if ((dapl.getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD || dapl.getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FINISHED)
						&& dapl.getPlantCd() != null) {
					AssignmentWrapper assignmentWrapper = assignmentMap.get(dapl.getAssignmentId());
					Long truckId = assignmentWrapper.getAssignment().getTruckId();
					if (tuckIdsWithAssignmentToShow.contains(truckId)) {
						// already added
						continue;
					}
					if (plantCds.contains(dapl.getPlantCd())) {
						tuckIdsWithAssignmentToShow.add(truckId);
					}
				}
			}

			for (DispoAssignPartListEntity dapl : assignmentParts) {
				if (dapl.getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
					if (!assignmentMap.containsKey(dapl.getAssignmentId())) {
						// already removed
						continue;
					}
					AssignmentWrapper assignmentWrapper = assignmentMap.get(dapl.getAssignmentId());
					Long truckId = assignmentWrapper.getAssignment().getTruckId();

					if (!tuckIdsWithAssignmentToShow.contains(truckId)) {
						assignmentMap.remove(dapl.getAssignmentId());
					}
				}
			}
		}

		List<Long> orderIds = new ArrayList<Long>(orderIdAssignmentMap.keySet());

		// Load the truck types for which the assignment could be dispatched from a truck to another.
		List<DispoTruckTypeListEntity> truckTypeList = fetchTruckTypesForDispo(orderIds, locale);

		for (DispoTruckTypeListEntity truckType : truckTypeList) {
			Long orderId = truckType.getOrderId();
			// Get the assignmentId for an order from oMap
			AssignmentWrapper assignmentWrapper = orderIdAssignmentMap.get(orderId);
			if (assignmentWrapper != null) {
				Long assignmentId = assignmentWrapper.getAssignment().getAssignmentId();
				// Set the truckTypelList in assignmentsMap
				assignmentWrapper = assignmentMap.get(assignmentId);
				if (assignmentWrapper != null) {
					assignmentWrapper.setTruckTypeList(truckTypeList);
				}
			}
		}

		List<DispoOrderItemListEntity> oiList = fetchOrderItemsForDispo(orderIds, language);

		initPlanningInfo(orderIdUnloadPartMap);
		Iterator<Long> it = orderIdUnloadPartMap.keySet().iterator();
		while (it.hasNext()) {
			Long id = (Long) it.next();
			AssignmentWrapper ass = orderIdAssignmentMap.get(id);
			ass.initPlanningRevenueAttributes();
		}

		for (DispoOrderItemListEntity doil : oiList) {
			AssignmentWrapper aw = orderIdAssignmentMap.get(doil.getOrderId());
			aw.addItem(doil);
		}

		for (AssignmentWrapper aw : assignmentMap.values()) {
			aw.initialize();
		}

		return aws;
	}

	/**
	 * Updates the provided maps according to the attributes of the assignment part, and
	 * creates an assignmentpartWrapper object, and attaches that to the AssignmentWrapper.
	 * @param unloadPartMap
	 * @param oMap - map containing order id to AssignmentWrapper mapping
	 * @param dapl - the assignment part view entity to process
	 * @param aMap - the map of assignments
	 */
	private void addAssignmentPartWrapper(
			HashMap<Long, AssignmentPartWrapper> unloadPartMap,
			HashMap<Long, AssignmentWrapper> oMap, DispoAssignPartListEntity dapl,
			Map<Long, AssignmentWrapper> aMap) {

		AssignmentWrapper aw = aMap.get(dapl.getAssignmentId());
		if (aw != null) {
			// This might be null in case it was filtered based on the
			// plantcd above.
			AssignmentPartWrapper apw = aw.addPart(dapl);
			oMap.put(dapl.getOrderId(), aw);
			if (dapl.getOrderId() != null ) {
				unloadPartMap.put(dapl.getOrderId(), apw);
			}
		}
	}

	/**
	 * Calculates the distance and duration between two assignment parts
	 * with the help of GoogleMapsService.
	 * Each assignment part should contain a either a valid:
	 * - shippingpoint,
	 * - shipto,
	 * - coordinates.
	 *
	 * @param fromAssignmentPart DispoAssignPartListEntity instance
	 * which will be used as a FROM part.
	 * @param toAssignmentPart DispoAssignPartListEntity instance
	 * which will be used as a TO part.
	 *
	 * @return An GoogleMapsDistanceMatrixResultBean instance
	 * which holds the calculation status (OK or not), the distance and
	 * the duration in minutes.
	 */
	private GoogleMapsDistanceMatrixResultBean calculateDistanceAndDuration(
			DispoAssignPartListEntity fromAssignmentPart,
			DispoAssignPartListEntity toAssignmentPart) {
		final Long assignmentId = fromAssignmentPart.getAssignmentId();
		final Long seqNo = fromAssignmentPart.getAssignmentPartSeqNo();
		final Long assignmentPartId = fromAssignmentPart.getAssignmentPartId();

		ShippingpointEntity fromShippingpoint = null;
		ShippingpointEntity toShippingpoint = null;
		ShiptoEntity fromShipto = null;
		ShiptoEntity toShipto = null;

		Double fromCoordLat = null;
		Double fromCoordLon = null;
		Double toCoordLat = null;
		Double toCoordLon = null;

		// Getting FROM values
		if (fromAssignmentPart.getSpCoordLat() != null) {
			// From the shippingpoint of the assignment
			final Long fromShippingpointId = fromAssignmentPart.getShippingpointId();
			fromShippingpoint = shippingPointService.loadShippingPoint(fromShippingpointId);
		}
		else if (fromAssignmentPart.getStCoordLat() != null
				&& !fromAssignmentPart.getLnfShiptoFlag()) {
			// From the shipto of the assignment
			final Long fromShiptoId = fromAssignmentPart.getShiptoId();
			fromShipto = dispokarteService.fetchShipto(fromShiptoId);
		}
		else if (fromAssignmentPart.getCoordLat() != null) {
			// Need to use the coordinates of the assignment as FROM!
			fromCoordLat = fromAssignmentPart.getCoordLat();
			fromCoordLon = fromAssignmentPart.getCoordLon();
		}

		// Getting TO values
		if (toAssignmentPart.getSpCoordLat() != null) {
			// To the shippingpoint of the NEXT assignment
			final Long toShippingpointId = toAssignmentPart.getShippingpointId();
			toShippingpoint = shippingPointService.loadShippingPoint(toShippingpointId);
		}
		else if (toAssignmentPart.getStCoordLat() != null && !toAssignmentPart.getLnfShiptoFlag()) {
			final Long toShiptoId = toAssignmentPart.getShiptoId();
			toShipto = dispokarteService.fetchShipto(toShiptoId);
		}
		else if (toAssignmentPart.getCoordLat() != null) {
			toCoordLat = toAssignmentPart.getCoordLat();
			toCoordLon = toAssignmentPart.getCoordLon();
		}

		// All possible combination of origins and destinations are handled here
		if (LOG.isDebugEnabled()) {
			LOG.debug("## Calculating the distance and duration for the assignment part. "
					+ "assignmentId: '{}', seqNo: '{}', assignmentPartId: '{}'", new Object[] {
					assignmentId, seqNo, assignmentPartId });
		}

		GoogleMapsDistanceMatrixResultBean distanceMatrixResult = null;
		if (fromShippingpoint != null && toShipto != null) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromShippingpoint,
					toShipto);
		}
		else if (fromShipto != null && toShippingpoint != null) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(toShippingpoint,
					fromShipto);
		}
		else if (fromShippingpoint != null && toShippingpoint != null) {
			// Is this combination could exist?
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromShippingpoint,
					toShippingpoint);
		}
		else if (fromShipto != null && toShipto != null) {
			// In tour cases this is a valid combination
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromShipto, toShipto);
		}
		else if (fromShippingpoint != null && toCoordLat != null && toCoordLon != null) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromShippingpoint,
					toCoordLat, toCoordLon);
		}
		else if (fromShipto != null && toCoordLat != null && toCoordLon != null) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromShipto,
					toCoordLat, toCoordLon);
		}
		else if (fromCoordLat != null && fromCoordLon != null && toShippingpoint != null) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromCoordLat,
					fromCoordLon, toShippingpoint);
		}
		else if (fromCoordLat != null && fromCoordLon != null && toShipto != null) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromCoordLat,
					fromCoordLon, toShipto);
		}
		else if (fromCoordLat != null && fromCoordLon != null && toCoordLat != null
				&& toCoordLon != null) {
			distanceMatrixResult = googleMapsService.getDistanceMatrixResult(fromCoordLat,
					fromCoordLon, toCoordLat, toCoordLon);
		}

		return distanceMatrixResult;
	}

	/**
	 * Calculates the duration of the pre-load part of the assignment with thisAssignmentId.
	 * Used for the operation which sets the position of a pre_load assignment
	 * part of this assignment to correspond to the finished assingment part
	 * of the prev assignment.
	 *
	 * @see DispoAjaxService.setAssignmentPrePosition()
	 *
	 * @param prevAssignmentId The id of the prev (on the truck) assignment.
	 * @param thisAssignmentId The id of the actual assignment.
	 *
	 * @return The calculated duration in monutes for the pre-load part of the
	 * assignment with thisAssignmentId. If the calculation failed, returns -1.
	 */
	public long calculateDurationForSetAssignmentPrePosition(long prevAssignmentId,
			long thisAssignmentId) {
		long durationMin = -1;

		final ReadOnlyDao<DispoAssignPartListEntity> apDao = getDaoProvider().getDispoAssignPartListEntityDao();

		Filter<DispoAssignPartListEntity> filter = apDao.createFilter();
		filter.addExpression(Restrictions.eq(DispoAssignPartListEntity.ATTR_ASSIGNMENT_ID,
				prevAssignmentId));
		filter.addExpression(Restrictions.eq(
				DispoAssignPartListEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_FINISHED));
		final DispoAssignPartListEntity prevAssignmentFinishedPart = apDao.findSingle(filter);

		filter = apDao.createFilter();
		filter.addExpression(Restrictions.eq(DispoAssignPartListEntity.ATTR_ASSIGNMENT_ID,
				thisAssignmentId));
		filter.addExpression(Restrictions.eq(
				DispoAssignPartListEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_LOAD));

		final DispoAssignPartListEntity thisAssignmentLoadPart = apDao.findSingle(filter);

		final GoogleMapsDistanceMatrixResultBean distanceMatrixResult = calculateDistanceAndDuration(
				prevAssignmentFinishedPart, thisAssignmentLoadPart);

		if (distanceMatrixResult == null) {
			LOG.error(
					"Could not calculate the distance for the pre-load assignment part for setAssignmentPrePosition(). "
							+ "distanceMatrixResult is null. prevAssignmentId: '{}', thisAssignmentId: '{}'",
					new Object[] { prevAssignmentId, thisAssignmentId });
			return durationMin;
		}

		final String status = distanceMatrixResult.getStatus();
		if (!Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK.equals(status)) {
			LOG.error(
					"Could not calculate the distance for the pre-load assignment part for setAssignmentPrePosition(). "
							+ "status is not OK. status: '{}', prevAssignmentId: '{}', thisAssignmentId: '{}'",
					new Object[] { status, prevAssignmentId, thisAssignmentId });
		}
		else if (distanceMatrixResult.getDurationMin() == null) {
			LOG.error(
					"Could not calculate the distance for the pre-load assignment part for setAssignmentPrePosition(). "
							+ "distanceMatrixResult.getDurationSec() is null. prevAssignmentId: '{}', thisAssignmentId: '{}'",
					new Object[] { prevAssignmentId, thisAssignmentId });
		}
		else {
			durationMin = distanceMatrixResult.getDurationMin();
		}

		return durationMin;
	}

	/**
	 * Calculates the duration between the last _unload_ assignment part of the previous
	 * assignment and the first _load_ assignment part of the current assignment.
	 *
	 * Used for the operation which sets the return shipping point for the prev assignment.
	 *
	 * @see DispoAjaxService.setPrevAssignmentReturnShippingpoint()
	 *
	 * @param prevAssignmentId The id of the previous (on the truck) assignment.
	 * @param thisAssignmentId The id of the current assignment.
	 *
	 * @return The calculated duration in minutes between the last _unload_ assignment part
	 * of the previous assignment and the first _load_ assignment part of the current assignment.
	 * If the calculation fails, returns -1.
	 */
	public long calculateDurationForSetPrevAssignmentReturnShippingpoint(long prevAssignmentId,
			long thisAssignmentId, Locale locale) {
		long durationMin = -1;

		final ReadOnlyDao<DispoAssignPartListEntity> apDao = getDaoProvider().getDispoAssignPartListEntityDao();

		Filter<DispoAssignPartListEntity> filter = apDao.createFilter();
		filter.addExpression(Restrictions.eq(DispoAssignPartListEntity.ATTR_ASSIGNMENT_ID,
				prevAssignmentId));
		filter.addExpression(Restrictions.eq(DispoAssignPartListEntity.ATTR_LANG_CD,
				locale.getLanguage()));
		filter.addExpression(Restrictions.eq(
				DispoAssignPartListEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_UNLOAD));
		filter.addExpression(Order.asc(DispoAssignPartListEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));

		final List<DispoAssignPartListEntity> unloadPartsOfPrevAssignment = apDao.find(filter);
		if (unloadPartsOfPrevAssignment.isEmpty()) {
			return durationMin;
		}
		final DispoAssignPartListEntity lastUnloadPartOfPrevAssignment = unloadPartsOfPrevAssignment.get(unloadPartsOfPrevAssignment.size() - 1);

		filter = apDao.createFilter();
		filter.addExpression(Restrictions.eq(DispoAssignPartListEntity.ATTR_ASSIGNMENT_ID,
				thisAssignmentId));
		filter.addExpression(Restrictions.eq(
				DispoAssignPartListEntity.ATTR_ASSIGNMENT_PART_TYPE_CD,
				Constant.ASSIGNMENT_PART_TYPE_LOAD));
		filter.addExpression(Order.asc(DispoAssignPartListEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));

		final List<DispoAssignPartListEntity> loadPartsOfCurrentAssignment = apDao.find(filter);
		if (loadPartsOfCurrentAssignment.isEmpty()) {
			return durationMin;
		}
		final DispoAssignPartListEntity firstLoadPartOfCurrentAssignment = loadPartsOfCurrentAssignment.get(0);

		final GoogleMapsDistanceMatrixResultBean distanceMatrixResult = calculateDistanceAndDuration(
				lastUnloadPartOfPrevAssignment, firstLoadPartOfCurrentAssignment);

		if (distanceMatrixResult == null) {
			LOG.error(
					"Could not calculate the distance for the pre-load assignment part for setPrevAssignmentReturnShippingpoint(). "
							+ "distanceMatrixResult is null. prevAssignmentId: '{}', thisAssignmentId: '{}'",
					new Object[] { prevAssignmentId, thisAssignmentId });
			return durationMin;
		}

		final String status = distanceMatrixResult.getStatus();
		if (!Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK.equals(status)) {
			LOG.error(
					"Could not calculate the distance for the pre-load assignment part for setPrevAssignmentReturnShippingpoint(). "
							+ "status is not OK. status: '{}', prevAssignmentId: '{}', thisAssignmentId: '{}'",
					new Object[] { status, prevAssignmentId, thisAssignmentId });
		}
		else if (distanceMatrixResult.getDurationMin() == null) {
			LOG.error(
					"Could not calculate the distance for the pre-load assignment part for setPrevAssignmentReturnShippingpoint(). "
							+ "distanceMatrixResult.getDurationSec() is null. prevAssignmentId: '{}', thisAssignmentId: '{}'",
					new Object[] { prevAssignmentId, thisAssignmentId });
		}
		else {
			durationMin = distanceMatrixResult.getDurationMin();
		}

		return durationMin;
	}

	/**
	 * Calculates and corrects the distance and the duration of the currentAssignmentPart.
	 * Only called if the duration of currentAssignmentPart gets invalid.
	 */
	public void correctDistanceAndDurationOnInvalidAssignmentPart(DispoAssignPartListEntity currentAssignmentPart,
			DispoAssignPartListEntity nextAssignmentPart, Map<Long, DispoAssignPartListEntity> assignmentPartMap) {
		final Long assignmentId = currentAssignmentPart.getAssignmentId();
		final Long seqNo = currentAssignmentPart.getAssignmentPartSeqNo();
		final Long assignmentPartId = currentAssignmentPart.getAssignmentPartId();

		try {
			final GoogleMapsDistanceMatrixResultBean distanceMatrixResult = calculateDistanceAndDuration(
					currentAssignmentPart, nextAssignmentPart);

			if (distanceMatrixResult == null) {
				LOG.warn(
						"Could not calculate the distance and duration on loading the assignment part. "
								+ "No shippingpoint, shipto, or coordinates are available!"
								+ "assignmentId: '{}', seqNo: '{}', assignmentPartId: '{}'",
						new Object[] { assignmentId, seqNo, assignmentPartId });
				return;
			}

			final String status = distanceMatrixResult.getStatus();
			if (Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK.equals(status)) {

				// Correct the assignment part duration
				final Long durationMin = distanceMatrixResult.getDurationMin();
				if (durationMin != null) {
					List<AssignmentPartEntity> assignmentParts = orderService.correctAssignmentPartDuration(assignmentId, seqNo, durationMin);
					if (assignmentParts != null && assignmentPartMap != null) {
						for (AssignmentPartEntity ap : assignmentParts) {
							Long partId = ap.getAssignmentPartId();
							DispoAssignPartListEntity dispoAssPart = assignmentPartMap.get(partId);
							if (dispoAssPart != null) {
								dispoAssPart.setPlannedStart(ap.getPlannedStart());
							}
						}
					}
				}

				// Save the calculated distance
				final Long distanceMeter = distanceMatrixResult.getDistance();
				orderService.correctAssignmentPartDistance(assignmentPartId, distanceMeter);
				currentAssignmentPart.setTransportDistanceM(distanceMeter);

				if (LOG.isDebugEnabled()) {
					LOG.debug("## distanceMeter: '{}', durationMin: '{}'.", new Object[] {
							distanceMeter, durationMin });
				}
			}
			else {
				LOG.warn(
						"Could not calculate the distance and duration on loading the assignment parts. "
								+ "response status code: '{}', assignmentId: '{}', seqNo: '{}', assignmentPartId: '{}'",
						new Object[] { status, assignmentId, seqNo, assignmentPartId });
			}
		}
		catch (RuntimeException ex) {
			LOG.error(
					"Exception when calculating the distance and duration on loading the assignment parts."
							+ "assignmentId: '{}', seqNo: '{}', assignmentPartId: '{}'",
					new Object[][] { { "assignmentId", assignmentId }, { "seqNo", seqNo },
							{ "assignmentPartId", assignmentPartId } }, ex);
		}
	}

	/**
	 * Initializes the OrderWrapper/AssignmentPartWrapper with the prescheduled
	 * planning data.
	 * @param map
	 */
	private void initPlanningInfo(HashMap map) {
		if (!map.isEmpty()) {
			ArrayList orderIdList = new ArrayList();
			orderIdList.addAll(map.keySet());
			List revenuePlanningList = fetchRevenuePlanningForOrders(orderIdList);

			Iterator it = revenuePlanningList.iterator();
			while (it.hasNext()) {
				DispoPlanningEntity planningEntity = (DispoPlanningEntity) it.next();
				Object o = map.get(planningEntity.getOrderId());
				if (o instanceof OrderWrapper) {
					//OrderWrappers are on the bottom part of the dispo screen
					OrderWrapper ow = (OrderWrapper) o;
					ow.initPlanningRevenueAttributes(planningEntity);
				}
				else if (o instanceof AssignmentPartWrapper) {
					//AssignmentPartWrapper are on the upper part of the dispo screen, they are part of AssignmentWrapper objects
					AssignmentPartWrapper awp = (AssignmentPartWrapper) o;
					awp.initPlanningRevenueAttributes(planningEntity);
				}
			}
		}
	}

	private String setUp(TransferContext ctx) {
		ThreadLocalUserInfo.setDisableNextAccessRestrictions(false);
		ThreadLocalUserInfo.setAccessRestrictions(true);
		Long dispPrivUserId = getUserId(ctx);
		String pInfo = dispPrivUserId + " (" +  ctx.getPrincipal() + ")";
		ThreadLocalUserInfo.setUserId(dispPrivUserId);
		if (ConfigInitializer.inst.isInitialized()) {
			ConfigBean cb = ConfigInitializer.inst.getBean();
			LogonProperties lp = cb.getLogonProperties();
			boolean accRestr = lp.getAccessRestrictions();
			ThreadLocalUserInfo.setAccessRestrictions(accRestr);
		}
		return pInfo;
	}

	/**
	 * We should be able to get the principal from the sessioncontext
	 * however, for unauthenticated calls (e.g. from the init webapp)
	 * we get a 'java.lang.IllegalStateException: No valid security context for the caller identity'
	 * this can allegedly be remedied by setting:
	 * <unauthenticated-principal>anonymous</unauthenticated-principal>
	 * in standardjboss.xml, by my local tests didn't confirm that.
	 */
	private Long getUserId(TransferContext ctx) {
		try {
			return ctx.getUserId();
		}
		catch (IllegalStateException e) {
			// in this case we have a tech user, see comment above.
			LOG.debug("3d1978be39af4123a354b7b46140e079"); // find this place...
			return ctx.getUserId();
		}
	}

	/**
	 * Fetches rail order items for given order id list
	 *
	 * @param orders The id list of the Orders
	 * @param langCd The language
	 * @param productName The preferred material
	 * @return The rail order item list which belonging to the list of order ids.
	 */
	public List<DispoOrderItemListEntity> fetchRailOrderItemsForDispo(List<Long> orders,
			String langCd, String productName) {
		if (orders.isEmpty()) {
			return new ArrayList<DispoOrderItemListEntity>();
		}
		ReadOnlyDao<DispoOrderItemListEntity> doilDao =
				getDaoProvider().getDispoOrderItemListEntityDao();
		Filter<DispoOrderItemListEntity> f = doilDao.createFilter();

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(DispoOrderItemListEntity.ATTR_ORDER_ID, orders));
		f.addExpression(Restrictions.eq(DispoOrderItemListEntity.ATTR_MATERIAL_LANG_CD, langCd));

		if(!StringUtils.empty(productName)) {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("%").append(productName).append("%");
			String productNameValue= sBuilder.toString();
			f.addExpression(
				Restrictions.or(
					Restrictions.ilike(DispoOrderItemListEntity.ATTR_PURGED_MATERIAL_NAME, productNameValue),
					Restrictions.ilike(DispoOrderItemListEntity.ATTR_MATERIAL_NUMBER, productNameValue)
				)
			);
		}

		return (doilDao.find(f));
	}

	/**
	 * Fetches assignments to given orderNumber
	 *
	 * @param assignments The list of assignments
	 * @param langCd The language
	 * @param orderNumber The orderNumber of assignmentpart
	 * @return The list of assignmentParts which belonging to the given idlist of assignments and contained the orderNumber if presented.
	 * */
	public List<DispoAssignPartListEntity> fetchAssignmentPartsToOrderNumberForDispo(List assignments,
			String langCd, String orderNumber) {
		if (assignments.isEmpty()) {
			return new ArrayList<DispoAssignPartListEntity>();
		}

		final ReadOnlyDao<DispoAssignPartListEntity> dao =
				getDaoProvider().getDispoAssignPartListEntityDao();
		final Filter<DispoAssignPartListEntity> f = dao.createFilter();

		if(!StringUtils.empty(orderNumber)) {
			StringBuilder orderNumberExprVal = new StringBuilder();
			orderNumberExprVal.append("%").append(orderNumber).append("%");
			f.addExpression(Restrictions.like(DispoAssignPartListEntity.ATTR_ORDER_NUMBER_IN_STR, orderNumberExprVal.toString()));
		}

		f.addExpression(DbUtils.createSplittedInRestrictionOfIds(DispoAssignPartListEntity.ATTR_ASSIGNMENT_ID, assignments));
		f.addExpression(Restrictions.or(
				Restrictions.eq(DispoAssignPartListEntity.ATTR_LANG_CD, langCd),
				Restrictions.isNull(DispoAssignPartListEntity.ATTR_LANG_CD))); //there are no language-dependent fields if there is no order

		return dao.find(f);
	}

	/**
	 * Creates one pre-load for each WagonPreLoadBean specified.
	 * @param wagon not null. The wagon to be loaded
	 * @param lp not null. The loading point for all pre-loads
	 * @param preLoads not null. The list of pre-loads to be created
	 */
	public void preLoad(WagonEntity wagon, LoadingpointEntity lp, List<WagonPreLoadBean> preLoads) {
		Date loadingDate = new Date();

		// caches for production plant and uom
		Map<String, PlantBsEntity> prodPlantMap = new HashMap<String, PlantBsEntity>();
		Map<String, UomBsEntity> uomMap = new HashMap<String, UomBsEntity>();

		for (WagonPreLoadBean preLoad : preLoads) {

			// material
			Long sapMaterialId = preLoad.getSapMaterialId();
			SapMaterialEntity material = getDaoProvider().getSapMaterialEntityDao().findById(sapMaterialId);

			// production plant
			String prodPlantCd = preLoad.getProdPlantCd();
			PlantBsEntity prodPlant = prodPlantMap.get(prodPlantCd);
			if (prodPlant == null) {
				prodPlant = referentialDataService.findBsEntity(PlantBsEntity.class,
						prodPlantCd);
				prodPlantMap.put(prodPlantCd, prodPlant);
			}

			// weights/ quantities
			BigDecimal tareWeightT = preLoad.getTareWeightT();
			BigDecimal grossWeightT = preLoad.getGrossWeightT();
			BigDecimal netQuantity = preLoad.getNetQuantity();

			// unit of measure
			String netQuantityUomCd = preLoad.getNetQuantityUomCd();
			if (netQuantityUomCd == null) {
				netQuantityUomCd = Constant.UOM_TONS;
			}
			UomBsEntity netQuantityUom = uomMap.get(netQuantityUomCd);
			if (netQuantityUom == null) {
				netQuantityUom = referentialDataService.findBsEntity(UomBsEntity.class, netQuantityUomCd);
				uomMap.put(netQuantityUomCd, netQuantityUom);
			}

			LoadEntity load = loadingService.createAndPersistWagonLoad(wagon, material, netQuantityUom, prodPlant);

			loadingService.preLoad(load, lp,
					tareWeightT, grossWeightT,
					netQuantity, netQuantityUom,
					loadingDate, loadingDate,
					EventSource.LOGON);
		}
	}

	/**
	 * Returns the wagon transport category code for the wagon.
	 * Based on this one can determine if the wagon can load bag, bulk or container orders.
	 *
	 * @param wagon The wagon entity - not null.
	 *
	 * @return Long the transport category code for the wagon.
	 */
	public Long getWagonTrspCatCd(WagonEntity wagon) {
		final Long wagonId = wagon.getWagonId();

		final ReadOnlyDao<DispoWagonTrspCatEntity> dao = getDaoProvider().getDispoWagonTrspCatEntityDao();
		final Filter<DispoWagonTrspCatEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(DispoWagonTrspCatEntity.ATTR_WAGON_ID, wagonId));

		final DispoWagonTrspCatEntity wagonTrspCat = dao.findSingle(filter);
		final Long wagonTrspCatCd = wagonTrspCat.getWagonTrspCatCd();

		return wagonTrspCatCd;
	}

	private void createTruckModifications(List<DispoTruckListV2Entity> truckList, final String language, Date date, boolean isNotAuthorizedTruck, TimeZone userTimeZone,
			TruckModificationDTO truckModificationDTO, int beginIdx, Map<Long, Long> staleTruckIdsWithPdaStatus, List<Long> changedTruckIds) {
		List<Long> shippingpointIdList = new ArrayList<Long>();

		Map<Long, TruckWrapper> truckMap = new HashMap<Long, TruckWrapper>();
		Map<Long, TruckWrapper> driverMap = new HashMap<Long, TruckWrapper>();

		List<Long> truckIds = changedTruckIds;
		boolean mustCollectTruckIds = (truckIds == null);
		if(mustCollectTruckIds) {
			truckIds = new ArrayList<Long>();
		}

		for (DispoTruckListV2Entity dte : truckList) {
			Long truckId = dte.getTruckId();
			TruckWrapper tw = new TruckWrapper(dte, isNotAuthorizedTruck);
			truckMap.put(truckId, tw);

			if (mustCollectTruckIds) {
				truckIds.add(truckId);
			}
			else {
				if (!truckIds.contains(truckId)) {
					tw.setIsChanged(false);
					continue;
				}
				else {
					tw.setIsChanged(true);
				}
			}

			// If Truck Home Plant is set, we collect the shippingpointIds
			if (dte.getShippingpointId() != null) {
				shippingpointIdList.add(dte.getShippingpointId());
			}
			Boolean isCompositionValid = LogonUtil.isTruckCompositionValid(
					dte.getTruckTransportCatCd(), dte.getTrailerTrspCatCd(),
					dte.getContainerTrspCatCd());
			tw.setCompositionValid(isCompositionValid);
			if (dte.getDriverId() != null) {
				driverMap.put(dte.getDriverId(), tw);
			}
		}
		List<DispoTruckLoadListV2Entity> lList = fetchTruckLoads(truckIds, language);

		for (DispoTruckLoadListV2Entity ll : lList) {
			TruckWrapper tw = truckMap.get(ll.getTruckId());
			if(tw != null) {
				tw.addLoad(ll);
			}
		}

		List<TruckUnavailabilityEntity> uList = fetchTrucksUnavailability(truckIds, date);

		for (TruckUnavailabilityEntity tue : uList) {
			TruckWrapper tw = truckMap.get(tue.getTruck().getTruckId());
			if(tw != null) {
				tw.addTruckUnavailability(tue);
			}
		}

		List<TrafficJamEntity> jList = fetchTrafficJams(truckIds, date);

		for (TrafficJamEntity tj : jList) {
			TruckWrapper tw = truckMap.get(tj.getTruck().getTruckId());
			if(tw != null) {
				tw.addTrafficJam(tj);
			}
		}

		List<DispoDriverBreakListEntity> bList = fetchDriverBreaks(new ArrayList<Long>(driverMap.keySet()), date, true);

		for (DispoDriverBreakListEntity dbe : bList) {
			TruckWrapper tw = driverMap.get(dbe.getDriverId());
			if(tw != null) {
				tw.addDriverBreak(dbe);
			}
		}

		List<DispoHomePlantEntity> homePlants = fetchDispoHomePlants(shippingpointIdList);
		for (DispoHomePlantEntity dispoHomePlant : homePlants) {
			for (TruckWrapper tw : truckMap.values()) {
				if (tw.getTruck().getShippingpointId() != null
						&& tw.getTruck().getShippingpointId().equals(
								dispoHomePlant.getShippingpointId())) {
					tw.setHomePlant(dispoHomePlant);
					break;
				}
			}
		}

		ConfigBean cb = configService.getConfigBean();
		int appDefaultDispoDriverLunchBreakStart = cb.getAppDefaultDispoDriverLunchBreakStart();
		int appDefaultDispoDriverLunchBreakEnd = cb.getAppDefaultDispoDriverLunchBreakEnd();

		for (TruckWrapper tw : driverMap.values()) {
			tw.checkLunchBreak(appDefaultDispoDriverLunchBreakStart,
					appDefaultDispoDriverLunchBreakEnd, date, userTimeZone);
		}

		List<DispoMessageListEntity> mList = fetchDriverMessages(new ArrayList<Long>(driverMap.keySet()), date);

		for (DispoMessageListEntity dme : mList) {
			TruckWrapper tw = driverMap.get(dme.getDriverId());
			if(tw != null) {
				tw.addDriverMessage(dme);
			}
		}

		int index = beginIdx;
		for (DispoTruckListV2Entity dte : truckList) {
			Long truckId = dte.getTruckId();
			boolean isNew = !truckModificationDTO.removeStaleTruckId(truckId);
			TruckWrapper tw = truckMap.get(truckId);
			if(tw != null) {
				if (isNew) {
					truckModificationDTO.getNewTrucks().add(new IndexedObject(tw, index));
				} else if (tw.getIsChanged()) {
					truckModificationDTO.addChangedTruck(truckId, new IndexedObject(tw, index));
				}
			}

			index++;
		}
	}

	private boolean isPdaSessionStatusChanged(DispoTruckListV2Entity truck,
			Map<Long, Long> staleTruckIds) {
		Long currentPdaStatusCd = truck.getPdaSessionStatusCd();
		Long previousPdaStatusCd = staleTruckIds != null ? staleTruckIds.get(truck.getTruckId()) : null;

		if (currentPdaStatusCd == previousPdaStatusCd) { // if both null
			return false;
		}
		else if (currentPdaStatusCd != null
				&& previousPdaStatusCd != null
				&& currentPdaStatusCd.compareTo(previousPdaStatusCd) == 0) {
			return false;
		}
		else {
			return true;
		}
	}

	public TruckAndAssignmentModificationDTO getTruckAndAssigmentModificationList(TransferContext transferContext, Date date,
			final String language, Locale locale, Long poolId, List dispoCarriers,
			Boolean hideTrucksWithoutDeliveries, Boolean hideInactiveTrucks,
			Integer filterForGenericTruck, Collection<Long> orderDeliveryStatusCodes,
			Collection<String> plantCodes, Long orderNumber, Integer sizeOfOrderStatusList,
			List<Long> filterTruckIds, TimeZone userTimeZone, List<Long> staleTruckIds, Map<Long, Long> staleTruckIdsWithPdaStatus, Long lastModDate, Long truckPriority, boolean hideTrucksAndOrderOfPartners) {
		try {
			setUp(transferContext);
			TruckAndAssignmentModificationDTO result = getTruckAndAssigmentModificationList(date, language, locale, poolId, dispoCarriers,
					hideTrucksWithoutDeliveries, hideInactiveTrucks, filterForGenericTruck,
					orderDeliveryStatusCodes, plantCodes,
					orderNumber, sizeOfOrderStatusList, filterTruckIds, userTimeZone, staleTruckIds, staleTruckIdsWithPdaStatus, lastModDate, truckPriority, hideTrucksAndOrderOfPartners);
			// flush one more time before 'setUserId(null)' to make sure that all modified
			// entities have ctlModUser set correctly.
			getDaoProvider().getDirectJpaDao().flush();
			return result;
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		finally {
			ThreadLocalUserInfo.setUserId(null);
			ThreadLocalUserInfo.setAccessRestrictions(true);
			ThreadLocalUserInfo.setDisableNextAccessRestrictions(false);
		}
	}

	private void filterTruckAndAssigmentModificationList(TruckModificationDTO truckModification,
			Collection<Long> orderDeliveryStatusCds, List<AssignmentWrapper> assignments,
			Boolean hideTrucksWithoutDeliveries, Collection<Long> truckIdsWithOrders) {

		Map<Long, List<AssignmentWrapper>> truckAssignmentsMap = new HashMap<Long, List<AssignmentWrapper>>();
		for (Long truckId : truckIdsWithOrders) {
			List<AssignmentWrapper> assignmentList = truckAssignmentsMap.get(truckId);
			if (assignmentList == null) {
				assignmentList = new ArrayList<AssignmentWrapper>();
				truckAssignmentsMap.put(truckId, assignmentList);
			}
			for (AssignmentWrapper assignmentWrapper : assignments) {
				if (truckId.equals(assignmentWrapper.getAssignment().getTruckId())) {
					List<Long> assignmentOrderStatusCdList = assignmentWrapper.getOrderStatusCdList();
					if (assignmentOrderStatusCdList != null
							&& (assignmentOrderStatusCdList.isEmpty() || (orderDeliveryStatusCds != null && !Collections.disjoint(
									assignmentOrderStatusCdList, orderDeliveryStatusCds)))) {
						assignmentList.add(assignmentWrapper);
					}
				}
			}
		}

		assignments.clear();
		List<Long> truckHasAssignments = new ArrayList<Long>();
		for (Map.Entry<Long, List<AssignmentWrapper>> truckAssignments : truckAssignmentsMap.entrySet()) {
			long truckId =  truckAssignments.getKey();
			if (!truckAssignments.getValue().isEmpty()) {
				assignments.addAll(truckAssignments.getValue());
				truckHasAssignments.add(truckId);
			}
		}

		if (hideTrucksWithoutDeliveries) {
			truckModification.retainTrucks(truckHasAssignments);
		}
	}

	private boolean isChangedDispoTruckListEntity(DispoTruckListV2Entity dispoTruckList, Map<Long, Long> staleTruckIdsWithPdaStatus, Date lastAccessTime) {
		return isPdaSessionStatusChanged(dispoTruckList, staleTruckIdsWithPdaStatus) ||
				(dispoTruckList.getCtlModTs() != null && dispoTruckList.getCtlModTs().after(lastAccessTime));
	}

	private TruckAndAssignmentModificationDTO getTruckAndAssigmentModificationList(Date date, final String language, Locale locale,
			Long poolId, List dispoCarriers, Boolean hideTrucksWithoutDeliveries,
			Boolean hideInactiveTrucks, Integer filterForGenericTruck,
			Collection<Long> orderDeliveryStatusCds, Collection<String> plantCds,
			Long orderNumber, Integer sizeOfOrderStatusList, List<Long> filterTruckIds,
			TimeZone userTimeZone, List<Long> staleTruckIds, Map<Long, Long> staleTruckIdsWithPdaStatus, Long lastModDate, Long truckPriority, boolean hideTrucksAndOrderOfPartners) {

		boolean isNotAuthorizedTruck = false;
		int beginIdx = 0;
		Long currentTime = new Date().getTime();
		Date lastAccessTime = lastModDate != null ? new Date(lastModDate) : null;
		List<Long> truckIds = staleTruckIds;
		TruckModificationDTO truckModification = new TruckModificationDTO(truckIds);
		List carriers = new ArrayList(dispoCarriers);

 		// get authorized truck list
		List<DispoTruckListV2Entity> authorizedTruckList = fetchTrucksForDispo(language, poolId, carriers, filterForGenericTruck, hideInactiveTrucks, filterTruckIds, truckPriority);

		List<Long> changedTruckIds = lastAccessTime != null ? new ArrayList<Long>() : null;

		Set<Long> authorizedTruckIds = new HashSet<Long>();
		for (DispoTruckListV2Entity dispoTruckList : authorizedTruckList) {
			authorizedTruckIds.add(dispoTruckList.getTruckId());
			if(lastAccessTime != null && isChangedDispoTruckListEntity(dispoTruckList, staleTruckIdsWithPdaStatus, lastAccessTime)) {

				changedTruckIds.add(dispoTruckList.getTruckId());
			}
		}
		List<Long> authorizedTruckIdList = new ArrayList<Long>(authorizedTruckIds);

		if(lastAccessTime != null) {
			List<Long> changedIds = fetchTruckChangesForDispo(authorizedTruckIdList, date, lastAccessTime);
			changedTruckIds.addAll(changedIds);
		}

		if(changedTruckIds == null || !changedTruckIds.isEmpty()) {
			createTruckModifications(authorizedTruckList, language, date, isNotAuthorizedTruck , userTimeZone, truckModification, beginIdx, staleTruckIdsWithPdaStatus, changedTruckIds);
		} else {
			for (Long authorizedTruckId : authorizedTruckIds) {
				truckModification.removeStaleTruckId(authorizedTruckId);
			}
		}

		// get assignments
		List<AssignmentWrapper> assignments = getAssignmentList(language, carriers, authorizedTruckIdList, filterTruckIds, plantCds, poolId, date, locale, userTimeZone, orderNumber, hideTrucksAndOrderOfPartners);

		List<Long> notAuthorizedTruckIds = new ArrayList<Long>();
		Set<Long> truckIdsWithOrders = new HashSet<Long>();
		for (AssignmentWrapper assignmentWrapper: assignments) {
			DispoAssignmentListEntity assignment = assignmentWrapper.getAssignment();
			if (!authorizedTruckIds.contains(assignment.getTruckId())) {
				notAuthorizedTruckIds.add(assignment.getTruckId());
			}
			truckIdsWithOrders.add(assignment.getTruckId());
		}

		// Filter trucks and assignments by orderDeliveryStatusCds and custorderNumber
		filterTruckAndAssigmentModificationList(truckModification, orderDeliveryStatusCds, assignments, hideTrucksWithoutDeliveries, truckIdsWithOrders);

		// get not authorized truck list
		if(!notAuthorizedTruckIds.isEmpty()) {
			isNotAuthorizedTruck = true;
			beginIdx = authorizedTruckList.size();
			changedTruckIds = null;

			List<DispoTruckListV2Entity> notAuthorizedTruckList = fetchNotAuthorizedTrucksForDispo(poolId, notAuthorizedTruckIds, filterForGenericTruck, language);

			if(lastAccessTime != null) {
				changedTruckIds = new ArrayList<Long>();
				for (DispoTruckListV2Entity dispoTruckList : notAuthorizedTruckList) {
					if(isChangedDispoTruckListEntity(dispoTruckList, staleTruckIdsWithPdaStatus, lastAccessTime)) {
						changedTruckIds.add(dispoTruckList.getTruckId());
					}
				}
				List<Long> changedIds = fetchTruckChangesForDispo(notAuthorizedTruckIds, date, lastAccessTime);
				changedTruckIds.addAll(changedIds);
			}

			if(changedTruckIds == null || !changedTruckIds.isEmpty()) {
				createTruckModifications(notAuthorizedTruckList, language, date, isNotAuthorizedTruck, userTimeZone, truckModification, beginIdx, staleTruckIdsWithPdaStatus, changedTruckIds);
			} else {
				for (Long notAuthorizedTruckId : notAuthorizedTruckIds) {
					truckModification.removeStaleTruckId(notAuthorizedTruckId);
				}
			}

		}

		AssignmentModificationDTO assignmentModification = new AssignmentModificationDTO(assignments);
		TruckAndAssignmentModificationDTO truckAndAssignmentModification = new TruckAndAssignmentModificationDTO(truckModification, assignmentModification, currentTime);
		return truckAndAssignmentModification;
	}

	/**
	 * Returns orders being in status arrived at loading point
	 */
	public List<Long> getOrderNumbersToConfirm(List<Long> orderNumbers) {
		final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		JPAQuery<OrderEntity> query = orderDao.createJPAQuery();
		query.setDistinct(true);
		query.addSelect(SelectExpression.simpleSelect(OrderEntity.ATTR_ORDER_NUMBER));
		final Filter<OrderEntity> filter = new FilterImpl<OrderEntity>();
		filter.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_STATUS_CD, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT));
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(OrderEntity.ATTR_ORDER_NUMBER, orderNumbers));
		query.setFilter(filter);
		return orderDao.find(query);
	}
}

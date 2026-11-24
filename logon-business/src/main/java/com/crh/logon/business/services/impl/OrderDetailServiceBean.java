/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.kafka.KafkaPublisherService;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.SapVendorDTO;
import com.crh.logon.business.services.dispatchers.OrderDetailServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.localinterfaces.AssignmentServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.BizeventAuditServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CarrierServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ContributionCalcServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FrozenTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAssignmentServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.orderdetail.dto.BimodalTruckOrderItemDTO;
import com.crh.logon.business.services.utils.BindingsDTO;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderDetailsUtil;
import com.crh.logon.business.services.utils.SortOrder;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.business.stores.dictionaries.UnitOfMeasurementStoreInterface;
import com.crh.logon.business.stores.domain.AssignmentPartStoreInterface;
import com.crh.logon.business.stores.domain.AssignmentStoreInterface;
import com.crh.logon.business.stores.domain.CustorderOutputdocStoreInterface;
import com.crh.logon.business.stores.domain.LoadingPointStoreInterface;
import com.crh.logon.business.stores.domain.OrderItemMarginStoreInterface;
import com.crh.logon.business.stores.domain.OrderItemStoreInterface;
import com.crh.logon.business.stores.domain.OrderRepStoreInterface;
import com.crh.logon.business.stores.domain.OrderStoreInterface;
import com.crh.logon.business.stores.domain.PoolStoreInterface;
import com.crh.logon.business.stores.domain.ShippingPointStoreInterface;
import com.crh.logon.business.stores.sap.BusinessPartnerInterface;
import com.crh.logon.business.stores.sap.ContractItemStoreInterface;
import com.crh.logon.business.stores.sap.ShiptoSareaStoreInterface;
import com.crh.logon.business.stores.system.UserStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderOutputdocEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderRepDmgcodeTypeEntity;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.OrderTpSapVendorEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.TpSapVendorEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnMaterialUsageBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonBsEntity;
import com.crh.logon.persistence.entities.refdata.SapTpSapVendorPfBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;
import com.crh.logon.persistence.entities.views.BizeventOrderLogEntity;
import com.crh.logon.persistence.entities.views.BizeventTruckLogEntity;
import com.crh.logon.persistence.entities.views.DamagecodeSearchEntity;
import com.crh.logon.persistence.entities.views.OrderItemMarginEntity;
import com.crh.logon.persistence.entities.views.TpSapVendorSearchEntity;
import com.crh.logon.persistence.utils.ReadOnlyDaoImplNew;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;
import org.apache.commons.lang.Validate;


/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class OrderDetailServiceBean extends OrderDetailServiceDispatcher implements OrderDetailServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderDetailServiceBean.class);

	@EJB
	OrderStoreInterface orderStore;
	@EJB
	OrderRepStoreInterface orderRepairMaintenanceInformationStore;
	@EJB
	OrderItemStoreInterface orderItemStore;
	@EJB
	OrderItemMarginStoreInterface orderItemMarginStore;
	@EJB
	ContractItemStoreInterface contractItemStore;
	@EJB
	UserStoreInterface userStore;
	@EJB
	ShippingPointStoreInterface shippingPointStore;
	@EJB LoadingPointStoreInterface loadingPointStore;
	@EJB CustorderOutputdocStoreInterface custorderOutputdocStore;
	@EJB PoolStoreInterface poolStore;
	@EJB AssignmentStoreInterface assignmentStore;
	@EJB AssignmentPartStoreInterface assignmentPartStore;
	@EJB
	UnitOfMeasurementStoreInterface unitOfMeasurementStore;
	@EJB private ShiptoSareaStoreInterface shiptoSareaStore;
	@EJB BizeventAuditServiceLocalInterface bizeventAuditService;
	@EJB OrderServiceLocalInterface orderService;
	@EJB
	MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB
	RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB
	PoolAdminServiceLocalInterface poolAdminService;
	@EJB
	CarrierServiceLocalInterface carrierService;
	@EJB MaterialEnhancementsServiceLocalInterface materialEnhancementsService;
	@EJB PoolAssignmentServiceLocalInterface poolAssignmentService;
	@EJB AssignmentServiceLocalInterface assignmentService;
	@EJB ContributionCalcServiceLocalInterface contributionCalcService;
	@EJB FrozenTablesServiceLocalInterface frozenTablesService;
	@EJB private DeliverySettingServiceLocalInterface deliverySettingService;
	@EJB private KafkaPublisherService kafkaPublisherService;
	@EJB
	BusinessPartnerInterface businessPartnerStore;

	public OrderEntity loadOrder(Long orderId) {
		// [[
		OrderEntity result = null;
		Object[][] params = {{"orderId", orderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrder", params);
		}
		// ]]
		try {
			OrderEntity order = orderStore.findAndIntializeById(orderId);
			return result = order;
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
	 * Loads a {@code PersonEntity} for a given user id
	 * @param userId - the user we are looking for
	 */
	@AdnTrace
	public PersonEntity loadPerson(Long userId) {
		UserEntity user = userStore.findById(userId);
		return user.getPerson();
	}
	 
	/**
	 * Fetches a list of {@code ShippingpointEntity}s where where the plantCd equals to the parameter 
	 * @param plantCd - filter clause
	 * @return result list of entities if none found empty list
	 */
	public List<ShippingpointEntity> loadShippingPointList(String plantCd) {
		// [[
		List<ShippingpointEntity> result = null;
		Object[][] params = {{"plantCd", plantCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingPointList", params);
		}
		// ]]
		try {
			List<ShippingpointEntity> shippingPoints = shippingPointStore.findShippingPointsByPlantCd(plantCd);
			return result = shippingPoints;
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
	 * Fetches a list of {@code CustorderOutputdocEntity}s where where the custorderId equals to the parameter 
	 * The result list is sorted by the entity's creation timestamp.
	 * @param customerOrderId - filter clause
	 * @return result list of entities if none found empty list
	 */
	@AdnTrace
	public List<CustorderOutputdocEntity> findCollectiveDeliveryNotesByCustomerOrderIdOrderedByCreationTimeStamp(Long customerOrderId) {
		return custorderOutputdocStore.findByCustomerOrderIdOrderedByCreationTimeStamp(customerOrderId);
	}

	/**
	 * Fetches a list of {@code LoadingpointEntity}s where where the shippingPointId equals to the parameter 
	 * @param shippingPointId - filter clause
	 * @return result list of entities if none found empty list
	 */
	public List<LoadingpointEntity> findLoadingPointsByShippingPointId(Long shippingPointId) {
		// [[
		List<LoadingpointEntity> result = null;
		Object[][] params = {{"shippingPointId", shippingPointId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findLoadingPointsByShippingPointId", params);
		}
		// ]]
		try {
			return result = loadingPointStore.findLoadingPointsByShippingPointId(shippingPointId);
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
	 * Loads a list of bizevent audits for the order.
	 * 
	 * @param orderId
	 * @param langCd
	 */
	public List<BizeventAuditDisplayEntity> loadBizeventAudits(Long orderId, String langCd) {
		// [[
		Object[][] params = {{"orderId", orderId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadBizeventAudits", params);
		}
		// ]]
		try {
			
			final List<BizeventOrderLogEntity> bizeventLogs = loadBizevents(orderId, langCd);
			
			if (bizeventLogs.isEmpty()) {
				return new ArrayList<BizeventAuditDisplayEntity>();
			}
			
			final List<Long> bizeventIds = new ArrayList<Long>();
			for (BizeventOrderLogEntity log : bizeventLogs) {
				bizeventIds.add(log.getBizeventId());
			}
			
			final List<BizeventAuditDisplayEntity> bizeventAudits = bizeventAuditService.loadBizeventAudits(langCd, bizeventIds);
			return bizeventAudits;
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
	 * Loads a list of pools (PoolTxEntity)
	 * @param locale locale
	 * @adnlog.exclude return
	 */
	public List<PoolTxEntity> loadPoolList(Locale locale) {
		// [[
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadPoolList", params);
		}
		// ]]
		try {
		    return poolAdminService.fetchPoolTxEntities(locale);
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
	 * Fetches a {@code PoolEntity} where the poolId equals to the parameter 
	 * @param poolId - filter clause
	 * @return entity found if none null
	 */
	@AdnTrace
	public PoolEntity loadPool(Long poolId) {
		return poolStore.findById(poolId);
	}

	/**
	 * Returns a map of {@code CarrierEntity}s mapped by their carrier ID
	 * and the vendor is a commercial carrier
	 * @param poolId - filter clause
	 * @return result list of entities if none found empty list
	 */
	@AdnTrace
	public Map<Long, CarrierEntity> loadCommCarrierMap(Long poolId) {
		return carrierService.fetchCommercialCarriersByPoolId(poolId);
	}

	/**
	 * @see CarrierServiceBean#fetchExecutingCarriersByCommercialCarrierWithPreSelection(Long, Long)
	 */
	@AdnTrace
	public List<CarrierEntity> loadExecCarrierList(Long commercialCarrierId, Long execCarrierId) {
		return carrierService.fetchExecutingCarriersByCommercialCarrierWithPreSelection(commercialCarrierId, execCarrierId);
	}

	/**
	 * Loads driver(person entity) for a given truck id 
	 * @param truckId truck id for which we want to get a driver
	 * @adnlog.exclude return
	 */
	public PersonEntity loadDriver(Long truckId) {
		// [[
		Object[][] params = {{"truckId", truckId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadDriver", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<DriverEntity> driverDao = getDaoProvider().getDriverEntityDao();
			final Filter<DriverEntity> filter = driverDao.createFilter(); 
			
			filter.addExpression(Restrictions.eq(DriverEntity.ATTR_CURRENT_TRUCK_ID, truckId));			
			final DriverEntity driver = driverDao.findSingle(filter);
			if (driver == null) {
				return null;
			}
			return driver.getUser().getPerson();
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
	 * Finds and returns an {@code AssignmentEntity} using the provided orderId as a filter criteria
	 * @param orderId - key used for the lookup
	 * @return found entity or null
	 */
	@AdnTrace
	public AssignmentEntity loadAssignment(Long orderId) {
		return assignmentStore.findByOrderId(orderId);
	}


	/**
	 * Finds and returns an {@code AssignmentPartEntity} using the provided loadId as a filter criteria
	 * @param loadId - key used for the lookup
	 * @return found entity or null
	 */
	@AdnTrace
	public AssignmentPartEntity loadAssignmentPart(Long loadId) {
		return assignmentPartStore.findByLoadId(loadId);
	}
	
	/***
	 * Returns the number of orders in the assignment.
	 * I.e. tour: result > 1;
	 */
	@AdnTrace
	public BigDecimal getNumberOfOrdersInAssignment(Long orderId) {
		BigDecimal result;
		List <Object> arguments = new ArrayList<>();
		arguments.add(orderId);
				
		String q = "select cnt from (" +
				" select order_id, assignment_id, count (order_id) over (partition by assignment_id) as cnt" +
				" from "+  Constant.SCHEMA_USER_PREPEND + AssignmentPartEntity.TABLE_NAME +
				" where assignment_part_type_cd=3)" +
				" where order_id=?";
		
		List <Object> qResult = getDaoProvider().getDirectJpaDao().findNative(q, arguments.toArray());
		
		if ( qResult != null && !qResult.isEmpty() ) {
			result = ((BigDecimal) qResult.get(0));
		}
		else { 
			result = null;
		}
		return result;
	}	
	
	/**
	 * Loads current trailer of the truck
	 * @param truckId id of the truck 
	 * @adnlog.exclude return
	 */
	public TrailerEntity loadTrailer(Long truckId) {
		// [[
		Object[][] params = {{"truckId", truckId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTrailer", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<TrailerEntity> dao = getDaoProvider().getTrailerEntityDao();
			final Filter<TrailerEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID, truckId));
			return dao.findSingle(filter);
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
	 * Returns a listing of {@code BimodalTruckListingDTO} items which correspond
	 * to a bimodal rail order item. The resulting list contains the bimodal truck order items.
	 * @param orderItemId - order item id of the bimodal rail order item
	 * @param language - ISO language code - used for UOM text lookup
	 * @returns 	fetched list of DTOs or empty list if none found.
	 */
	@AdnTrace
	public List<BimodalTruckOrderItemDTO> loadBimodalTruckList(Long orderItemId, String language) {

		List<BimodalTruckOrderItemDTO> orderTruckList = new ArrayList<BimodalTruckOrderItemDTO>();

		List<OrderItemEntity> orderItemList = orderItemStore.findBimodalTruckOrderItemByRailOrderItemId(orderItemId);

		for (OrderItemEntity oi : orderItemList) {

			OrderEntity order = oi.getOrder();

			Long orderId = order.getOrderId();
			Long orderNumber = order.getOrderNumber();
			boolean authorized = loadOrder(orderId) != null; 

			AssignmentEntity assignment = assignmentStore.findByOrderId(orderId);

			String truckName = "";
			if (assignment != null) {
				TruckEntity truck = assignment.getTruck();
				if (truck != null){
					truckName = truck.getTruckName();
				}
			}

			LoadEntity load = oi.getLoad();

			String uomTxt = "";
			BigDecimal netQuantity = null;
			if (load != null && load.getLoadingTs() != null) {
				UomBsEntity netQuantityUom = load.getNetQuantityUom();
				String uomCd = netQuantityUom.getUomCd();
				UomTxEntity uomTx = unitOfMeasurementStore.findText(uomCd, language);
				uomTxt = uomTx != null ? uomTx.getTxt() : "";
				netQuantity = load.getNetQuantity();
			}

			BimodalTruckOrderItemDTO bimodalTruckListing = new BimodalTruckOrderItemDTO(truckName, orderId, orderNumber, authorized, netQuantity, uomTxt);
			orderTruckList.add(bimodalTruckListing);

		}

		return orderTruckList;

	}
	
	/**
	 * Loads an sap bp sarea
	 * @param sapBpId
	 * @param sareaCode
	 * @adnlog.exclude return
	 */
	public SapBpSareaEntity loadSapBpSarea(Long sapBpId, Long sareaCode) {
		// [[
		Object[][] params = {{"sapBpId", sapBpId},
							{"sareaCode", sareaCode}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadSapBpSarea", params);
		}
		// ]]
		try {
			final ReadOnlyDao<SapBpSareaEntity> dao = getDaoProvider().getSapBpSareaEntityDao();
			final Filter<SapBpSareaEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID, sapBpId));
			filter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAREA_ID, sareaCode));
			return dao.findSingle(filter);
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
	 * Loads a material description for a given materail id
	 * @adnlog.exclude return
	 */
	public SapMaterialDescriptionEntity loadMaterialDesc(Long materialId, String langCd) {
		// [[
		Object[][] params = {{"materialId", materialId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMaterialDesc", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapMaterialDescriptionEntity> descDao = getDaoProvider().getSapMaterialDescriptionEntityDao();
			final Filter<SapMaterialDescriptionEntity> filter = descDao.createFilter();
			
			filter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.sapMaterialId", materialId));
			filter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.langCd", langCd));
			return descDao.findSingle(filter);
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
	 * Loads a Material Norm, Certificate
	 * @param materialNo sap material id
	 * @param plantCd production plant code
	 * @adnlog.exclude return
	 */
	public MatPlantEntity loadMaterialNorm(Long materialId, String plantCd) {
		// [[
		Object[][] params = {{"materialId", materialId},
							{"plantCd", plantCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMaterialNorm", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<MatPlantEntity> rulesDao = getDaoProvider().getMatPlantEntityDao();
			final Filter<MatPlantEntity> filter = rulesDao.createFilter();
			
			filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, materialId));
			filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCd));
			return rulesDao.findSingle(filter);
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
	 * Loads a list of SapMaterialPlant
	 * @param materialId sap material id
	 * @param filterOnProductionPlant filters on production plant
	 * 
	 * @return returns all the plants or only the production plants. Never returns null.
	 */
	public List<PlantBsEntity> loadSapMaterialPlantList(Long materialId, Boolean filterOnProductionPlant) {
		// [[
		List<PlantBsEntity> result = null;
		Object[][] params = {{"materialId", materialId},
							{"filterOnProductionPlant", filterOnProductionPlant}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadSapMaterialPlantList", params);
		}
		// ]]
		try {
			
			final ReadOnlyDaoImplNew<SapMaterialPlantEntity> daoNew = getDaoProviderNew().getSapMaterialPlantEntityDaoNew();
			Authorizer.disableNextAccessRestriction();
			List<PlantBsEntity> newResult = daoNew.findNew(materialId,filterOnProductionPlant);
			return result = newResult;
			
//			final ReadOnlyDao<SapMaterialPlantEntity> dao = getDaoProvider().getSapMaterialPlantEntityDao();
//			final Filter<SapMaterialPlantEntity> filter = dao.createFilter();
//			
//			filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL_ID, materialId));
//			if (filterOnProductionPlant) {
//				filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_PRODUCTION_PLANT, Boolean.TRUE));
//			}
//			filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
//			filter.addExpression(Order.asc(SapMaterialPlantEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_PLANT_NAME));
//			
//			final JPAQuery<SapMaterialPlantEntity> jpaQ = dao.createJPAQuery();
//			jpaQ.addSelect(SelectExpression.simpleSelect(SapMaterialPlantEntity.ATTR_PLANT));
//			jpaQ.setDistinct(true);
//			jpaQ.setFilter(filter);
//			Authorizer.disableNextAccessRestriction();
//			
//			@SuppressWarnings("unchecked")
//			final List<PlantBsEntity> sapMaterialPlantList = dao.find(jpaQ);
//			return result = sapMaterialPlantList;
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
	 * Load all {@link PlantBsEntity} assigned for the given sapMaterialId.
	 * @param materialId sap material id
	 * @adnlog.exclude return
	 */
	public List <PlantBsEntity> loadPlantBySapMaterialId(Long sapMaterialId) {
		// [[
		Object[][] params = {{"sapMaterialId", sapMaterialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadPlantBySapMaterialId", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapMaterialPlantEntity> dao = getDaoProvider().getSapMaterialPlantEntityDao();
			final Filter<SapMaterialPlantEntity> filter = dao.createFilter();

			filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
			filter.addExpression(Order.asc(SapMaterialPlantEntity.ATTR_PLANT + "." + PlantBsEntity.ATTR_PLANT_NAME));
			
			final JPAQuery<SapMaterialPlantEntity> jpaQ = getDaoProvider().getSapMaterialPlantEntityDao().createJPAQuery();
			jpaQ.addSelect(SelectExpression.simpleSelect(SapMaterialPlantEntity.ATTR_PLANT));
			jpaQ.setDistinct(true);
			jpaQ.setFilter(filter);
			Authorizer.disableNextAccessRestriction();
			
			@SuppressWarnings("unchecked")
			final List<PlantBsEntity> result = dao.find(jpaQ);			
			return result;
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
	 * Check if {@link PlantBsEntity} is handled by Logon only.
	 * @param materialId sap material id
	 * @adnlog.exclude return
	 */
	public Boolean isLogonOnlySupplyPlant(PlantBsEntity plant,long sapMaterialId) {
		// [[
		Object[][] params = {{"plantId", plant.getPlantCd()}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isLogonOnlySupplyPlant", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapMaterialPlantEntity> dao = getDaoProvider().getSapMaterialPlantEntityDao();
			final Filter<SapMaterialPlantEntity> filter = dao.createFilter();

			filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_PLANT_CD, plant.getPlantCd()));
			filter.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL_ID,sapMaterialId));
			
			Authorizer.disableNextAccessRestriction();
			
			final long countOfSapMaterialPlant = dao.count(filter);
			
			
			return countOfSapMaterialPlant==0;
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
	 * Loads a material description for the given material id
	 * @param sapBpId 
	 * @adnlog.exclude return
	 */
	public ShiptoEntity loadShipto(Long sapBpId) {
		// [[
		Object[][] params = {{"sapBpId", sapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShipto", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			final Filter<ShiptoEntity> filter = new FilterImpl<ShiptoEntity>();
			
			filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, sapBpId));
			return shiptoDao.findSingle(filter);
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
	 * @param sapBpNr 
	 * @adnlog.exclude return
	 */
	public ShiptoEntity loadShiptoByBpNr(String sapBpNr) {
		// [[
		Object[][] params = {{"sapBpNr", sapBpNr}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShiptoByBpNr", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
			final Filter<SapBpEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_ID, sapBpNr));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SHIPTO, Boolean.TRUE));
			final SapBpEntity sapBpEntity = dao.findSingle(filter);
			final ShiptoEntity shipto = loadShipto(sapBpEntity.getSapBpId());
			return shipto;
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
	 * Loads a list of billtos.
	 * @adnlog.exclude return
	 */
	public List<ShiptoBilltoEntity> loadBilltos(Long shiptoId, Long sareaId) {
			final ReadOnlyDao<ShiptoBilltoEntity> dao = getDaoProvider().getShiptoBilltoEntityDao();
			final Filter<ShiptoBilltoEntity> filter =  dao.createFilter();

			filter.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_SHIPTO_SAREA + "." + ShiptoSareaEntity.ATTR_SHIPTO_ID, shiptoId));
			filter.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_SHIPTO_SAREA + "." + ShiptoSareaEntity.ATTR_SAREA_ID, sareaId));
			filter.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_BILLTO_SAP_BP + "." + SapBpEntity.ATTR_MARKED_FOR_DELETION, "0"));
			return dao.find(filter);
	}

	/**
	 * It loads a list of the billtos without any authorization
	 * 
	 * @param shiptoId
	 * @param salesAreaId
	 * @return List<ShiptoBilltoEntity>
	 */
	public List<ShiptoBilltoEntity> loadBilltosUnrestricted(Long shiptoId, Long salesAreaId) {
			Authorizer.disableNextAccessRestriction();
			return businessPartnerStore.loadBilltos(shiptoId, salesAreaId);
	}
	
	/**
	 * On order detail only certain errors should block saving, certain ones have to let
	 * the process save even inconsistent data. The cause of it is that certain consistency checks
	 * make no sense to do in the past, for example a plant might produce a material -> for a certain period
	 * the suply plant for an order where this material appears is valid. Then SAP updates the order, and then 
	 * the order is invalid. Despite the order should be possible to be changed but validation errors
	 * have to be communicated to the user.
	 * 
	 * Workaround: declaring throws exception is impossible in the signature of public methods because as of today
	 * service generation simply skips adding those throws statements
	 * 
	 */
	public ValidationException validateOrderInSeparateTransaction (
			OrderEntity order) {

		OrderDetailService service;
		try {
			//accessing through jndi lookup as TransactionAttributeType.REQUIRES_NEW needs to take effect!
			final InitialContext ctx = new InitialContext();
			service = (OrderDetailService) ctx.lookup("java:global/logon/logon-business//OrderDetailServiceBean!com.crh.logon.business.services.interfaces.OrderDetailService");
		}
		catch (Exception e) {
			throw new LogonException("Lookup failed for OrderDetailServiceBean!", e);
		}
		
		return service._validateOrder(order);
	}
	

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ValidationException _validateOrder( OrderEntity order ) throws ValidationException {

		try {
			final ShiptoSareaEntity shiptoSarea =  shiptoSareaStore.findShiptoSareaWithActiveShipto(order.getShiptoSapBp(), order.getSarea());
			
			final Set<Long> sapMaterialIds = getSapMaterialIdsFromOrder(order);
			final DeliverySettingShiptoMap shiptoMap = deliverySettingService.initDeliverySettingShiptoMapForDeliverySettingValidation(order.getSoldtoSapBp(), Locale.ENGLISH, shiptoSarea, sapMaterialIds);
			final DeliverySettingSalesAreaMap salesAreaMap = shiptoMap.getSalesAreaMapForSareaId(order.getSarea().getSareaId());
			
			orderService.validateOrder(order, salesAreaMap);
		}
    	catch (ValidationException e) {
    		    		
    		CustorderError[] blockerErrors = {CustorderError.OPEN_QUANTITY_CHECK_FAILURE, CustorderError.CUSTOMER_CREDIT_BLOCK, CustorderError.LEADTIME_WEB_ORDER_ND_EARLY, 
    				CustorderError.LEADTIME_WEB_ORDER_SD_EARLY, CustorderError.LEADTIME_WEB_ORDER_SD_LATER};
    		final List<CustorderError> blockers = Arrays.asList(blockerErrors);
    		
        	for ( ValidationErrorReason reason : e.getValidationErrorReasons() ) {
        		if (blockers.contains(reason.getCustorderError()))  {
        			throw e;    				
        		}
        		if ( reason.isRefersToNewEntity() ) {
        			throw e;
        		}
        	}
        	
    		return e;
		}
		return null;
	}

	private Set<Long> getSapMaterialIdsFromOrder(OrderEntity order) {
		Set<Long> sapMaterialIds = new HashSet<Long>();
		for (OrderItemEntity orderItem : order.getOrderItems()) {
			sapMaterialIds.add(orderItem.getSapMaterial().getSapMaterialId());
		}
		return sapMaterialIds;
	}
	
	public Map<String, Object> updateOrderAndCustorder(OrderEntity order, Boolean autoPoolAssignmentFlag, 
			Boolean autoCarrierAssignmentFlag, OrderRepEntity orderRep, boolean planningOrder, List<BindingsDTO> thirdPartyFreighters) {
		CustorderEntity custorder = order.getCustorder();
		if (custorder != null) {
			ReadWriteDao<CustorderEntity> customerOrderDataObject = getDaoProvider().getCustorderEntityDao();
			customerOrderDataObject.merge(custorder);
		}
		return updateOrder(order, autoPoolAssignmentFlag, autoCarrierAssignmentFlag, orderRep, planningOrder, thirdPartyFreighters);
	}

	public Map<String, Object> updateOrder(OrderEntity order, Boolean autoPoolAssignmentFlag,
			Boolean autoCarrierAssignmentFlag, OrderRepEntity orderRep, boolean planningOrder, List<BindingsDTO> thirdPartyFreighters) {
		/**
		 * On the screen it's possible to add new order items, which have to be postprocessed/initialized before persisting, just as if 
		 * they would be present from the beginning.
		 */
		orderService.initLaterAddedOrderItems(order);
		if (planningOrder) {
			orderService.initPlanningMaterialOrderItems(order);
		}
		return updateOrder(order, autoPoolAssignmentFlag, autoCarrierAssignmentFlag, orderRep, thirdPartyFreighters);
	}

	/**
	 * Entry function for order detail update order functionality. Depending on the parameter flags status related changes
	 * are done, and as a result of those the order's visibility changes for users. 
	 * Example: schedulers (level carrier) do not see the order until it is not in status 'transmitted to pool'. 
	 * The crh handler who can initiate changing the status from 'transmitted to pool' to transmitted to carrier is the pool manager (level pool)
	 * does see the order in case it is in 'transmitted to pool' status and is supposed to specify the carrier and trigger changing
	 * the order status to 'transmitted to carrier'.
	 * 
	 * The parameter repair order is supposed to handled together with the order, therefore it's update has to be within the same transaction.
	 * It is a {@link OrderRepEntity} object, which symbolizes additional data for rail repair orders. For the majority of the orders there is no
	 * repair order, and the parameter is null.
	 * 
	 * Return value: the function returns multiple objects including the merged order in order that the popup does not need to reload it. It also needs
	 * to return the order rep in case it was specified. 
	 * 
	 * From validation point of view order detail is special, as on the screen it is and should be possible to update orders executed in the past, and therefore
	 * the SAP data in the background can already be outdated/missing. Certain validation errors need to be ignored, but communicated, other validation errors
	 * have to block even in case of order detail. 
	 * 
	 * @param order - the {@link OrderEntity} which is the subject of the merge
	 * @param autoPoolAssignmentFlag - flag which decides if the status is supposed to be raised in case it is below 'transmitted to pool'
	 * @param autoCarrierAssignmentFlag - flag which decides if the status is supposed to be raised in case it is below 'transmitted to carrier'
	 * @param orderRep - 
	 *  
	 * @adnlog.exclude return
	 */
	private Map<String, Object> updateOrder(OrderEntity order, Boolean autoPoolAssignmentFlag, Boolean autoCarrierAssignmentFlag, OrderRepEntity orderRep, List<BindingsDTO> thirdPartyFreighters) {
		// [[
		Object[][] params = {{"order", order},
							{"autoPoolAssignmentFlag", autoPoolAssignmentFlag},
							{"autoCarrierAssignmentFlag", autoCarrierAssignmentFlag},
							{"orderRep", orderRep},
							{"thirdPartyFreighters", thirdPartyFreighters}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateOrder", params);
		}
		// ]]
		try {
			// LOGON-5062: upon modification of truck driver note, the new note is to 
			// be copied to the field CARRIER_TRUCK_DRIVER_NOTE
			final OrderEntity orderFromDb = getDaoProvider().getOrderEntityDao().findById(order.getOrderId());
			
			// save third party freighters data in the same TX as the Order
			saveBindings(orderFromDb, thirdPartyFreighters);
			
			if (orderFromDb != null) {
				final String oldNoteTruckDriver = orderFromDb.getNoteTruckDriver();
				final String newNoteTruckDriver = order.getNoteTruckDriver();
				if (!org.apache.commons.lang.StringUtils.equals(oldNoteTruckDriver, newNoteTruckDriver)) {
					orderService.resetCarrierNotes(order);
				}
			}
			else {
				LOG.error("Couldn't fetch order from DB, order ID: " + order.getOrderId() + ", order number: " + order.getOrderNumber());
			}
			
			
			/**
			 * Validate the order in a separate transaction. This method will throw a rollback exception in case
			 * there is a blocking error in the order.
			 */
			ValidationException ex = validateOrderInSeparateTransaction(order);
			
			
			ReadWriteDao<LoadEntity> loadDao = getDaoProvider().getLoadEntityDao();
			
			//TODO this entry function should be somehow unified with processNewOrders
			order = transmitToTransportLevel(order, autoPoolAssignmentFlag,
					autoCarrierAssignmentFlag);

			Map<Long, OrderItemEntity> orderItemsFromDbMap = orderFromDb.getOrderItems().stream()
					.collect(Collectors.toMap(OrderItemEntity::getOrderItemId, Function.identity()));
			boolean changedReturnedOrder = false;

			/* Some fields of the load (in particular tare/net weight) can be changed in the order
			 * details -> also persist load.
			 */
			final Collection<OrderItemEntity> orderItems = LogonPackageHandlingUtil.getMaterialItems(order);
			for (OrderItemEntity oi : orderItems) {
				
				LoadEntity load = oi.getLoad();
				if (load !=null)
				{
					// If the quantities in the load have changed, we need to persist.
					final BigDecimal newNetQty = load.getNetQuantity();
					final BigDecimal newTareWeight = load.getTareWeightT();
					final BigDecimal newGrossWeight = load.getGrossWeightT();
					final SapMaterialEntity newSapMaterial = load.getSapMaterial();
					
					// Reload the load to have the original values
					final Long loadId = load.getLoadId();
					final LoadEntity loadFromDb = loadDao.findById(loadId);
					if ( loadFromDb != null ){
						boolean netQtyChanged = newNetQty != null && (loadFromDb.getNetQuantity() == null || newNetQty.compareTo(loadFromDb.getNetQuantity()) != 0);
						boolean tareWeightChanged = newTareWeight != null && (loadFromDb.getTareWeightT() == null || newTareWeight.compareTo(loadFromDb.getTareWeightT()) != 0);
						boolean grossWeightChanged = newGrossWeight != null && (loadFromDb.getGrossWeightT() == null || newGrossWeight.compareTo(loadFromDb.getGrossWeightT()) != 0);
						boolean materialChanged = newSapMaterial != null && !newSapMaterial.getSapMaterialId().equals(loadFromDb.getSapMaterial().getSapMaterialId());

						if (netQtyChanged || tareWeightChanged || grossWeightChanged || materialChanged) {
							loadFromDb.setNetQuantity(newNetQty);
							loadFromDb.setTareWeightT(newTareWeight);
							loadFromDb.setGrossWeightT(newGrossWeight);
							loadFromDb.setSapMaterial(newSapMaterial);
							loadDao.merge(loadFromDb);
							oi.setLoad(loadFromDb);

							// Need to correct goods movement
							orderService.createAndSaveGoodsMovementEntity(load);
						}
					}
				}

				OrderItemEntity orderItemFromDb = orderItemsFromDbMap.get(oi.getOrderItemId());
				if (!changedReturnedOrder && orderItemFromDb != null && (
						!Objects.equals(oi.getReturnedQuantity(), orderItemFromDb.getReturnedQuantity())
								|| !Objects.equals(oi.getReturnedUom(), orderItemFromDb.getReturnedUom()))) {
					changedReturnedOrder = true;
				}

				//TODO why not update the LR numerator and denominator? those can also change on order detail?
				orderService.updateOrderItemNumeratorAndDenominator(oi, Constant.ORDER_ITEM_NUM_DENOM_TYPE_ORDERED);
			}

			// if transmitted to carrier already, but the user has set the carrier to '-' then the state should be "ack by pool" (LOGON-6892)
			Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
			if (order.getCommCarrier() == null && order.getExecCarrier() == null
					&& (orderStatusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK)
							|| orderStatusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER))){

				orderService.changeOrderStatus(order, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);
			}

			if (changedReturnedOrder) {
				kafkaPublisherService.publishReturnOrder(order);
			}
			
			/**
			 * This function call will handle merging the order, and making sure that those attributes which have to be
			 * in sync with the persisted data are recalculated. Example: fields which are calculated from ALL order items.
			 */
			final OrderEntity orderEntity = orderService.mergeOrder(order);
			
			final BizeventEntity bizevent = new BizeventEntity();
			BizeventTypeBsEntity bs = BizEventUtil.BIZEVENT_TYPE_EDIT_FIELD;
			bizevent.setBizeventType(bs);
			bizevent.setOrder(orderEntity);

			//initialize event_ts to "now"/systimestamp"
			bizevent.setEventTs(new Date());
			
			//will only persist the edit bizevent if something changes
			bizeventAuditService.persistAuditData(bizevent);
			
			if ( (order.getBpCorrectedFlag() != null) && (order.getBpCorrectedFlag().booleanValue()) ) {
				BizeventEntity event = new BizeventEntity();
				event.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, Constant.BIZEVENT_TYPE_MODIFY_FINISHED));
				event.setEventTs(new Date());
				event.setOrder(orderEntity);
				monitoringTablesService.processBizevent(event);
				bizeventAuditService.persistAuditData(event);
			}

			if (orderRep != null) {
				orderRep = orderRepairMaintenanceInformationStore.update(orderRep);
			}

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put(OrderEntity.class.getName(), orderEntity);
			map.put(OrderRepEntity.class.getName(), orderRep);
			map.put(ValidationException.class.getName(), ex);
			
			if (logDebugEnabled && (orderEntity != null)) {
				LOG.debug("Order (orderId= " + orderEntity.getOrderId() + ") updated successfully");
			}
			return map;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@AdnTrace
	public OrderItemEntity adjustReturnedOrderItemAttributes(OrderItemEntity orderItem,
			BigDecimal returnedQuantity, UomBsEntity returnedUom) {
		if (returnedQuantity != null && returnedUom != null) {
			orderItem.setReturnedQuantity(returnedQuantity);
			orderItem.setReturnedUom(orderItem.getLoadedUom());
			if (LogonPackageHandlingUtil.isPallet(orderItem) && 
					orderItem.getReturnedQuantity() != null &&
					orderItem.getReturnOrderReason() == null) {
				ReturnOrderReasonBsEntity defaultReturnReason = referentialDataService.findBsEntity(ReturnOrderReasonBsEntity.class,SalesDocumentDTO.ORDER_REASON_VOLUNTARY_RETURN);
				orderItem.setReturnOrderReason(defaultReturnReason);
			}
		}
		return orderItem;
	}

	@AdnTrace
	public OrderItemEntity adjustReturnedOrderItemAttributes(OrderItemEntity orderItem, 
			BigDecimal returnedQuantity, UomBsEntity returnedUom, ShippingpointEntity returnShippingPoint) {
		orderItem = adjustReturnedOrderItemAttributes(orderItem, returnedQuantity, returnedUom);
		
		ReturnOrderReasonBsEntity returnOrderReason = referentialDataService.findBsEntity(
				ReturnOrderReasonBsEntity.class, Constant.RETURN_ORDER_REASON_VOLUNTARY_RETURN);
		orderItem.setReturnOrderReason(returnOrderReason);
		
		ReturnMaterialUsageBsEntity returnMaterialUsage = referentialDataService.findBsEntity(
				ReturnMaterialUsageBsEntity.class, Constant.RETURN_MATERIAL_USAGE_REFILLING_VEHICLE_CD);
		orderItem.setReturnMaterialUsage(returnMaterialUsage);
		
		orderItem.setReturnShippingpoint(returnShippingPoint);
		
		PlantBsEntity returnDeliveryPlant = returnShippingPoint.getPlant();
		orderItem.setReturnPlant(returnDeliveryPlant);
		return orderItem;
	}
	
	/**
	 * Handles transmit to commercial carrier or transmit to pool depending on the flags provided.
	 * 
	 * @param order
	 * @param autoPoolAssignmentFlag
	 * @param autoCarrierAssignmentFlag
	 * @return
	 */
	private OrderEntity transmitToTransportLevel(OrderEntity order, Boolean autoPoolAssignmentFlag,
			Boolean autoCarrierAssignmentFlag) {
		Long poolId = (order.getPool() != null) ? order.getPool().getPoolId() : null; 
		Long commCarrierId = (order.getCommCarrier() != null) ? order.getCommCarrier().getCarrierId() : null; 
		
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd();
		if (orderStatusCd == Constant.ORDER_STATUS_NEW) {
		
			//Actual order update. Have to update the order because other services are called.
			order = orderService.mergeOrder(order);
			
			if (autoPoolAssignmentFlag) {
				// Pool checked -> state "transmitted to pool"
				poolAssignmentService.setOrderPool(order.getOrderNumber(), poolId, null);
			}
			
			if (autoCarrierAssignmentFlag) {
				// Both checked -> state "transmitted to carrier"
				poolAssignmentService.setOrderCarrier(order.getOrderNumber(), commCarrierId, null);
			}
		}
		else if ( (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) ||
				   (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) ) {
			
			if (autoCarrierAssignmentFlag) {
				//Actual order update. Have to update the order because other services are called.
				order = orderService.mergeOrder(order);
				// Both checked -> state "transmitted to carrier"
				poolAssignmentService.setOrderCarrier(order.getOrderNumber(), commCarrierId, null);
			}
		}
		return order;
	}

	
	/**
	 * Tries to update costs first. If updateCosts parameter is true, it will try to update the costs, but will give up the transaction, if it fails.
	 * If updateCost is false, it will update the frozen tables and the order, and will not update the costs. 
	 * 
	 * @param order
	 * @param autoPoolAssignmentFlag
	 * @param autoCarrierAssignmentFlag
	 * @param orderRep
	 */
	@AdnTrace
	public Map<String, Object> specialUpdateOrderAndOrderFinal(OrderEntity order,
			Boolean autoPoolAssignmentFlag, Boolean autoCarrierAssignmentFlag,
			OrderRepEntity orderRep, List<BindingsDTO> thirdPartyFreighters) {
		
		Map<String, Object> result = null;
		OrderFinalEntity finalOrder = null;
		List<LogonException> costExceptions = null;
		try {
			costExceptions = contributionCalcService.updateCosts(order);
			em.flush(); // order final needs this to be persisted because it uses a view on this data
		}
		catch (RuntimeException e) {
			String exceptionString = ExceptionUtil.exceptionString(e, null);
			LOG.error("Error when updating costs of order " + order.getOrderId() + " number: "
					+ order.getOrderNumber());
			LOG.error(exceptionString);
			throw new LogonException(Reason.RES_ERROR_COST_CALCULATION);
		}

		// LOGON-8295 Orders with status code 15(Delivered) might get inserted into the OrderFinal table.
		//This is not expected behaviour. To prevent this from happening, the status code is checked for 16(Completed).
		if (order.getOrderStatus().getOrderStatusCd() == Constant.ORDER_STATUS_COMPLETED) {
			//order finalization - save to the frozen tables
			try {
				finalOrder = frozenTablesService.freezeOrder(order);
			}
			catch (RuntimeException e) {
				String exceptionString = ExceptionUtil.exceptionString(e, null);
				LOG.error("Error when freezing order");
				LOG.error(exceptionString);
				throw new LogonException(Reason.RES_ERROR_FREEZING);
			}
		}
		final Map<String, Object> map = updateOrderAndCustorder(order, autoPoolAssignmentFlag,
				autoCarrierAssignmentFlag, orderRep, false, thirdPartyFreighters);
		if (map != null) {
			if (finalOrder != null) {
				map.put(OrderFinalEntity.class.getName(), finalOrder);
			}
			if (costExceptions != null && !costExceptions.isEmpty()) {
				map.put(Reason.class.getName(), costExceptions);
			}
		}
		return result = map;
	}
	
	public boolean isRailReturnOrRepairOrder(OrderEntity order) {
		// [[
		boolean result = false;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isRailReturnOrRepairOrder", params);
		}
		// ]]
		try {
			if (LogonUtil.isRailRepairOrder(order.getDeliveryType().getDeliveryTypeCd(), order.getDistributionDest().getDistributionDestCd())
					|| LogonUtil.isRailReturnOrder(order.getDeliveryType().getDeliveryTypeCd(), order.getDistributionDest().getDistributionDestCd())) {
				result = true;
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
	 * Updates the costs, and freezes the order if cost calculation was successful.
	 */
	public List<LogonException> updateCostsReFreezeOrder ( Long orderId ) throws LogonException {
			// [[
			List<LogonException> result = null;
			Object[][] params = {{"orderId", orderId}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "updateCostsReFreezeOrder", params);
			}
			// ]]
			try {
				final OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);				
				final List<LogonException> exceptions = contributionCalcService.updateCosts(order);
				
				em.flush();
				try {					
					//order finalization - save to the frozen tables - depending on the costs calculated before the	ReportFinishedCommFlag will be set accordingly
					frozenTablesService.freezeOrder( order );
				}
				catch (RuntimeException ex) {
					String exceptionString = ExceptionUtil.exceptionString(ex, null);
					LOG.error("Error when freezing order");
					LOG.error(exceptionString);
					throw new LogonException(Reason.RES_ERROR_FREEZING);
				}
				return result = exceptions;
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
	 * Returns a listrangeof damage codes.
	 */
	public ListRange loadDamageCodes(int skip, int num, String query, Locale locale) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadDamageCodes", params);
		}
		// ]]
		try {			
			
			final ReadOnlyDao<DamagecodeSearchEntity> searchDao = getDaoProvider().getDamagecodeSearchEntityDao();
			final Filter<DamagecodeSearchEntity> filt = searchDao.createFilter();
			
			filt.addExpression(Restrictions.or(
					Restrictions.ilike(DamagecodeSearchEntity.ATTR_DAMAGECODE_TXT, '%'+ query + '%'),
					Restrictions.ilike(DamagecodeSearchEntity.ATTR_TXT, '%' + query + '%')
			));
			filt.addExpression(Restrictions.eq(DamagecodeSearchEntity.ATTR_LANG_CD, locale.getLanguage()));
			filt.addExpression(Order.asc(DamagecodeSearchEntity.ATTR_DAMAGECODE_TXT));
			
			List<DamagecodeSearchEntity> damageCodes = searchDao.findNRecs(skip, num, filt);
			Long totalSize = searchDao.count(filt);

			ListRange res = new ListRange();
			res.setData(damageCodes.toArray());
			res.setTotalSize(totalSize);
			
			return result = res;
			
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
	 * Loads list of Bizevents for a given order
	 * @param orderId for which to load bizevents
	 * @param langCd language code (i.e. de, en)
	 * @adnlog.exclude return
	 */
	private List<BizeventOrderLogEntity> loadBizevents(Long orderId, String langCd) {
		return loadBizevents(orderId, langCd, null);
	}


	/**
	 * Loads list of Bizevents for a given order
	 * @param orderId for which to load bizevents
	 * @param langCd language code (i.e. de, en)
	 * @param orderBy
	 * @adnlog.exclude return
	 */
	public List<BizeventOrderLogEntity> loadBizevents(Long orderId, String langCd, 
			List<SortOrder> sortOrders) {
		return loadBizevents(null, null, sortOrders, orderId, langCd);
	}


	/**
	 * Loads list of Bizevents for a given order
	 * @param orderId for which to load bizevents
	 * @param langCd language code (i.e. de, en)
	 * @adnlog.exclude return
	 */
	public List<BizeventOrderLogEntity> loadBizevents(Integer skip, Integer num, List<SortOrder> sortOrders, Long orderId, String langCd) {
		// [[
		Object[][] params = {{"skip", skip},
							{"num", num},
							{"sortOrders", sortOrders},
							{"orderId", orderId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadBizevents", params);
		}
		// ]]
		
		try {
			Filter<BizeventOrderLogEntity> filter = getBizeventFilter(orderId, langCd);
			List<Order> orderByExps = DbUtils.convertSortOrdersToOrders(sortOrders);
			return runBizeventQuery(skip, num, orderByExps, filter);
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
	 * Calls find or findNrecs depending on the specified parameters.
	 * @param skip
	 * @param num
	 * @param orderByExpression
	 * @param filter
	 * @return
	 */
	private List<BizeventOrderLogEntity> runBizeventQuery(Integer skip, Integer num, List<Order> orderByExps, Filter<BizeventOrderLogEntity> filter) {
		// [[
		List<BizeventOrderLogEntity> result = null;
		Object[][] params = {{"skip", skip},
							{"num", num},
							{"orderByExps", orderByExps},
							{"filter", filter}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "runBizeventQuery", params);
		}
		// ]]
		try {
			List<BizeventOrderLogEntity> find = null;
			ReadOnlyDao<BizeventOrderLogEntity> bizeventDao = getDaoProvider().getBizeventOrderLogEntityDao();
			//run with the basic settings - query for the order and the active assignment
			if ( skip == null && num == null ) {
				
				for (Order ord : orderByExps) {
					filter.addExpression(ord);
				}
				find = bizeventDao.find(filter);
			}
			else {
				if (!orderByExps.isEmpty()) {
					for (Order ord : orderByExps) {
						filter.addExpression(ord);
					}
				}
				else {
					filter.addExpression(Order.desc(BizeventOrderLogEntity.ATTR_EVENT_TS));
					filter.addExpression(Order.desc(BizeventOrderLogEntity.ATTR_CRE_TS));
					filter.addExpression(Order.desc(BizeventOrderLogEntity.ATTR_BIZEVENT_ID));
				}
				
				find = bizeventDao.findNRecs(skip, num, filter);
			}
			return result = find;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	/** creates bizevent filter
	 * @param orderId for which to load bizevents
	 * @param langCd language code (i.e. de, en)
	 * @return
	 */
	private Filter<BizeventOrderLogEntity> getBizeventFilter(Long orderId, String langCd) {
		// [[
		Filter<BizeventOrderLogEntity> result = null;
		Object[][] params = {{"orderId", orderId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getBizeventFilter", params);
		}
		// ]]
		try {
			
			final Filter<BizeventOrderLogEntity> filter = new FilterImpl<BizeventOrderLogEntity>();
			
			filter.addExpression(Restrictions.eq(BizeventOrderLogEntity.ATTR_LANG_CD, langCd));
			filter.addExpression(Restrictions.not(
					Restrictions.eq(BizeventOrderLogEntity.ATTR_BIZEVENT_TYPE_CD, 
							Constant.BIZEVENT_TYPE_POSITION)));
			
			Long assignmentId = assignmentService.findActiveAssignmentIdForOrderId(orderId);
			if (assignmentId != null) {
				filter.addExpression(Restrictions.or(
						Restrictions.eq(BizeventOrderLogEntity.ATTR_BASE_ORDER_ID, orderId),
						Restrictions.and(
								Restrictions.eq(BizeventOrderLogEntity.ATTR_BASE_ASSIGNMENT_ID, assignmentId),
								Restrictions.isNull(BizeventOrderLogEntity.ATTR_BASE_ORDER_ID))));
			}
			else {
				filter.addExpression(Restrictions.eq(BizeventOrderLogEntity.ATTR_BASE_ORDER_ID, orderId));
			}
			
			return result = filter;
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
	 * Counts Bizevents for a given order
	 * @param orderId for which to count bizevents
	 * @adnlog.exclude return
	 */
	public long countBizevents(Long orderId, String langCd) {
		// [[
		Object[][] params = {{"orderId", orderId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countBizevents", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<BizeventOrderLogEntity> dao = 
					getDaoProvider().getBizeventOrderLogEntityDao();
			
			final Filter<BizeventOrderLogEntity> filter = getBizeventFilter(orderId, langCd);
			
			return dao.count(filter);
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
	 * Loads list of Bizevents for the truck log chart
	 * @param langCd language
	 * @param orderId for which to load bizevents
	 * @adnlog.exclude return
	 */
	public List<BizeventTruckLogEntity> loadTruckLog(Long orderId, String langCd) {
		// [[
		Object[][] params = {{"orderId", orderId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTruckLog", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<BizeventTruckLogEntity> dao = 
					getDaoProvider().getBizeventTruckLogEntityDao();
			final Filter<BizeventTruckLogEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(BizeventTruckLogEntity.ATTR_LANG_CD, langCd));
			filter.addExpression(Restrictions.isNotNull(BizeventTruckLogEntity.ATTR_SPEED));
			
			Long assignmentId = assignmentService.findActiveAssignmentIdForOrderId(orderId);
			if (assignmentId != null) {
				filter.addExpression(Restrictions.or(
						Restrictions.eq(BizeventTruckLogEntity.ATTR_ORDER_ID, orderId),
						Restrictions.eq(BizeventTruckLogEntity.ATTR_ASSIGNMENT_ID, assignmentId)));
			}
			else {
				filter.addExpression(Restrictions.eq(BizeventTruckLogEntity.ATTR_ORDER_ID, orderId));
			}
			
			//order by bizevent
			filter.addExpression(Order.desc(BizeventTruckLogEntity.ATTR_CRE_TS));
			
			return dao.find(filter);
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
	 * Loads list of order item margins for the commercial tab
	 * @param orderId for which to load margins
	 */
	public List<OrderItemMarginEntity> loadMarginList(Long orderId) {
		// [[
		List<OrderItemMarginEntity> result = null;
		Object[][] params = {{"orderId", orderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMarginList", params);
		}
		// ]]
		try {
			List<OrderItemMarginEntity> orderItemMargins = orderItemMarginStore.findByOrderId(orderId);
			return result = orderItemMargins;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public boolean isLoadMappedToExactlyOneOrderItem(Long loadId) {
		// [[
		boolean result = false;
		Object[][] params = {{"loadId", loadId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMappedToOneOrdetItemOnly", params);
		}
		// ]]
		try {
			List<OrderItemEntity> orderItems = orderItemStore.findByLoadId(loadId);
			return result = orderItems.size() == 1;
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
	 * Loads Order repair/maintenance for a given order  
	 * @param orderId - unique id used for the lookup
	 */
	public OrderRepEntity loadOrderRep(Long orderId) {
		// [[
		OrderRepEntity result = null;
		Object[][] params = {{"orderId", orderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrderRep", params);
		}
		// ]]
		try {
			OrderRepEntity orderRep = orderRepairMaintenanceInformationStore.findAndIntializeByOrderId(orderId);
			return result = orderRep;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public List<MaintTypeTxEntity> getMaintTypeTxList(Locale locale) {
		// [[
		List<MaintTypeTxEntity> result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMaintTypeTxList", params);
		}
		// ]]
		try {
			
			final Filter<MaintTypeTxEntity> filter = new FilterImpl<MaintTypeTxEntity>();
			filter.addExpression(Restrictions.eq("maintTypeTxPK.langCd", locale.getLanguage()));
			filter.addExpression(Order.ascCaseInsensitive("maintTypeTxPK.maintTypeBs.maintTypeCd"));
			
			return result = referentialDataService.findTxEntityFiltered(MaintTypeTxEntity.class, filter);
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
	 * Returns a tx list of damage codes for the bs entities in damageCodeBsList.
	 * @param locale
	 * @param repDamageCodeTypeList
	 * @return
	 */
	public List<DamagecodeTxEntity> getDamageCodeTxList(Locale locale, List<OrderRepDmgcodeTypeEntity> repDamageCodeTypeList) {
		// [[
		List<DamagecodeTxEntity> result = null;
		Object[][] params = {{"locale", locale},
							{"repDamageCodeTypeList", repDamageCodeTypeList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDamageCodeTxList", params);
		}
		// ]]
		try {
			if (repDamageCodeTypeList.isEmpty()) {
				return result = new ArrayList<DamagecodeTxEntity>();
			}
			final Filter<DamagecodeTxEntity> filter = new FilterImpl<DamagecodeTxEntity>();
			filter.addExpression(Restrictions.eq("damagecodeTxPK.langCd", locale.getLanguage()));

			final List<Long> ids = new ArrayList<Long>();
			for (OrderRepDmgcodeTypeEntity bs : repDamageCodeTypeList) {
				ids.add(bs.getDmgcodeType().getDamagecodeCd());
			}
			filter.addExpression(Restrictions.in("damagecodeTxPK.damagecodeBs.damagecodeCd", ids));
			filter.addExpression(Order.ascCaseInsensitive(DamagecodeTxEntity.ATTR_TXT));
			return result = referentialDataService.findTxEntityFiltered(DamagecodeTxEntity.class, filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<FireqMaterialMixdesignEntity> fetchFireqMatMixdesignBySapMatId(Long sapMatId) {
		// [[
		List<FireqMaterialMixdesignEntity> result = null;
		Object[][] params = {{"sapMatId", sapMatId}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "fetchFireqMatMixdesignBySapMatId", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<FireqMaterialMixdesignEntity> dao = getDaoProvider().getFireqMaterialMixdesignEntityDao();
			final Filter<FireqMaterialMixdesignEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(FireqMaterialMixdesignEntity.ATTR_SAP_MATERIAL_ID, sapMatId));
			return result = dao.find(filter);
		}
		finally {
		// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public FireqMaterialMixdesignEntity fetchMixDesignById(Long mixDesignId) {
		// [[
		FireqMaterialMixdesignEntity result = null;
		Object[][] params = {{"mixDesignId", mixDesignId}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "fetchMixDesignById", params);
		}
		// ]]
		try {

			final ReadOnlyDao<FireqMaterialMixdesignEntity> dao = getDaoProvider().getFireqMaterialMixdesignEntityDao();
			final Filter<FireqMaterialMixdesignEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(FireqMaterialMixdesignEntity.ATTR_MIX_DESIGN_ID, mixDesignId ));
			
			return dao.findSingle(filter);
		}
		finally {
		// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Fetches a List of all MixDesigns which belong to the given Production plant and belong to a given SapMaterial
	 * @param plantCd
	 * @param sapMaterialId
	 * @return
	 */
	public List<FireqMaterialMixdesignEntity> fetchMixDesignList(String plantCd, Long sapMaterialId) {
		// [[
		List<FireqMaterialMixdesignEntity> result = null;
		Object[][] params = {{"plantCd", plantCd},
							{"sapMaterialId", sapMaterialId}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "fetchMixDesignList", params);
		}
		// ]]
		try {

			final ReadOnlyDao<FireqMaterialMixdesignEntity> dao = getDaoProvider().getFireqMaterialMixdesignEntityDao();
			final Filter<FireqMaterialMixdesignEntity> filter = dao.createFilter();
			
			if (plantCd != null) {
				filter.addExpression(Restrictions.eq(FireqMaterialMixdesignEntity.ATTR_PLANT_CD, plantCd));
			}
			if (sapMaterialId != null) {
				filter.addExpression(Restrictions.eq(FireqMaterialMixdesignEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			}
			
			return dao.find(filter);			
		}
		finally {
		// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * @see MaterialEnhancementsServiceLocalInterface#replaceMaterialInItem(OrderEntity, Long, Long)
	 */
	public OrderEntity replaceMaterialInItem(OrderEntity order, Long orderItemId, Long sapContractItemId) {
		return materialEnhancementsService.replaceMaterialInItem(order, orderItemId, sapContractItemId);
	}
	
	/**
	 * @see MaterialEnhancementsServiceLocalInterface#updateMaterialAttributes(OrderEntity, Long, PlantBsEntity, UomBsEntity, BigDecimal)
	 */
	public OrderEntity updateMaterialAttributes(OrderEntity order, Long orderItemId, 
			PlantBsEntity prodPlant, UomBsEntity uomBs, BigDecimal quantity) {
		return materialEnhancementsService.updateMaterialAttributes(order, orderItemId, prodPlant, uomBs, quantity);
	}
	
	
	/**
	 * Returns the list of Partner Function Codes to be displayed in the PFVendor Popup in the 
	 * Third Party Freighters popup
	 */
	public List<String> fetchPartnerFunctionCodeList() {
		ReadOnlyDao<SapTpSapVendorPfBsEntity> sapTpVendorPfBsDao = getDaoProvider().getSapTpSapVendorPfBsEntityDao();
		
		// filter out inactive codes
		Filter<SapTpSapVendorPfBsEntity> filter = sapTpVendorPfBsDao.createFilter();
		filter.addExpression(Restrictions.eq(SapTpSapVendorPfBsEntity.ATTR_CTL_ACT_CD, true));
		
		JPAQuery<SapTpSapVendorPfBsEntity> jpaQuery = sapTpVendorPfBsDao.createJPAQuery();
		jpaQuery.setFilter(filter);
		jpaQuery.addSelect(SelectExpression.simpleSelect(SapTpSapVendorPfBsEntity.ATTR_SAP_TP_SAP_VENDOR_PF_CD));
		
		@SuppressWarnings("unchecked")
		List<String> result = sapTpVendorPfBsDao.find(jpaQuery);
		return result;
	}

	/**
	 * Returns the SAP Vendor List
	 */
	public ListRange fetchSapVendorList(int skip, int num, String query, Locale locale) {
		ReadOnlyDao<SapVendorEntity> sapVendorDao = getDaoProvider().getSapVendorEntityDao();
		
		Filter<SapVendorEntity> filter = sapVendorDao.createFilter();
		
		filter.addExpression(Restrictions.eq(SapVendorEntity.ATTR_LANG_CD, locale.getLanguage()));
		
		filter.addExpression(Restrictions.or(
				Restrictions.ilike(SapVendorEntity.ATTR_NAME1, "%" + query + "%"), 
				Restrictions.ilike(SapVendorEntity.ATTR_SAP_ID, "%" + query + "%")
		));
		
		filter.addExpression(Restrictions.neq(SapVendorEntity.ATTR_MARKED_FOR_DELETION, Constant.MARKED_FOR_DELETION));
		
		filter.addExpression(Order.asc(SapVendorEntity.ATTR_NAME1));
		filter.addExpression(Order.asc(SapVendorEntity.ATTR_SAP_VENDOR_ID));
		
		JPAQuery<SapVendorEntity> jpaQuery = sapVendorDao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.simpleSelect(SapVendorEntity.ATTR_SAP_VENDOR_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(SapVendorEntity.ATTR_NAME1));
		jpaQuery.addSelect(SelectExpression.simpleSelect(SapVendorEntity.ATTR_SAP_ID));
		jpaQuery.setFilter(filter);
		
		@SuppressWarnings("unchecked")
		List<Object[]> rows = sapVendorDao.findNRecs(skip, num, jpaQuery);
		List<SapVendorDTO> vendorList = new ArrayList<SapVendorDTO>();
		
		for (Object[] row : rows) {
			Long id = (Long) row[0];
			String name = (String) row[1];
			String sapId = (String) row[2];
			
			SapVendorDTO vendor = new SapVendorDTO(id,sapId,name);
			vendorList.add(vendor);
		}
		
		ListRange result = new ListRange();
		result.setData(vendorList.toArray());
		result.setTotalSize(sapVendorDao.count(filter));
		
		return result;
	}


/**
	 * Finds the {@link OrderTpSapVendorEntity}s belongs to a specific order
	 * @param order the {@link OrderEntity}
	 * @return list of  {@link OrderTpSapVendorEntity}s (or empty list), never null.
	 */
	public List<BindingsDTO> getExistingBindings(OrderEntity order) {
		// [[
		Object[][] params = { { "order", order } };

		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExistingBindings", params);
		}
		// ]]
		try {
			Validate.notNull(order, "argument 'order' should not be null!");
	
			final ReadWriteDao<OrderTpSapVendorEntity> dao = getDaoProvider().getOrderTpSapVendorEntityDao();
			final Filter<OrderTpSapVendorEntity> filter = dao.createFilter();
			// filter the entities which belongs to the specified order id
			filter.addExpression(Restrictions.eq(OrderTpSapVendorEntity.ATTR_ORDER_ID,
					order.getOrderId()));

			List<OrderTpSapVendorEntity> orderTpSapVendorEntityList = dao.find(filter);
			
			List<BindingsDTO> result = OrderDetailsUtil.createBindingsList(orderTpSapVendorEntityList);
			
			Collections.sort(result, BindingsDTO.getComparator());
			
			return result;
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
	 * Loads the TpSapVendorSearchEntity-ies based on the provided parameters.
	 * Search for 
	 * @param start start index 
	 * @param count number of items to retrieve
	 * @param searchFilter the expression the user has typed in 
	 */
	public ListRange fetchAvailableBindings(int start, int count, String searchFilter) {
		// [[
		Object[][] params = { { "start", start }, { "count", count },
				{ "searchFilter", searchFilter } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAvailableBindings", params);
		}
		// ]]
		try {

			final ReadOnlyDao<TpSapVendorSearchEntity> dao = getDaoProvider().getTpSapVendorSearchEntityDao();
			final Filter<TpSapVendorSearchEntity> filter = dao.createFilter();

			filter.addExpression(Order.asc(TpSapVendorSearchEntity.ATTR_NAME1));
			filter.addExpression(Order.asc(TpSapVendorSearchEntity.ATTR_SAP_TP_SAP_VENDOR_PF_CD));
			
			if (searchFilter != null && searchFilter.length() > 0) {

				filter.addExpression(Restrictions.or(
						Restrictions.ilike(TpSapVendorSearchEntity.ATTR_SAP_TP_SAP_VENDOR_PF_CD, "%"+searchFilter+"%"),
						Restrictions.ilike(TpSapVendorSearchEntity.ATTR_SAP_IFC_ID, "%"+searchFilter+"%"),
						Restrictions.ilike(TpSapVendorSearchEntity.ATTR_NAME1, "%"+searchFilter+"%"))
				);
			}

			List<TpSapVendorSearchEntity> tpSapVendorSearchList = dao.findNRecs(start, count, filter);

			List<BindingsDTO> availableList = new ArrayList<BindingsDTO>();
			
			for (TpSapVendorSearchEntity tpSapVendorSearchEntity : tpSapVendorSearchList) {

				BindingsDTO ab = BindingsDTO.createBindingsDTO(tpSapVendorSearchEntity);
				availableList.add(ab);
			}
			

			ListRange listRange = new ListRange();
			listRange.setData(availableList.toArray());
			listRange.setTotalSize(dao.count(filter));

			return listRange;
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
	 * Saves the bindings for the order
	 * @param order The {@link OrderEntity}, should be non-null
	 * @param webBindings list of {@link BindingsDTO} modified by the user on the web interface. 
	 * Can be null (in this case the method returns without any modification)
	 */
	public void saveBindings(OrderEntity order, List<BindingsDTO> webBindings) {
		// [[
		Object[][] params = {  { "bindings", webBindings }};

		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveBindings", params);
		}
		// ]]
		try {
			if (webBindings == null) {
				return;
			}
			
			Validate.notNull(order, "argument 'order' should not be null!");

			// Bindings exist on the DB for the Order
			List<BindingsDTO> dbBindings = getExistingBindings(order);
			
			// Bindings to remove ( db binding - web bindings)
			List<BindingsDTO> bindingsToRemove = new ArrayList<BindingsDTO>(dbBindings);
			bindingsToRemove.removeAll(webBindings);
			
			ReadWriteDao<OrderTpSapVendorEntity> dao = getDaoProvider().getOrderTpSapVendorEntityDao();
			
			for (BindingsDTO bindingToRemove: bindingsToRemove) {
				
				Long tpSapVendorid = bindingToRemove.getId();
				
				Filter<OrderTpSapVendorEntity> filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(OrderTpSapVendorEntity.ATTR_ORDER_ID, order.getOrderId()));
				filter.addExpression(Restrictions.eq(OrderTpSapVendorEntity.ATTR_TP_SAP_VENDOR_ID, tpSapVendorid));
				
				OrderTpSapVendorEntity entity = dao.findSingle(filter);
				
				if (entity != null) {
					dao.remove(entity);
				}
				else {
					if (logDebugEnabled) {
					LOG.warn("Can't find TpSapVendorEntity with orderId = " + order.getOrderId() + " tpSapVendorid = " + tpSapVendorid + " for deletion!");
				}}
			}
			
			// Bindings to create (web bindings - db bindings)
			List<BindingsDTO> bindingsToCreate = new ArrayList<BindingsDTO>(webBindings);
			bindingsToCreate.removeAll(dbBindings);

			ReadWriteDao<TpSapVendorEntity> tpSapVendorDao = getDaoProvider().getTpSapVendorEntityDao();
			
			for (BindingsDTO bindingToCreate: bindingsToCreate) {
				
				Long id = bindingToCreate.getId();
				
				TpSapVendorEntity tpSapVendorEntity = tpSapVendorDao.findById(id);
				
				if (tpSapVendorEntity != null) {
					OrderTpSapVendorEntity entity = new OrderTpSapVendorEntity();
					entity.setOrder(order);
					entity.setTpSapVendor(tpSapVendorEntity);
					dao.persist(entity);
				}
				else {
					if (logDebugEnabled) {
					LOG.warn("Can't find TpSapVendorEntity with Id = " + id + " for creating an OrderTpSapVendorEntity!");
				}}
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
	 * Updates the list of bindings when the user adds/removes a binding on the web interface
	 * @param tpSapVendorId id of the {@link TpSapVendorEntity}
	 * @param itemWasAdded true if a new item was added, false if item was removed
	 * @param existingBindings the list of bindings (stored in the session) to be modified.
	 * 
	 */
	public List<BindingsDTO>  updateExistingBindings(Long tpSapVendorId, Boolean itemWasAdded,
			List<BindingsDTO> existingBindings) {
		// [[
		Object[][] params = {  { "tpSapVendorId", tpSapVendorId }, 
				{ "itemWasAdded", itemWasAdded }, { "existingBindings", existingBindings } };

		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateExistingBindings", params);
		}
		// ]]
		try {
			
			Validate.notNull(tpSapVendorId, "argument 'tpSapVendorId' should not be null");
			Validate.notNull(itemWasAdded, "argument 'itemWasAdded' should not be null");
			Validate.notNull(existingBindings, "argument 'existingBindings' should not be null");
			

			if (itemWasAdded) {
				
				boolean alreadyInExistingBindings = OrderDetailsUtil.isBindingExists(tpSapVendorId, existingBindings);
				
				if (!alreadyInExistingBindings) {
					
					TpSapVendorEntity tpSapVendorEntity = getDaoProvider().getTpSapVendorEntityDao().findById(tpSapVendorId);
							
					BindingsDTO binding = BindingsDTO.createBindingsDTO(tpSapVendorEntity);
					
					existingBindings.add(binding);
					
					// sort items
					Collections.sort(existingBindings, BindingsDTO.getComparator());
				}
				
			}
			else {
				int index = OrderDetailsUtil.findItemById(tpSapVendorId, existingBindings);
				if (index != -1) {
					existingBindings.remove(index);
				}
			}
			
			return existingBindings;

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
	 * Saves the PF and Vendor assignment into TLGNC_TP_SAP_VENDOR.
	 * If the assignment already exists, does not create another record
	 * @param pfCode the PF code
	 * @param vendorId the ID of the SAP_VENDOR
	 */
	public void saveVendorPf(String pfCode, Long vendorId) {
		// [[
		Object[][] params = {  
				{ "pfCode", pfCode }, 
				{ "vendorId", vendorId }};

		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateExistingBindings", params);
		}
		// ]]
		try {
			
			Validate.notNull(pfCode, "argument 'pfCode' should not be null");
			Validate.notNull(vendorId, "argument 'vendorId' should not be null");

			ReadWriteDao<SapTpSapVendorPfBsEntity> pfDao = getDaoProvider().getSapTpSapVendorPfBsEntityDao();
			SapTpSapVendorPfBsEntity pfEntity = pfDao.findById(pfCode);
			
			ReadWriteDao<SapVendorEntity> sapVendorDao = getDaoProvider().getSapVendorEntityDao();
			SapVendorEntity sapEntity = sapVendorDao.findById(vendorId);
			
			if (pfEntity != null) {
				if (sapEntity != null) {
					// find an TpSapVendor with the two entities
					ReadWriteDao<TpSapVendorEntity> tpSapDao = getDaoProvider().getTpSapVendorEntityDao();
					Filter<TpSapVendorEntity> f = tpSapDao.createFilter();
					f.addExpression(Restrictions.eq(TpSapVendorEntity.ATTR_SAP_TP_SAP_VENDOR_PF_CD, pfCode));
					f.addExpression(Restrictions.eq(TpSapVendorEntity.ATTR_SAP_VENDOR_ID, vendorId));
					
					if (tpSapDao.find(f).size() == 0) {
						// create new TpSapVendorEntity
						TpSapVendorEntity tpSapVendorEntity = new TpSapVendorEntity();
						tpSapVendorEntity.setActiveFlag(Boolean.TRUE);
						tpSapVendorEntity.setSapTpSapVendorPf(pfEntity);
						tpSapVendorEntity.setSapVendor(sapEntity);
						
						tpSapDao.persist(tpSapVendorEntity);
					}
					else {
						if (logDebugEnabled) {
						LOG.warn("TpSapVendorEntity with pfCode = '" + pfCode + "' and vendorId = '" + vendorId + "' already exists, creation skipped.");
					}}
					
					
				}
				else {
					if (logDebugEnabled) {
					LOG.warn("SapVendorEntity with id = " + vendorId + " was not found!");
				}}
				
			}
			else {
				if (logDebugEnabled) {
				LOG.warn("SapTpSapVendorPfBsEntity with id = " + pfCode + " was not found!");
			}}
			
			
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}

		
	}
	
	public void saveSendToSapFlag(OrderEntity order) {
		orderService.mergeOrder(order);
		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, Constant.BIZEVENT_TYPE_FORCE_TO_SEND_TO_SAP));
		event.setEventTs(new Date());
		event.setOrder(order);
		monitoringTablesService.processBizevent(event);
		bizeventAuditService.persistAuditData(event);
	}

	public void saveItrackStatusChange(OrderEntity order) {
		orderService.mergeOrder(order);
		BizeventEntity event = new BizeventEntity();
		event.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, Constant.BIZEVENT_TYPE_EDIT_FIELD));
		event.setEventTs(new Date());
		event.setOrder(order);
		bizeventAuditService.persistAuditData(event);
	}

}


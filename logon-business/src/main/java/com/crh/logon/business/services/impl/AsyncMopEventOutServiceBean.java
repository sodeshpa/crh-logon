/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.commons.lang.ObjectUtils;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.beans.LogonLoadingPoint;
import com.crh.logon.beans.LogonOrderItem;
import com.crh.logon.beans.LogonRfidObject;
import com.crh.logon.beans.LogonRfidTag;
import com.crh.logon.business.persistence.dtos.LogonMessage;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.dispatchers.AsyncMopEventOutServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderChangeServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PdaSessionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.MopEventUtils;
import com.crh.logon.business.services.utils.UOMConverter;
import com.crh.logon.business.stores.domain.MopeventDataOutStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.eventbeans.LogonEventOutBeanFactory;
import com.crh.logon.eventbeans.LogonMessageOutBean;
import com.crh.logon.eventbeans.in.LogonEventRequestFactory;
import com.crh.logon.eventbeans.mop.CancelTour;
import com.crh.logon.eventbeans.mop.LoadOrder;
import com.crh.logon.eventbeans.mop.LoadingEnded;
import com.crh.logon.eventbeans.mop.LoadingStarted;
import com.crh.logon.eventbeans.mop.PostOrder;
import com.crh.logon.eventbeans.mop.PreOrder;
import com.crh.logon.eventbeans.mop.ReadyForOperation;
import com.crh.logon.eventbeans.mop.Setup;
import com.crh.logon.eventbeans.mop.TruckComposition;
import com.crh.logon.eventbeans.mop.TruckLoad;
import com.crh.logon.eventbeans.mop.UnloadOrder;
import com.crh.logon.persistence.entities.admin.RoleFuncEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MopeventDataOutEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderOutEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.LangBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeFormat;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class AsyncMopEventOutServiceBean extends AsyncMopEventOutServiceDispatcher implements AsyncMopEventOutServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AsyncMopEventOutServiceBean.class);
	
	private static final List<AssignmentPartEntity> NO_MORE_RELEVANT_ASSIGNMENT_PART = null;

	private static final String EMPTY = "";
	
	@EJB private MopeventDataOutStoreInterface mopDataOutStore;
	
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private OrderChangeServiceLocalInterface orderChangeService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private PdaSessionServiceLocalInterface pdaSessionService;
	@EJB private DispokarteServiceLocalInterface dispokarteService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;
	@EJB private TimeZoneServiceLocalInterface timeZoneService;
	
	
	private LogonRfidObject getLogonRfidObject(Long objectID, String name,
			RfidobjectEntity rfidObject) {

		LogonRfidObject item = new LogonRfidObject();
		item.setObjectID(String.valueOf(objectID));
		item.setObjectName(name);
		List rfidTagList = new ArrayList();
		if (rfidObject != null) {
			List rfidtags = rfidObject.getRfids();
			if (rfidtags != null) {
				for (int i = 0; i < rfidtags.size(); i++) {
					RfidtagEntity rfidtag = (RfidtagEntity) rfidtags.get(i);
					LogonRfidTag rfid = new LogonRfidTag();
					rfid.setRfidIdentity(rfidtag.getRfidIdentity());
					rfid.setRfidLabel(rfidtag.getName());
					rfidTagList.add(rfid);
				}
			}
		}
		item.setRfidTagList(rfidTagList);

		return item;
	}

	private List<LogonLoadingPoint> convertLoadingpoints(List<LoadingpointEntity> loadingpoints) {
		// [[
		List<LogonLoadingPoint> result = null;
		Object[][] params = { { "loadingpoints", loadingpoints } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertLoadingpoints", params);
		}
		// ]]
		try {
			if (loadingpoints != null) {
				result = new ArrayList<LogonLoadingPoint>();
				for (LoadingpointEntity loadingpoint : loadingpoints) {
					LogonLoadingPoint logonLp = new LogonLoadingPoint();
					logonLp.setRfidObject(getLogonRfidObject(loadingpoint.getLoadingpointId(),
							loadingpoint.getLoadingpointObjectLabel(), loadingpoint.getRfidobject()));
					logonLp.setWeighingType(getWeighingType(loadingpoint));
					result.add(logonLp);
				}
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

	private List convertShipToSilos(List shiptoSilos) {
		// [[
		List result = null;
		Object[][] params = { { "shiptoSilos", shiptoSilos } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertShipToSilos", params);
		}
		// ]]
		try {
			if (shiptoSilos != null) {
				result = new ArrayList();
				for (Iterator it = shiptoSilos.iterator(); it.hasNext();) {
					ShiptoSiloEntity shipToSilo = (ShiptoSiloEntity) it.next();
					result.add(getLogonRfidObject(shipToSilo.getShiptoSiloId(),
							shipToSilo.getSiloName(), shipToSilo.getRfidobject()));
				}
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

	private List convertContainers(List containers) {
		// [[
		List result = null;
		Object[][] params = { { "containers", containers } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertContainers", params);
		}
		// ]]
		try {
			if (containers != null) {
				result = new ArrayList();
				for (Iterator it = containers.iterator(); it.hasNext();) {
					ContainerEntity container = (ContainerEntity) it.next();
					result.add(getLogonRfidObject(container.getContainerId(),
							container.getContainerNo(), container.getRfidobject()));
				}
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

	private List convertWagons(List wagons) {
		// [[
		List result = null;
		Object[][] params = { { "wagons", wagons } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertWagons", params);
		}
		// ]]
		try {
			if (wagons != null) {
				result = new ArrayList();
				for (Iterator it = wagons.iterator(); it.hasNext();) {
					WagonEntity wagon = (WagonEntity) it.next();
					result.add(getLogonRfidObject(wagon.getWagonId(), wagon.getWagonNo(),
							wagon.getRfidobject()));
				}
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

	private String formatQuantity(BigDecimal quantity, UomBsEntity uom, Locale locale) {

		StringBuilder desc = null;
		if ((quantity == null) && (uom == null)) {

			return null;
		}
		if ((quantity != null) && (uom != null) && (uom.getUomCd().equals(Constant.UOM_TONS))) {
			// PDA prefers kg

			return quantity.multiply(Constant.TON_TO_KG) + " kg";
		}

		if (quantity != null) {
			desc = new StringBuilder();
			desc.append(quantity.toString());
		}
		if (uom != null) {
			UomTxEntity uomTx = referentialDataService.findTxEntity(UomTxEntity.class, locale,
					uom);
			if (desc == null) {
				desc = new StringBuilder();
			}
			else {
				desc.append(" ");
			}
			desc.append(uomTx.getTxt());
		}

		return desc != null ? desc.toString() : null;
	}

	private List convertUnloadOrderItems(List unloadOrderItems, ShiptoEntity shipto,
			AssignmentPartEntity ap, Locale locale) {
		// [[
		List<LogonOrderItem> result = null;
		Object[][] params = { { "unloadOrderItems", unloadOrderItems }, { "shipto", shipto },
				{ "ap", ap }, { "locale", locale } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertUnloadOrderItems", params);
		}
		// ]]
		try {
			if (unloadOrderItems != null) {
				result = new ArrayList<LogonOrderItem>();
				for (Iterator it = unloadOrderItems.iterator(); it.hasNext();) {
					OrderItemEntity orderItem = (OrderItemEntity) it.next();
					LogonOrderItem logonOrderItem = new LogonOrderItem();
					logonOrderItem.setOrderItemID(String.valueOf(orderItem.getOrderItemId()));
					logonOrderItem.setOrderedQuantity(formatQuantity(
							orderItem.getOrderedQuantity(), orderItem.getOrderedUom(), locale));
					logonOrderItem.setLoadedQuantity(formatQuantity(orderItem.getLoadedQuantity(),
							orderItem.getLoadedUom(), locale));
					List shiptoSilos = getShiptoSilos(orderItem, shipto);
					logonOrderItem.setShipToSiloList(convertShipToSilos(shiptoSilos));
					if ((orderItem.getLoad() != null)
							&& (orderItem.getLoad().getChamberNumber() != null)) {
						logonOrderItem.setChamber(orderItem.getLoad().getChamberNumber().toString());
					}
					logonOrderItem.setLastEvent(getLastEvent(ap));

					SapMaterialDescriptionEntity matDesc = dispokarteService.fetchSapMaterialDescription(
							orderItem.getSapMaterial().getSapMaterialId(), locale.getLanguage());
					String materialName = matDesc.getTxt();
					logonOrderItem.setMaterial(materialName);
					
					boolean returnable = LogonPackageHandlingUtil.isPallet(orderItem)
							&& !LogonUtil.isReturnPalletOnly(orderItem.getOrder());
					logonOrderItem.setReturnable(returnable);

					// 05-jan/toan: add load assignment part id for unload order items list so we can know which unload order is belong to which load part
					logonOrderItem.setLoadAssignmentPartID(getLoadAssignmentPartId(
							orderItem.getLoad().getLoadId(), ap));

					result.add(logonOrderItem);
				}
			}
			else if (ap.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_EXTERNAL) {
				result = new ArrayList<LogonOrderItem>();
				LogonOrderItem logonOrderItem = new LogonOrderItem();
				logonOrderItem.setOrderItemID(String.valueOf(ap.getAssignmentPartId()));
				logonOrderItem.setOrderedQuantity(formatQuantity(
						ap.getAssignment().getMaterialQuantity(),
						ap.getAssignment().getMaterialUom(), locale));
				logonOrderItem.setLastEvent(getLastEvent(ap));
				logonOrderItem.setMaterial(ap.getAssignment().getMaterialName());

				result.add(logonOrderItem);
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
	 * Get load assignment part id based on loadId from assignment part entity
	 * @param loadId the load id
	 * @param ap the assignment apart entity
	 */
	private String getLoadAssignmentPartId(Long loadId, AssignmentPartEntity ap) {
		List<AssignmentPartEntity> assignmentParts = ap.getAssignment().getAssignmentParts();
		for (AssignmentPartEntity assignmentPartEntity : assignmentParts) {
			if ((loadId != null) && (assignmentPartEntity != null)
					&& (assignmentPartEntity.getLoad() != null)
					&& (loadId.equals(assignmentPartEntity.getLoad().getLoadId()))) {
				return String.valueOf(assignmentPartEntity.getAssignmentPartId());
			}
		}

		return EMPTY;
	}

	private List<LoadingpointEntity> getLoadingpoints(Collection<OrderItemEntity> orderItems) {
		List<LoadingpointEntity> loadingpoints = null;
		LoadingpointEntity singleLp = null;
		for (OrderItemEntity orderItem : orderItems) {
			OrderEntity order = orderItem.getOrder();
			if (orderItem.getBimodalRailOrderItem() != null) {
				// no loadingpoints but wagon
				return null;
			}
			if ((orderItem.getLoad() != null) && (orderItem.getLoad().getLoadingpoint() != null)) {
				singleLp = findLoadingPoint(orderItem.getLoad().getLoadingpoint());
				break;
			}
			if (orderItem.getLoadingpoint() != null) {
				if (singleLp == null) {
					singleLp = findLoadingPoint(orderItem.getLoadingpoint());
				}
				else if (!singleLp.getLoadingpointId().equals(
						orderItem.getLoadingpoint().getLoadingpointId())) {
					// Multiple order items have different LPs set.
					// It's up to the driver to figure out where to go.

					return null;
				}
			}
			else {
				AssignmentPartEntity loadPart = orderItem.getLoad() != null
						&& orderChangeService.findLoadAssignmentPartForLoad(
								orderItem.getLoad()) != null ? orderChangeService.findLoadAssignmentPartForLoad(
						orderItem.getLoad())
						: null;
				if (loadPart != null
						&& loadPart.getShippingpoint() != null
						&& !loadPart.getShippingpoint().getShippingpointId().equals(
								order.getShippingpoint().getShippingpointId())) {
					//in case of the bag stock shippingpoint, the loadingpoint does not have to have a material restriction, as far SAP will use the pricing condition of the original SP
					singleLp = orderService.getSingleLoadingpointForShippingpointWithRFId(
							loadPart.getShippingpoint());
				}
				else {
					List<LdpointMatRestrEntity> materialRestrictionList = orderChangeService.fetchLoadingPointsWithRFIds(
							orderItem.getOrder().getShippingpoint().getShippingpointId(),
							orderItem.getSapMaterial().getSapMaterialId());
					if ((singleLp != null) && (!materialRestrictionList.contains(singleLp))) {
						// There's an other item which defines a LP which is not capable of loading this order item
						// It's up to the driver to figure out where to go.

						return null;
					}
					if (loadingpoints == null) {
						// Find all LP suitable for the 1st order item
						loadingpoints = new ArrayList<LoadingpointEntity>();
						if (materialRestrictionList != null) {
							for (LdpointMatRestrEntity matrestrEntity : materialRestrictionList) {
								loadingpoints.add(matrestrEntity.getLoadingpoint());
							}
						}
					}
					else {
						// See if the LP which are suiteable for earlier order items are suitable for this one,
						// if not, remove from list.
						for (Iterator<LoadingpointEntity> loadingpointIterator = loadingpoints.iterator(); loadingpointIterator.hasNext();) {
							LoadingpointEntity loadingPoint = (LoadingpointEntity) loadingpointIterator.next();
							boolean loadingPointMaterialRestrictionExists = false;
							for (LdpointMatRestrEntity loadingPointMaterialRestriction : materialRestrictionList) {
								LoadingpointEntity loadingPointOfRestriction = loadingPointMaterialRestriction.getLoadingpoint();
								if (loadingPointOfRestriction.getLoadingpointId().equals(loadingPoint.getLoadingpointId())) {
									loadingPointMaterialRestrictionExists = true;
									break;
								}
							}
							if (!loadingPointMaterialRestrictionExists) {
								loadingpointIterator.remove();
							}
						}
					}
				}
			}
		}
		if (singleLp != null) {
			// A single LP determines where to load
			loadingpoints = new ArrayList<LoadingpointEntity>();
			loadingpoints.add(singleLp);
		}

		return loadingpoints;
	}

	/**
	 * Attach the given loadingpoint entity to the PersistenceContext
	 * @param loadingpoint
	 * @return
	 */
	private LoadingpointEntity findLoadingPoint(LoadingpointEntity loadingpoint) {
		// [[
		LoadingpointEntity result = null;
		Object[][] params = { { "loadingpoint", loadingpoint } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findLoadingPoint", params);
		}
		// ]]
		try {
			LoadingpointEntity loadingPoint = em.find(LoadingpointEntity.class,
					loadingpoint.getLoadingpointId());

			return result = loadingPoint;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private ShiptoEntity getShipto(OrderEntity order) {

		SapBpEntity customer = order.getShiptoSapBp();

		ShiptoEntity shipto = null;
		if (customer != null) {
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP, customer));
			filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			shipto = getDaoProvider().getShiptoEntityDao().findSingle(filter);
		}

		return shipto;
	}

	private List getShiptoSilos(OrderItemEntity orderItem, ShiptoEntity shipto) {

		List shiptoSilos = new ArrayList();
		if ((orderItem.getShiptoSilo() != null)
				&& (orderItem.getShiptoSilo().getActiveFlag().booleanValue())) {
			shiptoSilos.add(orderItem.getShiptoSilo());
		}
		else if (shipto != null) {
			List allShiptoSilos = shipto.getSilos();
			for (Iterator it = allShiptoSilos.iterator(); it.hasNext();) {
				ShiptoSiloEntity shipToSilo = (ShiptoSiloEntity) it.next();
				if ((shipToSilo.getSapMaterial() != null)
						&& (shipToSilo.getSapMaterial().getSapMaterialId().equals(orderItem.getSapMaterial().getSapMaterialId()))
						&& (shipToSilo.getActiveFlag().booleanValue())) {
					shiptoSilos.add(shipToSilo);
				}
			}
		}

		return shiptoSilos;
	}

	private String concat(String str1, String str2) {
		if (str1 == null) {
			str1 = "";
		}
		if (str2 == null) {
			str2 = "";
		}
		return (str1 + " " + str2).trim();
	}

	private AssignmentPartEntity getNextAssignmentPart(AssignmentPartEntity assignmentPart) {
		AssignmentPartEntity res = null;
		List<AssignmentPartEntity> assignmentPartList = AssignmentUtils.getSortedAssignmentParts(assignmentPart.getAssignment());
		for (Iterator<AssignmentPartEntity> it = assignmentPartList.iterator(); it.hasNext();) {
			AssignmentPartEntity ap = (AssignmentPartEntity) it.next();
			if (ap.getAssignmentPartId().equals(assignmentPart.getAssignmentPartId())) {
				if (it.hasNext()) {
					res = (AssignmentPartEntity) it.next();
				}
				break;
			}
		}

		return res;
	}

	private String getWeighingType(LoadingpointEntity loadingpoint) {

		if (loadingpoint != null) {
			switch (loadingpoint.getWeighSystemLp().getWeighSystemLpCd().intValue()) {
			case (int) Constant.WEIGH_SYSTEM_LP_WS:
				if (loadingpoint.getShippingpoint().getWeighSystemSp().getWeighSystemSpCd() == (int) Constant.WEIGH_SYSTEM_SP_PARALLEL_TRUCK) {
					return Constant.WEIGHING_TYPE_PARALLEL;
				}
				else {
					return Constant.WEIGHING_TYPE_LOGON;
				}
			case (int) Constant.WEIGH_SYSTEM_LP_PDA:
				return Constant.WEIGHING_TYPE_PDA;
			case (int) Constant.WEIGH_SYSTEM_LP_LOGON:
				return Constant.WEIGHING_TYPE_LOGON;
			default:
				return null;
			}
		}

		return null;
	}

	private String getLoadingType(OrderItemEntity orderItem) {
		OrderEntity order = orderItem.getOrder();
		ShippingpointEntity shippingPoint = order.getShippingpoint();
		if (orderItem.getBimodalRailOrderItem() != null) {
			return Constant.LOADING_TYPE_WAGON;
		}
		if (shippingPoint != null
				&& (shippingPoint.getShipppointType().getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER))) {
			return Constant.LOADING_TYPE_CONTAINER;
		}
		if (LogonPackageHandlingUtil.isBag(orderItem) && !LogonUtil.isReturnPalletOnly(order)) {
			return Constant.LOADING_TYPE_BAG;
		}
		else if (LogonUtil.isReturnPalletOnly(order)) {
			return Constant.LOADING_TYPE_RETURN_PALLET_ONLY;
		}
		else if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
			return Constant.LOADING_TYPE_BULK;
		}

		throw new LogonException("Unknown loading type for order item "
				+ orderItem.getOrderItemId(), Reason.RES_ERROR_NOT_IMPL);
	}

	public BigDecimal getOrderedQuantityT(AssignmentPartEntity loadAp) {

		LoadEntity load = loadAp.getLoad();
		TruckEntity truck = loadAp.getAssignment().getTruck();
		BigDecimal orderedQuantityT = BigDecimal.ZERO;
		Collection orderItems = orderService.getOrderItems(load);
		for (Iterator it = orderItems.iterator(); it.hasNext();) {
			OrderItemEntity orderItem = (OrderItemEntity) it.next();
			BigDecimal orderItemQuantityT = UOMConverter.getUomConvertedValue(
					orderItem.getOrderedUom(), orderItem.getOrderedQuantity(), false);
			if (orderItemQuantityT != null) {
				orderedQuantityT = orderedQuantityT.add(orderItemQuantityT);
			}
		}
		BigDecimal maxNetWeightT = truckAdminService.getTruckCompositionMaxNetWeightT(truck);
		if ((orderedQuantityT.compareTo(BigDecimal.ZERO) == 0)
				|| (orderedQuantityT.compareTo(maxNetWeightT) > 0)) {
			orderedQuantityT = maxNetWeightT;
		}

		return orderedQuantityT;
	}

	private String getDrivingTimeMin(Date startDate, Date endDate) {

		if (startDate != null && endDate != null) {
			Calendar end = Calendar.getInstance();
			end.setTime(endDate);
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);

			return String.valueOf((end.getTimeInMillis() - start.getTimeInMillis()) / 60000);
		}
		return null;
	}
	
	private String getLastEvent(AssignmentPartEntity ap) {
		return getLastEvent(ap, false);
	}

	private String getLastEvent(AssignmentPartEntity ap, boolean canHaveMultipleLoadingSite) {
		// [[
		String result = null;
		Object[][] params = { { "ap", ap }, { "canHaveMultipleLoadingSite", canHaveMultipleLoadingSite }};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLastEvent", params);
		}
		// ]]
		try {

			AssignmentPartEntity nextAp = getNextAssignmentPart(ap);
			Long assignmentTypeCd = ap.getAssignment().getAssignmentType().getAssignmentTypeCd();

			if (assignmentTypeCd == Constant.ASSIGNMENT_TYPE_EXTERNAL) {

				ExtOrderEntity extOrder = ap.getExtOrder();
				if (extOrder != null) {
					Long orderStatusCode = extOrder.getOrderStatus().getOrderStatusCd().longValue();

					switch (ap.getAssignmentPartType().getAssignmentPartTypeCd().intValue()) {
					case (int) Constant.ASSIGNMENT_PART_TYPE_LOAD:

						if (orderStatusCode == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
							return result = null;
						}
						if (orderStatusCode == Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT) {
							return result = LogonEventRequestFactory.MopFactory.EVENT_ENTERSHIPPINGPOINT;
						}
						if (orderStatusCode == Constant.ORDER_STATUS_LOADING) {
							return result = Constant.EVENT_LOGON_STARTLOAD;//LogonEventRequestFactory.MopFactory.EVENT_LOGONSTARTLOAD;
						}
						// TODO Mar 9, 2015/hosszub: Should we do anything with status 11?
						Boolean leftLoadingSite = orderStatusCode >= Constant.ORDER_STATUS_SHIPPING;
						if (leftLoadingSite) {
							// All orders are loaded
							return result = LogonEventRequestFactory.MopFactory.EVENT_LEAVESHIPPINGPOINT;
						}
						break;

					case (int) Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
						if (orderStatusCode == Constant.ORDER_STATUS_SHIPPING) {
							return result = LogonEventRequestFactory.MopFactory.EVENT_LEAVESHIPPINGPOINT;
						}
						if (orderStatusCode == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) {
							return result = LogonEventRequestFactory.MopFactory.EVENT_ENTERSHIPTO;
						}
						if (orderStatusCode >= Constant.ORDER_STATUS_UNLOADING) {
							return result = LogonEventRequestFactory.MopFactory.EVENT_STARTUNLOAD;
						}
						break;
					default:
						break;
					}
				}
			}

			else if (assignmentTypeCd == Constant.ASSIGNMENT_TYPE_INTERNAL) {
				switch (ap.getAssignmentPartType().getAssignmentPartTypeCd().intValue()) {
				case (int) Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD:
				case (int) Constant.ASSIGNMENT_PART_TYPE_FROM:
					if ((nextAp != null) && (nextAp.getRealStart() != null)) {

						return result = LogonEventRequestFactory.MopFactory.EVENT_ENTERENDPOINT;
					}
					else if (ap.getRealStart() != null) {

						return result = LogonEventRequestFactory.MopFactory.EVENT_LEAVESTARTPOINT;
					}
				break;
				case (int) Constant.ASSIGNMENT_PART_TYPE_LOAD:
					LoadEntity load = ap.getLoad();
				boolean leftLoadingSite = false;
				boolean atLoadingSiteOrFurther = false;

				if (canHaveMultipleLoadingSite) {
					List<OrderItemEntity> orderItems = orderService.getOrderItems(load);

					if (orderItems != null && orderItems.size() > 0) {
						OrderItemEntity orderItem = orderItems.get(0);
						OrderEntity order = orderItem.getOrder();
						Long orderStatusCd = order.getOrderStatus().getOrderStatusCd();

						leftLoadingSite = LogonStatusUtils.isBeyondLoaded(orderStatusCd);
						atLoadingSiteOrFurther = LogonStatusUtils.isArrivedAtLoadingPointOrBeyond(orderStatusCd);
					}
				}
				else {
					long assignmentStatus = ap.getAssignment().getAssignStatus().getAssignStatusCd().longValue();
					leftLoadingSite = assignmentStatus >= Constant.ASSIGNMENT_STATUS_SHIPPING;
					atLoadingSiteOrFurther = assignmentStatus >= Constant.ASSIGNMENT_STATUS_LOADING;
				}

				if (leftLoadingSite) {
					// All orders are loaded
					return result = LogonEventRequestFactory.MopFactory.EVENT_LEAVESHIPPINGPOINT;
				}
				else if ((load != null) && (load.getLoadingTs() != null)) {
					if ((load.getNetQuantity() != null)
							&& (load.getNetQuantity().longValue() > 0)) {
						// This order was loaded
						return result = Constant.EVENT_LOGON_ENDLOAD;
					}
					// This order is being loaded
					return result = Constant.EVENT_LOGON_STARTLOAD;
				}
				else if (atLoadingSiteOrFurther) {
					// Loading has not started yet for this order (but truck is at SP)
					return result = LogonEventRequestFactory.MopFactory.EVENT_ENTERSHIPPINGPOINT;
				}
				break;
				case (int) Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
					OrderEntity order = ap.getOrder();
				if (order != null) {
					if ((order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_DELIVERED)
							|| (order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_FINISHED)) {
						// Order is finished

						return result = LogonEventRequestFactory.MopFactory.EVENT_LEAVESHIPTO;
					}
					else if (order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_UNLOADING) {
						// Order is being unloaded

						return result = LogonEventRequestFactory.MopFactory.EVENT_STARTUNLOAD;
					}
					else if (order.getOrderStatus().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) {
						// Unloading has not started yet for this order (but truck is at ShipTo)

						return result = LogonEventRequestFactory.MopFactory.EVENT_ENTERSHIPTO;
					}
				}
				else if (nextAp.getRealStart() != null) {

					return result = LogonEventRequestFactory.MopFactory.EVENT_LEAVESHIPTO;
				}
				break;
				default:
					break;
				}
			}

			return result = null;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public UnloadOrder getUnloadOrderData(AssignmentPartEntity assignmentPart, Locale locale) {
		// [[
		UnloadOrder result = null;
		Object[][] params = { { "assignmentPart", assignmentPart }, { "locale", locale } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getUnloadOrderData", params);
		}
		// ]]
		try {
			if (assignmentPart == null || assignmentPart.getOrder() == null || locale == null) {

				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}

			ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
			OrderEntity order = assignmentPart.getOrder(); 
			Long orderId= order.getOrderId();
			
			order = orderDao.findById(orderId);
			order.initialize();
			
			ShiptoEntity shipto = getShipto(order);
			if (shipto == null) {
				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}
			
			ReadWriteDao<BizeventEntity> bizEventDao = getDaoProvider().getBizeventEntityDao();
			Filter<BizeventEntity> bizEventFilter = new FilterImpl<BizeventEntity>();
			bizEventFilter.addExpression(Restrictions.eq(BizeventEntity.ATTR_BIZEVENT_TYPE_CD, Constant.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT));
			bizEventFilter.addExpression(Restrictions.eq(BizeventEntity.ATTR_ORDER_ID, orderId));
			List<BizeventEntity> enterShiptoBizEvents = bizEventDao.find(bizEventFilter);
			BizeventEntity enterShiptoBizEvent = null;
			if (enterShiptoBizEvents != null && enterShiptoBizEvents.size() > 0) {
				enterShiptoBizEvent = enterShiptoBizEvents.get(0);
			}
			
			AssignmentEntity assignment = assignmentPart.getAssignment();
			String startTime = DateTimeFormat.forMop(assignmentPart.getPlannedStart());
			String actualFirstEventTime = "";
			if (enterShiptoBizEvent != null) {
				actualFirstEventTime = DateTimeFormat.forMop(enterShiptoBizEvent.getEventTs());
			}
			
			AssignmentPartEntity nextAssignmentPart = getNextAssignmentPart(assignmentPart);
			String endTime = DateTimeFormat.forMop(nextAssignmentPart.getPlannedStart());

			SapBpEntity customer = order.getShiptoSapBp();
			String customerName = null;
			if (customer != null) {
				customerName = customer.getName1();
				if (StringUtils.notEmpty(customer.getName2())) {
					customerName += " " + customer.getName2();
				}
			}

			Date deliveryFromDat = order.getDeliveryFromDat();
			Date deliveryToDat = order.getDeliveryToDat();
			
			String deliveryType = null;
			if (shipto.getShiptoType().getShiptoTypeCd().longValue() == Constant.SHIPTO_TYPE_CONTAINER_TERMINAL) {
				deliveryType = Constant.PDA_DELIVERY_TYPE_CONTAINER;
			}
			
			Long signatureTypeCd;
			if (LogonUtil.isLandfill(order)) {
				signatureTypeCd = Constant.SIGNATURE_TYPE_NO_SIGNATURE;
			}
			else {
				signatureTypeCd = shipto.getSignatureType().getSignatureTypeCd();
				for (OrderItemEntity oie : order.getOrderItems()) {
					if (oie.getLoad() != null && oie.getLoad().getLdsOfflineMode()) {
						signatureTypeCd = Constant.SIGNATURE_TYPE_NO_SIGNATURE;
						break;
					}
				}
			}

			PointData point = new PointData();
			point.init(assignmentPart);

			LogonRfidObject customerRfid = null;

			result = (UnloadOrder) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_UNLOADORDER, null);
			// order specific data
			result.setAssignmentID(ObjectUtils.toString(assignment.getAssignmentId(), null));
			result.setAssignmentPartID(ObjectUtils.toString(assignmentPart.getAssignmentPartId(),
					null));
			result.setActualFirstEventTime(actualFirstEventTime);
			result.setSeqNo(ObjectUtils.toString(assignmentPart.getAssignmentPartSeqNo(), null));
			result.setOrderNumber(ObjectUtils.toString(order.getOrderNumber(), null));
			result.setDistance(ObjectUtils.toString(order.getTransportDistanceM(), null));
			result.setDrivingTime(ObjectUtils.toString(order.getTransportDurationMin(), null));
			result.setDeliveryFromDat(DateTimeFormat.forMop(deliveryFromDat));
			result.setDeliveryToDat(DateTimeFormat.forMop(deliveryToDat));
			result.setDeliveryTimeWindowFromDat(DateTimeFormat.forMop(deliveryFromDat));
			result.setDeliveryTimeWindowToDat(DateTimeFormat.forMop(deliveryToDat));
			result.setDeliveryType(deliveryType);
			result.setStartTime(startTime);
			result.setEndTime(endTime);
			result.setShipToID(point.pointId);
			result.setShipToName1(point.pointName1);
			result.setShipToName2(point.pointName2);
			result.setShipToName3(point.pointName3);
			result.setShipToCoordLat(point.pointCoordLat);
			result.setShipToCoordLon(point.pointCoordLon);
			result.setShipToGeofenceRadius(point.pointGeofenceRadius);
			result.setContactPersonName(order.getContactPersonTxt());
			result.setCustomerLanguage(customer == null || customer.getLang() == null ? null
					: customer.getLang().getLangCd());
			result.setCustomerName(customerName);
			result.setCustomer(customerRfid);
			result.setNoteForDeliveryNote(order.getCustDeliveryNoteComment() == null ? 
					order.getCarrierDeliveryNote() : order.getCustDeliveryNoteComment());

			result.setSignatureType(String.valueOf(signatureTypeCd));
			List<OrderItemEntity> orderItemsSortedByMaterialType = LogonPackageHandlingUtil.getOrderItemsLeadingRealMaterialEndingPallet(order);
			result.setUnloadOrderItems(convertUnloadOrderItems(orderItemsSortedByMaterialType, shipto, assignmentPart,
					locale));

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

	private List getWagons(OrderItemEntity orderItem) {

		List wagons = null;
		if ((orderItem.getBimodalRailOrderItem() != null)
				&& (orderItem.getBimodalRailOrderItem().getLoad() != null)
				&& (orderItem.getBimodalRailOrderItem().getLoad().getWagon() != null)) {
			wagons = new ArrayList();
			wagons.add(orderItem.getBimodalRailOrderItem().getLoad().getWagon());
		}

		return wagons;
	}

	public LoadOrder getLoadOrderData(AssignmentPartEntity assignmentPart, Locale locale,
			TruckEntity truck) {
		return getLoadOrderData(assignmentPart, locale, truck, Constant.LoadOrderType.LOADING_AT_CRH_SITE,
				NO_MORE_RELEVANT_ASSIGNMENT_PART);
	}
	
	public LoadOrder getLoadOrderData(AssignmentPartEntity assignmentPart, Locale locale,
			TruckEntity truck, Constant.LoadOrderType loadOrderType) {
		return getLoadOrderData(assignmentPart, locale, truck, loadOrderType, NO_MORE_RELEVANT_ASSIGNMENT_PART);
	}
	
	/**
	 * This method is made public for the PDA test page.
	 */
	public LoadOrder getLoadOrderData(AssignmentPartEntity assignmentPart, Locale locale,
			TruckEntity truck, Constant.LoadOrderType loadOrderType, List<AssignmentPartEntity> assignmentPartList) {
		// [[
		LoadOrder result = null;
		Object[][] params = {{"assignmentPart", assignmentPart},
							{"locale", locale},
							{"truck", truck},
							{"loadOrderType", loadOrderType},
							{"assignmentPartList", assignmentPartList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadOrderData", params);
		}
		// ]]
		try {
			if (assignmentPart == null || truck == null || locale == null) {
				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}
			
			if (assignmentPart.getLoad() == null) {
				LOG.error("Could not find load in the load assignment part. (Assignment part id:"
						+ assignmentPart.getAssignmentPartId());
				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}
			
			LoadEntity load = assignmentPart.getLoad();

			Collection<OrderItemEntity> orderItems = orderService.getOrderItems(load);
			if (orderItems == null || orderItems.size() == 0) {
				LOG.error("Could not find orderItems for load:" + load.getLoadId());
				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}
			
			OrderItemEntity bulkOrderItem = null;
			String expectedQuantityKG = null;
			String orderedQuantityDesc = null;
			BigDecimal orderedQuantity = BigDecimal.ZERO;
			UomBsEntity orderedUom = null;
			String loadingType = null;

			String orderNumber1 = null;
			String validationCode1 = null;
			
			for (OrderItemEntity orderItem : orderItems) {
				//Order: there can be multiple orders per load, in this case fetch only the first one
				if (orderItem.getOrder() != null 
						&& orderNumber1 == null && validationCode1 == null) {
					orderNumber1 = ObjectUtils.toString(orderItem.getOrder().getOrderNumber(), null);
					validationCode1 = orderItem.getOrder().getOrderValidationCode();
				}

				// loading type
				if (loadingType == null) {
					loadingType = getLoadingType(orderItem);
				}
				else if (!loadingType.equals(getLoadingType(orderItem))) {

					throw new LogonException("Could not determine loading type for load "
							+ load.getLoadId() + ", multiple loading types found",
							Reason.RES_ERROR_IMPL);
				}
				// amount
				if (orderedUom == null) {
					orderedUom = orderItem.getOrderedUom();
				}
				else if (orderedUom != orderItem.getOrderedUom()) {
			
					throw new LogonException("Order items for load " + load.getLoadId()
							+ " have different ordered UoM's", Reason.RES_ERROR_IMPL);
				}
				orderedQuantity = orderedQuantity.add(orderItem.getOrderedQuantity());
				// get bulk item
				if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
					if (orderItems.size() > 1) {
			
						throw new LogonException("More than 1 bulk order item linked to load "
								+ load.getLoadId(), Reason.RES_ERROR_IMPL);
					}
					bulkOrderItem = orderItem;
				}
			}
			orderedQuantityDesc = formatQuantity(orderedQuantity, orderedUom, locale);
			
			BigDecimal loadedQuantityConverted = UOMConverter.getUomConvertedValue(
					load.getNetQuantityUom(), load.getNetQuantity(), false);
			String loadedQuantity = null;
			if (loadedQuantityConverted != null) {
				if (Constant.UOM_PCE.equals(load.getNetQuantityUom().getUomCd())) {
					loadedQuantity = loadedQuantityConverted.toString();
				}
				else {
					loadedQuantity = "" + loadedQuantityConverted.multiply(Constant.TON_TO_KG).longValue();
				}
			}
			
			String materialName = dispokarteService.fetchSapMaterialDescription(
					load.getSapMaterial().getSapMaterialId(), locale.getLanguage()).getTxt();
			AssignmentEntity assignment = assignmentPart.getAssignment();
			String startTime = DateTimeFormat.forMop(assignmentPart.getPlannedStart());
			AssignmentPartEntity nextAssignmentPart = getNextAssignmentPart(assignmentPart);
			String endTime = DateTimeFormat.forMop(nextAssignmentPart.getPlannedStart());
			
			List containers = null;
			List wagons = null;
			
			if (bulkOrderItem != null) {
				if (bulkOrderItem.getBimodalRailOrderItem() != null) {
					BigDecimal orderQuantityT = getOrderedQuantityT(assignmentPart);
					BigDecimal remaingWagonQuantityT = null;
					if (bulkOrderItem.getBimodalRailOrderItem().getLoad() != null) {
						remaingWagonQuantityT = orderService.getWagonRemainingQuantityT(
								bulkOrderItem.getBimodalRailOrderItem().getLoad());
					}
					if ((remaingWagonQuantityT == null)
							|| (orderQuantityT.compareTo(remaingWagonQuantityT) <= 0)) {
						expectedQuantityKG = ""
								+ orderQuantityT.multiply(Constant.TON_TO_KG).longValue();
					}
					else {
						expectedQuantityKG = remaingWagonQuantityT.multiply(Constant.TON_TO_KG).toString();
					}
				}
				wagons = getWagons(bulkOrderItem);
				if (Constant.LOADING_TYPE_CONTAINER.equals(loadingType)) {
					containers = orderService.getContainers(bulkOrderItem);
				}
			}
			
			List<LogonLoadingPoint> loadingPointList;
			if (Constant.LoadOrderType.ACKNOWLEDGE_FULL_TRUCK == loadOrderType) {
				loadingType = Constant.LOADING_TYPE_ACKNOWLEDGE_FULL_TRUCK;
				loadingPointList = null;
			}
			else if (Constant.LOADING_TYPE_RETURN_PALLET_ONLY.equals(loadingType)) {
				loadingPointList = null;
			}
			else {
				loadingPointList = convertLoadingpoints(getLoadingpoints(orderItems));
			}
			
			PointData point = new PointData();
			String siteType = null;
			String lastEvent = null;
			
			if (Constant.LOADING_TYPE_RETURN_PALLET_ONLY.equals(loadingType)) {
				point.init(assignmentPart);
				lastEvent = getLastEvent(assignmentPart, true);
			}
			else if (Constant.LoadOrderType.LOADING_AT_CRH_SITE == loadOrderType) {
				point.init(assignmentPart);
				lastEvent = getLastEvent(assignmentPart);
			}
			else if (Constant.LoadOrderType.MEASURING_BEFORE_UNLOAD == loadOrderType) {
				if (assignmentPartList != null) {
					for (AssignmentPartEntity ap : assignmentPartList) {
						if (AssignmentUtils.isUnloadPart(ap)) {
							point.init(ap);
							break;
						}
					}
				}
				siteType = Constant.SITE_TYPE_UNLOADING_SITE;
				if (assignment.getAssignStatus().getAssignStatusCd().longValue() == Constant.ASSIGNMENT_STATUS_SHIPPING){
					lastEvent = LogonEventRequestFactory.MopFactory.EVENT_ORDERSTART;
				}
				else if (assignment.getAssignStatus().getAssignStatusCd().longValue() > Constant.ASSIGNMENT_STATUS_SHIPPING) {
					lastEvent = LogonEventRequestFactory.MopFactory.EVENT_ENTERSHIPPINGPOINT;
				}
			}
			else if (Constant.LoadOrderType.ACKNOWLEDGE_FULL_TRUCK == loadOrderType) {
				point.init(assignmentPart);
				siteType = Constant.SITE_TYPE_LOADING_SITE;
				lastEvent = getLastEvent(assignmentPart);
			}
			
			result = (LoadOrder) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_LOADORDER, null);
			
			// order specific data
			result.setAssignmentID(ObjectUtils.toString(assignment.getAssignmentId(), null));
			result.setAssignmentPartID(ObjectUtils.toString(assignmentPart.getAssignmentPartId(), null));
			result.setSeqNo(ObjectUtils.toString(assignmentPart.getAssignmentPartSeqNo(), null));
			result.setChamber(ObjectUtils.toString(load.getChamberNumber(), null));
			result.setSiteType(siteType);
			result.setLastEvent(lastEvent);
			result.setOrderedQuantity(orderedQuantityDesc);
			result.setMaterial(materialName);
			result.setStartTime(startTime);
			result.setEndTime(endTime);
			result.setShippingpointID(point.pointId);
			result.setShippingpointName(point.pointName1);
			result.setShippingpointCoordLat(point.pointCoordLat);
			result.setShippingpointCoordLon(point.pointCoordLon);
			result.setShippingpointGeofenceRadius(point.pointGeofenceRadius);
			result.setLoadingPointList(loadingPointList);
			result.setLoadingType(loadingType);
			result.setNoteToDriver(assignment.getCarrierTruckDriverNote());
			result.setExpectedQuantity(expectedQuantityKG);
			result.setLoadedQuantity(loadedQuantity);
			result.setContainerList(convertContainers(containers));
			result.setWagonList(convertWagons(wagons));
			result.setOrderNumber(orderNumber1);
			result.setValidationCode(validationCode1);
			
			final Boolean enableLdsOfflineMode = isLdsOfflineModeEnabled(
					assignmentPart, orderItems);
			result.setEnableLdsOfflineMode(Boolean.toString(enableLdsOfflineMode));
			result.setLdsOfflineMode(Boolean.toString(load.getLdsOfflineMode()));
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
	 * Setting here the ldsOfflineModeEnabled flag is the first step to end up in a regularization flow
	 * See CRHLOGON-98. Regularization is allowed only in specific cases.
	 * 
	 * @param assignmentPart
	 * @param orderItems
	 * @return
	 */
	private Boolean isLdsOfflineModeEnabled(
			AssignmentPartEntity assignmentPart,
			Collection<OrderItemEntity> orderItems) {
		final OrderEntity firstOrder = (orderItems.iterator().next()).getOrder();
		final Long deliveryTypeCd = firstOrder.getDeliveryType().getDeliveryTypeCd();
		
		Boolean enableLdsOfflineMode;
		
		if ( !deliveryTypeCd.equals(Constant.DELIVERY_TYPE_ROAD_DIRECT) || LogonUtil.isLandfill(firstOrder) || assignmentPart.getShippingpoint() == null ) {
			enableLdsOfflineMode = Boolean.FALSE;
		}
		else {
			enableLdsOfflineMode =  assignmentPart.getShippingpoint().getWeighSystemSp().getLdsOfflineModeEnable();
		}
		return enableLdsOfflineMode;
		
	}
	
	private LogonEventOutBean getExternalOrderData(AssignmentPartEntity assignmentPart,
			Locale locale) {
		// [[
		LogonEventOutBean result = null;
		Object[][] params = { { "assignmentPart", assignmentPart }, { "locale", locale } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExternalOrderData", params);
		}
		// ]]
		try {
			if (assignmentPart == null || locale == null) {
				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}
			final boolean loadOrder = assignmentPart.getAssignmentPartType().getAssignmentPartTypeCd() == Constant.ASSIGNMENT_PART_TYPE_LOAD;

			AssignmentEntity assignment = assignmentPart.getAssignment();
			String orderQuantity = formatQuantity(assignment.getMaterialQuantity(),
					assignment.getMaterialUom(), locale);
			String materialName = assignment.getMaterialName();

			PointData point = new PointData();
			point.init(assignmentPart);

			String startTime = DateTimeFormat.forMop(assignmentPart.getPlannedStart());
			AssignmentPartEntity nextAssignmentPart = getNextAssignmentPart(assignmentPart);
			String endTime = DateTimeFormat.forMop(nextAssignmentPart.getPlannedStart());
			Date endDate = nextAssignmentPart.getPlannedStart();

			String contactPerson = null;
			Date finishDate = null;
			List assignmentParts = assignment.getAssignmentParts();
			for (Iterator iterator = assignmentParts.iterator(); iterator.hasNext();) {
				AssignmentPartEntity lastAssignmentPart = (AssignmentPartEntity) iterator.next();
				if (lastAssignmentPart.getAssignmentPartType().getAssignmentPartTypeCd() == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
					contactPerson = lastAssignmentPart.getSiteContactPersonTxt();
					finishDate = lastAssignmentPart.getPlannedStart();
				}
			}

			String drivingTimeMin = getDrivingTimeMin(endDate, finishDate);

			if (loadOrder) {
				LoadOrder orderData = (LoadOrder) LogonEventOutBeanFactory.create(
						LogonEventOutBeanFactory.EVENT_LOADORDER, null);

				// order specific data
				orderData.setAssignmentID(ObjectUtils.toString(assignment.getAssignmentId(), null));
				orderData.setAssignmentPartID(ObjectUtils.toString(
						assignmentPart.getAssignmentPartId(), null));
				orderData.setSeqNo(ObjectUtils.toString(assignmentPart.getAssignmentPartSeqNo(),
						null));
				orderData.setChamber("1");
				orderData.setLoadingType(Constant.LOADING_TYPE_EXTERNAL);
				orderData.setMaterial(materialName);
				orderData.setOrderedQuantity(orderQuantity);
				orderData.setStartTime(startTime);
				orderData.setEndTime(endTime);
				orderData.setShippingpointID(point.pointId);
				orderData.setShippingpointName(point.pointName1);
				orderData.setShippingpointCoordLat(point.pointCoordLat);
				orderData.setShippingpointCoordLon(point.pointCoordLon);
				orderData.setShippingpointGeofenceRadius(point.pointGeofenceRadius);
				orderData.setNoteToDriver(assignment.getCarrierTruckDriverNote());
				orderData.setLastEvent(getLastEvent(assignmentPart));
				result = orderData;
			}
			else {
				UnloadOrder orderData = (UnloadOrder) LogonEventOutBeanFactory.create(
						LogonEventOutBeanFactory.EVENT_UNLOADORDER, null);

				// order specific data
				orderData.setAssignmentID(ObjectUtils.toString(assignment.getAssignmentId(), null));
				orderData.setAssignmentPartID(ObjectUtils.toString(
						assignmentPart.getAssignmentPartId(), null));
				if (assignmentPart.getExtOrder() != null) {
					orderData.setDeliveryFromDat(DateTimeFormat.forMop(assignmentPart.getExtOrder().getDeliveryFromDat()));
					orderData.setDeliveryToDat(DateTimeFormat.forMop(assignmentPart.getExtOrder().getDeliveryToDat()));
					orderData.setExternalOrderNumber(
							ObjectUtils.toString(assignmentPart.getExtOrder().getExtOrderNumber(), null));
				}
				orderData.setSeqNo(ObjectUtils.toString(assignmentPart.getAssignmentPartSeqNo(),
						null));
				orderData.setDrivingTime(drivingTimeMin);
				orderData.setStartTime(startTime);
				orderData.setEndTime(endTime);
				orderData.setShipToID(point.pointId);
				orderData.setShipToName1(point.pointName1);
				orderData.setShipToName2(point.pointName2);
				orderData.setShipToName3(point.pointName3);
				orderData.setShipToCoordLat(point.pointCoordLat);
				orderData.setShipToCoordLon(point.pointCoordLon);
				orderData.setShipToGeofenceRadius(point.pointGeofenceRadius);
				orderData.setContactPersonName(contactPerson);
				orderData.setNoteForDeliveryNote("");
				orderData.setSignatureType("" + Constant.SIGNATURE_TYPE_OPTIONAL);
				orderData.setUnloadOrderItems(convertUnloadOrderItems(null, null, assignmentPart,
						locale));
				result = orderData;
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

	private LogonEventOutBean getAssignmentPartData(AssignmentPartEntity assignmentPart,
			TruckEntity truck, Locale locale) {

		Long assignmentTypeCd = assignmentPart.getAssignment().getAssignmentType().getAssignmentTypeCd();
		Long statusCd = assignmentPart.getAssignment().getAssignStatus().getAssignStatusCd();
		Long partTypeCd = assignmentPart.getAssignmentPartType().getAssignmentPartTypeCd();
		AssignmentPartEntity nextAp = getNextAssignmentPart(assignmentPart);

		if (((partTypeCd.equals(Constant.ASSIGNMENT_PART_TYPE_LOAD)) || (partTypeCd.equals(Constant.ASSIGNMENT_PART_TYPE_UNLOAD)))
				&& (!statusCd.equals(Constant.ASSIGNMENT_STATUS_RETURNING))) {
			if (assignmentTypeCd == Constant.ASSIGNMENT_TYPE_EXTERNAL) {

				return getExternalOrderData(assignmentPart, locale);
			}
			else if (partTypeCd.equals(Constant.ASSIGNMENT_PART_TYPE_LOAD)) {

				return getLoadOrderData(assignmentPart, locale, truck);
			}
			else if (partTypeCd.equals(Constant.ASSIGNMENT_PART_TYPE_UNLOAD)) {

				return getUnloadOrderData(assignmentPart, locale);
			}
		}
		else if (partTypeCd.equals(Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD)
				&& (statusCd.equals(Constant.ASSIGNMENT_STATUS_NEW) || statusCd.equals(Constant.ASSIGNMENT_STATUS_PRE_LOAD))) {

			return getPrePostOrder(assignmentPart);
		}
		else if ((partTypeCd.equals(Constant.ASSIGNMENT_PART_TYPE_FROM))
				&& (nextAp != null)
				&& (nextAp.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_FINISHED))) {

			return getPrePostOrder(assignmentPart);
		}

		return null;
	}

	private List<LogonEventOutBean> getTourData(AssignmentEntity assignment, TruckEntity truck, Locale locale) {
		// [[
		List<LogonEventOutBean> result = null;
		Object[][] params = {{"assignment", assignment},
							{"truck", truck},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getTourData", params);
		}
		// ]]
		try {
			result = new ArrayList<LogonEventOutBean>();
			long assignmentTrStatus = assignment.getAssignTrStatus().getAssignTrStatusCd().longValue();
			long assignmentStatus = assignment.getAssignStatus().getAssignStatusCd().longValue();
			
			if (assignmentTrStatus == Constant.ASSIGN_STATUS_TR_TRANSMITTED &&
					assignmentStatus != Constant.ASSIGNMENT_STATUS_FINISHED && 
					assignmentStatus != Constant.ASSIGNMENT_STATUS_CANCELLED ) {
				
				List<AssignmentPartEntity> assignmentPartList = AssignmentUtils.getSortedAssignmentParts(assignment);
				for (AssignmentPartEntity assignmentPart : assignmentPartList) {
					if (AssignmentUtils.isLoadPart(assignmentPart) && assignmentPart.getLoad() != null 
							&& LogonUtil.isLandfill(assignmentPart.getLoad().getSapMaterial())) {
						LoadOrder loadOrderAcknowledgementAtCustomerSite = getLoadOrderData(assignmentPart, locale, 
								truck, Constant.LoadOrderType.ACKNOWLEDGE_FULL_TRUCK);
						if (loadOrderAcknowledgementAtCustomerSite != null) {
							result.add(loadOrderAcknowledgementAtCustomerSite);
						}
						
						LoadOrder loadOrderAtCrhSite = getLoadOrderData(assignmentPart, locale, truck,
								Constant.LoadOrderType.MEASURING_BEFORE_UNLOAD, assignmentPartList);
						if (loadOrderAtCrhSite != null) {
							result.add(loadOrderAtCrhSite);
						}
					}
					else {
						LogonEventOutBean data = getAssignmentPartData(assignmentPart, truck, locale);
						if (data != null) {
							result.add(data);
						}
					}
				}
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
	
	public void sendTourToPDA(TruckEntity truck, AssignmentEntity ass) {
		if (ass == null || truck == null) {
			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(ass);

		if (orders.size() >= 1 && orders.get(0).getNoPdaOrRailEventsFlag()) {
			return;
		}
		PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);
		if (session != null) {
			Locale locale = new Locale(session.getUser().getApplicationLang().getLangCd());
			List msgs = getTourData(ass, truck, locale);
			if ((msgs != null) && (!msgs.isEmpty())) {
				pdaSessionService.initOutgoingEvents(msgs, session);
				LOG.info("Attempting to send assignment (" + ass.getAssignmentId() +
						") to truck (" + truck.getTruckId() + "). Session: " + session.getPdaSessionId());

				send(createLogonMessageBean(msgs));
			}
			else {
				LOG.error("Could not determine tourdata for assignment: " + ass.getAssignmentId() +
						", truck: " + truck.getTruckId());
			}
		}
	}

	/**
	 * Sets into the bizevent the proper foreign reference which describes where the event happens.
	 * 
	 * The point id the pda knows of is either a shippingpoint or a shipto id depending on the fact if the unloading will happen at a shipto or at a shipping point.
	 * See also AsyncMopEventOutServiceBean.PointData construction.
	 *
	 * @param bizevent
	 * @param part
	 * @param pointId
	 */
	public void setRelevantBizeventPointId(BizeventEntity bizevent, AssignmentPartEntity part,
			final Long pointId) {
		if (pointId != null) {
			if (LogonUtil.isOrderWithReversedDirection(part.getOrder())) {
				//in case of landfill or return pallet the pda is sending back the shippingpoint id
				ShippingpointEntity sp = getDaoProvider().getShippingpointEntityDao().findById(
						pointId);
				if (sp == null) {

					throw new LogonException();
				}
				bizevent.setShippingpoint(sp);
			}
			else {
				//in all other cases the pda is sending back the shipto id
				ShiptoEntity shipto = getDaoProvider().getShiptoEntityDao().findById(pointId);
				if (shipto == null) {

					throw new LogonException();
				}
				bizevent.setShipto(shipto);
			}
		}
	}

	private class PointData {
		String pointId = null;

		String pointName1 = null;

		String pointName2 = null;

		String pointName3 = null;

		String pointCoordLat = null;

		String pointCoordLon = null;

		String pointGeofenceRadius = null;

		private void initialize(AssignmentPartEntity assignmentPart) {
			pointName1 = assignmentPart.getSiteName();
			pointName2 = assignmentPart.getSiteStreetHouseNo();
			pointName3 = concat(assignmentPart.getSitePostalCode(), assignmentPart.getSiteCity());
			if ((assignmentPart.getHomebaseFlag() != null)
					&& (assignmentPart.getHomebaseFlag().booleanValue())
					&& (assignmentPart.getAssignment().getTruck() != null)) {
				pointCoordLat = ObjectUtils.toString(
						assignmentPart.getAssignment().getTruck().getHomebaseCoordLat(), null);
				pointCoordLon = ObjectUtils.toString(
						assignmentPart.getAssignment().getTruck().getHomebaseCoordLon(), null);
			}
			else {
				pointCoordLat = ObjectUtils.toString(assignmentPart.getCoordLat(), null);
				pointCoordLon = ObjectUtils.toString(assignmentPart.getCoordLon(), null);
			}
			pointGeofenceRadius = String.valueOf(getConfigBean().getAppDefaultShippingpointShiptoGeofenceRadiusM());
			if ((assignmentPart.getShipto() != null) && (assignmentPart.getLnfShiptoFlag() != null)
					&& (assignmentPart.getLnfShiptoFlag().booleanValue())) {
				pointId = assignmentPart.getShipto().getShiptoId().toString();
			}
		}

		private void init(ShippingpointEntity shippingpoint) {
			if (shippingpoint != null) {
				pointId = shippingpoint.getShippingpointId().toString();
				pointName1 = shippingpoint.getShippingpointName();
				pointCoordLat = ObjectUtils.toString(shippingpoint.getCoordLat(), null);
				pointCoordLon = ObjectUtils.toString(shippingpoint.getCoordLon(), null);
				pointGeofenceRadius = ObjectUtils.toString(shippingpoint.getGeofenceRadius(), null);
			}
		}

		private void init(ShiptoEntity shipto) {
			SapBpEntity sap = shipto.getShiptoSapBp();
			if (sap != null) {
				pointName1 = concat(sap.getName1(), sap.getName2());
				pointName2 = concat(sap.getStreet(), sap.getHouseNumber());
				pointName3 = concat(sap.getPostalCode(), sap.getCity());
			}

			if (shipto != null) {
				pointId = shipto.getShiptoId().toString();
				pointCoordLat = ObjectUtils.toString(shipto.getCoordLat(), null);
				pointCoordLon = ObjectUtils.toString(shipto.getCoordLon(), null);
				pointGeofenceRadius = ObjectUtils.toString(shipto.getGeofenceRadius(), null);
			}
		}

		public void init(AssignmentPartEntity assignmentPart) {
			if (assignmentPart.getShippingpoint() != null) {
				init(assignmentPart.getShippingpoint());
			}
			else if ((assignmentPart.getShipto() != null)
					&& ((assignmentPart.getLnfShiptoFlag() == null) || (!assignmentPart.getLnfShiptoFlag().booleanValue()))) {
				init(assignmentPart.getShipto());
			}
			else {
				initialize(assignmentPart);
			}
		}
	}

	private LogonEventOutBean getPrePostOrder(AssignmentPartEntity assignmentPart) {
		// [[
		LogonEventOutBean result = null;
		Object[][] params = { { "assignmentPart", assignmentPart } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPrePostOrder", params);
		}
		// ]]
		try {
			if (assignmentPart == null) {
				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}

			final boolean preOrder = assignmentPart.getAssignmentPartType().getAssignmentPartTypeCd() == Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD;

			AssignmentEntity assignment = assignmentPart.getAssignment();
			String startTime = DateTimeFormat.forMop(assignmentPart.getPlannedStart());
			PointData startpoint = new PointData();
			startpoint.init(assignmentPart);

			AssignmentPartEntity nextAssignmentPart = getNextAssignmentPart(assignmentPart);
			String endTime = DateTimeFormat.forMop(nextAssignmentPart.getPlannedStart());
			PointData endpoint = new PointData();
			endpoint.init(nextAssignmentPart);

			String drivingTime = getDrivingTimeMin(assignmentPart.getPlannedStart(),
					nextAssignmentPart.getPlannedStart());

			if (preOrder) {
				PreOrder orderData = (PreOrder) LogonEventOutBeanFactory.create(
						LogonEventOutBeanFactory.EVENT_PREORDER, null);

				// order specific data
				orderData.setAssignmentID(ObjectUtils.toString(assignment.getAssignmentId(), null));
				orderData.setAssignmentPartID(ObjectUtils.toString(
						assignmentPart.getAssignmentPartId(), null));

				orderData.setDrivingTime(drivingTime);
				orderData.setStartTime(startTime);
				orderData.setEndTime(endTime);
				orderData.setStartpointName1(startpoint.pointName1);
				orderData.setStartpointName2(startpoint.pointName2);
				orderData.setStartpointName3(startpoint.pointName3);
				orderData.setStartpointCoordLat(startpoint.pointCoordLat);
				orderData.setStartpointCoordLon(startpoint.pointCoordLon);
				orderData.setStartpointGeofenceRadius(startpoint.pointGeofenceRadius);
				orderData.setEndpointName1(endpoint.pointName1);
				orderData.setEndpointName2(endpoint.pointName2);
				orderData.setEndpointName3(endpoint.pointName3);
				orderData.setEndpointCoordLat(endpoint.pointCoordLat);
				orderData.setEndpointCoordLon(endpoint.pointCoordLon);
				orderData.setEndpointGeofenceRadius(endpoint.pointGeofenceRadius);
				orderData.setLastEvent(getLastEvent(assignmentPart));

				return result = orderData;
			}
			else {
				PostOrder orderData = (PostOrder) LogonEventOutBeanFactory.create(
						LogonEventOutBeanFactory.EVENT_POSTORDER, null);

				// order specific data
				orderData.setAssignmentID(ObjectUtils.toString(assignment.getAssignmentId(), null));
				orderData.setAssignmentPartID(ObjectUtils.toString(
						assignmentPart.getAssignmentPartId(), null));

				orderData.setDrivingTime(drivingTime);
				orderData.setStartTime(startTime);
				orderData.setEndTime(endTime);
				orderData.setStartpointName1(startpoint.pointName1);
				orderData.setStartpointName2(startpoint.pointName2);
				orderData.setStartpointName3(startpoint.pointName3);
				orderData.setStartpointCoordLat(startpoint.pointCoordLat);
				orderData.setStartpointCoordLon(startpoint.pointCoordLon);
				orderData.setStartpointGeofenceRadius(startpoint.pointGeofenceRadius);
				orderData.setEndpointName1(endpoint.pointName1);
				orderData.setEndpointName2(endpoint.pointName2);
				orderData.setEndpointName3(endpoint.pointName3);
				orderData.setEndpointCoordLat(endpoint.pointCoordLat);
				orderData.setEndpointCoordLon(endpoint.pointCoordLon);
				orderData.setEndpointGeofenceRadius(endpoint.pointGeofenceRadius);
				orderData.setLastEvent(getLastEvent(assignmentPart));

				return result = orderData;
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public void sendCancelTourToPDA(TruckEntity truck, AssignmentEntity assignment) {
		if (assignment == null || truck == null) {

			throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
		}

		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		if (orders.size() >= 1 && orders.get(0).getNoPdaOrRailEventsFlag()) {
			return;
		}

		PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);
		if (session != null) {
			CancelTour ct = (CancelTour) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_CANCELTOUR, null);
			ct.setAssignmentID(assignment.getAssignmentId().toString());

			pdaSessionService.initOutgoingEvent(ct, session);
			send(ct);
		}
	}

	public void sendTruckCompositionToPDA(PdaSessionEntity session, DriverEntity driver) {
		// [[
		Object[][] params = { { "session", session }, { "driver", driver } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendTruckCompositionToPDA", params);
		}
		// ]]
		try {
			if (session == null) {
				return;
			}
			TruckEntity truck = driver.getCurrentTruck();
			if (truck == null) {
				return;
			}

			TruckComposition truckComposition = (TruckComposition) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_TRUCKCOMPOSITION, null);
			pdaSessionService.initOutgoingEvent(truckComposition, session);

			if (truck.getRfidobject() != null) {
				truckComposition.setTruckLabel(truck.getRfidobject().getObjectLabel());
			}
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID,
					truck.getTruckId()));
			TrailerEntity trailer = getDaoProvider().getTrailerEntityDao().findSingle(filter);
			if (trailer != null && trailer.getRfidobject() != null) {
				truckComposition.setTrailerLabel(trailer.getRfidobject().getObjectLabel());
				truckComposition.setNumberOfChambers(trailer.getNrChambers().toString());
			}
			else {
				truckComposition.setNumberOfChambers(truck.getNrChambers().toString());
			}
			filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID,
					truck.getTruckId()));
			ContainerEntity container = getDaoProvider().getContainerEntityDao().findSingle(filter);
			if (container != null && container.getRfidobject() != null) {
				truckComposition.setContainerLabel(container.getRfidobject().getObjectLabel());
			}

			send(truckComposition);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void sendTruckLoadToPDA(PdaSessionEntity session, AssignmentPartEntity ap,
			BigDecimal quantityT, ContainerEntity container, boolean plausible) {
		// [[
		Object[][] params = { { "session", session }, { "ap", ap }, { "quantityT", quantityT },
				{ "container", container }, { "plausible", plausible + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendTruckLoadToPDA", params);
		}
		// ]]
		try {
			if (session == null) {
				return;
			}
			TruckLoad truckLoad = (TruckLoad) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_TRUCKLOAD, null);
			pdaSessionService.initOutgoingEvent(truckLoad, session);

			truckLoad.setAssignmentPartID(ap.getAssignmentPartId().toString());
			truckLoad.setQuantity("" + quantityT.multiply(Constant.TON_TO_KG).longValue());
			if (container != null) {
				truckLoad.setContainerLabel(container.getContainerNo());
			}
			truckLoad.setPlausible("" + plausible);

			send(truckLoad);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void sendLoadingStarted(TruckEntity truck, AssignmentPartEntity ap) {
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(ap.getAssignment());

		if (orders.size() >= 1 && orders.get(0).getNoPdaOrRailEventsFlag()) {
			return;
		}
		PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);
		if (session != null) {
			send(createLoadingStarted(truck, ap));
		}
	}

	public LoadingStarted createLoadingStarted(TruckEntity truck, AssignmentPartEntity ap) {
		// [[
		LoadingStarted result = null;
		Object[][] params = { { "truck", truck }, { "ap", ap } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createLoadingStarted", params);
		}
		// ]]
		try {
			LoadingStarted loadingStarted = (LoadingStarted) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_LOADINGSTARTED, null);

			PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);
			if (session != null) {
				pdaSessionService.initOutgoingEvent(loadingStarted, session);

				loadingStarted.setAssignmentID("" + ap.getAssignment().getAssignmentId());
				loadingStarted.setAssignmentPartID("" + ap.getAssignmentPartId());

				return result = loadingStarted;
			}

			return result = null;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public void sendLoadingEnded(TruckEntity truck, AssignmentPartEntity ap,
			BigDecimal taraMeasureT, BigDecimal grossMeasureT, BigDecimal netMeasure,
			UomBsEntity netMeasureUom, SapMaterialEntity sapMaterial) {
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(ap.getAssignment());

		if (orders.size() >= 1 && orders.get(0).getNoPdaOrRailEventsFlag()) {
			return;
		}

		PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);

		if (session != null) {
			send(createLoadingEnded(truck, ap, taraMeasureT, grossMeasureT, netMeasure,
					netMeasureUom, sapMaterial));
		}
	}

	public LoadingEnded createLoadingEnded(TruckEntity truck, AssignmentPartEntity ap,
			BigDecimal taraMeasureT, BigDecimal grossMeasureT, BigDecimal netMeasure,
			UomBsEntity netMeasureUom, SapMaterialEntity sapMaterial) {
		// [[
		LoadingEnded result = null;
		Object[][] params = { { "truck", truck }, { "ap", ap }, { "taraMeasureT", taraMeasureT },
				{ "grossMeasureT", grossMeasureT }, { "netMeasure", netMeasure },
				{ "netMeasureUom", netMeasureUom }, { "sapMaterial", sapMaterial } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createLoadingEnded", params);
		}
		// ]]
		try {
			LoadingEnded loadingEnded = (LoadingEnded) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_LOADINGENDED, null);

			PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);
			if (session != null) {
				pdaSessionService.initOutgoingEvent(loadingEnded, session);

				loadingEnded.setAssignmentPartID(String.valueOf(ap.getAssignmentPartId()));
				if (taraMeasureT != null) {
					BigDecimal taraMeasureKg = taraMeasureT.multiply(Constant.TON_TO_KG);
					loadingEnded.setTareWeightKg(String.valueOf(taraMeasureKg.longValue()));
				}
				if (grossMeasureT != null) {
					BigDecimal grossMeasureKg = grossMeasureT.multiply(Constant.TON_TO_KG);
					loadingEnded.setTotalWeightKg(String.valueOf(grossMeasureKg.longValue()));
				}
				/*
				 * As far as we know the PDA is only capable to load bulk materials in tons.
				 * For now it is not the right time to change the PDA interface. Hopefully the
				 * conversion below will only be executed for materials ordered in tons (ie. we
				 * only convert to KGs). For the rest, I decided to try a conversion at least...
				 * --romek
				 */
				UomBsEntity uomKg = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_KG);
				BigDecimal netMeasureKg = BigDecimal.ZERO;

				try {
					UomConversionResult ucr = unitOfMeasurementConverterService.convert(netMeasure,
							netMeasureUom, uomKg, sapMaterial, null, null, false);
					netMeasureKg = ucr.getQuantity();
				}
				catch (LogonConversionException e) {
					LOG.warn("Could not convert material quantity to kg " + "(sapMaterialId = "
							+ sapMaterial.getSapMaterialId() + "): " + e.getMessage());
				}

				loadingEnded.setNetWeightKg(String.valueOf(netMeasureKg.longValue()));

				return result = loadingEnded;
			}

			return result = null;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public void sendLoadingStartedLoadingEnded(TruckEntity truck, AssignmentPartEntity ap,
			BigDecimal taraMeasureT, BigDecimal grossMeasureT, BigDecimal netMeasure,
			UomBsEntity netMeasureUom, SapMaterialEntity sapMaterial) {
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(ap.getAssignment());

		if (orders.size() >= 1 && orders.get(0).getNoPdaOrRailEventsFlag()) {
			return;
		}
		PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);
		if (session != null) {
			List messageBeans = new ArrayList();
			messageBeans.add(createLogonMessageBean(createLoadingStarted(truck, ap)));
			messageBeans.add(createLogonMessageBean(createLoadingEnded(truck, ap, taraMeasureT,
					grossMeasureT, netMeasure, netMeasureUom, sapMaterial)));

			sendMessageBeans(messageBeans);
		}
	}

	public void sendSetupToPDA(UserEntity user, PdaSessionEntity session) {
		// [[
		Object[][] params = { { "user", user }, { "session", session } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendSetupToPDA", params);
		}
		// ]]
		try {

			Setup setup = (Setup) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_SETUP, null);
			pdaSessionService.initOutgoingEvent(setup, session);

			if (user.getPdaUserNo() != null) {
				setup.setPdaUserNo(user.getPdaUserNo().toString());
			}
			LangBsEntity lang = user.getApplicationLang();
			if (lang != null) {
				setup.setPdaUserLanguage(lang.getLangCd());
			}
			else {
				setup.setPdaUserLanguage(Constant.LANGUAGE_CODE_GERMAN);
			}

			PersonEntity person = user.getPerson();
			if (person != null) {
				setup.setPdaUserName(person.getFirstname() + " " + person.getName());
			}
			setup.setPdaUserSignatureMissing(user.getSignature() != null
					&& Boolean.TRUE.equals(user.getSignatureValidFlag()) ? Constant.FALSE
					: Constant.TRUE);

			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(UserRoleEntity.ATTR_USER, user));
			List roles = getDaoProvider().getUserRoleEntityDao().find(filter);
			StringBuffer pdaUserFuncs = new StringBuffer();
			HashSet addedFuncs = new HashSet();
			for (Iterator it = roles.iterator(); it.hasNext();) {
				UserRoleEntity role = (UserRoleEntity) it.next();
				filter = new FilterImpl();
				filter.addExpression(Restrictions.eq(RoleFuncEntity.ATTR_ROLE, role.getRole()));
				filter.addExpression(Restrictions.ge(RoleFuncEntity.ATTR_FUNC_ID,
						Constant.FUNCTION_GROUP_PDA_MIN));
				filter.addExpression(Restrictions.le(RoleFuncEntity.ATTR_FUNC_ID,
						Constant.FUNCTION_GROUP_PDA_MAX));

				List roleFuncs = getDaoProvider().getRoleFuncEntityDao().find(filter);
				for (Iterator it2 = roleFuncs.iterator(); it2.hasNext();) {
					RoleFuncEntity roleFunc = (RoleFuncEntity) it2.next();
					final Long funcId = roleFunc.getFunc().getFuncId();
					if (!addedFuncs.contains(funcId)) {
						if (pdaUserFuncs.length() != 0) {
							pdaUserFuncs.append(",");
						}
						pdaUserFuncs.append(funcId);
						addedFuncs.add(funcId);
					}
				}
			}
			setup.setPdaUserRoles(pdaUserFuncs.toString());

			setup.setGeoPositionTimeIntervall(getConfigBean().getWsclientMopGeopositionTimeintervall());
			setup.setGeoPositionDistanceIntervall(getConfigBean().getWsclientMopGeopositionDistanceintervall());
			setup.setGeoPositionMinimumAngle(""
					+ getConfigBean().getWsclientMopGeopositionMinimumangle());
			setup.setGeoPositionMinimumDistance(""
					+ getConfigBean().getWsclientMopGeopositionMinimumdistance());

			filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(DriverEntity.ATTR_USER, user));
			filter.addExpression(Restrictions.eq(DriverEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			DriverEntity driver = getDaoProvider().getDriverEntityDao().findSingle(filter);

			if (driver != null) {
				TruckEntity truck = driver.getCurrentTruck();
				if (truck != null) {
					if (truck.getRfidobject() != null) {
						setup.setTruckLabel(truck.getRfidobject().getObjectLabel());
					}
					filter = new FilterImpl();
					filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID,
							truck.getTruckId()));
					TrailerEntity trailer = getDaoProvider().getTrailerEntityDao().findSingle(
							filter);
					if (trailer != null && trailer.getRfidobject() != null) {
						setup.setTrailerLabel(trailer.getRfidobject().getObjectLabel());
					}
					filter = new FilterImpl();
					filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID,
							truck.getTruckId()));
					ContainerEntity container = getDaoProvider().getContainerEntityDao().findSingle(
							filter);
					if (container != null && container.getRfidobject() != null) {
						setup.setContainerLabel(container.getRfidobject().getObjectLabel());
					}
				}
			}

			send(setup);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private List getAssignments(TruckEntity truck) {
		// [[
		List result = null;
		Object[][] params = { { "truck", truck } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAssignments", params);
		}
		// ]]
		try {
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_TRUCK, truck));
			filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD,
					Constant.ASSIGNMENT_STATUS_CANCELLED));
			filter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD,
					Constant.ASSIGNMENT_STATUS_FINISHED));
			filter.addExpression(Order.asc(AssignmentEntity.ATTR_MIN_START));

			result = getDaoProvider().getAssignmentEntityDao().find(filter);

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

	private LogonMessageOutBean createLogonMessageBean(LogonEventOutBean eventBean) {

		List events = new ArrayList();
		events.add(eventBean);

		return createLogonMessageBean(events);
	}

	private LogonMessageOutBean createLogonMessageBean(List eventBeans) {

		LogonMessageOutBean logonMessage = new LogonMessageOutBean();
		logonMessage.setSendersTime(new Date());
		logonMessage.setLogonEventBeans(eventBeans);
		LogonEventOutBean firstEventBean = (LogonEventOutBean) eventBeans.get(0);
		logonMessage.setSenderPDAId(firstEventBean.getSenderPDAId());
		logonMessage.setSessionId(firstEventBean.getSessionId());

		return logonMessage;
	}

	/**
	 * sends order/drivedata and the rfo at the end
	 * 
	 * @param bean mandatory
	 * @param truck optional
	 */
	public void sendOrderStatusToPDA(PdaSessionEntity session, TruckEntity truck) {
		// [[
		Object[][] params = { { "session", session }, { "truck", truck } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendOrderStatusToPDA", params);
		}
		// ]]
		try {
			if (session == null) {
				return;
			}
			List msgs = new ArrayList();
			// order/drive data(s)
			if (truck != null) {
				Locale locale = new Locale(session.getUser().getApplicationLang().getLangCd());
				List assignmentList = getAssignments(truck);
				if (assignmentList != null) {
					for (Iterator iterator = assignmentList.iterator(); iterator.hasNext();) {
						AssignmentEntity ass = (AssignmentEntity) iterator.next();
						
						List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(ass);

						if (orders.size() >= 1 && orders.get(0).getNoPdaOrRailEventsFlag()) {
							continue;
						}
						List events = getTourData(ass, truck, locale);
						if (!events.isEmpty()) {
							pdaSessionService.initOutgoingEvents(events, session);
							LogonMessageOutBean messageBean = createLogonMessageBean(events);
							msgs.add(messageBean);
						}
					}
				}
			}
			// RFO
			ReadyForOperation rfo = (ReadyForOperation) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_READYFOROPERATION, null);
			pdaSessionService.initOutgoingEvent(rfo, session);
			LogonMessageOutBean messageBean = createLogonMessageBean(rfo);
			msgs.add(messageBean);

			sendMessageBeans(msgs);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private LogonEventOutBean convertEvent(MopeventHeaderOutEntity outevent) {
		// [[
		LogonEventOutBean result = null;
		Object[][] params = { { "outevent", outevent } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertEvent", params);
		}
		// ]]
		try {
			HashMap mopData = new HashMap();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(MopeventDataOutEntity.ATTR_MOPEVENT_HEADER_OUT_ID,
					outevent.getMopeventHeaderOutId()));
			List outdatas = getDaoProvider().getMopeventDataOutEntityDao().find(filter);
			for (Iterator iterator = outdatas.iterator(); iterator.hasNext();) {
				MopeventDataOutEntity outdata = (MopeventDataOutEntity) iterator.next();
				mopData.put(outdata.getName(), outdata.getValue());
			}
			// add dummy values for the orderDatas, otherwise LogonEventBeanFactory fails
			if (LogonEventOutBeanFactory.EVENT_LOADORDER.equals(outevent.getEventName())) {
				mopData.put("loadingpointlist", Constant.NOT_AVAILABLE);
				mopData.put("containerlist", Constant.NOT_AVAILABLE);
				mopData.put("wagonlist", Constant.NOT_AVAILABLE);
			}
			else if (LogonEventOutBeanFactory.EVENT_UNLOADORDER.equals(outevent.getEventName())) {
				mopData.put("customer", Constant.NOT_AVAILABLE);
				mopData.put("unloadorderitems", Constant.NOT_AVAILABLE);
			}

			result = LogonEventOutBeanFactory.create(outevent.getEventName(), mopData);

			result.setSenderPDAId(outevent.getPdaNo());
			result.setSessionId(outevent.getSessionNo());
			result.setEventID(outevent.getEventId());

			// create loadingpointList, shiptoSiloList manually for orderDatas
			if (LogonEventOutBeanFactory.EVENT_LOADORDER.equals(outevent.getEventName())) {
				LoadOrder event = (LoadOrder) result;
				AssignmentPartEntity assignmentPart = getDaoProvider().getAssignmentPartEntityDao().findById(
						new Long(event.getAssignmentPartID()));
				if (assignmentPart == null) {

					return null;
				}
				LoadEntity load = assignmentPart.getLoad();

				filter = new FilterImpl();
				filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, load));
				List orderItems = getDaoProvider().getOrderItemEntityDao().find(filter);
				List loadingpoints = getLoadingpoints(orderItems);
				event.setLoadingPointList(convertLoadingpoints(loadingpoints));
				if (orderItems.size() == 1) {
					OrderItemEntity orderItem = (OrderItemEntity) orderItems.get(0);
					if ((LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem))
							&& (orderItem.getBimodalRailOrderItem() != null)) {
						List wagons = getWagons(orderItem);
						event.setWagonList(convertWagons(wagons));
						if (Constant.LOADING_TYPE_CONTAINER.equals(event.getLoadingType())) {
							List containers = orderService.getContainers(orderItem);
							event.setContainerList(convertContainers(containers));
						}
					}
				}
			}
			else if (LogonEventOutBeanFactory.EVENT_UNLOADORDER.equals(outevent.getEventName())) {
				UnloadOrder event = (UnloadOrder) result;
				AssignmentPartEntity assignmentPart = getDaoProvider().getAssignmentPartEntityDao().findById(
						new Long(event.getAssignmentPartID()));
				if (assignmentPart == null) {

					return null;
				}
				if (assignmentPart.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_EXTERNAL) {
					event.setUnloadOrderItems(convertUnloadOrderItems(null, null, assignmentPart,
							Locale.getDefault()));
				}
				else {
					OrderEntity order = assignmentPart.getOrder();
					ShiptoEntity shipto = getShipto(order);
					LogonRfidObject customerRfid = null;
					event.setCustomer(customerRfid);
					// TODO boon 17.12.09: this whole method needs to be changed, whole tour should be resend and session ID should not be copied like that.
					List orderItems = (List) LogonPackageHandlingUtil.getMaterialItems(order);
					event.setUnloadOrderItems(convertUnloadOrderItems(orderItems, shipto,
							assignmentPart, Locale.getDefault()));
				}
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

	public void resendEvent(MopeventHeaderOutEntity outevent) {
		// [[
		Object[][] params = { { "outevent", outevent } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resendEvent", params);
		}
		// ]]
		try {

			outevent.setNeedsResendingFlag(Boolean.FALSE);
			LogonEventOutBean leob = convertEvent(outevent);
			if (leob != null) {
				send(leob);
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

	@AdnTrace
	private List<LogonEventOutBean> getTourDataForAssignmentId(Long assignmentId) {
		AssignmentEntity assignment = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		List<LogonEventOutBean> msgs = null;
		
		if (assignment != null) {
			TruckEntity truck = assignment.getTruck();
			PdaSessionEntity session = pdaSessionService.getCurrentSession(truck);
			
			if (session != null) {
				Locale locale = new Locale(session.getUser().getApplicationLang().getLangCd());
				msgs = getTourData(assignment, truck, locale);
				
				if ((msgs != null) && (!msgs.isEmpty())) {
					pdaSessionService.initOutgoingEvents(msgs, session);
				}
			}
		}
		
		return msgs;
	}

	@AdnTrace
	private List<LogonEventOutBean> createOutMessagesFromHeaders(List<MopeventHeaderOutEntity> outevents) {
		List<LogonEventOutBean> msgs = new ArrayList<LogonEventOutBean>();
		Set<Long> alreadyProcessedAssignmentIds = new HashSet<Long>();

		for (MopeventHeaderOutEntity currentHeader : outevents) {
			
			if (MopEventUtils.isTourData(currentHeader)) {
				Long assignmentId = mopDataOutStore.getMopDataOutFieldLong(currentHeader, "assignmentid");
				
				if (assignmentId != null) {
					if (!alreadyProcessedAssignmentIds.contains(assignmentId)) {
						alreadyProcessedAssignmentIds.add(assignmentId);
						
						List<LogonEventOutBean> tourData = getTourDataForAssignmentId(assignmentId);
						if (tourData != null && !tourData.isEmpty()) {
							msgs.addAll(tourData);
							
							LOG.info("Resending assignment: " + assignmentId +
									" For the following session: " + tourData.get(0).getSessionId());
						}
					}
				}
				else {
					LOG.error("Could not find assignmentid for the following event header: " +
							currentHeader.getEventId());
				}
			}
			else {
				LogonEventOutBean leob = convertEvent(currentHeader);
				if (leob != null) {
					msgs.add(leob);
					
					LOG.info("Resending event: " + leob.getEventID() + 
							" For the following session: " + leob.getSessionId());
				}
			}

			currentHeader.setNeedsResendingFlag(Boolean.FALSE);
		}

		return msgs;
	}
	
	/**
	 * sends order/drivedata and the rfo at the end
	 * 
	 * @param bean mandatory
	 */
	public void resendOutEvents(PdaSessionEntity session) {
		// [[
		Object[][] params = { { "session", session } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resendOutEvents", params);
		}
		// ]]
		try {
			// set needs resending flag to false for the given pda
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(MopeventHeaderOutEntity.ATTR_SESSION_NO, ""
					+ session.getPdaSessionId()));
			filter.addExpression(Restrictions.eq(MopeventHeaderOutEntity.ATTR_NEEDS_RESENDING_FLAG,
					Boolean.TRUE));
			filter.addExpression(Order.asc(MopeventHeaderOutEntity.ATTR_MOPEVENT_HEADER_OUT_ID));
			
			List<MopeventHeaderOutEntity> outevents = getDaoProvider().getMopeventHeaderOutEntityDao().find(filter);
			List<LogonEventOutBean> msgs = createOutMessagesFromHeaders(outevents);

			// RFO
			ReadyForOperation rfo = (ReadyForOperation) LogonEventOutBeanFactory.create(
					LogonEventOutBeanFactory.EVENT_READYFOROPERATION, null);
			pdaSessionService.initOutgoingEvent(rfo, session);
			msgs.add(rfo);

			// TODO boon, 20091208: This might lead to messages too large for the GPRS connection to handle. Correct would be to send multiple message for which only the tours are contained in a single message.
			// If a message is too large, commserver will request the events seperately using EventNotSent.
			send(createLogonMessageBean(msgs));
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
	 * Sends msg out asynchronously to the client
	 * 
	 * @param client
	 * @param msg
	 */
	private void send(LogonEventOutBean event) {
		send(createLogonMessageBean(event));
	}

	private void send(LogonMessageOutBean messageBean) {
		List messageBeans = new ArrayList();
		messageBeans.add(messageBean);
		sendMessageBeans(messageBeans);
	}

	private void sendMessageBeans(List messageBeans) {
		if ((messageBeans != null) && (!messageBeans.isEmpty())) {
			for (Iterator it = messageBeans.iterator(); it.hasNext();) {
				LogonMessageOutBean messageBean = (LogonMessageOutBean) it.next();
				if (messageBean == null) {
					it.remove();
					continue;
				}
				for (Iterator it2 = messageBean.getLogonEventBeans().iterator(); it2.hasNext();) {
					LogonEventOutBean event = (LogonEventOutBean) it2.next();
					if (event.getEventID() == null) {
						event.setEventID(fetchSequenceNextVal(Constant.SEQUENCE_LOGON_EVENTID));
					}
				}
			}
			callMdb(messageBeans);
		}
	}

	public void callMdb(List messageBeans) {
		// [[
		Object[][] params = { { "messageBeans", messageBeans } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "callMdb", params);
		}
		// ]]
		Connection connection = null;
		MessageProducer messageProducer = null;
		Session session = null;
		
		try {
			InitialContext ctx = new InitialContext();
			
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("java:/ConnectionFactory");
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination destination = (Destination) ctx.lookup("queue/MopQueue");
			messageProducer = session.createProducer(destination);
			
			ObjectMessage message = session.createObjectMessage();
			LogonMessage msg = new LogonMessage(messageBeans, ThreadLocalUserInfo.getUserId(),
					ThreadLocalUserInfo.getAccessRestrictions(),
					ThreadLocalUserInfo.getDisableNextAccessRestrictions());
			message.setObject(msg);
			messageProducer.send(message);
		}
		catch (Exception e) {
			throw new LogonException(e);
		}
		finally {
			try {
				if (messageProducer != null) {				
					messageProducer.close();
				}
				if (session != null) {					
					session.close();
				}
				if (connection != null) {				
					connection.close();		
				}
			}
			catch (JMSException e) {
				String errorMessage = "Error closing message resources";
				LOG.error(errorMessage);
				throw new LogonException(errorMessage, e);
			}
			
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

}

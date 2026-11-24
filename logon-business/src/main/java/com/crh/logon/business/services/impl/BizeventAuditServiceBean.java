/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.naming.InitialContext;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.BizeventAuditServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.BizeventAuditService;
import com.crh.logon.business.services.localinterfaces.BizeventAuditServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.BizeventAuditUtil;
import com.crh.logon.business.services.utils.BizeventAuditWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.BizeventAuditEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Util class in the subject bizevent audits.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class BizeventAuditServiceBean extends BizeventAuditServiceDispatcher implements BizeventAuditServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BizeventAuditServiceBean.class);
	
	@EJB private GoodsMovementServiceLocalInterface goodsMovementService;
	@EJB private DeliverySettingServiceLocalInterface deliverySettingService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	
	/**
	 * Encapsulates the BP id and the role ID for a Bp.
	 * It is important to store not only the BP id but 
	 * the role (is shipto, soldto, billto).
	 * @author AdNovum Informatik AG
	 */
	 class BpKey {
		private final Long bpId;
		/** The "role" of the BP (is shipto, is soldto, is billto) */
		private final Long bpRoleId;
		
		public BpKey(Long bpId, Long bpRelationId) {
			super();
			this.bpId = bpId;
			this.bpRoleId = bpRelationId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((bpId == null) ? 0 : bpId.hashCode());
			result = prime * result + ((bpRoleId == null) ? 0 : bpRoleId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof BpKey) {
				final BpKey that = (BpKey) obj;
				return this.bpId.equals(that.bpId) && 
						this.bpRoleId.equals(that.bpRoleId);
			}
			else {
				// Other object is not even of the same type.
				return false;
			}
		}
	}

	
	
	
	/**
	 * Resolves language dependent fields and sets to {@link BizeventAuditWrapper} oldValueDisplay and newValueDisplay fields to
	 * the resolved by language value.
	 * 
	 * The resolution is according to certain rules where the value is actually stored for the audit config.
	 * 
	 * @param wrappers
	 * @param locale
	 */
	public List<BizeventAuditWrapper> wrapAuditViewEntities(List<BizeventAuditDisplayEntity> audits, Locale locale, TimeZone userTimeZone) {
		// [[
		List<BizeventAuditWrapper> result = null;
		Object[][] params = {{"audits", audits},
							{"locale", locale},
							{"userTimeZone", userTimeZone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "wrapAuditViewEntities", params);
		}
		// ]]
		try {
			
			Map<Long, List<BizeventAuditDisplayEntity>> auditsByConfigCode = new HashMap<Long, List<BizeventAuditDisplayEntity>>();
			
			for (BizeventAuditDisplayEntity audit : audits) {
				if (audit == null) {
					continue;
				}

				Long configCode = audit.getAuditBizeventConfigCd();
				if (configCode != null) {
					List<BizeventAuditDisplayEntity> auditsOfCurrentConfigCode = auditsByConfigCode.get(configCode);

					if (auditsOfCurrentConfigCode == null) {
						auditsOfCurrentConfigCode = new ArrayList<BizeventAuditDisplayEntity>();
						auditsByConfigCode.put(configCode, auditsOfCurrentConfigCode);
					}

					auditsOfCurrentConfigCode.add(audit);
				}
			}
			
			//sets to store the ids to resolve by language
			final Set<BpKey> bpSet = new HashSet<BpKey>();
			final Set<Long> poolSet = new HashSet<Long>();
			final Set<Long> carrierSet = new HashSet<Long>();
			final Set<String> orderStatusSet = new HashSet<String>();
			final Set<String> uomCdSet = new HashSet<String>();
			final Set<Long> loadingpointSet = new HashSet<Long>();
			final Set<Long> shippingpointSet = new HashSet<Long>();
			final Set<Long> deliveryTypeSet = new HashSet<Long>();
			final Set<Long> returnCodeSet = new HashSet<Long>();
			final Set<String> plantSet = new HashSet<String>();
			final Set<Long> siloSet = new HashSet<Long>();
			final Set<Long> spiSet = new HashSet<Long>();
			
			//maps which contain all the resolved ids
			final Map<Long, SapBpEntity> sapBpMap = new HashMap<Long, SapBpEntity>();
			final Map<Long, PoolTxEntity> poolTxMap = new HashMap<Long, PoolTxEntity>();
			final Map<Long, CarrierEntity> carrMap = new HashMap<Long, CarrierEntity>();
			final Map<Long, ShippingpointEntity> shippMap = new HashMap<Long, ShippingpointEntity>();
			final Map<Long, DeliveryTypeTxEntity> delMap = new HashMap<Long, DeliveryTypeTxEntity>();
			final Map<Long, OrderStatusTxEntity> statusTxMap = new HashMap<Long, OrderStatusTxEntity>();
			final Map<String, PlantBsEntity> plantMap = new HashMap<String, PlantBsEntity>();
			final Map<String, UomTxEntity> uomMap = new HashMap<String, UomTxEntity>();
			final Map<Long, LoadingpointEntity> lpMap = new HashMap<Long, LoadingpointEntity>();
			final Map<Long, ShiptoSiloEntity> siloMap = new HashMap<Long, ShiptoSiloEntity>();
			final Map<String, ReturnOrderReasonTxEntity> retMap = new HashMap<String, ReturnOrderReasonTxEntity>();
			final Map<String, OrderSpiTxEntity> spiMap = new HashMap<String, OrderSpiTxEntity>();
			
			final Map<Long, Map<?, ?>> auditCdToResolveMap = new HashMap<Long, Map<?, ?>>();
			
			for (Map.Entry<Long, List<BizeventAuditDisplayEntity>> entry : auditsByConfigCode.entrySet()) {
				
				final Long auditConfigCd = entry.getKey();
				final List<BizeventAuditDisplayEntity> wrapperByCd = entry.getValue();
				
				if ( !BizeventAuditUtil.isAuditBizeventValueImplicit(auditConfigCd)) {
					continue;
				}
				/**
				 * List containing codes to be resolved differently for each column.
				 */
				final Set<?> valueSet = BizeventAuditUtil.getValueListToResolve(wrapperByCd, auditConfigCd);
				
				Map<?, ?> map = null;
				
				if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID) ||
						auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID) ||
						auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID)
						) {
					final Set<Long> bpIdSet = (Set<Long>) valueSet;
					// Also need to store the auditConfigCd for the BP.
					for (Long bpId : bpIdSet) {
						final BpKey key = new BpKey(bpId, auditConfigCd);
						bpSet.add(key);
					}
					
					map = sapBpMap;
				}
				
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID)) {
					poolSet.addAll((Set<Long>) valueSet);
					map = poolTxMap;
				}
				
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID) ||
						 auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID )) {
					carrierSet.addAll((Set<Long>) valueSet);
					map = carrMap;
				}
			
				else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID)) {
					shippingpointSet.addAll((Set<Long>) valueSet);
					map = shippMap;
				}
			
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD)) {
					deliveryTypeSet.addAll((Set<Long>) valueSet);
					map = delMap;
				}
			
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD)) {
					orderStatusSet.addAll((Set<String>) valueSet);
					map = statusTxMap;
				}
			
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD)) {
					plantSet.addAll((Set<String>) valueSet);
					map = plantMap;
				}
			
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD) ||
						auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD) ||
						auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD) ||
						auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD) ) {
					uomCdSet.addAll((Set<String>) valueSet);
					map = uomMap;
				}
			
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID)) {
					loadingpointSet.addAll((Set<Long>) valueSet);
					map = lpMap;
				}
			
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID)) {
					siloSet.addAll((Set<Long>) valueSet);
					map = siloMap;
				}
				
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD)) {
					returnCodeSet.addAll((Set<Long>) valueSet);
					map = retMap;
				}
				
				else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SPI)) {
					spiSet.addAll((Set<Long>) valueSet);
					map = spiMap;
				}
				
				else {
					//for others there is no need to resolve anything
					continue;
				}
				auditCdToResolveMap.put(auditConfigCd, map);
			
			}
			//bulk load tx values for the specified ids and codes
			if (!bpSet.isEmpty()) {
				
				final ReadOnlyDao<SapBpEntity> bpDao = getDaoProvider().getSapBpEntityDao();
				final Filter<SapBpEntity> filter = new FilterImpl<SapBpEntity>();
				
				/* 
				 * LOGON-6400 (Error when selecting "order log" tab in order details)
				 * It is important to filter not only for the BP id but for the role (is shipto, soldto, billto)
				 * because based on this the authorizer will decide to which user restriction should be added. 
				 */
				final List<Expression> expressions = new ArrayList<Expression>();
				for (BpKey key : bpSet) {
					final Long bpId = key.bpId;
					final Long bpRoleId = key.bpRoleId;
					String roleAttributeName = null;
					if (bpRoleId.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID)) {
						roleAttributeName = SapBpEntity.ATTR_BP_IS_SHIPTO;
					}
					else if (bpRoleId.equals(Constant.AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID)) {
						roleAttributeName = SapBpEntity.ATTR_BP_IS_SOLDTO;
					}
					else if (bpRoleId.equals(Constant.AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID)) {
						roleAttributeName = SapBpEntity.ATTR_BP_IS_BILLTO;
					}
					expressions.add(Restrictions.and(
							Restrictions.eq(SapBpEntity.ATTR_SAP_BP_ID, bpId),
							Restrictions.eq(roleAttributeName, Boolean.TRUE)));
				}
				filter.addExpression(Restrictions.or(expressions));
				
				final List<SapBpEntity> sapBpList = bpDao.find(filter);
				for (SapBpEntity sapBp : sapBpList) {
					sapBpMap.put(sapBp.getSapBpId(), sapBp);
				}
			}
			
			if (!poolSet.isEmpty()) {
				
				final List<PoolTxEntity> poolList = deliverySettingService.fetchPoolTxsForPoolIds( poolSet, locale);
				for (PoolTxEntity pool : poolList) {
					poolTxMap.put(pool.getPoolTxPK().getPoolId(), pool);
				}
			}
			
			if (!carrierSet.isEmpty()) {
				
				final ReadOnlyDao<CarrierEntity> carrDao = getDaoProvider().getCarrierEntityDao();
				final Filter<CarrierEntity> carrfilter = new FilterImpl<CarrierEntity>();
				carrfilter.addExpression(Restrictions.in(CarrierEntity.ATTR_CARRIER_ID, carrierSet));
				
				final List<CarrierEntity> carrList = carrDao.find(carrfilter);
				for (CarrierEntity carr : carrList) {
					carrMap.put(carr.getCarrierId(), carr);
				}
			}
			
			if (!shippingpointSet.isEmpty()) {
				
				final ReadOnlyDao<ShippingpointEntity> shippDao = getDaoProvider().getShippingpointEntityDao();
				final Filter<ShippingpointEntity> stFilter = new FilterImpl<ShippingpointEntity>();
				stFilter.addExpression(Restrictions.in(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID, shippingpointSet));
				
				final List<ShippingpointEntity> shippList = shippDao.find(stFilter);
				for (ShippingpointEntity shipp : shippList) {
					shippMap.put(shipp.getShippingpointId(), shipp);
				}
			}
			
			if (!deliveryTypeSet.isEmpty()) {
				
				final Filter<DeliveryTypeTxEntity> delFilter = new FilterImpl<DeliveryTypeTxEntity>();
				delFilter.addExpression(Restrictions.in("deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd", deliveryTypeSet));
				delFilter.addExpression(Restrictions.eq("deliveryTypeTxPK.langCd", locale.getLanguage()));
				
				final List<DeliveryTypeTxEntity> delList = referentialDataService.findTxEntityFiltered(DeliveryTypeTxEntity.class, delFilter);
				for (DeliveryTypeTxEntity del : delList) {
					delMap.put(del.getDeliveryTypeTxPK().getDeliveryTypeBs().getDeliveryTypeCd(), del);
				}
			}
		
			if (!orderStatusSet.isEmpty()) {
				
				final Filter<OrderStatusTxEntity> stFilter = new FilterImpl<OrderStatusTxEntity>();
				stFilter.addExpression(Restrictions.in("orderStatusTxPK.orderStatusBs.orderStatusCd", orderStatusSet));
				stFilter.addExpression(Restrictions.eq("orderStatusTxPK.langCd", locale.getLanguage()));
				
				final List<OrderStatusTxEntity> statusTxList = referentialDataService.findTxEntityFiltered(OrderStatusTxEntity.class, stFilter);
				for (OrderStatusTxEntity statusTx : statusTxList) {
					statusTxMap.put(statusTx.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd(), statusTx);
				}
			}
			
			if (!plantSet.isEmpty()) {
				
				final Filter<PlantBsEntity> plantFilt = new FilterImpl<PlantBsEntity>();
				plantFilt.addExpression(Restrictions.in(PlantBsEntity.ATTR_PLANT_CD, plantSet));	
				
				final List<PlantBsEntity> plantList = referentialDataService.findBsEntityFiltered(PlantBsEntity.class, plantFilt);
				for (PlantBsEntity plant : plantList) {
					plantMap.put(plant.getPlantCd(), plant);
				}
			}
			
			if (!uomCdSet.isEmpty()) {
				
				final Filter<UomTxEntity> uomFilt = new FilterImpl<UomTxEntity>();
				uomFilt.addExpression(Restrictions.in("uomTxPK.uomBs.uomCd", uomCdSet));
				uomFilt.addExpression(Restrictions.eq("uomTxPK.langCd", locale.getLanguage()));
				
				final List<UomTxEntity> uomList = referentialDataService.findTxEntityFiltered(UomTxEntity.class, uomFilt);
				for (UomTxEntity uom : uomList) {
					uomMap.put(uom.getUomTxPK().getUomBs().getUomCd(), uom);
				}
			}
			
			if (!carrierSet.isEmpty()) {
				
				final ReadOnlyDao<LoadingpointEntity> lpDao = getDaoProvider().getLoadingpointEntityDao();
				final Filter<LoadingpointEntity> lpfilter = new FilterImpl<LoadingpointEntity>();
				lpfilter.addExpression(Restrictions.in(LoadingpointEntity.ATTR_LOADINGPOINT_ID, carrierSet));
				
				final List<LoadingpointEntity> lpList = lpDao.find(lpfilter);
				for (LoadingpointEntity lp : lpList) {
					lpMap.put(lp.getLoadingpointId(), lp);
				}
			}
			
			if (!siloSet.isEmpty()) {
				
				final ReadOnlyDao<ShiptoSiloEntity> siloDao = getDaoProvider().getShiptoSiloEntityDao();				
				final Filter<ShiptoSiloEntity> shipSilofilter = new FilterImpl<ShiptoSiloEntity>();
				shipSilofilter.addExpression(Restrictions.in(ShiptoSiloEntity.ATTR_SHIPTO_SILO_ID, siloSet));
				
				final List<ShiptoSiloEntity> siloList = siloDao.find(shipSilofilter);
				for (ShiptoSiloEntity silo : siloList) {
					siloMap.put(silo.getShiptoSiloId(), silo);
				}		
			}
			
			if (!returnCodeSet.isEmpty()) {
				
				final Filter<ReturnOrderReasonTxEntity> retFilt = new FilterImpl<ReturnOrderReasonTxEntity>();
				retFilt.addExpression(Restrictions.in("returnOrderReasonTxPK.returnOrderReasonBs.returnOrderReasonCd", returnCodeSet));
				retFilt.addExpression(Restrictions.eq("returnOrderReasonTxPK.langCd", locale.getLanguage()));
				
				final List<ReturnOrderReasonTxEntity> retList = referentialDataService.findTxEntityFiltered(ReturnOrderReasonTxEntity.class, retFilt);
				for (ReturnOrderReasonTxEntity ret : retList) {
					retMap.put(ret.getReturnOrderReasonTxPK().getReturnOrderReasonBs().getReturnOrderReasonCd(), ret);
				}
			}
			
			if (!spiSet.isEmpty()) {
				
				final Filter<OrderSpiTxEntity> spiFilt = new FilterImpl<OrderSpiTxEntity>();
				spiFilt.addExpression(Restrictions.in("orderSpiTxPK.orderSpiBs.orderSpiCd", spiSet));
				spiFilt.addExpression(Restrictions.eq("orderSpiTxPK.langCd", locale.getLanguage()));
				
				final List<OrderSpiTxEntity> spiList = referentialDataService.findTxEntityFiltered(OrderSpiTxEntity.class, spiFilt);
				for (OrderSpiTxEntity spi : spiList) {
					spiMap.put(spi.getOrderSpiTxPK().getOrderSpiBs().getOrderSpiCd(), spi);
				}
			}
			
			final List<BizeventAuditWrapper> wrappers = new ArrayList<BizeventAuditWrapper>();
			
			/**
			 * Creating wrapper objects from the view entities. For those view entities for which
			 * the value is implicit the display value has to be looked up.
			 */
			for (BizeventAuditDisplayEntity entity : audits) {
				
				final BizeventAuditWrapper wrapper = new BizeventAuditWrapper(entity, userTimeZone);
				final Long auditBizeventConfigCd = entity.getAuditBizeventConfigCd();					
				final Map<?, ?> map = auditCdToResolveMap.get(auditBizeventConfigCd);
				
				/**
				 * If the value is ment to be resolved, display the resolved value. Else show the
				 * db value.
				 */
				if (BizeventAuditUtil.isAuditBizeventValueImplicit(auditBizeventConfigCd)) {
					
					if (wrapper.getNewDbValue() != null) {						
						final Object newDbValue = map.get(wrapper.getNewDbValue());
						wrapper.setNewValueDisplay(BizeventAuditUtil.getDisplayText(newDbValue, auditBizeventConfigCd));
						
					}
					if (wrapper.getOldDbValue() != null) {						
						final Object oldDbValue = map.get(wrapper.getOldDbValue());
						wrapper.setOldValueDisplay(BizeventAuditUtil.getDisplayText(oldDbValue, auditBizeventConfigCd));
					}

				}
				else {
					wrapper.setNewValueDisplay(wrapper.getNewDbValue() != null ? wrapper.getNewDbValue().toString() : null);
					wrapper.setOldValueDisplay(wrapper.getOldDbValue() != null ? wrapper.getOldDbValue().toString() : null ) ;
				}
				
				wrappers.add(wrapper);
			}
			return result = wrappers;
			
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
	 * Loads bizevent audits for the specified bizevent ids.
	 * 
	 * @param langCd
	 * @param bizeventIds
	 * @return
	 */
	public List<BizeventAuditDisplayEntity> loadBizeventAudits(String langCd, List<Long> bizeventIds) {
		// [[
		List<BizeventAuditDisplayEntity> result = null;
		Object[][] params = {{"langCd", langCd},
				{"bizeventIds", bizeventIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadBizeventAudits", params);
		}
		// ]]
		try {

			final ReadOnlyDao<BizeventAuditDisplayEntity> auViewDao = getDaoProvider().getBizeventAuditDisplayEntityDao();
			final Filter<BizeventAuditDisplayEntity> filter = auViewDao.createFilter();

			filter.addExpression(Restrictions.in(BizeventAuditDisplayEntity.ATTR_BIZEVENT_ID, bizeventIds));
			filter.addExpression(Restrictions.eq(BizeventAuditDisplayEntity.ATTR_LANG_CD, langCd));
			filter.addExpression(Order.desc(BizeventAuditDisplayEntity.ATTR_EVENT_TIME));

			return result = auViewDao.findNRecs(0,100,filter);
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
	 * @param order
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public OrderEntity _loadDbOrder(final Long orderId) {
		
		OrderEntity dborder = getDaoProvider().getOrderEntityDao().findById(orderId);
		if ( dborder != null ) {			
			dborder.initialize();
		}
		return dborder;
	}

	
	/**
	 * Checks if audited fields have been changed in the order or not. In case they changed 
	 * persists BizeventAuditEntities referencing this event.
	 * 
	 * The bizevent needs to be persisted already at this point, the only exception is the edit bizevent
	 * which is persisted here in case at least one audit bizevent has to be persisted. The purpose
	 * of the edit bizevent is to reference changes field bizevent audits therefore if there is no such
	 * there is no persist.
	 * 
	 * @param event
	 */
	public void persistAuditData(BizeventEntity event) {
		// [[
		Object[][] params = {{"event", event}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "persistAuditData", params);
		}
		// ]]
		try {
			final Long bizeventTypeCd = event.getBizeventType().getBizeventTypeCd();
			final OrderEntity order = event.getOrder();
			
			if ( order == null ) {
				throw new LogonException("Cannot persist order related audit data without an order.", Reason.RES_ERROR_IMPL);
			}
			
			BizeventAuditService bizeventAuditService = null;
			
			try {
				//accessing through jndi lookup as TransactionAttributeType.REQUIRES_NEW needs to take effect!
				final InitialContext ctx = new InitialContext();
				bizeventAuditService = (BizeventAuditService) ctx.lookup("java:global/logon/logon-business//BizeventAuditServiceBean!com.crh.logon.business.services.interfaces.BizeventAuditService");
			}
			catch (Exception e) {
				throw new LogonException("Lookup failed for BizeventAuditService!", e);
			}

			//load the order in a complete new transaction that none of the changes are visible
			OrderEntity dborder = bizeventAuditService._loadDbOrder(order.getOrderId());
			if ( dborder == null ) {
				return;
			}
			
			final List <BizeventAuditEntity> orderAudits = BizeventAuditUtil.getOrderAudits(order, dborder);
			
			BizeventAuditUtil.addMandatoryBizeventAudit(bizeventTypeCd, order, dborder, orderAudits);

			//in the case of edit bizevent persist if there is at least one audit.
			if ( bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_EDIT_FIELD) && !orderAudits.isEmpty()) {
				monitoringTablesService.processBizevent(event);
			}
			
			for ( BizeventAuditEntity audit : orderAudits ) {
				audit.setBizevent(event);
				getDaoProvider().getBizeventAuditEntityDao().persist(audit);
			}
			
			if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_EDIT_FIELD)
					&& isDeliveredQuantityChanged(orderAudits)
					&& Constant.ORDER_STATUS_DELIVERED == dborder.getOrderStatus().getOrderStatusCd()) {				
				goodsMovementService.createGoodsMovementEntity(order, false, true, false);
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
	
	private boolean isDeliveredQuantityChanged(List<BizeventAuditEntity> orderAudits) {
		
		if (orderAudits == null || orderAudits.isEmpty()) {
			return false;
		}
		
		for (BizeventAuditEntity audit : orderAudits) {
			if (Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY.equals(audit.getAuditBizeventConfig().getAuditBizeventConfigCd())) {
				return true;
			}
		}
		return false;
	}
	
}

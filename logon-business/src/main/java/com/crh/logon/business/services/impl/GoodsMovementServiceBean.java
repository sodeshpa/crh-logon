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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.utils.GoodsMovementUtils;
import com.crh.logon.constants.Constant;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.GoodsMovementServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SapOutgoingDataServiceLocalInterface;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.GoodsMovementHelperEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementDTO;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementItemDTO;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class GoodsMovementServiceBean extends GoodsMovementServiceDispatcher implements GoodsMovementServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GoodsMovementServiceBean.class);

	@EJB private GoodsMovementServiceLocalInterface goodsMovementService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private SapOutgoingDataServiceLocalInterface sapOutgoingDataService;
	
	/**
	 * Based on the <code>load<code> parameter, decide (if needed) create a new Goods movement entry.
	 * If the load already has a goods movement record --. first cancel it, and create the new one
	 * 
	 * @param load
	 * 			The {@link LoadEntity} to generate for the GM record
	 * @param orderItem
	 * 			At return handling and credit note proces we need the order item return information.
	 * 			Nullable.
	 * @param isLoadingGM
	 * 			Is the Gm record created at endload
	 * @param isReturnGM
	 * 			Is the GM created at return.
	 * @param isCreditNoteGM
	 * 			Is the GM created at credit note process.
	 */
	public GoodsMovementEntity createGoodsMovementEntity(LoadEntity load, OrderItemEntity orderItem,
			boolean isLoadingGM, boolean isReturnGM, boolean isCreditNoteGM) {
		
		if (load == null) {
			String errorMsg = "The 'load' parameter is null!";
			LOG.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}
		
		// In the case of pre-loading, the gmHelper can still be null after this, which is OK
		List<GoodsMovementHelperEntity> gmHelperList = getGoodsMovementHelperList(load, null);
		GoodsMovementHelperEntity gmHelper = null;
		if(gmHelperList != null && !gmHelperList.isEmpty()){
			gmHelper = gmHelperList.get(0);
		}
		
		SapMaterialEntity sapMaterial = load.getSapMaterial();	
		List<OrderItemEntity> orderItemList = orderService.getOrderItems(load);
		
		final boolean canCreateGM = GoodsMovementUtils.isGMRequiredForLoadAndOrderItems(load, orderItemList);
		if (!canCreateGM) {
			return null;
		}
		
		/*
		 * Now starting to create the goods movement
		 */
		GoodsMovementEntity e = new GoodsMovementEntity();
		
		/*
		 * Do the general part (independent of GM type etc.)
		 */
		e.setLoad(load); // FK
		e.setSapMaterial(sapMaterial); // FK
		e.setIsEndloadGm(isLoadingGM);
		e.setIsCreditNoteGm(isCreditNoteGM);
		e.setIsReturnGm(isReturnGM);
		
		final String referenceDocumentNumber =  gmHelper == null ? null : gmHelper.getRefDocNrSap();
		e.setReferenceDocumentNumberSap(referenceDocumentNumber);
		
		final String movementType;
		final PlantBsEntity fromPlant;
		final String fromSloc;
		final PlantBsEntity toPlant;
		final String toSloc;
		final BigDecimal netQuantity;
		final UomBsEntity netQuantityUom;
		
		final LoadEntity loadedFromLoad = load.getLoadedFromLoad();
		final boolean loadedFromAnotherLoad = (loadedFromLoad != null);
		
		final ShippingpointEntity originalFromSP;
		if (loadedFromAnotherLoad) {
			originalFromSP = getShippingpointFromLoad(loadedFromLoad);
			if (originalFromSP == null) {
				final String msg = String.format("The shipping point of loadedFromLoad %d cannot " +
						"be determined, hence the GMR for load %d cannot be created.",
						loadedFromLoad.getLoadId(), load.getLoadId());
				throw new LogonException(msg, Reason.RES_ERROR_UNSPECIFIED);
			}
		}
		else {
			originalFromSP = getShippingpointFromLoad(load);
			if (originalFromSP == null) {
				final String msg = String.format("The shipping point of load %d cannot " +
						"be determined, hence the GMR cannot be created.", load.getLoadId());
				throw new LogonException(msg, Reason.RES_ERROR_UNSPECIFIED);
			}
		}
		
		final ShippingpointEntity originalToSP = getShippingpointFromLoad(load);
		if (originalToSP == null) {
			final String msg = String.format("The shipping point of load %d cannot " +
					"be determined, hence the GMR cannot be created.", load.getLoadId());
			throw new LogonException(msg, Reason.RES_ERROR_UNSPECIFIED);
		}
		
		
		/*
		 * Now start with the most important distinction: loading, return or credit note
		 */
		if (isLoadingGM) {
			
			// from plant
			final ShippingpointEntity fromSP = originalFromSP;
			fromPlant = fromSP.getPlant();
			
			// to plant
			final ShippingpointEntity toSP = originalToSP;
			toPlant = toSP.getPlant();
			
			/*
			 * Continue with the second distinction:
			 *  * either a bimodal road part or container case -> 301
			 *  * or any other case -> 311, 312, 551
			 */
			if (loadedFromAnotherLoad) {
				// type
				movementType = GoodsMovementItemDTO.TR_BETWEEN_PLANTS;
				
				// from SLOC
				fromSloc = getVirtualOrRealSloc(fromSP, sapMaterial);
				if (fromSloc == null) {
					return null;
				}
				
				// to SLOC
				toSloc = getVirtualOrRealSloc(toSP, sapMaterial);
				if (toSloc == null) {
					return null;
				}
			}
			else {
				// type
				movementType = GoodsMovementItemDTO.TR_WITHIN_PLANT;
				
				// from SLOC
				fromSloc = getRealStorageLocation(fromSP, sapMaterial);
				if (StringUtils.isEmpty(fromSloc)) {
					final String msg = String.format("The real storage location is undefined " +
							"forshipping point [%s, %s]. Cannot create GMR.",
							fromSP.getSapShippingpointNo(), fromSP.getShippingpointName());
					LOG.warn(msg);
					return null;
				}
				
				// to SLOC
				toSloc = GoodsMovementUtils.getVirtualStorageLocation(toPlant, sapMaterial);
				if (StringUtils.isEmpty(toSloc)) {
					final String msg = String.format("The virtual storage location is undefined " +
							"forshipping point [%s, %s]. No need to create a GMR.",
							toSP.getSapShippingpointNo(), toSP.getShippingpointName());
					LOG.info(msg);
					return null;
				}
			}
			
			/*
			 * Quantity and unit are independent of that
			 */
			netQuantity = load.getNetQuantity();
			netQuantityUom = load.getNetQuantityUom();
		}
		else {
			
			/*
			 * Returns and credit notes are possible only if the order item is given and has the
			 * necessary fields filled.
			 * Additionally, exception discovered in LOGON-6649 and LOGON-6679: this service can
			 * also be called for a load and order item which don't have a return quantity, where
			 * we must do nothing.
			 * Additionally if the return quantity handling is set to no adjustment, then returns should not
			 * trigger goods movement.
			 */
			if (orderItem != null && orderItem.getReturnedQuantity() == null ||
					(orderItem.getReturnQuantHandling() != null && orderItem.getReturnQuantHandling().getReturnQuantHandlingCd().equals(
							Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT))){
				return null;
			}
			else {
				GoodsMovementUtils.validateOrderItem(orderItem);
			}
			
			final boolean isRefillingReturn = GoodsMovementUtils.isRefillingReturn(orderItem);
			final boolean isMixQualityReturn = GoodsMovementUtils.isMixQualityReturn(orderItem);
			final boolean isRubbishReturn = GoodsMovementUtils.isRubishReturn(orderItem);
			
			final ShippingpointEntity returnSP;
			{
				final ShippingpointEntity tmp = orderItem.getReturnShippingpoint();
				if (tmp.getSendSapShippingpoint() != null) {
					returnSP = tmp.getSendSapShippingpoint();
				}
				else {
					returnSP = tmp;
				}
			}
			
			/*
			 * Here, it is easiest to look at the from plant and to plant independently. The value of
			 * loadedFromAnotherLoad decides on the to plant, while the value of isCreditNoteGM
			 * decides on the from plant.
			 */
			// from shipping point and plant
			final ShippingpointEntity fromSP;
			if (isCreditNoteGM) {
				/*
				 * Currently, the CN is not implemented to respect the return plant and shipping
				 * point, hence the from plant for this GMR must currently not be the return plant.
				 * Discussions are ongoing though ...
				 */
				//fromSP = returnSP;
				fromSP = originalToSP;
			}
			else {
				fromSP = originalToSP;
			}
			fromPlant = fromSP.getPlant();
			
			// to shipping point and plant
			final ShippingpointEntity toSP;
			if (loadedFromAnotherLoad) {
				toSP = originalFromSP;
			}
			else {
				toSP = returnSP;
			}
			toPlant = toSP.getPlant();
			
			final boolean isSamePlant = fromPlant.getPlantCd().equals(toPlant.getPlantCd());
			
			/*
			 * At this point, the specific use case must be distinguished, since a 312 must not be
			 * sent if the fromSloc is not virtual, and from/to are exchanged (back to original
			 * direction).
			 */
			if (isSamePlant && isRefillingReturn) {
				/*
				 * Here, we should distinguish the case where an automatic return for the rail part
				 * has been created from the case where it has not. For now, we assume it has, so we
				 * must not send a GMR 312 anymore for the rail part.
				 */
				if (loadedFromAnotherLoad) {
					final String msg = String.format("Assuming an automatic return has been created " +
							"we do not need to send any GMR at this point. [load: %d]",
							load.getLoadId());
					LOG.info(msg);
					return null;
				}
				// type
				movementType = GoodsMovementItemDTO.TR_SAME_PLANT_VIRT_TO_REAL_SLOC;
				
				// from SLOC
				fromSloc = getRealStorageLocation(toSP, sapMaterial);
				if (StringUtils.isEmpty(fromSloc)) {
					final String msg = String.format("The real storage location is undefined " +
							"forshipping point [%s, %s]. Cannot create GMR.",
							toSP.getSapShippingpointNo(), toSP.getShippingpointName());
					LOG.warn(msg);
					return null;
				}
				
				// to SLOC
				toSloc = GoodsMovementUtils.getVirtualStorageLocation(fromPlant, sapMaterial);
				if (StringUtils.isEmpty(toSloc)) {
					final String msg = String.format("The virtual storage location is undefined " +
							"forshipping point [%s, %s]. No need to create a GMR.",
							fromSP.getSapShippingpointNo(), fromSP.getShippingpointName());
					LOG.info(msg);
					return null;
				}
			}
			else {
				
				// from SLOC
				fromSloc = getVirtualOrRealSloc(fromSP, sapMaterial);
				if (fromSloc == null) {
					return null;
				}
				
				if (isRubbishReturn) {
					
					// type
					movementType = GoodsMovementItemDTO.TR_RUBISH;
					
					// to SLOC
					toSloc = null;
				}
				else {
					
					// type
					movementType = GoodsMovementItemDTO.TR_BETWEEN_PLANTS;
					
					// to SLOC
					if (loadedFromAnotherLoad) {
						/*
						 * This implicitly ignores (overrules) the case of isMixQualityReturn, which
						 * is not applicable.
						 */
						if (isMixQualityReturn) {
							LOG.warn("Mix quality return is not applicable to loads which have been "+
									"loaded from another load.");
						}
						
						/*
						 * Furthermore, this also assumes that an automatic return for the rail part
						 * has been created. To be precise, we should check that and enforce the real
						 * storage location if that is not the case.
						 */
						toSloc = getVirtualOrRealSloc(toSP, sapMaterial);
						if (toSloc == null) {
							return null;
						}
					}
					else {
						
						if (isMixQualityReturn) {
							toSloc = toPlant.getStorageLocMixQuality();
							if (StringUtils.isEmpty(toSloc)) {
								final String msg = String.format("The mix quality storage location is " +
										"undefined for plant [%s, %s]. Cannot create GMR",
										toPlant.getPlantCd(), toPlant.getPlantName());
								LOG.warn(msg);
								return null;
							}
						}
						else {
							toSloc = getRealStorageLocation(toSP, sapMaterial);
							if (StringUtils.isEmpty(toSloc)) {
								final String msg = String.format("The real storage location is undefined " +
										"forshipping point [%s, %s]. Cannot create GMR",
										toSP.getSapShippingpointNo(), toSP.getShippingpointName());
								LOG.warn(msg);
								return null;
							}
						}
					}
				}
			}
			
			/*
			 * Quantity and unit are independent of that
			 */
			netQuantity = orderItem.getReturnedQuantity();
			netQuantityUom = orderItem.getReturnedUom();
		}
		
		e.setFromPlant(fromPlant);
		e.setFromStorageLoc(fromSloc);
		e.setMovementType(movementType);
		if (GoodsMovementItemDTO.TR_RUBISH.equals(movementType)) {
			e.setToPlant(null);
			e.setToStorageLoc(null);
		}
		else {
			e.setToPlant(toPlant);
			e.setToStorageLoc(toSloc);
		}
		e.setNetQuantity(netQuantity);
		e.setNetQuantityUom(netQuantityUom);
		
		e.setSentGoodsmoveToQueueTs(new Date());
		
		// create a cancel goods movement, if needed
		GoodsMovementEntity cancelGoodsMovementEntity = getCancelGoodsMovement(e, isLoadingGM, isReturnGM, isCreditNoteGM);
		
		/*
		 * if the cancel goods movement has the same baseUom and net quantity, then do nothing!
		 * REASON: 
		 * The goods movement record is already generated (and maybe already sent) --> No need to cancel
		 * and resend the same values. 
		 */
		ReadWriteDao<GoodsMovementEntity> goodsMovementEntityDao = getDaoProvider().getGoodsMovementEntityDao();
		if (cancelGoodsMovementEntity != null) {
			// TODO (bartha Oct 30, 2012): maybe we need to do more check, not the quantity/uom change
			/*
			boolean isSameUom = cancelGoodsMovementEntity.getNetQuantityUom() != null
					&& e.getNetQuantityUom() != null
					&& cancelGoodsMovementEntity.getNetQuantityUom().getUomCd().equals(
							e.getNetQuantityUom().getUomCd());
			
			boolean isSameNetQuantity = cancelGoodsMovementEntity.getNetQuantity() != null
					&& e.getNetQuantity() != null
					&& cancelGoodsMovementEntity.getNetQuantity().equals(e.getNetQuantity());
			
			if (isSameUom && isSameNetQuantity) {
				return null;
			}
			*/
			
			goodsMovementEntityDao.persist(cancelGoodsMovementEntity);
		}
		
		return goodsMovementEntityDao.persist(e);
	}
	
	/**
	 * Check if the given <code>goodsMovementEntity</code> already has a record in tlgnc_goods_movment.
	 * If has, create and return a canceletion record.
	 */
	public GoodsMovementEntity getCancelGoodsMovement(GoodsMovementEntity goodsMovementEntity,
			boolean isLoadingGM, boolean isReturnGM, boolean isCreditNoteGM) {
		// [[
		GoodsMovementEntity result = null;
		Object[][] params = {{"goodsMovementEntity", goodsMovementEntity},
							{"isLoadingGM", isLoadingGM + ""},
							{"isReturnGM", isReturnGM + ""},
							{"isCreditNoteGM", isCreditNoteGM + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCancelGoodsMovement", params);
		}
		// ]]
		try {
			if (goodsMovementEntity == null) {
				String errorMsg = "The 'goodsMovementEntity' parameter cannot be <NULL>";
				
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}

			List<GoodsMovementEntity> list = getGoodsMovementList(
					goodsMovementEntity.getLoad().getLoadId(), isLoadingGM, isReturnGM,
					isCreditNoteGM);
			
			if (list == null || list.isEmpty()) {
				LOG.debug("No goods movement found for the cancel...");
				
				return result = null;
			}
				
			GoodsMovementEntity foundGoodsMovementEntity = list.get(0);
			
			GoodsMovementEntity stornoEntity = GoodsMovementUtils.getCancelGoodsMovementEntity(foundGoodsMovementEntity);
			
			return result = stornoEntity;
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
	 * Load a list of {@link GoodsMovementEntity} based on the given parameters.
	 * 
	 * @param loadId
	 * 			The load id
	 * @param isLoadingGM
	 * 			Is the goods movement created at "endload"
	 * @param isReturnGM
	 * 			Is goods movement created at Return handling
	 * @param isCreditNoteGM
	 * 			Is goods movement created at credit note process.
	 * @return
	 * 		A list of {@link GoodsMovementEntity}, in order descending by {@link GoodsMovementEntity#getSentGoodsmoveToQueueTs()}
	 */
	private List<GoodsMovementEntity> getGoodsMovementList(Long loadId, boolean isLoadingGM, boolean isReturnGM, boolean isCreditNoteGM) {
		// [[
		List<GoodsMovementEntity> result = null;
		Object[][] params = {{"loadId", loadId},
							{"isLoadingGM", isLoadingGM + ""},
							{"isReturnGM", isReturnGM + ""},
							{"isCreditNoteGM", isCreditNoteGM + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getGoodsMovementList", params);
		}
		// ]]
		try {
			if (loadId == null) {
				String errorMsg = "The 'loadId' parameter cannot be <NULL>";
				
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			final ReadOnlyDao<GoodsMovementEntity> dao = getDaoProvider().getGoodsMovementEntityDao();
			final Filter<GoodsMovementEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_LOAD_ID, loadId));
			filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_IS_ENDLOAD_GM, isLoadingGM));
			filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_IS_RETURN_GM, isReturnGM));
			filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_IS_CREDIT_NOTE_GM, isCreditNoteGM));
			filter.addExpression(Order.desc(GoodsMovementEntity.ATTR_CTL_CRE_TS));
			
			// the previous cancel GM and the previous GM can have the same TS, even though the 
			// the cancel GM is to be considered older. Using the id in addition ensures that
			// see LOGON-7290
			filter.addExpression(Order.desc(GoodsMovementEntity.ATTR_GOODS_MOVEMENT_ID));
			
			//we need only the first one from the descending order
			return result = dao.findNRecs(0, 1, filter);
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
	 * Returns all goods movement entries for a load.
	 */
	public List<GoodsMovementEntity> getAllGoodsMovementsForLoad(Long loadId) {
		// [[
		List<GoodsMovementEntity> result = null;
		Object[][] params = {{"loadId", loadId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAllGoodsMovementsForLoad", params);
		}
		// ]]
		try {
			if (loadId == null) {
				String errorMsg = "The 'loadId' parameter cannot be <NULL>";
				
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			final ReadOnlyDao<GoodsMovementEntity> dao = getDaoProvider().getGoodsMovementEntityDao();
			final Filter<GoodsMovementEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_LOAD_ID, loadId));
			filter.addExpression(Order.desc(GoodsMovementEntity.ATTR_CTL_CRE_TS));
			
			// the previous cancel GM and the previous GM can have the same TS, even though the 
			// the cancel GM is to be considered older. Using the id in addition ensures that
			// see LOGON-7290
			filter.addExpression(Order.desc(GoodsMovementEntity.ATTR_GOODS_MOVEMENT_ID));
			
			return result = dao.find(filter);
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
	 * Returns true if any goods movement is present for the given loadId, false otherwise.
	 */
	public Boolean goodsMovementsPresentForLoad(Long loadId) {
		// [[
		Boolean result1 = null;
		Object[][] params = {{"loadId", loadId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "goodsMovementsPresentForLoad", params);
		}
		// ]]
		try {
			Filter<GoodsMovementEntity> filter = new FilterImpl<GoodsMovementEntity>();
			filter.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_LOAD_ID, loadId));
			
			//we need only the first one from the descending order
			ReadOnlyDao<GoodsMovementEntity> goodsMovementEntityDao = getDaoProvider().getGoodsMovementEntityDao();
			long count = goodsMovementEntityDao.count(filter);
			return result1 = 0 < count;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	/**
	 * 
	 * @param load
	 * @param order
	 * @return
	 */
	public List<GoodsMovementHelperEntity> getGoodsMovementHelperList(LoadEntity load, OrderEntity order) {
		// [[
		List<GoodsMovementHelperEntity> result = null;
		Object[][] params = {{"load", load},
							{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getGoodsMovementHelperList", params);
		}
		// ]]
		try {
			
			if (load == null) {
				String errorMsg = "The 'load' parameter is <NULL>";
				LOG.error(errorMsg);
				
				throw new IllegalArgumentException(errorMsg);
			}
			
			ReadOnlyDao<GoodsMovementHelperEntity> dao = getDaoProvider().getGoodsMovementHelperEntityDao();
			Filter<GoodsMovementHelperEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(GoodsMovementHelperEntity.ATTR_LOAD_ID, load.getLoadId()));
			if (order != null) {
				f.addExpression(Restrictions.eq(GoodsMovementHelperEntity.ATTR_ORDER_ID, order.getOrderId()));
			}
			f.addExpression(Order.asc(GoodsMovementHelperEntity.ATTR_PLANNED_START));
			return result = dao.find(f);
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
	 * Create goods movement records for the given <code>orderEntry</code>, the goods movements
	 * are created for the order items, if the GM is needed based on the order items load entity.
	 * 
	 * @param orderEntity
	 * @param isLoadingGM
	 * @param isReturnGM
	 * @param isCreditNoteGM
	 * @return
	 */
	public List<GoodsMovementEntity> createGoodsMovementEntity(OrderEntity orderEntity,
			boolean isLoadingGM, boolean isReturnGM, boolean isCreditNoteGM) {
		// [[
		List<GoodsMovementEntity> result = null;
		Object[][] params = {{"orderEntity", orderEntity},
							{"isLoadingGM", isLoadingGM + ""},
							{"isReturnGM", isReturnGM + ""},
							{"isCreditNoteGM", isCreditNoteGM + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createGoodsMovementEntity", params);
		}
		// ]]
		try {
			
			if (orderEntity == null) {
				LOG.warn("The 'orderEntity' parameter is null");
				
				return result = null;
			}
			
			List<OrderItemEntity> orderItemList = orderEntity.getOrderItems();
			
			if (orderItemList == null || orderItemList.isEmpty()) {
				LOG.warn("The order item list is empty for order id:'" + orderEntity.getOrderId() + "'");
				
				return result = null;
			}
			
			result = new ArrayList<GoodsMovementEntity>();
			for (OrderItemEntity orderItemEntity : orderItemList) {
				if (orderItemEntity.getLoad() == null) {
					continue; // the load is not mandatory for the order item --> if not defined skip
				}
				
				LoadEntity loadEntity = orderItemEntity.getLoad();
	
				GoodsMovementEntity entity = createGoodsMovementEntity(loadEntity, orderItemEntity,
						isLoadingGM, isReturnGM, isCreditNoteGM);
				if (entity != null) {
					result.add(entity);
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
	
	/**
	 * Create a goods movement (if the GM requirements match) record for the give order item.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public GoodsMovementEntity createGoodsMovementEntity(Long orderItemId, boolean isLoadingGM,
			boolean isReturnGM, boolean isCreditNoteGM) {
		// [[
		GoodsMovementEntity result = null;
		Object[][] params = {{"orderItemId", orderItemId},
							{"isLoadingGM", isLoadingGM + ""},
							{"isReturnGM", isReturnGM + ""},
							{"isCreditNoteGM", isCreditNoteGM + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createGoodsMovementEntity", params);
		}
		// ]]
		try {
			OrderItemEntity orderItem = getDaoProvider().getOrderItemEntityDao().findById(orderItemId);
			
			if (orderItem == null || orderItem.getLoad() == null) {
				return result = null;				
			}
			
			LoadEntity loadEntity = orderItem.getLoad();

			return result = createGoodsMovementEntity(loadEntity, orderItem,
					isLoadingGM, isReturnGM, isCreditNoteGM);
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
	 * This method sends all the undelivered goodsmovement notifications from
	 * logon to SAP. Unsent means, that the connection to SAP could not be
	 * established, at the time the event occured. All the goodmovements which
	 * do not have the timestamp ATTR_SENT_GOODSMOVE_TO_SAP_TS are unset until
	 * the call. This method makes another attempt to send all these
	 * notifications again.
	 */
	public void sendAllUnsentGoodsMovementNotificationsToSap() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendAllUnsentGoodsMovementNotificationsToSap");
		}
		// ]]
		try {

			// Loading all GoodsMovements which have to be sent
			Filter<GoodsMovementEntity> filter = new FilterImpl<GoodsMovementEntity>();
			filter.addExpression(Restrictions.isNull(GoodsMovementEntity.ATTR_SENT_GOODSMOVE_TO_SAP_TS));
			filter.addExpression(Order.asc(GoodsMovementEntity.ATTR_SENT_GOODSMOVE_TO_QUEUE_TS)); // FIFO

			List<GoodsMovementEntity> unsentGoodsMovementEntityList = getDaoProvider().getGoodsMovementEntityDao().find(filter);

			LOG.info(unsentGoodsMovementEntityList.size() + " goodsmovements will be sent to the goodsmovement interface");

			StringBuilder failedGoodsMovements = null;
			
			// Iterating over the all the GoodsMovements which have to be sent
			for (GoodsMovementEntity unsentGoodsMovementEntity : unsentGoodsMovementEntityList) {

				try {
					// try to send the goodsmovements
					goodsMovementService._sendSapGoodsMovement(unsentGoodsMovementEntity.getGoodsMovementId());
				}
				catch ( Exception e ) {
					LOG.error("Failed to send goodsmovement to goodsmovement interface. Goodsmovement id=" + 
							unsentGoodsMovementEntity.getGoodsMovementId() + 
							", load id=" + 
							(unsentGoodsMovementEntity.getLoad() != null ? unsentGoodsMovementEntity.getLoad().getLoadId() : "null") + 
							".");
					ExceptionUtil.errException(LOG, e, null);
					if (failedGoodsMovements == null) {
						failedGoodsMovements = new StringBuilder();
					}
					else {
						failedGoodsMovements.append(", ");						
					}
					failedGoodsMovements.append(unsentGoodsMovementEntity.getGoodsMovementId());
				}
			}
			
			if (failedGoodsMovements != null) {
				LOG.error("The following goodsmovements were not sent to the goodsmovement interface: Goodsmovement id in [" + failedGoodsMovements + "].");
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
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _sendSapGoodsMovement(Long goodsMovementId) {
		if (goodsMovementId == null) {
			String errorMsg = "The 'goods movement id' cannot be <NULL>";
			LOG.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}
		
		ReadWriteDao<GoodsMovementEntity> goodsMovementDao = getDaoProvider().getGoodsMovementEntityDao();
		GoodsMovementEntity goodsMovementEntity = goodsMovementDao.findById(goodsMovementId);
		
		if (goodsMovementEntity == null) {
			LOG.error("Couldn't load goods movement with Id: '" + goodsMovementId + "'");
			throw new LogonException();
		}
		
		/*
		 * LOGON-6323: In order to make sure, that the GMRs are not sent more than once, do the steps:
		 * 1.) The tlgnc_goods_movement.SENT_GOODSMOVE_TO_SAP_TS is null
		 * 2.) Update the tlgnc_goods_movement.SENT_GOODSMOVE_TO_SAP_TS with current timestamp
		 * and call the merge on the entity with flush. 
		 * (NOTE: This step MUST be before the SapOutgoingDataService#sendGoodsMovement() service call,
		 * with this we can make sure nobody changed the entity. As if flush fails with PersistenceException)
		 * 3.) Call SapOutgoingDataService#sendGoodsMovement() to send the GMR to SAP. The service
		 * will throw an unchecked LogonException, if cannot send the GMR, which will trigger a
		 * rollback on the current transaction --> nothing is saved, and nothing is sent. 
		 * But!!! Only if quantity is greater than zero 
		 */
		
		// STEP 1
		if (goodsMovementEntity.getSentGoodsmoveToSapTs() != null) {
			LOG.warn("The Goods Movement with ID: '" + goodsMovementId + "', was already sent to SAP!");
			return; // should never happen
		}
		// STEP 2
		// Set and persist the timestamp to the GoodsMovementEntity so the order can be finalized later
		Date timeNow = Calendar.getInstance().getTime();
		goodsMovementEntity.setSentGoodsmoveToSapTs(timeNow);
		goodsMovementDao.merge(goodsMovementEntity, true);
		
		// STEP 3
		GoodsMovementDTO goodsMovementDto = sapOutgoingDataService.getGoodsMovementDTOfromGoodsMovementEntity(goodsMovementEntity);

		// Can only send if quantity is greater than zero because SAP would faint otherwise 
		if (goodsMovementEntity.getNetQuantity().compareTo(BigDecimal.ZERO) > 0) {
			sapOutgoingDataService.sendGoodsMovement(goodsMovementDto);
		}
	}
	
	/**
	 * Removes the load reference from all GMs which refer to the given load. Used when a load
	 * is to be deleted.
	 * @param load The load whose reference should be deleted
	 */
	public void removeLoadReferenceFromGoodsMovements(LoadEntity load) {
		
		final ReadWriteDao<GoodsMovementEntity> dao = getDaoProvider().getGoodsMovementEntityDao();
		final Filter<GoodsMovementEntity> f = dao.createFilter();
		
		f.addExpression(Restrictions.eq(GoodsMovementEntity.ATTR_LOAD_ID, load.getLoadId()));
		final List<GoodsMovementEntity> list = dao.find(f);
		
		for (GoodsMovementEntity gm : list) {
			gm.setLoad(null);
		}
	}
	
	private ShippingpointEntity getShippingpointFromLoad(LoadEntity load) {
		
		final LoadingpointEntity lp = load.getLoadingpoint();
		final ShippingpointEntity sp;
		
		if (lp != null) {
			sp = lp.getShippingpoint();
		}
		else {
			final Collection<OrderItemEntity> orderItems = orderService.getOrderItems(load);
			if (orderItems == null || orderItems.isEmpty()) {
				// this can only happen in case of pre-loading where no loading point is set
				return null;
			}
			OrderItemEntity firstOrderItem = orderItems.iterator().next();
			sp = firstOrderItem.getOrder().getShippingpoint();
		}
		
		return sp;
	}
	
	private String getVirtualOrRealSloc(ShippingpointEntity sp, SapMaterialEntity material) {
		
		final String vSloc = GoodsMovementUtils.getVirtualStorageLocation(sp.getPlant(), material);
		if (StringUtils.isEmpty(vSloc)) {
			final String rSloc = getRealStorageLocation(sp, material);
			if (StringUtils.isEmpty(rSloc)) {
				final String msg = String.format("Neither real nor virtual storage location " +
						"is defined for shipping point (%s, %s). [material: %s]",
						sp.getSapShippingpointNo(), sp.getShippingpointName(),
						material.getSapMaterialNo());
				LOG.warn(msg);
				return null;
			}
			else {
				return rSloc;
			}
		}
		else {
			return vSloc;
		}
	}
	
	private String getRealStorageLocation(ShippingpointEntity sp, SapMaterialEntity material) {
		
		final MatShippingpointEntity matSP = orderService.fetchMatShippingpoint(material.getSapMaterialId(),
				sp.getShippingpointId());
		
		final String sloc = GoodsMovementUtils.getStorageLocation(sp, matSP);
		
		return sloc;
	}
	
	public String getStorageLocationForSapDocuments(OrderItemEntity orderItem, ShippingpointEntity sp) {
		
		final String sloc;
		final LoadEntity load = orderItem.getLoad();
		List<OrderItemEntity> orderItemList = orderService.getOrderItems(load);
				
		final boolean canCreateGM = GoodsMovementUtils.isGMRequiredForLoadAndOrderItems(load, orderItemList);
		
		if (canCreateGM) {
			sloc = getVirtualOrRealSloc(sp, load.getSapMaterial());
		}
		else {
			sloc = getRealStorageLocation(sp, load.getSapMaterial());
		}
		
		return sloc;
	}
}


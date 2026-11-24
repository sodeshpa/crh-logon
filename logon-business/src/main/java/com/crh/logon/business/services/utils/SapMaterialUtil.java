/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.*;

import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialSareaEntity;
import com.crh.logon.persistence.entities.views.SapMatSareaContractEntity;

/**
 * @author AdNovum Informatik AG
 */
public class SapMaterialUtil {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SapMaterialUtil.class);

	/**
	 * Convenience method. SAP material does not depend strictly on SareaEntity.ATTR_SAREA_ID, because
	 * the Division will be only known when the material is used, eg. based on Contact information.
	 * Therefore SapMaterialSareaEntity no longer has the attribute SAREA_ID, now replaced by SORG_CD and
	 * DISTRIBUTION_CHANNEL.
	 * 
	 * @param filter filter to add the restriction on
	 * @param sarea a Sales Area the restriction is based on
	 */
	public static void addSareaRestriction(Filter<SapMaterialSareaEntity> filter, SareaEntity sarea) {
		// [[
		Object[][] params = {{"filter", filter},
							{"sarea", sarea}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SapMaterialUtil.class, "addSapMaterialSareaRestriction", params);
		}
		// ]]
		try {
			filter.addExpression(Restrictions.eq(SapMaterialSareaEntity.ATTR_SORG, sarea.getSorg()));
			filter.addExpression(Restrictions.eq(SapMaterialSareaEntity.ATTR_DISTRIBUTION_CHANNEL,
					sarea.getDistributionChannel()));
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
	 * This is the "IN" version of {@link #addSareaRestriction(Filter, SareaEntity)}, meaning that
	 * at least one of the given SORG_CD + DISTRIBUTION_CHANNEL pairs should match the SAP material.
	 * 
	 * @param filter filter to add the restriction on
	 * @param sareaList a list of Sales Areas the restriction is based on
	 */
	public static void addInSareaListRestriction(Filter<SapMaterialSareaEntity> filter,
			List<SareaEntity> sareaList) {
		// [[
		Object[][] params = {{"filter", filter},
							{"sareaList", sareaList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SapMaterialUtil.class, "addInSareaListRestriction", params);
		}
		// ]]
		try {
			List<Expression> sareaExpressions = new ArrayList<Expression>(sareaList.size());
			
			for (SareaEntity sarea: sareaList) {
				List<Expression> sareaExpressionParts = new ArrayList<Expression>(2);
			
				sareaExpressionParts.add(Restrictions.eq(SapMaterialSareaEntity.ATTR_SORG, sarea.getSorg()));
				sareaExpressionParts.add(Restrictions.eq(SapMaterialSareaEntity.ATTR_DISTRIBUTION_CHANNEL,
						sarea.getDistributionChannel()));
				
				Expression sareaExpression = Restrictions.and(sareaExpressionParts);
				sareaExpressions.add(sareaExpression);
			}
			
			filter.addExpression(Restrictions.or(sareaExpressions));
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
	 * Creates a new sap contract item entity with planning material, 
	 * and links it to the given sap contract.
	 * 
	 * @param sapContractEntity
	 * @param planningMaterial
	 * @return sap contract item entity
	 */
	public static SapContractItemEntity createNewPlanningMaterialContractItem(SapContractEntity sapContractEntity, 
			SapMaterialEntity planningMaterial) {
		// [[
		SapContractItemEntity result = null;
		Object[][] params = {{"sapContractEntity", sapContractEntity},
							{"planningMaterial", planningMaterial}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SapMaterialUtil.class, "createNewPlanningMaterialContractItem", params);
		}
		// ]]
		try {
			Long contractItemNo = null;
			if (Constant.SAP_MATERIAL_NO_AGG_PLANNING.equals(planningMaterial.getSapMaterialNo())) {
				contractItemNo = Constant.PLANNING_AGGREGATE_CONTRACT_ITEM_NUMBER;
			}
			else if (Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING.equals(planningMaterial.getSapMaterialNo())) {
				contractItemNo = Constant.PLANNING_LANDFILL_CONTRACT_ITEM_NUMBER;
			}
			SapContractItemEntity sapContractItemEntity = new SapContractItemEntity();
			sapContractItemEntity.setSapContract(sapContractEntity);
			sapContractItemEntity.setContractItemNo(contractItemNo);
			sapContractItemEntity.setSapMaterial(planningMaterial);
			sapContractItemEntity.setTransportCostIncludedFlag(false);
			sapContractItemEntity.setIsApGroupLeader(false);
			
			return result = sapContractItemEntity;
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
	 * Searches for a sap contract item belonging to the given sap contract, which 
	 * refers to the planning material.
	 * There must be either 0 or 1 such contract item.
	 * Returns null, if no such contract.
	 * 
	 * @param sapContractEntity
	 * @return sap contract entity
	 */
	public static SapContractItemEntity getPlanningMaterialContractItem(String planningMaterialNo, SapContractEntity sapContractEntity) {
		// [[
		SapContractItemEntity result = null;
		Object[][] params = {{"planningMaterialNo", planningMaterialNo},
							{"sapContractEntity", sapContractEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SapMaterialUtil.class, "getPlanningMaterialContractItem", params);
		}
		// ]]
		try {
			List<SapContractItemEntity> sapContractItemList = sapContractEntity.getSapContractItems();
			for (SapContractItemEntity sapContractItemEntity : sapContractItemList) {
				if (sapContractItemEntity.getSapMaterial().getSapMaterialNo().equals(planningMaterialNo)) {
					return result = sapContractItemEntity;
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

	/**
	 * Performs a simple linear search on sapMaterialCollection to find the first
	 * SapMaterialEntity with the given sapMaterialId.
	 *
	 * This method is a replacement for the former ListUtils.findListItem... methods.
	 * Those are quite slow because those uses reflection technique.
	 *
	 * @param sapMaterialCollection in which the SapMaterialEntity should be searched.
	 * @param sapMaterialId this ID is used when searching for the SapMaterialEntity.
	 * @return the SapMaterialEntity if found, null otherwise.
	 */
	public static Long findSapMaterialInCollection(
			final Collection<Long> sapMaterialCollection, final Long sapMaterialId) {

		if (sapMaterialCollection == null || sapMaterialId == null) {
			throw new IllegalArgumentException(
					"The sapMaterialCollection and sapMaterialId parameters should not be null!");
		}

		Long result = null;

		for (Long sapMaterialEntity : sapMaterialCollection) {
			if (sapMaterialEntity.equals(sapMaterialId)) {
				result = sapMaterialEntity;
				break;
			}
		}

		return result;
	}

	/**
	 * Prunes the given input map <em>contractItemsPerMaterial</em> of quantity restricted materials
	 * which no longer have any available or open quantity.
	 * <p/>
	 * NOTE: This method will directly modify the input Map <em>contractItemsPerMaterial</em>
	 * <p/>
	 * The removal of materials from the given map is a two step process. In the first step, we check the
	 * list of contract items for ever SapMaterialEntity instance. For each contract item, if we find a
	 * corresponding ContractItemQuantity instance, then we evaluate the remaining or open quantity. On
	 * those contract items who have no corresponding ContractItemQuantity or still have open quantity
	 * are taken. In the second step, we iterate through the new map and remove all of those materials
	 * who have no contract items.
	 * <p/>
	 * Note that we could perhaps improve on the performance a bit by requiring the caller to send
	 * a set of SapMaterialEntity Ids to check. Of course this does depend on the number of entries
	 * in the <em>contractItemsPerMaterial</em> so for now, we do not bother with this premature
	 * optimization. It is just noted here for completeness.
	 *
	 * @param contractItemsPerMaterial a Map which maps SapMaterialEntity instances to a List of
	 *                                 SapContractItemEntity instances. These contract items instances all
	 *                                 reference the SapMaterialEntity in question. Note that this map
	 *                                 is directly modified by this method!
	 *
	 * @param quantitiesByContractItemIds a Map which maps SapContractItemEntity Ids to ContractItemQuantity
	 *                                    instances. Note that not all SapContractItemEntity instances found
	 *                                    in <em>contractItemsPerMaterial</em> are guaranteed to be in the
	 *                                    list. However, if they are in the list, then we take the open
	 *                                    quantity into consideration.
	 */
	public static void pruneQuantityRestrictedMaterials(
			Map<Long, List<SapMatSareaContractEntity>> contractItemsPerMaterial,
			Map<Long, ContractItemQuantity> quantitiesByContractItemIds) {

		//--- Pass 1: Remove ContractItems without any open quantity
		for (List<SapMatSareaContractEntity> contractItems: contractItemsPerMaterial.values()) {
			Iterator<SapMatSareaContractEntity> it = contractItems.iterator();
			while (it.hasNext()) {
				SapMatSareaContractEntity contractItem = it.next();
				ContractItemQuantity qty =
						quantitiesByContractItemIds.get(contractItem.getSapContractItemId());

				BigDecimal openQty = (qty != null) ? qty.getOpenQuantity() : null;
				if (openQty != null && BigDecimal.ZERO.compareTo(openQty) == 0) {
					it.remove();
				}
			}
		}

		//--- Pass 2: Remove all materials without any contract items
		Iterator<Map.Entry<Long, List<SapMatSareaContractEntity>>> it =
				contractItemsPerMaterial.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Long, List<SapMatSareaContractEntity>> entry = it.next();
			if (entry.getValue().isEmpty()) {
				it.remove();
			}
		}
	}
}

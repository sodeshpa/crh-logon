/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.dispatchers.QuantityRestrictionServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.QuantityRestrictionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.ContractItemQuantity;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.QuantityRestrictionCalculation;
import com.crh.logon.business.services.utils.QuantityRestrictionConversionError;
import com.crh.logon.business.services.utils.QuantityRestrictionError;
import com.crh.logon.business.stores.sap.ContractItemStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomDimensionBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.ContractItemQtyEntity;
import com.crh.logon.persistence.entities.views.ContractItemQuantityEntity;
import com.crh.logon.persistence.entities.views.OrderItemQuantityEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Service methods for truck revenue planning report/export.
 *
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class QuantityRestrictionServiceBean extends QuantityRestrictionServiceDispatcher implements QuantityRestrictionServiceLocalInterface {

	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(QuantityRestrictionServiceBean.class);

	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private ContractItemStoreInterface sapContractItemStore;
	
	/**
	 * !!!NOT FOR PUBLIC USE!!!
	 * <p/>
	 * Helper interface used internally to make the mocking and unit-testing of the Service
	 * implementation a bit easier. We don't really care about the DB-level access for
	 * unit-testing the business logic and we will assume that the Nevis JPA code is already
	 * properly tested.
	 * <p/>
	 * We could perhaps think about making the getDaoProvider() in the generated code at least
	 * protected so we can mock it in the unit tests.
	 */
	interface FindOperation<T> {
		public T execute();
	}

	/**
	 * Validates the list of order items and ensures that enough quantity is still available to fullfill
	 * the requests.
	 *
	 * @return List of all validation errors which occurred or an empty List if validation was
	 * 	successful. This method never returns <code>null</code>
	 */
	public List<QuantityRestrictionError> validateQuantities(OrderEntity order, Map<Long, QuantityRestrictionCalculation> quantityRestrCalcMap) {
		// [[
		List<QuantityRestrictionError> result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateQuantities", params);
		}
		// ]]
		try {
			result = new ArrayList<QuantityRestrictionError>();

			//--- Prune out non-controlled order items ---//
			final boolean isReturnOrder = LogonUtil.isReturnPalletOnly(order);
			List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
			for (OrderItemEntity orderItem : order.getOrderItems()) {
				SapContractItemEntity sapContractItem = orderItem.getSapContractItem();
				if (sapContractItem == null) {
					continue;
				}
				if (isReturnOrder && !LogonPackageHandlingUtil.isService(orderItem)) {
					continue;
				}
				if (sapContractItem.getIsApGroupLeader()) {
					orderItems.add(orderItem);
				}
			}

			//--- Partition the list of OrderItems by SAP contract item ids ---//
			Map<Long, List<OrderItemEntity>> orderItemsByContractItemId = new HashMap<Long, List<OrderItemEntity>>();
			for (OrderItemEntity orderItem : orderItems) {
				SapContractItemEntity contractItem = orderItem.getSapContractItem();
				if(contractItem != null) {
					Long contractItemId = contractItem.getSapContractItemId();
					List<OrderItemEntity> groupMembers = orderItemsByContractItemId.get(contractItemId);
					if (groupMembers == null) {
						groupMembers = new ArrayList<OrderItemEntity>();
						orderItemsByContractItemId.put(contractItemId, groupMembers);
					}
					groupMembers.add(orderItem);											
				}
			}

			//--- Sum all OrderItem Quantities for each SAP contract item ---//
			Map<Long, BigDecimal> orderedQuantityByContractItemId = new HashMap<Long, BigDecimal>();
			for (Long contractItemId : orderItemsByContractItemId.keySet()) {
				BigDecimal totalQty = BigDecimal.ZERO;

				for (OrderItemEntity orderItem : orderItemsByContractItemId.get(contractItemId)) {
					SapContractItemEntity contractItem = orderItem.getSapContractItem();

					BigDecimal orderedQty = convertToTargetQuantityUom(
							orderItem.getOrderedQuantity(),
							orderItem.getOrderedUom(),
							contractItem,
							orderItem.getOrderedUomNumerator(),
							orderItem.getOrderedUomDenominator());

					if (orderedQty == null) {
						result.add(new QuantityRestrictionConversionError(
								orderItem,
								contractItem.getTargetQuantityUom().getUomCd()));

						// if we cannot convert to the target quantity, then we have no chance of
						// computing and validating the total order quantity for this contract item
						totalQty = null;
						break;
					}

					BigDecimal orderItemSentQty = BigDecimal.ZERO;

					if (Constant.ORDER_STATUS_FINISHED == orderItem.getOrder().getOrderStatus().getOrderStatusCd()) {
						// for FINISHED orders, we need to take into account the cached value
						// TOTAL_SENT_QUANTITY on the contract item. The reason we need to subtract
						// both the DELIVERED_QTY and RETURNED_QTY from the running total is that
						// when an order item is adjusted (updated), its DELIVERED_QTY is immediately
						// reduced by RETURNED_QTY. However, the value which was added to TOTAL_SENT_QUANTITY
						// in OrderServiceBean,_setToFinished() was the original DELIVERED_QTY.
						//
						// Ex. At time T an order O is set to FINISHED. Delivered=99, TOTAL_SENT_QUANTITY = 99
						//	   At time T+1, order O is updated: Returned=50, Delivered=49, TOTAL_SENT_QUANTITY=99
						//
						// NOTE: we cannot simply subtract TOTAL_SENT_QUANTITY from the running total as multiple
						// Advance Payment order items can point to the same contract item.

						BigDecimal deliveredQty = convertToTargetQuantityUom(
								orderItem.getDeliveredQuantity(),
								orderItem.getDeliveredUom(),
								contractItem,
								null, null);

						if (deliveredQty == null) {
							result.add(new QuantityRestrictionConversionError(
									orderItem,
									contractItem.getTargetQuantityUom().getUomCd()));

							// if we cannot convert to the target quantity, then we have no chance of
							// computing and validating the total order quantity for this contract item
							totalQty = null;
							break;
						}

						if (orderItem.getReturnedQuantity() != null 
								&& orderItem.getReturnedUom() != null) {
							BigDecimal returnedQty = convertToTargetQuantityUom(
									orderItem.getReturnedQuantity(),
									orderItem.getReturnedUom(),
									contractItem,
									null, null);
	
							if (returnedQty == null) {
								result.add(new QuantityRestrictionConversionError(
										orderItem,
										contractItem.getTargetQuantityUom().getUomCd()));
	
								// if we cannot convert to the target quantity, then we have no chance of
								// computing and validating the total order quantity for this contract item
								totalQty = null;
								break;
							}
	
							orderItemSentQty = deliveredQty.add(returnedQty);
						}
						else {
							orderItemSentQty = deliveredQty;
						}
					}

					totalQty = totalQty.add(orderedQty).subtract(orderItemSentQty);
				}

				if (totalQty != null) {
					orderedQuantityByContractItemId.put(contractItemId, totalQty);
				}
			}

			//--- Create list of SAP contract items for which we have a total ordered quantity ---//
			List<SapContractItemEntity> contractItems = new ArrayList<SapContractItemEntity>();
			for (OrderItemEntity orderItem : orderItems) {
				SapContractItemEntity contractItem = orderItem.getSapContractItem();
				if(contractItem != null) {
					Long contractItemId = contractItem.getSapContractItemId();
					if (orderedQuantityByContractItemId.containsKey(contractItemId)) {
						contractItems.add(contractItem);
					}					
				}
			}

			//--- Collect all order items which are being updated ---//
			List<Long> excludedOrderItemIds = new ArrayList<Long>();
			for (OrderItemEntity orderItem : orderItems) {
				if (orderItem.getOrderItemId() != null) {
					// We are updating an already existing order => do not include the quantities
					// of the items of this order in the open quantity!
					excludedOrderItemIds.add(orderItem.getOrderItemId());
				}
			}

			Map<Long, ContractItemQuantity> contractItemQtys =
					computeQuantitiesForQuantityControlledContractItems(
							contractItems,
							excludedOrderItemIds);

			//--- Validate that the ordered quantities are available ---//
			for (ContractItemQuantity itemQty: contractItemQtys.values()) {
				if (itemQty.isPlanningMaterialQuantity()) {
					// just process for now - it will be checked again when an actual
					// material is selected
					orderItemsByContractItemId.remove(itemQty.getSapContractItemId());
					continue;
				}

				BigDecimal totalQty = orderedQuantityByContractItemId.get(itemQty.getSapContractItemId());

				if (itemQty.getOpenQuantity().compareTo(totalQty) < 0) {
					List<OrderItemEntity> orderItemsForContractItem = orderItemsByContractItemId.get(itemQty.getSapContractItemId());
					if(orderItemsForContractItem != null) {
						for (OrderItemEntity orderItem : orderItemsForContractItem) {
							SapContractItemEntity contractItem = orderItem.getSapContractItem();
							String uomCd = null;
							if(contractItem != null && contractItem.getTargetQuantityUom() != null) {
								uomCd = contractItem.getTargetQuantityUom().getUomCd();
							}
							result.add(new QuantityRestrictionError(orderItem, itemQty.getOpenQuantity(), uomCd, totalQty));
						}						
					}
				}

				if(quantityRestrCalcMap != null) {
					QuantityRestrictionCalculation quantityRestrictionCalculation = quantityRestrCalcMap.get(itemQty.getSapContractItemId());
					if(quantityRestrictionCalculation != null) {			
						BigDecimal openQuantity = quantityRestrictionCalculation.getOpenQuantity();
						if(openQuantity != null) {
							if (openQuantity.compareTo(totalQty) < 0) {
								List<OrderItemEntity> orderItemsForContractItem = orderItemsByContractItemId.get(itemQty.getSapContractItemId());
								if(orderItemsForContractItem != null) {
									for (OrderItemEntity orderItem : orderItemsForContractItem) {
										SapContractItemEntity contractItem = orderItem.getSapContractItem();
										String uomCd = null;
										if(contractItem != null && contractItem.getTargetQuantityUom() != null) {
											uomCd = contractItem.getTargetQuantityUom().getUomCd();
										}
										result.add(new QuantityRestrictionError(orderItem, openQuantity, uomCd, totalQty));
									}									
								}
							} else {
								BigDecimal sumNonDeliveries = quantityRestrictionCalculation.getSumNonDeliveries();
								BigDecimal newSumNonDeliveries = sumNonDeliveries == null ? BigDecimal.ZERO : sumNonDeliveries.add(totalQty);
								quantityRestrictionCalculation.setSumNonDeliveries(newSumNonDeliveries);
								
								BigDecimal newOpenQuantity = openQuantity.subtract(totalQty);
								if(newOpenQuantity.compareTo(BigDecimal.ZERO) < 0) {
									newOpenQuantity = BigDecimal.ZERO;
								}
								quantityRestrictionCalculation.setOpenQuantity(newOpenQuantity);													
							}													
						}
					}
				}
				
				
				// processed: remove from main list
				orderItemsByContractItemId.remove(itemQty.getSapContractItemId());
			}

			//--- Sanity check: Flag all unprocessed order items as errors (shouldn't happen) ---//
			for (List<OrderItemEntity> items : orderItemsByContractItemId.values()) {
				for (OrderItemEntity item : items) {
					LOG.error("Sanity Check Failed: No ContractItemQuantity found for [contract-item-id: "
									  + item.getSapContractItem().getSapContractItemId()
									  + ", order-item-id: "
									  + item.getOrderItemId()
									  + "]");

					SapContractItemEntity contractItem = item.getSapContractItem();
					result.add(new QuantityRestrictionError(
							item,
							BigDecimal.ZERO,
							contractItem.getTargetQuantityUom().getUomCd(),
							item.getOrderedQuantity()));
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
	 * Does the open quantity calculation for a single contract item.
	 *
	 * @param sapContractItem
	 * @param excludedOrderItemIds in case we are updating an existing order, do not include
	 *                             these orderItemIds in the calculation. (Note: if we start supporting mass updates of
	 *                             SDOs belonging to customer orders, this needs redesign.)
	 * @return the result of the calculation, or null if there was no quantity restriction
	 */
	public QuantityRestrictionCalculation calculateOpenQuantityForContractItem(
			SapContractItemEntity sapContractItem, List excludedOrderItemIds) {
		return calculateOpenQuantityForContractItem(sapContractItem, excludedOrderItemIds, false);
	}

	public QuantityRestrictionCalculation calculateOpenQuantityForContractItem(SapContractItemEntity sapContractItem, List excludedOrderItemIds, boolean collectLoadQuantities) {
		// [[
		QuantityRestrictionCalculation result = null;
		Object[][] params = {{"sapContractItem", sapContractItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateOpenQuantityForContractItems", params);
		}
		// ]]
		try {
			List<SapContractItemEntity> sapContractItems = new ArrayList<SapContractItemEntity>(1);
			sapContractItems.add(sapContractItem);

			Map<Long, QuantityRestrictionCalculation> calculationMap =
					calculateOpenQuantityForContractItems(sapContractItems, excludedOrderItemIds, collectLoadQuantities);

			Long sapContractItemId = sapContractItem.getSapContractItemId();
			return result = calculationMap.get(sapContractItemId);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public QuantityRestrictionCalculation calculateOpenQuantityForQuantityRestrictedContractItem(SapContractItemEntity contractItem) {
		// [[
		QuantityRestrictionCalculation result = null;
		Object[][] params = {{"sapContractItem", contractItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateOpenQuantityForQuantityRestrictedContractItem", params);
		}
		// ]]
		try {
			
			SapContractEntity sapContract = contractItem.getSapContract();
			boolean quantityControlledContract = LogonUtil.isQuantityControlledContract(sapContract);
			Long sapContractItemId = contractItem.getSapContractItemId();
			if (!quantityControlledContract) {
				return result;
			}
			else if (contractItem.getTargetQuantity() == null) {
				LOG.error("Inconsistency found in contract data: targetQuantity is null on " + "contract item (id = " + sapContractItemId + "), but contract is quantity controlled.");
				return result;
			}
			else if (contractItem.getTargetQuantityUom() == null) {
				LOG.error("Inconsistency found in contract data: targetQuantityUom is null " + "on contract item (id = " + sapContractItemId + "), but contract is quantity controlled.");
				return result;
			}

			ReadOnlyDao<ContractItemQtyEntity> sciqeDao = getDaoProvider().getContractItemQtyEntityDao();
			ContractItemQtyEntity sentContractItemQuantity = sciqeDao.findById(sapContractItemId);
			
			QuantityRestrictionCalculation calculation = new QuantityRestrictionCalculation();

			if(sentContractItemQuantity != null) {
				BigDecimal targetQuantity = sentContractItemQuantity.getTargetQuantity();
				BigDecimal remainingQuantity = sentContractItemQuantity.getRemainingQuantity();
				
				if(targetQuantity != null && remainingQuantity != null && targetQuantity.compareTo(remainingQuantity) >= 0) {
					BigDecimal deliveredQuantity = targetQuantity.subtract(remainingQuantity);

					UomBsEntity uom = contractItem.getTargetQuantityUom();
					
					deliveredQuantity = convertToTargetQuantityUom(deliveredQuantity, uom, contractItem, null, null);
					calculation.setSumDeliveries(deliveredQuantity);
					
					remainingQuantity = convertToTargetQuantityUom(remainingQuantity, uom, contractItem, null, null);
					calculation.setSumNonDeliveries(remainingQuantity);
					
					calculation.setOpenQuantity(remainingQuantity);				
				}							
			}


			return result = calculation;
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
	 * Not only calculates the open quantity, but also other details of the calculation that is
	 * required by contract visualization (but those are relatively cheap compared to the whole
	 * process, so they are all done in one run).
	 *
	 * @param sapContractItems
	 * @param excludedOrderItemIds in case we are updating an existing order, do not include these
	 *                             orderItemIds in the calculation. (Note: if we start supporting mass updates of SDOs
	 *                             belonging to customer orders, this needs redesign.)
	 * @return the result of the calculation collected into a map for those contract items for
	 *         which there were quantity restrictions and the calculation did not fail due to
	 *         conversion problems
	 */
	public Map<Long, QuantityRestrictionCalculation> calculateOpenQuantityForContractItems(List<SapContractItemEntity> sapContractItems,
													 List<Long> excludedOrderItemIds, boolean collectLoadQuantities) {
		// [[
		Map<Long, QuantityRestrictionCalculation> result = null;
		Object[][] params = {{"sapContractItems", sapContractItems},
							 {"excludedOrderItemIds", excludedOrderItemIds},
							 {"collectLoadQuantities", collectLoadQuantities}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateOpenQuantityForContractItems", params);
		}
		// ]]
		try {
			Map<Long, SapContractItemEntity> contractItemsById = new HashMap<Long, SapContractItemEntity>();
			for (SapContractItemEntity contractItem : sapContractItems) {
				SapContractEntity sapContract = contractItem.getSapContract();
				boolean quantityControlledContract = LogonUtil.isQuantityControlledContract(sapContract);
				Long sapContractItemId = contractItem.getSapContractItemId();
				if (!quantityControlledContract) {
					continue;
				}
				else if (contractItem.getTargetQuantity() == null) {
					LOG.error("Inconsistency found in contract data: targetQuantity is null on "
							+ "contract item (id = " + sapContractItemId
							+ "), but contract is quantity controlled.");
					continue;
				}
				else if (contractItem.getTargetQuantityUom() == null) {
					LOG.error("Inconsistency found in contract data: targetQuantityUom is null "
							+ "on contract item (id = " + sapContractItemId
							+ "), but contract is quantity controlled.");
					continue;
				}
				contractItemsById.put(sapContractItemId, contractItem);
			}

			Map<Long, QuantityRestrictionCalculation> calculationMap =
					new HashMap<Long, QuantityRestrictionCalculation>();

			if (!contractItemsById.isEmpty()) {
				// Fetch information about quantities of delivered/non-delivered orders
				ReadOnlyDao<OrderItemQuantityEntity> oiqeDao = getDaoProvider().getOrderItemQuantityEntityDao();
				Filter<OrderItemQuantityEntity> filter = oiqeDao.createFilter();

				filter.addExpression(Restrictions.in(OrderItemQuantityEntity.ATTR_SAP_CONTRACT_ITEM_ID, contractItemsById.keySet()));

				if ((excludedOrderItemIds != null) && (!excludedOrderItemIds.isEmpty())) {
					filter.addExpression(Restrictions.not(
							Restrictions.in(OrderItemQuantityEntity.ATTR_ORDER_ITEM_ID,
											excludedOrderItemIds)
					));
				}

				List<OrderItemQuantityEntity> orderItemQuantityList = oiqeDao.find(filter);

				// Create a mockup UomBsEntity so we can reuse UomConverterService.
				UomBsEntity uom = new UomBsEntity();
				UomDimensionBsEntity uomDimension = new UomDimensionBsEntity();
				uom.setUomDimension(uomDimension);

				// Calculate open quantity per contract item
				for (Long sapContractItemId : contractItemsById.keySet()) {					
					QuantityRestrictionCalculation calculation = new QuantityRestrictionCalculation();
					calculationMap.put(sapContractItemId, calculation);
					SapContractItemEntity sapContractItem = contractItemsById.get(sapContractItemId);
					if (sapContractItem != null) {
						BigDecimal openQuantity = sapContractItem.getTargetQuantity();
						calculation.setOpenQuantity(openQuantity);
					}					
				}

				for (OrderItemQuantityEntity orderItemQuantity : orderItemQuantityList) {
					Long sapContractItemId = orderItemQuantity.getSapContractItemId();
					if(sapContractItemId == null) {
						continue;
					}
					
					QuantityRestrictionCalculation calculation = calculationMap.get(sapContractItemId);
					if (calculation == null) {
						continue;
					}
					
					SapContractItemEntity sapContractItem = contractItemsById.get(sapContractItemId);
					if (sapContractItem == null) {
						continue;
					}
					
					BigDecimal openQuantity = calculation.getOpenQuantity();
					if (openQuantity == null) {
						openQuantity = sapContractItem.getTargetQuantity();
					}
					
					BigDecimal quantityToSubtract;
					BigDecimal deliveredQuantity = orderItemQuantity.getDeliveredQuantity();
					BigDecimal loadedQuantity = orderItemQuantity.getLoadedQuantity();
	
					if (deliveredQuantity != null) {
						quantityToSubtract = deliveredQuantity;
	
						uom.setUomCd(orderItemQuantity.getDeliveredUomCd());
						uom.setFuzzyQuantityFlag(orderItemQuantity.getDFuzzyQuantityFlag());
						uom.setNumeratorForConversion(
								orderItemQuantity.getDNumeratorForConversion());
						uom.setDenominatorForConversion(
								orderItemQuantity.getDDenominatorForConversion());
						uomDimension.setUomDimensionCd(orderItemQuantity.getDUomDimensionCd());
	
						quantityToSubtract = convertToTargetQuantityUom(quantityToSubtract,
																		uom, sapContractItem, null, null);
					} else if (collectLoadQuantities && loadedQuantity != null) {
						quantityToSubtract = loadedQuantity;
	
						uom.setUomCd(orderItemQuantity.getLoadedUomCd());
						uom.setFuzzyQuantityFlag(orderItemQuantity.getDFuzzyQuantityFlag());
						uom.setNumeratorForConversion(
								orderItemQuantity.getDNumeratorForConversion());
						uom.setDenominatorForConversion(
								orderItemQuantity.getDDenominatorForConversion());
						uomDimension.setUomDimensionCd(orderItemQuantity.getDUomDimensionCd());
	
						quantityToSubtract = convertToTargetQuantityUom(quantityToSubtract,
																		uom, sapContractItem, null, null);							
					} else {
						BigDecimal orderedQuantity = orderItemQuantity.getOrderedQuantity();
						quantityToSubtract = orderedQuantity;
	
						uom.setUomCd(orderItemQuantity.getOrderedUomCd());
						uom.setFuzzyQuantityFlag(orderItemQuantity.getOFuzzyQuantityFlag());
						uom.setNumeratorForConversion(
								orderItemQuantity.getONumeratorForConversion());
						uom.setDenominatorForConversion(
								orderItemQuantity.getODenominatorForConversion());
						uomDimension.setUomDimensionCd(orderItemQuantity.getOUomDimensionCd());
	
						Long orderedUomNumerator =
								orderItemQuantity.getO2bNumeratorForConversion();
						Long orderedUomDenominator =
								orderItemQuantity.getO2bDenominatorForConversion();
	
						quantityToSubtract = convertToTargetQuantityUom(quantityToSubtract,
																		uom, sapContractItem, orderedUomNumerator,
																		orderedUomDenominator);
					}
	
					Long orderNumber = orderItemQuantity.getOrderNumber();
					Long orderItemNo = orderItemQuantity.getOrderItemNo();
	
					if (quantityToSubtract != null) {
						List<String> deliveries = calculation.getDeliveries();
						BigDecimal sumDeliveries = calculation.getSumDeliveries();
	
						List<String> nonDeliveries = calculation.getNonDeliveries();
						BigDecimal sumNonDeliveries = calculation.getSumNonDeliveries();
	
						BigDecimal sumLoaded = calculation.getSumLoaded();
						
						if (deliveredQuantity != null) {
							sumDeliveries = sumDeliveries.add(quantityToSubtract);
							calculation.setSumDeliveries(sumDeliveries);
	
							deliveries.add(orderNumber + "/" + orderItemNo);
						} else if (collectLoadQuantities && loadedQuantity != null) {
							sumLoaded = sumLoaded.add(quantityToSubtract);
							calculation.setSumLoaded(sumLoaded);
						} else {
							sumNonDeliveries = sumNonDeliveries.add(quantityToSubtract);
							calculation.setSumNonDeliveries(sumNonDeliveries);
	
							nonDeliveries.add(orderNumber + "/" + orderItemNo);
						}
					}
					else {
						List<String> conversionFailures = calculation.getConversionFailures();
						conversionFailures.add(orderNumber + "/" + orderItemNo);
						quantityToSubtract = BigDecimal.ZERO;
					}
	
					openQuantity = openQuantity.subtract(quantityToSubtract);

					calculation.setOpenQuantity(openQuantity);
				}
			}

			return result = calculationMap;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public Map<Long, QuantityRestrictionCalculation> calculateOpenQuantityForContractItems(List<SapContractItemEntity> sapContractItems,List<Long> excludedOrderItemIds) {
		return calculateOpenQuantityForContractItems(sapContractItems, excludedOrderItemIds, false);
	}
	
	/**
	 * See {@link #computeQuantitiesForQuantityControlledContractItems(List, List)}.
	 */
	@AdnTrace
	public Map<Long, ContractItemQuantity> computeQuantitiesForQuantityControlledContractItemIds(
			final List<Long> allContractsItemIds,
			final List<Long> excludedOrderItemIds) {
		
		Authorizer.disableNextAccessRestriction();
		final List<SapContractItemEntity> allContractItems = sapContractItemStore.findSapContractItemsById(allContractsItemIds);
		
		return computeQuantitiesForQuantityControlledContractItems(allContractItems, excludedOrderItemIds);
	}
		
	/**
	 * Computes the set of quantities for the list of quantity-controlled Contract items.
	 * <p/>
	 * Quantity controlled contract items are restricted to the total available or open quantity left
	 * for the contract item. That is, unlike normal contract items where the target quantity serves
	 * merely as a hint to the amount available, a quantity-controlled contract item cannot exceed
	 * its defined target quantity.
	 * <p/>
	 * This method will compute the open and used quantities for each SAP contract item given.
	 *
	 * @param contractsItems       a List of <code>SapContractItemEntity</code> instances
	 *
	 * @param excludedOrderItemIds a possible null List of OrderItemEntity Ids to exclude when computing
	 *                             the set of quantities for each contract item. This is necessary in cases
	 *                             where we are updating an OrderItemEntity and need to compute the open
	 *                             quantity for the corresponding contract item.
	 *
	 * @return a Map of ContractItemQuantity instances keyed by SAP contract item id
	 *
	 * @specification LRQ 303.0 Advance Payment Solution, Chapters. 4.3.1
	 */
	public Map<Long, ContractItemQuantity> computeQuantitiesForQuantityControlledContractItems(
			final List<SapContractItemEntity> allContractsItems,
			final List<Long> excludedOrderItemIds) {
		// [[
		Map<Long, ContractItemQuantity> result = null;
		Object[][] params = {{"allContractsItems", allContractsItems},
							 {"excludedOrderItemIds", excludedOrderItemIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "computeQuantitiesForQuantityControlledContractItems", params);
		}
		// ]]
		try {
			//--- Prune out non-quantity controlled items and sanity check some of the data ---//
			List<SapContractItemEntity> contractItems =
					pruneNonQuantityControlledContractItems(allContractsItems);

			if (contractItems.isEmpty()) {
				LOG.debug("No Group Leader contract items to process");
				return result = Collections.EMPTY_MAP;
			}

			//--- Collect all ids in order to perform 1 query ---//
			final Map<Long, SapContractItemEntity> contractItemsById = new HashMap<Long, SapContractItemEntity>();
			for (SapContractItemEntity item : contractItems) {
				contractItemsById.put(item.getSapContractItemId(), item);
			}
			
			
			//--- Make unit testing a bit simpler by running the DAO operations in a separate method ---//
			FindOperation<List<ContractItemQuantityEntity>> findOp = new FindOperation() {
				public List<ContractItemQuantityEntity> execute() {
					final ReadOnlyDao<ContractItemQuantityEntity> dao =
							getDaoProvider().getContractItemQuantityEntityDao();

					Filter<ContractItemQuantityEntity> filter = dao.createFilter();
					filter.addExpression(Restrictions.in(
							ContractItemQuantityEntity.ATTR_SAP_CONTRACT_ITEM_ID, contractItemsById.keySet()));
					List<Long> notValidatedDeliveryTypes = Arrays.asList(Constant.DELIVERY_TYPE_RETURN_PICKUP, Constant.DELIVERY_TYPE_RETURN_DELIVERED);
					
					filter.addExpression(Restrictions.or(
							Restrictions.not(Restrictions.in(ContractItemQuantityEntity.ATTR_DELIVERY_TYPE_CD, notValidatedDeliveryTypes )),
							Restrictions.eq(ContractItemQuantityEntity.ATTR_PACKAGE_TYPE_CD, Constant.PACKAGE_TYPE_SERVICE)));
					
					if (excludedOrderItemIds != null && !excludedOrderItemIds.isEmpty()) {
						filter.addExpression(Restrictions.not(
								Restrictions.in(ContractItemQuantityEntity.ATTR_ORDER_ITEM_ID,
												excludedOrderItemIds)));
					}

					return dao.find(filter);
				}
			};

			List<ContractItemQuantityEntity> items = executeFindOperation(findOp);

			//--- Partition quantity entities by contract item id (makes lookups a bit simpler) --//
			Map<Long, List<ContractItemQuantityEntity>> groups =
					new HashMap<Long, List<ContractItemQuantityEntity>>();

			for (ContractItemQuantityEntity entity : items) {
				List<ContractItemQuantityEntity> groupMembers = groups.get(entity.getSapContractItemId());
				if (groupMembers == null) {
					groupMembers = new ArrayList<ContractItemQuantityEntity>();
					groups.put(entity.getSapContractItemId(), groupMembers);
				}
				groupMembers.add(entity);
			}

			//--- Compute the quantities for each contract item ---//
			Map<Long, ContractItemQuantity> totalQuantities = new HashMap<Long, ContractItemQuantity>();
			for (SapContractItemEntity contractItem: contractItems) {
				List<ContractItemQuantityEntity> members = groups.get(contractItem.getSapContractItemId());

				BigDecimal openQty = computeOpenQuantityForContractItem(
						contractItem, members);

				// Planning materials do not have a UoM set
				ContractItemQuantity itemQty = new ContractItemQuantity(
						contractItem.getSapContractItemId(),
						contractItem.getTargetQuantity(),
						openQty,
						contractItem.getTargetQuantityUom() != null
								? contractItem.getTargetQuantityUom().getUomCd()
								: null);

				totalQuantities.put(contractItem.getSapContractItemId(), itemQty);
			}

			return result = totalQuantities;
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
	 * Prune out all non-quantity controlled contract items and perform some
	 * simple sanity checking to make debugging of error cases easier rather
	 * than just simply throwing a NPE or something similar.
	 */
	private List<SapContractItemEntity> pruneNonQuantityControlledContractItems(
			List<SapContractItemEntity> origContractsItems) {
		// [[
		List<SapContractItemEntity> result = null;
		Object[][] params = {{"origContractsItems", origContractsItems}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "pruneNonQuantityControlledContractItems", params);
		}
		// ]]
		try {
			if (origContractsItems == null || origContractsItems.isEmpty()) {
				LOG.debug("No contract items to prune");
				return result = Collections.EMPTY_LIST;
			}

			//--- Prune out non-quantity controlled items and sanity check some of the data ---//
			List<SapContractItemEntity> contractItems =
					new ArrayList<SapContractItemEntity>(origContractsItems);

			Iterator<SapContractItemEntity> contractItemsIter = contractItems.iterator();
			while (contractItemsIter.hasNext()) {
				SapContractItemEntity entity = contractItemsIter.next();
				if (!entity.getIsApGroupLeader()) {
					contractItemsIter.remove();
				}
			}

			return result = contractItems;
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
	 * Helper method used internally to make the mocking and unit-testing of the Service
	 * implementation a bit easier. All of our DAO operations are run through this method so
	 * that we can easily mock it.
	 */
	<T> T executeFindOperation(FindOperation<T> op) {
		return op.execute();
	}

	/**
	 * Compute the open and used quantities for the given <em>contractItem</em> over all
	 * <em>ContractItemQuantityEntity</em> given.
	 *
	 * The algorithm for computing the USED quantities is taken directly from the Advance Payment (LRQ 303.0)
	 * specification, Chapter 5.1. Note that the document talks about summing over all order items related to
	 * a contract item in an Advance Payment (AP) group. But, in actuality, the order items are all associated
	 * with the AP group leader since only this contract item is made available to the application. For this
	 * reason we just sum the used quantities over all ContractItemQuantityEntity instances. Furthermore,
	 * we need to take into consideration the TOTAL_SENT_QUANTITY which represents the total quantity on
	 * FINISHED orders. It is a cached value which is updated when orders are FINISHED to avoid querying
	 * FINISHED orders in the VLGNC_CONTRACT_ITEM_QUANTITY view (performance reasons).
	 *
	 * If we fail to convert any quantity UoM (unit of measure) to the base UoM, then we consider its USED-QTY
	 * to be set to its TARGET-QTY (i.e. we assume 0 OPEN-QTY left for that particular
	 * item).
	 *
	 * @param contractItem the contract item whose quantities we want to compute
	 *
	 * @param contractItemQuantities  a List of ContractItemQuantityEntity objects. These objects contain
	 *                                the data required to compute the ContractItemQuantity instances. Note that
	 *                                FINISHED orders are filtered by the view - FINISHED order quantities are
	 *                                stored on the TOTAL_SEND_QUANTITY field for the contract item
	 *
	 * @return a ContractItemQuantity
	 *
	 * @specification LRQ 303.0 Advance Payment Solution, Chapters. 5.1
	 */
	BigDecimal computeOpenQuantityForContractItem(
			SapContractItemEntity contractItem,
			List<ContractItemQuantityEntity> contractItemQuantities) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"contractItem", contractItem},
							 {"contractItemQuantities", contractItemQuantities}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "computeOpenQuantityForContractItem", params);
		}
		// ]]
		try {
			if (contractItem.getTargetQuantity() == null) {
				// Chapter 5 (Pre-requisite): this has to be set, otherwise we assume 0 quantity
				LOG.error("Inconsistency found in contract data: targetQuantity is null on "
						  + "contract item (id: " + contractItem.getSapContractItemId() + ")");
				return result = BigDecimal.ZERO;
			}

			if (contractItem.getTargetQuantityUom() == null) {
				// Even though this is stated as a pre-requisite in Chapter 5 (Pre-requisite)
				// this is not the case. The problem is that Planning Materials will have
				// neither a target-quantity or target-quantity-uom-cd set

				SapMaterialEntity material = contractItem.getSapMaterial();
				if (material != null && Constant.PLANNING_MATERIAL_IDS.contains(material.getSapMaterialId())) {
					LOG.info(String.format("ContractItem %s has planning material"
					+ " - no quantity checking possible at the moment - delaying until actual "
					+ "material is selected", contractItem.getSapContractItemId()));

					return result = ContractItemQuantity.PLANNING_MATERIAL_QUANTITY;
				}

				LOG.error("Inconsistency found in contract data: targetQuantityUom is null "
						  + "on contract item (id: " + contractItem.getSapContractItemId() + ")");
				return result = BigDecimal.ZERO;
			}

			//--- Establish our baseline used quantity from FINISHED orders ---//
			BigDecimal totalUsedQty = BigDecimal.ZERO;

			if (contractItem.getTotalSentQuantity() != null) {
				totalUsedQty = totalUsedQty.add(contractItem.getTotalSentQuantity());
			}

			//--- Calculate the current pending used quantity for non-FINISHED orders ---//
			BigDecimal usedQtyNotKnownToSap = calculateUsedQtyNotKnownToSap(contractItem, contractItemQuantities);
			totalUsedQty = totalUsedQty.add(usedQtyNotKnownToSap);

					//--- Sanity check and return result ---//
			BigDecimal targetQty = contractItem.getTargetQuantity();
			if (targetQty.compareTo(totalUsedQty) < 0) {
				LOG.error("Total used quantity (" + totalUsedQty + ") > total target quantity ("
						  + targetQty + ") for contract item (id: "
						  + contractItem.getSapContractItemId() + "). Assuming 0 open quantity.");

				return result = BigDecimal.ZERO;
			}

			return result = targetQty.subtract(totalUsedQty);
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
	 * Return the sum of the loaded + ordered + delivered quantities for the open orders in the target
	 * unit of measurement of the SAP contract item.
	 * @param contractItem
	 * @return
	 */
	public BigDecimal computeOpenQtyInTargetUom(SapContractItemEntity contractItem) {
		final ReadOnlyDao<ContractItemQuantityEntity> dao = getDaoProvider().getContractItemQuantityEntityDao();

		Filter<ContractItemQuantityEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ContractItemQuantityEntity.ATTR_SAP_CONTRACT_ITEM_ID, contractItem.getSapContractItemId()));

		List<Long> notValidatedDeliveryTypes = Arrays.asList(Constant.DELIVERY_TYPE_RETURN_PICKUP, Constant.DELIVERY_TYPE_RETURN_DELIVERED);

		filter.addExpression(Restrictions.or(
				Restrictions.not(Restrictions.in(ContractItemQuantityEntity.ATTR_DELIVERY_TYPE_CD, notValidatedDeliveryTypes )),
				Restrictions.eq(ContractItemQuantityEntity.ATTR_PACKAGE_TYPE_CD, Constant.PACKAGE_TYPE_SERVICE)));

		List<ContractItemQuantityEntity> contractItemQuantities = dao.find(filter);

		return calculateUsedQtyNotKnownToSap(contractItem, contractItemQuantities);
	}

	/**
	 * Calculate the total used quantity for non-FINISHED orders
	 * @param contractItem the contract item whose quantities we want to compute
	 *
	 * @param contractItemQuantities  a List of ContractItemQuantityEntity objects. These objects contain
	 *                                the data required to compute the ContractItemQuantity instances. Note that
	 *                                FINISHED orders are filtered by the view - FINISHED order quantities are
	 *                                stored on the TOTAL_SEND_QUANTITY field for the contract item
	 *
	 * @return the total used quantity
	 *
	 */
	private BigDecimal calculateUsedQtyNotKnownToSap(SapContractItemEntity contractItem,
			List<ContractItemQuantityEntity> contractItemQuantities) {

		// Create UomBsEntity so we can reuse UomConverterService
		UomBsEntity uom = new UomBsEntity();
		UomDimensionBsEntity uomDimension = new UomDimensionBsEntity();
		uom.setUomDimension(uomDimension);

		BigDecimal totalUsedQtyNotKnownToSap = BigDecimal.ZERO;

		//--- Compute the current pending used quantity for non-FINISHED orders ---//
		if (contractItemQuantities != null) {
			for (ContractItemQuantityEntity itemQty : contractItemQuantities) {
				BigDecimal usedQty = null;

				Long orderStatus = itemQty.getOrderStatusCd();
				if (orderStatus != null && orderStatus == Constant.ORDER_STATUS_DELETED) {
					usedQty = BigDecimal.ZERO;
				}
				else if (itemQty.getDeliveredQuantity() != null) {
					uom.setUomCd(itemQty.getDeliveredUomCd());
					uom.setFuzzyQuantityFlag(itemQty.getDFuzzyQuantityFlag());
					uom.setNumeratorForConversion(itemQty.getDNumeratorForConversion());
					uom.setDenominatorForConversion(itemQty.getDDenominatorForConversion());
					uomDimension.setUomDimensionCd(itemQty.getDUomDimensionCd());

					usedQty = convertToTargetQuantityUom(
							itemQty.getDeliveredQuantity(), uom,
							contractItem,
							null,
							null);
				}
				else if (itemQty.getLoadedQuantity() != null) {
					uom.setUomCd(itemQty.getLoadedUomCd());
					uom.setFuzzyQuantityFlag(itemQty.getLFuzzyQuantityFlag());
					uom.setNumeratorForConversion(itemQty.getLNumeratorForConversion());
					uom.setDenominatorForConversion(itemQty.getLDenominatorForConversion());
					uomDimension.setUomDimensionCd(itemQty.getLUomDimensionCd());

					usedQty = convertToTargetQuantityUom(
							itemQty.getLoadedQuantity(), uom,
							contractItem,
							null,
							null);
				}
				else if (itemQty.getOrderedQuantity() != null) {
					uom.setUomCd(itemQty.getOrderedUomCd());
					uom.setFuzzyQuantityFlag(itemQty.getOFuzzyQuantityFlag());
					uom.setNumeratorForConversion(itemQty.getONumeratorForConversion());
					uom.setDenominatorForConversion(itemQty.getODenominatorForConversion());
					uomDimension.setUomDimensionCd(itemQty.getOUomDimensionCd());

					usedQty = convertToTargetQuantityUom(
							itemQty.getOrderedQuantity(), uom,
							contractItem,
							itemQty.getO2bNumeratorForConversion(),
							itemQty.getO2bDenominatorForConversion());
				}
				else {
					// we are dealing with a contract item with no outstanding orders
					LOG.info("No outstanding orders found for contract item (id: "
							+ contractItem.getSapContractItemId() + ")");
					usedQty = BigDecimal.ZERO;
				}

				if (usedQty == null) {
					LOG.error("Conversion error for contract item (id: "
							+ contractItem.getSapContractItemId() + "): "
							+ formatContractItemQuantityEntity(itemQty));

					// Assume that we have used everything.
					usedQty = contractItem.getTargetQuantity();
				}

				totalUsedQtyNotKnownToSap = totalUsedQtyNotKnownToSap.add(usedQty);
			}
		}
		return totalUsedQtyNotKnownToSap;
	}

	private String formatContractItemQuantityEntity(ContractItemQuantityEntity entity) {
		return new StringBuilder()
				.append("[")
				.append("delivered => ")
				.append("{qty: ").append(entity.getDeliveredQuantity())
				.append(",uom: ").append(entity.getDeliveredUomCd())
				.append(",uom-dimension: ").append(entity.getDUomDimensionCd())
				.append(",fuzzy: ").append(entity.getDFuzzyQuantityFlag())
				.append(",numerator: ").append(entity.getDNumeratorForConversion())
				.append(",denominator: ").append(entity.getDNumeratorForConversion())
				.append("}, ")
				.append("loaded => ")
				.append("{qty: ").append(entity.getLoadedQuantity())
				.append(",uom: ").append(entity.getLoadedUomCd())
				.append(",uom-dimension: ").append(entity.getLUomDimensionCd())
				.append(",fuzzy: ").append(entity.getLFuzzyQuantityFlag())
				.append(",numerator: ").append(entity.getLNumeratorForConversion())
				.append(",denominator: ").append(entity.getLNumeratorForConversion())
				.append("}, ")
				.append("ordered => ")
				.append("{qty: ").append(entity.getOrderedQuantity())
				.append(",uom: ").append(entity.getOrderedUomCd())
				.append(",uom-dimension: ").append(entity.getOUomDimensionCd())
				.append(",fuzzy: ").append(entity.getOFuzzyQuantityFlag())
				.append(",numerator: ").append(entity.getONumeratorForConversion())
				.append(",denominator: ").append(entity.getONumeratorForConversion())
				.append(",o2b-numerator: ").append(entity.getO2bNumeratorForConversion())
				.append(",o2b-denominator: ").append(entity.getO2bDenominatorForConversion())
				.append("}]")
				.toString();
	}

	/**
	 * Attempts to convert the given quantity from the given uom to the one defined by the contract
	 * item. If the conversion fails, it returns null.
	 *
	 * @param quantity
	 * @param fromUom            either the ordered quantity or the delivered quantity
	 * @param sapContractItem
	 * @param numeratorToBaseUom
	 * @return
	 */
	BigDecimal convertToTargetQuantityUom(BigDecimal quantity, UomBsEntity fromUom,
										  SapContractItemEntity sapContractItem, Long orderedNumerator, Long orderedDenominator) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"quantity", quantity},
							 {"fromUom", fromUom},
							 {"sapContractItem", sapContractItem},
							 {"orderedNumerator", orderedNumerator},
							 {"orderedDenominator", orderedDenominator}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertToTargetQuantityUom", params);
		}
		// ]]
		try {
			UomBsEntity targetQuantityUom = sapContractItem.getTargetQuantityUom();
			
			SapMaterialEntity sapMaterial = sapContractItem.getSapMaterial(); // Not nullable!

			if (targetQuantityUom != null) {
				String targetQuantityUomCd = targetQuantityUom.getUomCd();
				String fromUomCd = fromUom.getUomCd();

				try {
					/*
					 * If the fromUom is a vehicle type, we could still do the conversion even if
					 * that UoM is fuzzy. That is because "TLGNC_ORDER_ITEM.ORDERED_NUMERATOR" and
					 * "TLGNC_ORDER_ITEM.ORDERED_DENOMINATOR" hold the conversion factors to the
					 * material base UoM. So if we are converting from ordered UoM, we do
					 * the following conversions:
					 * 
					 *     fromUom -> material base UoM -> target quantity UoM
					 * 
					 * Note: target quantity UoM will never be a fuzzy UoM, ie. never a vehicle
					 * type!
					 * 
					 * However, delivered UoM by definition is expected to be the same as
					 * the base UoM.
					 */
					if (isFuzzyUom(fromUom) &&
							(orderedNumerator != null) &&
							(orderedDenominator != null)) {

						quantity = unitOfMeasurementConverterService.convert(quantity, orderedNumerator,
															   orderedDenominator, true);
						fromUom = sapMaterial.getBaseUom();
						fromUomCd = fromUom.getUomCd();
					}

					if (!targetQuantityUomCd.equals(fromUomCd)) {
						UomConversionResult ucr = unitOfMeasurementConverterService.convert(quantity, fromUom,
																			  targetQuantityUom, sapMaterial, null, null, false);

						quantity = ucr.getQuantity();
					}
				}
				catch (LogonConversionException e) {
					LOG.warn("Skipping conversion of quantity in UoM " + fromUomCd
									 + " to target quantity UoM " + targetQuantityUomCd
									 + " due to an error (sapMaterialId = " + sapMaterial.getSapMaterialId()
									 + "): " + e.getMessage());

					quantity = null;
				}
			}

			return result = quantity;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private boolean isFuzzyUom(UomBsEntity uom) {
		if (uom.getFuzzyQuantityFlag() != null) {
			return uom.getFuzzyQuantityFlag();
		}

		// fallback to the old implementation with an addition for container
		String uomCd = uom.getUomCd();
		return  Constant.UOM_CONTAINER.equals(uomCd) ||
				Constant.UOM_LKW.equals(uomCd) ||
				Constant.UOM_WAGON.equals(uomCd);
	}
	
	public Map<Long, QuantityRestrictionCalculation> getQuantityRestrCalcMap(List<OrderEntity> orders) {
		List<SapContractItemEntity> sapContractItems = new ArrayList<SapContractItemEntity>();
		for (OrderEntity order : orders) {
			List<OrderItemEntity> orderItems = order.getOrderItems();
			if(orderItems != null) {
				for (OrderItemEntity orderItem : orderItems) {
					if(orderItem.getSapContractItem() != null) {
						sapContractItems.add(orderItem.getSapContractItem());
					}
				}
			}
		}
		return calculateOpenQuantityForContractItems(sapContractItems, Collections.EMPTY_LIST);
	}
	
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ContributionCalcServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ContributionCalcServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CurrencyExchRateEntity;
import com.crh.logon.persistence.entities.common.IudCostEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class ContributionCalcServiceBean extends ContributionCalcServiceDispatcher implements ContributionCalcServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContributionCalcServiceBean.class);
	private static final String MAIN_CURRENCY = "EUR";
	
	@EJB private DispokarteServiceLocalInterface dispokarteService;

	
	public List<LogonException> updateCosts(OrderEntity order) {
		// [[
		List<LogonException> result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateCosts", params);
		}
		// ]]
		try {
			List<LogonException> list = new ArrayList<LogonException>();
			/**
			 * The main idea here is that if certain fields are null, then freeze order will set the ReportFinishedCommFlag to false in order to
			 * sign that the order commercially could not be finished. It is important therefore that the update does not leave the values as they are
			 * if they are invalid, but they have to be set to null.
			 * If we throw exception here then the changes to costs are going to be rolled back, and freeze order will not be able to determine that the 
			 * costs are invalid --> the order will still count as valid.
			 */
			// TODO (emoke Jun 15, 2010): Boon proposed to use ApplicationException instead of using these maps everywhere in this class
			Date orderActualDeliveryDat = order.getActualDeliveryDat();
			if (orderActualDeliveryDat == null) {
				LogonException logonException = new LogonException("TLGNC_ORDER.ACTUAL_DELIVERY_DAT is null. Order id: "
						+ order.getOrderId() + ". ", Reason.RES_ERROR_CCALC_DELIV_DATE_NOT_NULL,
						params);
				list.add(logonException);
			}
			if (order.getShippingpoint() == null){
				LogonException logonException = new LogonException("TLGNC_ORDER.SHIPPINGPOINT is null. Order id: "
						+ order.getOrderId() + ". ", Reason.RES_ERROR_CCALC_SHIPPOINT_NOT_NULL, params);
				list.add(logonException);
			}			 
			
			if (order.getOrderItems() != null) {
				for (OrderItemEntity item : order.getOrderItems()) {
					// TODO (emoke Jun 11, 2010):would make sense to invalidate first just like in case of overall problem with the order, values will be set anyway if they could be determined
					if ( order.getShippingpoint() == null || orderActualDeliveryDat == null) {
						//the order itself is invalid
						item.setGrossPriceMat(null); // gross_price_mat
						item.setProdCostMat(null); // prod_cost_mat
						item.setHandlingCostMat(null); // handling_cost_mat
						item.setIudCost(null);
						item.setOutCost(null);
						item.setDeliveryDatExchangeRate(null);
						continue;
					}
					
					if (item.getDeliveredQuantity() == null) {
						//freeze order: if null --> invalid
						LogonException logonException = new LogonException(
								"TLGNC_ORDER_ITEM.DELIVERED_QUANTITY is null. Order id: "
										+ order.getOrderId() + " and order item id: "
										+ item.getOrderItemId() + ". ",
								Reason.RES_ERROR_CCALC_DELIV_Q_NOT_NULL, params);
						list.add(logonException);
						
						// it does not make sense to continue here. there are several NPE candidates
						// further in the process
						continue;
					}
					
					if (item.getDeliveredUom() == null){
						//freeze order: if null --> invalid
						LogonException logonException = new LogonException(
								"TLGNC_ORDER_ITEM.DELIVERED_UOM_CD is null. Order id: "
										+ order.getOrderId() + " and order item id: "
										+ item.getOrderItemId() + ". ",
								Reason.RES_ERROR_CCALC_DELIV_UOM_NOT_NULL, params);
						list.add(logonException);
						
						// it does not make sense to continue here. there are several NPE candidates
						// further in the process
						continue;
					}
					
					//check if UOM are the same
					if (!item.getDeliveredUom().getUomCd().equals(item.getSapMaterial().getBaseUom().getUomCd())) {
						LogonException logonException = new LogonException(
								"TLGNC_ORDER_ITEM.DELIVERED_UOM is not equal TLGNC_ORDER_ITEM.SAP_MATERIAL.BASE_UOM. Order id: "
										+ order.getOrderId() + " and order item id: "
										+ item.getOrderItemId() + ". ",
								Reason.RES_ERROR_CCALC_WRONG_UOM, params);
						list.add(logonException);
					}
					
					// Variable Production Costs of Material
					try {
						BigDecimal cost = getProdCostMat(order, item);
						item.setProdCostMat(cost);
					}
					catch (LogonException e){
						//freeze order: if null --> invalid (this is checked as notnull prod_cost in fact in freezeorder)
						item.setProdCostMat(null);
						String msg = "Could not calculate TLGNC_ORDER_ITEM.PROD_COST_MAT. Order id: "
								+ order.getOrderId()
								+ " and order item id: "
								+ item.getOrderItemId() + ". ";
						LogonException logonException = new LogonException(msg, e.getReason(), params);
						list.add(logonException);
					}
					
					String itemCurrencyCd = item.getCurrency().getCurrencyCd();
					if (!order.getShippingpoint().getPlant().getPlantCd().equals(item.getProductionPlant().getPlantCd())) { 
						// ------A) Supply <> Production Plant------//
						
						// ADN48372: if order is dependent on another order (bimodal): value to be taken from the (rail) order's "cost of distribution" per ton, i.e. vlgnc_order_item_margin.total_dist_cost_pt
						if ( item.getBimodalRailOrderItem() != null ) {
							// TRUCK_item.quantity * railorder_item.getDistributionCust / railorder_item.delivered_quantity
							OrderItemEntity railOrderItem = item.getBimodalRailOrderItem();
							
							// check delivered quantity, might be zero!
							if ( railOrderItem.getDeliveredQuantity() == null || 0 == railOrderItem.getDeliveredQuantity().compareTo(new BigDecimal(0))) {
								// if any of them is null but quantity is == 0 => item.setIudCost (0) -> this is tolerated
								item.setIudCost(new BigDecimal(0));
							}
							else {
								BigDecimal distributionCost = null;
								if (railOrderItem.getIudCost() == null || railOrderItem.getHandlingCostMat() == null || railOrderItem.getOutCost() == null) {
									// if any of them is null but quantity is != 0 => item.setIudCost (null) -> flag as error in calculation
									item.setIudCost(null);
								}
								else {
									distributionCost = railOrderItem.getIudCost().add(railOrderItem.getHandlingCostMat()).add(railOrderItem.getOutCost());
								}
								
								if ( !railOrderItem.getDeliveredUom().getUomCd().equals( item.getDeliveredUom().getUomCd() )) {
									// if uom is not the same -> set iudCost to null
									//freeze order: if null --> invalid
									item.setIudCost(null);
								}
								if ( (distributionCost != null) && (railOrderItem.getDeliveredQuantity() != null) && (railOrderItem.getDeliveredQuantity().compareTo(BigDecimal.ZERO) != 0) ) {
									BigDecimal unitCost = distributionCost.divide(railOrderItem.getDeliveredQuantity(), Constant.SCALE, BigDecimal.ROUND_HALF_UP);
									if ( (unitCost != null) && (item.getDeliveredQuantity() != null) && (item.getDeliveredQuantity().compareTo(BigDecimal.ZERO) != 0) ) {
										BigDecimal resultCost = unitCost.multiply(item.getDeliveredQuantity());
										
										// do currency calculation
										BigDecimal exchangeRate = getExchangeRate(railOrderItem.getCurrency().getCurrencyCd(), itemCurrencyCd, orderActualDeliveryDat);
										if (exchangeRate != null) {
											resultCost = resultCost.multiply(exchangeRate);
											item.setIudCost(resultCost);
										}
									}
								}
							}
						}
						// end of ADN48372
						else {
							// Inter-Unit Distribution Costs 
							try {
								BigDecimal cost = getIudCostMat(order, item);
								item.setIudCost(cost);
							}
							catch (LogonException e){
								//freeze order: if null --> invalid
								item.setIudCost(null);
								String msg = "Could not calculate TLGNC_ORDER_ITEM.IUD_COST. Order id: "
										+ order.getOrderId()
										+ " and order item id: "
										+ item.getOrderItemId() + ". ";
								LogonException logonException = new LogonException(msg, e.getReason(), params);
								list.add(logonException);
							}
						}
					}
					else {
						// ------B) Supply == Production Plant------//
						// Inter-Unit Distribution Costs 
						item.setIudCost(BigDecimal.ZERO);
					}
					
					// Variable Handling Costs of Material
					try {
						BigDecimal cost = getHandlingCostMat(order, item);
						item.setHandlingCostMat(cost);
					}
					catch (LogonException e){
						//freeze order: if null --> invalid
						item.setHandlingCostMat(null);
						String msg = "Could not calculate TLGNC_ORDER_ITEM.HANDLING_COST_MAT. Order id: "
								+ order.getOrderId()
								+ " and order item id: "
								+ item.getOrderItemId() + ". ";
						LogonException logonException = new LogonException(msg, e.getReason(), params);
						list.add(logonException);
					}
					
					// Outbound Distribution Costs
					try {
						BigDecimal cost = getOutCost(order, item);
						if (cost != null) {
							item.setOutCost(cost.negate());
						}
					}
					catch (LogonException e){
						//freeze order: if null --> invalid
						item.setOutCost(null);
						String msg = "Could not calculate TLGNC_ORDER_ITEM.OUT_COST. Order id: "
								+ order.getOrderId() + " and order item id: "
								+ item.getOrderItemId() + ". ";
						LogonException logonException = new LogonException(msg, e.getReason(), params);
						list.add(logonException);
					}
					
					//Calculate order_item.DELIVERY_DAT_EXCHANGE_RATE.  EUR/order_item.curency_cd
					BigDecimal rate = getExchangeRate(MAIN_CURRENCY, itemCurrencyCd, orderActualDeliveryDat);
					if (rate != null) {
						item.setDeliveryDatExchangeRate(rate);
					} 
					else {
						//freeze order: if null --> invalid
						String msg = "Exchange rate for 'TLGNC_ORDER_ITEM.DELIVERY_DAT_EXCHANGE_RATE' cannot be calculated. Order id: "
								+ order.getOrderId()
								+ " and order item id: "
								+ item.getOrderItemId()
								+ ", currencyCd: "
								+ itemCurrencyCd != null ? itemCurrencyCd : "null"
								+ ", actualDeliveryDat: "
								+ orderActualDeliveryDat != null ? orderActualDeliveryDat.toString() : "null";
						LogonException logonException = new LogonException(msg,
								Reason.RES_ERROR_CCALC_DELIV_DATE_EXCH_RATE, params);
						LOG.error(msg);
						list.add(logonException);
					}
					
					//Gross price of the material
					if (item.getRate() != null) {
						BigDecimal grossPriceMat = item.getRate().multiply(item.getDeliveredQuantity());
						item.setGrossPriceMat(grossPriceMat);
					}
				}
			}
			return result = list;
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
	 * Calculate Inter-Unit Distribution Costs 
	 * @param order order
	 * @param item order item 
	 * @param service ContributionCalcService
	 * @returnOutbound Distribution Costs
	 */
	private BigDecimal getIudCostMat(OrderEntity order, OrderItemEntity item) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"order", order},
							{"item", item}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getIudCostMat", params);
		}
		// ]]
		try {
			
			BigDecimal cost = null;
			
			IudCostEntity iudCost = fetchIudCost(item.getProductionPlant().getPlantCd(), 
					order.getShippingpoint().getPlant().getPlantCd(), 
					item.getSapMaterial().getBaseUom().getUomCd(),
					item.getSapMaterial().getMaterialPackage().getMaterialPackageCd());
			 
			if (iudCost == null) {
				LOG.error("Could not find TLGNC_IUD_COST for the given production plant = " 
						+ item.getProductionPlant().getPlantCd() + " and supply plant = " 
						+ order.getShippingpoint().getPlant().getPlantCd() +".");
				throw new LogonException(Reason.RES_ERROR_CCALC_IUD_COST);
			}
			
			// current/future value checked based on order.actual_delivery_dat
			cost = geCostValue(order.getActualDeliveryDat(), iudCost.getFutureValidFromDat(), iudCost.getVarIudCost(), iudCost.getFutureVarIudCost());
			
			//check if cost was calculated
			if (cost == null) {
				LOG.error("Whether TLGNC_IUD_COST.VARIABLE_PROD_COST_MAT or TLGNC_IUD_COST.FUTURE_VARIABLE_PROD_COST_MAT is null.");
				throw new LogonException(Reason.RES_ERROR_CCALC_PRO_VAR_PROD_COST);
			}
			
			// multiplied by Q
			cost = item.getDeliveredQuantity().multiply(cost);
			
			// converted into the order item's currency if currencies differ.
			if (iudCost.getCurrency() != null) {
				if (!item.getCurrency().getCurrencyCd().equals(iudCost.getCurrency().getCurrencyCd())) {
					BigDecimal rate = getExchangeRate(iudCost
							.getCurrency().getCurrencyCd(), item
							.getCurrency().getCurrencyCd(), order
							.getActualDeliveryDat());
			
					if (rate != null) {
						cost = cost.multiply(rate);
					} 
					else {
						LOG.error("Currency exchange rate " + iudCost.getCurrency().getCurrencyCd() + ":" + item.getCurrency().getCurrencyCd() + " cannot be calculated.");
						throw new LogonException(Reason.RES_ERROR_CCALC_IUD_EXCH_RATE);
					}
				}
			}
			else {
				LOG.error("tlgnc_iud_cost.currency_cd is null.");
				throw new LogonException(Reason.RES_ERROR_CCALC_IUD_CUR_CODE);
			}
			
			return result = cost;
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
	 * Calculate Outbound Distribution Costs
	 * 
	 * NOTE (romek Aug 31, 2011): In Logon 1.6 (branch logon_1_6_0_1) this logic was based on transport cost
	 * conditions from SAP (TLGNS_SAP_TRANSPORT_COND), but in EBM we do not know yet how it will be done.
	 * In case they are needed again, for more information you can also check our Twiki:
	 * https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/WorkshopCrh20090622#Minutes_1_Transport_Conditions
	 * 
	 * @param order order
	 * @param item order item 
	 * @param service ContributionCalcService
	 * @param cost OUT cost
	 * @returnOutbound Distribution Costs
	 */
	private BigDecimal getOutCost(OrderEntity order, OrderItemEntity item) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"order", order},
							{"item", item}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOutCost", params);
		}
		// ]]
		try {
			ShiptoEntity shipto = dispokarteService.fetchShiptoBySapId(order.getShiptoSapBp().getSapId());
			
			if (shipto == null) {
				LOG.error("Could not find TLGNC_SHIPTO.  DispokarteService.fetchShiptoByBpI(" + order.getShiptoSapBp().getSapId() + ") returned null!");
				throw new LogonException(Reason.RES_ERROR_CCALC_OUT_SHIPTO);
			}
			
			// For now there are no transport cost conditions.
			return result = BigDecimal.ZERO;
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
	 * Calculate Variable Handling Costs of Material
	 * @param order order
	 * @param item order item 
	 * @param service ContributionCalcService
	 * @param cost handling cost
	 * @return Variable Handling Costs of Material
	 */
	private BigDecimal getHandlingCostMat(OrderEntity order, OrderItemEntity item) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"order", order},
							{"item", item}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getHandlingCostMat", params);
		}
		// ]]
		try {
			
			BigDecimal cost = null;
			
			//true if it is modeled material specific (value from tlgnc_mat_shippingpoint).
			boolean matShippFlag = true;
			
			MatShippingpointEntity matShipp = fetchMatShipp(order.getShippingpoint().getShippingpointId(), item.getSapMaterial().getSapMaterialId());
			
			if (matShipp != null) {
				// current/future value checked based on order.actual_delivery_dat
				cost = geCostValue(order.getActualDeliveryDat(), matShipp.getFutureValidFromDat(), matShipp.getVarHandlingCostMat(), matShipp.getFutureVarHandlingCostMat());
				boolean isFutureCost = isFutureCost(order.getActualDeliveryDat(), matShipp.getFutureValidFromDat());
				
				// check if cost was calculated
				if (cost == null) {
					LOG.error("ORDER_ID=" + order.getOrderId() + 
							", ORDER_NO=" + order.getOrderNumber() + 
							", ORDER_ITEM_ID=" + item.getOrderItemId() + 
							", SAP_MATERIAL_ID=" + item.getSapMaterial().getSapMaterialId() + 
							", SHIPPINGPOINT_ID=" + order.getShippingpoint() == null ? "null" : order.getShippingpoint().getShippingpointId() +  
							", Variable Handling Costs of Material cannot be calculated (TLGNC_MAT_SHIPPINGPOINT." + 
							(isFutureCost ? "FUTURE_VARIABLE_PROD_COST_MAT" : "VARIABLE_PROD_COST_MAT") + 
							" is null).");
					matShippFlag = false;
				}
			} 
			else {
				matShippFlag = false;
			}
			
			
			if (matShippFlag) {
				//-------------value from tlgnc_mat_shippingpoint------------//
				
				// multiplied by Q
				cost = item.getDeliveredQuantity().multiply(cost);
				
				// converted into the order item's currency if currencies differ.
				if ( (matShipp != null) && (matShipp.getCurrency() != null) ) {
					if (!item.getCurrency().getCurrencyCd().equals(matShipp.getCurrency().getCurrencyCd())) {
						BigDecimal rate = getExchangeRate(matShipp
								.getCurrency().getCurrencyCd(), item
								.getCurrency().getCurrencyCd(), order
								.getActualDeliveryDat());
				
						if (rate != null) {
							cost = cost.multiply(rate);
						} 
						else {
							LOG.error("Currency exchange rate " + matShipp.getCurrency().getCurrencyCd() + ":" + item.getCurrency().getCurrencyCd() + " cannot be calculated.");
							throw new LogonException(Reason.RES_ERROR_CCALC_HAN_EXCH_RATE);
						}
					}
				}
				else {
					LOG.error("tlgnc_mat_shippingpoint.currency_cd is null!");
					throw new LogonException(Reason.RES_ERROR_CCALC_HAN_CUR_CODE_MAT);
				}
				
				//don't need to check if UOM are the same
			} 
			else {
				//-------------value from tlgnc_shippingpoint------------//
				ShippingpointEntity shipp = order.getShippingpoint();
				
				// current/future value checked based on order.actual_delivery_dat
				cost = geCostValue(order.getActualDeliveryDat(), shipp.getFutureValidFromDat(), shipp.getVarHandlingCostMat(), shipp.getFutureVarHandlingCostMat());
			
				//check if cost was calculated
				if (cost == null) {
					LOG.error("Whether TLGNC_SHIPPINGPOINT.VARIABLE_PROD_COST_MAT or TLGNC_SHIPPINGPOINT.FUTURE_VARIABLE_PROD_COST_MAT is null.");
					throw new LogonException(Reason.RES_ERROR_CCALC_HAN_VAR_PROD_COST);
				}
				
				//check if UOM are the same
				if (shipp.getUom() != null) {
					if (!item.getDeliveredUom().getUomCd().equals(shipp.getUom().getUomCd())) {
						LOG.error("TLGNC_ORDER_ITEM.DELIVERED_UOM is not equal TLGNC_SHIPPINGPOINT.UOM_CD.");
						throw new LogonException(Reason.RES_ERROR_CCALC_HAN_WRONG_UOM);
					}
				}
				else {
					LOG.error("TLGNC_SHIPPINGPOINT.UOM_CD is null.");
					throw new LogonException(Reason.RES_ERROR_CCALC_HAN_UOM_NOT_NULL);
				}
				
				// multiplied by Q
				cost = item.getDeliveredQuantity().multiply(cost);
				
				// converted into the order item's currency if currencies differ.
				if (shipp.getCurrency() != null) {
					if (!item.getCurrency().getCurrencyCd().equals(shipp.getCurrency().getCurrencyCd())) {
						BigDecimal rate = getExchangeRate(shipp
								.getCurrency().getCurrencyCd(), item
								.getCurrency().getCurrencyCd(), order
								.getActualDeliveryDat());
				
						if (rate != null) {
							cost = cost.multiply(rate);
						} 
						else {
							LOG.error("Currency exchange rate " + shipp.getCurrency().getCurrencyCd() + ":" + item.getCurrency().getCurrencyCd() + " cannot be calculated.");
							throw new LogonException(Reason.RES_ERROR_CCALC_HAN_EXCH_RATE);
						}
					}
				}
				else {
					LOG.error("tlgnc_shippingpoint.currency_cd is null!");
					throw new LogonException(Reason.RES_ERROR_CCALC_HAN_CUR_CODE);
				}
			
			}
			
			return result = cost;
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
	 * Calculate Variable Production Costs of Material
	 * @param order order
	 * @param item order item 
	 * @param service ContributionCalcService
	 * @return Variable Production Costs of Material
	 */
	private BigDecimal getProdCostMat(OrderEntity order, OrderItemEntity item) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"order", order},
							{"item", item}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getProdCostMat", params);
		}
		// ]]
		try {
				
			BigDecimal cost = null;
			MatPlantEntity matPlant = fetchMatPlant(item
					.getProductionPlant().getPlantCd(), item
					.getSapMaterial().getSapMaterialId());
			
			if (matPlant == null) {
				LOG.error("Could not find TLGNC_MAT_PLANT for the given plant_cd=" 
						+ item.getProductionPlant().getPlantCd() + " and sap_material_id=" 
						+ item.getSapMaterial().getSapMaterialId() +".");
				throw new LogonException(Reason.RES_ERROR_CCALC_PRO_MAT_PLANT);
			}
			
			// current/future value checked based on order.actual_delivery_dat
			cost = geCostValue(order.getActualDeliveryDat(), matPlant.getFutureValidFromDat(), matPlant.getVariableProdCostMat(), matPlant.getFutureVariableProdCostMat());
			
			//check if cost was calculated
			if (cost == null) {
				LOG.error("Whether TLGNC_MAT_PLANT.VARIABLE_PROD_COST_MAT or TLGNC_MAT_PLANT.FUTURE_VARIABLE_PROD_COST_MAT is null.");
				throw new LogonException(Reason.RES_ERROR_CCALC_PRO_VAR_PROD_COST);
			}
			
			// multiplied by Q
			cost = item.getDeliveredQuantity().multiply(cost);
			
			// converted into the order item's currency if currencies differ.
			if (matPlant.getCurrency() != null) {
				if (!item.getCurrency().getCurrencyCd().equals(matPlant.getCurrency().getCurrencyCd())) {
					BigDecimal rate = getExchangeRate(matPlant
							.getCurrency().getCurrencyCd(), item
							.getCurrency().getCurrencyCd(), order
							.getActualDeliveryDat());
			
					if (rate != null) {
						cost = cost.multiply(rate);
					} 
					else {
						LOG.error("Currency exchange rate " + matPlant.getCurrency().getCurrencyCd() + ":" + item.getCurrency().getCurrencyCd() + " cannot be calculated.");
						throw new LogonException(Reason.RES_ERROR_CCALC_PRO_EXCH_RATE);
					}
				}
			}
			else {
				LOG.error("tlgnc_mat_plant.currency_cd is null!");
				throw new LogonException(Reason.RES_ERROR_CCALC_PRO_CUR_CODE);
			}
			
			return result = cost;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	
	
	/**  calculates exchange rate for the given currencies 
	 * @param curFrom currency to convert from 
	 * @param curTo currency to convert to
	 * @param validFromDate exchange rate for the given date
	 * @param ContributionCalcService
	 * @return exchange rate
	 */
	private BigDecimal getExchangeRate(String curFrom, String curTo,
			Date validFromDate) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"curFrom", curFrom},
							{"curTo", curTo},
							{"validFromDate", validFromDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExchangeRate", params);
		}
		// ]]
		try {
			BigDecimal rateFrom = fetchExchangeRate(curFrom, validFromDate);
			BigDecimal rateTo = fetchExchangeRate(curTo, validFromDate);
			
			if (logDebugEnabled) {
				LOG.debug("Currency from '" + curFrom + "' has exchange rate " + rateFrom);
				LOG.debug("Currency to '" + curTo + "' has exchange rate " + rateTo);
			} 
			if ((rateFrom != null) && (rateTo != null)) {
				BigDecimal rate = rateTo.divide(rateFrom, Constant.SCALE, BigDecimal.ROUND_HALF_UP);
				return result = rate;
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
	 * The "future_valid_from_dat" must be consulted by Logon when it determines a cost value. For an order the date which matters is the order delivery date:
     * if future_valid_from_dat is null : take the value "xyz_cost"
     * if < future_valid_from_dat : take the current value "xyz_cost"
     * if >= future_valid_from_dat : take the value "future_xyz_cost" 
	 * @param actualDeliveryDat
	 * @param futureValidFromDate
	 * @param cost
	 * @param futureCost
	 * @return cost
	 */
	private BigDecimal geCostValue(Date actualDeliveryDat, Date futureValidFromDate, BigDecimal cost, BigDecimal futureCost) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"actualDeliveryDat", actualDeliveryDat},
							{"futureValidFromDate", futureValidFromDate},
							{"cost", cost},
							{"futureCost", futureCost}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "geCostValue", params);
		}
		// ]]
		try {
			
			if (isFutureCost(actualDeliveryDat, futureValidFromDate)) {
				return result = futureCost;
			} 
			else {
				return result = cost;
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
	
	private boolean isFutureCost(Date actualDeliveryDat, Date futureValidFromDate) {
		return !(futureValidFromDate == null || actualDeliveryDat.before(futureValidFromDate));		
	}

	
	/**
	 * Fetches exchange rate for the given currency
	 * @adnlog.exclude return
	 */
	public BigDecimal fetchExchangeRate(String currency, Date validFromDate) {
		// [[
		Object[][] params = {{"currency", currency},
							{"validFromDate", validFromDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchExchangeRate", params);
		}
		// ]]
		try {
			//if actual delivery date is not set
			if (validFromDate == null){
				validFromDate = new Date(); 
			}
			
			final ReadWriteDao<CurrencyExchRateEntity> dao = getDaoProvider().getCurrencyExchRateEntityDao();
			
			final Filter<CurrencyExchRateEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(CurrencyExchRateEntity.ATTR_CURRENCY_CD, currency));
			filter.addExpression(Restrictions.ge(CurrencyExchRateEntity.ATTR_VALID_FROM_DAT, validFromDate));
			filter.addExpression(Order.desc(CurrencyExchRateEntity.ATTR_VALID_FROM_DAT));
			
			List<CurrencyExchRateEntity> list = dao.find(filter);
			if (!list.isEmpty()) {
				CurrencyExchRateEntity rate = (CurrencyExchRateEntity)list.get(0);
				return rate.getExchangeRate();
			}
			
			return null;
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
	 * Fetches MatPlantEntity for the given plant code and sap material id
	 * @adnlog.exclude return
	 */
	public MatPlantEntity fetchMatPlant(String plantCd, Long materialId) {
		// [[
		Object[][] params = {{"plantCd", plantCd},
							{"materialId", materialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMatPlant", params);
		}
		// ]]
		try {
			final ReadWriteDao<MatPlantEntity> dao = getDaoProvider().getMatPlantEntityDao();
			
			final Filter<MatPlantEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID, materialId));
			
			return  dao.findSingle(filter);
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
	 * Fetches MatShippingpointEntity for the given shipping point id code and sap material id
	 * @adnlog.exclude return
	 */
	public MatShippingpointEntity fetchMatShipp(Long shippId, Long materialId) {
			// [[
			Object[][] params = {{"shippId", shippId},
								{"materialId", materialId}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "fetchMatShipp", params);
			}
			// ]]
			try {
				final ReadWriteDao<MatShippingpointEntity> dao = getDaoProvider().getMatShippingpointEntityDao();
				
				final Filter<MatShippingpointEntity> filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(MatShippingpointEntity.ATTR_SHIPPINGPOINT_ID, shippId));
				filter.addExpression(Restrictions.eq(MatShippingpointEntity.ATTR_SAP_MATERIAL_ID, materialId));
				
				return  dao.findSingle(filter);
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
	 * Fetches IudCostEntity for the given "production plant to supply plant" and UOM code
	 * material id
	 * 
	 * @adnlog.exclude return
	 */
	public IudCostEntity fetchIudCost(String fromPlantCd, String toPlantCd, String uomCd, String materialPackageCd) {
		// [[
		Object[][] params = {{"fromPlantCd", fromPlantCd},
							{"toPlantCd", toPlantCd},
							{"uomCd", uomCd},
							{"materialPackageCd", materialPackageCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchIudCost", params);
		}
		// ]]
		try {
			final ReadWriteDao<IudCostEntity> dao = getDaoProvider().getIudCostEntityDao();
			
			final Filter<IudCostEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(IudCostEntity.ATTR_FROM_PLANT_CD, fromPlantCd));
			filter.addExpression(Restrictions.eq(IudCostEntity.ATTR_TO_PLANT_CD, toPlantCd));
			filter.addExpression(Restrictions.eq(IudCostEntity.ATTR_UOM_CD, uomCd));
			filter.addExpression(Restrictions.eq(IudCostEntity.ATTR_MATERIAL_PACKAGE_CD, materialPackageCd));
			
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
	
	
}

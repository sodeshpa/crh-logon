/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.dispatchers.PricingCallServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.PricingCallServiceLocalInterface;

import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ITM_NUMBER_type5;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZFSD_PRICE_SIMULATIONResponse;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZSSD_PS_ITEMS;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZTSD_PS_ITEMS_type0;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZTSD_PS_ITEMS_type1;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SapOutgoingDataServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PricingCallEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.LogonToSap;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTO;

/**
 * Service bean to handle: LRQ 045.0 Interface - Pricing Call
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class PricingCallServiceBean extends PricingCallServiceDispatcher implements PricingCallServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PricingCallServiceBean.class);

	@EJB private OrderServiceLocalInterface orderService;
	@EJB private SapOutgoingDataServiceLocalInterface sapOutgoingDataService;
	
	/**
	 * Sends a PricingCall request to SAP and also books the reason to DB
	 * @param order
	 */
	public void sendPricingCall(OrderEntity order) {
		// [[
		Object[][] params = { { "order", order } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendPricingCall", params);
		}
		// ]]
		try {

			ReadWriteDao<PricingCallEntity> pricingCallDao = getDaoProvider().getPricingCallEntityDao();

			SareaEntity sarea = order.getSarea();

			List<OrderItemEntity> pricingCallOrderItems = new ArrayList<OrderItemEntity>();
			
			// This map will be needed later when we need to find the orderItem belonging to a specific order_item_no
			final Map<Long, OrderItemEntity> orderItemMap = new HashMap<Long, OrderItemEntity>();

			for (OrderItemEntity orderItem : order.getOrderItems()) {
				if (orderService.isPricingCallRequiredForOrderItem(orderItem, sarea)) {
					pricingCallOrderItems.add(orderItem);
					
					final Long orderItemNo = orderItem.getOrderItemNo();
					orderItemMap.put(orderItemNo, orderItem);
				}
			}
			
			// Sanity check - do we have at least one Order Item for the Pricing Call
			if (pricingCallOrderItems.isEmpty()) {
				LOG.warn("Avoid sending Pricing Call because there are no orderItems which would require a Pricing Call.");
				return;
			}

			PricingCallDTO pricingCallDTO = sapOutgoingDataService.getPricingCallDTO(order, pricingCallOrderItems);

			ZFSD_PRICE_SIMULATIONResponse response = LogonToSap.sendPriceSimulation(getConfigBean(), pricingCallDTO);
			ZTSD_PS_ITEMS_type0 responseItemType = response.getZTSD_PS_ITEMS();

			if (responseItemType == null) {
				String message = "Pricing call did not return any ZTSD_PS_ITEMS";
				LOG.error(message);
				throw new LogonException(message, Reason.RES_ERROR_PRICING_CALL_BUSINESS_ERROR);
			}

			ZSSD_PS_ITEMS[] responseItems = responseItemType.getItem();

			if (responseItems == null) {
				String message = "Pricing call did not return any ZTSD_PS_ITEMS.item";
				LOG.error(message);
				throw new LogonException(message, Reason.RES_ERROR_PRICING_CALL_BUSINESS_ERROR);
			}

			for (ZSSD_PS_ITEMS responseItem : responseItems) {

				// Prepare attributes
				Date requestTs = new Date();

				BigDecimal netValue = null;
				if (responseItem.getNETWR() != null) {
					netValue = responseItem.getNETWR().getNETWR_type0();
				}

				BigDecimal vatAmount = null;
				if (responseItem.getMWSBP() != null) {
					vatAmount = responseItem.getMWSBP().getMWSBP_type0();
				}

				BigDecimal targetQuantity = BigDecimal.ZERO;
				if (responseItem.getTARGET_QTY() != null) {
					targetQuantity = new BigDecimal(responseItem.getTARGET_QTY().getTARGET_QTY_type0());
				}

				String targetQuantityUomCd = Constant.UOM_TONS;
				if (responseItem.getTARGET_QU() != null) {
					targetQuantityUomCd = responseItem.getTARGET_QU().getTARGET_QU_type0();
				}

				ITM_NUMBER_type5 itmNumber = responseItem.getITM_NUMBER();
				String orderItemNoStr = itmNumber.getITM_NUMBER_type4();
				Long orderItemNo = Long.parseLong(orderItemNoStr);
				
				OrderItemEntity orderItem = orderItemMap.get(orderItemNo);

				SapMaterialEntity sapMaterial = orderItem.getSapMaterial();

				SapContractItemEntity sapContractItem = orderItem.getSapContractItem();

				SapContractEntity sapContract = null;
				CurrencyBsEntity currency = null;

				if (sapContractItem != null) {
					sapContract = sapContractItem.getSapContract();
					currency = sapContract.getCurrency();
				}

				SapBpEntity soldtoSapBp = order.getSoldtoSapBp();

				PricingCallEntity pricingCallEntity = new PricingCallEntity();
				pricingCallEntity.setRequestTs(requestTs);
				pricingCallEntity.setNetValue(netValue);
				pricingCallEntity.setVatAmount(vatAmount);
				pricingCallEntity.setTargetQuantity(targetQuantity);
				pricingCallEntity.setTargetQuantityUomCd(targetQuantityUomCd);
				pricingCallEntity.setOrderItem(orderItem);
				pricingCallEntity.setSapMaterial(sapMaterial);
				pricingCallEntity.setSapContract(sapContract);
				pricingCallEntity.setSoldtoSapBp(soldtoSapBp);
				pricingCallEntity.setCurrency(currency);

				pricingCallDao.persist(pricingCallEntity);

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
	*	Finds the most recent {@code PricingCallEntity} by the orderId field
	 * @param orderItemId - identifier of the mapped {@code OrderItemEntity}
	 * @return the found {@code PricingCallEntity} or if none exists null
	 */
	public PricingCallEntity findPricingCallEntityByOrderItemId(Long orderItemId) {
		// [[
		PricingCallEntity result = null;
		Object[][] params = {{"orderItemId", orderItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findPricingCallEntityByOrderItemId", params);
		}
		// ]]
		try {
			
			ReadWriteDao<PricingCallEntity> pricingCallDao = getDaoProvider().getPricingCallEntityDao();
			
			Filter<PricingCallEntity> filter = pricingCallDao.createFilter();
			filter.addExpression(Restrictions.eq(PricingCallEntity.ATTR_ORDER_ITEM_ID, orderItemId));
			filter.addExpression(Order.desc(PricingCallEntity.ATTR_PRICING_CALL_ID));
			
			List<PricingCallEntity> resultList = pricingCallDao.find(filter);
			
			if (resultList.isEmpty()) {
				result = null;
			}
			else {
				result = resultList.get(0); 
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

}

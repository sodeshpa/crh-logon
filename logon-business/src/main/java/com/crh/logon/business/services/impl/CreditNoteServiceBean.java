/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.dispatchers.CreditNoteServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ContributionCalcServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CreditNoteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FrozenTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.LangUtil;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.SortOrder;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.ReturnQuantHandlingBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.CreditNoteProposalsEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class CreditNoteServiceBean extends CreditNoteServiceDispatcher implements CreditNoteServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CreditNoteServiceBean.class);

	@EJB private CreditNoteServiceLocalInterface creditNoteService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private GoodsMovementServiceLocalInterface goodsMovementService;
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	
	@EJB private ContributionCalcServiceLocalInterface contributionCalcService;
	@EJB private FrozenTablesServiceLocalInterface frozenTablesService;


	/**
	 * @param query
	 * @param langCd
	 * @return
	 */
	public long countCreditNotesList(String query, String langCd) {
		// [[
		long result = 0L;
		Object[][] params = {{"query", query},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countCreditNotesList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<CreditNoteProposalsEntity> dao = getDaoProvider().getCreditNoteProposalsEntityDao();
			final Filter<CreditNoteProposalsEntity> filter = createCreditNoteListFilter(null, query, langCd);
			
			return result = dao.count(filter);
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
	 * @param skip
	 * @param num
	 * @param sortOrder
	 * @param query
	 * @param langCd
	 * @return
	 */
	public List<CreditNoteProposalsEntity> fetchCreditNotesList(int skip, int num, SortOrder sortOrder,
			String query, String langCd) {
		// [[
		List<CreditNoteProposalsEntity> result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sortOrder", sortOrder},
							{"query", query},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCreditNotesList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<CreditNoteProposalsEntity> dao = getDaoProvider().getCreditNoteProposalsEntityDao();
			final Filter<CreditNoteProposalsEntity> filter = createCreditNoteListFilter(sortOrder, query, langCd);
			
			return result = dao.findNRecs(skip, num, filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@AdnTrace
	public List<CreditNoteProposalsEntity> fetchCreditNotesListByOrderIds(List<Long> orderIds, String langCd) {

		final ReadOnlyDao<CreditNoteProposalsEntity> dao = getDaoProvider().getCreditNoteProposalsEntityDao();
		String langCode = (StringUtils.empty(langCd) ? "en" : langCd);

		Filter<CreditNoteProposalsEntity> filter = new FilterImpl<>();

		filter.addExpression(Restrictions.eq(CreditNoteProposalsEntity.ATTR_LANG_CD, langCode));
		filter.addExpression(Restrictions.in(CreditNoteProposalsEntity.ATTR_ORDER_ID, orderIds));

		return dao.find(filter);
	}
	
	
	public void handleCreditNoteQuantities(OrderItemEntity orderItem, boolean createNote) throws LogonException {
		// [[
		Object[][] params = { { "orderItem", orderItem }, { "createNote", createNote + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleCreditNoteQuantities", params);
		}
		// ]]
		try {
			//--- Handle Return Quantity Code ---//
			Long returnQuantHandlingCode = (createNote ? Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_CREATED
					: Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT);
			ReturnQuantHandlingBsEntity rqhBsEntity = referentialDataService.findBsEntity(
					ReturnQuantHandlingBsEntity.class, returnQuantHandlingCode);

			orderItem.setReturnQuantHandling(rqhBsEntity);

			BigDecimal loadedQty = orderItem.getLoadedQuantity();
			BigDecimal returnedQty = orderItem.getReturnedQuantity();

			UomBsEntity deliveredUom = orderItem.getDeliveredUom();
			if(deliveredUom == null) {
				String msgKey = "creditnote.save.deliveredUom.isblank";
				LOG.error(Message.lookup(Locale.ENGLISH, msgKey));
				throw new LogonException(msgKey, Reason.RES_ERROR_IMPL);				
			}
			
			// update order_item.delivered_quantity = loaded - returned
			if (loadedQty != null && returnedQty != null) {
				try {
					UomBsEntity loadedUom = orderItem.getLoadedUom();
					
					SapMaterialEntity orderItemSapMaterial = orderItem.getSapMaterial();
					
					BigDecimal deliveredQuantity = loadedQty.subtract(returnedQty);
					
					UomConversionResult ucr = unitOfMeasurementConverterService.convert(deliveredQuantity,
							loadedUom.getUomCd(), deliveredUom.getUomCd(),
							orderItemSapMaterial.getSapMaterialId(), null, null, false);
					deliveredQuantity = ucr.getQuantity();
					orderItem.setDeliveredQuantity(deliveredQuantity);
				}
				catch (LogonConversionException e) {
					final StringBuilder msg = new StringBuilder();
					msg.append("Could not convert delivered quantity UOM ").append(
							orderItem.getOrderedUom()).append(" to").append(
							orderItem.getDeliveredUom()).append(" for material ").append(
							orderItem.getSapMaterial()).append("(").append(e.getMessage()).append(
							")");
					LOG.error(msg.toString());
					throw new LogonException(msg.toString(), e);
				}
			}
			//--- Handle TOTAL_SENT_QUANTITY ---//

			// LOGON-6624/LRQ 303.0 Advance Payment, 6.9.2 Credit Note Process
			// only an approved credit note will raise the total open quantity
			// (i.e. lower the total sent qty which was set when the order was FINISHED)
			SapContractItemEntity contractItem = orderItem.getSapContractItem();
			if (createNote && returnedQty != null && contractItem != null
					&& contractItem.getIsApGroupLeader()) {

				BigDecimal totalSentQty = contractItem.getTotalSentQuantity();
				if (totalSentQty != null) {
					// it is not a required field and we could be dealing with an item which
					// existed before the DB was updated

					BigDecimal returnedQtyInDeliveredUom = null;
					try {
						UomConversionResult ucr = unitOfMeasurementConverterService.convert(returnedQty,
								orderItem.getReturnedUom().getUomCd(), orderItem.getDeliveredUom().getUomCd(),
								orderItem.getSapMaterial().getSapMaterialId(), null, null, false);
						
						returnedQtyInDeliveredUom = ucr.getQuantity();
					}
					catch (LogonConversionException e) {
						final StringBuilder msg = new StringBuilder();
						msg.append("Could not convert returned quantity UOM ").append(
								orderItem.getReturnedUom()).append(" to").append(
								orderItem.getDeliveredUom()).append(" for material ").append(
								orderItem.getSapMaterial()).append("(").append(e.getMessage()).append(
								")");
						LOG.error(msg.toString());
						throw new LogonException(msg.toString(), e);
					}
					
					// SANITY CHECK: this should not happen if set
					if (totalSentQty.compareTo(returnedQtyInDeliveredUom) < 0) {
						throw new LogonException(String.format(
								"Returned qty %s > total sent qty %s on contract item %s",
								returnedQtyInDeliveredUom, totalSentQty, contractItem.getSapContractItemId()),
								Reason.RES_IMPL_BUG, params);
					}

					contractItem.setTotalSentQuantity(totalSentQty.subtract(returnedQtyInDeliveredUom));
				}
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
	public CreditNoteEntity _processReturnQuantity(Long orderItemId, boolean createNote) throws LogonException {
		// [[
		CreditNoteEntity result = null;
		Object[][] params = {{"orderItemId", orderItemId},
							{"createNote", createNote + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_processReturnQuantity", params);
		}
		// ]]
		try {
			if (orderItemId == null) {
				throw new LogonException(
						"No orderItemId has been provided.",
						Reason.RES_ERROR_IMPL, params);
			}
			OrderItemEntity orderItem = getDaoProvider().getOrderItemEntityDao().findById(orderItemId);
			if (orderItem == null) {
				throw new LogonException(
						"No OrderItemEntity has been found with the provided orderItemId.",
						Reason.RES_ERROR_IMPL, params);
			}
			OrderEntity order = orderItem.getOrder();
			
			CreditNoteEntity creditNote = null;
			if (createNote) {
				Locale locale = LangUtil.getOrderLocale(order);
				Long orderNumber = order.getOrderNumber();
				String orderNbrString = 
					(orderNumber == null ? "null" : orderNumber.toString());
				String noteDelivery = Message.lookup(locale,
						"creditnote.save.details.note.delivery");
				
				noteDelivery = noteDelivery.replaceFirst("\\{0\\}", orderNbrString);
				
				creditNote = new CreditNoteEntity();
				creditNote.setCreditQuantity(orderItem.getReturnedQuantity());
				creditNote.setCreditUom(orderItem.getReturnedUom());
				creditNote.setOrderItem(orderItem);
				creditNote.setOrderNumber(fetchSequenceNextVal("SLGNC_ORDER_NUMBER"));
				creditNote.setNoteDelivery(noteDelivery);
				getDaoProvider().getCreditNoteEntityDao().persist(creditNote);
			}
			handleCreditNoteQuantities(orderItem, createNote);
			
			// Update cost calculation based on new data in order item
			try {
				contributionCalcService.updateCosts(order);
				em.flush(); // order final needs this to be persisted because it uses a view on this data				
			}
			catch (LogonException e) {
				LOG.error("Cost Calculation Error: " + e.getReason() + ", " + e.getMessage());
			}
			frozenTablesService.freezeOrder(order);
			
			return result = creditNote;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void processReturnQuantity(HashMap<Long, List<Long>> orderItemsByOrderIdMap,
			boolean createNote) throws LogonException {
		for (Long orderId : orderItemsByOrderIdMap.keySet()) {
			List<Long> creditNoteIdList = new ArrayList<Long>();
			try {
				for (Long orderItemId : orderItemsByOrderIdMap.get(orderId)) {
					CreditNoteEntity creditNote = creditNoteService._processReturnQuantity(orderItemId,
							createNote);
					creditNoteIdList.add(creditNote.getCreditNoteId());
					goodsMovementService.createGoodsMovementEntity(orderItemId, false, false, true);
				}
				orderService.transmitToSap(creditNoteIdList);
			}
			catch (LogonException e) {
				throw e;
			}
			catch (Exception e) {
				LOG.error(
						"Error sending credit note. Will be send again during update_orders batch.",
						e);
			}
		}

	}
	

	/**
	 * @param sortOrder
	 * @param query
	 * @param langCd
	 * @return
	 */
	protected Filter<CreditNoteProposalsEntity> createCreditNoteListFilter(SortOrder sortOrder, String query,
			String langCd) {
		// [[
		Filter<CreditNoteProposalsEntity> result = null;
		Object[][] params = {{"sortOrder", sortOrder},
							{"query", query},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createCreditNoteListFilter", params);
		}
		// ]]
		try {
			String langCode = (StringUtils.empty(langCd) ? "en" : langCd);
			
			result = new FilterImpl<CreditNoteProposalsEntity>();
			
			result.addExpression(
					Restrictions.eq(
							CreditNoteProposalsEntity.ATTR_LANG_CD, langCode));
			
			if (sortOrder != null) {
				result.addExpression(sortOrder.getOrder());
			}

			if (!query.equals("")) {
				result.addExpression(Restrictions.eq(CreditNoteProposalsEntity.ATTR_ORDER_NUMBER, Long.valueOf(query)));
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

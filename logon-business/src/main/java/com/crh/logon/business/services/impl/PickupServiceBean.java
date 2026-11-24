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
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.print.PrintException;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.dispatchers.PickupServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigurableSequencesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CustorderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PickupServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonPrintException;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.PickupTerminalEntity;
import com.crh.logon.persistence.entities.views.SoldshipFlexSrchEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;

/**
 * Used from AuftragBearbeitungForm, EntryAction.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class PickupServiceBean extends PickupServiceDispatcher implements PickupServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PickupServiceBean.class);

	private static final BigDecimal NO_TARE_WEIGHT = null;

	private static final BigDecimal NO_GROSS_WEIGHT = null;

	private static final Date NO_LOADING_START_TIME = null;

	private static final Date NO_LOADING_END_TIME = null;
	
	@EJB
	private ConfigurableSequencesServiceLocalInterface sequenceService;
	@EJB
	private PickupServiceLocalInterface pickupService;
	@EJB
	private OrderServiceLocalInterface orderService;
	@EJB
	private CustorderServiceLocalInterface customerOrderService;
	@EJB private LoadingServiceLocalInterface loadingService;
	
	private List fetchPickupShipToIds(List<Long> shiptoSapBpIdList) {
		final ReadOnlyDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();
		final Filter<ShiptoEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SELF_SERVICE_FLAG, Boolean.TRUE));
		if ((shiptoSapBpIdList != null) && (!shiptoSapBpIdList.isEmpty())) {
			filter.addExpression(Restrictions.in(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpIdList));
		}
		
		final JPAQuery<ShiptoEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_SAP_BP_ID));
		jpaQuery.setDistinct(true);

		return dao.findNRecs(0, 100, jpaQuery);
	}
	
	/**
	 * Queries shiptos which have pickup available option enabled.
	 * @return
	 */
	public List fetchPickupShipTosWithoutSarea() {
		// [[
		List<SoldshipFlexSrchEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPickupShipTosWithoutSarea");
		}
		// ]]
		try {
			//perhaps this could be replaced by a view, but unfortunately joins here are not that straightforward, thus chose rather this solution
			final ReadOnlyDao<SoldshipFlexSrchEntity> dao = getDaoProvider().getSoldshipFlexSrchEntityDao();
			Filter<SoldshipFlexSrchEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(SoldshipFlexSrchEntity.ATTR_SHIPTO_MARKED_FOR_DELETION, "0"));
			filter.addExpression(Restrictions.eq(SoldshipFlexSrchEntity.ATTR_SOLDTO_MARKED_FOR_DELETION, "0"));
			final JPAQuery<SoldshipFlexSrchEntity> jpaQuery = dao.createJPAQuery();
			jpaQuery.setFilter(filter);
			jpaQuery.addSelect(SelectExpression.simpleSelect(SoldshipFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID));
			jpaQuery.setDistinct(true);
			
			@SuppressWarnings("unchecked")
			final List<Long> shipTos = dao.findNRecs(0, 100, jpaQuery);
			if ((shipTos == null) || (shipTos.isEmpty())) {
				return result = new ArrayList<SoldshipFlexSrchEntity>();
			}
			
			//restrict to those shiptos which have pickup enabled
			@SuppressWarnings("unchecked")
			final List<Long> res = fetchPickupShipToIds(shipTos);
			
			if ((res == null) || (res.isEmpty())) {
				return result = new ArrayList<SoldshipFlexSrchEntity>();
			}
			
			filter = dao.createFilter();
			filter.addExpression(Restrictions.in(SoldshipFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID, res));
			filter.addExpression(Order.asc(SoldshipFlexSrchEntity.ATTR_SHIPTO_NAME));

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
	 * @param locale
	 * @param user
	 * @param shippingpointId
	 * @param loadingpointId
	 * @param bulkPickupCreationFlag indicates whether bulk order creation is allowed on the pickup terminal
	 * 
	 */
	public List<PickupTerminalEntity> loadOrders(Locale locale, Long shippingPointId,
			Long loadingPointId, boolean bulkPickupCreationFlag) {
		// [[
		List<PickupTerminalEntity> result = null;
		Object[][] params = {{"locale", locale},
							{"shippingPointId", shippingPointId},
							{"loadingPointId", loadingPointId},
							{"bulkPickupCreationFlag", bulkPickupCreationFlag + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrders", params);
		}
		// ]]
		try {
			final ReadOnlyDao<PickupTerminalEntity> dao = getDaoProvider().getPickupTerminalEntityDao();
			final Filter<PickupTerminalEntity> filter = dao.createFilter();
			String language = locale.getLanguage();
			if (language.isEmpty()) {
				language = Constant.DEFAULT_LANGUAGE;
			}
			Expression onlyCurrentLanguage = Restrictions.eq(PickupTerminalEntity.ATTR_LANG_CD, language);
			Expression onlyWithGivenShippingPoint = Restrictions.eq(PickupTerminalEntity.ATTR_SHIPPINGPOINT_ID, shippingPointId);
			Expression withGivenLoadingPoint = Restrictions.eq(PickupTerminalEntity.ATTR_LOADINGPOINT_ID, loadingPointId);
			Expression withoutLoadingPoint = Restrictions.isNull(PickupTerminalEntity.ATTR_LOADINGPOINT_ID);
			filter.addExpression(onlyCurrentLanguage);
			filter.addExpression(onlyWithGivenShippingPoint);
			filter.addExpression(Restrictions.or(withGivenLoadingPoint, withoutLoadingPoint));
			
			if (!bulkPickupCreationFlag) {
				Expression isBagOrder = Restrictions.in(
						PickupTerminalEntity.ATTR_MATERIAL_PACKAGE_CD,
						Constant.bagOrderMaterialPackageCds);
				filter.addExpression(isBagOrder);
			}
			filter.addExpression(Order.asc(PickupTerminalEntity.ATTR_DELIVERY_FROM_DAT));
			filter.addExpression(Order.asc(PickupTerminalEntity.ATTR_ORDER_ID));
			return result = dao.findNRecs(0, 100, filter);
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
	 * @param locale 
	 * 
	 */
	public List loadPalets() {		
		// [[
		List<SapMaterialEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadPalets");
		}
		// ]]
		try {
			final ReadOnlyDao<SapMaterialEntity> dao = getDaoProvider().getSapMaterialEntityDao();
			final Filter<SapMaterialEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(SapMaterialEntity.ATTR_MATERIAL_PACKAGE_CD, Constant.MATERIAL_PACKAGE_PALET_EMPTY));
			
			return result = dao.findNRecs(0,100, filter);
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
	 * Watch out! Called from TerminalUserFilter for each call.
	 */
	public PickupLoadingpointEntity getLoadingpointEntityForIdentifier(String ipAddress) {
		// [[
		PickupLoadingpointEntity result = null;
		Object[][] params = {{"ipAddress", ipAddress}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadingpointEntityForIdentifier", params);
		}
		// ]]
		try {
			final ReadOnlyDao<PickupLoadingpointEntity> dao = getDaoProvider().getPickupLoadingpointEntityDao();
			final Filter<PickupLoadingpointEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(PickupLoadingpointEntity.ATTR_CLIENT_IDENTIFIER, ipAddress));
			
			final PickupLoadingpointEntity findSingle = dao.findSingle(filter);
			if (findSingle == null) {
				throw new LogonException();
			}
			else{
				return result = findSingle;
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

	
	/**
	 * Creates or updates the pickup customer order, and loads it if is allowed.
	 * 
	 * @param detachedCustorder
	 *            Customer order data, detached (not managed).
	 * @param pickupLoadingpoint
	 * @param serviceProcessIndicator
	 *            if the template material (dispocard) specifies, then the SPI_CD, otherwise null
	 * @return the updated or newly created custorder
	 */
	public CustorderEntity saveAndLoadPickupCustorder(CustorderEntity detachedCustorder,
			PickupLoadingpointEntity pickupLoadingpoint, String serviceProcessIndicator) {
		// [[
		CustorderEntity result = null;
		Object[][] params = {{"detachedCustorder", detachedCustorder},
				{ "pickupLoadingpoint", pickupLoadingpoint },
				{ "serviceProcessIndicator", serviceProcessIndicator } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveAndLoadPickupCustorder", params);
		}
		// ]]
		try {
			final CustorderEntity persistedCustorder;
			boolean isPickupCustorderEffectiveLoadable =
					OrderUtils.isPickupCustorderEffectiveLoadable(detachedCustorder, pickupLoadingpoint);
			/**
			 * If it is not 'loadable', then it does not have to be done in a separate transaction
			 * and/or no lock must be acquired for sequence counter access.
			 * 
			 */
			if (!isPickupCustorderEffectiveLoadable) {
				return persistedCustorder =
						createOrUpdatePickupCustorder(detachedCustorder, serviceProcessIndicator);
			}
			ConfSeqLock lock;
			boolean isNewOrder = detachedCustorder.getCustorderId() == null;
			if (isNewOrder) {
				SapContractEntity sapContract = detachedCustorder.getSapContract();
				ShippingpointEntity shippingpoint = detachedCustorder.getShippingpoint();
				lock = sequenceService.acquireLockForContractAndShippingpoint(
						sapContract,
						shippingpoint);
			}
			else {
				List<OrderEntity> orders = detachedCustorder.getOrders();
				lock = sequenceService.acquireLockForOrders(orders);
			}
			try {
				persistedCustorder = pickupService.saveAndLoadPickupCustorder(detachedCustorder,
						pickupLoadingpoint, lock, serviceProcessIndicator);
			}
			finally {
				sequenceService.releaseLock(lock);
			}
			return result = persistedCustorder;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public CustorderEntity generateAndSendPDFsOfCustorder(CustorderEntity detachedCustorder,
			LoadingpointEntity loadingpoint)
			throws LogonPrintException {

		Long custorderId = detachedCustorder.getCustorderId();

		CustorderEntity managedCustorder = em.find(CustorderEntity.class, custorderId);
		List<OrderEntity> orders = managedCustorder.getOrders();
		orders.size();

		ConfSeqLock lock = sequenceService.acquireLockForOrders(orders);

		try {
			boolean allNecessaryDNsOfAllOrdersWerePrinted = true;
			for (OrderEntity order : orders) {
				OrderStatusBsEntity orderStatus = order.getOrderStatus();
				boolean isLoadedOrBeyond = LogonStatusUtils.isLoadedOrBeyond(orderStatus);
				if (isLoadedOrBeyond) {
					allNecessaryDNsOfAllOrdersWerePrinted &= orderService.generateAndSendDeliveryNotes(order,
							OrderServiceBean.SEND_FAX, OrderServiceBean.SEND_EMAIL,
							OrderServiceBean.PRINT_PAPER, loadingpoint);
				}
			}
			if (!allNecessaryDNsOfAllOrdersWerePrinted) {
				throw new LogonPrintException();
			}
		}
		finally {
			sequenceService.releaseLock(lock);
		}
		return managedCustorder;
	}
	
	/**
	 * 
	 * @param serviceProcessIndicator
	 *            if the template material (dispocard) specifies, then the SPI_CD, otherwise null
	 * @return updated custorder
	 */
	private CustorderEntity createOrUpdatePickupCustorder(CustorderEntity detachedCustorder,
			String serviceProcessIndicator) {
		final CustorderEntity custorder;
		/**
		 * Payer is set when the order is created in the Terminal application 
		 * even if the dispocard did not specified any. Therefore we have to unset the payer
		 * before persisting the new CustorderEntity if payer is ignored.
		 */
		customerOrderService.cleanUpPayerIfInvalid(detachedCustorder);
		orderService.setDefaultReturnReasonForPallets(detachedCustorder.getOrders());
		
		boolean isNewOrder = detachedCustorder.getCustorderId() == null;
		if (isNewOrder) {
			custorder = customerOrderService.createCustomerOrder(detachedCustorder,
					true, true, true, null, Constant.WEIGH_SYSTEM_LP_LOGON, false, serviceProcessIndicator, true);
		}
		else {
			String noteDelivery = detachedCustorder.getNoteDelivery();
			for (OrderEntity order : detachedCustorder.getOrders()) {
				order.setNoteDelivery(noteDelivery);
			}
			custorder = customerOrderService.mergeCustomerOrder(detachedCustorder);
		}
		
		return custorder;
	}
	
	/**
	 * Loads the pickup customer order if at a 'loadable' pickup loading point or simply prints the
	 * latest existing delivery note of each order otherwise.
	 * @param custorderId
	 * @param pickupLoadingpoint
	 * @throws PrintException 
	 */
	public void loadAndPrintPickupCustorder(Long custorderId,
			PickupLoadingpointEntity pickupLoadingpoint) throws LogonPrintException {
		// [[
		Object[][] params = {{"custorderId", custorderId},
							{"pickupLoadingpoint", pickupLoadingpoint}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadAndPrintPickupCustorder", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			ReadWriteDao<CustorderEntity> custorderEntityDao = daoProvider.getCustorderEntityDao();
			final CustorderEntity custorder = 
					custorderEntityDao.findById(custorderId);
			List<OrderEntity> custorderOrders = custorder.getOrders();
			LoadingpointEntity loadingpoint = pickupLoadingpoint.getLoadingpoint();
			if (OrderUtils.isPickupCustorderEffectiveLoadable(custorder, pickupLoadingpoint)) {
				
				ConfSeqLock lock = null;
				try {
					// before serializing it, we must initialize
					custorder.initialize();
					lock = sequenceService.acquireLockForOrders(custorderOrders);
					pickupService.loadPickupCustorder(custorder, pickupLoadingpoint, lock);
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
				final CustorderEntity updatedCustorder = custorderEntityDao.findById(custorderId);
				final List<OrderEntity> orders = updatedCustorder.getOrders();
				boolean allNecessaryDNsOfAllOrdersWerePrinted = true;
				for (OrderEntity order : orders) {
					OrderStatusBsEntity orderStatus = order.getOrderStatus();
					boolean loadedOrBeyond = LogonStatusUtils.isLoadedOrBeyond(orderStatus);
					if (loadedOrBeyond) {
						// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
						allNecessaryDNsOfAllOrdersWerePrinted &=
								orderService.generateAndSendDeliveryNotes(order, true, true, true, loadingpoint);
					}
				}
				if (!allNecessaryDNsOfAllOrdersWerePrinted) {
					throw new LogonPrintException();
				}
			}
			else {
				
				final List<OrderEntity> orders = custorderOrders;
				for (OrderEntity order : orders) {
					Long orderId = order.getOrderId();
					List<OrderOutputdocEntity> docs = 
							orderService.loadDeliveryNoteList(orderId);
					Long orderNumber = order.getOrderNumber();
					if (docs.isEmpty()) {
						LOG.error("No tlgnc_order_outputdoc entry found for " 
								+ "order id " + orderId
								+ ", order number " + orderNumber
								+ ". Could not send delivery note.");
					}
					else {
						OrderOutputdocEntity doc = docs.get(0);
						OutputdocEntity pdfOutputdoc = doc.getPdfOutputdoc();
						try {
							orderService.sendDeliveryNoteToPrinter(order, pdfOutputdoc, loadingpoint);
						}
						catch (PrintException printException) {
							String errorMessage = "Printing failed for order id " + orderId +
									", order number: " + orderNumber + ".";
							LOG.error(errorMessage, printException);
							throw new LogonPrintException();
						}
					}
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
	
	/**
	 * @param serviceProcessIndicator if the template material (dispocard) specifies, then the SPI_CD, otherwise null
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public CustorderEntity saveAndLoadPickupCustorder(CustorderEntity detachedCustorder,
			PickupLoadingpointEntity pickupLoadingpoint, ConfSeqLock lock, String serviceProcessIndicator) {
		final CustorderEntity custorder = createOrUpdatePickupCustorder(detachedCustorder,serviceProcessIndicator);
		List<OrderEntity> orders = custorder.getOrders();
		LoadingpointEntity loadingpoint = pickupLoadingpoint.getLoadingpoint();
		loadingService.setOrdersToLoaded(orders, loadingpoint, NO_TARE_WEIGHT, NO_GROSS_WEIGHT,
				NO_LOADING_START_TIME, NO_LOADING_END_TIME,
				EventSource.LOGON, lock);
		return custorder;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public void loadPickupCustorder(CustorderEntity detachedCustorder,
			PickupLoadingpointEntity pickupLoadingpoint, ConfSeqLock lock) {
		Long custorderId = detachedCustorder.getCustorderId();
		CustorderEntity managedCustomerOrder = em.find(CustorderEntity.class, custorderId);
		List<OrderEntity> orders = managedCustomerOrder.getOrders();
		LoadingpointEntity loadingpoint = pickupLoadingpoint.getLoadingpoint();

		loadingService.setOrdersToLoaded(orders, loadingpoint, NO_TARE_WEIGHT,
				NO_GROSS_WEIGHT,
				NO_LOADING_START_TIME, NO_LOADING_END_TIME,
				EventSource.LOGON, lock);
	}
}

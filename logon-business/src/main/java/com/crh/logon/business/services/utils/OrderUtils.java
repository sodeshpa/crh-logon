/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crh.logon.business.services.interfaces.DeliverySettingService;
import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.business.stores.sap.AlternativeUnitOfMeasurementInterface;
import org.apache.commons.beanutils.BeanUtils;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.Ostermiller.util.RandPass;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.GlobalTransportBackgroundData;
import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingBasicDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.PoolDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.PoolTxPK;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxPK;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxPK;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * Placeholder for misc utility methods for Orders.
 * 
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class OrderUtils {
	
	public static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderUtils.class);
	
	public static final char[] NUMBERS_USED_IN_ORDER_VALIDATION_CODE = {'0','1','2','3','4','5','6','7','8','9'};

	private static final SapBpEntity BILLTO_NOT_IN_LIST = null;

	private static final FireqMaterialMixdesignEntity MIX_DESIGN_NOT_IN_LIST = null;

	private static final SapBpEntity BUSINESS_PARTNER_NOT_IN_LIST = null;

	private static final PoolEntity POOL_NOT_IN_LIST = null;

	private static final PoolTxEntity POOL_TEXT_NOT_IN_LIST = null;

	private static final ExecutingCarrierDTO EXECUTING_CARRIER_NOT_IN_LIST = null;
	
	private static final CommercialCarrierDTO COMMERCIAL_CARRIER_NOT_IN_LIST = null;

	private static final PlantBsEntity PLANT_NOT_IN_LIST = null;

	private static final ShiptoSiloEntity SHIPTO_NOT_IN_LIST = null;

	private static final LoadingpointEntity LOADING_POINT_NOT_IN_LIST = null;

	private static final ShippingpointEntity SHIPPING_POINT_NOT_IN_LIST = null;

	private static final DeliveryTypeBsEntity DELIVERY_TYPE_NOT_IN_LIST = null;

	private static final UomBsEntity UNIT_OF_MEASURE_NOT_IN_LIST = null;
	
	private static Set<Long> atDriverStatuses = new HashSet<Long>();

	static {
		atDriverStatuses.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);
		atDriverStatuses.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK);
		atDriverStatuses.add(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
		atDriverStatuses.add(Constant.ORDER_STATUS_LOADING);
		atDriverStatuses.add(Constant.ORDER_STATUS_LOADED);
		atDriverStatuses.add(Constant.ORDER_STATUS_SHIPPING);
		atDriverStatuses.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);
	}
	
	/**
	 * Determines whether the payer should be ignored and left empty during the whole workflow.
	 * See LOGON-6762 for details.
	 * 
	 * @param sapContractEntity
	 * @return true if both of the auto payer-determination flags are ON.
	 */
	public static boolean isPayerIgnored(SapContractEntity sapContractEntity) {
		// TLGNC_ORDER.SAP_CONTRACT_ID is nullable!
		if (sapContractEntity == null) {
			return false;
		}
		SapContractTypeBsEntity sapContractType = sapContractEntity.getSapContractType();
		SareaEntity sarea = sapContractEntity.getSarea();
		return sapContractType.getAutoPayerDetermFlag() && sarea.getAutoPayerDetermFlag();
	}

	public static Calendar parseDeliveryDateTime(
			TimeZone timeZone,
			String fmtDeliveryDate, String fmtDeliveryTime,
			Long openMin,
			Long openMinDefault) {
		Date deliveryDate;

		try {
			deliveryDate = timeZone.parseDayMonthYearHourMinute(fmtDeliveryDate, fmtDeliveryTime);
		}
		catch (Exception exception) {
			LOG.error(String.format("Parsing '%s %s' failed. Defaulting to tomorrow",
					fmtDeliveryDate, fmtDeliveryTime));

			Date tomorrow = timeZone.tomorrow();
			if (openMin == null) {
				openMin = openMinDefault;
			}

			Calendar calendar = timeZone.asCalendar();
			calendar.setTime(tomorrow);
			calendar.add(Calendar.MINUTE, openMin.intValue());

			deliveryDate = calendar.getTime();
		}

		Calendar result = timeZone.asCalendar();
		result.setTime(deliveryDate);
		return result;
	}
	
	/**
	 * Set the contract references of the order.
	 * @param contractItem
	 * @param altUom the {@link SapMaterialAltUomEntity} if UoM is alternative. null otherwise
	 * @param order, sapContract will be set
	 * @param orderItem, rate, currency, contractItem and sapMaterial will be set.
	 */
	public static void setContractDependentAttributes(CustorderEntity custorderEntity,
			CustorderItemEntity custorderItemEntity, SapContractItemEntity contractItem) {
		if (contractItem != null) {

			SapContractEntity contract = contractItem.getSapContract();
			SapContractEntity contractFromOrder = custorderEntity.getSapContract();

			if (contract != null && contractFromOrder != null
					&& !contract.getSapContractId().equals(contractFromOrder.getSapContractId())) {
				
				throw new LogonException("Invalid contract set on order", Reason.RES_IMPL_BUG);
			}

			BigDecimal rate = calculateRate(contractItem);
			custorderItemEntity.setRate(rate);

			CurrencyBsEntity currency = contract.getCurrency();

			if (currency == null) {
				throw new LogonException("Contract (no: " + contract.getSapContractNo()
						+ ") was sent with no currency", Reason.RES_IMPL_BUG);
			}
			
			custorderItemEntity.setCurrency(currency);
			
			custorderEntity.setSapContract(contract);
			custorderItemEntity.setSapContractItem(contractItem);
		}
		else {
			/*
			 * This may only happen when a Service item is added to the order, which is never the first
			 * item.
			 */
			custorderItemEntity.setRate(null);

			/*
			 * Currency is a required field, but not known for Service materials. Hard-coding CHF is
			 * not okay, because SalesDocumentDTO.currency in the SAP outgoing interface would
			 * require every material to be set to the same currency. ==> Since rate is null, no
			 * conversion needed, so we can set the currency to be identical to the one of the first
			 * item.
			 */

			if (LogonPackageHandlingUtil.isServiceOnlyCustomerOrder(custorderEntity)) {
				final CustorderItemEntity firstServiceItem = LogonUtil.getFirstServiceCustorderItem(custorderEntity.getCustorderItems());
				custorderItemEntity.setCurrency(firstServiceItem.getCurrency());
			}
			else {
				final CustorderItemEntity firstMaterialItem = LogonUtil.getFirstMaterialCustorderItem(custorderEntity.getCustorderItems());
				custorderItemEntity.setCurrency(firstMaterialItem.getCurrency());
			}
		}
	}
	
	/**
	 * Returns the rate calculated from the sap contract item.
	 * @param contractItem
	 * @return
	 */
	public static BigDecimal calculateRate(SapContractItemEntity contractItem) {
		/*
		 * Calculate rate (material price), which is for now simply netValue / quantity. As Urs wrote:
		 * "The correct details could be more complicated, and involve UOM conversion in order taking."
		 * Leave rate as null if input data is not available or invalid.
		 */
		BigDecimal rate = null;
		BigDecimal netValue = contractItem.getNetValue();
		BigDecimal targetQuantity = contractItem.getTargetQuantity();
		String targetQuantityUomCd = contractItem.getTargetQuantityUom() != null ? contractItem.getTargetQuantityUom().getUomCd()
				: null;

		if (netValue != null && targetQuantity != null && targetQuantityUomCd != null) {
			
			if (targetQuantity.compareTo(BigDecimal.ZERO) != 0) {
				rate = netValue.divide(targetQuantity, Constant.SCALE, BigDecimal.ROUND_HALF_UP);
			}
		}

		return rate;
	}
	
	/**
	 * Append the corresponding note fields from the given ShiptoSareaRestrEntity to the notes of OrderEntity
	 * 
	 * @param order Must not be null
	 * @param shiptoSareaRestr Must not be null
	 */
	public static void appendToOrderNotesFromShiptoSareaRestr(OrderEntity order, ShiptoSareaRestrEntity shiptoSareaRestr) {
		// get the strings and trim the note if needed
		String noteDelivery = StringUtils.appendWithNewLine(order.getNoteDelivery(),
				shiptoSareaRestr.getNoteDelivery());
		noteDelivery = trimIfNeededWithErrorLogMessage(order, noteDelivery,
				OrderEntity.ATTR_NOTE_DELIVERY, OrderEntity.DBSIZE_NOTE_DELIVERY);
		String noteDispatching = StringUtils.appendWithNewLine(order.getNoteDispatching(),
				shiptoSareaRestr.getNoteDispatching());
		noteDispatching = trimIfNeededWithErrorLogMessage(order, noteDispatching,
				OrderEntity.ATTR_NOTE_DISPATCHING, OrderEntity.DBSIZE_NOTE_DISPATCHING);
		String noteOrderMgmt = StringUtils.appendWithNewLine(order.getNoteOrderMgmt(),
				shiptoSareaRestr.getNoteOrderMgmt());
		noteOrderMgmt = trimIfNeededWithErrorLogMessage(order, noteOrderMgmt,
				OrderEntity.ATTR_NOTE_ORDER_MGMT, OrderEntity.DBSIZE_NOTE_ORDER_MGMT);
		String noteRailwayDisp = StringUtils.appendWithNewLine(order.getNoteRailwayDisp(),
				shiptoSareaRestr.getNoteRailwayDisp());
		noteRailwayDisp = trimIfNeededWithErrorLogMessage(order, noteRailwayDisp,
				OrderEntity.ATTR_NOTE_RAILWAY_DISP, OrderEntity.DBSIZE_NOTE_RAILWAY_DISP);
		String noteTruckDriver = StringUtils.appendWithNewLine(order.getNoteTruckDriver(),
				shiptoSareaRestr.getNoteTruckDriver());
		noteTruckDriver = trimIfNeededWithErrorLogMessage(order, noteTruckDriver,
				OrderEntity.ATTR_NOTE_TRUCK_DRIVER, OrderEntity.DBSIZE_NOTE_TRUCK_DRIVER);
		// set the notes
		order.setNoteDelivery(noteDelivery);
		order.setNoteDispatching(noteDispatching);
		order.setNoteOrderMgmt(noteOrderMgmt);
		order.setNoteRailwayDisp(noteRailwayDisp);
		order.setNoteTruckDriver(noteTruckDriver);
	}
	
	/**
	 * Trim input note to fit into the database</br>
	 * Additional fields are required for logging if the size actually exceeds of the fields.
	 * 
	 * @param order - the order which note fields are to be inserted
	 * @param note - the note
	 * @param fieldName - <i>for logging purposes</i> field name in database
	 * @param maxLength - <i>for logging purposes</i> max field length
	 * @return the note with the maximum length of maxLength
	 */
	public static String trimIfNeededWithErrorLogMessage(OrderEntity order, String note, String fieldName, long maxLength) {
		if (note.length() > maxLength) {
			// log the error
			LOG.error(MessageFormat.format("Order ID: {0} Input ( {1} ) for field {2} exceeds the max length of {3}. Input will be trimmed"
					, order.getOrderId(), note, fieldName, maxLength));
			// trim the string
			note = note.substring(0, (int) maxLength);
		}
		return note;
	}
	
	/**
	 * Creates a list of {@link SplittedOrderData} based on the parameter {@link CustorderEntity}
	 * in <b>bulk</b> mode.
	 * 
	 * <b>Please note this method will alter the {@link CustorderEntity}s order list with the splitted list!</b>
	 * 
	 * <p>See: LRQ 070.2: 2.2 Overview of Should solution</p>
	 * 
	 * <ul>
	 * <li>OrderEntity 		<1 .. 1> OrderItem</li>
	 * <li>CustorderEntity 	<1 .. 1> CustorderItemEntity</li>
	 * <li>CustorderEntity 	<1 .. N> OrderEntity</li>
	 * </ul>
	 * 
	 * @param custorderEntity - the entity on which the generation depends
	 * @param shipTo - will be set on every @{OrderItem}
	 * @param countOfDeliveries - the returned list size will be equal to this
	 * @param qtyPerDelivery - will be set on every @{OrderItem}
	 * @return The size of the list equals to the <i>countOfDeliveries</i>
	 * @throws NullPointerException if the @{link CustorderEntity} has orders preset but the order does not know the @{link CustorderEntity}
	 */
	public static List<SplittedOrderData> createSplittedOrdersBulk(CustorderEntity custorderEntity,
			ShiptoEntity shipTo, int countOfDeliveries, BigDecimal qtyPerDelivery) {
		List<OrderEntity> createdOrderEntitites = new ArrayList<OrderEntity>();
		List<SplittedOrderData> ordersListFromCustorders = new ArrayList<SplittedOrderData>();

		OrderEntity originalOrder;

		if (custorderEntity.getOrders() == null || custorderEntity.getOrders().isEmpty()) {
			originalOrder = null;
		}
		else {
			originalOrder = custorderEntity.getOrders().get(0);
		}

		for (int i = 0; i < countOfDeliveries; i++) {

			// TODO: This should populated based on the CustorderEntity 
			OrderEntity order;

			// If the custorder does not have the orders prefilled

			if (originalOrder == null) {

				order = new OrderEntity();
				order.setArchivedFlag(false);
				order.setDeliveryFromDat(new Date());
				order.setDeliveryToDat(new Date());
				order.setCustorder(custorderEntity);

				// TODO: This should populated based on the CustorderItemEntity				
				OrderItemEntity orderItem = new OrderItemEntity();

				// Fill-in default values that don't come from the New Order data (order creation screen)
				orderItem.setOrderItemId(null);
				orderItem.setOrder(order);
				orderItem.setOrderedQuantity(qtyPerDelivery);

				List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
				orderItems.add(orderItem);
				order.setOrderItems(orderItems);

				createdOrderEntitites.add(order);

			}
			else {

				OrderEntity orderClone;

				try {
					orderClone = (OrderEntity) BeanUtils.cloneBean(originalOrder);
				}
				catch (Exception e) {
					throw new RuntimeException("Exception occurred during clone of OrderEntity", e);
				}

				orderClone.setOrderId(null);
				orderClone.setCustorder(custorderEntity);

				order = orderClone;
				OrderItemEntity orderItemEntity = order.getOrderItems().get(0);
				orderItemEntity.setOrderedQuantity(qtyPerDelivery);

				createdOrderEntitites.add(orderClone);

			}

			custorderEntity.setOrders(createdOrderEntitites);

			// Create SplittedOrderData from the OrderEntity
			Boolean exactCustorderQtyFlag = CustorderUtils.getExactCustorderQty(custorderEntity);
			SplittedOrderData.DeliveryType deliveryType = LogonUtil.getDeliveryType(order);
			SplittedOrderData splittedOrderData = new SplittedOrderData(order, shipTo,
					deliveryType, exactCustorderQtyFlag, i);
			ordersListFromCustorders.add(splittedOrderData);

		}
		return ordersListFromCustorders;
	}
	
	/**
	 * Creates a list of {@link SplittedOrderData} based on the parameter {@link CustorderEntity}
	 * in <b>bag</b> mode.
	 * 
	 * <b>Please note this method will alter the {@link CustorderEntity}s order list with the splitted list!</b>
	 * 
	 * <p>See: LRQ 070.2: 2.2 Overview of Should solution</p>
	 * 
	 * <ul>
	 * <li>OrderEntity 		<1 .. N> OrderItem</li>
	 * <li>CustorderEntity 	<1 .. N> CustorderItemEntity</li>
	 * <li>CustorderEntity 	<1 .. 1> OrderEntity</li>
	 * </ul>
	 * 
	 * @param custorderEntity - the entity on which the generation depends 
	 * @param shipTo - will be set on every @{OrderItem}
	 * @param qtyPerDelivery - will be set on every @{OrderItem}
	 * @return The size of the list is one
	 * @throws NullPointerException if the @{link CustorderEntity} has orders preset but the order does not know the @{link CustorderEntity}
	 */
	public static List<SplittedOrderData> createSplittedOrdersBag(CustorderEntity custorderEntity,
			ShiptoEntity shipTo, BigDecimal qtyPerDelivery) {
		OrderEntity order = null;

		if (custorderEntity.getOrders() == null || custorderEntity.getOrders().isEmpty()) {
			order = null;
		}
		else {
			order = custorderEntity.getOrders().get(0);
		}

		if (order == null) {

			List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();

			// TODO: This should populated based on the CustorderEntity 
			order = new OrderEntity();
			order.setArchivedFlag(false);
			order.setDeliveryFromDat(new Date());
			order.setDeliveryToDat(new Date());

			for (int i = 0; i < custorderEntity.getCustorderItems().size(); i++) {

				// TODO: This should populated based on the CustorderItemEntity				
				OrderItemEntity orderItem = new OrderItemEntity();

				// Fill-in default values that don't come from the New Order data (order creation screen)
				orderItem.setOrderItemId(null);
				orderItem.setOrder(order);
				orderItem.setOrderedQuantity(qtyPerDelivery);

				orderItems.add(orderItem);

			}

			order.setOrderItems(orderItems);
			order.setCustorder(custorderEntity);

		}
		else {

			List<OrderItemEntity> orderItems = order.getOrderItems();
			for (OrderItemEntity orderItemEntity : orderItems) {
				orderItemEntity.setOrderedQuantity(qtyPerDelivery);
			}

		}

		// Only one entry in case of bag
		Boolean exactCustorderQtyFlag = CustorderUtils.getExactCustorderQty(custorderEntity);
		SplittedOrderData.DeliveryType deliveryType = LogonUtil.getDeliveryType(order);
		SplittedOrderData splittedOrderData = new SplittedOrderData(order, shipTo, deliveryType,
				exactCustorderQtyFlag, 0);

		return Arrays.asList(new SplittedOrderData[] { splittedOrderData });
	}
	
	/**
	 * LOGON-5117
	 * This function puts the id of the order to the proper list based on the rules.
	 * Please also see {@link OrderUtilsTest}
	 * @param order
	 * @param deleteOrderNumberList
	 * @param manualDeallocateOrderNumberList
	 */
	public static void checkOrderStateAndFillLists(OrderEntity order, Map<Long, OrderEntity> deleteOrderMap, List<Long> manualDeallocateOrderNumberList) {
		Long statusCd = order.getOrderStatus().getOrderStatusCd();
		if (statusCd.equals(Constant.ORDER_STATUS_NEW) ||
				statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) ||
				statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) ||
				statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER) ||
				statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK)) {
			deleteOrderMap.put(order.getOrderId(), order);
		}
		// Handling special cases
		else if ((statusCd.equals(Constant.ORDER_STATUS_DISPATCHED) || 
				statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER))) {
			if (order.getExecCarrier() == null) {
				// Planning trucks
				deleteOrderMap.put(order.getOrderId(), order);
			}
			else if (order.getCustorder() != null &&
					order.getCustorder().getOrderCreationType().getOrderCreationTypeCd() == 
						Constant.ORDER_CREATION_TYPE_EXTERNAL_SYSTEM) {
				// LDS created the customer order
				deleteOrderMap.put(order.getOrderId(), order);
			}
			else {
				manualDeallocateOrderNumberList.add(order.getOrderNumber());
			}
		}
		else if (statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL) ||
				statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK) ||
				statusCd.equals(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT) ||
				statusCd.equals(Constant.ORDER_STATUS_LOADING) ||
				statusCd.equals(Constant.ORDER_STATUS_LOADED) ||
				statusCd.equals(Constant.ORDER_STATUS_SHIPPING) ||
				statusCd.equals(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) ||
				statusCd.equals(Constant.ORDER_STATUS_UNLOADING)) {
			manualDeallocateOrderNumberList.add(order.getOrderNumber());
		}
	}
	
	/**
	 * Returns true, if endLoad can be called on the order, which means that it is at least
	 * 'transmitted to driver', but 'up to arrived at unloading point'.
	 * @param order
	 * @return
	 */
	public static boolean isInEndloadableState(OrderEntity order) {
		boolean result = false;
		if (atDriverStatuses.contains(order.getOrderStatus().getOrderStatusCd())) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 1.) Loads Custorder
	 * 2.) Loops through the orders of the Custorder and collects the ids of the orders to the lists
	 * @param custorderEntity - the {@link CustorderEntity}
	 * @param deleteOrderNumberList - order ids that has to be automatically deleted will go here
	 * @param manualDeallocateOrderNumberList - order ids that has to be manually deallocated will go here
	 */
	public static void fillOrderNumberLists(CustorderEntity custorderEntity, Map<Long, OrderEntity> deleteOrderMap,
			List<Long> manualDeallocateOrderNumberList) {
		List<OrderEntity> orders = custorderEntity.getOrders();

		for (OrderEntity order : orders) {
			OrderUtils.checkOrderStateAndFillLists(order, deleteOrderMap, manualDeallocateOrderNumberList);
		}
	}
	
	/**
	 * Sorts a list or order items by the order item number from the lowest to the highest.
	 * @param orderItemList
	 * @return sorted orderItemList
	 */
	public static List<OrderItemEntity> sortOrderItems(List<OrderItemEntity> orderItemList){
		Collections.sort(orderItemList, new Comparator<OrderItemEntity>() {
			public int compare(OrderItemEntity o1, OrderItemEntity o2) {
				return o1.getOrderItemNo().compareTo(o2.getOrderItemNo());
			}
		});
		return orderItemList;
	}
	
	/**
	 * Returns the order items of the given order sorted by the order item number from lowest to highest.
	 * @param order
	 * @return sorted order items
	 */
	public static List<OrderItemEntity> getSortedOrderItems(OrderEntity order) {
		List<OrderItemEntity> orderItems = order.getOrderItems();
		sortOrderItems(orderItems);
		return orderItems;
	}
	
	/**
	 * Returns a {@link DeliverySettingDTO} attribute containing all entity references which are retrieved
	 * from the valid lists of the original
	 * @param inputDeliverySettingParameters
	 * @param salesAreaMap
	 * @param originalDeliverySetting 
	 * @param isCashSales
	 * @param truckAdminService 
	 * @param truckAdminService TODO
	 * @param bestellungService TODO
	 * @param poolAdminService TODO
	 * @param compositeIdentifier 
	 * @return
	 */
	public static DeliverySettingDTO getDeliverySettingFromBasicDTO(
			DeliverySettingBasicDTO inputDeliverySettingParameters,

			DeliverySettingSalesAreaMap salesAreaMap, DeliverySettingDTO originalDeliverySetting, boolean isCashSales, TruckAdminService truckAdminService, BestellungService bestellungService, DeliverySettingService deliverySettingService) {
		//customer order related type attributes
		BigDecimal quantity = null;
		BigDecimal perSingleDeliveryOrderQuantity = null;
		UomBsEntity perSingleDeliveryOrderUnitOfMeasure = null;
		Double deliveryFrequencyEveryTime = null;
		String deliveryFrequencyEveryTimeUnitOfMeasureCode = null;
		BigDecimal deliveryFrequencyQuantityPerHour = null;
		UomBsEntity deliveryFrequencyQuantityPerHourUnitOfMeasure = null;
		Double deliveryTimeTolerance = null;
		String deliveryTimeToleranceUnitOfMeasureCode = null;
		
		Boolean exactCustomerOrderQuantityFlag = null;
		Boolean exactSingleDeliveryOrderQuantityFlag = null;
		
		//basic attributes
		DeliveryTypeBsEntity deliveryType = null;
		ShippingpointEntity shippingPoint = null;
		LoadingpointEntity loadingpoint = null;
		ShiptoSiloEntity shipToSilo = null;
		PlantBsEntity supplyPlant = null;
		PlantBsEntity productionPlant = null;
		PoolEntity pool = null;
		SapBpEntity payerSapBusinessPartner = null;
		SapBpEntity billtoSapBusinessPartner = null;
		FireqMaterialMixdesignEntity mixdesign = null;
		UomBsEntity unitOfMeasure = null;
		
		CommercialCarrierDTO commercialCarrierDTO = null;
		ExecutingCarrierDTO executingCarrierDTO = null;
		
		//restriction only fields
		Boolean autoCarrierAssignmentFlag = null;
		Boolean autoPoolAssignmentFlag = null;
		Boolean autoReleaseFlag = null;

		//special process indicators
		Boolean sapSuppressMinChargeFlag = null;
		Boolean onHoldOrderAndLockedFlag = null;
		Boolean freeDeliveryFlag = null;
		Boolean releaseSapFlag = null;
		Boolean openCustomerOrderFlag = null;
		
		CarrierEntity commercialCarrier = null;
		CarrierEntity executingCarrier = null;

		final DeliverySettingDTO.DeliverySettingLangDepData additionalData = originalDeliverySetting.getAdditionalData();
		final ShiptoSareaEntity shipToSalesArea = originalDeliverySetting.getShiptoSarea();
		final SapMaterialEntity sapMaterial = originalDeliverySetting.getSapMaterial();
		final SapContractItemEntity sapContractItem = originalDeliverySetting.getSapContractItem();
		
		final DeliveryOptionConfigurationDTO deliverySettingOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(inputDeliverySettingParameters, salesAreaMap);
	
		autoCarrierAssignmentFlag = inputDeliverySettingParameters.getAutoCarrierAssignmentFlag();
		autoPoolAssignmentFlag = inputDeliverySettingParameters.getAutoPoolAssignmentFlag();
		autoReleaseFlag = inputDeliverySettingParameters.getAutoReleaseFlag();
		openCustomerOrderFlag = inputDeliverySettingParameters.getOpenCustorderFlag();
		sapSuppressMinChargeFlag = inputDeliverySettingParameters.getSapSuppressMinChargeFlag();
		onHoldOrderAndLockedFlag = inputDeliverySettingParameters.getOnHoldOrderAndLockedFlag();
		freeDeliveryFlag = inputDeliverySettingParameters.getFreeDeliveryFlag();
		releaseSapFlag = inputDeliverySettingParameters.getReleaseSapFlag();
		exactCustomerOrderQuantityFlag = inputDeliverySettingParameters.getExactCustorderQtyFlag();
		exactSingleDeliveryOrderQuantityFlag = inputDeliverySettingParameters.getExactSdorderQtyFlag();

		quantity = inputDeliverySettingParameters.getQuantity();
		perSingleDeliveryOrderQuantity = inputDeliverySettingParameters.getPerSdorderQuantity();

		List<UomTxEntity> unitOfMeasuresOfDeliverySetting = deliverySettingOptionConfiguration.getUomList();
		String perSingleDeliveryOrderUnitOfMeasureCode = inputDeliverySettingParameters.getPerSdorderUomCd();
		if (perSingleDeliveryOrderUnitOfMeasureCode != null) {
			perSingleDeliveryOrderUnitOfMeasure = findUnitOfMeasureByCode(
					unitOfMeasuresOfDeliverySetting,
					perSingleDeliveryOrderUnitOfMeasureCode);
		}

		// Selected vehicle type codes on UI
		List<Long> selectedVehichleTypeCodes = inputDeliverySettingParameters.getVehicleTypeCds();
		// TruckWagonTypeWrappers available from configuration
		List<TruckWagonTypeWrapper> vehicleTypes = deliverySettingOptionConfiguration.getVehicleTypeWrapperList();

		// Create list of selected truckWagonTypeWrappers based on the set of codes.
		List<TruckWagonTypeWrapper> selectedTruckWagonTypes;
		if (selectedVehichleTypeCodes != null) {
			selectedTruckWagonTypes = filterVehicleTypeWrapperByTypeCodes(vehicleTypes, selectedVehichleTypeCodes);
		}
		else {
			selectedTruckWagonTypes = new LinkedList<TruckWagonTypeWrapper>();
		}

		deliveryTimeTolerance = inputDeliverySettingParameters.getDeliveryTimeTolerance();
		deliveryTimeToleranceUnitOfMeasureCode = inputDeliverySettingParameters.getDeliveryTimeToleranceUomCd();
		deliveryFrequencyQuantityPerHour = inputDeliverySettingParameters.getDeliveryFreqQtyperhre();

		String deliveryFrequencyQuantityPerHourUnitOfMeasureCode = inputDeliverySettingParameters.getDeliveryFreqQtyperhreUomCd();
		if (deliveryFrequencyQuantityPerHourUnitOfMeasureCode != null) {
			deliveryFrequencyQuantityPerHourUnitOfMeasure = findUnitOfMeasureByCode(
					unitOfMeasuresOfDeliverySetting,
					deliveryFrequencyQuantityPerHourUnitOfMeasureCode);
		}

		deliveryFrequencyEveryTime = inputDeliverySettingParameters.getDeliveryFreqEveryTime();
		deliveryFrequencyEveryTimeUnitOfMeasureCode = inputDeliverySettingParameters.getDeliveryFreqEveryTimeUomCd();

		Long deliveryTypeCode = inputDeliverySettingParameters.getDeliveryTypeCd();
		if (deliveryTypeCode != null) {
			List<DeliveryTypeTxEntity> deliveryTypeList = deliverySettingOptionConfiguration.getDeliveryTypeList();
			deliveryType = findDeliveryTypeByCode(deliveryTypeCode, deliveryTypeList);
		}

		Long shippingPointIdentifier = inputDeliverySettingParameters.getShippingpointId();
		if (shippingPointIdentifier != null ) {				
			List<ShippingpointDTO> shippingPoints = deliverySettingOptionConfiguration.getShippingpoinList();
			shippingPoint = findShippingPointByIdentifier(shippingPointIdentifier, shippingPoints, bestellungService);
		}

		Long loadingPointIdentifier = inputDeliverySettingParameters.getLoadingpointId();
		if (loadingPointIdentifier != null) {
			loadingpoint = findLoadingPointByIdentifier(deliverySettingOptionConfiguration,
					loadingPointIdentifier, bestellungService);
		}
		

		Long shiptoSiloIdentifier = inputDeliverySettingParameters.getShiptoSiloId();
		if (shiptoSiloIdentifier != null) {
			List<ShiptoSiloEntity> silos = deliverySettingOptionConfiguration.getSiloList();
			shipToSilo = findShiptoSiloByIdentifier(shiptoSiloIdentifier, silos);
		}

		String supplyPlantCode = inputDeliverySettingParameters.getSupplyPlantCd();
		if (supplyPlantCode != null) {
			List<SupplyPlantDTO> supplyPlantList = deliverySettingOptionConfiguration.getSupplyPlantList();
			supplyPlant = findSupplyPlantByCode(supplyPlantCode, supplyPlantList, bestellungService);
			if (supplyPlant == null ) {
				shippingPoint = null;
				loadingpoint = null;
			}
		}

		String productionPlantCode = inputDeliverySettingParameters.getProductionPlantCd();
		if (productionPlantCode != null) {
			List<PlantDTO> productionPlantList = deliverySettingOptionConfiguration.getProdPlantList();
			productionPlant = findProductionPlantByCode(productionPlantCode, productionPlantList, bestellungService);
		}
		
		Long commercialCarrierIdentifier = inputDeliverySettingParameters.getCommercialCarrierId();
		if (commercialCarrierIdentifier != null) {
			List<CommercialCarrierDTO> commercialCarriers = deliverySettingOptionConfiguration.getCommercialCarrierList();
			commercialCarrierDTO = findCommercialCarrierByIdentifier(commercialCarrierIdentifier,
					commercialCarriers);
			if ( commercialCarrierDTO != null ) {
				commercialCarrier = truckAdminService.fetchCarrier(commercialCarrierDTO.getCarrierId());
			}
		}
		
		Long executingCarrierIdentifier = inputDeliverySettingParameters.getExecutingCarrierId();
		if (executingCarrierIdentifier != null) {
			List<ExecutingCarrierDTO> executingCarriers = deliverySettingOptionConfiguration.getExecutingCarrierList();
			executingCarrierDTO = findExecutingCarrierByIdentifier(executingCarrierIdentifier,
					executingCarriers);
			if ( executingCarrierDTO != null ) {
				executingCarrier = truckAdminService.fetchCarrier(executingCarrierDTO.getCarrierId());
			}
		}
		
		Long poolIdentifier = inputDeliverySettingParameters.getPoolId();
		if (poolIdentifier != null) {
			PoolTxEntity poolText = null;
			List<PoolTxEntity> poolList = deliverySettingOptionConfiguration.getPoolList();
			poolText = findPoolTextByIdentifier(poolIdentifier, poolList);
			if (poolText != null) {
				DeliverySettingShiptoMap salesAreaShipToSettings = salesAreaMap.getShiptoSettings();
				GlobalTransportBackgroundData globalTransportData = salesAreaShipToSettings.getGlobalTransportData();
				List<PoolDTO> poolsFromGlobalTransportData = globalTransportData.getPools();
				pool = findPoolByIdentifier(poolIdentifier, poolsFromGlobalTransportData, deliverySettingService);
			}
		}

		Long payerSapBusinessPartnerIdentifier = inputDeliverySettingParameters.getPayerSapBpId();
		if (payerSapBusinessPartnerIdentifier != null) {
			List<SapBpEntity> payers = deliverySettingOptionConfiguration.getPayerList();
			payerSapBusinessPartner = findBusinessPartnerByIdentifier(
					payerSapBusinessPartnerIdentifier, payers);
		}

		Long billtoSapBusniessPartnerIdentifier = inputDeliverySettingParameters.getBilltoSapBpId();
		if (billtoSapBusniessPartnerIdentifier != null) {
			List<ShiptoBilltoEntity> billTos = deliverySettingOptionConfiguration.getBilltoList();
			billtoSapBusinessPartner = findBillToByBusinessPartnerIdentifier(
					billtoSapBusniessPartnerIdentifier, billTos);
		}

		Long mixDesignIdentifier = inputDeliverySettingParameters.getMixDesignId();
		if (mixDesignIdentifier != null) {
			List<FireqMaterialMixdesignEntity> materialMixDesigns = deliverySettingOptionConfiguration.getMaterialMixDesignList();
			mixdesign = findMixDesingByIdentifier(mixDesignIdentifier,
					materialMixDesigns);
		}


		String unitOfMeasureOfInputDeliverySettings = inputDeliverySettingParameters.getUomCd();
		if (unitOfMeasureOfInputDeliverySettings != null) {
			unitOfMeasure = findUnitOfMeasureByCode(unitOfMeasuresOfDeliverySetting,
					unitOfMeasureOfInputDeliverySettings);
		}
		
		ShiptoSareaRestrEntity shiptoSalesAreaRestriction = originalDeliverySetting.getShiptoSareaRestr();
		String serviceProcessIndicatorId = inputDeliverySettingParameters.getServiceProcessIndicatorId();
		
		DeliverySettingDTO deliverySetting = new DeliverySettingDTO(DeliverySettingDTO.RESOLUTION_NOT_RELEVANT, 
				additionalData, 
				quantity, 
				productionPlant, 
				shippingPoint, 
				commercialCarrier,
				executingCarrier,
				pool, 
				shipToSilo, 
				billtoSapBusinessPartner, 
				supplyPlant, 
				loadingpoint, 
				deliveryType, 
				unitOfMeasure, 
				payerSapBusinessPartner, 
				shipToSalesArea, 
				exactCustomerOrderQuantityFlag, 
				exactSingleDeliveryOrderQuantityFlag, 
				autoPoolAssignmentFlag, 
				autoCarrierAssignmentFlag, 
				autoReleaseFlag, 
				freeDeliveryFlag, 
				releaseSapFlag, 
				onHoldOrderAndLockedFlag, 
				openCustomerOrderFlag, 
				sapSuppressMinChargeFlag, 
				perSingleDeliveryOrderQuantity, 
				perSingleDeliveryOrderUnitOfMeasure, 
				deliveryFrequencyEveryTime, 
				deliveryFrequencyEveryTimeUnitOfMeasureCode, 
				deliveryFrequencyQuantityPerHour,
				deliveryFrequencyQuantityPerHourUnitOfMeasure, 
				deliveryTimeTolerance, 
				deliveryTimeToleranceUnitOfMeasureCode,
				mixdesign, 
				sapContractItem, 
				sapMaterial, 
				selectedTruckWagonTypes, 
				shiptoSalesAreaRestriction, 
				serviceProcessIndicatorId,
				isCashSales);
		
		return deliverySetting;
	}

	public static SapBpEntity findBillToByBusinessPartnerIdentifier(
			Long billtoSapBusniessPartnerIdentifier,
			List<ShiptoBilltoEntity> billTos) {
		for (ShiptoBilltoEntity currentShipToBillTo : billTos) {
			SapBpEntity currentBillToSapBusinessPartner = currentShipToBillTo.getBilltoSapBp();
			Long currentSapBusinessPartnerIdentifier = currentBillToSapBusinessPartner.getSapBpId();
			if (currentSapBusinessPartnerIdentifier.equals(billtoSapBusniessPartnerIdentifier)) {
				return currentBillToSapBusinessPartner;
			}
		}
		return BILLTO_NOT_IN_LIST;
	}

	public static FireqMaterialMixdesignEntity findMixDesingByIdentifier(Long mixDesignIdentifier,
			List<FireqMaterialMixdesignEntity> materialMixDesigns) {
		for (FireqMaterialMixdesignEntity currentFireqMaterialMixdesign : materialMixDesigns) {
			Long currentMixDesignIdentifier = currentFireqMaterialMixdesign.getMixDesignId();
			if (currentMixDesignIdentifier.equals(mixDesignIdentifier)) {
				return currentFireqMaterialMixdesign;
			}
		}
		return MIX_DESIGN_NOT_IN_LIST;
	}

	public static SapBpEntity findBusinessPartnerByIdentifier(
			Long payerSapBusinessPartnerIdentifier, List<SapBpEntity> businessPartners) {
		for (SapBpEntity currentBusinessPartner : businessPartners) {
			Long currentSapBusinessPartnerIdentifier = currentBusinessPartner.getSapBpId();
			if (currentSapBusinessPartnerIdentifier.equals(payerSapBusinessPartnerIdentifier)) {
				return currentBusinessPartner;
			}
		}
		return BUSINESS_PARTNER_NOT_IN_LIST;
	}

	private static PoolEntity findPoolByIdentifier(Long poolIdentifier, List<PoolDTO> poolList, DeliverySettingService deliverySettingService) {
		for (PoolDTO currentPool : poolList) {
			Long currentPoolIdentifier = currentPool.getPoolId();
			if (currentPoolIdentifier.equals(poolIdentifier)) {
				return deliverySettingService.findPoolById(poolIdentifier);
			}
		}
		return POOL_NOT_IN_LIST;
	}

	public static PoolTxEntity findPoolTextByIdentifier(
			Long poolIdentifier, List<PoolTxEntity> poolList) {
		for (PoolTxEntity currentPoolText : poolList) {
			PoolTxPK currentPoolKey = currentPoolText.getPoolTxPK();
			Long currentPoolIdentifier = currentPoolKey.getPoolId();
			if (currentPoolIdentifier.equals(poolIdentifier)) {
				return currentPoolText;
			}
		}
		return POOL_TEXT_NOT_IN_LIST;
	}

	
	public static CommercialCarrierDTO findCommercialCarrierByIdentifier(Long commercialCarrierIdentifier,
			List<CommercialCarrierDTO> commercialCarriers) {
		for (CommercialCarrierDTO currentCommercialCarrier : commercialCarriers) {
			Long currentCommercialCarrierIdentifier = currentCommercialCarrier.getCarrierId();
			if (currentCommercialCarrierIdentifier.equals(commercialCarrierIdentifier)) {
				return currentCommercialCarrier;
			}
		}
		return COMMERCIAL_CARRIER_NOT_IN_LIST;
	}
	
	public static ExecutingCarrierDTO findExecutingCarrierByIdentifier(Long executingCarrierIdentifier,
			List<ExecutingCarrierDTO> executingCarriers) {
		for (ExecutingCarrierDTO currentExecutingCarrier : executingCarriers) {
			Long currentExecutingCarrierIdentifier = currentExecutingCarrier.getCarrierId();
			if (currentExecutingCarrierIdentifier.equals(executingCarrierIdentifier)) {
				return currentExecutingCarrier;
			}
		}
		return EXECUTING_CARRIER_NOT_IN_LIST;
	}

	public static PlantBsEntity findSupplyPlantByCode(String supplyPlantCode,
			List<SupplyPlantDTO> supplyPlantList, BestellungService bestellungService) {
		for (SupplyPlantDTO currentSupplyPlant: supplyPlantList) {
			String currentSupplyPlantCode = currentSupplyPlant.getPlantCd();
			if (currentSupplyPlantCode.equals(supplyPlantCode)) {
				PlantBsEntity supplyPlant = bestellungService.findPlantByCdNoAuthorization(currentSupplyPlantCode);
				return supplyPlant;
			}
		}
		return PLANT_NOT_IN_LIST;
	}
	
	public static PlantBsEntity findProductionPlantByCode(String productionPlantCode,
			List<PlantDTO> productionPlantList, BestellungService bestellungService) {
		for (PlantDTO currentSupplyPlant: productionPlantList) {
			String currentProductionPlantCode = currentSupplyPlant.getPlantCd();
			if (currentProductionPlantCode.equals(productionPlantCode)) {
				PlantBsEntity productionPlant = bestellungService.findPlantByCdNoAuthorization(currentProductionPlantCode);
				return productionPlant;
			}
		}
		return PLANT_NOT_IN_LIST;
	}

	public static ShiptoSiloEntity findShiptoSiloByIdentifier(Long shiptoSiloIdentifier,
			List<ShiptoSiloEntity> silos) {
		for (ShiptoSiloEntity currentShiptoSilo : silos) {
			Long currentShiptoSiloIdentifier = currentShiptoSilo.getShiptoSiloId();
			if (currentShiptoSiloIdentifier.equals(shiptoSiloIdentifier)) {
				return currentShiptoSilo;
			}
		}
		return SHIPTO_NOT_IN_LIST;
	}

	public static LoadingpointEntity findLoadingPointByIdentifier(
			final DeliveryOptionConfigurationDTO deliverySettingOptionConfiguration,
			Long loadingPointIdentifier, BestellungService bestellungService) {
		for (LoadingPointDTO currentLoadingpoint : deliverySettingOptionConfiguration.getLoadingpointList()) {
			Long currentLoadingPointIdentifier = currentLoadingpoint.getLoadingpointId();
			if (currentLoadingPointIdentifier.equals(loadingPointIdentifier)) {
				LoadingpointEntity loadingpoint = bestellungService.findLoadingpointByIdNoAuthorization(currentLoadingpoint.getLoadingpointId());
				return loadingpoint;
			}
		}
		return LOADING_POINT_NOT_IN_LIST;
	}

	public static ShippingpointEntity findShippingPointByIdentifier(
			Long shippingPointIdentifier,
			List<ShippingpointDTO> shippingPoints, BestellungService bestellungService) {
		for (ShippingpointDTO currentShippingPoint : shippingPoints) {
			if (currentShippingPoint.getShippingpointId().equals(shippingPointIdentifier)) {
				ShippingpointEntity shippingPoint = bestellungService.findShippingpointByIdNoAuthorization(currentShippingPoint.getShippingpointId());
				return shippingPoint;
			}
		}
		return SHIPPING_POINT_NOT_IN_LIST;
	}

	public static DeliveryTypeBsEntity findDeliveryTypeByCode(Long deliveryTypeCode,
			List<DeliveryTypeTxEntity> deliveryTypeList) {
		for (DeliveryTypeTxEntity currentDeliveryTypeText : deliveryTypeList) {
			DeliveryTypeTxPK currentDeliveryTypeTextKey = currentDeliveryTypeText.getDeliveryTypeTxPK();
			DeliveryTypeBsEntity currentDeliveryType = currentDeliveryTypeTextKey.getDeliveryTypeBs();
			Long currentDeliveryTypeCode = currentDeliveryType.getDeliveryTypeCd();
			if (currentDeliveryTypeCode.equals(deliveryTypeCode)) {
				return currentDeliveryType;
			}
		}
		return DELIVERY_TYPE_NOT_IN_LIST;
	}

	public static UomBsEntity findUnitOfMeasureByCode(
			List<UomTxEntity> unitOfMeasuresOfDeliverySetting,
			String unitOfMeasureCodeFilter) {
		for (UomTxEntity currentUnitOfMeasureText : unitOfMeasuresOfDeliverySetting) {
			UomTxPK currentUnitOfMeasureTextKey = currentUnitOfMeasureText.getUomTxPK();
			UomBsEntity currentUnitOfMeasure = currentUnitOfMeasureTextKey.getUomBs();
			String currentUnitOfMeasureCode = currentUnitOfMeasure.getUomCd();
			if (currentUnitOfMeasureCode.equals(unitOfMeasureCodeFilter)) {
				return currentUnitOfMeasure;
			}
		}
		return UNIT_OF_MEASURE_NOT_IN_LIST;
	}

	/**
	 * Filters a list of vehicle type wrappers by vehicle code.
	 * @param list A list of <code>TruckWagonTypeWrapper</code>s
	 * @param typeCodes A collection of vehicle type codes
	 * @return A new list which contains exactly those elements of <code>list</code>
	 * whose type code is contained in <code>typeCodes</code>.
	 * @throws NullPointerException If any of the two (mandatory) argument is null.
	 */
	private static List<TruckWagonTypeWrapper> filterVehicleTypeWrapperByTypeCodes(
			List<TruckWagonTypeWrapper> list, 
			Collection<Long> typeCodes)
	{
		// Input check
		if (list == null) {
			throw new NullPointerException("Input list must not be null.");
		}
		if (typeCodes == null) {
			throw new NullPointerException("List of codes must not be null");
		}

		// Initialize empty list. We use LinkedList() since we only add and don't require
		// random access. 
		final List<TruckWagonTypeWrapper> filtered = new LinkedList<TruckWagonTypeWrapper>();

		// Shortcut: If no type codes are given, resulting list will be empty anyway.
		if (typeCodes.isEmpty()) {
			return filtered;
		}

		// Filter
		for (TruckWagonTypeWrapper truckWagonTypeWrapper : list) {
			if (typeCodes.contains(truckWagonTypeWrapper.getTypeCd())) {
				filtered.add(truckWagonTypeWrapper);
			}
		}

		return filtered;
	}

	/**
	 * Iterates over the given order list and their order items and collects all the listed materials
	 * @return a Set<SapMaterialEntity> containing all the collected materials
	 */
	public static Set<SapMaterialEntity> collectAllMaterialsForOrders(List<OrderEntity> orders) {

		Set<SapMaterialEntity> materials;

		if (orders == null || orders.isEmpty()) {
			return Collections.<SapMaterialEntity>emptySet();
		}

		materials = new HashSet<SapMaterialEntity>();

		Map<Long, SapMaterialEntity> collectedMaterials = new HashMap<Long, SapMaterialEntity>();

		for (OrderEntity order : orders) {
			for (OrderItemEntity item : order.getOrderItems()) {
				collectedMaterials.put(item.getSapMaterial().getSapMaterialId(), item.getSapMaterial());
			}
		}

		Set<Long> keySet = collectedMaterials.keySet();
		for (Long materialId : keySet) {
			materials.add(collectedMaterials.get(materialId));
		}

		return materials;

	}

	/**
	 * @param deliverySettingDTO
	 * @param orderItem
	 */
	public static void setupOrderItemAttributes(DeliverySettingDTO deliverySettingDTO,
			OrderItemEntity orderItem) {
		orderItem.setLoadingpoint(deliverySettingDTO.getLoadingpoint());
		orderItem.setSapMaterial(deliverySettingDTO.getSapMaterial());
		orderItem.setShiptoSilo(deliverySettingDTO.getShiptoSilo()); // nullable
		orderItem.setProductionPlant(deliverySettingDTO.getProductionPlant());
		orderItem.setOrderedQuantity(deliverySettingDTO.getQuantity());
		orderItem.setOrderedUom(deliverySettingDTO.getUom());
		
	}

	

	/**
	 * Initialize such order items, which are not present in the contract. Such items can only be saved without
	 * contract item reference, and accepting the consequence that the fields initialized in {{@link #initItemInSapContract(OrderEntity, OrderItemEntity, DeliverySettingService)}
	 * will be left empty.
	 * 
	 * @param order
	 * @param orderItem
	 */
	public static void initItemNotInContract(OrderEntity order, OrderItemEntity orderItem) {
		orderItem.setRate(null);
		orderItem.setCurrency(order.getSapContract().getCurrency());

		//we have price information only for items that are within a contract.
		//=> no price information for services without a contract.
		orderItem.setNetValue(null);
		orderItem.setVatPercentage(null);
		orderItem.setTargetQuantity(null);
		orderItem.setTargetQuantityUom(null);
		orderItem.setTargetUomNumerator(null);
		orderItem.setTargetUomDenominator(null);
	}

	/**
	 * Set the contract references of the order.
	 * @param deliverySettingService TODO
	 * @param altUom the {@link SapMaterialAltUomEntity} if UoM is alternative. null otherwise
	 * @param order, sapContract will be set
	 * @param orderItem, rate, currency, contractItem and sapMaterial will be set.
	 */
	@Deprecated
	public static void initItemInSapContract(OrderItemEntity orderItem,
			AlternativeUnitOfMeasurementInterface alternativeUnitOfMeasurementStore,
			boolean isCashSalesOrExportInvoice) {
		final SapContractItemEntity sapContractItem = orderItem.getSapContractItem();
		final SapContractEntity sapContract = sapContractItem.getSapContract();

		final BigDecimal rate = OrderUtils.calculateRate(sapContractItem);

		orderItem.setRate(rate);
		orderItem.setCurrency(sapContract.getCurrency());

		if (isCashSalesOrExportInvoice) {
			orderItem.setNetValue(sapContractItem.getNetValue());
			orderItem.setVatPercentage(sapContractItem.getVatPercentage());
			
			final BigDecimal targetQuantity = sapContractItem.getTargetQuantity();
			final UomBsEntity targetQuantityUom;
			final Long targetUomNumerator;
			final Long targetUomDenominator;
			
			if (targetQuantity == null) {
				targetQuantityUom = null;
				targetUomNumerator = null;
				targetUomDenominator = null;
			}
			else {
				targetQuantityUom = sapContractItem.getTargetQuantityUom();
				if (targetQuantityUom == null) {
					throw new LogonException(
							"Inconsistent contract item (missing target quantity UoM). contract item: "
									+ sapContract.getSapContractNo() + "-"
									+ sapContractItem.getContractItemNo(),
							Reason.RES_ORDER_PROCESSING_ERROR);
				}

				final SapMaterialEntity sapMaterial = sapContractItem.getSapMaterial();
				final UomBsEntity sapMaterialBaseUom = sapMaterial.getBaseUom();
				String targetUnitOfMeasureCode = targetQuantityUom.getUomCd();
				if (sapMaterialBaseUom.getUomCd().equals(targetUnitOfMeasureCode)) {
					targetUomNumerator = 1L;
					targetUomDenominator = 1L;
				}
				else {
					SapMaterialEntity material = sapContractItem.getSapMaterial();
					Long sapMaterialId = material.getSapMaterialId();
					final SapMaterialAltUomEntity sapMatAltUomEntity = alternativeUnitOfMeasurementStore.fetchByMaterialAndUnitOfMeasureCode(
							sapMaterialId, targetUnitOfMeasureCode);
					if (sapMatAltUomEntity == null) {
						throw new LogonException(
								"Inconsistent contract item (target quantity UoM is not base nor alternative UoM). contract item: "
								+ sapContract.getSapContractNo() + "-" + sapContractItem.getContractItemNo(), Reason.RES_ORDER_PROCESSING_ERROR);
					}

					targetUomNumerator = sapMatAltUomEntity.getNumeratorForConversion();
					targetUomDenominator = sapMatAltUomEntity.getDenominatorForConversion();
				}
			}

			orderItem.setTargetQuantity(targetQuantity);
			orderItem.setTargetQuantityUom(targetQuantityUom);
			orderItem.setTargetUomNumerator(targetUomNumerator);
			orderItem.setTargetUomDenominator(targetUomDenominator);
		}
	}
	
	public static void generateOrderValidationCode(OrderEntity order) {
		try {
			if (order != null && order.getOrderValidationCode() == null) {
				RandPass gen = new RandPass(SecureRandom.getInstance(Constant.SECURE_RANDOM_ALGORITHM), NUMBERS_USED_IN_ORDER_VALIDATION_CODE);
				String orderValidationCode = gen.getPass((int) OrderEntity.DBSIZE_ORDER_VALIDATION_CODE);
				order.setOrderValidationCode(orderValidationCode);
			}
		}
		catch (NoSuchAlgorithmException ex) {
			String msg = "Instantiation of secure random generator failed, algorithm: " + Constant.SECURE_RANDOM_ALGORITHM;
			LOG.error(msg);
			throw new LogonException(msg, Reason.RES_ERROR_IMPL);
		}
	}
	
	public static class OrderItemNoComparator implements Comparator<OrderItemEntity> {
		public int compare(OrderItemEntity oi1, OrderItemEntity oi2) {
			return oi1.getOrderItemNo().compareTo(oi2.getOrderItemNo());
		}
	}
	
	public static List<OrderItemEntity> getOrderItemsFromOrderSorted(OrderEntity order) {
		
		List<OrderItemEntity> orderItems = order.getOrderItems();
		Collections.sort(orderItems, new OrderItemNoComparator());
		
		return orderItems;
	}
	
	/**
	 * In case of bulk pickup orders ORDERS_LOADABLE_TERMINAL_FLAG is ignored and 
	 * considered as OFF
	 * 
	 * See LOGON-5073 for details.
	 * 
	 * @param custorderEntity
	 * @param pickupLoadingpoint
	 * @return
	 */
	public static boolean isPickupCustorderEffectiveLoadable(CustorderEntity custorderEntity, PickupLoadingpointEntity pickupLoadingpoint) {
		if (pickupLoadingpoint == null) {
			return false;
		}
		
		if (custorderEntity.getDeliveryType() != null
				&& LogonUtil.isReturnPalletOnly(custorderEntity.getDeliveryType().getDeliveryTypeCd())) {
			return false;
		}
		
		boolean result = pickupLoadingpoint.getOrdersLoadableTerminalFlag() &&
							!LogonPackageHandlingUtil.isBulk(custorderEntity);
		return result;
	}
	
	/**
	 * Determining "the LP" of the order
	 * 
	 * @param order
	 * @return loadingpoint of the order
	 */
	public static LoadingpointEntity getLoadingpointFromOrder(OrderEntity order) {
		List<OrderItemEntity> orderItemList = order.getOrderItems();
		sortOrderItems(orderItemList);
		LoadingpointEntity firstPlannedLoadingpoint = null;
		// The first order item's load's LP determines the order's LP
		for (OrderItemEntity orderItem : orderItemList) {
			LoadEntity load = orderItem.getLoad();
			if (load != null) {
				LoadingpointEntity loadingpointOfLoad = load.getLoadingpoint();
				if (loadingpointOfLoad != null) {
					return loadingpointOfLoad;
				}
			}
			LoadingpointEntity loadingpointOfOrderItem = orderItem.getLoadingpoint();
			if (firstPlannedLoadingpoint == null && loadingpointOfOrderItem != null) {
				firstPlannedLoadingpoint = loadingpointOfOrderItem;
			}
		}
		// If there was no load for the order, return the first planned LP if it exists.
		if (firstPlannedLoadingpoint != null) {
			return firstPlannedLoadingpoint;
		}
		Long orderNumber = order.getOrderNumber();
		LOG.error("No loadingpoint found for the ordernumber: " + orderNumber);
		/*
		 * Not throwing LogonException here, because of the risk of stopping business processes.
		 */
		return null;
	}
	
	public static boolean isWebOrderDeletable(Long webOrderStatusCd, long webOrderDeliveryTypeCd) {
		boolean isPickup = webOrderDeliveryTypeCd == Constant.DELIVERY_TYPE_PICKUP || 
			webOrderDeliveryTypeCd == Constant.DELIVERY_TYPE_SHIP_PICKUP || 
			webOrderDeliveryTypeCd == Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP ;
		
		boolean isWebOrderDeletable = webOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_NEW)) ||
				webOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL)) ||
				webOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK)) ||
				webOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER)) ||
				webOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK)) ||
				(isPickup && ( webOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_DISPATCHED)) || 
						webOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) )));
		
		return isWebOrderDeletable;
	}
}

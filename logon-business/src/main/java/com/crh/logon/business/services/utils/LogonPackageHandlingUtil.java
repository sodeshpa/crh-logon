/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * The class contains all necessary methods used related to package handling.
 * See also TLGNR_MATERIAL_PACKAGE_BS table, and comments.
 * @author AdNovum Informatik AG
 */
public class LogonPackageHandlingUtil {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonPackageHandlingUtil.class);

    
	public enum PackageType {
		
		BULK_POWDER(Constant.PACKAGE_TYPE_BULK_POWDER, Constant.PACKAGE_TYPE_BULK_POWDER_STR),
		BAG(Constant.PACKAGE_TYPE_BBAG, Constant.PACKAGE_TYPE_BBAG_STR),
		SERVICE(Constant.PACKAGE_TYPE_SERVICE, Constant.PACKAGE_TYPE_SERVICE_STR),
		BULK_DUMPED(Constant.PACKAGE_TYPE_BULK_DUMPED, Constant.PACKAGE_TYPE_BULK_DUMPED_STR),
		UNKNOWN(Constant.PACKAGE_TYPE_UNKNOWN, Constant.PACKAGE_TYPE_UNKNOWN_STR);
		
		private long typeConstant;
		private String typeName;
		
		private PackageType(long typeConstant, String typeName) {
			this.typeConstant = typeConstant;
			this.typeName = typeName;
		}
		
		public long getTypeConstant() {
			return this.typeConstant;
		}
		
		public String getTypeName() {
			return this.typeName;
		}
	}
    

	/**
	 * Checks if an order item has packaging type 'bulk'
	 * @param orderItem The order item to check
	 * @return true if the order item has packaging type 'bulk', false otherwise
	 */
	public static boolean isBulkDumpOrBulkPowder(OrderItemEntity orderItem) {
		
		return isBulkDumpOrBulkPowder(orderItem.getSapMaterial());
	}
	
	public static boolean isBulkDumped(OrderItemEntity orderItem)
	{
		return isBulkDumped(orderItem.getSapMaterial());
	}
	
	public static boolean isBulkPowder(OrderItemEntity orderItem)
	{
		return isBulkPowder(orderItem.getSapMaterial());
	}
	
	/**
	 * Checks if an order item has packaging type 'bag'
	 * @param orderItem The order item to check
	 * @return true if the order item has packaging type 'bag', false otherwise
	 */
	public static boolean isBag(OrderItemEntity orderItem) {
		
		return isBag(orderItem.getSapMaterial());
	}
	
	/**
	 * Checks if an order item counts as a service. Currently everything what is not bag and
	 * not bulk is a service.
	 * @param orderItem The order item to check
	 * @return true if the order item is not bag or bulk
	 */
	public static boolean isService(OrderItemEntity orderItem) {
		
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		return isService(sapMaterial);
	}
	
	/**
	 * Checks if an order item counts as a pallet. IMPORTANT: there is no separate type for pallets in
	 * TLGNR_PACKAGE_TYPE_BS/TX they are mapped to Bag (so isBag() will return true for pallets too).
	 * However is SAP and in TLGNR_MATERIAL_PACKAGE_BS/TX pallets (S003) and bags (S002) are differentiated.
	 * @param orderItem The order item to check
	 * @return true if the order item is pallet
	 */
	public static boolean isPallet(OrderItemEntity orderItem) {
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		return isPallet(sapMaterial);
	}

	/**
	 * Checks if the sap material is a bulk sap material according to the logonPackageTypeCd.
	 * @param sapMaterial
	 * @return true if and only if it is definitely bulk
	 */
	public static boolean isBulkDumpOrBulkPowder(SapMaterialEntity sapMaterial) {
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
		if (logonPackageType != null) {			
			Long packageTypeCd = logonPackageType.getPackageTypeCd();
			return isBulkDumpOrBulkPowder(packageTypeCd);
		}
		return false;
	}

	/**
	 * Returns true if the parameter logonPackageTypeCd is considered a bulk dumped or bulk powder.
	 * @param packageTypeCd
	 * @return
	 */
	public static boolean isBulkDumpOrBulkPowder(Long packageTypeCd) {
		return isBulkDumped(packageTypeCd) || 
				isBulkPowder(packageTypeCd);
	}
	
	/**
	 * Checks if the sap material is a bulk dumped sap material according to the logonPackageTypeCd.
	 * @param sapMaterial
	 * @return true if and only if it is definitely bulk
	 */
	public static boolean isBulkDumped(SapMaterialEntity sapMaterial) {
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
		if (logonPackageType != null) {
			Long packageTypeCd = logonPackageType.getPackageTypeCd();
			long logonPackageTypeCd = packageTypeCd.longValue();
			return isBulkDumped(logonPackageTypeCd);
		}
		return false;
	}

	/**
	 * Returns true if the parameter logonPackageTypeCd is considered a bulk dumped.
	 * @param logonPackageTypeCd
	 * @return
	 */
	private static boolean isBulkDumped(long logonPackageTypeCd) {
		return logonPackageTypeCd == Constant.PACKAGE_TYPE_BULK_DUMPED;
	}
	
	/**
	 * Checks if the sap material is a bulk powder sap material according to the logonPackageTypeCd.
	 * @param sapMaterial
	 * @return true if and only if it is definitely bulk
	 */
	public static boolean isBulkPowder(SapMaterialEntity sapMaterial) {
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
		if (logonPackageType != null) {
			Long packageTypeCd = logonPackageType.getPackageTypeCd();
			long isBulkPowder = packageTypeCd.longValue();
			return isBulkPowder(isBulkPowder);
		}
		return false;
	}

	/**
	 * Returns true if the parameter logonPackageTypeCd is considered a bulk powder.
	 * @param isBulkPowder
	 * @return
	 */
	private static boolean isBulkPowder(long logonPackageTypeCd) {
		return logonPackageTypeCd == Constant.PACKAGE_TYPE_BULK_POWDER;
	}
	
	/**
	 * Checks if the sap material is a bag sap material according to the logonPackageTypeCd.
	 * @param sapMaterial
	 * @return true if and only if it is definitely bag
	 */	
	public static boolean isBag(SapMaterialEntity sapMaterial) {
		
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
		if (logonPackageType != null) {
			Long packageTypeCd = logonPackageType.getPackageTypeCd();
			return (packageTypeCd.longValue() == Constant.PACKAGE_TYPE_BBAG);
		}
		return false;
	}
	
	/**
	 * Checks if the sap material is a service sap material according to the logonPackageTypeCd.
	 * @param sapMaterial
	 * @return true if and only if it is definitely service
	 */
	public static boolean isService(SapMaterialEntity sapMaterial) {
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
		if (logonPackageType != null && 
				!LogonPackageHandlingUtil.isBag(sapMaterial) && 
				!isBulkDumpOrBulkPowder(sapMaterial) && 
				LogonPackageHandlingUtil.isService(logonPackageType.getPackageTypeCd()) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the SAP material is a pallet. IMPORTANT: there is no separate type for pallets in
	 * TLGNR_PACKAGE_TYPE_BS/TX they are mapped to Bag (so isBag() will return true for pallets too).
	 * However is SAP and in TLGNR_MATERIAL_PACKAGE_BS/TX pallets (S003) and bags (S002) are differentiated.
	 * @param orderItem The order item to check
	 * @return true if the order item is pallet
	 */
	public static boolean isPallet(SapMaterialEntity sapMaterial) {
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		if (sapMaterial != null && 
				materialPackage != null && 
				Constant.MATERIAL_PACKAGE_PALET_EMPTY.equals(materialPackage.getMaterialPackageCd())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if and only if the package type is bulk.
	 * @param packageTypeCd
	 * @return
	 */
	public static boolean isBulk(Long packageTypeCd) {
		if ( packageTypeCd == null ) {
			return false;
		}
		switch (packageTypeCd.intValue()) {	
		case (int)Constant.PACKAGE_TYPE_BULK_POWDER:
			return true;
		case (int)Constant.PACKAGE_TYPE_BBAG:
		case (int)Constant.PACKAGE_TYPE_SERVICE:
		default:
			return false;
		}
	}
	
	/**
	 * Returns true if and only if the package type is bag.
	 * @param packageTypeCd
	 * @return
	 */
	public static boolean isBag(Long packageTypeCd)
	{
		return Constant.PACKAGE_TYPE_BBAG == packageTypeCd;
	}
    
	/**
	 * Returns true if and only if the package type is service.
	 * @param packageTypeCd
	 * @return
	 */
	public static boolean isService(Long packageTypeCd) 
	{
		return Constant.PACKAGE_TYPE_SERVICE == packageTypeCd;
	}
	
	/**
	 * Returns true only in case the load counts as a bulk load. Checks
	 * according to the sap materials logon package type cd.
	 * Please note that there is no SERVICE load, only bag and bulk load! 
	 * @param load
	 * @return
	 */
	public static boolean isBulk(LoadEntity load) {
		
		return isBulkDumpOrBulkPowder(load.getSapMaterial());
	}

	/**
	 * Returns true only in case the load counts as a bag load. Checks
	 * according to the sap materials logon package type cd.
	 * Please note that there is no SERVICE load, only bag and bulk load!
	 * @param load
	 * @return
	 */
	public static boolean isBag(LoadEntity load) {
		
		return LogonPackageHandlingUtil.isBag(load.getSapMaterial());
	}
	
	/**
	 * Checks if the given package type is a material type (as opposed
	 * to a service type).
	 * @param packageType The package type of an order item's material
	 * @return true if the type is a material type, false otherwise.
	 */
	public static boolean isMaterialType(final Long packageType) {
		return Constant.PACKAGE_TYPE_BULK_POWDER == packageType || 
				Constant.PACKAGE_TYPE_BBAG == packageType ||
				Constant.PACKAGE_TYPE_BULK_DUMPED == packageType;
	}

	public static boolean isMaterialType(final LogonPackageHandlingUtil.PackageType packageType) {
		return isMaterialType(packageType.getTypeConstant());
	}
	
	/**
	 * Checks the package combination of the order items, and returns a set of error codes
	 * in case something is wrong with the items presented in orderItems. 
	 * Valid combinations:
	 * bulk item and service(s)
	 * bag item(s) and service(s)
	 * service(s) only (LRQ 070.3 / LOGON-4743)
	 * 
	 * @param orderItems
	 */
	public static void checkLogonPackageCombination(List orderItems) throws ValidationException{
		Iterator it = orderItems.iterator();
		Long logonPackageTypeCd = null;
		boolean hasService = false;
		boolean hasMultiOrderItem = false;

		UomBsEntity orderedUom = null;
		BigDecimal orderedQuantity = null;
		SapMaterialEntity sapMaterial = null;
		boolean retRepOrder = false;
		
		Object parentObject = null;
		
		while (it.hasNext()) {
			
			Object o  = it.next();
			
			if ( o instanceof OrderItemEntity ) {
				OrderItemEntity item = (OrderItemEntity) o;
				parentObject = item.getOrder();
				String distributionDestCd = ((OrderEntity)parentObject).getDistributionDest().getDistributionDestCd();
				if (LogonUtil.isRepairDistributionDest(distributionDestCd) || LogonUtil.isReturnDistributionDest(distributionDestCd)) {
					retRepOrder = true;
				}
				orderedUom = item.getOrderedUom();
				orderedQuantity = item.getOrderedQuantity();
				sapMaterial = item.getSapMaterial();
			}
			else if ( o instanceof CustorderItemEntity) {
				CustorderItemEntity item = (CustorderItemEntity) o;
				parentObject = item.getCustorder();
				orderedUom = item.getOrderedUom();
				orderedQuantity = item.getOrderedQuantity();
				sapMaterial = item.getSapMaterial();				 
			}
			
			// ordered quantity and UoM could be null in performance order
			if ( orderedUom != null && orderedQuantity != null && orderedUom.getCreateMultiOrderFlag() && orderedQuantity.compareTo(BigDecimal.ONE) > 0) {
				hasMultiOrderItem = true;
			}
			PackageTypeBsEntity logonPackageType = sapMaterial.getMaterialPackage().getLogonPackageType();
			if ( logonPackageType == null) {
				CustorderError error = CustorderError.UNKNOWN_LOGON_PACKAGE_TYPE;
				throwIntegrityException(parentObject, error);

			}
			else {
				Long currentPackageTypeCd = logonPackageType.getPackageTypeCd();
				
				if (!retRepOrder && isService(currentPackageTypeCd)){
					hasService = true;
					//services do should not define the main profile of the order, except for repair/return rail orders!
					continue;
				}
				if (logonPackageTypeCd == null ) {
					logonPackageTypeCd = currentPackageTypeCd;
				}
				else if ( logonPackageTypeCd != currentPackageTypeCd.longValue()) {
					throwIntegrityException(parentObject, CustorderError.MIXED_PACKAGE_COMBINATION);
				}
				else if ( orderedUom != null && !orderedUom.getUomCd().equals(Constant.UOM_CONTAINER) && 
						(currentPackageTypeCd == Constant.PACKAGE_TYPE_BULK_POWDER ||
						currentPackageTypeCd == Constant.PACKAGE_TYPE_BULK_DUMPED) ) {
					throwIntegrityException(parentObject, CustorderError.MORE_THAN_ONE_BULK_ITEM);
				}
			}
		}
		
		// LRQ 070.3 Service Material Only Orders (LOGON-4743): from now on it is possible to create an order containing only service materials
		if ( logonPackageTypeCd == null && !hasService) {
			//if it was not possible to define the type of the order bag/bulk then do NOT let it through!
			throwIntegrityException(parentObject, CustorderError.UNKNOWN_LOGON_PACKAGE_TYPE);
		}
		
		// LRQ 070.2: it is possible -> each Single Delivery Order will be created with all the service materials added to the Customer Order
//		if ( hasService && hasMultiOrderItem ) {
//			return CustorderError.MULTI_ORDER_FLAGGED_ITEM_WITH_SERVICE;
//		}
		return;
	}

	private static void throwIntegrityException(Object o, CustorderError error) throws ValidationException{
		
		if (o instanceof OrderEntity ) {
			String[] additionalInfo = {};
			throw new ValidationException(new ValidationErrorReason(error, (OrderEntity) o, additionalInfo));
		}
		else if ( o instanceof CustorderEntity) {
			String[] additionalInfo = {};
			throw new ValidationException(new ValidationErrorReason(error, (CustorderEntity) o, additionalInfo));
		}
	}
	
	
	public static CustorderError checkLogonPackageCombination(final CustorderEntity custorder, final boolean isOnlyCustorderItems) throws ValidationException{
		// [[
		CustorderError result = null;
		Object[][] params = {{"custorder", custorder},
							{"isOnlyCustorderItems", isOnlyCustorderItems + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonPackageHandlingUtil.class, "checkLogonPackageCombination", params);
		}
		// ]]
		try {
			if (custorder == null) {
				throw new LogonException("No customer order specified for checking package combination", Reason.RES_ERROR_IMPL);
			}
			
			// --- CHECK CUSTOMER ORDER ITEMS ---
			
			final List<CustorderItemEntity> custorderItems = (List<CustorderItemEntity>) custorder.getCustorderItems();
			if (custorderItems == null || custorderItems.isEmpty()) {
				throw new LogonException("No customer order itemspecified for checking package combination", Reason.RES_ERROR_IMPL);
			}
			
			checkLogonPackageCombination(custorderItems);
			
			final Set<SapMaterialEntity> sapMaterialsOfCustorderItems = new HashSet<SapMaterialEntity>();
			for (final CustorderItemEntity custorderItem : custorderItems) {
				final SapMaterialEntity sapMaterial = custorderItem.getSapMaterial();
				final boolean isNewlyAdded = sapMaterialsOfCustorderItems.add(sapMaterial);
				if (!isNewlyAdded) {
					return result = CustorderError.MATERIAL_NOT_UNIQUE;
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
	 * Returns a list with those items, which must be handled  as service items.
	 * For service items there is no load, no loading assignment part. 
	 * The return list of order items
	 * the items, which are material items, and therefore DO NEED bag/bulk handling. (Ie. bulk, bag, and
	 * items for which the package type cannot be determined.)
	 * 
	 * <p>TODO 28-Nov-2012/romek: This function should return a {@link List}, rather than
	 * a {@link Collection}! It is sub-optimal to get the first element by calling
	 * <code>orderItems.iterator().next()</code> instead of <code>orderItems.get(0)</code>, and
	 * why to cast to List from outside when we know we would always be dealing with a List? This
	 * cleanup however takes quite a lot of code changes now.</p>
	 * 
	 * @param order
	 * @return List of service items in the order
	 */
	public static Collection<OrderItemEntity> getServiceItems(OrderEntity order) {
		return getServiceItems(order.getOrderItems());
	}

	public static List<CustorderItemEntity> getServiceCustorderItems(List<CustorderItemEntity> custorderItems) {
		final List<CustorderItemEntity> serviceItems = new LinkedList<CustorderItemEntity>();
		for (CustorderItemEntity currItem : custorderItems) {
			if (isService(currItem.getSapMaterial())) {
				serviceItems.add(currItem);
			}
		}
		return serviceItems;
	}
	
	public static List<OrderItemEntity> getServiceItems(List<OrderItemEntity> orderItems) {
		final List<OrderItemEntity> serviceItems = new LinkedList<OrderItemEntity>();
		for (OrderItemEntity currItem : orderItems) {
			if (isService(currItem)) {
				serviceItems.add(currItem);
			}
		}
		return serviceItems;
	}
	
	/**
	 * Returns a list with those items, which must be handled either as bag or bulk. Will NOT return
	 * the items, which are not material items, and therefore do not need bag/bulk handling. (Ie. service, and
	 * items for which the package type cannot be determined.)
	 * 
	 * @param order
	 * @return
	 */
	public static List<OrderItemEntity> getMaterialItems(OrderEntity order) {
		if (order == null) {
			throw new LogonException(Reason.RES_ERROR_IMPL);
		}
		
		return getMaterialItems(order.getOrderItems());
	}
	
	public static List<CustorderItemEntity> getMaterialCustorderItems(List<CustorderItemEntity> orderItems) {
		final List<CustorderItemEntity>materialItems = new ArrayList<CustorderItemEntity>();
		for (CustorderItemEntity item : orderItems) {
			if (isBulkDumpOrBulkPowder(item.getSapMaterial())) {
				materialItems.add(item);
			}
			else if (isBag(item.getSapMaterial())) {
				materialItems.add(item);
			}
		}
		return materialItems;
	}
	
	public static List<OrderItemEntity> getMaterialItems(List<OrderItemEntity> orderItems) {
		List<OrderItemEntity> materialItems = new ArrayList<OrderItemEntity>();
		for (OrderItemEntity item : orderItems) {
			if ( item.getLoad() != null ) {
				//this is necessary until the orders must be backwards compatible with the current
				// orders in DB (such as return orders) which still have a load, though they are not
				// bag/bulk
				materialItems.add(item);
			}
			else if (isBulkDumpOrBulkPowder(item)) {
				materialItems.add(item);
			}
			else if (isBag(item)) {
				materialItems.add(item);
			}
		}
		return materialItems;
	}
	
	/**
	 * Returns a list of orderItems. Non-pallet items are leading the list, 
	 * then pallets are included.
	 * 
	 * @param order
	 * @return non-pallet orderItems and pallet orderItems in this order
	 */
	public static List<OrderItemEntity> getOrderItemsLeadingRealMaterialEndingPallet(OrderEntity order) {
		if (order == null) {
			throw new LogonException(Reason.RES_ERROR_IMPL);
		}
		List<OrderItemEntity> materialOrderItems = getMaterialItems(order.getOrderItems());
		List<OrderItemEntity> nonPalletOrderItems = new ArrayList<OrderItemEntity>(materialOrderItems.size());
		List<OrderItemEntity> palletOrderItems = new ArrayList<OrderItemEntity>(materialOrderItems.size());
		
		for (OrderItemEntity orderItem : materialOrderItems) {
			if (!isPallet(orderItem)) {
				nonPalletOrderItems.add(orderItem);
			}
			else {
				palletOrderItems.add(orderItem);
			}
		}

		List<OrderItemEntity> sortedOrderItems = new ArrayList<OrderItemEntity>();
		sortedOrderItems.addAll(nonPalletOrderItems);
		sortedOrderItems.addAll(palletOrderItems);
		return sortedOrderItems;
	}
	
	public static boolean isBulk(CustorderEntity custorder) {
		return isTypeOf(custorder, LogonPackageHandlingUtil.PackageType.BULK_POWDER).get(LogonPackageHandlingUtil.PackageType.BULK_POWDER) ||
				isTypeOf(custorder, LogonPackageHandlingUtil.PackageType.BULK_DUMPED).get(LogonPackageHandlingUtil.PackageType.BULK_DUMPED);
	}
	
	public static boolean isBag(CustorderEntity custorder) {
		return isTypeOf(custorder, LogonPackageHandlingUtil.PackageType.BAG).get(LogonPackageHandlingUtil.PackageType.BAG);
	}
	
	public static boolean hasService(CustorderEntity custorder) {
		return isTypeOf(custorder,LogonPackageHandlingUtil.PackageType.SERVICE).get(LogonPackageHandlingUtil.PackageType.SERVICE);
	}
	
	
	public static boolean isServiceOnlyCustomerOrder(CustorderEntity custorder) {
		// [[
		boolean result = false;
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonPackageHandlingUtil.class, "isServiceOnlyCustomerOrder", params);
		}
		// ]]
		try {
			final Map<LogonPackageHandlingUtil.PackageType, List<CustorderItemEntity>> pkgTypeCOIListMap = getPackageTypeCustorderItemMap(custorder);
			
			if (pkgTypeCOIListMap == null) {
				return result = false;
			}
			else {
				boolean hasService = false;
				
				for (LogonPackageHandlingUtil.PackageType pkgType : pkgTypeCOIListMap.keySet()) {
					if (LogonPackageHandlingUtil.PackageType.SERVICE.equals(pkgType)) {
						final List<CustorderItemEntity> serviceCOIs = pkgTypeCOIListMap.get(pkgType);
						if (serviceCOIs != null && !serviceCOIs.isEmpty()) {
							hasService = true;
						}
					}
					else {
						final List<CustorderItemEntity> nonServiceCOIs = pkgTypeCOIListMap.get(pkgType);
						if (nonServiceCOIs != null && !nonServiceCOIs.isEmpty()) {
							return result = false;
						}
					}
				}
				
				return result = hasService;
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
	
	public static boolean isPalletCustomerOrder(CustorderEntity custorder) {
		// [[
		boolean result = false;
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonPackageHandlingUtil.class, "isPalletCustomerOrder", params);
		}
		// ]]
		try {
			Map<LogonPackageHandlingUtil.PackageType, List<CustorderItemEntity>> pkgTypeCOIListMap = getPackageTypeCustorderItemMap(custorder);
			
			boolean hasPallet = false;
			
			if (pkgTypeCOIListMap == null) {
				return result = false;
			}
			else {
				for (LogonPackageHandlingUtil.PackageType pkgType : pkgTypeCOIListMap.keySet()) {
					if (LogonPackageHandlingUtil.PackageType.BAG.equals(pkgType)) {	// no separate logon package type exist for pallets they are mapped to bag!
						final List<CustorderItemEntity> bagCOIs = pkgTypeCOIListMap.get(pkgType);
						if (bagCOIs != null && !bagCOIs.isEmpty()) {
							for (CustorderItemEntity bagCOI : bagCOIs) {
								final SapMaterialEntity sapMaterial = bagCOI.getSapMaterial();
								if (LogonPackageHandlingUtil.isPallet(sapMaterial)) {
									hasPallet = true;
								}
								else {
									return result = false;
								}
							}
						}
						else {
							return result = false;
						}
					}
					else if (!LogonPackageHandlingUtil.PackageType.SERVICE.equals(pkgType)) {	// return pallet orders can have service items
						final List<CustorderItemEntity> otherCOIs = pkgTypeCOIListMap.get(pkgType);
						if (otherCOIs != null && !otherCOIs.isEmpty()) {
							return result = false;
						}
					}
			
				}
				
				return result = hasPallet;
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
	
	
	public static Map<LogonPackageHandlingUtil.PackageType, Boolean> isTypeOf(CustorderEntity custorder, LogonPackageHandlingUtil.PackageType... packagingTypes) {
		// [[
		Map<LogonPackageHandlingUtil.PackageType,Boolean> result = null;
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonPackageHandlingUtil.class, "isTypeOf", params);
		}
		// ]]
		try {
			// initialize result map with false values by default
			result = new HashMap<LogonPackageHandlingUtil.PackageType, Boolean>();
			for (LogonPackageHandlingUtil.PackageType packagingType : packagingTypes) {
				result.put(packagingType, false);
			}
			
			Map<LogonPackageHandlingUtil.PackageType, List<CustorderItemEntity>> packageTypeCoisMap = getPackageTypeCustorderItemMap(custorder);
			if (packageTypeCoisMap == null) {
				return result;
			}
			
			// since the result initialized with false values by default we have to only update the true ones
			for (LogonPackageHandlingUtil.PackageType packagingType : packagingTypes) {
				List<CustorderItemEntity> coiListOfType = packageTypeCoisMap.get(packagingType);
				if (coiListOfType != null && !coiListOfType.isEmpty()) {
					result.put(packagingType, true);
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
	
	public static Map<LogonPackageHandlingUtil.PackageType, List<CustorderItemEntity>> getPackageTypeCustorderItemMap(final CustorderEntity custorder) {
		// [[
		Map<LogonPackageHandlingUtil.PackageType,List<CustorderItemEntity>> result = null;
		Object[][] params = {{"custorder", custorder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonPackageHandlingUtil.class, "getPackageTypeCustorderItemMap", params);
		}
		// ]]
		try {
			result = new HashMap<LogonPackageHandlingUtil.PackageType, List<CustorderItemEntity>>();
			
			if (custorder == null) {
				String errMsg = "customer order is null";
				LOG.error(errMsg);
				return result = null;
			}
			
			@SuppressWarnings("unchecked")
			final List<CustorderItemEntity> custorderItems = custorder.getCustorderItems();
			if (custorderItems == null || custorderItems.isEmpty()) {
				String errMsg = "no customer order item found in customer order";
				LOG.error(errMsg);
				return result = null;
			}
			
			for (LogonPackageHandlingUtil.PackageType packageType : LogonPackageHandlingUtil.PackageType.values()) {
				result.put(packageType, new ArrayList<CustorderItemEntity>());
			}
			
			for (CustorderItemEntity custorderItem : custorderItems) {
				SapMaterialEntity sapMaterial = custorderItem.getSapMaterial();
				
				if (LogonPackageHandlingUtil.isBulkPowder(sapMaterial)) {
					result.get(LogonPackageHandlingUtil.PackageType.BULK_POWDER).add(custorderItem);
				}
				else if (LogonPackageHandlingUtil.isBag(sapMaterial)) {
					result.get(LogonPackageHandlingUtil.PackageType.BAG).add(custorderItem);
				}
				else if (LogonPackageHandlingUtil.isService(sapMaterial)) {
					result.get(LogonPackageHandlingUtil.PackageType.SERVICE).add(custorderItem);
				}
				else if (LogonPackageHandlingUtil.isBulkDumped(sapMaterial)) {
					result.get(LogonPackageHandlingUtil.PackageType.BULK_DUMPED).add(custorderItem);
				}
				else {
					String errMsg = "unknown packaging type. SAP material: " + sapMaterial.getSapMaterialId();
					LOG.error(errMsg);
					result.get(LogonPackageHandlingUtil.PackageType.UNKNOWN).add(custorderItem);
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

	
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * A class that adds invalidMap support to other classes. Also has utility functions to operate on the map.
 * 
 * NOTE: It is the freedom of the class extending this class to decide whether it wants to expose
 * the invalidMap to the outside world. (This is required for DeliverySettingShiptoMap which has an inner
 * invalidMap for each DeliverySettingSalesAreaMap,) This is less of a problem if we later decide not to
 * let other classes extend this class, but have this class stored in a member field.
 * 
 * @author AdNovum Informatik AG
 *
 */
public abstract class InvalidMapSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(InvalidMapSupport.class);

	/**
	 * Map which contains objects and error codes. Different error codes count different on
	 * certain callers.
	 */
	private Map<Object, List<ShiptoSalesAreaError>> invalidMap = new HashMap<Object, List<ShiptoSalesAreaError>>();


	protected Map<Object, List<ShiptoSalesAreaError>> getInvalidMap() {
		return invalidMap;
	}

	/**
	 * @param message
	 * @param restr
	 */
	protected void updateInvalidMap(Object obj, ShiptoSalesAreaError message) {
		List<ShiptoSalesAreaError> list = invalidMap.get(obj);
		
		if (list == null) {
			list = new ArrayList<ShiptoSalesAreaError>();
			this.invalidMap.put(obj, list);
		}
		
		list.add(message);
	}


	/**
	 * Checks if the invalidMap contains errors of at least one of the given types for an object.
	 * (Convenience method.)
	 * 
	 * <p>NOTE (romek May 21, 2011): This method should only be used when there is no need to handle all
	 * kinds of errors for an object differently, otherwise it would rather be ineffective.</p>
	 * 
	 * @param invalidMap
	 * @param object a key of the invalidMap
	 * @param errorTypes error types to check (if empty or null, any error will match, but then it is
	 *         just simpler to call <code>invalidMap.containsKey(object)</code>)
	 * @return true if found errors, false otherwise
	 * @see #containsError(Map, Object, ShiptoSalesAreaError[], ShiptoSalesAreaError[])
	 * @adnlogger.notrace
	 */
	public static boolean containsError(Map<Object, List<ShiptoSalesAreaError>> invalidMap, Object object,
			ShiptoSalesAreaError... errorTypes) {
		return containsError(invalidMap, object, errorTypes, null);
	}


	/**
	 * Checks if the invalidMap contains errors for an object, excluding the given error types.
	 * (Convenience method.)
	 * 
	 * <p>NOTE (romek May 21, 2011): This method should only be used when there is no need to handle all
	 * kinds of errors for an object differently, otherwise it would rather be ineffective.</p>
	 * 
	 * @param invalidMap
	 * @param object a key of the invalidMap
	 * @param exceptions error types to ignore
	 * @return true if found errors, false otherwise
	 * @see #containsError(Map, Object, ShiptoSalesAreaError[], ShiptoSalesAreaError[])
	 * @adnlogger.notrace
	 */
	public static boolean containsErrorExcept(Map<Object, List<ShiptoSalesAreaError>> invalidMap, Object object,
			ShiptoSalesAreaError... exceptions) {
		return containsError(invalidMap, object, null, exceptions);
	}


	/**
	 * Search for errors for an object in the invalidMap matching the given criterias. Note that the normal
	 * usage would imply either "errorTypes" or "exceptions" being empty, but handling both criterias in
	 * a common function was a design goal, hence this is a private method.
	 * 
	 * @param invalidMap
	 * @param object a key of the invalidMap
	 * @param errorTypes error types to check (if empty or null, any error will match)
	 * @param exceptions error types to ignore
	 * @return true if found errors, false otherwise
	 */
	private static boolean containsError(Map<Object, List<ShiptoSalesAreaError>> invalidMap, Object object,
			ShiptoSalesAreaError[] errorTypes, ShiptoSalesAreaError[] exceptions) {
		// [[
		boolean result = false;
		Object[][] params = {{"invalidMap", invalidMap},
							{"object", object},
							{"errorTypes", errorTypes},
							{"exceptions", exceptions}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(InvalidMapSupport.class, "containsError", params);
		}
		// ]]
		try {
			List<ShiptoSalesAreaError> errorsList = invalidMap.get(object);
			List<ShiptoSalesAreaError> errorTypesList = errorTypes != null ? Arrays.asList(errorTypes) : null;
			List<ShiptoSalesAreaError> exceptionsList = exceptions != null ? Arrays.asList(exceptions) : null;
			
			if (errorsList != null) {
				for (ShiptoSalesAreaError error: errorsList) {
					if (exceptionsList != null && exceptionsList.contains(error)) {
						continue;
					}
			
					if (errorTypesList != null && !errorTypesList.isEmpty()) {
						if (errorTypesList.contains(error)) {
							return result = true;
						}
					}
					else {
						return result = true;
					}
				}
			}
			
			return result = false;
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
	 * Checks if the invalidMap contains errors of at least one of the given types for an object type.
	 * (Convenience method.)
	 * 
	 * <p>NOTE (romek May 21, 2011): This method should only be used when there is no need to handle all
	 * kinds of errors for an object differently, otherwise it would rather be ineffective.</p>
	 * 
	 * @param invalidMap
	 * @param objectType the class that the keys of the invalidMap must match
	 * @param errorTypes error types to check (if empty or null, any error will match, but then it is
	 *         just simpler to call <code>invalidMap.containsKey(object)</code>)
	 * @return true if found errors, false otherwise
	 * @see #containsError(Map, Object, <ShiptoSalesAreaError>[], ShiptoSalesAreaError[])
	 * @adnlogger.notrace
	 */
	public static boolean containsError(Map<Object, List<ShiptoSalesAreaError>> invalidMap, Class<?> objectType,
			ShiptoSalesAreaError... errorTypes) {
		return containsError(invalidMap, objectType, errorTypes, null);
	}


	/**
	 * Checks if the invalidMap contains errors for an object type, excluding the given error types.
	 * (Convenience method.)
	 * 
	 * <p>NOTE (romek May 21, 2011): This method should only be used when there is no need to handle all
	 * kinds of errors for an object differently, otherwise it would rather be ineffective.</p>
	 * 
	 * @param invalidMap
	 * @param objectType the class that the keys of the invalidMap must match
	 * @param exceptions error types to ignore
	 * @return true if found errors, false otherwise
	 * @see #containsError(Map, Object, <ShiptoSalesAreaError>[], ShiptoSalesAreaError[])
	 * @adnlogger.notrace
	 */
	public static boolean containsErrorExcept(Map<Object, List<ShiptoSalesAreaError>> invalidMap, Class<?> objectType,
			ShiptoSalesAreaError... exceptions) {
		return containsError(invalidMap, objectType, null, exceptions);
	}


	/**
	 * Search for errors for an object type in the invalidMap matching the given criterias. Note that
	 * the normal usage would imply either "errorTypes" or "exceptions" being empty, but handling both
	 * criterias in a common function was a design goal, hence this is a private method.
	 * 
	 * @param invalidMap
	 * @param objectType the class that the keys of the invalidMap must match
	 * @param errorTypes error types to check (if empty or null, any error will match)
	 * @param exceptions error types to ignore
	 * @return true if found errors, false otherwise
	 */
	private static boolean containsError(Map<Object, List<ShiptoSalesAreaError>> invalidMap, Class<?> objectType,
			ShiptoSalesAreaError[] errorTypes, ShiptoSalesAreaError[] exceptions) {
		// [[
		boolean result = false;
		Object[][] params = {{"invalidMap", invalidMap},
							{"objectType", objectType},
							{"errorTypes", errorTypes},
							{"exceptions", exceptions}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(InvalidMapSupport.class, "containsError", params);
		}
		// ]]
		try {
			List<ShiptoSalesAreaError> errorTypesList = errorTypes != null ? Arrays.asList(errorTypes) : null;
			List<ShiptoSalesAreaError> exceptionsList = exceptions != null ? Arrays.asList(exceptions) : null;
			
			for (Object object: invalidMap.keySet()) {
				if (!object.getClass().equals(objectType)) {
					continue;
				}
				
				List<ShiptoSalesAreaError> errorsList = invalidMap.get(object);
				
				for (ShiptoSalesAreaError error: errorsList) {
					if (exceptionsList != null && exceptionsList.contains(error)) {
						continue;
					}
					
					if (errorTypesList != null && !errorTypesList.isEmpty()) {
						if (errorTypesList.contains(error)) {
							return result = true;
						}
					}
					else {
						return result = true;
					}
				}
			}
			
			return result = false;
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
	 * This method might disappear with time. It is unclear currently if there is a point to keep shiptoSalesarea errors at all. 
	 * @param invalidMap
	 * @param order
	 * @return
	 */
	public static LinkedList<ValidationErrorReason> getCustorderExceptionFromInvalidMap(
			Map<Object, List<ShiptoSalesAreaError>> invalidMap,
			OrderItemEntity item) {
		
		final LinkedList<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		if (LOG.isDebugEnabled()) {
			LOG.debug("There is a validation error for the new order.");
		}
		
		for (Map.Entry<Object, List<ShiptoSalesAreaError>> error : invalidMap.entrySet()) {
			
			for (ShiptoSalesAreaError err : error.getValue()) {
				if (LOG.isDebugEnabled()) {					
					LOG.debug(String.format(
							"Object class: %s error: %s",
							ClassUtils.getShortClassName(error.getKey(), "null"),
							err));
				}
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(err.getCorrespondingCustorderError(), item, additionalInfo));
			}
		}
		return validationErrorReasons;
	}
	
	/**
	 * This method might disappear with time. It is unclear currently if there is a point to keep shiptoSalesarea errors at all. 
	 * @param invalidMap
	 * @param order
	 * @return
	 */
	public static LinkedList<ValidationErrorReason> getCustorderExceptionFromInvalidMap(
			Map<Object, List<ShiptoSalesAreaError>> invalidMap,
			CustorderItemEntity item) {
		
		final LinkedList<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();

		if (LOG.isDebugEnabled()) {
			LOG.debug("There is a validation error for the new order.");
		}
		
		for (Map.Entry<Object, List<ShiptoSalesAreaError>> error : invalidMap.entrySet()) {
			
			for (ShiptoSalesAreaError err : error.getValue()) {
				if (LOG.isDebugEnabled()) {					
					LOG.debug(String.format(
							"Object class: %s error: %s",
							ClassUtils.getShortClassName(error.getKey(), "null"),
							err));
				}
				String[] additionalInfo = {};
				validationErrorReasons.add(new ValidationErrorReason(err.getCorrespondingCustorderError(), item, additionalInfo));
			}
		}
		return validationErrorReasons;
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.actions;

//import ch.nevis.jcan.log.JcanLogger;
//import ch.nevis.jcan.log.JcanLoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.crh.logon.terminal.web.utils.LoadingpointProvider;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.web.actions.OrderTakingActionSupport;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class TerminalActionSupport extends OrderTakingActionSupport {
    private static final JcanLogger LOG =
       	JcanLoggerFactory.getLogger(TerminalActionSupport.class);
    
    public static final int COUNT_DOWN_SECONDS = 10;
    public static final int COUNT_DOWN_SECONDS_ERROR_LOGIN = 5;
	
    protected LoadingpointEntity getLoadingpoint() {
		// [[
		LoadingpointEntity result1 = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadingpoint");
		}
		// ]]
		try {
			return result1 =  LoadingpointProvider.getLoadingpoint();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
    }
    
    protected PickupLoadingpointEntity getPickupLoadingpoint() {
		// [[
		PickupLoadingpointEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPickupLoadingpoint");
		}
		// ]]
		try {
			result = LoadingpointProvider.getPickupLoadingpoint();
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
	 * @param e
	 * @return
	 */
	protected String getExceptionString(Exception e) {
		String exceptionString = "";
		if (e != null) {
		     exceptionString = ExceptionUtil.exceptionString(e, null);
		}
		return exceptionString;
	}

	public static String getOrderValidationCode(List<OrderEntity> orders) {
		if (orders == null || orders.size() <= 0) {
			return "";
		}
		List<String> orderValidationCodes = new ArrayList<String>();
		for (OrderEntity orderEntity : orders) {
			orderValidationCodes.add(orderEntity.getOrderValidationCode());
		}
		String joinedOrderValidationCodes = StringUtils.join(orderValidationCodes, ",");
		return joinedOrderValidationCodes;
	}

	public static String getOrderNumber(List<OrderEntity> orders) {
		if (orders == null || orders.size() < 1) {
			return null;
		}
		OrderEntity orderEntity = orders.get(0);
		Long orderNumber = orderEntity.getOrderNumber();
		return (orderNumber != null) ? orderNumber.toString() : null;
	}
	
	
	// lajos/14082013: moved functionality from DeliverySettingServiceBean to here without logical change
	/**
	 * Leave only materials having certain package type in a map of delivery settings.  
	 * @param deliverySettingMap
	 * @return
	 */
	public Map<String, DeliverySettingDTO> filterMaterialTypes(Map<String,
			DeliverySettingDTO> deliverySettingMap, List<String> remainingTypes) {
		// [[
		Map<String,DeliverySettingDTO> result = null;
		Object[][] params = {{"deliverySettingMap", deliverySettingMap},
							{"remainingTypes", remainingTypes}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "filterMaterialTypes", params);
		}
		// ]]
		try {
			Map<String, DeliverySettingDTO> lRestrictions = new HashMap<String, DeliverySettingDTO>();
			lRestrictions.putAll(deliverySettingMap);
			Iterator<Entry<String, DeliverySettingDTO>> it = lRestrictions.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, DeliverySettingDTO> entry = it.next();
				DeliverySettingDTO deliverySetting = entry.getValue();
				
				if (!remainingTypes.contains(deliverySetting.getMaterialPackageCd())) {
					it.remove();
				}
			}
			return result = lRestrictions;
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.order.web.utils.chart.ChartUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.PoolAllCarrierEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.CheckboxDropdownEntity;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ChartAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange" 
 * 
 */
public class ChartAjaxService extends DwrServiceSupport {
	
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ChartAjaxService.class);
    
    /**
     * 
     * @param poolIds
     * @param request
     * @param session
     * @return
     */
    public List<CheckboxDropdownEntity> fetchExecCarrierList(String poolIds,
    		HttpServletRequest request,
    		HttpSession session) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"poolIds", poolIds},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchExecCarrierList", params);
		}
		// ]]
		try {    		
    		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
    		final List<CheckboxDropdownEntity> execCarrierList = new ArrayList<CheckboxDropdownEntity>();
    		
    		if (poolIds == null || poolIds.isEmpty()) {
	    		final List<PoolAllCarrierEntity> carrierEntityList = getCarrierService().fetchAllExecutingCarrierList();
	    		for (PoolAllCarrierEntity entity : carrierEntityList) {
	    			final CheckboxDropdownEntity dropdownEntity = new CheckboxDropdownEntity(entity.getCarrierName(), String.valueOf(entity.getCarrierId()));
	    			dropdownEntity.setChecked(false);
	    			execCarrierList.add(dropdownEntity);
	    		}
    		}
    		else {
    			Locale locale = request.getLocale();
    			List<Long> poolList = StringUtils.stringToArrayList(poolIds);
    			
    			List<ExecutingCarrierDTO> distinctCarrierList = getCarrierService().fetchDistinctExecutingCarrierList(poolList, locale);
    			
	    		for (ExecutingCarrierDTO carrier : distinctCarrierList) {
					CheckboxDropdownEntity dropdownEntity = 
							new CheckboxDropdownEntity(carrier.getDisplayName(), String.valueOf(carrier.getCarrierId()));
	    			dropdownEntity.setChecked(false);
	    			execCarrierList.add(dropdownEntity);
	    		}
    		}
    		return result = execCarrierList;
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
     * 
     * @param plantCd
     * @param request
     * @param session
     * @return
     */
    public List<CheckboxDropdownEntity> fetchShippingPointList(String plantCd,
    		HttpServletRequest request,
    		HttpSession session) {
    	// [[
    	List<CheckboxDropdownEntity> result = null;
    	Object[][] params = {{"plantCd", plantCd},
    			{"request", request},
    			{"session", session}};
    	boolean logDebugEnabled = LOG.isDebugEnabled();
    	if (logDebugEnabled) {
    		LOG.entering(this, "fetchExecCarrierList", params);
    	}
    	// ]]
    	try {    		
    		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
    		
    		final Locale locale = request.getLocale();
    		List<CheckboxDropdownEntity> shippingPointList = ChartUtils.getShippingPointList(getBestellungService(), plantCd, false, locale);
			if (shippingPointList != null && shippingPointList.size() > Constant.DROP_DOWN_MAX_SIZE) {
				shippingPointList = shippingPointList.subList(0, Constant.DROP_DOWN_MAX_SIZE);
			}
			return result = shippingPointList;
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
     * 
     * @param transCategCdCd
     * @param request
     * @param session
     * @return
     */
    public List<CheckboxDropdownEntity> fetchPoolList(String transCatCd,
    		HttpServletRequest request,
    		HttpSession session) {
    	// [[
    	List<CheckboxDropdownEntity> result = null;
    	Object[][] params = {{"transCatCd", transCatCd},
    			{"request", request},
    			{"session", session}};
    	boolean logDebugEnabled = LOG.isDebugEnabled();
    	if (logDebugEnabled) {
    		LOG.entering(this, "fetchExecCarrierList", params);
    	}
    	// ]]
    	try {    		
    		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
    		
			final Locale locale = request.getLocale();    		
    		return result = ChartUtils.fetchPoolList(transCatCd, locale, false, getPoolAdminService());
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
     * 
     * @param transCategCdCd
     * @param request
     * @param session
     * @return
     */
    public List<DropdownEntity> fetchPlantList(String companycodeCd,
    		HttpServletRequest request,
    		HttpSession session) {
    	// [[
    	List<DropdownEntity> result = null;
    	Object[][] params = {{"companycodeCd", companycodeCd},
    			{"request", request},
    			{"session", session}};
    	boolean logDebugEnabled = LOG.isDebugEnabled();
    	if (logDebugEnabled) {
    		LOG.entering(this, "fetchExecCarrierList", params);
    	}
    	// ]]
    	try {    		
    		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
    		
    		return result = PlantSareaUtils.getPlantByCompanycodeCd(getPlantSareaService(), companycodeCd);
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

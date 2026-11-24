/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax.masterdata;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ShippingPointService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * AJAX components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ShippingPointAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.web.utils.DropdownEntity"
 */
public class ShippingPointAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointAjaxService.class);

	/**
	 * Loads all existing plants from TLGNC_PLANT. These are shown in plant dropdown on the UI.
	 * @param orderby String
	 * @param asc boolean
	 * @adnlog.exclude return
	 */
	public List<DropdownEntity> loadPlants(String[] query) {
		// [[
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadPlants", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT);
			final List<DropdownEntity> plants = PlantSareaUtils.getPlantList(getPlantSareaService());
			return plants;
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
	 * Loads shipping points from TLGNC_SHIPPINGPOINT for given plant id. These are shown in shipping points dropdown on the UI.
	 * @param query String[]
	 * @adnlog.exclude return
	 */
	public List<ShippingpointEntity> loadShippingPoints(String[] query, HttpServletRequest request, HttpSession session) {
		// [[
		Object[][] params = {{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingPoints", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT);
			String orderby = ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME; //order by name
			
			boolean asc = true;
			if (StringUtils.notEmpty(query[1])) {
				try {
					asc = Boolean.parseBoolean(query[1]);
				}
				catch (Exception e) {
					LOG.error("Error parsing boolean (query[1], asc): " + query[1]);
				}
			}
			
			String plantId = query[2];
			
			boolean allowCreate = false;
			if (StringUtils.notEmpty(query[3])) {
				try {
					allowCreate = Boolean.parseBoolean(query[3]);
				}
				catch (Exception e) {
					LOG.error("Error parsing boolean (query[3], allowCreate): " + query[3]);
				}
			}
			
			ShippingPointService shipPointService = getServiceLocator().getShippingPointService();
			List<ShippingpointEntity> shippingPoints = shipPointService.loadShippingPoints(orderby, asc, plantId);

			if (allowCreate && Authorization.isAllowed(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT_CREATE)) {
				ShippingpointEntity dummyShippingpointEntity = new ShippingpointEntity();
				dummyShippingpointEntity.setShippingpointId(-1L);
				dummyShippingpointEntity.setShippingpointName(Message.lookup(request.getLocale(), "shippingpoint.text.createNewShippingpoint"));
				shippingPoints.add(dummyShippingpointEntity);
			}

			return shippingPoints;
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
	 * Called when Railway Station popup is opened to choose a station for the shipping point 
	 */
	public ListRange fetchRailwayStationList(int start, int count, String orderBy, String query,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRailwayStationList", params);
		}
		// ]]
		try {
			
			//the addition, and removal must be authorized in railway station separately, this is used by a lot of jsps 
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			ListRange listRange = new ListRange();
		
			ShippingPointService spService = getServiceLocator().getShippingPointService();
			List railwayStationList = spService.loadRailwayStations(start, count, orderBy, query);
			listRange.setData(railwayStationList.toArray());
			listRange.setTotalSize(spService.countRailwayStations(query));
			
			return result = listRange;
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

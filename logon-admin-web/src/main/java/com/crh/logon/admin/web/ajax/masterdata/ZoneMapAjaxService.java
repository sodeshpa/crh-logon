/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax.masterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.masterdata.ZoneMapForm;
import com.crh.logon.business.services.interfaces.ZoneMapService;
import com.crh.logon.persistence.entities.common.TrspZoneVertexEntity;
import com.crh.logon.persistence.entities.refdata.TrspZoneBsEntity;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * AJAX components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ZoneMapAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="map" match="java.util.Map"
 */
public class ZoneMapAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ZoneMapAjaxService.class);

	/**
	 * Loads coordinates of all polygons.
	 * @param query String
	 * @adnlog.exclude return
	 */
	public List<List<?>> loadPolygons(String[] query, HttpServletRequest request) {
		// [[
		Object[][] params = {{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadPolygons", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG);
			ZoneMapForm zoneForm = (ZoneMapForm) request.getSession().getAttribute(
					ZoneMapForm.SESSION_ZONEMAPFORM_NAME);
			
			//get polygon coordinates
			ZoneMapService zmService = getServiceLocator().getZoneMapService();
			List<TrspZoneVertexEntity> zoneVertexsList = zmService.loadTrspZoneVertexs();
			
			List<List<?>> list = new ArrayList<List<?>>();
			list.add(zoneForm.getPolygonList());
			list.add(zoneVertexsList);
			
			return list;
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
	* Called to fill trsp zone drop down 
	*/
	public List<TrspZoneBsEntity> fetchTrspZoneList(String[] query, HttpServletRequest request) {
		// [[
		List result = null;
		Object[][] params = {{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTrspZoneList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG);
			ZoneMapForm zoneForm = (ZoneMapForm) request.getSession().getAttribute(
					ZoneMapForm.SESSION_ZONEMAPFORM_NAME);
			return result = zoneForm.getTrspZoneList();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public String savePolygons(Map<String, Map<String, String>> map, HttpServletRequest request) {
		// [[
		String result = null;
		Object[][] params = {{"map", map},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "savePolygons", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG);
			ZoneMapForm zoneForm = (ZoneMapForm) request.getSession().getAttribute(
					ZoneMapForm.SESSION_ZONEMAPFORM_NAME);
			
			//map of list of TrspZoneVertexEntity
			Map vertexMap = new HashMap();
			
			for (Map.Entry<String, Map<String, String>> pairs : map.entrySet()) {
			
				Map<String, String> mapTmp = (Map<String, String>) pairs.getValue();
				List<TrspZoneVertexEntity> listTmp = new ArrayList<TrspZoneVertexEntity>();
				
				int size = 0;
				if (mapTmp != null) {
					size = mapTmp.entrySet().size() / 2;
				}
				
				for (int i = 0; i <  size; i++) {
					TrspZoneVertexEntity vertex = new TrspZoneVertexEntity();
					
					String lat = mapTmp.get(i + "_lat");
					String lng = mapTmp.get(i + "_lng");
					vertex.setCoordLat(Double.valueOf(lat));
					vertex.setCoordLon(Double.valueOf(lng));
					vertex.setSeqNo(Long.valueOf(i));
					
					listTmp.add(vertex);
				}
				
				vertexMap.put(pairs.getKey(), listTmp);
			}
			
			zoneForm.setTrspZoneVertexMap(vertexMap);
			
			return result = "OK";
			
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

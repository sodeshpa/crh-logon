/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax.masterdata;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.ajax.util.OrderBy;
import com.crh.logon.business.services.interfaces.RailTimetableService;
import com.crh.logon.persistence.entities.common.RailDistanceEntity;
import com.crh.logon.persistence.entities.views.RailDistanceViewEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.RailwayStationsUtil;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to
 * AJAX components.
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="RailTimetableAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.web.utils.DropdownEntity"
 */

public class RailTimetableAjaxService extends DwrServiceSupport  {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableAjaxService.class);
	
	public List<DropdownEntity> fetchRailwayStationList(String[] query) {
		List<DropdownEntity> result = null;

		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);
		
		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		
		String stationNameSearchText = null;
		if (query != null && query.length > 0 && StringUtils.notEmpty(query[0])) {
			stationNameSearchText = query[0];
		}
		boolean isActive = true;

		result = RailwayStationsUtil.getRailwayStationList(railTimetableService, stationNameSearchText, isActive);
		return result;
	}
	
	public ListRange searchDistances(
			int skip,
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
			ListRange result = null;
		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);
		
		OrderBy orderBy = new OrderBy(sort, RailDistanceViewEntity.ATTR_DEP_STATION_ID);
		
		String depRailwayStationId = query.get("depStationId");
		String arrRailwayStationId = query.get("arrStationId");

		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		result = railTimetableService.fetchDistanceList(skip, count, depRailwayStationId, arrRailwayStationId, orderBy.getField(), orderBy.isAscending());
		return result;
	}
	
	public ListRange searchTimetables(
			int skip,
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
		ListRange result = null;
		Authorization.authorize(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE);
		
		OrderBy orderBy = new OrderBy(sort, RailDistanceEntity.ATTR_DEP_RAILWAY_STATION_ID);
		
		String depRailwayStationId = query.get("depRailwayStation");
		String arrRailwayStationId = query.get("arrRailwayStation");

		RailTimetableService railTimetableService = getServiceLocator().getRailTimetableService();
		result = railTimetableService.fetchTimetableList(skip, count, depRailwayStationId, arrRailwayStationId, orderBy.getField(), orderBy.isAscending());
		return result;
	}

}

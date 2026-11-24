/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.crh.logon.business.services.interfaces.RailTimetableService;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;

public class RailwayStationsUtil {

	public static List<DropdownEntity> getRailwayStationList(
			RailTimetableService railTimetableService, String stationNameSearchText,
			boolean isActive) {
		List<DropdownEntity> result = null;
		List<RailwayStationEntity> stationList = railTimetableService.fetchRailwayStationList(
				stationNameSearchText, isActive);

		if (stationList == null || stationList.isEmpty()) {
			return result = Collections.EMPTY_LIST;
		}

		result = new ArrayList<DropdownEntity>();
		for (Iterator<RailwayStationEntity> iter = stationList.iterator(); iter.hasNext();) {
			RailwayStationEntity entity = iter.next();

			DropdownEntity dropdownEntity = new DropdownEntity(
					entity.getRailwayStationName().toString(),
					entity.getRailwayStationId().toString());
			result.add(dropdownEntity);
		}
		return result;
	}

}

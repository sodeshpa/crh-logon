/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.crh.logon.business.services.dispatchers.RailTimetableImportServiceDispatcher;
import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import au.com.bytecode.opencsv.bean.MappingStrategy;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.RailTimetableCsvBean;
import com.crh.logon.business.services.localinterfaces.RailTimetableImportServiceLocalInterface;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class RailTimetableImportServiceBean extends RailTimetableImportServiceDispatcher
	implements RailTimetableImportServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableImportServiceBean.class);

	private Long getTimeInMinutes(String value) {
		
		if (value == null) {
			return null;
		}
		String[] units = value.split(":");
		if (units.length != 2) {
			throw new LogonException("Error parsing " + value, Reason.RES_ERROR_UNSPECIFIED);
		}
		long hours = Long.parseLong(units[0]);
		long minutes = Long.parseLong(units[1]);
		
		return hours*60 + minutes;
	}


	private void importBean(RailTimetableCsvBean bean) {

		RailTimetableEntity entity = new RailTimetableEntity();
				
		ReadWriteDao<RailwayStationEntity> railwayStationEntityDao = getDaoProvider().getRailwayStationEntityDao();
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(bean.getRailwayStationCountryFrom())));
		filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, StringUtils.leftPad(bean.getRailwayStationFrom(), (int)RailwayStationEntity.DBSIZE_RAILWAY_STATION_NO, '0')));
		RailwayStationEntity depRailwayStation = railwayStationEntityDao.findSingle(filter);
		if (depRailwayStation != null) {
			entity.setDepRailwayStation(depRailwayStation);
		}
		else {
			throw new LogonException("depRailwayStation not found. ATTR_UIC_COUNTRY_CD=" + bean.getRailwayStationCountryFrom() + " ATTR_RAILWAY_STATION_NO=" + bean.getRailwayStationFrom(), Reason.RES_ERROR_UNSPECIFIED);
		}
		filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(bean.getRailwayStationCountryTo())));
		filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, StringUtils.leftPad(bean.getRailwayStationTo(), (int)RailwayStationEntity.DBSIZE_RAILWAY_STATION_NO, '0')));
		RailwayStationEntity arrRailwayStation = railwayStationEntityDao.findSingle(filter);
		if (arrRailwayStation != null) {
			entity.setArrRailwayStation(arrRailwayStation);	
		}
		else {
			throw new LogonException("arrRailwayStation not found. ATTR_UIC_COUNTRY_CD=" + bean.getRailwayStationCountryFrom() + " ATTR_RAILWAY_STATION_NO=" + bean.getRailwayStationFrom(), Reason.RES_ERROR_UNSPECIFIED);
		}
		entity.setDepTrainNo(StringUtils.isNotEmpty(bean.getTrainNoDeparture())?bean.getTrainNoDeparture():null);
		entity.setArrTrainNo(StringUtils.isNotEmpty(bean.getTrainNoArrival())?bean.getTrainNoArrival():null);
		entity.setDepSwitchingTimeMin(getTimeInMinutes(bean.getSwitchingTimeDeparture()));
		entity.setDepTimeMin(getTimeInMinutes(bean.getDeparture()));
		entity.setArrTimeMin(getTimeInMinutes(bean.getArrival()));
		entity.setArrSwitchingTimeMin(getTimeInMinutes(bean.getSwitchingTimeArrival()));
		entity.setArrOffsetDays(Long.parseLong(bean.getDaysOffset()));
		entity.setTimetableComment(StringUtils.isNotEmpty(bean.getComment())?bean.getComment():null);
		// Only LOGON-exported file will container the activeFlag, therefore it's always true if not present
		entity.setActiveFlag(StringUtils.isEmpty(bean.getActiveFlag())?Boolean.TRUE:Boolean.parseBoolean(bean.getActiveFlag()));
		
		ReadWriteDao<RailTimetableEntity> railTimetableEntityDao = getDaoProvider().getRailTimetableEntityDao();
		railTimetableEntityDao.persist(entity);
	}
	
	/**
	 * The complete dataset is processed within one transaction, this way there's only two possible outcomes:
	 * 1) The timetable is completely replaced by the new timetable.
	 * 2) The timetable has not been altered.
	 * @param timetableData
	 */
	public int importRailTimetable(char[] timetableData) {
		// [[
		int result = 0;
		Object[][] params = {{"timetableData", timetableData}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "importRailTimetable", params);
		}
		// ]]
		try {
			
			HeaderColumnNameTranslateMappingStrategy strat = new HeaderColumnNameTranslateMappingStrategy() {
				public void captureHeader(CSVReader reader) throws IOException {
			        header = reader.readNext();
			        if (header == null){
			        	throw new LogonException("The timetable csv file should not be empty!", Reason.RES_ERROR_CSV_IS_EMPTY);
			        }
			        Map mapping = getColumnMapping();
			        List headerList = Arrays.asList(header);
			        for (Iterator it=mapping.keySet().iterator();it.hasNext();) {
			        	String columnName = (String)it.next();
			        	if ( (!headerList.contains(columnName)) && (!columnName.toLowerCase().contains("optional")) ) {
			        		throw new LogonException("The column '" + columnName + "' is missing", Reason.RES_ERROR_UNSPECIFIED);
			        	}
			        }
			    }
			};
			strat.setType(RailTimetableCsvBean.class);
			final Map map = new HashMap();
			map.put("#Railway_Station_Country_From", "railwayStationCountryFrom");
			map.put("Railway_Station_From", "railwayStationFrom");
			map.put("Railway_Station_From_Name", "railwayStationFromName");
			map.put("Train_no_departure", "trainNoDeparture");
			map.put("Stellz. Abfahrt", "switchingTimeDeparture");
			map.put("Abfahrt", "departure");
			map.put("Railway_Station_Country_To", "railwayStationCountryTo");
			map.put("Railway_Station_To", "railwayStationTo");
			map.put("Railway_Station_To_Name", "railwayStationToName");
			map.put("Train_no_arrival", "trainNoArrival");
			map.put("Tage+", "daysOffset");
			map.put("Ankunft", "arrival");
			map.put("Stellz. Ankunft", "switchingTimeArrival");
			map.put("Comment", "comment");
			map.put("Benutzer", "user");
			map.put("Zeit", "time");
			map.put("Active_in_LOGON_(optional)", "activeFlag");
			strat.setColumnMapping(map);
			CsvToBean csv = new CsvToBean() {
				public List parse(MappingStrategy mapper, Reader reader) {
			        try {
			            CSVReader csv = new CSVReader(reader, ';');
			            mapper.captureHeader(csv);
			            String[] line;
			            List list = new ArrayList();
			            while(null != (line = csv.readNext())) {
			                Object obj = processLine(mapper, line);
			                list.add(obj);
			            }
			            return list;
			        } catch (Exception e) {
			        	if (e instanceof LogonException) {
			        		throw (LogonException)e;
			        	}
			            throw new LogonException("Error parsing CSV", e);
			        }
			    }
			};
			List list = csv.parse(strat, new CharArrayReader(timetableData));
			Query q = em.createQuery("DELETE FROM " + RailTimetableEntity.class.getSimpleName());
			int count = q.executeUpdate();
			LOG.debug(count + " rows removed");
			for (int i=0;i<list.size();i++) {
				importBean((RailTimetableCsvBean)list.get(i));
			}
			
			return result = list.size();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	public List getRailTimetableList() {
		
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Order.asc(RailTimetableEntity.ATTR_RAIL_TIMETABLE_ID));
		
		return getDaoProvider().getRailTimetableEntityDao().find(filter);
	}
}

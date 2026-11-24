/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.beans.DispoDriverBreakBean;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.persistence.entities.common.DriverBreakEntity;
import com.crh.logon.persistence.entities.views.DispoDriverBreakListEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class DriverBreaksUtils {

	/**
	 * Merges oldBreaks and newBreak into a new Break List.
	 * @param newBreak
	 * @param oldBreaks
	 * @return new List of DriverBreakEntities. Entities that have been moved to the 
	 * 			result list are removed  from the oldBreaks list
	 */
	public static List mergeDriverBreaks(DriverBreakEntity newBreak, List oldBreaks){
		List result = new LinkedList();
		//merge with existing list
		Iterator breaks = oldBreaks.iterator();
		boolean merged = false;
		while (breaks.hasNext()){
			DriverBreakEntity dbe = (DriverBreakEntity)breaks.next();
			if (newBreak.getIsLunchBreak().booleanValue()){
				dbe.setIsLunchBreak(Boolean.FALSE);
			}
			if (dbe.getStartDat().before(newBreak.getStartDat())){
				if (dbe.getEndDat().before(newBreak.getStartDat())){
					result.add(dbe);
					breaks.remove();
				}
				else{
					newBreak.setStartDat(dbe.getStartDat());
					newBreak.setEndDat(dbe.getEndDat().before(newBreak.getEndDat())?newBreak.getEndDat():dbe.getEndDat());
					newBreak.setIsLunchBreak(newBreak.getIsLunchBreak().booleanValue() || dbe.getIsLunchBreak().booleanValue());
					if ((dbe.getDescription() == null) || (dbe.getDescription().trim().length() == 0)){
						dbe.setDescription(newBreak.getDescription());
					}
					result.add(newBreak);
					merged = true;
				}
			}
			else {
				if (newBreak.getEndDat().before(dbe.getStartDat())){
					if (!merged){
						merged = true;
						result.add(newBreak);
					}
					result.add(dbe);
					breaks.remove();
				}
				else{
					if (dbe.getIsLunchBreak().booleanValue()){
						newBreak.setIsLunchBreak(Boolean.TRUE);
					}
					if (newBreak.getEndDat().before(dbe.getEndDat())){
						newBreak.setEndDat(dbe.getEndDat());
					}
					if ((newBreak.getDescription() == null) || (newBreak.getDescription().trim().length() == 0)){
						newBreak.setDescription(dbe.getDescription());
					}
					result.add(newBreak);
					merged = true;
				}
			}
		}
		if (!merged){
			result.add(newBreak);
		}
		return result;
	}
			
	/**
	 * Simulates mergeDriverBreaks for dispo, using DispoDriverBreakBean instead of
	 * DriverBreakEntities
	 */
	public static List<DispoDriverBreakBean> mergeDriverBreaks(DispoDriverBreakBean officialLunchBreak, List<DispoDriverBreakBean> currentBreakList){	
		List<DispoDriverBreakBean> result = new LinkedList<DispoDriverBreakBean>();
		
		boolean isLunchBreakMerged = false;
		for (DispoDriverBreakBean currentDriverBreak : currentBreakList){
			//Change the currentDriverBreak's isLunchBreak flag to false to be sure that only one lunchBreak will be available in the result
			if (officialLunchBreak.getIsLunchBreak()){
				currentDriverBreak.setIsLunchBreak(Boolean.FALSE);
			}
			if (currentDriverBreak.getStartDat().before(officialLunchBreak.getStartDat())){
				//If there is a space between the current break and the official lunch then it shouldn't be merged
				if (currentDriverBreak.getEndDat().before(officialLunchBreak.getStartDat())){
					result.add(currentDriverBreak);
				}
				else{
					//If the current driver break is overlapping the official lunch then it should be the part of the official lunch
					boolean officialIsLunchBreak = officialLunchBreak.getIsLunchBreak();
					boolean currentIsLunchBreak = currentDriverBreak.getIsLunchBreak();
					Boolean isLunchBreak = officialIsLunchBreak || currentIsLunchBreak;
					currentDriverBreak.setIsLunchBreak(isLunchBreak);

					if (currentDriverBreak.getEndDat().before(officialLunchBreak.getEndDat())){
						currentDriverBreak.setEndDat(officialLunchBreak.getEndDat());
					}
					else{
						currentDriverBreak.setEndDat(currentDriverBreak.getEndDat());	
					}
					
					if (StringUtils.empty(currentDriverBreak.getDescription())){
						currentDriverBreak.setDescription(officialLunchBreak.getDescription());
					}
					
					officialLunchBreak = currentDriverBreak;
				}
			}
			else {
				//if it isn't overlapping the official lunch break with the current one then it shouldn't be merged
				if (officialLunchBreak.getEndDat().before(currentDriverBreak.getStartDat())){
					//If the official lunch wasn't added to the result then add it 
					if (!isLunchBreakMerged){
						isLunchBreakMerged = true;
						result.add(officialLunchBreak);
					}
					result.add(currentDriverBreak);
				}
				else{
					//if there is an overlapping between the official lunch break and the current break 
					//then merge them together by extending the end of the lunch break to the end of the current break
					if (currentDriverBreak.getIsLunchBreak()){
						officialLunchBreak.setIsLunchBreak(Boolean.TRUE);
					}
					if (officialLunchBreak.getEndDat().before(currentDriverBreak.getEndDat())){
						officialLunchBreak.setEndDat(currentDriverBreak.getEndDat());
					}					
					if (StringUtils.empty(officialLunchBreak.getDescription())){
						officialLunchBreak.setDescription(currentDriverBreak.getDescription());
					}
					result.add(officialLunchBreak);
					isLunchBreakMerged = true;
				}
			}
		}
		//If lunch break does not exists in the result then add it 
		if (!isLunchBreakMerged){
			result.add(officialLunchBreak);
		}
		return result;
	}
	
    /**
     * Returns a list of the breaks from the db for that day for the certain driver. Needs to be queried for the day, as far the lunch break 
     * is by default at the time configured in the configbean, but if there is another break which is a lunch break during the day, then 
     * the default break is not there anymore.
     * @param driverId - the breaks of this driver will be returned
     * @param day - on this day
     * @param dispoService
     * @param configService needed to retrieve the default lunch break time
     * @return
     */
	public static List<DispoDriverBreakListEntity> getBreaksIncludingLunchBreak(Long driverId,
			Date day,
            DispoServiceLocalInterface dispoService, ConfigServiceLocalInterface configService) {
		Iterator<DispoDriverBreakListEntity> it;
        Date date = DateTimeUtils.cleanTimeWithDefaultTimeZone(day);
		Long[] drivers = { driverId };
        
        int dispoDriverLunchBreakStart = configService.getConfigBean().getAppDefaultDispoDriverLunchBreakStart();
        int dispoDriverLunchBreakEnd = configService.getConfigBean().getAppDefaultDispoDriverLunchBreakEnd();
        
        //MOP breaks are already implicitely added to the reported is-times and they have no influecs on estimated times, as they are always in the past
		List<DispoDriverBreakListEntity> breaks = dispoService.fetchDriverBreaks(
				Arrays.asList(drivers), date, false);
        
        if ( breaks == null ) {
			breaks = new ArrayList<DispoDriverBreakListEntity>();
        }
        //lunch breaks are handled differently on dispo, but must be added to the calculated time
        it = breaks.iterator();
        boolean hasLunchBreak = false;
        while (it.hasNext()) {
            DispoDriverBreakListEntity driverBreak = (DispoDriverBreakListEntity) it.next();
            if ( driverBreak.getIsLunchBreak().booleanValue()) {
                hasLunchBreak = true;
            }
        }
        if ( !hasLunchBreak ){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MINUTE, dispoDriverLunchBreakStart);
            Date newStartDate = c.getTime();
            c.setTime(date);
            c.add(Calendar.MINUTE, dispoDriverLunchBreakEnd);
            Date newEndDate = c.getTime();
            
            DispoDriverBreakListEntity newDbe = new DispoDriverBreakListEntity();
            
            newDbe.setStartDat(newStartDate);
            newDbe.setEndDat(newEndDate);
            newDbe.setIsLunchBreak(Boolean.TRUE);
            newDbe.setIsMopBreak(Boolean.FALSE);
            breaks.add(newDbe);
        }
        return breaks;
    }
	
}

/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Calendar;
import java.util.Date;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.MaintRhythmEntity;

/**
 * @author AdNovum Informatik AG
 */
public final class MaintenanceUtil {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaintenanceUtil.class);

	private static int getEarliestNextMaint(MaintRhythmEntity mrEntity, int afterThisYear) {
		// [[
		int result = 0;
		Object[][] params = {{"mrEntity", mrEntity},
							{"afterThisYear", afterThisYear + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MaintenanceUtil.class, "getEarliestNextMaint", params);
		}
		// ]]
		try {
			if ( Boolean.TRUE.equals(mrEntity.getYear1Flag()) && afterThisYear < 1) {
				return result = 1;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear2Flag()) && afterThisYear < 2) {
				return result = 2;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear3Flag()) && afterThisYear < 3) {
				return result = 3;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear4Flag()) && afterThisYear < 4) {
				return result = 4;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear5Flag()) && afterThisYear < 5) {
				return result = 5;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear6Flag()) && afterThisYear < 6) {
				return result = 6;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear7Flag()) && afterThisYear < 7) {
				return result = 7;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear8Flag()) && afterThisYear < 8) {
				return result = 8;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear9Flag()) && afterThisYear < 9) {
				return result = 9;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear10Flag()) && afterThisYear < 10) {
				return result = 10;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear11Flag()) && afterThisYear < 11) {
				return result = 11;
			}
			else if ( Boolean.TRUE.equals(mrEntity.getYear12Flag()) && afterThisYear < 12) {
				return result = 12;
			}
			return result = -1;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static Date getNextMaintDate(Date lastMaintDate, MaintRhythmEntity maintRhythm) {
		// [[
		Date result = null;
		Object[][] params = {{"lastMaintDate", lastMaintDate},
							{"maintRhythm", maintRhythm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MaintenanceUtil.class, "getNextMaintDate", params);
		}
		// ]]
		try {
			if ((lastMaintDate == null) || (maintRhythm == null)) {
				return result = null;
			}
			Date nextMaintDate = null;
			Calendar currentDate = Calendar.getInstance();
			Calendar nextDate;
			
			int afterYear = 0;
			int actPeriod = maintRhythm.getActivationPeriodD().intValue();
			
			while ( afterYear < 12 ) {
				int nextYear = getEarliestNextMaint(maintRhythm, afterYear);
				nextDate = Calendar.getInstance();
				nextDate.setTime(lastMaintDate);
				nextDate.add(Calendar.YEAR, nextYear);
				nextDate.add(Calendar.DAY_OF_MONTH, -actPeriod);
				if (nextDate.after(currentDate) ) {
					nextMaintDate = nextDate.getTime();
					break;
				}
				afterYear++;
			}
			return result = nextMaintDate;
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

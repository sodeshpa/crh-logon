/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;


public enum SalesAreaWebOrderOnWeekendSettings implements Serializable {

	DISABLED(0),
	SATURDAY(1),
	SATURDAY_AND_SUNDAY(2);

	private int numberOfDaysOnWeekend;
	
	private final static int NUMBER_OF_WEEKEND_DAYS = 2;
	
	private SalesAreaWebOrderOnWeekendSettings(int flag) {
		this.numberOfDaysOnWeekend = NUMBER_OF_WEEKEND_DAYS - flag;
	}
	
	/**
	 * @return the numberOfDaysOnWeekend
	 */
	public int getNumberOfDaysOnWeekend() {
		return numberOfDaysOnWeekend;
	}

	public static SalesAreaWebOrderOnWeekendSettings valueOf(Long numberOfWeekendDay) {		
		SalesAreaWebOrderOnWeekendSettings result = DISABLED;
		if(numberOfWeekendDay != null) {
			if(numberOfWeekendDay == 1L) {
				result = SATURDAY;
			} else if(numberOfWeekendDay == 2L) {
				result = SATURDAY_AND_SUNDAY;
			}
		}
		return result;
	}
	
}

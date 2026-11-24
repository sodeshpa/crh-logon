/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author AdNovum Informatik AG
 */
public class OrderTakingEditorRailDeliveryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date actualDeliveryDatArr;
	private Date actualDeliveryDatDep;
	private Date earliestDeliveryDatArr;
	private Date earliestDeliveryDatDep;
	private Date overriddenDeliveryFromDateTime;
	private Date overriddenDeliveryToDateTime;
	private Long distanceMeters;
	private Long durationMinutes;

	/**
	 * User's timezone, in which we have to display the times.
	 */
	private TimeZone userTimeZone;

	/**
	 * 
	 * @param userTimeZone required for formatting the times properly
	 */
	public OrderTakingEditorRailDeliveryDto(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

	public Date getActualDeliveryDatArr() {
		return actualDeliveryDatArr;
	}

	public void setActualDeliveryDatArr(Date railActualDeliveryDatArr) {
		this.actualDeliveryDatArr = railActualDeliveryDatArr;
	}

	public Date getActualDeliveryDatDep() {
		return actualDeliveryDatDep;
	}

	public void setActualDeliveryDatDep(Date railActualDeliveryDatDep) {
		this.actualDeliveryDatDep = railActualDeliveryDatDep;
	}

	public Date getEarliestDeliveryDatArr() {
		return earliestDeliveryDatArr;
	}

	public void setEarliestDeliveryDatArr(Date railEarliestDeliveryDatArr) {
		this.earliestDeliveryDatArr = railEarliestDeliveryDatArr;
	}

	public Date getEarliestDeliveryDatDep() {
		return earliestDeliveryDatDep;
	}

	public void setEarliestDeliveryDatDep(Date railEarliestDeliveryDatDep) {
		this.earliestDeliveryDatDep = railEarliestDeliveryDatDep;
	}

	public Date getOverriddenDeliveryToDateTime() {
		return overriddenDeliveryToDateTime;
	}
	public void setOverriddenDeliveryToDateTime(Date overriddenDeliveryToDateTime) {
		this.overriddenDeliveryToDateTime = overriddenDeliveryToDateTime;
	}

	public Date getOverriddenDeliveryFromDateTime() {
		return overriddenDeliveryFromDateTime;
	}

	public void setOverriddenDeliveryFromDateTime(Date overriddenDeliveryFromDateTime) {
		this.overriddenDeliveryFromDateTime = overriddenDeliveryFromDateTime;
	}

	public Long getDistanceMeters() {
		return distanceMeters;
	}

	public void setDistanceMeters(Long railDistanceMeters) {
		this.distanceMeters = railDistanceMeters;
	}

	public Long getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Long railDurationMinutes) {
		this.durationMinutes = railDurationMinutes;
	}

	public String getFormattedActualDeliveryDatArr() {
		return userTimeZone.fmtAsDayMonthYearHourMinute(actualDeliveryDatArr);
	}

	public String getFormattedActualDeliveryDatDep() {
		return userTimeZone.fmtAsDayMonthYearHourMinute(actualDeliveryDatDep);
	}

	public String getFormattedEarliestDeliveryDatArr() {
		return userTimeZone.fmtAsDayMonthYearHourMinute(earliestDeliveryDatArr);
	}

	public String getFormattedEarliestDeliveryDatDep() {
		return userTimeZone.fmtAsDayMonthYearHourMinute(earliestDeliveryDatDep);
	}

	public String getFormattedOverriddenDeliveryToDateTime() {
		return userTimeZone.fmtAsDayMonthYearHourMinute(overriddenDeliveryToDateTime);
	}

	public String getFormattedOverriddenDeliveryFromDateTime() {
		return userTimeZone.fmtAsDayMonthYearHourMinute(overriddenDeliveryFromDateTime);
	}
}

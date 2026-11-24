/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeParser;

public class ITrackReportFilterParametersDto implements Serializable  {

	public String startDate;
	public String endDate;
	public List<Long> selectedSareaIds;
	public List<Long> selectedShippingPointIds;
	public List<Long>  selectedCommCarrierIds;
	public List<Long> selectedExecCarrierIds;
	public List<Long> selectedShiptoSapBpIds;
	public List<Long> selectedSoldtoSapBpIds;

	public TimeZone userTimeZone;

	public void setUserTimeZone(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<Long> getSelectedSareaIds() {
		return selectedSareaIds;
	}

	public void setSelectedSareaIds(List<Long> selectedSareaIds) {
		this.selectedSareaIds = selectedSareaIds;
	}

	public List<Long> getSelectedShippingPointIds() {
		return selectedShippingPointIds;
	}

	public void setSelectedShippingPointIds(List<Long> selectedShippingPointIds) {
		this.selectedShippingPointIds = selectedShippingPointIds;
	}

	public List<Long> getSelectedCommCarrierIds() {
		return selectedCommCarrierIds;
	}

	public void setSelectedCommCarrierIds(List<Long> selectedCommCarrierIds) {
		this.selectedCommCarrierIds = selectedCommCarrierIds;
	}

	public List<Long> getSelectedExecCarrierIds() {
		return selectedExecCarrierIds;
	}

	public void setSelectedExecCarrierIds(List<Long> selectedExecCarrierIds) {
		this.selectedExecCarrierIds = selectedExecCarrierIds;
	}

	public List<Long> getSelectedShiptoSapBpIds() {
		return selectedShiptoSapBpIds;
	}

	public void setSelectedShiptoSapBpIds(List<Long> selectedShiptoSapBpIds) {
		this.selectedShiptoSapBpIds = selectedShiptoSapBpIds;
	}

	public List<Long> getSelectedSoldtoSapBpIds() {
		return selectedSoldtoSapBpIds;
	}

	public void setSelectedSoldtoSapBpIds(List<Long> selectedSoldtoSapBpIds) {
		this.selectedSoldtoSapBpIds = selectedSoldtoSapBpIds;
	}


	/**
	 * Returns the selected end date in the users timezone
	 * @return
	 */
	public LocalDate getParsedEndDate() {
		try {
			return DateTimeParser.parseLogonDayMonthYearToLocalDate(endDate, userTimeZone.getLocale());
		}
		catch (ParseException e) {
			throw new LogonException(Reason.RES_ERROR_ITRACK_REPORTING_INVALID_DATE);
		}
	}

	/**
	 * Returns the selected start date in the users timezone
	 * @return
	 */
	public LocalDate getParsedStartDate() {
		try {
			return DateTimeParser.parseLogonDayMonthYearToLocalDate(startDate, userTimeZone.getLocale());
		}
		catch (ParseException e) {
			throw new LogonException(Reason.RES_ERROR_ITRACK_REPORTING_INVALID_DATE);
		}
	}

	public void validate() {
		long diffInDays = ChronoUnit.DAYS.between(getParsedStartDate(), getParsedEndDate()) + 1;

		//to prevent overloading force to select at least one filter parameter if the time range is above 60 days
		if (selectedSareaIds.isEmpty() && selectedShiptoSapBpIds.isEmpty() &&
				selectedSoldtoSapBpIds.isEmpty() && selectedShippingPointIds.isEmpty()
				&& selectedCommCarrierIds.isEmpty() && selectedExecCarrierIds.isEmpty() &&
				diffInDays > 60)
			throw new LogonException(Reason.RES_ERROR_ITRACK_REPORTING_FILTER_REQUIRED);

	}
}

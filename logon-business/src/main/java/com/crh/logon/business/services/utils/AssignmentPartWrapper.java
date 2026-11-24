package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoPlanningEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.WagonNumberUtils;

public class AssignmentPartWrapper implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private DispoAssignPartListEntity assignmentPart;
	private List<DispoOrderItemListEntity> orderItems;
	private boolean durationInvalid;
	private Map<Long, String> wagonNumbers;
	private Set<String> distinctWagonNumbers;

	//prescheduling attributes - have effect on UI in case of truck
	private boolean isPreschedulingRequired;
	private boolean isPreschedulingDone;
	private Double freightCost;
	private String freightCurrency;

	//initialized from unload assignment part related prescheduling data
	private Long plannedTruckId;
	private Date plannerUnloadPlannedStart;

	private TimeZone timeZone;
	
	
	public AssignmentPartWrapper(DispoAssignPartListEntity assignmentPart,
			List<DispoOrderItemListEntity> orderItems, TimeZone timeZone) {
		this.assignmentPart = assignmentPart;
		this.orderItems = orderItems;
		durationInvalid = false;
		wagonNumbers = new HashMap<Long, String>();
		distinctWagonNumbers = new HashSet<String>();
		this.timeZone = timeZone;
	}

	public DispoAssignPartListEntity getAssignmentPart() {
		return assignmentPart;
	}

	public List<DispoOrderItemListEntity> getOrderItems() {
		return orderItems;
	}

	public void checkDuration(AssignmentPartWrapper next) {
		if (next ==  null) {
			return;
		}
		// Needed for initial calculation on drive to parts
		boolean preLoadPart = assignmentPart.getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD);
		boolean driveToPart = assignmentPart.getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_TO);
		boolean driveFromPart = assignmentPart.getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_FROM);
		if ((preLoadPart) && assignmentPart.getTransportDistanceM() == null) {
			durationInvalid = true;
		}
		else if (driveToPart && assignmentPart.getTransportDistanceM() == null) {
			durationInvalid = true;
		}
		else if ((driveFromPart) && assignmentPart.getTransportDistanceM() == null) {
			durationInvalid = true;
		} 
		// Recalculation or initial calculation for non drive to parts
		else if (next.getAssignmentPart().getPlannedStart().equals(assignmentPart.getPlannedStart())) {
			durationInvalid =
				!ObjectUtils.equals(next.getAssignmentPart().getShippingpointId(), assignmentPart.getShippingpointId()) ||
				!ObjectUtils.equals(next.getAssignmentPart().getShiptoName(), assignmentPart.getShiptoName()) ||
				!ObjectUtils.equals(next.getAssignmentPart().getCoordLat(), assignmentPart.getCoordLat()) ||
				!ObjectUtils.equals(next.getAssignmentPart().getCoordLon(), assignmentPart.getCoordLon());
		}
	}

	public boolean getDurationInvalid() {
		return durationInvalid;
	}

	public Map<Long, String> getWagonNumbers() {
		return wagonNumbers;
	}

	public Set<String> getDistinctWagonNumbers() {
		return distinctWagonNumbers;
	}

	public void initialize() {
		wagonNumbers = new HashMap<Long, String>();
		distinctWagonNumbers = new HashSet<String>();
		if (orderItems != null) {
			for (DispoOrderItemListEntity doil : orderItems) {
				if (doil.getBrWagonNo() != null) {
					String disp = WagonNumberUtils.toDisplayNo(doil.getBrWagonNo());
					distinctWagonNumbers.add(disp);
					wagonNumbers.put(doil.getOrderItemId(), disp);
				}
			}
		}
	}

	
	public void initPlanningRevenueAttributes(DispoPlanningEntity revenuePlanning) {
		plannedTruckId = revenuePlanning.getTruckId();
		if ( revenuePlanning.getPlannedStart() != null && revenuePlanning.getOrderDeliveryDuration() != null ) {			
			plannerUnloadPlannedStart = new Date(revenuePlanning.getPlannedStart().getTime() + (revenuePlanning.getOrderDeliveryDuration().longValue() * 60 * 1000));
		}

		if ( this.assignmentPart.getCarrierRequiresPlanningFlag() ) {			
			isPreschedulingRequired = true;
			if (revenuePlanning.getPlannedStart() != null ) {
				//it is already planned by the prescheduler, this has to be shown differently on the UI!
				isPreschedulingDone = true;
			}

			freightCost = revenuePlanning.getFreightCost() != null ? revenuePlanning.getFreightCost().doubleValue() : null;
			freightCurrency = revenuePlanning.getFreightCostCurrencyCd();
		}
	}
	
	public boolean isPreschedulingRequired() {
		return isPreschedulingRequired;
	}

	public boolean isPreschedulingDone() {
		return isPreschedulingDone;
	}

	public Double getFreightCost() {
		return freightCost;
	}

	public String getFreightCurrency() {
		return freightCurrency;
	}

	public Long getPlannedTruckId() {
		return plannedTruckId;
	}

	public String getPlannerUnloadPlannedStart() {
		return (plannerUnloadPlannedStart != null) ? DateTimeUtils.formatHoursMinutes(plannerUnloadPlannedStart)  : "";
	}
	
	public boolean isDurationInvalid() {
		return durationInvalid;
	}

	public String getPlannedStartAsDayMonthHourMinute() {
		return timeZone.fmtAsDayMonthHourMinuteTrailingDot(assignmentPart.getPlannedStart());
	}

	public String getDeliveryFrom() {
		return timeZone.fmtAsDayMonthTrailingDot(assignmentPart.getDeliveryFromDat());
	}

	public String getDeliveryTo() {
		return timeZone.fmtAsDayMonthTrailingDot(assignmentPart.getDeliveryToDat());
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import com.crh.logon.business.services.utils.TimeZone;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * Represents a simple vehicle and information about its assignment (if any).
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleAssignmentInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The ID of the simple vehicle.
	 */
	final private Long vehicleId;
	
	/**
	 * The name of the vehicle.
	 * Table: tlgnc_simple_vehicle.VEHICLE_NAME
	 */
	final private String vehicleName;
	
	/**
	 * The registration no. of the vehicle
	 * Table: tlgnc_simple_vehicle.VEHICLE_REG_NO
	 */
	final private String vehicleRegNo;
	
	/**
	 * The type of the vehicle.
	 */
	final private String vehicleType;
	
	/**
	 * The priority of the vehicle
	 */
	final private Long priority;
	
	/**
	 * The tare weight in tons
	 */
	final private BigDecimal tareWeight;
	
	/**
	 * The max gross weight in tons
	 */
	final private BigDecimal maxGrossWeight;
	
	/**
	 * The carrier's name
	 */
	final private String carrierName;
	
	/**
	 * The assignment status in the language of the logged in user
	 */
	final private String assignmentStatus;
	
	/**
	 * The ID of the associated assignment. null if this vehicle doesn't have an active assignment.
	 */
	final private Long assignmentId;
	
	/**
	 * Whether all order within the current assignment have been released (and can thus be loaded).
	 */
	final private boolean released;
	
	/**
	 * Whether an LDS is defined for the shipping point of the current assignment. Influences
	 * the deallocation logic.
	 */
	final private boolean ldsDefined;
	
	/**
	 * The parts belonging to the assignment. Empty if and only if the assignmentId is null.
	 */
	final private Collection<SimpleVehicleAssignmentPart> parts;

	private TimeZone userTimeZone;

	/**
	 * Creates a new simple vehicle assignment.
	 * @param vehicleId The ID of the vehicle entity
	 * @param vehicleName The human readable name of the vehicle
	 * @param vehicleType The human readable type of the vehicle (language dependent)
	 * @param priority The priority of the vehicle
	 * @param tareWeight The tare weight of the vehicle
	 * @param maxGrossWeight The maximal gross weight of the vehicle, or <code>null</code> if not known
	 * @param carrierName The name of the vehicle's carrier
	 * @param assignmentStatus The human readable status of the assignment
	 * (new/shipping/finished), or <code>null</code> if there is no assignment (language dependent)
	 * @param assignmentId The ID of the assignment entity, if the simple vehicle
	 * has an active assignment, or null otherwise.
	 * @param parts The parts of the assignment. This must be null if and only if the assignment is null.
	 * @throws IllegalArgumentException If the vehicle ID is null, or if either <code>assignmentId</code>
	 * or <code>parts</code> is null, but not both.
	 */
	public SimpleVehicleAssignmentInfo(
			Long vehicleId,
			String vehicleName,
			String vehicleRegNo,
			String vehicleType,
			Long priority,
			BigDecimal tareWeight,
			BigDecimal maxGrossWeight,
			String carrierName,
			String assignmentStatus,
			Long assignmentId,
			boolean released,
			boolean ldsDefined,
			Collection<SimpleVehicleAssignmentPart> parts)
	{
		if (vehicleId == null) {
			throw new IllegalArgumentException("vehicleId must not be null");
		}
		if (vehicleName == null) {
			throw new IllegalArgumentException("vehicleName must not be null.");
		}
		if (vehicleType == null) {
			throw new IllegalArgumentException("vehicleType must not be null");
		}
		if (priority == null) {
			throw new IllegalArgumentException("priority must not be null");
		}
		if (tareWeight == null) {
			throw new IllegalArgumentException("tareWeight must not be null");
		}
		if (carrierName == null) {
			throw new IllegalArgumentException("carrierName must not be null");
		}
		if (parts == null) {
			throw new IllegalArgumentException("parts list must not be null -- use an empty list if there are no results");
		}
		if (assignmentId == null && !parts.isEmpty()) {
			throw new IllegalArgumentException("If assignmentId is null then parts must be empty.");
		}
		if (parts.isEmpty() && assignmentId != null) {
			throw new IllegalArgumentException("If an assignmentId is given then the parts list must not be empty.");
		}
		
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.vehicleRegNo = vehicleRegNo;
		this.vehicleType = vehicleType;
		this.priority = priority;
		this.tareWeight = tareWeight;
		this.maxGrossWeight = maxGrossWeight;
		this.carrierName = carrierName;
		this.assignmentStatus = assignmentStatus;
		this.assignmentId = assignmentId;
		this.released = released;
		this.ldsDefined = ldsDefined;
		this.parts = parts;
	}
	
	
	public String getAssignmentStart()
	{
		// TODO (marius Nov 12, 2012): Implement: planned_start of the first assignment part
		return userTimeZone.fmtAsDayMonthYear4(new Date());
	}

	
	public String getAssignmentEnd()
	{
		// TODO (marius Nov 12, 2012): Implement: planned_start of the last assignment part
		return userTimeZone.fmtAsDayMonthYear4(new Date());
	}

	/**
	 * Returns the vehicle whose assignments this object represents.
	 * @return the vehicle; not null
	 */
	public Long getVehicleId() {
		return vehicleId;
	}


	/**
	 * Returns the ID of the associated active assignment, or null
	 * if there is no active assignment for the vehicle
	 * @return the assignmentId; nullable
	 */
	public Long getAssignmentId() {
		return assignmentId;
	}


	/**
	 * Returns true if all orders within the current assignment are
	 * released.
	 * @return
	 */
	public boolean isReleased() {
		return released;
	}


	/**
	 * Returns true if an LDS is defined for the shipping point of the
	 * current assignment
	 * @return
	 */
	public boolean isLdsDefined() {
		return ldsDefined;
	}


	/**
	 * Returns the list of assignment parts
	 * @return the parts; not null if <code>assignmentId</code> is not null
	 */
	public Collection<SimpleVehicleAssignmentPart> getParts() {
		return parts;
	}


	/**
	 * @return the vehicleName; not null
	 */
	public String getVehicleName() {
		return vehicleName;
	}


	/**
	 * @return the vehicleRegNo; not null
	 */
	public String getVehicleRegNo() {
		return vehicleRegNo;
	}


	/**
	 * @return the vehicleType in the user's language; not null
	 */
	public String getVehicleType() {
		return vehicleType;
	}


	/**
	 * @return the priority of the vehicle; not null
	 */
	public Long getPriority() {
		return priority;
	}


	/**
	 * @return the tareWeight of the vehicle; not null
	 */
	public BigDecimal getTareWeight() {
		return tareWeight;
	}


	/**
	 * @return the maxGrossWeight; nullable
	 */
	public BigDecimal getMaxGrossWeight() {
		return maxGrossWeight;
	}


	/**
	 * @return the carrierName; not null
	 */
	public String getCarrierName() {
		return carrierName;
	}


	/**
	 * @return the assignment status in the user's language;
	 * null if there is no assignment for the vehicle
	 */
	public String getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setUserTimeZone(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}
}

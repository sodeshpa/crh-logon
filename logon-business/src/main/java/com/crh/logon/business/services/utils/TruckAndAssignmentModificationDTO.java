/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

public class TruckAndAssignmentModificationDTO implements Serializable {

	private static final long serialVersionUID = 802456492544361944L;

	private final TruckModificationDTO truckModification;
	
	private final AssignmentModificationDTO assignmentModification;
	
	private final Long lastAccessTime;	
		
	/**
	 * @param truckModification
	 * @param assignmentModification
	 * @param lastAccessTime
	 */
	public TruckAndAssignmentModificationDTO(TruckModificationDTO truckModification, AssignmentModificationDTO assignmentModification, Long lastAccessTime) {
		this.truckModification = truckModification;
		this.assignmentModification = assignmentModification;
		this.lastAccessTime = lastAccessTime;
	}

	/**
	 * @return the truckModification
	 */
	public TruckModificationDTO getTruckModification() {
		return truckModification;
	}
	
	/**
	 * @return the assignmentModification
	 */
	public AssignmentModificationDTO getAssignmentModification() {
		return assignmentModification;
	}
	
	/**
	 * @return the lastAccessTime
	 */
	public Long getLastAccessTime() {
		return lastAccessTime;
	}
	
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.List;

public class AssignmentModificationDTO implements Serializable {

	private static final long serialVersionUID = -7846829909076913857L;
	
	private final List<AssignmentWrapper> assignments;

	public AssignmentModificationDTO(List<AssignmentWrapper> assignments) {
		this.assignments = assignments;
	}

	/**
	 * @return the assignments
	 */
	public List<AssignmentWrapper> getAssignments() {
		return assignments;
	}
	
}

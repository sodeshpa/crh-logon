/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.containers;

import java.util.LinkedList;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.AssignmentPartEntity;

public class AssignmentPartContainer {
	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(AssignmentPartContainer.class);
	final List<AssignmentPartEntity> assignmentParts;
	Long sequenceNumber;
	
	public AssignmentPartContainer() {
		assignmentParts = new LinkedList<AssignmentPartEntity>();
		sequenceNumber = 0l;
	}
	
	/**
	 * It adds the given assignmentPart to the container and sets the assignmentPart's sequenceNumber.
	 * @param assignmentPart
	 */
	public void add(AssignmentPartEntity assignmentPart){
		// [[
		Object[][] params = {{"assignmentPart", assignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "add", params);
		}
		// ]]
		try {
			assignmentPart.setAssignmentPartSeqNo(sequenceNumber++);
			assignmentParts.add(assignmentPart);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * It returns with the inner assignmentPart list of the container.
	 * @return List<AssignmentPartEntity>
	 */
	public List<AssignmentPartEntity> getAsList() {
		return assignmentParts;
	}
}

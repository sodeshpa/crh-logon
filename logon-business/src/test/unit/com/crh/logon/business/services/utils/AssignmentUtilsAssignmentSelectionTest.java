/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AssignmentUtilsAssignmentSelectionTest {

	private List<AssignmentEntity> assignments;
	
	@Before
	public void setup() {
		assignments = new ArrayList<AssignmentEntity>();
	}
	
	@Test
	public void fetchAssignmentFromParts() {
		
		// AAA, B, CC, DD
		int[] assignmentGroup = new int[]{3, 1, 2, 2};
		List<AssignmentPartEntity> parts = createAssignmentGroup(assignmentGroup);

		List<AssignmentEntity> fetchedAssignments = AssignmentUtils.fetchAssignmentFromParts(parts);
		
		Assert.assertEquals(assignmentGroup.length, fetchedAssignments.size());
		
		for (int i = 0; i < fetchedAssignments.size(); i++) {
			AssignmentEntity actual = fetchedAssignments.get(i);
			AssignmentEntity expected = assignments.get(i);
			Assert.assertEquals(expected.getAssignmentId(), actual.getAssignmentId());
		}
		
	}
	
	@Test
	public void testProperAmountOfAssignmentsCreated() {
		
		// AAA, B, CC, DD
		int[] assignmentGroup = new int[]{3, 1, 2, 2};
		List<AssignmentPartEntity> parts = createAssignmentGroup(assignmentGroup);
		
		Assert.assertEquals(8, parts.size());
		
	}
	
	@Test
	public void testProperAmountOfPartsCreated() {
		
		// AAA, B, CC, DD
		int[] assignmentGroup = new int[]{3, 1, 2, 2};
		createAssignmentGroup(assignmentGroup);
		
		Assert.assertEquals(0L, assignments.get(0).getAssignmentId().longValue());
		Assert.assertEquals(1L, assignments.get(1).getAssignmentId().longValue());
		Assert.assertEquals(2L, assignments.get(2).getAssignmentId().longValue());
		Assert.assertEquals(3L, assignments.get(3).getAssignmentId().longValue());
		
	}

	/**
	 * @param assignmentGroup
	 * @return
	 */
	private List<AssignmentPartEntity> createAssignmentGroup(int[] assignmentGroup) {
		
		List<AssignmentPartEntity> parts = new ArrayList<AssignmentPartEntity>();
		
		for (int i = 0; i < assignmentGroup.length; i++) {
			
			int countOfPartsIn = assignmentGroup[i];
			
			AssignmentEntity assignment = new AssignmentEntity();
			
			List<AssignmentPartEntity> partsForCurrentAssignment = new ArrayList<AssignmentPartEntity>();
			
			for (int j = 0; j < countOfPartsIn; j++) {
				AssignmentPartEntity part = new AssignmentPartEntity();
				part.setAssignment(assignment);
				partsForCurrentAssignment.add(part);
				parts.add(part);
			}
			
			assignment.setAssignmentParts(partsForCurrentAssignment);
			assignment.setAssignmentId(Long.valueOf(""+i));
			assignment.setManuallyScheduled(Boolean.TRUE);
			
			assignments.add(assignment);
			
		}
		
		return parts;
	}
	
}

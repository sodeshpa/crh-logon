/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AssignmentUtilsPartFindTest {

	private long [] singleDeliveryTypes = new long[] {
			Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD,
			Constant.ASSIGNMENT_PART_TYPE_LOAD, 
			Constant.ASSIGNMENT_PART_TYPE_TO, 
			Constant.ASSIGNMENT_PART_TYPE_UNLOAD, 
			Constant.ASSIGNMENT_PART_TYPE_FROM, 
			Constant.ASSIGNMENT_PART_TYPE_FINISHED
	};
	
	private long [] multiDropTypes = new long[] {
			Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD,
			Constant.ASSIGNMENT_PART_TYPE_LOAD,
			Constant.ASSIGNMENT_PART_TYPE_LOAD, 
			Constant.ASSIGNMENT_PART_TYPE_TO, 			
			Constant.ASSIGNMENT_PART_TYPE_UNLOAD,
			Constant.ASSIGNMENT_PART_TYPE_TO,
			Constant.ASSIGNMENT_PART_TYPE_UNLOAD,
			Constant.ASSIGNMENT_PART_TYPE_FROM, 
			Constant.ASSIGNMENT_PART_TYPE_FINISHED
	};
	
	private AssignmentEntity singleDeliveryAssignment ;
	private AssignmentEntity multiDropAssignment;
	
	@Before
	public void setup() {
		singleDeliveryAssignment = new AssignmentEntity();
		multiDropAssignment = new AssignmentEntity();
		initAssignment(singleDeliveryAssignment, singleDeliveryTypes);
		initAssignment(multiDropAssignment, multiDropTypes);
	}

	private void initAssignment(AssignmentEntity assignmentEntity, long[] types) {
		List<AssignmentPartEntity> assignmentParts = new ArrayList<AssignmentPartEntity>();
		for (Long type : types) {
			assignmentParts.add(createPartWithType(type, assignmentEntity));
		}
		assignmentEntity.setAssignmentParts(assignmentParts);
	}

	@Test
	public void testFindMissingType() {

			AssignmentPartEntity found = AssignmentUtils.findAssignmentPartByType(singleDeliveryAssignment, -1L);
			Assert.assertNull(found);
		
	}
	
	@Test
	public void testFindByType() {

		for (Long type : singleDeliveryTypes) {
			AssignmentPartEntity found = AssignmentUtils.findAssignmentPartByType(singleDeliveryAssignment, type);
			Assert.assertEquals(type.longValue(), found.getAssignmentPartType().getAssignmentPartTypeCd().longValue());
		}
		
	}
	
	private AssignmentPartEntity createPartWithType(Long code, AssignmentEntity assignmentEntity) {
		
		AssignmentPartEntity partEntity = new AssignmentPartEntity();
		
		AssignmentPartTypeBsEntity assignmentPartType = new AssignmentPartTypeBsEntity();
		assignmentPartType.setAssignmentPartTypeCd(code);
		
		partEntity.setAssignmentPartType(assignmentPartType);
		partEntity.setAssignment(assignmentEntity);
		
		return partEntity;
	}
	
}
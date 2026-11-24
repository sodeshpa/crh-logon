/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.order.web.utils.TourPart;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AssignmentEditActionTest {

	@Test
	public void testFullScenario() {

		// Assignments
		long[] codes = new long[]{0, 1, 1, 2, 3, 2, 3, 4, 5};
		List<AssignmentPartEntity> parts = createListByTypeCodes(codes);
		
		// Tourparts
		int[] tourCodes = new int[]{0, 1, 1, 2, 2, 3};
		Long[] distances = new Long[]{5L, null, null, 10L, 20L, 35L};
		List<TourPart> tourParts = getTourParts(tourCodes, distances);
		
		AssignmentEditAction.updateAssignmentPartDistancesFromTourParts(tourParts, parts);
		
		Assert.assertEquals(5L, parts.get(0).getTransportDistanceM().longValue());
		Assert.assertNull(parts.get(1).getTransportDistanceM());
		Assert.assertNull(parts.get(2).getTransportDistanceM());
		Assert.assertEquals(10L, parts.get(3).getTransportDistanceM().longValue());
		Assert.assertEquals(20L, parts.get(5).getTransportDistanceM().longValue());
		Assert.assertEquals(35L, parts.get(7).getTransportDistanceM().longValue());
		
	}
	
	@Test
	public void testWithoutPreScenario() {

		// Assignments
		long[] codes = new long[]{1, 1, 2, 3, 2, 3, 4, 5};
		List<AssignmentPartEntity> parts = createListByTypeCodes(codes);
		
		// Tourparts
		int[] tourCodes = new int[]{1, 1, 2, 2, 3};
		Long[] distances = new Long[]{null, null, 10L, 20L, 35L};
		List<TourPart> tourParts = getTourParts(tourCodes, distances);
		
		AssignmentEditAction.updateAssignmentPartDistancesFromTourParts(tourParts, parts);
		
		Assert.assertNull(parts.get(0).getTransportDistanceM());
		Assert.assertNull(parts.get(1).getTransportDistanceM());
		Assert.assertEquals(10L, parts.get(2).getTransportDistanceM().longValue());
		Assert.assertEquals(20L, parts.get(4).getTransportDistanceM().longValue());
		Assert.assertEquals(35L, parts.get(6).getTransportDistanceM().longValue());
		
	}
	
	@Test
	public void testWithoutRetScenario() {

		// Assignments
		long[] codes = new long[]{0, 1, 1, 2, 3, 2, 3};
		List<AssignmentPartEntity> parts = createListByTypeCodes(codes);
		
		// Tourparts
		int[] tourCodes = new int[]{0, 1, 1, 2, 2};
		Long[] distances = new Long[]{5L, null, null, 10L, 20L};
		List<TourPart> tourParts = getTourParts(tourCodes, distances);
		
		AssignmentEditAction.updateAssignmentPartDistancesFromTourParts(tourParts, parts);
		
		Assert.assertEquals(5L, parts.get(0).getTransportDistanceM().longValue());
		Assert.assertNull(parts.get(1).getTransportDistanceM());
		Assert.assertNull(parts.get(2).getTransportDistanceM());
		Assert.assertEquals(10L, parts.get(3).getTransportDistanceM().longValue());
		Assert.assertEquals(20L, parts.get(5).getTransportDistanceM().longValue());
		
	}

	@Test 
	public void testCreateListByTypeCodes() {
		
		long[] codes = new long[]{0, 1, 1, 2, 3, 2, 3, 4, 5};
		List<AssignmentPartEntity> parts = createListByTypeCodes(codes);
		for (int i = 0; i < parts.size(); i++) {
			AssignmentPartEntity actual = parts.get(i);
			AssignmentPartTypeBsEntity assignmentPartType = actual.getAssignmentPartType();
			Assert.assertEquals(codes[i], assignmentPartType.getAssignmentPartTypeCd().longValue());
		}
	}
	
	@Test
	public void testGetTourParts() {
		
		int[] codes = new int[]{0, 1, 1, 2, 2, 3};
		Long[] distances = new Long[]{5L, null, null, 10L, 20L, 35L};
		
		List<TourPart> tourParts = getTourParts(codes, distances);
		
		Assert.assertEquals(5L, tourParts.get(0).getTransportDistanceM().longValue());
		Assert.assertNull(tourParts.get(1).getTransportDistanceM());
		Assert.assertNull(tourParts.get(2).getTransportDistanceM());
		Assert.assertEquals(10L, tourParts.get(3).getTransportDistanceM().longValue());
		Assert.assertEquals(20L, tourParts.get(4).getTransportDistanceM().longValue());
		Assert.assertEquals(35L, tourParts.get(5).getTransportDistanceM().longValue());
		
	}

	// ---
	
	private List<TourPart> getTourParts(int[] codes, Long[] distances) {
		
		List<TourPart> tourParts = new ArrayList<TourPart>();
		
		for (int i = 0; i < codes.length; i++) {
			tourParts.add(getTourPart(codes[i], distances[i]));
		}
		
		return tourParts;
	}

	public TourPart getTourPart(int type, Long distance) {
		TourPart tourPart = new TourPart(null, null, distance);
		tourPart.setType(type);
		return tourPart;
	}
	
	public List<AssignmentPartEntity> createListByTypeCodes(long[] codes) {
		
		List<AssignmentPartEntity> list = new ArrayList<AssignmentPartEntity>();
		
		for (long i : codes) {
			AssignmentPartEntity entity = new AssignmentPartEntity();
			AssignmentPartTypeBsEntity assignmentPartType = new AssignmentPartTypeBsEntity();
			assignmentPartType.setAssignmentPartTypeCd(Long.valueOf(i));
			entity.setAssignmentPartType(assignmentPartType);
			list.add(entity);
		}
		
		return list;
	}
	
}

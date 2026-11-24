/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.unittest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class TestCollectionContains {
	
	private static final int COUNT = 100000;
	
	private static List<SapMaterialEntity> list = new ArrayList<SapMaterialEntity>();
	
	private static final Long findSapMatId = new Long(999);
	
	class SapMaterialEntityComparator implements Comparator<SapMaterialEntity> {
		public int compare(SapMaterialEntity mat1, SapMaterialEntity mat2) {
			return mat1.getSapMaterialId().compareTo(mat2.getSapMaterialId());
		}
	}

	@BeforeClass
	public static void setUpEntityList() {
		for (int i = 0; i < COUNT; i++) {
			SapMaterialEntity sapMaterial = new SapMaterialEntity();
			sapMaterial.setSapMaterialId(new Long(i+1));
			list.add(sapMaterial);
		}
	}
	
	@Test
	public void findWithLinearSearch() throws Exception {
		SapMaterialEntity result = null;
		
		for (SapMaterialEntity sapMaterialEntity : list) {
			if (sapMaterialEntity.getSapMaterialId().equals(findSapMatId)) {
				result = sapMaterialEntity;
				break;
			}
		}
		
		if (result == null) {
			Assert.fail("Cannot find item!");
		}
	}
	
	@Test
	public void findWithBinarySearch() throws Exception {
		SapMaterialEntity dummyObject = new SapMaterialEntity();
		dummyObject.setSapMaterialId(findSapMatId);
		
		Collections.sort(list, new SapMaterialEntityComparator());
		
		int index = Collections.binarySearch(list, dummyObject,
				new SapMaterialEntityComparator());

		if (index == -1) {
			Assert.fail("Cannot find item!");
		}
	}
	
	@Test
	public void findWithMap() {
		// Create map
		Map<Long, SapMaterialEntity> map = new HashMap<Long, SapMaterialEntity>();
		
		//Populate map
		for (SapMaterialEntity sapMaterialEntity : list) {
			map.put(sapMaterialEntity.getSapMaterialId(), sapMaterialEntity);
		}
		
		// Get item
		SapMaterialEntity result = map.get(findSapMatId);

		if (result == null) {
			Assert.fail("Cannot find item!");
		}
	}
	
	@Test
	public void createMap() {
		Map<Long, SapMaterialEntity> map = new HashMap<Long, SapMaterialEntity>();
		
		for (SapMaterialEntity sapMaterialEntity : list) {
			map.put(sapMaterialEntity.getSapMaterialId(), sapMaterialEntity);
		}
		
		if (map.size() != list.size()) {
			Assert.fail("Cannot build map!");
		}
	}
}

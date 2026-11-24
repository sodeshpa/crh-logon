/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;

/**
 * @author AdNovum Informatik AG
 */
public class TestTruckWagonTypeWrapperUtil {
	
	static List<TruckWagonTypeWrapper> list0 = new ArrayList<TruckWagonTypeWrapper>();
	static List<TruckWagonTypeWrapper> list1 = new ArrayList<TruckWagonTypeWrapper>();
	static List<TruckWagonTypeWrapper> list2 = new ArrayList<TruckWagonTypeWrapper>();
	static List<TruckWagonTypeWrapper> list3 = new ArrayList<TruckWagonTypeWrapper>();
	static List<TruckWagonTypeWrapper> list4 = new ArrayList<TruckWagonTypeWrapper>();
	static List<TruckWagonTypeWrapper> list5 = new ArrayList<TruckWagonTypeWrapper>();
	
	@BeforeClass
	public static void setupClass() {
		
		list0.add(getTruckWagonTypeWrapper(0L));
		list0.add(getTruckWagonTypeWrapper(0L));
		list0.add(getTruckWagonTypeWrapper(0L));
		
		list1.add(getTruckWagonTypeWrapper(2L));
		list1.add(getTruckWagonTypeWrapper(4L));
		list1.add(getTruckWagonTypeWrapper(1L));
		list1.add(getTruckWagonTypeWrapper(3L));
		
		list2.add(getTruckWagonTypeWrapper(2L));
		list2.add(getTruckWagonTypeWrapper(0L));
		list2.add(getTruckWagonTypeWrapper(4L));
		list2.add(getTruckWagonTypeWrapper(1L));
		list2.add(getTruckWagonTypeWrapper(3L));
		
		list3.add(getTruckWagonTypeWrapper(2L, 35L));
		list3.add(getTruckWagonTypeWrapper(0L, 30L));
		list3.add(getTruckWagonTypeWrapper(4L, 30L));
		list3.add(getTruckWagonTypeWrapper(2L, 40L));
		list3.add(getTruckWagonTypeWrapper(3L, 30L));
		
		list4.add(getTruckWagonTypeWrapper(2L, 35L));
		list4.add(getTruckWagonTypeWrapper(0L, 30L));
		list4.add(getTruckWagonTypeWrapper(2L, 40L, 10L));
		list4.add(getTruckWagonTypeWrapper(2L, 40L));
		list4.add(getTruckWagonTypeWrapper(4L, 30L));
		list4.add(getTruckWagonTypeWrapper(2L, 40L, 11L));
		list4.add(getTruckWagonTypeWrapper(3L, 30L));
		
		list5.add(getTruckWagonTypeWrapper(-1L, 25L));
		list5.add(getTruckWagonTypeWrapper(2L, 35L));
		list5.add(getTruckWagonTypeWrapper(0L, 30L));
		list5.add(getTruckWagonTypeWrapper(2L, 40L, 10L));
		list5.add(getTruckWagonTypeWrapper(2L, 40L));
		list5.add(getTruckWagonTypeWrapper(4L, 30L));
		list5.add(getTruckWagonTypeWrapper(-1L, 30L));
		list5.add(getTruckWagonTypeWrapper(2L, 40L, 11L));
		list5.add(getTruckWagonTypeWrapper(3L, 30L));
		
	}

	@Test
	public void testZeroIsPermitted() {
		
		TruckWagonTypeWrapper findProperTruck = TruckWagonTypeWrapperUtil.findProperTruck(list0);
		Assert.assertNull(findProperTruck);
		
	}
	
	@Test
	public void passingEmptyListReturnsNull()
	{
		Assert.assertNull(TruckWagonTypeWrapperUtil.findProperTruck(new LinkedList<TruckWagonTypeWrapper>()));
	}
	
	@Test
	public void testUseHighestImportance() {
		
		TruckWagonTypeWrapper findProperTruck = TruckWagonTypeWrapperUtil.findProperTruck(list1);
		Assert.assertEquals(1L, findProperTruck.getPriority());
		
	}
	
	@Test
	public void testSkipZero() {
		
		TruckWagonTypeWrapper findProperTruck = TruckWagonTypeWrapperUtil.findProperTruck(list2);
		Assert.assertEquals(1L, findProperTruck.getPriority());
		
	}
	
	@Test
	public void testForSamePrioUseBiggerWeight() {
		
		TruckWagonTypeWrapper findProperTruck = TruckWagonTypeWrapperUtil.findProperTruck(list3);
		Assert.assertEquals(2L, findProperTruck.getPriority());
		Assert.assertEquals(40L, findProperTruck.getMaxGrossWeightT().longValue());
		
	}
	
	@Test
	public void testForSameWeightUseBiggerVolume() {
		
		TruckWagonTypeWrapper findProperTruck = TruckWagonTypeWrapperUtil.findProperTruck(list4);
		Assert.assertEquals(2L, findProperTruck.getPriority());
		Assert.assertEquals(11L, findProperTruck.getVolumeM3().longValue());
		
	}
	
	@Test
	public void testMinusOneIsMaxPrio() {
		
		TruckWagonTypeWrapper findProperTruck = TruckWagonTypeWrapperUtil.findProperTruck(list5);
		Assert.assertEquals(2L, findProperTruck.getPriority());
		Assert.assertEquals(11L, findProperTruck.getVolumeM3().longValue());
		
	}
	
	private static TruckWagonTypeWrapper getTruckWagonTypeWrapper(long importance) {
		return getTruckWagonTypeWrapper(importance, 0L, null); 
	}
	
	private static TruckWagonTypeWrapper getTruckWagonTypeWrapper(long importance, long grossWeightTon) {
		return getTruckWagonTypeWrapper(importance, grossWeightTon, null);
	}
	
	private static TruckWagonTypeWrapper getTruckWagonTypeWrapper(long importance, final long grossWeightTon, final Long volume) {
		
		TruckWagonTypeWrapper wrapper = new TruckWagonTypeWrapper(importance, Long.MIN_VALUE) {
			
			@Override
			public BigDecimal getVolumeM3() {
				return volume == null ? null : new BigDecimal(volume);
			}
			
			@Override
			public BigDecimal getMaxGrossWeightT() {
				return new BigDecimal(grossWeightTon);
			}
		};
		
		return wrapper;
		
	}
	
}
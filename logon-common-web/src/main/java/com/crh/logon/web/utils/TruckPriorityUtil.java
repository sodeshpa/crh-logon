/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import com.crh.logon.constants.Constant;

/**
 * Provides Truck Priority related functionality.
 *  
 * @author AdNovum Informatik AG
 */
public class TruckPriorityUtil {
	
	/**
	 * Creates an array of numbers - possible Truck priorities.
	 * This can be used to create dropdown on the UI.
	 * @return long[] - containing the possible Truck priorities
	 */
	public static long[] createTruckPriorityArray() {
		/* 
		 * Priority has inverted meaning. 
		 * The minimum priority value (99) is the higher number, and the maximum (1) is the lower. 
		 */
		int min = new Long(Constant.TRUCK_PRIORITY_MIN).intValue();
		int max = new Long(Constant.TRUCK_PRIORITY_MAX).intValue();

		// We have to include the "inactive" value besides 1-99.
		int finalSize = min + 1;
		long[] truckPriorities = new long[finalSize];

		truckPriorities[0] = Constant.TRUCK_PRIORITY_TRUCK_INACTIVE;

		for (int i = max; i <= min; i++) {
			truckPriorities[i] = i;
		}

		return truckPriorities;
	}
	
	/**
	 * Creates an array of numbers - possible Simple Vehicle priorities.
	 * This can be used to create dropdown on the UI.
	 * @return long[] - containing the possible Simple Vehicle priorities
	 */
	public static long[] createSimpleVehiclePriorityArray() {
		/* 
		 * Priority has inverted meaning. 
		 * The minimum priority value (99) is the higher number, and the maximum (0) is the lower. 
		 */
		int min = new Long(Constant.SIMPLE_VEHICLE_PRIORITY_MIN).intValue();
		int max = new Long(Constant.SIMPLE_VEHICLE_PRIORITY_MAX).intValue();

		int finalSize = min+1;
		long[] priorities = new long[finalSize];
		
		for (int i = max; i <= min; i++) {
			priorities[i] = i;
		}

		return priorities;
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;

/**
 * This static class shall be used to check states in a specific interval (ex. after delivered, before loaded, etc). 
 * This makes it easier mainainable and quickly understandable in the code.
 * 
 * Please add new, easy and clear understandable status ranges when needed!
 * 
 * @author AdNovum Informatik AG
 */
public class LogonStatusUtils {
	
	/* Prepared order status */
//	set.add(Constant.ORDER_STATUS_NEW); 						// 1
//	set.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL); 		// 2
//	set.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);		// 3
//	set.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);		// 4
//	set.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);	// 5
//	set.add(Constant.ORDER_STATUS_DISPATCHED);					// 6
//	set.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);		// 7
//	set.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK);	// 8
//	set.add(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);		// 9
//	set.add(Constant.ORDER_STATUS_LOADING);						// 10
//	set.add(Constant.ORDER_STATUS_LOADED);						// 11
//	set.add(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);			// 18
//	set.add(Constant.ORDER_STATUS_SHIPPING);					// 12
//	set.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);	// 13
//	set.add(Constant.ORDER_STATUS_UNLOADING);					// 14
//	set.add(Constant.ORDER_STATUS_DELIVERED);					// 15
//	set.add(Constant.ORDER_STATUS_FINISHED);					// 16
//	set.add(Constant.ORDER_STATUS_COMPLETED);					// 16
//	set.add(Constant.ORDER_STATUS_DELETED);						// 17
	
	
	/**
	 * For more convenient operations on a set of order status codes.
	 */
	private static class OrderStatusSet extends HashSet<Long> {
		
		private static final long serialVersionUID = 1L;
		
		public OrderStatusSet() {
			super();
		}
		
		public OrderStatusSet(Collection<Long> c) {
			super(c);
		}
		
		public boolean contains(OrderStatusBsEntity orderStatus) {
			Long orderStatusCd = orderStatus.getOrderStatusCd();
			return contains(orderStatusCd);
		}
	}
	
	private final static OrderStatusSet beforeDispatched;
	private final static OrderStatusSet dispatchedOrBefore;
	private final static OrderStatusSet beyondDispatched;
	private final static OrderStatusSet dispatchedOrBeyond;
	
	private final static OrderStatusSet beforeArrivedAtLoadingPoint;
	private final static OrderStatusSet arrivedAtLoadingPointOrBeyond;
	
	private final static OrderStatusSet beforeLoading;
	private final static OrderStatusSet loadingOrBefore;
	private final static OrderStatusSet beyondLoading;
	private final static OrderStatusSet loadingOrBeyond;
	
	private final static OrderStatusSet beforeLoaded;
	private final static OrderStatusSet loadedOrBefore;
	private final static OrderStatusSet beyondLoaded;
	private final static OrderStatusSet loadedOrBeyond;
	
	private final static OrderStatusSet beforeArrivedAtUnloadingPoint;
	private final static OrderStatusSet arrivedAtUnloadingPointOrBefore;
	private final static OrderStatusSet beyondArrivedAtUnloadingPoint;
	private final static OrderStatusSet arrivedAtUnloadingPointOrBeyond;
	
	private final static OrderStatusSet beforeDelivered;
	private final static OrderStatusSet deliveredOrBefore;
	private final static OrderStatusSet beyondDelivered;
	private final static OrderStatusSet deliveredOrBeyond;
	
	private final static OrderStatusSet beyondDispatchedAndDeliveredOrBefore;
	
	static {
		beforeDispatched = new OrderStatusSet();
		beforeDispatched.add(Constant.ORDER_STATUS_NEW);                                            //  1
		beforeDispatched.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);                            //  2
		beforeDispatched.add(Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);                        //  3
		beforeDispatched.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);                         //  4
		beforeDispatched.add(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);                     //  5
		
		dispatchedOrBefore = new OrderStatusSet(beforeDispatched);
		dispatchedOrBefore.add(Constant.ORDER_STATUS_DISPATCHED);                                   //  6
		
		beyondDispatched = new OrderStatusSet();
		
		beyondDispatched.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);                          //  7
		beyondDispatched.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK);                      //  8
		beyondDispatched.add(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);                        //  9
		beyondDispatched.add(Constant.ORDER_STATUS_LOADING);                                        // 10
		beyondDispatched.add(Constant.ORDER_STATUS_LOADED);                                         // 11
		beyondDispatched.add(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);                            // 18
		beyondDispatched.add(Constant.ORDER_STATUS_SHIPPING);                                       // 12
		beyondDispatched.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);                     // 13
		beyondDispatched.add(Constant.ORDER_STATUS_UNLOADING);                                      // 14
		beyondDispatched.add(Constant.ORDER_STATUS_DELIVERED);                                      // 15
		beyondDispatched.add(Constant.ORDER_STATUS_FINISHED);                                       // 16
		beyondDispatched.add(Constant.ORDER_STATUS_DELETED);                                        // 17

		dispatchedOrBeyond = new OrderStatusSet(beyondDispatched);
		dispatchedOrBeyond.add(Constant.ORDER_STATUS_DISPATCHED);                                   //  6
		
		beforeArrivedAtLoadingPoint = new OrderStatusSet(dispatchedOrBefore);
		beforeArrivedAtLoadingPoint.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);                             //  7
		beforeArrivedAtLoadingPoint.add(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK);                         //  8
		
		beforeLoading = new OrderStatusSet(beforeArrivedAtLoadingPoint);
		beforeLoading.add(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);                           //  9
		
		loadingOrBefore = new OrderStatusSet(beforeLoading);
		loadingOrBefore.add(Constant.ORDER_STATUS_LOADING);                                         // 10
		
		beyondLoading = new OrderStatusSet();
		beyondLoading.add(Constant.ORDER_STATUS_LOADED);                                            // 11
		beyondLoading.add(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);                               // 18
		beyondLoading.add(Constant.ORDER_STATUS_SHIPPING);                                          // 12
		beyondLoading.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);                        // 13
		beyondLoading.add(Constant.ORDER_STATUS_UNLOADING);                                         // 14
		beyondLoading.add(Constant.ORDER_STATUS_DELIVERED);                                         // 15
		beyondLoading.add(Constant.ORDER_STATUS_FINISHED);                                          // 16
		beyondLoading.add(Constant.ORDER_STATUS_DELETED);                                           // 17
		
		loadingOrBeyond = new OrderStatusSet(beyondLoading);
		loadingOrBeyond.add(Constant.ORDER_STATUS_LOADING);                                         // 10
		
		arrivedAtLoadingPointOrBeyond = new OrderStatusSet(loadingOrBeyond);
		arrivedAtLoadingPointOrBeyond.add(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
		
		beforeLoaded = new OrderStatusSet(loadingOrBefore);
		
		loadedOrBefore = new OrderStatusSet(beforeLoaded);
		loadedOrBefore.add(Constant.ORDER_STATUS_LOADED);                                           // 11
		
		beyondLoaded = new OrderStatusSet();
		beyondLoaded.add(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);                                // 18
		beyondLoaded.add(Constant.ORDER_STATUS_SHIPPING);                                           // 12
		beyondLoaded.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);                         // 13
		beyondLoaded.add(Constant.ORDER_STATUS_UNLOADING);                                          // 14
		beyondLoaded.add(Constant.ORDER_STATUS_DELIVERED);                                          // 15
		beyondLoaded.add(Constant.ORDER_STATUS_FINISHED);                                           // 16
		beyondLoaded.add(Constant.ORDER_STATUS_DELETED);                                            // 17
		
		loadedOrBeyond = new OrderStatusSet(beyondLoaded);
		loadedOrBeyond.add(Constant.ORDER_STATUS_LOADED);
		
		
		beforeArrivedAtUnloadingPoint = new OrderStatusSet(loadedOrBefore);
		beforeArrivedAtUnloadingPoint.add(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);               // 18
		beforeArrivedAtUnloadingPoint.add(Constant.ORDER_STATUS_SHIPPING);                          // 12
		
		arrivedAtUnloadingPointOrBefore = new OrderStatusSet(beforeArrivedAtUnloadingPoint);
		arrivedAtUnloadingPointOrBefore.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);      // 13
		
		beyondArrivedAtUnloadingPoint = new OrderStatusSet();
		beyondArrivedAtUnloadingPoint.add(Constant.ORDER_STATUS_UNLOADING);                         // 14
		beyondArrivedAtUnloadingPoint.add(Constant.ORDER_STATUS_DELIVERED);                         // 15
		beyondArrivedAtUnloadingPoint.add(Constant.ORDER_STATUS_FINISHED);                          // 16
		beyondArrivedAtUnloadingPoint.add(Constant.ORDER_STATUS_DELETED);                           // 17
		
		arrivedAtUnloadingPointOrBeyond = new OrderStatusSet(beyondArrivedAtUnloadingPoint);
		arrivedAtUnloadingPointOrBeyond.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);      // 13
		
		
		beforeDelivered = new OrderStatusSet(loadedOrBefore);
		beforeDelivered.add(Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);                             // 18
		beforeDelivered.add(Constant.ORDER_STATUS_SHIPPING);                                        // 12
		beforeDelivered.add(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);                      // 13
		beforeDelivered.add(Constant.ORDER_STATUS_UNLOADING);                                       // 14
		
		deliveredOrBefore = new OrderStatusSet(beforeDelivered);
		deliveredOrBefore.add(Constant.ORDER_STATUS_DELIVERED);                                     // 15
		
		beyondDelivered = new OrderStatusSet();
		beyondDelivered.add(Constant.ORDER_STATUS_FINISHED);                                        // 16
		beyondDelivered.add(Constant.ORDER_STATUS_DELETED);                                         // 17
		
		deliveredOrBeyond = new OrderStatusSet(beyondDelivered);
		deliveredOrBeyond.add(Constant.ORDER_STATUS_DELIVERED);                                     // 15
		
		
		// Intersections
		beyondDispatchedAndDeliveredOrBefore = new OrderStatusSet(beyondDispatched);
		beyondDispatchedAndDeliveredOrBefore.retainAll(deliveredOrBefore);
	}
	
	
	public static Set<Long> getBeyondDispatched() {
		return beyondDispatched;
	}
	public static Set<Long> getBeforeDelivered() {
		return beforeDelivered;
	}
	
	
	public static boolean isBeforeDispatched(OrderStatusBsEntity orderStatus) {
		return beforeDispatched.contains(orderStatus);
	}
	public static boolean isBeforeDispatched(Long orderStatusCd) {
		return beforeDispatched.contains(orderStatusCd);
	}
	public static boolean isDispatchedOrBefore(OrderStatusBsEntity orderStatus) {
		return dispatchedOrBefore.contains(orderStatus);
	}
	public static boolean isDispatchedOrBefore(Long orderStatusCd) {
		return dispatchedOrBefore.contains(orderStatusCd);
	}
	public static boolean isBeyondDispatched(OrderStatusBsEntity orderStatus) {
		return beyondDispatched.contains(orderStatus);
	}
	public static boolean isBeyondDispatched(Long orderStatusCd) {
		return beyondDispatched.contains(orderStatusCd);
	}
	public static boolean isDispatchedOrBeyond(OrderStatusBsEntity orderStatus) {
		return dispatchedOrBeyond.contains(orderStatus);
	}
	public static boolean isDispatchedOrBeyond(Long orderStatusCd) {
		return dispatchedOrBeyond.contains(orderStatusCd);
	}
	
	
	public static boolean isLoadingOrBeyond(OrderStatusBsEntity orderStatus) {
		return loadingOrBeyond.contains(orderStatus);
	}
	public static boolean isLoadingOrBeyond(Long orderStatusCd) {
		return loadingOrBeyond.contains(orderStatusCd);
	}
	public static boolean isBeyondLoading(OrderStatusBsEntity orderStatus) {
		return beyondLoading.contains(orderStatus);
	}
	public static boolean isBeyondLoading(Long orderStatusCd) {
		return beyondLoading.contains(orderStatusCd);
	}
	public static boolean isLoadingOrBefore(OrderStatusBsEntity orderStatus) {
		return loadingOrBefore.contains(orderStatus);
	}
	public static boolean isLoadingOrBefore(Long orderStatusCd) {
		return loadingOrBefore.contains(orderStatusCd);
	}
	
	public static boolean isBeforeArrivedAtLoadingPoint(OrderStatusBsEntity orderStatus) {
		return beforeArrivedAtLoadingPoint.contains(orderStatus);
	}
	public static boolean isBeforeArrivedAtLoadingPoint(Long orderStatusCd) {
		return beforeArrivedAtLoadingPoint.contains(orderStatusCd);
	}
	
	public static boolean isArrivedAtLoadingPointOrBeyond(OrderStatusBsEntity orderStatus) {
		return arrivedAtLoadingPointOrBeyond.contains(orderStatus);
	}
	public static boolean isArrivedAtLoadingPointOrBeyond(Long orderStatusCd) {
		return arrivedAtLoadingPointOrBeyond.contains(orderStatusCd);
	}
	
	public static boolean isBeforeLoading(OrderStatusBsEntity orderStatus) {
		return beforeLoading.contains(orderStatus);
	}
	public static boolean isBeforeLoading(Long orderStatusCd) {
		return beforeLoading.contains(orderStatusCd);
	}
	
	
	public static boolean isLoadedOrBeyond(OrderStatusBsEntity orderStatus) {
		return loadedOrBeyond.contains(orderStatus);
	}
	public static boolean isLoadedOrBeyond(Long orderStatusCd) {
		return loadedOrBeyond.contains(orderStatusCd);
	}
	public static boolean isBeyondLoaded(OrderStatusBsEntity orderStatus) {
		return beyondLoaded.contains(orderStatus);
	}
	public static boolean isBeyondLoaded(Long orderStatusCd) {
		return beyondLoaded.contains(orderStatusCd);
	}
	public static boolean isLoadedOrBefore(OrderStatusBsEntity orderStatus) {
		return loadedOrBefore.contains(orderStatus);
	}
	public static boolean isLoadedOrBefore(Long orderStatusCd) {
		return loadedOrBefore.contains(orderStatusCd);
	}
	public static boolean isBeforeLoaded(OrderStatusBsEntity orderStatus) {
		return beforeLoaded.contains(orderStatus);
	}
	public static boolean isBeforeLoaded(Long orderStatusCd) {
		return beforeLoaded.contains(orderStatusCd);
	}
	
	
	public static boolean isArrivedAtUnloadingPointOrBeyond(OrderStatusBsEntity orderStatus) {
		return arrivedAtUnloadingPointOrBeyond.contains(orderStatus);
	}
	public static boolean isArrivedAtUnloadingPointOrBeyond(Long orderStatusCd) {
		return arrivedAtUnloadingPointOrBeyond.contains(orderStatusCd);
	}
	public static boolean isBeyondArrivedAtUnloadingPoint(OrderStatusBsEntity orderStatus) {
		return beyondArrivedAtUnloadingPoint.contains(orderStatus);
	}
	public static boolean isBeyondArrivedAtUnloadingPoint(Long orderStatusCd) {
		return beyondArrivedAtUnloadingPoint.contains(orderStatusCd);
	}
	public static boolean isArrivedAtUnloadingPointOrBefore(OrderStatusBsEntity orderStatus) {
		return arrivedAtUnloadingPointOrBefore.contains(orderStatus);
	}
	public static boolean isArrivedAtUnloadingPointOrBefore(Long orderStatusCd) {
		return arrivedAtUnloadingPointOrBefore.contains(orderStatusCd);
	}
	public static boolean isBeforeArrivedAtUnloadingPoint(OrderStatusBsEntity orderStatus) {
		return beforeArrivedAtUnloadingPoint.contains(orderStatus);
	}
	public static boolean isBeforeArrivedAtUnloadingPoint(Long orderStatusCd) {
		return beforeArrivedAtUnloadingPoint.contains(orderStatusCd);
	}
	
	
	public static boolean isDeliveredOrBeyond(OrderStatusBsEntity orderStatus) {
		return deliveredOrBeyond.contains(orderStatus);
	}
	public static boolean isDeliveredOrBeyond(Long orderStatusCd) {
		return deliveredOrBeyond.contains(orderStatusCd);
	}
	public static boolean isBeyondDelivered(OrderStatusBsEntity orderStatus) {
		return beyondDelivered.contains(orderStatus);
	}
	public static boolean isBeyondDelivered(Long orderStatusCd) {
		return beyondDelivered.contains(orderStatusCd);
	}
	public static boolean isDeliveredOrBefore(OrderStatusBsEntity orderStatus) {
		return deliveredOrBefore.contains(orderStatus);
	}
	public static boolean isDeliveredOrBefore(Long orderStatusCd) {
		return deliveredOrBefore.contains(orderStatusCd);
	}
	public static boolean isBeforeDelivered(OrderStatusBsEntity orderStatus) {
		return beforeDelivered.contains(orderStatus);
	}
	public static boolean isBeforeDelivered(Long orderStatusCd) {
		return beforeDelivered.contains(orderStatusCd);
	}
	
	
	public static boolean isBeyondDispatchedAndDeliveredOrBefore(OrderStatusBsEntity orderStatus) {
		return beyondDispatchedAndDeliveredOrBefore.contains(orderStatus);
	}
	public static boolean isBeyondDispatchedAndDeliveredOrBefore(Long orderStatusCd){
		return beyondDispatchedAndDeliveredOrBefore.contains(orderStatusCd);
	}
}

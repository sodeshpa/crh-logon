/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 * Contains information about number of orders and weight of orders.
 */
public class OrderChangeConstants implements Serializable{
	
	//these are the states, from which the order can go to arrived at loadingpoint/loaded state, and can transmit to parallel
	 public static final Long [] statuses = {//(Long.valueOf(Constant.ORDER_STATUS_DISPATCHED) ),
		 (Long.valueOf( Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER))
			 , (Long.valueOf( Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK))									 
			 , (Long.valueOf( Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT)) 
			 , (Long.valueOf( Constant.ORDER_STATUS_LOADING))};

		//Shipping UI dropdown choices - mean transport condition filter
		public static final String SHIPPING_UI_TRANSPORTATION_CHOICE_BULK_POWDER = "BULK POWDER";
		public static final String SHIPPING_UI_TRANSPORTATION_CHOICE_BULK_DUMPED = "BULK DUMPED";
		public static final String SHIPPING_UI_TRANSPORTATION_CHOICE_BAG= "BAG";
}

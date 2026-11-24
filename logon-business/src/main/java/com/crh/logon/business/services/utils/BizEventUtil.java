/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class BizEventUtil {

	public static final BizeventTypeBsEntity BIZEVENT_TYPE_NEW = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRANSMITTED_TO_POOL = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_DISPATCHED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_CANCEL_ON_WEIGH_SYSTEM = new BizeventTypeBsEntity();	
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_LOADING = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_LOADED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_SHIPPING = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_UNLOADING = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_DELIVERED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_FINISHED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_DELETED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK_ACK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK_ACK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_PRE_LOAD_CANCELLED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_RETURN_CANCELLED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRUCK_DRIVER_CHANGED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRUCK_COMPOSITION_CHANGED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRUCK_COMPOSITION_CONFIRMED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_CANCEL_ASSIGNMENT = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_REMOVE_ASSIGNMENT = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_RE_ALLOCATE = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_CREATE_EXTERNAL_ASSIGNMENT = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_EDIT_ASSIGNMENT = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_RETURNED = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_SET_COMM_CARRIER = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_DELIVERY_NOTE = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_SET_SILO = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_LEAVE_SHIPTO = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_SET_POOL = new BizeventTypeBsEntity();	
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_POOL_ACK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_COMM_CARRIER_ACK = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_SET_EXEC_CARRIER = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_EXEC_CARRIER_REJECTS = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_POSITION = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_TRANSMITTED_TO_RAIL = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_EDIT_FIELD = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_COMM_CARRIER_REJECTS = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_SCAN_CONTAINER = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_UPDATED_SCHEDULING = new BizeventTypeBsEntity();
	public static final BizeventTypeBsEntity BIZEVENT_TYPE_REGULARIZATION = new BizeventTypeBsEntity();
	
	static{
		BIZEVENT_TYPE_NEW.setBizeventTypeCd(Constant.BIZEVENT_TYPE_NEW);
		BIZEVENT_TYPE_TRANSMITTED_TO_POOL.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL);
		BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK);
		BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER);
		BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK);
		BIZEVENT_TYPE_DISPATCHED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_DISPATCHED);
		BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK);
		BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK);
		BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT.setBizeventTypeCd(Constant.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT);
		BIZEVENT_TYPE_CANCEL_ON_WEIGH_SYSTEM.setBizeventTypeCd(Constant.BIZEVENT_TYPE_CANCEL_ON_WEIGH_SYSTEM);		
		BIZEVENT_TYPE_LOADING.setBizeventTypeCd(Constant.BIZEVENT_TYPE_LOADING);
		BIZEVENT_TYPE_LOADED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_LOADED);
		BIZEVENT_TYPE_SHIPPING.setBizeventTypeCd(Constant.BIZEVENT_TYPE_SHIPPING);
		BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT.setBizeventTypeCd(Constant.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT);
		BIZEVENT_TYPE_UNLOADING.setBizeventTypeCd(Constant.BIZEVENT_TYPE_UNLOADING);
		BIZEVENT_TYPE_DELIVERED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_DELIVERED);
		BIZEVENT_TYPE_FINISHED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_FINISHED);
		BIZEVENT_TYPE_DELETED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_DELETED);
		BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK);
		BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK_ACK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK_ACK);
		BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK);
		BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK_ACK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK_ACK);
		BIZEVENT_TYPE_PRE_LOAD_CANCELLED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_PRE_LOAD_CANCELLED);
		BIZEVENT_TYPE_RETURN_CANCELLED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_RETURN_CANCELLED);
		BIZEVENT_TYPE_TRUCK_DRIVER_CHANGED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRUCK_DRIVER_CHANGED);
		BIZEVENT_TYPE_TRUCK_COMPOSITION_CHANGED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRUCK_COMPOSITION_CHANGED);
		BIZEVENT_TYPE_TRUCK_COMPOSITION_CONFIRMED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRUCK_COMPOSITION_CONFIRMED);
		BIZEVENT_TYPE_CANCEL_ASSIGNMENT.setBizeventTypeCd(Constant.BIZEVENT_TYPE_CANCEL_ASSIGNMENT);
		BIZEVENT_TYPE_REMOVE_ASSIGNMENT.setBizeventTypeCd(Constant.BIZEVENT_TYPE_REMOVE_ASSIGNMENT);
		BIZEVENT_TYPE_RE_ALLOCATE.setBizeventTypeCd(Constant.BIZEVENT_TYPE_RE_ALLOCATE);
		BIZEVENT_TYPE_CREATE_EXTERNAL_ASSIGNMENT.setBizeventTypeCd(Constant.BIZEVENT_TYPE_CREATE_EXTERNAL_ASSIGNMENT);
		BIZEVENT_TYPE_EDIT_ASSIGNMENT.setBizeventTypeCd(Constant.BIZEVENT_TYPE_EDIT_ASSIGNMENT);
		BIZEVENT_TYPE_RETURNED.setBizeventTypeCd(Constant.BIZEVENT_TYPE_RETURNED);
		BIZEVENT_TYPE_SET_COMM_CARRIER.setBizeventTypeCd(Constant.BIZEVENT_TYPE_SET_COMM_CARRIER);
		BIZEVENT_TYPE_COMM_CARRIER_ACK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_COMM_CARRIER_ACK);
		BIZEVENT_TYPE_DELIVERY_NOTE.setBizeventTypeCd(Constant.BIZEVENT_TYPE_DELIVERY_NOTE);
		BIZEVENT_TYPE_SET_SILO.setBizeventTypeCd(Constant.BIZEVENT_TYPE_SET_SILO);
		BIZEVENT_TYPE_LEAVE_SHIPTO.setBizeventTypeCd(Constant.BIZEVENT_TYPE_LEAVE_SHIPTO);
		BIZEVENT_TYPE_SET_POOL.setBizeventTypeCd(Constant.BIZEVENT_TYPE_SET_POOL);
		BIZEVENT_TYPE_POOL_ACK.setBizeventTypeCd(Constant.BIZEVENT_TYPE_POOL_ACK);
		BIZEVENT_TYPE_SET_EXEC_CARRIER.setBizeventTypeCd(Constant.BIZEVENT_TYPE_SET_EXEC_CARRIER);
		BIZEVENT_TYPE_EXEC_CARRIER_REJECTS.setBizeventTypeCd(Constant.BIZEVENT_TYPE_EXEC_CARRIER_REFUSED);		
		BIZEVENT_TYPE_POSITION.setBizeventTypeCd(Constant.BIZEVENT_TYPE_POSITION);
		BIZEVENT_TYPE_TRANSMITTED_TO_RAIL.setBizeventTypeCd(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_RAIL);
		BIZEVENT_TYPE_EDIT_FIELD.setBizeventTypeCd(Constant.BIZEVENT_TYPE_EDIT_FIELD);
		BIZEVENT_TYPE_COMM_CARRIER_REJECTS.setBizeventTypeCd(Constant.BIZEVENT_TYPE_COMM_CARRIER_REFUSED);
		BIZEVENT_TYPE_SCAN_CONTAINER.setBizeventTypeCd(Constant.BIZEVENT_TYPE_SCAN_CONTAINER);
		BIZEVENT_TYPE_UPDATED_SCHEDULING.setBizeventTypeCd(Constant.BIZEVENT_TYPE_UPDATED_SCHEDULING);
		BIZEVENT_TYPE_REGULARIZATION.setBizeventTypeCd(Constant.BIZEVENT_TYPE_REGULARIZATION);
	}


	public static void processCoordinates(BizeventEntity event)
	{
		ShippingpointEntity sp = event.getShippingpoint();
		ShiptoEntity shipto = event.getShipto();
		OrderEntity order = event.getOrder();
		
		if ((event.getCoordLat() == null) || (event.getCoordLon() == null)) {
			if (sp != null) {
				event.setCoordLat(sp.getCoordLat());
				event.setCoordLon(sp.getCoordLon());
			}
			else if (shipto != null) {
				if (order != null && LogonUtil.isLnfShipto(shipto)) {
					event.setCoordLat(order.getLnfCoordLat());
					event.setCoordLon(order.getLnfCoordLon());
				}
				else {
					event.setCoordLat(shipto.getCoordLat());
					event.setCoordLon(shipto.getCoordLon());
				}
			}
		}
		else {
			if ((sp != null) && (event.getCoordLat() != null) && (event.getCoordLon() != null) 
					&& (!sp.getCoordFixFlag().booleanValue())) {
				sp.setCoordLon(event.getCoordLon());
				sp.setCoordLat(event.getCoordLat());
				sp.setCoordFixFlag(Boolean.TRUE);
			}
		}
	}
}

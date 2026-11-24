/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Iterator;
import java.util.List;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.OrderRepMaintTypeEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.common.WagonMaintEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeBsEntity;

/**
 * Util class related to rail special orders, functionality commomly used from business and web.
 * @author AdNovum Informatik AG
 */
public class OrderSpecialUtils {

	
	/**
	 * Returns the sap material no depending on the parameteres provided.
	 * @param distDest
	 * @param orderRep
	 * @param wagon 
	 * @return
	 */
	public static String getEmptySapMaterialNo(String distDest, OrderRepEntity orderRep, WagonEntity wagon) {
		String materialNo = null;
		if (!Constant.DISTRIBUTION_DEST_REPAIR.equals(distDest)) {
			materialNo = wagon.getWagonType().getEmptySapMaterialNo();
		}
		else {			
			List orderRepMaintTypes = orderRep.getOrderRepMaintTypes();
			materialNo = getRepairRailOrderMaterialNo(wagon, orderRepMaintTypes);
		}
		return materialNo;
	}

	/**
	 * Returns the empty sap material no according to the wagon type, and the maintenance types.
	 * @param wagon
	 * @param orderRepMaintTypes
	 * @return
	 */
	public static String getRepairRailOrderMaterialNo(WagonEntity wagon, List orderRepMaintTypes) {
		String materialNo;
		if (OrderSpecialUtils.isRepairMaintenanceTypePresent(orderRepMaintTypes)) {
			materialNo = wagon.getWagonType().getRepairSapMaterialNo();
		}
		else {
			materialNo = wagon.getWagonType().getMaintenanceSapMaterialNo();
		}
		return materialNo;
	}

	/**
	 * Returns true in case among maintenance types have repairFlag on.
	 * @param maint
	 * @return
	 */
	public static boolean isRepairMaintenanceTypePresent(List objectList) {
		boolean isRepairPresent = false;
		if ( objectList.isEmpty() ) {
			return isRepairPresent;
		}
		else {
			Iterator it = objectList.iterator();
			while (it.hasNext()) {
				Object object = it.next();
				MaintTypeBsEntity maintType = null;
				if ( object  instanceof OrderRepMaintTypeEntity ) {
					OrderRepMaintTypeEntity orrepM = (OrderRepMaintTypeEntity) object;
					maintType = orrepM.getMaintType();
				}
				else {
					maintType = ((WagonMaintEntity) object).getMaintType();
				}
				if ( maintType.getIsRepairFlag().booleanValue() ) {
					isRepairPresent = true;
					break;
				}
			}				
		}
		
		return isRepairPresent;
	}
	
}

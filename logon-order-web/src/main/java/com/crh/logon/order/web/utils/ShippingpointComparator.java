/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.util.Comparator;

import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class ShippingpointComparator implements Comparator {

	/**
	 * Compare 2 shippingpointentity based on the shippingpoint name
	 * 
	 * @param lhs The left hand side value of the comparison
	 * @param rhs The right hand side value of the comparison
	 * @return If return value greater than 0, the rhs value is the greater, else if the return value is
	 * 0 then the lhs = rhs else lhs is greater then rhs.
	 * */
	@Override
	public int compare(final Object lhs, final Object rhs) {
		
		//if both value null they are equals.
		if(null == lhs && null == rhs) {
			return 0;
		}		
		//If lhs null than we have to return -1.
		if (null == lhs) {
			return -1;
		}
		//If rhs null we have to return 1 to keep transitive 
		if(null == rhs) {
			return 1;
		}
		
		final String lhsShipToName;
		final String rhsShipToName;

		if (lhs instanceof ShippingpointEntity && rhs instanceof ShippingpointEntity){
			lhsShipToName= ((ShippingpointEntity)lhs).getShippingpointName();
			rhsShipToName = ((ShippingpointEntity)rhs).getShippingpointName();
		}
		else if ( lhs instanceof ShippingpointDTO && rhs instanceof ShippingpointDTO ) {
			lhsShipToName= ((ShippingpointDTO)lhs).getShippingpointName();
			rhsShipToName = ((ShippingpointDTO)rhs).getShippingpointName();
		}
		else {
			throw new LogonException("Cannot compare shippingpoint, unknown type", Reason.RES_ERROR_IMPL);
		}
		
		if(null == lhsShipToName && null == rhsShipToName) {
			return 0;
		}
		//we have to handle the rhs value also because compareTo does not accept null as right hand side value.
		if (null == lhsShipToName) {
			return -1;
		}
		if (null == rhsShipToName) {
			return 1;
		}
		return lhsShipToName.compareTo(rhsShipToName);
	}
}

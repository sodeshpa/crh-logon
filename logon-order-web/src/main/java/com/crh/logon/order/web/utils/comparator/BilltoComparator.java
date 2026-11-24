/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.comparator;

import java.util.Comparator;

import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.views.SapSoldBillSareaSrchEntity;


/**
 * @author AdNovum Informatik AG
 */
public class BilltoComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		String name1 = null;
		if (o1 instanceof SapSoldBillSareaSrchEntity) {
			name1 = ((SapSoldBillSareaSrchEntity) o1).getBilltoName();
		}
		else if (o1 instanceof ShiptoBilltoEntity) {
			name1 = ((ShiptoBilltoEntity) o1).getBilltoSapBp().getName1();
		}
		if ( name1 == null ) {
			return -1;
		}
		String name2 = null;
		if (o1 instanceof SapSoldBillSareaSrchEntity) {
			name2 = ((SapSoldBillSareaSrchEntity) o2).getBilltoName();
		}
		else if (o1 instanceof ShiptoBilltoEntity) {
			name2 = ((ShiptoBilltoEntity) o2).getBilltoSapBp().getName1();
		}
		return name1.compareTo(name2);
	}

	
}

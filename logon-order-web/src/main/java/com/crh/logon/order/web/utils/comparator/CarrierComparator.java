/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.comparator;

import java.util.Comparator;

import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class CarrierComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		if (o1 instanceof CarrierEntity && o2 instanceof CarrierEntity) {
			
			String name1 = ((CarrierEntity) o1).getDisplayName();
			if ( name1 == null ) {
				return -1;
			}
			String name2 = ((CarrierEntity) o2).getDisplayName();
			return name1.compareTo(name2);
		}
		else if ( o1 instanceof CommercialCarrierDTO && o2 instanceof CommercialCarrierDTO ) {
			String name1 = ((CommercialCarrierDTO) o1).getDisplayName();
			if ( name1 == null ) {
				return -1;
			}
			String name2 = ((CommercialCarrierDTO) o2).getDisplayName();
			return name1.compareTo(name2);			
		}
		else {
			throw new LogonException("Not supported type to compare as carrier.", Reason.RES_ERROR_IMPL);
		}
	}
}

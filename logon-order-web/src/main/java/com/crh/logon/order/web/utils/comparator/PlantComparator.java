/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.comparator;

import java.util.Comparator;

import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class PlantComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		String name1;
		String name2;
		if ( o1 instanceof PlantBsEntity && o2 instanceof PlantBsEntity ) {
		 name1 = ((PlantBsEntity) o1).getPlantName();
		 name2 = ((PlantBsEntity) o2).getPlantName();
		}
		else if ( o1 instanceof SupplyPlantDTO && o2 instanceof SupplyPlantDTO ) {
			name1 = ((SupplyPlantDTO) o1).getPlantName();
			name2 = ((SupplyPlantDTO) o2).getPlantName();
		}
		else if ( o1 instanceof PlantDTO && o2 instanceof PlantDTO) {
			name1 = ((PlantDTO) o1).getPlantName();
			name2 = ((PlantDTO) o2).getPlantName();			
		}
		else {
			throw new LogonException("Cannot sort plant, unknown type",Reason.RES_ERROR_IMPL);
		}
		if ( name1 == null ) {
			return -1;
		}
		
		return name1.compareTo(name2);
	}
}

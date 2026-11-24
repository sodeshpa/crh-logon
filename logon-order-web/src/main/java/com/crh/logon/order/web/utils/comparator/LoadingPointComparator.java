/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.comparator;

import java.util.Comparator;

import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class LoadingPointComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		final String name1;
		final String name2;
		
		if (o1 instanceof LoadingpointEntity && o2 instanceof LoadingpointEntity) {
			name1 = ((LoadingpointEntity) o1).getLoadingpointName();
			name2 = ((LoadingpointEntity) o2).getLoadingpointName();
		}
		else if (o1 instanceof LoadingPointDTO && o2 instanceof LoadingPointDTO) {
			name1 = ((LoadingPointDTO) o1).getLoadingpointName();
			name2 = ((LoadingPointDTO) o2).getLoadingpointName();
		}
		else {
			throw new LogonException("Cannot compare loadingpoints, unknown type.", Reason.RES_ERROR_IMPL);
		}
		if ( name1 == null ) {
			return -1;
		}
		return name1.compareTo(name2);
	}


}

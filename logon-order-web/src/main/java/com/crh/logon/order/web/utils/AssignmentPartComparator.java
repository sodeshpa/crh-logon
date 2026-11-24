/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.util.Comparator;

import com.crh.logon.persistence.entities.common.AssignmentPartEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AssignmentPartComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		Long seqNo1 = ((AssignmentPartEntity) o1).getAssignmentPartSeqNo();
		if ( seqNo1 == null ) {
			return -1;
		}
		Long seqNo2 = ((AssignmentPartEntity) o2).getAssignmentPartSeqNo();
		return seqNo1.compareTo(seqNo2);
	}

}

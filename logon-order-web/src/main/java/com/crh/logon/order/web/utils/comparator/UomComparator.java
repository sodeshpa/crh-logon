/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.comparator;

import java.util.Comparator;

import com.crh.logon.persistence.entities.refdata.UomTxEntity;

/**
 * @author AdNovum Informatik AG
 */
public class UomComparator implements Comparator<UomTxEntity> {

	public int compare(UomTxEntity o1, UomTxEntity o2) {
		String txt = o1.getTxt();
		if ( txt == null ) {
			return -1;
		}
		String txt2 = o2.getTxt();
		return txt.compareTo(txt2);
	}

}

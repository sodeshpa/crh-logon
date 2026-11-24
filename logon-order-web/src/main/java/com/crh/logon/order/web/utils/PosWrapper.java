/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

/**
 * @author AdNovum Informatik AG
 */
public class PosWrapper {
	
	private Long value;
	private String label;

	public PosWrapper(Long value, String label) {
		this.label = label;
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

}

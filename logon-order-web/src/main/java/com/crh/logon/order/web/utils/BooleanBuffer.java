/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.io.Serializable;

/**
 * A bean holding a boolean value.
 * 
 * @author AdNovum Informatik AG
 */
public class BooleanBuffer implements Serializable {

	private static final long serialVersionUID = 1L;
	private Boolean value;

	public BooleanBuffer(Boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
}

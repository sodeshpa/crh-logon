/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

public class DemandPlanningSapBpDTO implements Serializable {

	private final Long sapBpId;

	private final String name;

	public DemandPlanningSapBpDTO(Long sapBpId, String name) {
		this.sapBpId = sapBpId;
		this.name = name;
	}

	public Long getSapBpId() {
		return sapBpId;
	}

	public String getName() {
		return name;
	}
}

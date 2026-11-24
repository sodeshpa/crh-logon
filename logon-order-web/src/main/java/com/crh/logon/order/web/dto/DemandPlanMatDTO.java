/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.dto;

public class DemandPlanMatDTO {

	private final Long sapMaterialId;

	private final String name;

	private final boolean available;

	public DemandPlanMatDTO(Long sapMaterialId, String name, boolean available) {
		this.sapMaterialId = sapMaterialId;
		this.name = name;
		this.available = available;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public String getName() {
		return name;
	}

	public boolean isAvailable() {
		return available;
	}
}

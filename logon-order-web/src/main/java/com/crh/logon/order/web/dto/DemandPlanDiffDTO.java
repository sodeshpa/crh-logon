/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.dto;

import java.math.BigDecimal;

public class DemandPlanDiffDTO {

	private final String name;

	private final BigDecimal planned;

	private final BigDecimal ordered;

	public DemandPlanDiffDTO(String name, BigDecimal planned, BigDecimal ordered) {
		this.name = name;
		this.planned = planned;
		this.ordered = ordered;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPlanned() {
		return planned;
	}

	public BigDecimal getOrdered() {
		return ordered;
	}
}

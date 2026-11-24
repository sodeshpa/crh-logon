/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

public class DemandPlanningMatDTO implements Serializable {

	private final long sapMaterialId;

	private final String plantName;

	private final boolean available;

	public DemandPlanningMatDTO(long sapMaterialId, String plantName, boolean available) {
		this.sapMaterialId = sapMaterialId;
		this.plantName = plantName;
		this.available = available;
	}

	public long getSapMaterialId() {
		return sapMaterialId;
	}

	public String getPlantName() {
		return plantName;
	}

	public boolean isAvailable() {
		return available;
	}

	public static class Builder {

		private String plantName;

		private boolean available;

		public String plantName() {
			return plantName;
		}

		public Builder plantName(String plantName) {
			this.plantName = plantName;
			return this;
		}

		public boolean available() {
			return available;
		}

		public Builder available(boolean available) {
			this.available = available;
			return this;
		}

		public DemandPlanningMatDTO build(long sapMaterialId) {
			return new DemandPlanningMatDTO(sapMaterialId, plantName, available);
		}
	}
}

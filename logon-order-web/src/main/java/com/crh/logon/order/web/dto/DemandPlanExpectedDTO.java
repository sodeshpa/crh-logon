/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DemandPlanExpectedDTO {

	private final List<String> dateHeaders;

	private final List<DemandPlanExpectedRowDTO> rows;

	public DemandPlanExpectedDTO(List<String> dateHeaders,
			List<DemandPlanExpectedRowDTO> rows) {
		this.dateHeaders = dateHeaders;
		this.rows = rows;
	}

	public List<String> getDateHeaders() {
		return dateHeaders;
	}

	public List<DemandPlanExpectedRowDTO> getRows() {
		return rows;
	}

	public static class DemandPlanExpectedRowDTO {

		private final String materialName;

		private final List<BigDecimal> amounts;

		private BigDecimal total;

		public DemandPlanExpectedRowDTO(String materialName) {
			this.materialName = materialName;
			this.amounts = new ArrayList<>();
			this.total = BigDecimal.ZERO;
		}

		public String getMaterialName() {
			return materialName;
		}

		public List<BigDecimal> getAmounts() {
			return amounts;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public void addAmountToColumn(BigDecimal amount, int index) {
			int size = amounts.size();
			if (index < size) {
				amounts.set(index, amounts.get(index).add(amount));
			} else {
				if (index > size) {
					for (int i= size; i<index; i++) {
						amounts.add(null);
					}
				}
				amounts.add(amount);
			}
			total = total.add(amount);
		}
	}

}

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO class for moving <code>SapContractItemEntity</code> quantity information across the service layer.
 *
 * @author AdNovum Informatik AG
 */
public class ContractItemQuantity implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final BigDecimal INVALID_QUANTITY = new BigDecimal(-1);

	// a sentinel value used to indicate that the quantity was not computable
	// at the time the call was made. This is not an error since Planning
	// Materials do not have any information about the UoM or quantity until
	// they are explcitly assigned. But rather than using null and run the
	// danger of NPEs (and questions about why null is a valid value), we
	// use this sentinel value instead.
	public static final BigDecimal PLANNING_MATERIAL_QUANTITY = new BigDecimal(-2);

	private final long sapContractItemId;
	private final String uom;

	private BigDecimal totalQty;
	private BigDecimal usedQty;
	private BigDecimal openQty;

	/**
	 * Initializes a new instance of ContractItemQuantity, The initial values assume that we have
	 * no OPEN quantity (0) and that our USED quantity equals our TOTAL quantity
	 */
	public ContractItemQuantity(long sapContractItemId, BigDecimal totalQty, BigDecimal openQty, String uom) {
		this.sapContractItemId = sapContractItemId;
		this.uom = uom;

		this.totalQty = totalQty;
		this.openQty  = openQty;
		this.usedQty  = INVALID_QUANTITY;

		if (this.totalQty == null) {
			this.totalQty = INVALID_QUANTITY;
		}
		if (this.openQty == null) {
			this.openQty = BigDecimal.ZERO;
		}

		if (!PLANNING_MATERIAL_QUANTITY.equals(this.openQty)) {
			if (this.totalQty.compareTo(this.openQty) >= 0) {
				this.usedQty = this.totalQty.subtract(this.openQty);
			}
			else {
				this.openQty = BigDecimal.ZERO;
				this.usedQty = INVALID_QUANTITY;
			}
		}
	}

	public long getSapContractItemId() {
		return sapContractItemId;
	}

	public BigDecimal getUsedQuantity() {
		return usedQty;
	}

	public BigDecimal getTotalQuantity() {
		return totalQty;
	}

	public BigDecimal getOpenQuantity() {
		// perhaps a bit contradictory given the point of our sentinel value, but this
		// method is used for creating DeliverySettingDTO objects and for
		// performing validations - 2 things where we do not want our sentinel
		// quantity value being used. In this case, we revert to using null since
		// the code which calls this method already takes such things into account
		return isPlanningMaterialQuantity() ? null : openQty;
	}

	public boolean isPlanningMaterialQuantity() {
		return PLANNING_MATERIAL_QUANTITY.equals(openQty);
	}

	public String toString() {
		return new StringBuilder(super.toString())
				.append(" [id: ").append(sapContractItemId)
				.append(", uom: ").append(uom)
				.append(", total: ").append(totalQty)
				.append(", used: ").append(usedQty)
				.append(", open: ").append(openQty)
				.append("]")
				.toString();
	}
}

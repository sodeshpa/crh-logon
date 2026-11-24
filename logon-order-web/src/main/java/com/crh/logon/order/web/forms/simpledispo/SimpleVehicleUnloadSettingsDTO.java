/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.simpledispo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO to hold the user entered data from Simple Vehicle Scheduling - Unload popup.
 * 
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleUnloadSettingsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long orderItemId;

	private BigDecimal deliveredQuantity;

	public SimpleVehicleUnloadSettingsDTO(Long orderItemId, BigDecimal deliveredQuantity) {

		this.orderItemId = orderItemId;
		this.deliveredQuantity = deliveredQuantity;
	}

	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public BigDecimal getDeliveredQuantity() {
		return deliveredQuantity;
	}

	public void setDeliveredQuantity(BigDecimal deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}

}

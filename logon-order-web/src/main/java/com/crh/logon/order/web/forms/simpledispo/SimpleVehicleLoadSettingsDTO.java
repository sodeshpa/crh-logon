/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.simpledispo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO to hold the user entered data from Simple Vehicle Scheduling - Load popup.
 * 
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleLoadSettingsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long orderItemId;
	private Long loadingPointId;
	private BigDecimal loadedQty;
	
	public SimpleVehicleLoadSettingsDTO(Long orderItemId, 
			Long loadingPointId,
			BigDecimal loadedQty) {
		
		this.orderItemId = orderItemId;
		this.loadingPointId = loadingPointId;
		this.loadedQty = loadedQty;
	}

	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Long getLoadingPointId() {
		return loadingPointId;
	}

	public void setLoadingPointId(Long loadingPointId) {
		this.loadingPointId = loadingPointId;
	}

	public BigDecimal getLoadedQty() {
		return loadedQty;
	}

	public void setLoadedQty(BigDecimal loadedQty) {
		this.loadedQty = loadedQty;
	}
}

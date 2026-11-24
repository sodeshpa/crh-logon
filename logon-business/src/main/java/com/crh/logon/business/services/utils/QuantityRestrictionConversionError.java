package com.crh.logon.business.services.utils;

import com.crh.logon.persistence.entities.common.OrderItemEntity;

import java.math.BigDecimal;

/**
 * @author AdNovum Informatik AG
 */
public class QuantityRestrictionConversionError extends QuantityRestrictionError {

	public QuantityRestrictionConversionError(OrderItemEntity orderItem, String targetQuantityUomCd) {
		super(orderItem, BigDecimal.ZERO, targetQuantityUomCd, BigDecimal.ZERO);
	}
}

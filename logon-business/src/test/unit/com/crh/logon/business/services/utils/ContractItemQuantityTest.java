package com.crh.logon.business.services.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author AdNovum Informatik AG
 */
public class ContractItemQuantityTest {

	@Test
	public void basic() {
		final BigDecimal targetQty = new BigDecimal(25);
		final BigDecimal openQty = new BigDecimal(10);

		ContractItemQuantity qty = new ContractItemQuantity(
				1000L,
				targetQty,
				openQty,
				"TO");

		assertThat(qty.getOpenQuantity(), equalTo(openQty));
		assertThat(qty.getUsedQuantity(), equalTo(targetQty.subtract(openQty)));
		assertThat(qty.getTotalQuantity(), equalTo(targetQty));
	}

	@Test
	public void nullTargetQuantity() {
		final BigDecimal openQty = new BigDecimal(10);

		ContractItemQuantity qty = new ContractItemQuantity(
				1000L,
				null,
				openQty,
				"TO");

		assertThat(qty.getOpenQuantity(), equalTo(BigDecimal.ZERO));
		assertThat(qty.getUsedQuantity(), equalTo(ContractItemQuantity.INVALID_QUANTITY));
		assertThat(qty.getTotalQuantity(), equalTo(ContractItemQuantity.INVALID_QUANTITY));
	}

	@Test
	public void nullOpenQuantity() {
		final BigDecimal targetQty = new BigDecimal(25);

		ContractItemQuantity qty = new ContractItemQuantity(
				1000L,
				targetQty,
				null,
				"TO");

		assertThat(qty.getOpenQuantity(), equalTo(BigDecimal.ZERO));
		assertThat(qty.getUsedQuantity(), equalTo(targetQty));
		assertThat(qty.getTotalQuantity(), equalTo(targetQty));
	}

	@Test
	public void nullOpenAndTargetQuantities() {
		ContractItemQuantity qty = new ContractItemQuantity(
				1000L,
				null,
				null,
				"TO");

		assertThat(qty.getOpenQuantity(), equalTo(BigDecimal.ZERO));
		assertThat(qty.getUsedQuantity(), equalTo(ContractItemQuantity.INVALID_QUANTITY));
		assertThat(qty.getTotalQuantity(), equalTo(ContractItemQuantity.INVALID_QUANTITY));
	}

	@Test
	public void smallerTargetThanOpenQuantity() {
		final BigDecimal targetQty = new BigDecimal(10);
		final BigDecimal openQty = new BigDecimal(25);

		ContractItemQuantity qty = new ContractItemQuantity(
				1000L,
				targetQty,
				openQty,
				"TO");

		assertThat(qty.getOpenQuantity(), equalTo(BigDecimal.ZERO));
		assertThat(qty.getUsedQuantity(), equalTo(ContractItemQuantity.INVALID_QUANTITY));
		assertThat(qty.getTotalQuantity(), equalTo(targetQty));
	}

	@Test
	public void testToString() {
		final BigDecimal targetQty = new BigDecimal(25);
		final BigDecimal openQty = new BigDecimal(10);

		ContractItemQuantity qty = new ContractItemQuantity(
				1000L,
				targetQty,
				openQty,
				"TO");

		assertThat(qty.toString(), is(not(nullValue())));
	}
}

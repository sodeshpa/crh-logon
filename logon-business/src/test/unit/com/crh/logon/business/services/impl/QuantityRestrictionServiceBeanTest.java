package com.crh.logon.business.services.impl;

import com.crh.logon.business.services.utils.ContractItemQuantity;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.views.ContractItemQuantityEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author AdNovum Informatik AG
 */
public class QuantityRestrictionServiceBeanTest {

	private List<ContractItemQuantityEntity> testQuantityEntities;

	@Before
	public void setup() {
		final BigDecimal itemQty = new BigDecimal(40);

		final long[] STATUS = {
				Constant.DAILY_ORDER_STATUS_ALREADY_ORDERED,
				Constant.ORDER_STATUS_DELIVERED,
				Constant.ORDER_STATUS_LOADED,
				Constant.ORDER_STATUS_DELETED
		};

		// Admittedly, this abuses the difference between mocks and stubs since
		// only in sideEffectCallsInComputeOpenQuantityForContractItem() do we really
		// need mocks. But it reduces the noise in each test method and that, I think,
		// is a far better benefit.
		testQuantityEntities = new ArrayList<ContractItemQuantityEntity>();
		for (long status : STATUS) {
			ContractItemQuantityEntity entity = mock(ContractItemQuantityEntity.class);
			when(entity.getSapContractItemId()).thenReturn(status);

			if (status == Constant.ORDER_STATUS_DELETED) {
				when(entity.getOrderStatusCd()).thenReturn(status);
			}
			else if (status == Constant.ORDER_STATUS_DELIVERED) {
				when(entity.getDeliveredQuantity()).thenReturn(itemQty);
			}
			else if (status == Constant.ORDER_STATUS_LOADED) {
				when(entity.getLoadedQuantity()).thenReturn(itemQty);
			}
			else {
				when(entity.getOrderedQuantity()).thenReturn(itemQty);
			}

			testQuantityEntities.add(entity);
		}
	}

	@Test
	public void computeQuantitiesForQuantityControlledContractItemsWithNullList() {
		QuantityRestrictionServiceBean bean = new QuantityRestrictionServiceBean();

		Map<Long, ContractItemQuantity> results =
				bean.computeQuantitiesForQuantityControlledContractItems(null, null);

		assertTrue(results.isEmpty());
	}

	@Test
	public void computeQuantitiesForQuantityControlledContractItemsWithEmptyList() {
		QuantityRestrictionServiceBean bean = new QuantityRestrictionServiceBean();

		Map<Long, ContractItemQuantity> results =
				bean.computeQuantitiesForQuantityControlledContractItems(
						Collections.EMPTY_LIST,
						null);

		assertTrue(results.isEmpty());
	}

	@Test
	public void computeQuantitiesForQuantityControlledContractItems() {
		//--- DATA:
		List<SapContractItemEntity> contractItems = new ArrayList<SapContractItemEntity>();
		for (int i = 1; i < 4; i++) {
			SapContractItemEntity contractItem = new SapContractItemEntity();
			contractItem.setSapContractItemId((long) i);
			contractItem.setTargetQuantity(new BigDecimal(10 * i));
			contractItem.setTargetQuantityUom(new UomBsEntity());
			contractItem.getTargetQuantityUom().setUomCd("TO");
			contractItem.setIsApGroupLeader(Boolean.TRUE);
			contractItems.add(contractItem);
		}

		List<ContractItemQuantityEntity> testDbEntities = new ArrayList<ContractItemQuantityEntity>();
		for (SapContractItemEntity contractItem : contractItems) {
			for (int i = 1; i <= 3; i++) {
				ContractItemQuantityEntity entity = new ContractItemQuantityEntity();
				entity.setSapContractItemId(contractItem.getSapContractItemId());
				testDbEntities.add(entity);
			}
		}

		QuantityRestrictionServiceBean bean = spy(new QuantityRestrictionServiceBean());
		doReturn(testDbEntities)
				.when(bean).executeFindOperation(
				Matchers.<QuantityRestrictionServiceBean.FindOperation<List<ContractItemQuantityEntity>>>any());

		doReturn(new BigDecimal(5))
				.when(bean).computeOpenQuantityForContractItem(
				any(SapContractItemEntity.class),
				Matchers.<List<ContractItemQuantityEntity>>any());

		//--- INVOKE:
		Map<Long, ContractItemQuantity> results =
				bean.computeQuantitiesForQuantityControlledContractItems(
						contractItems,
						null);

		//--- ASSERT:
		// the open quantity for each contract item = 5
		// => contract-item-1 (target: 10, open: 5, used: 5)
		// => contract-item-2 (target: 20, open: 5, used: 15)
		// => contract-item-3 (target: 30, open: 5, used: 25)
		for (ContractItemQuantity qty: results.values()) {
			if (qty.getSapContractItemId() == 1L) {
				assertThat(qty.getTotalQuantity(), equalTo(new BigDecimal(10)));
				assertThat(qty.getUsedQuantity(), equalTo(new BigDecimal(5)));
			}
			else if (qty.getSapContractItemId() == 2L) {
				assertThat(qty.getTotalQuantity(), equalTo(new BigDecimal(20)));
				assertThat(qty.getUsedQuantity(), equalTo(new BigDecimal(15)));
			}
			else {
				assertThat(qty.getTotalQuantity(), equalTo(new BigDecimal(30)));
				assertThat(qty.getUsedQuantity(), equalTo(new BigDecimal(25)));
			}
		}
	}

	/**
	 * For each contract item, the computeQuantitiesForQuantityControlledContractItems()
	 * will partition of the results of the DB calls to VLGNC_CONTRACT_ITEM_QUANTITY into
	 * a Map of contract item and its quantities.
	 */
	@Test
	public void partitioningInComputeQuantitiesForQuantityControlledContractItems() {
		//--- DATA:
		List<SapContractItemEntity> contractItems = new ArrayList<SapContractItemEntity>();
		for (int i = 1; i <= 3; i++) {
			SapContractItemEntity item = new SapContractItemEntity();
			item.setSapContractItemId((long) i);
			item.setTargetQuantity(new BigDecimal(200));
			item.setTargetQuantityUom(new UomBsEntity());
			item.getTargetQuantityUom().setUomCd("TO");
			item.setIsApGroupLeader(Boolean.TRUE);
			contractItems.add(item);
		}

		List<ContractItemQuantityEntity> testDbEntities = new ArrayList<ContractItemQuantityEntity>();
		for (int i = 1; i <= 5; i++) {
			ContractItemQuantityEntity entity = new ContractItemQuantityEntity();

			// given the modulo here, we should have the following partition:
			//  contract item 1 => 1 item  (index: 0 => 3%3)
			//  contract item 2 => 2 items (index: 1 => 1%3, 4%3)
			//  contract item 3 => 2 items (index: 2 => 2%3, 5%3)
			entity.setSapContractItemId(contractItems.get(i % contractItems.size()).getSapContractItemId());
			testDbEntities.add(entity);
		}

		QuantityRestrictionServiceBean bean = spy(new QuantityRestrictionServiceBean());
		doReturn(testDbEntities)
				.when(bean).executeFindOperation(
				Matchers.<QuantityRestrictionServiceBean.FindOperation<List<ContractItemQuantityEntity>>>any());

		// we are not interested in the open quantity here, just the partitioning
		doReturn(new BigDecimal(1))
				.when(bean).computeOpenQuantityForContractItem(
				any(SapContractItemEntity.class),
				Matchers.<List<ContractItemQuantityEntity>>any());

		//--- INVOKE:
		bean.computeQuantitiesForQuantityControlledContractItems(
				contractItems,
				null);

		//--- ASSERT:
		ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);

		verify(bean, times(contractItems.size())).computeOpenQuantityForContractItem(
				any(SapContractItemEntity.class),
				argumentCaptor.capture());

		for (Object o : argumentCaptor.getAllValues()) {
			List<ContractItemQuantityEntity> args = (List<ContractItemQuantityEntity>) o;

			Long contractItemId = args.get(0).getSapContractItemId();
			if (args.size() == 1) {
				assertThat(contractItemId, equalTo(1L));
			}
			else {
				assertTrue(contractItemId == 2L || contractItemId == 3L);
			}
		}
	}

	@Test
	public void computeOpenQuantityForContractItemWithoutTargetQuantity() {
		QuantityRestrictionServiceBean bean = new QuantityRestrictionServiceBean();

		BigDecimal openQty = bean.computeOpenQuantityForContractItem(new SapContractItemEntity(),
																	 Collections.EMPTY_LIST);
		assertThat(openQty, equalTo(BigDecimal.ZERO));
	}

	@Test
	public void computeOpenQuantityForContractItemWithoutTargetQuantityUoM() {
		SapContractItemEntity contractItem = new SapContractItemEntity();
		contractItem.setTargetQuantity(new BigDecimal(25));

		QuantityRestrictionServiceBean bean = new QuantityRestrictionServiceBean();

		BigDecimal openQty = bean.computeOpenQuantityForContractItem(contractItem,
																	 Collections.EMPTY_LIST);
		assertThat(openQty, equalTo(BigDecimal.ZERO));
	}

	@Test
	public void computeOpenQuantityForContractItemWithNoOpenOrders() {
		SapContractItemEntity contractItem = new SapContractItemEntity();
		contractItem.setTargetQuantity(new BigDecimal(25));
		contractItem.setTargetQuantityUom(new UomBsEntity());
		contractItem.setIsApGroupLeader(Boolean.TRUE);

		QuantityRestrictionServiceBean bean = new QuantityRestrictionServiceBean();

		BigDecimal openQty = bean.computeOpenQuantityForContractItem(contractItem,
																	 Collections.EMPTY_LIST);
		assertThat(openQty, equalTo(contractItem.getTargetQuantity()));
	}

	@Test
	public void computeOpenQuantityForContractItemWithTransmittedOrders() {
		SapContractItemEntity contractItem = new SapContractItemEntity();
		contractItem.setTargetQuantity(new BigDecimal(25));
		contractItem.setTargetQuantityUom(new UomBsEntity());
		contractItem.setTotalSentQuantity(new BigDecimal(10));
		contractItem.setIsApGroupLeader(Boolean.TRUE);

		QuantityRestrictionServiceBean bean = new QuantityRestrictionServiceBean();

		BigDecimal openQty = bean.computeOpenQuantityForContractItem(contractItem,
																	 Collections.EMPTY_LIST);
		assertThat(openQty, equalTo(new BigDecimal(15)));
	}

	@Test
	public void computeOpenQuantityForContractItem() {
		final BigDecimal targetQty = new BigDecimal(100);
		final BigDecimal convertedQty = new BigDecimal(20);

		UomBsEntity uom = new UomBsEntity();
		uom.setUomCd("TO");

		SapContractItemEntity contractItem = new SapContractItemEntity();
		contractItem.setSapContractItemId(1000L);
		contractItem.setTargetQuantity(targetQty);
		contractItem.setTargetQuantityUom(uom);
		contractItem.setTotalSentQuantity(new BigDecimal(5));  // transmitted orders
		contractItem.setIsApGroupLeader(Boolean.TRUE);

		QuantityRestrictionServiceBean bean = spy(new QuantityRestrictionServiceBean());
		doReturn(convertedQty).when(bean)
				.convertToTargetQuantityUom(any(BigDecimal.class),
											any(UomBsEntity.class),
											any(SapContractItemEntity.class),
											any(Long.class),
											any(Long.class));

		//--- INVOKE:
		BigDecimal openQty =
				bean.computeOpenQuantityForContractItem(contractItem,
														testQuantityEntities);

		//--- ASSERT: 3 order items, each with a converted qty of 20 = 60 total + 5 from transmitted orders
		assertThat(openQty, equalTo(targetQty.subtract(new BigDecimal(65))));
	}

	@Test
	public void computeOpenQuantityForContractItemWithUsedLargerThanTargetQty() {
		final BigDecimal targetQty = new BigDecimal(100);
		final BigDecimal convertedQty = new BigDecimal(50);

		UomBsEntity uom = new UomBsEntity();
		uom.setUomCd("TO");

		SapContractItemEntity contractItem = new SapContractItemEntity();
		contractItem.setSapContractItemId(1000L);
		contractItem.setTargetQuantity(targetQty);
		contractItem.setTargetQuantityUom(uom);
		contractItem.setIsApGroupLeader(Boolean.TRUE);

		QuantityRestrictionServiceBean bean = spy(new QuantityRestrictionServiceBean());
		doReturn(convertedQty).when(bean)
				.convertToTargetQuantityUom(any(BigDecimal.class),
											any(UomBsEntity.class),
											any(SapContractItemEntity.class),
											any(Long.class),
											any(Long.class));

		//--- INVOKE:
		BigDecimal openQty =
				bean.computeOpenQuantityForContractItem(contractItem,
														testQuantityEntities);

		//--- ASSERT: 3 order items, each with a converted qty of 50 = 150 total
		// this is larger than the contract item's target quantity
		assertThat(openQty, equalTo(BigDecimal.ZERO));
	}

	@Test
	public void computeOpenQuantityForContractItemWithConversionFailure() {
		UomBsEntity uom = new UomBsEntity();
		uom.setUomCd("TO");

		SapContractItemEntity contractItem = new SapContractItemEntity();
		contractItem.setSapContractItemId(1000L);
		contractItem.setTargetQuantity(new BigDecimal(100));
		contractItem.setTargetQuantityUom(uom);
		contractItem.setIsApGroupLeader(Boolean.TRUE);

		QuantityRestrictionServiceBean bean = spy(new QuantityRestrictionServiceBean());
		doReturn(null).when(bean)
				.convertToTargetQuantityUom(any(BigDecimal.class),
											any(UomBsEntity.class),
											any(SapContractItemEntity.class),
											any(Long.class),
											any(Long.class));

		//--- INVOKE:
		BigDecimal openQty =
				bean.computeOpenQuantityForContractItem(contractItem,
														testQuantityEntities);

		//--- ASSERT: due to conversion failure, we assume 0 quantity
		assertThat(openQty, equalTo(BigDecimal.ZERO));
	}

	@Test
	public void sideEffectCallsInComputeOpenQuantityForContractItem() {
		// By side-effects, we mean that depending on the order status, a different set
		// of methods for computing the UoM conversion are invoked. Since the methods
		// have very similar naming, we ensure here that the right set of methods is invoked

		UomBsEntity uom = new UomBsEntity();
		uom.setUomCd("TO");

		SapContractItemEntity contractItem = mock(SapContractItemEntity.class);
		when(contractItem.getTargetQuantity()).thenReturn(new BigDecimal(25));
		when(contractItem.getTargetQuantityUom()).thenReturn(uom);
		when(contractItem.getIsApGroupLeader()).thenReturn(Boolean.TRUE);

		QuantityRestrictionServiceBean bean = spy(new QuantityRestrictionServiceBean());
		doReturn(new BigDecimal(20)).when(bean)
				.convertToTargetQuantityUom(any(BigDecimal.class),
											any(UomBsEntity.class),
											any(SapContractItemEntity.class),
											any(Long.class),
											any(Long.class));

		//--- INVOKE: (we don't care about the return value for this test)
		bean.computeOpenQuantityForContractItem(
				contractItem,
				testQuantityEntities);

		//--- ASSERT: Verify side-effect calls
		for (ContractItemQuantityEntity entity : testQuantityEntities) {
			verify(entity).getOrderStatusCd();

			if (entity.getSapContractItemId() == Constant.ORDER_STATUS_DELETED) {
				// deleted automatically results in 0 USED_QTY - nothing else is required
				assertTrue(true);  // avoid the damn PMD warning
			}
			else if (entity.getSapContractItemId() == Constant.ORDER_STATUS_DELIVERED) {
				verify(entity).getDeliveredUomCd();
				verify(entity).getDFuzzyQuantityFlag();
				verify(entity).getDNumeratorForConversion();
				verify(entity).getDNumeratorForConversion();
				verify(entity).getDUomDimensionCd();
			}
			else if (entity.getSapContractItemId() == Constant.ORDER_STATUS_LOADED) {
				verify(entity).getLoadedUomCd();
				verify(entity).getLFuzzyQuantityFlag();
				verify(entity).getLNumeratorForConversion();
				verify(entity).getLNumeratorForConversion();
				verify(entity).getLUomDimensionCd();
			}
			else {
				verify(entity).getOrderedUomCd();
				verify(entity).getOFuzzyQuantityFlag();
				verify(entity).getONumeratorForConversion();
				verify(entity).getONumeratorForConversion();
				verify(entity).getOUomDimensionCd();
				verify(entity).getO2bNumeratorForConversion();
				verify(entity).getO2bDenominatorForConversion();
			}
		}
	}
}

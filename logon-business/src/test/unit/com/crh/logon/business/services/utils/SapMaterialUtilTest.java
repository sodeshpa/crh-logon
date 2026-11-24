package com.crh.logon.business.services.utils;

import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMatSareaContractEntity;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author AdNovum Informatik AG
 */
public class SapMaterialUtilTest {

	private static final int NO_MATERIALS = 3;
	private static final int NO_CONTRACT_ITEMS = 2;

	private List<SapMaterialEntity> materials;
	private Map<Long, List<SapMatSareaContractEntity>> materialContractItems;

	@Before
	public void setup() {
		// [Material-0, Material-1, Material-2]
		materials = new ArrayList<SapMaterialEntity>();
		for (int i=0; i < NO_MATERIALS; i++) {
			SapMaterialEntity material = new SapMaterialEntity();
			material.setSapMaterialId((long) i);
			materials.add(material);
		}

		// {
		//  Material-0 => [ContractItem-0,  ContractItem-1],
		//  Material-1 => [ContractItem-10, ContractItem-11],
		//  Material-2 => [ContractItem-20, ContractItem-21]
		// }
		materialContractItems = new HashMap<Long, List<SapMatSareaContractEntity>>();
		for (int i=0; i < materials.size(); i++) {
			SapMaterialEntity material = materials.get(i);

			List<SapMatSareaContractEntity> contractItems = new ArrayList<SapMatSareaContractEntity>();
			for (int j=0; j < NO_CONTRACT_ITEMS; j++) {
				SapMatSareaContractEntity item = new SapMatSareaContractEntity();
				item.setSapContractItemId(j + (10 * material.getSapMaterialId()));
				contractItems.add(item);
			}

			materialContractItems.put(material.getSapMaterialId(), contractItems);
		}
	}

	@Test
	public void pruneQuantityRestrictedMaterials() {
		//-- DATA: (already done in @Before)

		//--- INVOKE:
		SapMaterialUtil.pruneQuantityRestrictedMaterials(
				materialContractItems,
				new HashMap<Long, ContractItemQuantity>());

		//--- ASSERT:
		assertThat(materialContractItems.size(), equalTo(NO_MATERIALS));
		for (List<SapMatSareaContractEntity> items: materialContractItems.values()) {
			assertThat(items.size(), equalTo(NO_CONTRACT_ITEMS));
		}
	}

	@Test
	public void pruneQuantityRestrictedMaterialsWithContractItemQuantities() {
		//--- DATA: (in addition to data in @Before)
		Map<Long, ContractItemQuantity> quantities = new HashMap<Long, ContractItemQuantity>();

		// Material-0: is not restricted
		// Material-1: is restricted, but still has open quantities
		// Material-2: is restricted and has no open quantities
		//
		quantities.put(10L, new ContractItemQuantity(10L, new BigDecimal(10), BigDecimal.ZERO, "TO"));
		quantities.put(20L, new ContractItemQuantity(20L, new BigDecimal(20), BigDecimal.ZERO, "TO"));
		quantities.put(21L, new ContractItemQuantity(21L, new BigDecimal(30), BigDecimal.ZERO, "TO"));

		//--- INVOKE:
		SapMaterialUtil.pruneQuantityRestrictedMaterials(materialContractItems, quantities);

		//--- ASSERT:
		assertThat(materialContractItems.size(), equalTo(NO_MATERIALS - 1));
		for (SapMaterialEntity material: materials) {
			List<SapMatSareaContractEntity> contractItems = materialContractItems.get(material.getSapMaterialId());
			if (material.getSapMaterialId() == 0L) {
				// all contract items should be present
				assertThat(contractItems.size(), equalTo(NO_CONTRACT_ITEMS));
			}
			else if (material.getSapMaterialId() == 1L) {
				// the first item should have been deleted
				assertThat(contractItems.size(), equalTo(NO_CONTRACT_ITEMS - 1));
			}
			else {
				assertThat(contractItems, is(nullValue()));
			}
		}
	}
}

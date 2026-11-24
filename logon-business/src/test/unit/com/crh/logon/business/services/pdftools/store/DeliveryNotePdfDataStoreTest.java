/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.store;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class DeliveryNotePdfDataStoreTest {

	private static final String THE_SIZE_OF_THE_FILTERED_LIST_IS_NOT_THE_EXPECTED = "The size of the filtered list is not the expected";
	private static final String METHOD_SHOULD_ALWAYS_RETURN_A_NON_NULL_LIST = "Method should always return a non null list";
	DeliveryNotePdfDataStore store = new DeliveryNotePdfDataStore(null, new Locale("en"), null, true, false, null);
	
	@Test
	public void getMaterialPlantNormsOfCountryTest() {
		List<SapMatPlantNormEntity> materialPlantNorms = getTestMatPlantNorms(Arrays.asList(
				Constant.COUNTRY_CODE_CH,
				Constant.COUNTRY_CODE_IT)
		);
		List<SapMatPlantNormEntity> materialPlantNormsOfCountry = store.getMaterialPlantNormsOfCountry(
				Constant.COUNTRY_CODE_CH, materialPlantNorms);
		validateCountryFilteredPlantNormResult(materialPlantNormsOfCountry, 1);

		materialPlantNorms = getTestMatPlantNorms(Arrays.asList(
				Constant.COUNTRY_CODE_CH,
				Constant.COUNTRY_CODE_CH,
				Constant.COUNTRY_CODE_IT)
		);
		materialPlantNormsOfCountry = store.getMaterialPlantNormsOfCountry(
				Constant.COUNTRY_CODE_CH, materialPlantNorms);
		validateCountryFilteredPlantNormResult(materialPlantNormsOfCountry, 2);

		materialPlantNorms = getTestMatPlantNorms(Arrays.asList(
				Constant.COUNTRY_CODE_IT)
		);
		materialPlantNormsOfCountry = store.getMaterialPlantNormsOfCountry(
				Constant.COUNTRY_CODE_CH, materialPlantNorms);
		validateCountryFilteredPlantNormResult(materialPlantNormsOfCountry, 0);
		
		materialPlantNorms = Collections.<SapMatPlantNormEntity> emptyList();
		materialPlantNormsOfCountry = store.getMaterialPlantNormsOfCountry(
				Constant.COUNTRY_CODE_CH, materialPlantNorms);
		Assert.assertNotNull(METHOD_SHOULD_ALWAYS_RETURN_A_NON_NULL_LIST,
				materialPlantNormsOfCountry);
		validateCountryFilteredPlantNormResult(materialPlantNormsOfCountry, 0);
	}

	public List<SapMatPlantNormEntity> getTestMatPlantNorms(List<String> countryCodes) {
		List<SapMatPlantNormEntity> materialPlantNorms = new ArrayList<SapMatPlantNormEntity>();
		for (String countryCode : countryCodes) {
			materialPlantNorms.add(getTestMatPlantNormForCountry(countryCode));
		}
		return materialPlantNorms;
	}

	private SapMatPlantNormEntity getTestMatPlantNormForCountry(String countryCode) {
		SapMatPlantNormEntity testMatPlantNorm = new SapMatPlantNormEntity();
		testMatPlantNorm.setDestCountry(countryCode);
		return testMatPlantNorm;
	}

	private void validateCountryFilteredPlantNormResult(
			List<SapMatPlantNormEntity> materialPlantNormsOfCountry, int expectedResultSize) {
		Assert.assertNotNull(METHOD_SHOULD_ALWAYS_RETURN_A_NON_NULL_LIST,
				materialPlantNormsOfCountry);
		Assert.assertEquals(THE_SIZE_OF_THE_FILTERED_LIST_IS_NOT_THE_EXPECTED,
				expectedResultSize, materialPlantNormsOfCountry.size());
	}

	@Test
	public void buildSinglePriceStringForExportInvoiceTest() {
		
		// test for LOGON-7361
		
		BigDecimal zeroPrice = new BigDecimal(0);
		BigDecimal nonZeroPrice = new BigDecimal(13).setScale(Constant.MONEY_SCALE);
		String euro = "EUR";
		String franc = "CHF";
		String matLabel = "Mat price";
		String totalLabel = "Total nettoprice";
		OrderItemEntity orderItem = new OrderItemEntity();
		OrderEntity order = new OrderEntity();
		SapContractEntity sapContract = new SapContractEntity();
		SapContractTypeBsEntity contractType = new SapContractTypeBsEntity();
		SapContractItemEntity contractItem = new SapContractItemEntity();
		MaterialPackageBsEntity regularPackage = new MaterialPackageBsEntity();
		MaterialPackageBsEntity servicePackage = new MaterialPackageBsEntity();
		PackageTypeBsEntity regularPackageType = new PackageTypeBsEntity();
		PackageTypeBsEntity servicePackageType = new PackageTypeBsEntity();
		SapMaterialEntity regularMaterial = new SapMaterialEntity();
		SapMaterialEntity serviceMaterial = new SapMaterialEntity();
		
		regularPackageType.setPackageTypeCd(Constant.PACKAGE_TYPE_BULK_POWDER);
		regularPackage.setLogonPackageType(regularPackageType);
		regularMaterial.setMaterialPackage(regularPackage);
		servicePackageType.setPackageTypeCd(Constant.PACKAGE_TYPE_SERVICE);
		servicePackage.setLogonPackageType(servicePackageType);
		serviceMaterial.setMaterialPackage(servicePackage);
		
		// case 1: price is zero and contract type = ZFCH
		contractType.setSapContractTypeCd(Constant.SAP_CONTRACT_TYPE_FREE_OF_CHARGE);
		sapContract.setSapContractType(contractType);
		contractItem.setSapContract(sapContract);
		orderItem.setSapContractItem(contractItem);
		orderItem.setOrder(order);
		orderItem.setSapMaterial(regularMaterial);
		order.setArchivedFlag(false);
		order.setFreeDeliveryFlag(false);
		
		String priceString = store.buildSinglePriceStringForExportInvoice(matLabel, orderItem, zeroPrice, euro);
		assertEquals(priceString, matLabel + " (" + euro + "): " + "0.00");
		
		// case 2: price is zero but contract type != ZFCH
		contractType.setSapContractTypeCd(Constant.SAP_CONTRACT_TYPE_ADVANCE_PAYMENT);
		sapContract.setSapContractType(contractType);
		contractItem.setSapContract(sapContract);
		orderItem.setSapContractItem(contractItem);

		priceString = store.buildSinglePriceStringForExportInvoice(matLabel, orderItem, zeroPrice, euro);
		assertEquals(priceString, "");
		
		// case 3: price is not zero and FREE_DELIVERY_FLAG = 1
		order.setFreeDeliveryFlag(true);
		priceString = store.buildSinglePriceStringForExportInvoice(totalLabel, orderItem, nonZeroPrice, franc);
		assertEquals(priceString, totalLabel + " (" + franc + "): " + "0.00");
		
		// case 4: price is not zero and FREE_DELIVERY_FLAG = 0
		order.setFreeDeliveryFlag(false);
		priceString = store.buildSinglePriceStringForExportInvoice(totalLabel, orderItem, nonZeroPrice, franc);
		assertEquals(priceString, totalLabel + " (" + franc + "): " + "13.00");
		
		// case 5: the order item is a service material
		orderItem.setSapMaterial(serviceMaterial);
		priceString = store.buildSinglePriceStringForExportInvoice(totalLabel, orderItem, nonZeroPrice, franc);
		assertEquals(priceString, totalLabel + " (" + franc + "): " + "13.00");
	}
}

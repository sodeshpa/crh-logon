/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomDimensionBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class UOMConverterTest {

	@Test
	public void testGetUomValueNumAndDenumSame()
	{
		
		BigDecimal amount = new BigDecimal("1234");
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(3L);
		uomEntity.setDenominatorForConversion(3L);
		
		BigDecimal result = UOMConverter.getUomValue(uomEntity, amount, true);
		
		Assert.assertEquals(amount, result);
		
	}
	
	@Test
	public void testGetUomValueNumGtDenum()
	{
		
		BigDecimal amount = new BigDecimal("10");
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(3L);
		uomEntity.setDenominatorForConversion(2L);
		
		BigDecimal result = UOMConverter.getUomValue(uomEntity, amount, true);
		
		Assert.assertEquals(result.doubleValue(), 15D);
		
	}
	
	@Test
	public void testGetUomConvertedValueDisallowedFuzzy()
	{
		
		boolean allowFuzzyQty = false;
		
		BigDecimal amount = new BigDecimal("10");
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(3L);
		uomEntity.setDenominatorForConversion(2L);

		// Not fuzzy, should get result
		uomEntity.setFuzzyQuantityFlag(false);
		BigDecimal result = UOMConverter.getUomConvertedValue(uomEntity, amount, allowFuzzyQty);
		Assert.assertEquals(result.doubleValue(), 15D);
		
		// !Fuzzy, should get null
		uomEntity.setFuzzyQuantityFlag(true);
		result = UOMConverter.getUomConvertedValue(uomEntity, amount, allowFuzzyQty);
		Assert.assertNull(result);

		
	}
	
	@Test
	public void testGetUomConvertedValueAllowedFuzzy()
	{

		boolean allowFuzzyQty = true;
		
		BigDecimal amount = new BigDecimal("10");
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(3L);
		uomEntity.setDenominatorForConversion(2L);

		// Not fuzzy, should get result
		uomEntity.setFuzzyQuantityFlag(false);
		BigDecimal result = UOMConverter.getUomConvertedValue(uomEntity, amount, allowFuzzyQty);
		Assert.assertEquals(result.doubleValue(), 15D);
		
		// Fuzzy and fuzzy allowed, should get result
		uomEntity.setFuzzyQuantityFlag(true);
		result = UOMConverter.getUomConvertedValue(uomEntity, amount, allowFuzzyQty);
		Assert.assertEquals(result.doubleValue(), 15D);

		
	}
	
	@Test
	public void testGetMultiplierForCalculationToUomDimensionCd()
	{
		
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(8L);
		uomEntity.setDenominatorForConversion(2L);
		uomEntity.setFuzzyQuantityFlag(false);
		
		Assert.assertEquals(4D, UOMConverter.getMultiplierForCalculationToUomDimensionCd(uomEntity, false).doubleValue());
		
	}
	
	/**
	 * Should return null when fuzzy is disallowed and the uomEntity is fuzzy
	 */
	@Test
	public void testGetMultiplierForCalculationToUomDimensionCdShouldReturnNull()
	{
		
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(8L);
		uomEntity.setDenominatorForConversion(2L);
		uomEntity.setFuzzyQuantityFlag(true);
		
		Assert.assertNull(UOMConverter.getMultiplierForCalculationToUomDimensionCd(uomEntity, false));
		
	}
	
	@Test
	public void testGetMultiplierForCalculationFromUomDimensionCd()
	{
		
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(8L);
		uomEntity.setDenominatorForConversion(2L);
		uomEntity.setFuzzyQuantityFlag(false);
		
		Assert.assertEquals(0.25D, UOMConverter.getMultiplierForCalculationFromUomDimensionCd(uomEntity, false).doubleValue());
		
	}
	
	@Test
	public void testGetMultiplierForCalculationFromUomDimensionCdShouldReturnNull()
	{
		
		UomBsEntity uomEntity = new UomBsEntity();
		uomEntity.setUomCd("TEST_UOM");
		uomEntity.setNumeratorForConversion(8L);
		uomEntity.setDenominatorForConversion(2L);
		uomEntity.setFuzzyQuantityFlag(true);
		
		Assert.assertNull(UOMConverter.getMultiplierForCalculationFromUomDimensionCd(uomEntity, false));
		
	}

	@Test
	public void testConvertUomToAnotherUomUsingCommonDimension()
	{

		UomDimensionBsEntity uomDimension = new UomDimensionBsEntity();
		uomDimension.setUomDimensionCd("TON");
		
		UomBsEntity fromEntity = new UomBsEntity();
		fromEntity.setUomCd("TEST_UOM_1");
		fromEntity.setNumeratorForConversion(8L);
		fromEntity.setDenominatorForConversion(2L);
		fromEntity.setFuzzyQuantityFlag(false);
		fromEntity.setUomDimension(uomDimension);
		
		UomBsEntity toEntity = new UomBsEntity();
		toEntity.setUomCd("TEST_UOM_2");
		toEntity.setNumeratorForConversion(6L);
		toEntity.setDenominatorForConversion(3L);
		toEntity.setFuzzyQuantityFlag(false);
		toEntity.setUomDimension(uomDimension);
		
		BigDecimal amount = new BigDecimal("2");
		
		Assert.assertEquals(4D, UOMConverter.convertUomToAnotherUomUsingCommonDimension(fromEntity, toEntity, amount, false).doubleValue());
		
	}
	
	@Test
	public void testConvertUomToAnotherUomUsingCommonDimensionAllowFuzzy()
	{

		UomDimensionBsEntity uomDimension = new UomDimensionBsEntity();
		uomDimension.setUomDimensionCd("TON");
		
		UomBsEntity fromEntity = new UomBsEntity();
		fromEntity.setUomCd("TEST_UOM_1");
		fromEntity.setNumeratorForConversion(8L);
		fromEntity.setDenominatorForConversion(2L);
		fromEntity.setFuzzyQuantityFlag(false);
		fromEntity.setUomDimension(uomDimension);
		
		UomBsEntity toEntity = new UomBsEntity();
		toEntity.setUomCd("TEST_UOM_2");
		toEntity.setNumeratorForConversion(6L);
		toEntity.setDenominatorForConversion(3L);
		toEntity.setFuzzyQuantityFlag(true);
		toEntity.setUomDimension(uomDimension);
		
		BigDecimal amount = new BigDecimal("2");
		
		Assert.assertEquals(4D, UOMConverter.convertUomToAnotherUomUsingCommonDimension(fromEntity, toEntity, amount, true).doubleValue());
		
	}
	
	@Test
	public void testConvertUomToAnotherUomUsingCommonDimensionNullBecauseFuzzy()
	{

		UomDimensionBsEntity uomDimension = new UomDimensionBsEntity();
		uomDimension.setUomDimensionCd("TON");
		
		UomBsEntity fromEntity = new UomBsEntity();
		fromEntity.setUomCd("TEST_UOM_1");
		fromEntity.setNumeratorForConversion(8L);
		fromEntity.setDenominatorForConversion(2L);
		fromEntity.setFuzzyQuantityFlag(true);
		fromEntity.setUomDimension(uomDimension);
		
		UomBsEntity toEntity = new UomBsEntity();
		toEntity.setUomCd("TEST_UOM_2");
		toEntity.setNumeratorForConversion(6L);
		toEntity.setDenominatorForConversion(3L);
		toEntity.setFuzzyQuantityFlag(false);
		toEntity.setUomDimension(uomDimension);
		
		BigDecimal amount = new BigDecimal("2");
		
		Assert.assertNull(UOMConverter.convertUomToAnotherUomUsingCommonDimension(fromEntity, toEntity, amount, false));
		
	}
	
	@Test
	public void testConvertUomToAnotherUomUsingCommonDimensionDifferentDimension()
	{

		UomDimensionBsEntity uomDimensionTon = new UomDimensionBsEntity();
		uomDimensionTon.setUomDimensionCd("TON");
		
		UomDimensionBsEntity uomDimensionPce = new UomDimensionBsEntity();
		uomDimensionPce.setUomDimensionCd("PCE");
		
		UomBsEntity fromEntity = new UomBsEntity();
		fromEntity.setUomCd("TEST_UOM_1");
		fromEntity.setNumeratorForConversion(8L);
		fromEntity.setDenominatorForConversion(2L);
		fromEntity.setFuzzyQuantityFlag(false);
		fromEntity.setUomDimension(uomDimensionTon);
		
		UomBsEntity toEntity = new UomBsEntity();
		toEntity.setUomCd("TEST_UOM_2");
		toEntity.setNumeratorForConversion(6L);
		toEntity.setDenominatorForConversion(3L);
		toEntity.setFuzzyQuantityFlag(false);
		toEntity.setUomDimension(uomDimensionPce);
		
		BigDecimal amount = new BigDecimal("2");
		
		Assert.assertNull(UOMConverter.convertUomToAnotherUomUsingCommonDimension(fromEntity, toEntity, amount, false));
		
	}
	
	@Test
	public void testConvertAltUomToMaterialBaseUomCheckForNull()
	{
		
		Assert.assertNull(UOMConverter.convertAltUomToMaterialBaseUom(null, new BigDecimal("0"), new OrderItemEntity()));
		Assert.assertNull(UOMConverter.convertAltUomToMaterialBaseUom(new UomBsEntity(), null, new OrderItemEntity()));
		Assert.assertNull(UOMConverter.convertAltUomToMaterialBaseUom(new UomBsEntity(), new BigDecimal("0"), null));
		
	}
	
	@Test
	public void testConvertAltUomToMaterialBaseUomOrderedUom()
	{
		
		UomBsEntity orderedUom = new UomBsEntity();
		orderedUom.setFuzzyQuantityFlag(false);
		orderedUom.setUomCd("TON");
		
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setOrderedUomNumerator(8L);
		orderItem.setOrderedUomDenominator(2L);
		orderItem.setOrderedUom(orderedUom);
	
		UomBsEntity altUom = new UomBsEntity();
		altUom.setNumeratorForConversion(6L);
		altUom.setFuzzyQuantityFlag(false);
		altUom.setUomCd("TON");
		
		Assert.assertEquals(40D, UOMConverter.convertAltUomToMaterialBaseUom(altUom, new BigDecimal("10"), orderItem).doubleValue());
		
	}
	
	@Test
	public void testConvertAltUomToMaterialBaseUomLoadedUom()
	{
		
		UomBsEntity orderedUom = new UomBsEntity();
		orderedUom.setFuzzyQuantityFlag(false);
		orderedUom.setUomCd("PCE");
		
		UomBsEntity loadedUom = new UomBsEntity();
		loadedUom.setFuzzyQuantityFlag(false);
		loadedUom.setUomCd("TON");
		
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setOrderedUom(orderedUom);
		orderItem.setLoadedUom(loadedUom);
		
		orderItem.setLrUomNumerator(6L);
		orderItem.setLrUomDenominator(3L);
	
		UomBsEntity altUom = new UomBsEntity();
		altUom.setNumeratorForConversion(6L);
		altUom.setFuzzyQuantityFlag(false);
		altUom.setUomCd("TON");
		
		Assert.assertEquals(20D, UOMConverter.convertAltUomToMaterialBaseUom(altUom, new BigDecimal("10"), orderItem).doubleValue());
		
	}
	
}
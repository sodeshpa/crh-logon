/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.beans.UomConverterBean;
import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 */
public class UomConverterBeanTest {

	@Test
	public void testCase1() {
		UomConverterBean ucb = new UomConverterBean(Constant.UOM_BAG, Constant.UOM_TONS, 1L, 40L);
	
		UomConversionResult result = ucb.convertQuantity(Constant.UOM_BAG, Constant.UOM_TONS, new BigDecimal(40));
		Assert.assertEquals(1L, result.getQuantity().longValue());
		Assert.assertEquals(1L, result.getNumerator().longValue());
		Assert.assertEquals(40L, result.getDenominator().longValue());
		
		result = ucb.convertQuantity(Constant.UOM_TONS, Constant.UOM_BAG, new BigDecimal(1));
		Assert.assertEquals(40L, result.getQuantity().longValue());
		Assert.assertEquals(40L, result.getNumerator().longValue());
		Assert.assertEquals(1L, result.getDenominator().longValue());
	}
	
	@Test
	public void testCase2() {
		UomConverterBean ucb = new UomConverterBean(Constant.UOM_LKW, Constant.UOM_TONS, 28L, 1L);
		
		UomConversionResult result = ucb.convertQuantity(Constant.UOM_LKW, Constant.UOM_TONS, new BigDecimal(1));
		Assert.assertEquals(28L, result.getQuantity().longValue());
		Assert.assertEquals(28L, result.getNumerator().longValue());
		Assert.assertEquals(1L, result.getDenominator().longValue());
		
		result = ucb.convertQuantity(Constant.UOM_TONS, Constant.UOM_LKW, new BigDecimal(28));
		Assert.assertEquals(1L, result.getQuantity().longValue());
		Assert.assertEquals(1L, result.getNumerator().longValue());
		Assert.assertEquals(28L, result.getDenominator().longValue());
	}
}

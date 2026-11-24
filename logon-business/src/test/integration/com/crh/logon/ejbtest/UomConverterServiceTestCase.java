/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.math.BigDecimal;
import javax.ejb.EJBException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.interfaces.UomConverterService;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomDimensionBsEntity;
import com.crh.logon.utils.LogonConversionException;
import org.junit.Assert;

/**
 * UomConverterService tests.
 *
 * @author AdNovum Informatik AG
 */
public class UomConverterServiceTestCase extends JunitSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UomConverterServiceTestCase.class);
	private static final String UOM_CD_TEST = "TEST_UOM";
	private static final String UOM_CD_TEST_FROM = "TEST_UOM_FROM";
	private static final String UOM_CD_TEST_TO = "TEST_UOM_TO";
	private static final String UOM_CD_TO = "TO";

	protected UomConverterService uomConverterService;

	public UomConverterServiceTestCase(String testCaseName) {
		super(testCaseName);
	}

	protected void setUp() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setUp");
		}
		// ]]
		try {
			uomConverterService = serviceLocator.getUomConverterService();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToDimensionWithSameNumAndDen() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToDimensionWithSameNumAndDen");
		}
		// ]]
		try {
			UomBsEntity fromUom = createUom(UOM_CD_TEST, UOM_CD_TO, 3L, 3L, false);
			UomBsEntity toUom = createUom(UOM_CD_TO, UOM_CD_TO, 1L, 1L, false);
			BigDecimal quantity = new BigDecimal(1234L);

			UomConversionResult ucr = uomConverterService.convert(quantity, fromUom, toUom, null,
					null, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(1234D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToUomWithNullParams() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToUomWithNullParams");
		}
		// ]]
		try {
			// *** #1: quantity is null
			try {
				uomConverterService.convert(null, (UomBsEntity) null, null,
						null, null, null, true);

				Assert.fail("quantity is null, yet conversion is successful");
			}
			catch (EJBException e) {
				Throwable cause = e.getCause();

				if (!(cause instanceof IllegalArgumentException)) {
					throw e;
				}
			}

			// *** #2: fromUom is null
			try {
				uomConverterService.convert(BigDecimal.ONE, (UomBsEntity) null,
						null, null, null, null, true);

				Assert.fail("fromUom is null, yet conversion is successful");
			}
			catch (EJBException e) {
				Throwable cause = e.getCause();

				if (!(cause instanceof IllegalArgumentException)) {
					throw e;
				}
			}

			// *** #3: toUom is null
			UomBsEntity fromUom = createUom(UOM_CD_TEST_FROM, UOM_CD_TO, 8L, 2L, false);

			try {
				uomConverterService.convert(BigDecimal.ONE, fromUom,
						null, null, null, null, true);

				Assert.fail("toUom is null, yet conversion is successful");
			}
			catch (EJBException e) {
				Throwable cause = e.getCause();

				if (!(cause instanceof IllegalArgumentException)) {
					throw e;
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToDimensionWithNumGtDen() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToDimensionWithNumGtDen");
		}
		// ]]
		try {
			UomBsEntity fromUom = createUom(UOM_CD_TEST, UOM_CD_TO, 3L, 2L, false);
			UomBsEntity toUom = createUom(UOM_CD_TO, UOM_CD_TO, 1L, 1L, false);
			BigDecimal quantity = new BigDecimal(10L);

			UomConversionResult ucr = uomConverterService.convert(quantity, fromUom, toUom, null,
					null, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(15D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToUomWithFuzzyQty() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToUomWithFuzzyQty");
		}
		// ]]
		try {
			// *** #1: fuzzyQuantityFlag: true on toUom, allowFuzzyQty: false
			UomBsEntity fromUom = createUom(UOM_CD_TEST_FROM, UOM_CD_TO, 8L, 2L, false);
			UomBsEntity toUom = createUom(UOM_CD_TEST_TO, UOM_CD_TO, 6L, 3L, true);
			BigDecimal quantity = new BigDecimal(2L);

			try {
				uomConverterService.convert(quantity, fromUom, toUom, null, null, null, false);

				Assert.fail("toUom is fuzzy, fuzzy UoMs are not allowed, yet the conversion is "
						+ "successful");
			}
			/*
			 * TODO (marius Dec 21, 2012):
			 * At this spot we can see if the business delegate properly throws a
			 * LogonConversionException.
			 */
			catch (LogonConversionException e) {
				// Do nothing, because we expect this.
			}

			// *** #2: fuzzyQuantityFlag: false on toUom, allowFuzzyQty: false
			toUom.setFuzzyQuantityFlag(false);

			UomConversionResult ucr = uomConverterService.convert(quantity, fromUom, toUom, null,
					null, null, false);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(4D, convertedQuantity.doubleValue(), 0D);

			// *** #3: fuzzyQuantityFlag: true on toUom, allowFuzzyQty: true
			toUom.setFuzzyQuantityFlag(true);

			ucr = uomConverterService.convert(quantity, fromUom, toUom, null, null, null, true);
			convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(4D, convertedQuantity.doubleValue(), 0D);

			// *** #4: fuzzyQuantityFlag: true on fromUom, allowFuzzyQty: false
			toUom.setFuzzyQuantityFlag(false);
			fromUom.setFuzzyQuantityFlag(true);

			try {
				uomConverterService.convert(quantity, fromUom, toUom, null, null, null, false);

				Assert.fail("fromUom is fuzzy, fuzzy UoMs are not allowed, yet the conversion is "
						+ "successful");
			}
			catch (LogonConversionException e) {
				// Do nothing because we expect this exception
			}

			// *** #5: fuzzyQuantityFlag: true on fromUom, allowFuzzyQty: true
			ucr = uomConverterService.convert(quantity, fromUom, toUom, null, null, null, true);
			convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(4D, convertedQuantity.doubleValue(), 0D);

			// *** #6: fuzzyQuantityFlag: true on both, allowFuzzyQty: true
			toUom.setFuzzyQuantityFlag(true);

			ucr = uomConverterService.convert(quantity, fromUom, toUom, null, null, null, true);
			convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(4D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertFromDimension() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertFromDimension");
		}
		// ]]
		try {
			UomBsEntity fromUom = createUom(UOM_CD_TO, UOM_CD_TO, 1L, 1L, false);
			UomBsEntity toUom = createUom(UOM_CD_TEST, UOM_CD_TO, 8L, 2L, false);
			BigDecimal quantity = new BigDecimal(1L);

			UomConversionResult ucr = uomConverterService.convert(quantity, fromUom, toUom, null,
					null, null, false);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(0.25D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToUom() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToUom");
		}
		// ]]
		try {
			UomBsEntity fromUom = createUom(UOM_CD_TEST_FROM, UOM_CD_TO, 8L, 2L, false);
			UomBsEntity toUom = createUom(UOM_CD_TEST_TO, UOM_CD_TO, 6L, 3L, false);
			BigDecimal quantity = new BigDecimal(2L);

			UomConversionResult ucr = uomConverterService.convert(quantity, fromUom, toUom,
					null, null, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(4D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToUomWithDifferentDimension() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToUomWithDifferentDimension");
		}
		// ]]
		try {
			UomBsEntity fromUom = createUom(UOM_CD_TEST_FROM, UOM_CD_TO, 8L, 2L, false);
			UomBsEntity toUom = createUom(UOM_CD_TEST_TO, "PCE", 6L, 3L, false);
			BigDecimal quantity = new BigDecimal(2L);

			try {
				uomConverterService.convert(quantity, fromUom, toUom, null, null, null, true);

				Assert.fail("dimensions are different, yet the conversion is successful");
			}
			catch (LogonConversionException e) {
				// Do nothing, this exception is expected.
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToDimensionWithAltUom() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToDimensionWithAltUom");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(80L);

			UomConversionResult ucr = uomConverterService.convert(quantity, "BAG", UOM_CD_TO,
					-1027L, null, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(2D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToUomWithAltUom() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToUomWithAltUom");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(80L);

			UomConversionResult ucr = uomConverterService.convert(quantity, "BAG", "CKG",
					-1027L, null, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();


			Assert.assertEquals(20D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertToUomWithTwoAltUoms() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertToUomWithTwoAltUoms");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(2);

			UomConversionResult ucr = uomConverterService.convert(quantity, "27T", "MTQ",
					-1031L, null, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(216D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertLkwToDimensionWithoutTruckType() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertLkwToDimensionWithoutTruckType");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(2);

			try {
				uomConverterService.convert(quantity, "LKW", UOM_CD_TO, null, null, null, true);

				Assert.fail("Converting from LKW, truck type is not specified, yet the conversion is successful");
			}
			catch (EJBException e) {
				Throwable cause = e.getCause();

				if (!(cause instanceof IllegalArgumentException)) {
					throw e;
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertLkwToDimensionWithTruckType() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertLkwToDimensionWithTruckType");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(2);

			// truck type is now 99T, see "testdata/minimal_setup.sql"!
			UomConversionResult ucr = uomConverterService.convert(quantity, "LKW", UOM_CD_TO,
					null, 5L, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(198D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertDimensionToLkwWithTruckType() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertDimensionToLkwWithTruckType");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(198);

			UomConversionResult ucr = uomConverterService.convert(quantity, UOM_CD_TO, "LKW", null,
					5L, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(2D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertLkwToCkgWithTruckType() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertLkwToCkgWithTruckType");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(2);

			UomConversionResult ucr = uomConverterService.convert(quantity, "LKW", "CKG",
					null, 5L, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(1980D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testConvertCkgToLkwWithTruckType() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testConvertCkgToLkwWithTruckType");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(1980L);

			UomConversionResult ucr = uomConverterService.convert(quantity, "CKG", "LKW",
					null, 5L, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(2D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void testLOGON5662() throws LogonConversionException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testLOGON5662");
		}
		// ]]
		try {
			BigDecimal quantity = new BigDecimal(4L);

			UomConversionResult ucr = uomConverterService.convert(quantity, "MTQ", UOM_CD_TO,
					-2702L, null, null, true);
			BigDecimal convertedQuantity = ucr.getQuantity();

			Assert.assertEquals(1D, convertedQuantity.doubleValue(), 0D);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private static UomDimensionBsEntity createUomDimension(String uomDimensionCd) {
		// [[
		UomDimensionBsEntity result = null;
		Object[][] params = { { "uomDimensionCd", uomDimensionCd } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UomConverterServiceTestCase.class, "createUomDimension", params);
		}
		// ]]
		try {
			UomDimensionBsEntity uomDimension = new UomDimensionBsEntity();
			uomDimension.setUomDimensionCd(uomDimensionCd);
			uomDimension.setCtlActCd(true);
			result = uomDimension;
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private static UomBsEntity createUom(String uomCd, UomDimensionBsEntity uomDimension,
			Long numerator, Long denominator, Boolean fuzzyQuantityFlag) {
		// [[
		UomBsEntity result = null;
		Object[][] params = { { "uomCd", uomCd },
				{ "uomDimension", uomDimension },
				{ "numerator", numerator },
				{ "denominator", denominator },
				{ "fuzzyQuantityFlag", fuzzyQuantityFlag } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UomConverterServiceTestCase.class, "createUom", params);
		}
		// ]]
		try {
			UomBsEntity uom = new UomBsEntity();
			uom.setUomCd(uomCd);
			uom.setUomDimension(uomDimension);
			uom.setNumeratorForConversion(numerator);
			uom.setDenominatorForConversion(denominator);
			uom.setFuzzyQuantityFlag(fuzzyQuantityFlag);
			uom.setCtlActCd(true);
			uom.setAvailableInWebOrderFlag(true);
			uom.setCreateMultiOrderFlag(true);
			result = uom;
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private static UomBsEntity createUom(String uomCd, String uomDimensionCd,
			Long numerator, Long denominator, Boolean fuzzyQuantityFlag) {
		// [[
		UomBsEntity result = null;
		Object[][] params = { { "uomCd", uomCd },
				{ "uomDimensionCd", uomDimensionCd },
				{ "numerator", numerator },
				{ "denominator", denominator },
				{ "fuzzyQuantityFlag", fuzzyQuantityFlag } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UomConverterServiceTestCase.class, "createUom", params);
		}
		// ]]
		try {
			UomDimensionBsEntity uomDimension = createUomDimension(uomDimensionCd);
			result = createUom(uomCd, uomDimension, numerator, denominator,
					fuzzyQuantityFlag);
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
}

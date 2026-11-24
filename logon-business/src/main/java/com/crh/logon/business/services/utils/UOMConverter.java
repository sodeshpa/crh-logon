/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.impl.UomConverterServiceBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * This util class contains methods which can be used when dealing with different
 * UOMs.
 * 
 * Not to be instantiated.
 * 
 * @author AdNovum Informatik AG
 */
public final class UOMConverter {
	
	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(UOMConverter.class);
	
	/**
	 * Not to be instantiated.
	 */
	private UOMConverter()	{
	}
	
	/**
	 * Transforms the amount to the unitless measurement using the numerator and denominator.
	 * 
	 * Example:
	 * amt := 10
	 * num := 3
	 * den := 2
	 * 
	 * Returns: (10 * 3) / 2 = 15
	 * 
	 * <b>Should only work if numerator>=denominator</b>
	 *
	 * @param numerator
	 * @param denominator
	 * @param amount - amount to be converted
	 * @return if numerator = denominator then amount; otherwise then (amt * num) / den.
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal convert(BigDecimal amount, Long numerator,
			Long denominator, boolean forward) {
		
		BigDecimal result;
		BigDecimal nDec = new BigDecimal(numerator);
		BigDecimal dDec = new BigDecimal(denominator);
		
		if (!forward) {
			BigDecimal tmp = nDec;
			nDec = dDec;
			dDec = tmp;
		}
		
		result = amount;
		if (nDec.compareTo(dDec) != 0) {
			result = amount.multiply(nDec).divide(dDec, Constant.SCALE, BigDecimal.ROUND_HALF_UP);
		}
		return result;
	}
	
	/**
	 * @see #convert(BigDecimal, Long, Long, boolean)
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal getUomValue(UomBsEntity uomEntity, BigDecimal amount,
			boolean forward) {
		
		Long numerator = uomEntity.getNumeratorForConversion();
		Long denominator = uomEntity.getDenominatorForConversion();
		String uomCd = uomEntity.getUomCd();
		
		return getUomValue(numerator, denominator, uomCd, amount, forward);
	}
	
	/**
	 * @see #convert(BigDecimal, Long, Long, boolean)
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal getUomValue(SapMaterialAltUomEntity altUomEntity, BigDecimal amount,
			boolean forward) {
		
		Long numerator = altUomEntity.getNumeratorForConversion();
		Long denominator = altUomEntity.getDenominatorForConversion();
		String uomCd = altUomEntity.getUom().getUomCd();
		
		return getUomValue(numerator, denominator, uomCd, amount, forward);
	}
	
	/**
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal getUomValue(Long numerator, Long denominator, String uomCd,
			BigDecimal amount, boolean forward) {
		
		BigDecimal result = null;
		
		if (numerator == null || denominator == null) {
			LOG.error("Cannot convert unit of measurement, numerator or denominator for "
					+ "conversion of \'" + uomCd + "\' not defined!");
			return result;
		}
		if ((forward && denominator.longValue() == 0)
				|| (!forward && numerator.longValue() == 0)) {
			LOG.error("Cannot convert unit of measurement " + uomCd + ", division by zero.");
			return result;
		}
		
		result = convert(amount, numerator, denominator, forward);
		
		return result;
	}
	
	
	/**
	 * Returns the quantity calculated to uom_dimension_cd using the numerator and denominator.
	 * Example Uom_bs S25 (25 kg bag) uom_dimension_cd TO (tons) numberator: 1 denominator: 40.
	 * Sent as parameter 1, then 1/40 will be sent back. 
	 * 
	 * @param uomEntity - entity holding the numerator and denominator {@see UomBsEntity}
	 * @param amount - amount to be converted
	 * @param allowFuzzyQty - If true returns the quantity calculated to uom_dimension_cd using
	 *     the numerator and denominator ignoring the fuzzy quantity flag.
	 *     <b>This option should be used carefully, only where non-accurate calculations are
	 *     acceptable!</b>
	 * @return see {@link #getUomValue(UomBsEntity, BigDecimal)
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal getUomConvertedValue(UomBsEntity uomEntity,
		BigDecimal amount, boolean allowFuzzyQty) {
		
		return getUomConvertedValue(uomEntity, amount, allowFuzzyQty, true);
	}
	
	/**
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal getUomConvertedValue(UomBsEntity uomEntity,
		BigDecimal amount, boolean allowFuzzyQty, boolean forward) {
		
		BigDecimal result = null;
		if ( (amount == null) || (uomEntity == null)
				|| skipBecauseFuzzy(uomEntity, allowFuzzyQty) ) {
			return result;
		}
		
		return result = getUomValue(uomEntity, amount, forward);
	}
	
	/**
	 * Returns the multiplier with what the uom has to be multiplied to get the amount in
	 * uom_dimension_cd.
	 * 
	 * @param uomEntity - entity holding the numerator and denominator {@see UomBsEntity}
	 * @param allowFuzzyQty - If true returns the quantity calculated to uom_dimension_cd using
	 *     the numerator and denominator ignoring the fuzzy quantity flag. 
	 *     <b>This option should be used carefully, only where non-accurate calculations are
	 *     acceptable!</b>
	 * @return null if the uomEntity is fuzzy and allowFuzzyQty = true; otherwise calls
	 *     @{link {@link #getUomValue(UomBsEntity, BigDecimal)} with amount = 1
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal getMultiplierForCalculationToUomDimensionCd(UomBsEntity uomEntity,
			boolean allowFuzzyQty) {
		
		return getUomConvertedValue(uomEntity, BigDecimal.ONE, allowFuzzyQty, true);
		
	}
	
	/**
	 * Returns the multiplier with what the uom_dimension_cd is multiplied to get the value in
	 * the uomEntity.
	 * (Inverse method of getMultiplierForCalculationToUomDimensionCd).
	 * Warning: returns null if the uom is null or is fuzzy, or numerator or denominator is null.
	 * 
	 * @param uomEntity - entity holding the numerator and denominator {@see UomBsEntity}
	 * @param allowFuzzyQty - If true returns the quantity calculated to uom_dimension_cd using
	 *     the numerator and denominator ignoring the fuzzy quantity flag. 
	 *     <b>This option should be used carefully, only where non-accurate calculations are
	 *     acceptable!</b>
	 * @return null if the uomEntity is fuzzy and allowFuzzyQty = true; otherwise
	 *     1*denominator/numerator
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal getMultiplierForCalculationFromUomDimensionCd(UomBsEntity uomEntity,
			boolean allowFuzzyQty) {
		
		return getUomConvertedValue(uomEntity, BigDecimal.ONE, allowFuzzyQty, false);
		
	}
	
	/**
	 * @param uomEntity
	 * @param allowFuzzyQty
	 * @return true if the uomEntity is fuzzy and the allowFuzzyQty is set to false
	 */
	private static boolean skipBecauseFuzzy(UomBsEntity uomEntity, boolean allowFuzzyQty) {
		return !allowFuzzyQty && uomEntity.getFuzzyQuantityFlag().booleanValue();
	}
	
	/**
	 * Converts two uoms to another uom using their common uom_dimension_cd, nominator and
	 * denominator.
	 * The scale of the original amount remains unchanged.
	 * Warning: if either of the two uomBsEntities is null or their UOM_DIMENSION_CD does not
	 * correspond, or either of them is fuzzy the function will return null.
	 * Returns the amount in fromUomEntity in toUomEntity.
	 * Example: from uom: P48 to uom: S25. 1P48 = 48/40TO, 1S25=1/40TO. 1P48=48S25. 
	 * If amount is 1, the result returned will be 48.
	 * 
	 * @param uomEntity
	 * @param amount
	 * @param allowFuzzyQty - If true returns the quantity calculated to uom_dimension_cd using
	 *     the numerator and denominator ignoring the fuzzy quantity flag.
	 *     <b>This option should be used carefully, only where non-accurate calculations are
	 *     acceptable!</b>
	 * @return
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal convertUomToAnotherUomUsingCommonDimension(UomBsEntity fromUomEntity,
			UomBsEntity toUomEntity, BigDecimal amount, boolean allowFuzzyQty) {
		BigDecimal result = null;
		if ( amount == null || (fromUomEntity == null)
				|| skipBecauseFuzzy(fromUomEntity, allowFuzzyQty) || (toUomEntity == null)
				|| skipBecauseFuzzy(toUomEntity, allowFuzzyQty) ) {
			return result;
		}
		if ( fromUomEntity.getUomCd().equals(toUomEntity.getUomCd())) {
			return result = amount;
		}
		if (fromUomEntity.getUomDimension() == null
				|| fromUomEntity.getNumeratorForConversion() == null
				|| fromUomEntity.getDenominatorForConversion() == null) {
			return result;
		}
		if (toUomEntity.getUomDimension() == null
				|| toUomEntity.getNumeratorForConversion() == null
				|| toUomEntity.getDenominatorForConversion() == null) {
			return result;
		}
		if ( !fromUomEntity.getUomDimension().getUomDimensionCd().equals(
				toUomEntity.getUomDimension().getUomDimensionCd())) {
			return result;
		}
		
		BigDecimal multiplierFromUom1ToCommonUomDimension =
				getMultiplierForCalculationToUomDimensionCd(fromUomEntity, allowFuzzyQty);
		
		BigDecimal multiplierFromCommonUomDimensionToUom2 =
				getMultiplierForCalculationFromUomDimensionCd(toUomEntity, allowFuzzyQty);
		
		if ( multiplierFromCommonUomDimensionToUom2 == null
				|| multiplierFromUom1ToCommonUomDimension == null ) {
			return result = null;
		}
		
		result = (amount.multiply(multiplierFromUom1ToCommonUomDimension)).multiply(
				multiplierFromCommonUomDimensionToUom2);
		return result;
	}
	
	/**
	 * Converts the provided AUOM to the orderitem's ordered or loaded UOM.
	 * @param altUom
	 * @param quantity
	 * @param orderItem
	 * @return
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal convertAltUomToMaterialBaseUom(UomBsEntity altUom,
			BigDecimal quantity, OrderItemEntity orderItem) {
		
		BigDecimal result = null;
		
		if (altUom == null || quantity == null || orderItem == null) {
			return result = null;
		}
		
		Long orderedNumerator = orderItem.getOrderedUomNumerator();
		Long orderedDenominator = orderItem.getOrderedUomDenominator();
		Long loadedNumerator = orderItem.getLrUomNumerator();
		Long loadedDenominator = orderItem.getLrUomDenominator();
		
		// Check if the ordered uom has the same uom code as the alt.uom
		UomBsEntity orderedUom = orderItem.getOrderedUom();
		result = getUomByCd(orderedUom, altUom.getUomCd(), orderedNumerator, orderedDenominator,
				quantity);
		
		if (result != null) {
			return result;
		}
		
		// Check if the loaded uom has the same uom code as the alt.uom
		UomBsEntity loadedUom = orderItem.getLoadedUom();
		result = getUomByCd(loadedUom, altUom.getUomCd(), loadedNumerator, loadedDenominator,
				quantity);
		
		return result;
	}
	
	private static BigDecimal getUomByCd(UomBsEntity uomBsEntity, String altUomCd, Long numerator,
			Long denominator, BigDecimal quantity) {
		
		BigDecimal result = null;
		
		if (uomBsEntity != null && uomBsEntity.getUomCd().equals(altUomCd)) {
			if (numerator != null && denominator != null) {
				if (uomBsEntity.getFuzzyQuantityFlag() != null
						&& !uomBsEntity.getFuzzyQuantityFlag().booleanValue()) {
					if (denominator.longValue() != 0) {
						result = convert(quantity, numerator, denominator, true);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @deprecated Please use {@link UomConverterServiceBean}.
	 */
	public static BigDecimal convertAltUomToMaterialBaseUom(UomBsEntity altUom,
			BigDecimal quantity, SapMaterialEntity sapMaterial,
			ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltUomDao) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"altUom", altUom},
							{"quantity", quantity},
							{"sapMaterial", sapMaterial},
							{"sapMatAltUomDao", sapMatAltUomDao}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UOMConverter.class, "convertAltUomToMaterialBaseUom", params);
		}
		// ]]
		try {
			if (altUom == null || quantity == null || sapMaterial == null
					|| sapMatAltUomDao == null) {
				LOG.error("invalid input field(s): null");
				return result = null;
			}
			
			Filter<SapMaterialAltUomEntity> filter = sapMatAltUomDao.createFilter();
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL,
					sapMaterial));
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM, altUom));
			SapMaterialAltUomEntity sapMaterialAltUom = sapMatAltUomDao.findSingle(filter);
			
			if (sapMaterialAltUom != null) {
				return result = getUomValue(sapMaterialAltUom, quantity, true);
			}
			else {
				LOG.error("not an alternative UoM of material. altUoM: " + altUom + ", material: "
						+ sapMaterial.toString());
			}
			
			return result = null;
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
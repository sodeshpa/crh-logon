/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.SapMaterialUtil;
import com.crh.logon.business.services.dispatchers.UomConverterServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomDimensionBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialSareaEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Service methods for truck revenue planning report/export.
 * 
 * @author AdNovum Informatik AG
 */
@Stateless
@Interceptors(UserInfoInterceptor.class)
@Named
public class UomConverterServiceBean extends UomConverterServiceDispatcher implements UomConverterServiceLocalInterface {

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private BestellungServiceLocalInterface bestellungService;

	
	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(UomConverterServiceBean.class);
	
	/**
	 * Performs a quantity conversion between two UoMs.
	 * @throws LogonConversionException 
	 * 
	 * @see #convert(BigDecimal, UomBsEntity, UomBsEntity, SapMaterialEntity, TruckTypeBsEntity,
	 *     WagonTypeBsEntity, boolean)
	 */
	public UomConversionResult convert(BigDecimal quantity, String fromUomCd, String toUomCd,
									   Long sapMaterialId, Long truckTypeCd, Long wagonTypeCd, boolean allowFuzzyQty) throws LogonConversionException {
		// [[
		UomConversionResult result = null;
		Object[][] params = {{"quantity", quantity},
							{"fromUomCd", fromUomCd},
							{"toUomCd", toUomCd},
							{"sapMaterialId", sapMaterialId},
							{"truckTypeCd", truckTypeCd},
							{"wagonTypeCd", wagonTypeCd},
							{"allowFuzzyQty", allowFuzzyQty + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convert", params);
		}
		// ]]
		try {
			
			UomBsEntity fromUom = referentialDataService.findBsEntity(UomBsEntity.class, fromUomCd);
			UomBsEntity toUom = referentialDataService.findBsEntity(UomBsEntity.class, toUomCd);
			
			SapMaterialEntity sapMaterial = (sapMaterialId != null) ? bestellungService.getSapMaterialById(sapMaterialId) : null;			
			TruckTypeBsEntity truckType = (truckTypeCd != null)	? referentialDataService.findBsEntity(TruckTypeBsEntity.class, truckTypeCd) : null;			
			WagonTypeBsEntity wagonType = (wagonTypeCd != null) ? referentialDataService.findBsEntity(WagonTypeBsEntity.class, wagonTypeCd) : null;
			
			return result = convert(quantity, fromUom, toUom, sapMaterial, truckType, wagonType, allowFuzzyQty);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Performs a quantity conversion between two UoMs.
	 * 
	 * <p>Note: if you need to convert between an UoM and its dimension, specify the dimension
	 * as an UoM.</p>
	 * 
	 * @param quantity the quantity to convert from one UoM to another.
	 * @param fromUom the original UoM.
	 * @param toUom the target UoM.
	 * @param sapMaterial (optional) if specified, alternative UoMs will be taken into account
	 * @param truckType must be specified if <code>fromUom</code> or <code>toUom</code> can be
	 *     a fuzzy vehicle-specific UoM, and fuzzy calculations are allowed.
	 * @param wagonType must be specified if <code>fromUom</code> or <code>toUom</code> can be
	 *     a fuzzy vehicle-specific UoM, and fuzzy calculations are allowed.
	 * @param allowFuzzyQty if true, allows calculation with fuzzy UoMs, otherwise the method
	 *     will throw an exception, see below.
	 * @return the converted amount (never null), along with a pair of numerator and denominator
	 *     factors calculated throughout the conversion, meaning <code>convertedQuantity =
	 *     quantity * (numerator / denominator)</code>.
	 * @throws LogonConversionException when a known conversion error occurs for the following reasons:
	 *     <ul>
	 *         <li>RES_ERROR_UOM_CONV_FUZZY_UOM: the conversion would be performed using a fuzzy
	 *             UoM, but that is not allowed.</li>
	 *         <li>RES_ERROR_UOM_CONV_DIMENSION_UNKNOWN: when converting from/to a standard UoM
	 *             and the dimension of that standard UoM is not defined.</li>
	 *         <li>RES_ERROR_UOM_CONV_DIMENSION_MISMATCH: when converting from/to a standard UoM
	 *             and the dimensions of the input UoMs do not match.</li>
	 *         <li>RES_ERROR_UOM_CONV_NUM_OR_DEN_ZERO: if the numerator/denominator is zero
	 *             for at least one of the given UoMs, yet they are required in the calculation.
	 *             </li>
	 *         <li>RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM: when converting from a standard
	 *             UoM to a material base/alternative UoM, or vice versa, but there was no
	 *             alternative UoM matching the expected dimension.</li>
	 *     </ul>
	 * @throws IllegalArgumentException if one of the parameters are missing/invalid,
	 */
	public UomConversionResult convert(BigDecimal quantity, UomBsEntity fromUom, UomBsEntity toUom,
			SapMaterialEntity sapMaterial, TruckTypeBsEntity truckType, WagonTypeBsEntity wagonType,
			boolean allowFuzzyQty) throws LogonConversionException {
		// Sanity checks
		if (quantity == null) {
			throw new IllegalArgumentException("Parameter 'quantity' must be specified");
		}
		if (fromUom == null) {
			throw new IllegalArgumentException("Parameter 'fromUom' must be specified.");
		}
		if (toUom == null) {
			throw new IllegalArgumentException("Parameter 'toUom' must be specified.");
		}
		
		UomConversionResult ucr = new UomConversionResult(quantity, 1L, 1L);
		String fromUomCd = fromUom.getUomCd();
		String toUomCd = toUom.getUomCd();
		
		if (fromUomCd.equals(toUomCd)) {
			return ucr;
		}
		if (skipBecauseFuzzy(fromUom, allowFuzzyQty)) {
			throw new LogonConversionException("fromUom=" + fromUomCd
					+ " is fuzzy, skipping conversion.", Reason.RES_ERROR_UOM_CONV_FUZZY_UOM);
		}
		if (skipBecauseFuzzy(toUom, allowFuzzyQty)) {
			throw new LogonConversionException("toUom=" + toUomCd
					+ " is fuzzy, skipping conversion.", Reason.RES_ERROR_UOM_CONV_FUZZY_UOM);
		}
		
		UomBsEntity baseUom = (sapMaterial != null) ? sapMaterial.getBaseUom() : null;
		String baseUomCd = (baseUom != null) ? baseUom.getUomCd() : null;
		
		/**
		 * Both the "from UoM" and the "to UoM" can be one of:
		 * 
		 *    - Vehicle UoM (eg. LKW).
		 *    - Standard UoM (eg. KG).
		 *    - Base UoM (of a material, eg. TO or MTQ).
		 *    - Alternative UoM (eg. MTQ or TO).
		 * 
		 * With:
		 * 
		 *     c: converter function
		 *     f: from UoM
		 *     t: to UoM
		 * 
		 * it is true that:
		 * 
		 *     c(f, t) = 1 / c(t, f).
		 * 
		 * Therefore it is enough to check the following cases:
		 * 
		 *    - f = Vehicle UoM -> t = Vehicle UoM,
		 *    - f = Vehicle UoM -> t = Base UoM,
		 *    - f = Vehicle UoM -> t = Alt UoM,
		 *    - f = Vehicle UoM -> t = Std UoM,
		 *    - f = Base UoM    -> t = Alt UoM,
		 *    - f = Base UoM    -> t = Std UoM,
		 *    - f = Alt UoM     -> t = Alt UoM,
		 *    - f = Alt UoM     -> t = Std UoM,
		 *    - f = Std UoM     -> t = Std UoM.
		 *    
		 * Anything else: swap f with t, and the direction of the conversion.
		 * 
		 * However, that would have the potential to do double queries due to alt UoM detection.
		 * Therefore I will not call this function twice.
		 */
		if (fromUomCd.equals(Constant.UOM_LKW) || fromUomCd.equals(Constant.UOM_WAGON)) {
			// Vehicle -> Dimension
			Long[] fromNumDen = calculateNumDenForVehicle(fromUom, truckType, wagonType);
			ucr = convert(ucr, fromNumDen[0], fromNumDen[1], true);
			
			if (toUomCd.equals(Constant.UOM_LKW) || toUomCd.equals(Constant.UOM_WAGON)) {
				// ***** Vehicle -> Vehicle
				// Dimension -> Vehicle
				Long[] toNumDen = calculateNumDenForVehicle(toUom, truckType, wagonType);
				ucr = convert(ucr, toNumDen[0], toNumDen[1], false);
			}
			else if (toUomCd.equals(baseUomCd)) {
				// ***** Vehicle -> Base
				Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, null,
						Constant.UOM_TONS);
				
				if (altOrBaseUom == null) {
					throw new LogonConversionException("Could not find a way to convert from "
							+ fromUomCd + " to " + toUomCd,
							Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
				}
				else if (altOrBaseUom instanceof UomBsEntity) {
					// Dimension -> Base
					ucr = convertBetweenUomAndDimension(ucr, baseUom, false);
				}
				else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
					// Dimension -> Alt
					SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
					UomBsEntity altUomUom = altUom.getUom();
					ucr = convertBetweenUomAndDimension(ucr, altUomUom, false);
					
					// Alt -> Base
					ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, true);
				}
			}
			else {
				SapMaterialAltUomEntity toAltUom = fetchAltUom(toUomCd, sapMaterial);
				
				if (toAltUom != null) {
					// ***** Vehicle -> Alt
					Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, toAltUom,
							Constant.UOM_TONS);
					
					if (altOrBaseUom == null) {
						throw new LogonConversionException("Could not find a way to convert from "
								+ fromUomCd + " to " + toUomCd,
								Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
					}
					else if (altOrBaseUom instanceof UomBsEntity) {
						// Dimension -> Base
						ucr = convertBetweenUomAndDimension(ucr, baseUom, false);
						
						// Base -> Alt
						ucr = convertBetweenAltUomAndBaseUom(ucr, toAltUom, false);
					}
					else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
						SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
						UomBsEntity altUomUom = altUom.getUom();
						
						if (altUomUom.getUomCd().equals(toUomCd)) {
							// Dimension -> Alt
							ucr = convertBetweenUomAndDimension(ucr, toUom, false);
						}
						else {
							// Dimension -> Other Alt
							ucr = convertBetweenUomAndDimension(ucr, altUomUom, false);
							
							// Other Alt -> Base
							ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, true);
							
							// Base -> Alt
							ucr = convertBetweenAltUomAndBaseUom(ucr, toAltUom, false);
						}
					}
				}
				else {
					// ***** Vehicle -> Std
					String fromUomDimensionCd = Constant.UOM_TONS;
					String toUomDimensionCd = getDimensionCd(toUom);
					
					if (!fromUomDimensionCd.equals(toUomDimensionCd)) {
						throw new LogonConversionException("fromUom and toUom have different "
								+ "dimensions (" + fromUomDimensionCd + " and "
								+ toUomDimensionCd + ")",
								Reason.RES_ERROR_UOM_CONV_DIMENSION_MISMATCH);
					}
					
					ucr = convertBetweenUomAndDimension(ucr, toUom, false);
				}
			}
		}
		else if (fromUomCd.equals(baseUomCd)) {
			SapMaterialAltUomEntity toAltUom = fetchAltUom(toUomCd, sapMaterial);
			
			if (toUomCd.equals(Constant.UOM_LKW) || toUomCd.equals(Constant.UOM_WAGON)) {
				// ***** Base -> Vehicle
				Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, null,
						Constant.UOM_TONS);
				
				if (altOrBaseUom == null) {
					throw new LogonConversionException("Could not find a way to convert from "
							+ fromUomCd + " to " + toUomCd,
							Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
				}
				else if (altOrBaseUom instanceof UomBsEntity) {
					// Base -> Dimension
					ucr = convertBetweenUomAndDimension(ucr, fromUom, true);
				}
				else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
					SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
					UomBsEntity altUomUom = altUom.getUom();
					
					// Base -> Alt
					ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, false);
					
					// Alt -> Dimension
					ucr = convertBetweenUomAndDimension(ucr, altUomUom, true);
				}
				
				// Dimension -> Vehicle
				Long[] toNumDen = calculateNumDenForVehicle(toUom, truckType, wagonType);
				ucr = convert(ucr, toNumDen[0], toNumDen[1], false);
			}
			else if (toAltUom != null) {
				// ***** Base -> Alt
				ucr = convertBetweenAltUomAndBaseUom(ucr, toAltUom, false);
			}
			else {
				// ***** Base -> Std
				String toUomDimensionCd = getDimensionCd(toUom);
				
				Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, null,
						toUomDimensionCd);
				
				if (altOrBaseUom == null) {
					throw new LogonConversionException("Could not find a way to convert from "
							+ fromUomCd + " to " + toUomCd,
							Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
				}
				else if (altOrBaseUom instanceof UomBsEntity) {
					// Base -> Dimension
					ucr = convertBetweenUomAndDimension(ucr, fromUom, true);
				}
				else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
					SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
					UomBsEntity altUomUom = altUom.getUom();
					
					// Base -> Alt
					ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, false);
					
					// Alt -> Dimension
					ucr = convertBetweenUomAndDimension(ucr, altUomUom, true);
				}
				
				// Dimension -> Std
				ucr = convertBetweenUomAndDimension(ucr, toUom, false);
			}
		}
		else {
			SapMaterialAltUomEntity fromAltUom = fetchAltUom(fromUomCd, sapMaterial);
			
			if (fromAltUom != null) {
				if (toUomCd.equals(Constant.UOM_LKW) || toUomCd.equals(Constant.UOM_WAGON)) {
					// ***** Alt -> Vehicle
					Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, fromAltUom,
							Constant.UOM_TONS);
					
					if (altOrBaseUom == null) {
						throw new LogonConversionException("Could not find a way to convert from "
								+ fromUomCd + " to " + toUomCd,
								Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
					}
					else if (altOrBaseUom instanceof UomBsEntity) {
						// Alt -> Base
						ucr = convertBetweenAltUomAndBaseUom(ucr, fromAltUom, true);
						
						// Base -> Dimension 
						ucr = convertBetweenUomAndDimension(ucr, baseUom, true);
					}
					else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
						SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
						UomBsEntity altUomUom = altUom.getUom();
						
						if (altUomUom.getUomCd().equals(toUomCd)) {
							// Alt -> Dimension
							ucr = convertBetweenUomAndDimension(ucr, fromUom, true);
						}
						else {
							// Alt -> Base
							ucr = convertBetweenAltUomAndBaseUom(ucr, fromAltUom, true);
							
							// Base -> Other Alt
							ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, false);
							
							// Other Alt -> Dimension 
							ucr = convertBetweenUomAndDimension(ucr, altUomUom, true);
						}
					}
					
					// Dimension -> Vehicle
					Long[] toNumDen = calculateNumDenForVehicle(toUom, truckType, wagonType);
					ucr = convert(ucr, toNumDen[0], toNumDen[1], false);
				}
				else if (toUomCd.equals(baseUomCd)) {
					// ***** Alt -> Base
					ucr = convertBetweenAltUomAndBaseUom(ucr, fromAltUom, true);
				}
				else {
					SapMaterialAltUomEntity toAltUom = fetchAltUom(toUomCd, sapMaterial);
					
					if (toAltUom != null) {
						// ***** Alt -> Alt
						// Alt -> Base
						ucr = convertBetweenAltUomAndBaseUom(ucr, fromAltUom, true);
						
						// Base -> Alt
						ucr = convertBetweenAltUomAndBaseUom(ucr, toAltUom, false);
					}
					else {
						// ***** Alt -> Std
						// Alt -> Base
						ucr = convertBetweenAltUomAndBaseUom(ucr, fromAltUom, true);
						
						String toUomDimensionCd = getDimensionCd(toUom);
						
						Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, null,
								toUomDimensionCd);
						
						if (altOrBaseUom == null) {
							throw new LogonConversionException("Could not find a way to convert from "
									+ fromUomCd + " to " + toUomCd,
									Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
						}
						else if (altOrBaseUom instanceof UomBsEntity) {
							// Base -> Dimension 
							ucr = convertBetweenUomAndDimension(ucr, baseUom, true);
						}
						else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
							SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
							UomBsEntity altUomUom = altUom.getUom();
							
							// Base -> Other Alt
							ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, false);
							
							// Other Alt -> Dimension 
							ucr = convertBetweenUomAndDimension(ucr, altUomUom, true);
						}
						
						// Dimension -> Std
						ucr = convertBetweenUomAndDimension(ucr, toUom, false);
					}
				}
			}
			else {
				String fromUomDimensionCd = getDimensionCd(fromUom);
				
				// Std -> Dimension
				ucr = convertBetweenUomAndDimension(ucr, fromUom, true);
				
				if (toUomCd.equals(Constant.UOM_LKW) || toUomCd.equals(Constant.UOM_WAGON)) {
					// ***** Std -> Vehicle
					String toUomDimensionCd = Constant.UOM_TONS;
					
					if (!fromUomDimensionCd.equals(toUomDimensionCd)) {
						throw new LogonConversionException("fromUom and toUom have different "
								+ "dimensions (" + fromUomDimensionCd + " and "
								+ toUomDimensionCd + ")",
								Reason.RES_ERROR_UOM_CONV_DIMENSION_MISMATCH);
					}
					
					// Dimension -> Vehicle
					Long[] toNumDen = calculateNumDenForVehicle(toUom, truckType, wagonType);
					ucr = convert(ucr, toNumDen[0], toNumDen[1], false);
				}
				else if (toUomCd.equals(baseUomCd)) {
					// ***** Std -> Base
					Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, null,
							fromUomDimensionCd);
					
					if (altOrBaseUom == null) {
						throw new LogonConversionException("Could not find a way to convert from "
								+ fromUomCd + " to " + toUomCd,
								Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
					}
					else if (altOrBaseUom instanceof UomBsEntity) {
						// Dimension -> Base
						ucr = convertBetweenUomAndDimension(ucr, toUom, false);
					}
					else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
						SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
						UomBsEntity altUomUom = altUom.getUom();
						
						// Dimension -> Alt 
						ucr = convertBetweenUomAndDimension(ucr, altUomUom, false);
						
						// Alt -> Base
						ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, true);
					}
				}
				else {
					SapMaterialAltUomEntity toAltUom = fetchAltUom(toUomCd, sapMaterial);
					
					if (toAltUom != null) {
						// ***** Std -> Alt
						Object altOrBaseUom = findAltOrBaseUomWithCommonDim(sapMaterial, null,
								fromUomDimensionCd);
						
						if (altOrBaseUom == null) {
							throw new LogonConversionException("Could not find a way to convert from "
									+ fromUomCd + " to " + toUomCd,
									Reason.RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM);
						}
						else if (altOrBaseUom instanceof UomBsEntity) {
							// Dimension -> Base
							ucr = convertBetweenUomAndDimension(ucr, baseUom, false);
						}
						else if (altOrBaseUom instanceof SapMaterialAltUomEntity) {
							SapMaterialAltUomEntity altUom = (SapMaterialAltUomEntity) altOrBaseUom;
							UomBsEntity altUomUom = altUom.getUom();
							
							// Dimension -> Other Alt
							ucr = convertBetweenUomAndDimension(ucr, altUomUom, false);
							
							// Other Alt -> Base
							ucr = convertBetweenAltUomAndBaseUom(ucr, altUom, true);
						}
						
						// Base -> Alt
						ucr = convertBetweenAltUomAndBaseUom(ucr, toAltUom, false);
					}
					else {
						// ***** Std -> Std
						String toUomDimensionCd = getDimensionCd(toUom);
						
						if (!fromUomDimensionCd.equals(toUomDimensionCd)) {
							throw new LogonConversionException("fromUom and toUom have different "
									+ "dimensions (" + fromUomDimensionCd + " and "
									+ toUomDimensionCd + ")",
									Reason.RES_ERROR_UOM_CONV_DIMENSION_MISMATCH);
						}
						
						// Dimension -> Std
						ucr = convertBetweenUomAndDimension(ucr, toUom, false);
					}
				}
			}
		}
		
		ucr = simplifyNumAndDen(ucr);
		
		return ucr;
	}
	
	/**
	 * Converts the given amount between two UoMs defined by their respective numerator and
	 * denominator factors. (Both ways are possible.)
	 * 
	 * <p>NOTE: It only makes sense to use this function directly if you are sure about
	 * the conversion factors, and they require no lookup, eg. if they come from <code>
	 * TLGNC_ORDER_ITEM.ORDERED_NUMERATOR</code> and <code>DENOMINATOR</code>.</p>
	 * 
	 * <p>It is used by {@link #convert(BigDecimal, UomBsEntity, UomBsEntity, SapMaterialEntity,
	 * TruckTypeBsEntity, WagonTypeBsEntity, boolean)} for two purposes:</p>
	 * 
	 * <ul>
	 *     <li>Convert between two standard UoMs.</li>
	 *     <li>Convert between a material base UoM and an alternative UoM.</li>
	 * </ul>
	 * 
	 * <p>In other words, if we want to convert an amount from one standard UoM to another one, we
	 * have to find a common measurement (ie. the dimension) between them. This is achieved by two
	 * steps:</p>
	 * 
	 * <ol>
	 *     <li>Express the amount given in <code>fromUom</code> in the dimension common with
	 *         the <code>toUom</code>.</li>
	 *     <li>Calculate back the amount to <code>toUom</code> from that dimension.</li>
	 * </ol>
	 * 
	 * <p>Mathematically speaking: <code>amount * (fromNum / fromDen) / (toNum / toDen)</code>.
	 * This is only possible if both UoMs have a common dimension.</p>
	 * 
	 * <p>For example, if we convert 5 KG to CKG, their dimension is TO, and the
	 * numerators/denominators are:</p>
	 * 
	 * <ul>
	 *     <li>KG -> TO: <code>1/1000</code></li>
	 *     <li>TO -> CKG: <code>1/10</code></li>
	 * </ul>
	 * 
	 * <p>So we would do the following calculation: <code>5 KG * (1 / 1000) * (10 / 1) = 0.05 CKG
	 * </code>.</p>
	 * 
	 * <p>For alternative UoMs we skip the conversions from/to dimension, because they are most
	 * likely used to convert between UoMs of different dimensions eg. from weight to volume
	 * and vice versa. Therefore, alternative UoMs have numerators/denominators to convert
	 * directly to the base UoM.
	 * 
	 * @param quantity - the quantity to convert
	 * @param numerator - part of the conversion factor
	 * @param denominator - part of the conversion factor
	 * @param forward - true for conversion from standard UoM to dimension, or from alternative
	 *     UoM to base UoM, false for backwards conversion
	 * @return the converted amount
	 * @throws LogonConversionException 
	 */
	public BigDecimal convert(BigDecimal quantity, Long numerator, Long denominator,
			boolean forward) throws LogonConversionException {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"quantity", quantity},
							{"numerator", numerator},
							{"denominator", denominator},
							{"forward", forward + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convert", params);
		}
		// ]]
		try {
			if (numerator == null || denominator == null) {
				throw new LogonConversionException("Numerator or denominator is null",
						Reason.RES_ERROR_UOM_CONV_NUM_OR_DEN_ZERO);
			}
			if (numerator == 0 || denominator == 0) {
				throw new LogonConversionException("Numerator or denominator is zero",
						Reason.RES_ERROR_UOM_CONV_NUM_OR_DEN_ZERO);
			}
			
			if (numerator.compareTo(denominator) != 0) {
				if (!forward) {
					Long tmp = numerator;
					numerator = denominator;
					denominator = tmp;
				}
				
				BigDecimal nDec = new BigDecimal(numerator);
				BigDecimal dDec = new BigDecimal(denominator);
				
				quantity = quantity.multiply(nDec).divide(dDec, Constant.SCALE,
						BigDecimal.ROUND_HALF_UP);
			}
			
			return result = quantity;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Convenience method that returns the sales a UoM of a sap material within a certain sales
	 * area.
	 * 
	 */
	public UomBsEntity getSalesUom(SapMaterialEntity sapMaterial, SareaEntity sarea) {
		UomBsEntity salesUom = null;
		ReadOnlyDao<SapMaterialSareaEntity> sapMaterialSareaDao =
				getDaoProvider().getSapMaterialSareaEntityDao();
		FilterImpl<SapMaterialSareaEntity> sapMaterialSareaFilter =
				new FilterImpl<SapMaterialSareaEntity>();
		sapMaterialSareaFilter.addExpression(
				Restrictions.eq(SapMaterialSareaEntity.ATTR_SAP_MATERIAL_ID,
						sapMaterial.getSapMaterialId()));
		SapMaterialUtil.addSareaRestriction(sapMaterialSareaFilter, sarea);
		Authorizer.disableNextAccessRestriction();
		SapMaterialSareaEntity sapMaterialSarea = sapMaterialSareaDao.findSingle(
				sapMaterialSareaFilter);
		if (sapMaterialSarea != null) {
			salesUom = sapMaterialSarea.getSalesUom();
		}
		return salesUom;
	}
	
	/**
	 * Check if an UoM is fuzzy, ie. if a conversion cannot be made from or to that UoM.
	 * 
	 * @param uomEntity
	 * @param allowFuzzyQty
	 * @return true if the uomEntity is fuzzy and the allowFuzzyQty is set to false
	 * @adnlog.notrace
	 */
	private static boolean skipBecauseFuzzy(UomBsEntity uomEntity, boolean allowFuzzyQty) {
		return !allowFuzzyQty && uomEntity.getFuzzyQuantityFlag();
	}
	
	/**
	 * Find the alternative measurement definition for the given material and UoM.
	 *
	 * <p>In certain cases a unit of measurement may not be precise enough to define the amount of
	 * a material. (For example, one cubic meter of material A may weight more than the same of
	 * material B.) What we can do then is, the material should define a base UoM, and how to
	 * convert between that base UoM and other UoMs called alternative UoMs. So if we are
	 * converting from a UoM that is an alternative UoM for that material, we must convert to
	 * the base UoM first, and then from there to the target UoM.</p>
	 * 
	 * @param uomCd the non-precise UoM that an alternative one is to be found for
	 * @param sapMaterial the material which "concretizes" the UoM
	 * @return
	 */
	private SapMaterialAltUomEntity fetchAltUom(String uomCd, SapMaterialEntity sapMaterial) {
		// [[
		SapMaterialAltUomEntity result = null;
		Object[][] params = {{"uomCd", uomCd},
							{"sapMaterial", sapMaterial}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAltUom", params);
		}
		// ]]
		try {
			if (uomCd == null) {
				throw new IllegalArgumentException("invalid input field(s): null");
			}
			if (sapMaterial == null) {
				return result = null;
			}
			
			ReadWriteDao<SapMaterialAltUomEntity> altUomDao =
					getDaoProvider().getSapMaterialAltUomEntityDao();
			Filter<SapMaterialAltUomEntity> filter = altUomDao.createFilter();
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL,
					sapMaterial));
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM_CD, uomCd));
			SapMaterialAltUomEntity altUom = altUomDao.findSingle(filter);
			
			return result = altUom;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Convenience method that returns the dimension code of a standard UoM, because those UoMs
	 * must have a dimension, unlike alternative UoMs.
	 * 
	 * @param uom a standard UoM (which is not an alternative UoM of a material)
	 * @return the dimension code (should never be null).
	 * @throws LogonConversionException 
	 * @see #getBaseUom(UomBsEntity, SapMaterialAltUomEntity, SapMaterialEntity)
	 * @adnlog.notrace
	 */
	private static String getDimensionCd(UomBsEntity uom) throws LogonConversionException {
		UomDimensionBsEntity uomDimension = uom.getUomDimension();
		
		if (uomDimension == null) {
			String uomCd = uom.getUomCd();
			
			throw new LogonConversionException("Dimension is unknown for '" + uomCd + "'. Either "
					+ "the starndard UoM or the material-specific base UoM should define it. "
					+ "(Hint: missing alternative UoM?)",
					Reason.RES_ERROR_UOM_CONV_DIMENSION_UNKNOWN);
		}
		
		String uomDimensionCd = uomDimension.getUomDimensionCd();
		
		return uomDimensionCd;
	}
	
	private UomConversionResult convertBetweenAltUomAndBaseUom(UomConversionResult prevResult,
			SapMaterialAltUomEntity altUom, boolean forward) throws LogonConversionException {
		// [[
		UomConversionResult result = null;
		Object[][] params = {{"prevResult", prevResult},
							{"altUom", altUom},
							{"forward", forward + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertBetweenAltUomAndBaseUom", params);
		}
		// ]]
		try {
			Long numerator = altUom.getNumeratorForConversion();
			Long denominator = altUom.getDenominatorForConversion();
			
			return result = convert(prevResult, numerator, denominator, forward);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private UomConversionResult convertBetweenUomAndDimension(UomConversionResult prevResult,
			UomBsEntity uom, boolean forward) throws LogonConversionException {
		// [[
		UomConversionResult result = null;
		Object[][] params = {{"prevResult", prevResult},
							{"uom", uom},
							{"forward", forward + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertBetweenUomAndDimension", params);
		}
		// ]]
		try {
			Long numerator = uom.getNumeratorForConversion();
			Long denominator = uom.getDenominatorForConversion();
			
			return result = convert(prevResult, numerator, denominator, forward);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Similar to {@link #convert(BigDecimal, Long, Long, boolean)},
	 * but it also stores the conversion factor into an {@link UomConversionResult} object.
	 * 
	 * @param prevResult - the previous conversion result (or it may be the initial value too)
	 *     containing the quantity to convert
	 * @param numerator - part of the conversion factor
	 * @param denominator - part of the conversion factor
	 * @param forward - true for conversion from standard UoM to dimension, or from alternative
	 *     UoM to base UoM, false for backwards conversion
	 * @return the converted amount and the new numerator/denominator for the overall conversion
	 * @throws LogonConversionException 
	 * @see #convert(BigDecimal, Long, Long, boolean)
	 */
	private UomConversionResult convert(UomConversionResult prevResult, Long numerator,
			Long denominator, boolean forward) throws LogonConversionException {
		// [[
		UomConversionResult result = null;
		Object[][] params = {{"prevResult", prevResult},
							{"numerator", numerator},
							{"denominator", denominator},
							{"forward", forward + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convert", params);
		}
		// ]]
		try {
			BigDecimal quantity = prevResult.getQuantity();
			
			quantity = convert(quantity, numerator, denominator, forward);
			
			numerator *= prevResult.getNumerator();
			denominator *= prevResult.getDenominator();
			
			return result = new UomConversionResult(quantity, numerator, denominator);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Convert a vehicle capacity value into a rational number (numerator and denominator). This
	 * may lose precision, but if we do not do this, we cannot calculate the numerator and
	 * denominator for the overall conversion in the end.
	 * 
	 * @param uom
	 * @param truckType
	 * @param wagonType
	 * @return
	 */
	private static Long[] calculateNumDenForVehicle(UomBsEntity uom, TruckTypeBsEntity truckType,
			WagonTypeBsEntity wagonType) {
		// [[
		Long[] result = null;
		Object[][] params = {{"uom", uom},
							{"truckType", truckType},
							{"wagonType", wagonType}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UomConverterServiceBean.class, "calculateNumDenForVehicle", params);
		}
		// ]]
		try {
			String uomCd = uom.getUomCd();
			BigDecimal planningNetWeight = null;
			
			if (uomCd.equals(Constant.UOM_LKW)) {
				if (truckType == null) {
					throw new IllegalArgumentException("UoM is " + uomCd
							+ ", but no truck type was specified");
				}
				
				planningNetWeight = truckType.getPlanningNetWeightT();
			}
			else if (uomCd.equals(Constant.UOM_WAGON)) { 
				if (wagonType == null) {
					throw new IllegalArgumentException("UoM is " + uomCd
							+ ", but no wagon type was specified");
				}
				
				planningNetWeight = wagonType.getMaxGrossWeightT();
			}
			
			return result = LogonUtil.createNominatorDenumerator(planningNetWeight,
					Constant.SCALE);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	
//	/**
//	 * If the UoM is "LKW" (truck) or "BW" (wagon), then the exact amount depends on the capacity
//	 * of the vehicle. AFAIK when we do this conversion, we can only provide a planned/approximate
//	 * conversion here, because the exact values are only known the soonest after loading.
//	 * 
//	 * <p>Note that "LKW" and "BW" have a dimension of "TO", which matches the UoM of the capacity
//	 * values stored in the DB, therefore no extra conversions are needed.</p>
//	 * 
//	 * @param quantity
//	 * @param uom
//	 * @param truckType
//	 * @param wagonType
//	 * @param forward
//	 * @return
//	 * @throws NullPointerException if the vehicle type has no max gross weight defined.
//	 */
//	private static VehicleSpecificConversionResult convertVehicleSpecificUom(BigDecimal quantity,
//			UomBsEntity uom, TruckTypeBsEntity truckType, WagonTypeBsEntity wagonType,
//			boolean forward) {
//		// [[
//		VehicleSpecificConversionResult result = null;
//		Object[][] params = {{"quantity", quantity},
//							{"uom", uom},
//							{"truckType", truckType},
//							{"wagonType", wagonType},
//							{"forward", forward + ""}};
//		boolean logDebugEnabled = LOG.isDebugEnabled();
//		if (logDebugEnabled) {
//			LOG.entering(UomConverterServiceBean.class, "convertVehicleSpecificUom", params);
//		}
//		// ]]
//		try {
//			String whichUom = (forward) ? "fromUom" : "toUom";
//			BigDecimal factor = null;
//			boolean handled = false;
//			String uomCd = uom.getUomCd();
//			
//			if (uomCd.equals(Constant.UOM_LKW)) {
//				if (truckType == null) {
//					throw new IllegalArgumentException(whichUom + "=" + uomCd
//							+ ", but no truck type was specified");
//				}
//				
//				factor = truckType.getPlanningNetWeightT();
//				handled = true;
//			}
//			else if (uomCd.equals(Constant.UOM_WAGON)) { 
//				if (wagonType == null) {
//					throw new IllegalArgumentException(whichUom + "=" + uomCd
//							+ ", but no wagon type was specified");
//				}
//				
//				factor = wagonType.getMaxGrossWeightT();
//				handled = true;
//			}
//			
//			if (handled) {
//				if (factor == null) {
//					throw new NullPointerException(whichUom + "=" + uomCd
//							+ ", but vehicle type has no max gross weight defined");
//				}
//				
//				if (forward) {
//					quantity = quantity.multiply(factor);
//				}
//				else {
//					quantity = quantity.divide(factor, Constant.SCALE, BigDecimal.ROUND_HALF_UP);
//				}
//			}
//			
//			return result = new VehicleSpecificConversionResult(quantity, handled);
//		}
//		finally {
//		// [[
//			if (logDebugEnabled) {
//				LOG.exiting(result);
//			}
//		// ]]
//		}
//	}
//	
//	/**
//	 * A bean for storing the results of {@link UomConverterServiceBean#convertVehicleSpecificUom(
//	 * BigDecimal, UomBsEntity, TruckTypeBsEntity, WagonTypeBsEntity, boolean)}.
//	 * 
//	 * @author AdNovum Informatik AG
//	 */
//	private static class VehicleSpecificConversionResult {
//		
//		private final BigDecimal quantity;
//		private final boolean handled;
//		
//		/**
//		 * @param quantity
//		 * @param changed
//		 */
//		public VehicleSpecificConversionResult(BigDecimal quantity, boolean handled) {
//			this.quantity = quantity;
//			this.handled = handled;
//		}
//		
//		public BigDecimal getQuantity() {
//			return quantity;
//		}
//		
//		public boolean hasBeenHandled() {
//			return handled;
//		}
//		
//	}
//	
//	/**
//	 * Vehicles can have two kind of capacities: one is given in TO, and the other in MTQ. When
//	 * both values are defined for a vehicle, we must check whether one of those measurements
//	 * do not overflow the vehicle in the other measurement.
//	 * 
//	 * @param quantity
//	 * @param toUom
//	 * @param toUom2 
//	 * @param truckType
//	 * @param wagonType
//	 * @return
//	 */
//	private static BigDecimal checkForVehicleSpecificLimits(BigDecimal quantity, UomBsEntity uom,
//			TruckTypeBsEntity truckType, WagonTypeBsEntity wagonType, boolean forward) {
//		// [[
//		BigDecimal result = null;
//		Object[][] params = {{"quantity", quantity},
//							{"uom", uom},
//							{"truckType", truckType},
//							{"wagonType", wagonType},
//							{"forward", forward + ""}};
//		boolean logDebugEnabled = LOG.isDebugEnabled();
//		if (logDebugEnabled) {
//			LOG.entering(UomConverterServiceBean.class, "checkForVehicleSpecificLimits", params);
//		}
//		// ]]
//		try {
//			String whichUom = (forward) ? "fromUom" : "toUom";
//			
//			if (uom.equals(Constant.UOM_LKW) || uom.equals(Constant.UOM_WAGON)
//					|| uom.equals(Constant.UOM_MTQ)) {
//				/*
//				 * FIXME 11-10-2012/romek: We will not handle this case yet because it would
//				 * significantly increase the complexity of the converter service.
//				 */
//				LOG.error(whichUom + "=" + uom.getUomCd() + ", but capacity check for this UoM "
//						+ "is not supported yet, skipping.");
//			}
//			
//			return result = quantity;
//		}
//		finally {
//		// [[
//			if (logDebugEnabled) {
//				LOG.exiting(result);
//			}
//		// ]]
//		}
//	}
	
	/**
	 * Find a base UoM or alternative UoM that matches the given dimension so that it can be
	 * used to convert between standard UoMs and material-specific UoMs.
	 * 
	 * @param sapMaterial
	 * @param altUom an already known alternative UoM, eg. "fromUom" or "toUom" 
	 * @param targetDimCd the desired UoM dimension
	 * @return
	 */
	private Object findAltOrBaseUomWithCommonDim(SapMaterialEntity sapMaterial,
			SapMaterialAltUomEntity altUom, String targetDimCd) {
		// [[
		Object result = null;
		Object[][] params = {{"sapMaterial", sapMaterial},
							{"altUom", altUom},
							{"targetDimCd", targetDimCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findAltOrBaseUomWithCommonDim", params);
		}
		// ]]
		try {
			// Check if the already known alternative UoM matches the desired dimension.
			if (altUom != null) {
				UomBsEntity uom = altUom.getUom();
				UomDimensionBsEntity uomDimension = uom.getUomDimension();
					
				if (uomDimension != null) {
					String uomDimensionCd = uomDimension.getUomDimensionCd();
					
					if (uomDimensionCd.equals(targetDimCd)) {
						return result = altUom;
					}
				}
			}
			
			// Check if the base UoM of the material matches the desired dimension.
			UomBsEntity baseUom = sapMaterial.getBaseUom();
			UomDimensionBsEntity uomDimension = baseUom.getUomDimension();
			
			if (uomDimension != null) {
				String uomDimensionCd = uomDimension.getUomDimensionCd();
				
				if (uomDimensionCd.equals(targetDimCd)) {
					return result = baseUom;
				}
			}
			
			// Find an alternative UoM that can still match the desired dimension.
			ReadWriteDao<SapMaterialAltUomEntity> altUomDao =
					getDaoProvider().getSapMaterialAltUomEntityDao();
			Filter<SapMaterialAltUomEntity> filter = altUomDao.createFilter();
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL,
					sapMaterial));
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_UOM + "."
					+ UomBsEntity.ATTR_UOM_DIMENSION_CD, targetDimCd));
			return result = altUomDao.findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Attempts to simplify a given pair of numerator and denominator, or leaves them unchanged
	 * if a common divisor cannot be found.
	 * 
	 * @param ucr 
	 * @return
	 */
	private UomConversionResult simplifyNumAndDen(UomConversionResult ucr) {
		// [[
		UomConversionResult result = null;
		Object[][] params = {{"ucr", ucr}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "simplifyNumAndDen", params);
		}
		// ]]
		try {
			BigDecimal quantity = ucr.getQuantity();
			Long numerator = ucr.getNumerator();
			Long denominator = ucr.getDenominator();
			
			Long gcd = LogonUtil.findGCD(numerator, denominator);
			
			if (gcd != null) {
				numerator /= gcd;
				denominator /= gcd;
				
				ucr = new UomConversionResult(quantity, numerator, denominator);
			}
			
			return result = ucr;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	/**
	 * Convenience method for converting the given quantity of the order item to the
	 * UoM for the given contract item.
	 *
	 * @param orderItem  	items whose quantity is being converted (need just for error handling)
	 * @param qty		 	the quantity to convert
	 * @param fromUoM	 	the quantity's unit of measure
	 * @param contractItem	used to determine the toUoM for the conversion process
	 *
	 * @return the converted result as a BigDecimal
	 */
	public BigDecimal convertOrderItemQtyToContractItemUoM(
			OrderItemEntity orderItem,
			BigDecimal qty,
			UomBsEntity fromUoM,
			SapContractItemEntity contractItem) {

		// [[
		BigDecimal result = null;
		Object[][] params = {{"orderItem", orderItem},
							 {"qty", qty},
							 {"fromUoM", fromUoM},
							 {"contractItem", contractItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertOrderItemQtyToContractItemUoM", params);
		}
		// ]]
		try {
			UomBsEntity toUoM = contractItem.getTargetQuantityUom();
			try {
				UomConversionResult conversion = convert(
						qty,
						fromUoM,
						toUoM,
						contractItem.getSapMaterial(),
						null,
						null,
						false);

				if (conversion == null || conversion.getQuantity() == null) {
					throw new LogonConversionException(Reason.RES_ERROR_IMPL);
				}

				return result = conversion.getQuantity();
			}
			catch (LogonConversionException e) {
				String msg = String.format(
						"Converting %s from UoM '%s' to UoM '%s' failed for order item: %s_%s",
						qty,
						fromUoM.getUomCd(),
						toUoM.getUomCd(),
						orderItem.getOrder().getOrderNumber(),
						orderItem.getOrderItemNo());

				throw new LogonException(msg, e);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public BigDecimal convertQuantityFromContractItemUoM(OrderItemEntity orderItem, SapContractItemEntity contractItem, BigDecimal quantity, UomBsEntity toUom) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"orderItem", orderItem},
							{"contractItem", contractItem},
							{"quantity", quantity},
							{"toUom", toUom}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertQuantityFromContractItemUoM", params);
		}
		// ]]
		UomBsEntity fromUom = contractItem.getTargetQuantityUom();
		
		if (fromUom == null) {
			String errMsg = "target quantity UoM of the SAP contract item is null, sapContractItemId: " + contractItem.getSapContractItemId();
			throw new IllegalArgumentException(errMsg);
		}
		
		try {
			UomConversionResult ucr = convert(quantity, contractItem.getTargetQuantityUom(), toUom, contractItem.getSapMaterial(), null, null, false);
			return result = ucr.getQuantity();
		}
		catch (LogonConversionException e) {
			String msg = String.format(
					"Converting %s from UoM '%s' to UoM '%s' failed for order item: %s_%s",
					quantity,
					fromUom.getUomCd(),
					toUom.getUomCd(),
					orderItem.getOrder().getOrderNumber(),
					orderItem.getOrderItemNo());
		
			throw new LogonException(msg, e);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<SapMaterialAltUomEntity> fetchAltUom(SapMaterialEntity sapMaterial) {
		// [[
		List<SapMaterialAltUomEntity> result = null;
		Object[][] params = {{"sapMaterial", sapMaterial}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAltUom", params);
		}
		// ]]
		try {
			if (sapMaterial == null) {
				return result = null;
			}
			
			final ReadWriteDao<SapMaterialAltUomEntity> altUomDao =
					getDaoProvider().getSapMaterialAltUomEntityDao();
			final Filter<SapMaterialAltUomEntity> filter = altUomDao.createFilter();
			filter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL,
					sapMaterial));
			return result = altUomDao.find(filter);
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoWagonLoadListEntity;
import com.crh.logon.utils.LogonConversionException;

/**
 * Created for the rail scheduling screen to extend the view with calculated conversion factor
 * from net quantity UoM to tons.
 * 
 * <p>The whole class is only used for the bag case.</p>
 * 
 * @author AdNovum Informatik AG
 */
public class LoadWrapper {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LoadWrapper.class);
	
	private final DispoWagonLoadListEntity load;
	private final BigDecimal netQuantityInTons;
	
	/**
	 * @param load
	 */
	public LoadWrapper(DispoWagonLoadListEntity load, UomConverterServiceLocalInterface uomConverterService) {
		this.load = load;
		
		netQuantityInTons = calculateNetQuantityInTons(uomConverterService);
	}
	
	public DispoWagonLoadListEntity getLoad() {
		return load;
	}
	
	public BigDecimal getNetQuantityInTons() {
		return netQuantityInTons;
	}
	
	// TODO 29-Nov-2012/romek: Similar method in WagonWrapper, ContainerWrapper, LoadWrapper.
	private BigDecimal calculateNetQuantityInTons(UomConverterServiceLocalInterface uomConverterService) {
		BigDecimal netQuantity = load.getNetQuantity();
		String netQuantityUomCd = load.getNetQuantityUomCd();
		
		BigDecimal netQuantityInTons = null;
		
		if (netQuantity != null && netQuantityUomCd != null) {
			Long sapMaterialId = load.getSapMaterialId(); // Not nullable.
			
			try {
				UomConversionResult ucr = uomConverterService.convert(netQuantity,
						netQuantityUomCd, Constant.UOM_TONS, sapMaterialId, null, null, false);
				
				netQuantityInTons = ucr.getQuantity();
			}
			catch (LogonConversionException e) {
				LOG.warn("Cannot convert wagon net quantity from UoM " + netQuantityUomCd
						+ " to tons (sapMaterialId = " + sapMaterialId + "): " + e.getMessage());
			}
		}
		
		return netQuantityInTons;
	}
	
}

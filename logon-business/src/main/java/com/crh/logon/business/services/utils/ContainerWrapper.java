/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoWContainerListEntity;
import com.crh.logon.utils.LogonConversionException;

/**
 * @author AdNovum Informatik AG
 */
public class ContainerWrapper implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerWrapper.class);
	
	private final DispoWContainerListEntity container;
	private final Collection<String> possibleMaterialList;
	private final BigDecimal netQuantityInTons;

	private TimeZone timeZone;
	
	public ContainerWrapper(DispoWContainerListEntity container,
							UomConverterServiceLocalInterface unitOfMeasurementConverterService, TimeZone timeZone) {
		this.container = container;
		this.timeZone = timeZone;
		
		possibleMaterialList = new HashSet<String>();
		
		if (container.getMaterialName() != null) {
			possibleMaterialList.add(container.getMaterialName());
		}
		
		netQuantityInTons = calculateNetQuantityInTons(unitOfMeasurementConverterService);
	}
	
	public DispoWContainerListEntity getContainer() {
		return container;
	}
	
	public Collection<String> getPossibleMaterialList() {
		return possibleMaterialList;
	}
	
	public void addPossibleMaterial(String materialName) {
		if (container.getMaterialName() == null) {
			possibleMaterialList.add(materialName);
		}
	}
	
	public BigDecimal getNetQuantityInTons() {
		return netQuantityInTons;
	}

	public boolean isLoaded() {
		if (container.getLoadId() != null) {
			if (container.getLogonPackageTypeCd() == null) {
				// We have a load with a non-SAP material.
				// TODO 29-Nov-2012/romek: This is no longer true, also clean up raildispo.js!
				return true;
			}
			if ((container.getNetQuantity() != null) && (container.getNetQuantity() != 0)) {
				// We have a load with a SAP material and there is a net quantity.
				return true;
			}
		}
		
		return false;
	}
	
	// TODO 29-Nov-2012/romek: Similar method in WagonWrapper, ContainerWrapper, LoadWrapper.
	private BigDecimal calculateNetQuantityInTons(UomConverterServiceLocalInterface unitOfMeasurementConverterService) {
		Double netQuantityAsDouble = container.getNetQuantity();
		String netQuantityUomCd = container.getNetQuantityUomCd();
		
		BigDecimal netQuantityInTons = null;
		
		if (netQuantityAsDouble != null && netQuantityUomCd != null) {
			BigDecimal netQuantity = new BigDecimal(netQuantityAsDouble);
			Long sapMaterialId = container.getSapMaterialId(); // Not nullable.
			
			try {
				UomConversionResult ucr = unitOfMeasurementConverterService.convert(netQuantity,
						netQuantityUomCd, Constant.UOM_TONS, sapMaterialId, null, null, false);
				
				netQuantityInTons = ucr.getQuantity();
			}
			catch (LogonConversionException e) {
				LOG.warn("Cannot convert container net quantity from UoM " + netQuantityUomCd
						+ " to tons (sapMaterialId = " + sapMaterialId + "): " + e.getMessage());
			}
		}
		
		return netQuantityInTons;
	}

	public String getLoadingTSAsDayMonthHourMinute() {
		return timeZone.fmtAsDayMonthHourMinuteTrailingDot(container.getLoadingTs());
	}
}

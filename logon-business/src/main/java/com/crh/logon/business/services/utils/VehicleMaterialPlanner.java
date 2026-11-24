/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.beans.MaterialQuantityUomBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
public class VehicleMaterialPlanner implements Serializable {

	private static final long serialVersionUID = 4492487429015747538L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(VehicleMaterialPlanner.class);
	
	final private CustorderSplitWrapper splitWrapper;
	final private Map<Long, BigDecimal> sapMaterialIdQtyInTons;
	final private List<MaterialQuantityUomBean> addedMaterialQuantityUomCds;
	private BigDecimal remainingCapacityInTons;
	
	public VehicleMaterialPlanner(CustorderSplitWrapper splitWrapper) {
		this.splitWrapper = splitWrapper;
		this.remainingCapacityInTons = CustorderUtils.calculatePerSingleDeliveryQuantityInTonsForBagCase(splitWrapper);
		this.sapMaterialIdQtyInTons = new HashMap<Long, BigDecimal>();
		this.addedMaterialQuantityUomCds = new ArrayList<MaterialQuantityUomBean>();
	}
	
	
	public BigDecimal getRemainingCapacity(SapMaterialEntity sapMaterial, String uomCd) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"sapMaterial", sapMaterial},
							{"uomCd", uomCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRemainingCapacity", params);
		}
		// ]]
		try {
			try {
				
				final long deliveryTypeCd = splitWrapper.getCustorder().getDeliveryType().getDeliveryTypeCd();
				
				// all order items go to one truck at bimodal roar-part
				if (LogonUtil.isBimodalTruckPartOrder(deliveryTypeCd)) {
					result = Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE;
				}
				else {
					result = splitWrapper.convertQuantity(sapMaterial, Constant.UOM_TONS, uomCd, this.remainingCapacityInTons).getQuantity();
				}
			}
			catch (LogonException e) {
				LOG.warn("could not convert to tons, no practical limit for this material ("+ Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE_STR + " will be used), material ID: " + sapMaterial.getSapMaterialId());
				result = Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE;
			}
			
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
	
	
	public boolean addMaterial(SapMaterialEntity sapMaterial, BigDecimal qty, String uomCd) {
		// [[
		boolean result = false;
		Object[][] params = {{"sapMaterial", sapMaterial},
							{"qty", qty},
							{"uomCd", uomCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addMaterial", params);
		}
		// ]]
		try {
			BigDecimal qtyInTons = null;
			try {
				qtyInTons = splitWrapper.convertQuantity(sapMaterial, uomCd, Constant.UOM_TONS, qty).getQuantity();
			}
			catch (LogonException e) {
				LOG.warn("could not convert to tons, will be counted as zero tons, material ID: " + sapMaterial.getSapMaterialId());
				qtyInTons = BigDecimal.ZERO;
			}

			if (this.remainingCapacityInTons.compareTo(qtyInTons) < 0) {
				if (logDebugEnabled) {
					LOG.debug("could not add material, remaining weight capacity reached");
				}
				return result = false;
			}
			else {
				this.remainingCapacityInTons = this.remainingCapacityInTons.subtract(qtyInTons);
				
				BigDecimal totalsapMatQtyInTons = sapMaterialIdQtyInTons.get(sapMaterial.getSapMaterialId());
				if (totalsapMatQtyInTons == null) {
					totalsapMatQtyInTons = BigDecimal.ZERO;
				}
				
				totalsapMatQtyInTons = totalsapMatQtyInTons.add(qtyInTons);
				sapMaterialIdQtyInTons.put(sapMaterial.getSapMaterialId(), totalsapMatQtyInTons);
				addedMaterialQuantityUomCds.add(new MaterialQuantityUomBean(sapMaterial.getSapMaterialId(), qty, uomCd));
				
				return result = true;
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
	
	public Map<Long, BigDecimal> getSapMaterialIdQtyInTons() {
		return sapMaterialIdQtyInTons;
	}


	public List<MaterialQuantityUomBean> getAddedMaterialQuantityUomCds() {
		return Collections.unmodifiableList(this.addedMaterialQuantityUomCds);
	}

	
}

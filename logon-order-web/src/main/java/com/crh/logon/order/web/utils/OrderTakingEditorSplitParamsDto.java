/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.crh.logon.utils.StringUtils;


/**
 * @author AdNovum Informatik AG
 */
public class OrderTakingEditorSplitParamsDto {

	// input only parameters
	private List<Long> sapMatIds;
	private Long maxPrioTruckTypeCd; 
	private Long deliveryTypeCd; 
	private Boolean exactCustorderQtyFlag;
	private Boolean exactQtyPerSdoFlag;
	private Double deliveryFqyEveryTime;
	private String deliveryFqyEveryTimeUomCd;
	private Double deliveryFqyQtyPerHre;
	private String deliveryFqyQtyPerHreUomCd;
	private Date delFromDat;
	private Double deliveryTimeTolerance;
	private String deliveryTimeToleranceUomCd;
	
	// input and output parameters
	private List<BigDecimal> orderedQuantities;
	private List<String> orderedUomCds;
	private BigDecimal quantityPerSdo;
	private String quantityPerSdoUomCd;
	private Date delToDat;
	
	// output only parameters
	private int numDeliveries;
	private String numDeliveriesStr;
	private int maxNumOfOrders;
	private String quantityPerSdoStr;
	

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public boolean isValid() {
		
		if (sapMatIds == null || sapMatIds.isEmpty()) {
			return false;
		}
		else {
			for (Long sapMatId : sapMatIds) {
				if (sapMatId == null) {
					return false;
				}
			}
		}
		
		if (deliveryTypeCd == null ||  deliveryTypeCd == -1) {
			return false;
		}
		
		if (delFromDat == null || maxPrioTruckTypeCd == null) {
			return false;
		}
		
		if (orderedQuantities == null && delToDat == null) {
			return false;
		}
		
		if (orderedQuantities == null && deliveryFqyEveryTime == null && deliveryFqyQtyPerHre == null) {
			return false;
		}
		
//		if (orderedQuantity != null && (StringUtils.empty(orderedUomCd) || "-1".equals(orderedUomCd))) {
//			return false;
//		}
//		
//		if (orderedQuantity != null && BigDecimal.ZERO.compareTo(orderedQuantity) >= 0) {
//			return false;
//		}
		
		if (delToDat != null && delToDat.compareTo(delFromDat) < 0) {
			return false;
		}
		
		if (deliveryFqyEveryTime != null && (StringUtils.empty(deliveryFqyEveryTimeUomCd) || "-1".equals(deliveryFqyEveryTimeUomCd))) {
			return false;
		}
		
		if (deliveryFqyEveryTime != null && deliveryFqyEveryTime <= 0.0) {
			return false;
		}
		
		if (deliveryFqyQtyPerHre != null && (StringUtils.empty(deliveryFqyQtyPerHreUomCd) || "-1".equals(deliveryFqyQtyPerHreUomCd))) {
			return false;
		}
		
		if (deliveryFqyQtyPerHre != null && deliveryFqyQtyPerHre <= 0.0) {
			return false;
		}
		
		if (deliveryTimeTolerance != null && (StringUtils.empty(deliveryTimeToleranceUomCd) ||  "-1".equals(deliveryTimeToleranceUomCd))) {
			return false;
		}
		
		if (deliveryTimeTolerance != null && deliveryTimeTolerance < 0.0) {
			return false;
		}
		
		return true;
	}
	

	public Boolean getExactCustorderQtyFlag() {
		return exactCustorderQtyFlag;
	}
	public void setExactCustorderQtyFlag(Boolean exactCustorderQtyFlag) {
		this.exactCustorderQtyFlag = exactCustorderQtyFlag;
	}


	public BigDecimal getQuantityPerSdo() {
		return quantityPerSdo;
	}
	public void setQuantityPerSdo(BigDecimal quantityPerSdo) {
		this.quantityPerSdo = quantityPerSdo;
	}

	public String getQuantityPerSdoUomCd() {
		return quantityPerSdoUomCd;
	}
	public void setQuantityPerSdoUomCd(String quantityPerSdoUomCd) {
		this.quantityPerSdoUomCd = quantityPerSdoUomCd;
	}
	
	public Boolean getExactQtyPerSdoFlag() {
		return exactQtyPerSdoFlag;
	}
	public void setExactQtyPerSdoFlag(Boolean exactQtyPerSdoFlag) {
		this.exactQtyPerSdoFlag = exactQtyPerSdoFlag;
	}

	public Double getDeliveryFqyEveryTime() {
		return deliveryFqyEveryTime;
	}
	public void setDeliveryFqyEveryTime(Double deliveryFqyEveryTime) {
		this.deliveryFqyEveryTime = deliveryFqyEveryTime;
	}

	public Double getDeliveryFqyQtyPerHre() {
		return deliveryFqyQtyPerHre;
	}
	public void setDeliveryFqyQtyPerHre(Double deliveryFqyQtyPerHre) {
		this.deliveryFqyQtyPerHre = deliveryFqyQtyPerHre;
	}

	public Double getDeliveryTimeTolerance() {
		return deliveryTimeTolerance;
	}
	public void setDeliveryTimeTolerance(Double deliveryTimeTolerance) {
		this.deliveryTimeTolerance = deliveryTimeTolerance;
	}
	
	public int getNumDeliveries() {
		return numDeliveries;
	}
	public void setNumDeliveries(int numDeliveries) {
		this.numDeliveries = numDeliveries;
	}

	public String getNumDeliveriesStr() {
		return numDeliveriesStr;
	}
	public void setNumDeliveriesStr(String numDeliveriesStr) {
		this.numDeliveriesStr = numDeliveriesStr;
	}
	
	public int getMaxNumOfOrders() {
		return maxNumOfOrders;
	}
	public void setMaxNumOfOrders(int maxNumOfOrders) {
		this.maxNumOfOrders = maxNumOfOrders;
	}

	public String getQuantityPerSdoStr() {
		return quantityPerSdoStr;
	}
	public void setQuantityPerSdoStr(String quantityPerSdoStr) {
		this.quantityPerSdoStr = quantityPerSdoStr;
	}
	
	public Date getDelFromDat() {
		return delFromDat;
	}
	public void setDelFromDat(Date delFromDat) {
		this.delFromDat = delFromDat;
	}

	public Date getDelToDat() {
		return delToDat;
	}
	public void setDelToDat(Date delToDat) {
		this.delToDat = delToDat;
	}

	public List<Long> getSapMatIds() {
		return sapMatIds;
	}
	public void setSapMatIds(List<Long> sapMatIds) {
		this.sapMatIds = sapMatIds;
	}

	public List<BigDecimal> getOrderedQuantities() {
		return orderedQuantities;
	}
	public void setOrderedQuantities(List<BigDecimal> orderedQuantities) {
		this.orderedQuantities = orderedQuantities;
	}

	public List<String> getOrderedUomCds() {
		return orderedUomCds;
	}
	public void setOrderedUomCds(List<String> orderedUomCds) {
		this.orderedUomCds = orderedUomCds;
	}

	public String getDeliveryFqyEveryTimeUomCd() {
		return deliveryFqyEveryTimeUomCd;
	}
	public void setDeliveryFqyEveryTimeUomCd(String deliveryFqyEveryTimeUomCd) {
		this.deliveryFqyEveryTimeUomCd = deliveryFqyEveryTimeUomCd;
	}

	public String getDeliveryFqyQtyPerHreUomCd() {
		return deliveryFqyQtyPerHreUomCd;
	}
	public void setDeliveryFqyQtyPerHreUomCd(String deliveryFqyQtyPerHreUomCd) {
		this.deliveryFqyQtyPerHreUomCd = deliveryFqyQtyPerHreUomCd;
	}

	public String getDeliveryTimeToleranceUomCd() {
		return deliveryTimeToleranceUomCd;
	}
	public void setDeliveryTimeToleranceUomCd(String deliveryTimeToleranceUomCd) {
		this.deliveryTimeToleranceUomCd = deliveryTimeToleranceUomCd;
	}

	public Long getMaxPrioTruckTypeCd() {
		return maxPrioTruckTypeCd;
	}
	public void setMaxPrioTruckTypeCd(Long maxPrioTruckTypeCd) {
		this.maxPrioTruckTypeCd = maxPrioTruckTypeCd;
	}
	
	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}
	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
	}
	
}

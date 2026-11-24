/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author AdNovum Informatik AG
 */
public class TruckRouteDataBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long truckRouteId;
	private Long startShippingpointId;
	private String startShippingpointName;
	private Long endShiptoBpId;
	private String endShiptoBpSapId;
	private String endShiptoBpName1;
	private String endShiptoBpPostalCode;
	private String endShiptoBpCity;
	private Long timeCalculatedMin;
	private BigDecimal timeCorrectionFactor;
	private Double timePlanned;
	private Long distanceCalculatedM;
    private BigDecimal distanceCorrectionFactor;
    private Long correctedDistance;
	private String routeComment;
	private String sapTrspModeCd;
	private String sapTrspModeTxt;
	private String langCd;
	
	public Long getTruckRouteId() {
		return truckRouteId;
	}
	public void setTruckRouteId(Long truckRouteId) {
		this.truckRouteId = truckRouteId;
	}
	public Long getStartShippingpointId() {
		return startShippingpointId;
	}
	public void setStartShippingpointId(Long startShippingpointId) {
		this.startShippingpointId = startShippingpointId;
	}
	public String getStartShippingpointName() {
		return startShippingpointName;
	}
	public void setStartShippingpointName(String startShippingpointName) {
		this.startShippingpointName = startShippingpointName;
	}
	public Long getEndShiptoBpId() {
		return endShiptoBpId;
	}
	public void setEndShiptoBpId(Long endShiptoBpId) {
		this.endShiptoBpId = endShiptoBpId;
	}
	public String getEndShiptoBpSapId() {
		return endShiptoBpSapId;
	}
	public void setEndShiptoBpSapId(String endShiptoBpSapId) {
		this.endShiptoBpSapId = endShiptoBpSapId;
	}
	public String getEndShiptoBpName1() {
		return endShiptoBpName1;
	}
	public void setEndShiptoBpName1(String endShiptoBpName1) {
		this.endShiptoBpName1 = endShiptoBpName1;
	}
	public String getEndShiptoBpPostalCode() {
		return endShiptoBpPostalCode;
	}
	public void setEndShiptoBpPostalCode(String endShiptoBpPostalCode) {
		this.endShiptoBpPostalCode = endShiptoBpPostalCode;
	}
	public String getEndShiptoBpCity() {
		return endShiptoBpCity;
	}
	public void setEndShiptoBpCity(String endShiptoBpCity) {
		this.endShiptoBpCity = endShiptoBpCity;
	}
	public Long getTimeCalculatedMin() {
		return timeCalculatedMin;
	}
	public void setTimeCalculatedMin(Long timeCalculatedMin) {
		this.timeCalculatedMin = timeCalculatedMin;
	}
	public BigDecimal getTimeCorrectionFactor() {
		return timeCorrectionFactor;
	}
	public void setTimeCorrectionFactor(BigDecimal timeCorrectionFactor) {
		this.timeCorrectionFactor = timeCorrectionFactor;
	}
	public Double getTimePlanned() {
		return timePlanned;
	}
	public void setTimePlanned(Double timePlanned) {
		this.timePlanned = timePlanned;
	}
	public Long getDistanceCalculatedM() {
		return distanceCalculatedM;
	}
	public void setDistanceCalculatedM(Long distanceCalculatedM) {
		this.distanceCalculatedM = distanceCalculatedM;
	}
    public void setDistanceCorrectionFactor(java.math.BigDecimal distanceCorrectionFactor) {
        this.distanceCorrectionFactor = distanceCorrectionFactor;
    }
    public java.math.BigDecimal getDistanceCorrectionFactor() {
        return distanceCorrectionFactor;
    }
    public void setCorrectedDistance(Long correctedDistance) {
        this.correctedDistance = correctedDistance;
    }
    public Long getCorrectedDistance() {
        return correctedDistance;
    }
	public String getRouteComment() {
		return routeComment;
	}
	public void setRouteComment(String routeComment) {
		this.routeComment = routeComment;
	}
	public String getSapTrspModeCd() {
		return sapTrspModeCd;
	}
	public void setSapTrspModeCd(String sapTrspModeCd) {
		this.sapTrspModeCd = sapTrspModeCd;
	}
	public String getSapTrspModeTxt() {
		return sapTrspModeTxt;
	}
	public void setSapTrspModeTxt(String sapTrspModeTxt) {
		this.sapTrspModeTxt = sapTrspModeTxt;
	}
	public String getLangCd() {
		return langCd;
	}
	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}
}

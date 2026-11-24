/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.dispo.AssignmentEditForm;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.AssignPartStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.web.utils.UserProvider;

/**
 * @author AdNovum Informatik AG
 */
public class TourPart implements Serializable {
	
	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TourPart.class);

	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_PRELOAD = 0; 
	public static final int TYPE_LOAD = 1;
	public static final int TYPE_DELIVERY = 2;
	public static final int TYPE_RETURN = 3;
	
	private AssignmentPartEntity arr;
	private AssignmentPartEntity dep; 
	private int type;
	private int seqNum;
	private Long transportDistanceM;

	private TimeZone deliveryPlantTz;
	private TimeZone shiptoTz;
	private TimeZone lnfShiptoTz;

	private Locale locale;

	// LOGON-6664: move from AssignmentEditForm to the individual TourParts, however, initialized by form
	private boolean isAcrossMidnight;
	
	public TourPart (AssignmentPartEntity arr,	AssignmentPartEntity dep, Long transportDistanceM) {
		// [[
		Object[][] params = {{"arr", arr},
							{"dep", dep}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TourPart", params);
		}
		// ]]
		try {
			this.arr = arr;
			this.dep = dep;
			this.transportDistanceM = transportDistanceM;
			
			if (arr == null) {
				type = TYPE_PRELOAD;
				return;
			}
			
			long apOneType = arr.getAssignmentPartType().getAssignmentPartTypeCd().longValue();
			if ( apOneType == Constant.ASSIGNMENT_PART_TYPE_LOAD ) {
				type = TYPE_LOAD;
			} else if ( apOneType == Constant.ASSIGNMENT_PART_TYPE_UNLOAD ) {
				type = TYPE_DELIVERY;
			} else if ( apOneType == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
				type = TYPE_RETURN;
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
	
	public boolean getIsBagOrderLoadPart(){
		return (type == TYPE_LOAD) && (arr != null) && (arr.getLoad() != null) && (arr.getLoad().getSapMaterial().getMaterialPackage().getLogonPackageType() != null) &&
		 (arr.getLoad().getSapMaterial().getMaterialPackage().getLogonPackageType().getPackageTypeCd() == Constant.PACKAGE_TYPE_BBAG); 
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	
	public Date getPlannedArrival() {
		if (arr != null) {
			return arr.getPlannedStart();
		}
		return null;
	}

	public String getPlannedArrivalFmt() {
		return formatDate(getPlannedArrival());
	}

	public Date getRealArrival() {
		if (arr != null) {
			return arr.getRealStart();
		}
		return null;
	}

	public String getRealArrivalFmt() {
		return formatDate(getRealArrival());
	}

	public Date getPlannedDeparture() {
		if (dep != null) {
			return dep.getPlannedStart(); 
		}
		return null;
	}

	public String getPlannedDepartureFmt() {
		return formatDate(getPlannedDeparture());
	}

	public Date getRealDeparture() {
		if (dep != null) {
			return dep.getRealStart();
		}
		return null;
	}

	public String getRealDepartureFmt() {
		return formatDate(getRealDeparture());
	}

	public AssignmentPartEntity getLocationAp() {
		if (arr == null) {
			return dep;
		}
		return arr;
	}
	
	public String getLocationTx() {
		String result = "";
		AssignmentPartEntity ap = getLocationAp();
		
		if ( ap.getShippingpoint() != null && StringUtils.notEmpty(ap.getShippingpoint().getShippingpointName()) ) {
			result = ap.getShippingpoint().getShippingpointName();
		}
		else if ( ap.getShipto() != null && StringUtils.notEmpty(ap.getShipto().getShiptoSapBp().getName1()) && !ap.getLnfShiptoFlag().booleanValue()) {
			result = ap.getShipto().getShiptoSapBp().getName1();
		} 
		else if ( ap.getHomebaseFlag() ) {
			result = "homebase";
		}
		else if (ap.getSiteName() != null) {
			result = ap.getSiteName();
		}
		else if (ap.getCoordLat() != null && ap.getCoordLon() != null) {
			result = (ap.getCoordLat()) + " " + (ap.getCoordLon());
		}
		return result;
	}
	
	public String getLocationType() {
		String result = "";
		AssignmentPartEntity ap = getLocationAp();
		
		if ( ap.getShippingpoint() != null && StringUtils.notEmpty(ap.getShippingpoint().getShippingpointName()) ) {
			result = AssignmentEditForm.POSITION_SHIPPINGPOINT;
		}
		else if ( ap.getShipto() != null && StringUtils.notEmpty(ap.getShipto().getShiptoSapBp().getName1()) ) {
			result = AssignmentEditForm.POSITION_SHIPTO;
		} 
		else if ( ap.getHomebaseFlag() ) {
			result = AssignmentEditForm.POSITION_HOMEBASE;
		}
		else if (ap.getCoordLat() != null && ap.getCoordLon() != null) {
			result = AssignmentEditForm.POSITION_COORDS;
		}
		else {
			result = AssignmentEditForm.POSITION_NONE;
		}
		return result;
	}
	
	public ShippingpointEntity getShippingpoint() {
			return getLocationAp().getShippingpoint();
	}
	
	public void setShippingpoint(ShippingpointEntity sp) {
		if (arr != null) {
			arr.setShippingpoint(sp);
		}
		if (dep != null) {
			dep.setShippingpoint(sp);
		}
	}
	
	public ShiptoEntity getShipto() {
		return getLocationAp().getShipto();
	}
	
	public void setShipto(ShiptoEntity shipto) {
		if (arr != null) {
			arr.setShipto(shipto);
		}
		if (dep != null) {
			dep.setShipto(shipto);
		}
	}
	
	public void setAssignPartStatus(AssignPartStatusBsEntity status) {
		if (arr != null) {
			arr.setAssignPartStatus(status);
		}
		if (dep != null) {
			dep.setAssignPartStatus(status);			
		}
	}
	
	public void setHomebaseFlag(Boolean homebaseFlag) {
		if (arr != null) {
			arr.setHomebaseFlag(homebaseFlag);
		}
		if (dep != null) {
			dep.setHomebaseFlag(homebaseFlag);
		}
	}
	
	public Boolean getHomebaseFlag() {
		return getLocationAp().getHomebaseFlag();
	}
	
	public void setCoordLon(Double coordLon) {
		if (arr != null) {
			arr.setCoordLon(coordLon);
		}
		if (dep != null) {
			dep.setCoordLon(coordLon);
		}
	}
	
	public Double getCoordLon() {
		return getLocationAp().getCoordLon();
	}
	
	public void setCoordLat(Double coordLat) {
		if (arr != null) {
			arr.setCoordLat(coordLat);
		}
		if (dep != null) {
			dep.setCoordLat(coordLat);
		}
	}
	
	public Double getCoordLat() {
		return getLocationAp().getCoordLat();
	}
	
	public void setPlannedDeparture(Date plannedDeparture) {
		if (dep != null) {
			dep.setPlannedStart(plannedDeparture);
		}
	}
	
	public void setRealDeparture(Date realDeparture) {
		if (dep != null) {
			dep.setRealStart(realDeparture);
		}
	}
	
	public void setPlannedArrival(Date plannedArrival) {
		if (arr != null) {
			arr.setPlannedStart(plannedArrival);
		}
	}
	
	public void setRealArrival(Date realArrival) {
		if (arr != null) {
			arr.setRealStart(realArrival);
		}
	}
	
	public OrderEntity getOrder() {
		if (arr != null) {
			return arr.getOrder();
		}
		return null;
	}
	
	public ExtOrderEntity getExtOrder() {
		if (arr != null) {
			return arr.getExtOrder();
		}
		return null;
	}
	
	public Double getActiveCoordLon() {
		AssignmentPartEntity ap = getLocationAp();
		
		String locType = getLocationType();
		if ( AssignmentEditForm.POSITION_COORDS.equals(locType) ||
				(AssignmentEditForm.POSITION_SHIPTO.equals(locType) && ap.getLnfShiptoFlag().booleanValue())) {
			return ap.getCoordLon();
		}
		else if ( AssignmentEditForm.POSITION_SHIPPINGPOINT.equals(locType) ) {
			return ap.getShippingpoint().getCoordLon();
		}
		else if ( AssignmentEditForm.POSITION_SHIPTO.equals(locType) ) {
			return ap.getShipto().getCoordLon();
		}
		else if ( AssignmentEditForm.POSITION_HOMEBASE.equals(locType) ) {
			AssignmentEntity assignment = ap.getAssignment();
			Double homebaseCoordLon = null;
			TruckEntity truck = assignment.getTruck();
			if (truck != null) {
				homebaseCoordLon = truck.getHomebaseCoordLon();
			}
			SimpleVehicleEntity simpleVehicle = assignment.getSimpleVehicle();
			if (simpleVehicle != null) {
				homebaseCoordLon = simpleVehicle.getHomebaseCoordLon();
			}
			return homebaseCoordLon;
		}
		return null;
	}
	
	public void setActiveCoordLat(Double coordLat) {
		
	}
	
	public Double getActiveCoordLat() {
		AssignmentPartEntity ap = getLocationAp();
		
		String locType = getLocationType();
		if ( AssignmentEditForm.POSITION_COORDS.equals(locType) ||
				(AssignmentEditForm.POSITION_SHIPTO.equals(locType) && ap.getLnfShiptoFlag().booleanValue())) {
			return ap.getCoordLat();
		}
		else if ( AssignmentEditForm.POSITION_SHIPPINGPOINT.equals(locType) ) {
			return ap.getShippingpoint().getCoordLat();
		}
		else if ( AssignmentEditForm.POSITION_SHIPTO.equals(locType) ) {
			return ap.getShipto().getCoordLat();
		}
		else if ( AssignmentEditForm.POSITION_HOMEBASE.equals(locType) ) {
			AssignmentEntity assignment = ap.getAssignment();
			Double homebaseCoordLat = null;
			TruckEntity truck = assignment.getTruck();
			if (truck != null) {
				homebaseCoordLat = truck.getHomebaseCoordLat();
			}
			SimpleVehicleEntity simpleVehicle = assignment.getSimpleVehicle();
			if (simpleVehicle != null) {
				homebaseCoordLat = simpleVehicle.getHomebaseCoordLat();
			}
			return homebaseCoordLat;
		}
		return null;
	}
	
	public void setExternalMaterial(String externalMaterial) {
		getLocationAp().getAssignment().setMaterialName(externalMaterial);
	}
	
	public void setExternalQuantity(String externalQuantity) {
		getLocationAp().getAssignment().setMaterialQuantity(externalQuantity == null ? null : new BigDecimal(externalQuantity));
	}
	
	public void setExternalUom(UomBsEntity externalUom) {
		getLocationAp().getAssignment().setMaterialUom(externalUom);
	}
	
	public String getExternalMaterial() {
		return getLocationAp().getAssignment().getMaterialName();
	}
	
	public BigDecimal getExternalQuantity( ) {
		return getLocationAp().getAssignment().getMaterialQuantity();
	}
	
	public UomBsEntity getExternalUom() {
		return getLocationAp().getAssignment().getMaterialUom();
	}

	public void incSeqNum() {
		seqNum++;
	}

	public AssignmentPartEntity getDepartureAp() {
		return dep;
	}
	
	public void setDepartureAp(AssignmentPartEntity ap) {
		dep = ap;
	}

	public AssignmentPartEntity getArrivalAp() {
		return arr;
	}
	
	public long getDurationMin() {
		if ((arr == null) || (dep == null)) {
			return 0;
		}
		return (dep.getPlannedStart().getTime() - arr.getPlannedStart().getTime()) / 60000;
	}

	public String getSiteName() {
		return getLocationAp().getSiteName();
	}

	public void setSiteName(String siteName) {
		if (arr != null) {
			arr.setSiteName(siteName);
		}
		if (dep != null) {
			dep.setSiteName(siteName);
		}
	}

	public String getSiteLoadUnload() {
		return getLocationAp().getSiteLoadUnload();
	}

	public void setSiteLoadUnload(String siteLoadUnload) {
		if (arr != null) {
			arr.setSiteLoadUnload(siteLoadUnload);
		}
		if (dep != null) {
			dep.setSiteLoadUnload(siteLoadUnload);
		}
	}

	public String getSiteContactPersonTxt() {
		return getLocationAp().getSiteContactPersonTxt();
	}

	public void setSiteContactPersonTxt(String siteContactPersonTxt) {
		if (arr != null) {
			arr.setSiteContactPersonTxt(siteContactPersonTxt);
		}
		if (dep != null) {
			dep.setSiteContactPersonTxt(siteContactPersonTxt);
		}
	}

	public String getSiteStreetHouseNo() {
		return getLocationAp().getSiteStreetHouseNo();
	}

	public void setSiteStreetHouseNo(String siteStreetHouseNo) {
		if (arr != null) {
			arr.setSiteStreetHouseNo(siteStreetHouseNo);
		}
		if (dep != null) {
			dep.setSiteStreetHouseNo(siteStreetHouseNo);
		}
	}

	public String getSiteCountry() {
		return getLocationAp().getSiteCountry();
	}

	public void setSiteCountry(String siteCountry) {
		if (arr != null) {
			arr.setSiteCountry(siteCountry);
		}
		if (dep != null) {
			dep.setSiteCountry(siteCountry);
		}
	}

	public String getSitePostalCode() {
		return getLocationAp().getSitePostalCode();
	}

	public void setSitePostalCode(String sitePostalCode) {
		if (arr != null) {
			arr.setSitePostalCode(sitePostalCode);
		}
		if (dep != null) {
			dep.setSitePostalCode(sitePostalCode);
		}
	}

	public String getSiteCity() {
		return getLocationAp().getSiteCity();
	}

	public void setSiteCity(String siteCity) {
		if (arr != null) {
			arr.setSiteCity(siteCity);
		}
		if (dep != null) {
			dep.setSiteCity(siteCity);
		}
	}
	
	public Boolean getLnfShiptoFlag() {
		return getLocationAp().getLnfShiptoFlag();
	}

	public void setLnfShiptoFlag(Boolean lnfShiptoFlag) {
		if (arr != null) {
			arr.setLnfShiptoFlag(lnfShiptoFlag);
		}
		if (dep != null) {
			dep.setLnfShiptoFlag(lnfShiptoFlag);
		}
		
	}

	/**
	 * @return distance of arr if arr is not null and the distance is filled otherwise null 
	 */
	public BigDecimal getDistance() {
		
		if (transportDistanceM == null) {
			return null;
		}
		
		// Have to divide by 1000 for display in KM
		BigDecimal partDistance = new BigDecimal(transportDistanceM);
		BigDecimal result = partDistance.divide(new BigDecimal("1000"));
		return result.stripTrailingZeros();
		
	}
	
	/**
	 * @param distance => input in double like 31.4 km which will be transformed to meters
	 * and set on the arr {@link AssignmentPartEntity}
	 */
	public void setDistance(BigDecimal newDistance) {
		
		BigDecimal result = newDistance.multiply(new BigDecimal("1000"));
		transportDistanceM = result.longValue();
		
	}
	
	/**
	 * @return the transportDistanceM
	 */
	public Long getTransportDistanceM() {
		return transportDistanceM;
	}

	public void setDeliveryPlantTimeZone(TimeZone deliveryPlantTz) {
		this.deliveryPlantTz = deliveryPlantTz;
	}

	public void setShiptoTimeZone(TimeZone shiptoTz) {
		this.shiptoTz = shiptoTz;
	}

	public void setLnfShiptoTimeZone(TimeZone lnfShiptoTz) {
		this.lnfShiptoTz = lnfShiptoTz;
	}

	public void setAcrossMidnight(boolean acrossMidnight) {
		isAcrossMidnight = acrossMidnight;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getArrivalUTCOffset() {
		switch (type) {
			case TYPE_PRELOAD:
				return null;
			default:
				return utcOffsetFor(getPlannedArrival(), getRealArrival());
		}
	}

	public String getDepartureUTCOffset() {
		switch (type) {
			case TYPE_RETURN:
				return null;
			default:
				return utcOffsetFor(getPlannedDeparture(), getRealDeparture());
		}
	}

	public TimeZone getTimeZone() {
		String position = getLocationType();

		switch (type) {
			case TYPE_PRELOAD:   // fall through
			case TYPE_RETURN:
				if (AssignmentEditForm.POSITION_HOMEBASE.equals(position)
						|| AssignmentEditForm.POSITION_SHIPPINGPOINT.equals(position)
						|| AssignmentEditForm.POSITION_COORDS.equals(position)) {
					return deliveryPlantTz;
				}
				if (AssignmentEditForm.POSITION_SHIPTO.equals(position)) {
					return getCurrentShiptoTimeZone();
				}
				break;
			case TYPE_LOAD:
				return deliveryPlantTz;
			case TYPE_DELIVERY:
				return getCurrentShiptoTimeZone();
			default:
				break;
		}

		return null;
	}

	private TimeZone getCurrentShiptoTimeZone() {
		if (lnfShiptoTz != null) {
			return lnfShiptoTz;
		}
		return shiptoTz;
	}

	private String formatDate(Date d) {
		if (d == null) {
			return null;
		}

		TimeZone tz = getTimeZone();
		if (tz != null) {
			if (isAcrossMidnight) {
				return tz.fmtAsDayMonthYearHourMinute(d);
			}
			else {
				return tz.fmtAsHourMinute(d);
			}
		}
		else {
			if (isAcrossMidnight) {
				return DateTimeFormat.asLogonDayMonthYearHourMinute(d, locale, null);
			}
			else {
				return DateTimeFormat.asHourMinute(d);
			}
		}
	}

	private String utcOffsetFor(Date planned, Date real) {
		Date d = planned;
		if (d == null) {
			d = real;
		}
		if (d == null) {
			return null;
		}

		TimeZone tz = getTimeZone();
		if (tz == null) {
			return null;
		}

		if (tz.isSame(UserProvider.getTimezone())) {
			return null;
		}

		return tz.fmtAsUTCOffset(d);
	}
}

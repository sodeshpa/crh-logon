package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.utils.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;


public class DistanceValidationInfoBean  implements Serializable {

	private static final long serialVersionUID = 1L;


	public static final String POPUP_LITDICT_KEY_ACTIVE = "application.errors.custordervalidation.distance.rules.popup.ACTIVE";
	public static final String POPUP_LITDICT_KEY_INACTIVE = "application.errors.custordervalidation.distance.rules.popup.INACTIVE";

	private List<ValidationErrorReason> distanceBasedErrorReasons;

	private boolean dbltActiveFlag = false;

	private String l1 = "";
	private String l2 = "";
	private String distance = "";

	private String h1 = "H1";
	private String h2 = "H2";

	private DistanceValidationInfoRow distanceUnderL1;
	private DistanceValidationInfoRow distanceBetweenL1AndL2;
	private DistanceValidationInfoRow distanceOverL2;

	private Date earliestPossibleDeliveryFromDate;

	public boolean isRowDataEmpty(){
		return (distanceUnderL1 == null || distanceBetweenL1AndL2 == null || distanceOverL2 == null);
	}

	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		return tsb.toString();
	}



	public String getL1() {
		return l1;
	}

	public void setL1(String l1) {
		this.l1 = l1;
	}

	public String getL2() {
		return l2;
	}

	public void setL2(String l2) {
		this.l2 = l2;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void setDistanceFromMeters(String meters) {
		if (StringUtils.empty(meters)) {
			this.distance = "";
			return;
		}

		Long distanceInMeter = Long.valueOf(meters);
		this.distance = String.valueOf(distanceInMeter.doubleValue() * 0.001);
	}


	public String getH1() {
		return h1;
	}

	public void setH1(String h1) {
		this.h1 = h1;
	}

	public String getH2() {
		return h2;
	}

	public void setH2(String h2) {
		this.h2 = h2;
	}

	public boolean isDbltActiveFlag() {
		return dbltActiveFlag;
	}

	public void setDbltActiveFlag(boolean dbltActiveFlag) {
		this.dbltActiveFlag = dbltActiveFlag;
	}

	public List<ValidationErrorReason> getDistanceBasedErrorReasons() {
		if (this.distanceBasedErrorReasons == null) {
			this.distanceBasedErrorReasons = new ArrayList<ValidationErrorReason>();
		}
		return distanceBasedErrorReasons;
	}

	public void addDistanceBasedErrorReason(
			ValidationErrorReason distanceBasedErrorReason) {
		if (this.distanceBasedErrorReasons == null) {
			this.distanceBasedErrorReasons = new ArrayList<ValidationErrorReason>();
		}
		this.distanceBasedErrorReasons.add(distanceBasedErrorReason);
	}

	public void addDistanceBasedErrorReasons(
			List<ValidationErrorReason> distanceBasedErrorReasons) {
		if (this.distanceBasedErrorReasons == null) {
			this.distanceBasedErrorReasons = new ArrayList<ValidationErrorReason>();
		}
		this.distanceBasedErrorReasons.addAll(distanceBasedErrorReasons);
	}


	public DistanceValidationInfoRow getDistanceUnderL1() {
		return distanceUnderL1;
	}

	public void setDistanceUnderL1(
			DistanceValidationInfoRow distanceUnderL1) {
		this.distanceUnderL1 = distanceUnderL1;
	}

	public DistanceValidationInfoRow getDistanceBetweenL1AndL2() {
		return distanceBetweenL1AndL2;
	}

	public void setDistanceBetweenL1AndL2(
			DistanceValidationInfoRow distanceBetweenL1AndL2) {
		this.distanceBetweenL1AndL2 = distanceBetweenL1AndL2;
	}

	public DistanceValidationInfoRow getDistanceOverL2() {
		return distanceOverL2;
	}

	public void setDistanceOverL2(
			DistanceValidationInfoRow distanceOverL2) {
		this.distanceOverL2 = distanceOverL2;
	}

	public Date getEarliestPossibleDeliveryFromDate() {
		return earliestPossibleDeliveryFromDate;
	}

	public void setEarliestPossibleDeliveryFromDate(Date earliestPossibleDeliveryFromDate) {
		this.earliestPossibleDeliveryFromDate = earliestPossibleDeliveryFromDate;
	}

}

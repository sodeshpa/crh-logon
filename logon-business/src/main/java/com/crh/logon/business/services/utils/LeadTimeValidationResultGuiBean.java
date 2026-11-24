package com.crh.logon.business.services.utils;

public class LeadTimeValidationResultGuiBean {


	/**
	 * Holds the text of distance validation rules
	 */
	private String distanceValidationPopUpText = "";

	private String earliestPossibleDeliveryFromDateString;
	private String earliestPossibleDeliveryFromTimeString;
	private String earliestPossibleDeliveryToDateString;
	private String earliestPossibleDeliveryToTimeString;

	private Long distanceMeters;


	public String getDistanceValidationPopUpText() {
		return distanceValidationPopUpText;
	}

	public void setDistanceValidationPopUpText(String distanceValidationPopUpText) {
		this.distanceValidationPopUpText = distanceValidationPopUpText;
	}


	public String getEarliestPossibleDeliveryFromDateString() {
		return earliestPossibleDeliveryFromDateString;
	}

	public void setEarliestPossibleDeliveryFromDateString(String earliestPossibleDeliveryFromDateString) {
		this.earliestPossibleDeliveryFromDateString = earliestPossibleDeliveryFromDateString;
	}

	public String getEarliestPossibleDeliveryFromTimeString() {
		return earliestPossibleDeliveryFromTimeString;
	}

	public void setEarliestPossibleDeliveryFromTimeString(String earliestPossibleDeliveryFromTimeString) {
		this.earliestPossibleDeliveryFromTimeString = earliestPossibleDeliveryFromTimeString;
	}

	public String getEarliestPossibleDeliveryToDateString() {
		return earliestPossibleDeliveryToDateString;
	}

	public void setEarliestPossibleDeliveryToDateString(String earliestPossibleDeliveryToDateString) {
		this.earliestPossibleDeliveryToDateString = earliestPossibleDeliveryToDateString;
	}

	public String getEarliestPossibleDeliveryToTimeString() {
		return earliestPossibleDeliveryToTimeString;
	}

	public void setEarliestPossibleDeliveryToTimeString(String earliestPossibleDeliveryToTimeString) {
		this.earliestPossibleDeliveryToTimeString = earliestPossibleDeliveryToTimeString;
	}

	public Long getDistanceMeters() {
		return distanceMeters;
	}

	public void setDistanceMeters(Long distanceMeters) {
		this.distanceMeters = distanceMeters;
	}

	@Override
	public String toString() {
		return "LeadTimeValidationResultGuiBean{" +
				"distanceValidationPopUpText='" + distanceValidationPopUpText + '\'' +
				", earliestPossibleDeliveryFromDateString='" + earliestPossibleDeliveryFromDateString + '\'' +
				", earliestPossibleDeliveryFromTimeString='" + earliestPossibleDeliveryFromTimeString + '\'' +
				", earliestPossibleDeliveryToDateString='" + earliestPossibleDeliveryToDateString + '\'' +
				", earliestPossibleDeliveryToTimeString='" + earliestPossibleDeliveryToTimeString + '\'' +
				", distanceMeters=" + distanceMeters +
				'}';
	}
}


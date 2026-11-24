/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class RailTimetableDetailBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDistanceDetailBean.class);

	public static final String FORM_TYPE_NEW = "new";

	public static final String FORM_TYPE_EXISTING = "existing";

	private static final long serialVersionUID = 1L;

	private String railTimetableId;

	private String depRailwayStationName;
	
	private String depRailwayStationId;

	private String depTrainNo;

	private String depSwitchingTimeMin;

	private String depTimeMin;

	private String arrRailwayStationName;
	
	private String arrRailwayStationId;

	private String arrOffsetDays;

	private String arrTrainNo;

	private String arrTimeMin;

	private String arrSwitchingTimeMin;

	private String timetableComment;

	/**
	 * @return the railTimetableId
	 */
	public String getRailTimetableId() {
		return railTimetableId;
	}

	/**
	 * @param railTimetableId the railTimetableId to set
	 */
	public void setRailTimetableId(String railTimetableId) {
		this.railTimetableId = railTimetableId;
	}

	/**
	 * @return the depTrainNo
	 */
	public String getDepTrainNo() {
		return depTrainNo;
	}

	/**
	 * @param depTrainNo the depTrainNo to set
	 */
	public void setDepTrainNo(String depTrainNo) {
		this.depTrainNo = depTrainNo;
	}

	/**
	 * @return the depSwitchingTimeMin
	 */
	public String getDepSwitchingTimeMin() {
		return depSwitchingTimeMin;
	}

	/**
	 * @param depSwitchingTimeMin the depSwitchingTimeMin to set
	 */
	public void setDepSwitchingTimeMin(String depSwitchingTimeMin) {
		this.depSwitchingTimeMin = depSwitchingTimeMin;
	}

	/**
	 * @return the depTimeMin
	 */
	public String getDepTimeMin() {
		return depTimeMin;
	}

	/**
	 * @param depTimeMin the depTimeMin to set
	 */
	public void setDepTimeMin(String depTimeMin) {
		this.depTimeMin = depTimeMin;
	}

	/**
	 * @return the arrOffsetDays
	 */
	public String getArrOffsetDays() {
		return arrOffsetDays;
	}

	/**
	 * @param arrOffsetDays the arrOffsetDays to set
	 */
	public void setArrOffsetDays(String arrOffsetDays) {
		this.arrOffsetDays = arrOffsetDays;
	}

	/**
	 * @return the arrTrainNo
	 */
	public String getArrTrainNo() {
		return arrTrainNo;
	}

	/**
	 * @param arrTrainNo the arrTrainNo to set
	 */
	public void setArrTrainNo(String arrTrainNo) {
		this.arrTrainNo = arrTrainNo;
	}

	/**
	 * @return the arrTimeMin
	 */
	public String getArrTimeMin() {
		return arrTimeMin;
	}

	/**
	 * @param arrTimeMin the arrTimeMin to set
	 */
	public void setArrTimeMin(String arrTimeMin) {
		this.arrTimeMin = arrTimeMin;
	}

	/**
	 * @return the arrSwitchingTimeMin
	 */
	public String getArrSwitchingTimeMin() {
		return arrSwitchingTimeMin;
	}

	/**
	 * @param arrSwitchingTimeMin the arrSwitchingTimeMin to set
	 */
	public void setArrSwitchingTimeMin(String arrSwitchingTimeMin) {
		this.arrSwitchingTimeMin = arrSwitchingTimeMin;
	}

	/**
	 * @return the timetableComment
	 */
	public String getTimetableComment() {
		return timetableComment;
	}

	/**
	 * @param timetableComment the timetableComment to set
	 */
	public void setTimetableComment(String timetableComment) {
		this.timetableComment = timetableComment;
	}

	/**
	 * @return the depRailwayStationName
	 */
	public String getDepRailwayStationName() {
		return depRailwayStationName;
	}

	/**
	 * @param depRailwayStationName the depRailwayStationName to set
	 */
	public void setDepRailwayStationName(String depRailwayStationName) {
		this.depRailwayStationName = depRailwayStationName;
	}

	/**
	 * @return the arrRailwayStationName
	 */
	public String getArrRailwayStationName() {
		return arrRailwayStationName;
	}

	/**
	 * @param arrRailwayStationName the arrRailwayStationName to set
	 */
	public void setArrRailwayStationName(String arrRailwayStationName) {
		this.arrRailwayStationName = arrRailwayStationName;
	}

	/**
	 * @return the depRailwayStationId
	 */
	public String getDepRailwayStationId() {
		return depRailwayStationId;
	}

	/**
	 * @param depRailwayStationId the depRailwayStationId to set
	 */
	public void setDepRailwayStationId(String depRailwayStationId) {
		this.depRailwayStationId = depRailwayStationId;
	}

	/**
	 * @return the arrRailwayStationId
	 */
	public String getArrRailwayStationId() {
		return arrRailwayStationId;
	}

	/**
	 * @param arrRailwayStationId the arrRailwayStationId to set
	 */
	public void setArrRailwayStationId(String arrRailwayStationId) {
		this.arrRailwayStationId = arrRailwayStationId;
	}
}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class RailDistanceDetailBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDistanceDetailBean.class);

	public static final String FORM_TYPE_NEW = "new";

	public static final String FORM_TYPE_EXISTING = "existing";
	
	public static final String FORM_TYPE_JOINED = "joined";

	private static final long serialVersionUID = 1L;

	private String railDistanceId;

	private String depStationName;
	
	private String depStationId;

	private String arrStationName;
	
	private String arrStationId;

	private String tariffDistanceM;

	public String getRailDistanceId() {
		return railDistanceId;
	}

	public void setRailDistanceId(String railDistanceId) {
		this.railDistanceId = railDistanceId;
	}

	public String getTariffDistanceM() {
		return tariffDistanceM;
	}

	public void setTariffDistanceM(String tariffDistance) {
		this.tariffDistanceM = tariffDistance;
	}

	public String getDepStationName() {
		return depStationName;
	}

	public void setDepStationName(String depStationName) {
		this.depStationName = depStationName;
	}

	public String getDepStationId() {
		return depStationId;
	}

	public void setDepStationId(String depStationId) {
		this.depStationId = depStationId;
	}

	public String getArrStationName() {
		return arrStationName;
	}

	public void setArrStationName(String arrStationName) {
		this.arrStationName = arrStationName;
	}

	public String getArrStationId() {
		return arrStationId;
	}

	public void setArrStationId(String arrStationId) {
		this.arrStationId = arrStationId;
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;


import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;

/**
 * @author AdNovum Informatik AG
 */
public class RailDispoExportParameters implements Serializable {

	private static final long serialVersionUID = 1L;

	private Locale locale;
	private String exportingUser;
	private String utcOffset;
	private String timeOfExport;
	private List<RailPlantSpEntity> shippingPoints;
	private List<DispoPlantTrackListEntity> selectedTracks;
	private RailPlantEntity plant;
	private List<RailDispoExportOrderRow> orderResult;
	private List<RailDispoExportWagonRow> wagonResult;
	private Map<Long, Boolean> statuses;

	private RailDispoExportParameters() {}

	public RailDispoExportParameters(
			Locale locale, 
			String exportingUser,
			String timeOfExport,
			String utcOffset,
			RailPlantEntity plant,
			List<RailPlantSpEntity> shippingPoints, 
			List<DispoPlantTrackListEntity> selectedTracks,
			List<RailDispoExportOrderRow> orderResult,
			List<RailDispoExportWagonRow> wagonResult,
			Map<Long, Boolean> statuses) {
		this.locale = locale;
		this.exportingUser = exportingUser;
		this.timeOfExport = timeOfExport;
		this.utcOffset = utcOffset;
		this.plant = plant;
		this.shippingPoints = shippingPoints;
		this.selectedTracks = selectedTracks;
		this.orderResult = orderResult;
		this.wagonResult = wagonResult;
		this.statuses = statuses;
	}

	public Locale getLocale() {
		return locale;
	}

	public RailPlantEntity getPlant() {
		return plant;
	}

	public String getExportingUser() {
		return exportingUser;
	}

	public List<RailPlantSpEntity> getShippingPoints() {
		return shippingPoints;
	}

	public List<DispoPlantTrackListEntity> getSelectedTracks() {
		return selectedTracks;
	}

	public String getUtcOffset() {
		return utcOffset;
	}

	public String getTimeOfExport() {
		return timeOfExport;
	}

	public List<RailDispoExportOrderRow> getOrderResult() {
		return orderResult;
	}

	public List<RailDispoExportWagonRow> getWagonResult() {
		return wagonResult;
	}

	public Map<Long, Boolean> getStatuses() {
		return statuses;
	}

}

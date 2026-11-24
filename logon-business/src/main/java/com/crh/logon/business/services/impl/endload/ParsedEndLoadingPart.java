/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl.endload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.crh.logon.beans.LoadedQuantityData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingPartData;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ParsedEndLoadingPart implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final EndLoadingPartData endLoadingPartData;
	private final Long assignmentPartId;
	private final LoadingpointEntity loadingPoint;
	private final Date loadingEndTime;
	private final BigDecimal grossWeightInTons;
	private final Map<Long, LoadedQuantityData> loadingData;
	private final Long mixDesignId;

	public ParsedEndLoadingPart(EndLoadingPartData endLoadingPartData, Long assignmentPartId,
			LoadingpointEntity loadingPoint, Date loadingEndTime, BigDecimal grossWeightInTons,
			Map<Long, LoadedQuantityData> loadingData, Long mixDesignId) {
		this.endLoadingPartData = endLoadingPartData;
		this.assignmentPartId = assignmentPartId;
		this.loadingPoint = loadingPoint;
		this.loadingEndTime = loadingEndTime;
		this.grossWeightInTons = grossWeightInTons;
		this.loadingData = loadingData;
		this.mixDesignId = mixDesignId;
	}

	public EndLoadingPartData getEndLoadingPartData() {
		return endLoadingPartData;
	}

	public Long getAssignmentPartId() {
		return assignmentPartId;
	}

	public LoadingpointEntity getLoadingPoint() {
		return loadingPoint;
	}

	public Date getLoadingEndTime() {
		return loadingEndTime;
	}

	public BigDecimal getGrossWeightInTons() {
		return grossWeightInTons;
	}

	public Map<Long, LoadedQuantityData> getLoadingData() {
		return loadingData;
	}

	public Long getMixDesignId() {
		return mixDesignId;
	}

}
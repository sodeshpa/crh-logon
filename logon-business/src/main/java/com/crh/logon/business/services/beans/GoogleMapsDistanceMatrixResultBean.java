/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * GoogleMapsServiceBean uses this class to return the duration and the distance.
 * 
 * @author AdNovum Informatik AG
 */
public final class GoogleMapsDistanceMatrixResultBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String status;
	
	/** Stores the distance value in meters as coming from google*/
	private final Long rawDistanceMeters;
	
	/** distance from google modified by the distance correction factor */
	private final Long correctedDistanceMeters;
	
	/** Stores the duration value in minutes. */
	private final Long durationMin;
	
	/** true if the duration is corrected. */
	private final Boolean corrected;
	
	/** Stores the duration value received from google (raw) in minutes.
	 * Must be null, if data is not directly from google (but from cache).*/
	private final Long rawGoogleDurationMin;
	
	public GoogleMapsDistanceMatrixResultBean(String status) {
		this(status, (Long) null, (Long) null);
	}

	public GoogleMapsDistanceMatrixResultBean(String status, String distanceMeters, String durationSec) {
		this.status = status;
		this.rawDistanceMeters = (distanceMeters != null) ? Long.valueOf(distanceMeters) : null;
		this.correctedDistanceMeters = (distanceMeters != null) ? Long.valueOf(distanceMeters) : null;
		
		if (durationSec != null) {
			long durationSecLong = Long.parseLong(durationSec);
			this.durationMin = Long.valueOf(Math.round(durationSecLong/60));
			this.rawGoogleDurationMin = Long.valueOf(Math.round(durationSecLong/60));
		}
		else {
			this.durationMin = null;
			this.rawGoogleDurationMin = null;
		}
		
		this.corrected = Boolean.FALSE;
	}
	
	public GoogleMapsDistanceMatrixResultBean(String status, Long distanceMeters, Long durationMin) {
		this.status = status;
		this.rawDistanceMeters = distanceMeters;
		this.correctedDistanceMeters = distanceMeters; // default correction factor is 1 so we can use it like this 
		this.durationMin = durationMin;
		this.corrected = Boolean.FALSE;
		this.rawGoogleDurationMin = null;
	}
	
	/**
	 * Use this constructor to create a corrected result based on the uncorrected one and a correction factor
	 * @param uncorrectedResult
	 * @param timeCorrectionFactor
	 */
	public GoogleMapsDistanceMatrixResultBean(GoogleMapsDistanceMatrixResultBean uncorrectedResult,
			double timeCorrectionFactor, double distanceCorrectionFactor) {
		if (uncorrectedResult.corrected.booleanValue()) {
			throw new LogonException("Attempt to correct a distance matrix result which is already" +
					" corrected!", Reason.RES_ERROR_IMPL);
		}
		
		this.status = uncorrectedResult.status;
		this.rawDistanceMeters = uncorrectedResult.rawDistanceMeters;
		this.rawGoogleDurationMin = uncorrectedResult.rawGoogleDurationMin;
		
		if (uncorrectedResult.durationMin != null) {
			this.durationMin = Math.round(uncorrectedResult.durationMin * timeCorrectionFactor);
		}
		else {
			this.durationMin = null;
		}
		
		if (uncorrectedResult.rawDistanceMeters != null) {
			this.correctedDistanceMeters = Math.round(uncorrectedResult.rawDistanceMeters * distanceCorrectionFactor);
		} else {
			this.correctedDistanceMeters = uncorrectedResult.rawDistanceMeters;
		}
		
		this.corrected = Boolean.TRUE;
	}
	
	/**
	 * Returns the distance value in meters.
	 */
	public Long getDistance() {
		return rawDistanceMeters;
	}
	
	/** Distance multiplied by the distance correction factor */
	public Long getCorrectedDistance() {
		return correctedDistanceMeters;
	}
	
	/**
	 * Returns the duration value in minutes.
	 */
	public Long getDurationMin() {
		return durationMin;
	}

	/**
	 * Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Returns the duration value received from google (raw) in minutes.
	 */
	public Long getRawGoogleDurationMin() {
		return rawGoogleDurationMin;
	}
}

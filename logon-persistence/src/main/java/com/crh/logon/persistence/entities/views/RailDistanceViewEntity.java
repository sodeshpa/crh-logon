/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.views;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_RAIL_DISTANCE")
public class RailDistanceViewEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_RAIL_DISTANCE";
    
    @Id
    @Column(name = "ROW_NUMBER", nullable = false)
    private Long rowNumber;
    
    @Column(name = "RAIL_DISTANCE_ID")
    private Long railDistanceId;
    
    @Column(name = "DEP_STATION_ID", nullable = false)
    private Long depStationId;

    @Column(name = "ARR_STATION_ID", nullable = false)
    private Long arrStationId;
    
    @Column(name = "DEP_STATION_NAME")
    private String depStationName;
    
    @Column(name = "ARR_STATION_NAME")
    private String arrStationName;
    
    @Column(name = "TARIFF_DISTANCE_M")
    private Long tariffDistanceM;

    // Fieldnames
    public static final String ATTR_DEP_STATION_NAME = "depStationName";
    public static final String ATTR_ARR_STATION_NAME = "arrStationName";
    public static final String ATTR_DEP_STATION_ID = "depStationId";
    public static final String ATTR_ARR_STATION_ID = "arrStationId";
    public static final String ATTR_TARIFF_DISTANCE_M = "tariffDistanceM";
    
    // Field length on DB
    public static final long DBSIZE_DEP_STATION_NAME = 50;
    public static final long DBSIZE_ARR_STATION_NAME = 50;
    public static final long DBSIZE_TARIFF_DISTANCE_M = 10;
	
	/**
     * DEP_STATION_ID is NOT NULLABLE
     * Field length on DB: 12
     */
	public Long getDepStationId() {
		return depStationId;
	}

	/**
     * DEP_STATION_ID is NOT NULLABLE
     * Field length on DB: 12
     */
	public void setDepStationId(Long depStationId) {
		this.depStationId = depStationId;
	}

	/**
     * ARR_STATION_ID is NOT NULLABLE
     * Field length on DB: 12
     */
	public Long getArrStationId() {
		return arrStationId;
	}

	/**
     * ARR_STATION_ID is NOT NULLABLE
     * Field length on DB: 12
     */
	public void setArrStationId(Long arrStationId) {
		this.arrStationId = arrStationId;
	}

	/**
     * DEP_STATION_NAME is NOT NULL
     * Field length on DB: 50
     */
	public String getDepStationName() {
		return depStationName;
	}

	/**
     * DEP_STATION_NAME is NOT NULL
     * Field length on DB: 50
     */
	public void setDepStationName(String depStationName) {
		this.depStationName = depStationName;
	}

	/**
     * ARR_STATION_NAME is NOT NULL
     * Field length on DB: 50
     */
	public String getArrStationName() {
		return arrStationName;
	}

	/**
     * ARR_STATION_NAME is NOT NULL
     * Field length on DB: 50
     */
	public void setArrStationName(String arrStationName) {
		this.arrStationName = arrStationName;
	}

	/**
     * TARIFF_DISTANCE_M is NOT NULL
     * Field length on DB: 10
     */
	public Long getTariffDistanceM() {
		return tariffDistanceM;
	}

	/**
     * TARIFF_DISTANCE_M is NOT NULL
     * Field length on DB: 10
     */
	public void setTariffDistanceM(Long tariffDistanceM) {
		this.tariffDistanceM = tariffDistanceM;
	}

	/**
     * ROW_NUMBER is NOT NULLABLE
     * Field length on DB: 12
     */
	public Long getRowNumber() {
		return rowNumber;
	}

	/**
     * ROW_NUMBER is NOT NULLABLE
     * Field length on DB: 12
     */
	public void setRowNumber(Long rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
     * RAIL_DISTANCE_ID is NOT NULLABLE
     * Field length on DB: 12
     */
	public Long getRailDistanceId() {
		return railDistanceId;
	}

	/**
     * RAIL_DISTANCE_ID is NOT NULLABLE
     * Field length on DB: 12
     */
	public void setRailDistanceId(Long railDistanceId) {
		this.railDistanceId = railDistanceId;
	}
}
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.views;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import ch.nevis.gdi.jpa.BaseEntity;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_PLANNING_TRUCK")
public class PlanningTruckEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";

	private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "VLGNC_PLANNING_TRUCK";

	@Id
	@Column(name = "COMPOSITE_ID")
	private String compositeId;

	@Column(name = "TRUCK_ID")
	private Long truckId;

	@Column(name = "CARRIER_ID")
	private Long carrierId;

	@Column(name = "POOL_ID")
	private Long poolId;

	@Column(name = "TRUCK_TYPE_CD")
	private Long truckTypeCd;

	// Fieldnames
	public static final String ATTR_TRUCK_ID = "truckId";
	public static final String ATTR_CARRIER_ID = "carrierId";
	public static final String ATTR_POOL_ID = "poolId";
	public static final String ATTR_TRUCK_TYPE_CD = "truckTypeCd";

	// Field length on DB
	public static final long DBSIZE_TRUCK_ID = 12;
	public static final long DBSIZE_POOL_ID = 12;
	public static final long DBSIZE_CARRIER_ID = 12;
	public static final long DBSIZE_TRUCK_TYPE_CD = 3;
	public static final long DBSIZE_COMPOSITE_ID = 81;

	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append(super.toString());
		if (LOG_ENTITIES.isDebugEnabled()) {
			tsb.append("truckId", "truckId");
			tsb.append("carrierId", "carrierId");
			tsb.append("poolId", "poolId");
			tsb.append("truckTypeCd", "truckTypeCd");
		}
		else if (LOG_ENTITIES.isInfoEnabled()) {
			tsb.append("truckId", truckId); // ID
		}
		return tsb.toString();
	}

	/**
	 * TRUCK_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getTruckId() {
		return truckId;
	}

	/**
	 * TRUCK_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setTruckId(Long truckId) {
		this.truckId = truckId;
	}

	/**
	 * TRUCK_TYPE_CD is NOT nullable
	 * Field length on DB: 3
	 */
	public Long getTruckTypeCd() {
		return truckTypeCd;
	}

	/**
	 * TRUCK_TYPE_CD is NOT nullable
	 * Field length on DB: 3
	 */
	public void setTruckTypeCd(Long truckTypeCd) {
		this.truckTypeCd = truckTypeCd;
	}

	/**
	 * POOL_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getPoolId() {
		return poolId;
	}

	/**
	 * POOL_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}

	/**
	 * CARRIER_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getCarrierId() {
		return carrierId;
	}

	/**
	 * CARRIER_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setCarrierId(Long carrierId) {
		this.carrierId = carrierId;
	}


	/**
	 * COMPOSITE_ID is nullable
	 * Field length on DB: 81
	 */
	public String getCompositeId() {
		return compositeId;
	}

	/**
	 * COMPOSITE_ID is nullable
	 * Field length on DB: 81
	 */
	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}
}

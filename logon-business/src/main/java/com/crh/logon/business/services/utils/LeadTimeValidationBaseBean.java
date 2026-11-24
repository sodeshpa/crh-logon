package com.crh.logon.business.services.utils;

import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.views.SareaDisplayEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

public class LeadTimeValidationBaseBean {

	private Long allowWebOrderOnWeekend;
	private Boolean dbltActiveFlag;
	private Boolean sdltAndNdltActive;
	private Long dbltDistanceL1;
	private Long dbltDistanceL2;
	private Long h1EarlyOrderLimit;
	private Long h2EarliestDeliveryFrom;
	private Long dbltOffsetD1;
	private Long dbltOffsetD2;
	private Long dbltOffsetD3;
	private Long dbltOffsetD4;
	private Long dbltOffsetD5;
	private Long dbltOffsetD6;

	/**
	 * only for tracking
	 */
	private Long shiptoSareaId;
	private Long sareaId;

	protected LeadTimeValidationBaseBean () {
	}

	public LeadTimeValidationBaseBean (SareaDisplayEntity entity) {
		this.sareaId = entity.getSareaId();
		this.allowWebOrderOnWeekend = entity.getAllowWebOrderOnWeekend();
		this.dbltActiveFlag = null; 	// null means no dbltActiveFlag field exist at all in sarea
		this.sdltAndNdltActive = null;	// null means no sdltAndNdltActive field exist at all in sarea
		this.dbltDistanceL1 = entity.getDbltDistanceL1();
		this.dbltDistanceL2 = entity.getDbltDistanceL2();
		this.h1EarlyOrderLimit = entity.getH1EarlyOrderLimit();
		this.h2EarliestDeliveryFrom = entity.getH2EarliestDeliveryFrom();
		this.dbltOffsetD1 = entity.getDbltOffsetD1();
		this.dbltOffsetD2 = entity.getDbltOffsetD2();
		this.dbltOffsetD3 = entity.getDbltOffsetD3();
		this.dbltOffsetD4 = entity.getDbltOffsetD4();
		this.dbltOffsetD5 = entity.getDbltOffsetD5();
		this.dbltOffsetD6 = entity.getDbltOffsetD6();
	}

	/**
	 * Use values from shiptoSareaEntity. If shiptoSareaEntity value is null than use sareaEntity value.
	 *
	 * @param shiptoSareaEntity
	 * @param sareaEntity
	 */
	public LeadTimeValidationBaseBean(ShiptoSareaEntity shiptoSareaEntity,
			SareaEntity sareaEntity) {
		// this field exist only in sarea
		this.allowWebOrderOnWeekend = sareaEntity != null? sareaEntity.getAllowWebOrderOnWeekend() : null;

		// these 2 fields exist only in shipto-sarea
		this.dbltActiveFlag = shiptoSareaEntity.getDbltActiveFlag();
		this.sdltAndNdltActive = shiptoSareaEntity.getSdltAndNdltActive();

		// the rest of the entries are exist in both entity
		if (sareaEntity == null) {
			this.dbltDistanceL1 = shiptoSareaEntity.getDbltDistanceL1();
			this.dbltDistanceL2 = shiptoSareaEntity.getDbltDistanceL2();
			this.h1EarlyOrderLimit = shiptoSareaEntity.getH1EarlyOrderLimit();
			this.h2EarliestDeliveryFrom = shiptoSareaEntity.getH2EarliestDeliveryFrom();
			this.dbltOffsetD1 = shiptoSareaEntity.getDbltOffsetD1();
			this.dbltOffsetD2 = shiptoSareaEntity.getDbltOffsetD2();
			this.dbltOffsetD3 = shiptoSareaEntity.getDbltOffsetD3();
			this.dbltOffsetD4 = shiptoSareaEntity.getDbltOffsetD4();
			this.dbltOffsetD5 = shiptoSareaEntity.getDbltOffsetD5();
			this.dbltOffsetD6 = shiptoSareaEntity.getDbltOffsetD6();
			this.shiptoSareaId = shiptoSareaEntity.getShiptoSareaId();
		}
		else {
			this.sareaId = sareaEntity.getSareaId();
			this.shiptoSareaId = shiptoSareaEntity.getShiptoSareaId();
			this.dbltDistanceL1 = shiptoSareaEntity.getDbltDistanceL1() != null ? shiptoSareaEntity.getDbltDistanceL1()
					: sareaEntity.getDbltDistanceL1();
			this.dbltDistanceL2 = shiptoSareaEntity.getDbltDistanceL2() != null ? shiptoSareaEntity.getDbltDistanceL2()
					: sareaEntity.getDbltDistanceL2();
			this.h1EarlyOrderLimit = shiptoSareaEntity.getH1EarlyOrderLimit() != null ? shiptoSareaEntity.getH1EarlyOrderLimit()
					: sareaEntity.getH1EarlyOrderLimit();
			this.h2EarliestDeliveryFrom = shiptoSareaEntity.getH2EarliestDeliveryFrom() != null ? shiptoSareaEntity.getH2EarliestDeliveryFrom()
					: sareaEntity.getH2EarliestDeliveryFrom();
			this.dbltOffsetD1 = shiptoSareaEntity.getDbltOffsetD1() != null ? shiptoSareaEntity.getDbltOffsetD1()
					: sareaEntity.getDbltOffsetD1();
			this.dbltOffsetD2 = shiptoSareaEntity.getDbltOffsetD2() != null ? shiptoSareaEntity.getDbltOffsetD2()
					: sareaEntity.getDbltOffsetD2();
			this.dbltOffsetD3 = shiptoSareaEntity.getDbltOffsetD3() != null ? shiptoSareaEntity.getDbltOffsetD3()
					: sareaEntity.getDbltOffsetD3();
			this.dbltOffsetD4 = shiptoSareaEntity.getDbltOffsetD4() != null ? shiptoSareaEntity.getDbltOffsetD4()
					: sareaEntity.getDbltOffsetD4();
			this.dbltOffsetD5 = shiptoSareaEntity.getDbltOffsetD5() != null ? shiptoSareaEntity.getDbltOffsetD5()
					: sareaEntity.getDbltOffsetD5();
			this.dbltOffsetD6 = shiptoSareaEntity.getDbltOffsetD6() != null ? shiptoSareaEntity.getDbltOffsetD6()
					: sareaEntity.getDbltOffsetD6();
		}
	}

	boolean isBaseDataEmpty(){
		return (dbltDistanceL1 == null && dbltDistanceL2 == null &&
				dbltOffsetD1 == null && dbltOffsetD2 == null
				&& dbltOffsetD3 == null && dbltOffsetD4 == null
				&& dbltOffsetD5 == null && dbltOffsetD6 == null
				&& h1EarlyOrderLimit == null && h2EarliestDeliveryFrom == null);
	}

	public Boolean getDbltActiveFlag() {
		return dbltActiveFlag;
	}

	public void setDbltActiveFlag(Boolean dbltActiveFlag) {
		this.dbltActiveFlag = dbltActiveFlag;
	}

	public Boolean getSdltAndNdltActive() {
		return sdltAndNdltActive;
	}

	public void setSdltAndNdltActive(Boolean sdltAndNdltActive) {
		this.sdltAndNdltActive = sdltAndNdltActive;
	}

	public Long getDbltDistanceL1() {
		return dbltDistanceL1;
	}

	public void setDbltDistanceL1(Long dbltDistanceL1) {
		this.dbltDistanceL1 = dbltDistanceL1;
	}

	public Long getDbltDistanceL2() {
		return dbltDistanceL2;
	}

	public void setDbltDistanceL2(Long dbltDistanceL2) {
		this.dbltDistanceL2 = dbltDistanceL2;
	}

	public Long getH1EarlyOrderLimit() {
		return h1EarlyOrderLimit;
	}

	public void setH1EarlyOrderLimit(Long h1EarlyOrderLimit) {
		this.h1EarlyOrderLimit = h1EarlyOrderLimit;
	}

	public Long getH2EarliestDeliveryFrom() {
		return h2EarliestDeliveryFrom;
	}

	public void setH2EarliestDeliveryFrom(Long h2EarliestDeliveryFrom) {
		this.h2EarliestDeliveryFrom = h2EarliestDeliveryFrom;
	}

	public Long getDbltOffsetD1() {
		return dbltOffsetD1;
	}

	public void setDbltOffsetD1(Long dbltOffsetD1) {
		this.dbltOffsetD1 = dbltOffsetD1;
	}

	public Long getDbltOffsetD2() {
		return dbltOffsetD2;
	}

	public void setDbltOffsetD2(Long dbltOffsetD2) {
		this.dbltOffsetD2 = dbltOffsetD2;
	}

	public Long getDbltOffsetD3() {
		return dbltOffsetD3;
	}

	public void setDbltOffsetD3(Long dbltOffsetD3) {
		this.dbltOffsetD3 = dbltOffsetD3;
	}

	public Long getDbltOffsetD4() {
		return dbltOffsetD4;
	}

	public void setDbltOffsetD4(Long dbltOffsetD4) {
		this.dbltOffsetD4 = dbltOffsetD4;
	}

	public Long getDbltOffsetD5() {
		return dbltOffsetD5;
	}

	public void setDbltOffsetD5(Long dbltOffsetD5) {
		this.dbltOffsetD5 = dbltOffsetD5;
	}

	public Long getDbltOffsetD6() {
		return dbltOffsetD6;
	}

	public void setDbltOffsetD6(Long dbltOffsetD6) {
		this.dbltOffsetD6 = dbltOffsetD6;
	}

	public Long getAllowWebOrderOnWeekend() {
		return allowWebOrderOnWeekend;
	}

	public void setAllowWebOrderOnWeekend(Long allowWebOrderOnWeekend) {
		this.allowWebOrderOnWeekend = allowWebOrderOnWeekend;
	}

	public Long getShiptoSareaId() {
		return shiptoSareaId;
	}

	public Long getSareaId() {
		return sareaId;
	}

	public String getIdsForTrack(){
		return "sareaId="+(sareaId!=null?sareaId:"")+",shiptoSareaId="+(shiptoSareaId!=null?shiptoSareaId:"");
	}

	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		return tsb.toString();
	}

}
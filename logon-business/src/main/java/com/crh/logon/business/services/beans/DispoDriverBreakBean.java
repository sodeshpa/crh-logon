/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.crh.logon.persistence.entities.views.DispoDriverBreakListEntity;

/**
 * It's a DTO like bean for DispoDriverBreakListEntity
 * 
 * @author AdNovum Informatik AG
 *
 */
public class DispoDriverBreakBean implements Serializable {

	private static final long serialVersionUID = 3300281423027795986L;

	private Long driverBreakId;

	private Long driverId;

	private Date startDate;

	private Date endDate;

	private String description;

	private Boolean isLunchBreak;

	private Boolean isMopBreak;

	/**
	 * Initialize the new object with DispoDriverBreakListEntity's data
	 * @param dispoDriverBreakListEntity
	 */
	public DispoDriverBreakBean(DispoDriverBreakListEntity dispoDriverBreakListEntity) {
		this.driverBreakId = dispoDriverBreakListEntity.getDriverBreakId();
		this.driverId = dispoDriverBreakListEntity.getDriverId();
		this.startDate = dispoDriverBreakListEntity.getStartDat();
		this.endDate = dispoDriverBreakListEntity.getEndDat();
		this.description = dispoDriverBreakListEntity.getDescription();
		this.isLunchBreak = dispoDriverBreakListEntity.getIsLunchBreak();
		this.isMopBreak = dispoDriverBreakListEntity.getIsMopBreak();		
	}

	/**
	 * Initizalize the new driverBreak with given start and finish date (time)
	 * @param startDate
	 * @param endDate
	 */
	private DispoDriverBreakBean(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Create a lunch break between the given dates (and times)
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static DispoDriverBreakBean createLunchBreak(Date startDate, Date endDate) {
		DispoDriverBreakBean dispoDriverBreakBean = new DispoDriverBreakBean(startDate, endDate);		
		dispoDriverBreakBean.setIsLunchBreak(Boolean.TRUE);
		dispoDriverBreakBean.setIsMopBreak(Boolean.FALSE);
		
		return dispoDriverBreakBean;
	}

	public void setDriverBreakId(Long driverBreakId) {
		this.driverBreakId = driverBreakId;
	}

	public Long getDriverBreakId() {
		return driverBreakId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setStartDat(Date startDat) {
		this.startDate = startDat;
	}

	public Date getStartDat() {
		return startDate;
	}

	public void setEndDat(Date endDat) {
		this.endDate = endDat;
	}

	public Date getEndDat() {
		return endDate;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setIsLunchBreak(Boolean isLunchBreak) {
		this.isLunchBreak = isLunchBreak;
	}

	public Boolean getIsLunchBreak() {
		return isLunchBreak;
	}

	public void setIsMopBreak(Boolean isMopBreak) {
		this.isMopBreak = isMopBreak;
	}

	public Boolean getIsMopBreak() {
		return isMopBreak;
	}
	
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append(super.toString());
		tsb.append("driverBreakId", "driverBreakId");
		tsb.append("driverId", "driverId");
		tsb.append("startDat", "startDat");
		tsb.append("endDat", "endDat");
		tsb.append("description", "description");
		tsb.append("isLunchBreak", "isLunchBreak");
		tsb.append("isMopBreak", "isMopBreak");

		return tsb.toString();
	}	
}

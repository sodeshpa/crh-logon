/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.common.WagonMaintEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * @author AdNovum Informatik AG
 */
public class MaintenanceBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MaintenanceBean.class);
	
	
	private static List repairShiptoList;

	private String maintTypeCd;
	private MaintTypeTxEntity maintTypeTx;
	
	private WagonMaintEntity wagonMaintEntity;

	private TimeZone timezone;
	
	public MaintenanceBean() {
		this.wagonMaintEntity = new WagonMaintEntity();
		this.wagonMaintEntity.setActiveFlag(Boolean.TRUE);
	}
	
	public MaintenanceBean(WagonMaintEntity wagonMaintEntity,
						   String maintTypeCd,
						   MaintTypeTxEntity maintTypeTx,
						   TimeZone timezone) {

		this.wagonMaintEntity = wagonMaintEntity;
		this.maintTypeCd = maintTypeCd;
		this.maintTypeTx = maintTypeTx;
		this.timezone = timezone;
	}
	
	public MaintTypeTxEntity getMaintTypeTx() {
		return maintTypeTx;
	}
	public void setMaintTypeTx(MaintTypeTxEntity maintTypeTx) {
		this.maintTypeTx = maintTypeTx;
	}

	public WagonMaintEntity getWagonMaintEntity() {
		return wagonMaintEntity;
	}
	public void setWagonMaintEntity(WagonMaintEntity wagonMaintEntity) {
		this.wagonMaintEntity = wagonMaintEntity;
	}
	public String getMaintTypeCd() {
		return maintTypeCd;
	}
	public void setMaintTypeCd(String maintTypeCd) {
		this.maintTypeCd = maintTypeCd;
	}
	
	private SapBpEntity pickRepairPlant(String selectedRepairPlantId) {
		if ( "-1".equals( selectedRepairPlantId) ) {
			return null;
		}
		ListIterator plantIt = repairShiptoList.listIterator();
		while ( plantIt.hasNext() ) {
			SapBpEntity sapBpEntity = (SapBpEntity) plantIt.next();
			if ( sapBpEntity.getSapBpId().toString().equals( selectedRepairPlantId )) {
				return sapBpEntity;
			}
		}
		return null;
	}

	public String getSapBpId() {
		if ( wagonMaintEntity.getShiptoSapBp() == null) {
			return null;
		}
		return wagonMaintEntity.getShiptoSapBp().getSapBpId().toString();
	}
	
	public void setSapBpId(String sapBpId) {
		wagonMaintEntity.setShiptoSapBp( pickRepairPlant(sapBpId) );
	}

	public static List getRepairShiptoList() {
		return repairShiptoList;
	}
	
	public static void setRepairShiptoList(List repairShiptoList) {
		MaintenanceBean.repairShiptoList = repairShiptoList;
	}

	public String getNextDate() {
		return formatDate(wagonMaintEntity.getNextDat());
	}

	public void setNextDate(String nextDate) {
		Date d = parseDate(nextDate);
		wagonMaintEntity.setNextDat(d);
	}

	public String getLastDate() {
		return formatDate(wagonMaintEntity.getLastDat());
	}

	public void setLastDate(String lastDate) {
		Date d = parseDate(lastDate);
		wagonMaintEntity.setLastDat(d);
	}

	private Date parseDate(String text) {
		try {
			return timezone.parseDayMonthYear(text);
		}
		catch (ParseException exc) {
			LOG.info("Parsing date string [" + text + "] failed", exc);
			return null;
		}
	}

	private String formatDate(Date timestamp) {
		return timezone.fmtAsDayMonthYear(timestamp);
	}
}

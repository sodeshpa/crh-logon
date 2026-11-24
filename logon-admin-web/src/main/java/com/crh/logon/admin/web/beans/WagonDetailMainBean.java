/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.refdata.PlantTrackBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.PlantRailwayStationEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.ResourceKey;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.web.validators.TypeValidation;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class WagonDetailMainBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonDetailMainBean.class);

	private static final long serialVersionUID = 1L;
	private String wagonNo;
	private String wagonTypeName;
	private String wagonKeeperName;
	private SapBpEntity returnShiptoSapBp;
	private String returnShipToName;
	private RailwayStationEntity stationTo;
	private RailwayStationEntity stationFrom;
	private String arrivalDate;
	private String departureDate;
	private String plannedArrivalDate;
	private String plannedDepartureDate;
	private List loadList;

	private List railwayStationList;
	private List plantTracks;
	
	//editable fields
	private RailwayStationEntity lastKnownRailwayStation;
	private Long lastKnownRailwayStationId;
	private String currentTrackPositionNo;
	private String currentPlantTrackCd;
    private String arrivalLastKnownStationDate;
    private String arrivalLastKnownStationTime;

	private TimeZone timezone;

	public void setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
	}

	public TimeZone getTimeZone() {
		return timezone;
	}

	public String getWagonNo() {
		return wagonNo;
	}
	public void setWagonNo(String wagonNo) {
		this.wagonNo = wagonNo;
	}
	public String getWagonKeeperName() {
		return wagonKeeperName;
	}
	public void setWagonKeeperName(String wagonKeeperName) {
		this.wagonKeeperName = wagonKeeperName;
	}
	public String getReturnShipToName() {
		return returnShipToName;
	}
	public void setReturnShipToName(String returnShipToName) {
		this.returnShipToName = returnShipToName;
	}

	@ResourceKey("wagon.current_track_position_no")
	@TypeValidation(java.lang.Long.class)
	public String getCurrentTrackPositionNo() {
		return currentTrackPositionNo;
	}

	public void setCurrentTrackPositionNo(String currentTrackPositionNo) {
		this.currentTrackPositionNo = currentTrackPositionNo;
	}
	
	@ResourceKey("wagon.current_track_cd")
	@TypeValidation(java.lang.Long.class)
	public String getCurrentPlantTrackCd() {
		return currentPlantTrackCd;
	}
	
	public PlantTrackBsEntity getCurrentPlantTrackBs() {
		if (!StringUtils.empty(currentPlantTrackCd)) {			
			return ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService().findBsEntity(PlantTrackBsEntity.class, Long.parseLong(currentPlantTrackCd));
		}
		return null;
	}

	public void setCurrentPlantTrackCd(String currentPlantTrackCd) {
		this.currentPlantTrackCd = currentPlantTrackCd;
	}

	public RailwayStationEntity getLastKnownRailwayStation() {
		if (lastKnownRailwayStationId == null) {			
			return null;
		}
		if ( (this.lastKnownRailwayStation == null) || (!lastKnownRailwayStationId.equals(this.lastKnownRailwayStation.getRailwayStationId())) ) {
			// find the lastKnownRailwayStation entity in the list
			this.lastKnownRailwayStation = null;
			if ((lastKnownRailwayStationId != null) && (railwayStationList != null)) {
				for (Iterator it=railwayStationList.iterator(); it.hasNext();) {
					RailwayStationEntity railwayStation = (RailwayStationEntity)it.next();
					if (railwayStation.getRailwayStationId().equals(lastKnownRailwayStationId)) {
						this.lastKnownRailwayStation = railwayStation;
						break;
					}
				}
			}
		}
		return this.lastKnownRailwayStation;
	}

	public void setLastKnownRailwayStation(RailwayStationEntity lastKnownRailwayStation) {
		this.lastKnownRailwayStation = lastKnownRailwayStation;
	}

	public SapBpEntity getReturnShiptoSapBp() {
		return returnShiptoSapBp;
	}

	public void setReturnShiptoSapBp(SapBpEntity returnShiptoSapBp) {
		this.returnShiptoSapBp = returnShiptoSapBp;
	}

	public String getWagonTypeName() {
		return wagonTypeName;
	}

	public void setWagonTypeName(String wagonTypeName) {
		this.wagonTypeName = wagonTypeName;
	}

	public RailwayStationEntity getStationTo() {
		return stationTo;
	}

	public void setStationTo(RailwayStationEntity stationTo) {
		this.stationTo = stationTo;
	}

	public RailwayStationEntity getStationFrom() {
		return stationFrom;
	}

	public void setStationFrom(RailwayStationEntity stationFrom) {
		this.stationFrom = stationFrom;
	}

	public String getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}

	public String getPlannedDepartureDate() {
		return plannedDepartureDate;
	}

	public void setPlannedDepartureDate(String plannedDepartureDate) {
		this.plannedDepartureDate = plannedDepartureDate;
	}

	public String getPlannedArrivalDate() {
		return plannedArrivalDate;
	}

	public void setPlannedArrivalDate(String plannedArrivalDate) {
		this.plannedArrivalDate = plannedArrivalDate;
	}

	public Date getArrivalLastKnownStationDat() {
		try {
			if (arrivalLastKnownStationDate != null) {
				if (arrivalLastKnownStationTime != null) {
					return timezone.parseDayMonthYear4HourMinute(
							arrivalLastKnownStationDate, arrivalLastKnownStationTime);
				}
				return timezone.parseDayMonthYear4(arrivalLastKnownStationDate);
			}
		}
		catch (ParseException e) {
			LOG.error("Parsing 'arrivalLastKnownStation' failed: "
							  + "[arrivalLastKnownStationDate: " + arrivalLastKnownStationDate
							  + ", arrivalLastKnownStationTime: " + arrivalLastKnownStationTime + "]",
					  e);
			// continue
		}

		return null;
	}

	/*
	 * Setter called by BeanUtils.copyProperties
	 */
	public void setArrivalLastKnownStationDat(Date timestamp) {
		if (timestamp != null) {
			setArrivalLastKnownStationDate(timezone.fmtAsDayMonthYear4(timestamp));
			setArrivalLastKnownStationTime(timezone.fmtAsHourMinute(timestamp));
		}
		else {
			setArrivalLastKnownStationDate(null);
			setArrivalLastKnownStationTime(null);
		}
	}

	public List getRailwayStationList() {
		return railwayStationList;
	}

	public void setRailwayStationList(List railwayStationList) {
		this.railwayStationList = railwayStationList;
	}

	public Long getLastKnownRailwayStationId() {
		return lastKnownRailwayStationId;
	}

	public void setLastKnownRailwayStationId(Long lastKnownRailwayStationId) {
		this.lastKnownRailwayStationId = lastKnownRailwayStationId;
	}

	public String getArrivalLastKnownStationDate() {
		return arrivalLastKnownStationDate;
	}

	public void setArrivalLastKnownStationDate(String arrivalLastKnownStationDate) {
		this.arrivalLastKnownStationDate = arrivalLastKnownStationDate;
	}

	public String getArrivalLastKnownStationTime() {
		return arrivalLastKnownStationTime;
	}

	public void setArrivalLastKnownStationTime(String arrivalLastKnownStationTime) {
		this.arrivalLastKnownStationTime = arrivalLastKnownStationTime;
	}
	public List getLoadList() {
		return loadList;
	}
	public void setLoadList(List loadList) {
		this.loadList = loadList;
	}
	public List getPlantTracks() {
		return plantTracks;
	}
	public void setPlantTracks(List plantTracks) {
		this.plantTracks = plantTracks;
	}

	/**
	 * Deletes the properties, which must be empty for a new wagon.
	 */
	public void resetNewWagonProperties(){
		setStationTo(null);
		setStationFrom(null);
		setArrivalDate(null);
		setDepartureDate(null);
		setPlannedArrivalDate(null);
		setPlannedDepartureDate(null);

//		setItemList(null);
		setLoadList(null);
		setCurrentTrackPositionNo(null);
		setCurrentPlantTrackCd(null);
	    setArrivalLastKnownStationDate(null);
	    setArrivalLastKnownStationTime(null);
		setLastKnownRailwayStation(null);
		setLastKnownRailwayStationId(null);
	}

	public PlantRailwayStationEntity getCurrentPlantRailwayStation() {
		if (StringUtils.empty(currentPlantTrackCd) || plantTracks == null) {
			return null;
		}
		long currPlantTrackCd = Long.parseLong(getCurrentPlantTrackCd());
		for (Iterator iterator = plantTracks.iterator(); iterator.hasNext();) {
			PlantRailwayStationEntity plantRailwayStationEntity = (PlantRailwayStationEntity) iterator.next();
			if (currPlantTrackCd == plantRailwayStationEntity.getPlantTrackCd()) {
				return plantRailwayStationEntity;
			}
		}
		return null;
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.crh.logon.business.services.beans.DispoDriverBreakBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.TrafficJamEntity;
import com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity;
import com.crh.logon.persistence.entities.views.DispoDriverBreakListEntity;
import com.crh.logon.persistence.entities.views.DispoHomePlantEntity;
import com.crh.logon.persistence.entities.views.DispoMessageListEntity;
import com.crh.logon.persistence.entities.views.DispoTruckListV2Entity;
import com.crh.logon.persistence.entities.views.DispoTruckLoadListV2Entity;

/**
 * @author AdNovum Informatik AG
 */
public class TruckWrapper implements Serializable {

	// Implementing equals() and hashcode() so that we can use TruckWrappers in a HashSet.

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		/* Assumption: two TruckWrappers for the same truck are equal,
		 * or put differently, the column TRUCK_ID in the view VLGNC_DISPO_TRUCK_LIST
		 * is unique.
		 */
		return truck.getTruckId().intValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		/* Assumption: two TruckWrappers for the same truck are equal,
		 * or put differently, the column TRUCK_ID in the view VLGNC_DISPO_TRUCK_LIST
		 * is unique.
		 */
		if (obj instanceof TruckWrapper) {
			final TruckWrapper other = (TruckWrapper) obj;
			return truck.getTruckId().equals(other.getTruck().getTruckId());
		}
		else {
			// Not a truck
			return false;
		}
	}

	private DispoTruckListV2Entity truck;
	private List<TruckUnavailabilityEntity> truckUnavailabilities;
	private List<TrafficJamEntity> trafficJams;
	private List<DispoDriverBreakBean> driverBreaks;
	private List<DispoDriverBreakListEntity> mopBreaks;
	private List<DispoTruckLoadListV2Entity> loads;
	private DispoDriverBreakBean lunchBreak;
	/** 
	 * This entity holds the information about the Home Plant for the truck.
	 * This is affected by authorization, but the Truck Home Plant information in 
	 * DispoTruckListEntity itself is not.
	 * 
	 * For more details please see: LOGON-4999 Implement Truck Home Plant related changes
	 */
	private DispoHomePlantEntity homePlant;
	private List<DispoMessageListEntity> driverMessages;
	private boolean hasNewMessage;
	private boolean hasNewError;
	private boolean isCompositionValid;
	private boolean isNotAuthorized;
	private boolean isChanged;
	

 
	public TruckWrapper(DispoTruckListV2Entity truck, boolean isNotAuthorized) {
		this.truck = truck;
		this.isNotAuthorized = isNotAuthorized;
		this.truckUnavailabilities = new LinkedList<TruckUnavailabilityEntity>();
		this.trafficJams = new LinkedList<TrafficJamEntity>();
		this.driverBreaks = new LinkedList<DispoDriverBreakBean>();
		this.lunchBreak = null;
		this.homePlant = null;
		this.mopBreaks = new LinkedList<DispoDriverBreakListEntity>();
		this.driverMessages = new LinkedList<DispoMessageListEntity>();
		this.hasNewMessage = false;
		this.hasNewError = false;
		this.loads = new LinkedList<DispoTruckLoadListV2Entity>();
		this.isCompositionValid = false;
	}
	
	public DispoTruckListV2Entity getTruck() {
		return truck;
	}

	public List<DispoDriverBreakBean> getDriverBreaks() {
		return driverBreaks;
	}

	public List<TruckUnavailabilityEntity> getTruckUnavailabilities() {
		return truckUnavailabilities;
	}
	
	public void addTruckUnavailability(TruckUnavailabilityEntity tue){
		truckUnavailabilities.add(tue);
		this.isChanged = true;
	}

	public void addDriverBreak(DispoDriverBreakListEntity dbe) {
		if (dbe.getIsMopBreak().booleanValue()){
			mopBreaks.add(dbe);
		}
		else {
			DispoDriverBreakBean dispoDriverBreak = new DispoDriverBreakBean(dbe);
			driverBreaks.add(dispoDriverBreak);
			if (dbe.getIsLunchBreak().booleanValue()){
				this.lunchBreak = dispoDriverBreak;
			}
		}
		this.isChanged = true;				
	}

	/**
	 * checks if a lunch break exists, and adds one if it doesn't.
	 * 
	 * @param startTimeInMinutes
	 * @param endTimeInMinutes
	 * @param date
	 */
	public void checkLunchBreak(int startTimeInMinutes, int endTimeInMinutes, Date date, TimeZone timeZone) {
		if (this.lunchBreak != null){
			return;
		}
		Calendar c = timeZone.asCalendar();
		c.setTime(date);
		c.add(Calendar.MINUTE, startTimeInMinutes);
		Date officialLunchStart = c.getTime();
		c.setTime(date);
		c.add(Calendar.MINUTE, endTimeInMinutes);
		Date officialLunchFinish = c.getTime();					
		DispoDriverBreakBean officialLunchBreak = DispoDriverBreakBean.createLunchBreak(officialLunchStart, officialLunchFinish);		
		
		driverBreaks = DriverBreaksUtils.mergeDriverBreaks(officialLunchBreak, driverBreaks);		
		
		for (DispoDriverBreakBean driverBreak: driverBreaks){
			if (driverBreak.getIsLunchBreak()){
				lunchBreak = driverBreak;
			}
		}		
	}

	public List<DispoDriverBreakListEntity> getMopBreaks() {
		return mopBreaks;
	}

	public void addTrafficJam(TrafficJamEntity tj) {
		trafficJams.add(tj);
		this.isChanged = true;		
	}

	public List<TrafficJamEntity> getTrafficJams() {
		return trafficJams;
	}

	public void addDriverMessage(DispoMessageListEntity dme) {
		if (dme.getPdaMessageTypeCd().longValue() == Constant.PDA_MESSAGE_TYPE_MARKET_INFO) {
			return;
		}
		driverMessages.add(dme);
		this.isChanged = true;				
		if (!dme.getReceiverAckFlag()){
			hasNewMessage = true;
			if (dme.getPdaMessageTypeCd().longValue() == Constant.PDA_MESSAGE_TYPE_ERROR){
				hasNewError = true;
			}
		}
	}
	
	public void addLoad(DispoTruckLoadListV2Entity load){
		loads.add(load);
		this.isChanged = true;
	}
	
	public List<DispoTruckLoadListV2Entity> getLoads(){
		return loads;
	}

	public boolean getHasNewMessage() {
		return hasNewMessage;
	}

	public boolean getHasNewError() {
		return hasNewError;
	}
	
	public boolean getIsBag() {
		return ((truck.getContainerTrspCatCd() != null) && (truck.getContainerTrspCatCd().longValue() == Constant.TKATEGORIE_BAG)) ||
				((truck.getTrailerTrspCatCd() != null) && (truck.getTrailerTrspCatCd().longValue() == Constant.TKATEGORIE_BAG)) ||
				((truck.getTruckTransportCatCd() != null) && (truck.getTruckTransportCatCd().longValue() == Constant.TKATEGORIE_BAG));
	}

	public boolean isCompositionValid() {
		return isCompositionValid;
	}

	public void setCompositionValid(boolean isCompositionValid) {
		this.isCompositionValid = isCompositionValid;
	}

	public boolean getIsNotAuthorized() {
		return isNotAuthorized;
	}

	public DispoHomePlantEntity getHomePlant() {
		return homePlant;
	}

	public void setHomePlant(DispoHomePlantEntity homePlant) {
		this.homePlant = homePlant;
		this.isChanged = true;		
	}
	
	/**
	 * @return the isChanged
	 */
	public boolean getIsChanged() {
		return isChanged;
	}

	/**
	 * @param isChanged the isChanged to set
	 */
	public void setIsChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
	@Override
	/**
	 * Returns the truck name in the form "Truck: Sometruck (ID 123), carrier: SomeCarrier"
	 */
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("Truck: ").append(truck.getTruckName()).append(" (ID ").append(truck.getTruckId())
		.append("), carrier: ")
		.append(truck.getCarrierDisplayName());

		return sb.toString();
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class GeneralLoadData implements Serializable {
	private static final long serialVersionUID = 7671731648557181544L;
	
	private String deliverySilo;
	private String plantExit;
	private String plantEntrance;
	private String truckPlateNumber;
	private String trailerPlateNumber;
	
	public String getPlantExit() {
		return plantExit;
	}
	public void setPlantExit(String plantExit) {
		this.plantExit = plantExit;
	}
	public String getPlantEntrance() {
		return plantEntrance;
	}
	public void setPlantEntrance(String plantEntrance) {
		this.plantEntrance = plantEntrance;
	}
	public String getTruckPlateNumber() {
		return truckPlateNumber;
	}
	public void setTruckPlateNumber(String truckPlateNumber) {
		this.truckPlateNumber = truckPlateNumber;
	}
	public String getTrailerPlateNumber() {
		return trailerPlateNumber;
	}
	public void setTrailerPlateNumber(String trailerPlateNumber) {
		this.trailerPlateNumber = trailerPlateNumber;
	}
	public String getDeliverySilo() {
		return deliverySilo;
	}
	public void setDeliverySilo(String deliverySilo) {
		this.deliverySilo = deliverySilo;
	}
}

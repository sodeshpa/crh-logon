/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.testdtos;

import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;

/**
 * @author AdNovum Informatik AG
 */
public class TruckComposition {
	
	private TruckEntity truck;
	private TrailerEntity trailer;
	private ContainerEntity container;
	
	public TruckComposition(TruckEntity truck, TrailerEntity trailer, ContainerEntity container) {
		this.truck = truck;
		this.trailer = trailer;
		this.container = container;
	}
	
	public TruckEntity getTruck() {
		return truck;
	}
	public TrailerEntity getTrailer() {
		return trailer;
	}
	public ContainerEntity getContainer() {
		return container;
	}
	
}

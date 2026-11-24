package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.crh.logon.persistence.entities.views.CommExecCarrierPfEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;

/**
 * Purpose is to convert into a structure the connections between commercial 
 * and executing carriers, in order that the structure is usable on the scheduling screen. 
 * The commercial carriers can only come if the users are authorized, but the executing carriers
 * can also appear if the user is not authorized to them!
 * 
 * @author AdNovum Informatik AG
 *
 */
public class DispoCarrierWrapper implements Serializable{

	private static final long serialVersionUID = 1L;

	private final PoolCarrierCarrierEntity poolCarrierPartnerFunction;

	//redundant fields, set in order to be able to refer to them much easier from the javascript
	private final Boolean vnIsExecCarrier;
	private final Boolean vnIsCommCarrier;
	private final String carrierName;
	
	//defining this attribute as a constant to be able to build up map according to this property, if changing carrierId naming please update this constant as well
	public static final String ATTR_CARRIER_ID = "carrierId"; 
	private final Long carrierId;
	
	private List<CommExecCarrierPfEntity> execCarrierPartners = new LinkedList<CommExecCarrierPfEntity>();
	

	public DispoCarrierWrapper(PoolCarrierCarrierEntity poolCarrierPartnerFunction) {
		
		this.poolCarrierPartnerFunction = poolCarrierPartnerFunction;
		
		this.vnIsExecCarrier = poolCarrierPartnerFunction.getVnIsExecCarrier();
		this.vnIsCommCarrier = poolCarrierPartnerFunction.getVnIsCommCarrier();
		this.carrierId = poolCarrierPartnerFunction.getCarrierId();
		this.carrierName = poolCarrierPartnerFunction.getCarrierName();
	}
	
	public void addExecCarrierPartner(CommExecCarrierPfEntity partnerFunction) {
		execCarrierPartners.add(partnerFunction);
	}
	
	public Long getCarrierId() {
		return carrierId;
	}
	
	/**
	 * Returns all executing carrier partner function for the carrier, if applicable.
	 * For pure executing carriers it is not applicable, therefore will never contain anything.
	 * 
	 * @return
	 */
	public List<CommExecCarrierPfEntity> getExecCarrierPartners() {
		return execCarrierPartners;
	}

	public PoolCarrierCarrierEntity getPoolCarrierPartnerFunction() {
		return poolCarrierPartnerFunction;
	}

	public Boolean getVnIsExecCarrier() {
		return vnIsExecCarrier;
	}

	public Boolean getVnIsCommCarrier() {
		return vnIsCommCarrier;
	}

	public String getCarrierName() {
		return carrierName;
	}
}

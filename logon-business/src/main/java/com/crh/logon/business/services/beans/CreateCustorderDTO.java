/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedLoadOrderData;

public class CreateCustorderDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String custorderNumber;
	private final List<ReducedLoadOrderData> loadOrderData;
	
	/**
	 * 
	 */
	public CreateCustorderDTO(String custorderNumber) {
		this.custorderNumber = custorderNumber;
		this.loadOrderData = new ArrayList<ReducedLoadOrderData>();
	}
	
	/**
	 * @return the custorderNumber
	 */
	public String getCustorderNumber() {
		return custorderNumber;
	}
	
	/**
	 * @return the loadOrderData
	 */
	public List<ReducedLoadOrderData> getLoadOrderData() {
		return loadOrderData;
	}
	
}

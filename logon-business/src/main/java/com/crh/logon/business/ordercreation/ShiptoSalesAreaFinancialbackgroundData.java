/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

public class ShiptoSalesAreaFinancialbackgroundData implements Serializable{

	private static final long serialVersionUID = 1L;

	public Set<Long> getValidBilltoSapBpIds() {
		return validBilltoSapBpIds;
	}


	public void setValidBilltoSapBpIds(Set<Long> validBilltoSapBpIds) {
		this.validBilltoSapBpIds = validBilltoSapBpIds;
	}


	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}


	public SapBpEntity getSoldToSapBp() {
		return soldToSapBp;
	}


	public void setShiptoBilltos(List<ShiptoBilltoEntity> shiptoBilltos) {
		this.shiptoBilltos = shiptoBilltos;
	}


	public void setValidPayerList(List<SapBpEntity> validPayerList) {
		this.validPayerList = validPayerList;
	}


	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(ShiptoSalesAreaFinancialbackgroundData.class);

	private  ShiptoSareaEntity shiptoSarea;
	public void setShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}


	public void setSoldToSapBp(SapBpEntity soldToSapBp) {
		this.soldToSapBp = soldToSapBp;
	}

	public void setSoldtoCustomerDeliveryBlock(Boolean soldtoCustomerDeliveryBlock) {
		this.soldtoCustomerDeliveryBlock = soldtoCustomerDeliveryBlock;
	}

	public void setSoldtoCustomerOrderBlock(Boolean soldtoCustomerOrderBlock) {
		this.soldtoCustomerOrderBlock = soldtoCustomerOrderBlock;
	}
	
	public void setSoldtoCustomerPostingBlock(Boolean soldtoCustomerPostingBlock) {
		this.soldtoCustomerPostingBlock = soldtoCustomerPostingBlock;
	}

	/**
	 * @return the shiptoSareaMarkedForDel
	 */
	public Boolean getShiptoSareaMarkedForDel() {
		return shiptoSareaMarkedForDel;
	}


	/**
	 * @param shiptoSareaMarkedForDel the shiptoSareaMarkedForDel to set
	 */
	public void setShiptoSareaMarkedForDel(Boolean shiptoSareaMarkedForDel) {
		this.shiptoSareaMarkedForDel = shiptoSareaMarkedForDel;
	}


	/**
	 * @return the shiptoSareaSgroupActive
	 */
	public Boolean getShiptoSareaSgroupActive() {
		return shiptoSareaSgroupActive;
	}


	/**
	 * @param shiptoSareaSgroupActive the shiptoSareaSgroupActive to set
	 */
	public void setShiptoSareaSgroupActive(Boolean shiptoSareaSgroupActive) {
		this.shiptoSareaSgroupActive = shiptoSareaSgroupActive;
	}


	/**
	 * @return the shiptoSareaSofficeActive
	 */
	public Boolean getShiptoSareaSofficeActive() {
		return shiptoSareaSofficeActive;
	}


	/**
	 * @param shiptoSareaSofficeActive the shiptoSareaSofficeActive to set
	 */
	public void setShiptoSareaSofficeActive(Boolean shiptoSareaSofficeActive) {
		this.shiptoSareaSofficeActive = shiptoSareaSofficeActive;
	}


	private  SapBpEntity soldToSapBp;
	
	private Set<Long> validBilltoSapBpIds;
	private List<ShiptoBilltoEntity> shiptoBilltos;
	private List<SapBpEntity> validPayerList;

	/**
	 * Indicates if the soldto's tlgns_sap_bp_companycode entry has the posting_block flag set.
	 */
	private boolean soldtoCompanycodeBlocked;
	
	/**
	 * Customer Delivery Block in SAP (KNVV_LIFSD). Expected for Sold-To only. 
	 * If set, Logon will display warning, possibly forbid disposition.
	 */
	private Boolean soldtoCustomerDeliveryBlock;
	
	/**
	 * Customer Order Block in SAP (KNVV_AUFSD). Expected for Sold-To only. 
	 * If set, Logon will display a warning, possibly even forbid creating an order.
	 */
	private Boolean soldtoCustomerOrderBlock;
	
	/**
	 * Indicates if the shiptoSarea has been deleted in SAP.
	 */
	private Boolean shiptoSareaMarkedForDel;
	
	/**
	 * The ctl_act_cd flag of the shipto's sales group for this sales area.
	 */
	private Boolean shiptoSareaSgroupActive;
	
	/**
	 * The ctl_act_cd flag of the shipto's sales office for this sales area.
	 */
	private Boolean shiptoSareaSofficeActive;

	private Boolean soldtoCustomerPostingBlock;


	/**
	 * Returns the valid billtos.
	 * @return
	 */
	protected List<ShiptoBilltoEntity> getValidBilltos() {
		List<ShiptoBilltoEntity> validBilltos = new ArrayList<ShiptoBilltoEntity>();
		validBilltos.addAll(shiptoBilltos);
		
		Iterator<ShiptoBilltoEntity> it = validBilltos.iterator();
		while (it.hasNext()) {
			ShiptoBilltoEntity selectedBillto = (ShiptoBilltoEntity) it.next();
			if (this.validBilltoSapBpIds.contains(selectedBillto.getBilltoSapBp().getSapBpId())){
				if (!selectedBillto.getBilltoSapBp().getMarkedForDeletion().equals("0")){
					it.remove();
				}
			}
			else {
				it.remove();
			}
		}
		
		return validBilltos;
	}


	public List<SapBpEntity> getValidPayerList() {
		return validPayerList;
	}


	/**
	 * Checks if the given Soldto (General Data + Company Code Data) blocks customers from taking orders.
	 * 
	 * @return true if the customer is blocked, false otherwise
	 */
	protected boolean isSoldToPartnerBlocked() {
		// [[
		boolean result = false;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "checkForCustomerCreditBlock");
		}
		// ]]
		try {
			Boolean centralOrderBlock = soldToSapBp.getCentralOrderBlock();
			Boolean centralDeliveryBlock = soldToSapBp.getCentralDeliveryBlock();
			Boolean centralPostingBlock = soldToSapBp.getCentralPostingBlock();
			
			if ((centralDeliveryBlock != null && centralDeliveryBlock.booleanValue())
				|| (centralOrderBlock != null && centralOrderBlock.booleanValue())
				|| (centralPostingBlock != null && centralPostingBlock.booleanValue())) {
				return result = true;
			}
			
			if (soldtoCompanycodeBlocked) {
				return result = true;
			}
			
			return result = false;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Checks if the given (Soldto) Sales Area blocks customers from taking orders.
	 * 
	 * @param soldtoSapBpSarea the SapBpSareaEntity for a Soldto Sales Area
	 * @return true if the customer is blocked, false otherwise
	 */
	public boolean isSoldtoSapBpSalesAreaBlocked() {
		// [[
		boolean result = false;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isSoldtoSapBpSalesAreaBlocked");
		}
		// ]]
		try {
			Boolean customerDeliveryBlock = soldtoCustomerDeliveryBlock;
			Boolean customerOrderBlock = soldtoCustomerOrderBlock;
			Boolean customerPostingBlock = soldtoCustomerPostingBlock;
			
			return result = (customerDeliveryBlock != null && customerDeliveryBlock.booleanValue())
					|| (customerOrderBlock != null && customerOrderBlock.booleanValue() 
					|| (customerPostingBlock != null && customerPostingBlock.booleanValue()) );
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	public List<ShiptoBilltoEntity> getShiptoBilltos() {
		return shiptoBilltos;
	}

	/**
	 * @param soldtoCompanycodeBlocked the soldtoCompanycodeBlocked to set
	 */
	public void setSoldtoCompanycodeBlocked(boolean soldtoCompanycodeBlocked) {
		this.soldtoCompanycodeBlocked = soldtoCompanycodeBlocked;
	}
	
	
	
}

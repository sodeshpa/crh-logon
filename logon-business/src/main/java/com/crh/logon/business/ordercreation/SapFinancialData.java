/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.SapFinancialDataEntity;

/**
 * This class corresponds exactly to the generated view entity SapFinancialDataEntity,
 * but it converts the latter (which is a flat list) into a more structured object.
 * For example, given a Soldto, a Shipto, and a Salesarea, a query of SapFinancialDataEntity
 * may return several rows:
 * <ul>
 * 	<li>one for each billto,</li>
 * 	<li>times one for each payer,</li>
 * 	<li>times one for each shipto-billto</li>
 * </ul>
 * <p>
 * Differently put, a list of SapFinancialDataEntity objects for a given soldto, shipto, and sarea
 * has a structure like this:
 * <p>
 * <pre>
 * soldtoId A | shiptoId B | sareaId C | billtoId D | payerId F | shiptoBilltoId H
 * soldtoId A | shiptoId B | sareaId C | billtoId D | payerId F | shiptoBilltoId I
 * soldtoId A | shiptoId B | sareaId C | billtoId D | payerId G | shiptoBilltoId H
 * soldtoId A | shiptoId B | sareaId C | billtoId D | payerId G | shiptoBilltoId I
 * soldtoId A | shiptoId B | sareaId C | billtoId E | payerId F | shiptoBilltoId H
 * soldtoId A | shiptoId B | sareaId C | billtoId E | payerId F | shiptoBilltoId I
 * soldtoId A | shiptoId B | sareaId C | billtoId E | payerId G | shiptoBilltoId H
 * soldtoId A | shiptoId B | sareaId C | billtoId E | payerId G | shiptoBilltoId I
 * </pre>
 * <p>
 * The corresponding <code>SapFinancialData</code> object has a structure like this:
 * <p>
 * <pre>
 * | billtoId D |--------------------+
 * | billtoId E |                    |
 *                             +-----+-------------------------------+
 * | payerId F |---------------| soldtoId A | shiptoId B | sareaId C |
 * | payerId G |               +------+------------------------------+
 *                                    |
 * | shiptoBilltoId H |---------------+
 * | shiptoBilltoId I |
 * </pre>
 */
public class SapFinancialData implements Serializable
{
	/**
	 * Data relevant to a billto. Corresponds to the tlgns_sap_bp table joined via
	 * tlgns_sap_bp_pf_billto and to tlgnc_shipto_billto.
	 */
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SapFinancialData.class);
	
	private static final long serialVersionUID = 1L;

	/**
	 * The sap_bp_id of the soldto to which this data belongs.
	 */
	final private Long soldtoSapBpId;
	
	/**
	 * The sap_bp_id of the shipto to which this data belongs.
	 */
	final private Long shiptoSapBpId;
	
	/**
	 * The ID of the tlgnc_sarea to which this data belongs.
	 */
	final private Long sareaId;
	
	/**
	 * Customer Delivery Block in SAP (KNVV_LIFSD). Expected for Sold-To only. 
	 * If set, Logon will display warning, possibly forbid disposition.
	 */
	final private Boolean soldtoCustomerDeliveryBlock;
	
	/**
	 * Customer Order Block in SAP (KNVV_AUFSD). Expected for Sold-To only. 
	 * If set, Logon will display a warning, possibly even forbid creating an order.
	 */
	final private Boolean soldtoCustomerOrderBlock;
	
	
	final private Boolean soldtoCustomerPostingBlock;
	
	/**
	 * Indicates if the shiptoSarea has been deleted in SAP.
	 */
	final private Boolean shiptoSareaMarkedForDel;
	
	/**
	 * The ctl_act_cd flag of the shipto's sales group for this sales area.
	 */
	final private Boolean shiptoSareaSgroupActive;
	
	/**
	 * The ctl_act_cd flag of the shipto's sales office for this sales area.
	 */
	final private Boolean shiptoSareaSofficeActive;
	
	/**
	 * The set of billto IDs joined via soldto / tlngs_sap_bp_pf_soldto. These are the IDs of all
	 * billto partners in SAP of the soldto.
	 */
	final private Set<Long> soldtoBilltoSapBpIds = new HashSet<Long>();
	
	/**
	 * Flag to indicate if soldto's sap_bp_companycodes is blocked
	 */
	final private boolean soldtoCompanycodeBlocked;
	
	/**
	 * Set of sap_bp_ids of the soldto's payer partners.
	 */
	final private Set<Long> soldtoPayerSapBpIds = new HashSet<Long>();

	/**
	 * Creates a new SapFinancialData object from a list of view results from
	 * vlgns_sap_financial_data.
	 * @param entities A non-empty list of SapFinancialDataEntity object corresponding to the rows
	 * returned by the view. Each row must have the <em>same</em> soldto, shipto, and sarea.
	 * @throws IllegalArgumentException if the passed entities are not for a unique
	 * (soldto, shipto, sarea) tuple, or if the list of entities is empty.
	 */
	public SapFinancialData(List<SapFinancialDataEntity> entities)
	throws IllegalArgumentException
	{
		// [[
		Object[][] params = {{"entities", entities}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SapFinancialData", params);
		}
		// ]]
		try {
			/* Set all attributes that are invariant across all rows, taking them from the first
			 * row.
			 */
			final SapFinancialDataEntity firstEntity = entities.get(0);
			soldtoSapBpId = firstEntity.getSoldtoSapBpId();
			shiptoSapBpId = firstEntity.getShiptoSapBpId();
			sareaId = firstEntity.getSareaId();
			
			// nullable; default = not blocked
			if (firstEntity.getSoldtoCustDeliveryBlock() != null) {
				soldtoCustomerDeliveryBlock = firstEntity.getSoldtoCustDeliveryBlock();
			}
			else {
				soldtoCustomerDeliveryBlock = Boolean.FALSE;
			}
			
			// nullable; default = not blocked
			if (firstEntity.getSoldtoCustOrderBlock() != null) {
				soldtoCustomerOrderBlock = firstEntity.getSoldtoCustOrderBlock();
			}
			else {
				soldtoCustomerOrderBlock = Boolean.FALSE;
			}
			
			if (firstEntity.getSoldtoCustPostingBlock() != null) {
				soldtoCustomerPostingBlock = firstEntity.getSoldtoCustPostingBlock();
			}
			else {
				soldtoCustomerPostingBlock = Boolean.FALSE;				
			}
			
			// not nullable
			shiptoSareaMarkedForDel = Constant.MARKED_FOR_DELETION.equals(
					firstEntity.getShiptoSareaMarkedForDel());
			
			// nullable; default = active
			if (firstEntity.getShiptoSgroupActCd() != null) {
				shiptoSareaSgroupActive = firstEntity.getShiptoSgroupActCd();
			}
			else {
				shiptoSareaSgroupActive = Boolean.TRUE;
			}
			
			// nullable; default = active
			if (firstEntity.getShiptoSofficeActCd() != null) {
				shiptoSareaSofficeActive = firstEntity.getShiptoSofficeActCd();
			}
			else {
				shiptoSareaSofficeActive = Boolean.TRUE;
			}
			
			if (firstEntity.getSoldtoCcPostingBlock() != null)
			{
				soldtoCompanycodeBlocked = firstEntity.getSoldtoCcPostingBlock();
			}
			else {
				soldtoCompanycodeBlocked = Boolean.FALSE;
			}
			
			// Go over all rows and set row-specific attributes.
			for (SapFinancialDataEntity entity : entities)
			{
				soldtoBilltoSapBpIds.add(entity.getBilltoSapBpId());
				soldtoPayerSapBpIds.add(entity.getPayerSapBpId());
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @return the soldtoCustomerDeliveryBlock flag; never null
	 */
	public Boolean getSoldtoCustomerDeliveryBlock() {
		return soldtoCustomerDeliveryBlock;
	}


	/**
	 * @return the soldtoCustomerOrderBlock flag; never null
	 */
	public Boolean getSoldtoCustomerOrderBlock() {
		return soldtoCustomerOrderBlock;
	}
	
	/**
	 * @return the soldtoCustomerPostingBlock flag; never null
	 */
	public Boolean getSoldtoCustomerPostingBlock() {
		return soldtoCustomerPostingBlock;
	}

	/**
	 * @return the shiptoSareaMarkedForDel flag; never null
	 */
	public Boolean getShiptoSareaMarkedForDel() {
		return shiptoSareaMarkedForDel;
	}


	/**
	 * @return the shiptoSareaSgroupActive flag; never null
	 */
	public Boolean getShiptoSareaSgroupActive() {
		return shiptoSareaSgroupActive;
	}


	/**
	 * @return the shiptoSareaSofficeActive flag; never null
	 */
	public Boolean getShiptoSareaSofficeActive() {
		return shiptoSareaSofficeActive;
	}


	/**
	 * @return the IDs of the soldto's billto partners
	 */
	public Set<Long> getSoldtoBilltoSapBpIds() {
		return soldtoBilltoSapBpIds;
	}


	/**
	 * @return the soldtoCompanycodeBlocked flag; never null
	 */
	public boolean isSoldtoCompanycodeBlocked() {
		return soldtoCompanycodeBlocked;
	}


	/**
	 * @return the soldtoPayerSapBpIds
	 */
	public Set<Long> getSoldtoPayerSapBpIds() {
		return soldtoPayerSapBpIds;
	}

}

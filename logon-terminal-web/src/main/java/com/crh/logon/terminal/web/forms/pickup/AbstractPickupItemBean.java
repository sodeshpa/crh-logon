/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.forms.pickup;

import java.io.Serializable;
import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;


/**
 * 
 * Purpose of this class is to have a common bean above the {@link CustorderItemEntity} and the
 * pallet items which are {#link {@link OrderItemEntity}
 *
 */
public abstract class AbstractPickupItemBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(AbstractPickupItemBean.class);

	protected DeliverySettingDTO deliverySettingDTO;

	
	public AbstractPickupItemBean(DeliverySettingDTO deliverySetting) {
		// [[
		Object[][] params = {{"deliverySetting", deliverySetting}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "AbstractPickupItemBean", params);
		}
		// ]]
		try {
			if (logDebugEnabled) {
				LOG.debug("Selected delivery setting [compositeIdentifier=" + deliverySetting.getCompositeIdentifier() + "]");
			}
			this.deliverySettingDTO = deliverySetting;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public String getCompositeId(){
		return deliverySettingDTO.getCompositeIdentifier();	
	}
	

	public DeliverySettingDTO getDeliverySetting() {
		return deliverySettingDTO;
	}
	
	public abstract void setQuantity(BigDecimal quantity);
	
	public abstract Object getItemEntity();

}


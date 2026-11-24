/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoSiloVerificationWrapper implements Serializable {

	private static final long serialVersionUID = -5921314875499278414L;
	private ShiptoSiloEntity shiptoSilo;
	private SiloVerificationType siloVerificationType;
	
	
	public ShiptoSiloEntity getShiptoSilo() {
		return shiptoSilo;
	}

	public void setShiptoSilo(ShiptoSiloEntity shiptoSilo) {
		this.shiptoSilo = shiptoSilo;
	}

	public SiloVerificationType getSiloVerificationType() {
		return siloVerificationType;
	}

	public void setSiloVerificationType(SiloVerificationType siloVerificationType) {
		this.siloVerificationType = siloVerificationType;
	}

	public enum SiloVerificationType {
		
		NOT_VERIFIED(Constant.SILO_VERIFICATION_TYPE_NOT_VERIFIED),
		RFID_SCAN(Constant.SILO_VERIFICATION_TYPE_RFID_SCAN),
		RFID_NAME(Constant.SILO_VERIFICATION_TYPE_RFID_NAME),
		RFID_LABEL(Constant.SILO_VERIFICATION_TYPE_RFID_LABEL);
		
		private long verificationType;

		private SiloVerificationType(long verificationType) {
			this.verificationType = verificationType;
		}

		public long getVerificationType() {
			return verificationType;
		}
	}
	
}

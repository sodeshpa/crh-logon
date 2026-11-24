package com.crh.logon.terminal.web.forms;

import com.crh.logon.business.services.utils.TimeZone;

public abstract class TerminalOrderDTO<EntityClass> {

	protected EntityClass terminalOrderEntity;
	protected String fmtActualDeliveryDat;

	protected String fmtDeliveryFromDat;

	protected String fmtDeliveryToDat;

	protected TimeZone shippingPointTimeZone;

	public TerminalOrderDTO(EntityClass terminalOrderEntity, TimeZone shippingPointTimeZone) {
		this.terminalOrderEntity = terminalOrderEntity;
		this.shippingPointTimeZone = shippingPointTimeZone;
		initFmtTimes();
	}

	abstract protected void initFmtTimes();

	public String getFmtActualDeliveryDat() {
		return fmtActualDeliveryDat;
	}

	public String getFmtDeliveryFromDat() {
		return fmtDeliveryFromDat;
	}

	public String getFmtDeliveryToDat() {
		return fmtDeliveryToDat;
	}

	public TimeZone getShippingPointTimeZone() {
		return shippingPointTimeZone;
	}

	public EntityClass getTerminalOrderEntity() {
		return terminalOrderEntity;
	}

}
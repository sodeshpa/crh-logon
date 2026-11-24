/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.forms;

import java.util.Date;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.views.PickupTerminalEntity;

/**
 * This is a simple wrapper / bean / DTO for
 * BagTerminalEnity and PickupTerminalEntity
 * in order to have timezone formatted date texts
 * instead of the default (Europe/Paris) formatting of dates coming from the DB.
 * 
 * @author adnovum
 *
 */
public class PickupTerminalOrderDTO extends TerminalOrderDTO<PickupTerminalEntity> {

	public PickupTerminalOrderDTO(PickupTerminalEntity terminalOrderEntity, TimeZone shippingPointTimeZone) {
		super(terminalOrderEntity, shippingPointTimeZone);
	}

	protected void initFmtTimes() {
		Date actualDeliveryDat = terminalOrderEntity.getActualDeliveryDat();
		fmtActualDeliveryDat = shippingPointTimeZone.fmtAsDayMonthYearHourMinute(actualDeliveryDat);
		Date deliveryFromDat = terminalOrderEntity.getDeliveryFromDat();
		fmtDeliveryFromDat = shippingPointTimeZone.fmtAsDayMonthYearHourMinute(deliveryFromDat);
		Date deliveryToDat = terminalOrderEntity.getDeliveryToDat();
		fmtDeliveryToDat = shippingPointTimeZone.fmtAsDayMonthYearHourMinute(deliveryToDat);
	}
}

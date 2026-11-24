package com.crh.logon.order.web.actions.dispo;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.utils.TourPart;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;

/**
 * @uathor AdNovum Informatik AG
 */
public class AssignmentActionSupport extends OrderActionSupport {

	protected TourPart createTourPart(AssignmentPartEntity arr, AssignmentPartEntity dep, Long transportDistanceM) {
		TourPart part = new TourPart(arr, dep, transportDistanceM);

		AssignmentPartEntity entity = part.getLocationAp();
		if (entity != null) {
			part.setDeliveryPlantTimeZone(getTimeZoneService().loadForShippingPoint(
					entity.getShippingpoint(), getUserLocale()));

			part.setShiptoTimeZone(getTimeZoneService().loadForShipTo(
					entity.getShipto(), getUserLocale()));

			if (part.getOrder() != null && part.getOrder().getLnfTimezone() != null) {
				TimezoneBsEntity tzEntity = part.getOrder().getLnfTimezone();
				part.setLnfShiptoTimeZone(new TimeZone(tzEntity.getTimezoneCd(), getUserLocale()));
			}
		}

		part.setLocale(getUserLocale());

		return part;
	}
}

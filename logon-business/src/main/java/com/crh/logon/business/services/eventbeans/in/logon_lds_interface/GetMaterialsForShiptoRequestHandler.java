
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.GetMaterialsForShiptoRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.GetMaterialsForShiptoResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ShiptoMaterialData;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GetMaterialsForShiptoRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_SHIPTO = "shipTo";

	private static final String FIELD_SOLDTO = "soldTo";

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final GetMaterialsForShiptoRequest request = (GetMaterialsForShiptoRequest) eventBean;

		validateMandatory(FIELD_SHIPTO, request.getShipTo());
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final GetMaterialsForShiptoRequest request = (GetMaterialsForShiptoRequest) eventBean;
		final GetMaterialsForShiptoResponse response =
				(GetMaterialsForShiptoResponse) createLogonOKEventResponse(GetMaterialsForShiptoResponse.class);

		/**
		 * This list contains all those delivery settings which are considered valid for this shipto + soldto
		 * combinations.
		 * See {@link DeliverySettingServiceBean#fetchContractsForShiptoSareaSoldto} what contracts are considered
		 * valid.
		 */

		final ShiptoEntity shipTo = getShipTo(request.getShipTo(), FIELD_SHIPTO);
		final SapBpEntity soldTo;
		if (StringUtils.isNotEmpty(request.getSoldTo())) {
			soldTo = getSoldTo(request.getSoldTo(), FIELD_SOLDTO);
		}
		else {
			soldTo = null;
		}

		final DeliverySettingShiptoMap dssm = super.getDeliverySettingShiptoMap(shipTo, soldTo, null);

		final Collection<DeliverySettingDTO> dsDTOs = dssm.getMaterialDeliverySettingList();
		final List<ShiptoMaterialData> smdList = response.getMaterial();
		for (DeliverySettingDTO dsDTO : dsDTOs) {
			smdList.add(ldsService.createShiptoMaterialData(dsDTO));
		}

		return response;
	}
}

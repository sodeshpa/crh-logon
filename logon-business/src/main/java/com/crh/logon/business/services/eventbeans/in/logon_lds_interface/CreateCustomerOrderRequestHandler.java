package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.util.Collection;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.beans.CreateCustorderDTO;
import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.BusinessPartnerData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CreateCustomerOrderResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderData;
import org.apache.commons.lang.BooleanUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class CreateCustomerOrderRequestHandler extends AbstractOrderDataToCustorderRequestHandler implements RequestHandlerLocalInterface {

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final CreateCustomerOrderRequest request = (CreateCustomerOrderRequest) eventBean;
		super.validate(request.getOrder());
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		final CreateCustomerOrderRequest request = (CreateCustomerOrderRequest) eventBean;
		final CreateCustomerOrderResponse response = (CreateCustomerOrderResponse) createLogonOKEventResponse(CreateCustomerOrderResponse.class);

		final OrderData orderData = request.getOrder();
		CustorderDTO processCustorderResult = super.processOrderData(orderData);
		CustorderEntity resultCustomerOrder = processCustorderResult.getCustorderEntity();
		
		preProcessForOneTimeSoldToAndShipTo(resultCustomerOrder, orderData.getSoldTo(), orderData.getShipTo());
		
		CreateCustorderDTO createCustorderDTO = customerOrderService.createCustomerOrderFromLds(
				processCustorderResult, true, true, orderData.isAutoScheduleRelease(), orderData.getPlannedDeliveryTime(), Constant.WEIGH_SYSTEM_LP_WS, true);

		response.setCustomerOrderNo(createCustorderDTO.getCustorderNumber());
		response.getLoadOrderData().addAll(createCustorderDTO.getLoadOrderData());
		
		return response;
	}
	
	private void preProcessForOneTimeSoldToAndShipTo(CustorderEntity custorderEntity, BusinessPartnerData soldTo, BusinessPartnerData shipTo) {
		@SuppressWarnings("unchecked")
		final Collection<OrderEntity> orders = custorderEntity.getOrders();
		for (OrderEntity order : orders) {

			if (BooleanUtils.isTrue(order.getSoldtoSapBp().getTreatAsOnetimeFlag())) {
				order.setOneTimeSoldtoName(soldTo.getName1());
				order.setOneTimeSoldtoStreetHouseno(soldTo.getStreet());
				order.setOneTimeSoldtoCity(soldTo.getCity());
				order.setOneTimeSoldtoPostalCode(soldTo.getZipCode());
				order.setOneTimeSoldtoCountry(soldTo.getCountry());
				order.setOneTimeSoldtoVatNo(soldTo.getVatRegistrationNumber());
			}

			if (BooleanUtils.isTrue(order.getShiptoSapBp().getTreatAsOnetimeFlag())) {
				/*
				 * TODO 19-Oct-2012/nemes: this is not in the BusinessPartnerData, so if this data will be used on a map later we are in trouble.
				order.setLnfCoordLat();
				order.setLnfCoordLon();
				*/
				order.setLnfSiteName(shipTo.getName1());
				order.setLnfSiteStreetHouseNo(shipTo.getStreet());
				order.setLnfSiteCountry(shipTo.getCountry());
				order.setLnfSitePostalCode(shipTo.getZipCode());
				order.setLnfSiteCity(shipTo.getCity());
				order.setLnfSiteVatNo(shipTo.getVatRegistrationNumber());
			}
		}
	}
}

package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.CustorderDTO;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ValidateCustomerOrderRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ValidateCustomerOrderResponse;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ValidateCustomerOrderRequestHandler extends AbstractOrderDataToCustorderRequestHandler
		implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ValidateCustomerOrderRequestHandler.class);

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final ValidateCustomerOrderRequest request = (ValidateCustomerOrderRequest) eventBean;
		super.validate(request.getOrder());
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = { { "eventBean", eventBean } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			final ValidateCustomerOrderRequest request = (ValidateCustomerOrderRequest) eventBean;
			final ValidateCustomerOrderResponse response = createLogonOKEventResponse(ValidateCustomerOrderResponse.class);

			final OrderData orderData = request.getOrder();
			final CustorderDTO processCustorderResult = super.processOrderData(orderData);
			final CustorderEntity resultCustomerOrder = processCustorderResult.getCustorderEntity();

			final List<OrderEntity> orders = resultCustomerOrder.getOrders();
			final List<OrderData> resultOrders = response.getOrder();

			for (OrderEntity order : orders) {
				resultOrders.add(ldsService.createOrderData(order));
			}

			result = response;
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
}

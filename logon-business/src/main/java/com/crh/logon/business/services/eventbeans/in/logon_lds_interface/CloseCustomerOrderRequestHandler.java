/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.CustorderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderLifecycleReturnCode;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CloseCustomerOrderRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CloseCustomerOrderResponse;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class CloseCustomerOrderRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_CUSTORDER_ID = "customerOrderNo";

	@EJB
	private CustorderServiceLocalInterface customerOrderService;

	/* (non-Javadoc)
	 * @see LogonEventRequestHandler#validate(LogonEventRequest)
	 */
	@Override
	protected void validate(LogonEventRequest eventBean) {
		final CloseCustomerOrderRequest request = (CloseCustomerOrderRequest) eventBean;

		validateMandatory(FIELD_CUSTORDER_ID, request.getCustomerOrderNo());
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final CloseCustomerOrderRequest request = (CloseCustomerOrderRequest) eventBean;
		final CloseCustomerOrderResponse response = createLogonOKEventResponse(CloseCustomerOrderResponse.class);

		final Long customerOrderNo = parse(request.getCustomerOrderNo(), FIELD_CUSTORDER_ID, true);
		final CustorderEntity custorderEntity = customerOrderService.findCustorderByNumber(customerOrderNo);

		checkIfOrderWasCreatedByLDS(custorderEntity);

		final Map<Long, OrderEntity> deleteOrderMap = new HashMap<Long, OrderEntity>();
		final List<Long> manualDeallocateOrderNumberList = new ArrayList<Long>();

		OrderUtils.fillOrderNumberLists(custorderEntity, deleteOrderMap, manualDeallocateOrderNumberList);

		final CustorderLifecycleReturnCode code =
				orderService.cascadeCloseCustomerOrder(deleteOrderMap, custorderEntity.getCustorderId());

		if (code.isError()) {
			throw newApplicationError(String.format(
					"Failed to close Customer Order with Id: %d, reason: %s", customerOrderNo, code.toString()));
		}

		return response;
	}

	/**
	 * We can only allow to close orders that has been created by LDS. This requirement should be in synch with the how {@link
	 * CreateCustomerOrderRequestHandler} creates the order.
	 *
	 * @param custorderEntity - to be checked
	 * @throws LogonEventException if the {@link CustorderEntity} was not created by LDS
	 */
	private void checkIfOrderWasCreatedByLDS(CustorderEntity custorderEntity) {
		if (custorderEntity.getOrderCreationType().getOrderCreationTypeCd() != Constant.ORDER_CREATION_TYPE_EXTERNAL_SYSTEM) {
			throw newApplicationError("Customer Order was not created by LDS: " + custorderEntity.getCustorderId());
		}
	}
}
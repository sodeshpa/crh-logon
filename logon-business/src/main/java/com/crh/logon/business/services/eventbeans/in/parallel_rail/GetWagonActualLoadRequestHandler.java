/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.parallel_rail;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.GetWagonActualLoadRequest;
import com.holcim.logon.eventbeans.in.parallel_rail.GetWagonActualLoadResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * @author AdNovum Informatik AG
 * @deprecated Old interface, no longer used.
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GetWagonActualLoadRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	@EJB private OrderServiceLocalInterface orderService;
	
	
	protected void validate(LogonEventRequest eventBean) {
    	
		GetWagonActualLoadRequest bean = (GetWagonActualLoadRequest)eventBean;
    	validateMandatory(FIELD_WAGON, bean.getWagon());
    }
	
	
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		GetWagonActualLoadRequest bean = (GetWagonActualLoadRequest)eventBean;
		WagonEntity wagon = getWagon(bean.getWagon());
		
		// Get the load assigned to the wagon
		// Supports only 1 bulk load/wagon
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
		filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
		List loadList = getDaoProvider().getLoadEntityDao().find(filter);
		if ( (loadList == null) || (loadList.isEmpty()) ) {
			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.NO_LOAD_ON_WAGON, bean.getWagon()));
		}
		else if (loadList.size() > 1) {
			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.INCOMPATIBLE_WAGON, bean.getWagon()));
		}
		LoadEntity load = (LoadEntity)loadList.get(0);
		// Get the load
		GetWagonActualLoadResponse response = (GetWagonActualLoadResponse)createLogonEventResponse(GetWagonActualLoadResponse.class, ParallelRailResponseCode.OK);
		response.setLoadingTime(load.getLoadingTs());
		response.setMaterial(load.getSapMaterial().getSapMaterialIfcNo());
		if (load.getLoadOrderId() != null) {
			response.setVerladeauftragID(load.getLoadOrderId());
		}
		else {
			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "No parallel verladeauftragId found for load " + load.getLoadId()));
		}
		// Get customer
		SapBpEntity shiptoSapBp = null;
		Collection orders = orderService.getOrders(load);
		if (!orders.isEmpty()) {
			if (orders.size() == 1) {
				OrderEntity order = (OrderEntity)orders.iterator().next();
				shiptoSapBp = order.getShiptoSapBp();
			}
			else {
				
				throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.INCOMPATIBLE_WAGON, bean.getWagon()));
			}
		}
		if (shiptoSapBp != null) {
			response.setCustomer(shiptoSapBp.getSapIfcId());
			response.setCustomerName(shiptoSapBp.getName1());
		}
		
		return response;
	}
}

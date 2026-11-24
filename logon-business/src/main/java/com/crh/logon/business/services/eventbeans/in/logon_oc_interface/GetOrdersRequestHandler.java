package com.crh.logon.business.services.eventbeans.in.logon_oc_interface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.PagingUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.OcGetOrdersViewEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersRequest;
import com.holcim.logon.eventbeans.in.logon_oc_interface.GetOrdersResponse;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GetOrdersRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_SOLDTO_SAPID = "soldToSapId";

	private static final String FIELD_PAGE = "page";

	private static final int PAGE_SIZE = 50;

	private static final int MAX_ORDER_COUNT = 1000;

	private static final String STATUS_OPEN = "OPEN";

	private static final String STATUS_IN_TRANSIT = "IN TRANSIT";

	private static final String STATUS_FINISHED = "FINISHED";

	private static final String STATUS_ERROR = "ERROR";

	private static final String STATUS_DELETED = "DELETED";

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final GetOrdersRequest request = (GetOrdersRequest) eventBean;
		validateMandatory(FIELD_SOLDTO_SAPID, request.getSoldToSapId());
		BigDecimal page = request.getPage();
		validateMandatory(FIELD_PAGE, page);
		try {
			PagingUtil.validatePageField(page, request.getEncodedOrderNumbers());
		} catch (RuntimeException re) {
			throw newApplicationError(re.getMessage());
		}
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		final GetOrdersRequest request = (GetOrdersRequest) eventBean;
		final GetOrdersResponse response = createLogonOKEventResponse(GetOrdersResponse.class);

		String soldToSapId = request.getSoldToSapId();
		String shipToSapId = request.getShiptoSapId();
		Long creationTypeCd = null;
		if (!isEmpty(request.getOrderCreationType())) {
			creationTypeCd = request.getOrderCreationType().longValue();
		}
		List<Long> selectedLogonOrders = request.getSelectedLogonOrders().stream().map(Long::parseLong).collect(Collectors.toList());

		int filterHash = PagingUtil.calculateFilterHash(soldToSapId, shipToSapId, selectedLogonOrders);

		List<Long> allOrderNumbers;
		if (!isEmpty(request.getEncodedOrderNumbers())) {
			try {
				// we have the exact order numbers encoded, decode them into order numbers for paging
				allOrderNumbers = PagingUtil.decodeOrderNumbers(filterHash, request.getEncodedOrderNumbers());
			}
			catch (RuntimeException re) {
				throw newApplicationError(re.getMessage());
			}
		}
		else {
			allOrderNumbers = getOrderNumbers(soldToSapId, shipToSapId, creationTypeCd, selectedLogonOrders);
			response.setEncodedOrderNumbers(PagingUtil.encodeOrderNumbers(filterHash, allOrderNumbers, PAGE_SIZE));
		}

		int orderCount = allOrderNumbers.size();
		int page = request.getPage().intValue();
		int skip = (page - 1) * PAGE_SIZE;

		if (orderCount == 0) {
			return response;
		}

		if (skip >= orderCount) {
			throw newApplicationError("Page number is too big");
		}

		response.setTotalNumberOfPages(PagingUtil.calculateTotalNumberOfPages(orderCount, PAGE_SIZE));

		List<Long> relevantOrderNumbers = PagingUtil.getRelevantOrderNumbers(allOrderNumbers, skip, PAGE_SIZE);
		List<OcGetOrdersViewEntity> results = getOrders(relevantOrderNumbers, soldToSapId, shipToSapId, creationTypeCd);

		List<GetOrdersResponse.Order> orders = new ArrayList<>();
		results.forEach(ocGetOrdersViewEntity -> orders.add(toOrder(ocGetOrdersViewEntity)));

		response.getOrder().addAll(orders);

		return response;
	}

	private static GetOrdersResponse.Order toOrder(OcGetOrdersViewEntity ocGetOrdersViewEntity) {
		GetOrdersResponse.Order order = new GetOrdersResponse.Order();

		order.setLogonOrderNo(ocGetOrdersViewEntity.getOrderNumber().toString());
		order.setSorgCd(ocGetOrdersViewEntity.getSareaSorgCd());
		order.setSoldToSapId(ocGetOrdersViewEntity.getSoldtoSapId());
		order.setShiptoSapId(ocGetOrdersViewEntity.getShiptoSapId());
		order.setEarliestDeliveryTime(ocGetOrdersViewEntity.getDeliveryFromDat());
		order.setLatestDeliveryTime(ocGetOrdersViewEntity.getDeliveryToDat());
		order.setSupplyPlantNo(ocGetOrdersViewEntity.getSupplyPlantNo());
		order.setShippingpointNo(ocGetOrdersViewEntity.getSapShippingpointNo());
		order.setDeliveryType(ocGetOrdersViewEntity.getDeliveryTypeCd().toString());
		order.setOrderStatus(getOrderStatusText(ocGetOrdersViewEntity.getOrderStatusCd()));
		order.getOrderItems().addAll(toOrderItems(ocGetOrdersViewEntity));

		return order;
	}

	private static List<GetOrdersResponse.Order.OrderItems> toOrderItems(OcGetOrdersViewEntity ocGetOrdersViewEntity) {
		List<GetOrdersResponse.Order.OrderItems> orderItems = new ArrayList<>();

		ocGetOrdersViewEntity.getOrderItems().forEach(orderItemEntity -> {
			GetOrdersResponse.Order.OrderItems orderItem = new GetOrdersResponse.Order.OrderItems();

			orderItem.setLogonOrderItemNumber(BigDecimal.valueOf(orderItemEntity.getOrderItemNo()));
			orderItem.setQuantity(orderItemEntity.getOrderedQuantity());

			if (orderItemEntity.getSapMaterial() != null) {
				orderItem.setMaterialNo(orderItemEntity.getSapMaterial().getSapMaterialNo());
			}
			if (orderItemEntity.getOrderedUom() != null) {
				orderItem.setQuantityUom(orderItemEntity.getOrderedUom().getUomCd());
			}

			orderItems.add(orderItem);
		});

		return orderItems;
	}

	private static String getOrderStatusText(Long orderStatusCd) {
		if (orderStatusCd.equals(Constant.ORDER_STATUS_DELETED)) {
			return STATUS_DELETED;
		}
		if (LogonStatusUtils.isBeforeLoading(orderStatusCd)) {
			return STATUS_OPEN;
		}
		if (LogonStatusUtils.isDeliveredOrBeyond(orderStatusCd)) {
			return STATUS_FINISHED;
		}
		if (LogonStatusUtils.isLoadingOrBeyond(orderStatusCd)) {
			return STATUS_IN_TRANSIT;
		}
		return STATUS_ERROR;
	}

	private Filter<OcGetOrdersViewEntity> getOrdersFilter(ReadOnlyDao<OcGetOrdersViewEntity> dao, String soldToSapId, String shiptoSapId,
			Long creationTypeCd, List<Long> selectedLogonOrders) {
		final Filter<OcGetOrdersViewEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OcGetOrdersViewEntity.ATTR_SOLDTO_SAP_ID, soldToSapId));

		if (!isEmpty(shiptoSapId)) {
			filter.addExpression(Restrictions.eq(OcGetOrdersViewEntity.ATTR_SHIPTO_SAP_ID, shiptoSapId));
		}
		if (!isEmpty(creationTypeCd)) {
			filter.addExpression(Restrictions.eq(OcGetOrdersViewEntity.ATTR_ORDER_CREATION_TYPE_CD, creationTypeCd));
		}
		if (selectedLogonOrders != null && !selectedLogonOrders.isEmpty()) {
			filter.addExpression(Restrictions.in(OcGetOrdersViewEntity.ATTR_ORDER_NUMBER, selectedLogonOrders));
		}

		filter.getOrderByExpressions().add(Order.asc(OcGetOrdersViewEntity.ATTR_ORDER_NUMBER));
		filter.getOrderByExpressions().add(Order.desc(OcGetOrdersViewEntity.ATTR_DELIVERY_FROM_DAT));

		return filter;
	}

	private List<Long> getOrderNumbers(String soldToSapId, String shiptoSapId, Long creationTypeCd,
			List<Long> selectedLogonOrders) {
		final ReadOnlyDao<OcGetOrdersViewEntity> dao = getDaoProvider().getOcGetOrdersViewEntityDao();
		final Filter<OcGetOrdersViewEntity> filter = getOrdersFilter(dao, soldToSapId, shiptoSapId, creationTypeCd,
				selectedLogonOrders);

		JPAQuery<OcGetOrdersViewEntity> orderQuery = dao.createJPAQuery();
		orderQuery.setFilter(filter);
		orderQuery.addSelect(SelectExpression.simpleSelect(OcGetOrdersViewEntity.ATTR_ORDER_NUMBER));

		return dao.findNRecs(0, MAX_ORDER_COUNT, orderQuery);
	}

	private List<OcGetOrdersViewEntity> getOrders(List<Long> orderNumbers, String soldToSapId, String shiptoSapId,
			Long creationTypeCd) {
		final ReadOnlyDao<OcGetOrdersViewEntity> dao = getDaoProvider().getOcGetOrdersViewEntityDao();
		final Filter<OcGetOrdersViewEntity> filter = getOrdersFilter(dao, soldToSapId, shiptoSapId, creationTypeCd, orderNumbers);

		return dao.find(filter);
	}
}

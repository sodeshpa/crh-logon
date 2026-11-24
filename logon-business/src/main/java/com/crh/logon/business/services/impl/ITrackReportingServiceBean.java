/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.impl;

import static java.util.stream.Collectors.toList;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.GeneralDisplayNameDto;
import com.crh.logon.business.services.beans.ITrackReportFilterParametersDto;
import com.crh.logon.business.services.dispatchers.ITrackReportingServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ITrackReportingServiceLocalInterface;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.ItrackReportEntity;
import com.crh.logon.persistence.entities.views.ItrackShiptoSapBpEntity;
import com.crh.logon.persistence.entities.views.ItrackSoldtoSapBpEntity;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ITrackReportingServiceBean extends ITrackReportingServiceDispatcher implements ITrackReportingServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ITrackReportingServiceBean.class);

	private static Filter<ItrackSoldtoSapBpEntity> createSoldtoSapBpListFilter(ReadOnlyDao<ItrackSoldtoSapBpEntity> dao,
			String query, boolean sort) {
		// taken from BestellungServiceBean.createSapBpListFilter
		final Filter<ItrackSoldtoSapBpEntity> filter = dao.createFilter();
		if (query != null) {
			filter.addExpression(Restrictions.or(
					Restrictions.ilike(ItrackSoldtoSapBpEntity.ATTR_SOLDTO_NAME1, "%" + query + "%"),
					Restrictions.ilike(ItrackSoldtoSapBpEntity.ATTR_SOLDTO_NAME2, "%" + query + "%"),
					Restrictions.eq(ItrackSoldtoSapBpEntity.ATTR_SOLDTO_SAP_ID, query)));
		}
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(ItrackSoldtoSapBpEntity.ATTR_SOLDTO_NAME1));
		}


		return filter;
	}

	private static Filter<ItrackShiptoSapBpEntity> createShiptoSapBpListFilter(ReadOnlyDao<ItrackShiptoSapBpEntity> dao,
			String query, boolean sort) {
		// taken from BestellungServiceBean.createSapBpListFilter
		final Filter<ItrackShiptoSapBpEntity> filter = dao.createFilter();
		if (query != null) {
			filter.addExpression(Restrictions.or(
					Restrictions.ilike(ItrackShiptoSapBpEntity.ATTR_SHIPTO_NAME1, "%" + query + "%"),
					Restrictions.ilike(ItrackShiptoSapBpEntity.ATTR_SHIPTO_NAME2, "%" + query + "%"),
					Restrictions.eq(ItrackShiptoSapBpEntity.ATTR_SHIPTO_SAP_ID, query)));
		}
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(ItrackShiptoSapBpEntity.ATTR_SHIPTO_NAME1));
		}

		return filter;
	}

	@AdnTrace
	public List<ItrackReportEntity> fetchReportList(ITrackReportFilterParametersDto parameters) {

		final ReadOnlyDao<ItrackReportEntity> dao = getDaoProvider().getItrackReportEntityDao();

		Filter<ItrackReportEntity> filter = createReportFilter(dao, parameters);

		return dao.find(filter);
	}

	private static Filter<ItrackReportEntity> createReportFilter(ReadOnlyDao<ItrackReportEntity> dao, ITrackReportFilterParametersDto parameters){

		final Filter<ItrackReportEntity> filter = dao.createFilter();
		if (parameters.getSelectedSareaIds() != null && !parameters.getSelectedSareaIds() .isEmpty()) {
			filter.addExpression(Restrictions.in(ItrackReportEntity.ATTR_SAREA_ID, parameters.getSelectedSareaIds() ));
		}
		if (parameters.getSelectedCommCarrierIds() != null && !parameters.getSelectedCommCarrierIds().isEmpty()) {
			filter.addExpression(Restrictions.in(ItrackReportEntity.ATTR_COMM_CARRIER_ID, parameters.getSelectedCommCarrierIds()));
		}
		if (parameters.getSelectedShiptoSapBpIds() != null && !parameters.getSelectedShiptoSapBpIds().isEmpty()) {
			filter.addExpression(Restrictions.in(ItrackReportEntity.ATTR_SHIPTO_SAP_BP_ID, parameters.getSelectedShiptoSapBpIds()));
		}
		if (parameters.getSelectedShippingPointIds() != null && !parameters.getSelectedShippingPointIds().isEmpty()) {
			filter.addExpression(Restrictions.in(ItrackReportEntity.ATTR_SP_SHIPPINGPOINT_ID, parameters.getSelectedShippingPointIds()));
		}
		if (parameters.getSelectedExecCarrierIds() != null && !parameters.getSelectedExecCarrierIds().isEmpty()) {
			filter.addExpression(Restrictions.in(ItrackReportEntity.ATTR_EXEC_CARRIER_ID, parameters.getSelectedExecCarrierIds()));
		}
		if (parameters.getSelectedSoldtoSapBpIds() != null && !parameters.getSelectedSoldtoSapBpIds().isEmpty()) {
			filter.addExpression(Restrictions.in(ItrackReportEntity.ATTR_SOLDTO_SAP_BP_ID, parameters.getSelectedSoldtoSapBpIds()));
		}

		ZoneId zoneId = parameters.userTimeZone.getZoneId();
		Date parsedStartDate = Date.from(parameters.getParsedStartDate().atTime(LocalTime.MIDNIGHT).atZone(zoneId).toInstant());
		filter.addExpression(Restrictions.ge(ItrackReportEntity.ATTR_UNLOADING_TIME, parsedStartDate));
		Date parsedEndDate = Date.from(parameters.getParsedEndDate().atTime(LocalTime.MAX).atZone(zoneId).toInstant());
		filter.addExpression(Restrictions.le(ItrackReportEntity.ATTR_UNLOADING_TIME, parsedEndDate));

		filter.getOrderByExpressions().add(Order.asc(ItrackShiptoSapBpEntity.ATTR_SHIPTO_NAME1));

		return filter;
	}

	@AdnTrace
	public List<GeneralDisplayNameDto> fetchSoldtoList(int start, int count, String query) {
		final ReadOnlyDao<ItrackSoldtoSapBpEntity> dao = getDaoProvider().getItrackSoldtoSapBpEntityDao();

		Filter<ItrackSoldtoSapBpEntity> filter = createSoldtoSapBpListFilter(dao, query, true);

		JPAQuery<ItrackSoldtoSapBpEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);

		jpaQuery.addSelect(SelectExpression.simpleSelect(ItrackSoldtoSapBpEntity.ATTR_SOLDTO_SAP_BP_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(ItrackSoldtoSapBpEntity.ATTR_SOLDTO_NAME1));

		return toGeneralDisplayNameDtoList(dao.findNRecs(start, count, jpaQuery));
	}

	@AdnTrace
	public long countSoldtoList(String query) {
		final ReadOnlyDao<ItrackSoldtoSapBpEntity> dao = getDaoProvider().getItrackSoldtoSapBpEntityDao();
		Filter<ItrackSoldtoSapBpEntity> filter = createSoldtoSapBpListFilter(dao, query, false);

		return (Long) dao.count(filter);
	}

	@AdnTrace
	public List<GeneralDisplayNameDto> fetchShiptoList(int start, int count, String query) {
		final ReadOnlyDao<ItrackShiptoSapBpEntity> dao = getDaoProvider().getItrackShiptoSapBpEntityDao();

		Filter<ItrackShiptoSapBpEntity> filter = createShiptoSapBpListFilter(dao, query, true);

		JPAQuery<ItrackShiptoSapBpEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);

		jpaQuery.addSelect(SelectExpression.simpleSelect(ItrackShiptoSapBpEntity.ATTR_SHIPTO_SAP_BP_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(ItrackShiptoSapBpEntity.ATTR_SHIPTO_NAME1));

		return toGeneralDisplayNameDtoList(dao.findNRecs(start, count, jpaQuery));
	}

	@AdnTrace
	public long countShiptoList(String query) {
		final ReadOnlyDao<ItrackShiptoSapBpEntity> dao = getDaoProvider().getItrackShiptoSapBpEntityDao();
		Filter<ItrackShiptoSapBpEntity> filter = createShiptoSapBpListFilter(dao, query, false);

		return (Long) dao.count(filter);
	}


	private static JPAQuery<SareaEntity> createSareaListQuery(ReadWriteDao<SareaEntity> dao,
			String query, boolean sort) {
		// taken from BestellungServiceBean.createSapBpListFilter
		final Filter<SareaEntity> filter = dao.createFilter();
		if (query != null) {
			filter.addExpression(Restrictions.ilike(SareaEntity.ATTR_SAREA_DISPLAY_LONGNAME, "%" + query + "%"));
			filter.addExpression(Restrictions.eq(SareaEntity.ATTR_ITRACK_AVAILABLE_FLAG, Boolean.TRUE));
		}
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(SareaEntity.ATTR_SAREA_DISPLAY_LONGNAME));
		}

		JPAQuery<SareaEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);

		return jpaQuery;
	}
	private static JPAQuery<ShippingpointEntity> createShippingpointListQuery(ReadWriteDao<ShippingpointEntity> dao,
			String query, boolean sort) {
		// taken from BestellungServiceBean.createSapBpListFilter
		final Filter<ShippingpointEntity> filter = dao.createFilter();
		if (query != null) {
			filter.addExpression(Restrictions.ilike(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME, "%" + query + "%"));
		}
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));
		}

		JPAQuery<ShippingpointEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);

		return jpaQuery;
	}

	private static JPAQuery<CarrierEntity> createCommCarrierListQuery(ReadWriteDao<CarrierEntity> dao,
			String query, boolean sort) {
		final Filter<CarrierEntity> filter = dao.createFilter();
		if (query != null) {
			filter.addExpression(Restrictions.ilike(CarrierEntity.ATTR_DISPLAY_NAME, "%" + query + "%"));

		}
		filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR + "."
				+ SapVendorEntity.ATTR_VN_IS_COMM_CARRIER,Boolean.TRUE));
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
		}

		JPAQuery<CarrierEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);

		return jpaQuery;
	}

	private static JPAQuery<CarrierEntity> createExecCarrierListQuery(ReadWriteDao<CarrierEntity> dao,
			String query, boolean sort) {
		final Filter<CarrierEntity> filter = dao.createFilter();
		if (query != null) {
			filter.addExpression(Restrictions.ilike(CarrierEntity.ATTR_DISPLAY_NAME, "%" + query + "%"));
		}
		filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR + "."
				+ SapVendorEntity.ATTR_VN_IS_EXEC_CARRIER,Boolean.TRUE));
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
		}

		JPAQuery<CarrierEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);

		return jpaQuery;
	}

	@AdnTrace
	public long countSareaList(String query) {
		final ReadWriteDao<SareaEntity> dao = getDaoProvider().getSareaEntityDao();
		JPAQuery<SareaEntity> jpaQuery = createSareaListQuery(dao, query, false);
		jpaQuery.addSelect(SelectExpression.countDistinct(SareaEntity.ATTR_SAREA_ID));

		return (Long) dao.findSingle(jpaQuery);
	}

	@AdnTrace
	public List<GeneralDisplayNameDto> fetchShippingpointList(int start, int count, String query) {
		final ReadWriteDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
		JPAQuery<ShippingpointEntity> jpaQuery = createShippingpointListQuery(dao, query, true);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));

		return toGeneralDisplayNameDtoList(dao.findNRecs(start, count, jpaQuery));
	}

	@AdnTrace
	public long countShippingpointList(String query) {
		final ReadWriteDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
		JPAQuery<ShippingpointEntity> jpaQuery = createShippingpointListQuery(dao, query, false);
		jpaQuery.addSelect(SelectExpression.countDistinct(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID));

		return (Long) dao.findSingle(jpaQuery);
	}

	@AdnTrace
	public List<GeneralDisplayNameDto> fetchCommCarrierList(int start, int count, String query) {
		final ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
		JPAQuery<CarrierEntity> jpaQuery = createCommCarrierListQuery(dao, query, true);
		jpaQuery.addSelect(SelectExpression.simpleSelect(CarrierEntity.ATTR_CARRIER_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(CarrierEntity.ATTR_DISPLAY_NAME));

		return toGeneralDisplayNameDtoList(dao.findNRecs(start, count, jpaQuery));
	}

	@AdnTrace
	public List<GeneralDisplayNameDto> fetchExecCarrierList(int start, int count, String query) {
		final ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
		JPAQuery<CarrierEntity> jpaQuery = createExecCarrierListQuery(dao, query, true);
		jpaQuery.addSelect(SelectExpression.simpleSelect(CarrierEntity.ATTR_CARRIER_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(CarrierEntity.ATTR_DISPLAY_NAME));

		return toGeneralDisplayNameDtoList(dao.findNRecs(start, count, jpaQuery));
	}

	@AdnTrace
	public long countCommCarrierList(String query) {
		final ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
		JPAQuery<CarrierEntity> jpaQuery = createCommCarrierListQuery(dao, query, false);
		jpaQuery.addSelect(SelectExpression.countDistinct(CarrierEntity.ATTR_CARRIER_ID));

		return (Long) dao.findSingle(jpaQuery);
	}

	@AdnTrace
	public long countExecCarrierList(String query) {
		final ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
		JPAQuery<CarrierEntity> jpaQuery = createCommCarrierListQuery(dao, query, false);
		jpaQuery.addSelect(SelectExpression.countDistinct(CarrierEntity.ATTR_CARRIER_ID));

		return (Long) dao.findSingle(jpaQuery);
	}

	@AdnTrace
	public List<GeneralDisplayNameDto> fetchSareaList(int start, int count, String query) {
		final ReadWriteDao<SareaEntity> dao = getDaoProvider().getSareaEntityDao();
		JPAQuery<SareaEntity> jpaQuery = createSareaListQuery(dao, query, true);
		jpaQuery.addSelect(SelectExpression.simpleSelect(SareaEntity.ATTR_SAREA_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(SareaEntity.ATTR_SAREA_DISPLAY_LONGNAME));

		return toGeneralDisplayNameDtoList(dao.findNRecs(start, count, jpaQuery));
	}
	private static List<GeneralDisplayNameDto> toGeneralDisplayNameDtoList(List<Object[]> queryResult) {
		return queryResult.stream().map(it -> new GeneralDisplayNameDto((Long) it[0], (String) it[1])).collect(toList());
	}
}

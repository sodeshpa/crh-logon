/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.impl;

import static java.util.Date.from;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import javax.ejb.EJB;
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
import com.crh.logon.business.services.beans.DemandPlanningMatDTO;
import com.crh.logon.business.services.beans.DemandPlanningSapBpDTO;
import com.crh.logon.business.services.dispatchers.DemandPlanningServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DemandPlanningServiceLocalInterface;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.dictionaries.UnitOfMeasurementStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.DemandPlanEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.DemandPlanDiffEntity;
import com.crh.logon.persistence.entities.views.DemandPlanMatEntity;
import com.crh.logon.persistence.entities.views.DemandPlanPlanEntity;
import com.crh.logon.persistence.entities.views.DemandPlanShipSoldEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class DemandPlanningServiceBean extends DemandPlanningServiceDispatcher implements DemandPlanningServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DemandPlanningServiceBean.class);

	@EJB
	private UnitOfMeasurementStoreInterface unitOfMeasurementStore;

	private static JPAQuery<DemandPlanShipSoldEntity> createSoldtoSapBpListQuery(ReadOnlyDao<DemandPlanShipSoldEntity> dao,
			String query, boolean sort) {
		// taken from BestellungServiceBean.createSapBpListFilter
		final Filter<DemandPlanShipSoldEntity> filter = dao.createFilter();
		if (query != null) {
			filter.addExpression(Restrictions.or(
					Restrictions.ilike(DemandPlanShipSoldEntity.ATTR_SOLDTO_NAME1, "%" + query + "%"),
					Restrictions.ilike(DemandPlanShipSoldEntity.ATTR_SOLDTO_NAME2, "%" + query + "%"),
					Restrictions.eq(DemandPlanShipSoldEntity.ATTR_SOLDTO_SAP_ID, query)));
		}
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(DemandPlanShipSoldEntity.ATTR_SOLDTO_NAME1));
		}

		JPAQuery<DemandPlanShipSoldEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);
		jpaQuery.setDistinct(true);

		return jpaQuery;
	}

	private static JPAQuery<DemandPlanShipSoldEntity> createShiptoSapBpListQuery(ReadOnlyDao<DemandPlanShipSoldEntity> dao,
			Long soldtoSapBpId, String query, boolean sort) {
		// taken from BestellungServiceBean.createSapBpListFilter
		final Filter<DemandPlanShipSoldEntity> filter = dao.createFilter();
		if (soldtoSapBpId != null) {
			filter.addExpression(Restrictions.eq(DemandPlanShipSoldEntity.ATTR_SOLDTO_SAP_BP_ID, soldtoSapBpId));
		}
		if (query != null) {
			filter.addExpression(Restrictions.or(
					Restrictions.ilike(DemandPlanShipSoldEntity.ATTR_SHIPTO_NAME1, "%" + query + "%"),
					Restrictions.ilike(DemandPlanShipSoldEntity.ATTR_SHIPTO_NAME2, "%" + query + "%"),
					Restrictions.eq(DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_ID, query)));
		}
		if (sort) {
			filter.getOrderByExpressions().add(Order.asc(DemandPlanShipSoldEntity.ATTR_SHIPTO_NAME1));
		}

		JPAQuery<DemandPlanShipSoldEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filter);
		jpaQuery.setDistinct(true);

		return jpaQuery;
	}

	private static Filter<DemandPlanMatEntity> createMaterialFilter(ReadOnlyDao<DemandPlanMatEntity> dao, Date[] dateRange,
			Long shiptoSapBpId) {
		Filter<DemandPlanMatEntity> filter = dao.createFilter();
		if (shiptoSapBpId != null) {
			filter.addExpression(Restrictions.eq(DemandPlanMatEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		}
		filter.addExpression(Restrictions.and(
				Restrictions.ge(DemandPlanMatEntity.ATTR_VALID_UNTIL_DAT, dateRange[0]),
				Restrictions.le(DemandPlanMatEntity.ATTR_VALID_FROM_DAT, dateRange[1])));

		return filter;
	}

	private static ZonedDateTime toDBTime(LocalDate date, DayOfWeek wantedDay,
			BiFunction<LocalDate, ZoneId, ZonedDateTime> function) {
		if (date.getDayOfWeek() != wantedDay) {
			LOG.warn("Incoming date {} is not a {}", date, wantedDay);
			throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_INVALID_DATE);
		}
		ZoneId zoneId = TimeZone.getDBTimeZone().getZoneId();
		return function.apply(date, zoneId);
	}

	private static Date[] toManageDates(LocalDate startDate) {
		ZonedDateTime startMonday = toDBTime(startDate, DayOfWeek.MONDAY, LocalDate::atStartOfDay);
		ZonedDateTime endSunday = startMonday.plusDays(7).minusSeconds(1);
		return new Date[] { from(startMonday.toInstant()), from(endSunday.toInstant()) };
	}

	private static Date[] toManageDates(LocalDate startDate, LocalDate endDate) {
		ZonedDateTime startMonday = toDBTime(startDate, DayOfWeek.MONDAY, LocalDate::atStartOfDay);
		ZonedDateTime endSunday = toDBTime(endDate, DayOfWeek.SUNDAY, (date, zone) ->
				date.atTime(23, 59, 59).atZone(zone));
		return new Date[] { from(startMonday.toInstant()), from(endSunday.toInstant()) };
	}

	private static void assertWeightCorrect(BigDecimal weight) {
		// make sure the weight isn't negative
		if (weight.compareTo(BigDecimal.ZERO) <= 0 || weight.scale() > DemandPlanEntity.DBSIZE_PRECISION_PLANNED_WEIGHT_T
				|| weight.precision() > DemandPlanEntity.DBSIZE_PLANNED_WEIGHT_T) {
			LOG.warn("Incoming weight {} is invalid", weight);
			throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_INVALID_WEIGHT);
		}
	}

	private static void assertDemandPlanEditingEnabled(DaoProvider daoProvider, long demandPlanId, Long newSapMaterialId,
			boolean isDemandPlanningAdminUser) {
		// make sure the demand plan exists (use the view to enforce authorizations)

		ReadOnlyDao<DemandPlanPlanEntity> demandPlanPlanEntityDao = daoProvider.getDemandPlanPlanEntityDao();
		DemandPlanPlanEntity demandPlanPlanEntity = demandPlanPlanEntityDao.findById(demandPlanId);
		if (demandPlanPlanEntity == null) {
			LOG.warn("DemandPlanPlan with id {} doesn't exist (for the user)", demandPlanId);
			throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_NO_PLAN);
		}

		// we check if the current material can be edited (we can modify an unavailable material)
		assertMaterialChangingEnabled(daoProvider, demandPlanPlanEntity.getShiptoSapBpId(),
				demandPlanPlanEntity.getSapMaterialId(), demandPlanPlanEntity.getStartDate(),
				demandPlanPlanEntity.getEndDate(), isDemandPlanningAdminUser, true);

		if (newSapMaterialId != null && !demandPlanPlanEntity.getDemandPlanId().equals(newSapMaterialId)) {
			// check if the new material can be edited (we cannot modify to an unavailable material)
			assertMaterialChangingEnabled(daoProvider, demandPlanPlanEntity.getShiptoSapBpId(),
					newSapMaterialId, demandPlanPlanEntity.getStartDate(), demandPlanPlanEntity.getEndDate(),
					isDemandPlanningAdminUser, false);
			// in case the sapMaterialId is changing (when we update an existing item), then we have to check if the material
			// is not yet used
			assertMaterialIsNotUsed(daoProvider, demandPlanPlanEntity.getDemandPlanId(), demandPlanPlanEntity.getShiptoSapBpId(),
					newSapMaterialId, demandPlanPlanEntity.getStartDate(), demandPlanPlanEntity.getEndDate());
		}
	}

	private static void assertMaterialChangingEnabled(DaoProvider daoProvider, long shiptoSapBpId, long sapMaterialId,
			Date startDate, Date endDate, boolean isDemandPlanningAdminUser, boolean unavailableEnabled) {
		// mare sure the material exists (use the view to enforce authorizations)
		ReadOnlyDao<DemandPlanMatEntity> matDao = daoProvider.getDemandPlanMatEntityDao();
		Filter<DemandPlanMatEntity> matFilter = matDao.createFilter();
		matFilter.addExpression(Restrictions.eq(DemandPlanMatEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
		matFilter.addExpression(Restrictions.eq(DemandPlanMatEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		matFilter.addExpression(Restrictions.or(
				Restrictions.ge(DemandPlanMatEntity.ATTR_VALID_UNTIL_DAT, startDate),
				Restrictions.le(DemandPlanMatEntity.ATTR_VALID_FROM_DAT, endDate)));
		List<DemandPlanMatEntity> material = matDao.find(matFilter);
		if (material.isEmpty()) {
			LOG.warn("DemandPlanMat with shipto_sap_bp {} and sap_material_id {} doesn't exist (for the user)", shiptoSapBpId,
					sapMaterialId);
			throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_NO_MAT);
		}
		ZoneId dbZoneId = TimeZone.getDBTimeZone().getZoneId();
		ZonedDateTime now = ZonedDateTime.now(dbZoneId);
		ZonedDateTime startDateInDB = ZonedDateTime.ofInstant(startDate.toInstant(), dbZoneId);
		// make sure we don't edit past entries
		if (startDateInDB.isBefore(now)) {
			// we are either editing this week or a previous week, admin can edit this
			// calculate this week's monday
			ZonedDateTime thisWeekMonday = now.truncatedTo(ChronoUnit.DAYS).plusDays(1L - now.getDayOfWeek().getValue());
			if (startDateInDB.isBefore(thisWeekMonday)) {
				LOG.warn("User cannot change a past week's demand plan");
				throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_MAT_CANNOT_CHANGE);
			}
		}
		if (!isDemandPlanningAdminUser) {
			long limit = material.stream().map(DemandPlanMatEntity::getLatestUntilH).filter(Objects::nonNull)
					.max(Long::compareTo).orElse(0L);
			// make sure we are not after the editing time, this also makes sure that we are not editing a past week
			ZonedDateTime latestEditDate = startDateInDB.minusHours(limit);
			if (now.isAfter(latestEditDate)) {
				LOG.warn("DemandPlanMat with shipto_sap_bp {} and sap_material_id {} can no longer be edited/created with start"
						+ " date {}", shiptoSapBpId, sapMaterialId, startDate);
				throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_MAT_CANNOT_CHANGE);
			}
		}
		if (!unavailableEnabled) {
			// check if the material is unavailable
			boolean available =
					material.stream().map(DemandPlanMatEntity::getAvailable).reduce(Boolean.FALSE, Boolean::logicalOr);
			if (!available) {
				// material not available and we unavailableEnabled was also false, throw error
				LOG.warn("DemandPlanMat with shipto_sap_bp {} and sap_material_id {} is unavailable", shiptoSapBpId,
						sapMaterialId);
				throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_MAT_CANNOT_CHANGE);
			}
		}
	}

	private static void assertMaterialIsNotUsed(DaoProvider daoProvider, Long demandPlanId, long shiptoSapBpId,
			long sapMaterialId, Date startDate, Date endDate) {
		ReadOnlyDao<DemandPlanPlanEntity> dao = daoProvider.getDemandPlanPlanEntityDao();
		Filter<DemandPlanPlanEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(DemandPlanPlanEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		filter.addExpression(Restrictions.eq(DemandPlanPlanEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
		filter.addExpression(Restrictions.ge(DemandPlanPlanEntity.ATTR_START_DATE, startDate));
		filter.addExpression(Restrictions.le(DemandPlanPlanEntity.ATTR_END_DATE, endDate));
		if (demandPlanId != null) {
			filter.addExpression(Restrictions.neq(DemandPlanPlanEntity.ATTR_DEMAND_PLAN_ID, demandPlanId));
		}
		long count = dao.count(filter);
		if (count > 0) {
			LOG.warn("sap_materila_id {} is already present for shipto_sap_bp {} in date range {} - {}", sapMaterialId,
					shiptoSapBpId, startDate, endDate);
			throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_MAT_NOT_UNIQUE);
		}
	}

	private static List<DemandPlanningSapBpDTO> toDemandPlanSapBpDTOList(List<Object[]> queryResult) {
		return queryResult.stream().map(it -> new DemandPlanningSapBpDTO((Long) it[0], (String) it[1])).collect(toList());
	}

	@AdnTrace
	public List<DemandPlanningSapBpDTO> fetchSoldtoList(int start, int count, String query) {
		final ReadOnlyDao<DemandPlanShipSoldEntity> dao = getDaoProvider().getDemandPlanShipSoldEntityDao();
		JPAQuery<DemandPlanShipSoldEntity> jpaQuery = createSoldtoSapBpListQuery(dao, query, true);
		jpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SOLDTO_SAP_BP_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SOLDTO_NAME1));

		return toDemandPlanSapBpDTOList(dao.findNRecs(start, count, jpaQuery));
	}

	@AdnTrace
	public long countSoldtoList(String query) {
		final ReadOnlyDao<DemandPlanShipSoldEntity> dao = getDaoProvider().getDemandPlanShipSoldEntityDao();
		JPAQuery<DemandPlanShipSoldEntity> jpaQuery = createSoldtoSapBpListQuery(dao, query, false);
		jpaQuery.addSelect(SelectExpression.countDistinct(DemandPlanShipSoldEntity.ATTR_SOLDTO_SAP_BP_ID));

		return (Long) dao.findSingle(jpaQuery);
	}

	private Set<Long> fetchShiptoSapBpIds(Long soldtoSapBpId) {
		final ReadOnlyDao<DemandPlanShipSoldEntity> dao = getDaoProvider().getDemandPlanShipSoldEntityDao();
		JPAQuery<DemandPlanShipSoldEntity> jpaQuery = createShiptoSapBpListQuery(dao, soldtoSapBpId, null, false);
		jpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_BP_ID));

		return new HashSet<Long>(dao.find(jpaQuery));
	}

	@AdnTrace
	public List<DemandPlanningSapBpDTO> fetchShiptoList(int start, int count, Long soldtoSapBpId, String query) {
		final ReadOnlyDao<DemandPlanShipSoldEntity> dao = getDaoProvider().getDemandPlanShipSoldEntityDao();
		JPAQuery<DemandPlanShipSoldEntity> jpaQuery = createShiptoSapBpListQuery(dao, soldtoSapBpId, query, true);
		jpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_BP_ID));
		jpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SHIPTO_NAME1));

		return toDemandPlanSapBpDTOList(dao.findNRecs(start, count, jpaQuery));
	}

	@AdnTrace
	public List<DemandPlanningSapBpDTO> fetchShiptoList(List<Long> shipToSapBpIds, boolean fetchNoLongerValidsToo) {
		// GDI cannot handle an empty input list, so we return with an empty list if the incoming id list is empty
		if (shipToSapBpIds != null && shipToSapBpIds.isEmpty()) {
			return Collections.emptyList();
		}
		if (fetchNoLongerValidsToo) {
			// All Shiptos
			ReadWriteDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
			Filter<SapBpEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.in(SapBpEntity.ATTR_SAP_BP_ID, shipToSapBpIds));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SHIPTO, true));
			filter.getOrderByExpressions().add(Order.asc(SapBpEntity.ATTR_NAME1));
			JPAQuery<SapBpEntity> jpaQuery = dao.createJPAQuery();
			jpaQuery.setFilter(filter);
			jpaQuery.addSelect(SelectExpression.simpleSelect(SapBpEntity.ATTR_SAP_BP_ID));
			jpaQuery.addSelect(SelectExpression.simpleSelect(SapBpEntity.ATTR_NAME1));

			return toDemandPlanSapBpDTOList(dao.find(jpaQuery));
		} else {
			// DemandPlanShipSoldEntity only has valid shiptos that have or had in the past material mappings
			final ReadOnlyDao<DemandPlanShipSoldEntity> dao = getDaoProvider().getDemandPlanShipSoldEntityDao();
			JPAQuery<DemandPlanShipSoldEntity> jpaQuery = createShiptoSapBpListQuery(dao, null, null, true);
			jpaQuery.getFilter().addExpression(Restrictions.in(DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_BP_ID, shipToSapBpIds));
			jpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_BP_ID));
			jpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SHIPTO_NAME1));

			return toDemandPlanSapBpDTOList(dao.find(jpaQuery));
		}
	}

	@AdnTrace
	public long countShiptoList(Long soldtoSapBpId, String query) {
		final ReadOnlyDao<DemandPlanShipSoldEntity> dao = getDaoProvider().getDemandPlanShipSoldEntityDao();
		JPAQuery<DemandPlanShipSoldEntity> jpaQuery = createShiptoSapBpListQuery(dao, soldtoSapBpId, query, false);
		jpaQuery.addSelect(SelectExpression.countDistinct(DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_BP_ID));

		return (Long) dao.findSingle(jpaQuery);
	}

	private List<DemandPlanningMatDTO> fetchMaterialList(ReadOnlyDao<DemandPlanMatEntity> dao,
			Filter<DemandPlanMatEntity> filter) {
		List<DemandPlanMatEntity> list = dao.find(filter);
		Map<Long, DemandPlanningMatDTO> values = new HashMap<>();

		list.forEach(it ->
				values.merge(it.getSapMaterialId(),
						new DemandPlanningMatDTO(it.getSapMaterialId(), it.getPlantName(), it.getAvailable()), (o1, o2) -> {
							if (o1.isAvailable()) {
								return o1;
							}
							else if (o2.isAvailable()) {
								return o2;
							}
							else {
								return o1;
							}
						})
		);
		return new ArrayList<>(values.values());
	}

	@AdnTrace
	public List<DemandPlanningMatDTO> fetchMaterialList(LocalDate startDate, Long shiptoSapBpId) {
		ReadOnlyDao<DemandPlanMatEntity> dao = getDaoProvider().getDemandPlanMatEntityDao();
		Filter<DemandPlanMatEntity> filter = createMaterialFilter(dao, toManageDates(startDate), shiptoSapBpId);

		return fetchMaterialList(dao, filter);
	}

	@AdnTrace
	public List<DemandPlanningMatDTO> fetchMaterialList(LocalDate startDate, LocalDate endDate, List<Long> sapMaterialIds) {
		// GDI cannot handle an empty input list, so we return with an empty list if the incoming id list is empty
		if (sapMaterialIds != null && sapMaterialIds.isEmpty()) {
			return Collections.emptyList();
		}

		ReadOnlyDao<DemandPlanMatEntity> dao = getDaoProvider().getDemandPlanMatEntityDao();
		Filter<DemandPlanMatEntity> filter = createMaterialFilter(dao, toManageDates(startDate, endDate), null);
		filter.addExpression(Restrictions.in(DemandPlanMatEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds));

		return fetchMaterialList(dao, filter);
	}

	private List<DemandPlanPlanEntity> fetchPlanningList(Date[] dateRange, Long soldtoSapBpId, Long shiptoSapBpId) {
		ReadOnlyDao<DemandPlanPlanEntity> dao = getDaoProvider().getDemandPlanPlanEntityDao();
		Filter<DemandPlanPlanEntity> filter = dao.createFilter();
		if (shiptoSapBpId != null) {
			filter.addExpression(Restrictions.eq(DemandPlanPlanEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		}
		else if (soldtoSapBpId != null) {
			Set<Long> shiptoSapBpIds = fetchShiptoSapBpIds(soldtoSapBpId);
			filter.addExpression(Restrictions.in(DemandPlanPlanEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpIds));
		}
		filter.addExpression(Restrictions.ge(DemandPlanPlanEntity.ATTR_START_DATE, dateRange[0]));
		filter.addExpression(Restrictions.le(DemandPlanPlanEntity.ATTR_END_DATE, dateRange[1]));
		filter.getOrderByExpressions().add(Order.asc(DemandPlanPlanEntity.ATTR_START_DATE));

		return dao.find(filter);
	}

	@AdnTrace
	public List<DemandPlanPlanEntity> fetchPlanningList(LocalDate startDate, Long soldtoSapBpId, Long shiptoSapBpId) {
		return fetchPlanningList(toManageDates(startDate), soldtoSapBpId, shiptoSapBpId);
	}

	@AdnTrace
	public List<DemandPlanPlanEntity> fetchPlanningList(LocalDate startDate, LocalDate endDate, Long soldtoSapBpId,
			Long shiptoSapBpId) {
		return fetchPlanningList(toManageDates(startDate, endDate), soldtoSapBpId, shiptoSapBpId);
	}

	@AdnTrace
	public long createPlanning(LocalDate startDay, long shiptoSapBpId, long sapMaterialId, BigDecimal weight,
			boolean isDemandPlanningAdminUser) {
		DaoProvider daoProvider = getDaoProvider();
		// convert the date
		Date[] dateRange = toManageDates(startDay);
		// make sure the shipto is correct, fetch shipto id (use the view to enforce authorizations)
		ReadOnlyDao<DemandPlanShipSoldEntity> shiptoDao = daoProvider.getDemandPlanShipSoldEntityDao();
		JPAQuery<DemandPlanShipSoldEntity> shiptoQuery = createShiptoSapBpListQuery(shiptoDao, null, null, false);
		shiptoQuery.getFilter().addExpression(Restrictions.eq(DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		shiptoQuery.addSelect(SelectExpression.simpleSelect(DemandPlanShipSoldEntity.ATTR_SHIPTO_ID));

		Long shiptoId = (Long) shiptoDao.findSingle(shiptoQuery);
		if (shiptoId == null) {
			LOG.warn("DemandPlanShipto with shipto_sap_bp {} doesn't exist (for the user)", shiptoSapBpId);
			throw new LogonException(Reason.RES_ERROR_DEMANDPLAN_NO_SHIPTO);
		}

		// mare sure the material is correct (use the view to enforce authorizations)
		Date startDate = dateRange[0];
		Date endDate = dateRange[1];
		assertMaterialChangingEnabled(daoProvider, shiptoSapBpId, sapMaterialId, startDate, endDate, isDemandPlanningAdminUser,
				false);
		assertMaterialIsNotUsed(daoProvider, null, shiptoSapBpId, sapMaterialId, startDate, endDate);
		assertWeightCorrect(weight);

		// create DemandPlanEntity and persist it
		DemandPlanEntity demandPlanEntity = new DemandPlanEntity();
		demandPlanEntity.setShipto(daoProvider.getShiptoEntityDao().findById(shiptoId));
		demandPlanEntity.setSapMaterial(daoProvider.getSapMaterialEntityDao().findById(sapMaterialId));
		demandPlanEntity.setStartDate(startDate);
		demandPlanEntity.setEndDate(endDate);
		demandPlanEntity.setPlannedWeightT(weight);

		ReadWriteDao<DemandPlanEntity> demandPlanEntityDao = daoProvider.getDemandPlanEntityDao();
		demandPlanEntityDao.persist(demandPlanEntity);

		return demandPlanEntity.getDemandPlanId();
	}

	@AdnTrace
	public void updatePlanning(long demandPlanId, long sapMaterialId, BigDecimal weight, boolean isDemandPlanningAdminUser) {
		DaoProvider daoProvider = getDaoProvider();
		assertDemandPlanEditingEnabled(daoProvider, demandPlanId, sapMaterialId, isDemandPlanningAdminUser);
		assertWeightCorrect(weight);

		ReadWriteDao<DemandPlanEntity> demandPlanEntityDao = daoProvider.getDemandPlanEntityDao();
		DemandPlanEntity demandPlanEntity = demandPlanEntityDao.findById(demandPlanId);
		if (sapMaterialId != demandPlanEntity.getSapMaterial().getSapMaterialId()) {
			demandPlanEntity.setSapMaterial(daoProvider.getSapMaterialEntityDao().findById(sapMaterialId));
		}
		demandPlanEntity.setPlannedWeightT(weight);
		demandPlanEntityDao.merge(demandPlanEntity);
	}

	@AdnTrace
	public void deletePlanning(long demandPlanId, boolean isDemandPlanningAdminUser) {
		DaoProvider daoProvider = getDaoProvider();
		assertDemandPlanEditingEnabled(daoProvider, demandPlanId, null, isDemandPlanningAdminUser);

		ReadWriteDao<DemandPlanEntity> demandPlanEntityDao = daoProvider.getDemandPlanEntityDao();
		DemandPlanEntity demandPlanEntity = demandPlanEntityDao.findById(demandPlanId);

		demandPlanEntityDao.remove(demandPlanEntity);
	}

	@AdnTrace
	public String getUoMTonsText(Locale locale) {
		UomTxEntity text = unitOfMeasurementStore.findText(Constant.UOM_TONS, locale.getLanguage());
		return text.getTxt();
	}

	@AdnTrace
	public List<DemandPlanDiffEntity> fetchDifferenceBetweenOrderedAndPlanned(LocalDate startDate, LocalDate endDate,
			Long soldtoSapBpId, Long shiptoSapBpId) {

		Date[] dateRange = toManageDates(startDate, endDate);

		// material filtering
		// we can only show orders that could have been planned for material-wise
		ReadOnlyDao<DemandPlanMatEntity> matDao = getDaoProvider().getDemandPlanMatEntityDao();
		Filter<DemandPlanMatEntity> matFilter = createMaterialFilter(matDao, dateRange, shiptoSapBpId);
		JPAQuery<DemandPlanMatEntity> matJpaQuery = matDao.createJPAQuery();
		matJpaQuery.setFilter(matFilter);
		matJpaQuery.setDistinct(true);
		matJpaQuery.addSelect(SelectExpression.simpleSelect(DemandPlanMatEntity.ATTR_SAP_MATERIAL_ID));
		List<Long> sapMaterialIds = matDao.find(matJpaQuery);

		if (sapMaterialIds.isEmpty()) {
			// we have no material, no diff can be displayed
			return Collections.emptyList();
		}

		ReadOnlyDao<DemandPlanDiffEntity> dao = getDaoProvider().getDemandPlanDiffEntityDao();
		Filter<DemandPlanDiffEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.in(DemandPlanDiffEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds));

		// shipto filtering
		if (shiptoSapBpId != null) {
			filter.addExpression(Restrictions.eq(DemandPlanDiffEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		}
		else {
			// we don't want to show all shiptos of the orders, we are only interested in the shiptos that the user could select
			// optional soldto filtering happens in this method
			Set<Long> shiptoSapBpIds = fetchShiptoSapBpIds(soldtoSapBpId);
			filter.addExpression(Restrictions.in(DemandPlanDiffEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpIds));
		}

		filter.addExpression(Restrictions.and(
				Restrictions.ge(DemandPlanDiffEntity.ATTR_START_DATE, dateRange[0]),
				Restrictions.le(DemandPlanDiffEntity.ATTR_END_DATE, dateRange[1])));

		return dao.find(filter);
	}
}

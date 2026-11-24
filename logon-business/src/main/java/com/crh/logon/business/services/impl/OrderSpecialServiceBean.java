/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.OrderSpecialServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderSpecialServiceLocalInterface;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.stores.sap.BusinessPartnerInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.SapMatDescPackEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class OrderSpecialServiceBean extends OrderSpecialServiceDispatcher implements OrderSpecialServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderSpecialServiceBean.class);

	private static final String DEFAULT_SERVICE_PLANT_CD = Constant.PROD_PLANT_SIGGENTHAL;

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	BusinessPartnerInterface businessPartnerStore;

	/**
	 * Returns all plants.
	 * @return
	 */
	public List<RailPlantEntity> fetchPlantList(String plantPart) {
		// [[
		List<RailPlantEntity> result = null;
		Object[][] params = {{"plantPart", plantPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPlantList", params);
		}
		// ]]
		try {

			final ReadOnlyDao<RailPlantEntity> dao = getDaoProvider().getRailPlantEntityDao();
			final Filter<RailPlantEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.ilike(RailPlantEntity.ATTR_PLANT_NAME,
					'%' + plantPart + '%'));
			filter.addExpression(Order.ascCaseInsensitive(RailPlantEntity.ATTR_PLANT_NAME));
			
			return result = dao.find(filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns a list of rail pools.
	 * <b>Can be empty!</b>
	 * @return
	 */
	private List<Long> getRailPoolIDs() {
		final ReadOnlyDao<PoolEntity> dao = getDaoProvider().getPoolEntityDao();
		final Filter<PoolEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(PoolEntity.ATTR_SAP_TRSP_MODE_CD, Constant.TRANSPORTATION_MODE_TRAIN));
		JPAQuery<PoolEntity> query = dao.createJPAQuery();
		query.setFilter(filter);
		query.addSelect(SelectExpression.simpleSelect(PoolEntity.ATTR_POOL_ID));
		@SuppressWarnings("unchecked")
		List<Long> poolIDs = dao.find(query);
		return poolIDs;
	}

	/**
	 * Returns a list of {@code CarrierEntity}s where each carrier belongs to a rail pool and is active.
	 * @param carrierPart - if provided the filtering will respect for the display name of the carriers 
	 */
	public List<CarrierEntity> fetchRailCarriers(String carrierPart) {
		// [[
		List<CarrierEntity> result = null;
		Object[][] params = {{"carrierPart", carrierPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarrierList", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			ReadOnlyDao<PoolCarrierEntity> poolCarrierDao = daoProvider.getPoolCarrierEntityDao();
			Filter<PoolCarrierEntity> poolCarrierFilter = poolCarrierDao.createFilter();

			List<Long> railPoolIDs = getRailPoolIDs();
			if (!railPoolIDs.isEmpty()) {
				poolCarrierFilter.addExpression(DbUtils.createSplittedInRestriction(PoolCarrierEntity.ATTR_POOL_ID, railPoolIDs));
			}
			if (carrierPart != null) {
				poolCarrierFilter.addExpression(Restrictions.ilike(PoolCarrierEntity.ATTR_CARRIER + "." + CarrierEntity.ATTR_DISPLAY_NAME, '%' + carrierPart + '%'));
			}

			poolCarrierFilter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER + "." + CarrierEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));

			JPAQuery<PoolCarrierEntity> poolCarrierQuery = poolCarrierDao.createJPAQuery();
			poolCarrierQuery.setFilter(poolCarrierFilter);
			poolCarrierQuery.addSelect(SelectExpression.simpleSelect(PoolCarrierEntity.ATTR_CARRIER_ID));
			poolCarrierQuery.setDistinct(true);

			List<Long> carrierIds = poolCarrierDao.find(poolCarrierQuery);

			if (carrierIds.isEmpty()) {
				return Collections.<CarrierEntity>emptyList();
			}

			ReadOnlyDao<CarrierEntity> carrierDao = daoProvider.getCarrierEntityDao();
			Filter<CarrierEntity> carrierFilter = carrierDao.createFilter();
			carrierFilter.addExpression(DbUtils.createSplittedInRestriction(CarrierEntity.ATTR_CARRIER_ID, carrierIds));
			carrierFilter.addExpression(Order.ascCaseInsensitive(CarrierEntity.ATTR_DISPLAY_NAME));

			List<CarrierEntity> carriers = carrierDao.find(carrierFilter);

			return carriers;

		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * @return null if there is no mapped {@link CarrierEntity}
	 */
	public CarrierEntity getDefaultRailCarrier() {
		// [[
		CarrierEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDefaultRailCarrier");
		}
		// ]]
		try {

			ReadOnlyDao<PoolCarrierCarrierEntity> poolCarrierDao = getDaoProvider().getPoolCarrierCarrierEntityDao();
			Filter<PoolCarrierCarrierEntity> poolCarrierFilter = poolCarrierDao.createFilter();

			List<Long> railPoolIDs = getRailPoolIDs();
			if (!railPoolIDs.isEmpty()) {
				poolCarrierFilter.addExpression(DbUtils.createSplittedInRestriction(
						PoolCarrierCarrierEntity.ATTR_POOL_ID, railPoolIDs));
			}

			poolCarrierFilter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_CARRIER_ID + "." + PoolCarrierCarrierEntity.ATTR_CARRIER_ACTIVE_FLAG, Boolean.TRUE));

			JPAQuery<PoolCarrierCarrierEntity> query = poolCarrierDao.createJPAQuery();
			query.setFilter(poolCarrierFilter);
			query.addSelect(SelectExpression.simpleSelect(PoolCarrierCarrierEntity.ATTR_CARRIER_ID));
			@SuppressWarnings("unchecked")
			List<PoolCarrierCarrierEntity> poolCarrierIDs = poolCarrierDao.find(query);
			if (poolCarrierIDs.isEmpty()) {
				return null;
			}
			
			ReadOnlyDao<CarrierEntity> carrierDao = getDaoProvider().getCarrierEntityDao();
			Filter<CarrierEntity> carrierFilter = carrierDao.createFilter();
			carrierFilter.addExpression(DbUtils.createSplittedInRestriction(CarrierEntity.ATTR_CARRIER_ID, poolCarrierIDs));
			carrierFilter.addExpression(Restrictions.eq(CarrierEntity.ATTR_TRANSMIT_TO_CARRIER_FLAG, Boolean.TRUE));
			
			List<CarrierEntity> resultCarriers = carrierDao.find(carrierFilter);
			
			if (resultCarriers.size() == 1) {
				return resultCarriers.get(0);
			}
			else {
				return null;
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	/**
	 * Returns the one and only pool carrier association for the carrier, if exists, and
	 * return null if there is none, or there is more. In case this association can be determined,
	 * then the pool is straightforward for this carrier.
	 */
	public PoolCarrierEntity getDefaultRailPoolCarrier(Long carrierId) {
		// [[
		PoolCarrierEntity result = null;
		Object[][] params = {{"carrierId", carrierId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDefaultRailPoolCarrier", params);
		}
		// ]]
		try {
			final ReadOnlyDao<PoolCarrierEntity> dao = getDaoProvider().getPoolCarrierEntityDao();
			final Filter<PoolCarrierEntity> filt = dao.createFilter();
			filt.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER_ID, carrierId));
			final List<PoolCarrierEntity> associations = getDaoProvider().getPoolCarrierEntityDao().find(filt);
			if ( associations == null || associations.isEmpty() || associations.size() > 1) {
				result = null;
			}
			else {
				result = associations.get(0);
			}
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
	
	public CarrierEntity getCarrier(Long carrierId) {
		// [[
		CarrierEntity result = null;
		Object[][] params = {{"carrierId", carrierId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCarrier", params);
		}
		// ]]
		try {
			
			if (carrierId == null) {
				
				return result = null;
			}
					
			return result = getDaoProvider().getCarrierEntityDao().findById(carrierId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Loads a ShiptoEntity for the given sapBpId
	 * @param sapBpId 
	 * @adnlog.exclude return
	 */
	public ShiptoEntity loadShipto(Long sapBpId) {
		// [[
		Object[][] params = {{"sapBpId", sapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShipto", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			final Filter<ShiptoEntity> filter = shiptoDao.createFilter();
			filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, sapBpId));
		
			return shiptoDao.findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Returns all materials.
	 * @return
	 */
	public List<RailTimetableEntity> fetchRailTimetableList(Long depStation, Long arrStation) {
		// [[
		List<RailTimetableEntity> result = null;
		Object[][] params = {{"depStation", depStation},
							{"arrStation", arrStation}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRailTimetableList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<RailTimetableEntity> dao = getDaoProvider().getRailTimetableEntityDao();
			final Filter<RailTimetableEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_DEP_RAILWAY_STATION_ID, depStation));
			filter.addExpression(Restrictions.eq(RailTimetableEntity.ATTR_ARR_RAILWAY_STATION_ID, arrStation));
			
			return result = dao.find(filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns all materials.
	 * @return
	 */
	public ListRange fetchMaterialList(int skip, int num, String materialPart, String langCd,
			String prodPlantCd) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"materialPart", materialPart},
							{"langCd", langCd},
							{"prodPlantCd", prodPlantCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMaterialList", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapMaterialPlantDescEntity> dao = getDaoProvider().getSapMaterialPlantDescEntityDao();
			final Filter<SapMaterialPlantDescEntity> filter = dao.createFilter();
			if (StringUtils.notEmpty(materialPart)) {
				filter.addExpression(Restrictions.ilike(SapMaterialPlantDescEntity.ATTR_TXT,
						'%' + materialPart + '%'));
			}
			filter.addExpression(Restrictions.eq(SapMaterialPlantDescEntity.ATTR_LANG_CD, langCd));
			filter.addExpression(Restrictions.eq(SapMaterialPlantDescEntity.ATTR_PLANT_CD,
					prodPlantCd));
			filter.addExpression(Restrictions.neq(SapMaterialPlantDescEntity.ATTR_BASE_UOM_CD, Constant.UOM_EMPTY_WAGON_27T));
			filter.addExpression(Restrictions.neq(SapMaterialPlantDescEntity.ATTR_BASE_UOM_CD, Constant.UOM_EMPTY_WAGON_54T));
			filter.addExpression(Order.ascCaseInsensitive(SapMaterialPlantDescEntity.ATTR_TXT));
			
			final List<SapMaterialPlantDescEntity> list = dao.findNRecs(skip, num, filter);
			ListRange listRange = new ListRange();
			listRange.setTotalSize(dao.count(filter));
			listRange.setData(list.toArray());
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns shipping points for a give plant.
	 * @return
	 */
	public ListRange fetchShippingpointList(int skip, int num, String plantCd) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"plantCd", plantCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShippingpointList", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
			final Filter<ShippingpointEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Restrictions.isNotNull(ShippingpointEntity.ATTR_RAILWAY_STATION));
			filter.addExpression(Order.ascCaseInsensitive(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));
			
			List<ShippingpointEntity> shPoints = dao.findNRecs(skip, num, filter);
			ListRange listRange = new ListRange();
			listRange.setTotalSize(dao.count(filter));
			listRange.setData(shPoints.toArray());
			
			return result = listRange;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ShippingpointEntity fetchStationSp(Long railwayStationId) {
		// [[
		ShippingpointEntity result = null;
		Object[][] params = {{"railwayStationId", railwayStationId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchStationSp", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
			final Filter<ShippingpointEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_RAILWAY_STATION_ID, railwayStationId));
			
			final List<ShippingpointEntity> shPoints = dao.findNRecs(0, 1, filter);
			
			if (shPoints.isEmpty()) {
				
				return result = null;
			}
			
			return result = shPoints.get(0);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns shipping point.
	 * @return
	 */
	public ShippingpointEntity fetchShippingpoint(Long ShippingpointId) {
		// [[
		ShippingpointEntity result = null;
		Object[][] params = {{"ShippingpointId", ShippingpointId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShippingpoint", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();
			final Filter<ShippingpointEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID, ShippingpointId));
			return result = dao.findSingle(filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns spa material.
	 * @return
	 */
	public SapMaterialEntity fetchSapMaterial(Long sapMaterialId) {
		// [[
		SapMaterialEntity result = null;
		Object[][] params = {{"sapMaterialId", sapMaterialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterial", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapMaterialEntity> dao = getDaoProvider().getSapMaterialEntityDao();
			final Filter<SapMaterialEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(SapMaterialEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			return result = dao.findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/** Returns current/previous load for the given wagon id.
	 * @param wagonId
	 * @param langCd
	 * @param currentFlag true if current, false if previous load
	 * @return
	 */
	public Map<String, Object> fetchCurrentLoad(Long wagonId, String langCd, boolean currentFlag) {
		// [[
		Map<String, Object> result = null;
		Object[][] params = {{"wagonId", wagonId},
							{"langCd", langCd},
							{"currentFlag", currentFlag + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCurrentLoad", params);
		}
		// ]]
		try {
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			ReadWriteDao<LoadEntity> loadDao = getDaoProvider().getLoadEntityDao();
			Filter<LoadEntity> loadFilter = loadDao.createFilter();
			loadFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON_ID, wagonId));
			loadFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_SAP_MATERIAL + "."
					+ SapMaterialEntity.ATTR_BASE_UOM + "." + UomBsEntity.ATTR_UOM_CD,
					Constant.UOM_TONS));
			loadFilter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
			if (currentFlag) {
				loadFilter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			}
			else {
				loadFilter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_UNLOADING_TS));
			}
			loadFilter.addExpression(Order.desc(LoadEntity.ATTR_LOADING_TS));
			
			List<LoadEntity> list = loadDao.find(loadFilter);
			
			if ((list != null) && (!list.isEmpty())) {
				LoadEntity load = list.get(0);
				map.put(LoadEntity.class.getName(), load);
				
				ReadWriteDao<SapMaterialDescriptionEntity> descDao =
						getDaoProvider().getSapMaterialDescriptionEntityDao();
				Filter<SapMaterialDescriptionEntity> descFilter = descDao.createFilter();
				descFilter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.sapMaterialId",
						load.getSapMaterial().getSapMaterialId()));
				descFilter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.langCd",
						langCd));
				SapMaterialDescriptionEntity desc = descDao.findSingle(descFilter);
				map.put(SapMaterialDescriptionEntity.class.getName(), desc);
			}
			
			return result = map;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns list of billtos for a given shipto sarea.
	 * @return
	 */
	public ListRange fetchBilltoList(int skip, int num, Long shiptoSapBpId, Long sareaId) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"shiptoSapBpId", shiptoSapBpId},
							{"sareaId", sareaId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchBilltoList", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			final Filter<ShiptoEntity> filter = shiptoDao.createFilter();
			filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
			ShiptoEntity shipto = shiptoDao.findSingle(filter);

			final List<SapBpEntity> list = new ArrayList<SapBpEntity>();
			if (shipto != null) {
				List<ShiptoBilltoEntity> shBilltos = businessPartnerStore.loadBilltos(shipto.getShiptoId(), sareaId);
				
//				ReadWriteDao<ShiptoSareaEntity> shiptosareaDao = getDaoProvider().getShiptoSareaEntityDao();
//				filter = new FilterImpl();
//				filter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO, shipto));
//				filter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SAREA_ID, sareaId));
//				ShiptoSareaEntity shiptoSarea = shiptosareaDao.findSingle(filter);
//			
//				if (shiptoSarea != null) {
//					shiptoSarea.initialize();
			
					for (ShiptoBilltoEntity shiptoBillto : shBilltos) {
						list.add(shiptoBillto.getBilltoSapBp());
					}
//				}
			}
			
			ListRange listRange = new ListRange();
			listRange.setTotalSize(list.size());
			listRange.setData(list.toArray());
			
			return result = listRange;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/** Returns wagon
	 * @param wagonId
	 * @return
	 */
	public WagonEntity loadWagon(Long wagonId) {
		// [[
		WagonEntity result = null;
		Object[][] params = {{"wagonId", wagonId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadWagon", params);
		}
		// ]]
		try {
			final ReadOnlyDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
			final Filter<WagonEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_ID, wagonId));
			
			WagonEntity wagon = dao.findSingle(filter);
			wagon.initialize();
			return result = wagon;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Loads a material description for a given materail NO
	 * @adnlog.exclude return
	 */
	public SapMatDescPackEntity loadMaterialDesc(String materialNo, String langCd) {
		// [[
		Object[][] params = {{"materialNo", materialNo},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMaterialDesc", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapMaterialEntity> dao = getDaoProvider().getSapMaterialEntityDao();
			final Filter<SapMaterialEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(SapMaterialEntity.ATTR_SAP_MATERIAL_NO, materialNo));
			
			final SapMaterialEntity sapMat = dao.findSingle(filter);			
			if (sapMat != null) {
				final ReadOnlyDao<SapMatDescPackEntity> descDao = getDaoProvider().getSapMatDescPackEntityDao();
				final Filter<SapMatDescPackEntity> descFilter = descDao.createFilter();
				
				descFilter.addExpression(Restrictions.eq(SapMatDescPackEntity.ATTR_SAP_MATERIAL_ID,
						sapMat.getSapMaterialId()));
				descFilter.addExpression(Restrictions.eq(SapMatDescPackEntity.ATTR_LANG_CD, langCd));
			
				return descDao.findSingle(descFilter);
			}
			return null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Service method to provide data for the Sold-to/Ship-to/Sarea paging combobox for special Order Taking
	 *   
	 * @param skip from pager
	 * @param num from pager
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @param custWithoutOrderToday if true, only customers without an order for the current day will be returned 
	 * @return List of SapSoldShipSareaSrchEntity entities matching the query
	 * @adnlog.exclude return
	 */
	public ListRange fetchSoldtoShiptoSareaList(int skip, int num, String query, boolean custWithoutOrderToday, Locale locale, Boolean isRepairMaintenanceDeliveryType) {
		// [[
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query},
							{"custWithoutOrderToday", custWithoutOrderToday + ""},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldtoShiptoSareaList", params);
		}
		// ]]
		try {
			
			final boolean isDummyQuery = query.startsWith("DMY");

			final ReadOnlyDao<SoldshipSareaFlexSrchEntity> searchDao = getDaoProvider().getSoldshipSareaFlexSrchEntityDao();
			final Filter<SoldshipSareaFlexSrchEntity> filter = searchDao.createFilter();
			
			if (!StringUtils.empty(query)) {
				if (isDummyQuery) {
					
					filter.addExpression(Restrictions.or(
							Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_NAME, query),
							Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_NAME, query),
							Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_POSTAL_CODE, query),
							Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_CITY, query),
							Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_NR, query),
							Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_NR, query)
						)
					);
					
				}
				else {
				    query = replaceWildcards(query);
	
					filter.addExpression(Restrictions.ilike(SoldshipSareaFlexSrchEntity.ATTR_COMPOSITE_SEARCH_STRING, query));
				}
			}
			if (isRepairMaintenanceDeliveryType) {
				filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_RAIL_RETURN_SAREA_FLAG, Boolean.TRUE));
				filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_TYPE_CD, Constant.SHIPPINGPOINT_TYPE_RAIL_REPAIR));
			}
			
			filter.addExpression(Restrictions.isNotNull(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_RAILWAY_STATION_ID));
			// --- Language filter (daily order status is the only lang dependent field) ---
			filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_DAILY_ORDER_STATUS_LANG_CD, locale.getLanguage()));
			// --- Customers without ordering today ---
			if (custWithoutOrderToday) {
				filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_DAILY_ORDER_CONTACT_FLAG, true));
				filter.addExpression(Order.asc(SoldshipSareaFlexSrchEntity.ATTR_DAILY_ORDER_STATUS_CD));
			}
			
			
			filter.addExpression(Order.asc(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_NAME));
			filter.addExpression(Order.asc(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_NAME));
			filter.addExpression(Order.asc(SoldshipSareaFlexSrchEntity.ATTR_SAREA_DISPLAY_LONGNAME));

			ListRange res = new ListRange();
			final List<SoldshipSareaFlexSrchEntity> shipTos = searchDao.findNRecs(skip, num, filter);
			final Long totalSize = searchDao.count(filter);
			
			// --- Adding Dummy-Shipto to the end of the list ---
			if (!custWithoutOrderToday && skip + num >= totalSize) {
			    SoldshipSareaFlexSrchEntity dummy = new SoldshipSareaFlexSrchEntity();
				dummy.setCompositeId("-1");
				shipTos.add(dummy);
			}
			
			res.setData(shipTos.toArray());
			res.setTotalSize(totalSize);
			
			return res;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private String replaceWildcards(String query) {
		// [[
		String result = null;
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "replaceWildcards", params);
		}
		// ]]
		try {
            query = query.replace("|", "||");
            query = query.replace("%", "|%");
            query = query.replace("_", "|_");
            /* now replace the query wildcard characlter '*'
             * by the JPAQL escape character*/
            query = query.replace('*', '%');
            // replace white spaces with wildcard 
            query = query.replaceAll("\\s", "%");
            /* now convert to uppercase */
            query = query.toUpperCase();
            /* finally add a wildcard character at the end and at the begining of the String*/
            query = '%' + query + '%';
            return result = query;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

	/**
 	 * Default production plant for empty materials. Move this method to a more generic service, if needed
 	 * somewhere else too, so that the plant_cd is only hard-coded once!
	 */
	public PlantBsEntity getDefaultServicePlant() {
		// [[
		LOG.debug("getDefaultServicePlant()");
		// ]]
		return referentialDataService.findBsEntity(PlantBsEntity.class, DEFAULT_SERVICE_PLANT_CD);
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ShippingPointServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.RailwayStationCommEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.LdsServerDropdownEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ShippingPointServiceBean extends ShippingPointServiceDispatcher implements ShippingPointServiceLocalInterface {

	@EJB private DeliverySettingServiceLocalInterface deliverySettingService;
	@EJB private RFIDServiceLocalInterface rfidService;
	@EJB private RefDataGenericsService refDataGenericsService;

	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointServiceBean.class);
	public ShippingpointEntity getShippingPointById(Long spId) {
		// [[
		ShippingpointEntity result = null;
		Object[][] params = {{"spId", spId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShippingPointById", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ShippingpointEntity> spDao = getDaoProvider().getShippingpointEntityDao();
			return result = spDao.findById(spId);
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
	 * Loads shipping points from TLGNC_SHIPPINGPOINT for given plant id. These are shown in shipping points dropdown on the UI.
	 * @param orderby String
	 * @param asc boolean
	 * @param plantId long 
	 * @adnlog.exclude return
	 */
	public List loadShippingPoints(String orderby, boolean asc, String plantId) {
		// [[
		Object[][] params = {{"orderby", orderby},
							{"asc", asc + ""},
							{"plantId", plantId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingPoints", params);
		}
		// ]]
		try {
			
			ReadWriteDao<ShippingpointEntity> spDao = getDaoProvider().getShippingpointEntityDao();
			
			Filter filter = new FilterImpl();
			if (StringUtils.notEmpty(plantId)) {
				filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT_CD, plantId));
			}
			
			if (StringUtils.notEmpty(orderby)) {
				if (asc) {
					filter.addExpression(Order.asc(orderby));
				}
				else {
					filter.addExpression(Order.desc(orderby));
				}
			}
			
			return spDao.find(filter);
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
	 * Loads shipping points from TLGNC_SHIPPINGPOINT for all the given plant ids. 
	 * These are shown in shipping points dropdown on the UI.
	 * @param orderby String
	 * @param asc boolean
	 * @param plantIds List<String> 
	 * @param roadOnly only load road type shippingpoints (road, container, bimodal truck)
	 * @adnlog.exclude return
	 */
	public List loadShippingPoints(String orderby, boolean asc, List plantIds, boolean roadOnly) {
		// [[
		List result = null;
		Object[][] params = {{"orderby", orderby},
							{"asc", asc + ""},
							{"plantIds", plantIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingPoints", params);
		}
		// ]]
		try {
			ReadWriteDao<ShippingpointEntity> spDao = getDaoProvider().getShippingpointEntityDao();
			
			Filter<ShippingpointEntity> filter = spDao.createFilter();
			if(plantIds != null && !plantIds.isEmpty()) {
				filter.addExpression(DbUtils.createSplittedInRestriction(ShippingpointEntity.ATTR_PLANT_CD, plantIds));
			}
			if (roadOnly) {
				final Long[] roadShippingpointTypes = { 
						Constant.SHIPPINGPOINT_TYPE_ROAD,
						Constant.SHIPPINGPOINT_TYPE_CONTAINER,
						Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK 
				};
				filter.addExpression(Restrictions.in(ShippingpointEntity.ATTR_SHIPPPOINT_TYPE_CD,
						roadShippingpointTypes));
			}
			
			if (StringUtils.notEmpty(orderby)) {
				if (asc) {
					filter.addExpression(Order.asc(orderby));
				}
				else {
					filter.addExpression(Order.desc(orderby));
				}
			}
			
			return result = spDao.find(filter);
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
	 * Loads shipping point from TLGNC_SHIPPINGPOINT for given shipping point id.
	 * @param spId shipping point id
	 * @adnlog.exclude return
	 */
	public ShippingpointEntity loadShippingPoint(long spId) {
		// [[
		Object[][] params = {{"spId", spId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingPoint", params);
		}
		// ]]
		try {
			
			ReadWriteDao<ShippingpointEntity> spDao = getDaoProvider().getShippingpointEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID, spId));
			return spDao.findSingle(filter);
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
	 * Loads materials from VLGNS_SAP_MATERIAL_PLANT_DESC for a given PLANT_CD.
	 * @param plantCd plant code
	 * @param langCd language code
	 * @adnlog.exclude return
	 */
	public List loadPlantMaterial(String plantCd, String langCd) {
		// [[
		Object[][] params = {{"plantCd", plantCd},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadPlantMaterial", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapMaterialPlantDescEntity> materialDao = getDaoProvider().getSapMaterialPlantDescEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(SapMaterialPlantDescEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Restrictions.eq(SapMaterialPlantDescEntity.ATTR_LANG_CD, langCd));
			filter.addExpression(Order.asc(SapMaterialPlantDescEntity.ATTR_TXT));
			
			return materialDao.find(filter); 
			
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
	 * Loads all available server names for a given config table infix
	 * @adnlog.exclude return
	 */
	public List<LdsServerDropdownEntity> loadLdsServers(String infix) {
		// [[
		Object[][] params = {{"infix", infix}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadLdsServers", params);
		}
		// ]]
		try {
			ReadOnlyDao<LdsServerDropdownEntity> ldsServerDao = getDaoProvider().getLdsServerDropdownEntityDao();
			Filter<LdsServerDropdownEntity> f = new FilterImpl<LdsServerDropdownEntity>();
			f.addExpression(Restrictions.eq(LdsServerDropdownEntity.ATTR_TYPE, infix));
			return ldsServerDao.find(f);
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
	 * Checks whether the lds server is required. If the current lds web service url has placeholders, it is required.
	 * @adnlog.exclude return
	 */
	public boolean isLdsServerRequired(String infix) {
		Object[][] params = {{"infix", infix}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isLdsServerRequired", params);
		}
		// ]]
		try {
			String value = getConfigBean().getProperty("wsclient." + infix + ".host.url");
			if (value != null) {
				return value.contains("${name}");
			}
			return false;
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
	 * Loads list of shipping points from TLGNC_LOADINGPOINT for given shipping point id.
	 * @param spId shipping point id
	 * @adnlog.exclude return
	 */
	public List loadLoadingPoint(long spId) {
		// [[
		Object[][] params = {{"spId", spId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadLoadingPoint", params);
		}
		// ]]
		try {
			ReadWriteDao<LoadingpointEntity> lpDao = getDaoProvider().getLoadingpointEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT_ID, spId));
			filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Order.asc(LoadingpointEntity.ATTR_LOADINGPOINT_NAME));
			
			List ls = lpDao.find(filter);
			for (int i = 0; i < ls.size(); i++) {
				LoadingpointEntity loadingpointEntity = (LoadingpointEntity) ls.get(i);
				loadingpointEntity.initialize();
				if (loadingpointEntity.getRfidobject() != null) {
					loadingpointEntity.getRfidobject().initialize();
				}
			}
			return ls;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public LoadingpointEntity getLoadingpoint(long lpId) {
		// [[
		LoadingpointEntity result = null;
		Object[][] params = {{"lpId", lpId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadingpoint", params);
		}
		// ]]
		try {
			ReadWriteDao<LoadingpointEntity> lpDao = getDaoProvider().getLoadingpointEntityDao();
			return result = lpDao.findById(lpId);
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
	 * Loads list of all available RFIDs
	 * @adnlog.exclude return
	 */
//	public List loadRFIDs() {
//		// [[
//		boolean logDebugEnabled = LOG.isDebugEnabled();
//		if (logDebugEnabled) {
//			LOG.entering(this, "loadRFIDs");
//		}
//		// ]]
//		try {
//			ReadWriteDao<RfidtagEntity> rfidDao = getDaoProvider().getRfidtagEntityDao();
//			
//			Filter filter = new FilterImpl();
//			filter.addExpression(Restrictions.isNull(RfidtagEntity.ATTR_RFIDOBJECT_ID));
//			// FIXME (valdas Feb 20, 2009): do we need to check active flag?
//			
//			return rfidDao.find(filter);
//		}
//		finally {
//		// [[
//			if (logDebugEnabled) {
//				LOG.exiting();
//			}
//		// ]]
//		}
//	}

	/**
	 * Loads list of all Railway Stations from TLGNC_RAILWAY_STATION.
	 * @adnlog.exclude return
	 */
	public List loadRailwayStationList() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadRailwayStationList");
		}
		// ]]
		try {
			ReadWriteDao<RailwayStationEntity> rsDao = getDaoProvider().getRailwayStationEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_ACTIVE_FLAG,
					Boolean.TRUE));
			filter.addExpression(Order.asc(RailwayStationEntity.ATTR_RAILWAY_STATION_NO));
			filter.addExpression(Order.asc(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME));
			List ls = rsDao.find(filter);
			
			return ls;
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
	 * Loads Railway Stations for a given from Railway Station Id
	 * @adnlog.exclude return
	 */
	public RailwayStationEntity loadRailwayStation(Long railwayStationId) {
		// [[
		Object[][] params = {{"railwayStationId", railwayStationId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadRailwayStation", params);
		}
		// ]]
		try {
			ReadWriteDao<RailwayStationEntity> rsDao = getDaoProvider().getRailwayStationEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_RAILWAY_STATION_ID, railwayStationId));
			return rsDao.findSingle(filter);
			
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
	 * Load the operational Railway Station for a given from Railway Station Id and the commercial railway station
	 * if exists (if not then the operational railway station will be the commercial one as well) 
	 * @adnlog.exclude return
	 */
	public RailwayStationEntity[] loadOpAndCommRailwayStations(Long railwayStationId) {
		// [[
		Object[][] params = {{"railwayStationId", railwayStationId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOpAndCommRailwayStations", params);
		}
		// ]]
		try {
			RailwayStationEntity[] result = null;
			
			ReadWriteDao<RailwayStationEntity> rsDao = getDaoProvider().getRailwayStationEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_RAILWAY_STATION_ID, railwayStationId));
			RailwayStationEntity opStation = rsDao.findSingle(filter);
			if ( opStation == null) {
				return result;
			}
			result = new RailwayStationEntity[2];
			result[0] = opStation;
			ReadWriteDao<RailwayStationCommEntity> commDao = getDaoProvider().getRailwayStationCommEntityDao();
			Filter commFilter = new FilterImpl();
			commFilter.addExpression(Restrictions.eq(RailwayStationCommEntity.ATTR_OP_RAILWAY_STATION_ID, railwayStationId));
			
			RailwayStationCommEntity commEntity = commDao.findSingle(commFilter);
			
			if ( commEntity == null || commEntity.getCommRailwayStation() == null) {
				result[1] = opStation;
				return result;
			}
			
			result[1] = commEntity.getCommRailwayStation();
			
			return result;
			
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
	 * Updates the given detached ShippingpointEntity in the DB.
	 *
	 * @param dto the detached ShippingpointEntity serving as a DTO object for passing the information to update
	 *
	 * @return the updated ShippingpointEntity instance (note that this instance is not the same
	 * 		   as the passed in <em>dto</em>
	 */
	public ShippingpointEntity updateShippingPoint(ShippingpointEntity dto) {
		// [[
		ShippingpointEntity result = null;
		Object[][] params = {{"dto", dto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateShippingPoint", params);
		}
		// ]]
		try {
			ReadWriteDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();

			//--- Explicitly update any child references for merge ---//
			if (dto.getRailwayStation() != null) {
				RailwayStationEntity rse = loadRailwayStation(dto.getRailwayStation().getRailwayStationId());
				dto.setRailwayStation(rse);
			}

			//--- Reattach Shippingpoint instance to session and flush out new changes ---//
			result = dao.merge(dto);

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

	/**
	 * Updates the DB with the list of detached LoadingPointEntity objects.
	 * <p/>
	 * IMPLEMENTATION NOTE: The individual DTO instances in the list have been populated with
	 * the in ShippingPointAction.unwrapLoadingPointList() which already loads and sets some
	 * of the child references (e.g. Printer, DeliveryNoteType, etc). However, the list of
	 * MaterialRestrictions and the RFID objects are not setup.. I would have moved the code there,
	 * but the code was already here (as well as my initial "fix"), so I just merged my code into
	 * this implementation (trying to minimize the number of points in the code which have to be changed).
	 *
	 * As for the setup of the MaterialRestrictions, in the case of an update, the restrictions
	 * need to be set on the incoming LoadingpointEntity DTO object. The reason is that we
	 * use DAO.merge to copy the DTO into a managed entity. The incoming DTO already has
	 * everything else set (yes, even though the Printer and DeliveryNoteType entities are
	 * detached, they will be loaded back up on merge - it is just the collection of detached
	 * objects where problems start to arise, in particular, the SapMaterial instances have to
	 * be loaded already for merge to work).
	 *
	 * For the RFID information, we always set this up first before either persisting or
	 * merging.
	 *
	 * @param dtos a List<LoadingpointEntity> instance containing detached objects
	 *
	 * @return a List<LoadingpointEntity> containing the updated LoadingPointEntity objects
	 */
	public List<LoadingpointEntity> updateLoadingPoints(List<LoadingpointEntity> dtos) {
		// [[
		List<LoadingpointEntity> result = null;
		Object[][] params = {{"dtos", dtos}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateLoadingPoints", params);
		}
		// ]]
		try {
			result = new ArrayList<LoadingpointEntity>();

			ReadWriteDao<LoadingpointEntity> dao = getDaoProvider().getLoadingpointEntityDao();

			for (LoadingpointEntity dto : dtos) {

				if (dto.getLoadingpointId() == null || dto.getLoadingpointId() == 0L) {
					// (valdas Feb 24, 2009): It should always be null! Struts resets it to 0.
					dto.setLoadingpointId(null);
				}

				//--- Setup RFID information ---//
				RFIDBean.AbstractRFIDBean bean = RFIDBean.LoadingpointRFIDBean(dto);
				RfidobjectEntity rfObj = (dto.getLoadingpointId() == null) ?
						rfidService.validateAndPersistRFIDObject(Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO, bean) :
						rfidService.mergeRfIdObject(bean, Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);

				dto.setRfidobject(rfObj);

				//--- Create new entity or re-attach old entity to session ---//
				LoadingpointEntity entity;
				if (dto.getLoadingpointId() == null) {

					for (LdpointMatRestrEntity matRest : dto.getMatRestrictions()) {

						SapMaterialEntity sapMaterial = matRest.getSapMaterial();
						Long sapMaterialId = sapMaterial.getSapMaterialId();

						// If the restriction constellation does not exist in the list from DB it means we did not have such before
						// SapMaterialEntity could be cached (by ID) for better performance but i am not doing now as we are before release
						SapMaterialEntity foundMaterial = deliverySettingService.getSapMaterialById(sapMaterialId);
						matRest.setSapMaterial(foundMaterial);

					}

					entity = dao.persist(dto);

				}
				else {

					if (logDebugEnabled) {
						LOG.debug("Updating Loading Point started with ID=[" + dto.getLoadingpointId() + "]");
					}

					// setup material restrictions back on DTO with valid SAP material references
					ReadWriteDao<LdpointMatRestrEntity> matRestDao = getDaoProvider().getLdpointMatRestrEntityDao();

					// Restrictions are coming here with ID field cleared (i.e. null). So in order to avoid non-unique
					// exceptions which would happen if we try to push the same loadingpoint-sapmaterial constellation
					// we look up the restrictions from the DB and merge those also existing in the new setup coming from
					// caller (i.e. UI)
					Filter<LdpointMatRestrEntity> matRestFilter = new FilterImpl<LdpointMatRestrEntity>();
					matRestFilter.addExpression(Restrictions.eq(LdpointMatRestrEntity.ATTR_LOADINGPOINT, dto));
					List<LdpointMatRestrEntity> loadingPointMatRestList = matRestDao.find(matRestFilter);

					List<LdpointMatRestrEntity> newRestrications = new ArrayList<LdpointMatRestrEntity>();

					for (LdpointMatRestrEntity matRest : dto.getMatRestrictions()) {

						SapMaterialEntity sapMaterial = matRest.getSapMaterial();
						Long sapMaterialId = sapMaterial.getSapMaterialId();

						// If the restriction constellation does not exist in the list from DB it means we did not have such before
						LdpointMatRestrEntity foundMatRest = findMatRestInLoadingPointMatRestList(loadingPointMatRestList, dto.getLoadingpointId(), sapMaterialId);
						if (foundMatRest == null) {
							// SapMaterialEntity could be cached (by ID) for better performance but i am not doing now as we are before release
							SapMaterialEntity foundMaterial = deliverySettingService.getSapMaterialById(sapMaterialId);
							matRest.setSapMaterial(foundMaterial);
						}
						// If exists in the list no change
						else {
							matRest = foundMatRest;
						}

						// Only entries coming from the UI will be contained in the new list so deletion also works
						newRestrications.add(matRest);

					}

					if (logDebugEnabled) {
						LOG.debug("Updating Loading Point finished with ID=[" + dto.getLoadingpointId() + "]");
					}

					dto.setMatRestrictions(newRestrications);
					entity = dao.merge(dto);

				}

				result.add(entity);
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

	private LdpointMatRestrEntity findMatRestInLoadingPointMatRestList(List<LdpointMatRestrEntity> loadingPointMatRestList, Long loadingPointId, Long sapMaterialId) {
		for (LdpointMatRestrEntity matRestrEntity : loadingPointMatRestList) {
			if (matRestrEntity.getLoadingpoint().getLoadingpointId().equals(loadingPointId) && matRestrEntity.getSapMaterial().getSapMaterialId().equals(sapMaterialId)) {
				return matRestrEntity;
			}
		}
		return null;
	}

	/*
	 * Helper method used by updateLoadingPoints to add the set of MaterialRestrictions to the
	 * given entity. There is 1 pre-requisite here and that is that the given entity has been
	 * cleared of its MaterialRestrictions.
	 *
	 * This method will only add the new restrictions to the entity's list. The removal of
	 * restrictions happens indirectly when the object is updated in the DB because our
	 * LoadingpointEntity has CascadeType.DELETE_ORPHAN set on the 'materialRestrictions'.
	 */
	private void updateMaterialRestrictionsOnLoadingPoint(LoadingpointEntity entity,
														  List<LdpointMatRestrEntity> dtoRestrictions) {
		//--- We batch the loading of SAP Materials ---//
		Set<Long> materialIds = new HashSet<Long>();
		for (LdpointMatRestrEntity restriction : dtoRestrictions) {
			SapMaterialEntity material = restriction.getSapMaterial();
			materialIds.add(material.getSapMaterialId());
		}

		//--- Create any new material restrictions ---//
		if (!materialIds.isEmpty()) {
			List<SapMaterialEntity> materials = loadMaterials(new ArrayList<Long>(materialIds));
			for (SapMaterialEntity material : materials) {
				LdpointMatRestrEntity restriction = new LdpointMatRestrEntity();
				restriction.setLoadingpoint(entity);
				restriction.setSapMaterial(material);
				entity.getMatRestrictions().add(restriction);
			}
		}
	}

	private List<SapMaterialEntity> loadMaterials(List<Long> materialIds) {
		if (materialIds.size() == 1) {
			return Arrays.asList(loadSapMaterial(materialIds.get(0)));
		}
		else {
			ReadOnlyDao<SapMaterialEntity> dao = getDaoProvider().getSapMaterialEntityDao();
			Filter f = dao.createFilter();
			f.addExpression(Restrictions.in(SapMaterialEntity.ATTR_SAP_MATERIAL_ID, materialIds));

			return dao.find(f);
		}
	}

	public LoadingpointEntity checkLoadingpointNameUnicity(ShippingpointEntity spEntity, List loadingpoints) {
		LoadingpointEntity result = null;
		Iterator it = loadingpoints.iterator();
		List names = new ArrayList();
		while (it.hasNext()) {
			Filter filt = new FilterImpl();
			LoadingpointEntity lp = (LoadingpointEntity) it.next();
			if (names.contains(lp.getLoadingpointName())){
				result = lp;
				break;
			}
			else{
				names.add(lp.getLoadingpointName());
			}
		}
		return result;
	}
	
	/**
	 * If the LP is _active_, check if another _active_ LP exists at the same lds server
	 * (sp.ldsServer) with identical weigh_system_loadingpoint_no, and display an error if there is a duplicate.
	 * Inactive LPs may have the same lds server.
	 * The weigh_system_loadingpoint_no must be unique in the context of the type+lds server (if exists).
	 * There is no unique constraint since there can be more inactive loadingpoints, and the lds server may be null.
	 * @param spEntity
	 * @param loadingpoints
	 * @return {@link LoadingpointEntity} which is the first colliding
	 */
	public List<LoadingpointEntity> getCollidingLoadingpoints(ShippingpointEntity spEntity, List<LoadingpointEntity> loadingpoints) {
		// [[
		List<LoadingpointEntity> result = null;
		Object[][] params = {{"spEntity", spEntity},
							{"loadingpoints", loadingpoints}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCollidingLoadingpoints", params);
		}
		// ]]
		try {
			if ( loadingpoints == null || loadingpoints.isEmpty() ) {
				//no need to check anything
				return result = null;
			}
			
			SortedSet<String> weighSystemLoadingpointNoSet = new TreeSet<String>();
			List<LoadingpointEntity> collidingLps = new ArrayList<LoadingpointEntity>();
			List<Expression> orRestrictions = new ArrayList<Expression>();
			
			for (LoadingpointEntity lp : loadingpoints) {
				if (lp.getActiveFlag().equals(Boolean.FALSE)) {
					continue;	
				}
				if (StringUtils.empty(lp.getWeighSystemLoadingpointNo())){
					continue;
				}
				if (!lp.getWeighSystemLp().getWeighSystemLpCd().equals(Constant.WEIGH_SYSTEM_LP_WS)) {
					continue;
				}
				if (weighSystemLoadingpointNoSet.contains(lp.getWeighSystemLoadingpointNo())) {
					collidingLps.add(lp);
				}
				weighSystemLoadingpointNoSet.add(lp.getWeighSystemLoadingpointNo());
				orRestrictions.add(Restrictions.eq(LoadingpointEntity.ATTR_WEIGH_SYSTEM_LOADINGPOINT_NO, 
						lp.getWeighSystemLoadingpointNo()));
			}
			
			if (weighSystemLoadingpointNoSet.isEmpty()) {
				//no need to check weighing system no uniqueness, since there is no loading point which have weigh system loading point no and is active
				return result = collidingLps;
			}
			
			// no check for uniqueness within the same plant
			ReadWriteDao<LoadingpointEntity> dao = getDaoProvider().getLoadingpointEntityDao();
			Filter<LoadingpointEntity> f = dao.createFilter();

			f.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			f.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_WEIGH_SYSTEM_LP_CD, Constant.WEIGH_SYSTEM_LP_WS));
			// the ones from the same shipping point have already been checked above
			f.addExpression(Restrictions.neq(LoadingpointEntity.ATTR_SHIPPINGPOINT + "." + ShippingpointEntity.ATTR_SHIPPINGPOINT_ID, spEntity.getShippingpointId()));
			f.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT + "." + ShippingpointEntity.ATTR_PLANT_CD, spEntity.getPlant().getPlantCd()));
			f.addExpression(Restrictions.or(orRestrictions));
			
			List<LoadingpointEntity> lps = dao.find(f);
			collidingLps.addAll(lps);
			
			return result = collidingLps;
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
	 * Loads list of all available Railway Stations
	 * @adnlog.exclude return
	 */
	public List loadRailwayStations(int skip, int num, String orderBy, String query) {
		// [[
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"orderBy", orderBy},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadRailwayStations", params);
		}
		// ]]
		try {
			ReadWriteDao<RailwayStationEntity> rsDao = getDaoProvider().getRailwayStationEntityDao();
			
			Filter filter = new FilterImpl();
			if ( StringUtils.notEmpty( query )) {
				try {
					Long.parseLong(query);
					filter.addExpression(Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, query + '%'));
				}
				
				catch (NumberFormatException ex) {
					filter.addExpression(Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME, query + '%'));
				}
			}
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_ACTIVE_FLAG, true));
			filter.addExpression(Order.asc(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME));
			
			return rsDao.findNRecs(skip, num, filter);
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
	 * counts available Railway Stations
	 * @adnlog.exclude return
	 */
	public Long countRailwayStations(String query) {
		// [[
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countRailwayStations", params);
		}
		// ]]
		try {
			ReadWriteDao<RailwayStationEntity> rsDao = getDaoProvider().getRailwayStationEntityDao();
			
			Filter filter = new FilterImpl();
			if ( StringUtils.notEmpty( query )) {
				try {
					Long.parseLong(query);
					filter.addExpression(Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, query + '%'));
				}
				
				catch (NumberFormatException ex) {
					filter.addExpression(Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME, query + '%'));
				}
			}
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_ACTIVE_FLAG, true));
			
			return rsDao.count(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public Map<Long,RailwayStationEntity> findRailwayStationsForShippingPoints(Set<Long> shippintPointIds){
		// [[
		Map<Long,RailwayStationEntity> result = null;
		Object[][] params = {{"shippintPointIds", shippintPointIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findRailwayStationsForShippingPoints", params);
		}
		// ]]
		try {

			if (shippintPointIds == null || shippintPointIds.isEmpty()) {
				return Collections.<Long, RailwayStationEntity>emptyMap();
			}

			Map<Long,RailwayStationEntity> stations = new HashMap<Long, RailwayStationEntity>();
			
			DaoProvider daoProvider = getDaoProvider();
			
			ReadWriteDao<ShippingpointEntity> shippingpointDao = daoProvider.getShippingpointEntityDao();
			
			List<Long> shippintPointIdList = new ArrayList<Long>(shippintPointIds);
			
			Filter<ShippingpointEntity> filter = new FilterImpl<ShippingpointEntity>();
			filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(ShippingpointEntity.ATTR_SHIPPINGPOINT_ID, shippintPointIdList));
			
			List<ShippingpointEntity> shippingPoints = shippingpointDao.find(filter);
			for (ShippingpointEntity shippingpointEntity : shippingPoints) {
				RailwayStationEntity railwayStation = shippingpointEntity.getRailwayStation();
				if (railwayStation != null) {
					stations.put(shippingpointEntity.getShippingpointId(), railwayStation);
				}
			}
			
			return result = stations;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public long countShippingpoint(String sapShippingpointNo) {
			// [[
			long result = 0L;
			Object[][] params = {{"sapShippingpointNo", sapShippingpointNo}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "countShippingpoint", params);
			}
			// ]]
			try {
				ReadWriteDao<ShippingpointEntity> shippingpointDao = getDaoProvider().getShippingpointEntityDao();
				Filter<ShippingpointEntity> f = new FilterImpl<ShippingpointEntity>();
				f.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_SAP_SHIPPINGPOINT_NO, sapShippingpointNo));
				return result = shippingpointDao.count(f);				
			}
			finally {
			// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
				}
			// ]]
			}
	}

	public ShippingpointEntity createShippingpoint(ShippingpointEntity shippingpoint) {
		// [[
		ShippingpointEntity result = null;
		Object[][] params = {{"shippingpoint", shippingpoint}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createShippingpoint", params);
		}
		// ]]
		try {
			if (shippingpoint.getTimezone() == null) {
				shippingpoint.setTimezone(refDataGenericsService.findBsEntity(TimezoneBsEntity.class, 
						Constant.TIMEZONE_EUROPE_PARIS));
			}
			
			ReadWriteDao<ShippingpointEntity> shippingpointDao = getDaoProvider().getShippingpointEntityDao();
			return result = shippingpointDao.persist(shippingpoint);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public MatShippingpointEntity loadMatShippingpoint(long matShippingpointId) {
		// [[
		MatShippingpointEntity result = null;
		Object[][] params = {{"matShippingpointId", matShippingpointId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMatShippingpoint", params);
		}
		// ]]
		try {
			ReadWriteDao<MatShippingpointEntity> matShippingpointDao = getDaoProvider().getMatShippingpointEntityDao();
			return result = matShippingpointDao.findById(matShippingpointId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public MatShippingpointEntity loadMatShippingpoint(long shippingpointId, long sapMaterialId) {
		// [[
		MatShippingpointEntity result = null;
		Object[][] params = {{"shippingpointId", shippingpointId + ""},
							{"sapMaterialId", sapMaterialId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMatShippingpoint", params);
		}
		// ]]
		try {
			ReadWriteDao<MatShippingpointEntity> matShippingpointDao = getDaoProvider().getMatShippingpointEntityDao();
			
			Filter<MatShippingpointEntity> filter = new FilterImpl<MatShippingpointEntity>();
			filter.addExpression(Restrictions.eq(MatShippingpointEntity.ATTR_SHIPPINGPOINT_ID, shippingpointId));
			filter.addExpression(Restrictions.eq(MatShippingpointEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			return result = matShippingpointDao.findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public MatShippingpointEntity createMatShippingpoint(MatShippingpointEntity matShippingpoint) {
		// [[
		MatShippingpointEntity result = null;
		Object[][] params = {{"matShippingpoint", matShippingpoint}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createMatShippingpoint", params);
		}
		// ]]
		try {
			ReadWriteDao<MatShippingpointEntity> matShippingpointDao = getDaoProvider().getMatShippingpointEntityDao();
			return result = matShippingpointDao.persist(matShippingpoint);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public MatShippingpointEntity updateMatShippingpoint(MatShippingpointEntity matShippingpoint) {
		// [[
		MatShippingpointEntity result = null;
		Object[][] params = {{"matShippingpoint", matShippingpoint}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateMatShippingpoint", params);
		}
		// ]]
		try {
			ReadWriteDao<MatShippingpointEntity> matShippingpointDao = getDaoProvider().getMatShippingpointEntityDao();
			return result = matShippingpointDao.merge(matShippingpoint);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public void deleteMatShippingpoint(long matShippingpointId) {
		// [[
		Object[][] params = {{"matShippingpointId", matShippingpointId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteMatShippingpoint", params);
		}
		// ]]
		try {
			MatShippingpointEntity matShippingpoint = loadMatShippingpoint(matShippingpointId);
			ReadWriteDao<MatShippingpointEntity> matShippingpointDao = getDaoProvider().getMatShippingpointEntityDao();
			matShippingpointDao.remove(matShippingpoint);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public SapMaterialEntity loadSapMaterial(Long sapMaterialId) {
		// [[
		SapMaterialEntity result = null;
		Object[][] params = {{"sapMaterialId", sapMaterialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadSapMaterial", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapMaterialEntity> sapMaterialEntityDao = getDaoProvider().getSapMaterialEntityDao();
			return result = sapMaterialEntityDao.findById(sapMaterialId);
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
	 * The function will update the TruckRoute table according to the input.
	 * If the shippingpoint coordinates are changed by the customer,
	 * it will remove the existing TruckrouteEntities ( which are belonging
	 * to the shippingpoint).
	 * 
	 * @param updatedShippingPoint The shippingPoint which contains the new coordinates ( lat, lng )
	 * */
	public void updateTruckRoutesToShippingPointCoords(final ShippingpointEntity updatedShippingPoint) {
		// [[
		Object[][] params = {{"updatedShippingPoint", updatedShippingPoint}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTruckRoutesToShippingPointCoords", params);
		}
		// ]]
		try {
			ReadOnlyDao<ShippingpointEntity> shippingPointDao = getDaoProvider().getShippingpointEntityDao();
			ShippingpointEntity storedShippingPoint = shippingPointDao.findById(updatedShippingPoint.getShippingpointId());
			
			//There is no existing shippingpoint, thus there is no existing truckroute
			if(null == storedShippingPoint) {
				return;
			}
			
			if( null != storedShippingPoint.getCoordLat() && null != storedShippingPoint.getCoordLon()
					&& null != updatedShippingPoint.getCoordLat() && null != updatedShippingPoint.getCoordLon()) {
				
				double origCoordLat = storedShippingPoint.getCoordLat();
				double origCoordLng = storedShippingPoint.getCoordLon();
			
				double newCoordLat = updatedShippingPoint.getCoordLat();
				double newCoordLng = updatedShippingPoint.getCoordLon();
			
				if ( (origCoordLat != newCoordLat) || (origCoordLng != newCoordLng) ) {
					removeTruckRouteEntitiesForShippingPoint(storedShippingPoint.getShippingpointId());
				}
			}
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
	 * Remove the corresponding TruckRouteEntities for the
	 * input shippingpoint
	 * 
	 * @param shippingPointId The id (key) of the shippingpoint.
	 * 
	 * */
	public void removeTruckRouteEntitiesForShippingPoint(Long shippingPointId) {
		// [[
		Object[][] params = {{"shippingPointId",shippingPointId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeTruckRouteEntitiesForShippingPoint", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckRouteEntity> truckRouteDao = getDaoProvider().getTruckRouteEntityDao();
			Filter<TruckRouteEntity> filter = new FilterImpl<TruckRouteEntity>();
			
			filter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_START_SHIPPINGPOINT_ID, shippingPointId));
			List<TruckRouteEntity> removeableTruckRoutes = truckRouteDao.find(filter);
			
			if(!removeableTruckRoutes.isEmpty()) {
				for(TruckRouteEntity truckRoute : removeableTruckRoutes) {
					truckRouteDao.remove(truckRoute);
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}	
	}
	
}
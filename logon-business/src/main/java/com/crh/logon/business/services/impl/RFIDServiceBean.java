/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.rfidadministration.parameters.RFIDDataCache;
import com.crh.logon.business.services.dispatchers.RFIDServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.CronstatusServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.RfidTagOperation;
import com.crh.logon.persistence.entities.RfidTagComparator;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.views.RfidobjectSiloAttachEntity;
import com.crh.logon.persistence.entities.views.RfidtagDetailEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * This serviceBean is supposed to do be responsible for operations related to RFIDObjects, and RFidtag
 * objects.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class RFIDServiceBean extends RFIDServiceDispatcher implements RFIDServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RFIDServiceBean.class);
	
	@EJB private RFIDServiceLocalInterface rfidService;
	@EJB private CronstatusServiceLocalInterface cronstatusService;
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;

	public static final RfidobjectEntity NOT_ASSIGNED_TO_RFID_OBJECT = null;
	
	/**
	 * Loads list of all available RFIDs
	 * @param rfidTagsToOmit list of rfidtag not to include into result list
	 * @adnlog.exclude return
	 */
	public List<RfidtagEntity> loadRFIDs(int numberOfRowsToSkip, int numberOfRowsToReturn,
			String query, Set<RfidtagEntity> rfidTagsToOmit) {
		final ReadOnlyDao<RfidtagEntity> dao = getDaoProvider().getRfidtagEntityDao();
		final Filter<RfidtagEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.isNull(RfidtagEntity.ATTR_RFIDOBJECT_ID));
		filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_ACTIVE_FLAG, true));
		filter.addExpression(Order.asc(RfidtagEntity.ATTR_NAME));

		if ((rfidTagsToOmit != null) && (!rfidTagsToOmit.isEmpty())) {
			final List<Long> rfidTagIdentifiers = new ArrayList<Long>();
			for (RfidtagEntity currentRfidTag : rfidTagsToOmit) {
				Long currentRfidTagIdentifier = currentRfidTag.getRfidtagId();
				rfidTagIdentifiers.add(currentRfidTagIdentifier);
			}
			filter.addExpression(Restrictions.not(Restrictions.in(RfidtagEntity.ATTR_RFIDTAG_ID,
					rfidTagIdentifiers)));
		}
		filter.addExpression(Restrictions.like(RfidtagEntity.ATTR_NAME, query + "%"));
		return dao.findNRecs(numberOfRowsToSkip, numberOfRowsToReturn, filter);
	}
	
	
	/**
	 * Counts all available RFIDs
	 * @param rfidTagsToExclude list of rfidtag not to include into result list
	 * @adnlog.exclude return
	 */
	public long countRFIDs(String query, Set<RfidtagEntity> rfidTagsToExclude) {
		// [[
		Object[][] params = {{"query", query},
							{"rfidTagsToExclude", rfidTagsToExclude}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countRFIDs", params);
		}
		// ]]
		try {
			final ReadOnlyDao<RfidtagEntity> dao = getDaoProvider().getRfidtagEntityDao();			
			final Filter<RfidtagEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.isNull(RfidtagEntity.ATTR_RFIDOBJECT_ID));
			filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_ACTIVE_FLAG, true));
			if ((rfidTagsToExclude != null) && (!rfidTagsToExclude.isEmpty())) { 
				final List<Long> rfidtagIdList = new ArrayList<Long>();
				for (RfidtagEntity rfidtag : rfidTagsToExclude) {
					rfidtagIdList.add(rfidtag.getRfidtagId());
				}
				filter.addExpression(Restrictions.not(Restrictions.in(RfidtagEntity.ATTR_RFIDTAG_ID, rfidtagIdList)));
			}
			filter.addExpression(Restrictions.like(RfidtagEntity.ATTR_NAME, query + "%"));
			
			return dao.count(filter);
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
	 * Loads list of all available RFIDs
	 * @adnlog.exclude return
	 */
	public List<RfidtagEntity> loadRFIDs() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadRFIDs");
		}
		// ]]
		try {
			
			final ReadOnlyDao<RfidtagEntity> dao = getDaoProvider().getRfidtagEntityDao();			
			final Filter<RfidtagEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.isNull(RfidtagEntity.ATTR_RFIDOBJECT_ID));
			filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_ACTIVE_FLAG, true));
			return dao.find(filter);
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
	 * Loads list of all RFID tags and the corresponding RFID objects details, 
	 * if there is any corresponding
	 */
	public List<RfidtagDetailEntity> fetchRfidDetailList(Long rfidObjectType, String identity, String name, String objectLabel, Boolean active, String lang) {
		// [[
		Object[][] params = {{"rfidObjectType", rfidObjectType},
							{"identity", identity},
							{"name", name},
							{"objectLabel", objectLabel},
							{"active", active},
							{"lang", lang}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRfidDetailList", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<RfidtagDetailEntity> dao = getDaoProvider().getRfidtagDetailEntityDao();			
			final Filter<RfidtagDetailEntity> filter = dao.createFilter();
			
			if (rfidObjectType != null) {
				filter.addExpression(Restrictions.eq(RfidtagDetailEntity.ATTR_RFIDOBJECT_TYPE_CD, rfidObjectType));
			}
			if (! StringUtils.empty(identity)) {
				filter.addExpression(Restrictions.ilike(RfidtagDetailEntity.ATTR_RFID_IDENTITY,  '%' + identity + '%'));
			}
			if (! StringUtils.empty(name)) {
				filter.addExpression(Restrictions.ilike(RfidtagDetailEntity.ATTR_NAME,  '%' + name + '%'));
			}
			if (! StringUtils.empty(objectLabel)) {
				filter.addExpression(Restrictions.ilike(RfidtagDetailEntity.ATTR_OBJECT_LABEL,  '%' + objectLabel + '%'));
			}
			if (active != null) {
				filter.addExpression(Restrictions.eq(RfidtagDetailEntity.ATTR_ACTIVE_FLAG,  active));
			}
			
			/////////
			// TODO (istvan Jun 9, 2009): add type-dependent filtering here
			
			
			filter.addExpression(Restrictions.or(
					Restrictions.eq(RfidtagDetailEntity.ATTR_RFID_OBJECT_TYPE_LANG_CD, lang),
					Restrictions.isNull(RfidtagDetailEntity.ATTR_RFID_OBJECT_TYPE_LANG_CD)
			)
			);
			return dao.find(filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public ListRange fetchRfidDetailListForAjax(int skip, int num, String sort, boolean asc, HashMap<String, String> searchCrit, Locale locale) {
		// [[
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRfidDetailListForAjax", params);
		}
		// ]]
		try {
			
			final String identity = searchCrit.get(RfidtagDetailEntity.ATTR_RFID_IDENTITY);
			final String name = searchCrit.get(RfidtagDetailEntity.ATTR_NAME);
			final String objectLabel = searchCrit.get(RfidtagDetailEntity.ATTR_OBJECT_LABEL);
			final String lang = locale.getLanguage();
						
			final ReadOnlyDao<RfidtagDetailEntity> dao = getDaoProvider().getRfidtagDetailEntityDao();
			final Filter<RfidtagDetailEntity> filter = dao.createFilter();
			
			if (searchCrit.containsKey(RfidtagDetailEntity.ATTR_RFIDOBJECT_TYPE_CD)) {
				final Long rfidObjectType = Long.valueOf(searchCrit.get(RfidtagDetailEntity.ATTR_RFIDOBJECT_TYPE_CD));
				if (-1 != rfidObjectType) {
					filter.addExpression(Restrictions.eq(RfidtagDetailEntity.ATTR_RFIDOBJECT_TYPE_CD, rfidObjectType));
				}
			}
			if (!StringUtils.empty(identity)) {
				filter.addExpression(Restrictions.ilike(RfidtagDetailEntity.ATTR_RFID_IDENTITY,  '%' + identity + '%'));
			}
			if (!StringUtils.empty(name)) {
				filter.addExpression(Restrictions.ilike(RfidtagDetailEntity.ATTR_NAME,  '%' + name + '%'));
			}
			
			if (!StringUtils.empty(objectLabel)) {
				filter.addExpression(Restrictions.ilike(RfidtagDetailEntity.ATTR_OBJECT_LABEL,  '%' + objectLabel + '%'));
			}
			
			filter.addExpression(Restrictions.or(
					Restrictions.eq(RfidtagDetailEntity.ATTR_RFID_OBJECT_TYPE_LANG_CD, lang),
					Restrictions.isNull(RfidtagDetailEntity.ATTR_RFID_OBJECT_TYPE_LANG_CD)
			)
			);
			// TODO (istvan Jun 9, 2009): active
			
			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			
			ListRange rfidTagDetailEntities = new ListRange();
			rfidTagDetailEntities.setData(dao.findNRecs(skip, num, filter).toArray());
			rfidTagDetailEntities.setTotalSize(dao.count(filter));
			
			return rfidTagDetailEntities;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	
	public RfidtagEntity fetchRFIDTagById(Long rfidTagId) {
		// [[
		RfidtagEntity result = null;
		Object[][] params = {{"rfidTagId", rfidTagId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRFIDTagById", params);
		}
		// ]]
		try {
			final ReadOnlyDao<RfidtagEntity> rfidtagEntityDao = getDaoProvider().getRfidtagEntityDao();
			final RfidtagEntity rfidTag = rfidtagEntityDao.findById(rfidTagId);
			return result = rfidTag;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public RFIDBean.AbstractRFIDBean initSiloSelection(int index, ShiptoEntity shipTo) {
		List<ShiptoSiloEntity> silos = shipTo.getSilos();
		ShiptoSiloEntity selectedSilo = silos.get(index);
		RfidobjectEntity selectedRfidObject = null;
		selectedRfidObject = selectedSilo.getRfidobject();
		if (selectedRfidObject == null) {
			selectedRfidObject = new RfidobjectEntity();
			RfidobjectTypeBsEntity rfidObjectTypeShiptoSilo = referentialDataService.findBsEntity(
					RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_SHIPTO_SILO);
			selectedRfidObject.setRfidobjectType(rfidObjectTypeShiptoSilo);
		}
		selectedSilo.setRfidobject(selectedRfidObject);
		RFIDBean.AbstractRFIDBean siloSelection = RFIDBean.ShiptoSiloRFIDBean(selectedSilo);
		return siloSelection;
	}

	public RfidtagEntity persistRFIDTag(RfidtagEntity newRFIDTag) {
		// [[
		RfidtagEntity result = null;
		Object[][] params = {{"newRFIDTag", newRFIDTag}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "persistRFIDTag", params);
		}
		// ]]
		try {
			final DaoProvider daoProvider = getDaoProvider();
			final ReadWriteDao<RfidtagEntity> rfidtagEntityDao = daoProvider.getRfidtagEntityDao();
			
			//checking whether identity is free
			final Filter<RfidtagEntity> rfidIdentityFilter = rfidtagEntityDao.createFilter();
			rfidIdentityFilter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_RFID_IDENTITY, newRFIDTag.getRfidIdentity()));
			
			if (rfidtagEntityDao.count(rfidIdentityFilter) > 0) {
				throw new LogonException(Reason.RES_RFIDTAG_IDENTITY_NOT_UNIQUE);
			}
			
			return result = rfidtagEntityDao.persist(newRFIDTag);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public RfidtagEntity mergeRFIDTag(RfidtagEntity rfidTagToModify) {
		// [[
		RfidtagEntity result = null;
		Object[][] params = {{"rfidTagToModify", rfidTagToModify}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mergeRFIDTag", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			final ReadWriteDao<RfidtagEntity> rfidtagEntityDao = daoProvider.getRfidtagEntityDao();
			
			//checking whether identity is free
			final Filter<RfidtagEntity> rfidIdentityFilter = rfidtagEntityDao.createFilter();
			rfidIdentityFilter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_RFID_IDENTITY, rfidTagToModify.getRfidIdentity()));
			rfidIdentityFilter.addExpression(Restrictions.neq(RfidtagEntity.ATTR_RFIDTAG_ID, rfidTagToModify.getRfidtagId()));
			
			if (rfidtagEntityDao.count(rfidIdentityFilter) > 0) {
				throw new LogonException(Reason.RES_RFIDTAG_IDENTITY_NOT_UNIQUE);
			}
			
			return result = rfidtagEntityDao.merge(rfidTagToModify);
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
	 * Merges the rfidobject. In case it is null, nothing happens. 
	 * The merge updates the rfIds list of the rfidobject also.
	 * @param typeCd - typeCd of the object, this is going to be checked against the typeCd of the rfidobject
	 * @throws exeption if the rfifobject has typeCd, but does not correspond with the typeCd
	 */
	public RfidobjectEntity mergeRfIdObject(RFIDBean.AbstractRFIDBean bean, Long typeCd) {
		// [[
		RfidobjectEntity result = null;
		Object[][] params = {{"bean", bean},
							{"typeCd", typeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mergeRfIdObject", params);
		}
		// ]]
		try {
			RfidobjectEntity rfidobject = bean.getRfidObject();
			
			//in case the entity did not have an rfidobject, it has to get a new one
			if (rfidobject == null) {
				final RfidobjectTypeBsEntity bs = referentialDataService.findBsEntity(RfidobjectTypeBsEntity.class, typeCd);
				rfidobject = new RfidobjectEntity();
				rfidobject.setRfidobjectType(bs);
				bean.setRfidObject(rfidobject);
				rfidobject.setRfids(new ArrayList<RfidtagEntity>());
			}
			
			if (rfidobject.getRfids() == null) {
				rfidobject.setRfids(new ArrayList<RfidtagEntity>());
			}
			bean.generateRFIDObjectLabel(this, true); // do check for duplicates and throw LogonException handled by the application
			if (rfidobject.getRfidobjectType() != null && !rfidobject.getRfidobjectType().getRfidobjectTypeCd().equals(typeCd)) {
				LOG.error("Type of update did not correspond with the objects rfid type.");
				throw new LogonException();
			}
			
			final ReadWriteDao<RfidobjectEntity> dao2 = getDaoProvider().getRfidobjectEntityDao();
			if (rfidobject.getRfidobjectId() != null) {
				rfidobject = dao2.merge(rfidobject);
				em.refresh(rfidobject);
			}
			else {
				rfidobject = validateAndPersistRFIDObject(typeCd, bean);
			}
			
			return result = rfidobject;
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
	 * @return the rfidobject if found or null if not.
	 * 
	 * UQ_TLGNC_RFIDOBJECT_TYPE_LABEL ensures that findSingle won't throw NonUniqueResultException
	 */
	public RfidobjectEntity findSingleRFID(String objectLabel, Long typeCode) {
		final ReadOnlyDao<RfidobjectEntity> dao = getDaoProvider().getRfidobjectEntityDao();
		final Filter<RfidobjectEntity> filter = dao.createFilter();
		
		filter.addExpression(Restrictions.ilike(RfidobjectEntity.ATTR_OBJECT_LABEL, objectLabel));
		filter.addExpression(Restrictions.eq(RfidobjectEntity.ATTR_RFIDOBJECT_TYPE_CD, typeCode));

		return dao.findSingle(filter);
	}

	
	/**
	 * Queries and returns {@link RfidobjectSiloAttachEntity} entities filtered according to the given parameters.
	 * Only the first 100.
	 * @param final Filter filt
	 */	
	public List<RfidobjectSiloAttachEntity> queryRFIDSiloView(String label, String labelNoPrefix, Long typeCd){

		final ReadOnlyDao<RfidobjectSiloAttachEntity> dao = getDaoProvider().getRfidobjectSiloAttachEntityDao();
		final Filter<RfidobjectSiloAttachEntity> filt = dao.createFilter();
		
		//inbuilt case sensitive, either found with id or without id
		//because the context for this object can change it must be assured that other objects assigned or currently not assigned
		//won't have the same searchstring
		filt.addExpression(Restrictions.or(
				Restrictions.ilike(RfidobjectSiloAttachEntity.ATTR_OBJECT_LABEL, label),
				Restrictions.and(
						Restrictions.ilike(RfidobjectSiloAttachEntity.ATTR_OBJECT_LABEL, "%_" + labelNoPrefix),
						Restrictions.eq(RfidobjectSiloAttachEntity.ATTR_PORTABLE_SILO_FLAG, true)
						)
					)
				);
		filt.addExpression(Restrictions.eq(RfidobjectSiloAttachEntity.ATTR_RFIDOBJECT_TYPE_CD, typeCd));

		return dao.findNRecs(0, 100, filt);		
	}


	/**
	 * Persists the rfidobject - WARNING - only if it has any tag attached. In case it does not have any
	 * tag, it won't be saved to the db, will be set to null, and will be returned like that. 
	 * The merge updates the rfIds list of the rfidobject also.
	 * 
	 * @param typeCode - typeCd of the object, this is going to be checked against the typeCd of the rfidobject
	 * @throws LogonException if the RFID object has the same {label, type code} as another one in the DB 
	 */
	public RfidobjectEntity validateAndPersistRFIDObject(Long typeCode, RFIDBean.AbstractRFIDBean bean) {
		// [[
		RfidobjectEntity result = null;
		Object[][] params = {{"typeCd", typeCode},
							{"bean", bean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "persistRfIdObject", params);
		}
		// ]]
		try {
			RfidobjectEntity rfidObject = bean.getRfidObject();
			bean.generateAndValidateAndSetRFIDObjectLabel(this);
			
			final RfidobjectTypeBsEntity rfidObjectType = referentialDataService.findBsEntity(RfidobjectTypeBsEntity.class, typeCode);
			rfidObject.setRfidobjectType(rfidObjectType);

			
			final ReadWriteDao<RfidobjectEntity> dao = getDaoProvider().getRfidobjectEntityDao();
			
			final List<RfidtagEntity> rfidTags = rfidObject.getRfids();
			rfidObject.setRfids(null);
			rfidObject = dao.persist(rfidObject);
			rfidObject.setRfids(rfidTags);
			rfidObject = dao.merge(rfidObject);				
			
			return result = rfidObject;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	
	public RfidobjectEntity newRfidobjectEntity(long type) {
		final RfidobjectEntity result = new RfidobjectEntity();
		final RfidobjectTypeBsEntity bs = referentialDataService.findBsEntity(RfidobjectTypeBsEntity.class, type);
	    result.setRfidobjectType(bs);
	    return result;
	}
	
	
	private boolean updateLabel(RFIDBean.AbstractRFIDBean bean, Long id, String name) {
		// [[
		boolean result = false;
		Object[][] params = {{"bean", bean},
							{"id", id},
							{"name", name}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateLabel", params);
		}
		// ]]
		try {
			
			final String labelBefore = bean.getRfidObject().getObjectLabel();
			bean.generateRFIDObjectLabel(this, false); // do not check for duplicates - required to handle circular swapping of labels (ADN44026).
			
			final String labelAfter = bean.getRfidObject().getObjectLabel();
			
			if ((labelAfter == null && labelBefore != null) ||
					(labelAfter != null && !labelAfter.equals(labelBefore))) {
				LOG.debug("Updated rfidobject label for " + name + " (" + id + "). '" + labelBefore + "' -> '" + labelAfter + "'.");
			
				return result = true;
			}
			return result = false;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	public int _updateLoadingpoints() {

		int nbrOfUpdatedRecs = 0;
		int nbrOfInsertedRecs = 0;

		final ReadWriteDao<RfidobjectEntity> rfidobjectDao = getDaoProvider().getRfidobjectEntityDao();
		final ReadOnlyDao<LoadingpointEntity> loadingpointDao = getDaoProvider().getLoadingpointEntityDao();
		List<LoadingpointEntity> entities = loadingpointDao.find((Filter<LoadingpointEntity>) null);
		for (LoadingpointEntity loadingpoint : entities) {
			RFIDBean.AbstractRFIDBean bean = RFIDBean.LoadingpointRFIDBean(loadingpoint);
			if (bean.getRfidObject() == null) {
				final String searchString = bean.getRFIDSearchString();
				RfidobjectEntity rfidObject = findSingleRFID(searchString, Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);

				if (rfidObject == null) {
					rfidObject = newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);
					loadingpoint.setRfidobject(rfidObject);
					nbrOfInsertedRecs++;
					LOG.debug("Inserted new rfidobject for loadingpoint (" + loadingpoint.getLoadingpointId() + ")");

					bean = RFIDBean.LoadingpointRFIDBean(loadingpoint);
					bean.generateRFIDObjectLabel(this, false);    
					rfidobjectDao.persist(rfidObject);
				}
				else {
					LOG.error("RFIDObject with with type loadingpoint (typeCd:" + Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO + ") and the following label: " + searchString + " already exists, cannot create the rfidobject for the loadingpoint (ID" + loadingpoint.getLoadingpointId() + " loadingpoint name: " + loadingpoint.getLoadingpointName());
				}
			}
			else if (updateLabel(bean, loadingpoint.getLoadingpointId(), "loadingpoint")) {
				nbrOfUpdatedRecs++;
			}
		}
		LOG.info("Loadingpoints updated: " + nbrOfUpdatedRecs + " labels updated. " + nbrOfInsertedRecs + " rfidobjects added.");
		em.flush();
		
		return nbrOfUpdatedRecs + nbrOfInsertedRecs;
	}
	
	
	public int _updateShiptoSilos() {

		int nbrOfUpdatedRecs = 0;
		int nbrOfInsertedRecs = 0;

		final ReadWriteDao<RfidobjectEntity> rfidobjectDao = getDaoProvider().getRfidobjectEntityDao();
		final ReadOnlyDao<ShiptoSiloEntity> shiptoSiloDao = getDaoProvider().getShiptoSiloEntityDao();
		final List<ShiptoSiloEntity> entities = shiptoSiloDao.find((Filter<ShiptoSiloEntity>) null);
		for (ShiptoSiloEntity shiptoSilo : entities) {
			RFIDBean.AbstractRFIDBean bean = RFIDBean.ShiptoSiloRFIDBean(shiptoSilo);
			if (bean.getRfidObject() == null) {
				final String searchString = bean.getRFIDSearchString();
				RfidobjectEntity rfidObject = findSingleRFID(searchString, Constant.RFIDOBJECT_TYPE_SHIPTO_SILO);

				if (rfidObject == null) {
					rfidObject = newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO);
					shiptoSilo.setRfidobject(rfidObject);
					nbrOfInsertedRecs++;
					LOG.debug("Inserted new rfidobject for shiptosilo (" + shiptoSilo.getShiptoSiloId() + ")");

					bean = RFIDBean.ShiptoSiloRFIDBean(shiptoSilo);
					bean.generateRFIDObjectLabel(this, false);    
					rfidobjectDao.persist(rfidObject);
				}
				else {
					LOG.error("RFIDObject with with type silo (typeCd: " + Constant.RFIDOBJECT_TYPE_SHIPTO_SILO + ") and the following label: " + searchString + " already exists, cannot create the rfidobject for the silo (ID: " + shiptoSilo.getShiptoSiloId() + "silo name " + shiptoSilo.getSiloName() + ")");
				}
			}
			else if (updateLabel(bean, shiptoSilo.getShiptoSiloId(), "shiptosilo")) {
				nbrOfUpdatedRecs++;
			}
		}
		LOG.info("ShiptoSilos updated: " + nbrOfUpdatedRecs + " labels updated. " + nbrOfInsertedRecs + " rfidobjects added.");
		em.flush();
		
		return nbrOfUpdatedRecs + nbrOfInsertedRecs;
	}
	
	
	public int _updateTrailers() {

		int nbrOfUpdatedRecs = 0;
		int nbrOfInsertedRecs = 0;

		final ReadWriteDao<RfidobjectEntity> rfidobjectDao = getDaoProvider().getRfidobjectEntityDao();
		final ReadOnlyDao<TrailerEntity> trailerDao = getDaoProvider().getTrailerEntityDao();
		final List<TrailerEntity> entities = trailerDao.find((Filter<TrailerEntity>) null);
		for (TrailerEntity trailer : entities) {
			RFIDBean.AbstractRFIDBean bean = RFIDBean.TrailerRFIDBean(trailer);
			if (bean.getRfidObject() == null) {
				final String searchString = bean.getRFIDSearchString();
				RfidobjectEntity rfidObject = findSingleRFID(searchString, Constant.RFIDOBJECT_TYPE_TRAILER);

				if (rfidObject == null) {
					rfidObject = newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_TRAILER);
					trailer.setRfidobject(rfidObject);
					nbrOfInsertedRecs++;
					LOG.debug("Inserted new rfidobject for trailer (" + trailer.getTrailerId() + ")");

					bean = RFIDBean.TrailerRFIDBean(trailer);
					bean.generateRFIDObjectLabel(this, false);
					rfidobjectDao.persist(rfidObject);    
				}
				else {
					LOG.error("RFIDObject with with type trailer (typeCd:" + Constant.RFIDOBJECT_TYPE_TRAILER + ") and the following label: " + searchString + " already exists, cannot create the rfidobject for the trailer (ID: " + trailer.getTrailerId() + " plate Nr.: " + trailer.getTrailerPlateNo() + ")");
				}
			}
			else if (updateLabel(bean, trailer.getTrailerId(), "trailer")) {
				nbrOfUpdatedRecs++;
			}
		}
		LOG.info("Trailers updated: " + nbrOfUpdatedRecs + " labels updated. " + nbrOfInsertedRecs + " rfidobjects added.");
		em.flush();
		
		return nbrOfUpdatedRecs + nbrOfInsertedRecs;
	}
	
	
	public int _updateTrucks() {
		
		int nbrOfUpdatedRecs = 0;
        int nbrOfInsertedRecs = 0;

		final ReadWriteDao<RfidobjectEntity> rfidobjectDao = getDaoProvider().getRfidobjectEntityDao();
		final ReadOnlyDao<TruckEntity> truckDao = getDaoProvider().getTruckEntityDao();
		final List<TruckEntity> allTruckInDB = truckDao.find((Filter<TruckEntity>) null);
		for (TruckEntity truck : allTruckInDB) {
            RFIDBean.AbstractRFIDBean bean = RFIDBean.TruckRFIDBean(truck);
            Long truckId = truck.getTruckId();
			if (bean.getRfidObject() != null) {
				boolean updateLabel = updateLabel(bean, truckId, "truck");
				if (updateLabel) {
					nbrOfUpdatedRecs++;
				}
				continue;
			}

			final String searchString = bean.getRFIDSearchString();
			RfidobjectEntity rfidObject = findSingleRFID(searchString,
					Constant.RFIDOBJECT_TYPE_TRUCK);

			if (rfidObject != null) {
				LOG.error("RFIDObject with with type truck (typeCd:"
						+ Constant.RFIDOBJECT_TYPE_TRUCK + ") and the following label: "
						+ searchString
						+ " already exists, cannot create the rfidobject for the truck (ID: "
						+ truckId + "Plate Nr.: " + truck.getTruckPlateNo() + ")");
				continue;
			}

			rfidObject = newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_TRUCK);
			truck.setRfidobject(rfidObject);

			bean = RFIDBean.TruckRFIDBean(truck);
			bean.generateRFIDObjectLabel(this, false);

			rfidobjectDao.persist(rfidObject);

			nbrOfInsertedRecs++;
			LOG.debug("Inserted new rfidobject for truck (id: " + truckId + ")");
        }
        LOG.info("Trucks updated: " + nbrOfUpdatedRecs + " labels updated. " + nbrOfInsertedRecs + " rfidobjects added.");
        em.flush();
        
        return nbrOfUpdatedRecs + nbrOfInsertedRecs;
	}
	
	
	public int _updatePdaUsers() {
		
		int nbrOfUpdatedRecs = 0;
        int nbrOfInsertedRecs = 0;

		final ReadWriteDao<RfidobjectEntity> rfidobjectDao = getDaoProvider().getRfidobjectEntityDao();
		final ReadWriteDao<RfidtagEntity> rfidTagDao = getDaoProvider().getRfidtagEntityDao();
		final ReadWriteDao<UserEntity> userDao = getDaoProvider().getUserEntityDao();
		
		final List<UserEntity> entities = userDao.find((Filter<UserEntity>) null);		
        // 1) we must first delete any rfid object of users which have no PDA user number (anymore).
		for (UserEntity user : entities) {
            if (user.getPdaUserNo() == null) {
            	// if there is no PDA user number (anymore) but an RFID object exists, we must
            	// delete that object (and free all its RFID tags).
            	RfidobjectEntity rfidObject = user.getPdaUserRfidobject();
            	if (rfidObject != null) {
            		LOG.debug("Deleting RFIDObject '"+rfidObject.getRfidobjectId()+"' since user '"+user.getUserId()+"' has no PDA user number (anymore).");
            		final Filter<RfidtagEntity> rfidTagFilter = rfidTagDao.createFilter();
            		rfidTagFilter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_RFIDOBJECT, rfidObject));
            		final List<RfidtagEntity> rfidTags = rfidTagDao.find(rfidTagFilter);
            		for (RfidtagEntity rfidTag : rfidTags) {
            			rfidTag.setRfidobject(null);
            			rfidTagDao.persist(rfidTag);
            		}
            		user.setPdaUserRfidobject(null);
            		userDao.persist(user);
            		rfidobjectDao.remove(rfidObject);
            	}
            }
        }
        
        // 2) update/create rfid objects for users which have a PDA user number. 
		for (UserEntity user : entities) {
            if (user.getPdaUserNo() != null) {
                RFIDBean.AbstractRFIDBean bean = RFIDBean.UserRFIDBean(user);
                if (bean.getRfidObject() == null) {
                    final String searchString = bean.getRFIDSearchString();
                    RfidobjectEntity rfidObject = findSingleRFID(searchString, Constant.RFIDOBJECT_TYPE_PDA_USER);
                    
                    if (rfidObject == null) {
                        rfidObject = newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_PDA_USER);
                        user.setPdaUserRfidobject(rfidObject);
                        nbrOfInsertedRecs++;
                        LOG.debug("Inserted new rfidobject for user (" + user.getUserId() + ")");
                        bean = RFIDBean.UserRFIDBean(user);
                        bean.generateRFIDObjectLabel(this, false);    
                        rfidobjectDao.persist(rfidObject);
                    }
                    else {
                    	LOG.error("RFIDObject with with type pdauser(typeCd:" + Constant.RFIDOBJECT_TYPE_PDA_USER + ") and the following label: " + searchString + " already exists, cannot create the rfidobject for the user (ID: " + user.getUserId() + " User login: " + user.getExtLogin() + ")");
                    }
                    
                }
                else if (updateLabel(bean, user.getUserId(), "user")) {
                    nbrOfUpdatedRecs++;
                }
            }
        }
        LOG.info("PDA Users updated: " + nbrOfUpdatedRecs + " labels updated. " + nbrOfInsertedRecs + " rfidobjects added.");
        em.flush();
        
        return nbrOfUpdatedRecs + nbrOfInsertedRecs;
	}
	
	
	public int _updateWagons() {
		
		int nbrOfUpdatedRecs = 0;
        int nbrOfInsertedRecs = 0;
        

		final ReadWriteDao<RfidobjectEntity> rfidobjectDao = getDaoProvider().getRfidobjectEntityDao();
		final ReadOnlyDao<WagonEntity> wagonDao = getDaoProvider().getWagonEntityDao();
		final List<WagonEntity> entities = wagonDao.find((Filter<WagonEntity>) null);
        for (WagonEntity wagon : entities) {
            RFIDBean.AbstractRFIDBean bean = RFIDBean.WagonRFIDBean(wagon);
            if (bean.getRfidObject() == null) {
                final String searchString = bean.getRFIDSearchString();
                RfidobjectEntity rfidObject = findSingleRFID(searchString, Constant.RFIDOBJECT_TYPE_WAGON);
                
                if (rfidObject == null) {
                    rfidObject = newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_WAGON);
                    wagon.setRfidobject(rfidObject);
                    nbrOfInsertedRecs++;
                    LOG.debug("Inserted new rfidobject for wagon (" + wagon.getWagonId() + ")");
                    bean = RFIDBean.WagonRFIDBean(wagon);
                    bean.generateRFIDObjectLabel(this, false);    
                    rfidobjectDao.persist(rfidObject);
                }
                else {
                	LOG.error("RFIDObject with with type wagon (typeCd:" + Constant.RFIDOBJECT_TYPE_WAGON + ") and the following label: " + searchString + " already exists, cannot create the rfidobject for the wagon (ID: " + wagon.getWagonId() + ")");                    	
                }
            }
            else if (updateLabel(bean,  wagon.getWagonId(), "wagon")) {
                nbrOfUpdatedRecs++;
            }
        }
        LOG.info("Wagons updated: " + nbrOfUpdatedRecs + " labels updated. " + nbrOfInsertedRecs + " rfidobjects added.");
        em.flush();
        
        return nbrOfUpdatedRecs + nbrOfInsertedRecs;
	}
	
	
	public int _updateContainers() {
		
		int nbrOfUpdatedRecs = 0;
        int nbrOfInsertedRecs = 0;

		final ReadWriteDao<RfidobjectEntity> rfidobjectDao = getDaoProvider().getRfidobjectEntityDao();
		final ReadOnlyDao<ContainerEntity> containerDao = getDaoProvider().getContainerEntityDao();
		final List<ContainerEntity> entities = containerDao.find((Filter<ContainerEntity>) null);
        for (ContainerEntity container : entities) {
            RFIDBean.AbstractRFIDBean bean = RFIDBean.ContainerRFIDBean(container);
            if (bean.getRfidObject() == null) {
                final String searchString = bean.getRFIDSearchString();
                RfidobjectEntity rfidObject = findSingleRFID(searchString, Constant.RFIDOBJECT_TYPE_CONTAINER);
                
                if (rfidObject == null) {
                    rfidObject = newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_CONTAINER);
                    container.setRfidobject(rfidObject);
                    nbrOfInsertedRecs++;
                    LOG.debug("Inserted new rfidobject for container (" + container.getContainerId() + ")");
                    bean = RFIDBean.ContainerRFIDBean(container);
                    bean.generateRFIDObjectLabel(this, false);    
                    rfidobjectDao.persist(rfidObject);
                }
                else {
                	LOG.error("RFIDObject with with type container (typeCd:" + Constant.RFIDOBJECT_TYPE_CONTAINER + ") and the following label: " + searchString + " already exists, cannot create the rfidobject for the container (ID: " + container.getContainerId() + ")");                    	
                }
            }
            else if (updateLabel(bean, container.getContainerId(), "container")) {
                nbrOfUpdatedRecs++;
            }
        }
        LOG.info("Containers updated: " + nbrOfUpdatedRecs + " labels updated. " + nbrOfInsertedRecs + " rfidobjects added.");
        em.flush();
        
        return nbrOfUpdatedRecs + nbrOfInsertedRecs; 
	}
	
	
    @ScheduledTask(name = "rfid_object_label_cleanup", second = "0", minute = "0", hour = "1", configurationMethodName = "getRfidObjectLabelCleanupCronEnabled")	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void syncRFIDLabels() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "syncRFIDLabels");
		}
		// ]]
		try {
	        int nbrOfUpdates = 0;
	        
        	nbrOfUpdates += rfidService._updateLoadingpoints();
        	nbrOfUpdates += rfidService._updateShiptoSilos();
        	nbrOfUpdates += rfidService._updateTrailers();
        	nbrOfUpdates += rfidService._updateTrucks();
        	nbrOfUpdates += rfidService._updatePdaUsers();
        	nbrOfUpdates += rfidService._updateWagons();
	        nbrOfUpdates += rfidService._updateContainers();
			
	        if (nbrOfUpdates > 0) {
	        	long newRunNumber = cronstatusService._increaseCronjobRunNumber(Constant.CRONSTATUS_RFID_OBJECT_LABEL_CLEANUP);
	        	LOG.info("RFID labels have been cleaned up. " + nbrOfUpdates + " objects modified"
	                    + "; LAST_RUN_NUMBER: " + newRunNumber + ";");
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

	public RfidobjectEntity loadRFID(long rfidObjectId) {
		// [[
		RfidobjectEntity result = null;
		Object[][] params = {{"rfidObjectId", rfidObjectId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadRFID", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<RfidobjectEntity> dao = getDaoProvider().getRfidobjectEntityDao();
			RfidobjectEntity rfidObject = dao.findById(rfidObjectId);
			return result = rfidObject;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public UserEntity removeRfidObjectOfUser(UserEntity user) {
		// [[
		UserEntity result = null;
		Object[][] params = {{"user", user}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeRfidObjectOfUser", params);
		}
		// ]]
		try {
			final RfidobjectEntity rfidObject = user.getPdaUserRfidobject();
			if (rfidObject != null) {
				LOG.debug("Deleting RFIDObject '"+rfidObject.getRfidobjectId()+"' since user '"+user.getUserId()+"' has no PDA user number (anymore).");
				final ReadWriteDao<RfidtagEntity> rfidTagDao = getDaoProvider().getRfidtagEntityDao();
				final Filter<RfidtagEntity> rfidTagFilter = rfidTagDao.createFilter();
				rfidTagFilter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_RFIDOBJECT, rfidObject));
				final List<RfidtagEntity> rfidTags = rfidTagDao.find(rfidTagFilter);
				for (RfidtagEntity rfidTag : rfidTags) {
					rfidTag.setRfidobject(null);
					rfidTagDao.persist(rfidTag);
				}
				user.setPdaUserRfidobject(null);
				result = getDaoProvider().getUserEntityDao().merge(user);
				getDaoProvider().getRfidobjectEntityDao().remove(rfidObject);
				
				return result;
			}
			
			return user;
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
	 * Add or remove an RFID tag from/to an RFID object
	 * 
	 * @param selectedRfidTagId
	 * @param operation
	 * @param form TODO
	 */
	public void changeRfidTagAssignment(Long selectedRfidTagId,
			RfidTagOperation operation,
			RfidobjectEntity selectedRfidObject,
			RFIDDataCache rfidDataCache) {

		if (selectedRfidObject == Constant.NOT_SPECIFIED) {
			return;
		}

		List<RfidtagEntity> currentlyAssignedRfidTags = selectedRfidObject.getRfids();
		if (currentlyAssignedRfidTags == null) {
			currentlyAssignedRfidTags = new ArrayList<RfidtagEntity>();
		}
		Comparator<RfidtagEntity> rfidTagComparator = new RfidTagComparator();
		Set<RfidtagEntity> rfidTagsOfSelectedRfidObject = new TreeSet<RfidtagEntity>(
				rfidTagComparator);
		rfidTagsOfSelectedRfidObject.addAll(currentlyAssignedRfidTags);

		Set<RfidtagEntity> rfidsToAddOnPersist = rfidDataCache.getRfidsToAddOnPersist();
		Set<RfidtagEntity> rfidsToRemoveOnPersist = rfidDataCache.getRfidsToRemoveOnPersist();

		RfidtagEntity selectedRfidTag;
		switch (operation) {
		case ADD_RFID_TAG:
			selectedRfidTag = fetchRFIDTagById(selectedRfidTagId);
			em.detach(selectedRfidTag);
			
			//Remove the rfid if it is already in the list and readd the up to date one
			rfidTagsOfSelectedRfidObject.remove(selectedRfidTag);
			rfidTagsOfSelectedRfidObject.add(selectedRfidTag);
			
			selectedRfidTag.setRfidobject(selectedRfidObject);
			
			rfidsToAddOnPersist.add(selectedRfidTag);
			rfidsToRemoveOnPersist.remove(selectedRfidTag);
			break;
		case REMOVE_RFID_TAG:
			selectedRfidTag = getRfidTagByIdentifier(rfidTagsOfSelectedRfidObject,
					selectedRfidTagId);
			if (selectedRfidTag != null) {
				selectedRfidTag.setRfidobject(NOT_ASSIGNED_TO_RFID_OBJECT);
				rfidsToRemoveOnPersist.add(selectedRfidTag);
				rfidsToAddOnPersist.remove(selectedRfidTag);
			}
			break;
		default:
			throw new LogonException(Reason.RES_IMPL_BUG);
		}

		selectedRfidObject.setRfids(new ArrayList<RfidtagEntity>(rfidTagsOfSelectedRfidObject));
	}

	public ListRange findAssignableRfids(int start, int count, String query,
			RFIDDataCache rfidDataCache) {
		ListRange listRange = new ListRange();
		Set<RfidtagEntity> rfidsToAddOnPersist = rfidDataCache.getRfidsToAddOnPersist();
		List<RfidtagEntity> assignableRfids = rfidService.loadRFIDs(start, count, query,
				rfidsToAddOnPersist);
		Set<RfidtagEntity> rfidsToRemoveOnPersist = rfidDataCache.getRfidsToRemoveOnPersist();
		assignableRfids.addAll(rfidsToRemoveOnPersist);
		RfidTagComparator rfidTagComparator = new RfidTagComparator();
		Collections.sort(assignableRfids, rfidTagComparator);

		listRange.setData(assignableRfids.toArray());
		listRange.setTotalSize(rfidService.countRFIDs(query, rfidsToAddOnPersist));

		return listRange;
	}

	/**
	 * Since it cannot be removed from the rfid object, it must remain there, just the idtag reference must
	 * be updated.
	 * @param selectedRfidTagIdentifier
	 * 
	 * @return
	 */
	public RfidtagEntity getRfidTagByIdentifier(Set<RfidtagEntity> rfidTags,
			Long selectedRfidTagIdentifier) {
		for (RfidtagEntity currentRfidTag : rfidTags) {
			Long currentRfidTagIdentifier = currentRfidTag.getRfidtagId();
			if (currentRfidTagIdentifier.equals(selectedRfidTagIdentifier)) {
				return currentRfidTag;
			}
		}
		return null;
	}
}

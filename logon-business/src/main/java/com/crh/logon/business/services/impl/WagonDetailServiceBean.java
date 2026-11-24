/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.WagonDetailServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.WagonDetailServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.MaintRhythmEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.WagonDmgcodeTypeEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.common.WagonKeeperEntity;
import com.crh.logon.persistence.entities.common.WagonMaintEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantTrackBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.persistence.entities.views.PlantRailwayStationEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.WagonDetailAssignmentEntity;
import com.crh.logon.persistence.entities.views.WagonDetailLoadListEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class WagonDetailServiceBean extends WagonDetailServiceDispatcher implements WagonDetailServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonDetailServiceBean.class);

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private RFIDServiceLocalInterface rfidService;
	
	public WagonEntity getWagon(Long wagonId) {
		// [[
		WagonEntity result = null;
		Object[][] params = {{"wagonId", wagonId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagon", params);
		}
		// ]]
		try {
			
			ReadWriteDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_ID, wagonId));
			WagonEntity wagon = dao.findSingle(filter);
			if (wagon.getRfidobject() != null) {
					wagon.getRfidobject().initialize();
			}
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
	 * Retrieves a view entities with the latest active assignment related to this wagon, which has 
	 * assignment part which has assignment parts with filled in realstart, but also have parts without realstart.
	 * A normal closed assignment has only filled in realstart. 
	 * @param wagonId
	 * @return
	 */
	public List getCurrentAssignmentView(Long wagonId, String langCd) {
		// [[
		List result = null;
		Object[][] params = {{"wagonId", wagonId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCurrentAssignmentView", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<WagonDetailAssignmentEntity> dao = getDaoProvider().getWagonDetailAssignmentEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(WagonDetailAssignmentEntity.ATTR_WAGON_ID, wagonId));
			filter.addExpression(Restrictions.eq(WagonDetailAssignmentEntity.ATTR_LANG_CD, langCd));
			filter.addExpression(Order.asc(WagonDetailAssignmentEntity.ATTR_DRIVETO_PLANNED_START));
			List assignments  = dao.find(filter);
			return result = assignments;
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
	 * @param itemIds
	 * @return
	 */
	public List getLoadList(Long wagonId, String langCd) {
		// [[
		List result = null;
		Object[][] params = {{"wagonId", wagonId},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadList", params);
		}
		// ]]
		try {
			ReadOnlyDao<WagonDetailLoadListEntity> dao = getDaoProvider().getWagonDetailLoadListEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(WagonDetailLoadListEntity.ATTR_WAGON_ID, wagonId));
			filter.addExpression(Restrictions.eq(WagonDetailLoadListEntity.ATTR_LANG_CD, langCd));
			filter.addExpression(Order.desc(WagonDetailLoadListEntity.ATTR_LOADING_TS));
			result = dao.find(filter);
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
	 * Returns the current assignment of the wagon.
	 * @param wagonId
	 * @return
	 */
/*	public List getOrderViewEntity(Long wagonId, Locale locale) {
		// [[
		List result = null;
		Object[][] params = {{"wagonId", wagonId},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderViewEntity", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<WagonDetailOrderListEntity> dao = getDaoProvider().getWagonDetailOrderListEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(WagonDetailOrderListEntity.ATTR_WAGON_ID, wagonId));
			filter.addExpression(Restrictions.eq(WagonDetailOrderListEntity.ATTR_LANG_CD, locale.getLanguage()));
			return result = dao.find(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}*/
	
	
	
	public List getWagonKeeperList() {
		// [[
		List result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagonKeeperList");
		}
		// ]]
		try {
			
			ReadWriteDao<WagonKeeperEntity> dao = getDaoProvider().getWagonKeeperEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Order.ascCaseInsensitive(WagonKeeperEntity.ATTR_WAGON_KEEPER_NAME));
			
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
	 * Returns 
	 * @return
	 */
	public ListRange getRailwayStationListForName(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailwayStationListForName", params);
		}
		// ]]
		try {
			
			ReadWriteDao<RailwayStationEntity> dao = getDaoProvider().getRailwayStationEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_ACTIVE_FLAG,Boolean.TRUE));
			if ( StringUtils.notEmpty(query) ) {				
				filter.addExpression(Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME, '%' + query + '%'));
			}
			filter.addExpression(Order.ascCaseInsensitive(RailwayStationEntity.ATTR_RAILWAY_STATION_NAME));
			List stations = dao.findNRecs(skip,num,filter);
			result = new ListRange();
			result.setTotalSize(dao.count(filter));
			result.setData(stations.toArray());
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
	 * Returns 
	 * @return
	 */
	public ListRange getRailwayStationListForNo(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailwayStationListForNo", params);
		}
		// ]]
		try {
			
			ReadWriteDao<RailwayStationEntity> dao = getDaoProvider().getRailwayStationEntityDao();
			Filter filt = new FilterImpl();
			if (StringUtils.notEmpty(query)){
				long longValue = Constant.INVALID_KEY;
				try{
					longValue = Long.parseLong(query);
				}
				catch (NumberFormatException e){
					longValue = Constant.INVALID_KEY;
				}
				if (longValue == Constant.INVALID_KEY) {
					Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%');
				}
				else{					
					filt.addExpression(Restrictions.or(
							Restrictions.eq(RailwayStationEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(longValue)),
							Restrictions.ilike(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, '%'+ query +'%')
					));
				}
				filt.addExpression(Order.ascCaseInsensitive(RailwayStationEntity.ATTR_RAILWAY_STATION_NO));
			}
			List stations = dao.findNRecs(skip,num,filt);
			result = new ListRange();
			result.setTotalSize(dao.count(filt));
			result.setData(stations.toArray());
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
	
	public List fetchTrackListForRailwayStation(Long railwayStationId, String lang) {
		// [[
		List result = null;
		Object[][] params = {{"railwayStationId", railwayStationId},
							{"lang", lang}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTrackListForRailwayStation", params);
		}
		// ]]
		try {
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(PlantRailwayStationEntity.ATTR_RAILWAY_STATION_ID, railwayStationId));			
			f.addExpression(Restrictions.eq(PlantRailwayStationEntity.ATTR_LANG_CD, lang));	
			f.addExpression(Order.asc(PlantRailwayStationEntity.ATTR_TRACK_NAME));
			return result = getDaoProvider().getPlantRailwayStationEntityDao().find(f);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}	
	
	public List getShiptoSapBpList() {
		// [[
		List result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShiptoSapBpList");
		}
		// ]]
		try {
			
			ReadWriteDao<ShiptoSareaEntity> dao = getDaoProvider().getShiptoSareaEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.isNotNull(ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_RAILWAY_STATION + "." + RailwayStationEntity.ATTR_RAILWAY_STATION_ID));
			filter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_TYPE_CD, Constant.SHIPTO_TYPE_PLANT));
			filter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SAREA + "." + SareaEntity.ATTR_RAIL_RETURN_SAREA_FLAG, Boolean.TRUE));
			filter.addExpression(Order.ascCaseInsensitive(ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_RAILWAY_STATION + "." + RailwayStationEntity.ATTR_RAILWAY_STATION_NAME));

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
	
	public WagonEntity storeWagon(WagonEntity wagon) {
		// [[
		WagonEntity result = null;
		Object[][] params = {{"wagon", wagon}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeWagon", params);
		}
		// ]]
		try {
			
			ReadWriteDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
			if (wagon.getWagonId() == null) {
				AbstractRFIDBean bean = RFIDBean.WagonRFIDBean(wagon);
				RfidobjectEntity rfObj = rfidService.validateAndPersistRFIDObject(Constant.RFIDOBJECT_TYPE_WAGON, bean);
				wagon.setRfidobject(rfObj);
				return result = dao.persist(wagon);
			}
			else {
				AbstractRFIDBean bean = RFIDBean.WagonRFIDBean(wagon);
				RfidobjectEntity rfObj = rfidService.mergeRfIdObject(bean, Constant.RFIDOBJECT_TYPE_WAGON);
				wagon.setRfidobject(rfObj);
				
				final PlantTrackBsEntity track = wagon.getCurrentPlantTrack();
				final Long positionNo = wagon.getCurrentTrackPositionNo();
				
				//do not save the changes first because track changes must be handled separately see below
				Filter f = new FilterImpl();
				f.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_ID, wagon.getWagonId()));
				WagonEntity wagonDb = getDaoProvider().getWagonEntityDao().findSingle(f);
				wagon.setCurrentPlantTrack(wagonDb.getCurrentPlantTrack());
				wagon.setCurrentTrackPositionNo(wagonDb.getCurrentTrackPositionNo());
				
				wagon = dao.merge(wagon);
				
				
				if((wagon.getCurrentPlantTrack() != null && track == null) ||
						(wagon.getCurrentPlantTrack() == null && track != null) ||
						(wagon.getCurrentPlantTrack() != null && track != null && !wagon.getCurrentPlantTrack().getPlantTrackCd().equals(track.getPlantTrackCd())) ||
						(wagon.getCurrentTrackPositionNo() != null && positionNo == null) ||
						(wagon.getCurrentTrackPositionNo() == null && positionNo != null) ||
						(wagon.getCurrentTrackPositionNo() != null && positionNo != null && !wagon.getCurrentTrackPositionNo().equals(positionNo))						
				){
					//any change happened in the track/position data in this case the positions and tracks must be renumbered by disposervice				
					//track and position needs to be updated separately applying the same logic as the raildispo does at the time of switching/updating track/pos
					String[] wagons = {wagon.getWagonNo()};
					if (track != null) {					
						dispoService.setWagonTrack(wagons, track.getPlantTrackCd());
					}
					else{
						dispoService.setWagonTrack(wagons, null);
					}
					
					if (positionNo != null) {
						String[] posArr = {wagon.getWagonNo()};
						dispoService.setWagonTrackPos(posArr, positionNo);
					}
					f = new FilterImpl();
					f.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_NO,wagon.getWagonNo()));
					wagon = getDaoProvider().getWagonEntityDao().findSingle(f);
					return result = wagon;
				}
				else{
					return result = wagon;
				}
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
	
		public String getNhmForSapMaterialId(Long sapMaterialId) {
		// [[
		String result = null;
		Object[][] params = {{"sapMaterialId", sapMaterialId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getNhmForSapMaterialId", params);
		}
		// ]]
		try {
			if (sapMaterialId == null) {
				return result =  null;
			}
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.eq(SapMaterialPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
			JPAQuery jpaQuery = getDaoProvider().getSapMaterialPlantEntityDao().createJPAQuery();
			jpaQuery.setFilter(filt);
			jpaQuery.addSelect(SelectExpression.simpleSelect(SapMaterialPlantEntity.ATTR_NHM_CODE));
			jpaQuery.setDistinct(true);
			List distinctNhms = getDaoProvider().getSapMaterialPlantEntityDao().find(jpaQuery);
			if (distinctNhms == null || distinctNhms.size() == 0 || distinctNhms.size() > 1) {
				return result =  null;
			}
			else{
				//this is the only acceptable case, if there is only one NHM code for all plants for this material
				return result = (String)distinctNhms.get(0);	
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
	
	public SapMaterialDescEntity getSapMaterialDescEntityForSapNo(String sapMaterialNo, String language) {
		// [[
		SapMaterialDescEntity result = null;
		Object[][] params = {{"sapMaterialNo", sapMaterialNo},
							{"language", language}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSapMaterialDescEntityForSapNo", params);
		}
		// ]]
		try {
			if (StringUtils.empty(sapMaterialNo)) {
				return result = null;
			}
				
			ReadOnlyDao<SapMaterialDescEntity> dao = getDaoProvider().getSapMaterialDescEntityDao();
			Filter filter = new FilterImpl();
			
			filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, language));
			filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_SAP_MATERIAL_NO, sapMaterialNo));
			
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
	 * Query for certain parameters of wagon. Used for deterimining id the wagon is unique.
	 * Returns true if there is another wagon with the parameters provided. Returns false in case
	 * the wagon is still saveable, and will not interfere with unique constraints.
	 * @param wagonNo
	 * @return
	 */
	public boolean findWagonWithUniqueParameters(Long wagonId, String wagonNo){
		// [[
		boolean result = false;
		Object[][] params = {{"wagonId", wagonId},
							{"wagonNo", wagonNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findWagonWithUniqueParameters", params);
		}
		// ]]
		try {
			
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.ilike(WagonEntity.ATTR_WAGON_NO, wagonNo));
			if (wagonId != null) {				
				filt.addExpression(Restrictions.neq(WagonEntity.ATTR_WAGON_ID, wagonId));
			}
			List wagonsWithParameter = getDaoProvider().getWagonEntityDao().find(filt);
			if (wagonsWithParameter != null && !wagonsWithParameter.isEmpty()) {
				return result = true;
			}
			else{
				return result = false;
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
	 * Query for maintenance data of a specific wagon.
	 * @param wagonNo
	 * @return 
	 */
	public List findWagonMaintListByWagonId(Long wagonId){
		// [[
		List result = null;
		Object[][] params = {{"wagonId", wagonId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findWagonMaintListByWagonId", params);
		}
		// ]]
		try {
			
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.eq(WagonMaintEntity.ATTR_WAGON_ID, wagonId));
			filt.addExpression(Restrictions.eq(WagonMaintEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			List wagonMaintList = getDaoProvider().getWagonMaintEntityDao().find(filt);
			return result = wagonMaintList;
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
	 * Query for fetching maintenance/repair type shiptos
	 * @return 
	 */
	public List<ShiptoSareaEntity> fetchRepairShiptoList() {
		// [[
		List<ShiptoSareaEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRepairShiptoList");
		}
		// ]]
		try {
			final ReadWriteDao<ShiptoSareaEntity> dao = getDaoProvider().getShiptoSareaEntityDao();
			final Filter<ShiptoSareaEntity> filt = dao.createFilter();
			filt.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_TYPE_CD, new Long(Constant.SHIPPINGPOINT_TYPE_RAIL_REPAIR)));
			filt.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			filt.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SAREA + "." + SareaEntity.ATTR_RAIL_RETURN_SAREA_FLAG, Boolean.TRUE));
			filt.addExpression(Order.desc(ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_SAP_BP + '.' + SapBpEntity.ATTR_NAME1));
			
			return result = dao.find(filt);
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
	 * Fetch a list of maintRhythmTypeBs
	 * @return 
	 */
	public List fetchMaintRhythmByCd(Long cd){
		// [[
		List result = null;
		Object[][] params = {{"cd", cd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMaintRhythmByCd", params);
		}
		// ]]
		try {
			
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.eq(MaintRhythmEntity.ATTR_MAINT_RHYTHM_TYPE_CD, cd));
			List resultList = getDaoProvider().getMaintRhythmEntityDao().find(filt);
			return result = resultList;
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
	 * Decides if the wagon is due to maintenance.
	 * Checks if there is any maintenance type which is scheduled within its activation period.
	 * 
	 * @param wagon
	 * @return null or the typeCd of the maintenance type 
	 */
	
	public String isMaintenanceDue(WagonEntity wagon) {
		// [[
		String result1 = null;
		Object[][] params = {{"wagon", wagon}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isMaintenanceDue", params);
		}
		// ]]
		try {
			String result = null;
			List wagonMaintList = findWagonMaintListByWagonId(wagon.getWagonId());
			Map maintRhythmMap = getMaintRhythmMap(wagon.getMaintRhythmType().getMaintRhythmTypeCd(), false); 
			
			if ( wagonMaintList == null) {
				return result1 = result;
			}
			Calendar currentDate = new GregorianCalendar();
			ListIterator wmIt = wagonMaintList.listIterator();
			while ( wmIt.hasNext() ) {
				WagonMaintEntity wm = (WagonMaintEntity) wmIt.next();
				if ( wm.getNextDat() != null ) {
					MaintRhythmEntity mr = (MaintRhythmEntity) maintRhythmMap.get(wm.getMaintType().getMaintTypeCd());
					Calendar nextMinusActivation = new GregorianCalendar();
					nextMinusActivation.setTime(wm.getNextDat());
					int activationPeriodD = mr == null ? Constant.WAGON_MAINTENANCE_DEFAULT_ACTIVATION_PERIOD_D : mr.getActivationPeriodD().intValue();
					nextMinusActivation.add(Calendar.DATE, activationPeriodD * -1);
					Calendar nextDate = new GregorianCalendar();
					nextDate.setTime(wm.getNextDat());
					if ( currentDate.before(nextDate) && currentDate.after(nextMinusActivation) ) {
						return result1 = wm.getMaintType().getMaintTypeCd();
					}
				}
			}
			return result1 = result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}

	public Map getMaintRhythmMap(Long maintRhythmTypeCd) {
		// [[
		Map result = null;
		Object[][] params = {{"maintRhythmTypeCd", maintRhythmTypeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMaintRhythmMap", params);
		}
		// ]]
		try {
			return result = getMaintRhythmMap(maintRhythmTypeCd, true);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private Map getMaintRhythmMap(Long maintRhythmTypeCd, boolean initAll) {
		// [[
		Map result = null;
		Object[][] params = {{"maintRhythmTypeCd", maintRhythmTypeCd},
				{"initAll", Boolean.toString(initAll)}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMaintRhythmMap", params);
		}
		// ]]
		try {
			List maintRhythmList = fetchMaintRhythmByCd(maintRhythmTypeCd);
			ListIterator mrIt = maintRhythmList.listIterator();
			Map rhythmMap = new HashMap();
			 
			while ( mrIt.hasNext() ) {
				MaintRhythmEntity mrEnt = (MaintRhythmEntity) mrIt.next();
				if (initAll) {
					mrEnt.initialize();
				}
				rhythmMap.put(mrEnt.getMaintType().getMaintTypeCd(), mrEnt);
			}
			
			return result = rhythmMap;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List storeWagonMaintList(List wagonMaintList) {
		List result = new ArrayList();
		if (wagonMaintList != null) {
			ReadWriteDao<WagonMaintEntity> dao = getDaoProvider().getWagonMaintEntityDao();
			ListIterator wmIt = wagonMaintList.listIterator();
			while (wmIt.hasNext()) {
				WagonMaintEntity wm = (WagonMaintEntity) wmIt.next();
				if ( wm.getWagonMaintId() == null ) {
					wm = dao.persist(wm);
					result.add(wm);
				}
				else {
					wm = dao.merge(wm);
					result.add(wm);
				}
			}
		}
		return result;
	}
	
	/**
	 * Iterates through the elements of the input list. Persist non persistent
	 * objects, merges formerly persisted ones. 
	 * 
	 * @param wagonDmgcodeTypeEntityList the list of {@link WagonDmgcodeTypeEntity}
	 * 		   to persist
	 * @return the persisted list
	 */
	public List storeWagonDmgcodeTypeEntityList(List wagonDmgcodeTypeEntityList) {
		// [[
		List result = null;
		Object[][] params = {{"wagonDmgcodeTypeEntityList", wagonDmgcodeTypeEntityList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeWagonDmgcodeTypeEntityList", params);
		}
		// ]]
		try {
			List persistedEntities = new ArrayList();
			if (wagonDmgcodeTypeEntityList != null) {
				ReadWriteDao<WagonDmgcodeTypeEntity> dao = getDaoProvider().getWagonDmgcodeTypeEntityDao();
				ListIterator wmIt = wagonDmgcodeTypeEntityList.listIterator();
				while (wmIt.hasNext()) {
					WagonDmgcodeTypeEntity wm = (WagonDmgcodeTypeEntity) wmIt.next();
					if ( wm.getWagonDmgcodeTypeId() == null ) {
						wm = dao.persist(wm);
						persistedEntities.add(wm);
					}
					else {
						wm = dao.merge(wm);
						persistedEntities.add(wm);
					}
				}
			}
			return result = persistedEntities;
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
	 * Returns a tx list of damage codes for the bs entities in wagonDmgcodeTypeEntity.
	 * @param locale
	 * @param wagonDmgcodeTypeEntityList
	 * @return
	 */
	public List getDamageCodeTxList(Locale locale, List wagonDmgcodeTypeEntityList) {
		// [[
		List result = null;
		Object[][] params = {{"locale", locale},
							{"wagonDmgcodeTypeEntityList", wagonDmgcodeTypeEntityList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDamageCodeTxList", params);
		}
		// ]]
		try {
			if (wagonDmgcodeTypeEntityList.isEmpty()) {
				return result = new ArrayList();
			}
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq("damagecodeTxPK.langCd", locale.getLanguage()));
			Iterator it = wagonDmgcodeTypeEntityList.iterator();
			List ids = new ArrayList();
			while (it.hasNext()) {
				WagonDmgcodeTypeEntity bs = (WagonDmgcodeTypeEntity) it.next();
				ids.add(bs.getDmgcodeType().getDamagecodeCd());
			}
			filter.addExpression(Restrictions.in("damagecodeTxPK.damagecodeBs.damagecodeCd", ids));
			filter.addExpression(Order.ascCaseInsensitive(DamagecodeTxEntity.ATTR_TXT));
			return result = referentialDataService.findTxEntityFiltered(DamagecodeTxEntity.class, filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
//	public void removeWagon(Long wagonId) {
//		// [[
//		Object[][] params = {{"wagonId", wagonId}};
//		boolean logDebugEnabled = LOG.isDebugEnabled();
//		if (logDebugEnabled) {
//			LOG.entering(this, "removeWagon", params);
//		}
//		// ]]
//		try {
//			
//			ReadWriteDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
//			Filter filter = new FilterImpl();
//			filter.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_ID, wagonId));
//			
//			WagonEntity wagon = dao.findSingle(filter);
//			if (wagon != null) {
//				dao.remove(wagon);
//			}
//		}
//		finally {
//		// [[
//			if (logDebugEnabled) {
//				LOG.exiting();
//			}
//		// ]]
//		}
//	}
}

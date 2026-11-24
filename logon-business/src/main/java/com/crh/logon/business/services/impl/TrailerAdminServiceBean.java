/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.dispatchers.TrailerAdminServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TrailerAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.TruckTrailerHistoryEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleCharBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class TrailerAdminServiceBean extends TrailerAdminServiceDispatcher implements TrailerAdminServiceLocalInterface{
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TrailerAdminServiceBean.class);
	
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private RFIDServiceLocalInterface rfidService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;

	/**
	 * @param skip from pager
	 * @param num from pager
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @adnlog.exclude return
	 */
	public List fetchCarriers(int skip, int num, String query) {
		// [[
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarriers", params);
		}
		// ]]
		try {
			List list = new ArrayList();
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			
			Filter filter =  new FilterImpl();
			long id = Constant.INVALID_KEY;
			try{				
				 id = Long.valueOf(query);
			}
			catch (NumberFormatException e){
				id = Constant.INVALID_KEY;
			}
			
			if ( id != Constant.INVALID_KEY ){
				filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_CARRIER_ID, id));
			}
			list = dao.find(filter);
			return list;
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
	 * Fetches all carriers.
	 * @adnlog.exclude return
	 */
	public List fetchCarriers() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarriers");
		}
		// ]]
		try {
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
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
	 * Fetches all selectable sap trailers for the create trailer popup's SapVehicleNo dropdown.
	 * @param carrierId selected carrier
	 * @param skip find parameter
	 * @param num find parameter
	 * @param query input text
	 * @return
	 */
	public List<SapVehicleEntity> fetchGenericSapTrailersOfCarrier(Long carrierId, int skip, int num, String query) {
		// [[
		List result = null;
		Object[][] params = {{"carrierId", carrierId},
							{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchGenericSapTrailersOfCarrier", params);
		}
		// ]]
		try {
			//SAP Vehicle Number | Sap Vehicle type | Sap Vehicle Desc
			
			//1. Get the vendor
			
			ReadWriteDao<CarrierEntity> carrierEntityDao = getDaoProvider().getCarrierEntityDao();			
			CarrierEntity carrier = carrierEntityDao.findById(carrierId);
			
			//2.) Get the unique set of sapVehicleTypeCd's for any matching description string
						
			FilterImpl txFilter = new FilterImpl();
			txFilter.addExpression(Restrictions.like(SapVehicleTypeTxEntity.ATTR_TXT, query + "%"));
			
			List<SapVehicleTypeTxEntity> sapVehicleTypeTxList = referentialDataService.findTxEntityFiltered(SapVehicleTypeTxEntity.class, txFilter);
			
			List<String> txIds = new ArrayList<String>();
			
			txIds.add("");
			
			for (SapVehicleTypeTxEntity sapVehicleTypeTxEntity : sapVehicleTypeTxList) {
				String sapVehicleTypeCd = sapVehicleTypeTxEntity.getSapVehicleTypeTxPK().getSapVehicleTypeBs().getSapVehicleTypeCd();
				if (!txIds.contains(sapVehicleTypeCd)) {
					txIds.add(sapVehicleTypeCd);
				}
			}
			
			txIds.add("");
			
			//3. Match the complete query
			
			ReadWriteDao<SapVehicleEntity> dao = getDaoProvider().getSapVehicleEntityDao();
			
			Filter<SapVehicleEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VENDOR, carrier.getSapVendor()));
			
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE + "." + SapVehicleTypeBsEntity.ATTR_LOGON_VEHICLE_TYPE_CD, Constant.VEHICLE_LOGON_TYPE_TRAILER));
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_CHAR  + "." + SapVehicleCharBsEntity.ATTR_SAP_VEHICLE_CHAR_CD, Constant.VEHICLE_CHARACTERISTIC_GENERIC));
			
			Expression inputRestriction = null;
			
			if (0 < txIds.size()) {
				inputRestriction = Restrictions.or(
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_NO, query + "%"),
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE_CD, query + "%"),
					Restrictions.in(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE_CD, txIds)
				);
			}
			else {
				inputRestriction = Restrictions.or(
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_NO, query + "%"),
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE_CD, query + "%")
				);		
			}
			
			filter.addExpression(inputRestriction);
			
			List<SapVehicleEntity> trucks = dao.findNRecs(skip, num, filter);
			return result = trucks;
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
	 * Returns the trailers for the given carrier.
	 * @param showOnlyActiveTrailers if true, only trailers with ACTIVE_FLAG == true will be returned
	 * @param carrierId the id of the carrier to which trailers will be queried
	 * @adnlog.exclude return
	 */
	@AdnTrace
	public List<TrailerEntity> fetchTrailersForCarrier(long carrierId, boolean showOnlyActiveTrailers) {

		List<TrailerEntity> list = new ArrayList<TrailerEntity>();
		ReadWriteDao<TrailerEntity> dao = getDaoProvider().getTrailerEntityDao();
		Filter<TrailerEntity> filter = new FilterImpl<TrailerEntity>();

		filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CARRIER_ID, carrierId));
		filter.addExpression(Order.asc(TrailerEntity.ATTR_TRAILER_NAME));
		if (showOnlyActiveTrailers) {
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_ACTIVE_FLAG, true));
		}

		list = dao.find(filter);
		return list;

	}
	
	/**
	 * @param trailerId the id of the trailer which is going to be returned
	 * @adnlog.exclude return
	 */
	public TrailerEntity fetchTrailer(long trailerId) {
		// [[
		Object[][] params = {{"trailerId", trailerId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTrailer", params);
		}
		// ]]
		try {
			
			ReadWriteDao<TrailerEntity> dao = getDaoProvider().getTrailerEntityDao();
			TrailerEntity trailer = dao.findById(trailerId);
			if (trailer.getRfidobject() != null ) {
				trailer.getRfidobject().initialize();
			}
			return trailer;
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
	 * @param carrierId the id of the carrier which is going to be returned
	 * @adnlog.exclude return
	 */
	public CarrierEntity fetchCarrier(long carrierId) {
		// [[
		Object[][] params = {{"carrierId", carrierId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarrier", params);
		}
		// ]]
		try {
			
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			CarrierEntity carrier = dao.findById(carrierId);
			return carrier;
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
	 * Updates an existing trailer.
	 */
	public TrailerEntity updateTrailer(TrailerEntity trailer, ContainerEntity newContainer, ContainerEntity originalContainer) {
		// [[
		TrailerEntity result = null;
		Object[][] params = {{"trailer", trailer},
							{"newContainer", newContainer},
							{"originalContainer", originalContainer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTrailer", params);
		}
		// ]]
		try {
			RfidobjectEntity rfidobject = trailer.getRfidobject();
			long rfidobjectTypeTrailer = Constant.RFIDOBJECT_TYPE_TRAILER;
			
			AbstractRFIDBean bean = RFIDBean.TrailerRFIDBean(trailer);
			rfidobject = rfidService.mergeRfIdObject(bean, rfidobjectTypeTrailer);
			trailer.setRfidobject(rfidobject);
			ReadWriteDao<TrailerEntity> dao = getDaoProvider().getTrailerEntityDao();
			trailer = dao.merge(trailer);
			
			TruckEntity truck = trailer.getCurrentTruck();
			if ( truck != null && trailer != null && truck.getTruckTransportCat().getTruckTransportCatCd().equals(Constant.TRUCK_TRSP_CAT_CONTAINER) ) {
				//in this case the container should only be attached to the truck, because it is not possible to have both
				LOG.error("Truck " + truck.getTruckId() + " is of transport cat container and the new composition contains also a trailer");
			}
			if (originalContainer != null && (newContainer == null || !newContainer.getContainerId().equals(originalContainer.getContainerId()))){
				truckAdminService.releaseContainer(originalContainer, false);
			}			
			
			if (newContainer != null ) {
				truckAdminService.attachContainerToTruckTrailerComposition(newContainer, truck, trailer,
						false);
			}
			
			return result = trailer;
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
	 * Creates a new trailer, and saves its fields.
	 */
	public TrailerEntity createTrailer(TrailerEntity trailer) {
		// [[
		TrailerEntity result = null;
		Object[][] params = {{"trailer", trailer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createTrailer", params);
		}
		// ]]
		try {
			RfidobjectEntity rfObj = trailer.getRfidobject();
			AbstractRFIDBean bean = RFIDBean.TrailerRFIDBean(trailer);
			rfObj = rfidService.validateAndPersistRFIDObject(Constant.RFIDOBJECT_TYPE_TRAILER, bean);
			trailer.setRfidobject(rfObj);
			
			ReadWriteDao<TrailerEntity> dao = getDaoProvider().getTrailerEntityDao();
			trailer = dao.persist(trailer);
			
			
			//updateSelectedPools(selectedPoolIds, trailer);
			return result = trailer;
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
	 * Loads truck trailer assignments.
	 * @param trailerId
	 * @return
	 */
	public List loadTrailerHistory(Long trailerId) {
		// [[
		List result = null;
		Object[][] params = {{"trailerId", trailerId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTrailerHistory", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckTrailerHistoryEntity> dao = getDaoProvider().getTruckTrailerHistoryEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TruckTrailerHistoryEntity.ATTR_TRAILER_ID, trailerId));
			filter.addExpression(Order.desc(TruckTrailerHistoryEntity.ATTR_TRUCK_TRAILER_HISTORY_ID));
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
	 * Get last position of trailer according to EVENT_TIMESTAMP. The trailer's last position is defined so.
	 * 
	 * @param trailerId
	 * @return double []
	 */
	public Double[] fetchLastPosition( long trailerId) {
		// [[
		Double[] result3 = null;
		Object[][] params = {{"trailerId", trailerId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLastPosition", params);
		}
		// ]]
		try {
			
			Double [] coordinates = new Double [2];
			ReadWriteDao<BizeventEntity> dao = getDaoProvider().getBizeventEntityDao();
			Filter filter = new FilterImpl();
			
			filter.addExpression(Restrictions.isNotNull(BizeventEntity.ATTR_COORD_LAT));
			filter.addExpression(Restrictions.isNotNull(BizeventEntity.ATTR_COORD_LON));
			filter.addExpression(Restrictions.eq(BizeventEntity.ATTR_TRAILER_ID, trailerId));
			filter.addExpression(Order.desc(BizeventEntity.ATTR_CTL_CRE_TS));
			
			List found = dao.findNRecs(0, 1, filter);
			if (found != null && !found.isEmpty() ){
				BizeventEntity event = (BizeventEntity) found.get(0);
				coordinates[0] = event.getCoordLat();
				coordinates[1] = event.getCoordLon();
			}
								
			return result3 = coordinates;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result3);
			}
		// ]]
		}
	}
	
	/**
	 * Returns the trailer for the truck (if exists);
	 * @param trailerId
	 * @return
	 */
	public ContainerEntity fetchContainerForTrailer(Long trailerId, boolean onlyActive) {
		// [[
		ContainerEntity result = null;
		Object[][] params = {{"trailerId", trailerId},
							{"onlyActive", onlyActive + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContainerForTrailer", params);
		}
		// ]]
		try {
			ContainerEntity container = null;
			ReadWriteDao<ContainerEntity> trailerDao = getDaoProvider().getContainerEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRAILER_ID, trailerId));
			if (onlyActive) {				
				filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			}
			container = trailerDao.findSingle(filter);
			return result = container;
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
	 * Query for certain parameters of trailer. Used for deterimining id the trailer is unique.
	 * Returns true if there is another trailer with the parameters provided. Returns false in case
	 * the trailer is still saveable, and will not interfere with unique constraints.
	 * @param trailerName
	 * @param plateNo
	 * @return
	 */
	public boolean findTailerWithUniqueParameters(Long trailerId, String trailerName, String plateNo){
		// [[
		boolean result = false;
		Object[][] params = {{"trailerId", trailerId},
							{"trailerName", trailerName},
							{"plateNo", plateNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findTailerWithUniqueParameters", params);
		}
		// ]]
		try {
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.or(
					Restrictions.ilike(TrailerEntity.ATTR_TRAILER_NAME, trailerName),
					Restrictions.ilike(TrailerEntity.ATTR_TRAILER_PLATE_NO, plateNo)
					));
			if (trailerId != null) {				
				filt.addExpression(Restrictions.neq(TrailerEntity.ATTR_TRAILER_ID, trailerId));
			}
			List trailersWithParameter = getDaoProvider().getTrailerEntityDao().find(filt);
			if (trailersWithParameter != null && !trailersWithParameter.isEmpty()) {
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

}

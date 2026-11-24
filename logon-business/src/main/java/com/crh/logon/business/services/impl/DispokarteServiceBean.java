/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.sap.dto.SapContractDispDto;
import com.crh.logon.business.services.dispatchers.DispokarteServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.business.services.utils.SapMaterialUtil;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.sap.BusinessPartnerInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoPersonEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrBtrulEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.ShiptoTruckTypeEntity;
import com.crh.logon.persistence.entities.common.ShiptoWagonTypeEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.refdata.BilltoRuletypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SignatureTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialSareaEntity;
import com.crh.logon.persistence.entities.views.BpSareaSofficeSgroupEntity;
import com.crh.logon.persistence.entities.views.ContractItemOrderItemListEntity;
import com.crh.logon.persistence.entities.views.SapContractDispEntity;
import com.crh.logon.persistence.entities.views.SapContractItemDispEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.SapSoldBillSareaSrchEntity;
import com.crh.logon.persistence.entities.views.ShiptoSareaRestrSearEntity;
import com.crh.logon.persistence.entities.views.SoldshipBestFlexSrchEntity;
import com.crh.logon.persistence.entities.views.SoldshipFlexSrchEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@Interceptors(UserInfoInterceptor.class)
@Named
public class DispokarteServiceBean extends DispokarteServiceDispatcher implements DispokarteServiceLocalInterface{
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispokarteServiceBean.class);
	private static final Long RESTRICTION_RULE_TYPE_MONTH = new Long(0);
	private static final Long RESTRICTION_RULE_TYPE_DAY_OF_WEEK = new Long(1);
	private static final Long RESTRICTION_RULE_TYPE_DAY_OF_MONTH = new Long(2);
	
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private ConfigServiceLocalInterface configService;
	@EJB private RFIDServiceLocalInterface rfidService;

	@EJB private BusinessPartnerInterface businessPartnerStore;
	
	public SapBpEntity getBilltoSapBpById(Long sapBpId) {
		// [[
		SapBpEntity result = null;
		Object[][] params = {{"sapBpId", sapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getBilltoSapBpById", params);
		}
		// ]]
		try {
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_BP_ID, sapBpId));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_BILLTO, Boolean.TRUE));
			
			return result = getDaoProvider().getSapBpEntityDao().findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public SapBpEntity getSoldtoSapBpById(Long sapBpId) {
		// [[
		SapBpEntity result = null;
		Object[][] params = {{"sapBpId", sapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSoldtoSapBpById", params);
		}
		// ]]
		try {
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_BP_ID, sapBpId));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, Boolean.TRUE));
			
			return result = getDaoProvider().getSapBpEntityDao().findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public SapBpEntity getShiptoSapBpById(Long sapBpId) {
		// [[
		SapBpEntity result = null;
		Object[][] params = {{"sapBpId", sapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShiptoSapBpById", params);
		}
		// ]]
		try {
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_BP_ID, sapBpId));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SHIPTO, Boolean.TRUE));
			
			return result = getDaoProvider().getSapBpEntityDao().findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public SapBpEntity getPayerSapBpById(Long sapBpId) {
		// [[
		SapBpEntity result = null;
		Object[][] params = {{"sapBpId", sapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPayerSapBpById", params);
		}
		// ]]
		try {
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_BP_ID, sapBpId));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_PAYER, Boolean.TRUE));
			
			return result = getDaoProvider().getSapBpEntityDao().findSingle(filter);
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
	 * Fetches existing ship-to sales area entities for a given ship-to and sold-to.
	 * @param shipto ship-to entity (not null)
	 * @param soldtoSapBpId sold-to sap bp id (not null)
	 * @param initialize if true, the ship-to sales area entities will be initialized
	 * @return list of ship-to sales area entities
	 */
	public List<ShiptoSareaEntity> fetchShiptoSareas(ShiptoEntity shipto, Long soldtoSapBpId, 
			boolean initialize) {
		// [[
		List<ShiptoSareaEntity> result = null;
		Object[][] params = {{"shipto", shipto},
							{"soldtoSapBpId", soldtoSapBpId},
							{"initialize", initialize + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoSareas", params);
		}
		// ]]
		try {
			final List<Long> commonSalesAreasForShiptoSoldto = 
					getCommonSalesAreaIdsForShiptoAndSoldtoId(shipto.getShiptoSapBp().getSapBpId(), soldtoSapBpId);
			
			return result = fetchShiptoSareas(shipto, commonSalesAreasForShiptoSoldto, initialize);
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
	 * Fetches existing ship-to sales area entities for a given ship-to and a list of sales areas
	 * @param shipto not null
	 * @param sareaIds not null
	 * @param initialize
	 * @return list of ship-to sales area entities
	 */
	public List<ShiptoSareaEntity> fetchShiptoSareas(ShiptoEntity shipto, List<Long> sareaIds, 
			boolean initialize)
	{
		// [[
		List<ShiptoSareaEntity> result = null;
		Object[][] params = {{"shipto", shipto},
							{"sareaIds", sareaIds},
							{"initialize", initialize + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoSareas", params);
		}
		// ]]
		try {
			if(sareaIds.isEmpty()) {
				return result = new ArrayList<ShiptoSareaEntity>();
			}
			
			final ReadWriteDao<ShiptoSareaEntity> dao = getDaoProvider().getShiptoSareaEntityDao();
			final Filter<ShiptoSareaEntity> f = dao.createFilter();
			
			f.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO, shipto));
			f.addExpression(Restrictions.in(ShiptoSareaEntity.ATTR_SAREA_ID, sareaIds));
			final List<ShiptoSareaEntity> shiptoSalesAreas = dao.find(f);
			
			if (initialize) {
				initializeShiptoSareas(shiptoSalesAreas);
			}
			
			return result = shiptoSalesAreas;
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
	 * Query for finding SapBpEntity by SAP ID
	 * @param id
	 * @return SapBpEntity matching the query
	 */
	public SapBpEntity fetchSoldtoSapBpBySapId(String sapId) {
		// [[
		SapBpEntity result = null;
		Object[][] params = {{"sapId", sapId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapBpBySapId", params);
		}
		// ]]
		try {
			ReadWriteDao<SapBpEntity> sapBpDao = getDaoProvider().getSapBpEntityDao();
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, Boolean.TRUE));
			f.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_ID, sapId));
			return result = sapBpDao.findSingle(f);
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
	 * Query for finding Shipto by id
	 * @param id
	 * @return ShiptoEntity matching the query
	 */
	public ShiptoEntity fetchShipto(Long id) {
		// [[
		ShiptoEntity result = null;
		Object[][] params = {{"id", id}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShipto", params);
		}
		// ]]
		try {
			
			ReadWriteDao<ShiptoEntity> shipToDao = getDaoProvider().getShiptoEntityDao();
			return result = shipToDao.findById(id.longValue());
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
	 * Create a new ShiptoEntity and saves it to the database.
	 * 
	 * @param shiptoSapId SAP ID of the SapBp to which the new shipto 
	 * corresponds.
	 * @param soldtoSapId  SAP ID of the SapBp to which the sales 
	 * areas will be loaded.
	 * 
	 * @return ShiptoEntity
	 */
	public ShiptoEntity createShipto(String shiptoSapId, String soldtoSapId) {
		// [[
		ShiptoEntity result = null;
		Object[][] params = {{"shiptoSapId", shiptoSapId},
							{"soldtoSapId", soldtoSapId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createShipto", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapBpEntity> sapBpEntityDao = getDaoProvider().getSapBpEntityDao();
			Filter<SapBpEntity> filter = sapBpEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SHIPTO, Boolean.TRUE));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_ID, shiptoSapId));
			SapBpEntity shiptoSapBp = getDaoProvider().getSapBpEntityDao().findSingle(filter);
			if (shiptoSapBp == null) {
				LOG.error("Cannot fetch shipto with shiptoSapId: " + shiptoSapId + 
						" and  soldtoSapId: " + soldtoSapId + ".");
				throw new LogonException(Reason.RES_ERROR_DISPOKARTE_NO_SHIPTO_FOUND);
			}
			ShiptoEntity shipto = new ShiptoEntity();			
			shipto.setUnloadingTimeCemMin(null);
			shipto.setUnloadingTimeAggMin(null);
			shipto.setLoadingTimeLfMin(null);			
			shipto.setShiptoSapBp(shiptoSapBp);
			shipto.setActiveFlag(Boolean.TRUE);
			shipto.setCoordFixFlag(Boolean.TRUE);
			shipto.setDailyOrderContactFlag(Boolean.FALSE);
			shipto.setPaperSingleDelnoteFlagCem(Boolean.TRUE);
			shipto.setPaperSingleDelnoteFlagAgg(Boolean.TRUE);
			shipto.setPaperCollDelnoteFlagCem(Boolean.FALSE);
			shipto.setPaperCollDelnoteFlagAgg(Boolean.FALSE);
			shipto.setEmailDelivnoteFlag(Boolean.FALSE);
			shipto.setRailHoldBackReOrderFlag(Boolean.FALSE);
			Long shiptoTypeCd;
			if (shiptoSapBp.getTreatAsOnetimeFlag()) {
				shiptoTypeCd = Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED;
			}
			else {
				shiptoTypeCd = Constant.SHIPTO_TYPE_STANDARD;
			}
			ShiptoTypeBsEntity type = referentialDataService.findBsEntity(ShiptoTypeBsEntity.class, shiptoTypeCd);
			shipto.setShiptoType(type);
			SignatureTypeBsEntity signature = referentialDataService.findBsEntity(SignatureTypeBsEntity.class, Long.valueOf(Constant.SIGNATURE_TYPE_MANDATORY));
			shipto.setSignatureType(signature);
			shipto.setSilos(new ArrayList<ShiptoSiloEntity>());
			// if opening times are empty -> set default from sales area or, if missing, from config table
			SareaEntity sarea = null;
			SapBpEntity soldto = fetchSoldtoSapBpBySapId(soldtoSapId);
			List<SareaEntity> salesAreas = fetchSalesAreas(shipto, soldto.getSapBpId());
			if (salesAreas != null && !salesAreas.isEmpty()) {
				sarea = (SareaEntity) salesAreas.get(0);
			}
			else {
				LOG.error("There is no active sales area for the shiptoSapId: " + shiptoSapId + 
						" and soldtoSapId: " + soldtoSapId + ".");
				throw new LogonException(Reason.RES_ERROR_DISPOKARTE_NO_ACTIVE_SAREA_FOR_SHIPTO);
			}
						
			Long open1From = getShiptoOpenTimeSareaOrConfig(sarea.getShiptoOpen1From(), new Long(
					configService.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1FROMMIN)));
			shipto.setOpen1From(open1From);

			Long open1To = getShiptoOpenTimeSareaOrConfig(sarea.getShiptoOpen1To(), new Long(
					configService.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1TOMIN)));
			shipto.setOpen1To(open1To);

			Long open2From = getShiptoOpenTimeSareaOrConfig(sarea.getShiptoOpen2From(), new Long(
					configService.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2FROMMIN)));
			shipto.setOpen2From(open2From);

			Long open2To = getShiptoOpenTimeSareaOrConfig(sarea.getShiptoOpen2To(), new Long(
					configService.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2TOMIN)));
			shipto.setOpen2To(open2To);

			shipto.setCoordLat(Constant.DEFAULT_COORD_LAT);
			shipto.setCoordLon(Constant.DEFAULT_COORD_LON);
	    	shipto.setGeofenceRadius(new Long(configService.getProperty(ConfigBean.CFG_APPDEFAULTSHIPPINGPOINTSHIPTOGEOFENCERADIUSM)));

			shipto.setShiptoPersonList(new ArrayList<ShiptoPersonEntity>());

			shipto = mergeShipto(shipto, null);
			
			return result = shipto;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private Long getShiptoOpenTimeSareaOrConfig(Long sareaValue, Long configValue) {
		Long result;
    	if (sareaValue != null) {
    		result = sareaValue;
    	}
    	else {
    		result = configValue;
    	}
    	return result;
	}
	
	/**
	 * Calls initialize for all the affected collections related to the shipto sales areas.
	 * 
	 * @param shiptoSalesAreas
	 */
	private void initializeShiptoSareas(Collection shiptoSalesAreas) {
		
		Iterator i = shiptoSalesAreas.iterator();
		while (i.hasNext()) {
			ShiptoSareaEntity shiptoSarea = (ShiptoSareaEntity) i.next();
			shiptoSarea.initialize();
			initializeShipto(shiptoSarea.getShipto());
			shiptoSarea.getSarea().initialize();
			if ( shiptoSarea.getRestrictions() == null ) {
				continue;
			}
			ListIterator restrictionIt = shiptoSarea.getRestrictions().listIterator();
			while (restrictionIt.hasNext()) {
				ShiptoSareaRestrEntity restr = (ShiptoSareaRestrEntity) restrictionIt.next();
				restr.initialize();
			}
		}
	}
	
	private void initializeShipto(ShiptoEntity shipto) {
		
		shipto.initialize();
		if (shipto.getSilos() != null && !shipto.getSilos().isEmpty()){
			Iterator it = shipto.getSilos().iterator();
			while (it.hasNext()) {
				ShiptoSiloEntity object = (ShiptoSiloEntity) it.next();
				if (object.getRfidobject() != null ) {
					object.getRfidobject().initialize();
				}
			}
		}
	}

	/**
	 * Fetch Shipto by BpId for editing
	 * @param bpId
	 * @return ShiptoEntity
	 */
	public ShiptoEntity fetchAndInitializeShiptoBySapId(String sapId) {
		// [[
		ShiptoEntity result = null;
		Object[][] params = {{"sapId", sapId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAndInitializeShiptoByBpId", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			Filter f = new FilterImpl();
			f.addExpression( Restrictions.eq( ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_SAP_ID, sapId) );
			f.addExpression( Restrictions.eq( ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE) );
			
			ShiptoEntity shipto = shiptoDao.findSingle(f);
			if (shipto == null) {
				
				return result = shipto;
			}
			initializeShipto(shipto);
			
			return result = shipto;
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
	 * Fetch Shipto by BpId
	 * @param bpId
	 * @return ShiptoEntity
	 */
	public ShiptoEntity fetchShiptoBySapId(String sapId) {
		// [[
		ShiptoEntity result = null;
		Object[][] params = {{"sapId", sapId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoByBpId", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			Filter<ShiptoEntity> shiptoEntityFilter = new FilterImpl<ShiptoEntity>();
			shiptoEntityFilter.addExpression( Restrictions.eq( ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_SAP_ID, sapId) );
			shiptoEntityFilter.addExpression( Restrictions.eq( ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE) );
			
			return result = shiptoDao.findSingle(shiptoEntityFilter);
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
	 * Fetch Shipto by SAP interface id. The interface id contains the leading zeros but basically corresponds with the 
	 * sap_id.
	 * 
	 * @param sapIfcId
	 * @return
	 */
	public ShiptoEntity fetchShiptoBySapIfcId(String sapIfcId) {
		// [[
		ShiptoEntity result = null;
		Object[][] params = {{"sapIfcId", sapIfcId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoBySapIfcId", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			Filter f = new FilterImpl();
			f.addExpression( Restrictions.eq( ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_SAP_IFC_ID, sapIfcId) );
			f.addExpression( Restrictions.eq( ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE) );
			
			return result = shiptoDao.findSingle(f);
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
	 * Fetch Soldto by SAP interface id. The interface id contains the leading zeros but basically corresponds with the 
	 * sap_id.
	 * @param id
	 * @return SapBpEntity matching the query
	 */
	public SapBpEntity fetchSoldtoSapBpBySapIfcId(String sapIfcId) {
		// [[
		SapBpEntity result = null;
		Object[][] params = {{"sapIfcId", sapIfcId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldtoSapBpBySapIfcId", params);
		}
		// ]]
		try {
			ReadWriteDao<SapBpEntity> sapBpDao = getDaoProvider().getSapBpEntityDao();
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, Boolean.TRUE));
			f.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_IFC_ID, sapIfcId));
			return result = sapBpDao.findSingle(f);
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
	 * merge a ShipTo
	 * @param ShiptoEntity
	 */
	public ShiptoEntity mergeShipto(ShiptoEntity shipTo, List shiptoSalesAreas) {
		// [[
		ShiptoEntity result = null;
		Object[][] params = {{"shipTo", shipTo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mergeShipto", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			//need to merge/persist the old/new attached rf objects with the related tags
			List<ShiptoSiloEntity> silos = shipTo.getSilos();
			if (silos != null && !silos.isEmpty()) {
				Iterator it = silos.iterator();
				while (it.hasNext()) {
					ShiptoSiloEntity silo = (ShiptoSiloEntity) it.next();					
					RfidobjectEntity rfidobject = silo.getRfidobject();
					AbstractRFIDBean bean = RFIDBean.ShiptoSiloRFIDBean(silo);
					if (rfidobject != null) {
						rfidobject = rfidService.mergeRfIdObject(bean, Constant.RFIDOBJECT_TYPE_SHIPTO_SILO);
						silo.setRfidobject(rfidobject);
					}
				}
			}
			if (shiptoSalesAreas != null) {
				for (Iterator it=shiptoSalesAreas.iterator();it.hasNext();) {
					ShiptoSareaEntity shiptoSarea = (ShiptoSareaEntity)it.next();
					getDaoProvider().getShiptoSareaEntityDao().merge(shiptoSarea, false);
				}
			}
			
			updateTruckRoutesToShiptoCoords(shipTo);
			
			shipTo = shiptoDao.merge(shipTo);
			if (shipTo == null) {
				
				return result = shipTo;
			}
			initializeShipto(shipTo);
			
			return result = shipTo;
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
	 * fetch sales areas for ship-to and sold-to
	 * @param shipto not null
	 * @param soldtoSapBpId not null
	 * @return List of sales areas
	 */
	public List<SareaEntity> fetchSalesAreas(ShiptoEntity shipto, Long soldtoSapBpId) {
		// [[
		List<SareaEntity> result = null;
		Object[][] params = {{"shipto", shipto},
							{"soldtoSapBpId", soldtoSapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSalesareas", params);
		}
		// ]]
		try {
			final SapBpEntity shiptoSapBp = shipto.getShiptoSapBp();
			
			final List<Long> commonSalesAreas = 
					getCommonSalesAreaIdsForShiptoAndSoldtoId(shiptoSapBp.getSapBpId(), soldtoSapBpId);
			
			return fetchSalesAreas(commonSalesAreas);
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
	 * fetches and initializes the sales area entities for given sales area ids
	 * @param sareaIds not null
	 * @return list of initialized sales areas
	 */
	public List<SareaEntity> fetchSalesAreas(List<Long> sareaIds)
	{
		// [[
		List<SareaEntity> result = null;
		Object[][] params = {{"sareaIds", sareaIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSalesAreas", params);
		}
		// ]]
		try {
			if (sareaIds.isEmpty()) {
				return result = new ArrayList<SareaEntity>();
			}
			
			final ReadWriteDao<SareaEntity> dao = getDaoProvider().getSareaEntityDao();
			final Filter<SareaEntity> f = dao.createFilter();
			
			f.addExpression(Restrictions.in(SareaEntity.ATTR_SAREA_ID, sareaIds));
			
			final List<SareaEntity> sareas = dao.find(f);
						
			for (SareaEntity sarea : sareas) {
				sarea.initialize();
			}
			
			return result = sareas;
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
	 * Retrieve the sales areas for which a partner function exists between the ship-to and the sold-to.
	 * In case no sold-to specified, returns all sales areas for the ship-to.
	 * TODO this should be solved differently, make sure that sold-to is always specified!!!
	 * @param shiptoSapBpId not null
	 * @param soldtoSapBpId not null
	 * @return list of sales area ids in which the sold-to and the ship-to are linked (not null)
	 */
	public List<Long> getCommonSalesAreaIdsForShiptoAndSoldtoId(Long shiptoSapBpId, Long soldtoSapBpId) {
		// [[
		List<Long> result = null;
		Object[][] params = {{"shiptoSapBpId", shiptoSapBpId},
							{"soldtoSapBpId", soldtoSapBpId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCommonSalesAreasForShiptoSoldto", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SoldshipSareaFlexSrchEntity> dao = getDaoProvider().getSoldshipSareaFlexSrchEntityDao();
			final Filter<SoldshipSareaFlexSrchEntity> f = dao.createFilter();
			
			f.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID,shiptoSapBpId));
			f.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID,soldtoSapBpId));
			
			final JPAQuery<SoldshipSareaFlexSrchEntity> jpaQuery = dao.createJPAQuery();
			jpaQuery.setFilter(f);
			jpaQuery.addSelect(SelectExpression.simpleSelect(SoldshipSareaFlexSrchEntity.ATTR_SAREA_ID));
			jpaQuery.setDistinct(true);
			
			@SuppressWarnings("unchecked")
			final List<Long> sareaIds = dao.find(jpaQuery);
			
			return result = sareaIds;
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
	 * 
	 * @param sapBpId
	 * @param sareaIdList optional (if null or empty, not used for filtering)
	 * @return
	 */
	public List<SapBpSareaEntity> fetchSapBpSareasForSapBp(Long sapBpId, List sareaIdList) {
		// [[
		List<SapBpSareaEntity> result = null;
		Object[][] params = {{"sapBpId", sapBpId},
							{"sareaIdList", sareaIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapBpSareasForSapBp", params);
		}
		// ]]
		try {
			if (sareaIdList == null || sareaIdList.isEmpty()) {
				return result = new ArrayList<SapBpSareaEntity>();
			}
			ReadOnlyDao<SapBpSareaEntity> sbsDao = getDaoProvider().getSapBpSareaEntityDao();
			Filter<SapBpSareaEntity> f = sbsDao.createFilter();
			f.addExpression( Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID, sapBpId) );
			f.addExpression( Restrictions.in(SapBpSareaEntity.ATTR_SAREA_ID, sareaIdList) );
			List<SapBpSareaEntity> sapBpSareaList = sbsDao.find(f);
			return result = sapBpSareaList;
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
	 * fetch BpSareaSofficeSgroupEntity
	 * @param shiptoSapBpId
	 * @param sareaId
	 * @return SapBpSareaEntity
	 */
	public BpSareaSofficeSgroupEntity fetchBpSareaSofficeSgroup(long sapBpId, long sareaId) {
		// [[
		BpSareaSofficeSgroupEntity result = null;
		Object[][] params = {{"sapBpId", sapBpId + ""},
							{"sareaId", sareaId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchBpSareaSofficeSgroup", params);
		}
		// ]]
		try {
			ReadOnlyDao<BpSareaSofficeSgroupEntity> dao = getDaoProvider().getBpSareaSofficeSgroupEntityDao();
			
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(BpSareaSofficeSgroupEntity.ATTR_SAREA_ID, sareaId));
			f.addExpression(Restrictions.eq(BpSareaSofficeSgroupEntity.ATTR_SAP_BP_ID, sapBpId));
			return result = dao.findSingle(f);
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
	 * Fethes a list of SapMaterialDescEntity entities by a list of SAP material ids and language from the DB.
	 */
	public List<SapMaterialDescEntity> fetchSapMaterialDescByLang(List<Long> materialIdList, String language) {
		// [[
		List<SapMaterialDescEntity> result = null;
		Object[][] params = {{"materialIdList", materialIdList},
							{"language", language}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialDescByLang", params);
		}
		// ]]
		try {
			if (materialIdList == null || materialIdList.isEmpty()) {
				return result = Collections.<SapMaterialDescEntity>emptyList();
			}
			
			final ReadOnlyDao<SapMaterialDescEntity> dao = getDaoProvider().getSapMaterialDescEntityDao();
			final Filter<SapMaterialDescEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, language));
			filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, materialIdList));
			
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
	 * fetch a list of SapMaterialDescriptions 
	 * @param SapmaterialId
	 * @param language
	 * @return SapMaterialDescriptionEntity
	 */
	public SapMaterialDescriptionEntity fetchSapMaterialDescription(Long materialId, String language) {
		// [[
		SapMaterialDescriptionEntity result = null;
		Object[][] params = {{"materialId", materialId},
							{"language", language}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialDescription", params);
		}
		// ]]
		try {
			ReadWriteDao<SapMaterialDescriptionEntity> mcDao = getDaoProvider().getSapMaterialDescriptionEntityDao();
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq("sapMaterialDescriptionPK.sapMaterialId", materialId ));
			f.addExpression(Restrictions.eq("sapMaterialDescriptionPK.langCd", language ));
			
			result = mcDao.findSingle(f);
			
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
	 * Fetch SapMaterialDescs for a language
	 * @param language
	 * @return List of SapMaterialDescEntities
	 */
	public List fetchSapMaterialDescByLang(String language) {
		// [[
		List result = null;
		Object[][] params = {{"language", language}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialDescByLang", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<SapMaterialDescEntity> dao = getDaoProvider().getSapMaterialDescEntityDao();
			Filter filter = new FilterImpl();
			
			filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, language));
			filter.addExpression(Order.asc(SapMaterialDescEntity.ATTR_TXT));
			
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
	 * Fetch SapMaterialDescs for a language and for a list of sales areas.
	 * Those materials will be returned, which are present at least for one of the sales areas.
	 * @param language
	 * @return List of SapMaterialDescEntities
	 */
	@AdnTrace
	public List<SapMaterialDescEntity> fetchSapMaterialDescByLang(String language,
			ShiptoEntity shipTo, SapBpEntity soldTo, List<Long> materialIds) {
		final List<SareaEntity> salesAreasOfShipto = getSalesAreasOfShipTo(shipTo, soldTo);

		DaoProvider daoProvider = getDaoProvider();
		final ReadOnlyDao<SapMaterialSareaEntity> salesAreaMaterialAssociationsDao = 
				daoProvider.getSapMaterialSareaEntityDao();
		final Filter<SapMaterialSareaEntity> salesAreaMaterialAssociationFilter = 
				salesAreaMaterialAssociationsDao.createFilter();

		SapMaterialUtil.addInSareaListRestriction(salesAreaMaterialAssociationFilter, salesAreasOfShipto);
		salesAreaMaterialAssociationFilter.addExpression(Restrictions.eq(
				SapMaterialSareaEntity.ATTR_SAP_MATERIAL + "."
						+ SapMaterialEntity.ATTR_MARKED_FOR_DELETION, false));
		if (materialIds != null && !materialIds.isEmpty()) {
			salesAreaMaterialAssociationFilter.addExpression(Restrictions.in(
					SapMaterialSareaEntity.ATTR_SAP_MATERIAL_ID, materialIds));
		}
		else if (materialIds != null && materialIds.isEmpty()) {
			return new ArrayList<SapMaterialDescEntity>();
		}

		final List<SapMaterialSareaEntity> salesAreaMaterialAssociations = 
				salesAreaMaterialAssociationsDao.find(salesAreaMaterialAssociationFilter);
		final List<Long> materialIdList = new ArrayList<Long>();

		for (final SapMaterialSareaEntity salesAreaMaterialAssociation : salesAreaMaterialAssociations) {
			SapMaterialEntity materialOfSalesArea = salesAreaMaterialAssociation.getSapMaterial();
			if (materialOfSalesArea != null
					&& !materialOfSalesArea.getMarkedForDeletion()) {
				materialIdList.add(materialOfSalesArea.getSapMaterialId());
			}
		}

		if (materialIdList.isEmpty()) {
			return new ArrayList<SapMaterialDescEntity>(0);
		}

		final ReadOnlyDao<SapMaterialDescEntity> materialDescriptionDao = daoProvider.getSapMaterialDescEntityDao();
		final Filter<SapMaterialDescEntity> materialDescriptionFilter = materialDescriptionDao.createFilter();
		
		materialDescriptionFilter.addExpression(
				Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, language));
		Expression materialRestriction = DbUtils.createSplittedInRestrictionOfIds(
				SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, materialIdList);
		materialDescriptionFilter.addExpression(materialRestriction);
		materialDescriptionFilter.addExpression(Order.asc(SapMaterialDescEntity.ATTR_TXT));

		return materialDescriptionDao.find(materialDescriptionFilter);
	}

	public List<SareaEntity> getSalesAreasOfShipTo(ShiptoEntity shipTo, SapBpEntity soldTo) {
		final List<SareaEntity> salesAreasOfShipto = new ArrayList<SareaEntity>();

		final List<ShiptoSareaEntity> shipToSalesAreaAssociations = fetchShiptoSareas(shipTo,
				soldTo.getSapBpId(), false);
		for (final ShiptoSareaEntity shipToSalesAreaAssociation : shipToSalesAreaAssociations) {
			final SareaEntity salesAreaOfShipto = shipToSalesAreaAssociation.getSarea();
			salesAreasOfShipto.add(salesAreaOfShipto);
		}
		return salesAreasOfShipto;
	}
	
	/**
	 * fetch a list of SapSoldBillSareaSrchEntities 
	 * @param SareaEntity
	 * @param SapBpEntity
	 * @return a list of SapSoldBillSareaSrchEntity
	 */
	public List <SapSoldBillSareaSrchEntity> fetchSapSoldBillSareas(SareaEntity sarea, SapBpEntity soldto) {
		// [[
		List result = null;
		Object[][] params = {{"sarea", sarea},
							{"soldto", soldto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapSoldBillSareas", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapSoldBillSareaSrchEntity> dao = getDaoProvider().getSapSoldBillSareaSrchEntityDao();
			Filter f = new FilterImpl();
			
			f.addExpression(Restrictions.eq(SapSoldBillSareaSrchEntity.ATTR_SAREA_ID, sarea.getSareaId()));
			f.addExpression(Restrictions.eq(SapSoldBillSareaSrchEntity.ATTR_SOLDTO_NR, soldto.getSapId()));
			result = dao.findNRecs(0, 1000, f);
			
			if (result.size() == 1000) {
				LOG.error("Too many Billtos found");
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


	/**
	 * Loads a list of Payer SapBpEntities belonging to the specified Sold To and Sales Area.
	 *
	 * @param sareaId
	 * @param soldtoSapBpId
	 * @adnlog.exclude return
	 */
	public List<SapBpEntity> loadPayers(Long sareaId, Long soldtoSapBpId) {
			return businessPartnerStore.loadPayers(sareaId,soldtoSapBpId);
	}


	/**
	 * fetch a list with all the SapMaterialEntities 
	 * @return a list with all the SapMaterialEntities 
	 * @adnlog.exclude return
	 */
	public List fetchAllMaterials() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllMaterials");
		}
		// ]]
		try {
			ReadWriteDao<SapMaterialEntity> matDao = getDaoProvider().getSapMaterialEntityDao();
			return  matDao.find((Filter)null);
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
	 * Queries shiptos and related sarea_cds are available for them.
	 * @param skip - start from this
	 * @param num - count of records
	 * @param query - String, in case a number it will be used as a search parameter for ID, otherwise looking in all String fields
	 * @return
	 */
	public ListRange fetchShipTos(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShipTos", params);
		}
		// ]]
		try {
			ListRange res = new ListRange();
			List shipTos = new ArrayList();
			Long totalSize = null;
			boolean isDummyQuery = query.startsWith("DMY");
			ReadOnlyDao<SoldshipFlexSrchEntity> dao = getDaoProvider().getSoldshipFlexSrchEntityDao();
			Filter filt = new FilterImpl();
			if (!isDummyQuery) {
				query = replaceWildcards(query);
				filt.addExpression(Restrictions.ilike(SoldshipFlexSrchEntity.ATTR_COMPOSITE_SEARCH_STRING, query));
			}
			else{
				filt.addExpression(Restrictions.or(
						Restrictions.eq(SoldshipFlexSrchEntity.ATTR_SHIPTO_NR, query),
						Restrictions.eq(SoldshipFlexSrchEntity.ATTR_SOLDTO_NR, query)
				)
				);
			}
            filt.addExpression(Order.asc(SoldshipFlexSrchEntity.ATTR_SHIPTO_NAME));
			shipTos = dao.findNRecs(skip, num, filt);
			totalSize = dao.count(filt);
			res.setData(shipTos.toArray());
			res.setTotalSize(totalSize);
			
			return result = res;
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
	 * Queries shiptos and related sarea_cds are available for them. Uses activeCem & activeAgg parameters.
	 * @param skip - start from this
	 * @param num - count of records
	 * @param query - String, in case a number it will be used as a search parameter for ID, otherwise looking in all String fields
	 * @param activeCem if true, only values with active CEM contracts will be returned
	 * @param activeAgg if true, only values with active AGG contracts will be returned
	 * @return
	 */
	@AdnTrace
	public ListRange fetchShipTos(int skip, int num, String query, boolean activeCem, boolean activeAgg) {
		boolean isDummyQuery = query.startsWith("DMY");
		ReadOnlyDao<SoldshipFlexSrchEntity> dao = getDaoProvider().getSoldshipFlexSrchEntityDao();
		Filter<SoldshipFlexSrchEntity> filter = new FilterImpl<>();
		if (!isDummyQuery) {
			query = replaceWildcards(query);
			filter.addExpression(Restrictions.ilike(SoldshipFlexSrchEntity.ATTR_COMPOSITE_SEARCH_STRING, query));
		}
		else{
			filter.addExpression(Restrictions.or(
					Restrictions.eq(SoldshipFlexSrchEntity.ATTR_SHIPTO_NR, query),
					Restrictions.eq(SoldshipFlexSrchEntity.ATTR_SOLDTO_NR, query)
					)
			);
		}

		final Date today = DateTimeUtils.getMorningOfDay(new Date());
		final Expression activeCemExpression = Restrictions.and(
				Restrictions.isNotNull(SoldshipBestFlexSrchEntity.ATTR_CEM_VALID_UNTIL_DAT),
				Restrictions.ge(SoldshipBestFlexSrchEntity.ATTR_CEM_VALID_UNTIL_DAT, today)
		);
		final Expression activeAggExpression = Restrictions.and(
				Restrictions.isNotNull(SoldshipBestFlexSrchEntity.ATTR_AGG_VALID_UNTIL_DAT),
				Restrictions.ge(SoldshipBestFlexSrchEntity.ATTR_AGG_VALID_UNTIL_DAT, today)
		);

		// --- With active CEM contract or active AGG contract (when both checkboxes are selected)
		if (activeCem && activeAgg) {
			filter.addExpression(Restrictions.or(
					activeCemExpression,
					activeAggExpression
			));
		}
		// --- With active CEM contract
		else if (activeCem) {
			filter.addExpression(activeCemExpression);
		}
		// --- With active AGG contract
		else if (activeAgg){
			filter.addExpression(activeAggExpression);
		}

		filter.addExpression(Order.asc(SoldshipFlexSrchEntity.ATTR_SHIPTO_NAME));
		List<SoldshipFlexSrchEntity> shipTos = dao.findNRecs(skip, num, filter);
		long totalSize = dao.count(filter);
		ListRange result = new ListRange();
		result.setData(shipTos.toArray());
		result.setTotalSize(totalSize);

		return result;
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
	 * fetch a list of contact persons for a shipTo 
	 * @param sapShiptoId 
	 * @return a list with UserEntities
	 * @adnlog.exclude return 
	 */
	public List fetchContactPersons(Long shipToId) {
		// [[
		Object[][] params = {{"shipToId", shipToId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContactPersons", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoPersonEntity> shiptoPersonDao = getDaoProvider().getShiptoPersonEntityDao();
			
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(ShiptoPersonEntity.ATTR_SHIPTO_ID, shipToId));

			return shiptoPersonDao.find(f);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	@AdnTrace
	public List<PersonEntity> fetchPerson(String name, String firstName) {
		ReadWriteDao<PersonEntity> personDao = getDaoProvider().getPersonEntityDao();
		Filter<PersonEntity> personFilter = new FilterImpl<PersonEntity>();
		personFilter.addExpression(Restrictions.eq(PersonEntity.ATTR_ACTIVE_FLAG, true));
		if (!StringUtils.empty(name)) {
			personFilter.addExpression(Restrictions.ilike(PersonEntity.ATTR_NAME, '%' + name + '%'));
		}
		if (!StringUtils.empty(firstName)) {
			personFilter.addExpression(Restrictions.ilike(PersonEntity.ATTR_FIRSTNAME, '%' + firstName + '%'));
		}
		return personDao.find(personFilter);
	}
	
	/**
	 * Persist a ShiptoPersonEntity
	 * @param ShiptoPersonEntity to persist
	 */
	public ShiptoPersonEntity persistShiptoPerson(ShiptoPersonEntity shipToPerson) {
			// [[
			ShiptoPersonEntity result = null;
			Object[][] params = {{"shipToPerson", shipToPerson}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "persistShiptoPerson", params);
			}
			// ]]
			try {
				ReadWriteDao<ShiptoPersonEntity> shiptoPersonDao = getDaoProvider().getShiptoPersonEntityDao();
				return result = shiptoPersonDao.persist(shipToPerson);
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
	 * Persist a PersonEntity
	 * @param PersonEntity to persist
	 */
	public PersonEntity persistPerson(PersonEntity person) {
			// [[
			PersonEntity result = null;
			Object[][] params = {{"person", person}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "persistPerson", params);
			}
			// ]]
			try {
				ReadWriteDao<PersonEntity> personDao = getDaoProvider().getPersonEntityDao();
				return result = personDao.persist(person);
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
	 * Merge a PersonEntity
	 * @param PersonEntity to merge
	 * @return PersonEntity
	 */
	public PersonEntity mergePerson(PersonEntity person) {
			// [[
			PersonEntity result = null;
			Object[][] params = {{"person", person}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "mergePerson", params);
			}
			// ]]
			try {
				ReadWriteDao<PersonEntity> personDao = getDaoProvider().getPersonEntityDao();
				return result = personDao.merge(person);
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
	 * fetch a list of ShiptoBilltoEntities by shiptoId 
	 * @param shiptoId
	 * @return a list of ShiptoBilltoEntities
	 */
	public List fetchShiptoBilltosByShiptoId(Long shiptoId) {
		// [[
		List result = null;
		Object[][] params = {{"shiptoId", shiptoId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoBilltosByShiptoId", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoBilltoEntity> billDao = getDaoProvider().getShiptoBilltoEntityDao();
			
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_SHIPTO_SAREA + "." + ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_ID, shiptoId));
			
			return result = billDao.find(f);
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
	 * fetch a list of ShiptoBilltoEntities by shiptoId and sareaId
	 * @param shiptoId
	 * @param sareaId
	 * @return a list of ShiptoBilltoEntities
	 */
	public List fetchShiptoBilltos(Long shiptoId, Long sareaId) {
		// [[
		List result = null;
		Object[][] params = {{"shiptoId", shiptoId},
							{"sareaId", sareaId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoBilltos", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoBilltoEntity> billDao = getDaoProvider().getShiptoBilltoEntityDao();
			
			Filter f = new FilterImpl();
			f.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_SHIPTO_SAREA + "." + ShiptoSareaEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_ID, shiptoId));
			f.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_SHIPTO_SAREA + "." + ShiptoSareaEntity.ATTR_SAREA + "." + SareaEntity.ATTR_SAREA_ID, sareaId));
			
			return result = billDao.find(f);
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
	 * check which Billto belongs to a restriction at a given date (based on the billto selection 
	 * rhythm) 
	 * @param current date
	 * @param restriction
	 * @return SapBpId of the appropriate billto 
	 */
	
	public Long findBilltoByRhythm(Calendar currentDate, Long shiptoSareaRestrId) {
		// [[
		Long result = null;
		Object[][] params = {{"currentDate", currentDate},
							{"shiptoSareaRestrId", shiptoSareaRestrId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findBilltoByRhythm", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			ReadWriteDao<ShiptoSareaRestrEntity> shiptoSareaRestrEntityDao = daoProvider.getShiptoSareaRestrEntityDao();
			ShiptoSareaRestrEntity restriction = shiptoSareaRestrEntityDao.findById(shiptoSareaRestrId);
			
			Long billtoSapBpId = null;
			BilltoRuletypeBsEntity billtoRuletype = restriction.getBilltoRuletype();
			if (billtoRuletype == null) {
				return result = null;
			}
			Long ruleTypeCd = billtoRuletype.getBilltoRuletypeCd();
			List<ShiptoSareaRestrBtrulEntity> rules = restriction.getRules();
			if (rules == null || rules.isEmpty()) {
				return result = null;
			}
			boolean isMonthRule = ruleTypeCd.equals(RESTRICTION_RULE_TYPE_MONTH);
			boolean isDayOfWeekRule = ruleTypeCd.equals(RESTRICTION_RULE_TYPE_DAY_OF_WEEK);
			boolean isDayOfMonthRule = ruleTypeCd.equals(RESTRICTION_RULE_TYPE_DAY_OF_MONTH);
			for (ShiptoSareaRestrBtrulEntity rule : rules) {
				BilltoRuletypeBsEntity currentBilltoRuletype = rule.getBilltoRuletype();
				Long currentBilltoRuletypeCd = currentBilltoRuletype.getBilltoRuletypeCd();
				if (!currentBilltoRuletypeCd.equals(ruleTypeCd)) {
					continue;
				}
				Long ruleValue = rule.getValue();
				int ruleIntValue = ruleValue.intValue();
				SapBpEntity ruleBilltoSapBp = rule.getBilltoSapBp();
				Long ruleBilltoSapBpId = ruleBilltoSapBp.getSapBpId();
				int currentMonth = currentDate.get(Calendar.MONTH);
				if (isMonthRule && currentMonth == ruleIntValue) {
						return result = ruleBilltoSapBpId;
				}
				if (isDayOfWeekRule) {
					// in our system mon = 0 ... sun = 6 but in Calendar sun = 1 ... sat = 7
					int convertedDate = ruleIntValue + 2;
					if (convertedDate == 8){
						convertedDate = 1;
					}
					int currentDayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
					if (currentDayOfWeek == convertedDate) {
						return result = ruleBilltoSapBpId;
					}
				}
				int currentDayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
				if (isDayOfMonthRule && currentDayOfMonth == ruleIntValue + 1) {
					return result = ruleBilltoSapBpId;
				}
			}
			return result = billtoSapBpId;
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
	 * Add missing sales areas for the shipto+soldto combination. Only those sales areas are
	 * created, for which there is a partner function in the sales area beetween the shipto+soldto.
	 * 
	 * @param initialzeShiptoSalesAreas in case you send true, you will get back in the list the sales areas initialized. Please check
	 * {@link ShiptoSareaEntity#initialize()} what collections it will initialze, and if you really need all those?
	 */
	public List <ShiptoSareaEntity> updateShiptoSalesAreas(ShiptoEntity shipto, SapBpEntity soldto, 
			boolean initialzeShiptoSalesAreas) {
		
		final List<Long> salesAreaIds = getCommonSalesAreaIdsForShiptoAndSoldtoId(
				shipto.getShiptoSapBp().getSapBpId(), soldto.getSapBpId());
		
		final List<ShiptoSareaEntity> shiptoSalesAreas = fetchShiptoSareas(shipto, salesAreaIds, 
				initialzeShiptoSalesAreas);
		
		final List<SareaEntity> salesAreas = fetchSalesAreas(salesAreaIds);
		
		// Determine new ship-to sales area candidates
		final List<SareaEntity> missingSalesAreas = new ArrayList<SareaEntity>();
		final List<Long> missingSalesAreaIds = new ArrayList<Long>();
		for (SareaEntity sarea : salesAreas) {
			boolean shiptoSareaExists = false;
			for (ShiptoSareaEntity shiptoSarea : shiptoSalesAreas) {
				if (shiptoSarea.getSarea().getSareaId().equals(sarea.getSareaId())) {
					shiptoSareaExists = true;
					break;
				}
			}
			if (!shiptoSareaExists) {
				missingSalesAreas.add(sarea);
				missingSalesAreaIds.add(sarea.getSareaId());
			}
		}
		
		if (!missingSalesAreas.isEmpty()) {
			// Start by querying all data required later on
			final ReadOnlyDao<SapBpSareaEntity> sapBpSareaDao = 
					getDaoProvider().getSapBpSareaEntityDao();
			final Filter<SapBpSareaEntity> sapBpSareaFilter = sapBpSareaDao.createFilter();
			sapBpSareaFilter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID, 
					shipto.getShiptoSapBp().getSapBpId()));
			sapBpSareaFilter.addExpression(Restrictions.in(SapBpSareaEntity.ATTR_SAREA_ID, 
					missingSalesAreaIds));
			sapBpSareaFilter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_MARKED_FOR_DELETION, 
					"0"));
			final List<SapBpSareaEntity> sapBpSareas = sapBpSareaDao.find(sapBpSareaFilter);
			
			final ReadOnlyDao<SapSoldBillSareaSrchEntity> sapSoldBillSareaDao = 
					getDaoProvider().getSapSoldBillSareaSrchEntityDao();
			final Filter<SapSoldBillSareaSrchEntity> sapSoldBillSareaFilter = 
					sapSoldBillSareaDao.createFilter();
			sapSoldBillSareaFilter.addExpression(
					Restrictions.eq(SapSoldBillSareaSrchEntity.ATTR_SOLDTO_SAP_BP_ID, 
							soldto.getSapBpId()));
			sapSoldBillSareaFilter.addExpression(
					Restrictions.in(SapSoldBillSareaSrchEntity.ATTR_SAREA_ID, 
							missingSalesAreaIds));
			final List<SapSoldBillSareaSrchEntity> sapSoldBillSareas = 
					sapSoldBillSareaDao.find(sapSoldBillSareaFilter);
			
			final ReadOnlyDao<SapContractEntity> sapContractDao = 
					getDaoProvider().getSapContractEntityDao();
			final Filter<SapContractEntity> sapContractFilter = sapContractDao.createFilter();
			sapContractFilter.addExpression(Restrictions.or(
					Restrictions.and(
							Restrictions.eq(SapContractEntity.ATTR_SOLDTO_SAP_BP_ID, soldto.getSapBpId()),
							Restrictions.eq(SapContractEntity.ATTR_SHIPTO_SAP_BP_ID, shipto.getShiptoSapBp().getSapBpId())),
					Restrictions.and(
							Restrictions.eq(SapContractEntity.ATTR_SOLDTO_SAP_BP_ID, soldto.getSapBpId()),
							Restrictions.eq(SapContractEntity.ATTR_SHIPTO_SAP_BP_ID, soldto.getSapBpId()))));
			sapContractFilter.addExpression(Restrictions.isNotNull(SapContractEntity.ATTR_BILLTO_SAP_BP_ID));
			sapContractFilter.addExpression(Restrictions.in(SapContractEntity.ATTR_SAREA_ID, missingSalesAreaIds));
			final List<SapContractEntity> sapContracts = sapContractDao.find(sapContractFilter);
			
			// Convert to maps (by sales area id) for ease of access further down
			final Map<Long, SapBpSareaEntity> sapBpSareaMap = new HashMap<Long, SapBpSareaEntity>();
			for (SapBpSareaEntity sapBpSarea : sapBpSareas) {
				sapBpSareaMap.put(sapBpSarea.getSarea().getSareaId(), sapBpSarea);
			}
			
			final Map<Long, Set<Long>> billtoPartnerMap = new HashMap<Long, Set<Long>>();
			for (SapSoldBillSareaSrchEntity sapSoldBillSarea : sapSoldBillSareas) {
				Long sareaId = sapSoldBillSarea.getSareaId();
				if (!billtoPartnerMap.containsKey(sareaId)) {
					billtoPartnerMap.put(sareaId, new TreeSet<Long>());
				}
				billtoPartnerMap.get(sareaId).add(sapSoldBillSarea.getBilltoSapBpId());
			}

			final Comparator<SapBpEntity> sapBpEntityComparator = new Comparator<SapBpEntity>() {
				public int compare(SapBpEntity bp1, SapBpEntity bp2) {
					return bp1.getSapBpId().compareTo(bp2.getSapBpId());
				}
			};

			final Map<Long, Set<SapBpEntity>> contractHeaderBilltoMap = 
					new HashMap<Long, Set<SapBpEntity>>();
			for (SapContractEntity sapContract : sapContracts) {
				Long sareaId = sapContract.getSarea().getSareaId();
				if (!contractHeaderBilltoMap.containsKey(sareaId)) {
					contractHeaderBilltoMap.put(sareaId, new TreeSet<SapBpEntity>(sapBpEntityComparator));
				}
				contractHeaderBilltoMap.get(sareaId).add(sapContract.getBilltoSapBp());
			}
			
			for (SareaEntity sarea : missingSalesAreas) {
				Long sareaId = sarea.getSareaId();
				
				ShiptoSareaEntity shiptoSarea = new ShiptoSareaEntity();
				
				shiptoSarea.setShipto(shipto);
				shiptoSarea.setSarea(sarea);
				shiptoSarea.setRestrictions(new LinkedList<ShiptoSareaRestrEntity>());
				shiptoSarea.setFreeDeliveryFlag(Boolean.FALSE);
				shiptoSarea.setReleaseSapOptionalFlag(Boolean.FALSE);
				shiptoSarea.setDbltActiveFlag(Boolean.FALSE);
				shiptoSarea.setSdltAndNdltActive(Boolean.FALSE);
				
				final SapBpSareaEntity sapBpSarea = sapBpSareaMap.get(sareaId);
				shiptoSarea.setDistributionDest(sapBpSarea.getDistributionDest());
				
				// 24-Jul-2013/stich: see also LOGON-6692
				// initialization of ship-to bill-tos based on contract header bill-tos
				final Set<Long> billtoPartners = billtoPartnerMap.get(sareaId);
				final Set<SapBpEntity> contractHeaderBilltos = contractHeaderBilltoMap.get(sareaId);
				
				final List<ShiptoBilltoEntity> shiptoBilltos = new ArrayList<ShiptoBilltoEntity>();
				if (contractHeaderBilltos != null && billtoPartners != null) {
					for (SapBpEntity billto : contractHeaderBilltos) {
						if (billtoPartners.contains(billto.getSapBpId())) {
							final ShiptoBilltoEntity shiptoBillto = new ShiptoBilltoEntity();
							shiptoBillto.setBilltoSapBp(billto);
							shiptoBillto.setShiptoSarea(shiptoSarea);
							shiptoBilltos.add(shiptoBillto);
						}
					}
				}
				
				shiptoSarea.setBilltos(shiptoBilltos);
				
				shiptoSarea = getDaoProvider().getShiptoSareaEntityDao().persist(shiptoSarea);
				shiptoSalesAreas.add(shiptoSarea);
			}
		}
		
		return shiptoSalesAreas;
	}



	
	/**
	 * fetch a shiptoWagonType by id 
	 * @param shiptoWagonTypeId
	 * @return ShiptoWagonType
	 */
	public ShiptoWagonTypeEntity fetchShiptoWagonTypeById(Long shiptoWagonTypeId) {
		// [[
		ShiptoWagonTypeEntity result = null;
		Object[][] params = {{"shiptoWagonTypeId", shiptoWagonTypeId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoWagonTypeById", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoWagonTypeEntity> swtDao = getDaoProvider().getShiptoWagonTypeEntityDao();
			return result = swtDao.findById(shiptoWagonTypeId);
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
	 * fetch a shiptoTruckType by id 
	 * @param shiptoTruckTypeId
	 * @return ShiptoTruckType
	 */
	private ShiptoTruckTypeEntity fetchShiptoTruckTypeById(Long shiptoTruckTypeCemId) {
		// [[
		ShiptoTruckTypeEntity result = null;
		Object[][] params = {{"shiptoTruckTypeId", shiptoTruckTypeCemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoTruById", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoTruckTypeEntity> sttDao = getDaoProvider().getShiptoTruckTypeEntityDao();
			return result = sttDao.findById(shiptoTruckTypeCemId);
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
	 * Inserts/updates/deletes entries in tlgnc_shipto_truck_type. Basically merges changes made on Dispokarte.
	 * @param shiptoTruckCemToPersistList
	 * @param shiptoTruckCemToMergeList
	 * @param shiptoTruckCemToDeleteList
	 */
	public List <ShiptoTruckTypeEntity> handleTruckTypes(List shiptoTruckToPersistList, List shiptoTruckToMergeList,
			List shiptoTruckToDeleteList) {
		// [[
		List<ShiptoTruckTypeEntity> result = null;
		Object[][] params = {{"shiptoTruckToPersistList", shiptoTruckToPersistList},
							{"shiptoTruckToMergeList", shiptoTruckToMergeList},
							{"shiptoTruckToDeleteList", shiptoTruckToDeleteList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleTruckTypes", params);
		}
		// ]]
		try {
			List resultList = new ArrayList();
			ReadWriteDao<ShiptoTruckTypeEntity> truckDao = getDaoProvider().getShiptoTruckTypeEntityDao();
			
			ListIterator truckDeleteIt = shiptoTruckToDeleteList.listIterator();
			while ( truckDeleteIt.hasNext() ) {
				ShiptoTruckTypeEntity te = (ShiptoTruckTypeEntity) truckDeleteIt.next();
				te = fetchShiptoTruckTypeById(te.getShiptoTruckTypeId());
				//te.setShipto(null);
				truckDao.remove(te);
			}
			
			ListIterator truckPersistIt = shiptoTruckToPersistList.listIterator(); 
			while ( truckPersistIt.hasNext() ) {
				ShiptoTruckTypeEntity te = (ShiptoTruckTypeEntity) truckPersistIt.next();
				te = truckDao.persist(te);
				resultList.add(te);
			}
			ListIterator truckMergeIt = shiptoTruckToMergeList.listIterator();
			while ( truckMergeIt.hasNext() ) {
				ShiptoTruckTypeEntity te = (ShiptoTruckTypeEntity) truckMergeIt.next();
				te = truckDao.merge(te);
				resultList.add(te);
			}
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
	 * @param shiptoWagonToPersistList
	 * @param shiptoWagonToMergeList
	 * @param shiptoWagonToDeleteList
	 */
	public List handleWagonTypes(List shiptoWagonToPersistList, List shiptoWagonToMergeList,
			List shiptoWagonToDeleteList) {
		// [[
		List result = null;
		Object[][] params = {{"shiptoWagonToPersistList", shiptoWagonToPersistList},
							{"shiptoWagonToMergeList", shiptoWagonToMergeList},
							{"shiptoWagonToDeleteList", shiptoWagonToDeleteList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleWagonTypes", params);
		}
		// ]]
		try {
			List resultList = new ArrayList();
			ReadWriteDao<ShiptoWagonTypeEntity> wagonDao = getDaoProvider().getShiptoWagonTypeEntityDao();
			ListIterator wagonDeleteIt = shiptoWagonToDeleteList.listIterator();
			while ( wagonDeleteIt.hasNext() ) {
				ShiptoWagonTypeEntity te = (ShiptoWagonTypeEntity) wagonDeleteIt.next();
				te = fetchShiptoWagonTypeById(te.getShiptoWagonTypeId());
				//te.setShipto(null);
				wagonDao.remove(te);
			}
			
			ListIterator wagonPersistIt = shiptoWagonToPersistList.listIterator(); 
			while ( wagonPersistIt.hasNext() ) {
				ShiptoWagonTypeEntity te = (ShiptoWagonTypeEntity) wagonPersistIt.next();
				te = wagonDao.persist(te);
				resultList.add(te);
			}
			ListIterator wagonMergeIt = shiptoWagonToMergeList.listIterator();
			while ( wagonMergeIt.hasNext() ) {
				ShiptoWagonTypeEntity te = (ShiptoWagonTypeEntity) wagonMergeIt.next();
				te = wagonDao.merge(te);
				resultList.add(te);
			}
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
	
	@AdnTrace
	public ListRange fetchSapContractDispDtoList(Long selectedShipTo,Long selectedSoldTo, int skip, int count, Locale locale, String orderby, boolean asc,
			boolean showOnlyActiveContracts) {
		// [[
		ListRange result = null;

			ReadOnlyDao<SapContractDispEntity> readOnlyDao = getDaoProvider().getSapContractDispEntityDao();			
			Filter<SapContractDispEntity> filterSSCE = new FilterImpl<SapContractDispEntity>();
			
			if (showOnlyActiveContracts) {
				Date today = new Date();
				today = DateTimeUtils.getMorningOfDay(today);
				filterSSCE.addExpression(
						// Restrictions.ge(SapContractEntity.ATTR_VALID_UNTIL_DAT, today),
						Restrictions.or(
								Restrictions.eq(SapContractDispEntity.ATTR_VALID_UNTIL_IGNORE_FLAG, true),
										Restrictions.ge(SapContractDispEntity.ATTR_VALID_UNTIL_DAT, today)));
			}
			
			filterSSCE.addExpression(
					Restrictions.or(
						Restrictions.and(
							Restrictions.eq(SapContractDispEntity.ATTR_SHIPTO_SAP_BP_ID, selectedShipTo),
							Restrictions.eq(SapContractDispEntity.ATTR_SOLDTO_SAP_BP_ID, selectedSoldTo)
						),
						//generic shipto contracts also have to come in (this contract is identifiable that shipto equals soldto)
						Restrictions.and(
							Restrictions.eq(SapContractDispEntity.ATTR_SHIPTO_SAP_BP_ID, selectedSoldTo),
							Restrictions.eq(SapContractDispEntity.ATTR_SOLDTO_SAP_BP_ID, selectedSoldTo)
						)
					)
			);
			
			String tmpOrder = SapContractDispDto.convertOrderByColumn(orderby);
			filterSSCE.addExpression(asc ? Order.asc(tmpOrder): (Order.desc(tmpOrder)));									
			List<SapContractDispEntity> sapContractEntities = readOnlyDao.findNRecs(skip, count, filterSSCE);			
			ListRange list = new ListRange();
			if (sapContractEntities.isEmpty()) {
				list.setData(new Object[] {});
				list.setTotalSize(0);
			}
			else {
				List<SapContractDispDto> dtoList = new ArrayList<SapContractDispDto>();
				ServiceLocator serviceLocator = new ServiceLocator(getConfigBean().getLogonProperties().getProviderurl(), false);
				
				Map<Long, TimeZone> shipToTimeZones = serviceLocator.getShiptoService().fetchShiptoTimeZonesForSapBpIds(new HashSet<Long>(Arrays.asList(selectedShipTo)), locale);
				TimeZone shipToTimeZone = shipToTimeZones.get(selectedShipTo);
				
				for (SapContractDispEntity sapContractDispEntity : sapContractEntities) {
					SapContractDispDto sapContractDispDto = new SapContractDispDto(sapContractDispEntity);
					
					sapContractDispDto.setValidFromDatInStringWithOffset(
							shipToTimeZone.fmtAsDayMonthYearHourMinuteWithOffset(sapContractDispDto.getValidFromDat()));
					sapContractDispDto.setValidUntilDatInStringWithOffset(
							shipToTimeZone.fmtAsDayMonthYearHourMinuteWithOffset(sapContractDispDto.getValidUntilDat()));
					
					dtoList.add(sapContractDispDto);
				}
				list.setData(dtoList.toArray());
				list.setTotalSize(readOnlyDao.count(filterSSCE));
			}			
			return result = list;
	}
	
	@AdnTrace
	public ListRange fetchSapContractItemDispEntityList(Long sapContractId, Locale locale, int skip, int count, String orderby, boolean asc) {


			ReadOnlyDao<SapContractItemDispEntity> readOnlyDao = getDaoProvider().getSapContractItemDispEntityDao();			
			Filter<SapContractItemDispEntity> filterSSCE = new FilterImpl<SapContractItemDispEntity>();
			filterSSCE.addExpression(asc ? Order.asc(orderby): (Order.desc(orderby)));
			filterSSCE.addExpression(Restrictions.eq(SapContractItemDispEntity.ATTR_SAP_CONTRACT_ID, sapContractId));
			filterSSCE.addExpression(Restrictions.eq(SapContractItemDispEntity.ATTR_LANG_CD, locale.getLanguage()));
			filterSSCE.addExpression(Restrictions.eq(SapContractItemDispEntity.ATTR_IS_AP_GROUP_LEADER, Boolean.FALSE));
			
			List<SapContractItemDispEntity> sapContractItemEntities = readOnlyDao.findNRecs(skip, count, filterSSCE);
			ListRange list = new ListRange();
			if (sapContractItemEntities.isEmpty()) {
				list.setData(new Object[] {});
				list.setTotalSize(0);
			}
			else {
				list.setData(sapContractItemEntities.toArray());
				list.setTotalSize(readOnlyDao.count(filterSSCE));
			}			
			return list;
		
	}

	/**
	 * Currently only used by the Contract Item Data panel.
	 */
	public SapContractItemEntity fetchSapContractItem(Long sapContractItemId) {
		// [[
		SapContractItemEntity result = null;
		Object[][] params = {{"sapContractItemId", sapContractItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapContractItem", params);
		}
		// ]]
		try {
			ReadWriteDao<SapContractItemEntity> scieDao = getDaoProvider().getSapContractItemEntityDao();
			return result = scieDao.findById(sapContractItemId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public List<ContractItemOrderItemListEntity> fetchContractItemOrderItemList(Long sapContractItemId) {
		// [[
		List<ContractItemOrderItemListEntity> result = null;
		Object[][] params = {{"sapContractItemId", sapContractItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContractItemOrderItemList", params);
		}
		// ]]
		try {
			ReadWriteDao<ContractItemOrderItemListEntity> dao = getDaoProvider().getContractItemOrderItemListEntityDao();
			Filter<ContractItemOrderItemListEntity> filter = new FilterImpl<ContractItemOrderItemListEntity>();
			filter.addExpression(Restrictions.eq(ContractItemOrderItemListEntity.ATTR_SAP_CONTRACT_ITEM_ID, sapContractItemId));			
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
	 * Currently only used by the Contract Item Data panel.
	 */
	public UomTxEntity fetchUomTxEntity(String uomCd, Locale locale) {
		return referentialDataService.findTxEntity(UomTxEntity.class, locale, uomCd);
	}
	
	/**
	 * Returns all distribution destinations, which are available for a normal shipto sales areas.
	 */
	public List<DistributionDestTxEntity> fetchDistributionDestTxEntity( Locale locale ) {
		Filter <DistributionDestTxEntity> filt = new FilterImpl<DistributionDestTxEntity>();
		filt.addExpression(Restrictions.eq("distributionDestTxPK.distributionDestBs.selectableForShiptoFlag", Boolean.TRUE));
		filt.addExpression(Restrictions.eq("distributionDestTxPK.langCd", locale.getLanguage()));
		return referentialDataService.findTxEntityFiltered(DistributionDestTxEntity.class, filt);
	}
	
	public Filter getDispocardSearchFilter(HashMap searchCrit) {
		// [[
		Filter result = null;
		Object[][] params = {{"searchCrit", searchCrit}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDispocardSearchFilter", params);
		}
		// ]]
		try {
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ShiptoSareaRestrSearEntity.ATTR_LANG_CD, searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_LANG_CD)));
			
			if (!StringUtils.empty((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_MATERIAL_TXT))) {
				filter.addExpression(Restrictions.eq(ShiptoSareaRestrSearEntity.ATTR_MATERIAL_TXT, Long.valueOf((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_MATERIAL_TXT))));
			}
			
			if (!StringUtils.empty((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_SAP_MATERIAL_ID))) {
				filter.addExpression(Restrictions.eq(ShiptoSareaRestrSearEntity.ATTR_SAP_MATERIAL_ID, Long.valueOf((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_SAP_MATERIAL_ID))));
			}
			
			if (!StringUtils.empty((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_POOL_ID))) {
				filter.addExpression(Restrictions.eq(ShiptoSareaRestrSearEntity.ATTR_POOL_ID, Long.valueOf((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_POOL_ID))));
			}
			
			if (!StringUtils.empty((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_COMM_CARRIER_ID))) {
				filter.addExpression(Restrictions.eq(ShiptoSareaRestrSearEntity.ATTR_COMM_CARRIER_ID, Long.valueOf((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_COMM_CARRIER_ID))));
			}
			
			if (!StringUtils.empty((String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_PLANT_CD))) {
				filter.addExpression(Restrictions.eq(ShiptoSareaRestrSearEntity.ATTR_PLANT_CD, (String)searchCrit.get(ShiptoSareaRestrSearEntity.ATTR_PLANT_CD)));
			}
			
			if (!StringUtils.empty((String)searchCrit.get("deliveryTypeCdList"))) {
				String[] attr = ((String)searchCrit.get("deliveryTypeCdList")).split(",");
				List deliveryTypeList = new ArrayList();
				for (int i=0; i<attr.length; i++) {
					deliveryTypeList.add(Long.valueOf(attr[i]));
				}
				
				filter.addExpression(Restrictions.in(ShiptoSareaRestrSearEntity.ATTR_DELIVERY_TYPE_CD, deliveryTypeList));
			}
			
			return result = filter;
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
	 * 
	 * @param skip
	 * @param num
	 * @param sort
	 * @param asc
	 * @param searchCrit
	 * @param locale
	 * @return
	 */
    public ListRange dispocardSearch(int skip, int num, String sort, boolean asc, HashMap searchCrit, Locale locale) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "dispocardSearch", params);
		}
		// ]]
		try {
			ReadOnlyDao<ShiptoSareaRestrSearEntity> shiptoSareaRestrSearchDao = getDaoProvider().getShiptoSareaRestrSearEntityDao();
			
			Filter filter = getDispocardSearchFilter(searchCrit);
			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			
			List orderList;
			long count;
			orderList = shiptoSareaRestrSearchDao.findNRecs(skip, num, filter);
			if ( (skip == 0) && (orderList.size() < num) ) {
				count = orderList.size();
			}
			else {
				count = shiptoSareaRestrSearchDao.count(filter);
			}
			
			result = new ListRange();
			result.setTotalSize(count);
			result.setData(orderList.toArray());
			
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
     * Used by the Dispocard-View Search screen / Carrier filter
     * 
     * @param skip
     * @param num
     * @return
     */
	public ListRange fetchCarrierList(int skip, int num, String query) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialList", params);
		}
		// ]]
		try {
			DaoProvider daoProv = getDaoProvider();
			
			ReadWriteDao<CarrierEntity> carrDao = daoProv.getCarrierEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.ilike(CarrierEntity.ATTR_DISPLAY_NAME, "%" + query + "%"));
			filter.addExpression(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
			
			result = new ListRange();
			result.setData(carrDao.findNRecs(skip, num, filter).toArray());
			result.setTotalSize(carrDao.count(filter));
			
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
	
	public DeliveryTypeBsEntity fetchDeliveryType(Long deliveryTypeCd) {
		// [[
		DeliveryTypeBsEntity result = null;
		Object[][] params = {{"deliveryTypeCd", deliveryTypeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchDeliveryType", params);
		}
		// ]]
		try {
			result = referentialDataService.findBsEntity(DeliveryTypeBsEntity.class, deliveryTypeCd);
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
	 * The function will update the TruckRoute table according to the input.
	 * If the shipTo coordinates are changed by the customer,
	 * it will remove the existing TruckrouteEntities ( which are belonging
	 * to the shipTo).
	 * 
	 * @param updatedShipto The shipTo which contains the new coordinates ( lat, lng )
	 * */
	public void updateTruckRoutesToShiptoCoords(final ShiptoEntity updatedShipto) {
		// [[
		Object[][] params = {{"updatedShipto", updatedShipto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTruckRoutesToShiptoCoords", params);
		}
		// ]]
		try {
			
			//The shipto is new and it will be used at the first time
			if(null == updatedShipto.getShiptoId()) {
				return;
			}
			
			ReadOnlyDao<ShiptoEntity> shipToDao = getDaoProvider().getShiptoEntityDao();
			ShiptoEntity storedShipto = shipToDao.findById(updatedShipto.getShiptoId());
			
			if(null == storedShipto) {
				return;
			}
			
			if( null != storedShipto.getCoordLat() && null != storedShipto.getCoordLon()
					&& null != updatedShipto.getCoordLat() && null != updatedShipto.getCoordLon()) {
				
				double origCoordLat = storedShipto.getCoordLat();
				double origCoordLng = storedShipto.getCoordLon();
			
				double newCoordLat = updatedShipto.getCoordLat();
				double newCoordLng = updatedShipto.getCoordLon();
			
				if ( (origCoordLat != newCoordLat) || (origCoordLng != newCoordLng) ) {
					removeTruckRouteEntitiesForShipto(storedShipto.getShiptoId());
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
	 * input shipTo.
	 * 
	 * @param shiptoId The id (key) of the shipTo.
	 * 
	 * */
	public void removeTruckRouteEntitiesForShipto(Long shipToId) {
		// [[
		Object[][] params = {{"shipToId",shipToId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeTruckRouteEntitiesForShipto", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckRouteEntity> truckRouteDao = getDaoProvider().getTruckRouteEntityDao();
			
			Filter<TruckRouteEntity> filter = new FilterImpl<TruckRouteEntity>();
			
			filter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_END_SHIPTO_ID, shipToId));
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.kafka.KafkaPublisherService;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PdaSessionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.MopContextBean;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.ShiptoSiloVerificationWrapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderInEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;

/**
 * @author AdNovum Informatik AG
 */
public abstract class LogonEventRequestHandler extends com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonEventRequestHandler.class);
	private static final Locale DEFAULT_LOCALE = new Locale("de");
	
	private static final BigDecimal POS_X_MIN = new BigDecimal(-180);
    private static final BigDecimal POS_X_MAX = new BigDecimal(180);

    private static final BigDecimal POS_Y_MIN = new BigDecimal(-90);
    private static final BigDecimal POS_Y_MAX = new BigDecimal(90);

    protected static final String FIELD_SESSION_ID = "SessionId";
    protected static final String FIELD_GEO_X = "GeoX";
    protected static final String FIELD_GEO_Y = "GeoY";
        
    protected static final String FIELD_ASSIGNMENT_ID = "AssignmentID";
    protected static final String FIELD_ASSIGNMENT_PART_ID = "AssignmentPartID";
    protected static final String FIELD_PDA_USER_LABEL = "PdaUserLabel";
    
    protected static final String FIELD_CONTAINER_LABEL = "containerLabel";
    protected static final String FIELD_WAGON_LABEL = "wagonLabel";
    protected static final String FIELD_TRUCK_LABEL = "truckLabel";
    protected static final String FIELD_TRAILER_LABEL = "trailerLabel";
    protected static final String FIELD_LOADINGPOINT_LABEL = "loadingpointLabel";
    protected static final String FIELD_SILO_LABEL = "siloLabel";

	private PdaSessionEntity pdaSession;
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
    @EJB private DispoServiceLocalInterface dispoService;
    @EJB private PdaSessionServiceLocalInterface pdaSessionService;
    @EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB private RFIDServiceLocalInterface rfIdService;
	@EJB protected KafkaPublisherService kafkaPublisherService;

	
    @Override
	protected MopContextBean getContextBean() {
		return (MopContextBean) super.getContextBean();
	}	
    
    @Override
	public LogonEventResponse handle(LogonEventRequest eventBean, LogonEventContextBean contextBean) {
	    try {
			MopRequest mopRequest = (MopRequest)eventBean;
	    	validatePosX(FIELD_GEO_X, mopRequest.getGeoX());
	        validatePosY(FIELD_GEO_Y, mopRequest.getGeoY());
	        
	    	pdaSession = null;

	    	if (mopRequest.getSessionId() != null) {
				pdaSession = pdaSessionService.getSession(mopRequest.getSessionId());
				if (pdaSession != null) {
					pdaSessionService.updateSession(getPdaSession(), mopRequest.getEventName());
				}
				// End the driver's break if necessary
				if (isActive(pdaSession)) {
		        	DriverEntity driver = getCurrentDriver();
			    	if (driver != null) {
				        String[] noResumeEvents = getConfigBean().getWsclientMopNoResumeEvents().split(";");
				        if (!Arrays.asList(noResumeEvents).contains(mopRequest.getEventName())) {
				        	dispoService.endMopBreak(driver, mopRequest.getEventTime());
			        	}
			    	}
				}
	        }
			
	        return super.handle(mopRequest, contextBean);	    	
	    }
		finally {
	    	pdaSession = null;
	    }
	}

	
	protected Locale getLocale(LogonEventRequest bean) {
	
		String langCd = null;
    	
		// PDA
    	PdaSessionEntity session = getPdaSession();
    	if (session != null) {
    		
    		langCd = session.getUser().getApplicationLang().getLangCd();
    	}
    	
    	if (langCd != null) {
    	
    		return new Locale(langCd);
    	}
    	
    	return DEFAULT_LOCALE;
	}
	
	
	protected final PdaSessionEntity getPdaSession() {		
		return pdaSession;
	}

	protected final PdaSessionEntity getActiveSession() {	
		final PdaSessionEntity pdaSession = getPdaSession();	
		if (isActive(pdaSession)) {
			return pdaSession;
		}
		
		return null;
	}
	
	private boolean isActive(PdaSessionEntity session) {
		return ((session != null) && (session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() != Constant.PDA_SESSION_STATUS_TERMINATED));
	}
	
	public DriverEntity getCurrentDriver() {
		// [[
		DriverEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCurrentDriver");
		}
		// ]]
		try {
			
			final PdaSessionEntity activeSession = getActiveSession();
			if (activeSession == null) {				
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "no session"));
			}
			
			final ReadOnlyDao<DriverEntity> dao = getDaoProvider().getDriverEntityDao();
			final UserEntity user = activeSession.getUser();
			final Filter<DriverEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(DriverEntity.ATTR_USER, user));
			filter.addExpression(Restrictions.eq(DriverEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			
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
	
	
	protected void validatePin(String field, String value, boolean mandatory) {
        
		if (mandatory) {
			validateMandatory(field, value);
		}
		if (!value.matches("\\d{4}")) {
			
			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.MANDATORY_FIELD_ERROR, field), "****");
		}
    }
	
	
	protected void rfidMandatory(String field, String rfidLabel, String rfidId) {
		
		atLeastOneMandatory(field, rfidLabel, rfidId);
	}


    protected void validatePosX(String field, BigDecimal value) {
        
    	if ( (value != null) && (value.compareTo(POS_X_MIN) < 0) && (value.compareTo(POS_X_MAX) > 0) ) {
            
    		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.MANDATORY_FIELD_ERROR, field), value);
        }
    }
    
    
    protected void validatePosY(String field, BigDecimal value) {
        
    	if ( (value != null) && (value.compareTo(POS_Y_MIN) < 0) && (value.compareTo(POS_Y_MAX) > 0) ) {
            
    		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.MANDATORY_FIELD_ERROR, field), value);
        }
    }


    private RfidtagEntity getRfidtagByRfidIdOrName(String rfidId, String rfidName) {
    	
    	if ( (StringUtils.empty(rfidId)) && (StringUtils.empty(rfidName)) ) {
    		
    		return null;
    	}
    	
    	final ReadOnlyDao<RfidtagEntity> dao = getDaoProvider().getRfidtagEntityDao();
    	final Filter<RfidtagEntity> filter = dao.createFilter();
        if (StringUtils.empty(rfidId)) {
            filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_NAME, rfidName));
        }
        else {
            filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_RFID_IDENTITY, rfidId));
        }
        filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
        
        return dao.findSingle(filter);
    }
    
    
    protected RfidobjectEntity getRfidObject(String rfidId, String rfidLabel, Long type) {
    	
    	return getRfidObject(null, rfidId, rfidLabel, type);
    }
    
    
    protected RfidobjectEntity getRfidObject(BizeventEntity bizevent, String rfidId, String rfidLabel, Long type) {
    	
    	RfidobjectEntity rfidObject = null;
    	
    	if ( (StringUtils.empty(rfidId)) && (StringUtils.empty(rfidLabel)) ) {
        	
        	return null;
        }
        else if (StringUtils.empty(rfidId)) {
            rfidObject = RFIDBean.findRfidObject(bizevent, rfidLabel, type, rfIdService);
        }
    	if (rfidObject == null) {
        	RfidtagEntity tag = getRfidtagByRfidIdOrName(rfidId, rfidLabel);
            if (tag != null) {
                rfidObject = tag.getRfidobject();
            }
            else {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, !StringUtils.empty(rfidId)?"rfidId":"rfidLabel"), !StringUtils.empty(rfidId)?rfidId:rfidLabel);
            }
        }
    	
    	if (rfidObject == null) {
    		
    		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, !StringUtils.empty(rfidId)?"rfidId":"rfidLabel"), !StringUtils.empty(rfidId)?rfidId:rfidLabel);
    	}
    	
    	return rfidObject;
    }
    

    protected TruckEntity getTruck(String rfidId, String rfidLabel) {
        
    	TruckEntity result = null;
        
    	final RfidobjectEntity rfidObject = getRfidObject(rfidId, rfidLabel, Constant.RFIDOBJECT_TYPE_TRUCK);
    	final ReadOnlyDao<TruckEntity> dao = getDaoProvider().getTruckEntityDao();
    	
    	if (rfidObject != null) {
    		final Filter<TruckEntity> filter = dao.createFilter();
    		filter.addExpression(Restrictions.eq(TruckEntity.ATTR_RFIDOBJECT, rfidObject));
    		
    		result = dao.findSingle(filter);
    		if (result == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "No truck found for rfid object"));
            }
    		else if (!result.getActiveFlag().booleanValue()) {
    			
    			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "pda.error.truck_is_inactive")));
    		}
        }

        return result;
    }
    
    
    protected TrailerEntity getTrailer(String rfidId, String rfidLabel) {
        
    	TrailerEntity result = null;
    	
    	final RfidobjectEntity rfidObject = getRfidObject(rfidId, rfidLabel, Constant.RFIDOBJECT_TYPE_TRAILER);
    	final ReadOnlyDao<TrailerEntity> dao = getDaoProvider().getTrailerEntityDao();
    	
    	if (rfidObject != null) {
    		final Filter<TrailerEntity> filter = dao.createFilter();
            filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_RFIDOBJECT, rfidObject));
            
            result = dao.findSingle(filter);
            if (result == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "No trailer found for rfid object"));
            }
    		else if (!result.getActiveFlag().booleanValue()) {
    			
    			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Trailer is inactive"));
    		}
        }

        return result;
    }
    

    protected LoadingpointEntity getLoadingpoint(BizeventEntity bizevent, String rfidId, String rfidLabel) {
        
    	LoadingpointEntity result = null;
    	
    	final RfidobjectEntity rfidObject = getRfidObject(bizevent, rfidId, rfidLabel, Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);
    	final ReadOnlyDao<LoadingpointEntity> dao = getDaoProvider().getLoadingpointEntityDao();
    	
    	if (rfidObject != null) {
    		final Filter<LoadingpointEntity> filter = dao.createFilter();
            filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_RFIDOBJECT, rfidObject));
            
            result = dao.findSingle(filter);
            if (result == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "No loadingpoint found for rfid object"));
            }
    		else if (!result.getActiveFlag().booleanValue()) {
    			
    			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Loadingpoint is inactive"));
    		}
        }

        return result;
    }
    
    
    protected ShiptoSiloEntity getShiptoSilo(BizeventEntity bizevent, String rfidId, String rfidLabel) {
        
    	ShiptoSiloEntity result = null;
        
    	final RfidobjectEntity rfidObject = getRfidObject(bizevent, rfidId, rfidLabel, Constant.RFIDOBJECT_TYPE_SHIPTO_SILO);
    	final ReadOnlyDao<ShiptoSiloEntity> dao = getDaoProvider().getShiptoSiloEntityDao();
    	
    	if (rfidObject != null) {
    		final Filter<ShiptoSiloEntity> filter = dao.createFilter();
            filter.addExpression(Restrictions.eq(ShiptoSiloEntity.ATTR_RFIDOBJECT, rfidObject));
            
            result = dao.findSingle(filter);
            if (result == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "No silo found for rfid object"));
            }
    		else if (!result.getActiveFlag().booleanValue()) {
    			
    			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Silo is inactive"));
    		}
        }

        return result;
    }
    
    
    protected ShiptoSiloVerificationWrapper getShiptoSiloWithVerificationType(BizeventEntity bizevent, String rfidId, String rfidLabel) {
		// [[
		ShiptoSiloVerificationWrapper result = null;
		Object[][] params = {{"bizevent", bizevent},
							{"rfidId", rfidId},
							{"rfidLabel", rfidLabel}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShiptoSiloWithVerificationType", params);
		}
		// ]]
		try {
			result = new ShiptoSiloVerificationWrapper();
			
			if (StringUtils.empty(rfidId) && StringUtils.empty(rfidLabel)) {
				return result;
			}
			
			ShiptoSiloVerificationWrapper.SiloVerificationType siloVerificationType = null;
			
			final RfidtagEntity rfidTag;	// the RFID Tag of the ship to silo
			{
				final ReadOnlyDao<RfidtagEntity> dao = getDaoProvider().getRfidtagEntityDao();
				final Filter<RfidtagEntity> filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
				
				// try to obtain the RFID Tag
				if (!StringUtils.empty(rfidId)) {
					filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_RFID_IDENTITY, rfidId));
					rfidTag = dao.findSingle(filter);
					siloVerificationType = ShiptoSiloVerificationWrapper.SiloVerificationType.RFID_SCAN;
				}
				else if (!StringUtils.empty(rfidLabel)) {
					filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_NAME, rfidLabel));
					rfidTag = dao.findSingle(filter);
					siloVerificationType = ShiptoSiloVerificationWrapper.SiloVerificationType.RFID_NAME;
				}
				else {
					rfidTag = null;
				}
			}
			
			final RfidobjectEntity rfidObject;	// the RFID Object of the ship to silo
			
			// try to obtain the RFID Object
			if (rfidTag != null && rfidTag.getRfidobject() != null) {	// use the RFID Tag if possible
				rfidObject = rfidTag.getRfidobject();
			}
			else {
				rfidObject = RFIDBean.findRfidObject(bizevent, rfidLabel, Constant.RFIDOBJECT_TYPE_SHIPTO_SILO, rfIdService);
				siloVerificationType = ShiptoSiloVerificationWrapper.SiloVerificationType.RFID_LABEL;
			}
			
			
			final ShiptoSiloEntity shiptoSilo;
			
			if (rfidObject == null) {
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, !StringUtils.empty(rfidId)?"rfidId":"rfidLabel"), !StringUtils.empty(rfidId)?rfidId:rfidLabel);
			}
			else {
				final ReadOnlyDao<ShiptoSiloEntity> dao = getDaoProvider().getShiptoSiloEntityDao();
				final Filter<ShiptoSiloEntity> filter = dao.createFilter();
				filter.addExpression(Restrictions.eq(ShiptoSiloEntity.ATTR_RFIDOBJECT, rfidObject));
				shiptoSilo = dao.findSingle(filter);
				
				if (shiptoSilo == null) {
					throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "No silo found for rfid object"));
				}
				else if (!shiptoSilo.getActiveFlag().booleanValue()) {
					throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Silo is inactive"));
				}
			}
			
			if (siloVerificationType == null) {
				siloVerificationType = ShiptoSiloVerificationWrapper.SiloVerificationType.NOT_VERIFIED;
			}
			
			result.setShiptoSilo(shiptoSilo);
			result.setSiloVerificationType(siloVerificationType);
			
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
    
    
    protected WagonEntity getWagon(String rfidId, String rfidLabel) {
        
    	WagonEntity result = null;
        
    	final RfidobjectEntity rfidObject = getRfidObject(rfidId, rfidLabel, Constant.RFIDOBJECT_TYPE_WAGON);
    	final ReadOnlyDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
    	
        if (rfidObject != null) {
    		final Filter<WagonEntity> filter = dao.createFilter();
            filter.addExpression(Restrictions.eq(WagonEntity.ATTR_RFIDOBJECT, rfidObject));
            
            result = dao.findSingle(filter);
            if (result == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "No wagon found for rfid object"));
            }
    		else if (!result.getActiveFlag().booleanValue()) {
    			
    			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Wagon is inactive"));
    		}
        }
        
        return result;
    }
    
    
    protected ContainerEntity getContainer(String rfidId, String rfidLabel) {
        
    	ContainerEntity result = null;
    	
    	final RfidobjectEntity rfidObject = getRfidObject(rfidId, rfidLabel, Constant.RFIDOBJECT_TYPE_CONTAINER);
    	final ReadOnlyDao<ContainerEntity> dao = getDaoProvider().getContainerEntityDao();
    	
        if (rfidObject != null) {
    		final Filter<ContainerEntity> filter = dao.createFilter();
            filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_RFIDOBJECT, rfidObject));
            
            result = dao.findSingle(filter);
            if (result == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "No container found for rfid object"));
            }
    		else if (!result.getActiveFlag().booleanValue()) {
    			
    			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Container is inactive"));
    		}
        }

        return result;
    }
    
    
    protected AssignmentEntity getAssignment(Long assignmentId) {
    	
    	AssignmentEntity result = null;
    	if (assignmentId != null) {
            result = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
            if (result == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_ASSIGNMENT_PART_ID), assignmentId);
            }
    	}
    	
    	return result;
    }
    

    protected AssignmentPartEntity getAssignmentPart(Long assignmentPartId) {
    	
        AssignmentPartEntity result = null;
        if (assignmentPartId != null) {
            result = getDaoProvider().getAssignmentPartEntityDao().findById(assignmentPartId);
            if (result == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_ASSIGNMENT_PART_ID), assignmentPartId);
            }
        }

        return result;
    }


	private double estimateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
		// [[
		double result = 0.0;
		Object[][] params = {{"lat1", lat1 + ""},
							{"lon1", lon1 + ""},
							{"lat2", lat2 + ""},
							{"lon2", lon2 + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "estimateDistanceKm", params);
		}
		// ]]
		try {
			
			double rLat1 = Math.toRadians(lat1);
			double rLat2 = Math.toRadians(lat2);
			return result = Math.acos(Math.sin(rLat1) * Math.sin(rLat2) + 
								  Math.cos(rLat1) * Math.cos(rLat2) *
								  Math.cos(Math.toRadians(lon2-lon1))) * 6371;
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
	 * Creates a Bizevent entity according to the parameters.Always calls 
	 * MonitoringTablesService.processBizevent, which persists the entity.
	 * @param eventType
	 * @param bean
	 * @param requiresActiveSession
	 * @return
	 */
	protected BizeventEntity createBizevent(long eventType, MopRequest bean,
			boolean requiresActiveSession) {
		return createBizevent(eventType, bean, requiresActiveSession, true);
	}


	/**
	 * Creates a Bizevent entity according to the parameters.
	 * @param eventType
	 * @param bean
	 * @param requiresActiveSession
	 * @param processBizevent Set to true if MonitoringTablesService.processBizevent is to be called
	 * already
	 * @return
	 */
	protected BizeventEntity createBizevent(long eventType, MopRequest bean,
			boolean requiresActiveSession, boolean processBizevent) {
		// [[
		BizeventEntity result = null;
		Object[][] params = {{"eventType", eventType + ""},
							{"bean", bean},
							{"requiresActiveSession", requiresActiveSession + ""},
							{"processBizevent", processBizevent + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createBizevent", params);
		}
		// ]]
		try {
			result = new BizeventEntity();
			DaoProvider daoProvider = getDaoProvider();
			
			result.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, eventType));
			
			if (getContextBean().getMopeventHeaderInId() != null) {
				MopeventHeaderInEntity mopHeaderEntry = getDaoProvider().getMopeventHeaderInEntityDao().findById(getContextBean().getMopeventHeaderInId());
				result.setOriginalMopevent(mopHeaderEntry);
			}
			
			if ( (bean.getGeoX() != null) && (bean.getGeoY() != null) ) {
				result.setCoordLon(Double.valueOf(bean.getGeoX().doubleValue()));
				result.setCoordLat(Double.valueOf(bean.getGeoY().doubleValue()));
			}
			
			result.setEventTs(bean.getEventTime());
			
			PdaSessionEntity activeSession = getActiveSession();
			if ( (requiresActiveSession) && (activeSession == null) ) {
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "no session"));
			}
			
			if (activeSession != null) {
				DriverEntity driver = getCurrentDriver();
				if (driver != null) {
					TruckEntity truck = driver.getCurrentTruck();
					if (truck != null) {
						result.setTruck(truck);
						
						final ReadOnlyDao<TrailerEntity> trailerDao = daoProvider.getTrailerEntityDao();
						final Filter<TrailerEntity> trailerFilter = trailerDao.createFilter();
						trailerFilter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
						result.setTrailer(trailerDao.findSingle(trailerFilter));
						
						final ReadOnlyDao<ContainerEntity> containerDao = daoProvider.getContainerEntityDao();
						final Filter<ContainerEntity> containerFilter = containerDao.createFilter();
						containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
						result.setContainer(containerDao.findSingle(containerFilter));
						
						if ( ((truck.getHomebaseCoordLat() != null) && (truck.getHomebaseCoordLon() != null)) &&
								((result.getCoordLat() != null) && (result.getCoordLon() != null)) ) {
							double distanceKm = estimateDistanceKm(result.getCoordLat(), result.getCoordLon(), truck.getHomebaseCoordLat(), truck.getHomebaseCoordLon());
							if ( (1000*distanceKm) < getConfigBean().getAppDefaultBizeventGeofenceRadiusM() ) {
								result.setCoordTruckHomebaseFlag(Boolean.TRUE);
							}
							else {
								result.setCoordTruckHomebaseFlag(Boolean.FALSE);
							}
						}
					}
				}
			}
			
			if (processBizevent) {
				result = monitoringTablesService.processBizevent(result);
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
    
    
	@Override
    protected LogonEventResponse createLogonEventResponse(BigInteger statusCode, String contextValue, Object... fmtParams) { // NOPMD
		LogonEventResponse response = createBasicLogonEventResponse(
				statusCode, contextValue);
		
		resolveMessageText(response, statusCode, contextValue, fmtParams);
		
		return response;
	}

	protected LogonEventResponse createBasicLogonEventResponse(
			MopResponseCode mopResponseCode) {
		return createBasicLogonEventResponse(mopResponseCode.value(), null);
	}
	
	protected LogonEventResponse createBasicLogonEventResponse(
			MopResponseCode mopResponseCode, String contextValue) {
		return createBasicLogonEventResponse(mopResponseCode.value(), contextValue);
	}
	
    @Override
	protected LogonEventResponse createBasicLogonEventResponse(
			BigInteger statusCode, String contextValue) {
		LogonEventResponse response = null;
		try {
			response = (LogonEventResponse)MopResponse.class.newInstance();
		}
		catch (Exception e) {
			
			throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
		}
		response.setCode(statusCode);
		response.setContextValue(contextValue);
		return response;
	}
    
    protected LogonEventResponse createLogonEventResponse(MopResponseCode responseCode, String contextValue, Object... fmtParams) { // NOPMD
    	
    	return createLogonEventResponse(responseCode.value(), contextValue, fmtParams);
    }
	
	
	protected LogonEventResponse createLogonEventResponse(MopResponseCode responseCode, String contextValue) {
    	
    	return createLogonEventResponse(responseCode, contextValue, (Object[])null);
    }
    
    protected LogonEventResponse createLogonEventResponse(MopResponseCode responseCode) {
    	
    	return createLogonEventResponse(responseCode, null);
    }

	protected ShippingpointEntity getShippingpoint(List<OrderEntity> orders, AssignmentPartEntity assignmentPart) {
		ShippingpointEntity shippingPoint = assignmentPart.getShippingpoint();
		if (shippingPoint == null) {
			OrderEntity firstOrder = orders.isEmpty() ? null : orders.get(0);
			shippingPoint = firstOrder != null ? firstOrder.getShippingpoint() : null;
		}
		return shippingPoint;
	}

}

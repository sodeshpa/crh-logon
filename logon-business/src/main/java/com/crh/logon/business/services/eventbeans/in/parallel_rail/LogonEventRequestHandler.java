/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.parallel_rail;

import java.math.BigInteger;
import java.util.Locale;

import javax.ejb.EJB;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailRequest;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class LogonEventRequestHandler extends com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonEventRequestHandler.class);
	private static final Locale DEFAULT_LOCALE = new Locale("de");
	
	protected static final String FIELD_CONTAINER_LABEL = "containerLabel";
    protected static final String FIELD_WAGON = "wagon";
	
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;

	
	protected Locale getLocale(LogonEventRequest bean) {	
		final ParallelRailRequest prr = (ParallelRailRequest) bean; 
		if (prr.getLanguage() != null) {		
			return new Locale(prr.getLanguage());
		}
			
		return DEFAULT_LOCALE;
	}
	
	
    protected WagonEntity getWagon(String wagonNo) {
		
    	WagonEntity wagon = null;
    	
    	if (!StringUtils.empty(wagonNo)) {
    		final ReadWriteDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
			final Filter<WagonEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_NO, wagonNo));
			filter.addExpression(Restrictions.eq(WagonEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			wagon = dao.findSingle(filter);
			
			if (wagon == null) {				
				throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.MANDATORY_FIELD_ERROR, FIELD_WAGON), wagonNo);
			}
    	}
    	
    	return wagon;
	}
    
    
    protected ContainerEntity getContainer(String containerName) {
		
    	ContainerEntity container = null;
    	
    	if (!StringUtils.empty(containerName)) {
    		final ReadWriteDao<ContainerEntity> dao = getDaoProvider().getContainerEntityDao();
			final Filter<ContainerEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CONTAINER_NAME, containerName));
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			container = dao.findSingle(filter);
			
			if (container == null) {			
				throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.MANDATORY_FIELD_ERROR, FIELD_CONTAINER_LABEL), containerName);
			}
    	}
    	
    	return container;
	}


	protected BizeventEntity createBizevent(long eventType, ParallelRailRequest bean) {
		// [[
		BizeventEntity result = null;
		Object[][] params = {{"eventType", eventType + ""},
							{"bean", bean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createBizevent", params);
		}
		// ]]
		try {
			BizeventEntity event = new BizeventEntity();
			
			event.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, eventType));
			event.setEventTs(bean.getEventTime());
			
			if (bean.getWagon() != null) {
				event.setWagon(getWagon(bean.getWagon()));
			}
			
			return result = event;
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


	@Override
	protected LogonEventResponse createBasicLogonEventResponse(
			BigInteger statusCode, String contextValue) {
		LogonEventResponse response = null;
		try {
			response = (LogonEventResponse)ParallelRailResponse.class.newInstance();
		}
		catch (Exception e) {
			
			throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
		}
		response.setCode(statusCode);
		response.setContextValue(contextValue);
		return response;
	}
	
	protected LogonEventResponse createLogonEventResponse(ParallelRailResponseCode responseCode, String contextValue, Object... fmtParams) { // NOPMD    	
    	return createLogonEventResponse(responseCode.value(), contextValue, fmtParams);
    }
		
	protected LogonEventResponse createLogonEventResponse(ParallelRailResponseCode responseCode, String contextValue) {    	
    	return createLogonEventResponse(responseCode, contextValue,(Object[])null);
    }
	
	protected LogonEventResponse createBasicLogonEventResponse(ParallelRailResponseCode responseCode, String contextValue) {    	
    	return createBasicLogonEventResponse(responseCode.value(), contextValue);
    }
        
    protected LogonEventResponse createLogonEventResponse(ParallelRailResponseCode responseCode) {    	
    	return createLogonEventResponse(responseCode, null);
    }
        
	protected LogonEventResponse createLogonEventResponse(Class <? extends ParallelRailResponse> clazz, ParallelRailResponseCode parallelRailResponseCode) {
		LogonEventResponse response = null;
		try {
			response = (LogonEventResponse)clazz.newInstance();
		}
		catch (Exception e) {
			
			throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
		}
		BigInteger responseCode = parallelRailResponseCode.value();
		response.setCode(responseCode);
		response.setContextValue(null);
		resolveMessageText(response, responseCode, null, null, (Object[])null);
		return response;
    }
}

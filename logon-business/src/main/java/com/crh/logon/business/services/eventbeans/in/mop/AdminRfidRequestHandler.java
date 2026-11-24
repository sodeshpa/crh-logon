/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.AdminRfidRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class AdminRfidRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AdminRfidRequestHandler.class);

    private static String FIELD_RFID_IDENTITY = "RFIDIdentity";
    private static String FIELD_RFID_LABEL = "RFIDLabel";
    private static String FIELD_OBJECT_LABEL = "ObjectLabel";
    private static String FIELD_OBJECT_TYPE = "ObjectType";
    
    
    protected void validate(LogonEventRequest eventBean) {
    
    	AdminRfidRequest bean = (AdminRfidRequest)eventBean;
    	validateMandatory(FIELD_RFID_IDENTITY, bean.getRfidIdentity());
    	validateMandatory(FIELD_OBJECT_LABEL, bean.getObjectLabel());
    	validateMandatory(FIELD_OBJECT_TYPE, bean.getObjectType());
    }
    
    
    /**
     * looks for an RFIDObject with label LIKE '%normalized(objectLabel)'.
     * @param objectLabel
     * @param objectType String representation of the object type (parsable to long).
     * @return the object found or NULL if none OR multiple objects found.
     */
    private RfidobjectEntity retrieveRfidObject(String objectLabel, Long objectType) {
		// [[
		RfidobjectEntity result = null;
		Object[][] params = {{"objectLabel", objectLabel},
							{"objectType", objectType}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "retrieveRfidObject", params);
		}
		// ]]
		try {
			// Object label is a construct with prefix. We don't know the whole context, so we can't
			// reproduce the whole object label --> heuristic: use default label conversion and try
			// to find one matching object.
			// If we find more we don't know which one to take --> do not select any.
			boolean exactSearch = false;
			String normalizedLabel = RFIDBean.dfltConvertToASCII(objectLabel);
            Filter filter = new FilterImpl();
            ReadWriteDao<RfidobjectEntity> dao = getDaoProvider().getRfidobjectEntityDao();
            filter.addExpression(Restrictions.eq(RfidobjectEntity.ATTR_RFIDOBJECT_TYPE_CD, objectType));
            if (objectType.longValue() == Constant.RFIDOBJECT_TYPE_PDA_USER) {
            	// no prefix for PDA users --> exact search:
            	exactSearch = true;
            	filter.addExpression(Restrictions.eq(RfidobjectEntity.ATTR_OBJECT_LABEL, normalizedLabel));
            }
            else {
            	// labels have prefix (context), needs 'like' search:
            	filter.addExpression(Restrictions.ilike(RfidobjectEntity.ATTR_OBJECT_LABEL, "%"+normalizedLabel));
            }
            List objects = dao.findNRecs(0, 10, filter);
            if (objects.size() == 1) {
            	// Return only if exactly one match found.
            	result = (RfidobjectEntity) objects.get(0);
            	LOG.debug("RFID object ID found: "+result.getRfidobjectId());
            }
            else if (objects.size() > 1) {
            	LOG.info("RFID object search by '"+(exactSearch?"":"%")+normalizedLabel+"', type="+objectType+": ambiguous result ("+objects.size()+" objects found).");
            }
            else {
            	LOG.info("No RFID object found by search for label"+(exactSearch?"='":" like '%")+normalizedLabel+"' and type="+objectType);
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
    

    protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = {{"eventBean", eventBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			AdminRfidRequest bean = (AdminRfidRequest)eventBean;
			LogonEventResponse response = createLogonEventResponse(MopResponseCode.OK);
            
            RfidobjectEntity object = retrieveRfidObject(bean.getObjectLabel(), bean.getObjectType());
            
            final String rfidIdent = bean.getRfidIdentity();
            // in case optional rfidLabel is an empty string:
            final String rfidLabel = StringUtils.empty(bean.getRfidLabel()) ? null : bean.getRfidLabel();
            
            Filter filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(RfidtagEntity.ATTR_RFID_IDENTITY, rfidIdent));
			
            ReadWriteDao<RfidtagEntity> dao = getDaoProvider().getRfidtagEntityDao();
            RfidtagEntity rfidtag = dao.findSingle(filter);
            
            if (rfidtag == null) {
            	if (rfidLabel == null) {
            		LOG.error("RFID label missing to create new RFID tag entry (unknown RFID identity '"+rfidIdent+"').");
            		
            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.MANDATORY_FIELD_MISSING, FIELD_RFID_LABEL));
            	}
                LOG.debug("Creating new RFID tag.");
                rfidtag = new RfidtagEntity();
                rfidtag.setName(rfidLabel);
                rfidtag.setRfidIdentity(rfidIdent);
                rfidtag.setActiveFlag(Boolean.TRUE);
            }
            else {
                if (!rfidtag.getActiveFlag()) {
                    LOG.debug("Reactivating rfidtag");
                    rfidtag.setActiveFlag(Boolean.TRUE);
                }
                if (rfidLabel != null && !rfidLabel.equals(rfidtag.getName())) {
                    LOG.debug("Setting/updating RFID tag label (name) to '"+rfidLabel+"'.");
                    rfidtag.setName(rfidLabel);
                }
            }
            
            if (object != null) {
            	if (rfidtag.getRfidobject() == null) {
            		LOG.info("Assigning RFID object '"+object.getRfidobjectId()
            				+"' to RFID tag (identity '"+rfidIdent+"', label '"+rfidtag.getName()+"').");
            	}
            	else if (rfidtag.getRfidobject().getRfidobjectId() != object.getRfidobjectId()) {
            		LOG.info("Re-assigning RFID object on RFID tag (identity '"+rfidIdent+"', label '"
            				+rfidtag.getName()+"') from ID '"+rfidtag.getRfidobject().getRfidobjectId()
            				+"' to '"+object.getRfidobjectId()+"'.");
            		if (object.getRfidobjectType().getRfidobjectTypeCd() !=
            			rfidtag.getRfidobject().getRfidobjectType().getRfidobjectTypeCd())
            		{
            			LOG.info("RFID object type changed! From '"+rfidtag.getRfidobject().getRfidobjectType().getRfidobjectTypeCd()
            					+"' to '"+object.getRfidobjectType().getRfidobjectTypeCd()+"'.");
            		}
            		
            	}
            }
            else {
            	response = createLogonEventResponse(MopResponseCode.MANDATORY_FIELD_ERROR, FIELD_OBJECT_LABEL, bean.getObjectLabel());
            }
        	rfidtag.setRfidobject(object);
            
            dao.persist(rfidtag);
            
            if (logDebugEnabled) {
            	LOG.debug("Successfully created/updated RFID Tag (identity='" + rfidIdent + "', label='" + rfidLabel
            			+ "', rfidObjectID='" + (object == null ? "null" : object.getRfidobjectId()) + "').");
            }
            
            return result = response;
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

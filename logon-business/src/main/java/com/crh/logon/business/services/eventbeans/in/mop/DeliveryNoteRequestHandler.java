/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.transaction.TransactionSynchronizationRegistry;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.DeliveryNoteRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class DeliveryNoteRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryNoteRequestHandler.class);

    private static String FIELD_SHIP_TO_ID = "ShipToID";

	@Resource(mappedName="java:comp/TransactionSynchronizationRegistry")
	private TransactionSynchronizationRegistry transactionRegistry;

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private OrderServiceLocalInterface orderService;
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;


    protected void validate(LogonEventRequest eventBean) {
    	
    	DeliveryNoteRequest bean = (DeliveryNoteRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
    	
    	// check the "shipToId" for internal assignment 
    	AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId()); 
    	if (!isExternalOrder(part)) {
    		validateMandatory(FIELD_SHIP_TO_ID, bean.getShipToId());
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
            DeliveryNoteRequest bean = (DeliveryNoteRequest)eventBean;
            
			if (bean.getSignatureName() != null && bean.getSignatureName().length() > OrderEntity.DBSIZE_CONTACT_PERSON_SIGNATURE_TXT) {
				throw new LogonEventException(createBasicLogonEventResponse(
													MopResponseCode.APPLICATION_ERROR, "Maximum length of the name is " + OrderEntity.DBSIZE_CONTACT_PERSON_SIGNATURE_TXT + " characters. Please review your input!"));
			}
            
			BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_DELIVERY_NOTE, bean, true);
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            bizevent.setAssignment(part.getAssignment());
	        bizevent.setOrder(part.getOrder());
			
	        
	        if ( !isExternalOrder(part) ) {
	        	
		        /**
		         * The point id the pda knows of is either a shippingpoint or a shipto id depending on the fact if the 
		         * unloading will happen at a shipto or at a shipping point.
		         * See also AsyncMopEventOutServiceBean.PointData construction.
		         */
	            final Long pointId = bean.getShipToId();
	            try {	            	
	            	asyncMopEventOutService.setRelevantBizeventPointId(bizevent, part, pointId);
	            }
	            catch (LogonException e) {
	            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_SHIP_TO_ID), pointId);
				}
	        }
            
            SignatureEntity signature = null;
            if (bean.getSignature() != null) {
	            final byte[] sign = Base64.decode(bean.getSignature());
	            signature = new SignatureEntity();
				
	            signature.setData(sign);
	            signature.setDataMimeType(referentialDataService.findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_PNG));
	            signature = getDaoProvider().getSignatureEntityDao().persist(signature);
            }
            orderService.setDeliveryNoteSignature(bizevent, signature, bean.getSignatureName(),  bean.getNote());
            LOG.error("asdfqweryxvc, {} " + transactionRegistry.getTransactionStatus(), transactionRegistry.getTransactionStatus());
            kafkaPublisherService.publishMOPEvent(bizevent.getOrder(), bean);

            return result = createLogonEventResponse(MopResponseCode.OK);
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
     * Check the assignment type. If the type is "external" return <code>true</code>,
     * otherwise <code>false</code>.
     * 
     * Note: if the <code>part</code> parameter is null, then <code>false</code> is returned.
     * 
     * @param part
     * @return
     */
    private boolean isExternalOrder(AssignmentPartEntity part) {
		// [[
		boolean result = false;
		Object[][] params = {{"part", part}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isExternalOrder", params);
		}
		// ]]
		try {
			if (part == null) {
				return result = false;
			}
			
			return result = part.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_EXTERNAL;
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.BizeventAuditConfiguration.AUDIT_GROUP;
import com.crh.logon.business.services.utils.BizeventAuditUtil;
import com.crh.logon.business.services.utils.BizeventAuditWrapper;
import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;

/**
 * @author AdNovum Informatik AG
 */
public class BizeventAuditValuePrintTag extends TagSupport { 
	

	private static final String C_NAME = 
		BizeventAuditValuePrintTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	public static final String OLD_VALUE = "oldValue";
	public static final String NEW_VALUE = "newValue";
		
	private int itemIndex;
	private String fieldName;
	
	
	public int doStartTag() throws JspException {

		ServletRequest request = pageContext.getRequest();
		List<BizeventAuditWrapper> auditEvents = (List<BizeventAuditWrapper>) request.getAttribute(OrderDetailForm.REQUEST_BIZEVENT_AUDIT_LIST);
		
		final Object attribute = pageContext.getAttribute("wrappersByBizeventId");
		Map <Long, List<BizeventAuditWrapper>> auditEventsByBusinessEventId = null;
		if (attribute != null) {
			auditEventsByBusinessEventId = (Map <Long, List<BizeventAuditWrapper>>) attribute;
		}
		else {
			auditEventsByBusinessEventId = new HashMap<Long, List<BizeventAuditWrapper>>();
			
			for (BizeventAuditWrapper auditEvent : auditEvents) {
				if (auditEvent == null) {
					continue;
				}

				BizeventAuditDisplayEntity auditBusinessEvent = auditEvent.getEntity();
				Long businessEventId = auditBusinessEvent.getBizeventId();
				List<BizeventAuditWrapper> auditEventsOfCurrentBusinessEventId = auditEventsByBusinessEventId.get(businessEventId);
				
				if (auditEventsOfCurrentBusinessEventId == null) {
					auditEventsOfCurrentBusinessEventId = new ArrayList<BizeventAuditWrapper>();
					auditEventsByBusinessEventId.put(businessEventId, auditEventsOfCurrentBusinessEventId);
				}

				auditEventsOfCurrentBusinessEventId.add(auditEvent);
			}

			pageContext.setAttribute("wrappersByBizeventId", auditEventsByBusinessEventId);
		}

		//current audit which is supposed to be printed
		final BizeventAuditWrapper bizeventAuditWrapper = auditEvents.get(itemIndex);
		final List<BizeventAuditWrapper> list = auditEventsByBusinessEventId.get(bizeventAuditWrapper.getEntity().getBizeventId());

		//only if these values do not correspond! if they correspond they must not render anything on this audit page, they are supposed to store only the state
		if ( ((bizeventAuditWrapper.getNewValueDisplay() != null && bizeventAuditWrapper.getOldValueDisplay() != null ) && !bizeventAuditWrapper.getOldValueDisplay().equals(bizeventAuditWrapper.getNewValueDisplay())) ||
				(bizeventAuditWrapper.getNewValueDisplay() == null && bizeventAuditWrapper.getOldValueDisplay() != null ) ||
				(bizeventAuditWrapper.getNewValueDisplay() != null && bizeventAuditWrapper.getOldValueDisplay() == null )
				) {

			//string containing all the values of the group, printed with different style the ones which are only in the group but are not the current audit itself
			final String str = getValueOfGroup(list, bizeventAuditWrapper);

			try {

				pageContext.getOut().write(str);
			}
			catch (IOException e) {
				LOG.error("Error in " + BizeventAuditValuePrintTag.C_NAME);
			}
		}
		return SKIP_BODY;
	}

	/**
	 * @param groupWrappers
	 * @param index
	 */
	private String getValueOfGroup(List<BizeventAuditWrapper> groupWrappers, BizeventAuditWrapper currAudit) {
		
		final Long auditBizeventConfigCd = currAudit.getEntity().getAuditBizeventConfigCd();
		AUDIT_GROUP group = BizeventAuditUtil.getGroupOfAuditConfigCd(auditBizeventConfigCd);
		if ( group == null ) {
			final String valueDisplay = getValueDisplay(currAudit);
			return valueDisplay == null ? " - " : valueDisplay;
		}
		
		List groupMembers = group.getAuditCodes();
		StringBuilder obj2 = null;
		
		for ( BizeventAuditWrapper bizeventAuditWrapper : groupWrappers ) {
			if ( !groupMembers.contains(bizeventAuditWrapper.getEntity().getAuditBizeventConfigCd()) ) {
				continue;
			}
			
			String valueDisplay = getValueDisplay(bizeventAuditWrapper);
			if ( valueDisplay != null ) {
				
				if ( obj2 == null ) {
					obj2 = new StringBuilder();
				}
				if ( !bizeventAuditWrapper.getEntity().getAuditBizeventConfigCd().equals(auditBizeventConfigCd)  ) {
					obj2.append(" <span style=\"color:c0c0c0\">" + valueDisplay + "</span>");	
				}
				else {				
					obj2.append(" " + valueDisplay);
				}
			}
			else if ( obj2 != null ) {
				obj2.append(" - ");
			}	
		}
		if ( obj2 == null ) {
			
			return " - ";
		}
		else {
			
			return obj2.toString();
		}
		
	}


	private String getValueDisplay(BizeventAuditWrapper bizeventAuditWrapper) {
		if ( this.fieldName.equals(OLD_VALUE)) {
			final String oldValueDisplay = bizeventAuditWrapper.getOldValueDisplay();
			return oldValueDisplay;
		}
		else {				
			final String newValueDisplay = bizeventAuditWrapper.getNewValueDisplay();
			return newValueDisplay;
		}
	}

	public void setItemIndex(int itemIndex) {			
		this.itemIndex = itemIndex;
	}


	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


}

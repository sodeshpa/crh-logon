/*
 * Created on Mar 26, 2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.crh.logon.admin.web.forms.test;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.web.forms.AbstractActionForm;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.validators.StrutsValidator;
import com.crh.logon.web.validators.TypeValidation;

/**
 * Used by the JSP which provides the test UI for the Fax and Sms services
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ManualFaxSmsTriggerForm"
 */
public final class ManualFaxSmsTriggerForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	private String number = "+36 1 487 50 00";
	private String message = "Greetings from AdNovum!";
	private String orderNo;
	private String serviceProvider;
	private String documentId;
	private boolean isFax;

	private String[] availableFaxProviders;
	private String[] availableSmsProviders;
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	@TypeValidation(Long.class)
	public String getOrderNo() {
		return orderNo;
	}
	
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public String getServiceProvider() {
		return serviceProvider;
	}
	
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@TypeValidation(Long.class)
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	} 
	
	public boolean isFax() {
		return isFax;
	}

	public void setFax(boolean isFax) {
		this.isFax = isFax;
	}

	public Collection<String> getAvailableServiceProviders() {
		return Arrays.asList(isFax ? availableFaxProviders : availableSmsProviders);
	}
	
	public String getAvailableFaxProvidersAsJSArray() {
		return "['" + StringUtils.join(availableFaxProviders, "', '") + "']";
	}

	public String getAvailableSmsProvidersAsJSArray() {
		return "['" + StringUtils.join(availableSmsProviders, "', '") + "']";
	}
	
    /**
     * Clears all the input fields on the web form, discards any existing
     * search results, and resets any existing pages. Called in response to
     * clicking the reset button on the web form.
     */
    public void clear(String[] availableFaxProviders, String[] availableSmsProviders) {
		number = "+36 1 487 50 00";
		message = "Greetings from AdNovum!";
		orderNo = null;
		serviceProvider = null;
		documentId = null;
		isFax = false;
		this.availableFaxProviders = availableFaxProviders;
		this.availableSmsProviders = availableSmsProviders;
    }
    
	@StrutsValidator
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = null;
		
    	if (StringUtils.isEmpty(number)) {
    		errors = MessageUtils.addActionError(request, "error.field.required", MessageUtils.getMessage(request, "fields.sms.no"));
    	}
    	
    	if (StringUtils.isEmpty(message)) {
    		errors = MessageUtils.addActionError(request, "error.field.required", MessageUtils.getMessage(request, "fields.sms.msg"));
    	}
    	
    	if (StringUtils.isEmpty(orderNo) ) {
    		errors = MessageUtils.addActionError(request, "error.field.required", MessageUtils.getMessage(request, "fields.sms.orderNo"));
    	}
    	
    	if (StringUtils.isEmpty(serviceProvider) ) {
    		errors = MessageUtils.addActionError(request, "error.field.required", MessageUtils.getMessage(request, "fields.sms.serviceProvider"));
    	}
    	
    	if (StringUtils.isEmpty(documentId) && isFax) {
    		errors = MessageUtils.addActionError(request, "error.field.required", MessageUtils.getMessage(request, "fields.fax.docid"));
    	}
    	
    	return errors;
	}
}
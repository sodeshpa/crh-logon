/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.eventbeans.mop.LoadOrder;
import com.crh.logon.eventbeans.mop.Message;
import com.crh.logon.eventbeans.mop.PostOrder;
import com.crh.logon.eventbeans.mop.PreOrder;
import com.crh.logon.eventbeans.mop.ReadyForOperation;
import com.crh.logon.eventbeans.mop.RefData;
import com.crh.logon.eventbeans.mop.RequestForPosition;
import com.crh.logon.eventbeans.mop.Setup;
import com.crh.logon.eventbeans.mop.Test;
import com.crh.logon.eventbeans.mop.UnloadOrder;
import com.crh.logon.web.forms.AbstractActionForm;
import com.crh.logon.web.validators.TypeValidation;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ManualPDATriggerForm"
 */
public class ManualPDATriggerForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ManualPDATriggerForm.class);
	
	private Map methodParameters; // <String, String> The parameter names (keys) and their types (value). These are set in the action after selecting an eventType
	private Map parameters; // <String, String> The parameter names (keys) and their types (value). These are set in the action after selecting an eventType
	private Map parameterValues; // <String, String> values.
	private List eventTypes;
	private String formAction;
	private String assignmentId, truckId;
	private String eventType;
	private String eventClassName;

    /**
     * Default constructor.
     */
    public ManualPDATriggerForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ManualPDATriggerForm");
		}
		// ]]
		try {
	        init();
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
     * Initializes the form. This has the effect of clearing all the input
     * fields on the web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			clear();
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
     * Clears all the input fields on the web form, discards any existing
     * search results, and resets any existing pages. Called in response to
     * clicking the reset button on the web form.
     */
    public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			eventTypes = findEventTypes();
			eventType = ((Class)eventTypes.get(0)).getName();
			parameters = new HashMap();
			methodParameters = new HashMap();
			parameterValues = new HashMap();
			formAction = null;
			assignmentId = null;
			truckId = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public ActionErrors performValidation(ActionMapping mapping, HttpServletRequest request) {
    	
    	return validateBean(mapping, request, this);
    }

    /**
     * Override struts method.
     * Perform form-specific validation of input parameters.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			if ("getPDAEventOptions".equals(formAction)) {
				
				return result = null;
			}
	        return result = performValidation(mapping, request);
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
     * Generates a list of classnames for which an entry should be added in the screen. All classes are of type LogonEventOutBean. 
     * @return A list of Strings.
     */
    private List findEventTypes() {
    	
    	//LogonEventOutBean
    	List results = new ArrayList();
    	results.add(PreOrder.class);
    	results.add(PostOrder.class);
    	results.add(Message.class);
    	results.add(UnloadOrder.class);
    	results.add(LoadOrder.class);
    	results.add(ReadyForOperation.class);
    	results.add(RefData.class);
    	results.add(RequestForPosition.class);
    	results.add(Setup.class);
    	results.add(Test.class);
    	
    	return results;
    }
    
    public List getEventTypes() {
		return eventTypes;
	}

	public Map getParameters() {
		return parameters;
	}
	
	public SortedMap getSortedParameters() {
		SortedMap sorted = new TreeMap();
		sorted.putAll(parameters);
		
		return sorted;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public Map getParameterValues() {
		return parameterValues;
	}
	
	public Object getParameterValue(String o) {
		return (String)parameterValues.get(o);
	}
	
	public void setParameterValue(String o, Object v) {
		parameterValues.put(o, v);
	}

	public void setParameterValues(Map parameterValues) {
		this.parameterValues = parameterValues;
	}
	
	public SortedMap getSortedMethodParameters() {
		SortedMap sorted = new TreeMap();
		sorted.putAll(methodParameters);
		
		return sorted;
	}

	public Map getMethodParameters() {
		return methodParameters;
	}

	public void setMethodParameters(Map methodParameters) {
		this.methodParameters = methodParameters;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	@TypeValidation(java.lang.Long.class)
	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	@TypeValidation(java.lang.Long.class)
	public String getTruckId() {
	    return truckId;
	}

	public void setTruckId(String truckId) {
	    this.truckId = truckId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventClassName() {
		return eventClassName;
	}

	public void setEventClassName(String eventClassName) {
		this.eventClassName = eventClassName;
	}


}

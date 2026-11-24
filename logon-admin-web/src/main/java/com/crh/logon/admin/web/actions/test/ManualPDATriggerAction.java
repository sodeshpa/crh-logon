/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.test.ManualPDATriggerForm;
import com.crh.logon.utils.time.DateTimeFormat;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.AsyncMopEventOutService;
import com.crh.logon.constants.Constant;
import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.eventbeans.LogonEventOutBeanFactory;
import com.crh.logon.eventbeans.mop.LoadOrder;
import com.crh.logon.eventbeans.mop.UnloadOrder;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ManualPDATriggerForm" path="/test/ManualPDATrigger" scope="session"
 *  validate="true" input="/test/ManualPDATriggerTile.def"
 *
 * @struts.action-forward name="pdaEventOptionsSet" path="/test/PDAEventOptionsTile.def"
 */
public class ManualPDATriggerAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ManualPDATriggerAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException {
		
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		Authorization.authorize(Authorization.TEST_PDA);
		ManualPDATriggerForm pdaForm;
		String forwardName = null;
		try {
			// Get the struts form.
			pdaForm = (ManualPDATriggerForm) form;
			
			if ("getPDAEventOptions".equals(pdaForm.getFormAction())) {
				forwardName = "pdaEventOptionsSet";
				ActionErrors errors = pdaForm.performValidation(mapping, request);
				if ( (errors == null) || (errors.isEmpty()) ) {
					handleGetPDAEventOptions(mapping, pdaForm, request, response);
				}
			}
			else if ("submit".equals(pdaForm.getFormAction())) {
				handleManualPDATrigger(mapping, pdaForm, request, response);
			}
			else {
				throw new LogonException("The action '" + pdaForm.getFormAction() + "' is invalid", Reason.RES_ERROR_IMPL);
			}
						
			return result = forwardName!=null?mapping.findForward(forwardName):mapping.getInputForward();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	private boolean handleGetPDAEventOptions(ActionMapping mapping, ManualPDATriggerForm pdaForm, HttpServletRequest request,	HttpServletResponse response) {
		
		String eventType = pdaForm.getEventClassName();
		String strDate = DateTimeFormat.forMop(new Date());
		pdaForm.getParameterValues().clear();
		pdaForm.getParameterValues().put("SendersTime", strDate);
		StringBuilder eventTypeName = new StringBuilder(eventType.substring(eventType.lastIndexOf('.') + 1));
		eventTypeName.setCharAt(0, Character.toLowerCase(eventTypeName.charAt(0)));
		pdaForm.getParameterValues().put("EventType", eventTypeName.toString());
		
		Class clazz;
		try {
			clazz = Class.forName(eventType);
		} catch (ClassNotFoundException e) {
			throw new LogonException(e);
		}
		if ( (clazz == LoadOrder.class) || (clazz == UnloadOrder.class) ) {
		    final boolean loadOrder = clazz == LoadOrder.class;
			AssignmentEntity assignment = null;
			TruckEntity truck = null;
            AssignmentPartEntity assignmentPart = null;
            AssignmentPartEntity unloadAassignmentPart = null;
            
			if (StringUtils.notEmpty(pdaForm.getAssignmentId())) {
				Long assignmentId = Long.parseLong(pdaForm.getAssignmentId());
				assignment = getServiceLocator().getOrderService().findAssignmentById(assignmentId);
				if (assignment == null) {
					MessageUtils.addActionError(request, "test.assignmentnotfound", assignmentId.toString());
				}
			}
			else {
			    MessageUtils.addActionError(request, "error.field.required", "assignmentId");
			}
			
            //FIXME cosmin 16.12.2009: this needs serius fixing in order to work with tours
			if (assignment != null) {
				for (Iterator it = assignment.getAssignmentParts().iterator();it.hasNext();) {
				    assignmentPart = (AssignmentPartEntity)it.next();
				    if (loadOrder && assignmentPart.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
					    break;
					}
					else if (!loadOrder && assignmentPart.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
						unloadAassignmentPart = assignmentPart;
					}
				}
				if (!loadOrder) {
					assignmentPart = unloadAassignmentPart;
				}
			}
			if (StringUtils.notEmpty(pdaForm.getTruckId())) {
                Long truckId = Long.parseLong(pdaForm.getTruckId());
                truck = getServiceLocator().getTruckAdminService().fetchTruck(truckId);
                if (truck == null) {
                    MessageUtils.addActionError(request, "test.trucknotfound", truckId.toString());
                }
            }
			
			
			if ( (assignmentPart != null) && (truck != null) ) {
				AsyncMopEventOutService mopService = getServiceLocator().getAsyncMopEventOutService();
				LogonEventOutBean msg = null;
				if (loadOrder) {
					msg = mopService.getLoadOrderData(assignmentPart, request.getLocale(), truck); 
				}
				else {
					msg = mopService.getUnloadOrderData(assignmentPart, request.getLocale());
				}
				Class msgClass = msg.getClass();
				do {
					if (msgClass != null) {
						Map parameters = getParameterMap(clazz);
						if (parameters != null) {
							for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
								String key = (String)it.next();
								try {
									Method m = null;
									try {
										m = msgClass.getMethod("get" + key);
									} catch (NoSuchMethodException e) {
										continue;
									}
									if (m != null) {
										Object value = m.invoke(msg);
										String stringValue = parseDataValue(key, value);
										if (stringValue != null) {
											pdaForm.getParameterValues().put(key, value.toString());
										}
									}
								} catch (Exception e) {
									throw new LogonException(e);
								}												
							}
						}
					}
					msgClass = msgClass.getSuperclass();
				} while (msgClass != null);						
			}
		}					

		// refresh the parameter input fields based on the selected event type
		// get the fields which are implemented in the event class itself (body)
		Map setters = getParameterMap(clazz);
		pdaForm.setParameters(setters);
		// get the fields implemented in the superclasses (header)
		setters = new HashMap();
		do {
			clazz = clazz.getSuperclass();
			if (clazz != null) {
				Map parameters = getParameterMap(clazz);
				if (parameters != null) {
					setters.putAll(parameters);
				}
			}
		} while (clazz != null);
		pdaForm.setMethodParameters(setters);
		
		return true;
	}
	
	
	private boolean handleManualPDATrigger(ActionMapping mapping, ManualPDATriggerForm pdaForm, HttpServletRequest request,	HttpServletResponse response) {
		
		// make a call to an event, find the event class
		String eventType = pdaForm.getEventType();
		Class eventClass;
		try {
			eventClass = Class.forName(eventType);
		}
		catch (ClassNotFoundException e) {
			LOG.error("Class not found", e);
			
			return false;
		}
		
		// Create the map containing the body data 
		HashMap logonData = new HashMap();
		// body
		List methods = getParameterMethods(eventClass);
		for (Iterator it=methods.iterator();it.hasNext();) {
			Method setter = (Method)it.next();
			String logonDataKey = setter.getName().substring(3);
			String logonDataStringValue = (String)pdaForm.getParameterValue(logonDataKey);
			if (StringUtils.notEmpty(logonDataStringValue)) {
				Object logonDataValue = null;
				Method m = null;
				for (int i=0;i<methods.size();i++) {
					m = (Method)methods.get(i);
					if (m.getName().equals("set" + logonDataKey)) {
						logonDataValue = parseLogonDataValue(m.getParameterTypes()[0], logonDataStringValue);
						break;
					}
				}
				if (logonDataValue == null) {
					LOG.debug("The parameter type could not be determined for " + logonDataKey);
				}
				else {
					LOG.debug("Adding logondata: " + logonDataKey.toLowerCase() + ",(" + logonDataValue.getClass().getSimpleName() + ")" + logonDataValue);
					logonData.put(logonDataKey.toLowerCase(), logonDataValue);					
				}
			}
		}
		// Create the instance with the body parameters
		Object eventClassInstance = LogonEventOutBeanFactory.create(eventType.substring(eventType.lastIndexOf('.') + 1), logonData);
		
		Class clazz = eventClass;
		// set header data by invoking the setter methods
		// be careful; this might get messy with overloaded methods
		clazz = clazz.getSuperclass();
		while (clazz != null) {
			methods = getParameterMethods(clazz);
			for (Iterator it=methods.iterator();it.hasNext();) {
				Method setter = (Method)it.next();
				String logonDataKey = setter.getName().substring(3);
				String logonDataStringValue = (String)pdaForm.getParameterValue(logonDataKey);
				if (StringUtils.notEmpty(logonDataStringValue)) {
					Object logonDataValue = parseLogonDataValue(setter.getParameterTypes()[0], logonDataStringValue);
					try {
						setter.invoke(eventClassInstance, logonDataValue);
						LOG.debug(setter.getName() + "((" +  logonDataValue.getClass().getSimpleName() + ")" + logonDataValue + ") invoked");
					}
					catch (Exception e) {
						throw new LogonException("Error setting parameter " + setter.getName(), e);
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
	
		try {
			getServiceLocator().getMopEventService().sendEvent((LogonEventOutBean)eventClassInstance);
			MessageUtils.addActionMessage(request, "errors.detail", "Message sent");
		} catch (javax.ejb.EJBException e) {
			if (e.getCause() instanceof LogonException) {
				LogonException logonException = (LogonException)e.getCause();
				if (logonException.getReason().equals(Reason.RES_ERROR_WSCLIENT)) {
					LOG.error(logonException.getMessage(), logonException);
					MessageUtils.addActionError(request, "errors.detail", "Failed to connect to webservice");
				}
				else {
					throw e;
				}
			}
			else {
				throw e;
			}
		}
		
		return true;
	}	
	
	
	/**
	 * Create a map of parameters which will be settable in the screen. 
	 * @param clazz The class for which to retrieve the parameters (based on the setter methods).
	 * @return A map<String, String> with key=parameter name, value=parameter type
	 */
	private Map getParameterMap(Class clazz) {
		Map parameters = new HashMap();
		List methods = getParameterMethods(clazz);
		for (int i=0;i<methods.size();i++) {
			Method m = (Method)methods.get(i);
			parameters.put(m.getName().substring(3), m.getParameterTypes()[0].getSimpleName());
		}
		
		return parameters;
	}
	
	/**
	 * Generates a list of setter methods on which the parameter fields are based.
	 * @param clazz The class for which to retrive the parameter list.
	 * @return A list<Method>.
	 */
	private List getParameterMethods(Class clazz) {
		List methods = new ArrayList();
		Method[] declMethods = clazz.getDeclaredMethods();
		if (methods != null) {
			for (int i=0;i<declMethods.length;i++) {
				Method m = declMethods[i];
				if ( (m.getName().startsWith("set")) && (m.getGenericParameterTypes().length == 1) ) {
					// method must be a setter taking a single argument
					methods.add(declMethods[i]);
				}
			}
		}
		
		return methods;
	}
	
	/**
	 * Converts a string value to an object of the given type.
	 * @param clazz The class of which the returned value must be.
	 * @param logonDataStringValue The string representation of the value.
	 * @return The converted object of type 'clazz'.
	 */
	private Object parseLogonDataValue(Class clazz, String logonDataStringValue) {
		Object paramValue = null;
		
		if (clazz.isAssignableFrom(List.class)) {
			// process list
			//String[] paramList = logonDataStringValue.split(";");
			//paramValue = Arrays.asList(paramList);
			return ""; // TODO: dummy, not implemented yet
		}
		else if (clazz.isAssignableFrom(Date.class)) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			try {
				paramValue = df.parse(logonDataStringValue);
			}
			catch (ParseException e) {
				throw new LogonException("Cannot process parameter type " + clazz.toString(), e);
			}
		}
		else if (clazz.isAssignableFrom(Long.class)) {
			paramValue = Long.parseLong(logonDataStringValue);
		}
		else if (clazz.isAssignableFrom(BigInteger.class)) {
			paramValue = new BigInteger(logonDataStringValue);
		}
		else if (clazz.isAssignableFrom(BigDecimal.class)) {
			paramValue = new BigDecimal(logonDataStringValue);
		}
		else if (clazz.isAssignableFrom(String.class)) {
			paramValue = logonDataStringValue;
		}
		else {
			throw new LogonException("Cannot process parameter type " + clazz.toString(), Reason.RES_ERROR_NOT_IMPL);
		}
		
		return paramValue;
	}
	
	private String parseDataValue(String key, Object value) {
		
		if (value == null) {
			
			return null;
		}
		if (value instanceof String) {
			
			return (String)value;
		}
		
		return null;
	}
}

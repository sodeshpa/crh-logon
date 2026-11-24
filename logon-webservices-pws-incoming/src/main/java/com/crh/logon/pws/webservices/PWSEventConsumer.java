/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pws.webservices;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.axis2.context.MessageContext;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interfaces.LogonEventService;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonEventSimulationException;
import com.crh.logon.constants.Constant;
import com.crh.logon.converters.ConversionException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.crh.logon.eventbeans.in.LogonEventRequestFactory;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailRequest;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import com.crh.logon.exception.WebservicesExceptionHandler;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * @author AdNovum Informatik AG
 */
public class PWSEventConsumer implements PWSEventConsumerInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PWSEventConsumer.class);
	
	private static final String MAIN_METHOD_NAME = "consume";
	private static final String PARALLEL_RAIL_DATE_PATTERN = "^\\d{2}.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2}:\\d{2}$";
	
	public PWSEventConsumer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "PWSEventConsumer");
		}
		// ]]
		try {
			if (logDebugEnabled) { // careful! the whole next block only prints information!
				MessageContext messageContext = MessageContext.getCurrentMessageContext();
				Iterator iter = messageContext.getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("1. " + key + ": " + messageContext.getProperty(key));
				}
				iter = messageContext.getOperationContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("2. " + key + ": " + messageContext.getOperationContext().getProperty(key));
				}
			
				iter = messageContext.getConfigurationContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("3. " + key + ": " + messageContext.getConfigurationContext().getProperty(key));
				}
			
				iter = messageContext.getServiceGroupContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("4. " + key + ": " + messageContext.getServiceGroupContext().getProperty(key));
				}
			
				iter = messageContext.getServiceContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("5. " + key + ": " + messageContext.getServiceContext().getProperty(key));
				}
			}
			else {
				MessageContext messageContext = MessageContext.getCurrentMessageContext();
				LOG.debug("REMOTE_ADDR		   : " + messageContext.getProperty(MessageContext.REMOTE_ADDR));
				LOG.debug("CHARACTER_SET_ENCODING: " + messageContext.getProperty(MessageContext.CHARACTER_SET_ENCODING));
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
	

	public PWSEventResponse consume(PWSEventRequest pwsRequest) {
		// [[
		PWSEventResponse result = null;
		Object[][] params = {{"pwsRequest", pwsRequest}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "consume", params);
		}
		// ]]
		try {
			
			PWSEventResponse pwsResponse = new PWSEventResponse();
			ParallelRailRequest request = null;
			ParallelRailResponse response = null;
			try {
				try {
					request = createLogonEventBean(pwsRequest);
				} 
				catch (ConversionException ce) {
					try {
						response = ParallelRailResponse.class.newInstance();
					}
					catch (Exception e) {
						
						throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
					}
					response.setCode(ParallelRailResponseCode.MANDATORY_FIELD_ERROR.value());
					response.setContextValue(ce.getPropertyName());
					response.setTxt(ServiceLocatorProvider.getServiceLocator().getLogonEventService().getMopStatusTxt(Locale.getDefault(), MopResponseCode.MANDATORY_FIELD_ERROR.value(), new Object[] { ce.getPropertyName(), ce.getPropertyValue() }));
				}
				response = processLogonEventServiceBean(request, Constant.INTERFACE_TYPE_PWS_10, params);
				if (response == null) {
					
					throw new LogonException("No response", Reason.RES_ERROR_IMPL);
				}
				pwsResponse.setCode(response.getCode());
				pwsResponse.setTxt(response.getTxt());
				pwsResponse.setContextValue(response.getContextValue());
				pwsResponse.setResponseData(getResponseData(response));
			} catch (Throwable t) {
				ExceptionUtil.errException(LOG, t, params);
				pwsResponse.setCode(ParallelRailResponseCode.TECHNICAL_ERROR.value());
				if (t instanceof LogonException) {
					LogonException ex = (LogonException)t;
					pwsResponse.setTxt(ex.getMessage() + " (" + ex.getId() + ")");
				}
				else {
					pwsResponse.setTxt(WebservicesExceptionHandler.handle(t, getClass().getSimpleName(), MAIN_METHOD_NAME));
				}
			}
			LOG.info("IN: ParallelRail Event Sender=" + (request!=null?request.getSenderInfo():"") +
					", status=" + pwsResponse.getCode() + 
					", text=" + pwsResponse.getTxt() +
	                ", contextValue=" + pwsResponse.getContextValue() +
	                ", event=" + (request!=null?request.getEventName():""));
			
			return result = pwsResponse;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private Object parseValue(String name, String strValue) {
		
		Object value = strValue;
		if ( (name.endsWith("time")) && (strValue.matches(PARALLEL_RAIL_DATE_PATTERN)) ) {
			// Parallel rail incorrectly sends the date in format dd.MM.yyyy HH:mm:ss
			StringBuilder sb = new StringBuilder(strValue.substring(6, 10));
			sb.append("-");
			sb.append(strValue.substring(3, 5));
			sb.append("-");
			sb.append(strValue.substring(0, 2));
			sb.append("T");
			sb.append(strValue.substring(11));
			
			return sb.toString();
		}
		
		return value;
	}
	
	
	private Map getDataElementsAsMap(DataElement[] deArray) {
		
		Map dataMap = new HashMap();
		for (DataElement de: deArray) {
			if (de.getDataElementChoice_type0() != null) {
				String name = de.getName().toLowerCase();
				if (de.getDataElementChoice_type0().getValue() != null) {
					dataMap.put(name, parseValue(name, de.getDataElementChoice_type0().getValue()));
				}
				else if (de.getDataElementChoice_type0().getMultiValue() != null) {
					MultiValue_type0 mv = de.getDataElementChoice_type0().getMultiValue();
					if (mv.getValue() != null) {
						List mvList = new ArrayList();
						for (String s: mv.getValue()) {
							mvList.add(parseValue(name, s));
						}
						dataMap.put(de.getName().toLowerCase(), mvList);
					}
				}
				else if (de.getDataElementChoice_type0().getData() != null) {
					List dataList = new ArrayList();
					for (Data_type0 dt: de.getDataElementChoice_type0().getData()) {
						if (dt.getDataElement() != null) {
							Map nestedDataMap = getDataElementsAsMap(dt.getDataElement());
							dataList.add(nestedDataMap);
						}
					}
					dataMap.put(name, dataList);
				}
			}
		}
			
		return dataMap;
	}
	
	
	private ParallelRailRequest createLogonEventBean(PWSEventRequest request) {
		// [[
		ParallelRailRequest result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createLogonEventBean", params);
		}
		// ]]
		try {
			Map allProps = new HashMap();
			if ( (request.getEventData() != null) && (request.getEventData().getDataElement() != null) ) {
				allProps = getDataElementsAsMap(request.getEventData().getDataElement());
			}
			ParallelRailRequest bean = (ParallelRailRequest)LogonEventRequestFactory.ParallelRailFactory.create(request.getEventName(), allProps);
			bean.setSenderInfo(request.getSenderInfo());
			bean.setEventTime(request.getSendersTime().getTime());
						
			return result = bean;
		}
		catch (RuntimeException re) {
			throw re;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private DataElementChoice_type0 getDataElementChoice(Object obj) {
		
		if (obj == null) {
		
			return null;
		}
		DataElementChoice_type0 dec = null;
		if (obj instanceof String) {
			dec = new DataElementChoice_type0();
			dec.setValue((String)obj);
		}
		else if (obj instanceof Date) {
			dec = new DataElementChoice_type0();
			dec.setValue(DateTimeFormat.forPws((Date) obj));
		}
		else if (obj instanceof List) {
			List list = null;
			list = (List)obj;
			if (list != null) {
				dec = new DataElementChoice_type0();
				if (!list.isEmpty()) {
					if (list.get(0) instanceof String) {
						String[] multiValue = (String[])list.toArray(new String[] {});
						MultiValue_type0 mv = new MultiValue_type0();
						mv.setValue(multiValue);
						dec.setMultiValue(mv);
					}
					else {
						Data_type0[] data = new Data_type0[list.size()];
						for (int i=0;i<list.size();i++) {
							data[i] = new Data_type0();
							data[i].setDataElement(getDataElements(list.get(i)));
						}
						dec.setData(data);
					}
				}
			}
		}
		else if (obj.getClass().getPackage().getName().startsWith("com.crh.logon")) {
			// Parse this as a nested bean
			dec = new DataElementChoice_type0();
			Data_type0[] data = new Data_type0[1];
			data[0] = new Data_type0();
			DataElement[] dataElements = getDataElements(obj);
			data[0].setDataElement(dataElements);
			dec.setData(data);
		}
		else {
			// Parse this with "toString", for BigInteger, BigDecimal, Boolean, etc.
			dec = new DataElementChoice_type0();
			dec.setValue(obj.toString());
		}
		
		return dec;
	}
	
	private DataElement[] getDataElements(Object obj) {
		
		if (obj != null) {
			if ( (obj instanceof String) || (obj instanceof List) ) {
				DataElement[] dataElements = new DataElement[1];
				dataElements[0] = new DataElement();
				dataElements[0].setName("data");
				dataElements[0].setDataElementChoice_type0(getDataElementChoice(obj));
				
				return dataElements;
			}
			PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(obj);
			if ( (propertyDescriptors != null) && (propertyDescriptors.length > 0) ) {
				List dataElements = new ArrayList();
				for (PropertyDescriptor pd: propertyDescriptors) {
					if (pd.getPropertyType().equals(Class.class)) {
						continue;
					}
					DataElement de = new DataElement();
					de.setName(pd.getName());
					Object objProp = null;
					try {
						objProp = PropertyUtils.getProperty(obj, pd.getName());
					} catch (Exception e) {
						throw new LogonException(e);
					}
					if (objProp != null) {
						DataElementChoice_type0 dec = getDataElementChoice(objProp);
						if (dec != null) {
							de.setDataElementChoice_type0(dec);
							dataElements.add(de);
						}
					}
				}
				
				if (!dataElements.isEmpty()) {
				
					return (DataElement[])dataElements.toArray(new DataElement[] {});
				}
			}
		}
		
		return null;
	}
	
	
	private DataElement[] getDataElements(ParallelRailResponse logonServiceResponse) {
		
		if (logonServiceResponse != null) {
			PropertyDescriptor[] ignorePds = PropertyUtils.getPropertyDescriptors(ParallelRailResponse.class);
			PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(logonServiceResponse);
			if ( (propertyDescriptors != null) && (propertyDescriptors.length > 0) ) {
				List dataElements = new ArrayList();
				for (PropertyDescriptor pd: propertyDescriptors) {
					if (pd.getPropertyType().equals(Class.class)) {
						continue;
					}
					if (ArrayUtils.contains(ignorePds, pd)) {
						continue;
					}
					DataElement de = new DataElement();
					de.setName(pd.getName());
					Object objProp = null;
					try {
						objProp = PropertyUtils.getProperty(logonServiceResponse, pd.getName());
					} catch (Exception e) {
						throw new LogonException(e);
					}
					if (objProp != null) {
						DataElementChoice_type0 dec = getDataElementChoice(objProp);
						if (dec != null) {
							de.setDataElementChoice_type0(dec);
							dataElements.add(de);
						}
					}
				}
				
				if (!dataElements.isEmpty()) {
				
					return (DataElement[])dataElements.toArray(new DataElement[] {});
				}
			}
		}
		
		return null;
	}
	
	private ResponseData_type0 getResponseData(ParallelRailResponse response) {
		
		ResponseData_type0 rd = null;
		DataElement[] dataElements = getDataElements(response);
		if ( (dataElements != null) && (dataElements.length > 0) ) {
			rd = new ResponseData_type0();
			rd.setDataElement(dataElements);
		}
		
		return rd;
	}
	
	private ParallelRailResponse processLogonEventServiceBean(LogonEventService logonEventService, LogonEventRequest request, long ifcType, Object[][] params) {
		ParallelRailResponse response = null;
		try {
			response = (ParallelRailResponse)logonEventService.processEventBean(request, ifcType);					
		}
		catch (LogonEventSimulationException e) {
			// this is an intended exception, do not log an error
			response = (ParallelRailResponse) e.getLogonEventResponse();
		}
		catch (LogonEventException logonEventException) {
			ExceptionUtil.errException(LOG, logonEventException, params);
			String requestClassName = request.getClass().getSimpleName();
			try {
				RequestHandlerLocalInterface handler = LocalServices.getLocalInterfaceForRequestHandler(requestClassName);
				String resolvedText = handler.getResponseMessage(request, logonEventException.getLogonEventResponse().getCode(), logonEventException.getFormatParameters());
				logonEventException.getLogonEventResponse().setTxt(resolvedText);
			}
			catch (Exception exception) {
				LOG.error("Could not resolve language dependent message.", exception);
			}
			response = (ParallelRailResponse)logonEventException.getLogonEventResponse();
		}			
		return response; 		
	}
	
	private ParallelRailResponse processLogonEventServiceBean(LogonEventRequest request, long ifcType, Object[][] params) {
		return processLogonEventServiceBean(ServiceLocatorProvider.getServiceLocator().getLogonEventService(), request, ifcType, params);
	}
	
}

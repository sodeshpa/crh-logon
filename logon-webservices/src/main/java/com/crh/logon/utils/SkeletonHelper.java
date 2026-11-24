/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.crh.logon.exception.WsServerSideException;
import com.crh.logon.webservices.services.SapService;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class SkeletonHelper {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(SkeletonHelper.class);

	private static final String ERRORMSG_CALLMETHODFOR = "unable to invoke the requested method for the provided object.";

	private static final String ERRORMSG_INVALIDREQUEST = "unable to parse the incoming SOAP-request."
			+ " Check, if it is valid according to the WSDL definition of its service, please!";

	private static final String MEMBER_DOCUMENT_FACTORY = "Factory";

	private static final String METHOD_ADDNEWSTATUS = "addNewStatus";

	private static final String METHOD_GETSTATUS = "getStatus";

	private static final String METHOD_NEWINSTANCE = "newInstance";

	private static final String METHOD_SETCODE = "setCode";

	private static final String METHOD_SETTXT = "setTxt";

	private static final BigInteger RESDT_CODE_SUCCESS = BigInteger.valueOf(1);

	private static final BigInteger RESDT_CODE_FAILURE = BigInteger.valueOf(-1);

	private static final String RESDT_TXT_SUCCESS = "operation successful.";

	private static final String RESDT_TXT_FAILURE = "operation FAILED.";

	private static final String PREFIX_ADD_NEW = "addNew";

	private static final String PREFIX_GETTER_METHOD = "get";

	private static final String POSTFIX_DOCUMENT_IMPL = "DocumentImpl";
	
	/**
	 * @param object
	 * @param methodName
	 * @param parameters
	 * @param parameterTypes
	 * @return
	 */
	public static final Object callMethodFor(Object object, String methodName,
			Object[] parameters, Class[] parameterTypes) {
		// [[
		Object result = null;
		Object[][] params = {{"object", object},
							{"methodName", methodName},
							{"parameters", parameters},
							{"parameterTypes", parameterTypes}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SkeletonHelper.class, "callMethodFor", params);
		}
		// ]]
		try {
			if ((object != null) && (StringUtils.notEmpty(methodName))) {
				Class clazz = (object instanceof Class ? (Class) object
						: object.getClass());
				Method method = clazz.getMethod(methodName, parameterTypes);
			
				result = method.invoke(object, parameters);
			}
			
			return result;
		}
		catch (InvocationTargetException itException) {
			Throwable throwable = itException.getTargetException();
		
			if (throwable instanceof Error) {
				throw new WsServerSideException(
						WsServerSideException.TYPE_UNCHECKED_ERROR, itException);
			}
		
			throw new WsServerSideException(WsServerSideException.TYPE_UNKNOWN,
					ERRORMSG_CALLMETHODFOR, itException);
		} 
		catch (Exception exception) {
			throw new WsServerSideException(WsServerSideException.TYPE_UNKNOWN,
					ERRORMSG_CALLMETHODFOR, exception);
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
	 * @param requestDocument
	 * @param translatedDocument
	 */
	public static void checkSoapRequest(Object requestDocument,
			Object translatedDocument) {
		// [[
		Object[][] params = {{"requestDocument", requestDocument},
							{"translatedDocument", translatedDocument}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SkeletonHelper.class, "checkSoapRequest", params);
		}
		// ]]
		try {
			if ((requestDocument == null) || (translatedDocument == null)) {
				LOG.error(ERRORMSG_INVALIDREQUEST);
				LOG.error("Request: " + requestDocument);
				LOG.error("Translated request: " + translatedDocument);
			
				throw new WsServerSideException(
						WsServerSideException.TYPE_INVALID_INPUT,
						ERRORMSG_INVALIDREQUEST);
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
	 * @param clazz
	 * @param serviceName
	 * @param operationName
	 * @param errorDetails
	 * @return
	 */
	public static final Object composeResponse(Class clazz, String serviceName,
			String operationName, String errorDetails) {
		// [[
		Object result = null;
		Object[][] params = {{"clazz", clazz},
							{"serviceName", serviceName},
							{"operationName", operationName},
							{"errorDetails", errorDetails}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SkeletonHelper.class, "composeResponse", params);
		}
		// ]]
		try {
			Object responseDocument = createWebserviceResponseObjects(clazz);
			Object responseStatus = getWebserviceResponseStatus(responseDocument);
			boolean isErroneous = StringUtils.notEmpty(errorDetails);
			BigInteger statusCode = RESDT_CODE_SUCCESS;
			String statusText = null;
			String details = RESDT_TXT_SUCCESS;
			
			if (isErroneous) {
				statusCode = RESDT_CODE_FAILURE;
				details = RESDT_TXT_FAILURE + "\n " + errorDetails;
			}
			
			statusText = getHeaderOfSrvMessages(serviceName, operationName,
					details);
			
			callMethodFor(responseStatus, METHOD_SETCODE,
					new Object[] { statusCode },
					new Class[] { BigInteger.class });
			callMethodFor(responseStatus, METHOD_SETTXT,
					new Object[] { statusText }, new Class[] { String.class });
			
			return (result = responseDocument);
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
	 * @return
	 */
	public static final SapService getSapService() {
		// [[
		SapService result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SkeletonHelper.class, "getSapService");
		}
		// ]]
		try {
			Context ctx = new InitialContext();
			SapService sap = (SapService) ctx.lookup("java:global/logon/logon-webservices/SapServiceBean!com.crh.logon.webservices.services.SapService");
			
			return (result = sap);
		}
		catch (NamingException e) {
			throw new RuntimeException(e);
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
	 * @param responseClass
	 * @return
	 */
	private static final Object createWebserviceResponseObjects(
			Class responseClass) {
		// [[
		Object result = null;
		Object[][] params = {{"responseClass", responseClass}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SkeletonHelper.class, "createWebserviceResponseObjects", params);
		}
		// ]]
		try {
			Object responseDocument = null;
			
			if (responseClass != null) {
				for (Class innerClass : responseClass.getClasses()) {
					String innerClassName = innerClass.getSimpleName();
			
					if (StringUtils.notEmpty(innerClassName)
							&& innerClassName.endsWith(MEMBER_DOCUMENT_FACTORY)) {
						responseDocument = callMethodFor(innerClass,
								METHOD_NEWINSTANCE, null, null);
			
						break;
					}
				}
			
				if (responseDocument != null) {
					String documentName = responseDocument.getClass()
							.getSimpleName();
					String nameOfAddNewMethod = PREFIX_ADD_NEW
							+ documentName
									.replaceAll(POSTFIX_DOCUMENT_IMPL, "");
					Object response = callMethodFor(responseDocument,
							nameOfAddNewMethod, null, null);
			
					if (response != null) {
						callMethodFor(response, METHOD_ADDNEWSTATUS, null, null);
					}
				}
			}
			
			return (result = responseDocument);
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
	 * @param serviceName
	 * @param operationName
	 * @param details
	 * @return
	 */
	private static final String getHeaderOfSrvMessages(String serviceName,
			String operationName, String details) {
		return "Service: " + serviceName + "; Operation: " + operationName
				+ "; Details: " + details;
	}

	/**
	 * @param responseDocument
	 * @return
	 */
	private static final Object getWebserviceResponseStatus(
			Object responseDocument) {
		// [[
		Object result = null;
		Object[][] params = {{"responseDocument", responseDocument}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SkeletonHelper.class, "getWebserviceResponseStatus", params);
		}
		// ]]
		try {
			Object responseStatus = null;
			
			if (responseDocument != null) {
				Class responseDocumentClass = responseDocument.getClass();
				String responseDocumentName = responseDocumentClass
						.getSimpleName();
				String methodName = PREFIX_GETTER_METHOD
						+ responseDocumentName.replaceAll(
								POSTFIX_DOCUMENT_IMPL, "");
				Object response = callMethodFor(responseDocument, methodName,
						null, null);
			
				if (response != null) {
					responseStatus = callMethodFor(response, METHOD_GETSTATUS,
							null, null);
				}
			}
			
			return (result = responseStatus);
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.math.BigInteger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.exception.WsServerSideException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class EntityHelper {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(EntityHelper.class);

	private static final int SAPIMPSTATUSCD_IMPORTED_FROM_SAP = 0;

	private static final String METHOD_SETSAPIMPIMPORTEDDAT = "setSapimpImportedDat";

	private static final String METHOD_SETSAPIMPSTATUSCD = "setSapimpStatusCd";

	/**
	 * @param entity
	 */
	public static final void setControllingAttributes(Object entity) {
		// [[
		Object[][] params = {{"entity", entity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(EntityHelper.class, "setControllingAttributes", params);
		}
		// ]]
		try {
			XMLGregorianCalendar calendar = DateTimeUtils
					.getTodayAsXmlGregorianCalendar();
			
			SkeletonHelper.callMethodFor(entity, METHOD_SETSAPIMPSTATUSCD,
					new Object[] { BigInteger
							.valueOf(SAPIMPSTATUSCD_IMPORTED_FROM_SAP) },
					new Class[] { BigInteger.class });
			
			SkeletonHelper.callMethodFor(entity, METHOD_SETSAPIMPIMPORTEDDAT,
					new Object[] { calendar },
					new Class[] { XMLGregorianCalendar.class });
		}
		catch (DatatypeConfigurationException dcException) {
			throw new WsServerSideException(
					WsServerSideException.TYPE_UNCHECKED_ERROR, dcException);
		} 
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}

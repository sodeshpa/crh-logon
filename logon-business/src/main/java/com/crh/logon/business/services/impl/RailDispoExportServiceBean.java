/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ejb.Stateless;

import com.crh.logon.business.export.raildispo.RailDispoExporter;
import com.crh.logon.business.services.beans.RailDispoExportParameters;
import com.crh.logon.business.services.dispatchers.RailDispoExportServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.RailDispoExportServiceLocalInterface;
import org.apache.poi.ss.usermodel.Workbook;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class RailDispoExportServiceBean extends RailDispoExportServiceDispatcher
	implements RailDispoExportServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDispoExportServiceBean.class);

	public byte[] export(RailDispoExportParameters exportParameters) {
		// [[
		byte[] result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "export");
		}
		// ]]
		try {

			RailDispoExporter exporter = new RailDispoExporter(exportParameters);
			Workbook workbook = exporter.export();
			
			// Write workbook to outPutStream
			try {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				workbook.write(baos);
				result = baos.toByteArray();

				return result;

			} catch (IOException exception) {
				String message = "Rail dispo export to excel failed";
				LOG.error(message);
				throw new LogonException(message, exception);
			}

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
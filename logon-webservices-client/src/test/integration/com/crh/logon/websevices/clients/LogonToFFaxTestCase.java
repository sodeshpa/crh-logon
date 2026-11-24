/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.websevices.clients;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.webservices.clients.LogonToFFax;
import com.crh.logon.webservices.clients.beans.ferrari.FileMessageBean;
import com.crh.logon.webservices.clients.beans.ferrari.ResponseBean;
import com.crh.logon.webservices.clients.beans.ferrari.StationeriesBean;
import com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean;

/**
 * @author AdNovum Informatik AG
 */
public class LogonToFFaxTestCase extends BaseClientTestCase {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(LogonToFFaxTestCase.class.getName());

	private static final String HELLO_WORLD = "Hello World!";

	/**
	 * @param arg0
	 */
	public LogonToFFaxTestCase(String arg0) {
		super(arg0);
		
		// [[
		Object[][] params = {{"arg0", arg0}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "LogonToFFaxTestCase", params);
		}
		// ]]
		try {
			; // PMD
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
	 * 
	 */
	public void testHelloWorld() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testHelloWorld");
		}
		// ]]
		try {
			String temp = LogonToFFax.helloWorld(this.configBean);
			
			assertEquals("FFax.HelloWorld sent bad response.", HELLO_WORLD,
					temp);
			
			LOG.info("FFax.HelloWorld: " + temp);
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
	 * 
	 */
	public void testGetStationeries() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testGetStationeries");
		}
		// ]]
		try {
			StationeriesBean stationeries = LogonToFFax
					.getStationeries(this.configBean);
			
			assertNotNull("FFax.GetStationeries: response is null.",
					stationeries);
			
			assertEquals(
					"FFax.GetStationeries: error during the call - bad response.",
					Boolean.TRUE.toString(), Boolean.toString(stationeries
							.isSuccessful()));
			
			LOG.info("FFax.GetStationeries: " + stationeries);
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
	 * 
	 */
	public void testSendFaxFile() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testSendFaxFile");
		}
		// ]]
		try {
			final String operationName = "SendFaxFile";
			
			final String number = "+36 1 487 50 00";
			final String mimeType = "application/pdf";
			final String pdfFileName = "testdoc.pdf";
			final String pdfFilePath = "src/test/resources/logontoffax/" + pdfFileName;
			
			byte[] content = null;
			FileMessageBean fBean = null;
			ResponseBean response = null;
			
			try {
				content = getFileContent(pdfFilePath);
			}
			catch (IOException ioException) {
				ExceptionUtil.errException(LOG, ioException, null);
			
				fail("Unable to open and retrieve the content of the testfile: "
						+ pdfFilePath + "!");
			}
			
			assertNotNull("Testfile is empty!", content);
			
			fBean = new FileMessageBean(number, mimeType, pdfFileName, content);
			response = LogonToFFax.sendFaxFile(fBean, this.configBean);
			checkFerrariResponse(response, operationName, Boolean.TRUE
					.toString());
			//TODO (dom Jun 8, 2009): why sending non-specified keywords to trigger error responses from mockup?
			//			fBean = new FileMessageBean(ARGSNUMBER_UNSUCCESSFUL_CALL, mimeType,
			//					pdfFileName, content);
			//			response = LogonToFFax.sendFaxFile(fBean, this.configBean);
			//			checkFerrariResponse(response, operationName, Boolean.FALSE
			//					.toString());
			//
			//			fBean = new FileMessageBean(ARGSNUMBER_EMPTYRESPONSE, mimeType,
			//					pdfFileName, content);
			//			response = LogonToFFax.sendFaxFile(fBean, this.configBean);
			//			checkFerrariResponse(response, operationName, Boolean.FALSE
			//					.toString());
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
	 * 
	 */
	public void testSendFaxText() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testSendFaxText");
		}
		// ]]
		try {
			final String operationName = "SendFaxText";
			final String argsText = "Greetings from AdNovum!";
			final String argsNumberAny = "+36 1 487 50 00";
			ResponseBean response = null;
			
			response = LogonToFFax.sendFaxText(new TextMessageBean(
					argsNumberAny, argsText), this.configBean);
			checkFerrariResponse(response, operationName, Boolean.TRUE
					.toString());
			//TODO (dom Jun 8, 2009): why sending non-specified keywords to trigger error responses from mockup?
			//			response = LogonToFFax.sendFaxText(new TextMessageBean(
			//					ARGSNUMBER_UNSUCCESSFUL_CALL, argsText), this.configBean);
			//			checkFerrariResponse(response, operationName, Boolean.FALSE
			//					.toString());
			//
			//			response = LogonToFFax.sendFaxText(new TextMessageBean(
			//					ARGSNUMBER_EMPTYRESPONSE, argsText), this.configBean);
			//			checkFerrariResponse(response, operationName, Boolean.FALSE
			//					.toString());
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
	 * @param testFilePath
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileContent(String testFilePath) throws IOException {
		// [[
		byte[] result = null;
		Object[][] params = {{"testFilePath", testFilePath}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getFileContent", params);
		}
		// ]]
		BufferedInputStream biStream = null;
		try {
			final int blockSize = 2048;
			
			final File testFile = new File(testFilePath);
			final InputStream fiStream = new FileInputStream(testFile);
			final List<byte[]> content = new ArrayList<byte[]>();
			byte[] buffer = new byte[blockSize];
			int totalNbrOfBytesRead = 0;
			int nbrOfBytesRead = 0;
			
			biStream = new BufferedInputStream(fiStream);			
			while ((nbrOfBytesRead = biStream.read(buffer)) != -1) {
				content.add(buffer);
			
				totalNbrOfBytesRead += nbrOfBytesRead;
			}
			
			if (totalNbrOfBytesRead > 0) {
				result = new byte[totalNbrOfBytesRead];
			
				for (int pIndex = 0; pIndex < content.size(); pIndex++) {
					byte[] block = (byte[]) content.get(pIndex);
			
					if (block != null) {
						int blockLength = (pIndex == (content.size() - 1) ? (totalNbrOfBytesRead % blockSize)
								: blockSize);
			
						for (int bIndex = 0; bIndex < blockLength; bIndex++) {
							result[pIndex * blockSize + bIndex] = block[bIndex];
						}
					}
				}
			}
			
			return result;
		}
		finally {
			if (biStream != null) {
				biStream.close();
			}
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

}

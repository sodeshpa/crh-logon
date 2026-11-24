/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.testclient;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * This class gets all the request files.
 * 
 * @author AdNovum Informatik AG
 */
final class RequestFetcher {

	/**
	 * SOAP MessageFactory used to parse the full *.request files.
	 */
	private final static MessageFactory SoapMessageFactory;
	
	static {
		try {
			SoapMessageFactory = MessageFactory.newInstance();
		} catch (SOAPException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/**
	 * Filers out files by the specified extension
	 * 
	 * @author AdNovum Informatik AG
	 */
	private final static class FileExtensionFilter implements FileFilter {
		
		private final String extension;
		
		private FileExtensionFilter(String extension) {
			if (StringUtils.isNotEmpty(extension)) {
				this.extension = extension;
			}
			else {
				this.extension = null;
			}
		}
		
		public boolean accept(File pathname) {
			if (!pathname.isFile()) {
				return false;
			}
			return (extension == null) || pathname.getName().endsWith(extension);			
		}
		
	}

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RequestFetcher.class);
	
	private final File directory;
	private final FileFilter fileExtensionFilter;

	/**
	 * @param directory an existing, readable directory
	 * @param extension
	 */
	RequestFetcher(File directory, String extension) {
		this.directory = directory;
		this.fileExtensionFilter = new FileExtensionFilter(extension);
	}
	
	/**
	 * 
	 * @param orderFileName the file which contains ordering information
	 * @param explicitOrder whether only explicitly ordered request files are included in the test
	 * @return ordered request files
	 */
	Collection<File> getAllRequestFiles(String orderFileName, boolean explicitOrder) {
		final boolean debugEnabled = LOG.isDebugEnabled();
		final Collection<File> result = new ArrayList<File>();
		if (debugEnabled) {
			Object[][] params = {
					{"orderFileName", orderFileName},
					{"explicitOrder", explicitOrder}};
			LOG.entering(this, "getRequestFiles", params);
		}
		try {
			// process the order file
			final Collection<String> orderOfTestcases = new ArrayList<String>();
			boolean infoEnabled = LOG.isInfoEnabled();
			if (StringUtils.isNotEmpty(orderFileName)) {
				final File orderOfTestcasesFile = new File(directory, orderFileName); 
				
				LineNumberReader in = null;
				try {
					in = new LineNumberReader(new FileReader(orderOfTestcasesFile));

					String line;
					int count = 0;
					while ((line = in.readLine()) != null) {
						if (!line.startsWith("#")) {
							orderOfTestcases.add(line.trim());
							count++;
						}
					}
					if (infoEnabled) {
						LOG.info(String.format(
								"Processing available testfiles according to the order defined in: '%1$s'",
								orderOfTestcasesFile));
						LOG.info(String.format("%1$s orders defined in total.",	count));
					}
				}
				catch (FileNotFoundException e) {
					if (infoEnabled) {
						LOG.info(String.format(
								"The specified order file (%s) does not exist (processing without explicit ordering)!",
								orderOfTestcasesFile));
					}
				}
				catch (IOException e) {
					LOG.error(String.format(
							"IO error (%2$s) while reading the specified order file (%1$s)!",
							orderOfTestcasesFile, e.getMessage()));
					
					orderOfTestcases.clear();
				}
				finally {
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {
							LOG.error(String.format(
									"IO error (%2$s) while closing the specified order file (%1$s)!",
									orderOfTestcasesFile, e.getMessage()));
						}
					}
				}
			}

			final File[] testFiles = directory.listFiles(this.fileExtensionFilter);

			if (infoEnabled) {
				LOG.info("The following " + testFiles.length + " file(s) will be processed:");
				for (int i = 0; i < testFiles.length; ++i) {
					String absolutePath = testFiles[i].getAbsolutePath();
					LOG.info("  - " + absolutePath);
				}
			}
			
			final Map<String, File> testFilesMap = new HashMap<String, File>();
			for (int i=0; i< testFiles.length; i++) {
				testFilesMap.put(testFiles[i].getName(), testFiles[i]);
			}
			
			// add ordered testcases first
			for (String testCase : orderOfTestcases) {
				if (testFilesMap.containsKey(testCase)) {
					result.add(testFilesMap.remove(testCase));
				}
				else {
					LOG.error(String.format(
							"There is no testcase named '%s' (defined in the order file)!",
							testCase));						
				}
			}

			if (infoEnabled) {
				if (!explicitOrder) {
					if (!orderOfTestcases.isEmpty()) {
						LOG.info(String.format(
								"From the file(s) %s were explicitly ordered, and %s added at the end without ordering.",
								result.size(), testFilesMap.size()));			
					}
				}
				else {
					if (orderOfTestcases.isEmpty()) {	
						LOG.info("No requests will be processed.");			
					}
					else {
						LOG.info(String.format(
								"From the file(s) %s were explicitly ordered, and none are added at the end without ordering.",
								result.size()));			
					}					
				}
			}
			
			if (!explicitOrder) {
				// add remaining testcases
				result.addAll(testFilesMap.values());
			}
			
			return result;
		}
		finally {
			if (debugEnabled) {
				LOG.exiting();
			}
		}
	}

	/**
	 * This method reads the *.request files, and after parsing pairs them up with services
	 * based on the put operation found in the SOAP envelope.
	 * 
	 * @param services the supported services
	 * @param files *.request files
	 * @return
	 */
	static Map<ServiceCaller, Collection<File>> mapRequestFilesToServices(Collection<ServiceCaller> services, Collection<File> files) {
		final boolean debugEnabled = LOG.isDebugEnabled();
		final boolean infoEnabled = LOG.isInfoEnabled();
		final Map<ServiceCaller, Collection<File>> result = new HashMap<ServiceCaller, Collection<File>>();
		if (debugEnabled) {
			Object[][] params = { { "files", files} };
			LOG.entering(RequestFetcher.class, "mapRequestFilesToServices", params);
		}
		try {
			final Map<String, ServiceCaller> operation2Service = new HashMap<String, ServiceCaller>();
			for (ServiceCaller service : services) {
				operation2Service.put(service.getOperationName(), service);
			}
			
			String operationName;
			Collection<File> operationFiles;
			SOAPMessage soapMessage; 
			Iterator<?> iter;
			Object element;
			ServiceCaller service;
			
			for (File file : files) {
				try {
					soapMessage = SoapMessageFactory.createMessage(null, new FileInputStream(file));

					operationName = null;
					iter = soapMessage.getSOAPBody().getChildElements();					
					while (iter.hasNext()) {
						element = iter.next();
						
						if (element instanceof SOAPBodyElement) {
							operationName = ((SOAPBodyElement)element).getLocalName();
						}
					}
					
					if (operationName == null) {
						LOG.error(String.format(
								"The request file '%s' does not contain an operation!", 
								file));
						continue;
					}

					if (infoEnabled) {
						LOG.info(String.format(
								"Mapped request file '%s' to operation '%s'.", 
								file, 
								operationName));
					}
					
					service = operation2Service.get(operationName);
					
					if (service == null) {
						LOG.error(String.format(
										"Could not map operation %s to a service!",
										operationName));
						continue;
					}
					if (infoEnabled) {
						LOG.info(String.format("Mapped operation '%s' to service '%s'.",
								operationName, service.getName()));
					}
					
					operationFiles = result.get(service);
					if (operationFiles == null) {
						operationFiles = new ArrayList<File>();
					}
					operationFiles.add(file);
					result.put(service, operationFiles);

				} catch (SOAPException e) {
					LOG.error(String.format(
							"The request file '%s' is not a well formed SOAP envelope!", 
							file));
					
				} catch (IOException e) {
					LOG.error(String.format(
							"IO error (%2$s) while reading a request file (%1$s)! Skipping.",
							file, e.getMessage()));					
				}
			}
			
			return result;
		}
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}			
	}
}
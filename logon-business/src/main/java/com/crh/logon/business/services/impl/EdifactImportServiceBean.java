/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.edifact.parser.EdifactParser;
import com.crh.logon.business.edifact.sbb.SbbIftstaBean;
import com.crh.logon.business.edifact.sbb.SbbIftstaHandler;
import com.crh.logon.business.services.dispatchers.EdifactImportServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EdifactImportServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.EdifactParseException;
import com.crh.logon.business.services.utils.FtpClientFactory;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.WagonEventEntity;
import com.crh.logon.persistence.entities.refdata.WagonEventStatusBsEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author AdNovum Informatik AG
 */
@TransactionManagement(TransactionManagementType.BEAN)
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class EdifactImportServiceBean extends EdifactImportServiceDispatcher implements EdifactImportServiceLocalInterface {

	private static final String FTP_SERVICE_NAME_EDIFACT_IMPORT = "edifact_import";

	private static final String STATUS_DESC_ARRIVED = "eingangskontrolle";

	private static final String STATUS_CODE_ARRIVED = "8";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EdifactImportServiceBean.class);

	@Resource
	private UserTransaction tx;

	@EJB
	private RefDataGenericsServiceLocalInterface referentialDataService;

	private String formatRws(Map<String, String> rwsCache, String fullCode) {
		if ((fullCode == null) || (fullCode.length() < 3)) {
			return fullCode;
		}
		else if (rwsCache.containsKey(fullCode)) {
			return rwsCache.get(fullCode);
		}
		else {
			final ReadWriteDao<RailwayStationEntity> dao = getDaoProvider().getRailwayStationEntityDao();
			final Filter<RailwayStationEntity> filter = dao.createFilter();
			filter.addExpression(
					Restrictions.eq(RailwayStationEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(fullCode.substring(0, 2))));
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, fullCode.substring(2)));
			filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));

			final RailwayStationEntity rws = dao.findSingle(filter);
			final String rwsString;
			if (rws != null) {
				rwsString = fullCode + " (" + rws.getRailwayStationName() + ")";
			}
			else {
				rwsString = fullCode;
			}
			rwsCache.put(fullCode, rwsString);
			return rwsString;
		}
	}

	private static String formatDate(DateFormat df, Date date) {
		if (date == null) {
			return null;
		}
		else {
			return df.format(date);
		}
	}

	private File getImportDumpFile(String fileName, boolean newFile) throws IOException {
		final Date now = new Date();
		final DateFormat dirDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final File dirFile = new File(getConfigBean().getEdifactImportDumpDir() + File.separator + dirDateFormat.format(now));

		if (!dirFile.exists() && !dirFile.mkdirs()) {
			throw new IOException("Output directory " + dirFile.getAbsolutePath() + " could not be created");
		}

		File outputFile = new File(dirFile, fileName);
		if (newFile && outputFile.exists()) {
			final DateFormat fileDateFormat = new SimpleDateFormat("HHmmssSSS");
			final int extIndex = fileName.lastIndexOf('.');
			final String baseFilename;
			final String extension;
			if (extIndex < 0) {
				baseFilename = fileName;
				extension = "";
			}
			else {
				baseFilename = fileName.substring(0, extIndex);
				extension = fileName.substring(extIndex);
			}

			outputFile = new File(dirFile, baseFilename + "-" + fileDateFormat.format(now) + extension);
		}

		return outputFile;
	}

	private void processFileStream(InputStreamReader reader) {
		// [[
		Object[][] params = { { "reader", reader } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processFileStream", params);
		}
		// ]]
		PrintStream dumpStream = null;
		DateFormat dumpDateFormat = null;
		Map<String, String> rwsCache = null;

		try {
			final EdifactParser edifactParser = new EdifactParser();
			final SbbIftstaHandler handler = new SbbIftstaHandler();
			edifactParser.parse(reader, handler);

			final Collection<SbbIftstaBean> sbbBeanList = handler.getSbbBeanList();
			if (logDebugEnabled) {
				LOG.debug(sbbBeanList.size() + " beans found");
			}

			final WagonEventStatusBsEntity WAGON_EVENT_STATUS_NEW = referentialDataService.findBsEntity(
					WagonEventStatusBsEntity.class,
					Constant.WAGON_EVENT_STATUS_NEW);

			if (logDebugEnabled && getConfigBean().getEdifactImportDumpEnable()) {

				final File outputFile = getImportDumpFile("debug.csv", false);
				dumpStream = new PrintStream(
						new FileOutputStream(outputFile, true),
						false,
						getConfigBean().getEdifactImportCharset());

				if (!outputFile.exists()) {
					dumpStream.println(
							"DateTimeOfMessage," +
									"InterchangeConrolReference," +
									"MessageReferenceNo," +
									"ConsignmentNoteNo," +
									"DepartureRailwayStation," +
									"DestinationRailwayStation," +
									"EndRailwayStation," +
									"ShipmentSequenceNo," +
									"ArrivalDate," +
									"CarrierReferenceNo," +
									"CurrentRailwayStation," +
									"DamageRemarks," +
									"DepartureDate," +
									"EvuNo," +
									"NextHandlingRailwayStation," +
									"PlannedArrivalDateEndRailwayStation," +
									"ScheduledDepartureDate," +
									"SenderReference," +
									"StatusChangeDate," +
									"StatusCode," +
									"StatusText," +
									"TrainNo," +
									"TransportationOrderNo," +
									"LoadUnitNo," +
									"WagonNo," +
									"Loaded");
				}

				dumpDateFormat = new SimpleDateFormat("MM-dd HH:mm");
				dumpDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

				rwsCache = new HashMap<>();
			}

			for (SbbIftstaBean sbbBean : sbbBeanList) {

				for (SbbIftstaBean.Consignment consignment : sbbBean.getConsignmentList()) {

					for (SbbIftstaBean.Consignment.Status status : consignment.getStatusList()) {

						for (SbbIftstaBean.Consignment.Status.Wagon wagon : status.getWagonList()) {

							final WagonEventEntity wagonEvent = new WagonEventEntity();
							wagonEvent.setWagonEventStatus(WAGON_EVENT_STATUS_NEW);
							wagonEvent.setInterchangeControlReference(sbbBean.getInterchangeConrolReference());
							wagonEvent.setMessageReferenceNo(sbbBean.getMessageReferenceNo());

							String statusCode = status.getStatusCode();
							// Status code and text are not always consistent.
							// Sometimes code=3 and text=Eingangskontrolle
							// are received. Correct here.
							// I believe this is wrong in the SBB data
							if (StringUtils.equals(status.getStatusText(), STATUS_DESC_ARRIVED)) {
								statusCode = STATUS_CODE_ARRIVED;
							}
							wagonEvent.setEventType(statusCode);
							wagonEvent.setEventDat(sbbBean.getDateTimeOfMessage());
							wagonEvent.setWagonNo(wagon.getWagonNo());
							wagonEvent.setCurRailwayStation(status.getCurrentRailwayStation());
							wagonEvent.setDepRailwayStation(consignment.getDepartureRailwayStation());
							wagonEvent.setDepDat(status.getDepartureDate());
							wagonEvent.setDestRailwayStation(consignment.getDestinationRailwayStation());
							wagonEvent.setArrDat(status.getArrivalDate());
							wagonEvent.setNextRailwayStation(status.getNextHandlingRailwayStation());
							getDaoProvider().getWagonEventEntityDao().persist(wagonEvent);

							if (dumpStream != null) {
								dumpStream.println(
										StringUtils.join(new String[] {
												formatDate(dumpDateFormat, sbbBean.getDateTimeOfMessage()),
												sbbBean.getInterchangeConrolReference(),
												sbbBean.getMessageReferenceNo(),
												consignment.getConsignmentNoteNo(),
												formatRws(rwsCache, consignment.getDepartureRailwayStation()),
												formatRws(rwsCache, consignment.getDestinationRailwayStation()),
												formatRws(rwsCache, consignment.getEndRailwayStation()),
												consignment.getShipmentSequenceNo(),
												formatDate(dumpDateFormat, status.getArrivalDate()),
												status.getCarrierReferenceNo(),
												formatRws(rwsCache, status.getCurrentRailwayStation()),
												status.getDamageRemarks(),
												formatDate(dumpDateFormat, status.getDepartureDate()),
												status.getEvuNo(),
												formatRws(rwsCache, status.getNextHandlingRailwayStation()),
												formatDate(dumpDateFormat, status.getPlannedArrivalDateEndRailwayStation()),
												formatDate(dumpDateFormat, status.getScheduledDepartureDate()),
												status.getSenderReference(),
												formatDate(dumpDateFormat, status.getStatusChangeDate()),
												status.getStatusCode(),
												status.getStatusText(),
												status.getTrainNo(),
												status.getTransportationOrderNo(),
												wagon.getLoadUnitNo(),
												wagon.getWagonNo(),
												Boolean.toString(wagon.isLoaded())
										}, ',')
								);
							}
						}
					}
				}
			}
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new LogonException(e);
		}
		finally {
			if (dumpStream != null) {
				dumpStream.close();
			}
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void processFileFromFtp(FTPClient ftpClient, FTPFile ftpFile) throws IOException {
		// [[
		Object[][] params = { { "ftpClient", ftpClient },
				{ "ftpFile", ftpFile } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processFileFromFtp", params);
		}
		// ]]
		try {
			final boolean edifactImportDumpEnable = getConfigBean().getEdifactImportDumpEnable();
			final String fileName = ftpFile.getName();

			long startTime = System.currentTimeMillis();
			final ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
			final boolean success;
			File outputFile = null;
			try (OutputStream out = edifactImportDumpEnable ? new TeeOutputStream(byteArrayOS,
					new FileOutputStream(outputFile = getImportDumpFile(fileName, true))) : byteArrayOS) {
				success = ftpClient.retrieveFile(fileName, out);
			}

			if (!success) {
				throw new LogonException("Failed to download file " + fileName, Reason.RES_ERROR_UNSPECIFIED);
			}

			if (LOG.isInfoEnabled()) {
				LOG.info(String.format("Retrieved file %s from ftp (%d sec) to %s",
						fileName,
						(System.currentTimeMillis() - startTime) / 1000,
						outputFile != null ? "file: " + outputFile.getCanonicalPath() : "in-memory"));
			}

			processFileFromFtp(ftpClient, fileName, byteArrayOS.toByteArray());
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void processFileFromFtp(FTPClient ftpClient, String ftpFileName, byte[] content) throws IOException {
		final Charset edifactImportCharset = Charset.forName(getConfigBean().getEdifactImportCharset());
		try (ByteArrayInputStream in = new ByteArrayInputStream(content);
				InputStreamReader reader = new InputStreamReader(in, edifactImportCharset)
		) {
			long startTime = System.currentTimeMillis();
			processFileStream(reader);
			if (LOG.isInfoEnabled()) {
				LOG.info(String.format("Processing the input stream from in-memory for ftp file %s (%d sec) done.",
						ftpFileName, (System.currentTimeMillis() - startTime) / 1000));
			}

			if (getConfigBean().getEdifactImportRemoveFiles()) {
				ftpClient.deleteFile(ftpFileName); // somehow return value is not reliable

				final int replyCode = ftpClient.getReplyCode();
				if (FTPReply.isPositiveCompletion(replyCode)) {
					if (LOG.isInfoEnabled()) {
						LOG.info("Deleted ftp file:" + ftpFileName);
					}
				}
				else {
					throw new LogonException("Failed to delete file " + ftpFileName, Reason.RES_ERROR_UNSPECIFIED);
				}
			}
		}
	}

	@ScheduledTask(name = "edifact_import", second = "5", minute = "3/10", hour = "*",
			configurationMethodName = "getEdifactImportCronEnabled")
	public void doImport() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doImport");
		}
		// ]]

		FTPClient ftpClient = null;
		try {
			if (StringUtils.isEmpty(getConfigBean().getEdifactImportUrl())) {
				if (LOG.isInfoEnabled()) {
					LOG.info("Config " + ConfigBean.CFG_EDIFACTIMPORTURL + " not specified, import disabled");
				}
				return;
			}

			ftpClient = FtpClientFactory.createFtpClient(getConfigBean(), FTP_SERVICE_NAME_EDIFACT_IMPORT);

			String prefixForInvalidFiles = "_invalid_bck_";
			int total = 0;
			int commited = 0;
			final FTPFile[] ftpFiles = ftpClient.listFiles();
			for (FTPFile ftpFile : ftpFiles) {
				if ((ftpFile == null) || (!ftpFile.isFile()) || (!ftpFile.getName().toLowerCase().endsWith(".bck"))
						|| (ftpFile.getName().toLowerCase().startsWith(prefixForInvalidFiles))) {
					continue;
				}
				else {
					total++;
				}

				try {
					tx.begin();
					processFileFromFtp(ftpClient, ftpFile); // new transaction
					tx.commit();
					commited++;

					if (LOG.isInfoEnabled()) {
						LOG.info("Remote file " + ftpFile.getName() + " successfully imported.");
					}
				}
				catch (Exception e) {
					try {
						tx.rollback();
					}
					catch (SystemException se) {
						throw new LogonException(se);
					}
					LOG.error("Error processing " + ftpFile.getName(), e);
					if (e instanceof EdifactParseException) {
						try {
							tx.begin();
							ftpClient.rename(ftpFile.getName(), prefixForInvalidFiles + ftpFile.getName());
							tx.commit();
						}
						catch (Exception ex) {
							LOG.error("Transactional error while renaming file:", e);
							throw new LogonException(ex);
						}
					}
				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("From ftp: " + commited + "/" + total + " files succesfully imported.");
			}
		}
		catch (IOException e) {
			throw new LogonException(e);
		}
		finally {
			if ((ftpClient != null) && (ftpClient.isConnected())) {
				FtpClientFactory.disconnectFtp(ftpClient);
			}
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	@PreDestroy
	public void preDestroy() {
		FtpClientFactory.shutdown();
	}

}

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import au.com.bytecode.opencsv.CSVWriter;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.dispatchers.ReportingServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EmailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ReportingServiceLocalInterface;
import com.crh.logon.business.services.reporting.LoadReportMetaData;
import com.crh.logon.business.services.reporting.ReportMetaData;
import com.crh.logon.business.services.reporting.SqlDataExportListener;
import com.crh.logon.business.services.reporting.SqlDataExporter;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.RoleFuncEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class ReportingServiceBean extends ReportingServiceDispatcher implements ReportingServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReportingServiceBean.class);

	private static final String REPORTS_DEFINITION_FILE = "reports.xml";

	private static final String REPORT_TEXT_DEFINITION_FILE = "report_texts.xml";

	private static final String MAILADDRESS_SEPARATOR = ",";

	private static final String ATTR_REPORT_CD = "report-cd";

	@EJB
	private RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	private PDFGeneratorServiceLocalInterface pdfGeneratorService;

	@EJB
	private EmailServiceLocalInterface emailService;

	private Node getReportXmlNode(String reportCd) {

		try {
			DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
			docBF.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder docB = docBF.newDocumentBuilder();
			Document doc =
					docB.parse(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(REPORTS_DEFINITION_FILE)));
			NodeList reportNodes = doc.getElementsByTagName("report");
			for (int i = 0; i < reportNodes.getLength(); i++) {
				Node reportNode = reportNodes.item(i);
				if (reportNode.getAttributes().getNamedItem(ATTR_REPORT_CD).getNodeValue().equals(reportCd)) {

					return reportNode;
				}
			}
		}
		catch (Exception e) {
			throw new LogonException(e);
		}

		return null;
	}

	private Node getReportTextXmlNode(String reportCd) {

		try {
			DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
			docBF.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder docB = docBF.newDocumentBuilder();
			Document doc =
					docB.parse(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(REPORT_TEXT_DEFINITION_FILE)));
			NodeList reportNodes = doc.getElementsByTagName("report-text");
			for (int i = 0; i < reportNodes.getLength(); i++) {
				Node reportNode = reportNodes.item(i);
				if (reportNode.getAttributes().getNamedItem(ATTR_REPORT_CD).getNodeValue().equals(reportCd)) {

					return reportNode;
				}
			}
		}
		catch (Exception e) {
			throw new LogonException(e);
		}

		return null;
	}

	public ReportMetaData createReportMetaData(String reportCd, Locale locale) {
		Node reportNode = getReportXmlNode(reportCd);
		if (reportNode == null) {

			throw new LogonException("Unknown reportCd " + reportCd, Reason.RES_ERROR_IMPL);
		}
		return createReportMetaData(reportCd, locale, reportNode);

	}

	public ReportMetaData createReportTextMetaData(String reportCd, Locale locale) {
		Node reportNode = getReportTextXmlNode(reportCd);
		if (reportNode == null) {

			throw new LogonException("Unknown text reportCd " + reportCd, Reason.RES_ERROR_IMPL);
		}
		return createReportMetaData(reportCd, locale, reportNode);

	}

	private ReportMetaData createReportMetaData(String reportCd, Locale locale, Node reportNode) {
		ReportMetaData rmd;

		String className = reportNode.getAttributes().getNamedItem("class").getNodeValue();
		Class<? extends ReportMetaData> clazz;
		try {
			clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader()).asSubclass(ReportMetaData.class);
		}
		catch (ClassNotFoundException e) {
			try {
				clazz = Class.forName(className).asSubclass(ReportMetaData.class);
			}
			catch (ClassNotFoundException f) {

				throw new LogonException("Report implementation " + className + " not found", f);
			}
		}
		try {
			rmd = clazz.getDeclaredConstructor().newInstance();
		}
		catch (Exception e) {

			throw new LogonException("Report implementation " + className + " could not be instantiated", e);
		}
		rmd.setReportCd(reportCd);
		rmd.setReportName(Message.lookup(locale, reportNode.getAttributes().getNamedItem("name-key").getNodeValue()));

		NodeList reportChildNodes = reportNode.getChildNodes();
		for (int j = 0; j < reportChildNodes.getLength(); j++) {
			Node reportChildNode = reportChildNodes.item(j);
			if (reportChildNode.getNodeName().equals("query")) {
				NodeList queryChildNodes = reportChildNode.getChildNodes();
				for (int k = 0; k < queryChildNodes.getLength(); k++) {
					Node queryChildNode = queryChildNodes.item(k);
					if (queryChildNode.getNodeType() == Node.CDATA_SECTION_NODE) {
						rmd.setRawQueryString(queryChildNode.getNodeValue());
						break;
					}
				}
			}
		}

		return rmd;
	}


	public List getAllReportMetaData(Locale locale) {
		List<ReportMetaData> reports = new ArrayList<>();

		try(InputStream in = getClass().getClassLoader().getResourceAsStream(REPORTS_DEFINITION_FILE)) {
			DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
			docBF.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder docB = docBF.newDocumentBuilder();
			Document doc = docB.parse(Objects.requireNonNull(in));
			NodeList reportNodes = doc.getElementsByTagName("report");
			for (int i = 0; i < reportNodes.getLength(); i++) {
				Node reportNode = reportNodes.item(i);
				Node reportingTab = reportNode.getAttributes().getNamedItem("reporting-tab");
				if (reportingTab != null && reportingTab.getNodeValue().equals("true")) {
					reports.add(
							createReportMetaData(reportNode.getAttributes().getNamedItem(ATTR_REPORT_CD).getNodeValue(), locale));
				}
			}
		}
		catch (Exception e) {

			throw new LogonException(e);
		}

		return reports;
	}


	private boolean isAuthorizedFor(UserEntity user, long funcId) {
		ReadWriteDao<UserRoleEntity> dao = getDaoProvider().getUserRoleEntityDao();
		Filter<UserRoleEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(UserRoleEntity.ATTR_USER, user));
		JPAQuery<UserRoleEntity> jpaQ = dao.createJPAQuery();
		jpaQ.addSelect(SelectExpression.simpleSelect(UserRoleEntity.ATTR_ROLE));
		jpaQ.setDistinct(true);
		jpaQ.setFilter(filter);
		List roleList = dao.find(jpaQ);

		ReadWriteDao<RoleFuncEntity> roleFuncEntityDao = getDaoProvider().getRoleFuncEntityDao();
		Filter<RoleFuncEntity> roleFuncEntityFilter = roleFuncEntityDao.createFilter();
		roleFuncEntityFilter.addExpression(Restrictions.in(RoleFuncEntity.ATTR_ROLE, roleList));
		roleFuncEntityFilter.addExpression(Restrictions.eq(RoleFuncEntity.ATTR_FUNC_ID, funcId));

		return (roleFuncEntityDao.count(roleFuncEntityFilter) > 0);
	}

	// This listener keeps track to see if data was actually exported.
	// If data was exported, 'dataExportBitSet[0]' will be true,
	// otherwise false.
	private static class SqlDataExportListenerImpl implements SqlDataExportListener {

		private final BitSet dataExportedBitSet = new BitSet(1);

		public String[] exportHeader(String[] columnNames, Locale locale) {
			dataExportedBitSet.clear();
			return columnNames;
		}

		public void exportRow(String[] columnNames, Object[] rowData, Map data, Locale locale, TimeZone timezone) {
			dataExportedBitSet.set(0, true);
		}

		public boolean isExported() {
			return dataExportedBitSet.get(0);
		}
	}

	@ScheduledTask(name = "send_daily_reports", second = "0", minute = "59", hour = "23",
			configurationMethodName = "getSendDailyReportsCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendDailyReports() {

		int mailSentCount = 0;
		final Date currentDate = new Date();
		final ServiceLocator serviceLocator = new ServiceLocator(getConfigBean().getLogonProperties().getProviderurl(), false);
		final MimeTypeBsEntity mimeType = referentialDataService.findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_CSV);

		final ReadOnlyDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
		final Filter<UserEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_PERSON + "." + PersonEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.isNotNull(UserEntity.ATTR_PERSON + "." + PersonEntity.ATTR_EMAIL));
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_SEND_DAILY_REPORTS_FLAG, Boolean.TRUE));
		filter.addExpression(Order.desc(UserEntity.ATTR_APPLICATION_LANG_CD));
		final List<UserEntity> userList = dao.find(filter);

		SqlDataExporter sde = new SqlDataExporter(serviceLocator);
		SqlDataExportListenerImpl listener = new SqlDataExportListenerImpl();
		sde.addExportListener(listener);

		Locale locale = null;
		ReportMetaData rmd = null;
		String subject = null;
		String text;
		String reportCdLoadShippingStats = Constant.REPORT_CD_LOAD_SHIPPING_STATS;
		for (UserEntity user : userList) {
			if (!isAuthorizedFor(user, Constant.REPORT_LOAD_SHIPPING_STATS)) {

				LOG.info("Not authorized: cannot send daily report 'load shipping stats' to user " + user.getUserId());
				continue;
			}
			if ((locale == null) || (!locale.getLanguage().equals(user.getApplicationLang().getLangCd()))) {
				// Create the next ReportMetaData based on the next locale in the user list (sorted by language!)
				locale = new Locale(user.getApplicationLang().getLangCd());

				rmd = createReportMetaData(reportCdLoadShippingStats, locale);
				if (rmd instanceof LoadReportMetaData) {
					LoadReportMetaData lrmd = (LoadReportMetaData) rmd;
					lrmd.setLangCd(user.getApplicationLang().getLangCd());
					lrmd.setLoadingFromDat(currentDate);
					lrmd.setLoadingToDat(currentDate);
				}

				subject = pdfGeneratorService
						.formatText("daily_reports.email.subject", locale, new Object[] { currentDate, rmd.getReportName() });
			}

			TimeZone userTZ = serviceLocator.getTimeZoneService().loadForUser(user);

			StringWriter swText = getCustomReportText(currentDate, locale, user, reportCdLoadShippingStats, serviceLocator);
			String additionaltext = swText.toString();
			text = pdfGeneratorService.formatText("daily_reports.email.text", locale,
					new Object[] { currentDate, rmd.getReportName(), additionaltext });

			rmd.init(serviceLocator, user.getUserId());
			// The shipping stats report is a small one, so it's safe to put
			// it in memory. If larger reports need to be mailed, make a
			// streaming solution.
			StringWriter sw = new StringWriter();
			sde.exportSqlData(sw, rmd.getQueryString(), rmd.getQParams(), locale, userTZ);
			if (listener.isExported()) {
				try {
					final String[] to = new String[] { user.getPerson().getEmail() };

					emailService.send(
							to, null, null,
							subject, text,
							null,
							emailService.createMimeBodyPartAttachment(
									sw.toString().getBytes(),
									mimeType,
									rmd.getReportName().replaceAll("\\s", "_") + "_" + new SimpleDateFormat("yyyyMMdd")
											.format(currentDate)));

					mailSentCount++;

				}
				catch (Exception e) {
					LOG.error(
							"Sending reporting mail to '" + user.getPerson().getName() + " " + user.getPerson().getFirstname()
									+ "' failed (email: '" + user.getPerson().getEmail() + "', subject: '" + subject + "')");
					ExceptionUtil.errException(LOG, e, null);
				}
			}
		}

		LOG.info(mailSentCount + " daily reports sent");
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendGoodsmovementReport() {
		// TODO (pascal Jun 19, 2012): This is not working nor finished yet. => LOGON-4809
	}

	@ScheduledTask(name = "send_staging_area_report_bpn", second = "0", minute = "3", hour = "0",
			configurationMethodName = "getSendStagingAreaReportBpnCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendStagingAreaReportBPN() {

		String reportCdStagingAreaReportBPN = Constant.REPORT_CD_STAGING_AREA_ERRORS_BPN;

		final Collection<String> mailTos = new HashSet<>();
		final String addresses = getConfigBean().getSapimportReportingBpnTo();
		if (addresses != null) {
			Collections.addAll(mailTos, addresses.split(MAILADDRESS_SEPARATOR));
		}

		sendStagingAreaReport(reportCdStagingAreaReportBPN, mailTos, "business partners");

	}


	@ScheduledTask(name = "send_staging_area_report_vn", second = "0", minute = "1", hour = "0",
			configurationMethodName = "getSendStagingAreaReportVnCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendStagingAreaReportVN() {

		String reportCdStagingAreaReportVN = Constant.REPORT_CD_STAGING_AREA_ERRORS_VN;

		final Collection<String> mailTos = new HashSet<>();
		final String addresses = getConfigBean().getSapimportReportingVnTo();
		if (addresses != null) {
			Collections.addAll(mailTos, addresses.split(MAILADDRESS_SEPARATOR));
		}

		sendStagingAreaReport(reportCdStagingAreaReportVN, mailTos, "vendors");

	}

	@ScheduledTask(name = "send_staging_area_report_mn", second = "0", minute = "2", hour = "0",
			configurationMethodName = "getSendStagingAreaReportMnCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendStagingAreaReportMN() {

		String reportCdStagingAreaReportMN = Constant.REPORT_CD_STAGING_AREA_ERRORS_MN;

		final Collection<String> mailTos = new HashSet<>();
		final String addresses = getConfigBean().getSapimportReportingMnTo();
		if (addresses != null) {
			Collections.addAll(mailTos, addresses.split(MAILADDRESS_SEPARATOR));
		}

		sendStagingAreaReport(reportCdStagingAreaReportMN, mailTos, "materials");

	}

	private void sendStagingAreaReport(String reportCd, Collection<String> mailTos, String businessObjectName) {

		int mailSentCount = 0;
		Date currentDate = new Date();
		ServiceLocator serviceLocator = new ServiceLocator(getConfigBean().getLogonProperties().getProviderurl(), false);
		MimeTypeBsEntity mimeType = referentialDataService.findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_CSV);

		SqlDataExporter sde = new SqlDataExporter(serviceLocator);
		SqlDataExportListenerImpl listener = new SqlDataExportListenerImpl();
		sde.addExportListener(listener);

		Locale locale = Locale.ENGLISH;
		TimeZone defaultTZ = new TimeZone(getConfigBean().getAppDefaultTimezone(), locale);
		ReportMetaData rmd;
		String subject;
		String text;

		rmd = createReportMetaData(reportCd, locale);

		subject = pdfGeneratorService
				.formatText("staging_area_report.email.subject", locale, new Object[] { businessObjectName, currentDate });
		text = pdfGeneratorService.formatText("staging_area_report.email.text", locale, new Object[] { rmd.getReportName() });

		rmd.init(serviceLocator, 0L);
		// TODO stich, May 2012: is it safe to put this into memory or is a streaming solution required?
		StringWriter sw = new StringWriter();
		sde.exportSqlData(sw, rmd.getQueryString(), null, locale, defaultTZ);
		if (listener.isExported()) {
			for (String recipient : mailTos) {
				try {

					final String[] to = new String[] { recipient };

					emailService.send(
							to, null, null,
							subject, text,
							null,
							emailService.createMimeBodyPartAttachment(
									sw.toString().getBytes(),
									mimeType,
									rmd.getReportName().replaceAll("\\s", "_") + "_" + new SimpleDateFormat("yyyyMMdd")
											.format(currentDate)));
					mailSentCount++;

				}
				catch (Exception e) {
					LOG.error("Sending reporting mail to '" + recipient + "' failed (email: '" + "recipient" + "', subject"
							+ ": '" + subject + "')");
					ExceptionUtil.errException(LOG, e, null);
				}
			}
		}

		LOG.info(mailSentCount + " staging area reports sent");

	}

	private StringWriter getCustomReportText(Date currentDate, Locale locale, UserEntity user,
			String reportCd, ServiceLocator serviceLocator) {

		ReportMetaData rmdText = createReportTextMetaData(reportCd, locale);
		SqlDataExporter sde = new SqlDataExporter(serviceLocator);

		if (rmdText instanceof LoadReportMetaData) {

			LoadReportMetaData lrmd = (LoadReportMetaData) rmdText;
			lrmd.setLoadingFromDat(currentDate);
			lrmd.setLoadingToDat(currentDate);
			lrmd.setLangCd(user.getApplicationLang().getLangCd());

		}

		TimeZone userTZ = serviceLocator.getTimeZoneService().loadForUser(user);

		rmdText.init(serviceLocator, user.getUserId());

		StringWriter sw = new StringWriter();

		sde.exportSqlData(sw,
				rmdText.getQueryString(),
				rmdText.getQParams(),
				locale,
				CSVWriter.NO_QUOTE_CHARACTER, false,
				userTZ);

		return sw;

	}

}
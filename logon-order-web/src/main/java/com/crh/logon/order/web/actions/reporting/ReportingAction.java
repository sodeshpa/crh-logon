/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.reporting;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import au.com.bytecode.opencsv.CSVReader;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.business.services.reporting.FreightCarrierRevenuePdfMetaData;
import com.crh.logon.business.services.reporting.ReportMetaData;
import com.crh.logon.business.services.reporting.SqlDataExportListener;
import com.crh.logon.business.services.reporting.SqlDataExporter;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.reporting.ReportingForm;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeZoneFactory;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_reporting_ReportingForm" path="/reporting/Reporting" scope="session" validate="false"
 *
 * @struts.action-forward name="success" path="ReportingTile.def"
 * @struts.action-forward name="preview" path="ReportingPreviewTile.def"
 * @struts.action-forward name="failed" path="ReportingTile.def"
 */
public class ReportingAction extends OrderActionSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReportingAction.class);
	
	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
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
		try {
			ReportingAuthorization.authorize(getServiceLocator());
			ReportingForm reportForm = (ReportingForm)form;
			if (request.getParameter("export") != null) {
				ReportMetaData rmd = null;
				try {
					rmd = getReportMetaData(request);
				}
				catch (ParseException e) {
					throw new LogonException("Invalid from date format", Reason.RES_ERROR_INVALID_DATE_FORMAT);
				}
				
				if (!ReportingAuthorization.isAllowed(rmd.getClass())) {
					throw new LogonException("Not authorized for report", Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
				}
				if (rmd instanceof FreightCarrierRevenuePdfMetaData) {
					// Freight Carrier Revenue PDF: special report, non-streaming solution
					FreightCarrierRevenuePdfMetaData frmd = (FreightCarrierRevenuePdfMetaData) rmd;
					TruckCarrierRevenuePdfDownloadAction downloadAction =
						new TruckCarrierRevenuePdfDownloadAction(frmd, reportForm.getCarriers());
					
					return downloadAction.execute(mapping, reportForm, request, response);
				}
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
				Date now = new Date();
				setDLHeaders(request, response, null,
						"report_" + simpleDateFormat.format(now) + ".csv");
				SqlDataExporter exporter = getSqlDataExporter(rmd.getReportCd());
				exporter.exportSqlData(response.getWriter(), rmd.getQueryString(),
						rmd.getQParams(), request.getLocale(), getUserTimezone());
				return result = null;
			}
			if (request.getParameter("preview") != null) {
				StringWriter w = null;
				CSVReader cvsReader = null;
				try {
					ReportMetaData rmd = null;
					try {
						rmd = getReportMetaData(request);
					}
					catch (ParseException e) {
						throw new LogonException("Invalid to date format", Reason.RES_ERROR_INVALID_DATE_FORMAT);
					}
					
					if (!ReportingAuthorization.isAllowed(rmd.getClass())) {
						throw new LogonException("Not authorized for report", Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
					}
					SqlDataExporter exporter = getSqlDataExporter(rmd.getReportCd());
					exporter.setMaxRows(100);
					w = new StringWriter();
					exporter.exportSqlData
						(w, rmd.getQueryString(), rmd.getQParams(), request.getLocale(), getUserTimezone());
					
					cvsReader = new CSVReader(new StringReader(w.toString()), ';');
					reportForm.setPreviewData(cvsReader.readAll());
				}
				finally {
					if (w != null) {
						w.close();
					}
					if (cvsReader != null) {
						cvsReader.close();
					}
				}
				return result = mapping.findForward("preview");
			}
			String forwardName = "success";
			
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private SqlDataExporter getSqlDataExporter(String reportCd) {

		SqlDataExporter exporter = new SqlDataExporter(getServiceLocator());

		SqlDataExportListener listener;
		if (reportCd.equals(Constant.REPORT_CD_TRANSPLUS)) {
			TimeZone userTimezone = getUserTimezone();
			String userTimezoneCd = userTimezone.getTimezoneCd();
			String appDefaultTimezoneCd = DateTimeZoneFactory.getDefaultTimeZoneId();
			listener = new TransplusReportingExportListener(appDefaultTimezoneCd, userTimezoneCd);
		}
		else {
			listener = new ReportingExportListener();
		}

		exporter.addExportListener(listener);

		return exporter;

	}
	
	private ReportMetaData getReportMetaData(HttpServletRequest request) throws ParseException {

		TimeZone userTimezone = getUserTimezone();
		String userTimezoneCd = userTimezone.getTimezoneCd();

		ReportMetaData rmd = getServiceLocator().getReportingService().createReportMetaData(request.getParameter(ReportMetaData.PARAM_REPORT_CD), request.getLocale());
		rmd.setUserTimezoneCd(userTimezoneCd);
		rmd.setupFilters(request);
		rmd.init(getServiceLocator(), getUser().getUserId());

		return rmd;

	}


	/**
	 * "Embedded" download action that is similar to the one for Dispo Export popup.
	 */
	private static class TruckCarrierRevenuePdfDownloadAction
			extends AbstractTruckRevenuePlanningDownloadAction {

		private final FreightCarrierRevenuePdfMetaData freightCarrierRevenuePdfMetaData;
		private final List<SelectableGeneralDisplayNameDto> carriers;

		public TruckCarrierRevenuePdfDownloadAction(FreightCarrierRevenuePdfMetaData reportMetaData,
				List<SelectableGeneralDisplayNameDto> carriers) {
			this.freightCarrierRevenuePdfMetaData = reportMetaData;
			this.carriers = carriers;
		}

		@Override
		protected DownloadData getDownloadData(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response) {
			// [[
			DownloadData result = null;
			Object[][] params = {{"mapping", mapping},
								{"form", form},
								{"request", request},
								{"response", response}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "getDownloadData", params);
			}
			// ]]
			try {
				
				String langCd = freightCarrierRevenuePdfMetaData.getLangCd();
				Date dateFrom = freightCarrierRevenuePdfMetaData.getDeliveryFromDat();
				Date dateTo = freightCarrierRevenuePdfMetaData.getDeliveryToDat();
				List<Long> selectedCarrierIDList = freightCarrierRevenuePdfMetaData.getSelectedCarrierIDs();
				
				// Clear time part
				TimeZone userTimeZone = getUserTimezone();

				dateFrom = userTimeZone.morningOf(dateFrom);
				dateTo = userTimeZone.midnightOf(dateTo);
				
				byte[] pdfBytes = getServiceLocator().getTruckRevenuePlanningService().generatePdf(
						selectedCarrierIDList, Collections.<Long>emptyList(), dateFrom, dateTo, new Locale(langCd),
						true, userTimeZone);
				
				MimeTypeBsEntity mimeBs = getRefDataGenericsService().findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_PDF);
				
				String fileName = "";
				
				if (selectedCarrierIDList.size() == 1) {
					Long carrierId = selectedCarrierIDList.get(0);
					String carrierName = getCarrierNameForId(carriers, carrierId);
					fileName = carrierName;
				}

				if (dateFrom == null) {
					dateFrom = new Date();
				}
				
				boolean showRange = !dateFrom.equals(dateTo);
				DownloadData data = new DownloadData(fileName + (!StringUtils.empty(fileName) ? "_" : "")
						+ getDateFileInFileNameFormat(dateFrom)
						+ (showRange ? (" - " + getDateFileInFileNameFormat(dateTo)) : "")
						+ "." + mimeBs.getFileExtension(), mimeBs.getMimeType(), pdfBytes);
				
				return result = data;
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
		 * Returns the name of the carrier belonging the given carrierId by iterating over the 
		 * available caerriers of the form.
		 */
		private String getCarrierNameForId(List<SelectableGeneralDisplayNameDto> carriers, Long carrierId) {
			String carrierlName = "";
			for (SelectableGeneralDisplayNameDto carrier : carriers) {
				if (carrierId.equals(carrier.getId())){
					carrierlName = carrier.getName() ;
					break;
				}
			}
			return carrierlName;
		}

	}

}

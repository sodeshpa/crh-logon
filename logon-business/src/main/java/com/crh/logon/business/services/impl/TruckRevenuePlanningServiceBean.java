/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.dispatchers.TruckRevenuePlanningServiceDispatcher;
import com.crh.logon.business.services.reporting.TruckRevenuePlanningPdfHelper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.text.PdfTextTransliterator;
import com.crh.logon.utils.text.Transliterator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import ch.nevis.jcan.print.base.PageNumberFooter;
import ch.nevis.jcan.print.base.PrinterException;
import ch.nevis.jcan.print.fonts.Font;
import ch.nevis.jcan.print.fonts.FontFactory;
import ch.nevis.jcan.print.layout.Alignment;
import ch.nevis.jcan.print.layout.Container;
import ch.nevis.jcan.print.pdfgen.PdfException;
import ch.nevis.jcan.print.pdfgen.PdfGen;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckRevenuePlanningServiceLocalInterface;
import com.crh.logon.business.services.reporting.TruckRevenuePlanningCommons;
import com.crh.logon.business.services.reporting.TruckRevenuePlanningXlsHelper;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.views.TruckRevenuePlanMatEntity;
import com.crh.logon.persistence.entities.views.TruckRevenuePlanningEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Service methods for truck revenue planning report/export.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class TruckRevenuePlanningServiceBean extends TruckRevenuePlanningServiceDispatcher implements TruckRevenuePlanningServiceLocalInterface{

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(TruckRevenuePlanningServiceBean.class);

	@EJB private GenericBlobServiceLocalInterface genericBlobService;
	
	/**
	 * Generate a truck revenue report in PDF format for trucks belonging to the given list of carriers,
	 * having orders in the given date interval.
	 * 
	 * <p>NOTE: currently it is expected that we do not generate huge documents so that streaming is not
	 * needed.</p>
	 * 
	 * @param carrierIds a list of carrier IDs
	 * @param dateFrom first day (the time part of this date does not matter) [optional]
	 * @param dateTo last day (the time part of this date does not matter) [optional]
	 * @param locale locale
	 * @param includeFreightRates whether to include freight rates in the report
	 * @return the generated PDF file content
	 */
	public byte[] generatePdf(List<Long> carrierIds, List<Long> poolIds, Date dateFrom, Date dateTo, Locale locale,
			boolean includeFreightRates, TimeZone userTimeZone) {
		// [[
		byte[] result = null;
		Object[][] params = {{"carrierIds", carrierIds},
							{"dateFrom", dateFrom},
							{"dateTo", dateTo},
							{"locale", locale},
							{"includeFreightRates", includeFreightRates + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "generatePdf", params);
		}
		// ]]
		try {
			// Fetch plannings
			List<TruckRevenuePlanningEntity> plannings = fetchTruckRevenuePlanning(carrierIds,
					poolIds, dateFrom, dateTo);
			
			List<Long> orderIds = new ArrayList<Long>(plannings.size());
			
			for (TruckRevenuePlanningEntity planning: plannings) {
				orderIds.add(planning.getOrderId());
			}
			
			// Fetch materials
			Map<Long, List<TruckRevenuePlanMatEntity>> planningMatsByOrder =
				fetchTruckRevenuePlanningMaterials(orderIds, locale);
			
			// Build a tree organizing the orders by (day, carrier name, truck name).
			Map<Long, String> carrierNamesById = new HashMap<Long, String>();
			Map<Long, String> truckNamesById = new HashMap<Long, String>();
			
			Map<Date, Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>>> carriersByDate =
				buildTree(plannings, carrierNamesById, truckNamesById);
			
			// Build PDF
			Date reportDate = new Date();
			String reportDateFormatted = TruckRevenuePlanningCommons.formatReportDate(reportDate, userTimeZone);
			String reportTimeFormatted = TruckRevenuePlanningCommons.formatReportTime(reportDate, userTimeZone);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] pdfBytes;
			
			PdfGen pdfGen = new PdfGen(baos);
			pdfGen.setMargins(TruckRevenuePlanningPdfHelper.MARGIN_LEFT,
					TruckRevenuePlanningPdfHelper.MARGIN_RIGHT,
					TruckRevenuePlanningPdfHelper.MARGIN_TOP,
					TruckRevenuePlanningPdfHelper.MARGIN_BOTTOM);
			pdfGen.setLandscape();
			
			// Load fonts
			ClassLoader classLoader = getClass().getClassLoader();
			
			InputStream arialTtf = classLoader.getResourceAsStream(
					TruckRevenuePlanningPdfHelper.ARIAL_TTF_FILE);
			InputStream arialBoldTtf = getClass().getClassLoader().getResourceAsStream(
					TruckRevenuePlanningPdfHelper.ARIAL_BOLD_TTF_FILE);
			
			FontFactory ff = FontFactory.getInstance();
			Font textFont;
			Font headerFont;
			Font boldTextFont;
			
			try {
				textFont = ff.createFont("Arial", 10, arialTtf, Font.WINANSI, true);
				headerFont = ff.createFont("Arial", 20, arialTtf, Font.WINANSI, true);
			}
			finally {
				try {
					arialTtf.close();
				}
				catch (IOException e) {
					LOG.error("Error closing stream", e);
				}
			}
			
			try {
				boldTextFont = ff.createFont("Arial-Bold", 10, arialBoldTtf, Font.WINANSI, true);
			}
			finally {
				try {
					arialBoldTtf.close();
				}
				catch (IOException e) {
					LOG.error("Error closing stream", e);
				}
			}
			
			// Load Crh logo
			BlobDTO crhLogoDTO = TruckRevenuePlanningCommons.fetchCrhLogo(genericBlobService);
			
			// FIXME (romek 9 Jun, 2011): PageNumberFooter does not support separator line correctly.
			PageNumberFooter footer = new PageNumberFooter(Alignment.RIGHT, "Page", "of");
			footer.setFont(textFont);
			footer.setMargins(TruckRevenuePlanningPdfHelper.MARGIN_LEFT,
					TruckRevenuePlanningPdfHelper.MARGIN_RIGHT,
					TruckRevenuePlanningPdfHelper.MARGIN_BOTTOM);
			
			if (carriersByDate.keySet().isEmpty()) {
				/*
				 * This should not happen. If there is no data to display, we display an error message
				 * instead, and that is already done using a LogonException.
				 */
				pdfGen.setFooter(footer);
				pdfGen.newPage();
			}
			else {
				Transliterator transliterator = new PdfTextTransliterator(locale);

				TruckRevenuePlanningPdfHelper helper =
						new TruckRevenuePlanningPdfHelper(locale, transliterator);

				for (Date plannedStartDay: carriersByDate.keySet()) {
					pdfGen.setFooter(footer);
					pdfGen.newPage();
					pdfGen.setFont(textFont);
					pdfGen.setAlternateFont(boldTextFont);
			
					Container container = new Container();

					String plannedStartDayFormatted = userTimeZone.fmtForExportReportTitle(plannedStartDay);

					// Header (not really, just the rectangle)
					helper.printHeader(pdfGen, container, headerFont,
									   reportDateFormatted, reportTimeFormatted, plannedStartDayFormatted,
									   crhLogoDTO);
			
					// Body
					Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>> trucksByCarrier =
						carriersByDate.get(plannedStartDay);

					helper.printBody(pdfGen, container, plannedStartDayFormatted,
									 trucksByCarrier, planningMatsByOrder, carrierNamesById, truckNamesById,
									 includeFreightRates);
					
					container.render(pdfGen);
				}
			}
			
			pdfGen.end();
			pdfBytes = baos.toByteArray();
			
			return result = pdfBytes;
		}
		catch (IOException e) {
			throw new LogonException(e, Reason.RES_ERROR_IMPL);
		}
		catch (PrinterException e) {
			throw new LogonException(e, Reason.RES_ERROR_IMPL);
		}
		catch (PdfException e) {
			throw new LogonException(e, Reason.RES_ERROR_IMPL);
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
	 * Generate a truck revenue report in Excel format for trucks belonging to the given carrier, having
	 * orders on the given day.
	 * 
	 * <p>NOTE: currently it is expected that we do not generate huge documents so that streaming is not
	 * needed.</p>
	 * 
	 * @param carrierId a carrier ID
	 * @param poolIds pools the seeked pre-schedule orders are created with 
	 * @param date current day of the scheduling screen (the time part of this date does not matter)
	 * @param locale locale (currently ignored, treated as Locale.FRENCH)
	 * @return the generated PDF file content
	 */
	public byte[] generateXls(Long carrierId, List<Long> poolIds, Date date, Locale locale, TimeZone userTimeZone) {
		// [[
		byte[] result = null;
		Object[][] params = {{"carrierId", carrierId},
							{"poolIds", poolIds},
							{"date", date},
							{"locale", locale},
							{"userTimeZone", userTimeZone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "generateXls", params);
		}
		// ]]
		try {
			/*
			 * No multi-lingual support for now, because the importing system could rely on specific
			 * column names, and they want to avoid that the excel importing applications have to adapt.
			 */
			locale = Locale.FRENCH;
			
			List<Long> carrierIds = new ArrayList<Long>();
			carrierIds.add(carrierId);
			
			// Fetch plannings
			List<TruckRevenuePlanningEntity> plannings = fetchTruckRevenuePlanning(carrierIds, poolIds, date, date);
			
			List<Long> orderIds = new ArrayList<Long>(plannings.size());
			
			for (TruckRevenuePlanningEntity planning: plannings) {
				orderIds.add(planning.getOrderId());
			}
			
			// Fetch materials
			Map<Long, List<TruckRevenuePlanMatEntity>> planningMatsByOrder =
				fetchTruckRevenuePlanningMaterials(orderIds, locale);
			
			// Build a tree organizing the orders by (day, carrier name, truck name),
			// like for the PDF report, although here we will only have 1 day and 1 carrier.)
			Map<Long, String> carrierNamesById = new HashMap<Long, String>();
			Map<Long, String> truckNamesById = new HashMap<Long, String>();
			
			Map<Date, Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>>> carriersByDate =
				buildTree(plannings, carrierNamesById, truckNamesById);
			
			// Create a new Excel document
			Date reportDate = new Date();
			
			Workbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Sheet1");
			
			/*
			 * Planning data should not be empty. If there is no data to display, we display an error
			 * message instead, and that is already done using a LogonException.
			 */
			if (!carriersByDate.keySet().isEmpty()) {
				Transliterator transliterator = new PdfTextTransliterator(locale);

				TruckRevenuePlanningXlsHelper helper =
						new TruckRevenuePlanningXlsHelper(locale, transliterator);

				Date plannedStartDay = carriersByDate.keySet().iterator().next();
				String plannedStartDayFormatted = userTimeZone.fmtForExportReportTitle(plannedStartDay);

				// Load Crh logo
				BlobDTO crhLogoDTO = TruckRevenuePlanningCommons.fetchCrhLogo(genericBlobService);
			
				// Load the example document containing the original formatting
				ClassLoader classLoader = getClass().getClassLoader();
				InputStream exampleWbIs = classLoader.getResourceAsStream(
						TruckRevenuePlanningXlsHelper.EXAMPLE_FILE);
			
				try {
					Workbook exampleWb = new HSSFWorkbook(exampleWbIs);
					Sheet exampleWbSheet = exampleWb.getSheetAt(0);
					
					// Copy formatting
					boolean displayGridlines = exampleWbSheet.isDisplayGridlines();
					sheet.setDisplayGridlines(displayGridlines);
					
					boolean displayZeros = exampleWbSheet.isDisplayZeros();
					sheet.setDisplayZeros(displayZeros);

					helper.cloneColumnFormatting(sheet, exampleWbSheet, 26);
					
					for (int i = 0; i < 13; i++) {
						helper.cloneRowFormatting(wb, sheet, exampleWbSheet, i, i);
					}
					
					// Header
					String carrierName = carrierNamesById.get(carrierId);

					helper.printHeader(wb, sheet, exampleWbSheet,
									   plannedStartDayFormatted,
									   reportDate,
									   carrierName,
									   crhLogoDTO, userTimeZone);
				
					// Body
					Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>> trucksByCarrier =
						carriersByDate.get(plannedStartDay);
				
					Map<Long, List<TruckRevenuePlanningEntity>> ordersByTruck = trucksByCarrier.get(
							carrierId);

					helper.printBody(wb, sheet, exampleWbSheet,
									 carrierName, ordersByTruck, planningMatsByOrder, truckNamesById);
				}
				finally {
					try {
						exampleWbIs.close();
					}
					catch (IOException e) {
						LOG.error("Error closing stream", e);
					}
				}
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] xlsBytes;
			wb.write(baos);
			xlsBytes = baos.toByteArray();
			
			return result = xlsBytes;
		}
		catch (IOException e) {
			throw new LogonException(e, Reason.RES_ERROR_IMPL);
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
	 * Get the truck revenue planning data (not including materials) for the given date interval. The from
	 * and to dates are treated as days so the caller does not have to worry about the minutes, seconds,
	 * and milliseconds set in either of the dates. Materials are fetched with {@link
	 * #fetchTruckRevenuePlanningMaterials(List, Locale)}.
	 * 
	 * @param carrierIds list of carrier IDs
	 * @param dayFrom day from [optional]
	 * @param dayTo day to [optional]
	 * @return truck revenue planning data
	 */
	private List<TruckRevenuePlanningEntity> fetchTruckRevenuePlanning(List<Long> carrierIds, List<Long> poolIds, Date dayFrom,
			Date dayTo) {
		// [[
		List<TruckRevenuePlanningEntity> result = null;
		Object[][] params = {{"carrierIds", carrierIds},
							{"dayFrom", dayFrom},
							{"dayTo", dayTo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTruckRevenuePlanning", params);
		}
		// ]]
		try {
			if (carrierIds.isEmpty()) {
				return result = Collections.<TruckRevenuePlanningEntity>emptyList();
			}
			
			dayFrom = DateTimeUtils.getMorningOfDay(dayFrom);
			dayTo = DateTimeUtils.getMidnightOfDay(dayTo);
			
			ReadOnlyDao<TruckRevenuePlanningEntity> trpeDao =
				getDaoProvider().getTruckRevenuePlanningEntityDao();
			
			Filter<TruckRevenuePlanningEntity> f = trpeDao.createFilter();
			
			f.addExpression(Restrictions.in(TruckRevenuePlanningEntity.ATTR_CARRIER_ID, carrierIds));
			if (!poolIds.isEmpty()) {
				f.addExpression(Restrictions.in(TruckRevenuePlanningEntity.ATTR_POOL_ID, poolIds));
			}
			
			if (dayFrom != null) {
				f.addExpression(Restrictions.ge(TruckRevenuePlanningEntity.ATTR_PLANNED_START_DAY, dayFrom));
			}
			if (dayTo != null) {
				f.addExpression(Restrictions.le(TruckRevenuePlanningEntity.ATTR_PLANNED_START_DAY, dayTo));
			}
			
			f.addExpression(Order.asc(TruckRevenuePlanningEntity.ATTR_PLANNED_START_DAY));
			f.addExpression(Order.asc(TruckRevenuePlanningEntity.ATTR_CARRIER_NAME));
			f.addExpression(Order.asc(TruckRevenuePlanningEntity.ATTR_TRUCK_NAME));
			f.addExpression(Order.asc(TruckRevenuePlanningEntity.ATTR_PLANNED_START_OF_UNLOADING));
			
			List<TruckRevenuePlanningEntity> plannings = trpeDao.find(f);
			
			if (plannings.isEmpty()) {
				throw new LogonException(Reason.RES_ERROR_TRUCK_REVENUE_PLANNING_NO_DATA);
			}
			
			return result = plannings; 
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	private Map<Long, List<TruckRevenuePlanMatEntity>> fetchTruckRevenuePlanningMaterials(
			List<Long> orderIds, Locale locale) {
		// [[
		Map result = null;
		Object[][] params = {{"orderIds", orderIds},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTruckRevenuePlanningMaterials", params);
		}
		// ]]
		try {
			Map<Long, List<TruckRevenuePlanMatEntity>> planningMatsByOrder =
				new HashMap<Long, List<TruckRevenuePlanMatEntity>>();
			
			if (!orderIds.isEmpty()) {
				ReadOnlyDao<TruckRevenuePlanMatEntity> trpmeDao =
					getDaoProvider().getTruckRevenuePlanMatEntityDao();
			
				Filter<TruckRevenuePlanMatEntity> f = trpmeDao.createFilter();
			
				f.addExpression(Restrictions.in(TruckRevenuePlanMatEntity.ATTR_ORDER_ID, orderIds));
				f.addExpression(Restrictions.eq(TruckRevenuePlanMatEntity.ATTR_LANG_CD,
						locale.getLanguage()));
			
				f.addExpression(Order.asc(TruckRevenuePlanMatEntity.ATTR_ORDER_ITEM_NAME));
			
				List<TruckRevenuePlanMatEntity> planningMats = trpmeDao.find(f);
			
				for (TruckRevenuePlanMatEntity planningMat: planningMats) {
					Long orderId = planningMat.getOrderId();
			
					List<TruckRevenuePlanMatEntity> planningMatsInnerList =
						planningMatsByOrder.get(orderId);
			
					if (planningMatsInnerList == null) {
						planningMatsInnerList = new ArrayList<TruckRevenuePlanMatEntity>();
						planningMatsByOrder.put(orderId, planningMatsInnerList);
					}
			
					planningMatsInnerList.add(planningMat);
				}
			}
			
			return result = planningMatsByOrder;
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
	 * Builds a tree organizing/sorting the orders by (date, carrier name, truck name).
	 * 
	 * @param plannings original list of plannings, already sorted
	 * @param carrierNamesById map to store carrier names in
	 * @param truckNamesById map to store truck names in
	 * @return the built tree
	 */
	private static Map<Date, Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>>> buildTree(
			List<TruckRevenuePlanningEntity> plannings, Map<Long, String> carrierNamesById,
			Map<Long, String> truckNamesById) {
		// [[
		Map result = null;
		Object[][] params = {{"plannings", plannings},
							{"carrierNamesById", carrierNamesById},
							{"truckNamesById", truckNamesById}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TruckRevenuePlanningServiceBean.class, "buildTree", params);
		}
		// ]]
		try {
			Map<Date, Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>>> carriersByDate =
				new TreeMap<Date, Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>>>();
			
			for (TruckRevenuePlanningEntity planning: plannings) {
				Date plannedStartDay = planning.getPlannedStartDay();
			
				Map<Long, Map<Long, List<TruckRevenuePlanningEntity>>> trucksByCarrier =
					carriersByDate.get(plannedStartDay);
			
				if (trucksByCarrier == null) {
					trucksByCarrier = new LinkedHashMap<Long, Map<Long, List<TruckRevenuePlanningEntity>>>();
					carriersByDate.put(plannedStartDay, trucksByCarrier);
				}
			
				Long carrierId = planning.getCarrierId();
			
				Map<Long, List<TruckRevenuePlanningEntity>> ordersByTruck = trucksByCarrier.get(carrierId);
			
				if (ordersByTruck == null) {
					ordersByTruck = new LinkedHashMap<Long, List<TruckRevenuePlanningEntity>>();
					trucksByCarrier.put(carrierId, ordersByTruck);
				}
			
				Long truckId = planning.getTruckId();
			
				List<TruckRevenuePlanningEntity> orders = ordersByTruck.get(truckId);
			
				if (orders == null) {
					orders = new ArrayList<TruckRevenuePlanningEntity>();
					ordersByTruck.put(truckId, orders);
				}
				
				orders.add(planning);
			
				String carrierName = planning.getCarrierName();
				carrierNamesById.put(carrierId, carrierName);
			
				String isPlanningTruckMark = planning.getIsPlanning() ? "" : " (*)";
				
				String truckName = planning.getTruckName() + isPlanningTruckMark;
				truckNamesById.put(truckId, truckName);
			}
			
			return result = carriersByDate;
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

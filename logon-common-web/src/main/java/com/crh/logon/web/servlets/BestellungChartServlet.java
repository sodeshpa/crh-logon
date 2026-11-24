/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.servlets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.time.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts.util.RequestUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.BestellungChartService;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity;
import com.crh.logon.persistence.entities.views.ChartLoadScheduledEntity;
import com.crh.logon.persistence.entities.views.ChartLoadUnscheduledEntity;
import com.crh.logon.persistence.entities.views.OrdChartNotAssignedEntity;
import com.crh.logon.persistence.entities.views.TruckChartAssignedEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.BestellungChartBean;

/**
 * @author AdNovum Informatik AG
 */
public class BestellungChartServlet extends LogonServlet {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BestellungChartServlet.class);
    
	private ServiceLocator locator;

	@Override
	public void init() throws ServletException {
		locator = getServiceLocator();
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		// [[
		Object[][] params = {{"req", req},
							{"resp", resp}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doGet", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			BestellungChartBean chartBean = BestellungChartBean.getSessionInstance(req);
			
			if (chartBean != null) {
					OutputStream out = resp.getOutputStream();
					int imgWidth = StringUtils.isNumeric(req.getParameter("width")) ? Math.min(1024, Integer.parseInt(req.getParameter("width"))) : 440; 
					int imgHeight = StringUtils.isNumeric(req.getParameter("height")) ? Math.min(768, Integer.parseInt(req.getParameter("height"))) : 240; 
					
					Locale locale = RequestUtils.getUserLocale(req, null);
		
					
					
					JFreeChart chart;
					
					if (Constant.CHART_TYPE_NR_OF_TRUCKS == chartBean.getChartType()) {
						// --- Rail or road ---
						if (Constant.CHART_TRANS_CAT_RAIL ==  chartBean.getTransCatId()) {
							chart = createRailChart(chartBean, locale);
						}
						else {
							chart = createTruckChartOptimized(chartBean, locale);
						}
					}
					else {
						chart = createNrOfLoadingsChart(chartBean, locale);
					}
					
					resp.setContentType("image/png");
					ChartUtilities.writeChartAsPNG(out, chart, imgWidth, imgHeight);
					out.close();
			} 
			else {
				if (logDebugEnabled) {
					LOG.debug("BestellungChartBean is NULL. Maybe an invalid request has been arrived.");
				}				
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
	 * greenArea: Number of the not loaded and not assigned wagons
	 * lightBlueArea: Number of the loaded and not assigned wagons (preloaded wagons)
	 * grayArea: Number of the outgoing wagons
	 */
	private JFreeChart createRailChart(BestellungChartBean chartBean, Locale locale) {
		// [[
		JFreeChart result = null;
		Object[][] params = {{"chartBean", chartBean},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createRailChart", params);
		}
		// ]]
		try {
			
			BestellungChartService bs = locator.getBestellungChartService();
			Calendar selectedDay = chartBean.getUserTimeZone().asCalendar(chartBean.getDeliveryFrom());
			
			String yAxisLabel = Message.lookup(locale, "bestellung.chart.number.of.wagons");
			
			try {
				selectedDay.setTime(chartBean.getUserTimeZone()
											 .parseDayMonthYear(chartBean.getSelectedDayStr()));
			}
			catch (ParseException e) {
				LOG.debug("Unable to parse selected day: keep 'Delivery from' value instead");
				chartBean.setSelectedDayStr(chartBean.getUserTimeZone()
													 .fmtAsDayMonthYear(selectedDay.getTime()));
			}
			LOG.debug("selectedDay  " + selectedDay.getTime());
			
			XYSeriesCollection dataset = new XYSeriesCollection();
			
			JFreeChart chart = ChartFactory.createXYStepChart(null, null, yAxisLabel, dataset,
					PlotOrientation.VERTICAL, false, // legend
					false, // tooltips
					false // urls
			);
			
			addTitlesToChart(chart, chartBean, locale);
			XYPlot plot = chart.getXYPlot(); 
			
			if (chartBean.getRailwayStationIds() != null && !chartBean.getRailwayStationIds().isEmpty()){
				
				List railChartStatisticsList = bs.getRailChartStatistics(selectedDay.getTime(), chartBean.getRailwayStationIds(), chartBean.getWagonTypeIds(), chartBean.getWagonTrspCatIds());
				
				XYSeries greenArea = new XYSeries("greenArea", false, true);
				XYSeriesCollection greenDataset = new XYSeriesCollection(greenArea);
				XYItemRenderer greenRenderer = new XYAreaRenderer();
				
				XYSeries lightBlueArea = new XYSeries("lightBlueArea", false, true);
				XYSeriesCollection lightBlueDataset = new XYSeriesCollection(lightBlueArea);
				XYItemRenderer lightBlueRenderer = new XYAreaRenderer();			
	
				XYSeries grayArea = new XYSeries("grayArea", false, true);
				XYSeriesCollection grayDataset = new XYSeriesCollection(grayArea);
				XYItemRenderer grayRenderer = new XYAreaRenderer();
							
				double grayNr= 0;
				double lightBlueNr= 0;
				double greenNr= 0;
	
				long currentTime = -1L;
			    double loadedWagons = 0;
			    double notLoadedWagons = 0;
			    double incomingWagons = 0;
			    double outgoingWagons = 0;
			    
			    Iterator it = railChartStatisticsList.iterator();
				while (it.hasNext()) {
					Object[] object =  (Object[])it.next();
				
					long actualTime = (object[1] != null) ? ((Timestamp) object[1]).getTime() : 0L;
					String gr = (object[2] != null) ? (object[2]).toString() : "";				
					Double count = (object[3] != null) ? ((BigDecimal)object[3]).doubleValue() : 0L;

					if(currentTime == -1L){
						currentTime = actualTime;
					}
					
					if ( actualTime != currentTime ){		
						grayNr = outgoingWagons;
						lightBlueNr = loadedWagons + grayNr;
						greenNr = notLoadedWagons + incomingWagons + lightBlueNr;
						
						greenArea.add(currentTime, Double.valueOf(greenNr));
						lightBlueArea.add(currentTime, Double.valueOf(lightBlueNr));
						grayArea.add(currentTime, Double.valueOf(grayNr));
						currentTime = actualTime;
						loadedWagons = 0.0;
						notLoadedWagons = 0.0;
						incomingWagons = 0.0;
						outgoingWagons = 0.0;
					}
					
					if ( actualTime == currentTime ) {
						if (gr.equals("loaded")){
							loadedWagons = count;
						}
						if (gr.equals("not loaded")){
							notLoadedWagons = count;
						}
						if (gr.equals("incoming")){
							incomingWagons = count;
						}
						if (gr.equals("outgoing")){
							outgoingWagons = count;
						}
					} 
					
				}
				
				grayRenderer.setSeriesPaint(0, new Color(60,0,0, 100));
				plot.setDataset(2, grayDataset);
				plot.setRenderer(2, grayRenderer);			
	
				lightBlueRenderer.setSeriesPaint(0, new Color(120,170,255, 150));
				plot.setDataset(3, lightBlueDataset);
				plot.setRenderer(3, lightBlueRenderer);
				
				greenRenderer.setSeriesPaint(0, new Color(151,255,120, 150));
				plot.setDataset(4, greenDataset);
				plot.setRenderer(4, greenRenderer);
				
				
				XYSeries fakeArea = new XYSeries("fakeArea", false, true);
				XYSeriesCollection fakeAreaDataset = new XYSeriesCollection(fakeArea);
				XYItemRenderer fakeAreaRenderer = new XYAreaRenderer();
				fakeArea.add(DateTimeUtils.getMorningOfDay(selectedDay).getTimeInMillis(), Double.valueOf(10));
				fakeAreaRenderer.setSeriesPaint(0, Color.GRAY);
				plot.setDataset(5, fakeAreaDataset);
				plot.setRenderer(5, fakeAreaRenderer);
				
			}
			
			plot.getDomainAxis().setLowerMargin(0.0);
			plot.getDomainAxis().setUpperMargin(0.0);
			
			((DateAxis) plot.getDomainAxis()).setTickUnit(new DateTickUnit(DateTickUnit.HOUR, 4));
			((DateAxis) plot.getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("HH:mm"));
			
			
			Stroke s = new BasicStroke(1.2f);
			plot.setDomainGridlineStroke(s);
			plot.setRangeGridlineStroke(s);
			
			return result = chart;
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
	 * 
	 * @param chartBean
	 * @param locale
	 * @return
	 */
	private JFreeChart createNrOfLoadingsChart(BestellungChartBean chartBean, Locale locale) {
		// [[
		JFreeChart result = null;
		Object[][] params = {{"chartBean", chartBean},
				{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createRailChart", params);
		}
		// ]]
		try {			
			BestellungChartService bs = locator.getBestellungChartService();
			ConfigService configSvc = locator.getConfigService();
			
			int scaleInMinutes = 5;
			 
			long appDefaultOpen1From = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1FROMMIN)); 
			long appDefaultOpen1To = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1TOMIN)); 
			long appDefaultOpen2From = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2FROMMIN)); 
			long appDefaultOpen2To = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2TOMIN));
			long appDefaultLoadingTime = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOLOADINGTIMELFMIN)); 
			
			Calendar now = 	DateTimeUtils.getNowCal();
			Calendar deliveryFrom = chartBean.getUserTimeZone().asCalendar(chartBean.getDeliveryFrom());
			Calendar deliveryTo = chartBean.getUserTimeZone().asCalendar(chartBean.getDeliveryTo());
			Calendar selectedDay = (Calendar)deliveryFrom.clone();
			
			String yAxisLabel = Message.lookup(locale, "bestellung.chart.number.of.loadings");
			
			try {
				selectedDay.setTime(chartBean.getUserTimeZone()
											 .parseDayMonthYear(chartBean.getSelectedDayStr()));
			}
			catch (ParseException e) {
				LOG.debug("Unable to parse selected day: keep 'Delivery from' value instead");
				chartBean.setSelectedDayStr(chartBean.getUserTimeZone()
													 .fmtAsDayMonthYear(selectedDay.getTime()));
			}
			LOG.debug("selectedDay  " + selectedDay.getTime());
			
			XYSeriesCollection dataset = new XYSeriesCollection();
			
			JFreeChart chart = ChartFactory.createXYStepChart(null, null, yAxisLabel, dataset,
					PlotOrientation.VERTICAL, false, // legend
					false, // tooltips
					false // urls
					);
			
			addTitlesToChart(chart, chartBean, locale);
			XYPlot plot = chart.getXYPlot(); 
			
			// Input 1: uncheduled orders
			List<ChartLoadUnscheduledEntity> notAssignedList = bs.fetchFilteredNonAssignedLoadings(
					chartBean.getTransCatId(),
					chartBean.getPoolIds(), 
					chartBean.getCarrierIds(), 
					chartBean.getTruckTypeIds(),
					chartBean.getDeliveryTypeIds(),
					chartBean.getPlantId(),
					chartBean.getShippingpointIds(),
					chartBean.getProductTypeIds(),
					selectedDay.getTime());
			
			// Input 2: Scheduled orders
			List<ChartLoadScheduledEntity> scheduledLoadingsList = bs.fetchFilteredScheduledLoadings(
					chartBean.getTransCatId(),
					chartBean.getPoolIds(), 
					chartBean.getCarrierIds(), 
					chartBean.getTruckTypeIds(),
					chartBean.getDeliveryTypeIds(),
					chartBean.getPlantId(),
					chartBean.getShippingpointIds(),
					chartBean.getProductTypeIds(),
					selectedDay.getTime());
			
			Calendar startTime = getStartTime(selectedDay, deliveryFrom);
			Calendar endTime = getEndTime(selectedDay, deliveryTo);
			
			Calendar delFromTime = (Calendar)selectedDay.clone();
			Calendar delToTime = (Calendar)selectedDay.clone();
			
			delFromTime.set(Calendar.HOUR_OF_DAY, deliveryFrom.get(Calendar.HOUR_OF_DAY)); 
			delFromTime.set(Calendar.MINUTE, deliveryFrom.get(Calendar.MINUTE));
			delToTime.set(Calendar.HOUR_OF_DAY, deliveryTo.get(Calendar.HOUR_OF_DAY)); 
			delToTime.set(Calendar.MINUTE, deliveryTo.get(Calendar.MINUTE));

			// ------------
			// Purple area: Fixed orders
			// ------------
			startTime = getStartTime(selectedDay, deliveryFrom);			
			Double fixedDeliveryMaxVal = 0d;			
			XYSeries fixedDeliverySeries = new XYSeries("Purple", false, true);
			XYSeriesCollection fixedDeliveryDataset = new XYSeriesCollection(fixedDeliverySeries);
			XYItemRenderer fixedDeliveryRenderer = new XYAreaRenderer2();
			Map<Long, Double> purpleAreaMap = new HashMap<Long, Double>();
			
			TimeZone appTimeZone = TimeZone.getAppTimeZone();
			TimeZone userTimeZone = chartBean.getUserTimeZone();
			
			double defLoadingDurInMillis = appDefaultLoadingTime * 60000;
			while (startTime.before(endTime) || startTime.equals(endTime)) {
				
				Double counter = 0d;
				
				// Assigned orders
				for (ChartLoadScheduledEntity scheduledLoading : scheduledLoadingsList) {
					Date firstLoadDate = scheduledLoading.getFirstLoadDat();
					Date firstToDate = scheduledLoading.getFirstToDat();
					Date shiftedFirstLoadDate = userTimeZone.shiftToTimeZone(firstLoadDate, appTimeZone);
					Date shiftedFirstToDate = userTimeZone.shiftToTimeZone(firstToDate, appTimeZone);
					if (DateTimeUtils.isBetween(startTime.getTime(), shiftedFirstLoadDate, shiftedFirstToDate)) {
						counter += 1;
					}
				}
				purpleAreaMap.put(startTime.getTimeInMillis(), counter);
			
				fixedDeliverySeries.add(startTime.getTimeInMillis(), counter);
				startTime.add(Calendar.MINUTE, scaleInMinutes);				
				fixedDeliveryMaxVal = (counter > fixedDeliveryMaxVal ? counter : fixedDeliveryMaxVal);
			}
			
			fixedDeliveryRenderer.setSeriesPaint(0, new Color(153, 50, 153, 200));
			
			plot.setDataset(5, fixedDeliveryDataset);
			plot.setRenderer(5, fixedDeliveryRenderer);			
			
			// ------------
			// Blue area: Flexible orders
			// ------------
			Double flexdeliveryMaxVal = 0d;
			
			XYSeries flexDeliverySeries = new XYSeries("Blue", false, true);
			XYSeriesCollection flexDeliveryDataset = new XYSeriesCollection(flexDeliverySeries);
			XYItemRenderer flexDeliveryRenderer = new XYAreaRenderer();
			startTime = getStartTime(selectedDay, deliveryFrom);
			
			while (startTime.before(endTime) || startTime.equals(endTime)) {
				
				Double counter = 0d;
				HashSet<String> compositeSet=new HashSet<String>();
				for (ChartLoadUnscheduledEntity noAssignEntity : notAssignedList) {					
					if (!compositeSet.contains(noAssignEntity.getCompositeId())) {
						compositeSet.add(noAssignEntity.getCompositeId());
						
						Calendar unavailableStartDate = chartBean.getUserTimeZone().asCalendar(noAssignEntity.getDeliveryFromDat());
						// FIXME (bartha Jul 24, 2012): check how can be noAssignEntity.getPlannedLoadingTimeMin() null?
						Long loadingTime = noAssignEntity.getPlannedLoadingTimeMin() == null ? 0L : noAssignEntity.getPlannedLoadingTimeMin();
						unavailableStartDate.add(Calendar.MINUTE, loadingTime.intValue() * -1);
						unavailableStartDate.add(Calendar.MINUTE, noAssignEntity.getTransportDurationMin().intValue() * -1);
						
						Calendar unavailableEndDate = chartBean.getUserTimeZone().asCalendar(noAssignEntity.getDeliveryToDat());
						unavailableEndDate.add(Calendar.MINUTE, noAssignEntity.getTransportDurationMin().intValue() * -1);
						
						if (DateTimeUtils.isBetween(startTime.getTime(), unavailableStartDate.getTime(), unavailableEndDate.getTime())) {
							
							Calendar delivFrom = chartBean.getUserTimeZone().asCalendar(noAssignEntity.getDeliveryFromDat());
							Calendar delivTo = chartBean.getUserTimeZone().asCalendar(noAssignEntity.getDeliveryToDat());														 
							Calendar open1From = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen1From() == null ? appDefaultOpen1From : noAssignEntity.getOpen1From());
							Calendar open1To = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen1To() == null ? appDefaultOpen1To : noAssignEntity.getOpen1To());
							Calendar open2From = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen2From() == null ? appDefaultOpen2From : noAssignEntity.getOpen2From());
							Calendar open2To = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen2To() == null ? appDefaultOpen2To : noAssignEntity.getOpen2To());
							
							double delivDiffMillis = DateTimeUtils.getDeliveryTimeDiffMillis(delivFrom, delivTo, open1From, open1To, open2From, open2To);						
							
							double loadingDurInMillis = noAssignEntity.getLoadingTime() == null ? defLoadingDurInMillis : noAssignEntity.getLoadingTime() * 60000;
							counter += (double)(loadingDurInMillis / (delivDiffMillis + loadingDurInMillis));
						}
					}									
				}
				
				counter +=  purpleAreaMap.get(startTime.getTimeInMillis());
				flexDeliverySeries.add(startTime.getTimeInMillis(), counter);
				startTime.add(Calendar.MINUTE, scaleInMinutes);				
				flexdeliveryMaxVal = (counter > flexdeliveryMaxVal ? counter : flexdeliveryMaxVal);
			}
			
			flexDeliveryRenderer.setSeriesPaint(0, new Color(153, 153, 255, 90));
			
			plot.setDataset(2, flexDeliveryDataset);
			plot.setRenderer(2, flexDeliveryRenderer);				
			
			// ------------
			// Blue line: used as a trick to show current time
			// ------------
			startTime = getStartTime(selectedDay, deliveryFrom);
			
			XYSeries currTimeSeries = new XYSeries("Current time", false, true);
			XYItemRenderer currTimeRenderer = plot.getRenderer();
			
			if (DateTimeUtils.cleanTimeWithDefaultTimeZone(now.getTime()).equals(DateTimeUtils.cleanTimeWithDefaultTimeZone(selectedDay.getTime())) && now.before(endTime)) {
				currTimeSeries.add(startTime.getTime().getTime(), 0);
				currTimeSeries.add(now.getTime().getTime() + 1, flexdeliveryMaxVal + 2);
				currTimeSeries.add(now.getTime().getTime() + 2, 0);
				currTimeSeries.add(endTime.getTime().getTime(), 0);
			}
			dataset.addSeries(currTimeSeries);
			
			currTimeRenderer.setSeriesPaint(0, Color.blue);			
			
			// ------------
			// Gray, half-transparent area: used to show the delivery from/to range
			// ------------
			startTime = getStartTime(selectedDay, deliveryFrom);
			
			XYSeries delRangeSeries = new XYSeries("Delivery from/to", false, true);
			XYSeriesCollection delRangeDataset = new XYSeriesCollection(delRangeSeries);
			XYItemRenderer delRangeRenderer = new XYAreaRenderer();
			
			delRangeSeries.add(startTime.getTime().getTime(), 0);
			delRangeSeries.add(delFromTime.getTime().getTime() - 1, 0);
			delRangeSeries.add(delFromTime.getTime().getTime(), flexdeliveryMaxVal + 2);
			delRangeSeries.add(delToTime.getTime().getTime(), flexdeliveryMaxVal + 2);
			delRangeSeries.add(delToTime.getTime().getTime() + 1, 0);
			delRangeSeries.add(endTime.getTime().getTime(), 0);
			
			delRangeRenderer.setSeriesPaint(0, new Color(253, 253, 253, 50));
			
			plot.setDataset(3, delRangeDataset);
			plot.setRenderer(3, delRangeRenderer);			
			
			plot.getDomainAxis().setLowerMargin(0.0);
			plot.getDomainAxis().setUpperMargin(0.0);			
			
			return result = chart;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	private JFreeChart createTruckChartOptimized(BestellungChartBean chartBean, Locale locale) {
		// [[
		JFreeChart result = null;
		Object[][] params = {{"chartBean", chartBean},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createTruckChart", params);
		}
		// ]]
		try {

			BestellungChartService bestellungChartService = locator.getBestellungChartService();
			ConfigService configSvc = locator.getConfigService();
			
			int scaleInMinutes = 5;
			 
			long appDefaultOpen1From = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1FROMMIN)); 
			long appDefaultOpen1To = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1TOMIN)); 
			long appDefaultOpen2From = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2FROMMIN)); 
			long appDefaultOpen2To = Long.parseLong(configSvc.getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2TOMIN));
			
			final TimeZone timeZone = chartBean.getUserTimeZone();
			
			Calendar now = timeZone.asCalendar();
			Calendar deliveryFrom = timeZone.asCalendar(chartBean.getDeliveryFrom());
			Calendar deliveryTo = timeZone.asCalendar(chartBean.getDeliveryTo());
			Calendar selectedDay = (Calendar)deliveryFrom.clone();
			
			String yAxisLabel = Message.lookup(locale, "bestellung.chart.number.of.trucks");
			
			try {
				selectedDay.setTime(timeZone.parseDayMonthYear(chartBean.getSelectedDayStr()));
			}
			catch (ParseException e) {
				LOG.debug("Unable to parse selected day: keep 'Delivery from' value instead");
				chartBean.setSelectedDayStr(timeZone.fmtAsDayMonthYear(selectedDay.getTime()));
			}
			
			Calendar startTime = getStartTime(selectedDay, deliveryFrom);
			Calendar endTime = getEndTime(selectedDay, deliveryTo);
			
			Calendar delFromTime = (Calendar)selectedDay.clone();
			Calendar delToTime = (Calendar)selectedDay.clone();
			
			delFromTime.set(Calendar.HOUR_OF_DAY, deliveryFrom.get(Calendar.HOUR_OF_DAY)); 
			delFromTime.set(Calendar.MINUTE, deliveryFrom.get(Calendar.MINUTE));
			delToTime.set(Calendar.HOUR_OF_DAY, deliveryTo.get(Calendar.HOUR_OF_DAY)); 
			delToTime.set(Calendar.MINUTE, deliveryTo.get(Calendar.MINUTE));
			
			XYSeriesCollection dataset = new XYSeriesCollection();
			
			JFreeChart chart = ChartFactory.createXYStepChart(null, null, yAxisLabel, dataset,
					PlotOrientation.VERTICAL, false, // legend
					false, // tooltips
					false // urls
			);
			
			addTitlesToChart(chart, chartBean, locale);
			
			XYPlot plot = chart.getXYPlot(); 
			
			// ------------
			// Red line: available trucks
			// ------------
			XYSeries availTruckSeries = new XYSeries("Verfubare LKWs", false, true);
			XYSeriesCollection availTruckDataset = new XYSeriesCollection(availTruckSeries);
			XYItemRenderer availTruckRenderer = new XYLineAndShapeRenderer();
			
			List carrierIds = chartBean.getCarrierIds();
			Long truckTotalCnt = bestellungChartService.getFilteredTruckTotalCount(
					chartBean.getPoolIds(), 
					carrierIds, 
					chartBean.getTruckTypeIds(),
					chartBean.getProductTypeIds()); 
			
			
			List unavList = bestellungChartService.fetchFilteredTruckUnavailabilities(
					chartBean.getPoolIds(), 
					carrierIds, 
					chartBean.getTruckTypeIds(),
					chartBean.getProductTypeIds(),
					selectedDay.getTime());
			
			while (startTime.before(endTime) || startTime.equals(endTime)) {
				long availCnt = truckTotalCnt;
				Set<Long> truckSet = new HashSet<Long>();
				for (int i=0; i<unavList.size(); i++) {
					TruckUnavailabilityEntity unavEntity = (TruckUnavailabilityEntity)unavList.get(i);
					if (DateTimeUtils.isBetween(startTime.getTime(), unavEntity.getStartDat(), unavEntity.getEndDat()) 
							&& unavEntity.getTruck().getActiveFlag()
							&& !truckSet.contains(unavEntity.getTruck().getTruckId())) {
						truckSet.add(unavEntity.getTruck().getTruckId());
						availCnt--;
					}
				}
				availTruckSeries.add(startTime.getTime().getTime(), availCnt);
				startTime.add(Calendar.MINUTE, scaleInMinutes);
			}
			
			availTruckRenderer.setSeriesPaint(0, Color.red);
			availTruckRenderer.setSeriesShape(0, ShapeUtilities.createDiamond(0.0f));
			
			plot.setDataset(1, availTruckDataset);
			plot.setRenderer(1, availTruckRenderer);
			
			// ------------
			// Blue line: used as a trick to show current time
			// ------------
			startTime = getStartTime(selectedDay, deliveryFrom);
			
			XYSeries currTimeSeries = new XYSeries("Current time", false, true);
			XYItemRenderer currTimeRenderer = plot.getRenderer();
			
			if (DateTimeUtils.cleanTimeWithDefaultTimeZone(now.getTime()).equals(DateTimeUtils.cleanTimeWithDefaultTimeZone(selectedDay.getTime())) && now.before(endTime)) {
				currTimeSeries.add(startTime.getTime().getTime(), 0);
				currTimeSeries.add(now.getTime().getTime() + 1, truckTotalCnt + 2);
				currTimeSeries.add(now.getTime().getTime() + 2, 0);
				currTimeSeries.add(endTime.getTime().getTime(), 0);
			}
			dataset.addSeries(currTimeSeries);
			
			currTimeRenderer.setSeriesPaint(0, Color.blue);
			
			
			// Input 1: Not assigned orders
			List<OrdChartNotAssignedEntity> notAssignedList = bestellungChartService.fetchFilteredNonAssignedOrders(
					chartBean.getPoolIds(), 
					carrierIds, 
					chartBean.getTruckTypeIds(),
					chartBean.getPlantId(),
					chartBean.getShippingpointIds(),
					chartBean.getProductTypeIds(),
				selectedDay.getTime());
			
			// Input 2: Assigned orders
			List<TruckChartAssignedEntity> assignedList = bestellungChartService.fetchAssignedTrucks(
					chartBean.getPoolIds(), 
					carrierIds, 
					chartBean.getTruckTypeIds(),
					chartBean.getPlantId(),
					chartBean.getShippingpointIds(),
					chartBean.getProductTypeIds(),
					selectedDay.getTime());
			
			TruckChartAssignedEntity dummyEnd = new TruckChartAssignedEntity();
			dummyEnd.setFromDat(new Date(0));
			dummyEnd.setToDat(new Date(1));
			dummyEnd.setAssignmentId(-1L);
			assignedList.add(dummyEnd);
			
			// ------------
			// Blue area: Flexible orders
			// ------------
			Double flexdeliveryMaxVal = 0d;
			
			XYSeries flexDeliverySeries = new XYSeries("Purple", false, true);
			XYSeriesCollection flexDeliveryDataset = new XYSeriesCollection(flexDeliverySeries);
			XYItemRenderer flexDeliveryRenderer = new XYAreaRenderer();
			startTime = getStartTime(selectedDay, deliveryFrom);

			Double fixedDeliveryMaxVal = 0d;
			XYSeries fixedDeliverySeries = new XYSeries("Purple", false, true);
			XYSeriesCollection fixedDeliveryDataset = new XYSeriesCollection(fixedDeliverySeries);
			XYItemRenderer fixedDeliveryRenderer = new XYAreaRenderer();
			
			/*
			 * The loop below will use these four Calendar instances instead of creating them 
			 * on every iteration.
			 */
			Calendar unavStartDat = timeZone.asCalendar();
			Calendar unavEndDat = timeZone.asCalendar();
			Calendar delivFrom = timeZone.asCalendar();
			Calendar delivTo = timeZone.asCalendar();
			
			while (startTime.before(endTime) || startTime.equals(endTime)) {
				
				Double counter = 0d;
				Double purpleCounter = 0d;
				
				// Non-assigned orders. Duration = Loading time + Transport Duration + Unloading time + Transport Duration
				for (OrdChartNotAssignedEntity noAssignEntity : notAssignedList) {
					
					// Flexible deliveries: from != to
					if (!noAssignEntity.getDeliveryFromDat().equals(noAssignEntity.getDeliveryToDat()))  {
						Long loadingTime = noAssignEntity.getPlannedLoadingTimeMin() == null ? 0L : noAssignEntity.getPlannedLoadingTimeMin();
						Long unLoadingTime = noAssignEntity.getPlannedUnloadingTimeMin(); 

						unavStartDat = calculateUnavailableStartDate(unavStartDat, noAssignEntity, loadingTime);
						unavEndDat = calculateUnavailableEndDate(unavEndDat, noAssignEntity);

						if (DateTimeUtils.isBetween(startTime.getTime(), unavStartDat.getTime(), unavEndDat.getTime())) {
							
							delivFrom.setTime(noAssignEntity.getDeliveryFromDat());
							delivTo.setTime(noAssignEntity.getDeliveryToDat());
							
							double circulationTimeMillis = (loadingTime + noAssignEntity.getTransportDurationMin().intValue() * 2 + unLoadingTime) * 60000;
							 
							Calendar open1From = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen1From() == null ? appDefaultOpen1From : noAssignEntity.getOpen1From());
							Calendar open1To = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen1To() == null ? appDefaultOpen1To : noAssignEntity.getOpen1To());
							Calendar open2From = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen2From() == null ? appDefaultOpen2From : noAssignEntity.getOpen2From());
							Calendar open2To = DateTimeUtils.getCalendarFromMinAfterMidnight(noAssignEntity.getOpen2To() == null ? appDefaultOpen2To : noAssignEntity.getOpen2To());
							
							Long delivDiffMillis = DateTimeUtils.getDeliveryTimeDiffMillis(delivFrom, delivTo, open1From, open1To, open2From, open2To);
							Double increments = circulationTimeMillis / (delivDiffMillis + circulationTimeMillis);
							counter += increments;
							
							if (noAssignEntity.getDeliveryFromDat().equals(noAssignEntity.getDeliveryToDat()))  {
								purpleCounter += increments;
							}
						}
					}
				}

				if (purpleCounter > 0d) {
					purpleCounter += 1;
				}
				
				for (TruckChartAssignedEntity assignEntity : assignedList) {
					if (DateTimeUtils.isBetween(startTime.getTime(), assignEntity.getFromDat(), assignEntity.getToDat())) {
						purpleCounter += counter + 1;
					}
				}
				
				flexDeliverySeries.add(startTime.getTimeInMillis(), counter);
				fixedDeliverySeries.add(startTime.getTimeInMillis(), purpleCounter);

				startTime.add(Calendar.MINUTE, scaleInMinutes);
				flexdeliveryMaxVal = (counter > flexdeliveryMaxVal ? counter : flexdeliveryMaxVal);
				fixedDeliveryMaxVal = (purpleCounter > fixedDeliveryMaxVal ? purpleCounter : fixedDeliveryMaxVal);
			}
			
			flexDeliveryRenderer.setSeriesPaint(0, new Color(153, 153, 255, 90));
			
			plot.setDataset(5, flexDeliveryDataset);
			plot.setRenderer(5, flexDeliveryRenderer);
			
			fixedDeliveryRenderer.setSeriesPaint(0, new Color(153, 50, 153, 80));
			
			plot.setDataset(2, fixedDeliveryDataset);
			plot.setRenderer(2, fixedDeliveryRenderer);
			
			// ------------
			// Gray, half-transparent area: used to show the delivery from/to range
			// ------------
			startTime = getStartTime(selectedDay, deliveryFrom);
			
			XYSeries delRangeSeries = new XYSeries("Delivery from/to", false, true);
			XYSeriesCollection delRangeDataset = new XYSeriesCollection(delRangeSeries);
			XYItemRenderer delRangeRenderer = new XYAreaRenderer();
			
			delRangeSeries.add(startTime.getTime().getTime(), 0);
			delRangeSeries.add(delFromTime.getTime().getTime() - 1, 0);
			delRangeSeries.add(delFromTime.getTime().getTime(), NumberUtils.max(truckTotalCnt.doubleValue(), Math.max(flexdeliveryMaxVal, fixedDeliveryMaxVal), 0d) + 2);
			delRangeSeries.add(delToTime.getTime().getTime(), NumberUtils.max(truckTotalCnt.doubleValue(), Math.max(flexdeliveryMaxVal, fixedDeliveryMaxVal), 0d) + 2);
			delRangeSeries.add(delToTime.getTime().getTime() + 1, 0);
			delRangeSeries.add(endTime.getTime().getTime(), 0);
			
			delRangeRenderer.setSeriesPaint(0, new Color(253, 253, 253, 50));
			
			plot.setDataset(3, delRangeDataset);
			plot.setRenderer(3, delRangeRenderer);
			
			plot.getDomainAxis().setLowerMargin(0.0);
			plot.getDomainAxis().setUpperMargin(0.0);
			
			return result = chart;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private Calendar calculateUnavailableStartDate(
			Calendar unavStartDat, OrdChartNotAssignedEntity noAssignEntity, Long loadingTime) {
		unavStartDat.setTime(noAssignEntity.getDeliveryFromDat()); // == DeliveryToDat
		// FIXME (bartha Jul 24, 2012): check how can be noAssignEntity.getPlannedLoadingTimeMin() null?		
		unavStartDat.add(Calendar.MINUTE, loadingTime.intValue() * -1);
		unavStartDat.add(Calendar.MINUTE, noAssignEntity.getTransportDurationMin().intValue() * -1);
		return unavStartDat;
	}

	private Calendar calculateUnavailableEndDate(
			Calendar unavEndDat, OrdChartNotAssignedEntity noAssignEntity) {
		unavEndDat.setTime(noAssignEntity.getDeliveryToDat());
		Long unLoadingTime = noAssignEntity.getPlannedUnloadingTimeMin(); 
		unavEndDat.add(Calendar.MINUTE, unLoadingTime.intValue());
		unavEndDat.add(Calendar.MINUTE, noAssignEntity.getTransportDurationMin().intValue());
		return unavEndDat;
	}
	
	private Calendar getStartTime(Calendar selectedDay, Calendar deliveryFrom) {
		Calendar startTime = (Calendar)selectedDay.clone();
		startTime.set(Calendar.HOUR_OF_DAY, Math.min(5, deliveryFrom.get(Calendar.HOUR_OF_DAY) - 1)); 
		startTime.set(Calendar.MINUTE, 0);
		return startTime;
	}
	
	private Calendar getEndTime(Calendar selectedDay, Calendar deliveryTo) {
		Calendar endTime = (Calendar)selectedDay.clone();
		endTime.set(Calendar.HOUR_OF_DAY, Math.max(18, deliveryTo.get(Calendar.HOUR_OF_DAY) + 1)); 
		endTime.set(Calendar.MINUTE, 30);
		return endTime;
	}
	
	private void addTitlesToChart(JFreeChart chart, BestellungChartBean chartBean, Locale locale) {
		
		String title = Message.lookup(locale, "bestellung.chart.title") + " " + chartBean.getSelectedDayStr();
		
		TextTitle txtTitle = new TextTitle(title, new Font(Font.SANS_SERIF, Font.BOLD, 14));
		
		Date now = DateTimeUtils.getNow();
		
		String genAt = chartBean.getUserTimeZone().fmtAsDayMonthYear(now);
		String dt = chartBean.getUserTimeZone().fmtAsHourMinuteSecond(now);
		TextTitle subTitle = new TextTitle("Generated at: " + genAt + " " + dt, new Font(Font.DIALOG, Font.PLAIN, 10));
		
		chart.setTitle(txtTitle);
		chart.addSubtitle(subTitle);
		
	}
	
	private List<Long> getListIds(String poolIds) {
		// [[
		List<Long> result = null;
		Object[][] params = {{"poolIds", poolIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPoolIds", params);
		}
		// ]]
		try {
			if (com.crh.logon.utils.StringUtils.empty(poolIds)) {
				return result = null;
			}
			
			List<String> helperList =  Arrays.asList(poolIds.split(","));
			
			if (helperList == null || helperList.isEmpty()) {
				return null;
			}
			
			result = new ArrayList<Long>();
			for (String poolId : helperList) {
				result.add(Long.valueOf(poolId));
			}
			
			return result;
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

/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.servlets;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.RequestUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CustomXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.views.BizeventTruckLogEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 */
public class TruckLogChartServlet extends LogonServlet {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckLogChartServlet.class);

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
			Authorization.authorize(Authorization.ORDER_DETAIL_TRUCK_LOG);
			
			OutputStream out = resp.getOutputStream();
			String orderIdStr = req.getParameter("orderId");
			int imgWidth = StringUtils.isNumeric(req.getParameter("width")) ? Math.min(1024,
					Integer.parseInt(req.getParameter("width"))) : 440;
			int imgHeight = StringUtils.isNumeric(req.getParameter("height")) ? Math.min(768,
					Integer.parseInt(req.getParameter("height"))) : 240;
			
			Locale locale = RequestUtils.getUserLocale(req, null);
			Long orderId = null;
			try {
				orderId = new Long(orderIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Wrong orderid format!");
			}
			
			OrderDetailService bs = locator.getOrderDetailService();
			List<BizeventTruckLogEntity> truckLoglist = bs.loadTruckLog(orderId, locale.getLanguage());
			JFreeChart chart = createTruckLogChart(truckLoglist, locale);
			
			resp.setContentType("image/png");
			ChartUtilities.writeChartAsPNG(out, chart, imgWidth, imgHeight);
			out.close();
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	@SuppressWarnings("deprecation")
	private JFreeChart createTruckLogChart(List<BizeventTruckLogEntity> truckLogList, Locale locale) {
		// [[
		JFreeChart result = null;
		Object[][] params = {{"truckLogList", truckLogList},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createTruckLogChart", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_DETAIL_TRUCK_LOG);
			String yAxisLabel = Message.lookup(locale, "orderdetail.truck.log.yaxis");
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			TimeSeries timeSeries = new TimeSeries("Speed", Second.class);
			
			ArrayList<String> toolTips = new ArrayList<String>();
			
			for (BizeventTruckLogEntity log : truckLogList) {
				Second time = new Second(log.getModTs());
				timeSeries.addOrUpdate(time, log.getSpeed());
				toolTips.add(log.getBizeventTypeTxt());
			}
			
			//tool tip
			CustomXYToolTipGenerator ttg = new CustomXYToolTipGenerator();
			ttg.addToolTipSeries(toolTips);
			
			dataset.addSeries(timeSeries);
			JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, yAxisLabel, dataset,
					false, true, true);
			
			chart.setBackgroundPaint(Color.white);
			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			plot.setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
			plot.setDomainCrosshairVisible(true);
			plot.setRangeCrosshairVisible(true);
			XYItemRenderer r = plot.getRenderer();
			r.setSeriesPaint(0, new Color(153, 153, 255));
			r.setToolTipGenerator(ttg);
			if (r instanceof XYLineAndShapeRenderer) {
				XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
				renderer.setBaseShapesVisible(true);
				renderer.setBaseShapesFilled(true);
			}
			
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

}

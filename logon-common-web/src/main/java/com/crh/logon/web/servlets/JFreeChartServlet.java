/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.servlets;

import java.awt.Color;
import java.awt.Shape;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.date.DateUtilities;
import org.jfree.util.ShapeUtilities;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * @author AdNovum Informatik AG
 */
public class JFreeChartServlet extends LogonServlet {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(JFreeChartServlet.class);
    
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
			
			OutputStream out = resp.getOutputStream();
			String chartType = req.getParameter("type");
			
			JFreeChart chart;
			
			if ("1".equals(chartType)) {
				chart = createChart1();
			}
			else if ("2".equals(chartType)) {
				chart = createChart2();
			}
			else if ("3".equals(chartType)) {
				chart = createChart3();
			}
			else {
				chart = createChart1();
			}
			
			resp.setContentType("image/png");
			ChartUtilities.writeChartAsPNG(out, chart, 600, 300);
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

	private JFreeChart createChart1() {
		// [[
		JFreeChart result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createChart1");
		}
		// ]]
		try {
			
			String title = "Auslastung Distribution";
			String yAxisLabel = "Anzahl Bahnwagen";
			
			Calendar now = Calendar.getInstance();
			
			XYSeries currTimeSeries = new XYSeries("Current time", false, true);
			currTimeSeries.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), 0);
			currTimeSeries.add(DateUtilities.createDate(2008, 12, 24, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)).getTime(), 13);
			
			XYSeries s0 = new XYSeries("Verfugbar", false, true);
			s0.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), 12);
			s0.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), 12);
			
			XYSeries s1 = new XYSeries("Zugewiesen", false, true);
			s1.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), 2);
			s1.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), 2);
			
			XYSeries s2 = new XYSeries("Vorgeladen Normo", false, true);
			s2.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), 3);
			s2.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), 3);
			
			XYSeries s3 = new XYSeries("Vorgeladen Fluvio", false, true);
			s3.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), 4);
			s3.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), 4);
			
			XYSeries s4 = new XYSeries("Verfugbar", false, true);
			s4.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), 4);
			s4.add(DateUtilities.createDate(2008, 12, 24, 9, 0).getTime(), 8);
			s4.add(DateUtilities.createDate(2008, 12, 24, 13, 0).getTime(), 11);
			s4.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), 11);
			
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(currTimeSeries);
			dataset.addSeries(s0);
			
			JFreeChart chart = ChartFactory.createXYStepChart(title, null, yAxisLabel, dataset,
					PlotOrientation.VERTICAL, true, // legend
					false, // tooltips
					false // urls
			);
			
			
			XYPlot plot = chart.getXYPlot();
			XYItemRenderer renderer = plot.getRenderer();
			renderer.setSeriesPaint(0, Color.blue);
			renderer.setSeriesPaint(1, Color.red);
			
			XYSeriesCollection dataset1 = new XYSeriesCollection();
			dataset1.addSeries(s1);
			dataset1.addSeries(s2);
			dataset1.addSeries(s3);
			dataset1.addSeries(s4);
			
			XYItemRenderer renderer1 = new XYStepAreaRenderer();
			renderer1.setSeriesPaint(0, Color.gray);
			renderer1.setSeriesPaint(1, Color.black);
			renderer1.setSeriesPaint(2, new Color(0, 180, 0));
			renderer1.setSeriesPaint(3, Color.white);
			
			plot.setDataset(1, dataset1);
			plot.setRenderer(1, renderer1);
			
			//legend
			Shape shape = ShapeUtilities.createDiamond(5.0f);
			LegendItemCollection legentCol = new LegendItemCollection();
			LegendItem item1 = new LegendItem("Verfugbar", null, null, null, shape, Color.white);
			LegendItem item2 = new LegendItem("Zugewiesen", null, null, null, shape, Color.gray);
			LegendItem item3 = new LegendItem("Vorgeladen Normo", null, null, null, shape, Color.black);
			LegendItem item4 = new LegendItem("Vorgeladen Fluvio", null, null, null, shape, new Color(0, 180, 0));
			LegendItem item5 = new LegendItem("Anzahl Auftrage", null, null, null, shape, Color.red);
			legentCol.add(item1);
			legentCol.add(item2);
			legentCol.add(item3);
			legentCol.add(item4);
			legentCol.add(item5);
			plot.setFixedLegendItems(legentCol);
			
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

	private JFreeChart createChart2() {
		// [[
		JFreeChart result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createChart2");
		}
		// ]]
		try {
			
			String title = "Auslastung Distribution";
			String yAxisLabel = "Anzahl LKWs";
			
			Random rnd = new Random();
			
			Calendar now = Calendar.getInstance();
			
			
			XYSeries currTimeSeries = new XYSeries("Current time", false, true);
			currTimeSeries.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), 0);
			currTimeSeries.add(DateUtilities.createDate(2008, 12, 24, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)).getTime(), 30);
			
			XYSeries s0 = new XYSeries("Verfubare LKWs", false, true);
			s0.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 7, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 8, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 11, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 12, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 14, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 15, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 16, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 17, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 19, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 20, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 21, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 22, 0).getTime(), rnd.nextInt(15) + 15);
			s0.add(DateUtilities.createDate(2008, 12, 24, 23, 0).getTime(), 0);
			
			XYSeries s1 = new XYSeries("Davon Terminlieferungen", false, true);
			s1.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 7, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 8, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 9, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 10, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 11, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 12, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 13, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 14, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 15, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 16, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 19, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 20, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 21, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 22, 0).getTime(), rnd.nextInt(12) + 3);
			s1.add(DateUtilities.createDate(2008, 12, 24, 23, 0).getTime(), 0);
			
			XYSeries s2 = new XYSeries("Belegte LKWs", false, true);
			s2.add(DateUtilities.createDate(2008, 12, 24, 6, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 7, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 8, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 9, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 10, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 11, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 12, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 13, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 14, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 15, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 16, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 18, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 19, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 20, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 21, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 22, 0).getTime(), rnd.nextInt(15) + 15);
			s2.add(DateUtilities.createDate(2008, 12, 24, 23, 0).getTime(), 0);
			
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(currTimeSeries);
			
			JFreeChart chart = ChartFactory.createXYStepChart(title, null, yAxisLabel, dataset,
					PlotOrientation.VERTICAL, true, // legend
					false, // tooltips
					false // urls
			);
			
			XYPlot plot = chart.getXYPlot();
			XYItemRenderer renderer = plot.getRenderer();
			renderer.setSeriesPaint(0, Color.blue);
			
			XYSeriesCollection dataset0 = new XYSeriesCollection();
			dataset0.addSeries(s0);
			XYItemRenderer renderer0 = new XYLineAndShapeRenderer();
			renderer0.setSeriesPaint(0, Color.red);
			renderer0.setSeriesShape(0, ShapeUtilities.createDiamond(0.0f));
			plot.setDataset(1, dataset0);
			plot.setRenderer(1, renderer0);
			
			XYSeriesCollection dataset1 = new XYSeriesCollection();
			dataset1.addSeries(s1);
			dataset1.addSeries(s2);
			XYItemRenderer renderer1 = new XYAreaRenderer();
			renderer1.setSeriesPaint(0, new Color(153, 51, 102));
			renderer1.setSeriesPaint(1, new Color(153, 153, 255));
			plot.setDataset(2, dataset1);
			plot.setRenderer(2, renderer1);
			
			plot.getDomainAxis().setLowerMargin(0.0);
			plot.getDomainAxis().setUpperMargin(0.0);
			
			//legend
			Shape shape = ShapeUtilities.createDiamond(5.0f);
			LegendItemCollection legentCol = new LegendItemCollection();
			LegendItem item1 = new LegendItem("Belegte LKWs", null, null, null, shape, new Color(153, 153, 255));
			LegendItem item2 = new LegendItem("Davon terminlieferungen", null, null, null, shape, new Color(153, 51, 102));
			LegendItem item3 = new LegendItem("Verfubare LKWs", null, null, null, shape, Color.red);
			legentCol.add(item1);
			legentCol.add(item2);
			legentCol.add(item3);
			plot.setFixedLegendItems(legentCol);
			
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


	private JFreeChart createChart3() {
		// [[
		JFreeChart result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createChart3");
		}
		// ]]
		try {
			
			String title = "ZH-125243";
			String yAxisLabel = "Geschwindigkeit";
			
			XYDataset dataset = createDataset(400);
			JFreeChart chart = ChartFactory.createXYStepChart(title, null, yAxisLabel,
					dataset, PlotOrientation.VERTICAL, false, false, false);
			
			XYPlot plot = chart.getXYPlot();
			plot.getDomainAxis().setLowerMargin(0.0);
			plot.getDomainAxis().setUpperMargin(0.0);
			plot.setForegroundAlpha(0.7f);
			
			XYItemRenderer renderer = plot.getRenderer();
			renderer.setSeriesPaint(0, new Color(153, 153, 255));
			
			// paint area below because there is a bug ChartFactory.createXYStepAreaChart
			XYItemRenderer renderer1 = new XYStepAreaRenderer(); 
			renderer1.setSeriesPaint(0, new Color(153, 153, 255));
			plot.setDataset(1, dataset);
			plot.setRenderer(1, renderer1);
			
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
	 * Create dummy data set. Later change with real one.
	 * @param count number of data points
	 * @return XYDataset
	 */
	private XYDataset createDataset(final int count) {
		// [[
		XYDataset result = null;
		Object[][] params = {{"count", count + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createDataset", params);
		}
		// ]]
		try {
			final TimeSeriesCollection dataset = new TimeSeriesCollection();
			final TimeSeries s1 = new TimeSeries("", Minute.class );
			RegularTimePeriod start = new Minute();
			double force = 3.0;
			for (int i = 0; i < count; i++) {
				s1.add(start, force);
				start = start.next();
				force = Math.max(0.5, force + (Math.random() - 0.5) * 0.5);
				
				if (i == 100) {
					for (int j=i; j<150; j++){
						s1.add(start, 0.0);
						start = start.next();
					}
					i=150;
				}
				if (i == 200) {
					for (int j=i; j<250; j++){
						s1.add(start, 0.0);
						start = start.next();
					}
					i=250;
				}
			}
			dataset.addSeries(s1);
			return result = dataset;
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

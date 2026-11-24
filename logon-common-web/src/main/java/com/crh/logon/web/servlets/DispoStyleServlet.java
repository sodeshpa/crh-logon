/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.web.utils.ConfigBeanProvider;

/**
 * Simple servlet to generate and serve the dispoColors.css for Truck Scheduling screen.
 * This CSS file containes the customizable colors form <code>tlgnr_config<code> table. 
 * 
 * @author AdNovum Informatik AG
 */
public class DispoStyleServlet extends LogonServlet {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BestellungChartServlet.class);

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
			final ConfigBean configBean = ConfigBeanProvider.getConfigBean();
			
			StringBuilder sb = new StringBuilder();
			
			// Error
			appendCssClass(sb, "unloadEr", configBean.getAppDefaultDispoColorUnloadEr());
			appendCssClass(sb, "toEr", configBean.getAppDefaultDispoColorToEr());

			// Planned
			appendCssClass(sb, "loadPl", configBean.getAppDefaultDispoColorLoadPl());
			appendCssClass(sb, "toPl", configBean.getAppDefaultDispoColorToPl());
			appendCssClass(sb, "unloadPl", configBean.getAppDefaultDispoColorUnloadPl());
			appendCssClass(sb, "fromPl", configBean.getAppDefaultDispoColorFromPl());
			appendCssClass(sb, "loadPlExt", configBean.getAppDefaultDispoColorLoadPlExt());
			appendCssClass(sb, "toPlExt", configBean.getAppDefaultDispoColorToPlExt());
			appendCssClass(sb, "unloadPlExt", configBean.getAppDefaultDispoColorUnloadPlExt());
			appendCssClass(sb, "fromPlExt", configBean.getAppDefaultDispoColorFromPlExt());
			
			// Transmitted
			appendCssClass(sb, "loadTr", configBean.getAppDefaultDispoColorLoadTr());
			appendCssClass(sb, "toTr", configBean.getAppDefaultDispoColorToTr());
			appendCssClass(sb, "unloadTr", configBean.getAppDefaultDispoColorUnloadTr());
			appendCssClass(sb, "fromTr", configBean.getAppDefaultDispoColorFromTr());
			appendCssClass(sb, "loadTrExt", configBean.getAppDefaultDispoColorLoadTrExt());
			appendCssClass(sb, "toTrExt", configBean.getAppDefaultDispoColorToTrExt());
			appendCssClass(sb, "unloadTrExt", configBean.getAppDefaultDispoColorUnloadTrExt());
			appendCssClass(sb, "fromTrExt", configBean.getAppDefaultDispoColorFromTrExt());
			
			// Confirmed
			appendCssClass(sb, "loadCf", configBean.getAppDefaultDispoColorLoadCf());
			appendCssClass(sb, "toCf", configBean.getAppDefaultDispoColorToCf());
			appendCssClass(sb, "unloadCf", configBean.getAppDefaultDispoColorUnloadCf());
			appendCssClass(sb, "fromCf", configBean.getAppDefaultDispoColorFromCf());
			appendCssClass(sb, "loadCfExt", configBean.getAppDefaultDispoColorLoadCfExt());
			appendCssClass(sb, "toCfExt", configBean.getAppDefaultDispoColorToCfExt());
			appendCssClass(sb, "unloadCfExt", configBean.getAppDefaultDispoColorUnloadCfExt());
			appendCssClass(sb, "fromCfExt", configBean.getAppDefaultDispoColorFromCfExt());
			
			// On-time order
			appendCssClass(sb, "loadOtPl", configBean.getAppDefaultDispoColorLoadOtPl());
			appendCssClass(sb, "toOtPl", configBean.getAppDefaultDispoColorToOtPl());
			appendCssClass(sb, "unloadOtPl", configBean.getAppDefaultDispoColorUnloadOtPl());
			appendCssClass(sb, "fromOtPl", configBean.getAppDefaultDispoColorFromOtPl());
			appendCssClass(sb, "loadOtTr", configBean.getAppDefaultDispoColorLoadOtTr());
			appendCssClass(sb, "toOtTr", configBean.getAppDefaultDispoColorToOtTr());
			appendCssClass(sb, "unloadOtTr", configBean.getAppDefaultDispoColorUnloadOtTr());
			appendCssClass(sb, "fromOtTr", configBean.getAppDefaultDispoColorFromOtTr());
			appendCssClass(sb, "loadOtCf", configBean.getAppDefaultDispoColorLoadOtCf());
			appendCssClass(sb, "toOtCf", configBean.getAppDefaultDispoColorToOtCf());
			appendCssClass(sb, "unloadOtCf", configBean.getAppDefaultDispoColorUnloadOtCf());
			appendCssClass(sb, "fromOtCf", configBean.getAppDefaultDispoColorFromOtCf());
			appendCssClass(sb, "loadOtAs", configBean.getAppDefaultDispoColorLoadOtAs());
			appendCssClass(sb, "toOtAs", configBean.getAppDefaultDispoColorToOtAs());
			appendCssClass(sb, "unloadOtAs", configBean.getAppDefaultDispoColorUnloadOtAs());
			appendCssClass(sb, "fromOtAs", configBean.getAppDefaultDispoColorFromOtAs());
			
			// External locked
			appendCssClass(sb, "loadExtLocked", configBean.getAppDefaultDispoColorLoadExtLocked());
			appendCssClass(sb, "toExtLocked", configBean.getAppDefaultDispoColorToExtLocked());
			appendCssClass(sb, "unloadExtLocked", configBean.getAppDefaultDispoColorUnloadExtLocked());
			appendCssClass(sb, "fromExtLocked", configBean.getAppDefaultDispoColorFromExtLocked());
			
			// On hold order
			appendCssClass(sb, "loadOnHold", configBean.getAppDefaultDispoColorLoadOnHold());
			appendCssClass(sb, "toOnHold", configBean.getAppDefaultDispoColorToOnHold());
			appendCssClass(sb, "unloadOnHold", configBean.getAppDefaultDispoColorUnloadOnHold());
			appendCssClass(sb, "fromOnHold", configBean.getAppDefaultDispoColorFromOnHold());
			
			// Locked
			appendCssClass(sb, "loadLo", configBean.getAppDefaultDispoColorLoadLo());
			appendCssClass(sb, "toLo", configBean.getAppDefaultDispoColorToLo());
			appendCssClass(sb, "unloadLo", configBean.getAppDefaultDispoColorUnloadLo());
			appendCssClass(sb, "fromLo", configBean.getAppDefaultDispoColorFromLo());
			
			// Prescheduled
			appendCssClass(sb, "loadPrescheduled", configBean.getAppDefaultDispoColorLoadPrescheduled());
			appendCssClass(sb, "fromPrescheduled", configBean.getAppDefaultDispoColorFromPrescheduled());
			appendCssClass(sb, "toPrescheduled", configBean.getAppDefaultDispoColorToPrescheduled());
			appendCssClass(sb, "unloadPrescheduled", configBean.getAppDefaultDispoColorUnloadPrescheduled());
			
			PrintWriter out = new PrintWriter(resp.getOutputStream());
			resp.setContentType("text/css");
			resp.addHeader("Cache-Control", "private, max-age=43200");
			out.println(sb.toString());
			out.flush();
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
	
	/**
	 * Appends one rule to the StringBuilder with the given classname and backgroundColor.
	 * 
	 * @param sb A (not null) StringBuilder instance to append the CSS rule String to.
	 * @param className The CSS class to use in the rule. 
	 * @param backgroundColor The background color code for the given class.
	 * 
	 * @return The StringBuilder instance.
	 */
	private StringBuilder appendCssClass(StringBuilder sb, String className, String backgroundColor) {

		if (sb != null) {
			sb.append("div.").append(className).append(" {\n\tbackground-color: ").append(
					backgroundColor).append(";\n}\n");
		}

		return sb;
	}

}

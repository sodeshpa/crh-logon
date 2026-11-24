/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.truckadmin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TruckRouteService;
import com.crh.logon.persistence.entities.views.TruckRouteSearchEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_TruckRouteForm"
 *  path="/truckadmin/TruckRoute" scope="session"
 *  validate="false" input="/truckadmin/TruckRoute.def"
 *
 * @struts.action-forward name="success" path="/truckadmin/TruckRoute.def"
 */
public class TruckRouteAction extends ActionSupport {
	
	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(TruckRouteAction.class);
	
	private static final char CSV_SEPERATOR_CHAR = ';';

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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R);
			
			String forwardName = "success";
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("truckroute", request);
			TruckRouteService truckRouteService = getServiceLocator().getTruckRouteService();
			
			if (request.getParameter("export") != null) {
				Map<String, String> criteria = new HashMap<String, String>();
				if (request.getParameter(TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID) != null) {
					criteria.put(TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID, request.getParameter(TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID));
				}
				if (request.getParameter(TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID) != null) {
					criteria.put(TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID, request.getParameter(TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID));
				}
				String sortBy = TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_NAME;
				boolean sortAsc = true;
				if ( (request.getParameter("sortField") != null) && (request.getParameter("sortDirection") != null) ) {
					sortBy = request.getParameter("sortField");
					sortAsc = "ASC".equals(request.getParameter("sortDirection"));
				}
				List<TruckRouteSearchEntity> truckRouteSearchEntityList = truckRouteService.searchTruckRoutes(criteria, sortBy, sortAsc);
				
				response.setContentType("text");
				response.setHeader("Content-disposition", "attachment;filename=truckroutes.csv");
				writeExportFile(response.getWriter(), truckRouteSearchEntityList);
			
				return result = null; 
			}
			
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


	private String getCSVValue(Object value, boolean appendSeperator) {
		
		StringBuilder sb = new StringBuilder();
		if (value != null) {
			sb.append('"');
			sb.append(value);
			sb.append('"');
		}
		if (appendSeperator) {
			sb.append(CSV_SEPERATOR_CHAR);
		}
		
		return sb.toString();
	}


	private void writeExportFile(Writer writer, List<TruckRouteSearchEntity> truckRouteList) {
		
		BufferedWriter bufWriter = new BufferedWriter(writer);
		for (TruckRouteSearchEntity truckRouteSearchEntity : truckRouteList) {
			StringBuilder line = new StringBuilder();
			line.append(getCSVValue(truckRouteSearchEntity.getStartShippingpointName(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getEndShiptoBpSapId(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getEndShiptoBpName1(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getEndShiptoBpPostalCode(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getEndShiptoBpCity(), true));
			line.append(getCSVValue(null, true)); // maintain column positions
			line.append(getCSVValue(null, true)); // maintain column positions
			line.append(getCSVValue(truckRouteSearchEntity.getTimeCalculatedMin(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getTimeCorrectionFactor(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getTimePlanned(), true));
			line.append(getCSVValue(null, true)); // maintain column positions
			line.append(getCSVValue(truckRouteSearchEntity.getDistanceCalculatedM(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getDistanceCorrectionFactor(), true));
			line.append(getCSVValue(truckRouteSearchEntity.getCorrectedDistance(), true));
			line.append(getCSVValue(null, true)); // maintain column positions
			line.append(getCSVValue(null, true)); // maintain column positions
			line.append(getCSVValue(truckRouteSearchEntity.getRouteComment(), false));
			try {
				bufWriter.write(line.toString());
				bufWriter.newLine();
			}
			catch (IOException e) {
				throw new LogonException(e);
			}
		}
		try {
			bufWriter.flush();
		}
		catch (IOException e) {
		}
	}
}

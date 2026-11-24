/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.WagonSearchForm;
import com.crh.logon.business.services.utils.TimeZone;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.reporting.SqlDataExportListener;
import com.crh.logon.business.services.reporting.SqlDataExporter;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.views.WagonSearchEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_WagonSearchForm" path="/WagonSearch" scope="session"
 *  validate="true" input="WagonSearchTile.def"
 *
 * @struts.action-forward name="input" path="WagonSearchTile.def"
 */
public class WagonSearchAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonSearchAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
				Authorization.authorize(Authorization.MASTER_DATA_RAIL_CARS);
				WagonSearchForm helpForm;
				String forwardName = "input";
				// Get the struts form.
				helpForm = (WagonSearchForm)form;
				final Locale userLocale = request.getLocale();
				
				// Check if cancel button clicked.
				if (isCancelled(request)) {
					helpForm.clear();
				}
				else if (request.getParameter("export") != null) {
					setDLHeaders(request, response, null, "wagon_export_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
					SqlDataExporter exporter = new SqlDataExporter(getServiceLocator());
					exporter.addExportListener(new SqlDataExportListener() {
						public String[] exportHeader(String[] columnNames,
								Locale locale) {
							
							return columnNames;
						}
				
						public void exportRow(String[] columnNames,
								Object[] rowData, Map data, Locale locale, TimeZone timezone) {
							Object arrLastKnownDat = data.get("ARRIVAL_LAST_KNOWN_STATION_DAT");
							if (arrLastKnownDat != null) {
								data.put("ARRIVAL_LAST_KNOWN_STATION_DAT", timezone.fmtAsDayMonthYear((Date)arrLastKnownDat));
							}
							Object arrLastKnownTime = data.get("ARRIVAL_LAST_KNOWN_STATION_TIM");
							if (arrLastKnownTime != null) {
								data.put("ARRIVAL_LAST_KNOWN_STATION_TIM", timezone.fmtAsHourMinute((Date)arrLastKnownTime));
							}
						}
						
					});
					
					List qParams = new ArrayList();
					StringBuilder whereClause = null;
					if (request.getParameter(WagonSearchEntity.ATTR_WAGON_TYPE_CD) != null) {
						String[] attrs = request.getParameter(WagonSearchEntity.ATTR_WAGON_TYPE_CD).split(",");
						whereClause = whereClause!=null?whereClause.append(" AND "):new StringBuilder();
						whereClause.append(" WAGON_TYPE_CD IN (");
						for (int i=0;i<attrs.length;i++) {
							whereClause.append("?");
							if (i < attrs.length-1) {
								whereClause.append(",");	
							}
							qParams.add(Long.parseLong(attrs[i]));
						}
						whereClause.append(")");
					}
					if (request.getParameter(WagonSearchEntity.ATTR_WAGON_NO) != null) {
						qParams.add("%" + request.getParameter(WagonSearchEntity.ATTR_WAGON_NO) + "%");
						whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
						whereClause.append(" WAGON_NO LIKE ?");
					}
					if (request.getParameter(WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID) != null) {
						qParams.add(Long.parseLong(request.getParameter(WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID)));
						whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
						whereClause.append(" LAST_KNOWN_STATION_ID = ?");
					}
					if (request.getParameter(WagonSearchEntity.ATTR_WAGON_KEEPER_ID) != null) {
						qParams.add(Long.parseLong(request.getParameter(WagonSearchEntity.ATTR_WAGON_KEEPER_ID)));
						whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
						whereClause.append(" WAGON_KEEPER_ID = ?");
					}
					if (request.getParameter(WagonSearchEntity.ATTR_ORDER_STATUS_CD) != null) {
						String[] attrs = request.getParameter(WagonSearchEntity.ATTR_ORDER_STATUS_CD).split(",");
						
						List<OrderStatusTxEntity> orderStatusList = getServiceLocator().getWagonSearchService().getOrderStatusList(userLocale);
						
						if (attrs.length != orderStatusList.size()) {
							
							whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
							whereClause.append(" ORDER_STATUS_CD IN (");
							for (int i=0;i<attrs.length;i++) {
								whereClause.append("?");
								if (i < attrs.length-1) {
									whereClause.append(",");	
								}
								qParams.add(Long.parseLong(attrs[i]));
							}
							whereClause.append(")");
						}
					}
					if (request.getParameter(WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD) != null) {
						String[] attrs = request.getParameter(WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD).split(",");
						
						List<DistributionDestTxEntity> distributionDesctList = getRefDataGenericsService().findTxEntityList(DistributionDestTxEntity.class, userLocale);
						
						if ( attrs.length != distributionDesctList.size() ) {
							
							whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
							whereClause.append(" DISTRIBUTION_DEST_CD IN (");
							for (int i=0;i<attrs.length;i++) {
								whereClause.append("?");
								if (i < attrs.length-1) {
									whereClause.append(",");	
								}
								qParams.add(attrs[i]);
							}
							whereClause.append(")");
						}
						
					}
					if (request.getParameter(WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID) != null) {
						qParams.add(Long.parseLong(request.getParameter(WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID)));
						whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
						whereClause.append(" DEST_RAILWAY_STATION_ID = ?");
					}
					if (request.getParameter(WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD) != null) {
						String[] attrs = request.getParameter(WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD).split(",");
						whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
						if ( (request.getParameter(WagonSearchEntity.ATTR_ACTIVE_FLAG) != null) && (Boolean.parseBoolean(request.getParameter(WagonSearchEntity.ATTR_ACTIVE_FLAG))) )  {
							whereClause.append(" ( (ACTIVE_FLAG = ?) OR");
							qParams.add(Boolean.TRUE);
						}
						else {
							whereClause.append(" ( (ACTIVE_FLAG = ?) AND");
							qParams.add(Boolean.FALSE);
						}
						whereClause.append(" WAGON_INACTIVE_REASON_CD IN (");
						for (int i=0;i<attrs.length;i++) {
							whereClause.append("?");
							if (i < attrs.length-1) {
								whereClause.append(",");	
							}
							qParams.add(attrs[i]);
						}
						whereClause.append(") )");
					}
					else if (request.getParameter(WagonSearchEntity.ATTR_ACTIVE_FLAG) != null) {
						qParams.add(request.getParameter(WagonSearchEntity.ATTR_ACTIVE_FLAG).equalsIgnoreCase("true")?new Integer(1):new Integer(0));
						whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
						whereClause.append(" ACTIVE_FLAG = ?");
					}
					whereClause = whereClause!=null?whereClause.append(" AND"):new StringBuilder();
					whereClause.append(" LANG_CD = ?");
					qParams.add(userLocale.getLanguage());
					exporter.exportSqlData(response.getWriter(), "SELECT " +
							"WAGON_NO, WAGON_TYPE_NAME, ACTIVE_FLAG, WAGON_INACTIVE_REASON_TXT, WAGON_KEEPER_NAME, WAGON_NOTE_DISPOSITION, WAGON_NOTE_RAILWAY_SPEDITION, RETURN_SHIPTO_NAME1, RETURN_STATION_NO, RETURN_STATION_NAME, LAST_KNOWN_STATION_NO, LAST_KNOWN_STATION_NAME, ARRIVAL_LAST_KNOWN_STATION_DAT, ARRIVAL_LAST_KNOWN_STATION_TIM, WAGON_IDLE_TIME_MIN, PLANT_TRACK_TXT, CURRENT_TRACK_POSITION_NO, MAX_GROSS_WEIGHT_T, TARE_WEIGHT_T, VOLUME_M3, LIMIT_CREDIT_NOTE_T, ORDER_NUMBER, ORDER_ITEM_NO, ORDER_STATUS_TXT, DISTRIBUTION_DEST_CD, DISTRIBUTION_DEST_TXT, SOLDTO_SAP_ID, SOLDTO_NAME1, SHIPTO_SAP_ID, SHIPTO_NAME1, DEST_RAILWAY_STATION_NO, DEST_RAILWAY_STATION_NAME, SAP_MATERIAL_NO, SAP_MATERIAL_TXT, LOADED_QUANTITY, LOADED_UOM, LOADING_LIMIT_A_T, LOADING_LIMIT_B_T, LOADING_LIMIT_C_T, LOADING_LIMIT_D_T, WHEELBASE_MM, WHEELBASE_BPP_MM, BUFFER_LENGTH_MM, UNDERCARRIAGE_MANUFACTURER, UNDERCARRIAGE_CONST_YEAR, SUPERSTRUCTURE_MANUFACTURER, SUPERSTRUCTURE_CONST_YEAR, UNLOADING_SYSTEM, DRAWING_TYPE, BRAKE_TYPE, BRAKE_BLOCKS_TYPE, BRAKE_BLOCKS_COUNT, BRAKE_CYLINDER_NO, BRAKE_RIGGING_NO, BRAKE_HOSEPIPE_NO, BRAKE_HOSEPIPE_NBS, BRAKE_HOSEPIPE_BS, MECHANICAL_LOAD_CHANGE, AIR_SHUT_OFF_COCK_TYPE, TRIPLE_VALVE_NO, TRIPLE_VALVE_TYPE, WEIGHING_VALVE_NO, WEIGHING_VALVE_TYPE, PRESSURE_EXCHANGER_TYPE, PRESSURE_EXCHANGER_NO, BUFFER_TYPE, BUFFER_CONST_YEAR, BOGIE_TYPE, BOGIE_NO, SPRING_TYPE, WHEELSET_1_NBS_NO, WHEELSET_1_NBS_BUILD, WHEELSET_1_NBS_DIAMETER_MM, WHEELSET_1_NBS_YEAR, WHEELSET_2_NBS_NO, WHEELSET_2_NBS_BUILD, WHEELSET_2_NBS_DIAMETER_MM, WHEELSET_2_NBS_YEAR, WHEELSET_3_BS_NO, WHEELSET_3_BS_BUILD, WHEELSET_3_BS_DIAMETER_MM, WHEELSET_3_BS_YEAR, WHEELSET_4_BS_NO, WHEELSET_4_BS_BUILD, WHEELSET_4_BS_DIAMETER_MM, WHEELSET_4_BS_YEAR, MAINT_RHYTHM_TYPE_CD, MAINT_G10_MAIN_TYPE_TXT, MAINT_G10_LAST_DAT, MAINT_G10_NEXT_DAT, MAINT_G10_SHIPTO, MAINT_G20_MAIN_TYPE_TXT, MAINT_G20_LAST_DAT, MAINT_G20_NEXT_DAT, MAINT_G20_SHIPTO, MAINT_G30_MAIN_TYPE_TXT, MAINT_G30_LAST_DAT, MAINT_G30_NEXT_DAT, MAINT_G30_SHIPTO, MAINT_G40_MAIN_TYPE_TXT, MAINT_G40_LAST_DAT, MAINT_G40_NEXT_DAT, MAINT_G40_SHIPTO, MAINT_G42_MAIN_TYPE_TXT, MAINT_G42_LAST_DAT, MAINT_G42_NEXT_DAT, MAINT_G42_SHIPTO, MAINT_G43_MAIN_TYPE_TXT, MAINT_G43_LAST_DAT, MAINT_G43_NEXT_DAT, MAINT_G43_SHIPTO, MAINT_G48_MAIN_TYPE_TXT, MAINT_G48_LAST_DAT, MAINT_G48_NEXT_DAT, MAINT_G48_SHIPTO, MAINT_P3M_MAIN_TYPE_TXT, MAINT_P3M_LAST_DAT, MAINT_P3M_NEXT_DAT, MAINT_P3M_SHIPTO, MAINT_P5_MAIN_TYPE_TXT, MAINT_P5_LAST_DAT, MAINT_P5_NEXT_DAT, MAINT_P5_SHIPTO, MAINT_C_MAIN_TYPE_TXT, MAINT_C_LAST_DAT, MAINT_C_NEXT_DAT, MAINT_C_SHIPTO " +
							"FROM " + Constant.SCHEMA_USER_PREPEND + "VLGNC_WAGON_SEARCH WHERE" + whereClause, qParams.toArray(),
										   userLocale,
										   getUserTimezone());
					
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
}

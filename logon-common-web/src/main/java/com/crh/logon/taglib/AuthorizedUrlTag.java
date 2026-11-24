/*
 * Author			: AdNovum Informatik AG
 * Version Number	: $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 */
public class AuthorizedUrlTag extends TagSupport {
	
	/**
	 * This map contains the urls of the related functionalities. Not all Authorization functionality codes have to be
	 * added here, only those, which are present on the navigation tile tabs, and we use it on the navigation tile.
	 * 
	 * Idea of the tag: a first level tab must be visible on the navigation tile,if the user has access at least to one sub
	 * tab of that. The authorization tag is designed to render the body of the tag if one has the right to see it, and therefore it will
	 * show the first level tab, in case any of the listed functionalities is available for the user.
	 * The question is to what url will the user be directed in case he clicks on the first level tab, 
	 * it is not ok, if we just hardcode any tab's url to the first level tab in case it is visible, because the user 
	 * might not have access to that, and he only will get the 'you are not authorized to access this page' message.
	 * We rather render there the url of the page which is first authorized to the user, the tag is supposed to return that url.
	 *
	 * See description of the tag in common-taglib.tld:
	 * Only valid WITHIN an AuthorizationTag. Returns the url of the functionality, which is first authorized
	 * for the user. Has a role if the url has to be set according to the first accessible functionality.
	 */
	private static HashMap functionToTabMapping = new HashMap();
	
	static {
		functionToTabMapping.put(Authorization.ORDER , "order/bestellung/BestellungShipToEntry.do"); // Order
		functionToTabMapping.put(Authorization.POOL_ASSIGNMENT , "order/auftrag/PoolAssignmentEntry.do"); // Pool Assignment
		functionToTabMapping.put(Authorization.WEB_ORDER , "cust/webbestellung/WebBestellungEntry.do"); // Web Order
		functionToTabMapping.put(Authorization.BW_INSTANDHALTUNG , "order/railrepair/RailRepairEntry.do");
		functionToTabMapping.put(Authorization.DISPATCHING_R , "order/dispo/DispoEntry.do"); // Dispatching: read access
		functionToTabMapping.put(Authorization.DISPATCHING_W , "order/dispo/DispoEntry.do"); // Dispatching: write access
		functionToTabMapping.put(Authorization.SIMPLE_VEHICLE_DISPATCHING_R , "order/simpledispo/SimpleDispoEntry.do"); // Simple Vehicle Dispatching: read access
		functionToTabMapping.put(Authorization.SIMPLE_VEHICLE_DISPATCHING_W , "order/sinmledispo/SimpleDispoEntry.do"); // Simple Vehicle Dispatching: write access
		functionToTabMapping.put(Authorization.DISPATCHING_POOL_W , "order/dispo/DispoEntry.do"); // Dispatching: write access for pool management
		functionToTabMapping.put(Authorization.SHIPPING_R , "order/auftrag/AuftragBearbeitungEntry.do"); // Shipping: read access
		functionToTabMapping.put(Authorization.SHIPPING_W , "order/auftrag/AuftragBearbeitungEntry.do"); // Shipping: write access
		functionToTabMapping.put(Authorization.RAIL_DISPATCHING_R , "order/raildispo/DispoEntry.do"); // Rail Dispatch: read access
		functionToTabMapping.put(Authorization.RAIL_DISPATCHING_W , "order/raildispo/DispoEntry.do"); // Rail Dispatch: write access
		functionToTabMapping.put(Authorization.RAILCARS_R , ""); // Railcars: read access (except Freimelden)
		functionToTabMapping.put(Authorization.RAILCARS_FREIMELDEN , ""); // Railcars Freimelden
		functionToTabMapping.put(Authorization.RAILCARS_W , ""); // Railcars: write access
		functionToTabMapping.put(Authorization.RAILCARS_RETURN_R , ""); // Railcars Return: read access
		functionToTabMapping.put(Authorization.RAILCARS_RETURN_W , ""); // Railcars Return: write access
		functionToTabMapping.put(Authorization.RAILCARS_VIEW_1 , ""); // Railcars View 1
		functionToTabMapping.put(Authorization.RAILCARS_VIEW_2 , ""); // Railcars View 2
		functionToTabMapping.put(Authorization.RAILCARS_VIEW_3 , ""); // Railcars View 3
		functionToTabMapping.put(Authorization.TRAIN_STATIONS_R , ""); // Train Stations: read access
		functionToTabMapping.put(Authorization.TRAIN_STATIONS_W , ""); // Train Stations: write access
		functionToTabMapping.put(Authorization.BLOCK_TRAINS_R , ""); // Block trains: read access
		functionToTabMapping.put(Authorization.BLOCK_TRAINS_W , ""); // Block trains: write access
		functionToTabMapping.put(Authorization.RAIL_MAINTENANCE , ""); // Rail Maintenance
		functionToTabMapping.put(Authorization.ORDER_LIST , "order/bestellung/BestellungSearchEntry.do"); // Order List
		functionToTabMapping.put(Authorization.ORDER_VIEW_2 , "order/creditnote/CreditNoteEntry.do"); // Order View 2
		functionToTabMapping.put(Authorization.ORDER_VIEW_3_MONITORING , "order/bestellung/BestellungMonitoringEntry.do"); // Order View 3 Monitoring
		functionToTabMapping.put(Authorization.ORDER_VIEW_4 , "order/bestellung/TruckPlanningEntry.do"); // Order View 4
		functionToTabMapping.put(Authorization.EXTERNAL_ORDER_LIST, "order/bestellung/BestellungExternalSearchEntry.do");
		functionToTabMapping.put(Authorization.LIVE_MAP , "order/auftrag/LiveKarteEntry.do"); // Live Map
		functionToTabMapping.put(Authorization.REPORTING_MAP , ""); // Reporting Map
		functionToTabMapping.put(Authorization.REPORT_ORDER_DATA_PER_CARRIER , "order/reporting/ReportingEntry.do");
		functionToTabMapping.put(Authorization.REPORT_ORDER_DATA_PER_CUSTOMER , "order/reporting/ReportingEntry.do");
		functionToTabMapping.put(Authorization.REPORT_ORDER_LIST , "order/reporting/ReportingEntry.do");
		functionToTabMapping.put(Authorization.REPORT_SAP_BW_EXPORT , "order/reporting/ReportingEntry.do");
		functionToTabMapping.put(Authorization.REPORT_DEFINITION , ""); // Report Definition
		functionToTabMapping.put(Authorization.MASTER_DATA_SHIPTO_CARD_R , "order/dispokarte/DispokarteShipToEntry.do"); // Master Data: Shipto Card: read access
		functionToTabMapping.put(Authorization.MASTER_DATA_SHIPTO_CARD_W , "order/dispokarte/DispokarteShipToEntry.do"); // Master Data: Shipto Card: write access
		functionToTabMapping.put(Authorization.MASTER_DATA_CARRIER_ASSIGNMT_SHIPTO_PROD , "admin/carrierassignment/CarrierAssignmentEntry.do"); // Master Data: Carrier assignment to Shipto and Products
		functionToTabMapping.put(Authorization.MASTER_DATA_RFID_MAINTENANCE , "admin/rfidadmin/RFIDAdminEntry.do"); // Master Data: RFID Maintenance
		functionToTabMapping.put(Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG , "admin/ZoneMapEntry.do"); // Master Data: Global Zonemap Bag
		functionToTabMapping.put(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT , "admin/ShippingPointEntry.do"); // Master Data: Global Shippingpoint (including Loadingpoint)
		functionToTabMapping.put(Authorization.MASTER_DATA_GLOBAL_MATERIAL , "admin/MaterialEnhancementsEntry.do"); // Master Data: Global Material (only enhancements, chromat.)
		functionToTabMapping.put(Authorization.MASTER_DATA_GLOBAL_SALES_AREA , "admin/SalesAreaEntry.do"); // Master Data: Configuration in LOGON for Multiple Opcos per Instance
		functionToTabMapping.put(Authorization.MASTER_DATA_GLOBAL_PRODUCT_CHANGE_MATRIX , "admin/ProductChangeMatrixViewEntry.do"); // Master Data: Product Change Matrix View
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_POOL , "admin/truckadmin/PoolAdminEntry.do"); // Master Data: Road Pool
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_TRUCK , "admin/truckadmin/TruckAdminEntry.do"); // Master Data: Road Truck (except for truck creation)
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE , "admin/truckadmin/TruckAdminEntry.do"); // Master Data: Road Truck Update
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_TRAILER , "admin/traileradmin/TrailerAdminEntry.do"); // Master Data: Road Trailer (except for trailer creation)
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE , "admin/traileradmin/TrailerAdminEntry.do"); // Master Data: Road Trailer Update
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_CONTAINER , "admin/containeradmin/ContainerAdminEntry.do"); // Master Data: Road Container (except for container creation)
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_CONTAINER_CREATE , "admin/containeradmin/ContainerAdminEntry.do"); // Master Data: Road Container Creation
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE , "admin/simplevehicleadmin/SimpleVehicleAdminEntry.do"); // Master Data: Road Container (except for container creation)
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE , "admin/simplevehicleadmin/SimpleVehicleAdminEntry.do"); // Master Data: Road Container Creation
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R , "admin/truckadmin/TruckRouteEntryAction.do"); // Master Data: Road Truck Route: read access
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_W , "admin/truckadmin/TruckRouteEntryAction.do"); // Master Data: Road Truck Route: write access
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_EXT_MAT_R , "admin/externalmaterialadmin/ExternalMaterialAdminEntry.do"); // External Material Admin: read access
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_EXT_MAT_W , "admin/externalmaterialadmin/ExternalMaterialAdminEntry.do"); // External Material Admin: write access
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_EXT_SITE_R , "admin/externalsiteadmin/ExternalSiteAdminEntry.do"); // External Site Admin: read access
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_EXT_SITE_W , "admin/externalsiteadmin/ExternalSiteAdminEntry.do"); // External Site Admin: write access
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_SILO , "admin//siloadmin/SiloAdminEntry.do"); // Master Data: Road Silo (except for silo creation)
		functionToTabMapping.put(Authorization.MASTER_DATA_ROAD_SILO_CREATE , "admin/siloadmin/SiloAdminEntry.do"); // Master Data: Road Silo Creation
		functionToTabMapping.put(Authorization.MASTER_DATA_RAIL_CARS , "admin/WagonSearchEntry.do"); // Master Data: Rail Cars, including upload
		functionToTabMapping.put(Authorization.MASTER_DATA_RAIL_TIMETABLE , "admin/RailTimetableEntry.do"); // Master Data: Rail Timetable, including upload
		functionToTabMapping.put(Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE , "admin/RailTimetableEntry.do"); // Master Data: Rail Timetable, including upload
		functionToTabMapping.put(Authorization.MASTER_DATA_DISPOCARD_UPDATE_VIEW , "admin/DispocardViewEntry.do"); // Master Data: Dispocard update view
		functionToTabMapping.put(Authorization.MASTER_DATA_DISPOCARD_UPDATE_EXPORT , "admin/rfidadmin/RFIDAdminEntry.do"); // Master Data: Dispocard update export
		functionToTabMapping.put(Authorization.MASTER_DATA_CARRIER_VIEW , "admin/CarrierEntry.do"); // Master Data: Carrier view
		functionToTabMapping.put(Authorization.USER_ADMIN_LOGON , "admin/useradmin/UserAdminEntry.do"); // User admin LOGON (intern)
		functionToTabMapping.put(Authorization.USER_ADMIN_LOGON_EXTERN , "admin/useradmin/UserAdminExternalEntry.do"); // User admin LOGON (extern)
		functionToTabMapping.put(Authorization.USER_ADMIN_PDA , "admin/pdaadmin/PDAAdminEntry.do"); // User Admin PDA
		functionToTabMapping.put(Authorization.TABLE_BROWSER , "admin/tablebrowser/TableViewEntry.do"); // Table Browser
		functionToTabMapping.put(Authorization.ORDER_DETAIL_R , ""); // Order Detail (Main): read access
		functionToTabMapping.put(Authorization.ORDER_DETAIL_W , ""); // Order Detail (Main): write access (except for delivery date/time)
		functionToTabMapping.put(Authorization.ORDER_DETAIL_DELIVERY_DATE_TIME , ""); // Order Detail (Main): write access for delivery date/time)
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MATERIAL_R , ""); // Order Detail Material: read access
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MATERIAL_W , ""); // Order Detail Material: write access (except for 6 special functions)
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MATERIAL_QUANTITY_UOM , ""); // Order Detail Material: change order quantity and UoM
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MATERIAL_BILLTO_PARTY , ""); // Order Detail Material: change billto party
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO , ""); // Order Detail Material: change customs serial number
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MATERIAL_FORWARDING_AGENT , ""); // Order Detail Material: change forwarding agent
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MATERIAL_DELIVERY_NOTE , ""); // Order Detail Material: change comment add/on delivery note
		functionToTabMapping.put(Authorization.ORDER_DETAIL_MAP , ""); // Order Detail Map
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_R , ""); // Order Detail Report: read access
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_W , ""); // Order Detail Report: write access (exept for 6 special functions)
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG , ""); // Order Detail Report: change service level flag
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CRH , ""); // Order Detail Report: change rating comment crh
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CARR , ""); // Order Detail Report: change rating comment carrier
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_COMMENT_DISPATCHING , ""); // Order Detail Report: change comment dispatching (rail/road)
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_COMMENT_DRIVER , ""); // Order Detail Report: change comment truck driver
		functionToTabMapping.put(Authorization.ORDER_DETAIL_REPORT_COMMENT_SHIPPING , ""); // Order Detail Report: change comment shipping (rail/road)
		functionToTabMapping.put(Authorization.ORDER_DETAIL_ORDER_MANAGEMENT , ""); // Order Detail Order Management
		functionToTabMapping.put(Authorization.ORDER_DETAIL_COMMERCIAL , ""); // Order Detail Commercial
		functionToTabMapping.put(Authorization.ORDER_DETAIL_ORDER_LOG , ""); // Order Detail Order Log
		functionToTabMapping.put(Authorization.ORDER_DETAIL_TRUCK_LOG , ""); // Order Detail Truck Log
		functionToTabMapping.put(Authorization.RAILCAR_DETAIL_BASE_R , ""); // Railcar Detail Base: read access
		functionToTabMapping.put(Authorization.RAILCAR_DETAIL_BASE_W , ""); // Railcar Detail Base: write access
		functionToTabMapping.put(Authorization.RAILCAR_DETAIL_ORDER_R , ""); // Railcar Detail Order: read access
		functionToTabMapping.put(Authorization.RAILCAR_DETAIL_ORDER_W , ""); // Railcar Detail Order: write access
		functionToTabMapping.put(Authorization.TRAIN_STATION_DETAIL_BASE_R , ""); // Train Station Detail Base: read access
		functionToTabMapping.put(Authorization.TRAIN_STATION_DETAIL_BASE_W , ""); // Train Station Detail Base: write access
		functionToTabMapping.put(Authorization.TRAIN_STATION_DETAIL_ORDER_R , ""); // Train Station Detail Order: read access
		functionToTabMapping.put(Authorization.TRAIN_STATION_DETAIL_ORDER_W , ""); // Train Station Detail Order: write access
		functionToTabMapping.put(Authorization.EXTERNAL_ORDERS_SHIPPING , ""); // External orders (Spediteur)
		functionToTabMapping.put(Authorization.F01_UNLOCK_PARALLEL_TERM , ""); // F01 Unlock Parallel Terminal
		functionToTabMapping.put(Authorization.F02_CLOSE_ORDER , ""); // F02 Close order. E.g. railcar availability message
		functionToTabMapping.put(Authorization.F03_ORDERMASTER , ""); // F03 Ordermaster (Order can always be changed)
		functionToTabMapping.put(Authorization.PDA_P01_BULK_BAG_HANDLING , ""); // PDA P01 Bulk/Bag Handling
		functionToTabMapping.put(Authorization.PDA_P02_TERMINAL_FORKLIFT , ""); // PDA P02 Terminal/Forklift
		functionToTabMapping.put(Authorization.PDA_P03_RFID_ADMIN , ""); // PDA P03 RFID-Admin
		functionToTabMapping.put(Authorization.PDA_P04_PDA_ADMIN , ""); // PDA P04 PDA-Admin
		functionToTabMapping.put(Authorization.TEST_PDA , "admin/test/ManualPDATriggerEntry.do"); // Manual Test PDA
		functionToTabMapping.put(Authorization.TEST_WEBSERVICE , "admin/test/ManualWebserviceTriggerEntry.do"); // Manual Test Webservice
		functionToTabMapping.put(Authorization.TEST_VERSIONCHECK , "init/versionCheck"); // Test Version Check
		functionToTabMapping.put(Authorization.TEST_CRONJOBS , "admin/test/CronJobsEntry.do"); // Test Cron Jobs
		functionToTabMapping.put(Authorization.TEST_FAX_SMS , "admin/test/ManualFaxSmsTriggerEntry.do"); // Manual Test Fax/Sms
		functionToTabMapping.put(Authorization.RAIL_CAR_OPTIMIZATION , "order/railcaroptimization/RailCarOptimizationEntry.do"); // Rail car optimization
		functionToTabMapping.put(Authorization.BIMODAL_MONITORING , "order/bimodalrailcarmonitoring/BimodalRailcarMonitoringEntry.do"); // Rail car optimization
		functionToTabMapping.put(Authorization.CONTAINER_MONITORING , "order/containermonitoring/ContainerMonitoringEntry.do"); // Container Monitoring optimization
		functionToTabMapping.put(Authorization.DEMAND_PLANNING , "order/demandplanning/ManagePlanningEntry.do"); // Demand Planning: manage demands
		functionToTabMapping.put(Authorization.DEMAND_PLANNING_REPORT_EXPECTED_DEMAND , "order/demandplanning/ReportExpectedEntry.do"); // Demand Planning: report demands
		functionToTabMapping.put(Authorization.DEMAND_PLANNING_REPORT_PLAN_VS_REAL , "order/demandplanning/ReportExpectedAndRealizedEntry.do"); // Demand
		// Planning: report expected demands and what was realized as orders
	}
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AuthorizedUrlTag.class);

	public int doStartTag() throws JspException {
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doStartTag");
		}
		// ]]
		try {
			Tag tagNotCasted = findAncestorWithClass(this, AuthorizationTag.class);
			AuthorizationTag tag = (AuthorizationTag) tagNotCasted;
			Long authorizedFunction = tag.getAuthorizedFunction();
			String url = "";
			if (authorizedFunction != null) {				
				 Object mapValue = functionToTabMapping.get(authorizedFunction);
				 if (mapValue == null) {
					 LOG.error("Implementation error: Invalid functionality '" + authorizedFunction + "' was given for AuthorizedUrlTag please check the mapping of functionalities");
					 return result = SKIP_BODY;
				 }
				 url = (String) mapValue;
				 if (StringUtils.isEmpty(url)) {
					 LOG.error("Implementation error: No url was given for functionality code: " + authorizedFunction + " please update the functionality - url mapping!");
				 }
			}
			
			JspWriter out = pageContext.getOut();				
			try {
				out.write(url);
			}
			catch (IOException e) {
				LOG.error("Error while trying to print out the first authorized url");
			}
			return	result = SKIP_BODY;
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

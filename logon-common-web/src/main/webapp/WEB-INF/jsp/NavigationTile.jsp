<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: NavigationTile.jsp,v $ --%>

<%@ page session="true" %>

<%@page import="com.crh.logon.web.utils.Authorization" %>
<%@ page import="com.crh.logon.web.utils.ServiceLocatorProvider" %>
<%@ page import="com.crh.logon.utils.AuthUtils" %>

<%@ include file="includeTLDs.jsp" %>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<ul id="navigation">
	<crh-common:authorize function="<%= Authorization.ORDER %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'ccbestellung'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.order"/></span></span></a></li>
	</crh-common:authorize>

	<crh-common:authorize function="<%= Authorization.WEB_ORDER %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'webbestellung'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.webbestellung"/></span></span></a></li>
	</crh-common:authorize>

	<crh-common:authorize function="<%= Authorization.BW_INSTANDHALTUNG %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'railrepair'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.railrepair"/></span></span></a></li>
	</crh-common:authorize>

	<crh-common:authorize function="<%= Authorization.ORDER_LIST %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'orderlist'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.ordersearch"/></span></span></a></li>
	</crh-common:authorize>

	<crh-common:authorize function="<%= Authorization.EXTERNAL_ORDER_LIST %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'extorderlist'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.extordersearch"/></span></span></a></li>
	</crh-common:authorize>
	<crh-common:authorize function="<%= Authorization.DISPATCHING_R %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'dispo'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.dispatching"/></span></span></a></li>
	</crh-common:authorize>
	<crh-common:authorize function="<%= Authorization.SIMPLE_VEHICLE_DISPATCHING_R %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'simpledispo'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.simpledispo"/></span></span></a></li>
	</crh-common:authorize>

	<%-- LKW --%>
	<crh-common:authorize function="<%= Authorization.SHIPPING_R %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>"
			   <c:if test="${ApplicationTabsBean == 'auftragbearbeitung'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.shipping"/></span></span></a></li>
	</crh-common:authorize>

	<crh-common:authorize function="<%= Authorization.RAIL_DISPATCHING_R %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'rail'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.rail"/></span></span></a></li>
	</crh-common:authorize>

	<%-- FIXME: no role for credit note? --%>
	<crh-common:authorize
			function="<%= Authorization.ORDER_VIEW_3_MONITORING %>"
			function2="<%= Authorization.RAIL_CAR_OPTIMIZATION %>"
			function3="<%= Authorization.LIVE_MAP %>"
			function4="<%= Authorization.POOL_ASSIGNMENT %>"
			function5="<%= Authorization.ORDER_VIEW_2 %>"> <!-- TODO add more functions here as sub-pages are implemented -->
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'monitoring'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.monitoring"/></span></span></a></li>
	</crh-common:authorize>

	<crh-common:authorize
			function="<%= Authorization.REPORT_ORDER_DATA_PER_CARRIER %>"
			function2="<%= Authorization.REPORT_ORDER_DATA_PER_CUSTOMER %>"
			function3="<%= Authorization.REPORT_ORDER_LIST %>"
			function4="<%= Authorization.REPORT_SAP_BW_EXPORT %>"
			function5="<%= Authorization.REPORT_ORDER_DATA_PER_ROUTE_CARRIER %>"
			function6="<%= Authorization.REPORT_ORDER_DATA_PER_ROUTE %>"
			function7="<%= Authorization.REPORT_ORDER_DATA_PER_POOL %>"
			function8="<%= Authorization.REPORT_ORDER_DATA_PER_SP_MATPKG %>"
			function9="<%= Authorization.REPORT_ORDER_DATA_PER_PLANT_MAT %>"
			function10="<%= Authorization.REPORT_TRANS_PER_PLANT_DT %>"
			function11="<%= Authorization.REPORT_TRANS_PER_PLANT %>"
			function12="<%= Authorization.REPORT_SALES_PER_CUST_MAT %>"
			function13="<%= Authorization.REPORT_SALES_PER_CUST %>"
			function14="<%= Authorization.REPORT_SALES_PER_MAT %>"
			function15="<%= Authorization.REPORT_ORDER_LIST_CUST %>"
			function16="<%= Authorization.REPORT_ORDER_LIST_CARRIER %>"
			function17="<%= Authorization.REPORT_ORDER_LIST_CRH %>"
			function18="<%= Authorization.REPORT_ORDER_LIST_COMM %>">
		<c:set var="url">/../order/reporting/ReportingEntry.do</c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'reporting'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.reporting"/></span></span></a></li>
	</crh-common:authorize>

		<crh-common:authorize
    						function="<%= Authorization.ITRACK_REPORTING_SERVICE_LEVEL_REPORT %>"
                			function2="<%= Authorization.ITRACK_REPORTING_DISTANCE_REPORT %>"
                			function3="<%= Authorization.ITRACK_REPORTING_TOLL_FEE_REPORT %>">
    		<c:set var="url">/../order/itrack/ITrackReportingEntry.do</c:set>
    		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'itrackreporting'}">class="highlight"</c:if>>
    			<span class="outer"><span class="inner"><fmt:message key="tabitem.itrackreporting"/></span></span></a></li>
    	</crh-common:authorize>

	<crh-common:authorize
			function="<%= Authorization.MASTER_DATA_SHIPTO_CARD_R %>"
			function2="<%= Authorization.MASTER_DATA_SHIPTO_CARD_W %>"
			function3="<%= Authorization.MASTER_DATA_CARRIER_ASSIGNMT_SHIPTO_PROD %>"
			function4="<%= Authorization.MASTER_DATA_RFID_MAINTENANCE %>"
			function5="<%= Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG %>"
			function6="<%= Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT %>"
			function7="<%= Authorization.MASTER_DATA_GLOBAL_PRODUCT_CHANGE_MATRIX %>"
			function8="<%= Authorization.MASTER_DATA_ROAD_POOL %>"
			function9="<%= Authorization.MASTER_DATA_ROAD_TRUCK %>"
			function10="<%= Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE %>"
			function11="<%= Authorization.MASTER_DATA_ROAD_TRAILER %>"
			function12="<%= Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE %>"
			function13="<%= Authorization.MASTER_DATA_ROAD_CONTAINER %>"
			function14="<%= Authorization.MASTER_DATA_ROAD_CONTAINER_CREATE %>"
			function15="<%= Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R %>"
			function16="<%= Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_W %>"
			function17="<%= Authorization.MASTER_DATA_ROAD_SILO %>"
			function18="<%= Authorization.MASTER_DATA_ROAD_SILO_CREATE %>"
			function19="<%= Authorization.MASTER_DATA_RAIL_CARS %>"
			function20="<%= Authorization.MASTER_DATA_RAIL_TIMETABLE %>"
			function21="<%= Authorization.MASTER_DATA_RAIL_CARS %>"
			function22="<%= Authorization.MASTER_DATA_ROAD_EXT_MAT_R %>"
			function23="<%= Authorization.MASTER_DATA_ROAD_EXT_MAT_W %>"
			function24="<%= Authorization.MASTER_DATA_ROAD_EXT_SITE_R %>"
			function25="<%= Authorization.MASTER_DATA_ROAD_EXT_SITE_W %>"
			function26="<%= Authorization.MASTER_DATA_ROAD_PLANNING_TRUCK_CREATE %>"
			function27="<%= Authorization.MASTER_DATA_ROAD_GENERIC_TRUCK_CREATE %>"
			function28="<%= Authorization.MASTER_DATA_ROAD_PLANNING_TRAILER_CREATE %>"
			function29="<%= Authorization.MASTER_DATA_ROAD_GENERIC_TRAILER_CREATE %>"
			function30="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE %>"
			function31="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE %>"
			function32="<%= Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE %>"
	>
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'masterdata'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.masterdata"/></span></span></a></li>
	</crh-common:authorize>

	<%-- Should be visible to everyone because of selfadmin --%>
	<c:set var="url">/../admin/useradmin/SelfAdminEntry.do</c:set>
	<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'admin'}">class="highlight"</c:if>>
		<span class="outer"><span class="inner"><fmt:message key="tabitem.admin"/></span></span></a></li>

	<crh-common:authorize
			function="<%= Authorization.DEMAND_PLANNING %>"
			function2="<%= Authorization.DEMAND_PLANNING_REPORT_EXPECTED_DEMAND %>"
			function3="<%= Authorization.DEMAND_PLANNING_REPORT_PLAN_VS_REAL %>">
		<%
			// not really a nice thing, to include this in the jsp, but couldn't find a nice, common place to keep it
			if (session.getAttribute("demandPlanCount") == null) {
				// manually authorize, after the Authorization check, without this the service call fails
				AuthUtils.setAuthorized(true);
				session.setAttribute("demandPlanCount", ServiceLocatorProvider.getServiceLocator().getDemandPlanningService().countShiptoList(null, ""));
			}
		%>
		<c:if test="${demandPlanCount > 0}">
			<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
			<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'demandplanning'}">class="highlight"</c:if>>
				<span class="outer"><span class="inner"><fmt:message key="tabitem.demandplanning"/></span></span></a></li>
		</c:if>
	</crh-common:authorize>

	<crh-common:authorize
			function="<%= Authorization.TEST_PDA %>"
			function2="<%= Authorization.TEST_VERSIONCHECK %>"
			function3="<%= Authorization.TEST_WEBSERVICE %>"
			function4="<%= Authorization.TEST_CRONJOBS %>"
			function5="<%= Authorization.TEST_FAX_SMS %>">
		<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
		<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationTabsBean == 'test'}">class="highlight"</c:if>>
			<span class="outer"><span class="inner"><fmt:message key="tabitem.test"/></span></span></a></li>
	</crh-common:authorize>
</ul>

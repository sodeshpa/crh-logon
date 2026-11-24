<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<script type="text/javascript">
var gmap;
var AUTO_REFRESH_INTERVAL = <%=ConfigBeanProvider.getConfigBean().getAppDefaultTruckPositionRefreshInterval()%>*1000;
var isOrderItrackAvailable = <c:out value="${order_auftrag_OrderDetailForm.orderITrackAvailable}" />;

GMapAPILoader.onAPIReady(function(){
	loadMap();
	loadStaticMapObjects();
	loadDynamicMapObjects('<c:out value="${order_auftrag_OrderDetailForm.order.orderId}"/>');
});		

Ext.onReady( function() {
	GMapAPILoader.load();
});

function loadMap() {
	var divMap = Ext.getDom('map');

	//set google map
	gmap = new google.maps.Map(divMap, defaultLogonGmapOptions);
    gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
	
	var latlng = new google.maps.LatLng(<%=Constant.MAP_CENTER_LAT%>, <%=Constant.MAP_CENTER_LNG%>);
	gmap.setCenter(latlng)
	gmap.setZoom(<%=Constant.MAP_CENTER_ZOOM_LEVEL%>); 
}	

//define labels for google map
var labelTrailerName 	= '<fmt:message key="livemap.label.trailer.name" />';
var labelTrailerPlateNo = '<fmt:message key="livemap.label.trailer.plateno" />';
var labelCarrier 		= '<fmt:message key="livemap.label.carrier" />';
var labelTruckName 		= '<fmt:message key="livemap.label.truck.name" />';
var labelTruckPlateNo 	= '<fmt:message key="livemap.label.truck.plateno" />';
var labelTruckType 		= '<fmt:message key="livemap.label.truck.type" />';
var labelDriver 		= '<fmt:message key="livemap.label.driver" />';
var labelOrderNumber 	= '<fmt:message key="livemap.label.order.number" />';
var labelOrderStatus 	= '<fmt:message key="livemap.label.order.status" />';
var labelProduct 		= '<fmt:message key="livemap.label.product" />';
var labelShipto 		= '<fmt:message key="livemap.label.shipto" />';
var labelLoadingOrder 	= '<fmt:message key="livemap.label.loading.order" />';
var labelPlantName 		= '<fmt:message key="livemap.label.plant.name" />';
var labelCurrentOrders 	= '<fmt:message key="livemap.label.current.orders" />';
var labelShPoint 		= '<fmt:message key="livemap.label.shpoint" />';
var labelEvent 			= '<fmt:message key="livemap.label.event" />';
var labelEventTime		= '<fmt:message key="livemap.label.eventtime" />';

var dateTimeFormat 		= '<%= DateTimeFormat.getDateTimeFormat4JS(pageContext.getRequest().getLocale()) %>'

//constants
var BIZEVENT_TYPE_SHIPPING = <%=Constant.BIZEVENT_TYPE_SHIPPING%>; 
var BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT = <%=Constant.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT%>; 
var BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT = <%=Constant.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT%>; 
var BIZEVENT_TYPE_LEAVE_SHIPTO = <%=Constant.BIZEVENT_TYPE_LEAVE_SHIPTO%>; 
var BIZEVENT_TYPE_TRAFFIC_JAM_START = <%=Constant.BIZEVENT_TYPE_TRAFFIC_JAM_START%>; 
var BIZEVENT_TYPE_TRAFFIC_JAM_END = <%=Constant.BIZEVENT_TYPE_TRAFFIC_JAM_END%>; 

//data for static objects
function getShippCoordLat() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.shpointCoordLat}"/>';
}
function getShippCoordLon() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.shpointCoordLon}"/>';
}
function getShippName() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.shpointName}"/>';
}
function getPlantCoordLat() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.plantCoordLat}"/>';
}
function getPlantCoordLon() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.plantCoordLon}"/>';
}
function getPlantName() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.plantName}"/>';
}
function getShiptoCoordLat() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.shiptoCoordLat}"/>';
}
function getShiptoCoordLon() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.shiptoCoordLon}"/>';
}
function getShiptoName() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.shiptoName}"/>';
}

function getLoadingTimestamp() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.loadingTimestamp}"/>';
}
function getUnloadingTimestamp() {
	return '<c:out value="${order_auftrag_OrderDetailForm.mapBean.unloadingTimestamp}"/>';
}
</script>


<div id="map" style="width: 99%;height:95%; margin-top: 0; margin-bottom: 10px; overflow:hidden" class="border">map</div>

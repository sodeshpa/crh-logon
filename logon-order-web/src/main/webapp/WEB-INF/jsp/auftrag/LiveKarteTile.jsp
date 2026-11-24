<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LiveKarteTile.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page
	import="com.crh.logon.order.web.actions.auftrag.LiveKarteAction"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%-- allows opening the page in it's own browser window --%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page
	import="com.crh.logon.business.services.beans.VehicleCoordinatesBean"%>
<%
	if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) {
%>

<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<link rel="stylesheet" type="text/css"
	href="<crh-common:url value="/css/crh_style.css"/>">
<jsp:include page="../JsLibTile.jsp"></jsp:include>
<%
	}
%>

<%-- DWR services --%>
<script type="text/javascript"
	src="<crh-common:url value='/dwr/interface/LiveKarteAjaxService.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/js/markermanager.js'/>"></script>
<script type="text/javascript" 
	src="<crh-common:url value='/js/logon-gmap.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value="/js/liveMap.js"/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>

<script type="text/javascript">

	var gmap;
	//GMarkerManager to keep all static objects (Plants, Shipping points, Shipto, etc)
	var mgrStatic = null; 
	//GMarkerManager to keep all dynamic objects (Trucks, Trailers, etc)
	var mgrDynamic = null;
	//keep all static objects and order info from filters
	var staticObjectMap;
	//keep dynamic objects
	var dynamicObjectList;
	var tempDynamicObjectList = null;
	var dynamicObjectListPagesize = Ext.isIE7?1000:100000;
	var bizeventMap = {};
	var truckData = {};
	var trailerData = {};
	var containerData = {};
	var eventData = {};
	var wagonData = {};
	
	// latlan bounds of all visible markers
	var dynamicMarkerBounds = null;
	var staticMarkerBounds = null;
	var refreshTime = <%=Constant.LIVE_MAP_REFRESH_TIME%>; //refresh time
	var dateFormat = "<%= DateTimeFormat.getDateTimeFormat4JS(pageContext.getRequest().getLocale()) %>";
	
	//simple search fields
	var orderMonitor;
	var lastUpdateDat;
	var advancedSearch = false;
	var orderStatusCheckComboBox; 
	var lastUpdateDatField;
	
	//define labels for google map
	var labelTrailerName 	= '<fmt:message key="livemap.label.trailer.name" />';
	var labelContainerName 	= '<fmt:message key="livemap.label.container.name" />';
	var labelWagonName     = '<fmt:message key="livemap.label.wagon.name" />';
	var labelTrailerPlateNo= '<fmt:message key="livemap.label.trailer.plateno" />';
	var labelCarrier 		= '<fmt:message key="livemap.label.carrier" />';
	var labelTruckName 		= '<fmt:message key="livemap.label.truck.name" />';
	var labelTruckPlateNo 	= '<fmt:message key="livemap.label.truck.plateno" />';
	var labelTruckType 		= '<fmt:message key="livemap.label.truck.type" />';
	var labelDriver 		= '<fmt:message key="livemap.label.driver" />';
	var labelOrderNumber 	= '<fmt:message key="livemap.label.order.number" />';
	var labelOrderStatus 	= '<fmt:message key="livemap.label.order.status" />';
	var labelDeliveryTo 	= '<fmt:message key="livemap.label.order.deliveryto" />';
	var labelProduct 		= '<fmt:message key="livemap.label.product" />';
	var labelShipto 		= '<fmt:message key="livemap.label.shipto" />';
	var labelLoadingOrder 	= '<fmt:message key="livemap.label.loading.order" />';
	var labelPlantName 		= '<fmt:message key="livemap.label.plant.name" />';
	var labelCurrentOrders 	= '<fmt:message key="livemap.label.current.orders" />';
	var labelShPoint 		= '<fmt:message key="livemap.label.shpoint" />';
	var labelEvent 		= '<fmt:message key="livemap.label.event" />';
	
	//marker arrays
	var plantArray = new Array();
	var shiptoArray = new Array();
	var shiptoGeoArray = new Array();
	var shPointArray = new Array(); 
	var shPointGeoArray = new Array(); 
	var truckArray = new Array();
	var trailerArray = new Array();
	var containerArray = new Array();
	var wagonArray = new Array();
	var actualRouteArray = new Array();
	var actualRouteMainArray = new Array();
	
	var routeEventArray = new Array();
	var passedRouteArray = new Array();
	
	var orderActualRouteMapping = {};
	
	//constants
	var BIZEVENT_TYPE_SHIPPING = <%=Constant.BIZEVENT_TYPE_SHIPPING%>; 
	var BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT = <%=Constant.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT%>; 
	var BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT = <%=Constant.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT%>; 
	var BIZEVENT_TYPE_LEAVE_SHIPTO = <%=Constant.BIZEVENT_TYPE_LEAVE_SHIPTO%>; 
	var BIZEVENT_TYPE_TRAFFIC_JAM_START = <%=Constant.BIZEVENT_TYPE_TRAFFIC_JAM_START%>; 
	var BIZEVENT_TYPE_TRAFFIC_JAM_END = <%=Constant.BIZEVENT_TYPE_TRAFFIC_JAM_END%>; 
	var BIZEVENT_TYPE_POSITION = <%=Constant.BIZEVENT_TYPE_POSITION%>; 
	
	GMapAPILoader.onAPIReady(function(){
		loadMap();
		loadAllMapObjects();
	});		

	Ext.onReady( function() {
		// --- Filters ---
		Ext.get('divAdvancedSearch').setDisplayed(false);
		<jsp:include page="LiveKarteInc.jsp"/>
		
		GMapAPILoader.load();
	});

	function loadMap() {
		var divMap = Ext.getDom('map');
		
		//set map height
		resizeMapVertically();
		window.onresize = resizeMapVertically;
		
		var defaultLogonGmapOptions = {  
			    panControl: false, 
			    zoomControl: false, 
			    mapTypeControl: false,
			    scaleControl: false,
			    streetViewControl: false,
			    overviewMapControl: false,
			    scrollwheel: true
		};
	    
		gmap = new google.maps.Map(divMap, defaultLogonGmapOptions);
	    gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
		var trafficLayer = new google.maps.TrafficLayer();
		trafficLayer.setMap(gmap);
		gmap.checkResize = function(){
			google.maps.event.trigger(this, 'resize');
		};
		
		var latlng = new google.maps.LatLng(<%=Constant.MAP_CENTER_LAT%>, <%=Constant.MAP_CENTER_LNG%>);
		gmap.setCenter(latlng);
		gmap.setZoom(<%=Constant.MAP_CENTER_ZOOM_LEVEL%>);


		google.maps.event.addListener(gmap, 'zoom_changed', function() {
		    zoomChangeBoundsListener = 
		        google.maps.event.addListener(gmap, 'bounds_changed', function(event) {
		            if (this.getPrevZoom < this.getZoom() && this.autoZoom) {
		                // Change max/min zoom here
		                this.setZoom(this.getPrevZoom);
		                this.autoZoom = false;
		            }else{
		            	this.getPrevZoom = this.getZoom();
		            }
		        google.maps.event.removeListener(zoomChangeBoundsListener);
		    });
		});
		
		hideShowLegend('hidden');
	}


	//return function from AJAX service
	function loadStaticMapObjectsReturn(map) {
		staticObjectMap = map;
		setupStaticMarkers();
	}
	
		//return function from AJAX service
	function loadDynamincMapObjectsReturn(list) {
		if (tempDynamicObjectList == null) {
			tempDynamicObjectList = list;
		}
		else {
			for (var i = 0; i < list.length; i++){
				tempDynamicObjectList[tempDynamicObjectList.length] = list[i];
			}
		}
		if (list.length < dynamicObjectListPagesize){
			dynamicObjectList = tempDynamicObjectList;
			tempDynamicObjectList = null;
			setupDynamicMarkers();
		}
		else {
			LiveKarteAjaxService.fetchAllDynamicMapObjects(
				tempDynamicObjectList.length, dynamicObjectListPagesize,
				loadDynamincMapObjectsReturn);
		}

	}


	//load dynamic objects (trucks, trailers, etc.)from db. get static objects from struts form. 
	function loadAllMapObjects() {
		LiveKarteAjaxService.fetchStaticMapObjects(loadStaticMapObjectsReturn); 
		LiveKarteAjaxService.fetchAllDynamicMapObjects(
			0, dynamicObjectListPagesize,loadDynamincMapObjectsReturn);
	}
	
	
	//load main fitlers when filters are changed (do not load from DB!)
	function refreshFilters() {
		
		form = Ext.getDom('LiveKarteForm');
		p = {
			'filterAction' :'<%=LiveKarteAction.FILTER_REFRESH%>'
		};
		addFormElements(p, form);
		
		var diva = Ext.get('divAdvancedSearch');
		diva.load( {
			url :'LiveKarte.do',
			params :p,
			text :'<fmt:message key="message.still_loading"/>',
			scripts : true,
			callback : function () {
				lastUpdateDat = new Date();
				lastUpdateDatField.setValue(lastUpdateDat);
			}
		});
	}
	
	//load fitlers when Refresh button is clicked (load from DB!)
	function loadFilters() {
		
		var orderStatusSelectedList = toCommaDelimitedStr(orderStatusCheckComboBox.getCheckedValues());
		
		form = Ext.getDom('LiveKarteForm');
		p = {
			'filterAction' :'<%=LiveKarteAction.FILTER_MAIN%>',
			'orderStatusSelectedList' :  orderStatusSelectedList
		};
		
		addFormElements(p, form);
		if (advancedSearch == false) {
			Ext.getDom('divAdvancedSearch').style.height = "0px";
			//Ext.get('divAdvancedSearch').setDisplayed(false);
		}
		
		var diva = Ext.get('divAdvancedSearch');
		diva.load( {
			url :'LiveKarte.do',
			params :p,
			text :'<fmt:message key="message.still_loading"/>',
			scripts :true,
			callback : function () {
				lastUpdateDat = new Date();
				lastUpdateDatField.setValue(lastUpdateDat);
				if (advancedSearch == false) {
					Ext.get('divAdvancedSearch').setDisplayed(advancedSearch);
				}
			} 
		});
	}

	// TODO eliminate
	function toCommaDelimitedStr(arr) {
		var res = '';
		for (var i=0;i<arr.length;i++) {
			res += arr[i];
			if (i + 1<arr.length) {
				res += ',';
			}
		}	
		return res;
	}
	
	//calculate height of the map
	function resizeMapVertically() {
		var divMap = Ext.getDom('map');		
		//set map height
		var windowHeight = getWindowHeight();
		var MAP_MIN_HEIGHT = 310;
		var mapHeight = (windowHeight > MAP_MIN_HEIGHT) ? windowHeight : MAP_MIN_HEIGHT;  
		divMap.style.height =  mapHeight + "px";
	}
</script>
 
<jsp:include page="../SubTab7Tile.jsp" />

<!-- Begin table 3 rows -->
<table class="big_form2">

	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.monitoring" />
		/ <fmt:message key="tabsubitem.7.1" /></th>
	</tr>
	<tr>

		<td><jsp:include page="../Sub1Tab7Tile.jsp" /> <html:form
			action="auftrag/LiveKarte.do" styleId="LiveKarteForm"
			style="margin-top: 0px;">
			<html:hidden property="formAction" value="" />

			<table id="searchTable" cellpadding="0" cellspacing="0"
				class="big_form2" style="margin: 0px; border-collapse: collapse;">
				<tr>
					<td class="border"
						style="background-color: F9F9F9; padding: 2px 0 0 10px;">
					<div id="divSimpleSearch">
					<table style="width: 100%;">
						<colgroup>
							<col width="15%">
							<col width="5%">
							<col width="20%">
							<col width="20%">
							<col width="20%">
							<col width="20%">
						</colgroup>
						<tr>
							<td class="tinylabel"><fmt:message
								key="bestellung.monitoring.lastupdate" /></td>
							<td class="tinylabel"><fmt:message
								key="bestellung.monitoring.live" /></td>
							<td class="tinylabel"><fmt:message key="fields.order.number" />
							</td>
							<td class="tinylabel"><fmt:message key="fields.order.status" />
							</td>
							<td rowspan="2" class="tinylabel">
							<table cellspacing="0" cellpadding="0"
								style="margin: 0px; padding: 0px; border: 0px;">
								<tr>
									<td><input id="buttonSearch" type="button"
										class="SmallButtonStyle"
										value="<fmt:message key="bestellung.monitoring.refresh"/>" /><br />
									</td>
								</tr>
								<tr>
									<td><input id="buttonToggleAdvanced" type="button"
										class="SmallButtonStyleDown"
										value="<fmt:message key="buttons.expand"/>" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td>
							<div style="float: left; "><input
								type="text" id="lastUpdateDatStr" /></div>
							</td>
							<td>
							<div id="autoRefresh" style="float: left;"></div>
							</td>
							<td>
							<div style="float: left; padding-right: 10px;"><input
								type="text" id="orderNumber" style="width: 180px" /></div>
							</td>
							<td><input type="text" id="orderStatusField"
								style="width: 180px" /></td>
						</tr>
					</table>
					</div>
					<div id="divAdvancedSearch"
						style="visibility: hidden; border-top: 1px solid #CCC;"><jsp:include
						page="filters/LiveKarteFilters.jsp" /></div>

					<div id="mapLegendDiv" style="border-top: 1px solid #CCC;">
					<table style="width: 100%; padding: 0px; margin: 0px;">
						<tbody>
							<tr>
								<td>
								<div id="liveMapTitle">
								<div style="float: left;"><span class="labelblue"><fmt:message
									key="livemap.label.livemap" />&nbsp;</span></div>
								<div style="float: left;">
								<div id="liveMapTimeLeftRefreshId" /></div>
								</div>
								</td>
								<td width="160" align="left"><nobr><span
									class="labelblue"><fmt:message
									key="livemap.label.legend" /> </span>&nbsp;&nbsp;<input
									id="hideLegendId" type="button"
									onclick="hideShowLegend('hidden')" class="SmallButtonStyle"
									value=" <fmt:message key="livemap.label.hide" /> "
									style="visibility: hidden; position: absolute;"> <input id="showLegendId"
									type="button" onclick="hideShowLegend('visible')"
									class="SmallButtonStyleLeft"
									value=" <fmt:message key="livemap.label.show" /> "
									style="position: absolute;"></nobr></td>
							</tr>
						</tbody> 
					</table>

				<table style="width: 100%; padding: 0px; margin: 0px;">
					<tbody>
						<tr>
							<td width="100%"><!--position:absolute; -->
							<div id="map" style="width: 100%; margin-top: 0px;overflow:hidden"
								class="border">map</div>
							</td>
							<td width="160">
							<div id="legendDiv" class="subformlist"
								style="visibility: hidden; position: absolute; font-size: 12px; height: 310px; width: 160px;">
							<table class="compact" cellpadding="0" cellspacing="0" border="0"
								style="width: 100%;">
								<colgroup>
									<col width="1%">
									<col width="98%">
									<col width="1%">
								</colgroup>
								<tbody>
									<tr class="zebra1">
										<td valign="middle" style="width: 1px"><input
											id="legendPlant" type="checkbox"
											onclick="checkStaticMapObjects();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.plant" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_ORDERMAP_PLANT%>"></td>
									</tr>
									<tr class="zebra0">
										<td valign="middle" style="width: 1px"><input
											id="legendShPoint" type="checkbox"
											onclick="checkStaticMapObjects();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.shpoint" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_ORDERMAP_SHIPPING_POINT%>"></td>
									</tr>
									<tr class="zebra1">
										<td valign="middle" style="width: 1px"><input
											id="legendShipto" type="checkbox"
											onclick="checkStaticMapObjects();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.shipto" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_ORDERMAP_SHIPTO%>"></td>
									</tr>
									<tr class="zebra0">
										<td valign="middle" style="width: 1px"><input
											id="legendRoute" type="checkbox" 
											onclick="checkMapRoute();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.planed.route" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_ROUTE%>"></td>
									</tr>
									<tr class="zebra1">
										<td valign="middle" style="width: 1px"><input
											id="legendEvent" type="checkbox" 
											onclick="checkDynamicMapObjects();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.actual.route" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_EVENT%>"></td>
									</tr>
									<tr class="zebra0">
										<td valign="middle" style="width: 1px"><input
											id="legendTruck" type="checkbox" checked="checked"
											onclick="checkDynamicMapObjects();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.truck" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_TRUCK_UNLOADED_INSIDE_GEOFENCE%>"></td>
									</tr>
									<tr class="zebra1">
										<td valign="middle" style="width: 1px"><input
											id="legendTrailer" type="checkbox" 
											onclick="checkDynamicMapObjects();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.trailer" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_TRAILER_UNLOADED%>"></td>
									</tr>
									<tr class="zebra0">
										<td valign="middle" style="width: 1px"><input
											id="legendContainer" type="checkbox"
											onclick="checkDynamicMapObjects();" 
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.container" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_CONTAINER_UNLOADED%>"></td>
									</tr>
									<tr class="zebra1">
										<td valign="middle" style="width: 1px"><input
											id="legendWagon" type="checkbox"
											onclick="checkDynamicMapObjects();"
											style="border: 0px none; margin: 0px; padding: 0px;" /></td>
										<td><fmt:message key="livemap.legend.vagon" /></td>
										<td><img
											src="/logon/admin/imagename?keyValue=<%=LiveKarteAction.LIVEMAP_RAILCAR_UNLOADED%>"></td>
									</tr>

								</tbody>
							</table>
							</div>
							</td>
						</tr>
					</tbody>
				</table>

				</div>

				</td>
				</tr>
			</table>


		</html:form> <!-- End Border Table --></td>
	</tr>
</table>

<%-- END $RCSfile: LiveKarteTile.jsp,v $ --%>

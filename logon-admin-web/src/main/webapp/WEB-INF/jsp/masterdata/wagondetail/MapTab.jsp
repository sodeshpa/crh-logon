<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.constants.Constant"%>
<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_MAP_TAB%>"/>

<script type="text/javascript">
var gmap;
GMapAPILoader.onAPIReady(function(){
	loadMap();
});		

Ext.onReady(function() {
	GMapAPILoader.load();
});

function loadMap() {
	var divMap = Ext.getDom('map');

	//set map height
	divMap.style.height = document.getElementById('mainSection').offsetHeight - 30;

	//set google map	
	gmap = new google.maps.Map(divMap, defaultLogonGmapOptions);
    gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
	
	var latlng = new google.maps.LatLng(<%=Constant.MAP_CENTER_LAT%>, <%=Constant.MAP_CENTER_LNG%>);
	gmap.setCenter(latlng)
	gmap.setZoom(<%=Constant.MAP_CENTER_ZOOM_LEVEL%>); 
	
}
</script>

<div id="map" style="width: 100%; margin-top: 0px;overflow:hidden" class="border">map</div>

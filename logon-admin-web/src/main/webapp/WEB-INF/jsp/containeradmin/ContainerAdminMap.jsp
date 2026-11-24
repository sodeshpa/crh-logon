<%@ include file="../includeTLDs.jsp"%>
<%@page
	import="com.crh.logon.persistence.entities.common.ContainerEntity"%>
<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<%@page import="com.crh.logon.constants.Constant"%>
<script type="text/javascript">
	var gmap;
	var marker;
	
	GMapAPILoader.onAPIReady( function() {
		loadMap('<c:out value="${admin_ContainerAdminForm.inputCoordLat}"/>',
				'<c:out value="${admin_ContainerAdminForm.inputCoordLon}"/>');
	});

	Ext.onReady( function() {
		GMapAPILoader.load();
	});

	function loadMap(lat1, lng1) {

		gmap = new google.maps.Map(Ext.getDom("map"), defaultLogonGmapOptions);
		gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
		var trafficLayer = new google.maps.TrafficLayer();
		trafficLayer.setMap(gmap);
		
		if ((lng1 != '') && (lat1 != '')) {
			var latlng1 = new google.maps.LatLng(lat1, lng1);
			if(marker){
				marker.setPosition(latlng1);
			}else{
				marker = new google.maps.Marker({map: gmap, position: latlng1});
			}
			
			var zoom = 8;
			gmap.setCenter(latlng1);
		    gmap.setZoom(zoom);
		} else {
			var latlng = new google.maps.LatLng(<%=Constant.MAP_CENTER_LAT%>, <%=Constant.MAP_CENTER_LNG%>);
			gmap.setCenter(latlng);
			gmap.setZoom(<%=Constant.MAP_CENTER_ZOOM_LEVEL%>);
		}

		google.maps.event.addListener(gmap, "rightclick", function(event) {
			
			if(marker){
				marker.setPosition(event.latLng);
			}else{
				marker = new google.maps.Marker({map: gmap, position: event.latLng});
	    	}
			Ext.getDom('coordLon').value = event.latLng.lng();
			Ext.getDom('coordLat').value = event.latLng.lat();
		});

	}

	function updateMap() {
		var lat = Ext.getDom('coordLat').value;
		var lng = Ext.getDom('coordLon').value;
		
		if ((lng != '') && (lat != '')) {

			var zoom = gmap.getZoom();
			if ( isNaN(zoom) ) {
				zoom = 13;
			}
			var latlng = new google.maps.LatLng(lat, lng);
			gmap.setCenter(latlng);
			gmap.setZoom(zoom);

			if(marker){
				marker.setPosition(latlng);
			}else{
				marker = new google.maps.Marker({map: gmap, position: latlng});
			}
			
		}
	}
	
</script>
<table width="100%">
	<tr>
		<td align="center">
		<div id="map"
			style="height: 320px; width: 320px; padding-bottom: 10px;overflow:hidden"
			class="border">map</div>
		</td>
	</tr>
	<tr>
		<td><span class="label"><br>
		<fmt:message key="contadmin.label.coordinates" /> </span> <br>
		<table class="noborder" cellpadding="0" cellspacing="0" border="0"
			style="margin-top: 4px; padding: 0px;">
			<colgroup>
				<col width="1%">
				<col width="48%">
				<col width="49%">
			</colgroup>
			<tr>
			<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;">
<fmt:message
					key="contadmin.label.lastknown" /></td>
				<td><html:text styleId="coordLat" style="width:100%;" property="inputCoordLat"
					maxlength="<%=ContainerEntity.DBSIZE_HOMEBASE_COORD_LAT + ""%>" onkeyup="updateMap();" /></td>
				<td><html:text styleId="coordLon" style="width:100%;" property="inputCoordLon"
					maxlength="<%=ContainerEntity.DBSIZE_HOMEBASE_COORD_LON + ""%>" onkeyup="updateMap();" /></td>
			</tr>
			<tr>
			<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;">
<fmt:message
					key="contadmin.label.lastknown.shippingpoint" /></td>
				<td colspan="2"><c:out value="${admin_ContainerAdminForm.container.lastKnownShippingpoint.shippingpointName}" /></td>
			</tr>
			<tr>
			<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;">
<fmt:message
					key="contadmin.label.lastknown.shippingpoint.date" /></td>
				<td colspan="2"><c:out value="${admin_ContainerAdminForm.container.arrivalLastKnownSpDat}" /></td>
			</tr>									
		</table>
		<br>
		</td>
	</tr>
</table>

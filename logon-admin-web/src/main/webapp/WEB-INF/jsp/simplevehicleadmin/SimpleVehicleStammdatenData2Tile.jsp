<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<script type="text/javascript">
	
<%-- See details in main tile: SimpleVehicleStammdatenTile --%>
	GMapAPILoader.onAPIReady(function() {

		load_map('<c:out value="${admin_SimpleVehicleAdminForm.inputHomeBaseLat}"/>',
				'<c:out value="${admin_SimpleVehicleAdminForm.inputHomeBaseLon}"/>',
				'<c:out value="${admin_SimpleVehicleAdminForm.lastPosition[0]}"/>',
				'<c:out value="${admin_SimpleVehicleAdminForm.lastPosition[1]}"/>');
	});

Ext.onReady(function(){
		
	GMapAPILoader.load();	
});
var defaultMapCenter = 
{  
	lat : <%=Constant.MAP_CENTER_LAT%>,
	lng : <%=Constant.MAP_CENTER_LNG%>,
	zoomLevel : <%=Constant.MAP_CENTER_ZOOM_LEVEL%>
};
</script>
<table width="100%">
	<tr>
		<td align="center">
			<div id="map"
				style="height: 320px; width: 320px; padding-bottom: 10px; overflow: hidden"
				class="border">map</div>
		</td>
	</tr>
	<tr>
		<td><span class="label"><br> <fmt:message
					key="roadmasterdata.fields.coordinates" /> </span> <br>
			<table class="noborder" border="0">
				<colgroup>
					<col width="1%">
					<col width="48%">
					<col width="49%">
				</colgroup>
				<tr>
					<td valign="top" class="formtitle"
						style="white-space: nowrap; border: 0px; vertical-align: middle; color: #003D7E; font-weight: bold; padding: 0px;"><fmt:message
							key="roadmasterdata.fields.homebase" /></td>
					<td><input class="bordertextfield" type="text"
						name="inputHomeBaseLat" style="width: 100%;" id="inputHomeBaseLat"
						value='<c:out value="${admin_SimpleVehicleAdminForm.inputHomeBaseLat}"/>' /></td>
					<td><input class="bordertextfield" type="text"
						name="inputHomeBaseLon" style="width: 100%;" id="inputHomeBaseLon"
						value='<c:out value="${admin_SimpleVehicleAdminForm.inputHomeBaseLon}"/>' /></td>
				</tr>
				<tr>
					<td valign="top" class="formtitle"
						style="white-space: nowrap; border: 0px; vertical-align: middle; color: #FF1100; font-weight: bold; padding: 0px;"><fmt:message
							key="roadmasterdata.fields.lastknown" /></td>
					<td><input class="bordertextfield" disabled type="text"  id="lastPosition0" name="lastPosition0"
						style="width: 100%;"
						value='<c:out value="${admin_SimpleVehicleAdminForm.lastPosition[0]}"/>' /></td>
					<td><input class="bordertextfield" disabled type="text"  id="lastPosition1" name="lastPosition1"
						style="width: 100%;"
						value='<c:out value="${admin_SimpleVehicleAdminForm.lastPosition[1]}"/>' /></td>
				</tr>
			</table> <br></td>
	</tr>
</table>

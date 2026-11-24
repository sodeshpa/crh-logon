<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<script type="text/javascript">
	
<%-- See details in main tile: LKWStammdatenTile --%>
	GMapAPILoader.onAPIReady(function() {

		load_map('<c:out value="${admin_TruckAdminForm.inputHomeBaseLat}"/>',
				'<c:out value="${admin_TruckAdminForm.inputHomeBaseLon}"/>',
				'<c:out value="${admin_TruckAdminForm.lastPosition[0]}"/>',
				'<c:out value="${admin_TruckAdminForm.lastPosition[1]}"/>');
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
		<!--  <%-- Struts needs this, fond no better way to do it --%> <html:form action="truckadmin/TruckAdmin.do" styleId="admin_TruckAdminForm">-->
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
						value='<c:out value="${admin_TruckAdminForm.inputHomeBaseLat}"/>' /></td>
					<td><input class="bordertextfield" type="text"
						name="inputHomeBaseLon" style="width: 100%;" id="inputHomeBaseLon"
						value='<c:out value="${admin_TruckAdminForm.inputHomeBaseLon}"/>' /></td>
				</tr>
				<tr>
					<td valign="top" class="formtitle"
						style="white-space: nowrap; border: 0px; vertical-align: middle; color: #FF1100; font-weight: bold; padding: 0px;"><fmt:message
							key="roadmasterdata.fields.lastknown" /></td>
					<td><input class="bordertextfield" disabled type="text" id="lastPosition0" name="lastPosition0"
						style="width: 100%;"
						value='<c:out value="${admin_TruckAdminForm.lastPosition[0]}"/>' /></td>
					<td><input class="bordertextfield" disabled type="text" id="lastPosition1" name="lastPosition1"
						style="width: 100%;"
						value='<c:out value="${admin_TruckAdminForm.lastPosition[1]}"/>' /></td>
				</tr>
				<tr>
					<td valign="top" class="formtitle"
						style="white-space: nowrap; border: 0px; vertical-align: middle; font-weight: bold; padding: 0px;">
						<fmt:message key="dispo.texte.HomePlant" />
					</td>
					<td colspan="2"><!--  --></td>
				</tr>
				<tr>
					<td><span class="label"><fmt:message key="dispo.truckhomeplant.labels.plant"/></span></td>
					<td>
						<html:select property="selectedPlant" style="width:230px;" styleId="selectedPlant" onchange="setSelectedPlant();">
							<html:option value="-1">-</html:option>
							<c:forEach var="plant"
								items="${admin_TruckAdminForm.plants}">
								<html:option value="${plant.value}">
									<c:out value="${plant.name}"/>
								</html:option>
							</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td><span class="label"><fmt:message key="dispo.truckhomeplant.labels.shippingpoint"/></span></td>
					<td>
						<html:select property="selectedShippingpoint" style="width:230px;" styleId="selectedShippingpoint">
							<html:option value="-1">-</html:option>
							<c:forEach var="sp" items="${admin_TruckAdminForm.shippingpoints}">
								<html:option value="${sp.shippingpointId}">
									<c:out value="${sp.shippingpointName}" />
								</html:option>
							</c:forEach>
						</html:select>
					</td>
				</tr>
			</table>
		<!--</html:form>-->   
			<br>
		 </td>
	</tr>
</table>

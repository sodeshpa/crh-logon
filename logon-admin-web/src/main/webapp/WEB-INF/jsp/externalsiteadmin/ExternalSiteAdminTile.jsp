<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ExternalSiteAdminTile.jsp,v $ --%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page	import="com.crh.logon.admin.web.actions.externalsiteadmin.ExternalSiteAdminAction"%>
<%@page	import="com.crh.logon.persistence.entities.common.ExtSiteEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ include file="../includeTLDs.jsp"%>

<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<script type="text/javascript">
var gmap;
var marker;
var geofenceCircle;

GMapAPILoader.onAPIReady(function(){
	<c:if test="${admin_ExternalSiteAdminForm.carrierId != null}">
	onReady();
	</c:if>
});

Ext.onReady( function() {
		GMapAPILoader.load();
} );

function onReady(){
	load_map();
	
	var coordLonField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'coordLon',
		selectOnFocus :true,
		vtype :"decNum",
		enableKeyEvents :true,
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value, Ext
					.getDom('radius').value);
			}
		}
	});

	var coordLatField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'coordLat',
		selectOnFocus :true,
		vtype :"decNum",
		enableKeyEvents :true,
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value, Ext
					.getDom('radius').value);
			}
		}
	});

	var radiusField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'radius',
		selectOnFocus :true,
		vtype :"numeric",
		enableKeyEvents :true,
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value, Ext
					.getDom('radius').value);
			}
		}
	});

	if ((Ext.getDom('coordLat').value != '') && (Ext.getDom('coordLon').value != '')) {
		update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value,Ext.getDom('radius').value);
	};

};

function load_map() {

	gmap = new google.maps.Map(Ext.getDom("map"), defaultLogonGmapOptions);
    gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
    var trafficLayer = new google.maps.TrafficLayer();
    trafficLayer.setMap(gmap);

    google.maps.event.addListener(gmap, "rightclick", function(event) {
    	
		if(marker){
			marker.setPosition(event.latLng);
		}else{
			marker = new google.maps.Marker({map: gmap, position: event.latLng});
    	}
	
		radius = Ext.getDom('radius').value;
		if (radius != '') {
			drawCircle(latLng, radius);
		}
	
		Ext.getDom('coordLon').value = event.latLng.lng();
		Ext.getDom('coordLat').value = event.latLng.lat();
		
	});

}

/**
 * updates google map
 * 
 * @param lat
 *            Latitude in degrees
 * @param lng
 *            Longitude in degrees
 * @param radius
 *            Radius of the circle in statute miles
 */
function update_map(lat, lng, radius) {
	if ((lng != '') && (lat != '')) {

		var zoom = gmap.getZoom();
		if ( isNaN(zoom) ) {
			zoom = 13;
		}
		var latlng =  new google.maps.LatLng(lat, lng);
		gmap.setCenter(latlng);
		gmap.setZoom(zoom);

		if(marker){
			marker.setPosition(latlng);
		}else{
			marker = new google.maps.Marker({map: gmap, position: latlng});
    	}

		if (radius != '') {
			drawCircle(latlng, radius);
		}

	}
}

/**
 * @param lat
 *            Latitude in degrees
 * @param lng
 *            Longitude in degrees
 * @param radius
 *            circleRadius of the circle in statute miles
 */
function drawCircle(latLng, circleRadius) {
	var d = circleRadius * 1;
	
	var circleOptions = {
	      strokeColor: "#FF0000",
	      strokeOpacity: 1,
	      strokeWeight: 2,
	      fillColor: "#000000",
	      fillOpacity: 0.25,
	      map: gmap,
	      center: latLng,
	      radius: d,
	      zIndex: -1000
	    };
	if(geofenceCircle){
		geofenceCircle.setOptions(circleOptions);
	}else{
	    geofenceCircle = new google.maps.Circle(circleOptions);
	}
}


function setAddress() {

	var zip = Ext.getDom('sitePostalCode').value
	var city = Ext.getDom('siteCity').value
	var streetHouseNr = Ext.getDom('siteStreeHouseNo').value
	var country = Ext.getDom('siteCountry').value
	if (Ext.isEmpty(streetHouseNr) && Ext.isEmpty(zip) && Ext.isEmpty(city) && Ext.isEmpty(country)) {
		Logon.message.warning(texts.missingaddress);
	}
	else {
		var address = streetHouseNr + " " + zip + " " + city + " " + country;
		geocoder = new google.maps.Geocoder();
		geocoder.geocode( { 'address': address}, this.addAddressToMap.createDelegate(this));
	}
}

function addAddressToMap (results, status) {
	if (!results || status != google.maps.GeocoderStatus.OK || results.size == 0) {
		Logon.message.warning(texts.addresserror);
	} 
	else {
       if (results.size > 1) {
       	Logon.message.warning(texts.ambiguousaddress);
       }
       else {
            var place = results[0];
            var lat = place.geometry.location.lat();
	        var lng = place.geometry.location.lng();
	        var zoom = gmap.getZoom();
	        if ( isNaN(zoom) ) {
		        zoom = 13;
	        }
	        var latlng =  new google.maps.LatLng(lat, lng);
			gmap.setCenter(latlng);
			gmap.setZoom(zoom);

			if(marker){
				marker.setPosition(latlng);
			}else{
				marker = new google.maps.Marker({map: gmap, position: latlng});
	    	}
			      
			Ext.getDom('coordLat').value = lat;
			Ext.getDom('coordLon').value = lng;
       }
	}
}

</script>


<script type="text/javascript" src="<crh-common:url value='/js/logon-validation.js'/>"></script>

<script type="text/javascript">
var forwards = {
		SELECT_CARRIER: '<%=ExternalSiteAdminAction.FORWARD_SELECT_CARRIER%>',
		SAVE: '<%=ExternalSiteAdminAction.FORWARD_SAVE%>',
		SELECT_SITE: '<%=ExternalSiteAdminAction.FORWARD_SELECT_SITE%>',
		ADD_SITE: '<%=ExternalSiteAdminAction.FORWARD_ADD_SITE%>',
		RESET: '<%=ExternalSiteAdminAction.FORWARD_RESET%>'
	};

	
	function selectCarrier(){
		document.getElementById("inputAction").value=forwards.SELECT_CARRIER;
		Ext.get('admin_ExternalSiteAdminForm').dom.submit();
	}

	function saveSite(){
		document.getElementById("inputAction").value=forwards.SAVE;
		Ext.get('admin_ExternalSiteAdminForm').dom.submit();
	}

	function selectSite(id){
		document.getElementById("siteId").value = id;
		document.getElementById("inputAction").value = forwards.SELECT_SITE;
	    Ext.get('admin_ExternalSiteAdminForm').dom.submit();
	}

	function addSite(){
		document.getElementById("inputAction").value=forwards.ADD_SITE;
		Ext.get('admin_ExternalSiteAdminForm').dom.submit();
	}

	function resetFields(){
		document.getElementById("inputAction").value=forwards.RESET;
		Ext.get('admin_ExternalSiteAdminForm').dom.submit();
	}
	
	var texts = {		
			ambiguousaddress: '<fmt:message key="extsite.warnings.ambiguous.address"/>',
			missingaddress: '<fmt:message key="extsite.warnings.missing.address"/>',
			addresserror: '<fmt:message key="extsite.warnings.address.error"/>'
	};
</script>


<div class="customer_max"><!-- beginn Border-Table   --> 
	<jsp:include page="../SubTab9Tile.jsp" /> <!-- Begin table 3 rows -->
	<html:form action="externalsiteadmin/ExternalSiteAdmin.do" styleId="admin_ExternalSiteAdminForm">
		<table class="big_form2" border="0">
			<tr class="classicblue">
				<th height="21" colspan="4" class="classicblue"><fmt:message
					key="tabitem.masterdata" />  <fmt:message key="tabsubitem.9.5" /></th>
			</tr>
			<tr>
				<td>
					<!-- Beginn Submenu 2-2-1-1 --> <jsp:include page="../Sub5Tab9Tile.jsp" /> 
					<input type="hidden" name="inputAction" id="inputAction" value="" />
					<input type="hidden" name="siteId" id="siteId" value="<c:out value="${admin_ExternalSiteAdminForm.siteId}"/>" />
			
					<%--------------------------------------------------------------%>
					<%---------------------  Carrier list  -------------------------%>
					<%--------------------------------------------------------------%>
					<div class="subform" style="border-top: 0px;"><!-- Beginn Search Table -->
						<table class="classicblue" style="width: 100%;">
						  <tr>
							<td>
								<table>
									<tr>
										<td valign="bottom"
											style="white-space: nowrap; border: 0px; padding-right: 4px;">
										</td>
										<span class="labelblue">
											<fmt:message key="extsite.label.carrier" />
										</span>
										<td style="white-space: nowrap; border: 0px;">
											<span class="content" style="border: 0px;"> 
												<html:select onchange="selectCarrier()" styleId="carrierId" property="carrierId" style="margin:0px; padding:0px; width:auto;">
													<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">
														<fmt:message key="extsite.label.carrierchoose" />
													</html:option>
													<c:forEach var="entry"
														items="${admin_ExternalSiteAdminForm.carrierMap}">
														<html:option value="${entry.key}">
															<c:out value="${entry.value.displayName}" />
														</html:option>
													</c:forEach>
												</html:select>
											</span>
										</td>
										<td style="border: 0px;"><span class="labelblue"><fmt:message
											key="extsite.label.address" /></span><br>
											<c:out value="${admin_ExternalSiteAdminForm.carrierName}" /> <br>
											<c:out value="${admin_ExternalSiteAdminForm.carrierStreet}" /> <br>
											<c:out value="${admin_ExternalSiteAdminForm.carrierCity}" />
										</td>
									</tr>
								</table>
							
							</td>
						  </tr>
						  
						  	<%---------------- if ( carrierId != null )  ----------------------------%>
							<c:if test="${admin_ExternalSiteAdminForm.carrierId != null}">
						  
						  							<tr>
						<th height="21" colspan="4"><fmt:message key="tabitem.masterdata" />
								/ <fmt:message key="tabsubitem.9.5" /> / <fmt:message
									key="tabsubitem.9.5.9" /></th>
						</tr>
						  
						  
						  <tr height="60%">
							<td>
								<table border=0 width="100%" >
									<tr>
											<td width="20%">
												<span class="subtitle1"> <fmt:message key="extsite.label.standort" /> </span>
												<div class="scrollist"
													style="margin: 4px 0px 4px 0px; height: 410px; width: 100%;">
													<table cellpadding="0" cellspacing="0" border="0"
														style="border: medium none; width: 100%;">
														<tbody>
															<c:set var="zebracount" value="1" />
															<c:if test="${empty admin_ExternalSiteAdminForm.siteMap}">
																<td>
																	<a href="#">
																		<fmt:message key="extsite.label.nosite" />
																	</a>
																</td>
															</c:if>
															<c:if test="${!empty admin_ExternalSiteAdminForm.siteMap}">
																<c:forEach var="entry"
																	items="${admin_ExternalSiteAdminForm.siteMap}">
																	<c:set var="zebracount" value="${(zebracount + 1)%2}" />
																	<tr
																		<c:if test="${entry.key eq admin_ExternalSiteAdminForm.siteId }">class="selected"</c:if>
																		<c:if test="${entry.key ne admin_ExternalSiteAdminForm.siteId }"> class="zebra<c:out value="${zebracount}" />" </c:if>>
																		<td><a href="#"
																			onclick='selectSite(<c:out value="${entry.key}"/>)'><c:out
																			value="${entry.value.siteName} ${entry.value.siteLoadUnload}"/></a></td>
																	</tr>
																</c:forEach>
															</c:if>
														</tbody>
													</table>
												</div>
											</td>
											<td>
											</td>
										<td width="40%">
						
											<!-- table of the "fields" -->
											<span class="subtitle1"> <fmt:message key="extsite.label.masterdata" /> </span>
												<div class="scrollist"
													style="margin: 4px 0px 4px 0px; height: 410px; width: 100%;">
											<table  width="100%" height="100%">
											
												<%--------------------------------------------------------------%>
												<%-----------------  siteName ----------------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td width="30%">
														<span class="label"> <fmt:message key="extsite.label.name" /> </span> 
													</td>
													<td width="70%">
														<html:text style="width:100%;" styleId="siteName" property="siteName" 
															maxlength="<%=ExtSiteEntity.DBSIZE_SITE_NAME + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%-----------------  activeFlag  --- ---------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.activeFlag" /> </span> 
													</td>
													<td class="content" style="white-space: nowrap; border: 0px;">
															<html:checkbox property="activeFlag"/>
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  siteLoadingPoint -----------------------------%>
												<%--------------------------------------------------------------%>												
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.texte.siteload_unload" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="siteLoadingPoint" property="siteLoadingPoint" 
															maxlength="<%=ExtSiteEntity.DBSIZE_SITE_NAME + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  siteStreeHouseNo ----------------------------%>
												<%--------------------------------------------------------------%>												
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.street.house.nr" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="siteStreeHouseNo" property="siteStreeHouseNo" 
															maxlength="<%=ExtSiteEntity.DBSIZE_SITE_STREET_HOUSE_NO + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  sitePostalCode ------------------------------%>
												<%--------------------------------------------------------------%>												
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.zip" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="sitePostalCode" property="sitePostalCode" 
															maxlength="<%=ExtSiteEntity.DBSIZE_SITE_POSTAL_CODE + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  siteCity ------------------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.city" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="siteCity" property="siteCity" 
															maxlength="<%=ExtSiteEntity.DBSIZE_SITE_CITY + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  siteCountry ---------------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.country" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="siteCountry" property="siteCountry" 
															maxlength="<%=ExtSiteEntity.DBSIZE_SITE_COUNTRY + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  siteContactPerson ---------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.cont.person" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="siteContactPerson" property="siteContactPerson" 
															maxlength="<%=ExtSiteEntity.DBSIZE_SITE_CONTACT_PERSON_TXT + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  geofenceRadiusM -----------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.geofence" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="radius" property="radius" 
															maxlength="<%=ExtSiteEntity.DBSIZE_GEOFENCE_RADIUS_M + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%---------------  loadTimeMin ---------------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.texte.siteload_unload.duration" /> </span> 
													</td>
													<td>
														<html:text style="width:100%;" styleId="loadTimeMin" property="loadTimeMin" 
															maxlength="<%=ExtSiteEntity.DBSIZE_LOAD_UNLOAD_TIME_MIN + ""%>" />
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%-------------- coordLat + coordLon ---------------------------%>
												<%--------------------------------------------------------------%>
												<tr>
													<td>
														<span class="label"> <fmt:message key="extsite.label.coordinates" /> </span> 
													</td>
													<td >
														<table style="width:100%;">
														<tr>
															<td>		
															<html:text style="width:100%;" styleId="coordLon" property="coordLon" 
																maxlength="<%=ExtSiteEntity.DBSIZE_COORD_LON + ""%>" />
															</td>
															<td>
															<html:text style="width:100%;" styleId="coordLat" property="coordLat" 
																maxlength="<%=ExtSiteEntity.DBSIZE_COORD_LAT + ""%>" />
															</td>
														</tr>	
														</table>	
													</td>
												</tr>
												
												<%--------------------------------------------------------------%>
												<%--------------- carrierTruckDriverNote -----------------------%>
												<%--------------------------------------------------------------%>
												<tr>
														<td>
															<div style="width: 50%;">
																<span class="label"><fmt:message
																	key="extsite.label.drivernotes" />
																</span>
																<c:set var="textarealength"><%=ExtSiteEntity.DBSIZE_CARRIER_TRUCK_DRIVER_NOTE + ""%></c:set>
															</div>
														</td>
														<td>					
															<html:textarea onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="truckDriverNote" property="truckDriverNote"
																	rows="4" style="width: 98%;">
															</html:textarea>
														</td>
												</tr>
											</table>
											</div>
											<!-- table of the "fields" END -->
							
										</td>
										<td>
											<input type="button" value="Set address" class="ViewButtonStyle" onClick='setAddress()'/>
											<div id="map" style="width: 450px; height: 400px; margin-top: 8px;overflow:hidden"
											class="border">map</div>
											
										</td>
									</tr>
								</table>
							</td>
						  </tr>
						  <tr height="20%">
							<td>
								<!-- buttons -->
								<table width="100%">
								<tr>
									<td width="80%">
									<div align="left">
										<input onclick="javascript:addSite();" type="button"
											class="SmallButtonStyle"
											value=" <fmt:message key="buttons.add"/> ">
									</div>
									</td>
									<td>
										<div align="right">
											<input id="resetButton" name="resetButton"
												onclick="javascript:resetFields();" type="button"
												class="ViewButtonStyle" style="margin-bottom: 4px;"
												value="<fmt:message key="buttons.reset"/>">
										</div>
									</td>
									<td>
										<div align="left">
											<input id="saveButton" name="saveButton"
												onclick="javascript:saveSite();" type="button"
												class="ViewButtonStyle" style="margin-bottom: 4px;"
												value="<fmt:message key="buttons.save"/>">
										</div>
									</td>
								</tr>
								</table>
								<!-- buttons END -->
						
							</td>
						  </tr>
						  	
						  	</c:if>
							<%----------------END:[ if ( carrierId != null ) ] --------------------------%>
						  
						  
						</table>
					</div>
				</td>
			</tr>
		</table>
	</html:form>
</div>
<!-- End Border Table -->

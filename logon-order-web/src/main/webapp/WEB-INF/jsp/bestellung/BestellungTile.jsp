<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BestellungTile.jsp,v $ --%>

<%@ page session="true" %>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ include file="../includeTLDs.jsp" %>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<c:set var="soldto" value="${form.editorList[0].order.soldtoSapBp}"/>
<c:set var="textareaCls" value="x-form-field comments" scope="request"/>
<c:set var="lnfShiptoTypeCd"><%=Long.valueOf(Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED)%></c:set>
<c:set var="isLnfShipto" scope="request" value="${form.shipTo.shiptoType.shiptoTypeCd == lnfShiptoTypeCd}"/>
<c:set var="isOneTimeSoldto" scope="request" value="${form.shiptoMap.soldTo.treatAsOnetimeFlag}"/>
<c:set var="displayShipToTimezone" scope="request" value="${form.displayShipToTimezone}"/>

<link type="text/css" rel="stylesheet"  
		href="<crh-common:url value="/css/bestellung.css"/>">

<script type="text/javascript" src='<crh-common:url
		value="/js/logon-validation.js"/>'></script>
<script type="text/javascript" src="<crh-common:url
		value="/js/logon-gmap.js"/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.MaterialCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/dwr/interface/OrderAjaxService.js'/>"></script>
<script type="text/javascript"
		src="<crh-common:url value="/js/logonPackageHandlingHelper.js"/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.TableStyleCheckComboBox.js'/>"></script>

<script type="text/javascript">

var bestellungFormVar;
var saveButtonWaitQueueSize = 0;
var blockChartRefresh = false;
var mapLoadingInProgress = false;
var chartLoadingInProgress = false;
var selectedEditorIdx = 0;

var texts = {		
		noteDeliveryLong: '<fmt:message key="bestellung.bemerkungen.zusatz"/>',
		noteDispatchLong: '<fmt:message key="bestellung.bemerkungen.pool"/>',
		noteTruckDriverLong: '<fmt:message key="bestellung.bemerkungen.chauffeur"/>',
		noteSpeditionLong: '<fmt:message key="bestellung.bemerkungen.bahndisposition"/>'
};

//var shiptoCoords = 

// TODO move to common js file
Ext.override(Ext.data.Store, {
    loadData : function(o, append){
        var r = this.reader.readRecords(Ext.isEmpty(this.emptyItem) ? o : [this.emptyItem].concat(o));
        this.loadRecords(r, {add: append}, true);
    }
})

<%-- TODO move to common util class --%> 
Array.prototype.findByProperty = function(property, value) {
    for (var i=0; i<this.length; i++) {
        if (this[i][property] == value) { 
            return this[i];
        }
    }
}

function logonPopupHideCallback() {
	// TODO: reload order lists	
};

function showDiv(id) {
	Ext.fly(id).removeClass('hiddenDiv');
}

function hideDiv(id) {
	Ext.fly(id).addClass('hiddenDiv');
}

function getDelFromDateTime(index) {
	// Can be null; i.e. in case of bimodal 
	if (Ext.getDom('deliveryFromDatStr['+index+']')) {	
		return Date.parseDate(Ext.getDom('deliveryFromDatStr['+index+']').value + editorList[index].delFromTime.getValue(), 'd.m.yH:i');
	}
}

function getDelToDateTime(index) {
	// Can be null; i.e. in case of bimodal 
	if (Ext.getDom('deliveryToDatStr['+index+']')) {	
		return Date.parseDate(Ext.getDom('deliveryToDatStr['+index+']').value + editorList[index].delToTime.getValue(), 'd.m.yH:i');
	}
}

function checkComments() {
	var idx = 0;
	var itemIdx = 0;

	//initialize comment arrays. Order notes must be check even if there are none for items
	var deliveryNotes = new Array();
	deliveryNotes.push("orderCommon.noteDelivery");
	var dispatchingNotes = new Array();
	dispatchingNotes.push("orderCommon.noteDispatching");
	var truckDriverNotes = new Array();
	truckDriverNotes.push("orderCommon.noteTruckDriver");
	var railwayNotes = new Array();
	railwayNotes.push("orderCommon.noteRailwayDisp");

	//collect item notes	
	while (document.getElementById("editorList["+idx+"].orderItems["+itemIdx+"].itemNoteDelivery")){
		while(document.getElementById("editorList["+idx+"].orderItems["+itemIdx+"].itemNoteDelivery")){
			deliveryNotes.push("editorList["+idx+"].orderItems["+itemIdx+"].itemNoteDelivery");
			dispatchingNotes.push("editorList["+idx+"].orderItems["+itemIdx+"].itemNoteDispatching");
			truckDriverNotes.push("editorList["+idx+"].orderItems["+itemIdx+"].itemNoteTruckDriver");
			railwayNotes.push("editorList["+idx+"].orderItems["+itemIdx+"].itemNoteRailway");
			itemIdx = itemIdx + 1;
		}
		idx = idx + 1;
	}

	//check all the fields at once
	var canSave = checkMergeableCommentLength(deliveryNotes,'<fmt:message key="warning.exceeded"/>',texts.noteDeliveryLong,<%=OrderEntity.DBSIZE_NOTE_DELIVERY + ""%>); 
	if (!canSave) {
		return false;
	}
	canSave = checkMergeableCommentLength(dispatchingNotes,'<fmt:message key="warning.exceeded"/>',texts.noteDispatchLong,<%=OrderEntity.DBSIZE_NOTE_DISPATCHING + ""%>);
	if (!canSave) {
		return false;
	}
	canSave = checkMergeableCommentLength(truckDriverNotes,'<fmt:message key="warning.exceeded"/>',texts.noteTruckDriverLong,<%=OrderEntity.DBSIZE_NOTE_TRUCK_DRIVER + ""%>);
	if (!canSave) {
		return false;
	}
	canSave = checkMergeableCommentLength(railwayNotes,'<fmt:message key="warning.exceeded"/>',texts.noteSpeditionLong,<%=OrderEntity.DBSIZE_NOTE_RAILWAY_DISP + ""%>);
	if (!canSave) {
		return false;
	}
	return true;
}

function submitWithAction(action){
	if (!checkComments()){
		return false;
	}
	form = Ext.getDom('bestellungForm4');
	Ext.query("input[name=action]", form)[0].value = action;
	form.submit();
}

function addChartDateDay(val) {
	var dayCombo = Ext.getDom('chartBean.selectedDayStr');
	if (val == -1 && dayCombo.selectedIndex > 0) {
		dayCombo.value = dayCombo.options[dayCombo.selectedIndex - 1].value;
		refreshChartAreaToDefer();
	}
	else if (val == 1 && dayCombo.selectedIndex < dayCombo.options.length - 1) {
		dayCombo.value = dayCombo.options[dayCombo.selectedIndex + 1].value;
		refreshChartAreaToDefer();
	}
}

function refreshChartArea(params) {
	// --- Do not refresh the chart, when the user clicks on the "Save" button immediately
	refreshChartAreaToDefer.defer(500, refreshChartAreaToDefer, [params]);
}

function refreshChartAreaToDefer(params) {
	
	if (!blockChartRefresh) {

		saveOrderBtn.disable();
		adjustDeliveriesBtn.disable();
		saveButtonWaitQueueSize++;
		
		Ext.getCmp('delFromDat['+selectedEditorIdx+']').clearInvalid();
		Ext.getCmp('delFromTime['+selectedEditorIdx+']').clearInvalid();
		Ext.getCmp('delToDat['+selectedEditorIdx+']').clearInvalid(); 
		Ext.getCmp('delToTime['+selectedEditorIdx+']').clearInvalid();					
		
		params = (Ext.isEmpty(params) ? {} : params);
		
		addFormElements(params, 'bestellungForm1');
		addFormElements(params, 'bestellungForm2');
		
		addChartFormParamas(params);
		
		// Explicitly set the call center flag to true, otherwise some chart elements
		// are not loaded.
		params['isOnCallCenter'] = 'true';
		
		var isChartDisplayable = params['isChartDisplayable'];
		if (isChartDisplayable == undefined) {
			params['isChartDisplayable'] = true;
			isChartDisplayable = true;
		}

		if (isChartDisplayable == true){
			Ext.get('divChartLoadMask').mask('Refreshing chart...', 'x-mask-loading');
		}
		Ext.get('divChart').load({
			url: 'Chart.do',
			params: params,
			scripts: true,
			text: '',
			timeout: 4000,
			callback: function(el, success) {
				if (!success) {
					Logon.message.error('An error occurred during the Chart-reloading');
				}
				else {
					var divChart = Ext.getDom("chart");
					divChart.style.display = "";

					var callCenterHeading = Ext.getDom("callCenterHeading");
					callCenterHeading.style.display = "";
					
					if (isChartDisplayable == true) {
						Ext.get('divChartLoadMask').unmask();
					}
					saveButtonWaitQueueSize--;
					if (saveButtonWaitQueueSize < 1) {
						saveOrderBtn.update();
						adjustDeliveriesBtn.update();
					}
				}
			},
			nocache: true
		});
	}
}

function resetTravelTimeFields() {
	Ext.getDom('cchDrivetoTime').value = '';
	Ext.getDom('cchReturnTime').value = '';
	Ext.getDom('cchCycleTime').value = '';
}

function updateTravelTimeFields(caller) {
	if (caller === 'map') {
		mapLoadingInProgress = false;
	}
	else if (caller === 'chart') {
		chartLoadingInProgress = false;
	}
	if ((!mapLoadingInProgress && !chartLoadingInProgress) || caller === 'unloadingTimeChangeHandler') {
		
		var editor = editorList[selectedEditorIdx];
		
		var drivetoTime;
		var returnTime;
		
		if (editor) {
			drivetoTime = parseFloat(editor.durationTime);
			returnTime = parseFloat(editor.durationTime);
		}
		
		var loadingTime = parseFloat(Ext.getDom('loadingTime').value); 
		var unloadingTime = parseFloat(Ext.getDom('unloadingTime').value);
		
		if (!isNaN(drivetoTime) && drivetoTime > 0) {
			Ext.getDom('cchDrivetoTime').value = Math.round(drivetoTime);
			Ext.getDom('cchReturnTime').value = Math.round(returnTime); // same as drive to time
			if (!isNaN(loadingTime) && !isNaN(unloadingTime)) {
				Ext.getDom('cchCycleTime').value = Math.round(loadingTime) + Math.round(drivetoTime) + Math.round(unloadingTime) + Math.round(drivetoTime);
			}
			else {
				Ext.getDom('cchCycleTime').value = '';
			}
		}
		else {
			Ext.getDom('cchCycleTime').value = '';
		}
	}
}

function switchContactPerson(name, firstname) {
	Ext.getDom('editorList[0].order.contactPersonTxt').value = (Ext.value(firstname, '') + ' ' + Ext.value(name, '')).trim();
}

function reloadChart() {
	var chartUrlParams = {
		productType: Ext.getDom('chartBean.productType').value,
		pool: Ext.getDom('chartBean.pool').value,
		carrier: Ext.getDom('chartBean.transporteur').value
		//vehicleCat: Ext.getDom('chartBean.vehicleCat').value,
		//vehicleType: Ext.getDom('chartBean.vehicleType').value
	}
	Ext.getDom('chartImg').src = '<c:url value="/bestellungChart?"/>' + Ext.urlEncode(chartUrlParams);
}


function getLogonAddressFromGooglePlace(place) {
	
	var streetAddress = logonGmap.searchAddressComponents(place, 'route');
	if(streetAddress){
		streetAddress = streetAddress.long_name;
	}else{
		streetAddress = '';
	}
	
	var streetNumber = logonGmap.searchAddressComponents(place, 'street_number');
	if(streetNumber){
		streetAddress = streetAddress + ', ' + streetNumber.long_name;
	}else{
		streetNumber = '';
	}
	
	var postalCode = logonGmap.searchAddressComponents(place, 'postal_code');
	if(postalCode){
		postalCode = postalCode.long_name;
	}else{
		postalCode = '';
	}
	
	var locality = logonGmap.searchAddressComponents(place, 'locality');
	if(locality){
		locality = locality.long_name;
	}else{
		locality = '';
	}
	
	var country = logonGmap.searchAddressComponents(place, 'country');
	if(country){
		country = country.short_name;
	}else{
		country = '';
	}
	
	return {
		countryCode: country,
		city: locality,
		postalCode: postalCode,
		streetHouseNo: streetAddress
	}
}

<c:if test="${isLnfShipto}">

	isLnf = true;
	
	lnfShipto = {
		addressToMap: function() {
			var lnfAddress = Ext.getDom('orderCommon.lnfSiteCountry').value + ' ' +
					Ext.getDom('orderCommon.lnfSitePostalCode').value + ' ' +
					Ext.getDom('orderCommon.lnfSiteCity').value + ' ' +
					Ext.getDom('orderCommon.lnfSiteStreetHouseNo').value;
			logonGmap.setShiptoAddress(lnfAddress);
		},
		mapToAddress: function() {
			if (logonGmap.addressSearchResult != null) {
				
				var logonAddress = getLogonAddressFromGooglePlace(logonGmap.addressSearchResult);

				Ext.getDom('orderCommon.lnfSiteCountry').value = logonAddress.countryCode; 
				Ext.getDom('orderCommon.lnfSitePostalCode').value = logonAddress.postalCode;  
				Ext.getDom('orderCommon.lnfSiteCity').value = logonAddress.city;
				Ext.getDom('orderCommon.lnfSiteStreetHouseNo').value = logonAddress.streetHouseNo;
			}			 
		}
	}
	
	zones = {
		polygonList: <c:out value="${form.json['zonePolygonList']}" escapeXml="false"/>,
		vertexList: <c:out value="${form.json['zoneVertexList']}" escapeXml="false"/>,
		pMarkers: new Array(),
		polygonColorMap: {}, 
		polygonMap: {},
		polygonDataMap: {},
		init: function() {

			//init polygon map
			for (var i=0; i<zones.polygonList.length; i++) {
				zones.polygonDataMap[zones.polygonList[i].trspZonePolygonId] = new Array();
			}
			
			var curMarker;
			
			if (zones.vertexList.length > 0) {
				curMarker = zones.vertexList[0]; 
				zones.polygonColorMap[curMarker.trspZonePolygon.trspZonePolygonId] = curMarker.trspZonePolygon.colorRgb;
			}
			
			for (var i=0; i<zones.vertexList.length; i++) {
	
				if (curMarker.trspZonePolygon.trspZonePolygonId == zones.vertexList[i].trspZonePolygon.trspZonePolygonId) {
					//create not dragable marker
					marker = getMarker(zones.vertexList[i].coordLat, zones.vertexList[i].coordLon);
	
				    //add marker to the polygon map
				    //polygonDataMap[curMarker.trspZonePolygon.trspZonePolygonId].push(new GLatLng(polygonList[i].coordLat, polygonList[i].coordLon));
				    zones.polygonDataMap[curMarker.trspZonePolygon.trspZonePolygonId].push(marker);
				    curMarker = zones.vertexList[i]; 
				}
				else {
					//create not dragable marker
					marker = getMarker(zones.vertexList[i].coordLat, zones.vertexList[i].coordLon);
	
					//create a new polygon map entry and add marker
					zones.polygonDataMap[zones.vertexList[i].trspZonePolygon.trspZonePolygonId] = new Array();
					//polygonDataMap[polygonList[i].trspZonePolygon.trspZonePolygonId].push(new GLatLng(polygonList[i].coordLat, polygonList[i].coordLon));
					zones.polygonDataMap[zones.vertexList[i].trspZonePolygon.trspZonePolygonId].push(marker);
					curMarker = zones.vertexList[i]; 
	
					//create polygon color map (just for init stage)
					zones.polygonColorMap[curMarker.trspZonePolygon.trspZonePolygonId] = curMarker.trspZonePolygon.colorRgb;
				}
			}
			
			// --- Generate all polygons ---
			for (var key in zones.polygonDataMap) {
				zones.polygonMap[key] = createPolygon(zones.polygonDataMap[key], '#'+zones.polygonColorMap[key]);
			}
			
		},
		draw: function() {
			// --- Draw all polygons ---
			for (var key in zones.polygonDataMap) {
				zones.polygonMap[key].setMap(logonGmap.map);
			}
		},
		show: function() {
			for (var key in zones.polygonDataMap) {
				zones.polygonMap[key].setVisible(true);
			}
		},
		hide: function() {
			for (var key in zones.polygonDataMap) {
				zones.polygonMap[key].setVisible(false);
			}
		},
		toggle: function(isOn) {
			if (isOn) {
				zones.show();
			}
			else {
				zones.hide();
			}
		}
	}
	
	//---------returns polygon for a given marker array and color-------------//
	function createPolygon(pMarkers, color) {

		var pPoints = new Array();
		for (i = 0; i < pMarkers.length; i++) {
			pPoints.push(pMarkers[i].getPosition());
		}
		
		// Close the shape with the last line or not
		if (typeof (pMarkers[0]) != "undefined") {
			pPoints.push(pMarkers[0].getPosition());
		}

		var polygon = new google.maps.Polygon({paths : pPoints,
			   strokeColor : color, 
			   strokeWeight : 3, 
			   strokeOpacity : 0.5,
			   fillColor : color,
			   fillOpacity : 0.3
			   });
		   
		return polygon;
	}
	
	function getMarker(coordLat, coordLon) {

		// Square marker icons
		var square = new google.maps.MarkerImage('<crh-common:url value="/images/square_black.png"/>',
				  new google.maps.Size(11, 11),
				  new google.maps.Point(0,0),
				  new google.maps.Point(5, 5));

		var latLng = new google.maps.LatLng(coordLat, coordLon);
		var marker = new google.maps.Marker({
			icon :square,
			draggable :false,
			raiseOnDrag : false,
			position: latLng
		});
		
		return marker;
	}

	function renderLnfHistory() {
		var lnfFields = [
		         			'orderId',
		         			'actualDeliveryDat',
		         			'lnfCoordLat',
		         			'lnfCoordLon',
		         			'lnfSiteName',
		         			'lnfSiteStreetHouseNo',
		         			'lnfSiteCountry',
		         			'lnfSitePostalCode',
		         			'lnfSiteCity'
		         			];
			
		var lnfStore= new Ext.data.JsonStore({
	    	fields: lnfFields,
	    	data : <c:out value="${form.json['lnfList']}" escapeXml="false"/>
		});

		 // Custom rendering Template
	    var lnfTemplate = new Ext.XTemplate(
	    	'<table class="classicblue" style="width:100%"><thead><tr>',
	    	'<th><fmt:message key="bestellung.lnf.site.name"/></th>',
	    	'<th><fmt:message key="bestellung.lnf.site.street.house.nr"/></th>',
	    	'<th><fmt:message key="bestellung.lnf.site.country"/></th>',
	    	'<th><fmt:message key="bestellung.lnf.site.zip.city"/></th>',
	    	'<th><fmt:message key="bestellung.lnf.postalcode"/></th>',
	    	'<th><fmt:message key="bestellung.lnf.actual.delivery.dat"/></th>',
	    	'</tr></thead><tbody>',
	        '<tpl for=".">',
		        	'<tr class="listitem zebra{[xindex % 2]}">',
		        		'<td><tpl if="!this.isNotAvailable(lnfSiteName)">{lnfSiteName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(lnfSiteStreetHouseNo)">{lnfSiteStreetHouseNo}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(lnfSiteCountry)">{lnfSiteCountry}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(lnfSitePostalCode)">{lnfSitePostalCode}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(lnfSiteCity)">{lnfSiteCity}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(actualDeliveryDat)">{actualDeliveryDat}</tpl></td>',
			        '</tr>', 
	        '</tpl>',
	        '</tbody></table>',
	        {
	            // XTemplate configuration:
	            compiled: true,
	            disableFormats: true,
	            isNotAvailable: function(val){	                
	                return val == null; 
	            },
	            notAvailableText:'<fmt:message key="webbestellung.text.chooseproduct"/>'
	        }	        
	    );
		var lnfHistoryCombo = new Ext.form.ComboBox({
			id: 'selectLnfHistory',
			name: 'selectLnfHistory',
		    transform:'selectLnfHistory',
		    applyTo:'selectLnfHistory',
			store:lnfStore,
			allowBlank: true,
			emptyText: '<fmt:message key="bestellung.lnf.select.from.lnf.history"/>',
			selectOnFocus: true,
			editable: true,
			shadow: false,
		    triggerAction: 'all',
		    mode: 'local',
		    tpl: lnfTemplate,
		    valueField: 'orderId',
		    displayField: 'txt',
		    width: Ext.get('selectLnfHistory').getWidth(),
		    itemSelector: 'tr.listitem',
	        onSelect: function(record){
				fillInLnfShiptoAddress(record.data);
				lnfHistoryCombo.collapse();
        	}
		});
	}
	
	function fillInLnfShiptoAddress(data) {
		if (!data) {
			return;
		}
		
		Ext.getDom('orderCommon.lnfSiteName').value = !Ext.isEmpty(data.lnfSiteName) ? data.lnfSiteName : "";
		Ext.getDom('orderCommon.lnfSiteCountry').value = (!Ext.isEmpty(data.lnfSiteCountry)
				? data.lnfSiteCountry : "");
		Ext.getDom('orderCommon.lnfSitePostalCode').value = data.lnfSitePostalCode;
		Ext.getDom('orderCommon.lnfSiteCity').value = !Ext.isEmpty(data.lnfSiteCity) ? data.lnfSiteCity : "";
		Ext.getDom('orderCommon.lnfSiteStreetHouseNo').value = (!Ext.isEmpty(data.lnfSiteStreetHouseNo)
				? data.lnfSiteStreetHouseNo : "");

		if (!Ext.isEmpty(data.lnfCoordLat) && !Ext.isEmpty(data.lnfCoordLon) ) {
			// Catching exceptions here is to make sure the rest of the script does not stop from execution
			// in case of an error with logonGmap (that would already have been reported), eg. when cloning
			// an order.
			try {
				logonGmap.setShiptoLatLngAndZone(data.lnfCoordLat, data.lnfCoordLon);
			}
			catch (e) {
				// console.log(e);
				// Ignore for now.
			}
		}
	}

	function resetOrdersOnSelectedLnfTimeZone() {
		// reset the New Order fields to force the user to re-think the Delivery From / Delivery To settings
		form = Ext.getDom('bestellungForm1');
		Ext.query("input[name=action]", form)[0].value = 'resetOrdersOnSelectedLnfTimeZone';

		saveOrderBtn.disable();
		adjustDeliveriesBtn.disable()

		form.submit();
	}

</c:if>

<c:if test="${isOneTimeSoldto}">
	
	function fillInOneTimeSoldtoAddress(data) {
		if (!data) {
			return;
		}
		
		Ext.getDom('orderCommon.oneTimeSoldtoName').value = (!Ext.isEmpty(data.oneTimeSoldtoName)
				? data.oneTimeSoldtoName : "");
		Ext.getDom('orderCommon.oneTimeSoldtoCountry').value = (!Ext.isEmpty(data.oneTimeSoldtoCountry)
				? data.oneTimeSoldtoCountry : "");
		Ext.getDom('orderCommon.oneTimeSoldtoPostalCode').value = data.oneTimeSoldtoPostalCode;
		Ext.getDom('orderCommon.oneTimeSoldtoCity').value = (!Ext.isEmpty(data.oneTimeSoldtoCity)
				? data.oneTimeSoldtoCity : "");
		Ext.getDom('orderCommon.oneTimeSoldtoStreetHouseno').value =
				!Ext.isEmpty(data.oneTimeSoldtoStreetHouseno) ? data.oneTimeSoldtoStreetHouseno : "";
		Ext.getDom('orderCommon.oneTimeSoldtoVatNo').value =
			!Ext.isEmpty(data.oneTimeSoldtoVatNo) ? data.oneTimeSoldtoVatNo : "";
	}
	
</c:if>

logonGmap = null;

GMapAPILoader.onAPIReady(function(){
	
   	// --- Currently only used in LNF cases. Move into a better place if needed ---
	<c:if test="${isLnfShipto}">
		google.maps.Polygon.prototype.Contains = function(latLng) {
		  var j=0;
		  var oddNodes = false;
		  var x = latLng.lng();
		  var y = latLng.lat();

		  var paths = this.getPath();

		  for (var i=0; paths != undefined && i < paths.getLength(); i++) {
			    j++;
			    if (j == paths.getLength()) {j = 0;}
			    if (((paths.getAt(i).lat() < y) && (paths.getAt(j).lat() >= y))
			    || ((paths.getAt(j).lat() < y) && (paths.getAt(i).lat() >= y))) {
			      if ( paths.getAt(i).lng() + (y - paths.getAt(i).lat())
			      /  (paths.getAt(j).lat()-paths.getAt(i).lat())
			      *  (paths.getAt(j).lng() - paths.getAt(i).lng())<x ) {
			        oddNodes = !oddNodes
			      }
			    }
			  }
			  return oddNodes;
		}
	</c:if>

	<c:if test="${isLnfShipto}">
		zones.init();
	</c:if>
	
	<c:choose>
		<c:when test="${not form.validShiptoCoords}">
			<c:set var="bp" value="${form.shipTo.shiptoSapBp}"/>
			<c:set var="countryOfBp" value="${form.countryForShipto}"/>
			<c:set var="shiptoAddress" value="${countryOfBp}, ${bp.postalCode}, ${bp.city}, ${bp.street}, ${bp.houseNumber}"/>
			logonGmap.loadMap("<c:out value="${shiptoAddress}"/>");
		</c:when>
		<c:otherwise>
			logonGmap.loadMap();
		</c:otherwise>
	</c:choose>

	<c:if test="${isLnfShipto}">
		zones.draw();
	</c:if>
	
	/*
	GEvent.addListener(logonGmap.map, "click", function(point) {

		var latlng = logonGmap.map.fromContainerPixelToLatLng(point);
		logonGmap.map.clearOverlays();
		marker = new GMarker(latlng);
		logonGmap.map.addOverlay(marker);
		
		//Ext.getDom('coordLon').value = latlng.lng();
		//Ext.getDom('coordLat').value = latlng.lat();
		
	});
	*/

	//if (isLnf) {
		//this.setupContextMenuControl();
		//logonGmap.map.addControl(new ContextMenuControl());
	//}
	
});

Ext.onReady(function(){

	logonGmap = new Logon.GMap({
		divId: 'divMap',
		shiptoLat: '<c:out value="${form.shipTo.coordLat}"/>',
		shiptoLng: '<c:out value="${form.shipTo.coordLon}"/>'
	});

	GMapAPILoader.load(true);
	Ext.getBody().on('unload', logonGmap.unloadMap);
	
	<c:if test="${form.shipTo.dailyOrderContactFlag && form.dailyOrderStatusCd != 4}">
		Ext.get('btnChangeDailyOrderStatus').on('click', function() {
			if (editorList[0].orderItems[0].sapMatCombo.getValue() && !confirm('You order has not been saved yet. Discard the order?')) { // TODO litdict
				return false;
			}
			Logon.message.showLoading('<fmt:message key="bestellung.indicator.changing.daily.order.status"/>...');
			Ext.getDom('bestellungForm4').submit();
		});
	</c:if>
	<c:if test="${isLnfShipto}">
		renderLnfHistory();
	</c:if>

});

</script>

<html:form action="bestellung/Bestellung.do" styleId="bestellungForm1">

<input type="hidden" name="action" id="action" value="save"/>
<input type="hidden" name="durationTime" id="durationTime" value=""/>
<input type="hidden" name="distanceMeters" id="distanceMeters" value=""/>

<html:hidden property="editorList[0].orderHelper.deliveryTrspZoneCd" styleId="editorList[0].orderHelper.deliveryTrspZoneCd"/>

<div class="box">
	<table width="99%">	
	  	<tr>
			<td class="labelblue">
				<fmt:message key="dispokarte.texte.kunde"/>:
			</td>
	    	<td>
	    		<c:out value="${soldto.sapId}"/>
	    	</td>
	    	<td>
	    		<b><c:out value="${soldto.name1}"/> <c:out value="${soldto.name2}"/></b>
	    		<c:if test="${not empty soldto.customerClassification}">
	    			(<c:out value="${soldto.customerClassification}"/>)
	    		</c:if>
	    	</td>
			<td class="labelblue">
				<fmt:message key="dispokarte.texte.shipto"/>:
			</td>
			<td>
	    		<c:out value="${form.shipTo.shiptoSapBp.sapId}"/>
	    	</td>
	       	<td>   	
	    		<b><c:out value="${form.shipTo.shiptoSapBp.name1}"/> <c:out value="${form.shipTo.shiptoSapBp.name2}"/></b>
	    	</td>
	       	<td>   	
	    		<c:out value="${form.shipTo.shiptoSapBp.street}"/>
	    		<c:out value="${form.shipTo.shiptoSapBp.houseNumber}"/>
	    	</td>
	    	<td>    	
	        	<c:out value="${form.shipTo.shiptoSapBp.country.sapCountryCd}"/> - 
	    		<c:out value="${form.shipTo.shiptoSapBp.postalCode}"/>
	    		<c:out value="${form.shipTo.shiptoSapBp.city}"/>
			</td>
			<td class="buttons right">	
				<input type="button" onClick="javascript:window.location='<c:url value="/bestellung/BestellungShipToEntry.do"/>'" class="ViewButtonStyle" value="<fmt:message key="dispokarte.texte.shiptoclear"/>"/>
		    </td>					
	    </tr>
	</table>
</div>

<table class="big_form2" style="border: 1px solid #CCCCCC; padding: 0px; margin-top: 5px">
	<tr>
		<!-- Ship-to -->
		<c:choose>
			<c:when test="${isOneTimeSoldto}">
				<c:set var="shiptoDataHeight" value="513px"/><%-- TODO armin: throw out this "implementation" --%>
			</c:when>
			<c:otherwise>
				<c:set var="shiptoDataHeight" value="410px"/>
			</c:otherwise>
		</c:choose>
		<td style="width: 33%; padding-right: 0px">
			<div class="border subform" style='height: <c:out value="${shiptoDataHeight}"/>'>
			<table style="table-layout:fixed" width="100%" cellpadding="0" cellspacing="0">
				<colgroup>
					<col width="8%">
					<col width="6%">
					<col width="6%">
					<col width="70%">
				</colgroup>
				<tr>
					<td colspan="4" style="padding: 2px">
						<!-- �ffnungszeiten / Opening times -->  		
						<p>
								<span class="label" style="margin-bottom: 2px">
									<fmt:message key="dispokarte.texte.oeffnungszeiten"/>:&nbsp;
								</span>
								<html:text styleClass="textfield" style="width: 42px" size="5" property="open1From" disabled="true"/> -
								<html:text styleClass="textfield" style="width: 42px" size="5" property="open1To" disabled="true"/> /
								<html:text styleClass="textfield" style="width: 42px" size="5" property="open2From" disabled="true"/> -
								<html:text styleClass="textfield" style="width: 42px" size="5" property="open2To" disabled="true"/>
						</p>

						<c:if test="${displayShipToTimezone}">
							<p style="margin-bottom: 3px; margin-top: 5px">
								<span class="label">
									<fmt:message key="application.label.timezone"/>:&nbsp;
									<c:out value="${form.shipToTimeZone.formattedAsTimeZoneOnly}"/>
								</span>
							</p>
						</c:if>

                        <!-- Contact person list / Ansprechpartner -->
						<table cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="label" style="padding: 0px; padding-top: 8px">
									<span style="margin-top: 4px"><fmt:message key="dispokarte.texte.ansprechpartner"/></span>
								</td>
								<td class="buttons right" style="vertical-align: top">
						 			<input type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="showPopup('../dispokarte/AnsprechpartnerEntry.do?referrer=order&action=add','')"/>
								</td>
							</tr>
						</table>
						<div class="scrollist" id="DivAnsprechpartner" style="height:
						<c:choose>
						<c:when test="${isLnfShipto}"> 
						40px;
						</c:when>
						<c:otherwise>
						70px;
						</c:otherwise>
						</c:choose>
						">
							<jsp:include page="../dispokarte/Ansprechpartner.jsp">
								<jsp:param name="referrer" value="order"/>
							</jsp:include>
						</div>
						
						<!-- Silo allocation / LNF shipto address -->  		
						<c:choose>
							<c:when test="${isLnfShipto}">
								<table cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="label" style="padding:0px;padding-top:8px">
											<span style="margin-top: 8px"><fmt:message key="bestellung.lnf.shipping.address"/></span>
										</td>
										<td class="buttons right" style="vertical-align: top">&nbsp;</td>
									</tr>
								</table>
								<div class="scrollist" id="DivLnfAddress" style="background-color: #F8F8F8">
									<div style="vertical-align: middle; padding: 4px;margin: 3px;">
										<input class="x-form-text x-form-field" name="selectLnfHistory" id="selectLnfHistory" style="height: 20px; width:100%"/>
									</div>
									<html:hidden property="orderCommon.lnfCoordLat" styleId="orderCommon.lnfCoordLat"/>
									<html:hidden property="orderCommon.lnfCoordLon" styleId="orderCommon.lnfCoordLon"/>
									<table cellspacing="0" cellpadding="0" "
											style="border-spacing: 0px; width: 100%; margin: 3px">
										<colgroup>
											<col width="110px">
											<col width="50px">
											<col width="220px">
											<col width="97px">
											<col width="45px">
										</colgroup>
										<tr>
											<td class="label" style="vertical-align: middle; padding: 2px">
												<span style="margin-top: 8px"><fmt:message key="bestellung.lnf.site.name"/>:</span>
											</td>
											<td colspan="3" style="vertical-align: middle; padding: 2px">
												<c:set var="maxlength"><%=OrderEntity.DBSIZE_LNF_SITE_NAME + ""%></c:set>
												<html:text property="orderCommon.lnfSiteName" styleId="orderCommon.lnfSiteName" maxlength="${maxlength}" styleClass="x-form-text x-form-field" style="height: 20px; width:100%"/>
											</td>
											<td style="vertical-align: middle; padding: 2px"></td>
										</tr>
										<tr>
											<td class="label" style="vertical-align: middle; padding: 2px">
												<span style="margin-top: 8px"><fmt:message key="bestellung.lnf.site.street.house.nr"/>:</span>
											</td>
											<td colspan="3" style="vertical-align: middle; padding: 2px">
												<c:set var="maxlength"><%=OrderEntity.DBSIZE_LNF_SITE_STREET_HOUSE_NO + ""%></c:set>
												<html:text property="orderCommon.lnfSiteStreetHouseNo" styleId="orderCommon.lnfSiteStreetHouseNo" maxlength="${maxlength}" styleClass="x-form-text x-form-field" style="height: 20px; width: 100%"/>
											</td>
											<td style="vertical-align: middle; padding: 2px">
												<a><img alt="Map to address" title="Map to address" onClick="lnfShipto.mapToAddress()" src="<crh-common:url value='/images/arrow_left_blue.gif'/>" style="margin-top: 3px; margin-left: 3px"></a>
											</td>
										</tr>
										<tr>
											<td class="label" style="vertical-align: middle; padding: 2px">
												<span style="margin-top: 8px"><fmt:message key="bestellung.lnf.site.zip.city"/>:</span>
											</td>
											<td style="vertical-align: middle; padding: 2px">
												<c:set var="maxlength"><%=OrderEntity.DBSIZE_LNF_SITE_POSTAL_CODE + ""%></c:set>
												<html:text property="orderCommon.lnfSitePostalCode" styleId="orderCommon.lnfSitePostalCode" maxlength="${maxlength}" styleClass="x-form-text x-form-field" style="height: 20px; width: 100%"/>
											</td>
											<td colspan="2" style="vertical-align: middle; padding: 2px">
												<c:set var="maxlength"><%=OrderEntity.DBSIZE_LNF_SITE_CITY + ""%></c:set>
												<html:text property="orderCommon.lnfSiteCity" styleId="orderCommon.lnfSiteCity" maxlength="${maxlength}" styleClass="x-form-text x-form-field" style="height: 20px; width: 100%"/>
											</td>
											<td style="vertical-align: middle; padding: 2px">
												<a><img alt="Address to map" title="Address to map" onClick="lnfShipto.addressToMap()" src="<crh-common:url value='/images/arrow_right_blue.gif'/>" style="margin-top: 3px; margin-left: 3px"></a>
											</td>
										</tr>
										<tr>
											<td class="label" style="vertical-align: middle; padding: 2px">
												<span style="margin-top: 8px">
													<fmt:message key="bestellung.lnf.site.country"/>, <fmt:message key="bestellung.lnf.site.vat"/>:
												</span>
											</td>
											<td style="vertical-align: middle; padding: 2px">
												<c:set var="maxlength"><%=OrderEntity.DBSIZE_LNF_SITE_COUNTRY + ""%></c:set>
												<html:text property="orderCommon.lnfSiteCountry" styleId="orderCommon.lnfSiteCountry" maxlength="${maxlength}" styleClass="x-form-text x-form-field" style="height: 20px; width: 100%"/>
											</td style="vertical-align: middle; padding: 2px">
											<td style="vertical-align: middle; padding: 2px">
												<c:set var="maxlength"><%= OrderEntity.DBSIZE_LNF_SITE_VAT_NO + "" %></c:set>
												<html:text property="orderCommon.lnfSiteVatNo"
														styleId="orderCommon.lnfSiteVatNo"
														maxlength="${maxlength}"
														styleClass="x-form-text x-form-field"
														style="height: 20px; width: 100%"/>
											</td>
											<td class="label" colspan="2" style="vertical-align: middle; padding: 2px">
												<input type="checkbox" checked="true" onClick="zones.toggle(this.checked)" style="margin-top: 2px"/> <fmt:message key="tabsubitem.9.4.2"/>
											</td>
										</tr>
										<tr>
											<td class="label" style="vertical-align: middle; padding: 2px">
												<nobr><fmt:message key="application.label.timezone"/></nobr>
											</td>
											<td colspan="3">
												<html:select styleId="lnfTimeZone" size="1"
															 style="width:100%;"
															 property="lnfSelectedTimeZoneCd"
															 onchange="javascript:resetOrdersOnSelectedLnfTimeZone()">
													<c:forEach var="tz" varStatus="status"
															   items="${form.timeZoneList}">
														<html:option value="${tz.timezoneCd}">
															<c:out value="${tz.formattedAsTimeZoneOnly}"/>
														</html:option>
													</c:forEach>
												</html:select>
											</td>
										</tr>
									</table>
								</div>
							</c:when>
							<c:otherwise>
								<table cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="label" style="padding:0px;padding-top:8px">
											<span style="margin-top: 8px"><fmt:message key="dispokarte.texte.silobelegung"/></span>
										</td>
										<td class="buttons right" style="vertical-align: top">
											<input type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="showPopup('../dispokarte/SilobelegungEntry.do?referrer=order&action=add','')"/>
										</td>
									</tr>
								</table>
								<div class="scrollist" id="DivSilobelegung" style="height: 70px;">
									<jsp:include page="../dispokarte/Silobelegung.jsp">
										<jsp:param name="referrer" value="order"/>
										<jsp:param name="initialLoad" value="true"/>
									</jsp:include>
								</div>
							</c:otherwise>
						</c:choose>

						<%-- One-time Soldto Address --%>
						<c:if test="${isOneTimeSoldto}">
							<table cellspacing="0" cellpadding="0" style="width: 100%">
								<tr>
									<td class="label" style="padding:0px;padding-top:8px">
										<span style="margin-top: 8px">
											<fmt:message key="bestellung.onetimesoldto.address.title"/>:
										</span>
									</td>
									<td class="buttons right" style="vertical-align: top">&nbsp;</td>
								</tr>
							</table>
							<div class="scrollist" id="DivOneTimeSoldtoAddress"
									style="background-color: #F8F8F8">
								<table cellspacing="0" cellpadding="0" 
										style="border-spacing: 0px; width: 100%">
									<tr>
										<colgroup>
											<col width="110px">
											<col width="50px">
											<col width="320px">
											<col width="45px">
										</colgroup>
										<td class="label" style="vertical-align: middle; padding: 2px;">
											<span style="margin-top: 8px">
												<fmt:message key="bestellung.onetimesoldto.name"/>:
											</span>
										</td>
										<td colspan="2" style="vertical-align: middle; padding: 2px">
											<c:set var="maxlength">
												<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_NAME + "" %>
											</c:set>
											<html:text property="orderCommon.oneTimeSoldtoName"
													styleId="orderCommon.oneTimeSoldtoName"
													maxlength="${maxlength}"
													styleClass="x-form-text x-form-field"
													style="height: 20px; width:100%"/>
										</td>
										<td style="vertical-align: middle; padding: 2px"></td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: middle; padding: 2px">
											<span style="margin-top: 8px">
												<fmt:message key="bestellung.onetimesoldto.street.house.nr"/>:
											</span>
										</td>
										<td colspan="2" style="vertical-align: middle; padding: 2px">
											<c:set var="maxlength">
												<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_STREET_HOUSENO + "" %>
											</c:set>
											<html:text property="orderCommon.oneTimeSoldtoStreetHouseno"
													styleId="orderCommon.oneTimeSoldtoStreetHouseno"
													maxlength="${maxlength}"
													styleClass="x-form-text x-form-field"
													style="height: 20px; width: 100%"/>
										</td>
										<td style="vertical-align: middle; padding: 2px"></td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: middle; padding: 2px">
											<span style="margin-top: 8px">
												<fmt:message key="bestellung.onetimesoldto.zip.city"/>:
											</span>
										</td>
										<td style="vertical-align: middle; padding: 2px">
											<c:set var="maxlength">
												<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_POSTAL_CODE + "" %>
											</c:set>
											<html:text property="orderCommon.oneTimeSoldtoPostalCode"
													styleId="orderCommon.oneTimeSoldtoPostalCode"
													maxlength="${maxlength}"
													styleClass="x-form-text x-form-field"
													style="height: 20px; width: 100%"/>
										</td>
										<td style="vertical-align: middle; padding: 2px">
											<c:set var="maxlength">
												<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_CITY + "" %>
											</c:set>
											<html:text property="orderCommon.oneTimeSoldtoCity"
													styleId="orderCommon.oneTimeSoldtoCity"
													maxlength="${maxlength}"
													styleClass="x-form-text x-form-field"
													style="height: 20px; width: 100%"/>
										</td>
										<td style="vertical-align: middle; padding: 2px"></td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: middle; padding: 2px">
											<span style="margin-top: 8px">
												<fmt:message key="bestellung.onetimesoldto.country"/>, <fmt:message key="bestellung.onetimesoldto.vat"/>:
											</span>
										</td>
										<td style="vertical-align: middle; padding: 2px">
											<c:set var="maxlength">
												<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_COUNTRY + "" %>
											</c:set>
											<html:text property="orderCommon.oneTimeSoldtoCountry"
													styleId="orderCommon.oneTimeSoldtoCountry"
													maxlength="${maxlength}"
													styleClass="x-form-text x-form-field"
													style="height: 20px; width: 100%"/>
										</td style="vertical-align: middle; padding: 2px">
										<td style="vertical-align: middle; padding: 2px">
											<c:set var="maxlength">
												<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_VAT_NO + "" %>
											</c:set>
											<html:text property="orderCommon.oneTimeSoldtoVatNo"
													styleId="orderCommon.oneTimeSoldtoVatNo"
													maxlength="${maxlength}"
													styleClass="x-form-text x-form-field"
													style="height: 20px; width: 100%"/>
										</td>
										<td style="vertical-align: middle; padding: 2px"></td>
									</tr>
								</table>
							</div>
						</c:if>
					</td>
				</tr>
			</table>									
			<c:set var="orderConfTooltip"><fmt:message key="dispokarte.texte.order.conf" /></c:set>
			<c:set var="loadConfTooltip"><fmt:message key="dispokarte.texte.loadorder.conf" /></c:set>
			<c:set var="delivnoteTooltip"><fmt:message key="dispokarte.texte.deliv.note" /></c:set>
			<table>
				<tr>
					<td class="label" style="padding: 2px">&nbsp;</td>
					<td class="label" style="padding: 2px"><fmt:message key="bestellung.order.confirm.abbrev"/></td>
					<td class="label" style="padding: 2px"><fmt:message key="bestellung.load.confirm.abbrev"/></td>
					<td class="label" style="padding: 2px"><fmt:message key="bestellung.delivery.note.abbrev"/></td>
					<td class="label" style="padding: 2px" title="<fmt:message key="dispokarte.texte.papierlieferschein.tooltip"/>">
						<html:checkbox property="orderCommon.delivnoteSinglePaperFlag" styleId="orderCommon.delivnoteSinglePaperFlag" value="true"/>
						<c:set target="${form.orderCommon}" property="delivnoteSinglePaperFlag" value="false"/>						
						<fmt:message key="dispokarte.texte.papierlieferschein"/>
					</td>
					<td class="label" style="padding: 2px" title="<fmt:message key="dispokarte.texte.printcollectivedeliverynote.tooltip"/>">
						<%-- LOGON-5630: DeliveryNoteCollFlag is no longer saved on indiviadual Order but on CustOrder--%>
						<html:checkbox property="delivnoteCollPaperFlag" styleId="delivnoteCollPaperFlag" value="true"/>
						<c:set target="${form}" property="delivnoteCollPaperFlag" value="false"/>
						<fmt:message key="dispokarte.texte.printcollectivedeliverynote"/>
					</td>
				</tr>
				<tr>
					<td class="label" align="right" style="padding: 2px"><fmt:message key="dispokarte.texte.fax"/>:</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.orderConfFaxFlag" title="${orderConfTooltip}" styleId="orderCommon.orderConfFaxFlag" value="true" disabled="${empty form.orderCommon.orderConfFaxNumber}"/>
						<c:set target="${form.orderCommon}" property="orderConfFaxFlag" value="false"/>
					</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.loadConfFaxFlag" title="${loadConfTooltip}" styleId="orderCommon.loadConfFaxFlag" value="true" disabled="${empty form.orderCommon.orderConfFaxNumber}"/>
						<c:set target="${form.orderCommon}" property="loadConfFaxFlag" value="false"/>
					</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.delivnoteFaxFlag" title="${delivnoteTooltip}" styleId="orderCommon.delivnoteFaxFlag" value="true" disabled="${empty form.orderCommon.orderConfFaxNumber}"/>
						<c:set target="${form.orderCommon}" property="delivnoteFaxFlag" value="false"/>
					</td>
					<td width="100%" style="padding: 2px" colspan="2">
						<html:text property="orderCommon.orderConfFaxNumber" styleId="orderCommon.orderConfFaxNumber" maxlength="80" styleClass="x-form-text x-form-field" style="height: 20px; width:100%"/>
					</td>
				</tr>
				<tr>
					<td class="label" align="right" style="padding: 2px"><fmt:message key="dispokarte.texte.mail"/>:</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.orderConfEmailFlag" title="${orderConfTooltip}" styleId="orderCommon.orderConfEmailFlag" value="true" disabled="${empty form.orderCommon.orderConfEmailAdresses}"/>
						<c:set target="${form.orderCommon}" property="orderConfEmailFlag" value="false"/>
					</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.loadConfEmailFlag" title="${loadConfTooltip}" styleId="orderCommon.loadConfEmailFlag" value="true" disabled="${empty form.orderCommon.orderConfEmailAdresses}"/>
						<c:set target="${form.orderCommon}" property="loadConfEmailFlag" value="false"/>
					</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.delivnoteEmailFlag" title="${delivnoteTooltip}" styleId="orderCommon.delivnoteEmailFlag" value="true" disabled="${empty form.orderCommon.orderConfEmailAdresses}"/>
						<c:set target="${form.orderCommon}" property="delivnoteEmailFlag" value="false"/>
					</td>
					<td style="padding: 2px" colspan="2">
						<html:text property="orderCommon.orderConfEmailAdresses" styleId="orderCommon.orderConfEmailAdresses" styleClass="x-form-text x-form-field" style="height: 20px; width:100%"/>
					</td>
				</tr>
				<tr>
					<td class="label" align="right" style="padding: 2px"><fmt:message key="dispokarte.texte.sms"/>:</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.orderConfSmsFlag" title="${orderConfTooltip}" styleId="orderCommon.orderConfSmsFlag" value="true" disabled="${empty form.orderCommon.orderConfSmsNumber}"/>
						<c:set target="${form.orderCommon}" property="orderConfSmsFlag" value="false"/>
					</td>
					<td style="padding: 2px">
						<html:checkbox property="orderCommon.loadConfSmsFlag" title="${loadConfTooltip}" styleId="orderCommon.loadConfSmsFlag" value="true" disabled="${empty form.orderCommon.orderConfSmsNumber}"/>
						<c:set target="${form.orderCommon}" property="loadConfSmsFlag" value="false"/>
					</td>
					<td style="padding: 2px">&nbsp;</td>
					<td style="padding: 2px" colspan="2">
						<html:text property="orderCommon.orderConfSmsNumber" styleId="orderCommon.orderConfSmsNumber" maxlength="80" styleClass="x-form-text x-form-field" style="height: 20px; width:100%"/>
					</td>
				</tr>
			</table>
			</div>
		</td>
		
		<!-- Map -->
		<td style="width: 33%; padding-right: 0px">
			<div id="divMapLoadMask" class="border subform" style='height: <c:out value="${shiptoDataHeight}"/>'>
				<c:if test="${not form.validShiptoCoords && not isLnfShipto}">
					<table style="width: 100%; margin-bottom: 5px">
						<tr>
							<td class="label" style="border-bottom: 1px solid #CCCCCC">
								<nobr>Ship-to address:</nobr> 
							</td>
							<td style="border-bottom: 1px solid #CCCCCC">
								<input id="shiptoAddressField" style="width: 150px" class="textfield" value="<c:out value="${shiptoAddress}"/>"/>
							</td>
							<td style="border-bottom: 1px solid #CCCCCC">
								<input id="shiptoAddressSearchBtn" type="button" value="Set address" class="ViewButtonStyle" onClick="logonGmap.setShiptoAddress(document.getElementById('shiptoAddressField').value)"/>
							</td>
						</tr>
					</table>
				</c:if>
				<c:choose>
					<c:when test="${not form.validShiptoCoords && not isLnfShipto}">
						<div id="divMap" style="height: 305px;overflow:hidden"></div>
					</c:when>
					<c:otherwise>
						<div id="divMap" style="height: 340px;overflow:hidden"></div>
					</c:otherwise>
				</c:choose>
			</div>
		</td>
		
		<!-- Chart -->
        <td style="width: 33%">
			<div id="divChartLoadMask" class="border subform" style='height: <c:out value="${shiptoDataHeight}"/>'>
                <div id="divChart" style="width: 100%; height: 100%">
                    <jsp:include page="Chart.jsp" flush="true">
                        <jsp:param name="filterPage" value="orderEntry" />
                        <jsp:param name="imgWidth" value="470" />
                        <jsp:param name="imgHeight" value="170" />
                        <jsp:param name="checkboxWidth" value="90" />
						<jsp:param name="isOnCallCenter" value="true" />
                    </jsp:include>
                </div> 
            </div>
        </td>
	</tr>
	
	<!-- ---------------- -->
	<!-- Notes / Comments -->
	<!-- ---------------- -->
	<c:set var="textarealength" value="255" scope="request"/>
	<tr>
		<td colspan="3" style="padding-top: 0px">
			<table class="compact" style="width: 100%">
				<tr>
					<td style="width: 20%; padding-right: 7px">
						<p class="label"><fmt:message key="dispokarte.texte.auftragsmanagement"/></p>
						<%-- 
						<html:textarea	property="orderCommon.noteOrderMgmt" styleId="orderCommon.noteOrderMgmt" styleClass="x-item-disabled ${textareaCls}" style="padding: 0px" disabled="true"></html:textarea>
						<html:textarea	property="orderItemCommon.itemNoteOrderMgmt" styleId="orderItemCommon.itemNoteOrderMgmt" styleClass="x-item-disabled ${textareaCls}" style="margin-top:2px" disabled="true"></html:textarea>
						--%>
						<div id="divNoteOrderMgmt" style="background: #FFFFFF url(<crh-common:url value='/images/default/form/text-bg.gif'/>) repeat-x scroll 0 0; border: 1px solid #B5B8C8; height: 42px; overflow: auto; color: #888888">
							<c:out value="${form.orderCommon.noteOrderMgmt}"/>
						</div>						
					</td>
					<td style="width: 20%; padding-right: 7px">
						<p class="label"><fmt:message key="dispokarte.texte.zusatzlieferschein"/></p>
						<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_DELIVERY + ""%></c:set>
						<html:textarea	property="orderCommon.noteDelivery" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="orderCommon.noteDelivery" styleClass="${textareaCls}" style="padding: 0px; font-size: 11px"></html:textarea>
					</td>
					<td style="width: 20%; padding-right: 7px">
						<p class="label"><fmt:message key="dispokarte.texte.poolspediteur"/></p>
						<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_DISPATCHING + ""%></c:set>
						<html:textarea	property="orderCommon.noteDispatching" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="orderCommon.noteDispatching" rows="2" styleClass="${textareaCls}" style="padding: 0px; font-size: 11px"></html:textarea>
					</td>
					<td style="width: 20%; padding-right: 7px">
						<p class="label"><fmt:message key="dispokarte.texte.chauffeur"/></p>
						<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_TRUCK_DRIVER + ""%></c:set>
						<html:textarea	property="orderCommon.noteTruckDriver" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="orderCommon.noteTruckDriver" rows="2" styleClass="${textareaCls}" style="padding: 0px; font-size: 11px"></html:textarea>
					</td>
					<td style="width: 20%">
						<p class="label"><fmt:message key="dispokarte.texte.bahndisposition"/></p>
						<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_RAILWAY_DISP + ""%></c:set>
						<html:textarea	property="orderCommon.noteRailwayDisp" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="orderCommon.noteRailwayDisp" rows="2" styleClass="${textareaCls}" style="padding: 0px; font-size: 11px"></html:textarea>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</html:form>

<!-- -------------------------------- -->
<!-- New / Ongoing / Completed orders -->
<!-- -------------------------------- -->

<jsp:include page="ListOrders.jsp"/>

<html:form action="bestellung/Bestellung.do" styleId="bestellungForm4">

	<input type="hidden" name="action" id="action" value="changeDailyOrderStatus"/>
	<input type="hidden" name="allowFutureContracts" id="allowFutureContracts" value="false"/>
	
	<div class="box" style="margin-top: 5px">
		<table width="99%">	
		<tr>
			<td class="label" width="1" style="vertical-align: middle">
				<c:if test="${form.shipTo.dailyOrderContactFlag}">
					<nobr><fmt:message key="fields.shipto.daily.order.status"/>: &nbsp;</nobr>
				</td>
				<td>
					<html:select property="dailyOrderStatusCd" disabled="${form.dailyOrderStatusCd == 4}" style="width: 150px">
						<c:forEach var="currStatus" varStatus="status" items="${form.dailyOrderStatusList}">
							<c:if test="${currStatus.dailyOrderStatusTxPK.dailyOrderStatusBs.dailyOrderStatusCd != 4 || form.dailyOrderStatusCd == 4}">
								<html:option value="${currStatus.dailyOrderStatusTxPK.dailyOrderStatusBs.dailyOrderStatusCd}">
									<c:out value="${currStatus.txt}"/>
								</html:option>
							</c:if>
						</c:forEach>
					</html:select>
					<c:if test="${form.dailyOrderStatusCd != 4}">
						&nbsp;<input type="button" id="btnChangeDailyOrderStatus" class="ViewButtonStyle" value=" Change ">
					</c:if>
				</c:if>
			</td>
			<td class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>"  onClick="submitWithAction('reset')">
			</td>
		</tr>
		</table>
	</div>

<br/>

</html:form>
<%-- END $RCSfile: BestellungTile.jsp,v $ --%>

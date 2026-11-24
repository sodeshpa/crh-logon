var mgrStatic;
var mapTabUpdateRunner;
var truckMarker;
var truckPlateNo;
var shiptoCoordLat;
var shiptoCoordLon;
var shippCoordLat;
var shippCoordLon;
var mapDrawer;
//load dynamic objects (trucks, trailers, etc)from db. get static objects from struts form. 
function loadStaticMapObjects() {
	mgrStatic = new MarkerManager(gmap);

	shippCoordLat = getShippCoordLat();
	shippCoordLon = getShippCoordLon();
	shiptoCoordLat = getShiptoCoordLat();
	shiptoCoordLon = getShiptoCoordLon();
	mgrStatic.addMarker(getShipPointMarker(gmap, shippCoordLat, shippCoordLon, getShippName()));
	mgrStatic.addMarker(getPlantMarker(gmap, getPlantCoordLat(), getPlantCoordLon(), getPlantName()));
	mgrStatic.addMarker(getShipToMarker(gmap, shiptoCoordLat, shiptoCoordLon, getShiptoName()));

	if ( !isOrderItrackAvailable && (shippCoordLat !== 0) && (shippCoordLon !== 0) && (shiptoCoordLat !== 0) && (shiptoCoordLon !== 0) ) {
		mapDrawer = new Logon.MapDrawer();
		mapDrawer.redrawAndRepositionMap(gmap, shippCoordLat, shippCoordLon, shiptoCoordLat, shiptoCoordLon);
	}
	mgrStatic.showMarkers();
}

//load dynamic objects (trucks, trailers, etc)from db. get static objects from struts form. 
function loadDynamicMapObjects(orderId) {
	LiveKarteAjaxService.fetchDynamicMapObjects(orderId, loadDynamicMapObjectsReturn);
}

//return function from AJAX service
function loadDynamicMapObjectsReturn(dynamicObjDList) {
	setupDynamicMarkersOD(dynamicObjDList);
}

function calculateArrivalDate(position) {
	if (!!position && !!orderId) {
		LiveKarteAjaxService.calculateArrivalDate(orderId, position.time, position.latitude, position.longitude, displayArrivalDate);
	}
}

function displayArrivalDate(arrival) {
	let eta = document.getElementById('eta');
	if (!eta) {
		eta = document.createElement('div');
		eta.id = 'eta';
		eta.className = 'formtitle';
		// append as last element 
		document.getElementById('map').parentElement.appendChild(eta);
	}
	eta.textContent = LitDict['orderdetail.log.prefix.eta'] + ': ' +
			(!!arrival ? new Date(arrival * 1000).format(dateTimeFormat) : 'n/a');
}

//draw dynamic objects
function setupDynamicMarkersOD(dynamicObjDList) {
	var mgrDynamic = new MarkerManager(gmap);
	var trailerSetFlag = false;
	
	// flags to identify truck icon color
	var shippingFlag = false; //truck on the way (112)
	var shPointFlag = false; //at shipping point (109)
	var shiptoFlag = false; //arrived at shipto (113)
	var unloadedFlag = false; //unloaded (172)
	var trafficJamStartFlag = false; //traffic jam start (210)
	var trafficJamEndFlag = false; //traffic jam end (211)
	var dynamicObject;
	
	for ( var i = 0; i < dynamicObjDList.length; i++) {
		dynamicObject = dynamicObjDList[i];
	
		//add Trailer 
		if ((trailerSetFlag === false) && (dynamicObject.trailerId != null)) {
			mgrDynamic.addMarker(getTrailerMarker(gmap, dynamicObject));
			trailerSetFlag = true;
		}

		//check truck status
		if (dynamicObject.bizeventTypeCd == BIZEVENT_TYPE_SHIPPING) {
			shippingFlag = true;
		}
		if (dynamicObject.bizeventTypeCd == BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT) {
			shPointFlag = true;
		}

		if (dynamicObject.bizeventTypeCd == BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT) {
			shiptoFlag = true;
		}
		if (dynamicObject.bizeventTypeCd == BIZEVENT_TYPE_LEAVE_SHIPTO) {
			unloadedFlag = true;
		}
		if (dynamicObject.bizeventTypeCd == BIZEVENT_TYPE_TRAFFIC_JAM_START) {
			//in case it is not a fist traffic jam
			if (trafficJamStartFlag) {
				trafficJamEndFlag = false;
			}
			trafficJamStartFlag = true;
		}
		if (dynamicObject.bizeventTypeCd == BIZEVENT_TYPE_TRAFFIC_JAM_END) {
			trafficJamEndFlag = true;
		}
		
		//draw the actual route the truck has been driving along 
		mgrDynamic.addMarker(getEventMarker(gmap, dynamicObject));
	}

	if (dynamicObjDList.length > 0) {
		//set truck icon
		dynamicObject = dynamicObjDList[dynamicObjDList.length - 1];
		var imageUrl = getTruckImageUrl(shippingFlag, shPointFlag, shiptoFlag, unloadedFlag, trafficJamStartFlag,
			trafficJamEndFlag, getLoadingTimestamp(), getUnloadingTimestamp());
		truckMarker = getTruckMarker(gmap, dynamicObject, imageUrl);
		mgrDynamic.addMarker(truckMarker);
		if (isOrderItrackAvailable) {
			if (!!mapTabUpdateRunner) {
				mapTabUpdateRunner.stopAll();
			}
			if (!!mapDrawer) {
				mapDrawer = null;
			}
			truckPlateNo = dynamicObject.truckPlateNo;
			manageTruckPosition();
		}
	}
	
	mgrDynamic.showMarkers();
}

function updateTruckPosition(position) {
	if (!mapDrawer) {
		mapDrawer = new Logon.MapDrawer();
		mapDrawer.redrawAndRepositionMap(gmap, position.latitude, position.longitude, shiptoCoordLat, shiptoCoordLon);
	}

	var pos = new google.maps.LatLng(position.latitude, position.longitude);
	truckMarker.setPosition(pos);
	calculateArrivalDate(position)
}

//get truck status icon name
function getTruckImageUrl(shippingFlag, shPointFlag, shiptoFlag, unloadedFlag, trafficJamStartFlag, trafficJamEndFlag, loadingTs, unloadingTs) {
	
	if (loadingTs != null) {
		var loadStatus = "";
		//set loading status
		if (unloadingTs == null) {
			loadStatus = "loaded";
		}
		else {
			loadStatus = "unloaded";
		}
		
		//geofence
		if ((shiptoFlag === true) && (unloadedFlag === false)) {
			return "livemap_truck_" + loadStatus + "_inside_geofence.png";
		}
		//route (on the way)
		if (shippingFlag === true) {
			return "livemap_truck_" + loadStatus + "_en_route.png";
		}
		//geofence
		if ((shPointFlag === true) && (shippingFlag === false)) {
			return "livemap_truck_" + loadStatus + "_inside_geofence.png";
		}
		//traffic jam
		if ((trafficJamStartFlag === true) && (trafficJamEndFlag === false)) {
			return "livemap_truck_" + loadStatus + "_traffic_jam.png";
		}
	}
	
	//FIXME valdas we need empty truck icon in case we cannot define status
	return "livemap_truck_partially_loaded_inside_geofence.png";
}


function getEventMarker(map, bizevent) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_event.png",
			  new google.maps.Size(10, 10),
			  new google.maps.Point(0,0),
			  new google.maps.Point(2, 10));
	
	var latlng = new google.maps.LatLng(bizevent.coordLat, bizevent.coordLon);
	var marker = new google.maps.Marker({
		position: latlng,
		icon: icon
		});
	
	google.maps.event.addListener(marker, "mouseover", function () {LiveKarteAjaxService.getBizeventDetail(bizevent.bizeventId,function(result) {
		var bizeventMapEntity = result.bizeventMapEntity;
		var content = '<table width=\'300\' style=\'border-radius: 1em 4em 1em 4em\'>'
			+ '<tr><td><span class=\'label\'>' + labelEventTime + ':</span> ' + result.eventTimeInUserTimeZone + '</td></tr>'
			+ '<tr><td><span class=\'label\'>' + labelEvent + ':</span> ' + bizeventMapEntity.bizeventTypeTxt + '</td></tr>'
			+ '<tr><td><span class=\'label\'>' + labelTruckName + ':</span> ' + bizeventMapEntity.truckName + '</td></tr>'
			+ '</table>';
		
		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(map, marker);
	});
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	return marker;
}

function manageTruckPosition() {
	mapTabUpdateRunner = new Ext.util.TaskRunner();
	updateTask = {
		run: function () {
			ITrackAjaxService.getLastPositionOfTheTruck(truckPlateNo, updateTruckPosition);
		},
		interval: AUTO_REFRESH_INTERVAL
	};
	mapTabUpdateRunner.start(updateTask);

	let logonPopupSelector=document.querySelector('#logonPopup');
	let mapTabSelector=document.querySelector('#loadMapTab');

	function callback (mutations, observer) {
		mapTabUpdateRunner.stopAll();
		observer.disconnect();
	}

	let mapTabObserver = new MutationObserver(callback);
	mapTabObserver.observe(logonPopupSelector, {attributes:true, attributeValue:"visibility"});
	mapTabObserver.observe(mapTabSelector, {attributes:true});
}

function getTruckMarker(map, bizevent, imageUrl) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=" + imageUrl,
			  new google.maps.Size(23, 23),
			  new google.maps.Point(0,0),
			  new google.maps.Point(2, 23));

	var latlng = new google.maps.LatLng(bizevent.coordLat, bizevent.coordLon);
	var marker = new google.maps.Marker({
		position: latlng,
		icon: icon
		});
	
	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table width='200' height='120' class='compact' border='0' cellpadding='0' cellspacing='0'>"
			+ "<tr><td><span class='label'>" + labelTruckName + ":</span> " + bizevent.truckName + " (" + bizevent.truckPlateNo + ")</td></tr>"
			+ "<tr><td><span class='label'>" + labelTruckType + ":</span> " + bizevent.truckTypeTxt +"</td></tr>"
			+ "<tr><td><span class='label'>" + labelDriver + ":</span> " + bizevent.driverName +"</td></tr>"
			+ "<tr><td><span class='label'>" + labelCarrier + ":</span> " + bizevent.truckCarrierName +"</td></tr>"
			+ "</table>";
		
		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(map, marker);
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	return marker;
}

function getTrailerMarker(map, bizevent) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_trailer_unloaded.png",
			  new google.maps.Size(23, 22),
			  new google.maps.Point(0,0),
			  new google.maps.Point(2, 22));
	
	var latlng = new google.maps.LatLng(bizevent.coordLat, bizevent.coordLon);
	var marker = new google.maps.Marker({
		position: latlng,
		icon: icon
		});
	
	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table width='200' height='120' >"
			+ "<tr><td><span class='label'>" + labelTrailerName + ":</span> " + bizevent.trailerName + "</td></tr>" 
			+ "<tr><td><span class='label'>" + labelTrailerPlateNo + ":</span> " + bizevent.trailerPlateNo + "</td></tr>" 
			+ "<tr><td><span class='label'>" + labelCarrier + ":</span> " + bizevent.trailerCarrierName + "</td></tr>" 
			+ "<tr><td><span class='label'>" + labelTruckName + ":</span> " + bizevent.truckName + " (" + bizevent.truckPlateNo + ")</td></tr>"
			+ "<tr><td><span class='label'>" + labelTruckType + ":</span> " + bizevent.truckTypeTxt +"</td></tr>"
			+ "</table>";
		
		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(map, marker);
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});

	return marker;
}

function getPlantMarker(map, plantCoordLat, plantCoordLon, plantName) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_ordermap_plant.png",
			  new google.maps.Size(19, 19),
			  new google.maps.Point(0,0),
			  new google.maps.Point(2, 19));
	
	var latlng = new google.maps.LatLng(plantCoordLat, plantCoordLon);
	var marker = new google.maps.Marker({
		position: latlng,
		icon: icon
		});
	
	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table >"
			+ "<tr><td><span class='label'>" + labelPlantName + ":</span> " + plantName + "</td></tr>"
			+ "</table>";
		
		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(map, marker);
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	return marker;
}

function getShipPointMarker(map, shippCoordLat, shippCoordLon, shippName) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_ordermap_shipping_point.png",
			  new google.maps.Size(23, 24),
			  new google.maps.Point(0,0),
			  new google.maps.Point(2, 24));
		
	var latlng = new google.maps.LatLng(shippCoordLat, shippCoordLon);
	var marker = new google.maps.Marker({
		position: latlng,
		icon: icon
		});

	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table>"
			+ "<tr><td><span class='label'>" + labelShPoint + ":</span> " + shippName + "</td></tr>"  
			+ "</table>";
		
		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(map, marker);
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});

	return marker;
}

function getShipToMarker(map, shiptoCoordLat, shiptoCoordLon, shiptoName) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_shipto.png",
			  new google.maps.Size(21, 21),
			  new google.maps.Point(0,0),
			  new google.maps.Point(2, 21));
	
	var latlng = new google.maps.LatLng(shiptoCoordLat, shiptoCoordLon);
	var marker = new google.maps.Marker({
		position: latlng,
		icon: icon
		});
	
	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table>"
			+ "<tr><td><span class='label'>" + labelShipto + ":</span> " + shiptoName + "</td></tr>"
			+ "</table>";
		
		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(map, marker);
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});

	return marker; 
}

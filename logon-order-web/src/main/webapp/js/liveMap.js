// draw static objects
function setupStaticMarkers() {
	
	if (mgrStatic == null) {
		mgrStatic = new MarkerManager(gmap);
	}
	
	var isPlantChecked = document.getElementById('legendPlant').checked;
	var isShPointChecked = document.getElementById('legendShPoint').checked;
	var isShiptoChecked = document.getElementById('legendShipto').checked;
	
	// temporary maps for keeping info for markers
	var plantMap = {};
	var plantOrderNumMap = {};
	var shiptoMap = {};
	var shiptoOrderNumMap = {};
	var shPointMap = {};
	var shPointOrderNumMap = {};
	
	//marker arrays
	var plantCount = 0;
	var shiptoCount = 0;
	var shPointCount = 0;
	plantArray = [];
	shiptoArray = [];
	shiptoGeoArray = [];
	shPointArray = [];
	shPointGeoArray = [];
	
	// fill temporary maps only with unique objects
	for (var key in staticObjectMap) {
		var orderFilter = staticObjectMap[key];
		var tmpArray = [];
		//plant markers
		if (typeof(plantMap[orderFilter.productionPlantCd]) == "undefined") {
			plantMap[orderFilter.productionPlantCd] = orderFilter;
			tmpArray[0] = orderFilter.orderNumber + "(" + orderFilter.materialTxt + ")";
			plantOrderNumMap[orderFilter.productionPlantCd] = tmpArray;
		}
		else {
			tmpArray = plantOrderNumMap[orderFilter.productionPlantCd];
			tmpArray[tmpArray.length] = orderFilter.orderNumber + "(" + orderFilter.materialTxt + ")";
			plantOrderNumMap[orderFilter.productionPlantCd] = tmpArray;
		}
	
		//shipping point markers
		if (typeof(shPointMap[orderFilter.shippingpointId]) == "undefined") {
			shPointMap[orderFilter.shippingpointId] = orderFilter;
			tmpArray = [];
			tmpArray[0] = orderFilter.orderNumber + "(" + orderFilter.materialTxt + ")";
			shPointOrderNumMap[orderFilter.shippingpointId] = tmpArray;
		}
		else {
			tmpArray = shPointOrderNumMap[orderFilter.shippingpointId];
			tmpArray[tmpArray.length] = orderFilter.orderNumber + "(" + orderFilter.materialTxt + ")";
			shPointOrderNumMap[orderFilter.shippingpointId] = tmpArray;
		}
	
		//shipto markers	
		if (typeof(shiptoMap[orderFilter.shiptoSapBpId]) == "undefined") {
			shiptoMap[orderFilter.shiptoSapBpId] = orderFilter;
			tmpArray = [];
			tmpArray[0] = orderFilter.orderNumber + "(" + orderFilter.materialTxt + ")";
			shiptoOrderNumMap[orderFilter.shiptoSapBpId] = tmpArray;
		}
		else {
			tmpArray = shiptoOrderNumMap[orderFilter.shiptoSapBpId];
			tmpArray[tmpArray.length] = orderFilter.orderNumber + "(" + orderFilter.materialTxt + ")";
			shiptoOrderNumMap[orderFilter.shiptoSapBpId] = tmpArray;
		}

	}
	
	
	//added markers
	var geofence;
	for (key in plantMap) {
		var plantMarker = getPlantMarker(plantMap[key], plantOrderNumMap[key]);
		//mgrStatic.addMarker(plantMarker, 0, 17);
		plantArray[plantCount] = plantMarker;
		plantCount++;
	}
	for (key in shiptoMap) {
		var order = shiptoMap[key];
		var shiptoMarker = getShipToMarker(order, shiptoOrderNumMap[key]);
		shiptoArray[shiptoCount] = shiptoMarker;
		shiptoCount++;
		//draw geofence around marker 
		if (typeof(order.shiptoGeofenceRadius) != "undefined") {
			geofence = drawCircle(order.shiptoCoordLat, order.shiptoCoordLon, order.shiptoGeofenceRadius);
			shiptoGeoArray.push(geofence);
			geofence.setMap(gmap);
		}
	}
	for (key in shPointMap) {
		var orderShippingPoint = shPointMap[key];
		var shPointMarker = getShipPointMarker(orderShippingPoint, shPointOrderNumMap[key]);
		shPointArray[shPointCount] = shPointMarker;
		shPointCount++;
		//draw geofence around marker 
		if (typeof(orderShippingPoint.shippGeofenceRadius) != "undefined") {
			geofence = drawCircle(orderShippingPoint.shippCoordLat, orderShippingPoint.shippCoordLon, orderShippingPoint.shippGeofenceRadius);
			shPointGeoArray.push(geofence);
			geofence.setMap(gmap);
		}
	}
	
	//hide/show markers according to legend selection 
	checkStaticMapObjects();
}


//draw dynamic objects
function setupDynamicMarkers() {
	
	if (mgrDynamic == null) {
		mgrDynamic = new MarkerManager(gmap);
	}
	
	var isEventChecked = document.getElementById('legendEvent').checked;
	var isTruckChecked = document.getElementById('legendTruck').checked;
	var isTrailerChecked = document.getElementById('legendTrailer').checked;
	var isContianerChecked = document.getElementById('legendContainer').checked;
	var isWagonChecked = document.getElementById('legendWagon').checked;
	var isRouteChecked = document.getElementById('legendRoute').checked;
	//marker arrays
	var truckCount = 0;
	var trailerCount = 0;
	var containerCount = 0;
	var wagonCount = 0;
	var actualRouteCount = 0;
	var orderEventCount = 0;
	var routeEventArrayCount = 0;
	
	
	truckArray = [];
	trailerArray = [];
	containerArray = [];
	wagonArray = [];
	//array of actual routes (of orderEventArray)
	actualRouteArray = [];
	//array of actual routes (of orderEventArray), but only the ones coming from the monitoring table
	actualRouteMainArray = [];
	//array of red + blue dots of one route (one order)
	var orderEventArray = [];
	//array of dynamic objects from dynamicObjectList to draw planned routes (blue one)
	routeEventArray = [];

	
	var shippingFlag = false; //truck on the way (112)
	var shPointFlag = false; //at shipping point (109)
	var shiptoFlag = false; //arrived at shipto (113)
	var unloadedFlag = false; //unloaded (172)
	var trafficJamStartFlag = false; //traffic jam start (210)
	var trafficJamEndFlag = false; //traffic jam end (211)
	
	if (dynamicObjectList) {
		var previousDynObj = dynamicObjectList[0];
	
		var orderId;
		if (typeof(dynamicObjectList[0]) != "undefined") {
			//set first dynamic object
			orderId = dynamicObjectList[0].orderId;
		}
		
		for ( var i = 0; i < dynamicObjectList.length; i++) {
			var currentDynObj = dynamicObjectList[i];
			var eventMarker;
			if (currentDynObj.bizeventTypeCd == BIZEVENT_TYPE_POSITION) { 
				//draw the actual route the truck has been driving along (red dots)
				eventMarker = getEventMarker(previousDynObj);
			}
			else {
				//display Non-Geoposition-Events with a blue dots
				eventMarker = getNonGeoEventMarker(previousDynObj);
			}
	
			orderEventArray[orderEventCount] = eventMarker;
			orderEventCount++; 
			
			var staticObject;
			orderId = currentDynObj.orderId;
			if (orderId && staticObjectMap) {
				staticObject = staticObjectMap[orderId];
			}
			var truckMarker;
			//the last dynamic object in the list
			if (staticObject && i == (dynamicObjectList.length - 1)) {
				var imageUrl = getTruckImageUrl(shippingFlag, shPointFlag, shiptoFlag, unloadedFlag, trafficJamStartFlag, trafficJamEndFlag, staticObject.loadingTimestamp, staticObject.unloadingTimestamp);	
				truckMarker = getTruckMarker(currentDynObj, staticObject, imageUrl);
				truckArray[truckCount] = truckMarker;
				truckCount++;
				
				//add markers for the Trailers
				trailerCount = setupTrailerMarkers(dynamicObjectList, orderId, staticObject, i, trailerCount);
				
				//add markers for the Containers
				containerCount = setupContainerMarkers(dynamicObjectList, orderId, staticObject, i, containerCount);
				
				//add markers for the Wagons
				wagonCount = setupWagonMarkers(dynamicObjectList, orderId, staticObject, i, wagonCount);
				
				//array of actual routes (red dots)
				actualRouteMainArray[actualRouteCount] = orderEventArray;
				actualRouteCount++; 
			}
			//draw trucks and trailers
			else if (staticObject && orderId != currentDynObj.orderId) {
				var truckImageUrl = getTruckImageUrl(shippingFlag, shPointFlag, shiptoFlag, unloadedFlag, trafficJamStartFlag, trafficJamEndFlag, staticObject.loadingTimestamp, staticObject.unloadingTimestamp);
				truckMarker = getTruckMarker(previousDynObj, staticObject, truckImageUrl);
				truckArray[truckCount] = truckMarker;
				truckCount++;
				
				//add markers for the Trailers
				trailerCount = setupTrailerMarkers(dynamicObjectList, orderId, staticObject, i, trailerCount);
				
				//add markers for the Containers
				containerCount = setupContainerMarkers(dynamicObjectList, orderId, staticObject, i, containerCount);
				
				//add markers for the Wagons
				wagonCount = setupWagonMarkers(dynamicObjectList, orderId, staticObject, i, wagonCount);
				
				orderId = currentDynObj.orderId;
				
				//reset flags
				shippingFlag = false;
				shPointFlag = false;
				shiptoFlag = false;
				unloadedFlag = false;   
				
				//array of actual routes (red dots + blue dots)
				actualRouteMainArray[actualRouteCount] = orderEventArray;
				actualRouteCount++; 
				//array of red dots of one route (one order)
				orderEventCount = 0;
				orderEventArray = [];
				
				routeEventArray[routeEventArrayCount] = previousDynObj;
				routeEventArrayCount++;
			}
			
			
			//check truck status
			if (previousDynObj.bizeventTypeCd == BIZEVENT_TYPE_SHIPPING) {
				shippingFlag = true;
			}
			if (previousDynObj.bizeventTypeCd == BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT) {
				shPointFlag = true;
			}
			if (previousDynObj.bizeventTypeCd == BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT) {
				shiptoFlag = true;
			}
			if (previousDynObj.bizeventTypeCd == BIZEVENT_TYPE_LEAVE_SHIPTO) {
				unloadedFlag = true;
			}
			if (previousDynObj.bizeventTypeCd == BIZEVENT_TYPE_TRAFFIC_JAM_START) {
				//in case it is not a fist traffic jam
				if (trafficJamStartFlag) {
					trafficJamEndFlag = false;
				}
				trafficJamStartFlag = true;
			}
			if (previousDynObj.bizeventTypeCd == BIZEVENT_TYPE_TRAFFIC_JAM_END) {
				trafficJamEndFlag = true;
			}
			
			previousDynObj = currentDynObj;
		}
	}
	
	//hide/show markers according to legend selection
	checkDynamicMapObjects();
	//need to check rout because it is not a part of dynamic objects.
	checkMapRoute();
}


// puts a trailer marker at the latest position
function setupTrailerMarkers(dynamicObjectList, orderId, order, j, trailerCount) {
	for ( var i = j; i < dynamicObjectList.length; i++) {
		if (orderId == dynamicObjectList[i].orderId) {
			if (dynamicObjectList[i].trailerId != null) {
				var trailerMarker = getTrailerMarker(dynamicObjectList[i], order);
				mgrDynamic.addMarker(trailerMarker);
				trailerArray[trailerCount] = trailerMarker;
				trailerCount++;
				return trailerCount;
			}
		}
	}
	return trailerCount;
}

//puts a container marker at the latest position
function setupContainerMarkers(dynamicObjectList, orderId, order, j, containerCount) {
	for ( var i = j; i < dynamicObjectList.length; i++) {
		if (orderId == dynamicObjectList[i].orderId) {
			if (dynamicObjectList[i].containerId != null) {
				var marker = getContainerMarker(dynamicObjectList[i], order);
				mgrDynamic.addMarker(marker);
				containerArray[containerCount] = marker;
				containerCount++;
				return containerCount;
			}
		}
	}
	return containerCount;
}

//puts a wagon marker at the latest position
function setupWagonMarkers(dynamicObjectList, orderId, order, j, wagonCount) {
	for ( var i = j; i < dynamicObjectList.length; i++) {
		if (orderId == dynamicObjectList[i].orderId) {
			if (dynamicObjectList[i].wagonId != null) {
				var marker = getWagonMarker(dynamicObjectList[i], order);
				mgrDynamic.addMarker(marker);
				wagonArray[wagonCount] = marker;
				wagonCount++;
				return wagonCount;
			}
		}
	}
	return wagonCount;
}

//-----START OF TRAILER RELATED PART
function showTrailerInfoWindow(vehicleInputData) {
	var bizevent = vehicleInputData.bizevent;
	var order = vehicleInputData.order;
	var marker = vehicleInputData.marker;
	var deliveryToDat = vehicleInputData.deliveryToDat;
	
	var content = "<table class='compact'  width='300' height='150'  border='0' cellpadding='0' cellspacing='0'>"
	+ "<tr><td><span class='label'>" + labelTrailerName + "</span></td></tr>"
	+ "<tr><td>" + bizevent.trailerName + " " + bizevent.trailerPlateNo + "</td></tr>"
	+ "<tr><td><hr width='100%' /></td></tr>"
	+ "<tr><td><span class='label'>" + labelOrderNumber + ":</span> " + order.orderNumber + "</td></tr>" 
	+ "<tr><td>" + order.materialTxt + "</td></tr>"
	+ "<tr><td>" + order.shiptoName1 + "</td></tr>"
	+ "<tr><td>" + labelDeliveryTo + ": " + deliveryToDat + "</td></tr>"
	+ "</table>";
	
	marker.infoWindow.setOptions({content: content,
		  disableAutoPan: true
	});
	marker.infoWindow.open(gmap, marker);
}

//ajax response callback function
function getBizeventTrailerReturn(bizeventResponse) {
	if (bizeventResponse) {
		bizeventMap[bizeventResponse.bizeventId] = bizeventResponse;
		trailerData[bizeventResponse.bizeventId].bizevent = bizeventResponse;
		showTrailerInfoWindow(truckData[bizeventResponse.bizeventId]);
	}
}

function getTrailerMarker(vehicleCoordinates, order) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_trailer_unloaded.png",
			  new google.maps.Size(23, 22),
			  new google.maps.Point(0,0),
			  new google.maps.Point(12, 11));
	
	var latlng = new google.maps.LatLng(vehicleCoordinates.coordLat, vehicleCoordinates.coordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	});
	if(!marker.infoWindow){
		marker.infoWindow = new google.maps.InfoWindow();
	}
	
	var deliveryToDat = "";
	if ( (typeof(order.deliveryTo) != "undefined") && (order.deliveryTo != null) ) {
		deliveryToDat = order.deliveryTo;
	}
	
	trailerData[vehicleCoordinates.bizeventId] = {
			marker: marker,
			deliveryToDat: deliveryToDat,
			order: order
	};
	
	google.maps.event.addListener(marker, "mouseover", function() {
		if (typeof(bizeventMap[vehicleCoordinates.bizeventId]) == "undefined") {
			LiveKarteAjaxService.getBizevent(vehicleCoordinates, getBizeventTrailerReturn);
		}
		else {
			if (trailerData[vehicleCoordinates.bizeventId].bizevent === undefined
				|| trailerData[vehicleCoordinates.bizeventId].bizevent === null) {
				trailerData[vehicleCoordinates.bizeventId].bizevent = bizeventMap[vehicleCoordinates.bizeventId];
			}
			showTrailerInfoWindow(trailerData[vehicleCoordinates.bizeventId]);
		}
	});
	 
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	google.maps.event.addListener(marker, "click", function() {
		 showPopup("../auftrag/OrderDetailEntry.do?orderid=" + vehicleCoordinates.orderId, labelLoadingOrder);
	});

	return marker;
}

//-----END OF TRAILER RELATED PART

//-----START OF CONTAINER RELATED PART
function showContainerInfoWindow(vehicleInputData) {
	
	var bizevent = vehicleInputData.bizevent;
	var order = vehicleInputData.order;
	var marker = vehicleInputData.marker;
	var deliveryToDat = vehicleInputData.deliveryToDat;
	
	var content = "<table class='compact'  width='300' height='150'  border='0' cellpadding='0' cellspacing='0'>"
		+ "<tr><td><span class='label'>" + labelContainerName + "</span></td></tr>"
		+ "<tr><td>" + bizevent.containerName + " " + bizevent.containerNo + "</td></tr>"
		+ "<tr><td><hr width='100%' /></td></tr>"
		+ "<tr><td><span class='label'>" + labelOrderNumber + ":</span> " + order.orderNumber + "</td></tr>" 
		+ "<tr><td>" + order.materialTxt + "</td></tr>"
		+ "<tr><td>" + order.shiptoName1 + "</td></tr>"
		+ "<tr><td>" + labelDeliveryTo + ": " + deliveryToDat + "</td></tr>"
		+ "</table>";
		
	marker.infoWindow.setOptions({content: content,
		  disableAutoPan: true
	});
	marker.infoWindow.open(gmap, marker);
}

//ajax response callback function
function getBizeventContainerReturn(bizeventResponse) {
	if (bizeventResponse) {
		bizeventMap[bizeventResponse.bizeventId] = bizeventResponse;
		containerData[bizeventResponse.bizeventId].bizevent = bizeventResponse;
		showContainerInfoWindow(containerData[bizeventResponse.bizeventId]);
	}
}

function getContainerMarker(vehicleCoordinates, order) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_container_unloaded.png",
			  new google.maps.Size(28, 17),
			  new google.maps.Point(0,0),
			  new google.maps.Point(14, 9));
	
	var latlng = new google.maps.LatLng(vehicleCoordinates.coordLat, vehicleCoordinates.coordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	  });
	if(!marker.infoWindow){
		marker.infoWindow = new google.maps.InfoWindow();
	}
	
	var deliveryToDat = "";
	if ( (typeof(order.deliveryTo) != "undefined") && (order.deliveryTo != null) ) {
		deliveryToDat = order.deliveryTo;
	}
	
	containerData[vehicleCoordinates.bizeventId] = {
			marker: marker,
			deliveryToDat: deliveryToDat,
			order: order
	};
	
	google.maps.event.addListener(marker, "mouseover", function() {
		if (typeof(bizeventMap[vehicleCoordinates.bizeventId]) == "undefined") {
			LiveKarteAjaxService.getBizevent(vehicleCoordinates, getBizeventContainerReturn);
		}
		else {
			if (containerData[vehicleCoordinates.bizeventId].bizevent === undefined
				|| containerData[vehicleCoordinates.bizeventId].bizevent === null) {
				containerData[vehicleCoordinates.bizeventId].bizevent = bizeventMap[vehicleCoordinates.bizeventId];
			}
			showContainerInfoWindow(containerData[vehicleCoordinates.bizeventId]);
		}
	});
	
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	google.maps.event.addListener(marker, "click", function() {
		 showPopup("../auftrag/OrderDetailEntry.do?orderid=" + vehicleCoordinates.orderId, labelLoadingOrder);
	});
  
	return marker; 
}
//-----END OF CONTAINER RELATED PART

//-----START OF WAGON RELATED PART
function showWagonDataInfoWindow(vehicleInputData) {
	var bizevent = vehicleInputData.bizevent;
	var order = vehicleInputData.order;
	var marker = vehicleInputData.marker;
	var deliveryToDat = vehicleInputData.deliveryToDat;
	
	var content = "<table class='compact'  width='300' height='150'  border='0' cellpadding='0' cellspacing='0'>"
		+ "<tr><td><span class='label'>" + labelWagonName + "</span></td></tr>"
		+ "<tr><td>" + bizevent.wagonNo + "</td></tr>"
		+ "<tr><td><hr width='100%' /></td></tr>"
		+ "<tr><td><span class='label'>" + labelOrderNumber + ":</span> " + order.orderNumber + "</td></tr>" 
		+ "<tr><td>" + order.materialTxt + "</td></tr>"
		+ "<tr><td>" + order.shiptoName1 + "</td></tr>"
		+ "<tr><td>" + labelDeliveryTo + ": " + deliveryToDat + "</td></tr>"
		+ "</table>";
		
	marker.infoWindow.setOptions({content: content,
		  disableAutoPan: true
	});
	marker.infoWindow.open(gmap, marker);
}

function getBizeventWagonReturn(bizeventResponse) {
	bizeventMap[bizeventResponse.bizeventId] = bizeventResponse;
	wagonData[bizeventResponse.bizeventId].bizevent = bizeventResponse;
	showWagonDataInfoWindow(wagonData[bizeventResponse.bizeventId]);
}

function getWagonMarker(vehicleCoordinates, order) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_railcar_unloaded.png",
			  new google.maps.Size(34, 17),
			  new google.maps.Point(0,0),
			  new google.maps.Point(17, 9));
	
	var latlng = new google.maps.LatLng(vehicleCoordinates.coordLat, vehicleCoordinates.coordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	});
	if(!marker.infoWindow){
		marker.infoWindow = new google.maps.InfoWindow();
	}
	
	var deliveryToDat = "";
	if ( (typeof(order.deliveryTo) != "undefined") && (order.deliveryTo != null) ) {
		deliveryToDat = order.deliveryTo;
	}
	
	wagonData[vehicleCoordinates.bizeventId] = {
			marker: marker,
			deliveryToDat: deliveryToDat,
			order: order
	};
	
	google.maps.event.addListener(marker, "mouseover", function() {
		if (typeof(bizeventMap[vehicleCoordinates.bizeventId]) == "undefined") {
			LiveKarteAjaxService.getBizevent(vehicleCoordinates, getBizeventWagonReturn);
		}
		else {
			if (wagonData[vehicleCoordinates.bizeventId].bizevent === undefined
				|| wagonData[vehicleCoordinates.bizeventId].bizevent === null) {
				wagonData[vehicleCoordinates.bizeventId].bizevent = bizeventMap[vehicleCoordinates.bizeventId];
			}
			showWagonDataInfoWindow(wagonData[vehicleCoordinates.bizeventId]);
		}
	});
	
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	google.maps.event.addListener(marker, "click", function() {
		 showPopup("../auftrag/OrderDetailEntry.do?orderid=" + vehicleCoordinates.orderId, labelLoadingOrder);
	});

	return marker;
}
//-----END OF WAGON RELATED PART

//-----START OF TRUCK RELATED PART
// get truck status icon name
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

function showTruckInfoWindow(vehicleInputData) {
	
	var bizevent = vehicleInputData.bizevent;
	var order = vehicleInputData.order;
	var marker = vehicleInputData.marker;
	var deliveryToDat = vehicleInputData.deliveryToDat;
	
	var content = "<table class='compact'  width='300' height='150'  border='0' cellpadding='0' cellspacing='0'>"
	+ "<tr><td><span class='label'>" + labelTruckName + "</span></td></tr>"
	+ "<tr><td>" + bizevent.truckName + " " + bizevent.truckPlateNo + "</td></tr>"
	+ "<tr><td>" + bizevent.driverName +"</td></tr>"
	+ "<tr><td><hr width='100%' /></td></tr>"
	+ "<tr><td><span class='label'>" + labelOrderNumber + ":</span> " + order.orderNumber + "</td></tr>" 
	+ "<tr><td>" + order.materialTxt + "</td></tr>"
	+ "<tr><td>" + order.shiptoName1 + "</td></tr>"
	+ "<tr><td>" + labelDeliveryTo + ": " + deliveryToDat + "</td></tr>"
	+ "</table>";

	marker.infoWindow.setOptions({content: content,
		  disableAutoPan: true
	});
	marker.infoWindow.open(gmap, marker);
}

//ajax response callback function
function getBizeventTruckReturn(bizeventResponse) {
	if (bizeventResponse) {
		bizeventMap[bizeventResponse.bizeventId] = bizeventResponse;
		truckData[bizeventResponse.bizeventId].bizevent = bizeventResponse;
		showTruckInfoWindow(truckData[bizeventResponse.bizeventId]);
	}
}

function getTruckMarker(vehicleCoordinates, order, imageUrl) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=" + imageUrl,
			  new google.maps.Size(23, 23),
			  new google.maps.Point(0,0),
			  new google.maps.Point(12, 12));
	
	var latlng = new google.maps.LatLng(vehicleCoordinates.coordLat, vehicleCoordinates.coordLon);
	var marker = new google.maps.Marker({
		position: latlng,
		icon: icon
	});
	if(!marker.infoWindow){
		marker.infoWindow = new google.maps.InfoWindow();
	}
	
	var deliveryToDat = "";
	if (typeof(order.deliveryTo) != "undefined") {
		deliveryToDat = order.deliveryTo;
	}
	
	truckData[vehicleCoordinates.bizeventId] = {
			marker: marker,
			deliveryToDat: deliveryToDat,
			order: order
	};
	
	google.maps.event.addListener(marker, "mouseover", function() {
		if (typeof(bizeventMap[vehicleCoordinates.bizeventId]) == "undefined") {
			LiveKarteAjaxService.getBizevent(vehicleCoordinates, getBizeventTruckReturn);
		}
		else {
			if (truckData[vehicleCoordinates.bizeventId].bizevent === undefined
				|| truckData[vehicleCoordinates.bizeventId].bizevent === null) {
				truckData[vehicleCoordinates.bizeventId].bizevent = bizeventMap[vehicleCoordinates.bizeventId];
			}
			showTruckInfoWindow(truckData[vehicleCoordinates.bizeventId]);
		}
	});

	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});

	google.maps.event.addListener(marker, "click", function() {
		showPopup("../auftrag/OrderDetailEntry.do?orderid=" + vehicleCoordinates.orderId, labelLoadingOrder);
	});
	
	return marker;
}
//-----END OF TRUCK RELATED PART

//-----START OF ACTUAL ROUTE RELATED PART
function showEventInfoWindow(vehicleInputData) {
	if (vehicleInputData) {
		return;
	}
	
	var bizevent = vehicleInputData.bizevent;
	var eventTs = vehicleInputData.eventTs;
	var marker = vehicleInputData.marker;
	
	var speed = "";
	if (typeof(bizevent.speed) != "undefined") {
		speed = bizevent.speed + "km/h ";
	} 
	
	var course = "";
	if (typeof(bizevent.course) != "undefined") {
		course = bizevent.course;
	}
	
	var content = "<table class='compact'  width='300'>"
			+ "<tr><td><span class='label'>" + labelEvent + ":</span></td></tr>"
			+ "<tr><td>" + bizevent.bizeventTypeTxt + "</td></tr>"
			+ "<tr><td>" + eventTs.format(dateFormat) + "</td></tr>"
			+ "<tr><td>" + speed  + course + "</td></tr>"
			+ "</table>";

	marker.infoWindow.setOptions({content: content,
		  disableAutoPan: true
	});
	marker.infoWindow.open(gmap, marker);
}

//ajax response callback function
function getBizeventReturn(bizeventResponse) {
	if (bizeventResponse) {
		bizeventMap[bizeventResponse.bizeventId] = bizeventResponse;
		eventData[bizeventResponse.bizeventId].bizevent = bizeventResponse;
		showEventInfoWindow(eventData[bizeventResponse.bizeventId]);
	}
}

function getEventMarker(vehicleCoordinates) {

	//Display Geoposition-Events with a red dot
	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_event.png",
			  new google.maps.Size(10,10),
			  new google.maps.Point(0,0),
			  new google.maps.Point(5, 5));
	
	var latlng = new google.maps.LatLng(vehicleCoordinates.coordLat, vehicleCoordinates.coordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	  });
	
	if(!marker.infoWindow){
		marker.infoWindow = new google.maps.InfoWindow();
	}
	
	eventData[vehicleCoordinates.bizeventId] = {
			marker: marker,
			eventTs: vehicleCoordinates.eventTs
	};
	
	google.maps.event.addListener(marker, "mouseover", function() {
		if (typeof(bizeventMap[vehicleCoordinates.bizeventId]) == "undefined") {
			LiveKarteAjaxService.getBizevent(vehicleCoordinates, getBizeventReturn);
		}
		else {
			if (eventData[vehicleCoordinates.bizeventId].bizevent === undefined
				|| eventData[vehicleCoordinates.bizeventId].bizevent === null) {
				eventData[vehicleCoordinates.bizeventId].bizevent = bizeventMap[vehicleCoordinates.bizeventId];
			}
			showEventInfoWindow(eventData[vehicleCoordinates.bizeventId]);
		}
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	google.maps.event.addListener(marker, "click", function() {
		if (typeof(orderActualRouteMapping[vehicleCoordinates.orderId]) == "undefined") {
			LiveKarteAjaxService.getBizeventsForOrder(0, dynamicObjectListPagesize, vehicleCoordinates, getBizeventListReturn);
		}
		else {
			checkDynamicMapEventObjects();
		}
	});
	
	return marker;
}

//ajax response callback function
function getBizeventListReturn(bizeventList) {
	if (!bizeventList) {
		return;
	}
	var previousDynObj = bizeventList[0];

	var orderId;
	if (typeof(bizeventList[0]) != "undefined") {
		//set first dynamic object
		orderId = bizeventList[0].orderId;
	}
	var orderEventArray = [];
	var orderEventCount = 0;
	var actualRouteCount = 0;
	var routeEventArrayCount = 0;
	for ( var i = 0; i < bizeventList.length; i++) {
		var currentDynObj = bizeventList[i];
		var eventMarker;
		if (currentDynObj.bizeventTypeCd == BIZEVENT_TYPE_POSITION) { 
			//draw the actual route the truck has been driving along (red dots)
			eventMarker = getEventMarker(previousDynObj);
		}
		else {
			//display Non-Geoposition-Events with a blue dots
			eventMarker = getNonGeoEventMarker(previousDynObj);
		}

		orderEventArray[orderEventCount] = eventMarker;
		orderEventCount++; 
		
		orderId = currentDynObj.orderId;
		
		//the last dynamic object in the list
		if (i == (bizeventList.length - 1)) {
			//array of actual routes (red dots)
			actualRouteArray[actualRouteCount] = orderEventArray;
			actualRouteCount++; 
		}
		//draw trucks and trailers
		else if (orderId != currentDynObj.orderId) {
			//array of actual routes (red dots + blue dots)
			actualRouteArray[actualRouteCount] = orderEventArray;
			actualRouteCount++; 
			//array of red dots of one route (one order)
			orderEventCount = 0;
			orderEventArray = [];
			
			routeEventArray[routeEventArrayCount] = previousDynObj;
			routeEventArrayCount++;
		}
		
		previousDynObj = currentDynObj;
	}
	
	checkDynamicMapEventObjects();
}

function showNonGeoEventInfoWindow(vehicleInputData) {
	
	var bizevent = vehicleInputData.bizevent;
	var eventTs = vehicleInputData.eventTs;
	var marker = vehicleInputData.marker;
	
	var content = "<table class='compact' width='300'>"
		+ "<tr><td><span class='label'>" + labelEvent + ":</span></td></tr>"
		+ "<tr><td>" + bizevent.bizeventTypeTxt + "</td></tr>"
		+ "<tr><td>" + eventTs.format(dateFormat) + "</td></tr>"
		+ "</table>";

	marker.infoWindow.setOptions({content: content,
		  disableAutoPan: true
	});
	marker.infoWindow.open(gmap, marker);
}

//ajax response callback function
function getBizeventNonGeoEventReturn(bizeventResponse) {
	if (bizeventResponse) {
		bizeventMap[bizeventResponse.bizeventId] = bizeventResponse;
		eventData[bizeventResponse.bizeventId].bizevent = bizeventResponse;
		showNonGeoEventInfoWindow(eventData[bizeventResponse.bizeventId]);
	}
}

function getNonGeoEventMarker(vehicleCoordinates) {
 
	//Display Non-Geoposition-Events with a blue dot
	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_non_geo_event.png",
			  new google.maps.Size(10, 10),
			  new google.maps.Point(0,0),
			  new google.maps.Point(5, 5));
	
	var latlng = new google.maps.LatLng(vehicleCoordinates.coordLat, vehicleCoordinates.coordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	  });
	
	if(!marker.infoWindow){
		marker.infoWindow = new google.maps.InfoWindow();
	}
	
	eventData[vehicleCoordinates.bizeventId] = {
			marker: marker,
			eventTs: vehicleCoordinates.eventTs
	};
	
	google.maps.event.addListener(marker, "mouseover", function() {
		if (typeof(bizeventMap[vehicleCoordinates.bizeventId]) == "undefined") {
			LiveKarteAjaxService.getBizevent(vehicleCoordinates, getBizeventNonGeoEventReturn);
		}
		else {
			if (eventData[vehicleCoordinates.bizeventId].bizevent === undefined
				|| eventData[vehicleCoordinates.bizeventId].bizevent === null) {
				eventData[vehicleCoordinates.bizeventId].bizevent = bizeventMap[vehicleCoordinates.bizeventId];
			}
			showNonGeoEventInfoWindow(eventData[vehicleCoordinates.bizeventId]);
		}
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	google.maps.event.addListener(marker, "click", function() {
		if (typeof(orderActualRouteMapping[vehicleCoordinates.orderId]) == "undefined") {
			LiveKarteAjaxService.getBizeventsForOrder(0, dynamicObjectListPagesize,vehicleCoordinates, getBizeventListReturn);
		}
		else {
			checkDynamicMapEventObjects();
		}
	});
	
	return marker;
}
//-----END OF ACTUAL ROUTE RELATED PART

function getPlantMarker(order, orderNumberList) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_ordermap_plant.png",
			  new google.maps.Size(19, 19),
			  new google.maps.Point(0,0),
			  new google.maps.Point(10, 10));
	
	var latlng = new google.maps.LatLng(order.plantCoordLat, order.plantCoordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	  });
	
	var orderNumbers = "";
	for ( var i = 0; i < orderNumberList.length; i++) {
		orderNumbers = orderNumbers + "<nobr>"+ orderNumberList[i] + "</nobr>&nbsp;";
		
		if (i % 3 === 0) {
			orderNumbers = orderNumbers + "<br/>";
		}
	}
	
	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table width='400' >"
				+ "<tr><td><span class='label'>" + labelPlantName + ":</span></td></tr>"
				+ "<tr><td><span class='formtitle'>" + order.plantName + "</span></td></tr>"
				+ "<tr><td><span class='formtitle'>" + labelCurrentOrders + ": " + orderNumbers + "</span></td></tr>"
				+ "<tr><td>&nbsp;</td></tr>"
				+ "</table>";

		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(gmap, marker);
	});

	var removeEvent = google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});
	
	return marker;
}

function getShipToMarker(order, orderNumberList) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_shipto.png",
			  new google.maps.Size(21, 21),//exact size of the image - for center aligning
			  new google.maps.Point(0, 0),
			  new google.maps.Point(11, 11));//center of the image
	
	var latlng = new google.maps.LatLng(order.shiptoCoordLat, order.shiptoCoordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	  });

	var orderNumbers = "";
	for ( var i = 0; i < orderNumberList.length; i++) {
		orderNumbers = orderNumbers + "<nobr>"+ orderNumberList[i] + "</nobr>&nbsp;";
		
		if (i % 3 === 0) {
			orderNumbers = orderNumbers + "<br/>";
		}
	}
	
	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table width='400'>"
				+ "<tr><td><span class='label'>" + labelShipto + ":</span></td></tr>"
				+ "<tr><td><span class='formtitle'>" + order.shiptoName1 + "</span></td></tr>"
				+ "<tr><td><span class='formtitle'>" + labelCurrentOrders + ": " + orderNumbers + "</span></td></tr>"
				+ "<tr><td>&nbsp;</td></tr>"
				+ "</table>";

		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(gmap, marker);
	});
	
	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});

	return marker; 
}

function getShipPointMarker(order, orderNumberList) {

	var icon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_ordermap_shipping_point.png",
			  new google.maps.Size(23, 24),
			  new google.maps.Point(0,0),
			  new google.maps.Point(12, 12));
	
	var latlng = new google.maps.LatLng(order.shippCoordLat, order.shippCoordLon);
	var marker = new google.maps.Marker({
	  position: latlng,
	  icon: icon
	  });

	var orderNumbers = "";
	for ( var i = 0; i < orderNumberList.length; i++) {
		orderNumbers = orderNumbers + "<nobr>"+ orderNumberList[i] + "</nobr>&nbsp;";
		
		if (i % 3 === 0) {
			orderNumbers = orderNumbers + "<br/>";
		}
	}

	google.maps.event.addListener(marker, "mouseover", function() {
		var content = "<table width='400'>"
				+ "<tr><td><span class='label'>" + labelShPoint + ":</span> </td></tr>"  
				+ "<tr><td><span class='formtitle'>" + order.shippingpointName + "</span></td></tr>"  
				+ "<tr><td><span class='formtitle'>" + labelCurrentOrders + ": " + orderNumbers + "</span></td></tr>"
				+ "<tr><td>&nbsp;</td></tr>"
				+ "</table>";
				
		if(!marker.infoWindow){
			marker.infoWindow = new google.maps.InfoWindow();
		}
		marker.infoWindow.setOptions({content: content,
			  disableAutoPan: true
		});
		marker.infoWindow.open(gmap, marker);
	});

	google.maps.event.addListener(marker, "mouseout", function() {
		marker.infoWindow.close();
	});

	return marker;
}



/**
 * @param lat Latitude in degrees
 * @param lng Longitude in degrees
 * @param radius circleRadius of the circle in statute miles
 */
function drawCircle(lat, lng, circleRadius) {
	var d = circleRadius * 1;
	var latLng = new google.maps.LatLng(lat, lng);
	var circleOptions = {
		  strokeColor: "#FF0000",
		  strokeOpacity: 1,
		  strokeWeight: 2,
		  fillColor: "#000000",
		  fillOpacity: 0.25,
		  center: latLng,
		  radius: d
		};
	var	geofenceCircle = new google.maps.Circle(circleOptions);
	return geofenceCircle;
}



// check/uncheck all check boxes for a ginven filter
function selectAll(element, selected) {
	var el = document.getElementById(element).getElementsByTagName('input');
	for ( var i = 0; i < el.length; i++) {
		el[i].checked = selected;
	}
	//only refresh but not reload from the db
	refreshFilters();
}

//hide or show map legend
function hideShowLegend(visibility) {
	var divMap = Ext.getDom('map');
	if (visibility == 'hidden') {
		Ext.get("legendDiv").slideOut('r', {useDisplay: true});
		setTimeout(function(){
			gmap.checkResize();
		}, 500);
		
		//make 'hide' link invisible  and 'show' link visible 
		document.getElementById("hideLegendId").style.visibility = "hidden";
		document.getElementById("showLegendId").style.visibility = "visible";
	} else {
		
		Ext.get("legendDiv").show();
		Ext.getDom("legendDiv").style.position="static";
		gmap.checkResize();
		
		//make 'hide' link visible  and 'show' link invisible 
		document.getElementById("hideLegendId").style.visibility = "visible";
		document.getElementById("showLegendId").style.visibility = "hidden";
	}
}


// returns the size of the current window
function getWindowHeight() {
	var myHeight = 0;
	if (typeof (window.innerWidth) == 'number') {
		// Non-IE
		myHeight = window.innerHeight;
	} else if (document.documentElement
			&& (document.documentElement.clientHeight)) {
		// IE 6+ in 'standards compliant mode'
		myHeight = document.documentElement.clientHeight;
	} else if (document.body
			&& (document.body.clientHeight)) {
		// IE 4 compatible
		myHeight = document.body.clientHeight;
	}
	return myHeight;
}

//returns the size of the current window
function getWindowWidth() {
	var myWidth = 0;
	if (typeof (window.innerWidth) == 'number') {
		// Non-IE
		myWidth = window.innerWidth;
	} else if (document.documentElement
			&& (document.documentElement.clientWidth)) {
		// IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
	} else if (document.body
			&& (document.body.clientWidth)) {
		// IE 4 compatible
		myWidth = document.body.clientWidth;
	}
	return myWidth;
}

//hide/show route objects 
function checkMapRoute() {
	var isRouteChecked = document.getElementById('legendRoute').checked;
	if (isRouteChecked) {
		
		var routeCount = 0;
		var previousDynObj = dynamicObjectList[i];
		var orderId; 
		if (typeof(dynamicObjectList[0]) != "undefined") {
			//set first dynamic object
			orderId = dynamicObjectList[0].orderId;
		}
		
		var orderIdForStaticObject;
		var staticObject;
		// draw new routes
		var mapDrawer = new Logon.MapDrawer();
		var callback = function(response, status) {
			if (status != google.maps.DirectionsStatus.OK) {
				Logon.message.warning('Could not calculate route status=' + status);
			}
		};
		
		if (staticObjectMap) {
			for ( var i = 0; i < dynamicObjectList.length; i++) {
				var currentDynObj = dynamicObjectList[i];
				
				//the last event in the list of one order OR the last dynamic object in the whole list
				if (i == (dynamicObjectList.length - 1)) {
					orderIdForStaticObject = currentDynObj.orderId;
					staticObject = staticObjectMap[orderIdForStaticObject];
					if (staticObject) {
						mapDrawer.redrawAndRepositionMap(
								gmap,
								currentDynObj.coordLat,
								currentDynObj.coordLon,
								staticObject.shiptoCoordLat,
								staticObject.shiptoCoordLon,
								callback);
						routeCount++;
					}
				}
				else if (orderId != currentDynObj.orderId) {
					orderIdForStaticObject = previousDynObj.orderId;
					staticObject = staticObjectMap[orderIdForStaticObject];
					if (staticObject) {
						mapDrawer.redrawAndRepositionMap(
								gmap,
								previousDynObj.coordLat,
								previousDynObj.coordLon,
								staticObject.shiptoCoordLat,
								staticObject.shiptoCoordLon,
								callback);
						routeCount++;
					}
				}
			
				orderId = currentDynObj.orderId;
				previousDynObj = currentDynObj;
			}
		}
		centerMap();
	}
}

function centerMap() {
	
	var totalBounds = new google.maps.LatLngBounds();
	if ( (staticMarkerBounds) && 
			(!staticMarkerBounds.isEmpty()) ) {
		totalBounds.extend(staticMarkerBounds.getSouthWest());
		totalBounds.extend(staticMarkerBounds.getNorthEast());
	}
	if ( (dynamicMarkerBounds) && 
			(!dynamicMarkerBounds.isEmpty()) ) {
		totalBounds.extend(dynamicMarkerBounds.getSouthWest());
		totalBounds.extend(dynamicMarkerBounds.getNorthEast());
	}
	if (!totalBounds.isEmpty()) {
		gmap.fitBounds(totalBounds);
		gmap.autoZoom = true;
	}
}


function checkStaticMapObjects() {
	mgrStatic.clearMarkers();
	staticMarkerBounds = new google.maps.LatLngBounds();
	//clear geofences
	for ( var i = 0; i < shiptoGeoArray.length; i++) {
		shiptoGeoArray[i].setMap(null);
	}
	for ( var j = 0; j < shPointGeoArray.length; j++) {
		shPointGeoArray[j].setMap(null);
	}
	//shiptoGeoArray = [];
	
	
	var isPlantChecked = document.getElementById('legendPlant').checked;
	var isShPointChecked = document.getElementById('legendShPoint').checked;
	var isShiptoChecked = document.getElementById('legendShipto').checked;
	
	addNotHiddenMapObjects(mgrStatic, isPlantChecked, plantArray, staticMarkerBounds);
	addNotHiddenMapObjectsGeo(mgrStatic, isShiptoChecked, shiptoArray, shiptoGeoArray, staticMarkerBounds);
	addNotHiddenMapObjectsGeo(mgrStatic, isShPointChecked, shPointArray, shPointGeoArray, staticMarkerBounds);
	
	mgrStatic.showMarkers();
	centerMap();
}


function checkDynamicMapObjects() {
	mgrDynamic.clearMarkers();
	dynamicMarkerBounds = new google.maps.LatLngBounds();
	//clear passed route
	for ( var i = 0; i < passedRouteArray.length; i++) {
		passedRouteArray[i].setMap(null);
	}
	passedRouteArray = [];
	
	var isTruckChecked = document.getElementById('legendTruck').checked;
	var isTrailerChecked = document.getElementById('legendTrailer').checked;
	var isContianerChecked = document.getElementById('legendContainer').checked;
	var isWagonChecked = document.getElementById('legendWagon').checked;
	var isEventChecked = document.getElementById('legendEvent').checked;
	
	
	addNotHiddenMapObjects(mgrDynamic, isTruckChecked, truckArray, dynamicMarkerBounds);
	addNotHiddenMapObjects(mgrDynamic, isTrailerChecked, trailerArray, dynamicMarkerBounds);
	addNotHiddenMapObjects(mgrDynamic, isContianerChecked, containerArray, dynamicMarkerBounds);
	addNotHiddenMapObjects(mgrDynamic, isWagonChecked, wagonArray, dynamicMarkerBounds);
	addNotHiddenGeoEvents(mgrDynamic, isEventChecked, actualRouteMainArray);
	
	mgrDynamic.showMarkers();
	centerMap();
}

function checkDynamicMapEventObjects() {
	mgrDynamic.clearMarkers();
	//clear passed route
	for ( var i = 0; i < passedRouteArray.length; i++) {
		passedRouteArray[i].setMap(null);
	}
	passedRouteArray = [];
	
	var isEventChecked = document.getElementById('legendEvent').checked;
	
	addNotHiddenGeoEvents(mgrDynamic, isEventChecked, actualRouteArray);
	
	mgrDynamic.showMarkers();
	centerMap();
}


//add objects to the map without geofence
function addNotHiddenMapObjects(mgr, showFlag, objectArray, bounds){
	if (showFlag) {
		for ( var i = 0; i < objectArray.length; i++) {
			mgr.addMarker(objectArray[i]);
			if ( (bounds) && (objectArray[i].getPosition().lat() !== 0) && (objectArray[i].getPosition().lng() !== 0) ) {
				bounds.extend(objectArray[i].getPosition());
			}
		}
	}
}

//add objects to the map with geofence
function addNotHiddenMapObjectsGeo(mgr, showFlag, objectArray, geofenceArray, bounds){
	if (showFlag) {
		for ( var i = 0; i < objectArray.length; i++) {
			mgr.addMarker(objectArray[i]);
			geofenceArray[i].setMap(gmap);
			if ( (bounds) && (objectArray[i].getPosition().lat() !== 0) && (objectArray[i].getPosition().lng() !== 0) ) {
				bounds.extend(objectArray[i].getPosition());
			}
		}
	}
}

//show all events and draw lines between them
function addNotHiddenGeoEvents(mgr, showFlag, objectArray){
	if (showFlag) {
		var lineThickness = 5; // pixels
		var lineOpacity = 0.5;
		var lineColor = "#FF0000";
	
		for ( var j = 0; j < objectArray.length; j++) {
			objectSubArray = objectArray[j];
			var objectSubPoints = new google.maps.MVCArray();
			for ( var i = 0; i < objectSubArray.length; i++) {
				mgr.addMarker(objectSubArray[i]);
				objectSubPoints.push(objectSubArray[i].getPosition());
				var line = new google.maps.Polyline({					
					path: objectSubPoints,
					strokeColor: lineColor, 
					strokeWeight: lineThickness, 
					strokeOpacity: lineOpacity
				});
				line.setMap(gmap);
				passedRouteArray.push(line);
			}
		}
	}
}

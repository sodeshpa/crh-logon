Ext.ns('Logon');

isLnf = false;
trspZoneCd = null;
shiptoZone = null;

/**
 * This GMap has 
 *   - route calculation methods
 *   - 
 */
Logon.GMap = function(config) {
	config = config || {};
	Ext.apply(this, config);
	
	var mapDrawer = new Logon.MapDrawer();
	this.container = Ext.get(this.divId);
	this.maskWrapper = this.container.wrap();
	this.maskWrapper.mask('Loading Google Maps...', 'x-mask-loading');
	this.addressSearchResult = null;
	this.infoWindow = null;
	this.geocoder = null; 

	this.shiptoMarker = null;
	this.clickedPoint = null;
	this.logonDistanceControl = null;
	
	this.addEvents({
		"mapLoaded" : true,
		"routeUpdated" : true
	});
	/**
	 * 
	 * google.maps.Duration 
	 * 
	 * google.maps.Distance 
	 */
	this.calculateDistanceDuration = function(durObj, distObj, params) {
	  /*
		 The method "durObj" provides the following two properties:
		   1. value => returns the travel time t in seconds
		   2. text => returns the travel time t as a localized String
	  */
		
	  var durField = document.getElementById(params.durationFieldId);
	  var distField = document.getElementById(params.distanceFieldId);
	  
	  var duration = -1;
	  var distance = -1;
	  var isTruck = params.delTypeCd == 1 || params.delTypeCd == 4;
	  var isRail = params.delTypeCd == 2 || params.delTypeCd == 3;
	  var isPickup = params.delTypeCd == 5;
	  var isBlockTrain = params.delTypeCd == 6;
	  var isShip = params.delTypeCd == 7;
	  var isReturn = params.delTypeCd == 9 || params.delTypeCd == 10;
	  
	  var corrFactor = (isRail) ? 1 : params.durationCorrFactor;
	  var iconImg = '';
	  
	  if (isTruck || isPickup || isShip || isBlockTrain || isReturn) {
		  if (isTruck) {
			  iconImg = '<img src="' + crhCommonUrl('../images/silk/lorry.png') + '"/>&nbsp;';
		  } 
		  else if (isPickup) {
			  iconImg =  'Pickup:&nbsp;';
		  }
		  else if (isShip) {
			  iconImg =  'Ship:&nbsp;';
		  }
		  else if (isReturn) {
			  iconImg =  'Return:&nbsp;';
		  }
		  else {
			  iconImg =  'Block train:&nbsp;';  
		  }
		  if (durObj !== undefined){  
			 duration = Math.round(durObj.value / 60) * corrFactor;
		  }
		  if (distObj !== undefined){  
			 distance = distObj.value;
		  }
	  }
	  else if (isRail) {
		  iconImg = 'Rail:&nbsp;';
		  duration = params.railDurationMinutes;
		  distance = params.railDistanceMeters;
	  }
	  else {
		  iconImg = '';
	  }
	  
	  
	  var distStr = distance > -1 ? (Math.round(distance / 100) / 10) : 'n.a.';
	  var durStr = duration > -1 ? Math.round(duration) : 'n.a.';
	  
	  var distDurLabel = iconImg + (distStr + ' km (' + durStr + ' min)');
	  
	  if(!this.logonDistanceControl){
		  this.logonDistanceControl = this.getLogonDistanceControlDiv();
		  this.map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(this.logonDistanceControl);
		  if (this.map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].getAt(0)){
			  this.map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].getAt(0).innerHTML=distDurLabel;
		  }
	  }
	  
	  if (durField != null){
		if (duration > -1) {
			durField.value = duration;
		}
		else {
			//there may be no route between the two points (as there are such cases on the normal google map too)
			durField.value = '';
			//document.getElementById("divLogonDistanceControl").innerHTML = '...';
		}
	  }
	  
	  if (distField != null) {
		if (distance > -1) {
			distField.value = distance;
		}
		else {
			distField.value = '';
		}
	  }
	  
	  this.maskWrapper.unmask();
	  
	  if (params.callback != null){
		 params.callback(duration);
	  }
	   
	  return duration; 
	}    
	
	
	/**
	 * this is displaying the ditance and time
	 */
	this.getLogonDistanceControlDiv = function() {
			var distDiv = document.createElement('div');
			
			distDiv.style.color = "#000000";
			distDiv.style.backgroundColor = "white";
			distDiv.style.font = "small Arial";
			distDiv.style.fontSize = "12px";
			distDiv.style.border = "1px solid black";
			distDiv.style.padding = "2px";
			distDiv.style.marginBottom = "3px";
			distDiv.style.textAlign = "center";
			distDiv.style.width = "13em";
			
			distDiv.setAttribute('id', 'divLogonDistanceControl');
			return distDiv;
	};

	/**
	 * route calculation from shippingPoint to shipTo
	 * 
	 */
	this.updateRoute = function(params) {
		if (!this.map) {
			return false;
		}
		
		if (Ext.isEmpty(params.spLat) || Ext.isEmpty(params.spLng) || Ext.isEmpty(params.shiptoLat) || Ext.isEmpty(params.shiptoLng)) {
			if (this.durationFieldId) {	
				document.getElementById(this.durationFieldId).value = '';
			}			
			if (this.distanceFieldId) {	
				document.getElementById(this.distanceFieldId).value = '';
			}			
			return false;
		}	
	 
		var isTruck = (params.delTypeCd == 1 || params.delTypeCd == 4);
		var isRail = (params.delTypeCd == 2 || params.delTypeCd == 3);
		var isPickup = (params.delTypeCd == 5);
		var isBlockTrain = params.delTypeCd == 6;
		var isShip = params.delTypeCd == 7;
		
		this.maskWrapper.mask('Calculating route...', 'x-mask-loading');  
		
		/*
		 * The route calculation is different in google maps v3
		 * http://code.google.com/apis/maps/documentation/javascript/directions.html
		 */
		
		var logonGmap = this;
		
		try {
			mapDrawer.redrawAndRepositionMap(this.map, params.spLat, params.spLng, params.shiptoLat, params.shiptoLng, function(response, status) {
				var duration;
				var distance;
				
				if (status == google.maps.DirectionsStatus.OK) {
					duration = response.routes[0].legs[0].duration;
					distance = response.routes[0].legs[0].distance;
					logonGmap.maskWrapper.unmask();
					logonGmap.calculateDistanceDuration(duration, distance, params);
				}
				else {
					if (params.spLat && params.spLng) {
						logonGmap.map.setCenter(new google.maps.LatLng(params.spLat, params.spLng));
					}
					else {
						logonGmap.map.setCenter(new google.maps.LatLng(params.shiptoLat, params.shiptoLng));
					}
					if(status === google.maps.GeocoderStatus.ZERO_RESULTS){
						Logon.message.warning('No route available');						
					}else {
						Logon.message.warning('Could not calculate route (status = ' + status + ')!');
					}
					logonGmap.maskWrapper.unmask();
				}
			});
		} catch(e) {
			Logon.message.warning("Redraw GoogleMaps failed!");
			logonGmap.maskWrapper.unmask();
		}
	}
	
	this.searchAddressComponents = function(place, key){
		for (i=0; i < place.address_components.length; i++) {
			for (j=0; j < place.address_components[i].types.length; j++) {
			   if(place.address_components[i].types[j] == key) {
				  var country = place.address_components[i];
				  return country;
			   }
			}
		}

	}
	
	/**
	 * Handles result of address search -> creates infowindow to display found address for coordinates. Sets marker and map center to the found coordinates. 
	 */
	this.showClickedAddress = function(results, status) {
		
		if (!results || status != google.maps.GeocoderStatus.OK) {
			this.addressSearchResult = null;
			Logon.message.warning("Unable to determine the address. Status Code: " + status);
		} 
		else {
			place = results[0];
			this.addressSearchResult = place;
			point = place.geometry.location;
			
			this.setShiptoLatLngAndZone(point);
			var content = '<b>Clicked coordinates: </b>' + this.clickedPoint.position.lat() + ', ' + this.clickedPoint.position.lng() + '<br/>' +
			'<b>Zone: </b>' + (shiptoZone != null ? shiptoZone.trspZone.trspZoneCd : '-') + '<br/>' +
			'<b>Nearest address coordinates: </b>' + point.lat() + "," + point.lng() + '<br>' +
			'<b>Nearest address: </b>' + place.formatted_address + '<br>' +
			'<b>Country code: </b> ' + this.searchAddressComponents(place, 'country').short_name;
			
			if(!this.foundPlaceInfoWindow){
				this.foundPlaceInfoWindow = new google.maps.InfoWindow();
			}
			this.foundPlaceInfoWindow.setOptions({content: content,
				  disableAutoPan: false
			});
			this.foundPlaceInfoWindow.open(this.map, this.shiptoMarker);
		}
	}

	/**
	 * searches for address near by the clicked point
	 */
	this.getAddressByClick = function(event) {
		
		if(this.clickedPoint){
			this.clickedPoint.setPosition(event.latLng);
		}else{
			this.clickedPoint = new google.maps.Marker({map: this.map, 
														 position: event.latLng,
														 icon :this.arrowIcon,
														 shadow: this.arrowIconShadow
														 });
		}
		
		if (event.latLng != null) {
			Ext.getDom('orderCommon.lnfCoordLat').value = event.latLng.lat(); 
			Ext.getDom('orderCommon.lnfCoordLon').value = event.latLng.lng();
			if(!this.geocoder){
				this.geocoder = new google.maps.Geocoder();
			}
			this.geocoder.geocode( { 'location': event.latLng}, this.showClickedAddress.createDelegate(this));
		}
	}
	
	/*
	 * Sets the ship-to coordinates on the map and updates the list price or all order items according to the zone of the coordinate
	 */
	this.setShiptoLatLngAndZone = function(latLng) {
		
		this.shiptoLat = latLng.lat();
		this.shiptoLng = latLng.lng();
		shiptoLatLng = latLng;
		
		Ext.getDom('durationTime').value = 0;				
		Ext.getDom('distanceMeters').value = 0;
		
		if (isLnf) {
			
			Ext.getDom('orderCommon.lnfCoordLat').value = latLng.lat();
			Ext.getDom('orderCommon.lnfCoordLon').value = latLng.lng();
			
			shiptoZone = null;
			trspZoneCd = null;
			
			for (var i=0; i<zones.polygonList.length; i++) {
				var key = zones.polygonList[i].trspZonePolygonId;
				if (zones.polygonMap[key].Contains(shiptoLatLng) 
						&& (shiptoZone == null || shiptoZone.ZIndex < zones.polygonList[i].ZIndex)) {
					shiptoZone = zones.polygonList[i];
					trspZoneCd = shiptoZone.trspZone.trspZoneCd;
				}
			}
			
			Ext.getDom('editorList[0].orderHelper.deliveryTrspZoneCd').value = trspZoneCd || ''; // This field is above the editor area
		}
		
		var addCenterShiptoLatLng = true;
		if (editorList != null) {
			
			var editor = editorList[selectedEditorIdx];
			
			if (!editor.spCombo.isEmptySelection()) {
				var spRec = editor.spCombo.getSelectedRecord();
				if (Ext.type(spRec.data.coordLon) == 'number' && Ext.type(spRec.data.coordLat) == 'number') {
					addCenterShiptoLatLng = false; // Will be added by updateRoute
					updateDurationTimeAndMap(spRec, jsonData[0].delTypeCd, jsonData[0].railData.durationMinutes, jsonData[0].railData.distanceMeters, jsonData[0].durationCorrectionFactor);
				}
			}
			
		}
		if(this.shiptoMarker){
			this.shiptoMarker.setPosition(shiptoLatLng);
			this.shiptoMarker.setMap(this.map);
		}
		else{
			this.shiptoMarker = new google.maps.Marker({map: this.map, position: shiptoLatLng});
		}
		if (addCenterShiptoLatLng) {
			this.map.setCenter(shiptoLatLng);
		}
		
	}
	
	/**
	 * 
	 * Initial loading of map.
	 * 
	 */
	this.loadMap = function(autoAddress) {
		try {
			this.map = new google.maps.Map(this.container.dom, defaultLogonGmapOptions);
			this.map.setMapTypeId(google.maps.MapTypeId.ROADMAP);
			var trafficLayer = new google.maps.TrafficLayer();
			trafficLayer.setMap(this.map);
			this.logonDistanceControl = null;
			
			var initFn = this.initFn || Ext.emptyFn;

			// --- LNF ship-to ---
			if (isLnf) {
				//initialize arrow icon and arrow shadow
				var url = "http://maps.google.com/mapfiles/";
				this.arrowIcon = new google.maps.MarkerImage(url + 'arrow.png',
						  new google.maps.Size(39, 34),
						  new google.maps.Point(0, 0),
						  new google.maps.Point(20, 34));
				
				this.arrowIconShadow = new google.maps.MarkerImage(url + 'arrowshadow.png',
						  new google.maps.Size(39, 34),
						  new google.maps.Point(0, 0),
						  new google.maps.Point(20, 34));
				
				google.maps.event.addListener(this.map, 'click', this.getAddressByClick.createDelegate(this));
				for (var key in zones.polygonDataMap) {//add listener also to polygons
					google.maps.event.addListener(zones.polygonMap[key], 'click', this.getAddressByClick.createDelegate(this));
				}
			}
		
			this.directionsService = new google.maps.DirectionsService();
			
			initFn();
			this.fireEvent('mapLoaded');
			
			if (!Ext.isEmpty(autoAddress)) {
				this.setShiptoAddress(autoAddress);
			}
			else if (this.addressSearchResult != null) {
				this.setShiptoLatLngAndZone(this.addressSearchResult.geometry.location);
				if (this.clickedPoint != null) {
					this.clickedPoint.setMap(map);
				}
			}
			else {
				if (!Ext.isEmpty(this.shiptoLat) && !Ext.isEmpty(this.shiptoLng)){
					var latlng =  new google.maps.LatLng(this.shiptoLat, this.shiptoLng);
					this.setShiptoLatLngAndZone(latlng);
				}
			}
		} catch(e) {
			Logon.message.warning("Loading GoogleMaps failed!");					
		} finally {
			this.maskWrapper.unmask();			
		}
	};
	
	this.unloadMap = function() {
		if (this.map) {
			GUnload();
		}
	}
	
	/**
	 * Sets coordinates by given address. (this method is called if no saved coordinates are present, then coordinates will be searched by given default address.)
	 */
	this.setShiptoAddress = function(address) {
		if (!Ext.isEmpty(address)) {
			if (!isLnf) {
				disableButton('shiptoAddressSearchBtn');
			}
			this.maskWrapper.mask('Searching address...', 'x-mask-loading');
			if(!this.geocoder){
				this.geocoder = new google.maps.Geocoder();
			}
			this.geocoder.geocode( { 'address': address}, this.addAddressToMap.createDelegate(this));
		}
		else {
			Logon.message.warning("Please type the address of the ship-to");
		}
	}

	/*
	 * Search for address and set on map
	 */
	this.addAddressToMap = function(results, status) {
		var maskWrapper = this.maskWrapper;
		if (!results || status != google.maps.GeocoderStatus.OK) {
			Logon.message.warning('Unable to find address');
			maskWrapper.unmask();
		} 
		else {
			if (results.length > 1) {
				Logon.message.warning('The ship-tp coordinates cannot be unambiguously determined from the address. Please provide a more detailed address');
				maskWrapper.unmask();
			}
			else {
				if (!isLnf) {
					maskWrapper.mask('Saving address for ship-to...', 'x-mask-loading');
				}
				var place = results[0];
				var lat = place.geometry.location.lat();
				var lng = place.geometry.location.lng();
				var shiptoLatLng = new google.maps.LatLng(lat, lng);
				this.addressSearchResult = place;

				this.setShiptoLatLngAndZone(shiptoLatLng);

				if (editorList != null && editorList[0].spCombo.isEmptySelection()) {
					
					var content = '<b>Nearest address coordinates: </b>' + lat + "," + lng + '<br>' +
							'<b>Zone: </b>' + (shiptoZone != null ? shiptoZone.trspZone.trspZoneCd : '-') + '<br/>' +
							'<b>Nearest address: </b>' + place.formatted_address + '<br>' +
							'<b>Country code: </b> ' + this.searchAddressComponents(place, 'country').short_name;
					
					if(!this.foundPlaceInfoWindow){
						this.foundPlaceInfoWindow = new google.maps.InfoWindow();
					}
					this.foundPlaceInfoWindow.setOptions({content: content,
						  disableAutoPan: false
					});
					this.foundPlaceInfoWindow.open(this.map, this.shiptoMarker);
				}
				
				if (!isLnf) {
					OrderAjaxService.setCoordinatesForShipto(lat, lng, "orderCreate", function() {
						maskWrapper.unmask();
						Logon.message.success('Successfully saved ship-to coordinates');
					});
				}
				else {
					maskWrapper.unmask();
				}
				
			}
			
			// TODO use parameters
			/*
			Ext.getDom('shipTo.coordLat').value = place.Point.coordinates[1];
			Ext.getDom('shipTo.coordLon').value = place.Point.coordinates[0];
			*/
		}
		if (!isLnf) {
			enableButton('shiptoAddressSearchBtn');
		}
	}
	
	return this;
}
Ext.extend(Logon.GMap, Ext.util.Observable);

/*
 * MapDrawer must be available for google.maps.Map objects, not only for Logon.Gmap, as google.maps.Map
 * is commonly used in Logon. This is why it is not included in Logon.Gmap.
 * */
Logon.MapDrawer = function() {
	this.directionsService = null;
	
	this.redrawAndRepositionMap = function(map, fromLat, fromLon, toLat, toLon, callback, waypoints, keepZoom) {
		if (this.directionsService === null || this.directionsService === undefined) {
			this.directionsService = new google.maps.DirectionsService();
		}
		
		var origin = new google.maps.LatLng(fromLat, fromLon);
		var destination = new google.maps.LatLng(toLat, toLon);
		
		if (keepZoom) {
			this.directionsService.keepZoom = keepZoom;
		}
		if(this.directionsDisplay){
			this.directionsDisplay.setMap(null);
		}
		this.directionsDisplay = new google.maps.DirectionsRenderer({map : map});
		var directionServiceOptions = {
				origin: origin,
				destination: destination,
				waypoints: waypoints,
				travelMode: google.maps.TravelMode.DRIVING
		};
		
		var self = this;
		this.directionsService.route(directionServiceOptions, function(response, status) {
			if (status == google.maps.DirectionsStatus.OK) {
				var directionDisplayOptions = {
						polylineOptions: {
							strokeColor: '#0000FF',
							strokeOpacity: '0.45',
							strokeWeight: '5'
						}
				};
				self.directionsDisplay.setOptions(directionDisplayOptions);
				self.directionsDisplay.setDirections(response);
			}
			if (callback) {
				callback(response, status);
			}
		});
	}
}

var gmap;
var marker;
var infoWindow;
var geofenceCircle;
var defaultPointLat = 0; 
var defaultPointLon = 0;
var defaultRadius = 8000; 

GMapAPILoader.onAPIReady(function(){
	onReady();
});

Ext.onReady(function (){
	GMapAPILoader.load();		
	});

function onReady() {

	load_map();
	
	var coordLonField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'FldLngKoord',
		selectOnFocus :true,
		vtype :"decNum",
		enableKeyEvents :true,
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('FldLatKoord').value, Ext.getDom('FldLngKoord').value, Ext
					.getDom('FldRadius').value);
			}
		}
	});

	var coordLatField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'FldLatKoord',
		selectOnFocus :true,
		vtype :"decNum",
		enableKeyEvents :true,
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('FldLatKoord').value, Ext.getDom('FldLngKoord').value, Ext
					.getDom('FldRadius').value);
			}
		}
		

	});

	var radiusField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'FldRadius',
		selectOnFocus :true,
		vtype :"numeric",
		enableKeyEvents :true,
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('FldLatKoord').value, Ext.getDom('FldLngKoord').value, Ext
					.getDom('FldRadius').value);
			}
		}
	});

	
	if (isCoordKnown) {
	if ((Ext.getDom('FldLatKoord').value !== '') && (Ext.getDom('FldLngKoord').value !== '')) {
		update_map(Ext.getDom('FldLatKoord').value, Ext.getDom('FldLngKoord').value,Ext.getDom('FldRadius').value);
	}
	else{
		Ext.getDom('FldLngKoord').value = defaultPointLon;
		Ext.getDom('FldLatKoord').value = defaultPointLat;
		Ext.getDom('FldRadius').value = defaultRadius;
		update_map(Ext.getDom('FldLatKoord').value, Ext.getDom('FldLngKoord').value,Ext.getDom('FldRadius').value);
	}
	}
	else{
		if (Ext.getDom('shiptoAddressField')){
			setShiptoAddress(Ext.getDom('shiptoAddressField').value);
		}
	}

}

function load_map() {

	gmap = new google.maps.Map(Ext.getDom("map"), defaultLogonGmapOptions);
	gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
	gmap.setZoom(13);
	
	marker = new google.maps.Marker({map: gmap});
	google.maps.event.addListener(gmap, "rightclick", function(event) {
		
		if(marker){
			marker.setPosition(event.latLng);
		}else{
			marker = new google.maps.Marker({map: gmap, position: event.latLng});
		}

		radius = Ext.getDom('FldRadius').value;
		if (radius !== '') {
			drawCircle(event.latLng, radius);
		}

		Ext.getDom('FldLngKoord').value = event.latLng.lng();
		Ext.getDom('FldLatKoord').value = event.latLng.lat();
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
	if ((lng !== '') && (lat !== '')) {
		var zoom = gmap.getZoom();
		if ( isNaN(zoom) ) {
			zoom = 13;
		}
		var latLng = new google.maps.LatLng(lat, lng);
		gmap.setCenter(latLng);
		gmap.setZoom(zoom);
		marker.setPosition(latLng);
		if (radius !== '') {
			drawCircle(latLng, radius);
		}
	}
}

/**
 * 
 * 
 * @param lat
 *            Latitude in degrees
 * @param lng
 *            Longitude in degrees
 * @param radius
 *            circleRadius of the circle in statute miles // FIXME (andras Feb 17, 2012): statue miles?? refactored the original method and used gmap built in circle
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
		  radius: d
		};
	if(geofenceCircle){
		geofenceCircle.setOptions(circleOptions);
	}else{
		geofenceCircle = new google.maps.Circle(circleOptions);
	}
}

//the same as logon-gmap's setShiptooAddress
function setShiptoAddress(address) {
	if (!Ext.isEmpty(address)) {
		disableButton('shiptoAddressSearchBtn');
		geocoder = new google.maps.Geocoder();
		geocoder.geocode( { 'address': address}, this.addAddressToMap.createDelegate(this));
	}
	else {
		Logon.message.warning(texts.missingaddress);
	}
}

//the same as logon-gmap's addressToMap
function addAddressToMap (results, status) {
	if (!results || status != google.maps.GeocoderStatus.OK || results.size === 0) {
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
			
			if(!infoWindow){
				infoWindow = new google.maps.InfoWindow();
			}
			infoWindow.setOptions({content: place.formatted_address,
				  disableAutoPan: false
			});
			OrderAjaxService.setCoordinatesForShipto(lat, lng, "dispokarte", function(){
				Ext.getDom('FldLatKoord').value = lat;
				Ext.getDom('FldLngKoord').value = lng;
				update_map(lat, lng, Ext.getDom('FldRadius').value);
				Logon.message.success(texts.coordsretrieved);
				infoWindow.open(gmap, marker); // FIXME (andras Feb 17, 2012): the bubble does not autopan to fully visible on loading the page
			});
	   }
	}
	enableButton('shiptoAddressSearchBtn');
}


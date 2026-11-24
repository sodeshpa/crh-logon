var gmap;
var geofenceCircle;
var marker;

Ext.onReady( function() {
	GMapAPILoader.load();
} );

GMapAPILoader.onAPIReady( function() {

	load_map();
	
	var coordLonField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'coordLon',
		selectOnFocus :true,
		vtype :"decNum",
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		enableKeyEvents :true,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value,Ext.getDom('radius').value);
			}
		}
	});

	var coordLatField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'coordLat',
		selectOnFocus :true,
		vtype :"decNum",
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		enableKeyEvents :true,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value,Ext.getDom('radius').value);
			}
		}
	});

	var radiusField = new Ext.form.TextField( {
		allowBlank :true,
		applyTo :'radius',
		selectOnFocus :true,
		vtype :"numeric",
		validateOnBlur :false,
		validationEvent :'keyup',
		validationDelay :500,
		enableKeyEvents :true,
		listeners: {
			keyup: function(element) {
				update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value,Ext.getDom('radius').value);
			}
		}
	});

	
	if ((Ext.getDom('coordLat').value !== '') && (Ext.getDom('coordLon').value !== '')) {
		update_map(Ext.getDom('coordLat').value, Ext.getDom('coordLon').value,Ext.getDom('radius').value);
	}

});

function load_map() {

	gmap = new google.maps.Map(Ext.getDom("map"), defaultLogonGmapOptions);
	gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
	
	google.maps.event.addListener(gmap, "rightclick", function(event) {
		
		var latLng = event.latLng;
		if(marker){
			marker.setPosition(latLng);
		}else{
			marker = new google.maps.Marker({map: gmap, position: latLng});
		}
	
		var radius = Ext.getDom('radius').value;
		if (radius !== '') {
			drawCircle(latLng, radius);
		}
	
		Ext.getDom('coordLon').value = latLng.lng();
		Ext.getDom('coordLat').value = latLng.lat();
		
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
		var latLng =  new google.maps.LatLng(lat, lng);
		gmap.setCenter(latLng);
		gmap.setZoom(zoom);

		if(marker){
			marker.setPosition(latLng);
		}else{
			marker = new google.maps.Marker({map: gmap, position: latLng});
		}

		if (radius !== '') {
			drawCircle(latLng, radius);
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
			  radius: d
			};
		
		if(geofenceCircle){
			geofenceCircle.setOptions(circleOptions);
		}else{
			geofenceCircle = new google.maps.Circle(circleOptions);
		}
}
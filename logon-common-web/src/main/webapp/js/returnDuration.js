/**
By calling updateRoute with the parameters: 
shippingPointLatitude, shippingPointLongitude, shipToLatitude, shipToLongitude : between the given two points will the route be given
gmapDivId - id of the DIV element. Either an Ext panel's gmap, or a simple div.
inputField - this field will store the duration time between the two points. GIVEN IN MINUTES, AND NOT SECONDS!
**/

// variable for the GMap instance, which will be loaded 
var map;
var directionsService;


function updateRouteExistingMap(shippingPointLatitude, shippingPointLongitude, shipToLatitude, shipToLongitude, callback, context)
{ 
  /* 
	 TO DO: 
	 An Ajax-Object has to determine the passed parameters from the server and pass them to the method loadGMap.
  */
  calcRouteDuration(shippingPointLatitude, shippingPointLongitude, shipToLatitude, shipToLongitude, callback, context);
}



function calcRouteDuration (shippingPointX, shippingPointY, shipToX, shipToY, callback, context)
{
	if(directionsService === undefined){
		directionsService = new google.maps.DirectionsService();
	}
	
	var shippingPoint = new google.maps.LatLng(shippingPointX, shippingPointY);
	var shipTo = new google.maps.LatLng(shipToX, shipToY); 
	var directionServiceOptions = {
			  origin: shippingPoint,
			  destination: shipTo,
			  travelMode: google.maps.TravelMode.DRIVING
	};
	
	this.directionsService.route(directionServiceOptions, function(response, status) {
		if (status == google.maps.DirectionsStatus.OK) {
		   var durationObj = response.routes[0].legs[0].duration;
		   var distance = response.routes[0].legs[0].distance;
		   var duration = -1;
		   if (durationObj !== undefined){  
			 duration = durationObj.value/60;
		   }
		   callback(duration, context, distance);	       
		}
	});
}


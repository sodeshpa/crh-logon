	var controls = [];
	//var maps = [];
	var eaMap;
	var eaMarkersArray = [];
	var coords = [];
	var locked = {pre:false, to:false, from:false, 'return':false};
	var loadingTime = 0; 
	var unloadingTime = 0; 
	var loadingText = 'load';
 
	function clearEaMarkers() {
		  if (eaMarkersArray) {
			for (var i = 0; i < eaMarkersArray.length; i++ ) {
				eaMarkersArray[i].setMap(null);
			}
			eaMarkersArray = [];
		  }
		}
	
	function setActive(position){
		
		prefix = Ext.getDom('selectedAssignmentPartSeqNo').value;
		
		var cb = Ext.getDom(position + 'Position');
		cb.checked = true;
		
		var spSearch = controls.shippingpointSearch;
		var stSearch = controls.shiptoSearch;
		var arrPlanned = controls.plannedArrival;
		var arrReal = controls.realArrival;
		var depPlanned = controls.plannedDeparture;
		var depReal = controls.realDeparture;
		var coordLat = Ext.get('lat');		
		var coordLon = Ext.get('lon');
		var siteName = Ext.get('siteName');
		var siteLoadUnload = Ext.get('siteLoadUnload');
		var siteContactPersonTxt = Ext.get('siteContactPersonTxt');
		var siteStreetHouseNo = Ext.get('siteStreetHouseNo');
		var siteCountry = Ext.get('siteCountry');
		var sitePostalCode = Ext.get('sitePostalCode');
		var siteCity = Ext.get('siteCity');
		var calcArr = Ext.get('calculateArrival');
		var calcDep = Ext.get('calculateDeparture');
		var map = Ext.get('eaMapDiv');
		
		if (position == 'sp'){
			spSearch.enable();
		}
		else{
			spSearch.disable();
		}
		if (position == 'sp'){
			Ext.getDom(prefix + 'activeCoordLon').value = spSearch.coords.lon; 
			Ext.getDom(prefix + 'activeCoordLat').value = spSearch.coords.lat;
		}

		if (position == 'shipto' && !locked[prefix]){
			stSearch.enable();
		}
		else{
			stSearch.disable();
		}
		if (position == 'shipto'){
			Ext.getDom(prefix + 'activeCoordLon').value = stSearch.coords.lon; 
			Ext.getDom(prefix + 'activeCoordLat').value = stSearch.coords.lat;
		}

		if (position == 'coords' && !locked[prefix]){
			coordLat.removeClass('disabled');
			coordLon.removeClass('disabled');
			if (siteName) {
				siteName.removeClass('disabled');
				siteLoadUnload.removeClass('disabled');
				siteContactPersonTxt.removeClass('disabled');
				siteStreetHouseNo.removeClass('disabled');
				siteCountry.removeClass('disabled');
				sitePostalCode.removeClass('disabled');
				siteCity.removeClass('disabled');
			}
			coordLat.dom.disabled = false;
			coordLon.dom.disabled = false;
			if (siteName) {
				siteName.dom.disabled = false;
				siteLoadUnload.dom.disabled = false;
				siteContactPersonTxt.dom.disabled = false;
				siteStreetHouseNo.dom.disabled = false;
				siteCountry.dom.disabled = false;
				sitePostalCode.dom.disabled = false;
				siteCity.dom.disabled = false;
			}
			if ( (!coordLat.dom.value || !coordLon.dom.value) && (gmapEnabled) ) {
				var latLng = map.getCenter();
				coordLat.dom.value = latLng.lat();
				coordLon.dom.value = latLng.lng();
			}
		}
		else{
			coordLat.addClass('disabled');
			coordLon.addClass('disabled');
			if (siteName) {
				siteName.addClass('disabled');
				siteLoadUnload.addClass('disabled');
				siteContactPersonTxt.addClass('disabled');
				siteStreetHouseNo.addClass('disabled');
				siteCountry.addClass('disabled');
				sitePostalCode.addClass('disabled');
				siteCity.addClass('disabled');
			}
			coordLat.dom.disabled = true;
			coordLon.dom.disabled = true;
			if (siteName) {
				siteName.dom.disabled = true;
				siteLoadUnload.dom.disabled = true;
				siteContactPersonTxt.dom.disabled = true;
				siteStreetHouseNo.dom.disabled = true;
				siteCountry.dom.disabled = true;
				sitePostalCode.dom.disabled = true;
				siteCity.dom.disabled = true;
			}
		}
		if (position == 'coords'){
			Ext.getDom(prefix + 'activeCoordLon').value = stSearch.coords.lon; 
			Ext.getDom(prefix + 'activeCoordLat').value = stSearch.coords.lat;
		}
		
		if (position == 'none'){
			if (arrPlanned){
				arrPlanned.disable();
				arrReal.disable();
				if (calcArr){
					calcArr.dom.disabled = true;
				}
			}
			if (depPlanned){
				depPlanned.disable();
				depReal.disable();
				if (calcDep){
					calcDep.dom.disabled = true;
				}
			}
			Ext.getDom(prefix + 'activeCoordLon').value = null; 
			Ext.getDom(prefix + 'activeCoordLat').value = null;
		}
		else{
			if (arrPlanned){
				if (calcArr && !calcArr.dom.checked){
					arrPlanned.enable();
				}
				arrReal.enable();
				if (calcArr){
					calcArr.dom.disabled = false;
				}
			}				
			if (depPlanned){	
				if (calcDep && !calcDep.dom.checked){
					depPlanned.enable();
				}
				depReal.enable();
				if (calcDep){
					calcDep.dom.disabled = false;
				}
			}
		}
		setMapMarker(prefix);
		if (position == 'none'){
			map.mask();
		}
		else{
			map.unmask();
		}
	}
	
	var mapPrefix;
	
	function addMap(prefix){
		
		
		if (!gmapEnabled) {
			
			return;
		}
				
		if(!eaMap) {
			eaMap = new google.maps.Map(Ext.getDom('eaMapDiv'), defaultLogonGmapOptions);
			eaMap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
			var trafficLayer = new google.maps.TrafficLayer();
			trafficLayer.setMap(eaMap);
			
			// TODO: use current truck position 
			var latLng = new google.maps.LatLng(47, 8.5);
			eaMap.setCenter(latLng);
			eaMap.setZoom(11);
			google.maps.event.addListener(eaMap, "rightclick", function(event){
				var latLng = event.latLng;
				setCoords(mapPrefix, {lat: latLng.lat(), lon: latLng.lng()}, true);
			});
		}
		
		setMapMarker(prefix, prefix == mapPrefix);
		mapPrefix = prefix;
				
	}
	
	function createMapsClickListener(){
		return function(latLng) {
			submitPage({setSelectedPart: this.prefix});
		}
	}
	
	function setMapMarker(prefix, keepZoom) {
		if (gmapEnabled) {
			clearEaMarkers();
			var p = 0;
			var lat = Ext.getDom(p + 'activeCoordLat');
			var lon = Ext.getDom(p + 'activeCoordLon');
			var location = Ext.getDom(p + 'activeLocationType');
			var coords = [];
			var waypoints = [];
			var from = null;
			var to = null;
			var toLatLngStr = null;
			var fromLatLngStr = null;
			while (lat && lon) {
				var latLng = new google.maps.LatLng(lat.value, lon.value);
				var icon = icons.coordIcon;
				if (location.value == "Sp") {
					icon = icons.spIcon;
				}
				else if (location.value == "Shipto") {
					icon = icons.stIcon;
				}
				var marker = new google.maps.Marker({
					icon:icon,
					position: latLng,
					draggable :false,
					raiseOnDrag : false
				});
				
				marker.prefix = p;
				google.maps.event.addListener(marker, "click", createMapsClickListener());
				
				marker.setMap(eaMap);
				eaMarkersArray.push(marker);
				if (p == prefix) {
					marker.setIcon(icons.selectIcon);
					marker.setClickable(false);
				} 
				var ll = new google.maps.LatLng(lat.value, lon.value);
				var nc = lat.value + ',' + lon.value;
				if (from == null) {
					from = ll;
					fromLatLngStr = lat.value + ',' + lon.value;
				}
				else if (nc !== fromLatLngStr) {
					to = ll;

					toLatLngStr = lat.value + ',' + lon.value;			

					// 'stopover:true' is a workaround for a GMap problem (see:LOGON-5221)
					coords.push({
						location:nc,
						stopover:true
					});			
				}
				
				p++;
				lat = Ext.getDom(p + 'activeCoordLat');
				lon = Ext.getDom(p + 'activeCoordLon');
				location = Ext.getDom(p + 'activeLocationType');
				if(lat && lon){
					if(lat.value === '' || lon.value === '') {
						return;
					}
				}
			}

			for (var i=0;i<coords.length;i++)
			{ // waypoint should not include start and end destinations. 
			  // those are rendered anyway through the 'to' and 'from' variables.
				if(fromLatLngStr !== coords[i].location && toLatLngStr !== coords[i].location ){
					waypoints.push(coords[i]);
				}
			}
			
			var mapDrawer = new Logon.MapDrawer();
			mapDrawer.redrawAndRepositionMap(eaMap, from.lat(), from.lng(), to.lat(), to.lng(), function(response, status) {
				if (status != google.maps.DirectionsStatus.OK) {
					Logon.message.warning('Google Maps could not calculate route status=' + status);
				}
			}, 
			waypoints, keepZoom);
		}
	}
	
	function setCoords(prefix, c, keepZoom){
		var cb = Ext.getDom('coordsPosition');
		if (cb && cb.checked){	
			Ext.getDom('lon').value = c.lon;
			Ext.getDom('lat').value = c.lat;
			Ext.getDom(prefix + 'activeCoordLon').value = c.lon; 
			Ext.getDom(prefix + 'activeCoordLat').value = c.lat;
			setMapMarker(prefix, keepZoom);
			calcTimesCurrPart();
		}
	}
	
	function findCoords(address, prefix){
		
		if (!gmapEnabled) {
			
			return;
		}
		var geocoder = new google.maps.Geocoder();
		geocoder.geocode( { 'address': address}, this.setCoordsFromAddress.createDelegate(this, {'prefix':prefix}, true));
	}
	
	function setCoordsFromAddress(results, status, args) {
		if (results && status == google.maps.GeocoderStatus.OK) {
			var place = results[0];
			var lat = place.geometry.location.lat();
			var lng = place.geometry.location.lng();
			var foundCoords = new google.maps.LatLng(lat, lng);
			
			Ext.getDom('lat').value = foundCoords.lat();
			Ext.getDom('lon').value = foundCoords.lng();
			Ext.getDom(args.prefix + 'activeCoordLon').value = foundCoords.lng(); 
			Ext.getDom(args.prefix + 'activeCoordLat').value = foundCoords.lat();
		}
		else {
			Ext.getDom('lat').value = '';
			Ext.getDom('lon').value = '';
			Ext.getDom(args.prefix + 'activeCoordLon').value = ''; 
			Ext.getDom(args.prefix + 'activeCoordLat').value = '';
		}
		setMapMarker(args.prefix);
		calcTimesCurrPart();
	}
	
	function deleteAssignment() {
		if (confirm("Are you sure you want to delete this assignment?")) {
			a = {'delete': ''};
			addFormElements(a, 'AssignmentEditForm');
	
			showPopup('AssignmentEdit.do', null, a);
		}
	}
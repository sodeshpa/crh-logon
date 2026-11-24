// AufliegerAdminTile.jsp
function updatePlateNoShowRFIDPopup() {
	var inputPlateNumber = document.getElementById("inputPlateNr").value;
	AdminRFIDPopupAjaxService.selectTrailerByPlanteNumber(inputPlateNumber, function() {
		showPopup('../AdminRFIDPopup.do');
	});
}

function updateChambers() {
	document.getElementById("inputAction").value=forwards.UPDATE_CHAMBERS;
	var chambers = document.getElementById("inputNrChambers").value;
	var chamber1T = document.getElementById("inputMaxNetWeightChamber1T").value;
	var chamber2T = document.getElementById("inputMaxNetWeightChamber2T").value;
	var params = {"inputAction":document.getElementById("inputAction").value, "inputNrChambers": chambers,"inputMaxNetWeightChamber1T": chamber1T, "inputMaxNetWeightChamber2T": chamber2T, "AjaxCall":"AjaxCall"};
	var diva = Ext.get('changefields');
	diva.load({
		url: 'TrailerAdmin.do', 
		params: params,
		scripts: true
	});
}

function selectCarrier() {
	document.getElementById("inputAction").value=forwards.SELECT_CARRIER;
	Ext.get('admin_TrailerAdminForm').dom.submit();
}

function selectTrailer(id) {
	var butt = document.getElementById("saveButton");
	if (butt !== null) {		
		document.getElementById("saveButton").value = texts.buttonsSave;
	}
	document.getElementById("trailerId").value = id;
	document.getElementById("inputAction").value=forwards.LOAD_TRAILER;
	Ext.get('admin_TrailerAdminForm').dom.submit();
}

function changeFields() {
	var selectChamber = document.getElementById("inputNrChambers");
	if (selectChamber) {			
		var value = parseFloat(selectChamber.value);
		
		var chamber1T = document.getElementById("inputMaxNetWeightChamber1T");
		chamber1T.disabled=false;
		if (value < 1) {
			chamber1T.value = "0";
			chamber1T.disabled=true;
		}
		
		var chamber2T = document.getElementById("inputMaxNetWeightChamber2T");
		chamber2T.disabled=false;
		if (value < 2 ) {
			chamber2T.value = "0";
			chamber2T.disabled=true;
		}
	}
}

function saveTrailer(isNew) {
	var bool = checkCommentLength("inputComment", texts.commonConfirm,texts.commentField, lengths.commentLength);
	if (!bool) {
		return false;
	}
	if (isNew ) {
		document.getElementById("inputAction").value=forwards.SAVE_NEW;
	}
	else {
		document.getElementById("inputAction").value=forwards.SAVE;
	}
	
	Ext.get('admin_TrailerAdminForm').dom.submit();
}

function resetTrailer() {		
	document.getElementById("inputAction").value=forwards.RESET;
	Ext.get('admin_TrailerAdminForm').dom.submit();
}

function addNewTrailer() {
	showPopup('TrailerAdmin.do?inputAction='+ forwards.CREATE_TRAILER);
}

function saveNewTrailer() {
	document.getElementById("trailerId").value = "";
	document.getElementById("inputAction").value=forwards.SAVE_NEW;
	var diva = Ext.get('logonPopup');
	var params = {"inputAction":forwards.SAVE_NEW,"inputTaraMeasure":document.getElementById("popupTaraMeasure").value,"inputMaxMeasure":document.getElementById("popupMaxMeasure").value,"inputPlateNr":document.getElementById("popupPlateNr").value,"inputTrailerName":document.getElementById("inputTrailerName").value,"inputSapVehicleNo":sapVehicleIdCombo.getValue(),"characteristic":document.getElementById("characteristic").value};
	diva.load({
		url: 'TrailerAdmin.do', 
		params: params,
		text: texts.exjsloading,
		scripts: true
	});
}

// Auflieger3Tile.jsp - 
function load_map(lat1, lng1) {	
	var gmap = new google.maps.Map(Ext.getDom('map'), defaultLogonGmapOptions);
	gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
   
	var latlng;
	if ((lng1 !== '') && (lat1 !== '') && (lng1 !== null) && (lat1 !== null) && (lng1 !== undefined) && (lat1 !== undefined)) {
		latlng = new google.maps.LatLng(lat1, lng1);
		var marker = new google.maps.Marker({map: gmap, position: latlng});
	}
	else {
		latlng = new google.maps.LatLng(defaultMapCenter.lat, defaultMapCenter.lng);
	}
	var zoom = defaultMapCenter.zoomLevel;
	gmap.setCenter(latlng);
	gmap.setZoom(zoom);
}
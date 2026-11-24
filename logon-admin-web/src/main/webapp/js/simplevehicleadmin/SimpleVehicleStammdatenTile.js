SimpleVehicleStammdatenTile = {
	texts: {},
	init : function (optimisticLockText) {
		SimpleVehicleStammdatenTile.texts.optimisticLockText = optimisticLockText;
	}
}
// SimpleVehicleStammdatenTile.jsp - 

function renderTypeSelect(){
	document.getElementById("inputAction").value = forwards.ADJUST_FIELDS;
	var type = document.getElementById("inputSimpleVehicleTypeCd").value;
	var maxmeasure = document.getElementById("inputMaxMeasure").value;
	var tarameasure = document.getElementById("inputTaraMeasure").value;
	var volume = document.getElementById("inputVolumeM3").value;
	var units = document.getElementById("inputUnitCount").value;
	var chambers = document.getElementById("inputNrChambers").value;
	var internal = document.getElementById("inputInternal").value;
	var status = document.getElementById("inputStatus").value;
		
	var params = {
			"inputAction":document.getElementById("inputAction").value, 
			"inputSimpleVehicleTypeCd":type, 
			"inputVolumeM3":volume, 
			"inputUnitCount":units, 
			"inputMaxMeasure":maxmeasure, 
			"inputTaraMeasure":tarameasure, 
			"inputNrChambers":chambers, 
			"inputInternal":internal, 
			"inputStatus":status, 
			"ajaxCall":"ajaxCall"
			};
	var diva = Ext.get('changefields');
	diva.load({
		url: 'SimpleVehicleAdmin.do', 
		params: params,
		scripts: true
	});
}

function selectCarrier(){
	document.getElementById("inputAction").value=forwards.SELECT_CARRIER;
	Ext.get('admin_SimpleVehicleAdminForm').dom.submit();
}

function removeOldFields(form, name){
	// remove old settings if any
	var oldField = '#'+form+' input[name='+name+']';
	Ext.select(oldField).remove();
}

function storeValueAsHiddenField(form, name, value){
	  
	// construct list of tags
	var newField ={ tag: 'input',
					type: 'hidden',
					name: name,
					value: value
			};
	
	// add new field to form
	Ext.DomHelper.append(Ext.get(form), newField);
}
function getSelectedPools() {
	var ids = [];
	var tab=	document.getElementById('selectedPoolsTbody');
	if (tab) {	
		var length = tab.rows.length;
		for (var i = 0; i < length; i++) {
			var pool = tab.rows[i].firstChild.firstChild;
			ids.push(pool.id);
		}
	}
	return ids;
}

function saveSimpleVehicle(isNew) {
	//TODO 2012-Nov-18/awe:(copied from Truck) if this popup add new remains there is no need for the SAVE_NEW
	var bool = checkCommentLength("inputComment", texts.commonConfirm,texts.commentField, lengths.commentLength);
	if (!bool) {
		return false;
	}
	
	if (isNew ) {
		action = forwards.SAVE_NEW;
	}
	else {
		action = forwards.SAVE;
	}

	submitWithAjax(action);
	
}

function submitWithAjax(action) {
	form = Ext.getDom('admin_SimpleVehicleAdminForm');
	Ext.query("input[name=inputAction]", form)[0].value = action;
	p = {
			'formAction' :action
	};
	addFormElements(p, form);
	// add selected pools separately
	var ids = getSelectedPools();
	p.inputPoolIds = ids;
	
	executeUpdate.run('masterdata',
			'SimpleVehicleAdmin.do', p,
			'SimpleVehicleAdmin.do', {'inputAction' : 'update'},
			'.*OptimisticLockException.*', SimpleVehicleStammdatenTile.texts.optimisticLockText);
	
}

function saveNewSimpleVehicle() {
	document.getElementById("simpleVehicleId").value = "";
	document.getElementById("inputAction").value=forwards.SAVE_NEW;
	var diva = Ext.get('logonPopup');
	var params = {"inputAction":forwards.SAVE_NEW,
			"inputSimpleVehicleTypeCd":document.getElementById("popupSimpleVehicleTypeCd").value,
			"inputTaraMeasure":document.getElementById("popupTaraMeasure").value,
			"inputVehicleRegNo":document.getElementById("popupRegNo").value,
			"inputVehicleName":document.getElementById("popupVehicleName").value,
			"inputSapVehicleNo":document.getElementById("popupSapVehicleNo").value,
			"inputNrChambers":document.getElementById("popupNrChambers").value};
	diva.load({
		url: 'SimpleVehicleAdmin.do', 
		params: params,
		text: texts.exjsloading,
		scripts: true
	});
}

function resetSimpleVehicle(id){		
	document.getElementById("inputAction").value=forwards.RESET;
	document.getElementById("simpleVehicleId").value=id;
	submitWithAjax(forwards.RESET);
}

function addNewSimpleVehicle(){
	showPopup('SimpleVehicleAdmin.do?inputAction='+ forwards.CREATE_SIMPLE_VEHICLE);
}

// SimpleVehicleStammdatenData2Tile.jsp
// TODO 2012-Nov-18/awe: copied from Truck, move this to a common js file 
function load_map(lat, lng, lat1, lng1) {
	
	var gmap = new google.maps.Map(Ext.getDom("map"), defaultLogonGmapOptions);
	gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
	var marker = null;
	var latlng = new google.maps.LatLng(defaultMapCenter.lat, defaultMapCenter.lng);
	var zoom = defaultMapCenter.zoomLevel;
	
	if ((lng !== '') && (lat !== '') && (lng !== null) && (lat !== null) && (lng !== undefined) && (lat !== undefined)){
		latlng = new google.maps.LatLng(lat, lng);
		zoom = 10;
	}
	gmap.setCenter(latlng);
	gmap.setZoom(zoom);

	if ((lng !== '') && (lat !== '') && (lng !== null) && (lat !== null) && (lng !== undefined) && (lat !== undefined)){
		marker = new google.maps.Marker({map: gmap, position: latlng});
	}
	if ((lng1 !== '') && (lat1 !== '') && (lng1 !== null) && (lat1 !== null) && (lng1 !== undefined) && (lat1 !== undefined)){
		var latlng1 = new google.maps.LatLng(lat1, lng1);
		var marker2 = new google.maps.Marker({map: gmap, position: latlng1});
	}	    

	google.maps.event.addListener(gmap, "rightclick", function(event) {
	
		if(marker){
			marker.setPosition(event.latLng);
		}else{
			marker = new google.maps.Marker({map: gmap, position: event.latLng});
		}
		Ext.getDom('inputHomeBaseLon').value = event.latLng.lng();
		Ext.getDom('inputHomeBaseLat').value = event.latLng.lat();
	});

}

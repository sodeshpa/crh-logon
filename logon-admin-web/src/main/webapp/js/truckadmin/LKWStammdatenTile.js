LKWStammdatenTile = {
	texts: {},
	init : function (optimisticLockText) {
		LKWStammdatenTile.texts.optimisticLockText = optimisticLockText;
	}
}
// LKWStammdatenTile.jsp
function updatePlateNoShowRFIDPopup(){
	var inputPlateNumber = document.getElementById("inputPlateNr").value;
	AdminRFIDPopupAjaxService.selectTruckByPlateNumber(inputPlateNumber, function() {
		showPopup('../AdminRFIDPopup.do');
	});
}

function renderTypeSelect(){
	document.getElementById("inputAction").value=forwards.ADJUST_FIELDS;
	var tkat = document.getElementById("inputTkategorie").value;
	var pda = document.getElementById("inputPdaFlag").checked;
	var type = document.getElementById("inputLkwType").value;
	var maxmeasure = document.getElementById("inputMaxMeasure").value;
	var tarameasure = document.getElementById("inputTaraMeasure").value;
	var filter = document.getElementById("inputDustfilter").value;
	var volumen = document.getElementById("inputVolumeM3").value;
	var trailer = document.getElementById("inputTrailerSelect").value;
	var container = document.getElementById("inputContainerSelect").value;
	var chambers = document.getElementById("inputNrChambers").value;
	var chamber1T = document.getElementById("inputMaxNetWeightChamber1T").value;
	var chamber2T = document.getElementById("inputMaxNetWeightChamber2T").value;
	var characteristic = document.getElementById("characteristic").value;	
	var selectedPlant = document.getElementById("selectedPlant").value;	
		
	var params = {
		"inputAction" : document.getElementById("inputAction").value,
		"inputTkategorie" : tkat,
		"inputLkwType" : type,
		"inputPdaFlag" : pda,
		"inputVolumeM3" : volumen,
		"inputTrailerSelect" : trailer,
		"inputDustfilter" : filter,
		"inputNrChambers" : chambers,
		"inputMaxNetWeightChamber1T" : chamber1T,
		"inputMaxNetWeightChamber2T" : chamber2T,
		"inputMaxMeasure" : maxmeasure,
		"inputTaraMeasure" : tarameasure,
		"inputContainerSelect" : container,
		"characteristic" : characteristic,
		"selectedPlant" : selectedPlant,
		"ajaxCall" : "ajaxCall"
	};
	var diva = Ext.get('changefields');
	diva.load({
		url: 'TruckAdmin.do', 
		params: params,
		scripts: true
	});
}
/** Loads the shippingpoints based on the selected plant */
function setSelectedPlant() {
	renderTypeSelect();
}

function selectCarrier(){
	document.getElementById("inputAction").value=forwards.SELECT_CARRIER;
	Ext.get('admin_TruckAdminForm').dom.submit();
}

function selectTrailer(el){
	var id = el.value;
	document.getElementById("trailerChamberNr").innerHTML = trailerData[id] &&trailerData[id].nrChambers !== "" ? trailerData[id].nrChambers : "-";
	document.getElementById("trailerChamber2T").innerHTML = trailerData[id] && trailerData[id].chamber2 !== "" ? trailerData[id].chamber2 : "-";
	document.getElementById("trailerChamber1T").innerHTML = trailerData[id] && trailerData[id].chamber1 !== "" ? trailerData[id].chamber1 : "-";
	document.getElementById("trailerMaxMeasure").innerHTML= trailerData[id] && trailerData[id].maxGross !== "" ? trailerData[id].maxGross : "-";
	document.getElementById("trailerTaraMeasure").innerHTML = trailerData[id] && trailerData[id].tara !== "" ? trailerData[id].tara : "-";
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

function saveTruck(isNew) {
	//TODO:if this popup add new remains there is no need for the SAVE_NEW
	var bool = checkCommentLength("inputComment", texts.commonConfirm,texts.commentField, lengths.commentLength);
	if (!bool) {
		return false;
	}
	var trailer = document.getElementById("inputTrailerSelect");
	if (trailer.disabled === true) {
		storeValueAsHiddenField("admin_TruckAdminForm", "inputTrailerSelect", trailer.value ); 
	}
	var container = document.getElementById("inputContainerSelect");
	if (container.disabled === true) {
		storeValueAsHiddenField("admin_TruckAdminForm", "inputTrailerSelect", container.value ); 
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
	form = Ext.getDom('admin_TruckAdminForm');
	Ext.query("input[name=inputAction]", form)[0].value = action;
	p = {
			'formAction' :action
	};
	addFormElements(p, form);
	// add selected pools separately
	var ids = getSelectedPools();
	p.inputPoolIds = ids;
	
	executeUpdate.run('masterdata',
			'TruckAdmin.do', p,
			'TruckAdmin.do', {'inputAction' : 'update'},
			'.*OptimisticLockException.*', LKWStammdatenTile.texts.optimisticLockText);
	
}

function saveNewTruck() {
	document.getElementById("truckId").value = "";
	document.getElementById("inputAction").value=forwards.SAVE_NEW;
	var diva = Ext.get('logonPopup');
	var params = {"inputAction":forwards.SAVE_NEW,"inputTaraMeasure":document.getElementById("popupTaraMeasure").value,"inputPlateNr":document.getElementById("popupPlateNr").value,"inputTruckName":document.getElementById("inputTruckName").value,"inputSapVehicleNo":sapVehicleIdCombo.getValue(),"characteristic":document.getElementById("characteristic").value};
	diva.load({
		url: 'TruckAdmin.do', 
		params: params,
		text: texts.exjsloading,
		scripts: true
	});
}



function resetTruck(id){

	// We block the display with a loading layer so
	// the user can't click the trucks 'around' while
	// loading.
	var mkArgs = {
		removeMask: true
	};

	var mk = new Ext.LoadMask('masterdata', mkArgs);
	mk.show();
	
	document.getElementById("inputAction").value=forwards.RESET;
	document.getElementById("truckId").value=id;
	submitWithAjax(forwards.RESET);
	
}

function addNewTruck(){
	showPopup('TruckAdmin.do?inputAction='+ forwards.CREATE_TRUCK);
}

// This part is used by LKWStammData2Tile.jsp
function moveBackPool(index, id, name){	
	document.getElementById('selectedPoolsTbody').deleteRow(index);
	 var y=document.createElement('option');
	y.id=id;
	y.value=name;
	y.text=name;
	var sel = document.getElementById("selectPool");
	try
  {
  sel.add(y,null); // standards compliant
  }
catch(ex)
  {
  sel.add(y); // IE only
  }
}
function resetPoolSelection(){
	var indexes = [];
	var tab=	document.getElementById('selectedPoolsTbody');
	var length = tab.rows.length;
	for ( var i=0; i < length; i++){
		var pool = tab.rows[i].firstChild.firstChild;
		indexes.push({index:tab.rows[i].rowIndex, id:pool.id, name:pool.name});
	}
		for ( var i2=0; i2 < indexes.length; i2++){
			moveBackPool(indexes[i2].index-i2, indexes[i2].id, indexes[i2].name);
		}
}

function selectAll(elName){
	var el = document.getElementById(elName);
	var i=0;	
	while ( i != el.options.length){
		el.options[i].selected=true;
		i++;
	}
}


function deSelectAll(elName){
	var el = document.getElementById(elName);
	var i=0;	
	while ( i!= el.options.length){
		el.options[i].selected=false;
		i++;
	}
}

function moveSelectedRight(){
	var sel = document.getElementById("selectPool");
	for (var i=0; i<sel.options.length; i++){
		var selOp =  sel.options[i];
		if (selOp.selected === true){
			moveRight( selOp.id , selOp.value);
		}
	}
	while (sel.selectedIndex != -1){
		sel.remove(sel.selectedIndex);
	}
}

function moveRight (id, name){

		var cellFuncs = [
		function(data) { return '<input type="hidden" id="' + data.poolId + '" name="' + data.poolName + '"/> ' + data.poolName },
		function(data) { return '<a onclick="moveBackPool(this.parentNode.parentNode.rowIndex, \''+ data.poolId +'\',\''+ data.poolName + '\')"><img src="' + crhCommonUrl('../images/delete2.png') + '" alt="' + texts.mouseoverPooldetach + '" width="16" height="16" border="0"></a>' }
		];
		
		function Data(id,name){
			this.poolId=id;
			this.poolName = name;
		}
		var dd = new Data(id, name);
		dwr.util.addRows( "selectedPoolsTbody", [dd], cellFuncs, { escapeHtml:false });
}

// LKWStammdatenData3Tile.jsp
function load_map(lat, lng, lat1, lng1) {
	
	var gmap = new google.maps.Map(Ext.getDom("map"), defaultLogonGmapOptions);
	gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
	var marker = null;
	var latlng = new google.maps.LatLng(defaultMapCenter.lat, defaultMapCenter.lng);
	var zoom = defaultMapCenter.zoomLevel;
	
	if ((lng !== '') && (lat !== '')){
		latlng = new google.maps.LatLng(lat, lng);
		zoom = 10;
	}
	gmap.setCenter(latlng);
	gmap.setZoom(zoom);

	if ((lng !== '') && (lat !== '')){
		marker = new google.maps.Marker({map: gmap, position: latlng});
	}
	if ((lng1 !== '') && (lat1 !== '')){
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

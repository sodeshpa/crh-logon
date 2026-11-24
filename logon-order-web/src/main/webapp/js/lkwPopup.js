function load_lkwpopup_map(lat, lng) {

	if (!gmapEnabled) {
		
		return;
	}
	var gmap = new google.maps.Map(Ext.getDom("map"), defaultLogonGmapOptions);
	gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
	
	var latlng = new google.maps.LatLng(defaultMapCenter.lat, defaultMapCenter.lng);
	var zoom = defaultMapCenter.zoomLevel;
	if ((lng !== '') && (lat !== '')){
		latlng = new google.maps.LatLng(lat, lng);
		zoom = 10;
		var marker = new google.maps.Marker({map:gmap, position: latlng});
	}
	gmap.setCenter(latlng);
	gmap.setZoom(zoom);
}

function Values(id, event, fromDate, fromTime, untilDate, untilTime){
	//this stores the info about the currently edited/added row
	this.id = id;
	this.event = event;
	this.fromTime = fromTime;
	this.fromDate= fromDate;
	this.untilDate = untilDate;
	this.untilTime = untilTime;
	this.setBackValues = function(){
		//Ff deletes the values from innerHtml
		document.getElementById("inputTruckUnavailabilityId").value = this.id;
		document.getElementById("inputEvent").value = this.event;
		document.getElementById("inputTimeFrom").value = this.fromTime;
		document.getElementById("inputDateFrom").value = this.fromDate;
		document.getElementById("inputDateUntil").value = this.untilDate;
		document.getElementById("inputTimeUntil").value =this.untilTime
	}
	this.setValuesFromDocument = function(){
		this.id = document.getElementById("inputTruckUnavailabilityId").value;
		this.event = document.getElementById("inputEvent").value;
		this.fromTime = document.getElementById("inputTimeFrom").value;
		this.fromDate= document.getElementById("inputDateFrom").value;
		this.untilDate =document.getElementById("inputDateUntil").value;
		this.untilTime = document.getElementById("inputTimeUntil").value ;
	}
	this.checkEmpty = function(){
		var val = true;
		var a = "";
		if (this.event === ""){
			Logon.message.info(lkwPopupInfo.errMsgs[0]);
			val = false;
		}
		if (this.fromTime === "" || this.untilTime === ""){
			Logon.message.info(lkwPopupInfo.errMsgs[1]);
			val = false;
		}
		if (this.fromDate === "" || this.untilDate === ""){
			Logon.message.info(lkwPopupInfo.errMsgs[2]);
			val = false;
		}
		return val;
	}
}

var lkwPopupInner;
var vals = new Values();
var row;

function saveTruckUnAvailability() {
	if (!document.getElementById("inputTruckUnavailabilityId")){
		return;
	}
	vals.setValuesFromDocument();
	if(!vals.checkEmpty()){
		return;
	}
	if (!checkValidity()){
		return;
	}
	if (document.getElementById("inputTruckUnavailabilityId").value !== ""){
		a = {
				"inputAction" :"edit"
		};			
	}
	else{
		a = {
				"inputAction" :"save"
		};		
	}
	addFormElements(a, "LKWPopupForm");
	lkwPopupInner = document.getElementById('logonPopup').innerHTML;
	showPopup('LKWPopup.do',
			lkwPopupInfo.extJsLoading, a);
}

function closePopup(){
	logonPopup = Ext.get("logonPopup");
	hidePopup();
	refreshTrucks();
}


function deleteTruckUnAvailability(id) {
	if (document.getElementById("inputTruckUnavailabilityId") && document.getElementById("inputTruckUnavailabilityId").value != id){
		return;
	}
	if (!confirm(lkwPopupInfo.deleteConfirm)){
		return;
	}
	a = {
			"inputAction" :"delete",
			"inputTruckUnavailabilityId": id
	};
	addFormElements(a, "LKWPopupForm");

	showPopup('LKWPopup.do',
			lkwPopupInfo.extJsLoading, a);
}


function editRow( rowEdited ,id, event, fromDate, fromTime, untilDate, untilTime){
	if (document.getElementById("inputTruckUnavailabilityId") && document.getElementById("inputTruckUnavailabilityId").value != id){
		return;
	}
	row = rowEdited;
	replaceRow(rowEdited);	
	vals = new Values(id, event, fromDate, fromTime, untilDate, untilTime);
	vals.setBackValues();
}
function replaceRow(){
	row.cells[0].innerHTML ='<input type = "hidden" name = "inputTruckUnavailabilityId" id="inputTruckUnavailabilityId" value=""/><input name="inputEvent" style="width:100%" type = "text" id="inputEvent" value=""/>';
	row.cells[1].innerHTML = '<input type = "text" id="inputDateFrom" name="inputDateFrom" value=""/>' ;
	row.cells[2].innerHTML = '<input type = "text" id="inputTimeFrom" name = "inputTimeFrom" value=""/>';
	row.cells[3].innerHTML = '<input type = "text" id="inputDateUntil" name="inputDateUntil" value=""/>';
	row.cells[4].innerHTML ='<input type = "text" id="inputTimeUntil" name = "inputTimeUntil" value=""/>';
	row.cells[5].innerHTML ='';
	applyExtElements();
}

function addGeneralRow(){
	if (document.getElementById("inputTruckUnavailabilityId")){
		//if something is edited do not add new
		return;
	}

	var cellFuncs = [
					function(data) { return '<input type = "hidden" name = "inputTruckUnavailabilityId" id="inputTruckUnavailabilityId" value=""/><input name="inputEvent" style="width:100%" type = "text" id="inputEvent" value=""/>'; },
					function(data) { return '<input type = "text" id="inputDateFrom" name="inputDateFrom" value=""/>' },
					function(data) { return '<input type = "text" id="inputTimeFrom" name = "inputTimeFrom" value=""/>' },
					function(data) { return '<input type = "text" id="inputDateUntil" name="inputDateUntil" value=""/>' },
					function(data) { return '<input type = "text" id="inputTimeUntil" name = "inputTimeUntil" value=""/>' },
					function(data) { return '' }
					];

	dwr.util.addRows( "unavailabilities", [""], cellFuncs, { escapeHtml:false });
	applyExtElements();

}

var lkwpopupDateFrom;
var lkwpopupTimeFrom;
var lkwpopupTimeUntil;
var lkwpopupDateUntil;

function applyExtElements(){
	lkwpopupDateFrom = new Ext.form.DateField({
		id: 'inputDateFrom',
		format: 'd.m.y',
		altFormats: 'd.m.Y',
		applyTo: 'inputDateFrom',
		allowBlank: false,
		style: 'margin-bottom: 2px;width: 90px;',
		width:'90px',
		//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
		markInvalid:function(msg){
			msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
			Ext.form.DateField.superclass.markInvalid.call(this,msg);
		}
	});

	lkwpopupTimeFrom = new Ext.form.TimeField({
		id: 'inputTimeFrom',
		format: 'H:i',
		applyTo: 'inputTimeFrom',
		allowBlank: false,
		style: 'margin-bottom: 2px;width: 70px;',
		width:'70px'
	});	

	lkwpopupTimeUntil = new Ext.form.TimeField({
		id: 'inputTimeUntil',
		format: 'H:i',
		applyTo: 'inputTimeUntil',
		allowBlank: false,
		style: 'margin-bottom: 2px;width: 70px;',
		width:'70px'
	});

	lkwpopupDateUntil = new Ext.form.DateField({
		id: 'inputDateUntil',
		format: 'd.m.y',
		altFormats: 'd.m.Y',
		applyTo: 'inputDateUntil',
		allowBlank: false,
		style: 'margin-bottom: 2px;width: 90px;',
		width:'90px',
		//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
		markInvalid:function(msg){
			msg = msg.replace(this.format, "dd.mm.yyyy or dd.mm.yy");
			Ext.form.DateField.superclass.markInvalid.call(this,msg);
		}		
	});
}

function checkValidity(){
	var valid = lkwpopupDateFrom.validate();
	valid = valid && lkwpopupTimeFrom.validate();
	valid = valid && lkwpopupDateUntil.validate();
	valid = valid && lkwpopupTimeUntil.validate();
	return valid;
}
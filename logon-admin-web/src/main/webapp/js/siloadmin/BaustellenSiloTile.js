// BaustellenSiloTile.jsp
function updatenameShowRFIDPopup(){
	var name = document.getElementById("inputSiloName").value;
	AdminRFIDPopupAjaxService.selectSiloByName(name, function(){
		showPopup('../AdminRFIDPopup.do');
	});
}

function selectSilo(id){
	var butt = document.getElementById("saveButton");
	if (butt !== null){		
		document.getElementById("saveButton").value = texts.buttonsSave;
	}
	document.getElementById("siloId").value = id;
	document.getElementById("inputAction").value=forwards.LOAD_SILO;
	Ext.get('admin_SiloAdminForm').dom.submit();
}

function removeShipto(){
	document.getElementById("inputAction").value=forwards.REMOVE_SHIPTO;
	Ext.get('admin_SiloAdminForm').dom.submit();
}

function saveSilo(isNew){
	var comment = document.getElementById("inputComment");
	if (comment.value.length>200 ) {
		var bool = confirm(texts.errorsCommentLong);
		if(bool){
			comment.value=comment.value.substring(0,200)
		}
		else{
			return false;
		}
	}

	if (isNew ){
		document.getElementById("inputAction").value=forwards.SAVE_NEW;
	}
	else{
		document.getElementById("inputAction").value=forwards.SAVE;
	}
	
	Ext.get('admin_SiloAdminForm').dom.submit();
}

function overwriteDefault(bool, defaultVal){
		var unloadingTime = document.getElementById("inputUnloadingTime");
	if (bool){
		unloadingTime.disabled = false;
	}
	else{
		unloadingTime.disabled = true;
		unloadingTime.value = defaultVal;
	}
}

function resetSilo(){		
	document.getElementById("inputAction").value=forwards.RESET;
	Ext.get('admin_SiloAdminForm').dom.submit();
}

function addNewSilo(){
	document.getElementById("inputAction").value=forwards.CREATE_SILO;
	Ext.get('admin_SiloAdminForm').dom.submit();
}

// SiloData3Tile.jsp
function load_map(lat1, lng1) {
	
  var gmap = new google.maps.Map(map, defaultLogonGmapOptions);
	gmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	var trafficLayer = new google.maps.TrafficLayer();
	trafficLayer.setMap(gmap);
 
  var latlng;
  if ((lng1 !== '') && (lat1 !== '') && (lng1 !== null) && (lat1 !== null) && (lng1 !== undefined) && (lat1 !== undefined)){
		latlng = new google.maps.LatLng(lat1, lng1);
		var marker = new google.maps.Marker({map: gmap, position: latlng});
  }
  else{
	latlng = new google.maps.LatLng(defaultMapCenter.lat, defaultMapCenter.lng);
  }
	var zoom = defaultMapCenter.zoomLevel;
  gmap.setCenter(latlng);
  gmap.setZoom(zoom);
}

// SiloShiptoSearchTile.jsp
function initSiloShiptoSearch() {
	
	var shiptoRecordType = Ext.data.Record.create([
		{name: 'shiptoNr', mapping: 'sapId'},
		{name: 'shiptoId', mapping: 'shiptoId'},    
		{name: 'shiptoName', mapping: 'name1'},    
		{name: 'postalCode', mapping: 'postalCode'},    
		{name: 'city', mapping: 'city'}
	]);
	
	var shipToListStore = new Ext.data.Store({
		proxy: new Ext.data.DWRProxyForPager(SiloAdminAjaxService.fetchShiptoList, true),
		reader: new Ext.data.ListRangeReader({id:'shiptoId', totalProperty:'totalSize'}, shiptoRecordType),
		remoteSort: true
	});

	// Custom rendering Template
	var resultTpl = new Ext.XTemplate(
		'<table class="classic"><thead><tr>',
		'<th width="100">' + texts.shiptoId + '</th>',
		'<th width="300">' + texts.shiptoName + '</th>',
		'<th width="100">' + texts.shiptoOrt + '</th>',
		'<th width="100">' + texts.shiptoPlz + '</th>',
		'</tr></thead><tbody>',
		   '<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
		   '<td>{shiptoNr}</td>',        
		   '<td>{shiptoName}</td>',
		   '<td>{city}</td>',
		   '<td>{postalCode}</td>',
		   '</tr></tpl>',
		   '</tbody></table>'
	);
	   
	var search = new Ext.form.ComboBox({
		   store: shipToListStore,
		   displayField:'id',
		   typeAhead: false,
		   loadingText: texts.exjsloading,
		   maxHeight:800,
		   minChars:1,
		   pageSize:20,
		   hideTrigger:true,
		   tpl: resultTpl,
		   applyTo: 'search',
		   itemSelector: 'tr.listitem',
		   onSelect: function(record){
			   document.getElementById("inputAction").value = forwards.CHANGE_SHIPTO;
			   document.getElementById("inputShiptoId").value=record.data.shiptoId;
			   search.collapse();
			   Ext.get('admin_SiloAdminForm').dom.submit();
		   }
	});
	
	Ext.get("search").setTop("0px");
}
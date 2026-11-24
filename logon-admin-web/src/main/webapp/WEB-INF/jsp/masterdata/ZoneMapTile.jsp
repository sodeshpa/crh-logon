<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ZoneMapTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@page
	import="com.crh.logon.admin.web.actions.masterdata.ZoneMapAction"%>

<link rel="stylesheet"
	href="<crh-common:url value="/css/Ext.ux.form.ColorPickerFieldPlus.css"/>"
	type="text/css">
<script type="text/javascript"
	src="<crh-common:url value='/dwr/interface/ZoneMapAjaxService.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/js/ext-ux/Ext.ux.form.ColorPicker.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/js/ext-ux/Ext.ux.form.ColorPickerFieldPlus.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<%@page import="com.crh.logon.constants.Constant"%>


<script type="text/javascript">

	var texts = {
		select: '<fmt:message key="option.select"/>',
		exjsloading: '<fmt:message key="shippingpoint.text.exjsloading"/>',
		messageBoxDelete: '<fmt:message key="zonemap.messagebox.delete"/>',
		messageBoxCancel: '<fmt:message key="zonemap.messagebox.cancel"/>',
		messageBoxConfirm: '<fmt:message key="zonemap.messagebox.confirm"/>',
		messageBoxText: '<fmt:message key="zonemap.messagebox.text"/>'
	};

	var gmap;
	var polygonMap = {}; 
	var polygonDataMap = {};

	//loaded init polygon list (TrspZonePolygonEntity)
	var initPolygonlist; 

	var polygonListSize = <c:out value="${admin_ZoneMapForm.polygonListSize}"/>;
	var selectedPolygonId = <c:out value="${admin_ZoneMapForm.selectedPolygonId}"/>;
	var squareNotSelectedImage = '<crh-common:url value="/images/square_black.png"/>';
	var squareSelectedImage = '<crh-common:url value="/images/square_red.png"/>';
	var squareOnMouseImage = '<crh-common:url value="/images/square_grey.png"/>';

	var squareNotSelected;
	var squareSelected;
	var squareOnMouse;
	
	//sets value when polygon is added or removed 
	var polygonAction = '';
	var zIndex = [['-','-1'], ['0','0'], ['1','1'], ['2', '2'], ['3','3'], ['4','4'], ['5','5'], ['6','6'], ['7','7'], ['8','8'], ['9','9']];
    

	GMapAPILoader.onAPIReady( function() {
		loadMap();
		loadAllPolygons();
	});

	Ext.onReady( function() {
		GMapAPILoader.load();
		initPolygonList();
	});


	//--------------creates zone drop downs and color picers for all polygons-----------------// 
	//--------------on the right of the screen-----------------//
	function initPolygonList() {
		
		// DataStore definitions for zIndex combo box
	    var storeZindex = new Ext.data.SimpleStore({
	        fields: ['name','zindex'],
	        data : zIndex 
	    });
		
		// --- Data record and DataStore definitions ---
		var zoneRec = Ext.data.Record.create([
		    {name: "trspZoneCd", mapping: "trspZoneCd"}
		]);
	    
		var zoneStore = new Ext.data.Store( {
			proxy :new Ext.data.DWRProxy( ZoneMapAjaxService.fetchTrspZoneList),
			reader :new Ext.data.ObjectReader( {
				id :"trspZoneCd"
			}, zoneRec),
			remoteSort: true
		});
		
		zoneStore.load();	
		
		
		for ( var i = 0; i < polygonListSize; i++) {
			var polygonId = document.getElementById('hiddenPolygonId'+i).value;

			//color pickers
			var cp = new Ext.ux.form.ColorPickerFieldPlus({
				id: 'colorId' + polygonId,
				renderTo: 'divColorId' + polygonId});
			var color = document.getElementById('hiddencolorId'+polygonId).value; 
			//set color box value and color
			cp.setValue(color); //colorBox.set({value: color});
			var colorBox = Ext.get('colorId' + polygonId);
			var colorMenu = new Ext.ux.menu.ColorMenu();
			var invertColor = colorMenu.picker.rgbToHex(colorMenu.picker.invert(colorMenu.picker.hexToRgb(color)));
			colorBox.applyStyles('background: #' + (color || "ffffff") + '; color: #' + invertColor + ';');
			
			cp.on('change', function(field, newValue, oldValue){
				setSelectedColor(field.getId(), newValue);
			});
			
			//zone comboboxes
			polygonCombo = new Ext.form.ComboBox( {
		    	id: 'zoneCdId'+polygonId,
		        applyTo: 'zoneCdIdDummy'+polygonId,
		        store: zoneStore,
		        width: 120,
		        displayField: 'trspZoneCd',
		        valueField: 'trspZoneCd',
				typeAhead :true,
				minChars :2,
				mode :'local',
				forceSelection :true,
				triggerAction :'all',
				emptyText :texts.select ,
				selectOnFocus :true,
				loadingText: texts.exjsloading
			});
			
			polygonCombo.on('select', function(combo) {
				document.getElementById('hidden' + combo.getId()).value = combo.getValue();
			});
			
			var zone = document.getElementById('hiddenzoneCdId' + polygonId).value;
			polygonCombo.setValue(zone);
			
		    // zIndex combo box
		    var comboZindex = new Ext.form.ComboBox({
		    	id: 'zIndex'+polygonId,
		    	applyTo: 'zIndexDummy'+polygonId,
		    	hideTrigger : true,
		        store: storeZindex,
		        displayField:'name',
		        valueField: 'zindex',
		        typeAhead: true,
		        mode: 'local',
				triggerAction :'all',
				emptyText : '-',
				selectOnFocus :true,
				forceSelection :true,
			 	editable: false,
				loadingText: texts.exjsloading
		    });
			var zindex = document.getElementById('hiddenzIndex' + polygonId).value;
			comboZindex.setValue(zindex);
			
			comboZindex.on('select', function(combo) {
				document.getElementById('hidden' + combo.getId()).value = combo.getValue();
			});
		}

	}

	//----------creates a google map--------------//
	function loadMap() {
		var divMap = Ext.getDom('zoneMap');

		//set map height
		var windowHeight = getWindowHeight();
		var mapHeight = ((windowHeight - 250) > 520) ? (windowHeight - 250) : 520;
		divMap.style.height = mapHeight + "px";

		gmap = new google.maps.Map(divMap, defaultLogonGmapOptions);
		gmap.setOptions({mapTypeId : google.maps.MapTypeId.ROADMAP,
						 disableDoubleClickZoom : true
						 });
		var trafficLayer = new google.maps.TrafficLayer();
	    trafficLayer.setMap(gmap);
		
		google.maps.event.addListener(gmap, "click", function(event){leftClick(event);});
		var latlng = new google.maps.LatLng(<%=Constant.MAP_CENTER_LAT%>, <%=Constant.MAP_CENTER_LNG%>);
		gmap.setCenter(latlng);
		gmap.setZoom(<%=Constant.MAP_CENTER_ZOOM_LEVEL%>);

		// Square marker icons
		squareSelected = new google.maps.MarkerImage(squareSelectedImage,
				  new google.maps.Size(11, 11),
				  new google.maps.Point(0,0),
				  new google.maps.Point(5, 5));
		  
		squareOnMouse = new google.maps.MarkerImage(squareOnMouseImage,
				  new google.maps.Size(11, 11),
				  new google.maps.Point(0,0),
				  new google.maps.Point(5, 5));
		  
		squareNotSelected = new google.maps.MarkerImage(squareNotSelectedImage,
				  new google.maps.Size(11, 11),
				  new google.maps.Point(0,0),
				  new google.maps.Point(5, 5));
	}
	
	
	//----------set hidden color field and polygon on the map--------------//
	function setSelectedColor(idStr, color){
		
		document.getElementById('hidden' + idStr).value = color; 
		id = idStr.substring(7);
		if (typeof (polygonDataMap[id]) != "undefined") {
			
		    polygonMap[id].setOptions({strokeColor : '#' + color, 
									   strokeWeight : 3, 
									   strokeOpacity : 0.8,
									   fillColor : '#' + color,
									   fillOpacity : 0.3
									   });
		}
	}

	
	//----------set red dots around selected polygon------------//
	function setSelectedPolygon(polygonId){

		var newPolygonMarkersPrev = new Array();
		var polygonMarkersPrev = polygonDataMap[selectedPolygonId];

		//unset draggable markers for the previous selected polygon
		if (typeof (polygonMarkersPrev) != "undefined") {
			for ( var n = 0; n < polygonMarkersPrev.length; n++) { 
				newMarker = getMarker();
				//remove old marker from the map 
				polygonMarkersPrev[n].setMap(null);
				//add new marker
				newPolygonMarkersPrev.push(newMarker);
				newMarker.setMap(gmap);
				newMarker.setPosition(polygonMarkersPrev[n].getPosition());
			}
			polygonDataMap[selectedPolygonId] = newPolygonMarkersPrev;
		}
		
		var newPolygonMarkers = new Array();
		var polygonMarkers = polygonDataMap[polygonId];
				
		//set draggable markers for the selected polygon
		if (typeof (polygonMarkers) != "undefined") { 	
			for ( var n = 0; n < polygonMarkers.length; n++) { 
				newMarker = getSelectedPolygonMarker();
				//remove old marker from the map 
				polygonMarkers[n].setMap(null);
				//add new marker
				newPolygonMarkers.push(newMarker);
				newMarker.setMap(gmap);
				newMarker.setPosition(polygonMarkers[n].getPosition());
			}
			polygonDataMap[polygonId] = newPolygonMarkers;
		}
		
		//assign new polygon id
		selectedPolygonId=polygonId;
	}

	
	//-------------AJAX function to load polygon data-------------------//
	function loadAllPolygons() {
		ZoneMapAjaxService.loadPolygons('parameter',loadAllPolygonsReturn);
	}

	
	//-------------return function from AJAX service-------------------//
	function loadAllPolygonsReturn(list) {

		var initPolygonlist = list[0];
		var polygonList = list[1];

		var pMarkers = new Array();
		//map of grouped polygons
		polygonDataMap = {}; 
		var polygonColorMap = {}; 

		//init polygon map
		for ( var i = 0; i < initPolygonlist.length; i++) {
			polygonDataMap[initPolygonlist[i].trspZonePolygonId] = new Array();
		}


		var curMarker;
		if (polygonList.length > 0) {
			curMarker = polygonList[0]; 
			//polygonDataMap[curMarker.trspZonePolygon.trspZonePolygonId] = new Array();
			polygonColorMap[curMarker.trspZonePolygon.trspZonePolygonId] = curMarker.trspZonePolygon.colorRgb;
		}
	
		
		for ( var i = 0; i < polygonList.length; i++) {

			if (curMarker.trspZonePolygon.trspZonePolygonId == polygonList[i].trspZonePolygon.trspZonePolygonId) {
				//create not dragable marker
				marker = getMarker();
				//add new marker
				marker.setMap(gmap);
				var latLng = new google.maps.LatLng(polygonList[i].coordLat, polygonList[i].coordLon);
				marker.setPosition(latLng);

			    //add marker to the polygon map
			    polygonDataMap[curMarker.trspZonePolygon.trspZonePolygonId].push(marker);
			    curMarker = polygonList[i]; 
			}
			else {
				//create not dragable marker
				marker = getMarker();
				marker.setMap(gmap);
				var latLng = new google.maps.LatLng(polygonList[i].coordLat, polygonList[i].coordLon);
				marker.setPosition(latLng);

				//create a new polygon map entry and add marker
				//polygonDataMap[polygonList[i].trspZonePolygon.trspZonePolygonId] = new Array();
				polygonDataMap[polygonList[i].trspZonePolygon.trspZonePolygonId].push(marker);
				curMarker = polygonList[i]; 

				//create polygon color map (just for init stage)
				polygonColorMap[curMarker.trspZonePolygon.trspZonePolygonId] = curMarker.trspZonePolygon.colorRgb;
			}
		}

		//draw all polygons
		for (var key in polygonDataMap) {
			polygonMap[key] = drawPolygon(polygonDataMap[key], '#'+polygonColorMap[key]); 
		}

		//make polygon draggable (red dots)
		setSelectedPolygon(selectedPolygonId);
	}

	
	//--------------returns not dragable white marker---------------//
	function getMarker() {
		var marker = new google.maps.Marker({
			icon :squareNotSelected,
			draggable :false,
			raiseOnDrag : false
		});

		google.maps.event.addListener(marker, "click", function(point) {

			var foundFlag = false;
			var selPolygonId;
			
			//find polygon of the marker
			for (var key in polygonDataMap) {
				var polygonMarkers = polygonDataMap[key]; 

				for ( var n = 0; n < polygonMarkers.length; n++) {
					if (polygonMarkers[n] == marker) {
						foundFlag = true;
						selPolygonId = key;
						break;
					}
				}
				
				if (foundFlag) {
					break;
				}
			}

			//make polygon draggable (red dots)
			setSelectedPolygon(selPolygonId)

			form = Ext.getDom('ZoneMapForm');
			for (i=0;i < form.selectedPolygonId.length; i++) {
				if (form.selectedPolygonId[i].value == selectedPolygonId) {
					form.selectedPolygonId[i].checked = true;
					break;
				}
			}
		});
		
		return marker;
	}

	
	//--------------returns dragable red marker---------------//
	function getSelectedPolygonMarker() {
		// Make markers draggable
		var marker = new google.maps.Marker({
			icon :squareSelected,
			draggable :true,
			raiseOnDrag : false
		});

		google.maps.event.addListener(marker, "drag", function() {
			var color = document.getElementById('hiddencolorId'+selectedPolygonId).value; 
			polygonMap[selectedPolygonId].setMap(null);
			polygonMap[selectedPolygonId] = drawPolygon(polygonDataMap[selectedPolygonId], '#'+color);
		});

		google.maps.event.addListener(marker, "mouseover", function() {
			marker.setIcon(squareOnMouse);
		});

		google.maps.event.addListener(marker, "mouseout", function() {
			marker.setIcon(squareSelected);
		});

		google.maps.event.addListener(marker, "click", function() {
			//remove clicked marker
			var remMarkers = polygonDataMap[selectedPolygonId];
			for ( var n = 0; n < remMarkers.length; n++) {
				if (remMarkers[n] == marker) {
					remMarkers[n].setMap(null);
					break;
				}
			}
			
			remMarkers.splice(n, 1);
			polygonDataMap[selectedPolygonId] = remMarkers;
			   			
			var color = document.getElementById('hiddencolorId'+selectedPolygonId).value; 
			polygonMap[selectedPolygonId].setMap(null);
			polygonMap[selectedPolygonId] = drawPolygon(polygonDataMap[selectedPolygonId], '#'+color);
		
			
		});
		
		return marker;
	}

	
	//---------returns polygon for a given marker array and color-------------//
	function drawPolygon(pMarkers, color) {

		var pPoints = new Array();
		for (i = 0; i < pMarkers.length; i++) {
			pPoints.push(pMarkers[i].getPosition());
		}
		
		// Close the shape with the last line or not
		if (typeof (pMarkers[0]) != "undefined") {
			pPoints.push(pMarkers[0].getPosition());
		}
		var pShape = new google.maps.Polygon({paths : pPoints,
											   map: gmap, 
											   strokeColor : color, 
											   strokeWeight : 3, 
											   strokeOpacity : 0.8,
											   fillColor : color,
											   fillOpacity : 0.3
											   });

		return pShape;
	}


	//-----------adds new marker to the selectd polygon-------------------//
	function leftClick(event) {
		if (event.latLng && polygonDataMap[selectedPolygonId]) {
			newMarker = getSelectedPolygonMarker();
			newMarker.setMap(gmap);
			newMarker.setPosition(event.latLng);
			//polygonDataMap[selectedPolygonId] = new Array();
			polygonDataMap[selectedPolygonId].push(newMarker);
			var color = document.getElementById('hiddencolorId'+selectedPolygonId).value; 
			polygonMap[selectedPolygonId].setMap(null);
			polygonMap[selectedPolygonId] = drawPolygon(polygonDataMap[selectedPolygonId], '#'+color);
		}
	}

	// returns the size of the current window
	function getWindowHeight() {
		var myHeight = 0;
		if (typeof (window.innerWidth) == 'number') {
			// Non-IE
			myHeight = window.innerHeight;
		} else if (document.documentElement
				&& (document.documentElement.clientHeight)) {
			// IE 6+ in 'standards compliant mode'
			myHeight = document.documentElement.clientHeight;
		} else if (document.body && (document.body.clientHeight)) {
			// IE 4 compatible
			myHeight = document.body.clientHeight;
		}
		return myHeight;
	}


	
	//---------------------------------------------------------------//
	//-------Functions to add, remove and save polygons--------------// 
    //---------------------------------------------------------------// 
	function savePolygonsReturn(status){

		//real save!
		polygonAction = '<%=ZoneMapAction.FORWARD_SAVE%>';
		form = Ext.getDom('ZoneMapForm');
		p = {'formAction' : polygonAction}; 
		addFormElements(p, form);

		var diva = Ext.get('DivPolygon');
		diva.load( {
			url :'ZoneMap.do',
			params : p,
			text :texts.exjsloading,
			scripts :true
		});
	}


	function savePolygons() {

		//save polygon coordintes. Sending map to AJAX service
		var mapToSave = {};
		for (var key in polygonDataMap) {
			var list = polygonDataMap[key];
			var mapTmp = {};
			for (i = 0; i < list.length; i++) {
				mapTmp[i + '_lat'] = list[i].getPosition().lat();
				mapTmp[i + '_lng'] = list[i].getPosition().lng();
			}
			mapToSave[key] = mapTmp;
		}
		
		ZoneMapAjaxService.savePolygons(mapToSave, savePolygonsReturn);
	}

	function addPolygon() {

		polygonAction = '<%=ZoneMapAction.FORWARD_ADD%>';
		
	    form = Ext.getDom('ZoneMapForm');
	 	p = {'formAction' : polygonAction}; 
	 	addFormElements(p, form);

	 	
		var diva = Ext.get('DivPolygon');
		diva.load( {
			url :'ZoneMap.do',
			params : p,
			text :texts.exjsloading,
			scripts :true
		});
	}

	function removePolygon(index, selPolId) {
		Ext.MessageBox.buttonText.yes = texts.messageBoxDelete;
		Ext.MessageBox.buttonText.no = texts.messageBoxCancel;

		Ext.MessageBox
				.confirm(
						texts.messageBoxConfirm,
						texts.messageBoxText,
						function(btn) {
							if (btn == 'yes') {

								//remove polygon and markers form the map
								polygonMap[selPolId].setMap(null);
								var remMarkers = polygonDataMap[selPolId];
								for ( var n = 0; n < remMarkers.length; n++) {
									remMarkers[n].setMap(null);
								}
								delete polygonDataMap[selPolId];
								delete polygonMap[selPolId];
								
								
								polygonAction = '<%=ZoneMapAction.FORWARD_REMOVE%>';
								
							    form = Ext.getDom('ZoneMapForm');
							 	p = {'formAction' :polygonAction, 
							 		 'index' : index}; 
							 	addFormElements(p, form);
								 		 
								var diva = Ext.get('DivPolygon');
								diva.load( {
									url :'ZoneMap.do',
									params : p,
									text : texts.exjsloading,
									scripts :true
								});
							}
						});
	}
		
	function cancelPolygons() {
		form = Ext.getDom('ZoneMapForm');
		Ext.query("input[name=formCancel]", form)[0].value = '<%=ZoneMapAction.FORWARD_CANCEL%>';
		form.submit();
	} 


</script>

<body>
<jsp:include page="../SubTab9Tile.jsp" />

<!-- Begin table 3 rows -->
<table class="big_form2">

	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.masterdata" />
		/ <fmt:message key="tabsubitem.9.4" /></th>
	</tr>
	<tr>

		<td><!-- Beginn Submenu 2-2-1-1 --> <jsp:include
			page="../Sub4Tab9Tile.jsp" /> <html:form action="ZoneMap.do"
			styleId="ZoneMapForm" style="margin-top: 0px;">

			<html:hidden property="formCancel" value="" />
			
			<table class="big_form2" border="0" width="100%"
				style="padding: 0px; margin: 0px;">
				<tr>
					<td>
					<div id="zoneMap" style="width: 100%; margin-top: 0px;overflow:hidden"
						class="border">map</div>
					</td>
					<td style="width: 400;" align="right"><input type="button"
						class="SmallButtonStyle"
						value="<fmt:message key="zonemap.text.addpolygon"/>"
						onclick="addPolygon()" />
					<div class="scrollist" id="DivPolygon" style="height: 500px; width: 400px">
					<jsp:include page="ZoneMapPolygon.jsp"></jsp:include></div>
					</div>
					<br>
					<input type="button" class="ViewButtonStyle"
						onClick="cancelPolygons()"
						value="<fmt:message key="zonemap.botton.cancel"/>">
						&nbsp;&nbsp;
					<input type="button" class="ViewButtonStyle"
						onClick="savePolygons()"
						value="<fmt:message key="zonemap.botton.save"/>">

					</td>
				</tr>
			</table>
		</html:form></td>
	</tr>
</table>
</body>
<%-- END $RCSfile: ZoneMapTile.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>


<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

	// --- Data record and DataStore definitions ---
	var vehicleRec = Ext.data.Record.create([
   	    {name: "compositeId" },
   	    {name: "vehicleId"},
   	 	{name: "vehicleType"},
   	 	{name: "vehicleName"}
   	]);
	var extOrderSearchRec = Ext.data.Record.create([
		{name: "compositeId",               mapping: "entity.compositeId"},
		{name: "extOrderId",                mapping: "entity.extOrderId"},
		{name: "extOrderNumber",            mapping: "entity.extOrderNumber"},
		{name: "shiptoName",                mapping: "entity.shiptoName"},
		{name: "shiptoLoadUnload",          mapping: "entity.shiptoLoadUnload"},
		{name: "shiptoContactPersonTxt",    mapping: "entity.shiptoContactPersonTxt"},
		{name: "shiptoStreetHouseNo",       mapping: "entity.shiptoStreetHouseNo"},
		{name: "shiptoCountry",             mapping: "entity.shiptoCountry"},
		{name: "shiptoPostalCode",          mapping: "entity.shiptoPostalCode"},
		{name: "shiptoCity",                mapping: "entity.shiptoCity"},
		{name: "shippingpointName",         mapping: "entity.shippingpointName"},
		{name: "spLoadUnload",              mapping: "entity.spLoadUnload"},
		{name: "spContactPersonTxt",        mapping: "entity.spContactPersonTxt"},
		{name: "spStreetHouseNo",           mapping: "entity.spStreetHouseNo"},
		{name: "spCountry",                 mapping: "entity.spCountry"},
		{name: "spPostalCode",              mapping: "entity.spPostalCode"},
		{name: "spCity",                    mapping: "entity.spCity"},
		{name: "materialName",              mapping: "entity.materialName"},
		{name: "orderedUomCd",              mapping: "entity.orderedUomCd"},
		{name: "orderedUomName",            mapping: "entity.orderedUomName"},
		{name: "orderedQuantity",           mapping: "entity.orderedQuantity"},
		{name: "loadedUomCd",               mapping: "entity.loadedUomCd"},
		{name: "loadedUomName",             mapping: "entity.loadedUomName"},
		{name: "loadedQuantity",            mapping: "entity.loadedQuantity"},
		{name: "deliveredUomCd",            mapping: "entity.deliveredUomCd"},
		{name: "deliveredUomName",          mapping: "entity.deliveredUomName"},
		{name: "deliveredQuantity",         mapping: "entity.deliveredQuantity"},
		{name: "deliveryFrom"},
		{name: "deliveryTo"},
		{name: "orderStatusCd",             mapping: "entity.orderStatusCd"},
		{name: "statusTxt",                 mapping: "entity.statusTxt"},
		{name: "poolName",                  mapping: "entity.poolName"},
		{name: "carrierName",               mapping: "entity.carrierName"},
		{name: "truckName",                 mapping: "entity.truckName"},
		{name: "extDeliveryNote",           mapping: "entity.extDeliveryNote"}
	]);

    var vehicleStore = new Ext.data.Store({
    	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.fetchVehicleList(skip, count, sort, query, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'compositeId', totalProperty:'totalSize'}, vehicleRec)
    });
    var orderSearchStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.searchExternalOrders(skip, count, sort, searchArea.searchFilters, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'compositeId', totalProperty:'totalSize'}, extOrderSearchRec), 
		remoteSort: true
    });

    var lastUpdateDat;
    // --- Search Area Handler Obj ---
	var searchArea = {
		pageSize: 25,
		searchFilters: {},
		getVisibleColumnWidths: function() {
			var widths = {};
			var columnModel = grid.getColumnModel();
			for (colName in defaultColumnWidths) {
				var index = columnModel.getIndexById(colName);
				if (index != -1 && !columnModel.isHidden(index)) {
					widths[colName] = columnModel.getColumnWidth(index);
				}
			}
			return widths;
		},
		initSearchCrits: function (forceSystemDefault, grid) {
			this.grid = grid;
			
			var defaultSetType = whichDefaultSetToLoad(forceSystemDefault);
			if (defaultSetType == 'user') {
				applyUserDefaultValues();
			}
			else if (defaultSetType == 'system') {
				applySystemDefaultValues();
			}
		},
		
		getVisibleColumnIndexes: function() {
			var indexes = {};
			var columnModel = grid.getColumnModel();
			for (colName in defaultColumnIndexes) {
				var index = columnModel.getIndexById(colName);
				if (index != -1 && !columnModel.isHidden(index)) {
					indexes[colName] = index;
				}
			}
			return indexes;
		},
		
		getSearchCrits: function() {
			
			if ( (deliveryFromDat.getValue()) && (deliveryToDat.getValue()) && (deliveryFromDat.getValue() > deliveryToDat.getValue()) ) {
				Logon.message.warning('<fmt:message key="ordersearch.error.invalid.delivery.earlier.later"/>');
				<%-- don't return undifned, that could lead to NPE's later--%>
				deliveryFromDat.setValue(null);
				deliveryToDat.setValue(null);
			}

			if (orderNumberText.getValue()) {

				deliveryFromDat.setValue(null);
				deliveryToDat.setValue(null);
				orderStatusCheckComboBox.selectAll();
				vehicleCombo.setValue(null);
			}
			else if (!deliveryFromDat.getValue()) {
				var d = new Date();
				d.setHours(0);
				d.setMilliseconds(0);
				d.setMinutes(0);
				d.setSeconds(0);
				deliveryFromDat.setValue(d);
			}
			
			var orderStatusList = orderStatusCheckComboBox.getCheckedValues();
			if ( (!orderStatusList) || (orderStatusList.length == 0) ) {
				orderStatusCheckComboBox.selectAll();
			}
			
			return {
				<%--  Note: when adding new filters, make sure to adjust the export function: see: BestellungSearchEntryAction --%>
				extOrderNumber: orderNumberText.getValue(),
				extDeliveryNote: deliveryNoteText.getValue(),
				deliveryFromDat: deliveryFromDat.getValue() ? deliveryFromDat.value:  undefined,
				deliveryToDat: deliveryToDat.getValue() ? deliveryToDat.value : undefined,
				statusList: toCommaDelimitedStr(orderStatusCheckComboBox.getCheckedValues()),
				vehicleCompositeId: vehicleCombo.getValue(),
				vehicleName: vehicleCombo.lastSelectionText
			}
		},
		searchOrders: function(startRecord) {
			var filters = this.getSearchCrits();
			this.searchFilters = this.getSearchCrits();
			startRecord = 0;
		    orderSearchStore.load({params:{start: startRecord, limit: this.pageSize, sort: '', query: filters}});
		    lastUpdateDat = new Date();
		},
		searchOrdersKeepPaging: function(){
			var startRecord = 0;
			if (!(pagingBar === undefined) && !(pagingBar.cursor === undefined) && !(pagingBar.pageSize === undefined)) {
				var activePage = Math.ceil((pagingBar.cursor + pagingBar.pageSize) / pagingBar.pageSize);
				startRecord = activePage * pagingBar.pageSize - pagingBar.pageSize;
				if (isNaN(startRecord)){
					startRecord = 0;
				}
			}
			this.searchOrders(startRecord);
		}
	}

// ---- Delivery from / to ----
var deliveryFromDat = new Ext.form.DateField({
	id: 'deliveryFromDat',
   	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
   	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
   	applyTo: 'deliveryFromDatStr',
   	width: 100
});

var deliveryToDat = new Ext.form.DateField({
	id: 'deliveryToDat',
   	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
   	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
   	applyTo: 'deliveryToDatStr',
   	width: 100
});
    
var orderNumberText = new Ext.form.TextField({
	id: 'orderNumberText',
	applyTo: 'orderNumber',
	maskRe: /\d/i 
});
    
var deliveryNoteText = new Ext.form.TextField({
	id: 'deliveryNoteText',
	applyTo: 'deliveryNote'
});

var orderStatusStore = new Ext.data.SimpleStore({
    fields: ['cd', 'txt', 'checked'],
    data : [ 
<c:forEach var="orderStatus" varStatus="status" items="${order_bestellung_BestellungExternalSearchForm.orderStatusList}">
	[
'<c:out value="${orderStatus.orderStatusTxPK.orderStatusBs.orderStatusCd}"/>',
		'<c:out value="${orderStatus.txt}"/>',
		true
	]
	<c:if test="${!status.last}">,</c:if>
</c:forEach>
    ]
});

var orderStatusCheckComboBox = new Ext.ux.CheckComboBox({
    store: orderStatusStore,
    displayField: 'txt',
    checkedField: 'checked',
    valueField: 'cd',
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    emptyText:'all',
    selectOnFocus: true,
    width: 180,
    listWidth: 200
});
orderStatusCheckComboBox.applyToMarkup('orderStatusField');

var vehicleCombo = new Ext.ux.TwinCombo({
	id: 'vehicleCombo',
	applyTo: 'vehicleCompositeId',
	store: new Ext.data.SimpleStore({
		fields: ['compositeId', 'vehicleName'],
		data : [
			<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.vehicleCompositeId}">
				['<c:out value="${order_bestellung_BestellungExternalSearchForm.vehicleCompositeId}" escapeXml="false"/>',
				'<c:out value="${order_bestellung_BestellungExternalSearchForm.vehicleName}" escapeXml="false"/>'
				]
			</c:if>
		]
	}),
	displayField: 'vehicleName',
	valueField: 'compositeId',
	pageSize: 20,
	width: 180,
	listWidth: 250,
	typeAhead: false,
	triggerAction: 'all',
	selectOnFocus: true
});

// Period selection, TODO boon 20091126: if the button with popup is going to be re-used, please make a component out of it.
var periodSelector = new Ext.ux.PeriodSelector({
	fromDateComponent: deliveryFromDat,
	toDateComponent: deliveryToDat
});
periodSelector.yesterday.text = '<fmt:message key="auftragbearbeitung.texte.gestern"/>'; 
periodSelector.today.text = '<fmt:message key="auftragbearbeitung.texte.heute"/>';
periodSelector.tomorrow.text = '<fmt:message key="auftragbearbeitung.texte.morgen"/>';
periodSelector.week.text = '<fmt:message key="auftragbearbeitung.texte.w"/>';
periodSelector.month.text = '<fmt:message key="auftragbearbeitung.texte.m"/>';
periodSelector.year.text = '<fmt:message key="auftragbearbeitung.texte.YTD"/>';

var el = document.createElement("div");
var periodPanel = new Ext.Panel({
	renderTo: el,
	layout: 'fit',
	border: true,
	items: [ periodSelector ]
});

var periodPanelLayer = new Ext.Layer();
periodPanelLayer.appendChild(periodPanel.getEl());

periodSelectorButton = new Ext.Button({
	text: '<img src="../images/add.gif"/>',
	template: new Ext.Template(
            '<table border="0" cellpadding="0" cellspacing="0"><tbody><tr>',
            '<td><em unselectable="on"><button type="{1}">{0}</button></em></td>',
            '</tr></tbody></table>'),
	handler: function() {
		periodPanelLayer.toggle();
		if (periodPanelLayer.isVisible()) {
			periodPanelLayer.alignTo('periodSelectorTriggerId');
		}
	},
	renderTo: 'periodSelectorTriggerId'
});

Ext.getDoc().on("mousedown", function(e) {
	if ( (periodPanelLayer.isVisible()) && 
			(!e.within(periodPanel.getEl())) &&
			(!e.within(periodSelectorButton.getEl())) ) { 
		periodPanelLayer.hide();
	}
});

/* functions for handling save personal settings (LOGON-6837) 
 - general code, used more places, needs to be moved in a separate place */
function whichDefaultSetToLoad(forceSystemDefault) {
	var applyUserDefaultSettings = false;
	<c:if test="${order_bestellung_BestellungExternalSearchForm.applyUserDefaultSettings}">
		applyUserDefaultSettings = true;
	</c:if>

	if (forceSystemDefault == true) {
		return "system";
	}
	else if (applyUserDefaultSettings == true) {
		return "user";
	}
	else {
		return "system";
	}
}

function applySystemDefaultValues() {
	resetValues();
	
	applyColumnIndexes();
	
	for (var col in defaultColumnWidths) {
		var width = defaultColumnWidths[col];
		applyColumnWidths(col, width, 'system');
	}
	
	grid.width = Ext.get('divSimpleSearch').getWidth();
	grid.render();
}

function applyUserDefaultValues() {
	//Delivery From field; date type
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.defaultDeliveryFromString}">
		deliveryFromDat.setValue('<c:out value="${order_bestellung_BestellungExternalSearchForm.defaultDeliveryFromString}"/>');
	</c:if>
	
	//Delivery To field; date type
	if (!deliveryToDat.getValue()) {
		<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.defaultDeliveryToString}">
			deliveryToDat.setValue('<c:out value="${order_bestellung_BestellungExternalSearchForm.defaultDeliveryToString}"/>');
		</c:if>
	}
	
	//Order status field; Ext.ux.CheckComboBox with Long
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.defaultOrderStatusList}">
		orderStatusCheckComboBox.selectNone();
		<c:forEach var="defaultOrderStatus" varStatus="status" items="${order_bestellung_BestellungExternalSearchForm.defaultOrderStatusList}">
			orderStatusCheckComboBox.setChecked(<c:out value="${defaultOrderStatus}"/>, true);
		</c:forEach>
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.vehicleCompositeId}">
		var vehicleCompositeId = '<c:out value="${order_bestellung_BestellungExternalSearchForm.vehicleCompositeId}"/>';
		vehicleCombo.preselectValue(vehicleCompositeId, true);
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.extDeliveryNote}">
		var extDeliveryNote = '<c:out value="${order_bestellung_BestellungExternalSearchForm.extDeliveryNote}"/>';
		deliveryNote.value = extDeliveryNote;
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wExtOrderNumber}">
		applyColumnWidths("extOrderNumber", <c:out value="${order_bestellung_BestellungExternalSearchForm.wExtOrderNumber}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wExtDeliveryNote}">
		applyColumnWidths("extDeliveryNote", <c:out value="${order_bestellung_BestellungExternalSearchForm.wExtDeliveryNote}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShiptoName}">
		applyColumnWidths("shiptoName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShiptoName}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShiptoLoadUnload}">
		applyColumnWidths("shiptoLoadUnload", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShiptoLoadUnload}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShiptoContactPersonTxt}">
		applyColumnWidths("shiptoContactPersonTxt", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShiptoContactPersonTxt}"/>, 'user');
	</c:if>
	
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShiptoStreetHouseNo}">
		applyColumnWidths("shiptoStreetHouseNo", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShiptoStreetHouseNo}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShiptoCountry}">
		applyColumnWidths("shiptoCountry", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShiptoCountry}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShiptoPostalCode}">
		applyColumnWidths("shiptoPostalCode", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShiptoPostalCode}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShiptoCity}">
		applyColumnWidths("shiptoCity", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShiptoCity}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wStatusTxt}">
		applyColumnWidths("statusTxt", <c:out value="${order_bestellung_BestellungExternalSearchForm.wStatusTxt}"/>, 'user');
	</c:if>
	
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wMaterialName}">
		applyColumnWidths("materialName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wMaterialName}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wOrderedQuantity}">
		applyColumnWidths("orderedQuantity", <c:out value="${order_bestellung_BestellungExternalSearchForm.wOrderedQuantity}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wOrderedUomName}">
		applyColumnWidths("orderedUomName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wOrderedUomName}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wDeliveredQuantity}">
		applyColumnWidths("deliveredQuantity", <c:out value="${order_bestellung_BestellungExternalSearchForm.wDeliveredQuantity}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wDeliveredUomName}">
		applyColumnWidths("deliveredUomName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wDeliveredUomName}"/>, 'user');
	</c:if>
	
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wShippingpointName}">
		applyColumnWidths("shippingpointName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wShippingpointName}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wSpLoadUnload}">
		applyColumnWidths("spLoadUnload", <c:out value="${order_bestellung_BestellungExternalSearchForm.wSpLoadUnload}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wSpContactPersonTxt}">
		applyColumnWidths("spContactPersonTxt", <c:out value="${order_bestellung_BestellungExternalSearchForm.wSpContactPersonTxt}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wSpStreetHouseNo}">
		applyColumnWidths("spStreetHouseNo", <c:out value="${order_bestellung_BestellungExternalSearchForm.wSpStreetHouseNo}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wSpCountry}">
		applyColumnWidths("spCountry", <c:out value="${order_bestellung_BestellungExternalSearchForm.wSpCountry}"/>, 'user');
	</c:if>
	
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wSpPostalCode}">
		applyColumnWidths("spPostalCode", <c:out value="${order_bestellung_BestellungExternalSearchForm.wSpPostalCode}"/>, 'user');
	</c:if>

	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wSpCity}">
		applyColumnWidths("spCity", <c:out value="${order_bestellung_BestellungExternalSearchForm.wSpCity}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wDeliveryFrom}">
		applyColumnWidths("deliveryFrom", <c:out value="${order_bestellung_BestellungExternalSearchForm.wDeliveryFrom}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wDeliveryTo}">
		applyColumnWidths("deliveryTo", <c:out value="${order_bestellung_BestellungExternalSearchForm.wDeliveryTo}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wPoolName}">
		applyColumnWidths("poolName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wPoolName}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wCarrierName}">
		applyColumnWidths("carrierName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wCarrierName}"/>, 'user');
	</c:if>
	
	<c:if test="${!empty order_bestellung_BestellungExternalSearchForm.wTruckName}">
		applyColumnWidths("truckName", <c:out value="${order_bestellung_BestellungExternalSearchForm.wTruckName}"/>, 'user');
	</c:if>

	grid.width = Ext.get('divSimpleSearch').getWidth();
	grid.render();
	
}

function applyColumnWidths(colName, width, typeOfDefaults) {
	var columnModel = grid.getColumnModel();
	var index = columnModel.getIndexById(colName);
	if (index != -1) {
		var visible;
		if (typeOfDefaults == 'user') {
			visible = (width && width > 0);
		}
		else if (typeOfDefaults == 'system') {
			visible = (defaultVisibleColumns.indexOf(colName) != -1);
		}
		if (visible == true) {
			columnModel.setColumnWidth(index, width);
		}
		columnModel.setHidden(index, !visible);
	}
}

function applyColumnIndexes() {
	
	for (var column in defaultColumns) {
		var col = defaultColumns[column];
		var id = col.id;
		col["customIndex"] = defaultColumnIndexes[id];
	}
	defaultColumns.sort(function(a, b) {
	    return a.customIndex - b.customIndex;
	});
	var cm = new Ext.grid.ColumnModel(defaultColumns);
	grid.reconfigure(grid.getStore(), cm);
}

function resetValues() {
	deliveryFromDat.setValue(null);
	deliveryToDat.setValue(null);
	orderStatusCheckComboBox.selectAll();
	vehicleCombo.setValue(null);
	deliveryNote.value = null;
	
	var d = new Date();
	d.setHours(0);
	d.setMilliseconds(0);
	d.setMinutes(0);
	d.setSeconds(0);
	deliveryFromDat.setValue(d);
	
	orderStatusCheckComboBox.selectAll();
}

function initStores() {
	var vehicleStore = new Ext.data.Store({
		proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.fetchVehicleList(skip, count, sort, query, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'compositeId', totalProperty:'totalSize'}, vehicleRec)
	});
	
	vehicleCombo.store = vehicleStore;
}

logonPopupHideCallback = searchArea.searchOrdersKeepPaging.createDelegate(searchArea, []);

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	
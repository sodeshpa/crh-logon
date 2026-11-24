// --- Data record and DataStore definitions ---
var truckRouteSearchRec = Ext.data.Record.create([
	{name: "truckRouteId"},
	{name: "startShippingpointName"},
	{name: "endShiptoBpSapId"},
	{name: "endShiptoBpName1"},
	{name: "endShiptoBpPostalCode"},
	{name: "endShiptoBpCity"},
	{name: "timeCalculatedMin"},
	{name: "sapTrspModeCd"},
	{name: "sapTrspModeTxt"},
	{name: "langCd"},
	{name: "timeCorrectionFactor"},
	{name: "timePlanned"},
	{name: "distanceCalculatedM"},
	{name: "distanceCorrectionFactor"},
	{name: "correctedDistance"},
	{name: "routeComment"}
]);

var truckRouteSearchStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { TruckRouteAjaxService.searchTruckRoutes(skip, count, sort, searchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'truckRouteId', totalProperty:'totalSize'}, truckRouteSearchRec), 
	remoteSort: true
});

var shippingpointRec = Ext.data.Record.create([
	{name: "shippingpointId"},
	{name: "shippingpointName"}
]);

var shippingpointStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { TruckRouteAjaxService.fetchShippingpointList(skip, count, query, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'shippingpointId', totalProperty:'totalSize'}, shippingpointRec)
});

var sapBpRec = Ext.data.Record.create([
	{name: "sapId", mapping: "sapId"},
	{name: "sapBpId", mapping: "sapBpId"},
	{name: "name1", mapping: "name1"}
]);

var shiptoStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { TruckRouteAjaxService.fetchSapBpList(skip, count, sort, query, false, true, false, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'sapBpId', totalProperty:'totalSize'}, sapBpRec)
});

// --- Search Area Handler Obj ---
var searchArea = {
	pageSize: 25,
	searchFilters: {},
	searchTruckRoutes: function(filters) {
		this.searchFilters = filters;
		truckRouteSearchStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
	}
}

var dateTimeRenderer = Ext.util.Format.dateRenderer('d/m/Y H:i');

var operationsBar = new Ext.Toolbar([
		{id: 'saveButton', text: texts.buttonSave},
		{xtype: 'tbseparator'},
		{id: 'resetButton', text: texts.buttonReset}
]);

// ------ PAGER ---------
var pagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: searchArea.pageSize,
	store: truckRouteSearchStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

// ------ GRID ----------
var grid;

function createGrid() {
	grid = new Ext.grid.EditorGridPanel({
		id: 'searchGrid',
		renderTo:'divSearchResults',
		width: Ext.get('divSimpleSearch').getWidth(),
		height: 400,
		enableColumnHide: true,
		viewConfig: {autoFill: true, forceFit: true},        
		stripeRows: true,
		title: texts.gridtitle,
		store: truckRouteSearchStore,
		pageSize: searchArea.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		// grid columns
		columns:[
		{
			id: 'startShippingpointName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.shippingpoint,
			dataIndex: 'startShippingpointName',
			sortable: true,
			width: 120
		},{
			id: 'endShiptoBpSapId', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.shiptoId,
			dataIndex: 'endShiptoBpSapId',
			sortable: true,
			width: 80
		},{
			id: 'endShiptoBpName1', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.shiptoName,
			dataIndex: 'endShiptoBpName1',
			sortable: true,
			width: 120
		},{
			id: 'endShiptoBpPostalCode',
			header: texts.shiptoPlz,
			dataIndex: 'endShiptoBpPostalCode',
			sortable: true,
			width: 80
		},{
			id: 'endShiptoBpCity', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.shiptoOrt,
			dataIndex: 'endShiptoBpCity',
			sortable: true,
			width: 80
		},{
			id: 'timeCalculatedMin', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.traveltimegooglemaps,
			dataIndex: 'timeCalculatedMin',
			sortable: true,
			align: 'right'
		},{
			id: 'sapTrspMode', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.transportmode,
			dataIndex: 'sapTrspModeTxt',
			sortable: true,
			align: 'right',
			width: 80
		},{
			id: 'timeCorrectionFactor', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.timecorrectionfactor,
			dataIndex: 'timeCorrectionFactor',
			sortable: true,
			editor: new Ext.form.TextField({allowBlank: true}),
			align: 'right',
			width: 80
		},{
			id: 'timePlanned', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.timeplanned,
			dataIndex: 'timePlanned',
			sortable: true,
			align: 'right'
		},{
			id: 'distanceCalculatedM', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.distancegooglemaps,
			dataIndex: 'distanceCalculatedM',
			sortable: true,
			align: 'right'
		},{
			id: 'distanceCorrectionFactor', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.distancecorrectionfactor,
			dataIndex: 'distanceCorrectionFactor',
			sortable: true,
			editor: new Ext.form.TextField({allowBlank: true}),
			align: 'right',
			width: 80
		},{
			id: 'correctedDistance', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.correcteddistance,
			dataIndex: 'correctedDistance',
			sortable: true,
			align: 'right',
			width: 80
		},{
			id: 'routeComment', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
			header: texts.comment,
			dataIndex: 'routeComment',
			sortable: true,
			editor: new Ext.form.TextField({allowBlank: true}),
			width: 120
		}
		],
	
		// paging bar on the bottom
		bbar: pagingBar,
		tbar: operationsBar
	});
	
	// --- Editor Handler Obj ---
	TruckRouteList = Ext.extend(Ext.ux.grid.EditableList, {

		callServiceForSave : function(changedRecords, callbackForSave) {
			TruckRouteAjaxService.saveTruckRoutes(
				changedRecords, {callback: callbackForSave});
		}, 

		validateRecords: function(pRecords, pMessages) {
			for (var index = 0; index < pRecords.length; index++) {
				var mRecord = pRecords[index];
				var timeCorrectionFactor = mRecord.get('timeCorrectionFactor');

				if(  (timeCorrectionFactor) && (parseFloat(timeCorrectionFactor) != timeCorrectionFactor) ) {
					pMessages.add(
						new Ext.ux.valid.ValidationMessage(
							Ext.ux.valid.ValidationMessage.TYPE_ERROR, 
							null, 
							null, 
							texts.ErrorInvalidTimecorrectionfactor));
				}
			}
		}
	});

	var editor = new TruckRouteList();
	
	editor.init({
		grid:				grid, 
		pageSize:			pagingBar.getPageSize(), 
		saveButtonId:		'saveButton', 
		resetButtonId:		'resetButton', 
		pageChangeTitle:    texts.listPageChangeTitle,
		pageChangeMessage:  texts.listPageChangeMessage,
		maskMessage:        texts.listMaskMessage,
		noChangesMessage:   texts.listNoChangesMessage,
		buttonYes:          texts.listButtonYes,
		buttonNo:           texts.listButtonNo,
		buttonCancel:       texts.listButtonCancel
	});
	
	grid.render();
}

var startShippingpointCombo;
var endShiptoCombo;
function createSearchFilters() {
	
	startShippingpointCombo = new Ext.ux.TwinCombo({
		id: 'startShippingpointCombo',
		applyTo: 'startShippingpointCombo',
		store: shippingpointStore,
		displayField: 'shippingpointName',
		valueField: 'shippingpointId',
		pageSize: 20,
		width: 180,
		listWidth: 250,
		typeAhead: false,
		triggerAction: 'all',
		selectOnFocus: true
	});
	
	endShiptoCombo = new Ext.ux.TwinCombo({
		id: 'endShiptoCombo',
		applyTo: 'endShiptoCombo',
		store: shiptoStore,
		displayField: 'name1',
		valueField: 'sapBpId',
		pageSize: 20,
		width: 180,
		listWidth: 250,
		typeAhead: false,
		triggerAction: 'all',
		selectOnFocus: true
	});
}

Ext.onReady(function() {
	createGrid();
	createSearchFilters();
	searchArea.searchTruckRoutes({});
	Ext.get('buttonSearch').on('click', function() {
		searchArea.searchTruckRoutes({
			startShippingpointId: startShippingpointCombo.getValue(),
			endShiptoBpId: endShiptoCombo.getValue()
		});
	});

	Ext.get('buttonExport').on('click', function() {
		var url = 'TruckRoute.do?export=true';
		if (searchArea.searchFilters.startShippingpointId !== undefined 
			&& searchArea.searchFilters.startShippingpointId !== '') {
			url += '&startShippingpointId=' + searchArea.searchFilters.startShippingpointId;
		}
		if (searchArea.searchFilters.endShiptoBpId !== undefined
			&& searchArea.searchFilters.endShiptoBpId !== '') {
			url += '&endShiptoBpId=' + searchArea.searchFilters.endShiptoBpId;
		}
		if (truckRouteSearchStore.getSortState() !== undefined
			&& truckRouteSearchStore.getSortState() !== '') {
			url += '&sortField=' + truckRouteSearchStore.getSortState().field + '&sortDirection=' + truckRouteSearchStore.getSortState().direction;
		}
		
		window.open(url); 
	});
});
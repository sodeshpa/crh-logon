//COMMON

Ext.onReady( function() {
	createGrid();
	createSearchFilters();
	Ext.get('buttonSearch').on('click', function() {
		refreshGrids();
	} );
	
	railDistanceSearchGrid.getSelectionModel().on('rowselect', function(sm, rowIdx, record) {
		railTimetableSearchArea.searchTimetables( {
			depRailwayStation: record.get('depStationId'),
			arrRailwayStation: record.get('arrStationId')
		});
	});
	
	stationFromStore.load();
});

function refreshGrids() {
	if(stationFromCombo.getValue()) {
		refreshRailTimetables();
		refreshRailDistances();
	}
}

function refreshRailTimetables() {
	railTimetableSearchArea.searchTimetables( {
		depRailwayStation: stationFromCombo.getValue(),
		arrRailwayStation: stationToCombo.getValue()
	});
}

function refreshRailDistances() {
	railDistanceSearchArea.searchDistances( {
		depStationId: stationFromCombo.getValue(),
		arrStationId: stationToCombo.getValue(),
	});
}

function createGrid() {
	createRailDistanceGrid();
	createRailTimetableGrid();
}

function createSearchFilters() {
	stationFromCombo = new Ext.form.ComboBox( {
		store: stationFromStore,
		displayField: 'name',
		valueField: 'value',
		typeAhead: false,
		minChars: 2,
		forceSelection: true,
		triggerAction: 'all',
		emptyText: texts.select,
		selectOnFocus: true,
		applyTo: 'stationFromCombo',
		loadingText: texts.loading
	} );
	
	stationToCombo = new Ext.form.ComboBox( {
		store: stationToStore,
		displayField: 'name',
		valueField: 'value',
		typeAhead: false,
		minChars: 2,
		forceSelection: true,
		triggerAction: 'all',
		emptyText: texts.select,
		selectOnFocus: true,
		applyTo: 'stationToCombo',
		loadingText: texts.loading,
		disabled: true
	} );

	stationFromCombo.on('select', function(record) {
		stationToCombo.clearValue();
		stationToCombo.reset();
		stationToStore.reload({params: {start: 0, limit: stationToCombo.pageSize, query: ""} });
		stationToCombo.setDisabled(false);
	} );
	
}

// RAIL TIMETABLE

var stationRecordType = Ext.data.Record.create( [ {
	name: "name",
	mapping: "name",
	type: "string"
}, {
	name: "value",
	mapping: "value",
	type: "string"
} ]);

var stationFromStore = new Ext.data.Store( {
	proxy: new Ext.data.DWRProxy(RailTimetableAjaxService.fetchRailwayStationList),
	reader: new Ext.data.ObjectReader( {
		id: "value"
	}, stationRecordType),
	remoteSort: true
} );

var stationToStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxy(RailTimetableAjaxService.fetchRailwayStationList),
	reader: new Ext.data.ObjectReader( {
		id: "value"
	}, stationRecordType),
	remoteSort: true
});

var railTimetableSearchRecordType = Ext.data.Record.create([
	{name: "railTimetableId"},
	{name: "depRailwayStation.railwayStationName"},
	{name: "depSwitchingTimeMin"},
	{name: "depTimeMin"},
	{name: "arrRailwayStation.railwayStationName"},
	{name: "arrSwitchingTimeMin"},
	{name: "arrTimeMin"},
	{name: "arrOffsetDays"},
	{name: "timetableComment"}
]);
 
var railTimetableSearchStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { RailTimetableAjaxService.searchTimetables(skip, count, sort, railTimetableSearchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id: 'railTimetableId', totalProperty: 'totalSize'}, railTimetableSearchRecordType), 
	remoteSort: true
});

var railTimetableSearchArea = {
	pageSize: 25,
	searchFilters: {},
	searchTimetables: function(filters) {
		this.searchFilters = filters;
		railTimetableSearchStore.load({params: {start: 0, limit: this.pageSize, sort: '', query: filters} });
	}
};

var railTimetablePagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: railTimetableSearchArea.pageSize,
	store: railTimetableSearchStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

var stationFromCombo;
var stationToCombo;
var materialSearchGrid;
var railTimetableSearchGrid;

function createRailTimetableGrid() {
	railTimetableSearchGrid = new Ext.grid.GridPanel({
		id: 'railTimetableSearchGrid',
		renderTo:'divRailTimetableSearchResults',
		width: Ext.get('searchTable').getWidth(),
		height: 400,
		enableColumnHide: true,
		viewConfig: {autoFill: true, forceFit: true},
		stripeRows: true,
		title: railtimetabletexts.gridtitle,
		store: railTimetableSearchStore,
		pageSize: railTimetableSearchArea.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		// grid columns
		columns:[
		{
			id: 'functions',
			header: railtimetabletexts.functions,
			renderer: function(val, meta, rec) {
				return '<a href="javascript:showPopup(\'RailTimetableDetailEntry.do?action=edit&railTimetableDetails.railTimetableId=' + rec.data.railTimetableId + '\')">' + imgIconEdit() + '</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
				'<a href="javascript:deleteRailTimetable(' + rec.data.railTimetableId + ');">' + imgIconDelete() + '</a>';
		},
		sortable: false,
		width: 50
		},
		{
			id: 'depRailwayStation.railwayStationName',
			header: railtimetabletexts.departure,
			dataIndex: 'depRailwayStation.railwayStationName',
			sortable: true,
			width: 150
		},{
			id: 'depSwitchingTimeMin',
			header: railtimetabletexts.deptimeswitch,
			dataIndex: 'depSwitchingTimeMin',
			sortable: true,
			renderer: timeRenderer,
			width: 130
		},{
			id: 'depTimeMin',
			header: railtimetabletexts.deptime,
			dataIndex: 'depTimeMin',
			sortable: true,
			renderer: timeRenderer,
			width: 100
		},{
			id: 'arrRailwayStation.railwayStationName',
			header: railtimetabletexts.arrival,
			dataIndex: 'arrRailwayStation.railwayStationName',
			sortable: true,
			width: 150
		},{
			id: 'arrSwitchingTimeMin',
			header: railtimetabletexts.arrtime,
			dataIndex: 'arrSwitchingTimeMin',
			sortable: true,
			renderer: timeRenderer,
			width: 130
		},{
			id: 'arrTimeMin',
			header: railtimetabletexts.arrtimeswitch,
			dataIndex: 'arrTimeMin',
			sortable: true,
			renderer: timeRenderer,
			width: 100
		},{
			id: 'arrOffsetDays',
			header: railtimetabletexts.dayoffset,
			dataIndex: 'arrOffsetDays',
			sortable: true,
			width: 80
		},{
			id: 'timetableComment',
			header: railtimetabletexts.comment,
			dataIndex: 'timetableComment',
			sortable: true,
			width: 150
		}
		],
		
		bbar: railTimetablePagingBar
	});
	
	railTimetableSearchGrid.render();
	
}

function timeRenderer(minutes) {
	var hourOfDay = Math.floor(minutes / 60);
	var stringHour = hourOfDay+"";
	if(stringHour.length < 2) {
		stringHour = "0" + stringHour;
	}
	
	
	var minuteOfHour = minutes % 60;
	var stringMinute = minuteOfHour+"";
	if(stringMinute.length < 2) {
		stringMinute = "0" + stringMinute;
	}
	
	return stringHour + ":" + stringMinute;
}

//RAIL DISTANCETABLE

railDistanceSearchRecordType = Ext.data.Record.create([
	{name: "rowNumber"},
	{name: "railDistanceId"},
	{name: "depStationName"},
	{name: "depStationId"},
	{name: "arrStationName"},
	{name: "arrStationId"},
	{name: "tariffDistanceM"},
]);

var railDistanceSearchArea = {
	pageSize: 25,
	searchFilters: {},
	searchDistances: function(filters) {
		this.searchFilters = filters;
		railDistanceSearchStore.load({params: {start: 0, limit: this.pageSize, sort: '', query: filters} });
	}
};

var railDistanceSearchStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { RailTimetableAjaxService.searchDistances(skip, count, sort, railDistanceSearchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id: 'rowNumber', totalProperty: 'totalSize'}, railDistanceSearchRecordType), 
	remoteSort: true
});

var railDistancePagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: railDistanceSearchArea.pageSize,
	store: railDistanceSearchStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

var railDistanceSearchGrid;

var selectionModel = new Ext.grid.RowSelectionModel({singleSelect:true});

function createRailDistanceGrid() {
	railDistanceSearchGrid = new Ext.grid.GridPanel({
		id: 'railDistanceSearchGrid',
		renderTo:'divRailDistanceSearchResults',
		width: Ext.get('searchTable').getWidth(),
		height: 400,
		enableColumnHide: true,
		selModel: selectionModel,
		viewConfig: {autoFill: true, forceFit: true},
		stripeRows: true,
		title: raildistancetabletexts.gridtitle,
		store: railDistanceSearchStore,
		pageSize: railDistanceSearchArea.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		// grid columns
		columns:[
		{
			id: 'functions',
			header: raildistancetabletexts.functions,
			renderer: function(val, meta, rec) {
				if(rec.data.railDistanceId == null) {
					return '<a href="javascript:showPopup(\'RailDistanceDetailEntry.do?action=edit' + "&depStationId=" + rec.data.depStationId 
						+ "&arrStationId=" + rec.data.arrStationId + '\')">' + imgIconEdit();
				} else {
					return '<a href="javascript:showPopup(\'RailDistanceDetailEntry.do?action=edit&railDistanceDetails.railDistanceId=' 
						+ rec.data.railDistanceId + "&depStationId=" + rec.data.depStationId 
						+ "&arrStationId=" + rec.data.arrStationId + '\')">' + imgIconEdit();
				}
		},
		sortable: false,
		width: 50
		},
		{
			id: 'depStationName',
			header: raildistancetabletexts.stationNameFrom,
			dataIndex: 'depStationName',
			sortable: true,
			width: 300
		},{
			id: 'arrStationName',
			header: raildistancetabletexts.stationNameTo,
			dataIndex: 'arrStationName',
			sortable: true,
			width: 300
		},{
			id: 'tariffDistanceM',
			header: raildistancetabletexts.railDistance,
			dataIndex: 'tariffDistanceM',
			sortable: true,
			width: 225
		}
		],
		
		bbar: railDistancePagingBar
	});
	
	railDistanceSearchGrid.render();	
}
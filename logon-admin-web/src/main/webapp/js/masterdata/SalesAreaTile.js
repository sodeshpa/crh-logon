var salesAreaSearchRecordType = Ext.data.Record.create([
	{name: "sareaId"},	
	{name: "compositeId"},
	{name: "sorgAddressName"},
	{name: "sorgCd"},
	{name: "distributionChannel"},
	{name: "divisionCd"},
	{name: "sareaDisplayLongname"},
	{name: "delnoteUsesWsNumberFlag"},
	{name: "shortTermOrderThresholdMi"},
	{name: "allowBimodalCreationTillMi"},
	{name: "shiptoOpen1From"},
	{name: "shiptoOpen1To"},
	{name: "shiptoOpen2From"},
	{name: "shiptoOpen2To"},
	{name: "mailFrom"},
	{name: "mailTestTo"},
	{name: "mailBcc"},
	{name: "mailRailadmin"},
	{name: "mailEncoding"}
]);

var salesAreaSearchStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { SalesAreaAjaxService.searchSaleasAreas(skip, count, sort, searchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id: 'compositeId', totalProperty: 'totalSize'}, salesAreaSearchRecordType), 
	remoteSort: true
});

var searchArea = {
	pageSize: 20,
	searchFilters: {},
	searchSaleasAreas: function(filters) {
		this.searchFilters = filters;
		salesAreaSearchStore.load({params: {start: 0, limit: this.pageSize, sort: '', query: filters} });
	}
}

var pagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: searchArea.pageSize,
	store: salesAreaSearchStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

var minutesAfterMidnightRenderer = function(val, meta, rec) {
	if (val == null) {
		return null;
	}
	else {
		var mod = val % (24 * 60);
		var hours = Math.floor(mod / 60);
		var minutes = mod % 60;
		if (hours < 10) {
			hours = '0' + hours;
		}
		if (minutes < 10) {
			minutes = '0' + minutes;
		}
		return hours + ':' + minutes;
	}
}

function createGrid() {
	salesAreaResultsGrid = new Ext.grid.GridPanel({
		id: 'salesAreaGrid',
		renderTo:'divSalesAreaResults',
		height: 550,
		enableColumnHide: true,
		viewConfig: {autoFill: true, forceFit: true},
		stripeRows: true,
		title: texts.gridtitle,
		store: salesAreaSearchStore,
		pageSize: searchArea.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		// grid columns
		columns:[
		{
			id: 'functions',
			header: texts.functions,
			renderer: function(val, meta, rec) {
				return '<a href="javascript:showPopup(\'SalesAreaDetailEntry.do?action=edit&salesAreaDetails.compositeId=' + rec.data.compositeId + '\')">' + imgIconEdit() + '</a>';
			},
			sortable: false,
			width: 35
		},{
			id: 'sareaId',
			header: texts.sareaId,
			dataIndex: 'sareaId',
			sortable: true, 
			width: 60
		},{
			id: 'sorgCd',
			header: texts.sorgCd,
			dataIndex: 'sorgCd',
			sortable: true,
			width: 60
		},{
			id: 'distributionChannel',
			header: texts.distributionChannel,
			dataIndex: 'distributionChannel',
			sortable: true,
			width: 60
		},{
			id: 'division',
			header: texts.division,
			dataIndex: 'divisionCd',
			sortable: true,
			width: 40
		},{
			id: 'sareaDisplayLongname',
			header: texts.sareaDisplayLongname,
			dataIndex: 'sareaDisplayLongname',
			sortable: true,
			width: 80
		},{
			id: 'delnoteUsesWsNumberFlag',
			header: texts.delnoteUsesWsNumberFlag,
			dataIndex: 'delnoteUsesWsNumberFlag',
			sortable: true,
			width: 80
		},{
			id: 'shortTermOrderThresholdMi',
			header: texts.shortTermOrderThresholdMi,
			dataIndex: 'shortTermOrderThresholdMi',
			sortable: true,
			width: 80,
			hidden: true
		},{
			id: 'allowBimodalCreationTillMi',
			header: texts.allowBimodalCreationTillMi,
			dataIndex: 'allowBimodalCreationTillMi',
			sortable: true,
			width: 80,
			hidden: true,
			renderer: minutesAfterMidnightRenderer
		},{
			id: 'shiptoOpen1From',
			header: texts.shiptoOpen1From,
			dataIndex: 'shiptoOpen1From',
			sortable: true,
			width: 80,
			hidden: true,
			renderer: minutesAfterMidnightRenderer
		},{
			id: 'shiptoOpen1To',
			header: texts.shiptoOpen1To,
			dataIndex: 'shiptoOpen1To',
			sortable: true,
			width: 80,
			hidden: true,
			renderer: minutesAfterMidnightRenderer
		},{
			id: 'shiptoOpen2From',
			header: texts.shiptoOpen2From,
			dataIndex: 'shiptoOpen2From',
			sortable: true,
			width: 80,
			hidden: true,
			renderer: minutesAfterMidnightRenderer
		},{
			id: 'shiptoOpen2To',
			header: texts.shiptoOpen2To,
			dataIndex: 'shiptoOpen2To',
			sortable: true,
			width: 80,
			hidden: true,
			renderer: minutesAfterMidnightRenderer
		},{
			id: 'mailFrom',
			header: texts.mailFrom,
			dataIndex: 'mailFrom',
			sortable: true,
			width: 80,
			hidden: true
		},{
			id: 'mailTestTo',
			header: texts.mailTestTo,
			dataIndex: 'mailTestTo',
			sortable: true,
			width: 80,
			hidden: true
		},{
			id: 'mailBcc',
			header: texts.mailBcc,
			dataIndex: 'mailBcc',
			sortable: true,
			width: 80,
			hidden: true
		},{
			id: 'mailRailadmin',
			header: texts.mailRailadmin,
			dataIndex: 'mailRailadmin',
			sortable: true,
			width: 80,
			hidden: true
		},{
			id: 'mailEncoding',
			header: texts.mailEncoding,
			dataIndex: 'mailEncoding',
			sortable: true,
			width: 40,
			hidden: true
		}
		],

		bbar: pagingBar
	});
	
	salesAreaResultsGrid.render();
}

var sorgRecordType = Ext.data.Record.create( [ {
	name: "value",
	mapping: "sorgCd",
	type: "string"
} ]);

var sorgStore = new Ext.data.Store( {	
	proxy: new Ext.data.DWRProxy(SalesAreaAjaxService.fetchSalesOrganisations),
	reader: new Ext.data.ObjectReader( {
		id: "sorgCd"
	}, sorgRecordType),
	remoteSort: true
} );

var sorgCombo;

function createSearchFilters() {
	sorgCombo = new Ext.form.ComboBox( {
		store: sorgStore,
		displayField: 'value',
		valueField: 'value',
		typeAhead: true,
		minChars: 2,
		mode: 'local',
		forceSelection: true,
		triggerAction: 'all',
		emptyText: texts.select,
		selectOnFocus: true,
		applyTo: 'sorgCombo',
		loadingText: texts.loading
	} );

	sorgCombo.on('select', function(record) {
		searchArea.searchSaleasAreas({sorgCd: sorgCombo.getValue()});
	} );
}
		

Ext.onReady( function() {
	createGrid(); 
	
	Ext.get('buttonSearch').on('click', function() {
		searchArea.searchSaleasAreas({sorgCd: sorgCombo.getValue()});
	} );
	sorgStore.load({});
	createSearchFilters();
	searchArea.searchSaleasAreas({});
} );

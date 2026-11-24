//COMMON

Ext.onReady( function() {
	createGrid();
	createSearchFilters();
	Ext.get('buttonSearch').on('click', function() {
		refreshGrids();
	} );
	
	Ext.get('buttonFilter').on('click', function() {
		refreshGrids();
	} );
	
	plantStore.load();
});

function refreshGrids() {
	refreshMatPlants();
	refreshMatPlantNorms();
}

function refreshMatPlants() {
	matPlantSearchArea.searchMatPlants( {
		plantCd: plantsCombo.getValue(),
		sapMaterialId: materialsCombo.getValue()
	});
}

function refreshMatPlantNorms() {
	matPlantNormSearchArea.searchMatPlantNorms( {
		plantCd: plantsCombo.getValue(),
		sapMaterialId: materialsCombo.getValue(),
		sapMaterialNormCd: Ext.getDom('norm').value,
		sapLanguageCd: Ext.getDom('lang').value,
		sorgCd: Ext.getDom('sorg').value
	});
}

function createGrid() {
	createMaterialEnhancementsGrid();
	createSapMaterielPlantNormsGrid();
}

function createSearchFilters() {
	plantsCombo = new Ext.form.ComboBox( {
		store: plantStore,
		displayField: 'name',
		valueField: 'value',
		typeAhead: false,
		minChars: 2,
		forceSelection: true,
		triggerAction: 'all',
		emptyText: texts.select,
		selectOnFocus: true,
		applyTo: 'plantsCombo',
		loadingText: texts.loading
	} );
	
	materialsCombo = new Ext.ux.TwinCombo( {
		id: 'materialsCombo',
		applyTo: 'materialsCombo',
		store: materialStore,
		displayField: 'display',
		valueField: 'value',
		pageSize: 20,
		width: 300,
		listWidth: 300,
		typeAhead: false,
		forceSelection: true,
		triggerAction: 'all',
		minChars: 2,
		selectOnFocus: true,
		disabled: true
	} );

	plantsCombo.on('select', function(record) {
		materialsCombo.clearValue();
		materialsCombo.reset();
		materialStore.reload({params: {start: 0, limit: materialsCombo.pageSize, query: ""} });
		materialsCombo.setDisabled(false);
	} );
	
}

// MAT PLANT

var plantRecordType = Ext.data.Record.create( [ {
	name: "name",
	mapping: "name",
	type: "string"
}, {
	name: "value",
	mapping: "value",
	type: "string"
} ]);

var plantStore = new Ext.data.Store( {
	proxy: new Ext.data.DWRProxy(MaterialEnhancementsAjaxService.fetchPlantsList),
	reader: new Ext.data.ObjectReader( {
		id: "value"
	}, plantRecordType),
	remoteSort: true
} );

var materialRecordType = Ext.data.Record.create( [ {
	name: "display",
	mapping: "display",
	type: "string"
}, {
	name: "value",
	mapping: "sapMaterialId",
	type: "string"
} ]);

var materialStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { MaterialEnhancementsAjaxService.fetchMaterialsList(skip, count, sort, [ plantsCombo.getValue(), query ], callback) }, true),
	reader: new Ext.data.ListRangeReader({id: 'sapMaterialId', totalProperty: 'totalSize'}, materialRecordType)
});

var matPlantSearchRecordType = Ext.data.Record.create([
	{name: "matPlantId"},
	{name: "plantName"},
	{name: "plantCd"},
	{name: "materialDesc"},
	{name: "langCd"},
	{name: "sapMaterialId"},
	{name: "sapMaterialNo"},
	{name: "brandSalesName"},
	{name: "normDesc"},
	{name: "cpdNumber"},
	{name: "logo1Z1"},
	{name: "logo1Z2"},
	{name: "nationalNorm"},
	{name: "autoaddMaterialDesc"},
	{name: "autoaddNUom"},
	{name: "autoadd1PerNOfUom"},
	{name: "updateBlockedByHjid"}
]);
 
var matPlantSearchStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { MaterialEnhancementsAjaxService.searchMatPlants(skip, count, sort, matPlantSearchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id: 'matPlantId', totalProperty: 'totalSize'}, matPlantSearchRecordType), 
	remoteSort: true
});

var matPlantSearchArea = {
	pageSize: 25,
	searchFilters: {},
	searchMatPlants: function(filters) {
		this.searchFilters = filters;
		matPlantSearchStore.load({params: {start: 0, limit: this.pageSize, sort: '', query: filters} });
	}
};

var matEnchPagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: matPlantSearchArea.pageSize,
	store: matPlantSearchStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

var plantsCombo;
var materialsCombo;
var materialSearchGrid;
var matPlantSearchGrid;

function createMaterialEnhancementsGrid() {
	matPlantSearchGrid = new Ext.grid.GridPanel({
		id: 'matPlantSearchGrid',
		renderTo:'divMaterialEnhancementsSearchResults',
		width: Ext.get('searchTable').getWidth(),
		height: 400,
		enableColumnHide: true,
		viewConfig: {autoFill: true, forceFit: true},
		stripeRows: true,
		title: matplanttexts.gridtitle,
		store: matPlantSearchStore,
		pageSize: matPlantSearchArea.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		// grid columns
		columns:[
		{
			id: 'functions',
			header: matplanttexts.functions,
			renderer: function(val, meta, rec) {
			return '<a href="javascript:showPopup(\'MaterialEnhancementsDetailEntry.do?action=edit&materialEnhancementsDetails.matPlantId=' + rec.data.matPlantId + '\')">' + imgIconEdit() + '</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
			'<a href="javascript:deleteMatPlant(' + rec.data.matPlantId + ');">' + imgIconDelete() + '</a>';
		},
		sortable: false,
		width: 40
		},{
			id: 'error',
			header: matplanttexts.error,
			renderer: errorRenderer,
			sortable: false,
			width: 20
		},{
			id: 'plantName',
			header: matplanttexts.plantName,
			dataIndex: 'plantName',
			sortable: true,
			width: 100
		},{
			id: 'materialDesc',
			header: matplanttexts.materialDesc,
			dataIndex: 'materialDesc',
			sortable: true,
			width: 100
		},{
			id: 'sapMaterialNo',
			header: matplanttexts.sapMaterialNo,
			dataIndex: 'sapMaterialNo',
			sortable: true,
			width: 80
		},{
			id: 'brandName',
			header: matplanttexts.brandName,
			dataIndex: 'brandSalesName',
			sortable: true,
			width: 80
		},{
			id: 'productLineText1',
			header: matplanttexts.productLineText1,
			dataIndex: 'normDesc',
			sortable: true,
			width: 100
		},{
			id: 'productLineText2',
			header: matplanttexts.productLineText2,
			dataIndex: 'cpdNumber',
			sortable: true,
			width: 100
		},{
			id: 'logo1Z1',
			header: matplanttexts.logo1z1,
			dataIndex: 'logo1Z1',
			sortable: true,
			width: 80
		},{
			id: 'logo1Z2',
			header: matplanttexts.logo1z2,
			dataIndex: 'logo1Z2',
			sortable: true,
			width: 80
		},{
			id: 'nationalNorm',
			header: matplanttexts.nationalNorm,
			dataIndex: 'nationalNorm',
			sortable: true,
			width: 80
		},{
			id: 'autoaddMaterialDesc',
			header: matplanttexts.autoaddMaterialDesc,
			dataIndex: 'autoaddMaterialDesc',
			sortable: true,
			width: 80
		}
		],
		
		bbar: matEnchPagingBar
	});
	
	matPlantSearchGrid.render();
	
}

//MAT PLANT NORMS

var langRecordType = Ext.data.Record.create( 
	[{name: "langCd"}, {name: "txt"}]
);

var langStore = new Ext.data.Store( {
	proxy: new Ext.data.DWRProxy(MaterialEnhancementsAjaxService.fetchLangList),
	reader: new Ext.data.ObjectReader( {
		id: "langCd"
	}, plantRecordType),
	remoteSort: true
});

matPlantNormSearchRecordType = Ext.data.Record.create([
	{name: "plantCd"},
	{name: "txt"},
	{name: "sapMaterialNo"},
	{name: "sapMaterialNormCd"},
	{name: "sapLanguageCd"},
	{name: "destCountry"},
	{name: "sorgCd"},
	{name: "showLogoFlag"},
	{name: "zcode1"},
	{name: "zcode2"},
	{name: "text1"},
	{name: "text2"},
	{name: "text3"},
	{name: "markedForDeletion"}
]);

var matPlantNormSearchArea = {
	pageSize: 25,
	searchFilters: {},
	searchMatPlantNorms: function(filters) {
		this.searchFilters = filters;
		matPlantNormsSearchStore.load({params: {start: 0, limit: this.pageSize, sort: '', query: filters} });
	}
};

var matPlantNormsSearchStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { MaterialEnhancementsAjaxService.searchMatPlantNorms(skip, count, sort, matPlantNormSearchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id: 'sapMatPlantNormId', totalProperty: 'totalSize'}, matPlantNormSearchRecordType), 
	remoteSort: true
});

var matPlantNormPagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: matPlantNormSearchArea.pageSize,
	store: matPlantNormsSearchStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

var matPlantNormSearchGrid;

function createSapMaterielPlantNormsGrid() {
	matPlantNormSearchGrid = new Ext.grid.GridPanel({
		id: 'sapMatPlantNormSearchGrid',
		renderTo:'divSapMaterialPlantNormsSearchResults',
		width: Ext.get('searchTable').getWidth(),
		height: 400,
		enableColumnHide: true,
		viewConfig: {autoFill: true, forceFit: true},
		stripeRows: true,
		title: matnormtexts.gridtitle,
		store: matPlantNormsSearchStore,
		pageSize: matPlantNormSearchArea.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		// grid columns
		columns:[
		{
			id: 'plant',
			header: matnormtexts.plant,
			dataIndex: 'plantCd',
			sortable: true,
			width: 30
		},{
			id: 'material',
			header: matnormtexts.material,
			dataIndex: 'txt',
			sortable: true
		},{
			id: 'materialno',
			header: matnormtexts.materialno,
			dataIndex: 'sapMaterialNo',
			sortable: true,
			width: 40
		},{
			id: 'norm',
			header: matnormtexts.norm,
			dataIndex: 'sapMaterialNormCd',
			sortable: true,
			width: 40
		},{
			id: 'language',
			header: matnormtexts.language,
			dataIndex: 'sapLanguageCd',
			sortable: true,
			width: 50
		},{
			id: 'destCountry',
			header: matnormtexts.destCountry,
			dataIndex: 'destCountry',
			sortable: true,
			width: 60
		},{
			id: 'sorg',
			header: matnormtexts.sorg,
			dataIndex: 'sorgCd',
			sortable: true,
			width: 60
		},{
			id: 'logo',
			header: matnormtexts.logo,
			dataIndex: 'showLogoFlag',
			sortable: true,
			width: 30
		},{
			id: 'zcode1',
			header: matnormtexts.zcode1,
			dataIndex: 'zcode1',
			sortable: true,
			width: 30
		},{
			id: 'zcode2',
			header: matnormtexts.zcode2,
			dataIndex: 'zcode2',
			sortable: true,
			width: 30
		},{
			id: 'text1',
			header: matnormtexts.text1,
			dataIndex: 'text1',
			sortable: true
		},{
			id: 'text2',
			header: matnormtexts.text2,
			dataIndex: 'text2',
			sortable: true
		},{
			id: 'text3',
			header: matnormtexts.text3,
			dataIndex: 'text3',
			sortable: true
		},{
			id: 'deleted',
			header: matnormtexts.deleted,
			dataIndex: 'markedForDeletion',
			sortable: true,
			width: 30
		}
		],
		
		bbar: matPlantNormPagingBar
	});
	
	matPlantNormSearchGrid.render();	
}
//Javascript belonging to Order monitoring/Monitoring/Bimodal monitoring

Ext.override(Ext.data.Store, {
    loadData : function(o, append){
        var r = this.reader.readRecords(Ext.isEmpty(this.emptyItem) ? o : [this.emptyItem].concat(o));
        this.loadRecords(r, {add: append}, true);
    }
})

function initBimodalRailcarMonitoringFilters() {

	companyCodeSelector = new Ext.form.ComboBox({
		mode: 'local',
		editable: true,
		forceSelection: false,
		id: 'selectedCompanyCode',
		name: 'selectedCompanyCode',
		transform:'selectedCompanyCode',
		allowBlank: true,
		triggerAction: 'all',
		store: new Ext.data.JsonStore({
			fields: ['companycodeCd', 'companycodeName'],
			data : companyCodeListJsOn,
            emptyItem: {'companycodeCd': '', 'companycodeName': '...'}			
		}),
		width: 130,
		listWidth: 200,
		valueField: 'companycodeCd',
		emptyText: texts.select,
		displayField: 'companycodeName'
	});

	companyCodeSelector.on('select', function(combo, record) {
		a={};
		a[actions.ajaxSetCompanyCode]='';
		addFormElements(a, "order_BimodalRailcarMonitoringForm"); //add form data
		Ext.Ajax.request({
			url: 'BimodalRailcarMonitoring.do', 
			params: a,
			success: function(response){
			/*jslint evil: true */
			eval(response.responseText);
			productionPlantCombo.forceReloadStore();
			sapMatCombo.forceReloadStore();
		}
		});	
	});

	var productionPlantComboRec = Ext.data.Record.create([
														  {name: "plantCd"},
														  {name: "plantName"}
														  ]);

	var productionPlantComboStore = new Ext.data.Store({
		proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback){

			var queryParams = [];

			queryParams.push(companyCodeSelector.getValue());
			queryParams.push(query);

			BimodalRailcarMonitoringAjaxService.fetchPlantsList(skip, count, sort, queryParams, callback)

		}),
		reader: new Ext.data.ListRangeReader({id:'plantCd', totalProperty:'totalSize'}, productionPlantComboRec),
		remoteSort:true
	});

	var sapMatComboRec = Ext.data.Record.create([
												 {name: "sapMaterialId"},
												 {name: "txt"}
												 ]);

	var sapMatComboStore = new Ext.data.Store({
		proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) {
			var queryParams = [];

			queryParams.push(companyCodeSelector.getValue());
			queryParams.push(productionPlantCombo.getValue());
			queryParams.push(query);

			BimodalRailcarMonitoringAjaxService.fetchMaterialsList(skip, count, sort, queryParams, callback); 
		}),
		reader: new Ext.data.ListRangeReader({id:'sapMaterialId', totalProperty:'totalSize'}, sapMatComboRec),
		remoteSort:true
	});

	productionPlantCombo = new Ext.ux.TwinCombo( {
		id:'selectedProductionPlantCd',
		store: productionPlantComboStore,
		displayField: 'plantName',
		valueField: 'plantCd',
		typeAhead: true,
		minChars: 2,
		forceSelection: true,
		triggerAction: 'all',
		emptyText: texts.select,
		pageSize:20,
		selectOnFocus: true,
		transform: 'selectedProductionPlantCd',
		loadingText: texts.loading,
		forceReloadStore: function(){
			this.reset();
			this.store.removeAll();
			//force the reload on trigger
			this.lastQuery = null;
		}
	} );

	productionPlantCombo.on('select',function(combo, record) {
		sapMatCombo.forceReloadStore();
	});

	sapMatCombo = new Ext.ux.TwinCombo( {
		id:'selectedSapMatId',
		store: sapMatComboStore,
		displayField: 'txt',
		valueField: 'sapMaterialId',
		typeAhead: true,
		forceSelection: true,
		pageSize:20,
		minChars: 2,
		triggerAction: 'all',
		emptyText: texts.select,
		selectOnFocus: true,
		transform: 'selectedSapMatId',
		loadingText: texts.loading,
		forceReloadStore: function(){
		this.reset();
		this.store.removeAll();
		// force the reload on trigger
		this.lastQuery = null;
	}
	} );

	shiptoList = new Ext.form.TriggerField({
		readOnly:true
	});


	shiptoList.listPanel = Ext.get('DivShiptosEdit');
	shiptoList.onTriggerClick = function(){
		this.listPanel.load({
			url: 'BimodalRailcarMonitoring.do',
			params: actions.ajaxEditShiptos,
			text: texts.loading,
			scripts:true,
			callback: function(el){
				el.show();
			}	
		});
		Ext.getDoc().on('mousewheel', this.collapseIf, this);
		Ext.getDoc().on('mousedown', this.collapseIf, this); 
	}
	shiptoList.collapse = function(){
		this.listPanel.hide();
		this.listPanel.dom.innerHTML = "";
		Ext.getDoc().un('mousewheel', this.collapseIf, this);
		Ext.getDoc().un('mousedown', this.collapseIf, this); 
	}
	shiptoList.collapseIf = function(e){
		if(!e.within(this.listPanel)){
			this.collapse();
		}
	}
	shiptoList.applyToMarkup('ShiptosField');
}

function setSelectedShiptoCheckboxes(){
	a={};
	a[actions.ajaxSetShiptos]='';
	addFormElements(a, "order_BimodalRailcarMonitoringForm"); //add form data
	Ext.Ajax.request({
		url: 'BimodalRailcarMonitoring.do',
		params: a,
		success: function(response){
		/*jslint evil: true */
		eval(response.responseText);
		productionPlantCombo.forceReloadStore();
		sapMatCombo.forceReloadStore();
	}
	});

}

function setAllCheckboxes(value, container){
	list = Ext.query("input[type='checkbox']", Ext.getDom(container));
	for (i = 0; i < list.length; i++){
		list[i].checked = value;
	}
}

function search(){
	var diva = Ext.get('bimodalRailcarMonitoringResults');
	a={};
	a[actions.search]='';
	addFormElements(a, "order_BimodalRailcarMonitoringForm"); //add form data
	diva.load({
		url: 'BimodalRailcarMonitoring.do', 
		params: a,
		text: texts.exjsloading,
		scripts: true
	});
}

function setOrderBy(colname) {
	if (document.getElementById("sortByColumn").value == colname){
		var bool = document.getElementById("sortAsc").value;
		if (bool == 'true'){
			document.getElementById("sortAsc").value = false;
		}
		else{
			document.getElementById("sortAsc").value = true;
		}
	}
	else{
		document.getElementById("sortByColumn").value = colname;
	}
	search();
}

function onOrderDetailClose(){
	search();
}	

function logonPopupHideCallback() {
	search();
}
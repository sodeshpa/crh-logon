//Javascript belonging to Order monitoring/Monitoring/Container monitoring

Ext.override(Ext.data.Store, {
    loadData : function(o, append){
        var r = this.reader.readRecords(Ext.isEmpty(this.emptyItem) ? o : [this.emptyItem].concat(o));
        this.loadRecords(r, {add: append}, true);
    }
})

function initContainerMonitoringFilters() {
	
	var productionPlantComboRec = Ext.data.Record.create([
														  {name: "plantCd"},
														  {name: "plantName"}
														  ]);

	var productionPlantComboStore = new Ext.data.Store({
		proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback){

			var queryParams = [];

			queryParams.push(query);

			ContainerMonitoringAjaxService.fetchPlantsList(skip, count, sort, queryParams, callback)

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

			queryParams.push(productionPlantCombo.getValue());
			queryParams.push(query);

			ContainerMonitoringAjaxService.fetchMaterialsList(skip, count, sort, queryParams, callback); 
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

	shippingPointList = new Ext.form.TriggerField({
		readOnly:true
	});


	shippingPointList.listPanel = Ext.get('DivShippingPointsEdit');
	shippingPointList.onTriggerClick = function(){
		var parameters={};
		parameters[action] = actions.ajaxEditShippingPoints;
		this.listPanel.load({ 
			url: 'ContainerMonitoring.do',
			params: parameters,
			text: texts.loading,
			scripts:true,
			callback: function(el){
				el.show();
			}	 
		});
		Ext.getDoc().on('mousewheel', this.collapseIf, this);
		Ext.getDoc().on('mousedown', this.collapseIf, this); 
	}
	shippingPointList.collapse = function(){
		this.listPanel.hide();
		this.listPanel.dom.innerHTML = "";
		Ext.getDoc().un('mousewheel', this.collapseIf, this);
		Ext.getDoc().un('mousedown', this.collapseIf, this); 
	}
	shippingPointList.collapseIf = function(e){
		if(!e.within(this.listPanel)){
			this.collapse();
		}
	}
	
	shippingPointList.applyToMarkup('ShippingPointsField');
}

function setSelectedShippingPointCheckboxes(){
	parameters={};
	parameters[action]=actions.ajaxSetShippingPoints;
	addFormElements(parameters, "order_ContainerMonitoringForm"); //add form data
	Ext.Ajax.request({
		url: 'ContainerMonitoring.do',
		params: parameters,
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

function search(actionStr){
	var diva = Ext.get('containerMonitoringResults');
	parameters={};
	parameters[action]=actionStr;
	addFormElements(parameters, "order_ContainerMonitoringForm"); //add form data
	diva.load({
		url: 'ContainerMonitoring.do', 
		params: parameters,
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
	search(actions.search);
}

function onOrderDetailClose(){
	search();
}	

function logonPopupHideCallback() {
	search();
}
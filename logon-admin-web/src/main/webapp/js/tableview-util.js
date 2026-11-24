Ext.onReady(function(){
	Ext.QuickTips.init();

	//do not show the dropdown in reference editor dropdown
	if(document.getElementById('tableSelectionDropdown') != null){
	
		var tableRecordType = Ext.data.Record.create([
			{name: "name", type: "string"}
		]);
	
		var form = window.document.forms.admin_TableViewForm;
		
		var tableStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxy(TableBrowserAjaxService.tableList, true),
			reader: new Ext.data.ObjectReader({id: "name"}, tableRecordType)
		});
	
		var combo = new Ext.form.ComboBox({
			store: tableStore,
			displayField:'name',
			typeAhead: true,
			minChars: 2,
			mode: 'remote',
			forceSelection: true,
			triggerAction: 'all',
			emptyText: setEmptyText(),
			selectOnFocus:true,
			applyTo: 'tableSelectionDropdown',
			onSelect: function(record){
				form.selectedTable.value=record.data.name;
				form.submit();
			}
		});
	}
});
Ext.namespace('Ext.ux.grid');

Ext.ux.grid.CheckColumn = function(config){

	Ext.apply(this, config);

	if (!this.id) {
		this.id = Ext.id();
	}

	this.addEvents("mousedown");

	this.renderer = this.renderer.createDelegate(this);

	Ext.ux.grid.CheckColumn.superclass.constructor.call(this);
};

Ext.extend(Ext.ux.grid.CheckColumn, Ext.util.Observable, {

	init:	function(grid) {
		this.grid = grid;
			this.grid.on(
				'render', 
				function() {
					var view = this.grid.getView();
					var selModel = this.grid.getSelectionModel();

					view.mainBody.on('mousedown', this.onMouseDown, this);
					selModel.on('keypressed-on-cc', this.onKeyDown, this);
				}, 
				this);
	},

	onKeyDown:	function(rowIndex, colIndex, id) {
		if (this.id === id) {
			var record = this.grid.store.getAt(rowIndex);
			if (this.grid.getColumnModel().isCellEditable(colIndex, rowIndex)) {
				var event = { grid: this.grid, record: record, field: this.dataIndex, value: record.data[this.dataIndex], row: rowIndex, column: colIndex };
				this.grid.fireEvent("beforeedit", event);
				if (!event.cancel) {
					record.set(this.dataIndex, !record.data[this.dataIndex]);
					this.grid.fireEvent("afteredit", { field: this.dataIndex, record: record });
					event.originalValue = event.value;
					event.value = record.data[this.dataIndex];
					this.grid.fireEvent("afteredit", event);
				}
			}
		}
	},

	onMouseDown:	function(event, target) {
		if (target.className && (target.className.indexOf('x-grid3-cc-'+this.id) != -1)) {
			event.stopEvent();

			var view = this.grid.getView();
			var selModel = this.grid.getSelectionModel();
			var rowIndex = view.findRowIndex(target);
			var colIndex = view.findCellIndex(target);
			var record = this.grid.store.getAt(rowIndex);
			
			if (this.grid.getColumnModel().isCellEditable(colIndex, rowIndex)) {
				var event = { grid: this.grid, record: record, field: this.dataIndex, value: record.data[this.dataIndex], row: rowIndex, column: colIndex };
				this.grid.fireEvent("beforeedit", event);
				if (!event.cancel) {
					record.set(this.dataIndex, !record.data[this.dataIndex]);
					event.originalValue = event.value;
					event.value = record.data[this.dataIndex];
					this.grid.fireEvent("afteredit", event);
				}
			}
			
			selModel.select(rowIndex, colIndex);
			
			this.grid.stopEditing();
		}
	},

	renderer:	function(v, p, record) {
		p.css += ' x-grid3-check-col-td'; 
		return '<div id="x-grid3-cc-'+this.id+'" class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
	}

});

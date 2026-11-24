/*
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
*/
Ext.namespace('Ext.ux.grid');

/*
 * Additional events support for Ext.grid.EditorGridPanel. (Feel free to extend it with other features you
 * need and might be useful generally in the application.)
 */
Ext.ux.grid.LogonEditorGridPanel = Ext.extend(Ext.grid.EditorGridPanel, {

	initComponent: function() {
		Ext.ux.grid.LogonEditorGridPanel.superclass.initComponent.call(this);
		this.addEvents("afterEditorFieldRendered");
	},
	
	startEditing: function(row, col) {
		Ext.ux.grid.LogonEditorGridPanel.superclass.startEditing.call(this, row, col);
		if (this.editing) {
			(function() {
				var r = this.store.getAt(row);
				var field = this.colModel.getDataIndex(col);
				var e = {
					grid: this,
					record: r,
					field: field,
					value: r.data[field],
					row: row,
					column: col
				};
				this.fireEvent("afterEditorFieldRendered", e);
			}).defer(51, this);
		}
	}

});

Ext.reg('logoneditorgridpanel', Ext.ux.grid.LogonEditorGridPanel);

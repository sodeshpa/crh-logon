Ext.namespace('Ext.ux');

/**
 * Table-style CheckComboBox.
 * 
 * FIXME 18-Oct-2012/romek: Problems with the current implementation:
 * 
 *     - If any of the lines wrap, we have to set the heights manually or the layout breaks.
 *     - Some of the style settings below should match Ext.form.CheckBox, and should not be
 *       hard-coded!
 */
Ext.ux.TableStyleCheckComboBox = Ext.extend(Ext.ux.CheckComboBox, {
	
	additionalColumns: [], // Like displayField, as array.
	checkboxColumnTitle: null,
	additionalColumnTitles: [],
	checkboxColumnWidth: null,
	additionalColumnWidths: [],
	
	initPanel: function(checkboxes, bulkButtons) {
		var cells = this.generateCells(checkboxes);
		
		this.panel = new Ext.Panel({
			renderTo: this.innerList,
			layout: 'table',
			layoutConfig: {columns: this.additionalColumns.length + 1},
			border: false,
			items: cells,
			buttons: bulkButtons
		});
		
		var panelEl = this.panel.getEl();
		
		// Set the style of additional fields similar to the one of the checkboxes. 
		panelEl.select('.additionalColumnBody .x-panel-body').each(function(el) {
			el.setStyle({
				'line-height': '18px',
				'padding-top': '3px',
				'position': 'static' // Fix for invisible cells under IE8.
			});
		}, this);
		
		panelEl.select('.mainColumnBody .x-panel-body').each(function(el) {
			el.setStyle({
				'position': 'static' // Fix for invisible cells under IE8.
			});
		}, this);
		
		// Set equal height everywhere.
		panelEl.select('tr').each(function(tr) {
			var maxHeight = 0;
			var cells = tr.select('.mainColumnBody .x-form-check-wrap, ' +
					'.additionalColumnBody .x-panel-body, .mainColumnHeader .x-panel-header, ' +
					'.additionalColumnHeader .x-panel-header');
			
			cells.each(function(cell) {
				var height = cell.getHeight();
				
				if (height > maxHeight) {
					maxHeight = height;
				}
			});
			
			cells.each(function(cell) {
				cell.setHeight(maxHeight);
			});
		});
		
	},
	
	// private
	generateCells: function(checkboxes) {
		var cells = [];
		var lineCount = 0;
		
		// Column titles
		cells.push({
			title: this.checkboxColumnTitle,
			border: false,
			frame: false,
			width: this.checkboxColumnWidth,
			height: 0,
			cls: 'mainColumn mainColumnHeader',
			html: ''
		});
		
		for (var i = 0; i < this.additionalColumns.length; i++) {
			cells.push({
				title: this.additionalColumnTitles[i],
				border: false,
				frame: false,
				width: this.additionalColumnWidths[i],
				height: 0,
				cls: 'additionalColumn additionalColumnHeader',
				html: ''
			});
		}
		
		// Data cells
		this.store.each(function(record) {
			var checkbox = checkboxes[lineCount];
			
			cells.push({
				border: false,
				frame: false,
				width: this.checkboxColumnWidth,
				cls: 'mainColumn mainColumnBody',
				items: [ checkbox ]
			});
			
			for (var i = 0; i < this.additionalColumns.length; i++) {
				cells.push({
					border: false,
					frame: false,
					width: this.additionalColumnWidths[i],
					cls: 'additionalColumn additionalColumnBody zebra' + (lineCount % 2),
					html: record.data[this.additionalColumns[i]]
				});
			}
			
			lineCount++;
			return true;
		}, this);
		
		return cells;
	}
});

Ext.reg('tablestylecheckcombobox', Ext.ux.TableStyleCheckComboBox);

customEventHelper.trigger(document, 'tableStyleCheckComboBoxLoaded');

Ext.namespace('Ext.ux');

Ext.ux.CheckComboBox = Ext.extend(Ext.form.ComboBox, {
	
	allLabel: 'all',
	noneLabel: 'inactive',
	multiLabel: 'multiple',
	singleSelect: false,
	useBulkButtons: true, // Whether to display the "all", "none" buttons.
	buttonAll: null,
	buttonNone: null,
	initialized: false,
	
	initComponent: function() {
		Ext.ux.CheckComboBox.superclass.initComponent.call(this);
		
		/*
		 * Instance variables (especially if they are objects) should be initialized here,
		 * otherwise all instances of CheckComboBox may share the same variable. Only "constants"
		 * and default settings should be created on prototype level.
		 */
		this.checkboxMap = [];
		
		this.refreshLabel();
	},
	
	initList: function(arg) {
		
		if (this.initialized && arg == this) {
			this.updateList();
			return;
		}
		
		// BEGIN: Code copied from "Ext.form.ComboBox#initList"
		var cls = 'x-combo-list';
		
		this.list = new Ext.Layer({
			shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain: false
		});
		
		var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
		this.list.setWidth(lw);
		this.list.swallowEvent('mousewheel');
		this.innerList = this.list.createChild({cls: cls + '-inner'});
		this.innerList.on('mouseover', this.onViewOver, this);
		this.innerList.on('mousemove', this.onViewMove, this);
		this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));
		
		this.view = new Ext.DataView({
			applyTo: this.innerList,
			tpl: this.tpl,
			singleSelect: true,
			selectedClass: this.selectedClass,
			itemSelector: this.itemSelector || '.' + cls + '-item'
		});
		// END: Code copied from "Ext.form.ComboBox#initList"
		
		var checkboxPanels = [];
		var bulkButtons = [];
		var count = 0;
		
		this.store.each(function(record) {
			
			var checkbox = new Ext.form.Checkbox({
				id: this.id + '-cb-' + record.data[this.valueField],
				boxLabel: record.data[this.displayField],
				checked: record.data[this.checkedField],
				handler: this.checkboxChanged.createDelegate(this, [ record ], true)
			});
			this.checkboxMap[record.data[this.valueField]] = checkbox;
			
			checkboxPanels[count] = new Ext.Panel({
				id: 'zebra-panel-' + count,
				layout: 'fit',
				border: false,
				cls: 'zebra' + (count % 2),
				items: [ checkbox ]
			});
			count++;
			
			return true;
		}, this);
		
		if (this.useBulkButtons) {
			
			var buttonTpl = new Ext.Template(
					'<table border="0" cellpadding="0" cellspacing="0"><tbody><tr><td>',
						'<button type="{1}">',
							'<div class="SmallButtonStyle" ',
									'style="padding-top: 1px; padding-left: 12px;">{0}</div>',
						'</button>',
					'</td></tr></tbody></table>'
			);
			
			this.buttonAll = new Ext.Button({
				template: buttonTpl,
				text: this.allLabel,
				handler: this.selectAll.createDelegate(this),
				disabled: this.singleSelect
			});
			
			this.buttonNone = new Ext.Button({
				template: buttonTpl,
				text: this.noneLabel,
				handler: this.selectNone.createDelegate(this),
				disabled: this.singleSelect
			});
			
			bulkButtons = [ this.buttonAll, this.buttonNone ];
		}
		
		this.initPanel(checkboxPanels, bulkButtons);
		this.refreshLabel();
		
		this.initialized = true;
	},
	
	/**
	 * Initializes/renders the root panel of this CheckComboBox.
	 * 
	 * NOTE: This function is overridden by Ext.ux.TableStyleCheckComboBox for a tabled layout.
	 */
	// private
	initPanel: function(checkboxes, bulkButtons) {
		this.panel = new Ext.Panel({
			renderTo: this.innerList,
			border: false,
			items: checkboxes,
			buttons: bulkButtons
		});
	},
	
	updateList: function() {
		
		this.store.each(function(record) {
			
			var checkbox = this.checkboxMap[record.data[this.valueField]];
			
			var checkboxChecked = "";
			
			if (checkbox) {
				checkbox.setValue(record.data[this.checkedField]);
				checkboxChecked = checkbox.getValue();
			}
			
		}, this);
		
	}, 
	
	setSingleSelect: function(singleSelect) {
		this.singleSelect = singleSelect;
		
		if (singleSelect) {
			this.selectNone();
			this.refreshLabel();
		}
		
		if (this.buttonAll && this.buttonNone) {
			if (singleSelect) {
				this.buttonAll.disable();
				this.buttonNone.disable();
			}
			else {
				this.buttonAll.enable();
				this.buttonNone.enable();
			}
		}
	},
	
	restrictHeight: function(){
		
		// BEGIN: Copied code from Ext.js ComboBox, to
		// limit height according to maxHeight property.
		// See LOGON-5682
		this.innerList.dom.style.height = '';
		var inner = this.innerList.dom;
		var h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight);
		h = Math.min(h, this.maxHeight);
		this.innerList.setHeight(h);
		// END: Copied code from Ext.js ComboBox
		
		this.list.beginUpdate();
		this.list.alignTo(this.wrap, this.listAlign);
		this.list.endUpdate();
	},
	
	selectAll: function() {
		
		var updateLabel = false;
		this.store.each(function(record) {
			var checkbox = this.checkboxMap[record.data[this.valueField]];
			if (checkbox) {
				checkbox.setValue(true);
			}
			else {
				record.data[this.checkedField] = true;
				updateLabel = true;
			}
		}, this);
		
		if (updateLabel) {
			
			this.refreshLabel();
		}
	},
	
	selectNone: function() {
		
		var updateLabel = false;
		this.store.each(function(record) {
			
			var checkbox = this.checkboxMap[record.data[this.valueField]];
			if (checkbox) {
				checkbox.setValue(false);
			}
			else {
				record.data[this.checkedField] = false;
				updateLabel = true;
			}
		}, this);
		
		if (updateLabel) {
			
			this.refreshLabel();
		}
	},
	
	setChecked: function(id, checked) {
		var idx = this.store.find(this.valueField, new RegExp('^' + id + '$'));
		var record = this.store.getAt(idx);
		record.data[this.checkedField] = checked;
		this.refreshLabel();
	},
	
	initChecked: function(id, checked) {
		var idx = this.store.find(this.valueField, new RegExp('^' + id + '$'));
		var record = this.store.getAt(idx);
		record.data[this.checkedField] = checked;
		var checkbox = this.checkboxMap[record.data[this.valueField]];
		//overridden in logon.js
    	this.suspendEvents(false);
		checkbox.setInitialValue(checked);
		this.resumeEvents();
		this.refreshLabel();
	},
	
	setDisabled: function(id, disabled) {
		var idx = this.store.find(this.valueField, new RegExp('^' + id + '$'));
		var record = this.store.getAt(idx);
		var checkbox = this.checkboxMap[record.data[this.valueField]];
		if ( disabled ) {			
			checkbox.disable();
		}
		else {
			checkbox.enable();
		}
	},
	
	refreshLabel: function() {
	
		var selectCount = 0;
		var selectedText = this.multiLabel;
		
		this.store.each(function(record) {
			
			if (record.data[this.checkedField]) {
				selectCount += 1;
				selectedText = record.data[this.displayField];
			}
		}, this);
		
		if (this.store.getCount() > 0) {
			if (selectCount == this.store.getCount() && this.store.getCount() != 1) {
				selectedText = this.allLabel;
			}
			else if (selectCount == 0) {
				selectedText = this.noneLabel;
			}
			else if (selectCount > 1) {
				selectedText = this.multiLabel;
			}
		}
		if (this.el) {
			this.setRawValue(selectedText);
		}
		this.emptyText = selectedText;
		this.applyEmptyText();
	},
	
	checkboxChanged: function(checkbox, event, record) {
		if ( this.singleSelect == true && checkbox.getValue() == true ) {
			this.store.each(function(curRecord) {
				if (curRecord.data[this.valueField] == record.data[this.valueField]) {
					record.data[this.checkedField] = checkbox.getValue();
				}
				else {
					var curCheckbox = this.checkboxMap[curRecord.data[this.valueField]];
					curCheckbox.setValue(false);
				}
			}, this);
		}
		else {
			record.data[this.checkedField] = checkbox.getValue();
		}
		this.fireEvent('change', this);
		this.refreshLabel();
	},
	
	getCheckedValues: function() {
		
		var res = [];
		var count = 0;
		this.store.each(function(record) {
			if (record.data[this.checkedField]) {
				res[count++] = record.data[this.valueField];
			}
			return true
		}, this);
		
		return res;
	},
	
	getCheckedValuesAsParams: function() {
		
		var res = {};
		var count = 0;
		this.store.each(function(record) {
			res[record.data[this.valueField]] = record.data[this.checkedField];
			return true
		}, this);
		
		return res;
	}
	
});

Ext.reg('checkcombobox', Ext.ux.CheckComboBox);

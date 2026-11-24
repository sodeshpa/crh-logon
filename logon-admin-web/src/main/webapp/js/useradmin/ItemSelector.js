/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

/*
 * Note that this control will most likely remain as an example, and not as a core Ext form
 * control.  However, the API will be changing in a future release and so should not yet be
 * treated as a final, stable API at this time.
 */
 
/** 
 * @class Ext.ux.ItemSelector
 * @extends Ext.form.Field
 * A control that allows selection of between two Ext.ux.MultiSelect controls.
 * 
 *  @history
 *    2008-06-19 bpm Original code contributed by Toby Stuart
 * 
 * @constructor
 * Create a new ItemSelector
 * @param {Object} config Configuration options
 */
 //var toMultiSelect;
 //var fromMultiSelect;
Ext.ux.ItemSelector = Ext.extend(Ext.form.Field,  {
	msWidth:200,
	msHeight:300,
	hideNavIcons:false,
	imagePath:"",
	iconLeft:"arrow_left.gif",
	iconRight:"arrow_right.gif",
	drawLeftIcon:true,
	drawRightIcon:true,
	fromStore:null,
	toStore:null,
	fromData:null, 
	toData:null,
	displayField:0,
	valueField:1,
	switchToFrom:false,
	allowDup:false,
	focusClass:undefined,
	delimiter:',',
	readOnly:false,
	toLegend:null,
	fromLegend:null,
	toSortField:null,
	fromSortField:null,
	toSortDir:'ASC',
	fromSortDir:'ASC',
	toTBar:null,
	fromTBar:null,
	bodyStyle:null,
	border:false,
	defaultAutoCreate:{tag: "div"},
	
	initComponent: function(){
		Ext.ux.ItemSelector.superclass.initComponent.call(this);
		this.addEvents({
			'rowdblclick' : true,
			'change' : true
		});         
	},

	onRender: function(ct, position){
		var parentPanel = this.findParentByType("panel");
		Ext.ux.ItemSelector.superclass.onRender.call(this, ct, position);

		this.fromMultiselect = new Ext.ux.Multiselect({
			legend: this.fromLegend,
			delimiter: this.delimiter,
			allowDup: this.allowDup,
			copy: this.allowDup,
			allowTrash: this.allowDup,
			dragGroup: this.readOnly ? null : "drop2-"+this.el.dom.id,
			dropGroup: this.readOnly ? null : "drop1-"+this.el.dom.id,
			width: this.msWidth,
			height: this.msHeight,
			dataFields: this.dataFields,
			data: this.fromData,
			displayField: this.displayField,
			valueField: this.valueField,
			store: this.fromStore,
			isFormField: false,
			tbar: this.fromTBar,
			appendOnly: true,
			sortField: this.fromSortField,
			sortDir: this.fromSortDir
		});
		this.fromMultiselect.on('dblclick', this.onRowDblClick, this);

		if (!this.toStore) {
			this.toStore = new Ext.data.SimpleStore({
				fields: this.dataFields,
				data : this.toData
			});
		}
		this.toStore.on('add', this.valueChanged, this);
		this.toStore.on('remove', this.valueChanged, this);
		this.toStore.on('load', this.valueChanged, this);

		this.toMultiselect = new Ext.ux.Multiselect({
			legend: this.toLegend,
			delimiter: this.delimiter,
			allowDup: this.allowDup,
			dragGroup: this.readOnly ? null : "drop1-"+this.el.dom.id,
			//dropGroup: this.readOnly ? null : "drop2-"+this.el.dom.id+(this.toSortField ? "" : ",drop1-"+this.el.dom.id),
			dropGroup: this.readOnly ? null : "drop2-"+this.el.dom.id+",drop1-"+this.el.dom.id,
			width: this.msWidth,
			height: this.msHeight,
			displayField: this.displayField,
			valueField: this.valueField,
			store: this.toStore,
			isFormField: false,
			tbar: this.toTBar,
			sortField: this.toSortField,
			sortDir: this.toSortDir,
			allAvailableData: this.allAvailableData
		});
		this.toMultiselect.on('dblclick', this.onRowDblClick, this);
				
		var p = new Ext.Panel({
			bodyStyle:this.bodyStyle,
			border:this.border,
			layout:"table",
			layoutConfig:{columns:3}
		});
		p.add(this.switchToFrom ? this.toMultiselect : this.fromMultiselect);
		var icons = new Ext.Panel({header:false});
		p.add(icons);
		var buttons = new Ext.Panel({header:false});
		p.add(this.switchToFrom ? this.fromMultiselect : this.toMultiselect);
		p.add(buttons);
		p.render(this.el);
		icons.el.down('.'+icons.bwrapCls).remove();
		buttons.el.down('.'+buttons.bwrapCls).remove();

		if (this.imagePath !== "" && this.imagePath.charAt(this.imagePath.length-1) != "/")
			this.imagePath += "/";
		this.iconLeft = this.imagePath + (this.iconLeft || 'left2.gif');
		this.iconRight = this.imagePath + (this.iconRight || 'right2.gif');
		var el=icons.getEl();
		this.addIcon = el.createChild({tag:'img', src:this.switchToFrom?this.iconLeft:this.iconRight, style:{cursor:'pointer', margin:'2px'}});
		el.createChild({tag: 'br'});
		this.removeIcon = el.createChild({tag:'img', src:this.switchToFrom?this.iconRight:this.iconLeft, style:{cursor:'pointer', margin:'2px'}});
		el.createChild({tag: 'br'});
		var buttonPanelEl=buttons.getEl();
		if ( !parentPanel.referencedMultiSelectionPresent ||( parentPanel.referencedMultiSelectionPresent && this.name == 'itemselectorMultiple' )){        
			buttonPanelEl.insertHtml('afterBegin','<input class="SmallButtonStyle" onClick="' + parentPanel.name + '.findByType(\'itemselector\')[0].selectNone()" type="button" value=" keine "/>');
			buttonPanelEl.insertHtml('afterBegin','<input class="SmallButtonStyle" onClick="' + parentPanel.name + '.findByType(\'itemselector\')[0].selectAll()"type="button" value=" alle "/>');
		}
		else if ( parentPanel.referencedMultiSelectionPresent && this.name == 'itemselectorSimple'){
			buttonPanelEl.insertHtml('afterBegin','<input class="SmallButtonStyle" onClick="' + parentPanel.name + '.findByType(\'itemselector\')[1].selectNone()" type="button" value=" keine "/>');
			buttonPanelEl.insertHtml('afterBegin','<input class="SmallButtonStyle" onClick="' + parentPanel.name + '.findByType(\'itemselector\')[1].selectAll()"type="button" value=" alle "/>');        
		}
		
		if (!this.readOnly) {
			this.addIcon.on('click', this.fromTo, this);
			this.removeIcon.on('click', this.toFrom, this);
		}
		if (!this.drawLeftIcon || this.hideNavIcons) { this.addIcon.dom.style.display='none'; }
		if (!this.drawRightIcon || this.hideNavIcons) { this.removeIcon.dom.style.display='none'; }

		var tb = p.body.first();
		this.el.setWidth(p.body.first().getWidth());
		p.body.removeClass();
		
		this.hiddenName = this.name;
		var hiddenTag={tag: "input", type: "hidden", value: "", name:this.name};
		this.hiddenField = this.el.createChild(hiddenTag);
		this.valueChanged(this.toStore);
		this.findParentByType("panel").lowerToMultiSelect = this.toMultiselect;
		this.findParentByType("panel").lowerFromMultiSelect= this.fromMultiselect;
	},
	
	initValue:Ext.emptyFn,
   
	fromTo : function() {
		var selectionsArray = this.fromMultiselect.view.getSelectedIndexes();
		var records = [];
		if (selectionsArray.length > 0) {
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.fromMultiselect.view.store.getAt(selectionsArray[i]);
				records.push(record);
			}
			if(!this.allowDup)selectionsArray = [];
			for (var j = 0; j < records.length; j++) {
				record = records[j];
				if(this.allowDup){
					var x=new Ext.data.Record();
					record.id=x.id;
					this.toMultiselect.view.store.add(record);
				}else{
					this.fromMultiselect.view.store.remove(record);
					this.toMultiselect.view.store.add(record);
					if (this.name == 'itemselectorMultiple'){                    
						this.addRelated(record);
					}
					selectionsArray.push((this.toMultiselect.view.store.getCount() - 1));
				}
			}
		}
		this.toMultiselect.view.refresh();
		this.fromMultiselect.view.refresh();
		if(this.toSortField)this.toMultiselect.store.sort(this.toSortField, this.toSortDir);
		if(this.allowDup)this.fromMultiselect.view.select(selectionsArray);
		else this.toMultiselect.view.select(selectionsArray);
	},
	
	toFrom : function() {
		var selectionsArray = this.toMultiselect.view.getSelectedIndexes();
		var records = [];
		if (selectionsArray.length > 0) {
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.toMultiselect.view.store.getAt(selectionsArray[i]);
				records.push(record);
			}
			selectionsArray = [];
			for (var j = 0; j < records.length; j++) {
				record = records[j];
				this.toMultiselect.view.store.remove(record);
				if(!this.allowDup){
					this.fromMultiselect.view.store.add(record);
					if (this.name == 'itemselectorMultiple'){                    
						this.removeRelated(record);
					}                    
					selectionsArray.push((this.fromMultiselect.view.store.getCount() - 1));
				}
			}
		}
		this.fromMultiselect.view.refresh();
		this.toMultiselect.view.refresh();
		if(this.fromSortField)this.fromMultiselect.store.sort(this.fromSortField, this.fromSortDir);
		this.fromMultiselect.view.select(selectionsArray);
	},
	
	valueChanged: function(store) {
		var record = null;
		var values = [];
		for (var i=0; i<store.getCount(); i++) {
			record = store.getAt(i);
			values.push(record.get(this.valueField));
		}
		this.hiddenField.dom.value = values.join(this.delimiter);
		this.fireEvent('change', this, this.getValue(), this.hiddenField.dom.value);
	},
	
	getValue : function() {
		return this.hiddenField.dom.value;
	},
	
	onRowDblClick : function(vw, index, node, e) {
		return this.fireEvent('rowdblclick', vw, index, node, e);
	},
	reset: function(){
		range = this.toMultiselect.store.getRange();
		this.toMultiselect.store.removeAll();
		if (!this.allowDup) {
			this.fromMultiselect.store.add(range);
			this.fromMultiselect.store.sort(this.displayField,'ASC');
		}
		this.valueChanged(this.toMultiselect.store);
	},
	selectNone: function(){
				this.toMultiselect.view.selectRange(0,this.toMultiselect.store.data.items.length,false);
				this.toFrom();
	},
	selectAll: function(){
				this.fromMultiselect.view.selectRange(0,this.fromMultiselect.store.data.items.length,false);
				this.fromTo();
	},
	addRelated: function(record){
		var id1 = record.data.code;
		var desc = record.data.desc;
		record = Ext.data.Record.create([
				{name: 'id1',type:"string"},
				{name: 'id2', type:'string'},
				{name: 'desc', type: 'string'}
		]);
		
		for (var i=0; i<this.toMultiselect.allAvailableData.length; i++){
			if(	this.toMultiselect.allAvailableData[i].id1 == id1){
				var newRec = new record({
					id1: id1,
					id2: this.toMultiselect.allAvailableData[i].id2,
					desc: desc + "," + this.toMultiselect.allAvailableData[i].desc
				});
				this.findParentByType("panel").lowerFromMultiSelect.view.store.add(newRec);
			}	
		}
	},
	removeRelated: function(record){
		var id1 = record.data.code;
		var fromMulti = this.findParentByType("panel").lowerFromMultiSelect.view.store;
		var toMulti = this.findParentByType("panel").lowerToMultiSelect.view.store;
		var doUntil = fromMulti.data.items.length;
		var removeThese = [];		
		for (var i=0; i<doUntil; i++){
			var rec = fromMulti.getAt(i);
			if( rec.data.id1 == id1){
				removeThese.push(rec);
			}		
		}
		for (var j=0; j< removeThese.length; j++){
				 fromMulti.remove(removeThese[j]);		
		}
		doUntil = toMulti.data.items.length;
		removeThese = [];
		for (var k = 0; k < doUntil; k++){
			var rec2 = toMulti.getAt(k);
			if( rec2.data.id1 == id1){
				 removeThese.push(rec2);
			}		
		}
		for (var l = 0; l < removeThese.length; l++){
				 toMulti.remove(removeThese[l]);		
		}				
	}    
});
Ext.reg("itemselector", Ext.ux.ItemSelector);
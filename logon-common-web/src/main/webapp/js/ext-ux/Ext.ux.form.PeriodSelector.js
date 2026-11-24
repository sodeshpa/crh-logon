/*
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
*/
Ext.namespace('Ext.ux');

Ext.ux.PeriodSelector = Ext.extend(Ext.BoxComponent, {
	
	itemCls: 'period-selector',
	fromDateComponent: null,
	toDateComponent: null,
	buttonTemplate: new Ext.Template(
			'<table border="0" cellpadding="0" cellspacing="0"><tbody><tr>',
			'<td><em unselectable="on"><button type="{1}">{0}</button></em></td>',
			'</tr></tbody></table>'),
	
	expandFrom: {
		show: true,
		text: '<img src="../images/arrow_left_blue.gif"/>',
		cls: 'x-period-expand-from',
		handler: function() { 
			var date = this.fromDateComponent.getValue();
			if (date) {
				this.fromDateComponent.setValue(date.add(Date.DAY, -1));
			}
		}
	},
	
	expandTo: {
		show: true,
		text: '<img src="../images/arrow_right_blue.gif"/>',
		cls: 'x-period-expand-to',
		handler: function() { 
			var date = this.toDateComponent.getValue();
			if (date) {
				this.toDateComponent.setValue(date.add(Date.DAY, 1));
			}
		}
	},
			
	yesterday: {
		show: true,
		text: 'Yd',
		cls: 'x-period-yesterday',
		handler: function() {
			var today = new Date();
			today.clearTime(false);
			var yesterday = today.add(Date.DAY, -1);
			this.fromDateComponent.setValue(yesterday);
			this.toDateComponent.setValue(yesterday);
		}
	},
	today: {
		show: true,
		text: 'Td',
		cls: 'x-period-today',
		handler: function() {
			var today = new Date();
			today.clearTime(false);
			this.fromDateComponent.setValue(today);
			this.toDateComponent.setValue(today);
		}
	},
	tomorrow: {
		show: true,
		text: 'Tm',
		cls: 'x-period-tomorrow',
		handler: function() {
			var today = new Date();
			today.clearTime(false);
			var tomorrow = today.add(Date.DAY, 1);
			this.fromDateComponent.setValue(tomorrow);
			this.toDateComponent.setValue(tomorrow);
		}
	},
	week: {
		show: true,
		text: 'W',
		cls: 'x-period-week',
		handler: function() {
			var today = new Date();
			today.clearTime(false);
			var day = today.getDay();
			this.fromDateComponent.setValue(today.add(Date.DAY, (-1*day) + 1));
			this.toDateComponent.setValue(today);
		}
	},
	month: {
		show: true,
		text: 'M',
		cls: 'x-period-month',
		handler: function() {
			var today = new Date();
			today.clearTime(false);
			var date = today.getDate();
			this.fromDateComponent.setValue(today.add(Date.DAY, (-1*date) + 1));
			this.toDateComponent.setValue(today);
		}
	},
	year: {
		show: true,
		text: 'Yr',
		cls: 'x-period-year',
		handler: function() {
			var today = new Date();
			today.clearTime(false);
			var date = today.getDate();
			var month = today.getMonth();
			this.fromDateComponent.setValue(today.add(Date.DAY, (-1*date) + 1).add(Date.MONTH, (-1*month)));
			this.toDateComponent.setValue(today);
		}
	},
	
	buttons: null,
	
	template: new Ext.XTemplate(
			'<table cellspacing="0">',
				'<tpl for="."><td class="x-period-item {cls}"></td></tpl>',
			'</table>'),
	
	initComponent : function(){
		Ext.ux.PeriodSelector.superclass.initComponent.call(this);
		this.addEvents(
				'select'
		);
		this.buttons = [ this.expandFrom, this.yesterday, this.today, this.tomorrow, this.week, this.month, this.year, this.expandTo ];
	},
	
	onRender : function(container, position){
		var el = document.createElement("div");
		el.id = this.getId();
		el.className = this.itemCls;
		el.innerHTML = this.template.apply(this.buttons);
		container.dom.insertBefore(el, position);
		this.el = Ext.get(el);
		
		for (var i=0;i<this.buttons.length;i++) {
			if (this.buttons[i].show) {
				var b = new Ext.Button({
					text: this.buttons[i].text,
					template: this.buttonTemplate,
					handler: this.buttons[i].handler.createDelegate(this),
					cls: 'x-period-btn',
					overCls: 'x-period-btn-over', 
					renderTo: this.el.child('td.' + this.buttons[i].cls, true)
				});
				b.on("click", function(button, e) {
						this.fireEvent('select', this);
					}.createDelegate(this));
			}
		}
	}	
});
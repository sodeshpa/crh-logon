/**
 * @class Ext.ux.form.ColorPickerFieldPlus
 * @extends Ext.form.TwinTriggerField
 * This class makes Ext.ux.ColorPicker and Ext.ux.form.ColorPickerField available as a form field.
 * @license: IYUIYBMAC (If you use it, you buy me a Coke)
 * @author of Ext.ux.form.ColorPickerField: Robert B. Williams (extjs id: vtswingkid)
 * @author of Ext.ux.form.ColorPickerFieldPlus: Ramiro P. Saenz
 * @constructor
 * Creates a new ColorPickerFieldPlus
 * @param {Object} config Configuration options
 * @version 1.0
 * @date 14/04/09 (dd/mm/yy)
 */

Ext.namespace("Ext.ux", "Ext.ux.menu", "Ext.ux.form");
Ext.ux.menu.ColorItem = function(config){
	if(!config)config={};
	config.style="width:350px;";
	Ext.ux.menu.ColorItem.superclass.constructor.call(this, new Ext.ux.ColorPicker(config), config);
	this.picker = this.component;
	this.addEvents('select');
	this.picker.on("render", function(picker){
		picker.getEl().swallowEvent("click");
	});
	this.picker.on("select", this.onSelect, this);	
};
Ext.extend(Ext.ux.menu.ColorItem, Ext.menu.Adapter, {
	// private
	onSelect : function(picker, color){
		this.fireEvent("select", this, color, picker);
		Ext.ux.menu.ColorItem.superclass.handleClick.call(this);
	}
});

Ext.ux.menu.ColorMenu = function(config){
	Ext.ux.menu.ColorMenu.superclass.constructor.call(this, config);
	this.plain = true;
	var ci = new Ext.ux.menu.ColorItem(config);
	this.add(ci);
	this.picker = ci.picker;
	this.relayEvents(ci, ["select"]);	
};
Ext.extend(Ext.ux.menu.ColorMenu, Ext.menu.Menu, {
	beforeDestroy : function() {
		this.picker.destroy();
	}
});

Ext.form.VTypes["hexText"] = "Invalid Hex code, eg: (#F0F0F0)";
//Ext.form.VTypes["hex"] = /^#?([a-f]|[A-F]|[0-9]){3}(([a-f]|[A-F]|[0-9]){3})?$/;
Ext.form.VTypes["hex"] = function(v){ return /^#?([a-f]|[A-F]|[0-9]){3}(([a-f]|[A-F]|[0-9]){3})?$/.test(v); };


Ext.ux.form.ColorPickerFieldPlus = Ext.extend(Ext.form.TwinTriggerField,  {
	/**
	 * @cfg {String} fieldClass The default CSS class for the field (defaults to "x-form-trigger")
	 */
	trigger1Class: 'x-form-color-trigger-1',
	/**
	 * @cfg {String} fieldClass The default CSS class for the field (defaults to "x-form-trigger")
	 */
	trigger2Class: 'x-form-color-trigger-2',
	/**
	 * @cfg {String} fieldClass The default CSS class for the field (defaults to "x-form-image")
	 */
	hideTrigger1: false,
	/**
	 * @cfg {String} fieldClass The default CSS class for the field (defaults to "x-form-image")
	 */
	hideTrigger2: false,
	// private
	vtype: 'hex',
	// private

	initComponent: function() {
		Ext.ux.form.ColorPickerFieldPlus.superclass.initComponent.call(this);
		this.menu1 = new Ext.menu.ColorMenu();
		this.menu2 = new Ext.ux.menu.ColorMenu();
		this.on('valid', function() {
			var v = this.getValue() || "ffffff";
			var i = this.menu2.picker.rgbToHex(
				this.menu2.picker.invert(this.menu2.picker.hexToRgb(v)));
			this.el.applyStyles('background: #' + v + '; color: #' + i + ';');
			
			// fire change event
			if(String(v) !== String(this.startValue)){
			  this.fireEvent('change', this, v, this.startValue);
			  this.startValue = v;
			} 

		}, this);
	},
	setValue: function(v) {
		Ext.ux.form.ColorPickerFieldPlus.superclass.setValue.apply(this, arguments);
		var i = this.menu2.picker.rgbToHex(this.menu2.picker.invert(this.menu2.picker.hexToRgb(v)));
		this.el.applyStyles('background: #' + (v || "ffffff") + '; color: #' + i + ';');
	},
	
	onDestroy : function(){
		if(this.menu2) {
			this.menu2.destroy();
		}
		if(this.wrap){
			this.wrap.remove();
		}
		Ext.ux.form.ColorPickerFieldPlus.superclass.onDestroy.call(this);
	},
	reset : function(){
		Ext.ux.form.ColorPickerFieldPlus.superclass.reset.call(this);
		this.el.applyStyles('background: #FFFFFF; color: #000000;');
	},
	menu1Listeners : {
		select: function(m, c){
			this.setValue(c);
			this.focus.defer(10, this);
		},
		show : function(m){ // retain focus styling
			this.onFocus();
		},
		hide : function(){
			this.focus.defer(10, this);
			var ml = this.menu1Listeners;
			this.menu1.un("select", ml.select,  this);
			this.menu1.un("show", ml.show,  this);
			this.menu1.un("hide", ml.hide,  this);
		}
	},
	menu2Listeners : {
		select: function(m, c){
			this.setValue(c);
			this.focus.defer(10, this);
		},
		show : function(m){ // retain focus styling
			this.onFocus();
		},
		hide : function(){
			this.focus.defer(10, this);
			var ml = this.menu2Listeners;
			this.menu2.un("select", ml.select,  this);
			this.menu2.un("show", ml.show,  this);
			this.menu2.un("hide", ml.hide,  this);
		}
	},
	onTrigger1Click : function(){
		if(this.disabled){
			return;
		}
		this.menu1.on(Ext.apply({}, this.menu1Listeners, {
			scope:this
		}));

		this.menu1.show(this.el, "tl-bl?");
	},
	onTrigger2Click : function(){
		if(this.disabled){
			return;
		}
		this.menu2.on(Ext.apply({}, this.menu2Listeners, {
		   scope:this
		}));
		this.menu2.show(this.el, "tl-bl?");
		this.menu2.picker.setColor(this.getValue());
	}
});
Ext.reg('colorpickerfieldplus', Ext.ux.form.ColorPickerFieldPlus);
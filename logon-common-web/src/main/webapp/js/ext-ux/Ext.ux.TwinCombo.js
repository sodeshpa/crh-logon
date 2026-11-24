Ext.namespace('Ext.ux');

Ext.ux.TwinCombo = Ext.extend(Ext.form.ComboBox, {
	initComponent: function() {
		this.triggerConfig = {
			tag:'span', cls:'x-form-twin-triggers', cn:[
			{tag: "input", type:"button", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger1Class},
			{tag: "input", type:"button", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger2Class}
		]};
		this.onTrigger2Click = this.onTrigger2Click.createInterceptor(function() {
			this.collapse();
		});
		Ext.ux.TwinCombo.superclass.initComponent.call(this);
	},
	getTrigger: Ext.form.TwinTriggerField.prototype.getTrigger,
	initTrigger: Ext.form.TwinTriggerField.prototype.initTrigger,
	onTrigger1Click: Ext.form.ComboBox.prototype.onTriggerClick,
	trigger1Class: Ext.form.ComboBox.prototype.triggerClass,
	trigger2Class: 'x-form-clear-trigger',
	onTrigger2Click: function() {
		this.clearValue();
	}
});
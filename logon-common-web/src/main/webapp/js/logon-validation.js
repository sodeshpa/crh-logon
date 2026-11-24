Ext.apply(Ext.form.VTypes,{

	numericText: "Only numbers are allowed.",
	numericMask: /[0-9]/,
	numericRe: /[0-9]/,
	numeric :function (v) {
		return this.numericRe.test(v);
	} ,

	decNumText: "Only decimal numbers are allowed.",
	decNumMask: /[\-+.0-9]/,
	decNumRe: /^[\-+]?[0-9]+(\.[0-9]+)?$/,
	decNum : function (v) {
		return this.decNumRe.test(v);
	}

}); 
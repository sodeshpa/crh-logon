Ext.data.DWRProxy = function(dwrCall){
  Ext.data.DWRProxy.superclass.constructor.call(this);
  this.dwrCall = dwrCall;
};

Ext.extend(Ext.data.DWRProxy, Ext.data.DataProxy, {
  load : function(params, reader, callback, scope, arg) {
	if(this.fireEvent("beforeload", this, params) !== false) {
	
	  var delegate = this.loadResponse.createDelegate(this, [reader, callback, scope, arg], 1);
	  var callParams = [];
	  
	  callParams.push(params.query);
			
	  callParams.push(delegate);
	  this.dwrCall.apply(this, callParams);
	} 
	else {
	  callback.call(scope || this, null, arg, false);
	}
  },
  
  loadResponse : function(listRange, reader, callback, scope, arg) {
	var result;
	try {
	  result = reader.read(listRange);
	} catch(e) {
	  this.fireEvent("loadexception", this, null, response, e);
	  callback.call(scope, null, arg, false);
	  return;
	}
	callback.call(scope, result, arg, true);
  },

  update : function(dataSet){},

  updateResponse : function(dataSet)
  {}
});



Ext.data.ObjectReader = function(meta, recordType){
	Ext.data.ObjectReader.superclass.constructor.call(this, meta, recordType);
};
Ext.extend(Ext.data.ObjectReader, Ext.data.DataReader, {
	read : function(response){
	var sid = this.meta ? this.meta.id : null;
		var recordType = this.recordType, fields = recordType.prototype.fields;
		var records = [];
		var root = response;
		for(var i = 0; i < root.length; i++){
			var obj = root[i];
			var values = {};
			var id = obj[sid];
			
			for(var j = 0, jlen = fields.length; j < jlen; j++){
				var f = fields.items[j];
				var k = f.mapping !== undefined && f.mapping !== null ? f.mapping : f.name;

				var v = obj[k] !== undefined ? obj[k] : f.defaultValue;
				v = f.convert(v);
				values[f.name] = v;
				
			}
			var record = new recordType(values, id);
			records[records.length] = record;
		}
		return {
			records : records,
			totalRecords : records.length
		};
	}
});

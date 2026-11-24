Ext.namespace('Ext.ux.data');

Ext.ux.data.OriginalRecordStore = function(records) {

	this.originalRecords = new Array();
	this.nbrOfRecords = 0;

	if (!Ext.isEmpty(records)) {
		for (var index = 0; index < records.length; index++) {
			var oRecord = records[index];

			if (!Ext.isEmpty(oRecord)) {
				var nRecord = oRecord.copy();

				this.originalRecords.push(nRecord);
			}
		}

		this.nbrOfRecords = this.originalRecords.length;
	}

};

Ext.ux.data.OriginalRecordStore.prototype = { 

	isEmpty : function() {
		return !(this.nbrOfRecords > 0);
	}, 

	getById : function(recordId) {
		var result = null;

		if (!Ext.isEmpty(recordId) && (!this.isEmpty())) {
			for (var index = 0; index < this.nbrOfRecords; index++) {
				var oRecord = this.originalRecords[index];

				if ((!Ext.isEmpty(oRecord)) && (oRecord.id === recordId)) {
					result = oRecord;

					break;
				}
			}
		}

		return result;
	}

};
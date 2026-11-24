Ext.namespace('Ext.ux.valid');

Ext.ux.valid.ValidationMessage = function(type, recordId, fieldId, message) {
	this.type = type;
	this.recordId = recordId;
	this.fieldId = fieldId;
	this.message = message;
};

Ext.ux.valid.ValidationMessage.TYPE_ERROR = 'error';
Ext.ux.valid.ValidationMessage.TYPE_INFO = 'info';
Ext.ux.valid.ValidationMessage.TYPE_WARNING = 'warning';
Ext.ux.valid.ValidationMessage.TYPE_SUCCESS = 'success';

Ext.ux.valid.ValidationMessages = function() {
	this.errorMessages = new Array();
	this.infoMessages = new Array();
	this.warningMessages = new Array();
	this.successMessages = new Array();
	this.erroneousDwrRecords = new Array();
};

Ext.ux.valid.ValidationMessages.prototype = {

	hasErrors: function() {
		return (this.errorMessages.length > 0);
	}, 

	hasInfoMessages: function() {
		return (this.infoMessages.length > 0);
	},
	
	hasSuccessMessages: function() {
		return (this.successMessages.length > 0);
	}, 

	hasWarningMessages: function() {
		return (this.warningMessages.length > 0);
	}, 

	hasOnlyErrors: function() {
		return this.hasErrors && (!this.hasInfoMessages()) && (!this.hasWarningMessages()) && (!this.hasSuccessMessages());
	}, 

	getErroneousDwrRecordIds: function() {
		return this.erroneousDwrRecords;
	},

	add: function(message) {
		if (!Ext.isEmpty(message)) {
			if (Ext.ux.valid.ValidationMessage.TYPE_ERROR === message.type) {
				this.errorMessages.push(message);
			}
			else if (Ext.ux.valid.ValidationMessage.TYPE_INFO === message.type) {
				this.infoMessages.push(message);
			} 
			else if (Ext.ux.valid.ValidationMessage.TYPE_WARNING === message.type) {
				this.warningMessages.push(message);
			}
			else if (Ext.ux.valid.ValidationMessage.TYPE_SUCCESS === message.type) {
				this.successMessages.push(message);
			}
		} 
	}, 

	displayMessages: function() {
		if (this.hasErrors()) {
			this.displayMessagesWithType(Ext.ux.valid.ValidationMessage.TYPE_ERROR);
		}
		
		if (this.hasWarningMessages()) {
			this.displayMessagesWithType(Ext.ux.valid.ValidationMessage.TYPE_WARNING);
		}

		if (this.hasInfoMessages()) {
			this.displayMessagesWithType(Ext.ux.valid.ValidationMessage.TYPE_INFO);
		}
		
		if (this.hasSuccessMessages()) {
			this.displayMessagesWithType(Ext.ux.valid.ValidationMessage.TYPE_SUCCESS);
		}		
	}, 

	displayMessagesWithType: function(type) {
		var msgs = new Array();
		var msgText = '';
		var msgTypeInvalid = false;

		if (Ext.ux.valid.ValidationMessage.TYPE_ERROR === type) {
			msgs = this.errorMessages;
		} 
		else if (Ext.ux.valid.ValidationMessage.TYPE_INFO === type) {
			msgs = this.infoMessages;
		} 
		else if (Ext.ux.valid.ValidationMessage.TYPE_WARNING === type) {
			msgs = this.warningMessages;
		}
		else if (Ext.ux.valid.ValidationMessage.TYPE_SUCCESS === type ) {
			msgs = this.successMessages;
		} 		
		else {
			msgTypeInvalid = true;
		}

		for (var index = 0; index < msgs.length; index++) {
			var msg = msgs[index];

			msgText += msg.message;

			if (index != (msgs.length - 1)) {
				msgText += '<br/>';
			}
		}

		if (!msgTypeInvalid) {
			Logon.message[type](msgText);
		}
		else {
			Logon.message.info(msgText);
		}

	}, 

	processDwrMessages : function(dwrMessageBeans) {
		if (!Ext.isEmpty(dwrMessageBeans)) {
			for (var index = 0; index < dwrMessageBeans.length; index++) {
				var dwrMessage = dwrMessageBeans[index];

				if (!Ext.isEmpty(dwrMessage)) {
					this.add(
						new Ext.ux.valid.ValidationMessage(
							dwrMessage.type, 
							dwrMessage.recordId, 
							dwrMessage.fieldId, 
							dwrMessage.message));

					if (Ext.ux.valid.ValidationMessage.TYPE_ERROR === dwrMessage.type) {
						this.erroneousDwrRecords.push(dwrMessage.recordId);
					}
				}
			}
		}
	}

};

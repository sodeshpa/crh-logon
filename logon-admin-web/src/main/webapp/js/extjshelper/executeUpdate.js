executeUpdate = {
		texts: {},
		updateButtons : [], // list of already added update buttons
		// temporary objects for a single call
		divId : String,
		updateUrl : String,
		updateParams : String,
		exception : String,
		question : String,
		// old state
		baseRenderer: Object,
		showLoadIndicator: Boolean,
		renderer: {
			render : function (el, response, updateManager, callback) {
						// restore old state
						var diva = Ext.get(executeUpdate.divId);
						updater = diva.getUpdater();
						updater.setRenderer(executeUpdate.baseRenderer);
						updater.showLoadIndicator = executeUpdate.showLoadIndicator;
						
						// special handling for exception
						if (response.responseText.match(executeUpdate.exception)) {
							Ext.Msg.show({
								title: executeUpdate.texts.exceptionTitle,
								msg: executeUpdate.question,
								buttons: Ext.Msg.YESNO,
								icon: Ext.MessageBox.QUESTION,
								fn: function(btn) {

									// set back the original error handler
									if (btn == 'yes') {
										executeUpdate.update(executeUpdate.divId, executeUpdate.updateUrl, executeUpdate.updateParams);
									}
									else {
										if (!executeUpdate.updateButtons[executeUpdate.divId]) {
											var params = '';
											var separator = '';
											for(var i in executeUpdate.updateParams) {
												params += separator + "'" + i + "' : '" + executeUpdate.updateParams[i].replace("'", "''") +"'";
												separator = ', ';
											}
											// add update button to the div
											document.getElementById(executeUpdate.divId).innerHTML =
												"<input type='button' name='Update' value='" + executeUpdate.texts.updateButtonText +
												"' onclick=\"executeUpdate.update('" + executeUpdate.divId + "', '" + executeUpdate.updateUrl + "', { " + params + " });\"/>" +
												document.getElementById(executeUpdate.divId).innerHTML;
											executeUpdate.updateButtons[executeUpdate.divId] = true;
										}
									}
								}
							});
						}
						else {
							// call the original renderer
							executeUpdate.baseRenderer.render(el, response, updateManager, callback);
						}
					} 
		},
		init : function(updatedText, exceptionTitle, updateButtonText) {
			executeUpdate.texts.updatedText = updatedText;
			executeUpdate.texts.exceptionTitle = exceptionTitle;
			executeUpdate.texts.updateButtonText = updateButtonText;
		},
		// public functions
		update : function (divId, updateUrl, updateParams) {
			var diva = Ext.get(divId);
			diva.load( {
				url : updateUrl,
				params : updateParams,
				scripts : true
			});
			delete executeUpdate.updateButtons[divId];
		},
		run : function (divId, executeUrl, executeParams, updateUrl, updateParams, exception, question) {
			var diva = Ext.get(divId);
			updater = diva.getUpdater();
			// store old state and input parameters
			executeUpdate.baseRenderer = updater.getRenderer();
			executeUpdate.showLoadIndicator = updater.showLoadIndicator;
			executeUpdate.divId = divId;
			executeUpdate.updateUrl = updateUrl;
			executeUpdate.updateParams = updateParams;
			executeUpdate.exception = exception;
			executeUpdate.question = question;

			// set fake updater parameters
			updater.showLoadIndicator = false;
			updater.setRenderer(executeUpdate.renderer);

			diva.load( {
				url : executeUrl,
				params : executeParams,
				scripts : true
			});
			
			// the old state is restored in the local renderer
		}

}
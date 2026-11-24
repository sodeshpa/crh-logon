function grayoutScreen(divp){
	var greyLayer =	divp;
	
	if (typeof divp == 'undefined' || divp == null) {
		greyLayer =	 Ext.get('grayout');
	}
	
	greyLayer.setWidth(document.body.scrollWidth);
	greyLayer.setHeight(document.body.scrollHeight);
	greyLayer.show();
}

function unGrayoutScreen(divp){
	var greyLayer =	divp;
	
	if (typeof divp == 'undefined' || divp == null) {
		greyLayer =	 Ext.get('grayout');
	}	
	greyLayer.hide();
}

var logonPopup = null;
var logonPopupReadyCallback = null;
var logonPopupHideCallback=null;

function showPopup(url, loadtext, parameters, divp, transparentBg, shiftX){
	if (!transparentBg) {
		grayoutScreen(divp);
	}
	
	if (typeof divp == 'undefined' || divp == null) {
		divp = Ext.get('logonPopup');
	}
	
	logonPopup = divp;
	logonPopupReadyCallback = null;
	var submitParams;
	if (parameters) {
		submitParams = parameters;
	}
	else {
		submitParams = {};
	}
	submitParams.showPopup = true;
	divp.load({
		url: url, 
		text: loadtext,
		scripts: true,
		params: submitParams,
		callback: function(el){
			el.center();
			if (shiftX) {
				el.setLeft(el.getLeft() + shiftX);
			}
			if (el.getLeft() < 0){
				el.setLeft(0);
			}
			if (el.getTop() < 0) {
				/*
				 * LOGON-6542 
				 * (Material Enhancement: when "Create MatPlant Entry" Production Plants are not displayed)
				 * It seems, el.center() in some resolutions could not center the popup properly,
				 * resulting a negative top attribute, which makes the popup unusable.
				 */
				el.setTop(4);
			}
			el.show();
			if (logonPopupReadyCallback) {
				logonPopupReadyCallback();
			}
			
			/*
			 * LOGON-6117
			 * (Edit assignment popup for simple vehicles - make it scrollable)
			 */
			if(window.attachEvent) {
			    window.attachEvent('onresize', resizePopup);
			}
			else if(window.addEventListener) {
			    window.addEventListener('resize', resizePopup, true);
			}
			else {
				console.info("The browser does not support Javascript event binding");
			}
			resizePopup();
		}
	});
}

function onPopupReady(callback) {
	
	logonPopupReadyCallback = callback;
}

function showPopupHtml(html,divp){
	
	var greyLayer = Ext.get('grayout');
	
	if (typeof divp == 'undefined' || divp == null) {
		divp = Ext.get('logonPopup'); 
	}
	else {
		greyLayer = Ext.get('grayout2');
	}

	grayoutScreen(greyLayer);
	logonPopup = divp;
	divp.dom.innerHTML = html;
	divp.update(html);
	divp.repaint();
	divp.center();
	if (divp.getLeft() < 0){
		divp.setLeft(0);
	}	
	divp.show();
}

/**
 * 
 * @returns IE version if browers is Internet explorer otherwise returns -1
 * WARNING: only works below inter explorer versions 11. in IE 11 they changed the user agent so it returns -1 aswell.
 */
function getInternetExplorerVersion() {
	var rv = -1;
	if (navigator.appName == 'Microsoft Internet Explorer'){
		var ua = navigator.userAgent;
		var re  = new RegExp("MSIE ([0-9]{1,}[.0-9]{0,})");
		if (re.exec(ua) != null){
			rv = parseFloat( RegExp.$1 );
		}
	}
	return rv;
}


function resizePopup(){
	var popupEl = Ext.get('logonPopup');
	
	// reset the popup's size to default
	popupEl.removeClass('popup-scrollable').setHeight('auto');
	
	var top = popupEl.getTop();
	var windowHeight = Ext.getBody().getViewSize().height;
	
	// Check if popup height higher than browser height
	if(popupEl.getHeight() + 4 > windowHeight){
		
		var scrollableClass;
		getInternetExplorerVersion();
		if (getInternetExplorerVersion() == 8 || getInternetExplorerVersion() == 9) {
			scrollableClass = 'old-ie-popup-scrollable';
	    }
		else {
			scrollableClass = 'popup-scrollable';
		}
		
		popupEl.addClass(scrollableClass).setHeight(windowHeight - 4);
	}
}

function hidePopup(popup){
	
	var greyLayer = Ext.get('grayout');
	
	if (typeof popup == 'undefined' || popup == null) {
		popup = Ext.get("logonPopup");
		logonPopup = null;
	}
	else {
		greyLayer = Ext.get('grayout2');
	}
	
	if (popup){
		popup.removeClass('popup-scrollable').setHeight('auto');
		popup.hide();
	}
	popup = null;
	logonPopupReadyCallback = null;
	unGrayoutScreen(greyLayer);
	
	// Remove onresize event when closing the popup
	if (window.detachEvent) {
		// For IE8
		window.detachEvent("onresize", resizePopup);
	} else if (window.removeEventListener) {
		window.removeEventListener("resize", resizePopup);
	} else {
		console.info("The browser does not support Javascript event binding");
	}
}
/**
 * Give the name of the function if you want to call that function after hide popup. Typically 
 * when the caller page must be refreshed to reflect the changes made on the popup.
 * @param func
 * @return
 */
function hidePopupCallFunction(){
	hidePopup();
	if (logonPopupHideCallback){
		logonPopupHideCallback();
	}
}

function isPopupShown(){
	return logonPopup != null;
}

function addFormElements(obj, formId){

	list = Ext.query("input[type!='button'][type!='submit']", Ext.getDom(formId));
	for (i = 0; i < list.length; i++){
		if ((list[i].type == 'checkbox') || (list[i].type == 'radio')){
			if (list[i].checked){
				obj[list[i].name] = list[i].value;
			}
		}
		else { 
			obj[list[i].name] = list[i].value;
		}
	}
	list = Ext.query("select", Ext.getDom(formId));
	for (i = 0; i < list.length; i++){
		if(list[i].multiple !== false){
			var values = getSelectionValues(list[i]);
			obj[list[i].name] = values;
		}
		else{
			obj[list[i].name] = list[i].value;
		}
	}
	list = Ext.query("textarea", Ext.getDom(formId));
	for (i = 0; i < list.length; i++){
		obj[list[i].name] = list[i].value;
	}
}

function getSelectionValues(element){
	var values = [];
	var idx;
	if(element.options){
		for (idx = 0; idx < element.options.length; idx++){
		//if(typeof array[element]!='undefined'  ){
			if(element.options[idx].selected === true){
				values[values.length] = element.options[idx].value;
			}
		}
	}
	return values;
}

function appendPartErrors(name){
	pe = Ext.get("part_errors");
	e = Ext.get("errors");
	e.select("table").remove();
	e.appendChild(pe.select("table"));
	pe.remove();
}

function replaceOptions(optid, options, value, fixedvals){
	tpl =  new Ext.XTemplate(
		'<tpl for="options">',
		'<option value="{id}" <tpl if="id == parent.value">selected</tpl> >',
		'{name}',
		'</option>',
		'</tpl>');

	opt = Ext.get(optid);
	if (!Ext.isArray(options)){
		options = [];
	}
	data = {"value": value,
			"options": options};
	opt.update(fixedvals);
	tpl.append(opt, data);
}

function selectTab(tabContainer, tabName){
	list = Ext.select("a", false, Ext.getDom(tabContainer));
	list.removeClass("highlight");
	Ext.get(tabName).addClass("highlight");
}

function checkMaxLength(event, Object, MaxLen)
{
	if (event.keyCode == 8 || event.keyCode == 46 ){
		return true;
	}
	else{
		return (Object.value.length < MaxLen);
	}
}

/**
 * Removes the old field if exists on the form, and adds a new one with the given value.
 * form - id,name of the form, to which the hidden field is going to be appended
 * name - name of the field to add to the form
 * value - value of the field
**/
function storeValueAsHiddenField(form, name, value){
	// remove old settings if any
	var oldField = '#'+form+' input[name='+name+']';
	Ext.select(oldField).remove();
	  
	// construct list of tags
	var newField ={ tag: 'input',
					type: 'hidden',
					name: name,
					value: value
			};
	
	// add new field to form
	Ext.DomHelper.append(Ext.get(form), newField);
}

function crhCommonUrl(url) {
	
	if (logonBuildVersion) {
		
		return url + '?logonversion=' + logonBuildVersion;
	}
	
	return url;
}

var GMapAPILoader = {
	taskStarted :false,
	// Ext.TaskMgt task
	run : function() { // dynamically loads Google API script
		this.loadGMapAPIScript();
		setTimeout(function(){
				"use strict";
				GMapAPILoader.checkAPIStatus();
			}, 5000);
		},
	interval :0,
	repeat :1,
	// Private
	loadAdditionalScripts :false,
	onAPIReadyCallbackFn : [], // The callback function to invoke when api & page are loaded
	onAPIFailedCallbackFn :[], // The callback function to invoke when api failed to load
	loaded :false, // True if Google API is loaded, false otherwise
	failed :false, // True if attempt to load Google API failed, false otherwise
	apiLoaded : function() { // Callback invoked when Google API is loaded
		if (this.onAPIReadyCallbackFn) {
			if (this.loadAdditionalScripts) {
				this.loadReturnDurationScript();
				this.loadLogonGMapScript();
			}
			//FIXME (andras)document.body.onunload = GUnload; -- Google API v3 handles this automatically
			// The document is loaded already and the callback function was set already; invoke callback immediately
			for (var i=0;i<this.onAPIReadyCallbackFn.length;i++) {
				this.onAPIReadyCallbackFn[i]();
			}
		}
		this.loaded = true;
	},
	checkAPIStatus : function() {
		if ((!this.loaded) && (this.onAPIFailedCallbackFn)) {
			for (var i=0;i<this.onAPIFailedCallbackFn.length;i++) {
				this.onAPIFailedCallbackFn[i]();
			}
		} else {
			this.failed = true;
		}
	},
	// Public
	onAPIReady : function(callbackFn) { // Invoked in order to subscribe to the event in case the API & page are loaded
		if (this.loaded) {
			// The API & document are loaded already, invoke callback immediately
			callbackFn();
		} else {
			this.onAPIReadyCallbackFn.push(callbackFn);
		}
	},
	onAPIFailed : function(callbackFn) { // Invoked in order to subscribe to the event in case the API failed to load
		if (this.failed) {
			// The API failed already, invoke callback immediately
			callbackFn();
		} else {
			this.onAPIFailedCallbackFn.push(callbackFn);
		}
	},
	load : function(loadAdditionalScripts) {
		if (!this.taskStarted) {
			this.taskStarted = true;
			this.loadAdditionalScripts = loadAdditionalScripts;
			Ext.TaskMgr.start(GMapAPILoader);
		}
	},
	loadGMapAPIScript: function() {
		try {
			var script = document.createElement("script");
			script.src = gMapApiBaseUrl + "maps/api/js?v=3&key=" + gmapApiKey + "&callback=GMapAPILoader.apiLoaded";
			script.type = "text/javascript";
			document.getElementsByTagName("head")[0].appendChild(script);
		} catch(e) {
			Logon.message.warning("Fetching Google JS library failed!");
		}
		
		var script2 = document.createElement("script");
		script2.src = crhCommonUrl(Logon.contextPath + "/js/logon-gmap-common.js");
		script2.type = "text/javascript";
		document.getElementsByTagName("head")[0].appendChild(script2);
	},
	loadReturnDurationScript: function() {
		var script = document.createElement("script");
		script.src = crhCommonUrl("../js/returnDuration.js");
		script.type = "text/javascript";
		document.getElementsByTagName("head")[0].appendChild(script);
	},
	/*
	loadContextMenuControlScript: function() {
		var script = document.createElement("script");
		script.src = crhCommonUrl("../js/contextmenucontrol.js");
		script.type = "text/javascript";
		document.getElementsByTagName("head")[0].appendChild(script);
	},
	*/
	loadLogonGMapScript: function() {
		/* TODO gergely: under development
		var script = document.createElement("script");
		script.src = "../js/logon-gmap.js";
		script.type = "text/javascript";
		document.getElementsByTagName("head")[0].appendChild(script);
		*/
	}
}
	
// TODO eliminate the usage, use LogonUtils.disableBtn()
function disableButton(id) {
	var btn = Ext.get(id);
	if (btn != null) {
		LogonUtils.disableBtn(btn);
	}
}

// TODO eliminate the usage, use LogonUtils.enableBtn()
function enableButton(id) {
	var btn = Ext.get(id); 
	LogonUtils.enableBtn(btn);
}

//this is needed because javascript cannot add correctly 7.3 + 4.85
function roundToDecimalAndTrimEndZeros(result, dec){
	if ( typeof result == 'undefined' ) {
		return result;
	}
	if ( result == null ) {
		return result;
	}
	var fixed = result.toFixed(dec);
	if (fixed.indexOf('.') > 0) {
		while (fixed.charAt(fixed.length-1) == '0'){
			fixed = fixed.substring(0,fixed.length-1);
		}
		if(fixed.charAt(fixed.length-1) == '.'){
			fixed = fixed.substring(0,fixed.length-1);
		}
	}
	return fixed;
}

// TODO gergely: under development, will be renamed as Logon.utils 
LogonUtils = {
	disableBtn: function(btn) {
		btn.dom.disabled = true;
		if (btn.hasClass('ViewButtonStyle')) {
			btn.replaceClass('ViewButtonStyle', 'ViewButtonStyleDisabled');
		}
		else if (btn.hasClass('SmallButtonStyle')) {
			btn.replaceClass('SmallButtonStyle', 'SmallButtonStyleDisabled');
		}
	},
	enableBtn: function(btn) {
		btn.dom.disabled = false;
		if (btn.hasClass('ViewButtonStyleDisabled')) {
			btn.replaceClass('ViewButtonStyleDisabled', 'ViewButtonStyle');
		}
		else if (btn.hasClass('SmallButtonStyleDisabled')) {
			btn.replaceClass('SmallButtonStyleDisabled', 'SmallButtonStyle');
		}
	}
}

// Simple helper function to return a default value (2nd param) if the given value is empty (1st param)
function nvl(val, defVal) {
	
	if(!Ext.isEmpty(val)){
		return val;
	} else if(defVal === undefined || defVal === null){
		return '';
	} else{
		return defVal;
	}	
	
}


	var orgExtUpdaterProcessSuccess = Ext.Updater.prototype.processSuccess;
	var orgExtUpdaterProcessFailure = Ext.Updater.prototype.processFailure;  
	var orgExtUpdaterUpdate = Ext.Updater.prototype.update;
	
	Ext.override(Ext.Updater, {
		intermediateMsgIntervalId: null,
		hideElement: false,
		update : function(url, params, callback, discardUrl){
			orgExtUpdaterUpdate.apply(this, arguments);
			this.hideElement = !this.el.isVisible();
			if (!this.isAutoRefreshing()) {
				if (this.intermediateMsgTimerId) {
					clearInterval(this.intermediateMsgIntervalId);
				}
				if (Ext.Updater.defaults.intermediateMsgInterval) {
					var thisC = this;
					intermediateMsgIntervalId = setInterval(this.showIntermediateMessage.createDelegate(this), Ext.Updater.defaults.intermediateMsgInterval*1000);
				}
			}
		},
	
		showIntermediateMessage: function() {
			if (this.isUpdating()) {
				this.el.update('<div class="loading-indicator">[' + new Date().toLocaleTimeString() + '] ' + Ext.Updater.defaults.intermediateMsgText + '</div>');
				if (!this.el.isVisible()) {
					this.el.show();
				}
			}
			else {
				if (this.intermediateMsgTimerId) {
					clearInterval(this.intermediateMsgIntervalId);
					this.intermediateMsgTimerId = null;
				}
			}
		},
		
		// private
		processSuccess : function(response){
			if (this.intermediateMsgTimerId) {
				clearInterval(this.intermediateMsgIntervalId);
				this.intermediateMsgTimerId = null;
			}
			if (this.hideElement) {
				this.el.hide();
			}
			orgExtUpdaterProcessSuccess.apply(this, arguments);
		},
		
		// private
		processFailure : function(response){
			if (this.intermediateMsgTimerId) {
				clearInterval(this.intermediateMsgIntervalId);
				this.intermediateMsgTimerId = null;
			}
			orgExtUpdaterProcessFailure.apply(this, arguments);
			if (!this.isAutoRefreshing()) {
				if (this.el.dom.id == 'logonPopup') {
					hidePopup();
					Logon.message.error('The screen could not be opened. An unknown error occurred.');
				}
				else {
					this.el.dom.innerHTML = 'An unknown error occurred, please reload the page.';
					Logon.message.error('An unknown error occurred, please reload the page.');
				}
			}
		}
		
	});


Ext.override(Ext.DatePicker, {
	startDay : 1
});

Ext.override(Ext.form.Checkbox, {
	setInitialValue : function(v){
		this.suspendEvents(false);
		this.setValue(v);
		this.resumeEvents();
	}
})

Ext.override(Ext.form.DateField, {
	altFormats :  'j.n.y|j.n.Y'
});


// application-wide defaults
Ext.Updater.defaults.timeout = 120;
Ext.Updater.defaults.intermediateMsgInterval = 20;
Ext.Updater.defaults.intermediateMsgText = updaterIntermediateMsgText;

// custom add-ons for ExtJS components
Ext.override(Ext.form.ComboBox, {
	isEmptySelection: function() {
		return (Ext.isEmpty(this.getValue()) || this.getValue() == -1 || this.getValue() == '-1');
	}
});

Ext.override(Ext.form.ComboBox, {
	getSelectedRecord: function() {
		if (this.isEmptySelection()) {
			return null;
		}
		else {
			return this.store.getAt(this.store.find(this.valueField, this.getValue()));
		}
	}
});

Ext.override(Ext.form.ComboBox, {
	setSelectedIndex: function(idx) {
		if (this.store.getCount() > 0) {
			this.setValue(this.store.getAt(idx).get(this.valueField));
		}
		else {
			this.clearValue();
		}
	}
});

Ext.override(Ext.form.ComboBox, {
	preselectValue: function(preval, force) {
		if (!Ext.isEmpty(preval) && this.store.find(this.valueField, preval) != -1) {
			this.setValue(preval);
		} 
		else if (!force) {
			this.clearValue();
		} 
		else {
			this.setSelectedIndex(0);
		}
		
	}
});


// TODO gergely fix or eliminate it 
Ext.apply(Ext.form.VTypes, {
	daterange : function(val, field) {
		var date = field.parseDate(val);

		if(!date){
			return;
		}
		/*
		if (field.startDateField && (!this.dateRangeMax || (date.getTime() != this.dateRangeMax.getTime()))) {
			var start = Ext.getCmp(field.startDateField);
			start.setMaxValue(date);
			start.validate();
			this.dateRangeMax = date;
		} 
		else if (field.endDateField && (!this.dateRangeMin || (date.getTime() != this.dateRangeMin.getTime()))) {
			var end = Ext.getCmp(field.endDateField);
			end.setMinValue(date);
			end.validate();
			this.dateRangeMin = date;
		}
		*/
		/*
		 * Always return true since we're only using this vtype to set the
		 * min/max allowed values (these are tested for after the vtype test)
		 */
		return true;
	} 
});

//Checks the appended length of several field's length. Appends the fields and if it exceeds the maximum length, a warning message will be displayed
function checkMergeableCommentLength(fieldNames, message, uiFieldText, originalLength) {
	var length = parseFloat(originalLength);
	if (isNaN(length)){
		return false;
	}
	var text = "";
	for ( var i = 0; i < fieldNames.length; i++) {
		var comment = document.getElementById(fieldNames[i]);
		if (comment === null){
			continue;
		}
		// extra comment added with a newline special character
		if (comment.value && i > 0) {
			length--;
		}
		text = text.concat(comment.value);
	}
	
	if (text.length>length ) {
		message = message.replace(/\{0\}/,uiFieldText);
		message = message.replace(/\{1\}/,originalLength);
		Logon.message.warning(message);
		return false;
	}
	return true;
}

//Checks the field's length, and shows a confirmation message  with parameter message in case it exceeds length. Returns the value of confirmation.
function checkCommentLength(fieldName, message, uiFieldText, length){
	message = message.replace(/\{0\}/,uiFieldText);
	message = message.replace(/\{1\}/,length);
	var floatLength = parseFloat(length);
	if (isNaN(floatLength)){
		return false;
	}
	var comment = document.getElementById(fieldName);
	if (comment === null){
		return true;
	}
	if (comment.value.length > floatLength) {
		var bool = confirm(message);
		if(bool){
			comment.value = comment.value.substring(0,floatLength);
			return true;
		}
		else{
			return false;
		}
	}
	return true;
}

function transformSelectToCombobox(customConfig) {
	var domElem = Ext.getDom(customConfig.transform);
	var config = {
		hiddenId: domElem.id,
		hiddenName: domElem.name,
		allowBlank: false,
		editable: false,
		triggerAction: 'all',
		width: 100,
		listWidth: 240,
		forceSelection:true
	}
	Ext.apply(config, customConfig);
	return (new Ext.form.ComboBox(config));
}

function encodeQuotes(val) {
	return val.replace(/'/g, "&acute;");
}

//show a popup dialog named as 'title', showing a label with name 'field' and an input field
//doOnSave function is going to be called in case Save button had been clicked returning the 
//input field's value
// __title______________________________________________________________
//|																		|
//|addtext																|
//|																		|
//|	field: input:inputDialogText										|
//|																		|
//|																		|
//|				button:texts.CancelText		button: texts.SaveText		|
//_____________________________________________________________________

function showCommonReasonDialog(texts,  doOnSave, doOnCancel, inputFieldProperties ){
	if (typeof inputFieldProperties == 'undefined') {
		inputFieldProperties = {
				showInputField:true,
				required:true
		}
	}
	var dialog = '<form action="#" id="CancelForm">' +
	'<table class="classicblue" style="border:none; width:auto" class="big_form2">' +
	'<tr><th>' +
	texts.title +
	'</th></tr>' +
	'<tr> <td class="popupcontent">' + ( texts.addText!= null ? texts.addText : '') +
		'<table width="100%" class="big_form2" >' +
		(inputFieldProperties.showInputField ?
			'<tr> <td width="25%" class="label">' +
			'<nobr>' + texts.reason + ' :</nobr>' +
			'</td><td>' +
			'<input type="text" value="" name="cancelText" id="inputDialogText" maxlength="200" style="width:500px">'+
			'</td></tr>' : '') +
			'<tr><td colspan="2" class="buttons right">' + 
				'<input type="button" class="ViewButtonStyle" value="'+ texts.CancelText+'" id="inputDialogCancel"/>&nbsp;' +
				'<input ' + (inputFieldProperties.required ? 'class="ViewButtonStyleDisabled" disabled' : 'class="ViewButtonStyle"' )+ ' type="button"  value="' + texts.SaveText+ '" id="inputDialogSave"/>' +
			'</td></tr>' +
		'</table></td></tr>'+
	'</table></form>';
	var originalPopup = null;
	if (isPopupShown()){
		originalPopup = Ext.getDom('logonPopup').innerHTML;
	}
	showPopupHtml(dialog);
	if (inputFieldProperties.required){
		Ext.getDom("inputDialogText").focus();
		var text = Ext.get('inputDialogText');
		text.on('keyup',function(){
			if (Ext.getDom("inputDialogText").value.length !== 0) {
				Ext.getDom("inputDialogSave").className='ViewButtonStyle';
				Ext.getDom("inputDialogSave").disabled=false;
			}
			else {
				Ext.getDom("inputDialogSave").className='ViewButtonStyleDisabled';
				Ext.getDom("inputDialogSave").disabled=true;			
			}
		})
	}
	var savebtn = Ext.get('inputDialogSave');
	Ext.get('inputDialogCancel').on('click', 
	function(){
		if (typeof doOnCancel != 'undefined') {
			doOnCancel();
		}
		else if ( originalPopup != null ) {
			showPopupHtml(originalPopup);			
		}
		else {
			hidePopup();
		}
	});
	savebtn.on('click', function(){
			var inputDialogText = inputFieldProperties.showInputField ? Ext.getDom("inputDialogText").value : null;
			if (originalPopup != null ) {
				showPopupHtml(originalPopup);	
			}
			doOnSave.createDelegate(this,[inputDialogText])();
	});
}

function showConfirmationDialog(title, text, confirmBtnText, cancelBtnText, onConfirmFn) {
	var originalBtnTextYes = Ext.MessageBox.buttonText.yes;
	var originalBtnTextNo = Ext.MessageBox.buttonText.no;
	Ext.MessageBox.buttonText.yes = confirmBtnText;
	Ext.MessageBox.buttonText.no = cancelBtnText;
	
	Ext.MessageBox.confirm(title, text, function(btnId) {
		if (btnId == 'yes') {
			onConfirmFn();
		}
		Ext.MessageBox.buttonText.yes = originalBtnTextYes;
		Ext.MessageBox.buttonText.no = originalBtnTextNo;
	});	
}

function showSecondaryPopupHtml(innerHtml, doOnCancel){
	var originalPopup = null;
	if (isPopupShown()){
		originalPopup = Ext.getDom('logonPopup').innerHTML;
	}
	showPopupHtml(innerHtml);
	Ext.get('inputDialogCancel').on('click', 
	function(){
		if (typeof doOnCancel != 'undefined') {
			doOnCancel();
		}
		else if ( originalPopup != null ) {
			showPopupHtml(originalPopup);			
		}
		else {
			hidePopup();
		}
	});
}

function isObjectEmpty(obj) {
	for (var prop in obj) {
		if (obj.hasOwnProperty(prop)) {
			return false;
		}
	}
	
	return true;
}

/**
 * Adds simple support for custom events (browser-independent).
 */
customEventHelper = {
	on: function(element, eventName, callback) {
		if (document.addEventListener) { // Firefox & IE 9 and newer
			element.addEventListener(eventName, callback, true);
		}
		else { // IE 8
			if (element === document) {
				element = document.getElementsByTagName('body')[0];
			}
			
			if (!element.customEvents) {
				element.customEvents = {
					handlers: {},
					mainHandler: function(event) {
						if (event.propertyName === 'customEventProperty') {
							var element = event.srcElement;
							var eventName = element.customEventProperty;
							var handlers = element.customEvents.handlers[eventName];
							
							for (var i = 0; i < handlers.length; i++) {
								handlers[i]();
							}
						}
					}
				};
				
				element.attachEvent("onpropertychange", element.customEvents.mainHandler);
			}
			
			if (!element.customEvents.handlers[eventName]) {
				element.customEvents.handlers[eventName] = [];
			}
			
			element.customEvents.handlers[eventName].push(callback);
		}
	},
	
	off: function(element, eventName, callback) {
		if (document.addEventListener) { // Firefox & IE 9 and newer
			element.removeEventListener(eventName, callback, true); 
		}
		else { // IE 8
			if (element === document) {
				element = document.getElementsByTagName('body')[0];
			}
			
			if (!element.customEvents) {
				return;
			}
			
			if (eventName) {
				if (element.customEvents.handlers[eventName]) {
					var handlers = element.customEvents.handlers[eventName];
					
					if (callback) {
						var index = handlers.indexOf(callback);
						
						if (index >= 0) {
							handlers.splice(index, 1);
						}
						
						if (!handlers.length) {
							delete element.customEvents.handlers[eventName];
						}
					}
					else {
						delete element.customEvents.handlers[eventName];
					}
				}
			}
			else {
				element.customEvents.handlers = {};
			}
			
			if (isObjectEmpty(element.customEvents.handlers)) {
				element.detachEvent("onpropertychange", element.customEvents.mainHandler);
				element.customEvents = undefined;
				element.customEventProperty = undefined;
			}
		}
	},
	
	one: function(element, eventName, callback) {
		var that = this;
		
		var wrappedCallback = function() {
			that.off(element, eventName, wrappedCallback);
			callback();
		}
		
		this.on(element, eventName, wrappedCallback);
	},
	
	trigger: function(element, eventName) {
		if (document.addEventListener) { // Firefox & IE 9 and newer
			var event = document.createEvent("UIEvents");
			event.initEvent(eventName, false, true);
			element.dispatchEvent(event);
		}
		else { // IE 8
			if (element === document) {
				element = document.getElementsByTagName('body')[0];
			}
			
			element.customEventProperty = eventName;
		}
	}
};

function eventFire(el, etype){
	if (el.fireEvent) {
		(el.fireEvent('on' + etype));
	} else {
		var evObj = document.createEvent('Events');
		evObj.initEvent(etype, true, false);
		el.dispatchEvent(evObj);
	}
}

function contains(array, elem) {
    var i = array.length;
    while (i--) {
       if (array[i] === elem) {
           return true;
       }
    }
    return false;
}

function substract(arr1, arr2) {
	for(var idx2 = 0; idx2 < arr2.length; idx2++) {
		for(var idx1 = 0; idx1 < arr1.length; idx1++) {
			if(arr2[idx2] == arr1[idx1]) {
				arr1.splice(idx1, 1);
			}
		}
	}
	return arr1;
}

function merge(arr1, arr2) {
	var result = [].concat(arr2);
	for(var i = 0; i < arr1.length; i++) {
		for(var ii = 0; ii < arr2.length; ii++) {
			if(arr1[i] == arr2[ii]) {
				result.push(arr1[i]);
				break;
			}
			
		}
		if(ii == arr2.length) {
			result.push(arr1[i]);
		}
	}
	return result;
}

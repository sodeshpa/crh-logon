<%@page import="com.crh.logon.business.services.reporting.ReportMetaData"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<c:set var="form" scope="page" value="${order_reporting_ReportingForm}" />

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

	var lastSelectedCarriers = [];
	var lastSelectedPools = [];

	// Delivery from-to
	var fromDat = new Ext.form.DateField({
		id: 'fromDat',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'fromDatStr',
    	width: 100
	});
	
	var toDat = new Ext.form.DateField({
		id: 'toDat',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'toDatStr',
    	width: 100
	});
	// Period selection, TODO boon 20091126: if the button with popup is going to be re-used, please make a component out of it.
	var periodSelector = new Ext.ux.PeriodSelector({
		fromDateComponent: fromDat,
		toDateComponent: toDat
	});
	periodSelector.yesterday.text = '<fmt:message key="auftragbearbeitung.texte.gestern"/>'; 
	periodSelector.today.text = '<fmt:message key="auftragbearbeitung.texte.heute"/>';
	periodSelector.tomorrow.text = '<fmt:message key="auftragbearbeitung.texte.morgen"/>';
	periodSelector.week.text = '<fmt:message key="auftragbearbeitung.texte.w"/>';
	periodSelector.month.text = '<fmt:message key="auftragbearbeitung.texte.m"/>';
	periodSelector.year.text = '<fmt:message key="auftragbearbeitung.texte.YTD"/>';

	var el = document.createElement("div");
	var periodPanel = new Ext.Panel({
		renderTo: el,
		layout: 'fit',
		border: true,
		items: [ periodSelector ]
	});

	var periodPanelLayer = new Ext.Layer();
	periodPanelLayer.appendChild(periodPanel.getEl());

	periodSelectorButton = new Ext.Button({
		text: '<img src="../images/add.gif"/>',
		template: new Ext.Template(
	            '<table border="0" cellpadding="0" cellspacing="0"><tbody><tr>',
	            '<td><em unselectable="on"><button type="{1}">{0}</button></em></td>',
	            '</tr></tbody></table>'),
		handler: function() {
			periodPanelLayer.toggle();
			if (periodPanelLayer.isVisible()) {
				periodPanelLayer.alignTo('periodSelectorTriggerId');
			}
		},
		renderTo: 'periodSelectorTriggerId'
	});

	Ext.getDoc().on("mousedown", function(e) {
		if ( (periodPanelLayer.isVisible()) && 
				(!e.within(periodPanel.getEl())) &&
				(!e.within(periodSelectorButton.getEl())) ) { 
			periodPanelLayer.hide();
		}
	});
	
	// Distribution dest type
	var distributionDestCheckComboBox = new Ext.ux.CheckComboBox({
	    store: new Ext.data.JsonStore({
        	fields: [{name: 'distributionDestCd', mapping: 'distributionDestTxPK.distributionDestBs.distributionDestCd' }, {name: 'txt'}],
        	data : <c:out value="${form.json['distributionDestList']}" escapeXml="false"/>
    	}),
	    displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'distributionDestCd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'inactive',
	    selectOnFocus: true,
	    width: 140,
	    listWidth: 200
	});
	distributionDestCheckComboBox.applyToMarkup('distributionDestField');
	distributionDestCheckComboBox.selectAll();
	
	// Material package type
	var materialPackageCheckComboBox = new Ext.ux.CheckComboBox({
	    store: new Ext.data.JsonStore({
	    	fields: [{name: 'materialPackageCd', mapping: 'materialPackageTxPK.materialPackageBs.materialPackageCd'}, {name: 'txt'}],
        	data : <c:out value="${form.json['materialPackageList']}" escapeXml="false"/>
    	}),
	    displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'materialPackageCd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'inactive',
	    selectOnFocus: true,
	    width: 140,
	    listWidth: 200
	});
	materialPackageCheckComboBox.applyToMarkup('materialPackageField');
	materialPackageCheckComboBox.selectAll();
	
	// Order status
	var orderStatusCheckComboBox = new Ext.ux.CheckComboBox({
	    store: new Ext.data.JsonStore({
			fields: [
				{name: 'id', mapping: 'id'}, 
				{name: 'name', mapping: 'name'},
				{name: 'selected', mapping: 'selected'}
			],
			data: []
    	}),
	    displayField: 'name',
	    checkedField: 'selected',
	    valueField: 'id',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'all',
	    selectOnFocus: true,
	    width: 140,
	    listWidth: 200
	});
	orderStatusCheckComboBox.applyToMarkup('orderStatusField');
	
	// Language
	var langCombo = new Ext.form.ComboBox({
	    applyTo:'langField',
		selectOnFocus: true,
		editable: false,
	    typeAhead: false,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
	    	fields: [{name: 'langCd', mapping: 'langTxPK.langBs.langCd'}, {name: 'txt'}],
        	data : <c:out value="${form.json['langList']}" escapeXml="false"/> 
    	}),
	    mode: 'local',
	    width: 140,
	    listWidth: 250,
	    valueField: 'langCd',
	    displayField: 'txt'
	});
	langCombo.setValue('<%= pageContext.getRequest().getLocale().getLanguage() %>');

	var poolCheckComboBox = new Ext.ux.CheckComboBox({
		id: 'poolField',
		applyTo: 'poolField',
		store: new Ext.data.JsonStore({
			fields: [
				{name: 'id', mapping: 'id'}, 
	    		{name: 'name', mapping: 'name'},
	    		{name: 'selected', mapping: 'selected'}
			],
			data: <c:out value="${form.json['poolList']}" escapeXml="false"/>
		}),
		displayField: 'name',
		checkedField: 'selected',
		valueField: 'id',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: 'inactive',
		selectOnFocus: true,
		width: 140,
		listWidth: 200,
		markInvalid: function() {
			Logon.message.warning('<fmt:message key="dispoexport.warning.pool"/>');
		},
		validateOnSubmit: function() {
			var noneSelected = this.emptyText == this.noneLabel;
			if (noneSelected) {
				this.markInvalid();
			}
			return !noneSelected;
		}
	});
	poolCheckComboBox.selectAll();
	poolCheckComboBox.on("blur", function(combo, record){
		
		var selectedPools =  poolCheckComboBox.getCheckedValues();
		
		// If the selection was not changed
		if (selectedPools.join('') == lastSelectedPools.join('')) {
			return;
		}
		
		ReportingAjaxService.updateSelectedPoolIds(selectedPools, function(res) {
			
			carrierCheckComboBox.store.loadData(res);
			carrierCheckComboBox.initList();
			
		});
		
		lastSelectedPools = selectedPools;
		
	});

	var carrierCheckComboBox = new Ext.ux.CheckComboBox({
		id: 'carrierField',
		applyTo: 'carrierField',
		store: new Ext.data.JsonStore({
			fields: [
				{name: 'id', mapping: 'id'}, 
	    		{name: 'name', mapping: 'name'},
	    		{name: 'selected', mapping: 'selected'}
			],
			data: <c:out value="${form.json['carrierList']}" escapeXml="false"/>
		}),
		displayField: 'name',
		checkedField: 'selected',
		valueField: 'id',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: 'inactive',
		selectOnFocus: true,
		width: 140,
		listWidth: 200,
		markInvalid: function() {
			Logon.message.warning('<fmt:message key="dispoexport.warning.carrier"/>');
		},
		validateOnSubmit: function() {
			var noneSelected = this.emptyText == this.noneLabel;
			if (noneSelected) {
				this.markInvalid();
			}
			return !noneSelected;
		}
	});
	carrierCheckComboBox.selectAll();
	carrierCheckComboBox.refreshLabel();
	carrierCheckComboBox.on("blur", function(combo, record){
		
		var selectedCarriers =  carrierCheckComboBox.getCheckedValues();
		
		// If the selection was not changed
		if (selectedCarriers.join('') == lastSelectedCarriers.join('')) {
			return;
		}
		
		ReportingAjaxService.updateSelectedCarrierIds(selectedCarriers);
		
		lastSelectedCarriers = selectedCarriers;
		
	});

	// Report type
	var reportCombo = new Ext.form.ComboBox({
	    applyTo:'reportField',
		selectOnFocus: true,
		forceSelection: true,
		editable: false,
	    typeAhead: false,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: ['reportCd', 'reportName', 'propertyNames', 'previewDisabled'],
        	data : <c:out value="${form.json['reportList']}" escapeXml="false"/> 
    	}),
	    mode: 'local',
	    width: 140,
	    listWidth: 250,
	    valueField: 'reportCd',
	    displayField: 'reportName'
	});
	reportCombo.on('select', function(combo, record, index) {
		var propertyNames = record.data['propertyNames'];
		if (propertyNames.indexOf('distributionDestCdList') < 0) {
			distributionDestCheckComboBox.disable();
		}
		else {
			distributionDestCheckComboBox.enable();
		}
		if (propertyNames.indexOf('materialPackageCdList') < 0) {
			materialPackageCheckComboBox.disable();
		}
		else {
			materialPackageCheckComboBox.enable();
		}
		// For example Transplus
		if (propertyNames.indexOf('selectedOrderStatusCds') >= 0) {
			orderStatusCheckComboBox.enable();
			ReportingAjaxService.updateOrderStatusList("transplus", function(res) {
				orderStatusCheckComboBox.store.loadData(res);
				orderStatusCheckComboBox.initList();
			});
		}
		else if (propertyNames.indexOf('finishedCommercial') >= 0) {
			orderStatusCheckComboBox.enable();
			ReportingAjaxService.updateOrderStatusList("orderreportmetadata", function(res) {
				orderStatusCheckComboBox.store.loadData(res);
				orderStatusCheckComboBox.initList();
			});
		}
		else {
			orderStatusCheckComboBox.disable();
		}
		if (propertyNames.indexOf('selectedPoolIDs') < 0) {
			poolCheckComboBox.selectAll();
			poolCheckComboBox.disable();
		}
		else {
			poolCheckComboBox.enable();
		}
		if (propertyNames.indexOf('selectedCarrierIDs') < 0) {
			carrierCheckComboBox.selectAll();
			carrierCheckComboBox.disable();
		}
		else {
			carrierCheckComboBox.enable();
		}
		if (propertyNames.indexOf('langCd') < 0) {
			langCombo.disable();
		}
		else {
			langCombo.enable();
		}
		if (propertyNames.indexOf('deliveryFromDat') >= 0) {
			Ext.get('fromDatLabel').update("<fmt:message key="fields.order.delivery.from"/>");
			fromDat.setDisabled(false);
		}
		else if (propertyNames.indexOf('loadingFromDat') >= 0) {
			Ext.get('fromDatLabel').update("<fmt:message key="reporting.field.loading_from"/>");
			fromDat.setDisabled(false);
		}
		else {
			fromDat.setDisabled(true);
		}
		if (propertyNames.indexOf('deliveryToDat') >= 0) {
			Ext.get('toDatLabel').update("<fmt:message key="fields.order.delivery.to"/>");
			toDat.setDisabled(false);
			periodSelectorButton.setDisabled(false);
		}
		else if (propertyNames.indexOf('loadingToDat') >= 0) {
			Ext.get('toDatLabel').update("<fmt:message key="reporting.field.loading_to"/>");
			toDat.setDisabled(false);
			periodSelectorButton.setDisabled(false);
		}
		else {
			toDat.setDisabled(true);
			periodSelectorButton.setDisabled(true);
		}
		
		if (propertyNames.indexOf('previewDisabled') >= 0) {
			disableButton('buttonPreview');
		}
		else {
			enableButton('buttonPreview');
		}
	});
	reportCombo.setValue(reportCombo.getStore().getAt(0).get('reportCd'));
	reportCombo.fireEvent('select', reportCombo, reportCombo.getStore().getAt(0), 0);
	
	function getSearchFields() {

		return {
			reportCd: reportCombo.getValue(),
			fromDat: fromDat.getValue()?fromDat.value:undefined,
			toDat: toDat.getValue()?toDat.value:undefined,
			destinationCdList: toCommaDelimitedStr(distributionDestCheckComboBox.getCheckedValues()),
			materialPackageCdList: toCommaDelimitedStr(materialPackageCheckComboBox.getCheckedValues()),
			orderStatusCdList: toCommaDelimitedStr(orderStatusCheckComboBox.getCheckedValues()),
			langCd: langCombo.getValue(),
			poolIdList: poolCheckComboBox.getCheckedValues().join(','),
			carrierIdList: carrierCheckComboBox.getCheckedValues().join(',')
		}
	}

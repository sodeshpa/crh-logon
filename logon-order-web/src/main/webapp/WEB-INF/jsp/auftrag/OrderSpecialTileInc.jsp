<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderSpecialTileInc.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp"%>

<%-- For JS syntax highlighting in Eclipse --%>
<%@page import="com.crh.logon.order.web.forms.auftrag.OrderSpecialForm"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<c:if test="false"><script type="text/javascript"></c:if>

	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

	// --- Data record and DataStore definitions ---
	//-------  Shipping point -------//
	var shpointComboRec = Ext.data.Record.create([
	     {name: "shippingpointId"},
	     {name: "shippingpointName"},
	     {name: "railwayStationId", mapping: "railwayStation.railwayStationId" }
	]);			

	var plantComboRec = Ext.data.Record.create([
         	     	{name: "plantCd"},
         	        {name: "plantName"}
         	]);	

	//-------  Plant -------//
    var plantStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderSpecialAjaxService.getPlantList(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'plantCd', totalProperty:'totalSize'}, plantComboRec)
    });

	plantCombo = new Ext.ux.TwinCombo({
    	id: 'plantCombo',
    	hiddenName : 'plantCdHidden',
    	applyTo: 'plantCdId',
        store: plantStore,
        displayField: 'plantName',
        valueField: 'plantCd',
        pageSize: 20,
        minChars:2,
        forceSelection: false,
        width: 200,
        editable: true,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
    });
        
    var shpointStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { 
        	query = plantCombo.getValue();
			//set values if they are not set
        	if ((typeof(skip)=="undefined") || (skip == null)) {
        		skip = 0;
        		count = 20;
        	}
        	OrderSpecialAjaxService.getShippingpointList(skip, count, sort, query,  callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'shippingpointId', totalProperty:'totalSize'}, shpointComboRec)
    });
    
	shpointCombo = new Ext.ux.TwinCombo({
    	id: 'shpointCombo',
        applyTo: 'shpointId',
        store: shpointStore,
        displayField: 'shippingpointName',
        valueField: 'shippingpointId',
        pageSize: 20,
        width: 200,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
	});

    plantCombo.on('select', function(record) {
		//clear shipping point list
	    shpointCombo.clearValue();
	    shpointStore.reload();
	    Ext.getDom('plantRailwayStationCd').value = '';
     	checkTransmitEnabled();
	});

    plantCombo.setValue(Ext.getDom('plantCd').value);
    plantCombo.setRawValue(Ext.getDom('plantName').value);
    
	
    shpointCombo.on('select', function(el, record, index) {
     	Ext.getDom('plantRailwayStationCd').value = record.data.railwayStationId;
     	checkTransmitEnabled();
       OrderSpecialAjaxService.checkTimetable(Ext.getDom('osShiptoSapBpId').value, shpointCombo.getValue(), checkTimetableReturn);
	});
	
	shpointCombo.setValue(Ext.getDom('shippingpointId').value);
    shpointCombo.setRawValue(Ext.getDom('shippingpointName').value);
	

    //-------  Delivery Dist. Typ -------//
    var distDestComboRec = Ext.data.Record.create([
	     {name: "distributionDestTxPK.distributionDestBs.distributionDestCd"},
	     {name: "txt"}
	]);			

    var distDestData = <c:out value="${order_auftrag_OrderSpecialForm.jsonDistDestData}" escapeXml="false"/>;
          
    distDestCombo = new Ext.ux.TwinCombo({
    	mode: 'local',
    	id: 'distDestCombo',
        applyTo: 'distDestCdId',
        store: new Ext.data.JsonStore({
        	fields: ['distributionDestTxPK.distributionDestBs.distributionDestCd', 
        			'txt'
        			],
        	data: distDestData 
    	}),
        displayField: 'txt',
        valueField: 'distributionDestTxPK.distributionDestBs.distributionDestCd',
        width: 180,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
	});
	
	distDestCombo.preselectValue('<%= Constant.DISTRIBUTION_DEST_REPAIR %>', true);

	distDestCombo.on('select', function(record) {
		if (distDestCombo.getValue() == '<%= Constant.DISTRIBUTION_DEST_REPAIR %>') {
			document.getElementById('maintTypeDiv').style.display = "block";
		}
		else {
			document.getElementById('maintTypeDiv').style.display = "none";
		}
		delete Ext.getCmp('searchCombo').lastQuery;
		updateEmptyMaterial();
	});

    //-------  Delivery from date -------//
	deliveryFromDat = new Ext.form.DateField({
		id: 'orderSpecialDelFromDatCom',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'orderSpecialDelDatId',
    	width: 100
	});

	deliveryFromDat.on('select', checkTransmitEnabled);
	deliveryFromDat.on('change', checkTransmitEnabled);

	//-------  Carrier, since there's so few, no pool selection (yet) -------//
    var carrierComboRec = Ext.data.Record.create([
	     {name: "carrierId"},
	     {name: "displayName"}
	]);			

    var carrierStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { 
        	OrderSpecialAjaxService.getCarrierList(skip, count, sort, query,  callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'carrierId', totalProperty:'totalSize'}, carrierComboRec)
    });
    
	carrierCombo = new Ext.ux.TwinCombo({
    	id: 'carrierCombo',
    	hiddenName : 'carrierIdHidden',
        applyTo: 'carrierInputId',
        store: carrierStore,
        displayField: 'displayName',
        valueField: 'carrierId',
        pageSize: 20,
        width: 180,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
	});

	carrierCombo.setValue(Ext.getDom('carrierId').value);
	carrierCombo.setRawValue(Ext.getDom('carrierDisplayName').value);
	
    //-------  Bill To -------//
    var billtoComboRec = Ext.data.Record.create([
	     {name: "name1"},
	     {name: "sapBpId"}
	]);			

    var billtoStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { 
        	var shiptoSapBpId = Ext.getDom('osShiptoSapBpId').value;
        	var sareaId = Ext.getDom('osSareaId').value
        	OrderSpecialAjaxService.getBilltoList(skip, count, sort, query, shiptoSapBpId, sareaId, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'sapBpId', totalProperty:'totalSize'}, billtoComboRec)
    });
    
	var billtoCombo = new Ext.ux.TwinCombo({
    	id: 'billtoCombo',
        applyTo: 'billto',
        store: billtoStore,
        displayField: 'name1',
        valueField: 'sapBpId',
        pageSize: 20,
        width: 150,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true,
        clearValue: function(){
    		Ext.getDom('osBilltoSapBpId').value = "";
    		Ext.ux.TwinCombo.superclass.clearValue.call(this);
    	}
	});

	billtoCombo.on("select", function(combo, record){
			Ext.getDom('osBilltoSapBpId').value = record.data.sapBpId;
	});

	//-------  Production Plant -------//
   	var prodPlantComboRec = Ext.data.Record.create([
        	     	{name: "plantCd"},
        	        {name: "plantName"}
        			]);	
    
    var prodPlantStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderSpecialAjaxService.getPlantList(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'plantCd', totalProperty:'totalSize'}, prodPlantComboRec)
    });

	prodPlantCombo = new Ext.ux.TwinCombo({
    	id: 'prodPlantCombo',
    	hiddenName : 'prodPlantCdHidden',
    	applyTo: 'prodPlantCdId',
        store: prodPlantStore,
        displayField: 'plantName',
        valueField: 'plantCd',
        pageSize: 20,
        minChars:2,
        forceSelection: false,
        width: 120,
        editable: true,
        disabled: true,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
    });

	prodPlantCombo.on('select', function(record) {
		//clear shipping point list
	    materialCombo.clearValue();
	    materialStore.reload();
	});
	
	//-------  Production Plant -------//
   	var materialComboRec = Ext.data.Record.create([
        	     	{name: "sapMaterialId"},
        	        {name: "txt"}
        			]);	
    
    var materialStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { 
        	prodPlantCd = prodPlantCombo.getValue();
			//set values if they are not set
        	if ((typeof(skip)=="undefined") || (skip == null)) {
        		skip = 0;
        		count = 20;
        	}
        	if (typeof query == "function" && callback == undefined) {
                callback = query;
                query = "";
        	}
            OrderSpecialAjaxService.getMaterialList(skip, count, sort, query, prodPlantCd, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'sapMaterialId', totalProperty:'totalSize'}, materialComboRec)
    });

	materialCombo = new Ext.ux.TwinCombo({
    	id: 'materialCombo',
    	hiddenName : 'materialHidden',
    	applyTo: 'sapMatId',
        store: materialStore,
        displayField: 'txt',
        valueField: 'sapMaterialId',
        pageSize: 20,
        minChars:2,
        forceSelection: false,
        width: 140,
        editable: true,
        disabled: true,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
    });


	
	
	// --- Data record and DataStore definitions ---
	var damageCodeRec = Ext.data.Record.create([
	    {name: "damagecodeCd", mapping: "damagecodeCd"},
	    {name: "txt", mapping: "txt"},
	    {name: "damagecodeTxt", mapping: "damagecodeTxt"}
	]);

    var damageCodeTagStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderSpecialAjaxService.fetchDamageCodeList(skip, count, sort, query, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'damagecodeCd', totalProperty:'totalSize'}, damageCodeRec)
    });

	var template = new Ext.XTemplate(
			'<tpl for="."><div class="x-combo-list-item">{damagecodeTxt} <span class="dynlabel">({txt})</span></div></tpl>',
	{
	    compiled: true,
	    disableFormats: true
	}
	);
	
	// --- damageCode Tag Comboboxes
	damageCodeTagCombo = new Ext.ux.TwinCombo({
    	id: 'repBean.damageCode',
        applyTo: 'damageCodeDummy',
        store:  damageCodeTagStore,
        displayField: 'txt',
        valueField: 'damagecodeCd',
        pageSize: 20,
        width: 220,
        listWidth: 330,
        typeAhead: false,
        triggerAction: 'all' ,
        selectOnFocus: true,
        tpl:template
	});

	damageCodeTagCombo.on('select', function(combo, record) {
		var buttSave = document.getElementById("addDamageCdButton");
    	buttSave.disabled = false;
	});

    

    

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	
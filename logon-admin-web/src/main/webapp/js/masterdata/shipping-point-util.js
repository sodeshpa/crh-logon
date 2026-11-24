var gmap;
var plantCombo;
var spointStore;
var spointCombo;

Ext.onReady( function() {
	
	////////////////////////////////////////
	//////////Plant list////////////////////
	////////////////////////////////////////
	var plantRecordType = Ext.data.Record.create( [ {
		name :"name",
		mapping :'name',
		type :"string"
	}, {
		name :"value",
		mapping :'value',
		type :"string"
	} ]);

	var plantStore = new Ext.data.Store( {
		proxy :new Ext.data.DWRProxy(ShippingPointAjaxService.loadPlants),
		reader :new Ext.data.ObjectReader( {
			id :"plantId"
		}, plantRecordType),
		remoteSort: true
	});

	var params = [];
	params.push('plantName');
	params.push('true');
	plantStore.load({params:{query: params}});	
	
	plantCombo = new Ext.form.ComboBox( {
		store :plantStore,
		displayField :'name',
		valueField :'value',
		typeAhead :true,
		minChars :2,
		mode :'local',
		forceSelection :true,
		triggerAction :'all',
		emptyText :'Select...',
		selectOnFocus :true,
		applyTo :'plants',
		loadingText: 'Loading...'
	});

	plantCombo.on('select', function(record) {
		loadSpointCombo(record.value);
	});
	
	////////////////////////////////////////
	//////////Shipping point list///////////
	////////////////////////////////////////
	var spointRecordType = Ext.data.Record.create( [ {
		name :"name",
		mapping :'shippingpointName',
		type :"string"
	}, {
		name :"value",
		mapping :'shippingpointId',
		type :"string"
	}
	]);
	
	spointStore = new Ext.data.Store( {
		proxy :new Ext.data.DWRProxy(
				ShippingPointAjaxService.loadShippingPoints),
		reader :new Ext.data.ObjectReader( {
			id :"shippingpointId"
		}, spointRecordType),
		remoteSort: true
	});
	
	spointCombo = new Ext.form.ComboBox( {
		store :spointStore,
		displayField :'name',
		valueField :'value',
		typeAhead :true,
		minChars :2,
		mode :'local',
		forceSelection :true,
		triggerAction :'all',
		emptyText :'Select...',
		selectOnFocus :true,
		applyTo :'shippingPoints',
		disabled :true,
		loadingText: 'Loading...'
	});
	
	spointCombo.on('select', function(record) {
			loadShippingPoint(record.value);
	});
});


function loadSpointCombo(value) { 
	//clear shipping point list
	spointCombo.clearValue();
	spointCombo.setDisabled(true);
	
	var params = [];
	params.push('shippingpointId');
	params.push('true');
	params.push(value);
	params.push('true');
	spointStore.load({params: {query: params} });	
	
	spointCombo.setDisabled(false);
	
	//clear screen 
	Ext.get("ShippingPointDiv").dom.innerHTML='';
}

function loadShippingPoint(spid) {
	if (spid == -1) {
		var plantCd = plantCombo.getValue();
		var plantStore = plantCombo.getStore(); 
		var plantRecordIndex = plantStore.find('value', plantCd);
		var plantName = plantStore.getAt(plantRecordIndex).get('name');
		showPopup('ShippingPointCreatePopupEntry.do?plantCd=' + plantCd + '&plantName=' + plantName);
	}
	else {
		form = Ext.getDom('ShippingPointForm');
		p = {'select':'','spid': spid};
		addFormElements(p, form);
	
		var diva = Ext.get('ShippingPointDiv');
		diva.load( {
			url :'ShippingPoint.do',
			params: p,
			text :'Loading...',
			scripts :true
		} );
	}
}

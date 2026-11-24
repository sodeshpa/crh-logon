//this ibject here is a leftover from 1.6 candidate to be removed
dispokarte = {
		updateRunning : false,
		//public functions
		updateSapMatPrice : function (idx, priceStr) {
			Ext.getDom('priceDiv'+idx).innerHTML = priceStr;
		}
};

//Util function as for the simple time maps a map is received from the ajax service, and ext cannot deal with the map itself. Convert it to a list
function convertMapToList(map) {
	var i = 0;	
	var list=[];
	for (var prop in map) { 
		list[i]={};
		list[i].key = prop;
		list[i].value = map[prop];
		i++;
	}
	return list;
}


Ext.override(Ext.form.Checkbox, {
	setInitialValue : function(v){
		this.suspendEvents(false);
		this.setValue(v);
		this.resumeEvents();
	}
})

Ext.override(Ext.data.Store, {
	loadData : function(o, append){
		var r = this.reader.readRecords(Ext.isEmpty(this.emptyItem) ? o : [this.emptyItem].concat(o));
		this.loadRecords(r, {add: append}, true);
	}
})

function switchCustomerOrderMode(index) {
	combosettings[index].switchCustomerOrderMode();
}

function getOrderSpiTxtFromValue(){
	return function(value, rec) {
		var returnValue = value;
		if (value.orderSpiBs !== undefined && value.orderSpiBs !== null) {
			returnValue = value.orderSpiBs.orderSpiTxt;
		}
		else{
			if (returnValue === ""){
				returnValue = "-";
			}
		}
		return Ext.util.Format.htmlDecode(returnValue);
	}
}

function getOrderSpiCdFromValue(){
	return function(value, rec) {
		var returnValue = value;
		if (value.orderSpiBs !== undefined && value.orderSpiBs !== null) {
			returnValue = value.orderSpiBs.orderSpiCd;
		}
		else{
			if (returnValue === ""){
				returnValue = "-1";
			}
		}
		return Ext.util.Format.htmlDecode(returnValue);
	}
}

function initSalesAreaTabs() {
	// Custom rendering Template
	var materialComboTemplate = new Ext.XTemplate(		
		'<table class="classicblue" style="width:100%"><thead><tr>',
		'<th>' + texts.bestellung_materialcontractcombo_material+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_contract+ '</th>',			
		'<th>' + texts.bestellung_materialcontractcombo_salesarea+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_productionPlant+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_packagetype+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_quantity+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_uom+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_deliveryType+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_supplyPlant+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_billto+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_payer+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_contractMaterial+ '</th>',
		'<th>' + texts.bestellung_materialcontractcombo_basedOn+ '</th>',
		'</tr></thead><tbody>',
		'<tpl for=".">',
				'<tr class="listitem zebra{[xindex % 2]}">',
					'<td><tpl if="!this.isNotAvailable(sapMaterialId)">{txt} ({sapMaterialNo})</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(sapContractId)">{sapContractNo} ({contractItemNo}) {sapContractIncoterms}</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(sareaId)">{sareaDisplayName}</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(productionPlantCd)">{productionPlantName} ({productionPlantCd})</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(materialPackageTxt)">{materialPackageTxt}</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(quantity)">{quantity}</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(uomCd)">{uomCd}</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(deliveryTypeTxt)">{deliveryTypeTxt}</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(supplyPlantCd)">{supplyPlantName}({supplyPlantCd})</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(billtoSapBpId)">{billtoName}</tpl></td>',
					'<td><tpl if="!this.isNotAvailable(payerSapBpId)">{payerName}</tpl></td>',
					'<td>{[this.getResolutionOfMaterialText(values.resolutionType)]}<tpl if="!this.isNotAvailable(contractSapMaterialTxt)"> ({contractSapMaterialTxt})</tpl> </td>',
					'<td>{[this.getBasedOnText(values.deliverySettingType)]}</td>',
				'</tr>', 
		'</tpl>',
		'</tbody></table>',
		{
							
			// XTemplate configuration:
			compiled: true,
			disableFormats: true,
			isNotAvailable: function(val){					
				return val == null; 
			},
			getBasedOnText:function(val){
				if (val == constants.DELIVERY_SETTING_BASED_ON_CONTRACT) {
					return texts.bestellung_materialcontractcombo_deliverySettingBasedOnContract;
				}
				else if (val == constants.DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION) {
					return texts.bestellung_materialcontractcombo_deliverySettingBasedContractCombinedRestriction+ '';
				}
				else if (val == constants.DELIVERY_SETTING_BASED_ON_RESTRICTION) {
					return texts.bestellung_materialcontractcombo_deliverySettingBasedOnRestriction;
				}
				else {
					return '';
				}
			},
			getResolutionOfMaterialText:function(val){
				if (val == constants.RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL) {
					return texts.bestellung_materialcontractcombo_materialResolvedFromPricingGroup;
				}
				else if (val == constants.RESOLUTION_EXPLICIT_IN_CONTRACT) {
					return texts.bestellung_materialcontractcombo_materialExplicitelyDefined;
				}
				else if (val == constants.RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL) {
					return texts.bestellung_materialcontractcombo_materialResolvedFromDivision;
				}
				else {
					return '';
				}
			}
		}			
	);
	  
	 // This material combo contains all possible delivery settings formed from the basic sources such as materials from valid contracts
	 // materials from the sales area like services. ---
		var materialCombo = new Ext.ux.MaterialCombo({
			tpl: materialComboTemplate,
			id: 'materialComboSelectedCompositeIdentifier',
			name: 'materialComboSelectedCompositeIdentifier',
			transform:'materialComboSelectedCompositeIdentifier',
			emptyText: texts.bestellung_materialcontractcombo_choose,
			store: new Ext.data.JsonStore({
				fields: [
							'compositeIdentifier',
							'sapMaterialId', 
							'txt', 
							'productionPlantCd',
							'productionPlantByContract',
							'billtoSapBpId', 
							'payerSapBpId',
							'poolId', 
							'commercialCarrierId',
							'executingCarrierId',
							'deliveryTypeCd',
							'deliveryTypeTxt',
							'quantity', 
							'uomCd', 
							'shiptoSiloId',
							'shippingpointId',
							'loadingpointId',
							'autoPoolAssignmentFlag',
							'autoCarrierAssignmentFlag',
							'releaseSapFlag',
							'noteOrderMgmt',
							'noteDelivery',
							'noteDispatching',
							'noteTruckDriver',
							'noteRailwayDisp',
							'shiptoSareaRestrId',
							'billtoRuletypeCd',
							'sapMaterialNo',
							'freeDeliveryFlag',
							'logonPackageTypeCd',
							'materialPackageCd',
							'materialPackageTxt',
							'composite_id',
							'supplyPlantName',
							'supplyPlantCd',
							'siloName',
							'commercialCarrierName',
							'executingCarrierName',
							'billtoName',
							'payerName',
							'productionPlantName',
							'shippingpointName',
							'sareaId',
							'sapContractId',
							'sapContractItemId',
							'shiptoSareaRestrId',
							'sapContractNo',
							'contractItemNo',
							'sareaDisplayName',
							'contractSapMaterialTxt',
							'deliverySettingType',
							'resolutionType',
							'sapContractIncoterms',
							'searchByText'					
							],
				data: productList,
				sortInfo :{
				field:'txt',
				direction:'ASC'
				}  
			})
		});

		materialCombo.on('select', function(combo, record) {
			modifyRestrictions({
				'addMaterial': '',
				'isServiceOnly': nvl(serviceOnlyOrderCheckbox.getValue()),
				'materialComboSelectedCompositeIdentifier': record.data.compositeIdentifier
					});
		});
		
		// --- SERVICE ONLY ORDER ---
		serviceOnlyOrderCheckbox = new Ext.form.Checkbox({
			id: 'serviceOnlyOrderFlag',
			applyTo: 'serviceOnlyOrderFlag',
			checked: false,
			prodList: productList,
			soProdList: serviceOnlyProductList,
			handler: function(element, checked) {
				materialCombo.disable();
				materialCombo.clearValue();
				
				if (checked) {
					materialCombo.store.loadData(element.soProdList);
				}
				else {
					materialCombo.store.loadData(element.prodList);
				}
				
				materialCombo.enable();
			}
		});
		
		//----- Initialize the combos, checkboxes and also the lists. The delivery settings are formed from the ShiptoSareaRestrEntity entities.----			
		for ( var i=0; i<restrictionDeliverySettings.length; i++ ) {
			
			var restrictionDeliverySetting = restrictionDeliverySettings[i];
			
			var combosetting = createComboSetting(i, restrictionDeliverySetting);
			combosettings[i] = combosetting;			
			
			if (restrictionDeliverySetting.isLandfillMaterial) {
				combosetting.exactSdorderQtyFlagCheckbox.disable();
				combosetting.exactCustorderQtyFlagCheckbox.disable();
			}
			
			//Needed to synch the selected vehicle types with the deliverysettingdto
			combosetting.vehicleTypeCombo.on('blur', comboSettingOrderQtyNullAndUpdate(combosettings));
			
			combosetting.payerCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));			
			combosetting.billtoCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.commercialCarrierCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.executingCarrierCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.poolCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.loadingpointCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.shippingpointCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.supplyPlantCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.uomCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.productionPlantCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			
			combosetting.deliveryTypeCombo.on('select', comboSettingVehicleTypeAndUpdate(combosettings));
			
			combosetting.siloCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.mixDesignCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.serviceProcessIndicatorList.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.combinedCombo.on('change', callUpdateOnSpecificComboSetting(combosettings));
			
			//Auto pool and carrier next to the pool and carrier dropdown
			combosetting.autoPoolCheckbox.on('check', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.autoCarrierCheckbox.on('check', callUpdateOnSpecificComboSetting(combosettings));

			//Customer order flags in the quantity and uom section			
			combosetting.exactCustorderQtyFlagCheckbox.on('check', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.exactSdorderQtyFlagCheckbox.on('check', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.quantity.on('change', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.perSdorderQuantity.on('change', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.deliveryTimeTolerance.on('change', callUpdateOnSpecificComboSetting(combosettings));			
			combosetting.deliveryTimeToleranceUomCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.deliveryFreqQtyperhre.on('change', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.deliveryFreqQtyperhreUomCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.deliveryFreqEveryTime.on('change', callUpdateOnSpecificComboSetting(combosettings));
			combosetting.deliveryFreqEveryTimeUomCombo.on('select', callUpdateOnSpecificComboSetting(combosettings));
		
			combosettings[i].combinedCombo.initList();
			combosettings[i].vehicleTypeCombo.initList();
			combosettings[i].serviceProcessIndicatorList.initList();
			combosettings[i].updateComboSelections(restrictionDeliverySettings[i]);
			
		}
		
		//end for ( var i=0;i<restrictionDeliverySettings.length;i++ )
}

function callUpdateOnSpecificComboSetting(combosettings){
	return function(combo, record) {
		combosettings[combo.index].updateComboSetting();
	}
}

function comboSettingOrderQtyNullAndUpdate(combosettings){
	return function(combo, record) {
		combosettings[combo.index].perSdorderQuantity.setValue(null);
		combosettings[combo.index].updateComboSetting();
	}
}

function comboSettingVehicleTypeAndUpdate(combosettings){
	return function(combo, record) {
		combosettings[combo.index].vehicleTypeCombo.store.loadData({}, false);
		combosettings[combo.index].vehicleTypeCombo.initList();
		combosettings[combo.index].perSdorderQuantity.setValue(null);
		combosettings[combo.index].updateComboSetting();
	}
}

function createComboSetting(i, restrictionDeliverySetting){
	return {
		//PRODUCTION PLANT 
		index:i,
		productionPlantCombo: new Ext.form.ComboBox({
			index:i,	
			forceSelection:false,
			allowBlank: true,
			emptyText: texts.extjs_select,					
			mode: 'local',
			editable: false,
			id: 'productionPlantCd'+ i,
			name: 'productionPlantCd'+ i,
			transform:'productionPlantCd'+ i,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'plantCd', mapping: 'plantCd'}, 
					{name: 'plantName', mapping: 'plantName'}],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.prodPlantList,
				emptyItem: {'plant' : {'plantName': '...', 'plantCd': '-1'}}
			}),
			tpl: '<tpl for="."><div class="x-combo-list-item">{plantName} <tpl if="plantName != \'...\'"><span class="dynlabel">({plantCd})</span></tpl></div></tpl>',
			width: 110,
			listWidth: 180,
			valueField: 'plantCd',
			displayField: 'plantName'
		}),
		
		//MIX DESIGN 
		mixDesignCombo: new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,
			emptyText: texts.extjs_select,					
			mode: 'local',
			editable: false,
			disabled: restrictionDeliverySetting.isLandfillMaterial,
			id: 'mixDesignId'+ i,
			name: 'mixDesignId'+ i,
			transform:'mixDesignId'+ i,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'mixDesignId', mapping: 'mixDesignId'}, 
					{name: 'aggRmxCode', mapping: 'aggRmxCode'}, 
					{name: 'mixDesignCd', mapping:'mixDesignCd'}],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.materialMixDesignList,							
				emptyItem: {'mixDesign' : {'mixDesignCd': '...', 'aggRmxCode': '...', 'mixDesignId': '-1'}}
			}),
			tpl: '<tpl for="."><div class="x-combo-list-item">{mixDesignCd} <tpl if="aggRmxCode != \'...\'"><span class="dynlabel">({aggRmxCode})</span></tpl></div></tpl>',
			width: 110,
			listWidth: 180,
			valueField: 'mixDesignId',
			displayField: 'mixDesignCd'
		}),				
						
		//DELIVERY TYPE 
		deliveryTypeCombo: new Ext.form.ComboBox({
			emptyText: texts.extjs_select,					
			index:i,
			forceSelection:false,
			allowBlank: true,
			mode: 'local',
			editable: false,
			id: 'deliveryTypeCd'+ i,
			transform:'deliveryTypeCd'+ i,
			name: 'deliveryTypeCd'+ i,				
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'deliveryTypeCd', mapping: 'deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd'}, 
					{name: 'txt', mapping: 'txt'}],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.deliveryTypeList,						
				emptyItem: {'txt': '...', 'deliveryTypeTxPK': {'deliveryTypeBs': {'deliveryTypeCd': '-1'}}}
			}),
			width: 100,
			listWidth: 160,
			valueField: 'deliveryTypeCd',
			displayField: 'txt'
		}),
		
		//VEHICLE TYPE 
		vehicleTypeCombo: new Ext.ux.TableStyleCheckComboBox({
			index: i,
			transform: 'vehicleTypeCombo' + i,
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'truckTypeCd', mapping: 'typeCd'},
					{name: 'priority', mapping: 'priority'},
					{name: 'truckTypeName', mapping: 'typeName'},
					{name: 'maxGrossWeightT', mapping: 'maxGrossWeightT'}
				],
				data: restrictionDeliverySetting.deliveryOptionConfiguration.
						vehicleTypeWrapperList
			}),
			displayField: 'truckTypeName',
			checkedField: 'checked',
			valueField: 'truckTypeCd',
			additionalColumns: ['priority', 'maxGrossWeightT'],
			checkboxColumnTitle: texts.dispokarte_texte_trucktype_type,
			additionalColumnTitles: [
				texts.dispokarte_texte_trucktype_prio,
				texts.dispokarte_texte_trucktype_maxgrossweight
			],
			mode: 'local',
			triggerAction: 'all',
			emptyText: 'all',
			selectOnFocus: true,
			width: 100,
			listWidth: 350,
			maxHeight: 400, // LOGON-5682: Usability of truck type list
			checkboxColumnWidth: 150,
			additionalColumnWidths: [50, 150]
		}),
		
		//SILO 
		siloCombo:new Ext.form.ComboBox({
			emptyText: texts.extjs_select,					
			index:i,
			mode: 'local',
			editable: false,
			disabled: restrictionDeliverySetting.isLandfillMaterial,
			forceSelection:false,
			allowBlank: true,
			id: 'siloId' + i,
			name: 'siloId' + i,
			transform: 'siloId' + i,			
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: ['shiptoSiloId', 'siloName'],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.siloList,						
				emptyItem: {'siloName' : '...', 'shiptoSiloId': '-1'}	
			}),
			width: 100,
			listWidth: 160,
			displayField: 'siloName',
			valueField: 'shiptoSiloId'
		}),
		
		//UNIT OF MEASURE (UOM) 
		uomCombo:new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,					
			mode: 'local',
			emptyText: texts.extjs_select,					
			editable: false,
			id: 'uomCd' + i,
			transform: 'uomCd' + i,
			name: 'uomCd' + i,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'uomCd', mapping: 'uomTxPK.uomBs.uomCd'}, 
					{name: 'txt', mapping: 'txt'}],
					data:restrictionDeliverySetting.deliveryOptionConfiguration.uomList,
					emptyItem: {'txt': '...', 'uomTxPK': {'uomBs': {'uomCd': '-1'}}}
			}),		
			valueField: 'uomCd',
			displayField: 'txt',		
			width: 130
		}),
		
		//SUPPLY PLANT (DELIVERY PLANT) 
		supplyPlantCombo:new Ext.form.ComboBox({
			index:i,
			mode: 'local',
			emptyText: texts.extjs_select,					
			editable: false,
			forceSelection:false,
			allowBlank: true,			
			id: 'supplyPlantCd' + i,
			name: 'supplyPlantCd' + i,
			transform:'supplyPlantCd' + i,
			triggerAction: 'all',			
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'plantCd', mapping: 'plantCd'}, 
					{name: 'plantName', mapping: 'plantName'}, 
					{name: 'productionPlant', mapping:'productionPlant'}],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.supplyPlantList,							
				emptyItem: {'plant' : {'plantName': '...', 'plantCd': '-1'}}
			}),
			tpl: '<tpl for="."><div class="x-combo-list-item">{plantName} <tpl if="plantName != \'...\'"><span class="dynlabel">({plantCd})</span></tpl></div></tpl>',
			width: 130,
			listWidth: 180,
			valueField: 'plantCd',
			displayField: 'plantName'
		}),
		
		//SHIPPINGPOINT 
		shippingpointCombo: new Ext.form.ComboBox({
			index:i,
			emptyText: texts.extjs_select,					
			forceSelection:false,
			allowBlank: true,
			mode: 'local',
			editable: false,
			id: 'shippingpointId' + i,
			name: 'shippingpointId' + i,
			transform:'shippingpointId' + i,
			triggerAction: 'all',				
			store: new Ext.data.JsonStore({
				fields: [{name: 'shippingpointId', mapping: 'shippingpointId'}, 
						{name: 'shippingpointName', mapping: 'shippingpointName'}, 
						{name: 'coordLat', mapping: 'coordLat'}, 
						{name: 'coordLon', mapping: 'coordLon'},
						{name: 'plantCd', mapping: 'plantCd'},
						{name: 'railwayStationId', mapping: 'railwayStationId'}
						],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.shippingpoinList,								
				emptyItem: { 'shippingpointId': -1, 'shippingpointName' : '...', plant: {}, railwayStation: {}}
			}),
			width: 130,
			listWidth: 200,
			tpl: '<tpl for="."><div class="x-combo-list-item">{shippingpointName} <tpl if="shippingpointId != -1"><span class="dynlabel">(LKW<tpl if="railwayStationId != null">,BW</tpl>)</span></tpl></div></tpl>',
			valueField: 'shippingpointId',
			displayField: 'shippingpointName'
		}),
		
		//LOADINGPOINT
		loadingpointCombo:new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,
			emptyText: texts.extjs_select,					
			mode: 'local',			
			editable: false,
			id: 'loadingpointId' + i,
			name: 'loadingpointId' + i,
			transform:'loadingpointId' + i,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: ['loadingpointId', 'loadingpointName'],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.loadingpointList,						
				emptyItem: {'loadingpointName' : '...', 'loadingpointId':'-1'} 
			}),
			width: 130,
			listWidth: 200,
			valueField: 'loadingpointId',
			displayField: 'loadingpointName'
		}),
		
		//POOL 
		poolCombo: new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,				
			emptyText: texts.extjs_select,
			mode: 'local',
			editable: false,
			id: 'poolId' + i,
			name: 'poolId' + i,
			transform: 'poolId' + i,
			triggerAction: 'all',			
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'poolId', mapping: 'poolTxPK.poolId'}, 
					{name: 'name', mapping: 'name'}
				],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.poolList,						
				emptyItem: {'name': '...', 'poolTxPK' : { 'poolId': -1}}
			}),
			valueField: 'poolId',
			displayField: 'name',
			width: 130,
			listWidth: 250
		}),

		//COMMERCIAL CARRIER 
		commercialCarrierCombo :new Ext.form.ComboBox({
			index:i,
			emptyText: texts.extjs_select,					
			forceSelection:false,
			allowBlank: true,			
			mode: 'local',
			id: 'commercialCarrierId'+ i,
			name: 'commercialCarrierId'+ i,
			transform:'commercialCarrierId'+ i,
			selectOnFocus: true,			
			editable: true,
			minChars: 2,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: ['carrierId', 'displayName', 'carrierRequiresPlanningFlag'],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.commercialCarrierList,
				emptyItem: {'displayName' : '...', 'carrierId':'-1'}
			}),
			valueField: 'carrierId',
			displayField: 'displayName',
			width: 130,
			listWidth: 250
		}),

		//EXECUTING CARRIER 
		executingCarrierCombo :new Ext.form.ComboBox({
			index:i,
			emptyText: texts.extjs_select,					
			forceSelection:false,
			allowBlank: true,			
			mode: 'local',
			id: 'executingCarrierId'+ i,
			name: 'executingCarrierId'+ i,
			transform:'executingCarrierId'+ i,
			selectOnFocus: true,			
			editable: true,
			minChars: 2,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: ['carrierId', 'displayName', 'carrierRequiresPlanningFlag'],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.executingCarrierList,
				emptyItem: {'displayName' : '...', 'carrierId':'-1'}
			}),
			valueField: 'carrierId',
			displayField: 'displayName',
			width: 130,
			listWidth: 250
		}),

		//BILLTO 
		billtoCombo : new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,
			emptyText: texts.extjs_select,								
			mode: 'local',
			editable: false,
			id: 'billtoSapBpId'+ i,
			name: 'billtoSapBpId'+ i,
			transform:'billtoSapBpId' +i ,
			triggerAction: 'all',		
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'name1', mapping: 'billtoSapBp.name1'},
					{name: 'sapBpId', mapping: 'billtoSapBp.sapBpId'},
					{name: 'name2', mapping: 'billtoSapBp.name2'},
					{name: 'sapId', mapping: 'billtoSapBp.sapId'}
				],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.billtoList,						
				emptyItem: {'billtoSapBp' : {'name1' : '...', 'name2' : '', 'sapId' : '-'}}
			}),
			valueField: 'sapBpId',
			displayField: 'name1',
			tpl: '<tpl for="."><div class="x-combo-list-item">{name1} <tpl if="name2 != null">{name2}</tpl><tpl if="name1 != \'...\'"><span class="dynlabel">({sapId})</span></tpl></div></tpl>',
			width: 75,
			listWidth: 250,
			listAlign: 'tr-br'
		}),
		
		//PAYER 
		payerCombo : new Ext.form.ComboBox({
			index:i,
			emptyText: texts.extjs_select,					
			forceSelection:false,
			allowBlank: true,
			mode: 'local',
			editable: false,
			id: 'payerSapBpId'+ i,
			name: 'payerSapBpId'+ i,
			transform:'payerSapBpId'+ i,			
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{ name: 'name1', mapping: 'name1' },
					{ name: 'name2', mapping: 'name2' },
					{ name: 'sapBpId', mapping: 'sapBpId' },
					{ name: 'sapId', mapping: 'sapId' }
				],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.payerList,						
				emptyItem: {
					'name1' : '...',
					'name2' : '',
					'sapBpId' : '',
					'sapId' : ''
				}
			}),
			valueField: 'sapBpId',
			displayField: 'name1',
			tpl: new Ext.XTemplate(
					'<tpl for=".">',
						'<div class="x-combo-list-item">',
							'{name1} ',
							'<tpl if="name2 != null">{name2}</tpl> ',
							'<tpl if="name1 != \'...\'">',
								'<span class="dynlabel">({sapId})</span>',
							'</tpl>',
						'</div>',
					'</tpl>'),
			width: 100,
			listWidth: 250,
			listAlign: 'tr-br'
		}),
		
		//QUANTITY
		quantity: new Ext.form.TextField({
			index:i,
			id: 'quantity'+ i,
			allowBlank: true,
			transform: 'quantity'+ i,
			applyTo: 'quantity'+ i,
			selectOnFocus: true,
			vtype: 'decNum',			
			style: 'width: 35px; text-align: right; margin-top: 5px;'
		}),
		
		//PER SINGLE DELIVERY QUANTITY
		perSdorderQuantity: new Ext.form.TextField({
			index:i,
			id: 'perSdorderQuantity'+ i,
			allowBlank: true,
			transform: 'perSdorderQuantity'+ i,
			applyTo: 'perSdorderQuantity'+ i,
			selectOnFocus: true,
			vtype: 'decNum',			
			style: 'width: 35px; text-align: right; margin-top: 5px;'
		}),
		
		//PER SINGLE DELIVERY UOM
		perSdorderUomCd: new Ext.form.Hidden({
			index:i,
			id: 'perSdorderUomCd'+ i
		}),
		
		//PER SINGLE DELIVERY UOM LABEL
		perSdorderUomLabel: new Ext.form.Label({
			index:i,
			id: 'perSdorderUomLabel'+ i,
			applyTo: 'perSdorderUomLabel'+ i,
			style: 'text-align: left'
		}),
	
		//DELIVERY TIME TOLERANCE
		deliveryTimeTolerance: new Ext.form.TextField({
			index:i,
			id: 'deliveryTimeTolerance'+ i,
			allowBlank: true,
			transform: 'deliveryTimeTolerance'+ i,
			applyTo: 'deliveryTimeTolerance'+ i,
			selectOnFocus: true,
			vtype: 'decNum',			
			style: 'width: 35px; text-align: right; margin-top: 5px;'
		}),
		
		//DELIVERY TIME TOLERANCE UOM
		deliveryTimeToleranceUomCombo:new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,					
			mode: 'local',
			emptyText: texts.extjs_select,					
			editable: false,
			id: 'deliveryTimeToleranceUomCd' + i,
			transform: 'deliveryTimeToleranceUomCd' + i,
			name: 'deliveryTimeToleranceUomCd' + i,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'key'}, 
					{name: 'value'}],
				data:convertMapToList(restrictionDeliverySetting.deliveryOptionConfiguration.deliveryTimeToleranceUomMap),							
				emptyItem: {'key': '-1', 'value': '...'}
			}),		
			valueField: 'key',
			displayField: 'value',		
			width: 75
		}),
		
		//DELIVERY FREQUENCY QUANTITY PER HRE
		deliveryFreqQtyperhre: new Ext.form.TextField({
			index:i,
			id: 'deliveryFreqQtyperhre'+ i,
			forceSelection:false,
			allowBlank: true,
			transform: 'deliveryFreqQtyperhre'+ i,
			applyTo: 'deliveryFreqQtyperhre'+ i,
			selectOnFocus: true,
			vtype: 'decNum',			
			style: 'width: 35px; text-align: right; margin-top: 5px'
		}),
		
		//DELIVERY FREQUENCY QUANTITY PER HRE UOM
		deliveryFreqQtyperhreUomCombo:new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,					
			mode: 'local',
			emptyText: texts.extjs_select,					
			editable: false,
			id: 'deliveryFreqQtyperhreUomCd' + i,
			transform: 'deliveryFreqQtyperhreUomCd' + i,
			name: 'deliveryFreqQtyperhreUomCd' + i,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'uomCd', mapping: 'uomTxPK.uomBs.uomCd'}, 
					{name: 'txt', mapping: 'txt'}],
				data:restrictionDeliverySetting.deliveryOptionConfiguration.uomList,							
				emptyItem: {'txt': '...', 'uomTxPK': {'uomBs': {'uomCd': '-1'}}}
			}),		  
			valueField: 'uomCd',
			displayField: 'txt',		
			width: 75
		}),
		
		//DELIVERY FREQUENCY EVERY TIME 
		deliveryFreqEveryTime: new Ext.form.TextField({
			index:i,
			id: 'deliveryFreqEveryTime'+ i,
			allowBlank: true,
			transform: 'deliveryFreqEveryTime'+ i,
			applyTo: 'deliveryFreqEveryTime'+ i,
			selectOnFocus: true,
			vtype: 'decNum',			
			style: 'width: 75px; text-align: right; margin-top: 5px; margin-left: 2px'
		}),
		
		//DELIVERY FREQUENCY EVERY TIME UOM
		deliveryFreqEveryTimeUomCombo:new Ext.form.ComboBox({
			index:i,
			forceSelection:false,
			allowBlank: true,					
			mode: 'local',
			emptyText: texts.extjs_select,					
			editable: false,
			id: 'deliveryFreqEveryTimeUomCd' + i,
			transform: 'deliveryFreqEveryTimeUomCd' + i,
			name: 'deliveryFreqEveryTimeUomCd' + i,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				fields: [
					{name: 'key'}, 
					{name: 'value'}],
					data:convertMapToList(restrictionDeliverySetting.deliveryOptionConfiguration.deliveryFreqEveryTimeUomMap),							
					emptyItem: {'key': '-1', 'value': '...'}
			}),		
			valueField: 'key',
			displayField: 'value',		
			width: 75
		}),
		
		//SPECIAL ORDER INDICATORS
		combinedCombo: new Ext.ux.CheckComboBox({
			index:i,
			transform: 'checkboxCombo' + i,
			store: new Ext.data.SimpleStore({
				fields: [ 'txt', 'type', 'checked'],
				data : [ 
		[texts.dispokarte_texte_freeOfCharge, 'freeDeliveryFlag',false],
		[texts.dispokarte_texte_releaseSAP,'releaseSapFlag',false],
		[texts.dispokarte_texte_onHoldOrderAndLockedFlag,'onHoldOrderAndLockedFlag',false],
		[texts.dispokarte_texte_suppressMinChargeSapFlag,'sapSuppressMinChargeFlag',false],
		[texts.dispokarte_texte_openCustorderFlag,'openCustorderFlag',false],
		[texts.dispokarte_texte_autoReleaseFlag,'autoReleaseFlag',false]				
				]
			}),
			displayField: 'txt',
			checkedField: 'checked',
			valueField: 'type',
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',				
			emptyText:'all',
			selectOnFocus: true,
			useBulkButtons: false,
			width: 130,
			listWidth: 200,
			style: 'margin-left: 30px;'
		}),
		
		// --- SP Indicator				
		 serviceProcessIndicatorList : new Ext.form.ComboBox({
			index:i,
			mode: 'local',
			editable: false,
			forceSelection: true,
			emptyText: texts.extjs_select,
			id: 'serviceProcessIndicatorList' + i,
			name: 'serviceProcessIndicatorList' + i,
			transform:'serviceProcessIndicatorList' + i,
			allowBlank: true,
			triggerAction: 'all',
			store: new Ext.data.JsonStore({
				data : restrictionDeliverySetting.deliveryOptionConfiguration.serviceProcessIndicatorList,
				fields: [
					{name: 'name2', type: 'auto', mapping: 'orderSpiTxPK', convert: getOrderSpiTxtFromValue()},
					{name: 'serviceProcessIndicatorId', type: 'auto', mapping: 'orderSpiTxPK', convert: getOrderSpiCdFromValue()}				
				],
				emptyItem: {
					'name2' : '...&nbsp;',
					'serviceProcessIndicatorId' : '-1'
				}
			}),
			valueField: 'serviceProcessIndicatorId',
			displayField: 'name2',
			tpl: new Ext.XTemplate(
					'<tpl for=".">',
						'<div class="x-combo-list-item">',
							'{name2} ',					
						'</div>',
					'</tpl>'),
			width: 100,
			listWidth: 250,
			listAlign: 'tr-br'
		}),
		autoPoolCheckbox: new Ext.form.Checkbox({
			index:i,
			id: 'autoPool'+ i,
			applyTo: 'autoPool'+ i,
			selectOnFocus: true
		}),
		autoCarrierCheckbox: new Ext.form.Checkbox({
			index:i,
			id: 'autoCarrier'+ i,
			applyTo: 'autoCarrier'+ i
		}),
		exactSdorderQtyFlagCheckbox: new Ext.form.Checkbox({
			index:i,
			id: 'exactSdorderQtyFlag'+ i,
			applyTo: 'exactSdorderQtyFlag'+ i
		}),
		exactCustorderQtyFlagCheckbox: new Ext.form.Checkbox({
			applyTo: 'exactCustorderQtyFlag'+ i,
			index:i,
			id: 'exactCustorderQtyFlag'+ i
		}),
		availableInPickupAppFlagCheckbox: new Ext.form.Checkbox({
			applyTo: 'availableInPickupAppFlag'+ i,
			index:i,
			id: 'availableInPickupAppFlag'+ i
		}),
		availableInCustWebFlagCheckbox: new Ext.form.Checkbox({
			applyTo: 'availableInCustWebFlag'+ i,
			index:i,
			id: 'availableInCustWebFlag'+ i
		}),
		reloadCombos: function(deliverysetting) {
			
			var deliveryOptionConfiguration = deliverysetting.deliveryOptionConfiguration;
			this.payerCombo.store.loadData(deliveryOptionConfiguration.payerList);
			this.billtoCombo.store.loadData(deliveryOptionConfiguration.billtoList);
			this.commercialCarrierCombo.store.loadData(deliveryOptionConfiguration.commercialCarrierList);
			this.executingCarrierCombo.store.loadData(deliveryOptionConfiguration.executingCarrierList);
			this.poolCombo.store.loadData(deliveryOptionConfiguration.poolList);
			this.loadingpointCombo.store.loadData(deliveryOptionConfiguration.loadingpointList);
			this.shippingpointCombo .store.loadData(deliveryOptionConfiguration.shippingpoinList);
			this.supplyPlantCombo.store.loadData(deliveryOptionConfiguration.supplyPlantList);
			this.uomCombo.store.loadData(deliveryOptionConfiguration.uomList);
			this.productionPlantCombo.store.loadData(deliveryOptionConfiguration.prodPlantList);
			this.deliveryTypeCombo.store.loadData(deliveryOptionConfiguration.deliveryTypeList);
			this.vehicleTypeCombo.store.loadData(deliveryOptionConfiguration.vehicleTypeWrapperList, false);
			this.vehicleTypeCombo.initList();
			this.siloCombo.store.loadData(deliveryOptionConfiguration.siloList);
			this.mixDesignCombo.store.loadData(deliveryOptionConfiguration.materialMixDesignList);
			
			this.deliveryTimeToleranceUomCombo.store.loadData(convertMapToList(deliveryOptionConfiguration.deliveryTimeToleranceUomMap));
			this.deliveryFreqQtyperhreUomCombo.store.loadData(deliveryOptionConfiguration.uomList);
			this.deliveryFreqEveryTimeUomCombo.store.loadData(convertMapToList(deliveryOptionConfiguration.deliveryFreqEveryTimeUomMap));		
			this.serviceProcessIndicatorList.store.loadData(deliveryOptionConfiguration.serviceProcessIndicatorList);

			
			this.updateComboSelections(deliverysetting);
			
			// collapse all dropdowns
			// not the best solution (couldn't find better), as the reload is async -- must be after realod to make sure that the dropdown is collapsed 
			this.shippingpointCombo.collapse();
			this.payerCombo.collapse();
			this.billtoCombo.collapse();
			this.commercialCarrierCombo.collapse();
			this.executingCarrierCombo.collapse();
			this.poolCombo.collapse();
			this.loadingpointCombo.collapse();
			this.supplyPlantCombo.collapse();
			this.uomCombo.collapse();
			this.productionPlantCombo.collapse();
			this.deliveryTypeCombo.collapse();
			this.vehicleTypeCombo.collapse();
			this.siloCombo.collapse();
			this.mixDesignCombo.collapse();					
			
			this.deliveryTimeToleranceUomCombo.collapse();
			this.deliveryFreqQtyperhreUomCombo.collapse();
			this.deliveryFreqEveryTimeUomCombo.collapse();						
			
			this.serviceProcessIndicatorList.collapse();					
		},
		handleEnableCombos: function(deliverysetting){
			/**
			 * Disable changing payer on dispocard if					 
			 * auto payer-determination flags are ON and
			 * payer is already empty (no need for correction)
			 */					
			if (deliverysetting.payerIgnored && !deliverysetting.payerName) {
				this.payerCombo.disable();
			}
			else {
				this.payerCombo.enable();						
			}
			
			/**
			 * Disable and uncheck availableInPickupAppFlagCheckboxif restrictions 
			 * are not valid for the Terminap Applicationb Order.
			 * 
			 */
			if (deliverysetting.availableInPickupAppFlagEnabled) {
				this.availableInPickupAppFlagCheckbox.enable();
			}
			else {
				this.availableInPickupAppFlagCheckbox.disable();
			}
			
			/**
			 * Disable and uncheck availableInCustWebFlagCheckbox if restrictions 
			 * are not valid for the Web Order.
			 * 
			 */
			if (deliverysetting.availableInCustWebFlagEnabled) {
				this.availableInCustWebFlagCheckbox.enable();
			}
			else {
				this.availableInCustWebFlagCheckbox.setInitialValue(false);
				this.availableInCustWebFlagCheckbox.disable();
			}
			
			this.combinedCombo.setDisabled('releaseSapFlag', !deliverysetting.releaseSapFlagEditable);
			this.combinedCombo.setDisabled('freeDeliveryFlag', !deliverysetting.freeOfChargeEditable);
			this.combinedCombo.setDisabled('autoReleaseFlag', !deliverysetting.autoReleaseFlagEditable);
			this.combinedCombo.setDisabled('openCustorderFlag', !deliverysetting.openCustorderFlagEditable);
			this.combinedCombo.setDisabled('sapSuppressMinChargeFlag', !deliverysetting.sapSuppressMinChargeEditable);
			
			if (deliverysetting.productionPlantEditable){						
				this.productionPlantCombo.enable();
			}
			else {
				this.productionPlantCombo.disable();
			}
			if (deliverysetting.deliveryTypeEditable){						
				this.deliveryTypeCombo.enable();
			}
			else {
				this.deliveryTypeCombo.disable();
			}
			
			if (deliverysetting.shiptoSiloEditable && !deliverysetting.isLandfillMaterial){						
				this.siloCombo.enable();
			}
			else {
				this.siloCombo.disable();
			}					
			if ( deliverysetting.exactCustorderQtyFlagEditable && !deliverysetting.isLandfillMaterial) {
				this.exactCustorderQtyFlagCheckbox.enable();					
			}
			else {
				this.exactCustorderQtyFlagCheckbox.disable();
			}
			if (deliverysetting.supplyPlantEditable){						
				this.supplyPlantCombo.enable();
			}
			else {
				this.supplyPlantCombo.disable();
			}
			if ( deliverysetting.autoCarrierAssignmentFlagEditable ) {
				this.autoCarrierCheckbox.enable();					
			}
			else {
				this.autoCarrierCheckbox.disable();
			}
			if ( deliverysetting.autoPoolAssignmentFlagEditable ) {
				this.autoPoolCheckbox.enable();					
			}
			else {
				this.autoPoolCheckbox.disable();
			}
			
			if ( deliverysetting.carrierEditable ) {
				this.commercialCarrierCombo.enable();
				this.executingCarrierCombo.enable();
			}
			else {
				this.commercialCarrierCombo.disable();
				this.executingCarrierCombo.disable();
			}
			if ( deliverysetting.poolEditable ) {
				this.poolCombo.enable();					
			}
			else {
				this.poolCombo.disable();
			}
			if ( deliverysetting.exactSdorderQtyFlagEditable && !deliverysetting.isLandfillMaterial) {
				this.exactSdorderQtyFlagCheckbox.enable();					
			}
			else {
				this.exactSdorderQtyFlagCheckbox.disable();
			}
			if ( deliverysetting.deliveryFreqEveryTimeEditable ) {
				this.deliveryFreqEveryTime.enable();					
			}
			else {
				this.deliveryFreqEveryTime.disable();
			}
			if ( deliverysetting.deliveryFreqEveryTimeUomCdEditable ) {
				this.deliveryFreqEveryTimeUomCombo.enable();					
			}
			else {
				this.deliveryFreqEveryTimeUomCombo.disable();
			}
			if ( deliverysetting.deliveryTimeToleranceEditable ) {
				this.deliveryTimeTolerance.enable();					
			}
			else {
				this.deliveryTimeTolerance.disable();
			}
			if ( deliverysetting.deliveryTimeToleranceUomCdEditable ) {
				this.deliveryTimeToleranceUomCombo.enable();					
			}
			else {
				this.deliveryTimeToleranceUomCombo.disable();
			}
			if ( deliverysetting.perSdorderQuantityEditable ) {
				this.perSdorderQuantity.enable();					
			}
			else {
				this.perSdorderQuantity.disable();
			}
			if ( deliverysetting.deliveryFreqQtyperhreEditable ) {
				this.deliveryFreqQtyperhre.enable();					
			}
			else {
				this.deliveryFreqQtyperhre.disable();
			}
			if ( deliverysetting.deliveryFreqQtyperhreUomEditable ) {
				this.deliveryFreqQtyperhreUomCombo.enable();					
			}
			else {
				this.deliveryFreqQtyperhreUomCombo.disable();
			}
		},
		switchCustomerOrderMode: function(){
			//Switching the customer order mode depends on if either frequency per hour is selected - this means that it is a performance order
			//template, or frequency per delivery is specified. The two cannot be specified at the same time.
			if ( this.deliveryFreqQtyperhre.getValue() ) {
				this.deliveryFreqQtyperhre.setValue('');
				this.updateComboSetting();
			}
			else {
				this.deliveryFreqQtyperhre.setValue('1');
				this.updateComboSetting();						
			}
		},
		updateComboSetting: function() {
			currentIndex = this.index;
			//Construc
			var deliverySettingBasicDTO = {
				'poolId': this.poolCombo.getValue(),
				'shiptoSiloId': this.siloCombo.getValue(),
				'deliveryTypeCd': this.deliveryTypeCombo.getValue(),
				'vehicleTypeCds': this.vehicleTypeCombo.getCheckedValues(),
				'productionPlantCd': this.productionPlantCombo.getValue(),
				'uomCd': this.uomCombo.getValue(),
				'payerSapBpId': this.payerCombo.getValue(),
				'supplyPlantCd':this.supplyPlantCombo.getValue() ,
				'mixDesignId': this.mixDesignCombo.getValue(),
				'commercialCarrierId': this.commercialCarrierCombo.getValue(),
				'executingCarrierId': this.executingCarrierCombo.getValue(),
				'billtoSapBpId': this.billtoCombo.getValue(),
				'loadingpointId': this.loadingpointCombo.getValue(),
				'shippingpointId': this.shippingpointCombo.getValue(),
				
				'autoPoolAssignmentFlag': this.autoPoolCheckbox.getValue(),
				'autoCarrierAssignmentFlag': this.autoCarrierCheckbox.getValue(),
				
				'exactCustorderQtyFlag': this.exactCustorderQtyFlagCheckbox.getValue(),
				'exactSdorderQtyFlag': this.exactSdorderQtyFlagCheckbox.getValue(),
				
				'quantity': this.quantity.getValue(),
				'perSdorderQuantity': this.perSdorderQuantity.getValue(),
				'perSdorderUomCd': this.perSdorderUomCd.getValue(),
				'deliveryFreqEveryTime': this.deliveryFreqEveryTime.getValue(),
				'deliveryFreqEveryTimeUomCd': this.deliveryFreqEveryTimeUomCombo.getValue(),
				'deliveryFreqQtyperhre': this.deliveryFreqQtyperhre.getValue(),
				'deliveryFreqQtyperhreUomCd': this.deliveryFreqQtyperhreUomCombo.getValue(),
				'deliveryTimeTolerance': this.deliveryTimeTolerance.getValue(),
				'deliveryTimeToleranceUomCd': this.deliveryTimeToleranceUomCombo.getValue(),
				'serviceProcessIndicatorId': this.serviceProcessIndicatorList.getValue()
			};
			var checkedFlags = this.combinedCombo.getCheckedValuesAsParams();
			for (var prop in checkedFlags) { 
				deliverySettingBasicDTO[prop] = checkedFlags[prop];
			}
			
			OrderAjaxService.updateRestriction(this.index, deliverySettingBasicDTO, function(result) {
					combosettings[currentIndex].reloadCombos(result);
			});
		},
		updateComboSelections: function (deliverysetting) {
			this.payerCombo.reset();
			this.billtoCombo.reset();
			this.commercialCarrierCombo.reset();
			this.executingCarrierCombo.reset();
			this.poolCombo.reset();
			this.loadingpointCombo.reset();
			this.shippingpointCombo.reset();
			this.supplyPlantCombo.reset();
			this.uomCombo.reset();
			this.productionPlantCombo.reset();
			this.deliveryTypeCombo.reset();
			this.siloCombo.reset();
			this.mixDesignCombo.reset();
			
			this.serviceProcessIndicatorList.reset();
			
			this.payerCombo.preselectValue(deliverysetting.payerSapBpId,true);
			this.billtoCombo.preselectValue(deliverysetting.billtoSapBpId,true);
			this.commercialCarrierCombo.preselectValue(deliverysetting.commercialCarrierId,true);
			this.executingCarrierCombo.preselectValue(deliverysetting.executingCarrierId,true);
			this.poolCombo.preselectValue(deliverysetting.poolId,true);
			this.loadingpointCombo.preselectValue(deliverysetting.loadingpointId,true);
			this.shippingpointCombo.preselectValue(deliverysetting.shippingpointId,true);
			this.supplyPlantCombo.preselectValue(deliverysetting.supplyPlantCd,true);
			this.uomCombo.preselectValue(deliverysetting.uomCd,true);
			this.productionPlantCombo.preselectValue(deliverysetting.productionPlantCd,true);
			this.deliveryTypeCombo.preselectValue(deliverysetting.deliveryTypeCd,true);
			this.siloCombo.preselectValue(deliverysetting.shiptoSiloId,true);
			this.mixDesignCombo.preselectValue(deliverysetting.mixDesignId,true);
			
			if (deliverysetting.vehicleTypeCds && deliverysetting.vehicleTypeCds.length > 0) {
				for (var i = 0; i < deliverysetting.vehicleTypeCds.length; i++) {
					
					var recordId = deliverysetting.vehicleTypeCds[i];
					
					var idx = this.vehicleTypeCombo.store.find(this.vehicleTypeCombo.valueField, new RegExp('^' + recordId + '$'));
					var record = this.vehicleTypeCombo.store.getAt(idx);
					
					// Only trying to check if record available => JS error otherwise
					if (record) {
						this.vehicleTypeCombo.initChecked(recordId, true);
					} 
					
				}
			}
			else {
				this.vehicleTypeCombo.selectAll();
			}
			this.vehicleTypeCombo.updateList();
			
			//COMBINED COMBO CONTAINING VARIOUS FLAG
			this.combinedCombo.initChecked('freeDeliveryFlag',deliverysetting.freeDeliveryFlag);
			this.combinedCombo.initChecked('releaseSapFlag',deliverysetting.releaseSapFlag);
			this.combinedCombo.initChecked('onHoldOrderAndLockedFlag',deliverysetting.onHoldOrderAndLockedFlag);
			this.combinedCombo.initChecked('sapSuppressMinChargeFlag',deliverysetting.sapSuppressMinChargeFlag);
			this.combinedCombo.initChecked('openCustorderFlag',deliverysetting.openCustorderFlag);
			this.combinedCombo.initChecked('autoReleaseFlag',deliverysetting.autoReleaseFlag);
			
			//SP INDICATOR
			this.serviceProcessIndicatorList.preselectValue(deliverysetting.serviceProcessIndicatorId,true);
			
			//AUTO POOL AND CARRIER SEPARATE NEXT TO THE POOL AND CARRIER
			this.autoPoolCheckbox.setInitialValue(deliverysetting.autoPoolAssignmentFlag);
			this.autoCarrierCheckbox.setInitialValue(deliverysetting.autoCarrierAssignmentFlag);
			
			this.exactCustorderQtyFlagCheckbox.setInitialValue(deliverysetting.exactCustorderQtyFlag);
			this.exactSdorderQtyFlagCheckbox.setInitialValue(deliverysetting.exactSdorderQtyFlag);
			
			this.quantity.setValue(deliverysetting.quantity);					
			this.perSdorderQuantity.setValue(deliverysetting.perSdorderQuantity);
			this.perSdorderUomCd.setValue(deliverysetting.perSdorderUomCd);
			this.perSdorderUomLabel.setText(this.perSdorderUomCd.getValue());
			this.deliveryFreqEveryTime.setValue(deliverysetting.deliveryFreqEveryTime);
			this.deliveryFreqEveryTimeUomCombo.preselectValue(deliverysetting.deliveryFreqEveryTimeUomCd,true);
			this.deliveryFreqQtyperhre.setValue(deliverysetting.deliveryFreqQtyperhre);
			this.deliveryFreqQtyperhreUomCombo.preselectValue(deliverysetting.deliveryFreqQtyperhreUomCd,true);
			this.deliveryTimeTolerance.setValue(deliverysetting.deliveryTimeTolerance);
			this.deliveryTimeToleranceUomCombo.preselectValue(deliverysetting.deliveryTimeToleranceUomCd,true);
			
			this.handleEnableCombos(deliverysetting);
		}
	};
}
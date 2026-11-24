<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderSpecialShpitoSearchInc.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp"%>


<%-- For JS syntax highlighting in Eclipse --%>
<c:if test="false"><script type="text/javascript"></c:if>


	Ext.form.ComboBox.prototype.shadow = false;

	var soldtoShiptoSareaRecord = Ext.data.Record.create([
	    {name: 'compositeId', mapping: 'compositeId'},    
	    {name: 'shiptoSapBpId', mapping: 'shiptoSapBpId'},    
	    {name: 'soldtoSapBpId', mapping: 'soldtoSapBpId'},    
	    {name: 'soldtoNr', mapping: 'soldtoNr'},    
	    {name: 'soldtoName', mapping: 'soldtoName'},
	    {name: 'soldtoMarkedForDeletion', mapping: 'soldtoMarkedForDeletion'},    
	    {name: 'shiptoMarkedForDeletion', mapping: 'shiptoMarkedForDeletion'},
	    {name: 'shiptoNr', mapping: 'shiptoNr'},    
	    {name: 'shiptoName', mapping: 'shiptoName'},
	    {name: 'postalCode', mapping: 'postalCode'},
	    {name: 'city', mapping: 'city'},
	    {name: 'sareaId', mapping: 'sareaId'},
	    {name: 'sorgCd', mapping: 'sorgCd'},
	    {name: 'sareaDisplayLongname', mapping: 'sareaDisplayLongname'},
	    {name: 'dailyOrderStatusCd', mapping: 'dailyOrderStatusCd'},
	    {name: 'dailyOrderStatusTxt', mapping: 'dailyOrderStatusTxt'}
	]);
	
	var searchObj = {
		custWithoutOrderToday: function() {
			//console.log('status = ' + custWithoutOrderToday);
			return custWithoutOrderToday;
		},
		queryStr: function() {
			return Ext.getCmp('searchCombo').getValue();
		},
		soldtoShiptoSareaListStore:  new Ext.data.Store({
		    	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) {
		    		OrderSpecialAjaxService.fetchSoldtoShiptoSareaList(skip, count, sort, searchObj.queryStr(), custWithoutOrderToday, callback) 
			    }, true),
		    	reader: new Ext.data.ListRangeReader({id: 'compositeId', totalProperty: 'totalSize'}, soldtoShiptoSareaRecord),
			    remoteSort: true
		}),
		toggleDailyOrder: function() {
			disableButton('buttonNoDailyOrder');
			custWithoutOrderToday = !custWithoutOrderToday;
			Ext.getCmp('searchCombo').focus(); 
			Ext.getCmp('searchCombo').expand();
		    searchObj.soldtoShiptoSareaListStore.load({params:{start: 0, limit: 20, sort: '', query: ''}, callback: function() {
				Ext.getDom('buttonNoDailyOrder').value = custWithoutOrderToday ? '<fmt:message key="daily.order.customers.all"/>' : '<fmt:message key="daily.order.customers.without.order"/>';
				enableButton('buttonNoDailyOrder');
		    }});
		}	
	}
	

    // Custom rendering Template
    var resultTpl = new Ext.XTemplate(
    	'<table class="classic"><thead><tr>',
    	'<th><fmt:message key="fields.kunde.id"/></th>',
    	'<th><fmt:message key="fields.kunde.name"/></th>',
    	'<th><fmt:message key="fields.shipto.id"/></th>',
    	'<th><fmt:message key="fields.shipto.name"/></th>',
    	'<th><fmt:message key="fields.shipto.plz"/></th>',
    	'<th><fmt:message key="fields.shipto.ort"/></th>',
    	'<th><fmt:message key="fields.shipto.sales.area"/></th>',
    	'<tpl if="custWithoutOrderToday">',
	    	'<th><fmt:message key="fields.shipto.daily.order.status"/></th>',
    	'</tpl>',
    	'</tr></thead><tbody>',
        '<tpl for=".">',
	        '<tpl if="compositeId != \'-1\'">',
	        	'<tr class="listitem zebra{[xindex % 2]}">',
			        '<tpl if=\"soldtoMarkedForDeletion == \'0\'\">',
			        '<td>{soldtoNr}</td>',
			        '</tpl>',
			        '<tpl if=\"soldtoMarkedForDeletion == \'1\'\">',
			        '<td><strike>{soldtoNr}</strike></td>',
			        '</tpl>',
			        '<td>{soldtoName}</td>',
			        '<tpl if=\"shiptoMarkedForDeletion == \'0\'\">',
			        '<td>{shiptoNr}</td>',
			        '</tpl>',
			        '<tpl if=\"shiptoMarkedForDeletion == \'1\'\">',
			        '<td><strike>{shiptoNr}</strike></td>',
			        '</tpl>',
			        '<td>{shiptoName}</td>',
			        '<td>{postalCode}</td>',
			        '<td>{city}</td>',
			        '<td>{sareaDisplayLongname}</td>',
			    	'<tpl if="custWithoutOrderToday">',
				    	'<tpl if="dailyOrderStatusCd != \'1\'">',
					        '<td><nobr><span style="color: #999999">{dailyOrderStatusTxt}</span></nobr></td>',
				        '</tpl>',
				    	'<tpl if="dailyOrderStatusCd == 1">',
					        '<td><nobr><span style="color: #000000">{dailyOrderStatusTxt}</span></nobr></td>',
				        '</tpl>',
			        '</tpl>',
		        '</tr>', 
	        '</tpl>',
        '</tpl>',
        '</tbody></table>'
    );

    
    var searchCombo = new Ext.form.ComboBox({
    	id: 'searchCombo',
    	msgTarget: 'side',
    	validate: function() { return false },
        store: searchObj.soldtoShiptoSareaListStore,
        displayField: 'shiptoNr',
        typeAhead: false,
        loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
        maxHeight: 800,
        width: Ext.fly('searchCell').getWidth() - 10,
        listWidth: Ext.isIE ? Ext.fly('searchCell').getWidth() - 13 : Ext.fly('searchCell').getWidth() - 10,
        minChars: 1,
        pageSize: 20,
        hideTrigger: true,
        tpl: resultTpl,
        applyTo: 'search',
        triggerAction: 'all',
        itemSelector: 'tr.listitem',
        clearValue: function(){
        	Ext.getDom('osShiptoSapBpId').value = "";
        	Ext.getDom('osSoldtoSapBpId').value = "";
        	Ext.getDom('osSareaId').value = "";            
        	billtoCombo.clearValue();
        	Ext.ux.TwinCombo.superclass.clearValue.call(this);
        },
        onSelect: function(record){
            searchCombo.collapse();

        	Ext.getDom('osShiptoSapBpId').value = record.data.shiptoSapBpId;
        	Ext.getDom('osBilltoSapBpId').value = record.data.soldtoSapBpId;
        	Ext.getDom('osSoldtoSapBpId').value = record.data.soldtoSapBpId;
        	Ext.getDom('osSareaId').value = record.data.sareaId;
        	searchCombo.setValue(record.data.shiptoNr + ' ' + record.data.shiptoName + ' ' + record.data.sareaDisplayLongname);
            
            billtoCombo.setValue(record.data.soldtoSapBpId); 
        	billtoCombo.setRawValue(record.data.soldtoName); 

        	OrderSpecialAjaxService.checkTimetable(record.data.shiptoSapBpId, shpointCombo.getValue(), checkTimetableReturn);
        	var sapMaterialId = Ext.getDom('sapMaterialId').value;
        	OrderSpecialAjaxService.getMailDataForSpecOrder(record.data.shiptoSapBpId, record.data.sareaId, fillMailData);
            
        }
    });
    Ext.fly('search').setTop("0px");

    
    function checkTimetableReturn(timetableExist) {
		
    	if (!timetableExist) { 
    		Logon.message.warning('<fmt:message key="orderspecial.warning.no,timetable"/>');
        }
    }

    function fillMailData(objects) {
    	Ext.getDom('emailTo').value = "";
    	Ext.getDom('emailText').value = "";
    	if (objects[0]) {
    		shipto = objects[0];
			Ext.getDom('emailTo').value = nvl(shipto.orderConfEmailAddresses);
			Ext.getDom('emailText').value = nvl(shipto.noteDelivery) + "\n";
    	}
    	if (objects[1]) {
    		sareaRest = objects[1];
    		Ext.getDom('emailText').value += nvl(sareaRest.noteDelivery) + "\n";
    	}
		var wagonNoteMaintenance = '<c:out value="${order_auftrag_OrderSpecialForm.wagon.wagonNoteMaintenance }"/>';
    	Ext.getDom('emailText').value += "\n" + wagonNoteMaintenance + "\n";
    }
    

<%-- END $RCSfile: OrderSpecialShpitoSearchInc.jsp,v $ --%>

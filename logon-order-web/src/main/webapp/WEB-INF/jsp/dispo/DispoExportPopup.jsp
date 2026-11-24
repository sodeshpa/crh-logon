<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: DispoExportPopup.jsp,v $ --%>
<%-- This popup is shown when on the Dispatch tab one clicks on the name of a truck.--%>
<%@ page session="true"%>
<%@page import="com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity"%>


<%@ include file="../includeTLDs.jsp"%>

<%@page import="com.crh.logon.order.web.forms.dispo.DispoExportPopupForm"%>
<%@ page import="com.crh.logon.utils.time.DateTimePatterns" %>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/DispoExportAjaxService.js'/>"></script>

<script type="text/javascript">

var documentTypeCombo;
var dispoExportDateFrom;
var dispoExportDateTo;
var dispoExportCarrierCheckCombo;
var dispoExportPoolCheckCombo;

var lastSelectedCarriers = [];
var lastSelectedPools = [];

var dispoExportPopupTexts = {
    invalidDateFrom: '<fmt:message key="dispoexport.warning.date.from"/>',
    invalidCarrier: '<fmt:message key="dispoexport.warning.carrier"/>',
    invalidDateTo: '<fmt:message key="dispoexport.warning.date.to"/>'
};

function submitWithValues() {

    var selectedCarriers = dispoExportCarrierCheckCombo.getCheckedValues() ? dispoExportCarrierCheckCombo.getCheckedValues().join(',') : '';
    var selectedPools = dispoExportPoolCheckCombo.getCheckedValues() ? dispoExportPoolCheckCombo.getCheckedValues().join(',') : '';

    storeValueAsHiddenField("DispoExportPopupForm", "selectedCarrierIDs", selectedCarriers);
    storeValueAsHiddenField("DispoExportPopupForm", "selectedPoolIDs", selectedPools);

    if (!dispoExportDateFrom.validate() || !dispoExportDateTo.validate() || !dispoExportCarrierCheckCombo.validateOnSubmit()
            || !dispoExportPoolCheckCombo.validateOnSubmit()) {
        return false;
    }

    form = Ext.getDom('DispoExportPopupForm');
    form.submit();

}

Ext.onReady(function () {

    dispoExportDateFrom = new Ext.form.DateField({
        id: 'stringFromDate',
        applyTo: 'stringFromDate',
        showToday: false,
        allowBlank: false,
        format: '<%=DateTimePatterns.JS_DATE%>',
		//custom warning in case it is invalid
		markInvalid: function(){Logon.message.warning(dispoExportPopupTexts.invalidDateFrom);},
		menuListeners: Ext.applyIf({
			  select: function(m, d){
			    Ext.form.DateField.prototype.menuListeners.select.apply(this, arguments);
			    dispoExportDateTo.setMinValue(this.value);
			    if (dispoExportDateTo.disabled == true ) {
				    dispoExportDateTo.setValue(this.value);
			    }
			  }
			}, Ext.form.DateField.prototype.menuListeners)
	});
	
			
	dispoExportDateTo = new Ext.form.DateField({
		id : 'stringToDate',
		applyTo : 'stringToDate',
		showToday: false,
		allowBlank: false,
		//custom warning in case it is invalid
		markInvalid: function(){Logon.message.warning(dispoExportPopupTexts.invalidDateTo);},
		format:  '<%=DateTimePatterns.JS_DATE%>'
	});

	dispoExportCarrierCheckCombo = new Ext.ux.CheckComboBox({
		store: new Ext.data.JsonStore({
	    	fields: [
	    		{name: 'id', mapping: 'id'}, 
	    		{name: 'name', mapping: 'name'},
	    		{name: 'selected', mapping: 'selected'}
	    	],
	    	data : <c:out value="${order_dispo_DispoExportPopupForm.jsonCarrierList}" escapeXml="false"/> 
		}),
	    displayField: 'name',
	    checkedField: 'selected',
	    valueField: 'id',
	    typeAhead: true,
	  	//custom warning in case it is invalid
	    markInvalid: function(){Logon.message.warning(dispoExportPopupTexts.invalidCarrier);},
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'inactive',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200,
	    validateOnSubmit: function(){
		    var noneSelected = this.emptyText == this.noneLabel;
		    if (noneSelected) {
			    this.markInvalid();
		    }
		    return !noneSelected;
		}
	});
	dispoExportCarrierCheckCombo.applyToMarkup('selectedCarrierIDs');
	dispoExportCarrierCheckCombo.refreshLabel();
	dispoExportCarrierCheckCombo.on("blur", function(combo, record){
		
		var selectedCarriers =  dispoExportCarrierCheckCombo.getCheckedValues();
		
		// If the selection was not changed
		if (selectedCarriers.join('') == lastSelectedCarriers.join('')) {
			return;
		}
		
		DispoExportAjaxService.updateSelectedCarrierIds(selectedCarriers);
		
		lastSelectedCarriers = selectedCarriers;
		
	});
	
	dispoExportPoolCheckCombo = new Ext.ux.CheckComboBox({
		store: new Ext.data.JsonStore({
	    	fields: [
	    		{name: 'id', mapping: 'id'}, 
	    		{name: 'name', mapping: 'name'},
	    		{name: 'selected', mapping: 'selected'}
	    	],
	    	data : <c:out value="${order_dispo_DispoExportPopupForm.jsonPoolList}" escapeXml="false"/> 
		}),
	    displayField: 'name',
	    checkedField: 'selected',
	    valueField: 'id',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'inactive',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200,
	    validateOnSubmit: function(){
		    var noneSelected = this.emptyText == this.noneLabel;
		    if (noneSelected) {
			    this.markInvalid();
		    }
		    return !noneSelected;
		}
	});
	dispoExportPoolCheckCombo.applyToMarkup('selectedPoolIDs');
	dispoExportPoolCheckCombo.on("blur", function(combo, record){
		
		var selectedPools =  dispoExportPoolCheckCombo.getCheckedValues();
		
		// If the selection was not changed
		if (selectedPools.join('') == lastSelectedPools.join('')) {
			return;
		}
		
		DispoExportAjaxService.updateSelectedPoolIds(selectedPools, function(res) {
			
			dispoExportCarrierCheckCombo.store.loadData(res);
			dispoExportCarrierCheckCombo.initList();
			
		});
		
		lastSelectedPools = selectedPools;
		
	});
	
	documentTypeCombo = new Ext.form.ComboBox({
		id:'exportType',
		name: 'exportType',
	    transform:'exportType',
	    submitValue:false,
		store: new Ext.data.JsonStore({
	    	fields: [
	    		{name: 'exportType', mapping: 'exportType'}, 
	    		{name: 'exportTypeName', mapping: 'exportTypeName'} 
	    	],
	    	data:[{"exportType":"<%=DispoExportPopupForm.GENERATION_TYPE_PDF%>","exportTypeName":"<fmt:message key="dispoexport.option.pdf"/>"},{"exportType":"<%=DispoExportPopupForm.GENERATION_TYPE_EXCEL%>","exportTypeName":"<fmt:message key="dispoexport.option.excel"/>"}] 
		}),
	    displayField: 'exportTypeName',
	    valueField: 'exportType',
	    mode: 'local',
	    triggerAction: 'all',
	    hiddenName:'exportType',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200,
	    autoSelect:true
	});
	
	documentTypeCombo.setValue('<%=DispoExportPopupForm.GENERATION_TYPE_PDF%>');
	
	documentTypeCombo.on("select", function(combo, record){
			if ( record.data.exportType == '<%=DispoExportPopupForm.GENERATION_TYPE_PDF%>' ) {
				dispoExportCarrierCheckCombo.setSingleSelect(false);
				dispoExportDateTo.enable();
			}
			else {
				dispoExportCarrierCheckCombo.setSingleSelect(true);
				dispoExportDateTo.setValue(dispoExportDateFrom.getValue());
				dispoExportDateTo.disable();
			}
		}
	);

	//set the date To's mimum value according to the initial Date coming from Dispo
	dispoExportDateTo.setMinValue('<c:out value="${order_dispo_DispoExportPopupForm.stringFromDate}" escapeXml="false"/>');
});


</script> 

<html:form action="/dispo/DispoExportPopup.do"  styleId="DispoExportPopupForm">
	<table border="0" class="classicblue" style="width: 680px; height: 170px">
		<tbody>
			<tr>
				<th height="21" colspan="4"><fmt:message key="buttons.export.scheduling"/></th>
			</tr>
			<tr style="height: 30px">
				<td class="label" align="left" style="padding-left:3px; padding-top:3px">
		 			<fmt:message key="dispoexport.exporttype"/>
		 		</td>
				<td align="left">
					<html:text styleClass='textfield' style='width: 132px' styleId="exportType" property="exportType"/>
				</td>
			</tr>	
			<tr style="height: 70px">
				<td class="label" align="left" style="padding-left:3px">
		 			<fmt:message key="dispoexport.pools"/>
		 		</td>
				<td align="left" style="padding:3px">
					<input type="text" id="selectedPoolIDs" style="width: 180px;">
				</td>
				<td class="label" align="left" style="padding-left:3px">
		 			<fmt:message key="dispoexport.carriers"/>
		 		</td>
				<td align="left" style="padding:3px">
					<input type="text" id="selectedCarrierIDs" style="width: 180px;">
				</td>
			</tr>
			<tr style="height: 30px">
				<td class="label" align="left" style="padding-left:3px">
		 			<fmt:message key="dispoexport.datefrom"/>
		 		</td>
				<td align="left" style="padding:3px">
					<html:text styleClass='textfield' style='width: 132px' styleId="stringFromDate" property="stringFromDate"/>
				</td>
				<td class="label" align="left">
		 			<fmt:message key="dispoexport.dateto"/>
		 		</td>
				<td align="left">
					<html:text styleClass="textfield" style='width: 132px' styleId="stringToDate" property="stringToDate"/>
				</td>
				
			</tr>
			<tr>
				<td align="left" valign="bottom" colspan="2" class="label" style="padding-left:3px padding-bottom:3px">
		 			<input id="burronGenerate" onclick="javascript:submitWithValues()" type="button" class="ViewButtonStyle" value="<fmt:message key="dispoexport.button.generatereport"/>"/>
		 		</td>
				<td align="right" valign="bottom" colspan="2" style="padding-right:3px; padding-bottom:3px">
					<input id="burronCancel" type="button" class="ViewButtonStyle" value="<fmt:message key="dispoexport.button.close"/>" onclick="hidePopup()"/>
				</td>
			</tr>
		</tbody>
	</table>
</html:form>

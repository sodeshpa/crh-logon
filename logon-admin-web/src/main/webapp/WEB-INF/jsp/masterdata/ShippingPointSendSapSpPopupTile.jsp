<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.ShippingPointSendSapSpPopupAction" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.ShippingPointSendSapSpPopupForm" %>
<%@ page import="com.crh.logon.persistence.entities.common.ShippingpointEntity" %>

<c:set var="formBean" value="${sessionScope.admin_ShippingPointSendSapSpPopupForm.shippingPointSendSapSpBean}"/>
<c:set var="plantCd" value="${formBean.plantCd}"/>
<c:set var="shippingpointId" value="${formBean.shippingpointId}"/>

<script type="text/javascript">
	var sendSapSpPlantStore;
	var sendSapSpPlantCombo;
	var sendSapSpShippingpointStore;
	var sendSapSpShippingpointCombo;
	
	Ext.get('buttonSendSapSpSave').on('click', function() {
		var shippingPointSendSapSpPopupDiv = Ext.get('ShippingPointSendSapSpPopupDiv');
		var submitParams = {'shippingPointSendSapSpBean.plantCd': sendSapSpPlantCombo.getValue(),
				'shippingPointSendSapSpBean.shippingpointId': sendSapSpShippingpointCombo.getValue()};
		storeValueAsHiddenField('ShippingPointSendSapSpPopupForm', 'sendSapSpFormAction', '<%= ShippingPointSendSapSpPopupAction.FORWARD_SAVE %>');
		addFormElements(submitParams, "<%= ShippingPointSendSapSpPopupForm.SESSION_SHIPPING_POINT_SEND_SAP_SP_POPUP_FORM_NAME %>");
	
		shippingPointSendSapSpPopupDiv.load( {
			url: '<c:url value="ShippingPointSendSapSpPopup.do"/>',
			params: submitParams,
			text: '<fmt:message key="extjs.loading"/>',
			scripts: true
		} );
	} );
	
	Ext.get('buttonSendSapSpCancel').on('click', function() {
		hidePopup();
	} );
	
	Ext.onReady( function() {
	
		var sendSapSpPlantRecordType = Ext.data.Record.create( [ {
			name: "name"
		}, {
			name: "value"
		} ]);
	
		
		sendSapSpPlantStore = new Ext.data.Store( {
			proxy: new Ext.data.DWRProxy(ShippingPointAjaxService.loadPlants),
			reader: new Ext.data.ObjectReader( {
				id: "plantId"
			}, sendSapSpPlantRecordType),
			remoteSort: true
		});
		
		var params = new Array();
	    params.push("plantName");
	    params.push("true");
	    sendSapSpPlantStore.load( { params: {query: params} } );	
		
	    sendSapSpPlantCombo = new Ext.form.ComboBox( {
			store: sendSapSpPlantStore,
			displayField: 'name',
			valueField: 'value',
			typeAhead: true,
			minChars: 2,
			mode: 'local',
			forceSelection: true,
			triggerAction: 'all',
			emptyText: '<fmt:message key="extjs.select"/>',
			selectOnFocus: true,
			applyTo: 'sendSapSpPlantCombo',
			width: 250,
			loadingText: '<fmt:message key="extjs.loading"/>'
		});
	
	    sendSapSpPlantCombo.on('select', function(record) {
			loadSendSapSpShippingpointCombo(record.value);
		});
		
		var sendSapSpShippingpointRecordType = Ext.data.Record.create( [ {
			name: "name",
			mapping: "shippingpointName",
			type: "string"
		}, {
			name: "value",
			mapping: "shippingpointId",
			type: "string"
		} ]);
		
		sendSapSpShippingpointStore = new Ext.data.Store( {
			proxy: new Ext.data.DWRProxy(ShippingPointAjaxService.loadShippingPoints),
			reader: new Ext.data.ObjectReader( {
				id: "shippingpointId"
			}, sendSapSpShippingpointRecordType),
			remoteSort: true
		});
		
		sendSapSpShippingpointCombo = new Ext.form.ComboBox( {
			store: sendSapSpShippingpointStore,
			displayField: 'name',
			valueField: 'value',
			typeAhead: true,
			minChars: 2,
			mode: 'local',
			forceSelection: true,
			triggerAction: 'all',
			emptyText: '<fmt:message key="extjs.select"/>',
			selectOnFocus: true,
			applyTo: 'sendSapSpShippingpointCombo',
			disabled: true,
			width: 250,
			loadingText: '<fmt:message key="extjs.loading"/>'
		});
	});
	
	function loadSendSapSpShippingpointCombo(value) { 
		//clear shipping point list
		sendSapSpShippingpointCombo.clearValue();
		sendSapSpShippingpointCombo.setDisabled(true);
		
		var params = new Array();
		params.push("shippingpointId");
		params.push("true");
		params.push(value);
		params.push("false");
		sendSapSpShippingpointStore.load( { params: {query: params} } );	
		
		sendSapSpShippingpointCombo.setDisabled(false);
	}

<c:if test="${not empty plantCd}">
	var firstLoadSendSapSpPlantStore = true;

	sendSapSpPlantStore.addListener('load', function() {
		if (firstLoadSendSapSpPlantStore) {
			sendSapSpPlantCombo.setValue('<c:out value="${plantCd}" />');
			loadSendSapSpShippingpointCombo(sendSapSpPlantCombo.getValue());
			firstLoadSendSapSpPlantStore = false;
		}
	} );
</c:if>
<c:if test="${not empty shippingpointId}">
	var firstLoadSendSapSpShippingpointStore = true;

	sendSapSpShippingpointStore.addListener('load', function() {
		if (firstLoadSendSapSpShippingpointStore) {
			sendSapSpShippingpointCombo.setValue('<c:out value="${shippingpointId}" />');
			firstLoadSendSapSpShippingpointStore = false;
		}
	} );
</c:if>
</script>

<div id="ShippingPointSendSapSpPopupDiv">
	<html:form action="ShippingPointSendSapSpPopup.do" onsubmit="return false;" styleId="ShippingPointSendSapSpPopupForm">
		<html:hidden property="sendSapSpFormAction" value="" />
		<table class="classicblue" style="border: none; width: auto;">
			<tr>
				<th colspan="2">
					<fmt:message key="shippingpoint.popup.sendsapsp.title"/>
				</th>
			</tr>
			<tr>
				<td class="popupcontent">
					<table width="100%">
						<tr>
							<td class="label right">
								<fmt:message key="shippingpoint.field.plants" />:
							</td>
							<td>
								<html:text styleId="sendSapSpPlantCombo" property="sendSapSpPlantCombo"
									value="" style="width: 95%;" />
							</td>
						</tr>
						<tr>
							<td class="label right">
								<fmt:message key="shippingpoint.field.shippingpoints" />:
							</td>
							<td>
								<html:text styleId="sendSapSpShippingpointCombo" property="sendSapSpShippingpointCombo"
									value="" style="width: 95%;" />
							</td>
						</tr>
						<tr>
							<td colspan="2" class="buttons right">
								<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" id="buttonSendSapSpSave" />
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" id="buttonSendSapSpCancel" />  
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</html:form>
</div>

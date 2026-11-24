<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ReturnReasonPopupTile.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@ page import="com.crh.logon.order.web.ajax.OrderDetailAjaxService"%>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/OrderDetailAjaxService.js'/>"></script>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>

<c:set var="returnReasonPopupUrl">
    /../order/auftrag/ReturnReasonPopup.do
</c:set>

<c:set var="materialUsedForRefilling">
    <%= Constant.RETURN_MATERIAL_USAGE_REFILLING_VEHICLE_CD %>
</c:set>

<c:set var="materialUsedForMixedQua">
    <%= Constant.RETURN_MATERIAL_USAGE_MIXED_QUALITIES_CD %>
</c:set>

<c:set var="materialUsedForRubish">
    <%= Constant.RETURN_MATERIAL_USAGE_RUBISH_CD %>
</c:set>

<style>
    /* override the .x-form-cb-label CSS class used for RadioGroup. Use the common logon CSS class 'formtitle' */
    .required .x-form-cb-label {
                   font-size: 12px;
	               font-weight:normal;
	               color:#333333;
	               margin-bottom: 4px;}
</style>
<script type="text/javascript">
    
    var plantCombo = null;
    var shippingPointCombo = null;
    var returnedMaterialUsedForRadioGroup = null;
    
    function closeReturnReasonPopup() {
        
    	var p = {};
        var form = Ext.getDom('ReturnReasonPopupForm');
        addFormElements(p, form);
        
        p['returnedMaterialUsedFor'] = returnedMaterialUsedForRadioGroup.items.get(0).getGroupValue();
        p['selectedPlantCd'] = plantCombo.getValue();
        p['selectedShippingpointId'] = shippingPointCombo.getValue();

        var diva = Ext.get('logonPopup2');
        diva.load( {
            url :'<c:url value="${returnReasonPopupUrl}"/>',
            params : p,
            text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
            scripts :true
        });
    }
    
    function closePopup2() {
            
        var logonPopup2 = Ext.get('logonPopup2');
        hidePopup(logonPopup2);
    }

    Ext.onReady(function(){
        
        Ext.form.ComboBox.prototype.shadow = false;
        Ext.form.ComboBox.prototype.emptyText = "Select...";
        Ext.form.ComboBox.prototype.minChars = 1;
        Ext.form.ComboBox.prototype.allowBlank = true;
        Ext.form.ComboBox.prototype.forceSelection = true;
        Ext.menu.Menu.prototype.shadow = false;
        
        // returnedMaterialUsedFor
        returnedMaterialUsedForRadioGroup = new Ext.form.RadioGroup({
            id:'returnedMaterialUsedForRadioGroup',
            renderTo: 'returnedMaterialUsedFor',
            columns: 1,
            xtype: 'radiogroup',
            items: [
                {boxLabel: '<fmt:message key="orderdetail.popup.return.reason.materialUsedFor.txt1" />', name: 'rg-col-1', inputValue: <%= Constant.RETURN_MATERIAL_USAGE_REFILLING_VEHICLE_CD %>, itemCls: 'required'},
                {boxLabel: '<fmt:message key="orderdetail.popup.return.reason.materialUsedFor.txt2" />', name: 'rg-col-1', inputValue: <%= Constant.RETURN_MATERIAL_USAGE_MIXED_QUALITIES_CD %>, itemCls: 'required'},
                {boxLabel: '<fmt:message key="orderdetail.popup.return.reason.materialUsedFor.txt3" />', name: 'rg-col-1', inputValue: <%= Constant.RETURN_MATERIAL_USAGE_RUBISH_CD %>, itemCls: 'required'}
            ]
        });

        <c:choose>
        	<c:when test="${order_auftrag_ReturnReasonPopupForm.returnedMaterialUsedFor == materialUsedForRefilling}">
            	returnedMaterialUsedForRadioGroup.items.items[0].setValue(true);
        	</c:when>
        	<c:when test="${order_auftrag_ReturnReasonPopupForm.returnedMaterialUsedFor == materialUsedForMixedQua}">
            	returnedMaterialUsedForRadioGroup.items.items[1].setValue(true);
        	</c:when>
        	<c:when test="${order_auftrag_ReturnReasonPopupForm.returnedMaterialUsedFor == materialUsedForRubish}">
            	returnedMaterialUsedForRadioGroup.items.items[2].setValue(true);
        	</c:when>
        	<c:otherwise>
            	returnedMaterialUsedForRadioGroup.items.items[0].setValue(true);
        	</c:otherwise>
    	</c:choose>     
        
        // -- Plant --
        plantCombo = new Ext.ux.TwinCombo({
                id: 'plantCombo',
                applyTo:'plantId',
                selectOnFocus: true,
                typeAhead: true,
                triggerAction: 'all',
                store: new Ext.data.JsonStore({
                    fields: [
                            {name: 'value', mapping: 'plantCd'}, 
                            {name: 'name', mapping: 'plantName'} 
                        ],
                    data : <c:out value="${order_auftrag_ReturnReasonPopupForm.json['plantBsList']}" escapeXml="false"/>
                }),
                mode: 'local',
                width: 200,
                listWidth: 250,
                valueField: 'value',
                displayField: 'name',
                <c:if test="${not empty order_auftrag_ReturnReasonPopupForm.selectedPlantCd}">
                	value: '<c:out value="${order_auftrag_ReturnReasonPopupForm.selectedPlantCd}" escapeXml="false"/>',
            	</c:if>
                onTrigger2Click: function() {
                    shippingPointCombo.clearValue();
                    shippingPointCombo.disable();
                    this.clearValue();
                }
        });
        
        plantCombo.on('select', function(combo, record) {
			shippingPointCombo.clearValue();
			shippingPointCombo.disable();
			<c:if test="${!order_auftrag_ReturnReasonPopupForm.isServiceOnlyOrder}">
				OrderDetailAjaxService.fetchShippingPointList(record.data['value'], function(res) { 
					shippingPointCombo.store.loadData(res);
					shippingPointCombo.enable();
					shippingPointCombo.initList();
				});
			</c:if>
		});
        
		// --- Shipping point ---
        shippingPointCombo = new Ext.ux.TwinCombo({
			id: 'shippingPointCombo',
			applyTo:'shippingPointId',
			store: new Ext.data.JsonStore({
            	 fields: [
                          {name: 'value', mapping: 'shippingpointId'}, 
                          {name: 'name', mapping: 'shippingpointName'} 
                      ],
                data : <c:out value="${order_auftrag_ReturnReasonPopupForm.json['shippingPointList']}" escapeXml="false"/>  
                }),   
             displayField: 'name',
             valueField: 'value',
             typeAhead: true,
             triggerAction: 'all',
             emptyText:'inactive',
             selectOnFocus: true,
             mode: 'local',
             width: 200,
             <c:if test="${not empty order_auftrag_ReturnReasonPopupForm.selectedShippingpointId}">
                value: '<c:out value="${order_auftrag_ReturnReasonPopupForm.selectedShippingpointId}" escapeXml="false"/>',
            </c:if>
             listWidth: 250
            });
		
        <c:if test="${order_auftrag_ReturnReasonPopupForm.isServiceOnlyOrder}">
	        shippingPointCombo.clearValue();
	        shippingPointCombo.disable();
		</c:if>
		
    });
    
</script>

<c:set var="form" scope="page" value="${order_auftrag_ReturnReasonPopupForm}" />

<html:form
    action="auftrag/ReturnReasonPopup.do" styleId="ReturnReasonPopupForm"
    style="margin-top: 0px;">
<table class="classicblue" style="border:none; width:auto">
    <tr>
        <th>
            <fmt:message key="orderdetail.popup.return.reason.header" /> <c:out value="${form.returnReasonPopupTitle}" />
        </th>
    </tr>
    <tr>
        <td class="popupcontent">
            <table width="100%"> 
                <tr>
                    <td valign="bottom" class="label" style="white-space:nowrap;border:0px;">
                        <fmt:message key="orderdetail.popup.return.reason.text" />
                    </td>
                    <td valign="bottom" class="content">
                        <html:select styleId="selectedReturnReasonCd" property="selectedReturnReasonCd" style="width:200px;">
                                <html:option value="-1">-</html:option>
                                <c:forEach var="entity" items="${form.returnReasonList}">
                                    <html:option value="${entity.returnOrderReasonTxPK.returnOrderReasonBs.returnOrderReasonCd}">
                                        <c:out value="${entity.txt}" />
                                    </html:option>
                                </c:forEach>
                        </html:select>
                    </td>
                </tr>          
            </table>
        </td>
    </tr>
    <tr>
       <td>
           <table width="100%"> 
               <tr>
                    <td colspan="2">
                         <hr width="100%"/> 
                   </td>
               </tr>
               <tr>
                   <td valign="bottom" class="label" style="white-space:nowrap;border:0px;" colspan="2">
                        <fmt:message key="orderdetail.popup.return.reason.materialUsedFor" />:
                   </td>
               </tr>
               <tr>
                   <td valign="bottom" class="label" style="white-space:nowrap;border:0px;" colspan="2">
                        <div class="label" id="returnedMaterialUsedFor"></div>
                   </td>
               </tr>
               <tr>
                   <td valign="bottom" class="label" style="white-space:nowrap;border:0px;">
                        <fmt:message key="orderdetail.popup.return.reason.plant" />:
                   </td>
                   <td valign="bottom">
                        <input type="text" id="plantId" />  
                   </td>
               </tr>
               <tr>
                   <td class="label" style="white-space:nowrap;border:0px;">
                        <fmt:message key="orderdetail.popup.return.reason.shpoint" />:
                   </td>
                   <td>
                        <input type="text" id="shippingPointId" />  
                   </td>
               </tr>
           </table>
       </td>
    </tr>
    <tr>
       <td>
           <table width="100%"> 
                <tr>     
                    <td class="buttons right" colspan="2">
                        <nobr>
                        <input type="button" class="ViewButtonStyle" onClick="closePopup2()"
                            value="<fmt:message key="orderdetail.main.button.cancel"/>">
                        <input type="button" class="ViewButtonStyle" onClick="closeReturnReasonPopup()"
                            value="<fmt:message key="buttons.ok"/>">
                        </nobr>
                    </td>
                </tr>
           </table>
       </td>
    </tr>
</table>
</html:form>

<script type="text/javascript">
    Ext.get('logonPopup2').center();
    Ext.get('logonPopup2').show();
</script>

<%-- END $RCSfile: ReturnReasonPopupTile.jsp,v $ --%>

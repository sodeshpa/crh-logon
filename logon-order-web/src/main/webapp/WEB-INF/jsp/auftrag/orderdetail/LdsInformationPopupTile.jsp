<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@ page import="com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity" %>

<c:set var="ldsInformationPopupUrl">
    /../order/auftrag/LdsInformationPopup.do
</c:set>

<script type="text/javascript">
	Ext.onReady(function() {
		var attorneyDate = new Ext.form.DateField({
			id: 'attorneyStartDate',
	    	applyTo: 'attorneyStartDateStr',
	    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
	        allowBlank: true
		});
	});
	function closeLdsInformationPopup() {
	    
		var p = {};
	    var form = Ext.getDom('LdsInformationPopupForm');
	    addFormElements(p, form);
	
	    var diva = Ext.get('logonPopup');
	    diva.load( {
	        url :'<c:url value="${ldsInformationPopupUrl}"/>',
	        params : p,
	        text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
	        scripts :true
	    });
	}
</script>

<c:set var="form" scope="page" value="${order_auftrag_LdsInformationPopupForm}" />

<html:form
    action="auftrag/LdsInformationPopup.do" styleId="LdsInformationPopupForm"
    style="margin-top: 0px;">
    
    <table class="classicblue" style="border:none; width:auto">
	    <tr>
	        <th>
	            <fmt:message key="orderdetail.popup.lds.information.header" />
	        </th>
	    </tr>
	    <tr>
	       <td>
	           <table width="100%"> 
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.driver.name" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsDriverName" property="orderItemAssignmentLoad.ldsDriverName" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_DRIVER_NAME + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.driver.card.number" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsDriverCardNo" property="orderItemAssignmentLoad.ldsDriverCardNo" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_DRIVER_CARD_NO + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.driver.id" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsDriverId" property="orderItemAssignmentLoad.ldsDriverId" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_DRIVER_ID + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.attorney.name" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsAttorneyName" property="orderItemAssignmentLoad.ldsAttorneyName" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_ATTORNEY_NAME + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.attorney.start.date" />:
	                   </td>
	                   <td>
	                        <html:text styleId="attorneyStartDateStr" property="attorneyStartDateStr"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.dispatcher.name"/>:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsDispatcherName" property="orderItemAssignmentLoad.ldsDispatcherName" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_DISPATCHER_NAME + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.dispatcher.phone.number" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsDispatcherTel" property="orderItemAssignmentLoad.ldsDispatcherTel" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_DISPATCHER_TEL + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.dispatcher.id" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsDispatcherId" property="orderItemAssignmentLoad.ldsDispatcherId" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_DISPATCHER_ID + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.carrier.crm" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsCarrierCrm" property="orderItemAssignmentLoad.ldsCarrierCrm" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_CARRIER_CRM + ""%>"/> 
	                   </td>
	               </tr>
	               <tr>
	                   <td class="label" style="white-space:nowrap;border:0px;">
	                        <fmt:message key="orderdetail.popup.lds.information.seal.numbers" />:
	                   </td>
	                   <td>
	                        <html:text
							styleId="ldsSealNumbers" property="orderItemAssignmentLoad.ldsSealNumbers" maxlength="<%=OrdItemAssignmtLoadEntity.DBSIZE_LDS_SEAL_NUMBERS + ""%>"/> 
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
	                        <input type="button" class="ViewButtonStyle" onClick="hidePopup();"
	                            value="<fmt:message key="orderdetail.main.button.cancel"/>">
	                        <input type="button" class="ViewButtonStyle" onClick="closeLdsInformationPopup()"
	                            value="<fmt:message key="buttons.save"/>">
	                        </nobr>
	                    </td>
	                </tr>
	           </table>
	       </td>
	    </tr>
    </table>
</html:form>    
<script type="text/javascript">
    Ext.get('logonPopup').center();
    Ext.get('logonPopup').show();
</script>
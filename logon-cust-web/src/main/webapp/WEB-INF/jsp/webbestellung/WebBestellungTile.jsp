<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WebBestellungTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/WebOrderAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/logon-gmap.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/logonPackageHandlingHelper.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.MaterialCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript">
var editorList = null;

function submitWithAction(action){
	form = Ext.getDom('cust_WebBestellungForm');
	Ext.query("input[name=formAction]", form)[0].value = action;
	form.submit();
}

function submitWithAjax(action, triggeredAutomatically){

	document.getElementById("formAction").value=action;

	if (typeof triggeredAutomatically == 'undefined') {
		triggeredAutomatically = false;
	}

	var params = {'triggeredAutomatically': triggeredAutomatically};
	addFormElements(params, "cust_WebBestellungForm");
	var diva = Ext.get('updateDiv');
	disableSave();
	diva.load({
		url: 'WebBestellung.do', 
		params: params,
		scripts: true
	});
}

function webOrderDeleteOrder(id, orderNumber){
	if (confirm("Are you sure you want to delete order '" + orderNumber + "'?")) {
		var params = {"orderId":id};
		storeValueAsHiddenField("cust_WebBestellungForm", "orderId", id);
		submitWithAction("delete");
	}
}

logonGmap = null;

GMapAPILoader.onAPIReady(function(){

	logonGmap.loadMap();
	
});

Ext.onReady(function(){

	logonGmap = new Logon.GMap({ 
		divId: 'standortMap',
		shiptoLat: '<c:out value="${cust_WebBestellungForm.shipTo.coordLat}"/>',
		shiptoLng: '<c:out value="${cust_WebBestellungForm.shipTo.coordLon}"/>'
	});

	GMapAPILoader.load(true);
	Ext.getBody().on('unload', logonGmap.unloadMap);
});
    
      function refreshPage(){
		  Ext.get('cust_WebBestellungForm').dom.action = "WebBestellungEntry.do";
          Ext.get('cust_WebBestellungForm').dom.submit();
       }
      
      function onOrderDetailClose(){
    	  refreshPage();
    	}
</script> 

<c:set var="datePattern" value="dd/MM/yyyy" scope="request"/> 
<c:set var="timePattern" value="HH:mm" scope="request"/> 

<html:form action="webbestellung/WebBestellung.do" styleId="cust_WebBestellungForm">
<input type="hidden" name="formAction" id="formAction" value="save"/>
<input type="hidden" name="durationTime" id="durationTime" value=""/>
<input type="hidden" name="distanceMeters" id="distanceMeters" value=""/>
<input type="hidden" name="durationCorrFactor" id="durationCorrFactor" value=""/>

      
    <!-- -------------------------- -->
	<!-- Ship-to / Standort         -->
	<!-- -------------------------- -->
<table class="big_form2" style="border-collapse:collapse; min-width:2000px">
      <colgroup>
          <col width="50%">
          <col width="50%">
      </colgroup>
      
      <tr style="border-bottom: 2px solid #599ade;">
      <!-- Begin Ship-to -->
      	<td class="labelblue_bg" colspan="2"><span class="labelbluebig"><fmt:message key="bestellung.boxlabel.lieferort"/><span style="border:0px; margin-left:20px;">
					<html:select onchange="refreshPage()" styleId="compositeId" property="compositeId" styleClass="weborder-shipto-soldto-dropdown">
						<c:forEach var="entry" items="${cust_WebBestellungForm.shiptoList}">						
							<html:option value="${entry.compositeId}">
								<c:out value="${entry.shiptoName}"/>&nbsp;-&nbsp;<c:out value="${entry.soldtoName}"/>&nbsp;-&nbsp;<c:out value="${entry.street}"/>&nbsp;<c:out value="${entry.houseNumber}"/>&nbsp;<c:out value="${entry.city}"/>
							</html:option>						
						</c:forEach>
					</html:select>
      </span></span>
      <span class="labelbluebig"><fmt:message key="fields.shipto.name"/></span><span class="weborder-shiptoinfo-text"><c:out value="${cust_WebBestellungForm.selectedShipToName}"/></span>
      
        <span class="labelbluebig" style="margin-left: 100px;"><fmt:message key="bestellung.boxlabel.standort"/></span>
      	<span class="weborder-shiptoinfo-text">
      		<c:out value="${cust_WebBestellungForm.selectedShipToStreet}"/>&nbsp;<c:out value="${cust_WebBestellungForm.selectedShipToHouseNumber}"/>
      	</span>
      	<span class="weborder-shiptoinfo-text">
      		<c:out value="${cust_WebBestellungForm.selectedShiptoCountryCd}"/>&nbsp;-&nbsp;<c:out value="${cust_WebBestellungForm.selectedShiptoPostalCode}"/>&nbsp;<c:out value="${cust_WebBestellungForm.selectedShiptoCity}"/>
      	</span>
      <br></td>
      </tr>
      <tr>
        <td class="noborder">
        
        <div class="subform" style="width:100%;">
        
        <!-------------------------------------->
        <!-- Begin content Bestellbestätigung -->
        <!-------------------------------------->
        <span class="label"><fmt:message key="dispokarte.texte.bestaettigung"/></span>
			<c:set var="orderConfTooltip"><fmt:message key="dispokarte.texte.order.conf" /></c:set>
			<c:set var="loadConfTooltip"><fmt:message key="dispokarte.texte.loadorder.conf" /></c:set>
			<c:set var="delivnoteTooltip"><fmt:message key="dispokarte.texte.deliv.note" /></c:set>
			<table style="table-layout:fixed" width="100%" height="100%">
			 <colgroup>
         	<col width="25%">
            <col width="6%">
            <col width="6%">
            <col width="6%">
            <col width="47%">
          </colgroup>
				<tr>
					<td>&nbsp;</td>
					<td><fmt:message key="bestellung.order.confirm.abbrev"/></td>
					<td><fmt:message key="bestellung.load.confirm.abbrev"/></td>
					<td><fmt:message key="bestellung.delivery.note.abbrev"/></td>
					<td><fmt:message key="bestellung.order.confirm.delivery.note"/></td>
				</tr>
				<tr>
					<td><fmt:message key="dispokarte.texte.fax"/>:</td>
					<td>
						<html:checkbox property="custorder.orderConfFaxFlag" title="${orderConfTooltip}" styleId="custorder.orderConfFaxFlag" value="true" disabled="${empty cust_WebBestellungForm.custorder.orderConfFaxNumber}"/>
						<c:set target="${cust_WebBestellungForm.custorder}" property="orderConfFaxFlag" value="false"/>
					</td>
					<td>
						<html:checkbox property="custorder.loadConfFaxFlag" title="${loadConfTooltip}" styleId="custorder.loadConfFaxFlag" value="true" disabled="${empty cust_WebBestellungForm.custorder.orderConfFaxNumber}"/>
						<c:set target="${cust_WebBestellungForm.custorder}" property="loadConfFaxFlag" value="false"/>
					</td>
					<td>
						<html:checkbox property="custorder.delivnoteFaxFlag" title="${delivnoteTooltip}" styleId="custorder.delivnoteFaxFlag" value="true" disabled="${empty cust_WebBestellungForm.custorder.orderConfFaxNumber}"/>
						<c:set target="${cust_WebBestellungForm.custorder}" property="delivnoteFaxFlag" value="false"/>
					</td>
					<td width="100%">
						<html:text property="custorder.orderConfFaxNumber" styleId="custorder.orderConfFaxNumber" styleClass="textfield" style="width:100%"/>
					</td>					
				</tr>
				<tr>
					<td><fmt:message key="dispokarte.texte.mail"/>:</td>
					<td>
						<html:checkbox property="custorder.orderConfEmailFlag" title="${orderConfTooltip}" styleId="custorder.orderConfEmailFlag" value="true" disabled="${empty cust_WebBestellungForm.custorder.orderConfEmailAdresses}"/>
						<c:set target="${cust_WebBestellungForm.custorder}" property="orderConfEmailFlag" value="false"/>
					</td>
					<td>
						<html:checkbox property="custorder.loadConfEmailFlag" title="${loadConfTooltip}" styleId="custorder.loadConfEmailFlag" value="true" disabled="${empty cust_WebBestellungForm.custorder.orderConfEmailAdresses}"/>
						<c:set target="${cust_WebBestellungForm.custorder}" property="loadConfEmailFlag" value="false"/>
					</td>
					<td>
						<html:checkbox property="custorder.delivnoteEmailFlag" title="${delivnoteTooltip}" styleId="custorder.delivnoteEmailFlag" value="true" disabled="${empty cust_WebBestellungForm.custorder.orderConfEmailAdresses}"/>
						<c:set target="${cust_WebBestellungForm.custorder}" property="delivnoteEmailFlag" value="false"/>
					</td>
					<td>
						<html:text property="custorder.orderConfEmailAdresses" styleId="custorder.orderConfEmailAdresses" styleClass="textfield" style="width:100%"/>
					</td>					
				</tr>
				<tr>
					<td><fmt:message key="bestellung.paper.invoice"/>:</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>
						<html:checkbox property="custorder.delivnoteSinglePaperFlag" title="${delivnoteTooltip}" styleId="custorder.delivnoteSinglePaperFlag" value="true"/>
						<c:set target="${cust_WebBestellungForm.custorder}" property="delivnoteSinglePaperFlag" value="false"/>
					</td>
					<td>&nbsp;</td>				
				</tr>
			</table>	
            </div>
            <!-------------------------------------->
            <!-------- End Bestellbestätigung ------>
            <!-------------------------------------->
            </td>
      <!-------------------------------------->      
      <!------- Beginn Map Standort ----------->
      <!-------------------------------------->    
        <td>
	        <div id="standortMap"  class="subform" style="width:100%;height:148px;overflow:hidden"></div>
        </td>
      </tr>
</table>

<br>
<!-- -------------------------------- -->
<!-- New / Ongoing / Completed orders -->
<!-- -------------------------------- -->

<jsp:include page="ListWebOrders.jsp"/>
<div id="updateDiv">
<%-- This div is used to send the submit in case no validation error occurred--%>
</div>
</html:form>

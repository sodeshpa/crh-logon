<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: DispokarteTile.jsp,v $ --%>

<%@ page session="true" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.SareaEntity"%>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>
<%@page import="com.crh.logon.order.web.forms.dispokarte.DispokarteForm"%>
<%@page import="com.crh.logon.constants.ShiptoSalesAreaError"%>
<%@ include file="../includeTLDs.jsp" %>


<%@page import="com.crh.logon.order.taglib.DispokarteInvalidAttributeMarkerTag"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<link type="text/css" rel="stylesheet" href="<crh-common:url value="/css/dispokarte.css"/>">
		
<script type="text/javascript" src='<crh-common:url value="/js/logon-validation.js"/>'></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/OrderAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ContractDataAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.MaterialCombo.js'/>"></script>
<script type="text/javascript" src='<crh-common:url value="/js/dispokarte.js"/>'></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TableStyleCheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>

<jsp:include page="../SubTab9Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.1"/></th>
      </tr>
      <tr>
    
      <td>
<html:form action="dispokarte/DispokarteShipToEntry.do">

<table class="big_form2" style="padding: 2px; border-spacing:2px;">
  <tbody>
  	<tr>
		<td class="labelblue">
			<fmt:message key="dispokarte.texte.kunde"/>
		</td>
    	<td>
    		<c:out value="${order_dispokarte_DispokarteForm.soldTo.sapId}"/>
    	</td>
    	<c:if test="${not empty order_dispokarte_DispokarteForm.soldTo.updateBlockedByHjid}">
    		<td>
    			<img src="<crh-common:url value='/images/error.png'/>"
    				style="position:relative; left:0px; top:0px" ext:qclass="dispoqtip dispoinfoqtip" 
    				ext:qtip="<bean:message key="dispokarte.errors.sapimport" 
    				arg0="${order_dispokarte_DispokarteForm.soldTo.updateBlockedByHjid}"/>"/>
    		</td>
    	</c:if>
    	<td>
    		<b><c:out value="${order_dispokarte_DispokarteForm.soldTo.name1}"/> <c:out value="${order_dispokarte_DispokarteForm.soldTo.name2}"/></b>
    		<c:if test="${not empty order_dispokarte_DispokarteForm.soldTo.customerClassification} }">
   				(<c:out value="${order_dispokarte_DispokarteForm.soldTo.customerClassification}"/>)
   			</c:if>
    	</td>
		<td class="labelblue">
			<fmt:message key="dispokarte.texte.shipto"/>
		</td>
		<td>
    		<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.sapId}"/>
    	</td>
    	<c:if test="${not empty order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.updateBlockedByHjid}">
    		<td>
    			<img src="<crh-common:url value='/images/error.png'/>"
    				style="position:relative; left:0px; top:0px" ext:qclass="dispoqtip dispoinfoqtip" 
    				ext:qtip="<bean:message key="dispokarte.errors.sapimport" 
    				arg0="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.updateBlockedByHjid}"/>"/>
    		</td>
    	</c:if>
       	<td>   	
    		<b><c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.name1}"/> <c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.name2}"/></b>
    	</td>
    	<td>    	
    		<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.street}"/>
    		<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.houseNumber}"/>
    	</td>
    	<td>
    		<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.country.sapCountryCd}"/> - 
    		<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.postalCode}"/>
    		<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.city}"/>
		</td>		
			<td class="buttons right">	
				<input type="submit" class="ViewButtonStyle" name="delShipto" value="<fmt:message key="dispokarte.texte.shiptoclear"/>"/>
	    	</td>
    </tr>
  </tbody>
</table>

</html:form>

<script type="text/javascript">
	
function onLoadTab(){
}
	
function onUnloadTab(){
}

function tabChoose(tab){
	<%-- Switch Tab --%>
	selectTab("ShiptoTabs", tab);
	
	onUnloadTab();
	
	a = {"setTab":"", "tab": tab};
	addFormElements(a, "ShipToForm"); <%-- add form data --%>
	var diva = Ext.get('TabsDiv');
		diva.load({
			url: 'Dispokarte.do', 
			params: a,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true,
			callback: function(el){
				onLoadTab();
			}						
		});
<%--		diva.show(); --%>
	}
	
</script>

<table width="100%" border="0" cellpadding="0" cellspacing="0" id ="ShiptoTabs" style="margin-top:4px;"><tr><td width="100%" style="padding-left:4px"  class="subnaviborder">
<ul id="subnavigation">
  <li><a href="javascript:tabChoose('TabShipTo')" id='TabShipTo' <c:if test="${order_dispokarte_DispokarteForm.tab=='TabShipTo'}">class="highlight"</c:if>>
  	<span class="outer"><span class="inner"><fmt:message key="dispokarte.texte.shiptotab"/></span></span></a></li>
<c:forEach var='sarea' varStatus='status' items='${order_dispokarte_DispokarteForm.salesAreas}'>
  <c:set var="sareaTab" value="TabSalesArea${status.count - 1}"/>
  <li><a title="<c:out value="${sarea.sareaDisplayLongname}"/>" href="javascript:tabChoose('<c:out value="${sareaTab}"/>')" id='<c:out value="${sareaTab}"/>' <c:if test="${order_dispokarte_DispokarteForm.tab==sareaTab}">class="highlight"</c:if>>
  	<span class="outer"><span class="inner">
<crh-order:dispokarteInvalidAttributeMarker screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=((DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm")).getSalesAreaBean((SareaEntity)pageContext.getAttribute("sarea")).getShiptoSarea() %>" checkForErrorCode="<%=ShiptoSalesAreaError.SAREA_SORG %>">
  	<c:out value="${sarea.sorg.sorgCd}"/>-
</crh-order:dispokarteInvalidAttributeMarker>
  	<crh-order:dispokarteInvalidAttributeMarker screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=((DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm")).getSalesAreaBean((SareaEntity)pageContext.getAttribute("sarea")).getShiptoSarea() %>" checkForErrorCode="<%=ShiptoSalesAreaError.SAP_BP_SAREA %>">
<crh-order:dispokarteInvalidAttributeMarker screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=((DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm")).getSalesAreaBean((SareaEntity)pageContext.getAttribute("sarea")).getShiptoSarea() %>" checkForErrorCode="<%=ShiptoSalesAreaError.SAREA %>">
		<c:out value="${sarea.distributionChannel}"/>-<c:out value="${sarea.division.divisionCd}"/>
 </crh-order:dispokarteInvalidAttributeMarker>
</crh-order:dispokarteInvalidAttributeMarker>
  	</span></span></a></li>
</c:forEach>
  <li><a href="javascript:tabChoose('contractData')" id='contractData' <c:if test="${order_dispokarte_DispokarteForm.tab=='contractData'}">class="highlight"</c:if>>
  	<span class="outer"><span class="inner"><fmt:message key="dispokarte.texte.contract.data.visualization"/></span></span></a></li>
  	
</ul> 
</td>
</tr>
</table>

<div id="TabsDiv" class="border" style="border-top:none;">
	<c:choose>
		<c:when test="${order_dispokarte_DispokarteForm.tab == 'TabShipTo'}">
			<jsp:include page="TabShipTo.jsp"/>
		</c:when>
		<c:when test="${order_dispokarte_DispokarteForm.tab == 'contractData'}">
			<jsp:include page="ContractDataTile.jsp"/>
		</c:when>
		<c:otherwise>
			<jsp:include page="TabVko.jsp"/>
		</c:otherwise>
	</c:choose>
</div>

<script type="text/javascript">
function submitWithAction(action){
	form = Ext.getDom('ShipToForm');
	Ext.query("input[name=formAction]", form)[0].value = action;
	form.submit();
}

</script>

<html:form action="dispokarte/Dispokarte.do" onsubmit="return false;">
<table class="big_form2" style="margin-top: 2px;">
<tr>
	<td class="buttons right" style="vertical-align: middle">
		<crh-common:authorize function="<%= Authorization.MASTER_DATA_SHIPTO_CARD_W %>">
			<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>" onClick="submitWithAction('reset')">
			<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onClick="submitWithAction('save')">
		</crh-common:authorize>
	</td>
</tr>
</table>
</html:form>
      </td>
</tr>
</table>
<%-- END $RCSfile: DispokarteTile.jsp,v $ --%>

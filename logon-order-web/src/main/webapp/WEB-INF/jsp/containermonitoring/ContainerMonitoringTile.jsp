<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ContainerMonitoringTile.jsp,v $ --%>

<%@page import="com.crh.logon.order.web.actions.containermonitoring.ContainerMonitoringAction"%>
<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<%@page import="com.crh.logon.order.web.actions.containermonitoring.ContainerMonitoringAction" %>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ContainerMonitoringAjaxService.js'/>"></script>

<script type="text/javascript" src="<crh-common:url value='/js/containermonitoring/ContainerMonitoring.js'/>"></script>
	
<jsp:include page="../SubTab7Tile.jsp"/>

<style type="text/css">

	.dynlabel {
		font-size: 12px;
		color: #666666; 	
	}
	
	.listclass {
		overflow: hidden;
	}
	
	.tinylabel {
		font-size: 11px;
	}
	
	table tr td .tinylabel {
		padding-bottom: 0; 
		padding-top: 0;
	}
	
	/* IE */
	.x-panel-body {
		zoom: normal;
	}
	
	.x-panel-bwrap {
		zoom: normal;
	}
</style>

<script type="text/javascript">

var texts = {
		loading: '<fmt:message key="extjs.loading"/>',
		select: '<fmt:message key="extjs.select"/>'
	};

var actions = { 
		ajaxEditShippingPoints: '<%=ContainerMonitoringAction.AJAX_ACTION_EDIT_SHIPPINGPOINTS%>',
		ajaxSetShippingPoints:'<%=ContainerMonitoringAction.AJAX_ACTION_SET_SHIPPINGPOINTS%>',
		search:'<%=ContainerMonitoringAction.ACTION_SEARCH%>'
	};

var action = 'action';

var productionPlantCombo;
var sapMatCombo;

Ext.onReady(function() {
 
	initContainerMonitoringFilters();
		
	<jsp:include page="ContainerMonitoringShippingPointsPartTile.jsp"/> 	

	
	search(actions.search);
	
	// LOGON-6654 default serach filters to ALL
	var parameters = {};
	parameters[action] = actions.ajaxEditShippingPoints;
	shippingPointList.listPanel.load({
		url: 'ContainerMonitoring.do',
		params: parameters,
		text: texts.loading,
		scripts:true,
		callback: function(el){
			//el.show();
			setAllCheckboxes(true, 'DivShippingPointsEdit');
			setSelectedShippingPointCheckboxes();
		}	 
	});

});

</script>

<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.4"/></th>
      </tr>
      <tr>
    
      <td>
	<jsp:include page="../Sub1Tab7Tile.jsp"/>
	
<html:form action="containermonitoring/ContainerMonitoringEntry.do" styleId='order_ContainerMonitoringForm'>

<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
<tr>
	<td class="border" style="background-color: F9F9F9; padding: 2px 0 10px 10px;">
		<div>

			<table style="width: 100%;">
				<colgroup>
					<col width="15%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
					<col width="16%">
				</colgroup>
	<tr> 
 		 <td class="tinylabel">
 			<nobr><fmt:message key="container.monitoring.container.terminal"/></nobr>
 			
		</td>
 		 <td class="tinylabel">
 			<nobr><fmt:message key="bimodalrailcarmonitoring.texte.production.plant"/></nobr>
 			
		</td>
 		<td class="tinylabel">
 			<nobr><fmt:message key="bimodalrailcarmonitoring.texte.material"/></nobr>
 		</td>
		 <td width="100%" colspan="2">
 		</td> 

 		</tr>
 	<tr>
		<td>
			<input type="text" id="ShippingPointsField" style="width:150px;color: black">
			<div class="border" id="DivShippingPointsEdit" style="z-index:40;background:white;width:200px; position: absolute;visibility:hidden">
			</div>
 		</td>
 		<td>
 			<input type="text" name="selectedProductionPlantCd"	value="" id="selectedProductionPlantCd" style="width:100px">
 		</td> 		
		<td>
			<input type="text" name="selectedSapMatId"	value="" id="selectedSapMatId" style="width:100px">		
 		</td>
		<td>
        <input type="hidden" id="sortByColumn" name="sortByColumn" value="<c:out value="${order_ContainerMonitoringForm.sortByColumn}"/>"/>
        <input type="hidden" id="sortAsc" name="sortAsc" value="<c:out value="${order_ContainerMonitoringForm.sortAsc}"/>"/>
 		<input id="buttonSearch" type="button" class="SmallButtonStyle" onclick="javascript:search(actions.search)" value="<fmt:message key="buttons.search"/>"/>
 		</td>
    </tr>
</table>
</div>

</td>
</tr>
</table>
		<div id="containerMonitoringResults">
			<c:if test="${not empty order_ContainerMonitoringForm.resultList}">
				<jsp:include page="ContainerMonitoringResultsPartTile.jsp"/>
			</c:if>
		</div>
</html:form>
</td>
</tr>
</table>

<%-- END $RCSfile: ContainerMonitoringTile.jsp,v $ --%>

<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BimodalRailcarMonitoringTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<%@page import="com.crh.logon.order.web.actions.bimodalrailcarmonitoring.BimodalRailcarMonitoringAction" %>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/BimodalRailcarMonitoringAjaxService.js'/>"></script>

<script type="text/javascript" src="<crh-common:url value='/js/bimodalrailcarmonitoring/BimodalRailcarMonitoring.js'/>"></script>
	
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

var companyCodeListJsOn = <c:out value="${order_BimodalRailcarMonitoringForm.companyCodeListJsOn}" escapeXml="false"/>;

var actions = {
		ajaxSetCompanyCode: '<%=BimodalRailcarMonitoringAction.AJAX_ACTION_SET_COMPANYCODE%>',
		ajaxEditShiptos: '<%=BimodalRailcarMonitoringAction.AJAX_ACTION_EDIT_SHIPTOS%>',
		ajaxSetShiptos:'<%=BimodalRailcarMonitoringAction.AJAX_ACTION_SET_SHIPTOS%>',
		search:'<%=BimodalRailcarMonitoringAction.ACTION_SEARCH%>'
	};

var productionPlantCombo;
var sapMatCombo;
var companyCodeSelector;

Ext.onReady(function() {

	initBimodalRailcarMonitoringFilters();
		
	<jsp:include page="BimodalRailcarMonitoringShiptosPartTile.jsp"/>	


	var companyCodeRec = companyCodeSelector.store.getAt(companyCodeSelector.store.find("companycodeCd",'<c:out value="${order_BimodalRailcarMonitoringForm.selectedCompanyCode}"/>'));
	companyCodeSelector.preselectValue('<c:out value="${order_BimodalRailcarMonitoringForm.selectedCompanyCode}"/>');
	companyCodeSelector.fireEvent("select", companyCodeSelector, companyCodeRec);
	search();
	
	// LOGON-6654 default filter set to ALL
	shiptoList.listPanel.load({
			url: 'BimodalRailcarMonitoring.do',
			params: actions.ajaxEditShiptos,
			text: texts.loading,
			scripts:true,
			callback: function(el){
				setAllCheckboxes(true, 'DivShiptosEdit');
				setSelectedShiptoCheckboxes();
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
	
<html:form action="bimodalrailcarmonitoring/BimodalRailcarMonitoring.do" styleId='order_BimodalRailcarMonitoringForm'>

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
 			 <nobr><fmt:message key="bimodalrailcarmonitoring.texte.company.code"/></nobr>
		</td>
 		<td class="tinylabel">
			<nobr><fmt:message key="bimodalrailcarmonitoring.texte.shipto"/></nobr>
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
			<input type="text" name="selectedCompanyCode"	value="" id="selectedCompanyCode" style="width:100px">
 		</td>
		<td>
			<input type="text" id="ShiptosField" style="width:150px;color: black">
			<div class="border" id="DivShiptosEdit" style="z-index:40;background:white;width:200px; position: absolute;visibility:hidden">
			</div>
 		</td>
 		<td>
 			<input type="text" name="selectedProductionPlantCd"	value="" id="selectedProductionPlantCd" style="width:100px">
 		</td> 		
		<td>
			<input type="text" name="selectedSapMatId"	value="" id="selectedSapMatId" style="width:100px">		
 		</td>
		<td>
        <input type="hidden" id="sortByColumn" name="sortByColumn" value="<c:out value="${order_BimodalRailcarMonitoringForm.sortByColumn}"/>"/>
        <input type="hidden" id="sortAsc" name="sortAsc" value="<c:out value="${order_BimodalRailcarMonitoringForm.sortAsc}"/>"/>
 		<input id="buttonSearch" type="button" class="SmallButtonStyle" onclick="javascript:search()" value="<fmt:message key="buttons.search"/>"/>
 		</td>
    </tr>
</table>
</div>

</td>
</tr>
</table>
		<div id="bimodalRailcarMonitoringResults">
			<c:if test="${not empty order_BimodalRailcarMonitoringForm.resultList}">
				<jsp:include page="BimodalRailcarMonitoringResultsPartTile.jsp"/>
			</c:if>
		</div>
</html:form>
</td>
</tr>
</table>

<%-- END $RCSfile: BimodalRailcarMonitoringTile.jsp,v $ --%>

<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WagonSearchTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../../includeTLDs.jsp"%>
<c:set var="form" scope="page" value="${admin_WagonSearchForm}" />
<%@page import="com.crh.logon.admin.web.forms.masterdata.WagonSearchForm"%>
<%@page import="java.util.Map"%>
<%@page import="com.crh.logon.persistence.entities.views.WagonSearchEntity"%>
<%@page import="com.crh.logon.admin.web.beans.WagonSearchBean"%>

<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>
		<c:set var="mode" value="search" scope="request"/>
		<c:set var="defaultSort" value="wagonNo"/>
		<c:set var="defaultSortDir" value="ASC"/>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/WagonAdminAjaxService.js'/>"></script>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>

<script type="text/javascript">

Ext.onReady(function(){
	// --- Filters ---
	<jsp:include page="WagonSearchFiltersIncTile.jsp"/>

	searchArea.resetSearchCrits();
	<jsp:include page="WagonSearchTilePreselectValues.jsp"></jsp:include>
	
	// --- Result list ---
	<jsp:include page="WagonSearchResultGridIncTile.jsp"/>


    // --- Initial data load ---
    wagonSearchStore.setDefaultSort('<c:out value="${defaultSort}"/>', '<c:out value="${defaultSortDir}"/>');
    searchArea.searchWagons();
    
	Ext.get('buttonSearch').on('click', searchArea.searchWagons.createDelegate(searchArea, []));
	Ext.get('buttonReset').on('click', searchArea.resetSearchCrits.createDelegate(searchArea,[]));	
	Ext.get('buttonExport').on('click', function() {
		var url = 'WagonSearch.do?export';
		var crits = searchArea.getSearchCrits();

		if (crits.<%=WagonSearchEntity.ATTR_WAGON_TYPE_CD%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_WAGON_TYPE_CD%>=' + crits.<%=WagonSearchEntity.ATTR_WAGON_TYPE_CD%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_WAGON_NO%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_WAGON_NO%>=' + crits.<%=WagonSearchEntity.ATTR_WAGON_NO%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID%>=' + crits.<%=WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_WAGON_KEEPER_ID%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_WAGON_KEEPER_ID%>=' + crits.<%=WagonSearchEntity.ATTR_WAGON_KEEPER_ID%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_ORDER_STATUS_CD%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_ORDER_STATUS_CD%>=' + crits.<%=WagonSearchEntity.ATTR_ORDER_STATUS_CD%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD%>=' + crits.<%=WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID%>=' + crits.<%=WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD%>=' + crits.<%=WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD%>.id;
		}
		if (crits.<%=WagonSearchEntity.ATTR_ACTIVE_FLAG%>.value) {
			url += '&<%=WagonSearchEntity.ATTR_ACTIVE_FLAG%>=' + crits.<%=WagonSearchEntity.ATTR_ACTIVE_FLAG%>.id;
		}
				
		window.open(url); 
	});
});

</script> 
<div class="customer_max">
<jsp:include page="../../SubTab9Tile.jsp"/>   
<table class="big_form2">
	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.6"/></th>
	</tr>
	<tr>
    	<td>
            <jsp:include page="../../Sub6Tab9Tile.jsp"/>
   			<table class="classicblue"  style="width:100%;" style="margin-top:4px">
				<tr>
      				<th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.6"/> / <fmt:message key="tabsubitem.9.6.1"/></th>
	            </tr>
				<tbody>
					<tr>
						<td>
							
<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
<tr>
	<td class="border" style="background-color: F9F9F9">
		<div id="divSimpleSearch">
			<table>
				<tr>
					<td>
						<table cellpadding="0" cellspacing="0">
							<colgroup>
								<col width="25%">
								<col width="25%">
								<col width="25%">
								<col width="25%">
							</colgroup>
							<tbody>
								<tr>
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.wagonno" />:</nobr></span>						
									</td>
									<td>
										<input type="text" value="" id="wagonNoCombo" style="width: 150px"/>						
									</td>
									
									<!-- 
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.returnstation" />:</nobr></span>
									</td>
									<td>
										<input type="text" value="" id="returnCombo" style="width: 150px"/>
									</td>
									 -->
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.inactive_reason" />:</nobr></span>
									</td>
									<td>
										<input type="text" id="wagonInactiveReasonCheckCombo"/>
									</td>
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.order_status" />:</nobr></span>
									</td>
									<td>
										<input type="text" id="orderStatusCheckCombo"/>
									</td>
									<td> <!-- last know location -->
										<span class="label"><nobr><fmt:message key="wagonsearch.text.locationstation" />:</nobr></span>
									</td>
									<td>
										<input type="text" value="" id="locationCombo" style="width: 150px"/>
									</td>
								</tr>
								<tr>
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.type" />:</nobr></span>						
									</td>
									<td>
										<input type="text" id="wagonTypeCheckCombo"/>						
									</td>
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.keeper" />:</nobr></span>
									</td>
									<td>
										<input type="text" value="" id="keeperCombo" style="width: 150px"/>
									</td>
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.distribution_dest" />:</nobr></span>
									</td>
									<td>
										<input type="text" id="distributionDestCombo"/>
									</td>		
									<td>
										<span class="label"><nobr><fmt:message key="wagonsearch.text.dest_railway_station" />:</nobr></span>
									</td>
									<td>
										<input type="text" value="" id="destRwsCombo" style="width: 150px"/>
									</td>														
								</tr>
								</tbody>
							</table>
					</td>
					<td>
						<input id="buttonSearch" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.search"/>" style="margin-top: 3px"/>&nbsp;
						<input id="buttonReset" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>" style="margin-top: 3px"/>
						<input id="buttonExport" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.export"/>" style="margin-top: 3px"/>
					</td>
				</tr>
			</table>
		</div>
	</td>
</tr>
</table>


<div id="divSearchResults" style="margin-top: 10px"/>

</td>
					</tr>
				</tbody>
			</table>
		</td>
	</tr>
</table>
</div>
<%-- END $RCSfile: WagonSearchTile.jsp,v $ --%>

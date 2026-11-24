<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant"%>

<body>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<script type="text/javascript">
var texts = {
	loading: '<fmt:message key="extjs.loading"/>',
	select: '<fmt:message key="extjs.select"/>',
	pagerDisplayMsg: '<fmt:message key="extjsgrid.pager.displayMsg"/>',
	pagerEmptyMsg: '<fmt:message key="salesarea.pager.emptyMsg"/>',
	functions: '<fmt:message key="salesarea.grid.functions"/>',
	sareaId: '<fmt:message key="salesarea.grid.sareaId"/>',
	sorgCd: '<fmt:message key="salesarea.grid.sorgCd"/>',
	distributionChannel: '<fmt:message key="salesarea.grid.distributionChannel"/>',
	division: '<fmt:message key="salesarea.grid.division"/>',
	sareaDisplayLongname: '<fmt:message key="salesarea.grid.sareaDisplayLongname"/>',
	delnoteUsesWsNumberFlag: '<fmt:message key="salesarea.grid.delnoteUsesWsNumberFlag"/>',
	shortTermOrderThresholdMi: '<fmt:message key="salesarea.grid.shortTermOrderThresholdMi"/>',
	allowBimodalCreationTillMi: '<fmt:message key="salesarea.grid.allowBimodalCreationTillMi"/>',
	shiptoOpen1From: '<fmt:message key="salesarea.grid.shiptoOpen1From"/>',
	shiptoOpen1To: '<fmt:message key="salesarea.grid.shiptoOpen1To"/>',
	shiptoOpen2From: '<fmt:message key="salesarea.grid.shiptoOpen2From"/>',
	shiptoOpen2To: '<fmt:message key="salesarea.grid.shiptoOpen2To"/>',
	mailFrom: '<fmt:message key="salesarea.grid.mailFrom"/>',
	mailTestTo: '<fmt:message key="salesarea.grid.mailTestTo"/>',
	mailBcc: '<fmt:message key="salesarea.grid.mailBcc"/>',
	mailRailadmin: '<fmt:message key="salesarea.grid.mailRailadmin"/>',
	mailEncoding: '<fmt:message key="salesarea.grid.mailEncoding"/>',
	sorgAddressName: '<fmt:message key="salesarea.grid.sorgAddressName"/>'
};

function imgIconEdit() {
	return '<img src="<crh-common:url value='images/silk/pencil.png'/>" />';
}

</script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/SalesAreaAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/masterdata/SalesAreaTile.js'/>"></script>

<jsp:include page="../SubTab9Tile.jsp"/>

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.4"/></th>
      </tr>
      <tr>
    
      <td>
            <!-- Begin Submenu -->
<jsp:include page="../Sub4Tab9Tile.jsp"/>

<br>

<table class="big_form2" id="searchTable" cellpadding="0" cellspacing="0" border="0"
	style="padding: 0px; margin: 0px; margin-top: 20px;">
	<colgroup>
		<col width="10%">
		<col width="25%">
		<col width="10%">
		<col width="55%">
	</colgroup>
	<tr>
		<td valign="bottom"
			style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;"
			class="label"><fmt:message key="salesarea.search.SalesOrganisationCode" />:</td>
		<td style="border: 0px;"><input type="text" name="sorgCombo"
			value="" id="sorgCombo" style="width: 95%"></td>
		<td style="border: 0px; vertical-align: middle;"><input type="button" class="ViewButtonStyle"
			value="<fmt:message key="buttons.search"/>"
			id="buttonSearch"></td>
		<td></td>
	</tr>
</table>
<div id="divSalesAreaResults" style="padding: 0px; margin: 20px 0px 0px;"></div>

</td>
</tr>
</table>
</body>
</html>

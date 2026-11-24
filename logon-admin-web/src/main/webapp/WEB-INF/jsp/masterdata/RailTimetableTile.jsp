<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RailTimetableTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.web.utils.Authorization"%>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.MaterialEnhancementsAction" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.RailDistanceDetailAction" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.RailTimetableAction" %>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/MaterialEnhancementsAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RailTimetableAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<script type="text/javascript">
function showImportMessage() {

	Logon.message.info('<fmt:message key="railtimetable.import_started"/>');
}
<c:set var="form" scope="page" value="${admin_MaterialEnhancementsForm}" />

var texts = {
	loading: '<fmt:message key="extjs.loading"/>',
	select: '<fmt:message key="extjs.select"/>',
	pagerDisplayMsg: '<fmt:message key="extjsgrid.pager.displayMsg"/>',
	pagerEmptyMsg: '<fmt:message key="materialenhancements.pager.emptyMsg"/>',
	autoadd1PerNOfUom: '<fmt:message key="materialenhancements.grid.autoadd1PerNOfUom"/>',
	autoaddNUom: '<fmt:message key="materialenhancements.grid.autoaddNUom"/>'
};

var raildistancetabletexts = {
	gridtitle: '<fmt:message key="raildistancetable.grid.gridtitle"/>',
	functions: '<fmt:message key="raildistancetable.grid.functions"/>',
	stationNameFrom: '<fmt:message key="raildistancetable.grid.stationnamefrom"/>',
	stationNameTo: '<fmt:message key="raildistancetable.grid.stationnameto"/>',
	railDistance: '<fmt:message key="raildistancetable.grid.raildistance"/>',
	remark: '<fmt:message key="raildistancetable.grid.remark"/>'
};

var railtimetabletexts = {
	gridtitle: '<fmt:message key="railtimetable.grid.gridtitle"/>',
	functions: '<fmt:message key="railtimetable.grid.functions"/>',
	departure: '<fmt:message key="railtimetable.grid.departure"/>',
	arrival: '<fmt:message key="railtimetable.grid.arrival"/>',
	rail: '<fmt:message key="railtimetable.grid.rail"/>',
	dayoffset: '<fmt:message key="railtimetable.grid.dayoffset"/>',
	arrtimeswitch: '<fmt:message key="railtimetable.grid.arrtimeswitch"/>',
	arrtime: '<fmt:message key="railtimetable.grid.arrtime"/>',
	deptime: '<fmt:message key="railtimetable.grid.deptime"/>',
	deptimeswitch: '<fmt:message key="railtimetable.grid.deptimeswitch"/>',
	comment: '<fmt:message key="railtimetable.grid.comment"/>'
};

function imgIconEdit() {
	return '<img src="<crh-common:url value='images/silk/pencil.png'/>" />';
}

function imgIconDelete() {
	return '<img src="<crh-common:url value='images/delete2.png'/>" />';
}

function errorRenderer(val, meta, rec) {
	var result = '<div style="float: left; width: 20px;">';
	if (rec.data.updateBlockedByHjid != null && rec.data.updateBlockedByHjid != '') {
		var title = "<bean:message key="materialenhancements.grid.error.popup" arg0="' + rec.data.updateBlockedByHjid  + '"/>";
		result += '<img src="' + crhCommonUrl('./images/silk/error.png') + '" title="' + title + '"/>';
	}
	result += '</div>';
	return result;	
}

function deleteRailTimetable(railTimetableId) {
	Ext.MessageBox.buttonText.yes = '<fmt:message key="railtimetable.messagebox.delete"/>';
	Ext.MessageBox.buttonText.no = '<fmt:message key="railtimetable.messagebox.cancel"/>';

	Ext.MessageBox.confirm('<fmt:message key="railtimetable.messagebox.confirmation"/>',
		'<fmt:message key="railtimetable.messagebox.text"/>',
		function(btn) {
			if (btn == 'yes') {
				Ext.Ajax.request( {
					url :'<c:url value="RailTimetable.do"/>',
					method: 'POST',
					params: {'formAction': '<%= RailTimetableAction.FORWARD_DELETE_TIMETABLE %>',
						'railTimetableId': railTimetableId},
			        success: function(response) {
						Ext.get('buttonSearch').dom.click();

						if (stationToStore.lastOptions){
							stationToStore.reload(stationToStore.lastOptions);
						}
					}
				} );
			}
		} );
}



function deleteRailDistance(railDistanceId) {
	Ext.MessageBox.buttonText.yes = '<fmt:message key="railtimetable.messagebox.delete"/>';
	Ext.MessageBox.buttonText.no = '<fmt:message key="railtimetable.messagebox.cancel"/>';

	Ext.MessageBox.confirm('<fmt:message key="railtimetable.messagebox.confirmation"/>',
		'<fmt:message key="railtimetable.messagebox.text"/>',
		function(btn) {
			if (btn == 'yes') {
				Ext.Ajax.request( {
					url :'<c:url value="RailTimetable.do"/>',
					method: 'POST',
					params: {'formAction': '<%= RailTimetableAction.FORWARD_DELETE_DISTANCE %>',
						'railDistanceId': railDistanceId},
			        success: function(response) {
						Ext.get('buttonSearch').dom.click();

						if (stationToStore.lastOptions){
							stationToStore.reload(stationToStore.lastOptions);
						}
					}
				} );
			}
		} );
}

</script>
<script type="text/javascript" src="<crh-common:url value='/js/masterdata/RailTimetableTile.js'/>"></script>

<c:set var="displayTimetableDistance" value="${false}"/>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE %>">
	<c:set var="displayTimetableDistance" value="${true}"/>
</crh-common:authorize>

<c:set var="displayTimetableImportUpload" value="${false}"/>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_RAIL_TIMETABLE %>">
	<c:set var="displayTimetableImportUpload" value="${true}"/>
</crh-common:authorize>

<jsp:include page="../SubTab9Tile.jsp"/>   
<table class="big_form2">
	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.6"/></th>
	</tr>
	<tr>
    	<td>
            <jsp:include page="../Sub6Tab9Tile.jsp"/>
   			<table class="classicblue" style="margin-top:4px; width:100%">
				<tr>
      				<th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.6"/> / <fmt:message key="tabsubitem.9.6.4"/></th>
	            </tr>
				<tbody>
					<tr>
					<td>
						<c:if test="${displayTimetableDistance}">
							<!-- Begin table 3 rows -->
							<table class="big_form2">
								<tr>
									<td>
										<!-- Begin Submenu -->
										<table class="big_form2" id="searchTable" cellpadding="0"
											cellspacing="0" border="0" width="100%"
											style="padding: 0px; margin: 0px; margin-top: 20px;">
											<tr>
												<td valign="bottom"
													style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;"
													class="label"><fmt:message key="railtimetable.text.stationfrom" />:</td>
												<td style="border: 0px;"><input type="text"
													name="stationFromCombo" value="" id="stationFromCombo"
													style="width: 95%"></td>
												<td valign="bottom"
													style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;"
													class="label"><fmt:message key="railtimetable.text.stationto" />:</td>
												<td style="border: 0px;"><input type="text"
													name="stationToCombo" value="" id="stationToCombo"
													style="width: 95%"></td>
												<td style="border: 0px; vertical-align: middle;"><input
													type="button" class="ViewButtonStyle"
													value="<fmt:message key="buttons.search"/>"
													id="buttonSearch"></td>
												</td>
											</tr>
										</table>
										
										<div align="right"
											style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;"
											class="label">
											<a
												href="javascript:showPopup('<c:url value="RailDistanceDetailEntry.do"/>')"><img
												src="<crh-common:url value='images/add.png'/>" />&nbsp;
												<fmt:message key="railtimetable.text.createdistance" /> </a>
										</div>

										<div id="divRailDistanceSearchResults"></div>
										
										<div align="right"
											style="white-space: nowrap; border: 0px; padding-right: 4px; padding-top: 10px; vertical-align: middle;"
											class="label">
											<a
												href="javascript:showPopup('<c:url value="RailTimetableDetailEntry.do"/>')"><img
												src="<crh-common:url value='images/add.png'/>" />&nbsp;
												<fmt:message key="railtimetable.text.createtimetable" /> </a>
										</div>


										<table class="big_form2" id="searchTable2" cellpadding="0"
											cellspacing="0" border="0"
											style="padding: 0px; margin: 0px; margin-top: 20px;">
										</table>

										<div id="divRailTimetableSearchResults"></div>

									</td>
								</tr>
							</table>
						</c:if>
						</td>
						</tr>
					<tr>
						<td>
						<c:if test="${displayTimetableImportUpload}">
							<table class="classic">
								<tr>
									<td><html:form action="/RailTimetable"
											enctype="multipart/form-data" method="post"
											styleId="importForm">
											<div>
												<html:submit property="action" value="import"
													styleClass="ViewButtonStyle" onclick="showImportMessage();">Import CSV</html:submit>
												<html:file name="admin_RailTimetableForm"
													property="timetableImportFile" />
											</div>
											<div>
												<html:submit property="action" value="export"
													styleClass="ViewButtonStyle">Export CSV</html:submit>
											</div>
										</html:form></td>
								</tr>
							</table>
						</c:if>
						</td>

					</tr>
				</tbody>
			</table>
		</td>
	</tr>
</table>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<div class="customer_max">
    <!-- beginn Border-Table   -->
    <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    	<tr>
    		<td width="10" height="1">&nbsp;</td>
    		<td>
				<div style="float:left; margin:0px; padding:0px;"><img src="<crh-common:url value='/images/cccccc.gif'/>" alt="" width="1250" height="1" border="0" style="float:left; margin:0px; padding:0px;"></div><div style="clear:both; padding:0px;margin:0px;"></div>
   				<jsp:include page="../SubTab9Tile.jsp"/>   

				<!-- Begin table 3 rows -->
				<table style="background-color: #FFF; border: 0px; padding: 6px; width: 100%; border-collapse: separate; border-spacing:4px;">
     				<tr class="classicblue">
     					<th height="21" colspan="4" class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/></th>
					</tr>
     				<tr>
						<td>
           					<!-- Beginn Submenu 2-2-1-1 -->
							<jsp:include page="../Sub5Tab9Tile.jsp"/>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/TruckRouteAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.valid.ValidationMessages.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.EditableList.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
                            <%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
                            <script type="text/javascript">
                                var defaultDateFormat = '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>';
var texts = {
		traveltimegooglemaps: '<fmt:message key="truckroute.traveltimegooglemaps"/>',
		transportmode: '<fmt:message key="truckroute.transportmode"/>',
		timecorrectionfactor: '<fmt:message key="truckroute.timecorrectionfactor"/>',
		timeplanned: '<fmt:message key="truckroute.timeplanned"/>',
		traveltimelogon: '<fmt:message key="truckroute.traveltimelogon"/>',
		distancegooglemaps: '<fmt:message key="truckroute.distancegooglemaps"/>',
		distancelogon: '<fmt:message key="truckroute.distancelogon"/>',
		distancecorrectionfactor: '<fmt:message key="truckroute.distancecorrectionfactor"/>',
		correcteddistance: '<fmt:message key="truckroute.correcteddistance"/>',
		comment: '<fmt:message key="truckroute.comment"/>',
		errorInvalidTimecorrectionfactor: '<fmt:message key="truckroute.error.invalid.timecorrectionfactor"/>',
		lastdelivery: '<fmt:message key="truckroute.lastdelivery"/>',
		shiptoName: '<fmt:message key="truckroute.shipto.name"/>',
		pagerDisplayMsg: '<fmt:message key="extjsgrid.pager.displayMsg"/>',
		pagerEmptyMsg: '<fmt:message key="extjsgrid.pager.emptyMsg"/>',
		listPageChangeTitle: '<fmt:message key="extjsgrid.list.pageChangeTitle"/>',
		listPageChangeMessage: '<fmt:message key="extjsgrid.list.pageChangeMessage"/>',
		listMaskMessage: '<fmt:message key="extjsgrid.list.maskMessage"/>',
		listNoChangesMessage: '<fmt:message key="extjsgrid.list.noChangesMessage"/>',
		listButtonYes: '<fmt:message key="extjsgrid.list.buttonYes"/>',
		listButtonNo: '<fmt:message key="extjsgrid.list.buttonNo"/>',
		listButtonCancel: '<fmt:message key="extjsgrid.list.buttonCancel"/>',
		gridtitle: '<fmt:message key="truckroute.gridtitle"/>',
		buttonSave: '<fmt:message key="extjsgrid.button.save"/>',
		buttonReset: '<fmt:message key="extjsgrid.button.reset"/>',
		shippingpoint: '<fmt:message key="dispo.texte.shippingpoint"/>',
		shiptoId: '<fmt:message key="fields.shipto.id"/>',
		shiptoPlz: '<fmt:message key="fields.shipto.plz"/>',
		shiptoOrt: '<fmt:message key="fields.shipto.ort"/>'
};	
</script>
<script type="text/javascript" src="<crh-common:url value='/js/truckadmin/TruckRouteTile.js'/>"></script>

	<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
		<tr>
			<td class="border">
				<div id="divSimpleSearch">
					<table>
						<tr>
							<td>
								<table cellpadding="0" cellspacing="0">
									<colgroup>
										<col width="165">
										<col width="185">
									</colgroup>
									<tbody>
										<tr>
											<td>
												<span class="label"><nobr><fmt:message key="dispo.texte.shippingpoint" />:</nobr></span>						
											</td>
											<td>
												<input type="text" value="" id="startShippingpointCombo" style="width: 120px"/>
											</td>
											<td>
												<span class="label"><nobr><fmt:message key="dispo.texte.shipto" />:</nobr></span>					
											</td>
											<td>
												<input type="text" value="" id="endShiptoCombo" style="width: 120px"/>
											</td>
										</tr>
									</tbody>
								</table>
							</td>
							<td>
								<input id="buttonSearch" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.search"/>" style="margin-top: 3px"/>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td colspan="2">
				<div id="divSearchResults" style="margin-top: 10px"></div>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td align="right">
				<input id="buttonExport" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.export"/>" style="margin-top: 3px"/>
			</td>
		</tr>
	</table>
</td>
</tr>
</table>

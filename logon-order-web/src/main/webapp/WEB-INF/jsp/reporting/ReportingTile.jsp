<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ReportingTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx" %>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.order.web.actions.reporting.ReportingAction"%>
<%@page import="com.crh.logon.business.services.reporting.ReportMetaData"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ReportingAjaxService.js'/>"></script>
 
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

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.form.PeriodSelector.js'/>"></script>

<script type="text/javascript">
Ext.onReady(function(){
	<jsp:include page="ReportingSearchFilters.jsp"/>

	function getUrlSearchParameters() {

		var url = '';
		var crits = getSearchFields();
		if (crits.reportCd) {
			url += '&<%= ReportMetaData.PARAM_REPORT_CD %>=' + crits.reportCd;
		}
		if (crits.fromDat) {
			url += '&<%= ReportMetaData.PARAM_FROM_DAT %>=' + crits.fromDat;
		}
		if (crits.toDat) {
			url += '&<%= ReportMetaData.PARAM_TO_DAT %>=' + crits.toDat;
		}
		if (crits.destinationCdList) {
			url += '&<%= ReportMetaData.PARAM_DISTRIBUTION_DEST_CD_LIST %>=' + crits.destinationCdList;
		}
		if (crits.materialPackageCdList) {
			url += '&<%= ReportMetaData.PARAM_MATERIAL_PACKAGE_CD_LIST %>=' + crits.materialPackageCdList;
		}
		if (crits.orderStatusCdList) {
			url += '&<%= ReportMetaData.PARAM_ORDER_STATUS_CD_LIST %>=' + crits.orderStatusCdList;
		}
		if (crits.langCd) {
			url += '&<%= ReportMetaData.PARAM_LANG_CD %>=' + crits.langCd;
		}
		if (crits.poolIdList) {
			url += '&<%= ReportMetaData.PARAM_POOL_ID_LIST %>=' + crits.poolIdList;
		}
		if (crits.carrierIdList) {
			url += '&<%= ReportMetaData.PARAM_CARRIER_ID_LIST %>=' + crits.carrierIdList;
		}

		return url;
	}

	Ext.get('buttonExport').on('click', function() {

		if ( (fromDat.getValue()) && (toDat.getValue()) && (fromDat.getValue() > toDat.getValue()) ) {
			Logon.message.warning('<fmt:message key="reporting.error.invalid.delivery.earlier.later"/>');
			return;
		}
		if (!carrierCheckComboBox.disabled && !carrierCheckComboBox.validateOnSubmit()) {
			return false;
		}

		var url = 'Reporting.do?export';
		url = url + getUrlSearchParameters();

		var propertyNames = reportCombo.getSelectedRecord().data['propertyNames'];

		if (propertyNames.indexOf('openInSameWindow') < 0) {
			window.open(url);
		}
		else {
			window.location.href = url;
		}
	});

	Ext.get('buttonPreview').on('click', function() {
		
		if ((fromDat.getValue()) && (toDat.getValue()) && (fromDat.getValue() > toDat.getValue())) {
			Logon.message.warning('<fmt:message key="reporting.error.invalid.delivery.earlier.later"/>');
			return;
		}
		if (!carrierCheckComboBox.disabled && !carrierCheckComboBox.validateOnSubmit()) {
			return false;
		}
		
		var url = 'Reporting.do?preview';
		url = url + getUrlSearchParameters();
		p = {};		
		var diva = Ext.get('divPreview');
		diva.load( {
			url :url,
			params : p,
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
	});

	// This sets the the searchTable width in pixels, locking it even if the previewDiv grows larger
	Ext.get('searchTable').setWidth(Ext.get('divPreview').getWidth());
});

function toCommaDelimitedStr(arr) {
	var res = '';
	for (var i=0;i<arr.length;i++) {
		res += arr[i];
		if (i + 1<arr.length) {
			res += ',';
		}
	}	
	return res;
}
</script>

<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
<tr>
	<td class="border" style="background-color: F9F9F9; padding: 2px 0 0 10px;">
		<div id="divSimpleSearch" style="width: 100%">
			<table style="width: 100%;">
				<colgroup>
					<col width="12%">
					<col width="19%">
					<col width="11%">
					<col width="12%">
					<col width="11%">
					<col width="12%">
					<col width="11%">
					<col width="12%">
				</colgroup>
				<tr>
					<td class="tinylabel">
						<fmt:message key="fields.report_type"/>
					</td>
				
					<td class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="width: 100%;">
							<colgroup>
								<col width="50%">
								<col width="50%">
							</colgroup>
							<tr>
								<td style="padding: 0; margin: 0;">
									<span id="fromDatLabel">from</span>
								</td>
								<td style="padding: 0; margin: 0;">
									<span id="toDatLabel">to</span>
								</td>
							</tr>
						</table>
					</td>
					
					<td class="tinylabel">
						<fmt:message key="fields.order.distributiondestination"/>
					</td>
					
					<td class="tinylabel">
						<fmt:message key="shippingpoint.lpoint.field.packageType"/>
					</td>
					
					<td class="tinylabel">
						<fmt:message key="fields.order.status"/>
					</td>
					
					<td class="tinylabel">
						<fmt:message key="fields.pool.pool"/>
					</td>
					
					<td class="tinylabel">
						<fmt:message key="fields.carrier.commercial"/>
					</td>
					
					<td class="tinylabel">
						<fmt:message key="dispokarte.texte.sparche"/>
					</td>
					
					<td rowspan="2">
						<input id="buttonExport" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.export"/>"/><br/>
						<input id="buttonPreview" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.preview"/>"/><br/>
					</td>
				</tr>
				<tr>
					<td>
					  	<input type="text" id="reportField"/>
					</td>
								
					<td>
						<table cellspacing="0" cellpadding="0" style="width: 100%; padding: 0; margin: 0;">
							<colgroup>
								<col width="50%">
								<col width="50%">
							</colgroup>
							<tr>
								<td style="padding: 0; margin: 0;">
									<input type="text" id="fromDatStr" style="width: 10px;"/>
								</td>
								<td style="padding: 0; margin: 0;">
									<table cellspacing="0" cellpadding="0" style="width: 100%; border-collapse: collapse;">
										<colgroup>
											<col>
											<col width="100%">
										</colgroup>
										<tr>
											<td style="padding: 0; margin: 0;">
												<input type="text" id="toDatStr" style="width: 10px;" />
											</td>
											<td style="padding: 0; margin: 0; align: left; vertical-align: top;">
												<div id="periodSelectorTriggerId" style="padding: 0; margin: 0; align: left; vertical-align: top;"></div>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
					
					<td>
						<input type="text" id="distributionDestField"/>
					</td>
					
					<td>
						<input type="text" id="materialPackageField"/>
					</td>
					
					<td>
						<input type="text" id="orderStatusField"/>
					</td>
					
					<td>
						<input type="text" id="poolField"/>
					</td>
					
					<td>
						<input type="text" id="carrierField"/>
					</td>
					
					<td>
						<input type="text" id="langField"/>
					</td>
				</tr>
			</table>
		</div>
	</td>
</tr>
</table>

<div id="divPreview" style="margin-top: 2px"/>

<%-- END $RCSfile: ReportingTile.jsp,v $ --%>

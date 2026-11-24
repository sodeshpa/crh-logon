<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<%@ include file="../includeTLDs.jsp"%>

<jsp:include page="../SubTab12Tile.jsp"/>

<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/demandplanning.css"/>">

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/DemandPlanningAjaxService.js'/>"></script>
<script type="text/javascript" src='<crh-common:url value="/js/logon-validation.js"/>'></script>
<script type="text/javascript" src="<crh-common:url value='/js/demandPlanning.js'/>"></script>

<!-- Begin table 3 rows -->
<table class="big_form2" role="presentation">

	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.demandplanning" />
			/ <fmt:message key="tabsubitem.12.2" /></th>
	</tr>
	<tr>
		<td class="reportExpected">
			<section id="filter">
				<label for="fromDatePicker"><fmt:message key="demandplan.filter.from"/></label>
				<label for="toDatePicker"><fmt:message key="demandplan.filter.to"/></label>
				<label for="soldtoCombo"><fmt:message key="demandplan.filter.selectSoldto"/></label>
				<label for="shiptoCombo"><fmt:message key="demandplan.filter.selectShipto"/></label>
				<input type="text" id="fromDatePicker"/>
				<input type="text" id="toDatePicker"/>
				<input type="text" id="soldtoCombo"/>
				<input type="text" id="shiptoCombo"/>
				<div class="buttons">
					<input type="button" id="generateReportButton" class="ViewButtonStyle" value="<fmt:message key="demandplan.filter.generatereport"/>">
					<input type="button" id="exportXLSButton" class="ViewButtonStyle" value="<fmt:message key="demandplan.filter.exportxls"/>">
				</div>
			</section>
			<section id="report">
				<div id="reportTable"></div>
			</section>
		</td>
	</tr>
</table>
<script type="text/javascript">
	const texts = {
		'tons': '<c:out value="${order_demandplanning_DemandPlanningForm.uomTonsText}"/>',
	};
	Ext.onReady(function(){
		demandPlanning.reportExpected(
			{
				'dateFormat': '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				'dateAltFormats': '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>'
			}
		);
	});
</script>

<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@ page import="com.crh.logon.order.web.actions.demandplanning.ManagePlanningEntryAction" %>
<%@ page import="com.crh.logon.web.utils.Authorization" %><%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp"%>

<jsp:include page="../SubTab12Tile.jsp"/>

<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/demandplanning.css"/>">

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.form.NoFilterComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/DemandPlanningAjaxService.js'/>"></script>
<script type="text/javascript" src='<crh-common:url value="/js/logon-validation.js"/>'></script>
<script type="text/javascript" src="<crh-common:url value='/js/demandPlanning.js'/>"></script>

<table class="big_form2" role="presentation">

	<tr class="classicblue">
		<th scope="col" class="classicblue"><fmt:message key="tabitem.demandplanning" />
			/ <fmt:message key="tabsubitem.12.1" /></th>
	</tr>
	<tr>
		<td class="managePlanning">
			<section id="filter">
				<label for="weekDatePicker"><fmt:message key="demandplan.filter.selectWeek"/></label>
				<label for="shiptoCombo"><fmt:message key="demandplan.filter.selectShipto"/></label>
				<input type="text" id="weekDatePicker"/>
				<input type="text" id="shiptoCombo"/>
			</section>
			<section>
				<h3><fmt:message key="demandplan.plan.forecast"/></h3>
				<div id="plan">
				</div>
			</section>
		</td>
	</tr>
</table>

<template id="newItemButtonTemplate">
	<a class="newItemButton" href="#">+ <fmt:message key="demandplan.plan.addplan" /></a>
</template>
<template id="planRowTemplate">
	<input type="text" class="materialCombo">
	<input type="text" class="weightInput">
	<div class="uomLabelWrapper">
		<c:out value="${order_demandplanning_DemandPlanningForm.uomTonsText}"/>
	</div>
	<a class="deleteButton" href="#">
		<img title="<fmt:message key="demandplan.plan.deleteplan"/>" alt="<fmt:message key="demandplan.plan.deleteplan"/>" src="<crh-common:url value='/images/delete.gif'/>"/>
	</a>
	<a class="editButton" href="#">
		<img title="<fmt:message key="demandplan.plan.editplan"/>" alt="<fmt:message key="demandplan.plan.editplan"/>" src="<crh-common:url value='/images/edit.gif'/>"/>
	</a>
	<a class="saveButton" href="#">
		<img title="<fmt:message key="demandplan.plan.saveplan"/>" alt="<fmt:message key="demandplan.plan.saveplan"/>" src="<crh-common:url value='/images/confirm.gif'/>"/>
	</a>
</template>

<script type="text/javascript">
	Ext.onReady(function(){
		demandPlanning.managePlanning(
			{
				'dateFormat': '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				'dateAltFormats': '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
				'weekCount': <%= ManagePlanningEntryAction.WEEK_COUNT %>,
				'isAdmin': <%= Authorization.isAllowed(Authorization.DEMAND_PLANNING_ADMIN_RIGHTS) %>
			}
		);
	});
</script>
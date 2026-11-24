<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.RailTimetableDetailAction" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.RailTimetableDetailEntryAction" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.RailTimetableDetailForm" %>
<%@ page import="com.crh.logon.admin.web.beans.RailTimetableDetailBean" %>
<%@ page import="com.crh.logon.persistence.entities.common.RailTimetableEntity" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<c:set var="form" value="${sessionScope.admin_RailTimetableDetailForm}"/>
<c:set var="formBean" value="${sessionScope.admin_RailTimetableDetailForm.railTimetableDetails}"/>

<c:set var="formType"><%= RailTimetableDetailBean.FORM_TYPE_NEW %></c:set>
<c:set var="railTimetableId" value=""/>
<c:set var="hiddenDepTimeMin" value="${formBean.depTimeMin}"/>
<c:set var="hiddenArrTimeMin" value="${formBean.arrTimeMin}"/>
<c:set var="hiddenDepSwitchingTimeMin" value="${formBean.depSwitchingTimeMin}"/>
<c:set var="hiddenArrSwitchingTimeMin" value="${formBean.arrSwitchingTimeMin}"/>
<c:if test="${not empty param['railTimetableDetails.railTimetableId']}">
	<c:set var="formType"><%= RailTimetableDetailBean.FORM_TYPE_EXISTING %></c:set>
	<c:set var="railTimetableId" value="${param['railTimetableDetails.railTimetableId']}"/>
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RailTimetableAjaxService.js'/>"></script>
<script type="text/javascript">

Ext.onReady( function() {

	Ext.get('buttonCancel').on('click', function() {
		hidePopup();
	} );
	
	Ext.get('buttonSave').on('click', function() {
		var railTimetableDetailDiv = Ext.get('RailTimetableDetailDiv');

		<c:if test="${formType == 'new'}">
		var submitParams = {'depStationId': detailStationFromCombo.getValue(),
			'arrStationId': detailStationToCombo.getValue() };
		</c:if>
		<c:if test="${formType == 'existing'}">	
		var submitParams = {'railTimetableDetails.railDistanceId': '<c:out value="${formBean.railTimetableId}" />'};
		</c:if>
		
		storeValueAsHiddenField('RailTimetableDetailForm', 'formAction', '<%= RailTimetableDetailAction.FORWARD_SAVE %>');
		addFormElements(submitParams, "<%= RailTimetableDetailForm.SESSION_RAIL_TIMETABLE_DETAIL_FORM_NAME %>");

		railTimetableDetailDiv.load( {
			url: '<c:url value="RailTimetableDetail.do"/>',
			params: submitParams,
			text: '<fmt:message key="extjs.loading"/>',
			scripts: true
		} );
	} );
	
	<c:if test="${formType == 'new'}">
	var detailStationRecordType;
	var detailStationFromStore;
	var detailStationToStore;
	var detailStationFromCombo;
	var detailStationToCombo;
	var firstLoadDetailStationFromStore = true;

	function initFormHeader() {
		detailStationRecordType = Ext.data.Record.create( [ {
			name: "name",
			mapping: "name",
			type: "string"
		}, {
			name: "value",
			mapping: "value",
			type: "string"
		} ]);

		var detailStationFromStore = new Ext.data.Store( {
			proxy: new Ext.data.DWRProxy(RailTimetableAjaxService.fetchRailwayStationList),
			reader: new Ext.data.ObjectReader( {
				id: "value"
			}, detailStationRecordType),
			remoteSort: true
		} );

		detailStationFromStore.load();

		var detailStationToStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxy(RailTimetableAjaxService.fetchRailwayStationList),
			reader: new Ext.data.ObjectReader( {
				id: "value"
			}, detailStationRecordType),
			remoteSort: true
		});

		detailStationFromCombo = new Ext.form.ComboBox( {
			store: detailStationFromStore,
			displayField: 'name',
			valueField: 'value',
		    typeAhead: false,
			minChars: 2,
			forceSelection: true,
			triggerAction: 'all',
			emptyText: '',
			selectOnFocus: true,
			applyTo: 'railTimetableDetails.depRailwayStationName',
			loadingText: '<fmt:message key="extjs.loading"/>'
		} );
		
		detailStationToCombo = new Ext.form.ComboBox( {
			store: detailStationToStore,
			displayField: 'name',
			valueField: 'value',
			typeAhead: false,
			minChars: 2,
			forceSelection: true,
			triggerAction: 'all',
			emptyText: '',
			selectOnFocus: true,
			applyTo: 'railTimetableDetails.arrRailwayStationName',
			loadingText: '<fmt:message key="extjs.loading"/>',
			disabled: true
		} );

		detailStationFromCombo.on('select', function(record) {
			detailStationToCombo.clearValue();
			detailStationToCombo.reset();
			detailStationToStore.reload({params: {start: 0, limit: detailStationToCombo.pageSize, query: ""} });
			detailStationToCombo.setDisabled(false);

			} );
	}

	initFormHeader();	
</c:if>

	var depSwitchingTimeMin = new Ext.form.TimeField({
		id: 'depSwitchingTimeMin',
		increment: 5,
		format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		value: detailTimeRenderer(${hiddenDepSwitchingTimeMin}),
		listeners: {
			change: depSwitchingTimeChange
		},
		applyTo: 'railTimetableDetails.depSwitchingTimeMin',
		width: 82
	});
	
	var depTimeMin = new Ext.form.TimeField({
		id: 'depTimeMin',
		increment: 5,
		format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		value: detailTimeRenderer(${hiddenDepTimeMin}),
		listeners: {
			change: depTimeChange
		},
		applyTo: 'railTimetableDetails.depTimeMin',
		width: 82
	});
	
	var arrTimeMin = new Ext.form.TimeField({
		id: 'arrTimeMin',
		increment: 5,
		format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		value: detailTimeRenderer(${hiddenArrTimeMin}),
		listeners: {
			change: arrTimeChange
		},
		applyTo: 'railTimetableDetails.arrTimeMin',
		width: 82
	});
	
	var arrSwitchingTimeMin = new Ext.form.TimeField({
		id: 'arrSwitchingTimeMin',
		increment: 5,
		format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		value: detailTimeRenderer(${hiddenArrSwitchingTimeMin}),
		listeners: {
			change: arrSwitchingTimeChange
		},
		applyTo: 'railTimetableDetails.arrSwitchingTimeMin',
		width: 82
	});
} );

function depTimeChange(field, newValue, oldValue) {
	var hiddenDepTimeMinField = document.getElementById("hiddenDepTimeMin");
	var temp = newValue.split(":");
	hiddenDepTimeMinField.value = parseInt(temp[0]) * 60 + parseInt(temp[1]);
}

function depSwitchingTimeChange(field, newValue, oldValue) {
	var hiddenDepTimeMinField = document.getElementById("hiddenDepSwitchingTimeMin");
	var temp = newValue.split(":");
	hiddenDepTimeMinField.value = parseInt(temp[0]) * 60 + parseInt(temp[1]);
}

function arrTimeChange(field, newValue, oldValue) {
	var hiddenDepTimeMinField = document.getElementById("hiddenArrTimeMin");
	var temp = newValue.split(":");
	hiddenDepTimeMinField.value = parseInt(temp[0]) * 60 + parseInt(temp[1]);
}

function arrSwitchingTimeChange(field, newValue, oldValue) {
	var hiddenDepTimeMinField = document.getElementById("hiddenArrSwitchingTimeMin");
	var temp = newValue.split(":");
	hiddenDepTimeMinField.value = parseInt(temp[0]) * 60 + parseInt(temp[1]);
}

function detailTimeRenderer(minutes) {
	var hourOfDay = Math.floor(minutes / 60);
	var stringHour = hourOfDay+"";
	if(stringHour.length < 2) {
		stringHour = "0" + stringHour;
	}
	
	
	var minuteOfHour = minutes % 60;
	var stringMinute = minuteOfHour+"";
	if(stringMinute.length < 2) {
		stringMinute = "0" + stringMinute;
	}
	
	return stringHour + ":" + stringMinute;
}

</script>

<div id="RailTimetableDetailDiv">
<html:form action="RailTimetableDetail.do" styleId="RailTimetableDetailForm">
	<html:hidden property="formAction" value="" />
	<html:hidden property="railTimetableDetails.railTimetableId" value="${railTimetableId}" />
	
	<!-- begin Border-Table -->
	<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
		style="padding: 0px; margin: 0px; width: auto;">
		<tr><td>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<th class="classicblue left">
						<fmt:message key="railtimetable.text.departure" />
					</th>
					<th class="classicblue left">
						<c:if test="${formType == 'new'}">
							<html:text styleId="railTimetableDetails.depRailwayStationName" property="railTimetableDetails.depRailwayStation.railwayStationName"
								style="width: 95%;" />
						</c:if>
						<c:if test="${formType == 'existing'}">	
							<c:out value="${formBean.depRailwayStation.railwayStationName}" />
						</c:if>
					</th>
					<th class="classicblue left">
						<fmt:message key="railtimetable.text.arrival" />
					</th>
					<th class="classicblue left">
						<c:if test="${formType == 'new'}">
							<html:text styleId="railTimetableDetails.arrRailwayStationName" property="railTimetableDetails.arrRailwayStation.railwayStationName"
								value="${formBean.arrRailwayStation.railwayStationName}" style="width: 95%;" />
						</c:if>
						<c:if test="${formType == 'existing'}">
							<c:out value="${formBean.arrRailwayStation.railwayStationName}" />
						</c:if>
					</th>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<table class="classic"
				style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<td class="label left">
						<fmt:message key="railtimetable.text.deptrainno" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.depTrainNo" property="railTimetableDetails.depTrainNo"
							value="${formBean.depTrainNo}" maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_DEP_TRAIN_NO) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="railtimetable.text.arrtrainno" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.arrTrainNo" property="railTimetableDetails.arrTrainNo"
							value="${formBean.arrTrainNo}" maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_ARR_TRAIN_NO) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="railtimetable.text.depswitchingtimemin" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.depSwitchingTimeMin" property="form.depSwitchingTimeMin" value="${form.depSwitchingTimeMin}"
						maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_DEP_SWITCHING_TIME_MIN) %>" />
						<html:hidden styleId="hiddenDepSwitchingTimeMin" property="railTimetableDetails.depSwitchingTimeMin" value="${formBean.depSwitchingTimeMin}" />
					</td>
					<td class="label left">
						<fmt:message key="railtimetable.text.arrswitchingtimemin" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.arrSwitchingTimeMin" property="form.arrSwitchingTimeMin" value="${form.arrSwitchingTimeMin}"
							maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_ARR_SWITCHING_TIME_MIN) %>" />
						<html:hidden styleId="hiddenArrSwitchingTimeMin" property="railTimetableDetails.arrSwitchingTimeMin" value="${formBean.arrSwitchingTimeMin}" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="railtimetable.text.deptimemin" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.depTimeMin" property="form.depTimeMin" value="${form.depTimeMin}"
							maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_DEP_TIME_MIN) %>" />
						<html:hidden styleId="hiddenDepTimeMin" property="railTimetableDetails.depTimeMin" value="${formBean.depTimeMin}" />
					</td>
					<td class="label left">
						<fmt:message key="railtimetable.text.arrtimemin" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.arrTimeMin" property="form.arrTimeMin" value="${form.arrTimeMin}"
							maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_ARR_TIME_MIN) %>" />
						<html:hidden styleId="hiddenArrTimeMin" property="railTimetableDetails.arrTimeMin" value="${formBean.arrTimeMin}" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="railtimetable.text.timetablecomment" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.timetableComment" property="railTimetableDetails.timetableComment"
							value="${formBean.timetableComment}" maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_TIMETABLE_COMMENT) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="railtimetable.text.arroffsetdays" />:
					</td>
					<td>
						<html:text styleId="railTimetableDetails.arrOffsetDays" property="railTimetableDetails.arrOffsetDays"
							value="${formBean.arrOffsetDays}" maxlength="<%= String.valueOf(RailTimetableEntity.DBSIZE_ARR_OFFSET_DAYS) %>" style="width: 180;" />
					</td>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<table style="border: none; margin-top: 10px; width: 100%;">
				<tr><td class="buttons right">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save" />" id="buttonSave">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel" />" id="buttonCancel">
				</td></tr>
			</table>
		</td></tr>
	</table>
</html:form>
</div>
<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.RailDistanceDetailAction" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.RailDistanceDetailEntryAction" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.RailDistanceDetailForm" %>
<%@ page import="com.crh.logon.admin.web.beans.RailDistanceDetailBean" %>
<%@ page import="com.crh.logon.persistence.entities.common.RailDistanceEntity" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<c:set var="form" value="${sessionScope.admin_RailDistanceDetailForm}"/>
<c:set var="formBean" value="${sessionScope.admin_RailDistanceDetailForm.railDistanceDetails}"/>

<c:set var="formType"><%= RailDistanceDetailBean.FORM_TYPE_NEW %></c:set>
<c:set var="railDistanceId" value=""/>
<c:if test="${(not empty param['railDistanceDetails.railDistanceId']) || ((not empty param['depStationId']) && (not empty param['arrStationId']))}">
	<c:set var="formType"><%= RailDistanceDetailBean.FORM_TYPE_EXISTING %></c:set>
	<c:set var="railDistanceId" value="${param['railDistanceDetails.railDistanceId']}"/>
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RailTimetableAjaxService.js'/>"></script>
<script type="text/javascript">

Ext.onReady( function() {

	Ext.get('buttonCancel').on('click', function() {
		hidePopup();
	} );
	
	Ext.get('buttonSave').on('click', function() {
		var railDistanceDetailDiv = Ext.get('RailDistanceDetailDiv');

		<c:if test="${formType == 'new'}">
		var submitParams = {'depStationId': detailStationFromCombo.getValue(),
			'arrStationId': detailStationToCombo.getValue() };
		</c:if>
		<c:if test="${formType == 'existing'}">	
		var submitParams = {'railDistanceDetails.railDistanceId': '<c:out value="${formBean.railDistanceId}" />'};
		</c:if>
		
		storeValueAsHiddenField('RailDistanceDetailForm', 'formAction', '<%= RailDistanceDetailAction.FORWARD_SAVE %>');
		addFormElements(submitParams, "<%= RailDistanceDetailForm.SESSION_RAIL_DISTANCE_DETAIL_FORM_NAME %>");

		railDistanceDetailDiv.load( {
			url: '<c:url value="RailDistanceDetail.do"/>',
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
			applyTo: 'railDistanceDetails.depStationName',
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
			applyTo: 'railDistanceDetails.arrStationName',
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
	
} );
	
</script>

<div id="RailDistanceDetailDiv">
<html:form action="RailDistanceDetail.do" styleId="RailDistanceDetailForm">
	<html:hidden property="formAction" value="" />
	<html:hidden property="railDistanceDetails.railDistanceId" value="${railDistanceId}" />
	
	<!-- begin Border-Table -->
	<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
		style="padding: 0px; margin: 0px; width: auto;">
		<tr><td>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<th class="classicblue left">
						<c:if test="${formType == 'new'}">
							<html:text styleId="railDistanceDetails.depStationName" property="railDistanceDetails.depRailwayStation.railwayStationName"
								style="width: 95%;" />
						</c:if>
						<c:if test="${formType == 'existing'}">	
							<c:out value="${formBean.depRailwayStation.railwayStationName}" />
						</c:if>
					</th>
					<th class="classicblue left">
						<c:if test="${formType == 'new'}">
							<html:text styleId="railDistanceDetails.arrStationName" property="railDistanceDetails.arrRailwayStation.railwayStationName"
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
						<fmt:message key="raildistance.text.raildistance" />:
					</td>
					<td>
						<html:text styleId="railDistanceDetails.tariffDistanceM" property="railDistanceDetails.tariffDistanceM"
							value="${formBean.tariffDistanceM}" maxlength="<%= String.valueOf(RailDistanceEntity.DBSIZE_TARIFF_DISTANCE_M) %>" style="width: 180;" />
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
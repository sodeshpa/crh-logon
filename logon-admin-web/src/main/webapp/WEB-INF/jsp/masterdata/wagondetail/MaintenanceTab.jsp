<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<c:set var="wagonDetailMaintenance" scope="page" value="${admin_WagonDetailForm.wagonDetailMaintenance}" />

<script type="text/javascript">

function recalculate(){
	if (document.getElementById('wagonDetailMaintenance.selectedMaintRhythmTypeCd').value != '0') {
		var params = ({recalculateDates : ''});
		addFormElements(params, "WagonDetailForm");
		var diva = Ext.get('tableDiv');
		diva.load({
			url: 'WagonDetail.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}
}
</script>

<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_MAINTENANCE_TAB%>"/>

	<html:select size="1" styleId="wagonDetailMaintenance.selectedMaintRhythmTypeCd" property="wagonDetailMaintenance.selectedMaintRhythmTypeCd" style="width: 250;" styleClass="${defaultTextfieldStyleClass}">
	<c:forEach var="maintRhythmTypeRow" items="${wagonDetailMaintenance.maintRhythmTypeList}">
		<html:option value="${maintRhythmTypeRow.maintRhythmTypeTxPK.maintRhythmTypeBs.maintRhythmTypeCd}"><c:out value="${maintRhythmTypeRow.txt}"/></html:option>
	</c:forEach>
	</html:select>
	
	<input value="<fmt:message key="buttons.recalculate"/>" class="ViewButtonStyle" onclick="recalculate();"/>
	
	<div id="tableDiv">
		<jsp:include page="WagonMaintenanceTable.jsp"/>
	</div>
	
	<div id="dmgDiv">
		<jsp:include page="WagonMaintenanceDmgTile.jsp"/>
	</div>

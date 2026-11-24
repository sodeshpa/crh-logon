<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: EditAssignment.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.constants.Constant"%>
<% com.crh.logon.order.web.forms.dispo.AssignmentEditForm aeForm =(com.crh.logon.order.web.forms.dispo.AssignmentEditForm)session.getAttribute("order_dispo_AssignmentEditForm"); %>

<c:set var="ASSIGN_STATUS_TR_TRANSMITTED"><%=Constant.ASSIGN_STATUS_TR_TRANSMITTED%></c:set>
<c:set var="datePattern" value="HH:mm:ss" scope="request"/>

<script type="text/javascript">
	eaMap = null;
	mapPrefix = null;

	function submitPage(params) {
		addFormElements(params, "AssignmentEditForm");
		var diva = Ext.get('DivEditAssignment');
		Ext.Updater.defaults.showLoadIndicator = false;
		diva.load({
			url: '../dispo/AssignmentEdit.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}
	
	function editAssignmentCancel() {
		hidePopup();
		
		if (typeof orderUpdated !== "undefined" && orderUpdated) {
			orderUpdated = false;
			refreshAssignments();
		}
	}
	
</script>

<html:form action="dispo/AssignmentEdit.do" onsubmit="return false;" styleId="AssignmentEditForm">
<table class="classicblue" style="border:none; width:auto"  class="big_form2">
<tr>
	<th colspan="2">
		<fmt:message key="dispo.texte.editassignment"/> -
		<c:out value="${order_dispo_AssignmentEditForm.truckName}" />
	</th>
</tr>
<tr>
	<td class="popupcontent" width="900">
		<table width="100%" class="big_form2" >
			<tr>
				<td colspan='1' class='border'>
					<%-- map --%>
					<div style='width: 900px; height: 180px; overflow:hidden'  id='eaMapDiv'>
					</div>
				</td>
			</tr>
		</table>
		<table style="width: 100%;">
			<tr>
				<td  class='labelblue'>
					<fmt:message key="dispo.texte.notes"/>
				</td>
			</tr>
			<tr>
				<td class='label'>
					<fmt:message key="dispo.texte.dispatcher"/>:
				</td>
				<td class='label'>
					<fmt:message key="dispo.texte.Driver"/>:
				</td>
			</tr>
			<tr>
				<td>
					<html:textarea rows="1" style="width: 400px;height:60px" styleClass='textfield' styleId="carrierDispatchingNote" property="assignment.carrierDispatchingNote"/>
				</td>
				<td>
					<html:textarea rows="1" style="width: 400px;height:60px" styleClass='textfield' styleId="carrierTruckDriverNote" property="assignment.carrierTruckDriverNote"/>
				</td>
			</tr>
		</table>
		<div id="DivEditAssignment">
			<jsp:include page="EditAssignmentTable.jsp"/>
		</div>
	</td>
</tr>
<tr>
	<td>
		<table width="100%" class="big_form2" >
			<tr>
				<c:if test="${order_dispo_AssignmentEditForm.externalOrder && (order_dispo_AssignmentEditForm.assignment.assignmentId != null)}">	
					<td class="buttons">
						<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.delete"/>" onclick="submitPage({'delete': ''})"/>
					</td>
				</c:if>
				<td class="buttons right">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="editAssignmentCancel()"/> 
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="submitPage({'save': ''})"/>
					<c:if test="${order_dispo_AssignmentEditForm.assignment.assignTrStatus.assignTrStatusCd == ASSIGN_STATUS_TR_TRANSMITTED}">	
						<input type="button" class="ViewButtonStyle" value="<fmt:message key="dispo.button.saveresubmit"/>" onclick="submitPage({'save': '', 'resubmit':''})"/>
					</c:if>
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
</html:form>



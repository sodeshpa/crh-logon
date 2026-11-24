<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<table style="width: 100%;">
	<tr>
		<td style="vertical-align: top; width: 50%;">
			<c:if test="${not empty sessionScope.admin_ManualPDATriggerForm.methodParameters}">
				<div class="labelblue">Header data</div>
			</c:if>
			<table style="width: 100%">
				<c:forEach var="parameter" items="${sessionScope.admin_ManualPDATriggerForm.sortedMethodParameters}">
					<tr>
						<td style="width: 50%;">
							<c:out value="${parameter.key} (${parameter.value})"/>
						</td>
						<td>
							<html:text name="admin_ManualPDATriggerForm" property="parameterValue(${parameter.key})" size="30" styleClass="textfield"/>
						</td>
					</tr>
				</c:forEach>
			</table>	
		</td>
		<td style="vertical-align: top; width: 50%;">			
			<c:if test="${not empty sessionScope.admin_ManualPDATriggerForm.parameters}">
				<div class="labelblue">Body data</div>
			</c:if>
			<table style="width: 100%">
				<c:forEach var="parameter" items="${sessionScope.admin_ManualPDATriggerForm.sortedParameters}">
					<tr style="width: 100%">
						<td style="width: 50%;">
							<c:out value="${parameter.key} (${parameter.value})"/>
						</td>
						<td>
							<html:text name="admin_ManualPDATriggerForm" property="parameterValue(${parameter.key})" size="30" styleClass="textfield"/>
						</td>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
</table>

<%@ include file="../includeTLDs.jsp"%>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld"
	prefix="crh-common"%>
<%@ page
	import="com.crh.logon.taglib.fallbacklogic.DefaultFallBackLogicTag"%>
<%@page
	import="com.crh.logon.order.web.forms.dispokarte.DispokarteForm"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_dispokarte_DispokarteForm}" />

<table cellpadding="0" cellspacing="0"
	style="margin: 0px; border-collapse: collapse;">

	<tr>
		<td style="width: 240px" valign="top"><%-- truck type priorities --%>
		<span class="label"><fmt:message
			key="dispokarte.texte.truckPriorities" /></span>
			
			
		<div class="scrollist" id="DivTruckTypes" style="height: 280px; width: 240px">
			<table class="classic" style="border: none"
				style="margin: 0px; border-collapse: collapse; padding: 0px;">
				<tr>
					<th width="20"><img
						src="<crh-common:url value='/images/active.png'/>" /></th>
					<th width="20"><img
						src="<crh-common:url value='/images/delete.gif'/>" /></th>
					<th width="20"><fmt:message key="dispokarte.texte.prio" /></th>
					<th width="180"><fmt:message key="dispokarte.texte.type" /></th>
				</tr>
				<c:forEach var="type" varStatus="status"
					items="${order_dispokarte_DispokarteForm.truckTypeWrapperList}">
					<tr class="zebra<c:out value="${status.count % 2}"/>">
						<td valign="middle"><html:radio value="true"
							property="truckAllowance[${status.count-1}]"
							styleId="truckAllowanceTrue${status.count-1}"
							onchange="setupPriority('truck',${status.count-1})" /></td>
						<td valign="middle"><html:radio value="false"
							property="truckAllowance[${status.count-1}]"
							styleId="truckAllowanceFalse${status.count-1}"
							onchange="setupPriority('truck',${status.count-1})" /></td>
						<td valign="middle"><c:if test='${type.priority != 0}'>
							<html:text property="truckPrio[${status.count-1}]"
								style="width:20px;" maxlength="2" size="2"
								styleId="truckPrio${status.count-1}" />
						</c:if> <c:if test='${type.priority == 0}'>
							<html:text property="truckPrio[${status.count-1}]"
								style="width:20px;" disabled="true" maxlength="2" size="2"
								styleId="truckPrio${status.count-1}" />
						</c:if></td>
						<td valign="middle" style="width: 300px"><c:out
							value="${type.typeName}" /></td>
					</tr>
				</c:forEach>
			</table>	
		
		</div>
		</td>
		<td style="width: 200px"><span class="label"><fmt:message
			key="dispokarte.texte.kundenunterschrift" /></span>
		<div><html:select size="1" property="selectedSignatureTypeCd"
			style="width:140px;">
			<c:forEach var="row"
				items="${order_dispokarte_DispokarteForm.signatureTypeList}">
				<html:option
					value="${row.signatureTypeTxPK.signatureTypeBs.signatureTypeCd}">
					<c:out value="${row.txt}" />
				</html:option>
			</c:forEach>
		</html:select></div>
		</td>
	</tr>
</table>


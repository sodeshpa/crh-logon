<%@ include file="../includeTLDs.jsp" %>

<c:set var="selectedOrderStatusesString"
       value='${order_dispo_DispoForm.selectedOrderStatusesString}'/>

<c:choose>
	<c:when test="${selectedOrderStatusesString == 'all'}">
		orderStatusList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedOrderStatusesString == 'none'}">
		orderStatusList.setValue("<fmt:message key="dispo.button.inactive" />");
	</c:when>
	<c:otherwise>
		orderStatusList.setValue("<c:out value='${selectedOrderStatusesString}' escapeXml="false"/>");
	</c:otherwise>
</c:choose>
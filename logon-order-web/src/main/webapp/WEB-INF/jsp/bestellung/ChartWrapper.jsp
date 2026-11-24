<%@ include file="../includeTLDs.jsp" %>

<c:choose>
	<c:when test="${hideFormTagForChart}">
		<jsp:include page="Chart.jsp" flush="true" />
	</c:when>
	<c:otherwise>
 		<!--  <%-- Struts needs this, found no better way to do it --%> 
		<html:form action="bestellung/TruckPlanningEntry.do">
		-->
		<%--
		--%>

		<jsp:include page="Chart.jsp" />
		<!--  
		</html:form>
		-->
 	</c:otherwise>
</c:choose>
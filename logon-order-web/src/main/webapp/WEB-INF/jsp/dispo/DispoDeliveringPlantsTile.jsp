<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: DispoDeliveringPlantsTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>

<c:set var="selectedDeliveringPlantsString"
       value='${order_dispo_DispoForm.selectedDeliveringPlantsString}'/>

<c:choose>
	<c:when test="${selectedDeliveringPlantsString == 'all'}">
		deliveringPlantList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedDeliveringPlantsString == 'none'}">
		deliveringPlantList.setValue("<fmt:message key="dispo.button.inactive" />");
	</c:when>
	<c:otherwise>
		deliveringPlantList.setValue("<c:out value='${selectedDeliveringPlantsString}' escapeXml="false"/>");
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${order_dispo_DispoForm.numberOfSelectedDeliveringPlants == 1}">
		deliveringPlantList.singleDeliveringPlantSelected = true;
	</c:when>
	<c:otherwise>
		deliveringPlantList.singleDeliveringPlantSelected = false;
	</c:otherwise>
</c:choose>	

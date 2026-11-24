<%@ include file="../includeTLDs.jsp" %>

<c:set var="dispoForm" value="${order_dispo_DispoForm}"/>

var displayFieldValue = "";

<c:if test="${dispoForm.hidePlanningTrucks}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.planning" /> ";
</c:if>
<c:if test="${dispoForm.hideRealTrucks}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.real" /> ";
</c:if>
<c:if test="${dispoForm.hideInactiveTrucks}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.inactive" /> ";
</c:if>
<c:if test="${dispoForm.hideTrucksWithoutDeliveries}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.withoutdeliveries" /> ";
</c:if>
<c:if test="${dispoForm.hideScheduledCustorders}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.scheduledcustorders" /> ";
</c:if>
<c:if test="${dispoForm.hideClosedCustorders}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.closedcustorders" /> ";
</c:if>
<c:if test="${dispoForm.hideCustorderHeaders}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.custorderheader" /> ";
</c:if>
<c:if test="${dispoForm.hideTrucksAndOrderOfPartners}">
	displayFieldValue += "<fmt:message key="dispo.texte.trucks.hide.trucksandordersofpartners" /> ";
</c:if>
if (displayFieldValue === "") {
	displayFieldValue = "<fmt:message key="dummy.select" />";
}

displayField.setValue(displayFieldValue);

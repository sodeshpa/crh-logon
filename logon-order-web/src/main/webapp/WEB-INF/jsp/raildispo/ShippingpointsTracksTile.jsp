<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: ShippingpointsTracksTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>
<c:set var="selectedShippingpointsString" value='${order_raildispo_DispoForm.selectedShippingpointsString}'/>
<c:choose>
	<c:when test="${selectedShippingpointsString == 'all'}">
shippingpointList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedShippingpointsString == 'inactive'}">
shippingpointList.setValue("<fmt:message key="dispo.button.uncheck" />");
	</c:when>
	<c:otherwise>
shippingpointList.setValue("<c:out value='${selectedShippingpointsString}'/>");
	</c:otherwise>
</c:choose>
<c:set var="selectedTracksString" value='${order_raildispo_DispoForm.selectedTracksString}'/>
<c:choose>
	<c:when test="${selectedTracksString == 'all'}">
tracksList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedTracksString == 'inactive'}">
tracksList.setValue("<fmt:message key="dispo.button.uncheck" />");
	</c:when>
	<c:otherwise>
tracksList.setValue("<c:out value='${selectedTracksString}'/>");
	</c:otherwise>
</c:choose>
Ext.getDom('NoTrackField').checked = <c:out value='${order_raildispo_DispoForm.showNoTrack}'/>;

statusString = '';
statusCnt = 0;
<c:if test='${order_raildispo_DispoForm.statusIncoming}'>
	statusString += '<fmt:message key="raildispo.texte.status.incoming" /> ';
	statusCnt++;
</c:if>

<c:if test='${order_raildispo_DispoForm.statusAtSp}'>
	statusString += '<fmt:message key="raildispo.texte.status.atSp" /> ';
	statusCnt++;
</c:if>

<c:if test='${order_raildispo_DispoForm.statusLoaded}'>
	statusString += '<fmt:message key="raildispo.texte.status.loaded" /> ';
	statusCnt++;
</c:if>

<c:if test='${order_raildispo_DispoForm.statusTransmitted}'>
	statusString += '<fmt:message key="raildispo.texte.status.transmitted" />';
	statusCnt++;
</c:if>
if (statusCnt == 0) {
	statusList.setValue('<fmt:message key="dispo.button.uncheck" />');
}
else if (statusCnt == 4) {
	statusList.setValue('<fmt:message key="dispo.button.check" />');
}
else {
	statusList.setValue(statusString);
}
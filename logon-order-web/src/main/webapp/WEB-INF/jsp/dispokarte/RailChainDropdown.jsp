<%@ include file="../includeTLDs.jsp" %>
<!--  <%-- Struts needs this, fond no better way to do it --%> 
<html:form action="dispokarte/Dispokarte.do">
-->

<html:select property="railChainShippingpointId" style="width:230px;">
	<html:option value="-1">-</html:option>
	<c:forEach var="sp" items="${order_dispokarte_DispokarteForm.shippingpointListForRailChainPlant}">
		<html:option value="${sp.shippingpointId}"><c:out value="${sp.shippingpointName}"/></html:option>
	</c:forEach>
</html:select>
										
<!--  <%-- Struts needs this, fond no better way to do it --%> 
</html:form>
-->
										
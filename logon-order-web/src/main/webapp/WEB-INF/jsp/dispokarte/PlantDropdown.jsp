<%@ include file="../includeTLDs.jsp" %>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispokarte_DispokarteForm}" />

<c:if test="${order_dispokarte_DispokarteForm.plantSelection}">
	<span class="label"><fmt:message key="dispokarte.texte.deliveryForPlant" /></span>
	<div style="margin-top: 4px">
		<html:select property="plantPlantCd" style="width:230px;" styleId="plantDD">
			<html:option value="-1">-</html:option>
			<c:forEach var="plant" varStatus="plantStatus" items="${order_dispokarte_DispokarteForm.plantList}">
				<html:option value="${plant.plantCd}">
					<c:out value="${plant.plantName}" />
				</html:option>
			</c:forEach>
		</html:select>
	</div>
</c:if>

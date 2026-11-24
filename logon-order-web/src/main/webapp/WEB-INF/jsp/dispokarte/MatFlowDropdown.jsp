<%@ include file="../includeTLDs.jsp"%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispokarte_DispokarteForm}" />

<c:if test="${order_dispokarte_DispokarteForm.displayMatChainSelection}">
	<span class="label"><fmt:message key="dispokarte.texte.matFlowSP" /></span>
	<div style="margin-top: 4px">
		<html:select property="matChainPlantCd" style="width:230px;" onchange="modifyMatChainPlant()" styleId="matChainPlantDD">
			<html:option value="-1">-</html:option>
			<c:forEach var="plant" varStatus="plantStatus"
				items="${order_dispokarte_DispokarteForm.plantList}">
				<html:option value="${plant.plantCd}">
					<c:out value="${plant.plantName}" />
				</html:option>
			</c:forEach>
		</html:select>
	</div>
	<div style="margin-top: 4px">
		<html:select property="matChainShippingpointId" style="width:230px;">
			<html:option value="-1">-</html:option>
			<c:forEach var="sp" items="${order_dispokarte_DispokarteForm.shippingpointListForMatChainPlant}">
				<html:option value="${sp.shippingpointId}">
					<c:out value="${sp.shippingpointName}" />
				</html:option>
			</c:forEach>
		</html:select>
	</div>
</c:if>

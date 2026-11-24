<%@ include file="../includeTLDs.jsp" %>
						<select onchange="selectShippingPoint()" name="selectedShippingPointId" id="selectedShippingPointId" style="margin:0px 0px 10px 0px;  padding:0px; width:100%;">
						<option value="">Select...</option>
						<c:forEach var="entry" items="${order_auftrag_AuftragBearbeitungForm.selectableShippingPoints}">
						<option value='<c:out value="${entry.shippingpointId}"/>'><c:out value="${entry.shippingpointName}"/></option>
						</c:forEach>
</select>
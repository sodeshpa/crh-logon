<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<%@ taglib uri="http://com.crh.logon/tags/pdf-rendering.tld" prefix="pdf"%>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.crh.logon.business.services.beans.pdf.deliverynote.*" %>
<%@ page import="com.crh.logon.business.factories.PDFPageFactory" %>
<fmt:setBundle basename="LitDict"/>

<%
	String COLON = ": ", DASH = " / ", SPACE = " ";

	DeliveryNotePdfDataSource data = (DeliveryNotePdfDataSource) request.getAttribute(PDFPageFactory.REPORT_DATASOURCE);
	BodyData body = data.getBody();
%>

<div class="orderitem-container">
	<table class="orderitem-table" >
		<% if (body.getOrderItem().getMaterialLayoutType() == MaterialLayoutType.BAG) { %>
			<tr>
				<td colspan="2"><pdf:cut width="355"><fmt:message key="pdf.material"/><%= COLON %></pdf:cut></td>
				<td class="right"><pdf:cut width="55"><fmt:message key="pdf.ordered.quantity"/><%= COLON %></pdf:cut></td>
				<td class="right"><pdf:cut width="55"><fmt:message key="pdf.delivered.quantity"/><%= COLON %></pdf:cut></td>			
				<td class="right"><pdf:cut width="55"><fmt:message key="pdf.weights.net"/><%= COLON %></pdf:cut></td>
			</tr>
		<% } else { %>
			<tr>
				<td colspan="2">
					<pdf:cut width="<%= body.getOrderItem().isShowReturn() ? 345 : 385 %>">
						<fmt:message key="pdf.material"/><%= COLON %>
					</pdf:cut>
				</td>
				<td class="right"><pdf:cut width="40"><fmt:message key="pdf.weights.gross"/><%= COLON %></pdf:cut></td>
				<td class="right"><pdf:cut width="40"><fmt:message key="pdf.weights.tare"/><%= COLON %></pdf:cut></td>
				<% if (body.getOrderItem().isShowReturn()) { %>
					<td class="right"><pdf:cut width="40"><fmt:message key="pdf.weights.return"/><%= COLON %></pdf:cut></td>
				<% } %>		
				<td class="right"><pdf:cut width="55"><fmt:message key="pdf.weights.net"/><%= COLON %></pdf:cut></td>
			</tr>
		<% } %>
		<% for (BagItemData bag : body.getOrderItem().getBagItems()) { %>
				<tr>
					<td><pdf:cut width="180"><%= bag.getMaterialName() %></pdf:cut></td>
					<td width="175"><%= bag.getMaterialDescription() %></td>
					<td class="right"><pdf:cut width="55"><%= bag.getOrderedQuantity() %></pdf:cut></td>
					<td class="right"><pdf:cut width="55"><%= bag.getDeliveredQuantity() %></pdf:cut></td>
					<td class="right"><pdf:cut width="55"><%= bag.getNetQuantity() %></pdf:cut></td>
				</tr>
				<tr>
					<td><pdf:cut width="180"><%= bag.getSapMaterialNumber() %></pdf:cut></td>
					<td><pdf:cut width="175"><%= bag.getProductionPlant() + SPACE + bag.getMixDesign() %></pdf:cut></td>
					<td colspan="3"></td>
				</tr>
		<% } %>
		<% for (BulkItemData bulk : body.getOrderItem().getBulkItems()) { %>
				<tr>
					<td><pdf:cut width="180"><%= bulk.getMaterialName() %></pdf:cut></td>
					<td width="<%= body.getOrderItem().isShowReturn() ? 165 : 205 %>">
						<%= bulk.getMaterialDescription() %>
					</td>
					<td class="right"><pdf:cut width="40"><%= bulk.getGrossWeight() %></pdf:cut></td>
					<td class="right"><pdf:cut width="40"><%= bulk.getTareWeight() %></pdf:cut></td>
					<% if (body.getOrderItem().isShowReturn()) { %>
						<td class="right">
							<pdf:cut width="40">
								<% if (bulk.renderReturnWeight()) { %>
									<%= bulk.getReturnWeight() %>
								<% } %>
							</pdf:cut>
						</td>
					<% } %>	
					<td class="right"><pdf:cut width="55"><%= bulk.getNetWeight() %></pdf:cut></td>
				</tr>
				<tr>
					<td><pdf:cut width="180"><%= bulk.getSapMaterialNumber() %></pdf:cut></td>
					<td>
						<pdf:cut width="<%= body.getOrderItem().isShowReturn() ? 165 : 205 %>">
							<%= bulk.getProductionPlant() + SPACE + bulk.getMixDesign() %>
						</pdf:cut>
					</td>
					<td colspan="<%= body.getOrderItem().isShowReturn() ? "4" : "3" %>"></td>
				</tr>
		<% } %>
		<% for (ServiceItemData service : body.getOrderItem().getServiceItems()) { %>
				<tr>
					<td><pdf:cut width="180"><%= service.getMaterialName() %></pdf:cut></td>
					<td width="<%= body.getOrderItem().isShowReturn() ? 165 : 205 %>">
						<%= service.getMaterialDescription() %>
					</td>
					<td colspan="<%= body.getOrderItem().isShowReturn() ? "3" : "2" %>"></td>
					<td class="right"><pdf:cut width="55"><%= service.getNetWeight() %></pdf:cut></td>
				</tr>
		<% } %>
	</table>
	<% if (body.getOrderItem().renderReturnedPallets()) { %>
		<div>
			<fmt:message key="pdf.weights.returned.pallets"/>
			<%= COLON + body.getOrderItem().getNumberOfReturnedPallets()%>
		</div>
	<% } %>
	<% if (body.getOrderItem().renderDeliveryTypeInfo()) { %>
		<div>
			<%= body.getOrderItem().getDeliveryTypeInfo() %>
		</div>
	<% } %>
</div>
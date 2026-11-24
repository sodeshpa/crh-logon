<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<%@ taglib uri="http://com.crh.logon/tags/pdf-rendering.tld" prefix="pdf"%>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.crh.logon.business.services.beans.pdf.deliverynote.*" %>
<%@ page import="com.crh.logon.order.web.utils.pdfgenerator.ImageBase64Loader"%>
<%@ page import="com.crh.logon.business.factories.PDFPageFactory" %>
<fmt:setBundle basename="LitDict"/>

<%
	String COLON = ": ", DASH = " / ", SPACE = " ";

	DeliveryNotePdfDataSource data = (DeliveryNotePdfDataSource) request.getAttribute(PDFPageFactory.REPORT_DATASOURCE);
	FooterData footer = data.getFooter();
	BodyData body = data.getBody();
%>

<div id="transpot-data">
	<div  class="text7">
		<% if (body.getTransport().isCarrierResponsible()) { %>
			<fmt:message key="pdf.transport_responsible.carrier"/>
		<% } else if (body.getTransport().isCrhResponsible()) { %>
			<fmt:message key="pdf.transport_responsible.crh"/>
		<% } else if (body.getTransport().isCustomerResponsible()) { %>
			<fmt:message key="pdf.transport_responsible.customer"/>
		<% } %>
	</div>
	<% if (body.getTransport().renderChromatComment()) { %>
		<div class="text7">
			<%= body.getTransport().getChromatReductionComment() %>
		</div>
	<% } %>
</div>

<div class="ruler"></div>

<div id="remark-data">
	<div>
		<fmt:message key="pdf.comment"/><%= COLON %>
	</div>
	<div class="text8">
	<% if (footer.getRemarkData().renderComment()) { %>
		<%= footer.getRemarkData().getComment() %>
	<% } %>	
	</div>
	<div>
	<% if (footer.getRemarkData().renderFixRemark()) { %>
		<%= footer.getRemarkData().getFixRemark() %>
	<% } %>
	</div>
	<table id="signature-container">
		<tr id="signature-fields">
			<td>
				<% if (!footer.getRemarkData().renderDriverSignature()) { %>
					<% if (footer.getRemarkData().renderDispatcherName()) { %>
						<pdf:cut width="173"><%= footer.getRemarkData().getDispatcherName() %></pdf:cut>
					<% } %>
					<% if (footer.getRemarkData().renderDispatcherTel()) { %>
						<pdf:cut width="173"><%= footer.getRemarkData().getDispatcherTel() %></pdf:cut>
					<% } %>
					<% if (footer.getRemarkData().renderDispatcherId()) { %>
						<pdf:cut width="173"><%= footer.getRemarkData().getDispatcherId() %></pdf:cut>
					<% } %>
				<% } %>
			</td>
			<td>
				<% if (!footer.getRemarkData().renderDriverSignature()) { %>
					<% if (footer.getRemarkData().renderDriverName()) { %>
						<pdf:cut width="173"><%= footer.getRemarkData().getDriverName() %></pdf:cut>
					<% } %>
					<% if (footer.getRemarkData().renderDriverCardNo()) { %>
						<pdf:cut width="173"><%= footer.getRemarkData().getDriverCardNo() %></pdf:cut>
					<% } %>
					<% if (footer.getRemarkData().renderDriverId()) { %>
						<pdf:cut width="173"><%= footer.getRemarkData().getDriverId() %></pdf:cut>
					<% } %>
				<% } else { %>
						<img class="signature-image" src="<%= ImageBase64Loader.getBase64CssImageContent(
						footer.getRemarkData().getDriverSignature()) %>"/>
				<% } %>
			</td>
			<td>
				<% if (footer.getRemarkData().renderCustomerSignature()) { %>
						<img class="signature-image" src="<%= ImageBase64Loader.getBase64CssImageContent(
						footer.getRemarkData().getCustomerSignature()) %>"/>
				<% } %>
			</td>
		</tr>
	
		<tr>
			<td>
				<% if (!footer.getRemarkData().renderDriverDispatcherInfo()) { %>
					<pdf:cut width="173">(<fmt:message key="pdf.coll.crh"/>)</pdf:cut>
				<% } %>
			</td>
			<td>
				<% if (!footer.getRemarkData().renderDriverDispatcherInfo()) { %>
				<pdf:cut width="173">
					(<% if (footer.getRemarkData().renderDriverFullName()) {  %>
						<%= footer.getRemarkData().getDriverFullName() + DASH %>
					<% } %>
					<fmt:message key="pdf.signature.driver"/>)
				</pdf:cut>
				<% } %>
			</td>
			<td>
				<pdf:cut width="173">
					(<% if (footer.getRemarkData().renderCustomerName()) {  %>
						<%= footer.getRemarkData().getCustomerName() + DASH %>
					<% } %>
					<fmt:message key="pdf.signature.customer"/>)
				</pdf:cut>
			</td>
		</tr>
		
	</table>
</div>

<div id="footer-data" class="text7">
	<div>
		<%=footer.getFooterTitle() %>
	</div>
	<div>
		<%=footer.getFooterText() %>
	</div>
	<% if (footer.renderCalibrationData()) { %>
		<div>
			<%=footer.getCalibrationText() %>
		</div>
		<% for (int i = 0; (i < footer.getCalibrationDatas().size()) && (i < 1); i++) { %>
			<pdf:cut width="520">
				<%=footer.getCalibrationDatas().get(i).getTareData() %>
			</pdf:cut>
			<pdf:cut width="520">
				<%=footer.getCalibrationDatas().get(i).getGrossData() %>
			</pdf:cut>
		<% } %>
	<% } %>
</div>
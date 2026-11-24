<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<%@ taglib uri="http://com.crh.logon/tags/pdf-rendering.tld" prefix="pdf"%>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.crh.logon.business.services.beans.pdf.deliverynote.*" %>
<%@ page import="com.crh.logon.order.web.utils.pdfgenerator.ImageBase64Loader"%>
<%@ page import="com.crh.logon.business.factories.PDFPageFactory" %>
<%@ page import="com.crh.logon.business.services.utils.Message" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.util.List" %>
<fmt:setBundle basename="LitDict"/>

<%
	String COLON = ": ", DASH = " / ", SPACE = " ";

	DeliveryNotePdfDataSource data = (DeliveryNotePdfDataSource) request.getAttribute(PDFPageFactory.REPORT_DATASOURCE);
	HeaderData header = data.getHeader();
%>

<table id="address-table" class="text9">
	<%  List<AddressEntryData> addressList = header.getBusinessAddress().getAddressEntryList();
		int cellsPerRow = 3;
		int addressCount = addressList.size();
		int addressRowCount = (addressCount - (addressCount % cellsPerRow)) / cellsPerRow ;
		int currentIndex = 0;
		
		for (int rowNum = 0 ; rowNum < addressRowCount ; rowNum++) { %>
			<tr>
				<% 	int columnNum = 0;
					while(currentIndex < addressCount && columnNum < cellsPerRow) { 
						currentIndex = rowNum * cellsPerRow + columnNum;
						columnNum++;
						AddressEntryData addressEntryData = addressList.get(currentIndex);
				%>
						<td class="address-container" 
							<% 
								if (columnNum == 1) {
									out.print(" style='padding-right: 5px;'");
								} 
								else if (columnNum == cellsPerRow) {
									out.print(" style='padding-left: 5px;'");
								}
								else {
									out.print(" style='padding-right: 5px; padding-left: 5px;'");
								}
							%>
						>
							<%if (addressEntryData.isVisible()) { %>
								<table>
									<tr>
										<td class="border-bottom">
											<%= Message.lookup(data.getPageSettings().getLocale(), addressEntryData.getName()) + COLON%>
										</td>
										<td class="right border-bottom">
											<%= addressEntryData.getId()%>
										</td>
									</tr>
									<tr>
										<td colspan="2">
										
										
										
											<% 
												List<String> fieldList = addressEntryData.getBodyFieldList();
												for (String field : fieldList) {
													if (StringUtils.isNotEmpty(field)) {
											%>
														<pdf:cut width="165"><%= field %></pdf:cut>
											<%
													}
												}
											%>
											
											<%
												// if there is (ship-to) contact data available
												if (addressEntryData.hasContact()) {
											%>
												<fmt:message key="pdf.shipto.contact"/>
												<%= addressEntryData.getContact() %>
											<% 
												}
											%>
											
										</td>
									</tr>
								</table>
							<% } %>
						</td>
				<% } %>
			</tr>
		<% } %>
</table>

<div class="crossboundary-container">
<% if (header.getCrossBoundary().renderBanner()) { %>
	<img class="banner-image" src="<%= ImageBase64Loader.getBase64CssImageContent(
			header.getCrossBoundary().getBannerImage())%>"/>
<% } else { %>
	<table>
		<tr>
			<td class="crossboundary-data">
				<%	if (header.getCrossBoundary().renderNhm()) { %>
					<div>
						<fmt:message key="pdf.nhm.code"/><%= COLON + header.getCrossBoundary().getNhm()%>
					</div>
				<% } %>
				<% if (header.getCrossBoundary().renderCrossBoundary()) { %>
					<div>
						<%= header.getCrossBoundary().getCrossBoundary() %>
					</div>
				<% } %>
			</td>
		</tr>
	</table>
<% } %>
</div>
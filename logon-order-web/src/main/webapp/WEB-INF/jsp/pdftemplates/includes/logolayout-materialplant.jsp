<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<%@ taglib uri="http://com.crh.logon/tags/pdf-rendering.tld" prefix="pdf"%>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.crh.logon.business.services.beans.pdf.deliverynote.*" %>
<%@ page import="com.crh.logon.order.web.utils.pdfgenerator.ImageBase64Loader"%>
<%@ page import="com.crh.logon.business.factories.PDFPageFactory" %>

<%
	DeliveryNotePdfDataSource data = (DeliveryNotePdfDataSource) request.getAttribute(PDFPageFactory.REPORT_DATASOURCE);
	MaterialLogoData materialLogo = data.getBody().getMaterialLogo();
	
	int FIRST_LOGO_WIDTH = 91;
	int GENERIC_LOGO_WIDTH = 126;
	int LOGO_WIDTH = 42;
%>

<% if (materialLogo.renderLogos()) {  %>
<table class="logo-main-container">
	<tr>
		<% 
			for (int i = 0; i < materialLogo.getLogoList().size() ; i++) {
				LogoData logoData = materialLogo.getLogoList().get(i);
				Integer logoWidth = logoData.isGenericLogo()  ? GENERIC_LOGO_WIDTH : i == 0 ? FIRST_LOGO_WIDTH : LOGO_WIDTH;
		%>
			<td class="logo-image-td" rowspan="<%=logoData.isGenericLogo() ? "2" : "1"%>">
				<img 
				style="width: <%=logoWidth + "px"%>;"
				class="logo-image"
				src="<%= ImageBase64Loader.getBase64CssImageContent(logoData.getLogo())%>"/>
			</td>
		<% } %>
	</tr>
	<tr>
		<%
			for (int i = 0; i < materialLogo.getLogoList().size(); i++) {
				LogoData logoData = materialLogo.getLogoList().get(i);
				Integer logoWidth = logoData.isGenericLogo()  ? GENERIC_LOGO_WIDTH : i == 0 ? FIRST_LOGO_WIDTH : LOGO_WIDTH;
				
				if (!logoData.isGenericLogo()) {
		%>
				<td class="logo-text-td">
					<ul style="list-style: none;" class="logo-text-container">
						<% if (logoData.renderText1()) { %>
							<li><%= logoData.getText1() %>
						<% } %>
						<% if (logoData.renderText2()) { %>
							<li><%= logoData.getText2() %>
						<% } %>
						<% if (logoData.renderText3()) { %>
							<li><%= logoData.getText3() %>
						<% } %>
					</ul>
				</td>
		<% 
				}
			}
		%>
	</tr>
</table>
<% } %>
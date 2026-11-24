<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt"%>
<%@ taglib uri="http://com.crh.logon/tags/pdf-rendering.tld"
	prefix="pdf"%>

<%@ page trimDirectiveWhitespaces="true"%>
<%@ page
	import="com.crh.logon.business.services.beans.pdf.deliverynote.*"%>
<%@ page
	import="com.crh.logon.order.web.utils.pdfgenerator.ImageBase64Loader"%>
<%@ page import="com.crh.logon.business.factories.PDFPageFactory"%>
<fmt:setBundle basename="LitDict" />

<%
	String COLON = ": ", DASH = " / ", SPACE = " ";

	DeliveryNotePdfDataSource data = (DeliveryNotePdfDataSource) request.getAttribute(PDFPageFactory.REPORT_DATASOURCE);
	HeaderData header = data.getHeader();
%>

<table id="sorg-data-main-table" class="text7">
	<tr>
		<td id="sorg-crh-logo-container"><img id="sorg-crh-logo"
			src="<%=ImageBase64Loader.getBase64CssImageContent(header.getSalesOrganization().getCrhLogo())%>" />
		</td>
		<td id="sorg-data-address-container">
			<table>
				<tr>
					<td width="120"><%=header.getSalesOrganization().getDestinationAddress1()%></td>
					<td width="85"><%=header.getSalesOrganization().getDestinationTel()%></td>
				</tr>
				<tr>
					<td width="120"><%=header.getSalesOrganization().getDestinationAddress2()%></td>
					<td width="85"><%=header.getSalesOrganization().getDestinationFax()%></td>
				</tr>
				<tr>
					<td width="120"><%=header.getSalesOrganization().getDestinationAddress3()%></td>
					<td width="85"><%=header.getSalesOrganization().getDestinationTax()%></td>
				</tr>
				<tr>
					<td colspan="2" width="205"><%=header.getSalesOrganization().getDestinationEmail()%></td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table id="title-table">
	<tr>
		<td>
			<table class="title-left-section">
				<tr class="text14 bold">
					<td><pdf:cut width="140">
							<%=header.getTitle().getLabel_deliveryNote()%>
						</pdf:cut></td>
					<td class="right"><pdf:cut width="140">
							<%=header.getTitle().getOrderNumber()%>
						</pdf:cut></td>
				</tr>
				<tr class="text7">
					<td><pdf:cut width="140">
							<fmt:message key="pdf.version" /><%=COLON + header.getTitle().getDeliveryNoteDate()%>
						</pdf:cut></td>
					<td class="right"><pdf:cut width="140">
							<%
								if (header.getTitle().renderAltOrderNumber2()) {
							%>
							<%=header.getTitle().getLabel_altOrderNumber2() + COLON
							+ header.getTitle().getAltOrderNumber2() + SPACE%>
							<%
								}
							%>
							<%
								if (header.getTitle().renderAltOrderNumber()) {
							%>
							<%=header.getTitle().getLabel_altOrderNumber() + COLON
							+ header.getTitle().getAltOrderNumber()%>
							<%
								}
							%>
						</pdf:cut></td>
				</tr>
			</table>
		</td>
		<td class="right">
			<table class="title-right-section">
				<tr class="text9">
					<td><pdf:cut width="90">
							<fmt:message key="pdf.plant" /><%=COLON%>
						</pdf:cut></td>
					<td><pdf:cut width="140">
							<%=header.getTitle().getDeliveryPlant()%>
						</pdf:cut></td>
				</tr>
				<tr class="text9">
					<td><pdf:cut width="90">
							<fmt:message key="pdf.shipping.point" /><%=COLON%>
						</pdf:cut></td>
					<td><pdf:cut width="140">
							<%=header.getTitle().getShippingPoint()%>
						</pdf:cut></td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table id="order-header-table" class="text9">
	<tr>
		<td>
			<table class="title-left-section">
				<tr>
					<td>
						<%
							if (header.getOrderHeader().renderTruck()) {
						%> <pdf:cut
							width="150">
							<fmt:message key="pdf.truck.plate.no" />
							<%=COLON
							+ header.getOrderHeader().getGeneralLoadData().getTruckPlateNumber()%>
						</pdf:cut> <%
 	}
 %>
					</td>
					<td class="right bold">
						<%
							if (header.getOrderHeader().renderDistance()) {
						%> <pdf:cut
							width="130">
							<fmt:message key="pdf.distance" /><%=COLON + header.getOrderHeader().getDistance() + SPACE%><fmt:message
								key="pdf.km" />
						</pdf:cut> <%
 	}
 %>
					</td>
				</tr>
				<%
					if (header.getOrderHeader().renderTrailer()) {
				%>
				<tr>
					<td colspan="2"><pdf:cut width="280">
							<fmt:message key="pdf.trailer.plate.no" /><%=COLON
							+ header.getOrderHeader().getGeneralLoadData().getTrailerPlateNumber()%>
						</pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderAdditionalVehicleData()) {
				%>
				<tr>
					<td colspan="2"><pdf:cut width="280">
							<%
								if (header.getOrderHeader().renderBimodalWagonNumber()) {
							%>
							<fmt:message key="pdf.wagon.no" />
							<%=COLON
								+ header.getOrderHeader().getGeneralOrderData().getBimodalWagonNumber()
								+ SPACE%>
							<%
								}
							%>
							<%
								if (header.getOrderHeader().renderContainerNumbers()) {
							%>
							<fmt:message key="pdf.container.no" />
							<%=COLON
								+ header.getOrderHeader().getGeneralOrderData().getContainerNumbers()%>
							<%
								}
							%>
						</pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderDeliveryWindow()) {
				%>
				<tr class="text7" style="line-height: 9px;">
					<td colspan="2"><pdf:cut width="280">
							<fmt:message key="pdf.delivery.window" />
							<%=COLON + header.getOrderHeader().getDeliveryWindow()%>
						</pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderCustomerRefContractNumber()) {
				%>
				<tr>
					<td colspan="2"><pdf:cut width="280">
							<%=header.getOrderHeader().getLabel_customerRefContractNumber() + COLON
							+ header.getOrderHeader().getCustomerRefContractNumber()%>
						</pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderSealsNumbers()) {
				%>
				<tr>
					<td colspan="2"><pdf:cut width="280">
							<fmt:message key="pdf.seals.numbers" />
							<%=COLON + header.getOrderHeader().getSealsNumbers()%>
						</pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderCarrierCrm()) {
				%>
				<tr>
					<td colspan="2"><pdf:cut width="280">
							<fmt:message key="pdf.carrier.crm" />
							<%=COLON + header.getOrderHeader().getCarrierCrm()%>
						</pdf:cut></td>
				</tr>
				<%
					}
				%>
			</table>
		</td>
		<td class="right">
			<table class="title-right-section">
				<%
					if (header.getOrderHeader().renderSiloNumber()) {
				%>
				<tr>
					<td><pdf:cut width="90">
							<fmt:message key="pdf.delivery.silo" /><%=COLON%></pdf:cut></td>
					<td><pdf:cut width="140"><%=header.getOrderHeader().getGeneralLoadData().getDeliverySilo()%></pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderPlantEntance()) {
				%>
				<tr>
					<td><pdf:cut width="90">
							<fmt:message key="pdf.plant.entry" /><%=COLON%></pdf:cut></td>
					<td><pdf:cut width="140"><%=header.getOrderHeader().getGeneralLoadData().getPlantEntrance()%></pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderEndLoadDate()) {
				%>
				<tr>
					<td><pdf:cut width="90">
							<fmt:message key="pdf.endload.date" /><%=COLON%></pdf:cut></td>
					<td><pdf:cut width="140"><%=header.getOrderHeader().getEndLoadDate()%></pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderPlantExit()) {
				%>
				<tr>
					<td><pdf:cut width="90">
							<fmt:message key="pdf.plant.exit" /><%=COLON%></pdf:cut></td>
					<td><pdf:cut width="140"><%=header.getOrderHeader().getGeneralLoadData().getPlantExit()%></pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderDeliveryTime()) {
				%>
				<tr>
					<td><pdf:cut width="90">
							<fmt:message key="pdf.delivery.time" /><%=COLON%></pdf:cut></td>
					<td><pdf:cut width="140"><%=header.getOrderHeader().getDeliveryTime()%></pdf:cut></td>
				</tr>
				<%
					}
				%>
				<%
					if (header.getOrderHeader().renderIncoterm()) {
				%>
				<tr>
					<td><pdf:cut width="90">
							<fmt:message key="pdf.coll.incoterm" /><%=COLON%></pdf:cut></td>
					<td><pdf:cut width="140">
							<%=header.getOrderHeader().getIncoterm()%>
							<%
								if (header.getOrderHeader().renderDeliveryType()) {
							%>
							<%=DASH + header.getOrderHeader().getDeliveryType()%>
							<%
								}
							%>
						</pdf:cut></td>
				</tr>
				<%
					}
				%>
				<tr>
					<td><pdf:cut width="90">
							<fmt:message key="pdf.coll.reason_transport" /><%=COLON%></pdf:cut></td>
					<td><pdf:cut width="140">
							<%
								if (header.getOrderHeader().isTransportReasonIsStpo()) {
							%>
							<fmt:message key="pdf.coll.reason_transport.stpo" />
							<%
								}
									else {
							%>
							<fmt:message key="pdf.coll.reason_transport.sales" />
							<%
								}
							%>
						</pdf:cut></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<%@ include file="../../includeTLDs.jsp"%>

<table class="classicblue" style="border:none; width:auto">
<tr>
	<th>
		<fmt:message key="orderdetail.third.party.freighters.create.new.sap.vendor.partner.function"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
		<table width="100%">
			<tr>
				<td>
					<fmt:message key="orderdetail.third.party.freighters.function.code" />
				</td>
				<td>
					<select id="partnerFunctionCode">
						<c:forEach var="partnerFunctionCode" items="${partnerFunctionCodes}">
							<option value="<c:out value="${partnerFunctionCode}"/>"><c:out value="${partnerFunctionCode}"/></option>
						</c:forEach>
					</select>
				</td>
				<td>
					<fmt:message key="orderdetail.third.party.freighters.sap.vendor" />
				</td>
				<td>
					<input type="text" id="partnerFunctionVendor"/>
				</td>
			</tr>
			<tr>
				<td class="buttons right" colspan="4">
					<input id="vendorpfcancel" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.back"/>"/>
					<input id="vendorpfsave" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>"/>
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
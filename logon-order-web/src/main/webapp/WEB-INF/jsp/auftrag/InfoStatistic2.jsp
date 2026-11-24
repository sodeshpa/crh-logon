
<%@ include file="../includeTLDs.jsp" %>
<%@ page import="java.text.DecimalFormat" %>

<%
DecimalFormat df = new DecimalFormat();
df.setMaximumFractionDigits(0);
%>
<table style="width:100%;noborder">	
	<tr><td>
	<span class="labelblue"><fmt:message key="auftragbearbeitung.texte.produktmix"/></span>
	<!-- beginn Produktmix: div with scrollbars -->
	<div class="subformlist" id="productMix" style="font-size:12px;">
		<table border="0" cellspacing="0" cellpadding="0" style="padding:0px; margin:0px; width:100%;">
			<c:forEach var="statEntry" items="${order_auftrag_AuftragBearbeitungForm.stat.materialAndWeight}">
				<tr>
					<td title="<c:out value="${statEntry.productionPlantName}"/>">
						<div style="white-space: nowrap; overflow: hidden; text-overflow:ellipsis;">
							<c:set var="weightMaterialPercent" value="${(statEntry.weight / order_auftrag_AuftragBearbeitungForm.stat.totalWeightOfOrders) * 100}"/>
								<%=df.format(pageContext.getAttribute("weightMaterialPercent"))%>%  / <fmt:formatNumber type="number" maxFractionDigits="0" value="${statEntry.weight}"/>t <c:out value="${statEntry.name}"/>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	</td></tr>
	<tr align="right"><td>
		<div style="text-align:right;" id="updateJS"></div>
	</td></tr>	
</table>
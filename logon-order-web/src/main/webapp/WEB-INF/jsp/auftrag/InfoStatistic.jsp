
<%@ include file="../includeTLDs.jsp" %>

<c:set var="weightPercent" value="${order_auftrag_AuftragBearbeitungForm.stat.weightPercent}"/>
<c:set var="numberPercent" value="${order_auftrag_AuftragBearbeitungForm.stat.numberPercent}"/>
<table border="0" cellspacing="0" cellpadding="0" style="padding:0px; margin:0px; width:100%;"><tr><td>
	<span class="labelblue"><fmt:message key="auftragbearbeitung.texte.statistik"/></span>
	<div id="statistic">
		<table class="noborder" cellpadding="0" cellspacing="0" border="0" style="margin-top:4px;">
			<colgroup>
				<col width="1%">
				<col width="48%">
				<col width="49%">
			</colgroup>
			<tr>
				<td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><span class="formtitle"><fmt:message key="auftragbearbeitung.texte.menge"/>:</span></td>
				<td valign="top"  style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><fmt:formatNumber type="number" maxFractionDigits="0" value="${order_auftrag_AuftragBearbeitungForm.stat.completedWeightOfOrders}"/>t / <fmt:formatNumber type="number" maxFractionDigits="0" value="${order_auftrag_AuftragBearbeitungForm.stat.totalWeightOfOrders}"/>t</td>
				<td style="vertical-align:bottom; padding:0px 4px 0px 4px;">
	          		<div style="height:15px; width:100%; background-color:#d1e0ff;padding:0px; border: 1px solid #999;margin-bottom:1px;">
	          			<div style="height:15px; width:<c:out value="${weightPercent}"/>%; background-color:#6699ff;padding:0px;"></div>
	          		</div>
				</td>
	        </tr>
	        <tr>
				<td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><span class="formtitle"><fmt:message key="auftragbearbeitung.texte.lieferungen"/>:</span></td>
				<td valign="top"  style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><c:out value="${order_auftrag_AuftragBearbeitungForm.stat.completedNumberOfOrders}"/> / <c:out value="${order_auftrag_AuftragBearbeitungForm.stat.totalNumberOfOrders}"/></td>
				<td style="vertical-align:bottom; padding:0px 4px 0px 4px;">
	          		<div style="height:15px; width:100%; background-color:#d1e0ff; padding:0px; border: 1px solid #999;">
	          			<div style="height:15px; width:<c:out value="${numberPercent}"/>%; background-color:#6699ff;padding:0px;"></div>
	          		</div>
				</td>
			</tr>
		</table>                                         	
	</div>
</td></tr></table>
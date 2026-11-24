<%@ include file="../includeTLDs.jsp"%>

<table class="classic" style="border: none; width: 100%">
	<tbody>
		<tr>
			<td class="label left">
			</td>
			<td class="label left">
				<nobr>
					<fmt:message key="dispo.texte.railwaystation.name"/>
				</nobr>
			</td>
			<td class="label left">
				<nobr>
					<fmt:message key="dispo.texte.railwaystation.number"/>
				</nobr>
			</td>
		</tr>
		<tr>
			<td class="label" width="110">
				<fmt:message key="shippingpoint.field.operationalStation" />
			</td>
			<td width="120">
				<c:out value="${admin_ShippingPointForm.operationStation.railwayStationName}"/>
			</td>
			<td width="120">
				<c:out value="${admin_ShippingPointForm.operationStation.railwayStationNo}"/>
			</td>
		</tr>
		<tr>
			<td class="label" width="110">
				<fmt:message key="shippingpoint.field.commercialStation" />
			</td>
			<td width="120">
					<c:out value="${admin_ShippingPointForm.commStation.railwayStationName}"/>
			</td>
			<td width="120">
				<c:out value="${admin_ShippingPointForm.commStation.railwayStationNo}"/>
			</td>
		</tr>
	</tbody>
</table>
<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: ContainerMonitoringShippingPointsEditPartTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp"%>
<%-- Struts needs this fake form tag, fond no better way to do it. --%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_ContainerMonitoringForm}" />

<div
	style="overflow-y: auto; border-left: none; border-top: none; border-right: none;"
	id="ContainerMonitoringEditShippingPointsList" class="border">
	<table class="classic" style="border: none">
		<c:forEach var="currshippingpoint" varStatus="status"
			items="${order_ContainerMonitoringForm.shippingPoints}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
				<td><html:checkbox
						property="selectedShippingPointCheckbox[${status.count - 1}]"
						value="true" onclick="setSelectedShippingPointCheckboxes()"></html:checkbox>
				</td>
				<td><c:out value="${currshippingpoint.shippingpointName}" /></td>
			</tr>
		</c:forEach>
	</table>
</div>
<script type="text/javascript">
	var el = Ext.get("ContainerMonitoringEditShippingPointsList");
	if (el.getHeight() > 300) {
		el.setHeight(300);
	}
</script>

<div class="buttons right" style="vertical-align: top; width: 200px">
	<input type="button" class="SmallButtonStyle"
		value="<fmt:message
					key="dispo.button.check" />"
		onclick="javascript:setAllCheckboxes(true, 'DivShippingPointsEdit');setSelectedShippingPointCheckboxes();shippingPointList.collapse()">
	<input type="button" class="SmallButtonStyle"
		value="<fmt:message
					key="dispo.button.uncheck" />"
		onclick="javascript:setAllCheckboxes(false, 'DivShippingPointsEdit');setSelectedShippingPointCheckboxes()">
</div>

<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: RailCarOptimizationShippingpointsEditTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_RailCarOptimizationForm}"/>

<div style="overflow-y: auto;border-left: none; border-top: none;border-right: none;" id="railcaroptimizationEditShippingpointsList" class="border">
<table class="classic" style="border:none">
<c:forEach var="sp" varStatus="status" items="${order_RailCarOptimizationForm.shippingpoints}">
<tr class="zebra<c:out value="${status.count % 2}"/>">
<td>
	<html:checkbox property="selectedShippingpointCheckbox[${status.count - 1}]" value="true" onclick="setSelectedShippingpointCheckboxes()"></html:checkbox>
</td>
<td><c:out value="${sp.shippingpointName}"/></td>
</tr>
</c:forEach>
</table>
</div>
<script type="text/javascript">
	var el = Ext.get("railcaroptimizationEditShippingpointsList");
	if (el.getHeight() > 300){
		el.setHeight(300);
	}
</script>

<div class="buttons right" style="vertical-align: top; width:200px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.check" />"
					onclick="javascript:setAllCheckboxes(true, 'DivShippingpointsEdit');setSelectedShippingpointCheckboxes();shippingpointList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.uncheck" />"
					onclick="javascript:setAllCheckboxes(false, 'DivShippingpointsEdit');setSelectedShippingpointCheckboxes()">
</div> 
 
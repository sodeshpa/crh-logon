<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: BimodalRailcarMonitoringShiptosEditPartTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_BimodalRailcarMonitoringForm}"/>

<div style="overflow-y: auto;border-left: none; border-top: none;border-right: none;" id="BimodalRailcarMonitoringEditShiptosList" class="border">
<table class="classic" style="border:none">
<c:forEach var="currshipto" varStatus="status" items="${order_BimodalRailcarMonitoringForm.shiptos}">
<tr class="zebra<c:out value="${status.count % 2}"/>">
<td>
	<html:checkbox property="selectedShiptoCheckbox[${status.count - 1}]" value="true" onclick="setSelectedShiptoCheckboxes()"></html:checkbox>
</td>
<td><c:out value="${currshipto.name1}"/></td>
</tr>
</c:forEach>
</table>
</div>
<script type="text/javascript">
	var el = Ext.get("BimodalRailcarMonitoringEditShiptosList");
	if (el.getHeight() > 300){
		el.setHeight(300);
	}
</script>

<div class="buttons right" style="vertical-align: top; width:200px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.check" />"
					onclick="javascript:setAllCheckboxes(true, 'DivShiptosEdit');setSelectedShiptoCheckboxes();shiptoList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.inactive" />"
					onclick="javascript:setAllCheckboxes(false, 'DivShiptosEdit');setSelectedShiptoCheckboxes()">
</div> 
 
<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: RailCarOptimizationWagonTypesEditTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_RailCarOptimizationForm}"/>

<div style="overflow-y: auto;border-left: none; border-top: none;border-right: none;" id="railcaroptimizationEditWagonTypesList" class="border">
<table class="classic" style="border:none">
<c:forEach var="wagontype" varStatus="status" items="${order_RailCarOptimizationForm.wagonTypes}">
<tr class="zebra<c:out value="${status.count % 2}"/>">
<td>
	<html:checkbox property="selectedWagonTypeCheckbox[${status.count - 1}]" value="true" onclick="setSelectedWagonTypeCheckboxes()"></html:checkbox>
</td>
<td><c:out value="${wagontype.wagonTypeName}"/></td>
</tr>
</c:forEach>
</table>
</div>
<script type="text/javascript">
	var el = Ext.get("railcaroptimizationEditWagonTypesList");
	if (el.getHeight() > 300){
		el.setHeight(300);
	}
</script>

<div class="buttons right" style="vertical-align: top; width:200px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.check" />"
					onclick="javascript:setAllCheckboxes(true, 'DivWagonTypesEdit');setSelectedWagonTypeCheckboxes();wagonTypeList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.uncheck" />"
					onclick="javascript:setAllCheckboxes(false, 'DivWagonTypesEdit');setSelectedWagonTypeCheckboxes()">
</div> 
 
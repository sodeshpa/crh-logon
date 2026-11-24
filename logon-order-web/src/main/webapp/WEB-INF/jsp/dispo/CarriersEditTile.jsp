<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: CarriersEditTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispo_DispoForm}"/>

<script type="text/javascript">
	function setSelectedCarrierCheckboxes() {
		modifyButtonState(Ext.getDom("buttonSelectAllForTransmit"), false);
		a = { 'setCarrierrs' : '' };
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
			url: 'Dispo.do',
			params: a,
			success: function(response) {
				eval(response.responseText);
				refreshTrucks(); // Defined in DispoTile.jsp
				refreshOrders(); // Defined in DispoTile.jsp
			}
		});
	}
</script>

<div style="overflow-y: auto; border-left: none; border-top: none; border-right: none;"
     id="dispoEditCarriersList" class="border"
>
	<table class="classic" style="border:none">
		<c:forEach var="carrier" varStatus="status" items="${order_dispo_DispoForm.carriers}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
				<td>
					<html:checkbox property="selectedCarriersCheckbox[${status.count - 1}]" value="true"
					               onclick="setSelectedCarrierCheckboxes()"></html:checkbox>
				</td>
				<td><c:out value="${carrier.carrierName}"/></td>
			</tr>
		</c:forEach>
	</table>
</div>

<script type="text/javascript">
	var el = Ext.get("dispoEditCarriersList");
	if (el.getHeight() > 300){
		el.setHeight(300);
	}
</script>

<div class="buttons right" style="vertical-align: top; width:200px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.button.check" />"
	       onclick="javascript:setAllCheckboxes(true, 'DivCarriersEdit'); setSelectedCarrierCheckboxes(); carrierList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.button.uncheck" />"
	       onclick="javascript:setAllCheckboxes(false, 'DivCarriersEdit'); setSelectedCarrierCheckboxes()">
</div>

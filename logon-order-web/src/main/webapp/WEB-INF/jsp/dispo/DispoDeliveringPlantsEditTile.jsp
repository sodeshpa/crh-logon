<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: DispoDeliveringPlantsEditTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>

<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispo_DispoForm}"/>

<script type="text/javascript">
	function setSelectedDeliveringPlantCheckboxes() {
		modifyButtonState(Ext.getDom("buttonSelectAllForTransmit"), false);
		a = { 'setDeliveringPlants' : '' };
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
			url: 'Dispo.do',
			params: a,
			success: function(response) {
				eval(response.responseText);
				refreshTrucks();
				refreshOrders();
			}
		});
	}
</script>

<div style="overflow-y: auto;border-left: none; border-top: none;border-right: none;"
     id="dispoEditDeliveringPlantsList" class="border"
>
	<table class="classic" style="border:none">
		<c:forEach var="deliveringPlant" varStatus="status" items="${order_dispo_DispoForm.deliveringPlants}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
				<td>
					<html:checkbox property="selectedDeliveringPlantsCheckbox[${status.count - 1}]"
					               value="true" onclick="setSelectedDeliveringPlantCheckboxes()">
					</html:checkbox>
				</td>
				<td>
					<c:out value="${deliveringPlant.name} (${deliveringPlant.value})"/>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>

<script type="text/javascript">
	var el = Ext.get("dispoEditDeliveringPlantsList");

	if (el.getHeight() > 300){
		el.setHeight(300);
	}
</script>

<div class="buttons right" style="vertical-align: top; width:200px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.button.check" />"
	       onclick="javascript:setAllCheckboxes(true, 'divDeliveringPlantsEdit'); setSelectedDeliveringPlantCheckboxes(); deliveringPlantList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.button.inactive" />"
		   onclick="javascript:setAllCheckboxes(false, 'divDeliveringPlantsEdit'); setSelectedDeliveringPlantCheckboxes()">
</div>

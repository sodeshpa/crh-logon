<%@ include file="../includeTLDs.jsp" %>

<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispo_DispoForm}"/>

<script type="text/javascript">
	function setSelectedOrderStatusCheckboxes() {
		modifyButtonState(Ext.getDom("buttonSelectAllForTransmit"), false);
		a = { 'setOrderStatuses' : '' };
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
     id="dispoEditOrderStatusesList" class="border">
	<table class="classic" style="border:none">
		<c:forEach var="orderStatus" varStatus="status" items="${order_dispo_DispoForm.orderStatusList}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
				<td>
					<html:checkbox property="selectedOrderStatusesCheckbox[${status.count - 1}]"
					               value="true" onclick="setSelectedOrderStatusCheckboxes()">
					</html:checkbox>
				</td>
				<td>
					<c:out value="${orderStatus.txt}"/>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>

<div class="buttons right" style="vertical-align: top; width:200px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.button.check" />"
	       onclick="javascript:setAllCheckboxes(true, 'divOrderStatusEdit'); setSelectedOrderStatusCheckboxes(); orderStatusList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.button.inactive" />"
		   onclick="javascript:setAllCheckboxes(false, 'divOrderStatusEdit'); setSelectedOrderStatusCheckboxes()">
</div>

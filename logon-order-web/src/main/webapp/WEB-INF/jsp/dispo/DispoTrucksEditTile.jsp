<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: DispoTrucksEditTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.constants.Constant"%>

<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispo_DispoForm}"/>

<c:set var="dispoForm" value="${order_dispo_DispoForm}"/>

<c:set var="FILTER_FOR_TRUCK_ALL"><%= Constant.FILTER_FOR_TRUCK_ALL %></c:set>
<c:set var="FILTER_FOR_TRUCK_PLANNING_ONLY"><%= Constant.FILTER_FOR_TRUCK_PLANNING_ONLY %></c:set>
<c:set var="FILTER_FOR_TRUCK_REAL_ONLY"><%= Constant.FILTER_FOR_TRUCK_REAL_ONLY %></c:set>

<script type="text/javascript">
	function setSelectedTruckFilterOption() {
		modifyButtonState(Ext.getDom("buttonSelectAllForTransmit"), false);
		a = { 'setTrucks' : '' };
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
     id="dispoEditTrucksList" class="border"
>
	<table class="classic" style="border: none">

		<!-- Filter options for Truck -->

		<tr class="zebra1">
			<td>
				<html:radio property="selectedFilterForTruck"
				            value="${FILTER_FOR_TRUCK_ALL}"
				            styleId="filterForTruckRadioOption${FILTER_FOR_TRUCK_ALL}"
				            onclick="setSelectedTruckFilterOption()">
				</html:radio>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.all"/>
			</td>
		</tr>

		<tr class="zebra0">
			<td>
				<html:radio property="selectedFilterForTruck"
				            value="${FILTER_FOR_TRUCK_PLANNING_ONLY}"
				            styleId="filterForTruckRadioOption${FILTER_FOR_TRUCK_PLANNING_ONLY}"
				            onclick="setSelectedTruckFilterOption()">
				</html:radio>
			</td>
			<td>
				<span style="font-style: italic"><fmt:message key="dispo.texte.trucks.summary.planning"/></span>
			</td>
		</tr>

		<tr class="zebra1">
			<td>
				<html:radio property="selectedFilterForTruck"
				            value="${FILTER_FOR_TRUCK_REAL_ONLY}"
				            styleId="filterForTruckRadioOption${FILTER_FOR_TRUCK_REAL_ONLY}"
				            onclick="setSelectedTruckFilterOption()">
				</html:radio>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.real"/>
			</td>
		</tr>

		<!-- Filter option for Trucks with Orders -->

		<tr>
			<td style="height: 5px"></td>
			<td style="height: 5px"></td>
		</tr>

		<tr class="zebra0">
			<td>
				<html:checkbox property="showOnlyTrucksWithOrders"
				               value="true"
				               styleId="showOnlyTrucksWithOrdersCheckbox"
				               onclick="setSelectedTruckFilterOption()">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.onlywithorders"/>
			</td>
		</tr>

	</table>
</div>

<script type="text/javascript">
	var el = Ext.get("dispoEditTrucksList");

	if (el.getHeight() > 300){
		el.setHeight(300);
	}
</script>


<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.constants.Constant"%>

<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispo_DispoForm}"/>

<c:set var="dispoForm" value="${order_dispo_DispoForm}"/>

<div style="overflow-y: auto;border-left: none; border-top: none;border-right: none;"
     id="dispoEditDisplayList" class="border"
>
	<table class="classic" style="border: none">

		<!-- Filter options for Display -->

		<tr class="zebra1">
			<td>
				<html:checkbox property="hidePlanningTrucks" value="true"
					styleId="hidePlanningTrucksCheckbox"
					onclick="setSelectedDisplayFilterOption();resizeTables();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.planning"/>
			</td>
		</tr>
		<tr class="zebra0">
			<td>
				<html:checkbox property="hideRealTrucks" value="true"
					styleId="hideRealTrucksCheckbox"
					onclick="setSelectedDisplayFilterOption();resizeTables();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.real"/>
			</td>
		</tr>
		<tr class="zebra1">
			<td>
				<html:checkbox property="hideInactiveTrucks" value="true"
					styleId="hideInactiveTrucksCheckbox"
					onclick="setSelectedDisplayFilterOption();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.inactive"/>
			</td>
		</tr>
		
		<tr class="zebra0">
			<td>
				<html:checkbox property="hideTrucksWithoutDeliveries" value="true"
					styleId="hideTrucksWithoutDeliveriesCheckbox"
					onclick="setSelectedDisplayFilterOption();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.withoutdeliveries"/>
			</td>
		</tr>
		<tr class="zebra1">
			<td>
				<html:checkbox property="hideScheduledCustorders" value="true"
					styleId="hideScheduledCustordersCheckbox"
					onclick="setSelectedDisplayFilterOption();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.scheduledcustorders"/>
			</td>
		</tr>
		<tr class="zebra0">
			<td>
				<html:checkbox property="hideClosedCustorders" value="true"
					styleId="hideClosedCustordersCheckbox"
					onclick="setSelectedDisplayFilterOption();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.closedcustorders"/>
			</td>
		</tr>
		<tr class="zebra1">
			<td>			
				<html:checkbox property="hideCustorderHeaders" value="true"
					styleId="hideCustorderHeadersCheckbox"
					onclick="setSelectedDisplayFilterOption();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.custorderheader"/>
			</td>
		</tr>
		<tr class="zebra0">
			<td>
				<html:checkbox property="hideTrucksAndOrderOfPartners" value="true"
					styleId="hideTrucksAndOrderOfPartnersCheckbox"
					onclick="setSelectedDisplayFilterOption();">
				</html:checkbox>
			</td>
			<td>
				<fmt:message key="dispo.texte.trucks.hide.trucksandordersofpartners"/>
			</td>
		</tr>

	</table>
</div>

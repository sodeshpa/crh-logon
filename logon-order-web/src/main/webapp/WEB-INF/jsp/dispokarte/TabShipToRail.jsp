<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<%@ page import="com.crh.logon.taglib.fallbacklogic.DefaultFallBackLogicTag" %>
<%@page import="com.crh.logon.persistence.entities.common.ShiptoEntity"%>
<%@page import="com.crh.logon.order.web.forms.dispokarte.DispokarteForm"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_dispokarte_DispokarteForm}" />
<c:if test="${not empty order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.railwayStation}" >

<table cellpadding="0" cellspacing="0" style="margin: 0px; border-collapse: collapse; padding: 0px 0px 0px 0px;">
<tr>
	<td> 
		<%-- wagon type priorities --%>  		
		<div class="label">
			<span style="margin-top: 4px"><fmt:message key="dispokarte.texte.wagonPriorities"/></span>
		</div>
		<div class="scrollist" id="DivWagonTypes" style="height: 280px; width: 240px">
			<table class="classic" style="border:none">
					<tr>
						<th width="20">
							<img src="../images/active.png"/>
						</th>
						<th width="20">
							<img src="<crh-common:url value='/images/delete.gif'/>"/>
						</th>
						<th width="20">
							<fmt:message key="dispokarte.texte.prio"/>
						</th>
						<th width="180">
							<fmt:message key="dispokarte.texte.type"/>
						</th>
					</tr>
				<c:forEach var="type" varStatus="status" items="${order_dispokarte_DispokarteForm.wagonTypeWrapperList}">
					<tr class="zebra<c:out value="${status.count % 2}"/>">
						<td valign="middle">
							<html:radio value="true" property="wagonAllowance[${status.count-1}]" styleId="wagonAllowanceTrue${status.count-1}"  onchange="setupPriority('wagon',${status.count-1})"/>
						</td>
						<td valign="middle">
							<html:radio value="false" property="wagonAllowance[${status.count-1}]" styleId="wagonAllowanceFalse${status.count-1}" onchange="setupPriority('wagon',${status.count-1})"/>
						</td>
						<td valign="middle">
							<c:if test='${type.priority != 0}'>
								<html:text property="wagonPrio[${status.count-1}]" style="width:20px;" maxlength="2" size="2" styleId="wagonPrio${status.count-1}"/>
							</c:if>
							<c:if test='${type.priority == 0}'>
								<html:text property="wagonPrio[${status.count-1}]" style="width:20px;" disabled="true" maxlength="2" size="2" styleId="wagonPrio${status.count-1}"/>
							</c:if>
						</td>
						<td valign="middle" style="width:300px">
							<c:out value="${type.typeName}"/>
						</td> 
					</tr>
				</c:forEach>
			</table>
		</div>
	</td>

	<td>
		<div>
		<table cellpadding="0" cellspacing="0" style="margin: 0px; border-collapse: collapse; padding: 0px;">
			<tr>
				<td class="label" width="110">
					<fmt:message key="dispo.texte.railwaystation.name"/>
				</td>
				<td width="120">
					<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.railwayStation.railwayStationName}"/>
				</td>
			</tr>
			<tr>
				<td class="label" width="110">
					<fmt:message key="dispo.texte.railwaystation.number"/>
				</td>
				<td width="120">
					<c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.railwayStation.railwayStationNo}"/>
				</td>
			</tr>
			<tr>
				<td class="label" width="110">
					<fmt:message key="dispo.texte.railwaystation.unloadingpoint"/>
				</td>
				<td width="120">
					<c:set var="maxlength">
						<%= ShiptoEntity.DBSIZE_TRAIN_STATION_UNLOADING_POINT + "" %>						
					</c:set>
					<html:text property="shipTo.trainStationUnloadingPoint" maxlength="${maxlength}"></html:text>
					
				</td>
			</tr>
			<tr>
				<td class="label" width="110">
					<fmt:message key="dispo.texte.railwaystation.sbbcustomernr"/>
				</td>
				<td width="120">
					<c:set var="maxlength">
						<%= ShiptoEntity.DBSIZE_SBB_CUSTOMER_NUMBER + "" %>
					</c:set>
					<html:text property="shipTo.sbbCustomerNumber" maxlength="${maxlength}"></html:text>
				</td>
			</tr>
		</table>
		</div>
		
		<c:if test="${order_dispokarte_DispokarteForm.displayRailChainSelection}">
			<div style="margin-top: 20px;">
			<span class="label"><fmt:message
				key="dispokarte.texte.railChainSP" /></span>
			<div style="margin-top:4px"><html:select property="railChainPlantCd"
				style="width:230px;" onchange="modifyRailChainPlant()"
				styleId="railChainPlantDD">
				<html:option value="-1">-</html:option>
				<c:forEach var="plant" varStatus="plantStatus"
					items="${order_dispokarte_DispokarteForm.plantList}">
					<html:option value="${plant.plantCd}">
						<c:out value="${plant.plantName}" />
					</html:option>
				</c:forEach>
			</html:select></div>
			<div id="railChainDiv" style="margin-top:4px"><html:select
				property="railChainShippingpointId" style="width:230px;">
				<html:option value="-1">-</html:option>
				<c:forEach var="sp"
					items="${order_dispokarte_DispokarteForm.shippingpointListForRailChainPlant}">
					<html:option value="${sp.shippingpointId}">
						<c:out value="${sp.shippingpointName}" />
					</html:option>
				</c:forEach>
			</html:select></div>
			</div>
		</c:if>
		
		<div style="margin-top: 6px">
			<html:checkbox property="shipTo.railHoldBackReOrderFlag" value="true"/>
			<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="railHoldBackReOrderFlag" value="false"/>
			<fmt:message key="dispo.texte.railwaystation.holdback"/>
		</div>
		</td>
	</tr>
</table>

</c:if>

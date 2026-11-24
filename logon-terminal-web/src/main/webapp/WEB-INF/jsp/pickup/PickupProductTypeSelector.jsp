<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupProductTypeSelector.jsp,v $ --%>
<%@ page session="true"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>

<%@ include file="../includeTLDs.jsp"%>
<title><fmt:message key="pickup.create.steps.selectproducttype"/></title>
<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>

<% pageContext.setAttribute("FORM", session.getAttribute(PickupForm.FORM_NAME));%>
<script type="text/javascript">
var actions = {
	SELECT_PRODUCT_TYPE: '<%=PickupAction.ACTION_SELECT_PRODUCT_TYPE%>',
	CANCEL_ADD_ITEM: '<%=PickupAction.ACTION_CANCEL_ADDITEM%>',
	CANCEL_CREATE: '<%=PickupAction.ACTION_CANCEL_CREATE%>'
};

function selectProductType(value) {
	document.getElementById("inputAction").value=actions.SELECT_PRODUCT_TYPE;
	document.getElementById("productType").value=value;
	document.forms[0].submit();	
	disableAll();
}

</script>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize
	function="<%=Authorization.PICKUP_ORDER_TERMINAL_W %>" 
	function2="<%=Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER%>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCommon.js'/>"></script>
</crh-common:authorize>

<html:form styleId = "${FORM_NAME}" action="${ACTION_URL}">
<input type="hidden" name="inputAction" id="inputAction" value=""/>
<input type="hidden" name="compositeId" id="compositeId" value=""/>
<html:hidden property="productType" styleId="productType" /> 
					                              
	<table height="100%" class="centered"  cellpadding="0" cellspacing="0" border="0" width="100%"
		style="padding: 0px; margin: 0px;">
		<tr style="height:20%" >
			<td>
				<b><fmt:message key="pickup.create.steps.selectproducttype"/></b>
			</td>
		</tr>
		<tr style="height:60%">
			<td>
				<c:set var="itemcounter">0</c:set>
				<div style="overflow:auto; height:420px">
					<table>
					<c:forEach var="entry" items="${FORM.productTypeMap}">
					<c:if test="${itemcounter mod 2 eq 0}">					
						<tr>
					</c:if>
							<td>
							<c:set var="itemcounter"><c:out value="${itemcounter+1 }"/></c:set>
							<input style="font-size:24px;width:300px"
								onclick='javascript:selectProductType("<c:out value="${entry.key}"/>")' 
								id = "product<c:out value="${entry.key}"/>" 
								type="button" class="ViewButtonStyle"  
								value="<c:out value="${entry.value}"/>">
							</td>
					<c:if test="${itemcounter mod 2 eq 0}">					
						</tr>
					</c:if>
					</c:forEach>
					</table>
					<c:if test="${itemcounter eq 0}">
						<fmt:message key="pickup.create.noproductsavailable"/>
					</c:if>
				</div>
			</td>
		</tr>
		<tr style="height:20%" >
			<td>
			<c:choose>
				<c:when test="${empty FORM.custorderBean.custorderItemBeanList }">
					<c:if test="${empty terminal_PickupForm.custorderBean.custorderEntity.custorderId }">
					<input onclick='javascript:cancelCreateOrder()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.cancelcreateorder"/> ">
				</c:if>
				<c:if test="${!empty terminal_PickupForm.custorderBean.custorderEntity.custorderId }">
					<input onclick='javascript:cancelEditOrder()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.canceleditorder"/> ">
				</c:if>	
				</c:when>
				<c:otherwise>
					<input onclick='javascript:cancelAddItem()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.canceladditem"/> ">	
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
	</table>
</html:form>

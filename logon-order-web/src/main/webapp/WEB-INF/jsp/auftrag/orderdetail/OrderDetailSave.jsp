<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- This page is loaded during the save. it refreshes order status related info.--%>
<%@ include file="../../includeTLDs.jsp"%>
<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->

<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.order.web.actions.bestellung.BestellungAction"%>
<%@page import="com.crh.logon.order.web.forms.auftrag.OrderDetailForm"%>
<%@page import="org.apache.struts.action.ActionMessage"%>
<%@page import="org.apache.struts.action.ActionMessages"%>
<%@page import="java.util.Iterator"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />

<c:set var="ORDER_STATUS_NEW"><%=Constant.ORDER_STATUS_NEW%></c:set>
<c:set var="ORDER_STATUS_TRANSMITTED_TO_POOL"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_POOL%></c:set>
<c:set var="ORDER_STATUS_TRANSMITTED_TO_POOL_ACK"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK%></c:set>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>
<c:set var="isSpecialDetailDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_W %>">
<c:set var="isSpecialDetailDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isForwardToPoolDisabled">true</c:set>
<c:if test="${(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_NEW)}">
    <c:set var="isForwardToPoolDisabled">false</c:set>
</c:if>
<c:set var="isForwardToCarrierDisabled">true</c:set>
<c:if test="${(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_NEW) ||
            (order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_POOL) ||
            (order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_POOL_ACK)}">
    <c:set var="isForwardToCarrierDisabled">false</c:set>
</c:if>
<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%>


<div id="orderStatusListDivHidden"
	style="position: relative; visibility: hidden; width: 0; height: 0;">
	<span class="labelblue"><fmt:message
			key="orderdetail.main.label.order.status" /> </span><br>
		<textarea name="orderStatusList" cols="80" rows="5" class="textfield"
			style="width: 99%"><c:forEach var="entity"
			varStatus="status"
			items="${order_auftrag_OrderDetailForm.orderBean.orderStatusList}"><c:out value="${entity}" />
</c:forEach></textarea></div>


<div id="pageTitleDivHidden"
	style="position: relative; visibility: hidden; width: 0; height: 0;">
<div style="font-size: 14px;"><c:out
	value="${order_auftrag_OrderDetailForm.title}" />&nbsp;&nbsp;&nbsp;<c:out
	value="${order_auftrag_OrderDetailForm.orderBean.orderStatus}" /></div>
</div>

<div id="autoPoolAssignmentFlagDivHidden"
	style="position: relative; visibility: hidden; width: 0; height: 0;">
	<c:set var="forwardToPool"><fmt:message key="orderdetail.main.field.forward.pool" /></c:set>
 	<html:checkbox property="orderBean.autoPoolAssignmentFlag" title="${forwardToPool}"
		disabled="${isSpecialDetailDisabled || isForwardToPoolDisabled}" />
</div>

<div id="autoCommCarrierAssignmentFlagDivHidden"
	style="position: relative; visibility: hidden; width: 0; height: 0;">
	<c:set var="forwardToCommCarrier"><fmt:message key="orderdetail.main.field.forward.commcarrier" /></c:set>
	<html:checkbox property="orderBean.autoCommCarrierAssignmentFlag" title="${forwardToCommCarrier}" 
		disabled="${isSpecialDetailDisabled || isForwardToCommCarrierDisabled}" />
</div> 


<script type="text/javascript">

	document.getElementById('orderStatusListDiv').innerHTML = document
			.getElementById('orderStatusListDivHidden').innerHTML;
	document.getElementById('orderStatusListDivHidden').innerHTML = "";
	
	document.getElementById('pageTitleDiv').innerHTML = document
			.getElementById('pageTitleDivHidden').innerHTML;
	document.getElementById('pageTitleDivHidden').innerHTML = "";

	document.getElementById('autoPoolAssignmentFlagDiv').innerHTML = document
			.getElementById('autoPoolAssignmentFlagDivHidden').innerHTML;
	document.getElementById('autoPoolAssignmentFlagDivHidden').innerHTML = "";

	document.getElementById('autoCommCarrierAssignmentFlagDiv').innerHTML = document
			.getElementById('autoCommCarrierAssignmentFlagDivHidden').innerHTML;
	document.getElementById('autoCommCarrierAssignmentFlagDivHidden').innerHTML = "";

	<crh-common:authorize function="<%=Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT %>">
	if (typeof currentTab != 'undefined' && !Ext.isEmpty(currentTab) && currentTab =='<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>'){
	<%
	ActionMessages messages =(ActionMessages)request.getAttribute("org.apache.struts.action.ACTION_MESSAGE");
	if (messages != null){
		Iterator it =messages.get();
		if (it != null){			
			while(it.hasNext()) {
				Object message = it.next();
				if (message != null && ((ActionMessage)message).getKey().equals("orderdetail.message.success.save")){
					%>
					setCurrentTab('<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>');
					<%
				}
			}
		}
	}
	%>
	}
	</crh-common:authorize>
	
	<%
	String reloadAction = (String)request.getAttribute(BestellungAction.RELOAD);
	if (BestellungAction.RELOAD_ALL.equals(reloadAction)) {
		%>
		reloadOrderDirectly(orderId);
		<%
	}
	%>
	
</script>

<!--</html:form>--> 
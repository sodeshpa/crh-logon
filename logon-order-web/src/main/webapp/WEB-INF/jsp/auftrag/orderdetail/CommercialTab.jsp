<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page
	import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page
	import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
	
<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>


<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />


<jsp:include page="MaterialTabItem.jsp"></jsp:include>

<%--   div id="commercialTableDiv"
				style="margin-top: 0px; border: 0px; padding: 0px; spacing: 0px; width: 100%;"><jsp:include
				page="CommercialTabTable.jsp"></jsp:include></div>
<div class="buttons right" style="padding:4px;">			
<input type="button" <c:if test="${order_auftrag_OrderDetailForm.recalculateCostsFlag}">disabled</c:if>  class="ViewButtonStyle" onClick="saveOrder('<%=OrderDetailAction.FORWARD_COMMERCIAL_TAB_RECALC%>')"
							value="<fmt:message key="orderdetail.com.button.recalc.costs"/>">
</div --%>
<!--</html:form>-->

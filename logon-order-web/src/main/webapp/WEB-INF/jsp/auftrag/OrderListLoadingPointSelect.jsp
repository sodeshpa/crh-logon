<%@ page import="com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm" %>
<%@ page import="com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity" %>
<%@ page import="com.crh.logon.persistence.entities.common.LoadingpointEntity" %>
<%@ page import="com.crh.logon.order.web.actions.auftrag.AuftragBearbeitungAction" %>
<%@ page import="com.crh.logon.business.services.beans.OrderChangeLogic"%>
<%@ taglib uri="http://struts.apache.org/tags-logic-el" prefix="logic" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Iterator"%>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.LoadingpointEntity"%>

<crh-common:authorize function="<%= Authorization.SHIPPING_W %>">
			<%
             OrdItemAssignmtLoadEntity ent = (OrdItemAssignmtLoadEntity)request.getAttribute("order");
			 Long id = ent.getLoadId();
			
			List list = ((AuftragBearbeitungForm)session.getAttribute("order_auftrag_AuftragBearbeitungForm")).getSelectableOrderLoadingpointList();
			pageContext.setAttribute("selectableOrderLoadingpointList", list);
            %>
		<table style="width:100%">
			<colgroup>
			<col width="70%">
			<col width="30%">
			</colgroup>
			<tr>
				<td colspan="2" style="padding:0px;" align="left">
	<crh-order:disableSimpleChangeFunctionality  orderKey="order" functionality="<%=OrderChangeLogic.FUNCTIONALITY_UPDATE_LOADINGPOINT_DATA%>">
	        		<crh-order:simpleChangeDisabledBody>
            <html:select name="order" property="loadingpointId" disabled="true">
				<option value=""><fmt:message key="option.select"/></option>
				<html:options collection="selectableOrderLoadingpointList" property="loadingpoint.loadingpointId" labelProperty="loadingpoint.loadingpointName" />
			</html:select>			            	
            		</crh-order:simpleChangeDisabledBody>
            		<crh-order:simpleChangeEnabledBody>
			<c:set var="function">changeWeighSystemButton('<c:out value="${order.loadId}"/>', this.value);</c:set>
            <html:select name="order" property="loadingpointId"  styleId="<%=id.toString() + "inputLoadingPointId"%>"  onchange="<%=(String)pageContext.getAttribute("function") %>">
				<option value=""><fmt:message key="option.select"/></option>
				<html:options collection="selectableOrderLoadingpointList" property="loadingpoint.loadingpointId" labelProperty="loadingpoint.loadingpointName" />
			</html:select>			            	
    		      	</crh-order:simpleChangeEnabledBody>
	</crh-order:disableSimpleChangeFunctionality>
		</td>							
		</tr>
		<tr>
		<td>
			<c:if test="${empty order.loadOrderId}">-</c:if>
			<c:if test="${!empty order.loadOrderId}"><c:out value="${order.loadOrderId}"/></c:if>
		</td>
		<td align="right">
		<nobr>
			<crh-order:disableSimpleChangeFunctionality orderKey="order" functionality="<%=OrderChangeLogic.FUNCTIONALITY_CANCEL_LOADING%>">
				<crh-order:simpleChangeEnabledBody>
					<a id='<c:out value="${order.loadId}"/>cancelLoading' onclick="cancelLoading('<c:out value="${order.loadId}"/>');">
						<img src="<crh-common:url value="/images/delete2.png"/>" title="<fmt:message key="auftragbearbeitung.alt.cancelloading"/>"/>
					</a>
				</crh-order:simpleChangeEnabledBody>
			</crh-order:disableSimpleChangeFunctionality>
			<crh-order:disableSimpleChangeFunctionality  orderKey="order" functionality="<%=OrderChangeLogic.FUNCTIONALITY_CANCEL_WEIGH_SYSTEM%>">
				<crh-order:simpleChangeEnabledBody>
				<img onclick='cancelAnWeighSystem(<c:out value="${order.loadId}"/>)' src="<crh-common:url value='/images/delete.gif'/>" title="<fmt:message key="auftragbearbeitung.alt.cancelonweighsystem"/>">
				</crh-order:simpleChangeEnabledBody>
			</crh-order:disableSimpleChangeFunctionality>
				<crh-order:disableSimpleChangeFunctionality  orderKey="order" functionality="<%=OrderChangeLogic.FUNCTIONALITY_TRANSMIT_TO_WEIGH_SYSTEM%>">
					<crh-order:simpleChangeDisabledBody>
						<a id='<c:out value="${order.loadId}"/>transmitOrder' onclick='function(){return false;};'><img alt="<fmt:message key="auftragbearbeitung.button.transmitweighsystem"/>" src="<crh-common:url value='/images/balance_grey.png'/>"/></a>
					</crh-order:simpleChangeDisabledBody>
					<crh-order:simpleChangeEnabledBody>
						<a id='<c:out value="${order.loadId}"/>transmitOrder' onclick='transmitToWeighSystem("<c:out value="${order.loadId}"/>");'><img alt="<fmt:message key="auftragbearbeitung.button.transmitweighsystem"/>" title="<fmt:message key="auftragbearbeitung.alt.cancelonweighsystem"/>" src="<crh-common:url value='/images/balance.png'/>"/></a>
					</crh-order:simpleChangeEnabledBody>
				</crh-order:disableSimpleChangeFunctionality>
			</nobr>		
		</td>
		</tr>		                       
</table>
</crh-common:authorize>